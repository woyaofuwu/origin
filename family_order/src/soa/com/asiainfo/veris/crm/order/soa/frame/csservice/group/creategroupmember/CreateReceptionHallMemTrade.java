package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;

/**
 * @program: hain_order
 * @description: JKDT集团客户同步
 * @author: zhangchengzhi
 * @create: 2018-09-29 17:16
 **/

public class CreateReceptionHallMemTrade  extends CreateGroupMember {

	 private static final Logger logger = LoggerFactory.getLogger(CreateReceptionHallMemTrade.class);
	 
	// XXX 处理基类取不到业务类型
	    @Override
	    public String setTradeTypeCode() throws Exception
	    {
	        // 1- 继承基类处理
	        //super.setTradeTypeCode();

	       return "2352";
	    }

    /**
     * @description 根据网外成员手机号到各CRM库查找三户信息
     * @auhtor xunyl
     * @date 2013-07-11
     */
    protected static IDataset searchMemUserInfoFromCrm(String memSerialNumber, String[] connNames) throws Exception
    {
        // 1- 定义成员用户信息
        IDataset memUserInfo = new DatasetList();

        // 2- CRM库查找网外成员的三户信息
        for (int i = 0; i < connNames.length; i++)
        {
            String connName = connNames[i];
            if (connName.indexOf("crm") >= 0)
            {
                memUserInfo = UserInfoQry.getUserInfoBySn(memSerialNumber, "0", "06", connName);
                if (null != memUserInfo && memUserInfo.size() != 0)
                {
                    break;
                }
            }
        }

        // 3- 返回成员用户信息
        return memUserInfo;
    }

    protected CreateBBossMemberReqData reqData = null;

    /**
     * @description 重新基类登记UU关系，基类登记到UU表，BBOSS侧登记到BB表
     * @author xunyl
     * @date 2014-07-25
     */
    protected void actTradeRelationUU() throws Exception
    {
        IData relaData = new DataMap();

        relaData.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", reqData.getMemRoleB());
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());

        // 处理产品级控制UU关系生效时间
        dealRelationStartDate(relaData);
        // 如果是统付业务，bb生效时间和属性相关
//        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
//        // 如果为流量统付产品，
//        if (isFlux)
//        {
//            String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
//            IDataset paramDataList = reqData.cd.getProductParamList(baseMemProduct);// 获取产品参数
//            BbossPayBizInfoDealbean.dealPayBizRelaEffect(relaData, paramDataList);
//        }
        super.addTradeRelationBb(relaData);
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
        // 产品子表
        actTradePrdAndPrdParam();

        // 关系表
        actTradeRelationUU();

        // 判断当前是否是流量统付，而且是否定额模式，如果是，则需要生成付费关系
        if (reqData.isMerchInfo)
        {
            setVitualInfo();

        }
        else
        {
            // 登记BBOSS侧成员产品信息
        	infoRegDataEceecrpMerchMeb(reqData.getBbossProductInfo()); //rename

            // 登记other表
            infoRegDataOther();

        }

        //记录流程
//        savaProduce()

    }

    /**
     * @description 公用业务数据校验
     * @author xunyl
     * @date 2014-03-24
     */
    protected void checkMebDiversify(IData map) throws Exception
    {
        // 如果是网外号码，而且没有三户信息，不做公用业务数据校验
        String memSerialNumber = map.getString("SERIAL_NUMBER");
        boolean isOutNetSn = false;
        isOutNetSn = this.isOutNetSn(memSerialNumber);
        if (isOutNetSn)
        {
            return;
        }
        super.checkMebDiversify(map);
    }

    /**
     * @description 订单受理前校验
     * @author xunyl
     * @date 2014-03-24
     */
    protected void chkTradeBefore(IData map) throws Exception
    {
        // 如果是网外号码，而且没有三户信息，不做受理前校验
        String memSerialNumber = map.getString("SERIAL_NUMBER");
        boolean isOutNetSn = false;
        isOutNetSn = this.isOutNetSn(memSerialNumber);
        if (isOutNetSn)
        {
            return;
        }
        super.chkTradeBefore(map);
    }

    /**
     * @description 网外号码创建虚拟三户信息
     * @author xunyl
     * @date 2013-07-16
     */
    protected void createVitualUserData(String memSerialNumber, IData map) throws Exception
    {
        // 1- 创建UCA对象
        UcaData ucaData = new UcaData();

        // 2- 创建客户资料信息
        String custId = SeqMgr.getCustId();
        this.setVitualCustInfo(ucaData, custId);

        // 3- 创建用户资料信息
        this.setVitualUserInfo(ucaData, memSerialNumber,custId);

        // 4- 创建账户资料信息
        this.setVisualAccoutInfo(ucaData, custId);

        // 5- 将UCA设置到线程中
        DataBusManager.getDataBus().setUca(ucaData);
        reqData.setUca(ucaData);
        // 6- 设置集团UCA对象
        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");
        UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);
        if (grpUCA == null)
        {
            grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(map);
        }
        reqData.setGrpUca(grpUCA);

        String cacheKey = CacheKey.getUcaKeyUserBySn(memSerialNumber, reqData.getUca().getUser().getEparchyCode());
        SharedCache.set(cacheKey, ucaData.getUser().toData(), 600);
    }

    /**
     * 判断当前的是否为集团付费，如果为集团付费，且有对应的付费项目，则插入payrelation表 如果多条资费则入多条付费关系 如果为个人付费，则不插payrelation表 个人付费模式时不需要插入payrelation信息
     * 正向 如果集团受理选择集团付费，则该资费为集团付费， 反向 只要该资费配置了付费项，则为集团付费 chenyi 2014-8-5
     *
     * @throws Exception
     */
    public void dealPayRelaInfo() throws Exception
    {
        boolean flux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
        if(flux)//统付业务直接返回
        {
            return;
        }

        String planTypeCode = moduleData.getPlanTypeCode();
        boolean isExistpayItemCode = false;// 集团付费模式下必须有付费项目

        IDataset discntDataset = reqData.cd.getDiscnt();
        // 如果是集团付费模式 必须配置对应的资费项目
        if (IDataUtil.isEmpty(discntDataset) && "G".equals(planTypeCode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_823);
        }
        // 反向接口如果没订购资费直接返回
        else if (IDataUtil.isEmpty(discntDataset) && "6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            return;
        }
        // 如果订购资费且为集团付费或者反向 则走付费项
        else if (IDataUtil.isNotEmpty(discntDataset) && ("G".equals(planTypeCode) || "6".equals(CSBizBean.getVisit().getInModeCode())))
        {
            for (int i = 0, sizeI = discntDataset.size(); i < sizeI; i++)
            {

                IData discntData = discntDataset.getData(i);
                String discntcode = discntData.getString("DISCNT_CODE");
                String payItemCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                        { "1", "B", discntcode, "PAYITEM" });// 获取付费项目

                if (StringUtils.isEmpty(payItemCode))
                {
                    return;
                }

                isExistpayItemCode = true;// 此次集团付费模式订购存在付费项目

                String userid = reqData.getUca().getUserId();
                String acctidA = reqData.getGrpUca().getAcctId();

                IData payRela = PayRelationDealBean.infoPayrelation(userid, payItemCode, acctidA);// 拼写payRe表数据

                this.addTradePayrelation(payRela);

            }
        }

        // 集团付费模式下没有配置对应付费项目编码
        if ("G".equals(planTypeCode) && !isExistpayItemCode)
        {
            CSAppException.apperr(GrpException.CRM_GRP_832);
        }
    }

    /**
     * 根据用户ID查询TF_F_USER_GRP_MERCHP表得到产品对应哪个的订单号和订购关系ID
     */
    protected IData getGrpMerchpInfo() throws Exception
    {
        IDataset merchPDatas = UserEcrecepProductInfoQry.qryEcrecepProductInfoByUserIdMerchSpecProductSpecStatus(
                reqData.getGrpUca().getUser().getUserId(), null, null, null, null);
        if (merchPDatas == null || merchPDatas.size() == 0)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_106);
        }
        return merchPDatas.getData(0);
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateBBossMemberReqData();
    }

    /**
     * chenyi 2014-4-15 处理流量统付业务 将账务所需数据登记MebCenPay表同步账务
     *
     * @returnv
     * @throws Exception
     */
    private void infoMebCenPay() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IDataset paramDataList = reqData.cd.getProductParamList(baseMemProduct);// 获取产品参数
        String ecId = reqData.getGrpUca().getCustGroup().getMpGroupCustCode();// 全网集团编码
        String serial_number = reqData.getUca().getSerialNumber();
        String product_id = reqData.getGrpProductId();
        String userid = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();
        IData cenPayData = BbossPayBizInfoDealbean.getDataMebCenPay(paramDataList, userid, userIdA, product_id, ecId, serial_number);

        this.addTradeMebCenpay(cenPayData);

    }

    /**
     * @description 登记BBOSS侧的成员产品信息
     * @author xunyl
     * @date 2013-04-23
     */
    protected void infoRegDataEntireMerchMeb(IData productInfo) throws Exception
    {
        IData merchMebInfo = new DataMap();
        String productId = reqData.getGrpUca().getProductId();
        merchMebInfo.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());// 成员用户的手机号码
        merchMebInfo.put("SERVICE_ID", productId.length() > 8 ? productId.substring(0, 8) : productId);
        merchMebInfo.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());
        merchMebInfo.put("EC_SERIAL_NUMBER", reqData.getGrpUca().getUser().getSerialNumber());// 集团用户的手机号码
        merchMebInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());

        // 根据用户ID查询TF_F_USER_GRP_MERCHP表得到产品对应哪个的订单号和订购关系ID
        IData grpMerchPInfo = this.getGrpMerchpInfo();
        merchMebInfo.put("PRODUCT_ORDER_ID", grpMerchPInfo.getString("PRODUCT_ORDER_ID")); // 产品订单号
        merchMebInfo.put("PRODUCT_OFFER_ID", grpMerchPInfo.getString("PRODUCT_OFFER_ID", "")); // 产品订购关系ID
        merchMebInfo.put("STATUS", "A");
        merchMebInfo.put("START_DATE", productInfo.getString("EFF_DATE", getAcceptTime()));
        merchMebInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        merchMebInfo.put("RSRV_TAG1", productInfo.getString("MEB_TYPE", "1"));// 成员类型,1-签约成员2-白名单0-黑名单
        merchMebInfo.put("INST_ID", SeqMgr.getInstId());
        this.addTradeGrpMerchMeb(merchMebInfo);
    }


    protected void infoRegDataEceecrpMerchMeb(IData productInfo) throws Exception {
        IData merchMebInfo = new DataMap();
        String productId = reqData.getGrpUca().getProductId();
        merchMebInfo.put("USER_ID", reqData.getUca().getUser().getUserId());// 成员用户的手机号码
        merchMebInfo.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());// 成员用户的手机号码
        merchMebInfo.put("SERVICE_ID", productId.length() > 8 ? productId.substring(0, 8) : productId);
        merchMebInfo.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());
        merchMebInfo.put("EC_SERIAL_NUMBER", reqData.getGrpUca().getUser().getSerialNumber());// 集团用户的手机号码
        merchMebInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());

        // 根据用户ID查询TF_F_USER_GRP_MERCHP表得到产品对应哪个的订单号和订购关系ID
        IData grpMerchPInfo = this.getGrpMerchpInfo();
        merchMebInfo.put("PRODUCT_ORDER_ID", grpMerchPInfo.getString("PRODUCT_ORDER_ID")); // 产品订单号
        merchMebInfo.put("PRODUCT_OFFER_ID", grpMerchPInfo.getString("PRODUCT_OFFER_ID", "")); // 产品订购关系ID
        merchMebInfo.put("STATUS", "A");
        merchMebInfo.put("START_DATE", productInfo.getString("EFF_DATE", getAcceptTime()));
        merchMebInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        merchMebInfo.put("RSRV_TAG1", productInfo.getString("MEB_TYPE", "1"));// 成员类型,1-签约成员2-白名单0-黑名单
        merchMebInfo.put("INST_ID", SeqMgr.getInstId());
        this.addEcrecrpMerchMeb(merchMebInfo);

        //登记集客受理大厅流程信息
        IData epData = GrpCommonBean.actEcrecepProcedure(reqData.getUca().getUser().getUserId(), SeqMgr.getInstId(), reqData.getUca().getUser().getSerialNumber(), grpMerchPInfo.getString("PRODUCT_ORDER_ID"), grpMerchPInfo.getString("PRODUCT_OFFER_ID", ""), "1", "A", "A");
        this.addTradeEcrecepProcedure(epData);
    }


    /**
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
        serviceInfo.put("RSRV_STR9", "78101");// 服务开通侧用成员的service_id对应为78101
        
        serviceInfo.put("RSRV_STR1", "JKDT");// 服务开通侧用成员的service_id对应为78101
        serviceInfo.put("OPER_CODE", "06");
        serviceInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
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
        String productId = reqData.getGrpProductId();

        // 判断当前是否是流量统付 ，而且是否定额模式，如果是，则需要生成付费关系
        String productSpecCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                { "1", "B", productId, "PRO" });// 获取集团产品编码
        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(productId);// 判断是否统付业务

        // 如果为流量统付产品，
        if (isFlux)
        {
            //首先判断是否已经暂停叠加包
            IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(reqData.getGrpUca().getUserId(), "0");
            String userState = userInfo.getString("RSRV_STR5", "");
            if (userState.equals("F"))
            {
                // 如果暂停了，直接抛出异常
                CSAppException.apperr(CrmUserException.CRM_USER_3005);
            }

            // 则需要同步字段给账务
            infoMebCenPay();
            return;
        }

        //融合V网
        //if("20004".equals(productSpecCode)){
        if(BbossDcrossFusionBizDealBean.isBbossDcrossBizSendPF(productSpecCode)){
            IData paramMap=getUserTypeAndRoleShort();
            infoRegDataImpu(paramMap.getString("USER_TYPE"), paramMap.getString("ROLE_SHORT"));
            infoRegOtherData();
        }


        //3- 集团客户一点支付业务
        if("99903".equals(productSpecCode)){
            //需要登记高级付费关系pay_relation表
            String memUserId = reqData.getUca().getUser().getUserId();
            String grpUserId = reqData.getGrpUca().getUser().getUserId();
            String grpAcctId = reqData.getGrpUca().getAcctId();
            String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
            IDataset paramInfoList = reqData.cd.getProductParamList(baseMemProduct);
            IData payRelationInfo = PayRelationDealBean.addYDZFPayRelation(memUserId, grpUserId, grpAcctId, paramInfoList);
            this.addTradePayrelation(payRelationInfo);
            return;
        }

        // 4- 行业网关云MAS业务
//        if (BbossIAGWCloudMASDealBean.isIAGWCloudMAS(productSpecCode))
//        {
//            String operState = "01";  //成员新增：01 成员退订：02
//            IData blackWhiteData = BbossIAGWCloudMASDealBean.makDataForBlackWhite(reqData.getUca().getUser().getUserId(), reqData.getGrpUca().getUser().getUserId(), productId, reqData.getUca().getUser().getSerialNumber(), operState);
//            this.addTradeBlackwhite(blackWhiteData);
//        }

        // 5- 其它业务
        this.dealPayRelaInfo();


    }

    /**
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-02
     */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateBBossMemberReqData) getBaseReqData();
    }

    /**
     * @description 根据成员手机号判断该成员是否属于网外号码
     * @author xunyl
     * @date 2013-07-16
     */
    protected boolean isOutNetSn(String memSerialNumber) throws Exception
    {
        // 1- 定义返回结果
        boolean isOutNetSn = false;

        // 2- 根据手机号码查询当前的路由下是否存在有成员用户信息
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);
        if (null != memberUserInfoList && memberUserInfoList.size() > 0)
        {
            return isOutNetSn;
        }

        // 3- 如果为机顶盒SN，则直接当作网外号码处理(做虚拟用户开户)
        if(StringUtils.isNotEmpty(memSerialNumber) && memSerialNumber.length()==32){
            return true;
        }

        // 4- 判断是否为本省网内号码，如果为本省网内号码并且没有有效用户信息，直接抛错
        String prov_code = BizEnv.getEnvString("crm.grpcorp.provincecode");
        IData msisdnInfo = MsisdnInfoQry.getMsisonBySerialnumber(memSerialNumber, prov_code, "1", null);
        if (IDataUtil.isNotEmpty(msisdnInfo) && IDataUtil.isEmpty(memberUserInfoList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_842);
        }

        // 5- 循环各个地州库，查询成员用户信息是否存在
        String[] connNames = Route.getAllCrmDb();
        if (connNames == null)
        {
            CSAppException.apperr(DbException.CRM_DB_9);
        }
        memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);
        if (null == memberUserInfoList || memberUserInfoList.size() == 0)
        {
            isOutNetSn = true;
        }

        // 6- 返回结果
        return isOutNetSn;
    }

    /**
     * @description 将前台传递过来的BBOSS数据放入RD中
     * @author xunyl
     * @date 2013-04-03
     */
    public void makBBossReqData(IData map) throws Exception
    {
        reqData.setMerchInfo(map.getBoolean("IS_MERCH_INFO"));
        IDataset discntSet = reqData.cd.getDiscnt();
        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(map.getString("PRODUCT_ID",""));  //判断是否为流量统付业务
        if (isFlux) //若为流量统付业务
        {
            IDataset fluxDiscntSet = (IDataset) Clone.deepClone(discntSet);
            reqData.setFluxDiscnt(fluxDiscntSet);
            reqData.cd.getDiscnt().clear();
        }
        if (!map.getBoolean("IS_MERCH_INFO") == true)
        {// 商产品类型判定，商品不需要插入TF_B_TRADE_GRP_MERCH_MEB表，而产品需要插入
            reqData.setBbossProductInfo(map.getData("PRODUCT_INFO"));
        }
        else
        {
            // 设置套餐生效时间
            reqData.setEffDate(map.getString("EFF_DATE"));
        }
    }

    /**
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-02
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
            if (IDataUtil.isNotEmpty(attrInfo))
            {
                attr_value = attrInfo.getData(0).getString("ATTR_VALUE");
            }
        }
        //二次确认标记为是，表示需要做二次确认，并且没有待二次确认记录，表示首次确认，则需要插预处理工单并登记待二次确认记录
        if (!map.getBoolean("IS_CONFIRM",false))
        {
            if ("是".equals(attr_value))
            {
                params.put("EC_ID", map.getString("GROUP_ID"));
                IDataset whiteConfirm = CSAppCall.call("SS.GroupInfoChgSVC.qryWhiteConfirmInfo", params);
                if (IDataUtil.isEmpty(whiteConfirm)){
                    map.put("ORDER_PRE", true);
                    map.put("IF_SMS", false);
                    map.put("WHITE_FLAG", true);
                }
            }
        }else{
            map.remove("ORDER_PRE");
            map.remove("IF_SMS");
            map.remove("WHITE_FLAG");
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

    public void makReqDataElement() throws Exception
    {
        // 解析资源信息
        GroupModuleParserBean.mebRes(reqData, moduleData);

        // 解析产品和产品元素信息
        GroupModuleParserBean.mebElement(reqData, moduleData);

        // 解析产品参数信息
        makReqDataProductParam();

    }

    /**
     * @descrition 重载基类方法(目的是在基类创建UCA对象前使用MAP对象的值，经典场景为成员新增场合，为往外号码创建虚三户)
     * @author xunyl
     * @date 2013-07-17
     */
    @Override
    protected void makUca(IData map) throws Exception
    {
        // 1- 非商品数据，直接退出
        if (!map.getBoolean("IS_MERCH_INFO") == true)
        {
            super.makUcaForMebNormal(map);
            return;
        }

        // 2- 如果是网外号码，而且没有三户信息，需要创建虚拟三户
        String memSerialNumber = map.getString("SERIAL_NUMBER");
        boolean isOutNetSn = false;
        isOutNetSn = this.isOutNetSn(memSerialNumber);
        if (isOutNetSn)
        {
            this.createVitualUserData(memSerialNumber, map);
            return;
        }

        // 3- 调用基类处理
        super.makUcaForMebNormal(map);
    }

    /**
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

    /**
     * 重写父类方法，特殊处理成员属性名称
     *
     * @author zhangcheng6
     * @throws Exception
     */
    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);

        // 如果是成员属性参数
        if ("P".equals(map.getString("INST_TYPE", "")))
        {
            // 查询成员属性名称
            String paramCode = map.getString("ATTR_CODE", "");
            IDataset paramSet = BBossAttrQry.qryBBossAttrByAttrCode(paramCode);
            if (paramSet != null && paramSet.size() > 0)
            {
                map.put("RSRV_STR3", paramSet.getData(0).getString("ATTR_NAME", ""));
            }
            if (!StringUtils.equals("6", CSBizBean.getVisit().getInModeCode()))
            {
                String productId = paramSet.getData(0).getString("PRODUCT_ID", "");
                //String mebAttrCode = paramCode.substring(productId.length());
                map.put("ATTR_CODE", paramCode);
            }
        }
    }

    /**
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    @Override
    protected void setTradeBase() throws Exception
    {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为1，针对省行业网关云MAS的情况进行排除
        String specProductId = GrpCommonBean.productToMerch(reqData.getGrpUca().getProductId(), 0);
        IData data = bizData.getTrade();
        if (reqData.isMerchInfo)
        {
            data.put("OLCOM_TAG", "0");
        }
        else if ("6".equals(CSBizBean.getVisit().getInModeCode()) && !BbossIAGWCloudMASDealBean.isIAGWCloudMAS(specProductId) && !BbossDcrossFusionBizDealBean.isBbossDcrossBizSendPF(specProductId))
        {// 渠道类型为IBOSS,并且不为省行业网关云MAS业务
            data.put("OLCOM_TAG", "0");
        }
        else
        {
            data.put("OLCOM_TAG", "1");
        }
        
      //以上是老的BBOSS逻辑理，解为针对云MAS要发服开并且为开环，暂时不处理，以下代码是集客大厅的业务逻辑
        //daidl 
        /*如果受理模式是6并且在数据库中配置了次产品的全网编码需要发服开，我们就统一发服开*/
        if ("6".equals(CSBizBean.getVisit().getInModeCode())) {
         //   IDataset dataset = CommparaInfoQry.getCommparaInfos("CSM", "9079", specProductId);
            if (specProductId.equals("910401")) {
//                data.put("OLCOM_TAG", dataset.getData(0).getString("PARA_CODE1"));
//                //读配置判断是否等待服开回单
//                String pfWait = dataset.getData(0).getString("PARA_CODE2");
//                data.put("PF_WAIT", pfWait);
            	  data.put("OLCOM_TAG", "1");
                  data.put("PF_WAIT", "0");
            }
        }
       
    }

    /**
     * @description 子类重写父类方法，目的是使基类方法不再执行
     * @author xunyl
     * @2013-07-17
     */
    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        String specProductId = GrpCommonBean.productToMerch(reqData.getGrpUca().getProductId(), 0);
        //if ("20004".equals(specProductId))
        if(BbossDcrossFusionBizDealBean.isBbossDcrossBizSendPF(specProductId))
        {
            map.put("RSRV_STR1", "2"); // 写死为2
        }
    }

    /**
     * @description 往外号码创建账户资料信息
     * @author xunyl
     * @date 2013-07-16
     */
    protected void setVisualAccoutInfo(UcaData ucaData, String custId) throws Exception
    {
        IData accountInfo = new DataMap();
        accountInfo.put("ACCT_ID", SeqMgr.getAcctId()); // 帐户标识
        accountInfo.put("CUST_ID", custId); // 归属客户标识
        accountInfo.put("PAY_NAME", ucaData.getUser().getSerialNumber()); // 帐户名称
        accountInfo.put("PAY_MODE_CODE", "0"); // 帐户类型：现金、托收，代扣（见参数表）
        accountInfo.put("SCORE_VALUE", "0"); // 帐户积分
        accountInfo.put("OPEN_DATE", getAcceptTime()); // 开户时间
        accountInfo.put("REMOVE_TAG", "0"); // 注销标志：0-在用，1-已销
        accountInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        accountInfo.put("CREDIT_VALUE", "0");//信用值
        accountInfo.put("BASIC_CREDIT_VALUE", "0");//基础信用值
        accountInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());//路由地州
        ucaData.setAccount(new AccountTradeData(accountInfo));
    }

    /**
     * @description 网外号码创建虚拟用户主体服务台账信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVisualMainSvcInfo(String userId) throws Exception
    {
        IData mainSvcInfo = new DataMap();
        mainSvcInfo.put("USER_ID", userId);// 用户标识
        mainSvcInfo.put("USER_ID_A", "-1");// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。
        mainSvcInfo.put("PRODUCT_ID", "8714");// 产品标识
        mainSvcInfo.put("PACKAGE_ID", "871401");// 包标识
        mainSvcInfo.put("ELEMENT_ID", "871");// 服务标识
        mainSvcInfo.put("MAIN_TAG", "1");// 主体服务标志：0-否，1-是
        mainSvcInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        mainSvcInfo.put("START_DATE", getAcceptTime());// 开始时间
        mainSvcInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        mainSvcInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        super.addTradeSvc(mainSvcInfo);
    }

    /**
     * @description 网外号码创建虚拟用户主体服务状态台账信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVisualMainSvcStateInfo(String userId) throws Exception
    {
        IData mainSvcStateInfo = new DataMap();
        mainSvcStateInfo.put("USER_ID", userId);// 用户标识
        mainSvcStateInfo.put("STATE_CODE", "0"); // 正常
        mainSvcStateInfo.put("SERVICE_ID", "871");
        mainSvcStateInfo.put("MAIN_TAG", "1");
        mainSvcStateInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
        mainSvcStateInfo.put("START_DATE", getAcceptTime());
        mainSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        mainSvcStateInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        super.addTradeSvcstate(mainSvcStateInfo);
    }

    /**
     * @description 创建默认付费账户的付费关系台账
     * @author xunyl
     * @date 2013-07-17
     */
    protected void setVisualPayRelaInfo(String userId, String acctId) throws Exception
    {
        IData payRelaInfo = new DataMap();
        payRelaInfo.put("USER_ID", userId);
        payRelaInfo.put("ACCT_ID", acctId); // 帐户标识
        payRelaInfo.put("PAYITEM_CODE", "-1"); // 付费帐目编码
        payRelaInfo.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        payRelaInfo.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行

        payRelaInfo.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        payRelaInfo.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        payRelaInfo.put("DEFAULT_TAG", "1"); // 默认标志
        payRelaInfo.put("LIMIT_TYPE", 0); // 限定方式：0-不限定，1-金额，2-比例
        payRelaInfo.put("LIMIT", 0); // 限定值
        payRelaInfo.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        payRelaInfo.put("INST_ID", SeqMgr.getInstId()); // 实例标识
        String effDate = reqData.getEffDate();
        payRelaInfo.put("START_CYCLE_ID", effDate.substring(0, 4) + effDate.substring(5, 7)); // 起始帐期
        payRelaInfo.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231()); // 终止帐期
        payRelaInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        super.addTradePayrelation(payRelaInfo);
    }

    /**
     * @description 创建成员虚拟产品信息
     * @author xunyl
     * @date 2014-03-24
     */
    protected void setVisualProductInfo(String userId) throws Exception
    {
        IData productInfo = new DataMap();
        productInfo.put("USER_ID", userId);// 用户编号
        productInfo.put("USER_ID_A", "-1");
        productInfo.put("PRODUCT_ID", "6666");// 产品标识
        productInfo.put("PRODUCT_MODE", "10");// 产品的模式
        productInfo.put("MAIN_TAG", "1");// 虚拟用户主产品标识
        productInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        productInfo.put("BRAND_CODE", "HYBG"); // 品牌
        productInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
        productInfo.put("START_DATE", getAcceptTime());
        productInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        super.addTradeProduct(productInfo);
    }

    /**
     * @description 网外号码创建虚拟客户信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVitualCustInfo(UcaData ucaData,String custId) throws Exception
    {
        // 1- 登记TF_B_TRADE_CUSTOMER表
        IData customerInfo = new DataMap();
        customerInfo.put("CUST_ID", custId); // 客户标识
        customerInfo.put("CUST_NAME", "外省号码虚拟客户"); // 客户名称
        customerInfo.put("SIMPLE_SPELL", "外省号码虚拟客户");// 客户名称简拼
        customerInfo.put("CUST_TYPE", "0"); // 客户类型：0-个人客户，1-集团客户，2-家庭客户，3-团体客户
        customerInfo.put("CUST_KIND", ""); // 客户分类：对客户的细分（可来源于经分），供营销使用
        customerInfo.put("CUST_STATE", "0");// 客户状态：0-在网、1-潜在
        customerInfo.put("PSPT_TYPE_CODE", "1");// 证件类别：见参数表TD_S_PASSPORTTYPE
        customerInfo.put("PSPT_ID", "123456789"); // 证件号码
        customerInfo.put("OPEN_LIMIT", "0");// 限制开户数：限制客户的身份证可以开用户的数量，为0表示不限制。
        customerInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 归属地市
        customerInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        customerInfo.put("IN_DATE", getAcceptTime()); // 建档时间
        customerInfo.put("REMOVE_TAG", "0");// 销档标志：0-正常、1-销档
        customerInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustomer(new CustomerTradeData(customerInfo));

        // 2- 登记TF_B_TRADE_CUST_PERSON表
        IData customerPersonInfo = new DataMap();
        customerPersonInfo.put("CUST_ID", custId); // 客户标识
        customerPersonInfo.put("PSPT_TYPE_CODE", "1");
        customerPersonInfo.put("PSPT_ID", "123456789");
        customerPersonInfo.put("CUST_NAME", "外省号码虚拟客户");
        customerPersonInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
        ucaData.setCustPerson(new CustPersonTradeData(customerPersonInfo));
    }

    /**
     * @description 成员虚拟开户信息入表
     * @author xunyl
     * @date 2014-03-24
     */
    protected void setVitualInfo() throws Exception
    {
        String memSerialNumber = reqData.getUca().getSerialNumber();
        boolean isOutNetSn = this.isOutNetSn(memSerialNumber);
        if (isOutNetSn)
        {
            // 客户台账入表
            IData customer = reqData.getUca().getCustomer().toData();
            addTradeCustomer(customer);

            // 个人客户台账信息入表
            IData custperson = reqData.getUca().getCustPerson().toData();
            addTradeCustPerson(custperson);

            //个人用户信息入表
            IData  userData=reqData.getUca().getUser().toData();
            addTradeUser(userData);

            //个人账户信息入表
            IData accountData=reqData.getUca().getAccount().toData();
            addTradeAccount(accountData);

            // 4- 创建虚拟用户主体服务台账
            String userId = reqData.getUca().getUser().getUserId();
            this.setVisualMainSvcInfo(userId);

            // 5- 创建虚拟用户主体服务状态台账
            this.setVisualMainSvcStateInfo(userId);

            // 7- 创建默认付费账户的付费关系台账
            String acctId = reqData.getUca().getAccount().getAcctId();
            this.setVisualPayRelaInfo(userId, acctId);

            // 8- 创建成员主产品信息
            this.setVisualProductInfo(userId);
        }
    }

    /**
     * @description 网外号码创建虚拟用户信息
     * @authro xunyl
     * @date 2013-07-16
     */
    protected void setVitualUserInfo(UcaData ucaData, String memSerialNumber,String custId) throws Exception
    {
        IData memUserInfo = new DataMap();
        memUserInfo.put("USER_ID", SeqMgr.getUserId()); // 用户标识
        memUserInfo.put("CUST_ID", custId); // 客户标识

        memUserInfo.put("USECUST_ID", custId);
        memUserInfo.put("BRAND_CODE", "JKDT"); // 当前品牌编码   daidl
        memUserInfo.put("PRODUCT_ID", "8714"); // 当前产品标识
        memUserInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地市
        memUserInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        memUserInfo.put("USER_TYPE_CODE", "8"); // 用户类型
        memUserInfo.put("USER_STATE_CODESET", "0"); // 用户主体服务状态集：见服务状态参数表
        memUserInfo.put("NET_TYPE_CODE", "06"); // 网别编码
        memUserInfo.put("SERIAL_NUMBER", memSerialNumber); // 服务号码
        memUserInfo.put("SCORE_VALUE", "0"); // 积分值
        memUserInfo.put("CREDIT_CLASS", "0"); // 信用等级
        memUserInfo.put("BASIC_CREDIT_VALUE", "0"); // 基本信用度
        memUserInfo.put("CREDIT_VALUE", "0"); // 信用度
        memUserInfo.put("ACCT_TAG", "0"); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        memUserInfo.put("PREPAY_TAG", "0"); // 预付费标志：0-后付费，1-预付费。（省内标准）
        memUserInfo.put("MPUTE_MONTH_FEE", "0"); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        memUserInfo.put("IN_DATE", getAcceptTime()); // 建档时间
        memUserInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 建档员工
        memUserInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 建档渠道
        memUserInfo.put("OPEN_MODE", "0"); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        memUserInfo.put("OPEN_DATE", getAcceptTime()); // 开户时间
        memUserInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        memUserInfo.put("REMOVE_TAG", "0");// 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        ucaData.setUser(new UserTradeData(memUserInfo));
    }

    /**
     * @description 重写处理登记和完工短信的方法，特殊情况(流量统付)下，不需要采用配置模板发短信
     * @author xunyl
     * @date 2015-06-29
     */

    protected void actRegAndFinishSms(IData map) throws Exception{
        // 1- 根据省内的产品编号获取集团产品编号
        String productId = reqData.getGrpProductId();
        String productSpecCode = GrpCommonBean.productJKDTToMerch(productId, 0);

        //2- 根据各自业务判断是否采用配置模板下发短信
        boolean isSendSmsByModel = false;
        String grpProductUserId = reqData.getGrpUca().getUserId();
        String mebSerailNumber = reqData.getUca().getSerialNumber();
        isSendSmsByModel = DealBbossSmsInfoBean.isSendSmsByModel(productSpecCode, grpProductUserId,mebSerailNumber);

        //3- 如果采用配置模板发送短信，则直接退出
        if(isSendSmsByModel){
            super.actRegAndFinishSms(map);
        }
        //4-企业视频彩铃发新增完工发短信在这儿  daidl
        if (StringUtils.equals("910401", productSpecCode)) {
            sendCL(map);
            //绑定服务发服务开通
            addTradeSvcToF(map);
            IDataset userSvcInfo = UserSvcInfoQry.queryUserSvcByUserId(reqData.getUca().getUser().getUserId(), "190", "0898");
            if(IDataUtil.isEmpty(userSvcInfo))
            {
                //绑定服务发服务开通
                addTradeSvcToFV(map);
                addImpuInfo(map);
            }
        }
        
    }
    private void addTradeSvcToF(IData map) throws Exception
    {
    	
    	 IData mainSvcInfo = new DataMap();
         mainSvcInfo.put("USER_ID",  reqData.getUca().getUser().getUserId());// 用户标识
         mainSvcInfo.put("USER_ID_A",reqData.getGrpUca().getUserId());// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。
         mainSvcInfo.put("ELEMENT_ID", "99662710");//与服开商定服务
         mainSvcInfo.put("SERVICE_ID", "99662710");//与服开商定服务
         mainSvcInfo.put("PACKAGE_ID", "99662710");
         mainSvcInfo.put("MAIN_TAG", "0");// 主体服务标志：0-否，1-是
         mainSvcInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
         mainSvcInfo.put("START_DATE", getAcceptTime());// 开始时间
         mainSvcInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
         mainSvcInfo.put("END_DATE", SysDateMgr.getTheLastTime());

         super.addTradeSvc(mainSvcInfo);
    }

    private void addTradeSvcToFV(IData map) throws Exception
    {

        IData mainSvcInfo = new DataMap();
        mainSvcInfo.put("USER_ID",  reqData.getUca().getUser().getUserId());// 用户标识
        mainSvcInfo.put("USER_ID_A",reqData.getGrpUca().getUserId());// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。
        mainSvcInfo.put("ELEMENT_ID", "190");//与服开商定服务
        mainSvcInfo.put("SERVICE_ID", "190");//与服开商定服务
        mainSvcInfo.put("PACKAGE_ID", "190");
        mainSvcInfo.put("MAIN_TAG", "0");// 主体服务标志：0-否，1-是
        mainSvcInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        mainSvcInfo.put("START_DATE", getAcceptTime());// 开始时间
        mainSvcInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        mainSvcInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        super.addTradeSvc(mainSvcInfo);
    }
    
    private void addImpuInfo(IData map) throws Exception
    {

    	String url1 = "";
    	String imsi = "";
    	String url2 = "@hain.ims.mnc000.mcc460.3gppnetwork.org";
    	String strTel = "+86"+reqData.getUca().getUser().getSerialNumber();
    	
    	IDataset ids = BofQuery.queryUserAllValidRes(reqData.getUca().getUser().getUserId(),"0898");
		
		if(IDataUtil.isNotEmpty(ids))
		{
			for(int i = 0 ; i < ids.size(); i++)
			{
				if("1".equals(ids.getData(i).getString("RES_TYPE_CODE")))
				{
					imsi = ids.getData(i).getString("IMSI");
					break;
				}
			}
		}
    	if(StringUtils.isNotBlank(imsi) && imsi.length() >= 5)
		{
			url1 = imsi + "@ims.mnc0"+imsi.substring(3, 5)+".mcc460.3gppnetwork.org";
		}
        IData mainImpuInfo = new DataMap();
        mainImpuInfo.put("INST_ID",  SeqMgr.getInstId());// 实例标识
        mainImpuInfo.put("MODIFY_TAG", "0");// 状态属性：0-增加，1-删除，2-变更
        mainImpuInfo.put("USER_ID",  reqData.getUca().getUser().getUserId());// 用户标识
        mainImpuInfo.put("TEL_URL", strTel);
        mainImpuInfo.put("SIP_URL", url1);
        mainImpuInfo.put("IMPI", url1);
        mainImpuInfo.put("START_DATE", getAcceptTime());
        mainImpuInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        mainImpuInfo.put("RSRV_STR2", getStrToChar(strTel));
        mainImpuInfo.put("RSRV_STR5", strTel+url2);
        super.addTradeImpu(mainImpuInfo);
    }
    
    private String getStrToChar(String strTel) {
		String tmp = strTel.toString();
		tmp = tmp.replaceAll("\\+", "");
		char[] c = tmp.toCharArray();
		String str2 = "";
		for(int i=c.length-1; i>=0; i--){
			
			str2 += String.valueOf(c[i]);
			str2 += ".";
		}
		str2 += "e164.arpa";
		return str2;
	}
    
    /**
     * 发送集客大厅企业视频彩铃业务完工短信  2019.1.15
     * @param map
     * @throws Exception
     */
    private void sendCL(IData map) throws Exception {
    	logger.debug("========map+===="+map);
        String grpProductUserId = reqData.getGrpUca().getUserId();
        String productId = reqData.getGrpUca().getProductId();
        
        logger.debug("========reqData.getGrpUca()+===="+reqData.getGrpUca());
        logger.debug("========productId+===="+productId);
        logger.debug("========grpProductUserId+===="+grpProductUserId);
        //本地资费编码
        String grpDiscntCode = "";
        String templateContent = "";

        IDataset grpDiscntInfos = UserDiscntInfoQry.queryUserAllDiscntByUserId(grpProductUserId);
        logger.debug("========grpDiscntInfos+===="+grpDiscntInfos);
        for (int i = 0; i < grpDiscntInfos.size(); i++) {
            IData grpDiscntInfo = grpDiscntInfos.getData(i);
            logger.debug("===========grpDiscntInfo+===="+grpDiscntInfo);
            grpDiscntCode = grpDiscntInfo.getString("DISCNT_CODE", "");
            logger.debug("===========grpDiscntCode+===="+grpDiscntCode);
        }

        String merchDiscnt = GrpCommonBean.productJKDTToMerch(grpDiscntCode, 1);
        String productSpecCode = GrpCommonBean.productJKDTToMerch(productId, 0);
        logger.debug("===========merchDiscnt+===="+merchDiscnt);
        logger.debug("===========productSpecCode+===="+productSpecCode);
        IDataset prodCommparaList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9090",
                productSpecCode, merchDiscnt);
        logger.debug("===========prodCommparaList==+===="+prodCommparaList);
        if (IDataUtil.isNotEmpty(prodCommparaList)) {
//            IDataset grp_infos = GrpInfoQry.queryUserGroupInfos(grpProductUserId, "0");
//            String grpSimpleName = grp_infos.getData(0).getString("CUST_NAME");// 获取集团简称或集团名
        	String grpSimpleName = reqData.getGrpUca().getCustomer().getCustName();// 获取集团简称或集团名
            String commParaMin = prodCommparaList.getData(0).getString("PARA_CODE2", "");// 属性值
            // 获取短信模板，拼短信内容1
            templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_CL_0098");
            if(!"".equals(templateContent)){
            	 templateContent = templateContent.replaceAll("@\\{GROUP_CUST_NAME\\}", grpSimpleName);
                 templateContent = templateContent.replaceAll("@\\{EFF_DATE_NAME\\}",
                 SysDateMgr.getCurMonth() + "月" + SysDateMgr.getCurDay() + "日");
                 templateContent = templateContent.replaceAll("@\\{MIN\\}",commParaMin);
                 logger.debug("===========templateContent==+===="+templateContent);
                 sendTradeSms(templateContent);

            }
                   }
            }

    

    /**
     * 发送完工短信
     * @param templateContent
     * @throws Exception
     */
    private void sendTradeSms(String templateContent) throws Exception {
    	
        DataMap smsdata = new DataMap();
        smsdata.put("TRADE_ID", getTradeId());
        smsdata.put("START_DATE", SysDateMgr.getSysTime());
        smsdata.put("END_DATE", SysDateMgr.getEndCycle20501231());
        smsdata.put("MODIFY_TAG", "0");

        smsdata.put("SMS_NOTICE_ID", SeqMgr.getSmsSendId()); // 流水编号SMS_NOTICE_ID

        smsdata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());// 地州编码
        smsdata.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
        smsdata.put("RECV_OBJECT",  reqData.getUca().getSerialNumber());
        smsdata.put("RECV_ID", smsdata.getString("RECV_ID", "-1"));// //因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务
        smsdata.put("RECV_OBJECT_TYPE", smsdata.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
        // smsdata.put("RSRV_STR3", map.getString("RECV_OBJECT"));//
        // 手机号,注意:此号为集团客户经理的手机号
        smsdata.put("USER_ID", "-1");// 因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务
        smsdata.put("NOTICE_CONTENT", templateContent);// 短信内容（子类自己拼）

        smsdata.put("SMS_NET_TAG", "0");//
        smsdata.put("CHAN_ID", "C002");//
        smsdata.put("SMS_TYPE_CODE", "20");// 20用户办理业务通知
        smsdata.put("SMS_PRIORITY", "1000");// 短信优先级
        smsdata.put("SMS_KIND_CODE", "12");// 02与SMS_TYPE_CODE配套
        // 具体看td_b_smstype
        smsdata.put("NOTICE_CONTENT_TYPE", "0");// 0指定内容发送
        smsdata.put("REFER_TIME", SysDateMgr.getSysTime());// 提交时间
        smsdata.put("FORCE_REFER_COUNT", "1");// 指定发送次数
        smsdata.put("MONTH", SysDateMgr.getCurMonth());// 月份
        smsdata.put("DAY", SysDateMgr.getCurDay()); // 日期

        smsdata.put("DEAL_TIME", SysDateMgr.getSysTime());// 完成时间
        smsdata.put("DEAL_STATE", "0");// 处理状态,0：未处理

        smsdata.put("CANCEL_TAG", "0");
        logger.debug("========smsdata+===="+smsdata);
        this.addTradeSms(smsdata);
    }


    /**
     * @description 处理台账Other子表的数据
     * @author luys 2017-12-19 15:20
     */
    public void infoRegOtherData() throws Exception{
        // 创建ENUM用户报文
        setRegTradeOther("8173", "01", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue(), "ENUM", "创建ENUM用户");
    }

    /**
     * 作用：写other表，用来发报文用
     *
     * @author luys 2017-12-19 15:20
     * @param serviceId
     * @param operCode
     * @param modifyTag
     * @throws Exception
     */
    public void setRegTradeOther(String serviceId, String operCode, String modifyTag, String valueCode, String rsrvValue) throws Exception{
        IData centreData = new DataMap();
        centreData.put("RSRV_VALUE_CODE", valueCode); // domain域
        centreData.put("RSRV_VALUE", rsrvValue);
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", serviceId); // 服务id
        centreData.put("RSRV_STR12", "221"); // HSS_SP_SIFC
        centreData.put("RSRV_STR20", "101"); // HSS_SPIFC_TEMPLATE_ID
        centreData.put("OPER_CODE", operCode); // 操作类型
        centreData.put("MODIFY_TAG", modifyTag);
        centreData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        this.addTradeOther(centreData);
    }

    /**
     * 作用：获取用户类型、角色、短号
     *
     * @author luys 2017-12-19 15:20
     * @throws Exception
     */
    public IData getUserTypeAndRoleShort()throws Exception{
        IData result=new DataMap();
        String netTypeCode = "00";
        String power = "1";
        String userType = "0"; // 用户类型
        String custId = reqData.getGrpUca().getCustId(); // 集团客户id
        String userIdB = reqData.getUca().getUserId(); // 成员用户id
        String eparchyCode = reqData.getUca().getUser().getEparchyCode(); // 成员地州
        netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别
        // 查impu信息
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(userIdB, eparchyCode);
        if (IDataUtil.isNotEmpty(impuInfo)){
            // 用户类型
            userType = impuInfo.getData(0).getString("RSRV_STR1", "");
        }
        if ("00".equals(netTypeCode)){
            // 1: 传统移动用户
            userType = "1";
        }

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = reqData.cd.getProductParamMap(mebProductId);
        if (IDataUtil.isEmpty(paramData)){
            paramData = new DataMap();
        }
        String shortCode = paramData.getString("SHORT_CODE","");
        paramData.put("CNRT_SHORT_CODE", shortCode); // 固话短号发报文

        String stype = GroupImsUtil.getMebOrderImsVpn(custId, userIdB) == true ? "1" : "0";
        // 短号有效标志，0-多媒体电话无效；1-融合V网有效
        paramData.put("ServiceType", stype);
        // 移除该字段，保证短号入attr表
        paramData.remove("OLD_SHORT_CODE");
        reqData.cd.putProductParamList(mebProductId, IDataUtil.iData2iDataset(paramData, "ATTR_CODE", "ATTR_VALUE")); // 参数插入attr表
        // impu表RSRV_STR4字段拼值
        String roleShort = power + "|" + shortCode;
        result.put("USER_TYPE", userType);
        result.put("ROLE_SHORT", roleShort);
        return result;
    }

    /**
     * @description 处理主台账表数据
     * @author luys
     * @date 2017-12-20
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        String specProductId = GrpCommonBean.productToMerch(reqData.getGrpUca().getProductId(), 0);
        if ("20004".equals(specProductId)) {
            data.put("RSRV_STR1", "2"); // 写死为2
        }
    }

    /**
     * @description 处理台账impu子表的数据
     * @param userType
     * @param roleShort
     * @throws Exception
     */
    public void infoRegDataImpu(String userType, String roleShort) throws Exception
    {
        // 查询是否存在IMPU信息；
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();
        String userId = reqData.getUca().getUser().getUserId(); // 成员userId
        String serialNumber = reqData.getUca().getSerialNumber(); // 成员sn
        String netTypecode = reqData.getUca().getUser().getNetTypeCode();

        String tmptype = "3";
        // 获取IMPU终端类型，如果没有，那么就取默认的3
        if (StringUtils.isBlank(userType))
        {
            if ("00".equals(netTypecode))
            {
                userType = "1"; // 1: 传统移动用户
            }
            else
            {
                userType = "0";
            }
            tmptype = "3";
        }
        else
        {
            tmptype = userType;
        }

        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(userId, userType, eparchyCode);
        if (IDataUtil.isEmpty(impuInfo))
        {
            IDataset dataset = new DatasetList();
            IData impuData = new DataMap();
            // 获取IMPI
            StringBuilder strImpi = new StringBuilder();
            genImsIMPI(serialNumber, strImpi, tmptype);
            // 获取IMPU
            StringBuilder strTel = new StringBuilder();
            StringBuilder strSip = new StringBuilder();
            genImsIMPU(serialNumber, strTel, strSip, tmptype);

            impuData.put("INST_ID", SeqMgr.getInstId());// 实例ID
            impuData.put("USER_ID", userId); // 用户标识
            impuData.put("TEL_URL", strTel); // 公有标识IMPU
            impuData.put("SIP_URL", strSip);
            impuData.put("IMPI", strImpi); // 私有标识IMPI
            impuData.put("IMS_USER_ID", serialNumber); // IMS门户网站用户名
            // 使用6位随机数密码
            String imsPassword = (!"".equals(reqData.getImsPassword()) ? reqData.getImsPassword() : StrUtil.getRandomNumAndChar(6));

            impuData.put("IMS_PASSWORD", imsPassword); // IMS门户网站密码
            impuData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());// 开始时间
            impuData.put("END_DATE", SysDateMgr.getTheLastTime());// 结束时间
            String tmp = strTel.toString();
            tmp = tmp.replaceAll("\\+", "");
            char[] c = tmp.toCharArray();
            String str2 = "";
            for (int i = c.length - 1; i >= 0; i--)
            {

                str2 += String.valueOf(c[i]);
                str2 += ".";
            }
            str2 += "e164.arpa";

            String str3 = "";
            for (int i = 4; i >= 0; i--)
            {

                str3 += String.valueOf(c[i]);
                str3 += ".";
            }
            str3 += "e164.arpa";

            impuData.put("RSRV_STR1", userType);
            impuData.put("RSRV_STR2", str2);
            impuData.put("RSRV_STR3", str3);
            impuData.put("RSRV_STR4", roleShort); // 成员角色|短号

            impuData.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
            dataset.add(impuData);
            addTradeImpu(dataset);
        }
        else
        {
            IDataset dataset = new DatasetList();
            IData impuData = new DataMap();
            impuData = impuInfo.getData(0);
            impuData.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.MODI.getValue());

            impuData.put("RSRV_STR4", roleShort); // 成员角色|短号

            dataset.add(impuData);
            addTradeImpu(dataset);
        }
    }

    /**
     * 生成IMPI
     *
     * @author luys
     * @throws Exception
     */
    public void genImsIMPI(String serialNumber, StringBuilder strImpi, String telType) throws Exception
    {

        String imsDomain = "@ims.ge.chinamobile.com";

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strImpi.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strImpi.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strImpi.append(serialNumber).append(imsDomain);
        }
    }

    /**
     * 生成IMPU
     *
     * @author luys
     * @throws Exception
     */
    public void genImsIMPU(String serialNumber, StringBuilder strTel, StringBuilder strSip, String telType) throws Exception
    {

        String imsDomain = "@ims.ge.chinamobile.com";

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strTel.append(serialNumber);
            strSip.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
    }

}
