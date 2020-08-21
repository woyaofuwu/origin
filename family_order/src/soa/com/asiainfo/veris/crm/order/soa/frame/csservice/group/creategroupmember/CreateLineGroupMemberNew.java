package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import java.util.Iterator;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.Encryptor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpBaseAudiInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ElementTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupTradeBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupProductUtil;

public class CreateLineGroupMemberNew extends GroupTradeBaseBean {

    protected GrpModuleData moduleData = new GrpModuleData();

    protected CreateLineGroupMemberReqData reqData = null;

    protected String tradeId = "";

    /** 生成登记信息
     * 
     * @throws Exception */
    public void actTradeBefore() throws Exception {

        super.actTradeBefore();

        //   actTradeDiscntBefore();

        // actTradeSvcBefore();
    }

    @Override
    public void actTradeSub() throws Exception {
        super.actTradeSub();

        // 成员用户资料
        actTradeUser();

        // 产品子表
        actTradePrdAndPrdParam();

        // 服务状态表
        super.actTradeSvcState();

        //处理付费关系
        actTradePayRela();

        //处理默认付费关系
        //actTradeDefaultPayRela();

        //处理UU关系
        actTradeRelationUU();

        // 处理新增账户
        if(reqData.isAcctIsAdd()) {
            actTradeAccountInfo();
        }

        // 处理工单依赖
        // infoRegDataTradeLimit();
    }

    /** 新增账户
     * 
     * @throws Exception */
    protected void actTradeAccountInfo() throws Exception {
        IData accountData = reqData.getUca().getAccount().toData();

        // 账户
        if(IDataUtil.isNotEmpty(accountData) && reqData.isAcctIsAdd()) {
            super.addTradeAccount(accountData);

            IData acctConsignData = reqData.getACCT_CONSIGN();

            if(IDataUtil.isNotEmpty(acctConsignData)) {
                super.addTradeAcctConsign(acctConsignData);
            }
        }
    }

    protected void actTradePayRela() throws Exception {
        // 付费计划
        IDataset payPlanList = reqData.cd.getPayPlan();

        if(IDataUtil.isNotEmpty(payPlanList)) {
            super.addTradeUserPayplan(payPlanList);
        }

        // 付费关系
        IDataset payRelaList = reqData.cd.getPayRelation();

        if(IDataUtil.isNotEmpty(payRelaList)) {
            super.addTradePayrelation(payRelaList);
        }

        // 特殊付费
        IDataset specialPayList = reqData.cd.getSpecialPay();

        if(IDataUtil.isNotEmpty(specialPayList)) {
            super.addTradeUserSpecialepay(specialPayList);
        }
    }

    /*protected void actTradeDefaultPayRela() throws Exception {
        // 新增付费关系
        IData addPayRelaData = new DataMap();
    
        addPayRelaData.put("USER_ID", reqData.getUca().getUserId());
        addPayRelaData.put("ACCT_ID", reqData.getUca().getAcctId());
        addPayRelaData.put("PAYITEM_CODE", "-1");
        addPayRelaData.put("ACCT_PRIORITY", "0"); // 账户优先级
        addPayRelaData.put("USER_PRIORITY", "0"); // 用户优先级
        addPayRelaData.put("BIND_TYPE", "0"); // 账户绑定方式
        addPayRelaData.put("DEFAULT_TAG", "1"); // 默认标志
        addPayRelaData.put("ACT_TAG", "1"); // 作用标志
        addPayRelaData.put("LIMIT_TYPE", "0"); // 限定方式
        addPayRelaData.put("LIMIT", "0"); // 限定值
        addPayRelaData.put("COMPLEMENT_TAG", "0"); // 限定值
        addPayRelaData.put("INST_ID", SeqMgr.getInstId()); // inst_id
        addPayRelaData.put("START_CYCLE_ID", SysDateMgr.getNowCyc()); // 开始账期
        // 基类有对6位的处理
        addPayRelaData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231()); // 开始账期
        addPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        super.addTradePayrelation(addPayRelaData);
    }*/

    protected void actTradeRelationUU() throws Exception {
        IData relaData = new DataMap();

        relaData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        relaData.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
        relaData.put("USER_ID_B", reqData.getUca().getUserId());
        relaData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
        relaData.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", reqData.getMemRoleB());
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeRelation(relaData);
    }

    @Override
    public IData getProductInfoByElement(IDataset productInfoCaches, IData elementData) throws Exception
    {
        String productId = elementData.getString("PRODUCT_ID");
        if(StringUtils.isBlank(productId) || StringUtils.equals("-1", productId)) {
            return null;
        }

        if(IDataUtil.isEmpty(productInfoCaches)) {
            IDataset tradeProducts = bizData.getTradeProduct();
            IDataset userProducts = BofQuery.getUserAllProducts(elementData.getString("USER_ID"), BizRoute.getRouteId());

            IDataset future = new DatasetList();
            if(userProducts != null && tradeProducts != null) {
                future = DataBusUtils.getFuture(userProducts, tradeProducts, new String[] { "INST_ID" });
                future = DataBusUtils.filterInValidDataByEndDate(future);
            }

            productInfoCaches.addAll(future);
        }

        for (int i = 0, size = productInfoCaches.size(); i < size; i++) {
            IData userProduct = productInfoCaches.getData(i);
            if(StringUtils.equals(userProduct.getString("PRODUCT_ID"), productId)) {
                return userProduct;
            }
        }

        return null;
    }

    /*protected void actTradeDiscntBefore() throws Exception{
    	IDataset dataset = reqData.cd.getDiscnt();
    	
    	if(IDataUtil.isEmpty(dataset)){
    		return;
    	}
    	//成员暂无资费，先去掉所有资费
    	//IDataset mebSvcDatas = UProductElementInfoQry.queryForceDiscntsByProductId(reqData.getUca().getProductId());
    	//System.out.println(mebSvcDatas);
    	dataset.clear();
    }*/

    /*protected void actTradeSvcBefore() throws Exception{
    	IDataset dataset = reqData.cd.getSvc();
    	
    	IDataset mebSvcDatas = UProductElementInfoQry.queryPackageElementsByProductIdPackageId(reqData.getUca().getProductId(),"0",reqData.getUca().getUserEparchyCode());
    	
    	IData mebSvcData = new DataMap();
    	if(mebSvcDatas != null && mebSvcDatas.size()>1){
    		for(int i =0;i<mebSvcDatas.size();i++){
    			IData svcData = mebSvcDatas.getData(i);
    			if("S".endsWith(svcData.getString("ELEMENT_TYPE_CODE",""))){
    				String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(svcData.getString("PRODUCT_ID"), "0", svcData.getString("ELEMENT_ID"));
    				if("1".equals(mainTag)){
    					mebSvcData = svcData;
    					break;
    				}
    			}
    		}
    	}
    	
    	if(IDataUtil.isEmpty(dataset) && IDataUtil.isNotEmpty(mebSvcData)){
    		mebSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
    		String isNeedPf = GrpPfUtil.getSvcPfState(mebSvcData.getString("MODIFY_TAG"), reqData.getUca().getUserId(), mebSvcData.getString("ELEMENT_ID"));
    		mebSvcData.put("IS_NEED_PF", isNeedPf);
    		mebSvcData.put("PACKAGE_ID", "0");
    		mebSvcData.put("PRODUCT_ID", reqData.getBaseMebProductId());
    		mebSvcData.put("PRODUCT_MODE", GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.toString());
    		mebSvcData.put("INST_ID", SeqMgr.getInstId());
    		mebSvcData.put("SERVICE_ID",mebSvcData.getString("ELEMENT_ID"));
    		mebSvcData.put("RSRV_NUM1", "0");
    		mebSvcData.put("RSRV_NUM2", "0");
    		mebSvcData.put("RSRV_NUM3", "0");
    		mebSvcData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
    		mebSvcData.put("START_DATE",SysDateMgr.getSysTime());
    		dataset = new DatasetList();
    		dataset.add(mebSvcData);
    		reqData.cd.putSvc(dataset);
    		return;
    	}
    	
    	IData data = new DataMap();
    	if(dataset.size()>1){
    		for(int i =0;i<dataset.size();i++){
    			IData svcData = dataset.getData(i);
    			if("S".endsWith(svcData.getString("ELEMENT_TYPE_CODE",""))){
    				String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(svcData.getString("PRODUCT_ID"), svcData.getString("PACKAGE_ID"), svcData.getString("SERVICE_ID"));
    				if("1".equals(mainTag)){
    					data = svcData;
    					break;
    				}
    			}
    		}
    	}else{
    		data = dataset.first();
    	}
    	
    	if(IDataUtil.isEmpty(data) || IDataUtil.isEmpty(mebSvcData)){
    		return;
    	}
    	String elementId = mebSvcData.getString("ELEMENT_ID");
    	data.put("ELEMENT_ID", elementId);
    	data.put("SERVICE_ID", elementId);
    	data.put("PRODUCT_ID", reqData.getUca().getProductId());
    	data.put("PRODUCT_MODE", GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue());
    }*/

    protected void actTradePrdAndPrdParam() throws Exception {
        IData productIdData = reqData.cd.getProductIdSet();

        // 成员基本产品
        String baseMebProductId = reqData.getUca().getProductId();//ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 添加主产品信息
        productIdData.put(baseMebProductId, TRADE_MODIFY_TAG.Add.getValue());

        IDataset productList = new DatasetList();

        Iterator<String> iterator = productIdData.keySet().iterator();
        while (iterator.hasNext()) {
            String productId = iterator.next();

            String productMode = UProductInfoQry.getProductModeByProductId(productId);

            IData productData = new DataMap();

            // 产品INST_ID
            String instId = SeqMgr.getInstId();
            productData.put("PRODUCT_ID", productId);
            productData.put("PRODUCT_MODE", productMode);

            productData.put("USER_ID", reqData.getUca().getUser().getUserId()); // 实例标识
            productData.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId()); // 实例标识
            productData.put("INST_ID", instId); // 实例标识
            productData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime()); // 开始时间
            productData.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
            productData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            productList.add(productData);

            if(productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue())) {
                IDataset productParam = reqData.cd.getProductParamList(productId);
                if(IDataUtil.isNotEmpty(productParam)) {
                    // 过滤以NOTIN_开头的属性，这种属性不需要插表
                    super.filterParamAttr("NOTIN_", productParam);

                    IDataset dataset = new DatasetList();
                    for (int i = 0, iSzie = productParam.size(); i < iSzie; i++) {
                        IData paramData = productParam.getData(i);
                        String keyParam = paramData.getString("ATTR_CODE");
                        String valueParam = paramData.getString("ATTR_VALUE");

                        if(keyParam.equals("FEE_MON_SHORT")) {
                            if("".equals(valueParam)) {
                                valueParam = "0";
                                continue;
                            } else
                                valueParam = String.valueOf(100 * Integer.parseInt(valueParam));
                        }
                        IData map = new DataMap();
                        map.put("USER_ID", reqData.getUca().getUser().getUserId());
                        map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
                        map.put("INST_TYPE", "P");
                        map.put("RELA_INST_ID", instId);
                        map.put("INST_ID", SeqMgr.getInstId());
                        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        map.put("ATTR_CODE", keyParam);
                        map.put("ATTR_VALUE", valueParam);
                        map.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                        map.put("END_DATE", SysDateMgr.getTheLastTime());
                        dataset.add(map);
                    }

                    super.addTradeAttr(dataset);
                }
            }

        }

        reqData.cd.putProduct(productList);
        super.addTradeProduct(productList);
    }

    protected void actTradeUser() throws Exception {

        IData userData = reqData.getUca().getUser().toData();

        // 用户
        if(IDataUtil.isNotEmpty(userData)) {
            // 设置用户密码
            String userPasswd = reqData.getUca().getUser().getUserPasswd();

            if(!"".equals(userPasswd)) {
                userPasswd = Encryptor.fnEncrypt(userPasswd, reqData.getUca().getUser().getUserId().substring(reqData.getUca().getUser().getUserId().length() - 9));
            }

            userData.put("USER_PASSWD", userPasswd); // 用户密码

            userData.put("IN_DATE", getAcceptTime());
            userData.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
            userData.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

            userData.put("OPEN_DATE", getAcceptTime());
            userData.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
            userData.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
            userData.put("REMOVE_TAG", "0");

            userData.put("DEVELOP_DEPART_ID", reqData.getUca().getUser().getDevelopDepartId()); // 发展渠道
            userData.put("DEVELOP_CITY_CODE", reqData.getUca().getUser().getDevelopCityCode()); // 发展业务区

            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 应为前台传过来

            userData.put("USER_DIFF_CODE", ""); // 海南取消用户类别
        }

        this.addTradeUser(userData);
    }

    protected void regTrade() throws Exception {
        super.regTrade();
        IData data = bizData.getTrade();

        //data.put("RSRV_STR9", tradeId);

        data.put("CUST_ID", reqData.getUca().getGrpCustId()); // 客户标识
        data.put("CUST_NAME", reqData.getUca().getCustGroup().getCustName()); // 客户名称
        data.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
        data.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识

        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber()); // 服务号码

        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", reqData.getUca().getProductId()); // 产品标识
        data.put("BRAND_CODE", reqData.getUca().getBrandCode()); // 品牌编码

        data.put("CUST_ID_B", reqData.getGrpUca().getUser().getCustId()); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1。
        data.put("USER_ID_B", reqData.getGrpUca().getUserId()); // 用户标识B：关联业务中的B用户标识，通常为一集团用户或虚拟用户。对于非关联业务填-1。
        data.put("ACCT_ID_B", reqData.getGrpUca().getAcctId()); // ????
        data.put("SERIAL_NUMBER_B", reqData.getGrpUca().getSerialNumber());
    }

    @Override
    protected void makUca(IData map) throws Exception {
        super.makUca(map);
        
        makUcaForMebNormal(map);
        reqData.getUca().getUser().setRsrvStr1("EOS");
    }

    protected final void makUcaForMebNormal(IData map) throws Exception {

        makUcaForGroup(map);

        String mebBaseProductId = map.getString("MEB_PRODUCT_ID");//ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        reqData.setBaseMebProductId(mebBaseProductId);

        if(!reqData.isAcctIsAdd()) {
            map.put("ACCT_ID", reqData.getGrpUca().getAcctId());//专线成员默认使用集团账户
        }
        //map.put("PRODUCT_ID", mebBaseProductId);
        makUcaForMebOpen(map);
    }

    protected void makUcaForGroup(IData map) throws Exception {
        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");

        UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);

        if(grpUCA == null) {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", grpSerialNumber);
            grpUCA = UcaDataFactory.getNormalUcaBySnForGrp(param);
            /*String batTag = map.getString("BAT_TAG");
            try{
            	grpUCA = UcaDataFactory.getNormalUcaBySnForGrp(param);
            }catch(Exception e){
            	if("ture".equals(batTag)){
            		String productId = map.getString("PRODUCT_ID");
            		param.put("CUST_ID", map.getString("CUST_ID"));
            		param.put("ACCT_ID", map.getString("ACCT_ID"));
            		grpUCA = UcaDataFactory.getNormalUcaByCustIdForGrp(param);
            		IData userInfo = new DataMap();
            		userInfo.put("USER_ID", map.getString("GRP_USER_ID"));
            		userInfo.put("CUST_ID", grpUCA.getCustGroup().getCustId());
            		userInfo.put("SERIAL_NUMBER", grpSerialNumber);
            		userInfo.put("PRODUCT_ID", productId);
            		UserTradeData utd = new UserTradeData(userInfo);
            		grpUCA.setUser(utd);
            		grpUCA.setProductId(productId);
            		grpUCA.setBrandCode(UProductInfoQry.getBrandCodeByProductId(productId));
            	}else{
            		CSAppException.apperr(BofException.CRM_BOF_017,grpSerialNumber);
            	}
            }*/
        }
        reqData.setGrpUca(grpUCA);
    }

    protected void makUcaForMebOpen(IData map) throws Exception {
        UcaData uca = UcaDataFactory.getNormalUcaByCustIdForGrp(map);

        IData baseUserInfo = map.getData("USER_INFO");

        if(IDataUtil.isEmpty(baseUserInfo)) {
            baseUserInfo = new DataMap();
        }

        // 生成用户序列
        String userId = SeqMgr.getUserId();

        // 得到数据
        String productId = reqData.getBaseMebProductId();// 必须传
        String serialNumber = map.getString("SERIAL_NUMBER");

        IData userInfo = new DataMap();
        userInfo.put("USER_ID", userId);// 用户标识
        userInfo.put("CUST_ID", uca.getCustGroup().getCustId()); // 归属客户标识
        userInfo.put("USECUST_ID", baseUserInfo.getString("USECUST_ID", uca.getCustGroup().getCustId())); // 使用客户标识：如果不指定，默认为归属客户标识

        userInfo.put("EPARCHY_CODE", baseUserInfo.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 归属地市
        userInfo.put("CITY_CODE", baseUserInfo.getString("CITY_CODE", CSBizBean.getVisit().getCityCode())); // 归属业务区

        userInfo.put("CITY_CODE_A", baseUserInfo.getString("CITY_CODE_A", ""));
        userInfo.put("USER_PASSWD", baseUserInfo.getString("USER_PASSWD", "")); // 用户密码
        userInfo.put("USER_DIFF_CODE", baseUserInfo.getString("USER_DIFF_CODE", "")); // 用户类别
        userInfo.put("USER_TYPE_CODE", baseUserInfo.getString("USER_TYPE_CODE", "8")); // 用户类型
        userInfo.put("USER_TAG_SET", baseUserInfo.getString("USER_TAG_SET", ""));

        // 用户标志集：主要用来做某些信息的扩充，如：催缴标志、是否可停机标志，对于这个字段里面第几位表示什么含义在扩展的时候定义
        userInfo.put("USER_STATE_CODESET", baseUserInfo.getString("USER_STATE_CODESET", "0")); // 用户主体服务状态集：见服务状态参数表
        userInfo.put("NET_TYPE_CODE", baseUserInfo.getString("NET_TYPE_CODE", "00")); // 网别编码

        userInfo.put("SERIAL_NUMBER", serialNumber);// 必须由前台传,对于第3放接口,需要根据in_mode_code后台构造sn

        userInfo.put("SCORE_VALUE", baseUserInfo.getString("SCORE_VALUE", "0")); // 积分值
        userInfo.put("CONTRACT_ID", baseUserInfo.getString("CONTRACT_ID", "")); // 合同号

        userInfo.put("CREDIT_CLASS", baseUserInfo.getString("CREDIT_CLASS", "0")); // 信用等级
        userInfo.put("BASIC_CREDIT_VALUE", baseUserInfo.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
        userInfo.put("CREDIT_VALUE", baseUserInfo.getString("CREDIT_VALUE", "0")); // 信用度
        userInfo.put("CREDIT_CONTROL_ID", baseUserInfo.getString("CREDIT_CONTROL_ID", "0")); // 信控规则标识
        userInfo.put("ACCT_TAG", baseUserInfo.getString("ACCT_TAG", "0")); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        userInfo.put("PREPAY_TAG", baseUserInfo.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
        userInfo.put("MPUTE_MONTH_FEE", baseUserInfo.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        userInfo.put("MPUTE_DATE", baseUserInfo.getString("MPUTE_DATE", "")); // 月租重算时间
        userInfo.put("FIRST_CALL_TIME", baseUserInfo.getString("FIRST_CALL_TIME", "")); // 首次通话时间
        userInfo.put("LAST_STOP_TIME", baseUserInfo.getString("LAST_STOP_TIME", "")); // 最后停机时间
        userInfo.put("CHANGEUSER_DATE", baseUserInfo.getString("CHANGEUSER_DATE", "")); // 过户时间
        userInfo.put("IN_NET_MODE", baseUserInfo.getString("IN_NET_MODE", "")); // 入网方式
        userInfo.put("IN_DATE", baseUserInfo.getString("IN_DATE", getAcceptTime())); // 建档时间
        userInfo.put("IN_STAFF_ID", baseUserInfo.getString("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        userInfo.put("IN_DEPART_ID", baseUserInfo.getString("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        userInfo.put("OPEN_MODE", baseUserInfo.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        userInfo.put("OPEN_DATE", baseUserInfo.getString("OPEN_DATE", getAcceptTime())); // 开户时间
        userInfo.put("OPEN_STAFF_ID", baseUserInfo.getString("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId())); // 开户员工
        userInfo.put("OPEN_DEPART_ID", baseUserInfo.getString("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 开户渠道
        userInfo.put("DEVELOP_STAFF_ID", baseUserInfo.getString("DEVELOP_STAFF_ID", "")); // 发展员工
        userInfo.put("DEVELOP_DATE", baseUserInfo.getString("DEVELOP_DATE", getAcceptTime())); // 发展时间
        userInfo.put("DEVELOP_DEPART_ID", baseUserInfo.getString("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 发展渠道
        userInfo.put("DEVELOP_CITY_CODE", baseUserInfo.getString("DEVELOP_CITY_CODE", CSBizBean.getVisit().getCityCode())); // 发展市县
        userInfo.put("DEVELOP_EPARCHY_CODE", baseUserInfo.getString("DEVELOP_EPARCHY_CODE", CSBizBean.getUserEparchyCode())); // 发展地市
        userInfo.put("DEVELOP_NO", baseUserInfo.getString("DEVELOP_NO", "")); // 发展文号
        userInfo.put("ASSURE_CUST_ID", baseUserInfo.getString("ASSURE_CUST_ID", "")); // 担保客户标识
        userInfo.put("ASSURE_TYPE_CODE", baseUserInfo.getString("ASSURE_TYPE_CODE", "")); // 担保类型
        userInfo.put("ASSURE_DATE", baseUserInfo.getString("ASSURE_DATE", "")); // 担保期限

        // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        userInfo.put("REMOVE_TAG", baseUserInfo.getString("REMOVE_TAG", "0")); //

        userInfo.put("PRE_DESTROY_TIME", baseUserInfo.getString("PRE_DESTROY_TIME", "")); // 预销号时间
        userInfo.put("DESTROY_TIME", baseUserInfo.getString("DESTROY_TIME", "")); // 注销时间
        userInfo.put("REMOVE_EPARCHY_CODE", baseUserInfo.getString("REMOVE_EPARCHY_CODE", "")); // 注销地市
        userInfo.put("REMOVE_CITY_CODE", baseUserInfo.getString("REMOVE_CITY_CODE", "")); // 注销市县
        userInfo.put("REMOVE_DEPART_ID", baseUserInfo.getString("REMOVE_DEPART_ID", "")); // 注销渠道
        userInfo.put("REMOVE_REASON_CODE", baseUserInfo.getString("REMOVE_REASON_CODE", "")); // 注销原因
        userInfo.put("REMARK", baseUserInfo.getString("REMARK", "")); // 备注

        userInfo.put("RSRV_NUM1", baseUserInfo.getString("RSRV_NUM1", "")); // 预留数值1
        userInfo.put("RSRV_NUM2", baseUserInfo.getString("RSRV_NUM2", "")); // 预留数值2
        userInfo.put("RSRV_NUM3", baseUserInfo.getString("RSRV_NUM3", "")); // 预留数值3
        userInfo.put("RSRV_NUM4", baseUserInfo.getString("RSRV_NUM4", "")); // 预留数值4
        userInfo.put("RSRV_NUM5", baseUserInfo.getString("RSRV_NUM5", "")); // 预留数值5

        userInfo.put("RSRV_STR1", baseUserInfo.getString("RSRV_STR1", "")); // 预留字段1
        userInfo.put("RSRV_STR2", baseUserInfo.getString("RSRV_STR2", "")); // 预留字段2
        userInfo.put("RSRV_STR3", baseUserInfo.getString("RSRV_STR3", "")); // 预留字段3
        userInfo.put("RSRV_STR4", baseUserInfo.getString("RSRV_STR4", "")); // 预留字段4
        userInfo.put("RSRV_STR5", baseUserInfo.getString("RSRV_STR5", "")); // 预留字段5
        userInfo.put("RSRV_STR6", baseUserInfo.getString("RSRV_STR6", "")); // 预留字段6
        userInfo.put("RSRV_STR7", baseUserInfo.getString("RSRV_STR7", "")); // 预留字段7
        userInfo.put("RSRV_STR8", baseUserInfo.getString("RSRV_STR8", "")); // 预留字段8
        userInfo.put("RSRV_STR9", baseUserInfo.getString("RSRV_STR9", "")); // 预留字段9
        userInfo.put("RSRV_STR10", baseUserInfo.getString("RSRV_STR10", "")); // 预留字段10
        userInfo.put("RSRV_DATE1", baseUserInfo.getString("RSRV_DATE1", "")); // 预留日期1
        userInfo.put("RSRV_DATE2", baseUserInfo.getString("RSRV_DATE2", "")); // 预留日期2
        userInfo.put("RSRV_DATE3", baseUserInfo.getString("RSRV_DATE3", "")); // 预留日期3
        userInfo.put("RSRV_TAG1", baseUserInfo.getString("RSRV_TAG1", "")); // 预留标志1
        userInfo.put("RSRV_TAG2", baseUserInfo.getString("RSRV_TAG2", "")); // 预留标志2
        userInfo.put("RSRV_TAG3", baseUserInfo.getString("RSRV_TAG3", "")); // 预留标志3

        userInfo.put("STATE", baseUserInfo.getString("STATE", "ADD")); // 集团开户设置为ADD

        // ...
        UserTradeData utd = new UserTradeData(userInfo);
        uca.setUser(utd);

        uca.setProductId(productId);
        uca.setBrandCode(baseUserInfo.getString("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId)));

        // 账户是否新增,true为新增,false为取已有的
        boolean acctIdAdd = map.getBoolean("ACCT_IS_ADD");

        IData baseAcctInfo = map.getData("ACCT_INFO");

        if(IDataUtil.isEmpty(baseAcctInfo)) {
            baseAcctInfo = new DataMap();
        }

        String acctId = null;
        if(acctIdAdd) {
            // 构造acctData
            IData acctInfo = new DataMap();

            acctId = SeqMgr.getAcctId();

            acctInfo.put("ACCT_ID", acctId); // 帐户标识
            acctInfo.put("CUST_ID", baseAcctInfo.getString("CUST_ID", uca.getCustGroup().getCustId())); // 归属客户标识

            String payNameChange = baseAcctInfo.getString("PAY_NAME_ISCHANGED", "true");
            if(payNameChange.equals("false")) {
                baseAcctInfo.put("PAY_NAME", uca.getCustGroup().getCustName());
            }
            acctInfo.put("PAY_NAME", map.getString("PAY_NAME", uca.getCustGroup().getCustName())); // 帐户名称
            acctInfo.put("PAY_MODE_CODE", baseAcctInfo.getString("PAY_MODE_CODE", "0")); // 帐户类型

            // 增加非现金类别
            if(!"0".equals(acctInfo.getString("PAY_MODE_CODE"))) {
                IData acctConsign = new DataMap();
                acctConsign.put("ACCT_BALANCE_ID", baseAcctInfo.getString("ACCT_BALANCE_ID"));
                acctConsign.put("ACCT_ID", acctId);
                acctConsign.put("ACT_TAG", baseAcctInfo.getString("ACCT_TAG", "1"));
                acctConsign.put("ASSISTANT_TAG", baseAcctInfo.getString("ASSISTANT_TAG", "0"));
                acctConsign.put("BANK_ACCT_NAME", baseAcctInfo.getString("BANK_ACCT_NAME"));
                acctConsign.put("BANK_ACCT_NO", baseAcctInfo.getString("BANK_ACCT_NO", ""));
                acctConsign.put("BANK_CODE", baseAcctInfo.getString("BANK_CODE"));
                if(StringUtils.isEmpty(baseAcctInfo.getString("CITY_CODE"))) {
                    acctInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
                } else {
                    acctInfo.put("CITY_CODE", baseAcctInfo.getString("CITY_CODE"));
                }
                acctConsign.put("CONSIGN_MODE", baseAcctInfo.getString("CONSIGN_MODE"));
                acctConsign.put("CONTACT", baseAcctInfo.getString("CONTACT"));
                acctConsign.put("CONTACT_PHONE", baseAcctInfo.getString("CONTACT_PHONE"));
                acctConsign.put("CONTRACT_ID", baseAcctInfo.getString("CONTRACT_ID"));
                acctConsign.put("CONTRACT_NAME", baseAcctInfo.getString("CONTRACT_NAME"));
                acctConsign.put("END_CYCLE_ID", baseAcctInfo.getString("END_CYCLE_ID"));
                acctConsign.put("EPARCHY_CODE", baseAcctInfo.getString("EPARCHY_CODE"));
                acctConsign.put("MODIFY_TAG", baseAcctInfo.getString("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()));
                acctConsign.put("PAYMENT_ID", baseAcctInfo.getString("PAYMENT_ID"));
                acctConsign.put("PAY_FEE_MODE_CODE", baseAcctInfo.getString("PAY_FEE_MODE_CODE"));
                acctConsign.put("PAY_MODE_CODE", baseAcctInfo.getString("PAY_MODE_CODE"));
                acctConsign.put("POST_ADDRESS", baseAcctInfo.getString("POST_ADDRESS"));
                acctConsign.put("POST_CODE", baseAcctInfo.getString("POST_CODE"));
                acctConsign.put("PRIORITY", baseAcctInfo.getString("PRIORITY"));
                acctConsign.put("REMARK", baseAcctInfo.getString("REMARK"));
                acctConsign.put("RSRV_STR1", baseAcctInfo.getString("RSRV_STR1", ""));
                acctConsign.put("RSRV_STR10", baseAcctInfo.getString("RSRV_STR10", ""));
                acctConsign.put("RSRV_STR2", baseAcctInfo.getString("RSRV_STR2", ""));
                acctConsign.put("RSRV_STR3", baseAcctInfo.getString("RSRV_STR3", ""));
                acctConsign.put("RSRV_STR4", baseAcctInfo.getString("RSRV_STR4", ""));
                acctConsign.put("RSRV_STR5", baseAcctInfo.getString("RSRV_STR5", ""));
                acctConsign.put("RSRV_STR6", baseAcctInfo.getString("RSRV_STR6", ""));
                acctConsign.put("RSRV_STR7", baseAcctInfo.getString("RSRV_STR7", ""));
                acctConsign.put("RSRV_STR8", baseAcctInfo.getString("RSRV_STR8", ""));
                acctConsign.put("RSRV_STR9", baseAcctInfo.getString("RSRV_STR9", ""));
                acctConsign.put("START_CYCLE_ID", baseAcctInfo.getString("START_CYCLE_ID"));
                acctConsign.put("SUPER_BANK_CODE", baseAcctInfo.getString("SUPER_BANK_CODE"));

                //互联网界面改造 新增账户加instid
                acctConsign.put("INST_ID", baseAcctInfo.getString("INST_ID", SeqMgr.getInstId()));

                reqData.setACCT_CONSIGN(acctConsign);
            }

            //
            acctInfo.put("ACCT_DIFF_CODE", baseAcctInfo.getString("ACCT_DIFF_CODE", "")); // 帐户类别
            acctInfo.put("ACCT_PASSWD", baseAcctInfo.getString("ACCT_PASSWD", "")); // 帐户密码
            acctInfo.put("SUPER_BANK_CODE", baseAcctInfo.getString("SUPER_BANK_CODE", "")); // 上级银行编码
            acctInfo.put("ACCT_TAG", baseAcctInfo.getString("ACCT_TAG", "")); // 合帐标记
            acctInfo.put("NET_TYPE_CODE", baseAcctInfo.getString("NET_TYPE_CODE", "00")); // 网别编码
            acctInfo.put("EPARCHY_CODE", baseAcctInfo.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode())); //
            if(StringUtils.isEmpty(baseAcctInfo.getString("CITY_CODE"))) {
                acctInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
            } else {
                acctInfo.put("CITY_CODE", baseAcctInfo.getString("CITY_CODE"));
            }
            acctInfo.put("BANK_CODE", baseAcctInfo.getString("BANK_CODE", "")); // 银行编码
            acctInfo.put("BANK_ACCT_NO", baseAcctInfo.getString("BANK_ACCT_NO", "")); // 银行帐号
            acctInfo.put("SCORE_VALUE", baseAcctInfo.getString("SCORE_VALUE", "0")); // 帐户积分
            acctInfo.put("CREDIT_CLASS_ID", baseAcctInfo.getString("CREDIT_CLASS_ID", "0")); // 信用等级
            acctInfo.put("BASIC_CREDIT_VALUE", baseAcctInfo.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
            acctInfo.put("CREDIT_VALUE", baseAcctInfo.getString("CREDIT_VALUE", "0")); // 信用度
            acctInfo.put("DEBUTY_USER_ID", baseAcctInfo.getString("DEBUTY_USER_ID", "")); // 代表用户标识
            acctInfo.put("DEBUTY_CODE", baseAcctInfo.getString("DEBUTY_CODE", "")); // 代表号码
            acctInfo.put("CONTRACT_NO", baseAcctInfo.getString("CONTRACT_NO", "")); // 合同号
            acctInfo.put("DEPOSIT_PRIOR_RULE_ID", baseAcctInfo.getString("DEPOSIT_PRIOR_RULE_ID", "")); // 存折优先规则标识
            acctInfo.put("ITEM_PRIOR_RULE_ID", baseAcctInfo.getString("ITEM_PRIOR_RULE_ID", "")); // 帐目优先规则标识
            acctInfo.put("OPEN_DATE", getAcceptTime()); // 开户时间
            acctInfo.put("REMOVE_TAG", "0"); // 注销标志：0-在用，1-已销
            acctInfo.put("REMOVE_DATE", ""); // 销户时间

            // 修改属性
            acctInfo.put("RSRV_STR1", baseAcctInfo.getString("RSRV_STR1", "")); // 预留字段1
            acctInfo.put("RSRV_STR2", baseAcctInfo.getString("RSRV_STR2", productId)); // 预留字段2
            acctInfo.put("RSRV_STR3", baseAcctInfo.getString("RSRV_STR3", UProductInfoQry.getProductNameByProductId(productId)));
            acctInfo.put("RSRV_STR4", baseAcctInfo.getString("RSRV_STR4", "")); // 预留字段4
            // data.put("RSRV_STR4", commData.getData().getString("ACCT_PASSWD"));// 账户密码
            acctInfo.put("RSRV_STR5", baseAcctInfo.getString("RSRV_STR5", "")); // 预留字段5
            acctInfo.put("RSRV_STR6", baseAcctInfo.getString("RSRV_STR6", "")); // 预留字段6
            acctInfo.put("RSRV_STR7", baseAcctInfo.getString("RSRV_STR7", "")); // 预留字段7
            acctInfo.put("RSRV_STR8", baseAcctInfo.getString("RSRV_STR8", "")); // 预留字段8
            acctInfo.put("RSRV_STR9", baseAcctInfo.getString("RSRV_STR9", "")); // 预留字段9
            acctInfo.put("RSRV_STR10", baseAcctInfo.getString("RSRV_STR10", "")); // 预留字段10

            acctInfo.put("REMARK", ""); // 备注

            acctInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            AccountTradeData atd = new AccountTradeData(acctInfo);
            uca.setAccount(atd);
        }

        // 把集团uca放到databus总线,用sn作为key取
        DataBusManager.getDataBus().setUca(uca);

        reqData.setUca(uca);

        //将新增账户信息放入缓存
        String cacheKey = CacheKey.getUcaKeyActByAcctId(acctId, Route.CONN_CRM_CG);
        if(StringUtils.isNotBlank(cacheKey)) {
            SharedCache.set(cacheKey, reqData.getUca().getAccount().toData(), 600);
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception {
        return new CreateLineGroupMemberReqData();
    }

    @Override
    protected void initReqData() throws Exception {
        super.initReqData();

        reqData = (CreateLineGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception {
        super.makInit(map);
        moduleData.getMoudleInfo(map);
        //tradeId = map.getString("TRADE_ID");
        //map.put("NEED_RULE", false);

    }

    @Override
    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);

        reqData.setAcctIsAdd(map.getBoolean("ACCT_IS_ADD", false));

        reqData.setMemRoleB("2");

        // 解析产品结构
        makReqDataElement();

    }

    public void makReqDataElement() throws Exception {

        // 解析资源信息
        GroupModuleParserBean.mebRes(reqData, moduleData);

        // 解析产品和产品元素信息
        GroupModuleParserBean.mebElement(reqData, moduleData);

        // 处理产品和产品参数
        makReqDataProductParam();

        // 处理集团定制
        //makReqDataGrpPackage();

        // 付费计划处理
        makReqDataPlanInfo(reqData, moduleData);

    }

    private void makReqDataPlanInfo(CreateLineGroupMemberReqData reqData, GrpModuleData moduleData) throws Exception {
        //新增专线成员为个人付费
        String planTypeCode = "P";

        boolean ifBooking = reqData.isIfBooking(); // 是否预约
        String firstTimeNextMonth = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
        String lastTimeThisMonth = SysDateMgr.getLastDateThisMonth();

        IData userAcctDay = DiversifyAcctUtil.getUserAcctDay(reqData.getUca().getUserId());

        if(IDataUtil.isNotEmpty(userAcctDay)) {
            // 用户为分散用户, 处理元素时间
            if(!DiversifyAcctUtil.checkUserAcctDay(userAcctDay, "1", false)) {
                String lastDayThisAcct = userAcctDay.getString("LAST_DAY_THISACCT", "");
                String fisrtDayNextAcct = userAcctDay.getString("FIRST_DAY_NEXTACCT", "");
                if(StringUtils.isNotBlank(lastDayThisAcct)) {
                    lastTimeThisMonth = lastDayThisAcct + SysDateMgr.getEndTime235959();
                }
                if(StringUtils.isNotBlank(fisrtDayNextAcct)) {
                    firstTimeNextMonth = fisrtDayNextAcct + SysDateMgr.getFirstTime00000();
                }
            }
        }

        IData payPlanData = new DataMap();

        //新增付费计划
        payPlanData.put(planTypeCode, TRADE_MODIFY_TAG.Add.getValue());

        // 处理付费计划
        IDataset payPlanList = new DatasetList(); // 付费计划

        IDataset payRelaList = new DatasetList(); // 付费关系

        String planName = StaticUtil.getStaticValue("PAYPLAN_PLANTYPE", planTypeCode);

        IData addPayPlanData = new DataMap();
        addPayPlanData.put("USER_ID", reqData.getUca().getUserId());
        addPayPlanData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        addPayPlanData.put("PLAN_ID", SeqMgr.getPlanId());
        addPayPlanData.put("PLAN_TYPE_CODE", planTypeCode);
        addPayPlanData.put("PLAN_NAME", planName);
        addPayPlanData.put("PLAN_DESC", planName);
        addPayPlanData.put("START_DATE", ifBooking ? firstTimeNextMonth : reqData.getAcceptTime());
        addPayPlanData.put("END_DATE", SysDateMgr.getTheLastTime());
        addPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        payPlanList.add(addPayPlanData);

        String payItemCode = "-1"; //-1表示代付所有付费项

        // 新增付费关系
        IData addPayRelaData = new DataMap();

        addPayRelaData.put("USER_ID", reqData.getUca().getUserId());
        if(reqData.isAcctIsAdd()) {
            addPayRelaData.put("ACCT_ID", reqData.getUca().getAcctId());
        } else {
            addPayRelaData.put("ACCT_ID", reqData.getGrpUca().getAcctId());
        }
        addPayRelaData.put("PAYITEM_CODE", payItemCode);
        addPayRelaData.put("ACCT_PRIORITY", "0"); // 账户优先级
        addPayRelaData.put("USER_PRIORITY", "0"); // 用户优先级
        addPayRelaData.put("BIND_TYPE", "0"); // 账户绑定方式
        addPayRelaData.put("DEFAULT_TAG", "1"); // 默认标志
        addPayRelaData.put("ACT_TAG", "1"); // 作用标志
        addPayRelaData.put("LIMIT_TYPE", "0"); // 限定方式
        addPayRelaData.put("LIMIT", "0"); // 限定值
        addPayRelaData.put("COMPLEMENT_TAG", "0"); // 限定值
        addPayRelaData.put("INST_ID", SeqMgr.getInstId()); // inst_id
        addPayRelaData.put("START_CYCLE_ID", ifBooking ? SysDateMgr.getNextCycle() : SysDateMgr.getNowCyc()); // 开始账期
        // 基类有对6位的处理
        addPayRelaData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231()); // 开始账期
        addPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 添加付费关系
        payRelaList.add(addPayRelaData);


        // 设置会话数据
        reqData.cd.putPayPlan(payPlanList);
        reqData.cd.putPayRelation(payRelaList);

    }

    /** 解析产品参数信息
     * 
     * @throws Exception */
    protected void makReqDataProductParam() throws Exception {
        IDataset productParamList = moduleData.getProductParamInfo();

        // 处理产品参数信息
        for (int i = 0, size = productParamList.size(); i < size; i++) {
            IData productParamData = productParamList.getData(i);

            String mebBaseProductId = reqData.getUca().getProductId();//ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

            reqData.cd.putProductParamList(mebBaseProductId, productParamData.getDataset("PRODUCT_PARAM"));
        }
    }

    @Override
    protected String setOrderTypeCode() throws Exception {
        return "0";
    }

    protected void initProductCtrlInfo() throws Exception {

        String productId = reqData.getGrpUca().getProductId();
        getProductCtrlInfo(productId, BizCtrlType.CreateUser);
        String mebProductId = reqData.getUca().getProductId();
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(mebProductId, BizCtrlType.CreateUser);
        reqData.setValidateMethod(ctrlInfo.getAttrStr1Value("Validate"));
        reqData.setProductCtrlInfo(mebProductId, ctrlInfo);
    }

    @Override
    protected String setTradeTypeCode() throws Exception {
        // 设置产品信息
        String productId = reqData.getUca().getProductId();

        // 得到产品控制信息
        BizCtrlInfo ctrlInfo = reqData.getProductCtrlInfo(productId);

        // 得到业务类型
        String tradeTypeCode = ctrlInfo.getTradeTypeCode();

        // 设置业务类型
        return tradeTypeCode;
    }

    @Override
    protected void setTradeProduct(IData map) throws Exception {
        super.setTradeProduct(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识

        String productId = reqData.getUca().getProductId();

        map.put("PRODUCT_ID", reqData.getUca().getProductId()); // 产品标识

        map.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "10")); // 产品的模式：00:基本产品，01:附加产品

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        map.put("BRAND_CODE", brandCode); // 品牌编码

        // data.put("OLD_PRODUCT_ID", map.getString("OLD_PRODUCT_ID", "")); // 原产品标识
        // data.put("OLD_BRAND_CODE", map.getString("OLD_BRAND_CODE", "")); // 原品牌编码

        map.put("INST_ID", map.getString("INST_ID", "0")); // 实例标识

        // data.put("CAMPN_ID", map.getString("CAMPN_ID", ""));// 活动标识
        map.put("START_DATE", map.getString("START_DATE", SysDateMgr.getSysTime())); // 开始时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 结束时间
        //专线成员，成员产品即为主产品
        map.put("MAIN_TAG", map.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
    }

    @Override
    protected void setTradeUser(IData map) throws Exception {
        super.setTradeUser(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
        map.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber()); // 服务号码

        map.put("BRAND_CODE", reqData.getUca().getBrandCode()); // 当前品牌编码
        map.put("PRODUCT_ID", reqData.getUca().getProductId()); // 当前产品标识

        map.put("USER_TYPE_CODE", map.getString("USER_TYPE_CODE", "8")); // 用户类型
        map.put("USER_STATE_CODESET", map.getString("USER_STATE_CODESET", "0")); // 用户主体服务状态集：见服务状态参数表

        map.put("SCORE_VALUE", map.getString("SCORE_VALUE", "0")); // 积分值
        map.put("CREDIT_CLASS", map.getString("CREDIT_CLASS", "0")); // 信用等级
        map.put("BASIC_CREDIT_VALUE", map.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
        map.put("CREDIT_VALUE", map.getString("CREDIT_VALUE", "0")); // 信用度
        map.put("CREDIT_CONTROL_ID", map.getString("CREDIT_CONTROL_ID", "0")); // 信控规则标识
        map.put("ACCT_TAG", map.getString("ACCT_TAG", "0")); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        map.put("PREPAY_TAG", map.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
        map.put("MPUTE_MONTH_FEE", map.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算

        map.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 建档员工
        map.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 建档渠道
        map.put("OPEN_MODE", map.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户

        map.put("RSRV_TAG1", StringUtils.isBlank(map.getString("RSRV_TAG1")) ? GroupProductUtil.getClassId(reqData.getUca().getCustGroup().getClassId(), "1") : map.getString("RSRV_TAG1")); // 预留标志1
    }

    @Override
    protected void setTradeUserPayplan(IData map) throws Exception {
        super.setTradeUserPayplan(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // 用户标识A
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception {
        super.setTradeSvc(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", reqData.getGrpUca().getUserId()); // 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1

        String elementId = map.getString("ELEMENT_ID", "");

        String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), elementId);
        map.put("MAIN_TAG", mainTag);// 主体服务标志：0-否，1-是
        map.put("CAMPN_ID", map.getString("CAMPN_ID"));

        if(elementId.matches("910|911")) {
            map.put("RSRV_STR1", "boss"); // 预留字段1
            map.put("RSRV_STR2", "boss"); // 预留字段2
            map.put("RSRV_STR3", reqData.getUca().getCustGroup().getGroupId());// 预留字段3
            map.put("RSRV_STR4", "123456"); // 预留字段4
        } else {
            map.put("RSRV_STR1", map.getString("RSRV_STR1", ""));// 预留字段1
            map.put("RSRV_STR2", map.getString("RSRV_STR2", ""));// 预留字段2
            map.put("RSRV_STR3", map.getString("RSRV_STR3", ""));// 预留字段3
            map.put("RSRV_STR4", map.getString("RSRV_STR4", ""));// 预留字段4
        }
    }

    @Override
    protected void setTradeDiscnt(IData map) throws Exception {
        super.setTradeDiscnt(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", reqData.getGrpUca().getUserId());// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        map.put("SPEC_TAG", map.getString("SPEC_TAG", "0")); // 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
        map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()))); // 关系类型
    }

    @Override
    protected void setTradeRes(IData map) throws Exception {
        super.setTradeRes(map);

        map.put("USER_ID", reqData.getUca().getUserId());
        map.put("USER_ID_A", reqData.getGrpUca().getUserId());
    }

    @Override
    protected void setTradeFeeTax(IData map) throws Exception {
        super.setTradeFeeTax(map);

        String tradeTypeCode = getTradeTypeCode();
        String productId = reqData.getGrpUca().getProductId();

        String elementId = map.getString("DISCNT_GIFT_ID");
        String feeMode = map.getString("FEE_MODE");
        String feeTypeCode = map.getString("FEE_TYPE_CODE");

        IDataset taxList = null;
        String type = CSBaseConst.TAX_TYPE.SALE.getValue();
        String rate = "0";
        String discount = "0";

        boolean elementIdFlag = true;

        if(ProvinceUtil.isProvince(ProvinceUtil.HAIN)) {

            elementIdFlag = !"-1".equals(elementId);

        }
        if(StringUtils.isNotEmpty(elementId) && elementIdFlag) // 处理元素收费
        {
            taxList = ElementTaxInfoQry.qryTaxByElementId(tradeTypeCode, productId, elementId, feeMode, feeTypeCode, CSBizBean.getUserEparchyCode());
        } else
        // 处理产品收费
        {
            taxList = FeeItemTaxInfoQry.qryTaxByTradeProductFee(tradeTypeCode, productId, feeMode, feeTypeCode, CSBizBean.getUserEparchyCode());
        }

        if(IDataUtil.isNotEmpty(taxList)) {
            IData elementTaxData = taxList.getData(0);
            type = elementTaxData.getString("TYPE", CSBaseConst.TAX_TYPE.SALE.getValue());
            rate = elementTaxData.getString("RATE", "0");
            discount = elementTaxData.getString("DISCOUNT", "0");
        }

        map.put("TYPE", type);
        map.put("RATE", rate);
        map.put("FACT_PAY_FEE", map.getString("FEE"));
        map.put("DISCOUNT", discount);

        // 计算税率
        TaxCalcUtils.getTradeFeeTaxForCalculate(IDataUtil.idToIds(map));
    }

    /** 生成集团业务稽核工单 REQ201804280001集团合同管理界面优化需求
     * 
     * @param map
     * @throws Exception
     * @author chenzg
     * @date 2018-7-3 */
    protected void actGrpBizBaseAudit(IData map) throws Exception {
        boolean actVoucherFlag = BizEnv.getEnvBoolean("grp.biz.audit", false);
        if(actVoucherFlag) {
            //成员业务上传凭证信息则生成集团业务稽核工单
            String voucherFileList = map.getString("MEB_VOUCHER_FILE_LIST", "");
            String auditStaffId = map.getString("AUDIT_STAFF_ID", "");
            if(StringUtils.isNotBlank(voucherFileList)) {
                String auditId = "";
                if(StringUtils.isNotBlank(map.getString("ORIG_BATCH_ID", ""))) {
                    auditId = map.getString("ORIG_BATCH_ID", ""); //批量任务的批次号不为空就取批次号
                } else {
                    auditId = this.getTradeId(); //不然就取业务流水号
                }
                IDataset tradeDiscnts = this.bizData.getTradeDiscnt();
                String addDisncts = "";
                String delDiscnts = "";
                String modDiscnts = "";
                if(IDataUtil.isNotEmpty(tradeDiscnts)) {
                    for (int i = 0; i < tradeDiscnts.size(); i++) {
                        IData each = tradeDiscnts.getData(i);
                        String modifyTag = each.getString("MODIFY_TAG", "");
                        String discntCode = each.getString("DISCNT_CODE", "");
                        if(TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)) {
                            addDisncts += StringUtils.isNotBlank(addDisncts) ? "," + discntCode : discntCode;
                        } else if(TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)) {
                            delDiscnts += StringUtils.isNotBlank(delDiscnts) ? "," + discntCode : discntCode;
                        } else if(TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag)) {
                            modDiscnts += StringUtils.isNotBlank(modDiscnts) ? "," + discntCode : discntCode;
                        }
                    }
                }
                IData param = new DataMap();
                param.put("AUDIT_ID", auditId); //批量业务的批次号或业务流水号trade_id
                param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(auditId)); //受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
                param.put("BIZ_TYPE", StringUtils.isNotBlank(this.reqData.getBatchId()) ? "2" : "1"); //业务工单类型：1-单条，2-批量业务
                param.put("TRADE_TYPE_CODE", this.reqData.getTradeType().getTradeTypeCode()); //业务类型编码：见参数表TD_S_TRADETYPE
                param.put("GROUP_ID", this.reqData.getGrpUca().getCustGroup().getGroupId()); //集团客户编码
                param.put("CUST_NAME", this.reqData.getGrpUca().getCustGroup().getCustName()); //集团客户名称
                param.put("GRP_SN", this.reqData.getGrpUca().getSerialNumber()); //集团产品编码
                param.put("CONTRACT_ID", ""); //合同编号
                param.put("VOUCHER_FILE_LIST", voucherFileList); //凭证信息上传文件ID
                param.put("ADD_DISCNTS", addDisncts); //新增优惠
                param.put("DEL_DISCNTS", delDiscnts); //删除优惠
                param.put("MOD_DISCNTS", modDiscnts); //变更优惠
                param.put("STATE", "0"); //稽核单状态:0-初始，1-稽核通过，2-稽核不通过
                param.put("IN_DATE", SysDateMgr.getSysTime()); //提交时间
                param.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()); //提交工号
                param.put("AUDIT_STAFF_ID", auditStaffId); //稽核人工号
                GrpBaseAudiInfoQry.addGrpBaseAuditInfo(param);
            }
        }
    }
}
