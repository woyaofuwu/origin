
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CloseSmsSVC extends CSBizService
{

    public IDataset loadChildInfo(IData data) throws Exception
    {
        IDataset dataset = new DatasetList();
        CloseSmsBean bean = BeanManager.createBean(CloseSmsBean.class);

        IDataset result = bean.qryUserMainSvcState(data);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_433, data.getString("SERIAL_NUMBER"));
        }
        else
        {
            if (!data.getString("USER_STATE_CODESET").equals(result.getData(0).getString("STATE_CODE")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_433, data.getString("SERIAL_NUMBER"));
            }
        }

        result = bean.checkRedMemberIsExists(data);
        if (IDataUtil.isNotEmpty(result))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_83);
        }

        result = StaticUtil.getStaticList("BLACK_USER_STATE");
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                String dataId = result.getData(i).getString("DATA_ID", "");
                if (dataId.equals(data.getString("USER_STATE_CODESET", "")))
                {
                    IData param = new DataMap();
                    param.put("STATE_CLOSED_TAG", "1");
                    param.put("STATE_CLOSED_NAME", result.getData(i).getString("DATA_NAME", ""));
                    dataset.add(param);
                }
            }
        }
        return dataset;
    }
}
