
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class ChangeProduct4BarcodeReg implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        IDataset barcodeConfig = input.getDataset("BARCODE_CONFIG");
        String discnt = "", productId = "";
        int discntIndex = 1;
        for (int index = 0, size = barcodeConfig.size(); index < size; index++)
        {

            IData configData = barcodeConfig.getData(index);
            String elementTypeCode = configData.getString("ELEMENT_TYPE");
            if ("P".equals(elementTypeCode))
            {
                input.put("ELEMENT_TYPE_CODE", "P");
                input.put("ELEMENT_ID", configData.getString("ELEMENT_ID"));
                input.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            }
            else if ("D".equals(elementTypeCode))
            {
                String key = "DISCNT_STR" + discntIndex;
                input.put(key, configData.getString("ELEMENT_ID"));
                discntIndex++;
            }
        }
        IFilterIn changeProductFilter = new ChangeProductFilter();
        changeProductFilter.transferDataInput(input);
    }
}
