package com.asiainfo.veris.crm.order.web.person.broadband.widenet.gponuserorderdestroy;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * GPON宽带预约拆机
 */
public abstract class GponUserOrderDestroy extends PersonBasePage
{
    /**
     * 查询宽带资料后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        //欠费信息
        IDataset infos = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryBusiInfo", data);
        setInfo(infos.getData(0));
        
        //宽带地址信息
        IDataset wideInfos = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        setWideInfo(wideInfos.getData(0));
        
        //宽带预约拆机信息
        IDataset destroyInfos = CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.getGponDestroyInfo", data);
        setDestroyInfo(destroyInfos.getData(0));
    }
    
    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = CSViewCall.call(this, "SS.OrderDestoryGponRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setInfo(IData info);
    public abstract void setWideInfo(IData wideInfo);
    public abstract void setDestroyInfo(IData destroyInfo);
}
