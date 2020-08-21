
package com.asiainfo.veris.crm.iorder.web.igroup.bboss.common;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public class FrontProdConverter 
{
    private static boolean isConverterProdId = false;
    /** 
    * @Title: prodConverter 
    * @Description: 产品id等属性转换
    * @param @param prodInfo PROD_SPEC_ID、OPER_TYPE(如果判断是否为bboss产品则需要加入MAIN_TAG)
    * @param @param judgment
    * @param @throws Exception  
    * @return void    
    * @author chenkh
    * @throws 
    */
    public static void prodConverter(IBizCommon bc,IData prodInfo, boolean judgment) throws Exception
    {
        isConverterProdId = false;
        if (prodInfo.getBoolean("IS_CONVERT"))
        {
            isConverterProdId = true;
        }
        if (judgment)
        {
            if (!isBbossProduct(prodInfo))
                return ;
        }
        
        String operType = prodInfo.getString("OPER_TYPE");
        // 1、集团受理转换
        if (EcConstants.FLOW_ID_EC_CREATE.equals(operType))
        {
            convertProdInfoForOpen(bc,prodInfo);
            return ;
        }
        
        // 2、集团 注销、变更转换
        if (EcConstants.FLOW_ID_EC_CHANGE.equals(operType))
        {
            convertProdInfoForChgAndDst(bc,prodInfo);
            return ;
        }
        if (EcConstants.FLOW_ID_EC_DELETE.equals(operType))
        {
            prodInfo.put("OPER_TYPE", "2");
            prodInfo.put("PROD_SPEC_ID",converProdId(bc,prodIdConvert(prodInfo.getString("PROD_SPEC_ID"))));
        }
        
        // 4、成员 新增转换
        if (EcConstants.FLOW_ID_MEMBER_CREATE.equals(operType))
        {
        	prodInfo.put("OPER_TYPE", "1");
            prodInfo.put("PROD_SPEC_ID",mebProdIdConvert(bc,prodIdConvert(prodInfo.getString("PROD_SPEC_ID"))));
        }
        // 4、成员 变更转换
        if (EcConstants.FLOW_ID_MEMBER_CHANGE.equals(operType))
        {
            prodInfo.put("OPER_TYPE", "6");
            prodInfo.put("PROD_SPEC_ID",mebProdIdConvert(bc,prodIdConvert(prodInfo.getString("PROD_SPEC_ID"))));
        }
        // 5、成员 注销转换
        if (EcConstants.FLOW_ID_MEMBER_DELETE.equals(operType))
        {
            prodInfo.put("OPER_TYPE", "1");
            prodInfo.put("PROD_SPEC_ID",mebProdIdConvert(bc,prodIdConvert(prodInfo.getString("PROD_SPEC_ID"))));
        }
    }

    private static void convertProdInfoForChgAndDst(IBizCommon bc,IData prodInfo) throws Exception
    {
        // 转换成产品操作编码
        String merchpOperType = convertOperType(prodInfo);
        
        prodInfo.put("OPER_TYPE", merchpOperType);
        prodInfo.put("PROD_SPEC_ID", converProdId(bc,prodIdConvert(prodInfo.getString("PROD_SPEC_ID"))));
    }

    private static boolean isBbossProduct(IData prodInfo) throws Exception
    {
        if (prodInfo.getBoolean("MainTag") && ("BOSG".equals(queryBrandByOfferId(prodInfo.getString("PROD_SPEC_ID")))))
            return true;
        if (!prodInfo.getBoolean("MainTag"))
        {
            String productId = prodIdConvert(prodInfo.getString("PROD_SPEC_ID"));
            if (StringUtils.isEmpty(productId))
            {
                return false;
            }
            if ("BOSG".equals(queryBrandByOfferId(productId)))
                return true;
        }
        return false;
    }
    
    private static String queryBrandByOfferId(String offerId) throws Exception
    {
        IData offerInfo = IUpcViewCall.queryOfferByOfferId(offerId);
        if(IDataUtil.isEmpty(offerInfo))
        {
            CSViewException.apperr(GrpException.CRM_GRP_428, offerId);
        }
        return offerInfo.getString("BRAND", "");
    }

    private static void convertProdInfoForOpen(IBizCommon bc,IData prodInfo) throws Exception
    {
        boolean isPre = EcBbossCommonViewUtil.isPreMerchpOffer(bc,prodIdConvert(prodInfo.getString("PROD_SPEC_ID")));
        
        // 预受理
        if (isPre)
        {
            prodInfo.put("OPER_TYPE", "10");
        }
        // 正式受理
        else {
            prodInfo.put("OPER_TYPE", "1");
        }
        
        prodInfo.put("PROD_SPEC_ID", converProdId(bc,prodIdConvert(prodInfo.getString("PROD_SPEC_ID"))));

    }
    
    public static String converProdId(IBizCommon bc,String prodId) throws Exception
    {
        String value = "";
        IDataset result = CommonViewCall.getAttrsFromAttrBiz(bc,"1","B","PRO",prodId);
        if (DataUtils.isNotEmpty(result)) 
        {
            value= result.getData(0).getString("ATTR_VALUE");
        }
        return value;
    }

    public static String prodIdConvert(String productId)
    {
        if (!isConverterProdId)
            return productId;
        int tempProd = Integer.parseInt(productId);
        tempProd -= 600000000;
        if (tempProd < 0 || tempProd == 940)
        {
            return "";
        }
        return String.valueOf(tempProd);
    }
    
    private static String mebProdIdConvert(IBizCommon bc,String productId) throws Exception
    {
        String offerId ="";
        String offerCode = "";
        IDataset result =IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null,IUpcViewCall.getOfferIdByOfferCode(productId),UpcConst.PM_OFFER_JOIN_REL_TYPE_ECMEB);
        if (DataUtils.isNotEmpty(result)) 
        {
            offerId = result.getData(0).getString("OFFER_ID");
            offerCode = IUpcViewCall.getOfferCodeByOfferId(offerId);
        }
        
        
        String value = converProdId(bc,offerCode);
        
        return "M".concat(value);
    }

    private static String convertOperType(IData prodInfo) throws Exception
    {
        String merchpOperType = prodInfo.getString("MERCHP_OPER_TYPE");
        
        return merchpOperType;
    }
}
