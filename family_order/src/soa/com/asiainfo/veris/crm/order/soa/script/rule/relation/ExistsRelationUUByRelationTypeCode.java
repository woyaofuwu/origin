
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsRelationUUByRelationTypeCode extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsRelationUUByRelationTypeCode.class);

    /**
     * 判断用户有哪种RelationUU关系
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsRelationUUByRelationTypeCode() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRelationTypeCodeS = ruleParam.getString(databus, "RELATION_TYPE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listUU = databus.getDataset("TF_F_RELATION_UU");

        /* 开始逻辑规则校验 */
        for (Iterator iterUU = listUU.iterator(); iterUU.hasNext();)
        {
            IData UU = (IData) iterUU.next();

            if (strRelationTypeCodeS.indexOf("|" + UU.getString("RELATION_TYPE_CODE") + "|") > -1)
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsRelationUUByRelationTypeCode() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
