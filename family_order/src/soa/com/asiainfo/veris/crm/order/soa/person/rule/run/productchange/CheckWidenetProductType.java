
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckWidenetProductType extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String userId = databus.getString("USER_ID");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String productMode = "";
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// 查询时校验
        {
            if ("614".equals(tradeTypeCode))
            {
                productMode = "09";
            }
            else if ("616".equals(tradeTypeCode))
            {
                productMode = "11";
            }
            else if ("631".equals(tradeTypeCode))
            {
                productMode = "13";
            }
            else if ("601".equals(tradeTypeCode))
            {
                productMode = "07";
            }
            IDataset userProductInfos = UserProductInfoQry.getUserWidenetProductInfo(userId, productMode);
            if (IDataUtil.isEmpty(userProductInfos))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604020", "未查询到用户订购的有效宽带产品！");
            }

        }

        return false;
    }

}
