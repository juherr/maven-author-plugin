package net.aneantis.mojo.author;

import java.io.File;
import org.apache.maven.model.Build;
import org.apache.maven.model.Developer;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author juherr
 */
public class AuthorMojoTest extends AbstractMojoTestCase {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();
    }

    /**
     * @throws Exception
     */
    public void testMojoGoal() throws Exception {
        File testPom = getTestFile("src/test/resources/unit/basic-test/basic-test-plugin-config.pom");
        AuthorMojo mojo = (AuthorMojo) lookupMojo("author", testPom);


        Developer developer = new Developer();
        developer.setId("juherr");
        developer.setName("Julien Herr");
        developer.setEmail("julien@herr.com");
        Developer developer2 = new Developer();
        developer2.setId("flgourie");
        developer2.setName("Florian Gourier");
        developer2.setEmail("florian.gourier@gmail.com");
        MavenProject project = new AuthorMavenProjectStub(developer,developer2);

        Build build = new Build();
        build.setSourceDirectory(getTestPath("src/main/java"));
        project.setBuild(build);

        setVariableValueToObject(mojo, "project", project);
        setVariableValueToObject(mojo, "output", getTestFile("target/authors.xml"));

        assertNotNull(mojo);

        mojo.execute();

        File expected = getTestFile("target/authors.xml");
        assertTrue(expected.exists());
    }
}
