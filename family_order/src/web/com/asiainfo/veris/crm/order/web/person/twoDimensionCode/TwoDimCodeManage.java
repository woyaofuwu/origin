
package com.asiainfo.veris.crm.order.web.person.twoDimensionCode;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 二维码管理
 * 
 * @author dengshu
 * @date 2014年5月21日-下午4:53:25
 */
public abstract class TwoDimCodeManage extends PersonBasePage
{
    /**
     * 删除二维码信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void deleteTwoDimCode(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData();

        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.TwoDimCodeManageSVC.deleteTwoDimCode", inputData);

        if (!IDataUtil.isEmpty(result))
        {
            IData data = result.getData(0);
            boolean flag = data.getBoolean("SUCCESS");

            if (!flag)
            {
                this.setAjax("ALERT_INFO", "删除失败！请联系管理员。");
            }
        }

    }

    public void downloadTwoDimCode(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        String fileId = "106743754604";// input.getString("FILE_ID","");
        String fileName = input.getString("FILE_NAME", "");
        // ftp文件管理器
        FtpFileAction ftpFile = new FtpFileAction();
        // visit参数，去掉FtpFileAction 报错
        ftpFile.setVisit(this.getVisit());
        // 临时存放二维码图片路径
        // String imgPath = TwoDimCodeAdd.class.getResource("/").getPath()+"../../upload/twodimcode_temp/";
        // String fullPath = imgPath+fileName;
        // 加入中间图片
        if (null != fileId && fileId.length() > 0)
        {
            // 从服务器获取上传的logo图片
            File TwoDimImgFile = ftpFile.download(fileId);
            // 创建输出流
            BufferedImage src = ImageIO.read(TwoDimImgFile);
            // ImageIO.write(src, "png", new File(fullPath));
            ImageIO.write(src, "png", cycle.getRequestContext().getResponse().getOutputStream());
        }
    }

    /**
     * 查询二维码
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryTwoDimCode(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        // 模糊查询名称
        String barcodeName = inputData.getString("BARCODE_NAME", "");
        if (barcodeName.length() > 0)
        {
            inputData.put("BARCODE_NAME", "%" + inputData.getString("BARCODE_NAME") + "%");
        }
        // 结束日期加23点
        String end_date = inputData.getString("END_DATE", "");
        if (end_date.length() > 0)
        {
            inputData.put("END_DATE", end_date + SysDateMgr.END_DATE);
        }
        // 开始日期加00点
        String start_date = inputData.getString("START_DATE", "");
        if (start_date.length() > 0)
        {
            inputData.put("START_DATE", start_date + SysDateMgr.START_DATE_FOREVER);
        }
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.TwoDimCodeManageSVC.queryTwoDimCode", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        for(int i=0,size=dataset.size();i<size;i++){
        	IData tempInfo=dataset.getData(i);
        	String fileId=tempInfo.getString("FILE_ID");
        	String url=ImpExpUtil.getDownloadPath(fileId, pageutil.getFtpFile(fileId).getString("FILE_NAME"));
        	tempInfo.put("URL",url);
        }
        setPageCount(result.getDataCount());
        // 设置页面返回数据
        setInfos(dataset);
    }

    // 查询条件
    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    // 表格信息
    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

}
