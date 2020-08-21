
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 用户处于停机状态，换卡且进行开通时，记录子台账
 * 
 * @author wangf
 */
public class GeneSvcAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SimCardReqData simCardRD = (SimCardReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        List<SvcStateTradeData> svcList = uca.getUserSvcsState();
        if (svcList != null && svcList.size() > 0)
        {
            for (SvcStateTradeData svcState : svcList)
            {
                if ("1".equals(svcState.getMainTag()))
                {
                    if ("0".equals(simCardRD.getOpenMobileTag())&&"1".equals(svcState.getStateCode()))
                    {
                        SvcStateTradeData oldData = svcState.clone();
                        oldData.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
                        oldData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(simCardRD.getUca().getSerialNumber(), oldData);

                        SvcStateTradeData newData = new SvcStateTradeData();
                        newData.setInstId(SeqMgr.getInstId());
                        newData.setUserId(simCardRD.getUca().getUserId());
                        newData.setServiceId(oldData.getServiceId());
                        newData.setStartDate(btd.getRD().getAcceptTime());
                        newData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                        newData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        newData.setMainTag(oldData.getMainTag());
                        newData.setStateCode("0");
                        btd.add(simCardRD.getUca().getSerialNumber(), newData);
                        
                        UserTradeData userData = null;
                        List<UserTradeData>  userList = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
                    	if(userList==null||userList.size()==0){
                    		userData = uca.getUser().clone();
                    		
                        }else{
                        	userData = userList.get(0);
                        }
                    	userData.setUserStateCodeset("0");
                        userData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        if(userList==null||userList.size()==0){
                        	btd.add(btd.getRD().getUca().getSerialNumber(), userData);
                        }
                        break;
                    }
                }
            }

            //modify by xiaozb,清报停和挂失的标志位
            ChangeSvcStateComm changeSvcStateComm = new ChangeSvcStateComm();
            changeSvcStateComm.clearRsrvSpecTagInfo(btd);
        }
    }

}
