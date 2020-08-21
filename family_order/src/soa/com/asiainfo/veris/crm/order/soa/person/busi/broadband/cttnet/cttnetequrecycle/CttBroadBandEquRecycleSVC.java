
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetequrecycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class CttBroadBandEquRecycleSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset checkRes(IData param) throws Exception
    {
        param.put("RES_TYPE_CODE", "W");
        param.put("USER_ID_A", "-1");
        if ("0".equals(param.getString("RES_CODE", "0"))) // 前台默认为0
        {
            param.put("RES_CODE", "-1");
        }
        // data 为一个数组 USER_ID_A、 USER_ID、RES_TYPE_CODE、RES_CODE、RES_KIND_CODE
        IDataset dataset = UserResInfoQry.checkRes(param);
        // 如果存在用户资源信息，则校验通过
        if (dataset != null && dataset.size() > 0)
        {
            return dataset;
        }
        else
        {
            return null;
        }
    }

    public IDataset getOldBroadBandInfo(IData param) throws Exception
    {
        IDataset addrInfoDataset = WidenetInfoQry.getUserWidenetInfo(param.getString("USER_ID"));// BroadBandInfoQry.queryBroadBandAddressInfo(param);
        if (addrInfoDataset.isEmpty())
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_13, param.getString("SERIAL_NUMBER"));
        }
        return addrInfoDataset;
    }
}
