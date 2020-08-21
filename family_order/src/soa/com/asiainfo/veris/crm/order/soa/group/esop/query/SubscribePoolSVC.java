package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class SubscribePoolSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	
	public void saveSubscribePool(IData param) throws Exception
	{
		String ibsysid = IDataUtil.chkParam(param, "IBSYSID");
		
		IDataset subscribeData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
		if (DataUtils.isEmpty(subscribeData)) {
			return;
		}
		
		String productID = subscribeData.first().getString("BUSI_CODE");
		String groupID = subscribeData.first().getString("GROUP_ID","");
		
		IDataset eomsStateInfos = WorkformEomsStateBean.qryEomsStateByIbsysid(ibsysid);
		
		if (DataUtils.isEmpty(eomsStateInfos)) {
			return;
		}
		
		IDataset poolInfos = new DatasetList();
		String sysdate =SysDateMgr.getSysTime();
		
		for (int i = 0; i < eomsStateInfos.size(); i++) {
			IData poolData =  new DataMap();
			
			IData eomsInfo = eomsStateInfos.getData(i);
			String productNO = eomsInfo.getString("PRODUCT_NO");
			String recordNum = eomsInfo.getString("RECORD_NUM");
			String attrCode = "";
			 
			
//			IData attrInfo = WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysid, attrCode, recordNum);
//			
//			if (DataUtils.isEmpty(attrInfo)) {
//				continue;
//			}
//			
//			String attrValue = attrInfo.getString("ATTR_VALUE");
			
			
			poolData.put("POOL_ID", SeqMgr.getInstId());
			poolData.put("POOL_NAME","PRODUCTNO");
			poolData.put("POOL_VALUE",productNO);
			poolData.put("POOL_CODE",productID);
			poolData.put("POOL_LEVEL","");
			poolData.put("STATE","F");
			poolData.put("REL_IBSYSID",ibsysid);
			poolData.put("CREATE_DATE",sysdate);
			poolData.put("UPDATE_DATE",sysdate);
			poolData.put("REMARK","");
			poolData.put("RSRV_STR1",recordNum);
			poolData.put("RSRV_STR2",groupID);
			poolData.put("RSRV_STR3","");
			
			poolInfos.add(poolData);
		}
		
		SubscribePoolBean.insertSubscribePool(poolInfos);
	}
	
	public void updateSubscribePoolStateByApply(IData param) throws Exception {
		String ibsysid = param.getString("IBSYSID");
		//通过开通单的IBSYSID查询专线实例号
		IDataset productNos = WorkformAttrBean.selProductNoByIbsysid(param);
		if(IDataUtil.isNotEmpty(productNos)) {
			for (int i = 0; i < productNos.size(); i++) {
				String productNo = productNos.getData(i).getString("ATTR_VALUE");
				//通过开通单的IBSYSID查询关联的勘察单号
				IDataset attrInfos = WorkformAttrBean.qryAttrByIbsysid(ibsysid);
				if(IDataUtil.isNotEmpty(attrInfos)) {
					for (int j = 0; j < attrInfos.size(); j++) {
						IData attrInfo = attrInfos.getData(j);
						//如果是关联勘察单的开通单，更新勘察池表，否则不更新
						if(attrInfo.getString("ATTR_CODE").equals("CONFIBSYSID")) {
							String confCrmIbsysid = attrInfo.getString("ATTR_VALUE");
							IData input = new DataMap();
							input.put("REL_IBSYSID", confCrmIbsysid);
							input.put("POOL_VALUE", productNo);
							input.put("NODE_ID", param.getString("NODE_ID"));
							//通过勘察单号、专线实例号和节点名修改勘察池状态
							SubscribePoolBean.updateSubscribePoolStateByApply(input);
						}
					}
				}
			}
		}
	}
	public void saveSubscribePoolForIdc(IData param) throws Exception
	{
		String ibsysid = IDataUtil.chkParam(param, "IBSYSID");
		String bpmTempletID = param.getString("BPM_TEMPLET_ID","");

		IDataset subscribeData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
		if (DataUtils.isEmpty(subscribeData)) {
			return;
		}
		
		String productID = subscribeData.first().getString("BUSI_CODE");
		String groupID = subscribeData.first().getString("GROUP_ID","");
		IData inparam=new DataMap();
		inparam.put("IBSYSID", ibsysid);
		inparam.put("NODE_ID", "apply");
		String poolValue=null;
		String recordNum=null;
		String relIbsysid=null;
		IDataset attrList=WorkformAttrBean.getNewInfoByIbsysidAndNodeId(inparam);
		if (DataUtils.isNotEmpty(attrList)) {
			for (int i = 0; i < attrList.size(); i++) {
				IData attrData=attrList.getData(i);
				if(DataUtils.isNotEmpty(attrData)){
					if("IDC_SERIAL_NUMBER".equals(attrData.getString("ATTR_CODE",""))){
						poolValue=attrData.getString("ATTR_VALUE","");
						recordNum=attrData.getString("RECORD_NUM","");
					}else if("IDC_RelIbsysid".equals(attrData.getString("ATTR_CODE",""))){
						relIbsysid=attrData.getString("ATTR_VALUE","");
					}
				}
				
				
			}
		}
		if(poolValue==null){
			return;
		}
		String sysdate =SysDateMgr.getSysTime();
		if("EDIRECTLINECHECKIDC".equals(bpmTempletID)||"EDIRECTLINECHANGECHECKIDC".equals(bpmTempletID)){
			insertSubscribePool(poolValue,productID,ibsysid,sysdate,recordNum,groupID);
		}else if("EDIRECTLINEPREEMPTIONIDC".equals(bpmTempletID)){
			if(relIbsysid==null){
				return;
			}
			String nodeId=param.getString("NODE_ID");
			IData input = new DataMap();
			input.put("POOL_NAME", "IDC_SERIAL_NUMBER");
			input.put("REL_IBSYSID", relIbsysid);
			input.put("POOL_VALUE", poolValue);
			input.put("NODE_ID", nodeId);
			if(nodeId.equals("apply")) {//申请节点后将状态改为A
				input.put("STATE", "A");
	        }else if(nodeId.equals("End")) {//结束节点前将状态改为9
				input.put("STATE", "9");
	        }
			//通过勘察单号、专线实例号和节点名修改勘察池状态
//			SubscribePoolBean.updateSubscribePoolStateByApply(input);
			SubscribePoolBean.updateSubscribePoolState(input);
			if("End".equals(nodeId)){
				insertSubscribePool(poolValue,productID,ibsysid,sysdate,recordNum,groupID);
			}
			
		}else if("EDIRECTLINEOPENIDC".equals(bpmTempletID)||"EDIRECTLINECHANGEIDC".equals(bpmTempletID)){
			if(relIbsysid==null){
				return;
			}
			String nodeId=param.getString("NODE_ID");
			IData input = new DataMap();
			input.put("POOL_NAME", "IDC_SERIAL_NUMBER");
			input.put("REL_IBSYSID", relIbsysid);
			input.put("POOL_VALUE", poolValue);
			input.put("NODE_ID", nodeId);
			if(nodeId.equals("apply")) {//申请节点后将状态改为A
				input.put("STATE", "A");
	        }else if(nodeId.equals("End")) {//结束节点前将状态改为9
				input.put("STATE", "9");
	        }
			//通过勘察单号、专线实例号和节点名修改勘察池状态
//			SubscribePoolBean.updateSubscribePoolStateByApply(input);
			SubscribePoolBean.updateSubscribePoolState(input);

		}
	}
	private void insertSubscribePool(String poolValue,String productID,String ibsysid,String sysdate,String recordNum,String groupID)  throws Exception{
		IDataset poolInfos = new DatasetList();
		IData poolData =  new DataMap();
		String attrCode = "";
		poolData.put("POOL_ID", SeqMgr.getInstId());
		poolData.put("POOL_NAME","IDC_SERIAL_NUMBER");
		poolData.put("POOL_VALUE",poolValue);
		poolData.put("POOL_CODE",productID);
		poolData.put("POOL_LEVEL","");
		poolData.put("STATE","F");
		poolData.put("REL_IBSYSID",ibsysid);
		poolData.put("CREATE_DATE",sysdate);
		poolData.put("UPDATE_DATE",sysdate);
		poolData.put("REMARK","");
		poolData.put("RSRV_STR1",recordNum);
		poolData.put("RSRV_STR2",groupID);
		poolData.put("RSRV_STR3","");
		
		poolInfos.add(poolData);
		SubscribePoolBean.insertSubscribePool(poolInfos);
	}
	public void saveSubscribePoolToFirstIbsysidForIdc(IData param) throws Exception
	{
		String ibsysid = IDataUtil.chkParam(param, "IBSYSID");
		String nodeId=IDataUtil.chkParam(param, "NODE_ID");
		if("apply".equals(nodeId)){
			IDataset subscribeData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
			if (DataUtils.isEmpty(subscribeData)) {
				return;
			}
			
			String productID = subscribeData.first().getString("BUSI_CODE");
			String groupID = subscribeData.first().getString("GROUP_ID","");
			IData inparam=new DataMap();
			inparam.put("IBSYSID", ibsysid);
			inparam.put("NODE_ID", "apply");
			String orderNumFlag=null;
			String chooseFirstIbsysid=null;
			IDataset attrList=WorkformAttrBean.getNewInfoByIbsysidAndNodeId(inparam);
			if (DataUtils.isNotEmpty(attrList)) {
				for (int i = 0; i < attrList.size(); i++) {
					IData attrData=attrList.getData(i);
					if(DataUtils.isNotEmpty(attrData)){
						if("ORDER_OrderNumFlag".equals(attrData.getString("ATTR_CODE",""))){
							orderNumFlag=attrData.getString("ATTR_VALUE","");
						}else if("ORDER_ChooseFirstIbsysid".equals(attrData.getString("ATTR_CODE",""))){
							chooseFirstIbsysid=attrData.getString("ATTR_VALUE","");
						}
					}
					
					
				}
			}
			
			if("1".equals(orderNumFlag)){
				chooseFirstIbsysid=ibsysid;
			}else if(orderNumFlag==null||chooseFirstIbsysid==null){
				return;
			}
			String sysdate =SysDateMgr.getSysTime();
			IDataset poolInfos = new DatasetList();
			IData poolData =  new DataMap();
			poolData.put("POOL_ID", SeqMgr.getInstId());
			poolData.put("POOL_NAME","ORDER_OrderNumFlag");
			poolData.put("POOL_VALUE",ibsysid);
			poolData.put("POOL_CODE",productID);
			poolData.put("POOL_LEVEL","");
			poolData.put("STATE","F");
			poolData.put("REL_IBSYSID",chooseFirstIbsysid);
			poolData.put("CREATE_DATE",sysdate);
			poolData.put("UPDATE_DATE",sysdate);
			poolData.put("REMARK","");
			poolData.put("RSRV_STR1","");
			poolData.put("RSRV_STR2",groupID);
			poolData.put("RSRV_STR3","");
			poolInfos.add(poolData);
			SubscribePoolBean.insertSubscribePool(poolInfos);
		}else{
			IData qrySubscribePoolParam1=new DataMap();
	        qrySubscribePoolParam1.put("POOL_NAME", "ORDER_OrderNumFlag");
//	        qrySubscribePoolParam1.put("STATE", "F");
	        qrySubscribePoolParam1.put("POOL_VALUE", ibsysid);
	        IDataset qrySubscribePoolParamList1=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam1);
	        if(IDataUtil.isEmpty(qrySubscribePoolParamList1)||qrySubscribePoolParamList1.size()==0||
	        		IDataUtil.isEmpty(qrySubscribePoolParamList1.getData(0))){
	        }else{
		        //通过勘察单号、专线实例号和节点名修改勘察池状态
	        	IData qrySubscribePoolParam=new DataMap();
		        qrySubscribePoolParam.put("POOL_NAME", "ORDER_OrderNumFlag");
		        qrySubscribePoolParam.put("REL_IBSYSID", qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID"));
		        if("eomsProess".equals(nodeId)){
			        qrySubscribePoolParam.put("STATEOLD", "F");
			        qrySubscribePoolParam.put("STATE", "A");
					SubscribePoolBean.updateSubscribePoolState(qrySubscribePoolParam);
		        }else if("End".equals(nodeId)){//只修改自身
//			        qrySubscribePoolParam.put("STATEOLD", "A");
			        qrySubscribePoolParam.put("POOL_VALUE", ibsysid);
			        qrySubscribePoolParam.put("STATE", "9");
					SubscribePoolBean.updateSubscribePoolState(qrySubscribePoolParam);
		        }
	        }
	        
		
		
		}
			
	}
	
}