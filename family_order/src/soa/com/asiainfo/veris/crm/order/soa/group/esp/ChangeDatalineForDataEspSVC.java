package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class ChangeDatalineForDataEspSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public IDataset changeUserDatalineOrder(IData map) throws Exception
    {
		IDataset result = new DatasetList();
		String ibsysId = map.getString("IBSYSID","");
//		String busiformId =  map.getString("BUSIFORM_ID","");
//		String cancelTag  = map.getString("CANCEL_TAG","");
//		String recordNum = map.getString("RECORD_NUM","");
		
		
		
		IData param = new DataMap();
		param.put("IBSYSID", ibsysId);
		param.put("RECORD_NUM", "0");
		IDataset groupInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(groupInfos))
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
        }
	    String productId = groupInfos.first().getString("PRODUCT_ID");
	    IData paramqry  = new DataMap();
		paramqry.put("IBSYSID", ibsysId);
    	IDataset productSubs = CSAppCall.call("SS.WorkformProductSubSVC.qryProductByIbsysid", paramqry);
		IDataset lineInfos = new DatasetList();
    	for(int i=0;i<productSubs.size();i++){
    	    IData lineInfo = new DataMap();
    		IData mapSub = productSubs.getData(i);
    		String recordNum = mapSub.getString("RECORD_NUM");
    	    IDataset lineEomsSubData =  new DatasetList();
	    	param.put("RECORD_NUM", recordNum);
	    	param.put("NODE_ID", "apply");
	    	lineEomsSubData =CSAppCall.call("SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", param);
		
    		for(int j=0;j<lineEomsSubData.size();j++){
        		IData attrInfo = lineEomsSubData.getData(j);
        		lineInfo.put(attrInfo.getString("ATTR_CODE","").replace("pam_NOTIN_", ""), attrInfo.getString("ATTR_VALUE",""));
        		
    		}
    		
    		
    		
    		lineInfos.add(lineInfo);
    		
    	}
    	
	    	
    	IData mapData = new DataMap(map);
    	mapData=DatalineEspUtil.getEosInfo(mapData,productId,"0898");
    	mapData.put("RSRV_STR7", "DATAHANGE");
		map.put("EOSDATA", new DatasetList(mapData));
    	
    	
    	map.put("PRODUCT_ID", productId);
    	map.put("LineInfos", lineInfos);
    	map.put("ROUTE_EPARCHY_CODE", "0898");
    	map.put("USER_EPARCHY_CODE", "0898");
    	
    	
    	
		
		
		result = CSAppCall.call("SS.ChangeMemberforDataElementSVC.crtOrder",map);
		
//		result = CSAppCall.call("SS.ChangeNetinMemberElementSVC.crtOrder",input);
		
		
		return result;
    }
	
	
}
