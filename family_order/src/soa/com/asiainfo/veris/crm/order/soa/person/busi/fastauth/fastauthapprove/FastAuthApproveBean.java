
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.fastauthapprove;

import java.util.Date;
import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqSmsSendId;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.FastAuthApproveQry;

public class FastAuthApproveBean extends CSBizBean
{

    public int approveAuth(IData param, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        // 先请前台传入的ASK_IDS进行加工并去除用户选择需要自动剔除的ids
        String removeFlag = param.getString("REMOVE_FLAG");
        String AskIds = "";
        String askIdLists = param.getString("ASK_IDS");
        IDataset ids = new DatasetList(askIdLists);
        String op = param.getString("OP_FLAG");
        if ("1".equals(removeFlag))
        { // 如果需要剔除记录
            if ("reject".equals(op))
            {
                for (int i = 0; i < ids.size(); i++)
                {
                    if (ids.getData(i).getString("AWS_STATE").equals("1"))
                    {
                        ids.remove(i);
                        i--;

                    }
                }
            }
            else if ("accept".equals(op))
            {
                for (int i = 0; i < ids.size(); i++)
                {
                    if (ids.getData(i).getString("AWS_STATE").equals("2"))
                    {
                        ids.remove(i);
                        i--;
                    }
                }
            }
        }
        for (int i = 0; i < ids.size(); i++)
        {
            AskIds += ids.getData(i).getString("ASK_ID");
            if (ids.size() > 1 && i < ids.size() - 1)
                AskIds += ",";
        }

        if ("".equals(AskIds.trim()) || ",".equals(AskIds.trim()))
        {
            return 999;
        }
        IDataset paramList = new DatasetList();
        if ("accept".equals(op))
        {
            String[] Ask_Ids = AskIds.split(",");

            for (int i = 0; i < Ask_Ids.length; i++)
            {
                IData para = new DataMap();
                para.put("ASK_ID", Ask_Ids[i]);
                para.put("AWS_STATE", "1");
                para.put("AWS_DATE", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
                para.put("AWS_STAFF_ID", this.getVisit().getStaffId());
                para.put("AWS_DEPART_ID", this.getVisit().getDepartId());
                paramList.add(para);
            }
        }
        else if ("reject".equals(op))
        {
            String[] Ask_Ids = AskIds.split(",");

            for (int i = 0; i < Ask_Ids.length; i++)
            {
                IData para = new DataMap();
                para.put("ASK_ID", Ask_Ids[i]);
                para.put("AWS_STATE", "2");
                para.put("AWS_DATE", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
                para.put("AWS_STAFF_ID", this.getVisit().getStaffId());
                para.put("AWS_DEPART_ID", this.getVisit().getDepartId());
                paramList.add(para);
            }

        }
        FastAuthApproveQry.ApproveFastAuth(paramList);
        int[] successFlag = sendMsgToCust(paramList, routeEparchyCode);
        return successFlag[0];
    }

    public IDataset queryApplyTrade(IData data, Pagination page) throws Exception
    {
        String menuId = data.getString("MENU_ID", "");
        String askStaffId = data.getString("ASK_STAFF_ID", "");
        String askDepartId = data.getString("ASK_DEPART_ID", "");
        String awsState = data.getString("AWS_STATE", "");
        String askStartDate = data.getString("ASK_START_DATE", "");
        String askEndDate = data.getString("ASK_END_DATE", "");
        return FastAuthApproveQry.queryApplyTrade(menuId, askStaffId, askDepartId, awsState, askStartDate, askEndDate, page);
    }

    public IDataset queryAuthTradeType(IData data, Pagination page) throws Exception
    {
        // String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String menuId = data.getString("MENU_ID", "");
        String nowDate = data.getString("NOW_DATE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        return FastAuthApproveQry.queryAuthTradeType(menuId, nowDate, startDate, endDate, page);
    }

    /**
     * java产生6位随机数 中测新增
     */
    public String randomNum()
    {
        String s = "";
        while (s.length() < 6)
        {
            s += (int) (Math.random() * 10);
        }

        return s;
    }

    /**
     * 发送通知
     */
    public int[] sendMsgToCust(IDataset params, String routeEparchyCode) throws Exception
    {

        // IDataset params= params1.getDataset("IData4IO");
        IData smsPara;
        IDataset compa = FastAuthApproveQry.queryCommByAttrCode("CSM", "1099", "1100", "ZZZZ");
        // 审核申请后，向短信表内插入数据，通知申请人员

        for (Iterator iterator = params.iterator(); iterator.hasNext();)
        {
            IData param = (IData) iterator.next();
            smsPara = new DataMap();
            String smssendId = Dao.getSequence(SeqSmsSendId.class);
            smsPara.put("SMS_NOTICE_ID", smssendId);
            smsPara.put("EPARCHY_CODE", this.getTradeEparchyCode());
            smsPara.put("IN_MODE_CODE", "0");
            IData userInfo = FastAuthApproveQry.getSerialInfo(param.getString("ASK_ID")).getData(0);

            smsPara.put("SERIAL_NUMBER", userInfo.getString("ASK_SERIAL"));
            smsPara.put("MENU_TITLE", userInfo.getString("MENU_TITLE"));

            String content = compa.getData(0).getString("PARA_CODE20").replaceAll("VPERSON", this.getVisit().getStaffName());
            content = content.replaceAll("VID", param.getString("ASK_ID"));
            content = content.replaceAll("VTRADETYPE", smsPara.getString("MENU_TITLE"));
            content = content.replaceAll("VFLAG", "1".equals(param.getString("AWS_STATE")) ? "通过" : "拒绝");

            String approve_state = param.getString("AWS_STATE");
            if ("1".equals(approve_state))
            { // 审核通过
                String pwd = randomNum();
                content += "随机密码是：" + pwd;

                FastAuthApproveQry.insertPwd(param.getString("ASK_ID"), pwd);
            }
            smsPara.put("NOTICE_CONTENT", content);
            smsPara.put("PRIORITY", compa.getData(0).getString("PARA_CODE3"));
            smsPara.put("STAFF_ID", this.getVisit().getStaffId());
            smsPara.put("DEPART_ID", this.getVisit().getDepartId());
            smsPara.put("REMARK", compa.getData(0).getString("PARA_CODE4", ""));
            smsPara.put("FORCE_OBJECT", "");
            String hms = SysDateMgr.getSysTime();
            smsPara.put("FORCE_START_TIME", hms);

            // Date dd = new Date(SysDateMgr.getSysTime());
            // DateFormatUtils.format(dd, "yyyy-MM-dd HH:mm:ss");
            Date dd = SysDateMgr.string2Date(hms, SysDateMgr.PATTERN_STAND);
            // Date dd = sdf.parse(SysDateMgr.getSysTime());

            long tomoTime = (dd.getTime() / 1000) + 24 * 60 * 60;
            dd.setTime(tomoTime * 1000);

            smsPara.put("FORCE_END_TIME", dd);

            param.putAll(smsPara);
        }

        return FastAuthApproveQry.sendMsgToCust(params, routeEparchyCode);

    }

}
