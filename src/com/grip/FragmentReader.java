package com.grip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FragmentReader extends DefaultHandler {
   private final List imagesList = new ArrayList();
   private final List fragmentList = new ArrayList();
   private final String contentRoot;
   private final boolean isResource;
   private SourceImage lastSourceImage = null;

   public FragmentReader(String contentRoot, boolean isResource) throws IOException {
      this.contentRoot = contentRoot;
      this.isResource = isResource;
   }

   public void startDocument() throws SAXException {
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
      String imageName = null;
      TransparencyMode alpha = null;
      Float x = null;
      Float y = null;
      Float w = null;
      Float h = null;

      for(int i = 0; i < attributes.getLength(); ++i) {
         String attributeName = attributes.getQName(i);
         String attributeValue = attributes.getValue(i);
         if (qName.equals("Image")) {
            if (attributeName.equals("name_imageType_0")) {
               imageName = attributeValue + ".png";
            } else if (attributeName.equals("alpha")) {
               alpha = TransparencyMode.valueOf(attributeValue.toUpperCase());
            }

            if (imageName != null && alpha != null) {
               try {
                  this.imagesList.add(this.lastSourceImage = new SourceImage(this.contentRoot, this.isResource, imageName, alpha));
               } catch (IOException var16) {
                  IOException e = var16;
                  e.printStackTrace();
               }
            }
         } else if (qName.equals("Fragment")) {
            if (this.lastSourceImage == null) {
               break;
            }

            if (attributeName.equals("name_utf_0")) {
               imageName = attributeValue;
            } else if (attributeName.equals("x_short_1")) {
               x = Float.valueOf(attributeValue);
            } else if (attributeName.equals("y_short_2")) {
               y = Float.valueOf(attributeValue);
            } else if (attributeName.equals("w_short_3")) {
               w = Float.valueOf(attributeValue);
            } else if (attributeName.equals("h_short_4")) {
               h = Float.valueOf(attributeValue);
            }

            if (imageName != null && x != null && y != null && w != null && h != null) {
               Fragment fragment = new Fragment(this.lastSourceImage, imageName, x, y, w, h);
               this.lastSourceImage.addFragment(fragment);
               this.fragmentList.add(fragment);
            }
         }
      }

   }

   public void endDocument() throws SAXException {
   }

   public List getFragments() {
      return this.fragmentList;
   }
}
