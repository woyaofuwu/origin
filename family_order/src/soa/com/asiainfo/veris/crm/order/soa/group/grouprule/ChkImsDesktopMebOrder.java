
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

public class ChkImsDesktopMebOrder extends BreBase implements IBREScript
{

    /**
     * 2017051101规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkImsDesktopMebOrder.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkImsDesktopMebOrder()  >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String userIdA = databus.getString("USER_ID", "");//集团产品的user_id
        
        IData userInfos = UcaInfoQry.qryUserInfoByUserId(userIdA);
        
        if(IDataUtil.isNotEmpty(userInfos))
        {
        	String stateCode = userInfos.getString("USER_STATE_CODESET","");
        	if(!StringUtils.equals("0", stateCode))
        	{
        		err = "该集团用户的状态是欠费停机状态或非开通状态,不允许添加成员!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
        	}
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkImsDesktopMebOrder() <<<<<<<<<<<<<<<<<<<");
        }

        return true;
    }

}
