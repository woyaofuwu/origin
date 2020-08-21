
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: IsExistsBookingChangeProduct.java
 * @Description: 产品变更时用户是否存在预约产品变更【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 21, 2014 10:29:12 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 21, 2014 maoke v1.0.0 修改原因
 */
public class IsExistsBookingChangeProduct extends BreBase implements IBREScript
{
    /**
     * @Description: 是否存在预约产品
     * @param userId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 21, 2014 4:33:14 PM
     */
    public boolean isExistsBookingChangeProduct(String userId) throws Exception
    {
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);

        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();

            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);

                if (userProduct.getString("START_DATE").compareTo(sysDate) >= 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

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
                String newProductId = reqData.getString("NEW_PRODUCT_ID", "");

                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))
                {
                    String userId = reqData.getString("USER_ID");

                    if (this.isExistsBookingChangeProduct(userId))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 500110, "用户存在预约产品,预约产品未生效之前不能再次变更产品!");

                        return true;
                    }
                }
            }
        }
        return false;
    }
}
