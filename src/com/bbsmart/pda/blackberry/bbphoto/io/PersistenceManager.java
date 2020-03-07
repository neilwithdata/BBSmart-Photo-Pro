package com.bbsmart.pda.blackberry.bbphoto.io;

import java.util.Vector;

import com.bbsmart.pda.blackberry.bbphoto.AppInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public final class PersistenceManager {  
    private static PersistentObject store = PersistentStore
			.getPersistentObject(AppInfo.APP_KEY);

	public static PersistentObject privateStore = PersistentStore
			.getPersistentObject(AppInfo.APP_PRIVATE_KEY);
    
    private static PersistenceVector data;
    
    private static final int NUM_STORE_ITEMS = 5;
    // Element 0: Store version number
    // Element 1: Album List
    // Element 2: Slideshow Options
    // Element 3: Screensaver Options
    // Element 4: General Options
    
    static {
        synchronized (store) {
            data = (PersistenceVector) store.getContents();
            try {
                if (data == null) { // Create for the first time
                    data = new PersistenceVector();
                    defaultPopulateStore(data);
                } else {
                    String currentVersion = AppInfo.VERSION_STRING;
                    String storeVersion = (String) data.elementAt(0);

                    if (!currentVersion.equals(storeVersion)) {
                        upgradeDataStore(data, storeVersion);
                    }
                }
            } catch (Exception e) { // Exception occurred upgrading store; reset
                data = new PersistenceVector();
                defaultPopulateStore(data);
            } finally {
                store.setContents(data);
                store.commit();
            }
        }
    }
    
    private static void defaultPopulateStore(Vector data) {
        data.removeAllElements();
        data.setSize(NUM_STORE_ITEMS);

        // Store version number
        data.setElementAt(AppInfo.VERSION_STRING, 0);

        // Other persistent objects will dynamically add themselves on-demand
    }
    
    /**
    * Upgrades the data store (data)
    * 
    * @param storeVersion
    * @param data
    */
    private static void upgradeDataStore(Vector data, String storeVersion) {

    }

    public static Object getStoreDataAtIndex(int index) {
        Object o = null;
        synchronized (store) {
            try {
                data = (PersistenceVector) store.getContents();
                o = data.elementAt(index);
            } catch (Exception e) {
                return null; // Controlled access exception - hide silently
            }
        }
        return o;
    }
    
    public static void setStoreDataAtIndex(int index, Object o) {
        synchronized (store) {
            try {
                data = (PersistenceVector) store.getContents();
                data.setElementAt(o, index);
                store.setContents(data);
                store.commit();
            } catch (Exception e) {
                // Controlled access exception - hide silently
            }
        }
    }
}
