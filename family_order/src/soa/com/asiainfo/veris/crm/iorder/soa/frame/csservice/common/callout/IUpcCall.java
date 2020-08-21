package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.callout;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;

/**
 * 集客订单中心使用
 * @author sungq3
 *
 */
public class IUpcCall
{
    public static IDataset queryOfferChaAndValByCond(String offerId, String attrObj, String fieldName, String mgmtDistrictId) throws Exception
    {
        return IUpcCallIntf.queryOfferChaAndValByCond(offerId, attrObj, fieldName, mgmtDistrictId);
    }
    
    /**
     * 查询集团成员产品
     * 
     * @param grpOfferId
     * @return
     * @throws Exception
     */
    public static IDataset queryMebOffersByEcOfferId(String ecOfferId) throws Exception
    {
        return IUpcCallIntf.queryOfferJoinRelByOfferId(ecOfferId, UpcConst.PM_OFFER_JOIN_REL_TYPE_ECMEB, UpcConst.SELECT_FLAG_MUST_CHOOSE);
    }

    public static IDataset queryOfferByOfferCode(String ecOfferId) throws Exception {
        return IUpcCallIntf.queryOfferJoinRelByOfferId(ecOfferId, UpcConst.PM_OFFER_JOIN_REL_TYPE_ECMEB, UpcConst.SELECT_FLAG_MUST_CHOOSE);
    }


    public static IData queryOfferByOfferCodeNew(String offerCode) throws Exception {
        return IUpcCallIntf.queryOfferByOfferCode(offerCode, null);
    }

    public static IDataset queryOfferGroups(String offerId, String mgmtDistrict) throws Exception {
            return IUpcCallIntf.queryOfferGroups(offerId, mgmtDistrict);
    }
    public static String getUseTagByProductId (String offerCode) throws Exception {
        return IUpcCallIntf.getUseTagByProductId(offerCode);

    }

    public static IDataset queryOfferJoinRelAndOfferByOfferId(String offerId , String relType , String selectFlag , String queryCha) throws Exception{
        return IUpcCallIntf.queryOfferJoinRelAndOfferByOfferId(offerId, relType, selectFlag, queryCha);
    }


    public static IDataset qryOfferByOfferIdRelOfferId(String offerCode, String offerType, String relOfferCode, String relOfferType, String queryCha) throws Exception {
        return IUpcCallIntf.qryOfferByOfferIdRelOfferId(offerCode,offerType,relOfferCode,relOfferType,queryCha);
    }
}
