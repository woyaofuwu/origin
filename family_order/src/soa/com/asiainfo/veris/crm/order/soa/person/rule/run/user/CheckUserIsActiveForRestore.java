package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CheckUserIsActiveForRestore extends BreBase implements IBREScript{

	private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String userId = databus.getString("USER_ID");
        IDataset userInfos = UserInfoQry.queryAllUserByUserId(userId);
        
        if (IDataUtil.isNotEmpty(userInfos))
        {
        	IData userInfo=userInfos.getData(0);
            String acctTag = userInfo.getString("ACCT_TAG","");
            if ("2".equals(acctTag))
            {
                return true;
            }

        }

        return false;
    }
    
}
