
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifypwd.order.buildreqdata;

import com.ailk.common.data.IData;
import com.ailk.common.util.DESUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifypwd.order.requestdata.CttBroadBandModifyPWDReqData;

public class BuildCttBroadBandModifyPWDReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        CttBroadBandModifyPWDReqData reqData = (CttBroadBandModifyPWDReqData) brd;
        reqData.setNewPasswd(DESUtil.encrypt("123456"));
        reqData.setNewPasswdAgain(DESUtil.encrypt("123456"));
        reqData.setOldPasswd(param.getString("OLD_PASSWD"));
        reqData.setPasswdType(param.getString("PASSWD_TYPE"));
        reqData.setUserPassword(param.getString("USER_PASSWORD"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadBandModifyPWDReqData();
    }
}
