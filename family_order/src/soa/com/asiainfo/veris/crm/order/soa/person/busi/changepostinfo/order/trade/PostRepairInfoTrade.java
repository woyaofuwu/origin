
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata.PostRepairInfoReqData;

public class PostRepairInfoTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createMainTrade(btd);
        insertUserOtherServ(btd);
    }

    /**
     * 登记邮寄补录信息主台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createMainTrade(BusiTradeData btd) throws Exception
    {
        PostRepairInfoReqData reqData = (PostRepairInfoReqData) btd.getRD();
        MainTradeData mainTD = btd.getMainTradeData();
        mainTD.setRsrvStr1(reqData.getRepairReason());
        mainTD.setRsrvStr2(reqData.getPostContent());
        mainTD.setRsrvStr3(reqData.getEmailContent());
        mainTD.setRsrvStr4(reqData.getRepairMonth());
        mainTD.setRsrvStr5(reqData.getPostCode());
        mainTD.setRsrvStr6(reqData.getPostFaxNbr());
        mainTD.setRsrvStr7(reqData.getPostEmail());
        mainTD.setRsrvStr8(reqData.getPostName());
        mainTD.setRsrvStr9(reqData.getPostAddress());
    }

    /**
     * 插入邮寄资料补录信息
     * 
     * @param btd
     * @throws Exception
     */
    private void insertUserOtherServ(BusiTradeData btd) throws Exception
    {
        PostRepairInfoReqData reqData = (PostRepairInfoReqData) btd.getRD();
        IData params = new DataMap();
        params.put("INST_ID", SeqMgr.getInstId());
        params.put("PARTITION_ID", "3988");
        params.put("USER_ID", reqData.getUca().getUserId());
        params.put("SERVICE_MODE", "PR");
        params.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());
        params.put("PROCESS_INFO", "补寄邮寄信息");
        params.put("RSRV_NUM1", reqData.getRepairReason());
        // 保存补寄原因编码
        params.put("RSRV_NUM2", "0");
        params.put("RSRV_NUM3", "0");
        params.put("RSRV_STR1", btd.getTradeId());
        params.put("RSRV_STR2", reqData.getPostContent());
        params.put("RSRV_STR3", reqData.getEmailContent());
        params.put("RSRV_STR4", reqData.getRepairMonth());
        params.put("RSRV_STR5", reqData.getPostCode());
        params.put("RSRV_STR6", reqData.getPostFaxNbr());
        params.put("RSRV_STR7", reqData.getPostEmail());
        params.put("RSRV_STR8", reqData.getPostName());
        params.put("RSRV_STR10", reqData.getPostAddress());
        params.put("RSRV_DATE1", reqData.getAcceptTime());
        params.put("PROCESS_TAG", "0");
        params.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        params.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        params.put("START_DATE", reqData.getAcceptTime());
        params.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        params.put("REMARK", reqData.getRemark());

        Dao.insert("TF_F_USER_OTHERSERV", params);
    }
}
