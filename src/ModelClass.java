import com.sun.source.tree.*;

import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ModelClass {

    private String name;
    private String extendedSuperClass;
    public List<String> inheritedSuperClasses;
    public HashMap<String, ModelVariable> variables;
    public HashMap<String, ModelMethod> methods;
    private String visibility;
    private boolean isInterface;

    public ModelClass(Name name, Tree extendedSuperClass, List<? extends Tree> inheritedSuperClasses,
                      List<? extends Tree> members, ModifiersTree mt) {
        // Name
        this.name = name.toString();
        // Extended Class
        if (extendedSuperClass == null) {
            this.extendedSuperClass = null;
        } else {
            this.extendedSuperClass = extendedSuperClass.toString();
        }
        // Implemented Classes
        this.inheritedSuperClasses = new LinkedList<String>();
        for (Tree i : inheritedSuperClasses) {
            this.inheritedSuperClasses.add(i.toString());
        }
        this.visibility = extractVisibility(mt);
        setVariablesAndMethods(members);
    }

    private void setVariablesAndMethods(List<? extends Tree> members) {
        this.variables = new HashMap<String, ModelVariable>();
        this.methods = new HashMap<String, ModelMethod>();
        for (Tree m : members) {
            if (m.getKind() == Tree.Kind.VARIABLE) {
                VariableTree vt = (VariableTree) m;
                ModelVariable mv = new ModelVariable(vt.getName(), vt.getType(), vt.getModifiers(), false);
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
        String name = methodTree.getName().toString();
        if (name.contains("init")) {
            // Rename constructor method(s) to match class name (needed for UMI/XMI)
            name = this.name;
        }
        List<? extends VariableTree> params = methodTree.getParameters();
        Tree returnType = methodTree.getReturnType();
        List<StatementTree> localVariables = new LinkedList<StatementTree>();
        // methodTree.getBody() == null if the class is an interface
        if (methodTree.getBody() == null) {
            this.isInterface = true;
        } else {
            this.isInterface = false;
            List<? extends StatementTree> bodyStatements = methodTree.getBody().getStatements();
            for (StatementTree s : bodyStatements) {
                if (s.getKind() == Tree.Kind.VARIABLE) {
                    // Only detects variable initializations; ignores value resets
                    localVariables.add(s);
                }
            }
        }
        return new ModelMethod(name, params, localVariables, returnType, methodTree.getModifiers());
    }

    // http://publib.boulder.ibm.com/infocenter/rsdvhelp/v6r0m1/index.jsp?topic=%2Fcom.ibm.xtools.viz.java.doc%2Ftopics%2Fcvisibility.html
    private String extractVisibility(ModifiersTree mt) {
        String mtString = mt.toString();
        if (mtString.contains("private")) return "private";
        if (mtString.contains("protected")) return "protected";
        if (mtString.contains("public")) return "public";
        if (mtString.contains("package")) return "package";
        System.out.print("WARNING: Unrecognized visibility for class '" + this.name + "'. ");
        System.out.print("Found: " + mt.toString() + ". ");
        System.out.print("UML only supports 'package', 'public', 'protected', or 'private'. ");
        System.out.println("Defaulting to 'public'.");
        return "public";
    }

    public String getName() {
        return this.name;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public String getExtendedSuperClass() {
        return this.extendedSuperClass;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

}
