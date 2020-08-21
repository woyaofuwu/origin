package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 
 * @ClassName: FillUserElementInfoUtil  
 * @Description: 补齐用户元素信息工具类
 * @author penghb penghb@asiainfo.com  
 * @date 2017年4月8日 上午11:02:48  
 *
 */
public class FillUserElementInfoUtil
{
    public static void fillUserElementProductIdAndPackageId(IDataset elementList, IData paramData, String flag) throws Exception
    {
        if (IDataUtil.isEmpty(elementList))
        {
            return;
        }
        if (paramData == null)
        {
            paramData = new DataMap();
        }
        
        IData tempOfferRel = new DataMap();
        for (int i = 0; i < elementList.size(); i++)
        {
            IData element = elementList.getData(i);
            String userId = element.getString("USER_ID","");
            
            if (StringUtils.isBlank(flag))
            {
                IDataset offerRels = null;
                if(StringUtils.equals(userId, "")){
                    offerRels = UserOfferRelInfoQry.queryUserOfferRelInfosByRelOfferInstId(element.getString("INST_ID"));
                }
                else
                {
                    IDataset userOfferRels = tempOfferRel.getDataset(userId);
                    if(IDataUtil.isEmpty(userOfferRels))
                    {
                        userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId,"0898");
                        tempOfferRel.put(userId, userOfferRels);
                    }
                    
                    offerRels = DataHelper.filter(userOfferRels, "REL_OFFER_INS_ID="+element.getString("INST_ID"));
                }
                
                
                if (IDataUtil.isEmpty(offerRels))
                {
                    //compatibleFill(paramData, element);
                    continue;
                }
                
                IData offerRel = null;
                if(offerRels.size() > 1)//大于1的情况只会全是P，取最大开始时间的P
                {
                	IDataset mainOfferRels = findMPMaxStartDate(offerRels);//取最大开始时间主产品
                	if(IDataUtil.isEmpty(mainOfferRels))//存在取不到主产品的数据，可能是集团成员产品
                	{
                		for (int j = 0; j < offerRels.size(); j++) {
                			IData offerRelj = offerRels.getData(j);
                			String offerType = offerRelj.getString("OFFER_TYPE");
                			if ("P".equals(offerType)) {
                				offerRel = offerRelj;
                				break;
							}
						}
                		
                		if(IDataUtil.isEmpty(offerRel)) offerRel = offerRels.getData(0);
                	}else
                	{
                		offerRel = mainOfferRels.getData(0);
                	}
                }else
                {
                	offerRel = offerRels.getData(0);
                }
                
                if(IDataUtil.isNotEmpty(offerRel))
                {
	                String offerType = offerRel.getString("OFFER_TYPE");
	                if(StringUtils.equals("P", offerType))
	                {
	                    element.put("PRODUCT_ID", offerRel.getString("OFFER_CODE"));
	                    element.put("PACKAGE_ID", offerRel.getString("GROUP_ID"));
	                    continue;
	                }
	                else if(StringUtils.equals("K", offerType))
	                {
	                    String offerInsId = offerRel.getString("OFFER_INS_ID");
	                    IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByInstId(offerInsId);
	                    if(IDataUtil.isEmpty(userSaleActiveInfos))
	                    {
	                    	element.put("PACKAGE_ID", "-1");
		                    element.put("PRODUCT_ID", "-1");
	                    }else
	                    {
		                    String packageId = offerRel.getString("OFFER_CODE");
		                    element.put("PACKAGE_ID", packageId);
		                    element.put("PRODUCT_ID", userSaleActiveInfos.getData(0).getString("PRODUCT_ID"));
		                    //集团营销活动受理特殊处理
		                    String campnType = userSaleActiveInfos.getData(0).getString("CAMPN_TYPE");
		                    if(StringUtils.isNotBlank(campnType) && (StringUtils.equals("01GPYX01", campnType) ||
		                    		StringUtils.equals("01GPYX02", campnType) || StringUtils.equals("01GPYX03", campnType) ||
		                    		StringUtils.equals("01GPYX04", campnType) ))
		                    {
		                    	element.put("PRODUCT_ID", "");
		                    }
	                    }
	                    continue;
	                }else
	                {
	                	element.put("PACKAGE_ID", "-1");
	                    element.put("PRODUCT_ID", "-1");
	                }
                }else
                {
                	element.put("PACKAGE_ID", "-1");
                    element.put("PRODUCT_ID", "-1");
                }
            }
            else
            {
                // 其它特殊处理暂时不管
            }
            
            //compatibleFill(paramData, element);
        }
    }

    /**
     * 
     * @Title: compatibleFill  
     * @Description: 兼容性填充
     * @param @param paramData
     * @param @param element
     * @param @throws Exception    设定文件  
     * @return void    返回类型  
     * @throws
     */
    private static void compatibleFill(IData paramData, IData element) throws Exception
    {
        if (StringUtils.isEmpty(element.getString("PRODUCT_ID")) || StringUtils.equals("-1", element.getString("PRODUCT_ID")))
        {
            String productId = paramData.getString("PRODUCT_ID");
            IDataset userProductList = null;
            if (StringUtils.isNotBlank(productId))
            {
                userProductList = UserProductInfoQry.getUserProductByUserIdProductId(element.getString("USER_ID"), productId);
            }
            
            if (IDataUtil.isEmpty(userProductList))
            {
                if (StringUtils.isBlank(element.getString("USER_ID_A")) && StringUtils.isNotBlank(paramData.getString("USER_ID_A")))
                {
                    element.put("USER_ID_A", paramData.getString("USER_ID_A"));
                }
                userProductList = UserProductInfoQry.getProductInfo(element.getString("USER_ID"), element.getString("USER_ID_A"));
            }
            // 查产品资料表补齐
            userProductList = IDataUtil.removeFilter(userProductList, "PRODUCT_ID=11");// 集团附加产品
            userProductList = IDataUtil.removeFilter(userProductList, "PRODUCT_ID=13");// 集团成员附加产品
            if (IDataUtil.isNotEmpty(userProductList))
            {
                productId = userProductList.getData(0).getString("PRODUCT_ID");
                element.put("PRODUCT_ID", productId);
            }

        }
        
        if (StringUtils.isEmpty(element.getString("PACKAGE_ID")) || StringUtils.equals("-1", element.getString("PACKAGE_ID")))
        {
            String elementId = element.getString("ELEMENT_ID");
            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
            if (StringUtils.isBlank(elementId))
            {
                if (element.containsKey("DISCNT_CODE") && StringUtils.isNotBlank(element.getString("DISCNT_CODE")))
                {
                    elementId = element.getString("DISCNT_CODE");
                    elementTypeCode = BofConst.ELEMENT_TYPE_CODE_DISCNT;
                }
                else if (element.containsKey("SERVICE_ID") && StringUtils.isNotBlank(element.getString("SERVICE_ID"))) 
                {
                    elementId = element.getString("SERVICE_ID");
                    elementTypeCode = BofConst.ELEMENT_TYPE_CODE_SVC;
                }
            }
            if (StringUtils.isNotBlank(elementId) && StringUtils.isNotBlank(elementTypeCode))
            {
                IData elementCfg = ProductElementsCache.getElement(element.getString("PRODUCT_ID"), elementId, elementTypeCode);
                if (IDataUtil.isNotEmpty(elementCfg))
                {
                    element.put("PACKAGE_ID", elementCfg.getString("GROUP_ID","-1"));
                }
            }
        }
    }
    
    /**
     * 
     * @Title: filterUserElementsByProductIdPackageId  
     * @Description: 根据productId和packageId过滤用户元素信息
     * @param @param userElements
     * @param @param productId
     * @param @param packageId
     * @param @throws Exception    设定文件  
     * @return IDataset    返回类型  
     * @throws
     */
    public static IDataset filterUserElementsByProductIdPackageId(IDataset userElements, String productId, String packageId) throws Exception
    {
        if (IDataUtil.isEmpty(userElements))
        {
            return userElements;
        }
        // 传了productId或者packageId 在过滤下
        if (StringUtils.isNotBlank(productId) && StringUtils.isBlank(packageId))
        {
            userElements = DataHelper.filter(userElements, "PRODUCT_ID="+productId);
        }
        else if (StringUtils.isNotBlank(productId) && StringUtils.isNotBlank(packageId))
        {
            userElements = DataHelper.filter(userElements, "PRODUCT_ID="+productId+",PACKAGE_ID="+packageId);
        }
        else if (StringUtils.isBlank(productId) && StringUtils.isNotBlank(packageId))
        {
            userElements = DataHelper.filter(userElements, "PACKAGE_ID="+packageId);
        }
        
        return userElements;
    }
    
	private static IDataset findMPMaxStartDate(IDataset offerRels) throws Exception
	{
		if(ArrayUtil.isEmpty(offerRels)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : offerRels){
			IData offerRel = (IData)obj;
			String offerType = offerRel.getString("OFFER_TYPE");
			if(!BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType)){
				continue;
			}
			
			String offerCode = offerRel.getString("OFFER_CODE");
			String startDate = offerRel.getString("START_DATE");
			OfferCfg offerCfg = OfferCfg.getInstance(offerCode, offerType);
			boolean isMain = offerCfg.isMain();
			if(!isMain){
				continue;
			}
			String relOfferInsId = offerRel.getString("REL_OFFER_INS_ID");
			boolean isFind = false;
			for(Object object : offerRels){
				IData tempRel = (IData)object;
				String tempOfferCode = tempRel.getString("OFFER_CODE");
				String tempOfferType = tempRel.getString("OFFER_TYPE");
				String tempRelOfferInsId = tempRel.getString("REL_OFFER_INS_ID");
				if(!BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(tempOfferType)){
					continue;
				}
				if(!tempRelOfferInsId.equals(relOfferInsId)){
					continue;
				}
				if(tempRelOfferInsId.equals(relOfferInsId) && tempOfferCode.equals(offerCode) && tempOfferType.equals(offerType)){
					//完全相同
					continue;
				}
				String tempStartDate = tempRel.getString("START_DATE");
				if(SysDateMgr.compareTo(tempStartDate, startDate) < 0){
					continue;
				}
				OfferCfg tempOfferCfg = OfferCfg.getInstance(tempOfferCode, tempOfferType);
				boolean tempIsMain = tempOfferCfg.isMain();
				if(!tempIsMain){
					continue;
				}
				isFind = true;
				break;
			}
			if(!isFind){
				//表示是最大的那条
				rst.add(offerRel);
			}
		}
		return rst;
	}
}
