package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetdestroy.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetdestroy.order.requestdata.DestroyNoPhoneWideUserNowRequestData;

/**
 * 无手机宽带拆机释放光猫资源
 * @author yuyj3
 *
 */
public class ReleaseModemAction implements ITradeAction {

	@Override
	public void  executeAction(BusiTradeData btd) throws Exception 
	{
		DestroyNoPhoneWideUserNowRequestData rd = (DestroyNoPhoneWideUserNowRequestData)btd.getRD();
		
		String userId = rd.getUca().getUserId();
		
		if (rd.getModermReturn().equals("1"))
		{
		    IDataset modemInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "FTTH");;
	        
	        if(IDataUtil.isNotEmpty(modemInfos))
	        {
	            for(int i=0;i<modemInfos.size();i++)
	            {
                    IData modem = modemInfos.getData(i);
                    
                    String rsrvTag1 = modem.getString("RSRV_TAG1","");
                    if(rsrvTag1 != null && !"3".equals(rsrvTag1) && !"".equals(rsrvTag1))
                    {
                        String resNo = modem.getString("RSRV_STR1",""); //光猫串码
                        if(resNo != null && !"".equals(resNo))
                        {
                            String serialNumber = btd.getRD().getUca().getSerialNumber();
                            if(serialNumber != null && !"".equals(serialNumber) && serialNumber.startsWith("KD_"))
                            {
                                serialNumber = serialNumber.substring(3,serialNumber.length());
                            }
                            //调用光猫释放接口，释放光猫预占信息 
                            IData param = new DataMap();
                            param.put("RES_NO", resNo);//终端串号 
                            param.put("PARA_VALUE1", serialNumber);//PARA_VALUE1 
                            param.put("SALE_FEE", 0);//销售费用 非销售时为0 
                           
                            param.put("PARA_VALUE7", 0);//代办费 
                           
                            param.put("TRADE_ID", SeqMgr.getTradeIdFromDb());//台账流水 
                            param.put("X_CHOICE_TAG", "1");//0-终端销售,1—终端销售退货 
                            param.put("ES_TYPE_CODE", "4");//资源类型,终端的传入4 
                            param.put("CONTRACT_ID", modem.getString("RSRV_STR12",""));//销售订单号 ,销售时占用的流水
                            param.put("PRODUCT_MODE", "0");//全网统一操盘合约机销售标志 
                            param.put("X_RES_NO_S", resNo);//终端串号 
                            param.put("X_RES_NO_E", resNo);//终端串号 
                            param.put("PARA_VALUE13", "0");//是否有销售酬金 0-没有 1-有 
                            param.put("PARA_VALUE14", "0");//裸机价格 
                            param.put("PARA_VALUE15", "0");//客户购机折让价格 
                            param.put("PARA_VALUE16", "0");//客户预存话费 
                            param.put("PARA_VALUE17", "0");//客户实际购机款 
                            param.put("PARA_VALUE18", "0");//客户实缴费用总额 
                            param.put("PARA_VALUE9", "03");//客户捆绑合约类型 
                            param.put("SERIAL_NUMBER", serialNumber);//客户号码 
                            param.put("PARA_VALUE1", serialNumber);//客户号码 
                            param.put("USER_NAME", new String(btd.getRD().getUca().getCustomer().getCustName().getBytes("UTF-8"),"GBK"));//客户名称 
                            param.put("STAFF_ID", modem.getString("UPDATE_STAFF_ID"));//销售员工 
                            param.put("TRADE_STAFF_ID", modem.getString("UPDATE_STAFF_ID"));//员工（要求是领设备的业务办理员工） 
                            param.put("TRADE_DEPART_ID", modem.getString("UPDATE_DEPART_ID"));//部门（要求是领设备的业务办理员工所在部门） 
                            param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
                            param.put("FTTH_RTN_MODEM", "BUSI");
                            IDataset results = HwTerminalCall.returnTopSetBoxTerminal(param);
                            
                            if(IDataUtil.isNotEmpty(results))
                            {
                                if(!StringUtils.equals(results.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
                                    CSAppException.appError("61313", "调用华为接口ITF_MONNI释放光猫占用出错：" + results.first().getString("X_RESULTINFO","") + ";调用接口入参:" + param);
                                }
                            }
                            
                        }
                    }
	            }
	        }
		}
	}
}
