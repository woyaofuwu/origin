
package com.asiainfo.veris.crm.order.soa.group.taxprint;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TaxException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tax.TaxLogInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeTaxInfoQry;

public class GrpTaxPrintTicketApplyBean
{

    /**
     * 根据TRADE_ID查询增值税详细信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTaxDetailByTradeId(String tradeId) throws Exception
    {
        IDataset retDataset = new DatasetList();

        IDataset feeTaxList = TradeFeeTaxInfoQry.qryTradeFeeTaxByTradeId(tradeId);

        if (IDataUtil.isEmpty(feeTaxList))
        {
            return retDataset;
        }

        // 遍历信息
        for (int i = 0, row = feeTaxList.size(); i < row; i++)
        {
            IData feeTaxData = feeTaxList.getData(i);

            String feeTypeCode = feeTaxData.getString("FEE_TYPE_CODE", ""); // 费用类型
            float rate = Float.valueOf(feeTaxData.getString("RATE", "0")); // 税率
            float salePrice = Float.valueOf(feeTaxData.getString("SALE_PRICE", "0")) / 100; // 销售价格
            float busiFee = Float.valueOf(feeTaxData.getString("BUSI_FEE", "0")) / 100; // 实收金额
            int count = Integer.valueOf(feeTaxData.getString("COUNT", "1")); // 单价

            String feeItemName = FeeItemInfoQry.getFeeItemNameByFeeItemCode(feeTypeCode);

            feeTaxData.put("FEEITEM_NAME", feeItemName);
            feeTaxData.put("SALE_PRICE", salePrice);
            feeTaxData.put("COUNT", count);
            feeTaxData.put("PRICE", salePrice / count);
            feeTaxData.put("RATE", String.valueOf(rate / 100) + "%");

            // 销售价格不等于实际价格, 产生折扣折让数据
            if (salePrice != busiFee)
            {
                IData discountData = (IData) Clone.deepClone(feeTaxData);

                discountData.put("SALE_PRICE", busiFee - salePrice);
                discountData.put("FEEITEM_NAME", "折扣折让-" + feeItemName);

                retDataset.add(discountData);
            }

            retDataset.add(feeTaxData);
        }

        return retDataset;
    }

    /**
     * 创建申请单信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset createApproverReceipt(IData input) throws Exception
    {
        IData retData = new DataMap();

        String tradeId = input.getString("TRADE_ID");
        String custId = input.getString("CUST_ID");
        String compName = input.getString("COMP_NAME"); // 名称
        String nextUser = input.getString("NEXT_USER"); // 审批人
        String taxApplyId = input.getString("TAX_APPLY_ID"); // 税号
        String bankNo = input.getString("BANK_NO"); // 银行账号
        String remark = input.getString("REMARK"); // 备注
        float totalBusiFee = 0; // 总费用

        // 查询打印日志信息
        IDataset taxLogList = TaxLogInfoQry.qryTaxLogByTradeId(tradeId);

        if (IDataUtil.isEmpty(taxLogList))
        {
            CSAppException.apperr(TaxException.CRM_TAX_16, tradeId);
        }

        IData taxLogData = taxLogList.getData(0);

        IDataset feeTaxList = TradeFeeTaxInfoQry.qryTradeFeeTaxByTradeId(tradeId);

        if (IDataUtil.isEmpty(feeTaxList))
        {
            CSAppException.apperr(TaxException.CRM_TAX_17, tradeId);
        }

        IData vatData = new DataMap(); // 保存税率费用信息

        // 遍历数据
        for (int i = 0, row = feeTaxList.size(); i < row; i++)
        {
            IData feeTaxData = feeTaxList.getData(i);

            String feeTypeCode = feeTaxData.getString("FEE_TYPE_CODE", ""); // 费用类型
            String rate = feeTaxData.getString("RATE", "0"); // 税率
            float salePrice = Float.valueOf(feeTaxData.getString("SALE_PRICE", "0")); // 销售价格
            float busiFee = Float.valueOf(feeTaxData.getString("BUSI_FEE", "0")); // 实收金额
            float discount = Float.valueOf(feeTaxData.getString("DISCOUNT", "0")); // 折扣折让金额

            // 总金额
            totalBusiFee += busiFee;

            // 税率明细
            IData vatDetailData = new DataMap();

            String feeItemName = FeeItemInfoQry.getFeeItemNameByFeeItemCode(feeTypeCode);

            vatDetailData.put("INTEGRATE_ITEM", feeItemName); // 账目名称
            vatDetailData.put("FEE", Math.round(salePrice)); // 含税金额
            vatDetailData.put("F42", rate); // 税率
            vatDetailData.put("F41", "0"); // 缴费卡含税价
            vatDetailData.put("F40", Math.round(discount)); // 折扣折让含税价

            if (vatData.containsKey(rate)) // 同一税率
            {
                IDataset vatDetailList = vatData.getDataset(rate);

                vatDetailList.add(vatDetailData);

            }
            else
            // 不同税率
            {
                IDataset vatDetailList = new DatasetList();

                vatDetailList.add(vatDetailData);

                vatData.put(rate, vatDetailList);
            }
        }

        String taxApplyTaskId = SeqMgr.getTaxApplyTaskId();

        // 调用账务接口
        IData acctSvcData = new DataMap();

        acctSvcData.put("SOURCE_TYPE", "1"); // 渠道来源: 0-账务; 1-营业
        acctSvcData.put("CUST_ID", custId); // 客户标识
        acctSvcData.put("PAY_NAME", compName); // 付费名称
        acctSvcData.put("NEXT_USER", nextUser); // 下一审核人
        acctSvcData.put("TRADE_TIME", SysDateMgr.getSysTime()); // 打票时间
        acctSvcData.put("PRINT_ID", taxApplyTaskId); // 打票流水
        acctSvcData.put("ACCT_ID", taxLogData.getString("ACCT_ID")); // 账务标识
        acctSvcData.put("SERIAL_NUMBER", taxLogData.getString("SERIAL_NUMBER"));
        acctSvcData.put("REMARK", remark);
        acctSvcData.put("VAT_INFOS", vatData);

        IData acctRetData = AcctCall.applyVatInvoice(acctSvcData);

        String receiptId = acctRetData.getString("TASK_ID");

        // 未返回值
        if (StringUtils.isBlank(receiptId))
        {
            CSAppException.apperr(TaxException.CRM_TAX_19);
        }

        // 申请单信息
        IData receiptData = new DataMap();

        receiptData.put("TASK_ID", taxApplyTaskId);
        receiptData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        receiptData.put("RECEIPT_ID", receiptId);
        receiptData.put("TRADE_ID", tradeId);
        receiptData.put("RECEIPT_TYPE", "1");
        receiptData.put("CUST_NAME", compName);
        receiptData.put("TAX_NO", taxApplyId);
        receiptData.put("BNK_ACCTID", bankNo);
        receiptData.put("SERIAL_NUMBER", compName);
        receiptData.put("REMARK", remark);
        receiptData.put("REVIEW", "");
        receiptData.put("PAYEE", Math.round(totalBusiFee));
        receiptData.put("RECEIPT_SATE", "0");
        receiptData.put("PRINTED_TAG", "0");
        receiptData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        receiptData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        receiptData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        // 插入数据
        boolean isSuccess = Dao.insert("TF_LOG_APPROVE_RECEIPT", receiptData,Route.getJourDb(Route.CONN_CRM_CG));

        if (!isSuccess)
        {
            CSAppException.apperr(TaxException.CRM_TAX_18);
        }

        // 设置返回值
        retData.put("ORDER_ID", "申请单号: " + taxApplyTaskId);

        return IDataUtil.idToIds(retData);
    }
}
