
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changeacctdiscnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctDiscntQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class ChangeAcctDiscntSVC extends OrderService
{

    /**
     * 获取可办理优惠列表
     * 
     * @author yuezy
     * @param input
     * @throws Exception
     */
    public IDataset getAcctDiscntList(IData input) throws Exception
    {
        IDataset res = new DatasetList();
        IData backData = new DataMap();
        backData.put("DISCNT_CODE", "-1");
        backData.put("DISCNT_NAME", "退订");
        res.add(backData);

        IDataset resTemp = CommparaInfoQry.getOnlyByAttr("CSM", "9908", getVisit().getStaffEparchyCode());
        for (int i = 0; i < resTemp.size(); i++)
        {
            IData resData = new DataMap();
            resData.put("DISCNT_CODE", resTemp.getData(i).getString("PARAM_CODE"));
            resData.put("DISCNT_NAME", resTemp.getData(i).getString("PARAM_NAME"));
            res.add(resData);
        }

        return res;

    }

    /**
     * 获取原有优惠关系
     * 
     * @author yuezy
     * @param input
     * @throws Exception
     */
    public IDataset getOldAcctDisnctInfo(IData input) throws Exception
    {
        String acct_id = input.getString("ACCT_ID");
        boolean checkRes = true;
        IDataset returnDataset = new DatasetList();
        IDataset tempSet = AcctDiscntQry.getAcctDisInfoByAcctId(acct_id);
        if (tempSet.size() == 1)
        {
            if (tempSet.getData(0).getString("START_DATE").substring(0, 10).equals(SysDateMgr.getFirstDayOfNextMonth4WEB()))
            {
                checkRes = false;
                CSAppException.apperr(CrmUserException.CRM_USER_1186);
            }
        }
        else if (tempSet.size() > 1)
        {
            checkRes = false;
        }

        IDataset acctDiscntTradeSet = AcctDiscntQry.getAcctDisnctTradeByAcctId(acct_id);
        if (acctDiscntTradeSet.size() > 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1186);
            checkRes = false;
        }

        if (checkRes)
        {
            if (tempSet.size() > 0)
            {

                String distinctCode = tempSet.getData(0).getString("DISCNT_CODE");
                IDataset resTemp = CommparaInfoQry.getCommpara("CSM", "9908", distinctCode, getVisit().getStaffEparchyCode());
                if (IDataUtil.isNotEmpty(resTemp))
                {
                    String disnct_name = resTemp.getData(0).getString("PARAM_NAME");
                    tempSet.getData(0).put("DISCNT_NAME", disnct_name);
                }
                returnDataset.add(tempSet.getData(0));
            }
            else
            {
                IData temp = new DataMap();
                temp.put("DISCNT_CODE", "");
                returnDataset.add(temp);
            }
        }
        return returnDataset;
    }

    /**
     * 固话账户优惠变更 TRADE_TYPE_CODE:9704
     **/

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "9704");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "9704");
    }

}
