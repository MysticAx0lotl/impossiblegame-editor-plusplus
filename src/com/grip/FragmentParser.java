/*    */ package com.grip;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.helpers.DefaultHandler;
/*    */ 
/*    */ 
/*    */ public class FragmentParser
/*    */   extends DefaultHandler
/*    */ {
/*    */   final DataOutputStream outputStream;
/*    */   final List<Integer> fragmentCounts;
/*    */   final Iterator<Integer> fragmentCountsIterator;
/*    */   
/*    */   public FragmentParser(String outputFile, int imageCount, List<Integer> fragmentCounts) throws IOException {
/* 21 */     this.outputStream = new DataOutputStream(new FileOutputStream(outputFile));
/* 22 */     this.outputStream.writeShort(imageCount);
/* 23 */     this.fragmentCounts = fragmentCounts;
/* 24 */     this.fragmentCountsIterator = this.fragmentCounts.iterator();
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
/* 36 */     String key = qName;
/* 37 */     System.out.println(key);
/*    */     
/* 39 */     for (int i = 0; i < attributes.getLength(); i++) {
/* 40 */       System.out.print("      ");
/* 41 */       String name = attributes.getQName(i);
/* 42 */       System.out.print(name);
/* 43 */       System.out.print(" - ");
/* 44 */       System.out.println(attributes.getValue(name));
/*    */       
/* 46 */       String attributesValue = attributes.getValue(i);
/* 47 */       if (qName.equals("Image")) {
/* 48 */         switch (i) {
/*    */           case 0:
/*    */             try {
/* 51 */               this.outputStream.writeUTF(attributesValue + ".png");
/* 52 */             } catch (IOException e) {
/* 53 */               e.printStackTrace();
/*    */             } 
/*    */             break;
/*    */           case 1:
/*    */             try {
/* 58 */               this.outputStream.writeShort(((Integer)this.fragmentCountsIterator.next()).intValue());
/* 59 */             } catch (IOException e) {
/* 60 */               e.printStackTrace();
/*    */             } 
/*    */             try {
/* 63 */               this.outputStream.writeUTF(attributesValue);
/* 64 */             } catch (IOException e) {
/* 65 */               e.printStackTrace();
/*    */             } 
/*    */             break;
/*    */         } 
/*    */       
/* 70 */       } else if (qName.equals("Fragment")) {
/*    */         try {
/* 72 */           if (i == 0) {
/* 73 */             this.outputStream.writeUTF(attributesValue);
/*    */           } else {
/* 75 */             float value = Float.parseFloat(attributesValue);
/* 76 */             this.outputStream.writeFloat(value);
/*    */           } 
/* 78 */         } catch (IOException e) {
/* 79 */           e.printStackTrace();
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void endDocument() throws SAXException {}
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\FragmentParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */