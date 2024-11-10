/*    */ package com.grip;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import javax.xml.parsers.ParserConfigurationException;
/*    */ import javax.xml.parsers.SAXParser;
/*    */ import javax.xml.parsers.SAXParserFactory;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.XMLReader;
/*    */ 
/*    */ 
/*    */ public class FragmentConvertor
/*    */ {
/*    */   private static String convertToFileURL(String filename) {
/* 15 */     String path = (new File(filename)).getAbsolutePath();
/* 16 */     if (File.separatorChar != '/') {
/* 17 */       path = path.replace(File.separatorChar, '/');
/*    */     }
/*    */     
/* 20 */     if (!path.startsWith("/")) {
/* 21 */       path = "/" + path;
/*    */     }
/* 23 */     return "file:" + path;
/*    */   }
/*    */   
/*    */   public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
/* 27 */     String fileURL = convertToFileURL(args[0] + ".xml");
/*    */     
/* 29 */     SAXParserFactory factory = SAXParserFactory.newInstance();
/*    */     
/* 31 */     SAXParser saxParser = factory.newSAXParser();
/* 32 */     XMLReader xmlParser = saxParser.getXMLReader();
/* 33 */     FragmentCounter fragmentCounter = new FragmentCounter();
/* 34 */     xmlParser.setContentHandler(fragmentCounter);
/* 35 */     xmlParser.parse(fileURL);
/*    */     
/* 37 */     FragmentParser fragmentParser = new FragmentParser(args[0] + ".bin", fragmentCounter.getImageCount(), fragmentCounter.getFragmentCounts());
/* 38 */     xmlParser.setContentHandler(fragmentParser);
/* 39 */     xmlParser.parse(fileURL);
/*    */   }
/*    */ }


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\TheImpossibleGame\editor\TheImpossibleGameEditor.jar!\com\grip\FragmentConvertor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */