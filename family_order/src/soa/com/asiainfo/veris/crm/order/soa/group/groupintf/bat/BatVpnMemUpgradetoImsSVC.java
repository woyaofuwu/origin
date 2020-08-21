
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatVpnMemUpgradetoImsSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "SS.BatVpnMemUpgradeSVC.crtTrade";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

    }

    /**
     * 作用： 批量校验其他信息(子类实现)
     * 
     * @throws Exception
     */
    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        batData.put("TRADE_TYPE_CODE", "2529");

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        // 校验集团用户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", condData.getString("USER_ID"));
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        chkMemberUCABySerialNumber(inparam);

        // 判断服务号码状态
        if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serialNumber);
        }

    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "chk");
        // 集团信息
        ruleData.put("PRODUCT_ID", getGrpUcaData().getProductId());
        ruleData.put("CUST_ID", getGrpUcaData().getCustId());
        ruleData.put("USER_ID", getGrpUcaData().getUserId());
        // 成员信息
        ruleData.put("EPARCHY_CODE_B", getMebUcaData().getUser().getEparchyCode());
        ruleData.put("USER_ID_B", getMebUcaData().getUserId());
        ruleData.put("BRAND_CODE_B", getMebUcaData().getBrandCode());
        ruleData.put(Route.ROUTE_EPARCHY_CODE, getMebUcaData().getUser().getEparchyCode());
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID")); // 集团userid
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID")); // 集团产品ID
        svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER")); // 成员sn
        svcData.put("MEM_DISCNT_CODE", IDataUtil.getMandaData(condData, "DISCNT")); // 成员 选定资费
    }

}
