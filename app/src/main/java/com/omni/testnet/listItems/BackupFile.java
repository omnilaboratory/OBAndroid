package com.omni.testnet.listItems;

import java.io.File;

public class BackupFile {
    boolean isSelected = false;
    String fileType = "else";
    String lastEdit = "";
    String filename = "";
    File file = null;
    boolean hasChildFile = false;
    int childFileNum = 0;
    
    public BackupFile(boolean isSelected,String fileType,String filename,String lastEdit,File file){
        this.isSelected = isSelected;
        this.fileType = fileType;
        this.lastEdit = lastEdit;
        this.filename = filename;
        this.file = file;
    };

    public BackupFile(boolean isSelected,String fileType,String filename,String lastEdit,File file,int childFileNum){
        this.isSelected = isSelected;
        this.fileType = fileType;
        this.lastEdit = lastEdit;
        this.filename = filename;
        this.file = file;
        this.childFileNum = childFileNum;
        if(childFileNum>0){
            this.hasChildFile = true;    
        }
    };

    public File getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileType() {
        return fileType;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public int getChildFileNum() {
        return childFileNum;
    }

    public boolean isHasChildFile() {
        return hasChildFile;
    }

    public boolean getSelected(){
        return isSelected;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setChildFileNum(int childFileNum) {
        this.childFileNum = childFileNum;
    }

    public void setHasChildFile(boolean hasChildFile) {
        this.hasChildFile = hasChildFile;
    }
}
