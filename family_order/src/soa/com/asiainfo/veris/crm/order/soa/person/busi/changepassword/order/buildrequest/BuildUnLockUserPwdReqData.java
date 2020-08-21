
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.UnLockUserPwdReqData;

/**
 * 用户密码解锁请求数据处理
 * 
 * @author liutt
 */
public class BuildUnLockUserPwdReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        UnLockUserPwdReqData pwdReqData = (UnLockUserPwdReqData) brd;
        pwdReqData.setUnLockType(param.getString("UNLOCK_TYPE"));
        pwdReqData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
        pwdReqData.setPsptId(param.getString("PSPT_ID"));
        pwdReqData.setMessage(param.getString("MESSAGE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new UnLockUserPwdReqData();
    }
}
