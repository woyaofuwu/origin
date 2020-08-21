package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferDelData;

public class GDelOfferVerb extends GVerb
{

    private GOfferDelData offerData;

    private IData offerEntity; // 支持传入已经查询过销售品实体信息，减少一次查询
    
    private String userIdA = null;

    private boolean isAutoDelUpOfferRel = true;
    
    private boolean isAutoDelMainOfferCha = true;

    public GDelOfferVerb(String offerInstId, String expireDate) throws Exception
    {
        offerData = new GOfferDelData();
        offerData.setOfferInsId(offerInstId);
        offerData.setExpireDate(expireDate);
    }

    public GDelOfferVerb(String offerInstId) throws Exception
    {
        offerData = new GOfferDelData();
        offerData.setOfferInsId(offerInstId);
    }

    public GDelOfferVerb(GOfferDelData offerData) throws Exception
    {
        this.offerData = offerData;
    }

    public GDelOfferVerb(IData offerEntity) throws Exception
    {
        this.offerEntity = offerEntity;
    }

    public void setExpireDate(String expireDate)
    {
        if (offerData == null)
            offerData = new GOfferDelData();

        offerData.setExpireDate(expireDate);
    }

    public boolean isAutoDelUpOfferRel()
    {

        return isAutoDelUpOfferRel;
    }

    public void setAutoDelUpOfferRel(boolean isAutoDelUpOfferRel)
    {

        this.isAutoDelUpOfferRel = isAutoDelUpOfferRel;
    }
    
    public boolean isAutoDelMainOfferCha()
    {
        return isAutoDelMainOfferCha;
    }

    public void setAutoDelMainOfferCha(boolean isAutoDelMainOfferCha)
    {
        this.isAutoDelMainOfferCha = isAutoDelMainOfferCha;
    }
    
    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {
        // 注销销售品的实体数据
        if (offerData == null && offerEntity == null)
        {
            return null;
        }

        boolean qrySelOffer = true;
        String offerInstId = null;
        String offerType = null;

        if (offerEntity != null)
        {
            offerInstId = offerEntity.getString("INST_ID");
            offerType = offerEntity.getString("OFFER_TYPE");

            if (StringUtils.isEmpty(offerInstId))
            {
                offerInstId = offerData.getOfferInsId();
                offerType = offerData.getOfferType();
            }
            else
            {
                qrySelOffer = false;
            }
        }
        else
        {
            offerInstId = offerData.getOfferInsId();
            offerType = offerData.getOfferType();
        }

        if (StringUtils.isEmpty(offerInstId))
        {
            return null;
        }

        if (StringUtils.isEmpty(offerType))
            offerType = "";// 调用查询逻辑

        if (qrySelOffer)
        {
            IDataset offerList = new DatasetList();
            if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_DISCNT))
            {
                offerList = UserDiscntInfoQry.getUserProductDis(reqData.getUca().getUserId(), userIdA);
            }
            else if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_SVC))
            {
                offerList = UserSvcInfoQry.getUserProductSvc(reqData.getUca().getUserId(), userIdA, null);
            }
            else if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_PRODUCT))
            {
                offerList = UserProductInfoQry.getProductInfo(reqData.getUca().getUserId(), userIdA);
            }
            else if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_RES))
            {
                offerList = UserResInfoQry.getUserProductRes(reqData.getUca().getUserId(), userIdA, null);
            }

            if (IDataUtil.isEmpty(offerList))
                return null;

            offerEntity = offerList.getData(0);
        }

        // 终止销售品
        offerEntity.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        String expireDate = SysDateMgr.getSysDate();
        if (offerData != null && StringUtils.isNotEmpty(offerData.getExpireDate()))
        {
            expireDate = offerData.getExpireDate();
        }
        offerEntity.put("END_DATE", expireDate);

        // 加入容器中需要登记删除的商品中
        if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_DISCNT))
        {
            reqData.cd.getDiscnt().add(offerEntity);
        }
        else if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_SVC))
        {
            reqData.cd.getSvc().add(offerEntity);
        }
        else if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_PRODUCT))
        {
            reqData.cd.getProduct().add(offerEntity);
        }
        else if (StringUtils.equals(offerType, BofConst.ELEMENT_TYPE_CODE_RES)) 
        {
            reqData.cd.getRes().add(offerEntity);
        }

        if (isAutoDelMainOfferCha())
        {
            // 注销用户参数信息
            IDataset offerChaList = UserAttrInfoQry.qryUserAttrByUserRelaInstId(reqData.getUca().getUserId(), offerEntity.getString("INST_ID"));
            
            if (IDataUtil.isNotEmpty(offerChaList))
            {
                for (int j = 0, chaSize = offerChaList.size(); j < chaSize; j++)
                {
                    IData oce = offerChaList.getData(j);
                    oce.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    oce.put("END_DATE", offerEntity.getString("END_DATE"));
                    
                    // 放入实体容器
                    reqData.cd.getElementParam().add(oce);
                }
            }
        }

        if (isAutoDelUpOfferRel)
        {
            // 查询商品上层关系UM_BUNDLE_OFFER_REL
            IDataset bundleOfferRelList = getUpBundleOfferRel(offerInstId);

            if (IDataUtil.isNotEmpty(bundleOfferRelList))
            {
                for (int i = 0, size = bundleOfferRelList.size(); i < size; i++)
                {
                    IData bore = bundleOfferRelList.getData(i);
                    bore.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    bore.put("END_DATE", offerEntity.getString("END_DATE"));

                    // 放入实体容器
                    reqData.cd.getOfferRel().add(bore);
                }
            }
        }

        return offerEntity;
    }

    /**
     * 获取商品上层构成关系
     * 
     * @param relType
     * @param relOfferInsId
     * @return
     * @throws Exception
     */
    private IDataset getUpBundleOfferRel(String relOfferInsId) throws Exception
    {
        IDataset bundleOfferRelList = UserOfferRelInfoQry.qryUserOfferRelInfosByRelOfferInstId(relOfferInsId);
        
        return bundleOfferRelList;
    }
}
