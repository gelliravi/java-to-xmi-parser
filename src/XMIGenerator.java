import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static java.util.Collections.singleton;

public class XMIGenerator {
    public static void main(String[] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException("ERROR: Exactly one argument is required: the root directory to search for source codes.");
        }

        File searchDir = new File(args[0]);
        if (!searchDir.isDirectory()) {
            throw new IllegalArgumentException("ERROR: The provided path is not a directory: " + searchDir.getAbsolutePath());
        }

        System.out.println(">>> Searching for Java source code files under " + searchDir);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            System.out.println("ERROR: compiler == null. You most likely didn't setup tools.jar correctly. " +
                    "Check the README for further instructions.");
            System.exit(1);
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        System.out.println(">>> Detecting sources: ");

        try {
            fileManager.setLocation(StandardLocation.SOURCE_PATH, singleton(searchDir.getAbsoluteFile()));
            Iterable<JavaFileObject> sources = fileManager.list(
                    StandardLocation.SOURCE_PATH,
                    "",
                    singleton(JavaFileObject.Kind.SOURCE),
                    true);

            System.out.println(">>> Sources found:\n " + sources);

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, sources);

            CustomProcessor proc = new CustomProcessor();
            task.setProcessors(singleton(proc));

            System.out.println(">>> Processing sources...");
            task.call();

            System.out.println(">> Finished processing sources...");

            // Determine output file name
            System.out.print("Enter a filename for the output XMI file: ");
            Scanner input = new Scanner(System.in);
            String outputFilename = input.next();

            // Append '.xmi' to output file if necessary
            if (outputFilename.length() < 3) outputFilename += ".xmi";
            String fileExtension = outputFilename.substring(outputFilename.length() - 4, outputFilename.length());
            if (!fileExtension.equalsIgnoreCase(".xmi")) outputFilename += ".xmi";
            System.out.println("Output filename: " + outputFilename);

            System.out.println(">>> Generating XMI file...");

        } catch (IOException ex) {
            System.out.println("ERROR: File IO Exception");
            System.out.println(ex.getMessage());
        }

        System.out.println(">>> Complete");
    }
}
