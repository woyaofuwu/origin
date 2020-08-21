
package com.asiainfo.veris.crm.order.soa.group.dataline.datalineinstancemgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDataLineAttrInfoQry;



public class DatalineInstanceMgrSvc extends CSBizService
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 根据专线实例查询该专线实例是否有未完工的工单
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
    public IData qryDatalineInstanceByProductNo(IData inParam) throws Exception
    {
    	String productNo = IDataUtil.getMandaData(inParam, "PRODUCT_NO");
    	IDataset lineData = TradeDataLineAttrInfoQry.qryDatalineInstanceByProductNo(productNo);
    	IData result = new DataMap();
        if(IDataUtil.isNotEmpty(lineData)){
            result.put("PRODUCTNO_EXISTS", "TRUE");
        } else {
            result.put("PRODUCTNO_EXISTS", "FALSE"); 
        }
        return result;
        
    }
}                      