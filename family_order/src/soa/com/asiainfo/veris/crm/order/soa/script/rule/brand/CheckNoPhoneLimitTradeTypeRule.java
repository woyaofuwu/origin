package com.asiainfo.veris.crm.order.soa.script.rule.brand;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.NoPhoneTradeUtil;

/**
 * @ClassName CheckNoPhoneLimitTradeTypeRule
 * @Description 无手机宽带账号或手机号码只能办理无手机的业务，其他业务不允许办理。
 * @Author ApeJungle
 * @Date 2019/7/8 11:12
 * @Version 1.0
 */
public class CheckNoPhoneLimitTradeTypeRule extends BreBase implements IBREScript {

    private static final long serialVersionUID = 8687825166597590097L;
    private static final Logger log = Logger.getLogger(CheckNoPhoneLimitTradeTypeRule.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {

        if ("0".equals(databus.getString("X_CHOICE_TAG", "0"))) {
            String serialNumber = databus.getString("SERIAL_NUMBER");
            String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            
            // 表示号码是无手机用户
            if (NoPhoneTradeUtil.checkIfNoPhoneUserNewForRule(serialNumber)) {
                // 如果PARA_CODE1配置为4900|4240这种格式，那么最好不用 contains 和 indexOf 方法，因为可能会误判！
                IDataset params = CommparaInfoQry.getCommpara("CSM", "2828", "ENABLE_TRADE_TYPE", CSBizBean.getUserEparchyCode());
                if (IDataUtil.isNotEmpty(params)) {
                	//配置开关 N-关，Y-开
                	String switchFlag = params.first().getString("PARA_CODE1");
                	if ("N".equals(switchFlag)) {
						return false;
					}
                	
                    String enableTradeStr = params.first().getString("PARA_CODE3");
                    if (StringUtils.isNotEmpty(enableTradeStr)) {
                        String[] enableTradeArray = enableTradeStr.split("\\|");
                        for (int i = 0, s = enableTradeArray.length; i < s; i++) {
                            if (enableTradeArray[i].equals(tradeTypeCode)) return false;
                        }
                    }
                }
                
                //表示不能受理该业务
                String errorInfo = "该号码[" + serialNumber + "]为无手机宽带用户号码，不能办理该业务！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6082, errorInfo);
                return true;
            }
        }
        
        return false;
    }
}
