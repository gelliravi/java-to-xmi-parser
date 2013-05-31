import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class DOMWriter {

    private Document doc;
    private IDManager idManager;

    public DOMWriter(IDManager idManager, HashMap<String, ModelClass> classes, String outputFilename) {
        // Create XMI document
        this.doc = new DocumentImpl();

        // Initialize idManager to manage "xmi:id"s
        this.idManager = idManager;

        // Generate parse tree
        Element xmi = generateXMIHeader();
        generateDocumentation(xmi);
        Element root = generateXMIRoot(xmi);
        parseModels(root, classes, outputFilename);
        setupProfileApplication(root);
        setupUnknownExternals(root);

        // Create the XMI document from the parse tree. See the XML4J parser documentation for more details.
        OutputFormat format = new OutputFormat(doc, "UTF-8", true);
        try {
            FileWriter file = new FileWriter(outputFilename);
            XMLSerializer serial = new XMLSerializer(file, format);
            serial.asDOMSerializer();
            serial.serialize(doc);
            file.close();
        } catch (IOException ex) {
            System.out.println("ERROR: File IO Exception");
            System.out.println(ex.getMessage());
        }
    }

    /* -- Overview of UML/XMI XML Elements --
    * packagedElement -> package or class
    * ownedAttribute -> class instance variable
    * ownedOperation -> class method
    * ownedParameter -> class method method parameter
    * generalization -> signifies that this class extends another class */

    private void setupUnknownExternals(Element root) {
        Element pe = pElement("uml:Package");
        pe.setAttribute("name", "Unknown Externals");
        for (String ue : idManager.unknownExternalIDs.keySet()) {
            Element e = pElement("uml:Class");
            e.setAttribute("name", ue);
            e.setAttribute("xmi:id", idManager.getID(ue)); // Overwrite the id set in 'pElement()'
            pe.appendChild(e);
        }
        root.appendChild(pe);
    }

    private void setupProfileApplication(Element root) {
        Element pe = pElement("uml:Profile");
        pe.setAttribute("name", "Java Profile");
        pe.setAttribute("xmi:id", idManager.getProfileID()); // Overwrite the id set in 'pElement()'
        for (String primitive : idManager.primitiveIDs.keySet()) {
            Element e = pElement("uml:PrimitiveType");
            e.setAttribute("name", primitive);
            e.setAttribute("xmi:id", idManager.getID(primitive)); // Overwrite the id set in 'pElement()'
            pe.appendChild(e);
        }
        root.appendChild(pe);
    }

    // Generate a packagedElement for each class
    private void parseModels(Element root, HashMap<String, ModelClass> classes, String outputFilename) {
        Element pack = pElement("uml:Package");
        pack.setAttribute("name", outputFilename);
        root.appendChild(pack);
        // Generate classes
        for (ModelClass mc : classes.values()) {
            Element c = generateClass(mc);
            // If it extends another class, setup the association as a generalization
            if (mc.getExtendedSuperClass() != null) generateGeneralization(c, mc.getExtendedSuperClass());
            // Set an interfaceRealization for each interface this class inherits
            for (String i : mc.inheritedSuperClasses) {
                generateInterfaceRealization(c, mc.getName(), i);
            }
            // Generate a class's variables
            for (ModelVariable mv : mc.variables.values()) {
                Element v = ownedAttribute(mv.getName(), mv.getVisibility(), mv.getType());
                v.getAttribute("xmi:id");
                c.appendChild(v);
            }
            // Generate a class's methods
            for (ModelMethod mm : mc.methods.values()) {
                Element m = generateMethod(mm);
                c.appendChild(m);
            }
            pack.appendChild(c);
        }
        //setupAssociations(pack); Doesn't appear to be necessary
        Element profileApplication = element("profileApplication");
        profileApplication.setAttribute("xmi:type", "uml:ProfileApplication");
        profileApplication.setAttribute("xmi:id", idManager.generate());
        profileApplication.setAttribute("appliedProfile", idManager.getProfileID());
        pack.appendChild(profileApplication);
    }

    // Doesn't appear to be necessary
    /*private void setupAssociations(Element e) {
        // foreach association

        // corresponds to ownedAttribute's 'association' element
        Element pe = pElement("uml:Association");
        //String peID = idManager.generate();
        pe.setAttribute("xmi:id", peID);

        // corresponds to ownedAttribute
        Element memberEndOne = element("memberEnd");
        memberEndOne.setAttribute("xmi:idref", );

        Element ownedEnd = element("ownedEnd");
        String ownedEndID = idManager.generate();
        ownedEnd.setAttribute("xmi:id", ownedEndID);
        ownedEnd.setAttribute("type", ); // The class that the reference variable is an instance variable for

        // corresponds to ownedAttribute's 'association' element
        Element association = element("association");
        association.setAttribute("xmi:idref", peID);

        // corresponds to ownedEnd's ID
        Element memberEndTwo = element("memberEnd");
        memberEndTwo.setAttribute("xmi:idref", ownedEndID);

        ownedEnd.appendChild(association);
        pe.appendChild(memberEndOne);
        pe.appendChild(ownedEnd);
        pe.appendChild(memberEndTwo);
        e.appendChild(pe);
    }*/

    private void generateInterfaceRealization(Element e, String className, String interfaceName) {
        Element i = element("interfaceRealization");
        i.setAttribute("xmi:type", "uml:InterfaceRealization");
        i.setAttribute("xmi:id", idManager.generate());
        i.setAttribute("contract", idManager.getID(interfaceName));
        Element c = element("client");
        c.setAttribute("xmi:idref", idManager.getID(className));
        Element s = element("supplier");
        s.setAttribute("xmi:idref", idManager.getID(interfaceName));
        i.appendChild(c);
        i.appendChild(s);
        e.appendChild(i);
    }

    private void generateGeneralization(Element e, String superClass) {
        Element g = element("generalization");
        g.setAttribute("xmi:type", "uml:Generalization");
        g.setAttribute("xmi:id", idManager.generate());
        g.setAttribute("general", idManager.getID(superClass));
        e.appendChild(g);
    }

    // Generate an ownedOperation for each method (including constructors) of the class
    private Element generateMethod(ModelMethod mm) {
        Element e = ownedOperation(mm.getName(), mm.getVisibility());
        // Generate an ownedParameter for each parameter (only parameters; no local variables)
        for (ModelVariable mv : mm.variables.values()) {
            if (mv.isParameter()) {
                Element p = ownedParameter(mv.getName(), mv.getType());
                e.appendChild(p);
            }
        }
        // If the method has a return value...
        if (mm.getReturnType() != null) {
            Element r = returnOwnedParameter(mm.getReturnType());
            e.appendChild(r);
        }
        return e;
    }

    private Element returnOwnedParameter(String type) {
        Element e = element("ownedParameter");
        e.setAttribute("xmi:type", "uml:Parameter");
        e.setAttribute("xmi:id", idManager.generate());
        e.setAttribute("direction", "return");
        e.setAttribute("name", "return");
        setType(e, type);
        return e;
    }

    private Element ownedParameter(String name, String type) {
        Element e = element("ownedParameter");
        e.setAttribute("xmi:type", "uml:Parameter");
        e.setAttribute("xmi:id", idManager.generate());
        e.setAttribute("name", name);
        setType(e, type);
        return e;
    }

    private Element ownedOperation(String name, String visibility) {
        Element e = element("ownedOperation");
        e.setAttribute("xmi:type", "uml:Operation");
        e.setAttribute("xmi:id", idManager.generate());
        e.setAttribute("name", name);
        e.setAttribute("visibility", visibility);
        e.setAttribute("concurrency", "concurrent");
        return e;
    }

    private Element ownedAttribute(String name, String visibility, String type) {
        Element e = element("ownedAttribute");
        e.setAttribute("xmi:type", "uml:Property");
        e.setAttribute("xmi:id", idManager.generate());
        e.setAttribute("name", name);
        e.setAttribute("visibility", visibility);
        setType(e, type);
        return e;
    }

    private Element setType(Element e, String type) {
        if (type != null) {
            if (idManager.getID(type) != null) {
                e.setAttribute("type", idManager.getID(type));
            } else {
                // Type is not defined by source files, meaning that it must be either a class from the Java library
                // (ie. String) or void or a Java primitive type
                String id = idManager.generate(type);
                e.setAttribute("type", id);
            }
        }
        return e;
    }

    // Generate a packagedElement with the class name of type uml:Class
    private Element generateClass(ModelClass mc) {
        Element classElement = pElement("uml:Class");
        classElement.setAttribute("name", mc.getName());
        classElement.setAttribute("xmi:id", idManager.getID(mc.getName()));
        classElement.setAttribute("visibility", mc.getVisibility());
        return classElement;
    }

    // packagedElement
    private Element pElement(String xmiType) {
        Element packagedElement = element("packagedElement");
        packagedElement.setAttribute("xmi:type", xmiType);
        packagedElement.setAttribute("xmi:id", idManager.generate());
        return packagedElement;
    }

    private Element element(String title) {
        return doc.createElement(title);
    }

    private Text text(String content) {
        return doc.createTextNode(content);
    }

    private Element generateXMIRoot(Element xmi) {
        Element xmiRoot = element("uml:Model");
        xmiRoot.setAttribute("xmi:id", idManager.generate());
        xmiRoot.setAttribute("name", "Root");
        xmi.appendChild(xmiRoot);
        return xmiRoot;
    }

    private void generateDocumentation(Element xmi) {
        // Create documentation element
        Element documentation = element("xmi:Documentation");
        // -- exporter element
        Element exporter = element("exporter");
        exporter.appendChild(text(XMIGenerator.programName));
        // -- exporterVersion element
        Element exporterVersion = element("exporterVersion");
        exporterVersion.appendChild(text(XMIGenerator.programVersion));
        // -- contact element
        Element contact = element("contact");
        contact.appendChild(text(XMIGenerator.programEmail));

        documentation.appendChild(exporter);
        documentation.appendChild(exporterVersion);
        documentation.appendChild(contact);
        xmi.appendChild(documentation);
    }

    private Element generateXMIHeader() {
        // Create XMI element
        Element xmi = element("xmi:XMI");
        xmi.setAttribute("xmi:version", "2.0");
        xmi.setAttribute("xmlns:xmi", "http://www.omg.org/spec/XMI/20110701");
        xmi.setAttribute("xmlns:uml", "http://www.omg.org/spec/UML/20110701");
        doc.appendChild(xmi);
        return xmi;
    }
}
