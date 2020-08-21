
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class WidenetChangeDiscntFilter implements IFilterIn
{

    /**
     * 计算两个字符串日期(yyyy-MM-dd)之间的间隔 例如: 输入: String "2012-09-20" , String "2006-12-01" 输出: int[] {6, -3, 19}
     * 
     * @param d1
     * @param d2
     * @throws Exception
     * @return r
     * @author wenhj
     */
    public static int[] calcIntervalOfTwoDate(String d1, String d2) throws Exception
    {
        int[] r =
        { 0, 0, 0 };

        // 取日期部分(前10个字符)，按间隔符(-)做分割
        String[] ds1 = d1.trim().substring(0, 10).split("-");
        String[] ds2 = d2.trim().substring(0, 10).split("-");

        r[0] = Integer.parseInt(ds1[0]) - Integer.parseInt(ds2[0]);
        r[1] = Integer.parseInt(ds1[1]) - Integer.parseInt(ds2[1]);
        r[2] = Integer.parseInt(ds1[2]) - Integer.parseInt(ds2[2]);

        return r;
    }

    /**
     * 批量办理校园宽带套餐入参检查
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "DATA1");// 套餐编码
        IDataUtil.chkParam(param, "DATA2");// 开始时间
        IDataUtil.chkParam(param, "DATA3");// 结束时间
        IDataUtil.chkParam(param, "DATA4");// 实缴金额
        IDataUtil.chkParam(param, "DATA5");// ENABLE_TAG
        IDataUtil.chkParam(param, "DATA6");// DEFINE_MONTHS
        IDataUtil.chkParam(param, "DATA7");// RSRV_STR2
        IDataUtil.chkParam(param, "DATA8");// RSRV_STR3
        IDataUtil.chkParam(param, "DATA9");// RSRV_STR4
    }

    public void composeDiscntInfo(IData param) throws Exception
    {

        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("DEFER_TAG", "A");
        data.put("DISCNT_CODE", param.getString("DATA1"));
        data.put("START_DATE", param.getString("DATA2"));
        data.put("END_DATE", param.getString("DATA3"));
        dataset.add(data);
        param.put("SPC_USER_DISCNTS", dataset.toString());
    }

    public void transferDataInput(IData input) throws Exception
    {
        checkInparam(input);
        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
            ;
        }
        String startDate = input.getString("DATA2");
        String endDate = input.getString("DATA3");
        String openDate = userInfo.getString("OPEN_DATE");
        if (startDate.compareTo(openDate) < 0)
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_20);
        }
        // ------应缴费用校验------------
        int d4 = Integer.parseInt(input.getString("DATA4"));
        String d6 = input.getString("DATA6");
        int d7 = Integer.parseInt(input.getString("DATA7"));
        int d9 = Integer.parseInt(input.getString("DATA9"));
        int dr = SysDateMgr.dayInterval(startDate, SysDateMgr.getDateNextMonthFirstDay(startDate));// //取d2日期部分，计算d2所在月份剩余天数(含当天)
        int[] di = calcIntervalOfTwoDate(endDate, startDate);
        int ying = 0;
        if ("0".equals(d6))
        {// 首月按日收费，月费封顶，次月按月收取
            ying = (d9 * dr > d7 ? d7 : d9 * dr) + d7 * (di[0] * 12 + di[1]);
        }
        else if ("1".equals(d6))
        {// 按月收费
            ying = d7 + d7 * (di[0] * 12 + di[1]);
        }
        else
        {
            // 其他情况
        }
        if (ying != d4)
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_21, Double.toString(ying / 100.0), Double.toString(d4 / 100.0));
        }
        composeDiscntInfo(input);
    }
}
