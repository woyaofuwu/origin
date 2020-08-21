
package com.asiainfo.veris.crm.order.soa.group.dataline.datalinerepeal;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;



public class DatalineRepealOneByOneSvc  extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Logger logger = Logger.getLogger(DatalineRepealOneByOneSvc.class);
    
    /**
     * 一单多线的截止
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset datalineRepealOneByOne(IData data) throws Exception
    {
    	IDataset batResultSet = new DatasetList();
    	
    	if(logger.isDebugEnabled()){
    		logger.debug("服务开通传入的参数-一单多线:" + data.toString());
    	}
    	
    	IData retData = new DataMap();
        retData.put("X_RESULTINFO", "成功!");
        retData.put("X_RESULTCODE", "0");
        batResultSet.add(retData);
        
        if(IDataUtil.isNotEmpty(data))
        {
        	String productNo = data.getString("PRODUCT_NO","");//产品实例编号
        	String serialNo = data.getString("SERIAL_NO","");//bss工单号 ESOP2017092800091
        	
        	if(StringUtils.isNotBlank(productNo) && StringUtils.isNotBlank(serialNo))
        	{
        		IData param = new DataMap();
        		param.put("PRODUCT_NO", productNo.trim());
        		param.put("ATTR_VALUE", serialNo.trim());
        		IDataset tradeDatas = DatalineRepealOneByOneQry.queryDatalineTradeIdByProductNo(param);
        		if(IDataUtil.isNotEmpty(tradeDatas))
        		{
        			String tradeId = tradeDatas.getData(0).getString("TRADE_ID","");
        			if(StringUtils.isNotBlank(tradeId))
        			{
        				DatalineRepealOneByOneQry.insertBhTradeByTradeId(tradeId);
                		DatalineRepealOneByOneQry.deleteBTradeByTradeId(tradeId);
        			}
        		}
        	}
        }
    	
    	return batResultSet;
    }
    
}                      