
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaActionCodeInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckIsBeautifulNumberLimit.java
 * @Description: "188靓号抢鲜"开户用户产品变更检验【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 22, 2014 4:55:08 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 22, 2014 maoke v1.0.0 修改原因
 */
public class CheckIsBeautifulNumberLimit extends BreBase implements IBREScript
{
    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String userProductId = databus.getString("PRODUCT_ID");// 老产品

                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))
                {
                    String serialNumber = databus.getString("SERIAL_NUMBER");

                    IDataset saActionCodeData = SaActionCodeInfoQry.querySaActionCodeBySn(serialNumber);

                    if (IDataUtil.isNotEmpty(saActionCodeData))
                    {
                        IData tempData = saActionCodeData.getData(0);

                        String rsrvStr1 = tempData.getString("RSRV_STR1");
                        String rsrvNum1 = tempData.getString("RSRV_NUM1");

                        if ("1".equals(rsrvStr1) && Integer.parseInt(rsrvNum1) >= 1188)
                        {
                            UcaData ucaData = (UcaData) databus.get("UCADATA");

                            String brandCode = UProductInfoQry.getBrandCodeByProductId(newProductId);
                            String openDate = ucaData.getUser().getOpenDate();

                            if (SysDateMgr.monthInterval(openDate, SysDateMgr.getSysTime()) < 12)
                            {
                                if (!"G001".equals(brandCode) || "10000000".equals(newProductId) || "10001311".equals(newProductId))
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 514301, "\"188靓号抢鲜\"活动要求保证金大于或等于1188元的号码,须登记且连续使用月租费或月最低消费额在80元以上的全球通套餐满12个月!");

                                    return true;
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
