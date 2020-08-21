
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.PreTradeInfoQry;

public class ChangePhonePreInfoQueryBean extends CSBizBean
{

    public IDataset queryChangeCardInfo(IData input) throws Exception
    {

        String serial_number = input.getString("SERIAL_NUMBER", "");
        IData data = new DataMap();
        IDataset result = new DatasetList();

        String param_code1 = "";
        if ("".equals(serial_number))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_201); // 号码为空
        }
        // --------获取参数-----
        String subsys_code = "CSM";
        String param_attr = "8000";
        String param_code = "ALTSN_PRE_TRADETYPECODE";
        IDataset dataset = CommparaInfoQry.getCommNetInfo(subsys_code, param_attr, param_code);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_202);// "8000","没有配置"+param_attr+"参数！"
        }
        else
        {
            param_code1 = dataset.getData(0).getString("PARA_CODE1", "");
        }
        // --------end -----
        IDataset dataset1 = PreTradeInfoQry.queryPreStatus1(serial_number, param_code1);
        if (IDataUtil.isNotEmpty(dataset1))
        {
            data = dataset1.getData(0);
            if ("1".equals(data.getString("IN_MODE_CODE", "")))
                data.put("IN_MODE_CODE", "营业厅");
            else
                data.put("IN_MODE_CODE", "其他");
        }
        result.add(data);

        return result;
    }

}
