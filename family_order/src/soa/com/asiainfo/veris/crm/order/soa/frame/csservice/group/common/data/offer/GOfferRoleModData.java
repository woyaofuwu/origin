package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class GOfferRoleModData extends OfferData
{
    private String roleId;

    private String busitype;

    private String accessNum;

    private IData subscriber;

    private IData account;

    private IData customer;

    private IData commondata;

    private List<GOfferAddData> atomAddOffers;

    private List<GOfferDelData> atomDelOffers;

    private List<GOfferModData> atomModOffers;

    private List<GOfferCombinedAddData> conbinedAddOffers;

    private List<GOfferCombinedModData> conbinedModOffers;

    private List<GOfferMultiRolesModData> multieModOffers;

    private List<GOfferMultiRolesAddData> multieAddOffers;

    public static GOfferRoleModData getInstance(IData offerData) throws Exception
    {
        GOfferRoleModData roleOffer = new GOfferRoleModData();
        if (offerData == null)
            return roleOffer;
        roleOffer.setOfferId(offerData.getString("OFFER_ID"));
        roleOffer.setRoleId(offerData.getString("ROLE_ID"));
        roleOffer.setAccessNum(offerData.getString("ACCESS_NUM"));
        roleOffer.setSubscriber(offerData.getData("SUBSCRIBER"));
        roleOffer.setAccount(offerData.getData("ACCOUNT"));
        roleOffer.setCustomer(offerData.getData("CUSTOMER"));
        roleOffer.setCommondata(offerData.getData("COMMON_DATA"));
        roleOffer.setBusitype(offerData.getString("BUSI_TYPE"));

        IDataset offers = offerData.getDataset("OFFERS");

        if (IDataUtil.isNotEmpty(offers))
        {
            for (int i = 0, size = offers.size(); i < size; i++)
            {
                // 遍历组合销售品的第二层销售品
                IData offer = offers.getData(i);
                String roleOpercode = offer.getString("OPER_CODE");

                IDataset secondLevConOffers = offer.getDataset("SUBOFFERS");// 判断第一层offer数据结构下是否有子层offer数据（第二层）
                IDataset secondLevRolesOffers = offer.getDataset("ROLE_OFFERS");// 判断第一层offer数据结构下是否挂了其它角色的子层offer（第二层）

                if (StringUtils.isNotBlank(roleOpercode) && roleOpercode.equals(TRADE_MODIFY_TAG.DEL.getValue()))
                {
                    roleOffer.addAtomDelOfferData(GOfferDelData.getInstance(offer));
                    continue;
                }

                // 多角色的销售品
                if (IDataUtil.isNotEmpty(secondLevRolesOffers))
                {

                    if (TRADE_MODIFY_TAG.Add.getValue().equals(roleOpercode))
                    {
                        roleOffer.addSubMultieRolesAddOfferData(GOfferMultiRolesAddData.getInstance(offer));
                    }
                    else
                    {
                        roleOffer.addSubMultieRolesModOfferData(GOfferMultiRolesModData.getInstance(offer));
                    }
                }
                else if (IDataUtil.isNotEmpty(secondLevConOffers))
                {
                    if (TRADE_MODIFY_TAG.Add.getValue().equals(roleOpercode))
                    {
                        roleOffer.addSubCombinedAddOfferData(GOfferCombinedAddData.getInstance(offer));
                    }
                    else
                    {
                        roleOffer.addSubCombinedModOfferData(GOfferCombinedModData.getInstance(offer));
                    }
                }
                else
                {
                    if (TRADE_MODIFY_TAG.Add.getValue().equals(roleOpercode))
                    {
                        roleOffer.addAtomAddOfferData(GOfferAddData.getInstance(offer));
                    }
                    else
                    {
                        // 如果操作编码存在且为注销，则注销；否则为变更
                        roleOffer.addAtomModOfferData(GOfferModData.getInstance(offer));
                    }
                }

            }
        }
        return roleOffer;
    }

    public String getRoleId()
    {
        return roleId;
    }

    public void setRoleId(String roleId)
    {
        this.roleId = roleId;
    }

    public String getAccessNum()
    {
        return accessNum;
    }

    public void setAccessNum(String accessNum)
    {
        this.accessNum = accessNum;
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

    public List<GOfferModData> getAtomModOffers()
    {
        return atomModOffers;
    }

    public void setAtomModOffers(List<GOfferModData> atomModOffers)
    {
        this.atomModOffers = atomModOffers;
    }

    public List<GOfferCombinedModData> getConbinedModOffers()
    {
        return conbinedModOffers;
    }

    public void addAtomModOfferData(GOfferModData atomOffer)
    {
        if (atomOffer == null)
            return;
        if (atomModOffers == null)
            atomModOffers = new ArrayList<GOfferModData>();
        atomModOffers.add(atomOffer);
    }

    public void addAtomDelOfferData(GOfferDelData atomOffer)
    {
        if (atomOffer == null)
            return;
        if (atomDelOffers == null)
            atomDelOffers = new ArrayList<GOfferDelData>();
        atomDelOffers.add(atomOffer);
    }

    public void addAtomAddOfferData(GOfferAddData atomOffer)
    {
        if (atomOffer == null)
            return;
        if (atomAddOffers == null)
            atomAddOffers = new ArrayList<GOfferAddData>();
        atomAddOffers.add(atomOffer);
    }

    public void addSubCombinedModOfferData(GOfferCombinedModData conOfferData)
    {
        if (conOfferData == null)
            return;
        if (conbinedModOffers == null)
            conbinedModOffers = new ArrayList<GOfferCombinedModData>();
        conbinedModOffers.add(conOfferData);
    }

    public void addSubCombinedAddOfferData(GOfferCombinedAddData conOfferData)
    {
        if (conOfferData == null)
            return;
        if (conbinedAddOffers == null)
            conbinedAddOffers = new ArrayList<GOfferCombinedAddData>();
        conbinedAddOffers.add(conOfferData);
    }

    public void addSubMultieRolesModOfferData(GOfferMultiRolesModData multieOfferData)
    {
        if (multieOfferData == null)
            return;
        if (multieModOffers == null)
            multieModOffers = new ArrayList<GOfferMultiRolesModData>();
        multieModOffers.add(multieOfferData);
    }

    public void addSubMultieRolesAddOfferData(GOfferMultiRolesAddData multieOfferData)
    {
        if (multieOfferData == null)
            return;
        if (multieAddOffers == null)
            multieAddOffers = new ArrayList<GOfferMultiRolesAddData>();
        multieAddOffers.add(multieOfferData);
    }

    public void setConbinedModOffers(List<GOfferCombinedModData> conbinedModOffers)
    {
        this.conbinedModOffers = conbinedModOffers;
    }

    public List<GOfferMultiRolesModData> getMultieModOffers()
    {
        return multieModOffers;
    }

    public void setMultieModOffers(List<GOfferMultiRolesModData> multieModOffers)
    {
        this.multieModOffers = multieModOffers;
    }

    public List<GOfferAddData> getAtomAddOffers()
    {
        return atomAddOffers;
    }

    public void setAtomAddOffers(List<GOfferAddData> atomAddOffers)
    {
        this.atomAddOffers = atomAddOffers;
    }

    public List<GOfferDelData> getAtomDelOffers()
    {
        return atomDelOffers;
    }

    public void setAtomDelOffers(List<GOfferDelData> atomDelOffers)
    {
        this.atomDelOffers = atomDelOffers;
    }

    public List<GOfferCombinedAddData> getConbinedAddOffers()
    {
        return conbinedAddOffers;
    }

    public void setConbinedAddOffers(List<GOfferCombinedAddData> conbinedAddOffers)
    {
        this.conbinedAddOffers = conbinedAddOffers;
    }

    public List<GOfferMultiRolesAddData> getMultieAddOffers()
    {
        return multieAddOffers;
    }

    public void setMultieAddOffers(List<GOfferMultiRolesAddData> multieAddOffers)
    {
        this.multieAddOffers = multieAddOffers;
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
