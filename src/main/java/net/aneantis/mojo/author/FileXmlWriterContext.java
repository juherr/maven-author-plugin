package net.aneantis.mojo.author;

import java.io.File;
import java.util.Map;
import net.aneantis.mojo.author.xmldata.XMLFileInfo;

/**
 *
 * @author juherr
 */
public final class FileXmlWriterContext {

    private final File destinationFile;
    private final int sourceFileNumber;
    private final Map<String, XMLFileInfo> authorsFiles;

    public FileXmlWriterContext(final File destinationFile, final int sourceFileNumber, final Map<String, XMLFileInfo> authorsFiles) {
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

    public Map<String, XMLFileInfo> getAuthorsFiles() {
        return authorsFiles;
    }
}
