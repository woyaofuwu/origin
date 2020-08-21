
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class DeleteUserInfo implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeEparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");
        String tradeCityCode = mainTrade.getString("CITY_CODE");
        String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
        String removeReson = mainTrade.getString("RSRV_STR8");
        String remark = mainTrade.getString("REMARK");
        String acctId = mainTrade.getString("ACCT_ID");
        IDataset finishInfos = TradePbossFinishInfoQry.getTradePbossFinish(tradeId);
        if (IDataUtil.isEmpty(finishInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_14);
        }
        String finishDate = finishInfos.getData(0).getString("FINISH_DATE");
        if (StringUtils.isBlank(finishDate))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_15);
        }

        // 更新用户资料表
        UserInfoQry.updataWidenetUserInfoDelete(userId, finishDate, tradeEparchyCode, tradeCityCode, tradeDepartId, removeReson, remark);
        // 账户表
        AcctInfoQry.updataWidenetAcctInfoDelete(acctId, finishDate);
        // 更新用户关系信息
        UserInfoQry.updataWidenetUserRelationDelete(userId);

    }

}
