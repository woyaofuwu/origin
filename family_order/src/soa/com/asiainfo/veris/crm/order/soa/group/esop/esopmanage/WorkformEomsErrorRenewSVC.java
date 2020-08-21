package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.IsspConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;
//勘查及开通的错单重派
public class WorkformEomsErrorRenewSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	private IData eosCom = new DataMap();

	public void errorReSend(IData inparam) throws Exception
	{
		eosCom = inparam;
		
		String serialNo = eosCom.getString("serialNo", "");
		IDataset workformEmosInfos = WorkformEomsBean.qryworkformEOMSBySerialNo(serialNo);
		
		if(DataUtils.isEmpty(workformEmosInfos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SERIALNO:"+serialNo+",未获取到TF_B_EOP_EOMS数据！");
		}
		IData workformEmosInfo = workformEmosInfos.first();
		String ibsysid = workformEmosInfo.getString("IBSYSID");
		String subIbsysid = workformEmosInfo.getString("SUB_IBSYSID");
		String groupSeq = workformEmosInfo.getString("GROUP_SEQ","0");
		String recordNum = workformEmosInfo.getString("RECORD_NUM","-1");
		String operType= workformEmosInfo.getString("OPER_TYPE", "");
		int sheetType = workformEmosInfo.getInt("SHEETTYPE");
		
		//更新detail表状态
		updateWorkformEomsState(ibsysid,operType,sheetType,recordNum);

		//更新流程业务信息表
		updateWorkformAttr(ibsysid,subIbsysid,groupSeq,recordNum);
		

        String operTye = eosCom.getString("opertype", "");
        if("openErrorRenewWorkSheet".equals(operTye)){
            //如果是开通错单，需要修改trade订单表数据,serialNo开通时记录的是trade表的trade_id
        	syncTradeInfo(ibsysid, serialNo);
    		
        }else if("confirmErrorRenewWorkSheet".equals(operTye)){
            //如果是勘查错单，需要修改STEP表状态
    		IData inData = new DataMap();
    		inData.put("BUSIFORM_NODE_ID",workformEmosInfo.getString("BPM_ID"));
        	CSAppCall.call("SS.WorkformStepSVC.updState", inData);
        }
	}
	
	private void updateWorkformEomsState(String ibsysid,String operType,int sheetType,String recordNum) throws Exception
	{
		IDataset configParam = IsspConfigQry.getParamValue("EOMS_BUSI_STATE_"+sheetType, operType);
		if(DataUtils.isEmpty(configParam)){
			return;
		}
		String newState = configParam.first().getString("PARAMVALUE");

		WorkformEomsStateBean.updEomsStateByIbsysidRecordNum(ibsysid, recordNum, newState);
	}

	private void updateWorkformAttr(String ibsysid,String subIbsysid,String groupSeq,String recordNum) throws Exception
	{
		IDataset workformAttrs = eosCom.getDataset("opDetail");

		if(DataUtils.isEmpty(workformAttrs))
		{
			return;
		}
		//删除原来的参数信息
		WorkformAttrBean.delEopAttrBySubibsysidGroupseq(subIbsysid, groupSeq, recordNum);
		IDataset otherAttr = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysid, groupSeq, "0");
		int maxSeq = 1;
		if(DataUtils.isNotEmpty(otherAttr))
		{
			maxSeq = otherAttr.size()+1;
		}
		IDataset attrs = new DatasetList();
		for(int i = 0 ; i < workformAttrs.size() ; i ++)
		{
			IData workformAttr = workformAttrs.getData(i);
			IData attr = new DataMap();
			attr.put("RECORD_NUM", recordNum);
			attr.put("ATTR_CODE", workformAttr.getString("fieldEnName"));
			attr.put("ATTR_NAME", workformAttr.getString("fieldChName"));
			attr.put("ATTR_VALUE", workformAttr.getString("fieldContent"));
			attr.put("SUB_IBSYSID", subIbsysid);
			attr.put("IBSYSID", ibsysid);
			attr.put("SEQ", SeqMgr.getAttrSeq());
			attr.put("GROUP_SEQ", groupSeq);
            attr.put("NODE_ID", "newWorkSheet");
            attr.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
			attr.put("UPDATE_TIME", SysDateMgr.getSysTime());
			attrs.add(attr);
		}
		//新增修改后的参数信息
		if(DataUtils.isNotEmpty(attrs))
		{
			WorkformAttrBean.insertWorkformAttr(attrs);
		}

	}
	private void syncTradeInfo(String ibsysid,String serialNo) throws Exception{
        //如果是开通错单，需要修改trade订单表数据
		IDataset workformAttrs = eosCom.getDataset("opDetail");

		if(DataUtils.isEmpty(workformAttrs))
		{
			return;
		}
		//获取路由
		IDataset workformDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
		
		if(DataUtils.isEmpty(workformDataset)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:"+ibsysid+",未获取到TF_B_EOP_SUBSCRIBE数据！");
		}
		String eparchyCode = workformDataset.getData(0).getString("EPARCHY_CODE");

		//查询trade参数信息
		IDataset tradeAttrs = new DatasetList();//TradeAttrInfoQry.getTradeAttrByTradeIdInstType(serialNo,"P",eparchyCode);
		
		if(DataUtils.isEmpty(tradeAttrs))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID:"+serialNo+",未获取到TF_B_TRADE_ATTR数据！");
		}
		IDataset unTradeAttrList = new DatasetList();

		//参数比对
		for(int i = 0 ; i < workformAttrs.size() ; i ++)
		{
			IData workformAttr = workformAttrs.getData(i);
			boolean isExist = false;
			for(int j = 0 ; j < tradeAttrs.size() ; j ++)
			{
				IData tradeAttr = tradeAttrs.getData(j);
				if(workformAttr.getString("fieldEnName","").equals(tradeAttr.getString("ATTR_CODE",""))){
					isExist = true;
					if(!workformAttr.getString("fieldContent","").equals(tradeAttr.getString("ATTR_VALUE",""))){
						//值变了，更新tradeattr表
						tradeAttr.put("ATTR_VALUE", workformAttr.getString("fieldContent"));
//						TradeAttrInfoQry.updateAttrValue(tradeAttr,eparchyCode);
					}
					break;
				}
			}
			if(!isExist){
				unTradeAttrList.add(workformAttr);
			}
		}
		IData temAttr = tradeAttrs.first();
		for(int a=0,sizea=unTradeAttrList.size();a<sizea;a++){
			//将trade表中不存在的属性加进去
			IData attr = new DataMap();
			attr.putAll(temAttr);
			attr.put("INST_ID", "");//SeqMgr.getInstIdEpachy(eparchyCode));
			attr.put("ATTR_VALUE", unTradeAttrList.getData(a).getString("fieldContent"));
			attr.put("ATTR_CODE", unTradeAttrList.getData(a).getString("fieldEnName",""));
			attr.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
			attr.put("UPDATE_TIME", SysDateMgr.getSysTime());
			TradeAttrInfoQry.insertAttrInfo(attr);
		}
		IDataset trade = new DatasetList();//TradeInfoQry.getMainTradeByPk(serialNo,"0",eparchyCode);
		if(DataUtils.isEmpty(trade)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID:"+serialNo+",未获取到TF_B_TRADE数据！");
		}
		//更新trade表状态
		TradeInfoQry.updateTradeStateByTradeId(serialNo, trade.first().getString("ACCEPT_MONTH"), "0","0",eparchyCode);
		//更新order表状态
//		OrderInfoQry.updateStateByOrderId(trade.first().getString("ORDER_ID"), "0", eparchyCode);
	}

}
