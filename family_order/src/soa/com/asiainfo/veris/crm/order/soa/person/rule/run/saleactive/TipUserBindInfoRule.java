
package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TipUserBindInfoRule.java
 * @Description: 用户合约绑定业务提示信息
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-20 下午4:22:20
 */
public class TipUserBindInfoRule extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");
        IDataset bingUserDataset = UserBindInfoQry.queryUserBindByUserId(userId); // 是否为中高端用户
        if (IDataUtil.isNotEmpty(bingUserDataset))
        {
            bingUserDataset = UserBindInfoQry.queryMaxSaleActiveByUserId(userId);
            if (IDataUtil.isEmpty(bingUserDataset))
            {
                IDataset dataset = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isEmpty(dataset))
                {
                    String msg = "该用户为拍照中高端客户，但从未办理任何捆绑业务，请推荐客户办理营销活动。";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, "", msg);
                    return true;
                }
            }
            else
            {
                IData info = bingUserDataset.getData(0);
                StringBuilder msg = new StringBuilder(300);
                msg.append("该用户为拍照中高端客户，其最后一笔合约捆绑类业务为【").append(info.getString("PRODUCT_NAME")).append("】,此业务在\"").append(info.getString("END_DATE")).append("\"即将到期，请推荐客户办理营销活动.");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, "", msg.toString());
                return true;
            }
        }

        return false;
    }
}
