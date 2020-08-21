
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

/**
 * 集团成员变更服务转换
 * 
 * @author penghaibo
 */
public class BatChgMemElementDistributeTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        String svcName = "";

        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

        if ("BOSG".equals(brandCode))
        {
            svcName = "CS.ChangeBBossMemSVC.crtOrder";
        }
        else if ("10005742".equals(productId))
        {
            svcName = "SS.ChangeAdcMemElementSVC.crtOrder";
        }
        else
        {
            svcName = "CS.ChangeMemElementSvc.changeMemElement";
        }

        svcData.put("REAL_SVC_NAME", svcName);
    }
}
