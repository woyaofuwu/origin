package com.asiainfo.veris.crm.order.soa.person.busi.onepsptmultiUser;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.wade.container.util.log.Log;

/**
 * 
 * @author chenfeng9
 * @date 2017年12月1日
 * @param OnePsptMultiUserSVC
 */
public class OnePsptMultiUserSVC extends CSBizService {
	
	private static final long serialVersionUID = -2809747789074461316L;
	/**
	 * 批量操作任务执行状态查询
	 */
	public IDataset checkInputData(IData input) throws Exception {
		if(!checkInfoAll(input)){
			Log.info("进入checkInfoAll——————————————————————————");
        	throw new RuntimeException("用户信息校验失败，请确认用户的信息完整！");
        }
        String serialNumber = input.getString("SERIAL_NUMBER");
        String isPersonPspt = input.getString("IS_UNIT_PSPT_TYPE");// 0表示个人客户  1表示单位客户
    	IData inParam = new DataMap();
        inParam.put("PSPT_ID", input.get("PSPT_ID"));
        inParam.put("PSPT_TYPE_CODE", input.get("PSPT_TYPE_CODE"));
        inParam.put("SERIAL_NUMBER", serialNumber);
        
        IDataset checkResult = new DatasetList();
        if("0".equals(isPersonPspt)){
        	checkResult = Dao.qryByCodeAllCrm("TF_F_USER","SEL_CUSTNAME_BY_PSPT",inParam,true);
        }
        if("1".equals(isPersonPspt)){
        	IDataset tempList = Dao.qryByCodeAllCrm("TF_F_CUST_PERSON","SEL_BY_PSPT_CUSTID",inParam,true);//根据单位证件号查询使用人的证件号码
        	if(IDataUtil.isEmpty(tempList)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到证件信息，请确认证件信息填写正确！");
        	}
        	String userPsptId = tempList.getData(0).getString("PSPT_ID");//单位证件开户使用人证件信息
        	String userPsptTypeCode = tempList.getData(0).getString("PSPT_TYPE_CODE");
        	inParam.put("PSPT_ID", userPsptId);
        	inParam.put("PSPT_TYPE_CODE", userPsptTypeCode);
        	checkResult = Dao.qryByCodeAllCrm("TF_F_USER","SEL_CUSTNAME_BY_PSPT",inParam,true);
        	checkResult.add(tempList.getData(0));
        }
        
        //根据证件号码获取数据库信息
        if(checkResult.size()<1){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息，请确认用户信息填写正确！");
        }
        if(checkResult.size()<2){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户下有多余用户！");
        }
        
        //比较是否存在不同证件名
        for(int i=0;i<checkResult.size();i++){
        	int count = 0;
        	for(int j=i+1;j<checkResult.size();j++){
        		if(StringUtils.equals(checkResult.getData(i).getString("CUST_NAME"), checkResult.getData(j).getString("CUST_NAME"))){
        			count++;
        			if(count == (checkResult.size()-1)){
        	        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"该证件不存在一证多名情况！");
        	        }
        		}
        	}
        }
        //需要销户的用户列---这里默认一个手机号码只对应一个用户，不存在同一个号码多用户情况、
        IDataset backResult = new DatasetList();
        for(int i = 0,size = checkResult.size();i<size;i++){
        	DataMap dataMap = (DataMap) checkResult.get(i);
        	if(StringUtils.equals(serialNumber, dataMap.getString("SERIAL_NUMBER"))){
        		backResult = checkResult.getDataset(i);
        		break;
        	}
        }
    	return backResult;
	}
	
	/**
	 * 批量操作任务执行状态查询
	 */
	private boolean checkInfoAll(IData input){
		Log.info("进入checkInfoAll————————————————————————————");
		boolean checkResult = false;
		if(!StringUtils.isBlank(input.getString("SERIAL_NUMBER"))&&!StringUtils.isBlank(input.getString("PSPT_TYPE_CODE"))&&!StringUtils.isBlank(input.getString("PSPT_ID"))){
			checkResult = true;
		}
		return checkResult;
	}
}