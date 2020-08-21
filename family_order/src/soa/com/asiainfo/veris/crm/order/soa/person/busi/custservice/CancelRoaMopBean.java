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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;

/**
 * @author:YF
 * @Dat:   2013年12月20日   下午7:35:31 
 * @version: v1.0
 * @Description : TODO
 */
public class CancelRoaMopBean extends CSBizBean {
	
	public IDataset cancelRoaMop(IData input,String servicename) throws Exception {
		IDataset dataset = new DatasetList();
		
		IData param = new DataMap();
		
		this.checkState(input);
		
		param = this.InputData(input,servicename);
		
		dataset = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param);
		
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
		
		String idencode = input.getString("IDENT_CODE","");
		
		String contactId = input.getString("CONTACT_ID");
		
		//------用户信息校验
		IDataset userInfo = UserInfoQry.getUserInfoBySn(serial_number,"0");
		if(IDataUtil.isEmpty(userInfo)){
			CSAppException.apperr(CrmUserException.CRM_USER_1);
		}
		
		CheckUserIDentUnEffTBean bean = (CheckUserIDentUnEffTBean)BeanManager.createBean(CheckUserIDentUnEffTBean.class);
		//-----用户凭证校验
		if("".equals(idencode)){
			CSAppException.apperr(CrmUserException.CRM_USER_1);    //获取用户凭证失败
		}else{
			IData data = bean.queryUserIdentUnEffT(input);
		}
		
	}
	
	
	
	/*
	 * 必传字段：SERIAL_NUMBER、ELEMENT_ID、ELEMENT_TYPE_CODE、MODIFY_TAG
	 * 其余字段：RSRV_STR1、START_DATE、ABSOLUTE_START_DATE（元素生效的绝对时间，批量接口新增优惠时传入）
	 *  ABSOLUTE_END_DATE（元素失效的绝对时间，批量接口新增优惠时传入 ）
	 */
	private IData InputData(IData input,String servicename) throws Exception{
		IDataset dataset = CommparaInfoQry.getDefaultOpenSvcId("CSM","1114",servicename);    //寻找配置的serviceid
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(ParamException.CRM_PARAM_292);
		}
		String effectType = input.getString("EFFECT_TYPE");  //生效方式：1、立即；2、次日；3、次月。
		String serial_number = input.getString("SERIAL_NUMBER");
		String serviceid = dataset.getData(0).getString("PARAM_CODE","0");
		String elementtypecode = dataset.getData(0).getString("PARA_CODE2","S");
		String product_id = "";
		String package_id = "";
		
		//查询用户已开通的服务
		IDataset datasetsvc = new DatasetList();
		UcaData uca = UcaDataFactory.getNormalUca(serial_number); 
		datasetsvc = UserSvcInfoQry.queryUserSvcByUserId(uca.getUserId(),serviceid,null);
		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(datasetsvc,null,null);
		if(IDataUtil.isNotEmpty(datasetsvc)){
			for(int i = 0 ; i < datasetsvc.size() ; i++){
				IData pa = datasetsvc.getData(i);
				if(serviceid.equals(pa.getString("SERVICE_ID"))){
					product_id = pa.getString("PRODUCT_ID");
					package_id = pa.getString("PACKAGE_ID");
					break;
				}else{
					CSAppException.apperr(CrmUserException.CRM_USER_24);
				}
			}
		}else{
			CSAppException.apperr(CrmUserException.CRM_USER_121);
		}
		
		IDataset selectedElements = new DatasetList();
		IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("PACKAGE_ID", package_id);
        data.put("ELEMENT_ID", serviceid);
        data.put("MODIFY_TAG", "1");
        data.put("ELEMENT_TYPE_CODE", elementtypecode);
        selectedElements.add(data);
        
        IData param = new DataMap();
        param.put("MODIFY_TAG", "0");  //0、开通；1、取消；2、修改
		param.put("ELEMENT_ID", "18");//国内漫游
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("ELEMENT_TYPE_CODE", "S");
		param.put("SERIAL_NUMBER", serial_number);
		
		if("1".equals(effectType)){//立即
			param.put("RSRV_STR1", effectType);
		}else if("2".equals(effectType)){//次日
			param.put("RSRV_STR1", "2");
			param.put("ABSOLUTE_START_DATE", SysDateMgr.getTomorrowDate());
		}else if("3".equals(effectType)){//次月
			param.put("RSRV_STR1", "0");
		}
		selectedElements.add(param);

        IData dataInfo = new DataMap();
        dataInfo.put("SELECTED_ELEMENTS", selectedElements);
        dataInfo.put("SERIAL_NUMBER", serial_number);
		
        if("1".equals(effectType)){//立即
        	dataInfo.put("EFFECT_NOW", effectType);
		}else{
			dataInfo.put("EFFECT_NOW", "");
		}
		
		return dataInfo;
	}

}
