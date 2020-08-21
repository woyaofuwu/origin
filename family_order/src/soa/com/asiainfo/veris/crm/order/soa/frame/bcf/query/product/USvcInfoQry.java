
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.wade.container.util.log.Log;

public final class USvcInfoQry
{
    /**
     * 根据服务标识查询服务名称
     * 
     * @param svcId
     * @return
     * @throws Exception
     */
    public static String getSvcNameBySvcId(String svcId) throws Exception
    {
        try
        {
            return UpcCall.qryOfferNameByOfferTypeOfferCode(svcId, BofConst.ELEMENT_TYPE_CODE_SVC);
        }
        catch (Exception e)
        {
            return "";
        }
    }
    
    public static String getSvcExplainBySvcId(String svcId) throws Exception
    {
        try
        {
            OfferCfg offercfg = OfferCfg.getInstance(svcId, BofConst.ELEMENT_TYPE_CODE_SVC);
            if (offercfg == null)
                return "";

            else
                return offercfg.getDescription();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * 根据服务标识查询服务配置信息
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IData qryServInfoBySvcId(String serviceId) throws Exception
    {
        OfferCfg offercfg=null; //OfferCfg.getInstance(serviceId, BofConst.ELEMENT_TYPE_CODE_SVC);
    	try{ //modify hefeng 对抛错的信息进行捕获处理
    		offercfg= OfferCfg.getInstance(serviceId, BofConst.ELEMENT_TYPE_CODE_SVC);
		}catch (Exception e){
			//log.info("(""+e);
			offercfg=null;
		}
        
        if(offercfg == null)
            return null;
        
        IData svcInfo = new DataMap();
        svcInfo.put("SERVICE_ID", offercfg.getOfferCode());
        svcInfo.put("SERVICE_NAME", offercfg.getOfferName());
        svcInfo.put("NET_TYPE_CODE", offercfg.getNetTypeCode());
        svcInfo.put("INTF_MODE", offercfg.getIntfMode());
        
        svcInfo.putAll(getSvcExtChaInfoByProductId(serviceId));//放入扩展字段
        
        return svcInfo;
    }

    /**
     * 查询产品下的必选服务(顺便获取主体服务判断标志)
     * 
     */
    public static IDataset qryRequireServBySvcId(String offerCode) throws Exception
    {
   	    IDataset offerInfos = UpcCall.qryComRelOffersByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, offerCode);
   	    if(IDataUtil.isNotEmpty(offerInfos))
   	    {
   	    	for(int i=0;i<offerInfos.size();i++)
   	    	{
   	    		IData offer = offerInfos.getData(i);
   	    		if(!("S".equals(offer.getString("OFFER_TYPE", ""))))
   	    		{
   	    			offerInfos.remove(i);
   	    			i--;
   	    		}else{
   	    			offer.put("SERVICE_ID",offer.getString("OFFER_CODE")); 
   	    			offer.put("MAIN_TAG", offer.getString("IS_MAIN_SVC"));
   	    		}  	    		
   	    	}
   	    }
   	    return offerInfos;
    }
    
    /**
     * 根据服务标识查询订购模式
     * 
     * @param svcId
     * @return
     * @throws Exception
     */
    public static String getOrderModeBySvcId(String svcId) throws Exception
    {
        OfferCfg offercfg= OfferCfg.getInstance(svcId, BofConst.ELEMENT_TYPE_CODE_SVC);
        if(offercfg == null)
            return "";
        
        else
            return offercfg.getOrderMode();
    }
    
    public static IDataset getSvcByProduct(String productId) throws Exception
    {
        IDataset dataset = UpcCall.queryAtomOffersFromGroupByOfferIdType(productId, BofConst.ELEMENT_TYPE_CODE_SVC);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0; i<dataset.size(); i++)
        	{
        		IData data = dataset.getData(i);
        		data.put("SERVICE_ID", data.getString("ELEMENT_ID"));
        		data.put("SERVICE_NAME", data.getString("ELEMENT_NAME")); 
        	}
        }
        
         return dataset;
    }
    
    public static IDataset qryOffersWithOfferTypeFilter(String offerType) throws Exception
    {
        IDataset dataset = UpcCall.qryOffersWithOfferTypeFilter(offerType);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0; i<dataset.size(); i++)
        	{
        		IData data = dataset.getData(i);
        		data.put("SERVICE_ID", data.getString("OFFER_CODE"));
        		data.put("SERVICE_NAME", data.getString("OFFER_NAME")); 
        	}
        }
        
         return dataset;
    }
    
    /**
     * 
     * @Title: getSvcExtChaInfoByProductId  
     * @Description: 获取服务表的备用字段 
     * @param @param serviceId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IData    返回类型  
     * @throws
     */
    public static IData getSvcExtChaInfoByProductId(String serviceId) throws Exception
    {
        IDataset results = UpcCall.qryOfferExtChaByOfferId(serviceId, BofConst.ELEMENT_TYPE_CODE_SVC, "TD_B_SERVICE");
        if (IDataUtil.isEmpty(results))
        {
            return new DataMap();
        }
        return results.getData(0);
    }
}
