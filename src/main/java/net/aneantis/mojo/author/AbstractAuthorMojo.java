/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.aneantis.mojo.author;

import com.thoughtworks.qdox.JavaDocBuilder;
import org.codehaus.plexus.util.DirectoryScanner;
import com.thoughtworks.qdox.model.JavaClass;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Developer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author flgourie
 */
public abstract class AbstractAuthorMojo extends AbstractMojo {
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project = null;

    protected Set<Developer> getDevelopers(final String authorValue) {
        Set<Developer> result = new HashSet<Developer>();
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
        if (!result.isEmpty() && result.size() != authorValue.split(",").length) {
            getLog().warn("The convention is not respected for \"" + authorValue + "\". See http://download.oracle.com/javase/1.5.0/docs/tooldocs/windows/javadoc.html#@author");
        }

        return result;
    }

    protected JavaClass[] getJavaClasses() {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(project.getBuild().getSourceDirectory()));
        return builder.getClasses();
    }

    protected int getNumberOfJavaSource() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(project.getBuild().getSourceDirectory());
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.scan();
        return scanner.getIncludedFiles().length;
    }
}
