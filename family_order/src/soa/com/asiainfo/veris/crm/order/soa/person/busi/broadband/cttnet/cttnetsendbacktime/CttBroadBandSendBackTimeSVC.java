
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetsendbacktime;

import java.util.Calendar;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class CttBroadBandSendBackTimeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public int countLeavDay(String startDate, String endDate) throws Exception
    {
        long allDays = 0;

        Date date1 = SysDateMgr.string2Date(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
        Date date2 = SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
        Date date3 = SysDateMgr.string2Date(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        Calendar c3 = Calendar.getInstance();
        c3.setTime(date3);

        if (c1.getTimeInMillis() < c3.getTimeInMillis() && c3.getTimeInMillis() < c2.getTimeInMillis())
        {
            // 开始时间 < 当前时间 < 结束时间
            allDays = ((c2.getTimeInMillis()) - (c3.getTimeInMillis())) / (1000 * 24 * 60 * 60);

        }
        else if (c3.getTimeInMillis() < c1.getTimeInMillis() && c1.getTimeInMillis() < c2.getTimeInMillis())
        {
            // 当前时间 < 开始时间 < 结束时间
            allDays = ((c2.getTimeInMillis()) - (c1.getTimeInMillis())) / (1000 * 24 * 60 * 60);
        }
        else
        {
            allDays = 0;
        }

        return Math.abs(Integer.parseInt(String.valueOf(allDays)));
    }

    public IDataset getSendBackHistory(IData input) throws Exception
    {
        String instId = input.getString("INSTID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String userId = input.getString("USER_ID");
        IDataset historyDataset = new DatasetList();
        if (!"".equals(instId))
        {

            IDataset dataset = BroadBandInfoQry.qrySendBackTimeFormOther(userId, instId);
            ;
            if (dataset != null && dataset.size() > 0)
            {
                for (int i = 0; i < dataset.size(); i++)
                {
                    IData data = new DataMap();

                    String specTag = dataset.getData(i).getString("RSRV_TAG1", "");
                    if ("1".equals(specTag))
                    {
                        data.put("SEND_BACK_TYPE", "报开补退");
                    }
                    else
                    {
                        data.put("SEND_BACK_TYPE", "人工补退");
                    }

                    String staffId = dataset.getData(i).getString("UPDATE_STAFF_ID");
                    String staffName = UStaffInfoQry.getStaffNameByStaffId(getVisit().getStaffId());

                    data.put("SEND_BACK_DAYS", dataset.getData(i).getString("RSRV_VALUE"));
                    data.put("UPDATE_STAFF_NAME", staffName);
                    data.put("UPDATE_TIME", dataset.getData(i).getString("UPDATE_TIME"));
                    historyDataset.add(data);
                }
            }
        }
        return historyDataset;
    }

    public IDataset qryBroadBandUser(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        IData data = new DataMap();
        // 查询宽带信息
        // input.getData("USER_INFO").getString("SERIAL_NUMBER");
        String serialNumber = input.getString("SERIAL_NUMBER");
        if (serialNumber == null || "".equals(serialNumber))
        {
            serialNumber = input.getString("AUTH_SERIAL_NUMBER");
        }
        String userId = input.getString("USER_ID");

        IDataset accessInfos = WidenetInfoQry.getUserWidenetInfo(userId);// BroadBandInfoQry.getBroadBandWidenetActByUserId(userId);
        // int size = accessInfos.size();
        // for (int i = 0; i < size; i++)
        // {
        // IData temp = accessInfos.getData(i);
        // if ("0".equals(temp.getString("RSRV_STR1")))// 0-宽带账号；18-绑定手机号码
        // {
        // data.putAll(temp);
        // }
        // }
        if (IDataUtil.isEmpty(accessInfos))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, serialNumber);
        }

        IDataset userSvcStateInfos = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
        if (IDataUtil.isEmpty(userSvcStateInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_144);
        }

        int days = 0;

        IDataset discntDataset = new DatasetList();

        String modemDiscntCode = "";

        IDataset paramDataInfos = ParamInfoQry.getCommparaByCode("CSM", "1128", "D", "ZZZZ");
        if (paramDataInfos != null && !paramDataInfos.isEmpty())
        {
            modemDiscntCode = paramDataInfos.getData(0).getString("PARA_CODE1", "");
        }

        IDataset dataset = UserDiscntInfoQry.getDiscntByUserId(input.getString("USER_ID"));

        if (dataset != null && dataset.size() > 0)
        {
            for (int i = 0; i < dataset.size(); i++)
            {

                IData discntData = new DataMap();

                // 计算退补天数
                int leavDay = countLeavDay(dataset.getData(i).getString("START_DATE"), dataset.getData(i).getString("END_DATE"));

                String discntCode = dataset.getData(i).getString("DISCNT_CODE", "");
                if ("31910001".equals(discntCode))
                { // 宽带补退特殊优惠编码
                    discntData.put("DISCNT_TYPE", "补退优惠");
                    discntData.put("DISCNT_NAME", "宽带补退优惠");
                }
                else if (modemDiscntCode.equals(discntCode))
                {
                    discntData.put("DISCNT_TYPE", "modem优惠");
                    discntData.put("DISCNT_NAME", "modem优惠");
                }
                else
                {
                    discntData.put("DISCNT_TYPE", "正常优惠");
                    String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(dataset.getData(i).getString("DISCNT_CODE"));
                    discntData.put("DISCNT_NAME", discntName);
                    // days = dataset.getData(i).getInt("LEAVING_DAYS", 0);
                    days = leavDay;
                }
                discntData.put("START_DATE", dataset.getData(i).getString("START_DATE", ""));
                discntData.put("END_DATE", dataset.getData(i).getString("END_DATE", ""));
                discntData.put("INST_ID", dataset.getData(i).getString("INST_ID", ""));
                // discntData.put("LEAVING_DAYS",dataset.getData(i).getString("LEAVING_DAYS", ""));
                discntData.put("LEAVING_DAYS", leavDay);

                discntDataset.add(discntData);
            }
        }

        // this.setSendBackInfo(sendBackData);
        IData sendBackData = new DataMap();
        sendBackData.put("MIN", -days);
        sendBackData.put("SEND_BACK_DAYS", "0");

        data.put("DISCNT_INFO", discntDataset);// 设置用户资费信息

        data.put("SEND_BACK_DATA", sendBackData);// 设置用户退补天数
        // IDataset addrInfos = BroadBandInfoQry.queryBroadBandAddressInfo(input);
        // if (addrInfos != null || addrInfos.size() > 0)
        // {
        data.put("addrInfo", accessInfos.getData(0));
        // }
        
        result.add(data);
        return result;

    }
}
