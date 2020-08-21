
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductVideoFlowDiscntBean;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductRegSVC.java
 * @Description: 产品变更登记类
 * @version: v1.0.0
 * @author: maoke
 * @date: Jul 28, 2014 10:01:05 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jul 28, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", input.getString("TRADE_TYPE_CODE", "110"));
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "110");
    }
    
    @Override
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {

         String elementId = input.getString("ELEMENT_ID","");  
        
        
        if (isExists(elementId) || "OVER_CHECK".equals(btd.getRD().getRemark()))
        {
           return;
        }
    	//针对视频流量业务资费包的特殊校验
    	new ChangeProductVideoFlowDiscntBean().checkVideoFlowDiscnt(tableData, btd);
    	
    	super.checkAfterRule(tableData, btd);
    }
    
    private boolean isExists(String elementId) throws Exception
    {
        String result = "";
         IDataset serviceListMain = StaticUtil.getStaticList("CHGPRO_OVERCHECK_ELEMENTS"); 
          
          if (IDataUtil.isNotEmpty(serviceListMain))
          {
              for (Iterator iterator = serviceListMain.iterator(); iterator.hasNext();)
              { 
                IData item = (IData) iterator.next(); 
                
                if (item.getString("DATA_ID","").equals(elementId))
                {
                    result = item.getString("DATA_ID","");
                    break;
                } 
              }
             
          }
         return StringUtils.isNotBlank(result);
    }
}
