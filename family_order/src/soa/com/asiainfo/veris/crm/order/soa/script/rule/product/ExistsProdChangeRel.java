
package com.asiainfo.veris.crm.order.soa.script.rule.product;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsProdChangeRel extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsProdChangeRel.class);

    /**
     * 判断亲情号码能否作变主产品
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsProdChangeRel() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        IData param = new DataMap();
        param.put("USER_ID_B", ruleParam.getString(databus, "USER_ID"));
        param.put("PRODUCT_ID", ruleParam.getString(databus, "PRODUCT_ID"));
        param.put("NEXT_ACCT_DATE", SysDateMgr.getFirstDayOfNextMonth());

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        if (Dao.qryByRecordCount("TD_S_CPARAM", "ExistsFamilyProductByUserId", param))
        {
            if (Dao.qryByRecordCount("TD_S_CPARAM", "ExistsFamilyProductLimitByUserId", param))
            {
                bResult = true;
            }
        }
        else
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsProdChangeRel() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
