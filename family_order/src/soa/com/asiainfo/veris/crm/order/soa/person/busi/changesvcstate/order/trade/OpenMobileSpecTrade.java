
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;

/**
 * 局方开机特殊处理：由 P_CSM_OPENMOBILEINTF 存储过程翻译过来
 * 
 * @author liuke
 */
public class OpenMobileSpecTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        
        String serviceId = "0";
        //随e行
        if(StringUtils.equals("G005", ucaData.getBrandCode()))
        {
        	serviceId = "1002";
        }
        SvcStateTradeData svcStateTradeData = ucaData.getUserSvcsStateByServiceId(serviceId);
        if (null != svcStateTradeData)
        {
            SvcStateTradeData delSvcStateTrade = new SvcStateTradeData();
            delSvcStateTrade.setUserId(ucaData.getUserId());
            delSvcStateTrade.setServiceId(serviceId);
            delSvcStateTrade.setStateCode("4");// 局方停机状态
            delSvcStateTrade.setMainTag(svcStateTradeData.getMainTag());
            delSvcStateTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delSvcStateTrade.setInstId(svcStateTradeData.getInstId());
            delSvcStateTrade.setStartDate(svcStateTradeData.getStartDate());
            delSvcStateTrade.setEndDate(btd.getRD().getAcceptTime());
            delSvcStateTrade.setRemark(btd.getRD().getRemark());
            btd.add(ucaData.getSerialNumber(), delSvcStateTrade);

            SvcStateTradeData newSvcStateTrade = new SvcStateTradeData();
            newSvcStateTrade.setUserId(ucaData.getUserId());
            newSvcStateTrade.setServiceId(serviceId);
            newSvcStateTrade.setStateCode("0");// 开通状态
            newSvcStateTrade.setMainTag(svcStateTradeData.getMainTag());
            newSvcStateTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            newSvcStateTrade.setInstId(SeqMgr.getInstId());
            newSvcStateTrade.setStartDate(btd.getRD().getAcceptTime());
            newSvcStateTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
            newSvcStateTrade.setRemark(btd.getRD().getRemark());
            btd.add(ucaData.getSerialNumber(), newSvcStateTrade);
        }

        IData param = new DataMap();
        param.put("USER_ID", ucaData.getUserId());
        param.put("TRADE_ID", btd.getTradeId());

        // 更新TF_F_USER_ACTIVATE_TEMP2 表的状态
        /*StringBuilder updActiveSb = new StringBuilder(100);
        updActiveSb.append("UPDATE TF_F_USER_ACTIVATE_TEMP2 A ");
        updActiveSb.append("SET A.STATUS = '1', ");
        updActiveSb.append("A.Trade_Id = :TRADE_ID ");
        updActiveSb.append("WHERE A.USER_ID = :USER_ID ");
        Dao.executeUpdate(updActiveSb, param);*/

        // 更新user表的RSRV_NUM1 字段
        StringBuilder updUserSb = new StringBuilder(100);
        updUserSb.append("UPDATE TF_F_USER A ");
        updUserSb.append("SET RSRV_NUM1 = 2 ");
        updUserSb.append("WHERE A.USER_ID = :USER_ID ");
        Dao.executeUpdate(updUserSb, param);
    }

}
