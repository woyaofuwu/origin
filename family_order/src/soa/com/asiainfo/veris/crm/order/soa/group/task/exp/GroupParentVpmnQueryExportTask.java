
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class GroupParentVpmnQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        AsynDealVisitUtil.dealVisitInfo(inParam);

        String queryType = inParam.getString("cond_QueryType");

        String serialNumber = "";

        if ("0".equals(queryType))
        {
            serialNumber = inParam.getString("cond_PARENT_SERIAL_NUMBER");
        }
        else if ("1".equals(queryType))
        {
            serialNumber = inParam.getString("cond_SUB_SERIAL_NUMBER");
        }

        // 查询VPMN用户信息
        IData userData = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);

        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_315, serialNumber);
        }

        // 集团用户id
        String userId = userData.getString("USER_ID");

        // 产品标识
        String productId = userData.getString("PRODUCT_ID");

        // 品牌编码
        String brandCode = userData.getString("BRAND_CODE");

        if ("0".equals(queryType)) // 根据母VPMN信息查询
        {
            if (!"8050".equals(productId) || !"VPMN".equals(brandCode))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_209, serialNumber);
            }
        }
        else if ("1".equals(queryType)) // 根据子VPMN信息查询
        {
            if (!"VPMN".equals(brandCode))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_210, serialNumber);
            }
            IData param = new DataMap();
            param.put("USER_ID_B", userId);
            param.put("RELATION_TYPE_CODE", "40");
            param.put(Route.ROUTE_EPARCHY_CODE, userData.getString("EPARCHY_CODE"));
            IDataset relaDataset = CSAppCall.call("CS.RelaUUInfoQrySVC.getRelaUUInfoByUserIdBAndRelaTypeCode", param);
            if (IDataUtil.isEmpty(relaDataset))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_211);
            }
            IData relaData = relaDataset.getData(0);
            userId = relaData.getString("USER_ID_A", "");
        }

        // 查询VPMN信息
        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset userVpnList = CSAppCall.call("CS.UserVpnInfoQrySVC.qryParentUserVpnByUserId", svcData);

        if (IDataUtil.isEmpty(userVpnList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_212);
        }

        // 数据转换
        for (int i = 0, row = userVpnList.size(); i < row; i++)
        {
            IData userVpnData = userVpnList.getData(i);

            String vpmnType = userVpnData.getString("VPMN_TYPE", ""); // VPMN类型

            String rsrvStr2 = "其它";

            if ("0".equals(vpmnType))
            {
                rsrvStr2 = "本地集团";
            }
            else if ("1".equals(vpmnType))
            {
                rsrvStr2 = "全省集团";
            }
            else if ("2".equals(vpmnType))
            {
                rsrvStr2 = "全国集团";
            }
            else if ("3".equals(vpmnType))
            {
                rsrvStr2 = "本地化全省级集团";
            }

            userVpnData.put("RSRV_STR2", rsrvStr2);
        }
        return userVpnList;
    }

}
