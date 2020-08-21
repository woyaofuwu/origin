
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;

/**
 * 69900337 产品才配置
 * 
 * @author Mr.Z
 */
public class CheckWlanServiceLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 3655889411228548032L;

    private static Logger logger = Logger.getLogger(CheckWlanServiceLimit.class);

    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckWlanServiceLimit() >>>>>>>>>>>>>>>>>>");
        }

        IDataset userPlatSvc = databus.getDataset("TF_F_USER_PLATSVC");
        if (IDataUtil.isEmpty(userPlatSvc))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062407, "用户还没有开通wlan服务，开通wlan后才能享受优惠！");
            return false;
        }

        for (int index = 0, size = userPlatSvc.size(); index < size; index++)
        {
            IData userPlatData = userPlatSvc.getData(index);
            String serviceId = userPlatData.getString("SERVICE_ID");
            IData svcData = PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
            String bizTypeCode = svcData.getString("BIZ_TYPE_CODE");
            String bizStateCode = userPlatData.getString("BIZ_STATE_CODE");

            if (!(("A".equals(bizStateCode) || "N".equals(bizStateCode)) && ("02".equals(bizTypeCode) || "09".equals(bizTypeCode))))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062407, "用户还没有开通wlan服务，开通wlan后才能享受优惠！");
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckWlanServiceLimit() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
