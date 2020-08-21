
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.log.ActionLog;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.TradeActionConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.TradeConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;

public class TradeProxy implements InvocationHandler
{
    public static ITrade getInstance(BaseReqData brd) throws Exception
    {
        return TradeConfig.getTradeBean(brd);
    }

    private ITrade tradeImpl;

    public TradeProxy(ITrade tradeImpl)
    {
        this.tradeImpl = tradeImpl;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        // 加载并执行rule,暂时注释
        // List<ITradeRule> rules = TradeRuleConfig.getTradeBeforeRules((BaseReqData) args[0]);
        // if (rules != null && rules.size() > 0)
        // {
        // int size = rules.size();
        // for (int i = 0; i < size; i++)
        // {
        // ITradeRule busiRule = rules.get(i);
        // busiRule.checkRule((BaseReqData) args[0]);
        // }
        // }

        BusiTradeData result = (BusiTradeData) method.invoke(this.tradeImpl, args);

        // 加载并执行action
        List<ITradeAction> actions = TradeActionConfig.getTradeActions(result);

        if (actions == null || actions.size() == 0)
        {
            return result;
        }

        // 初始化
        String tradeTypeCode = result.getTradeTypeCode();
        long lStartTime = -1;
        String clazz = "";

        for (int i = 0, size = actions.size(); i < size; i++)
        {

            ITradeAction action = actions.get(i);
            clazz = action.getClass().toString();

            // 动作开始时间
            lStartTime = ActionLog.getStartTime();

            action.executeAction(result);

            // 动作监控日志
            ActionLog.log(tradeTypeCode, clazz, lStartTime);
        }

        // checkAfter, 暂时注释
        // List<ITradeAfterRule> afterRules = TradeRuleConfig.getTradeAfterRules(result);
        // if (afterRules != null && afterRules.size() > 0)
        // {
        // int size = afterRules.size();
        // for (int i = 0; i < size; i++)
        // {
        // ITradeAfterRule busiAfterRule = afterRules.get(i);
        // busiAfterRule.checkTradeAfterRule(result);
        // }
        // }

        return result;
    }
}
