
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUserBrandForRestore.java
 * @Description: 复机校验用户品牌
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-1 下午4:59:37
 */
public class CheckUserBrandForRestore extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String brandCode = databus.getString("BRAND_CODE");

        if (!StringUtils.startsWith(brandCode, "G"))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "540006", "不受理非个人品牌用户复机！");
            return true;
        }
        if (StringUtils.startsWith(brandCode, "GS0"))
        {
            String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);
            StringBuilder msg = new StringBuilder(50);
            msg.append("该用户为原智能网【").append(brandName).append("】用户,不能办理复机！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", msg.toString());
            return true;
        }

        return false;
    }

}
