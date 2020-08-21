
package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
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
 * @ClassName: CheckUserSvcStateChgRule.java
 * @Description: 校验用户的服务状态是否符合td_s_trade_svcstate表服务状态变更配置
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-19 下午3:18:18
 */
public class CheckUserSvcStateChgRule extends BreBase implements IBREScript
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
        for (int i = 0, sizeParamSvcState = paramSvcState.size(); i < sizeParamSvcState; i++)
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

        // 查询用户有效主体服务状态
        IDataset svcStateDataset = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
        if (IDataUtil.isEmpty(svcStateDataset) || svcStateDataset.size() > 2)
        {
            StringBuilder msg = new StringBuilder(100);
            msg.append("用户服务状态资料错误！用户主服务没有有效的服务状态，请检查数据！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", msg.toString());
            return true;
        }
        
        IDataset userSvcState = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
        if (IDataUtil.isEmpty(userSvcState))
        {
            String errorInfo = "获取用户服务状态无数据！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 515002, errorInfo);

        }

        // 查询服务状态变化配置参数
        IDataset paramSvcState = TradeSvcStateParamInfoQry.querySvcStateParamByKey(tradeTypeCode, brandCode, productId, eparchyCode);
        if (IDataUtil.isEmpty(paramSvcState))
        {
            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode, eparchyCode);
            String errorInfo = "获取用户[" + tradeTypeName + "]业务服务状态参数无数据！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 515003, errorInfo);
        }

        boolean flag = false;
        String service_id = "";
        String state_code = "";
        String mainServiceId = "";
        String mainStateCode = "";

        for (int i = 0, sizeUserSvcState = userSvcState.size(); i < sizeUserSvcState; i++)
        {
            service_id = userSvcState.get(i, "SERVICE_ID").toString();
            state_code = userSvcState.get(i, "STATE_CODE").toString();
            if (StringUtils.equals("1", userSvcState.get(i, "MAIN_TAG").toString()))
            {
                mainServiceId = service_id;
                mainStateCode = state_code;
            }

            for (int j = 0, sizeParamSvcState = paramSvcState.size(); j < sizeParamSvcState; j++)
            {
                String tempServiceId = paramSvcState.get(j, "SERVICE_ID").toString();
                String tempOldStateCode = paramSvcState.get(j, "OLD_STATE_CODE").toString();
                if (StringUtils.equals(service_id, tempServiceId) && StringUtils.equals(state_code, tempOldStateCode))
                {
                    flag = true;
                    break;
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

                errorInfo.append("用户").append(serialNumber).append("的").append(serviceName).append("服务状态为").append(stateName).append(", 不满足服务状态变化配置规则！");
            }
            else
            {
                errorInfo.append("用户没有当前业务所需要的服务状态数据，不能受理该业务！");
            }

            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 515004, errorInfo.toString());

        }
        return false;
    }
}
