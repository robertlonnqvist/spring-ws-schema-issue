package com.tdc;

import org.springframework.core.io.ClassPathResource;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        SimpleXsdSchema schema = new SimpleXsdSchema(new ClassPathResource("schema.xsd"));
        schema.afterPropertiesSet();

        int threads = 10;

        ExecutorService service = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            service.execute(() -> parse(schema.getSource()));
        }
        service.shutdownNow();
    }

    private static void parse(Source source) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(source, new StreamResult(writer));
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

}
