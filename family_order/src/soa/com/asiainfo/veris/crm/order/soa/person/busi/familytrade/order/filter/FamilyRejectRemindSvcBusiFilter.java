
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyCreateBean;

public class FamilyRejectRemindSvcBusiFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        String isInterface = input.getString("IN_TAG", "1");// 1表示接口

        if (StringUtils.equals(isInterface, "1"))
        {
            IDataUtil.chkParam(input, "SERIAL_NUMBER");// 办理号码
            IDataUtil.chkParam(input, "REJECT_MODE");// 拒收类型：1-全网拒接，2-成员拒接
            // IDataUtil.chkParam(input, "IN_MODE_CODE");

            String rejectMode = input.getString("REJECT_MODE");

            // 如果是成员拒接
            if (StringUtils.equals(rejectMode, "2"))
            {
                String serialNumberBStr = input.getString("SERIAL_NUMBER_B", "");

                if (StringUtils.isBlank(serialNumberBStr))
                {
                    // 办理亲亲网成员拒接时成员列表参数为空
                    CSAppException.apperr(FamilyException.CRM_FAMILY_740);
                }

                IDataset mebList = new DatasetList();
                String[] serialNumberBs = StringUtils.split(serialNumberBStr, ",");

                for (int i = 0, length = serialNumberBs.length; i < length; i++)
                {
                    String serialNumberB = serialNumberBs[i];
                    IData user = UcaInfoQry.qryUserInfoBySn(serialNumberB);

                    if (IDataUtil.isEmpty(user))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberB);
                    }

                    IData member = new DataMap();
                    member.put("SERIAL_NUMBER_B", serialNumberB);
                    mebList.add(member);
                }

                input.put("MEB_LIST", mebList);
            }

            FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
            bean.getAllMebList(input);
        }
    }

}
