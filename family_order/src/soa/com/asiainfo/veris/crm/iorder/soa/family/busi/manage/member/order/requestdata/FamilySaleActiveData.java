
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class FamilySaleActiveData extends ProductModuleData
{
    // 营销活动OFFER_ID
    private String saleOfferId;

    // 营销活动大类
    private String campnType;

    // 营销活动产品ID
    private String saleProductId;

    // 营销包ID
    private String salePackageId;

    // 新增，修改，删除，标记
    private String modifyTag;

    // 生效时间
    private String startDate;

    // 失效时间
    private String endDate;

    // 有序实物
    private IDataset saleGoodsList;

    // 实物费用列表
    private IDataset saleGoodsFeeList;

    public FamilySaleActiveData(IData data) throws Exception
    {
        String offerCode = data.getString("ELEMENT_ID");// 营销活动包ID
        String offerType = data.getString("ELEMENT_TYPE_CODE");// K
        String productId = data.getString("PRODUCT_ID");// 融合产品ID
        String packageId = data.getString("PACKATGE_ID");// 角色GROUP_ID
        String modifyTag = data.getString("MODIFY_TAG");
        IDataset saleGoods = data.getDataset("GOODS_LIST"); // 实物列表
        IDataset goodsFeeList = data.getDataset("FEE_LIST"); // 实物费用列表

        IData offer = UpcCall.queryOfferByOfferId(offerType, offerCode);

        if (IDataUtil.isEmpty(offer))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_9, offerCode);
        }

        IDataset saleProducts = UpcCall.qryCatalogByOfferId(offerCode, offerType);

        if (IDataUtil.isEmpty(saleProducts))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_8);
        }

        setProductId(productId);
        setPackageId(packageId);
        setModifyTag(modifyTag);
        setSalePackageId(offerCode);
        setSaleOfferId(offer.getString("OFFER_ID", ""));
        setSaleProductId(saleProducts.getData(0).getString("CATALOG_ID", ""));
        setCampnType(saleProducts.getData(0).getString("UP_CATALOG_ID", ""));
        setSaleGoodsList(saleGoods);
        setSaleGoodsFeeList(goodsFeeList);

        String startDate = data.getString("START_DATE");
        String endDate = data.getString("END_DATE");

        // ----------------------------------------------
        // 如果对于营销活动没有传生效时间则根据配置进行计算
        // ----------------------------------------------
        if (StringUtils.isEmpty(startDate))
        {
            // -----------------------------------------------------------------------------------------------
            // 开始时间计算分为两种：
            // 一种是取营销活动自身生效方式进行计算。
            // 一种是取融合关系配置的生效方式进行计算。
            // 具体使用哪一种可以用下面字段进行控制。
            // -----------------------------------------------------------------------------------------------
            String basicCalStartDate = data.getString("BASIC_CAL_START_DATE", "");

            // 营销活动取活动本身的生效失效配置计算时间
            IDataset saleEnableMode = UpcCall.queryEnableModeRelByOfferId(offerType, offerCode);

            if (IDataUtil.isEmpty(saleEnableMode))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_10, offer.getString("OFFER_NAME"));
            }

            String enableTag = saleEnableMode.getData(0).getString("ENABLE_TAG");
            String startAbsDate = saleEnableMode.getData(0).getString("START_ABSOLUTE_DATE");
            String startOffset = saleEnableMode.getData(0).getString("START_OFFSET");
            String startUnit = saleEnableMode.getData(0).getString("START_UNIT");

            String endEnableTag = saleEnableMode.getData(0).getString("END_ENABLE_TAG");
            String endAbsDate = saleEnableMode.getData(0).getString("END_ABSOLUTE_DATE");
            String endOffset = saleEnableMode.getData(0).getString("END_OFFSET");
            String endUnit = saleEnableMode.getData(0).getString("END_UNIT");

            startDate = SysDateMgr.startDate(enableTag, startAbsDate, startOffset, startUnit);
            // 这里因为必选活动也走这里，所以判断下
            if (StringUtils.isNotEmpty(basicCalStartDate))
            {
                // 如果业务要求，活动的开始时间偏移配置取关系上的配置，不取活动自身配置。那么这样处理
                IData enableMode = UPackageElementInfoQry.queryElementEnableMode(productId, packageId, offerCode, offerType);
                if (IDataUtil.isNotEmpty(enableMode))
                {
                    enableTag = enableMode.getString("ENABLE_TAG");
                    startAbsDate = enableMode.getString("START_ABSOLUTE_DATE");
                    startOffset = enableMode.getString("START_OFFSET");
                    startUnit = enableMode.getString("START_UNIT");
                }
                startDate = SysDateMgr.startDateBook(basicCalStartDate, enableTag, startAbsDate, startOffset, startUnit);
            }
            // ---------------------------------------------
            // 如果结束时间没有传，则根据配置进行计算。
            // ---------------------------------------------
            if (StringUtils.isEmpty(endDate))
            {
                endDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsDate, endOffset, endUnit);
            }
        }

        setStartDate(startDate);
        setEndDate(endDate);
    }

    public String getSaleOfferId()
    {
        return saleOfferId;
    }

    public void setSaleOfferId(String saleOfferId)
    {
        this.saleOfferId = saleOfferId;
    }

    public String getCampnType()
    {
        return campnType;
    }

    public void setCampnType(String campnType)
    {
        this.campnType = campnType;
    }

    public String getSaleProductId()
    {
        return saleProductId;
    }

    public void setSaleProductId(String saleProductId)
    {
        this.saleProductId = saleProductId;
    }

    public String getSalePackageId()
    {
        return salePackageId;
    }

    public void setSalePackageId(String salePackageId)
    {
        this.salePackageId = salePackageId;
    }

    @Override
    public String getModifyTag()
    {
        return modifyTag;
    }

    @Override
    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    @Override
    public String getStartDate()
    {
        return startDate;
    }

    @Override
    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    @Override
    public String getEndDate()
    {
        return endDate;
    }

    @Override
    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public IDataset getSaleGoodsList()
    {
        return saleGoodsList;
    }

    public void setSaleGoodsList(IDataset saleGoodsList)
    {
        this.saleGoodsList = saleGoodsList;
    }

    public IDataset getSaleGoodsFeeList()
    {
        return saleGoodsFeeList;
    }

    public void setSaleGoodsFeeList(IDataset saleGoodsFeeList)
    {
        this.saleGoodsFeeList = saleGoodsFeeList;
    }
}
