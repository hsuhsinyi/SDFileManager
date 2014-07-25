package com.example.sdfilemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.os.Environment;
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
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				String targetFilePath = targetDir + File.separator
						+ file[i].getName();
				copyFile(sourceFile.getAbsolutePath(), targetFilePath);
			} else if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + File.separator + file[i].getName();
				// 准备复制的目标文件夹
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
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
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
			System.out.println("复制单个文件操作出错");
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
	 * TODO 得到文件夹大小
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
	 * TODO 将Srcpathname地址转换成descpathname 
	 * srcPathName：源地址如：/mnt/sdcard/file.mp3
	 * descPath 如：/mnt/sdcard/hhy
	 * 转换成/mnt/sdcard/hhy/file.mp3
	 * @throw 
	 * @return String 
	 */
	public static String parseSrcPathToDesc(String srcPathName, String descPath) {
	
		// 取得最后一个/的下标
		int index = srcPathName.lastIndexOf("/");
		// 将字符串转为字符数组
		char[] ch = srcPathName.toCharArray();
		// 根据 copyValueOf(char[] data, int offset, int count) 取得最后一个字符串
		return  descPath + File.separator + String.copyValueOf(ch, index + 1, ch.length - index
				- 1);
	} 
	

}
