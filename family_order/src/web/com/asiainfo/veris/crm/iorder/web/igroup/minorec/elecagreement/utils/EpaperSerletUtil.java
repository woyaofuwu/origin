package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class EpaperSerletUtil 
{
	
	public static String getContentType(String fileType)
	{
		String contentType = null;

		if ("STREAM".equalsIgnoreCase(fileType))
			contentType = "application/octet-stream";
		else if ("JPG".equals(fileType) || "JPEG".equals(fileType))
			contentType = "image/jpeg";
		else if ("GIF".equalsIgnoreCase(fileType))
			contentType = "image/gif";
		else if ("PNG".equalsIgnoreCase(fileType))
			contentType = "image/png";
		else if ("DOC".equalsIgnoreCase(fileType))
			contentType = "application/vnd.msword";
		else if ("XLS".equalsIgnoreCase(fileType))
			contentType = "application/vnd.ms-excel";
		else if ("PPT".equalsIgnoreCase(fileType))
			contentType = "application/vnd.ms-powerpoint";
		else if ("PDF".equalsIgnoreCase(fileType))
			contentType = "application/pdf";
		else
			contentType = "application/octet-stream";

		return contentType;
	}
	
	public static String getContentTypeByFileName(String fileName)
	{
		String fileType = getFileType(fileName);
		return getContentType(fileType);
	}
	
	public static String getFileType(String fileName)
	{
		if (fileName.lastIndexOf(".") == -1)
		{
			return null;
		}
		else
		{
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			return fileType.toUpperCase();
		}
	}
	
	public  static void writeData(HttpServletRequest hreq, HttpServletResponse hres, byte[] bytes) throws IOException {
		BufferedOutputStream bos = null;
		try {
			hres.setContentType("binary/octet-stream");
			hres.setContentLength(bytes.length);
			hres.setCharacterEncoding("UTF-8");
			hres.setHeader("Connection", "close");

			bos = new BufferedOutputStream(hres.getOutputStream());
			bos.write(bytes);
			bos.flush();
			bos.close();
			
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != bos) {
				bos.close();
			}
		}
	}
}
