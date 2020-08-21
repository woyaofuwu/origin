package com.asiainfo.veris.crm.order.pub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.util.FileManHelper;

public final class FtpUtil {
    private static final Logger log = Logger.getLogger(FtpUtil.class);
    public static final int FILE_TYPE_BINARY = 2;
    public static final int FILE_TYPE_ASCII = 0;
    protected FTPClient client;
    private String ftpserver;
    private int ftpport;
    private String ftpuser;
    private String ftppasswd;
    private String ftppath;

    public FtpUtil(String server, String user, String password) {
        this(server, 21, user, password, (String) null);
    }

    public FtpUtil(String server, String user, String password, String path) {
        this(server, 21, user, password, path);
    }

    public FtpUtil(String server, int port, String user, String password, String path) {
        this.ftpserver = server;
        this.ftpport = port;
        this.ftpuser = user;
        this.ftppasswd = password;
        this.ftppath = path;
        this.client = new FTPClient();
        this.connectServer(this.ftpserver, this.ftpport, this.ftpuser, this.ftppasswd, this.ftppath);
    }

    protected void connectServer(String server, int port, String user, String password, String path) {
        if (log.isDebugEnabled()) {
            log.debug("ftp>connected to " + server + ".");
        }

        try {
            this.client.connect(server, port);
        } catch (SocketException arg10) {
            throw new BaseException("FTP网络连接异常", arg10);
        } catch (IOException arg11) {
            throw new BaseException("FTP网络连接中断", arg11);
        }

        if (log.isDebugEnabled()) {
            log.debug("ftp>connection reply : " + this.client.getReplyCode());
        }

        boolean loginrs;
        try {
            loginrs = this.client.login(user, password);
        } catch (IOException arg9) {
            throw new BaseException("FTP登陆异常", arg9);
        }

        if (loginrs) {
            this.client.enterLocalPassiveMode();
            if (log.isDebugEnabled()) {
                log.debug("ftp>login successful.");
            }

            if (log.isDebugEnabled()) {
                log.debug("ftp>change working directory :" + path);
            }

            if (path != null && !"".equals(path)) {
                boolean changed = false;

                try {
                    changed = this.client.changeWorkingDirectory(path);
                    if (!changed) {
                        log.debug("ftp>change working directory error, current is " + this.client.printWorkingDirectory());
                    } else if (log.isDebugEnabled()) {
                        log.debug("ftp>change working directory ok, current is " + this.client.printWorkingDirectory());
                    }
                } catch (IOException arg8) {
                    throw new BaseException("FTP目录切换异常[" + path + "]", arg8);
                }
            }

        } else {
            throw new BaseException("FTP登陆异常Passive模式");
        }
    }

    public void setFileType(int fileType) {
        if (log.isDebugEnabled()) {
            log.debug("ftp>set " + (fileType == 0 ? "assii" : "binary") + " file type.");
        }

        try {
            this.client.setFileType(fileType);
        } catch (IOException arg2) {
            throw new BaseException("FTP传输过程中设置文件类型异常", arg2);
        }
    }

    public void closeServer() {
        if (this.client.isConnected()) {
            try {
                this.client.disconnect();
            } catch (IOException arg4) {
                throw new BaseException("FTP关闭连接时异常", arg4);
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>close " + this.ftpserver + "...");
                }

            }
        }

    }

    public boolean changeParentDirectory() {
        try {
            boolean result = this.client.changeToParentDirectory();
            if (result) {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>change parent directory ok.");
                    log.debug("ftp>current directory [" + this.client.printWorkingDirectory() + "]");
                }

                return result;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>change parent directory error.");
                    log.debug("ftp>current directory [" + this.client.printWorkingDirectory() + "]");
                }

                throw new BaseException("ftputil-10007");
            }
        } catch (IOException arg5) {
            throw new BaseException("FTP切换到上一级目录异常", arg5);
        } finally {
            ;
        }
    }

    public boolean changeDirectory(String path) {
        try {
            boolean result = this.client.changeWorkingDirectory(path);
            if (result) {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>change directory [" + path + "] ok.");
                    log.debug("ftp>current directory [" + this.client.printWorkingDirectory() + "]");
                }

                return result;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>change directory [" + path + "] error.");
                    log.debug("ftp>current directory [" + this.client.printWorkingDirectory() + "]");
                }

                throw new BaseException("FTP切换目录异常[" + path + "]");
            }
        } catch (IOException arg6) {
            throw new BaseException("FTP切换目录时网络异常", arg6);
        } finally {
            ;
        }
    }

    public boolean createDirectory(String path) {
        try {
            boolean result = this.client.makeDirectory(path);
            if (result) {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>create directory [" + path + "].");
                }

                return result;
            } else {
                throw new BaseException("FTP创建目录时异常[" + path + "]");
            }
        } catch (IOException arg3) {
            throw new BaseException("FTP创建目录时网络异常[" + path + "]", arg3);
        }
    }

    public boolean removeDirectory(String path) {
        try {
            boolean result = this.client.removeDirectory(path);
            if (result) {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>remove directory [" + path + "].");
                }

                return result;
            } else {
                throw new BaseException("FTP删除目录时异常[" + path + "]");
            }
        } catch (IOException arg3) {
            throw new BaseException("FTP删除目录时网络异常[" + path + "]", arg3);
        }
    }

    public boolean removeDirectory(String path, boolean isall) {
        if (!isall) {
            return this.removeDirectory(path);
        } else {
            FTPFile[] files;
            try {
                files = this.client.listFiles(path);
            } catch (IOException arg9) {
                throw new BaseException("FTP删除目录时异常[" + path + "][" + isall + "]", arg9);
            }

            if (files != null && files.length != 0) {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>remove directory [" + path + "] and sub directory.");
                }

                FTPFile[] e = files;
                int len$ = files.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    FTPFile ftpfile = e[i$];
                    String name = ftpfile.getName();
                    if (ftpfile.isDirectory()) {
                        this.removeDirectory(path + "/" + name, true);
                    } else if (ftpfile.isFile()) {
                        this.deleteFile(path + "/" + name);
                    }
                }

                try {
                    return this.client.removeDirectory(path);
                } catch (IOException arg8) {
                    throw new BaseException("FTP删除目录时异常[" + path + "][" + isall + "]", arg8);
                }
            } else {
                return this.removeDirectory(path);
            }
        }
    }

    public List<String> getFileList(String path) {
        FTPFile[] files;
        try {
            files = this.client.listFiles(path);
        } catch (IOException arg4) {
            throw new BaseException("FTP获取文件列表异常[" + path + "]", arg4);
        }

        ArrayList list = new ArrayList();
        if (files != null && files.length != 0) {
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    list.add(files[i].getName());
                }
            }

            return list;
        } else {
            return list;
        }
    }

    public boolean deleteFile(String remotePathName) {
        try {
            boolean result = this.client.deleteFile(remotePathName);
            if (result) {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>delete file [" + remotePathName + "].");
                }

                return result;
            } else {
                throw new BaseException("FTP删除文件时异常[" + remotePathName + "]");
            }
        } catch (IOException arg3) {
            throw new BaseException("FTP删除文件时网络异常[" + remotePathName + "]", arg3);
        }
    }

    public boolean uploadFile(String localFilePath) {
        String fileName = FileManHelper.getFileName(localFilePath);
        if (log.isDebugEnabled()) {
            log.debug("ftp>ready upload file [" + localFilePath + "] to [" + fileName + "]...");
        }

        FileInputStream in;
        try {
            in = new FileInputStream(localFilePath);
        } catch (FileNotFoundException arg4) {
            throw new BaseException("FTP上传文件时找不到源文件[" + localFilePath + "]", arg4);
        }

        return this.uploadFile((InputStream) in, fileName);
    }

    public boolean uploadFile(String localFilePath, String remoteFileName) {
        if (log.isDebugEnabled()) {
            log.debug("ftp>ready upload file [" + localFilePath + "] to [" + remoteFileName + "]...");
        }

        FileInputStream in;
        try {
            in = new FileInputStream(localFilePath);
        } catch (FileNotFoundException arg4) {
            throw new BaseException("FTP上传文件时找不到源文件[" + localFilePath + "][" + remoteFileName + "]", arg4);
        }

        return this.uploadFile((InputStream) in, remoteFileName);
    }

    public boolean uploadFile(InputStream in, String remoteFileName) {
        boolean result = false;

        try {
            result = this.client.storeFile(remoteFileName, in);
        } catch (Exception arg11) {
            throw new BaseException("FTP上传文件时异常[" + remoteFileName + "]", arg11);
        } finally {
            try {
                in.close();
            } catch (IOException arg10) {
                throw new BaseException("FTP上传文件后关闭文件流异常[" + remoteFileName + "]", arg10);
            }

            if (result && log.isDebugEnabled()) {
                log.debug("ftp>upload file to [" + remoteFileName + "].");
            }

        }

        return result;
    }

    public boolean downloadFile(String remoteFileName, String localFilePath) {
        if (log.isDebugEnabled()) {
            log.debug("ftp>ready download file [" + remoteFileName + "] to [" + localFilePath + "]...");
        }

        File file = new File(localFilePath);

        try {
            return this.downloadFile(remoteFileName, (OutputStream) (new FileOutputStream(file)));
        } catch (FileNotFoundException arg4) {
            throw new BaseException("FTP下载文件时找不到文件[" + localFilePath + "]", arg4);
        }
    }

    public void getBinaryFileStream(String remoteFileName, OutputStream os, long startPos, long endPos) {
        InputStream in = null;

        try {
            this.setFileType(2);
            FTPFile[] e = this.client.listFiles(remoteFileName);
            if (e == null || e.length <= 0) {
                throw new BaseException("FTP获取文件二进制流异常");
            }

            FTPFile ftpFile = e[0];
            long fileSize = ftpFile.getSize();
            if (startPos <= fileSize) {
                if (endPos > fileSize) {
                    endPos = fileSize;
                }

                if (endPos < 0L) {
                    endPos = fileSize;
                }

                this.client.setRestartOffset(startPos);
                in = this.client.retrieveFileStream(remoteFileName);
                byte[] a = new byte[1024];
                long downloaded = 0L;
                long byteReaded = 0L;
                long blockSize = endPos - startPos;

                while ((byteReaded = (long) in.read(a)) != -1L) {
                    downloaded += byteReaded;
                    if (downloaded > blockSize && blockSize >= 0L) {
                        byteReaded -= downloaded - blockSize;
                    }

                    os.write(a, 0, (int) byteReaded);
                    if (downloaded > blockSize && blockSize >= 0L) {
                        break;
                    }
                }
            } else if (log.isDebugEnabled()) {
                log.debug("ftp>startPosition is greater than the remote file size  [" + remoteFileName + "].");
            }
        } catch (IOException arg25) {
            throw new BaseException("FTP获取文件二进制流网络异常", arg25);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException arg24) {
                throw new BaseException("FTP获取文件二进制流关闭异常", arg24);
            }

        }

    }

    public boolean downloadFile(String remoteFileName, OutputStream out) {
        boolean result = false;

        try {
            result = this.client.retrieveFile(remoteFileName, out);
        } catch (Exception arg11) {
            throw new BaseException("FTP文件下载异常[" + remoteFileName + "]", arg11);
        } finally {
            try {
                out.close();
            } catch (IOException arg10) {
                throw new BaseException("FTP文件下载关闭输出流异常[" + remoteFileName + "]", arg10);
            }

            if (result && log.isDebugEnabled()) {
                log.debug("ftp>download file[" + remoteFileName + "].");
            }

        }

        return result;
    }

    public InputStream getFileStream(String remoteFileName) {
        InputStream in = null;

        try {
            in = this.client.retrieveFileStream(remoteFileName);
        } catch (Exception arg6) {
            throw new BaseException("FTP获取文件流异常[" + remoteFileName + "]", arg6);
        } finally {
            if (in != null) {
                if (log.isDebugEnabled()) {
                    log.debug("ftp>download file stream from [" + remoteFileName + "].");
                }

            }
        }

        return in;
    }

    public OutputStream storeFileStream(String remoteFileName) {
        if (log.isDebugEnabled()) {
            log.debug("ftp>store file stream from [" + remoteFileName + "].");
        }

        try {
            return this.client.storeFileStream(remoteFileName);
        } catch (IOException arg2) {
            throw new BaseException("FTP存入文件异常[" + remoteFileName + "]", arg2);
        }
    }

    public boolean rename(String from, String to) {
        if (log.isDebugEnabled()) {
            log.debug("ftp>rename file from [" + from + "] to [" + to + "].");
        }

        try {
            return this.client.rename(from, to);
        } catch (IOException arg3) {
            throw new BaseException("FTP重命名异常[" + from + "]", arg3);
        }
    }

    public boolean completePendingCommand() {
        try {
            return client.completePendingCommand();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
