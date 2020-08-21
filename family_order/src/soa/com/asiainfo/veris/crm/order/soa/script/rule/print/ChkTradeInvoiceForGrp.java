
package com.asiainfo.veris.crm.order.soa.script.rule.print;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

public class ChkTradeInvoiceForGrp extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        IData tradeData = new DataMap();

        tradeData.putAll(databus);

        String tradeId = tradeData.getString("TRADE_ID");

        String tradeRouteId = tradeData.getString("TRADE_ROUTE_ID");// 台账所在库

        // 受理类型名称
        String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(CSBizBean.getTradeEparchyCode());

        IDataset feeSubList = TradefeeSubInfoQry.qryTradeFeeSubByTradeId(tradeId, tradeRouteId);

        StringBuilder feeContent = new StringBuilder();

        for (int i = 0; i < feeSubList.size(); i++)
        {
            IData feeSubData = feeSubList.getData(i);

            String feeTypeCode = feeSubData.getString("FEE_TYPE_CODE");
            double fee = feeSubData.getDouble("FEE");

            String feeTypeName = FeeItemInfoQry.getFeeItemNameByFeeItemCode(feeTypeCode);

            feeContent.append("~~");
            feeContent.append(feeTypeName);
            feeContent.append(String.format("%3.2f", fee / 100.00));
        }

        IData outData = new DataMap();
        outData.put("TRADE_ID", tradeData.getString("TRADE_ID"));
        outData.put("TRADE_TYPE", tradeTypeName);
        outData.put("OPERATION_DATE", tradeData.getString("ACCEPT_DATE"));

        outData.put("CUST_NAME", tradeData.getString("CUST_NAME"));
        outData.put("SERIAL_NUMBER", null);
        outData.put("STAFF_NAME", getVisit().getStaffName());

        outData.put("FEE_CONTENT", feeContent.toString());

        // 返回数据
        databus.put("OUT_DATA", outData);

        return false;
    }

}
