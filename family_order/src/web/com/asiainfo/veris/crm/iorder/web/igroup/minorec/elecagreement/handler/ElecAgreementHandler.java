package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.handler;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.ArrayUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark.maker.WaterMarkMakerUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

public class ElecAgreementHandler extends BizHttpHandler {

    private static final transient Logger log = Logger.getLogger(ElecAgreementHandler.class);
    
    private static final String EAGREE_FTPSITE = "eosdata";

    private static final int BETY = 256;

    private static final int DEGREE = -45;
    private static final int DEFAULT_FONT_SIZE = 14;
    private static final int DEFAULT_COLS_NUM = 4;
    private static final int DEFAULT_HIGHT_NUM = 50;
    private static final float DEFAULT_ALPHA = 0.3F;

    private static final String FONT_NAME = "simsun";

    private static Font font2 = WaterMarkMakerUtils.createFont("elecpdftemplate/font/" + FONT_NAME + ".ttf");

    public void uploadImage() throws Exception {
        IData pageData = getData();
        IData data = new DataMap();
        String base64Bitmap = pageData.getString("base64Bitmap");
        if (StringUtils.isEmpty(base64Bitmap))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未找到图片信息！");
        }
        
        if(log.isDebugEnabled()){
            log.debug("pangs:-------------ARCHIVES_ID:"+pageData.getString("ARCHIVES_ID"));
        }
        
        //上传图片至FTP
        IData image = new DataMap();
        image.put("base64Bitmap", base64Bitmap);
        image.put("ARCHIVES_NAME",pageData.getString("ARCHIVES_NAME"));
        image.put("ARCHIVES_ID",pageData.getString("ARCHIVES_ID"));
        IDataset fileIds = uploadImageToFtp(image);
        
        //更新协议附件
        //data.put("base64Bitmap",base64Bitmap);
        //data.put("ARCHIVES_NAME",pageData.getString("ARCHIVES_NAME"));
        data.put("ARCHIVES_ATTACH",fileIds.toString());
        data.put("ARCHIVES_ID",pageData.getString("ARCHIVES_ID"));
        data.put("STATE_DESC","phone");

        CSViewCall.call( this,"SS.AgreementInfoSVC.uploadImage", data);
        
        if(log.isDebugEnabled()){
            log.debug("pangs:-------------data:"+data.toString());
        }

        if(IDataUtil.isNotEmpty(fileIds)){
            data.put("FILE_ID", fileIds.first().getString("FILE_ID"));
            data.put("FILE_NAME", fileIds.first().getString("FILE_NAME"));
        }
        setAjax(data);
    }


    private IDataset uploadImageToFtp(IData image) throws Exception{
        String imgStr = image.getString("base64Bitmap");
        String archivesName = image.getString("ARCHIVES_NAME",String.valueOf(System.currentTimeMillis()));

        IData input = new DataMap();
        input.put("ARCHIVES_ID",image.getString("ARCHIVES_ID"));
        IDataset archiveInfos = CSViewCall.call(this,"SS.AgreementInfoSVC.queryElectronicAgreementInfo",input);
        String agreementId = "";
        if(DataUtils.isNotEmpty(archiveInfos)){
            agreementId = archiveInfos.first().getString("AGREEMENT_ID");
        }
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return new DatasetList();
        BASE64Decoder decoder = new BASE64Decoder();
        try{
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i){
                if(b[i]<0){//调整异常数据
                    b[i]+=BETY;
                }
            }
            ByteArrayInputStream baInStream = new ByteArrayInputStream(b);
            byte[] imgByte = addWaterMark(baInStream,agreementId);
            if(log.isDebugEnabled()){
                log.debug("pangs:-------------imgByte:"+ArrayUtils.toString(imgByte));
            }

            ByteArrayInputStream inputStream = new ByteArrayInputStream(imgByte);

            // 4- 远程写入FTP
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(this.getVisit());
            String fileId = ImpExpUtil.getImpExpManager().getFileAction().upload(inputStream, EAGREE_FTPSITE, "elecAgreement",
                    archivesName + ".jpg",false);

            if(log.isDebugEnabled()){
                log.debug("pangs:-------------AGREEMENT_ID:"+agreementId);
                log.debug("pangs:-------------FILE_ID:"+fileId);
            }

            IData retData = new DataMap();
            retData.put("FILE_ID", fileId);
            retData.put("FILE_NAME", archivesName + ".jpg");
            IDataset results = new DatasetList();
            results.add(retData);
            return results;
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    private byte[] addWaterMark(InputStream inputStream,String agreementId) throws Exception{
        BufferedImage image = ImageIO.read(inputStream);

        //File file = new File("elecpdftemplate/logo/markLogo.png");
        InputStream markInputStream = ElecAgreementHandler.class.getClassLoader().getResourceAsStream("elecpdftemplate/logo/markLogo.png");
        BufferedImage waterImage = ImageIO.read(markInputStream);

        int imgHeight = image.getHeight();
        int imgWidth = image.getWidth();

        //安卓照片宽度大于1000
        int androidWidth = 1000;
        boolean flag = imgWidth>=androidWidth;

        int androidSize = 28;
        //缩放比例
        double p = 0.3d;
        if(flag){
            p = 0.6d;
        }
        int wh = (int)(waterImage.getHeight()*p);
        int ww = (int)(waterImage.getWidth()*p);


        String text = "合同编码："+agreementId;//StaticUtil.getStaticValue("EPAPER_PDF_WATER_STR", "99");

        String size = StaticUtil.getStaticValue("EPAPER_WATER_MAKER_FONTSIZE", "99");
        int sizeNum = 0;
        try {
            sizeNum = Integer.valueOf(size);
        }catch (NumberFormatException e){
            sizeNum = DEFAULT_FONT_SIZE;//默认字体为14
        }

        if(flag){
            sizeNum = androidSize;
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
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        graphics.drawImage(image, 0, 0, imgWidth, imgHeight, null);
        graphics.setColor(fontColor);
        graphics.setFont(font);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        graphics.rotate(Math.toRadians(DEGREE), imgWidth, imgHeight);
        JLabel infoLabel = new JLabel(text);
        //JLabel staffInfoLabel = new JLabel(staffInfo);
        //JLabel label = infoLabel.getWidth() > staffInfoLabel.getWidth() ? infoLabel : staffInfoLabel;
        JLabel label = infoLabel;
        FontMetrics metrics = label.getFontMetrics(font);
        int width = metrics.stringWidth(label.getText());

        if(!flag){
            width = (int)(width*Math.abs(Math.toRadians(DEGREE)));
        }

        int colsNum = (imgHeight / width) * DEFAULT_COLS_NUM;
        int rowsNum = imgWidth / width;

        int xPoint = -width;
        if(flag){
            xPoint = -400;
        }
        //把坐标原点移动到左下角
        graphics.translate(xPoint,imgHeight);

        int defAddHight = DEFAULT_HIGHT_NUM;
        if (!flag){
            defAddHight = 20;
        }

        for (int i = 0; i < colsNum; i++)
        {
            for (int j = 0; j < rowsNum; j++)
            {
                int x = i * width + j * width;
                int y = -i * width + j * width;
                graphics.drawString(text, x, y);
                graphics.drawImage(waterImage,x,y+defAddHight,ww,wh,null);
                //graphics.drawString(staffInfo, x, y + DEFAULT_HIGHT_NUM);
            }
        }
        graphics.dispose();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);

        ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
        Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, "JPG");

        ImageWriter writer = null;
        if (iter.hasNext()) {
            writer =  iter.next();
        }

        try {
            writer.setOutput(imageOutputStream);
            writer.write(image);
        }finally {
            if(writer != null){
                writer.dispose();
            }
        }

//        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
//
//        encoder.encode(image);
        //ImageIO.write(image, "JPG", outputStream);

        byte[] imgByte = outputStream.toByteArray();

        inputStream.close();
        outputStream.close();

        return imgByte;
    }

    public void test() throws Exception{
        File file = new File("D:\\usr\\attach.jpg");
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage image = ImageIO.read(file);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(image,"jpg",outputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            String imgStr = encoder.encode(outputStream.toByteArray());
            IData imageData = new DataMap();
            imageData.put("base64Bitmap", imgStr);
            imageData.put("ARCHIVES_NAME","测试协议");
            imageData.put("ARCHIVES_ID","163256");
            IDataset fileIds = uploadImageToFtp(imageData);
            IData data = new DataMap();
            if(IDataUtil.isNotEmpty(fileIds)){
                data.put("FILE_ID", fileIds.first().getString("FILE_ID"));
                data.put("FILE_NAME", fileIds.first().getString("FILE_NAME"));
            }
            setAjax(data);
        }finally {
            if(outputStream != null)
                outputStream.close();
        }
    }
}
