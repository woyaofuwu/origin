package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IData;

public class GOfferCombinedModData
{

    private GOfferModData selfOfferModData;

    private List<GOfferAddData> subAtomAddOffers;

    private List<GOfferModData> subAtomModOffers;

    private List<GOfferDelData> subAtomDelOffers;

    private List<GOfferCombinedAddData> subCombinedAddOffers;

    private List<GOfferCombinedModData> subCombinedModOffers;

    private List<GOfferMultiRolesAddData> subMultiRolesAddOffers;

    private List<GOfferMultiRolesModData> subMultiRolesModOffers;

    public GOfferModData getSelfOfferModData()
    {
        return selfOfferModData;
    }

    public void setSelfOfferModData(GOfferModData selfOfferModData)
    {
        this.selfOfferModData = selfOfferModData;
    }

    public List<GOfferAddData> getSubAtomAddOffers()
    {
        return subAtomAddOffers;
    }

    public void setSubAtomAddOffers(List<GOfferAddData> subAtomAddOffers)
    {
        this.subAtomAddOffers = subAtomAddOffers;
    }

    public List<GOfferModData> getSubAtomModOffers()
    {
        return subAtomModOffers;
    }

    public void setSubAtomModOffers(List<GOfferModData> subAtomModOffers)
    {
        this.subAtomModOffers = subAtomModOffers;
    }

    public void addSubAtomModOffers(GOfferModData subAtomModOffer)
    {
        if (subAtomModOffer == null)
            return;
        if (subAtomModOffers == null)
            subAtomModOffers = new ArrayList<GOfferModData>();
        subAtomModOffers.add(subAtomModOffer);
    }

    public List<GOfferDelData> getSubAtomDelOffers()
    {
        return subAtomDelOffers;
    }

    public void setSubAtomDelOffers(List<GOfferDelData> subAtomDelOffers)
    {
        this.subAtomDelOffers = subAtomDelOffers;
    }

    public void addSubAtomDelOffers(GOfferDelData subAtomDelOffer)
    {
        if (subAtomDelOffer == null)
            return;
        if (subAtomDelOffers == null)
            subAtomDelOffers = new ArrayList<GOfferDelData>();
        subAtomDelOffers.add(subAtomDelOffer);
    }

    public List<GOfferCombinedAddData> getSubCombinedAddOffers()
    {
        return subCombinedAddOffers;
    }

    public void setSubCombinedAddOffers(List<GOfferCombinedAddData> subCombinedAddOffers)
    {
        this.subCombinedAddOffers = subCombinedAddOffers;
    }

    public void addSubCombinedAddOffers(GOfferCombinedAddData combinedAddData)
    {
        if (combinedAddData == null)
            return;
        if (subCombinedAddOffers == null)
            subCombinedAddOffers = new ArrayList<GOfferCombinedAddData>();
        subCombinedAddOffers.add(combinedAddData);
    }

    public void addSubAtomAddOffers(GOfferAddData subAtomAddOffer)
    {
        if (subAtomAddOffer == null)
            return;
        if (subAtomAddOffers == null)
            subAtomAddOffers = new ArrayList<GOfferAddData>();
        subAtomAddOffers.add(subAtomAddOffer);
    }

    public List<GOfferCombinedModData> getSubCombinedModOffers()
    {
        return subCombinedModOffers;
    }

    public void setSubCombinedModOffers(List<GOfferCombinedModData> subCombinedModOffers)
    {
        this.subCombinedModOffers = subCombinedModOffers;
    }

    public void addSubCombinedModOffers(GOfferCombinedModData subCombinedModOffer)
    {
        if (subCombinedModOffer == null)
            return;
        if (subCombinedModOffers == null)
            subCombinedModOffers = new ArrayList<GOfferCombinedModData>();
        subCombinedModOffers.add(subCombinedModOffer);
    }

    public List<GOfferMultiRolesAddData> getSubMultiRolesAddOffers()
    {
        return subMultiRolesAddOffers;
    }

    public void setSubMultiRolesAddOffers(List<GOfferMultiRolesAddData> subMultiRolesAddOffers)
    {
        this.subMultiRolesAddOffers = subMultiRolesAddOffers;
    }

    public void addSubMultiRolesAddOffers(GOfferMultiRolesAddData multiRolesAddData)
    {
        if (multiRolesAddData == null)
            return;
        if (subMultiRolesAddOffers == null)
            subMultiRolesAddOffers = new ArrayList<GOfferMultiRolesAddData>();
        subMultiRolesAddOffers.add(multiRolesAddData);
    }

    public List<GOfferMultiRolesModData> getSubMultiRolesModOffers()
    {
        return subMultiRolesModOffers;
    }

    public void setSubMultiRolesModOffers(List<GOfferMultiRolesModData> subMultiRolesModOffers)
    {
        this.subMultiRolesModOffers = subMultiRolesModOffers;
    }

    public void addSubMultiRolesModOffers(GOfferMultiRolesModData multiRolesModData)
    {
        if (multiRolesModData == null)
            return;
        if (subMultiRolesModOffers == null)
            subMultiRolesModOffers = new ArrayList<GOfferMultiRolesModData>();
        subMultiRolesModOffers.add(multiRolesModData);
    }

    public static GOfferCombinedModData getInstance(IData offerData) throws Exception
    {
        GOfferCombinedModData offer = new GOfferCombinedModData();

        // 设置组合销售品顶层的自身销售品信息
        GOfferModData selfLevOfferData = GOfferModData.getInstance(offerData);
        offer.setSelfOfferModData(selfLevOfferData);

        IDataset secondLevOffers = offerData.getDataset("SUBOFFERS");

        if (IDataUtil.isNotEmpty(secondLevOffers))
        {
            for (int i = 0, size = secondLevOffers.size(); i < size; i++)
            {
                // 遍历组合销售品的第二层销售品
                IData secondLevOffer = secondLevOffers.getData(i);
                String secondopercode = secondLevOffer.getString("OPER_CODE");

                IDataset thirdLevCombinedOffers = secondLevOffer.getDataset("SUBOFFERS");// 判断组合销售品的第二层销售品是否为组合销售品
                IDataset thirdLevRolesOffers = secondLevOffer.getDataset("ROLE_OFFERS");// 判断组合销售品的第三层销售品是否为组合销售品

                if (IDataUtil.isNotEmpty(thirdLevRolesOffers))
                {

                    if (TRADE_MODIFY_TAG.Add.getValue().equals(secondopercode))
                    {
                        offer.addSubMultiRolesAddOffers(GOfferMultiRolesAddData.getInstance(secondLevOffer));
                    }
                    else
                    {
                        offer.addSubMultiRolesModOffers(GOfferMultiRolesModData.getInstance(secondLevOffer));
                    }

                }
                else if (IDataUtil.isNotEmpty(thirdLevCombinedOffers))
                {
                    if (TRADE_MODIFY_TAG.Add.getValue().equals(secondopercode))
                    {
                        offer.addSubCombinedAddOffers(GOfferCombinedAddData.getInstance(secondLevOffer));
                    }
                    else
                    {
                        offer.addSubCombinedModOffers(GOfferCombinedModData.getInstance(secondLevOffer));
                    }
                }
                else
                {
                    if (TRADE_MODIFY_TAG.Add.getValue().equals(secondopercode))
                    {
                        offer.addSubAtomAddOffers(GOfferAddData.getInstance(secondLevOffer));
                    }
                    else if (TRADE_MODIFY_TAG.DEL.getValue().equals(secondopercode))
                    {
                        offer.addSubAtomDelOffers(GOfferDelData.getInstance(secondLevOffer));
                    }
                    else
                    {
                        offer.addSubAtomModOffers(GOfferModData.getInstance(secondLevOffer));
                    }
                }
            }
        }

        return offer;

    }
}
