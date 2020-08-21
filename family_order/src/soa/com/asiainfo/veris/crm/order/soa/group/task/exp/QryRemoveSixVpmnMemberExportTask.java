
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class QryRemoveSixVpmnMemberExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        IDataset uuInfos = new DatasetList();
        String vpmnNoType = inParam.getString("cond_QueryType");
        String serialNumber = "";
        String userIdA = "";
        String userIdB = "";
        if ("1".equals(vpmnNoType))
        {
            serialNumber = inParam.getString("cond_SERIAL_NUMBER_A", ""); // 子
        }
        else if ("0".equals(vpmnNoType))
        {
            serialNumber = inParam.getString("cond_SERIAL_NUMBER", ""); // 母
        }

        // 根据集团编号获取集团用户信息
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            return null;
        }
        String userId = userInfo.getString("USER_ID", "");
        String productId = userInfo.getString("PRODUCT_ID", "");
        String brandCode = userInfo.getString("BRAND_CODE", "");

        if ("0".equals(vpmnNoType))
        {
            if (!"8050".equals(productId) || !"VPMN".equals(brandCode))
            {
                return null;
            }
            // userIdA = userId;

            // 查询母集团下的子集团，根据子集团查询非6用户
            IDataset parentdataset = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(userId, "40", pg);

            if (IDataUtil.isNotEmpty(parentdataset))
            {
                StringBuilder userIdAs = new StringBuilder();
                int size = parentdataset.size();
                for (int i = 0; i < size; i++)
                {
                    IData parent = parentdataset.getData(i);
                    userIdB = parent.getString("USER_ID_B");
                    if (i != 0)
                    {
                        userIdAs.append(",");
                    }
                    userIdAs.append(userIdB);
                }
                if (size > 1)
                {
                    // 如果有多个userIdA，就使用in做条件，否则用=
                    uuInfos = RelaUUInfoQry.qryRelaUUByInUIdaRemoveSix(userIdAs.toString(), "20", pg, Route.getCrmDefaultDb());
                }
                else
                {
                    uuInfos = RelaUUInfoQry.qryRelaUUByUIdaRemoveSix(userIdAs.toString(), "20", pg, Route.getCrmDefaultDb());
                }

            }
            else
            {
                return null;
            }

        }
        else if ("1".equals(vpmnNoType))
        {
            if (!"8000".equals(productId) || !"VPMN".equals(brandCode))
            {
                return null;
            }
            userIdB = userId;

            // 获取母集团ID
            IDataset parentVpmnInfos = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(userIdB, "40");
            if (IDataUtil.isEmpty(parentVpmnInfos))
            {
                uuInfos = RelaUUInfoQry.qryRelaUUByUIdaRemoveSix(userIdB, "20", pg, Route.getCrmDefaultDb());
            }
            else
            {// 存在子母关系
                IData groupVpmn = (IData) parentVpmnInfos.get(0);
                userIdA = groupVpmn.getString("USER_ID_A", "");

                // 查询母集团下的子集团，根据子集团查询非6用户
                IDataset parentdataset = RelaUUInfoQry.qryGrpRelaUUByUserIdA(userIdA, "40");
                if (IDataUtil.isNotEmpty(parentdataset))
                {
                    StringBuilder userIdAs = new StringBuilder();
                    int size = parentdataset.size();
                    for (int i = 0; i < size; i++)
                    {

                        IData parent = parentdataset.getData(i);
                        userIdB = parent.getString("USER_ID_B", "");
                        if (i != 0)
                        {
                            userIdAs.append(",");
                        }
                        userIdAs.append(userIdB);

                    }

                    uuInfos = RelaUUInfoQry.qryRelaUUByInUIdaRemoveSix(userIdAs.toString(), "20", pg, Route.getCrmDefaultDb());
                }
            }
        }

        if (IDataUtil.isEmpty(uuInfos))
        {
            return null;
        }
        for (int i = 0, size = uuInfos.size(); i < size; i++)
        {
            IData data = uuInfos.getData(i);
            String custManager = data.getString("CUST_MANAGER", "");
            String cityCode = data.getString("CITY_CODE", "");
            data.put("CUST_MANAGER_ID", custManager);
            data.put("CUST_MANAGER", !"".equals(custManager) ? UStaffInfoQry.getStaffNameByStaffId(custManager) : "");
            data.put("CITY_CODE", !"".equals(cityCode) ? StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", cityCode) : "");
            data.remove("ROWNUM_");
        }
        return uuInfos;
    }

}
