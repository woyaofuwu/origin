
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/*
 * ExistsTradeNow 有工单正在执行，请稍后办理。
 */

public class CheckFamilyByProduct2Rule extends BreBase implements IBREScript

{

    private static Logger logger = Logger.getLogger(CheckFamilyByProduct2Rule.class);

    /**
     * 该随e行号码已经与其他手机绑定,不能再次办理
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckFamilyByProduct2Rule() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String err = "";

        String strProductId = databus.getString("PRODUCT_ID");
        String strFamilyCount = databus.getString("RSRV_STR3");
        String strEparchyCode = databus.getString("EPARCHY_CODE");

        /* 获取规则配置信息 */
        String strProductIdPara = ruleParam.getString(databus, "PRODUCT_ID");

        /* 获取业务台账，用户资料信息 */

        /* 获取外部信息 */
        IDataset listComparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 1010, "FAMILYCN", strProductId, strEparchyCode);

        /* 开始逻辑规则校验 */
        if (listComparaInfo.size() == 1)
        {
            int iCount = Integer.parseInt(listComparaInfo.getData(0).getString("PARA_CODE2"));
            if (iCount != 0 && Integer.parseInt(strFamilyCount) > iCount)
            {
                if (strProductId.equals(strProductIdPara))
                {
                    bResult = true;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckFamilyByProduct2Rule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
