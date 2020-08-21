
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.requestdata.DestroyUserNowRequestData;

public class EndModermAction implements ITradeAction
{

	private static transient Logger logger = Logger.getLogger(AjustFeeAction.class);
    /**
     * 如果存在光猫，则调用华为接口进行光猫记录截止
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	
    	DestroyUserNowRequestData rd = (DestroyUserNowRequestData)btd.getRD();
    	if(rd.getModermReturn().equals("0"))//如果选择不退光猫，则不调华为接口
    		return;

    	String serialNumber = rd.getSerialNumberA();
    	
    	IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
    	if(userInfos.isEmpty()){
    		logger.error("FTTH宽带拆机时，查询用户信息失败！"+serialNumber);
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "FTTH宽带拆机时，查询用户信息失败！");
    		return;
    	}
    	String userId = userInfos.getData(0).getString("USER_ID");
        String grpTag = userInfos.getData(0).getString("RSRV_STR10",""); 
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        
        IDataset custInfos = UserInfoQry.getUserinfo(serialNumber);
        if(custInfos.isEmpty()){
    		logger.error("FTTH宽带拆机时，查询客户信息失败！"+serialNumber);
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "FTTH宽带拆机时，查询客户信息失败！");
    		return;
    	}
        
        IDataset userOtherInfos = null;
        
        if(grpTag.equals("BNBD"))//集团宽带
        	userOtherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"FTTH_GROUP");
        else//个人宽带
        	userOtherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"FTTH");
        
        if(!userOtherInfos.isEmpty())
		{
    		String modermCode = userOtherInfos.getData(0).getString("RSRV_STR1","");
    		if(!modermCode.equals(""))//如果光猫串号不为空
    		{
        		String modermFee = rd.getModemFee();
        		IData param = new DataMap();
        		param.put("RES_NO", modermCode);//终端串号
//        		param.put("REMARK", "");//备注
        		param.put("PARA_VALUE1", serialNumber);//PARA_VALUE1
        		param.put("SALE_FEE", 0);//销售费用 非销售时为0
//        		param.put("PARA_VALUE3", "");//活动标识
//        		param.put("PARA_VALUE4", "");//用户编码
//        		param.put("PARA_VALUE5", "");//返销时间
        		param.put("PARA_VALUE7", 0);//代办费
//        		param.put("DEVICE_COST", "");//进货价格
//        		param.put("PARA_VALUE8", "");//销售政策
        		param.put("TRADE_ID", rd.getTradeId());//台账流水
        		param.put("X_CHOICE_TAG", "1");//0-终端销售,1—终端销售退货
        		param.put("ES_TYPE_CODE", "4");//资源类型,终端的传入4
        		param.put("CONTRACT_ID", userOtherInfos.getData(0).getString("TRADE_ID",""));//销售订单号
        		param.put("PRODUCT_MODE", "0");//全网统一操盘合约机销售标志
        		param.put("X_RES_NO_S", modermCode);//终端串号
        		param.put("X_RES_NO_E", modermCode);//终端串号
        		param.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
        		param.put("PARA_VALUE14", "0");//裸机价格
        		param.put("PARA_VALUE15", "0");//客户购机折让价格
        		param.put("PARA_VALUE16", "0");//客户预存话费
        		param.put("PARA_VALUE17", "0");//客户实际购机款
        		param.put("PARA_VALUE18", "0");//客户实缴费用总额
        		param.put("PARA_VALUE9", "03");//客户捆绑合约类型
        		param.put("SERIAL_NUMBER", serialNumber);//客户号码
        		param.put("PARA_VALUE1", serialNumber);//客户号码
        		param.put("USER_NAME", new String(custInfos.getData(0).getString("CUST_NAME","").getBytes("UTF-8"),"GBK"));//客户名称
        		param.put("STAFF_ID", userOtherInfos.getData(0).getString("STAFF_ID",""));//销售员工
//        		param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
        		
        		IDataset results = HwTerminalCall.returnTopSetBoxTerminal(param);
        		
        		if(results==null || results.size()==0 || !results.getData(0).getString("X_RESULTCODE").equals("0"))
        		{
        	    	CSAppException.apperr(CrmCommException.CRM_COMM_103, results.getData(0).getString("X_RESULTINFO"));
        	    	if (logger.isDebugEnabled())
                    {
                        logger.error("FTTH宽带拆机时，光猫退机失败！"+serialNumber);
                    }
        		}
        		else
        		{
                	if (logger.isDebugEnabled())
                    {
                        logger.error("FTTH宽带拆机时，光猫退机成功！"+serialNumber);
                    }
                }
    		}
		}
        	
    }
}
