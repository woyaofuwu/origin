package com.asiainfo.veris.crm.iorder.web.person.remoteresetpswd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RemoteResetPswd extends PersonBasePage {

	/**
	 * 查询卡类型
	 * @param cycle
	 * @throws Exception
	 */
	public void qryCardType(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
//		IData cardType = new DataMap();
		pageData.put("BIZ_TYPE", "1011");
		IDataset cardType = CSViewCall.call(this, "SS.RemoteResetPswdSVC.queryCardType", pageData);
		IDataset friendCounts = CSViewCall.call(this, "SS.RemoteResetPswdSVC.numCheckQuery", pageData);
		if(IDataUtil.isNotEmpty(friendCounts)){
			IData friendCount = friendCounts.getData(0);
			String rspCode = friendCount.getString("RSP_CODE");
			String count = friendCount.getString("NUM_COUNT");
			if ("00".equals(rspCode)) {
				cardType.getData(0).put("NUM_COUNT", count);
			}
		}
		setAjax(cardType.getData(0));
	}
	
	/**
	 * 好友号码查询
	 * @param cycle
	 * @throws Exception
	 */
	public void numCheckQuery(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		pageData.put("BIZ_TYPE", "1011");
		IDataset ajaxData = CSViewCall.call(this, "SS.RemoteResetPswdSVC.numCheckQuery", pageData);
		
		setAjax(ajaxData.getData(0));
	}
	/**
	 * 鉴权
	 * @param cycle
	 * @throws Exception
	 */
	public void openResultAuth(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
//		IData cardType = new DataMap();
		pageData.put("BIZ_TYPE", "1011");
		IDataset cardType = CSViewCall.call(this, "SS.RemoteResetPswdSVC.openResultAuthF", pageData);
		setAjax(cardType.getData(0));
	}
	
	public void onTradeSubmit(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		//调用归属省密码重置接口
		pageData.put("CHANNEL", "11");
		IDataset result = CSViewCall.call(this, "SS.RemoteResetPswdSVC.passwordCZF", pageData);
		setAjax(result);
	}
    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 跨区密码重置是否免人像比对和身份证可手动输入权限
     * 
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void kqbkDataRight(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.kqbkDataRight", param);
        setAjax(dataset.getData(0));
    }
    
}
