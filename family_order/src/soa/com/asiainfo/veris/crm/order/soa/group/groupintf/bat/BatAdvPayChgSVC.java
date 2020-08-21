
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatAdvPayChgSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "SS.AdvPayChgSVC.crtTrade";

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
        // condData.put("PRODUCT_ID", "8000"); // j2ee 测试写死
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
        String operType = IDataUtil.chkParam(condData, "OPER_TYPE");// 1:新增,0:删除

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", grpUserId);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkMemberUCAAndStateBySerialNumber(inparam);

        chkMemberInfo(operType);
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
        String acctId = IDataUtil.chkParam(condData, "ACCT_ID");// 集团帐户ID
        String operType = IDataUtil.chkParam(condData, "OPER_TYPE");// 1:新增,0:删除
        svcData.put("USER_ID", grpUserId);
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("PRODUCT_ID", productId);
        svcData.put("ACCT_ID", acctId);// 集团帐户ID
        svcData.put("OPER_TYPE", operType);
        svcData.put("PAYITEM_CODE", IDataUtil.chkParam(condData, "PAYITEM_CODE"));
        svcData.put("LIMIT_TYPE", IDataUtil.chkParam(condData, "LIMIT_TYPE"));
        svcData.put("LIMIT", IDataUtil.chkParam(condData, "LIMIT7"));
        svcData.put("START_CYCLE_ID", IDataUtil.chkParam(condData, "START_CYCLE"));
        svcData.put("END_CYCLE_ID", IDataUtil.chkParam(condData, "END_CYCLE"));
        svcData.put("COMPLEMENT_TAG", IDataUtil.chkParam(condData, "COMPLEMENT_TAG"));

    }

    /**
     * 检查成员资料
     * 
     * @param operType
     * @throws Exception
     */

    public void chkMemberInfo(String operType) throws Exception
    {
        String memUserId = getMebUcaData().getUserId();
        String memAcctId = getMebUcaData().getAcctId();
        IData info = new DataMap();
        info.put("USER_ID", memUserId);
        if ("1".equals(operType))
        {
            IDataset purchase = UserSaleActiveInfoQry.queryPurchaseInfo(info);

            if (IDataUtil.isNotEmpty(purchase))
            {
                for (int i = 0; i < purchase.size(); i++)
                {
                    IData data = purchase.getData(i);
                    IDataset comInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "9987", data.getString("PRODUCT_ID"), "0898");
                    if (IDataUtil.isNotEmpty(comInfos))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_655);
                    }
                }
            }
        }

        IData payrelationInfo = UcaInfoQry.qryDefaultPayRelaByUserId(memUserId);
        if (IDataUtil.isEmpty(payrelationInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_735);

        }
        // 一卡多号 rela=30和97
        IDataset relationInfos = RelaUUInfoQry.queryRelationInfo(memUserId);
        if (IDataUtil.isNotEmpty(relationInfos))
        {
            IData relationInfo = (IData) relationInfos.get(0);
            payrelationInfo = UcaInfoQry.qryDefaultPayRelaByUserId(relationInfo.getString("USER_ID_A"));
            String acctIda = ((IData) payrelationInfo.get(0)).getString("ACCT_ID"); // 主卡成员默认付费账户id
            if (acctIda.equals(memAcctId)) // 如果成员账户与主卡成员账户一样则报错
            {
                CSAppException.apperr(GrpException.CRM_GRP_656);
            }
        }
        // add by lixiuyu@20111110 统一付费业务副卡的号码办理集团统付业务时（包括所有的集团统付办理途径均要限制），要进行提示限制办理
        IDataset relaDatas = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(memUserId, "56", "2");
        if (IDataUtil.isNotEmpty(relaDatas))
        {
            CSAppException.apperr(GrpException.CRM_GRP_635);
        }
    }
}
