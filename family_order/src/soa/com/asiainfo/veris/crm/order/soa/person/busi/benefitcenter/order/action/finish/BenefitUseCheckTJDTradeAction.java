package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * beforeAction校验停简单调用出场接口订单标识是否已存在,防止重复记录用户出场记录
 * BUG20200420112956 机场停车权益重复使用记录BUG
 * @author 梁端刚
 * @version V1.0
 * @date 2020/4/20 21:15
 */
public class BenefitUseCheckTJDTradeAction implements ITradeFinishAction {
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId, PersonConst.BENEFIT_RIGHT_USE_RECORD);
        if(IDataUtil.isEmpty(tradeOthers)){
            return;
        }
        String rsrvStr4 = tradeOthers.getData(0).getString("RSRV_STR4");
        if(StringUtils.isBlank(rsrvStr4)){
            return;
        }
        IDataset userOthersByTingJDTradeID = UserOtherInfoQry.getByTingJDTradeID(rsrvStr4);
        if(IDataUtil.isNotEmpty(userOthersByTingJDTradeID)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "由于出场接口重试机制，该出场通知已记录，请清理该笔订单！");
        }
    }
}
