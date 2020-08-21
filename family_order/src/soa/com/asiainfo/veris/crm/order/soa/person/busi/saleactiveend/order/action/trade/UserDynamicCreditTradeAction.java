
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.trade;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCreditInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestdata.SaleActiveEndReqData;

public class UserDynamicCreditTradeAction implements ITradeAction
{

    private void dealOtherActiveCreditValue(SaleActiveEndReqData saleActiveEndReqData) throws Exception
    {
        UcaData uca = saleActiveEndReqData.getUca();

        List<SaleActiveTradeData> userBackSaleActives = getUserBackSaleActives(saleActiveEndReqData);

        if (CollectionUtils.isEmpty(userBackSaleActives))
            return;

        SaleActiveTradeData thisActiveUserData = uca.getUserSaleActiveByRelaTradeId(saleActiveEndReqData.getRelationTradeId());

        if (thisActiveUserData == null)
            return;

//        String thisActiveStartDate = thisActiveUserData.getStartDate();
//        String thisActiveEndDate = thisActiveUserData.getEndDate();
//        String thisActiveMonth = thisActiveUserData.getMonths();

        // int intervalMoths = Integer.parseInt(SaleActiveUtil.getIntervalMoths(thisActiveStartDate, thisActiveEndDate,
        // thisActiveMonth));

        for (SaleActiveTradeData userActiveData : userBackSaleActives)
        {
            // 不管是终止什么活动，都是先取消所有活动的信用度，再申请其它没有取消的活动的信用度
            // if(SaleActiveUtil.getDayIntervalNoAbs(userActiveData.getStartDate(), thisActiveStartDate) > 0) continue;

            String relationTradeId = userActiveData.getRelationTradeId();

            if (thisActiveUserData.getRelationTradeId().equals(relationTradeId))
                continue;

            IDataset tradeCreditDataset = TradeCreditInfoQry.getTradeCreditByPK(relationTradeId, "0");

            if (IDataUtil.isEmpty(tradeCreditDataset))
                continue;

            for (int index = 0, size = tradeCreditDataset.size(); index < size; index++)
            {
                // String startDate = SysDateMgr.getAddMonthsNowday(-intervalMoths, userActiveData.getStartDate());
                // String endDate = SysDateMgr.getAddMonthsNowday(-intervalMoths, userActiveData.getEndDate());
                // 由于userBackSaleActives从UCA获取，针对顺延的营销活动已经往前推移，所以不需要再推移
                String startDate = userActiveData.getStartDate();
                String endDate = userActiveData.getEndDate();
                String userId = saleActiveEndReqData.getUca().getUserId();
                String creditValue = tradeCreditDataset.getData(index).getString("CREDIT_VALUE");
                CreditCall.modifyUserCreditDate(userId, creditValue, startDate, endDate);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveEndReqData saleActiveEndReqData = (SaleActiveEndReqData) btd.getRD();
        String relationTradeId = saleActiveEndReqData.getRelationTradeId();

        IDataset tradeCreditDataset = TradeCreditInfoQry.getTradeCreditByPK(relationTradeId, "0");

        if (IDataUtil.isEmpty(tradeCreditDataset))
            return;

        UcaData uca = btd.getRD().getUca();

        for (int index = 0, size = tradeCreditDataset.size(); index < size; index++)
        {
        	IData creditData = tradeCreditDataset.getData(index);
        	String creditValue = creditData.getString("CREDIT_VALUE");
            if(Long.parseLong(creditValue) >= 0)
            {
            	String userId = uca.getUserId();
                //String endDate = SysDateMgr.getSysTime();
            	String startDate = creditData.getString("START_DATE");//songlm 20140115 如果当月办理、当月终止，传给信控的起始时间不对，与李秀玉商定改为原起始时间
            	String endDate = creditData.getString("END_DATE");
                CreditCall.cancelUserCredit(userId, creditValue, startDate, endDate);//songlm 20140115 如果当月办理、当月终止，传给信控的起始时间不对，与李秀玉商定改为原起始时间
            }
        }

        dealOtherActiveCreditValue(saleActiveEndReqData);
    }

    private List<SaleActiveTradeData> getUserBackSaleActives(SaleActiveEndReqData saleActiveEndReqData) throws Exception
    {
        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", saleActiveEndReqData.getUca().getUserEparchyCode());

        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();
        UcaData uca = saleActiveEndReqData.getUca();

        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, noBackConfigs);
        List<SaleActiveTradeData> userBackSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);
        userBackSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userBackSaleActives);
        userBackSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userBackSaleActives);

        return userBackSaleActives;
    }

}
