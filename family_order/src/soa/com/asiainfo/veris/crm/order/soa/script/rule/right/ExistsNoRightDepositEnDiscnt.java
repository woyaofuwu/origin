
package com.asiainfo.veris.crm.order.soa.script.rule.right;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsNoRightDepositEnDiscnt extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsNoRightDepositEnDiscnt.class);

    /**
     * 查找操作员是否有权限办理该笔流水的预存奖励业务优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsNoRightDepositEnDiscnt() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bExists = false;
        IDataset listCommpara = null;

        /* 获取规则配置信息 */
        String strRsrvStr1 = ruleParam.getString(databus, "RSRV_STR7");
        String strStaffId = ruleParam.getString(databus, "TRADE_STAFF_ID");
        String strTradeEparchyCode = ruleParam.getString(databus, "TRADE_EPARCHY_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

        /* 开始逻辑规则校验 */
        if (!"SUPERUSR".equals(strStaffId))
        {
            for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
            {
                IData tradeDiscnt = (IData) iter.next();

                bExists = false;
                if ("0".equals(tradeDiscnt.getString("MODIFY_TAG")))
                {
                    if (listCommpara == null)
                    {
                        listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 71, strRsrvStr1, strTradeEparchyCode);
                    }

                    for (Iterator iterator = listCommpara.iterator(); iterator.hasNext();)
                    {
                        IData commparam = (IData) iterator.next();

                        if (commparam.getString("RSRV_STR2").equals(tradeDiscnt.getString("DISCNT_CODE")))
                        {
                            bExists = true;
                            break;
                        }
                    }

                    if (bExists)
                    {
                        bExists = !StaffPrivUtil.isDistPriv(strStaffId, tradeDiscnt.getString("DISCNT_CODE"));
                    }
                }

                if (bExists)
                {
                    bResult = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsNoRightDepositEnDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
