package com.asiainfo.veris.crm.order.soa.person.busi.interroamday;

import java.text.DateFormat;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class InterRoamPackageTradeBean extends CSBizBean{

	/**
     * 赠送关系查询
     * @author xuejy
     * @throws Exception
     */
    public IDataset GiftRelationQuery(IData idata) throws Exception{
    	IDataset results = new DatasetList();
    	IDataset giftResults = IBossCall.interRoamGiftQuery(idata.getString("SERIAL_NUMBER"),idata.getString("OPR_TYPE"));
    	if(IDataUtil.isNotEmpty(giftResults)){
    		for(int i = 0; i < giftResults.size(); i++){
    			IData giftResult = giftResults.getData(i);
    			IData result = new DataMap();
        		result.putAll(giftResult);
        		String prodId = giftResult.getString("PROD_ID","");
        		IDataset commparaSet = CommparaInfoQry.queryCommparaInfoByParaCode2("CSM", "2742", prodId);
        		if(IDataUtil.isNotEmpty(commparaSet)){
        			String discntCode = commparaSet.getData(0).getString("PARAM_CODE");
        			String discntName = commparaSet.getData(0).getString("PARAM_NAME");
        			result.put("DISCNT_CODE",discntCode);
        			result.put("DISCNT_NAME",discntName);
        		}
        		String effTime = SysDateMgr.date2String(SysDateMgr.string2Date(giftResult.getString("SALES_EFF_TIME",""), SysDateMgr.PATTERN_STAND_SHORT),SysDateMgr.PATTERN_STAND);
        		result.put("SALES_EFF_TIME", effTime);
        		results.add(result);
        	}
    	}
    	return results;
    }
    /**
     * 订购关系查询
     * @author xuejy
     * @throws Exception
     */
    public IDataset interRoamPackageQuery(IData idata) throws Exception{
    	IData result = new DataMap();
    	IDataset results = new DatasetList();
//    	IDataset GSMLevels = CSAppCall.call("SS.InterRoamingSVC.roamBirthQry", idata);
    	IDataset GSMLevels = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GSM_BY_SN", idata);
    	String curState = "";
    	if(IDataUtil.isNotEmpty(GSMLevels)){
    		IData GSMLevel = GSMLevels.getData(0);
    			curState = GSMLevel.getString("USER_CLASS");
    			result.put("GSM_STATE", curState);
    	}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到用户全球通等级");
		}
    	IDataset packageInfos = CommparaInfoQry.qryInterRoamPackageByParaCode6("CSM", "2742", result.getString("GSM_STATE"));
    	if(IDataUtil.isEmpty(packageInfos)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "未配置用户当前等级叠加包产品！");
    	}else{
    		String prodId = packageInfos.getData(0).getString("PARA_CODE2");
    		String discntCode = packageInfos.getData(0).getString("PARAM_CODE");
    		String discntName = packageInfos.getData(0).getString("PARAM_NAME");
    		result.put("DISCNT_CODE", discntCode);
    		result.put("PROD_ID", prodId);
    		result.put("DISCNT_NAME", discntName);
    		results.add(result);
    		IData param = new DataMap();
    		param.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
    		IDataset giftResult = CSAppCall.call("SS.InterRoamDaySVC.interRoamQuery", param);
        	if(IDataUtil.isNotEmpty(giftResult) && StringUtils.isNotBlank(giftResult.getData(0).getString("PROD_ID"))){
        		//判断订购关系对应的全球通等级是否等于现有等级，不等于的话判断是否升级，赠送补送包
        		boolean checkGift = true;
    			IDataset commparaSet = new DatasetList();
    			for(int i = 0; i < giftResult.size(); i++){
    				IData ibossData =  giftResult.getData(i);
    				String giftProId = ibossData.getString("PROD_ID");
    				IDataset temp = CommparaInfoQry.queryCommparaInfoByParaCode2("CSM", "2742", giftProId);
    				if(IDataUtil.isNotEmpty(temp) && StringUtils.isNotBlank(temp.getData(0).getString("PARA_CODE6"))){
    					if(temp.getData(0).getString("PARA_CODE6").contains(",")){
    						//如果已订购补送包，则判断是否订购了当前等级的补送包
    						String state = "";
    						if(temp.getData(0).getString("PARA_CODE6").contains(";")){
    							state = temp.getData(0).getString("PARA_CODE6").split(",")[2];
    						}else{
    							state = temp.getData(0).getString("PARA_CODE6").split(",")[1];
    						}
    						if(curState.equals(state)){
    							checkGift = false;
    						}
    					}else{
    						commparaSet.add(temp.getData(0));
    					}
    					
    				}
    				if(giftProId.equals(prodId)){
            			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户已领取当前等级对应叠加包，无法再次领取！");
    				}
    			}
    			if(IDataUtil.isNotEmpty(commparaSet) && checkGift){
    				int t = 0;
    				for(int i = 0; i < commparaSet.size(); i++){
    					if(t < Integer.parseInt(commparaSet.getData(i).getString("PARA_CODE6"))){
    						t = Integer.parseInt(commparaSet.getData(i).getString("PARA_CODE6"));
    					}
    				}
    				String gsmState = String.valueOf(t);
        			if(StringUtils.isNotBlank(gsmState)){
        				String giftState = gsmState+","+curState;
        				if(gsmState.equals("1") && curState.equals("2")){
        					//银卡升金卡
        					giftState = gsmState+","+curState+";2,3";
        					
        				}else if(gsmState.equals("2") && curState.equals("3")){
        					//金卡升白金卡
        					giftState = "1,2;"+gsmState+","+curState;
        				}
        				//查询补送产品
        				IDataset curPackageInfos = CommparaInfoQry.qryInterRoamPackageByParaCode6("CSM", "2742", giftState);
        				if(IDataUtil.isNotEmpty(curPackageInfos)){
        					String prodId1 = curPackageInfos.getData(0).getString("PARA_CODE2");
        		    		String discntCode1 = curPackageInfos.getData(0).getString("PARAM_CODE");
        		    		String discntName1 = curPackageInfos.getData(0).getString("PARAM_NAME");
        		    		IData result1 = new DataMap();
        		    		result1.put("DISCNT_CODE", discntCode1);
        		    		result1.put("PROD_ID", prodId1);
        		    		result1.put("DISCNT_NAME", discntName1);
        		    		results.add(result1);
        				}
    				}
    			}
        	}
    	}
    	return results;
    }
}
