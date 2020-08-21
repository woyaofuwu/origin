package com.asiainfo.veris.crm.order.soa.person.busi.replacecard;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ReplaceCardOrderBean extends CSBizBean
{
    
    public static boolean synOrderInfo(IData data) throws Exception{
        return Dao.insert("TF_B_ORDER_DETAIL", data);
    }
    
    public static boolean synOrderDelivery(IData data) throws Exception{
        return Dao.insert("TF_B_DELIVERY_INFO", data);
    }
    
    public static boolean synOrderItem(IData data) throws Exception{
        return Dao.insert("TF_B_ORDER_ITEM", data);
    }
    
    
  	public IDataset qryOrderInfo(IData param,Pagination pagination) throws Exception{
  		 
  		return Dao.qryByCodeParser("TF_B_ORDER_DETAIL", "QUE_ORDER_INFO", param,pagination);
  	}
  	
  	public IData conFirm(IData data) throws Exception{
  		IData dataTag = new DataMap();
  		Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_BY_ORDER_ID", data);
    	dataTag.put("SUCCESS", "SUCCESS");
	    return dataTag;
    }
  	
  	public IDataset qryReadyGood(IData param) throws Exception{
 		 
  		return Dao.qryByCodeParser("TF_B_ORDER_DETAIL", "QUE_BY_ORDERID", param);
  	}
  	
  	public IData readyGoodUpdate(IData data) throws Exception{
  		IData dataTag = new DataMap();
  		Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_BY_ORDER_ID", data);
    	dataTag.put("SUCCESS", "SUCCESS");
	    return dataTag;
    }
  	
  	public IDataset qrysendGood(IData param) throws Exception{
		 
  		return Dao.qryByCodeParser("TF_B_ORDER_DETAIL", "QUE_BY_ORDERID", param);
  	}
  	
  	public IData sendGoodUpdate(IData data) throws Exception{
  		IData dataTag = new DataMap();
  		Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_SENGGOOD_ORDER_ID", data);
    	dataTag.put("SUCCESS", "SUCCESS");
	    return dataTag;
    }
  	
  	public IData completeUpdate(IData data) throws Exception{
  		IData dataTag = new DataMap();
  		Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_BY_ORDER_ID", data);
    	dataTag.put("SUCCESS", "SUCCESS");
	    return dataTag;
    }
  	
  	public IData writeCardUpdate(IData data) throws Exception{
  		IData dataTag = new DataMap();
  		Dao.executeUpdateByCodeCode("TF_B_ORDER_DETAIL", "UPDATE_WRITECARD_ORDER_ID", data);
    	dataTag.put("SUCCESS", "SUCCESS");
	    return dataTag;
    }
}
