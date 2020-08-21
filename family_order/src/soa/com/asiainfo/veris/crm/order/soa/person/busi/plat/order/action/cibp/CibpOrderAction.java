
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.cibp;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

/**
 * Copyright: Copyright 2017 alik
 * 
 * @ClassName: CibpOrderAction.java
 * @Description: 0000退订魔百盒业务后，放开该号码可以在平台业务办理魔百和-基础包月（15元）-未来电视
 * @version: v1.0.0
 * @author: songxw
 */
public class CibpOrderAction implements IProductModuleAction
{
    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
    	PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
    	List<AttrTradeData> platSvcAttrList = pstd.getAttrTradeDatas();
    	if(PlatConstants.OPER_ORDER.equals(pstd.getOperCode())){
	    	//获取机顶盒ID
	    	String stbId = PlatUtils.getAttrValue("STBID", platSvcAttrList);
			IData param = new DataMap();
			param.put("USER_ID", uca.getUserId());
			param.put("SERIAL_NUMBER", uca.getSerialNumber());
			param.put("TRADE_EPARCHY_CODE", "0898");
			param.put("ROUTE_EPARCHY_CODE", "0898");
			//根据用户查询用户是否办理互联网电视服务信息
	    	IDataset internetTvSvc = CSAppCall.call("SS.DestroyTopSetBoxSVC.queryUserInternetTvPlatSvc4J", param);
	    	if(IDataUtil.isEmpty(internetTvSvc)){
	    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据手机号码和机顶盒ID未查询到资源信息,不允许在此界面办理！");
	    	}else{
	    		boolean hasTvSvc = false;
	    		for (int i = 0; i < internetTvSvc.size(); i++)
	            {
	                IData map = internetTvSvc.getData(i);
	                
	                //如果包含了互联网电视的服务
	                if(stbId.equals(map.getString("IMSI"))){
	                	hasTvSvc = true;
	                	break;
	                }
	            }
	    		if(!hasTvSvc){
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据手机号码和机顶盒ID未查询到资源信息,不允许在此界面办理！");
	    		}
	    	}
    	}
    }
}