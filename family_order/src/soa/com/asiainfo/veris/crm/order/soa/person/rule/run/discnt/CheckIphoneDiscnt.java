
package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckIphoneDiscnt.java
 * @Description: 校验IPHONE营销活动最低消费对应的优惠 根据IPHONE营销活动包，对应到语音和流量不允许做变更处理【配置条件：TradeCheckAfter】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 20, 2014 8:04:56 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 20, 2014 maoke v1.0.0 修改原因
 */
public class CheckIphoneDiscnt extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String userId = databus.getString("USER_ID");

        if (StringUtils.isBlank(xChoiceTag))// 提交时校验，依赖请求数据
        {
            IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

            if (IDataUtil.isNotEmpty(listTradeDiscnt))
            {
                IDataset commparam1655 = UserDiscntInfoQry.getCommpara1655ByUserId(userId);

                if (IDataUtil.isNotEmpty(commparam1655))
                {
                    for (int j = 0, size = commparam1655.size(); j < size; j++)
                    {
                        String paramCode = commparam1655.getData(j).getString("PARAM_CODE");
                        String paramName = commparam1655.getData(j).getString("PARAM_NAME");
                        String paraCode2 = commparam1655.getData(j).getString("PARA_CODE2");
                        String paraCode3 = commparam1655.getData(j).getString("PARA_CODE3");

                        for (int i = 0, tdSize = listTradeDiscnt.size(); i < tdSize; i++)
                        {
                            IData element = listTradeDiscnt.getData(i);

                            String elementId = element.getString("DISCNT_CODE");
                            String elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                            String modifyTag = element.getString("MODIFY_TAG");

                            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) && (elementId.equals(paraCode2) || elementId.equals(paraCode3)))
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 589005, paramCode + "：【" + paramName + "】相对应的" + elementId + "：【" + elementName + "】优惠不能删除！");

                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
