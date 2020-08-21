package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonerollbacktopsetbox.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonerollbacktopsetbox.order.requestdata.NoPhoneRollbackTopSetBoxRequestData;

public class NoPhoneRollbackTopSetBoxTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		NoPhoneRollbackTopSetBoxRequestData req=(NoPhoneRollbackTopSetBoxRequestData)bd.getRD();;
		String serialNumber=req.getSerialNumber();
		/*if(!"KD_".equals(serialNumber.substring(0, 3)))
        {
			serialNumber="KD_" + serialNumber;
        }*/
		
		//根据服务号  ， 查询用户信息
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(userInfo == null){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"数据异常：用户不存在!");
		}
		
		String userId = userInfo.getString("USER_ID");
		
		//根据用户编号  ， 查询    TF_R_TOPSET_ROLLBACK  表 
		IData boxInfo =UserResInfoQry.queryRollbackTopSetBox(userId).first();

		UcaData uca=bd.getRD().getUca();
		
		/*
		 * 如果用户在办理魔百和的时候收取了押金，就需要退
		 */
		String rsrvNum2=boxInfo.getString("RSRV_NUM2","");//押金
		if(rsrvNum2!=null&&!rsrvNum2.equals("")){
			int rsrvNum2Int=Integer.parseInt(rsrvNum2);
			
			if(rsrvNum2Int>0){
				//获取费用信息
				String money="20000";
				
				//查询   td_s_commpara（通用参数表） ？
				IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
				
				if(IDataUtil.isNotEmpty(moneyDatas)){
					money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
				}
				
				//调用服开的给 现金接口  【押金】 ————>【现金】
				//调用账务的接口进行押金返回
				IDataset inAccts =  AcctCall.backFee(uca.getUserId(), req.getTradeId(), "15000", "9016", "16001", money);
		   		IData inAcct = inAccts.first();
				if(IDataUtil.isEmpty(inAcct)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口报错！");
				}else{
					String resultCode=inAcct.getString("RESULT_CODE","");
					if(!resultCode.equals("0")){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口错误："+inAcct.getString("RESULT_INFO",""));
					}
				}
			}
		}
		
		//删除撤单的临时表数据  （ 删除 TF_R_TOPSET_ROLLBACK表中该用户的数据）
		UserResInfoQry.delRollbackTopSetBox(userId);
	}
}
