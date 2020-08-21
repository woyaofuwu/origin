
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import com.ailk.common.config.ModuleCfg;
import com.ailk.common.file.FileUtil;
import com.ailk.common.util.FtpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.cmcc.mm7.vasp.common.MMContent;
import com.cmcc.mm7.vasp.common.MMContentType;
import com.cmcc.mm7.vasp.common.SOAPDecodeException;
import com.cmcc.mm7.vasp.common.SOAPDecoder;
import com.cmcc.mm7.vasp.message.MM7DeliverReq;
import com.cmcc.mm7.vasp.message.MM7RSReq;

public class Mm7Parse
{
    private static final String charSet = "GBK";

    private static final String localtempdir = "/tmp";

    protected static Random rand = null;

    // 一级BOSS主机地址
    private static String ibossIP;

    private static String ibossUser;

    private static String ibossPassword;

    // 一级BOSS彩信文件上传目录
    private static String ibossUpDir;

    // 一级BOSS彩信文件下发目录
    private static String ibossDownDir;

    // 文件服务器主机配置(解析后文件存放地址)
    private static String destIP;

    private static String destUser;

    private static String destPassword;

    private static String destPath;

    static
    {
        try
        {
            ibossIP = ModuleCfg.getProperty("mmsfile/ftp/iboss_server");
            ibossUser = ModuleCfg.getProperty("mmsfile/ftp/iboss_user");
            ibossPassword = ModuleCfg.getProperty("mmsfile/ftp/iboss_password");
            ibossUpDir = ModuleCfg.getProperty("mmsfile/ftp/iboss_updir");
            ibossDownDir = ModuleCfg.getProperty("mmsfile/ftp/iboss_downdir");

            destIP = ModuleCfg.getProperty("mmsfile/ftp/dest_server");
            destUser = ModuleCfg.getProperty("mmsfile/ftp/dest_user");
            destPassword = ModuleCfg.getProperty("mmsfile/ftp/dest_password");
            destPath = ModuleCfg.getProperty("mmsfile/ftp/dest_sourcepath");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static String createFileName(String fileName)
    {
        String result = null;
        if (null != fileName && fileName.trim().length() > 0)
        {
            String temp = fileName.toLowerCase();
            if (temp.indexOf(".") < 1)
            {
                return result;
            }
            result = temp.substring(0, temp.indexOf("."));
            if (temp.indexOf(".jpeg") > 0)
            {
                result += ".jpeg";
            }
            else if (temp.indexOf(".jpg") > 0)
            {
                result += ".jpg";
            }
            else if (temp.indexOf(".gif") > 0)
            {
                result += ".gif";
            }
            else if (temp.indexOf(".png") > 0)
            {
                result += ".png";
            }
            else if (temp.indexOf(".mid") > 0)
            {
                result += ".mid";
            }
            else if (temp.indexOf(".txt") > 0)
            {
                result += ".txt";
            }
            else if (temp.indexOf(".smil") > 0)
            {
                result += ".smil";
            }
            else if (temp.indexOf(".xml") > 0)
            {
                result += ".xml";
            }
        }
        return result;
    }

    private static String createFileNameByContentType(MMContentType type) throws Exception
    {
        String result = null;
        if (null != type)
        {
            String time = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
            if (type.getSubType().trim().equalsIgnoreCase("jpeg"))
            {
                result = time + generateRandom(4) + ".jpg";
            }
            else if (type.getSubType().trim().equalsIgnoreCase("gif"))
            {
                result = time + generateRandom(4) + ".gif";
            }
            else if (type.getSubType().trim().equalsIgnoreCase("midi"))
            {
                result = time + generateRandom(4) + ".mid";
            }
            else if (type.getSubType().trim().equalsIgnoreCase("png"))
            {
                result = time + generateRandom(4) + ".png";
            }
            else if (type.getPrimaryType().trim().equalsIgnoreCase("text"))
            {
                result = time + generateRandom(4) + ".txt";
            }
        }
        return result;
    }

    public static void deleteFile(String path)
    {
        File file = new File(path);
        if (!file.isDirectory())
        {
            file.delete();
        }
        else if (file.isDirectory())
        {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; i++)
            {
                File filessFile = new File(path + "\\" + fileList[i]);
                if (!filessFile.isDirectory())
                {
                    filessFile.delete();
                }
                else if (filessFile.isDirectory())
                {
                    deleteFile(path + "\\" + fileList[i]);
                }
            }
            file.delete();
        }
    }

    public static void doGenerateMultiMedia(MM7DeliverReq mm7DeliverReq, String fileDir) throws Exception
    {
        // 判断是否存在多媒体消息的内容，如果存在，则取出多媒体消息的内容
        if (mm7DeliverReq.isContentExist())
        {
            MMContent mmsContents = mm7DeliverReq.getContent();// 取出多媒体消息的内容
            // 判断是否存在嵌套媒体
            String fileName = "";
            if (mmsContents.isMultipart())
            {
                String contentType = mmsContents.getContentType().getSubType();
                List contentList = mmsContents.getSubContents();
                if (contentType.equalsIgnoreCase("related"))
                {
                    for (int j = 0; j < contentList.size(); j++)
                    {
                        MMContent mmContent = (MMContent) contentList.get(j);
                        fileName = mmContent.getContentID();
                        if (null != fileName && fileName.trim().length() > 0)
                        {
                            fileName = fileName.replaceAll("<", "");
                            fileName = fileName.replaceAll(">", "");
                            fileName = getFileNameR(fileName, mmContent.getContentType().getSubType());
                            fileName = createFileName(fileName);

                        }
                        if (null == fileName || fileName.trim().length() == 0)
                        {
                            fileName = removequto(mmContent.getContentName());
                            fileName = createFileName(fileName);
                        }
                        try
                        {
                            byte content[] = mmContent.getContent();
                            if (mmContent.getContentType().getSubType().equals("plain"))
                            {
                                String str = new String(content, "utf-8");
                                OutputStream out = new FileOutputStream(fileDir + "/" + fileName);
                                OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");
                                writer.write(str);
                                writer.close();
                            }
                            else
                            {
                                FileOutputStream fileStream = new FileOutputStream(fileDir + "/" + fileName);
                                fileStream.write(content);
                                fileStream.close();
                            }

                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < contentList.size(); i++)
                    {
                        MMContent mmContent = (MMContent) contentList.get(i);
                        byte content[] = mmContent.getContent();
                        fileName = mmContent.getContentID();
                        if (null != fileName && fileName.trim().length() > 0)
                        {
                            fileName = fileName.replaceAll("<", "");
                            fileName = fileName.replaceAll(">", "");
                            fileName = getFileName(fileName, mmContent.getContentType().getSubType());
                            fileName = createFileName(fileName);
                        }
                        if (null == fileName || fileName.trim().length() == 0)
                        {
                            fileName = createFileNameByContentType(mmContent.getContentType());
                        }
                        try
                        {
                            FileOutputStream fileStream = new FileOutputStream(fileDir + "/" + fileName);
                            fileStream.write(content);
                            fileStream.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else
            {
                try
                {
                    fileName = mmsContents.getContentName();
                    if (null == fileName || fileName.trim().length() == 0)
                    {
                        fileName = mmsContents.getContentID();
                    }
                    fileName = createFileName(fileName);

                    byte content[] = mmsContents.getContent();
                    FileOutputStream fileStream = new FileOutputStream(fileDir + fileName);
                    fileStream.write(content);
                    fileStream.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void downloadAttachFile(FtpUtil sourceftp, String filePathName, String localFileName) throws Exception
    {
        try
        { // 下载失败时FtpUtil会抛异常，故不检查返回值
            sourceftp.downloadFile(filePathName, localFileName);
        }
        catch (Exception e)
        {
            // 若未下载成功，可能是因为目标文件已被压缩，尝试加上gz后缀名下载
            sourceftp.downloadFile(filePathName + ".gz", localFileName + ".gz");
            // 若压缩后的文件下载成功，需要进行解压
            // 建立grip压缩文件输入流
            FileInputStream fin = new FileInputStream(localFileName + ".gz");
            // 建立gzip解压工作流
            GZIPInputStream gzin = new GZIPInputStream(fin);
            // 建立解压文件输出流
            FileOutputStream fout = new FileOutputStream(localFileName);
            try
            {
                byte[] buf = new byte[1024];
                int num;
                while ((num = gzin.read(buf, 0, buf.length)) != -1)
                {
                    fout.write(buf, 0, num);
                }
                File gzFile = new File(localFileName + ".gz");
                gzFile.delete();
            }
            catch (Exception ex)
            {
                throw ex;
            }
            finally
            {
                gzin.close();
                fout.close();
                fin.close();
            }
        }
    }

    public static MM7DeliverReq gatherDeliverReqFromFile(String filename) throws Exception
    {
        MM7DeliverReq req = new MM7DeliverReq();
        FileInputStream in = new FileInputStream(filename);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1)
        {
            out.write(temp, 0, size);
        }
        in.close();

        SOAPDecoder soapDecoder = new SOAPDecoder();
        soapDecoder.setMessage(out);
        String s = new String(out.toByteArray());
        try
        {
            soapDecoder.decodeMessage();
            MM7RSReq res = soapDecoder.getMessage();
            if (res == null)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "文件地址" + filename + "**文件内容：" + s + "**转码后文件内容：" + res + "**文件解析失败！");
            }
            if (soapDecoder.getMessageName().equals("DeliverReq"))
            {
                req = (MM7DeliverReq) res;
                ;
            }
            return req;
        }
        catch (SOAPDecodeException e)
        {
            e.printStackTrace();
        }
        return req;
    }

    public static String generateRandom(int bits)
    {
        if (null == rand)
        {

            rand = new Random(System.currentTimeMillis());
        }
        StringBuilder buffer = new StringBuilder();
        String temp;
        while (bits > 9)
        {
            temp = String.valueOf(rand.nextInt(10 ^ 9));
            buffer.append(temp);
            bits -= temp.length();
        }

        while (bits > 0)
        {
            temp = String.valueOf(rand.nextInt(1 << bits));
            buffer.append(temp);
            bits -= temp.length();
        }
        return buffer.toString();

    }

    public static File[] getFileList(String path) throws Exception
    {
        File file = new File(path);
        return ((file.exists()) ? file.listFiles() : null);
    }

    public static String getFileName(String fileName, String fileType)
    {
        if ((null != fileName) && (fileName.length() > 0))
        {
            if (fileName.indexOf(".") >= 0)
            {
                String type = fileName.substring(fileName.lastIndexOf(".") + 1);
                if ((null != fileType) && (fileType.length() > 0))
                {
                    if (fileType.equals("plain"))
                    {
                        if (type.equals("txt") == false)
                        {
                            fileName = fileName + ".txt";
                        }
                    }
                    else
                    {
                        if (fileType.equals(type) == false)
                        {
                            fileName = fileName + "." + fileType;
                        }
                    }
                }
            }
            else
            {
                if (fileType.equals("plain"))
                {
                    fileName = fileName + ".txt";
                }
                else
                {
                    fileName = fileName + "." + fileType;
                }
            }
        }
        return fileName;
    }

    public static String getFileNameR(String fileName, String fileType)
    {
        if ((null != fileName) && (fileName.length() > 0))
        {
            if (fileName.indexOf(".") >= 0)
            {
                String type = fileName.substring(fileName.lastIndexOf(".") + 1);
                if ((null != fileType) && (fileType.length() > 0))
                {
                    if (fileType.equals("plain"))
                    {
                        if (type.equals("txt") == false)
                        {
                            fileName = fileName + ".txt";
                        }
                    }
                }
            }
            else
            {
                if (fileType.equals("plain"))
                {
                    fileName = fileName + ".txt";
                }
                else
                {
                    fileName = fileName + "." + fileType;
                }
            }
        }
        return fileName;
    }

    public static void main(String[] args)
    {
        String localFileName = "E://mmstest//source//WFSEND_0018_731_20130417_000015.dat";
        try
        {
            doGenerateMultiMedia(gatherDeliverReqFromFile(localFileName), "E://mmstest//dest//20131127");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean removeDirectory(String file_path, boolean isall) throws Exception
    {
        File file = new File(file_path);
        if (!(file.exists()))
            return false;

        if (isall)
        {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; ++i)
            {
                File fileItem = fileList[i];
                if (fileItem.isDirectory())
                    removeDirectory(fileItem.getPath(), isall);
                else
                {
                    fileItem.delete();

                }
            }
        }

        return file.delete();
    }

    public static String removequto(String name)
    {
        String newname = name;
        if (null != newname)
        {
            if (newname.indexOf("\"") == 0)
            {
                newname = newname.substring(1);
            }
            if (newname.indexOf("\"") == (newname.length() - 1))
            {
                newname = newname.substring(0, (newname.length() - 1));
            }
        }
        return newname;
    }

    /*
     * 从彩信内容附件解析出彩信多媒体文件,原始文件在FTP服务器上 filename 文件名
     */
    public static String resolvingMmsContent(String filename, String recvId, int type) throws Exception
    {
        StringBuilder reFileList = new StringBuilder();
        // 源文件所在目录
        String sourcePath;
        if (type == 0)
        {
            sourcePath = ibossUpDir;
        }
        else
        {
            sourcePath = ibossDownDir;
        }
        FileUtil fileUtil = new FileUtil();
        if (!fileUtil.createDirectory(localtempdir + "/" + recvId))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_73);
        }
        // 本地文件名
        String localFileName = localtempdir + "/" + recvId + "/" + filename;
        FtpUtil sourceftp = new FtpUtil(ibossIP, ibossUser, ibossPassword, sourcePath);
        sourceftp.setFileType(2);
        downloadAttachFile(sourceftp, sourcePath + "/" + filename, localFileName);
        sourceftp.closeServer();

        // 调用API解析，生成可视的多媒体文件
        doGenerateMultiMedia(gatherDeliverReqFromFile(localFileName), localtempdir + "/" + recvId);

        File[] list = getFileList(localtempdir + "/" + recvId);
        if (list != null && list.length > 0)
        {
            FtpUtil destftp = new FtpUtil(destIP, destUser, destPassword, destPath);
            String destFilePath = destPath + "/" + recvId.substring(0, 8);
            try
            {
                destftp.changeDirectory(destFilePath);
            }
            catch (Exception e)
            {
                destftp.createDirectory(destFilePath);
                destftp.changeDirectory(destFilePath);
            }
            destFilePath = destFilePath + "/" + recvId;
            destftp.createDirectory(destFilePath);
            destftp.changeDirectory(destFilePath);
            for (int i = 0; i < list.length; i++)
            {
                String fileName = list[i].getName();
                // 上传解析生成的多媒体文件至FTP服务器
                if (!filename.equals(fileName))
                {
                    destftp.setFileType(2);
                    destftp.uploadFile(localtempdir + "/" + recvId + "/" + fileName, destFilePath + "/" + fileName);
                    reFileList.append(fileName).append(";");
                }
            }

            // 删除本地临时目录
            removeDirectory(localtempdir + "/" + recvId, true);
        }
        int length = reFileList.toString().length();
        if (length > 0)
        {
            return reFileList.toString().substring(0, length - 1);
        }
        return null;
    }
}
