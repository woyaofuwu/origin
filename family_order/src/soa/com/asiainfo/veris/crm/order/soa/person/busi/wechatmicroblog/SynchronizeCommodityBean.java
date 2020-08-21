package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserUnionInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

public class SynchronizeCommodityBean  extends CSBizBean
{		
	/**
	* 校验入参
	* @param data
	* @throws Exception
	*/
	public void checkParam(IData data) throws Exception
	{
		if (data.getString("PROVINCE") == null
				|| data.getString("PROVINCE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_924);
		}
		if (data.getString("BIZ_TYPE_CODE") == null
				|| data.getString("BIZ_TYPE_CODE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1149);
		}
		if (data.getString("OPR_NUMB") == null
				|| data.getString("OPR_NUMB").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_299);
		}
	}
		
	/**
	 * 产品信息同步
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IData synchronizeAllCommodity(IData inparam) throws Exception
	{
		this.checkParam(inparam);
		IData data = new DataMap();
		IData info = new DataMap();
		//返回查询数据
		IDataset retList = new DatasetList();

		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		
		IDataset dataset = UserUnionInfoQry.getMarketProductByProvice(inparam);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_938);
		}
		
		data.put("PROVINCE", inparam.getString("PROVINCE"));
		data.put("OPR_TIME", df.format(new Date()));
		info.put("ELEMENT_TYPE_CODE", "01");
		info.put("PRODUCT_INFO", dataset);
		retList.add(info);
		data.put("PRODUCT_LIST", retList);
		data.put("X_RESULTCODE", "0");
		data.put("X_RESULTINFO", "OK!");
		/**************************合版本 duhj  2017/5/3 start******************************************/
		if(CustServiceHelper.isCustomerServiceChannel(inparam.getString("BIZ_TYPE_CODE"))){
			data.put("INTF_TYPE", "01");
		}
		/**************************合版本 duhj  2017/5/3 end******************************************/

		return data;
	}
		
}
