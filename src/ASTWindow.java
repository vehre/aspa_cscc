import antlr.collections.AST;
import antlr.debug.misc.JTreeASTModel;
import antlr.debug.misc.JTreeASTPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class ASTWindow extends JFrame {
    private JTabbedPane tabs;
    public ASTWindow(String title) {
        super(title);
        setSize(200, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    class MyTreeSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent event) {
            TreePath path = event.getPath();
            AST ast = (AST) path.getLastPathComponent();
            System.out.println(ast.getText()
            + "[" + ast.getType()
            + ", " + ast.getClass()
            + "]");
        }
    }


    private TreeSelectionListener listener = new MyTreeSelectionListener();

    public void addAST(String title, AST ast) {
        if (tabs == null) {
            tabs = new JTabbedPane();
        }
        JTreeASTPanel panel = new JTreeASTPanel(new JTreeASTModel(ast),
        listener);
        tabs.addTab(title == null ? "" : title, panel);
    }


    public void pack() {
        if (tabs != null) {
            getContentPane().add(tabs);
        }
        super.pack();
    }


    public boolean hasAST() {
        return tabs != null && tabs.getTabCount() > 0;
    }
}
