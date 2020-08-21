
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberSingleReqData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyRole;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * @Description 成员代付关系绑定
 * @Auther: zhenggang
 * @Date: 2020/8/4 15:05
 * @version: V1.0
 */
public class DealFamilyPayAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        FamilyMemberSingleReqData reqData = (FamilyMemberSingleReqData) btd.getRD();
        FamilyRole fr = reqData.getFamilyRole();

        // 不是代付不登记
        if (!fr.isFamilyPay())
        {
            return;
        }

        // 不是手机角色，但是受理号码和手机号码一致时不登记 TODO 预留高级代付，就算魔百和和手机公用一个用户，但是每个都有代付关系
//        if (!FamilyConstants.ROLE.PHONE.equals(fr.getRoleCode()) && reqData.getMainSn().equals(reqData.getUca().getSerialNumber()))
//        {
//            return;
//        }

        String eparchyCode = reqData.getUca().getUserEparchyCode();
        IData input = new DataMap();
        input.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        input.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        input.put("FAMILY_SERIAL_NUMER", reqData.getFamilySn());
        input.put("MEMBER_ROLE_CODE", fr.getRoleCode());
        input.put("MEMBER_REL_INST_ID", fr.getRoleInstId());
        input.put("TAG", fr.getModifyTag());
        IDataset result = CSAppCall.call("SS.FamilyPayRelationRegSVC.tradeReg", input);
        if (IDataUtil.isNotEmpty(result))
        {
            String tradeId = result.first().getString("TRADE_ID");
            String limitTradeId = btd.getTradeId();
            FamilyBusiRegUtil.insertTradeLimit(tradeId, limitTradeId, eparchyCode);
        }
    }

}
