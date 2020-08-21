
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

public class CheckLimitPrepayAcct extends BreBase implements IBREScript
{

	/**
     * 201907100001 规则
     */
	private static final long serialVersionUID = 549884901873075286L;

    private static Logger logger = Logger.getLogger(CheckLimitPrepayAcct.class);
    
	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckLimitPrepayAcct()  >>>>>>>>>>>>>>>>>>");
        }
		
		String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String grpAcctId = databus.getString("GRP_ACCT_ID");// 集团账户ID
        if(StringUtils.isBlank(grpAcctId))
        {
        	//批量业务进来的是放在GROUP_ACCT_ID里
        	// 集团账户ID
        	grpAcctId = databus.getString("GROUP_ACCT_ID");
        	if(StringUtils.isBlank(grpAcctId))
        	{
        		err = "未获取到集团账户ID!";
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        		return false;
        	}
        }
        
        IDataset acctInfos = AcctInfoQry.getPrepayAcctInfoByAcctId(grpAcctId);
        if(IDataUtil.isNotEmpty(acctInfos))
        {
        	err = "该账户是集团客户预缴款(虚拟)产品的专有账户!不可为别的用户做代付!";
    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    		return false;
        }
 
        if (logger.isDebugEnabled()){
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckLimitPrepayAcct() <<<<<<<<<<<<<<<<<<<");
        }
        return true;
    }

}
