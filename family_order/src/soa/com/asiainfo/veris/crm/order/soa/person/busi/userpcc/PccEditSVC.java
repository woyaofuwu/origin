
package com.asiainfo.veris.crm.order.soa.person.busi.userpcc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class PccEditSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(PccEditSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset dealInfo(IData input) throws Exception
    {
        UserPccImportBean bean = (UserPccImportBean) BeanManager.createBean(UserPccImportBean.class);
        IDataset userRes = UserResInfoQry.queryUserSimInfo(input.getString("USER_ID"), "1", getVisit().getStaffEparchyCode());
        if (IDataUtil.isNotEmpty(userRes))
        {
            IDataset dealInfos = new DatasetList();
            IData data = new DataMap();

            data.put("USER_ID", input.getString("USER_ID"));
            data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            data.put("SERIAL_NUMBER_A", input.getString("SERIAL_NUMBER_A"));
            data.put("IMSI", userRes.getData(0).getString("IMSI", ""));
            data.put("USER_GRADE", input.getString("USER_GRADE"));
            data.put("INSERT_DATE", SysDateMgr.getSysTime());
            data.put("USER_STATUS", input.getString("USER_STATUS"));
            data.put("BILL_TYPE", input.getString("BILL_TYPE"));
            data.put("USER_BILLCYCLEDATE", input.getString("USER_BILLCYCLEDATE"));
            data.put("OPER_CODE", input.getString("OPER_CODE"));
            data.put("REMARK", "前台手工插入");
            data.put("DEAL_STATE", "0");
            dealInfos.add(data);
            bean.batUserPccInfo(dealInfos);
        }

        return new DatasetList();
    }

    public IDataset getPccUserInfByUserId(IData input) throws Exception
    {
        PccEditBean bean = (PccEditBean) BeanManager.createBean(PccEditBean.class);

        IDataset ret = bean.getPccUserInfByUserId(input);
        return ret;
    }
}
