import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("*")
public class CustomProcessor extends AbstractProcessor {

    private final CustomTreeVisitor visitor = new CustomTreeVisitor();
    private Trees trees;

    @Override
    public void init(ProcessingEnvironment pe) {
        super.init(pe);
        trees = Trees.instance(pe);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        for (Element e : roundEnvironment.getRootElements()) {
            TreePath tp = trees.getPath(e);
            visitor.scan(tp, trees);
        }
        return true;
    }
}
