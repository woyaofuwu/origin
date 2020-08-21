
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

/**
 * VPMN成员批量绑定功能费减免优惠
 * 
 * @author liuzz
 */
public class BatChangeVpnJianMianDiscnt extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "CS.ChangeMemElementSvc.changeMemElement";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.ChangeMemberDis);
        svcName = SERVICE_NAME;
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        // 1 获取要变更资费

        String userIdA = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userIdA);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serialNumber);
        chkMemberUCABySerialNumber(inparam);

        if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serialNumber);
        }
        // 资费服务信息处理
        String discntA = IDataUtil.chkParam(condData, "DISCNT_CODE");// 现在要变的优惠
        dealMemberDiscntA(discntA);

        // 产品参数信息
        IData productParam = new DataMap();
        IData vpnData = UserVpnInfoQry.getMemberVpnByUserId(getMebUcaData().getUserId(), userIdA, getMebUcaData().getUserEparchyCode());
        String shortCode = vpnData.getString("SHORT_CODE");
        productParam.put("SHORT_CODE", shortCode);
        productParam.put("OLD_SHORT_CODE", shortCode);
        productParam.put("OUT_PROV_DISCNT", discntA);

        IDataset productParamDataset = new DatasetList();
        buildProductParam(batData.getString("PRODUCT_ID"), productParam, productParamDataset);

        batData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

    public void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);

        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebChg");

        ruleData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        ruleData.put("TRADE_TYPE_CODE", batData.getString("TRADE_TYPE_CODE"));
        ruleData.put("CUST_ID", condData.getString("CUST_ID"));// 集团客户标识
        ruleData.put("USER_ID", condData.getString("USER_ID"));// 集团用户标识
        ruleData.put("USER_ID_B", getMebUcaData().getUserId());// 成员用户标识
        ruleData.put("BRAND_CODE_B", getMebUcaData().getBrandCode());// 成员用户品牌
        ruleData.put("EPARCHY_CODE_B", getMebUcaData().getUser().getEparchyCode());// 成员归属地州
        ruleData.put("CHECK_TAG", condData.getString("CHECK_TAG", "-1"));
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        // TODO Auto-generated method stub
        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("IF_CENTRETYPE", condData.getString("IF_CENTRETYPE", ""));
        svcData.put("ELEMENT_INFO", condData.getDataset("DISCNT"));
        svcData.put("PRODUCT_PARAM_INFO", batData.getDataset("PRODUCT_PARAM_INFO"));
    }

    /**
     * 处理成员资费与服务
     * 
     * @author tengg
     * @param pd
     * @param DiscntCodeA
     * @param td
     * @throws Exception
     */
    public void dealMemberDiscntA(String DiscntCodeA) throws Exception
    {

        String user_id_b = getMebUcaData().getUserId(); // 成员user_id
        String user_id_a = IDataUtil.chkParam(condData, "USER_ID");// 集团用户编码

        IDataset groupDiscntInfo = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(user_id_a, null);
        if (null == groupDiscntInfo || groupDiscntInfo.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_669);// 成员所依赖的集团没有订购成员资费包，业务不能继续!
        }
        IDataset DISCNT = new DatasetList();
        IData discntData = new DataMap();

        for (int i = 0; i < groupDiscntInfo.size(); i++)
        {
            IData dicntInfo = (IData) groupDiscntInfo.get(i);
            if (DiscntCodeA.equals(dicntInfo.getString("ELEMENT_ID")))
            {
                discntData.put("ELEMENT_ID", DiscntCodeA);
                discntData.put("ELEMENT_TYPE_CODE", dicntInfo.getString("ELEMENT_TYPE_CODE"));
                discntData.put("USER_ID", user_id_b);
                discntData.put("USER_ID_A", user_id_a);
                discntData.put("PRODUCT_ID", dicntInfo.getString("PRODUCT_ID"));
                discntData.put("PACKAGE_ID", dicntInfo.getString("PACKAGE_ID"));
                discntData.put("STATE", "ADD");
                discntData.put("MODIFY_TAG", "0");
                discntData.put("START_DATE", SysDateMgr.getSysTime());
                discntData.put("END_DATE", dicntInfo.getString("END_DATE"));
                discntData.put("INST_ID", SeqMgr.getInstId());
            }
        }
        if (null == discntData || discntData.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_666, DiscntCodeA);// 成员所依赖的集团没有订购成员资费包中的【"+discntCodeA+"】资费套餐，业务不能继续!
        }
        // 查询成员订购的VPMN资费
        IDataset memberDiscntOrder = UserDiscntInfoQry.getUserProductDis(user_id_b, user_id_a);
        // 分散账期修改 获取成员账期信息
        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(user_id_b);
        if (mebUserAcctDay == null || mebUserAcctDay.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_657, user_id_b);// 服务号码[]用户账期信息不存在!
        }

        for (int i = 0; i < memberDiscntOrder.size(); i++)
        {
            IData dicntInfo = (IData) memberDiscntOrder.get(i);
            String discntCode = dicntInfo.getString("ELEMENT_ID");
            if (DiscntCodeA.equals(discntCode))
            {
                CSAppException.apperr(GrpException.CRM_GRP_665, DiscntCodeA);// 成员用户已经存在【%s】资费套餐，不需要再变更！
            }
        }

        discntData.put("DIVERSIFY_ACCT_TAG", "1");
        DISCNT.add(discntData);

        // 查询成员订购的服务资费
        IDataset memberSvcOrder = UserSvcInfoQry.getUserProductSvc(user_id_b, user_id_a, null);

        if (null == memberSvcOrder || memberSvcOrder.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_667);// 成员没有订购VPMN相应的服务包，业务不能继续！
        }
        IDataset SVC = new DatasetList();
        for (int i = 0; i < memberSvcOrder.size(); i++)
        {
            IData svcData = (IData) memberSvcOrder.get(i);
            if ("860".equals(svcData.getString("SERVICE_ID")))
            {
                svcData.put("STATE", "MODI");
                svcData.put("ELEMENT_ID", "860");
                // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                svcData.put("DIVERSIFY_ACCT_TAG", "1");
                SVC.add(svcData);
            }
        }

        if (null == SVC || SVC.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_668, "860", "860");// 成员没有订购VPMN成员服务包中的860服务或者860服务已失效，业务不能继续！
        }
        condData.put("DISCNT", DISCNT);
        condData.put("SVC", SVC);

    }
}
