
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsHisTradeDateLimitZFQQ extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsHisTradeDateLimitZFQQ.class);

    /**
     * 判断某个资费是否当月办理多次
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsHisTradeDateLimitZFQQ() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strTradeTypeCode = ruleParam.getString(databus, "TRADE_TYPE_CODE");
        String strUserId = ruleParam.getString(databus, "USER_ID");
        String strProductId = ruleParam.getString(databus, "PRODUCT_ID");
        String strDiscntCode = ruleParam.getString(databus, "ACCEPT_MONTH");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strEparchyCode = ruleParam.getString(databus, "TRADE_EPARCHY_CODE");

        int iTimes = ruleParam.getInt(databus, "TIMES");

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */

        /* 校验历史台账 */
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", strTradeTypeCode);
        param.put("USER_ID", strUserId);
        param.put("PRODUCT_ID", strProductId);
        param.put("ACCEPT_MONTH", strDiscntCode);
        param.put("PARAM_CODE", strParamCode);
        param.put("TRADE_EPARCHY_CODE", strEparchyCode);

        int iCount = Dao.qryByCode("TD_S_CPARAM", "QryTradeMultDiscntByMonth", param).getData(0).getInt("RECORDCOUNT");

        if (iCount < iTimes)
        {
            IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
            IDataset listCommpara = null;

            for (Iterator iterator = listTradeDiscnt.iterator(); iterator.hasNext();)
            {
                IData tradediscnt = (IData) iterator.next();

                if (listCommpara == null)
                {
                    listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 2001, strParamCode, strEparchyCode);
                }

                for (Iterator iter = listCommpara.iterator(); iter.hasNext();)
                {
                    IData commpara = (IData) iter.next();

                    if (tradediscnt.getString("DISCNT_CODE").equals(commpara.getString("PARA_CODE1")))
                    {
                        iCount++;
                        break;
                    }
                }
            }
        }

        bResult = iCount >= iTimes ? true : false;

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsHisTradeDateLimitZFQQ() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
