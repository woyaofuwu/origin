
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class SelectLimitDiscnt80007317 extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(SelectLimitDiscnt80007317.class);

    /**
     * 
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SelectLimitDiscnt80007317() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        String product_id = databus.getString("PRODUCT_ID", "");
        String cust_id = databus.getString("CUST_ID", "");
        if ("8000".equals(product_id))
        {
            if ("3700000037385919".equals(cust_id))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 821218, "该集团V网产品,不允许新增成员！");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SelectLimitDiscnt80007317() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
