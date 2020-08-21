
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatMebChgCancelRingSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "CS.ChangeMemElementSvc.changeMemElement";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

        batData.put(BIZ_CTRL_TYPE, BizCtrlType.ChangeMemberDis);
        condData.put("PRODUCT_ID", "6200"); // j2ee 测试写死
    }

    /**
     * 作用： 批量校验其他信息(子类实现)
     * 
     * @throws Exception
     */
    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String memSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        IData inparam = new DataMap();
        // 校验成员三户信息
        inparam.put("SERIAL_NUMBER", memSn);
        chkMemberUCABySerialNumber(inparam);

        // 校验成员是否为彩铃成员
        String memUserId = getMebUcaData().getUserId();
        String relaTypeCode = "26";
        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, relaTypeCode);
        if (IDataUtil.isEmpty(uuInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_708, memSn);
        }
        String grpUserId = uuInfos.getData(0).getString("USER_ID_A");
        // 校验集团用户信息
        inparam.clear();
        inparam.put("USER_ID", grpUserId);
        chkGroupUCAByUserId(inparam);
    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);
        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "chk");
        // 集团信息
        ruleData.put("PRODUCT_ID", getGrpUcaData().getProductId());
        ruleData.put("CUST_ID", getGrpUcaData().getCustId());
        ruleData.put("USER_ID", getGrpUcaData().getUserId());
        // 成员信息
        ruleData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        ruleData.put("USER_ID_B", getMebUcaData().getUserId());
        ruleData.put("BRAND_CODE_B", getMebUcaData().getBrandCode());
        ruleData.put("EPARCHY_CODE_B", getMebUcaData().getUser().getEparchyCode());
        ruleData.put("PRODUCT_ID_B", getMebUcaData().getProductId());
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        String grpUserId = getGrpUcaData().getUserId();
        String productId = getGrpUcaData().getProductId();
        String cancleLing = IDataUtil.getMandaData(condData, "cancelLing");

        svcData.put("USER_ID", grpUserId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        // 构建产品参数信息
        IData productParam = new DataMap();
        productParam.put("CANCEL_LING", cancleLing);
        IDataset productParamDataset = new DatasetList();
        buildProductParam(productId, productParam, productParamDataset);
        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }
}
