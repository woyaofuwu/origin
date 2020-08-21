package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferRelAddData;

public class GModOfferVerb extends GVerb
{
    private boolean isQzNeedChgOffer = false; // 是否需要强制执行变更操作
    
    public boolean isQzNeedChgOffer()
    {
        return isQzNeedChgOffer;
    }

    public void setQzNeedChgOffer(boolean isQzNeedChgOffer)
    {
        this.isQzNeedChgOffer = isQzNeedChgOffer;
    }

    private static final IData offerEntity = null;
    private GOfferModData omd;

    public GModOfferVerb() throws Exception
    {
        super();
    }

    public GModOfferVerb(GOfferModData omd) throws Exception
    {
        super();
        this.omd = omd;
        if (omd.isQzNeedChgOffer())
        {
            this.isQzNeedChgOffer = true;
        }
    }

    public void setOfferData(GOfferModData omd)
    {
        this.omd = omd;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {
        if (omd == null)
            return null;

        String offerInsId = omd.getOfferInsId();
        if (StringUtils.isEmpty(offerInsId))
            return null;

        String offerId = omd.getOfferId();
        if (StringUtils.isEmpty(offerId))
            return null;

        String offerType = omd.getOfferType();

        IDataset offerList = new DatasetList();
        if (StringUtils.equals(offerType, "D"))
        {
            offerList = UserDiscntInfoQry.getUserProductDiscntByUserIdAndInstId(reqData.getUca().getUserId(), offerInsId);
        }
        else if (StringUtils.equals(offerType, "S"))
        {
            offerList = UserSvcInfoQry.getUserProductSvcByUserIdAndInstId(reqData.getUca().getUserId(), offerInsId);
        }
        else if (StringUtils.equals(offerType, "P"))
        {
            offerList = UserProductInfoQry.getUserProductInfoByUserIdAndInstId(reqData.getUca().getUserId(), offerInsId);
        }

        if (IDataUtil.isEmpty(offerList))
            return null;

        IData oe = offerList.getData(0);

        boolean needOfferSel = false; // 是否修改销售本身

        // 处理商品特征实体
        List<ChaModData> offerChaModSpecs = omd.getModChaSpecs();
        if (offerChaModSpecs != null && offerChaModSpecs.size() > 0)
        {
            // 查询商品特征资料表数据
            IDataset offerChaList = UserAttrInfoQry.getUserAttrByInstID(reqData.getUca().getUserId(), offerInsId);

            for (ChaModData chaModData : offerChaModSpecs)
            {

                String code = chaModData.getChaSpecCode();
                String value = chaModData.getValue();

                // 如果是FEE开头，表示费用前台传过来的是元
                if (code.startsWith("FEE"))
                {
                    if (StringUtils.isEmpty(value))
                        value = "0";
                    else
                        value = String.valueOf(100 * Integer.parseInt(value));
                }

                boolean needAdd = true;

                // 修改销售品特征
                for (int i = 0, size = offerChaList.size(); i < size; i++)
                {
                    IData oldChaSpec = offerChaList.getData(i);
                    String oldChaSpecCode = oldChaSpec.getString("ATTR_CODE");
                    String oldValue = oldChaSpec.getString("ATTR_VALUE");

                    if (code.equals(oldChaSpecCode))
                    {
                        needAdd = false;

                        if (!value.equals(oldValue))
                        {
                            // 变更特征时记录销售品修改
                            needOfferSel = true;

                            // 资费属性变更时，新增一条删除一条
                            if (StringUtils.equals(offerType, "D"))
                            {
                                // 删除原有的
                                IData delOfferChaEntity = oldChaSpec;
                                String offerChaExpireDate = SysDateMgr.getSysDate();
                                delOfferChaEntity.put("END_DATE", offerChaExpireDate);
                                delOfferChaEntity.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                                reqData.cd.getElementParam().add(delOfferChaEntity);

                                ChaAddData chaAddData = new ChaAddData();
                                chaAddData.setValidDate(SysDateMgr.getNextSecond(offerChaExpireDate));
                                chaAddData.setChaSpecCode(delOfferChaEntity.getString("CHA_SPEC_CODE"));
                                chaAddData.setChaSpecId(delOfferChaEntity.getString("CHA_SPEC_ID"));
                                chaAddData.setValue(value);
                                GAddOfferChaVerb chaVerb = new GAddOfferChaVerb(oe, chaAddData);
                                chaVerb.run(reqData);

                            }
                            else
                            {
                                IData chaEntity = oldChaSpec;

                                chaEntity.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                chaEntity.put("ATTR_VALUE", value);

                                reqData.cd.getElementParam().add(chaEntity);
                            }
                        }
                    }
                }
                // 新增销售品特征
                if (needAdd)
                {
                    // 新增特征时记录销售品修改
                    needOfferSel = true;
                    GAddOfferChaVerb chaVerb = new GAddOfferChaVerb(oe, ChaModData.getChaAddData(chaModData));
                    chaVerb.run(reqData);
                }
            }
        }

        // 生成销售品关系
        List<GOfferRelAddData> relOffers = omd.getAddOfferRels();

        if (relOffers != null && relOffers.size() > 0)
        {
            for (GOfferRelAddData relOffer : relOffers)
            {
                String relType = relOffer.getRelType();
                String relInsId = relOffer.getRelOfferInsId();
                String relOfferCode = relOffer.getRelOfferCode();
                String relOfferType = relOffer.getRelOfferType();
                String bundValidDate = oe.getString("START_DATE");
                if (StringUtils.isEmpty(bundValidDate))
                    bundValidDate = reqData.getAcceptTime();
                String bundExpireDate = oe.getString("END_DATE");
                if (StringUtils.isEmpty(bundExpireDate))
                    bundExpireDate = SysDateMgr.getTheLastTime();

                GAddOfferRelVerb aobrverb = new GAddOfferRelVerb(relInsId, relOfferCode, relOfferType, offerInsId, offerId, offerType, relType);
                aobrverb.setValidDate(bundValidDate);
                aobrverb.setExpireDate(bundExpireDate);
                aobrverb.setGroupId(oe.getString("PACKAGE_ID", "-1"));

                aobrverb.run(reqData);
            }
        }

        // 修改销售品本身
        if (needOfferSel || isQzNeedChgOffer()) // 强制执行变更操作也可以
        {
            oe.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            
            if (StringUtils.equals(offerType, "D"))
            {
                reqData.cd.getDiscnt().add(oe);
            }
            else if (StringUtils.equals(offerType, "S"))
            {
                reqData.cd.getSvc().add(oe);
            }
            else if (StringUtils.equals(offerType, "P"))
            {
                reqData.cd.getProduct().add(oe);
            }
        }
        
        oe.put("OFFER_INS_ID", oe.getString("INST_ID"));
        oe.put("OFFER_ID", offerId);
        oe.put("OFFER_TYPE", offerType);

        return oe;
    }

}
