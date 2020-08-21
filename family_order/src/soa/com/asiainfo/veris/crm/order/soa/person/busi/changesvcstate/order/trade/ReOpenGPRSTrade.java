
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ReOpenGPRSBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.ReOpenGPRSReqData;

public class ReOpenGPRSTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        this.createtSvcStateTradeDate(btd);
    }

    /**
     * 拼业务台帐服务状态子表
     * 
     * @param reOpenGprsRD
     * @return
     * @throws Exception
     */
    private void createtSvcStateTradeDate(BusiTradeData btd) throws Exception
    {
        ReOpenGPRSReqData reqData = (ReOpenGPRSReqData) btd.getRD();
        UcaData uca = reqData.getUca();
        String userId = uca.getUserId();
        String strExecTime = reqData.getAcceptTime();
        String serialNumber = uca.getSerialNumber();
        SvcStateTradeData a = uca.getUserSvcsStateByServiceId("22");
        ReOpenGPRSBean bean = BeanManager.createBean(ReOpenGPRSBean.class);
        IData result = bean.getUserGprsStateCode(userId);
        String tag1 = result.getString("TAG1");
        if (StringUtils.equals("1", tag1))
        {
            IDataset dataset1 = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "22");
            if (IDataUtil.isNotEmpty(dataset1))
            {
                // 终止原来的
                SvcStateTradeData delTradeData = a.clone();
                delTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                delTradeData.setStateCode("2");
                delTradeData.setEndDate(strExecTime);
                btd.add(serialNumber, delTradeData);

                // 新增
                SvcStateTradeData addTradeData = new SvcStateTradeData();
                addTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addTradeData.setUserId(userId);
                addTradeData.setStartDate(strExecTime);
                addTradeData.setEndDate(SysDateMgr.getTheLastTime());
                addTradeData.setServiceId("22");
                addTradeData.setMainTag("0");
                addTradeData.setStateCode("0");
                addTradeData.setInstId(SeqMgr.getInstId());
                btd.add(serialNumber, addTradeData);
            }
        }
    }
}
