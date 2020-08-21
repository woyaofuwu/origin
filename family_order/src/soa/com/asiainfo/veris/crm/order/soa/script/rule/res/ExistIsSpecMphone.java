
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistIsSpecMphone extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistIsSpecMphone.class);

    /**
     * 判断客户名称是否包涵特殊字符
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistIsSpecMphone() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", databus.getString("SERIAL_NUMBER"));

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "ExistIsSpecMphone", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistIsSpecMphone() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
