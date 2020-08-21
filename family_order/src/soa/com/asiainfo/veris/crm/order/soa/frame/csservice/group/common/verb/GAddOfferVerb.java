package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferRelAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.OfferTransUtil;

public class GAddOfferVerb extends GVerb
{

    private GOfferAddData offerData;

    private IData offerEntity;

    public GAddOfferVerb() throws Exception
    {
        super();
    }

    public GAddOfferVerb(GOfferAddData offerData) throws Exception
    {
        super();
        this.offerData = offerData;
    }

    public GAddOfferVerb(IData offerEntity, GOfferAddData offerData) throws Exception
    {
        super();
        this.offerEntity = offerEntity;
        this.offerData = offerData;
    }

    public void setOfferAddData(GOfferAddData offerData)
    {
        this.offerData = offerData;
    }

    public void setOfferEntity(IData offerEntity)
    {
        this.offerEntity = offerEntity;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {

        if (offerData == null && offerEntity == null)
            return null;

        if (IDataUtil.isEmpty(offerEntity))
        {
            String offerId = offerData.getOfferId();
            if (StringUtils.isEmpty(offerId))
                return null;

            // 生成销售品的实体数据
            offerEntity = new DataMap();

            if (StringUtils.isNotBlank(offerData.getRelOfferId()))
            {
                offerEntity.put("REL_OFFER_ID", offerData.getRelOfferId());
            }
            offerEntity.put("OFFER_INS_ID", SeqMgr.getInstId());
            offerEntity.put("OFFER_ID", offerId);
            offerEntity.put("ACTION", TRADE_MODIFY_TAG.Add.getValue());
            offerEntity.put("OFFER_TYPE", offerData.getOfferType());

            String validDate = offerData.getValidDate();
            if (StringUtils.isEmpty(validDate))
                validDate = SysDateMgr.getSysDate();

            offerEntity.put("VALID_DATE", validDate);

            String expireDate = offerData.getExpireDate();
            if (StringUtils.isEmpty(expireDate))
                expireDate = SysDateMgr.getTheLastTime();
            offerEntity.put("EXPIRE_DATE", expireDate);
            offerEntity.put("GROUP_ID", offerData.getGroupId());
        }

        String operCode = offerEntity.getString("ACTION");
        if (StringUtils.equals(operCode, TRADE_MODIFY_TAG.Add.getValue()))
        {
            // 主要是判断是否需要新增offerEntity
            if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_SVC, offerEntity.getString("OFFER_TYPE")))
            {
                // 转换字段
                OfferTransUtil.OfferToSvcTrans(offerEntity);
                
                reqData.cd.getSvc().add(offerEntity);
            }
            else if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT, offerEntity.getString("OFFER_TYPE")))
            {
                // 转换字段
                OfferTransUtil.OfferToDiscntTrans(offerEntity);
                
                reqData.cd.getDiscnt().add(offerEntity);
            }
            else if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_PLATSVC, offerEntity.getString("OFFER_TYPE")))
            {
                // 转换字段
                OfferTransUtil.OfferToPlatSvcTrans(offerEntity);
                
                // reqData.cd.getSpSvc().add(offerEntity);
            }
            else if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT, offerEntity.getString("OFFER_TYPE")))
            {
                // 转换字段
                OfferTransUtil.OfferToProductTrans(offerEntity);
                
                IData productIdData = reqData.cd.getProductIdSet();
                if (IDataUtil.isEmpty(productIdData))
                {
                    productIdData = new DataMap();
                    reqData.cd.putProductIdSet(productIdData);
                }
                
                String modifyTag = productIdData.getString(offerEntity.getString("OFFER_ID"));

                // 如果不存在, 则新增产品处理信息
                if (StringUtils.isEmpty(modifyTag))
                {
                    productIdData.put(offerEntity.getString("OFFER_ID"), TRADE_MODIFY_TAG.Add.getValue());
                }
                // 如果同时存在新增和删除的操作, 则不处理产品信息
                else if (StringUtils.equals(modifyTag, offerEntity.getString("ACTION")))
                {
                    productIdData.remove(offerEntity.getString("OFFER_ID"));
                }
            }
        }

        // 生成销售品特征规格实例数据
        List<ChaAddData> offerChaSpecs = offerData.getOfferChaSpecs();
        if (offerChaSpecs != null && offerChaSpecs.size() > 0)
        {
            for (int i = 0, size = offerChaSpecs.size(); i < size; i++)
            {
                ChaAddData offerChaSpec = offerChaSpecs.get(i);
                GAddOfferChaVerb chaverb = new GAddOfferChaVerb(offerEntity, offerChaSpec);

                chaverb.run(reqData);
            }
        }

        // 生成销售品关系
        List<GOfferRelAddData> relOffers = offerData.getOfferRels();
        if (relOffers != null && relOffers.size() > 0)
        {
            for (GOfferRelAddData relOffer : relOffers)
            {
                String relOfferCode = relOffer.getRelOfferCode();
                String relOfferType = relOffer.getRelOfferType();
                String relType = relOffer.getRelType();
                String relInsId = relOffer.getRelOfferInsId();
                String bundValidDate = offerEntity.getString("START_DATE");
                String bundExpireDate = offerEntity.getString("END_DATE");

                GAddOfferRelVerb aobrverb = new GAddOfferRelVerb(relInsId, relOfferCode, relOfferType, offerEntity.getString("OFFER_INS_ID"), offerEntity.getString("OFFER_ID"), offerEntity.getString("OFFER_TYPE"), relType);
                aobrverb.setValidDate(bundValidDate);
                aobrverb.setExpireDate(bundExpireDate);
                aobrverb.setGroupId(offerEntity.getString("GROUP_ID", "-1"));

                aobrverb.run(reqData);
            }
        }

        return offerEntity;

    }

}
