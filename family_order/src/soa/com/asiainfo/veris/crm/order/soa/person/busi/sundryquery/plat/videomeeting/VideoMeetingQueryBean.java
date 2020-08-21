
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.videomeeting;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class VideoMeetingQueryBean extends CSBizBean
{

    /**
     * 查询手机支付的账户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryVideoMeetingBooking(IData param) throws Exception
    {
        IData result = new DataMap();
        IDataset BookingResult = new DatasetList();
        String msisdn = param.getString("SERIAL_NUMBER", "");
        if (StringUtils.isBlank(msisdn))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
        }

        IData param2 = new DataMap();
        param2.clear();
        param2.put("SERIAL_NUMBER", msisdn);
        // param2.put("USER_ID", value);

        return null;
    }

}
