
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryCriterionVpmnExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {

        IDataset vpnDataSet = new DatasetList();
        String vpmnNoType = inParam.getString("cond_QueryType"); // 0:母 1：子
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
            userIdA = userId;

            // 查询母集团下的子集团，根据子集团查询非规范子VPMN
            IDataset parentdataset = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(userIdA, "40", null);
            if (IDataUtil.isNotEmpty(parentdataset))
            {
                for (int i = 0; i < parentdataset.size(); i++)
                {

                    IData parent = parentdataset.getData(i);
                    userIdB = parent.getString("USER_ID_B", "");

                    IDataset vpnInfos = UserVpnInfoQry.qryCriterionVpnInfoByUserId(userIdB);
                    if (IDataUtil.isEmpty(vpnInfos))
                    {
                        continue;
                    }
                    IData vpnInfo = vpnInfos.getData(0);

                    String vpmnType = vpnInfo.getString("VPN_SCARE_CODE", "");// 由ng的VPMN_TYPE改为j2ee的VPN_SCARE_CODE
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
                        rsrvStr2 = "本地化省级集团";
                    }
                    vpnInfo.put("RSRV_STR2", rsrvStr2);

                    // 查询用户是否有802服务，有则为规范集团
                    IDataset tmpSvc = UserSvcInfoQry.getGrpSvcInfoByUserId(userIdB, "802");
                    if (IDataUtil.isEmpty(tmpSvc))
                    {
                        vpnInfo.put("RSRV_STR1", "非规范集团");
                    }
                    else
                    {
                        vpnInfo.put("RSRV_STR1", "规范集团");
                    }

                    vpnDataSet.add(vpnInfo);
                }
            }
            else
            {
                return null;
            }

        }
        else if ("1".equals(vpmnNoType))
        {
            if (!"VPMN".equals(brandCode))
            {
                return null;
            }
            userIdB = userId;

            // 获取母集团ID
            IDataset parentVpmnInfos = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(userIdB, "40");
            if (IDataUtil.isEmpty(parentVpmnInfos))
            {
                IDataset vpnInfos = UserVpnInfoQry.qryCriterionVpnInfoByUserId(userIdB);
                if (IDataUtil.isEmpty(vpnInfos))
                {
                    return null;
                }
                IData vpnInfo = vpnInfos.getData(0);

                String vpmnType = vpnInfo.getString("VPN_SCARE_CODE", "");// 由ng的VPMN_TYPE改为j2ee的VPN_SCARE_CODE
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
                    rsrvStr2 = "本地化省级集团";
                }
                vpnInfo.put("RSRV_STR2", rsrvStr2);

                // 查询用户是否有802服务，有则为规范集团
                IDataset tmpSvc = UserSvcInfoQry.getGrpSvcInfoByUserId(userIdB, "802");
                if (IDataUtil.isEmpty(tmpSvc))
                {
                    vpnInfo.put("RSRV_STR1", "非规范集团");
                }
                else
                {
                    vpnInfo.put("RSRV_STR1", "规范集团");
                }

                vpnDataSet.add(vpnInfo);
            }
            else
            {// 存在子母关系
                IData groupVpmn = (IData) parentVpmnInfos.get(0);
                userIdA = groupVpmn.getString("USER_ID_A", "");

                // 查询母集团下的子集团，根据子集团查询非规范子VPMN
                IDataset parentdataset = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(userIdA, "40", null);
                if (IDataUtil.isNotEmpty(parentdataset))
                {
                    for (int i = 0; i < parentdataset.size(); i++)
                    {

                        IData parent = parentdataset.getData(i);
                        userIdB = parent.getString("USER_ID_B", "");
                        IDataset vpnInfos = UserVpnInfoQry.qryCriterionVpnInfoByUserId(userIdB);
                        if (IDataUtil.isEmpty(vpnInfos))
                        {
                            continue;
                        }
                        IData vpnInfo = vpnInfos.getData(0);

                        String vpmnType = vpnInfo.getString("VPN_SCARE_CODE", "");// 由ng的VPMN_TYPE改为j2ee的VPN_SCARE_CODE
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
                            rsrvStr2 = "本地化省级集团";
                        }
                        vpnInfo.put("RSRV_STR2", rsrvStr2);

                        // 查询用户是否有802服务，有则为规范集团
                        IDataset tmpSvc = UserSvcInfoQry.getGrpSvcInfoByUserId(userIdB, "802");
                        if (IDataUtil.isEmpty(tmpSvc))
                        {
                            vpnInfo.put("RSRV_STR1", "非规范集团");
                        }
                        else
                        {
                            vpnInfo.put("RSRV_STR1", "规范集团");
                        }

                        vpnDataSet.add(vpnInfo);
                    }
                }
            }
        }

        if (IDataUtil.isEmpty(vpnDataSet))
        {
            return null;
        }
        for (int i = 0, size = vpnDataSet.size(); i < size; i++)
        {
            IData data = vpnDataSet.getData(i);
            String custManager = data.getString("CUST_MANAGER", "");
            String cityCode = data.getString("CITY_CODE", "");
            String indate = data.getString("IN_DATE", "");
            data.put("IN_DATE", !"".equals(indate) ? SysDateMgr.decodeTimestamp(indate, SysDateMgr.PATTERN_STAND) : "");
            data.put("CUST_MANAGER_ID", custManager);
            data.put("CUST_MANAGER", !"".equals(custManager) ? UStaffInfoQry.getStaffNameByStaffId(custManager) : "");
            data.put("CITY_CODE", !"".equals(cityCode) ? StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", cityCode) : "");
        }
        return vpnDataSet;

    }
}
