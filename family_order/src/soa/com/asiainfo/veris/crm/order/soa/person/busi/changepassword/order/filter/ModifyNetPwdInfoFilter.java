
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.filter;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.PasswordUtil;

/**
 * 互联网密码变更参数转换与校验
 * 
 * @author liutt
 */
public class ModifyNetPwdInfoFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        if (input.containsKey("USER_PASSWD"))
        {// USER_PASSWD接口过来的key
            String userPwd = input.getString("USER_PASSWD");
            if (StringUtils.equals("1", input.getString("X_CNVTAG")) && StringUtils.isNotBlank(userPwd))
            {
                userPwd = PasswordUtil.decrypt(userPwd);// 解密
            }
            input.put("NEW_PASSWD", userPwd);
            input.remove("USER_PASSWD");
        }
        if (input.containsKey("X_MANAGEMODE"))
        {// X_MANAGEMODE接口过来的key
            input.put("PASSWD_TYPE", input.getString("X_MANAGEMODE"));
            input.remove("X_MANAGEMODE");
        }
    }
}
