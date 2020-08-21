
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.log.LogBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ApnUserMgrImportTask extends ImportTaskExecutor
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
            
            //排重
            for (int j = i + 1; j < size; j++)
            {
            	IData dataIn = dataset.getData(j);
                if (dataOut.getString("RSRV_STR1").equals(dataIn.getString("RSRV_STR1")))
                {
                    //dataOut.put("IMPORT_ERROR", "【手机号码】重复");
                    //dataOut.put("IMPORT_RESULT", "false");
                    dataIn.put("IMPORT_ERROR", "【手机号码】重复");
                    dataIn.put("IMPORT_RESULT", "false");
                }
            }
            
            String impResult = dataOut.getString("IMPORT_RESULT","");
            if("false".equals(impResult))
            {
            	continue;
            }
            
            String serialNumber = dataOut.getString("RSRV_STR1", "").trim();
            setRouteId(Route.CONN_CRM_CG);
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                dataOut.put("IMPORT_ERROR", "用户资料不存在!");
                dataOut.put("IMPORT_RESULT", "false");
                continue;
            }
            
            String userId = userInfo.getString("USER_ID","");
            IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValue(userId, "USER_APNTAG", serialNumber);
            if(IDataUtil.isNotEmpty(otherInfos))
            {
            	dataOut.put("IMPORT_ERROR", "用户已经是APN用户!");
                dataOut.put("IMPORT_RESULT", "false");
                continue;
            }
        }
        
        IDataset failDataset = new DatasetList();
        for (int i = dataset.size() - 1; i > -1; i--)
        {
            IData importData = dataset.getData(i);
            if (!importData.getBoolean("IMPORT_RESULT"))
            {
                failDataset.add(importData);
                //将校验失败的数据从导入列表中移除
                dataset.remove(importData);
            }
        }

        if (IDataUtil.isNotEmpty(dataset))
        {
            //根据fileId获取导入文件的文件名
            String fileId = data.getString("fileId");
            IData info = (IData)ImpExpUtil.getImpExpManager().getFileAction().query(fileId);
            String fileName = info.getString("fileName");

            String staffId = data.getString("TRADE_STAFF_ID");
            String departId = data.getString("TRADE_DEPART_ID");
            String cityCode = data.getString("TRADE_CITY_CODE");

            CustManagerInfoQry.importVpmnDisInfo(dataset, importType, fileName, 
            		staffId, departId, cityCode, getTradeEparchyCode());
            
            //记录操作日志
            IData logData = new DataMap();
            logData.put("OPER_MOD", "APN用户批量导入临时表");
            logData.put("OPER_TYPE", "INS");
            logData.put("OPER_DESC", "输入参数为:" + dataset);
            logData.put("STAFF_ID", staffId);
            logData.put("DEPART_ID", departId);
            logData.put("CITY_ID", cityCode);
            logData.put("IP_ADDR", getVisit().getRemoteAddr());
            LogBean.insertOperLog(logData);
        }
        
        return failDataset;
    }

}
