package com.bbsmart.pda.blackberry.bbphoto.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;

import com.bbsmart.pda.blackberry.bbphoto.net.ImageHttpConnection;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;

/**
 * Convenience methods for handling images
 */
public final class ImageFileUtil {
	
	private static FileConnection fcOpen(String path) {
		FileConnection fc = null;
        // Open the file system and get the list of directories/files
        try {
            fc = (FileConnection)Connector.open("file:///" + path);
        } catch (Exception ioex) {		// Could not open file from given path
        	return null;
        }
        return fc;
	}
	
	private static boolean fcClose(FileConnection fc) {
        if (fc != null) {   // Everything is read, make sure to close the connection
            try {
                fc.close();
                fc = null;
            } catch (Exception ioex) { return false; }
        }
        return true;
	}
	
	public static boolean deleteImage(String path) {
		FileConnection fc = null;
    	try {
        	fc = fcOpen(path);
            fc.delete();
        } catch (Exception ioex) {		// Could not delete file
        	return false;
        } finally {
            fcClose(fc);
        }
        return true;
	}
	
	/**
     * Returns an EncodedImage from the file system
     * @param file full path name to the image (eg SDCard/blackberry/pictures/IMG0001.jpg)
     * @return a EncodedImage of the image at the given path.  Returns null if could not find.
     */
    public static EncodedImage getFileAsEncodedImage(String path) {
        return EncodedImage.createEncodedImage(getImageBytes(path), 0, -1);
    }
    
    public static byte[] getImageBytes(String path) {
        FileConnection fc = null;
        InputStream instream = null;
        byte data[];
        try {
        	fc = fcOpen(path);
        	data = new byte[(int)fc.fileSize()];
        	instream = fc.openInputStream();
        	instream.read(data);
        } catch (Exception ioex) {	// Could not get data from file
        	return null;
        } finally {
        	if (instream != null) {
        		try {
        			instream.close();
        		} catch (Exception ioex) { return null; }
        	}
        	fcClose(fc);
        }
        return data; 
    }
    
    public static Bitmap getThemeImage(String name) {
    	return getResourceImage(Themes.getCurrentTheme().getTheme()+'/'+name);
    }
    
    public static Bitmap getTitleTextImage(String name) {
    	return getResourceImage("titles/" + name);
    }
    
    public static Bitmap getResourceImage(String name) {
		return getResourceImage(name, 1);
    }
    
    // Only supports scaling by integer values
    public static Bitmap getResourceImage(String name, int scale) {
    	InputStream instream;
    	try {
    		instream = Class.forName(
			"com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto")
			.getResourceAsStream("/img/" + name);
    		byte[] data = new byte[instream.available()];
    		instream.read(data);
    		instream.close();

    		EncodedImage image = EncodedImage.createEncodedImage(data, 0, data.length);
    		image.setScale(scale);
    		return image.getBitmap();
    	} catch (Exception ioex){ return Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION); }
    }
    
    public static long getTotalMemory(String root) {
    	FileConnection fc = null;
    	long memory;
    	try {
    		fc = fcOpen(root);
    		memory = fc.totalSize();
    	} catch (Exception ioex){
    		return 0;
    	} finally {
    		fcClose(fc);
    	}
    	return memory;
    }
    
    public static long getUsedMemory(String root) {
    	FileConnection fc = null;
    	long memory;
    	try {
    		fc = fcOpen(root);
    		memory = fc.usedSize();
    	} catch (Exception ioex){
    		return 0;
    	} finally {
    		fcClose(fc);
    	}
    	return memory;
    }
    
    public static boolean hideImage(String path, boolean status) {
    	FileConnection fc = null;
    	try {
        	fc = fcOpen(path);
            fc.setHidden(status);
        } catch (Exception ioex) {		// Could not perform action on file
        	return false;
        } finally {
            fcClose(fc);
        }
        return true;
    }
    
    public static Bitmap loadThumbnail(String file, int width, int height) {
    	Bitmap b = new Bitmap(width, height);
    	file = file.substring(0, file.lastIndexOf('.')) + "-thumb.dat";
    	int[] data = new int[width*height];

    	FileConnection fc = null;
    	DataInputStream instream = null;
    	try{ 
    		fc = fcOpen(file);
    		instream = fc.openDataInputStream();
    		for(int i = 0; i < data.length; i++) {
    			data[i] = instream.readInt();
    		}
    	} catch(Exception ioex) {
    		return null;	// Error reading data
    	} finally {
    		if(instream != null) {
    			try {
    				instream.close();
    			} catch(Exception ioex) { return null; } // Error closing instream
    		}
    		fcClose(fc);
    	}
    	b.setARGB(data, 0, width, 0, 0, width, height);
    	return b;
    }
    
    public static HttpConnection makeConnection(String url) {
        ImageHttpConnection iConn = new ImageHttpConnection();
        iConn.setURL(url);
        iConn.setContent(new String(getImageBytes(url)));
            
        return(HttpConnection)iConn;                          
    }
    
    public static boolean renameImage(String path, String name) {
        FileConnection fc = null;
    	try {
        	fc = fcOpen(path);
            fc.rename(name);
        } catch (Exception ioex) {		// Could not rename file to given name
        	return false;
        } finally {
            fcClose(fc);
        }
        return true;
    }
    
    /**
     * Resizes the image as an EncodedImage to a best fit newWidth x newHeight
     * @param image The EncodedImage to resize
     * @param newWidth The new width to resize to
     * @param newHeight The new height to resize to
     * @return The EncodedImage to display at new set dimensions
     */
    public static EncodedImage resizeEI(EncodedImage image, int newWidth, int newHeight) {
    	int width = image.getWidth();
    	int height = image.getHeight();
    	int scale = Fixed32.toFP(1);
    	if(width > height) {
    		scale = Fixed32.toFP(width)/newWidth;
    		image = image.scaleImage32(scale, scale);
    	} else {
    		scale = Fixed32.toFP(height)/newHeight;
    		image = image.scaleImage32(scale, scale);
    	}
    	return image;
    }	
    
    // Save the thumbnail data to a file of the same name appended with "-thumb.dat";
    // Always save to a new file, deleting files if they happen to exist.
    public static void saveThumbnail(String file, int[] thumbnail) {
    	file = file.substring(0, file.lastIndexOf('.')) + "-thumb.dat";
    	FileConnection fc = null;
    	DataOutputStream outstream = null;
    	try {
    		fc = fcOpen(file);
			if(fc.exists()) {
				fc.delete();
			}
			fc.create();
    		outstream = fc.openDataOutputStream();
    		for(int i = 0; i < thumbnail.length; i++) {
    			outstream.writeInt(thumbnail[i]);
    		}
    		fc.setHidden(true);
    	} catch(Exception ioex) {
    		// Could not write data to file
    	} finally {
    		if(outstream != null) {
    			try {
    				outstream.close();
    			} catch(Exception ioex) {} // Error closing outstream
    		}
    		fcClose(fc);
    	}
    }
}