
package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 判断用户是否是中高端用户
 */
public class CheckUserTopInfo extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String userId = databus.getString("USER_ID");
        String msg = "";
        if ("0".equals(xChoiceTag))
        {
            IDataset dataset = UserBindInfoQry.queryUserBindByUserId(userId);
            if (IDataUtil.isEmpty(dataset))
            {
                return false;
            }
            IDataset maxset = UserBindInfoQry.queryMaxSaleActiveByUserId(userId);
            if (IDataUtil.isEmpty(maxset))
            {
                IDataset saleActiveInfos = UserSaleActiveInfoQry.queryAllSaleActiveByUserId(userId);
                if (IDataUtil.isEmpty(saleActiveInfos))
                {
                    msg = "该用户为拍照中高端客户，但从未办理任何捆绑业务，请推荐客户办理营销活动。";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 201509, msg);
                }
                return false;
            }

            IData data = maxset.getData(0);
            msg = "该用户为拍照中高端客户，其最后一笔合约捆绑类业务为【" + data.getString("PRODUCT_NAME") + "】,此业务在\"" + data.getString("END_DATE") + "\"到期，请推荐客户办理营销活动.";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 201509, msg);
        }
        return false;
    }

}
