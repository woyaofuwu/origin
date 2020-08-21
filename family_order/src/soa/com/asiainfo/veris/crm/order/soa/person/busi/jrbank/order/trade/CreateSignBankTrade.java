package com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationBankTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.jrbank.order.requestdata.CreateSignBankReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.HttpSvcTool;

public class CreateSignBankTrade extends BaseTrade implements ITrade {

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		// TODO Auto-generated method stub
		CreateSignBankReqData rd = (CreateSignBankReqData) bd.getRD();
		
		MainTradeData mainData = bd.getMainTradeData();
		mainData.setRemark("签约");
		
		if(rd.getIsIntf().equals("1")){//接口处理
			mainData.setExecTime(SysDateMgr.END_DATE_FOREVER);//预处理
			createRelationBankData(bd,new DataMap());
		}else{

			//调用银行的签约接口,获取台账的相关字段值 
			IData params = new DataMap();
			String pspTypeCode = rd.getUca().getCustomer().getPsptTypeCode();
			//证件类型判断传值 VIP卡和武装警察证没有
			
			if("0".equals(pspTypeCode)||"1".equals(pspTypeCode)){
				params.put("USER_IDENT_TYPE", "00");//身份证
			}else if("A".equalsIgnoreCase(pspTypeCode)){
				params.put("USER_IDENT_TYPE", "02");//护照
			}else if("C".equalsIgnoreCase(pspTypeCode)){
				params.put("USER_IDENT_TYPE", "04");//军官证
			}else {
				params.put("USER_IDENT_TYPE", "99");//军官证
			}
			params.put("RECV_BANK", rd.getRecvBank());
			params.put("USER_TYPE", "01");
			params.put("USER_ID", rd.getUca().getSerialNumber());
			params.put("X_TRANS_CODE", "UIP_BankSign");
			//ystem.out.println("UIP_BankSign params==============="+params);
			IDataset data = HttpSvcTool.sendHttpData(params, "UIP_BankSign");
			/*if(IDataUtil.isEmpty(data)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "签约接口出错");
			}*/
			if(IDataUtil.isNotEmpty(data)){
	    		if(!"0".equals(data.getData(0).getString("X_RESULTCODE"))){
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口调用失败！"+data.getData(0).getString("X_RESULTINFO"));
	    		}
	    	}
			
			//ystem.out.println("UIP_BankSign data==============="+data);
			createRelationBankData(bd , data.getData(0));
		}
			
	}

	 public void createRelationBankData(BusiTradeData bd,IData data) throws Exception{
		 
		 CreateSignBankReqData rd = (CreateSignBankReqData) bd.getRD();
		 
		 UcaData uca = rd.getUca();
		 RelationBankTradeData bankData = new RelationBankTradeData();
		 
		 bankData.setUserId(uca.getUserId());
		 int length = uca.getUserId().length(); 
		 bankData.setPartitionId(uca.getUserId().substring(length-4, length));
		 bankData.setSerialNumber(uca.getSerialNumber());
		 bankData.setSubId(data.getString("SUB_ID"));
		 bankData.setBankId(rd.getRecvBank());
		 bankData.setUserAccount(data.getString("USER_ACCOUNT"));
		 
//		 bankData.setPayType(rd.getPayType());
		 String paytype = rd.getPayType();
		 if(StringUtils.isBlank(paytype)){
			 paytype = "0";
		 }
		 bankData.setPayType(paytype);
		 
		 bankData.setAccountCat(data.getString("ACCOUNT_CAT"));
		 bankData.setSubTime(data.getString("SUB_TIME"));
		 
		 String rechThreshold = rd.getRechThreshold();
		 String rechAmount = rd.getRechAmount();
		 
		 if(StringUtils.isNotBlank(rechThreshold)){
			 rechThreshold = Integer.parseInt(rechThreshold)*100 + "";
		 }
		 if(StringUtils.isNotBlank(rechAmount)){
			 rechAmount = Integer.parseInt(rechAmount)*100 + "";
		 }
		 bankData.setRechThreshold(rechThreshold);
		 bankData.setRechAmount(rechAmount);
		 bankData.setInstId(SeqMgr.getInstId());
		 bankData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		 bankData.setStartDate(SysDateMgr.getSysDate());
		 bankData.setEndDate(SysDateMgr.END_DATE_FOREVER);
		 bankData.setPayMode(rd.getPreType());
		 bankData.setUserType("01");
		 bd.add(uca.getSerialNumber(), bankData);
		 
	 }
	 
}
