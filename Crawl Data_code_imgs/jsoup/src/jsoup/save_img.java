package jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class save_img {
	public static void download(String urlString, String path, String filename) throws Exception {
	    // reconstruct url
	    URL url = new URL(urlString);
	    // open url
	    URLConnection con = url.openConnection();
	    InputStream is = con.getInputStream();
	    // 1K buffer
	    byte[] bs = new byte[1024];
	    int len;
	    //Create path if not exits
	    filename = path+filename;
	    File f = new File(path);
	    if(!f.exists()){
	    	f.mkdirs();
	    }
	    OutputStream os = new FileOutputStream(filename);
	    // Read
	    while ((len = is.read(bs)) != -1) {
	      os.write(bs, 0, len);
	    }
	
	    os.close();
	    is.close();
	} 
}
