
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckProductTrans.java
 * @Description: 主产品变更时产品间转换限制【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 21, 2014 10:15:11 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 21, 2014 maoke v1.0.0 修改原因
 */
public class CheckProductTrans extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String userProductId = databus.getString("PRODUCT_ID");// 老产品

                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))
                {
                    IDataset transProductData = ProductInfoQry.getProductTransInfo(userProductId, newProductId);

                    if (IDataUtil.isEmpty(transProductData))
                    {
                        String newProductName = UProductInfoQry.getProductNameByProductId(newProductId);
                        String userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
                        ;

                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500020, "您的产品不能从【" + userProductName + "】变更为【" + newProductName + "】!");

                        return true;
                    }
                }
                if (StringUtils.isNotBlank(newProductId) && userProductId.equals(newProductId))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500021, "用户当前产品与新产品ID相同!");

                    return true;
                }
            }
        }

        return false;
    }
}
