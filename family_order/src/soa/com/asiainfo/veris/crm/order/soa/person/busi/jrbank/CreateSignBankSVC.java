package com.asiainfo.veris.crm.order.soa.person.busi.jrbank;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.RelationBankInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.HttpSvcTool;

/**
 * @CREATED
 */
public class CreateSignBankSVC extends CSBizService
{
    private static final long serialVersionUID = 7416263045210187376L;

    
    public IData loadInfos(IData data) throws Exception{
    	
    	//校验签约情况
    	checkRelation(data);
    	
    	IData returnData = new DataMap();
    	IData inparam = new DataMap();
    	IDataset signBanks = CommparaInfoQry.getCommByParaAttr("CSM", "339", getTradeEparchyCode());
    	IDataset holdList = CommparaInfoQry.getCommByParaAttr("CSM", "338", getTradeEparchyCode());
    	IDataset amountList = CommparaInfoQry.getCommByParaAttr("CSM", "337", getTradeEparchyCode());
    	
    	if(IDataUtil.isNotEmpty(holdList)){
//    		holdList sort
    	}
    	
    	returnData.put("signBanks", signBanks);
    	returnData.put("holdList", holdList);
    	returnData.put("amountList", amountList);
    	
    	return returnData;
    }
    
    public static void checkRelation(IData data) throws Exception{
    	
    	IDataset userRelationSet = RelationBankInfoQry.querySignBankByUid(data.getString("USER_ID"));
    	if(IDataUtil.isNotEmpty(userRelationSet)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经签约!");
    	}
    	
    }
    
    /**
     * 银行帐号校验
     * @param data
     * @return
     * @throws Exception 
     * @throws Exception
     */
    public IDataset checkSignBank(IData data) throws Exception{
    	
    	String pspTypeCode = data.getString("USER_IDENT_TYPE");
    	if("0".equals(pspTypeCode)||"1".equals(pspTypeCode)){
    		data.put("USER_IDENT_TYPE", "00");//身份证
		}else if("A".equalsIgnoreCase(pspTypeCode)){
			data.put("USER_IDENT_TYPE", "02");//护照
		}else if("C".equalsIgnoreCase(pspTypeCode)){
			data.put("USER_IDENT_TYPE", "04");//军官证
		}else {
			data.put("USER_IDENT_TYPE", "99");//军官证
		}
    	data.put("X_TRANS_CODE", "UIP_IsAccountSignable");
    	
    	IDataset result = HttpSvcTool.sendHttpData(data, "UIP_IsAccountSignable");
    	//ystem.out.println("UIP_IsAccountSignable result==="+result);
    	if(IDataUtil.isNotEmpty(result)){
    		if(!"0".equals(result.getData(0).getString("X_RESULTCODE"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口调用失败！"+result.getData(0).getString("X_RESULTINFO"));
    		}
    	}
    	return result;
    }
    
}
