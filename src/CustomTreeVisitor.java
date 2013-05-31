import com.sun.source.tree.*;
import com.sun.source.util.*;
import sun.rmi.rmic.iiop.IDLGenerator;

import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CustomTreeVisitor extends TreePathScanner<Object, Trees> {

    public HashMap<String, ModelClass> classes;
    public IDManager idManager;

    public CustomTreeVisitor() {
        super();
        this.classes = new HashMap<String, ModelClass>();
        this.idManager = new IDManager();
    }

    @Override
    public Object visitClass(ClassTree classTree, Trees trees) {
        Name name = classTree.getSimpleName();
        Tree extendsClause = classTree.getExtendsClause();
        List<? extends Tree> implementsClause = classTree.getImplementsClause();
        List<? extends Tree> members = classTree.getMembers();
        ModifiersTree mt = classTree.getModifiers();
        ModelClass mc = new ModelClass(name, extendsClause, implementsClause, members, mt);
        classes.put(mc.getName(), mc);
        idManager.generate(mc);
        return super.visitClass(classTree, trees);
    }



}
