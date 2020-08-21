
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;

public class SaleActive4BarcodeReg implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        IDataset barcodeConfig = input.getDataset("BARCODE_CONFIG");
        for (int x = 0, size = barcodeConfig.size(); x < size; x++)
        {
            IData activeParam = barcodeConfig.getData(x);
            String elementTypeCode = activeParam.getString("ELEMENT_TYPE");
            if ("P".equals(elementTypeCode))
            {
                String productId = activeParam.getString("ELEMENT_ID", "");
                input.put("PRODUCT_ID", productId);
                if (!"".equals(productId))
                {
                    SaleActiveBean saleActiveBean = new SaleActiveBean();
                    String campnType = saleActiveBean.getCampnType(productId);
                    input.put("CAMPN_TYPE", campnType);
                }
            }
            if ("K".equals(elementTypeCode))
            {
                input.put("PACKAGE_ID", activeParam.getString("ELEMENT_ID"));
            }
        }
        // ======SALEGOODS_IMEI
        String resNo = input.getString("RES_CODE", "");
        if (!"".equals(resNo))
        {
            input.put("SALEGOODS_IMEI", resNo);
        }
    }

}
