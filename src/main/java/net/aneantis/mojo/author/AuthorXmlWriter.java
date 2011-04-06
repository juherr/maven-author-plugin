package net.aneantis.mojo.author;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.Developer;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 *
 * @author juherr
 */
public class AuthorXmlWriter {

    private final String encoding;

    public AuthorXmlWriter(final String encoding) {
        this.encoding = encoding;
    }

    public void write(final AuthorXmlWriterContext context) throws AuthorPluginException {
        Writer w = null;
        try {
            w = WriterFactory.newXmlWriter(context.getDestinationFile());
        }
        catch (IOException e) {
            throw new AuthorPluginException("Exception while opening file[" + context.getDestinationFile().getAbsolutePath() + "]", e);
        }

        XMLWriter writer = new PrettyPrintXMLWriter(w, encoding, null);
        writer.startElement("author");
        writer.addAttribute("sourceNumber", String.valueOf(context.getSourceFileNumber()));
        for (Map.Entry<Developer, List<String>> developer : context.getAuthorsFiles().entrySet()) {
            writer.startElement("developer");
            writer.addAttribute("id", developer.getKey().getId());
            String name = developer.getKey().getName() != null ? developer.getKey().getName() : "Unknown name";
            writer.addAttribute("name", name);
            String email = developer.getKey().getEmail() != null ? developer.getKey().getEmail() : "Unknown email";
            writer.addAttribute("email", email);

            for (String file : developer.getValue()) {
                writer.startElement("class");
                writer.writeText(file);
                writer.endElement();
            }
            writer.endElement();
        }
        writer.endElement();
        try {
            w.close();
        }
        catch (IOException ex) {
            // TODO log
        }
    }
}
