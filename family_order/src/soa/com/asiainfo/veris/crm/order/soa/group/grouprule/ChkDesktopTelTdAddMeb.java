
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

public class ChkDesktopTelTdAddMeb extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkDesktopTelTdAddMeb.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkDesktopTelTdAddMeb() >>>>>>>>>>>>>>>>>>");
        }

        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        String mebBrandCode = databus.getString("BRAND_CODE_B", ""); // 个人的品牌
        String netTypeCode = "";
        
        String strUserIdB = databus.getString("USER_ID_B", "");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(strUserIdB);
        if(IDataUtil.isNotEmpty(userInfo))
        {
        	netTypeCode = userInfo.getString("NET_TYPE_CODE", "");
        }
        
        if (StringUtils.isNotBlank(mebBrandCode) && 
        		StringUtils.isNotBlank(netTypeCode) &&
        		!StringUtils.equals("TT02", mebBrandCode) &&
        		!StringUtils.equals("18", netTypeCode))
        {
        	String err = "该集团产品只允许品牌为TT02的用户或NET_TYPE_CODE=18的用户订购!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            return false;
        }
        
        if (logger.isDebugEnabled())
        {
        	logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkDesktopTelTdAddMeb() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        }

        return true;
    }

}
