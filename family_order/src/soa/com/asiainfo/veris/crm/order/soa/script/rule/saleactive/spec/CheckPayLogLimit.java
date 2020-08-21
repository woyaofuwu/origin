
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 业务提交前校验，仅用于接口。非签约礼包 2013存费送礼营销活动
 * 
 * @author Mr.Z
 */
public class CheckPayLogLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -692338536983985656L;

    private static Logger logger = Logger.getLogger(CheckPayLogLimit.class);

    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckPayLogLimit() >>>>>>>>>>>>>>>>>>");
        }

        String payChargeId = databus.getString("PAY_CHARGE_ID");
        if (StringUtils.isBlank(payChargeId))
            return true;

        if (payChargeId.length() != 7)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062407, "缴费流水" + payChargeId + "长度不是7位!");
            return false;
        }

        IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");
        if (IDataUtil.isNotEmpty(userSaleActives))
        {
            for (int index = 0, size = userSaleActives.size(); index < size; index++)
            {
                String rsrvStr2 = userSaleActives.getData(index).getString("RSRV_STR2");

                if (payChargeId.equals(rsrvStr2))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062407, "缴费流水" + payChargeId + "已经办理过活动，不能再次办理！");
                    return false;
                }
            }
        }

        String userId = databus.getString("USER_ID");

        IData tuxParam = new DataMap();
        tuxParam.put("USER_ID", userId);
        tuxParam.put("PAY_CHARGE_ID", payChargeId); // 缴费流水后7位
        tuxParam.put("VALID_TIME", "25"); // 活动有效期

        IDataset payLogs = new DatasetList(); // TODO QAM_CHECKPAYLOG 调用

        if (IDataUtil.isNotEmpty(payLogs))
        {
            String xResultCode = payLogs.getData(0).getString("X_RESULTCODE");
            String xResultInfo = payLogs.getData(0).getString("X_RESULTINFO");

            if ("0".equals(xResultCode))
            {
                String productId = databus.getString("PRODUCT_ID");
                String packageId = databus.getString("PACKAGE_ID");

                String eparchyCode = databus.getString("EPARCHY_CODE");

                IDataset commpara1008set = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "1008", productId, packageId, eparchyCode);

                if (IDataUtil.isEmpty(commpara1008set))
                    return true;

                int minFeeLimit = Integer.parseInt(commpara1008set.getData(0).getString("PARA_CODE2", "0"));
                int maxFeeLimit = Integer.parseInt(commpara1008set.getData(0).getString("PARA_CODE3", "0"));
                int recvFee = Integer.parseInt(payLogs.getData(0).getString("RECV_FEE", "0"));

                if (!(recvFee >= minFeeLimit && recvFee < maxFeeLimit))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "450016", "缴费流水[" + payChargeId + "]对应的缴费金额[" + recvFee + "]不在包档次要求金额区间[" + minFeeLimit + "][" + maxFeeLimit + "]");
                    return false;
                }
            }
            else
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, xResultCode, xResultInfo);
                return false;
            }
        }
        else
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062407, "调用账务接口[QAM_CHECKPAYLOG]出错！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckPayLogLimit() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
