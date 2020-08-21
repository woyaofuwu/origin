
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TipsSpecialOpenNeedInfo.java
 * @Description: 特殊开机提示信息
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-1 下午4:59:37
 */
public class TipsSpecialOpenNeedInfo extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String custName = databus.getString("CUST_NAME");
        // 获取特殊开机信息 QCC_GetSpecialOpenNeedInfo
        IDataset specialOpenInfoDataset = null;
        if (IDataUtil.isNotEmpty(specialOpenInfoDataset))
        {
            IData tempData = specialOpenInfoDataset.getData(0);
            Double rsrvNum1 = Double.parseDouble(tempData.getString("RSRV_NUM1", ""));
            Double rsrvNum2 = Double.parseDouble(tempData.getString("RSRV_NUM2", ""));

            rsrvNum1 = rsrvNum1 / 100.0;
            rsrvNum2 = rsrvNum2 / 100.0;
            StringBuilder msg = new StringBuilder(300);
            msg.append(custName).append("客户当前欠费").append(rsrvNum1).append("元，上月欠费");
            msg.append(rsrvNum2).append("元，本月已特殊开机");
            msg.append(tempData.getString("RSRV_NUM3", "")).append("次。是否继续办理业务？");

            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, "", msg.toString());
            return true;
        }

        return false;
    }
}
