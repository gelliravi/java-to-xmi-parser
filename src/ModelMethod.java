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
    private HashMap<String, ModelVariable> variables = new HashMap<String, ModelVariable>();
    private String returnType;

    public ModelMethod(Name name, List<? extends VariableTree> params, List<StatementTree> localVariables, Tree returnType) {
        // Name
        this.name = name.toString();
        // Parameters
        for (VariableTree p : params) {
            ModelVariable mv = new ModelVariable(p.getName(), p.getType(), true);
            variables.put(mv.getName(), mv);
        }
        // Local Variables
        for (StatementTree lv : localVariables) {
            String variableType = extractVariableType(lv);
            String variableName = extractVariableName(lv);
            ModelVariable mv = new ModelVariable(variableName, variableType, false);
            variables.put(mv.getName(), mv);
        }
        // Return Type
        if (returnType == null) {
            this.returnType = "null"; // constructor (ie. <init>) methods don't have a return type
        } else {
            this.returnType = returnType.toString();
        }
    }

    public String getName() {
        return this.name;
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
