
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.developstaff;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

public class DevelopStaffBean extends CSBizBean
{

    /**
     * 获取打印配置标识数据
     * 
     * @param data
     *            [SERIAL_NUMBER,TRADE_TYPE_CODE]
     * @return
     * @throws Exception
     */
    public static IData getDevelopStaffConfig(IData input) throws Exception
    {
        int flag = 0; // 开关控制

        IData data = new DataMap();
        IDataset ids = ParamInfoQry.getCommparaByAttrCode("CSM", "3622", "1", "1", CSBizBean.getTradeEparchyCode());
        if ((ids == null) || (ids.size() < 1))
        {
            flag = 1;
        }
        else
        {
            if (!"1".equals(ids.getData(0).getString("PARA_CODE2", "8")))
                flag = 1;
        }
        if (flag == 0)
        {
            // 权限控制
            // getVisit().hasPriv("SYS692")
            if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS692"))
            {

                IDataset ret = null;// PrivInfoQry.getStaffRight("SYS692", getVisit().getStaffId(), "1",
                // null);
                if ((ret == null) || (ret.size() < 1))
                {
                    flag = 1;
                }
            }
        }

        if (flag == 0)
        {
            // 业务类型控制
            IDataset ids_type = ParamInfoQry.getCommparaByAttrCode("CSM", "3623", "1", input.getString("TRADE_TYPE_CODE"), CSBizBean.getTradeEparchyCode());
            if ((ids_type == null) || (ids_type.size() < 1))
            {
                flag = 1;
            }
        }
        if (flag == 0)
        {
            data.put("DEVELOP_VISIBLE", "1");
        }
        else
        {
            data.put("DEVELOP_VISIBLE", "0");
        }

        return data;
    }

}
