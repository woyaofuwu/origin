
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;

public class SaleActiveFilter4Check implements IFilterIn
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

        SaleActiveBean saleActiveBean = new SaleActiveBean();
        String campnType = saleActiveBean.getCampnType(input.getString("PRODUCT_ID"));

        if (StringUtils.isBlank(campnType))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_7, input.getString("PRODUCT_ID"));
        }

        input.put("CAMPN_TYPE", campnType);

        if (StringUtils.isBlank(input.getString("PACKAGE_ID")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_2);
        }

//        ProductInfoQry productInfoQry = new ProductInfoQry();
//        IData prodcutInfos = productInfoQry.getProductPackageRelNoPriv(input.getString("PACKAGE_ID"), input.getString("PRODUCT_ID"), "0898");
        IDataset offers = UpcCall.qryOffersByCatalogIdAndOfferId(input.getString("PRODUCT_ID"), input.getString("PACKAGE_ID"), BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        if (IDataUtil.isEmpty(offers))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您传入的包" + input.getString("PACKAGE_ID") + "不是产品" + input.getString("PRODUCT_ID") + "下的包！");
        }

        if (!"YX04".equals(campnType))
        {
            IDataset terminalOrderInfos = TerminalOrderInfoQry.qryTerminalOrderInfoBySn(input.getString("SERIAL_NUMBER"));

            if (IDataUtil.isNotEmpty(terminalOrderInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您好，由于您当前存在" + terminalOrderInfos.getData(0).getString("RSRV_STR3") + "活动的预约，无法办理当前的活动！");
            }
        }

        if (StringUtils.isNotBlank(input.getString("TERMINAL_ID")))
        {
            input.put("SALEGOODS_IMEI", input.getString("TERMINAL_ID"));
        }

        input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
    }

}
