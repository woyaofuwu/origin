
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * 仅仅用于前台选包的时候的校验
 * 
 * @author Mr.Z
 */
public class CheckSpecUser4SelectPkg extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -7346214181893879073L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");
        IDataset userOtherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "PUSP");
        if (IDataUtil.isEmpty(userOtherInfos))
            return false;

//        IDataset pkgextDataset = PkgExtInfoQry.queryPackageExtInfo(databus.getString("PACKAGE_ID_A"), databus.getString("EPARCHY_CODE"));
        String tagSet1 = SaleActiveUtil.getPackageExtTagSet1(databus.getString("PACKAGE_ID_A"), null);//pkgextDataset.getData(0).getString("TAG_SET1", "");
        if (tagSet1.length() < 13)
            return false;

        char idx9 = tagSet1.charAt(8), idx11 = tagSet1.charAt(10);
        if (idx9 != '1' || idx11 != '1')
            return false;

        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 14061303, "该用户为特殊不交预存用户，不需要缴纳预存！");

        return true;
    }

}
