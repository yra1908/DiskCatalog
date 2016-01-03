package com.testtask.catalog.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.testtask.catalog.disk.model.Disk;

/**
 * Utility class for parsing file catalog.xml and working
 * with data inside file 
 */
public class CatalogParser {    
    
    public static final String FILE_NAME = "catalog.xml";    
    public static final String RESOURCE_DIR = "/temp/";        
    private static final String DISK_ID = "id";
    private static final String DISK_CONTENT = "content";
    private static final String DISK = "disk"; 
    
    /**
     * Retrieve all Disks from catalog.xml 
     * @return List<Disk> with all disks in file
     * @throws ParserConfigurationException
     */
    public List<Disk> parseXML() throws ParserConfigurationException{ 
        
        List<Disk> list = new ArrayList<Disk>();
        
        File file = new File(getPathToFile());
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = null;
        
        try {
            doc = dBuilder.parse(file);
        } catch (SAXException e) {            
            e.printStackTrace();
        } catch (IOException e) {            
            e.printStackTrace();
        }
        
        doc.getDocumentElement().normalize();        

        NodeList nList = doc.getElementsByTagName(DISK);       
        
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                
                int id =Integer.parseInt(eElement.getElementsByTagName(DISK_ID).item(0).getTextContent());
                String json = eElement.getElementsByTagName(DISK_CONTENT).item(0).getTextContent();                
                Disk disk = getDisk(id, json);
                list.add(disk);
            }
        }     
        
        return list;
    }
    
    /**
     * Add new Disk object to catalog.xml
     * @param disk object to add to XML file
     * @return disk object from catalog.xml
     * @throws ParserConfigurationException
     */
    public int addNewDiskToFile(Disk disk) throws ParserConfigurationException{
         
        String path = getPathToFile();
        File file = new File(path);
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = null;
        
        try {
            doc = dBuilder.parse(file);
        } catch (SAXException e) {            
            e.printStackTrace();
        } catch (IOException e) {            
            e.printStackTrace();
        }
        
        doc.getDocumentElement().normalize();
        
        Element root = doc.getDocumentElement();

        NodeList nList = doc.getElementsByTagName(DISK);         
        
        //new id equals id of last element plus one
        int newId = Integer.parseInt(nList.item(nList.getLength()-1).getChildNodes().item(1).getTextContent()) +1;
        
        //inserting new Disc to the end of XML file        
        disk.setId(newId);
        String json = diskToJSON(disk);
        
        Element newDisk = doc.createElement(DISK);
        Element newDiskId = doc.createElement(DISK_ID);
        Element newDiskContent = doc.createElement(DISK_CONTENT);
        
        newDiskId.appendChild(doc.createTextNode(String.valueOf(newId)));
        newDiskContent.appendChild(doc.createTextNode(json));
        newDisk.appendChild(newDiskId);
        newDisk.appendChild(newDiskContent);
        root.appendChild(newDisk);
        
        try {
            // write new content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult resultStream = new StreamResult(new File(path));
            transformer.transform(source, resultStream);
        } catch (Exception e) {
            System.out.println("error parsing");            
        }
        return newId;
    }
    
    /**
     * Remove Disk from catalog.xml by it's Id
     * @param id of Disk
     * @return boolean -true if removing successful
     * @throws ParserConfigurationException
     */
    public boolean removeDiskFromFile(int id) throws ParserConfigurationException{
        
        String path = getPathToFile();
        File file = new File(path);
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = null;
        boolean result = false;        
        
        try {
            doc = dBuilder.parse(file);
        } catch (SAXException e) {            
            e.printStackTrace();
        } catch (IOException e) {            
            e.printStackTrace();
        }
        
        doc.getDocumentElement().normalize();
        
        Element root = doc.getDocumentElement();

        NodeList nList = doc.getElementsByTagName(DISK);       
        
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            NodeList list = nNode.getChildNodes();            
            if(list.item(1).getTextContent().equals(String.valueOf(id))){                
                root.removeChild(nNode);
                result = true;
            }            
        }       
        
        try {
            // write new content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult resultStream = new StreamResult(new File(path));
            transformer.transform(source, resultStream);
        } catch (Exception e) {
            System.out.println("error parsing");
            return false;
        }             
        return result;
    }
    
    /**
     * Editing disk object in catalog.xml
     * @param disk object with new parameters
     * @return true if editing successful
     * @throws ParserConfigurationException
     */
    public boolean editDiskInFile(Disk disk) throws ParserConfigurationException {
        
        String path = getPathToFile();
        File file = new File(path);
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = null;
        boolean result = false;
        
        try {
            doc = dBuilder.parse(file);
        } catch (SAXException e) {            
            e.printStackTrace();
        } catch (IOException e) {            
            e.printStackTrace();
        }
        
        doc.getDocumentElement().normalize();        

        NodeList nList = doc.getElementsByTagName(DISK);       
        
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            NodeList list = nNode.getChildNodes();            
            if(list.item(1).getTextContent().equals(String.valueOf(disk.getId()))){ 
                String json = diskToJSON(disk);                
                for (int j = 0; j < list.getLength(); j++) {
                    Node node = list.item(j);                    
                    if(DISK_CONTENT.equals(node.getNodeName())){
                        node.setTextContent(json);
                        result=true; 
                    }
                }    
            }
        } 
        
        try {
            // write new content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult resultStream = new StreamResult(new File(path));
            transformer.transform(source, resultStream);
        } catch (Exception e) {
            System.out.println("error parsing");
            return false;
        }             
        return result;
    }


    private Disk getDisk(int id, String json) {
        Disk disk = new Disk();
        try {
            JSONObject obj = new JSONObject(json);
            
            disk.setId(id);
            disk.setModel(obj.getString("model"));
            disk.setDiameter(obj.getInt("diameter"));
            disk.setColor(obj.getString("color"));
            disk.setWeight(obj.getDouble("weight"));
            disk.setPrice(obj.getDouble("price"));
            disk.setMaterial(obj.getString("material"));
            disk.setDescription(obj.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }              
        return disk;
    }     

    private String diskToJSON(Disk disk) {
        ObjectWriter ow = new ObjectMapper().writer();        
        String json = null;
        try {
            json = ow.writeValueAsString(disk);
        } catch (JsonGenerationException e) {           
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
        return json;
    }  
    
    public static String getPathToFile(){
        String filePath = System.getProperty("catalina.base").replace("\\", "/")+RESOURCE_DIR + FILE_NAME;
        return filePath;
    }
    
    /* Another way of looking for filePath
     * private String getPathToFile(){
         String allPath = getClass().getResource(FILE_NAME).getPath();  // /C:/dev/STSworkspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/DiskCatalog/WEB-INF/classes/com/testtask/catalog/disk/catalog.xml
         String newPath = allPath.replace(PATH_TO_REMOVE, PATH_TO_ADD);
         System.out.println("new - "+newPath);
         System.out.println("old - "+allPath);
         return newPath;
     }*/ 
    
}
