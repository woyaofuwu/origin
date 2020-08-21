
package com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.order.requestdata.SaleActiveTransReqData;

public class SaleActiveTransRegSVC extends OrderService
{
    private static final long serialVersionUID = 669278851452760517L;

    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "530");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "530");
    }

    @SuppressWarnings("unchecked")
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        SaleActiveTransReqData saleActiveTransReqData = (SaleActiveTransReqData) btd.getRD();

        IData activeEndDataParam = new DataMap();

        activeEndDataParam.put("SERIAL_NUMBER", saleActiveTransReqData.getUca().getSerialNumber());
        activeEndDataParam.put("PRODUCT_ID", saleActiveTransReqData.getProductId());
        activeEndDataParam.put("PACKAGE_ID", saleActiveTransReqData.getPackageId());
        activeEndDataParam.put("CAMPN_TYPE", saleActiveTransReqData.getCampnType());
        activeEndDataParam.put("RELATION_TRADE_ID", saleActiveTransReqData.getRelationTradeId());
        activeEndDataParam.put("IS_RETURN", "0");
        activeEndDataParam.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVE_TRANS);
        if(StringUtils.isNotBlank(input.getString("PRE_TYPE")))
        {
        	activeEndDataParam.put("PRE_TYPE", input.getString("PRE_TYPE"));
        }
        CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", activeEndDataParam);

        IDataset tartgetSelectedEelments = saleActiveTransReqData.getTargetSelectedElements();

        if (IDataUtil.isEmpty(tartgetSelectedEelments))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_32, saleActiveTransReqData.getRelationTradeId());
        }

        IData svcParams = new DataMap();

        svcParams.put("SERIAL_NUMBER", saleActiveTransReqData.getTargetSn());
        svcParams.put("CAMPN_TYPE", saleActiveTransReqData.getCampnType());
        svcParams.put("PRODUCT_ID", saleActiveTransReqData.getProductId());
        svcParams.put("PACKAGE_ID", saleActiveTransReqData.getPackageId());
        svcParams.put("START_DATE", saleActiveTransReqData.getTargetStartDate());
        svcParams.put("END_DATE", saleActiveTransReqData.getTargetEndDate());
        svcParams.put("ONNET_START_DATE", saleActiveTransReqData.getTargetOnNetStartDate());
        svcParams.put("ONNET_END_DATE", saleActiveTransReqData.getTargetOnNetEndDate());
        svcParams.put("SELECTED_ELEMENTS", saleActiveTransReqData.getTargetSelectedElements());
        svcParams.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVE_TRANS);
        if(StringUtils.isNotBlank(input.getString("PRE_TYPE")))
        {
        	svcParams.put("PRE_TYPE", input.getString("PRE_TYPE"));
        }
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg", svcParams);
    }
}
