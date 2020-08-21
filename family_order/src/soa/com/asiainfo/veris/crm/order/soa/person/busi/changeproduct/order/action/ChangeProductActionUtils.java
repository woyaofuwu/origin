
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductActionUtils.java
 * @Description: 产品变更写Action时可公用的API
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 20, 2014 10:37:59 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 20, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductActionUtils
{
    /**
     * @Description: 根据ELEMENT_ID新增优惠台账
     * @param elementId
     * @param startDate
     * @param uca
     * @param btd
     * @throws Exception
     * @author: maoke
     * @date: Jun 28, 2014 4:24:50 PM
     */
    public static void addDiscntTradeByElementId(String elementId, String startDate, UcaData uca, BusiTradeData btd) throws Exception
    {
        IData productPkgData = ChangeProductActionUtils.getProductPackageId(uca, elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT);

        if (IDataUtil.isNotEmpty(productPkgData))
        {
            DiscntData discntData = new DiscntData();

            discntData.setElementId(elementId);
            discntData.setPackageId(productPkgData.getString("PACKAGE_ID"));
            discntData.setProductId(productPkgData.getString("PRODUCT_ID"));
            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            discntData.setPkgElementConfig(productPkgData.getString("PACKAGE_ID"));

            IData discntConfig = UDiscntInfoQry.getDiscntInfoByPk(elementId);
            if (IDataUtil.isEmpty(discntConfig))
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_140, elementId);
            }

            DiscntTradeData discntTradeData = new DiscntTradeData();

            discntTradeData.setUserId(uca.getUserId());
            discntTradeData.setElementId(discntData.getElementId());
            discntTradeData.setElementType(discntData.getElementType());
            discntTradeData.setCampnId(discntData.getCampnId());
            discntTradeData.setInstId(SeqMgr.getInstId());
            discntTradeData.setProductId(discntData.getProductId());
            discntTradeData.setPackageId(discntData.getPackageId());
            discntTradeData.setUserIdA("-1");
            discntTradeData.setRelationTypeCode("");
            discntTradeData.setSpecTag("0");
            discntTradeData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);
            discntTradeData.setEndDate(endDate.substring(0, 10) + SysDateMgr.getEndTime235959());
            discntTradeData.setRemark(discntData.getRemark());
            discntTradeData.setModifyTag(discntData.getModifyTag());

            btd.add(btd.getRD().getUca().getSerialNumber(), discntTradeData);
        }
    }

    /**
     * @Description: 根据service_id新增服务台账
     * @param elementId
     * @param startDate
     * @param uca
     * @param btd
     * @throws Exception
     * @author: maoke
     * @date: Jun 27, 2014 11:06:03 AM
     */
    public static void addSvcTradeByServiceId(String elementId, UcaData uca, BusiTradeData btd) throws Exception
    {
        IData productPkgData = ChangeProductActionUtils.getProductPackageId(uca, elementId, BofConst.ELEMENT_TYPE_CODE_SVC);

        if (IDataUtil.isNotEmpty(productPkgData))
        {
            SvcData svcData = new SvcData();

            svcData.setElementId(elementId);
            svcData.setElementType(BofConst.ELEMENT_TYPE_CODE_SVC);
            svcData.setPackageId(productPkgData.getString("PACKAGE_ID"));
            svcData.setProductId(productPkgData.getString("PRODUCT_ID"));
            svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            svcData.setPkgElementConfig();

            SvcTradeData svcTradeData = new SvcTradeData();

            svcTradeData.setUserId(uca.getUserId());
            svcTradeData.setElementId(svcData.getElementId());
            svcTradeData.setElementType(svcData.getElementType());
            svcTradeData.setCampnId(svcData.getCampnId());
            svcTradeData.setInstId(SeqMgr.getInstId());
            svcTradeData.setProductId(svcData.getProductId());
            svcTradeData.setPackageId(svcData.getPackageId());
            svcTradeData.setUserIdA("-1");
            svcTradeData.setMainTag(svcData.getMainTag());
            String startDate = ProductModuleCalDate.calStartDate(svcData, btd.getProductTimeEnv());
            svcTradeData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(svcData, startDate);
            svcTradeData.setEndDate(endDate.substring(0, 10) + SysDateMgr.getEndTime235959());
            svcTradeData.setRemark(svcData.getRemark());
            svcTradeData.setModifyTag(svcData.getModifyTag());

            btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData);
        }
    }

    /**
     * @Description: 循环找出元素对应的PRODUCT_ID、PACKAGE_ID
     * @param productElements
     * @param elementId
     * @param elementTypeCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 20, 2014 10:57:52 AM
     */
    public static IData getPackageElement(IDataset productElements, String elementId, String elementTypeCode) throws Exception
    {
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if (elementId.equals(element.getString("ELEMENT_ID")) && elementTypeCode.equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                return element;
            }
        }
        return null;
    }

    /**
     * @Description: 根据元素获取PRODUCT_ID、PACKAGE_ID 如不存在则报错【判定元素是否在此产品与包下面】
     * @param uca
     * @param elementId
     * @param elementTypeCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 20, 2014 11:32:52 AM
     */
    public static IData getProductPackageId(UcaData uca, String elementId, String elementTypeCode) throws Exception
    {
        ProductTradeData nextProduct = uca.getUserNextMainProduct();

        String userProductId = uca.getProductId();
        String userEparchyCode = uca.getUserEparchyCode();

        IDataset productElements = null;

        String nextProductId = null;
        if (nextProduct != null)
        {
            nextProductId = nextProduct.getProductId();
        }

        if (StringUtils.isNotBlank(nextProductId))
        {
            productElements = ProductElementsCache.getProductElements(nextProductId);
        }
        else
        {
            productElements = ProductElementsCache.getProductElements(userProductId);
        }

        if (IDataUtil.isNotEmpty(productElements))
        {
            IData productPkgData = getPackageElement(productElements, elementId, elementTypeCode);

            if (IDataUtil.isNotEmpty(productPkgData))
            {
                return productPkgData;
            }
            else
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_111, elementId);
            }
        }

        return null;
    }
}
