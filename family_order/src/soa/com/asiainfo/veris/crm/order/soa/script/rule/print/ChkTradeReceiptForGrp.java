
package com.asiainfo.veris.crm.order.soa.script.rule.print;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.encrypt.DesUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPsptTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeImpuInfoQry;

public class ChkTradeReceiptForGrp extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        IData tradeData = new DataMap();

        tradeData.putAll(databus);

        String tradeRouteId = tradeData.getString("TRADE_ROUTE_ID");

        // 受理类型名称
        String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeData.getString("TRADE_TYPE_CODE"));

        // 产品名称
        String productName = UProductInfoQry.getProductNameByProductId(tradeData.getString("PRODUCT_ID"));

        // 品牌名称
        String brandName = UBrandInfoQry.getBrandNameByBrandCode(tradeData.getString("BRAND_CODE"));

        // 集团客户编码
        String grpCustId = tradeData.getString("CUST_ID_B", "");
        String grpCustName = "";

        IData grpCustList = UcaInfoQry.qryCustomerInfoByCustIdForGrp(grpCustId);

        if (IDataUtil.isNotEmpty(grpCustList))
        {
            grpCustName = grpCustList.getString("CUST_NAME");
        }

        // 成员客户编码
        String mebCustId = tradeData.getString("CUST_ID", "");

        IData mebCustList = UcaInfoQry.qryPerInfoByCustId(mebCustId, tradeRouteId);

        String mebPsptType = "";
        String mebPsptTypeName = "";
        String mebPsptId = "";
        String mebAddr = "";
        String mebPhone = "";

        if (IDataUtil.isNotEmpty(mebCustList))
        {
            IData custData = mebCustList;
            mebPsptType = custData.getString("PSPT_TYPE_CODE", "");
            mebPsptTypeName = UPsptTypeInfoQry.getPsptTypeName(CSBizBean.getTradeEparchyCode(), mebPsptType);
            mebPsptId = custData.getString("PSPT_ID", "");
            mebAddr = custData.getString("PSPT_ADDR", "");
            mebPhone = custData.getString("PHONE", "");
        }

        // 免填单信息
        StringBuilder receiptInfo_1 = new StringBuilder();
        StringBuilder receiptInfo_2 = new StringBuilder();
        StringBuilder receiptInfo_3 = new StringBuilder();
        StringBuilder receiptInfo_4 = new StringBuilder();
        StringBuilder receiptInfo_5 = new StringBuilder();

        // 公用信息
        receiptInfo_1.append("~~");
        receiptInfo_1.append("业务类型：");
        receiptInfo_1.append(tradeTypeName);

        receiptInfo_1.append("~~");
        receiptInfo_1.append("受理方式：身份校验");

        // 集团信息
        receiptInfo_1.append("~~");
        receiptInfo_1.append("集团客戶名称：");
        receiptInfo_1.append(grpCustName);

        receiptInfo_1.append("~~");
        receiptInfo_1.append("集团服务号码：");
        receiptInfo_1.append(tradeData.getString("SERIAL_NUMBER_B"));

        // 成员信息
        receiptInfo_2.append("~~");
        receiptInfo_2.append("客戶地址：");
        receiptInfo_2.append(mebAddr);

        receiptInfo_2.append("~~");
        receiptInfo_2.append("联系电话：");
        receiptInfo_2.append(mebPhone);

        receiptInfo_2.append("~~");
        receiptInfo_2.append("证件类型：");
        receiptInfo_2.append(mebPsptTypeName);

        receiptInfo_2.append("~~");
        receiptInfo_2.append("证件号码：");
        receiptInfo_2.append(mebPsptId);

        receiptInfo_2.append("~~");
        receiptInfo_2.append("品牌：");
        receiptInfo_2.append(brandName);
        receiptInfo_2.append("            产品：");
        receiptInfo_2.append(productName);

        // 查询IMPU信息
        IDataset tradeImpuList = TradeImpuInfoQry.qryTradeImpuInfo(tradeData.getString("TRADE_ID"));

        if (IDataUtil.isNotEmpty(tradeImpuList))
        {
            String imsPassWord = tradeImpuList.getData(0).getString("IMS_PASSWORD");
            DesUtils desUtil = new DesUtils(tradeData.getString("USER_ID"));
            receiptInfo_2.append("~~");
            receiptInfo_2.append("IMS新密码：");
            receiptInfo_2.append(desUtil.decrypt(imsPassWord));
        }

        IData outData = new DataMap();

        outData.put("TRADE_ID", tradeData.getString("TRADE_ID"));
        outData.put("REMARK", tradeData.getString("REMARK"));
        outData.put("SERIAL_NUMBER", tradeData.getString("SERIAL_NUMBER", ""));
        outData.put("TRADE_TYPE_NAME", tradeTypeName);
        outData.put("IDEN_CHK_NAME", "身份校验");// 业务受理方式
        outData.put("CUST_NAME", tradeData.getString("CUST_NAME"));
        outData.put("DEPART_NAME", getVisit().getDepartName());
        outData.put("TRADE_STAFF_ID", tradeData.getString("TRADE_STAFF_ID", ""));
        outData.put("TRADE_STAFF_NAME", getVisit().getStaffName());

        String acceptDate = tradeData.getString("ACCEPT_DATE");

        // 受理时间
        String strY = acceptDate.substring(0, 4);
        String strM = acceptDate.substring(5, 7);
        String strD = acceptDate.substring(8, 10);
        String strT = acceptDate.substring(11, 19);

        outData.put("TRADE_YEAR", strY);
        outData.put("TRADE_MONTH", strM);
        outData.put("TRADE_DAY", strD);
        outData.put("TRADE_TIME", strT);

        outData.put("RECEIPT_INFO1", receiptInfo_1.toString());
        outData.put("RECEIPT_INFO2", receiptInfo_2.toString());
        outData.put("RECEIPT_INFO3", receiptInfo_3.toString());
        outData.put("RECEIPT_INFO4", receiptInfo_4.toString());
        outData.put("RECEIPT_INFO5", receiptInfo_5.toString());

        // 返回数据
        databus.put("OUT_DATA", outData);

        return false;
    }

}
