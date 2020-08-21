
package com.asiainfo.veris.crm.order.web.person.changespeinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ModifySpeInfo extends PersonBasePage
{
    /**
     * 检测IMSI是否正确,根据IMSI获取SIM_CARD_NO信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void imsiCheck(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String simCardNo = "";
        IDataset dataset = CSViewCall.call(this, "SS.ModifySpeInfoSVC.getSimCardInfo", data);
        if (IDataUtil.isNotEmpty(dataset))
        {// X_RESULTCODE 的值为0时代码查询有数据，否则无数据
            simCardNo = dataset.getData(0).getString("SIM_CARD_NO");
        }
        setAjax("SIM_CARD_NO", simCardNo);
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        IData userInfo = new DataMap(pgData.getString("USER_INFO", ""));
        setUserInfo(userInfo);

        IData inparam = new DataMap();
        inparam.put("USER_ID", userInfo.getString("USER_ID"));
        inparam.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        inparam.put("USER_STATE_CODESET", userInfo.getString("USER_STATE_CODESET"));
        inparam.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER", ""));
        IData data = CSViewCall.callone(this, "SS.ModifySpeInfoSVC.getCommInfo", inparam);
        //获取品牌列表
        setBrandList(data.getDataset("BrandList"));
        setStateList(data.getDataset("StateList"));
        setCommInfo(data);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        data.put("BRAND_CODE", data.getString("E_BRAND_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.ModifySpeInfoRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData commInfo);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setBrandList(IDataset brandInfo);
    
    public abstract void setStateList(IDataset stateInfo);


    
    /**
     * 检测SimCard是否正确，根据SIM_CARD_NO获取IMSI信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void simCardNoCheck(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String imsi = "";
        IDataset dataset = CSViewCall.call(this, "SS.ModifySpeInfoSVC.getSimCardInfo", data);
        if (IDataUtil.isNotEmpty(dataset))
        {
            imsi = dataset.getData(0).getString("IMSI");
        }
        setAjax("IMSI", imsi);

    }

}
