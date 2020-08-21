
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
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 校验产品是否在网厅预约办理过，目前仅有活动69900804用到,需和CheckUserOpenDate规则配合使用 td_bre_parameter 入参 PARAM_PRODUCT_ID 目前配置为 69900800
 * 
 * @author Mr.Z
 */
public class CheckTermainlOrder extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -7493689848041630292L;

    private static Logger logger = Logger.getLogger(CheckTermainlOrder.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckTermainlOrder() >>>>>>>>>>>>>>>>>>");
        }

        String serialNumber = databus.getString("SERIAL_NUMBER");
        String productId = ruleParam.getString(databus, "PARAM_PRODUCT_ID");

        IDataset terminalOrder = BreQry.getTerminalOrder(serialNumber, productId);

        if (IDataUtil.isEmpty(terminalOrder))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20047, "用户未在网厅预约办理此活动，不能继续办理！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckTermainlOrder() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
}
