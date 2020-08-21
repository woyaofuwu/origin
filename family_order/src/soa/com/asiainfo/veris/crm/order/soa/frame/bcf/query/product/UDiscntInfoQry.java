package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UDiscntInfoQry
{

    /**
     * 根据优惠编码查询优惠名称
     * 
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static String getDiscntExplainByDiscntCode(String discntCode) throws Exception
    {
        try
        {
            OfferCfg offercfg = OfferCfg.getInstance(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
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
     * 根据优惠编码查询优惠配置信息
     * 
     * @param discnt_code
     * @return
     * @throws Exception
     */
    public static IData getDiscntInfoByPk(String discnt_code) throws Exception
    {
        OfferCfg offercfg = OfferCfg.getInstance(discnt_code, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        if (offercfg == null)
            return new DataMap();

        IData discntInfo = new DataMap();
        discntInfo.put("DISCNT_CODE", offercfg.getOfferCode());
        discntInfo.put("DISCNT_NAME", offercfg.getOfferName());
        discntInfo.put("DISCNT_EXPLAIN", offercfg.getDescription());
        discntInfo.put("INTF_MODE", offercfg.getIntfMode());
        
        discntInfo.putAll(getDiscntExtChaInfoByDiscntCode(discnt_code));

        return discntInfo;
    }

    /**
     * 根据优惠编码查询优惠名称
     * 
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static String getDiscntNameByDiscntCode(String discntCode) throws Exception
    {
        try
        {
            return UpcCall.qryOfferNameByOfferTypeOfferCode(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        }
        catch (Exception e)
        {
            return "";
        }
    }
    /**
     * 根据权益编码查询权益包名称by huangmx5
     * 
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static String getWelfareNameByOfferCodeAndOfferType(String offerCode) throws Exception
    {
        try
        {
            return UpcCall.qryOfferNameByOfferTypeOfferCode(offerCode, "Q");
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * 根据优惠编码查询订购模式
     * 
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static String getOrderModeByDiscntCode(String discntCode) throws Exception
    {
        OfferCfg offercfg = OfferCfg.getInstance(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        if (offercfg == null)
            return "";

        else
            return offercfg.getOrderMode();
    }

    public static IDataset getDiscntByProduct(String productId) throws Exception
    {
        IDataset dataset = UpcCall.queryAtomOffersFromGroupByOfferIdType(productId, "D");
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);
                data.put("DISCNT_CODE", data.getString("ELEMENT_ID"));
                data.put("DISCNT_NAME", data.getString("ELEMENT_NAME"));
            }
        }

        return dataset;
    }

    /**
     * 根据offer_code获取 from_table_name 表的特定字段 StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_B_PRODUCT","PRODUCT_ID","RSRV_STR1",productId); 暂支持 TD_B_PRODUCT 和 TD_B_DISCNT hefeng
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getTableNameValue(String from_table_name, String field_name, String offer_type, String offer_code) throws Exception
    {

        IDataset dataset = UpcCall.getTableNameValue(from_table_name, field_name, offer_type, offer_code);
        if (IDataUtil.isEmpty(dataset))
        {
            return "";
        }

        return dataset.getData(0).getString("FIELD_VALUE","-1");//modify hefeng 新增取值为空给默认值 -1 
    }

    public static IDataset queryDiscntTypeByDiscntCode(String discntCode) throws Exception
    {
        IDataset dataset = UpcCall.getTableNameValue("TD_B_DTYPE_DISCNT", "DISCNT_TYPE_CODE", "D", discntCode);
        return dataset;
    }

    public static String getDiscntTypeByDiscntCode(String discntCode) throws Exception
    {
        IDataset dataset = queryDiscntTypeByDiscntCode(discntCode);
        if (IDataUtil.isEmpty(dataset))
        {
            return "";
        }

        return dataset.getData(0).getString("FIELD_VALUE","-1");//modify hefeng 新增取值为空给默认值 -1 
    }
    
    /**
     * 
     * @Title: getDiscntExtChaInfoByDiscntCode  
     * @Description: 获取资费表的备用字段
     * @param @param discntCode
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IData    返回类型  
     * @throws
     */
    public static IData getDiscntExtChaInfoByDiscntCode(String discntCode) throws Exception
    {
        IDataset results = UpcCall.qryOfferExtChaByOfferId(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT, "TD_B_DISCNT");
        if (IDataUtil.isEmpty(results))
        {
            return new DataMap();
        }
        return results.getData(0);
    }
    
    public static IDataset queryDiscntsByDiscntType(String discntType) throws Exception{
    	IDataset results = UpcCall.queryExtChasOffers("TD_B_DTYPE_DISCNT", "DISCNT_TYPE_CODE", discntType);
    	if(IDataUtil.isEmpty(results)){
    		return results;
    	}
    	for(int i=0;i<results.size();i++){
    		IData temp = results.getData(i);
    		if(temp.getString("EXPIRE_DATE").compareTo(SysDateMgr.getSysTime())<0){
    			results.remove(i);
    			i--;
    		}else{
    			temp.put("DISCNT_CODE", temp.getString("OFFER_CODE"));
    			temp.put("DISCNT_NAME", temp.getString("OFFER_NAME"));
    		}
    	}
/*    	for(Object obj : results){
    		IData result = (IData)obj;
    		result.put("DISCNT_CODE", result.getString("OFFER_CODE"));
    		result.put("DISCNT_NAME", result.getString("OFFER_NAME"));

    	}*/
    	return results;
    }
    
    public static IDataset queryDiscntsByPkgIdEparchy(String packageId, String eparchyCode) throws Exception
    {
        IDataset groupComRels = UpcCall.queryGroupComRelOfferByGroupId(packageId, eparchyCode);
        
        IDataset offerComRels = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType("K", packageId, "D", eparchyCode);
        
        IDataset result = new DatasetList();
        if(IDataUtil.isNotEmpty(groupComRels))
        {
            for(Object obj : groupComRels)
            {
                IData groupComRel = (IData) obj;
                groupComRel.put("ELEMENT_TYPE_CODE", groupComRel.getString("OFFER_TYPE"));
                groupComRel.put("ELEMENT_ID", groupComRel.getString("OFFER_CODE"));
            }
            
            result.addAll(groupComRels);
        }
        
        if(IDataUtil.isNotEmpty(offerComRels))
        {
            for(Object obj : offerComRels)
            {
                IData offerComRel = (IData) obj;
                offerComRel.put("ELEMENT_TYPE_CODE", offerComRel.getString("OFFER_TYPE"));
                offerComRel.put("ELEMENT_ID", offerComRel.getString("OFFER_CODE"));
            }
            
            result.addAll(offerComRels);
        }
        
        return result;
    }
}
