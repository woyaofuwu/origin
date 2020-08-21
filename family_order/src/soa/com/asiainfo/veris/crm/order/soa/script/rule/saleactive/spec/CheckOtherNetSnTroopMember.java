
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
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 2.3.13 param_attr = 83 >> 产品param_code需要校验异网转接号码是否在目标客户群para_code1 入参 TROOP_ID
 * 
 * @author Mr.Z
 */
public class CheckOtherNetSnTroopMember extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 2298380758463178128L;

    private static Logger logger = Logger.getLogger(CheckOtherNetSnTroopMember.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckOtherNetSnTroopMember() >>>>>>>>>>>>>>>>>>");
        }

        String serialNumber = databus.getString("SERIAL_NUMBER");
        IDataset userTransPhoneInfos = BreQry.getUserTransPhoneInfos(serialNumber);

        if (userTransPhoneInfos.getData(0).getInt("CNT") == 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20110, "该用户未存在异网转接号码，不能办理此业务！");
            return false;
        }

        IData userTransPhoneInfo = userTransPhoneInfos.getData(0);

        String troopId = ruleParam.getString(databus, "TROOP_ID");
        IDataset troopMemberSet = BreQry.getTroopMemberByTroopIdSn(troopId, userTransPhoneInfo.getString("PHONE_CODE_B"));

        if (IDataUtil.isEmpty(troopMemberSet))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20111, "该用户的异网转接号码" + userTransPhoneInfo.getString("PHONE_CODE_B") + "不是挖掘竞争对手的号码，不能办理此业务！");
            return false;
        }

        IDataset otherNetSnInfoset = BreQry.getOtherNetSnTroopMember(userTransPhoneInfo.getString("PHONE_CODE_B", ""));

        String count = otherNetSnInfoset.getData(0).getString("CNT");

        if (Integer.parseInt(count) > 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20111, "该用户的异网转接号码[" + userTransPhoneInfo.getString("PHONE_CODE_B") + "]已被挖掘并赠送过相关优惠，不能办理此业务！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckOtherNetSnTroopMember() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
