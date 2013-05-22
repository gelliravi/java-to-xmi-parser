import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.element.Name;
import java.util.LinkedList;
import java.util.List;

public class CustomTreeVisitor extends TreePathScanner<Object, Trees> {

    private ManagerOfModels manager;

    public CustomTreeVisitor() {
        super();
        this.manager = new ManagerOfModels();
    }

    @Override
    public Object visitClass(ClassTree classTree, Trees trees) {
        return super.visitClass(classTree, trees);
    }

    @Override
    public Object visitMethod(MethodTree methodTree, Trees trees) {

        // CONSIDER CONDENSING INTO visitClass method. This may be necessary to link ModelMethod to ModelClass

        Name name = methodTree.getName();
        List<? extends VariableTree> params = methodTree.getParameters();
        Tree returnType = methodTree.getReturnType();
        List<? extends StatementTree> bodyStatements = methodTree.getBody().getStatements();
        List<StatementTree> localVariables = new LinkedList<StatementTree>();
        for (StatementTree s : bodyStatements) {
            if (s.getKind() == Tree.Kind.VARIABLE) {
                // Only detects variable initialization; ignores value resets
                localVariables.add(s);
            }
        }

        ModelMethod mv = new ModelMethod(name, params, localVariables, returnType);
        // manager.addMethod(mv);

        /*
        System.out.println(name);
        System.out.println(methodTree.getParameters());

        */

        // I will eventually need to account for this
        // System.out.println(methodTree.getTypeParameters());

        /*
        ModifiersTree modifiers = methodTree.getModifiers();
        System.out.println(methodTree.getBody());
        System.out.println(methodTree.getClass());
        System.out.println(methodTree.getDefaultValue());
        System.out.println(methodTree.getKind());
        System.out.println(methodTree.getThrows());
        */

        return super.visitMethod(methodTree, trees);
    }

    @Override
    public Object visitVariable(VariableTree variableTree, Trees trees) {
        return super.visitVariable(variableTree, trees);
    }


}
