
package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckSvcStateRule.java
 * @Description: 校验用户的某个服务状态是否满足
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-19 下午4:22:20
 */
public class CheckSvcStateRule extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String paramStateCode = ruleParam.getString("SVC_STATE_CODE", "");// 取配置的服务状态，如果没有默认是检查0
        String userId = databus.getString("USER_ID");

        // 查询用户有效主体服务状态
        IDataset svcStateDataset = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
        if (IDataUtil.isEmpty(svcStateDataset) || svcStateDataset.size() > 2)
        {
            StringBuilder msg = new StringBuilder(100);
            msg.append("用户服务状态资料错误！用户主服务没有有效的服务状态，请检查数据！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", msg.toString());
            return true;
        }
        String svcStateGroups = "";
        String mainServiceId = "0";
        mainServiceId = svcStateDataset.getData(0).getString("SERVICE_ID", "");
        if (svcStateDataset.size() == 1)
        {
            svcStateGroups = svcStateDataset.getData(0).getString("STATE_CODE", "");
        }
        else
        {
            for (int i = 0, count = svcStateDataset.size(); i < count; i++)
            {
                String tempStateCode = svcStateDataset.getData(i).getString("STATE_CODE");
                if (StringUtils.equals("Y", tempStateCode))
                {
                    svcStateGroups = svcStateGroups + tempStateCode;
                }
                else
                {
                    svcStateGroups = tempStateCode + svcStateGroups;
                }
            }
        }
        // 查询出当前服务对应的状态名称
        String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode(mainServiceId, StringUtils.substring(svcStateGroups, 0, 1));
        databus.put("SVC_STATE_NAME", stateName);// 放置在databus中，方便错误信息提示中使用
        String[] paramStateCodeArray = StringUtils.split(paramStateCode, "|");
        for (int i = 0, count = paramStateCodeArray.length; i < count; i++)
        {
            if (StringUtils.equals(svcStateGroups, paramStateCodeArray[i]))// 配置的和查询出来的相同，则返回true
            {
                StringBuilder msg = new StringBuilder(100);
                if (StringUtils.length(svcStateGroups) == 2) // 携转
                {
                    msg.append("用户目前已经欠携出方费用，并").append(stateName).append("，不能办理该业务!");
                }
                else
                {
                    msg.append("用户当前状态为").append(stateName).append("，不能办理该业务!");
                }
                databus.put("TIPS_MSG", msg.toString());// 放置在databus中，方便错误信息提示中使用
                return true;
            }
        }

        return false;
    }
}
