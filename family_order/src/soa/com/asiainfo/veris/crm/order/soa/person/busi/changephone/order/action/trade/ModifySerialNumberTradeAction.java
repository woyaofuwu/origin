
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.action.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RentTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRentInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ModifyPhoneCodeReqData;

/**
 * 改号ACTION处理
 * 
 * @author wangf
 */
public class ModifySerialNumberTradeAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ModifyPhoneCodeReqData phoneReq = (ModifyPhoneCodeReqData) btd.getRD();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        String newSerialNumber = phoneReq.getNewSerialNumber();

        // TF_F_USER
        UserTradeData userData = phoneReq.getUca().getUser();
        UserTradeData tradeUser = userData.clone();
        tradeUser.setSerialNumber(newSerialNumber);
        tradeUser.setModifyTag(BofConst.MODIFY_TAG_UPD);
        btd.add(btd.getRD().getUca().getSerialNumber(), tradeUser);

        // relationuu台帐
        IDataset relationUUSet = RelaUUInfoQry.getRelaUUInfoByUseridB(userId, btd.getRoute(), null);
        for (int i = 0; i < relationUUSet.size(); i++)
        {
            RelationTradeData relationData = new RelationTradeData(relationUUSet.getData(i));
            relationData.setSerialNumberB(newSerialNumber);
            relationData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(btd.getRD().getUca().getSerialNumber(), relationData);
        }
        // 营销活动台帐
        List<SaleActiveTradeData> activeList = phoneReq.getUca().getUserSaleActives();
        for (SaleActiveTradeData activeData : activeList)
        {
            SaleActiveTradeData tradeData = activeData.clone();
            tradeData.setSerialNumber(newSerialNumber);
            tradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(btd.getRD().getUca().getSerialNumber(), tradeData);
        }

        // 用户租机业务
        IDataset userRentSet = UserRentInfoQry.queryUserRentByUserId(userId);
        for (int i = 0; i < userRentSet.size(); i++)
        {
            RentTradeData rentTradeData = new RentTradeData(userRentSet.getData(i));
            rentTradeData.setSerialNumber(newSerialNumber);
            rentTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(btd.getRD().getUca().getSerialNumber(), rentTradeData);
        }
        // TF_F_VPMN_GROUP_MEMBER和TF_F_USER_IMEI 在完工的action里update 处理

    }

}
