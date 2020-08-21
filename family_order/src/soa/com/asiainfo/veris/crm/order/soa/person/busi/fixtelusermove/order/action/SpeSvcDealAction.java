
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.action;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.requestdata.FixTelUserMoveRequestData;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName:
 * @Description: 固话换号对一些特殊服务：铁通无条件呼叫转移、铁通改号报知服务进行删除和添加
 * @version: v1.0.0
 * @author: dengyong3
 * @date:
 * @Modification History: Date Author Version Description
 */

public class SpeSvcDealAction implements ITradeAction
{

    /**
     * 添加特殊服务
     * 
     * @param btd
     * @param services
     * @throws Exception
     */
    private void createSvcTradeData(BusiTradeData btd) throws Exception
    {

        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();
        String month = telUserMoveRD.getNumChangeData().getMonth();
        if (null == month || month.equals(""))// 没有订购改号报知服务，则退出
            return;

        String endDate = SysDateMgr.getAddMonthsNowday(Integer.parseInt(month), SysDateMgr.getSysTime());
        SvcTradeData svcTradeData = new SvcTradeData();
        svcTradeData.setUserId(telUserMoveRD.getUca().getUserId());
        svcTradeData.setUserIdA("-1");
        svcTradeData.setProductId("-1");
        svcTradeData.setPackageId("-1");
        svcTradeData.setElementId("347");// 改号报知（月租6元）
        svcTradeData.setMainTag("0");
        svcTradeData.setInstId(SeqMgr.getInstId());
        svcTradeData.setStartDate(SysDateMgr.getSysDate());
        svcTradeData.setEndDate(endDate);
        svcTradeData.setRemark("换号订购改号报知服务");
        svcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(telUserMoveRD.getUca().getSerialNumber(), svcTradeData);
    }

    /**
     * 删除特殊服务
     * 
     * @param btd
     * @param services
     * @throws Exception
     */
    private void DelSpecSvcTrade(IDataset services, BusiTradeData btd) throws Exception
    {
        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();

        for (int i = 0; i < services.size(); ++i)
        {
            SvcTradeData svcTradeData = new SvcTradeData(services.getData(i));
            svcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            svcTradeData.setEndDate(SysDateMgr.getSysTime());
            svcTradeData.setRemark("因改号业务而终止该服务");
            btd.add(telUserMoveRD.getUca().getSerialNumber(), svcTradeData);
        }
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();
        IDataset services = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(telUserMoveRD.getUca().getUserId(), "346");// 铁通无条件呼叫转移

        // 终止无条件呼叫转移
        DelSpecSvcTrade(services, btd);

        services = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(telUserMoveRD.getUca().getUserId(), "347");// 铁通改号报知
        // 终止原铁通改号报知服务
        DelSpecSvcTrade(services, btd);

        // 添加新铁通改号报知服务
        createSvcTradeData(btd);
    }
}
