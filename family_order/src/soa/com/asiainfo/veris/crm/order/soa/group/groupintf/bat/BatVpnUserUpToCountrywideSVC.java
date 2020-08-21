
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatVpnUserUpToCountrywideSVC extends GroupBatService
{
    private static final String SERVICE_NAME = "SS.VpnUserUpToCountrywideSVC.crtTrade";

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
        batData.put("TRADE_TYPE_CODE", "3031");

        // 校验集团用户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", condData.getString("USER_ID"));
        chkGroupUCAByUserId(inparam);

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
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));
        svcData.put(Route.USER_EPARCHY_CODE, getGrpUcaData().getUser().getEparchyCode());
        svcData.put("productParam", IDataUtil.getMandaData(condData, "productParam"));
        svcData.put("GRP_PACKAGE_INFO", IDataUtil.getMandaData(condData, "GRP_PACKAGE")); // 集团成员定制
        svcData.put("OLD_801_FLAG", IDataUtil.getMandaData(condData, "OLD_801_FLAG"));
        svcData.put("HAS_DEL_801", IDataUtil.getMandaData(condData, "HAS_DEL_801"));
        svcData.put("VPN_SCARE_CODE", IDataUtil.getMandaData(condData, "VPN_SCARE_CODE"));
        svcData.put("HAS_VPN_SCARE", IDataUtil.getMandaData(condData, "HAS_VPN_SCARE"));
    }

}
