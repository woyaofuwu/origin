
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MebDataPckTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.flow.FlowInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.SpecialChangeProductReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;

public class SpecialChangeProductTrade extends com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade implements ITrade
{
	private static Logger logger = Logger.getLogger(com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.trade.SpecialChangeProductTrade.class);


	public void createBusiTradeData(BusiTradeData btd) throws Exception
	{

		SpecialChangeProductReqData request = (SpecialChangeProductReqData) btd.getRD();
		
		UcaData uca = request.getUca();
		
		List<ProductModuleData> userSelected = request.getProductElements();
		
		ProductTradeData nextProduct = uca.getUserNextMainProduct();

		ProductTimeEnv env = new ProductTimeEnv();
		if (request.isEffectNow())
		{
			env.setBasicAbsoluteStartDate(request.getAcceptTime());
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(request.getAcceptTime()));
		}
		// 元素拼串处理
	    ProductModuleCreator.createProductModuleTradeData(userSelected, btd, env);
	    
	    createCustMebDataPckTradeData(btd);
		
	}
	
	
	//流量经营：登记成员流量分配明细表,并扣减流量库存
	public void createCustMebDataPckTradeData(BusiTradeData btd) throws Exception
	{
		SpecialChangeProductReqData request = (SpecialChangeProductReqData) btd.getRD();
		String groupId = request.getGroupId();
		String transOrderId = request.getOutOrderId();// 平台订单流水
		String flowStr = request.getFlowItems();//
		IDataset flowsets = new DatasetList(flowStr);
		String datapckId ="";
		
		IData grpCustInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
		if(IDataUtil.isEmpty(grpCustInfo))
		{
			CSAppException.apperr(GrpException.CRM_GRP_131, groupId);
		}
		
		String grpcustId = grpCustInfo.getString("CUST_ID");
		
		IData paramdata = new DataMap();
		paramdata.put("ID", grpcustId);
		paramdata.put("GROUP_ID", groupId);
		paramdata.put("USER_ID", request.getUca().getUser().getUserId());
		paramdata.put("SERIAL_NUMBER", request.getUca().getUser().getSerialNumber());
		
		IDataset memDataset = decreaseFlowStock(paramdata,flowsets);
		
		for(int icount = 0;icount<memDataset.size();icount++)
		{
			SetMebDataPckTradeData(btd,memDataset.getData(icount),transOrderId);
		}
	}
	
	public void SetMebDataPckTradeData(BusiTradeData btd,IData mebData,String transOrderId) throws Exception
	{
		MebDataPckTradeData datapckData = new MebDataPckTradeData();
		
		datapckData.setId(mebData.getString("ID"));
		datapckData.setIdType("G");
		datapckData.setUserIdB(mebData.getString("USER_ID",""));
		datapckData.setSerialNumberB(mebData.getString("SERIAL_NUMBER",""));
		datapckData.setDatapckType("D");
		datapckData.setDatapckValue(mebData.getString("DATAPCK_VALUE"));
		datapckData.setDatapckCount(mebData.getString("DATAPCK_COUNT"));
		datapckData.setInstId(SeqMgr.getInstId());
		datapckData.setRelaInstId(mebData.getString("RELA_INST_ID"));
		datapckData.setStartDate(mebData.getString("PAK_START_DTAE"));
	    datapckData.setEndDate(mebData.getString("PAK_END_DTAE"));
	    datapckData.setModifyTag(BofConst.MODIFY_TAG_ADD);
	    datapckData.setRsrvStr1(transOrderId); //平台订单流水
	    datapckData.setRsrvStr2(mebData.getString("TRANS_ID")); //平台分配流水
	    btd.add(datapckData.getSerialNumberB(), datapckData);
	}
	
	
	
	
	
	synchronized public static IDataset decreaseFlowStock(IData param ,IDataset flowsets) throws Exception
	{
		String datapckId ="";
		IDataset mebDataset = new DatasetList();
		
		for(int i = 0 ;i<flowsets.size();i++)
		{
			
			boolean flag =false;
			String startData = flowsets.getData(i).getString("PAK_START_DTAE");
			String endData = flowsets.getData(i).getString("PAK_END_DTAE");
			String pckValue = flowsets.getData(i).getString("PAK_GPRS");//单个流量包的流量值
			String pckMoney = flowsets.getData(i).getString("PAK_MONEY");
			String pckCnt =  flowsets.getData(i).getString("PAK_NUM","1");
			int ipckCnt =Integer.parseInt(pckCnt);
			
			//按照流量包失效时间最近的优先分配
			
			//查询集团订购的流量包  
			//IData flowinfo = FlowInfoQry.getCustDatapckStock(instId);
			
			String grpcustId = param.getString("ID");
			String groupId = param.getString("GROUP_ID");
			IDataset datapckList = FlowInfoQry.getCustDatapckByIDValue(grpcustId,pckValue,pckMoney);
			if(IDataUtil.isEmpty(datapckList))
			{
				CSAppException.apperr(GrpException.CRM_GRP_895, groupId);
			}
			
			for(int j = 0 ;j<datapckList.size();j++)
			{
				IData mebData = new DataMap();
				mebData.putAll(param);
				mebData.put("PAK_START_DTAE", startData);
				mebData.put("PAK_END_DTAE", endData);
				mebData.put("TRANS_ID", flowsets.getData(i).getString("TRANS_ID"));
				String pckInstId = datapckList.getData(j).getString("INST_ID");
				
				//查库存
				IDataset pckStock = FlowInfoQry.getCustDatapckStock(grpcustId,pckInstId);
				if(IDataUtil.isEmpty(pckStock))
				{
					CSAppException.apperr(GrpException.CRM_GRP_896, groupId);
				}
				//库存总额
				String stockValue = pckStock.getData(0).getString("STOCK_VALUE");
				//库存数量
				String stockcnt = pckStock.getData(0).getString("STOCK_COUNT");
				//要分配的流量
				int pValue = ipckCnt*Integer.parseInt(pckValue);
				int icount = Integer.parseInt(stockcnt)-ipckCnt;
				if(Integer.parseInt(stockcnt) ==0 || Integer.parseInt(stockValue)==0)
				{
					continue;
				}
				else if(icount >= 0)
				{
//					datapckId = pckId;
					IData paramData = new DataMap();
//					paramData.put("DATAPCK_ID", pckId);
					paramData.put("INST_ID", pckInstId);
					paramData.put("ID", grpcustId);
					paramData.put("STOCK_VALUE", Integer.parseInt(stockValue)-pValue);
					paramData.put("STOCK_COUNT", Integer.parseInt(stockcnt)-ipckCnt);
					
					ChangeProductBean.decreaseFlowStock(paramData);
					
//					mebData.put("DATAPCK_ID", pckId);
					mebData.put("DATAPCK_COUNT", ipckCnt);
					mebData.put("DATAPCK_VALUE", pValue);
					mebData.put("RELA_INST_ID", pckInstId);
					
					mebDataset.add(mebData);
					
					flag = true;
					break;
				}
				else 
				{
//					datapckId = pckId;
					IData paramData = new DataMap();
//					paramData.put("DATAPCK_ID", pckId);
					paramData.put("INST_ID", pckInstId);
					paramData.put("ID", grpcustId);
					paramData.put("STOCK_VALUE", 0);
					paramData.put("STOCK_COUNT", 0);
					ChangeProductBean.decreaseFlowStock(paramData);
					ipckCnt= ipckCnt-Integer.parseInt(stockcnt);
					
//					mebData.put("DATAPCK_ID", pckId);
					mebData.put("DATAPCK_COUNT", stockcnt);
					mebData.put("DATAPCK_VALUE", pckValue);
					mebData.put("RELA_INST_ID", pckInstId);
					mebDataset.add(mebData);
					continue;
				}
			}
			
			if(!flag){
				CSAppException.apperr(GrpException.CRM_GRP_897);
			}
			
		}
		return mebDataset;
	}
		

}
