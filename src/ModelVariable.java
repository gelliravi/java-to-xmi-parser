import com.sun.source.tree.Tree;

import javax.lang.model.element.Name;

public class ModelVariable {

    private String name;
    private String type;
    private boolean isParameter;

    public ModelVariable(String name, String type, boolean isParameter) {
        this.name = name;
        this.type = type;
        this.isParameter = isParameter;
    }

    public ModelVariable(Name name, Tree type, boolean isParameter) {
        this(name.toString(), type.toString(), isParameter);
    }

    public String getName() {
        return this.name;
    }

}
