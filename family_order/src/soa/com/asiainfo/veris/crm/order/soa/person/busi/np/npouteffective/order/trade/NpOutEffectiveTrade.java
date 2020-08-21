
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.DestroyUserComm;
import com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.requestdata.NpOutEffectiveReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpOutEffectiveTrade.java
 * @Description: 携出生效 需要配置 td_s_tradectrl 备份 服开
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014年7月30日 下午3:33:13 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014年7月30日 lijm3 v1.0.0 修改原因
 */
public class NpOutEffectiveTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        createClearScoreTrade(btd);
        createNpTradeData(btd);
        createEndUserInfoTrade(btd);// 终止所有用户信息
        getSvcStateChangeTrade(btd);// 对应登记流程 GeneSVCStateChangeTrade
        modifyUserInfoTrade(btd);// 修改用setRemoveTag为7
        modifyMainSvcStateByUserid(btd);// 修改用户服务状态 对应老系完工流程节点 ModifyUserMainSvcStateGroups

    }

    // 处理积分台账
    public void createClearScoreTrade(BusiTradeData btd) throws Exception
    {
        NpOutEffectiveReqData reqData = (NpOutEffectiveReqData) btd.getRD();
        IData scoreInfo = AcctCall.queryUserScoreone(reqData.getUca().getUserId());

        if (IDataUtil.isNotEmpty(scoreInfo))
        {
            int oldScore = scoreInfo.getInt("SUM_SCORE");
            if (oldScore > 0)
            {
                ScoreTradeData scoreTD = new ScoreTradeData();
                scoreTD.setUserId(btd.getRD().getUca().getUserId());
                scoreTD.setCancelTag("0");
                scoreTD.setSerialNumber(btd.getRD().getUca().getSerialNumber());
                scoreTD.setScore(String.valueOf(oldScore));// 原积分
                scoreTD.setScoreTag("0");// 清理标识
                scoreTD.setScoreChanged(String.valueOf(-oldScore));// 积分改变
                scoreTD.setRemark("NP立即销户积分清零台账");
                btd.add(btd.getRD().getUca().getSerialNumber(), scoreTD);
            }
        }

    }

    // 终止用户相关资料订单
    private void createEndUserInfoTrade(BusiTradeData btd) throws Exception
    {
        DestroyUserComm destroyComm = new DestroyUserComm();
        destroyComm.createEndSvcStateInfoTrade(btd);// 服务状态
        destroyComm.createEndDiscntInfoTrade(btd);// 优惠
        destroyComm.createEndPayRelationInfoTrade(btd);// 付费关系
        destroyComm.createEndRelationUUTrade(btd);// uu关系
        destroyComm.createEndResInfoTrade(btd);// 资源
        destroyComm.createEndSvcInfoTrade(btd);// 服务
        destroyComm.createEndElementInfo(btd);// 元素
        destroyComm.createEndUserTrade(btd);// 用户
        destroyComm.createEndProductTrade(btd);// 产品
        destroyComm.createEndAttrInfoTrade(btd);// 属性

        destroyComm.createEndOtherTrade(btd);// 其他信息
        destroyComm.createEndShareRelaInfoTrade(btd);// 共享关系
        destroyComm.createEndUserWidenetTrade(btd);

    }

    /**
     * @Function: createNpTradeData
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-7 上午10:19:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-7 lijm3 v1.0.0 修改原因
     */
    public void createNpTradeData(BusiTradeData btd) throws Exception
    {
        NpOutEffectiveReqData reqData = (NpOutEffectiveReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();

        IDataset ids = TradeBhQry.getTradeBhInfos(reqData.getUca().getSerialNumber(), reqData.getUca().getUserId(), "41");
        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "查询携出申请历史主台帐无记录.");

        }
        String tradeId = ids.getData(0).getString("TRADE_ID");
        ids = TradeNpQry.getTradeNpByTradeId(tradeId);
        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "查询携出申请TF_B_TRADE_NP台帐无记录.");

        }

        String portInNetid = ids.getData(0).getString("PORT_IN_NETID");
        String portOutNetid = ids.getData(0).getString("PORT_OUT_NETID");
        String npServiceType = ids.getData(0).getString("NP_SERVICE_TYPE");
        String homeNetid = ids.getData(0).getString("HOME_NETID");

        NpTradeData nptd = new NpTradeData();
        nptd.setCredType(reqData.getUca().getCustPerson().getPsptTypeCode());
        nptd.setPsptId(reqData.getUca().getCustPerson().getPsptId());
        nptd.setPhone(reqData.getUca().getCustPerson().getHomePhone());
        nptd.setCustName(reqData.getUca().getCustPerson().getCustName());

        nptd.setSerialNumber(reqData.getUca().getSerialNumber());
        nptd.setUserId(reqData.getUca().getUserId());
        nptd.setTradeTypeCode("42");
        nptd.setBNpCardType("20000000");

        nptd.setNpServiceType(npServiceType);
        nptd.setPortInNetid(portInNetid);
        nptd.setPortOutNetid(portOutNetid);
        nptd.setHomeNetid(homeNetid);
        nptd.setFlowId(reqData.getFlowId());
        nptd.setMessageId(reqData.getMessageId());
        nptd.setCreateTime(reqData.getAcceptTime());
        nptd.setBookSendTime(reqData.getAcceptTime());
        nptd.setCancelTag("0");
        nptd.setState("000");
        //新增受理员工信息 add by dengyi5
        nptd.setUpdateDepartId(CSBizBean.getVisit().getDepartId());
        nptd.setUpdateStaffId(CSBizBean.getVisit().getStaffId());
        nptd.setEparchyCode(CSBizBean.getTradeEparchyCode());
        btd.add(serialNumber, nptd);
    }

    /**
     * @Function: geneSVCStateChangeTrade
     * @Description: 根据服务状态变更参数生成台帐服务子表资料
     * @param btd
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-24 上午9:59:19
     */
    public void geneSVCStateChangeTrade(BusiTradeData btd) throws Exception
    {
        TagInfoQry.queryNormalTagInfoByTagCode("ZZZZ", "CS_CHR_GENESVCSTATENEW", "CSM", "1");
    }

    /**
     * 构建服务状态变更订单表
     * 
     * @param btd
     * @throws Exception
     */
    private void getSvcStateChangeTrade(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.getSvcStateChangeTrade(btd);
    }

    /**
     * 构建服务状态变更订单表
     * 
     * @param btd
     * @throws Exception
     */
    private void modifyMainSvcStateByUserid(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.modifyMainSvcStateByUserId(btd);
    }

    /**
     * @Function: modifyUserInfoTrade
     * @Description: 该函数的功能描述 完工流程 ModiUserCodeset 移至此
     * @param btd
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-24 上午11:25:57
     */
    public void modifyUserInfoTrade(BusiTradeData btd) throws Exception
    {
        List<UserTradeData> utds = btd.get("TF_B_TRADE_USER");
        if (utds != null)
        {
            for (UserTradeData utd : utds)
            {
                utd.setRemoveTag("7");
                utd.setUserStateCodeset("X");
            }
        }
    }

}
