
package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckSaleActiveForNpUser.java
 * @Description: 用户为携号转网携入成功的用户方可办理这些活动
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-11-7 下午3:26:26
 */
public class CheckSaleActiveForNpUser extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        boolean returnFlag = false;
        String userId = databus.getString("USER_ID");
        String productId = databus.getString("PRODUCT_ID");
        boolean bNpSpecialActive = false;
        IDataset commparaDataset = CommparaInfoQry.getCommByParaAttr("CSM", "1489", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commparaDataset))
        {
            for (int j = 0, jCount = commparaDataset.size(); j < jCount; j++)
            {
                IData commparaData = commparaDataset.getData(j);
                if (StringUtils.equals(productId, commparaData.getString("PARAM_CODE")))
                {
                    bNpSpecialActive = true;
                    break;
                }
            }
        }
        if (bNpSpecialActive)
        {
            IData userData = UcaInfoQry.qryUserInfoByUserId(userId);
            IDataset userNpDataset = UserNpInfoQry.qryUserNpInfosByUserId(userId);
            if (IDataUtil.isNotEmpty(userNpDataset) && IDataUtil.isNotEmpty(userData))
            {
                String removeTag = userData.getString("REMOVE_TAG");
                String npTag = userNpDataset.getData(0).getString("NP_TAG");
                if (StringUtils.equals("0", removeTag) 
                        && (StringUtils.equals("1", npTag) || StringUtils.equals("6", npTag)))
                {
                    returnFlag = true;
                }
            }
        }else {
            returnFlag = true;
        }

        return returnFlag;
    }
}
