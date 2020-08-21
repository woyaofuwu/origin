package com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 电子协议单生成工具类
 *
 * @author ckh
 * @date 2018/10/12.
 */
public class ElecAgreementUtils
{

    private static final String TYPE_TEXT = "0";
    private static final String TYPE_TABLE = "1";
    private static final String TYPE_CHECKBOX = "2";
    private static final String TYPE_PIC = "3";
    private static final Rectangle RECT_NEW_PAGE = new Rectangle(0, 20, 559, 806);

    private static final String EAGREE_FTPSITE = "eosdata";
    private static final int POS_LENTH = 4;
    private static final int PIC_POS_LENTH = 2;

    private static final int DEFAULT_FONT_SIZE = 14;
    private static final int DEFAULT_COLS_NUM = 3;
    private static final int DEFAULT_ROWS_NUM = 4;
    private static final float DEFAULT_ALPHA = 0.3F;

    private static final int DEFAULT_CONTRACT_SEAL_HEIGHT = 100;
    private static final int DEFAULT_CONTRACT_SEAL_WIDTH = 150;

    private static BaseFont baseFont;

    static {
        try {
            baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            //初始化报错不影响系统
        }

    }

    /**
     * 构建pdf电子协议文档
     *
     * @param templateId pdf模板id
     * @param params     模板变量信息
     * @return {@link IData 生成文件信息}
     * @throws Exception 模板构建异常
     */
    public static IDataset buildTemplateToFile(String templateId, IDataset params, BizVisit visit) throws Exception
    {
        PdfReader reader = getTemplateStream(templateId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfStamper stamper = new PdfStamper(reader, outputStream);
        AcroFields form = stamper.getAcroFields();

        String agrementId = "";

        // 0- 查询出模板对应的配置表所有数据
        IData templateInfo = ElecAgrePdfDefQry.queryTemplateInfoById(templateId);
        if (DataUtils.isEmpty(templateInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "电子协议模板未定义，请检查模板配置！");
        }

        IDataset templateEleInfos = ElecAgrePdfDefQry.queryElementInfosById(templateId);

        // 1-开始处理属性
        for (int i = 0, sizeI = params.size(); i < sizeI; i++)
        {
            IData param = params.getData(i);
            if (DataUtils.isEmpty(param))
            {
                continue;
            }
            if(StringUtils.isNotBlank(param.getString("AGREEMENT_ID"))){
                agrementId = param.getString("AGREEMENT_ID");
            }

            String[] key = param.getNames();
            IDataset eleInfos = DataHelper.filter(templateEleInfos, "PDF_ELE_CODE=" + key[0]);
            if (DataUtils.isEmpty(eleInfos))
            {
                continue;
            }
            IData eleInfo = eleInfos.first();
            String eleType = eleInfo.getString("PDF_ELE_TYPE");
            // 1.1-处理表单数据
            if (TYPE_TEXT.equals(eleType) || TYPE_CHECKBOX.equals(eleType))
            {
                dealFormData(form, param.getString(key[0]), key[0],eleType);
            }

            // 1.2-处理表格数据
            else if (TYPE_TABLE.equals(eleType))
            {
                if (param.get(key[0]) instanceof IData)
                {
                    dealTableData(stamper, templateEleInfos, param.getData(key[0]), key[0]);
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, key[0], eleType, param.get(key[0]));
                }
            }

            // 1.3- 处理图像数据
            else if (TYPE_PIC.equals(eleType))
            {
                dealPicData(stamper, eleInfo, key[0], param.getString(key[0]));
            }
        }

        //加水印
        addWaterMark2PDF(reader,stamper,agrementId);

        //盖合同章
        addContractSeal(templateId,stamper,visit);

        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        return uploadToFtp(outputStream, templateInfo);
    }

    private static void addContractSeal(String templateId,PdfStamper stamper,BizVisit visit) throws Exception{
        String contractPositionStr = "";//StaticUtil.getStaticValue("CONTRACT_SEAL_POSITION",templateId);
        IDataset dataset = StaticUtil.getListDataSource(visit,"cen1","TD_S_STATIC","TYPE_ID","DATA_NAME",new String[]{"TYPE_ID","DATA_ID","VALID_FLAG"},new String[]{"CONTRACT_SEAL_POSITION",templateId,"0"});
        if(DataUtils.isNotEmpty(dataset)){
            contractPositionStr = dataset.first().getString("DATA_NAME");
        }
        if(StringUtils.isBlank(contractPositionStr)){
            return;
        }

        IData contractPositionData = new DataMap(contractPositionStr);

        String pageNum = contractPositionData.getString("PAGE_NUM");
        if (StringUtils.isEmpty(pageNum))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "电子协议合同章需要配置页面信息！");
        }
        PdfContentByte over = stamper.getOverContent(Integer.valueOf(pageNum));
        String position = contractPositionData.getString("POSITION");

        //获取合同章图片
        Image image = PaperlessCall.getContractSeal(visit);
        if(image == null){
            return;
        }
        if (StringUtils.isEmpty(position))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "电子协议合同章配置，POSITION为必填元素！");
        }
        String[] xyPos = position.split(",");
        if (xyPos.length != PIC_POS_LENTH)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "电子协议合同章配置，POSITION格式为\"x,y\"！");
        }
        image.setAbsolutePosition(Float.parseFloat(xyPos[0]), Float.parseFloat(xyPos[1]));
        image.scaleAbsolute(DEFAULT_CONTRACT_SEAL_WIDTH, DEFAULT_CONTRACT_SEAL_HEIGHT);
        over.addImage(image);
    }

    private static void addWaterMark2PDF(PdfReader reader,PdfStamper stamper,String agrementId) throws Exception{
        Image img = getImageInstance("logo/markLogo.png");
        int p = 20;
        img.scalePercent(p);
        String text = "合同编码："+agrementId;

        //字体大小
        String size = StaticUtil.getStaticValue("EPAPER_WATER_MAKER_FONTSIZE", "99");
        int sizeNum = 0;
        try {
            sizeNum = Integer.valueOf(size);
        }catch (NumberFormatException e){
            sizeNum = DEFAULT_FONT_SIZE;//默认字体为14
        }

        //透明度
        String alphaStr = StaticUtil.getStaticValue("EPAPER_PDF_WATER_OPACITY", "99");
        float alpha = 0;
        try {
            alpha = Float.valueOf(alphaStr);
        }catch (NumberFormatException e){
            alpha = DEFAULT_ALPHA;//默认透明度为0.3
        }

        //字体颜色
        String colorStr = StaticUtil.getStaticValue("EPAPER_WATER_MAKER_COLOR", "99");
        BaseColor fontColor = null;
        try{
            String[] colors = colorStr.split(",");
            fontColor = new BaseColor(Integer.valueOf(colors[0]),Integer.valueOf(colors[1]),Integer.valueOf(colors[2]));
        }catch (Exception e){
            fontColor = BaseColor.BLUE;
        }

        double al = ((double) 45 * 3.1415926535897931D) / 180D;
        float cos = (float) Math.cos(al);
        float sin = (float) Math.sin(al);

        img.setRotation(sin);
        img.setRotationDegrees(45);

        int first = 1;
        Rectangle re = reader.getPageSize(first);
        int high = (int)re.getHeight();
        int with = (int)re.getWidth();
        int pageNum = reader.getNumberOfPages();
        for(int i = 1;i<=pageNum;i++){
            PdfContentByte over = stamper.getOverContent(i);
            over.saveState();
            PdfGState pdfGState = new PdfGState();
            pdfGState.setFillOpacity(alpha);
            over.setGState(pdfGState);
            over.beginText();
            over.setColorFill(fontColor);
            over.setFontAndSize(baseFont, sizeNum);
            int cols =  with/DEFAULT_COLS_NUM;
            int rows =  high/DEFAULT_ROWS_NUM;
            for(int n = 0;n<DEFAULT_COLS_NUM;n++){
                for(int m=0;m<DEFAULT_ROWS_NUM;m++){
                    img.setAbsolutePosition(cols*n+20,rows*m);
                    over.addImage(img);
                    over.setTextMatrix(cos, sin, -sin, cos, cols*n+20, rows*m+50);
                    over.showText(text);
                }
            }

            over.endText();
            over.restoreState();
        }

    }

    private static void dealPicData(PdfStamper stamper, IData eleInfo, String attrCode, String src) throws Exception
    {
        String eleConfigStr = eleInfo.getString("PDF_ELE_STYLE");
        if (StringUtils.isEmpty(eleConfigStr))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  attrCode + "：电子协议元素配置错误或数据错误，图像类型元素需要配置图像样式！");
        }
        IData eleStyle = new DataMap(eleConfigStr);
        String pageNum = eleStyle.getString("PAGE_NUM");
        if (StringUtils.isEmpty(pageNum))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  attrCode + "：电子协议元素配置错误或数据错误，图像类型元素需要配置页面信息！");
        }
        PdfContentByte over = stamper.getOverContent(Integer.valueOf(pageNum));
        String position = eleStyle.getString("POSITION");

        Image image = getImageInstance(src);
        if (StringUtils.isEmpty(position))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  attrCode + "图像配置，POSITION为必填元素！");
        }
        String[] xyPos = position.split(",");
        if (xyPos.length != PIC_POS_LENTH)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  attrCode + "图像配置，POSITION格式为\"x,y\"！");
        }
        image.setAbsolutePosition(Float.parseFloat(xyPos[0]), Float.parseFloat(xyPos[1]));
        image.scaleAbsolute(image.getWidth() * 0.55f, image.getHeight() * 0.55f);
        over.addImage(image);
    }

    /**
     * 上传协议到FTP服务器
     *
     * @param outputStream 文件输出流
     * @param templateInfo {@link IData 模板定义信息}
     * @throws Exception 上传FTP异常
     */
    private static IDataset uploadToFtp(ByteArrayOutputStream outputStream, IData templateInfo) throws Exception
    {
        // 2- 准备文件信息
        String realName = templateInfo.getString("AGRE_PDF_NAME");
        ByteArrayInputStream baInStream = new ByteArrayInputStream(outputStream.toByteArray());
        // 4- 远程写入FTP
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(CSBizBean.getVisit());
        String fileId = ImpExpUtil.getImpExpManager().getFileAction().upload(baInStream, EAGREE_FTPSITE, "elecAgreement",
                realName + ".pdf",false);

        IData retData = new DataMap();
        retData.put("FILE_ID", fileId);
        retData.put("FILE_NAME", realName + ".pdf");
        IDataset results = new DatasetList();
        results.add(retData);

        return results;
    }

    /**
     * 制作表格pdf部分
     *
     * @param stamper          {@linkplain PdfStamper}
     * @param templateEleInfos 模板元素信息
     * @param value            表格数据
     * @param attrCode         表格变量名
     * @throws Exception 表格处理异常
     */
    private static void dealTableData(PdfStamper stamper, IDataset templateEleInfos, IData value, String attrCode)
            throws Exception
    {

        IData eleInfo = DataHelper.filter(templateEleInfos, "PDF_ELE_CODE=" + attrCode).first();

        String eleConfigStr = eleInfo.getString("PDF_ELE_STYLE");
        if (StringUtils.isEmpty(eleConfigStr))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"电子协议元素配置错误或数据错误，表格类型元素需要在配置表格样式！");
        }
        IData eleConfig = new DataMap(eleConfigStr);
        int pageNum = eleConfig.getInt("PAGE_NUM", 1);
        boolean ifNewPage = eleConfig.getBoolean("IF_NEW_PAGE", false);
        if(ifNewPage){
            stamper.insertPage(pageNum, PageSize.A4);
        }

        ColumnText columnText = new ColumnText(stamper.getOverContent(pageNum));
        String position = eleConfig.getString("POSITION");
        if (StringUtils.isEmpty(position))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"电子协议元素配置错误，表格类型元素需要配置表格属性POSITION！");
        }
        String[] xyPos = position.split(",");
        if (xyPos.length != POS_LENTH)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "POSITION,且为4个用\",\"分开的数字");
        }
        Rectangle rectangle1 = new Rectangle(Float.parseFloat(xyPos[0]), Float.parseFloat(xyPos[1]),
                Float.parseFloat(xyPos[2]), Float.parseFloat(xyPos[3]));
        columnText.setSimpleColumn(rectangle1);

        IDataset lineInfos = value.getDataset("LINE_INFOS");

        if (IDataUtil.isNotEmpty(lineInfos))
        {
            PdfPTable table = makeTableInfo(eleConfig, templateEleInfos, attrCode, lineInfos);
            columnText.addElement(table);
        }
        IDataset otherTable = value.getDataset("OTHER_TABLE");

        if (IDataUtil.isNotEmpty(otherTable))
        {
            PdfPTable table = makeOtherTable(eleConfig, otherTable);
            columnText.addElement(table);
        }

        int status = columnText.go();
        // 当还有元素没有处理的时候，开启新页继续画表格
        while (ColumnText.hasMoreText(status))
        {
            status = triggerNewPage(stamper, PageSize.A4, columnText, RECT_NEW_PAGE, ++pageNum);
        }
        stamper.setFormFlattening(true);
    }

    /**
     * 启用新页继续构建表格
     *
     * @param stamper     {@linkplain PdfStamper}
     * @param pageSize    当前文档页面大小
     * @param columnText  表格列文字对象
     * @param rectNewPage 新页面对象块
     * @param pageCount   页数
     * @return 当前数据状态
     * @throws DocumentException 文档处理异常
     */
    private static int triggerNewPage(PdfStamper stamper, Rectangle pageSize, ColumnText columnText,
                                      Rectangle rectNewPage,
                                      int pageCount) throws DocumentException
    {
        stamper.insertPage(pageCount, pageSize);
        PdfContentByte canvas = stamper.getOverContent(pageCount);
        columnText.setCanvas(canvas);
        columnText.setSimpleColumn(rectNewPage);

        return columnText.go();
    }

    /**
     * 构造表格对象
     *
     * @param eleConfig        表格对象配置
     * @param templateEleInfos 模板元素数据集
     * @param attrCode         表格变量名
     * @param rowInfos         行信息
     * @return {@linkplain PdfPTable}
     * @throws Exception 构建表格异常
     */
    private static PdfPTable makeTableInfo(IData eleConfig, IDataset templateEleInfos, String attrCode,
                                           IDataset rowInfos) throws Exception
    {
        int colNum = eleConfig.getInt("COL_NUM");
        if (colNum == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "电子协议元素配置错误，表格类型元素需要配置表格属性COL_NUM！ ");
        }
        PdfPTable table = new PdfPTable(colNum);

        // 查询表格配置
        IDataset tableInfos = ElecAgrePdfDefQry.queryTableInfoByIdAndSupEleCode(
                templateEleInfos.first().getString("AGRE_PDF_ID"), attrCode);
        if (DataUtils.isEmpty(tableInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "电子协议元素配置错误，表格类型元素需要配置表格列元素信息！");

        }

        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(bfChinese, 9, Font.NORMAL, BaseColor.BLACK);
        //设置表头信息
        for (int i = 0; i < tableInfos.size(); i++)
        {
            IData tableInfo = tableInfos.getData(i);
            PdfPCell cell = new PdfPCell(new Paragraph(tableInfo.getString("PDF_ELE_NAME"), font));
            table.addCell(cell);
        }
        table.setHeaderRows(1);

        // 设置表单信息
        for (int i = 0, sizeI = rowInfos.size(); i < sizeI; i++)
        {
            IData rowInfo = rowInfos.getData(i);
            for (int j = 0; j < tableInfos.size(); j++)
            {
                IData tableInfo = tableInfos.getData(j);
                PdfPCell cell = new PdfPCell(
                        new Paragraph(rowInfo.getString(tableInfo.getString("PDF_ELE_CODE")), font));
                table.addCell(cell);
            }
        }

        return table;
    }

    private static PdfPTable makeOtherTable(IData eleConfig,IDataset otherTable) throws Exception {
        int colNum = eleConfig.getInt("OTHER_TABLE_COL_NUM");
        int widthPercentage = eleConfig.getInt("OTHER_TABLE_WIDTHPERCENTAGE");
        int spacingBefore = eleConfig.getInt("OTHER_TABLE_SPACINGBEFORE");
        int spacingAfter = eleConfig.getInt("OTHER_TABLE_SPACINGAFTER");
        int border = eleConfig.getInt("BORDER");

        if (colNum == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "电子协议元素配置错误，表格类型元素需要配置表格属性COL_NUM！ ");
        }
        PdfPTable table = new PdfPTable(colNum);
        if(widthPercentage != 0 ){
            table.setWidthPercentage(widthPercentage);
        }
        if(spacingBefore != 0 ){
            table.setWidthPercentage(spacingBefore);
        }
        if(spacingAfter != 0 ){
            table.setWidthPercentage(spacingAfter);
        }

        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(bfChinese, 9, Font.NORMAL, BaseColor.BLACK);
        table.setHeaderRows(1);

        // 设置表单信息
        for (int i = 0, sizeI = otherTable.size(); i < sizeI; i++)
        {
            IData rowInfo = otherTable.getData(i);
            PdfPCell cell = new PdfPCell(
                    new Paragraph(rowInfo.getString("VALUE",""), font));
            cell.setBorder(border);
            cell.setColspan(rowInfo.getInt("COLSPAN",1));
            table.addCell(cell);
        }

        return table;
    }

    /**
     * 构建常规表单信息
     *
     * @param form     pdf表单
     * @param value    表单变量值
     * @param attrCode 表单变量名
     * @throws Exception 构建表单异常
     */
    private static void dealFormData(AcroFields form, String value, String attrCode,String eleType)
            throws Exception
    {
        // 值为空，直接返回
        if (StringUtils.isEmpty(value))
        {
            if(TYPE_TEXT.equals(eleType)){
                value = "\\";
            }else{
                return;
            }
        }

        form.setField(attrCode, value);
    }

    /**
     * 取得{@linkplain PdfReader}数据流对象
     *
     * @param templateId 模板id
     * @return {@linkplain PdfReader}
     * @throws Exception 取数据异常
     */
    private static PdfReader getTemplateStream(String templateId) throws Exception
    {
        return new PdfReader("elecpdftemplate/" + templateId + ".pdf");
    }

    /**
     * 取得{@linkplain Image}图像对象
     *
     * @param src 图像路径
     * @return @{@linkplain Image}
     * @throws IOException         io异常
     * @throws BadElementException 元素类型错误
     */
    private static Image getImageInstance(String src) throws Exception
    {
        InputStream is = ElecAgreementUtils.class.getClassLoader().getResourceAsStream("elecpdftemplate/" + src);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int length = 0;

        byte[] b = new byte[4096];

        while ((length = is.read(b)) != -1)
        {
            baos.write(b, 0, length);
        }

        return Image.getInstance(baos.toByteArray());
    }

    public static IDataset uploadImageToFtp(IData image){
        String imgStr = image.getString("base64Bitmap");
        String archivesName = image.getString("ARCHIVES_NAME",String.valueOf(System.currentTimeMillis()));
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return new DatasetList();
        BASE64Decoder decoder = new BASE64Decoder();
        try{
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i){
                if(b[i]<0){//调整异常数据
                    b[i]+=256;
                }
            }
            ByteArrayInputStream baInStream = new ByteArrayInputStream(b);
            // 4- 远程写入FTP
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(CSBizBean.getVisit());
            String fileId = ImpExpUtil.getImpExpManager().getFileAction().upload(baInStream, EAGREE_FTPSITE, "upload/import",
                    archivesName + ".jpg",true);

            IData retData = new DataMap();
            retData.put("FILE_ID", fileId);
            retData.put("FILE_NAME", archivesName + ".jpg");
            IDataset results = new DatasetList();
            results.add(retData);
            return results;
        }catch (Exception e){
            return new DatasetList();
        }
    }

    public static void checkParam(IData param,String key) throws Exception{
        String value = param.getString(key,"");
        if(StringUtils.isBlank(value)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"未获取到参数【"+key+"】！");
        }
    }

}
