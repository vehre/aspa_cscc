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

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public abstract class BaseMemberWrapper extends BaseMember {
    protected ASPDependantClass wrapped;
    private static Logger LOG = Logger.getLogger(BaseMemberWrapper.class);


    public ASPClass getEvaluatedClass() {
        return wrapped;
    }


    public void setEvaluatedClass(ASPClass clazz) {
        if (clazz instanceof ASPDependantClass) {
            wrapped = (ASPDependantClass) clazz;
            wrapped.setDependantMember(this);
        } else {
            LOG.warn("Expecting a ASPDependantClass");
        }
    }


    protected void setArgList(Member wrapped) {
        if (args != null) {
            List wArgs = wrapped.getArgs();
            if (wArgs != null) {
                wArgs = new ArrayList(wArgs);
                wArgs.addAll(args);
            } else {
                wArgs = args;
            }
            wrapped.setArgs(wArgs);
        }
    }
}
