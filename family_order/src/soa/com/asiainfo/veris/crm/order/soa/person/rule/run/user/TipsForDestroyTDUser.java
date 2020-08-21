/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TipsForDestroyTDUser.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-8-2 下午04:40:30 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-2 chengxf2 v1.0.0 修改原因
 */

public class TipsForDestroyTDUser extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-2 下午04:40:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 chengxf2 v1.0.0 修改原因
     */
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String productId = databus.getString("PRODUCT_ID");
        IData productInfo = UProductInfoQry.qryProductByPK(productId);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            if (!StringUtils.equals("00", productInfo.getString("PRODUCT_MODE")))
            {
                String msg = "该产品【" + productInfo.getString("PRODUCT_NAME") + "】用户不能在本界面办理立即销户业务";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, -1, msg);
            }
        }

        String userIdB = databus.getString("USER_ID");
        IDataset result = RelaUUInfoQry.getRelaUUInfoByRol(userIdB, "34");
        if (IDataUtil.isNotEmpty(result))
        {
            String msg = "业务受理前条件判断:该用户是双卡统一付费，继续操作将取消双卡统一付费业务！<br/>是否要继续业务的办理？选择【是】继续办理业务，选择【否】终止办理业务。";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, msg);
        }

        return true;
    }

}
