
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.LockUserPwdReqData;

/**
 * 用户密码解锁请求数据处理
 * 
 * @author liutt
 */
public class BuildLockUserPwdReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        LockUserPwdReqData pwdReqData = (LockUserPwdReqData) brd;
        pwdReqData.setOldTradeTypeCode(param.getString("OLD_TRADE_TYPE_CODE", ""));
        pwdReqData.setNetTypeCode(param.getString("NET_TYPE_CODE", brd.getUca().getUser().getNetTypeCode()));
        pwdReqData.setCheckMode(param.getString("CHECK_MODE", "Z"));// 不传，默认为Z
        pwdReqData.setInModeCode(param.getString("LOCK_IN_MODE_CODE"));
        pwdReqData.setErrorNum(param.getString("ERROR_NUM"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new LockUserPwdReqData();
    }
}
