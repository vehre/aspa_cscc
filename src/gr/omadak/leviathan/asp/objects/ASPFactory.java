/*
    This file is part of Aspa.

    Aspa is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Aspa is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Aspa; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package gr.omadak.leviathan.asp.objects;
import antlr.ASTFactory;
import antlr.CommonToken;
import antlr.collections.AST;
import gr.omadak.leviathan.asp.CommonConstants;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

public class ASPFactory extends ASTFactory {
    private List args;
    private AST instance;
    private BitSet argsUsed;
    private boolean errorsFound;
    private static final int FAKE_NODE = Integer.MAX_VALUE;

    protected static final int INVALID_ARG = Integer.MIN_VALUE;
    private static final Logger LOG = Logger.getLogger(ASPFactory.class);


    private AST getAST(int index) {
        AST result = (AST) args.get(index);
        argsUsed.set(index);
        return result;
    }


    private int[] parseArray(String cs) {
        StringTokenizer st = new StringTokenizer(cs, ",");
        int[] result = new int[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            try {
                result[i++] = Integer.parseInt(st.nextToken().trim()) - 1;
            } catch (NumberFormatException nfe) {
                LOG.error("Failed to parse array:" + cs);
            }
        }
        return result;
    }


    private AST simpleDub(AST ast) {
        AST result;
        if (ast instanceof ObjectAST) {
            ObjectAST orig = (ObjectAST) ast;
            result = new ObjectAST(
            new CommonToken(orig.getType(), orig.getText()),
            orig.getInstance());
        } else {
            result = super.dup(ast);
        }
        return result;
    }


    private AST dupAllArgs(AST ast) {
        AST result = null;
        int transCount = 0;
        /*
        exlude indexes:[1,2,6]
        include indexes:{1,2,4} or 1,2,4
        if text is null all args are used
        if text is -1 then only those not used will be used
        */
        int[] use_index = null, exclude_index = null;
        String tText = ast.getText();
        if (tText != null && tText.length() > 0) {
            if (tText.charAt(0) == '[' && tText.length() > 1) {
                exclude_index = parseArray(tText.substring(1,
                tText.length() - 1));
            } else if (tText.charAt(0) == '{'
            ||  Character.isDigit(tText.charAt(0))) {
                int lim = tText.charAt(0) == '{' ? 1 : 0;
                use_index = parseArray(tText.substring(lim,
                tText.length() - lim));
            } else {
                boolean isMinus;
                try {
                    isMinus = Integer.parseInt(tText) == -1;
                } catch (NumberFormatException nfe) {
                    isMinus = false;
                }
                if (isMinus) {
                    use_index = new int[args.size() - argsUsed.cardinality()];
                    exclude_index = new int[argsUsed.cardinality()];
                    int useIndex = 0, excludeIndex = 0;
                    for (int i = 0; i < args.size(); i++) {
                        if (argsUsed.get(i)) {
                            exclude_index[excludeIndex++] = i;
                        } else {
                            use_index[useIndex++] = i;
                        }
                    }
                }
            }
        } else {
            use_index = new int[args.size()];
            for (int i = 0; i < use_index.length; i++) {
                use_index[i] = i;
            }
        }
        if (use_index != null || exclude_index != null) {
            if (use_index != null) {
                Arrays.sort(use_index);
            }
            if (exclude_index != null) {
                Arrays.sort(exclude_index);
            }
            result = null;
            AST loopAST = null;
            for (int i = 0; i < args.size(); i++) {
                boolean isIncluded = use_index != null
                && Arrays.binarySearch(use_index, i) >= 0;
                boolean isExcluded = exclude_index != null
                && Arrays.binarySearch(exclude_index, i) >= 0;
                if (isIncluded && !isExcluded) {
                    AST replacement = getAST(i);
                    AST dubl = dupList(replacement);
                    transCount++;
                    if (loopAST != null) {
                        AST list = loopAST;
                        while (list.getNextSibling() != null) {
                            list = list.getNextSibling();
                        }
                        list.setNextSibling(dubl);
                    } else {
                        result = dubl;
                    }
                    loopAST = dubl;
                } else if (isIncluded && isExcluded) {
                    LOG.warn("An argument is bouth included and excluded");
                }
            }
        }
        if (result != null && transCount > 0) {
            AST fake = create(FAKE_NODE, "FAKE_NODE");
            fake.setFirstChild(result);
            result = fake;
        }
        return result;
    }


    public AST dup(AST ast) {
        AST result = null;
        if (ast == null) {
            return null;
        }
        if (ast.getType() == CommonConstants.TEMPLATE) {
            try {
                int index = Integer.parseInt(ast.getText().trim());
                index--;
                if (index < 0 || args == null || index >= args.size()) {
                    String msg = "Invalid index for argument replacement:"
                    + ast.getText();
                    if (args == null) {
                        msg += " because no args available";
                    } else if (index < 0) {
                        msg += " index should be at least 1";
                    } else {
                        msg += " index can not be greater than " + args.size();
                    }
                    throw new IndexOutOfBoundsException(msg);
                }
                AST replacement = getAST(index);
                result = dupList(replacement);
            } catch (IndexOutOfBoundsException ifb) {
                LOG.error(ifb.getMessage());
                errorsFound = true;
                result = create(INVALID_ARG);
            } catch (NumberFormatException nfe) {
                LOG.error(
                "Invalid text for argument template.Expected an int");
                errorsFound = true;
                result = create(INVALID_ARG);
            }
        } else if (ast.getType() == CommonConstants.ALL_ARGS) {
            result = dupAllArgs(ast);
        } else if (ast.getType() == CommonConstants.INSTANCE) {
            result = dupTree(instance);
        } else {
            result = simpleDub(ast);
        }
        return result;
    }


    private boolean isFake(AST ast) {
        int type = ast == null ? -1 : ast.getType();
        return type == FAKE_NODE;
    }

    /** Duplicate tree including siblings of root. */
    public AST dupList(AST t) {
        AST result = dupTree(t);
        //dupTree may return a fake constract like (FAKE_NODE node node ...)
        AST nt;
        if (isFake(result)) {
            result = result.getFirstChild();
            nt = result;
            while (nt.getNextSibling() != null) {
                nt = nt.getNextSibling();
            }
        } else {
            nt = result;
        }
        while (t != null) {
            t = t.getNextSibling();
            AST tree = dupTree(t);
            boolean fake = isFake(tree);
            if (fake) {
                tree = tree.getFirstChild();
            }
            if (nt != null) {
                nt.setNextSibling(tree);
            }
            nt = tree;
            if (fake && nt != null) {
                while (nt.getNextSibling() != null) {
                    nt = nt.getNextSibling();
                }
            }
        }
        return result;
    }


    public AST dupTree(AST t) {
        AST result = dup(t);
        if (result != null && result.getFirstChild() == null) {
            result.setFirstChild(dupList(t.getFirstChild()));
        }
        return result;
    }

    public void setArgs(List args) {
        this.args = args;
        if (args != null) {
            argsUsed = new BitSet(args.size());
        } else {
            argsUsed = null;
        }
    }

    public void setInstanceAST(AST ast) {
        instance = ast;
        errorsFound = false;
        if (instance != null && instance.getNextSibling() != null) {
            instance.setNextSibling(null);
        }
    }


    public boolean hadErrors() {
        return errorsFound;
    }
}
