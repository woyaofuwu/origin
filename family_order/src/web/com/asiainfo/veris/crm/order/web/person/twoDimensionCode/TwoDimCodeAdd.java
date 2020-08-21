
package com.asiainfo.veris.crm.order.web.person.twoDimensionCode;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @author dengshu
 * @date 2014年5月16日-下午4:25:09
 */
public abstract class TwoDimCodeAdd extends PersonBasePage
{
    // 文件名分隔符
    private static final String FILE_NAME_SPLIT = "_";

    // 图片文件后缀
    private static final String FILE_NAME_SUFFIX = "png";

    private static final Logger logger = Logger.getLogger(TwoDimCodeAdd.class);

    /**
     * 生成二维码初始化
     * 
     * @param cycle
     */
    public void addInit(IRequestCycle cycle) throws Exception
    {
        IData initModeData = new DataMap();
        IDataset initModeSet = new DatasetList();

        initModeData.put("VALUE", "0");
        initModeData.put("TEXT", "普通营业厅");
        initModeSet.add(initModeData);

        this.setInmodeinfo(initModeSet);
    }

    /**
     * 新增或修改补充插入元素
     * 
     * @param input
     * @param createFlag
     */
    private void addParam(IData input, boolean createFlag) throws Exception
    {
        input.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
        input.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
        input.put("UPDATE_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        if (createFlag)
        {

            String barcodeId = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssS").substring(1);
            input.put("BARCODE_ID", barcodeId);
            // 待审批
            input.put("STATE", "0");
            /*
             * 模型删去该部分内容 input.put("CREATE_DEPART_ID", this.getVisit().getDepartId()); input.put("CREATE_STAFF_ID",
             * this.getVisit().getStaffId()); input.put("CREATE_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
             */
        }

    }

    /**
     * 审批二维码
     * 
     * @param cycle
     */
    public void approvalTwoDimCode(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        // 添加时间等信息
        this.addParam(input, false);
        IDataset result = CSViewCall.call(this, "SS.TwoDimCodeManageSVC.approvalTwoDimCode", input);
        String msg = "";

        if (!IDataUtil.isEmpty(result))
        {
            IData data = result.getData(0);
            if (data.getBoolean("SUCCESS"))
            {
                msg = "审批二维码生成成功！";
            }
            else
            {
                msg = "审批二维码生成失败！";
            }
        }
        else
        {
            msg = "审批二维码生成失败！";
        }
        this.setAjax("ALERT_INFO", msg);
    }

    /**
     * 处理回填数据文件名
     * 
     * @param baseData
     */
    private void dealFileName(IData baseData) throws Exception
    {
        if (baseData == null)
        {
            CSViewException.apperr(ElementException.CRM_ELEMENT_500);
        }
        // 二维码文件名
        String fileName = baseData.getString("FILE_NAME", "");
        String middleFileId = "";

        if (fileName.length() > 1)
        {
            // 分割文件名 例1： 二维码图片_.png (无中间图片)，例2:二维码图片_106743754351.png
            String[] fileNameArray = fileName.split(FILE_NAME_SPLIT);
            String suf = "." + FILE_NAME_SUFFIX;

            if (!suf.equals(fileNameArray[1]))
            {
                // 中间图片ID
                middleFileId = fileNameArray[1].substring(0, fileNameArray[1].length() - suf.length());
            }
        }
        // 二维码ID
        baseData.put("TWO_DIM_FILE_ID", baseData.getString("FILE_ID"));
        // 中间图片ID
        baseData.put("FILE_ID", middleFileId);
    }

    /**
     * @param fileId
     *            上传文件图片ID
     * @param barcodeId
     *            待编码的二维码编码
     * @return 返回处理好的二维码图片编码
     * @throws Exception
     */
    private String generatePic(String fileId, String barcodeId,String barcodeName) throws Exception
    {
        // ClassLoader.getSystemResource(name).
        // 临时存放二维码图片路径
        String imgPath = TwoDimCodeAdd.class.getResource("/").getPath() + "twodimcode_temp/";

        if (System.getProperty("os.name").contains("Windows"))
        {
            imgPath = imgPath.substring(1);
        }

        File path = new File(imgPath);
        if (!path.exists())
        {
            path.mkdir();
        }

        // 二维码尺寸 1-40
        int size = 10;
        String imgName = "二维码图片" + FILE_NAME_SPLIT + barcodeName + "." + FILE_NAME_SUFFIX;
        String fullPath = imgPath + imgName;

        if(logger.isInfoEnabled()) logger.info(fullPath);

        TwoDimensionCode handler = new TwoDimensionCode();
        // ftp文件管理器
        FtpFileAction ftpFile = new FtpFileAction();
        // visit参数，去掉FtpFileAction 报错
        ftpFile.setVisit(this.getVisit());
        // String encoderContent = "姓名：周小喻  \n小名：女汉子  \n技能：骂街、撒泼、赖地、砍价  \n必杀技：哇哇哭   \n联系方式：10068" ;//
        // "Myblog [ http://sjsky.iteye.com ]" "\nEMail [ sjsky007@gmail.com ]";
        // 正常生成二维码
        handler.encoderQRCode(barcodeId, fullPath, FILE_NAME_SUFFIX, size);

        // 加入中间图片
        if (null != fileId && fileId.length() > 0)
        {

            // 从服务器获取上传的logo图片
            File middleFile = ftpFile.download(fileId);

            // 中间logo尺寸
            int middleSize = (67 + 12 * (size - 1)) / 4;
            
            // 缩放水印图片,为保证二维码的读取正确，图片不超过二维码图片的五分之一，这里设为六分之一
            String waterImgPath = handler.resizeImg(middleFile, middleSize, middleSize);

            // //图片合成，返回的是生成好的二维码图片的所在路径
            fullPath = handler.addImageWater(fullPath, waterImgPath, "thatway");
        }
        // 上传服务器后返回文件ID
        String retFileId = ftpFile.upload((new FileInputStream(fullPath)), "personserv", "twoDimCode", imgName);

        // 删除服务器中中间图片文件；避免生成失败重新提交缺少文件，不删除中间图片。
        // ftpFile.delete(fileId);
        // 删除服务器本机临时文件
        new File(fullPath).delete();

        return retFileId;

    }

    /**
     * 生成二维码初始化
     * 
     * @param cycle
     */
    public void modifyInit(IRequestCycle cycle) throws Exception
    {
        // 页面初始化信息
        this.addInit(cycle);
        IData input = this.getData();
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.TwoDimCodeManageSVC.queryTwoDimCodeDetail", input);

        if (!IDataUtil.isEmpty(result))
        {

            IData resultData = result.getData(0);
            // 主表信息
            IDataset baseInfo = resultData.getDataset("baseInfo");
            // 明细
            IDataset detailInfo = resultData.getDataset("detailInfo");

            IData baseData = baseInfo.getData(0);
            // 修改文件名信息
            this.dealFileName(baseData);

            // 数据回填
            this.setBase(baseData);
            this.setInfos(detailInfo);
        }
        else
        {
            CSViewException.apperr(ElementException.CRM_ELEMENT_500);
        }
    }

    /**
     * 保存二维码信息 修改、新增 为防止前台修改中间图片，在修改提交时候，将二维码图片重新生成再上传
     * 
     * @param cycle
     */
    public void saveTwoDimCode(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        // 获取表格数据
        String encodestr = input.getString("edit_table");
        // 新增标示
        boolean createFlag = true;

        if (input.getString("BARCODE_ID", "").length() > 1)
        {
            // 修改数据
            createFlag = false;
        }
        // 新增数据检验元素是否为空，修改数据元素修改可以为空
        if (createFlag && (encodestr == null || encodestr.length() < 1))
        {
            return;
        }

        // 添加时间等信息
        this.addParam(input, createFlag);
        input.put("createFlag", createFlag);
        // json 格式 edit_table 生成数据集
        IDataset submitInfoSet = new DatasetList(encodestr);
        // 覆盖元素串数据
        input.put("edit_table", submitInfoSet);
        // 生成二维码并上传到服务器
        String file_id = this.generatePic(input.getString("FILE_FIELD1"), input.getString("BARCODE_ID"),input.getString("BARCODE_NAME"));
        input.put("FILE_ID", file_id);
        IDataset result = CSViewCall.call(this, "SS.TwoDimCodeManageSVC.saveDimCode", input);
        String msg = "";

        if (!IDataUtil.isEmpty(result))
        {
            IData data = result.getData(0);
            if (data.getBoolean("SUCCESS"))
            {
                msg = "二维码生成成功！";
            }
            else
            {
                msg = "二维码生成失败！";
            }
        }
        else
        {
            msg = "二维码生成失败！";
        }
        this.setAjax("ALERT_INFO", msg);
    }
    
    /**
     * 生成二维码初始化
     * 
     * @param cycle
     */
    public void imgInit(IRequestCycle cycle) throws Exception
    {
        // 页面初始化信息
        this.addInit(cycle);
        IData input = this.getData();
        String fileId = input.getString("FILE_ID");
        String src="attach?fileId="+fileId+"&action=show&realName="+pageutil.getFtpFile(fileId).getString("FILE_NAME");
        IData baseData = new DataMap();
        baseData.put("SRC", src);
        // 数据回填
        this.setBase(baseData);
    }

    // 基本信息
    public abstract void setBase(IData cond);

    public abstract void setInfo(IData info);

    // 表格信息
    public abstract void setInfos(IDataset infos);

    // 渠道
    public abstract void setInmodeinfo(IDataset inmodeinfo);
}
