
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckUserSERIALVOCIEInfoRule extends BreBase implements IBREScript
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckUserSERIALVOCIEInfoRule.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckUserSERIALVOCIEInfoRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String serialNumber = databus.getString("SERIAL_NUMBER");
        String user_id = databus.getString("USER_ID_B");// 成员用户标识

        String err = "";

        /* 根据现网数据，确定下面验证不需要confirm by cc,modify by youys 20140805 */
        /*
         * if(!serialNumber.substring(0, 6).equals("157198")){ err = "用户手机号码不是157198号段，不能加入集团！";
         * BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 260101, err.toString()); return bResult; }
         */

        IDataset userVoicdeInfo = UserSvcInfoQry.queryUserSvcByUserId(user_id, "0", databus.getString("EPARCHY_CODE"));
        if (userVoicdeInfo.size() <= 0)
        {

            err = "用户未开通语音通话功能，不能加入集团！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 260101, err.toString());
            return bResult;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckUserSERIALVOCIEInfoRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
