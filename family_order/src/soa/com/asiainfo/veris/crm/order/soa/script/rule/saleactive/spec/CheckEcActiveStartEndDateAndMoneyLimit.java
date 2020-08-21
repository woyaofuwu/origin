
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 69900755、69900776产品用 用户在LIMIT_START_DATE至LIMIT_END_DATE之间未在电子渠道办理除LIMIT_PRODUCTS营销产品外，且活动金额大于 LIMIT_MONEY的活动，则报错。
 * 规则入参 LIMIT_START_DATE 2013-08-05 LIMIT_END_DATE LIMIT_PRODUCTS 多个产品用|XXX|的方式，本次为|69900577|69900398| LIMIT_MONEY 单位分
 * 10000、10000
 * 
 * @author Mr.Z
 */
public class CheckEcActiveStartEndDateAndMoneyLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -3133671840744405755L;

    private static Logger logger = Logger.getLogger(CheckEcActiveStartEndDateAndMoneyLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckEcActiveStartEndDateAndMoneyLimit() >>>>>>>>>>>>>>>>>>");
        }

        IDataset userSaleActiveDataset = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        String limitStartDate = ruleParam.getString(databus, "LIMIT_START_DATE");
        String limitEndDate = ruleParam.getString(databus, "LIMIT_END_DATE");
        String limitProducts = ruleParam.getString(databus, "LIMIT_PRODUCTS");
        String strMoney = ruleParam.getString(databus, "LIMIT_MONEY");

        int limiMoney = StringUtils.isNotBlank(strMoney) ? Integer.parseInt(strMoney) : 0;

        if (IDataUtil.isEmpty(userSaleActiveDataset))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20344, "用户在活动期间没有在网厅或10086热线下单购买金额为" + limiMoney / 100 + "元或以上的手机！");
            return false;
        }

        boolean isExistsTargetActive = false;
        for (int index = 0, size = userSaleActiveDataset.size(); index < size; index++)
        {
            IData userSaleActive = userSaleActiveDataset.getData(index);
            String startDate = userSaleActive.getString("START_DATE");
            String productId = userSaleActive.getString("PRODUCT_ID");
            String packageId = userSaleActive.getString("PACKAGE_ID");
            String relationTradeId = userSaleActive.getString("RELATION_TRADE_ID");
            int operFee = Integer.parseInt(userSaleActive.getString("OPER_FEE", "0"));
            int advancePay = Integer.parseInt(userSaleActive.getString("ADVANCE_PAY", "0"));
            boolean isNotProduct = limitProducts.indexOf(productId) < 0 ? true : false;

            if (isNotProduct && BreQry.isTermianlOrderActive(databus.getString("USER_ID"), relationTradeId, productId, packageId))
            {
                boolean isTimeBetween = startDate.compareTo(limitStartDate) > 0 && startDate.compareTo(limitEndDate) < 0;
                boolean isLimitMoney = operFee + advancePay > limiMoney ? true : false;
                if (isTimeBetween && isLimitMoney)
                {
                    isExistsTargetActive = true;
                    break;
                }
            }
        }

        if (!isExistsTargetActive)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20344, "用户在活动期间没有在网厅或10086热线下单购买金额为" + limiMoney / 100 + "元或以上的手机！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckEcActiveStartEndDateAndMoneyLimit() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
}
