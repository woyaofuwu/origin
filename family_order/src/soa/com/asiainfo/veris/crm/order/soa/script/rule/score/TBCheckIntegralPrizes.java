
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TBCheckIntegralPrizes extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckIntegralPrizes.class);

    /**
     * 判断用户品牌是否能受理积分兑换
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckIntegralPrizes() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strProvinceCode = databus.getString("PROVINCE_CODE");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strBrandCode = databus.getString("BRAND_CODE");

        /* 开始逻辑规则校验 */
        if ("330".equals(strTradeTypeCode))
        {
            if ("HAIN".equals(strProvinceCode))
            {
                if (!"G001".equals(strBrandCode) && !"G002".equals(strBrandCode) && !"G010".equals(strBrandCode))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, 751018, "业务受理前条件判断：该用户品牌不能办理积分兑奖业务！");
                }
            }
            else if ("TJIN".equals(strProvinceCode))
            {
                if (!"G001".equals(strBrandCode) && !"G010".equals(strBrandCode))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, 751018, "业务受理前条件判断：该用户品牌不能办理积分兑奖业务！");
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckIntegralPrizes() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
