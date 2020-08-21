
package com.asiainfo.veris.crm.order.web.person.changephone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ModifyPhoneCode extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        data.put("USER_ID", userInfo.getString("USER_ID", ""));
    
              IDataset relationList = CSViewCall.call(this,"SS.OneCardMultiNoSVC.qryRelationListForCRM", data);
             userInfo.put("isOneCard", "0");
               if(!relationList.isEmpty()){
               	userInfo.put("isOneCard", relationList.size());
        	}
        // 获取本业务营业费用资源信息和初始化校验
        IDataset output = CSViewCall.call(this, "SS.ModifyPhoneCodeSVC.loadChildInfo", data);
        setUserInfo(userInfo);
        setCustInfo(custInfo);
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
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.ModifyPhoneCodeRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 查询网上选号号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryNetChoosePhone(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));

        IDataset output = CSViewCall.call(this, "SS.ModifyPhoneCodeSVC.queryNetChoosePhone", data);
        setChooseInfos(output);
    }

    /**
     * 释放选占
     * 
     * @param cycle
     * @throws Exception
     */

    public void releaseSingleRes(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset output = CSViewCall.call(this, "SS.CreatePersonUserSVC.releaseSingleRes", data);
        setAjax(output);
    }

    public abstract void setChooseInfo(IData chooseInfo);

    public abstract void setChooseInfos(IDataset chooseInfos);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);

    /**
     * 校验号码或者sim卡 htag=0 号码 else sim卡
     * 
     * @param cycle
     * @throws Exception
     */
    public void verifyResourse(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));

        IDataset output = CSViewCall.call(this, "SS.ModifyPhoneCodeSVC.verifyResourse", data);
        setAjax(output);
    }
}
