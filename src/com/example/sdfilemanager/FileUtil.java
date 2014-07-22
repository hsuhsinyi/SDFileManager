package com.example.sdfilemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

public class FileUtil {
	
	/**  
	 * TODO to see sdcard is available
	 * 
	 * @throw 
	 * @return boolean 
	 */
	public static boolean isSdCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**  
	 * TODO delete a dir
	 * 
	 * @throw 
	 * @return void 
	 */
	public static void deleteFileOrDir(File file){
			//File file = new File(path);
		if(file.exists()){
			if(file.isFile()){
				file.delete();
			}else if(file.isDirectory()){
				File files[] = file.listFiles();
				if((files == null) || (files.length == 0)){
					file.delete();
					return;
				}
				for(File fileDir:files){
					deleteFileOrDir(fileDir);
				}
				file.delete();
			}
			
		}
		return;
	}	
	
	
	/**  
	 * TODO rename file 
	 * 
	 * @throw 
	 * @return boolean 
	 */
	public static File renameFile(File file, String descFileName){
		File newFile = new File(file.getParentFile().getAbsolutePath()+file.separator+descFileName);
		file.renameTo(newFile);
		return newFile;
	}
	
	/**  
	 * TODO jump to parent dir
	 * 
	 * @throw 
	 * @return void 
	 */
	public static String gotoParentDir(String path){
		File file = new File(path);
		File str_pa = file.getParentFile();
		String parPath = str_pa.getAbsolutePath();
		return parPath;
	}
	
	/**  
	 * TODO get sdcard path
	 * 
	 * @throw 
	 * @return String 
	 */
	public static String getSDPath(){
		String sdPath = Environment.getExternalStorageDirectory()+ File.separator;
		return sdPath;
	}
	
	
	/**  
	 * TODO copy directory
	 * 
	 * @throw 
	 * @return void 
	 */
	public static void copyDirectiory(String sourceDir, String targetDir) {
		// �½�Ŀ��Ŀ¼
		(new File(targetDir)).mkdirs();
		// ��ȡԴ�ļ��е�ǰ�µ��ļ���Ŀ¼
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// Դ�ļ�
				File sourceFile = file[i];
				// Ŀ���ļ�
				String targetFilePath = targetDir + File.separator
						+ file[i].getName();
				copyFile(sourceFile.getAbsolutePath(), targetFilePath);
			} else if (file[i].isDirectory()) {
				// ׼�����Ƶ�Դ�ļ���
				String dir1 = sourceDir + File.separator + file[i].getName();
				// ׼�����Ƶ�Ŀ���ļ���
				String dir2 = targetDir + File.separator + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}
	
	/**  
	 * TODO copy single file
	 * 
	 * @throw 
	 * @return void 
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // �ļ�����ʱ
				InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("���Ƶ����ļ���������");
			e.printStackTrace();
		}
	}
	
	
	public static void copyFileOrDirectory(String oldPath, String targetPath) {
		File file = new File(oldPath);
		Log.e("tag", "oldPath:" + oldPath + " targetPath:" + targetPath);
		if (file.isDirectory()) {
			copyDirectiory(oldPath, targetPath);
		} else {
			copyFile(oldPath, targetPath);
		}
	}
	
	public static void moveDirectiory(String sourceDir, String targetDir) {
		copyDirectiory(sourceDir, targetDir);
		File sourcefile = new File(sourceDir);
		deleteFileOrDir(sourcefile);
	}
	
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		File oldfile = new File(oldPath);
		deleteFileOrDir(oldfile);
	}
	
	public static void cut(String oldPath, String targetPath) {
		File file = new File(oldPath);
		if (file.isDirectory()) {
			moveDirectiory(oldPath, targetPath);
		} else {
			moveFile(oldPath, targetPath);
		}
	}
	
	 /**  
	 * TODO �õ��ļ��д�С
	 * 
	 * @throw 
	 * @return long 
	 */
	public static long getFolderSize(java.io.File file)throws Exception{  
	        long size = 0;  
	        java.io.File[] fileList = file.listFiles();  
	        for (int i = 0; i < fileList.length; i++)  
	        {  
	            if (fileList[i].isDirectory())  
	            {  
	                size = size + getFolderSize(fileList[i]);  
	            } else  
	            {  
	                size = size + fileList[i].length();  
	            }  
	        }  
	        return size/1048576;  
	    }  
	

	/**  
	 * TODO ��Src pathname��ַת����descpathname 
	 * srcPathName��Դ��ַ�磺/mnt/sdcard/file.mp3
	 * descPath �磺/mnt/sdcard/hhy
	 * ת����/mnt/sdcard/hhy/file.mp3
	 * @throw 
	 * @return String 
	 */
	public static String parseSrcPathToDesc(String srcPathName, String descPath) {
	
		// ȡ�����һ��/���±�
		int index = srcPathName.lastIndexOf("/");
		// ���ַ���תΪ�ַ�����
		char[] ch = srcPathName.toCharArray();
		// ���� copyValueOf(char[] data, int offset, int count) ȡ�����һ���ַ���
		return  descPath + File.separator + String.copyValueOf(ch, index + 1, ch.length - index
				- 1);
	} 
	

}