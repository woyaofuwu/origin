package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.verb;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;

public class GAddOfferChaVerb extends GVerb
{

    private ChaAddData chaAddData;
    
    private IData offer;
    
    
    public GAddOfferChaVerb() throws Exception
    {
    }

    public void setChaAddData(ChaAddData chaAddData)
    {
        this.chaAddData = chaAddData;
    }

    public void setOfferEntity(IData offer)
    {
        this.offer = offer;
    }

    public GAddOfferChaVerb(IData offer, ChaAddData chaAddData) throws Exception
    {
        this.chaAddData = chaAddData;
        this.offer = offer;
    }

    public IData run(GroupBaseReqData reqData) throws Exception
    {
        if (offer == null)
        {
            return null;
        }
        if (chaAddData == null)
        {
        	return null;
        }
        
        String offerInsId = offer.getString("OFFER_INS_ID");
        String offerId = offer.getString("OFFER_ID");
        String offerSpecCode = chaAddData.getChaSpecCode();
        String offerChaValue = chaAddData.getValue();
        
        //如果是NOTIN开头,不入表
        if(offerSpecCode.startsWith("NOTIN"))
            return null;
        
        //如果是FEE开头，表示费用
        if(offerSpecCode.startsWith("FEE"))
        {
            if(StringUtils.isEmpty(offerChaValue))
                offerChaValue = "0";
            else
                offerChaValue = String.valueOf(100 * Integer.parseInt(offerChaValue));
        }
        
        String offerChaInsId = SeqMgr.getInstId();
		String offerSpecId = chaAddData.getChaSpecId();
		String offerSpecValueCode = chaAddData.getChaSpecValCode();
		String offerChaValueId = chaAddData.getChaValueId();
		
		String offerChaValidDate = chaAddData.getValidDate();
		if(StringUtils.isEmpty(offerChaValidDate))
			offerChaValidDate = offer.getString("START_DATE");
		if(StringUtils.isEmpty(offerChaValidDate))
			offerChaValidDate = SysDateMgr.getSysDate();// reqData.getBusiBaseValidDate();
		String offerChaExpireDate = offer.getString("END_DATE");
		if(StringUtils.isEmpty(offerChaExpireDate))
			offerChaExpireDate = SysDateMgr.getTheLastTime();
		
		IData omOfferChaSpec = new DataMap();
		
		omOfferChaSpec.put("OFFER_CHA_INS_ID", offerChaInsId);//商品特征实例标识
		omOfferChaSpec.put("OFFER_INS_ID", offerInsId);//商品实例标识
		omOfferChaSpec.put("OFFER_ID", offerId);
		omOfferChaSpec.put("CHA_SPEC_ID", offerSpecId);//特征规格标识
		omOfferChaSpec.put("CHA_SPEC_VAL_ID", offerChaValueId);//特征规格值标识
		omOfferChaSpec.put("CHA_SPEC_CODE", offerSpecCode);//特征规格编码
		omOfferChaSpec.put("CHA_SPEC_VAL_CODE", offerSpecValueCode);//特征规格值编码
		omOfferChaSpec.put("VALUE", offerChaValue);//特征值
		omOfferChaSpec.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());//操作
		omOfferChaSpec.put("OFFER_TYPE", offer.getString("OFFER_TYPE"));
		omOfferChaSpec.put("START_DATE", offerChaValidDate);
		omOfferChaSpec.put("END_DATE", offerChaExpireDate);
		omOfferChaSpec.put("UP_CHA_INS_ID", "-1");
		
		reqData.cd.getElementParam().add(omOfferChaSpec);
		
        return omOfferChaSpec;
    }
}
