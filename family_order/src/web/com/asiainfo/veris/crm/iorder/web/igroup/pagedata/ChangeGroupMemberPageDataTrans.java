package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;

public class ChangeGroupMemberPageDataTrans extends PageDataTrans
{
    @Override
    public IData transformData() throws Exception
    {
        super.transformData();

        IData data = new DataMap();
        
        IDataset memberOffers = getOfferList();
        if(DataUtils.isEmpty(memberOffers))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员商品数据结构！");
        }
        
        IDataset offerInfoList = transformOfferList(memberOffers);
        data.put("ELEMENT_INFO", offerInfoList);
        
        IDataset compOfferChaList = getOfferChaList();
        if(DataUtils.isNotEmpty(compOfferChaList))
        {
            data.put("PRODUCT_PARAM_INFO", transformOfferChaList(getProductId(), compOfferChaList));
        }
        
        IData commonInfo = getCommonData();
        if(DataUtils.isNotEmpty(commonInfo))
        {
            data.put("MEM_ROLE_B", commonInfo.getString("ROLE_CODE_B"));
            data.put("REMARK", commonInfo.getString("REMARK"));
            data.put("MEB_FILE_LIST", commonInfo.getString("MEB_FILE_LIST"));
            data.put("MEB_FILE_SHOW", commonInfo.getString("MEB_FILE_SHOW"));
        }
        
        // 集团用户信息
        IData ecSubscriber = getEcSubscriber();
        if(DataUtils.isEmpty(ecSubscriber))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到集团用户信息数据结构！");
        }
        data.put("USER_ID", ecSubscriber.getString("USER_ID"));
        
        //成员用户信息
        IData memSubscriber = getMemSubscriber();
        if(DataUtils.isEmpty(memSubscriber))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员用户信息数据结构！");
        }
        data.put("SERIAL_NUMBER", memSubscriber.getString("SERIAL_NUMBER"));
        
        return data;
    }
    
    public void setServiceName() throws Exception
    {
    	String brandCode = getBrandCode();
    	String productId = getProductId();
    	if("BOSG".equals(brandCode)){
    		 setSvcName("CS.ChangeBBossMemSVC.crtOrder");
    	}else if("10005742".equals(productId)){
    		setSvcName("SS.ChangeAdcMemElementSVC.crtOrder");
    	}
    	else{
            setSvcName(EcConstants.EC_OPER_SERVICE.CHANGE_ENTERPRISE_MEMBER.getValue());
        }

    }
}
