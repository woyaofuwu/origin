
package com.asiainfo.veris.crm.order.soa.person.busi.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DispatcherNpRequestBean.java
 * @Description: 对应老系统TCS_CreateNpTradeRequest 流程
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-21 上午10:20:16 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-21 lijm3 v1.0.0 修改原因
 */
public class DispatcherNpRequestBean extends CSBizBean
{

    public void AddTransferFailJob(String serialNumber, String portInNetId, String x_resultinfo) throws Exception
    {

        IDataset ids = CustomerInfoQry.getNPUNotFitUser(serialNumber, serialNumber, serialNumber, serialNumber, serialNumber);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);
            String userId = data.getString("USER_ID");
            IDataset userScores = AcctCall.queryUserScore(userId);
            if (IDataUtil.isNotEmpty(userScores))
            {
                data.put("SCORE_VALUE", userScores.getData(0).getString("SUM_SCORE"));
            }
            IData productInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(productInfo))
            {
                data.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
                data.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                data.put("PORT_OUT_NETID", portInNetId);
                data.put("RERROR_MESSAGE", x_resultinfo);
                data.put("CUST_CODE", serialNumber);
                SccCall.createTransferFailJob(data);
            }
        }
    }

    public IData dispatcherNpRequest(IData param) throws Exception
    {

        ModifyNpStateBean bean = BeanManager.createBean(ModifyNpStateBean.class);
        bean.transferForNpRequest(param);
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        String serialNumber = param.getString("SERIAL_NUMBER");
        if (StringUtils.isBlank(serialNumber))
        {
            param.put("SERIAL_NUMBER", param.getString("NPCODE"));
        }

        if ("42".equals(tradeTypeCode))
        {
            // 携入生效
            CSAppCall.call("SS.NpOutEffectiveRegSVC.tradeReg", param);// 对应老系统
            // TCS_AcceptApplyDestroyUserReg
        }
        else if ("1503".equals(tradeTypeCode))
        {
            // 总部申请取消
            CSAppCall.call("SS.InNpApplyCancelSVC.tradeReg", param);// 对应老系统
            // TCS_NpApplyCancelCSMS
        }
        else if ("1504".equals(tradeTypeCode))
        {
            // 携出生效取消
            CSAppCall.call("SS.NpApplyCancelOutRegSVC.tradeReg", param);// 对应老系统
            // TCS_NpApplyCancelOut
        }
        else if ("41".equals(tradeTypeCode))
        {
            if (param.getString("IS_ACCORD", "").equals("1"))
            {
                AddTransferFailJob(serialNumber, param.getString("PORT_IN_NETID", ""), param.getString("X_RESULTINFO", ""));
            }
            else
            {
                // 携出申请
                CSAppCall.call("SS.NpOutApplyRegSVC.tradeReg", param);// 对应老系统
                // TCS_AcceptNpOutApply
            }

        }
        else if ("189".equals(tradeTypeCode))
        {
            // 号码归还
            CSAppCall.call("SS.MobileNoReturnRegSVC.tradeReg", param);// 对应老系统
            // TCS_MobileNoReturn
        }
        else if ("47".equals(tradeTypeCode) || "48".equals(tradeTypeCode) || "43".equals(tradeTypeCode) || "45".equals(tradeTypeCode))
        {
            // 47-携入欠费销号 48--携出欠费销号 43--携出欠停 （携入方落地） 45--携出缴开（携入方落地）
            param.put("TRADE_TYPE_CODE", tradeTypeCode);

            CSAppCall.call("SS.CreditTradeRegSVC.tradeReg", param);

        }
        return param;
    }

}
