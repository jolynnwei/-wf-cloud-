
package com.project.network.ssugaeting.ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Jin on 2018-06-04.
 */

public class FTPConnection {
    private static final String TAG = "FTPConnection";
    private static final String FTP_URL = "ftp://192.168.0.2:2221";
    private static final String FTP_HOST = "192.168.0.5";
    private static final String USER_NAME = "testuser";
    // testuser
    // francis
    private static final String USER_PASSWORD = "test";
    // test
    private static final int FTP_PORT = 21;

    public FTPClient mFTPClient = null;

    public FTPConnection() {
        mFTPClient = new FTPClient();
    }

    public boolean ftpConnect() {
        boolean result = false;
        try {
            mFTPClient.setControlEncoding("euc-kr");
            mFTPClient.connect(FTP_HOST, FTP_PORT);
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                result = mFTPClient.login(USER_NAME, USER_PASSWORD);
                mFTPClient.enterLocalPassiveMode();
            }
        } catch (Exception e) {
            Log.d(TAG, "Couldn't connect to host");
            Log.d("Exception", e.getMessage());
        }
        return result;
    }

    public boolean ftpDisconnect() {
        boolean result = false;
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "Failed to disconnect with server");
        }
        return result;
    }

    public String ftpGetDirectory() {
        String directory = null;
        try {
            directory = mFTPClient.printWorkingDirectory();
        } catch (Exception e) {
            Log.d(TAG, "Couldn't get current directory");
        }
        return directory;
    }

    public boolean ftpChangeDirectory(String directory) {
        try {
            mFTPClient.changeWorkingDirectory(directory);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Couldn't change the directory");
        }
        return false;
    }

    public String[] ftpGetFileList(String directory) {
        String[] fileList = null;
        int i = 0;
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(directory);
            fileList = new String[ftpFiles.length];
            for (FTPFile file : ftpFiles) {
                String fileName = file.getName();

                if (file.isFile()) {
                    fileList[i] = "(File) " + fileName;
                } else {
                    fileList[i] = "(Directory) " + fileName;
                }

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public boolean ftpCreateDirectory(String directory) {
        boolean result = false;
        try {
            result = mFTPClient.makeDirectory(directory);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't make the directory");
        }
        return result;
    }

    public boolean ftpDeleteDirectory(String directory) {
        boolean result = false;
        try {
            result = mFTPClient.removeDirectory(directory);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't remove directory");
        }
        return result;
    }

    public boolean ftpDeleteFile(String file) {
        boolean result = false;
        try {
            result = mFTPClient.deleteFile(file);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't remove the file");
        }
        return result;
    }

    public boolean ftpRenameFile(String from, String to) {
        boolean result = false;
        try {
            result = mFTPClient.rename(from, to);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't rename file");
        }
        return result;
    }

    public boolean ftpDownloadFile(String srcFilePath, String desFilePath) {
        boolean result = false;
        try {
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);

            FileOutputStream fos = new FileOutputStream(desFilePath);
            result = mFTPClient.retrieveFile(srcFilePath, fos);
            fos.close();
        } catch (Exception e) {
            Log.d(TAG, "Download failed");
            Log.d(TAG, e.getMessage());
        }
        return result;
    }

    public boolean ftpUploadFile(File imageFile, String desDirectory) {
        boolean result = false;
        if (!imageFile.exists())
            Log.d("imageFile", "not exist!");
        try {
            mFTPClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            FileInputStream fis = new FileInputStream(imageFile);
            mFTPClient.storeFile(imageFile.getName(), fis);
            mFTPClient.rename(imageFile.getName(), desDirectory + ".jpg");
            fis.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());