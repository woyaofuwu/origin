
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo.CreateRedMemberBean;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DeleteBlackUserInfoAction.java
 * @Description: 黑名单用户信息修改TL_B_BLACKUSER表的状态，当trade_type_code为102时，在TL_B_BLACKUSER表删除用户
 * @version: v1.0.0
 * @author: zhuyu
 * @date: 2014-6-18 下午3:10:00
 */
public class DeleteBlackUserInfoAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = "86" + btd.getRD().getUca().getSerialNumber();
        IDataset blackInfos = UserBlackInfoQry.getBlackUserInfo(userId);

        if (IDataUtil.isNotEmpty(blackInfos))
        {
            CreateRedMemberBean bean = (CreateRedMemberBean) BeanManager.createBean(CreateRedMemberBean.class);
            String processTag = blackInfos.getData(0).getString("PROCESS_TAG");

            if ("2".equals(processTag))
            {
                IData inParam = new DataMap();
                inParam.put("USER_ID", userId);
                inParam.put("EFFECT_TAG", "1");
                inParam.put("PROCESS_TAG", "2");
                inParam.put("REMARK", "黑名单用户");
                bean.updateBlackUser(inParam);
            }
            else if ("1".equals(processTag))
            {
                IData inParam = new DataMap();
                inParam.put("USER_ID", userId);
                inParam.put("PROCESS_TAG", "1");
                bean.updateExitBlackUser(inParam);
                inParam.clear();
                inParam.put("USER_ID", userId);
                inParam.put("PROCESS_TAG", "2");
                inParam.put("SERIAL_NUMBER", serialNumber);
                bean.InsertBlackUser(inParam);
            }
        }

    }

}
