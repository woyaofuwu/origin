
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserpwd.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserpwd.order.requestdata.CttModifyUserPwdInfoReqData;

/**
 * 用户密码变更请求数据类
 */
public class BuildCttModifyUserPwdInfoReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttModifyUserPwdInfoReqData pwdReqData = (CttModifyUserPwdInfoReqData) brd;
        pwdReqData.setNewPasswd(param.getString("NEW_PASSWD"));
        pwdReqData.setNewPasswdAgain(param.getString("NEW_PASSWD_AGAIN"));
        pwdReqData.setOldPasswd(param.getString("OLD_PASSWD"));
        pwdReqData.setPasswdType(param.getString("PASSWD_TYPE"));
        pwdReqData.setUserPassword(param.getString("USER_PASSWORD"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttModifyUserPwdInfoReqData();
    }

}
