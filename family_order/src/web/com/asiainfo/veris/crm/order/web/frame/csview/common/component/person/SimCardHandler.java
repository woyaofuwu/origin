
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class SimCardHandler extends BizHttpHandler
{
    /**
     * 写卡后动作 接收前台自定义服务
     * 
     * @throws Exception
     */
    public void afterWriteCard() throws Exception
    {
        IData data = getData();
        String svcName = data.getString("AFTER_WRITE_SVC", "CS.WriteCardSVC.afterWriteCard");
        IDataset datas = CSViewCall.call(this, svcName, data);
        setAjax(datas.getData(0));
    }

    public void beforeCheckSimcard() throws Exception
    {
        IData data = getData();
        IDataset datas = CSViewCall.call(this, "CS.WriteCardSVC.beforeCheckSimcard", data);
        setAjax(datas);
    }

    /**
     * 写卡前动作 接收前台自定义服务
     * 
     * @throws Exception
     */
    public void beforeWriteCard() throws Exception
    {
        IData data = getData();
        String svcName = data.getString("BEFORE_WRITE_SVC", "CS.WriteCardSVC.beforeWriteCard");
        IDataset datas = CSViewCall.call(this, svcName, data);
        setAjax(datas.getData(0));
    }

    /**
     * 校验白卡
     * 
     * @throws Exception
     */
    public void checkEmptyCard() throws Exception
    {
        IData data = getData();
        IDataset datas = CSViewCall.call(this, "CS.SimCardCheckSVC.checkEmptyCard", data);
        setAjax(datas.getData(0));
    }

    /**
     * 校验SIM卡
     * 
     * @throws Exception
     */
    public void checkSimCard() throws Exception
    {
        IData data = getData();
        IDataset datas = CSViewCall.call(this, "CS.SimCardCheckSVC.checkSimCard", data);
        setAjax(datas.getData(0));
    }

    /**
     * 写卡后确认 接收前台自定义服务
     * 
     * @throws Exception
     */
    public void checkWriteCard() throws Exception
    {
        IData data = getData();
        String svcName = data.getString("CHECK_WRITE_SVC", "CS.WriteCardSVC.checkWriteCard");
        IDataset datas = CSViewCall.call(this, svcName, data);
        setAjax(datas.getData(0));
    }

    /**
     * 根据ICCID获取资源对应卡记录，主要读卡以后返回相关数据【比如预制卡的IMSI等等，非预制卡可以直接读取】
     * 
     * @throws Exception
     *             public void getResInfoByIccid() throws Exception { IData data = getData(); IDataset datas =
     *             CSViewCall.call(this, "CS.WriteCardSVC.getResInfoByIccid", data); setAjax(datas.getData(0)); }
     */
    /**
     * 确认是否为预制卡，即是否为新卡
     * 
     * @throws Exception
     */
    public void getNewCardInfo() throws Exception
    {
        IData data = getData();
        IDataset datas = CSViewCall.call(this, "CS.WriteCardSVC.getNewCardInfo", data);
        setAjax(datas.getData(0));
    }

    /**
     * 获取控件版本
     * 
     * @throws Exception
     */
    public void getOcxVersion() throws Exception
    {
        IData data = getData();
        IDataset datas = CSViewCall.call(this, "CS.WriteCardSVC.getOcxVersion", data);
        setAjax(datas.getData(0));
    }

    /**
     * 获取远程写卡控件下载URL，目前没使用
     * 
     * @throws Exception
     */
    public void getRemoteWriteCardUrl() throws Exception
    {
        IData data = getData();
        IDataset datas = CSViewCall.call(this, "CS.WriteCardSVC.getRemoteWriteCardUrl", data);
        setAjax(datas.getData(0));
    }

    /**
     * 选占SIM卡
     * 
     * @throws Exception
     */
    public void preOccupySimCard() throws Exception
    {
        IData data = getData();
        IDataset datas = CSViewCall.call(this, "CS.SimCardCheckSVC.preOccupySimCard", data);
        setAjax(datas.getData(0));
    }
}
