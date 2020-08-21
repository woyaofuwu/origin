
package com.asiainfo.veris.crm.order.soa.person.busi.newterminal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.TerminalCall;

public class NewTerminalSVC extends CSBizService
{

    public IDataset IPsiSMSValuateSatisfy(IData data) throws Exception
    {
        IDataset results = new DatasetList();
        data.remove("X_TRANS_CODE");
        results.add(TerminalCall.callHwTerminal("ITF_MONNI", data).getData(0));
        if (IDataUtil.isEmpty(results))
        {
            // 调用新终端接口无返回
            CSAppException.apperr(CrmUserException.CRM_USER_1178);
        }
        return results;

    }
}
