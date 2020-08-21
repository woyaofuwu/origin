
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * 2.3.7.2 param_attr = 70 >> 购机业务受限处理(无para_code1服务，不能办理param_code活动) 服务从td_bre_parameter传入 SERVICE_ID
 * 
 * @author Mr.Z
 */
public class CheckServiceLimit4Widenet extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -4656223390584003349L;

    private static Logger logger = Logger.getLogger(CheckServiceLimit4Widenet.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckServiceLimit4Widenet() >>>>>>>>>>>>>>>>>>");
        }

        String serviceId = ruleParam.getString(databus, "SERVICE_ID") == null ? "" : ruleParam.getString(databus, "SERVICE_ID");
        String serialNumber = databus.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 40026, "用户不是宽带用户，不能办理此活动！");
            return false;
        }

        boolean isexists = false;
        IDataset userServices = UserSvcInfoQry.qryUserSvcByUserId(userInfo.getString("USER_ID"));

        if (IDataUtil.isNotEmpty(userServices))
        {
            for (int index = 0, size = userServices.size(); index < size; index++)
            {
                if (serviceId.indexOf(userServices.getData(index).getString("SERVICE_ID", "")) > 0)
                {
                    isexists = true;
                    break;
                }
            }
        }

        if (!isexists)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20001, "用户不是8M宽带用户，不能办理此活动!");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckServiceLimit4Widenet() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
