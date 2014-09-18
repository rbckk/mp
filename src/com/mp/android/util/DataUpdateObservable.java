package com.mp.android.util;

import java.util.Observable;
/*
 * This class is used for updating Data 
 * add by fancuiru at 2013-12-27
 */

public class DataUpdateObservable extends Observable {
	private static DataUpdateObservable mDataUpdateObservable = new DataUpdateObservable();
	
	private DataUpdateObservable(){
		
	}
	public static DataUpdateObservable getInstance(){
		return mDataUpdateObservable;
		
	}
	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}
}
