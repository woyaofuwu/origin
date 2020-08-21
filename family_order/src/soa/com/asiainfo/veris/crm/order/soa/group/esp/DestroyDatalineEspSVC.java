package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class DestroyDatalineEspSVC  extends GroupOrderService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IDataset destroyGroupDatalineOrder(IData map) throws Exception
    {
		//入参
		String ibsysId = map.getString("IBSYSID");
		String recordNum = map.getString("RECORD_NUM");
		String userIdB = "";
		//查询集团用户信息
	    IData groupparam = new DataMap();
        groupparam.put("IBSYSID", ibsysId);
        groupparam.put("RECORD_NUM", "0");
        IDataset groupInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT", "SEL_BY_PK", groupparam, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(groupInfos))
        {
        	 CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        IData groupInfo = groupInfos.first();
		
		String userId = groupInfo.getString("USER_ID");//集团USER_id
		String productId = groupInfo.getString("PRODUCT_ID");
		if(productId.equals("7010")){
			IDataset result = CSAppCall.call("SS.ChangeDatalineEspSVC.changeDatalineOrder", map);
			return result;
		}else{
			groupparam.put("RECORD_NUM", recordNum);
			IDataset MebInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT_SUB", "SEL_BY_PK", groupparam, Route.getJourDb(BizRoute.getRouteId()));
	        if(DataUtils.isEmpty(MebInfos))
	        {
	        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号"+ibsysId+"查询产品TF_B_EOP_PRODUCT_SUB信息无数据！");
	        }
	        map.put("USER_ID_B", MebInfos.first().getString("USER_ID"));
		}
		 //1、查询集团信息
        IData param = new DataMap();
        param.put("USER_ID", userId);
        // 查询集团信息
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        
		 /**拼数据**/
		IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.USER_EPARCHY_CODE, grpUcaData.getUserEparchyCode());
        svcData.put("IF_BOOKING", "false");//esop来的是false
        svcData.put("REASON_CODE", map.getString("REASON_CODE"));//注销原因从esop获取
        svcData.put("REMARK", "ESOP改造除去BOSS页面"); //ESOP专线改造无页面了
        svcData.put("PRODUCT_ID", productId); // 产品ID
        svcData.put("AUDIT_STAFF_ID", "");//稽核信息，由于专线产品都不用稽核所以直接写死为空；
        //转换esop数据
        map = DatalineEspUtil.getEosInfo(map,productId,grpUcaData.getUserEparchyCode());
        svcData.put("EOS", new DatasetList(map));
       
        IDataset result = CSAppCall.call("SS.DestroyGroupMemberSVC.crtOrder", svcData);
      //  IDataset result = CSAppCall.call("CS.DestroyGroupUserSvc.destroyGroupUser", svcData);
		return result;
    }
	public IDataset destroyMemberDatalineOrder(IData map) throws Exception
    {
		String userId = map.getString("USER_ID");
		String productId = map.getString("PRODUCT_ID");
		
		 //1、查询集团信息
        IData param = new DataMap();
        param.put("USER_ID", userId);
        // 查询集团信息
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        
		 /**拼数据**/
		IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.USER_EPARCHY_CODE, grpUcaData.getUserEparchyCode());
        svcData.put("IF_BOOKING", "false");//esop来的是false
        svcData.put("REASON_CODE", map.getString("REASON_CODE"));//注销原因从esop获取
        svcData.put("REMARK", "ESOP改造除去BOSS页面"); //ESOP专线改造无页面了
        svcData.put("PRODUCT_ID", productId); // 产品ID
        svcData.put("AUDIT_STAFF_ID", "");//稽核信息，由于专线产品都不用稽核所以直接写死为空；
        svcData.put("EOS", new DatasetList(map));
        IDataset result = CSAppCall.call("SS.DestroyGroupMemberSVC.crtOrder", svcData);
		return result;
    }
	

}
