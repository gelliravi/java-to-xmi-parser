import java.util.HashMap;

public class IDManager {

    private int count;
    private HashMap<String, String> classIDs;
    public HashMap<String, String> unknownExternalIDs;
    public HashMap<String, String> primitiveIDs;
    private String profileID;

    public IDManager() {
        count = 0;
        classIDs = new HashMap<String, String>();
        unknownExternalIDs = new HashMap<String, String>();
        initializePrimitives();
        profileID = null;
    }

    private void initializePrimitives() {
        primitiveIDs = new HashMap<String, String>();
        primitiveIDs.put("byte", generate());
        primitiveIDs.put("char", generate());
        primitiveIDs.put("short", generate());
        primitiveIDs.put("int", generate());
        primitiveIDs.put("long", generate());
        primitiveIDs.put("float", generate());
        primitiveIDs.put("double", generate());
        primitiveIDs.put("boolean", generate());
        primitiveIDs.put("void", generate());
    }

    public String generate() {
        count++;
        return String.valueOf(count - 1);
    }

    public String generate(ModelClass mc) {
        if (classIDs.containsKey(mc.getName())) return getID(mc.getName());
        String id = generate();
        classIDs.put(mc.getName(), id);
        return id;
    }

    public String generate(String className) {
        // Is a 'primitive' type
        if (primitiveIDs.containsKey(className)) return primitiveIDs.get(className);
        // Is an 'unknown external' type
        if (unknownExternalIDs.containsKey(className)) return unknownExternalIDs.get(className);
        String id = generate();
        unknownExternalIDs.put(className, id);
        return id;
    }

    public String getID(String className) {
        if (classIDs.containsKey(className)) return classIDs.get(className);
        if (primitiveIDs.containsKey(className)) return primitiveIDs.get(className);
        if (unknownExternalIDs.containsKey(className)) return unknownExternalIDs.get(className);
        // At this point, the className must be an 'unknown external' because ALL classIDs were already
        // generated during the parsing of the Java source code, and primitives are fixed.
        return generate(className);
    }

    public String getProfileID() {
        if (profileID == null) profileID = generate();
        return profileID;
    }

}
