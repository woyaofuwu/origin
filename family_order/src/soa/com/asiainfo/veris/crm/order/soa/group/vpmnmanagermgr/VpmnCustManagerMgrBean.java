
package com.asiainfo.veris.crm.order.soa.group.vpmnmanagermgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class VpmnCustManagerMgrBean
{
    /**
     * 分配VPMN客户经理权限
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IData doDispatchVPMN(IData inparam) throws Exception
    {
        // 设置返回参数
        IData result = new DataMap();
        result.put("RESULT_CODE", "0");
        result.put("RESULT_INFO", "调配成功");

        // 新客户经理
        String newCustMgrId = inparam.getString("NEW_MANAGER_ID", "");

        // 当前操作员
        String currStaffId = inparam.getString("STAFF_ID");

        // 当前操作员工所属部门
        String currDeptId = inparam.getString("DEPART_ID");

        // 查询VMPN客户经理信息
        IDataset managerInfos = CustManagerInfoQry.qryVpmnManagerInfo("2", newCustMgrId);
        if (IDataUtil.isEmpty(managerInfos))
        {
            result.put("RESULT_CODE", "-2");
            result.put("RESULT_INFO", "该员工不是VPMN客户经理");
            return result;
        }
        IData managerInfo = managerInfos.getData(0);
        String areaFrame = managerInfo.getString("AREA_FRAME"); // 归属业务区关系节点串
        String area = areaFrame;
        if (area.length() > 7)
        {
            area = areaFrame.substring(8, 12); // 此处改为取8-12位业务区编码,海南特殊业务区对应地州
        }

        // 查询操作员信息
        IDataset staffInfos = StaffInfoQry.qryStaffInfoByStaffId(currStaffId);
        String currCityCode = staffInfos.getData(0).getString("CITY_CODE");
        if (currCityCode.length() > 7)
        {
            currCityCode = currCityCode.substring(8, 12); // 此处改为取8-12位业务区编码,海南特殊业务区对应地州
        }

        // 旧客户经理编码
        IDataset oldCustMgrIds = inparam.getDataset("OLD_CUST_MANAGER_ID");

        // VPMN编码
        IDataset userProdCodes = inparam.getDataset("USER_PRODUCT_CODE");

        // 权限编码
        IDataset rightCodes = inparam.getDataset("RIGHT_CODE");

        // 开始时间
        IDataset startDates = inparam.getDataset("START_DATE");

        // 结束时间
        IDataset endDates = inparam.getDataset("END_DATE");

        // 新客户经理编码
        String newManagerId = inparam.getString("NEW_MANAGER_ID");

        // 分配失败数量
        int faildNum = 0;

        // 分配成功数量
        int succNum = 0;

        // 调配错误提示信息
        StringBuilder sb = new StringBuilder();

        // 客户经理分配总数
        int total = inparam.getInt("X_RECORDNUM");

        for (int i = 0; i < total; i++)
        {
            String oldCustMgrId = (String) oldCustMgrIds.get(i);

            String userProdCode = (String) userProdCodes.get(i);

            String rightCode = (String) rightCodes.get(i);

            String endDate = (String) endDates.get(i);

            // 不是海南省局的工号，要进行业务区判断。
            if (!"HNSJ".equals(area) && !"HNSJ".equals(currCityCode))
            {
                if (!isSameEparchy(oldCustMgrId, area))
                {
                    sb.append("原客户经理").append(oldCustMgrId).append("和目标客户经理").append(newManagerId).append("所在区域不一致~");
                    faildNum++;
                    continue;
                }
            }

            // 修改VPMN客户经理权限 TF_M_STAFF_GRP_RIGHT
            IData param = new DataMap();
            param.put("STAFF_ID", oldCustMgrId);
            param.put("RIGHT_CODE", rightCode);
            param.put("USER_PRODUCT_CODE", userProdCode);
            param.put("START_DATE", verseTime((String) startDates.get(i)));
            param.put("END_DATE", SysDateMgr.getSysTime());
            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
            param.put("UPDATE_STAFF_ID", currStaffId);
            param.put("UPDATE_DEPART_ID", currDeptId);
            boolean updFlag = CustManagerInfoQry.updateStaffGrpRightByPK(param);

            // 获取VPMN客户经理权限
            IDataset vpnRights = CustManagerInfoQry.checkVpmnRight(newManagerId, userProdCode, rightCode);
            boolean insFlag = false;
            if (IDataUtil.isEmpty(vpnRights))
            {
                param.put("STAFF_ID", newManagerId);
                param.put("START_DATE", SysDateMgr.getSysTime());
                param.put("END_DATE", endDate);
                param.put("REMARK", "从VPMN客户经理" + oldCustMgrId + "调配得到" + newManagerId);
                insFlag = CustManagerInfoQry.insertVpnRight(param);
            }
            else
            {
                sb.append(oldCustMgrId).append("对").append(userProdCode).append("已拥有").append(rightCode).append("权限~");
            }

            if (updFlag && insFlag)
            {
                succNum++;
            }
            else
            {
                faildNum++;
            }
        }

        if (succNum == 0)
        {
            result.put("RESULT_CODE", "-3");
        }
        String resultInfo = "";
        if (sb.length() > 0)
        {
            resultInfo = "，失败详情：" + sb.toString();
        }
        result.put("RESULT_INFO", "VPMN客户经理调配成功数量:" + succNum + "，VPMN客户经理调配失败数量为:" + faildNum + resultInfo);
        result.put("SUCCESS", succNum + "");
        return result;
    }

    /**
     * @Description:处理VPMN产品客户经理分配批量导入数据
     * @author sungq3
     * @date 2014-05-22
     * @return
     * @throws Exception
     */
    public static boolean doThisVpmnInfo(IData inparam) throws Exception
    {
        String importId = inparam.getString("IMPORT_ID");
        String sysTime = SysDateMgr.getSysTime();
        String staffId = inparam.getString("STAFF_ID");
        String departId = inparam.getString("DEPART_ID");
        String eparchyCode = inparam.getString("EPARCHY_CODE");

        IData batParam = new DataMap();
        // 1、查询导入的数据
        IDataset importDataset = CustManagerInfoQry.queryThisVpmnManagerInfo(importId, "", null);
        if (IDataUtil.isEmpty(importDataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_143, importId);
        }

        for (int i = 0, size = importDataset.size(); i < size; i++)
        {
            IData tempData = importDataset.getData(i);
            tempData.put("DEAL_TIME", sysTime);
            tempData.put("DEAL_STAFF_ID", staffId);
            tempData.put("DEAL_DEPART_ID", departId);

            String vpnNo = tempData.getString("RSRV_STR1").trim();
            String newCustMgrId = tempData.getString("RSRV_STR2").trim();
            // 2、判断VPMN集团编码是否存在
            IDataset vpnInfos = UserVpnInfoQry.queryVpnInfoByVpnNo(vpnNo);
            if (IDataUtil.isEmpty(vpnInfos))
            {
                tempData.put("DEAL_STATE", "E");
                tempData.put("REMARK", vpnNo + ":不存在该编号的VPMN集团");
                CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);
                continue;
            }
            IData vpnInfo = vpnInfos.getData(0);
            // 3、判断要分配的员工是不是客户经理
            IDataset managerInfos = UStaffInfoQry.qryCustManagerStaffById(newCustMgrId);
            if (IDataUtil.isEmpty(managerInfos))
            {
                tempData.put("DEAL_STATE", "E");
                tempData.put("REMARK", newCustMgrId + ":该员工不是客户经理");
                CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);
                continue;
            }
            IData managerInfo = managerInfos.getData(0);
            // 4、判断是否需要重新分配(如果新分配的managerId和原来的managerId相同，则不需要重新分配)
            if (newCustMgrId.equals(vpnInfo.getString("CUST_MANAGER")))
            {
                tempData.put("DEAL_STATE", "E");
                tempData.put("REMARK", "该VPMN集团的客户经理已经是" + newCustMgrId + "，不需要重新分配");
                CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);
                continue;
            }
            // 5、判断业务区是否相同
            IDataset areaInfos = CustManagerInfoQry.queryVpmnCityCode(vpnNo);
            if (IDataUtil.isEmpty(areaInfos))
            {
                tempData.put("DEAL_STATE", "E");
                tempData.put("REMARK", vpnNo + ":获取不到该集团产品的客户业务区!");
                CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);
                continue;
            }
            
            IData areaInfo = areaInfos.getData(0);
            String groupAreaCode = areaInfo.getString("CITY_CODE");
            String currentArea = inparam.getString("CITY_CODE");
            String mangerAreaCode = managerInfo.getString("AREA_CODE");
            if (!"HNSJ".equals(groupAreaCode) && !"HNSJ".equals(currentArea))
            { // 不是海南省局的工号，要进行业务区判断。
                if (!mangerAreaCode.equals(groupAreaCode))
                {
                    tempData.put("DEAL_STATE", "E");
                    tempData.put("REMARK", "不能跨区域调配");
                    CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);
                    continue;
                }
            }
            // 以上都没问题，则可以进行客户经理的调配
            vpnInfo.put("CUST_MANAGER", newCustMgrId);
            vpnInfo.put("UPDATE_TIME", sysTime);
            vpnInfo.put("UPDATE_STAFF_ID", staffId);
            vpnInfo.put("UPDATE_DEPART_ID", departId);

            boolean flag = CustManagerInfoQry.updateVpnInfo(vpnInfo);
            if (flag)
            {
                tempData.put("DEAL_STATE", "J");
                tempData.put("REMARK", "处理成功！");
                CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);
            }
        }

        batParam.put("IMPORT_ID", importId);
        batParam.put("DEAL_STATE", "2");
        batParam.put("RSRV_STR10", "处理完成");
        CustManagerInfoQry.updateImportBatVpmnDisInfo(batParam, eparchyCode);
        return true;
    }

    /**
     * @Description:VPMN客户经理权限分配--将临时表数据导入到正式表中
     * @author wusf
     * @date 2009-8-26
     * @param pd
     * @param import_id
     * @throws Exception
     */
    public static boolean doThisVpmnManagerImport(IData inparam) throws Exception
    {
        String importId = inparam.getString("IMPORT_ID");
        String sysTime = SysDateMgr.getSysTime();
        String departId = inparam.getString("DEPART_ID");
        String operStaffId = inparam.getString("STAFF_ID");
        String eparchyCode = inparam.getString("EPARCHY_CODE");

        IDataset importDataset = CustManagerInfoQry.queryThisVpmnManagerInfo(importId, "", null);
        boolean vpnFlag = true;
        boolean areaFlag = true;
        for (int i = 0, size = importDataset.size(); i < size; i++)
        {
            IData tempData = importDataset.getData(i);
            String staffId = tempData.getString("RSRV_STR1");
            String userProductCode = tempData.getString("RSRV_STR2");
            String rightCode = tempData.getString("RSRV_STR3");
            String remark = tempData.getString("RSRV_STR4");
            // 3、判断要分配的员工是不是客户经理
            IDataset managerInfos = UStaffInfoQry.qryCustManagerStaffById(staffId);
            if (IDataUtil.isNotEmpty(managerInfos))
            {
                IData managerInfo = managerInfos.getData(0);
                String areaFrame = managerInfo.getString("AREA_FRAME"); // 归属业务区关系节点串
                String area = areaFrame;
                if (area.length() > 7)
                {
                    area = areaFrame.substring(8, 12); // 此处改为取8-12位业务区编码,海南特殊业务区对应地州
                }

                // 判断业务区是否一致
                if (!"HNSJ".equals(area))
                {// 不是海南省局的工号，要进行业务区判断。
                    IDataset areaInfos = CustManagerInfoQry.queryVpmnCityCode(userProductCode);
                    if (!area.equals(areaInfos.getData(0).getString("CITY_CODE")))
                    {
                        areaFlag = false;
                    }
                }
            }
            // 判断是否拥有该权限
            boolean vpmnFlag = true;
            IDataset useRes = CustManagerInfoQry.checkVpmnRight(staffId, userProductCode, rightCode);
            if (useRes.size() > 0)
            {
                vpmnFlag = false;
            }
            if (!vpmnFlag)
            {
                tempData.put("DEAL_STATE", "E");
                tempData.put("REMARK", "该客户经理已拥有该权限！");
            }
            else
            {
                if (!areaFlag)
                {
                    tempData.put("DEAL_STATE", "E");
                    tempData.put("REMARK", "分配的客户经理和集团所在地市不一致！");
                }
                else
                {
                    IData staffParam = new DataMap();
                    staffParam.put("STAFF_ID", staffId);
                    IData vpnParma = new DataMap();
                    vpnParma.put("STAFF_ID", staffId);
                    vpnParma.put("USER_PRODUCT_CODE", userProductCode);
                    vpnParma.put("RIGHT_CODE", rightCode);
                    vpnParma.put("UPDATE_STAFF_ID", operStaffId);
                    vpnParma.put("UPDATE_DEPART_ID", departId);
                    vpnParma.put("UPDATE_TIME", sysTime);
                    vpnParma.put("START_DATE", sysTime);
                    vpnParma.put("END_DATE", SysDateMgr.getTheLastTime());
                    vpnParma.put("REMARK", remark);
                    vpnFlag = CustManagerInfoQry.insertVpnRight(vpnParma);
                    if (vpnFlag)
                    {
                        tempData.put("DEAL_STATE", "J");
                        tempData.put("REMARK", "处理成功！");
                    }
                    else
                    {
                        tempData.put("DEAL_STATE", "E");
                        tempData.put("REMARK", "处理失败！");
                    }
                }
            }

            tempData.put("DEAL_TIME", sysTime);
            tempData.put("DEAL_STAFF_ID", operStaffId);
            tempData.put("DEAL_DEPART_ID", departId);
            CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);

        }
        IData batParam = new DataMap();
        batParam.put("IMPORT_ID", importId);
        batParam.put("DEAL_STATE", "2");
        batParam.put("DEAL_TIME", sysTime);
        batParam.put("DEAL_STAFF_ID", operStaffId);
        batParam.put("DEAL_DEPART_ID", departId);
        batParam.put("RSRV_STR10", "处理完成");
        boolean res = CustManagerInfoQry.updateImportBatVpmnDisInfo(batParam, eparchyCode);
        return res;
    }

    /**
     * @Description:VPMN客户经理分配（从临时表导入正式表）
     * @author wusf
     * @date 2009-8-27
     * @param pd
     * @param importId
     * @throws Exception
     */
    public static boolean doThisVpmnManagerInfo(IData inparam) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        String departId = inparam.getString("DEPART_ID");
        String operStaffId = inparam.getString("STAFF_ID");
        String eparchyCode = inparam.getString("EPARCHY_CODE");

        String importId = inparam.getString("IMPORT_ID");
        IDataset importDataset = CustManagerInfoQry.queryThisVpmnManagerInfo(importId, "", null);

        for (int i = 0; i < importDataset.size(); i++)
        {
            IData tempData = importDataset.getData(i);
            String staffId = tempData.getString("RSRV_STR1");
            String newstaffId = tempData.getString("RSRV_STR2");
            IData staffParam = new DataMap();
            staffParam.put("STAFF_ID", staffId);
            IDataset staffRights = CustManagerInfoQry.checkVpmnRight(staffId, "", "");
            for (int j = 0; j < staffRights.size(); j++)
            {
                IData staffRight = staffRights.getData(j);
                String startDate = verseTime(staffRight.getString("START_DATE"));
                staffRight.put("START_DATE", startDate);
                String remark = staffRight.getString("REMARK");
                IData rightParam = new DataMap();
                rightParam.put("STAFF_ID", newstaffId);
                rightParam.put("USER_PRODUCT_CODE", staffRight.getString("USER_PRODUCT_CODE"));
                rightParam.put("RIGHT_CODE", staffRight.getString("RIGHT_CODE"));
                IDataset res = CustManagerInfoQry.checkVpmnRight(newstaffId, staffRight.getString("USER_PRODUCT_CODE"), staffRight.getString("RIGHT_CODE"));

                if (IDataUtil.isEmpty(res))
                {
                    // 如果新的VPMN客户经理不存在该权限，则进行分配
                    // 先新增新的权限
                    staffRight.put("REMARK", "从VPMN客户经理" + staffId + "批量调配得到" + newstaffId);
                    staffRight.put("UPDATE_TIME", sysTime);
                    staffRight.put("UPDATE_STAFF_ID", operStaffId);
                    staffRight.put("UPDATE_DEPART_ID", departId);
                    staffRight.put("START_DATE", sysTime);
                    staffRight.put("STAFF_ID", newstaffId);
                    CustManagerInfoQry.insertVpnRight(staffRight);

                    // 再注销掉老的资料
                    staffRight.put("STAFF_ID", staffId);
                    staffRight.put("START_DATE", startDate);
                    staffRight.put("REMARK", remark);
                    staffRight.put("END_DATE", sysTime);
                    CustManagerInfoQry.updVpnRight(staffRight);

                }
                else
                {
                    // 如果新的VPMN客户经理存在该权限，直接将老的注销掉
                    staffRight.put("END_DATE", sysTime);
                    staffRight.put("UPDATE_TIME", sysTime);
                    staffRight.put("UPDATE_STAFF_ID", operStaffId);
                    staffRight.put("UPDATE_DEPART_ID", departId);
                    CustManagerInfoQry.updVpnRight(staffRight);
                }
            }
            tempData.put("DEAL_STATE", "J");
            tempData.put("REMARK", "处理成功！");
            tempData.put("DEAL_TIME", sysTime);
            tempData.put("DEAL_STAFF_ID", operStaffId);
            tempData.put("DEAL_DEPART_ID", departId);
            CustManagerInfoQry.updateImportDataVpmnDisInfo(tempData);
        }

        IData batParam = new DataMap();
        batParam.put("IMPORT_ID", importId);
        batParam.put("DEAL_STATE", "2");
        batParam.put("DEAL_TIME", sysTime);
        batParam.put("DEAL_STAFF_ID", operStaffId);
        batParam.put("DEAL_DEPART_ID", departId);
        batParam.put("RSRV_STR10", "处理完成");
        return CustManagerInfoQry.updateImportBatVpmnDisInfo(batParam, eparchyCode);

    }

    /**
     * @Description:判断客户经理归属区域是否一致
     * @author sungq3
     * @date 2014-06-26
     * @param oldVpmnManagerID
     * @param managerArea
     * @return
     * @throws Exception
     */
    private static boolean isSameEparchy(String oldVpmnManagerID, String managerArea) throws Exception
    {
        IDataset managerInfos = CustManagerInfoQry.qryVpmnManagerInfo("2", oldVpmnManagerID);
        IData managerInfo = managerInfos.getData(0);
        String oldManagerArea = managerInfo.getString("AREA_FRAME");
        if (oldManagerArea.length() > 7)
        {
            oldManagerArea = oldManagerArea.substring(8, 12); // 此处改为取8-12位业务区编码,海南特殊业务区对应地州
        }
        if (managerArea.equals(oldManagerArea))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static String verseTime(String dateTime)
    {
        int len = dateTime.length();
        dateTime = len > 19 ? dateTime.substring(0, 19) : dateTime;
        return dateTime;
    }
}
