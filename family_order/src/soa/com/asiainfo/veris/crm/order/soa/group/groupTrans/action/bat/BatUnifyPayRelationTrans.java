
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatUnifyPayRelationTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        
        addSubDataBefore(batData);
        transPayRelationRequestData(batData);
        addSubDataAfter(batData);
        
    }

    /**
     * 
     * @param idata
     * @throws Exception
     */
    protected void addSubDataBefore(IData idata) throws Exception
    {
        String condStr = idata.getString("CODING_STR", "");

        if (StringUtils.isBlank(condStr))
        {
            String batchTaskId = IDataUtil.getMandaData(idata, "BATCH_TASK_ID");
            condStr = BatTradeInfoQry.getTaskCondString(batchTaskId);
        }

        if (StringUtils.isNotBlank(condStr))
        {
            idata.putAll(new DataMap(condStr));
        }
        
        // 账目编码
        idata.put("PAYITEM_CODE", idata.getString("PAYITEM_CODE"));
        idata.put("newSnInfo_CheckAll", idata.getString("newSnInfo_CheckAll"));
        /*add by chenzg@20180703 REQ201804280001集团合同管理界面优化需求 批次号，生成集团业务稽核工单需要，要求一个批次只生成一笔稽核工单*/
        idata.put("ORIG_BATCH_ID", idata.getString("BATCH_ID")); 
    }
    
    /**
     * 
     * @param iData
     * @throws Exception
     */
    private void transPayRelationRequestData(IData iData) throws Exception
    {
        checkRequestData(iData);
    }
    
    /**
     * 
     * @param idata
     * @throws Exception
     */
    private void checkRequestData(IData idata) throws Exception
    {
        IDataUtil.chkParam(idata, "USER_ID");// 集团用户标识

        IDataUtil.chkParam(idata, "LIMIT_TYPE");

        IDataUtil.chkParam(idata, "COMPLEMENT_TAG");

        IDataUtil.chkParam(idata, "FEE_TYPE");

        IDataUtil.chkParam(idata, "START_CYCLE_ID");

        IDataUtil.chkParam(idata, "END_CYCLE_ID");

        IDataUtil.chkParam(idata, "SERIAL_NUMBER");
        
        String serialNumber = idata.getString("SERIAL_NUMBER","");
        
        
        //查询用户信息，并且可以判断是否是集团产品用户
        IData memInfo = UserInfoQry.getMebUserInfoBySN(serialNumber);
        if(IDataUtil.isEmpty(memInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }
        
        String isGrpSn = memInfo.getString("IsGrpSn");
        if(StringUtils.isNotBlank(isGrpSn) && StringUtils.equals("Yes", isGrpSn))
        {
            //集团产品用户
            idata.put("IsGrpNum", "true");
        } else 
        {
            idata.put("IsGrpNum", "false");
        }
        
    }
    
    /**
     * 
     * @param idata
     * @throws Exception
     */
    protected void addSubDataAfter(IData idata) throws Exception
    {

    }
}
