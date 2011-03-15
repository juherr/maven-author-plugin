package com.google.code.mojo.author;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Developer;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author juherr
 *
 * @goal author
 * @phase process-sources
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
        ClassLibrary lib = builder.getClassLibrary();
        try {
            // TODO scan for sources and add them
            builder.addSource(sourceDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Problem with the source directory.", e);
        }

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
                List<Developer> developers = project.getDevelopers();
                for (Developer developer : developers) {
                    if (containDeveloper(author.getValue(), developer)) {
                        addClass(developer, cls);
                    }
                }
            }
        }

        // TODO generate result file
        getLog().warn(authorsFiles.toString());
    }

    private static boolean containDeveloper(final String authorValue, final Developer developer) {
        if (authorValue.contains(developer.getId())) {
            return true;
        }
        if (authorValue.contains(developer.getEmail())) {
            return true;
        }
        if (authorValue.contains(developer.getName())) {
            return true;
        }
        return false;
    }

    private void addClass(final Developer developer, final JavaClass cls) {
        List<JavaClass> classes = authorsFiles.get(developer);
        if (classes == null) {
            classes = new ArrayList<JavaClass>();
            authorsFiles.put(developer, classes);
        }
        classes.add(cls);
    }
}
