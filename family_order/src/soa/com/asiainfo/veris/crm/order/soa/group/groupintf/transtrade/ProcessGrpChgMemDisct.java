
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade.UCAQryIntf;

public class ProcessGrpChgMemDisct
{

    /**
     * 根据集团编号CUST_ID查询集团产品订购关系
     * 
     * @param pd
     * @param data
     * @return
     * @author lixiuyu
     * @throws Throwable
     */
    // http://10.199.48.39:8889/httptran/CrmService?data={X_TRANS_CODE=["ITF_CRM_TcsGrpIntf"],%20X_SUBTRANS_CODE=["ProcessGrpChgMemDisct"],%20SERIAL_NUMBER=["13518004008"],%20GROUP_ID=["8989856126"],%20TRADE_STAFF_ID=["SUPERUSR"],%20TRADE_DEPART_ID=["00316"],%20TRADE_CITY_CODE=["HNSJ"],%20TRADE_EPARCHY_CODE=["0898"],%20MODIFY_TAG=["2"],%20DISCNT_CODE=["3040"],%20PRODUCT_ID=["10005742"],%20OPER_TYPE=["1"],%20EFFECT_TIME=["1"]}
    public static IDataset processGrpMemberInfo(IData data) throws Throwable
    {
        String modifyTag = IDataUtil.getMandaData(data, "MODIFY_TAG"); // "2"－集团产品成员优惠变更
        String productId = IDataUtil.getMandaData(data, "PRODUCT_ID");
        String groupId = IDataUtil.getMandaData(data, "GROUP_ID");
        String discntCode = IDataUtil.getMandaData(data, "DISCNT_CODE");// 原有优惠编码
        String operType = IDataUtil.getMandaData(data, "OPER_TYPE");// 操作类型：1.优惠删除 2.优惠新增
        String effectTime = IDataUtil.getMandaData(data, "EFFECT_TIME");// 生效类型：0.立即生效 1.下月生效
        String serialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        // 集团验证
        IData grpInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if (IDataUtil.isEmpty(grpInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_472, groupId);
        }
        String grpCustId = grpInfo.getString("CUST_ID");
        IDataset grpUserInfos = UserInfoQry.getUserInfoByCstIdProIdForGrp(grpCustId, productId, null);
        if (IDataUtil.isEmpty(grpUserInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_670, grpCustId, productId); // "您输入集团客户编码不存在，请核实后再办理。详情请咨询10086-8"
        }
        // // 成员手机号码验证 ,获取成员userid
        UcaData memUcaData = new UcaData();
        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serialNumber);
        boolean bool = uca.getBoolean("RESULT");
        if (!bool)
        {
            return uca.getDataset("RESULT_DATA");
        }
        else
        {
            memUcaData = (UcaData) uca.get("UCADATA");
        }
        String userIdB = memUcaData.getUserId();
        String memEparchCode = memUcaData.getUserEparchyCode();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
        if (StringUtils.isBlank(relationTypeCode))
        {
            CSAppException.apperr(GrpException.CRM_CRP_814, productId);
        }
        // 根据成员userid确定集团userid
        String grpUserId = "";
        for (int i = 0; i < grpUserInfos.size(); i++)
        {
            IData grpUserInfo = grpUserInfos.getData(i);
            grpUserId = grpUserInfo.getString("USER_ID");
            IDataset uuInfos = RelaUUInfoQry.qryUU(grpUserId, userIdB, relationTypeCode, null, memEparchCode);
            if (IDataUtil.isNotEmpty(uuInfos))
            {
                break;
            }
        }

        IDataset resultInfos = new DatasetList();
        IDataset processIds = new DatasetList();
        IData result = new DataMap();
        if ("2".equals(modifyTag)) // "2"－集团产品成员优惠变更
        {
            // 业务是否预约 true 预约 false 非预约工单
            String effectNow = "false";
            if ("0".equals(effectTime)) // 立即生效
            {
                effectNow = "true";
            }
            IDataset productElements = new DatasetList();
            IData elementInfo = new DataMap();
            if ("1".equals(operType)) // 操作类型：1.优惠删除 2.优惠新增
            {
                elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
            else if ("2".equals(operType)) // 操作类型：1.优惠删除 2.优惠新增
            {
                elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            }
            elementInfo.put("DISCNT_CODE", discntCode);
            productElements.add(elementInfo);
            IData callSvcParam = new DataMap();
            callSvcParam.put("USER_ID", grpUserId);
            callSvcParam.put("SERIAL_NUMBER", serialNumber);
            callSvcParam.put("PRODUCT_ID", productId); // 集团产品id
            callSvcParam.put("REMARK", "集团成员优惠变更处理接口 ");
            callSvcParam.put("EFFECT_NOW", effectNow);
            callSvcParam.put("LIST_INFOS", productElements);
            IDataset resultCallSvc = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", callSvcParam);
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
            }

            processIds.add(result);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:MODIFY_TAG【" + modifyTag + "】未定义!");
        }

        return processIds;

    }

    public static IDataset processGrpMemberVpmnInfo(IData data) throws Exception
    {
        String modifyTag = IDataUtil.getMandaData(data, "MODIFY_TAG"); // "1"-退订 "2"－集团产品成员优惠变更

        String serialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        String serialNumberA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");// 集团sn

        IData grpUserData = UcaInfoQry.qryUserInfoBySnForGrp(serialNumberA);

        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberA);
        }

        String grpUserId = grpUserData.getString("USER_ID");

        IData result = new DataMap();
        result.put("SERIAL_NUMBER", serialNumber);
        result.put("USER_ID_A", grpUserId);
        result.put("PRODUCT_ID", IDataUtil.getMandaData(data, "PRODUCT_ID"));
        result.put("MODIFY_TAG", modifyTag);

        IDataset results = null;

        if ("1".equals(modifyTag))
        {

            results = CSAppCall.call("CS.DestroyGroupMemberSvc.destroyGroupMember", result);

        }
        else if ("2".equals(modifyTag))
        {
            IDataset discntList = new DatasetList();
            IData discntOld = new DataMap();

            String discntCodeOld = IDataUtil.getMandaData(data, "DISCNT_CODE_B");// 原有优惠编码
            String discntCodeNew = IDataUtil.getMandaData(data, "DISCNT_CODE_A");// 新优惠编码
            String effectTime = IDataUtil.getMandaData(data, "EFFECT_TIME_TAG");// 生效类型：0.立即生效 1.下月生效

            String effectNow = "false";
            if ("0".equals(effectTime)) // 立即生效
            {
                effectNow = "true";
            }

            discntOld.put("MODIFY_TAG", "1");
            discntOld.put("DISCNT_CODE", discntCodeOld);
            discntList.add(discntOld);

            IData discntNew = new DataMap();
            discntNew.put("MODIFY_TAG", "0");
            discntNew.put("DISCNT_CODE", discntCodeNew);
            discntList.add(discntNew);

            result.put("LIST_INFOS", discntList);

            result.put("EFFECT_NOW", effectNow);

            results = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", result);

        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "MODIFY_TAG【" + modifyTag + "】只能为1或者2!");
        }

        return results;
    }
}
