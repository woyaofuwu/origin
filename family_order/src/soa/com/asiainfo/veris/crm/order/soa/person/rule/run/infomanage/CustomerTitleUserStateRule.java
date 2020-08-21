
package com.asiainfo.veris.crm.order.soa.person.rule.run.infomanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class CustomerTitleUserStateRule extends BreBase implements IBREScript
{

    /**
     * 0 开通 1 申请停机 2 挂失停机 3 并机停机 4 局方停机 5 欠费停机 6 申请销号 7 高额停机 8 欠费预销号 9 欠费销号 A 欠费半停机 B 高额半停机 E 转网销号停机 F 申请预销停机 G 申请半停机 I
     * 申请停机 J 初始停机 K 代理商停机 L 特殊停机 M 黑名单停机 N 人工开机 O 过期停机 P 电话开通停机 Q 无权停机 此规则是TradeCheckBefore规则
     **/
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);

        String userStateCodeset = null;
        String userId = null;
        if (uca != null)
        {
            SvcStateTradeData svcStateTrade = uca.getUserSvcsStateByServiceId("0");
            userStateCodeset = svcStateTrade.getStateCode();
        }
        else
        {
            userId = databus.getString("USER_ID");
            if (!StringUtils.isEmpty(userId))
            {
                IData user = UcaInfoQry.qryUserInfoByUserId(userId);
                IDataset userSvcStateList = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
                if (userSvcStateList != null && !userSvcStateList.isEmpty())
                {
                    IData userSvcState = userSvcStateList.getData(0);
                    userStateCodeset = userSvcState.getString("STATE_CODE");
                }
            }
            else
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_74.toString(), "用户标识USER_ID未传");

                return false;
            }

        }

        if (!"0".equals(userStateCodeset) && !"N".equals(userStateCodeset) && !"R".equals(userStateCodeset))
        {
            String stateName = SvcStateInfoQry.getUserStateName(userId, userStateCodeset);
            StringBuilder strb = new StringBuilder("业务受理前条件判断-用户处于【").append(stateName).append("】状态,不能办理该项业务！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, strb.toString(), strb.toString());
            return false;
        }

        return true;
    }

}
