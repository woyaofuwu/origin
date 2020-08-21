
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;

public class SaleActiveFilter4SchoolTrade implements IFilterIn
{
    public void transferDataInput(IData input) throws Exception
    {
        String operNumb = input.getString("OPER_NUMB");
        if (StringUtils.isBlank(operNumb))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_9);
        }
        String bizCataLog = input.getString("BIZ_CATALOG");
        if (StringUtils.isBlank(bizCataLog))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_10);
        }
        String verifyTime = input.getString("ORDER_TIME");
        if (StringUtils.isBlank(verifyTime))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_11);
        }
        String orderCode = input.getString("ORDER_CODE");
        if (StringUtils.isBlank(orderCode))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_12);
        }
        String serialNumber = input.getString("SERIAL_NUMBER");
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_13);
        }
        String operIdentify = input.getString("OPER_IDENTIFY");
        if (StringUtils.isBlank(operIdentify))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_17);
        }

        IDataset result = OrderPreInfoQry.queryOrderPreInfoByReqId(orderCode);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_18, orderCode);
        }
        IData orderPreData = result.getData(0);
        if (!orderPreData.getString("REPLY_STATE").equals("0"))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_19, orderCode);
        }
    }

}
