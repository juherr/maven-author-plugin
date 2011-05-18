package net.aneantis.mojo.author;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.model.Developer;

/**
 * 
 * @author juherr
 *
 * @phase process-sources
 * @goal file
 */
public final class FileMojo extends AbstractAuthorMojo {

    private final Map<String, List<Developer>> filesAuthors = new HashMap<String, List<Developer>>();
    /**
     * The directory for interpolated authors.xml.
     *
     * @parameter expression="${project.build.directory}/files.xml"
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

            if (cls.isInner()) {
                getLog().info("cls " + cls.getFullyQualifiedName()+" is inner and won't be counted in author plugin");
                continue;
            }

            DocletTag[] authors = cls.getTagsByName("author");
            if (authors.length <= 0) {
                addDevelopersToClass(Collections.EMPTY_SET, cls);
                ;
            }
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

        try {
            new AuthorXmlWriter(encoding, getLog()).write(new FileXmlWriterContext(output, getNumberOfJavaSource(), filesAuthors));
        } catch (AuthorPluginException e) {
            throw new MojoExecutionException("Failed to generate files.xml", e);
        }
    }

    private void addDevelopersToClass(final Set<Developer> developers, final JavaClass cls) {
        List<Developer> dev = new ArrayList<Developer>(developers);
        filesAuthors.put(cls.getFullyQualifiedName(), dev);
    }
}
