
package com.asiainfo.veris.crm.order.soa.person.busi.agent;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.agent.AgentBankAcctInfoQry;

public class AgentPayExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        String cityCode = input.getString("CITY_CODE");
        String startCode = input.getString("START_AGENT_CODE");
        String endCode = input.getString("END_AGENT_CODE");
        String acctCode = input.getString("ACCOUNT_CODE");
        String removeTag = input.getString("REMOVE_TAG");
        String payChannel = input.getString("PAY_CHANNEL");
        String acctype = input.getString("RSRV_STR1");
        IDataset agentPayInfos = AgentBankAcctInfoQry.queryAgentPayInfos(acctype,cityCode, startCode, endCode, acctCode, removeTag, payChannel, pagination, null);
        if (IDataUtil.isNotEmpty(agentPayInfos))
        {
            for (int i = 0, len = agentPayInfos.size(); i < len; i++)
            {
                agentPayInfos.getData(i).put("PAY_CHANNEL", StaticUtil.getStaticValue("AGENT_PAYFOR_CHANNEL_INFO", agentPayInfos.getData(i).getString("PAY_CHANNEL", "")));
                agentPayInfos.getData(i).put("RSRV_STR1", StaticUtil.getStaticValue("ACCT_TYPE_STATIC", agentPayInfos.getData(i).getString("RSRV_STR1", "")));
            }
        }
        return agentPayInfos;
    }

}
