
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public final class SimpleRuleCfgBean extends CSBizBean
{

    private void createBusiness(IData data) throws Exception
    {
        IData inParams = new DataMap();

        inParams.put("RULE_BIZ_ID", data.getString("RULE_BIZ_ID"));
        inParams.put("RULE_TYPE_CODE", "TradeCheckBefore");
        inParams.put("RULE_KIND_CODE", "TradeCheckSuperLimit");
        inParams.put("RULE_TWIG_CODE", data.getString("RULE_TWIG_CODE"));
        inParams.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        inParams.put("PRODUCT_ID", "-1");
        inParams.put("BRAND_CODE", "ZZZZ");
        inParams.put("EPARCHY_CODE", "ZZZZ");
        inParams.put("IN_MODE_CODE", "0123456789AHIJ");
        inParams.put("PACKAGE_ID", "-1");
        inParams.put("SPEC_CDTION", "");
        inParams.put("RIGHT_CODE", "");
        inParams.put("EXECUTE_ORDER", "1");
        inParams.put("RULE_CHECK_MODE", "0");
        inParams.put("TIPS_TYPE", data.getString("TIPS_TYPE"));
        inParams.put("TIPS_INFO", "业务受理前特殊业务限制：" + data.getString("TIPS_INFO"));
        inParams.put("START_DATE", data.getString("START_DATE"));
        inParams.put("END_DATE", data.getString("END_DATE"));
        inParams.put("REMARK", "前台简单配置");
        inParams.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
        inParams.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
        inParams.put("UPDATE_DATE", data.getString("UPDATE_DATE"));

        Dao.insert("td_bre_business", inParams);
    }

    private void createDefinition(IData data) throws Exception
    {
        IData inParams = new DataMap();

        inParams.put("SCRIPT_ID", data.getString("SCRIPT_ID"));
        inParams.put("SCRIPT_DESCRIPTION", data.getString("SCRIPT_DESCRIPTION"));
        inParams.put("SCRIPT_PATH", data.getString("SCRIPT_PATH"));
        inParams.put("SCRIPT_TYPE", data.getString("SCRIPT_TYPE"));
        inParams.put("SCRIPT_METHOD", data.getString("SCRIPT_METHOD"));
        inParams.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
        inParams.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
        inParams.put("UPDATE_DATE", data.getString("UPDATE_DATE"));

        Dao.insert("td_bre_definition", inParams);
    }

    private void createRelation(IData data) throws Exception
    {
        IData inParams = new DataMap();

        inParams.put("RULE_BIZ_ID", data.getString("RULE_BIZ_ID"));
        inParams.put("RULE_ID", data.getString("RULE_ID"));
        inParams.put("RULE_DESCRIPTION", "");
        inParams.put("SCRIPT_ID", data.getString("SCRIPT_ID"));
        inParams.put("EXECUTE_ORDER", "1");
        inParams.put("IS_REVOLT", "0");
        inParams.put("RIGHT_CODE", "");
        inParams.put("START_DATE", data.getString("START_DATE"));
        inParams.put("END_DATE", data.getString("END_DATE"));
        inParams.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
        inParams.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
        inParams.put("UPDATE_DATE", data.getString("UPDATE_DATE"));

        Dao.insert("td_bre_relation", inParams);
    }

    public IDataset createSimpleRule(IData data) throws Exception
    {
        // TODO 临时用这个序列
        data.put("RULE_BIZ_ID", SeqMgr.getTradeId());
        data.put("RULE_ID", SeqMgr.getTradeId());
        data.put("START_DATE", SysDateMgr.getSysTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("UPDATE_DATE", SysDateMgr.getSysTime());

        // 生成 td_bre_business
        createBusiness(data);

        // 生成 td_bre_relation
        createRelation(data);

        // 生成 td_bre_definition
        createDefinition(data);

        return null;
    }
}
