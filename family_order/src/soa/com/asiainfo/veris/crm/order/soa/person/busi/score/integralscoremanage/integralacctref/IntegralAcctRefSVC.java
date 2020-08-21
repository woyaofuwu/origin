
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.integralacctref;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;

public class IntegralAcctRefSVC extends CSBizService
{
    /**
     * @Des 积分账户修改参数调整
     * @author huangsl
     * @param data
     * @return
     * @throws Exception
     */
    public void dataCheck(IData data) throws Exception
    {
        if (data.getString("SERIAL_NUMBER", "").equals(""))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_115);
        }

        if (data.getString("IN_MODE_CODE", "").equals(""))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1202);
        }

        if (!data.getString("PSPT_TYPE_CODE", "").equals(""))
        {
            data.put("cond_PSP_TYPE_CODE", data.getString("PSPT_TYPE_CODE", ""));
        }

        if (!data.getString("PSPT_ID", "").equals(""))
        {
            data.put("cond_PSP_ID", data.getString("PSPT_ID", ""));
        }

        if (!data.getString("EMAIL", "").equals(""))
        {
            data.put("cond_EMAIL", data.getString("EMAIL", ""));
        }

        if (!data.getString("ADDRESS", "").equals(""))
        {
            data.put("cond_ADDRESS", data.getString("ADDRESS", ""));
        }

        if (!data.getString("START_DATE", "").equals(""))
        {
            data.put("cond_START_DATE", data.getString("START_DATE", ""));
        }

        if (!data.getString("END_DATE", "").equals(""))
        {
            data.put("cond_END_DATE", data.getString("END_DATE", ""));
        }

        if (!data.getString("CONTRACT_PHONE", "").equals(""))
        {
            data.put("cond_CONTRACT_PHONE", data.getString("CONTRACT_PHONE", ""));
        }
    }

    public IDataset getCommInfo(IData input) throws Exception
    {
        return ScoreAcctInfoQry.queryScoreAcctInfoByUserId(input.getString("USER_ID"), "10A", Route.CONN_CRM_CEN);
    }

    public IDataset queryScoreAcctList(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        if ("0".equals(input.getString("COND_QUERY_LIMIT")))
        {
            results = ScoreAcctInfoQry.queryScoreAcctInfoByPsptId(input.getString("COND_QUERY_VALUE"), this.getPagination());
        }
        else if ("1".equals(input.getString("COND_QUERY_LIMIT")))
        {
            results = ScoreAcctInfoQry.queryScoreAcctInfoByEmail(input.getString("COND_QUERY_VALUE"), "10A", this.getPagination());
        }
        else if ("2".equals(input.getString("COND_QUERY_LIMIdT")))
        {
            results = ScoreAcctInfoQry.queryScoreAcctInfoByContractSn(input.getString("COND_QUERY_VALUE"), "10A", this.getPagination());
        }
        else
        {
            results = ScoreAcctInfoQry.queryScoreAcctInfoBySn(input.getString("COND_QUERY_VALUE"), "10A", Route.CONN_CRM_CEN);
        }
        if (IDataUtil.isEmpty(results))
        {
        	CSAppException.apperr(ScoreException.CRM_SCORE_4);
        }

        // 模糊化处理
        int size = results.size();
        for (int i = 0; i < size; i++)
        {
            IData data = results.getData(i);
            if (!"".equals(data.getString("PSPT_ID", "")) && ("0".equals(data.getString("PSPT_TYPE_CODE", "")) || "1".equals(data.getString("PSPT_TYPE_CODE", ""))))
            {
                String psptID = data.getString("PSPT_ID", "");
                String psptIDReplace = "";
                for (int j = 0; j < psptID.length() - 1; j++)
                {
                    psptIDReplace += "*";
                }
                psptIDReplace += psptID.substring(psptID.length() - 1);
                data.put("PSPT_ID", psptIDReplace);
            }
        }
        return results;
    }
}
