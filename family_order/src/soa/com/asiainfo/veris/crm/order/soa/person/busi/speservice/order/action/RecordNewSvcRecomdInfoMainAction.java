
package com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.action;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata.NewSvcRecomdInfoRequestData;

public class RecordNewSvcRecomdInfoMainAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {

        List<MainTradeData> mainList = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
        NewSvcRecomdInfoRequestData reqData = (NewSvcRecomdInfoRequestData) btd.getRD();

        // mainList.get(0).setRsrvStr1(reqData.getRecomd_product());// 推荐产品
        // mainList.get(0).setRsrvStr2(reqData.getRecomd_product_result());// 推荐产品结果
        // mainList.get(0).setRsrvStr3(reqData.getRecomd_discnt());// 推荐优惠
        // mainList.get(0).setRsrvStr4(reqData.getRecomd_discnt_result());// 推荐优惠结果
        // mainList.get(0).setRsrvStr5(reqData.getRecomd_service());// 推荐服务
        // mainList.get(0).setRsrvStr6(reqData.getRecomd_service_result());// 推荐服务结果
        // mainList.get(0).setRsrvStr7(reqData.getRecomd_platsvc());// 推荐平台业务
        // mainList.get(0).setRsrvStr8(reqData.getRecomd_platsvc_result());// 推荐平台业务结果
        // mainList.get(0).setRsrvStr9(reqData.getRecomd_action());// 推荐活动
        // mainList.get(0).setRsrvStr10(reqData.getRecomd_action_result());// 推荐活动结果
        mainList.get(0).setRemark(reqData.getRemark());// 备注

    }

}
