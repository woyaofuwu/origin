
package com.asiainfo.veris.crm.order.soa.group.sentencedeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.common.query.SentenceDealQry;

public class SentenceDealSVC extends CSBizService
{

    private static final long serialVersionUID = 7489674168366515832L;

    public void dealSentenceByProc(IData input) throws Exception
    {
        String stmtName = input.getString("STMT_NAME");

        String[] ParamNames = (String[]) input.get("ParamNames");

        IData inParamData = input.getData("IN_PARAM_DATA");

        String routeId = input.getString("connectDB");

        SentenceDealQry.dealSentenceByProc(input, stmtName, ParamNames, inParamData, routeId);
    }

    public String dealSentenceBySql(IData input) throws Exception
    {
        String stmt_content = input.getString("STMT_CONTENT", "");

        StringBuilder sql = new StringBuilder(stmt_content);
        String routeId = input.getString("connectDB");

        int count = SentenceDealQry.dealSentenceBySql(sql, input, routeId);

        return String.valueOf(count);
    }

    public IDataset querySentence(IData input) throws Exception
    {
        IDataset dataset = SentenceDealQry.querySentence(input);
        return dataset;
    }

    public IDataset showSentence(IData input) throws Exception
    {
        IDataset dataset = SentenceDealQry.showSentence(input);
        return dataset;
    }

}
