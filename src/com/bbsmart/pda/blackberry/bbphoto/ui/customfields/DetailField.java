package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class DetailField extends VerticalFieldManager {
	public static Bitmap background = ImageFileUtil.getThemeImage(UiUtil.getDetailField());
	
	private BBPhotoLabelField fileName;
	private BBPhotoLabelField fileRes;
	private BBPhotoLabelField fileSize;
	
	public DetailField(AlbumPicture p) {
		fileName = new BBPhotoLabelField(p.toString());
		fileRes = new BBPhotoLabelField(p.getImageWidth()+ "x" + p.getImageHeight());
		fileSize = new BBPhotoLabelField(p.getFileSize() + " KB");
		
		add(fileName);
		add(fileRes);
		add(fileSize);
	}
	
	public void updateField(AlbumPicture p) {
		fileName.setText(p.toString());
		fileRes.setText(p.getImageWidth()+ "x" + p.getImageHeight());
		fileSize.setText(p.getFileSize() + " KB");
	}
	
	public void updateName(String path) {
		fileName.setText(path.substring(path.lastIndexOf('/')+1, path.lastIndexOf('.')));
		invalidate();
	}
	
	protected void sublayout(int width, int height) {
		super.sublayout(width, height);
		setExtent(background.getWidth(), background.getHeight());
	}

	public int getPreferredWidth() {
		return background.getWidth();
	}
	
	protected void paint(Graphics g) {
		if(isVisible()) {
			g.drawBitmap(0, 0, getWidth(), getHeight(), background, 0, 0);
			super.paint(g);
		}
	}
}