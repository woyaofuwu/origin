package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark.maker;

import com.ailk.biz.BizVisit;
import com.ailk.biz.view.IBizCommon;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.view.VisitManager;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 水印制作工具类
 *
 * @author ckh
 * @date 2018/12/26.
 */
public class WaterMarkMakerUtils
{
    private static final transient Logger log = Logger.getLogger(WaterMarkMakerUtils.class);

    private static final int IPV4_LENGTH = 15;

    /**
     * 取FTP文件
     *
     * @param fileId    FTP文件ID
     * @param hasSuffix 是否带后缀
     * @return {@linkplain File}
     * @throws Exception FTP读取异常
     */
    public static File getFTPFile(String fileId, boolean hasSuffix) throws Exception
    {
        IVisit visit = VisitManager.getVisit();
//        测试使用
//        File file = new File("D:\\testnew\\hnpdf\\协议1.pdf");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(visit);

        return ImpExpUtil.getImpExpManager().getFileAction().download(fileId, hasSuffix);
    }

    /**
     * 设置是否下载或者在线显示
     *
     * @param download 是否下载
     * @param file     文件对象
     * @param response {@linkplain HttpServletResponse}
     * @throws UnsupportedEncodingException 编码不支持
     */
    public static void setHeaderDownloadOrInline(boolean download, File file,String fileName, HttpServletResponse response)
            throws UnsupportedEncodingException
    {
        if (!download)
        {
            response.addHeader("Content-Disposition",
                    "inline;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
        }
        else
        {
            response.addHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
        }
    }

    /**
     * 创建字体，并加载到系统Java图形字体空间
     * @param path 字体路径
     * @return {@linkplain Font}
     */
    public static Font createFont(String path)
    {
        Font font = null;
        InputStream is = null;
        try
        {
            is = ImageWaterMarkMaker.class.getClassLoader().getResourceAsStream(path);
            // 创建自定义字体
            // 字体.ttf格式
            assert is != null;
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            // 设置字体风格、尺寸
            font.deriveFont(Font.BOLD, 25F);
            // 获得本地图形环境对象
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // 在本地图形环境中注册当前字体
            boolean register = ge.registerFont(font);
            is.close();
            if (register)
            {
                log.debug("注册字体成功");
            }
            else
            {
                log.error("注册字体失败。path:" + path);
            }
        }
        catch (Exception e)
        {
            log.error("注册字体失败！", e);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    log.error("文件读取流关闭失败！", e);
                }
            }
        }

        return font;
    }

    public static String getIpAddress(HttpServletRequest request) throws Exception{
        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>IPV4_LENGTH){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                //ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
                String[] ipArray = ipAddress.split(",");
                for(String ip : ipArray){
                    if(ip != null && ip.length()>0&&!"unknown".equalsIgnoreCase(ip)){
                        ipAddress = ip;
                        break;
                    }
                }
            }
        }
        return ipAddress;

    }

    public static void insertLog(IBizCommon bc,IData param, BizVisit visit) throws Exception{
        IData data = new DataMap();
        data.put("AGREEMENT_ID",param.getString("AGREEMENT_ID"));
        data.put("OPER_TYPE",StringUtils.isEmpty(param.getString("DOWNLOAD"))?"SHOW":"DOWNLOAD");
        data.put("OPER_TIME",SysDateMgr4Web.getSysDate());
        data.put("OPER_STAFF_ID",visit.getStaffId());
        data.put("OPER_DEPART_ID",visit.getDepartId());
        data.put("OPER_IP",param.getString("REQUEST_IP"));

        IData input  = new DataMap();
        IDataset dataset = new DatasetList();
        dataset.add(data);
        input.put("LOG_LIST",dataset);
        CSViewCall.call(bc,"SS.AgreementInfoSVC.insertLog",input);
    }
}
