
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRentInfoQry;

public class CheckIsRentBalance extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID", "");
        IDataset result = UserRentInfoQry.queryUserRentInfo(userId);
        if (IDataUtil.isNotEmpty(result))
        {
            String rsrvdate = result.getData(0).getString("RSRV_DATE", ""); // 结算时间
            String startdate = result.getData(0).getString("START_DATE"); // 开始时间
            boolean isBefore = SysDateMgr.string2Date(rsrvdate, SysDateMgr.PATTERN_STAND).before(SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND));
            // 结算时间在当前时间之前
            if (isBefore && !rsrvdate.equals(startdate) && StringUtils.substring(rsrvdate, 0, 10).equals(SysDateMgr.getSysDate()))
            {
                databus.put("TIP_INFO", "该用户当天已经结算过租金，请明天或以后再来办理结算租金业务！");
                return true;
            }
        }
        return false;
    }

}
