
package com.asiainfo.veris.crm.order.web.person.twoDimensionCode;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.swetake.util.Qrcode;

/**
 * @author dengshu
 * @date 2014年5月16日-下午5:41:34
 */
public class TwoDimensionCode
{

    /**
     * 图片打水印
     * 
     * @param bgImage
     *            背景图
     * @param waterImg
     *            水印图
     * @param uniqueFlag
     *            生成的新图片名称中的唯一标识，用来保证生成的图片名称不重复，如果为空或为null,将使用当前时间作为标识
     * @return 新图片路径
     */
    public static String addImageWater(String bgImage, String waterImg, String uniqueFlag)
    {
        int x = 0;
        int y = 0;
        String newImgPath = "";

        if (null == uniqueFlag)
        {
            uniqueFlag = SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND_SHORT);
        }
        else if (uniqueFlag.trim().length() < 1)
        {
            uniqueFlag = SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND_SHORT);
        }

        try
        {
            File file = new File(bgImage);
            String fileName = file.getName();
            Image image = ImageIO.read(file);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            Image waterImage = ImageIO.read(new File(waterImg)); // 水印文件
            int width_water = waterImage.getWidth(null);
            int height_water = waterImage.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1));
            int widthDiff = width - width_water;
            int heightDiff = height - height_water;
            x = widthDiff / 2;
            y = heightDiff / 2;

            g.drawImage(waterImage, x, y, width_water, height_water, null); // 水印文件结束
            g.dispose();

            if (bgImage.contains(fileName))
            {
                newImgPath = bgImage.replace(fileName, uniqueFlag + fileName);
            }
            File newImg = new File(newImgPath);
            ImageIO.write(bufferedImage, "png", newImg);

            File waterFile = new File(waterImg);

            if (file.exists())
            {
                file.delete();
            }

            if (waterFile.exists())
            {
                waterFile.delete();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return newImgPath;
    }

    public static void main(String[] args)
    {
        String imgPath = "D:/temp/Michael_QRCode.png";
        String encoderContent = "姓名：周小喻  \n小名：女汉子  \n技能：骂街、撒泼、赖地、砍价  \n必杀技：哇哇哭   \n联系方式：10068";// "Myblog [ http://sjsky.iteye.com ]"
        // "\nEMail [ sjsky007@gmail.com ]";
        TwoDimensionCode handler = new TwoDimensionCode();
        int size = 20;
        int middleSize = handler.getImgSize(size) / 4;
        handler.encoderQRCode(encoderContent, imgPath, "png", size);
        /*
         * try { // OutputStream output = new FileOutputStream(imgPath); // handler.encoderQRCode(encoderContent,
         * output); } catch (Exception e) { e.printStackTrace(); }
         */

        // 缩放水印图片,为保证二维码的读取正确，图片不超过二维码图片的五分之一，这里设为六分之一
        // d:/qr/heihei.png 这图片是要加在二维码中间的那张图
        String waterImgPath = resizeImg(new File("d:/temp/nn.png"), middleSize, middleSize);

        //
        // //生成带有边框图片的二维码,返回的是生成好的二维码图片的所在路径
        String qrImage = addImageWater(imgPath, waterImgPath, "thatway");

        String decoderContent = handler.decoderQRCode(qrImage);
    }

    /**
     * 图片缩放
     * 
     * @param filePath
     *            图片路径
     * @param height
     *            缩放到高度
     * @param width
     *            缩放宽度
     * @param fill
     *            比例足时是否填白 true为填白，二维码是黑白色，这里调用时建议设为true
     * @return 新图片路径
     */
    public static String resizeImg(File f, int width, int height)
    {

        String newImgPath = "";

        try
        {
            // File f = new File(filePath);
            String filePath = f.getPath();
            String fileName = f.getName();
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            // 填充白色
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
            // 放缩后图片比例不一致会出现黑边按短边截取黑边
            int tempWidth = itemp.getWidth(null);
            int tempHeight = itemp.getHeight(null);
            g.drawImage(itemp, 0, 0, tempWidth, tempHeight, Color.white, null);

            g.dispose();
            itemp = image;

            String now = SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND_SHORT);
            if (filePath.contains(fileName))
            {
                newImgPath = filePath.replace(fileName, now + fileName);
            }

            File newImg = new File(newImgPath);
            ImageIO.write((BufferedImage) itemp, "png", newImg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return newImgPath;
    }

    /**
     * 解析二维码（QRCode）
     * 
     * @param input
     *            输入流
     * @return
     */
    public String decoderQRCode(InputStream input)
    {
        BufferedImage bufImg = null;
        String content = null;
        try
        {
            bufImg = ImageIO.read(input);
            QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (DecodingFailedException dfe)
        {
            dfe.printStackTrace();
        }
        return content;
    }

    /**
     * 解析二维码（QRCode）
     * 
     * @param imgPath
     *            图片路径
     * @return
     */
    public String decoderQRCode(String imgPath)
    {
        // QRCode 二维码图片的文件
        File imageFile = new File(imgPath);
        BufferedImage bufImg = null;
        String content = null;
        try
        {
            bufImg = ImageIO.read(imageFile);
            QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (DecodingFailedException dfe)
        {
            dfe.printStackTrace();
        }
        return content;
    }

    /**
     * 生成二维码(QRCode)图片
     * 
     * @param content
     *            存储内容
     * @param imgPath
     *            图片路径
     * @param imgType
     *            图片类型
     * @param size
     *            二维码尺寸
     */
    public void encoderQRCode(String content, String imgPath, String imgType, int size)
    {
        try
        {
            BufferedImage bufImg = this.qRCodeCommon(content, imgType, size);

            File imgFile = new File(imgPath);
            // 生成二维码QRCode图片
            ImageIO.write(bufImg, imgType, imgFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int getImgSize(int size)
    {
        return 67 + 12 * (size - 1);
    }

    /**
     * 生成二维码(QRCode)图片的公共方法
     * 
     * @param content
     *            存储内容
     * @param imgType
     *            图片类型
     * @param size
     *            二维码尺寸
     * @return
     */
    private BufferedImage qRCodeCommon(String content, String imgType, int size) throws Exception
    {
        BufferedImage bufImg = null;
        try
        {
            Qrcode qrcodeHandler = new Qrcode();
            // 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
            qrcodeHandler.setQrcodeErrorCorrect('H');
            qrcodeHandler.setQrcodeEncodeMode('B');
            // 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
            qrcodeHandler.setQrcodeVersion(size);
            // 获得内容的字节数组，设置编码格式
            byte[] contentBytes = content.getBytes("utf-8");
            // 图片尺寸
            int imgSize = this.getImgSize(size);
            bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D gs = bufImg.createGraphics();
            // 设置背景颜色
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, imgSize, imgSize);

            // 设定图像颜色> BLACK
            gs.setColor(Color.BLACK);
            // 设置偏移量，不设置可能导致解析出错
            int pixoff = 2;
            // 输出内容> 二维码
            if (contentBytes.length > 0 && contentBytes.length < 800)
            {
                boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
                for (int i = 0; i < codeOut.length; i++)
                {
                    for (int j = 0; j < codeOut.length; j++)
                    {
                        if (codeOut[j][i])
                        {
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                        }
                    }
                }
            }
            else
            {
                return null;
            }
            gs.dispose();
            bufImg.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bufImg;
    }
}
