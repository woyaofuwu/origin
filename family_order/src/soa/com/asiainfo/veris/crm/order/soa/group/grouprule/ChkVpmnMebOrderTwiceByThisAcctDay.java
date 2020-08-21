
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class ChkVpmnMebOrderTwiceByThisAcctDay extends BreBase implements IBREScript
{

    /**
     * 1304规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkVpmnMebOrderTwiceByThisAcctDay.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkVpmnMebOrderTwiceByThisAcctDay()  >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userId = databus.getString("USER_ID_B");
        if (!"true".equals(databus.getString("IF_BOOKING", "")))
        { // 分散账期修改
            IData map = new DataMap();
            // map.put("TRADE_TYPE_CODE", "1025"); //集团用户成员开户，j2ee认为此判断错误，移除
            map.put("USER_ID", userId);

            map.put("START_DATE", DiversifyAcctUtil.getFirstTimeThisAcct(userId));
            map.put("END_DATE", DiversifyAcctUtil.getLastTimeThisAcctday(userId, null));

            // IDataset results1 = TradeInfoQry.getHisMainTradeByUserIdAndDate(map);

            map.put("TRADE_TYPE_CODE", "3034");
            IDataset results2 = TradeInfoQry.getHisMainTradeByUserIdAndDate(map);

            // if (results1 != null && results2 != null && (results1.size() + results2.size()) > 1)
            if (IDataUtil.isNotEmpty(results2) && results2.size() > 1)
            {
                err = "您" + DiversifyAcctUtil.getUserAcctDescMessage(userId, "0") + "办理VPMN成员新增业务已达2次，请于下账期办理。";

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                bResult = false;

            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChkVpmnMebOrderTwiceByThisAcctDay() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
