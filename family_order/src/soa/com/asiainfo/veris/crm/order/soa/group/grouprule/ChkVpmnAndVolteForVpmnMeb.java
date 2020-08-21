
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChkVpmnAndVolteForVpmnMeb extends BreBase implements IBREScript
{

    /**
     * 201412291513规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkVpmnAndVolteForVpmnMeb.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkVpmnAndVolteForVpmnMeb()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        
        //当前用户标识、成员user_id
        String strUserIdB = databus.getString("USER_ID_B", "");
        
        if(StringUtils.isNotEmpty(strUserIdB)){
            IDataset userSvcInfos = UserSvcInfoQry.queryUserSvcByUserId(strUserIdB, "190", databus.getString("EPARCHY_CODE"));
            if(IDataUtil.isNotEmpty(userSvcInfos) && userSvcInfos.size() > 0){
                IDataset userResults = UserSvcInfoQry.queryUserSvcByUserId(strUserIdB, "5860", databus.getString("EPARCHY_CODE")); 
                if(IDataUtil.isNotEmpty(userResults) && userResults.size() > 0){
                    err = "用户已订购服务【190】VoLTE和【5860】监务通,不能订购VPMN成员产品!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkVpmnAndVolteForVpmnMeb() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
