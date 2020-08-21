
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
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
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;

public class CreateGroupMemberTrans implements ITrans
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

    private void actPayPlanInfo(IData iData) throws Exception
    {

        /*
         * String userIdA = IDataUtil.chkParam(iData, "USER_ID");// 集团用户标识 IData params = new DataMap();
         * params.put("USER_ID", userIdA); params.put("USER_ID_A", "-1"); IDataset plans =
         * CSAppCall.call("CS.UserPayPlanInfoQrySVC.getPayPlanInfosByUserIdForGrp", params); if
         * (IDataUtil.isEmpty(plans)) CSAppException.apperr(GrpException.CRM_GRP_713, "没有选择付费方式!"); String playTypeCode
         * = plans.getData(0).getString("PLAN_TYPE_CODE");
         */
        String playTypeCode = "P";// 接口都为P
        iData.put("PLAN_TYPE_CODE", playTypeCode);
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

        iData.remove("SERVICE_CODE");

        iData.remove("DISCNT_CODE");
    }

    private void addBaseDataBefore(IData iData) throws Exception
    {
        String userId = iData.getString("USER_ID_A", iData.getString("USER_ID"));

        if (StringUtils.isBlank(userId))
        {
            String serialNumberA = iData.getString("SERIAL_NUMBER_A", "");

            IData grpUserData = UcaInfoQry.qryUserInfoBySnForGrp(serialNumberA);

            if (IDataUtil.isEmpty(grpUserData))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberA);
            }

            userId = grpUserData.getString("USER_ID");
        }

        iData.put("USER_ID", userId);
        iData.remove("USER_ID_A");

        iData.put("OPER_TYPE", BizCtrlType.CreateMember); // 操作类型
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
        String sn = IDataUtil.chkParam(iData, "SERIAL_NUMBER");

        String productId = IDataUtil.chkParam(iData, "PRODUCT_ID");
        String userId = IDataUtil.chkParam(iData, "USER_ID");

        IData grpUserData = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", sn);
        IDataset mebInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryUserInfoBySn", params);

        if (IDataUtil.isEmpty(mebInfos))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }

        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(BofException.CRM_BOF_011);
        }

        if (!productId.equals(grpUserData.getString("PRODUCT_ID")))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "该集团用户没有受理过该产品,请先进行该产品的业务受理!");
        }

        // 校验uu关系
        IData uuData = new DataMap();
        uuData.put("PRODUCT_ID", productId);

        uuData.put("USER_ID", userId);

        uuData.put("USER_ID_B", mebInfos.getData(0).getString("USER_ID"));// 成员userId

        IDataset uuDataset = ParamInfoQry.getProductUU(uuData);

        if (IDataUtil.isEmpty(uuDataset))
            return;

        String recordCount = uuDataset.getData(0).getString("RECORDCOUNT");

        if (Integer.parseInt(recordCount) == 0)
            return;

        CSAppException.apperr(GrpException.CRM_GRP_713, "此客户已是集团产品成员，不允许重复加入!");

    }

    // 每个省一样的
    private final void combineElement(IData iData) throws Exception
    {
        String productId = IDataUtil.chkParam(iData, "PRODUCT_ID");// 集团产品标识
        String grpUserId = IDataUtil.chkParam(iData, "USER_ID");
        String eparchyCode = CSBizBean.getUserEparchyCode();
        String staffId = CSBizBean.getVisit().getStaffId();

        IData params = new DataMap();
        params.put("GRP_USER_ID", grpUserId);
        params.put("PRODUCT_ID", productId);// 集团产品标识
        params.put("GRP_PRODUCT_ID", productId);// 集团产品标识

        boolean effectNow = iData.getBoolean("EFFECT_NOW", false);
        String bookingDate = iData.getString("PRODUCT_PRE_DATE", "");

        params.put("EFFECT_NOW", effectNow);
        params.put("PRODUCT_PRE_DATE", bookingDate);

        // 1,必须元素
        params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());

        IDataset defaultElements = CSAppCall.call("CS.SelectedElementSVC.getGrpMebOpenElements", params);
        defaultElements = defaultElements.getData(0).getDataset("SELECTED_ELEMENTS");

        // 2,可选成员元素(包括必须的)(已兼容成员所对的集团定制)
        IDataset plusElements = new DatasetList();
        params.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
        
        IData comp = ProductCompInfoQry.getProductFromComp(productId);
        String useTag = comp.getString("USE_TAG");
        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        // 定制
        if (GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag))
        {
        	IDataset tempElements = UserGrpPkgInfoQry.getUserGrpPkgInfoByPk(grpUserId, mebProductId, null, null, null);
        	plusElements.addAll(tempElements);
        }
        else
        {
        	IDataset tempElements = UProductElementInfoQry.getElementInfosByProductId(mebProductId);
        	plusElements.addAll(tempElements);
        }
        
        /*IDataset plusPackages = CSAppCall.call("CS.PackageSVC.getMemberProductPackages", params);
        plusPackages = plusPackages.getData(0).getDataset("GROUPS");
        for (int i = 0, iSize = plusPackages.size(); i < iSize; i++)
        {
            IData plusPackage = plusPackages.getData(i);
            String packageId = plusPackage.getString("GROUP_ID");
            if(StringUtils.isBlank(packageId) || StringUtils.equals("-1", packageId))
            {
                continue;
            }
            params.put("PACKAGE_ID", packageId);
            params.put("GROUP_ID", packageId);
            // 2.1根据包查询元素
            IDataset tempElements = CSAppCall.call("CS.PackageSVC.getMemberPackageElements", params);
            plusElements.addAll(tempElements);
        }*/

        // 3,获取接口数据
        IDataset intfMebSvcDs = iData.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = iData.getDataset("INTF_MEB_DIS_DATASET");
        IDataset elementResults = new DatasetList();
        elementResults.addAll(intfMebSvcDs);
        elementResults.addAll(intfMebDiscntDs);

        int resultsiz = elementResults.size();
        for (int i = resultsiz - 1; i >= 0; i--)
        {
            IData elementResult = elementResults.getData(i);
            String elementId = elementResult.getString("ELEMENT_ID");
            String packageId = elementResult.getString("PACKAGE_ID");
            String m_productId = elementResult.getString("PRODUCT_ID");

            String elementForceTag = elementResult.getString("FORCE_TAG");
            String elementDefaultTag = elementResult.getString("DEFAULT_TAG");

            String packageForceTag = elementResult.getString("PACKAGE_FORCE_TAG");
            String packageDefTag = elementResult.getString("PACKAGE_DEFAULT_TAG");
            // 4,必须元素排除
            if (("1".equals(packageForceTag)|| "1".equals(packageDefTag)) && ("1".equals(elementForceTag) || "1".equals(elementDefaultTag)))
            {// 必须元素
                elementResults.remove(i);
            }
            else
            {
                IDataset isCanElments = DataHelper.filter(plusElements, "ELEMENT_ID=" + elementId + ",PACKAGE_ID=" + packageId);
                // 4.1 判断当前元素是否属于可选
                if (IDataUtil.isEmpty(isCanElments))
                    CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]包[" + packageId + "]不能为当前成员所能选择的元素或没有给该[" + staffId + "]工号分配[" + elementId + "]元素的权限!");
                // 5,处理时间
                ElementUtil.dealSelectedElementStartDateAndEndDate(elementResult, bookingDate, effectNow, eparchyCode);
            }
        }

        // 5,最后的结果为defaultElements+elementResults
        elementResults.addAll(defaultElements);
        iData.put("ELEMENT_INFO", elementResults);

    }

    private void combineElementParams(IData iData) throws Exception
    {

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

    // 子类重载
    protected void parseProductParams(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

    }

    private void preparElement(IData iData) throws Exception
    {
        IDataset intfMebSvcDs = new DatasetList();// 接口传入集团成员服务
        IDataset intfMebDiscntDs = new DatasetList();// 接口传入集团成员优惠

        iData.put("INTF_MEB_SVC_DATASET", intfMebSvcDs);
        iData.put("INTF_MEB_DIS_DATASET", intfMebDiscntDs);
    }

    private void preparElementParams(IData iData) throws Exception
    {

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

        // ElementParamsInfo
        actElementParamsInfo(iData);

        // productParamsInfo
        actProductParamInfo(iData);

        // payplanInfo
        actPayPlanInfo(iData);

        // after
        addDataAfter(iData);
    }
}
