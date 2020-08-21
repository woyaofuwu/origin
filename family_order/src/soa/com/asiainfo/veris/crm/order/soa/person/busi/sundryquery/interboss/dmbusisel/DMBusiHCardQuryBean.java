
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class DMBusiHCardQuryBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(DMBusiHCardQuryBean.class);

    public IDataset getHCardInfo(IData conParams) throws Exception
    {
        String phonenum = conParams.getString("PHONENUM", "");
        String imeinum = conParams.getString("IMEINUM", "");

        IData resData = IBossCall.queryDMBusiHCardIBOSS(phonenum, imeinum);

        // IData resData = new DataMap(); resData.put("RESULTSTATUS", "0"); resData.put("SOFTWAREEDITIONNUM",
        // "09876543211234567890"); resData.put("MATCHTIME", SysDateMgr.getFirstDayOfThisMonth());
        // resData.put("PHONENUM", "13619240267"); resData.put("IMEINUM", "860143005681702"); resData.put("TERMINALID",
        // "352094000161619"); resData.put("FAILTYPE", "1010"); resData.put("FAILREASON", "失败原因");

        IDataset resDataset = new DatasetList();

        String resultStatus = (String) resData.get("RESULTSTATUS");

        IData info = new DataMap();
        info.put("RESULTSTATUS", resultStatus);

        if (resultStatus.equals("1"))
        {
            String failType = (String) resData.get("FAILTYPE");
            String failReason = (String) resData.get("FAILREASON");
            info = new DataMap();
            info.put("RESULTSTATUS", "失败");
            info.put("FAILTYPE", failType);
            info.put("FAILREASON", failReason);
            resDataset.add(info);

            // common.error(failReason);
            CSAppException.apperr(DMBusiException.CRM_DM_1, failReason);
        }
        else if (resultStatus.equals("0"))
        {

            String softwareeditionnum = "SOFTWAREEDITIONNUM";

            Object obj = resData.get(softwareeditionnum);
            if (obj instanceof String)
            {
                info.put("RESULTSTATUS", "成功");
                info.put("SOFTWAREEDITIONNUM", resData.get("SOFTWAREEDITIONNUM"));
                info.put("MATCHTIME", resData.get("MATCHTIME"));
                info.put("PHONENUM", resData.get("PHONENUM"));
                info.put("IMEINUM", resData.get("IMEINUM"));
                info.put("TERMINALID", resData.get("TERMINALID"));

                resDataset.add(info);
            }
            else
            {
                IDataset softwareeditionnumSet = (IDataset) resData.get(softwareeditionnum);

                IDataset matchtimeSet = (IDataset) resData.get("MATCHTIME");
                IDataset phonenumSet = (IDataset) resData.get("PHONENUM");
                IDataset imeinumSet = (IDataset) resData.get("IMEINUM");
                IDataset sterminalidSet = (IDataset) resData.get("TERMINALID");

                for (int i = 0; i < softwareeditionnumSet.size(); i++)
                {
                    info = new DataMap();
                    info.put("RESULTSTATUS", "成功");
                    info.put("SOFTWAREEDITIONNUM", softwareeditionnumSet.get(i));
                    info.put("MATCHTIME", matchtimeSet.get(i));
                    info.put("PHONENUM", phonenumSet.get(i));
                    info.put("IMEINUM", imeinumSet.get(i));
                    info.put("TERMINALID", sterminalidSet.get(i));
                    resDataset.add(info);
                }
            }
        }

        return resDataset;
    }
}
