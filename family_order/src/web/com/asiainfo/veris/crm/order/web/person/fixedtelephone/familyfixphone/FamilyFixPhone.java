
package com.asiainfo.veris.crm.order.web.person.fixedtelephone.familyfixphone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyFixPhone extends PersonBasePage
{
    /**
     * 功能说明：校验手机号 
     */
    public void checkAuthSerialNum(IRequestCycle cycle) throws Exception
    {
        IData userData = getData(); 
        String serialNumber = userData.getString("SERIAL_NUMBER", ""); 
        IDataset results = CSViewCall.call(this, "SS.FamilyFixPhoneSVC.checkAuthSerialNum", userData);
        setAjax(results.first());
    }
 
    /**
     * 功能说明：校验固话号 
     */
    public void checkFixPhoneNum(IRequestCycle cycle) throws Exception
    {
    	IData userData = getData(); 
        String fixNum = userData.getString("FIX_NUMBER", ""); 
        userData.put("FIX_NUMBER", "0898"+fixNum);
        IDataset results = CSViewCall.call(this, "SS.FamilyFixPhoneSVC.checkFixPhoneNum", userData);
        setAjax(results.first());
    }

   

    /**
     * 业务提交,组件默认提交action方法
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
        String fixNum=data.getString("FIX_NUMBER", "");
        if(!fixNum.startsWith("0898")){
        	data.put("FIX_NUMBER", "0898"+fixNum);
        }
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", ""));
        IDataset dataset = CSViewCall.call(this, "SS.FamilyFixPhoneRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    

    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
}
