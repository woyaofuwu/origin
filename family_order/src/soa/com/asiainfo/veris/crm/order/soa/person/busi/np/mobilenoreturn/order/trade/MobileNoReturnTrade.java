
package com.asiainfo.veris.crm.order.soa.person.busi.np.mobilenoreturn.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.np.mobilenoreturn.order.requestdata.MobileNoReturnReqData;

public class MobileNoReturnTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        MobileNoReturnReqData reqData = (MobileNoReturnReqData) btd.getRD();
        btd.getMainTradeData().setRemark("总部发起的号码归还告知");
        btd.getMainTradeData().setRsrvStr1(reqData.getResCode());
        btd.getMainTradeData().setRsrvStr5(reqData.getTagCode());
        btd.getMainTradeData().setRsrvStr8(reqData.getUserNpTag());// 老系统没有这个，为了完工流程action方便取userNptag 新加
        btd.getMainTradeData().setPriority("260");
        createUserTradeData(btd);
    }

    /**
     * @Function: createUserTradeData
     * @Description: 对应 完工ModifyUserTagSetNpSnReturn
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-17 下午4:32:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-17 lijm3 v1.0.0 修改原因
     */
    public void createUserTradeData(BusiTradeData btd) throws Exception
    {
        MobileNoReturnReqData reqData = (MobileNoReturnReqData) btd.getRD();

        if ("4".equals(reqData.getUserNpTag()))
        {
            UserTradeData utd = reqData.getUca().getUser().clone();
            utd.setModifyTag(BofConst.MODIFY_TAG_UPD);
            utd.setUserTagSet(reqData.getNewUserTagSet());
            btd.add(reqData.getUca().getUser().getSerialNumber(), utd);

            // np表这里没有处理。。因为np表没有MODIFY_TAG 看是否挂action处理
        }
    }

}
