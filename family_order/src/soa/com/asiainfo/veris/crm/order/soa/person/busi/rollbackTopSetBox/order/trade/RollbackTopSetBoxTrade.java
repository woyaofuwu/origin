package com.asiainfo.veris.crm.order.soa.person.busi.rollbackTopSetBox.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.rollbackTopSetBox.order.requestdata.RollbackTopSetBoxRequestData;


public class RollbackTopSetBoxTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		RollbackTopSetBoxRequestData req=(RollbackTopSetBoxRequestData)bd.getRD();;
		String serialNumber=req.getSerialNumber();
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = userInfo.getString("USER_ID");
		
		IData boxInfo =UserResInfoQry.queryRollbackTopSetBox(userId).first();
		//清除资源信息
//		ResTradeData resTD = new ResTradeData(boxInfo);
//		resTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
//		resTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
//		bd.add(req.getUca().getSerialNumber(), resTD);
		
		
		/*
		 * 如果在宽带撤单时，存在生效的魔百和业务的退库操作，就需要退机顶盒
		 */
//		String rsrvTag1=boxInfo.getString("RSRV_TAG1","");
//		if(rsrvTag1.equals("1")){
//			IData returnParam=new DataMap();
//			returnParam.put("RES_NO", boxInfo.getString("IMSI"));
//			returnParam.put("PARA_VALUE1", serialNumber);
//			returnParam.put("SALE_FEE", boxInfo.getString("RES_FEE",""));
//			returnParam.put("PARA_VALUE7", "0");
//			returnParam.put("DEVICE_COST", boxInfo.getString("DEVICE_COST","0"));
//			returnParam.put("X_CHOICE_TAG", "1");
//			returnParam.put("RES_TYPE_CODE", "4");
//			returnParam.put("PARA_VALUE11", boxInfo.getString("UPDATE_TIME"));
//			returnParam.put("PARA_VALUE14", boxInfo.getString("RES_FEE","0"));
//			returnParam.put("PARA_VALUE17", boxInfo.getString("RES_FEE","0"));
//			returnParam.put("PARA_VALUE1", serialNumber);
//			returnParam.put("USER_NAME", bd.getRD().getUca().getCustomer().getCustName());
//			returnParam.put("STAFF_ID", boxInfo.getString("UPDATE_STAFF_ID"));
//			returnParam.put("TRADE_ID", boxInfo.getString("INST_ID"));
//			
//			IDataset returnResult=HwTerminalCall.returnTopSetBoxTerminal(returnParam);
//			if(IDataUtil.isEmpty(returnResult)){
//				CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
//			}else{
//				String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
//				if(!resultCode.equals("0")){
//					CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
//							getData(0).getString("X_RESULTINFO",""));
//				}
//			}
//		}
		
		
		UcaData uca=bd.getRD().getUca();
		
		/*
		 * 如果用户在办理魔百和的时候收取了押金，就需要退
		 */
		String rsrvNum2=boxInfo.getString("RSRV_NUM2","");
		if(rsrvNum2!=null&&!rsrvNum2.equals("")){
			int rsrvNum2Int=Integer.parseInt(rsrvNum2);
			
			if(rsrvNum2Int>0){
				//获取费用信息
				String money="20000";
				IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
				if(IDataUtil.isNotEmpty(moneyDatas)){
					money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
				}
				
				//调用账务的接口进行押金返回
				IData params=new DataMap(); 
				params.put("SERIAL_NUMBER_1", serialNumber);
				params.put("SERIAL_NUMBER_2", serialNumber);
				params.put("DEPOSIT_CODE_1", "9016");
				params.put("DEPOSIT_CODE_2", "0");
				params.put("FEE", money);
				params.put("REMARK", "魔百和退机押金转预存");
		   		params.put("USER_ID_IN", uca.getUserId()); 
		   		params.put("USER_ID_OUT", uca.getUserId()); 
		   		//调用接口，将【押金】——>【现金】
				AcctCall.depositeToPhoneMoney(params);
			}
			
		}
		
		
		//删除撤单的临时表数据
		UserResInfoQry.delRollbackTopSetBox(userId);
		
	}
	

	
}
