
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckUserFaxInfoRule extends BreBase implements IBREScript
{

    /**
     * 商务宝SIM 成员变更校验
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckUserFaxInfoRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckUserFaxInfoRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = true;
        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String user_id = databus.getString("USER_ID_B");// 成员用户标识

        String err = "";
        IData map = new DataMap();
        map.put("USER_ID", user_id);
        map.put("SERVICE_ID", "46");
        ;
        IDataset userFax = UserSvcInfoQry.queryUserSvcByUserId(user_id, "46", databus.getString("EPARCHY_CODE"));
        if (IDataUtil.isNotEmpty(userFax))
        {

            return bResult;

        }
        else
        {
            err = "用户未开通传真服务功能，不能加入集团！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20140519200802", err.toString());
            bResult = false;
        }
        // --80101
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckUserFaxInfoRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
