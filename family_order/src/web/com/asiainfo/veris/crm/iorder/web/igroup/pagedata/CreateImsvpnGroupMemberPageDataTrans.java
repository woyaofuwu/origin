package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;

public class CreateImsvpnGroupMemberPageDataTrans extends PageDataTrans {

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
            data.put("PLAN_TYPE_CODE", commonInfo.getString("PLAN_TYPE_CODE"));
            data.put("FEE_TYPE", commonInfo.getString("FEE_TYPE"));
            data.put("LIMIT_TYPE", commonInfo.getString("LIMIT_TYPE"));
            data.put("LIMIT", commonInfo.getString("LIMIT"));
            data.put("EFFECT_NOW",commonInfo.getString("EFFECT_NOW")); //0-下月；1-立即
            data.put("IF_BOOKING", "0".equals(commonInfo.getString("EFFECT_NOW"))?true:false); //0-下月；1-立即
            data.put("REMARK", commonInfo.getString("REMARK"));
            data.put("MEB_FILE_LIST", commonInfo.getString("MEB_FILE_LIST"));
            data.put("MEB_FILE_SHOW", commonInfo.getString("MEB_FILE_SHOW"));
            data.put("PLAN_TYPE_CODE", commonInfo.getString("PLAN_TYPE_CODE"));

            //取得成员商品的商品特征值
            IDataset memOfferChaSpecs=memberOffers.getData(0).getDataset("OFFER_CHA_SPECS");
            if(DataUtils.isNotEmpty(memOfferChaSpecs)){
                IDataset resInfos=new DatasetList();
                for(int i = 0, size = memOfferChaSpecs.size(); i < size; i++)
                {
                    if(StringUtils.equals(memOfferChaSpecs.getData(i).getString("ATTR_CODE"),"SHORT_CODE")){//如果有短号
                        String shortCode=memOfferChaSpecs.getData(i).getString("ATTR_VALUE");
                        IData resInfo = new DataMap();
                        resInfo.put("RES_TYPE_CODE", "S");
                        resInfo.put("RES_CODE", shortCode);
                        resInfos.add(resInfo);
                        break;
                    }
                }
                commonInfo.put("RES_INFO",resInfos);
            }

            IDataset resInfoList = transformResInfo(commonInfo);
            if(DataUtils.isNotEmpty(resInfoList))
            {
                data.put("RES_INFO", resInfoList);
            }
        }

        // 集团用户信息
        IData ecSubscriber =  getEcSubscriber();
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
        if("BOSG".equals(brandCode)){
            setSvcName("CS.CreateBBossMemSVC.crtOrder");
        }else{
            setSvcName(EcConstants.EC_OPER_SERVICE.CREATE_ENTERPRISE_MEMBER.getValue());
        }

    }
}
