
package com.asiainfo.veris.crm.order.soa.person.rule.run.acct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

/**
 * 校验账期是否开账
 * 
 * @author liutt
 */
public class CheckAcctCycleUse extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))// 查询号码时校验
        {
//            String userId = databus.getString("USER_ID");
//            String lastCycleLateDate = AcctDayDateUtil.getCycleIdLastDayLastAcct(userId);// 上账期最后一天
//            IDataset cycleSet = ParamInfoQry.getCycleByDate(lastCycleLateDate);
//            if (IDataUtil.isNotEmpty(cycleSet))
//            {
//                String useTag = cycleSet.getData(0).getString("USE_TAG").trim();
//                if (StringUtils.equals("0", useTag))
//                {
//                    return true;// 本月还未开帐，不能办理过户业务
//                }
//            }
        	String acctId = databus.getString("ACCT_ID");
        	IData drecvData =  AcctCall.ifDrecvPeriod(acctId);//换成调用账务接口来判断 
        	if(StringUtils.equals("1", drecvData.getString("IF_DRECV_PERIOD"))){
        		return true;// 本月还未开帐，不能办理过户业务
        	}
        }
        return false;
    }
}
