
package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckIsRealNameForOffice.java
 * @Description: 局方停机和局方开机校验用户是否实名制
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-19 下午4:22:20
 */
public class CheckIsRealNameForOffice extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String serialNumber = databus.getString("SERIAL_NUMBER", "");
        String removeTag = databus.getString("REMOVE_TAG", "0");
        if (StringUtils.isNotEmpty(serialNumber) && "0".equals(removeTag))
        {
            String isRealName = databus.getString("IS_REAL_NAME", "0");
            String acctTag = databus.getString("ACCT_TAG");
            String openDateStr = databus.getString("OPEN_DATE", "");
            Date openDate = SysDateMgr.string2Date(openDateStr, SysDateMgr.PATTERN_STAND);
            Date paramDate = SysDateMgr.string2Date("2013-09-01" + SysDateMgr.START_DATE_FOREVER, SysDateMgr.PATTERN_STAND);
            if ((!"1".equals(isRealName) && !"0".equals(acctTag)) || ((!"1".equals(isRealName) && "0".equals(acctTag)) && paramDate.compareTo(openDate) <= 0))
            {
                return true;
            }
        }
        return false;
    }
}
