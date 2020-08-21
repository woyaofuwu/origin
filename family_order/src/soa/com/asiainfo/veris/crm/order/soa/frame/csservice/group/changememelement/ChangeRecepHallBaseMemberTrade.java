package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossPayBizInfoDealbean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBbossSmsInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.PayRelationDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class ChangeRecepHallBaseMemberTrade extends ChangeMemElement
{
	
	
	// XXX 处理基类取不到业务类型
    @Override
    public String setTradeTypeCode() throws Exception
    {
        // 1- 继承基类处理
        //super.setTradeTypeCode();

       return "2353";
    }
    
    /*
     * @descrition 查询被删除的参数是否能够找到对应的状态为新增的台帐信息
     * @author xunyl
     * @date 2013-10-24
     */
    protected static boolean isExistAddAttrInfo(String delParamCode, IDataset paramInfoList) throws Exception
    {
        boolean isExistAddAttr = false;

        IData paramInfo = new DataMap();
        for (int i = 0; i < paramInfoList.size(); i++)
        {
            paramInfo = paramInfoList.getData(i);
            String paramCode = paramInfo.getString("ATTR_CODE");
            String state = paramInfo.getString("STATE");
            if (delParamCode.equals(paramCode) && GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(state))
            {
                isExistAddAttr = true;
                break;
            }
        }

        return isExistAddAttr;
    }

    protected ChangeBBossMemberReqData reqData = null;

    protected String mebOperCode = "";

    protected String productOfferId = "";

    /**
     * @description 登记产品参数，一般来讲，如果是参数变更的情况，应该对应生成两条记录，一条删除，两外一条新增
     * @author xunyl
     * @throws Exception
     */
    @Override
    public void actTradePrdParam() throws Exception
    {
        // 获取产品参数
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IDataset productParams = reqData.cd.getProductParamList(baseMemProduct);
        if (IDataUtil.isEmpty(productParams))
        {
            return;
        }

        // 获取instId,转资料表用,转资料时，会根据instId查找到之前的记录做相应操作
        String USER_ID = reqData.getUca().getUserId();
        String PRODUCT_ID = baseMemProduct;
        IData productInfo = UserProductInfoQry.getUserProductBykey(USER_ID, PRODUCT_ID, reqData.getGrpUca().getUserId(), null);
        String relaInstId = productInfo.getString("INST_ID");

        // 参数集
        IDataset paramSet = new DatasetList();

        // 循环参数入表（一般对某个参数修改时，前台会产生两条记录，一条删除记录，一条新增记录，目的是给BBOSS侧发报文用）
        for (int i = 0; i < productParams.size(); i++)
        {
            IData productParam = productParams.getData(i);
            String paramCode = productParam.getString("ATTR_CODE");
            if (!StringUtils.equals("6", CSBizBean.getVisit().getInModeCode()))
            {
                IDataset bbossAttrInfoList = BBossAttrQry.qryBBossAttrByAttrCode(paramCode);
                String productId = bbossAttrInfoList.getData(0).getString("PRODUCT_ID", "");
                paramCode = paramCode.substring(productId.length());
            }
            String paramValue = productParam.getString("ATTR_VALUE");
            String paramName = productParam.getString("ATTR_NAME");
            if (GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_DEL.getValue().equals(productParam.getString("STATE")))
            {// 属性删除
                String modifyTag = TRADE_MODIFY_TAG.DEL.getValue();
                IDataset userAttrInfoList = UserAttrInfoQry.getUserAttr(reqData.getUca().getUserId(), "P", paramCode, null);
                if (IDataUtil.isNotEmpty(userAttrInfoList))
                {
                    IData userAttrInfo = userAttrInfoList.getData(0);
                    String startDate = userAttrInfo.getString("START_DATE");
                    String instId = userAttrInfo.getString("INST_ID");
                    String userAttrValue = userAttrInfo.getString("ATTR_VALUE");
                    String endDate = getAcceptTime();
                    addAttrTradeInfo(modifyTag, instId, relaInstId, paramCode, paramName, userAttrValue, startDate, endDate, paramSet, "0");
                }

                // /如果该当被删除的参数在在参数列表中找不到对应的状态为新增的记录，则需要新增一条值为""的参数台帐信息
                if (!isExistAddAttrInfo(productParam.getString("ATTR_CODE"), productParams))
                {
                    String startDate = getAcceptTime();
                    String endDate = SysDateMgr.getTheLastTime();
                    String instId = SeqMgr.getInstId();
                    addAttrTradeInfo("F", instId, relaInstId, paramCode, paramName, "", startDate, endDate, paramSet, "1");
                }
            }
            else if (GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(productParam.getString("STATE")))
            {// 属性新增
                String modifyTag = TRADE_MODIFY_TAG.Add.getValue();
                String startDate = getAcceptTime();
                String endDate = SysDateMgr.getTheLastTime();
                String instId = SeqMgr.getInstId();
                addAttrTradeInfo(modifyTag, instId, relaInstId, paramCode, paramName, paramValue, startDate, endDate, paramSet, "1");
            } else if ("1109031001".equals(productParam.getString("ATTR_CODE")) || "1109031002".equals(productParam.getString("ATTR_CODE"))) {
                String modifyTag = TRADE_MODIFY_TAG.DEL.getValue();
                IDataset userAttrInfoList = UserAttrInfoQry.getUserAttr(reqData.getUca().getUserId(), "P", paramCode, null);
                if (IDataUtil.isNotEmpty(userAttrInfoList))
                {
                    IData userAttrInfo = userAttrInfoList.getData(0);
                    String startDate = userAttrInfo.getString("START_DATE");
                    String instId = userAttrInfo.getString("INST_ID");
                    String userAttrValue = userAttrInfo.getString("ATTR_VALUE");
                    String endDate = getAcceptTime();
                    addAttrTradeInfo(modifyTag, instId, relaInstId, paramCode, paramName, userAttrValue, startDate, endDate, paramSet, "0");
                }

                // 属性新增
                modifyTag = TRADE_MODIFY_TAG.Add.getValue();
                String startDate = getAcceptTime();
                String endDate = SysDateMgr.getTheLastTime();
                String instId = SeqMgr.getInstId();
                addAttrTradeInfo(modifyTag, instId, relaInstId, paramCode, paramName, paramValue, startDate, endDate, paramSet, "1");
            }

        }
        if (!paramSet.isEmpty())
        {
            this.addTradeAttr(paramSet);
        }
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     *
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记TF_B_TRADE_GRP_MERCH_MEB表
        if (!reqData.isMerchInfo)
        {
            this.infoRegDataEntireMerchMeb(reqData.getBbossProductInfo());
            // 登记other表
            infoRegDataOther();

//            infoRegDataSpecial();

        }
    }

    /*
     * @description 新增参数数据(modifyTag为F时表示该台账不转资料)
     * @auhtor xunyl
     * @date 2013-10-24
     */
    protected void addAttrTradeInfo(String modifyTag, String instId, String relaInstId, String paramCode, String paramName, String paramValue, String startDate, String endDate, IDataset paramSet, String is_need_pf) throws Exception
    {
        IData newParam = new DataMap();
        newParam.put("MODIFY_TAG", modifyTag);
        newParam.put("INST_TYPE", "P");
        newParam.put("RELA_INST_ID", relaInstId);
        newParam.put("INST_ID", instId);
        newParam.put("ATTR_CODE", paramCode);// 属性编号
        newParam.put("RSRV_STR3", paramName);// 属性名称
        newParam.put("ATTR_VALUE", paramValue);// 属性值
        newParam.put("START_DATE", startDate);// 开始时间默认为系统当前时间
        newParam.put("END_DATE", endDate);// 结束时间默认为2050年
        newParam.put("IS_NEED_PF", is_need_pf);
        paramSet.add(newParam);
    }

    /**
     * 判断当前的是否为集团付费，如果为集团付费，且有对应的付费项目，则插入payrelation表 如果多条资费则入多条付费关系 如果为个人付费，则不插payrelation表 个人付费模式时不需要插入payrelation信息
     * 正向 如果集团受理选择集团付费，则该资费为集团付费， 反向 只要该资费配置了付费项，则为集团付费 chenyi 2014-8-5
     *
     * @throws Exception
     */
    public void dealPayRelaInfo() throws Exception
    {

        // 查询集团受理方式是否为集团付费
        IDataset payPlanfoDataset = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(reqData.getGrpUca().getUserId(), "-1");
        String planTypeCode = "";
        if (IDataUtil.isNotEmpty(payPlanfoDataset))
        {
            planTypeCode = payPlanfoDataset.getData(0).getString("PLAN_TYPE_CODE");
        }

        if ("G".equals(planTypeCode) || "6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            IDataset discntDataset = reqData.cd.getDiscnt();
            if (IDataUtil.isEmpty(discntDataset) && mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_ADD.getValue()) && "G".equals(planTypeCode))
            {
                CSAppException.apperr(GrpException.CRM_GRP_823);
            }
            else if (IDataUtil.isEmpty(discntDataset))
            {
                return;
            }

            for (int i = 0, sizeI = discntDataset.size(); i < sizeI; i++)
            {

                IData discntData = discntDataset.getData(i);
                String discntcode = discntData.getString("DISCNT_CODE");
                String modifytag = discntData.getString("MODIFY_TAG");
                String payItemCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                        { "1", "B", discntcode, "PAYITEM" });// 获取付费项目

                String userid = reqData.getUca().getUserId();
                String acctidA = reqData.getGrpUca().getAcctId();

                IDataset userPayInfo = PayRelaInfoQry.qryPayRealInfoByItemAndAcctId(payItemCode, acctidA);
                // 如果新增资费不存在付费关系就新增
                if (TRADE_MODIFY_TAG.Add.getValue().equals(modifytag) && IDataUtil.isEmpty(userPayInfo) && StringUtils.isNotEmpty(payItemCode))
                {

                    IData payRela = PayRelationDealBean.infoPayrelation(userid, payItemCode, acctidA);

                    this.addTradePayrelation(payRela);

                }

            }
        }

    }

    /*
     * @description 根据资费编码到trade_discnt表查找inst_id,用于与资费参数进行关联
     * @author xunyl
     * @date 2013-09-03
     */
    protected String getInstIdByDiscntCode(String discntCode) throws Exception
    {
        // 1- 定义返回对象
        String instId = "";

        // 2- 获取资费对象
        IDataset merchPDsts = bizData.getTradeDiscnt();

        // 2- 循环产品资费信息，获取相应的inst_id
        for (int i = 0; i < merchPDsts.size(); i++)
        {
            IData merchPDst = merchPDsts.getData(i);
            String elementId = merchPDst.getString("DISCNT_CODE");
            if (discntCode.equals(elementId))
            {
                instId = merchPDst.getString("INST_ID");
                break;
            }
        }

        // 3- 返回数据
        return instId;
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeBBossMemberReqData();
    }

    /*
     * @description 根据接口规范的成员操作编号获取服务开通侧配置的成员操作编号(供服务开通用)
     * @author xunyl
     * @date 2013-08-26
     */
    protected String getServOpType(String memOpType) throws Exception
    {
        // 1- 定义返回值
        String servOpType = "";

        // 2-获取对应的成员操作编号
        if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_ADD.getValue()))
        {
            servOpType = "06";
        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CANCLE.getValue()))
        {
            servOpType = "07";
        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY.getValue()))
        {
            servOpType = "03";
        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_PASTE.getValue()))
        {
            servOpType = "04";
        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue()))
        {
            servOpType = "05";
        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY_PARAM.getValue()))
        {
            servOpType = "08";// 默认为成员属性变更
            IDataset discntInfo = reqData.getFluxDiscnt();// 获取资费信息
            boolean isPayBiz = BbossPayBizInfoDealbean.isBbossPaybiz(discntInfo, reqData.getGrpProductId());// 判断是否订购流量叠加包

            if (isPayBiz)
            {
                servOpType = "20";// 流量叠加包操作类型
            }

        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY_IMS_PASSWORD.getValue()))
        {
            servOpType = "17";
        }

        // 3- 返回服务开通侧商品操作编号
        return servOpType;
    }

    /**
     * chenyi 2014-4-15 处理流量统付业务 将账务所需数据通过tf_b_trade_meb_cenpay表同步账务 如果订购流量叠加包需要新增一条数据且enddate为当前月末 如果是属性变更则直接修改
     *
     * @return
     * @throws Exception
     */
    private void infoMebCenPay() throws Exception
    {
        // 判断是否为流量叠加包
        IDataset discntInfo = reqData.getFluxDiscnt();// 获取资费信息
        boolean isPayBiz = BbossPayBizInfoDealbean.isBbossPaybiz(discntInfo, reqData.getGrpProductId());// 判断是否订购流量叠加包

        if (isPayBiz)
        {
            // 如果是流量叠加包则直接新增值

            IDataset mebCenPayDataset = BbossPayBizInfoDealbean.getFluxMebCenpayData(discntInfo, reqData.getUca().getUserId(), reqData.getGrpProductId(),productOfferId);
            this.addTradeMebCenpay(mebCenPayDataset);

        }
        else
        {
            // 普通成员属性变更
            // 1获取产品参数
            String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
            IDataset paramDataList = reqData.cd.getProductParamList(baseMemProduct);// 获取产品参数

            // 2获取变更信息
            IDataset mebCenPayDataset = BbossPayBizInfoDealbean.chgMebCenPayData(paramDataList, reqData.getUca().getUserId(),productOfferId);
            this.addTradeMebCenpay(mebCenPayDataset);
        }

    }

    /**
     * 业务台帐BBOSS产品成员子表
     *
     * @throws Exception
     */
    public void infoRegDataEntireMerchMeb(IData productInfo) throws Exception
    {
        mebOperCode = productInfo.getString("MEB_OPER_CODE");
        // 如果报文中的业务类型是重置IMS序列号，那么就按原来的参数修改来走
        if (mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY_IMS_PASSWORD.getValue()))
        {
            mebOperCode = GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY_PARAM.getValue();
        }

        IDataset merchMebs = UserEcrecepMebInfoQry.getSEL_BY_USERID_USERIDA(reqData.getUca().getUserId(), productInfo.getString("USER_ID"), reqData.getUca().getUserEparchyCode());

        if (merchMebs != null && merchMebs.size() > 0)
        {
            IData merchMeb = merchMebs.getData(0);
            productOfferId=merchMeb.getString("PRODUCT_OFFER_ID");
            if (mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY.getValue()))
            {
                merchMeb.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            }
            else if (mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY_PARAM.getValue()))
            {
                merchMeb.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            }
            merchMeb.put("RSRV_TAG1", productInfo.getString("MEB_TYPE", "1"));// 成员类型,1-签约成员2-白名单0-黑名单
            this.addEcrecrpMerchMeb(merchMeb);


            //登记集客受理大厅流程信息
            IData epData = GrpCommonBean.actEcrecepProcedure(reqData.getUca().getUser().getUserId(), SeqMgr.getInstId(), reqData.getUca().getUser().getSerialNumber(), merchMeb.getString("PRODUCT_ORDER_ID"), merchMeb.getString("PRODUCT_OFFER_ID", ""), "1", "0", "0");
            this.addTradeEcrecepProcedure(epData);

//            boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
//            // 如果是流量叠加包 需要同步TF_B_TRADE_GRP_MERCH_MB_DIS 到服开 走规范4.10接口
//            String productSpecCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
//                    { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
//                    { "1", "B", reqData.getGrpProductId(), "PRO" });// 获取集团产品编码
//            if (isFlux || StringUtils.equals("9101101", productSpecCode))
//            {
//                infoRegDataMerchMbDis(merchMeb, productInfo);
//            }
        }
    }

    /**
     * chenyi 2014-4-15 流量统付 统付业务订购流量叠加包时 需要将此表数据同步服开
     *
     * @param merchMeb
     * @throws Exception
     */
    private void infoRegDataMerchMbDis(IData merchMeb, IData productInfo) throws Exception
    {

        IDataset merchPDsts = reqData.getFluxDiscnt();// 1- 获取产品资费信息

        String member_order_number = productInfo.getString("MEMBER_ORDER_NUMBER");// 成员订购关系 需要在回单的时候更新到此表

        IDataset productDstInfo = BbossPayBizInfoDealbean.getDataMerchMbDis(merchPDsts, reqData.getGrpProductId(), merchMeb, member_order_number);

        this.addTradeMerchMebDiscnt(productDstInfo);
    }

    /*
     * @description 登记other表信息，供服务开通用
     * @author xunyl
     * @date 2013-08-26
     */
    protected void infoRegDataOther() throws Exception
    {
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "78101");
        serviceInfo.put("OPER_CODE", getServOpType(mebOperCode));
        serviceInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());
        this.addTradeOther(serviceInfo);
    }

    /**
     * chenyi 2014-7-14 特殊业务登记特殊表
     *
     * @throws Exception
     */
    protected void infoRegDataSpecial() throws Exception
    {
        String product_spec_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                { "1", "B", reqData.getGrpProductId(), "PRO" });// 获取集团产品编码
        // 流量统付业务登记tf_b_trade_GRP_CENPAY 同步字段给账务
        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
        // 如果为流量统付产品，
        if (isFlux)
        {
            infoMebCenPay();// 则需要同步字段给账务
            String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
            IDataset paramDataList = reqData.cd.getProductParamList(baseMemProduct);// 获取产品参数
            IData tempData = BbossPayBizInfoDealbean.chgPayBizRelaEffect(paramDataList, reqData.getGrpUca().getUserId(), reqData.getUca().getUserId());// 获取变更时间
            if (IDataUtil.isNotEmpty(tempData))
            {
                infoPaybizRelaBB(tempData);//如果生效时间有修改 更新BB关系生效时间
            }
            return;
        }

        // 3集团一点支付
        if (StringUtils.equals("99903", product_spec_code))
        {
            String memUserId = reqData.getUca().getUser().getUserId();
            String grpAcctId = reqData.getGrpUca().getAcctId();
            String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
            IDataset paramInfoList = reqData.cd.getProductParamList(baseMemProduct);
            IDataset payRelationInfo = PayRelationDealBean.chgYDZFPayRelation(memUserId, grpAcctId, paramInfoList);
            this.addTradePayrelation(payRelationInfo);
            return;
        }

        // 4- 其它业务
        this.dealPayRelaInfo();

    }

    /**
     * @Function:
     * @Description:修改BB关系生效时间
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    protected void infoPaybizRelaBB(IData tempData) throws Exception
    {
        // 1获取BB关系
        String merchpRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", reqData.getGrpProductId(), false);
        IDataset productMemberBBInfoList = RelaBBInfoQry.getBBByUserIdAB(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId(), "1", merchpRelationTypeCode);
        // 2.修改BB关系的生效时间
        if (IDataUtil.isNotEmpty(productMemberBBInfoList))
        {
            IData bbRelaData = productMemberBBInfoList.getData(0);
            BbossPayBizInfoDealbean.chgEffect(bbRelaData, tempData);
            this.addTradeRelationBb(bbRelaData);
        }
    }

    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-25
     */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeBBossMemberReqData) getBaseReqData();
    }

    /*
     * @description 将前台传递过来的BBOSS数据放入RD中
     * @author xunyl
     * @date 2013-04-25
     */
    public void makBBossReqData(IData map) throws Exception
    {
        reqData.setMerchInfo(map.getBoolean("IS_MERCH_INFO"));
        IDataset discntSet = reqData.cd.getDiscnt();
        String productId = map.getString("PRODUCT_ID","");
        String productSpecCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                { "1", "B", reqData.getGrpProductId(), "PRO" });// 获取集团产品编码
        if (BbossPayBizInfoDealbean.isFluxTFBusiness(productId)) //若为流量统付业务
        {
            IDataset fluxDiscntSet = (IDataset) Clone.deepClone(discntSet);
            reqData.setFluxDiscnt(fluxDiscntSet);
            reqData.cd.getDiscnt().clear();
        }else if(StringUtils.equals("9101101", productSpecCode)){
            IDataset fluxDiscntSet = (IDataset) Clone.deepClone(discntSet);
            reqData.setFluxDiscnt(fluxDiscntSet);
        }

        if (!map.getBoolean("IS_MERCH_INFO") == true)
        {// 商产品类型判定，商品不需要插入TF_B_TRADE_GRP_MERCH_MEB表，而产品需要插入
            reqData.setBbossProductInfo(map.getData("PRODUCT_INFO"));
        }
    }

    /*
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-25
     */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        makBBossReqData(map);
        //记录白名单二次确认信息
        mekWhiteFlag(map);
    }
    /***
     *  处理白名单二次确认标识
     *
     * @author zhangbo18 2017-03-22
     * @param map
     * @throws Exception
     */
    protected void mekWhiteFlag(IData map) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", map.getString("USER_ID"));
        params.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
        //获取需要二次确认属性的属性编码
        IDataset commparams = CommparaInfoQry.getCommparaAllCol("CSM", "4695", params.getString("PRODUCT_ID"), "ZZZZ");
        String twoWhiteListParamCode = "";
        if (IDataUtil.isNotEmpty(commparams) && IDataUtil.isNotEmpty(commparams.getData(0)))
        {
            twoWhiteListParamCode = commparams.getData(0).getString("PARA_CODE1");
        }
        String attr_value = "";
        //获取二次确认管理方式的值
        if (StringUtils.isNotBlank(twoWhiteListParamCode))
        {
            IDataset attrInfo = UserAttrInfoQry.getUserAttrByUserInstType(map.getString("USER_ID"), twoWhiteListParamCode);
            if (IDataUtil.isNotEmpty(attrInfo.getData(0)))
            {
                attr_value = attrInfo.getData(0).getString("ATTR_VALUE");
            }
        }
        //二次确认标记为是，表示需要做二次确认，并且没有待二次确认记录，表示首次确认，则需要插预处理工单并登记待二次确认记录
        if ("是".equals(attr_value))
        {
            map.put("WHITE_FLAG", true);
        }
    }



    /***
     *  处理白名单二次确认信息
     *
     * @author zhangbo18 2017-03-22
     * @param map
     * @throws Exception
     */
    protected void actWhiteConfirmInfo(IData map) throws Exception
    {
        if (map.getBoolean("WHITE_FLAG"))
        {
            IData params = new DataMap();
            params.put("USER_ID", map.getString("USER_ID"));
            IDataset results = CSAppCall.call("SS.GroupInfoChgSVC.qryGrpPlatInfo", params);
            if (IDataUtil.isNotEmpty(results))
            {
                IData result = results.getData(0);
                //登记待二次确认记录
                String insid = SeqMgr.getInstId();
                result.put("INST_ID", insid);
                result.put("EC_ID", result.getString("GROUP_ID"));
                result.put("PROD_ID", result.getString("INST_ID"));
                result.put("PROD_NAME", result.getString("BIZ_NAME"));
                result.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
                result.put("EFFT_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                result.put("RSRV_STR1", map.getString("REQUEST_ID"));
                results = CSAppCall.call("SS.GroupInfoChgSVC.regWhiteConfirmInfo", result);
            }
        }
    }

    /*
     * @description 成员暂停与恢复，修改相关表状态
     * @author xunyl
     * @date 2013-09-10
     */
    protected void modifyStateForRelaTabs(String memOpType) throws Exception
    {
        // 1- 修改BB表状态
        String grpUserId = reqData.getGrpUca().getUserId();
        String memUserId = reqData.getUca().getUserId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IDataset mebRelaBBInfoList = RelaBBInfoQry.getBBByUserIdAB(grpUserId, memUserId, "1", relationTypeCode);
        if (IDataUtil.isEmpty(mebRelaBBInfoList))
        {
            return;
        }
        IData mebRelaBBInfo = mebRelaBBInfoList.getData(0);
        if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_PASTE.getValue()))
        {
            mebRelaBBInfo.put("RSRV_STR5", "N");
        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue()))
        {
            mebRelaBBInfo.put("RSRV_STR5", "A");
        }
        mebRelaBBInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        this.addTradeRelationBb(mebRelaBBInfo);

        // 2- 修改服务状态表(删除一条，新增一条)
        IDataset newProductSvcStateInfoList = new DatasetList();
        IDataset productSvcInfoList = UserSvcInfoQry.getUserProductSvc(memUserId, grpUserId, null);
        if (null == productSvcInfoList || productSvcInfoList.size() < 0)
        {
            return;
        }
        for (int i = 0; i < productSvcInfoList.size(); i++)
        {
            IData productSvcInfo = productSvcInfoList.getData(i);
            String svcId = productSvcInfo.getString("SERVICE_ID");
            IDataset productSvcStateInfoList = UserSvcStateInfoQry.getUserLastStateByUserSvc(memUserId, svcId);
            if (!productSvcStateInfoList.isEmpty())
            {
                IData productSvcStateInfo = productSvcStateInfoList.getData(0);
                productSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                productSvcStateInfo.put("END_DATE", getAcceptTime());
                newProductSvcStateInfoList.add(productSvcStateInfo);
            }
            IData newProductSvcStateInfo = (IData) Clone.deepClone(productSvcInfo);
            newProductSvcStateInfo.put("START_DATE", getAcceptTime());
            newProductSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());
            newProductSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            newProductSvcStateInfo.put("INST_ID", SeqMgr.getInstId());
            if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_PASTE.getValue()))
            {
                newProductSvcStateInfo.put("STATE_CODE", "n");
            }
            else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue()))
            {
                newProductSvcStateInfo.put("STATE_CODE", "0");
            }
            newProductSvcStateInfoList.add(newProductSvcStateInfo);
            this.addTradeSvcstate(newProductSvcStateInfoList);
        }
    }

    /*
     * @description 登记完工依赖表数据
     * @author xunyl
     * @2013-09-12
     */
    protected void retTradeData(IDataset dataset) throws Exception
    {
        super.retTradeData(dataset);
        IData merchToMerchPInfo = new DataMap();
        merchToMerchPInfo.put("BBOSS_TAG", "BBOSS_TAG");
        merchToMerchPInfo.put("TRADE_ID", bizData.getTrade().getString("TRADE_ID"));
        dataset.add(merchToMerchPInfo);
    }

    /*
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception
    {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        IData data = bizData.getTrade();
        if (reqData.isMerchInfo)
        {
            data.put("OLCOM_TAG", "0");
        }
        else if ("6".equals(CSBizBean.getVisit().getInModeCode()))
        {// 渠道类型为IBOSS
            data.put("OLCOM_TAG", "0");
        }
        else
        {
            data.put("OLCOM_TAG", "1");
        }
    }
    /**
     * @description 重写处理登记和完工短信的方法，特殊情况(流量统付)下，不需要采用配置模板发短信
     * @author xunyl
     * @date 2015-06-29
     */
    protected void actRegAndFinishSms(IData map) throws Exception{
        // 1- 根据省内的产品编号获取集团产品编号
        String productId = reqData.getGrpProductId();
        String productSpecCode = GrpCommonBean.productToMerch(productId, 0);

        //2- 根据各自业务判断是否采用配置模板下发短信
        boolean isSendSmsByModel = false;
        String grpProductUserId = reqData.getGrpUca().getUserId();
        String mebSerailNumber = reqData.getUca().getSerialNumber();
        isSendSmsByModel = DealBbossSmsInfoBean.isSendSmsByModel(productSpecCode, grpProductUserId,mebSerailNumber);

        //3- 如果采用配置模板发送短信，则直接退出
        if(isSendSmsByModel){
            super.actRegAndFinishSms(map);
        }
    }

}
