
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckSchoolProductExistsJWTSvc.java
 * @Description: 办理某校园卡产品不能存在监务通服务【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 22, 2014 2:36:17 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 22, 2014 maoke v1.0.0 修改原因
 */
public class CheckSchoolProductExistsJWTSvc extends BreBase implements IBREScript
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
                    if ("10001005".equals(newProductId) || "10001139".equals(newProductId))
                    {
                        String productName = UProductInfoQry.getProductNameByProductId(newProductId);

                        UcaData ucaData = (UcaData) databus.get("UCADATA");

                        List<SvcTradeData> svcData = ucaData.getUserSvcBySvcId("5860");

                        if (svcData != null && svcData.size() > 0)
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 514315, "#监务通用户，不允许办理校园卡【" + productName + "】产品！");

                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }
}
