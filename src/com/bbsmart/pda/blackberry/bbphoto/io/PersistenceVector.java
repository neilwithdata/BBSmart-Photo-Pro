package com.bbsmart.pda.blackberry.bbphoto.io;

import java.util.Vector;

import net.rim.device.api.util.Persistable;

/**
 * Vector to ensure the persistent store is deleted when the 
 * application is uninstalled.
 */
public class PersistenceVector extends Vector implements Persistable{
	public PersistenceVector() {
		super();
	}
}