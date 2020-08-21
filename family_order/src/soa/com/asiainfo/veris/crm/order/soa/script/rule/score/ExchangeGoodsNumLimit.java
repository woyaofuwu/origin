
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;

public class ExchangeGoodsNumLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExchangeGoodsNumLimit.class);

    /**
     * @author huangsl
     * @deprecated 积分兑换 家乐福礼品卷兑换次数限制
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExchangeGoodsNumLimit() >>>>>>>>>>>>>>>>>>");
        boolean bResult = false;

        int ticketCount = 0;
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "1".equals(xChoiceTag))
        {
            IData pgData = databus.getData("REQDATA");// 请求的数据
            if (IDataUtil.isNotEmpty(pgData))
            {
                if (StringUtils.isBlank(pgData.getString("EXCHANGE_DATA")))
                {
                    IDataset ruleData = ExchangeRuleInfoQry.queryByRuleId(pgData.getString("RULE_ID"), this.getUserEparchyCode());
                    IData param = ruleData.size() == 1 ? ruleData.getData(0) : null;
                    if (IDataUtil.isNotEmpty(param))
                    {
                        if ("N".equals(param.getString("EXCHANGE_TYPE_CODE")))
                        {
                            for (int j = 0; j < pgData.getInt("COUNT", 1); j++)
                            {
                                ticketCount++;
                            }
                            if (ticketCount > 1)
                            {
                                bResult = true;
                            }
                        }
                    }
                }
                else
                {
                    IDataset exchangeData = new DatasetList(pgData.getString("EXCHANGE_DATA"));
                    if (IDataUtil.isNotEmpty(exchangeData))
                    {
                        int size = exchangeData.size();
                        for (int i = 0; i < size; i++)
                        {
                            IData temp = exchangeData.getData(i);
                            IDataset ruleData = ExchangeRuleInfoQry.queryByRuleId(temp.getString("RULE_ID"), this.getUserEparchyCode());
                            IData param = ruleData.size() == 1 ? ruleData.getData(0) : null;
                            if (IDataUtil.isNotEmpty(param))
                            {
                                if ("N".equals(param.getString("EXCHANGE_TYPE_CODE")))
                                {
                                    for (int j = 0; j < temp.getInt("COUNT", 1); j++)
                                    {
                                        ticketCount++;
                                    }
                                    if (ticketCount > 1)
                                    {
                                        bResult = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExchangeGoodsNumLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
