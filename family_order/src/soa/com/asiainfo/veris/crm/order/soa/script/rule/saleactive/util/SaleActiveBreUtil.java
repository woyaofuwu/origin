
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public final class SaleActiveBreUtil
{
    public static IDataset getActivesByProductId(IDataset actives, String productId) throws Exception
    {
        IDataset returnDataset = new DatasetList();

        for (int index = 0, size = actives.size(); index < size; index++)
        {
            IData active = actives.getData(index);
            String userActiveProductId = active.getString("PRODUCT_ID");
            if (!userActiveProductId.equals(productId))
                continue;
            returnDataset.add(active);
        }

        return returnDataset;
    }
    
    /**
     * @author yanwu
     * @param actives
     * @param productId
     * @param PackageId
     * @return 判断产品和包
     * @throws Exception
     */
    public static IDataset getActivesByProductIdPackageId(IDataset actives, 
    													    String productId, 
    													    String PackageId) throws Exception
    {
    	if( IDataUtil.isEmpty(actives) )  
    		return null;
    	
        IDataset returnDataset = new DatasetList();

        for (int index = 0, size = actives.size(); index < size; index++)
        {
            IData active = actives.getData(index);
            String userActiveProductId = active.getString("PRODUCT_ID");
            String userActivePackageId = active.getString("PACKAGE_ID");
            if ( !userActiveProductId.equals(productId) && !userActivePackageId.equals(PackageId) )
                continue;
            returnDataset.add(active);
        }

        return returnDataset;
    }
    
    /**
     * @author yanwu
     * @param IDatas
     * @param key
     * @param value
     * @return 判断包含返回下标，不包含返回-1;
     * @throws Exception
     */
    public static int getIDatasetByKey(IDataset IDatas, String key, String value) throws Exception
    {
    	if( IDataUtil.isEmpty(IDatas) )  
    		return -1;
        for (int index = 0, size = IDatas.size(); index < size; index++)
        {
            IData active = IDatas.getData(index);
            String IDvalue = active.getString(key);
            if ( IDvalue.equals(value) )
            	return index;
        }
        return -1;
    }
    
    /**
     * @author yanwu
     * @param actives
     * @param productId
     * @param PackageId
     * @return 判断产品和包,包含返回false，不包含返回true;
     * @throws Exception
     */
    public static boolean getActivesByProductIdPackageIdB(IDataset actives, 
    													   String productId, 
    													   String PackageId) throws Exception
    {
    	if( IDataUtil.isEmpty(actives) )  
    		return true;
    	
        for (int index = 0, size = actives.size(); index < size; index++)
        {
            IData active = actives.getData(index);
            String userActiveProductId = active.getString("PRODUCT_ID");
            String userActivePackageId = active.getString("PACKAGE_ID");
            if ( userActiveProductId.equals(productId) && userActivePackageId.equals(PackageId) )
            	return false;
        }

        return true;
    }

    public static int getIntervalMoths(String startDate, String endDate) throws Exception
    {
        String sysDate = SysDateMgr.getSysTime();

        return SysDateMgr.monthInterval(sysDate, endDate);
    }

    public static IData getMaxEndDateActiveFromUserSaleActive(IDataset userSaleActives)
    {
        String maxEndDate = "";
        int index = -1;
        for (int i = 0, size = userSaleActives.size(); i < size; i++)
        {
            IData userSaleActive = userSaleActives.getData(i);
            String endDate = userSaleActive.getString("END_DATE");
            if (endDate.compareTo(maxEndDate) > 0)
            {
                maxEndDate = endDate;
                index = i;
            }
        }
        return userSaleActives.getData(index);
    }

    public static IData getMaxStartDateActiveFromUserSaleActive(IDataset userSaleActives)
    {
        String maxStartDate = "";
        int index = -1;
        for (int i = 0, size = userSaleActives.size(); i < size; i++)
        {
            IData userSaleActive = userSaleActives.getData(i);
            String startDate = userSaleActive.getString("START_DATE");
            if (startDate.compareTo(maxStartDate) > 0)
            {
                maxStartDate = startDate;
                index = i;
            }
        }
        return userSaleActives.getData(index);
    }

    public static IDataset getNoQyyxActives(IDataset actives) throws Exception
    {
        IDataset returnDataset = new DatasetList();
        for (int index = 0, size = actives.size(); index < size; index++)
        {
            IData active = actives.getData(index);
            String campnType = active.getString("CAMPN_TYPE");
            if (SaleActiveUtil.isQyyx(campnType))
                continue;
            returnDataset.add(active);
        }
        return returnDataset;
    }

    public static IDataset getQyyxActives(IDataset actives) throws Exception
    {
        IDataset returnDataset = new DatasetList();
        for (int index = 0, size = actives.size(); index < size; index++)
        {
            IData active = actives.getData(index);
            String campnType = active.getString("CAMPN_TYPE");
            if (!SaleActiveUtil.isQyyx(campnType))
                continue;
            returnDataset.add(active);
        }
        return returnDataset;
    }
    
    public static List<SaleActiveTradeData> getQyyxActives(List<SaleActiveTradeData> actives) throws Exception
    {
    	List<SaleActiveTradeData> returnTradeData = new ArrayList<SaleActiveTradeData>();
        for (SaleActiveTradeData saleActive : actives)
        {
            String campnType = saleActive.getCampnType();
            if (!SaleActiveUtil.isQyyx(campnType))
            {
                continue;
            }
            returnTradeData.add(saleActive);
        }
        return returnTradeData;
    }

    public static boolean is1593Active(String productId, String eparchyCode)throws Exception
    {
        IDataset commparaDataset = CommparaInfoQry.getCommPkInfo("CSM", "1593", productId, eparchyCode);

        if(IDataUtil.isNotEmpty(commparaDataset))
        {
        	return true;
        }
        
    	return false;
    }
    
    
    public static boolean isExists1593OtherActives(IDataset userActives, String productId, String eparchyCode) throws Exception
    {
        IDataset commparaDataset = CommparaInfoQry.getCommPkInfo("CSM", "1593", productId, eparchyCode);

        if (IDataUtil.isEmpty(commparaDataset) || CollectionUtils.isEmpty(userActives))
        {
            return false;
        }

        for (int index = 0, size = commparaDataset.size(); index < size; index++)
        {
            IData commparaData = commparaDataset.getData(index);
            String paraCode1 = commparaData.getString("PARA_CODE1");

            for (int j=0,s=userActives.size(); j<s; j++)
            {
            	IData saleActiveData = userActives.getData(j);
                String endProductId = saleActiveData.getString("PRODUCT_ID");

                if (!endProductId.equals(paraCode1))
                {
                    continue;
                }
                return true;
            }
        }

        return false;
    }
    
    public static boolean isExists1593Actives(List<SaleActiveTradeData> userActives, String productId, String eparchyCode) throws Exception
    {
        IDataset commparaDataset = CommparaInfoQry.getCommPkInfo("CSM", "1593", productId, eparchyCode);

        if (IDataUtil.isEmpty(commparaDataset) || CollectionUtils.isEmpty(userActives))
        {
            return false;
        }

        for (int index = 0, size = commparaDataset.size(); index < size; index++)
        {
            IData commparaData = commparaDataset.getData(index);
            String paraCode1 = commparaData.getString("PARA_CODE1");

            for (SaleActiveTradeData saleActiveTradeData : userActives)
            {
                String endPackageId = saleActiveTradeData.getPackageId();
                String endProductId = saleActiveTradeData.getProductId();

                if (!endProductId.equals(paraCode1) || ("60003192".equals(endPackageId) || "60003193".equals(endPackageId)))
                {
                    continue;
                }
                return true;
            }
        }

        return false;
    }

    public static String getEndDateByPid(String startDate, String endDate, String tradeTypeCode, String productId, String packageId) throws Exception
    {
        String newEndDate = endDate;
        IDataset paraInfos = CommparaInfoQry.getCommparaByCode1("CSM", "173", tradeTypeCode, productId, null);
        if(IDataUtil.isNotEmpty(paraInfos))
        {
            IDataset extInfos = UpcCall.queryEnableModeRelByOfferId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId);
            if(IDataUtil.isNotEmpty(extInfos))
            {
                String endEnableTag = extInfos.getData(0).getString("DISABLE_MODE");
                if(StringUtils.equals("0", endEnableTag))
                {
                    IDataset results = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", packageId, null);
                    if(IDataUtil.isNotEmpty(results))
                    {
                        String month = results.getData(0).getString("PARA_CODE4");
                        newEndDate = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(month), startDate);
                    }else
                    {
                        String month = "12";
                        newEndDate = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(month), startDate);
                    }
                    
                }
            }
        }
        return  newEndDate;
    }

    public static String getNewEndDateByPid(String startDate, String endDate, String tradeTypeCode, String productId, String packageId) throws Exception
    {
        String newEndDate = endDate;
        IDataset results = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", packageId, null);
        if(IDataUtil.isNotEmpty(results))
        {
            String month = results.getData(0).getString("PARA_CODE4");
            String newDate = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(month), startDate);
            int day = SysDateMgr.daysBetween(SysDateMgr.getSysDate(),newDate);
            if (day >= 0) {
                newEndDate = newDate;
            }else{
                newEndDate = "expired";
            }
        }
        return  newEndDate;
    }
    
    public static String getProductTagSet(String productId) throws Exception
    {
        if(StringUtils.isNotBlank(productId))
        {
            IData cha = UpcCall.queryTempChaByCond(productId, "TD_B_PRODUCT");
            return cha.getString("TAG_SET");
        }
        
        return null;
    }
    
    /**
     * 查询营销包下面所有默认必选元素费用
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getSaleAtiveFeeList(String productId, String packageId) throws Exception
    {
        IDataset tradeFeeDataset = new DatasetList();
        
        //查询下面所有
        IDataset forceAndDefaultElements = UPackageElementInfoQry.queryForceDefaultElementByPackageId(packageId, "1", "1");
        
        if (IDataUtil.isEmpty(forceAndDefaultElements))
        {
            return null;
        }
        
        for (int i = 0; i < forceAndDefaultElements.size(); i++)
        {
            IData forceAndDefaultElement = forceAndDefaultElements.getData(i);
            //查询营销活动费用配置
            IDataset businessFee = ProductFeeInfoQry.getSaleActiveFee("240", BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, forceAndDefaultElement.getString("ELEMENT_TYPE_CODE"), forceAndDefaultElement.getString("ELEMENT_ID"), productId);
            
            if (IDataUtil.isNotEmpty(businessFee))
            {
                tradeFeeDataset.addAll(businessFee);
            }
        }
        
        return tradeFeeDataset;
    }


    //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 start
    /**
     * 查询办理的155活动配置的9155活动
     * @param saleProductId 用户办理活动活动编码
     * @param salePackageId 用户办理活动的包编码
     * @param eparchyCode 所属地区编码
     * @return 返回结果集size可为0,为0代表未找到该155在9155中配置指定活动
     * @throws Exception
     */
    public static IDataset get155ActiveAssigned9155Config(String saleProductId,String salePackageId,String eparchyCode)throws Exception{
        IDataset assignednoBackConfigs=new DatasetList();
        List<String> noBackProductIds = new ArrayList<String>();
        List<String> noBackPackageIds = new ArrayList<String>();
        //查找非返类155配置的活动
        IDataset noBack155Configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", eparchyCode);
        if(IDataUtil.isEmpty(noBack155Configs)){
            return assignednoBackConfigs;
        }
        SaleActiveUtil.getCommparaProductIdAndPackageId(noBackProductIds, noBackPackageIds, noBack155Configs);
        //办理的活动若为155活动,查找9155中指定的顺延依赖活动
        if(noBackPackageIds.contains(salePackageId)){
            assignednoBackConfigs=CommparaInfoQry.getCommparaInfoBy5("CSM", "9155","1"
                    ,salePackageId,eparchyCode,null);
        }else if(noBackProductIds.contains(saleProductId)) {
            assignednoBackConfigs = CommparaInfoQry.getCommparaInfoBy5("CSM", "9155", "0"
                    , saleProductId, eparchyCode,null);
        }
        return assignednoBackConfigs;
    }

    /**
     * 查找用户已办理的活动中与正在办理的活动在9155配置有对应关系的活动
     * @param userActives 用户已办理的活动集
     * @param saleProductId 用户办理活动活动编码
     * @param salePackageId 用户办理活动的包编码
     * @param eparchyCode 所属地区编码
     * @return 返回结果集size可为0, 为0代表用户已办理的活动未找到9155配置对应关系的活动
     * @throws Exception
     */
    public static List<SaleActiveTradeData> getUserExists9155Actives(List<SaleActiveTradeData> userActives,
                  String saleProductId,String salePackageId,String eparchyCode) throws Exception{
        List<SaleActiveTradeData>  userExists9155Actives=new ArrayList<SaleActiveTradeData>();
        //用户没有已办理活动
        if(CollectionUtils.isEmpty(userActives)){
            return userExists9155Actives;
        }

        //用户正在办理活动无对应9155配置
        IDataset assigned9155Configs=get155ActiveAssigned9155Config(saleProductId,salePackageId,eparchyCode);
        if(IDataUtil.isEmpty(assigned9155Configs)) {
            return userExists9155Actives;
        }

        //查找用户已办理的活动中与正在办理的活动在9155配置有对应关系的活动
        for (int i = 0, isize = assigned9155Configs.size(); i < isize; i++){
            IData assignednoBackConfig = assigned9155Configs.getData(i);
            String assignedProductId=assignednoBackConfig.getString("PARA_CODE2","");
            String assignedPackageId=assignednoBackConfig.getString("PARA_CODE3","");
            for (int j=0,jsize=userActives.size(); j<jsize; j++){
                SaleActiveTradeData saleActiveData = userActives.get(j);
                String userSaleProductId = saleActiveData.getProductId();
                String userSalePackageId = saleActiveData.getPackageId();
                if(StringUtils.isNotBlank(assignedProductId)&&StringUtils.isNotBlank(assignedPackageId)){
                    //9155配置了指定活动和包
                    if(assignedProductId.equals(userSaleProductId)&&assignedPackageId.equals(userSalePackageId)){
                        userExists9155Actives.add(saleActiveData);
                    }
                }else if(StringUtils.isNotBlank(assignedProductId)){
                    //9155只配置指定活动
                    if(assignedProductId.equals(userSaleProductId)){
                        userExists9155Actives.add(saleActiveData);
                    }
                }
            }
        }
        return userExists9155Actives;
    }
    //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 end

}
