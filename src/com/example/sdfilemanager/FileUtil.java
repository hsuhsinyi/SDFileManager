package com.example.sdfilemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
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
	
	 /**
	  * 获取视频的缩略图
	  * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	  * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	  * @param videoPath 视频的路径
	  * @param width 指定输出视频缩略图的宽度
	  * @param height 指定输出视频缩略图的高度度
	  * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	  *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	  * @return 指定大小的视频缩略图
	  */
	 private Bitmap getVideoThumbnail(String videoPath, int width, int height,
	   int kind) {
	  Bitmap bitmap = null;
	  // 获取视频的缩略图
	  bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
	  System.out.println("w"+bitmap.getWidth());
	  System.out.println("h"+bitmap.getHeight());
	  bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
	    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	  return bitmap;
	 }
	 
	 /**
	  * 根据指定的图像路径和大小来获取缩略图
	  * 此方法有两点好处：
	  *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	  *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	  *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
	  *        用这个工具生成的图像不会被拉伸。
	  * @param imagePath 图像的路径
	  * @param width 指定输出图像的宽度
	  * @param height 指定输出图像的高度
	  * @return 生成的缩略图
	  */
	 private Bitmap getImageThumbnail(String imagePath, int width, int height) {
	  Bitmap bitmap = null;
	  BitmapFactory.Options options = new BitmapFactory.Options();
	  options.inJustDecodeBounds = true;
	  // 获取这个图片的宽和高，注意此处的bitmap为null
	  bitmap = BitmapFactory.decodeFile(imagePath, options);
	  options.inJustDecodeBounds = false; // 设为 false
	  // 计算缩放比
	  int h = options.outHeight;
	  int w = options.outWidth;
	  int beWidth = w / width;
	  int beHeight = h / height;
	  int be = 1;
	  if (beWidth < beHeight) {
	   be = beWidth;
	  } else {
	   be = beHeight;
	  }
	  if (be <= 0) {
	   be = 1;
	  }
	  options.inSampleSize = be;
	  // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
	  bitmap = BitmapFactory.decodeFile(imagePath, options);
	  // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
	  bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
	    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	  return bitmap;
	 }
	

}
