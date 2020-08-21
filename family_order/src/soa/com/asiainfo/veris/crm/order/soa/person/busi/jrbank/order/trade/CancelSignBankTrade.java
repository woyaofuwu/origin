package com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationBankTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.RelationBankInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.requestdata.CancelSignBankReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.HttpSvcTool;

public class CancelSignBankTrade extends BaseTrade implements ITrade {

	/* (non-Javadoc)
	 * @see com.ailk.bof.execute.impl.BaseTrade#createBusiTradeData(com.ailk.bof.data.tradedata.BusiTradeData)
	 */
	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		// TODO Auto-generated method stub
		CancelSignBankReqData rd = (CancelSignBankReqData) bd.getRD();
		
		String selectValues = rd.getSelectValues(); 
		
		if(rd.getIsIntf().equals("1")){//接口处理
			IDataset dataset = new DatasetList();
			IData data = new DataMap();
			data.put("SUB_ID", selectValues);
			dataset.add(data);
			createDelTradeRelationBankData(bd, dataset);
		}else{
		
			IDataset dataSet = new DatasetList();
			IData tempData = null;
			String[] selSignArr = selectValues.split(",");
			for(int i=0;i<selSignArr.length;i++){
				String temp = selSignArr[i];
				String[] everArr = temp.split("\\$");
				String subId = everArr[0];
				String recvBank = everArr[1];
				tempData = new DataMap();
				tempData.put("SUB_ID", subId);
				dataSet.add(tempData);
				//ystem.out.println("接口调用前=subId=="+subId+"   recvBank"+recvBank);
				invokeBankIntf(bd,subId,recvBank);
			}
			
			createDelTradeRelationBankData(bd,dataSet);
			MainTradeData mainData = bd.getMainTradeData();
			mainData.setRemark("解约");
		}
			
	}

	public void invokeBankIntf(BusiTradeData bd,String subId, String recvBank) throws Exception{
		CancelSignBankReqData rd = (CancelSignBankReqData) bd.getRD();
		IData params = new DataMap();
		params.put("USER_TYPE", "01");//01手机号码;02飞信号;03	宽带用户号;04	Email
		params.put("USER_ID", rd.getUca().getSerialNumber());
		params.put("RECV_BANK", recvBank);
		params.put("SUB_ID", subId);
		params.put("X_TRANS_CODE", "UIP_BankSignDestroy");
		//UIP_BankSignDestroy
		IDataset result = HttpSvcTool.sendHttpData(params, "UIP_BankSignDestroy");
		if(IDataUtil.isNotEmpty(result)){
    		if(!"0".equals(result.getData(0).getString("X_RESULTCODE"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口调用失败！"+result.getData(0).getString("X_RESULTINFO"));
    		}
    	}
		
	}
		
		

	 public void createDelTradeRelationBankData(BusiTradeData bd,IDataset dataset) throws Exception{
		 
		 CancelSignBankReqData rd = (CancelSignBankReqData) bd.getRD();
		 UcaData uca = rd.getUca();
		 String userId=uca.getUserId();
		 
		 for(int i=0;i<dataset.size();i++){
			 
			 IData bankinfo = RelationBankInfoQry.querySignBankByUidSid(userId, dataset.getData(i).getString("SUB_ID"));
			 RelationBankTradeData bankData = new RelationBankTradeData(bankinfo);
			 
			 bankData.setModifyTag(BofConst.MODIFY_TAG_UPD);
			 bankData.setEndDate(SysDateMgr.getSysDate());
			 bd.add(uca.getSerialNumber(), bankData);
		 }
	 }
	   
}
