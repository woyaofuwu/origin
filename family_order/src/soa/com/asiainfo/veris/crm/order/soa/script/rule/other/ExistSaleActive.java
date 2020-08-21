
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;
import org.apache.log4j.Logger;

public class ExistSaleActive extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistSaleActive.class);

    /**
     * Copyright: Copyright 2014 Asiainfo-Linkage
     *
     * @Description: 【TradeCheckBefore】
     * @author: chenchunni
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistSaleActive() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        String action_type = databus.getString("ACTION_TYPE");
        String orderTypeCode = databus.getString("ORDER_TYPE_CODE","110");
        logger.debug("ExistSaleActive-----xChoiceTag"+xChoiceTag);
        logger.debug("ExistSaleActive-----action_type"+action_type);


        if ("TRADEALL.TradeCheckBefore".equals(action_type) && "110".equals(orderTypeCode)){// 提交前业务检查

            //获取一下提交的用户user_id, 判断用户是否办理泛渠道的营销包，若存在该营销活动则拦截不给用户变更
            String  newProductId =  databus.getString("NEW_PRODUCT_ID","");
            String userId =  databus.getString("USER_ID","");
            logger.debug("ExistSaleActive-----newProductId"+newProductId);
            if (!"".equals(newProductId)){// 本次进行主产品变更，需要判断用户是否办理配置在1304中的营销活动，办理则进行拦截
                // 查询当前用户有效的营销活动资料
                boolean flag = false;
                String startDate = "";
                String endDate = "";

                IDataset active = CommparaInfoQry.getCommByParaAttr("CSM", "1304","0898");
                if (IDataUtil.isNotEmpty(active)){// 存在变更主产品需要校验是否存在在有效期的营销活动
                    for (int i=0; i<active.size(); i++){
                        String productId =  active.getData(i).getString("PARAM_CODE","");
                        String info = active.getData(i).getString("PARA_CODE2","由于参加营销活动，活动期限是");
                        IDataset sale  = BreQry.qryUserSaleActiveInfo(productId,userId);
                        logger.debug("ExistSaleActive-----sale"+sale);
                        if (IDataUtil.isNotEmpty(sale)){
                            startDate = sale.first().getString("START_DATE");
                            endDate = sale.first().getString("END_DATE");
                            logger.debug("ExistSaleActive-----START_DATE"+startDate);
                            logger.debug("ExistSaleActive-----END_DATE"+endDate);
                            StringBuilder errInfo = new StringBuilder("业务受理前条件判断：");
                            errInfo.append(info+startDate+"-"+endDate+"，您在活动期限内无法变更套餐。");
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751144, errInfo.toString());
                            flag = true;
                            break;
                        }
                    }
                }
                bResult = flag;
            }else {
                bResult = false;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistSaleActive() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }

}
