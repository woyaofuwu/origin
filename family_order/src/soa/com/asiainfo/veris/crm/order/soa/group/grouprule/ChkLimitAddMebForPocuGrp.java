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

public class ChkLimitAddMebForPocuGrp extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkLimitAddMebForPocuGrp.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkLimitAddMebForPocuGrp() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        
        if(StringUtils.isNotBlank(userIdB))
        {
        	IData userInfo = UcaInfoQry.qryUserInfoByUserId(userIdB);
        	if(IDataUtil.isEmpty(userInfo))
        	{   
        		err = "规则校验时，根据成员用户标识查询成员用户信息不存在!USER_ID=" + userIdB;
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    			return false;
        	}
        	else
        	{
        		 String netTypeCode = userInfo.getString("NET_TYPE_CODE", "");
                 // 判断当前号码是否是IMS用户
                 if ("07".equals(netTypeCode))
                 {
                	 err = "物联网号码不能新增该产品!";
                     BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                     return false;
                 }
        	}
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkLimitAddMebForPocuGrp()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

}
