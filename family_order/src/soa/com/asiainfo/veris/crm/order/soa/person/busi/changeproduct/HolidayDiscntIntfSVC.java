package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**    
 * Copyright: Copyright  2016 Asiainfo
 * 
 * @ClassName: HolidayDiscntIntfSVC.java
 * @Description: 产品变更无规则校验登记服务类
 *
 * @version: v1.0.0
 * @author: maoke
 * @date: 5 2, 2016 10:34:15 AM 
 *
 * Modification History:
 * Date            Author      Version        Description
 *-------------------------------------------------------*
 * 5 2, 2016    yanwu       v1.0.0           修改原因	
 */
public class HolidayDiscntIntfSVC extends CSBizService
{
    
	public IDataset tradeReg(IData input) throws Exception
    {
		IDataUtil.chkParam(input, "SERIAL_NUMBER");				// 手机号码（付费号码）
		IDataUtil.chkParam(input, "OBJECT_SERIAL_NUMBER");		// 赠送号码
		IDataUtil.chkParam(input, "DISCNT_CODE");				// 办理套餐编码
		IDataUtil.chkParam(input, "OBJECT_DISCNT_CODE");		// 赠送套餐编码
		IDataset results = new DatasetList();
		
		String strSn = input.getString("SERIAL_NUMBER");
		String strDc = input.getString("DISCNT_CODE");
		String strOsn = input.getString("OBJECT_SERIAL_NUMBER");
		//String strOdc = input.getString("OBJECT_DISCNT_CODE");
		if(strSn.equals(strOsn)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "办理号码和赠送号码不能一样");
		}
		
		IData dtObjectDiscnt = new DataMap();
		dtObjectDiscnt.putAll(input);
		
		IData dtHolidayDiscnt = new DataMap();
		//dtHolidayDiscnt.put("ORDER_TYPE_CODE", "110");
		//dtHolidayDiscnt.put("TRADE_TYPE_CODE", "110");
		dtHolidayDiscnt.put("SERIAL_NUMBER", strSn);
		dtHolidayDiscnt.put("ELEMENT_ID", strDc);
		dtHolidayDiscnt.put("ELEMENT_TYPE_CODE", "D");
		dtHolidayDiscnt.put("MODIFY_TAG", "0");
		dtHolidayDiscnt.put("BOOKING_TAG", "0");
		dtHolidayDiscnt.put("OBJECT", dtObjectDiscnt);
        results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", dtHolidayDiscnt);
		
		return results;
    }
    
}
