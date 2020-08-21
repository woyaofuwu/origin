
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changesvcstate.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;

public class FixTelChangeSvcStateTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        UcaData uca = btd.getRD().getUca();
        String userId = uca.getUserId();
        // String serialNumber = uca.getSerialNumber();
        // String acceptTime = btd.getRD().getAcceptTime(); // 通过受理时间获得受理月份
        //		 
        // IDataset svcStateParaBuf = TradeSvcStateParamInfoQry.querySvcStateParamByKey(tradeTypeCode,
        // uca.getBrandCode(), uca.getProductId(), uca.getUser().getEparchyCode());
        // if(IDataUtil.isNotEmpty(svcStateParaBuf)) {
        // for(int i=0,size=svcStateParaBuf.size(); i<size; i++) {
        // IData svcStateParaInfo = svcStateParaBuf.getData(i);
        // String serviceId = svcStateParaInfo.getString("SERVICE_ID");
        // String oldStateCode = svcStateParaInfo.getString("OLD_STATE_CODE");
        // String newStateCode = svcStateParaInfo.getString("NEW_STATE_CODE");
        // SvcStateTradeData svcStateTD = btd.getRD().getUca().getUserSvcsStateByServiceId(serviceId);
        // if(svcStateTD!=null && svcStateTD.getStateCode().equals(oldStateCode)) {
        // SvcStateTradeData delTrade = svcStateTD.clone();
        // delTrade.setEndDate(acceptTime);
        // delTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);//删除
        // btd.add(serialNumber, delTrade);
        //					
        // SvcStateTradeData addTrade = new SvcStateTradeData();
        // addTrade.setUserId(userId);
        // addTrade.setServiceId(serviceId);
        // addTrade.setStateCode(newStateCode);
        // addTrade.setMainTag(svcStateTD.getMainTag());
        // addTrade.setStartDate(acceptTime);
        // addTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
        // addTrade.setInstId(SeqMgr.getInstId());
        // addTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);//新增
        // btd.add(serialNumber, addTrade);
        // }
        // }
        // }
        IDataset dataset = TradeInfoQry.getTradeBookByUserIdTradeType(userId, PersonConst.TRADE_TYPE_CODE_FIX_TEL_STOP);
        if (PersonConst.TRADE_TYPE_CODE_FIX_TEL_OPEN.equals(tradeTypeCode) && IDataUtil.isNotEmpty(dataset))
        {
            // 报开时有预约报停工单，直接完工报开
        }
        else
        {
            ChangeSvcStateComm comm = new ChangeSvcStateComm();
            comm.getSvcStateChangeTrade(btd);// 获取用户服务状态变更订单
            comm.modifyMainSvcStateByUserId(btd);// 修改用户主体服务状态和最后停机时间

            MainTradeData mainTrade = btd.getMainTradeData();
            mainTrade.setRemark(btd.getRD().getRemark());
            mainTrade.setNetTypeCode(PersonConst.FIX_TEL_NET_TYPE_CODE);
        }
        //信控对商务电话欠费半停7802、欠停7803、缴费开机7801、商务电话报停9760、商务电话报开9761、商务电话局方停机9762、商务电话局方开机9763时，
        //在主台账中的RSRV_STR8/RSRV_STR9/RSRV_STR10，记录标志位、157号码、相应的IMSI，以便服务开通可以送指令。
        String brand = btd.getMainTradeData().getBrandCode();
        if ((StringUtils.equals("7801", tradeTypeCode) || StringUtils.equals("7802", tradeTypeCode) || StringUtils.equals("7803", tradeTypeCode)
        		|| StringUtils.equals("9760", tradeTypeCode) || StringUtils.equals("9761", tradeTypeCode) || StringUtils.equals("9762", tradeTypeCode)
        		|| StringUtils.equals("9763", tradeTypeCode))
        		&& (StringUtils.equals("TT02", brand) || StringUtils.equals("TT04", brand)))
        {
            String serialNumberA = ""; // 157号码
            String userIdA = ""; // 157号码对应的userId
            String imsiA = ""; // 157号码对应的IMSI
            IDataset relaUUInfos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(btd.getRD().getUca().getUserId(), "T2", null);
            if (DataSetUtils.isNotBlank(relaUUInfos))
            {
                serialNumberA = relaUUInfos.first().getString("SERIAL_NUMBER_A","");
                userIdA = relaUUInfos.first().getString("USER_ID_A","");               
            }
            IDataset userResInfos = UserResInfoQry.queryUserResByUserIdResType(userIdA,"1");
            if (DataSetUtils.isNotBlank(userResInfos))
            {
            	imsiA = userResInfos.first().getString("IMSI","");             
            }
            //System.out.println("--------zx1796-------serialNumberA:"+serialNumberA+",imsiA:"+imsiA);
            btd.getMainTradeData().setRsrvStr8("1");
            btd.getMainTradeData().setRsrvStr9(serialNumberA);
            btd.getMainTradeData().setRsrvStr10(imsiA);
        }
    }

}
