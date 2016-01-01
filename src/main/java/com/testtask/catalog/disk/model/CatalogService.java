package com.testtask.catalog.disk.model;

import java.util.List;

public interface CatalogService {
    
    /**
     * Get All Disk from catalog.xml
     * @return all Disks
     */
    public List<Disk> findAll();
    
    /**
     * Search for Disks in catalog.xml by keyword and Disk diameter
     * @param keyword in Disk name for searching
     * @param diameter of Disk for searching
     * @return Disks that matches searching parameters
     */
    public List<Disk> search(String keyword, Integer diameter);
    
    /**
     * Remove disk from catalog by Disk Id
     * @param id of selected Disk
     * @return true if removing successful 
     */
    public boolean removeDisk(int id);
    
    /**
     * Adding new Disk to catalog.xml
     * @param disk to add to catalog
     * @return disk instance from catalog.xml
     */
    public Disk addDisk(Disk disk);
    
    /**
     * Editing selected disk
     * @param disk 
     * @return true if editing successful
     */
    public boolean editDisk(Disk disk);

}
