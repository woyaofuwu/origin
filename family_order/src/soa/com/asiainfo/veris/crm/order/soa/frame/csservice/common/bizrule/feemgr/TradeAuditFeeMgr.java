
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.bizrule.feemgr;

import java.sql.SQLException;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeePayMoneyInfoQry;

/**
 * 本来用于业务登记后进行费用稽核，主要是把cpp的逻辑移到java侧
 * 
 * @author xj
 */
public class TradeAuditFeeMgr
{

    /**
     * 费用稽核 （1）检查台帐主表“营业费用” 与 费用子表“营业费用”是否一致 （2）检查台帐主表“押金” 与 费用子表“押金”是否一致 （3）检查台帐主表“预存” 与 费用子表“预存”是否一致 （4）检查台帐主表费用之和 与
     * 费用子表费用之和是否一致 （5）检查台帐主表费用之和 与 付款费用之和是否一致 Aug 18, 20095:40:53 PM
     * 
     * @author xj
     * @throws Exception
     * @throws SQLException
     */
    public static void auditTradefee(String tradeId) throws SQLException, Exception
    {

        // 获取主台帐信息
        IData odlTradeInfos = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", null);

        if (IDataUtil.isEmpty(odlTradeInfos))
        {
            CSAppException.apperr(FeeException.CRM_FEE_27, tradeId);
        }

        double operFee = 0; // 主台帐“营业费用”
        double foregift = 0; // 主台帐“押金”
        double advancePay = 0; // 主台帐“预存”

        // 从主台帐信息中获取相关费用信息
        operFee = Double.valueOf(odlTradeInfos.getString("OPER_FEE", "0")).doubleValue();
        foregift = Double.valueOf(odlTradeInfos.getString("FOREGIFT", "0")).doubleValue();
        advancePay = Double.valueOf(odlTradeInfos.getString("ADVANCE_PAY", "0")).doubleValue();

        // 获取费用子表信息
        // IData inParams2 = new DataMap();
        // inParams2.put("TRADE_ID", tradeId);
        // IDataset tradeFeeSubInfos = TradefeeSubInfoQry.getTradefeeSubInfoByPk( inParams2);

        // double subOperFee = 0; // 费用子表“营业费用”
        // double subForegift = 0; // 费用子表“押金”
        // double subAdvancePay = 0; // 费用子表“预存”

        // 如果费用子表信息不为空，则从费用子表中获取相关费用信息
        // if (tradeFeeSubInfos.size() > 0)
        // {
        // for (int i = 0; i < tradeFeeSubInfos.size(); i++)
        // {
        // IData tradeFeeSubInfo = tradeFeeSubInfos.getData(i);
        // String feeMode = tradeFeeSubInfo.getString("FEE_MODE");
        // if ("0".equals(feeMode)) // 营业费用
        // {
        // subOperFee = Double.valueOf(tradeFeeSubInfo.getString("FEE", "0")).doubleValue();
        // }
        // else if ("1".equals(feeMode)) // 押金
        // {
        // subForegift = Double.valueOf(tradeFeeSubInfo.getString("FEE", "0")).doubleValue();
        // }
        // else if ("2".equals(feeMode)) // 预存
        // {
        // subAdvancePay = Double.valueOf(tradeFeeSubInfo.getString("FEE", "0")).doubleValue();
        // }
        // }
        // }

        // // 主台帐和费用子表进行费用稽核
        // // 营业费用稽核
        // if (operFee != subOperFee)
        // {
        // CSAppEx.error("台帐费用稽核：费用子表中的营业费用和台帐主表中的数据不符!(" + subOperFee + ", " + operFee + ")(单位：分)");
        // }
        // // 押金稽核
        // if (foregift != subForegift)
        // {
        // CSAppEx.error("台帐费用稽核：费用子表中的押金和台帐主表中的数据不符!(" + subForegift + ", " + foregift + ")(单位：分)");
        // }
        // // 预存稽核
        // if (advancePay != subAdvancePay)
        // {
        // CSAppEx.error("台帐费用稽核：费用子表中的预存和台帐主表中的数据不符!(" + subAdvancePay + ", " + advancePay + ")(单位：分)");
        // }

        double mainTradeFeeSum = 0; // 主台帐费用之和
        // double tradeFeeSubSum = 0; // 费用子表费用之和
        double tradeFeePayMoneySum = 0; // 付款费用之和

        // 检查台帐主表费用之和 与 费用子表费用之和是否一致
        mainTradeFeeSum = operFee + foregift + advancePay;
        // tradeFeeSubSum = subOperFee + subForegift + subAdvancePay;
        // if (mainTradeFeeSum != tradeFeeSubSum)
        // {
        // CSAppEx.error("台帐费用稽核：费用子表中的费用总额和台帐主表中的数据不符!(" + tradeFeeSubSum + ", " + mainTradeFeeSum + ")(单位：分)");
        // }

        IDataset payMoneyInfos = new DatasetList();
        IData feePayData = TradeFeePayMoneyInfoQry.getPayMoneyInfo(tradeId);
        payMoneyInfos.add(feePayData);
        if (payMoneyInfos.size() > 0)
        {
            for (int j = 0; j < payMoneyInfos.size(); j++)
            {
                tradeFeePayMoneySum += Double.valueOf(payMoneyInfos.getData(j).getString("MONEY", "0")).doubleValue();
            }
        }

        // 检查台帐主表费用之和 与 付款费用之和是否一致
        if (mainTradeFeeSum != tradeFeePayMoneySum)
        {
            CSAppException.apperr(FeeException.CRM_FEE_26, tradeFeePayMoneySum, mainTradeFeeSum);
        }
    }

    /**
     * 费用稽核 （1）检查台帐主表“营业费用” 与 费用子表“营业费用”是否一致 （2）检查台帐主表“押金” 与 费用子表“押金”是否一致 （3）检查台帐主表“预存” 与 费用子表“预存”是否一致 （4）检查台帐主表费用之和 与
     * 费用子表费用之和是否一致 （5）检查台帐主表费用之和 与 付款费用之和是否一致 Aug 18, 20095:40:53 PM
     * 
     * @author xj
     */
    public void auditTradefee() throws Exception
    {

        String tradeId = "";
        double mainTradeFeeSum = 0; // 主台帐费用之和
        double tradeFeeSubSum = 0; // 费用子表费用之和
        double tradeFeePayMoneySum = 0; // 付款费用之和
        double operFee = 0; // 主台帐“营业费用”
        double foregift = 0; // 主台帐“押金”
        double advancePay = 0; // 主台帐“预存”
        double subOperFee = 0; // 费用子表“营业费用”
        double subForegift = 0; // 费用子表“押金”
        double subAdvancePay = 0; // 费用子表“预存”

        // 获取主台帐信息
        IData mainTradeInfo = new DataMap();
        IData inParams = new DataMap();
        inParams.put("TRADE_ID", "");
        inParams.put("CANCEL_TAG", "0");
        IData mainTradeInfos = UTradeInfoQry.qryTradeByTradeId(inParams.getString("TRADE_ID"), inParams.getString("CANCEL_TAG"));
        if (IDataUtil.isEmpty(mainTradeInfo))
        {
            CSAppException.apperr(FeeException.CRM_FEE_27, tradeId);
        }

        // 从主台帐信息中获取相关费用信息
        operFee = Double.valueOf(mainTradeInfo.getString("OPER_FEE", "0")).doubleValue();
        foregift = Double.valueOf(mainTradeInfo.getString("FOREGIFT", "0")).doubleValue();
        advancePay = Double.valueOf(mainTradeInfo.getString("ADVANCE_PAY", "0")).doubleValue();

        // 获取费用子表信息
        IData inParams2 = new DataMap();
        inParams2.put("TRADE_ID", tradeId);
        inParams2.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        IDataset tradeFeeSubInfos = Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_1", inParams2);

        // 如果费用子表信息不为空，则从费用子表中获取相关费用信息
        if (tradeFeeSubInfos.size() > 0)
        {
            for (int i = 0; i < tradeFeeSubInfos.size(); i++)
            {
                IData tradeFeeSubInfo = tradeFeeSubInfos.getData(i);
                String feeMode = tradeFeeSubInfo.getString("FEE_MODE");
                if ("0".equals(feeMode)) // 营业费用
                {
                    subOperFee = Double.valueOf(tradeFeeSubInfo.getString("FEE", "0")).doubleValue();
                }
                else if ("1".equals(feeMode)) // 押金
                {
                    subForegift = Double.valueOf(tradeFeeSubInfo.getString("FEE", "0")).doubleValue();
                }
                else if ("2".equals(feeMode)) // 预存
                {
                    subAdvancePay = Double.valueOf(tradeFeeSubInfo.getString("FEE", "0")).doubleValue();
                }
            }
        }

        // 主台帐和费用子表进行费用稽核
        // 营业费用稽核
        if (operFee != subOperFee)
        {
            CSAppException.apperr(FeeException.CRM_FEE_16, subOperFee, operFee);
        }

        // 押金稽核
        if (foregift != subForegift)
        {
            CSAppException.apperr(FeeException.CRM_FEE_47, subForegift, foregift);
        }

        // 预存稽核
        if (advancePay != subAdvancePay)
        {
            CSAppException.apperr(FeeException.CRM_FEE_25, subAdvancePay, advancePay);
        }

        // 检查台帐主表费用之和 与 费用子表费用之和是否一致
        mainTradeFeeSum = operFee + foregift + advancePay;
        tradeFeeSubSum = subOperFee + subForegift + subAdvancePay;
        if (mainTradeFeeSum != tradeFeeSubSum)
        {
            CSAppException.apperr(FeeException.CRM_FEE_47, tradeFeeSubSum, mainTradeFeeSum);
        }

        // 获取付款费用
        IData inParams3 = new DataMap();
        inParams3.put("TRADE_ID", tradeId);
        IDataset payMoneyInfos = Dao.qryByCode("TF_B_TRADEFEE_PAYMONEY", "SEL_BY_TRADE", inParams3);
        if (payMoneyInfos.size() > 0)
        {
            for (int j = 0; j < payMoneyInfos.size(); j++)
            {
                tradeFeePayMoneySum += Double.valueOf(payMoneyInfos.getData(j).getString("MONEY", "0")).doubleValue();
            }
        }

        // 检查台帐主表费用之和 与 付款费用之和是否一致
        if (mainTradeFeeSum != tradeFeePayMoneySum)
        {
            CSAppException.apperr(FeeException.CRM_FEE_47, tradeFeePayMoneySum, mainTradeFeeSum);
        }
    }
}
