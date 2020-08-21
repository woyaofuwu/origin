
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product.PackageSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class BatPackageSVC extends PackageSVC
{
    private static final long serialVersionUID = 1L;

    public IDataset getPackageElements(IData param) throws Exception
    {
        IDataset pachageElements = ProductInfoQry.getPackageElements(param.getString("PACKAGE_ID"), param.getString("EPARCHY_CODE"));
        if (IDataUtil.isNotEmpty(pachageElements))
        {
            for (int i = 0; i < pachageElements.size(); i++)
            {
                IData pachageElement = pachageElements.getData(i);
                if (IDataUtil.isNotEmpty(pachageElement))
                {
                    String elementId = pachageElement.getString("ELEMENT_ID");
                    if ("15".equals(elementId))
                    {
                        pachageElements.remove(i);
                    }
                }
            }
        }
        return pachageElements;
    }

}
