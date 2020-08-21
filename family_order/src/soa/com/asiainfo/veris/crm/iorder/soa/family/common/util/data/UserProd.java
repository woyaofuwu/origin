
package com.asiainfo.veris.crm.iorder.soa.family.common.util.data;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

/**
 * @Description 用户产品类
 * @Auther: zhenggang
 * @Date: 2020/7/24 15:56
 * @version: V1.0
 */
public class UserProd
{
    // 家庭用户生效家庭产品
    private String userFamilyProductId;

    // 生效家庭产品名称
    private String userFamilyProductName;

    // 生效家庭产品描述
    private String userFamilyProductDesc;

    // 预约家庭产品
    private String nextFamilyProductId;

    // 预约产品名称
    private String nextFamilyProductName;

    // 预约家庭产品名称
    private String nextFamilyProductDesc;

    // 用户生效产品
    private String userProductId;

    // 生效产品名称
    private String userProductName;

    // 生效产品描述
    private String userProductDesc;

    // 用户产品开始时间
    private String userProductStartDate;

    // 用户产品失效时间
    private String userProductEndDate;

    // 用户预约产品
    private String nextProductId;

    // 预约产品名称
    private String nextProductName;

    // 预约产品描述
    private String nextProductDesc;

    // 预约产品生效时间
    private String nextProductStartDate;

    // 预约产品失效时间
    private String nextProductEndDate;

    public String getUserFamilyProductId()
    {
        return userFamilyProductId;
    }

    public void setUserFamilyProductId(String userFamilyProductId) throws Exception
    {
        this.userFamilyProductId = userFamilyProductId;
        if (StringUtils.isNotEmpty(userFamilyProductId))
        {
            IDataset dataset = UpcCallIntf.queryOfferInfoByOfferCodeAndOfferType(userFamilyProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (IDataUtil.isNotEmpty(dataset))
            {
                this.setUserFamilyProductName(dataset.first().getString("OFFER_NAME"));
                this.setUserFamilyProductDesc(dataset.first().getString("DESCRIPTION"));
            }
        }
    }

    public String getUserFamilyProductName()
    {
        return userFamilyProductName;
    }

    private void setUserFamilyProductName(String userFamilyProductName)
    {
        this.userFamilyProductName = userFamilyProductName;
    }

    public String getUserFamilyProductDesc()
    {
        return userFamilyProductDesc;
    }

    private void setUserFamilyProductDesc(String userFamilyProductDesc)
    {
        this.userFamilyProductDesc = userFamilyProductDesc;
    }

    public String getNextFamilyProductId()
    {
        return nextFamilyProductId;
    }

    public void setNextFamilyProductId(String nextFamilyProductId) throws Exception
    {
        this.nextFamilyProductId = nextFamilyProductId;
        if (StringUtils.isNotEmpty(nextFamilyProductId))
        {
            IDataset dataset = UpcCallIntf.queryOfferInfoByOfferCodeAndOfferType(nextFamilyProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (IDataUtil.isNotEmpty(dataset))
            {
                this.setNextFamilyProductName(dataset.first().getString("OFFER_NAME"));
                this.setNextFamilyProductDesc(dataset.first().getString("DESCRIPTION"));
            }
        }
    }

    public String getNextFamilyProductName()
    {
        return nextFamilyProductName;
    }

    private void setNextFamilyProductName(String nextFamilyProductName)
    {
        this.nextFamilyProductName = nextFamilyProductName;
    }

    public String getNextFamilyProductDesc()
    {
        return nextFamilyProductDesc;
    }

    private void setNextFamilyProductDesc(String nextFamilyProductDesc)
    {
        this.nextFamilyProductDesc = nextFamilyProductDesc;
    }

    public String getUserProductId()
    {
        return userProductId;
    }

    public void setUserProductId(String userProductId) throws Exception
    {
        this.userProductId = userProductId;
        if (StringUtils.isNotEmpty(userProductId))
        {
            IDataset dataset = UpcCallIntf.queryOfferInfoByOfferCodeAndOfferType(userProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (IDataUtil.isNotEmpty(dataset))
            {
                this.setUserProductName(dataset.first().getString("OFFER_NAME"));
                this.setUserProductDesc(dataset.first().getString("DESCRIPTION"));
            }
        }
    }

    public String getUserProductName()
    {
        return userProductName;
    }

    private void setUserProductName(String userProductName)
    {
        this.userProductName = userProductName;
    }

    public String getNextProductId()
    {
        return nextProductId;
    }

    public void setNextProductId(String nextProductId) throws Exception
    {
        this.nextProductId = nextProductId;
        if (StringUtils.isNotEmpty(nextProductId))
        {
            IDataset dataset = UpcCallIntf.queryOfferInfoByOfferCodeAndOfferType(nextProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (IDataUtil.isNotEmpty(dataset))
            {
                this.setNextProductName(dataset.first().getString("OFFER_NAME"));
                this.setNextProductDesc(dataset.first().getString("DESCRIPTION"));
            }
        }
    }

    public String getNextProductName()
    {
        return nextProductName;
    }

    private void setNextProductName(String nextProductName)
    {
        this.nextProductName = nextProductName;
    }

    public String getNextProductStartDate()
    {
        return nextProductStartDate;
    }

    public void setNextProductStartDate(String nextProductStartDate)
    {
        this.nextProductStartDate = nextProductStartDate;
    }

    public String getUserProductStartDate()
    {
        return userProductStartDate;
    }

    public void setUserProductStartDate(String userProductStartDate)
    {
        this.userProductStartDate = userProductStartDate;
    }

    public String getUserProductEndDate()
    {
        return userProductEndDate;
    }

    public void setUserProductEndDate(String userProductEndDate)
    {
        this.userProductEndDate = userProductEndDate;
    }

    public String getNextProductEndDate()
    {
        return nextProductEndDate;
    }

    public void setNextProductEndDate(String nextProductEndDate)
    {
        this.nextProductEndDate = nextProductEndDate;
    }

    public String getUserProductDesc()
    {
        return userProductDesc;
    }

    public void setUserProductDesc(String userProductDesc)
    {
        this.userProductDesc = userProductDesc;
    }

    public String getNextProductDesc()
    {
        return nextProductDesc;
    }

    public void setNextProductDesc(String nextProductDesc)
    {
        this.nextProductDesc = nextProductDesc;
    }
}
