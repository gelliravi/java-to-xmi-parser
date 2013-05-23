import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;

import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ModelClass {

    private String name;
    private String extendedSuperClass;
    private List<String> inheritedSuperClasses;
    // private HashMap<String, ModelClass> classes;
    private HashMap<String, ModelVariable> variables;
    private HashMap<String, ModelMethod> methods;

    public ModelClass(Name name, Tree extendedSuperClass, List<? extends Tree> inheritedSuperClasses,
                      List<? extends Tree> members) {
        // Name
        this.name = name.toString();
        // Extended Class
        if (extendedSuperClass == null) {
            this.extendedSuperClass = "null";
        } else {
            this.extendedSuperClass = extendedSuperClass.toString();
        }
        // Implemented Classes
        for (Tree i : inheritedSuperClasses) {
            this.inheritedSuperClasses.add(i.toString());
        }
        setVariablesAndMethods(members);
    }

    private void setVariablesAndMethods(List<? extends Tree> members) {
        this.variables = new HashMap<String, ModelVariable>();
        this.methods = new HashMap<String, ModelMethod>();
        for (Tree m : members) {
            if (m.getKind() == Tree.Kind.VARIABLE) {
                VariableTree vt = (VariableTree) m;
                ModelVariable mv = new ModelVariable(vt.getName(), vt.getType(), false);
                variables.put(mv.getName(), mv);
            } else if (m.getKind() == Tree.Kind.METHOD) {
                ModelMethod mm = generateModelMethod((MethodTree) m);
                methods.put(mm.getName(), mm);
            } else {
                System.out.println("DEBUG: Unexpected Tree.Kind in ModelClass.setVariablesAndMethods " +
                        "(string: " + m.toString() +
                        ", kind: " + m.getKind());
            }
        }
    }

    private ModelMethod generateModelMethod(MethodTree methodTree) {
        Name name = methodTree.getName();
        List<? extends VariableTree> params = methodTree.getParameters();
        Tree returnType = methodTree.getReturnType();
        List<? extends StatementTree> bodyStatements = methodTree.getBody().getStatements();
        List<StatementTree> localVariables = new LinkedList<StatementTree>();
        for (StatementTree s : bodyStatements) {
            if (s.getKind() == Tree.Kind.VARIABLE) {
                // Only detects variable initializations; ignores value resets
                localVariables.add(s);
            }
        }

        return new ModelMethod(name, params, localVariables, returnType);
    }

    public String getName() {
        return this.name;
    }

}
