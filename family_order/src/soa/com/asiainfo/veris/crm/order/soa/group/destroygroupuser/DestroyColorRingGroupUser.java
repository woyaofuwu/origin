
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyColorRingGroupUser extends DestroyGroupUser
{

    /**
     * 构造函数
     */
    public DestroyColorRingGroupUser()
    {

    }

    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    @Override
    protected void regTrade() throws Exception
    {

        super.regTrade();
        IData map = bizData.getTrade();
        // 移动彩铃平台集团彩铃开销户接口规范.doc
        map.put("RSRV_STR1", "boss"); // operatoraccount鉴权账号
        map.put("RSRV_STR2", "boss"); // operatorpwd鉴权密码
        map.put("RSRV_STR3", reqData.getUca().getCustGroup().getGroupId()); // corpnumber集团编号
        map.put("RSRV_STR4", "123456"); // password集团密码

        // Boss 1.5 RSRV_STR6必须
        // dsrGetPayUserInfo->InputParams->ParamByName("SERIAL_NUMBER")->Value =
        // pv_str_PayNumber;
        map.put("RSRV_STR6", reqData.getUca().getSerialNumber()); // 集团编码
        map.put("RSRV_STR10", reqData.getUca().getSerialNumber()); // 集团编码
    }

}
