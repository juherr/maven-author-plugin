package net.aneantis.mojo.author;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.maven.model.Developer;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author juherr
 *
 * @phase process-sources
 * @goal author
 */
public class AuthorMojo extends AbstractMojo {

    private final Map<Developer, List<JavaClass>> authorsFiles = new HashMap<Developer, List<JavaClass>>();
    /**
     * Location of the file.
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourceDirectory;
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project = new MavenProject();

    @Override
    public void execute() throws MojoExecutionException {

        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(sourceDirectory);

        JavaClass[] classess = builder.getClasses();
        for (JavaClass cls : classess) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("process " + cls.getFullyQualifiedName());
            }
            DocletTag[] authors = cls.getTagsByName("author");
            for (DocletTag author : authors) {
                if (getLog().isDebugEnabled()) {
                    getLog().warn("author found in source: " + author.getValue());
                }

                Set<Developer> developers = getDevelopers(author.getValue());
                if (developers.isEmpty()) {
                    getLog().warn(author.getValue() + " is an unknown developer. You must add him in pom.xml file, section <developers>.");

                } else {
                    addClass(developers, cls);
                }
            }
        }

        // TODO generate result file
        getLog().warn(authorsFiles.toString());
    }

    private Set<Developer> getDevelopers(final String authorValue) {
        Set<Developer> result = new TreeSet<Developer>();
        List<Developer> developers = project.getDevelopers();
        for (Developer developer : developers) {
            if (authorValue.contains(developer.getId())) {
                result.add(developer);
                continue;
            }
            if (authorValue.contains(developer.getEmail())) {
                result.add(developer);
                continue;
            }
            if (authorValue.contains(developer.getName())) {
                result.add(developer);
                continue;
            }
        }

        return result;
    }

    private void addClass(final Set<Developer> developers, final JavaClass cls) {
        for (Developer developer : developers) {
            List<JavaClass> classes = authorsFiles.get(developer);
            if (classes == null) {
                classes = new ArrayList<JavaClass>();
                authorsFiles.put(developer, classes);
            }
            classes.add(cls);
        }
    }
}
