import javax.tools.*;
import java.io.File;
import java.io.IOException;

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
            System.out.println("ERROR: compiler == null");
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

            if (System.getProperty("sourcefind.debug", null) != null) {
                System.out.println(">>> Sources found:\n " + sources);
            }

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, sources);
            task.setProcessors(singleton(new CustomProcessor()));

            System.out.println(">>> Processing sources...");
            task.call();
        } catch (IOException ex) {
            System.out.println("ERROR: File IO Exception");
            System.out.println(ex.getMessage());
        }

        System.out.println(">>> Complete");
    }
}
