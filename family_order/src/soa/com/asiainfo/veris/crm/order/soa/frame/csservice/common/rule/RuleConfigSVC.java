
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule;

import java.net.InetAddress;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.RuleCfgInfoQry;

public class RuleConfigSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 保存规则集
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset saveRuleBusiness(IData input) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String ruleBizId = RuleCfgInfoQry.qryMaxRuleBizRuleId(tradeTypeCode);
        input.put("RULE_BIZ_ID", ruleBizId);

        String hostName = InetAddress.getLocalHost().getHostName().toString();
        input.put("UPDATE_STAFF_ID", hostName.length() < 9 ? hostName : hostName.subSequence(0, 8));// 获得本机名称
        input.put("UPDATE_DATE", SysDateMgr.getSysDate());
        Dao.insert("TD_BRE_BUSINESS_BASE", input, Route.CONN_CRM_CEN);

        Dao.insert("TD_BRE_RULE_MANAGE", input, Route.CONN_CRM_CEN);

        IDataset ret = new DatasetList();
        IData data = new DataMap();
        data.put("RULE_BIZ_ID", ruleBizId);
        ret.add(data);
        return ret;
    }

    /**
     * 保存规则
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset saveRuleDifinition(IData input) throws Exception
    {
        String scriptId = input.getString("SCRIPT_ID");

        IDataset set = RuleCfgInfoQry.qryRuleDefinitionByScriptId(scriptId);
        if (IDataUtil.isNotEmpty(set))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "规则ID已经存在！");
        }
        String hostName = InetAddress.getLocalHost().getHostName().toString();
        input.put("UPDATE_STAFF_ID", hostName.length() < 9 ? hostName : hostName.subSequence(0, 8));// 获得本机名称
        input.put("UPDATE_DATE", SysDateMgr.getSysDate());
        Dao.insert("TD_BRE_DEFINITION", input, Route.CONN_CRM_CEN);
        return new DatasetList();
    }

    /**
     * 保存规则集和规则关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset saveRuleRelation(IData input) throws Exception
    {
        String ruleBizId = input.getString("RULE_BIZ_ID");
        String tradeTypeCode = RuleCfgInfoQry.qryRuleBusinessByRuleBizId(ruleBizId).getData(0).getString("TRADE_TYPE_CODE");
        input.put("RULE_ID", RuleCfgInfoQry.qryMaxRuleId(tradeTypeCode));
        String hostName = InetAddress.getLocalHost().getHostName().toString();
        input.put("UPDATE_STAFF_ID", hostName.length() < 9 ? hostName : hostName.subSequence(0, 8));// 获得本机名称
        input.put("UPDATE_DATE", SysDateMgr.getSysDate());
        Dao.insert("TD_BRE_RELATION", input, Route.CONN_CRM_CEN);

        String tableDataStr = input.getString("TABLE_DATA", "");
        if (StringUtils.isNotEmpty(tableDataStr))
        {
            IDataset tableDataset = new DatasetList(tableDataStr);
            for (int i = 0; i < tableDataset.size(); i++)
            {
                tableDataset.getData(i).put("RULE_ID", input.getString("RULE_ID"));
                tableDataset.getData(i).put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                tableDataset.getData(i).put("UPDATE_DATE", input.getString("UPDATE_DATE"));
            }
            Dao.insert("TD_BRE_PARAMETER", tableDataset, Route.CONN_CRM_CEN);
        }
        return new DatasetList();
    }

}
