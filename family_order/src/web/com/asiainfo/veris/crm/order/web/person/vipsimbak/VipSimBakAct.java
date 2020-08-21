
package com.asiainfo.veris.crm.order.web.person.vipsimbak;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class VipSimBakAct extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IData param = new DataMap();

        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userInfo.getString("USER_ID"));

        IDataset vipSimbakInfos = CSViewCall.call(this, "SS.VipSimBakQuerySVC.getVipSimBakActInfo", param);
        if(IDataUtil.isNotEmpty(vipSimbakInfos)){
        	IData vipData = vipSimbakInfos.getData(0);
        	if(IDataUtil.isNotEmpty(vipData)){
        		vipData.put("CUST_NAME", custInfo.getString("CUST_NAME"));
                vipData.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
                vipData.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, vipData.getString("BRAND_CODE","")));
                this.setVipInfo(vipData);

                this.setInfos(vipData.getDataset("VIP_BAK_INFO"));
        	}
        }
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

        IDataset dataset = CSViewCall.call(this, "SS.VipSimBakActRegSVC.tradeReg", param);
        setAjax(dataset);

    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset infos);

    public abstract void setVipInfo(IData data);

}
