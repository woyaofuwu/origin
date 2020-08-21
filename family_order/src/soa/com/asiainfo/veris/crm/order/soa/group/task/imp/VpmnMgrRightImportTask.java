
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.log.LogBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class VpmnMgrRightImportTask extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_138);
        }
        String importType = data.getString("IMPORT_TYPE");
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData dataOut = dataset.getData(i);
            if ("VPMNPRO".equals(importType))
            {// VPMN产品经理批量导入
                for (int j = i + 1; j < size; j++)
                {
                    IData dataIn = dataset.getData(j);
                    if (dataOut.getString("RSRV_STR1").equals(dataIn.getString("RSRV_STR1")))
                    {
                        dataOut.put("IMPORT_ERROR", "【VPMN集团编码】重复");
                        dataOut.put("IMPORT_RESULT", "false");
                        dataIn.put("IMPORT_ERROR", "【VPMN集团编码】重复");
                        dataIn.put("IMPORT_RESULT", "false");
                    }
                }
            }
            else if ("VPMNDIS".equals(importType))
            {// VPMN客户经理批量分配
                String oldStaffId = dataOut.getString("RSRV_STR1").trim();
                String newStaffId = dataOut.getString("RSRV_STR2").trim();
                IDataset managerInfos = UStaffInfoQry.qryCustManagerStaffById(newStaffId);
                if (IDataUtil.isEmpty(managerInfos))
                {
                    dataOut.put("IMPORT_ERROR", newStaffId + "不是VPMN客户经理");
                    dataOut.put("IMPORT_RESULT", "false");
                    continue;
                }
                // 查询当前操作员区域
                String operArea = data.getString("TRADE_CITY_CODE");

                // 查询新客户经理区域
                String area = managerInfos.getData(0).getString("AREA_CODE");

                // 判断业务区是否一致
                if (!"HNSJ".equals(operArea) && !"HNSJ".equals(area))
                {// 不是海南省局的工号，要进行业务区判断。
                    IDataset oldManageInfo = UStaffInfoQry.qryCustManagerStaffById(oldStaffId);
                    String oldArea = oldManageInfo.getData(0).getString("AREA_CODE");// 归属业务区关系节点串

                    if (!oldArea.equals(area))
                    {
                        dataOut.put("IMPORT_ERROR", newStaffId + "和" + oldStaffId + "不在一个业务区");
                        dataOut.put("IMPORT_RESULT", "false");
                        continue;
                    }
                }
            }
            else if ("VPMNMGR".equals(importType))
            {// VPMN客户经理批量导入
                String staffId = dataOut.getString("RSRV_STR1", "").trim();
                String userProductCode = dataOut.getString("RSRV_STR2", "").trim();
                String rightCode = dataOut.getString("RSRV_STR3", "").trim();

                // 1查询权限编码是否存在
                String isRight = StaticUtil.getStaticValue("VPN_MEMBER_STAFFRIGHT", rightCode);
                if (StringUtils.isBlank(isRight))
                {
                    dataOut.put("IMPORT_ERROR", "VPMN权限编码不符合要求");
                    dataOut.put("IMPORT_RESULT", "false");
                    continue;
                }
                // 2查询员工是否有效
                IDataset staffInfo = StaffInfoQry.qryStaffInfoByStaffId(staffId);
                if (IDataUtil.isEmpty(staffInfo) || !"0".equals(staffInfo.getData(0).getString("DIMISSION_TAG")))
                {
                    dataOut.put("IMPORT_ERROR", "该员工编码已失效");
                    dataOut.put("IMPORT_RESULT", "false");
                    continue;
                }
                // 3不是海南省局的工号，要进行业务区判断
                setRouteId(Route.CONN_CRM_CG);
                IData userInfo = UcaInfoQry.qryUserInfoBySn(userProductCode);
                if (IDataUtil.isEmpty(userInfo))
                {
                    dataOut.put("IMPORT_ERROR", "VPMN集团资料不存在！");
                    dataOut.put("IMPORT_RESULT", "false");
                    continue;
                }
                String userCityCode = userInfo.getString("CITY_CODE");
                String operCityCode = data.getString("TRADE_CITY_CODE");// getVisit().getCityCode();
                if (!operCityCode.equals(userCityCode) && !operCityCode.equals("HNSJ"))
                {
                    dataOut.put("IMPORT_ERROR", "导入VPMN编码和操作员工不在同一业务区");
                    dataOut.put("IMPORT_RESULT", "false");
                    continue;
                }

                IDataset userVpn = UserVpnInfoQry.queryVpnInfoByVpnNo(userProductCode);
                if (IDataUtil.isEmpty(userVpn))
                {
                    dataOut.put("IMPORT_ERROR", "该VPMN编码不存在");
                    dataOut.put("IMPORT_RESULT", "false");
                    continue;
                }
                IDataset useRes = CustManagerInfoQry.checkVpmnRight(staffId, userProductCode, rightCode);
                if (IDataUtil.isNotEmpty(useRes))
                {
                    dataOut.put("IMPORT_ERROR", "该员工已拥有此权限");
                    dataOut.put("IMPORT_RESULT", "false");
                    continue;
                }
            }

        }
        IDataset failDataset = new DatasetList();
        for (int i = dataset.size() - 1; i > -1; i--)
        {
            IData importData = dataset.getData(i);
            if (!importData.getBoolean("IMPORT_RESULT"))
            {
                failDataset.add(importData);

                // 将校验失败的数据从导入列表中移除
                dataset.remove(importData);
            }
        }

        if (IDataUtil.isNotEmpty(dataset))
        {
            // 根据fileId获取导入文件的文件名
            String fileId = data.getString("fileId");
            IData info = (IData)ImpExpUtil.getImpExpManager().getFileAction().query(fileId);
            String fileName = info.getString("fileName");

            String staffId = data.getString("TRADE_STAFF_ID");
            String departId = data.getString("TRADE_DEPART_ID");
            String cityCode = data.getString("TRADE_CITY_CODE");

            CustManagerInfoQry.importVpmnDisInfo(dataset, importType, fileName, staffId, departId, cityCode, getTradeEparchyCode());
            // 记录客户经理操作日志
            IData logData = new DataMap();
            logData.put("OPER_MOD", "VPMN产品客户经理批量调配导入临时表");
            logData.put("OPER_TYPE", "INS");
            logData.put("OPER_DESC", "输入参数为:" + dataset);
            logData.put("STAFF_ID", staffId);
            logData.put("DEPART_ID", departId);
            logData.put("CITY_ID", cityCode);
            logData.put("IP_ADDR", getVisit().getRemoteAddr());
            // CSAppCall.call("CS.CustManagerInfoQrySVC.insertOperLog", logData);
            LogBean.insertOperLog(logData);
        }
        return failDataset;
    }

}
