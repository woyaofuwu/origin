
package com.asiainfo.veris.crm.order.soa.script.rule.cancel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TBCCheckGetdata extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCCheckGetdata.class);

    /**
     * 返销规则前数据准备
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCCheckGetdata() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        StringBuilder errInfo = new StringBuilder("业务取消校验：");

        String strTradeId = databus.getString("TRADE_ID");
        String strCancelStaffId = databus.getString("TRADE_STAFF_ID");
        String strCancelDepartId = databus.getString("TRADE_DEPART_ID");
        String strCancelCityCode = databus.getString("TRADE_CITY_CODE");
        String strCancelEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        String strCancelTradeTypeCode = databus.getString("TRADE_TYPE_CODE", "");// 前台传入的业务类型
        String strCancelDate = BreTimeUtil.getSysdate(databus);

        IData tradeInfo = null;
        if (strCancelTradeTypeCode.equals("110"))// 新系统产品变更是立即执行的，产品变更查询历史表
        {
            tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(strTradeId, "0", null);
        }
        else
        {
            tradeInfo = UTradeInfoQry.qryTradeByTradeId(strTradeId, "0", null);
        }
        if (IDataUtil.isEmpty(tradeInfo))
        {
            errInfo.append("获取trade_id=").append(strTradeId).append("的订单数据失败！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, -1, errInfo.toString());
            return true;
        }

        // 获取业务类型参数
        IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeInfo.getString("TRADE_TYPE_CODE"), tradeInfo.getString("EPARCHY_CODE"));
        if (IDataUtil.isEmpty(tradeTypeInfo))
        {
            errInfo.append("获取trade_id=[").append(strTradeId).append("]的订单的业务类型参数失败！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, -1, errInfo.toString());
            return true;
        }

        // 获取用户资料
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(tradeInfo.getString("USER_ID"));
        if (null == userInfo)
        {
            errInfo.append("获取user_id=[").append(tradeInfo.getString("USER_ID")).append("的用户资料失败！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, -1, errInfo.toString());
            return true;
        }

        tradeInfo.put("CANCEL_DATE", strCancelDate);
        tradeInfo.put("CANCEL_STAFF_ID", strCancelStaffId);
        tradeInfo.put("CANCEL_DEPART_ID", strCancelDepartId);
        tradeInfo.put("CANCEL_CITY_CODE", strCancelCityCode);
        tradeInfo.put("CANCEL_EPARCHY_CODE", strCancelEparchyCode);

        databus.putAll(tradeInfo);
        databus.put("TF_B_TRADE", tradeInfo);
        databus.put("TD_S_TRADETYPE", tradeTypeInfo);
        databus.put("TF_F_USER", userInfo);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCCheckGetdata() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
