
package com.asiainfo.veris.crm.order.web.person.vipsimbak;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class VipSimBakApp extends PersonBasePage
{

    /**
     * 备卡校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkBakCard(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData param = new DataMap();

        param.put("BAK_CARD_NO", data.getString("CARD_NO"));
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));

        IData simbakInfo = CSViewCall.call(this, "SS.VipSimBakAppSVC.checkBakCard", param).getData(0);

        simbakInfo.put("SIM_TYPE_CODE", simbakInfo.getString("RES_TYPE_CODE"));

        data.put("CAPACITY_TYPE_CODE", simbakInfo.getString("CAPACITY_TYPE_CODE"));
        data.put("RES_KIND_CODE", simbakInfo.getString("RES_KIND_CODE"));
        // 自己加上。为方便业务测试--start--
        data.put("VIP_TYPE_CODE", simbakInfo.getString("VIP_TYPE_CODE"));
        data.put("VIP_CLASS_ID", simbakInfo.getString("VIP_CLASS_ID"));
        data.put("newIsUSIM", simbakInfo.getString("newIsUSIM"));
        data.put("oldIsUSIM", simbakInfo.getString("oldIsUSIM"));
        data.put("RES_TYPE_CODE", simbakInfo.getString("RES_TYPE_CODE"));
        // simbakInfo.put("RES_KIND_NAME","test");
        // simbakInfo.put("BAK_CARD_NO",data.getString("CARD_NO"));
        // simbakInfo.put("IMSI", "11");
        // simbakInfo.put("KI", "2");
        // ---end--
        
        if(!data.getString("CREDIT_FEE_TAG","").equals("NO")){
            getFeelist(data);
        }

        this.setCondition(simbakInfo);
    }

    public void getFeelist(IData data) throws Exception
    {

        IDataset feeLists = CSViewCall.call(this, "SS.VipSimBakAppSVC.getDevicePrice", data);

        this.setAjax(feeLists);
    }

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

        IData param = new DataMap();

        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("REMOVE_TAG", "0");
        param.put("USER_ID", userInfo.getString("USER_ID"));

        // IData vipInfo = CSViewCall.call(this, "SS.VipSimBakQuerySVC.getVipSimBakAppInfo", param).getData(0);
        IData vipInfo = CSViewCall.call(this, "SS.VipSimBakAppSVC.getVipSimBakAppInfo", param).getData(0);
        vipInfo.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        vipInfo.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
        vipInfo.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        vipInfo.put("USER_ID", userInfo.getString("USER_ID"));
        vipInfo.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, vipInfo.getString("BRAND_CODE","")));
        IDataset simBakInfos = vipInfo.getDataset("VIP_BAK_INFO");

        this.setVipInfo(vipInfo);
        this.setInfos(simBakInfos);

    }

    
    public void checkEmptyCard(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset output = CSViewCall.call(this, "SS.SimCardCheckSVC.checkEmptyCard", data);
        setAjax(output);
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

        IDataset dataset = CSViewCall.call(this, "SS.VipSimBakAppRegSVC.tradeReg", param);
        setAjax(dataset);

    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset infos);

    public abstract void setVipInfo(IData data);

}
