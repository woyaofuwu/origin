
package com.asiainfo.veris.crm.order.web.person.badness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.config.ModuleCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AccessoryList extends PersonBasePage
{
    // 一级BOSS主机地址
    public static String ibossIP;

    public static String ibossUser;

    // protected static GlobalCfg common = GlobalCfg.;

    public static String ibossPassword;

    // 一级BOSS彩信文件上传目录
    private static String ibossUpDir;

    // 一级BOSS彩信文件下发目录
    public static String ibossDownDir;
    static
    {
        try
        {
            ibossIP = ModuleCfg.getProperty("mmsfile/ftp/iboss_server");
            ibossUser = ModuleCfg.getProperty("mmsfile/ftp/iboss_user");
            ibossPassword = ModuleCfg.getProperty("mmsfile/ftp/iboss_password");
            ibossUpDir = ModuleCfg.getProperty("mmsfile/ftp/iboss_updir");
            ibossDownDir = ModuleCfg.getProperty("mmsfile/ftp/iboss_downdir");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void checkFileExsist(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataOutput output = CSViewCall.callPage(this, "SS.AccessoryListSVC.checkFileExsist", data, null);

        if (output.getData() == null || output.getData().size() == 0)
        {
            // TODO:setAlertInfo("没有符合查询条件的【未完工工单】数据~");
        }
        IData idata = output.getData().getData(0);
        IData idatat = idata.getData("RET");
        setAjax(idatat);
    }

    public void queryAccessoryList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataOutput output = CSViewCall.callPage(this, "SS.AccessoryListSVC.queryAccessoryList", data, null);

        if (output.getData() == null || output.getData().size() == 0)
        {
            // TODO:setAlertInfo("没有符合查询条件的【未完工工单】数据~");
        }
        IData idata = output.getData().getData(0);

        IDataset idatas = idata.getDataset("RET");
        setInfos(idatas);

    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
