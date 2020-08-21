package com.asiainfo.veris.crm.iorder.web.igroup.openenterprisemember;

import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.uca.UCAInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import org.apache.tapestry.IRequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class OpenEnterpriseMember extends GroupBasePage {

    private static final Logger logger = LoggerFactory.getLogger(OpenEnterpriseMember.class);

    /**
     * 初始化
     *
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable {
        initImsTypes();

        IData condition = new DataMap();
        String grpUserEparchyCode = getTradeEparchyCode();
        condition.put("GRP_USER_EPARCHYCODE", grpUserEparchyCode);

        //是否是无外框登陆的
        condition.put("NOLogin_FLAG", getData().getBoolean("NOLogin_FLAG", false));

        // 端到端判断，页面初始化开始
        String ibsysid = getData().getString("IBSYSID", "");
        if (!"".equals(ibsysid)) {
            dealEsopData(getData());
        }
        setCondition(condition);

    }


    private void dealEsopData(IData inparam) throws Exception {
        // String flag = "1";
        // setFlag(flag);
        // IData resInfo = new DataMap();
        // resInfo.put("BRAND_CODE", "SRLG");
        // setInfo(resInfo);
        //
        // IData inData = new DataMap();
        //
        // inData.put("NODE_ID", getData().getString("NODE_ID", ""));
        // inData.put("IBSYSID", getData().getString("IBSYSID", ""));
        // inData.put("SUB_IBSYSID", getData().getString("SUB_IBSYSID", ""));
        // inData.put("OPER_CODE", "15");
        //
        // IData httpResult = CSViewCall.callone(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
        // if (IDataUtil.isEmpty(httpResult))
        // CSViewException.apperr(GrpException.CRM_GRP_508);
        //
        // // 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 结束
        // String groupId = httpResult.getString("GROUP_ID", "");
        // String productId = httpResult.getString("PRODUCT_ID", "");
        // String userId = httpResult.getString("USER_ID", "");
        // String productTypeCode = httpResult.getString("PRODUCT_TYPE_CODE", "");
        //
        // // 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 开始
        // IDataset eos = new DatasetList();
        //
        // IData eosData = new DataMap();
        //
        // eosData.put("IBSYSID", getData().getString("IBSYSID"));
        // eosData.put("SUB_IBSYSID", getData().getString("SUB_IBSYSID"));
        // eosData.put("NODE_ID", getData().getString("NODE_ID"));
        // eosData.put("WORK_ID", getData().getString("WORK_ID"));
        // eosData.put("BPM_TEMPLET_ID", getData().getString("BPM_TEMPLET_ID"));
        // eosData.put("MAIN_TEMPLET_ID", getData().getString("MAIN_TEMPLET_ID"));
        // eosData.put("FLOW_MAIN_ID", getData().getString("FLOW_MAIN_ID"));
        // eosData.put("ATTR_CODE", "ESOP");
        // eosData.put("ATTR_VALUE", getData().getString("IBSYSID"));
        // eosData.put("RSRV_STR1", getData().getString("NODE_ID"));
        // eosData.put("RSRV_STR3", getData().getString("SUB_IBSYSID"));
        //
        // // 根据产品ID 判断td_s_compare表，如果有数据，就填04 没有就填 01
        // IData param = new DataMap();
        // param.put("SUBSYS_CODE", "CSM");
        // param.put("PARAM_ATTR", "3369");
        // param.put("PARAM_CODE", productId);
        // param.put("EPARCHY_CODE", grpUserEparchyCode);
        //
        // IDataset dataset = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", param);
        // if (IDataUtil.isEmpty(dataset))
        // eosData.put("RSRV_STR2", "01");
        // else
        // eosData.put("RSRV_STR2", "04");
        // eos.add(eosData);
        //
        // condition.put("EOS", eos);
        // condition.put("ESOP_TAG", "ESOP");
        //
        // if (StringUtils.isEmpty(userId))
        // {
        // // 如果没取到USER_ID，则说明该集团以前就进行过集团开户
        // param.clear();
        // param.put("GROUP_ID", groupId);
        // param.put("PRODUCT_ID", productId);
        // IDataset usercustInfos = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGrpProductinfoByProductId", param);
        //
        // if (IDataUtil.isNotEmpty(usercustInfos))
        // {
        // userId = usercustInfos.getData(0).getString("USER_ID", "");
        // }
        // }
        //
        // if (StringUtils.isEmpty(userId))
        // {
        // CSViewException.apperr(GrpException.CRM_GRP_601, getData().getString("IBSYSID"));
        // }
        //
        // getData().put("USER_ID", userId);
        // getData().put("PRODUCT_ID", productId);
        // condition.put("ESOP_USER_ID", userId);
        // condition.put("ESOP_PRODUCT_ID", productId);
        // condition.put("ESOP_PRODUCT_TYPE_CODE", productTypeCode);
        // condition.put("cond_GROUP_ID", groupId);
        // condition.put("ESOP_CONTRACT_ID", contract_id);
        // condition.put("IBSYSID", ibsysid);
        //
        // // 获取集团成员产品信息
        // IDataset memProductInfos = getSRLGMebProductIdByProductId(productId);
        // condition.put("ESOP_PRODUCTMEBSET", memProductInfos);
        //
        // param.clear();
        // param.put("USER_ID", userId);
        // param.put("REMOVE_TAG", "0");
        // IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", param);
        //
        // if (IDataUtil.isNotEmpty(userInfos))
        // {
        // if (userInfos.getData(0) != null)
        // {
        // String sn = userInfos.getData(0).getString("SERIAL_NUMBER", "");
        // condition.put("cond_GROUP_SERIAL_NUMBER", sn);
        //
        // getData().put("CUST_ID", userInfos.getData(0).getString("CUST_ID"));
        // }
        // }
        //
        // getData().put("cond_GROUP_ID", groupId);
        // if (IDataUtil.isNotEmpty(memProductInfos) && memProductInfos.size() == 1)
        // {
        //
        // getData().put("PRODUCT_ID", memProductInfos.getData(0).getString("PRODUCT_ID"));
        // queryProductInfo(cycle);
        // }
        //
        // IData productInfo = getProductInfo();
        // String productName = productInfo.getString("PRODUCT_NAME");
        //
        // condition.put("ESOP_PRODUCT_NAME", productName);
        //
        // getGroupBaseInfo(cycle);
        //
        // // 根据产品显示是否体验客户
        // IData cparam = new DataMap();
        // cparam.put("SUBSYS_CODE", "CSM");
        // cparam.put("PARAM_ATTR", "3380");
        // cparam.put("PARAM_CODE", productId);
        // cparam.put("EPARCHY_CODE", grpUserEparchyCode);
        //
        // IDataset cparamset = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", cparam);
        // if(IDataUtil.isNotEmpty(cparamset)){
        // condition.put("IS_EXPER", "0");
        // }else{
        // condition.put("IS_EXPER", "1");
        // }
    }

    /**
     * 查询主商品基本信息
     *
     * @param cycle
     * @throws Exception
     */
    public void queryBaseOffer(IRequestCycle cycle) throws Exception {
        String offerCode = this.getData().getString("OFFER_CODE", "");
        String custId = this.getData().getString("CUST_ID", "");

        IData offerInfo = UpcViewCall.queryOfferByOfferId(this, "P", offerCode, "Y");
        if (DataUtils.isEmpty(offerInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_CODE" + offerCode + "没有查询到商品信息！");
        }

        // offerInfo.put("BRAND_CODE", EcUpcViewUtil.queryBrandByOfferId(offerId));

        IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        setCustInfo(custInfo);

        offerInfo.put("OPER_TYPE", BizCtrlType.CreateUser);

        setInitOffer(offerInfo);

        initImsTypes();

        // 初始化资源信息
        initialRes(cycle);

        IData ajaxData = new DataMap();
        ajaxData.put("OFFER_DATA", offerInfo);
        ajaxData.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());

        String flag = "false";
        String brandCode = offerInfo.getString("BRAND_CODE");
        if ("IMSG".equals(brandCode)) {
            flag = "true";
        }
        setImsFlag(flag);
        ajaxData.put("IMSG_FLAG", flag);

        this.setAjax(ajaxData);

    }

    /**
     * 前台数据提交
     *
     * @param cycle
     * @throws Exception
     */
    public void submit(IRequestCycle cycle) throws Exception
    {
        IDataset inparams = new DatasetList(getData().getString("SUBMIT_DATA"));

        IData cond = new DataMap();
        cond.put("IN_PARAMS", inparams);
        cond.put("OPER_TYPE", BizCtrlType.OpenGroupMeb);
        cond.put("OFFER_CODE", getData().getString("OFFER_CODE"));
        PageDataTrans pageTransData = PageDataTrans.getInstance(cond);

        IData svcParam = pageTransData.transformData();
        svcParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        svcParam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        //String svcName = pageTransData.getSvcName();

        logger.debug("=============================调用服务数据 {}", svcParam.toString());

        IDataset result = CSViewCall.call(this, "CS.OpenGroupMemberSVC.crtTrade", svcParam);

        logger.debug("=====服务调用结果：", result.toString());

        setAjax(result);
    }

    /**
     * 资源初始化
     *
     * @param cycle
     * @throws Exception
     */
    public void initialRes(IRequestCycle cycle) throws Exception {
        IData param = new DataMap();

        param.put("PRODUCT_ID", getData().getString("OFFER_CODE", ""));

        // 资源类型和名称
        IDataset resList = CSViewCall.call(this, "CS.ProductInfoQrySVC.getResTypeByMainProduct", param);

        if (IDataUtil.isNotEmpty(resList)) {
            // 非专线
            // setTag("0");
            setCondition(resList.first());
        } else {
            // 专线
            // setTag("1");

            // resList = new DatasetList();
            //
            // IData resData = new DataMap();
            // resData.put("RES_TYPE_CODE", "0");
            // resData.put("RES_CODE", this.generateSerialNumber);
            // resData.put("RES_ID", this.generateSerialNumber);
            // resData.put("RES_TYPE", "服务号码");
            // resList.add(resData);
            // setResList(resList);
            //
            // IData info = new DataMap();
            // info.put("SERIAL_NUMBER", this.generateSerialNumber);
            // setInfo(info);
        }
    }

    /**
     * 校验资源信息
     *
     * @param cycle
     * @throws Exception
     */
    public void checkResourceInfo(IRequestCycle cycle) throws Exception {
        IData inparam = getData();
        inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        inparam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "CS.OpenGroupMemberSVC.checkResourceSn", inparam);

        IData retdata = dataset.getData(0);

        IData checkResultData = new DataMap();

        if (IDataUtil.isNotEmpty(retdata)) {
            checkResultData.putAll(retdata);
        } else {
            checkResultData.put("X_RESULTCODE", "-1");
            checkResultData.put("X_RESULTINFO", "资源调用异常！");
        }

        setAjax(checkResultData);

    }

    private void initImsTypes() throws Exception {
        IDataset dataset = new DatasetList();

        IData data1 = new DataMap();
        data1.put("PARA_CODE1", "1");
        data1.put("PARA_CODE2", "固定终端(SIP硬终端或POTS话机)");
        dataset.add(data1);

        IData data2 = new DataMap();
        data2.put("PARA_CODE1", "2");
        data2.put("PARA_CODE2", "无卡PC客户端");
        dataset.add(data2);

        setImsTypes(dataset);
    }

    /**
     * 集团的名称和证件号校验
     * @author taosx
     * @param cycle
     * @throws Exception
     */
    public void checkPsptCustName(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset result = new DatasetList();
        IData outParam = new DataMap();

        String custName = data.getString("CUST_NAME", "");
        String psptId = data.getString("PSPT_ID", "");
        String psptType = data.getString("PSPT_TYPE_CODE", "");
        String custId = data.getString("CUST_ID", "");
        String groupId = data.getString("GROUP_ID", "");

        String code;
        String info;
        IData relaInfo = UCAInfoIntf.qryGrpCustInfoByGrpId(this, groupId);

        if (IDataUtil.isNotEmpty(relaInfo))
        {
            String relaName = relaInfo.getString("CUST_NAME");
            String relaPspt = relaInfo.getString("BUSI_LICENCE_NO");

            if (custName.equals(relaName) && psptId.equals(relaPspt)) {
                code = "0";
                info = "比对成功！";
            } else if (!custName.equals(relaName) && psptId.equals(relaPspt)) {
                code = "1";
                info = "客户名称与集团客户名称不一致，请重新输入！";
            } else if (custName.equals(relaName) && !psptId.equals(relaPspt)) {
                code = "1";
                info = "证件号码与集团证件号码不一致，请重新输入！";
            } else {
                code = "1";
                info = "客户名称与集团客户名称不一致，证件号码与集团证件号码不一致，请重新输入！";
            }
        } else {
            code = "1";
            info = "集团客户真实资料不存在！";
        }

        outParam.put("X_RESULTCODE", code);
        outParam.put("X_RESULTINFO", info);
        result.add(outParam);
        setAjax(result);
    }

    /**
     * 人像信息比对
     * @author taosx
     * @param clcle
     * @throws Exception
     */
    public void cmpPicInfo(IRequestCycle clcle) throws Exception {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 身份证在线校验
     *
     * @author taosx
     * @param clcle
     * @throws Exception
     */
    public void verifyIdCard(IRequestCycle clcle) throws Exception {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyIdCard", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 全网一证5号
     *
     * @author taosx
     * @param cycle
     * @throws Exception
     */
    public void checkGlobalMorePsptId(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.checkGlobalMorePsptId", data);
        setAjax(dataset.getData(0));
    }

    /**
     * 营业执照验证
     *
     * @author taosx
     * @param cycle
     * @throws Exception
     */
    public void verifyEnterpriseCard(IRequestCycle cycle) throws Exception {
        IData data = getData();

        logger.debug("====营业执照验证 start ：" + data);

        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyEnterpriseCard", data);

        logger.debug("====营业执照验证 end ：" + dataset);

        setAjax(dataset.getData(0));
    }

    /**
     * 组织机构代码证验证
     *
     * @author taosx
     * @param cycle
     * @throws Exception
     */
    public void verifyOrgCard(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyOrgCard", data);
        setAjax(dataset.getData(0));
    }

    public void verifyIdCardName(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyIdCardName", data);
        setAjax(dataset.getData(0));
    }

    /**
     * 手动输入权限
     * @author taosx
     * @param cycle
     * @throws Exception
     */
    public void getInput(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("X_RESULTCODE", "0");
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "INPUT_PERMISSION")) {
            data.put("X_RESULTCODE", "00");
        }
        setAjax(data);
    }

    /**
     * REQ201904260020新增物联网批量开户界面权限控制需求
     * 免人像比对权限判断
     * @author mengqx
     * @date 20190515
     * @param clcle
     * @throws Exception
     */
    public void isBatCmpPic(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isBatCmpPic", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 查询集团客户信息
     *
     * @param cycle
     * @throws Exception
     */
    public void queryCustGroupByGroupId(IRequestCycle cycle) throws Exception {
        String groupId = getData().getString("GROUP_ID");

        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = group.getString("CUST_ID");

        setGroupInfo(group);

        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId)) {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);

            if(IDataUtil.isEmpty(managerInfo))
            {
                managerInfo = new DataMap();
            }

            // 获取部分客户资料
            IData param = new DataMap();
            param.put("CUST_ID", custId);
            IData grpCustInfo = CSViewCall.callone(this, "CS.CustomerInfoQrySVC.getCustInfoByCustID", param);
            IData custInfoData = new DataMap();
            custInfoData.put("PSPT_ID", grpCustInfo.getString("PSPT_ID"));
            custInfoData.put("PSPT_TYPE_CODE", grpCustInfo.getString("PSPT_TYPE_CODE"));
            managerInfo.put("grpCustInfo", custInfoData);

            setCustMgrInfo(managerInfo);
        }
    }

    public abstract void setInitOffer(IData offer) throws Exception;

    public abstract void setCondition(IData cond) throws Exception;

    public abstract void setCustInfo(IData cond) throws Exception;

    public abstract void setImsTypes(IDataset imsTypes) throws Exception;

    public abstract void setEditInfo(IData editInfo) throws Exception;

    public abstract void setImsFlag(String flag) throws Exception;

    public abstract void setInAttr(IData inAttr);

    public abstract void setBusi(IData busi);

    public abstract void setGroupInfo(IData groupInfo) throws Exception;

    public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;
}
