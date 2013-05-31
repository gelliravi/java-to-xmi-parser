import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;

import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ModelMethod {

    private String name;
    public HashMap<String, ModelVariable> variables;
    private String returnType;
    private String visibility;

    public ModelMethod(String name, List<? extends VariableTree> params, List<StatementTree> localVariables, Tree returnType, ModifiersTree mt) {
        // Name
        this.name = name;
        // Initialize HashMap to store variables
        variables = new HashMap<String, ModelVariable>();
        // Parameter Variables
        for (VariableTree p : params) {
            ModelVariable mv = new ModelVariable(p.getName(), p.getType(), p.getModifiers(), true);
            variables.put(mv.getName(), mv);
        }
        // NOTE: Local variables are not necessary for UML/XMI. Leaving code in case it becomes useful in the future.
        // Local Variables
        /* for (StatementTree lv : localVariables) {
            String variableType = extractVariableType(lv);
            String variableName = extractVariableName(lv);
            ModelVariable mv = new ModelVariable(variableName, variableType, false);
            variables.put(mv.getName(), mv);
        }*/
        // Return Type
        if (returnType == null) {
            this.returnType = null; // constructor (ie. <init>) methods don't have a return type
        } else {
            this.returnType = returnType.toString();
        }
        this.visibility = extractVisibility(mt);
    }

    // http://publib.boulder.ibm.com/infocenter/rsdvhelp/v6r0m1/index.jsp?topic=%2Fcom.ibm.xtools.viz.java.doc%2Ftopics%2Fcvisibility.html
    private String extractVisibility(ModifiersTree mt) {
        String mtString = mt.toString();
        if (mtString.contains("private")) return "private";
        if (mtString.contains("protected")) return "protected";
        if (mtString.contains("public")) return "public";
        if (mtString.contains("package")) return "package";
        System.out.print("WARNING: Unrecognized visibility for method '" + this.name + "'. ");
        System.out.print("Found: " + mt.toString() + ". ");
        System.out.print("UML only supports 'package', 'public', 'protected', or 'private'. ");
        System.out.println("Defaulting to 'public'.");
        return "public";
    }

    public String getName() {
        return this.name;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public String getVisibility() {
        return this.visibility;
    }

    /* The following two methods extract the variable type and variable name.
       statement.toString() will be something like:
            "double dy = Math.abs(y - pt.getY());"
       Split into array of Strings using " " as a delimiter. The type will always be at index 0 and
       the variable will always be at index 1 of the resulting array. Note, the Java Compiler Tree
       API automatically formats statements to have the necessary statements, even if the original
       source code doesn't contain a space between the equals sign.
     */
    private String extractVariableType(StatementTree statement) {
        String[] split = statement.toString().split(" ");
        return split[0];
    }

    private String extractVariableName(StatementTree statement) {
        String[] split = statement.toString().split(" ");
        return split[1];
    }

}
