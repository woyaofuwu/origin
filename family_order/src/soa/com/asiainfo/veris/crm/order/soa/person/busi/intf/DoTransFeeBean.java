
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.DoTransFeeQry;

public class DoTransFeeBean extends CSBizBean
{

    /**
     * 给临时表添加数据
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData getTransFeeTemp(IData param) throws Exception
    {
        IData result = new DataMap();
        IDataUtil.chkParam(param, "TRADE_ID");
        IDataUtil.chkParam(param, "RSP_CODE");
        IDataset dataset = DoTransFeeQry.selTransFeeTemp(param);
        if (IDataUtil.isNotEmpty(dataset))
        {
            return dataset.getData(0);
        }
        return result;
    }

    /**
     * 给临时表添加数据
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData insertTransFeeTemp(IData param) throws Exception
    {
        IData result = new DataMap();
        ArrayList<String> paramList = new ArrayList<String>();
        paramList.add("LOG_ID");
        paramList.add("SEQ_ID");
        paramList.add("SYS_ID");
        paramList.add("PAY_TYPE");
        paramList.add("USER_ID");
        paramList.add("ACCT_ID");
        paramList.add("SERIAL_NUMBER");
        paramList.add("PAYAMOUNT");
        paramList.add("UNIT");
        paramList.add("REP_TIME");
        paramList.add("RSPCODE");
        for (int i = 0; i < paramList.size(); i++)
        {
            IDataUtil.chkParam(param, paramList.get(i));
        }
        int i = DoTransFeeQry.insertTransFeeTemp(param);
        if (i < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "插入日志操作失败!");
        }
        return result;
    }

    /**
     * 给临时表修改
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData updTransFeeTemp(IData param) throws Exception
    {
        IData result = new DataMap();
        IDataUtil.chkParam(param, "TRADE_ID");
        IDataUtil.chkParam(param, "RSP_CODE");
        DoTransFeeQry.updTransFeeTemp(param);

        return result;
    }
}
