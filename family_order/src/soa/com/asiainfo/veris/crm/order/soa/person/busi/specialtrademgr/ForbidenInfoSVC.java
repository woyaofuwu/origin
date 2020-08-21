
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ForbidenInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    // 取消用户禁查信息
    public IDataset cancelForbidenInfo(IData input) throws Exception
    {
        ForbidenInfoBean bean = BeanManager.createBean(ForbidenInfoBean.class);
        bean.CancelForbidenInfo(input);
        return null;
    }

    public IData getForbidenInfo(IData input) throws Exception
    {
        IData result = new DataMap();
        IDataset otherresults = new DatasetList();

        result.put("START_DATE", SysDateMgr.getSysTime());
        result.put("DEPART_ID", getVisit().getDepartId());
        result.put("STAFF_ID", getVisit().getStaffId());

        IDataset userlist = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"), input.getString("REMOVE_TAG"), "00");// UcaInfoQry.qryUserMainProdInfoBySn(input.getString("SERIAL_NUMBER"));

        if (userlist == null || userlist.size() < 1)
        {
            result.put("IS_FORBIDENED", "2");
        }
        else
        {
            IData userdata = userlist.getData(0);
            result.put("SERIAL_NUMBER", userdata.getString("SERIAL_NUMBER"));
            result.put("PRODUCT_ID", userdata.getString("PRODUCT_ID"));
            result.put("USER_STATE_CODESET", userdata.getString("USER_STATE_CODESET"));
            result.put("USER_ID", userdata.getString("USER_ID"));

            IData accidlist = UcaInfoQry.qryDefaultPayRelaByUserId(userdata.getString("USER_ID"), CSBizBean.getTradeEparchyCode());

            if (IDataUtil.isEmpty(accidlist) || IDataUtil.isNull(accidlist))
            {
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_113, userdata.getString("SERIAL_NUMBER"));
            }
            else
            {
                IData acciddata = accidlist;
                String acctId = acciddata.getString("ACCT_ID");
                IData acclist = UcaInfoQry.qryAcctInfoByAcctId(acctId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isEmpty(accidlist) || IDataUtil.isNull(accidlist))
                {
                    CSAppException.apperr(BofException.CRM_BOF_004);
                }
                else
                {
                    IData accdata = acclist;
                    result.put("PAY_NAME", accdata.getString("PAY_NAME"));
                    result.put("PAY_MODE_CODE", accdata.getString("PAY_MODE_CODE"));
                    result.put("CITY_CODE", accdata.getString("CITY_CODE"));

                    otherresults = UserOtherInfoQry.getForbidenInfo(userdata.getString("USER_ID"), "2", "0");

                    if (IDataUtil.isEmpty(otherresults) || IDataUtil.isNull(otherresults))
                    {
                        result.put("IS_FORBIDENED", "0");
                    }
                    else
                    {
                        result.put("IS_FORBIDENED", "1");
                    }
                }
            }
        }

        IData idata = new DataMap();
        idata.put("OTHERRESULTS", otherresults);
        idata.put("RESULTS", result);
        return idata;

    }

    public IDataset insertForbidenInfo(IData input) throws Exception
    {
        ForbidenInfoBean bean = BeanManager.createBean(ForbidenInfoBean.class);
        bean.InsertForbidenInfo(input);
        return null;
    }

    /**
     * @Function: queryForbidenInfo
     * @Description: 获取用户禁查信息 CRM-TO-ACCT
     * @param: @param data
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 9:43:25 AM Aug 3, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Aug 3, 2013 longtian3 v1.0.0 TODO:
     */
    public IDataset queryForbidenInfo(IData data) throws Exception
    {
        ForbidenInfoBean bean = BeanManager.createBean(ForbidenInfoBean.class);
        return bean.queryForbidenInfo(data);
    }
}
