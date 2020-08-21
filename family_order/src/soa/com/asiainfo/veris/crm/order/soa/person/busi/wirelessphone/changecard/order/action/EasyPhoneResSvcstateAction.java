
package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.changecard.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardBaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: EasyPhoneResSvcstateAction.java
 * @Description: 商务电话补换卡固话号码0898--SIM卡和服务台帐处理
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-24 下午2:43:40 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-24 yxd v1.0.0 修改原因
 */
public class EasyPhoneResSvcstateAction implements ITradeAction
{
    /**
     * @Function: createResTrade()
     * @Description: SIM卡
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-24 下午2:47:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 yxd v1.0.0 修改原因
     */
    public void createResTrade(BusiTradeData btd, SimCardReqData reqData, IData userInfo) throws Exception
    {
        // 固话--号码信息
        String userIdB = userInfo.getString("USER_ID_B");
        String serialNumberB = userInfo.getString("SERIAL_NUMBER_B");
        SimCardBaseReqData newSimInfo = reqData.getNewSimCardInfo();

        // 终止老SIM卡信息
        IDataset oldSimInfos = UserResInfoQry.getUserResInfosByUserIdResTypeCode(userIdB, "1");
		if (IDataUtil.isNotEmpty(oldSimInfos))
		{
			ResTradeData oldResTD = new ResTradeData(oldSimInfos.getData(0));
			oldResTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
			oldResTD.setEndDate(SysDateMgr.getSysTime());
			btd.add(serialNumberB, oldResTD);
		}

        // 新增新卡信息
        ResTradeData newResTD = new ResTradeData();
        newResTD.setUserId(userIdB);
        newResTD.setUserIdA("-1");
        newResTD.setResTypeCode("1");
        newResTD.setResCode(newSimInfo.getSimCardNo());
        newResTD.setInstId(SeqMgr.getInstId());
        newResTD.setImsi(newSimInfo.getImsi());
        newResTD.setKi(newSimInfo.getKi());
        newResTD.setStartDate(SysDateMgr.getSysDate());
        newResTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        newResTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumberB, newResTD);
    }

    /**
     * @Function: createUserSvcstateTrade()
     * @Description: 为0898固话号码停机用户开机
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-24 下午4:39:52 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 yxd v1.0.0 修改原因
     */
    public void createUserSvcstateTrade(BusiTradeData btd, SimCardReqData reqData, IData userInfo) throws Exception
    {
        String userIdB = userInfo.getString("USER_ID_B");
        String serialNumberB = userInfo.getString("SERIAL_NUMBER_B");
        IDataset userStateInfos = UserSvcStateInfoQry.getUserMainState(userIdB);
        if (DataSetUtils.isNotBlank(userStateInfos))
        {
            IData userStateInfo = userStateInfos.first();
            if (!StringUtils.equals("0", userStateInfo.getString("STATE_CODE")))
            {
                // 终止
                SvcStateTradeData oldData = new SvcStateTradeData(userStateInfo);
                oldData.setEndDate(SysDateMgr.getSysTime());
                oldData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumberB, oldData);
                // 新增
                SvcStateTradeData newData = new SvcStateTradeData();
                newData.setInstId(SeqMgr.getInstId());
                newData.setUserId(userIdB);
                newData.setServiceId(oldData.getServiceId());
                newData.setStartDate(SysDateMgr.getSysTime());
                newData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                newData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                newData.setMainTag(oldData.getMainTag());
                newData.setStateCode("0");
                btd.add(serialNumberB, newData);
            }
        }
    }

    public void executeAction(BusiTradeData btd) throws Exception
    {
        SimCardReqData reqData = (SimCardReqData) btd.getRD();
        // 获取绑定号码信息(非空就是商务电话)
        IDataset relaUUInfos = RelaUUInfoQry.getRelaUUInfoByUserIda(reqData.getUca().getUserId(), "T2", null);
        if (DataSetUtils.isNotBlank(relaUUInfos))
        {
            IData userInfo = relaUUInfos.first();
            this.createResTrade(btd, reqData, userInfo);
            this.createUserSvcstateTrade(btd, reqData, userInfo);
        }
    }
}
