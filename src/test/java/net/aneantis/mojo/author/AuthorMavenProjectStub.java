package net.aneantis.mojo.author;

import java.util.Arrays;
import java.util.List;
import org.apache.maven.model.Developer;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 *
 * @author juherr
 */
public class AuthorMavenProjectStub extends MavenProjectStub {

    private final List<Developer> developers;

    public AuthorMavenProjectStub(Developer... developers) {
        this.developers = Arrays.asList(developers);
    }

    @Override
    public List<Developer> getDevelopers() {
        return developers;
    }
}
