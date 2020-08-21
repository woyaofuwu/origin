/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUserNetTypeCode.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-6-24 下午03:47:18 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-6-24 chengxf2 v1.0.0 修改原因
 */

public class CheckUserNetTypeCode extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
            String netTypeCode = databus.getString("NET_TYPE_CODE", "00");

            String netTypeParam = param.get("NET_TYPE_CODE");

            if (!StringUtils.equals(netTypeParam, netTypeCode))
            {
                return true;
            }

        }
        return false;
    }

}
