package com.asiainfo.veris.crm.iorder.web.igroup.util;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.FrontProdConverter;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CommonViewCall {
    public static String getAttrValueFromAttrBiz(IBizCommon bc, String id, String idType, String obj, String code) throws Exception {
        String buisType = "";
        IData tempBiz = new DataMap();
        tempBiz.put("ID", id);
        tempBiz.put("ID_TYPE", idType);
        tempBiz.put("ATTR_OBJ", obj);
        tempBiz.put("ATTR_CODE", code);
        IDataset result = call("CS.AttrBizInfoQrySVC.getBizAttr", tempBiz);

        if (IDataUtil.isNotEmpty(result)) {
            buisType = result.first().getString("ATTR_VALUE", "");
        }
        return buisType;
    }

    public static String getAttrValueFromAttrBiz(String id, String idType, String obj, String code) throws Exception {
        String buisType = "";
        IData tempBiz = new DataMap();
        tempBiz.put("ID", id);
        tempBiz.put("ID_TYPE", idType);
        tempBiz.put("ATTR_OBJ", obj);
        tempBiz.put("ATTR_CODE", code);
        IDataset result = call("CS.AttrBizInfoQrySVC.getBizAttr", tempBiz);

        if (IDataUtil.isNotEmpty(result)) {
            buisType = result.first().getString("ATTR_VALUE", "");
        }
        return buisType;
    }

    public static IDataset getAttrsFromAttrBiz(IBizCommon bc, String id, String idType, String obj, String code) throws Exception {
        IData tempBiz = new DataMap();
        tempBiz.put("ID", id);
        tempBiz.put("ID_TYPE", idType);
        tempBiz.put("ATTR_OBJ", obj);
        tempBiz.put("ATTR_CODE", code);
        IDataset result = call("CS.AttrBizInfoQrySVC.getBizAttr", tempBiz);

        if (IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }

    public static boolean hasOfferSpecCha(IBizCommon bc, String operType, String merchpOperType, String offerId, String brand) throws Exception {
        // 针对bboss商品的情况,不需要查询point表来判断是否有商品属性,因为所有的bboss商品均有同样商品属性
        if ("BOSG".equals(brand) && merchpOperType.isEmpty() && !operType.contains("Mb")) {
            return true;
        }
        // 复杂参数页面加载动态配置页面
        IData offer = IUpcViewCall.queryOfferByOfferId(offerId);
        String offerCode = offer.getString("OFFER_CODE");
        String offerType = offer.getString("OFFER_TYPE");
        if ("P".equals(offerType)) {
            String dynConfg = CommonViewCall.getAttrValueFromAttrBiz(offerCode, "P", operType, "dynamicPage");
            if (StringUtils.isNotEmpty(dynConfg)) {
                return true;
            }
        }

        // BBOSS产品特殊处理，将数据转换为全网属性
        if ("BOSG".equals(brand)) {
            if (!"P".equals(offerType)) {
                return false;
            }

            IData prodSpec = new DataMap();
            prodSpec.put("PROD_SPEC_ID", offerCode);
            prodSpec.put("OPER_TYPE", operType);
            prodSpec.put("MERCHP_OPER_TYPE", merchpOperType);
            prodSpec.put("MAIN_TAG", true); // 暂时不需要转换产品编码
            FrontProdConverter.prodConverter(bc, prodSpec, false);
            operType = prodSpec.getString("OPER_TYPE");
            offerId = prodSpec.getString("PROD_SPEC_ID");
            if (StringUtils.isEmpty(offerId)) {
                return false;
            }
        }
        IData data = new DataMap();
        data.put("POINT_ONE", offerId);
        data.put("POINT_TWO", operType);
        IDataset result = call("SS.PointInfoSVC.queryPoint", data);
        return IDataUtil.isNotEmpty(result) ? true : false;
    }

    public static String getConfigParamValue(IBizCommon bc, String configName, String paramName) throws Exception {
        IData data = new DataMap();
        data.put("CONFIGNAME", configName);
        data.put("PARAMNAME", paramName);
        IDataset config = CSViewCall.call(bc, "SS.IsspConfigQrySVC.getParamValue", data);

        if (IDataUtil.isNotEmpty(config)) {
            return config.first().getString("PARAMVALUE", "");
        }
        return "";
    }

    public static IData getIsspBusiSpecRele(IBizCommon bc, String bpmTemplateId, String busiCode, String busiType) throws Exception {
        IData data = new DataMap();
        data.put("BPM_TEMPLET_ID", bpmTemplateId);
        data.put("BUSI_CODE", busiCode);
        data.put("BUSI_TYPE", busiType);
        return CSViewCall.callone(bc, "SS.IsspBusiSpecReleQrySVC.getIsspBusiSpecRele", data);
    }

    private final static IDataset call(String svcName, IData input) throws Exception {
        IDataOutput output = svcFatCall(svcName, input);
        return output.getData();
    }

    private final static IDataOutput svcFatCall(String svcName, IData data) throws Exception {
        IDataInput input = new DataInput();
        input.getData().putAll(data);

        IDataOutput output = ServiceFactory.call(svcName, input);

        return output;
    }

    public static IDataset getAdcMasPlatSvcParam(String grpUserId, String serviceId) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", grpUserId);
        data.put("SERVICE_ID", serviceId);
        IDataset result = call("SS.UserGrpPlatSvcInfoQrySVC.getGrpPayItemInfoByUserId", data);
        return result;
    }

    public static IDataset qryGrpUserSvcByUserSvcId(String userId, String serviceId) throws Exception {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("SERVICE_ID", serviceId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset result = call("CS.UserSvcInfoQrySVC.qryUserSvcByUserSvcId", inparam);
        return result;
    }

    public static IDataset qryBBossAttrByAttrCode(String attrCode) throws Exception {
        IData inparam = new DataMap();
        inparam.put("ATTR_CODE", attrCode);
        IDataset result = call("CS.BBossAttrQrySVC.qryBBossAttrByAttrCode", inparam);
        return result;
    }

    public static IDataset qryAuditInfo(String staffId, String staffName) throws Exception {
        IData inparam = new DataMap();
        inparam.put("STAFF_ID", staffId);
        inparam.put("STAFF_NAME", staffName);
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset result = call("SS.AuditStaffInfoQrySVC.qryAuditStaffInfo", inparam);
        return result;
    }

    /**
     * @author chenhh6
     */
    public static String getDepart(String code) throws Exception {
        IData inparam = new DataMap();
        String success = "false";
        inparam.put("DEPART_CODE", code);
        IDataset result = call("CS.BBossAttrQrySVC.qryBBossDepartCode", inparam);
        if (IDataUtil.isNotEmpty(result)) {
            success = "true";
        }
        return success;
    }

    public static IDataset qryEsopGrpBusiInfo(IBizCommon bc, IData input) throws Exception {
        return CSViewCall.call(bc, "SS.EsopInfoQrySVC.qryEsopGrpBusiInfo", input);
    }

}
