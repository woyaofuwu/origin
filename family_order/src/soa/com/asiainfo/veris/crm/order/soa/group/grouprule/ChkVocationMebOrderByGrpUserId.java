
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ChkVocationMebOrderByGrpUserId extends BreBase implements IBREScript
{

    /**
     * 201412291518规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkVocationMebOrderByGrpUserId.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled()){
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkVocationMebOrderByGrpUserId()  >>>>>>>>>>>>>>>>>>");
        }
        
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userIdb = databus.getString("USER_ID_B", "");// 成员用户标识
        
        if(StringUtils.isNotEmpty(userIdb)){
            
            IDataset otherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userIdb,"HYYYKBATCHOPEN");
            
            if(IDataUtil.isEmpty(otherInfos)){
                err = "该用户不是行业应用卡，不允许订购该集团产品。";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                return false;
            }
            
        }

        if (logger.isDebugEnabled()){
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkVocationMebOrderByGrpUserId() <<<<<<<<<<<<<<<<<<<");
        }
            
        return true;
    }

}
