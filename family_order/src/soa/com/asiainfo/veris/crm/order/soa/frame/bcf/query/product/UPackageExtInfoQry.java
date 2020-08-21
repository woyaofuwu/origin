package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UPackageExtInfoQry
{
    /** 查询营销包的扩展属性 */
    public static IData queryPkgExtInfoByPackageId(String packageId) throws Exception
    {
        IData data = new DataMap();
        IDataset chas = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        if(IDataUtil.isNotEmpty(chas))
        {
            data.put("PACKAGE_ID", packageId);
            data.put("PACKAGE_NAME", UPackageInfoQry.getPackageNameByPackageId(packageId));
            data.putAll(chas.getData(0));
        }
        
        return data;
    }
    
    /** 查询营销包的生失效属性 */
    public static IData queryPkgEnableByPackageId(String packageId) throws Exception
    {
        IData data = new DataMap();
        
        IDataset offerEnable = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId);
        if(IDataUtil.isNotEmpty(offerEnable))
        {
            data.put("PACKAGE_ID", packageId);
            data.put("PACKAGE_NAME", UPackageInfoQry.getPackageNameByPackageId(packageId));
            data.putAll(offerEnable.getData(0));
        }
        
        return data;
    }
    
    public static IData qryPkgExtEnableByPackageId(String packageId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("PACKAGE_NAME", UPackageInfoQry.getPackageNameByPackageId(packageId));
        
        IDataset offerEnable = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId);
        if(IDataUtil.isNotEmpty(offerEnable))
        {
            data.put("ENABLE_TAG", offerEnable.getData(0).getString("ENABLE_MODE"));
            data.put("START_ABSOLUTE_DATE", offerEnable.getData(0).getString("ABSOLUTE_ENABLE_DATE"));
            data.put("START_OFFSET", offerEnable.getData(0).getString("ENABLE_OFFSET"));
            data.put("START_UNIT", offerEnable.getData(0).getString("ENABLE_UNIT"));
            data.put("END_ENABLE_TAG", offerEnable.getData(0).getString("DISABLE_MODE"));
            data.put("END_ABSOLUTE_DATE", offerEnable.getData(0).getString("ABSOLUTE_DISABLE_DATE"));
            data.put("END_OFFSET", offerEnable.getData(0).getString("DISABLE_OFFSET"));
            data.put("END_UNIT", offerEnable.getData(0).getString("DISABLE_UNIT"));
            data.put("CANCEL_TAG", offerEnable.getData(0).getString("CANCEL_MODE"));
            
            data.putAll(offerEnable.getData(0));
        }
        
        IDataset chas = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        if(IDataUtil.isNotEmpty(chas))
        {
            data.putAll(chas.getData(0));
        }
        
        return data;
    }
    
    /** 查询包扩展属性 */
    public static IDataset queryPackageExtByPackageId(String from_table_name, String packageId) throws Exception
    {
        
        IDataset offerEnable = UpcCall.queryTempChasByChaIdTableName(from_table_name,BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId);

        if(IDataUtil.isNotEmpty(offerEnable))
        {
            for(int i = 0, isize = offerEnable.size(); i < isize; i++ )
            {
                IData temp = offerEnable.getData(i);
                temp.put("END_OFFSET", temp.getString("DISABLE_OFFSET"));
            }
        } 
        
        return offerEnable;
    }
}
