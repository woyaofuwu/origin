package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.gpsp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class GpspAction implements IProductModuleAction {

	@Override
	public void executeProductModuleAction(ProductModuleTradeData dealPmtd,
			UcaData uca, BusiTradeData btd) throws Exception {
		PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
		PlatOfficeData officeData = ((PlatSvcData) pstd.getPmd()).getOfficeData();
		
		if (null != officeData && "46".equals(officeData.getBizTypeCode()))
        {
			IData param = new DataMap();
    		param.put("SQL_REF", "SEL_BY_USERID_NOW");
    		param.put("USER_ID", uca.getUserId());
    		IDataset userSvcs = PlatInfoQry.getUserSvc(param); //获取用户所有服务

    		param.clear();
    		param.put("USER_ID", uca.getUserId());
    		IDataset userPlatSvcs = PlatInfoQry.getUserPlatSvc(param,"SEL_BY_USERID_NEW"); //获取用户所有平台服务
    		//合并服务列表
    		if(null != userPlatSvcs){
    			userSvcs.addAll(userPlatSvcs);
    		}
    		
    	    String volte_flag = "0";
    	    //遍历用户服务列表，校验是否存在智能网业务.
    	    for(int i = 0 ; i < userSvcs.size() ; i++){
    	    	IData svcInfo = userSvcs.getData(i);
    	    	//判断用户是否VOLTE用户，依据用户是否订购了 190 服务来判断。
    	    	if("0".equals(volte_flag) && "190".equals(svcInfo.getString("SERVICE_ID"))){
    	    		volte_flag = "1";
    	    		break;
    	    	}
    	    }
    	    
    	    if("1".equals(volte_flag)){
    	    	CSAppException.apperr(PlatException.CRM_PLAT_74, "订购volte的用户不允许订购彩印业务");
    	    }
        }
	}
}
