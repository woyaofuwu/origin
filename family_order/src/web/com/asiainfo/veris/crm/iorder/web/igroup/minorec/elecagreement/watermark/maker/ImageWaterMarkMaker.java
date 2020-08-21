package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark.maker;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.view.VisitManager;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.EpaperSerletUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 图片水印处理
 *
 * @author ckh
 * @date 2018/12/26.
 */
public class ImageWaterMarkMaker implements IWaterMarkMaker
{
    private static final String FONT_NAME = "simsun";
    private boolean isLoadCNFont = false;
    private static final int DEGREE = -45;

    private static final int DEFAULT_FONT_SIZE = 14;
    private static final int DEFAULT_COLS_NUM = 2;
    private static final int DEFAULT_HIGHT_NUM = 50;


    public ImageWaterMarkMaker()
    {
        Font font2 = WaterMarkMakerUtils.createFont("elecpdftemplate/font/" + FONT_NAME + ".ttf");
        if (font2 != null)
        {
            isLoadCNFont = true;
        }
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
        if (!isLoadCNFont)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "中文字体初始化失败！请联系开发人员！");
        }
        String fileId = paramMap.getString("FILE_ID");
        String fileType = paramMap.getString("IMG_TYPE");
        boolean hasSuffix = paramMap.getBoolean("needSuffix",true);
//        测试使用
        //File file = new File("D:\\usr\\attach.jpg");
        File file = WaterMarkMakerUtils.getFTPFile(fileId, hasSuffix);
        // 兼容无后缀的情况
        if (!file.exists())
        {
            file = WaterMarkMakerUtils.getFTPFile(fileId, false);
        }

        String fileName = paramMap.getString("FILE_NAME",file.getName());
        setResponseHeader(paramMap, response, file,fileName);
        makeWaterMaker(paramMap,file, fileType, response.getOutputStream());
    }

    /**
     * 设置图片水印
     *
     * @param file         文件对象
     * @param fileType     图片类型
     * @param outputStream {@link HttpServletResponse#getOutputStream() 返回输出流对象}
     * @throws Exception 处理异常
     */
    private void makeWaterMaker(IData paramMap,File file, String fileType, ServletOutputStream outputStream) throws Exception
    {
        BizVisit bizVisit = (BizVisit) VisitManager.getVisit();
        //String staffInfo = "合同编码：2019111314731771" ;//测试用
        String staffInfo = "工号：" + bizVisit.getStaffId() + "姓名：" + bizVisit.getStaffName();
        String text = "合同编码："+paramMap.getString("AGREEMENT_ID","");//StaticUtil.getStaticValue("EPAPER_PDF_WATER_STR", "99");

        String size = StaticUtil.getStaticValue("EPAPER_WATER_MAKER_FONTSIZE", "99");
        int sizeNum = 0;
        try {
            sizeNum = Integer.valueOf(size);
        }catch (NumberFormatException e){
            sizeNum = DEFAULT_FONT_SIZE;//默认字体为14
        }
        Font font = new Font(FONT_NAME, Font.BOLD, sizeNum);

        String colorStr = StaticUtil.getStaticValue("EPAPER_WATER_MAKER_COLOR", "99");
        Color fontColor = null;
        try{
            String[] colors = colorStr.split(",");
            fontColor = new Color(Integer.valueOf(colors[0]),Integer.valueOf(colors[1]),Integer.valueOf(colors[2]));
        }catch (Exception e){
            fontColor = Color.BLUE;
        }

        String alphaStr = StaticUtil.getStaticValue("EPAPER_PDF_WATER_OPACITY", "99");
        if (StringUtils.isEmpty(alphaStr))
        {
            alphaStr = "0.3";
        }
        float alpha = Float.valueOf(alphaStr);
        BufferedImage image = ImageIO.read(file);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        int imgHeight = image.getHeight();
        int imgWidth = image.getWidth();
        graphics.drawImage(image, 0, 0, imgWidth, imgHeight, null);
        graphics.setColor(fontColor);
        graphics.setFont(font);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        graphics.rotate(Math.toRadians(DEGREE), imgWidth, imgHeight);
        JLabel infoLabel = new JLabel(text);
        JLabel staffInfoLabel = new JLabel(staffInfo);
        JLabel label = infoLabel.getWidth() > staffInfoLabel.getWidth() ? infoLabel : staffInfoLabel;
        FontMetrics metrics = label.getFontMetrics(font);
        int width = metrics.stringWidth(label.getText());
        width = (int)(width*Math.abs(Math.toRadians(DEGREE)));
        int colsNum = (imgHeight / width) * DEFAULT_COLS_NUM;
        int rowsNum = imgWidth / width;

        //把坐标原点移动到左下角
        graphics.translate(-width,imgHeight);
        for (int i = 0; i < colsNum; i++)
        {
            for (int j = 0; j < rowsNum; j++)
            {
                int x = i * width + j * width;
                int y = -i * width + j * width;
                graphics.drawString(text, x, y);
                graphics.drawString(staffInfo, x, y + DEFAULT_HIGHT_NUM);
            }
        }
        graphics.dispose();
        ImageIO.write(image, fileType, outputStream);

    }

    /**
     * 设置返回头信息
     *
     * @param paramMap 请求参数
     * @param response {@linkplain HttpServletResponse}
     * @param file     PDF文件对象
     * @throws UnsupportedEncodingException 编码不支持
     */
    private void setResponseHeader(IData paramMap, HttpServletResponse response, File file, String fileName)
            throws UnsupportedEncodingException
    {
        String contentType = EpaperSerletUtil.getContentTypeByFileName(fileName);
        response.setContentType(contentType);
        boolean isDownload = !StringUtils.isEmpty(paramMap.getString("DOWNLOAD"));
        WaterMarkMakerUtils.setHeaderDownloadOrInline(isDownload, file, fileName, response);
    }
}
