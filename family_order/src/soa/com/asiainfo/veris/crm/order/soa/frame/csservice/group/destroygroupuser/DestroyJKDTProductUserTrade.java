
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;


import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossIAGWCloudMASDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossPayBizInfoDealbean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class DestroyJKDTProductUserTrade extends DestroyGroupUser {
    protected DestroyBBossUserReqData reqData = null;
    
    // XXX 处理基类取不到业务类型
    @Override
    public String setTradeTypeCode() throws Exception
    {
        // 1- 继承基类处理
        super.setTradeTypeCode();

       return "2346";
    }

    /*
     * @description 注销产品用户表信息(重写基类方法，基类方法只适用于普通集团产品)
     * @author xunyl
     * @date 2014-04-24
     */
    protected void actTradePrd() throws Exception {
        // 1- 查询用户产品信息
        String merchUserId = reqData.getOUT_MERCH_INFO().getString("USER_ID");
        IDataset userProductList = UserProductInfoQry.getProductInfo(reqData.getUca().getUserId(), merchUserId, Route.CONN_CRM_CG);
        IDataset productParam = reqData.cd.getProductParamList(reqData.getUca().getProductId());

        if (IDataUtil.isEmpty(userProductList)) {
            return;
        }

        IDataset userProductAttrDataset = new DatasetList();
        IDataset userProductAttrDatalist = new DatasetList();

        for (int i = 0, row = userProductList.size(); i < row; i++) {
            IData userProductData = userProductList.getData(i);

            userProductData.put("END_DATE", getAcceptTime());
            userProductData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

            String instId = userProductData.getString("INST_ID");

            // 处理产品参数信息
            if (StringUtils.isNotBlank(instId)) {
                // 查询产品参数信息
                IDataset userProductAttrList = UserAttrInfoQry.qryUserAttrByUserRelaInstId(reqData.getUca().getUserId(), instId);

                // 注销产品参数信息
                if (IDataUtil.isNotEmpty(userProductAttrList)) {
                    for (int j = 0, jRow = userProductAttrList.size(); j < jRow; j++) {
                        IData userProductAttrData = userProductAttrList.getData(j);
                        if (isExistAttr(userProductAttrData.getString("ATTR_CODE"), productParam)) {
                            continue;
                        }
                        userProductAttrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userProductAttrData.put("END_DATE", userProductData.getString("END_DATE"));
                        userProductAttrData.put("IS_NEED_PF", "0");// 1或者是空： 发指令,0不发指令


                        userProductAttrDatalist.add(userProductAttrData);
                    }

                    userProductAttrDataset.addAll(userProductAttrDatalist);
                }
            }
        }

        super.addTradeAttr(userProductAttrDataset);

        super.addTradeProduct(userProductList);
    }

    public boolean isExistAttr(String attrCode, IDataset attrset) throws Exception {
        for (int i = 0, sizeI = attrset.size(); i < sizeI; i++) {
            IData attrRow = attrset.getData(i);
            if (attrCode.equals(attrRow.getString("ATTR_CODE"))) {
                return true;
            }
        }
        return false;
    }

    /*
     * @description 修改BBOSS产品,产品资费和BB关系台帐数据（生成台帐后）
     * @author xunyl
     * @date 2013-04-18
     */
    public void actTradeSub() throws Exception {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记业务台帐BBOSS产品用户订购表子表、优惠子表
        infoRegDataEntireMerchP();
//        super.addTradeEcrecepProduct(reqData.cd.getMerchp());

        // 3- 登记产品参数表(普通集团产品注销时不需要写参数，但BBOSS某些商品需要填写注销原因)
        infoRegDataAttr();

        // 4- 登记other表，服务开通侧用
        infoRegDataOther();

        // 5- 解除商产品间的BB关系
        IData reletionData = this.getReletionBBMap();
        super.addTradeRelationBb(reletionData);

        // 6-特殊业务处理特殊表
        infoRegDataSpecial();
        //绑定7810服务
        this.addTradeSvc();
        this.addTradeSvcstate();

    }
    /**
     * 处理台帐服务表的数据
     * 
     * @param object
     * @throws Exception
     */
    protected void addTradeSvc() throws Exception
    {
    	IData svcData = new DataMap();
        svcData.put("USER_ID", reqData.getUca().getUserId());
        svcData.put("USER_ID_A", reqData.getOUT_MERCH_INFO().getString("USER_ID"));
        svcData.put("ELEMENT_ID", "7810");//与服开商定服务
        svcData.put("SERVICE_ID", "7810");//与服开商定服务
        svcData.put("PACKAGE_ID", "");
        svcData.put("MAIN_TAG", "0");
        svcData.put("INST_ID", SeqMgr.getInstId());
        svcData.put("PRODUCT_ID", reqData.getUca().getProductId());
        svcData.put("MODIFY_TAG", "1");
        svcData.put("SERV_PARA1", "");
        svcData.put("SERV_PARA2", "");
        svcData.put("SERV_PARA3", "");
        svcData.put("SERV_PARA4", "");
        svcData.put("SERV_PARA5", "");
        svcData.put("SERV_PARA6", "");
        svcData.put("SERV_PARA7", "");
        svcData.put("SERV_PARA8", "");
        svcData.put("START_DATE", SysDateMgr.getSysTime());
        svcData.put("END_DATE", SysDateMgr.getTheLastTime());
        super.addTradeSvc(svcData);
    }

    protected void  addTradeSvcstate() throws Exception{
    	IData svcData = new DataMap();
        svcData.put("USER_ID", reqData.getUca().getUserId());
        svcData.put("SERVICE_ID", "7810");//与服开商定服务
        svcData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        svcData.put("MAIN_TAG", "0");
        svcData.put("INST_ID", SeqMgr.getInstId());
        svcData.put("STATE_CODE", "1");
        svcData.put("MODIFY_TAG", "1");
        svcData.put("START_DATE", SysDateMgr.getSysTime());
        svcData.put("END_DATE", SysDateMgr.getTheLastTime());
        super.addTradeSvcstate(svcData);
    	
    }
    
    /*
     * @description 校验产品参数附件是否上传
     * @author xunyl
     * @date 2013-12-03
     */
    protected String checkParamFileUpLoad(String attrCode, String attrValue) throws Exception {
        // 1- 检查参数是否为附件类型
        String paramType = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_BBOSS_ATTR", new String[]
                {"ATTR_CODE"}, "EDIT_TYPE", new String[]
                {attrCode});
        if (StringUtils.equals("UPLOAD", paramType) && !"6".equals(CSBizBean.getVisit().getInModeCode())) {
            attrValue = GrpCommonBean.checkFileState(attrValue);
        }
        return attrValue;
    }

    /*
     * @description 根据商品用户编号查询其对应的MERCHP表所有子产品信息
     * @author xunyl
     * @date 2013-04-18
     */
    protected IData getGrpMerchAndDiscntInfo() throws Exception {
        // 1- 定义返回对象
        IData merchpAndDstMap = new DataMap();

        // 2- 获取产品用户编号
        String userId = reqData.getUca().getUserId();

        // 3- 添加Merchp表对象
        IData grpMerchpInfo = new DataMap();
        IDataset grpMerchpInfoList = UserEcrecepProductInfoQry.qryMerchpInfoByUserIdMerchSpecProductSpecStatus(userId, null, null, null);
        if (null != grpMerchpInfoList && grpMerchpInfoList.size() > 0) {
            grpMerchpInfo = grpMerchpInfoList.getData(0);
        }
        merchpAndDstMap.put("PRODUCT_INFO", grpMerchpInfo);

        // 4- 添加产品资费对象
        IData grpMerchpDstInfo = new DataMap();
        IDataset grpMerchpDstInfoList = UserGrpMerchpDiscntInfoQry.qryMerchpDiscntByUseridMerchScPrdouctScProductDc(userId, null, null, null, null);
        if (null != grpMerchpDstInfoList && grpMerchpDstInfoList.size() > 0) {
            grpMerchpDstInfo = grpMerchpDstInfoList.getData(0);
        }
        merchpAndDstMap.put("PRODUCT_DST", grpMerchpDstInfo);

        // 5- 返回结果
        return merchpAndDstMap;
    }

    /*
     * @description 解除商品与子产品间的BB关系
     * @author xunyl
     * @date 2013-04-24
     */
    protected IData getReletionBBMap() throws Exception {
        // 获取BB关系类型
        String merchpId = reqData.getUca().getProductId();
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", merchpId, true);
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(reqData.getUca().getUserId(), merchRelationTypeCode, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(relaBBInfoList)) {
            IData relaBBInfo = relaBBInfoList.getData(0);
            relaBBInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            relaBBInfo.put("END_DATE", getAcceptTime());
            return relaBBInfo;
        }
        return new DataMap();
    }

    /*
     * @description 业务台帐BBOSS产品用户订购表子表、优惠子表
     * @author xunyl
     * @date 2013-04-18
     */
    protected BaseReqData getReqData() throws Exception {
        return new DestroyBBossUserReqData();
    }

    /**
     * chenyi 2014-4-15 处理流量统付业务 将账务所需数据登记GrpCenPay表同步账务
     *
     * @throws Exception
     * @returnv
     */
    private void infoGrpCenPay() throws Exception {
        String userid = reqData.getUca().getUserId();
        IDataset cenPayData = BbossPayBizInfoDealbean.getDELgrpcenpay(userid);

        this.addTradeGrpCenpay(cenPayData);

    }

    /*
     * @descripiton 登记注销时的特殊属性
     * @author chenyi
     * @date 2013-08-26 特殊属性 1.如果资料表有数据 一条新增，一条删除发服开 2.如果没有直接新增发服开 普通属性 直接修改enddate 完工
     */
    public void infoRegDataAttr() throws Exception {
        // 1获取页面特殊参数
        IDataset productParam = reqData.cd.getProductParamList(reqData.getUca().getProductId());

        IDataset dataset = new DatasetList();

        // 2处理特殊属性

        //2.1反向自动注销，需要补全必填字段（如：对讲业务：注销原因）
        setSpecialAttrDate(productParam);

        // 如果对应参数在资料表有值，则需要一条新增，一条删除
        if (IDataUtil.isNotEmpty(productParam)) {
            for (int i = 0; i < productParam.size(); i++) {
                IData paramData = productParam.getData(i);
                String attrCode = paramData.getString("ATTR_CODE");
                String attrValue = paramData.getString("ATTR_VALUE", "");
                String attrState = paramData.getString("STATE");

                IData map = new DataMap();

                map.put("INST_TYPE", "P");
                map.put("RELA_INST_ID", SeqMgr.getInstId());
                map.put("IS_NEED_PF", "1");
                map.put("INST_ID", SeqMgr.getInstId());
                map.put("ATTR_CODE", attrCode);
                if (StringUtils.equals(attrState, "ADD")) {
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    map.put("ATTR_VALUE", checkParamFileUpLoad(attrCode, attrValue));
                    map.put("END_DATE", SysDateMgr.getTheLastTime());
                    map.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());
                } else if (StringUtils.equals(attrState, "DEL")) {
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    map.put("ATTR_VALUE", attrValue);
                    map.put("END_DATE", SysDateMgr.getSysDate());
                    map.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());
                }
                map.put("START_DATE", getAcceptTime());
                map.put("USER_ID", reqData.getUca().getUser().getUserId());
                map.put("RSRV_STR4", paramData.getString("ATTR_GROUP"));
                map.put("RSRV_STR3", paramData.getString("ATTR_NAME"));

                dataset.add(map);
            }
        }

        this.addTradeAttr(dataset);
    }

    /*
     * @description 业务台帐BBOSS产品用户订购表子表、优惠子表
     * @author xunyl
     * @date 2013-04-18
     */
    public void infoRegDataEntireMerchP() throws Exception
    {
        // 1- 获取BBOSS侧产品及产品资费信息
        IData merchpAndDstInfo = this.getGrpMerchAndDiscntInfo();

        // 2- 如果产品或者资费信息不存在，则直接退出
        if (merchpAndDstInfo.isEmpty())
        {
            return;
        }

        // 3- 修改BBOSS侧产品信息
        IData grpMerchpInfo = merchpAndDstInfo.getData("PRODUCT_INFO");
        if (IDataUtil.isNotEmpty(grpMerchpInfo))
        {
            grpMerchpInfo.put("STATUS", "D");
            grpMerchpInfo.put("END_DATE", getAcceptTime());
            grpMerchpInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            grpMerchpInfo.put("RSRV_STR1", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue());// 产品操作类型
            if(StringUtils.isNotBlank(reqData.getOUT_MERCH_INFO().getString("PRODUCT_OFFER_ID"))){
                grpMerchpInfo.put("PRODUCT_OFFER_ID", reqData.getOUT_MERCH_INFO().getString("PRODUCT_OFFER_ID"));
            }
            if(StringUtils.isNotBlank(reqData.getOUT_MERCH_INFO().getString("PRODUCT_ORDER_ID"))){
                grpMerchpInfo.put("PRODUCT_ORDER_ID", reqData.getOUT_MERCH_INFO().getString("PRODUCT_ORDER_ID"));
            }
            reqData.cd.putMerchp(IDataUtil.idToIds(grpMerchpInfo));
        }

        this.addTradeEcrecepProduct(reqData.cd.getMerchp());
        //登记集客受理大厅流程信息
        IData epData = GrpCommonBean.actEcrecepProcedure(reqData.getUca().getUser().getUserId(), SeqMgr.getInstId(), reqData.getUca().getUser().getSerialNumber(), grpMerchpInfo.getString("PRODUCT_ORDER_ID"), grpMerchpInfo.getString("PRODUCT_OFFER_ID", ""), "2", "D", "D");
        this.addTradeEcrecepProcedure(epData);

        // 3- 修改BBOSS侧产品信息
        IData grpMerchpDISInfo = merchpAndDstInfo.getData("PRODUCT_DST");
        if (IDataUtil.isNotEmpty(grpMerchpDISInfo))
        {
            grpMerchpDISInfo.put("END_DATE", getAcceptTime());
            grpMerchpDISInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            grpMerchpDISInfo.put("RSRV_STR1", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue());// 产品操作类型
            if(StringUtils.isNotBlank(reqData.getOUT_MERCH_INFO().getString("PRODUCT_OFFER_ID"))){
                grpMerchpInfo.put("PRODUCT_OFFER_ID", reqData.getOUT_MERCH_INFO().getString("PRODUCT_OFFER_ID"));
            }
            if(StringUtils.isNotBlank(reqData.getOUT_MERCH_INFO().getString("PRODUCT_ORDER_ID"))){
                grpMerchpInfo.put("PRODUCT_ORDER_ID", reqData.getOUT_MERCH_INFO().getString("PRODUCT_ORDER_ID"));
            }
            grpMerchpDISInfo.put("IS_NEED_PF", "0");// 1或者是空： 发指令 0不发指令
            this.addTradeGrpMerchpDiscnt(grpMerchpDISInfo);
        }
    }

    /*
     * @description 登记other表信息，供服务开通用
     * @author xunyl
     * @date 2013-08-26
     */
    protected void infoRegDataOther() throws Exception {
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "7810");// 服务开通侧集团service_id对应为7810
        serviceInfo.put("RSRV_STR1", "JKDT");// 服务开通侧集团集客大厅标记
        serviceInfo.put("OPER_CODE", "07");
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", getAcceptTime());
        serviceInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());
        this.addTradeOther(serviceInfo);
    }

    /**
     * chenyi 2014-7-14 特殊业务登记特殊表
     *
     * @throws Exception
     */
    protected void infoRegDataSpecial() throws Exception {
        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
        // 如果为流量统付产品，则需要同步字段给账务
        if (isFlux) {
            infoGrpCenPay();
        }

        // 行业网关云MAS业务需要登记TF_B_TRADE_GRP_PLATSVC 同步给服务开通
//        if (BbossIAGWCloudMASDealBean.isIAGWCloudMAS(GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0)))
//        {
//            String userid = reqData.getUca().getUserId();
//            String operState = "02"; // 01:订购 02:注销 04:暂停 05:恢复 08:用户信息变更
//            IData productParamInfos = BbossIAGWCloudMASDealBean.prepareProductData(userid);
//            String serialNumber = reqData.getUca().getUser().getSerialNumber();
//            String product_spec_code = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
//            String group_id = reqData.getUca().getCustGroup().getGroupId();
//
//            IData grpPlatSVCData = BbossIAGWCloudMASDealBean.makDataForGrpPlatSVC(userid, group_id, operState, product_spec_code, productParamInfos, serialNumber);
//            this.addTradeGrpPlatsvc(grpPlatSVCData);
//        }
        //BBOSS产品配置化发送二次白名单给SIMS平台
        if (BbossIAGWCloudMASDealBean.isIAGWTOSIMS(GrpCommonBean.productToMerch(reqData.getGrpProductId(), 0))) {
            String userid = reqData.getUca().getUserId();
            String operState = "01"; // 01:订购 02:注销 04:暂停 05:恢复 08:用户信息变更
            IData productParamInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
            String serialNumber = reqData.getUca().getUser().getSerialNumber();
            String product_spec_code = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
            String group_id = reqData.getUca().getCustGroup().getGroupId();
            productParamInfos.put("PRODUCT_ID", reqData.getUca().getProductId());
            IData grpPlatSVCData = BbossIAGWCloudMASDealBean.makDataForGrpPlatSVC(userid, group_id, operState, product_spec_code, productParamInfos, serialNumber);

            setEcBusiChgInfo(grpPlatSVCData);
        }
    }

    /**
     * 作用：IBOSS同步数据表记录
     *
     * @param data
     * @throws Exception
     * @author zhangbo18 2017-04-05
     */
    public void setEcBusiChgInfo(IData data) throws Exception {
        IData ecBusiInfo = new DataMap();
        String insid = SeqMgr.getInstId();
        ecBusiInfo.put("INST_ID", insid);
        ecBusiInfo.put("EC_ID", data.getString("GROUP_ID"));
        ecBusiInfo.put("EC_OPR_CODE", "04");
        ecBusiInfo.put("PRODUCT_NAME", data.getString("BIZ_NAME"));
        ecBusiInfo.put("PRODUCT_ID", data.getString("INST_ID"));
        ecBusiInfo.put("BIZ_CODE", data.getString("BIZ_CODE"));
        ecBusiInfo.put("BASE_ACCESSNO", data.getString("EC_BASE_IN_CODE"));
        ecBusiInfo.put("SERV_CODE", data.getString("SERV_CODE"));
        ecBusiInfo.put("SERV_TYPE", data.getString("SERV_TYPE"));
        ecBusiInfo.put("SOURCE", "02");
        ecBusiInfo.put("OPER_STATE", data.getString("OPER_STATE"));
        ecBusiInfo.put("BIZ_STATUS", data.getString("BIZ_STATUS"));
        ecBusiInfo.put("RB_LIST", data.getString("BIZ_ATTR"));
        ecBusiInfo.put("CONFIRMFLAG", data.getString("CONFIRMFLAG"));
        ecBusiInfo.put("ACCESSMODEL", data.getString("ACCESS_MODE"));
        ecBusiInfo.put("TEXT_SIGN_FLAG", data.getString("IS_TEXT_ECGN"));
        ecBusiInfo.put("TEXT_SIGN_DEFAULT", data.getString("DEFAULT_ECGN_LANG"));
        ecBusiInfo.put("TEXT_SIGN_ZH", data.getString("TEXT_ECGN_ZH"));
        ecBusiInfo.put("TEXT_SIGN_EN", data.getString("TEXT_ECGN_EN"));
        ecBusiInfo.put("BIZ_OPR_CODE", data.getString("OPER_STATE"));

        CSAppCall.call("SS.GroupInfoChgSVC.synChgEcBusiInfo", ecBusiInfo);
    }

    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-02
     */
    protected void initReqData() throws Exception {
        super.initReqData();
        reqData = (DestroyBBossUserReqData) getBaseReqData();
    }

    /*
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-02
     */
    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);
        // 渠道类型不为IBOSS 才能获取到页面的值
        if (!"6".equals(CSBizBean.getVisit().getInModeCode())) {
            makReqDataProductParam();
        }
        // 设置商品创建时的反馈信息
        reqData.setOUT_MERCH_INFO(map.getData("OUT_MERCH_INFO"));
    }

    /**
     * chenyi 属性处理 2014-7-4 特殊属性处理
     *
     * @throws Exception
     */
    private void makReqDataProductParam() throws Exception {
        // 产品参数
        IDataset infos = moduleData.getProductParamInfo();

        // 处理用户产品和产品参数
        for (int i = 0, size = infos.size(); i < size; i++) {
            IData info = infos.getData(i);

            // 产品ID
            String productId = info.getString("PRODUCT_ID");

            // 产品参数
            IDataset productParam = info.getString("PRODUCT_PARAM", "").equals("") ? null : new DatasetList(info.getString("PRODUCT_PARAM"));

            if (IDataUtil.isNull(productParam))
                return;

            reqData.cd.putProductParamList(productId, productParam);
        }
    }

    /*
     * @description 注销产品参数，供服务开通用
     * @author zhangc
     * @date 2013-08-26
     */
    protected void makReqElementParamData() throws Exception {
        IData inparams = new DataMap();
        inparams.put("USER_ID", reqData.getUca().getUser().getUserId());

        IDataset dataset = UserAttrInfoQry.getUserAttrByUserId(reqData.getUca().getUser().getUserId());

        if (IDataUtil.isEmpty(dataset)) {
            return;
        }

        IData data = null;
        for (int i = 0; i < dataset.size(); i++) {
            data = dataset.getData(i);

            if (reqData.isIfBooking()) {
                data.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            } else {
                data.put("END_DATE", getAcceptTime());
            }

            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            data.put("IS_NEED_PF", "0");// 1或者是空： 发指令 0不发指令
            data.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态，服开拼报文用
        }

        reqData.cd.putElementParam(dataset);
    }

    /*
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        String specProductId = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
        IData data = bizData.getTrade();
        if ("6".equals(CSBizBean.getVisit().getInModeCode())) {// 渠道类型为IBOSS
            data.put("OLCOM_TAG", "0");
        } else {
            data.put("OLCOM_TAG", "1");
        }
        
        //3- 集客大厅受理逻辑     daidl
        //如果受理模式是6并且在数据库中配置了次产品的全网编码需要发服开，我们就统一发服开
        if ("6".equals(CSBizBean.getVisit().getInModeCode())) {
            IDataset dataset = CommparaInfoQry.getCommparaInfos("CSM", "9079", specProductId);
            if (IDataUtil.isNotEmpty(dataset)) {
            	//读配置判断是否发指令
                data.put("OLCOM_TAG", dataset.getData(0).getString("PARA_CODE1"));
                //读配置判断是否等待服开回单
                String pfWait = dataset.getData(0).getString("PARA_CODE2");
                data.put("PF_WAIT", pfWait);
            }
        }
    }

    /*
     * @descrption 设置BBOSS独有的用户表数据
     * @author xunyl
     * @date 2013-04-02
     */
    protected void setTradeUser(IData map) throws Exception {
        super.setTradeUser(map);
        // 设置产品状态
        map.put("RSRV_STR5", "D");
    }

    /*
     * @descripiton 反向自动注销，需要补全必填字段（如：对讲业务：注销原因）
     * @author liaolc
     * @date 2017-06-12
     */
    protected void setSpecialAttrDate(IDataset productParam) throws Exception {
        String product_spec_code = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);

        //和对讲业务，到期不自动续约，需要系统自动注销时需要补齐"注销原因"字段
        if (StringUtils.equals("9101101", product_spec_code)) {
            if (!IDataUtil.dataSetContainsKeyAndValue(productParam, "ATTR_CODE", "91011010014"))//注销原因
            {
                IData map = new DataMap();
                map.put("ATTR_CODE", "91011010014");//注销原因
                map.put("ATTR_VALUE", "和对讲业务，到期不自动续约，需要系统自动注销");
                map.put("STATE", "ADD");
                productParam.add(map);
            }
        }

    }

}
