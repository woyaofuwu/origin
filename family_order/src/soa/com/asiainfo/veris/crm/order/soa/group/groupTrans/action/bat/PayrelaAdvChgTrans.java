
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;

public class PayrelaAdvChgTrans implements ITrans
{

    @Override
    public final void transRequestData(IData iData) throws Exception
    {
        addSubDataBefore(iData);
        transPayrelaAdvChgRequestData(iData);
        addSubDataAfter(iData);
    }

    private void transPayrelaAdvChgRequestData(IData iData) throws Exception
    {
        checkRequestData(iData);
    }

    // 子类重载
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

    private void checkRequestData(IData idata) throws Exception
    {
        IDataUtil.chkParam(idata, "USER_ID");// 集团用户标识

        IDataUtil.chkParam(idata, "LIMIT_TYPE");

        IDataUtil.chkParam(idata, "COMPLEMENT_TAG");

        IDataUtil.chkParam(idata, "FEE_TYPE");

        IDataUtil.chkParam(idata, "START_CYCLE_ID");

        IDataUtil.chkParam(idata, "END_CYCLE_ID");

    }

    // 子类重载
    protected void addSubDataAfter(IData idata) throws Exception
    {

    }

}
