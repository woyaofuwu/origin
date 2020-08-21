
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ChkCancelXqwVpmnMeb extends BreBase implements IBREScript
{

    /**
     * 请先退订融合一号通产品之后，再退订V网
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkCancelXqwVpmnMeb.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkCancelXqwVpmnMeb() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String err = "";
        String mebUserId = databus.getString("USER_ID_B", "");
        //
        // 1. 查成员是否订购乡情网优惠906039，是否 start
        IDataset disInfos = UserDiscntInfoQry.queryXQWDiscntInfoByUserIdAndDisCode(mebUserId, "906039");
        if (IDataUtil.isNotEmpty(disInfos))
        {
            err = "您已办理了“入乡情网送农信通活动”,如果您不再加入集团V网,\r\n月底系统判断无V网业务,则收取农信通业务功能费(2元/月),是否继续？";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, errCode, err);
            bResult = false;
        }
        // 1. 查成员是否订购乡情网优惠906039，是否 end
        // 2. 客户当前账期办理新增集团次数已达到两次，如取消则当账期不能再办理新增集团，是否继续？ start
        //注释掉 for:REQ201509160017关于优化VPMN成员办理界面的需求
        //String startDate = DiversifyAcctUtil.getFirstTimeThisAcct(mebUserId);
        //String endDate = DiversifyAcctUtil.getLastTimeThisAcctday(mebUserId, null);
        //IDataset ds1025infos = TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode("1025", mebUserId, startDate, endDate);// 集团用户成员开户
        //IDataset ds3034infos = TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode("3034", mebUserId, startDate, endDate);// 集团VPMN成员新增
        //if (IDataUtil.isNotEmpty(ds1025infos) && IDataUtil.isNotEmpty(ds3034infos) && (ds1025infos.size() + ds3034infos.size()) > 1)
        //{
        //if (IDataUtil.isNotEmpty(ds3034infos) && ds3034infos.size() > 1)
        //{
        //    err = "客户本月办理新增集团次数已达到两次，如取消则当月不能再办理新增集团，是否继续？，点击确定继续办理，点击取消停止办理。 ";
        //    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, errCode, err.toString());
        //    return true;
        //}
        ////注释掉 for:REQ201509160017关于优化VPMN成员办理界面的需求
        // 2. end

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChkCancelXqwVpmnMeb() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
