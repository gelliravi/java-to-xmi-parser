import java.util.HashMap;

public class ManagerOfModels {

    private HashMap<String, ModelClass> classes;
    //private HashMap<String, ModelMethod> methods;
    //private HashMap<String, ModelVariable> variables;

    public ManagerOfModels() {
        this.classes = new HashMap<String, ModelClass>();
        //this.methods = new HashMap<String, ModelMethod>();
        //this.variables = new HashMap<String, ModelVariable>();
    }

    public void addClass(ModelClass mc) {

    }

    /*public void addMethod(ModelMethod mm) {
        this.methods.put(mm.getName(), mm);
    }*/

    /*public void addVariable(ModelVariable mv) {

    }*/
}
