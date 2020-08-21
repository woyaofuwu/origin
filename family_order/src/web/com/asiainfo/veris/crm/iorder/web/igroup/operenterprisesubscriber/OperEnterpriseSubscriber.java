package com.asiainfo.veris.crm.iorder.web.igroup.operenterprisesubscriber;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.util.CookieUtil;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.FrontProdConverter;
import com.asiainfo.veris.crm.iorder.web.igroup.ecbasepage.EcBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productpackage.ProductPkgInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class OperEnterpriseSubscriber extends EcBasePage {
    private static final Logger logger = LoggerFactory.getLogger(OperEnterpriseSubscriber.class);

    public void initial(IRequestCycle cycle) throws Exception {

        // IData svcParam= new DataMap();
        // svcParam.put("SERIAL_NUMBER","1064825600759");
        // IDataset result = CSViewCall.call(this,"SS.ChangeSvcStateIntfSVC.createStopReg", svcParam);
        //

        IData pageData = this.getData();
        IData outOffer = new DataMap();
        outOffer.put("OUT_OFFER_ID", pageData.getString("OUT_OFFER_ID", ""));
        outOffer.put("OUT_OFFER_CODE", pageData.getString("OUT_OFFER_CODE", ""));
        outOffer.put("OUT_OFFER_NAME", pageData.getString("OUT_OFFER_NAME", ""));
        setInitOffer(outOffer);
        // 初始化页面信息
        IData info = new DataMap();

        IDataset acctDealList = StaticUtil.getStaticList("EC_ACCT_OPER_TYPE");
        info.put("ACCT_DEAL_LIST", acctDealList);
        info.put("ACCT_DEAL", "0"); // 设置默认账户操作为新增
        // esop
        String ibsysId = pageData.getString("IBSYSID");
        if (StringUtils.isNotBlank(ibsysId)) {
            if ("VP9983".equals(pageData.getString("BUSI_CODE"))) {
                pageData.put("BUSI_CODE", "9983");
            }
            dealEsop(pageData, info);
            info.put("ESOP_TAG", true);
            info.put("IBSYSID", pageData.getString("IBSYSID"));
            info.put("SUB_IBSYSID", pageData.getString("SUB_IBSYSID"));
            info.put("NODE_ID", pageData.getString("NODE_ID"));
            info.put("isEsopEbuop", pageData.getString("isEsopEbuop"));

            String productId = pageData.getString("BUSI_CODE", "");
            String custId = info.getString("CUST_ID", "");
            queryRechangeUserList(productId, custId);
        }
        // else
        // {
        // String showFuncNavigation = createFuncNaviCookie(cycle);
        // info.put("SHOW_FUNC_NAVIGATION", showFuncNavigation);
        // }

        setInfo(info);
    }

    private void dealEsop(IData pageData, IData info) throws Exception {
        IData temp = new DataMap();
        temp.put("IBSYSID", pageData.getString("IBSYSID"));
        temp.put("NODE_ID", pageData.getString("NODE_ID"));
        temp.put("BPM_TEMPLET_ID", pageData.getString("BPM_TEMPLET_ID"));
        temp.put("BUSI_CODE", pageData.getString("BUSI_CODE"));
        temp.put("BUSI_TYPE", pageData.getString("BUSI_TYPE"));
        temp.put("BUSIFORM_ID", pageData.getString("BUSIFORM_ID"));
        temp.put("BUSIFORM_NODE_ID", pageData.getString("BUSIFORM_NODE_ID"));
        temp.put("X_SUBTRANS_CODE", EcConstants.ESOPINIT_METHOD);
        IDataset offerDataInfos = CommonViewCall.qryEsopGrpBusiInfo(this, temp);
        IData commonData = new DataMap();
        String groupId = "";
        if (DataUtils.isNotEmpty(offerDataInfos) && DataUtils.isNotEmpty(offerDataInfos.getData(0))) {
            commonData = offerDataInfos.getData(0).getData("COMMON_DATA");
            groupId = commonData.getString("GROUP_ID");
            info.put("GROUP_ID", groupId);

            IData esopOffer = offerDataInfos.getData(0).getData("OFFER_DATA");
            initPageForEsop(esopOffer, info);

            IData contractInfo = offerDataInfos.getData(0).getData("CONTRACT_INFO");
            if (IDataUtil.isNotEmpty(contractInfo)) {
                info.put("ESOP_CONTRACT_INFO", contractInfo);
            }
        }
        IData esopInfo = queryEsopInfo(commonData);

        String custId = "";
        IData customer = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        if (DataUtils.isNotEmpty(customer)) {
            custId = customer.getString("CUST_ID");
            info.put("CUST_ID", custId);
            info.put("GROUP_ID", groupId);
            // 查询集团名称（没有模糊化）
            /* IData enterpriseData = EcCustomerViewUtil.queryEnterpriseNotFuzzyByCustId(customer.getString("CUST_ID")); if(DataUtils.isNotEmpty(enterpriseData)) { info.put("GROUP_NAME", enterpriseData.getString("GROUP_NAME")); } */
            info.put("GROUP_NAME", customer.getString("CUST_NAME"));
        }
        esopInfo.put("GROUP_ID", groupId);
        esopInfo.put("GROUP_NAME", customer.getString("CUST_NAME"));
        esopInfo.put("CUST_NAME", customer.getString("CUST_NAME"));
        esopInfo.put("CUST_ID", custId);
        esopInfo.put("OPER_TYPE", commonData.getString("OPER_TYPE"));

        info.put("ESOP_INFO", esopInfo);

        // String operType = ViewDataAssembler.transOperCodeToOperType(info.getString("OPER_CODE"), "EC");
        // info.put("OPER_TYPE", operType);
    }

    /**
     * 初始化esop属性
     * 
     * @param pageData
     * @return
     * @throws Exception
     */
    public IData queryEsopInfo(IData pageData) throws Exception {
        IData esopInfo = new DataMap();
        esopInfo.put("IS_ESOP", true);
        esopInfo.put("TRADE_ID", pageData.getString("TRADE_ID"));
        esopInfo.put("WORK_ID", pageData.getString("WORK_ID"));
        esopInfo.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));
        esopInfo.put("IBSYSID", pageData.getString("IBSYSID"));
        esopInfo.put("NODE_ID", pageData.getString("NODE_ID"));
        esopInfo.put("GROUP_ID", pageData.getString("GROUP_ID"));
        esopInfo.put("SUBSCRIBE_TYPE", pageData.getString("SUBSCRIBE_TYPE"));
        esopInfo.put("BPM_TEMPLET_ID", pageData.getString("BPM_TEMPLET_ID"));
        esopInfo.put("FLOW_MAIN_ID", pageData.getString("FLOW_MAIN_ID"));
        esopInfo.put("SUB_SUBSCRIBE_ID", pageData.getString("SUB_SUBSCRIBE_ID", ""));
        esopInfo.put("SUBSCRIBE_TYPE", pageData.getString("SUBSCRIBE_TYPE"));

        IData offer = UpcViewCall.queryOfferByOfferId(this, IUpcConst.ELEMENT_TYPE_CODE_PRODUCT, pageData.getString("BUSI_CODE"));

        esopInfo.put("OFFER_NAME", offer.getString("OFFER_NAME"));
        esopInfo.put("OFFER_ID", offer.getString("OFFER_ID"));
        /* initOperType(esopInfo); */
        return esopInfo;
    }

    /**
     * 根据顶层销售品加载必选销售品信息
     *
     * @param cycle
     * @throws Exception
     */
    public void queryOfferInfo(IRequestCycle cycle) throws Exception {
        String offerId = this.getData().getString("OFFER_ID", "");
        String groupId = this.getData().getString("GROUP_ID", "");
        String custId = this.getData().getString("CUST_ID", "");
        
        //REQ202001100001关于限制长期欠费的集团客户开通新业务的需求
    	IData data = checkFee(custId);
    	if (!DataUtils.isEmpty(data)) {
    		String state = data.getString("STATE","true");
    		if ("false".equals(state)) {
    			CSViewException.apperr(CrmCommException.CRM_COMM_103, data.getString("MSG",""));
			}
        }
        
        String grpUserEparchyCode = getTradeEparchyCode();

        IData offerInfo = IUpcViewCall.queryOfferByOfferId(offerId, "Y");
        if (DataUtils.isEmpty(offerInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID" + offerId + "没有查询到商品信息！");
        }
        String offerCode = offerInfo.getString("OFFER_CODE");

        // 查询TRADE_TYPE_CODE
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(offerCode, "P", BizCtrlType.CreateUser, "TradeTypeCode");
        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据" + offerCode + "没找到业务类型");
        }
        offerInfo.put("TRADE_TYPE_CODE", tradeTypeCode);

        // offerInfo.put("BRAND_CODE", EcUpcViewUtil.queryBrandByOfferId(offerId));

        // 待政企订单中心改造
        if (StringUtils.isBlank(this.getData().getString("ESOP_ATTR"))) {
            forbidCrmOrder(offerCode);
        }

        // 服务号码设置
        IData ecAccessNumInfo = getSerialNumber(groupId, offerCode, grpUserEparchyCode);
        offerInfo.put("SERIAL_NUMBER", ecAccessNumInfo.getString("SERIAL_NUMBER"));
        offerInfo.put("RES_TYPE_CODE", ecAccessNumInfo.getString("RES_TYPE_CODE"));
        offerInfo.put("SERIAL_NUMBER_SUCCESS", "false");
        offerInfo.put("IF_RES_CODE", ecAccessNumInfo.getString("IF_RES_CODE"));
        if ("0".equals(offerInfo.getString("IF_RES_CODE"))) {
            // 不需要校验服务号码
            offerInfo.put("SERIAL_NUMBER_SUCCESS", "true");
        }

        logger.debug("SERIAL_NUMBER=============================" + offerInfo);

        offerInfo.put("OPER_TYPE", BizCtrlType.CreateUser);

        // offerInfo.put("MODIFY_TAG", "0");//进到这个位置，操作必为集团产品新增

        /************************* 调用规则 开始 *************************/

        checkEcOpenBaseInfoRule("", custId, grpUserEparchyCode, offerCode);

        /************************* 调用规则 结束 *************************/

        IData ajaxData = new DataMap();
        // 集团定制
        String useTag = IUpcViewCall.getUseTagByProductId(offerInfo.getString("OFFER_CODE"));
        if ("1".equals(useTag)) {
            // 定制初始化
            IDataset mebOffers = IUpcViewCall.queryOfferJoinRelAndOfferByOfferId(offerId, "1", "", "");
            if (DataUtils.isEmpty(mebOffers)) {
                useTag = "0";// 如果不存在成员商品，则不需要定制
            } else {
                IDataset userGrpPackageList = queryEcPackages(offerCode, "");
                ajaxData.put("USER_PACKAGES", userGrpPackageList);
            }
        }
        setInitOffer(offerInfo);

        // 其他信息初始化
        IData info = new DataMap();

        IData tem = new DataMap();
        tem.put("ID", offerCode);
        tem.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);
        // 集团付费计划定制
        IDataset templates = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.showTemplate", tem);

        if (IDataUtil.isEmpty(templates)) {
            info.put("PAYFORMEB_TAG", "P");
            info.put("PAYFORMEB_CHOICEDISABLE", "true");
            info.put("PLAN_CHOICEDISABLE", "true");
        } else {
            String payFeeModeCode = templates.getData(0).getString("ATTR_VALUE", "*");

            // 判断权限
            boolean isPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "GRP_PAYPLAN_EDIT");

            // 如果付费方式为集团定制,并且有权限

            if ("C".equals(payFeeModeCode) && isPriv) {
                info.put("PAYFORMEB_TAG", "C");
                info.put("PAYFORMEB_CHOICEDISABLE", "true");
                // data.put("PLAN_MODE_TAG", "");
                info.put("PLAN_CHOICEDISABLE", "false");

            } else {
                info.put("PAYFORMEB_CHOICEDISABLE", "false");
                info.put("PLAN_CHOICEDISABLE", "true");
            }
        }
        // // 是否可以选择合户
        // templates = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.CanSameAcct",tem);
        //
        // if (IDataUtil.isEmpty(templates)) {//查不到数据，不支持合户
        // info.put("SHOW_ACCT_COMBINE", false);
        // }
        IDataset acctDealList = new DatasetList();// StaticUtil.getStaticList("EC_ACCT_OPER_TYPE");
        IData addAcct = new DataMap();
        IData combinAcct = new DataMap();
        addAcct.put("DATA_ID", "0");
        addAcct.put("DATA_NAME", "新增账户");

        combinAcct.put("DATA_ID", "1");
        combinAcct.put("DATA_NAME", "已有账户");// 海南客户要求改成已有账户,客户觉得合户账户容易混淆

        acctDealList.add(addAcct);
        acctDealList.add(combinAcct);
        info.put("ACCT_DEAL_LIST", acctDealList);
        info.put("ACCT_DEAL", "0"); // 设置默认账户操作为新增

        // 用户类别
        IDataset userDiffCodeList = StaticUtil.getStaticList("USER_DIFF_CODE");
        userDiffCodeList.removeAll(DataHelper.filter(userDiffCodeList, "DATA_ID=0")); // 出掉0.普通个人类型
        info.put("USER_DIFFCODE_LIST", userDiffCodeList);

        info.put("STAFF_ID", this.getVisit().getStaffId());
        info.put("STAFF_NAME", this.getVisit().getStaffName());
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", offerCode);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)) {
            info.put("AUDIT_INFO_SHOW", "false");
        } else {
            info.put("AUDIT_INFO_SHOW", "true");
        }

        setInfo(info);

        ajaxData.put("OFFER_DATA", offerInfo);
        ajaxData.put("USE_TAG", useTag);

        this.setAjax(ajaxData);

    }

    public IData getSerialNumber(String groupId, String productId, String grpUserEparchyCode) throws Exception {

        IData infoData = new DataMap();
        if (StringUtils.isEmpty(productId)) {
            return null;
        }
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(productId, "P", BizCtrlType.CreateUser, "TradeTypeCode");
        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据" + productId + "没找到业务类型");
        }
        // 避免服务号码的重复 add begin
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);

        IData grpSnData = new DataMap();
        for (int i = 0; i < 10; i++) {
            grpSnData = CSViewCall.callone(this, "CS.GrpGenSnSVC.genGrpSn", param);

            String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

            if (StringUtils.isEmpty(serialNumber)) {
                break;
            }

            IData userList = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);
            param.clear();
            param.put("SERIAL_NUMBER", serialNumber);
            param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);
            param.put("TRADE_TYPE_CODE", tradeTypeCode);
            IData tradeList = CSViewCall.callone(this, "CS.TradeInfoQrySVC.getMainTradeBySN", param);

            if (IDataUtil.isEmpty(userList) && IDataUtil.isEmpty(tradeList)) {
                break;
            }
        }
        // 避免服务号码的重复 add end

        String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

        String ifResCode = grpSnData.getString("IF_RES_CODE", "");
        // 资源类型 默认为G\

        String resTypeCode = "L";
        if (StringUtils.isBlank(resTypeCode)) {

            if (StringUtils.isNotEmpty(serialNumber) && "8070".equals(productId) && ("0").equals(ifResCode)) {
                resTypeCode = "T";
            } else {
                resTypeCode = "G";
            }
        }

        // 服务号码信息
        infoData.put("SERIAL_NUMBER", serialNumber);
        if ("0".equals(ifResCode)) {
            // 服务号码校验通过
            infoData.put("HIDDEN_SERIAL_NUMBER", serialNumber);
        }
        infoData.put("RES_TYPE_CODE", resTypeCode);
        infoData.put("IF_RES_CODE", ifResCode);
        return infoData;

    }

    /**
     * 根据用户实例id查询销售品实例
     *
     * @param cycle
     * @throws Exception
     */
    public void queryInsOfferInfo(IRequestCycle cycle) throws Exception {
        IData ajaxData = new DataMap();
        IDataset ajaxDataset = new DatasetList();

        String operType = this.getData().getString("OPER_TYPE", "");
        String subscriberInsId = this.getData().getString("USER_ID", "");
        // 获取商品实例
        IData insOfferData = new DataMap();

        IData insSubscriberData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, subscriberInsId);
        insOfferData.put("SERIAL_NUMBER", insSubscriberData.getString("SERIAL_NUMBER"));
        insOfferData.put("USER_ID", subscriberInsId);
        insOfferData.put("OPER_TYPE", operType);
        String offerCode = insSubscriberData.getString("PRODUCT_ID");
        IData offer = UpcViewCall.queryOfferByOfferId(this, "P", offerCode, "Y");
        String mainOfferId = offer.getString("OFFER_ID");
        insOfferData.put("OFFER_ID", mainOfferId);
        insOfferData.put("OFFER_CODE", offerCode);
        insOfferData.put("OFFER_TYPE", "P");
        insOfferData.put("BRAND_CODE", offer.getString("BRAND_CODE"));
        insOfferData.put("OFFER_NAME", offer.getString("OFFER_NAME"));
        insOfferData.put("OFFER_INS_ID", insSubscriberData.getString("PROD_INST_ID"));

        // 待政企订单中心改造
        if (StringUtils.isBlank(this.getData().getString("ESOP_ATTR"))) {
            forbidCrmOrder(offerCode);
        }

        String useTag = offer.getString("USE_TAG");
        if ("1".equals(useTag)) {// 如果定制，查询成员商品编码
                                 // 如果定制，查询成员商品编码
            String mebOfferId = IUpcViewCall.queryMemOfferIdByOfferId(mainOfferId);

            if (StringUtils.isBlank(mebOfferId)) {
                useTag = "0";// 如果不存在成员商品，则不需要定制
            } else {
                insOfferData.put("MEM_OFFER_ID", mebOfferId);
            }
        }
        setInitOffer(insOfferData);

        /************************* 调用规则 开始 *************************/
        checkEcInsBaseInfoRule(subscriberInsId, this.getData().getString("CUST_ID", ""), this.getTradeEparchyCode(), offerCode, operType);
        /************************* 调用规则 结束 *************************/

        IDataset insPriceOfferDataset = new DatasetList();
        IDataset insServiceOfferDataset = new DatasetList();
        IDataset insSubOfferDataset = new DatasetList();
        String brand = insOfferData.getString("BRAND_CODE", "");
        if (BizCtrlType.ChangeUserDis.equals(operType) || ("BOSG".equals(brand) && BizCtrlType.DestoryUser.equals(operType))) {// 变更时，需要构造完整的offer数据对象；注销时，不需要

            // 查询子销售品实例
            insSubOfferDataset = queryInsBundleOfferDataset(subscriberInsId, offerCode, mainOfferId);
            setServicePriceDataset(insSubOfferDataset, insServiceOfferDataset, insPriceOfferDataset);
            setServiceOfferList(insServiceOfferDataset);
            setPriceOfferList(insPriceOfferDataset);
            // 定制后续处理
            IDataset userGrpPackageList = queryEcPackages(offerCode, subscriberInsId);
            ajaxData.put("USER_PACKAGES", userGrpPackageList);

            //
        }

        IData group = new DataMap(); // 订购的子商品归属组信息
        // 构造销售品数据对象
        IData mainOfferData = buildOfferData(insOfferData, insSubOfferDataset, operType, "EC", group);
        if ("DLBG".equals(brand)) {
            // 动力100后续处理
            IDataset power100Subs = queryPower100Dataset(subscriberInsId, offerCode);
            setPower100Subs(power100Subs);
            mainOfferData.put("POWER100_PRODUCT_INFO", power100Subs);
        }

        IData info = new DataMap();
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", offerCode);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)) {
            info.put("AUDIT_INFO_SHOW", "false");
        } else {
            info.put("AUDIT_INFO_SHOW", "true");
        }

        setInfo(info);

        IData ajaxOfferData = new DataMap();
        ajaxOfferData.put("OFFER_ID", mainOfferId);
        ajaxOfferData.put("OFFER_DATA", mainOfferData);
        ajaxDataset.add(ajaxOfferData);

        ajaxData.put("OFFER_DATAS", ajaxDataset);

        ajaxData.put("SELECT_GROUP_OFFER", group);

        ajaxData.put("USE_TAG", useTag);
        this.setAjax(ajaxData);
    }

    public IDataset queryEcPackages(String productId, String ecUserId) throws Exception {
        IDataset userGrpPackageList = new DatasetList();
        IDataset elementList = new DatasetList();
        if (StringUtils.isNotBlank(ecUserId)) {
            IData inparam = new DataMap();
            inparam.put("USER_ID", ecUserId);
            elementList = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", inparam);
            if (DataUtils.isEmpty(elementList)) {
                return userGrpPackageList;
            }
            for (int i = 0; i < elementList.size(); i++) {
                IData temp = elementList.getData(i);
                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackage.put("MODIFY_TAG", "EXIST");// 默认不变

                IDataset element = IUpcViewCall.qryOfferByOfferIdRelOfferId(temp.getString("PRODUCT_ID"), "P", temp.getString("ELEMENT_ID"), temp.getString("ELEMENT_TYPE_CODE"), "");
                if (DataUtils.isNotEmpty(element)) {
                    userGrpPackage.put("SELECT_FLAG", element.first().getString("FORCE_TAG", ""));
                }

                userGrpPackageList.add(userGrpPackage);
            }
            setEcPackages(userGrpPackageList);
        } else {

            IData inparam = new DataMap();
            inparam.put("PRODUCT_ID", productId);
            inparam.put(Route.USER_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
            elementList = CSViewCall.call(this, "CS.ProductInfoQrySVC.getMebProductForceElements", inparam);
            for (int i = 0; i < elementList.size(); i++) {
                IData temp = elementList.getData(i);
                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_FORCE_TAG", temp.getString("ELEMENT_FORCE_TAG"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("MODIFY_TAG", "0");
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackage.put("SELECT_FLAG", temp.getString("ELEMENT_FORCE_TAG"));
                userGrpPackageList.add(userGrpPackage);
            }

            setEcPackages(userGrpPackageList);
        }

        // 获取必选包数据
        IDataset mebPkgList = ProductPkgInfoIntfViewUtil.qryMebForcePackageByGrpProId(this, productId, this.getVisit().getLoginEparchyCode());
        if ("7342".equals(productId) || "7343".equals(productId) || "7344".equals(productId)) {
            Iterator<Object> it = mebPkgList.iterator();
            while (it.hasNext()) {
                IData mebPkg = (IData) it.next();
                if ("734301".equals(mebPkg.getString("PRODUCT_ID"))) {
                    it.remove();
                }
            }
        }

        setForcePkgList(mebPkgList);

        return userGrpPackageList;
    }

    /**
     * 初始化产品特征规格区域
     *
     * @param cycle
     * @throws Exception
     */
    public void initOfferChaSpecByOfferId(IRequestCycle cycle) throws Exception {
        IData pageData = this.getData();
        String custId = pageData.getString("CUST_ID");
        String offerId = pageData.getString("OFFER_ID");
        String offerCode = pageData.getString("OFFER_CODE");
        String curOfferId = pageData.getString("SUB_OFFER_ID");// 当前设置的商品id
        String curOfferCode = pageData.getString("SUB_OFFER_CODE");// 当前设置的商品code
        String curOfferType = pageData.getString("SUB_OFFER_TYPE");// 当前设置的商品类型
        String brandCode = pageData.getString("BRAND_CODE");
        String subscriberInsId = pageData.getString("USER_ID");
        String offerInsId = pageData.getString("OFFER_INS_ID");

        String operType = PageDataTrans.transOperCodeToOperType(pageData.getString("OPER_CODE"), "EC");

        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", curOfferId); // POINT_ONE
        inAttr.put("NODE_ID", operType); // POINT_TWO
        if ("BOSG".equals(brandCode)) {
            // BBOSS本地产品编码转换为全网产品编码
            IData input = new DataMap();
            String merchOperType = this.getData().getString("MERCHP_OPER_TYPE");
            input.put("OPER_TYPE", inAttr.getString("NODE_ID"));
            input.put("PROD_SPEC_ID", curOfferCode);
            input.put("MERCHP_OPER_TYPE", merchOperType);
            FrontProdConverter.prodConverter(this, input, false);

            // 操作类型转换为全网操作类型
            inAttr.put("FLOW_ID", input.getString("PROD_SPEC_ID"));// POINT_ONE
            inAttr.put("NODE_ID", input.getString("OPER_TYPE"));// POINT_TWO
        }

        setInAttr(inAttr);

        // 初始化产品特征(非静态表加载的数据)
        // 属性这一块，后续处理
        // 如果产品特征有特殊处理,产品参数配置为P,服务参数(优惠会有组件处理,无需担心优惠问题)配置为S,如果走公用参数处理逻辑,则不需要配置
        String svcName = "";
        if (((curOfferCode.equals(offerCode)) && !("S".equals(curOfferType))) || "BOSG".equals(brandCode)) {
            svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, "P", operType, "InitOfferCha");
        } else {
            svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, "S", operType, "InitOfferCha");
        }

        IDataset grpItemInfo = new DatasetList();
        if (StringUtils.isBlank(svcName)) {// 没有配置，取默认服务初始化
            if (BizCtrlType.CreateUser.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryOfferChaForInit";
            } else if (BizCtrlType.ChangeUserDis.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryUserAttrForChgInit";
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作类型异常！OPER_TYPE=" + operType);
            }
        }
        IData busi = new DataMap();
        IData input = new DataMap();
        input.put("OFFER_ID", curOfferId);
        input.put("ATTR_OBJ", "0");
        input.put("EPARCHY_CODE", getTradeEparchyCode());
        input.put("USER_ID", subscriberInsId);
        input.put("OFFER_INS_ID", offerInsId);
        input.put("RELA_INST_ID", offerInsId);
        input.put("INST_TYPE", curOfferType);// USER_ATTR表中的INST_TYPE属性
        input.put("IS_MEB", "false");
        input.put("CUST_ID", custId);
        input.put("PRODUCT_ID", curOfferCode);
        input.put("OFFER_CODE", offerCode);// 方便ADC对特殊产品进行判断,对逻辑无影响
        input.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset result = CSViewCall.call(this, svcName, input);

        if (IDataUtil.isNotEmpty(result)) {
            IData groupParamData = result.getData(0);
            IData attrGroupData = groupParamData.getData("ATTR_GROUP_MAP");
            if ("BOSG".equals(brandCode) && IDataUtil.isNotEmpty(attrGroupData)) {

                if (IDataUtil.isNotEmpty(attrGroupData)) {
                    Iterator itr = attrGroupData.keySet().iterator();
                    while (itr.hasNext()) {

                        String key = itr.next().toString();
                        IDataset attrInfos = attrGroupData.getDataset(key);
                        for (int i = 0; i < attrInfos.size(); i++) {
                            IData attrInfo = attrInfos.getData(i);
                            IData paramInfo = new DataMap();

                            paramInfo.put("CHA_SPEC_ID", attrInfo.getString("FIELD_NAME").split("_")[0]);
                            paramInfo.put("ATTR_VALUE", attrInfo.getString("ATTR_VALUE"));
                            paramInfo.put("ATTR_GROUP", attrInfo.getString("ATTR_GROUP"));
                            paramInfo.put("ATTR_CODE", attrInfo.getString("FIELD_NAME").split("_")[0]);

                            grpItemInfo.add(paramInfo);
                        }

                    }
                }

            } else {

                IData param = result.getData(0);
                Iterator itr = param.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().toString();
                    if (key.contains("_")) {
                        String[] keys = key.split("♂♂");
                        if (keys.length > 1) {
                            key = keys[0];
                        }
                    }
                    // String paramData = param.getString(key);
                    // String value = param.getString(key);
                    IData paramData = param.getData(key);
                    String value = param.getString(key);
                    // 针对属性组的情况进行特殊处理
                    if (value.contains("♂♂")) {
                        value = paramData.getString("VALUE");
                        String[] groupItem = value.split("♂♂");
                        if (groupItem.length > 1) {
                            value = groupItem[0];
                            String groupAttr = groupItem[1];
                            IData paramInfo = new DataMap();
                            paramInfo.put("CHA_SPEC_ID", key);
                            paramInfo.put("CHA_VALUE", value);
                            paramInfo.put("GROUP_ATTR", groupAttr);
                            grpItemInfo.add(paramInfo);
                        }
                    }

                    busi.put(key, paramData);
                }

            }

        }
        setBusi(busi);

        IData retData = new DataMap();
        retData.put("result", grpItemInfo);
        this.setAjax(retData);
    }

    /**
     *
     * @Title queryNeddContract
     * @Description 查询合同是否为必选
     * @author zhouchao5
     * @param cycle
     * @throws Exception
     * @return void 返回类型
     */
    public void queryNeddContract(IRequestCycle cycle) throws Exception {
        String offerCode = getData().getString("OFFER_CODE", "-1");
        IData param = new DataMap();
        IData result = null;// EcParamViewUtil.queryCfgProdAttrItemByIdIdTypeObjCode(offerId, "P", "0", "NeedContract");
        if (result != null && result.size() > 0) {
            String value = result.getString("VALUE");
            if ("0".equals(value)) {
                param.put("NeedContract", true);
            } else {
                param.put("NeedContract", false);
            }
        } else {
            param.put("NeedContract", false);
        }
        setAjax(param);
    }

    /**
     * @Title: getBbossMerchPageConfig
     * @Description: 取得bboss产品配置
     * @param @param cycle
     * @param @throws Exception
     * @return void
     * @throws
     */
    public void getBbossMerchPageConfig(IRequestCycle cycle) throws Exception {
        String offerId = getData().getString("OFFER_ID", "0");
        String operType = getData().getString("OPER_TYPE", "");
        IData param = new DataMap();
        param.put("hasMerchPage", "false");

        IDataset result = CommonViewCall.getAttrsFromAttrBiz(this, offerId, "P", "BbossPage", "-1");
        if (result != null && DataUtils.isNotEmpty(result)) {
            param.put("hasMerchPage", "true");
        }
        setAjax(param);
    }

    /**
     * 校验商品是否支持当前操作类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkOperTypeByOfferId(IRequestCycle cycle) throws Exception {
        String offerCode = this.getData().getString("OFFER_CODE");
        String operType = this.getData().getString("OPER_TYPE");

        boolean flag = true;

        String busiType = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, "P", operType, "TradeTypeCode");
        if (StringUtils.isEmpty(busiType)) {
            flag = false;
        }

        IData ajaxData = new DataMap();
        ajaxData.put("CHECK_FLAG", flag);
        ajaxData.put("LOGIN_STAFF_ID", this.getVisit().getStaffId());
        this.setAjax(ajaxData);
    }

    /**
     * 构造esop传递过来的offerData
     *
     * @param inOfferData
     * @throws Exception
     */
    private void initPageForEsop(IData inOfferData, IData info) throws Exception {
        String groupId = info.getString("GROUP_ID");
        if (StringUtils.isBlank(groupId)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "没有获取到集团客户编码【GROUP_ID】！");
        }
        String operCode = inOfferData.getString("OPER_CODE");
        if (StringUtils.isBlank(operCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_268);
        }

        String offerCode = inOfferData.getString("OFFER_CODE");
        if (StringUtils.isBlank(offerCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_268);
        }
        String offerId = IUpcViewCall.getOfferIdByOfferCode(offerCode);

        IData initOffer = new DataMap();
        initOffer.put("OFFER_CODE", offerCode);
        initOffer.put("OFFER_ID", offerId);
        initOffer.put("OFFER_NAME", inOfferData.getString("OFFER_NAME"));
        initOffer.put("BRAND_CODE", UpcViewCall.queryBrandCodeByOfferCodeAndType(this, offerCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT));
        initOffer.put("BRAND", UpcViewCall.queryBrandCodeByOfferCodeAndType(this, offerCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT));
        initOffer.put("OPER_CODE", operCode);
        initOffer.put("GROUP_ID", groupId);

        if ("0".equals(operCode)) {
            initCreateForEsop(inOfferData, initOffer, info);
        } else if ("2".equals(operCode) || "1".equals(operCode)) {
            initUpdateDeleteForEsop(inOfferData, initOffer, info);
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_1135);
        }
        setInitOffer(initOffer);
    }

    private void initUpdateDeleteForEsop(IData inOfferData, IData initOffer, IData info) throws Exception {
        IData offerData = buildEsopOfferData(inOfferData);
        initOffer.put("ESOP_OFFER_DATA", offerData);

        // 1. 组装主商品信息
        String brandCode = inOfferData.getString("BRAND_CODE");
        String mainOfferId = inOfferData.getString("OFFER_ID");
        String mainUserId = inOfferData.getString("USER_ID");
        String operCode = inOfferData.getString("OPER_CODE");
        String operType = EcConstants.transOperCodeToOperType(operCode, "EC");

        // 2.查询商品用户信息
        IData userInfodData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, mainUserId);
        initOffer.put("SERIAL_NUMBER", userInfodData.getString("SERIAL_NUMBER"));
        initOffer.put("SERIAL_NUMBER_SUCCESS", "true"); // 不需要校验服务号码
        initOffer.put("IF_RES_CODE", "0");// 如果IF_RES_CODE=0，服务号码框Disabled
        initOffer.put("OFFER_INS_ID", userInfodData.getString("INST_ID"));// 对应ATTR表的rele_inst_id
        initOffer.put("USER_ID", mainUserId);
        if ("BOSG".equals(brandCode)) {
            initOffer.put("OPER_TYPE", EcConstants.FLOW_ID_EC_CHANGE);
        } else {
            initOffer.put("OPER_TYPE", operType);
        }

        String offerCode = userInfodData.getString("PRODUCT_ID");

        // 3.查询是否支持当前操作
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(offerCode, "P", operType, "TradeTypeCode");
        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSViewException.apperr(ProductException.CRM_PRODUCT_97);
        }
        initOffer.put("TRADE_TYPE_CODE", tradeTypeCode);

        // 4.查询定制信息
        IData offer = UpcViewCall.queryOfferByOfferId(this, "P", offerCode, "Y");
        String useTag = offer.getString("USE_TAG");
        if ("1".equals(useTag)) {// 如果定制，查询成员商品编码
            IDataset mebOffers = IUpcViewCall.queryOfferJoinRelAndOfferByOfferId(mainOfferId, "1", "", "");
            // 不存在成员产品
            if (DataUtils.isEmpty(mebOffers)) {
                useTag = "0";// 如果不存在成员商品，则不需要定制
            }
            // 定制后续处理
            String OfferCode=IUpcViewCall.getOfferCodeByOfferId(mainOfferId);
            IDataset userGrpPackageList = queryEcPackages(OfferCode, mainUserId);
            info.put("USER_PACKAGES", userGrpPackageList);
            info.put("USE_TAG", useTag);
            offerData.put("GRP_PACKAGE_INFO", userGrpPackageList);
        }

    }

    /**
     * 没有CHA_SPEC_ID属性的剔除
     * 
     * @return
     * @throws Exception
     */
    public void dealEsopCha(IData insOfferData) throws Exception {
        IDataset offerChaSpecs = insOfferData.getDataset("OFFER_CHA_SPECS");
        IDataset newOfferChaSpec = new DatasetList();
        if (DataUtils.isNotEmpty(offerChaSpecs)) {
            for (int i = 0; i < offerChaSpecs.size(); i++) {
                IData offerChaSpec = offerChaSpecs.getData(i);
                if (StringUtils.isNotEmpty(offerChaSpec.getString("CHA_SPEC_ID"))) {
                    newOfferChaSpec.add(offerChaSpec);
                }
            }
            if (DataUtils.isNotEmpty(offerChaSpecs)) {
                insOfferData.put("OFFER_CHA_SPECS", newOfferChaSpec);
            } else {
                insOfferData.remove("OFFER_CHA_SPECS");
            }
        }
    }

    private IData buildEsopOfferData(IData inOfferData) throws Exception {
        IData offerData = new DataMap();
        String offerId = inOfferData.getString("OFFER_ID");
        String brandCode = inOfferData.getString("BRAND_CODE");
        String operCode = inOfferData.getString("OPER_CODE");

        String operType = EcConstants.transOperCodeToOperType(operCode, "EC");

        offerData.put("OFFER_ID", offerId);
        offerData.put("OFFER_CODE", inOfferData.getString("OFFER_CODE"));
        offerData.put("OFFER_NAME", inOfferData.getString("OFFER_NAME"));
        offerData.put("BRAND_CODE", brandCode);
        offerData.put("OFFER_TYPE", UpcConst.ELEMENT_TYPE_CODE_PRODUCT);
        offerData.put("OPER_CODE", inOfferData.getString("OPER_CODE"));
        offerData.put("OPER_TYPE", operType);

        offerData.put(EcConstants.SUBIBID_RNUM, inOfferData.getString(EcConstants.SUBIBID_RNUM));// 需要登记到CRM-tf_B_trade表RSRV_STR4，用于CRM与ESOP产品对应关系
        String offerChaSpecsStr = inOfferData.getString("OFFER_CHA_SPECS");
        if (StringUtils.isNotBlank(offerChaSpecsStr) && !"BOSG".equals(brandCode)) {
            offerData.put("OFFER_CHA_SPECS", new DatasetList(offerChaSpecsStr));
        }

        IDataset subOfferList = new DatasetList();
        String subOfferStr = inOfferData.getString("SUBOFFERS", "");

        if (StringUtils.isNotEmpty(subOfferStr)) {
            subOfferList = new DatasetList(subOfferStr);
        }
        // 1.开通
        if (EcConstants.ACTION_CREATE.equals(operCode)) {
            eopCreateOfferData(inOfferData, subOfferList, offerData);
        } else if (EcConstants.ACTION_UPDATE.equals(operCode) || EcConstants.ACTION_DELETE.equals(operCode)) {
            eopUpdateAndDeleOfferData(inOfferData, subOfferList, offerData);
        }

        return offerData;
    }

    private void eopCreateOfferData(IData inOfferData, IDataset subOfferList, IData offerData) throws Exception {
        // 第一层 SUBOFFERS信息处理的存容器
        IDataset subOfferDataset = new DatasetList();

        IDataset mustSelOfferList = IUpcViewCall.queryOfferComRelOfferByOfferId(inOfferData.getString("OFFER_ID"), getTradeEparchyCode());// 查询商品的构成关系
        // 1.1把构成数据拼到SUBOFFERS
        if (IDataUtil.isNotEmpty(mustSelOfferList)) {
            buildMustSelOfferInSubOffers(mustSelOfferList, subOfferList, offerData);
        }

        // 1.2 处理SUBOFFERS数据，包括构成数据
        if (IDataUtil.isNotEmpty(subOfferList)) {
            IDataset serviceOfferList = new DatasetList();
            IDataset priceOfferList = new DatasetList();
            for (int i = 0, size = subOfferList.size(); i < size; i++) {
                IData subOfferData = new DataMap();
                IData subOffer = subOfferList.getData(i);
                String subOfferId = subOffer.getString("OFFER_ID");

                // 1.2.1 处理 单条SUBOFFERS数据，包括构成数据
                offerData.put("isEsopEbuop", inOfferData.getString("isEsopEbuop"));
                dealSubOffers(subOffer, offerData, subOfferData, serviceOfferList, priceOfferList, true);

                String offerType = subOfferData.getString("OFFER_TYPE");

                // 只有BBOSS商品下的子产品才会有资费、服务
                if (UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType)) {
                    // 1.2.2处理子商品的子商品
                    IDataset childSubOfferList = new DatasetList();
                    String subSubOfferSrt = subOffer.getString("SUBOFFERS");
                    if (StringUtils.isNotBlank(subSubOfferSrt)) {
                        childSubOfferList = new DatasetList(subSubOfferSrt);
                    }

                    // 第二层 SUBOFFERS信息处理后数据存放容器
                    IDataset childSubOfferDataset = new DatasetList();

                    // 1.2.2.1把构成数据拼到SUBOFFERS
                    IDataset childMustSelOfferList = IUpcViewCall.queryOfferComRelOfferByOfferId(subOfferId, getTradeEparchyCode());
                    if (IDataUtil.isNotEmpty(childMustSelOfferList)) {
                        buildMustSelOfferInSubOffers(childMustSelOfferList, childSubOfferList, subOffer);
                    }
                    if (IDataUtil.isNotEmpty(childSubOfferList)) {
                        for (int h = 0, hSize = childSubOfferList.size(); h < hSize; h++) {
                            IData childSubOfferData = new DataMap();
                            IData childSubOffer = childSubOfferList.getData(h);

                            // 1.2.1 处理 单条SUBOFFERS数据，包括构成数据
                            dealSubOffers(childSubOffer, subOfferData, childSubOfferData, serviceOfferList, priceOfferList, false);

                            childSubOfferDataset.add(childSubOfferData);
                            subOfferData.put("SUBOFFERS", childSubOfferDataset);
                        }
                    }
                }

                subOfferDataset.add(subOfferData);
                offerData.put("SUBOFFERS", subOfferDataset);
            }

            setServiceOfferList(serviceOfferList);
            setPriceOfferList(priceOfferList);
        }

    }

    private void eopUpdateAndDeleOfferData(IData inOfferData, IDataset subOfferList, IData offerData) throws Exception {
        String mainOfferCode = inOfferData.getString("OFFER_CODE");
        String mainUserId = inOfferData.getString("USER_ID");

        IData userInfodData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, mainUserId);
        offerData.put("SERIAL_NUMBER", userInfodData.getString("SERIAL_NUMBER"));
        offerData.put("OFFER_INS_ID", userInfodData.getString("INST_ID"));// 对应ATTR表的rele_inst_id
        offerData.put("USER_ID", mainUserId);

        // 第一层 SUBOFFERS信息处理的存容器
        IDataset subOfferDataset = new DatasetList();

        // 查询主商品下的子产品，商品资费，商品服务数据
        IDataset isExistOfferList = getSelectedOffers(mainOfferCode, mainUserId);

        // 1.1把主商品下的子产品，商品资费，商品服务拼到EOSP数据中的SUBOFFERS里
        if (IDataUtil.isNotEmpty(isExistOfferList)) {
            buildIsExistOfferInSubOffers(isExistOfferList, subOfferList, offerData);
        }

        // 1.2 处理SUBOFFERS数据，包括构成数据
        if (IDataUtil.isNotEmpty(subOfferList)) {
            IDataset serviceOfferList = new DatasetList();
            IDataset priceOfferList = new DatasetList();
            for (int i = 0, size = subOfferList.size(); i < size; i++) {
                IData subOfferData = new DataMap();
                IData subOffer = subOfferList.getData(i);

                // 1.2.1 处理 单条SUBOFFERS数据，包括构成数据
                offerData.put("isEsopEbuop", inOfferData.getString("isEsopEbuop"));
                offerData.put("GROUP_ID", getData().getString("GROUP_ID"));
                dealUpdataAndDeleSubOffers(subOffer, offerData, subOfferData, serviceOfferList, priceOfferList, true);

                String offerType = subOfferData.getString("OFFER_TYPE");
                String subUserId = subOfferData.getString("USER_ID");
                String subOfferCode = subOfferData.getString("OFFER_CODE");
                // 只有BBOSS商品下的子产品才会有资费、服务
                if (UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType)) {
                    // 1.2.2处理子商品的子商品
                    IDataset childSubOfferList = new DatasetList();
                    String subSubOfferSrt = subOffer.getString("SUBOFFERS");
                    if (StringUtils.isNotBlank(subSubOfferSrt)) {
                        childSubOfferList = new DatasetList(subSubOfferSrt);
                    }

                    // 第二层 SUBOFFERS信息处理后数据存放容器
                    IDataset childSubOfferDataset = new DatasetList();

                    // 1.2.2.1查询子产品下的资费，服务数据，把资料数据拼到EOP的SUBOFFERS里
                    IDataset isExistSubOfferList = getSelectedOffers(subOfferCode, subUserId);
                    if (IDataUtil.isNotEmpty(isExistSubOfferList)) {
                        buildIsExistOfferInSubOffers(isExistSubOfferList, childSubOfferList, subOffer);
                    }
                    if (IDataUtil.isNotEmpty(childSubOfferList)) {
                        for (int h = 0, hSize = childSubOfferList.size(); h < hSize; h++) {
                            IData childSubOfferData = new DataMap();
                            IData childSubOffer = childSubOfferList.getData(h);

                            // 1.2.1 处理 单条SUBOFFERS数据，包括构成数据
                            dealUpdataAndDeleSubOffers(childSubOffer, subOfferData, childSubOfferData, serviceOfferList, priceOfferList, false);

                            childSubOfferDataset.add(childSubOfferData);
                            subOfferData.put("SUBOFFERS", childSubOfferDataset);
                        }
                    }
                }

                subOfferDataset.add(subOfferData);
            }
            offerData.put("SUBOFFERS", subOfferDataset);
            setServiceOfferList(serviceOfferList);
            setPriceOfferList(priceOfferList);
        }
    }

    /**
     * 将esop传入的商品特征拼入CHA_SPEC_ID
     * 
     * @throws Exception
     */
    public void queryChaSpecIdToOfferChas(String offerCode, String offerType, String operType, IDataset offerChaSpecs) throws Exception {
        /* if(UpcConst.OFFER_TYPE_DISCNT.equals(offerType)) { IDataset offerChaList = IUpcViewCall.queryChaByOfferId(offerCode, offerType); if(DataUtils.isEmpty(offerChaList)) { return ; } for(int i = 0, sizeI = offerChaSpecs.size(); i < sizeI; i++)
         * { IData offerChaSpec = offerChaSpecs.getData(i); for(int j = 0, sizeJ = offerChaList.size(); j < sizeJ; j++) { IData offerCha = offerChaList.getData(j); if(offerCha.getString("FIELD_NAME").equals(offerChaSpec.getString("CHA_SPEC_CODE"))) {
         * offerChaSpec.put("CHA_SPEC_ID", offerCha.getString("CHA_SPEC_ID")); break; } } } } else { IDataset offerChaList = EcParamViewUtil.queryElementsByPointOnePointTwo(operType, offerId); if(DataUtils.isEmpty(offerChaList)) { return ; } for(int
         * i = 0, sizeI = offerChaSpecs.size(); i < sizeI; i++) { IData offerChaSpec = offerChaSpecs.getData(i); for(int j = 0, sizeJ = offerChaList.size(); j < sizeJ; j++) { IData offerCha = offerChaList.getData(j);
         * if(offerCha.getString("ELEMENT_KEY").equals(offerChaSpec.getString("CHA_SPEC_CODE"))) { offerChaSpec.put("CHA_SPEC_ID", offerCha.getString("ELEMENT_ID")); break; } } } } */

    }

    /**
     * 资源号码校验（预留）
     *
     * @param cycle
     * @throws Exception
     */
    public void checkAccesssNum(IRequestCycle cycle) throws Exception {
        /* IData data = this.getData(); String accessNumType = data.getString("SERIAL_NUMBER_TYPE"); IData retData = new DataMap(); if (EcConstants.SERIAL_NUMBER_TYPE_FOR_MUST_SN.equals(accessNumType)) { IData result =
         * CSViewCall.call(this,"OrderCentre.enterprise.param.ICfgProdAttrItemSV.queryResourceInfoByAccessNumAndType", data); IDataset retDataset = result.getDataset("RESULT_INFO"); IDataset resDataset = result.getDataset("RES_INFO");
         * retData.put("RESULT_INFO", retDataset.getData(0)); if (null != resDataset && 0 != resDataset.size()) { retData.put("RES_INFO", resDataset.getData(0)); } } else if (EcConstants.SERIAL_NUMBER_TYPE_FOR_MUST_TTGH.equals(accessNumType)) {
         * 
         * } else if (EcConstants.SERIAL_NUMBER_TYPE_FOR_MUST_TTSN.equals(accessNumType)) {
         * 
         * } else { CSViewException.apperr(EnterpriseException.CRM_EC_9); } this.setAjax(retData); */
    }

    /**
     * 前台数据提交
     *
     * @param cycle
     * @throws Exception
     */
    public void submit(IRequestCycle cycle) throws Exception {
        IDataset datas = new DatasetList(getData().getString("SUBMIT_DATA"));

        logger.debug("==============================前台传入数据{}", datas);

        String operType = datas.first().getString("OPER_TYPE");
        String ifCentreType = datas.first().getData("COMMON_INFO").getString("IF_CENTRETYPE", "");

        // //这里获取到的费用信息才是准确的
        // datas.first().put("X_TRADE_FEESUB", getData().getString("X_TRADE_FEESUB"));
        // datas.first().put("X_TRADE_PAYMONEY", getData().getString("X_TRADE_PAYMONEY"));
        //
        IData cond = new DataMap();
        cond.put("IN_PARAMS", datas);
        cond.put("OPER_TYPE", operType);
        cond.put("OFFER_CODE", getData().getString("OFFER_CODE"));

        // 获取所有元素，规则用，只能写在这里，因为下面PageDataTrans时，会改变datas的数据
        IDataset allElement = new DatasetList();
        if (DataUtils.isNotEmpty(datas.first().getDataset("OFFERS")) && DataUtils.isNotEmpty(datas.first().getDataset("OFFERS").first().getDataset("SUBOFFERS"))) {
            allElement.addAll(datas.first().getDataset("OFFERS").first().getDataset("SUBOFFERS"));
        }
        PageDataTrans pageTransData = PageDataTrans.getInstance(cond);

        IData svcParam = pageTransData.transformData();

        if (IDataUtil.isNotEmpty(datas.first().getData("AUDIT_INFO"))) {
            IData auditInfo = datas.first().getData("AUDIT_INFO");
            if (!StringUtils.isBlank(auditInfo.getString("AUDIT_STAFF_ID"))) {
                svcParam.put("AUDIT_STAFF_ID", auditInfo.getString("AUDIT_STAFF_ID", ""));
            }
            if (!StringUtils.isBlank(auditInfo.getString("MEB_VOUCHER_FILE_LIST"))) {
                svcParam.put("VOUCHER_FILE_LIST", auditInfo.getString("MEB_VOUCHER_FILE_LIST", ""));
            }
        }

        // 处理流量自由充流程数据
        IData eosInfo = svcParam.getData("EOS_INFO");
        String productId = getData().getString("OFFER_CODE");
        if ("7342".equals(productId) || "7343".equals(productId) || "7344".equals(productId)) {
            eosInfo.put("ATTR_CODE", "ESOP");
            eosInfo.put("ATTR_VALUE", eosInfo.getString("IBSYSID"));
            eosInfo.put("RSRV_STR1", eosInfo.getString("NODE_ID"));
            svcParam.put("EOS", new DatasetList(eosInfo));
        }

        svcParam.put("X_TRADE_FEESUB", datas.first().getDataset("X_TRADE_FEESUB"));
        svcParam.put("X_TRADE_PAYMONEY", datas.first().getDataset("X_TRADE_PAYMONEY"));

        /************************* 调用规则productinfo 开始 *************************/

        checkEcProductInfoRule(svcParam, allElement, operType);
        /************************* 调用规则productinfo 结束 *************************/
        svcParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        svcParam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        System.out.println(svcParam);
        String svc = pageTransData.getSvcName();

        // 对新增账户的非现金账户类别进行补充
        IData acctInfo = svcParam.getData("ACCT_INFO", new DataMap());
        if (IDataUtil.isNotEmpty(acctInfo)) {
            if (acctInfo.getString("CONSIGN_MODE", "0").equals("1") && acctInfo.getString("MODIFY_TAG", "3").equals("0")) {
                svcParam.put("ACCT_INFO", acctInfo);
            }
        }

        logger.debug("=============================调用服务 {} 数据 {}", svc, svcParam);

        // 调登记服务
        IDataset result = CSViewCall.call(this, svc, svcParam);

        logger.debug("=============================服务返回结果:{}", result);
        logger.debug("=============================服务流水号:{}, 请求入参:{}", result.first().getString("ORDER_ID"), svcParam);

        setAjax(result);
    }

    // 动力100查询子产品信息的方法
    public void queryOfferInstInfos(IRequestCycle cycle) throws Exception {
        String offerIdstr = this.getData().getString("OFFER_ID");
        // String offerCodestr = this.getData().getString("OFFER_CODE");
        String[] offerIds = offerIdstr.split("@");
        // String[] offerCodes = offerCodestr.split("@");
        String custId = this.getData().getString("CUST_ID");

        IDataset ajaxInfos = new DatasetList();
        for (int i = 0; i < offerIds.length; i++) {
            if (StringUtils.isEmpty(offerIds[i])) {
                continue;
            }
            IData offer = IUpcViewCall.queryOfferByOfferId(offerIds[i]);
            IData param = new DataMap();
            param.put("CUST_ID", custId);
            param.put("PRODUCT_ID", offer.getString("OFFER_CODE"));// offerCodes[i]);

            IDataset offers = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", param);

            if (DataUtils.isNotEmpty(offers) && offers.size() == 1) {
                ajaxInfos.add(offers.getData(0));
            } else if (DataUtils.isNotEmpty(offers) && offers.size() > 1) {
                IData offerIns = new DataMap();
                offerIns.put("OFFER_ID", offerIds[i]);
                offerIns.put("OFFER_NAME", IUpcViewCall.queryOfferByOfferId(offerIds[i]).getString("OFFER_NAME"));
                offerIns.put("INS_SIZE", offers.size());
                ajaxInfos.add(offerIns);
            }
        }
        IData ajax = new DataMap();
        ajax.put("INS_OFFERS", ajaxInfos);
        setAjax(ajax);
    }

    public void queryMainOfferInstancesByCustIdOfferId(IRequestCycle cycle) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", this.getData().getString("CUST_ID"));
        param.put("PRODUCT_ID", this.getData().getString("OFFER_CODE"));

        IDataset offers = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", param);
        // 还需要将旧key转换成新key
        setInstOffers(offers);

    }

    /**
     * 获取销售品构成关系的选择标记SELECT_FLAG
     *
     * @param offerId
     * @param relOfferId
     * @return
     * @throws Exception
     */
    /* private String queryOfferJoinSelectFlagByOfferIdAndRelOfferId(String offerId, String relOfferId) throws Exception { IData inparam = new DataMap(); inparam.put("OFFER_ID", offerId); inparam.put("REL_OFFER_ID", relOfferId); IData offerJoinInfo =
     * EcUpcViewUtil.queryOfferJoinRelBy2OfferIdRelType(offerId, relOfferId, "4"); if (DataUtils.isNotEmpty(offerJoinInfo)) { return offerJoinInfo.getString("SELECT_FLAG"); } else { return "-1"; } }
     * 
     * private IDataset queryInsEcPackageDataset(String subscriberInsId, String mainOfferId) throws Exception { IDataset insEcPackageDataset = EcOfferViewUtil.queryInsEcPackage(subscriberInsId); if (DataUtils.isNotEmpty(insEcPackageDataset)) {
     * IDataset mustSelSubOffers = EcUpcViewUtil.queryMustSelSubOffersByOfferId(mainOfferId, getTradeEparchyCode());
     * 
     * for (int i = 0, size = insEcPackageDataset.size(); i < size; i++) { IData ecPackageData = insEcPackageDataset.getData(i); ecPackageData.put("MAIN_OFFER_ID", mainOfferId);
     * 
     * //标识构成关系 boolean forceTag = false; for(int j = 0, sizeJ = mustSelSubOffers.size(); j < sizeJ; j++) { if(ecPackageData.getString("OFFER_ID", "").equals(mustSelSubOffers.getData(j).getString("OFFER_ID"))) {//标识必选子商品（构成关系或者必选包的必选元素） forceTag =
     * true; break; } } ecPackageData.put("FORCE_TAG", forceTag); ecPackageData.put("OFFER_NAME", EcUpcViewUtil.queryOfferNameByOfferId(ecPackageData.getString("OFFER_ID", ""))); ecPackageData.put("OPER_CODE", "EXISTS"); } }
     * 
     * return insEcPackageDataset; } */
    /* private IDataset queryPower100Dataset(String subscriberInsId, String mainOfferId) throws Exception { IDataset childSubs = new DatasetList(); String relationType = EcUpcViewUtil.queryRelationTypeCodeByOfferId(mainOfferId); IDataset
     * insChildSubDataset = EcSubscriberViewUtil.queryRelSubOffersBySubscriberIdAndRelType(subscriberInsId, relationType); if (DataUtils.isNotEmpty(insChildSubDataset)) { for (int i = 0, size = insChildSubDataset.size(); i < size; i++) { IData child
     * = new DataMap(); IData childSub = insChildSubDataset.getData(i);
     * 
     * String offerId = childSub.getString("OFFER_ID"); child.put("OFFER_ID", offerId); child.put("SERIAL_NUMBER", childSub.getString("SERIAL_NUMBER")); child.put("OFFER_NAME", childSub.getString("OFFER_NAME")); child.put("USER_ID",
     * childSub.getString("USER_ID")); child.put("OFFER_INS_ID", childSub.getString("OFFER_INS_ID")); child.put("OFFER_TYPE", EcUpcViewUtil.queryOfferTypeByOfferId(offerId)); child.put("OPER_CODE", "EXISTS"); // OPER_CODE初始为2-变更
     * child.put("SELECT_FLAG", queryOfferJoinSelectFlagByOfferIdAndRelOfferId(mainOfferId, offerId)); childSubs.add(child); } } return childSubs; } */

    private IDataset queryAttachOfferDataset(String offerId) throws Exception {
        IDataset attachOffers = null;// 方法已废弃：EcUpcViewUtil.queryOfferComRelDetailsByOfferIdAndRoleId(offerId, EcConstants.OFFER_COM_REL_ROLE_ID_ATTACHOFFER, false, getTradeEparchyCode());
        return attachOffers;
    }

    private String createFuncNaviCookie(IRequestCycle cycle) throws Exception {
        CookieUtil cookie = new CookieUtil(getRequest(), getResponse(), "CRM_ECNAVIGATION_COOKIE", 24 * 7);
        String showFuncNavigation = "1";
        if (cookie.load()) {
            showFuncNavigation = cookie.get("FUNC_NAVIGATION_EC_OFFER");
        }

        if (StringUtils.isEmpty(showFuncNavigation)) {
            showFuncNavigation = "1";
        }
        cookie.put("FUNC_NAVIGATION_EC_OFFER", showFuncNavigation);
        cookie.store();

        return showFuncNavigation;
    }

    private IData parseMapToData(Map map) throws Exception {
        IData data = new DataMap();

        Iterator itr = map.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next().toString();
            Object o = map.get(key);
            if (o instanceof String) {
                String value = map.get(key).toString();
                data.put(key, value);
                continue;
            } else if (o instanceof ArrayList) {
                List list = (ArrayList) map.get(key);
                IDataset dataset = new DatasetList();
                for (int i = 0, size = list.size(); i < size; i++) {
                    Map subMap = (HashMap) list.get(i);
                    IData subData = parseMapToData(subMap);
                    dataset.add(subData);
                }
                data.put(key, dataset);
                continue;
            } else if (o instanceof Map) {
                Map subMap = (HashMap) map.get(key);
                IData subData = parseMapToData(subMap);
                data.put(key, subData);
                continue;
            }
        }

        return data;
    }

    /**
     * 查询员工手机号
     *
     * @param cycle
     * @throws Exception
     */
    public void queryStaffInfo(IRequestCycle cycle) throws Exception {
        IData input = getData();
        IDataset staffInfos = CSViewCall.call(this, "BC.ISecStaffMgrSV.queryBbossStaffByParam", input);
        if (DataUtils.isNotEmpty(staffInfos)) {
            String access_number = staffInfos.getData(0).getString("MOBILE");
            String opBbossId = staffInfos.getData(0).getString("OP_BBOSS_ID");
            IData ajaxData = new DataMap();
            ajaxData.put("SERIAL_NUMBERBER", access_number);
            ajaxData.put("OP_BBOSS_ID", opBbossId);
            setAjax(ajaxData);
        }
    }

    /**
     * Check SerialNumber
     *
     * @param pageData
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle cycle) throws Exception {
        IData pageData = getData();

        String resultDesc = checkResultInfo(pageData);

        IData infoData = new DataMap();
        infoData.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
        infoData.put("RES_TYPE_CODE", pageData.getString("RES_TYPE_CODE", ""));

        IData ajaxData = new DataMap();
        if ("".equals(resultDesc)) // Validate Pass
        {
            infoData.put("IF_RES_CODE", "0");

            ajaxData.put("X_RESULTCODE", "0");
            ajaxData.put("X_RESULTINFO", "服务号码检验通过,录入的服务号码可以使用!");
        } else
        // Validate Failed
        {
            infoData.put("IF_RES_CODE", "1");
            ajaxData.put("X_RESULTCODE", "-1");
            ajaxData.put("X_RESULTINFO", resultDesc);
        }

        setInfo(infoData);
        setAjax(ajaxData);
    }

    /**
     * Check ResultInfo
     *
     * @param pageData
     * @return
     * @throws Exception
     */
    private String checkResultInfo(IData pageData) throws Exception {

        String productId = pageData.getString("PRODUCT_ID", "");
        String serialNumber = pageData.getString("SERIAL_NUMBER", "");
        String eparchyCode = getTradeEparchyCode();
        // 查询tradetypecode
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(productId, "P", BizCtrlType.CreateUser, "TradeTypeCode");
        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据" + productId + "没找到业务类型");
        }

        // 查询服务号码前缀
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, eparchyCode);
        IData preSnData = CSViewCall.callone(this, "SS.ProductInfoSVC.getGrpSnBySelectParam", param);
        String preSn = preSnData.getString("PRE_SN", "");// 服务号码前缀

        // 号码不能为空
        if (StringUtils.isEmpty(serialNumber)) {
            return "服务号码为空,校验失败,请输入集团编码!";
        }

        // 不能为手机号码: 130-188
        if ((serialNumber.substring(0, 2).equals("13") || serialNumber.subSequence(0, 3).equals("188")) && serialNumber.length() == 11) {
            return "服务号码请勿设置为手机号码!";
        }

        // 产品规则校验
        if (productId.equals("6200") && !serialNumber.substring(0, 3).equals(eparchyCode.substring(1, 4))) {
            return "服务号码前三位必需是所在地区编码的后三位[" + eparchyCode.substring(1, 4) + "]!";
        }

        // // VPN类产品规则校验
        // if ((productId.equals("8000") || productId.equals("8001") || productId.equals("8010") || productId.equals("8015")) && !serialNumber.substring(0, 4).equals(preSn))
        // {
        // return "服务号码前四位与配置表中代码[" + preSn + "]不一致,请修改!";
        // }

        // 号码位数限制
        if (productId.equals("6200") && serialNumber.length() != 10) {
            return "彩铃集团服务号码长度必须为10,请修改!";
        }
        if (productId.equals("7001") && serialNumber.length() != 7) {
            return "GPRS集团服务号码长度必须为7,请修改!";
        }
        if (productId.equals("7051") && serialNumber.length() != 10) {
            return "行业应用卡集团服务号码长度必须为10,请修改!";
        }
        // if (productId.equals("8000") && serialNumber.length() != 10)
        // {
        // return "智能网VPMN集团服务号码长度必须为10.请修改!";
        // }
        if (productId.equals("8001") && serialNumber.length() != 10) {
            return "VPMN(家庭集团)服务号码长度必须为10,请修改!";
        }
        if (productId.equals("8010") && serialNumber.length() != 10) {
            return "普通虚拟网集团服务号码长度必须为10,请修改!";
        }
        if (productId.equals("8015") && serialNumber.length() != 10) {
            return "VPCN(跨区)集团服务号码长度必须为10,请修改!";
        }
        if (productId.equals("9071") && serialNumber.length() != 10) {
            return "政企彩漫服务号码长度必须为10，请修改！";
        }
        if (productId.equals("9051")) {
            if (serialNumber.length() != 11 || !serialNumber.matches("^[0-9]+$")) {
                return "集团通讯录服务号码必须为11位数字，请修改！";
            }
            if (!"12553".equals(serialNumber.substring(0, 5)) || !serialNumber.substring(5, 7).equals(eparchyCode.substring(2, 4))) {
                return "集团通讯录服务号码前7位必须为12553" + eparchyCode.substring(2, 4) + "，请修改!";
            }

        }

        // 判断是否有相同的号码
        param.clear();
        param.put("SERIAL_NUMBER", serialNumber);
        IData userList = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put(Route.USER_EPARCHY_CODE, eparchyCode);
        IData tradeList = CSViewCall.callone(this, "CS.TradeInfoQrySVC.getMainTradeBySN", param);

        if (IDataUtil.isNotEmpty(userList) || IDataUtil.isNotEmpty(tradeList)) {
            return "该服务号码[" + serialNumber + "]已存在,请重新输入!";
        }

        return "";

    }

    public void queryEcAccountList(IRequestCycle cycle) throws Exception {

        // String value = getData().getString("VALUE", "");
        // String queryType = getData().getString("QUERY_TYPE", "");
        // if(StringUtils.isEmpty(value))
        // {
        // return;
        // }
        // IDataset accounts = new DatasetList();
        // try
        // {
        // if("1".equals(queryType)){
        // // 根据集团客户编码查询
        // IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, value,false);
        // accounts = UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this,groupInfo.getString("CUST_ID"));
        // }else if("2".equals(queryType)){
        // // 根据集团服务号码查询
        // IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, value);
        // IDataset defPayRelaInfos = UCAInfoIntfViewUtil.qryGrpValidPayRelaInfoByUserId(this, userInfo.getString("USER_ID"));
        //
        // if (IDataUtil.isNotEmpty(defPayRelaInfos))
        // {
        // for (int i = 0; i < defPayRelaInfos.size(); i++)
        // {
        // String acctId = defPayRelaInfos.getData(i).getString("ACCT_ID");
        // IData acctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId, false);
        // accounts.add(acctInfo);
        // }
        // }
        // }else if("3".equals(queryType)){
        // accounts = UCAInfoIntfViewUtil.qryGrpAcctInfoByContractNo(this, value, false);
        //
        // }
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        //
        // setEcAccountList(accounts);

        String custId = this.getData().getString("CUST_ID");
        IDataset accounts = UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custId);
        setEcAccountList(accounts);
    }

    private IDataset queryPower100Dataset(String subscriberInsId, String mainOfferId) throws Exception {
        IDataset childSubs = new DatasetList();
        String relationTypeCodeString = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, mainOfferId);
        IDataset insChildSubDataset = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this, subscriberInsId, relationTypeCodeString);

        if (DataUtils.isNotEmpty(insChildSubDataset)) {
            for (int i = 0, size = insChildSubDataset.size(); i < size; i++) {
                IData child = new DataMap();
                IData childSub = insChildSubDataset.getData(i);
                if (DataUtils.isEmpty(childSub)) {
                    continue;
                }

                IData temp = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, childSub.getString("USER_ID_B"));
                child.put("REMOVE_TAG", "0");
                child.put("USER_ID", temp.getString("USER_ID"));
                child.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                child.put("OFFER_CODE", temp.getString("PRODUCT_ID"));
                child.put("OPEN_DATE", temp.getString("OPEN_DATE"));
                child.put("SERIAL_NUMBER", temp.getString("SERIAL_NUMBER"));
                child.put("OFFER_ID", IUpcViewCall.getOfferIdByOfferCode(temp.getString("PRODUCT_ID")));
                child.put("SELECT_FLAG", queryOfferJoinSelectFlagByOfferIdAndRelOfferId(mainOfferId, temp.getString("PRODUCT_ID")));
                child.put("OFFER_NAME", temp.getString("PRODUCT_NAME"));
                child.put("OPER_CODE", "EXISTS"); // OPER_CODE鍒濆涓�-鍙樻洿
                childSubs.add(child);
            }
        }
        return childSubs;
    }

    /**
     * 判断当前产品是否为必选 0 默认必选 1 默认可选 2编辑可选 chenyi 2018-5-9
     * 
     * @return
     * @throws Exception
     */
    public String queryOfferJoinSelectFlagByOfferIdAndRelOfferId(String mainOfferId, String offerId) throws Exception {
        String selectFlag = "1";
        IDataset joinOfferInfo = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(mainOfferId, offerId, "4");
        if (IDataUtil.isNotEmpty(joinOfferInfo)) {
            selectFlag = joinOfferInfo.getData(0).getString("SELECT_FLAG");
        }
        return selectFlag;
    }

    /**
     * 初始化默认费用
     * 
     * @param tradeTypeCode
     *            业务类型编码
     * @param selectedElementList
     *            元素列表
     * @return
     * @throws Exception
     */
    public void initDefaultFee(IRequestCycle cycle) throws Exception {

        IDataset returnFeeList = new DatasetList();
        IDataset selectedElementList = new DatasetList();
        String tradeFeeType = "0";
        String offerid = getData().getString("OFFER_ID");
        String productId = IUpcViewCall.getOfferCodeByOfferId(offerid);
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(productId, "P", BizCtrlType.CreateUser, "TradeTypeCode");
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("TRADE_FEE_TYPE", tradeFeeType);
        param.put("EPARCHY_CODE", getTradeEparchyCode());

        if ("0".equals(tradeFeeType)) // 处理产品收费
        {
            param.put("ELEMENT_TYPE_CODE", "P");
            param.put("PACKAGE_ID", "-1");
            param.put("ELEMENT_ID", "-1");
        } else if ("4".equals(tradeFeeType)) // 处理元素收费
        {
            // 获取新增的服务或资费
            IDataset addServiceDiscntList = getAddServiceDiscntList(selectedElementList);
            param.put("SELECTED_ELEMENTS", addServiceDiscntList);
        }

        IDataset tradeFeeList = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.qryTradeTypeFeeForGrp", param);

        if (IDataUtil.isEmpty(tradeFeeList)) {
            return;
        }

        //

        // 遍历费用
        for (int j = 0, jRow = tradeFeeList.size(); j < jRow; j++) {
            IData tradeFeeData = tradeFeeList.getData(j);

            if (IDataUtil.isEmpty(tradeFeeData)) {
                continue;
            }
            String ruleBizTypeCode = tradeFeeData.getString("RULE_BIZ_KIND_CODE", "-1");

            if ("-1".equals(ruleBizTypeCode)) {
                IData feeData = new DataMap();
                feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
                feeData.put("ELEMENT_ID", tradeFeeData.getString("ELEMENT_ID"));
                feeData.put("FEE", tradeFeeData.getString("FEE", "0"));
                feeData.put("FEE_MODE", tradeFeeData.getString("FEE_MODE", ""));
                feeData.put("FEE_TYPE_CODE", tradeFeeData.getString("FEE_TYPE_CODE", ""));
                feeData.put("PAY_MODE", tradeFeeData.getString("PAY_MODE", ""));
                feeData.put("IN_DEPOSIT_CODE", tradeFeeData.getString("IN_DEPOSIT_CODE", ""));
                feeData.put("OUT_DEPOSIT_CODE", tradeFeeData.getString("OUT_DEPOSIT_CODE", ""));

                // 处理费用信息
                setGroupFeeList(feeData, returnFeeList);
            }
        }

        IData ajaxData = new DataMap();
        ajaxData.put("GRP_FEE_LIST", returnFeeList);
        this.setAjax(ajaxData);
    }

    /**
     * 从后台获取静态参数页面配置
     *
     * @param cycle
     * @throws Exception
     */
    public void queryProdParam(IRequestCycle cycle) throws Exception {
        String prodSpecId = getData().getString("OFFER_ID", "-1");// 成员商品
        IData param = new DataMap();
        IData offer = IUpcViewCall.getOfferInfoByOfferId(prodSpecId);
        String offerCode = offer.getString("OFFER_CODE");
        String offerType = offer.getString("OFFER_TYPE");
        String operType = getData().getString("OPER_TYPE", "");

        if ("P".equals(offerType)) {
            String value = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, offerType, operType, "dynamicPage");

            if (StringUtils.isNotEmpty(value)) {
                param = new DataMap(value);
            }
        }

        setAjax(param);
    }

    /**
     * 将费用信息inFeeData添加到费用列表feeList中
     * 
     * @param inFeeData
     * @param feeList
     * @throws Exception
     */
    private void setGroupFeeList(IData inFeeData, IDataset feeList) throws Exception {
        String inTradeTypeCode = inFeeData.getString("TRADE_TYPE_CODE"); // 业务类型
        String inElementId = inFeeData.getString("ELEMENT_ID"); // 元素ID
        String inFeeMode = inFeeData.getString("FEE_MODE");
        String inFeeTypeCode = inFeeData.getString("FEE_TYPE_CODE");// 157ADCMAS彩短信预存TD_B_PAYMENT表
        String inFactPayFee = inFeeData.getString("FEE", "0");// 应缴金额
        String inFee = inFeeData.getString("FEE", "0");// 实缴金额

        boolean isExist = false;// 费用项是否存在

        for (int i = 0, row = feeList.size(); i < row; i++) {
            IData feeData = feeList.getData(i);

            if (inFeeMode.equals(feeData.getString("FEE_MODE")) && inFeeTypeCode.equals(feeData.getString("FEE_TYPE_CODE"))) {
                int fee = Integer.parseInt(inFee) + Integer.parseInt(feeData.getString("FEE", "0"));
                int factPayFee = Integer.parseInt(inFactPayFee) + Integer.parseInt(feeData.getString("FACT_PAY_FEE", "0"));

                feeData.put("FEE", String.valueOf(fee));
                feeData.put("FACT_PAY_FEE", String.valueOf(factPayFee));

                isExist = true;
            }
        }

        if (!isExist) {
            IData feeData = new DataMap();
            feeData.put("TRADE_TYPE_CODE", inTradeTypeCode);
            if (!StringUtils.isEmpty(inElementId)) {
                feeData.put("ELEMENT_ID", inElementId);
            }
            feeData.put("FEE_MODE", inFeeMode);
            feeData.put("FEE_TYPE_CODE", inFeeTypeCode);
            feeData.put("FACT_PAY_FEE", inFactPayFee); // 应缴金额
            feeData.put("FEE", inFee); // 实缴金额
            feeList.add(feeData);
        }
    }

    /**
     * 从元素列表selectedElementList获取新增的服务和资费信息
     * 
     * @param selectedElementList
     * @return
     * @throws Exception
     */
    public IDataset getAddServiceDiscntList(IDataset selectedElementList) throws Exception {
        IDataset addElementList = new DatasetList();

        if (IDataUtil.isEmpty(selectedElementList)) {
            return addElementList;
        }

        for (int i = 0, size = selectedElementList.size(); i < size; i++) {
            IData elementData = selectedElementList.getData(i);
            if (("S".equals(elementData.getString("ELEMENT_TYPE_CODE", "")) || "D".equals(elementData.getString("ELEMENT_TYPE_CODE", ""))) && "0".equals(elementData.getString("MODIFY_TAG", ""))) {
                addElementList.add(elementData);
            }
        }

        return addElementList;
    }

    public void forbidCrmOrder(String offerCode) throws Exception {
        if ("7343".equals(offerCode) || "7344".equals(offerCode) || "7342".equals(offerCode)) {
            CSViewException.apperr(GrpException.CRM_GRP_909);
        }
    }

    /**
     * 查询稽核信息
     *
     * @param cycle
     * @throws Exception
     */
    public void queryAuditInfo(IRequestCycle cycle) throws Exception {
        String staffId = this.getData().getString("STAFF_ID", "");
        String staffName = this.getData().getString("STAFF_NAME", "");

        String myStaffId = this.getVisit().getStaffId();

        IDataset staffs = CommonViewCall.qryAuditInfo(staffId, staffName);
        // 过滤掉自己
        for (int i = 0; i < staffs.size(); i++) {
            IData each = staffs.getData(i);
            if (myStaffId.equals(each.getString("STAFF_ID", ""))) {
                staffs.remove(i);
                i--;
            }
        }
        setAuditInfoList(staffs);
    }

    public String queryRechangeUserList(String productId, String custId) throws Exception {
        // 设置集团客户的统付用户信息
        String userId = "";
        IDataset userinfos = new DatasetList();
        IDataset staticInfoList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID", new String[] { "TYPE_ID", "DATA_ID" }, new String[] { "PAYSYSTEM", productId });
        IData para = new DataMap();
        para.put("CUST_ID", custId);
        if (IDataUtil.isNotEmpty(staticInfoList)) {
            for (int i = 0; i < staticInfoList.size(); i++) {
                IData staticInfo = staticInfoList.getData(i);
                para.put("PRODUCT_ID", staticInfo.getString("DATA_ID"));
                userinfos.addAll(CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", para));
            }
            // 设置默认选中
            if (IDataUtil.isNotEmpty(userinfos)) {
                for (int j = 0; j < userinfos.size(); j++) {
                    // String oldUserId = userinfos.getData(j).getString("USER_ID");
                    // IData userParam = new DataMap();
                    // userParam.put("USER_ID", oldUserId);
                    // IDataset userDiscntList = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.getDiscntByUserIdAndDiscntId", userParam);
                    // if (IDataUtil.isNotEmpty(userDiscntList)) {
                    // userId = oldUserId;
                    // break;
                    // }
                }
                if (StringUtils.isBlank(userId)) {
                    userId = userinfos.getData(0).getString("USER_ID");
                }
            }
            setReChangeUsers(userinfos);
        }

        return userId;
    }

    private void initCreateForEsop(IData inOfferData, IData initOffer, IData info) throws Exception {
        String offerCode = inOfferData.getString("OFFER_CODE");
        String offerId = inOfferData.getString("OFFER_ID");
        String groupId = info.getString("GROUP_ID");

        // 拼装ESOP信息
        IData offerData = buildEsopOfferData(inOfferData);
        initOffer.put("OPER_TYPE", EcConstants.FLOW_ID_EC_CREATE);

        // 服务号码设置
        IData ecAccessNumInfo = new DataMap();
        if (StringUtils.isNotBlank(inOfferData.getString("SERIAL_NUMBER"))) {
            ecAccessNumInfo = getSerialNumberForEsopEbuop(groupId, offerCode, getTradeEparchyCode(), inOfferData.getString("SERIAL_NUMBER"));
        }
        if (DataUtils.isEmpty(ecAccessNumInfo)) {
            ecAccessNumInfo = getSerialNumber(groupId, offerCode, getTradeEparchyCode());
        }
        initOffer.put("SERIAL_NUMBER", ecAccessNumInfo.getString("SERIAL_NUMBER"));
        initOffer.put("ESOP_IF_RES_CODE", ecAccessNumInfo.getString("IF_RES_CODE"));
        initOffer.put("RES_TYPE_CODE", ecAccessNumInfo.getString("RES_TYPE_CODE"));
        initOffer.put("SERIAL_NUMBER_SUCCESS", "false");
        if ("0".equals(ecAccessNumInfo.getString("IF_RES_CODE")))// 如果IF_RES_CODE=0，服务号码框Disabled
        {
            // 不需要校验服务号码
            initOffer.put("SERIAL_NUMBER_SUCCESS", "true");
        }

        initOffer.put("ESOP_OFFER_DATA", offerData);

        // 其他信息初始化
        IData tem = new DataMap();
        tem.put("ID", offerCode);
        tem.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        // 集团付费计划定制
        IDataset templates = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.showTemplate", tem);

        if (IDataUtil.isEmpty(templates)) {
            info.put("PAYFORMEB_TAG", "P");
            info.put("PAYFORMEB_CHOICEDISABLE", "true");
            info.put("PLAN_CHOICEDISABLE", "true");
        } else {
            String payFeeModeCode = templates.getData(0).getString("ATTR_VALUE", "*");
            // 判断权限
            boolean isPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "GRP_PAYPLAN_EDIT");
            // 如果付费方式为集团定制,并且有权限

            if ("C".equals(payFeeModeCode) && isPriv) {
                info.put("PAYFORMEB_TAG", "C");
                info.put("PAYFORMEB_CHOICEDISABLE", "true");
                info.put("PLAN_CHOICEDISABLE", "false");

            } else {
                info.put("PAYFORMEB_CHOICEDISABLE", "false");
                info.put("PLAN_CHOICEDISABLE", "true");
            }
        }
        // 是否可以选择合户
        templates = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.CanSameAcct", tem);

        if (IDataUtil.isEmpty(templates)) {// 查不到数据，不支持合户
            info.put("SHOW_ACCT_COMBINE", false);
        }

        // 查询用户群名称是否必填
        templates = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.CanInserUserName", tem);
        if (IDataUtil.isEmpty(templates)) {
            info.put("USERNAME_CANNULL", "yes");
        } else {
            info.put("USERNAME_CANNULL", "no");
        }
        IDataset acctDealList = StaticUtil.getStaticList("SAME_ACCT");
        info.put("ACCT_DEAL_LIST", acctDealList);
        info.put("ACCT_DEAL", "0"); // 设置默认账户操作为新增

        // 用户类别
        IDataset userDiffCodeList = StaticUtil.getStaticList("USER_DIFF_CODE");
        userDiffCodeList.removeAll(DataHelper.filter(userDiffCodeList, "DATA_ID=0")); // 出掉0.普通个人类型
        info.put("USER_DIFFCODE_LIST", userDiffCodeList);

        // 集团定制
        String useTag = UpcViewCall.getUseTagByProductId(this, offerCode);
        if ("1".equals(useTag)) {
            // 定制初始化
            IDataset userGrpPackageList = queryEcPackages(offerCode, "");
            info.put("USER_PACKAGES", userGrpPackageList);
            info.put("USE_TAG", useTag);
            offerData.put("GRP_PACKAGE_INFO", userGrpPackageList);
        }

    }

    /* esop新流程客户经理提单的时候会生成集团服务号码，集团商品受理时默认取之 */
    public IData getSerialNumberForEsopEbuop(String groupId, String productId, String grpUserEparchyCode, String serialNumber) throws Exception {
        if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(productId) || StringUtils.isEmpty(serialNumber)) {
            return null;
        }

        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);

        // 之所以再调用SS.ProductInfoSVC.genGrpSn是为了获取IF_RES_CODE（该值为0或1）
        IData grpSnData = CSViewCall.callone(this, "SS.ProductInfoSVC.genGrpSn", param);

        String resTypeCode = "G";
        String ifResCode = grpSnData.getString("IF_RES_CODE", "");

        if (StringUtils.isNotEmpty(serialNumber) && "8070".equals(productId) && ("0").equals(ifResCode)) {
            resTypeCode = "T";
        }
        // 服务号码信息
        IData infoData = new DataMap();
        infoData.put("SERIAL_NUMBER", serialNumber);
        infoData.put("RES_TYPE_CODE", resTypeCode);
        infoData.put("IF_RES_CODE", ifResCode);
        return infoData;
    }

    /**
     * 把商品订购 的子产品、商品资费，服务 拼到第一层SUBOFFERS liaolc 2018-3-6
     */
    public void buildIsExistOfferInSubOffers(IDataset isExistOfferList, IDataset subOfferList, IData offerData) throws Exception {
        if (IDataUtil.isNotEmpty(isExistOfferList)) {
            for (int i = 0, size = isExistOfferList.size(); i < size; i++) {
                IData isExistOfferData = isExistOfferList.getData(i);
                String isExistOfferId = isExistOfferData.getString("OFFER_ID");
                String isExistOfferInsId = isExistOfferData.getString("OFFER_INS_ID");
                String isExistUserId = isExistOfferData.getString("USER_ID");
                boolean isExist = false;
                if (IDataUtil.isNotEmpty(subOfferList)) {
                    for (int j = 0, sizeJ = subOfferList.size(); j < sizeJ; j++) {
                        IData temp = subOfferList.getData(j);
                        String inOfferId = temp.getString("OFFER_ID");
                        if (isExistOfferId.equals(inOfferId)) {
                            // 给EOP过来的数据补字段信息
                            temp.put("USER_ID", isExistUserId);
                            temp.put("OFFER_INS_ID", isExistOfferInsId);
                            isExist = true;
                            break;
                        }
                    }
                }
                if (!isExist)// 产品受理时订购过的子产品、商品资费，服务在ESOP的SUBOFFERS信息里不存在,拼入SUBOFFERS
                {// isExistOfferList->subOfferList
                    if (IDataUtil.isNotEmpty(isExistOfferList)) {
                        for (int j = 0, sizeJ = isExistOfferList.size(); j < sizeJ; j++) {
                            IData isExistsubOffer = isExistOfferList.getData(j);
                            String subOfferId = isExistsubOffer.getString("OFFER_ID");

                            // 查询OFFER信息
                            IData offer = IUpcViewCall.queryOfferByOfferId(subOfferId);
                            if (DataUtils.isEmpty(offer)) {
                                continue;
                            }
                            String subOfferCode = offer.getString("OFFER_CODE");
                            String subOfferType = offer.getString("OFFER_TYPE");
                            String userId = isExistsubOffer.getString("USER_ID", "");
                            String relaInstId = isExistsubOffer.getString("OFFER_INS_ID", "");

                            IData subOfferData = new DataMap();
                            subOfferData.put("OFFER_ID", offer.getString("OFFER_ID"));
                            subOfferData.put("OFFER_CODE", subOfferCode);
                            subOfferData.put("OFFER_TYPE", subOfferType);
                            subOfferData.put("OPER_CODE", PageDataTrans.ACTION_EXITS);
                            subOfferData.put("GROUP_ID", isExistsubOffer.getString("GROUP_ID", "-1")); // 构成或子产品组为-1
                            subOfferData.put("OFFER_NAME", isExistsubOffer.getString("OFFER_NAME"));
                            subOfferData.put("START_DATE", isExistsubOffer.getString("START_DATE", SysDateMgr4Web.getSysTime()));
                            subOfferData.put("END_DATE", isExistsubOffer.getString("END_DATE", TimeUtil.EXPIRE_DATE));
                            subOfferData.put("USER_ID", userId);
                            subOfferData.put("OFFER_INS_ID", relaInstId);

                            IData data = new DataMap();
                            data.put("USER_ID", userId);
                            data.put("INST_TYPE", subOfferType);
                            data.put("INST_ID", relaInstId);
                            data.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                            IDataset userAttrList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserAttrByUserIdInstid", data);
                            IDataset OfferChaSpecsList = buildOfferChaSpecsFromAttrList(userAttrList);
                            if (IDataUtil.isNotEmpty(OfferChaSpecsList)) {
                                subOfferData.put("OFFER_CHA_SPECS", OfferChaSpecsList);// 资料中的产品参数，资费参数，服务参数
                            }

                            if (UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(subOfferType)) {
                                subOfferData.put("BRAND_CODE", UpcViewCall.queryBrandCodeByOfferCodeAndType(this, subOfferCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT));
                            }

                            subOfferList.add(subOfferData);
                        }
                    }
                }
            }
        }
    }

    private IDataset buildOfferChaSpecsFromAttrList(IDataset userAttrList) throws Exception {
        IDataset offerChaSpecs = new DatasetList();
        if (IDataUtil.isNotEmpty(userAttrList)) {
            for (int i = 0, size = userAttrList.size(); i < size; i++) {
                IData offerCha = new DataMap();
                offerCha.put("ATTR_CODE", userAttrList.getData(i).getString("ATTR_CODE"));
                offerCha.put("ATTR_VALUE", userAttrList.getData(i).getString("ATTR_VALUE"));
                offerCha.put("ATTR_NAME", userAttrList.getData(i).getString("ATTR_NAME"));
                offerChaSpecs.add(offerCha);
            }
        }
        return offerChaSpecs;
    }

    public abstract void setInitOffer(IData initOffer);

    public abstract void setInfo(IData info);

    public abstract void setServiceOfferList(IDataset serviceOfferList);

    public abstract void setPriceOfferList(IDataset priceOfferList);

    public abstract void setInAttr(IData inAttr);

    public abstract void setBusi(IData busi);

    public abstract void setEcAccountList(IDataset ecAccountList);

    public abstract void setEcPackages(IDataset ecPackages);

    public abstract void setForcePkgList(IDataset forcePkgList);

    public abstract void setInstOffers(IDataset instOffers);

    public abstract void setPower100Subs(IDataset power100Subs);

    public abstract void setAuditInfoList(IDataset auditList);

    public abstract void setReChangeUsers(IDataset reChangeUsers);

    /**
     * Check DEPART_CODE
     *
     * @param pageData
     * @throws Exception
     * @author chenhh6
     */
    public void checkDepartCode(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        String resultDesc = CommonViewCall.getDepart(pageData.getString("DEPART_ID", ""));
        IData ajaxData = new DataMap();
        if ("true".equals(resultDesc)) {// Validate Pass
            ajaxData.put("X_RESULTCODE", "0");
            ajaxData.put("X_RESULTINFO", "代理商校验成功。");
        } else {// Validate Failed
            ajaxData.put("X_RESULTCODE", "-1");
            ajaxData.put("X_RESULTINFO", "代理商校验失败，该代理商不存在，请输入正确的代理商编号。");
        }
        setAjax(ajaxData);
    }
    
    /*
     * REQ201909170024  关于企业宽带5折以下价格配置权限的开发需求 
     * guonj@
     * 2019-10-12
     */
    public void isPrivFiveDiscount(IRequestCycle cycle) throws Exception
    {
    	/*判断工号是否有套餐折扣权限*/
		String tradeStaffId = getVisit().getStaffId();
		IData data = getData();
    	data.put("X_RESULTCODE", "0");
    	if (StaffPrivUtil.isPriv(tradeStaffId, "PRIV_BROADBAND_DISCOUNT", "1")) {
    		data.put("X_RESULTCODE", "00");
    	}
        setAjax(data);
    }

    
    private IData checkFee(String custId) throws Exception {
    	String status = "true";
    	String msg = "";
    	IData results = new DataMap();
    	
    	IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "1066");
        iparam.put("PARAM_CODE", "VERIFY_FEE");
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset staticInfo = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        
		String isVerify = "";
		if (IDataUtil.isNotEmpty(staticInfo)){
			isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();// 是否进行验证,返回1为验证,0不验证
		}
		if (!"1".equals(isVerify)){
			results.put("STATE", status);
			results.put("MSG", msg);
			return results;
		}
		
		//免限制长期欠费集团订购产品权限
		if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_CHECKFEE")) {
			results.put("STATE", status);
			results.put("MSG", msg);
			return results;
		}
    	
    	
    	IData map = new DataMap();
    	map.put("CUST_ID", custId);
        IDataset idata = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", map);
    	if (IDataUtil.isNotEmpty(idata)) {
    		for (int i = 0; i < idata.size(); i++) {
    			IData data = idata.getData(i);
    			String acctId = data.getString("ACCT_ID");
    			String payModeCode = data.getString("PAY_MODE_CODE");
    			//调用账务接口
    			IData inparam = new DataMap();
    	        inparam.put("ACCT_ID", acctId);
    	        IDataset acctData = CSViewCall.call(this, "CS.AcctInfoQrySVC.checkFee", inparam);
    	        //需要对账务数据进行处理
    	        System.out.println("chenhh==acctData:"+acctData);
    			if (IDataUtil.isNotEmpty(acctData)) {
    				String minOweMouth = acctData.getData(0).getString("MIN_OWE_MONTH");	//账务返回最小账单月：201904
    				String Owefee = acctData.getData(0).getString("OWE_FEE");				//欠费金额
    				if (StringUtils.isNotEmpty(minOweMouth)) {
    					if (!checkMonth(minOweMouth) && StringUtils.isNotEmpty(Owefee)) {
    						double OwefeeD = Double.parseDouble(Owefee) * 0.01;
    						DecimalFormat df = new DecimalFormat("0.00"); 
    						String OwefeeS = df.format(OwefeeD);
    						status = "false";
    						msg = "该集团账户已欠费超过3个月，集团账户共计欠费" + OwefeeS + "元，请缴费后再办理业务";
						}
					}
				}else {
					status = "false";
					msg = "调用账务接口失败。";
				}
    		}
		}
        
    	
    	results.put("STATE", status);
		results.put("MSG", msg);
    	return results;
	}
    
    private boolean checkMonth(String minOweMouth){
    	boolean state = true;
    	Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String nowMonth = sdf.format(date);
		int minOweMouthY = Integer.parseInt(minOweMouth.substring(0, 4));
		int minOweMouthM = Integer.parseInt(minOweMouth.substring(4, 6));
		int nowMonthY = Integer.parseInt(nowMonth.substring(0, 4));
		int nowMonthM = Integer.parseInt(nowMonth.substring(4, 6));
		int sad = (nowMonthY - minOweMouthY)*12 + (nowMonthM - minOweMouthM);//月份差 = （第二年份-第一年份）*12 + 第二月份 -第一月份 ；
		System.out.println("总共欠费月份："+sad);
		if (sad > 3) {
			state = false;
		}
    	return state;
    }
}
