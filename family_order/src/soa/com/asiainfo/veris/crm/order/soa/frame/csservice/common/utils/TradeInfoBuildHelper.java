package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

public class TradeInfoBuildHelper
{    
    public static IDataset listToDataset(List<? extends BaseTradeData> ora )
    {
        IDataset result = new DatasetList();
        if (CollectionUtils.isEmpty(ora))
        {
            return result;   
        }
        for (Iterator iterator = ora.iterator(); iterator.hasNext();)
        {
            BaseTradeData baseTradeData = (BaseTradeData) iterator.next();
            result.add(baseTradeData.toData());
            
        }
        return result;
    }
    
    public static BusiTradeData getTradeInfoByType(String TradeTypeCode) throws Exception
    {
        List<BusiTradeData> btds = DataBusManager.getDataBus().getBtds();
        BusiTradeData rd = null;
        
        for(int i=0; i< btds.size(); i++){
            BusiTradeData btdTemp = btds.get(i);
            if (TradeTypeCode.equals(btdTemp.getTradeTypeCode()))
            {
                  rd = btdTemp;
            }
        } 
        return rd;
    }
     
}
