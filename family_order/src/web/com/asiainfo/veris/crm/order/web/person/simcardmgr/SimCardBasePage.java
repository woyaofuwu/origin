
package com.asiainfo.veris.crm.order.web.person.simcardmgr;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public class SimCardBasePage extends PersonBasePage
{
    protected static Logger log = Logger.getLogger(SimCardBasePage.class);

    /**
     * 写卡后处理, 根据写卡返回值更新白卡及SIM个性化资料状态
     * 
     * @param IRequestCycle
     * @throws Exception
     */
    public void afterWriteCard(IRequestCycle cycle) throws Exception
    {
        IData data1 = getData();
        data1.put("ROPER_TYPE", "afterWriteCard");
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.remoteOper", data1);
        setAjax(output);
    }

    /**
     * 写卡
     * 
     * @param cycle
     * @throws Exception
     */
    public void beforeWriteCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("ROPER_TYPE", "beforeWriteCard");
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.remoteOper", data);
        setAjax(output);
    }

    /**
     * 为SIM再利用作校验并准备参数（远程写卡、开户、复机等其它业务皆调用）
     * 
     * @param cycle
     * @throws Exception
     */
    public void check4ReuseCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("ROPER_TYPE", "check4ReuseCard");
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.remoteOper", data);
        setAjax(output);
    }

    /**
     * 读卡时校验卡类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkCardType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("ROPER_TYPE", "checkCardType");
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.remoteOper", data);
        setAjax(output);
    }

    /**
     * 校验控件版本
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkVersion(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("ROPER_TYPE", "checkVersion");
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.remoteOper", data);
        setAjax(output);
    }

    public void ckbefore(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.ChangeCardSVC.ckbefore", data);
        setAjax(dataset);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    }

    /**
     * 读卡操作
     * 
     * @param IRequestCycle
     * @throws Exception
     */
    public void readCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("ROPER_TYPE", "readCard");
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.remoteOper", data);
        setAjax(output);
    }

    /**
     * 资源释放
     * 
     * @param cycle
     * @throws Exception
     */
    public void releaseResource(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("ROPER_TYPE", "releaseResource");
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.remoteOper", data);
        setAjax(output);
    }

    /**
     * 校验新sim卡信息
     * 
     * @param IRequestCycle
     * @throws Exception
     */
    public void verifySimCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();// ctx.getData();
        IDataset output = CSViewCall.call(this, "SS.ChangeCardSVC.verifySimCard", data);
        data.putAll(output.getData(0));
        setAjax(output);
    }
}
