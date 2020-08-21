
package com.asiainfo.veris.crm.order.soa.group.taxprint;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class GrpTaxPrintTicketApplySVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset qryTaxApplyByGroupId(IData input) throws Exception
    {
        String operType = input.getString("OPER_TYPE");
        String groupId = input.getString("GROUP_ID");

        return SccCall.qryTaxApplyByGroupId(operType, groupId);
    }

    public IDataset qryNextUser(IData input) throws Exception
    {
        String areaCode = input.getString("AREA_CODE");
        String roleType = input.getString("ROLE_TYPE");

        return AcctCall.qryNextUser(areaCode, roleType);
    }

    public IDataset qryTaxDetailByTradeId(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");

        return GrpTaxPrintTicketApplyBean.qryTaxDetailByTradeId(tradeId);
    }

    public IDataset createApproverReceipt(IData input) throws Exception
    {
        return GrpTaxPrintTicketApplyBean.createApproverReceipt(input);
    }
}
