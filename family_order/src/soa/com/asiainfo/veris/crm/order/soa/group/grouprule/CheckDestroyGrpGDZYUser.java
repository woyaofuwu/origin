
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class CheckDestroyGrpGDZYUser extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    /**
     * 201911011512规则
     *
     */
    private static Logger logger = Logger.getLogger(CheckDestroyGrpGDZYUser.class);
    
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckDestroyGrpGDZYUser()  >>>>>>>>>>>>>>>>>>");
        }
        
        String err = "";
        String userId = databus.getString("USER_ID", "");//集团产品的user_id
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        if(StringUtils.isNotEmpty(userId))
        {
        	IData inParam = new DataMap();
            inParam.put("USER_ID", userId);
            inParam.put("RSRV_VALUE_CODE", "GDZY");
            IDataset otherInfos = TradeOtherInfoQry.queryUserOtherInfoByUserId(inParam);
            if(IDataUtil.isNotEmpty(otherInfos))
            {
                err = "该集团用户还有未失效的费项,不允许注销!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,errCode, err.toString());
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckDestroyGrpGDZYUser() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
