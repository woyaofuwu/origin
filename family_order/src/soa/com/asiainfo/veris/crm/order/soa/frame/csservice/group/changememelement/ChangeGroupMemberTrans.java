
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;

public class ChangeGroupMemberTrans implements ITrans
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
        // before
        preparElementParams(iData);

        // parse
        parseElementParams(iData);

        // combine
        combineElementParams(iData);
    }

    private void actProductParamInfo(IData iData) throws Exception
    {
        // before
        preparProductParams(iData);

        // parse
        parseProductParams(iData);

        // combine
        combineProductParams(iData);
    }

    private void addBaseDataAfter(IData iData) throws Exception
    {
        // 移除调临时对象
        // iData.remove("INTF_GRP_SVC_DATASET");
        // iData.remove("INTF_GRP_DIS_DATASET");
        // iData.remove("INTF_MEB_SVC_DATASET");
        // iData.remove("INTF_MEB_DIS_DATASET");

        // iData.remove("PRODUCT_PARAM");
        // iData.remove("LIST_INFOS");

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

        // iData.put(Route.USER_EPARCHY_CODE,iData.getString("TRADE_EPARCHY_CODE"));
        iData.put("OPER_TYPE", BizCtrlType.ChangeMemberDis); // 操作类型
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
        String sn = IDataUtil.chkParam(iData, "SERIAL_NUMBER");
        String userId = IDataUtil.chkParam(iData, "USER_ID");
        IData userInfos = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(userInfos))
            CSAppException.apperr(BofException.CRM_BOF_011);

        if (!StringUtils.equals(productId, userInfos.getString("PRODUCT_ID")))
            CSAppException.apperr(GrpException.CRM_GRP_713, "该集团用户没有受理过该产品,请先进行该产品的业务受理!");

    }

    // 每个省一样的
    private final void combineElement(IData iData) throws Exception
    {
        String productId = IDataUtil.chkParam(iData, "PRODUCT_ID");// 集团产品标识
        String grpUserId = IDataUtil.chkParam(iData, "USER_ID");
        String sn = IDataUtil.chkParam(iData, "SERIAL_NUMBER");// 成员sn
        IData mebInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(mebInfo))
            CSAppException.apperr(GrpException.CRM_GRP_713, "当前成员[" + sn + "]用户信息不存在!");

        String memUserId = mebInfo.getString("USER_ID");
        String eparchyCode = CSBizBean.getUserEparchyCode();
        // 1,成员必须的
        IData params = new DataMap();
        params.put("MEB_USER_ID", memUserId);
        params.put("GRP_USER_ID", grpUserId);
        params.put("PRODUCT_ID", productId);// 集团产品标识
        params.put("GRP_PRODUCT_ID", productId);// 集团产品标识

        boolean effectNow = iData.getBoolean("EFFECT_NOW", false);
        String bookingDate = iData.getString("PRODUCT_PRE_DATE", "");

        params.put("EFFECT_NOW", effectNow);
        params.put("EFFECT_NOW", effectNow);
        params.put("PRODUCT_PRE_DATE", bookingDate);

        // 2,已选元素(包括必选元素)
        params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
        IDataset defaultElements = CSAppCall.call("CS.SelectedElementSVC.getGrpMebChgElements", params);
        defaultElements = defaultElements.getData(0).getDataset("SELECTED_ELEMENTS");

        // 3,可选成员元素(包括必须的)(已兼容成员所对的集团定制)
        IDataset plusElements = new DatasetList();
        // params.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
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
            // 3.1根据包查询元素
            IDataset tempElements = CSAppCall.call("CS.PackageSVC.getMemberPackageElements", params);
            plusElements.addAll(tempElements);
        }*/

        // 4,处理可选的时间
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
            //String m_productId = elementResult.getString("PRODUCT_ID");

            String elementForceTag = elementResult.getString("FORCE_TAG");
            String elementDefaultTag = elementResult.getString("DEFAULT_TAG");

            //String packageForceTag = elementResult.getString("PACKAGE_FORCE_TAG");
            if ("1".equals(modifyTag) && ("1".equals(elementForceTag) || "1".equals(elementDefaultTag)))
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

        IDataset outDataset = iData.getDataset("ELEMENT_INFO");
        if (IDataUtil.isNotEmpty(outDataset))
            elementResults.addAll(outDataset);

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
        // actElementParamsInfo(iData);

        // productParamsInfo
        // actProductParamInfo(iData);

        // payplanInfo
        // actPayPlanInfo(iData);

        // after
        addDataAfter(iData);
    }
}
