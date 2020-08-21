
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.monitorinfo;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportQueryHarryPhone extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        // 服务返回结果集
        IDataset ids = CSAppCall.call("SS.QueryHarryPhoneSVC.queryHarryPhones", input);
        if (IDataUtil.isNotEmpty(ids))
        {
            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                String paraCode6 = data.getString("PARA_CODE6");
                String paraCode8 = data.getString("PARA_CODE8");
                if (StringUtils.isNotBlank(paraCode6))
                {

                    data.put("PARA_CODE6", StaticUtil.getStaticValue("HARRYPHONE_REASONCODE", paraCode6));
                }
                if (StringUtils.isNotBlank(paraCode8))
                {

                    data.put("PARA_CODE8", StaticUtil.getStaticValue("HARRYPHONE_PROCESSTAG", paraCode8));
                }
            }
        }
        return ids;
    }

}
