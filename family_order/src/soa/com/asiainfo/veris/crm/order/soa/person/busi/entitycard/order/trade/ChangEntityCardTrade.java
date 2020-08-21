/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.requestdata.ChangEntityCardRequestData;

/**
 * @CREATED by gongp@2014-5-29 修改历史 Revision 2014-5-29 下午05:21:36
 */
public class ChangEntityCardTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub

        ChangEntityCardRequestData reqData = (ChangEntityCardRequestData) bd.getRD();

        MainTradeData mainTD = bd.getMainTradeData();

        mainTD.setRsrvStr1("6");// 标志位：实体卡换卡
        mainTD.setRsrvStr2(reqData.getOldCardNo()); // 旧实体卡卡号
        mainTD.setRsrvStr3(reqData.getNewCardNo()); // 新实体卡卡号
        mainTD.setRsrvStr4("EntityCardSale");// 标志位：控制是否插资源销售日志表。
        mainTD.setRsrvStr5(reqData.getRemark());

        this.useEntityCard(reqData.getOldCardNo(), reqData.getNewCardNo());

    }

    public void useEntityCard(String oldCardNo, String newCardNo) throws Exception
    {

        IDataset oldCardInfo = ResCall.undoEntityCardInfo(oldCardNo, oldCardNo, getVisit().getDepartId(), "3");

        if (oldCardInfo.getData(0).getInt("X_RECORDNUM") != 1)
        {
            // THROW_C(216201, "调用资源更新接口出错！");
            CSAppException.apperr(ResException.CRM_RES_11);
        }

        IDataset resSet = ResCall.iEntityCardModifyState(newCardNo, newCardNo, CSBizBean.getVisit().getDepartId(), "3", "0", "", "0", "3");

        if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
        {
            // THROW_C(216201, "调用资源更新接口出错！");
            CSAppException.apperr(CrmUserException.CRM_USER_867);
        }
    }

}
