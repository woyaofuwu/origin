
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUserProductMode.java
 * @Description: 非个人和物联网产品模式用户，不能办理某业务
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-1 下午4:59:37
 */
public class CheckUserProductMode extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String productId = databus.getString("PRODUCT_ID");
        IData productData = UProductInfoQry.qryProductByPK(productId);

        if (IDataUtil.isNotEmpty(productData))
        {
            String productMode = productData.getString("PRODUCT_MODE");
            if (!StringUtils.equals("00", productMode) && !StringUtils.equals("15", productMode))
            {
                databus.put("PRODUCT_NAME", productData.getString("PRODUCT_NAME"));
                return true;
            }
        }

        return false;
    }

}
