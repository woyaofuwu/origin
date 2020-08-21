
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UPackageInfoQry
{
    public static String getPackageNameByPackageId(String packageId) throws Exception
    {
        try
        {
            OfferCfg offercfg= OfferCfg.getInstance(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
            if (offercfg == null)
                return "";

            else
                return offercfg.getOfferName();
        }
        catch (Exception e)
        {
            return "";
        }
    }
    
    public static String getPackageExplainByPackageId(String packageId) throws Exception
    {
        try
        {
            OfferCfg offercfg= OfferCfg.getInstance(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
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
    
    public static IData getPackageByPK(String packageId) throws Exception
    {
        OfferCfg offercfg= OfferCfg.getInstance(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        if(offercfg == null)
            return null;
        
        IData pkgInfo = new DataMap();
        pkgInfo.put("PACKAGE_ID", offercfg.getOfferCode());
        pkgInfo.put("PACKAGE_NAME", offercfg.getOfferName());
        pkgInfo.put("DESCRIPTION", offercfg.getDescription());
        
        return pkgInfo;
    }
    
    
    public static String getNameByPackageId(String packageId) throws Exception
    {
        try
        {
            IDataset groups =  UpcCall.queryGroupByCond(packageId, "");
            if(IDataUtil.isNotEmpty(groups))
            {
                return groups.getData(0).getString("GROUP_NAME");
            }
            else 
            {
                return "";
            }
        }
        catch (Exception e)
        {
            return "";
        }
        
    }
    
    /**
     * 查询产品下的包列表
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getPackagesByProductId(String productId) throws Exception
    {
        IDataset packageInfos = UpcCall.queryOfferGroupRelOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
        IDataset result = new DatasetList();
        
        if(IDataUtil.isNotEmpty(packageInfos))
        {
            for(int i = 0, isize = packageInfos.size(); i < isize; i++ )
            {
                IData packageInfo = packageInfos.getData(i);
                
                IData temp = new DataMap();
                temp.put("PRODUCT_ID", productId);
                temp.put("PACKAGE_ID", packageInfo.getString("GROUP_ID"));
                temp.put("PACKAGE_NAME", packageInfo.getString("GROUP_NAME"));
                temp.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                temp.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                temp.put("PACKAGE_TYPE_CODE", packageInfo.getString("GROUP_TYPE"));
                temp.put("LIMIT_TYPE", "");
                temp.put("MIN_NUMBER", packageInfo.getString("MIN_NUM"));
                temp.put("MAX_NUMBER", packageInfo.getString("MAX_NUM"));
                temp.put("RSRV_STR1", "");
                
                result.add(temp);
            }
        }
        
        return result;
        
    }
    
    /**
     * 查询产品下的包列表
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData getPackageByProductIdAndPackageId(String productId, String packageId) throws Exception
    {
        IData result = new DataMap();
        if (StringUtils.equals("0", packageId) || StringUtils.equals("-1", packageId))
        {
            result.put("PRODUCT_ID", productId);
            result.put("PACKAGE_ID", packageId);
            result.put("PACKAGE_NAME", "");
            result.put("FORCE_TAG", StringUtils.equals("0", packageId) ? "1" : "0");
            result.put("DEFAULT_TAG", StringUtils.equals("0", packageId) ? "1" : "0");
            result.put("MIN_NUMBER", "-1");
            result.put("MAX_NUMBER", "-1");
            return result;
        }
        IDataset packageInfos = UpcCall.queryOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, packageId, "Y");
        
        if(IDataUtil.isNotEmpty(packageInfos))
        {
            IData packageInfo = packageInfos.getData(0);
            
            IData temp = new DataMap();
            temp.put("PRODUCT_ID", productId);
            temp.put("PACKAGE_ID", packageInfo.getString("GROUP_ID"));
            temp.put("PACKAGE_NAME", packageInfo.getString("GROUP_NAME"));
            temp.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
            temp.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
            temp.put("PACKAGE_TYPE_CODE", packageInfo.getString("GROUP_TYPE"));
            temp.put("LIMIT_TYPE", "");
            temp.put("MIN_NUMBER", packageInfo.getString("MIN_NUM"));
            temp.put("MAX_NUMBER", packageInfo.getString("MAX_NUM"));
            temp.put("RSRV_STR1", packageInfo.getString("RSRV_STR1"));
            temp.put("PROD_PACK_REL_STR1", packageInfo.getString("RSRV_STR1"));
            
            result.putAll(temp);
        }
        
        return result;
        
    }
    
    public static IData getPackageLimitByPackageId(String packageId) throws Exception
    {
        IData result = getPackageByPK(packageId);
        
        IDataset offerGroupRelList = UpcCall.queryOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, packageId);
        if(IDataUtil.isNotEmpty(offerGroupRelList))
        {
            IData offerGroupRelData = offerGroupRelList.getData(0);
            result.put("LIMIT_TYPE", offerGroupRelData.getString("LIMIT_TYPE"));
            result.put("MIN_NUMBER", offerGroupRelData.getString("MIN_NUM"));
            result.put("MAX_NUMBER", offerGroupRelData.getString("MAX_NUM"));
        }else
        {
            result.put("LIMIT_TYPE", "");
            result.put("MIN_NUMBER", "-1");
            result.put("MAX_NUMBER", "-1");
        }
        
        return result;
    }
    
    /**
     * 查询产品下的必选包列表
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getForcePackagesByProductId(String productId) throws Exception
    {
        IDataset packageInfos = UpcCall.queryOfferGroupRelOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
        IDataset result = new DatasetList();
        
        if(IDataUtil.isNotEmpty(packageInfos))
        {
            for(int i = 0, isize = packageInfos.size(); i < isize; i++ )
            {
                IData packageInfo = packageInfos.getData(i);
                
                IData temp = new DataMap();
                temp.put("PRODUCT_ID", productId);
                temp.put("PACKAGE_ID", packageInfo.getString("GROUP_ID"));
                temp.put("PACKAGE_NAME", packageInfo.getString("GROUP_NAME"));
                temp.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                temp.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                temp.put("PACKAGE_TYPE_CODE", packageInfo.getString("GROUP_TYPE"));
                temp.put("LIMIT_TYPE", "");
                temp.put("MIN_NUMBER", packageInfo.getString("MIN_NUM"));
                temp.put("MAX_NUMBER", packageInfo.getString("MAX_NUM"));
                temp.put("RSRV_STR1", "");
                
                result.add(temp);
            }
        }
        
        result = DataHelper.filter(result, "FORCE_TAG=1");
        
        return result;
        
    }
  
}
