
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TipsUserKDOrAreaInfoForDestroy.java
 * @Description: 立即销户时如果用户有宽带提示用户宽带将被同步销户，否则，提升用户业务区信息
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-1 下午4:59:37
 */
public class TipsUserKDOrAreaInfoForDestroy extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String cityCode = databus.getString("CITY_CODE");
        String serialNumber = databus.getString("SERIAL_NUMBER");

        // 判断是否包含宽带用户
        IData kdUserInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(kdUserInfo))
        {
            String msg = "该客户手机号码下已有宽带业务，如手机号码销户，宽带将同步销户，请确认是否要进行手机销户？";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 525016, msg);
        }
        else
        {
            String cityName = UAreaInfoQry.getAreaNameByAreaCode(cityCode);
            StringBuilder msg = new StringBuilder(100);
            msg.append(serialNumber).append("是").append(cityName).append("(").append(cityCode).append(")").append("业务区号码，是否确认要销户，是否继续？");
            // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS,525017,msg.toString());
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, msg.toString());
        }

        return true;
    }

}
