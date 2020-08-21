
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeIpUserElement extends ChangeUserElement
{
    private String sGrpPwd;

    @Override
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        String strDefultPwd = TagInfoQry.getSysTagInfo("CS_INF_DEFALTIPPASSWD", "TAG_INFO", "0", reqData.getUca().getUserEparchyCode());
        if (strDefultPwd == null)
        {
            strDefultPwd = "000000";
        }
        if (strDefultPwd.equals(""))
        {
            strDefultPwd = "000000";
        }

        sGrpPwd = paramData.getString("PASSWORD");
        if (sGrpPwd == null)
        {
            sGrpPwd = strDefultPwd;
        }
        if (sGrpPwd.equals(""))
        {
            sGrpPwd = strDefultPwd;
        }
    }

    public IData getTradeUserExtendData() throws Exception
    {
        IData userData = super.getTradeUserExtendData();
        userData.put("RSRV_STR4", sGrpPwd);
        return userData;
    }
}
