package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.action.reg;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.requestdata.NoPhoneWideUserActiveRequestData;

/**
 * 针对新大陆生成激活工单费用
 * @author Administrator
 *
 */
public class NoPhoneCreateFeeAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		NoPhoneWideUserActiveRequestData widenetProductRD = (NoPhoneWideUserActiveRequestData) btd.getRD();
		String sn=btd.getRD().getUca().getSerialNumber();
		if(!sn.startsWith("KD_")){
        	sn="KD_"+sn;
        }
		String tradeId=widenetProductRD.getCreateUserTradeId();
		IDataset tradeList = BroadBandInfoQry.qryTradeHisInfoByTradeId(tradeId);
		if(IDataUtil.isNotEmpty(tradeList)){
			  //查询宽带信息台账
			  IDataset wideNetList=BroadBandInfoQry.qryTradeWidenetInfoByTradeId(tradeId);
			  //新大陆过来的或者是标记为RSRV_TAG2=N的为先装后付
			  if(tradeList.getData(0).getString("IN_MODE_CODE","").equals("SD")
					  ||((IDataUtil.isNotEmpty(wideNetList))&&("N".equals(wideNetList.getData(0).getString("RSRV_TAG2"))))){
				    //查询是否缴费成功
				    IDataset otherFees=UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(tradeList.getData(0).getString("USER_ID"),"NOPHONE_WNET_PAY_TAG");
				    if(IDataUtil.isNotEmpty(otherFees)&&otherFees.getData(0).getString("RSRV_STR1","").equals("1")){
					  	//调费用接口
			        	IDataset tradeDiscntList = BroadBandInfoQry.qryTradeDiscntInfoByTradeId(tradeList.getData(0).getString("TRADE_ID"));
			        	int baseFee=0;
			        	int modeFee=0;
			        	if(IDataUtil.isNotEmpty(tradeDiscntList)){
			        		for(int i=0;i<tradeDiscntList.size();i++){
			        			if("0".equals(tradeDiscntList.getData(i).getString("MODIFY_TAG"))){
					        		IData feeParam=new DataMap();
					            	feeParam.put("ROUTE_EPARCHY_CODE", "0898");
					            	feeParam.put("TRADE_TYPE_CODE", "680");
					            	feeParam.put("PRODUCT_ID", tradeList.getData(0).getString("PRODUCT_ID"));
					            	feeParam.put("ELEMENT_ID", tradeDiscntList.getData(i).getString("DISCNT_CODE"));
					            	feeParam.put("ELEMENT_TYPE_CODE", "D");
					            	IDataset feeList=CSAppCall.call("CS.ProductFeeInfoQrySVC.getProductFeeInfo", feeParam);
					            	if(IDataUtil.isNotEmpty(feeList)){
					            		for(int j=0;j<feeList.size();j++){
					            			if("9021".equals(feeList.getData(j).getString("FEE_TYPE_CODE"))){
					            				baseFee+=Integer.parseInt(feeList.getData(j).getString("FEE","0"));
											}
					            		}
					            	}
			        			}
			        		}
			        	}
			        	
			        	//是否有光猫
			        	if("0".equals(tradeList.getData(0).getString("RSRV_STR2"))){
			        		modeFee=10000;
			        	}
			        	
			        	//构造预存费用
			        	FeeTradeData feeTradeData = new FeeTradeData();
						feeTradeData.setFee(baseFee+"");
						feeTradeData.setFeeMode("2");
						feeTradeData.setFeeTypeCode("9021");
						feeTradeData.setOldfee(baseFee+"");
						feeTradeData.setUserId(btd.getRD().getUca().getUserId());
						feeTradeData.setRemark("新大陆APP无手机宽带开户费用写入");
						btd.add(btd.getRD().getUca().getSerialNumber(), feeTradeData);
			        	
						//构造光猫费用
			        	FeeTradeData modeFeeTradeData = new FeeTradeData();
			        	modeFeeTradeData.setFee(modeFee+"");
			        	modeFeeTradeData.setFeeMode("2");
			        	modeFeeTradeData.setFeeTypeCode("9002");
			        	modeFeeTradeData.setOldfee(modeFee+"");
			        	modeFeeTradeData.setUserId(btd.getRD().getUca().getUserId());
			        	modeFeeTradeData.setRemark("新大陆APP无手机宽带开户费用写入");
						btd.add(btd.getRD().getUca().getSerialNumber(), modeFeeTradeData);
						
						//支付方式
						IData input=btd.getRD().getPageRequestData();
						input.put("X_PAY_MONEY_CODE", "T");
						
						//构造支付总费用
						/*PayMoneyTradeData payMoneyTradeData = new PayMoneyTradeData();
						payMoneyTradeData.setPayMoneyCode("0");
						payMoneyTradeData.setMoney(allTotalTransFee);
						payMoneyTradeData.setRemark("新大陆APP无手机宽带开户费用写入");
						payMoneyTradeData.setOrderId(tradeList.getData(0).getString("ORDER_ID",""));
						btd.add(btd.getRD().getUca().getSerialNumber(), payMoneyTradeData);*/
						
						//构造主台账费用
						MainTradeData mainTrade=btd.getMainTradeData();
						if(mainTrade!=null){
							mainTrade.setFeeState("1");
							mainTrade.setAdvancePay((baseFee+modeFee)+"");
						}
			        	
				  }else{
					  CSAppException.apperr(CrmCommException.CRM_COMM_888, "该工单["+tradeId+"]没有缴费成功记录！");
				  }
			  }
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_888, "该工单["+tradeId+"]无手机宽带开户业务工单信息不存在！");
		}
	}

}
