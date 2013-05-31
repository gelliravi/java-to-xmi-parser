import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;

import javax.lang.model.element.Name;

public class ModelVariable {

    private String name;
    private String type;
    private String visibility;
    private boolean isParameter;

    public ModelVariable(String name, String type, ModifiersTree mt, boolean isParameter) {
        this.name = name;
        this.type = type;
        this.isParameter = isParameter;
        if (isParameter) {
            this.visibility = null;
        } else {
            this.visibility = extractVisibility(mt);
        }
    }

    public ModelVariable(Name name, Tree type, ModifiersTree visibility, boolean isParameter) {
        this(name.toString(), type.toString(), visibility, isParameter);
    }

    // http://publib.boulder.ibm.com/infocenter/rsdvhelp/v6r0m1/index.jsp?topic=%2Fcom.ibm.xtools.viz.java.doc%2Ftopics%2Fcvisibility.html
    private String extractVisibility(ModifiersTree mt) {
        String mtString = mt.toString();
        if (mtString.contains("private")) return "private";
        if (mtString.contains("protected")) return "protected";
        if (mtString.contains("public")) return "public";
        if (mtString.contains("package")) return "package";
        System.out.print("WARNING: Unrecognized visibility for variable '" + this.name + "'. ");
        System.out.print("Found: " + mt.toString() + ". ");
        System.out.print("UML only supports 'package', 'public', 'protected', or 'private'. ");
        System.out.println("Defaulting to 'public'.");
        return "public";
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public boolean isParameter() {
        return this.isParameter;
    }

}
