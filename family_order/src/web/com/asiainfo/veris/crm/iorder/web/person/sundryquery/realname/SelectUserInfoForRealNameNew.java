package com.asiainfo.veris.crm.iorder.web.person.sundryquery.realname;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SelectUserInfoForRealNameNew extends PersonBasePage{

	/**
     * 根据号码查询用户信息，包括在网和不在网
     * @param cycle
     * @throws Exception
     */
    public void getCheckUserInfo(IRequestCycle cycle) throws Exception
    {

        IData param = getData();
        IDataset infos = null ;
//        String normalUserCheck = param.getString("NORMAL_USER_CHECK", "");
        String serialNumber = param.getString("SERIAL_NUMBER", "").trim();
        if ("".equals(serialNumber))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "手机号码丢失！");
            return;
        }
        
        infos = CSViewCall.call(this, "SS.GetUser360ViewSVC.getCheckUserInfo", param) ;
        
//        if (normalUserCheck.equals("false"))
//        {
//        	infos = CSViewCall.call(this, "SS.GetUser360ViewSVC.getCheckUserInfo", param) ;
//        }
//        else
//        {
//            return;
//        }
        if(IDataUtil.isEmpty(infos))
        {
        	CSViewException.apperr(CrmUserException.CRM_USER_672);
        	return;
        } 
        //setCount(infos.size());
		setInfos(infos);
    }
    
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
}
