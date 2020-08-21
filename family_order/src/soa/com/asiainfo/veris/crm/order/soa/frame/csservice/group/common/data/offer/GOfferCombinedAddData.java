package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.org.apache.commons.lang3.ArrayUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IData;

public class GOfferCombinedAddData
{

    private GOfferAddData selfOfferData;

    private List<GOfferAddData> subAtomOffers = new ArrayList<GOfferAddData>();

    private List<GOfferCombinedAddData> subCombinedOffers = new ArrayList<GOfferCombinedAddData>();

    private List<GOfferMultiRolesAddData> subMultiRolesOffers;
    
    private String validDate;
    
    private String expireDate;

    public void setSelfOfferData(GOfferAddData selfOfferData)
    {
        this.selfOfferData = selfOfferData;
    }

    public GOfferAddData getSelfOfferData()
    {
        return selfOfferData;
    }

    public void addSubAtomOfferData(GOfferAddData atomOfferData)
    {
        if (atomOfferData == null)
            return;
        if (subAtomOffers == null)
            subAtomOffers = new ArrayList<GOfferAddData>();
        subAtomOffers.add(atomOfferData);
    }

    public List<GOfferAddData> getSubAtomOffers()
    {
        return subAtomOffers;
    }

    public void addSubCombinedOfferData(GOfferCombinedAddData conOfferData)
    {
        if (conOfferData == null)
            return;
        if (subCombinedOffers == null)
            subCombinedOffers = new ArrayList<GOfferCombinedAddData>();
        subCombinedOffers.add(conOfferData);
    }

    public List<GOfferCombinedAddData> getSubCombinedOffers()
    {
        return subCombinedOffers;
    }

    public void addSubMultiRoleOfferData(GOfferMultiRolesAddData roleData)
    {
        if (roleData == null)
            return;
        if (subMultiRolesOffers == null)
            subMultiRolesOffers = new ArrayList<GOfferMultiRolesAddData>();
        subMultiRolesOffers.add(roleData);
    }

    public List<GOfferMultiRolesAddData> getSubMultiRolesOffers()
    {
        return subMultiRolesOffers;
    }

    public void addOfferRel(String offerRelId, String offerType, String offerRelInsId, String relType)
    {
        GOfferRelAddData offerRel = new GOfferRelAddData();
        offerRel.setRelOfferInsId(offerRelInsId);
        offerRel.setRelType(relType);
        offerRel.setRelOfferCode(offerRelId);
        offerRel.setRelOfferType(offerType);
        selfOfferData.addOfferRel(offerRel);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"SELF_OFFER\":" + this.selfOfferData.toString());
        if (this.subAtomOffers != null && this.subAtomOffers.size() > 0)
        {
            sb.append(",\"SUB_ATOM_OFFERS\":[");
            for (GOfferAddData atom : this.subAtomOffers)
            {
                sb.append(atom.toString() + ",");
            }
            sb = sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("]");
        }

        if (this.subCombinedOffers != null && this.subCombinedOffers.size() > 0)
        {
            sb.append(",\"SUB_COMBINED_OFFERS\":[");
            for (GOfferCombinedAddData combined : this.subCombinedOffers)
            {
                sb.append(combined.toString() + ",");
            }
            sb = sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }

    public static GOfferCombinedAddData getInstance(IData offerData) throws Exception
    {
        GOfferCombinedAddData offer = new GOfferCombinedAddData();
        // 设置组合销售品顶层的自身销售品信息
        GOfferAddData selfLevOfferData = GOfferAddData.getInstance(offerData);
        offer.setSelfOfferData(selfLevOfferData);
        IDataset secondLevOffers = offerData.getDataset("SUBOFFERS");
        if (IDataUtil.isNotEmpty(secondLevOffers))
        {
            for (int i = 0, size = secondLevOffers.size(); i < size; i++)
            {// 遍历组合销售品的第二层销售品
                IData secondLevOffer = secondLevOffers.getData(i);
                IDataset thirdLevCombinedOffers = secondLevOffer.getDataset("SUBOFFERS");// 判断组合销售品的第二层销售品是否为组合销售品
                if (StringUtils.equals(TRADE_MODIFY_TAG.Add.getValue(), secondLevOffer.getString("OPER_CODE")))//新增的时候才处理
                {
                    //thirdLevCombinedOffers = OffersResolverTool.mergerDefaultRequiredSuboffers(thirdLevCombinedOffers, secondLevOffer.getString("OFFER_ID"), EcCommonTool.getMgmtDistrict());
                }
                IDataset thirdLevRolesOffers = secondLevOffer.getDataset("ROLE_OFFERS");// 判断组合销售品的第三层销售品是否为组合销售品

                if (IDataUtil.isNotEmpty(thirdLevRolesOffers))
                {
                    offer.addSubMultiRoleOfferData(GOfferMultiRolesAddData.getInstance(secondLevOffer));
                }
                else if (IDataUtil.isNotEmpty(thirdLevCombinedOffers))
                {
                    //secondLevOffer.put("SUBOFFERS", thirdLevCombinedOffers);
                    offer.addSubCombinedOfferData(GOfferCombinedAddData.getInstance(secondLevOffer));
                }
                else
                {
                    offer.addSubAtomOfferData(GOfferAddData.getInstance(secondLevOffer));
                }
            }
        }
        return offer;

    }

    public String getValidDate()
    {
        return validDate;
    }

    public void setValidDate(String validDate)
    {
        this.validDate = validDate;
    }

    public String getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }
}
