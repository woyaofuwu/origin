
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ProductUtils.java
 * @Description: 产品变更工具类
 * @version: v1.0.0
 * @author: maoke
 * @date: Sep 2, 2014 10:04:30 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Sep 2, 2014 maoke v1.0.0 修改原因
 */
public class ProductUtils
{
    protected static final Logger log = Logger.getLogger(ProductUtils.class);

    /**
     * @Description: 是否是预约变更 【是预约：true】
     * @param bookingDate
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 2, 2014 10:04:49 AM
     */
    public static boolean isBookingChange(String bookingDate) throws Exception
    {
        if (StringUtils.isNotBlank(bookingDate) && SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.getSysTime()) > 0)
        {
            return true;
        }

        return false;
    }

    /**
     * @Description: 是否预约产品变更【是预约产品变更：true】
     * @param bookingDate
     * @param userProductId
     * @param newProductId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 2, 2014 10:04:59 AM
     */
    public static boolean isBookingProductChange(String bookingDate, String userProductId, String newProductId) throws Exception
    {
        if (isBookingChange(bookingDate))
        {
            if (StringUtils.isNotBlank(userProductId) && StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))
            {
                return true;
            }
        }

        return false;
    }
    
    /**
     * 新的产商品模型装换成老Element
     * @param offerList
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset offerToElement(IDataset offerList, String productId) throws Exception
    {
        if (IDataUtil.isEmpty(offerList))
        {
            return offerList;
        }
        
        IData offer = null;
        IData element = null;
        
        IDataset elementList = new DatasetList();
        
        for (int i = 0; i < offerList.size(); i++)
        {
            offer = offerList.getData(i);
            
            element = new DataMap();
            
            element.put("PRODUCT_ID", productId);
            
            if (StringUtils.isNotBlank(offer.getString("GROUP_ID")))
            {
                element.put("PACKAGE_ID", offer.getString("GROUP_ID"));
            }
            else
            {
                element.put("PACKAGE_ID", "-1");
            }
            
            element.put("MAIN_TAG", offer.getString("IS_MAIN",""));
            element.put("ELEMENT_ID", offer.getString("OFFER_CODE",""));
            element.put("ELEMENT_NAME", offer.getString("OFFER_NAME",""));
            element.put("ELEMENT_EXPLAIN", offer.getString("DESCRIPTION",""));
            element.put("ELEMENT_TYPE_CODE", offer.getString("OFFER_TYPE",""));
            element.put("PACKAGE_FORCE_TAG", offer.getString("FORCE_TAG",""));
            element.put("FORCE_TAG", offer.getString("FORCE_TAG",""));
            element.put("PACKAGE_DEFAULT_TAG", offer.getString("DEFAULT_TAG",""));
            element.put("DEFAULT_TAG", offer.getString("DEFAULT_TAG",""));
            element.put("ELEMENT_FORCE_TAG", offer.getString("FORCE_TAG",""));
            element.put("ELEMENT_DEFAULT_TAG", offer.getString("DEFAULT_TAG",""));
            
            elementList.add(element);
        }
        
        return elementList;
    }
    
    /**
     * 给产商品元素填充包ID
     * @param offerList
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static void fillUserElementPackageId(IDataset elementList, String productId) throws Exception
    {
        if (IDataUtil.isEmpty(elementList))
        {
            return ;
        }

        IData element = null;
        
        for (int i = 0; i < elementList.size(); i++)
        {
            element = elementList.getData(i);
            
            if (StringUtils.isEmpty(element.getString("PRODUCT_ID","")) || "-1".equals(element.getString("PRODUCT_ID","")))
            {
                element.put("PRODUCT_ID", productId);
            }
            
            if (StringUtils.isEmpty(element.getString("PACKAGE_ID","")) || "-1".equals(element.getString("PACKAGE_ID","")))
            {
                IData elementCfg = ProductElementsCache.getElement(element.getString("PRODUCT_ID"), element.getString("ELEMENT_ID"), element.getString("ELEMENT_TYPE_CODE"));
                if (IDataUtil.isNotEmpty(elementCfg))
                {
                    element.put("PACKAGE_ID", elementCfg.getString("GROUP_ID","-1"));
                }
            }
        }
    }

    /**
	 * 获取产品变更的时间
	 * 
	 * @param oldProductId
	 * @param newProductId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getProductChangeDate(String oldProductId, String newProductId) throws Exception
	{
		String productChangeDate = null;
		IData productTran = UpcCall.queryOfferTransOffer(oldProductId, "P", newProductId, "P");
		if (productTran != null && productTran.size() > 0)
		{
			String enableTag = productTran.getString("ENABLE_MODE");

			if (StringUtils.equals("0", enableTag))// 立即生效
			{
				productChangeDate = SysDateMgr.getSysTime();
			}
			else if ((StringUtils.equals("1", enableTag)) || (StringUtils.equals("2", enableTag)))// 下帐期生效
			{
				productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
			}
			else if (StringUtils.equals("3", enableTag))// 按原产品的生效方效
			{
				IData productInfo = UProductInfoQry.qryProductByPK(oldProductId);
				String enableTagOld = productInfo.getString("ENABLE_TAG");

				if ((StringUtils.equals("0", enableTagOld)) || (StringUtils.equals("2", enableTagOld)))// 立即生效
				{
					productChangeDate = SysDateMgr.getSysTime();
				}
				else if (StringUtils.equals("1", enableTagOld))// 下帐期生效
				{
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
			else if (StringUtils.equals("4", enableTag))// 按新产品的生效方式
			{
				IData productInfo = UProductInfoQry.qryProductByPK(newProductId);// 里面ext接口会返回ENABLE_TAG
				String enableTagNew = productInfo.getString("ENABLE_TAG");
				if ((StringUtils.equals("0", enableTagNew)) || (StringUtils.equals("2", enableTagNew)))// 立即生效
				{
					productChangeDate = SysDateMgr.getSysTime();
				}
				else if (StringUtils.equals("1", enableTagNew))// 下帐期生效
				{
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
		}
		else
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_4);
		}
		return productChangeDate;
	}
}
