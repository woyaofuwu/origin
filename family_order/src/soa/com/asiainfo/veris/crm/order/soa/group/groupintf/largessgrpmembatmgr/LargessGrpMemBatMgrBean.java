
package com.asiainfo.veris.crm.order.soa.group.groupintf.largessgrpmembatmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;

public class LargessGrpMemBatMgrBean extends CSBizBean
{

    protected static final Logger logger = Logger.getLogger(LargessGrpMemBatMgrBean.class);
    
    /**
     * 创建畅享流量成员新增的批量任务
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatLargessLimitationMember(IData inParam) throws Exception
    {
        StringBuilder builder = new StringBuilder(50);
        IDataset relaList = new DatasetList();
        
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String grpSn = inParam.getString("GRP_SERIAL_NUMBER");
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<SERIAL_NUMBER的参数值>>>>>>>>>>>>>>>>>>>>" + serialNumber);
        }
        
        if(StringUtils.isNotBlank(serialNumber)){
            String[] strs = StringUtils.split(serialNumber, ",");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                data.put("SERIAL_NUMBER", strs[i].trim());
                relaList.add(data);
            }
        }
        
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<打印relaList的参数值>>>>>>>>>>>>>>>>>>>>");
            if (IDataUtil.isNotEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值>>>>>>>>>>>>>>>>>>>>" + relaList);
            } else if(IDataUtil.isEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值为空>>>>>>>>>>>>>>>>>>>>"); 
            } 
        }
        
        String limitfee = inParam.getString("LIMIT_FEE");
        String enet_info_query = inParam.getString("ENET_INFO_QUERY");
        String inmodecode = inParam.getString("IN_MODE_CODE","0");
        String systime = SysDateMgr.getSysTime();
        
        // 创建集团成员的批量任务
        IData mebBatData = new DataMap();
        
        IData mebCondStrData = new DataMap();
        mebCondStrData.put("BindTeam", enet_info_query);
        mebCondStrData.put("DISCNT_CODE", "73468");
        mebCondStrData.put("LimitFee", limitfee);
        mebCondStrData.put("GRP_SERIAL_NUMBER", grpSn);
        
        
        mebBatData.put("BATCH_OPER_TYPE", "OPENGROUPGFFFDISCNT");
        
        String batTaskName = inParam.getString("BAT_TASK_NAME","");
        if(StringUtils.isBlank(batTaskName)){
            batTaskName = "畅享流量批量分配-接口";
        }
        
        mebBatData.put("BATCH_TASK_NAME", batTaskName);
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inmodecode);
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";
        mebBatchId = BatDealBean.createBat(mebBatData, relaList);
        builder.append(mebBatchId);
        
        IData retData = new DataMap();
        retData.put("BATCH_ID", builder.toString());

        return IDataUtil.idToIds(retData);
    }    
}
