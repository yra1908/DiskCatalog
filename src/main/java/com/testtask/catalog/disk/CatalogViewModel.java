package com.testtask.catalog.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.testtask.catalog.disk.model.CatalogService;
import com.testtask.catalog.disk.model.CatalogServiceImpl;
import com.testtask.catalog.disk.model.Disk;

public class CatalogViewModel {
    
    private Integer diskD;
    private Integer addDiskD; 
    private String keyword;
    private String message; 
    
    //fields for adding new Disk
    private String addModel;
    private String addColor;
    private Double addWeight;
    private Double addPrice;
    private String addMaterial;
    private String addDescription;
    
    private int idToRemove;    

    private Disk selectedDisk;
    private List<Disk> list;
    
    private CatalogService catalogService = new CatalogServiceImpl(); 
    

    public List<Integer> getDiameters() {
        return Disk.getDiameters();
    }
    
    @Init
    public void init() {
        setDiskD(19);
        setAddDiskD(19);  
        catalogService.initParser();
    } 
    
    public List<Disk> getList(){
        return list;
    }
    
    public void setSelectedDisk(Disk selectedDisk) {
        this.selectedDisk = selectedDisk;
    }
    public Disk getSelectedDisk() {
        return selectedDisk;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getDiskD() {
        return diskD;
    }

    public void setDiskD(Integer diskD) {
        this.diskD = diskD;
    }
    
    public Integer getAddDiskD() {
        return addDiskD;
    }

    public String getAddModel() {
        return addModel;
    }

    public void setAddModel(String addModel) {
        this.addModel = addModel;
    }

    public String getAddColor() {
        return addColor;
    }

    public void setAddColor(String addColor) {
        this.addColor = addColor;
    }

    public Double getAddWeight() {
        return addWeight;
    }

    public void setAddWeight(Double addWeight) {
        this.addWeight = addWeight;
    }

    public Double getAddPrice() {
        return addPrice;
    }

    public void setAddPrice(Double addPrice) {
        this.addPrice = addPrice;
    }

    public String getAddMaterial() {
        return addMaterial;
    }

    public void setAddMaterial(String addMaterial) {
        this.addMaterial = addMaterial;
    }

    public String getAddDescription() {
        return addDescription;
    }

    public void setAddDescription(String addDescription) {
        this.addDescription = addDescription;
    }

    public void setAddDiskD(Integer addDiskD) {
        this.addDiskD = addDiskD;
    }
    
    public int getIdToRemove() {
        return idToRemove;
    }

    public void setIdToRemove(int idToRemove) {
        this.idToRemove = idToRemove;
    }
    
    @Command
    @NotifyChange({"list", "message", "selectedDisk"})
    public void search(){
        selectedDisk=null;
        if(keyword==null){
            keyword="?";
        }
        keyword=keyword.replaceAll("[<>%$*,./]", "").replace("\\", "");
        list = catalogService.search(keyword, diskD);
        if (list.isEmpty()){
            setMessage("Nothing found");
            return;
        }
        setMessage("Found " + list.size() + " disk");
    }
    
    @Command
    @NotifyChange({"list", "message", "selectedDisk"})
    public void findAll(){
        selectedDisk=null;
        list = catalogService.findAll();
        setMessage("Found " + list.size() + " disks");
    }
    
    @Command
    @NotifyChange({"list", "message", "selectedDisk"})
    public void addNewDisk(){
        selectedDisk=null;
        Disk disk = buildDisk(); 
        if(disk==null){
            setMessage("Error. Empty input fields.");
            return;
        }
        Disk newDisk = catalogService.addDisk(disk);        
        if (newDisk == null){
            setMessage("Error. Disk doesn't added.");
        } else{
            setMessage("Disk successfuly added");
            list = new ArrayList<Disk>();
            list.add(newDisk);
        } 
    }
    
    
    @Command
    @NotifyChange({"list", "message", "selectedDisk"})
    public void removeDisk(){
        if(selectedDisk==null){
            setMessage("Choose disk from list.");
            return;
        }
        int id = selectedDisk.getId();
        boolean result = catalogService.removeDisk(id);
        if(result){
            setMessage("Disk successfuly removed");
            list = catalogService.findAll();
            selectedDisk=null;
        } else {
            setMessage("Error. Can't remove chosen disk.");
        }
    }
    
    @Command
    @NotifyChange({"list", "message", "selectedDisk"})
    public void editDisk(){
        if(selectedDisk==null){
            setMessage("Can't edit empty Disk.");
            return;
        }
        boolean result = catalogService.editDisk(selectedDisk);
        if(result){
            setMessage("Disk successfuly edited");
            list = catalogService.search(selectedDisk.getModel(), selectedDisk.getDiameter());
        } else {
            setMessage("Error. Can't edit selected disk.");
        }
    }  
    
    private Disk buildDisk() {
        if(addModel==null || addColor==null || addWeight==null || 
                addPrice == null || addMaterial==null){
            return null;
        }
        Disk disk= new Disk();
        try {            
            disk.setModel(addModel);
            disk.setDiameter(addDiskD);
            disk.setColor(addColor);
            disk.setWeight(addWeight);
            disk.setPrice(addPrice);
            disk.setMaterial(addMaterial);
            disk.setDescription(addDescription); 
        } catch (Exception e) {
            setMessage("Wrong input data!");
            e.printStackTrace();
        }
        return disk;
    }  

}
