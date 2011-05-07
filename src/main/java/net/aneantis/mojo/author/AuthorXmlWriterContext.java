package net.aneantis.mojo.author;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Developer;

/**
 *
 * @author juherr
 */
public final class AuthorXmlWriterContext {

    private final File destinationFile;
    private final int sourceFileNumber;
    private final Map<Developer, List<String>> authorsFiles;

    public AuthorXmlWriterContext(final File destinationFile, final int sourceFileNumber, final Map<Developer, List<String>> authorsFiles) {
        this.destinationFile = destinationFile;
        this.sourceFileNumber = sourceFileNumber;
        this.authorsFiles = authorsFiles;
    }

    public File getDestinationFile() {
        return destinationFile;
    }

    public int getSourceFileNumber() {
        return sourceFileNumber;
    }

    public Map<Developer, List<String>> getAuthorsFiles() {
        return authorsFiles;
    }
}
