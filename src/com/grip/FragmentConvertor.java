package com.grip;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class FragmentConvertor {
   private static String convertToFileURL(String filename) {
      String path = (new File(filename)).getAbsolutePath();
      if (File.separatorChar != '/') {
         path = path.replace(File.separatorChar, '/');
      }

      if (!path.startsWith("/")) {
         path = "/" + path;
      }

      return "file:" + path;
   }

   public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
      String fileURL = convertToFileURL(args[0] + ".xml");
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      XMLReader xmlParser = saxParser.getXMLReader();
      FragmentCounter fragmentCounter = new FragmentCounter();
      xmlParser.setContentHandler(fragmentCounter);
      xmlParser.parse(fileURL);
      FragmentParser fragmentParser = new FragmentParser(args[0] + ".bin", fragmentCounter.getImageCount(), fragmentCounter.getFragmentCounts());
      xmlParser.setContentHandler(fragmentParser);
      xmlParser.parse(fileURL);
   }
}
