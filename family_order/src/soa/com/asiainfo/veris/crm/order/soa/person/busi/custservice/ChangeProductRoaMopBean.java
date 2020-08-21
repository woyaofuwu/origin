package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * @author:YF
 * @Dat:   2013年12月19日   下午3:33:30 
 * @version: v1.0
 * @Description : TODO
 */
public class ChangeProductRoaMopBean extends CSBizBean {
	
	public IDataset changeStates(IData input,String servicename) throws Exception{
		
		IDataset dataset = new DatasetList();
		
		IData param = new DataMap();
		
		this.checkState(input);
		
		param = this.InputData(input,servicename);
		
		dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
		
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
	
	
	
	/*
	 * 必传字段：SERIAL_NUMBER、ELEMENT_ID、ELEMENT_TYPE_CODE、MODIFY_TAG
	 * 其余字段：RSRV_STR1、START_DATE、ABSOLUTE_START_DATE（元素生效的绝对时间，批量接口新增优惠时传入）
	 *  ABSOLUTE_END_DATE（元素失效的绝对时间，批量接口新增优惠时传入 ）
	 */
	private IData InputData(IData input,String servicename) throws Exception{
		IData param = new DataMap();
		IDataset dataset = CommparaInfoQry.getDefaultOpenSvcId("CSM","1114",servicename);    //寻找配置的serviceid
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(ParamException.CRM_PARAM_292);
		}
		
		String svcType = input.getString("SVC_TYPE");   //漫游类型:1 表示港澳台漫游，2 表示国际漫游    湖南无此区分
		String effectType = input.getString("EFFECT_TYPE");  //生效方式：1、立即；2、次日；3、次月。
		String serial_number = input.getString("SERIAL_NUMBER");
		String serviceid = dataset.getData(0).getString("PARAM_CODE","0");
		String elementtypecode = dataset.getData(0).getString("PARA_CODE2","S");
		
		param.put("MODIFY_TAG", "0");
		param.put("ELEMENT_ID", serviceid);
		param.put("ELEMENT_TYPE_CODE", elementtypecode);
		param.put("SERIAL_NUMBER", serial_number);
		
		if("1".equals(effectType)){//立即
			param.put("RSRV_STR1", effectType);
		}else if("2".equals(effectType)){//次日
			param.put("RSRV_STR1", "2");
			param.put("ABSOLUTE_START_DATE", SysDateMgr.getTomorrowDate());
		}else if("3".equals(effectType)){//次月
			param.put("RSRV_STR1", "0");
		}
		
		return param;
	}
	
	
}
