
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
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
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSn;

public class CreateGroupUserTrans implements ITrans
{
    private void actAcctInfo(IData idata) throws Exception
    {
        // 1-拼基础数据acct_id,如果没有传,则以custId下面的第一个acctId做为默认付费账户
        String acctId = idata.getString("ACCT_ID", "");
        if (!StringUtils.isEmpty(acctId))
        {
            IData acctInfo = UcaInfoQry.qryAcctInfoByAcctIdForGrp(acctId);
            if (IDataUtil.isEmpty(acctInfo))
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_105);
        }
        else
        {// 以custId下面的第一个acctId做为默认付费账户
            IDataset accinfos = AcctInfoQry.getAcctInfoByCustIdForGrp(idata.getString("CUST_ID"));
            if (IDataUtil.isEmpty(accinfos))
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_141, idata.getString("CUST_ID"));
            acctId = accinfos.getData(0).getString("ACCT_ID");
            idata.put("ACCT_ID", acctId);
        }
    }

    private void actBaseInfo(IData iData) throws Exception
    {
        addSubInfoDataBefore(iData);

        addBaseInfoDataBefore(iData);
    }

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

    private void actPayPlanInfo(IData iData) throws Exception
    {

        /*
         * String userIdA = IDataUtil.chkParam(iData, "USER_ID");//集团用户标识 IData params = new DataMap();
         * params.put("USER_ID",userIdA); params.put("USER_ID_A", "-1"); IDataset plans =
         * CSAppCall.call("CS.UserPayPlanInfoQrySVC.getPayPlanInfosByUserIdForGrp", params);
         * if(IDataUtil.isEmpty(plans)) CSAppException.apperr(GrpException.CRM_GRP_713,"没有选择付费方式!"); String playTypeCode
         * = plans.getData(0).getString("PLAN_TYPE_CODE");
         */
        IDataset planDataset = new DatasetList();
        IData pPlan = new DataMap();
        pPlan.put("PLAN_NAME", "个人付费");
        pPlan.put("PLAN_TYPE", "P");
        pPlan.put("PLAN_TYPE_DESC", "个人付费");
        pPlan.put("PLAN_DESC", "个人付费");
        pPlan.put("PLAN_TYPE_CODE", "P");

        planDataset.add(pPlan);

        iData.put("PLAN_INFO", planDataset);
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

    private void actResInfo(IData iData) throws Exception
    {

        // 把短号加入资源中
        IDataset idsRes = iData.getDataset("RES_INFO", new DatasetList());

        IData idRes = new DataMap();
        idRes.put("CHECKED", "true");
        idRes.put("MODIFY_TAG", "0");
        idRes.put("RES_TYPE_CODE", "L");
        idRes.put("RES_TYPE", "集团产品编码");
        idRes.put("RES_CODE", iData.getString("SERIAL_NUMBER"));

        idsRes.add(idRes);

        iData.put("RES_INFO", idsRes);
    }

    private void addBaseDataAfter(IData iData) throws Exception
    {
        iData.remove("GROUP_ID");
        // 移除调临时对象
        iData.remove("INTF_GRP_SVC_DATASET");
        iData.remove("INTF_GRP_DIS_DATASET");
        iData.remove("INTF_MEB_SVC_DATASET");
        iData.remove("INTF_MEB_DIS_DATASET");

        iData.remove("PRODUCT_PARAM");

        iData.remove("DISCNT_CODE");

        iData.remove("SERVICE_CODE");
    }

    private void addBaseDataBefore(IData iData) throws Exception
    {
        iData.put("USER_ID", iData.getString("USER_ID_A", iData.getString("USER_ID")));
        iData.remove("USER_ID_A");
        // iData.put(Route.USER_EPARCHY_CODE,iData.getString("TRADE_EPARCHY_CODE"));
        iData.put("OPER_TYPE", BizCtrlType.CreateUser);// 操作类型
    }

    private void addBaseInfoDataBefore(IData iData) throws Exception
    {
        // TODO Auto-generated method stub

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

    protected void addSubInfoDataBefore(IData iData) throws Exception
    {
        IData genSnData = new DataMap();

        genSnData.put("PRODUCT_ID", iData.getString("PRODUCT_ID"));

        String sn = genGrpSn(genSnData);

        iData.put("SERIAL_NUMBER", sn);
    }

    protected void checkRequestData(IData iData) throws Exception
    {
        String groupId = IDataUtil.chkParam(iData, "GROUP_ID");
        String productId = IDataUtil.chkParam(iData, "PRODUCT_ID");

        IData grpInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if (IDataUtil.isEmpty(grpInfo))
            CSAppException.apperr(GrpException.CRM_GRP_472, groupId);

        String custId = grpInfo.getString("CUST_ID");
        String admNum = grpInfo.getString("GROUP_MGR_SN");
        iData.put("CUST_ID", custId);
        iData.put("GROUP_MGR_SN", admNum);

    }

    // 每个省一样的
    private final void combineElement(IData iData) throws Exception
    {
        String productId = IDataUtil.chkParam(iData, "PRODUCT_ID");// 集团产品标识
        String eparchyCode = CSBizBean.getUserEparchyCode();

        IDataset intfGrpSvcDs = iData.getDataset("INTF_GRP_SVC_DATASET");
        IDataset intfGrpDiscntDs = iData.getDataset("INTF_GRP_DIS_DATASET");
        IDataset intfMebSvcDs = iData.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = iData.getDataset("INTF_MEB_DIS_DATASET");

        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);// 集团产品标识

        boolean effectNow = iData.getBoolean("EFFECT_NOW", false);
        String bookingDate = iData.getString("PRODUCT_PRE_DATE", "");
        params.put("EFFECT_NOW", effectNow);
        params.put("PRODUCT_PRE_DATE", bookingDate);

        // 1,必须元素
        params.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset defaultElements = CSAppCall.call("CS.SelectedElementSVC.getGrpUserOpenElements", params);
        defaultElements = defaultElements.getData(0).getDataset("SELECTED_ELEMENTS");

        IDataset elementResults = new DatasetList();
        elementResults.addAll(intfGrpSvcDs);
        elementResults.addAll(intfGrpDiscntDs);

        // 2,集团可选元素(包括必须的)(已兼容成员所对的集团定制)
        IDataset plusElements = new DatasetList();
        IDataset tempElements = UProductElementInfoQry.getElementInfosByProductId(productId);
        plusElements.addAll(tempElements);
        /*IDataset plusPackages = UPackageInfoQry.getPackagesByProductId(productId);
        for (int i = 0, iSize = plusPackages.size(); i < iSize; i++)
        {
            IData plusPackage = plusPackages.getData(i);
            String packageId = plusPackage.getString("PACKAGE_ID");
            params.put("PACKAGE_ID", packageId);
            // 2.1根据包查询元素
            IDataset tempElements = CSAppCall.call("CS.PackageSVC.getPackageElements", params);
            if(IDataUtil.isNotEmpty(tempElements))
            {
            	for(int j=0; j<tempElements.size(); j++)
            	{
            		tempElements.getData(j).put("PACKAGE_ID", packageId);
            	}
            }
            plusElements.addAll(tempElements);
        }*/

        // 遍历接口
        int resultsiz = elementResults.size();
        for (int i = resultsiz - 1; i >= 0; i--)
        {
            // for(int i=0,iSize=elementResults.size();i<iSize;i++){

            IData elementResult = elementResults.getData(i);
            String elementId = elementResult.getString("ELEMENT_ID");
            String packageId = elementResult.getString("PACKAGE_ID");
            String m_productId = elementResult.getString("PRODUCT_ID");

            String elementForceTag = elementResult.getString("FORCE_TAG");
            String elementDefaultTag = elementResult.getString("DEFAULT_TAG");

            String packageForceTag = elementResult.getString("PACKAGE_FORCE_TAG");
            // 4,必须元素排除
            if ("1".equals(packageForceTag) && ("1".equals(elementForceTag) || "1".equals(elementDefaultTag)))
            {// 必须元素
                elementResults.remove(i);
            }
            else
            {
                IDataset isCanElments = DataHelper.filter(plusElements, "ELEMENT_ID=" + elementId + ",PACKAGE_ID=" + packageId);
                // 4.1 判断当前元素是否属于可选
                if (IDataUtil.isEmpty(isCanElments))
                    CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]包[" + packageId + "]不能为当前成员所能选择的元素!");
                // 5,处理时间
                ElementUtil.dealSelectedElementStartDateAndEndDate(elementResult, bookingDate, effectNow, eparchyCode);
            }

        }
        elementResults.addAll(defaultElements);

        // 5,成员部分的需要放到定制里面处理,见GrpPackageAction.java

        iData.put("ELEMENT_INFO", elementResults);
    }

    private void combineElementParams(IData iData) throws Exception
    {

    }

    private void combineGrpPackage(IData data) throws Exception
    {
        String grpProductId = IDataUtil.chkParam(data, "PRODUCT_ID");// 集团产品标识
        String eparchyCode = CSBizBean.getUserEparchyCode();
        String useTag = ProductCompInfoQry.getUseTagByProductId(grpProductId);

        if (!useTag.equals(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue()))
            return;

        // 1, 根据集团产品标识查询成员必须包
        String eparchycode = CSBizBean.getUserEparchyCode();
        boolean effectNow = data.getBoolean("EFFECT_NOW", false);
        String bookingDate = data.getString("PRODUCT_PRE_DATE", "");

        // 必选
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", grpProductId);
        inparam.put("EFFECT_NOW", effectNow);
        inparam.put("PRODUCT_PRE_DATE", bookingDate);
        inparam.put(Route.USER_EPARCHY_CODE, eparchyCode);
        IDataset defaultElementList = CSAppCall.call("CS.ProductInfoQrySVC.getMebProductForceElements", inparam);

        // 2,可选成员元素(包括必须的)(已兼容成员所对的集团定制)
        IDataset plusElements = new DatasetList();

        String productIdB = UProductMebInfoQry.getMemberMainProductByProductId(grpProductId);
        IDataset disPlusDataset = UDiscntInfoQry.getDiscntByProduct(productIdB);
        IDataset svcPlusDataset = USvcInfoQry.getSvcByProduct(productIdB);
        plusElements.addAll(disPlusDataset);
        plusElements.addAll(svcPlusDataset);

        IDataset intfMebSvcDs = data.getDataset("INTF_MEB_SVC_DATASET");// 接口传入集团成员服务
        IDataset intfMebDiscntDs = data.getDataset("INTF_MEB_DIS_DATASET");// 接口传入集团成员优惠

        // 3,获取接口数据
        IDataset elementResults = new DatasetList();
        elementResults.addAll(intfMebSvcDs);
        elementResults.addAll(intfMebDiscntDs);

        for (int i = 0, iSize = elementResults.size(); i < iSize; i++)
        {
            IData elementResult = elementResults.getData(i);
            String elementId = elementResult.getString("ELEMENT_ID");
            String packageId = elementResult.getString("PACKAGE_ID");
            String m_productId = elementResult.getString("PRODUCT_ID");

            String elementForceTag = elementResult.getString("FORCE_TAG");
            String elementDefaultTag = elementResult.getString("DEFAULT_TAG");

            String packageForceTag = elementResult.getString("PACKAGE_FORCE_TAG");
            // 4,必须元素排除
            if ("1".equals(packageForceTag) && ("1".equals(elementForceTag) || "1".equals(elementDefaultTag)))
            {// 必须元素
                elementResults.remove(i);
            }
            IDataset isCanElments = DataHelper.filter(plusElements, "ELEMENT_ID=" + elementId + ",PACKAGE_ID=" + packageId);
            // 4.1 判断当前元素是否属于可选
            if (IDataUtil.isEmpty(isCanElments))
                CSAppException.apperr(GrpException.CRM_GRP_713, "该元素[" + elementId + "]包[" + packageId + "]不能为当前成员所能选择的元素!");
            // 5,处理时间
            ElementUtil.dealSelectedElementStartDateAndEndDate(elementResult, bookingDate, effectNow, eparchyCode);

        }

        // 最后的结果为:intfMebSvc+mebForceSvcDs+mebForcePlatSvcDs,mebForceDisDs+intfMebDiscntDs
        elementResults.addAll(defaultElementList);
        data.put("GRP_PACKAGE_INFO", elementResults);
    }

    private void combineProductParams(IData iData) throws Exception
    {

    }

    private String genGrpSn(IData idata) throws Exception
    {
        IData grpSnData = new DataMap();
        String serialNumber = "";
        for (int i = 0; i < 10; i++)
        {
            grpSnData = GrpGenSn.genGrpSn(idata);

            serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

            if (StringUtils.isEmpty(serialNumber))
            {
                break;
            }

            IData data = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);

            if (IDataUtil.isEmpty(data))
            {
                break;
            }
        }
        // 避免服务号码的重复 add end
        return serialNumber;

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

        // base
        actBaseInfo(iData);

        // elementInfo
        actElementInfo(iData);

        // actGrpPackage
        actGrpPackage(iData);

        // ElementParamsInfo
        actElementParamsInfo(iData);

        // productParamsInfo
        actProductParamInfo(iData);

        // res
        actResInfo(iData);

        // actAcctInfo
        actAcctInfo(iData);

        // payplanInfo
        actPayPlanInfo(iData);

        // after
        addDataAfter(iData);
    }
}
