
package com.asiainfo.veris.crm.order.web.person.userpcc;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户动态策略计费控制信息导入
 */
public abstract class UserPccImport extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(UserPccImport.class);

    public static final String msgSuc = "S";

    public static final String msgFail = "F";

    public static final String msgKey = "MSG";

    public static final String msgTypeKey = "MSG_TYPE";

    public static final String msgNameKey = "NAME";

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

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     *             zhuyu 2014-3-18
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.UserPccImportSVC.dealPccImport", data);

        if (!dataset.isEmpty() && dataset.size() > 0)
        {
            String errorUrl = "";
            // 如果实名制预受理时 没有填写相应的资料 则用 客户原来的资料代替
            if (dataset.getData(0).getString("FailList") != null && dataset.getData(0).getString("FailList") != "")
            {
                String fileName = "用户动态策略计费控制信息导入错误数据(" + SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + ").xls";
                IDataset[] failDataset = new IDataset[]
                { dataset.getData(0).getDataset("FailList") };
                if (null != failDataset && failDataset.length > 0)
                {
                    // 导入模板
                    List configData = ExcelConfig.getSheets("export/UserPccExport.xml");
                    errorUrl = getURL4Excel(failDataset, configData, fileName);
                }
                setAjaxMsg(msgFail, errorUrl, "错误用户动态信息(" + SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + ")");
            }
            else
            {
                setAjaxMsg(msgSuc, "用户动态策略计费控制信息导入成功!", null);
            }

        }
        else
        {
            setAjaxMsg(msgSuc, "用户动态策略计费控制信息导入成功!", null);
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

}
