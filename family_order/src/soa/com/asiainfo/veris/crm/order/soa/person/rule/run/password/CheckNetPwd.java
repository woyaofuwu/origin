
package com.asiainfo.veris.crm.order.soa.person.rule.run.password;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 互联网密码验证规则类
 * 
 * @author liutt
 */
public class CheckNetPwd extends BreBase implements IBREScript
{
    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验
        {
            IData pgData = databus.getData("REQDATA");// 请求的数据
            if (IDataUtil.isNotEmpty(pgData))
            {
                UcaData ucaData = (UcaData) databus.get("UCADATA");
                String oldPwd = ucaData.getUser().getRsrvStr5();
                String newPwd = pgData.getString("NEW_PASSWD");
                String pwdType = pgData.getString("PASSWD_TYPE");
                if (StringUtils.equals("1", pwdType) || StringUtils.equals("2", pwdType))
                {// 1密码修改2密码新增
                    if (StringUtils.equals("1", pwdType) && StringUtils.isBlank(oldPwd))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 460104, "用户互联网密码不存在，不能做修改操作！");
                        return true;
                    }
                    if (StringUtils.equals("2", pwdType) && StringUtils.isNotBlank(oldPwd))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 460106, "用户互联网密码已存在，不能做新增操作！");
                        return true;
                    }
                    if (newPwd.length() < 6 || newPwd.length() > 16)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 460101, "互联网密码位数至少为6，最长16位！");
                        return true;
                    }
                    // 检查密码是否包括数字
                    Pattern p = Pattern.compile("(\\d)+");
                    Matcher m = p.matcher(newPwd);
                    if (!m.find())
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 460102, "互联网密码必须包括数字！");
                        return true;
                    }
                    // 检查密码是否包括字母
                    p = Pattern.compile("([a-z|A-Z])+");
                    m = p.matcher(newPwd);
                    if (!m.find())
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 460103, "互联网密码必须包括字母！");
                        return true;
                    }
                }
                else if (StringUtils.equals("5", pwdType))
                {// 密码重置
                    if (StringUtils.isBlank(oldPwd))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 460105, "用户互联网密码不存在，不能做重置操作！");
                        return true;
                    }
                }
                else
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 460107, "无效的互联网密码管理类型！");
                    return true;
                }

            }

        }
        return false;
    }
}
