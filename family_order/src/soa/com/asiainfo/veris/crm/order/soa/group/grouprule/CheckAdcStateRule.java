package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class CheckAdcStateRule extends BreBase implements IBREScript{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckAdcStateRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckAdcStateRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = true;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userId = databus.getString("USER_ID");
        String productId = databus.getString("PRODUCT_ID");
        //String attrCode = GrpCommonBean.merchToProduct("110224",1,null);
        if(productId.equals("996924")){ 
	        IDataset userInfos = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, Route.CONN_CRM_CG);
	        if (IDataUtil.isNotEmpty(userInfos))
	        {
	            for (int i = 0; i < userInfos.size(); i++)
	            {
	                IData userInfo = (IData) userInfos.get(i);
	                String stateCode = userInfo.getString("STATE_CODE");
	                if ("3".equals(stateCode))
	                {
	                	
	                    err = "该集团用户行业手机报业务服务为暂停状态，不允许成员管理，业务不能继续！";
	                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
	                    return false;
	                }
	            }
	        }
        }
        return bResult;

    }
}
