package com.asiainfo.veris.crm.order.soa.group.esop.esopattach;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttachBean;

/**
 * eos附件处理
 * Created on 2018/1/9.
 */
public class EsopAttachTaskSVC extends GroupOrderService
{
    public IDataset dealAttachInfo(IData data) throws Exception
    {
        IDataset ftpFileInfos = new DatasetList();
        IDataset workFormAttachInfos = new DatasetList();
        IDataset tempEsopTabAttachInfos = new DatasetList();
        IDataset params = data.getDataset("PARAM");
        for (int i = 0; i < params.size(); i++)
        {
            IData param = params.getData(i);
            IDataset results = WorkformAttachBean.qryOldWorkFormAttachInfo(param.getString("SEQ"),param.getString("SUB_IBSYSID"));
            if (IDataUtil.isEmpty(results))
            {
                return new DatasetList();
            }
            String fileId = param.getString("FILE_ID");
            IData oldAttachInfo = results.getData(0);
            // 1- 插入ftpfile表
            IData ftpFileInfo = dealFtpFileInfo(fileId, oldAttachInfo);
            // 2- 插入tf_b_workform_attach表
            IData workFormAttachInfo = dealWorkFormAttachInfo(fileId, oldAttachInfo);
            // 3- 更新TEMP_ESOP_TAB_ATTACH表状态
            IData tempEsopTabAttachInfo = dealTempEsopTabAttach(fileId, oldAttachInfo);
            ftpFileInfos.add(ftpFileInfo);
            workFormAttachInfos.add(workFormAttachInfo);
            tempEsopTabAttachInfos.add(tempEsopTabAttachInfo);
        }

        // 插表入库
        EsopAttachBean.insertFtpFile(ftpFileInfos);
        WorkformAttachBean.insertWorkformAttach(workFormAttachInfos);
//        EsopAttachBean.insertTempEsopTabAttach(tempEsopTabAttachInfos);

        return new DatasetList();
    }

    private IData dealTempEsopTabAttach(String fileId, IData oldAttachInfo)
    {
        IData tempData = new DataMap();
        tempData.put("IBSYSID", oldAttachInfo.getString("IBSYSID"));
        tempData.put("SEQ", oldAttachInfo.getString("SEQ"));
        tempData.put("SUB_IBSYSID", oldAttachInfo.getString("SUB_IBSYSID"));
        tempData.put("FILE_ID", fileId);
        tempData.put("STATUS", "Y");
        return tempData;
    }

    private IData dealWorkFormAttachInfo(String fileId, IData oldAttachInfo)
    {
        IData workFormAttachInfo = new DataMap();
        workFormAttachInfo.putAll(oldAttachInfo);
        workFormAttachInfo.put("FILE_ID", fileId);
        workFormAttachInfo.put("UPDATE_TIME", oldAttachInfo.getString("INSERT_TIME"));
        workFormAttachInfo.put("VALID_TAG", "0");
        workFormAttachInfo.put("RECORD_NUM", "0");
        workFormAttachInfo.put("GROUP_SEQ", "0");
        workFormAttachInfo.put("ACCEPT_MONTH", oldAttachInfo.getString("MONTH"));

        return workFormAttachInfo;
    }

    private IData dealFtpFileInfo(String fileId, IData oldAttachInfo)
    {
        IData ftpFileInfo = new DataMap();
        ftpFileInfo.put("FILE_ID", fileId);
        ftpFileInfo.put("FILE_NAME", oldAttachInfo.getString("DISPLAY_NAME"));
        ftpFileInfo.put("FTP_SITE", "esop");
        ftpFileInfo.put("FILE_PATH", "upload/attach");
        ftpFileInfo.put("FILE_TYPE", "1");
        ftpFileInfo.put("FILE_SIZE", oldAttachInfo.getString("ATTACH_LENGTH"));
        ftpFileInfo.put("FILE_KIND", "1");
        ftpFileInfo.put("CREA_STAFF", oldAttachInfo.getString("ATTACH_STAFF_ID"));
        ftpFileInfo.put("CREA_TIME", oldAttachInfo.getString("INSERT_TIME"));

        return ftpFileInfo;
    }
}
