
package com.asiainfo.veris.crm.order.soa.script.rule.payrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistMultiPayRelation extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistMultiPayRelation.class);

    /**
     * 判断一个帐号是否为多个用户付费
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistMultiPayRelation() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("USER_ID", databus.getString("USER_ID"));

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "ExistMultiPayRelation", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistMultiPayRelation() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
