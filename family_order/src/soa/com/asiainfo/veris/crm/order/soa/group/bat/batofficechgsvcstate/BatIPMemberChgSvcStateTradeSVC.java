
package com.asiainfo.veris.crm.order.soa.group.bat.batofficechgsvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatIPMemberChgSvcStateTradeSVC extends CSBizService
{
    public IDataset getIPMenberInfo(IData inparam) throws Exception
    {
        String serial_number = inparam.getString("SERIAL_NUMBER");
        String remove_tag = "0";
        String net_type_code = "00";
        IDataset userinfos = UserInfoQry.getUserInfoBySerialNumber(serial_number, remove_tag, net_type_code);
        if (IDataUtil.isEmpty(userinfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务号码不存在，业务不能继续！");
        }
        else
        {
            IData userindfo = userinfos.getData(0);
            String brand_code = userindfo.getString("BRAND_CODE");
            if (!"IP10".equals(brand_code))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务号码不存在，不是IP后付费用户，不能继续办理！");
            }
        }
        return queryBindIPPhone(inparam);

    }

    /**
     * 根据手机USER_ID获取IP直通车固定号码资料
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    private IDataset queryBindIPPhone(IData inparam) throws Exception
    {
        IDataset ruslt = new DatasetList();
        String serail_number = inparam.getString("SERIAL_NUMBER");

        IDataset userinfos = UserInfoQry.getUserInfoBySn(serail_number, "0");

        String userIdA = userinfos.getData(0).getString("USER_ID");
        String roleCodeB = "1";
        String relationTypeCode = "51";
        IDataset bindipphones = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA, roleCodeB, relationTypeCode, null);
        if (IDataUtil.isNotEmpty(bindipphones))
        {
            for (int i = 0; i < bindipphones.size(); i++)
            {
                IData bindinphone = bindipphones.getData(i);
                String user_id = bindinphone.getString("USER_ID_B");

                IData info = UcaInfoQry.qryUserInfoByUserId(user_id);
                if (IDataUtil.isEmpty(info))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "无IP固定号码用户主表资料");
                }

                IData userinfo = new DataMap();
                userinfo.put("SERIAL_NUMBER", info.getString("SERIAL_NUMBER"));
                ruslt.add(userinfo);
            }
            return ruslt;

        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "IP后付费集团没有成员");
            return null;
        }
    }
}
