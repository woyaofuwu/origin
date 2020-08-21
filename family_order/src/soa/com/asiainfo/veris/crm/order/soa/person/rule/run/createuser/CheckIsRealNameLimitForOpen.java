
package com.asiainfo.veris.crm.order.soa.person.rule.run.createuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckIsRealNameLimitForOpen.java
 * @Description: 开户判断实名制用户办理的产品
 * @version: v1.0.0
 * @author: sunxin
 * @date: May 21, 2014 10:12:45 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 21, 2014 sunxin v1.0.0 修改原因
 */
public class CheckIsRealNameLimitForOpen extends BreBase implements IBREScript
{

    // @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据
            UcaData ucaData = (UcaData) databus.get("UCADATA");
            if (IDataUtil.isNotEmpty(reqData))
            {

                String isRealName = reqData.getString("REAL_NAME");

                if (!"1".equals(isRealName))
                {
                    String newProductId = reqData.getString("PRODUCT_ID");

                    if (StringUtils.isNotBlank(newProductId))
                    {
                        String newProductName = UProductInfoQry.getProductNameByProductId(newProductId);

                        IDataset commpara1030 = CommparaInfoQry.getCommPkInfo("CSM", "1030", newProductId, CSBizBean.getTradeEparchyCode());

                        if (IDataUtil.isNotEmpty(commpara1030))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 12062701, "非实名制用户，不能办理该产品【" + newProductName + "】套餐!");

                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
