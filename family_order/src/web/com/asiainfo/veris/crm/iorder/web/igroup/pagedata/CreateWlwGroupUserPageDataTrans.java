package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;

/**
 * CreateWlwGroupUserPageDataTrans
 *
 * @anthor zhaozj6
 * @date 2018-01-22 16:25
 */
public class CreateWlwGroupUserPageDataTrans extends CreateGroupUserPageDataTrans {
    /**
     * 转换offer ELEMENT_INFO数据结构
     *
     * @param
     * @return
     * @throws Exception
     */
    @Override
    protected IDataset buildListOfferInfo(IDataset subOffers, String relOfferCode) throws Exception {
        IDataset listOfferInfo = new DatasetList();
        for (int j = subOffers.size(); j > 0; j--) {
            IData subOffer = subOffers.getData(j - 1);

            /*String subOfferOperCode = subOffer.getString("OPER_CODE");
            if(ACTION_UPDATE.equals(subOfferOperCode) || ACTION_EXITS.equals(subOfferOperCode))
            {
                boolean isChaValueChg = subOffer.getBoolean("IS_CHA_VALUE_CHANGE", false);
                IDataset subOfferChaList = subOffer.getDataset("OFFER_CHA_SPECS");
                if(IDataUtil.isNotEmpty(subOfferChaList) && isChaValueChg)
                {
                    subOffer.put("OPER_CODE", ACTION_UPDATE);
                }
                else
                {
                    subOffer.put("OPER_CODE", ACTION_EXITS);
                }
            }*/
            if (ACTION_EXITS.equals(subOffer.getString("OPER_CODE"))) {//如果子商品没有发生改变，则移除
                subOffers.remove(j - 1);
                continue;
            }

            IDataset OFFER_CHA_SPECS = subOffer.getDataset("OFFER_CHA_SPECS");
            IDataset ATTR_PARAM = new DatasetList();

            if(OFFER_CHA_SPECS!=null&&OFFER_CHA_SPECS.size()!=0){
                IData PLATSVC_INFO = new DataMap();
                for (int i = 0 ; i < OFFER_CHA_SPECS.size(); i++) {
                    IData CHA_SPEC = OFFER_CHA_SPECS.getData(i);
                    PLATSVC_INFO.put("pam_" + CHA_SPEC.getString("ATTR_CODE"), CHA_SPEC.getString("ATTR_VALUE"));
                }
                ATTR_PARAM.add(PLATSVC_INFO);
                ATTR_PARAM.addAll(OFFER_CHA_SPECS);
            }

            IData offerData = new DataMap();
            offerData.put("ELEMENT_ID", subOffer.getString("OFFER_CODE"));
            offerData.put("ATTR_PARAM", ATTR_PARAM);
            offerData.put("ELEMENT_TYPE_CODE", subOffer.getString("OFFER_TYPE"));
            offerData.put("INST_ID", subOffer.getString("OFFER_INS_ID", ""));
            offerData.put("PACKAGE_ID", subOffer.getString("GROUP_ID", "0")); //商品组标识
            offerData.put("PRODUCT_ID", relOfferCode);
            offerData.put("MODIFY_TAG", subOffer.getString("OPER_CODE"));

            offerData.put("START_DATE", subOffer.getString("START_DATE"));
            offerData.put("END_DATE", subOffer.getString("END_DATE"));

            listOfferInfo.add(offerData);
        }
        return listOfferInfo;
    }
}
