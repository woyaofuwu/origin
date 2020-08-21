
package com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct;

 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * REQ201607050007 关于移动电视尝鲜活动的需求
 * chenxy3 20160720
 * */
public class IMSChangeProductBean extends CSBizBean
{
	private static transient Logger logger = Logger.getLogger(IMSChangeProductBean.class);
	 
	
	/**
	 * 获取用户是否存在已完工（未完工）的宽带信息。
	 * */
	public static IDataset getUserWilenInfos(IData param) throws Exception
    { 
	    IDataset resultDataset = new DatasetList();
	    
        IDataset dataInfos = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_WILEN_BY_SN_PRODID", param);
        
        if (IDataUtil.isNotEmpty(dataInfos))
        {
            return dataInfos;
        }
        else
        {
            dataInfos = TradeInfoQry.getTradeInfoBySn(param.getString("SERIAL_NUMBER"));
            
            if (IDataUtil.isNotEmpty(dataInfos))
            {
                if (param.getString("PRODUCT_ID").equals(dataInfos.getData(0).getString("PRODUCT_ID")))
                {
                    return dataInfos;
                }
            }
        }
        
        return null;
    }  
}
