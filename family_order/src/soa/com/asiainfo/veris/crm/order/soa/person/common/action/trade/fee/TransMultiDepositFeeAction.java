
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.fee;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * 个人营销活动多存折转账，先转、后缴。
 * 
 * @author Mr.Z
 */
public class TransMultiDepositFeeAction implements ITradeAction
{
    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {

        String preType = btd.getRD().getPreType();
        String isConFirm = btd.getRD().getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        List<OtherFeeTradeData> otherFeeList = btd.getTradeDatas(TradeTableEnum.TRADE_OTHERFEE);
        UserTradeData user = btd.getRD().getUca().getUser();

        if (BofConst.MODIFY_TAG_ADD.equals(user.getModifyTag()))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "新增用户无法转账");
        }

        for (OtherFeeTradeData otherFee : otherFeeList)
        {
            String operType = otherFee.getOperType();

            if (!BofConst.OTHERFEE_SAME_TRANS.equals(operType))
                continue;

            String tradeId = btd.getTradeId();
            String sn = btd.getRD().getUca().getSerialNumber();
            String tradeFee = otherFee.getOperFee();
            String outDepositCode = otherFee.getOutDepositCode();
            String paymentId = otherFee.getPaymentId();
            String inDepositCode = otherFee.getInDepositCode();
            String actionCode = otherFee.getActionCode();
            String startDate = otherFee.getStartDate();

            AcctCall.sameTransFee(tradeId, sn, tradeFee, outDepositCode, inDepositCode, paymentId, actionCode, startDate);
        }
    }
}
