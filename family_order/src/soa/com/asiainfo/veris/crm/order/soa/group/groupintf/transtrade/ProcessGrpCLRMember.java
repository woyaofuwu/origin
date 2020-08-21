
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade.UCAQryIntf;

public class ProcessGrpCLRMember
{

    public static IData callSvcParam = new DataMap();

    /**
     * 根据集团编号CUST_ID查询集团产品订购关系
     * 
     * @param pd
     * @param data
     * @return
     * @author fanwenhui
     * @throws Throwable
     */
    // http://127.0.0.1:8080/httptran/CrmService?data={X_TRANS_CODE=["ITF_CRM_TcsGrpIntf"],%20X_SUBTRANS_CODE=["ProcessGrpCLRMem"],%20SERIAL_NUMBER=["15103066061"],%20SERIAL_NUMBER_A=["09955042698"],%20TRADE_STAFF_ID=["SUPERUSR"],%20X_CUST_TYPE=["SHORT_MESSAGE_PORTAL"],%20MODIFY_TAG=["0"],%20PRODUCT_ID=["6200"]}
    public static IDataset processGrpMemberInfo(IData data) throws Throwable
    {
        IData result = new DataMap();
        String intfType = IDataUtil.getMandaData(data, "X_CUST_TYPE"); // 渠道类型 “SHORT_MESSAGE_PORTAL” 短信营业厅
        String operType = IDataUtil.getMandaData(data, "MODIFY_TAG"); // 操作类型 “0” 成员产品订购

        // 验证成员sn和集团sn
        IDataset resultInfos = dealIntfBiz(data, intfType);
        if (IDataUtil.isNotEmpty(resultInfos))
            return resultInfos;

        IDataset processIds = new DatasetList();
        if ("0".equals(operType)) // “0”－集团成员产品订购；
        {
            IData param = new DataMap();
            param.put("USER_ID", callSvcParam.getString("USER_ID")); // 集团USER_ID
            param.put("SERIAL_NUMBER", callSvcParam.getString("SERIAL_NUMBER"));// 成员服务号码
            param.put("MEM_ROLE_B", "1");// 普通成员角色
            param.put("PLAN_TYPE_CODE", "P");// 个人付费
            param.put("PRODUCT_ID", "6200");// 集团彩铃
            IDataset resultCallSvc = CSAppCall.call("CS.CreateGroupMemberSvc.createGroupMember", param);
            if (IDataUtil.isEmpty(resultCallSvc))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "-1:Trade ERROR!");
            }
            else
            {
                result.put("X_LAST_RESULTINFO", "Trade OK!");
                result.put("X_RESULTCODE", "0");
                result.put("ORDER_ID", resultCallSvc.getData(0).getString("ORDER_ID"));
                result.put("TRADE_ID", resultCallSvc.getData(0).getString("TRADE_ID"));
                result.put("CUST_NAME", callSvcParam.getString("CUST_NAME"));
            }
            processIds.add(result);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:MODIFY_TAG【" + operType + "】未定义!");
        }

        return processIds;
    }

    /*
     * 处理短厅入参，验证成员sn和集团sn
     */
    private static IDataset dealIntfBiz(IData data, String intfType) throws Exception
    {
        IData result = new DataMap();
        IDataset resultInfos = new DatasetList();
        if ("SHORT_MESSAGE_PORTAL".equals(intfType))
        {// 短信营业厅

            // 得到 serialNumberA
            String serialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
            String serialNumberA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");

            IData dt = new DataMap();
            dt.put("SERIAL_NUMBER", serialNumberA);

            IData grpUserinfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberA);
            if (IDataUtil.isEmpty(grpUserinfo))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "-2:您输入的产品编码不存在，请核实后再办理。详情请咨询10086-8");
            }
            String ProductId = grpUserinfo.getString("PRODUCT_ID");
            if (!"6200".equals(ProductId))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "-2:您输入的产品编码不是集团彩铃的产品编码，请核实后再办理。详情请咨询10086-8");
            }
            String custId = grpUserinfo.getString("CUST_ID");
            IData grpinfo = UcaInfoQry.qryGrpInfoByCustId(custId);
            if (IDataUtil.isEmpty(grpinfo))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "-2:您输入的产品编码不存在，请核实后再办理。详情请咨询10086-8");
            }
            // 成员手机号码
            UcaData memUcaData = new UcaData();
            IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serialNumber);
            boolean bool = uca.getBoolean("RESULT");
            if (!bool)
            {
                resultInfos = uca.getDataset("RESULT_DATA");
                return resultInfos;
            }
            else
            {
                memUcaData = (UcaData) uca.get("UCADATA");
            }
            String memUserId = memUcaData.getUserId();
            String strRouteEparchyCode = memUcaData.getUserEparchyCode();

            IDataset UUinfo = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, "26", strRouteEparchyCode);
            if (IDataUtil.isNotEmpty(UUinfo))
            {
                String useridA = UUinfo.getData(0).getString("USER_ID_A");
                // 集团
                UcaData grpUcaData = new UcaData();
                uca.clear();
                uca = UCAQryIntf.getGroupUCAByUserId(useridA);
                bool = true;
                bool = uca.getBoolean("RESULT");
                if (bool)
                {
                    grpUcaData = (UcaData) uca.get("UCADATA");
                    String grpCustName = grpUcaData.getCustomer().getCustName();
                    CSAppException.apperr(GrpException.CRM_GRP_834, grpCustName);
                }

            }
            callSvcParam.put("USER_ID", grpUserinfo.getString("USER_ID")); // 集团USER_ID
            callSvcParam.put("SERIAL_NUMBER", serialNumber); // 成员服务号码
            callSvcParam.put("MEM_ROLE_B", "1"); // 普通成员角色
            callSvcParam.put("CUST_NAME", grpinfo.getString("CUST_NAME"));//集团名称

        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_835);
        }
        return null;
    }

    /**
     * 集团成员产品订购
     */
    public static IDataset createGroupUserMem(IData idata) throws Throwable
    {
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();

        callSvcParam.put("PLAN_TYPE_CODE", "P");
        callSvcParam.put("PRODUCT_ID", "6200");
        IDataset resultCallSvc = CSAppCall.call("CS.CreateGroupMemberSvc.createGroupMember", callSvcParam);
        if (IDataUtil.isEmpty(resultCallSvc))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "Trade ERROR!");
        }
        else
        {
            result.put("X_LAST_RESULTINFO", "Trade OK!");
            result.put("X_RESULTCODE", "0");
            result.put("ORDER_ID", resultCallSvc.getData(0).getString("ORDER_ID"));
        }

        resultInfos.add(result);
        return resultInfos;
    }

}
