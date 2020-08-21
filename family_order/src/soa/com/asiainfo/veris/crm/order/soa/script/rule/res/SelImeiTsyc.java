
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImeiInfoQry;

public class SelImeiTsyc extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(SelImeiTsyc.class);

    /**
     * * 判断该IMEI是否已被用
     * 
     * @param imei
     * @param databus
     * @return
     * @throws Exception
     */
    private boolean checkImeiIsExist(String imei, IData databus) throws Exception
    {
        boolean flag = false;
        String tradeId = databus.getString("TRADE_ID");
        IDataset listUserImei = UserImeiInfoQry.queryByImei(imei);

        for (int i = 0, size = listUserImei.size(); i < size; i++)
        {
            IData userImei = listUserImei.getData(i);
            String rsrvStr1 = userImei.getString("RSRV_STR1");

            // 如果表里的预留字段1的值与当前的TRADEID不一致，则表明不是当前台账的
            if (!tradeId.equals(rsrvStr1))
            {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 判断本次受理的营销活动涉及的手机串号是否已被用
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SelImeiTsyc() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSaleActive = databus.getDataset("TF_B_TRADE_SALE_ACTIVE");
        String tradeId = databus.getString("TRADE_ID");

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeSaleActive = listTradeSaleActive.iterator(); iterTradeSaleActive.hasNext();)
        {
            IData tradeSaleActive = (IData) iterTradeSaleActive.next();
            String imei = tradeSaleActive.getString("RSRV_STR23");

            if (StringUtils.isNotBlank(imei))
            {
                bResult = this.checkImeiIsExist(imei, databus);
                if (bResult)
                {
                    // 如果找到一条则返回TRUE
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SelImeiTsyc() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
