
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 69900745 产品用 用户在LIMIT_START_DATE至LIMIT_END_DATE之间未办理LIMIT_PACKAGES营销包，报错！ 规则入参 LIMIT_START_DATE 2013-06-25
 * LIMIT_END_DATE 2013-09-25 LIMIT_PACKAGES 多个包用|XXX|的方式，本次为|66006753|66006761|66006762|
 * 
 * @author Mr.Z
 */
public class CheckActiveStartEndDateLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 6807444979475562835L;

    private static Logger logger = Logger.getLogger(CheckActiveStartEndDateLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckActiveStartEndDateLimit() >>>>>>>>>>>>>>>>>>");
        }

        IDataset userSaleActiveDataset = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        String limitStartDate = ruleParam.getString(databus, "LIMIT_START_DATE");
        String limitEndDate = ruleParam.getString(databus, "LIMIT_END_DATE");
        String limitPackages = ruleParam.getString(databus, "LIMIT_PACKAGES");
        if (IDataUtil.isEmpty(userSaleActiveDataset))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20344, "用户在" + limitStartDate + "至" + limitEndDate + "期间未办理相应的流量季度包、半年包，不能继续办理！");
            return false;
        }
        boolean isExistsTargetActive = false;
        for (int index = 0, size = userSaleActiveDataset.size(); index < size; index++)
        {
            IData userSaleActive = userSaleActiveDataset.getData(index);
            String packageId = userSaleActive.getString("PACKAGE_ID");
            if (limitPackages.indexOf(packageId) > 0)
            {
                String startDate = userSaleActive.getString("START_DATE");
                if (startDate.compareTo(limitStartDate) > 0 && startDate.compareTo(limitEndDate) < 0)
                {
                    isExistsTargetActive = true;
                    break;
                }
            }
        }

        if (!isExistsTargetActive)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20344, "用户在" + limitStartDate + "至" + limitEndDate + "期间未办理相应的流量季度包、半年包，不能继续办理！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckActiveStartEndDateLimit() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
}
