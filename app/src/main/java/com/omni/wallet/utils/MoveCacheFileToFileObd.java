package com.omni.wallet.utils;

import android.util.Log;

import java.io.*;

/*** 复制文件夹或文件夹*/
public class MoveCacheFileToFileObd {
    // 复制文件
    // copy file
    public static void copyFile(File sourceFile, File targetFile){
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(sourceFile);
            output = new FileOutputStream(targetFile);
            BufferedInputStream inBuff = new BufferedInputStream(input);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);
            // 缓冲数组
            // buffer array
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            // Flushes this buffered output stream
            outBuff.flush();
            //关闭流
            // close stream
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 复制文件夹
    // copy copyDirectory
    public static void copyDirectiory(String sourceDir, String targetDir) {
        // 新建目标目录
        // Create new target directory
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        // Get the file or directory under the current source folder
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                // Source File
                File sourceFile = file[i];
                // 目标文件
                // Target file
                File targetFile = new File(new File(targetDir).getAbsolutePath()
                        + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                // Source folder to copy
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                // target folder to copy
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /** 删除单个文件
     * delete single file
     * @param filePath$Name 要删除的文件的文件名
     * @param filePath$Name the file name that will delete
     * @return 单个文件删除成功返回true，否则返回false
     * @return While delete single file successful return true,else return false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        // If the file corresponding to the file path exists and is a file, delete it directly
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /** 删除目录及目录下的文件
     * @param filePath 要删除的目录的文件路径
     * @param filePath the directory path that will delete
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory (String filePath){
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        // Automatically add a file separator if dir does not end with a file separator
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        // If the file corresponding to dir does not exist, or is not a directory, exit
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        // Delete all files in the folder, including subdirectories
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            // delete child files
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            // delete subdirectories
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        // delete the current directory
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: delete directory" + filePath + "successful！");
            return true;
        } else {
            return false;
        }
    }
}