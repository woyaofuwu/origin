package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr; 
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
/**
 * 无手机宽带
 * 停机90天后拆机，则对于光猫的数据进行丢失操作，押金沉淀。
 * @author zyc
 *
 */
public class NoPhoneUpModemStateFee implements ITradeAction {

	protected static Logger log = Logger.getLogger(NoPhoneUpModemStateFee.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String phoneDestroyType = btd.getRD().getPageRequestData().getString("NO_PHONE_90DAY_DESTORY","");
		if(phoneDestroyType != null && "90".equals(phoneDestroyType))
		{ 
			String serialNumber = btd.getRD().getUca().getSerialNumber();
	        String userId=btd.getRD().getUca().getUserId();
			 
	    	//查询租赁光猫信息
	        IDataset userOthersInfos = UserOtherInfoQry.getModemRentByCodeUserId(userId,"FTTH");
			if(IDataUtil.isNotEmpty(userOthersInfos))
    		{
				String rsrvTag1 = userOthersInfos.getData(0).getString("RSRV_TAG1","");
				String rsrvTag2 = userOthersInfos.getData(0).getString("RSRV_TAG2","");
				if(rsrvTag1 != null && "0".equals(rsrvTag1))
				{
					if(rsrvTag2 != null && !"3".equals(rsrvTag2))
					{
						String remark = "无手机宽带停机90天自动拆机，光猫丢失,押金沉淀";
						//只有租赁时才记录光猫的状态为丢失                                                    
						List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
			            
						for(int i = 0 ; i < otherTradeDatas.size() ; i++)
		                {
		                    OtherTradeData data = otherTradeDatas.get(i);
		                    String rsrvValueCode = data.getRsrvValueCode() ;
		                    //光猫押金
		                    if(rsrvValueCode != null && rsrvValueCode.equals("FTTH"))
		                    {
			                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);
								data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
	
								data.setRsrvTag2("4");//记录光猫丢失状态
	
								//押金沉淀
								String rsrvStr7 = userOthersInfos.getData(0).getString("RSRV_STR7","");
								String rsrvStr2 = userOthersInfos.getData(0).getString("RSRV_STR2","");
								if(rsrvStr7 != null && rsrvStr7.equals("0"))
								{ 
									IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
									if(IDataUtil.isNotEmpty(acctInfo))
									{
										IData inparams=new DataMap();
									    inparams.put("USER_ID", userId);
									    inparams.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
									    inparams.put("SERIAL_NUMBER", serialNumber); 
									    inparams.put("TRADE_FEE", rsrvStr2);
					                    IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUser_2(userId, "84073842");//调测费模式没有押金，不处理押金  modify_by_duhj_kd
					                    if(IDataUtil.isEmpty(discntInfo)){
	                                        IData inAcct = AcctCall.AMBackFee(inparams);	                                        
	                                        if(IDataUtil.isNotEmpty(inAcct))
	                                        {
	                                            String result = inAcct.getString("RESULT_CODE","");
	                                            if("0".equals(result))
	                                            {
	                                                //修改押金状态已沉淀
	                                                data.setRsrvStr7("3");//押金已沉淀
	                                                remark += "成功";
	                                            }
	                                            else
	                                            {
	                                                log.error("欠费销号,光猫押沉淀,调用账务押金沉淀接口返回错误:错误代码:" + result + ";错误信息:" + inAcct.getString("RESULT_INFO","") );
	                                                
	                                                remark += "失败";
	                                            }
	                                        } 
					                    }else {
					                        remark = "调测费模式不需要押金沉淀";
                                        }

									}
								}
								data.setRemark(remark);
							//btd.add(serialNumber, data);
		                	}
		                }
					}
				}
    		} 
		} 
	} 
}
