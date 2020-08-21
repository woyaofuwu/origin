package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedAddData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferCombinedModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferDelData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferModuleData;

public class EnterpriseModuleParserBean
{
    /**
     * 标准化销售品的入参结构[转换集团产品订购前台的参数]
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static List<GOfferCombinedAddData> parserGrpOpenOfferStrusts(GroupReqData reqData) throws Exception
    {
        String mainOfferId = reqData.getEnterpriseOfferId();
        List<ChaModData> mainOfferChaList = reqData.getCompOfferChaList();
        List<GOfferCombinedAddData> grpAddDatas = parserOrderOfferStrusts(mainOfferId, mainOfferChaList, reqData.getOfferModuleList());

        return grpAddDatas;
    }

    /**
     * 标准化销售品的入参结构[转换集团产品订购前台的参数]
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static List<GOfferCombinedAddData> parserCrtMbOfferStrusts(MemberReqData reqData) throws Exception
    {
        String grpOfferId = reqData.getEnterpriseOfferId();
        String mebBaseOfferId = UProductInfoQry.queryMemProductIdByProductId(grpOfferId);

        List<ChaModData> mainOfferChaList = reqData.getCompOfferChaList();
        List<GOfferCombinedAddData> memAddDatas = parserOrderOfferStrusts(mebBaseOfferId, mainOfferChaList, reqData.getOfferModuleList());

        // 需要将成员销售品上实例和集团的销售品实例建立绑定关系
        IDataset grpProductInfos = UserProductInfoQry.getProductInfo(reqData.getGrpUca().getUserId(), null, Route.CONN_CRM_CG);// 集团订购-1可不传，兼容老系统BBOSS

        if (IDataUtil.isNotEmpty(grpProductInfos))
        {
            IData grpProductInfoData = grpProductInfos.getData(0);
            memAddDatas.get(0).addOfferRel(grpProductInfoData.getString("PRODUCT_ID"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, grpProductInfoData.getString("INST_ID"), "4");
        }
        return memAddDatas;
    }

    /**
     * 标准化销售品的入参结构[转换产品订购前台的参数]
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static List<GOfferCombinedAddData> parserOrderOfferStrusts(String mainOfferId, List<ChaModData> mainOfferChaList, List<GOfferModuleData> offerOrderList) throws Exception
    {
        List<GOfferCombinedAddData> combinedAddDatas = new ArrayList<GOfferCombinedAddData>();    
        GOfferCombinedAddData combinedData = new GOfferCombinedAddData();

        GOfferAddData selfOfferData = new GOfferAddData();
        selfOfferData.setOfferId(mainOfferId);
        selfOfferData.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        combinedData.setSelfOfferData(selfOfferData);
        
//        // 捞取主商品的构成关系后台自动加入，前台暂时没弄
//        IDataset comRelDatas = UpcCall.queryOfferComRelOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, mainOfferId);
//        if (IDataUtil.isNotEmpty(comRelDatas))
//        {
//            for (int i = 0, size = comRelDatas.size(); i < size; i++)
//            {
//                IData comRelData = comRelDatas.getData(i);
//                GOfferAddData subOfferData = new GOfferAddData();
//                subOfferData.setOfferId(comRelData.getString("OFFER_CODE"));
//                subOfferData.setOfferType(comRelData.getString("OFFER_TYPE"));
//                subOfferData.setRelOfferId(mainOfferId);
//                
//                // 组合销售品下的原子销售品
//                combinedData.addSubAtomOfferData(subOfferData);
//            }
//        }

        // 是否存在主商品参数
        if (mainOfferChaList != null && mainOfferChaList.size() > 0)
        {
            List<ChaAddData> offerChaSpecs = new ArrayList<ChaAddData>();

            for (int i = 0, size = mainOfferChaList.size(); i < size; i++)
            {
                ChaModData chaModData = mainOfferChaList.get(i);
                offerChaSpecs.add(ChaModData.getChaAddData(chaModData));
            }
            selfOfferData.setOfferChaSpecs(offerChaSpecs);
        }

        // 遍历资费服务
        for (int i = 0, size = offerOrderList.size(); i < size; i++)
        {

            GOfferAddData subOfferData = new GOfferAddData();
            GOfferModuleData offerOrderData = offerOrderList.get(i);
            subOfferData.setOfferId(offerOrderData.getOfferId());
            subOfferData.setOfferType(offerOrderData.getOfferType());
            subOfferData.setValidDate(offerOrderData.getValidDate());
            subOfferData.setExpireDate(offerOrderData.getExpireDate());
            String relOfferId = offerOrderData.getRelOfferId();
            subOfferData.setRelOfferId(relOfferId);
            subOfferData.setGroupId(offerOrderData.getGroupId());
            
            List<ChaModData> chaSpecs = offerOrderData.getChaSpecs();
            if (chaSpecs != null && chaSpecs.size() > 0)
            {
                List<ChaAddData> offerChaSpecs = new ArrayList<ChaAddData>();

                for (int j = 0, sizej = chaSpecs.size(); j < sizej; j++)
                {
                    ChaModData chaModData = chaSpecs.get(j);
                    offerChaSpecs.add(ChaModData.getChaAddData(chaModData));
                }
                subOfferData.setOfferChaSpecs(offerChaSpecs);
            }

            if (StringUtils.isEmpty(relOfferId))
            {
                relOfferId = mainOfferId;
            }

            if (StringUtils.equals(mainOfferId, relOfferId))
            {
                // 组合销售品下的原子销售品
                combinedData.addSubAtomOfferData(subOfferData);
            }
            else
            {
                // 组合销售品下的组合销售品的原子销售品

                // 判断组合销售品下的组合销售品再是否在combinedData已经存在
                boolean existSubCombineOffer = false;
                GOfferCombinedAddData subCombineOffer = null;
                List<GOfferCombinedAddData> subCombinedDataList = combinedData.getSubCombinedOffers();

                for (GOfferCombinedAddData subCombinedData : subCombinedDataList)
                {
                    GOfferAddData subSelfData = subCombinedData.getSelfOfferData();
                    String tempOfferId = subSelfData.getOfferId();
                    if (tempOfferId.equals(relOfferId))
                    {
                        existSubCombineOffer = true;
                        subCombineOffer = subCombinedData;
                    }
                }
                // 组合销售品下的组合销售品在combinedData不存在，需要新增组合销售品
                if (!existSubCombineOffer)
                {
                    // 新增组合销售品
                    subCombineOffer = new GOfferCombinedAddData();

                    // 新增组合销售品本身的原子销售品
                    GOfferAddData subSelfOfferData = new GOfferAddData();
                    subSelfOfferData.setOfferId(relOfferId);
                    subSelfOfferData.setOfferType(offerOrderData.getRelOfferType());
                    subCombineOffer.setSelfOfferData(subSelfOfferData);

                    // 将组合销售品加到主销售品上
                    combinedData.addSubCombinedOfferData(subCombineOffer);
                    
                    // combinedAddDatas.add(subCombineOffer); // 暂时不知道怎么判断该组合销售品是否挂在主销售品下面
                }

                subCombineOffer.addSubAtomOfferData(subOfferData);
            }
        }
        
        combinedAddDatas.add(combinedData);

        return combinedAddDatas;

    }

    /**
     * 标准化销售品变更的入参结构
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static GOfferCombinedModData parserUserChgOfferStrusts(String mainOfferId, String mainOfferInstId, List<ChaModData> mainOfferChaList,
            List<GOfferModuleData> offerOrderList) throws Exception
    {
        // 组合销售品变更对象数据
        GOfferCombinedModData gocmd = new GOfferCombinedModData();

        // 组合销售品的自身销售品对象
        GOfferModData selfModData = new GOfferModData();
        gocmd.setSelfOfferModData(selfModData);

        selfModData.setOfferInsId(mainOfferInstId);
        selfModData.setOfferId(mainOfferId);
        selfModData.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        gocmd.setSelfOfferModData(selfModData);

        // 是否存在主商品参数
        if (mainOfferChaList != null && mainOfferChaList.size() > 0)
        {
            selfModData.setModChaSpecs(mainOfferChaList);
        }

        for (int i = 0, size = offerOrderList.size(); i < size; i++)
        {
            GOfferModuleData offerOrderData = offerOrderList.get(i);
            String offerId = offerOrderData.getOfferId();
            if (StringUtils.isEmpty(offerId))
                offerId = "";

            String offerInsId = offerOrderData.getOfferInsId();
            if (StringUtils.isEmpty(offerInsId))
                offerInsId = "";

            String relOfferId = offerOrderData.getRelOfferId();
            if (StringUtils.isEmpty(relOfferId))
                relOfferId = "";
            
            String relOfferType = offerOrderData.getRelOfferType();
            if (StringUtils.isEmpty(relOfferType))
                relOfferType = "";

            String relOfferInsId = offerOrderData.getRelOfferInsId();
            if (StringUtils.isEmpty(relOfferInsId))
                relOfferInsId = "";

            String operCode = offerOrderData.getOperCode();

            if (StringUtils.isEmpty(operCode))
                continue;

            boolean secAtom = false; // 是否为主销售品下的原子销售品

            GOfferCombinedModData subModCombineOffer = null;
            GOfferCombinedAddData subAddCombineOffer = null;

            if (StringUtils.equals(relOfferId, mainOfferId) || StringUtils.equals(relOfferInsId, mainOfferInstId))
            {
                secAtom = true;
            }
            else
            {
                secAtom = false;

                // 判断组合销售品下的组合销售品再是否在combinedData已经存在
                boolean existSubCombineOffer = false;

                // 如果上级销售品不为空，则上级销售品为修改销售品
                if (StringUtils.isNotEmpty(relOfferInsId))
                {
                    List<GOfferCombinedModData> subCombinedDataList = gocmd.getSubCombinedModOffers();

                    for (GOfferCombinedModData subCombinedData : subCombinedDataList)
                    {
                        GOfferModData subSelfData = subCombinedData.getSelfOfferModData();
                        String tempOfferInsId = subSelfData.getOfferInsId();
                        if (relOfferInsId.equals(tempOfferInsId))
                        {
                            existSubCombineOffer = true;
                            subModCombineOffer = subCombinedData;
                        }
                    }

                    // 组合销售品下的组合销售品在combinedData不存在，需要新增组合销售品
                    if (!existSubCombineOffer)
                    {
                        // 新增组合销售品
                        subModCombineOffer = new GOfferCombinedModData();

                        // 新增组合销售品本身的原子销售品
                        GOfferModData subSelfOfferData = new GOfferModData();
                        subSelfOfferData.setOfferInsId(relOfferInsId);
                        subSelfOfferData.setOfferId(relOfferId);
                        subSelfOfferData.setOfferType(relOfferType);
                        subModCombineOffer.setSelfOfferModData(subSelfOfferData);

                        // 将组合销售品加到主销售品上
                        gocmd.addSubCombinedModOffers(subModCombineOffer);
                    }
                }
                else
                {
                    List<GOfferCombinedAddData> subCombinedDataList = gocmd.getSubCombinedAddOffers();

                    for (GOfferCombinedAddData subCombinedData : subCombinedDataList)
                    {
                        GOfferAddData subSelfData = subCombinedData.getSelfOfferData();
                        String tempOfferId = subSelfData.getOfferId();
                        if (tempOfferId.equals(relOfferId))
                        {
                            existSubCombineOffer = true;
                            subAddCombineOffer = subCombinedData;
                        }
                    }

                    // 组合销售品下的组合销售品在combinedData不存在，需要新增组合销售品
                    if (!existSubCombineOffer)
                    {
                        // 新增组合销售品
                        subAddCombineOffer = new GOfferCombinedAddData();

                        // 新增组合销售品本身的原子销售品
                        GOfferAddData subSelfOfferData = new GOfferAddData();
                        subSelfOfferData.setOfferId(relOfferId);
                        subSelfOfferData.setOfferType(relOfferType);
                        subAddCombineOffer.addSubAtomOfferData(subSelfOfferData);

                        // 将组合销售品加到主销售品上
                        gocmd.addSubCombinedAddOffers(subAddCombineOffer);
                    }
                }
            }

            if (StringUtils.equals(operCode, TRADE_MODIFY_TAG.Add.getValue()))
            {
                GOfferAddData subOfferData = new GOfferAddData();
                subOfferData.setOfferId(offerOrderData.getOfferId());
                subOfferData.setOfferType(offerOrderData.getOfferType());
                subOfferData.setValidDate(offerOrderData.getValidDate());
                subOfferData.setExpireDate(offerOrderData.getExpireDate());
                subOfferData.setGroupId(offerOrderData.getGroupId());

                List<ChaModData> chaSpecs = offerOrderData.getChaSpecs();
                if (chaSpecs != null && chaSpecs.size() > 0)
                {
                    List<ChaAddData> offerChaSpecs = new ArrayList<ChaAddData>();
                    for (int j = 0, sizej = chaSpecs.size(); j < sizej; j++)
                    {
                        ChaModData chaModData = chaSpecs.get(j);
                        offerChaSpecs.add(ChaModData.getChaAddData(chaModData));
                    }
                    subOfferData.setOfferChaSpecs(offerChaSpecs);
                }

                if (secAtom)
                {
                    gocmd.addSubAtomAddOffers(subOfferData);
                }
                else
                {
                    if (StringUtils.isEmpty(relOfferInsId))
                    {
                        subAddCombineOffer.addSubAtomOfferData(subOfferData);
                    }
                    else
                    {
                        subModCombineOffer.addSubAtomAddOffers(subOfferData);
                    }
                }

            }

            if (StringUtils.equals(operCode, TRADE_MODIFY_TAG.MODI.getValue()))
            {

                GOfferModData subOfferData = new GOfferModData();
                subOfferData.setOfferInsId(offerInsId);
                subOfferData.setOfferId(offerId);
                subOfferData.setExpireDate(offerOrderData.getExpireDate());
                subOfferData.setValidDate(offerOrderData.getValidDate());
                subOfferData.setOfferType(offerOrderData.getOfferType());
                if (offerOrderData.isQzNeedChgOffer())
                {
                    subOfferData.setQzNeedChgOffer(true);
                }

                List<ChaModData> chaSpecs = offerOrderData.getChaSpecs();
                if (chaSpecs != null && chaSpecs.size() > 0)
                {
                    subOfferData.setModChaSpecs(chaSpecs);
                }
                if (secAtom)
                {
                    gocmd.addSubAtomModOffers(subOfferData);
                }
                else
                {
                    subModCombineOffer.addSubAtomModOffers(subOfferData);
                }
            }

            if (StringUtils.equals(operCode, TRADE_MODIFY_TAG.DEL.getValue()))
            {
                GOfferDelData subOfferData = new GOfferDelData();
                subOfferData.setOfferInsId(offerInsId);
                subOfferData.setExpireDate(offerOrderData.getExpireDate());
                subOfferData.setOfferType(offerOrderData.getOfferType());

                /*if (StringUtils.isEmpty(relOfferInsId))
                    continue;*/

                if (secAtom)
                {
                    gocmd.addSubAtomDelOffers(subOfferData);
                }
                else
                {
                    subModCombineOffer.addSubAtomDelOffers(subOfferData);
                }
            }
        }
        return gocmd;

    }

    /**
     * 标准化销售品变更的入参结构[转换集团产品变更前台的参数]
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static GOfferCombinedModData parserGrpUserChgOfferStrusts(GroupReqData reqData) throws Exception
    {
        String mainOfferId = reqData.getEnterpriseOfferId();

        IData mainProdInfo = UcaInfoQry.qryMainProdInfoByUserId(reqData.getUca().getUserId(), Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(mainProdInfo))
        {
            if (reqData.getOfferModuleList() == null || reqData.getOfferModuleList().size() == 0)
            {
                return null;
            }
            CSAppException.apperr(ProductException.CRM_PRODUCT_522, "主产品信息为空!");
        }
        String mainOfferInsId = mainProdInfo.getString("INST_ID");
        if (StringUtils.isBlank(mainOfferId))
        {
            mainOfferId = mainProdInfo.getString("PRODUCT_ID");
        }

        List<ChaModData> mainOfferChaList = reqData.getCompOfferChaList();

        return parserUserChgOfferStrusts(mainOfferId, mainOfferInsId, mainOfferChaList, reqData.getOfferModuleList());
    }

    /**
     * 标准化销售品变更的入参结构[转换集团成员变更前台的参数]
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static GOfferCombinedModData parserMemChgOfferStrusts(MemberReqData reqData) throws Exception
    {
        String grpOfferId = reqData.getEnterpriseOfferId();
        if (StringUtils.isBlank(grpOfferId))
        {
            IData grpMainProdInfo = UcaInfoQry.qryMainProdInfoByUserId(reqData.getGrpUca().getUserId(), Route.CONN_CRM_CG);
            if (IDataUtil.isEmpty(grpMainProdInfo))
            {
                // 没有订购集团产品
                return null;
            }
            grpOfferId = grpMainProdInfo.getString("PRODUCT_ID");
        }
        String mebBaseOfferId = UProductInfoQry.queryMemProductIdByProductId(grpOfferId);
        IDataset mebProductInfos = UserProductInfoQry.qryGrpMebProductByUserIdUserIdaProductId(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), mebBaseOfferId);
        if (IDataUtil.isEmpty(mebProductInfos))
        {
            if (reqData.getOfferModuleList() == null || reqData.getOfferModuleList().size() == 0)// 
            {
                return null;// 没有订购成员产品，且不需要变更元素信息，直接返回出去 
            }
            CSAppException.apperr(ProductException.CRM_PRODUCT_24);
        }
        String mebOfferInsId = mebProductInfos.getData(0).getString("INST_ID");

        List<ChaModData> mainOfferChaList = reqData.getCompOfferChaList();

        return parserUserChgOfferStrusts(mebBaseOfferId, mebOfferInsId, mainOfferChaList, reqData.getOfferModuleList());
    }

    public static IData parserCurrentOfferStruts(IData offerOrderInfo)
    {
        IData offerInfo = new DataMap();
        String offerId = offerOrderInfo.getString("OFFER_ID", "");
        String offerInsId = offerOrderInfo.getString("OFFER_INS_ID", "");
        String relOfferId = offerOrderInfo.getString("REL_OFFER_ID", "");
        String relOfferInsId = offerOrderInfo.getString("REL_OFFER_INS_ID", "");
        String operCode = offerOrderInfo.getString("OPER_CODE", "");
        String validDate = offerOrderInfo.getString("VALID_DATE");
        String expireDate = offerOrderInfo.getString("EXPIRE_DATE");
        offerInfo.put("OFFER_ID", offerId);
        offerInfo.put("OFFER_INS_ID", offerInsId);
        offerInfo.put("OPER_CODE", operCode);
        offerInfo.put("VALID_DATE", validDate);
        offerInfo.put("EXPIRE_DATE", expireDate);
        offerInfo.put("REL_OFFER_ID", relOfferId);
        offerInfo.put("REL_OFFER_INS_ID", relOfferInsId);
        return offerInfo;

    }

    public static IData parserCha(IData offerChaInfo, String offerId)
    {
        IData tempChaInfo = new DataMap();
        String chaSpecCode = offerChaInfo.getString("CHA_SPEC_CODE", "");
        String chaSpecId = offerChaInfo.getString("CHA_SPEC_ID", "");
        String value = offerChaInfo.getString("VALUE", "");
        String operCode = offerChaInfo.getString("OPER_CODE", "");
        if (StringUtils.isEmpty(chaSpecCode) && StringUtils.isEmpty(chaSpecId))
            return tempChaInfo;
        if (StringUtils.isEmpty(chaSpecId))
            chaSpecId = "212222";// add by lim veris todo

        tempChaInfo.put("CHA_SPEC_CODE", chaSpecCode);
        tempChaInfo.put("CHA_SPEC_ID", chaSpecId);
        tempChaInfo.put("VALUE", value);
        tempChaInfo.put("OPER_CODE", operCode);
        return tempChaInfo;
    }
}
