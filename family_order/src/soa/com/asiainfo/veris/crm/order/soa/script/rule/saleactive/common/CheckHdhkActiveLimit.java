
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;

/**
 * 前台业务受理前业务规则：只针对前台业务。actionType = "TradeCheckBefore";
 * 
 * @author Mr.Z
 */
public class CheckHdhkActiveLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -2792489391805861229L;

    private static Logger logger = Logger.getLogger(CheckHdhkActiveLimit.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckHdhkActiveLimit() >>>>>>>>>>>>>>>>>>");
        }

        IDataset hdhkActives = SaleActiveInfoQry.queryHdfkActivesByUserId(databus.getString("USER_ID"));

        if (IDataUtil.isNotEmpty(hdhkActives))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062201, SaleActiveBreConst.ERROR_21);
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 退出 CheckHdhkActiveLimit() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
