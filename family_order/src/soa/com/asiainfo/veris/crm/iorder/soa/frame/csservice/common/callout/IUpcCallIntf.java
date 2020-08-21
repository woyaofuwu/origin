package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.callout;

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
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

/**
 * 集客订单中心使用
 * @author sungq3
 *
 */
public class IUpcCallIntf
{
    /**
     * *特征相关 *根据商品ID与FILED_NAME查询商品的销售属性及值
     * 
     * @param offerId
     *            必填
     * @param attrObj
     *            必填
     * @param fieldName
     *            必填
     * @throws Exception
     */
    public static IDataset queryOfferChaAndValByCond(String offerId, String attrObj, String fieldName, String mgmtDistrictId) throws Exception
    {
        IData input = new DataMap();

        input.put("OFFER_ID", offerId);
        input.put("ATTR_OBJ", attrObj);
        input.put("FIELD_NAME", fieldName);
        input.put("MGMT_DISTRICT", mgmtDistrictId);
        IData result = call("UPC.Out.ChaQueryFSV.queryOfferChaAndValByCond", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
    
    public static IDataset queryOfferJoinRelByOfferId(String offerId, String relType, String selectFlag) throws Exception
    {
        return queryOfferJoinRelByOfferId(offerId, relType, selectFlag, null);
    }
    
    /**
     * *商品相关接口 *根据商品ID查询商品关联关系
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relType
     *            必填
     * @throws Exception
     */
    public static IDataset queryOfferJoinRelByOfferId(String offerId, String relType, String selectFlag, String eparchyCode) throws Exception
    {
        IData input = new DataMap();

        input.put("OFFER_ID", offerId);
        input.put("REL_TYPE", relType);
        input.put("SELECT_FLAG", selectFlag);
        input.put("MGMT_DISTRICT", eparchyCode);
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferJoinRelAndOfferByOfferId", input);
        IDataset dataset = result.getDataset("OUTDATA");

        return dataset;
    }
    
    private final static IData call(String svcName, IData input) throws Exception
    {
        
        return call(svcName, input, null, true);
    }

    private final static IData call(String svcName, IData input, Pagination pagin, boolean iscatch) throws Exception
    {
        String offerCode = input.getString("OFFER_CODE");
        if(StringUtils.isNotEmpty(offerCode) && "-1".equals(offerCode)) 
        {    IData result= new DataMap();
            IDataset set=new DatasetList();
            result.put("OUTDATA", set);
            return result;
        }
        
        ServiceResponse response = BizServiceFactory.call(svcName, input, pagin);
        // String url = "http://127.0.0.1:8080/order/service";
        // // String url = "http://10.200.179.136:10001/service";
        // ServiceRequest request = new ServiceRequest();
        // request.setData(input);
        // ServiceResponse response = BizServiceFactory.call(url, svcName, request, pagin, iscatch, false, 6000000, 60000);
        IData out = response.getBody();
        if (pagin != null)
            out.put("X_COUNTNUM", response.getDataCount());

        return out;

    }

    //新增方法处理报错问题
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

    public static IDataset queryOfferGroups(String offerId,String mgmtDistrict) throws Exception{
        IData data = new DataMap();
        data.put("OFFER_ID", offerId);
        data.put("MGMT_DISTRICT", mgmtDistrict);
        IData result = call("UPC.Out.OfferQueryFSV.queryOfferGroupRelOfferId", data);
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


}
