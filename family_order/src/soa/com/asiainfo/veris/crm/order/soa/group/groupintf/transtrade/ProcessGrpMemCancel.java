
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade.UCAQryIntf;

public class ProcessGrpMemCancel
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
    // http://10.200.138.2:8080/saleserv/httptran/CrmService?data={X_TRANS_CODE=["ITF_CRM_TcsGrpIntf"],%20X_SUBTRANS_CODE=["ProcessGrpMemCancel"],%20SERIAL_NUMBER=["18808960036"],%20GROUP_ID=["8989856387"],%20TRADE_STAFF_ID=["SUPERUSR"],%20TRADE_DEPART_ID=["00316"],%20TRADE_CITY_CODE=["HNSJ"],%20MODIFY_TAG=["1"],%20PRODUCT_ID=["6200"]}
    public static IDataset processGrpMemberInfo(IData data) throws Throwable
    {
        String operType = IDataUtil.getMandaData(data, "MODIFY_TAG"); // 操作类型 "1"－集团产品成员退订
        String productId = IDataUtil.getMandaData(data, "PRODUCT_ID");
        String groupId = IDataUtil.getMandaData(data, "GROUP_ID");
        String serialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");

        IData grpInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if (IDataUtil.isEmpty(grpInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_472, groupId);
        }
        String grpCustId = grpInfo.getString("CUST_ID");
        IDataset grpUserInfos = UserInfoQry.getUserInfoByCstIdProIdForGrp(grpCustId, productId, null);
        if (IDataUtil.isEmpty(grpUserInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_670, grpCustId, productId);
        }
        // 成员手机号码验证 ,获取成员userid
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

        IDataset processIds = new DatasetList();
        IData result = new DataMap();
        if ("1".equals(operType)) // "1"－集团产品成员退订
        {
            IData callSvcParam = new DataMap();
            callSvcParam.put("USER_ID", grpUserId);
            callSvcParam.put("SERIAL_NUMBER", serialNumber);
            callSvcParam.put("REMARK", "集团产品成员退订接口");
            IDataset resultCallSvc = CSAppCall.call("CS.DestroyGroupMemberSvc.destroyGroupMember", callSvcParam);
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
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:MODIFY_TAG【" + operType + "】未定义!");

        }

        return processIds;
    }

}
