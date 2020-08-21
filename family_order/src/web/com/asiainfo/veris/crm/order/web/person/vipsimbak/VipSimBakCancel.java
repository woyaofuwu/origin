
package com.asiainfo.veris.crm.order.web.person.vipsimbak;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class VipSimBakCancel extends PersonBasePage
{

    protected String getSysTagInfo(String tagCode, String key, String defaultValue) throws Exception
    {

        IData param = new DataMap();

        param.put("TAG_CODE", tagCode);
        param.put("SUBSYS_CODE", "CSM");
        param.put("USE_TAG", 0);

        IDataset dataset = CSViewCall.call(this, "SS.VipSimBakQuerySVC.getSysTagInfo", param);

        if (dataset.size() > 0)
        {
            IData data = dataset.getData(0);
            return data.getString(key, defaultValue);
        }
        return defaultValue;

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        String NORMAL_USER_SIMBAK = getSysTagInfo("CS_NORMALUSERSIMBAK", "TAG_CHAR", "0");// 参数字段

        IData param = new DataMap();

        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userInfo.getString("USER_ID"));

        IDataset vipSimbakInfos = CSViewCall.call(this, "SS.VipSimBakQuerySVC.getVipSimBakCancelInfo", param);

        IData vipData = vipSimbakInfos.getData(0);

        vipData.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        vipData.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        vipData.put("NORMAL_USER_SIMBAK", NORMAL_USER_SIMBAK);
        vipData.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, vipData.getString("BRAND_CODE","")));
        this.setVipInfo(vipData);

        this.setInfos(vipData.getDataset("VIP_BAK_INFO"));

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

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

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.put("REMARK", getData().getString("REMARK"));
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.VipSimBakCancelRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset infos);

    public abstract void setVipInfo(IData data);
}
