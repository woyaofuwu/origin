
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class BatChgVpmnShortCodeSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "SS.ChgVpmnShortCodeSVC.crtTrade";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

        // batData.put(BIZ_CTRL_TYPE, BizCtrlType.ChangeMemberDis);
        // condData.put("PRODUCT_ID", "6200"); // j2ee 测试写死
    }

    /**
     * 作用： 批量校验其他信息(子类实现)
     * 
     * @throws Exception
     */
    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", grpUserId);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkMemberUCAAndStateBySerialNumber(inparam);

        // 判断是否该集团用户成员
        String productId = getGrpUcaData().getProductId();
        String memUserId = getMebUcaData().getUserId();
        String relaTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
        String preFix = IDataUtil.chkParam(condData, "PRE_FIX");// 短号前缀
        String newShortCode = preFix + IDataUtil.chkParam(batData, "DATA1");// 短号
        batData.put("NEW_SHORT_CODE", newShortCode);
        IData uuInfo = RelaUUInfoQry.getRelaByPK(grpUserId, memUserId, relaTypeCode);
        if (IDataUtil.isEmpty(uuInfo))
        {
            // j2ee common.error("用户不是此集团用户的成员[用户id=" + userId_a + "成员用户id=" + userId_b + "关系id=" + relaTypeCode + "]");
            CSAppException.apperr(GrpException.CRM_GRP_709, grpUserId, memUserId, relaTypeCode);
        }
        // 验证短号码
        IData data = new DataMap();
        data.put("SHORT_CODE", newShortCode);
        data.put("USER_ID_A", grpUserId);
        data.put("EPARCHY_CODE", getMebUcaData().getUserEparchyCode());
        IData reData = VpnUnit.shortCodeValidateVpn(data);
        if (IDataUtil.isNotEmpty(reData))
        {
            if ("false".equals(reData.getString("RESULT")))
            {
                String err = reData.getString("ERROR_MESSAGE");
                CSAppException.apperr(VpmnUserException.VPMN_USER_186, err);
            }
        }
        //
        String oldshortcode = uuInfo.getString("SHORT_CODE", "");
        batData.put("OLD_SHORT_CODE", oldshortcode);
        IData productparams = new DataMap();
        productparams.put("SHORT_CODE", newShortCode);
        productparams.put("OLD_SHORT_CODE", oldshortcode);
        if (oldshortcode.equals(newShortCode))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_187);
        }
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
        String memUserId = getMebUcaData().getUserId();
        String productId = getGrpUcaData().getProductId();
        svcData.put("BATCH_OPER_TYPE", "VPMNCHANGEDSHORTCODE");
        svcData.put("USER_ID", grpUserId);
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("MEM_USER_ID", memUserId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("NEW_SHORT_CODE", batData.getString("NEW_SHORT_CODE"));
        svcData.put("OLD_SHORT_CODE", batData.getString("OLD_SHORT_CODE"));
        svcData.put("SMS_FLAG", batData.getString("SMS_FLAG"));
        svcData.put("MEM_ROLE_B", IDataUtil.chkParam(condData, "ROLE_CODE_B"));

        // 构建产品参数信息
        IData productParam = new DataMap();
        productParam.put("SHORT_CODE", batData.getString("NEW_SHORT_CODE"));
        productParam.put("OLD_SHORT_CODE", batData.getString("OLD_SHORT_CODE"));
        IDataset productParamDataset = new DatasetList();
        buildProductParam(productId, productParam, productParamDataset);
        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }
}
