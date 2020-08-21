
package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckUserSvcAndNPStateChgRule.java
 * @Description: 校验用户的服务状态和携转状态是否符合td_s_trade_svcstate表服务状态变更配置
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-19 下午3:18:18
 */
public class CheckUserSvcAndNPStateChgRule extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 检查用户当前服务状态数据是否存在配置的服务状态变化数据：主要是想将报错信息精确的提示出来
     * 
     * @param userSvcState
     * @param paramSvcState
     * @return
     * @throws Exception
     */
    private boolean checkExistsParamSvcState(IDataset userSvcState, IDataset paramSvcState) throws Exception
    {
        for (int i = 0, sizeparamSvcState = paramSvcState.size(); i < sizeparamSvcState; i++)
        {
            String tempServiceId = paramSvcState.get(i, "SERVICE_ID").toString();

            for (int j = 0, sizeUserSvcState = userSvcState.size(); j < sizeUserSvcState; j++)
            {
                if (StringUtils.equals(tempServiceId, userSvcState.get(j, "SERVICE_ID").toString()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String serialNumber = databus.getString("SERIAL_NUMBER");
        String brandCode = databus.getString("BRAND_CODE");
        String productId = databus.getString("PRODUCT_ID");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String eparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        IDataset userSvcState = new DatasetList();
        userSvcState = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);

        if (IDataUtil.isEmpty(userSvcState))
        {
            String errorInfo = "获取用户服务状态无数据！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 515002, errorInfo);
            return true;
        }

        // 查询服务状态变化配置参数
        IDataset paramSvcState = TradeSvcStateParamInfoQry.querySvcStateParamByKey(tradeTypeCode, brandCode, productId, eparchyCode);
        if (IDataUtil.isEmpty(paramSvcState))
        {
            String tradeTypeName = "";
            tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode, eparchyCode);
            String errorInfo = "获取用户[" + tradeTypeName + "]业务服务状态参数无数据！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 515003, errorInfo);
            return true;
        }

        boolean flag = false;
        String service_id = "";
        String old_state_code = "";
        String mainServiceId = "";
        String mainStateCode = "";

        for (int i = 0, sizeParamSvcState = paramSvcState.size(); i < sizeParamSvcState; i++)
        {
            service_id = paramSvcState.get(i, "SERVICE_ID").toString();
            old_state_code = paramSvcState.get(i, "OLD_STATE_CODE").toString();

            for (int j = 0, sizeUserSvcState = userSvcState.size(); j < sizeUserSvcState; j++)
            {
                String svcStateGroups = "";
                if (service_id.equals(userSvcState.get(j, "SERVICE_ID")))
                {
                    for (int k = 0, kSizeUserSvcState = userSvcState.size(); k < kSizeUserSvcState; k++)
                    {
                        if (userSvcState.get(k, "SERVICE_ID").toString().equals(paramSvcState.get(i, "SERVICE_ID")))
                        {
                            String svcStateCode = userSvcState.get(k, "STATE_CODE").toString();
                            if ("Y".equals(svcStateCode))
                            {
                                svcStateGroups = svcStateGroups + svcStateCode;
                            }
                            else
                            {
                                svcStateGroups = svcStateCode + svcStateGroups;
                            }
                        }
                    }
                    if (svcStateGroups.equals(old_state_code))
                    {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag)
            {
                break;
            }
        }

        if (!flag)
        {
            StringBuilder errorInfo = new StringBuilder();
            if (checkExistsParamSvcState(userSvcState, paramSvcState))
            {
                // 查询出当前主服务对应的状态
                String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode(mainServiceId, mainStateCode);

                String serviceName = USvcInfoQry.getSvcNameBySvcId(mainServiceId);

                errorInfo.append("用户[").append(serialNumber).append("]的[").append(serviceName).append("]服务状态为[").append(stateName).append("], 不满足服务状态变化配置规则！");
            }
            else
            {
                errorInfo.append("用户没有当前业务所需要的服务状态数据，不能受理该业务！");
            }

            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 515004, errorInfo.toString());
            return true;// 代表规则需要报错
        }

        return false;
    }
}
