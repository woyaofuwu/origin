
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class IsCustProduct extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if ("BaseInfo".equals(databus.getString("CHK_FLAG")))
        {
            String custId = databus.getString("CUST_ID");
            String productId = databus.getString("PRODUCT_ID");
            String inModeCode = databus.getString("IN_MODE_CODE");

            String ruleBizId = databus.getString("RULE_BIZ_ID");

            // 查询是否可以重复订购配置
            IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CGM", "5", productId, "ZZZZ");

            if (IDataUtil.isNotEmpty(commparaList))
            {
                return false;
            }

            // 查询用户品牌信息
            String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);

            // BBOSS接口可以办理多次
            if ("BOSG".equals(brandCode) && "6".equals(inModeCode))
            {
                return false;
            }

            // 查询用户信息
            IDataset userList = UserInfoQry.getUserInfoByCstIdProIdForGrp(custId, productId, null);

            if (IDataUtil.isNotEmpty(userList))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, ruleBizId, "集团客户已定购此产品, 同一产品只能办理一次!");
            }
        }

        return false;
    }

}
