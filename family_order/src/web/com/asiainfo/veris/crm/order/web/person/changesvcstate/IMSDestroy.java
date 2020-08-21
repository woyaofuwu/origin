/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.changesvcstate;

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

/**
 * @ClassName: IMSDestroy
 * @author: zhengkai5
 * @date: 20180514
 */
public abstract class IMSDestroy extends PersonBasePage
{
	
	public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        param.put("TRADE_TYPE_CODE", "6805");
        this.setInfo(param);
    }
	
	/**
	 * 输入号码后的查询
	 * */
    public void setPageInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	if(StringUtils.isEmpty(data.getString("SERIAL_NUMBER")))
        {
        	data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
    	data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	data.put("EPARCHY_CODE", "0898");
    	IData ImsInfo = null;
        try {
			ImsInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
		} catch (Exception e) {
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"该用户没有受理家庭IMS固话业务！");
		}
		if (IDataUtil.isNotEmpty(ImsInfo)) {
			IData imsInfo = new DataMap();
			String serialNumber = ImsInfo.getString("SERIAL_NUMBER_B");
			String userId = ImsInfo.getString("USER_ID_B");
			data.put("USER_ID", userId);
			
			IData imsDataInfo = new DataMap();
			imsDataInfo.put("USER_ID", ImsInfo.getString("USER_ID_B"));
			imsDataInfo.put("SERIAL_NUMBER", ImsInfo.getString("SERIAL_NUMBER_B"));
			
			IDataset result = CSViewCall.call(this,"SS.ChangeSvcStateSVC.getIMSInfoByJxSn",imsDataInfo);
			String code1 = result.getData(0).getString("CODE1");
			
			if (IDataUtil.isNotEmpty(result) && StringUtils.equals("-1", code1)) {
				CSViewException.apperr(CrmCommException.CRM_COMM_103,"IMS固话号码办理了“吉祥号码开户约定消费”活动,不能办理拆机!");
			}
			
			IData userProductInfo = CSViewCall.callone(this,"SS.GetUser360ViewSVC.qryUserProductInfoByUserId",data);
			if (IDataUtil.isNotEmpty(userProductInfo)) {
				String brandCode = userProductInfo.getString("BRAND_CODE");
				String productName = userProductInfo.getString("RSRV_STR5");
				imsInfo.put("BRAND_CODE", brandCode);
				imsInfo.put("PRODUCT_NAME", productName);
			}
			imsInfo.put("IMS_NUMBER", serialNumber);
			setInfo(imsInfo);
		}else
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"该用户没有受理家庭IMS固话业务！");
		}
    }
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	data.put("EPARCHY_CODE", "0898");
    	if (StringUtils.isNotEmpty(data.getString("SERIAL_NUMBER")))
        {
            if(data.getString("AUTH_SERIAL_NUMBER").startsWith("KD_"))
            {
            	data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER").replace("KD_", ""));
            }
            else
            {
            	data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
            }
        }
        
        //通过手机号码获取固话号码
		try {
			IData ImsInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
			if(IDataUtil.isEmpty(ImsInfo))
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103,"该用户未办理IMS固话业务！");
			}else {
				data.put("WIDE_SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
				data.put("SERIAL_NUMBER", ImsInfo.getString("SERIAL_NUMBER_B"));
			}
		} catch (Exception e) {
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"没有该用户信息！");
		}
        
        IDataset dataset = CSViewCall.call(this, "SS.IMSDestroyRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    
    public abstract void setInfo(IData info);
    public abstract void setCustInfo(IData info);
    public abstract void setUserInfo(IData info);
}
