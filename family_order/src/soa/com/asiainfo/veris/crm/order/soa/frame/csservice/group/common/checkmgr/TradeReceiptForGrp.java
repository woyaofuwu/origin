
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPayModeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPsptTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctConsignInfoQry;

public class TradeReceiptForGrp
{

    public static IData getPrintData(IData inData) throws Exception
    {

        IData outData = new DataMap();
        IData tradeInfo = null;

        if (!inData.containsKey("TF_B_TRADE"))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_52);
        }

        tradeInfo = (IData) inData.get("TF_B_TRADE");

        StringBuilder RECEIPT_INFO1 = new StringBuilder();
        StringBuilder RECEIPT_INFO2 = new StringBuilder();
        StringBuilder RECEIPT_INFO3 = new StringBuilder();
        StringBuilder RECEIPT_INFO4 = new StringBuilder();
        StringBuilder RECEIPT_INFO5 = new StringBuilder();

        // 受理员工姓名
        String staffName = UStaffInfoQry.getStaffNameByStaffId(tradeInfo.getString("TRADE_STAFF_ID"));

        // 受理员工部门
        String departName = UDepartInfoQry.getDepartNameByDepartId(tradeInfo.getString("TRADE_DEPART_ID"));

        // 受理类型名称
        String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeInfo.getString("TRADE_TYPE_CODE"));

        // 产品名称
        String productName = UProductInfoQry.getProductNameByProductId(tradeInfo.getString("PRODUCT_ID"));

        // 品牌名称
        String brandName = UBankInfoQry.getBankNameByBankCode(tradeInfo.getString("BRAND_CODE"));

        String idenChkName = "身份校验";

        IData param = new DataMap();

        // 集团客户编码
        String grpCustID = tradeInfo.getString("CUST_ID");

        IData idsGrp = UcaInfoQry.qryGrpInfoByCustId(grpCustID);

        String grpCustName = "";
        String grpCustAddr = "";
        String grpCustTele = "";
        String grpIdNo = "";
        String grpIdType = "";
        String grpIdType_Name = "";
        String grpYB = "";

        if (IDataUtil.isNotEmpty(idsGrp))
        {
            IData id = idsGrp;

            grpCustName = id.getString("CUST_NAME", "");
            grpCustAddr = id.getString("GROUP_ADDR", "");
            grpCustTele = id.getString("GROUP_CONTACT_PHONE", "");
            grpIdType = id.getString("BUSI_LICENCE_TYPE", "");
            grpIdType_Name = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", "DATA_ID", "DATA_NAME", grpIdType);
            grpIdNo = id.getString("BUSI_LICENCE_NO", "");
            grpYB = id.getString("POST_CODE", "");
        }

        // 集团用户ID
        String grpUserId = tradeInfo.getString("USER_ID");

        IData idsUser = UcaInfoQry.qryGrpInfoByCustId(grpCustID);

        // 集团帐户标识
        String grpActId = tradeInfo.getString("ACCT_ID");// 集团的账户ID

        IData acctIDset = UcaInfoQry.qryAcctInfoByAcctId(grpActId);

        IData acctData = new DataMap();

        String pay_mode_code = "";
        String pay_mode_name = "";

        if (IDataUtil.isNotEmpty(acctIDset))
        {
            acctData = (IData) acctIDset.get(0);

            pay_mode_code = acctData.getString("PAY_MODE_CODE", "");
            pay_mode_name = UPayModeInfoQry.getPayModeNameByPayModeCode(pay_mode_code);
        }

        String bank_name = "";

        if ("1".equals(pay_mode_code)) // 托收
        {
            IDataset bank_info = AcctConsignInfoQry.getConsignInfoByAcctIdForGrp(grpActId);

            IData acct_bank = new DataMap();

            if (bank_info.size() > 0)
            {
                acct_bank = (IData) bank_info.get(0);
                bank_name = acct_bank.getString("BANK_ACCT_NAME", "");
            }
        }

        // 成员用户信息
        String memCustId = tradeInfo.getString("CUST_ID");

        IData idsMeb = UcaInfoQry.qryPerInfoByCustId(memCustId);

        IData idmeb = new DataMap();

        String memPsptType = "";
        String memPsptTypeName = "";
        String memPsptid = "";
        String memAddr = "";
        String mebPhone = "";

        if (idsMeb.size() > 0)
        {
            idmeb = (IData) idsMeb.get(0);

            memPsptType = idmeb.getString("PSPT_TYPE_CODE", "");
            memPsptTypeName = UPsptTypeInfoQry.getPsptTypeName(CSBizBean.getTradeEparchyCode(), memPsptType);// 成员证件类型
            memPsptid = idmeb.getString("PSPT_ID", "");
            memAddr = idmeb.getString("PSPT_ADDR", "");
            mebPhone = idmeb.getString("PHONE", "");
        }

        // 免填单
        if (inData.getString("TEMPLET_TYPE").equals("3"))
        {
            RECEIPT_INFO1.append("客户名称：%CUST_NAME!");

            RECEIPT_INFO1.append("~~");
            RECEIPT_INFO1.append("服务号码：%SERIAL_NUMBER!");

            RECEIPT_INFO1.append("~~");
            RECEIPT_INFO1.append("客户地址：%GROUP_ADDR!");

            RECEIPT_INFO1.append("~~");
            RECEIPT_INFO1.append("联系电话：%GROUP_CONTACT_PHONE!");

            RECEIPT_INFO1.append("~~");
            RECEIPT_INFO1.append("证件号码：%BUSI_LICENCE_NO!");

            if ("1".equals(pay_mode_code)) // 托收
            {
                RECEIPT_INFO1.append("~~");
                RECEIPT_INFO1.append("开户银行：%BANK_ACCT_NAME!");
            }

            RECEIPT_INFO2.append("~~");
            RECEIPT_INFO2.append("邮编：%POST_CODE!");

            RECEIPT_INFO2.append("~~");
            RECEIPT_INFO2.append("证件类型：%PSPT_TYPE!");

            RECEIPT_INFO2.append("~~");
            RECEIPT_INFO2.append("帐户类型：%PAY_MODE!");

            RECEIPT_INFO3.append("业务类型：%TRADE_TYPE!");

            RECEIPT_INFO3.append("~~");
            RECEIPT_INFO3.append("受理方式：%IN_MODE_CODE!");

            RECEIPT_INFO3.append("~~");
            RECEIPT_INFO3.append("集团成员客户名称：%CUST_MEM_NAME!");

            RECEIPT_INFO3.append("~~");
            RECEIPT_INFO3.append("客户地址：%PSPT_ADDR!");

            RECEIPT_INFO3.append("~~");
            RECEIPT_INFO3.append("联系电话：%MEM_PHONE!");

            RECEIPT_INFO3.append("~~");
            RECEIPT_INFO3.append("证件类型：%MEM_PSPT_TYPE!");

            RECEIPT_INFO3.append("~~");
            RECEIPT_INFO3.append("证件号码：%MEM_PSPT_ID!");

            RECEIPT_INFO3.append("~~");
            RECEIPT_INFO3.append("品牌：%BAND!");

            RECEIPT_INFO3.append("            产品：%PRODUC_NAME!");

            RECEIPT_INFO5.append("业务流水号：%TRADE_ID!");

            RECEIPT_INFO5.append("~~");
            RECEIPT_INFO5.append("营业厅：%DEPART_NAME!");

            RECEIPT_INFO5.append("~~");
            RECEIPT_INFO5.append("受理员工：%TRADE_STAFF_ID!");

            RECEIPT_INFO5.append("~~");
            RECEIPT_INFO5.append("受理时间：%ACCEPT_DATE!");

            outData.put("RECEIPT_INFO2", RECEIPT_INFO2);
            outData.put("RECEIPT_INFO3", RECEIPT_INFO3);
            outData.put("RECEIPT_INFO4", RECEIPT_INFO4);
            outData.put("RECEIPT_INFO5", RECEIPT_INFO5);
        }

        // 发票
        if (inData.getString("TEMPLET_TYPE").equals("0"))
        {
            IData operFeeData = inData.getData("RECEIPT_INFO1");
            if (operFeeData != null && operFeeData.size() > 0)
            {
                Iterator<String> iterator = operFeeData.keySet().iterator();
                while (iterator.hasNext())
                {
                    String key = iterator.next();
                    RECEIPT_INFO1.append("~~    " + key + "：    " + operFeeData.getString(key, "0"));
                }
            }

            outData.put("ALL_MONEY_UPPER", inData.getString("ALL_MONEY_UPPER"));
            outData.put("ALL_MONEY_LOWER", inData.getString("ALL_MONEY_LOWER"));
        }

        outData.put("RECEIPT_INFO1", RECEIPT_INFO1);
        outData.put("TEMPLET_TYPE", inData.getString("TEMPLET_TYPE"));
        outData.put("X_RESULTCODE", "0");
        outData.put("X_RESULTINFO", "TradeOK!");

        return outData;
    }

}
