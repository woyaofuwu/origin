package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class FamilyUpdateSVC extends CSBizService {
	
	//updateFamily
	public IDataset updateFamily(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");

        if (StringUtils.isEmpty(sn))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "SERIAL_NUMBER");
        }
        IDataset resultList = new DatasetList();
        IData callResult = new DataMap();
		IData resultData = new DataMap();
		resultData.put("X_RESULTCODE", "0");
        try {
        	callResult = CSAppCall.call( "SS.UpdateFamilyRegSVC.tradeReg", input).getData(0);
		} catch (Exception e) {
			// TODO: handle exception
			resultData.put("X_RSPTYPE", "2");
			String[] errorMessage = e.getMessage().split("●");
            if(errorMessage[0].contains("您还未开通亲亲网业务")){
            	resultData.put("X_RESULTCODE", "1001");
            	resultData.put("X_RSPCODE","1001");
            	resultData.put("X_RESULTINFO", errorMessage[0]);
            	resultData.putAll(resultData);
        		resultList.add(resultData);
                return resultList;
            }else if(errorMessage[0].contains("您的亲亲网已经升级过了")){
            	resultData.put("X_RESULTCODE", "1002");
            	resultData.put("X_RSPCODE","1002");
            	resultData.put("X_RESULTINFO", errorMessage[0]);
            	resultData.putAll(resultData);
        		resultList.add(resultData);
                return resultList;
            }else if(errorMessage[0].contains("没有找到虚拟用户")){
            	resultData.put("X_RESULTCODE", "1003");
            	resultData.put("X_RSPCODE","1003");
            	resultData.put("X_RESULTINFO", errorMessage[0]);
            	resultData.putAll(resultData);
        		resultList.add(resultData);
                return resultList;
            }else if(errorMessage[0].contains("亲亲网已被注销")){
            	resultData.put("X_RESULTCODE", "1004");
            	resultData.put("X_RSPCODE","1004");
            	resultData.put("X_RESULTINFO", errorMessage[0]);
            	resultData.putAll(resultData);
        		resultList.add(resultData);
                return resultList;
            }else if(errorMessage[0].contains("您是亲亲网副号码")){
            	resultData.put("X_RESULTCODE", "1005");
            	resultData.put("X_RSPCODE","1005");
            	resultData.put("X_RESULTINFO", errorMessage[0]);
            	resultData.putAll(resultData);
        		resultList.add(resultData);
                return resultList;
            }else if(errorMessage[0].contains("查询不到主卡的亲亲关系")){
            	resultData.put("X_RESULTCODE", "1006");
            	resultData.put("X_RSPCODE","1006");
            	resultData.put("X_RESULTINFO", errorMessage[0]);
            	resultData.putAll(resultData);
        		resultList.add(resultData);
                return resultList;
            }else if(errorMessage[0].contains("您的亲亲网成员超出10个")){
            	resultData.put("X_RESULTCODE", "1007");
            	resultData.put("X_RSPCODE","1007");
            	resultData.put("X_RESULTINFO", errorMessage[0]);
            	resultData.putAll(resultData);
        		resultList.add(resultData);
                return resultList;
            }else {
            	throw e;
            }
		}
        resultData.putAll(callResult);
		resultList.add(resultData);
		return resultList;
    }

}
