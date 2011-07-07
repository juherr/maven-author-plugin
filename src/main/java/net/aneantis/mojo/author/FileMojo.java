package net.aneantis.mojo.author;

import net.aneantis.mojo.author.xmldata.XMLFileInfo;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.maven.model.Developer;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author juherr
 *
 * @phase process-sources
 * @goal file
 */
public final class FileMojo extends AbstractAuthorMojo {

    private final Map<String, XMLFileInfo> filesAuthors = new HashMap<String, XMLFileInfo>();
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
                getLog().info("cls " + cls.getFullyQualifiedName() + " is inner and won't be counted in author plugin");
                continue;
            }

            DocletTag[] authors = cls.getTagsByName("author");
            JavaMethod[] methods = cls.getMethods();

            for (JavaMethod method : methods) {
                DocletTag[] methodAuthors = method.getTagsByName("author");
                if (methodAuthors.length <= 0) {
                    addMethodDeveloperToClass(Collections.EMPTY_SET, cls, method);
                }
                for (DocletTag author : methodAuthors) {
                    getLog().info("author found in source method " + method.getCallSignature() + " : " + author.getValue());

                    Set<Developer> developers = getDevelopers(author.getValue());
                    if (developers.isEmpty()) {
                        getLog().warn(author.getValue() + " is an unknown developer. You must add him in pom.xml file, section <developers>.");
                    } else {
                        addMethodDeveloperToClass(developers, cls, method);
                    }
                }
            }
            if (authors.length <= 0) {
                addDevelopersToClass(Collections.EMPTY_SET, cls);
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
        if (classes != null && classes.length != 0) {
            try {
                new AuthorXmlWriter(encoding, getLog()).write(new FileXmlWriterContext(output, getNumberOfJavaSource(), filesAuthors));
            } catch (AuthorPluginException e) {
                throw new MojoExecutionException("Failed to generate files.xml", e);
            }
        }
    }

    private void addDevelopersToClass(final Set<Developer> developers, final JavaClass cls) {
        getLog().debug("adding developers " + StringUtils.join(developers.iterator(), ",") + " to class " + cls.getFullyQualifiedName());
        if (filesAuthors.containsKey(cls.getFullyQualifiedName())) {
            XMLFileInfo xfi = filesAuthors.get(cls.getFullyQualifiedName());
            xfi.addMainAuthors(developers);
        } else {
            XMLFileInfo xfi = new XMLFileInfo();
            xfi.addMainAuthors(developers);
            filesAuthors.put(cls.getFullyQualifiedName(), xfi);
        }
    }

    private void addMethodDeveloperToClass(final Set<Developer> developers, final JavaClass cls, JavaMethod method) {
        getLog().debug("adding developers " + StringUtils.join(developers.iterator(), ",") + " to method " + method.getCallSignature() + " in class " + cls.getFullyQualifiedName());
        if (filesAuthors.containsKey(cls.getFullyQualifiedName())) {
            XMLFileInfo xfi = filesAuthors.get(cls.getFullyQualifiedName());
            xfi.addMethodAuthors(method, developers);
        } else {
            XMLFileInfo xfi = new XMLFileInfo();
            xfi.addMethodAuthors(method, developers);
            filesAuthors.put(cls.getFullyQualifiedName(), xfi);
        }
    }
}
