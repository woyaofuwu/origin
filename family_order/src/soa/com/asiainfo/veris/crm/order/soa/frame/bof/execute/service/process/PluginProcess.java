
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.plugin.PluginFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.plugin.IPlugin;

public class PluginProcess
{

    public static void pluginProcess(List<BusiTradeData> btds, IData input) throws Exception
    {
        int size = btds.size();
        if (size > 1)
        {
            // 计算是否有工单依赖，有则插表
            IPlugin tradeDependDeal = PluginFactory.getInstance().build("TRADE_DEPEND", IPlugin.class);
            tradeDependDeal.deal(btds, input);
        }

        // 各个省对于整个ORDER的一些特殊处理，入参为传入的IData
        IPlugin orderSpecDeal = PluginFactory.getInstance().build("ORDER_SPECDEAL", IPlugin.class);
        orderSpecDeal.deal(btds, input);
    }

    public static void pluginProcessBefore(List<BusiTradeData> btds, IData input) throws Exception
    {
        IPlugin financialBossPlugin = PluginFactory.getInstance().build("FINANCIAL_BOSS", IPlugin.class);
        financialBossPlugin.deal(btds, input);
    }
}
