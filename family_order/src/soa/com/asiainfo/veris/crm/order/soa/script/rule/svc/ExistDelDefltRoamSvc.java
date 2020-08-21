
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistDelDefltRoamSvc extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistDelDefltRoamSvc.class);

    /**
     * 判断是否删除默认漫游级别服务
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistDelDefltRoamSvc() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");

        /* 开始逻辑规则校验 */
        IDataset listDefltRoamSvc = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_DefltRoamSvc", databus);

        int iCountDefltRomaSvc = listDefltRoamSvc.size();
        int iCountTradeSvc = listTradeSvc.size();
        for (int iSvc = 0; iSvc < iCountDefltRomaSvc; iSvc++)
        {
            for (int iTradeSvc = 0; iTradeSvc < iCountTradeSvc; iTradeSvc++)
            {
                if ("0".equals(listTradeSvc.get(iTradeSvc, "MODIFY_TAG")) && listTradeSvc.get(iTradeSvc, "SERVICE_ID").equals(listDefltRoamSvc.get(iSvc, "ELEMENT_ID")))
                {
                    bResult = true;
                    break;
                }
            }

            if (!bResult)
            {
                if (logger.isDebugEnabled())
                    logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistDelDefltRoamSvc() " + true + "<<<<<<<<<<<<<<<<<<<");
                return true;
            }

            bResult = false;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistDelDefltRoamSvc() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
