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

    private final Map<Developer, List<String>> authorsFiles = new HashMap<Developer, List<String>>();
    /**
     * Location of the file.
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourceDirectory;
    /**
     * The directory for interpolated authors.xml.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File outputDirectory;
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project = new MavenProject();
    /**
     * Character encoding for the auto-generated deployment file(s).
     *
     * @parameter default-value="UTF-8"
     */
    private String encoding;

    @Override
    public void execute() throws MojoExecutionException {

        JavaClass[] classes = getProjectSources();

        for (JavaClass cls : classes) {
            getLog().debug("process " + cls.getFullyQualifiedName());

            DocletTag[] authors = cls.getTagsByName("author");
            for (DocletTag author : authors) {
                getLog().info("author found in source: " + author.getValue());

                Set<Developer> developers = getDevelopers(author.getValue());
                if (developers.isEmpty()) {
                    getLog().warn(author.getValue() + " is an unknown developer. You must add him in pom.xml file, section <developers>.");
                } else {
                    addDevelopersToClass(developers, cls);
                }
            }
        }

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        File report = new File(outputDirectory, "authors.xml");
        AuthorXmlWriterContext context = new AuthorXmlWriterContext(report, classes.length, authorsFiles);
        try {
            new AuthorXmlWriter(encoding).write(context);
        }
        catch (AuthorPluginException e) {
            throw new MojoExecutionException("Failed to generate authors.xml", e);
        }
    }

    private Set<Developer> getDevelopers(final String authorValue) {
        Set<Developer> result = new TreeSet<Developer>();
        // get developers defined in the pom.xml
        List<Developer> developers = project.getDevelopers();
        for (Developer developer : developers) {
            // a developer could be mark in @author with his id/login, email or full name
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
        if (result.size() != authorValue.split(",").length) {
            getLog().warn("The convention is not respected for \"" + authorValue + "\". See http://download.oracle.com/javase/1.5.0/docs/tooldocs/windows/javadoc.html#@author");
        }

        return result;
    }

    private JavaClass[] getProjectSources() {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(sourceDirectory);
        return builder.getClasses();
    }

    private void addDevelopersToClass(final Set<Developer> developers, final JavaClass cls) {
        for (Developer developer : developers) {
            List<String> classes = authorsFiles.get(developer);
            if (classes == null) {
                classes = new ArrayList<String>();
                authorsFiles.put(developer, classes);
            }
            classes.add(cls.getFullyQualifiedName());
        }
    }
}
