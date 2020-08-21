
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.PreTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAltsnInfoQry;

public class ChangePhoneQueryBean extends CSBizBean
{

    public IDataset queryAltSnInfo(IData input) throws Exception
    {
        IData data = new DataMap();
        IDataset result = new DatasetList();
        String status = "";
        String serial_number = input.getString("SERIAL_NUMBER", "");
        if ("".equals(serial_number))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_201); // 号码为空
        }
        IDataset dataset = PreTradeInfoQry.queryAltCardPreInfo(serial_number);
        if (IDataUtil.isNotEmpty(dataset))
        { // 预登记信息

            data.put("STATUS", "1"); // 1:预约已登记，2：改号已激活
            data.put("ACCEPT_DATE", dataset.getData(0).getString("PRE_ACCEPT_TIME", ""));
            data.put("INVALID_DATE", dataset.getData(0).getString("PRE_INVALID_TIME", ""));
            data.putAll(dataset.getData(0));
            data.put("X_RESULTINFO", "OK");
            data.put("X_RECORDNUM", "1");
            data.put("X_RESULTCODE", "0");
            result.add(data);

        }
        else
        { // 预登记信息查询不到，查询处理信息

            String paramAttr = "8000";
            String paramCode = "ALT_AUTOCANCEL_MONTH";
            String interval = "";
            String subsysCode = "CSM";
            IDataset dataset2 = CommparaInfoQry.getCommNetInfo(subsysCode, paramAttr, paramCode);
            if (IDataUtil.isNotEmpty(dataset2))
            {
                interval = dataset2.getData(0).getString("PARA_CODE1", "");
            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_202); // "8000","没有配置"+param_attr+"参数！"
            }
            String rela_type = "1";
            String satus = "1";
            IDataset dataset3 = UserAltsnInfoQry.queryReqInfoBySn(serial_number, rela_type, satus, interval);
            if (IDataUtil.isNotEmpty(dataset3))
            {
                data.put("ACCEPT_DATE", dataset3.getData(0).getString("ACTIVATE_TIME", ""));
                if ("".equals(dataset3.getData(0).getString("EXPIRE_DEAL_TAG", "")))
                {
                    data.put("INVALID_DATE", dataset3.getData(0).getString("INVALID_DATE", ""));
                }
                else
                {
                    data.put("INVALID_DATE", SysDateMgr.getEndCycle205012());
                }
                data.put("STATUS", "2"); // 1:预约已登记，2：改号已激活
                data.put("X_RESULTINFO", "OK");
                data.put("X_RECORDNUM", "1");
                data.put("X_RESULTCODE", "0");
                result.add(data);
            }
            else
            {
                data.put("STATUS", "-1"); // 未查询到改号信息
                result.add(data);
            }
        }

        return result;
    }

}
