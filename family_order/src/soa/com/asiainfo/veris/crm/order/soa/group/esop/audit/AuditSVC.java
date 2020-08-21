package com.asiainfo.veris.crm.order.soa.group.esop.audit;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;

public class AuditSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public void submit(IData input) throws Exception{
		String bpmTempletId =  input.getString("BPM_TEMPLET_ID");
		String ibsysId =  input.getString("IBSYSID");
		String nodeId = input.getString("NODE_ID");
		IDataset attrInfo  = getInfos(bpmTempletId,ibsysId);//获取专线条数
		insertTable(attrInfo,ibsysId,nodeId);
		
	}
	
	public  IDataset saveAuditAttr(IData param) throws Exception{
    	IDataset workformAttrList = new DatasetList();
    	Iterator<String> itr = param.keySet().iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			String value = param.getString(key);
			IData dAttr = new DataMap();
			dAttr.put("ATTR_CODE", key);
			dAttr.put("ATTR_VALUE", value);
			dAttr.put("RECORD_NUM",  param.getString("RECORD_NUM"));
			workformAttrList.add(dAttr);
		}
		String nodeId =param.getString("NODE_ID");
		String subIbsysId = SeqMgr.getSubIbsysId();
		
		for(int i = 0 ; i < workformAttrList.size() ; i ++)
		{
			IData workformAttr = workformAttrList.getData(i);
			workformAttr.put("SUB_IBSYSID", subIbsysId);
			workformAttr.put("IBSYSID", param.getString("IBSYSID"));
			workformAttr.put("SEQ", SeqMgr.getAttrSeq());
            workformAttr.put("GROUP_SEQ", "0");
            workformAttr.put("NODE_ID", nodeId);
            
			//设置公共参数
			setCommonInfos(workformAttr);
		}
		
		WorkformAttrBean.insertWorkformAttr(workformAttrList);
		return workformAttrList;
	}
    
    private static void setCommonInfos(IData params) throws Exception
    {
		String groupId = params.getString("GROUP_ID","");
		String eparchCode = getVisit().getLoginEparchyCode();
		if(StringUtils.isNotEmpty(groupId))
		{
			IData custInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
			if(DataUtils.isNotEmpty(custInfo))
			{
				eparchCode = custInfo.getString("EPARCHY_CODE", "");
			}
		}
		if(StringUtils.isNotEmpty(eparchCode))
		{
			eparchCode = getVisit().getLoginEparchyCode();
		}
		
		params.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		params.put("ACCEPT_TIME", SysDateMgr.getSysTime());
		params.put("UPDATE_TIME", SysDateMgr.getSysTime());
		params.put("INSERT_TIME", SysDateMgr.getSysTime());
		params.put("CITY_CODE", getVisit().getCityCode());
		params.put("EPARCHY_CODE", eparchCode);
		params.put("DEPART_ID", getVisit().getDepartId());
		params.put("DEPART_NAME", getVisit().getDepartName());
		params.put("STAFF_ID", getVisit().getStaffId());
		params.put("STAFF_NAME", getVisit().getStaffName());
		params.put("STAFF_PHONE", getVisit().getSerialNumber());
    }
   

	
	/**
	 * 获取合同信息
	 * @param bpmTempletId
	 * @param ibsysId
	 * @return
	 * @throws Exception
	 */
	public IDataset getInfos(String bpmTempletId,String ibsysId) throws Exception
	{
    	//查询节点ID
    	IData param = new DataMap();
    	param.put("BPM_TEMPLET_ID", bpmTempletId);
    	param.put("NODE_TYPE", "3");
    	param.put("VALID_TAG", "0");
    	IDataset templetDatas = CSAppCall.call("SS.TempletGroupBizSVC.queryInfoByNodeType", param);
   		if (IDataUtil.isEmpty(templetDatas))
        {
   			CSAppException.apperr(CrmCommException.CRM_COMM_103,"模板ID错误！");
        }
   		
   		//查询SUB_IBSYSID
   		IData nodeParam = new DataMap();
   		nodeParam.put("IBSYSID", ibsysId);
   		nodeParam.put("NODE_ID", templetDatas.getData(0).getString("NODE_ID"));
   		IDataset nodeDatas = CSAppCall.call("SS.WorkformNodeSVC.qryNodeByIbsysidNodeTimeDesc", nodeParam);
   		if (IDataUtil.isEmpty(nodeDatas))
        {
   			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单流水号无节点信息！");
        }
   		String subIbsysId = nodeDatas.first().getString("SUB_IBSYSID");
   		
   		IData attrParam = new DataMap();
   		attrParam.put("IBSYSID", ibsysId);
   		attrParam.put("SUB_IBSYSID", subIbsysId);
   		attrParam.put("NODE_ID", templetDatas.getData(0).getString("NODE_ID"));
   		IDataset attrDatas = CSAppCall.call("SS.WorkformAttrSVC.qryInfoBySubIbsysid", attrParam);
   		if (IDataUtil.isEmpty(attrDatas))
        {
   			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单流水号无合同信息！");
        }   		
   		//查询专线列表   		
		return attrDatas;
	    	
	 }
	
	/**
	 * 根据attr信息获取两边内容,并插表
	 * @param bpmTempletId
	 * @param ibsysId
	 * @return
	 * @throws Exception
	 */
	public void insertTable(IDataset attrInfo,String ibsysId,String nodeId) throws Exception
	{
		int lineNo = attrInfo.getData(attrInfo.size()-1).getInt("RECORD_NUM");//取专线条数
		String subIbsysId = attrInfo.first().getString("SUB_IBSYSID");
		IDataset insertvalues = new DatasetList(); 
		for(int i =1 ;i <= lineNo; i++ )//专线RECORD_NUM从第一条开始
		{
			IData map = new DataMap();
			map.put("IBSYSID", ibsysId);
			map.put("SUB_IBSYSID", subIbsysId);
			map.put("RECORD_NUM", i);
			IDataset attrDatas = CSAppCall.call("SS.WorkformAttrSVC.qryAttrBySubIbsysidAndRecordNum", map);
			IData order = CSAppCall.callOne("SS.WorkformProductSVC.qryEopProductByIbsysId", map);
			String tradeId = order.getString("TRADE_ID");
			IData tradeMap = new DataMap();
			tradeMap.put("TRADE_ID", tradeId);
			tradeMap.put("RSRV_VALUE_CODE", "N001");
			IData other = CSAppCall.callOne("SS.OtherInfoQrySVC.qryOtherByTradeId", tradeMap);
			IDataset insertvalue = compareAttrAndOrder(attrDatas,other,nodeId,i);
			insertvalues.addAll(insertvalue);
		}
		saveWorkformAttr(insertvalues,nodeId,ibsysId);
		
	}
	
	/**
	 * 比较每一条类容
	 * @param bpmTempletId
	 * @param ibsysId
	 * @return
	 * @throws Exception
	 */
	public IDataset compareAttrAndOrder(IDataset attrDatas,IData other,String nodeId,int lineNo) throws Exception
	{
		IDataset insertvalue = new DatasetList();
		for(int i = 0; i< attrDatas.size();i++){
			IData attr  = attrDatas.getData(i);
			if("NOTIN_LINE_NO".equals(attr.getString("ATTR_CODE"))){ //专线实例号
				IData productNO = new DataMap();
				productNO.put("ATTR_CODE", "NOTIN_LINE_NO");
				productNO.put("ATTR_VALUE", attr.getString("ATTR_VALUE")+"|"+other.getString("RSRV_STR9"));
				productNO.put("RECORD_NUM", lineNo);
				insertvalue.add(productNO);
			}
			if("NOTIN_RSRV_STR1".equals(attr.getString("ATTR_CODE"))){ //专线带宽（兆）
				IData productNO = new DataMap();
				productNO.put("ATTR_CODE", "NOTIN_RSRV_STR1");
				productNO.put("ATTR_VALUE", attr.getString("ATTR_VALUE")+"|"+other.getString("RSRV_STR2"));
				productNO.put("RECORD_NUM", lineNo);
				insertvalue.add(productNO);
			}
			if("NOTIN_RSRV_STR2".equals(attr.getString("ATTR_CODE"))){ //专线价格
				IData productNO = new DataMap();
				productNO.put("ATTR_CODE", "NOTIN_RSRV_STR2");
				productNO.put("ATTR_VALUE", attr.getString("ATTR_VALUE")+"|"+other.getString("RSRV_STR3"));
				productNO.put("RECORD_NUM", lineNo);
				insertvalue.add(productNO);
			}
			if("NOTIN_RSRV_STR3".equals(attr.getString("ATTR_CODE"))){ //安装调试费（元）
				IData productNO = new DataMap();
				productNO.put("ATTR_CODE", "NOTIN_RSRV_STR3");
				productNO.put("ATTR_VALUE", attr.getString("ATTR_VALUE")+"|"+other.getString("RSRV_STR4"));
				productNO.put("RECORD_NUM", lineNo);
				insertvalue.add(productNO);
			}
			if("NOTIN_RSRV_STR4".equals(attr.getString("ATTR_CODE"))){ //一次性通信服务费（元）
				IData productNO = new DataMap();
				productNO.put("ATTR_CODE", "NOTIN_RSRV_STR4");
				productNO.put("ATTR_VALUE", attr.getString("ATTR_VALUE")+"|"+other.getString("RSRV_STR5"));
				productNO.put("RECORD_NUM", lineNo);
				insertvalue.add(productNO);
			}
		}
		return insertvalue;
	}
	
	
	private void saveWorkformAttr(IDataset workformAttrs,String nodeId,String ibsysid) throws Exception
	{
		String subIbsysid = SeqMgr.getSubIbsysId();
		String acceptMonth = SysDateMgr.getCurMonth();
		String updateTime = SysDateMgr.getSysTime();
		if(DataUtils.isEmpty(workformAttrs))
		{
			return;
		}
		
		for(int i = 0 ; i < workformAttrs.size() ; i ++)
		{
			IData workformAttr = workformAttrs.getData(i);
			workformAttr.put("SUB_IBSYSID", subIbsysid);
			workformAttr.put("IBSYSID", ibsysid);
			workformAttr.put("SEQ", SeqMgr.getAttrSeq());
            workformAttr.put("GROUP_SEQ", "0");
            workformAttr.put("NODE_ID", nodeId);
            workformAttr.put("ACCEPT_MONTH", acceptMonth);
            workformAttr.put("UPDATE_TIME",updateTime);
		}
		
		WorkformAttrBean.insertWorkformAttr(workformAttrs);
	}
}
