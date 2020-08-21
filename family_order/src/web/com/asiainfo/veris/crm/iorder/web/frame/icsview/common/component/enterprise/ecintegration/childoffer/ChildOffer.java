package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.ecintegration.childoffer;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.FrontProdConverter;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.payplanedit.PayPlanEditView;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class ChildOffer extends BizTempComponent {
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/ecintegration/childoffer/ChildOffer.js";

        if (isAjax) {
            includeScript(writer, jsFile, false, false);
        } else {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }

        String action = getPage().getData().getString("ACTION");
        if ("queryOfferForCreate".equals(action)) {
            queryOfferForCreate();
        } else if ("queryOfferEspgForCreate".equals(action)) {
            queryOfferEspgForCreate();
        }
        // else if("queryBbossMebOfferForCreate".equals(action))
        // {
        // queryBbossMebOfferForCreate();
        // }
        else if ("checkSerialNumber".equals(action)) {
            checkSerialNumber();
        } else if ("queryOfferForAddMeb".equals(action)) {
            queryOfferForAddMeb();
        } else if ("queryOfferEspgForAddMeb".equals(action)) {
            queryOfferForEspgAddMeb();
        } else if ("qryOrderStaffinfo".equals(action)) {
            qryOrderStaffinfo();
        }

    }

    /**
     ** ESP产品变更
     * 
     * @throws Exception
     * @Date 2019年11月1日
     * @author xieqj
     */
    public void queryOfferForEspgAddMeb() throws Exception {
        IData ecOffer = queryEcInsOffer();
        String bpmTempletId = getPage().getData().getString("BPM_TEMPLET_ID");
        String serialNumber = getPage().getData().getString("SERIAL_NUMBER");
        ecOffer.put("BPM_TEMPLET_ID", bpmTempletId);
        ecOffer.put("SERIAL_NUMBER", serialNumber);
        IData offerInfo = IUpcViewCall.queryOfferByOfferId(ecOffer.getString("OFFER_ID"), UpcConst.QUERY_COM_CHA_YES);
        if (DataUtils.isEmpty(offerInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID" + ecOffer.getString("OFFER_ID") + "没有查询到商品信息！");
        }
        ecOffer.put("OFFER_NAME", offerInfo.getString("OFFER_NAME"));
        ecOffer.put("OFFER_TYPE", offerInfo.getString("OFFER_TYPE"));
        ecOffer.put("BRAND_CODE", offerInfo.getString("BRAND_CODE"));
        setEcOffer(ecOffer);

        IDataset ecInsSubOfferList = ecOffer.getDataset("SUBOFFERS");
        if (IDataUtil.isNotEmpty(ecInsSubOfferList)) {
            setEcSubOfferList(ecInsSubOfferList);
        }

        IData info = new DataMap();

        String childOfferOperType = getPage().getData().getString("CHILD_OFFER_OPER_TYPE");
        info.put("CHILD_OFFER_OPER_TYPE", childOfferOperType); // 子商品操作类型

        IDataset childDelmebData = new DatasetList(getPage().getData().getString("CHILD_DELMEB_DATA"));
        info.put("CHILD_DELMEB_DATA", childDelmebData); // 已删除的成员

        // 服务号码
        info.putAll(getSerialNumber());
        if ("380300".equals(ecOffer.getString("OFFER_CODE")) || "380700".equals(ecOffer.getString("OFFER_CODE"))) {
            // 显示子群组集团商品信息的特定区域
            info.put("HAS_INS_ESPG_OFFER", true);
            info.put("SHOW_MEB_LIST", true);

            // 获取成员导入配置
            String ecOfferCode = ecOffer.getString("OFFER_CODE");
            IData importConfig = getMemberImportConfig(ecOfferCode);
            info.putAll(importConfig);
        } else {
            // 显示子群组集团商品信息的特定区域
            info.put("HAS_INS_ESPG_OFFER", true);
        }

        setInfo(info);
    }

    public void queryOfferForAddMeb() throws Exception {
        IData ecInsOffer = queryEcInsOffer();
        setEcOffer(ecInsOffer);

        IDataset ecInsSubOfferList = ecInsOffer.getDataset("SUBOFFERS");
        if (IDataUtil.isNotEmpty(ecInsSubOfferList)) {
            setEcSubOfferList(ecInsSubOfferList);
        }

        IData info = new DataMap();

        String childOfferOperType = getPage().getData().getString("CHILD_OFFER_OPER_TYPE");
        info.put("CHILD_OFFER_OPER_TYPE", childOfferOperType); // 子商品操作类型

        IDataset childDelmebData = new DatasetList(getPage().getData().getString("CHILD_DELMEB_DATA"));
        info.put("CHILD_DELMEB_DATA", childDelmebData); // 已删除的成员

        IData mebOffer = queryMebOffer(false);
        if (IDataUtil.isNotEmpty(mebOffer)) {
            setMebOffer(mebOffer);

            IDataset mebSubOfferList = mebOffer.getDataset("SUBOFFERS");
            if (IDataUtil.isNotEmpty(mebSubOfferList)) {
                setMebSubOfferList(mebSubOfferList);
            }

            info.put("HAS_EC_OFFER", false);
            info.put("HAS_EC_INS_OFFER", true);

            String useTag = IUpcViewCall.getUseTagByProductId(ecInsOffer.getString("OFFER_CODE"));
            if ("1".equals(useTag)) {
                info.put("ONLY_SHOW_DZ_OFFER", true);
                // 查询集团定制
                setGrpPackageList(queryEcPackages(ecInsOffer.getString("USER_ID")));
            }

            String relationTypeCode = IUpcViewCall.getRelationTypeCodeByOfferId(ecInsOffer.getString("OFFER_ID"));
            // 设置成员角色
            IDataset roleList = queryRoleList(relationTypeCode, ecInsOffer.getString("OFFER_ID"));
            info.put("ROLE_INFO", roleList);

            IData mebMustSelGroupOfferData = buildSelMustGroupOffersData(mebOffer.getString("OFFER_ID"), new DatasetList());
            info.put("MebMustSelGroupOfferData", mebMustSelGroupOfferData);

            info.put("HAS_MEB_OFFER", "DelMeb".equals(childOfferOperType) ? false : true);

            IData importConfig = getMemberImportConfig(ecInsOffer.getString("OFFER_CODE"));
            info.putAll(importConfig);
        } else {
            info.put("HAS_MEB_OFFER", false);
        }

        // 设置付费方式
        IDataset payPlans = queryPayplansByUserIdForMeb(ecInsOffer.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(payPlans)) {
            for (Object object : payPlans) {
                IData payInfo = (IData) object;
                payInfo.put("PAY_PLAN_CODE", payInfo.getString("PAY_TYPE_CODE"));
            }
        }
        info.put("MEB_PAY_PLAN_LIST", payPlans);

        setInfo(info);
    }

    public void queryOfferForCreate() throws Exception {
        IData ecOffer = queryEcOffer();
        setEcOffer(ecOffer);

        IDataset ecSubOfferList = ecOffer.getDataset("SUBOFFERS");
        if (IDataUtil.isNotEmpty(ecSubOfferList)) {
            setEcSubOfferList(ecSubOfferList);
        }

        IData info = new DataMap();
        info.put("HAS_EC_OFFER", true);

        boolean hasOfferData = getPage().getData().getBoolean("HAS_OFFER_DATA", false);
        if (!hasOfferData) {// HAS_OFFER_DATA=true，表示已经设置过商品数据，不需要再生成服务号码
            info.putAll(getSerialNumber());
        }
        info.put("SHOW_SERIAL_NUMBER", true);

        IData mebOffer = queryMebOffer(false);
        if (IDataUtil.isNotEmpty(mebOffer)) {
            setMebOffer(mebOffer);

            String useTag = IUpcViewCall.getUseTagByProductId(ecOffer.getString("OFFER_CODE"));
            if ("0".equals(useTag)) {
                info.put("HAS_DZ_MEB_OFFER", false);
            } else {
                info.put("HAS_DZ_MEB_OFFER", true);
            }

            IDataset mebSubOfferList = mebOffer.getDataset("SUBOFFERS");
            if (IDataUtil.isNotEmpty(mebSubOfferList)) {
                setMebSubOfferList(mebSubOfferList);
            }

            if ("1".equals(useTag)) {// 构造定制信息数据结构
                setGrpPackageList(buildGrpPackageList(mebOffer));
            }

            String relationTypeCode = IUpcViewCall.getRelationTypeCodeByOfferId(ecOffer.getString("OFFER_ID"));
            // 设置成员角色
            IDataset roleList = queryRoleList(relationTypeCode, ecOffer.getString("OFFER_ID"));
            info.put("ROLE_INFO", roleList);

            IData mebMustSelGroupOfferData = buildSelMustGroupOffersData(mebOffer.getString("OFFER_ID"), new DatasetList());
            info.put("MebMustSelGroupOfferData", mebMustSelGroupOfferData);

            // 获取成员导入配置
            String ecOfferCode = ecOffer.getString("OFFER_CODE");
            IData importConfig = getMemberImportConfig(ecOfferCode);
            info.putAll(importConfig);

            info.put("HAS_MEB_OFFER", true);
            // info.put("SHOW_MEB_LIST", true);
            // info.put("ADD_MEMBER_TYPE_LIST", getMemberTypeList());
        } else {
            info.put("HAS_MEB_OFFER", false);
        }

        IData ecMustSelGroupOfferData = buildSelMustGroupOffersData(ecOffer.getString("OFFER_ID"), new DatasetList());
        info.put("EcMustSelGroupOfferData", ecMustSelGroupOfferData);

        // 设置账户信息
        info.put("ACCT_DEAL_LIST", getAcctDealList());
        info.put("ACCT_DEAL", "0"); // 设置默认账户操作为新增

        // 初始化付费类型
        info.put("PAY_PLAN_LIST", queryPayPlansByOfferID(ecOffer.getString("OFFER_ID"), null));

        setInfo(info);

        IData ajaxData = new DataMap();
        ajaxData.put("IF_RES_CODE", info.getString("IF_RES_CODE", ""));
        getPage().setAjax(ajaxData);
    }

    // TODO Auto-generated method stub
    private void queryOfferEspgForCreate() throws Exception {
        IData ecOffer = queryEcOffer();
        setEcOffer(ecOffer);

        IDataset ecSubOfferList = ecOffer.getDataset("SUBOFFERS");
        if (IDataUtil.isNotEmpty(ecSubOfferList)) {
            setEcSubOfferList(ecSubOfferList);
        }
        IData info = new DataMap();
        // 服务号码
        info.putAll(getSerialNumber());
        if ("380300".equals(ecOffer.getString("OFFER_CODE")) || "380700".equals(ecOffer.getString("OFFER_CODE"))) {
            // 显示子群组集团商品信息的特定区域
            info.put("HAS_ESPG_OFFER", true);
            info.put("SHOW_MEB_LIST", true);

            // 获取成员导入配置
            String ecOfferCode = ecOffer.getString("OFFER_CODE");
            IData importConfig = getMemberImportConfig(ecOfferCode);
            info.putAll(importConfig);
        } else {
            // 显示子群组集团商品信息的特定区域
            info.put("HAS_ESPG_OFFER", true);
        }

        setInfo(info);
    }

    // public void queryBbossEcOfferForCreate() throws Exception
    // {
    // IData ecOffer = queryEcOffer();
    // setEcOffer(ecOffer);
    //
    // IDataset ecSubOfferList = ecOffer.getDataset("SUBOFFERS");
    // if(IDataUtil.isNotEmpty(ecSubOfferList))
    // {
    // setEcSubOfferList(ecSubOfferList);
    // }
    //
    // IData info = new DataMap();
    // IData ecMustSelGroupOfferData = buildSelMustGroupOffersData(ecOffer.getString("OFFER_ID"), new DatasetList());
    // info.put("EcMustSelGroupOfferData", ecMustSelGroupOfferData);
    // info.put("HAS_MEB_OFFER", false);
    // info.put("HAS_EC_OFFER", true);
    // setInfo(info);
    // }

    // public void queryBbossMebOfferForCreate() throws Exception
    // {
    // IData mebOffer = queryMebOffer(true);
    // setMebOffer(mebOffer);
    //
    // IDataset mebSubOfferList = mebOffer.getDataset("SUBOFFERS");
    // if(IDataUtil.isNotEmpty(mebSubOfferList))
    // {
    // setMebSubOfferList(mebSubOfferList);
    // }
    //
    // IData info = new DataMap();
    // info.put("HAS_EC_OFFER", false);
    // info.put("HAS_MEB_OFFER", true);
    // info.put("SHOW_MEB_LIST", false);
    //
    // String ecProdOfferId = getPage().getData().getString("EC_PRODUCT_OFFER_ID");
    // String relationTypeCode = IUpcViewCall.getRelationTypeCodeByOfferId(ecProdOfferId);
    //
    // //设置成员角色
    // IDataset roleList = queryRoleList(relationTypeCode, ecProdOfferId);
    // info.put("ROLE_INFO", roleList);
    //
    // IData mebMustSelGroupOfferData = buildSelMustGroupOffersData(mebOffer.getString("OFFER_ID"), new DatasetList());
    // info.put("MebMustSelGroupOfferData", mebMustSelGroupOfferData);
    //
    // setInfo(info);
    // }

    /**
     * 校验服务号码
     * 
     * @throws Exception
     */
    public void checkSerialNumber() throws Exception {
        String productId = getPage().getData().getString("PRODUCT_ID");
        String serialNumber = getPage().getData().getString("SERIAL_NUMBER");

        String resultDesc = checkResultInfo(productId, serialNumber);

        IData ajaxData = new DataMap();
        ajaxData.put("SERIAL_NUMBER", serialNumber);
        ajaxData.put("RES_TYPE_CODE", getPage().getData().getString("RES_TYPE_CODE", ""));
        ajaxData.put("SHOW_SERIAL_NUMBER", true);

        if ("".equals(resultDesc)) // Validate Pass
        {
            ajaxData.put("IF_RES_CODE", "0");

            ajaxData.put("X_RESULTCODE", "0");
            ajaxData.put("X_RESULTINFO", "服务号码检验通过,录入的服务号码可以使用!");
        } else {// Validate Failed
            ajaxData.put("IF_RES_CODE", "1");
            ajaxData.put("X_RESULTCODE", "-1");
            ajaxData.put("X_RESULTINFO", resultDesc);
        }

        getPage().setAjax(ajaxData);
    }

    private IData queryEcOffer() throws Exception {
        String ecOfferId = getPage().getData().getString("OFFER_ID");
        String effectNow = getPage().getData().getString("EFFECT_NOW");

        IData offerInfo = IUpcViewCall.queryOfferByOfferId(ecOfferId, UpcConst.QUERY_COM_CHA_YES);
        if (DataUtils.isEmpty(offerInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID" + ecOfferId + "没有查询到商品信息！");
        }

        // 是否有产品属性
        boolean hasOfferCha = queryHasOfferCha(ecOfferId, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, BizCtrlType.CreateUser, offerInfo);

        // 是否有商品组
        boolean hasOfferGroup = queryHasOfferGroup(ecOfferId);

        IData ecOffer = new DataMap();
        ecOffer.put("OFFER_ID", offerInfo.getString("OFFER_ID"));
        ecOffer.put("OFFER_NAME", offerInfo.getString("OFFER_NAME"));
        ecOffer.put("OFFER_CODE", offerInfo.getString("OFFER_CODE"));
        ecOffer.put("OFFER_TYPE", offerInfo.getString("OFFER_TYPE"));
        ecOffer.put("BRAND_CODE", offerInfo.getString("BRAND_CODE"));
        ecOffer.put("OPER_CODE", "0");
        ecOffer.put("HAS_OFFER_CHA", hasOfferCha);
        ecOffer.put("HAS_OFFER_GROUP", hasOfferGroup);

        // 查询子商品构成关系（必选子商品）
        IDataset subOfferMustSelList = IUpcViewCall.queryOfferComRelOfferByOfferId(ecOfferId, getVisit().getLoginEparchyCode());
        if (IDataUtil.isNotEmpty(subOfferMustSelList)) {
            IDataset subOfferList = new DatasetList();
            for (int i = 0, size = subOfferMustSelList.size(); i < size; i++) {
                IData ecSubOffer = new DataMap();
                ecSubOffer.put("OFFER_ID", subOfferMustSelList.getData(i).getString("OFFER_ID"));
                ecSubOffer.put("OFFER_NAME", subOfferMustSelList.getData(i).getString("OFFER_NAME"));
                ecSubOffer.put("OFFER_CODE", subOfferMustSelList.getData(i).getString("OFFER_CODE"));
                ecSubOffer.put("OFFER_TYPE", subOfferMustSelList.getData(i).getString("OFFER_TYPE"));
                ecSubOffer.put("GROUP_ID", "-1");
                ecSubOffer.put("OPER_CODE", "0");
                ecSubOffer.put("SELECT_FLAG", UpcConst.SELECT_FLAG_MUST_CHOOSE);
                ecSubOffer.put("REL_OFFER_CODE", offerInfo.getString("OFFER_CODE"));
                ecSubOffer.put("HAS_OFFER_GROUP", false); // 普通产品只有两级关系

                boolean hasSubOfferCha = queryHasOfferCha(ecSubOffer.getString("OFFER_ID"), ecSubOffer.getString("OFFER_TYPE"), BizCtrlType.CreateUser, offerInfo);
                ecSubOffer.put("HAS_OFFER_CHA", hasSubOfferCha);

                // 计算元素的生失效时间
                IData eleDate = queryElementDate(ecOffer.getString("OFFER_ID"), ecSubOffer.getString("OFFER_ID"), ecSubOffer.getString("OFFER_CODE"), ecSubOffer.getString("OFFER_TYPE"), null, BizCtrlType.CreateUser, effectNow);
                ecSubOffer.putAll(eleDate);

                subOfferList.add(ecSubOffer);
            }
            ecOffer.put("SUBOFFERS", subOfferList);
        }

        // 根据协议初始化元素
        String ele = getPage().getData().getString("AGREEMENT_ELEMENT_LIST");
        if (StringUtils.isNotBlank(ele)) {// 设置之后再打开就不加载协议里面的元素了
            IData agreementElement = new DataMap(ele);
            if (IDataUtil.isNotEmpty(agreementElement) && IDataUtil.isNotEmpty(agreementElement.getDataset("EC_OFFER"))) {
                for (int i = 0; i < agreementElement.getDataset("EC_OFFER").size(); i++) {
                    IData element = agreementElement.getDataset("EC_OFFER").getData(i);
                    IDataset offerList = IUpcViewCall.queryChildOfferByGroupId(element.getString("GROUP_ID", ""), this.getVisit().getLoginEparchyCode());
                    if (IDataUtil.isNotEmpty(offerList)) {// 加载集团子商品时删除集团定制商品
                        for (int k = 0; k < offerList.size(); k++) {
                            IData offer = offerList.getData(k);
                            if (offer.getString("OFFER_ID").equals(element.getString("OFFER_ID")) && "P".equals(offer.getString("OFFER_TYPE"))) {// 去掉商品定制信息
                                agreementElement.getDataset("EC_OFFER").remove(element);
                                i--;
                            }
                        }
                    }
                }
                initSubOfferListByAgreement(ecOffer, agreementElement.getDataset("EC_OFFER"), BizCtrlType.CreateUser);
            }
        }

        return ecOffer;
    }

    private IData queryMebOffer(boolean isBboss) throws Exception {
        String ecOfferId = getPage().getData().getString("OFFER_ID");
        String effectNow = getPage().getData().getString("EFFECT_NOW");

        // 成员商品编码
        String mebOfferId = isBboss ? ecOfferId : IUpcViewCall.queryMemOfferIdByOfferId(ecOfferId);
        if (StringUtils.isBlank(mebOfferId)) {
            return null;
        }

        IData offerInfo = new DataMap();
        if (StringUtils.isNotBlank(mebOfferId)) {
            offerInfo = IUpcViewCall.queryOfferByOfferId(mebOfferId, UpcConst.QUERY_COM_CHA_YES);
            if (DataUtils.isEmpty(offerInfo)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID" + mebOfferId + "没有查询到商品信息！");
            }
        }

        // 是否有产品属性
        boolean hasOfferCha = queryHasOfferCha(mebOfferId, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, BizCtrlType.CreateMember, offerInfo);

        // 是否有商品组，互联网专线不展示商品组，直接绑定资费，因为专线资费已经收在融合商品上，专线成员不收费
        boolean hasOfferGroup = queryHasOfferGroup(mebOfferId);

        IData mebOffer = new DataMap();
        mebOffer.put("OFFER_ID", offerInfo.getString("OFFER_ID"));
        mebOffer.put("OFFER_NAME", offerInfo.getString("OFFER_NAME"));
        mebOffer.put("OFFER_CODE", offerInfo.getString("OFFER_CODE"));
        mebOffer.put("OFFER_TYPE", offerInfo.getString("OFFER_TYPE"));
        mebOffer.put("OPER_CODE", "0");
        mebOffer.put("HAS_OFFER_CHA", hasOfferCha);
        mebOffer.put("HAS_OFFER_GROUP", hasOfferGroup);

        // 查询子商品构成关系（必选子商品）
        IDataset subOfferMustSelList = IUpcViewCall.queryOfferComRelOfferByOfferId(mebOfferId, getVisit().getLoginEparchyCode());
        if (IDataUtil.isNotEmpty(subOfferMustSelList)) {
            IDataset subOfferList = new DatasetList();
            for (int i = 0, size = subOfferMustSelList.size(); i < size; i++) {
                IData mebSubOffer = new DataMap();
                mebSubOffer.put("OFFER_ID", subOfferMustSelList.getData(i).getString("OFFER_ID"));
                mebSubOffer.put("OFFER_NAME", subOfferMustSelList.getData(i).getString("OFFER_NAME"));
                mebSubOffer.put("OFFER_CODE", subOfferMustSelList.getData(i).getString("OFFER_CODE"));
                mebSubOffer.put("OFFER_TYPE", subOfferMustSelList.getData(i).getString("OFFER_TYPE"));
                mebSubOffer.put("OPER_CODE", "0");
                mebSubOffer.put("SELECT_FLAG", UpcConst.SELECT_FLAG_MUST_CHOOSE);
                mebSubOffer.put("REL_OFFER_CODE", offerInfo.getString("OFFER_CODE"));
                mebSubOffer.put("HAS_OFFER_GROUP", false); // 普通产品只有两级关系

                boolean hasSubOfferCha = queryHasOfferCha(mebSubOffer.getString("OFFER_ID"), mebSubOffer.getString("OFFER_TYPE"), BizCtrlType.CreateMember, offerInfo);
                mebSubOffer.put("HAS_OFFER_CHA", hasSubOfferCha);

                // 计算元素的生失效时间
                IData eleDate = queryElementDate(mebOffer.getString("OFFER_ID"), mebSubOffer.getString("OFFER_ID"), mebSubOffer.getString("OFFER_CODE"), mebSubOffer.getString("OFFER_TYPE"), null, BizCtrlType.CreateMember, effectNow);
                mebSubOffer.putAll(eleDate);

                subOfferList.add(mebSubOffer);
            }
            mebOffer.put("SUBOFFERS", subOfferList);
        }

        // 根据协议初始化元素
        String ele = getPage().getData().getString("AGREEMENT_ELEMENT_LIST");
        if (StringUtils.isNotBlank(ele)) {

            IData agreementElement = new DataMap(ele);
            if (IDataUtil.isNotEmpty(agreementElement) && IDataUtil.isNotEmpty(agreementElement.getDataset("MEB_OFFER"))) {
                initSubOfferListByAgreement(mebOffer, agreementElement.getDataset("MEB_OFFER"), BizCtrlType.CreateMember);
            }

        }

        return mebOffer;
    }

    /**
     * 查询集团商品实例
     * 
     * @return
     * @throws Exception
     */
    private IData queryEcInsOffer() throws Exception {
        IData param = this.getPage().getData();
        String offerId = param.getString("OFFER_ID");
        String ecUserId = param.getString("USER_ID");
        IData ecInsOfferData = new DataMap();
        if (StringUtils.isBlank(ecUserId)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "没有获取到集团用户ID！");
        }
        // IData grpUserProduct = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, ecUserId);
        ecInsOfferData.put("OFFER_ID", offerId);
        ecInsOfferData.put("OFFER_CODE", param.getString("OFFER_CODE"));
        ecInsOfferData.put("USER_ID", ecUserId);
        ecInsOfferData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));

        IData input = new DataMap();
        input.put("USER_ID", ecUserId);
        input.put("PRODUCT_ID", param.getString("OFFER_CODE"));
        input.put(Route.USER_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
        IDataset ecInsChildOfferList = CSViewCall.call(this, "CS.SelectedElementSVC.getGrpChildOffers", input);
        if (IDataUtil.isNotEmpty(ecInsChildOfferList)) {
            ecInsOfferData.put("SUBOFFERS", ecInsChildOfferList);
            ecInsOfferData.put("HAS_INS_CHILD_OFFER", true);
        }

        return ecInsOfferData;
    }

    /**
     * 查询是否有商品特征
     * 
     * @param offerId
     * @param operType
     * @return
     * @throws Exception
     */
    private boolean queryHasOfferCha(String offerId, String offerType, String operType, IData offerInfo) throws Exception {
        if ("BOSG".equals(offerInfo.getString("BRAND_CODE")) && UpcConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType)) {
            String offerCode = offerInfo.getString("OFFER_CODE");
            IData prodSpec = new DataMap();
            prodSpec.put("PROD_SPEC_ID", offerCode);
            prodSpec.put("OPER_TYPE", operType);
            prodSpec.put("MERCHP_OPER_TYPE", "1");
            // 暂时不需要转换产品编码
            prodSpec.put("MAIN_TAG", true);
            FrontProdConverter.prodConverter(this, prodSpec, false);
            operType = prodSpec.getString("OPER_TYPE");
            offerId = prodSpec.getString("PROD_SPEC_ID");
            if (StringUtils.isEmpty(offerId)) {
                return false;
            }
        }

        IData data = new DataMap();
        data.put("POINT_ONE", offerId);
        data.put("POINT_TWO", operType);
        IDataset result = CSViewCall.call(this, "SS.PointInfoSVC.queryPoint", data);
        boolean hasOfferCha = IDataUtil.isNotEmpty(result) ? true : false;
        if (!hasOfferCha && UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType)) {
            hasOfferCha = IDataUtil.isNotEmpty(IUpcViewCall.queryChaByOfferId(offerId)) ? true : false;
        }
        return hasOfferCha;
    }

    /**
     * 查询是否有商品组
     * 
     * @param offerId
     * @return
     * @throws Exception
     */
    private boolean queryHasOfferGroup(String offerId) throws Exception {
        return IDataUtil.isNotEmpty(IUpcViewCall.queryOfferGroups(offerId, getVisit().getLoginEparchyCode())) ? true : false;
    }

    /**
     * 构建必选商品组信息以及选择的商品信息
     * 
     * @param mainOfferId
     * @param subOfferList
     * @return
     * @throws Exception
     */
    private IData buildSelMustGroupOffersData(String mainOfferId, IDataset subOfferList) throws Exception {
        IData selGroupData = new DataMap();

        // if("110000508301".equals(mainOfferId))
        // {//互联网专线成员产品不校验商品组，必选组资费已绑定
        // return new DataMap();
        // }

        IDataset groupList = IUpcViewCall.queryOfferGroups(mainOfferId, getVisit().getLoginEparchyCode());
        if (DataUtils.isNotEmpty(groupList)) {
            for (int i = 0, sizeI = groupList.size(); i < sizeI; i++) {
                IData group = groupList.getData(i);
                if (!UpcConst.SELECT_FLAG_MUST_CHOOSE.equals(group.getString("SELECT_FLAG"))) {
                    continue;
                }
                IData groupData = new DataMap();
                groupData.put("GROUP_ID", group.getString("GROUP_ID"));
                groupData.put("GROUP_NAME", group.getString("GROUP_NAME"));
                groupData.put("MAX_NUM", group.getString("MAX_NUM"));
                groupData.put("MIN_NUM", group.getString("MIN_NUM"));
                groupData.put("SELECT_FLAG", UpcConst.SELECT_FLAG_MUST_CHOOSE);
                groupData.put("LIMIT_TYPE", group.getString("LIMIT_TYPE"));

                StringBuilder mustSelOfferSB = new StringBuilder(500);
                IDataset selOfferList = new DatasetList();
                IDataset groupOfferList = UpcViewCall.queryGroupComRelOffer(this, group.getString("GROUP_ID"));
                for (int j = 0, sizeJ = groupOfferList.size(); j < sizeJ; j++) {
                    IData groupOffer = groupOfferList.getData(j);
                    String groupOfferId = groupOffer.getString("OFFER_ID");
                    String selectFlag = groupOffer.getString("SELECT_FLAG");
                    if (UpcConst.SELECT_FLAG_MUST_CHOOSE.equals(selectFlag)) {
                        mustSelOfferSB.append("@").append(groupOfferId);
                    }
                    for (int k = 0, sizeK = subOfferList.size(); k < sizeK; k++) {
                        if (groupOfferId.equals(subOfferList.getData(k).getString("OFFER_ID"))) {
                            IData selOffer = new DataMap();
                            selOffer.put("OFFER_ID", groupOfferId);
                            selOffer.put("OFFER_CODE", groupOffer.getString("OFFER_CODE"));
                            selOffer.put("OFFER_TYPE", groupOffer.getString("OFFER_TYPE"));
                            selOffer.put("SELECT_FLAG", selectFlag);
                            selOfferList.add(selOffer);
                            break;
                        }
                    }
                }

                if (IDataUtil.isNotEmpty(selOfferList)) {// 商品组中已经选择的商品
                    groupData.put("SEL_OFFER", selOfferList);
                }

                int length = mustSelOfferSB.length();
                if (length > 0) {// 必选的商品
                    groupData.put("MUST_SEL_OFFER", mustSelOfferSB.substring(1, length).toString());
                }
                selGroupData.put(groupData.getString("GROUP_ID"), groupData);
            }
        }

        return selGroupData;
    }

    /**
     * 查询并计算元素生失效时间
     * 
     * @return {"START_DATE":"","END_DATE":""}
     * @throws Exception
     */
    private IData queryElementDate(String offerId, String subOfferId, String subOfferCode, String subOfferType, String groupId, String operType, String effectNow) throws Exception {
        IData eleDate = new DataMap();

        String validDate = "";
        if (BizCtrlType.CreateMember.equals(operType) || BizCtrlType.CreateUser.equals(operType)) {
            if ("0".equals(effectNow)) {
                validDate = SysDateMgr4Web.getFirstDayOfNextMonth();
            } else {
                validDate = SysDateMgr4Web.getSysTime();
            }
        }

        IDataset offerData = IUpcViewCall.queryOfferEnableMode(offerId, groupId, subOfferCode, subOfferType);
        if (DataUtils.isNotEmpty(offerData)) {// 取配置 计算生失效时间
            if (StringUtils.isEmpty(validDate)) {
                validDate = SysDateMgr4Web.startDate(offerData.first().getString("ENABLE_MODE"), offerData.first().getString("ABSOLUTE_ENABLE_DATE"), offerData.first().getString("ENABLE_OFFSET"), offerData.first().getString("ENABLE_UNIT"));

            }
            eleDate.put("START_DATE", validDate);
            String endDate = SysDateMgr4Web.endDate(validDate, offerData.first().getString("DISABLE_MODE"), offerData.first().getString("ABSOLUTE_DISABLE_DATE"), offerData.first().getString("DISABLE_OFFSET"), offerData.first().getString(
                    "DISABLE_UNIT"));
            if (StringUtils.isNotBlank(endDate)) {
                eleDate.put("END_DATE", endDate);
            } else {
                eleDate.put("END_DATE", TimeUtil.EXPIRE_DATE);
            }

        } else {
            if (StringUtils.isEmpty(validDate)) {
                validDate = SysDateMgr4Web.getSysTime();
            }
            eleDate.put("START_DATE", validDate);
            eleDate.put("END_DATE", TimeUtil.EXPIRE_DATE);
        }
        return eleDate;
    }

    // private IData buildOfferData(IData offer) throws Exception
    // {
    // IData offerData = new DataMap();
    // offerData.put("OFFER_ID", offer.getString("OFFER_ID"));
    // offerData.put("OFFER_NAME", offer.getString("OFFER_NAME"));
    // offerData.put("OFFER_CODE", offer.getString("OFFER_CODE"));
    // offerData.put("OFFER_TYPE", offer.getString("OFFER_TYPE"));
    // offerData.put("BRAND_CODE", offer.getString("BRAND_CODE"));
    // return offerData;
    // }

    private IDataset buildGrpPackageList(IData memOffer) throws Exception {
        IDataset grpPackageList = new DatasetList();
        IDataset subOffers = memOffer.getDataset("SUBOFFERS");
        if (IDataUtil.isNotEmpty(subOffers)) {// 该suboffers属于构成关系，这里不再做重复查询
            for (int i = 0, size = subOffers.size(); i < size; i++) {
                IData subOffer = subOffers.getData(i);
                String selectFlag = subOffer.getString("SELECT_FLAG", "");
                String offerId = memOffer.getString("OFFER_ID", "");
                IData grpPackage = new DataMap();
                if ("0".equals(selectFlag)) {
                    grpPackage.put("ELEMENT_ID", subOffer.getString("OFFER_CODE"));
                    grpPackage.put("ELEMENT_NAME", subOffer.getString("OFFER_NAME"));
                    grpPackage.put("ELEMENT_TYPE_CODE", subOffer.getString("OFFER_TYPE"));
                    grpPackage.put("PRODUCT_ID", memOffer.getString("OFFER_CODE"));
                    grpPackage.put("PACKAGE_ID", "-1");
                    grpPackage.put("MODIFY_TAG", "0");
                    grpPackage.put("SELECT_FLAG", "0");
                    grpPackageList.add(grpPackage);
                } else {// 处理电子协议中选择的集团商品定制信息
                    grpPackage.put("ELEMENT_ID", subOffer.getString("OFFER_CODE"));
                    grpPackage.put("ELEMENT_NAME", subOffer.getString("OFFER_NAME"));
                    grpPackage.put("ELEMENT_TYPE_CODE", subOffer.getString("OFFER_TYPE"));
                    grpPackage.put("PRODUCT_ID", memOffer.getString("OFFER_CODE"));
                    grpPackage.put("PACKAGE_ID", subOffer.getString("GROUP_ID"));
                    grpPackage.put("MODIFY_TAG", "0");
                    grpPackage.put("SELECT_FLAG", selectFlag);
                    grpPackage.put("ELE_OFFER_ID", offerId);
                    grpPackageList.add(grpPackage);
                }
            }
        }
        return grpPackageList;
    }

    private IDataset queryEcPackages(String ecUserId) throws Exception {
        IDataset userGrpPackageList = new DatasetList();
        // IDataset elementList = new DatasetList();
        if (StringUtils.isNotBlank(ecUserId)) {
            IData inparam = new DataMap();
            inparam.put("USER_ID", ecUserId);
            IDataset elementList = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", inparam);
            if (DataUtils.isEmpty(elementList)) {
                return userGrpPackageList;
            }
            for (int i = 0, size = elementList.size(); i < size; i++) {
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
        }
        return userGrpPackageList;
    }

    private IData getSerialNumber() throws Exception {
        IData snData = new DataMap();
        // 服务号码设置
        String groupId = getPage().getData().getString("GROUP_ID");
        String offerCode = getPage().getData().getString("OFFER_CODE");
        IData ecAccessNumInfo = getSerialNumber(groupId, offerCode, getTradeEparchyCode());
        snData.put("SERIAL_NUMBER", ecAccessNumInfo.getString("SERIAL_NUMBER"));
        snData.put("RES_TYPE_CODE", ecAccessNumInfo.getString("RES_TYPE_CODE"));
        snData.put("SERIAL_NUMBER_SUCCESS", "false");
        snData.put("IF_RES_CODE", ecAccessNumInfo.getString("IF_RES_CODE"));
        if ("0".equals(ecAccessNumInfo.getString("IF_RES_CODE"))) {
            // 不需要校验服务号码
            snData.put("SERIAL_NUMBER_SUCCESS", "true");
        }

        return snData;
    }

    private IData getSerialNumber(String groupId, String productId, String grpUserEparchyCode) throws Exception {
        if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(productId)) {
            return null;
        }

        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(productId, "P", BizCtrlType.CreateUser, "TradeTypeCode");
        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据" + productId + "没找到业务类型");
        }

        // 避免服务号码的重复 add begin
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
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
        String resTypeCode = "L";
        if (StringUtils.isBlank(resTypeCode)) {
            if (StringUtils.isNotEmpty(serialNumber) && "8070".equals(productId) && ("0").equals(ifResCode)) {
                resTypeCode = "T";
            } else {
                resTypeCode = "G";
            }
        }
        // 服务号码信息
        IData infoData = new DataMap();
        infoData.put("SERIAL_NUMBER", serialNumber);
        infoData.put("RES_TYPE_CODE", resTypeCode);
        infoData.put("IF_RES_CODE", ifResCode);

        if ("0".equals(ifResCode)) {// 服务号码校验通过
            infoData.put("HIDDEN_SERIAL_NUMBER", serialNumber);
        }
        return infoData;
    }

    private String checkResultInfo(String productId, String serialNumber) throws Exception {
        String eparchyCode = getTradeEparchyCode();
        // 查询tradetypecode
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(productId, "P", BizCtrlType.CreateUser, "TradeTypeCode");
        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据" + productId + "没找到业务类型");
        }

        // 查询服务号码前缀
        // IData param = new DataMap();
        // param.put("PRODUCT_ID", productId);
        // param.put(Route.USER_EPARCHY_CODE, eparchyCode);
        // IData preSnData = CSViewCall.callone(this, "SS.ProductInfoSVC.getGrpSnBySelectParam", param);
        // String preSn = preSnData.getString("PRE_SN", "");// 服务号码前缀

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
        IData param = new DataMap();
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

    /**
     * 获取成员角色列表
     * 
     * @param offerId
     * @param ecSubscriberInsId
     * @return
     * @throws Exception
     */
    private IDataset queryRoleList(String relationTypeCode, String offerId) throws Exception {
        IData param = new DataMap();
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        IDataset roleList = CSViewCall.call(this, "CS.StaticInfoQrySVC.getRoleCodeList", param);

        if (IDataUtil.isNotEmpty(roleList)) {
            if ("110000006100".equals(offerId)) {
                IDataset filterList = DataHelper.filter(roleList, "ROLE_CODE_B=1");
                filterList.addAll(DataHelper.filter(roleList, "ROLE_CODE_B=8"));
                roleList.clear();
                roleList.addAll(filterList);
            } else if ("110000009048".equals(offerId)) {
                roleList.clear();
                IData roleData = new DataMap();
                roleData.put("ROLE_CODE_B", "2");
                roleData.put("ROLE_B", "商户管家终端");
                roleList.add(roleData);
            }
        }
        return roleList;
    }

    private IData getMemberImportConfig(String offerCode) throws Exception {
        IData result = new DataMap();
        IData data = new DataMap();
        data.put("SUBSYS_CODE", "CSM");
        data.put("PARAM_ATTR", "1936");
        data.put("PARAM_CODE", offerCode);
        IDataset configList = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaByParser", data);
        if (IDataUtil.isNotEmpty(configList)) {
            IData importConfig = configList.first();
            result.put("SHOW_MEB_LIST", true);
            result.put("EC_PROD_OFFER_CODE", offerCode);
            result.put("FILE_PATH", importConfig.getString("PARA_CODE17"));
            result.put("IMPORT_XML_PATH", importConfig.getString("PARA_CODE18"));
            result.put("EXPORT_XML_PATH", importConfig.getString("PARA_CODE19"));
            result.put("CHECK_SVC_NAME", importConfig.getString("PARA_CODE5"));
            result.put("INIT_MEB_SVC", importConfig.getString("PARA_CODE4"));
            result.put("ADD_MEMBER_TYPE_LIST", getMemberTypeList(importConfig.getString("PARA_CODE2")));
        }
        return result;
    }

    private IDataset getMemberTypeList(String type) throws Exception {
        IDataset memberTypeList = new DatasetList();
        if ("0".equals(type)) {
            IData type1 = new DataMap();
            type1.put("TEXT", "批量");
            type1.put("VALUE", "0");
            memberTypeList.add(type1);
        } else if ("1".equals(type)) {
            IData type2 = new DataMap();
            type2.put("TEXT", "单条");
            type2.put("VALUE", "1");
            memberTypeList.add(type2);
        } else {
            IData type1 = new DataMap();
            type1.put("TEXT", "批量");
            type1.put("VALUE", "0");

            IData type2 = new DataMap();
            type2.put("TEXT", "单条");
            type2.put("VALUE", "1");

            memberTypeList.add(type1);
            memberTypeList.add(type2);
        }
        return memberTypeList;
    }

    private IDataset getAcctDealList() throws Exception {
        IDataset acctDealList = new DatasetList();
        IData addAcct = new DataMap();
        IData combinAcct = new DataMap();
        addAcct.put("DATA_ID", "0");
        addAcct.put("DATA_NAME", "新增账户");

        combinAcct.put("DATA_ID", "1");
        combinAcct.put("DATA_NAME", "已有账户");// 海南客户要求改成已有账户,客户觉得合户账户容易混淆

        acctDealList.add(addAcct);
        acctDealList.add(combinAcct);
        return acctDealList;
    }

    private IDataset queryPayPlansByOfferID(String offerId, String subscriberInsId) throws Exception {
        IDataset payplanList = new DatasetList();

        IData resultData = PayPlanEditView.renderPayPlanEditInfo(this, offerId, subscriberInsId);
        if (IDataUtil.isEmpty(resultData)) {
            return payplanList;
        }

        IDataset payPanSrc = resultData.getDataset("PAYPLAN_SRC");
        if (IDataUtil.isEmpty(payPanSrc)) {
            return payplanList;
        }

        for (int i = 0, payPlanSize = payPanSrc.size(); i < payPlanSize; i++) {
            IData payPlan = new DataMap();
            payPlan.put("PLAN_TYPE_CODE", payPanSrc.getData(i).getString("PLAN_TYPE"));
            payPlan.put("PLAN_TYPE", payPanSrc.getData(i).getString("PLAN_NAME"));
            payPlan.put("PLAN_TYPE_SELECTED", payPanSrc.getData(i).getString("CHECKED", "false"));
            payplanList.add(payPlan);
        }

        return payplanList;
    }

    private IDataset queryPayplansByUserIdForMeb(String userId) throws Exception {
        IDataset payPlans = UserPayPlanInfoIntfViewUtil.qryPayPlanInfosByGrpUserIdForGrp(this, userId);
        IDataset payTypeSet = new DatasetList();
        if (IDataUtil.isNotEmpty(payPlans)) {
            for (int i = 0; i < payPlans.size(); i++) {
                IData tmp = payPlans.getData(i);
                String payTypeCode = tmp.getString("PLAN_TYPE_CODE", "");
                String payTypeName = tmp.getString("PLAN_NAME", "");

                boolean found = false;
                for (int j = 0; j < payTypeSet.size(); j++) {
                    IData data = payTypeSet.getData(j);
                    if (data.getString("PAY_TYPE_CODE").equals(payTypeCode)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    IData map = new DataMap();
                    map.put("PAY_TYPE_CODE", payTypeCode);
                    map.put("PAY_PLAN_NAME", payTypeName);
                    payTypeSet.add(map);
                }
            }
        }
        return payTypeSet;
    }

    /**
     * 将协议中选中的元素初始化到必选子商品列表中
     * 
     * @param offer
     * @param agreementElementList
     * @param operType
     * @throws Exception
     */
    private void initSubOfferListByAgreement(IData offer, IDataset agreementElementList, String operType) throws Exception {
        if (IDataUtil.isEmpty(agreementElementList)) {
            return;
        }
        IDataset agreeSubOfferList = new DatasetList();
        String effectNow = getPage().getData().getString("EFFECT_NOW");
        IDataset subOfferList = offer.getDataset("SUBOFFERS");
        for (int i = 0, sizeI = agreementElementList.size(); i < sizeI; i++) {
            boolean isNotExist = true;
            IData agreementElement = agreementElementList.getData(i);

            if (offer.getString("OFFER_ID").equals(agreementElement.getString("OFFER_ID"))) {
                agreementElementList.remove(agreementElement);
                isNotExist = false;
                continue;
            }
            if (IDataUtil.isEmpty(subOfferList)) {
                subOfferList = new DatasetList();
                isNotExist = true;
            } else {
                for (int j = 0, sizeJ = subOfferList.size(); j < sizeJ; j++) {
                    IData subOffer = subOfferList.getData(j);
                    if (subOffer.getString("OFFER_ID").equals(agreementElement.getString("OFFER_ID"))) {// 如果协议中的元素在构成元素中存在，则pass掉
                        isNotExist = false;
                        break;
                    }
                }

            }

            if (isNotExist) {// 如果协议中的元素在构成元素中不存在，则加到subofferlist中
                String agreementOfferId = agreementElement.getString("OFFER_ID");
                String groupId = agreementElement.getString("GROUP_ID");
                IData agreementOffer = IUpcViewCall.getOfferInfoByOfferId(agreementOfferId);
                if (IDataUtil.isEmpty(agreementOffer)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据协议中的商品编码" + agreementOfferId + "没查询到商品信息。");
                }

                IData subOfferData = new DataMap();
                subOfferData.put("OFFER_ID", agreementOffer.getString("OFFER_ID"));
                subOfferData.put("OFFER_NAME", agreementOffer.getString("OFFER_NAME"));
                subOfferData.put("OFFER_CODE", agreementOffer.getString("OFFER_CODE"));
                subOfferData.put("OFFER_TYPE", agreementOffer.getString("OFFER_TYPE"));
                subOfferData.put("GROUP_ID", groupId);
                subOfferData.put("OPER_CODE", "0");
                subOfferData.put("SELECT_FLAG", UpcConst.SELECT_FLAG_CAN_CHOOSE_NO);
                subOfferData.put("REL_OFFER_CODE", offer.getString("OFFER_CODE"));
                subOfferData.put("HAS_OFFER_GROUP", false); // 普通产品只有两级关系
                subOfferData.put("PRICE_CHA_TYPE", "OFFER");
                boolean hasSubOfferCha = queryHasOfferCha(subOfferData.getString("OFFER_ID"), subOfferData.getString("OFFER_TYPE"), operType, offer);
                subOfferData.put("HAS_OFFER_CHA", hasSubOfferCha);

                IData eleData = queryElementDate(offer.getString("OFFER_ID"), agreementOfferId, agreementOffer.getString("OFFER_CODE"), agreementOffer.getString("OFFER_TYPE"), groupId, operType, effectNow);
                subOfferData.putAll(eleData);

                agreeSubOfferList.add(subOfferData);
            }
        }
        if (agreeSubOfferList.size() > 0) {
            subOfferList.addAll(agreeSubOfferList);
        }
        offer.put("SUBOFFERS", subOfferList);
    }

    public String getTradeEparchyCode() throws Exception {
        String loginEparchyCode = getVisit().getLoginEparchyCode();

        // HNAN 07XX 等非数字地州，都转换成0731
        if (!StringUtils.isNumeric(loginEparchyCode)) {
            loginEparchyCode = "0898";
        }

        return loginEparchyCode;
    }

    protected void cleanupAfterRender(IRequestCycle cycle) {
        super.cleanupAfterRender(cycle);
        setInfo(null);
        setEcOffer(null);
        setMebOffer(null);
        setEcSubOfferList(null);
        setMebSubOfferList(null);
    }

    // ESP订购人员查询
    public void qryOrderStaffinfo() throws Exception {
        IData input = getPage().getData();
        IData inParam = new DataMap();
        String staffName = input.getString("cond_OrderStaffName", "");
        String roleId = StaticUtil.getStaticValue(getVisit(), "TD_B_EWE_CONFIG", "CONFIGNAME", "PARAMVALUE", "ESP_ORDER_ROLE");
        if (StringUtils.isBlank(roleId)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有指定ESP订购角色配置！请检查TD_B_EWE_CONFIG表配置！");
        }
        inParam.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        inParam.put("START_MAX", "0");
        inParam.put("ROWNUM_", "1000");
        inParam.put("X_GETMODE", "13");
        inParam.put("RIGHT_CODE", roleId);
        IDataset staffList = CSViewCall.call(this, "QSM_ChkSysOrgInfo", inParam);
        if (StringUtils.isNotBlank(staffName)) {
            for (int i = 0; i < staffList.size(); i++) {
                IData staff = staffList.getData(i);
                if (staffName.equals(staff.getString("STAFF_NAME"))) {
                    IDataset staffListName = new DatasetList();
                    staffListName.add(staff);
                    setStaffInfos(staffListName);
                }
            }
        } else {
            setStaffInfos(staffList);
        }
    }

    public abstract void setEcOffer(IData ecOffer);

    public abstract void setMebOffer(IData mebOffer);

    public abstract void setInfo(IData info);

    public abstract void setGrpPackageList(IDataset grpPackageList);

    public abstract void setEcSubOfferList(IDataset ecSubOfferList);

    public abstract void setMebSubOfferList(IDataset mebSubOfferList);

    public abstract void setStaffInfos(IDataset infos) throws Exception;
}
