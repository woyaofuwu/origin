package com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.welfare.exception.WelfareException;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * @author liwei
 * @version V1.0
 * @date 2020/7/7 10:53
 */
public class WelfareOfferFilter implements IFilterIn {
    /**
     * 参数检查
     *
     * @param input
     * @throws Exception
     */
    public void checkInputData(IData input) throws Exception {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String startDate = input.getString("START_DATE", "");
        String endDate = input.getString("END_DATE", "");
        //权益包直接传入开始时间和结束时间
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            //比较两个时间
            int iresult = startDate.compareTo(endDate);// 比较2个日期的大小
            if (iresult >= 0) {
                CSAppException.apperr(WelfareException.CRM_WELFARE_9);
            }

        }

    }

    @Override
    public void transferDataInput(IData input) throws Exception {
        this.checkInputData(input);
    }
}
