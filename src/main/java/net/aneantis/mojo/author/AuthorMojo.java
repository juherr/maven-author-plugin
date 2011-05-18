package net.aneantis.mojo.author;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.maven.model.Developer;

/**
 * 
 * @author juherr
 *
 * @phase process-sources
 * @goal author
 */
public final class AuthorMojo extends AbstractAuthorMojo {

    private final Map<Developer, List<String>> authorsFiles = new HashMap<Developer, List<String>>();
    /**
     * The directory for interpolated authors.xml.
     *
     * @parameter expression="${project.build.directory}/authors.xml"
     * @required
     * @readonly
     */
    private File output;
    /**
     * Character encoding for the auto-generated deployment file(s).
     *
     * @parameter default-value="UTF-8"
     */
    private String encoding;

    @Override
    public void execute() throws MojoExecutionException {

        JavaClass[] classes = getJavaClasses();
        for (JavaClass cls : classes) {
            getLog().debug("process " + cls.getFullyQualifiedName());

            DocletTag[] authors = cls.getTagsByName("author");
            for (DocletTag author : authors) {

                Set<Developer> developers = getDevelopers(author.getValue());
                if (developers.isEmpty()) {
                    getLog().warn(author.getValue() + " is an unknown developer. You must add him in pom.xml file, section <developers>.");
                } else {
                    getLog().info("calling add with "+DevelopersToString(developers)+"to class " + cls.getFullyQualifiedName());
                    addDevelopersToClass(developers, cls);
                }
            }
        }

        for (Entry<Developer, List<String>> entry : authorsFiles.entrySet()) {
            getLog().info("author " + entry.getKey().getId() + " have files :");
            for (String s : entry.getValue()) {
                getLog().info(s);
            }
        }

        try {
            new AuthorXmlWriter(encoding, getLog()).write(new AuthorXmlWriterContext(output, getNumberOfJavaSource(), authorsFiles));
        } catch (AuthorPluginException e) {
            throw new MojoExecutionException("Failed to generate authors.xml", e);
        }
    }

    private void addDevelopersToClass(final Set<Developer> developers, final JavaClass cls) {
        getLog().info("adding developpers "+DevelopersToString(developers)+"to class " + cls.getFullyQualifiedName());
        for (Developer developer : developers) {
            getLog().info("developer : " + developer.getId());
            List<String> classes = authorsFiles.get(developer);
            if (classes == null) {
                classes = new ArrayList<String>();
                authorsFiles.put(developer, classes);
            }
            getLog().info("putting developer " + developer.getId() + " to class " + cls.getFullyQualifiedName());
            classes.add(cls.getFullyQualifiedName());
        }
    }

    private String ListToString(List<String> l) {
        StringBuilder res = new StringBuilder();
        for(String s : l ) {
            res.append(s).append(" ");
        }
        return res.toString();
    }

    private String DevelopersToString(Set<Developer> l) {
        StringBuilder res = new StringBuilder();
        for(Developer d : l ) {
            res.append(d.getId()).append(" ");
        }
        return res.toString();
    }
}
