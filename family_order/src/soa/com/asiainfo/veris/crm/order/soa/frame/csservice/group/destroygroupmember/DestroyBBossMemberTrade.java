
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossDcrossFusionBizDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossIAGWCloudMASDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossPayBizInfoDealbean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.GrpDisAttrTransBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class DestroyBBossMemberTrade extends DestroyGroupMember
{
    /*
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

    protected DestroyBBossMemberReqData reqData = null;
    
    protected String productOfferId = "";

    /**
     * @description 注销BB关系(该类重写基类方法，基类注销的表为UU表，而BBOSS侧注销的表为BB表)
     * @throws Exception
     */
    protected void actTradeRelationUU() throws Exception
    {
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IDataset relaList = RelaBBInfoQry.qryBB(reqData.getGrpUca().getUser().getUserId(), reqData.getUca().getUserId(), relationTypeCode, null);

        if (IDataUtil.isEmpty(relaList))
        {
            return;
        }

        IData relaData = relaList.getData(0);
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        relaData.put("END_DATE", getAcceptTime());

        super.addTradeRelationBb(relaData);
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 1- 成员与商品间的关系
        if (reqData.isMerchInfo)
        {
            // 1-1 判断成员是否为网外号码
            boolean isOutNetSn = this.isOutNetSn();
            if (!isOutNetSn)
            {
                return;
            }

            // 1-2 判断网外号码除了当前需要解除成员关系的产品，是否没有何其它成员产品存在成员关系
            boolean isLastRelaBB = this.isLastRelaBB();
            if (isLastRelaBB)
            {// 删除虚拟三户
                this.delVitualUserInfo();
            }
        }

        // 2- 成员与产品间的关系
        if (!reqData.isMerchInfo)
        {
            this.infoRegDataEntireMerchMeb();
            // 登记other表
            infoRegOther();

            // 某些业务需要登记特殊表
            infoRegDataSpecial();
        }
    }

    /*
     * @description 删除虚拟用户付费关系资料
     * @author xunyl
     * @date 2013-07-18
     */
    protected void dealVisualPayRelation() throws Exception
    {
        // 1- 获取成员用户编号
        String memUserId = reqData.getUca().getSerialNumber();

        // 2- 根据成员用户编号查找成员用户付费关系
        IData inparam = new DataMap();
        inparam.put("ID", memUserId);
        IData payrelaInfo = PayRelaInfoQry.getPayRelation(inparam);

        // 3- 更改用户付费关系状态
        payrelaInfo.put("MODIFY_TAG", "DEL");
        payrelaInfo.put("END_CYCLE_ID", SysDateMgr.getNowCyc());

        // 4- 登记成员付费关系订单表
        super.addTradePayrelation(payrelaInfo);
    }

    /*
     * @desciption 删除虚拟用户主体服务资料
     * @author xunyl Datae 2013-07-18
     */
    protected void dealVisualSvcInfo(IDataset svcdatas) throws Exception
    {
        // 1- 主体服务不存在直接退出
        if (null == svcdatas || svcdatas.size() == 0)
        {
            return;
        }

        // 2- 循环变更主体服务
        IDataset svcset = new DatasetList();
        for (int i = 0; i < svcdatas.size(); i++)
        {
            if (svcdatas.getData(i).getString("MAIN_TAG").equals("1"))
            {
                svcdatas.getData(i).put("MODIFY_TAG", "DEL");
                svcdatas.getData(i).put("END_DATE", getAcceptTime());
                svcset.add(svcdatas.getData(i));
            }
        }

        // 3- 登记主体服务订单表
        super.addTradeSvc(svcset);
    }

    /*
     * @description 删除虚拟用户主体服务状态资料
     * @author xunyl
     * @date 2013-07-18
     */
    protected void dealVisualSvcStateInfo(IDataset svcdatas) throws Exception
    {
        // 1- 主体服务不存在直接退出
        if (null == svcdatas || svcdatas.size() == 0)
        {
            return;
        }

        // 2- 获取并且变更主体服务状态信息(服务状态信息删除时，生成两条数据)
        IDataset svcStateResult = new DatasetList();
        for (int i = 0; i < svcdatas.size(); i++)
        {
            IData svcdata = svcdatas.getData(i);
            String memUserId = svcdata.getString("USER_ID");
            String serviceId = svcdata.getString("SERVICE_ID");
            IDataset svcStateInfoList = UserSvcStateInfoQry.getUserLastStateByUserSvc(memUserId, serviceId);
            for (int j = 0; j < svcStateInfoList.size(); j++)
            {
                IData oldSvcStateInfo = svcStateInfoList.getData(j);
                oldSvcStateInfo.put("MODIFY_TAG", "DEL");
                oldSvcStateInfo.put("END_DATE", getAcceptTime());

                IData newSvcStateInfo = new DataMap();
                newSvcStateInfo.putAll(oldSvcStateInfo);
                newSvcStateInfo.put("STATE_CODE", "1");// 销户
                newSvcStateInfo.put("START_DATE", getAcceptTime());
                newSvcStateInfo.put("MODIFY_TAG", "ADD");
                newSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());

                svcStateResult.add(oldSvcStateInfo);
                svcStateResult.add(newSvcStateInfo);
            }
        }

        // 3- 登记主体服务状态订单表
        super.addTradeSvcstate(svcStateResult);
    }

    /*
     * @descripiton 删除虚拟三户信息
     * @author xunyl Datae 2013-07-18
     */
    protected void dealVisualUserInfo(IData memUserInfo) throws Exception
    {
        memUserInfo.put("REMOVE_TAG", "2"); // 写死为2
        memUserInfo.put("DESTROY_TIME", getAcceptTime()); // 注销时间
        memUserInfo.put("MODIFY_TAG", "DEL"); // 修改标志
        memUserInfo.put("REMOVE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 注销地市
        memUserInfo.put("REMOVE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 注销市县
        memUserInfo.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
        super.addTradeUser(memUserInfo);
    }

    /*
     * @description 删除虚拟三户信息
     * @author xunyl
     * @date 2013-07-17
     */
    protected void delVitualUserInfo() throws Exception
    {
        // 1- 删除虚拟用户资料
        String memSerialNumber = reqData.getUca().getSerialNumber();
        IData memUserInfo = this.getMemUserInfo(memSerialNumber);
        this.dealVisualUserInfo(memUserInfo);

        // 2- 删除虚拟用户主体服务资料
        String memUserId = memUserInfo.getString("USER_ID");
        IDataset svcdatas = UserSvcInfoQry.qryUserSvcByUserId(memUserId);
        this.dealVisualSvcInfo(svcdatas);

        // 3- 删除虚拟用户主体服务状态资料
        this.dealVisualSvcStateInfo(svcdatas);

        // 4- 删除虚拟用户付费关系资料
        this.dealVisualPayRelation();
    }

    /*
     * @description 获取成员用户信息(虚拟三户信息原本属于网外号，因此直接按照网外号方式获取)
     * @author xunyl
     * @date 2013-07-18
     */
    protected IData getMemUserInfo(String memSerialNumber) throws Exception
    {
        // 1- 定义成员用户信息
        IData memUserInfo = new DataMap();

        // 2- 获取所有路由配置
        String[] connNames = Route.getAllCrmDb();
        if (connNames == null)
        {
            CSAppException.apperr(DbException.CRM_DB_9);
        }

        // 3- 循环路由配置获取成员用户信息
        IDataset memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);
        if (null == memberUserInfoList || memberUserInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_243, memSerialNumber);
        }
        memUserInfo = memberUserInfoList.getData(0);

        // 4- 返回用户信息
        return memUserInfo;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyBBossMemberReqData();
    }

    /**
     * chenyi 2014-4-15 处理流量统付业务 将账务所需数据登记MebCenPay表同步账务
     * 
     * @returnv
     * @throws Exception
     */
    private void infoMebCenPay() throws Exception
    {
        String userid = reqData.getUca().getUserId();
        IDataset cenPayData = BbossPayBizInfoDealbean.getDELmebcenpay(userid,productOfferId);

        this.addTradeMebCenpay(cenPayData);

    }

    /**
     * 业务台帐BBOSS产品成员子表
     * 
     * @throws Exception
     */
    public void infoRegDataEntireMerchMeb() throws Exception
    {
        String userId = reqData.getUca().getUserId();// 成员用户编号
        String ecUserId = reqData.getGrpUca().getUser().getUserId();// 集团产品用户编号
        String eparchCode = reqData.getUca().getUserEparchyCode();// 路由
        IDataset datas = UserGrpMerchMebInfoQry.getSEL_BY_USERID_USERIDA(userId, ecUserId, eparchCode);
        if (datas == null || datas.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_84, reqData.getUca().getUserId());
        }

        IData data = datas.getData(0);

        productOfferId=data.getString("PRODUCT_OFFER_ID");
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        data.put("STATUS", "Z");
        data.put("END_DATE", getAcceptTime());
        data.put("MEB_OPER_CODE", GroupBaseConst.BBOSS_MEB_STATUS.MEB_CANCLE.getValue()); // 删除
        this.addTradeGrpMerchMeb(data);
    }

    /**
     * chenyi 2014-7-14 特殊业务登记特殊表
     * 
     * @throws Exception
     */
    protected void infoRegDataSpecial() throws Exception
    {
        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
        // 如果为流量统付产品，则需要同步字段给账务
        if (isFlux)
        {
            infoMebCenPay();
        }
        
        String productId = reqData.getGrpProductId();

        // 判断当前是否是流量统付 ，而且是否定额模式，如果是，则需要生成付费关系
        String productSpecCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                                    { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
                                    { "1", "B", productId, "PRO" });// 获取集团产品编码
        //融合V网
        //if("20004".equals(productSpecCode)){
        if(BbossDcrossFusionBizDealBean.isBbossDcrossBizSendPF(productSpecCode)){
        	infoRegOtherData();
            infoRegDataImpu();
        }
        
        
        
//        // 如果是行业网关云MAS业务，需要处理blackwhite表
//        if (BbossIAGWCloudMASDealBean.isIAGWCloudMAS(GrpCommonBean.productToMerch(reqData.getGrpUca().getProductId(), 0)))
//        {
//            String operState = "02";  //成员新增：01 成员退订：02
//            IData blackWhiteData = BbossIAGWCloudMASDealBean.makDataForBlackWhite(reqData.getUca().getUser().getUserId(), reqData.getGrpUca().getUserId(), reqData.getGrpUca().getProductId(), reqData.getUca().getUser().getSerialNumber(), operState);
//            this.addTradeBlackwhite(blackWhiteData);
//        }
    }

    /*
     * @description 登记other表信息，供服务开通用
     * @author xunyl
     * @date 2013-08-26
     */
    protected void infoRegOther() throws Exception
    {
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "78101");// 服务开通侧用成员的service_id对应为78101
        serviceInfo.put("OPER_CODE", "07");// 服务开通，成员注销约定操作类型为07
        serviceInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", getAcceptTime());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());
        this.addTradeOther(serviceInfo);
    }

    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-25
     */
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (DestroyBBossMemberReqData) getBaseReqData();
    }

    /*
     * @description 判断网外号码除了当前需要解除成员关系的产品，是否没有和其它成员产品存在成员关系
     * @author xunyl
     * @date 2013-07-17
     */
    protected boolean isLastRelaBB() throws Exception
    {
        // 1- 定义返回值
        boolean isLastRelaBB = true;

        // 2- 根据成员用户编号查询成员与商品间的BB关系(资料表)
        String productId = reqData.getGrpUca().getProductId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
        IDataset mebBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(reqData.getUca().getUserId(), relationTypeCode, BizRoute.getRouteId());

        // 3- 获取该成员用户与其它商品间的BB关系数目(非当前正在解除关系的商产品)
        int mebBBInfoCount = mebBBInfoList.size();
        for (int i = 0; i < mebBBInfoCount; i++)
        {
            String userIdA = mebBBInfoList.getData(i).getString("USER_ID_A");
            if (userIdA.equals(reqData.getGrpUca().getUser().getUserId()) || userIdA.equals(reqData.getProductUserId()))
            {
                mebBBInfoCount--;
            }
        }

        // 4- 根据成员用户编号查询成员成员与商品间的BB关系(台账表)
        IDataset userTradeBBInfoList = TradeRelaBBInfoQry.getNotFinishTradeBB(reqData.getUca().getUserId(), relationTypeCode, BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(userTradeBBInfoList))
        {
            for (int j = 0; j < userTradeBBInfoList.size(); j++)
            {
                if (userTradeBBInfoList.getData(j).getString("MODIFY_TAG").equals("0"))
                {
                    mebBBInfoCount++;
                    isLastRelaBB = false;
                    break;
                }
                else if (userTradeBBInfoList.getData(j).getString("MODIFY_TAG").equals("1"))
                {
                    mebBBInfoCount--;
                }
            }
        }

        // 5- 校验是否需要注销
        if (mebBBInfoCount != 0)
        {
            isLastRelaBB = false;
        }

        // 6- 返回结果
        return isLastRelaBB;
    }

    /*
     * @description 判断成员是否为网外号码
     * @author xunyl
     * @date 2013-07-17
     */
    protected boolean isOutNetSn() throws Exception
    {
        // 1- 定义返回值
        boolean isOutNetSn = true;

        // 2- 获取成员手机号码
        String memSerialNumber = reqData.getUca().getSerialNumber();

        // 3- 判断手机号码是否为本地号码
        if ("0".equals(memSerialNumber.substring(0, 1)))
        {// 铁通号
            isOutNetSn = ParamInfoQry.isExistLocalSerialnumber(memSerialNumber);
        }
        else
        {
            IData mofficeInfo = RouteInfoQry.getMofficeInfoBySn(memSerialNumber);
            if (IDataUtil.isNotEmpty(mofficeInfo))
            {
                isOutNetSn = false;
            }
        }

        // 4- 返回结果
        return isOutNetSn;
    }

    /*
     * @description 将前台传递过来的BBOSS数据放入RD中
     * @author xunyl
     * @date 2013-04-25
     */
    public void makBBossReqData(IData map) throws Exception
    {
        // 1- 获取商/产品信息标志
        boolean isMerchInfo = map.getBoolean("IS_MERCH_INFO");

        // 2- 添加商/产品信息标志至RD
        reqData.setMerchInfo(isMerchInfo);

        // 3- 商品信息的场合，需要添加产品用户编号至RD(虚拟三户销户用)
        reqData.setProductUserId(map.getString("PRODUCT_USER_ID"));
    }

    /*
     * (non-Javadoc)
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-25
     */
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
     * @description 成员注销时，成员参数不发送服务开通
     * @author xunyl
     * @date 2013-12-03
     */
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);
        if ("998101".equals(reqData.getGrpUca().getProductId()) && ("1001".equals(map.get("ATTR_CODE")) || "1002".equals(map.get("ATTR_CODE")))) {
        	map.put("IS_NEED_PF", "1");// 1或者是空： 发指令 0不发指令
        } else {
        	map.put("IS_NEED_PF", "0");// 1或者是空： 发指令 0不发指令
        }
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

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为1，针对省行业网关云MAS的情况进行排除
        String specProductId = GrpCommonBean.productToMerch(reqData.getGrpUca().getProductId(), 0);
        IData data = bizData.getTrade();
        if (reqData.isMerchInfo)
        {
            data.put("OLCOM_TAG", "0");
        }
        else if ("6".equals(CSBizBean.getVisit().getInModeCode()) && !BbossIAGWCloudMASDealBean.isIAGWCloudMAS(specProductId) && !BbossDcrossFusionBizDealBean.isBbossDcrossBizSendPF(specProductId))
        {// 渠道类型为IBOSS
            data.put("OLCOM_TAG", "0");
        }
        else
        {
            data.put("OLCOM_TAG", "1");
        }
    }
    
    /**
     * @description 处理付费关系方法重写，支持payrelaiton表的单独注销
     * @author xunyl
     * @date 2015-07-03
     */
    protected void actTradePayRela() throws Exception
    {
        //1- 调用基类方法，实现普通的付费关系能够正常注销
        super.actTradePayRela();
        
        //2- 查询是否有特殊的付费关系（例如集团客户一点支付业务，付费关系是BBOSS侧登记的，因此需要自己注销）
        String memUserId = reqData.getUca().getUserId();
        IDataset payRelaInfoList = PayRelaInfoQry.getPayrelationInfo(memUserId);
        if(IDataUtil.isEmpty(payRelaInfoList)){
            return;
        }
        
        //3- 获取成员与该BBOSS集团产品用户下的特殊高级付费关系
        String grpUserId = reqData.getGrpUca().getUserId();
        for(int i =0;i< payRelaInfoList.size();i++){
            IData payRelaInfo = payRelaInfoList.getData(i);
            String currGrpUserId = payRelaInfo.getString("RSRV_STR1","");
            if(StringUtils.equals(grpUserId, currGrpUserId)){
                addTradePayrelaForBboss(payRelaInfo);
            }
        }               
    }
    
    /**
     * @description 比较基类获得的高级付费关系中是否有该BBOSS集团产品与成员之间的付费关系，没有则加入注销列表
     * @author xunyl
     * @date 2015-07-04
     */
    private  void addTradePayrelaForBboss(IData payRelaInfo)throws Exception{
        //1- 获取基类登记的集团付费关系
        IDataset tradePayRelationInfoList = bizData.getTradePayrelation();
        if(IDataUtil.isEmpty(tradePayRelationInfoList)){
            payRelaInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            payRelaInfo.put("END_CYCLE_ID", SysDateMgr.getNowCyc());
            super.addTradePayrelation(payRelaInfo);
            return;
        }
        
        //2- 获取当前付费关系中的集团用户编号
        String grpUserId = payRelaInfo.getString("RSRV_STR1","");
        
        //3- 循环基类中的付费关系列表，查看当前的付费关系在基类付费关系列表中是否已经存在，不存在则添加
        for(int i=0;i<tradePayRelationInfoList.size();i++){
            IData tradePayRelationInfo = tradePayRelationInfoList.getData(i);
            String tempGrpUserId = tradePayRelationInfo.getString("RSRV_STR1","");
            if(StringUtils.equals(grpUserId,tempGrpUserId)){
                break;
            }  
            if(i==tradePayRelationInfoList.size()-1){
                payRelaInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                payRelaInfo.put("END_CYCLE_ID", SysDateMgr.getNowCyc());
                super.addTradePayrelation(payRelaInfo);
            }
        }        
    }
    
    /**
     * @description 处理台账Other子表的数据
     * @author luys
     * @date 2017-12-20
     */
    public void infoRegOtherData() throws Exception{
    	String user_id = reqData.getUca().getUserId();
    	String product_id = reqData.getUca().getProductId();
    	IData enumData = new DataMap();
        enumData.put("USER_ID", user_id);
        enumData.put("RSRV_VALUE_CODE", "ENUM");// domain域
        enumData.put("RSRV_VALUE", "删除ENUM用户");
        enumData.put("RSRV_STR1", product_id);// 产品ID

        enumData.put("RSRV_STR9", "8173"); // 服务id
        enumData.put("OPER_CODE", "02"); // 操作类型 02 删除

        enumData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        enumData.put("START_DATE", getAcceptTime());
        enumData.put("END_DATE", getAcceptTime()); // 立即截止
        enumData.put("INST_ID", SeqMgr.getInstId());
        addTradeOther(enumData);
    }
    
    /**
     * @description 将手机用户登记在impu表
     * @author luys
     * @date 2017-12-20
     * @throws Exception
     */
    public void infoRegDataImpu() throws Exception{
    	String netTypeCode = "00";
    	String userType = "0"; // 用户类型
		String userIdB = reqData.getUca().getUserId(); // 成员用户id
	    String eparchyCode = reqData.getUca().getUser().getEparchyCode(); // 成员地州
	    netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别
	     // 查impu信息
	    IDataset impuInfo1 = UserImpuInfoQry.queryUserImpuInfo(userIdB, eparchyCode);
	    if (IDataUtil.isNotEmpty(impuInfo1)){
	     	// 用户类型
	         userType = impuInfo1.getData(0).getString("RSRV_STR1", "");
	    }
	    if ("00".equals(netTypeCode)){
	     	// 1: 传统移动用户
	         userType = "1"; 
	    }

        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(userIdB, userType, eparchyCode);
        if (IDataUtil.isNotEmpty(impuInfo))
        {
            IDataset dataset = new DatasetList();
            for (int i = 0, size = impuInfo.size(); i < size; i++)
            {
                IData impuData = (IData) impuInfo.get(i);
                String rsrvstr1 = impuData.getString("RSRV_STR1");
                if ("1".equals(rsrvstr1))
                {
                    impuData.put("RSRV_STR5", "0");// 用于信控，标识为1是发了HSS,ENUM信息，为0则是发了后又取消
                    impuData.put("RSRV_STR4", "");
                    impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataset.add(impuData);
                }
                else
                {
                    impuData.put("RSRV_STR4", "");
                    impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataset.add(impuData);
                }
            }
            addTradeImpu(dataset);
        }
    }

}