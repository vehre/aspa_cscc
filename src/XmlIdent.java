import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlIdent {
    public static void main(String[] args) throws IOException, DocumentException {
        InputStream is;
        if (args.length > 0) {
            is = new FileInputStream(args[0]);
        } else {
            is = System.in;
        }
        SAXReader reader = new SAXReader();
        Document doc = reader.read(is);
        XMLWriter xwriter = new XMLWriter(System.out,
        OutputFormat.createPrettyPrint());
        xwriter.write(doc);
        xwriter.close();
    }
}
