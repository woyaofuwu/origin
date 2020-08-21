
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckProductCommRule.java
 * @Description: 产品变更通用规则【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 22, 2014 7:50:44 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 22, 2014 maoke v1.0.0 修改原因
 */
public class CheckProductCommRule extends BreBase implements IBREScript
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
                	String productName = UProductInfoQry.getProductNameByProductId(newProductId);
                    // 不符合办理的产品
                    IDataset commpara7639 = CommparaInfoQry.getCommParas("CSM", "7639", "operation_disabled_product", newProductId, CSBizBean.getTradeEparchyCode());

                    if (IDataUtil.isNotEmpty(commpara7639))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 514123, "您不符合办理新产品【" + productName + "】的条件!" + commpara7639.getData(0).getString("PARA_CODE20", ""));

                        return true;
                    }

                    // 非自然月用户不能办理
                    String acctDay = databus.getString("ACCT_DAY");

                    IDataset commpara8869 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "8869", "PRODUCT", newProductId, CSBizBean.getTradeEparchyCode());

                    if (IDataUtil.isNotEmpty(commpara8869) && !"1".equals(acctDay))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 514123, "您所办理的产品【" + productName + "】要求自然月用户才能办理,该用户当前结账日为:" + acctDay + "号!");

                        return true;
                    }
                }
            }
        }
        return false;
    }
}
