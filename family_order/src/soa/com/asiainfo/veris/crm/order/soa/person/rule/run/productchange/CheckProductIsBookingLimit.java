
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckProductIsBookingLimit.java
 * @Description: 校验当前要变更的产品只支持预约变更【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 21, 2014 10:27:12 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 21, 2014 maoke v1.0.0 修改原因
 */
public class CheckProductIsBookingLimit extends BreBase implements IBREScript
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
                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String bookingDate = reqData.getString("BOOKING_DATE");// 预约时间

                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId) && !ProductUtils.isBookingChange(bookingDate))// 预约时间为空或者预约时间小于当前时间
                {
                    IDataset commpara8859 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "8859", "PRODUCT", newProductId, CSBizBean.getTradeEparchyCode());

                    if (IDataUtil.isNotEmpty(commpara8859))
                    {
                        String newProductName = UProductInfoQry.getProductNameByProductId(newProductId);

                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500023, "您当前要变更的产品【" + newProductName + "】只支持预约变更!");

                        return true;
                    }
                }
            }
        }

        return false;
    }
}
