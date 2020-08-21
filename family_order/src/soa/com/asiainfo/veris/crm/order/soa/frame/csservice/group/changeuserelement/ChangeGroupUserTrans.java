
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;

public class ChangeGroupUserTrans implements ITrans
{
    private void actElementInfo(IData iData) throws Exception
    {
        // befoe
        preparElement(iData);

        // parse
        parseElement(iData);

        // combine
        combineElement(iData);

    }

    private void actElementParamsInfo(IData iData) throws Exception
    {
        // befoe
        preparElementParams(iData);

        // parse
        parseElementParams(iData);

        // combine
        combineElementParams(iData);
    }

    private void actGrpPackage(IData iData) throws Exception
    {
        // befoe
        preparGrpPackage(iData);

        // parse
        parseGrpPackage(iData);

        // combine
        combineGrpPackage(iData);
    }

    private void actProductParamInfo(IData iData) throws Exception
    {
        // befoe
        preparProductParams(iData);

        // parse
        parseProductParams(iData);

        // combine
        combineProductParams(iData);
    }

    private void addBaseDataAfter(IData iData) throws Exception
    {
        // 移除调临时对象
        iData.remove("INTF_GRP_SVC_DATASET");
        iData.remove("INTF_GRP_DIS_DATASET");
        iData.remove("INTF_MEB_SVC_DATASET");
        iData.remove("INTF_MEB_DIS_DATASET");

        iData.remove("PRODUCT_PARAM");
    }

    private void addBaseDataBefore(IData iData) throws Exception
    {
        iData.put("USER_ID", iData.getString("USER_ID_A", iData.getString("USER_ID")));
        iData.remove("USER_ID_A");
        // iData.put(Route.USER_EPARCHY_CODE,iData.getString("TRADE_EPARCHY_CODE"));
        iData.put("OPER_TYPE", BizCtrlType.ChangeUserDis);// 操作类型
    }

    private void addDataAfter(IData iData) throws Exception
    {

        addSubDataAfter(iData);

        addBaseDataAfter(iData);
    }

    private void addDataBefore(IData iData) throws Exception
    {

        addSubDataBefore(iData);

        addBaseDataBefore(iData);
    }

    protected void addSubDataAfter(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    // 子类重载
    protected void addSubDataBefore(IData iData) throws Exception
    {

    }

    protected void checkRequestData(IData iData) throws Exception
    {
        String productId = IDataUtil.chkParam(iData, "PRODUCT_ID");
        String userId = IDataUtil.chkParam(iData, "USER_ID");
        IData userInfos = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(userInfos))
            CSAppException.apperr(BofException.CRM_BOF_011);

        if (!StringUtils.equals(productId, userInfos.getString("PRODUCT_ID")))
            CSAppException.apperr(GrpException.CRM_GRP_713, "该集团用户没有受理过该产品,请先进行该产品的业务受理!");

        IDataset list = iData.getDataset("LIST_INFOS");
        if (IDataUtil.isEmpty(list))
            CSAppException.apperr(GrpException.CRM_GRP_713, "没有优惠信息，请确认后再进行操作！");

    }

    // 每个省一样的
    private final void combineElement(IData iData) throws Exception
    {
        IDataset intfGrpSvcDs = iData.getDataset("INTF_GRP_SVC_DATASET");
        IDataset intfGrpDiscntDs = iData.getDataset("INTF_GRP_DIS_DATASET");
        IDataset intfMebSvcDs = iData.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = iData.getDataset("INTF_MEB_DIS_DATASET");

        String productId = IDataUtil.chkParam(iData, "PRODUCT_ID");// 集团产品标识
        String eparchyCode = CSBizBean.getUserEparchyCode();
        String userId = IDataUtil.chkParam(iData, "USER_ID");
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);// 集团产品标识
        params.put("USER_ID", userId);// 集团产品标识

        boolean effectNow = iData.getBoolean("EFFECT_NOW", false);
        String bookingDate = iData.getString("PRODUCT_PRE_DATE", "");

        params.put("EFFECT_NOW", effectNow);
        params.put("PRODUCT_PRE_DATE", bookingDate);

        // 1,必须元素
        params.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset defaultElements = CSAppCall.call("CS.SelectedElementSVC.getGrpUserChgElements", params);
        defaultElements = defaultElements.getData(0).getDataset("SELECTED_ELEMENTS");

        // 2,集团可选元素(包括必须的)
        IDataset plusElements = new DatasetList();
        IDataset tempElements = UProductElementInfoQry.getElementInfosByProductId(productId);
        plusElements.addAll(tempElements);
        
        /*IDataset plusPackages = CSAppCall.call("CS.PackageSVC.getGrpPackagesByProduct", params);
        for (int i = 0, iSize = plusPackages.size(); i < iSize; i++)
        {
            IData plusPackage = plusPackages.getData(i);
            String packageId = plusPackage.getString("PACKAGE_ID");
            params.put("PACKAGE_ID", packageId);
            // 2.1根据包查询元素
            IDataset tempElements = CSAppCall.call("CS.PackageSVC.getPackageElements", params);
            plusElements.addAll(tempElements);
        }*/

        IDataset elementResults = new DatasetList();
        elementResults.addAll(intfGrpSvcDs);
        elementResults.addAll(intfGrpDiscntDs);

        for (int i = 0, iSize = elementResults.size(); i < iSize; i++)
        {
            IData elementResult = elementResults.getData(i);
            String modifyTag = elementResult.getString("MODIFY_TAG");
            String elementId = elementResult.getString("ELEMENT_ID");
            String packageId = elementResult.getString("PACKAGE_ID");
            String p_productId = elementResult.getString("PRODUCT_ID");

            String elementForceTag = elementResult.getString("ELEMENT_FORCE_TAG");
            String elementDefaultTag = elementResult.getString("ELEMENT_DEFAULT_TAG");

            String packageForceTag = elementResult.getString("PACKAGE_FORCE_TAG");
            if ("1".equals(modifyTag) && ("1".equals(packageForceTag) && ("1".equals(elementForceTag) || "1".equals(elementDefaultTag))))
            {// 必须元素不能退订

                CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]为集团必选元素,不能退订");

            }
            else if ("1".equals(modifyTag))
            {// 成员定制 退订
                IDataset isCanElments = DataHelper.filter(defaultElements, "ELEMENT_ID=" + elementId + ",PACKAGE_ID=" + packageId);
                if (IDataUtil.isEmpty(isCanElments))
                    CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]包[" + packageId + "]集团没有订购!");
                // 元素退订的时候需要查已有资料的开始时间
                String startDate = isCanElments.getData(0).getString("START_DATE");
                elementResult.put("START_DATE", startDate);
            }
            else if ("0".equals(modifyTag))
            {
                IDataset isCanElments = DataHelper.filter(plusElements, "ELEMENT_ID=" + elementId + ",PACKAGE_ID=" + packageId);
                if (IDataUtil.isEmpty(isCanElments))
                    CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]包[" + packageId + "]不能为当前集团所能选择的元素!");
            }
            ElementUtil.dealSelectedElementStartDateAndEndDate(elementResult, bookingDate, effectNow, eparchyCode);
        }

        iData.put("ELEMENT_INFO", elementResults);
    }

    private void combineElementParams(IData iData) throws Exception
    {

    }

    private void combineGrpPackage(IData iData) throws Exception
    {
        String grpProductId = IDataUtil.chkParam(iData, "PRODUCT_ID");// 集团产品标识
        String userId = IDataUtil.chkParam(iData, "USER_ID");
        boolean effectNow = iData.getBoolean("EFFECT_NOW", false);
        String bookingDate = iData.getString("PRODUCT_PRE_DATE", "");
        String eparchyCode = CSBizBean.getUserEparchyCode();

        String useTag = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PRODUCT_COMP", "PRODUCT_ID", "USE_TAG", grpProductId);
        if (!useTag.equals(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue()))
            return;

        // 必选
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        IDataset defaultElements = CSAppCall.call("CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", inparam);// 查询集团所订定制的元素CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp

        // 2,可选成员元素(包括必须的)(已兼容成员所对的集团定制)
        IDataset plusElements = new DatasetList();

        IDataset disPlusDataset = ProductInfoQry.getMebProductPlusDiscnt(grpProductId, eparchyCode);
        IDataset svcPlusDataset = ProductInfoQry.getMebProductPlusSvc(grpProductId, eparchyCode);
        plusElements.addAll(disPlusDataset);
        plusElements.addAll(svcPlusDataset);

        IDataset intfMebSvcDs = iData.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = iData.getDataset("INTF_MEB_DIS_DATASET");

        IDataset elementResults = new DatasetList();
        elementResults.addAll(intfMebSvcDs);
        elementResults.addAll(intfMebDiscntDs);

        for (int i = 0, iSize = elementResults.size(); i < iSize; i++)
        {
            IData elementResult = elementResults.getData(i);
            String modifyTag = elementResult.getString("MODIFY_TAG");
            String elementId = elementResult.getString("ELEMENT_ID");
            String packageId = elementResult.getString("PACKAGE_ID");
            String m_productId = elementResult.getString("PRODUCT_ID");

            String elementForceTag = elementResult.getString("ELEMENT_FORCE_TAG");
            String elementDefaultTag = elementResult.getString("ELEMENT_DEFAULT_TAG");

            String packageForceTag = elementResult.getString("PACKAGE_FORCE_TAG");
            if ("1".equals(modifyTag) && ("1".equals(packageForceTag) && ("1".equals(elementForceTag) || "1".equals(elementDefaultTag))))
            {// 必须元素不能退订

                CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]为集团必选元素,不能退订");

            }
            else if ("1".equals(modifyTag))
            {// 成员定制 退订
                IDataset isCanElments = DataHelper.filter(defaultElements, "ELEMENT_ID=" + elementId + ",PACKAGE_ID=" + packageId);
                if (IDataUtil.isEmpty(isCanElments))
                    CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]包[" + packageId + "]成员没有订购!");
                // 元素退订的时候需要查已有资料的开始时间
                String startDate = isCanElments.getData(0).getString("START_DATE");
                elementResult.put("START_DATE", startDate);
            }
            else if ("0".equals(modifyTag))
            {
                IDataset isCanElments = DataHelper.filter(plusElements, "ELEMENT_ID=" + elementId + ",PACKAGE_ID=" + packageId);
                if (IDataUtil.isEmpty(isCanElments))
                    CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]包[" + packageId + "]不能为当前成员所能选择的元素!");
            }
            ElementUtil.dealSelectedElementStartDateAndEndDate(elementResult, bookingDate, effectNow, eparchyCode);
        }

        IDataset resultsetDataset = new DatasetList();

        resultsetDataset.addAll(elementResults);

        iData.put("GRP_PACKAGE_INFO", resultsetDataset);
    }

    private void combineProductParams(IData iData) throws Exception
    {

    }

    // 子类重载
    protected void parseElement(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    // 子类重载
    protected void parseElementParams(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    protected void parseGrpPackage(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    // 子类重载
    protected void parseProductParams(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    private void preparElement(IData iData) throws Exception
    {
        // 1,处理接口入参,转为为idataset,没有过滤掉必选参数,接口可能传入接口参数
        IDataset intfGrpSvcDs = new DatasetList();// 接口传入集团服务
        IDataset intfGrpDiscntDs = new DatasetList();// 接口传入集团资费
        IDataset intfMebSvcDs = new DatasetList();// 接口传入集团成员服务
        IDataset intfMebDiscntDs = new DatasetList();// 接口传入集团成员优惠

        iData.put("INTF_GRP_SVC_DATASET", intfGrpSvcDs);
        iData.put("INTF_GRP_DIS_DATASET", intfGrpDiscntDs);
        iData.put("INTF_MEB_SVC_DATASET", intfMebSvcDs);
        iData.put("INTF_MEB_DIS_DATASET", intfMebDiscntDs);
    }

    private void preparElementParams(IData iData) throws Exception
    {

    }

    private void preparGrpPackage(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    private void preparProductParams(IData iData) throws Exception
    {

    }

    @Override
    public final void transRequestData(IData iData) throws Exception
    {
        // 不用接口传的iData,to-do

        // before
        addDataBefore(iData);

        // check
        checkRequestData(iData);

        // elementInfo
        actElementInfo(iData);

        actGrpPackage(iData);
        // ElementParamsInfo
        // actElementParamsInfo(iData);

        // productParamsInfo
        // actProductParamInfo(iData);

        // payplanInfo
        // actPayPlanInfo(iData);

        // after
        addDataAfter(iData);
    }
}
