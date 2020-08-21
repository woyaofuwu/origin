
package com.asiainfo.veris.crm.order.web.group.destroygroupuserforowe;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.fee.UserFeeIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo.UserProductElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.packageelement.PackageElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class DestroyGroupUserForOwe extends GroupBasePage
{
    /**
     * 提交方法
     *
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        // 业务提交前校验
        onSubmitBaseTradeCheck(cycle);

        IData condData = getData();

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put(Route.USER_EPARCHY_CODE, condData.getString(Route.USER_EPARCHY_CODE));
        svcData.put("REASON_CODE", condData.getString("REASON_CODE"));
        svcData.put("REMARK", condData.getString("REMARK"));
        svcData.put("DESTROY_ATTR", "OWEFEE"); // 欠费注销
        svcData.put("AUDIT_STAFF_ID", condData.getString("AUDIT_STAFF_ID", ""));


        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "CS.DestroyGroupUserSvc.destroyGroupUser", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }

    /**
     * 提交前校验
     *
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTradeCheck(IRequestCycle cycle) throws Exception
    {
        // 1. 判断产品是否提供此功能
        IData condData = getData();

        IDataset productElements = UserProductElementInfoIntfViewUtil.qryGrpUserElementInfosByUserId(this, condData.getString("USER_ID"));

        IDataset dctDataset = DataHelper.filter(productElements, "ELEMENT_TYPE_CODE=D");
        for (int j = 0, jSize = dctDataset.size(); j < jSize; j++) {
            String discntId = dctDataset.getData(j).getString("ELEMENT_ID");
            String packageId = dctDataset.getData(j).getString("PACKAGE_ID");
            String cancelTag = PackageElementInfoIntfViewUtil.qryCancelTagStrByPackageIdAndElementIdElementTypeCode(this, packageId, discntId, "D");
            if (!"".equals(cancelTag) && "4".equals(cancelTag)) {
                CSViewException.apperr(GrpException.CRM_GRP_890, discntId);
            }
        }
    }

    /**
     * 查询集团用户信息
     *
     * @param cycle
     * @throws Exception
     */
    public void qryGrpUser(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String serialNumber = condData.getString("SERIAL_NUMBER");

        // 查询集团用户三户信息
        IData ucaData = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, serialNumber);

        IData userData = ucaData.getData("GRP_USER_INFO");
        IData custData = ucaData.getData("GRP_CUST_INFO");
        IData acctData = ucaData.getData("GRP_ACCT_INFO");

        String userId = userData.getString("USER_ID");
        String custId = userData.getString("CUST_ID");
        String brandCode = userData.getString("BRAND_CODE", "");

        // 查询集团客户核心资料信息
        IData customerData = UCAInfoIntfViewUtil.qryGrpCustomerInfoByCustId(this, custId);

        custData.put("PSPT_TYPE_NAME", customerData.getString("PSPT_TYPE_NAME"));
        custData.put("PSPT_ID", customerData.getString("PSPT_ID"));

        // 判断是否可注销
        String paraCode1 = ""; // "ADCG".equals(brandCode) ? "csDestroyGrpNow@0011" : "csDestroyGrpNow@0012";
        if("ADCG".equals(brandCode)){
            paraCode1 = "csDestroyGrpNow@0011";
        } else if("VPMR".equals(brandCode)){
            paraCode1 = "csDestroyGrpNow@0031";
        } else if("VPEN".equals(brandCode)){
            paraCode1 = "csDestroyGrpNow@0032";
        } else if("VPMB".equals(brandCode)){
            paraCode1 = "csDestroyGrpNow@0033";
        } else if("VPMN".equals(brandCode)){
            paraCode1 = "csDestroyGrpNow@0035";
        } else {
            paraCode1 = "csDestroyGrpNow@0012";
        }
                
        // 查询配置信息
        IDataset commparaList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCode(this, "CSM", "1111", "TYPECODE", paraCode1, getTradeEparchyCode());

        boolean isDestroy = false; // 是否可注销, 默认不可注销

        if (IDataUtil.isNotEmpty(commparaList))
        {
            for (int i = 0, row = commparaList.size(); i < row; i++)
            {
                if (brandCode.equals(commparaList.getData(i).getString("PARA_CODE4", "")))
                {
                    isDestroy = true;
                    break;
                }
            }
        }

        // 不可注销
        if (isDestroy == false)
        {
            CSViewException.apperr(GrpException.CRM_GRP_633, brandCode);
        }

        // 查询用户欠费信息
        IData oweFeeData = UserFeeIntfViewUtil.qryGrpUserOweFeeInfo(this, userId);
        double lastOweFee = Double.valueOf(oweFeeData.getString("LAST_OWE_FEE"));
        double relaFee = Double.valueOf(oweFeeData.getString("REAL_FEE"));
        double acctBalance = Double.valueOf(oweFeeData.getString("ACCT_BALANCE"));

        acctData.put("OWE_FEE", String.format("%1$3.2f", (lastOweFee + relaFee) / 100.0));
        acctData.put("ACCT_BALANCE", String.format("%1$3.2f", acctBalance / 100.0));

        // 查询用户预存信息
        IData foregiftData = UserFeeIntfViewUtil.qryGrpUserForegiftInfo(this, userId);

        acctData.put("FOREGIFT", foregiftData.getString("FOREGIFT"));

        // 设置返回值
        setUserData(userData);
        setCustData(custData);
        setAcctData(acctData);
    }

    public abstract void setAcctData(IData acctData);

    public abstract void setCondition(IData condition);

    public abstract void setCustData(IData custData);

    public abstract void setUserData(IData userData);
}
