
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class PlatUserStateRule extends BreBase implements IBREScript
{

    /**
     * 0 开通 1 申请停机 2 挂失停机 3 并机停机 4 局方停机 5 欠费停机 6 申请销号 7 高额停机 8 欠费预销号 9 欠费销号 A 欠费半停机 B 高额半停机 E 转网销号停机 F 申请预销停机 G 申请半停机 I
     * 申请停机 J 初始停机 K 代理商停机 L 特殊停机 M 黑名单停机 N 人工开机 O 过期停机 P 电话开通停机 Q 无权停机 此规则是TradeCheckBefore规则
     **/
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
    	//前台受理的不校验
    	String inModeCode = CSBizBean.getVisit().getInModeCode();
    	if("0".equals(inModeCode))
    	{
    		return true;
    	}
    	
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        String removeTag = "0";
        String userStateCodeset = "0";
        String acctTag = "0"; // 出账标识
        if (uca != null)
        {
            SvcStateTradeData svcStateTrade = uca.getUserSvcsStateByServiceId("0");
            if (svcStateTrade != null)
            {
                userStateCodeset = svcStateTrade.getStateCode();
            }

            removeTag = uca.getUser().getRemoveTag();
            acctTag = uca.getUser().getAcctTag();
        }
        else
        {
            String userId = databus.getString("USER_ID");
            if (!StringUtils.isEmpty(userId))
            {
                IData user = UcaInfoQry.qryUserInfoByUserId(userId);
                if (user != null)
                {
                    removeTag = user.getString("REMOVE_TAG", "0");
                    acctTag = user.getString("ACCT_TAG", "0");
                }
                IDataset userSvcStateList = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
                if (IDataUtil.isNotEmpty(userSvcStateList))
                {
                    IData userSvcState = userSvcStateList.getData(0);
                    userStateCodeset = userSvcState.getString("STATE_CODE", "0");
                }
            }
            else
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "用户标识USER_ID未传");

                return false;
            }

        }

         if("2".equals(acctTag))
         {
	         BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0922_1.toString(),
	         PlatException.CRM_PLAT_0922_1.getValue());
	         return false;
         }

        if ("0".equals(removeTag))
        {
            // 0表示用户是正常的,但是需要检查其状态
        }
        else if ("1".equals(removeTag) || "3".equals(removeTag))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0923.toString(), PlatException.CRM_PLAT_0923.getValue());
            return false;
        }
        else if ("2".equals(removeTag) || "4".equals(removeTag))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0924.toString(), PlatException.CRM_PLAT_0924.getValue());
            return false;
        }
        else
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0925.toString(), PlatException.CRM_PLAT_0925.getValue());
            return false;
        }

        // 检查用户状态
        if ("0".equals(userStateCodeset) || "N".equals(userStateCodeset))
        {
            // 状态正常,无需处理
        }
        else if ("-A-B-G-".indexOf(userStateCodeset) >= 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0921.toString(), PlatException.CRM_PLAT_0921.getValue());
            return false;
        }
        else if ("-1-2-3-4-5-7-E-F-I-J-K-L-M-O-P-Q-".indexOf(userStateCodeset) >= 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0922.toString(), PlatException.CRM_PLAT_0922.getValue());
            return false;
        }
        else if ("-6-9-".indexOf(userStateCodeset) >= 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0924.toString(), PlatException.CRM_PLAT_0924.getValue());
            return false;
        }
        else if ("8".indexOf(userStateCodeset) >= 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0923.toString(), PlatException.CRM_PLAT_0923.getValue());
            return false;
        }
        else
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0922.toString(), PlatException.CRM_PLAT_0922.getValue());
            return false;
        }

        return true;
    }

}
