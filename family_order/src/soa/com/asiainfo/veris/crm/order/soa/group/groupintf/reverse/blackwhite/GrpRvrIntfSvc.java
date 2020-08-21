
package com.asiainfo.veris.crm.order.soa.group.groupintf.reverse.blackwhite;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.DealParamBean;
import com.asiainfo.veris.crm.order.soa.group.groupintf.reverse.GroupRevService;

public class GrpRvrIntfSvc extends GroupRevService
{
    private static final long serialVersionUID = 1L;

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        // 判断否是网外号码并设置路由
        chkIsOutSn(batData);

        // 根据operCode，设置业务操作类型
        setCtrlType(batData);

    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String userIdA = IDataUtil.chkParam(batData, "USER_ID");// 集团用户ID
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        String snType = batData.getString("SN_TYPE", "");

        if (!"OUT".equals(snType))
        {
            // 网外号码不检查是否有未完工工单和集团成员是否预约销户
            checkValidChk(batData);

            // 校验集团三户信息
            IData inparam = new DataMap();
            inparam.put("USER_ID", userIdA);
            chkGroupUCAByUserId(inparam);

            // 校验成员三户信息
            inparam.clear();
            inparam.put("SERIAL_NUMBER", serialNumber);
            chkMemberUCABySerialNumber(inparam);
        }
    }

    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);
        String ctrlType = batData.getString("BIZ_CTRL_TYPE", "");
        if (BizCtrlType.CreateMember.equals(ctrlType))
        {
            ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebOrder");
        }
        if (BizCtrlType.DestoryMember.equals(ctrlType))
        {
            ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebDestory");
        }
        if (BizCtrlType.ChangeMemberDis.equals(ctrlType))
        {
            ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebChg");
        }
        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");

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
        DealParamBean paramBean = new DealParamBean();

        // 拼ELEMENT_INFO串,如果是新增、修改才拼，退出不需要拼
        IDataset selectElements = paramBean.operProductParam(batData);
        svcData.put("USER_ID", batData.getString("GRP_USER_ID", ""));
        svcData.put("SERIAL_NUMBER", batData.getString("MOB_NUM", ""));
        svcData.put("MEM_ROLE_B", batData.getString("MEM_ROLE_B", "1"));
        svcData.put("PRODUCT_ID", batData.getString("GRPPRODUCT_ID", ""));
        svcData.put("REMARK", batData.getString("parm_REMARK"));
        svcData.put("ELEMENT_INFO", selectElements);
        svcData.put("REVERSE_FLAG", "0");// 0 ADCMAS反向受理标记

    }

    /**
     * 作用：根据参数设置调用的服务
     * 
     * @param pd
     * @param td
     * @param data
     * @throws Exception
     */

    public void setCallSvc(IData batData) throws Exception
    {
        String snType = batData.getString("SN_TYPE", "");
        String ctrlType = batData.getString("BIZ_CTRL_TYPE", "");

        if (BizCtrlType.CreateMember.equals(ctrlType))// 新增
        {
            if ("OUT".equals(snType))
            {
                svcName = "SS.BatOutNumberAddBlackWhiteSvc.batOutNumberAddBlackWhite";
            }
            else
            {
                svcName = "CS.CreateGroupMemberSvc.createGroupMember";
            }

        }
        if (BizCtrlType.ChangeMemberDis.equals(ctrlType))// 变更
        {
            if ("OUT".equals(snType))
            {
                svcName = "SS.BatOutNumberAddBlackWhiteSvc.batOutNumberAddBlackWhite";
            }
            else
            {
                svcName = "CS.ChangeMemElementSvc.changeMemElement";
            }
        }
        if (BizCtrlType.DestoryMember.equals(ctrlType))// 终止
        {
            if ("OUT".equals(snType))
            {
                svcName = "SS.BatOutNumberAddBlackWhiteSvc.batOutNumberAddBlackWhite";
            }
            else
            {
                svcName = "CS.DestroyGroupMemberSvc.destroyGroupMember";
            }
        }

    }

}
