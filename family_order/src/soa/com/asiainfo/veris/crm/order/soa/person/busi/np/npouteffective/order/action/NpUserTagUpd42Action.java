
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpUserTagUpd42Action.java
 * @Description: 对应老系统 NpUserTagUpd42
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-24 下午2:12:01 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-24 lijm3 v1.0.0 修改原因
 */
public class NpUserTagUpd42Action implements ITradeAction
{

    /**
     * @Function: createTradeSvc
     * @Description: //号码归属方为移动，发送指令: //(1)增加携出用户服务： G5500 参数：1，IMSI G002 2，电话号码DN G004 3，本局路由码RN V550
     * @param btd
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-24 下午2:13:39
     */
    public void createTradeSvc(BusiTradeData btd) throws Exception
    {

        SvcTradeData std = new SvcTradeData();
        std.setUserId(btd.getRD().getUca().getUserId());
        std.setUserIdA("-1");
        std.setProductId(btd.getMainTradeData().getProductId());
        std.setPackageId("-1");
        std.setElementId("8888");
        std.setMainTag("0");
        std.setInstId(SeqMgr.getInstId());
        std.setCampnId(btd.getMainTradeData().getCampnId());// 活动标识
        std.setStartDate(btd.getRD().getAcceptTime());
        std.setEndDate(SysDateMgr.END_DATE_FOREVER);
        std.setModifyTag(BofConst.MODIFY_TAG_ADD);
        std.setRemark(btd.getMainTradeData().getRemark());

        btd.add(btd.getRD().getUca().getSerialNumber(), std);
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String serialNumber = btd.getMainTradeData().getSerialNumber();
        IDataset ids = TradeNpQry.getValidTradeNpBySn(serialNumber);
        if (IDataUtil.isNotEmpty(ids))
        {
            if ("1".equals(ids.getData(0).getString("ASP", "1")))
            {
                modiUserTagSet(btd, "4");
                createTradeSvc(btd);// (1)增加携出用户服务： G5500 参数：1，IMSI G002 2，电话号码DN G004 3，本局路由码RN V550
            }
            else
            {
                modiUserTagSet(btd, "8");
            }
        }
    }

    /**
     * @Function: modiUserTagSet
     * @Description: 对应老系统完工流程 PModiUserTagSet
     * @param btd
     * @param tagSet
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-24 下午2:11:40
     */
    public void modiUserTagSet(BusiTradeData btd, String tagSet) throws Exception
    {

        List<UserTradeData> utds = btd.get("TF_B_TRADE_USER");
        if (utds != null)
        {
            for (UserTradeData utd : utds)
            {
                String uts = utd.getUserTagSet();
                if (StringUtils.isNotBlank(uts))
                {
                    uts = tagSet + uts.substring(1);
                    utd.setUserTagSet(uts);
                }
            }
        }
    }

}
