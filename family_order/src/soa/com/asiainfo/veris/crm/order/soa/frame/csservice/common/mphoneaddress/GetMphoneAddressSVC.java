
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.mphoneaddress;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.mphoneaddress.GetMphoneAddressQry;

public class GetMphoneAddressSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 700609690050225034L;

    /**
     * 获取移动号码归属地州 ITF_CRM_MphoneAddress
     * 
     * @param inparams
     *            SERIAL_NUMBER
     * @return EPARCHY_CODE
     * @throws Exception
     * @author
     */
    public IData getEparchyCode(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "SERIAL_NUMBER");

        IDataset res = GetMphoneAddressQry.getEparchyCodeBySn(data);

        IData result = new DataMap();

        if (IDataUtil.isEmpty(res))// 获取归属地州失败
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "手机号码不存在,获取归属地州失败!");
        }
        result.put("EPARCHY_CODE", res.getData(0).getString("EPARCHY_CODE"));
        return result;
    }

    /**
     * 手机通话归属地 add yangsh6
     * 
     * @param datas
     * @return
     * @throws Exception
     */
    public IData queryPhoneChargeCity(IData datas) throws Exception
    {
        IDataset dataSet = new DatasetList();
        IData data = new DataMap();
        IData tmp = new DataMap();
        IDataUtil.chkParam(datas, "SERIAL_NUMBER");
        dataSet = GetMphoneAddressQry.queryPhoneCity(datas);
        if (IDataUtil.isEmpty(dataSet))
        {
            data.put("X_RESULTCODE", "1");
            data.put("X_RESULTINFO", "查询手机通话归属地失败");
        }
        else
        {
            tmp = (IData) dataSet.get(0);
            // 如果tf_f_user_city没有对应的记录，则以tf_f_user.city_code为通话归属地。
            String chargeCity = tmp.getString("CITY_NAME2", "").equals("") ? tmp.getString("CITY_NAME1") : tmp.getString("CITY_NAME2");
            // String chargeCity = tmp.getString("CITY_NAME2");

            data.put("X_RESULTCODE", "0");
            data.put("X_RESULTINFO", "查询手机通话归属地成功！");
            data.put("ChargesProvince", tmp.getString("EPARCHY_NAME"));
            data.put("ChargesCity", chargeCity);
        }

        return data;
    }
}
