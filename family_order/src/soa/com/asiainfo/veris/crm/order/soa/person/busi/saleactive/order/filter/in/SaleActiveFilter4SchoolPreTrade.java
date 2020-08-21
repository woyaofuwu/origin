
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;

public class SaleActiveFilter4SchoolPreTrade implements IFilterIn
{
    public void transferDataInput(IData input) throws Exception
    {
        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_3);
        }

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
        String verifyTime = input.getString("VERIFY_TIME");
        if (StringUtils.isBlank(verifyTime))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_11);
        }
        String orderCode = input.getString("ORDER_CODE");
        if (StringUtils.isBlank(orderCode))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_12);
        }
        String promotionCode = input.getString("PROMOTION_CODE");
        if (StringUtils.isBlank(promotionCode))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_14);
        }

        IDataset promotionSet = SaleActiveInfoQry.queryCustSalePromotionInfo(promotionCode);

        if (IDataUtil.isEmpty(promotionSet))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_35, promotionCode);
        }

        input.put("PRODUCT_ID", promotionSet.getData(0).getString("PRODUCT_ID"));
        input.put("PACKAGE_ID", promotionSet.getData(0).getString("PACKAGE_ID"));

        SaleActiveBean saleActiveBean = new SaleActiveBean();
        String campnType = saleActiveBean.getCampnType(input.getString("PRODUCT_ID"));

        if (StringUtils.isBlank(campnType))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_7, input.getString("PRODUCT_ID"));
        }

        input.put("CAMPN_TYPE", campnType);

        input.put("PRE_TYPE", SaleActiveConst.PRE_TYPE_SALE_ACTIVE_SCHOOL);
        input.put("PRE_ID", SeqMgr.getOrderId());
    }
}
