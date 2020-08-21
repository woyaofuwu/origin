
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class CheckRestoreByProductType extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam paramBreRuleParam) throws Exception
    {
        String productId = dataBus.getString("PRODUCT_ID");
        IDataset ids = ProductInfoQry.getProductInfosByProductId(productId);
        if (IDataUtil.isNotEmpty(ids))
        {
            String productTypeCode = ids.getData(0).getString("PRODUCT_TYPE_CODE");
            if ("0900".equals(productTypeCode))
            {
                dataBus.put("ERROR_MSG", "540007-该用户原来属于随e行用户，不能复机！");
                return true;
            }
            if ("0700".equals(productTypeCode))
            {
                dataBus.put("ERROR_MSG", "540008-该用户原来属于IP直通车用户，不能复机！");
                return true;
            }
        }
        return false;
    }

}
