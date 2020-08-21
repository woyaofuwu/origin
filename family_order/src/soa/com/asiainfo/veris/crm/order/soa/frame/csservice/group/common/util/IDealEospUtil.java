
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeTempletReleInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;

public class IDealEospUtil
{
	public static final String SUCCESS_CODE			= "0";			//处理成功
	public static final String SUCCESS_INFO			= "success";	//成功时返回信息
	public static final String FAIL_CODE				= "-1";			//处理失败
	
	

	/**
	 * 查询Attr表数据转换成offerchaspecs格式
	 * @param subIbsysid
	 * @param recordNum
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryAttrTranOfferChaSpecs(String subIbsysid,String recordNum) throws Exception
	{
		IDataset offerChaSpecs = new DatasetList();
		IDataset attrList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysid, recordNum) ;
		if (IDataUtil.isNotEmpty(attrList)) 
		{
			
			for (int j = 0; j < attrList.size(); j++) 
			{
				IData temp = new DataMap();
				IData attrMap = attrList.getData(j);
				temp.put("ATTR_VALUE", attrMap.getString("ATTR_VALUE"));
				temp.put("ATTR_CODE", attrMap.getString("ATTR_CODE"));
				temp.put("ATTR_NAME", attrMap.getString("ATTR_NAME"));
				offerChaSpecs.add(temp);
			} 
		}
		return offerChaSpecs;
	}
	
	/**
	 *  根据流程标识转化CRM处理标识
	 * @param data
	 * @throws Exception
	 */
	public static String transOperCode(String  operType) throws Exception
	{
		String operCode = EcConstants.ACTION_UPDATE;//变更
		if("20".equals(operType))//开通
		{
			operCode = EcConstants.ACTION_CREATE;
		}
		else if ("25".equals(operType))//注销
		{
			operCode = EcConstants.ACTION_DELETE;
		}
		else//变更 
		{
			operCode = EcConstants.ACTION_UPDATE;
		}
		return operCode;
	}
	
	
	public static String getTransferParam(IData workFormData) 
	{
		StringBuilder transferParamBuilder = new StringBuilder();
		if(!"".equals(workFormData.getString("SUB_IBSYSID", "")))
		{
			transferParamBuilder.append("&SUB_IBSYSID=").append(workFormData.getString("SUB_IBSYSID", ""));
		}
		if(!"".equals(workFormData.getString("IBSYSID", "")))
		{
			transferParamBuilder.append("&IBSYSID=").append(workFormData.getString("IBSYSID", ""));
		}
		if(!"".equals(workFormData.getString("NODE_ID", "")))
		{
			transferParamBuilder.append("&NODE_ID=").append(workFormData.getString("NODE_ID", ""));
		}
		if(!"".equals(workFormData.getString("BPM_TEMPLET_ID", "")) || !"".equals(workFormData.getString("FLOW_ID", "")) )
		{
			transferParamBuilder.append("&BPM_TEMPLET_ID=").append(workFormData.getString("BPM_TEMPLET_ID", workFormData.getString("FLOW_ID", "")));
		}
		if(!"".equals(workFormData.getString("MAIN_TEMPLET_ID", "")) || !"".equals(workFormData.getString("FLOW_MAIN_ID", "")) )
		{
			transferParamBuilder.append("&MAIN_TEMPLET_ID=").append(workFormData.getString("MAIN_TEMPLET_ID", workFormData.getString("FLOW_MAIN_ID", "")));
		}
		return transferParamBuilder.toString();
	}

	
	/**
	 * liaolc
	 * 2018-03-13
	 * @param iBossData
	 * @param transParam
	 * @return
	 * @throws Exception
	 */
	public static void TransEosToTradeExtData(IData map,IData extData) throws Exception 
	{
		extData.put("ATTR_CODE", "ESOP");
		extData.put("ATTR_VALUE", map.getString("IBSYSID"));
		extData.put("RSRV_STR1", map.getString("NODE_ID"));
		extData.put("RSRV_STR2", map.getString("BPM_TEMPLET_ID"));
		extData.put("RSRV_STR3", map.getString("BUSIFORM_OPER_TYPE"));
		extData.put("RSRV_STR7", map.getString("GROUP_ID"));
		extData.put("RSRV_STR10", "EOS");
	}
	/**
	 * liaolc
	 * 2018-03-13
	 * @param iBossData
	 * @param transParam
	 * @return
	 * @throws Exception
	 */
	public static void TransTradeExtToEosData(String mainTradeId,	IData esopData) throws Exception 
	{
		// 2- 获取ESOP信息
		IDataset esopTradeExts = TradeInfoQry.getTradeForGrpBBoss(mainTradeId);
		if (IDataUtil.isNotEmpty(esopTradeExts)) 
		{
			IData data = esopTradeExts.getData(0);
			esopData.put("IBSYSID", data.getString("ATTR_VALUE",""));
			esopData.put("NODE_ID", data.getString("RSRV_STR1",""));
			esopData.put("BPM_TEMPLET_ID", data.getString("RSRV_STR2",""));
		}
	}
	
	/**
	 * liaolc
	 * 2018-03-13
	 * @param iBossData
	 * @param transParam
	 * @return
	 * @throws Exception
	 */
	public static void repNextNodeId(IData esopData) throws Exception 
	{
		String nodeId = esopData.getString("NODE_ID");
		String bpmTempletId = esopData.getString("BPM_TEMPLET_ID");
		if (StringUtils.isNotBlank(nodeId)&&StringUtils.isNotBlank(bpmTempletId))
		{
			IDataset dataset = EweNodeTempletReleInfoQry.qryInfoByBpmTempletNode(bpmTempletId, nodeId, EcEsopConstants.STATE_VALID);
			if (IDataUtil.isNotEmpty(dataset))
			{
				String nextNodeId = dataset.getData(0).getString("NEXT_NODE_ID");
				esopData.put("NODE_ID", nextNodeId);
			}
		}
	}
	

	/**
	 * 获取下这个节点
	 * liaolc
	 * 2018-03-13
	 * @param iBossData
	 * @param transParam
	 * @return
	 * @throws Exception
	 */
	public static String getNextNodeId(String nodeId, String bpmTempID) throws Exception 
	{
		String nextNodeId ="";
		IDataset dataset = EweNodeTempletReleInfoQry.qryInfoByBpmTempletNode(bpmTempID, nodeId, EcEsopConstants.STATE_VALID);
		if (IDataUtil.isNotEmpty(dataset))
		{
			nextNodeId = dataset.getData(0).getString("NEXT_NODE_ID");
		}
		return nextNodeId;
	}
	
	/**
	 * liaolc
	 * 2018-03-13
	 * @param iBossData
	 * @param transParam
	 * @return
	 * @throws Exception
	 */
	public static boolean dealTradeFinishCallEospAction(IData mainTrade,	IData extTrade) throws Exception 
	{
        // 跨省专线需要等最后一个专线完工时才调用esop的send接口，因此需要通过产品专线的最后一笔单子触发商品的sendesop
        String skipEosTag = mainTrade.getString("RSRV_STR1", ""); 
        if (skipEosTag.equals("SKIPSENDESOP"))
        { // 商品的RSRV_STR1上打了个跳过标记，不调用ESOP的send接口
        	 return false;
        }
        if (skipEosTag.equals("SENDEOS"))
        {// 跨省专线的RSRV_STR1上SENDEOS表明是最后一笔专线（BBOSS下发归档的时，在专线的台账上RSRV_STR1=SENDEOS RSRV_STR2=商品的tradeid）
            String tradeId = mainTrade.getString("RSRV_STR2", "");
            if (StringUtils.isBlank(tradeId))
            {
            	return false;
            }
        }
        callEopDriveService(extTrade);
        return false;
	}

	public static void callEopDriveService(IData extTrade) throws Exception 
	{
        IData esopData = new DataMap();
		String nodeId = extTrade.getString("RSRV_STR1","");
		String bpmTempID = extTrade.getString("RSRV_STR2","");
		String nextNodeId = getNextNodeId(nodeId, bpmTempID);
		
		esopData.put("IBSYSID", extTrade.getString("ATTR_VALUE",""));
		esopData.put("NODE_ID",nextNodeId );
		CSAppCall.callEop(EcConstants.SAVEANDSEND, esopData);
	}
	
	/**
	 * 获取下这个节点 
	 * 2018-03-19
	 * @param ibSysId
	 * @param nodeId
	 * @return
	 * @throws Exception
	 */
	public static String queryNextNodeId(String ibSysId,String nodeId) throws Exception 
	{
		System.out.println("=====nodeId======="+nodeId);
		String nextNodeId =""; 
		IData param1 = new DataMap();
		param1.put("IBSYSID", ibSysId);
		IDataset dataset1  = CSAppCall.call("SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid", param1);
		System.out.println("=====dataset1======="+dataset1);
		if(IDataUtil.isNotEmpty(dataset1)){
			String bpmTempID = dataset1.getData(0).getString("BPM_TEMPLET_ID");
			IData param = new DataMap();
			param.put("BPM_TEMPLET_ID", bpmTempID);
			param.put("NODE_ID", nodeId);
			param.put("VALID_TAG", EcEsopConstants.STATE_VALID); 
			System.out.println("=====param======="+param);
			IDataset dataset = CSAppCall.call("SS.EweNodeTempletReleInfoQrySVC.qryInfoByBpmTempletNode", param);
			System.out.println("=====dataset====lz==="+dataset);
			if (IDataUtil.isNotEmpty(dataset))
			{
				nextNodeId = dataset.getData(0).getString("NEXT_NODE_ID");
			}
		}
		return nextNodeId;
	}
	
	public static void setCommonInfos(IData params) throws Exception
    {
		params.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		params.put("ACCEPT_TIME", SysDateMgr.getSysTime());
		params.put("UPDATE_TIME", SysDateMgr.getSysTime());
		params.put("INSERT_TIME", SysDateMgr.getSysTime());
    }
}
