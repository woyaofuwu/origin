package com.asiainfo.veris.crm.order.web.person.remotedestroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class RemoteDestroyUser extends PersonBasePage{
	public void qryCardType(IRequestCycle cycle) throws Exception
	{
		IData params = getData();
		params.put("BIZ_TYPE", "1013");//现金费用转移
		IData cardTypeInfo = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.applyCardTypequery", params).getData(0);
		String homeProv = cardTypeInfo.getString("HOME_PROV");
		String isJxh = cardTypeInfo.getString("IS_JXH");
		String homeProvName = this.getPageUtil().getStaticValue("PROV_CODE", homeProv);//从省代码得到省份名称
		cardTypeInfo.put("HOME_PROV_NAME", homeProvName);
		if("0".equals(isJxh)){//好友号码查询 
			IDataset friendCounts = CSViewCall.call(this, "SS.RemoteResetPswdSVC.numCheckQuery", params);
			if(IDataUtil.isNotEmpty(friendCounts)){
				IData friendCount = friendCounts.getData(0);
				String rspCode = friendCount.getString("RSP_CODE");
				String count = friendCount.getString("NUM_COUNT");
				if ("00".equals(rspCode)) {
					cardTypeInfo.put("NUM_COUNT", count);
				}
			}
		}
		setAjax(cardTypeInfo);
	}

	public void openResultAuth(IRequestCycle cycle) throws Exception
	{
	  	IData params = getData();
	  	params.put("BIZ_TYPE", "1013");//现金费用转移

	  	IData resultAuthInfo = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.applyOpenResultAuth", params).getData(0);
	  	setAjax(resultAuthInfo);
	}

	public void checkSerialNumber(IRequestCycle cycle) throws Exception
	{
		IData input = getData();
		IData result = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.checkSerialNumberProv", input).first();
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
	
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		data.put("PSPT_TYPE_CODE", "0");
		data.put("PSPT_ID", data.getString("IDCARDNUM"));
		String serialNumber = data.getString("MOBILENUM");
        IDataset resultInfo = CSViewCall.call(this, "SS.RemoteDestroyUserRegSVC.tradeReg", data);
        if(DataSetUtils.isBlank(resultInfo)) {
			CSViewException.apperr(CrmCommException.CRM_COMM_595);
		}
		String orderId = resultInfo.first().getString("ORDER_ID");
        String tradeId = resultInfo.first().getString("TRADE_ID");
        String sid=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        orderId=orderId.substring(0, orderId.length()-4);
		data.put("REMOTE_ORDER_ID", "COP898" + sid + orderId);
        data.put("TRADE_ID", tradeId);
        CSViewCall.call(this, "SS.RemoteDestroyUserSVC.applySubmitCancel", data);
        CSViewCall.call(this, "SS.RemoteDestroyUserSVC.insPsptFrontBack", data);
        IData queryParam = new DataMap();
        queryParam.put("REMOTE_ORDER_ID", data.getString("REMOTE_ORDER_ID"));
		IDataset destroyOrders = CSViewCall.call(this,"SS.RemoteDestroyUserSVC.queryApplyDestroyUserTradeByOrderId", queryParam);
		System.out.println("============destroyOrders=========="+destroyOrders);
		if(IDataUtil.isNotEmpty(destroyOrders)){
  			IData data1 = new DataMap();
  			data1.put("REMOTE_SERIAL_NUMBER", serialNumber);
  			String remoteOrderId = destroyOrders.getData(0).getString("REMOTE_ORDER_ID", "");
  			data1.put("REMOTE_ORDER_ID", remoteOrderId);
  			CSViewCall.call(this,"SS.RemoteDestroyUserSVC.applyCancelAccount", data1);
  		}else{
  			CSViewException.apperr(CrmCommException.CRM_COMM_103,"无派单数据");
  		}
	    setAjax(resultInfo);
	}
	
	 /**
     * 跨区销户是否免人像比对和身份证可手动输入权限
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
    /**
     * 销户前校验
     * @param cycle
     * @throws Exception
     */
	public void destroyCheck(IRequestCycle cycle) throws Exception
	{
	  	IData params = getData();
	  	params.put("BIZ_TYPE", "1017");//销户前校验

	  	IData resultAuthInfo = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.destroyCheck", params).getData(0);
	  	setAjax(resultAuthInfo);
	}
}
