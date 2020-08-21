
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserIMSInfo extends PersonBasePage
{

	 /**
     * REQ201708240014_家庭IMS固话开发需求
     * <br/>
     * 通过服务号码或IMS固话或身份证号码，查询IMS固话信息
     * @author zhengkai5
     * @date 20180310
     */
    public void queryIMSInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	data.put("EPARCHY_CODE", "0898");
    	if (!StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")) || !StringUtils.isEmpty(data.getString("AUTH_SERIAL_NUMBER", "")))
        {
    		if(StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
    		{
    			data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
    		}
    		 //通过手机号码获取固话号码
    		 IData ImsInfo;
			try {
				ImsInfo = CSViewCall.callone(this,
						"SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
				if(IDataUtil.isNotEmpty(ImsInfo))
	    		 {
	    			 //IMS家庭固话   手机号码
	            	 String serialNumberB=ImsInfo.getString("SERIAL_NUMBER_B", "");
	         		 String userIdB = ImsInfo.getString("USER_ID_B", "");
	            	 data.put("SERIAL_NUMBER", serialNumberB);
	            	 data.put("USER_ID", userIdB);
	    		 }
			} catch (Exception e) {
				CSViewException.apperr(CrmCommException.CRM_COMM_103,"没有该用户信息！");
			}
    		 
		 }else if(!StringUtils.isEmpty(data.getString("FIX_NUMBER", "")))
		 {
			 if(data.getString("FIX_NUMBER", "").startsWith("0898"))
			 {
				 data.put("SERIAL_NUMBER", data.getString("FIX_NUMBER")); //固话号码
			 }
			 else 
			 {
				 data.put("SERIAL_NUMBER", "0898"+data.getString("FIX_NUMBER"));
			 }
			 IData userInfo = CSViewCall.callone(this, "SS.UserInfoQrySVC.getUserInfoBySerialNumber", data);
			 if(IDataUtil.isNotEmpty(userInfo))
			 {
				 data.put("USER_ID", userInfo.getString("USER_ID"));
			 }
		 }
		 else if(!StringUtils.isEmpty(data.getString("PSPT_ID", "")))
		 {
			 //通过身份证号码 获取 IMS固话
			String custId = null;
			IDataset custInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryCustInfoByPsptId", data);
			if(IDataUtil.isNotEmpty(custInfos))
			{
				for(int j=0;j<custInfos.size();j++){
					custId = custInfos.getData(j).getString("CUST_ID");
					data.put("CUST_ID", custId);
					IDataset userInfos = CSViewCall.call(this, "SS.UserInfoQrySVC.getAllNormalUserInfoByCustId", data);
					boolean flag = false;
					if(userInfos.size()>0){
						for(int i = 0;i<userInfos.size();i++)
						{
							if(userInfos.getData(i).getString("SERIAL_NUMBER").startsWith("0898"))
							{
								data.put("USER_ID", userInfos.getData(i).getString("USER_ID"));
								data.put("SERIAL_NUMBER", userInfos.getData(i).getString("SERIAL_NUMBER"));
								flag = true;
								break;
							}
						}
					}
					if(flag) break;
				}
			}
		 }
		 else
		 {
			 CSViewException.apperr(CrmCommException.CRM_COMM_103,"请输入相关信息！");
		 }
    	
		 //通过固话UserId查询产品和品牌信息
    	if(StringUtils.isNotEmpty(data.getString("USER_ID"))){
    		IData userProductInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryUserProductInfoByUserId", data);
    		if(IDataUtil.isNotEmpty(userProductInfo)){
    			IData info = new DataMap();
    			String brandCode = userProductInfo.getString("BRAND_CODE");
    			String productName = userProductInfo.getString("RSRV_STR5");
    			info.put("BRAND_CODE", brandCode);
    			info.put("PRODUCT_NAME", productName);
    			info.put("IMS_NUMBER", data.getString("SERIAL_NUMBER",""));
    			setInfo(info);
    		}
    		else 
    		{
    			CSViewException.apperr(CrmCommException.CRM_COMM_103,"该用户有失效的IMS固话业务！");
    		}	
    	}else
    	{
    		CSViewException.apperr(CrmCommException.CRM_COMM_103,"该用户没有办理IMS固话业务！");
    	}
    }
    
    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData IMSInfo);
}
