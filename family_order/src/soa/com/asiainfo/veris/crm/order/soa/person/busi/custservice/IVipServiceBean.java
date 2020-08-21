package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class IVipServiceBean extends CSBizBean {

	public IDataset vipSimBakRestore(IData param)throws Exception {
		if("".equals(param.getString("SERIAL_NUMBER"))){
			param.put("SERIAL_NUMBER", param.getString("MSISDN"));
		}
		this.checkState(param);
		IDataset dataset = new DatasetList();
		dataset = CSAppCall.call("SS.VipSimBakActRegSVC.tradeReg",param);
		return dataset;
	}
	
	/**
	 * 客户有效性校验
	 * @Title : checkState
	 * @Description:TODO
	 * @Param : @param input
	 * @return: void
	 * @throws Exception 
	 */
	private void checkState(IData input) throws Exception{
		
		String serial_number = input.getString("SERIAL_NUMBER");
		
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);
    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String userid = userInfo.getString("USER_ID");
        String identCode = input.getString("IDENT_CODE");
		String contactId = input.getString("CONTACT_ID");
		
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
		
	}

}
