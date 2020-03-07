package com.bbsmart.pda.blackberry.bbphoto.models;

import java.util.Hashtable;

import com.bbsmart.pda.blackberry.bbphoto.io.PersistenceManager;
import com.bbsmart.pda.blackberry.bbphoto.io.StoreUtil;

public final class SlideshowOptions {
	private static final int STORE_INDX = 2;
	
    // Options
    public int slideDelay;
    public int transEffect;
    public int randSeq;
    public boolean showNotes;
    
    // Singleton Accessor
    private static SlideshowOptions instance;

    public static SlideshowOptions getInstance() {
        if (instance == null) {
            instance = new SlideshowOptions();
        }
        return instance;
    }
    
    private SlideshowOptions() {    
        Object persistentObj = PersistenceManager.getStoreDataAtIndex(STORE_INDX);
        if (persistentObj == null) {
            // Currently not in the data store so create with defaults and save
            init();
            save();
        } else {
            restorePersistentObj(persistentObj);
        }
    }
    
    /**
     * Set all the options to their default values
     */
    private void init() {
        slideDelay    = 5;        //5 seconds
        transEffect   = 1;        //Random Transition Effect
        randSeq       = 1;        //Sequential
        showNotes     = false;    //Notes off
    }
    
    public void save() {
        PersistenceManager.setStoreDataAtIndex(STORE_INDX, getPersistentObj());
    }
    
    /**
     * Creates a persistent object for saving in the persistent store
     * @return A persistent Object
     */
    public Object getPersistentObj() {
        Hashtable persistentObj = new Hashtable(4);
        
        persistentObj.put("delay", 		new Integer(slideDelay));
        persistentObj.put("effect", 	new Integer(transEffect));
        persistentObj.put("randSeq", 	new Integer(randSeq));
        persistentObj.put("notes", 		new Boolean(showNotes));
        
        return persistentObj;
    }
    
    /**
     * Creates a persistent object for saving in the persistent store
     * @return A persistent Object
     */
    public void restorePersistentObj(Object persistentObj) {
        Hashtable persistentHash = (Hashtable)persistentObj;
        
        slideDelay = 	StoreUtil.unwrapInt(persistentHash, "delay");
        transEffect = 	StoreUtil.unwrapInt(persistentHash, "effect");
        randSeq = 		StoreUtil.unwrapInt(persistentHash, "randSeq");
        showNotes = 	StoreUtil.unwrapBool(persistentHash, "notes");
    }
} 
