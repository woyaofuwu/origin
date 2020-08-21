
package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUserKDDiscntAndDeposit.java
 * @Description: 校验用户专项存折是否尚有余额，如果存在余额，请退订宽带套餐后，再进行办理！
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-1 上午10:47:22
 */
public class CheckUserKDDiscntAndDeposit extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String eparchyCode = databus.getString("EPARCHY_CODE");

        IDataset limiteTrade = CommparaInfoQry.getCommpara("CSM", "7635", tradeTypeCode, eparchyCode);
        IDataset SYKD_Discnt = CommparaInfoQry.getCommpara("CSM", "7669", tradeTypeCode, eparchyCode);

        if (IDataUtil.isNotEmpty(SYKD_Discnt))
        {
            IDataset userDiscntInfo = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
            IDataset userDepositInfo = null;// "QAM_ACCTDEPOSIT_ACCTID"
            if (IDataUtil.isNotEmpty(userDiscntInfo) && IDataUtil.isNotEmpty(userDepositInfo))
            {
                String curMonth = SysDateMgr.getCurMonth();
                for (int i = 0, iCount = userDiscntInfo.size(); i < iCount; i++)
                {
                    IData tmpUserDiscntData = userDiscntInfo.getData(i);
                    String discntCode = tmpUserDiscntData.getString("DISCNT_CODE", "0");
                    String endData = tmpUserDiscntData.getString("END_DATE", "");
                    String endMonth = SysDateMgr.getMonthForDate(endData);
                    for (int j = 0, jCount = SYKD_Discnt.size(); j < jCount; j++)
                    {
                        if (StringUtils.equals(discntCode, SYKD_Discnt.getData(j).getString("PARA_CODE1")))
                        {
                            if (!StringUtils.equals(curMonth, endMonth))
                            {
                                for (int k = 0, kCount = limiteTrade.size(); k < kCount; k++)
                                {
                                    for (int m = 0, mCount = userDepositInfo.size(); m < mCount; m++)
                                    {
                                        if (StringUtils.equals(limiteTrade.getData(k).getString("PARA_CODE1"), userDepositInfo.getData(m).getString("DEPOSIT_CODE")))
                                        {
                                            if (userDepositInfo.getData(m).getInt("ODD_MONEY", 0) + userDepositInfo.getData(m).getInt("EVEN_MONEY", 0) != 0)
                                            {
                                                String errorString = "用户专项存折尚有余额，请退订宽带套餐后，再进行办理！";
                                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525015, errorString);
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        return false;
    }
}
