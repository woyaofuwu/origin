
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckMemUserAcctTagRule extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMemUserAcctTagRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMemUserAcctTagRule()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userIdb = databus.getString("USER_ID_B");//集团成员用户ID
        if(StringUtils.isNotEmpty(userIdb)){
        	IData userInfo = UcaInfoQry.qryUserInfoByUserId(userIdb);
        	if(IDataUtil.isNotEmpty(userInfo)){
        		String acctTag = userInfo.getString("ACCT_TAG", "");
        		//预开未激活状态（TF_F_USER表的ACCT_TAG=’2’） 则提示未激活用户不能办理本业务；
        		if("2".equals(acctTag)){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                    return false;
        		}
        	}
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckMemUserAcctTagRule() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
