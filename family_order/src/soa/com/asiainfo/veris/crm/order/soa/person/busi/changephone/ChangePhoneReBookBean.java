
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAltsnInfoQry;

public class ChangePhoneReBookBean extends CSBizBean
{

    public IDataset reBookAltSnInfo(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        IData data = new DataMap();
        String interval = "";
        // ------------获取参数---
        String param_attr = "8000";
        String param_code = "ALT_AUTOCANCEL_MONTH";
        String subsysCode = "CSM";
        IDataset dataset = new DatasetList();
        dataset = CommparaInfoQry.getCommNetInfo(subsysCode, param_attr, param_code);
        if (IDataUtil.isNotEmpty(dataset))
        {
            interval = dataset.getData(0).getString("PARA_CODE1", "");
        }
        else
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_202);// "8000","没有配置"+param_attr+"参数！"
        }
        // ----------获取参数 end ------
        String serial_number = input.getString("SERIAL_NUMBER", "");
        String rela_type = "1";
        String status = "1";
        IDataset dataset2 = new DatasetList();
        dataset2 = UserAltsnInfoQry.queryUserAnswerBySn(serial_number, status, rela_type, interval);
        if (IDataUtil.isNull(dataset2))
        { // 发起方的校验:直接报错
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_204); // 8002","该号码不是改号用户，或已过期不能续约！
        }
        String renew_Answer_flag = input.getString("OPER_CODE", "");

        int i = 0;

        // 更新续约信息
        i = this.updataReBookInfo(serial_number, renew_Answer_flag, status, rela_type);

        if (i <= 0)
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_205);// 执行失败
        }
        data.put("X_RESULTINFO", "OK");
        data.put("X_RECORDNUM", "1");
        data.put("X_RESULTCODE", "0");
        result.add(data);
        return result;
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public int updataReBookInfo(String serial_number, String renew_Answer_flag, String status, String rela_type) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("RENEW_ANSWER_FLAG", renew_Answer_flag);
        param.put("STATUS", status);
        param.put("RELA_TYPE", rela_type);
        param.put("CHANNEL", getVisit().getDepartId());
        return Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "UPD_RENEW_INFO", param);
    }
}
