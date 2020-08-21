
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossFinishInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetActInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ModifyEndDatePbossTime implements ITradeFinishAction
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
        
        //终止用户资料
        UserInfoQry.updataWidenetUserInfoDelete(userId, finishDate, tradeEparchyCode, tradeCityCode, tradeDepartId, removeReson, remark);
        //终止账户信息
        AcctInfoQry.updataWidenetAcctInfoDelete(acctId, finishDate);
        //终止用户关系
        UserInfoQry.updataWidenetUserRelationDelete(userId);
        
        
        TradeWideNetActInfoQry.updateProductEndDate( tradeId,  finishDate) ;    
        TradeWideNetActInfoQry.updateAttrEndDate( tradeId,  finishDate) ;    
        TradeWideNetActInfoQry.updateDiscntEndDate( tradeId,  finishDate) ;
        TradeWideNetActInfoQry.updateResEndDate( tradeId,  finishDate) ;
        String finishDate2 = finishDate.substring(0, 10) + SysDateMgr.END_DATE;
        TradeWideNetActInfoQry.updateSvcEndDate( tradeId,  finishDate2) ;        
        TradeWideNetActInfoQry.updateWidenetEndDate( tradeId,  finishDate) ;
        TradeWideNetActInfoQry.updateWidnetActEndDate( tradeId,  finishDate) ;    
        TradeWideNetActInfoQry.updatePayRelaEndDate( tradeId,  finishDate) ;
        TradeWideNetActInfoQry.updateUserAcctDayEndDate(tradeId, finishDate);
        
//        TradeDiscntInfoQry.updateStartDate(tradeId, finishDate);
//        // 处理产品
//        TradeProductInfoQry.updateStartDate(tradeId, finishDate);
//
//        // 更新服务时间
//        String finishDate2 = finishDate.substring(0, 10) + SysDateMgr.START_DATE_FOREVER;
//        TradeSvcInfoQry.updateStartDate(tradeId, finishDate2);
//
//        // 处理attr
//        TradeAttrInfoQry.updateStartDate(tradeId, finishDate);
//
//        TradeResInfoQry.updateStartDate(tradeId, finishDate);
//
//        TradeUserInfoQry.updateOpenDate(tradeId, finishDate);
//        TradePayRelaInfoQry.updateStartDate(tradeId, finishDate);
//        TradeWideNetInfoQry.updateStartDate(tradeId, finishDate);
//        TradeWideNetActInfoQry.updateStartDate(tradeId, finishDate);
//        TradeUserAcctDayInfoQry.updateStartDate(tradeId, finishDate);
//        TradeAccountAcctDayInfoQry.updateStartDate(tradeId, finishDate);
//        TradeAcctConsignInfoQry.updateStartDate(tradeId, finishDate);
//        TradeAcctInfoQry.updateStartDate(tradeId, finishDate);

    }

}
