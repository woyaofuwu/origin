
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class ChangeElement4BarcodeReg implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        IDataset barcodeConfig = input.getDataset("BARCODE_CONFIG");
        String elementId = "", elementTypeCode = "", modifyTag = "";
        for (int index = 0, size = barcodeConfig.size(); index < size; index++)
        {
            IData configData = barcodeConfig.getData(index);
            if (index != size - 1)
            {
                elementId += configData.getString("ELEMENT_ID") + ",";
                elementTypeCode += configData.getString("ELEMENT_TYPE") + ",";
                modifyTag += configData.getString("OPER_TYPE", "0") + ",";
            }
            else
            {
                elementId += configData.getString("ELEMENT_ID");
                elementTypeCode += configData.getString("ELEMENT_TYPE");
                modifyTag += configData.getString("OPER_TYPE", "0");
            }
        }
        input.put("ELEMENT_ID", elementId);
        input.put("ELEMENT_TYPE_CODE", elementTypeCode);
        input.put("MODIFY_TAG", modifyTag);
        input.put("NUM", barcodeConfig.size());
        IFilterIn changeProductFilter = new ChangeProductFilter();
        changeProductFilter.transferDataInput(input);
    }

}
