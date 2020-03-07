package com.bbsmart.pda.blackberry.bbphoto.models;

import java.util.Hashtable;

import com.bbsmart.pda.blackberry.bbphoto.io.PersistenceManager;
import com.bbsmart.pda.blackberry.bbphoto.io.StoreUtil;

public final class GeneralOptions {
    private static final int STORE_INDX = 4;
    
    // Application Options
    public boolean app_DoScan;
    public int app_ViewSelection;
    public boolean app_PromptIMGDelete;
    public String app_Theme;
    
    // Privacy Options
    public boolean privacy_ShowHide;
    public String privacy_Password;
    
    // Singleton Accessor
    private static GeneralOptions instance;

    public static GeneralOptions getInstance() {
        if (instance == null) {
            instance = new GeneralOptions();
        }
        return instance;
    }
      
    private GeneralOptions() {   
        Object persistentObj = PersistenceManager.getStoreDataAtIndex(STORE_INDX);
        if (persistentObj == null) {
            // Currently not in the data store so create with defaults and save
            init();
            save();
        } else {
            restorePersistentObj(persistentObj);
        } 
    }
    
    private void init() {
        app_DoScan = true;          // Scan memory on startup
        app_ViewSelection = 0;      // Thumbnail view
        app_PromptIMGDelete = true;	// Prompt user for image delete confirmation
        app_Theme = "Blue Floral";	// Name of the theme to use in the app
        
        privacy_ShowHide = true;    // Show hidden
        privacy_Password = "";      // No password
    }
    
    public void save() {
        PersistenceManager.setStoreDataAtIndex(STORE_INDX, getPersistentObj());
    }
    
    /**
     * Creates a persistent object for saving in the persistent store
     * @return A persistent Object
     */
    public Object getPersistentObj() {
        Hashtable persistentObj = new Hashtable(5);
        
        persistentObj.put("scan",       new Boolean(app_DoScan));
        persistentObj.put("viewSel",    new Integer(app_ViewSelection));
        persistentObj.put("imgDelete", 	new Boolean(app_PromptIMGDelete));
        persistentObj.put("theme", 		app_Theme);
        
        persistentObj.put("showHide",   new Boolean(privacy_ShowHide));
        persistentObj.put("privPass",   privacy_Password);
        
        return persistentObj;
    }
    
    /**
     * Sets this objects values to the values in the given persisted object
     * @param persistentObj A persisted representation of the object
     */
    public void restorePersistentObj(Object persistentObj) {
        Hashtable persistentHash = (Hashtable)persistentObj;
        
        app_DoScan =        	StoreUtil.unwrapBool(persistentHash, "scan");
        app_ViewSelection = 	StoreUtil.unwrapInt(persistentHash, "viewSel");
        app_PromptIMGDelete = 	StoreUtil.unwrapBool(persistentHash, "imgDelete");
        app_Theme = 			(String)persistentHash.get("theme");
        
        privacy_ShowHide =  	StoreUtil.unwrapBool(persistentHash, "showHide");
        privacy_Password =  	(String)persistentHash.get("privPass");
    }
} 
