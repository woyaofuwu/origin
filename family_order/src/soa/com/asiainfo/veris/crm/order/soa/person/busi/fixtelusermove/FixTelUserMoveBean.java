
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.telephone.TelInfoQry;

public class FixTelUserMoveBean extends CSBizBean
{

    /**
     * 界面初始化方法
     * 
     * @author dengyong3
     * @param data
     * @return
     * @throws Exception
     */
    public IData loadTradeInfo(IData data) throws Exception
    {
        // 根据userid查询固话安装信息
        IDataset telInfos = TelInfoQry.getTelInfo(data.getString("USER_ID"));
        IData telInfo = null;
        if (telInfos != null && !telInfos.isEmpty())
        {
            telInfo = telInfos.getData(0);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号码查询不到对应的装机信息");
        }
        return telInfo;
    }
}
