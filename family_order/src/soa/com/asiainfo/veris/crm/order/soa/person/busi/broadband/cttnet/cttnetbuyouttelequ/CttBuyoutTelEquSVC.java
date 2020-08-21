
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetbuyouttelequ;

import java.text.DecimalFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BuyoutTelEquQry;

public class CttBuyoutTelEquSVC extends CSBizService
{
    public IDataset queryBuyoutInfos(IData input) throws Exception
    {
        return BuyoutTelEquQry.queryBuyoutInfos(input.getString("LOG_ID"));
    }

    /**
     * 保存TD话机买断信息
     * 
     * @param pd
     * @param data
     * @throws Exception
     */
    public IData saveBuyoutTelEqu(IData data) throws Exception
    {
        DecimalFormat df = new DecimalFormat("0");

        String update_time = SysDateMgr.getSysTime();
        String update_staff_id = getVisit().getStaffId();
        String update_depart_id = getVisit().getDepartId();

        String log_id = data.getString("LOG_ID", "-1");
        if ("-1".equals(log_id))
        {
            log_id = SeqMgr.getBuyOutTeleQuLogId();
        }

        data.put("LOG_ID", log_id);
        data.put("TELEQU_FEE_TOTLE", df.format(Float.valueOf(data.getString("TELEQU_FEE_TOTLE", "0")).floatValue() * 100)); // 总计，转换成分
        data.put("UPDATE_TIME", update_time);
        data.put("UPDATE_STAFF_ID", update_staff_id);
        data.put("UPDATE_DEPART_ID", update_depart_id);
        data.put("RSRV_NUM1", "");
        data.put("RSRV_NUM2", "");
        data.put("RSRV_NUM3", "");
        data.put("RSRV_STR1", "");
        data.put("RSRV_STR2", "");
        data.put("RSRV_STR3", "");
        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", "");
        data.put("RSRV_TAG2", "");
        data.put("RSRV_TAG3", "");

        Dao.executeUpdateByCodeCode("TF_F_BUYOUT_TELEQU", "INS_ALL", data);
        IData redata = new DataMap();
        redata.put("LOG_ID", log_id);
        return redata;
    }
}
