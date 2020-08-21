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

public class ChangeImsvpnGroupMemberPageDataTrans extends PageDataTrans {
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

            //取得成员商品的商品特征值
            IDataset memOfferChaSpecs=memberOffers.getData(0).getDataset("OFFER_CHA_SPECS");
            if(DataUtils.isNotEmpty(memOfferChaSpecs)){
                IDataset resInfos=new DatasetList();
                for(int i = 0, size = memOfferChaSpecs.size(); i < size; i++)
                {
                    if(StringUtils.equals(memOfferChaSpecs.getData(i).getString("ATTR_CODE"),"SHORT_CODE")){//如果有短号
                        String shortCode=memOfferChaSpecs.getData(i).getString("ATTR_VALUE");//新增短号
                        String shortCodeOld=memOfferChaSpecs.getData(i).getString("OLD_ATTR_VALUE");//删除旧短号
                        if(StringUtils.isNotEmpty(shortCodeOld)&&!StringUtils.equals(shortCode,shortCodeOld)){//如果新旧短号变更了，OLD_ATTR_VALUE不为空
                            IData resInfo = new DataMap();
                            resInfo.put("RES_TYPE_CODE", "S");
                            resInfo.put("RES_CODE", shortCode);
                            resInfo.put("MODIFY_TAG","0");
                            resInfos.add(resInfo);

                            IData resInfoOld = new DataMap();
                            resInfoOld.put("RES_TYPE_CODE", "S");
                            resInfoOld.put("RES_CODE", shortCodeOld);
                            resInfoOld.put("MODIFY_TAG","1");
                            resInfos.add(resInfoOld);
                        }

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
