
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateIpGroupUser extends CreateGroupUser
{
    private String sIpPwd;

    private String sIpMgrPhone;

    private String sIpMgrInfo;

    private String sIpMgrAddress;

    /**
     * @description 处理trade表预留字段数据
     */
    public void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData tradeData = bizData.getTrade();

        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);

        sIpPwd = paramData.getString("PASSWORD");
        sIpMgrPhone = paramData.getString("MGRPHONE", "");
        sIpMgrInfo = paramData.getString("MGRINFO", "");
        sIpMgrAddress = paramData.getString("MGRADDRESS", "");

        String strDefultPwd = TagInfoQry.getSysTagInfo("CS_INF_DEFALTIPPASSWD", "TAG_INFO", "0", reqData.getUca().getUserEparchyCode());
        if (StringUtils.isEmpty(strDefultPwd))
        {
            strDefultPwd = "000000";
        }
        if (StringUtils.isEmpty(sIpPwd))
        {
            sIpPwd = strDefultPwd;
        }

        tradeData.put("RSRV_STR3", reqData.getUca().getUser().getSerialNumber());
        tradeData.put("RSRV_STR4", sIpPwd);
        tradeData.put("RSRV_STR7", sIpMgrInfo);
        tradeData.put("RSRV_STR8", sIpMgrPhone);
        tradeData.put("RSRV_STR9", sIpMgrAddress);
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);

        sIpPwd = paramData.getString("PASSWORD");
        sIpMgrPhone = paramData.getString("MGRPHONE", "");
        sIpMgrInfo = paramData.getString("MGRINFO", "");
        sIpMgrAddress = paramData.getString("MGRADDRESS", "");

        String strDefultPwd = TagInfoQry.getSysTagInfo("CS_INF_DEFALTIPPASSWD", "TAG_INFO", "0", reqData.getUca().getUserEparchyCode());
        if (StringUtils.isEmpty(strDefultPwd))
        {
            strDefultPwd = "000000";
        }
        if (StringUtils.isEmpty(sIpPwd))
        {
            sIpPwd = strDefultPwd;
        }
        map.put("USER_PASSWD", sIpPwd);
        map.put("RSRV_STR3", reqData.getUca().getUser().getSerialNumber());
        map.put("RSRV_STR4", sIpPwd);
        map.put("RSRV_STR7", sIpMgrInfo);
        map.put("RSRV_STR8", sIpMgrPhone);
        map.put("RSRV_STR9", sIpMgrAddress);
    }

}
