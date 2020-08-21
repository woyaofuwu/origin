package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

/**
 * 呼叫转移
 * @author:YF
 * @Dat:   2013年12月23日   下午8:21:05 
 * @version: v1.0
 * @Description : TODO
 */
public class TransferredBean extends CSBizBean {
	
	
	public IDataset openServ(IData param,String servicename) throws Exception{
		
		IDataset dataset = new DatasetList();
		
		this.checkState(param);
		
		param.put("MODIFY_TAG", "0");
		
		IData data = changeData(param,servicename);
		
		dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);
		
		return dataset;
		
	}
	
	
	
	public IDataset closeServ(IData input,String servicename) throws Exception{
		IDataset dataset = new DatasetList();
		
		this.checkState(input);
		
		input.put("MODIFY_TAG", "1");
		
		IData data = changeData(input,servicename);
		
		dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);
		
		return dataset;
	}
	
	public IData changeData(IData input,String servicename) throws Exception {
		
		IData param = new DataMap();
		IDataset dataset = CommparaInfoQry.getDefaultOpenSvcId("CSM","1114",servicename);    //寻找配置的serviceid
		
		String svcType = input.getString("SVC_TYPE");   //漫游类型:1 表示港澳台漫游，2 表示国际漫游    湖南无此区分
		String effectType = input.getString("EFFECT_TYPE");  //生效方式：1、立即；2、次日；3、次月。
		String serial_number = input.getString("SERIAL_NUMBER");
		String serviceid = dataset.getData(0).getString("PARAM_CODE","0");
		String elementtypecode = dataset.getData(0).getString("PARA_CODE2","S");
		String opr_code = input.getString("MODIFY_TAG");
		String transferNumber = input.getString("TRANSFER_NUMBER");
		
		param.put("MODIFY_TAG", opr_code);  //0、开通；1、取消；2、修改
		param.put("ELEMENT_ID", serviceid);
		param.put("ELEMENT_TYPE_CODE", elementtypecode);
		param.put("SERIAL_NUMBER", serial_number);
		//param.put("RSRV_STR2", "G11BOSS1");
		//param.put("RSRV_STR3", transferNumber);
		param.put("ATTR_STR1", "V12V1");
		param.put("ATTR_STR2", transferNumber);
		param.put("IS_PLAT_ORDER", "true");
		
		return param;
		
	}
	
	
	
	
	/**
	 * 客户有效性校验
	 * @Title : checkState
	 * @Description:TODO
	 * @Param : @param input
	 * @return: void
	 * @throws Exception 
	 */
	private void checkState(IData data) throws Exception{
		
		String sn = data.getString("SERIAL_NUMBER");
		
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String userid = userInfo.getString("USER_ID");
        String identCode = data.getString("IDENT_CODE");
		String contactId = data.getString("CONTACT_ID");
		
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
		
	}
	

}
