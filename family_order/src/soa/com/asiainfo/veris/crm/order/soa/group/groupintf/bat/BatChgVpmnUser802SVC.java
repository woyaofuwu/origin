
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class BatChgVpmnUser802SVC extends GroupBatService
{
    private static final String SERVICE_NAME = "CS.ChangeUserElementSvc.changeUserElement";

    /**
     * 作用： 批量初始化其他信息(子类继承)
     * 
     * @throws Exception
     */
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.ChangeUserDis);
        condData.put("PRODUCT_ID", "8000"); // j2ee 测试写死
    }

    /**
     * 作用： 批量校验其他信息(子类实现)
     * 
     * @throws Exception
     */
    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 子VPMN集团服务号码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkGroupUCABySerialNumber(inparam);

        String grpUserId = getGrpUcaData().getUserId();
        IDataset userVpnData = UserVpnInfoQry.qryUserVpnByUserId(grpUserId);
        if (IDataUtil.isEmpty(userVpnData))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_6, grpUserId);
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
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        String grpUserId = getGrpUcaData().getUserId();
        String productId = getGrpUcaData().getProductId();
        String contractId = getGrpUcaData().getUser().getContractId();
        svcData.put("USER_ID", grpUserId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("CONTRACT_ID", contractId);
        svcData.put(Route.USER_EPARCHY_CODE, getGrpUcaData().getUserEparchyCode());

        // 组装元素
        String operType = IDataUtil.chkParam(condData, "OPER_TYPE");// 操作类型 0--新增；1--注销
        IDataset productElements = infoRegDataSvc(grpUserId, operType);
        svcData.put("ELEMENT_INFO", productElements);
    }

    public IDataset infoRegDataSvc(String userId, String operType) throws Exception
    {
        IDataset SVC = new DatasetList();
        IDataset svcInfos = UserSvcInfoQry.getGrpSvcInfoByUserId(userId, "802");
        if ("0".equals(operType))
        {
            // 集团VPMN服务包新增“免“6”发送短号短、彩信功能”服务判断短号
            VpnUnit.validchk801Svc(userId, "1");

            if (IDataUtil.isEmpty(svcInfos))
            {
                IData svc802data = new DataMap();
                svc802data.put("USER_ID_A", "-1");
                svc802data.put("PRODUCT_ID", "8000");
                svc802data.put("PACKAGE_ID", "80000001");
                svc802data.put("ELEMENT_TYPE_CODE", "S");
                svc802data.put("ELEMENT_ID", "802");// 免“6”发送短号短、彩信功能”服务(802)
                svc802data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                svc802data.put("INST_ID", "");//
                svc802data.put("START_DATE", SysDateMgr.getSysTime());
                svc802data.put("END_DATE", SysDateMgr.getTheLastTime()); // j2ee 时间还要算
                // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                svc802data.put("DIVERSIFY_ACCT_TAG", "1");
                SVC.add(svc802data);
            }
            else
            {
                // common.error("1111", "用户免6发送短号短彩信服务(802)已存在，业务不能继续！");
                CSAppException.apperr(VpmnUserException.VPMN_USER_192);
            }

        }
        else if ("1".equals(operType))
        {

            if (IDataUtil.isNotEmpty(svcInfos))
            {
                IData svc = svcInfos.getData(0);
                IData svc802data = new DataMap();
                svc802data.put("PRODUCT_ID", svc.getString("PRODUCT_ID"));
                svc802data.put("PACKAGE_ID", svc.getString("PACKAGE_ID"));
                svc802data.put("ELEMENT_TYPE_CODE", "S");
                svc802data.put("ELEMENT_ID", "802");// 免“6”发送短号短、彩信功能”服务(802)
                svc802data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                svc802data.put("INST_ID", svc.getString("INST_ID"));//
                svc802data.put("START_DATE", svc.getString("START_DATE"));
                svc802data.put("END_DATE", SysDateMgr.getSysTime()); // j2ee 时间还要算
                // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                svc802data.put("DIVERSIFY_ACCT_TAG", "1");
                SVC.add(svc802data);
            }
            else
            {
                // common.error("1111", "用户没有相应免6发送短号短彩信服务(802)，业务不能继续！");
                CSAppException.apperr(VpmnUserException.VPMN_USER_192);
            }
        }
        return SVC;
    }

}
