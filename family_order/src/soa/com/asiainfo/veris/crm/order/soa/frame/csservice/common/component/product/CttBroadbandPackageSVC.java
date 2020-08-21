
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.CttBroadbandProductInfoQry;

public class CttBroadbandPackageSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset getPackageElements(IData param) throws Exception
    {
        String packageId = param.getString("PACKAGE_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");
        String userId = param.getString("USER_ID");
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE", "9712");
        String areaCode = param.getString("ARAE_CODE", "");
        IDataset discnts = null;

        if (!StringUtils.equals("9711", tradeTypeCode) && !StringUtils.equals("9712", tradeTypeCode))
        {
            PackageSVC packageSvc = new PackageSVC();
            discnts = packageSvc.getPackageElements(param);
        }
        else
        {
            discnts = CttBroadbandProductInfoQry.getDiscntElementByCondition(packageId, userId, eparchyCode, tradeTypeCode, areaCode);
        }

        return discnts;
    }
}
