/*    */ package com.grip;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class FragmentReader
/*    */   extends DefaultHandler
/*    */ {
/* 13 */   private final List<SourceImage> imagesList = new ArrayList<SourceImage>();
/* 14 */   private final List<Fragment> fragmentList = new ArrayList<Fragment>();
/*    */   private final String contentRoot;
/*    */   private final boolean isResource;
/* 17 */   private SourceImage lastSourceImage = null;
/*    */   
/*    */   public FragmentReader(String contentRoot, boolean isResource) throws IOException {
/* 20 */     this.contentRoot = contentRoot;
/* 21 */     this.isResource = isResource;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void startDocument() throws SAXException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
/* 33 */     String key = qName;
/*    */ 
/*    */     
/* 36 */     String imageName = null;
/* 37 */     TransparencyMode alpha = null;
/*    */     
/* 39 */     Float x = null;
/* 40 */     Float y = null;
/* 41 */     Float w = null;
/* 42 */     Float h = null;
/*    */     
/* 44 */     for (int i = 0; i < attributes.getLength(); i++) {
/*    */       
/* 46 */       String attributeName = attributes.getQName(i);
/*    */ 
/*    */       
/* 49 */       String attributeValue = attributes.getValue(i);
/*    */ 
/*    */       
/* 52 */       if (qName.equals("Image")) {
/* 53 */         if (attributeName.equals("name_imageType_0")) {
/* 54 */           imageName = attributeValue + ".png";
/* 55 */         } else if (attributeName.equals("alpha")) {
/* 56 */           alpha = TransparencyMode.valueOf(attributeValue.toUpperCase());
/*    */         } 
/*    */         
/* 59 */         if (imageName != null && alpha != null) {
/*    */           try {
/* 61 */             this.imagesList.add(this.lastSourceImage = new SourceImage(this.contentRoot, this.isResource, imageName, alpha));
/* 62 */           } catch (IOException e) {
/* 63 */             e.printStackTrace();
/*    */           }
/*    */         
/*    */         }
/* 67 */       } else if (qName.equals("Fragment")) {
/* 68 */         if (this.lastSourceImage == null) {
/*    */           break;
/*    */         }
/* 71 */         if (attributeName.equals("name_utf_0")) {
/* 72 */           imageName = attributeValue;
/* 73 */         } else if (attributeName.equals("x_short_1")) {
/* 74 */           x = Float.valueOf(attributeValue);
/* 75 */         } else if (attributeName.equals("y_short_2")) {
/* 76 */           y = Float.valueOf(attributeValue);
/* 77 */         } else if (attributeName.equals("w_short_3")) {
/* 78 */           w = Float.valueOf(attributeValue);
/* 79 */         } else if (attributeName.equals("h_short_4")) {
/* 80 */           h = Float.valueOf(attributeValue);
/*    */         } 
/*    */         
/* 83 */         if (imageName != null && x != null && y != null && w != null && h != null) {
/* 84 */           Fragment fragment = new Fragment(this.lastSourceImage, imageName, x.floatValue(), y.floatValue(), w.floatValue(), h.floatValue());
/* 85 */           this.lastSourceImage.addFragment(fragment);
/* 86 */           this.fragmentList.add(fragment);
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void endDocument() throws SAXException {}
/*    */ 
/*    */   
/*    */   public List<Fragment> getFragments() {
/* 97 */     return this.fragmentList;
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\FragmentReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */