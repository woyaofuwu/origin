
package com.asiainfo.veris.crm.order.soa.person.busi.heeduforschools.order.trade;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.person.busi.heeduforschools.order.requestdata.HeEduForSchoolsReqData;
import org.apache.log4j.Logger;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 *
 * @ClassName: TopSetBoxTrade.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-5 上午9:38:18 Modification History: Date Author Version Description
 * ------------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
 */
public class HeEduForSchoolsTrade extends BaseTrade implements ITrade {
    private static Logger log = Logger.getLogger(HeEduForSchoolsTrade.class);

    public void createBusiTradeData(BusiTradeData btd) throws Exception {
        HeEduForSchoolsReqData tsbReqData = (HeEduForSchoolsReqData) btd.getRD();

        System.out.println("BatHeEduForSchoolsChangeTrans-tsbReqData"+tsbReqData);//查看是否进入方法

//        createRelationUUTradeData(btd, tsbReqData);
        createRelationXXTTradeData(btd, tsbReqData);
        createRelationBBTradeData(btd, tsbReqData);
        createMemPlatSvcData(btd, tsbReqData);
        createSVCData(btd, tsbReqData);
        createBlackWhiteData(btd, tsbReqData);
        createPricePlanData(btd, tsbReqData);
        createPayPlanData(btd, tsbReqData);
        createAttrData(btd, tsbReqData);
        System.out.println("BatHeEduForSchoolsChangeTrans-finishReqData");//完成调用

    }

    /**
     * TF_F_RELATION_UU
     * TF_B_TRADE_RELATION
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
//    private void createRelationUUTradeData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {
//        RelationTradeData uuRelaTradeData = new RelationTradeData();
//        IDataset relatinonuuInfos = RelaUUInfoQry.getRelaUUInfoBySerialNumberA(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"),null);
//        if (IDataUtil.isNotEmpty(relatinonuuInfos)) {
//            IData relauuinfoData = new DataMap();
//            relauuinfoData.putAll(relatinonuuInfos.getData(0));
//            RelationTradeData relationuuTradeData = new RelationTradeData(relauuinfoData);
//            relationuuTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
//            relationuuTradeData.setInstId(SeqMgr.getInstId());
//            btd.add(reqData.getSerialNumber(), relationuuTradeData);
//        }
//
//    }

    /**
     * TF_F_RELATION_XXT
     * TF_B_TRADE_RELATION_XXT
     *REQ201910140021_关于和教育互动业务成员批量变更归属学校的需求
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createRelationXXTTradeData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {

        IDataset relatinonxxtInfos = RelaXxtInfoQry.qryMemInfoByECANDUserIdA(reqData.getCondDataA().getString("USER_ID"),reqData.getUserId(),"915001");
        String selectType = reqData.getSelectType();
        String instId=SeqMgr.getInstId();
        if (IDataUtil.isNotEmpty(relatinonxxtInfos)) {
            IData relaxxtinfoData = new DataMap();
            relaxxtinfoData.putAll(relatinonxxtInfos.getData(0));

            if ("1".equals(selectType)){
                RelationxxtTradeData relationxxtTradeDataModify = new RelationxxtTradeData(relaxxtinfoData);//修改
                relationxxtTradeDataModify.setend_date(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                relationxxtTradeDataModify.setmodify_tag("2");
                btd.add(reqData.getSerialNumber(), relationxxtTradeDataModify);//修改
            }else if ("2".equals(selectType)){
                RelationxxtTradeData relationxxtTradeDataAdd = new RelationxxtTradeData(relaxxtinfoData);//变更学校归属于B
                relationxxtTradeDataAdd.setstart_date(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                relationxxtTradeDataAdd.setmodify_tag("0");
                relationxxtTradeDataAdd.setec_user_id(reqData.getCondDataB().getString("USER_ID"));
                relationxxtTradeDataAdd.setinst_id(SeqMgr.getInstId());
                relationxxtTradeDataAdd.setrela_inst_id(instId);//邦定优惠instid对应
                btd.add(reqData.getSerialNumber(), relationxxtTradeDataAdd);//新增
            }
            createDiscntData(btd, reqData,instId);//邦定优惠instid



        }
    }

    /**
     * TF_F_RELATION_BB
     * TF_B_TRADE_RELATION_BB
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createRelationBBTradeData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {
        IDataset UserBBRelaInfos = RelaBBInfoQry.qryRelationBBAllBySerialNumberA(reqData.getCondDataA().getString("USER_ID"),reqData.getUserId());
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(UserBBRelaInfos)) {
            IData BBRelaInfo = new DataMap();
            BBRelaInfo.putAll(UserBBRelaInfos.getData(0));
            if ("1".equals(selectType)) {
                RelationBBTradeData TradeDataBModify = new RelationBBTradeData(BBRelaInfo);//修改
                TradeDataBModify.setModifyTag("2");
                TradeDataBModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                btd.add(reqData.getSerialNumber(), TradeDataBModify);
            }else if ("2".equals(selectType)){
                RelationBBTradeData TradeDataBAdd = new RelationBBTradeData(BBRelaInfo);//新增
                TradeDataBAdd.setModifyTag("0");
                TradeDataBAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                TradeDataBAdd.setUserIdA(reqData.getCondDataB().getString("USER_ID"));//变更学校归属于B
                IData users = UcaInfoQry.qryUserInfoByUserId(reqData.getCondDataB().getString("USER_ID"));
                TradeDataBAdd.setSerialNumberA(users.getString("SERIAL_NUMBER"));
                TradeDataBAdd.setInstId(SeqMgr.getInstId());
                btd.add(reqData.getSerialNumber(), TradeDataBAdd);
            }


        }

    }


    /**
     * TF_F_USER_GRP_MEB_PLATSVC
     * TF_B_TRADE_GRP_MEB_PLATSVC
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createMemPlatSvcData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {
        IDataset mebPlatSvcInfos = UserGrpMebPlatSvcInfoQry.getMemPlatSvcByecUserIdServiceId(reqData.getCondDataA().getString("USER_ID"),reqData.getUserId(),"915001");
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(mebPlatSvcInfos)) {
            IData mebPlatSvcData = new DataMap();
            mebPlatSvcData.putAll(mebPlatSvcInfos.getData(0));
            if ("1".equals(selectType)) {
                GrpMemPlatSvcTradeData grpMemPlatSvcTradeDataModify = new GrpMemPlatSvcTradeData(mebPlatSvcData);//修改
                grpMemPlatSvcTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                grpMemPlatSvcTradeDataModify.setModifyTag("2");
                btd.add(reqData.getSerialNumber(), grpMemPlatSvcTradeDataModify);
            }else if ("2".equals(selectType)){
                GrpMemPlatSvcTradeData grpMemPlatSvcTradeDataAdd = new GrpMemPlatSvcTradeData(mebPlatSvcData);//新增
                grpMemPlatSvcTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                grpMemPlatSvcTradeDataAdd.setModifyTag("0");
                grpMemPlatSvcTradeDataAdd.setInstId(SeqMgr.getInstId());
                grpMemPlatSvcTradeDataAdd.setEcUserId(reqData.getCondDataB().getString("USER_ID"));
                IData users = UcaInfoQry.qryUserInfoByUserId(reqData.getCondDataB().getString("USER_ID"));
                grpMemPlatSvcTradeDataAdd.setEcSerialNumber(users.getString("SERIAL_NUMBER"));
                IDataset qryMebPlatSvcInfos = UserGrpMebPlatSvcInfoQry.getMemPlatSvcByecUserIdServiceId(reqData.getCondDataB().getString("USER_ID"),"915001");
                grpMemPlatSvcTradeDataAdd.setServCode(qryMebPlatSvcInfos.getData(0).getString("SERV_CODE"));
                btd.add(reqData.getSerialNumber(), grpMemPlatSvcTradeDataAdd);
            }

        }
    }
    /**
     * TF_F_USER_discnt
     * tf_b_trade_discnt
     *优惠数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createDiscntData(BusiTradeData btd, HeEduForSchoolsReqData reqData,String instId) throws Exception {

        //		IData users = UcaInfoQry.qryUserInfoBySn(inputParam.getString("SERIAL_NUMBER"));
        IDataset useDiscnts = UserDiscntInfoQry.getDiscntByUserIdAndUserId(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"));
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(useDiscnts)) {
            IData useDiscntData = new DataMap();
            useDiscntData.putAll(useDiscnts.getData(0));
            if ("1".equals(selectType)) {
                DiscntTradeData discntTradeDataModify = new DiscntTradeData(useDiscntData);//修改
                discntTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                discntTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), discntTradeDataModify);
            }else if ("2".equals(selectType)){
                DiscntTradeData discntTradeDataAdd = new DiscntTradeData(useDiscntData);
                discntTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                discntTradeDataAdd.setModifyTag("0");
                discntTradeDataAdd.setInstId(instId);
                discntTradeDataAdd.setUserIdA(reqData.getCondDataB().getString("USER_ID"));
                btd.add(reqData.getUca().getSerialNumber(), discntTradeDataAdd);
            }
            createOfferRelData(btd, reqData,useDiscnts.getData(0).getString("INST_ID"),instId);

        }
    }

    /**
     * tf_b_trade_svc
     * tf_f_use_svc
     *svc数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createSVCData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {

        //		IData users = UcaInfoQry.qryUserInfoBySn(inputParam.getString("SERIAL_NUMBER"));
        IDataset useSvcs = UserSvcInfoQry.getUserSvcForByUserIdA(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"),"915001");
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(useSvcs)) {
            IData useSvcData = new DataMap();
            useSvcData.putAll(useSvcs.getData(0));
            if ("1".equals(selectType)) {
                SvcTradeData svcTradeDataModify = new SvcTradeData(useSvcData);//修改
                svcTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                svcTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), svcTradeDataModify);
            }else if ("2".equals(selectType)){
                SvcTradeData svcTradeDataAdd = new SvcTradeData(useSvcData);
                svcTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                svcTradeDataAdd.setModifyTag("0");
                svcTradeDataAdd.setInstId(SeqMgr.getInstId());
                svcTradeDataAdd.setIsNeedPf("0");
                svcTradeDataAdd.setUserIdA(reqData.getCondDataB().getString("USER_ID"));
//                svcTradeDataAdd.setElementId(reqData.getCondDataB().getString("SERVICE_ID"));
                btd.add(reqData.getUca().getSerialNumber(), svcTradeDataAdd);
            }


        }
    }


    /**
     * TF_F_USER_BLACKWHITE
     * TF_B_TRADE_BLACKWHITE
     * tf_f_use_svc
     *svc数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createBlackWhiteData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {
        IDataset blackWhiteSvcs = UserBlackWhiteInfoQry.getBlackWhiteInfoByUseridASerialNumberIData(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"),"915001");
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(blackWhiteSvcs)) {
            IData useBlackWhiteData = new DataMap();
            useBlackWhiteData.putAll(blackWhiteSvcs.getData(0));
            if ("1".equals(selectType)) {
                BlackWhiteTradeData blackWhiteTradeDataModify = new BlackWhiteTradeData(useBlackWhiteData);//修改
                blackWhiteTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                blackWhiteTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), blackWhiteTradeDataModify);
            }else if ("2".equals(selectType)){
                BlackWhiteTradeData blackWhiteTradeDataAdd = new BlackWhiteTradeData(useBlackWhiteData);
                blackWhiteTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                blackWhiteTradeDataAdd.setModifyTag("0");
                blackWhiteTradeDataAdd.setInstId(SeqMgr.getInstId());
                blackWhiteTradeDataAdd.setEcUserId(reqData.getCondDataB().getString("USER_ID"));
                IData users = UcaInfoQry.qryUserInfoByUserId(reqData.getCondDataB().getString("USER_ID"));
                blackWhiteTradeDataAdd.setEcSerialNumber(users.getString("SERIAL_NUMBER"));
                IDataset qryMebPlatSvcInfos = UserGrpMebPlatSvcInfoQry.getMemPlatSvcByecUserIdServiceId(reqData.getCondDataB().getString("USER_ID"),"915001");
                blackWhiteTradeDataAdd.setServCode(qryMebPlatSvcInfos.getData(0).getString("SERV_CODE"));
                btd.add(reqData.getUca().getSerialNumber(), blackWhiteTradeDataAdd);
            }


        }
    }



    /**
     * TF_F_USER_OFFER_REL
     * TF_B_TRADE_OFFER_REL
     * tf_f_use_svc
     *svc数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createOfferRelData(BusiTradeData btd, HeEduForSchoolsReqData reqData,String qryInstId,String  instId) throws Exception {
        IDataset offerRelSvcs = UserOfferRelInfoQry.qryUserAllOfferRelByUserIdAndRlUserId(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"),reqData.getCondDataA().getString("GROUP_ID"),qryInstId);
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(offerRelSvcs)) {
            IData useOfferRelData = new DataMap();
            useOfferRelData.putAll(offerRelSvcs.getData(0));
            if ("1".equals(selectType)) {
                OfferRelTradeData offerRelTradeDataModify = new OfferRelTradeData(useOfferRelData);//修改
                offerRelTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                offerRelTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), offerRelTradeDataModify);
            }else if ("2".equals(selectType)){
                OfferRelTradeData offerRelTradeDataAdd = new OfferRelTradeData(useOfferRelData);
                offerRelTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                offerRelTradeDataAdd.setModifyTag("0");
                offerRelTradeDataAdd.setInstId(SeqMgr.getInstId());
                offerRelTradeDataAdd.setRelUserId(reqData.getCondDataB().getString("USER_ID"));
                offerRelTradeDataAdd.setRelOfferInsId(instId);
                btd.add(reqData.getUca().getSerialNumber(), offerRelTradeDataAdd);
            }


        }
    }
    /**
     * TF_F_USER_ATTR
     * TF_B_TRADE_ATTR
     *svc数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createAttrData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {
        IDataset attrSvcs = UserAttrInfoQry.getUserAttrByRelaInstIdLikeAttrCode(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"));
        String selectType = reqData.getSelectType();
        String relaInstId=SeqMgr.getInstId();
        if (IDataUtil.isNotEmpty(attrSvcs)) {
            IData useAttrData = new DataMap();
            useAttrData.putAll(attrSvcs.getData(0));
            if ("1".equals(selectType)) {
                AttrTradeData attrTradeDataModify = new AttrTradeData(useAttrData);//修改
                attrTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                attrTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), attrTradeDataModify);
            }else if ("2".equals(selectType)){
                AttrTradeData attrTradeDataAdd = new AttrTradeData(useAttrData);
                attrTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                attrTradeDataAdd.setModifyTag("0");
                attrTradeDataAdd.setInstId(SeqMgr.getInstId());
                attrTradeDataAdd.setRelaInstId(relaInstId);
                btd.add(reqData.getUca().getSerialNumber(), attrTradeDataAdd);
            }

            createProductData(btd,reqData, attrSvcs.getData(0).getString("RELA_INST_ID"),relaInstId);

        }
    }

    /**
     * TF_B_TRADE_PRICE_PLAN
     * TF_F_USER_PRICE_PLAN
     *svc数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createPricePlanData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {
        IDataset pricePlanSvcs = BofQuery.queryUserAllPricePlanByUserIdAndofferInsId(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"));
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(pricePlanSvcs)) {
            IData usePricePlanData = new DataMap();
            usePricePlanData.putAll(pricePlanSvcs.getData(0));
            if ("1".equals(selectType)) {
                PricePlanTradeData pricePlanTradeDataModify = new PricePlanTradeData(usePricePlanData);//修改
                pricePlanTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                pricePlanTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), pricePlanTradeDataModify);
            }else if ("2".equals(selectType)){
                PricePlanTradeData pricePlanTradeDataAdd = new PricePlanTradeData(usePricePlanData);
                pricePlanTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                pricePlanTradeDataAdd.setModifyTag("0");
                pricePlanTradeDataAdd.setInstId(SeqMgr.getInstId());
                pricePlanTradeDataAdd.setUserIdA(reqData.getCondDataB().getString("USER_ID"));
                btd.add(reqData.getUca().getSerialNumber(), pricePlanTradeDataAdd);
            }


        }
    }

    /**
     * TF_B_TRADE_PRODUCT
     * TF_F_USER_PRODUCT
     *svc数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createProductData(BusiTradeData btd, HeEduForSchoolsReqData reqData,String qryInstId,String instId) throws Exception {
        IDataset productSvcs = UserProductInfoQry.qryUserMainProdInfoByUserIdAndUserIdAProductId(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"),qryInstId);
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(productSvcs)) {
            IData useProductData = new DataMap();
            useProductData.putAll(productSvcs.getData(0));
            if ("1".equals(selectType)) {
                ProductTradeData productTradeDataModify = new ProductTradeData(useProductData);//修改
                productTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                productTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), productTradeDataModify);
            }else if ("2".equals(selectType)){
                ProductTradeData productTradeDataAdd = new ProductTradeData(useProductData);//修改
                productTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                productTradeDataAdd.setModifyTag("0");
                productTradeDataAdd.setInstId(instId);
                productTradeDataAdd.setUserIdA(reqData.getCondDataB().getString("USER_ID"));
                productTradeDataAdd.setProductId(reqData.getCondDataB().getString("PRODUCT_ID"));
                btd.add(reqData.getUca().getSerialNumber(), productTradeDataAdd);
            }

        }
    }


    /**
     * TF_B_TRADE_USER_PAYPLAN
     * TF_F_USER_PAYPLAN
     *svc数据变更
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createPayPlanData(BusiTradeData btd, HeEduForSchoolsReqData reqData) throws Exception {
        IDataset payPlanSvcs = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(reqData.getUserId(),reqData.getCondDataA().getString("USER_ID"));
        String selectType = reqData.getSelectType();
        if (IDataUtil.isNotEmpty(payPlanSvcs)) {
            IData usePayPlanData = new DataMap();
            usePayPlanData.putAll(payPlanSvcs.getData(0));
            if ("1".equals(selectType)) {
                UserPayPlanTradeData   userPayPlanTradeDataModify = new UserPayPlanTradeData(usePayPlanData);//修改
                userPayPlanTradeDataModify.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                userPayPlanTradeDataModify.setModifyTag("2");
                btd.add(reqData.getUca().getSerialNumber(), userPayPlanTradeDataModify);
            }else if ("2".equals(selectType)){
                UserPayPlanTradeData   userPayPlanTradeDataAdd = new UserPayPlanTradeData(usePayPlanData);//修改
                userPayPlanTradeDataAdd.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                userPayPlanTradeDataAdd.setModifyTag("0");
                userPayPlanTradeDataAdd.setPlanId(SeqMgr.getPlanId());
                userPayPlanTradeDataAdd.setUserIdA(reqData.getCondDataB().getString("USER_ID"));
                btd.add(reqData.getUca().getSerialNumber(), userPayPlanTradeDataAdd);
            }


        }
    }




}
