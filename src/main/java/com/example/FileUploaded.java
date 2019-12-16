package com.example;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

@XmlRootElement
public class FileUploaded {

	private String filename;
	private String uploadDate;
	private long filesize;
	private byte[] data;

	public FileUploaded(String filename) {
		this.filename = filename;

		File file = GetFile();
		if (file.exists()) {
			this.filename = filename;
			filesize = file.length();
			
			BasicFileAttributes fileAttributes;
			try {
				fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				uploadDate = fileAttributes.creationTime().toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "FileUploaded{" + 
				"filename=" + filename + 
				", uploadDate=" + uploadDate + 
				", fileSize=" + filesize +
				"}";
	}
	
	public File GetFile() {
		File file = new File(Main.ROOT_REPOSITORY + File.separator + filename);
		return file;
	}
	
	public byte[] GetRawData() throws Exception {
		try {
			byte buffer[] = new byte[(int)filesize];
			String fullFilename = CreateFullFilename(filename);
			InputStream inputStream = new FileInputStream(fullFilename);
			inputStream.read(buffer);
			inputStream.close();
			return buffer;
		} catch (Exception ex) {
			throw ex;
		}		
	}
	
	public Boolean Delete() {
		File file = GetFile();
		if (file.exists()) {
			file.delete();
			return true;
		} else {
			return false;
		}
	}
	
	private static String CreateFullFilename(String filename) {
		return Main.ROOT_REPOSITORY + 
        	File.separatorChar + 
        	filename.replace('/', File.separatorChar)
        	.replace('\\', File.separatorChar);
	}
	
	static FileUploaded UploadFile (String filename, InputStream inputStream) throws Exception {
        int read = 0;
        byte[] bytes = new byte[1024];
 
        String fullPath = CreateFullFilename(filename);
        File file = new File (fullPath);
        file.getParentFile().mkdirs();
        OutputStream out = new FileOutputStream(file);
        while ((read = inputStream.read(bytes)) != -1) 
        {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
        
        return new FileUploaded(filename);
	}
	
	static Boolean Exists(String filename) {
		String fullPath = CreateFullFilename(filename);
		File file = new File(fullPath);
		return file.exists();
	}
	
	static List<FileUploaded> GetListing(String location) {
		List<FileUploaded> listing = new LinkedList<FileUploaded> ();
		
		String fullPathname = CreateFullFilename(location);
		File file = new File(fullPathname);
		if (file.isDirectory()) {
			for (File f: file.listFiles()) {
				String filename = f.getName();
	        	FileUploaded fileUploaded = new FileUploaded(filename);
	        	listing.add(fileUploaded);
			}
		}
		return listing;
	}
}
