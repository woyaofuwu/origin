
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.filter;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;

public class SaleActiveEndFilter4Intf implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_3);
        }

        if (StringUtils.isBlank(input.getString("PRODUCT_ID")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_1);
        }

        if (StringUtils.isBlank(input.getString("PACKAGE_ID")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_2);
        }

        SaleActiveBean saleActiveBean = new SaleActiveBean();
        String campnType = saleActiveBean.getCampnType(input.getString("PRODUCT_ID"));

        if (StringUtils.isBlank(campnType))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_7, input.getString("PRODUCT_ID"));
        }

        input.put("CAMPN_TYPE", campnType);

        if (StringUtils.isBlank(input.getString("RELATION_TRADE_ID")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_33);
        }

    }

}
