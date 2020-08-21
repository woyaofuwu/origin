package com.asiainfo.veris.crm.iorder.web.igroup.colorring;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class ColorringHandler extends BizHttpHandler
{
    public void shortNumColorringAdmin() throws Exception
    {
        IData pagedata = this.getData();
        IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;
        String serialNumber = pagedata.getString("SERIAL_NUMBER");
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);

        IDataset resDataset = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.isChinaMobileNumber", params);
        boolean isOutNumber = resDataset.getData(0).getBoolean("RESULT");
        if (!isOutNumber)
        {
            CSViewException.apperr(CrmUserException.CRM_USER_629);
        }

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("REMOVE_TAG", "0");
        // inparam.put("NET_TYPE_CODE", "00");

        // 查询手机号码归属地
        IDataset mofficeInfo = CSViewCall.call(this, "CS.RouteInfoQrySVC.getEparchyCodeBySn", inparam);
        if (mofficeInfo != null && mofficeInfo.size() > 0)
        {

            IDataset param = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySN", inparam);
            if (param != null && param.size() > 0)
            {
                IData datatemp = param.getData(0);
                // 分散账期修改
                IData userAcctDay = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, datatemp.getString("USER_ID"), datatemp.getString("EPARCHY_CODE"));
                String flag = userAcctDay.getString("USER_ACCTDAY_DISTRIBUTION");
                if (!flag.equals(GroupBaseConst.UserDaysDistribute.TRUE.getValue()))
                {
                    CSViewException.apperr(AcctDayException.CRM_ACCTDAY_29, serialNumber);

                }

                if (!datatemp.getString("USER_STATE_CODESET").trim().equals("0"))
                {
                    result = false;
                    paramresult.put("ERROR_MESSAGE", "主付费号码非开通状态！");
                    paramresult.put("RESULT", result);
                    results.put("AJAX_DATA", paramresult);
                }
                if (!datatemp.getString("BRAND_CODE").trim().substring(0, 1).equals("G"))
                {
                    result = false;
                    paramresult.put("ERROR_MESSAGE", "主付费号码非G网用户！");
                    paramresult.put("RESULT", result);
                    results.put("AJAX_DATA", paramresult);
                }
                if (datatemp.getString("BRAND_CODE").trim().equals("GS01"))
                {
                    result = false;
                    paramresult.put("ERROR_MESSAGE", "神州行用户不可以作为主付费号码！");
                    paramresult.put("RESULT", result);
                    results.put("AJAX_DATA", paramresult);
                }
                // 判断未完工工单
                IData parammap = new DataMap();
                parammap.put("USER_ID", datatemp.getString("USER_ID"));
                parammap.put("TRADE_TYPE_CODE", "2444");
                parammap.put("BRAND_CODE", datatemp.getString("BRAND_CODE"));
                parammap.put("LIMIT_ATTR", "0");
                parammap.put("LIMIT_TAG", "0");
                parammap.put("EPARCHY_CODE", datatemp.getString("EPARCHY_CODE"));
                parammap.put(Route.ROUTE_EPARCHY_CODE, datatemp.getString("EPARCHY_CODE"));

                IDataset ids = CSViewCall.call(this, "CS.TradeInfoQrySVC.getNoTrade", parammap);

                if (!IDataUtil.isEmpty(ids))
                {
                    IData map = ids.getData(0);
                    result = false;
                    paramresult.put("ERROR_MESSAGE", "主付费号码有业务定单未完工，请稍后,用户编码[" + datatemp.getString("USER_ID") + "],业务类型编码[2444]！");
                    paramresult.put("RESULT", result);
                    results.put("AJAX_DATA", paramresult);
                }
            }
            else
            {
                result = false;
                paramresult.put("ERROR_MESSAGE", "主付费号码无效！");
                paramresult.put("RESULT", result);
                results.put("AJAX_DATA", paramresult);
            }
        }
        else
        {
            result = false;
            paramresult.put("ERROR_MESSAGE", "该服务号码不存在！");
            paramresult.put("RESULT", result);
            results.put("AJAX_DATA", paramresult);
        }
        if (paramresult.getString("ERROR_MESSAGE", "").equals(""))
        {
            result = true;
            paramresult.put("RESULT", result);
            results.put("AJAX_DATA", paramresult);
        }
        
        String ajaxdatastr = results.getString("AJAX_DATA", "");
        
        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DataMap(ajaxdatastr));
        }
        
    }
}
