
package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACSvcState4CMNETAndCMWAP extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACSvcState4CMNETAndCMWAP.class);

    /**
     * 湖南GPRS分为CMNET CMWAP 判断GPRS时特殊处理
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACSvcState4CMNETAndCMWAP() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeSvc.iterator(); iter.hasNext();)
        {
            IData tradesvc = (IData) iter.next();

            String strSvcId = tradesvc.getString("SERVICE_ID");

            if ("98".equals(strSvcId) || "99".equals(strSvcId))
            {
                IData param = new DataMap();
                param.put("USER_ID", databus.getString("USER_ID"));
                IDataset listUserSvcState = Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_WAPNETSVC", param);

                if (listUserSvcState.size() == 1)
                {
                    if ("2".equals(listUserSvcState.getData(0).getString("STATE_CODE")))
                    {
                        if ("98".equals(listUserSvcState.getData(0).getString("SERVICE_ID")))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "业务登记后条件判断:用户的CMNET服务为暂停状态，请恢复，否则不能继续办理GPRS业务！");
                        }
                        else if ("99".equals(listUserSvcState.getData(0).getString("SERVICE_ID")))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "业务登记后条件判断:用户的CMWAP服务为暂停状态，请恢复，否则不能继续办理GPRS业务！");
                        }
                    }
                }
                else if (listUserSvcState.size() == 2)
                {
                    if (!("0".equals(listUserSvcState.getData(0).getString("STATE_CODE")) && "0".equals(listUserSvcState.getData(1).getString("STATE_CODE"))))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "业务登记后条件判断:用户的CMNET和CMWAP服务状态不是全为正常，请恢复，否则不能继续办理GPRS业务！");
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACSvcState4CMNETAndCMWAP() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
