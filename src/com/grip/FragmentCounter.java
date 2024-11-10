/*    */ package com.grip;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ public class FragmentCounter
/*    */   extends DefaultHandler
/*    */ {
/* 12 */   int imageCount = 0;
/* 13 */   int fragmentCount = 0;
/* 14 */   List<Integer> fragmentCounts = new ArrayList<Integer>();
/*    */   
/*    */   public List<Integer> getFragmentCounts() {
/* 17 */     return this.fragmentCounts;
/*    */   }
/*    */   
/*    */   public int getImageCount() {
/* 21 */     return this.imageCount;
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
/* 33 */     if (qName.equals("Image")) {
/* 34 */       if (this.imageCount > 0) {
/* 35 */         this.fragmentCounts.add(Integer.valueOf(this.fragmentCount));
/*    */       }
/* 37 */       this.imageCount++;
/* 38 */       this.fragmentCount = 0;
/*    */     } 
/*    */     
/* 41 */     if (qName.equals("Fragment")) {
/* 42 */       this.fragmentCount++;
/*    */     }
/*    */   }
/*    */   
/*    */   public void endDocument() throws SAXException {
/* 47 */     if (this.imageCount > 0)
/* 48 */       this.fragmentCounts.add(Integer.valueOf(this.fragmentCount)); 
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\FragmentCounter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */