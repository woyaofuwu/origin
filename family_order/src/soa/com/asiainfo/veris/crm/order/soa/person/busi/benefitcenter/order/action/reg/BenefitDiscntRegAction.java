package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.action.reg;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata.BenefitBindRelReqData;

/**
 * 办理权益绑定对应优惠
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/18 21:46
 */
public class BenefitDiscntRegAction  implements ITradeAction {

    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        BenefitBindRelReqData rd = (BenefitBindRelReqData)btd.getRD();
        String modifyTag = rd.getModifyTag();
        if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
            //新增权益标识需绑定对应优惠
            String discntCode = rd.getDiscntCode();
            String userId = rd.getUca().getUserId();
            IDataset userDiscntInfos = UserDiscntInfoQry.getUserByDiscntCode( userId,discntCode);
            if(IDataUtil.isEmpty(userDiscntInfos)){
                //为绑定对应优惠
                //当年没有绑定权益,说明当年第一次绑定,调产品变更接口办理权益对应优惠
                DataMap param = new DataMap();
                param.put("ELEMENT_TYPE_CODE", "D");
                param.put("ELEMENT_ID", discntCode);
                param.put("MODIFY_TAG", "0");
                param.put("BOOKING_TAG", "0");
                param.put("SERIAL_NUMBER",rd.getUca().getSerialNumber());
                param.put("RIGHT_ID", rd.getRightId());
                param.put("START_DATE", rd.getStartDate());
                param.put("END_DATE", rd.getEndDate());
                //不发送优惠绑定短信
                param.put("IS_NEED_SMS", "false");
                CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
            }
        }
    }
}
