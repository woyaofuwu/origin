
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class GrpBatChangeVpnSpecDiscntSVC extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "SS.ChangeVpnSpecDiscntSVC.crtTrade";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        checkParam(batData);
        String memSn = batData.getString("SERIAL_NUMBER");
        String relationTypeCode = "20";
        String grpProductId = "8000";
        IDataset dsUu = RelaUUInfoQry.getRelationUusBySnBTypeCode(memSn, relationTypeCode);
        if (IDataUtil.isEmpty(dsUu))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_169, memSn);
        }
        String grpUIdA = "";
        for (int i = 0; i < dsUu.size(); i++)
        {
            IData dUu = dsUu.getData(i);
            String uIdA = dUu.getString("USER_ID_A");
            IDataset ds = UserProductInfoQry.getUserProductByUserIdProductId(uIdA, grpProductId);
            if (IDataUtil.isNotEmpty(ds))
            {

                grpUIdA = uIdA;
            }
        }
        if (StringUtils.isBlank(grpUIdA))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_169, memSn);
        }
        // 校验集团用户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", grpUIdA);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", memSn);

        chkMemberUCABySerialNumber(inparam);

    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebOrder");
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
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("PRODUCT_ID", "8000");
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "false"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));
        svcData.put("DISCNT_CODE", condData.getString("DISCNT_CODE"));

    }

    /**
     * 校验参数必填
     * 
     * @param batData
     * @throws Exception
     */
    public void checkParam(IData batData) throws Exception
    {
        IDataUtil.chkParam(condData, "DISCNT_CODE");

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");

    }
}
