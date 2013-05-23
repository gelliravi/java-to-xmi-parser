import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CustomTreeVisitor extends TreePathScanner<Object, Trees> {

    public HashMap<String, ModelClass> classes;

    public CustomTreeVisitor() {
        super();
        this.classes = new HashMap<String, ModelClass>();
    }

    @Override
    public Object visitClass(ClassTree classTree, Trees trees) {
        Name name = classTree.getSimpleName();
        Tree extendsClause = classTree.getExtendsClause();
        List<? extends Tree> implementsClause = classTree.getImplementsClause();
        List<? extends Tree> members = classTree.getMembers();

        ModelClass mc = new ModelClass(name, extendsClause, implementsClause, members);
        classes.put(mc.getName(), mc);

        // I will eventually need to account for this
        // System.out.println(methodTree.getTypeParameters());

        return super.visitClass(classTree, trees);
    }


}
