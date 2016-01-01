package com.testtask.catalog.disk.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.testtask.catalog.disk.CatalogParser;

public class CatalogServiceImpl implements CatalogService {

    CatalogParser parser = new CatalogParser();
    
    public List<Disk> findAll() { 
        
        List<Disk> list=null;
        try {
            list = parser.parseXML();
        } catch (ParserConfigurationException e) {
            System.out.println("parsing error");
            e.printStackTrace();
        }        
        return list;
    }

    
    public List<Disk> search(String keyword, Integer diameter) {
                
        List<Disk> list=null;
        try {
            list = parser.parseXML();
        } catch (ParserConfigurationException e) {
            System.out.println("parsing error");
            e.printStackTrace();
        }
        List<Disk> returnList = new ArrayList<Disk>();
        for (Disk disk : list) {
            if(disk.getModel().toLowerCase().matches("(.*)"+keyword.toLowerCase()+"(.*)") && 
               disk.getDiameter()==diameter){
                returnList.add(disk);
            }
        }
        return returnList;
    }

    
    public boolean removeDisk(int id) {
        boolean result = false;
        try {
            result = parser.removeDiskFromFile(id);
        } catch (ParserConfigurationException e) {
            System.out.println("parsing error");
            e.printStackTrace();
        }      
        return result;

    }

   
    public Disk addDisk(Disk disk) {
        try {
            int id = parser.addNewDiskToFile(disk);
            disk.setId(id);            
            return disk;
        } catch (ParserConfigurationException e) {
            System.out.println("parsing error");
            e.printStackTrace();
            return null;
        }
    }

   
    public boolean editDisk(Disk disk) {
        boolean result = false;
        try {
            result = parser.editDiskInFile(disk);
        } catch (ParserConfigurationException e) {
            System.out.println("parsing error");
            e.printStackTrace();
        }      
        return result;
    }

}
