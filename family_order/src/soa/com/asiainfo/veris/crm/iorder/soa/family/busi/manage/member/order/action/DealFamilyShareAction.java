
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberSingleReqData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyRole;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * @Description 家庭共享关系绑定
 * @Auther: zhenggang
 * @Date: 2020/8/4 15:05
 * @version: V1.0
 */
public class DealFamilyShareAction implements ITradeAction
{
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        FamilyMemberSingleReqData reqData = (FamilyMemberSingleReqData) btd.getRD();
        FamilyRole fr = reqData.getFamilyRole();
        if (reqData.getFamilyRole().isFamilyShare() && reqData.getFamilyRole().getRoleCode().equals(FamilyRolesEnum.PHONE))
        {
            String eparchyCode = reqData.getUca().getUserEparchyCode();
            IData input = new DataMap();
            input.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            input.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            input.put("FAMILY_SERIAL_NUMER", reqData.getFamilySn());
            input.put("MEMBER_ROLE_CODE", fr.getRoleCode());
            input.put("MEMBER_REL_INST_ID", fr.getRoleInstId());
            input.put("TAG", fr.getModifyTag());
            IDataset result = CSAppCall.call("SS.GroupShareRelationRegSVC.tradeReg", input);
            if (IDataUtil.isNotEmpty(result))
            {
                String tradeId = result.first().getString("TRADE_ID");
                String limitTradeId = btd.getTradeId();
                FamilyBusiRegUtil.insertTradeLimit(tradeId, limitTradeId, eparchyCode);
            }
        }
    }
}
