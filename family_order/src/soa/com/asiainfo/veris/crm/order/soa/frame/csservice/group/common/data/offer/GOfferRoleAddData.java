package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class GOfferRoleAddData extends OfferData
{

    private String roleId;

    private String busitype;

    private String accessNum;

    private IData subscriber;

    private IData account;

    private IData customer;

    private IData commondata;

    private List<GOfferAddData> atomOffers;

    private List<GOfferCombinedAddData> conbinedOffers;

    private List<GOfferMultiRolesAddData> multieOffers;

    private String validDate;

    private String expireDate;

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

    public void setRoleId(String roleId)
    {
        this.roleId = roleId;
    }

    public void setAccessNum(String accessNum)
    {
        this.accessNum = accessNum;
    }

    public void addAtomOfferData(GOfferAddData atomOffer)
    {
        if (atomOffer == null)
            return;
        if (atomOffers == null)
            atomOffers = new ArrayList<GOfferAddData>();
        atomOffers.add(atomOffer);
    }

    public void addSubCombinedOfferData(GOfferCombinedAddData conOfferData)
    {
        if (conOfferData == null)
            return;
        if (conbinedOffers == null)
            conbinedOffers = new ArrayList<GOfferCombinedAddData>();
        conbinedOffers.add(conOfferData);
    }

    public void addSubMultieRolesOfferData(GOfferMultiRolesAddData multieOfferData)
    {
        if (multieOfferData == null)
            return;
        if (multieOffers == null)
            multieOffers = new ArrayList<GOfferMultiRolesAddData>();
        multieOffers.add(multieOfferData);
    }

    public static GOfferRoleAddData getInstance(IData offerData) throws Exception
    {
        GOfferRoleAddData roleOffer = new GOfferRoleAddData();
        if (offerData == null)
            return roleOffer;
        roleOffer.setOfferId(offerData.getString("OFFER_ID"));
        roleOffer.setRoleId(offerData.getString("ROLE_ID"));
        roleOffer.setBusitype(offerData.getString("BUSI_TYPE"));
        roleOffer.setAccessNum(offerData.getString("ACCESS_NUM"));
        roleOffer.setSubscriber(offerData.getData("SUBSCRIBER"));
        roleOffer.setAccount(offerData.getData("ACCOUNT"));
        roleOffer.setCustomer(offerData.getData("CUSTOMER"));
        roleOffer.setCommondata(offerData.getData("COMMON_DATA"));

        IDataset offers = offerData.getDataset("OFFERS");

        if (IDataUtil.isNotEmpty(offers))
        {
            for (int i = 0, size = offers.size(); i < size; i++)
            {
                IData offer = offers.getData(i);
                String offerId = offer.getString("OFFER_ID");
                String isBund = offer.getString("IS_BUND");
                if ("0".equals(isBund))
                {
                    roleOffer.addAtomOfferData(GOfferAddData.getInstance(offer));
                    continue;
                }

                IDataset secondLevConOffers = offer.getDataset("SUBOFFERS");// 判断第一层offer数据结构下是否有子层offer数据（第二层）
                IDataset secondLevRolesOffers = offer.getDataset("ROLE_OFFERS");// 判断第一层offer数据结构下是否挂了其它角色的子层offer（第二层）
                // 多角色的销售品
                if (IDataUtil.isNotEmpty(secondLevRolesOffers))
                {
                    GOfferMultiRolesAddData mutieOffer = GOfferMultiRolesAddData.getInstance(offer);
                    roleOffer.addSubMultieRolesOfferData(mutieOffer);
                    continue;
                }
                else
                {
                    GOfferCombinedAddData conOffer = GOfferCombinedAddData.getInstance(offer);
                    roleOffer.addSubCombinedOfferData(conOffer);
                    continue;
                }

            }
        }
        return roleOffer;
    }

    public List<GOfferAddData> getAtomOffers()
    {
        return atomOffers;
    }

    public void setAtomOffers(List<GOfferAddData> atomOffers)
    {
        this.atomOffers = atomOffers;
    }

    public List<GOfferCombinedAddData> getConbinedOffers()
    {
        return conbinedOffers;
    }

    public void setConbinedOffers(List<GOfferCombinedAddData> conbinedOffers)
    {
        this.conbinedOffers = conbinedOffers;
    }

    public List<GOfferMultiRolesAddData> getMultieOffers()
    {
        return multieOffers;
    }

    public void setMultieOffers(List<GOfferMultiRolesAddData> multieOffers)
    {
        this.multieOffers = multieOffers;
    }

    public String getRoleId()
    {
        return roleId;
    }

    public String getAccessNum()
    {
        return accessNum;
    }

    public IData getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(IData subscriber)
    {
        this.subscriber = subscriber;
    }

    public IData getAccount()
    {
        return account;
    }

    public void setAccount(IData account)
    {
        this.account = account;
    }

    public IData getCustomer()
    {
        return customer;
    }

    public void setCustomer(IData customer)
    {
        this.customer = customer;
    }

    public IData getCommondata()
    {
        return commondata;
    }

    public void setCommondata(IData commondata)
    {
        this.commondata = commondata;
    }

    public String getBusitype()
    {
        return busitype;
    }

    public void setBusitype(String busitype)
    {
        this.busitype = busitype;
    }
}
