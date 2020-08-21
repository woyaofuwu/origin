
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserAcctDayInfoQry;

/**
 * 用户户账期变更同步
 * 
 * @author liuke
 */
public class ChangeAcctDaySync implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // 新系统通过INTF_ID来判断是否存在用户账期变更的台账，减少无用的查询
        String intfId = mainTrade.getString("INTF_ID");
        if (StringUtils.isNotBlank(intfId) && StringUtils.indexOf(intfId, "TF_B_TRADE_USER_ACCTDAY,") < 0)
        {
            return;
        }

        String tradeId = mainTrade.getString("TRADE_ID");

        IDataset tradeUsertAcctDays = TradeUserAcctDayInfoQry.getTradeUserAcctDayInfoByTradeId(tradeId);

        // 存在2条或者以上的记录才做处理【此情况才会有账期变更可能存在】
        if (IDataUtil.isNotEmpty(tradeUsertAcctDays) && tradeUsertAcctDays.size() > 1)
        {
            // 存在修改用户账期的用户
            IDataset modUserAcctDayList = DataHelper.filter(tradeUsertAcctDays, "MODIFY_TAG=2");
            // modUserAcctDayList = DataHelper.distinct(modUserAcctDayList, "USER_ID", "");//过滤相同的userId

            IDataset syncAcctDays = new DatasetList();

            for (int i = 0, modSize = modUserAcctDayList.size(); i < modSize; i++)
            {
                String tempUserId = modUserAcctDayList.getData(i).getString("USER_ID");

                // 根据userid和modify_tag过滤出用户变更之后的账期
                IDataset newUserAcctDays = DataHelper.filter(tradeUsertAcctDays, "MODIFY_TAG=0,USER_ID=" + tempUserId);
                // tempUserAcctDays = DataHelper.distinct(tempUserAcctDays, "USER_ID", "");

                for (int k = 0, size = newUserAcctDays.size(); k < size; k++)
                {
                    IData acctDay = new DataMap();
                    IData newAccDay = newUserAcctDays.getData(k);
                    acctDay.put("ID_TYPE", "0");// ID类型:"0"用户,"1"账户
                    acctDay.put("ID", newAccDay.getString("USER_ID"));
                    acctDay.put("ACCT_DAY", newAccDay.getString("ACCT_DAY"));
                    acctDay.put("START_DATE", newAccDay.getString("START_DATE"));
                    acctDay.put("END_DATE", newAccDay.getString("END_DATE"));
                    acctDay.put("FIRST_ACTIVE_DATE", newAccDay.getString("FIRST_DATE").split(" ")[0].replaceAll("-", ""));// 首次结账日
                    acctDay.put("INST_ID", newAccDay.getString("INST_ID"));
                    acctDay.put("CHG_TYPE", newAccDay.getString("CHG_TYPE"));
                    acctDay.put("CHG_MODE", newAccDay.getString("CHG_MODE"));
                    acctDay.put("CHG_DATE", newAccDay.getString("CHG_DATE"));
                    syncAcctDays.add(acctDay);
                }
            }

            if (syncAcctDays.size() > 0)
            {
                AcctCall.changeAcctDaySync(syncAcctDays);
            }
        }
    }
}
