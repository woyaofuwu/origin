
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckChangeProductDiscnt.java
 * @Description: 校验产品变更时是否有有效的资费【auth】
 * @version: v1.0.0
 * @author: likai3
 * @date: May 23, 2014 2:55:01 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 23, 2014 likai3 v1.0.0 修改原因
 */
public class CheckChangeProductDiscnt extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String errorMsg = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// auth组建调用的时候校验
        {
            IDataset userDiscntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(databus.getString("USER_ID"));
            for (int i = 0; i < userDiscntInfos.size(); i++)
            {
                if (userDiscntInfos.getData(i).getString("PRODUCT_ID").equals("-1"))
                {
                    userDiscntInfos.remove(i);
                }
            }
            if (IDataUtil.isEmpty(userDiscntInfos))
            {
                // errorMsg = "当前用户没有一个有效的优惠,请续费后再进行产品变更!";
                // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
                return true;
            }
        }
        return false;
    }
}
