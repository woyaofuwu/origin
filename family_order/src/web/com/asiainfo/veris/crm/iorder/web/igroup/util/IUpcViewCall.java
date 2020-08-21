package com.asiainfo.veris.crm.iorder.web.igroup.util;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;


public class IUpcViewCall
{

    /**
     * 根据OFFERID获取OFFERCODE
     * @param offerId
     * @return
     * @throws Exception
     */
    public static  String getOfferCodeByOfferId(String offerId)throws Exception
    {
    	String offerCode ="";
        IData  offer = getOfferInfoByOfferId(offerId);
        if (IDataUtil.isNotEmpty(offer))
        {
        	offerCode = offer.getString("OFFER_CODE");
		}
     
        return offerCode;
    }
    
    /**
     * 根据OFFERCODE获取OFFERID
     * @param offerCode
     * @return
     * @throws Exception
     */
    public static  String getOfferIdByOfferCode(String offerCode)throws Exception
    {
    	String offerId ="";
        IData  offer = getOfferInfoByOfferCode(offerCode);
        if (IDataUtil.isNotEmpty(offer))
        {
        	offerId = offer.getString("OFFER_ID");
		}
     
        return offerId;
    }
    /**
     * 根据OFFERID获取OFFERINFO
     * @param offerId
     * @return
     * @throws Exception
     */
    public static  IData getOfferInfoByOfferId(String offerId)throws Exception
    {
        IData  offer = queryOfferByOfferId(offerId);
        if (IDataUtil.isEmpty(offer))
        {
        	return null;
		}
     
        return offer;
    }
    
    /**
     * 根据OFFERID获取OFFERINFO
     * @param offerId
     * @return
     * @throws Exception
     */
    public static  IData getOfferInfoByOfferCode(String offerCode)throws Exception
    {
        IData  offer = queryOfferByOfferCode(offerCode);
        if (IDataUtil.isEmpty(offer))
        {
        	return null;
		}
     
        return offer;
    }
    
    /**
     * *商品相关接口
     * *根据商品ID查询商品
     * @param offerType 必填
     * @param offerCode 必填
     * @param queryComCha 可填
     * @throws Exception
     */
     
    public static IData queryOfferByOfferId(String offerId) throws Exception
    {
        return queryOfferByOfferId(offerId,null);
    }
    
    /**
     * *商品相关接口
     * *根据商品ID查询商品
     * @param offerType 必填
     * @param offerCode 必填
     * @param queryComCha 可填
     * @throws Exception
     */
     
    public static IData queryOfferByOfferCode(String offerCode) throws Exception
    {
        return queryOfferByOfferCode(offerCode,null);
    }
    /**
     * *商品相关接口
     * *根据商品ID查询商品
     * @param offerType 必填
     * @param offerCode 必填
     * @param queryComCha 可填
     * @throws Exception
     */
     
    public static IData queryOfferByOfferId(String offerId , String queryComCha) throws Exception
    {
    	if(StringUtils.isBlank(offerId)){
    		return null;
    	}
        IData input = new DataMap();
        
        input.put("OFFER_ID",offerId);
        if(StringUtils.isNotEmpty(queryComCha)){
            input.put("QUERY_COM_CHA",queryComCha);
        }
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferId", input);
        IDataset dataset = result.getDataset("OUTDATA");
        if(IDataUtil.isNotEmpty(dataset))
        {
            return dataset.getData(0);
        }
        return null;
    }
    
    /**
     * *商品相关接口
     * *根据商品ID查询商品
     * @param offerType 必填
     * @param offerCode 必填
     * @param queryComCha 可填
     * @throws Exception
     */
     
    public static IData queryOfferByOfferCode(String offerCode , String queryComCha) throws Exception
    {
    	if(StringUtils.isBlank(offerCode)){
    		return null;
    	}
        IData input = new DataMap();
        
        input.put("OFFER_CODE",offerCode);
        input.put("OFFER_TYPE",IUpcConst.ELEMENT_TYPE_CODE_PRODUCT);
        if(StringUtils.isNotEmpty(queryComCha))
        {
            input.put("QUERY_COM_CHA",queryComCha);
        }
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferId", input);
        IDataset dataset = result.getDataset("OUTDATA");
        if(IDataUtil.isNotEmpty(dataset))
        {
            return dataset.getData(0);
        }
        return null;
    }
    
    /**
     * 
     * @Title: queryMemProductIdByProductId  
     * @Description: 根据集团产品查询成员产品
     * @param @param productId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return String    返回类型  
     * @throws
     */
    public static String queryMemOfferIdByOfferId(String offerId) throws Exception
    {
        IDataset joinRelOffers = queryOfferJoinRelAndOfferByOfferId(offerId, "1", "0", "");
        
        if (IDataUtil.isEmpty(joinRelOffers))
        {
            return ""; // 应该要报错的
        }
        
        return joinRelOffers.getData(0).getString("OFFER_ID");
    }
    
    public static String queryMemOfferCodeByOfferCode(String offerCode) throws Exception
    {
        IDataset joinRelOffers = queryOfferJoinRelAndOfferByOfferCodeOfferType(offerCode, IUpcConst.ELEMENT_TYPE_CODE_PRODUCT, "1", "0", "");

        if (IDataUtil.isEmpty(joinRelOffers))
        {
            return ""; // 应该要报错的
        }
        
        return joinRelOffers.getData(0).getString("OFFER_CODE");
    }
    
    /**
     * 
     * @Title: queryMemProductIdByProductId  
     * @Description: 根据集团产品查询成员产品
     * @param @param productId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return String    返回类型  
     * @throws
     */
    public static IDataset queryOfferByOfferIdGroupIdRelOfferIdType(String offerId,String relOfferId,String groupId,String groupSelectFlag,String mgmtDistrict) throws Exception
    {
        IData input = new DataMap();
        
        input.put("OFFER_ID", offerId);
        input.put("REL_OFFER_ID", relOfferId);
        input.put("MGMT_DISTRICT", mgmtDistrict);
        input.put("GROUP_ID",groupId);
        input.put("GROUP_SELECT_FLAG",groupSelectFlag);
        input.put("ENABLE_TAG","Y");
        
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferIdGroupIdRelOfferIdType", input);
        
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    /**
     * 
     * @Title: queryChildOfferByGroupId  
     * @Description: 根据GROUP_ID查询商品组明细
     * @param @param productId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return String    返回类型  
     * @throws
     */
    public static IDataset queryChildOfferByGroupId(String groupId,String mgmtDistrict) throws Exception
    {
        IData input = new DataMap();
        
        input.put("GROUP_ID", groupId);
        input.put("MGMT_DISTRICT", mgmtDistrict);
        IData result = call("UPC.Out.OfferQueryFSV.queryGroupComRelOfferByGroupId", input);
        
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
    /**
     * 
     * @Title: queryChildOfferByGroupIdOfferId  
     * @Description: 根据GROUP_ID查询商品组明细
     * @param @param productId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return String    返回类型  
     * @throws
     */
    public static IDataset queryChildOfferByGroupIdOfferId(String groupId,String offerCode,String offerType,String mgmtDistrict) throws Exception
    {
        IData input = new DataMap();
        input.put("GROUP_ID", groupId);
        input.put("OFFER_CODE", offerCode);
        input.put("OFFER_TYPE", offerType);
        input.put("MGMT_DISTRICT", mgmtDistrict);
        IData result = call("UPC.Out.GroupQueryFSV.queryChildOfferByGroupId", input);
        
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
    public static IDataset queryChaByOfferId(String offerId) throws Exception
    {
        IData input = new DataMap();
        input.put("OFFER_ID", offerId);

        IData result = call("UPC.Out.ChaQueryFSV.queryOfferChaAndValByCond", input);

        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    public static IDataset queryCatalogsByUpCatalogId(String upCatalogId) throws Exception
    {
        IData input = new DataMap();
        input.put("UP_CATALOG_ID", upCatalogId);

        IData result = call("UPC.Out.CatalogQueryFSV.qryCatalogsByUpCatalogId", input);

        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }
   
    public static IDataset queryOffersByCatalogId(String catalogId,String eparchyCode) throws Exception
    {
        IData input = new DataMap();
        input.put("CATALOG_ID", catalogId);
        input.put("EPARCHY_CODE", eparchyCode);

        IData result = call("UPC.Out.OfferQueryFSV.qryOffersByCatalogId", input);

        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
    
    public static String queryBrandCodeByOfferId(String offerId) throws Exception
    {
        IDataset results = queryOfferComChaByCond(offerId, "BRAND_CODE");
        
        if(IDataUtil.isEmpty(results))
        {
            return "";
        }
        
        return results.first().getString("FIELD_VALUE");
    }
    
    /**
     * 查询产品是否为定制产品
     */
    public static String getUseTagByOfferId(String offerId) throws Exception
    {
        IDataset results = queryOfferComChaByCond(offerId, "USE_TAG");
        if(IDataUtil.isEmpty(results))
        {
            return "";
        }
        
        return results.first().getString("FIELD_VALUE");
    }
    
    /**
     * 查询产品的关系类型
     */
    public static String getRelationTypeCodeByOfferId(String offerId) throws Exception
    {
        IDataset results = queryOfferComChaByCond(offerId, "RELATION_TYPE_CODE");
        if(IDataUtil.isEmpty(results))
        {
            return "";
        }
        
        return results.first().getString("FIELD_VALUE");
    }
    public static IDataset queryOfferComChaByCond(String offerId, String fieldName) throws Exception 
    {
        IData input = new DataMap();

        input.put("OFFER_ID", offerId);
        if (StringUtils.isNotEmpty(fieldName)) {
            input.put("FIELD_NAME", fieldName);
        }
        IData result = call("UPC.Out.ChaQueryFSV.queryOfferComChaByCond", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
    
    public static IDataset queryOfferComRelOfferByOfferId(String offerId, String eparchyCode) throws Exception
    {
    	if(StringUtils.isBlank(offerId)){ 
    		return null;
    	}
        IData input = new DataMap();

        input.put("OFFER_ID", offerId);
        input.put("MGMT_DISTRICT", eparchyCode);
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferComRelOfferByOfferId", input);
        IDataset dataset = result.getDataset("OUTDATA");
        if(IDataUtil.isNotEmpty(dataset))
        {
            for(int i = 0, size = dataset.size(); i < size; i++)
            {
                IData offerCom = dataset.getData(i);
                offerCom.put("SELECT_FLAG", "0"); // 构成关系表的商品都是必选商品
                offerCom.put("FORCE_TAG", true); // 标识构成关系或者必选包的必选元素
            }
        }

        return dataset;
    }
    
    public static IDataset queryOfferGroups(String offerId,String mgmtDistrict) throws Exception{
        IData data = new DataMap();
        data.put("OFFER_ID", offerId);
        data.put("MGMT_DISTRICT", mgmtDistrict);
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferGroupRelOfferId", data);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    public static IDataset queryMebGroupByOfferId(String offerId, String mgmtDistrict) throws Exception
    {
        IData param = new DataMap();
        param.put("OFFER_ID", offerId);
        param.put("MGMT_DISTRICT", mgmtDistrict);
        IData result = call("UPC.Out.GroupQueryFSV.queryMebGroupByOfferId", param);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    /**
     * *商品相关接口
     * *根据商品ID查询商品关联关系并关联出商品
     * @param offerType 必填
     * @param offerCode 必填
     * @param relType 可填
     * @param selectFlag 可填
     * @param queryCha 可填
     * @throws Exception
     */
    public static IDataset queryOfferJoinRelAndOfferByOfferId(String offerId , String relType , String selectFlag , String queryCha) throws Exception
    {
        IData input = new DataMap();
        input.put("OFFER_ID",offerId);
        if(StringUtils.isNotEmpty(relType)){
            input.put("REL_TYPE",relType);
        }
        if(StringUtils.isNotEmpty(selectFlag)){
            input.put("SELECT_FLAG",selectFlag);
        }
        if(StringUtils.isNotEmpty(queryCha)){
            input.put("QUERY_CHA",queryCha);
        }                    
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelAndOfferByOfferId", input);
        
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    public static IDataset queryOfferJoinRelAndOfferByOfferCodeOfferType(String offerCode, String offerType, String relType , String selectFlag , String queryCha) throws Exception
    {
        IData input = new DataMap();
        input.put("OFFER_CODE", offerCode);
        input.put("OFFER_TYPE", offerType);
        if(StringUtils.isNotEmpty(relType)){
            input.put("REL_TYPE",relType);
        }
        if(StringUtils.isNotEmpty(selectFlag)){
            input.put("SELECT_FLAG",selectFlag);
        }
        if(StringUtils.isNotEmpty(queryCha)){
            input.put("QUERY_CHA",queryCha);
        }
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelAndOfferByOfferId", input);
        
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }

    private final static IData call(String svcName, IData input) throws Exception{
        return call(svcName, input, null, true);
    }
    
    private final static IData call(String svcName, IData input, Pagination pagin, boolean iscatch) throws Exception{

        ServiceResponse response = BizServiceFactory.call(svcName, input, pagin);
        IData out = response.getBody();
        if(pagin != null)
            out.put("X_COUNTNUM", response.getDataCount());
        
        return out;
        
     }
    public static IDataset queryOfferJoinRelBy2OfferIdRelType(String offerCode, String relOfferCode, String relType)throws Exception 
    {
        IData input = new DataMap();
        input.put("OFFER_ID", offerCode);
        input.put("REL_OFFER_ID", relOfferCode); 
        input.put("REL_TYPE", relType);
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelBy2OfferIdRelType", input);

        IDataset dataset = result.getDataset("OUTDATA") ;

        return dataset;
    }
    
    public static IDataset queryOfferDetailByGroupIdOfferId(String groupId, String offerId) throws Exception
    {
        IData input = new DataMap();

        input.put("GROUP_ID", groupId);
        input.put("OFFER_ID", offerId);//元素

        IData result = call("UPC.Out.OfferQueryFSV.queryEnableModeByGroupIdOfferIdOfferType", input);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }
    
    /**
     * 
     * @Title: queryMemProductIdByProductId  
     * @Description: 根据集团产品查询成员产品
     * @param @param productId
     * @param @return
     * @param @throws Exception    设定文件  
     * @return String    返回类型  
     * @throws
     */
    public static IDataset queryOfferByOfferIdGroupIdRelOfferIdType(String offerCode,String offerType,String relOfferCode,String relOfferType,String groupId,String groupSelectFlag,String mgmtDistrict,String enableTag) throws Exception
    {
        IData input = new DataMap();
        
        input.put("OFFER_CODE", offerCode);
        input.put("REL_OFFER_CODE", relOfferCode);
        input.put("OFFER_TYPE", offerType);
        input.put("REL_OFFER_TYPE", relOfferType);
        input.put("MGMT_DISTRICT", mgmtDistrict);
        input.put("GROUP_ID",groupId);
        input.put("GROUP_SELECT_FLAG",groupSelectFlag);
        input.put("ENABLE_TAG",enableTag);//Y查询生失效方式
        
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferByOfferIdGroupIdRelOfferIdType", input);
        
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
    
    //-----------------ADD BY LIM ---------
    public static String getUseTagByProductId(String offerCode) throws Exception
    {
    	IData input = new DataMap();

		input.put("OFFER_TYPE", IUpcConst.ELEMENT_TYPE_CODE_PRODUCT);
		input.put("OFFER_CODE", offerCode);
		input.put("FIELD_NAME", "USE_TAG");
		IData result = call("UPC.Out.ChaQueryFSV.queryOfferComChaByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");
        if(IDataUtil.isEmpty(dataset)){
            return "";
        }
        else
        {
            return dataset.getData(0).getString("FIELD_VALUE");
        }
    }
    
    public static IDataset queryOfferComRelOfferByOfferId(String offerType, String offerCode, String eparchyCode) throws Exception
    {
      if ((StringUtils.isBlank(offerCode)) || (StringUtils.equals(offerCode, "-1")))
        return null;

      IData input = new DataMap();

      input.put("OFFER_TYPE", offerType);
      input.put("OFFER_CODE", offerCode);
      input.put("MGMT_DISTRICT", eparchyCode);
      IData result = call("UPC.Out.OfferQueryFSV.queryOfferComRelOfferByOfferId", input);
      IDataset dataset = result.getDataset("OUTDATA");

      return dataset;
    }
    
	/**
	 * *商品相关接口 *根据组编码查询组信息
	 * 
	 * @param groupId
	 *            必填
	 * @param groupType
	 *            必填
	 * @throws Exception
	 */

	public static IDataset queryGroupByCond(String groupId, String groupType) throws Exception {
		IData input = new DataMap();

		input.put("GROUP_ID", groupId);
		if (StringUtils.isNotEmpty(groupType)) {
			input.put("GROUP_TYPE", groupType);
		}
		IData result = call("UPC.Out.GroupQueryFSV.queryGroupByCond", input);
		IDataset dataset = result.getDataset("OUTDATA");

		return dataset;
	}
	
	public static IDataset queryGroupComRelOffer(String groupId, String eparchyCode, String staffId) throws Exception{
        IData data = new DataMap();
        data.put("GROUP_ID", groupId);
        data.put("MGMT_DISTRICT", eparchyCode);

    	
        IData result = call("UPC.Out.OfferQueryFSV.queryGroupComRelOfferByGroupId", data);
        IDataset dataset = result.getDataset("OUTDATA");
        return dataset;
    }
	
	public static IDataset queryOfferGroupsByProductId(String productId, String mgmtDistrict) throws Exception{
        IData data = new DataMap();
        data.put("OFFER_CODE", productId);
        data.put("OFFER_TYPE", "P");
        data.put("MGMT_DISTRICT", mgmtDistrict);
        
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferGroupRelOfferId", data);
        IDataset dataset = result.getDataset("OUTDATA");
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            for(int i = 0, isize = dataset.size(); i < isize; i++ )
            {
                IData packageInfo = dataset.getData(i);
                
                packageInfo.put("PRODUCT_ID", productId);// 12位的需要转换成短的
                packageInfo.put("PACKAGE_ID", packageInfo.getString("GROUP_ID"));
                packageInfo.put("PACKAGE_NAME", packageInfo.getString("GROUP_NAME"));
                packageInfo.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                packageInfo.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                packageInfo.put("PACKAGE_TYPE_CODE", packageInfo.getString("GROUP_TYPE"));
                packageInfo.put("LIMIT_TYPE", "");
                packageInfo.put("MIN_NUMBER", packageInfo.getString("MIN_NUM"));
                packageInfo.put("MAX_NUMBER", packageInfo.getString("MAX_NUM"));
                packageInfo.put("RSRV_STR1", "");
            }
        }
        
        return dataset;
    }
	
	
	public static IDataset queryOfferJoinRelOfferByOfferId(String offerId, String offerType, String relType, String selectFlag) throws Exception
    {
        IData data = new DataMap();
        data.put("OFFER_CODE", offerId);
        data.put("OFFER_TYPE", offerType);
        if (StringUtils.isNotEmpty(relType)) {
        	data.put("REL_TYPE", relType);
		}
		if (StringUtils.isNotEmpty(selectFlag)) {
			data.put("SELECT_FLAG", selectFlag);
		}
                                 
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelAndOfferByOfferId", data);
        IDataset dataset = result.getDataset("OUTDATA");
        
        return dataset;
    }
	
	public static IDataset queryOfferEnableMode(String offerId, String groupId, String elementId, String elementType)throws Exception
	{
		IData input = new DataMap();
		
		IDataset resultset = new DatasetList();
		
		input.put("OFFER_ID", offerId);
		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE_A", elementType);
		input.put("OFFER_CODE_A", elementId);
		
		IData result = call("UPC.Out.OfferQueryFSV.qryEnableModeByGroupIdOfferIdWithOfferIdA", input);
		
		IData data = result.getData("OUTDATA");
	
		if (IDataUtil.isNotEmpty(data))
			resultset.add(data);
	
		return resultset;
    }
	
	
	public static IDataset qryOfferByOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType, String queryCha) throws Exception {
		IData input = new DataMap();

		input.put("OFFER_CODE", offerCode);
		input.put("OFFER_TYPE", offerType);
		input.put("REL_OFFER_CODE", relOfferCode);
		input.put("REL_OFFER_TYPE", relOfferType);
		if (StringUtils.isNotEmpty(queryCha)) {
			input.put("QUERY_CHA", queryCha);
		}
		IData result = call("UPC.Out.OfferQueryFSV.qryOfferByOfferIdRelOfferId", input);
		IData data = result.getData("OUTDATA");

		if (IDataUtil.isEmpty(data)) {
			return null;
		}
		IDataset results = new DatasetList();
		results.add(data);

		return results;
	}
	
	public static IData queryElementEnableMode( String offerType, String offerCode, String groupId, String elementId, String elementType) throws Exception
    {
    	IData input = new DataMap();

		input.put("OFFER_TYPE", offerType);
		input.put("OFFER_CODE", offerCode);
		input.put("GROUP_ID", groupId);
		input.put("OFFER_TYPE_A", elementType);
		input.put("OFFER_CODE_A", elementId);
        
        IData result = call("UPC.Out.OfferQueryFSV.qryEnableModeByGroupIdOfferIdWithOfferIdA",input);
        
        IData data = result.getData("OUTDATA");
        
        return data;
    } 
	
}