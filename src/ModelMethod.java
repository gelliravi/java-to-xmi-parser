import java.util.HashMap;
import java.util.LinkedList;

public class ModelMethod {

    private EnumScope scope;
    private String returnType;
    private String name;
    private HashMap<String, ModelVariable> variables = new HashMap<String, ModelVariable>();

    public ModelMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
