package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.FileSystemRegistry;

import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class MemoryPopup extends PopupScreen {
	private static int GRAPH_SIZE;
	
	private int numRoots;
	private Vector roots = new Vector();
	
	private long[] totalMem;
	private long[] usedMem;
	private int[] usedMemPercent;
	
	private Bitmap[] pieChart;
	
	public MemoryPopup() {
		super(new DialogFieldManager(), PopupScreen.DEFAULT_CLOSE);
		setScreenFont();
		if(UiUtil.DEVICE_240W) {
			GRAPH_SIZE = 45;
		} else {
			GRAPH_SIZE = 75;
		}
		
		((DialogFieldManager)getDelegate()).addCustomField(
				new BBPhotoLabelField("Memory Status", UiUtil.getTitle(), BBPhotoLabelField.NON_FOCUSABLE | BBPhotoLabelField.HCENTER) {
					protected void layout(int width, int height) {
						super.layout(width, height);
						setExtent(Graphics.getScreenWidth()/2, getHeight());
					}
					protected void paint(Graphics g) {
						g.drawBitmap(0, 0, getWidth(), getHeight(), getBackground(), 0, 0);
						writeText(g, Graphics.BLACK);
					}
				});
		
        Enumeration rootEnum = FileSystemRegistry.listRoots();
        
        while(rootEnum.hasMoreElements()) {
        	roots.addElement(rootEnum.nextElement());
        	numRoots++;
        }
        
        totalMem = new long[numRoots];
        usedMem = new long[numRoots];
        usedMemPercent = new int[numRoots];
        pieChart = new Bitmap[numRoots];
        
        for(int i = 0; i < numRoots; i++) {
        	initRoot((String)roots.elementAt(i), i);
        }
	}
	
	private void initRoot(String root, int index) {
		pieChart[index] = new Bitmap(GRAPH_SIZE, GRAPH_SIZE);
		
		totalMem[index] = ImageFileUtil.getTotalMemory(root);
		usedMem[index] = ImageFileUtil.getUsedMemory(root);
		usedMemPercent[index] = (int)(((double)usedMem[index]/(double)totalMem[index])*100);
		
		VerticalFieldManager vfm = new VerticalFieldManager();
		HorizontalFieldManager hfm = new HorizontalFieldManager();
		
		vfm.add(new LabelField(root.substring(0, root.lastIndexOf('/')) + " Memory"));
		vfm.add(new LabelField("Used: " + usedMemPercent[index] + "%"));
		vfm.add(new LabelField("Used: " + (usedMem[index]/1024/1024) + " MB"));
		vfm.add(new LabelField("Total: " + (totalMem[index]/1024/1024) + " MB"));
		vfm.setPadding(0, 0, 0, 5);
		
		hfm.add(new BitmapField(pieChart[index]));
		hfm.add(vfm);
		hfm.setPadding(2, 0, 0, 0);
		
		((DialogFieldManager)getDelegate()).addCustomField(hfm);
	}
	
	protected void paint(Graphics graphics) {
		graphics.setBackgroundColor(Graphics.FULL_WHITE);
		graphics.clear();
		for(int i = 0; i < numRoots; i++) {
			Graphics chart = new Graphics(pieChart[i]);
	        
	        chart.setColor(Color.GREEN);
	        chart.fillArc(0,0,GRAPH_SIZE,GRAPH_SIZE,90,(360-(usedMemPercent[i]*360)/100));
	        if(usedMemPercent[i] > 0) {
	            chart.setColor(Color.RED);
	            chart.fillArc(0,0,GRAPH_SIZE,GRAPH_SIZE,-270,(90-(usedMemPercent[i]*360)/100)-90);
	        }
		}
		super.paint(graphics);
	}
	
	private void setScreenFont() {
		Font f;
		try {
			f = FontFamily.forName("BBMillbankTall").getFont(Font.BOLD, 17);
			f = f.derive(f.getStyle(), f.getHeight(), Ui.UNITS_px,
					Font.ANTIALIAS_STANDARD, 0);
		} catch (ClassNotFoundException cnfe) {
			f = getFont().derive(Font.BOLD, 17, Ui.UNITS_px,
					Font.ANTIALIAS_STANDARD, 0);
		}
		setFont(f);
	}
}