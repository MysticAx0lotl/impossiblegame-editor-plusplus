package com.grip;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FragmentCounter extends DefaultHandler {
   int imageCount = 0;
   int fragmentCount = 0;
   List fragmentCounts = new ArrayList();

   public List getFragmentCounts() {
      return this.fragmentCounts;
   }

   public int getImageCount() {
      return this.imageCount;
   }

   public void startDocument() throws SAXException {
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("Image")) {
         if (this.imageCount > 0) {
            this.fragmentCounts.add(this.fragmentCount);
         }

         ++this.imageCount;
         this.fragmentCount = 0;
      }

      if (qName.equals("Fragment")) {
         ++this.fragmentCount;
      }

   }

   public void endDocument() throws SAXException {
      if (this.imageCount > 0) {
         this.fragmentCounts.add(this.fragmentCount);
      }

   }
}
