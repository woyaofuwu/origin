
package com.asiainfo.veris.crm.order.web.person.hintinfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 */
public abstract class DiscntManage extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(DiscntManage.class);

    public static final String msgSuc = "S";

    public static final String msgFail = "F";

    public static final String msgKey = "MSG";

    public static final String msgTypeKey = "MSG_TYPE";

    public static final String msgNameKey = "NAME";

    public void getHintOperation(IData data) throws Exception
    {
        Boolean Flag = false;
        if (IDataUtil.isNotEmpty(data))
        {
            IDataset infos = CSViewCall.call(this, "SS.DiscntManageSVC.getHintOperation", data);

            if (IDataUtil.isNotEmpty(infos))
            {
                Flag = true;
            }
        }

        setClicked(Flag);
    }

    public String getURL4Excel(IDataset[] tagData, List configData, String fileName) throws Exception
    {
        // 生成失败的文件
        File File = ImpExpUtil.writeExcelData(tagData, "/", null, configData, ImpExpUtil.excel_03, 0, 0);
        // 将导入失败的文件上传
        String FileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(File), "personserv", "upload/import", fileName);
        // 生成文件下载的URL
        String Url = ImpExpUtil.getDownloadPath(FileId, fileName);

        return Url;
    }

    public void importDiscnt(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.DiscntManageSVC.dealDiscntImport", data);

        if (!dataset.isEmpty() && dataset.size() > 0)
        {
            String errorUrl = "";
            // 如果实名制预受理时 没有填写相应的资料 则用 客户原来的资料代替
            if (dataset.getData(0).getString("FailList") != null && dataset.getData(0).getString("FailList") != "")
            {
                String fileName = "优惠描述修改信息导入错误数据(" + SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + ").xls";
                IDataset[] failDataset = new IDataset[]
                { dataset.getData(0).getDataset("FailList") };
                if (null != failDataset && failDataset.length > 0)
                {
                    // 导入模板
                    List configData = ExcelConfig.getSheets("export/hintinfo/DiscntErrorExport.xml");
                    errorUrl = getURL4Excel(failDataset, configData, fileName);
                }
                setAjaxMsg(msgFail, errorUrl, "错误优惠描述信息(" + SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + ")");
            }
            else
            {
                setAjaxMsg(msgSuc, "优惠描述修改信息导入成功!", null);
            }

        }
        else
        {
            setAjaxMsg(msgSuc, "优惠描述修改信息导入成功!", null);
        }
    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // 根据用户权限设置操作按钮是否可见
        getHintOperation(data);

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        IDataset output = CSViewCall.call(this, "SS.DiscntManageSVC.upDiscntInfo", data);

        setAjax(output);
    }

    public void queryDiscntInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        IDataOutput output = CSViewCall.callPage(this, "SS.DiscntManageSVC.queryDiscntInfo", data, getPagination());

        // 根据用户权限设置操作按钮是否可见
        getHintOperation(data);

        setQueryDiscntInfoCount(output.getDataCount());

        setInfos(output.getData());
    }

    public void queryUpDiscntInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset output = CSViewCall.call(this, "SS.DiscntManageSVC.queryDiscntInfo", data);

        if (IDataUtil.isNotEmpty(output))
        {
            if (IDataUtil.isNotEmpty(output.getData(0)))
            {
                setInfo(output.getData(0));
            }

        }

    }

    protected void setAjaxMsg(String msgType, String msgContent, String name)
    {
        IData ajax = new DataMap();
        ajax.put(msgKey, msgContent);
        ajax.put(msgTypeKey, msgType);
        ajax.put(msgNameKey, name);
        setAjax(ajax);
    }

    public abstract void setClicked(boolean clicked);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setQueryDiscntInfoCount(long count);

}
