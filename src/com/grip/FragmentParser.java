package com.grip;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FragmentParser extends DefaultHandler {
   final DataOutputStream outputStream;
   final List fragmentCounts;
   final Iterator fragmentCountsIterator;

   public FragmentParser(String outputFile, int imageCount, List fragmentCounts) throws IOException {
      this.outputStream = new DataOutputStream(new FileOutputStream(outputFile));
      this.outputStream.writeShort(imageCount);
      this.fragmentCounts = fragmentCounts;
      this.fragmentCountsIterator = this.fragmentCounts.iterator();
   }

   public void startDocument() throws SAXException {
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
      String key = qName;
      System.out.println(key);

      for(int i = 0; i < attributes.getLength(); ++i) {
         System.out.print("      ");
         String name = attributes.getQName(i);
         System.out.print(name);
         System.out.print(" - ");
         System.out.println(attributes.getValue(name));
         String attributesValue = attributes.getValue(i);
         IOException e;
         if (qName.equals("Image")) {
            switch (i) {
               case 0:
                  try {
                     this.outputStream.writeUTF(attributesValue + ".png");
                  } catch (IOException var13) {
                     e = var13;
                     e.printStackTrace();
                  }
                  break;
               case 1:
                  try {
                     this.outputStream.writeShort((Integer)this.fragmentCountsIterator.next());
                  } catch (IOException var11) {
                     e = var11;
                     e.printStackTrace();
                  }

                  try {
                     this.outputStream.writeUTF(attributesValue);
                  } catch (IOException var10) {
                     e = var10;
                     e.printStackTrace();
                  }
            }
         } else if (qName.equals("Fragment")) {
            try {
               if (i == 0) {
                  this.outputStream.writeUTF(attributesValue);
               } else {
                  float value = Float.parseFloat(attributesValue);
                  this.outputStream.writeFloat(value);
               }
            } catch (IOException var12) {
               e = var12;
               e.printStackTrace();
            }
         }
      }

   }

   public void endDocument() throws SAXException {
   }
}
