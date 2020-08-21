
package com.asiainfo.veris.crm.order.soa.script.rule.product;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsFProdGCust extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsFProdGCust.class);

    /**
     * 判断用户产品
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsFProdGCust() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strEparchyCode = ruleParam.getString(databus, "EPARCHY_CODE");
        String strProductId = ruleParam.getString(databus, "PRODUCT_ID");

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        IDataset listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strEparchyCode);

        for (Iterator iter = listCommpara.iterator(); iter.hasNext();)
        {
            IData commpara = (IData) iter.next();

            if (strProductId.equals(commpara.getString("PARA_CODE1")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsFProdGCust() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
