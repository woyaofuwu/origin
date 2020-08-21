
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.custcontact;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustTouchQry;

public class CustTouchMgrBean extends CSBizBean
{

    /**
     * 修改客户接触信息明细
     * 
     * @data 2013-9-29
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public void modifyNewCustTouch(IData data) throws Exception
    {
        String touchId = data.getString("TOUCH_ID", "");
        String touchtime = data.getString("TOUCH_TIME", "");
        if (touchId.equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_989);
        }
        if (touchtime.equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_990);
        }
        TradeCustTouchQry.modifyNewCustTouch(data.getString("TOUCH_ID"), data.getString("TOUCH_TIME"), data.getString("REMARK"));

    }

    /**
     * 记录日志
     * 
     * @param data
     * @throws Exception
     */
    public void modifyNewCustTouchLog(IData data) throws Exception
    {

        IData param = new DataMap();
        param.put("FLOW_ID", SeqMgr.getTradeId());
        param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        param.put("CUST_CONTACT_ID", data.getString("TOUCH_ID", ""));
        param.put("MODIFY_TAG", "0");
        param.put("MODIFY_DESC", data.getString("MODIFY_DESC", ""));
        param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        param.put("UPDATE_CITY_CODE", getVisit().getCityCode());
        param.put("UPDATE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("REMARK", data.getString("REMARK", ""));
        TradeCustTouchQry.insertCustTouchLog(param);
    }

}
