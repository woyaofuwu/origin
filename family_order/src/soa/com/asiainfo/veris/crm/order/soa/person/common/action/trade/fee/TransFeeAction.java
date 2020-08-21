
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.fee;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class TransFeeAction implements ITradeAction
{
    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
        
        String preType = btd.getRD().getPreType();
        String isConFirm = btd.getRD().getIsConfirm();
        List<OtherFeeTradeData> otherFeeList = btd.getTradeDatas(TradeTableEnum.TRADE_OTHERFEE);
        for (OtherFeeTradeData otherFee : otherFeeList)
        {
            String operType = otherFee.getOperType();

            if (!BofConst.OTHERFEE_SAME_TRANS.equals(operType) && !BofConst.OTHERFEE_CAMPUS_TRANS.equals(operType))
                continue;

            String outUserId = otherFee.getUserId();
            String inUserId = otherFee.getUserId2();
            String fee = otherFee.getOperFee();
            String outDepositCode = otherFee.getOutDepositCode();
            String inDepositCode = otherFee.getInDepositCode();
            String remark = otherFee.getRemark();
            String actionCode = otherFee.getActionCode();
            String tradeId = btd.getTradeId();
            String sn = btd.getRD().getUca().getSerialNumber();
            String paymentId = otherFee.getPaymentId();
            String startDate = otherFee.getStartDate();
            String rsrvStr4 = otherFee.getRsrvStr4();
            String rsrvStr5 = otherFee.getRsrvStr5();
            
            if("99990825".equals(actionCode)){
	            String execDate = SysDateMgr.getSysTime();
	            String tagDate = "19";
	            String sysDay = "";
	            sysDay = execDate.substring(8, 10);
	                // 20日（含）后下月开始返还
	                if (sysDay.compareTo(tagDate) > 0)
	                {
	                	startDate = SysDateMgr.getDateNextMonthFirstDay(execDate);
	                }
            }

            if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            {
                // 预受理不调转账接口,查询余额够不够
                IData result = AcctCall.checkTransMoney(sn, outDepositCode, fee);
                String isEnough = result.getString("IS_ENOUGH");
                if (!"0".equals(isEnough))
                {
                    CSAppException.apperr(FeeException.CRM_FEE_150);
                }

                return;
            }
            else if("TO_TEMP".equals(rsrvStr4))//如果是配置了将预存转入临时存折
            {
            	//费用大于0时才调用转账
            	if (StringUtils.isNotBlank(fee) && Integer.valueOf(fee) > 0)
            	{
            	    AcctCall.tempTransFee(tradeId, sn, fee, outDepositCode, inDepositCode, paymentId, actionCode, startDate, rsrvStr5);
            	}
            }
            else
            {
            	//费用大于0时才调用转账
            	if (StringUtils.isNotBlank(fee) && Integer.valueOf(fee) > 0)
            	{
	            	if (StringUtils.isBlank(actionCode))
	                {
	                    if (BofConst.OTHERFEE_CAMPUS_TRANS.equals(operType))
	                    {
	                        AcctCall.campusTransFee(tradeId, outUserId, inUserId, outDepositCode, inDepositCode, fee, "1", "1", remark);
	                    }else if(BofConst.OTHERFEE_SAME_TRANS.equals(operType))
	                    {      	
	                    	AcctCall.transFee(tradeId, outUserId, outUserId, outDepositCode, inDepositCode, fee, remark);	
	                    }
	                    else
	                    {
	                        AcctCall.transFee(tradeId, outUserId, inUserId, outDepositCode, inDepositCode, fee, remark);
	                    }
	                }
	                else
	                {
	                    AcctCall.sameTransFee(tradeId, sn, fee, outDepositCode, inDepositCode, paymentId, actionCode, startDate);
	                }
            	}
            }
        }
    }
}
