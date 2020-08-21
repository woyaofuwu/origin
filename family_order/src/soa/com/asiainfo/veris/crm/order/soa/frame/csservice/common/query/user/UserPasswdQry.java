
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 用户密码相关查询
 * 
 * @author liutt
 */
public class UserPasswdQry
{

    /**
     * 读取用户当天连续输入的密码错误次数
     * 
     * @param userId
     * @param pwdTypeCode
     *            1-客服密码 2-互联网密码 （海南系统不分，可以不传）
     * @param inModeCode
     *            渠道（海南系统把该条件注释了，可以不传）
     * @return
     * @throws Exception
     */
    public static IDataset queryPasswdErrorCount(String userId, String pwdTypeCode, String inModeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        if (StringUtils.isNotBlank(pwdTypeCode))
        {
            param.put("PASSWD_TYPE_CODE", pwdTypeCode); // 1-客服密码 2-互联网密码
        }
        if (StringUtils.isNotBlank(inModeCode))
        {
            param.put("IN_MODE_CODE", inModeCode); // 渠道
        }
        return Dao.qryByCodeParser("TF_F_USER_PASSWD_ERROR", "SEL_PASSWD_ERROR_COUNT", param);

    }
}
