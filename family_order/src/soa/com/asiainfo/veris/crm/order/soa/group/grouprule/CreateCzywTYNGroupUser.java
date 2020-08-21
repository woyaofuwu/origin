
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class CreateCzywTYNGroupUser extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CreateCzywTYNGroupUser.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CreateCzywTYNGroupUser() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userId = databus.getString("USER_ID");
        IDataset userInfos = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            for (int i = 0; i < userInfos.size(); i++)
            {
                IData userInfo = (IData) userInfos.get(i);
                String stateCode = userInfo.getString("STATE_CODE");
                if ("5".equals(stateCode))
                {
                    err = "该集团用户主体服务为暂停状态，不能加入成员，业务不能继续！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                    return bResult;
                }
            }
        }

        return bResult;

    }
}
