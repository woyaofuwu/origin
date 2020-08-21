
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.ModifyNetPwdInfoReqData;

/**
 * 互联网密码变更请求数据类
 * 
 * @author liutt
 */
public class BuildModifyNetPwdInfoReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ModifyNetPwdInfoReqData pwdReqData = (ModifyNetPwdInfoReqData) brd;
        pwdReqData.setNewPasswd(param.getString("NEW_PASSWD"));
        pwdReqData.setPasswdType(param.getString("PASSWD_TYPE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ModifyNetPwdInfoReqData();
    }

}
