
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * 和标识用户才能办理“和商务融合产品包话费礼包（2018）”活动
 * @author chenzg
 * @date 2018-04-18
 */
public class CheckIsPgUnionPayUser extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 7932806441333937401L;
    private static Logger logger = Logger.getLogger(CheckIsPgUnionPayUser.class);
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckIsPgUnionPayUser() >>>>>>>>>>>>>>>>>>");
        }
        String userId = databus.getString("USER_ID", "");
        IDataset otherDs = UserOtherInfoQry.queryUserOtherInfoForPg(userId, "PG_UNIONPAY");
        if(IDataUtil.isEmpty(otherDs)){
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20180418, "和商务标识的手机号码才可以办理“和商务融合产品包话费礼包（2018）”优惠!");
            return false;
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckIsPgUnionPayUser() >>>>>>>>>>>>>>>>>>");
        }
        
        return true;
    }
}
