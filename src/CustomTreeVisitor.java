import com.sun.source.tree.*;
import com.sun.source.util.*;
import com.sun.tools.javac.tree.JCTree;

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
        String name = methodTree.getName().toString();
        Tree returnType = methodTree.getReturnType();

        System.out.println("name: " + name);
        if (returnType == null) {
            System.out.println("returnType: null"); // constructor (ie. <init>) methods don't have a return type
        } else {
            System.out.println("returnType: " + returnType.toString());
        }

        System.out.println();

        //manager.addMethod(name, )
        return super.visitMethod(methodTree, trees);
    }

    @Override
    public Object visitVariable(VariableTree variableTree, Trees trees) {
        return super.visitVariable(variableTree, trees);
    }


}
