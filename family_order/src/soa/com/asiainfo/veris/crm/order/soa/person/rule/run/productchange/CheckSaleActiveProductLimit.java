package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**    
 * Copyright: Copyright  2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckSaleActiveProductLimit.java
 * @Description: 存在营销活动某产品 不能办理 其他主产品的限制【TradeCheckBefore】
 *
 * @version: v1.0.0
 * @author: maoke
 * @date: Oct 8, 2014 10:13:54 AM 
 *
 * Modification History:
 * Date            Author      Version        Description
 *-------------------------------------------------------*
 * Oct 8, 2014    maoke       v1.0.0           修改原因	
 */
public class CheckSaleActiveProductLimit extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {
                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                String bookingDate = reqData.getString("BOOKING_DATE");// 预约时间
                
                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))//主产品变更
                {
                    UcaData uca = (UcaData) databus.get("UCADATA");

                    List<SaleActiveTradeData> saleActives =  uca.getUserSaleActives();
                    
                    if(saleActives != null && saleActives.size() > 0)
                    {
                        for(SaleActiveTradeData saleActive : saleActives)
                        {
                            String saleProductId = saleActive.getProductId();
                            String saleProductName = saleActive.getProductName();
                            String endDate = SysDateMgr.decodeTimestamp(saleActive.getEndDate(), SysDateMgr.PATTERN_STAND);
                            
                            IDataset commpara72 = CommparaInfoQry.getCommparaInfoByCode("CSM", "72", saleProductId, newProductId, CSBizBean.getTradeEparchyCode());
                            
                            if(IDataUtil.isNotEmpty(commpara72) && endDate.compareTo(SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND)) > 0)
                            {
                                String errorMsg = "该用户存在营销活动产品["+saleProductName+"],不能更改成主套餐【"+UProductInfoQry.getProductNameByProductId(newProductId)+"】!";
                                
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110240", errorMsg);

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
