
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.fastauthapply;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.FastAuthApplyQry;

public class FastAuthApplyBean extends CSBizBean
{

    public int applyAuthTrade(IData param) throws Exception
    {
        int applySuccessFlag = 1;
        String tradeId = FastAuthApplyQry.getTradeID(this.getTradeEparchyCode());
        String askStaffId = this.getVisit().getStaffId();
        String askDepartId = this.getVisit().getDepartId();
        param.put("ASK_STAFF_ID", askStaffId);
        param.put("ASK_DEPART_ID", askDepartId);
        IData retData = queryRule(param);
        if (!"0000".equals(retData.getString("opCode")))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_783, retData.getString("retInfo"));
        }
        else
        {
            String menuId = param.getString("MENU_ID", "");
            String askStartDate = param.getString("ASK_START_DATE", "");
            String askEndDate = param.getString("ASK_END_DATE", "");
            String awsDate = param.getString("AWS_DATE", "");
            String menuTitle = param.getString("MENU_TITLE", "");
            String askTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
            String askNum = param.getString("ASK_NUM", "");
            String awsState = "0";
            String awsStaffId = param.getString("AWS_STAFF_ID", "");
            String awsDepartId = param.getString("AWS_DEPART_ID", "");
            String askSerial = param.getString("ASK_SERIAL", "");
            String applyRemark = param.getString("APPLY_REMARK"); // rsrv_str1
            String rsrvStr2 = "0";// 0表示新增, 1 表示删除
            // if ("".equals(awsStaffId)){
            // awsStaffId = "SUPERUSR";
            // }
            FastAuthApplyQry.applyFastAuth(tradeId, menuId, menuTitle, askStaffId, askDepartId, askTime, askNum, askStartDate, askEndDate, awsState, awsStaffId, awsDepartId, awsDate, askSerial, applyRemark, rsrvStr2);
            IDataset staffDataset = FastAuthApplyQry.queryStaffSerial(awsStaffId);
            if (IDataUtil.isNotEmpty(staffDataset))
            {
                IData staffData = staffDataset.getData(0);
                if ("".equals(staffData.getString("SERIAL_NUMBER", "")))
                {
                    applySuccessFlag = 999;
                }
                else
                {
                    // param.put("SERIAL_NUMBER", staffData.getString("SERIAL_NUMBER"));
                }
            }
        }
        return applySuccessFlag;
    }

    public void delAuthTimes(IData data) throws Exception
    {
        String askId = data.getString("ASK_ID");
        if ("-1".equals(askId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_783, "未传入授权业务ID");
        }
        else
        {
            FastAuthApplyQry.delAuthTimes(askId);
        }

    }

    public IDataset queryApplyTrade(IData data, Pagination page) throws Exception
    {
        String menuId = data.getString("MENU_ID", "");
        String askStaffId = data.getString("ASK_STAFF_ID", "");
        String askDepartId = data.getString("ASK_DEPART_ID", "");
        String awsState = data.getString("AWS_STATE", "");
        String askStartDate = data.getString("ASK_START_DATE", "");
        String awsStaffId = data.getString("AWS_STAFF_ID", "");
        String askEndDate = data.getString("ASK_END_DATE", "");
        return FastAuthApplyQry.queryApplyTrade(menuId, askStaffId, askDepartId, awsState, askStartDate, askEndDate, awsStaffId, page);
    }

    public IDataset queryAuthTradeType(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String menuId = data.getString("MENU_ID", "");
        String nowDate = data.getString("NOW_DATE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        return FastAuthApplyQry.queryAuthTradeType(menuId, nowDate, startDate, endDate, page);
    }

    public IDataset queryPwd(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String askId = data.getString("ASK_ID", "");
        String pwd = data.getString("PWD", "");
        return FastAuthApplyQry.queryPwd(askId, pwd);

    }

    public IData queryRule(IData param) throws Exception
    {
        IData ret = new DataMap();
        ret.put("opCode", "0000");
        String startDate = param.getString("ASK_START_DATE");
        String endDate = param.getString("ASK_END_DATE");
        String rsrvStr2 = "0"; // 0添加 1 删除
        String menuId = param.getString("MENU_ID", "");
        IDataset reInfo = FastAuthApplyQry.judgeIsNotInApply(rsrvStr2, startDate, endDate, menuId);
        if (reInfo.size() == 0)
        {
            ret.put("opCode", "9991");
            ret.put("retInfo", "您申请的业务的有效期不在" + param.getString("ASK_START_DATE") + "至" + param.getString("ASK_END_DATE") + "的时间段内，不允许申请。请核实！");
            return ret;
        }
        String askStaffId = param.getString("ASK_STAFF_ID");
        String askDepartId = param.getString("ASK_DEPART_ID");
        int ii = FastAuthApplyQry.judgeIsNotApply(askStaffId, askDepartId, rsrvStr2, startDate, endDate, menuId).size();
        if (ii > 0)
        {
            ret.put("opCode", "9992");
            ret.put("retInfo", "您申请的业务在" + param.getString("ASK_START_DATE") + "至" + param.getString("ASK_END_DATE") + "时间段内已存在！请核实！");
            return ret;
        }

        return ret;

    }

    public IDataset queryStaffs(boolean flag, IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String area_code = data.getString("AREA_CODE", "");
        String dept_id = data.getString("AREA_CODE", "");
        String need_stafftag = data.getString("NEED_STAFFTAG", "false");
        String staff_tags = data.getString("STAFF_TAGS", "1");
        String staffId = data.getString("STAFF_ID", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String staffName = data.getString("STAFF_NAME", "");
        String sex = data.getString("SEX", "");
        return FastAuthApplyQry.queryStaffs(staffId, serialNumber, staffName, sex, flag, area_code, dept_id, need_stafftag, staff_tags, page);

    }

    public void updateAuthTrade(IData data) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        // String askStaffId = data.getString("ASK_STAFF_ID", "");
        // String askDepartId = data.getString("ASK_DEPART_ID", "");
        // String askTime = data.getString("ASK_TIME", "");
        // String rsrvStr1 = data.getString("RSRV_STR1", "");
        FastAuthApplyQry.updateAuthTrade(data);

    }

}
