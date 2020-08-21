package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark.maker;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.view.VisitManager;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.EpaperSerletUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * PDF水印处理
 *
 * @author ckh
 * @date 2018/12/24.
 */
public class PdfWaterMarkMaker implements IWaterMarkMaker, IBizCommon
{
    private final BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

    public PdfWaterMarkMaker() throws IOException, DocumentException
    {
    }

    /**
     * 处理水印
     *
     * @param paramMap 传入参数集合
     * @param response 返回对象
     */
    @Override
    public void dealWaterMarkMaker(IData paramMap, HttpServletResponse response) throws Exception
    {
        String fileId = paramMap.getString("FILE_ID");
        String download = paramMap.getString("DOWNLOAD");
        File file = WaterMarkMakerUtils.getFTPFile(fileId, true);
        String fileName = paramMap.getString("FILE_NAME",file.getName());
        byte[] bytesPdf = new byte[(int) file.length()];
        readFileToBytes(file, bytesPdf);
        setResponseHeader(response, download, file,fileName);
        OutputStream os = response.getOutputStream();
        makeWaterMark(bytesPdf, os,paramMap);

    }

    /**
     * 设置返回头信息
     *
     * @param response {@linkplain HttpServletResponse}
     * @param download 是否下载
     * @param file     PDF文件对象
     * @throws UnsupportedEncodingException 编码不支持
     */
    private void setResponseHeader(HttpServletResponse response, String download, File file, String fileName)
            throws UnsupportedEncodingException
    {
        String contentType = EpaperSerletUtil.getContentTypeByFileName(fileName);
        response.setContentType(contentType);
        boolean isDownload = !StringUtils.isEmpty(download);
        WaterMarkMakerUtils.setHeaderDownloadOrInline(isDownload, file, fileName, response);
    }

    /**
     * 从文件对象中读取出字节数组
     *
     * @param file     {@linkplain File}
     * @param bytesPdf {@linkplain byte[]}
     * @throws Exception FTP文件流读取失败
     */
    private void readFileToBytes(File file, byte[] bytesPdf) throws Exception
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
            fis.read(bytesPdf);
        }
        catch (IOException e)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "FTP文件流读取失败！");
        }
        finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
            }
            catch (IOException e)
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "文件读取流关闭失败");
            }
        }
    }

    /**
     * 设置PDF水印
     *
     * @param bytesPdf PDF内容字节数组
     * @param os       {@linkplain HttpServletResponse#getOutputStream()}
     * @throws Exception 水印设置失败
     */
    private void makeWaterMark(byte[] bytesPdf, OutputStream os,IData param) throws Exception
    {
        BizVisit bizVisit = (BizVisit) VisitManager.getVisit();
        String staffInfo = "工号：" + bizVisit.getStaffId() + "姓名：" + bizVisit.getStaffName();
        PdfReader reader = new PdfReader(bytesPdf);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, bos);
        int pageNum = reader.getNumberOfPages();
        for (int i = 1; i <= pageNum; i++)
        {
            PdfContentByte over = stamper.getOverContent(i);
            over.saveState();
            // 水印文字
            String text = "合同编码："+param.getString("AGREEMENT_ID","");//StaticUtil.getStaticValue("EPAPER_PDF_WATER_STR", "99");
            String strSize = StaticUtil.getStaticValue("EPAPER_PDF_WATER_FONTSIZE", "99");
            if (StringUtils.isEmpty(strSize))
            {
                strSize = "20";
            }
            int fontsize = Integer.parseInt(strSize);
            String strOpacity = StaticUtil.getStaticValue("EPAPER_PDF_WATER_OPACITY", "99");
            if (StringUtils.isEmpty(strOpacity))
            {
                strOpacity = "0.5";
            }
            float fillOpacity = Float.valueOf(strOpacity);
            PdfGState pdfGState = new PdfGState();
            pdfGState.setFillOpacity(fillOpacity);
            over.setGState(pdfGState);
            over.beginText();
            BaseColor fontColor = BaseColor.BLUE;
            over.setColorFill(fontColor);
            over.setFontAndSize(baseFont, fontsize);

            double alpha = ((double) 45 * 3.1415926535897931D) / 180D;
            float cos = (float) Math.cos(alpha);
            float sin = (float) Math.sin(alpha);

            over.setTextMatrix(cos, sin, -sin, cos, 200, 300);
            over.showText(staffInfo);
            over.setTextMatrix(cos, sin, -sin, cos, 150, 300);
            over.showText(text);

            over.setTextMatrix(cos, sin, -sin, cos, 200, 50);
            over.showText(staffInfo);
            over.setTextMatrix(cos, sin, -sin, cos, 150, 50);
            over.showText(text);

            over.setTextMatrix(cos, sin, -sin, cos, 200, 550);
            over.showText(staffInfo);
            over.setTextMatrix(cos, sin, -sin, cos, 150, 550);
            over.showText(text);

            over.endText();
            over.restoreState();
        }
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        byte[] b = bos.toByteArray();
        os.write(b, 0, b.length);
        os.close();
        os.flush();


        bos.close();
    }

    @Override
    public String getPageName()
    {
        return null;
    }

    @Override
    public String getMenuId()
    {
        return null;
    }

    @Override
    public String getLoginLogId()
    {
        return null;
    }

    @Override
    public IData getRequestData()
    {
        return null;
    }
}
