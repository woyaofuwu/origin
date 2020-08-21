
package com.asiainfo.veris.crm.order.web.group.sentencedeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SentenceDeal extends CSBasePage
{

    public void dealSentence(IRequestCycle cycle) throws Exception
    {
        /*
         * hide_STMT_ID hide_STMT_TYPE hide_STMT_CONTENT hide_STMT_NAME hide_PS_DB hide_OUT_PARAM hide_IN_PARAM_STR
         * hide_IN_PARAM
         */
        IData hideData = getData("hide");

        String inStr = hideData.getString("IN_PARAM");

        IData inParamData = new DataMap(inStr);
        String outStr = hideData.getString("OUT_PARAM");
        String connectDB = hideData.getString("PS_DB");
        String stmtContent = hideData.getString("STMT_CONTENT");
        String inParamStr = hideData.getString("IN_PARAM_STR");
        String stmtId = hideData.getString("STMT_ID");
        String stmtName = hideData.getString("STMT_NAME");

        hideData.put("STMT_NAME", stmtName);

        hideData.put("STMT_ID", stmtId);

        hideData.put("STMT_CONTENT", stmtContent);

        hideData.put("IN_PARAM_DATA", inParamData);

        hideData.put("connectDB", connectDB);

        IData outData = new DataMap();

        // 语句类型
        String stmtType = hideData.getString("STMT_TYPE");

        hideData.put("STMT_TYPE", stmtType);

        if ("0".equals(stmtType))
        {
            // SQL语句处理块
            IDataset iDataset = CSViewCall.call(this, "SS.SentenceDealSVC.dealSentenceBySql", hideData);

            String returnCountString = (String) iDataset.get(0);

            int returnCount = Integer.parseInt(returnCountString);

            if (returnCount >= 0)
            {
                outData.put("v_Resultinfo", "执行成功,影响数据 " + returnCount + " 行!");
            }
            else
            {
                outData.put("v_Resultinfo", "执行失败!");
            }
        }
        else if ("1".equals(stmtType))
        {
            // 存储过程处理
            String splitStr = inParamStr;

            if (!"".equals(outStr) && null != outStr)
            {
                splitStr += "," + outStr;
            }

            String[] ParamNames = splitStr.split(",");

            hideData.put("ParamNames", ParamNames);

            // 执行存储过程
            CSViewCall.call(this, "SS.SentenceDealSVC.dealSentenceByProc", hideData);

            // 获取存储过程输出值
            if (!"".equals(outStr) && null != outStr)
            {
                String outStrs[] = outStr.split(",");
                for (int j = 0; j < outStrs.length; j++)
                {
                    String key = outStrs[j];
                    String value = inParamData.getString(outStrs[j]);
                    outData.put(key, value);
                }
            }
        }

        setAjax(outData);
    }

    public void onInitSentence(IRequestCycle cycle) throws Exception
    {

    }

    public void querySentence(IRequestCycle cycle) throws Exception
    {

        IData data = getData("cond", true);

        IDataset iDataset = CSViewCall.call(this, "SS.SentenceDealSVC.querySentence", data);

        setInfos(iDataset);

    }

    public abstract void setChoiceMap(IData choiceMap);

    public abstract void setCondition(IData cond);

    public abstract void setDisFlag(String disFlag);

    public abstract void setInfos(IDataset atoms);

    public abstract void setInParams(IDataset inParams);

    public void showSentence(IRequestCycle cycle) throws Exception
    {

        IData data = getData("cond", true);

        String stmt_id = data.getString("Choice_ID", "");

        data.put("STMT_ID", stmt_id);

        IDataset iDataset = CSViewCall.call(this, "SS.SentenceDealSVC.showSentence", data);

        if (IDataUtil.isEmpty(iDataset))
        {
            return;
        }

        IData choiceMap = iDataset.getData(0);

        String paramsStr = choiceMap.getString("RSRV_STR1");

        String params[] = paramsStr.split(",");

        IDataset inparams = new DatasetList();
        for (int j = 0; j < params.length; j++)
        {
            IData map = new DataMap();
            map.put("KEY", params[j]);
            map.put("VAL", "");
            inparams.add(map);
        }

        setChoiceMap(choiceMap);
        setInParams(inparams);
        setDisFlag("true");

    }

}
