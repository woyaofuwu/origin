
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEncryptGeneInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ModifyPhoneCodeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 初始化服务密码
 * 
 * @author
 */
public class SimCardPwdAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        String newSimPasswd = "";
        String newSimKey = "";

        if ("142".equals(btd.getTradeTypeCode()) || "3821".equals(btd.getTradeTypeCode()))
        {
            SimCardReqData simCardData = (SimCardReqData) btd.getRD();

            newSimPasswd = simCardData.getNewSimCardInfo().getSimCardPasswd();
            newSimKey = simCardData.getNewSimCardInfo().getSimCardPasswdKey();
        }
        if ("143".equals(btd.getTradeTypeCode()))
        {
            ModifyPhoneCodeReqData simCardData = (ModifyPhoneCodeReqData) btd.getRD();

            newSimPasswd = simCardData.getNewSimCardInfo().getSimCardPasswd();
            newSimKey = simCardData.getNewSimCardInfo().getSimCardPasswdKey();
        }

        IDataset userEncyRes = UserEncryptGeneInfoQry.getEncryptGeneBySn(btd.getRD().getUca().getUserId());
        boolean changPwdTag = IDataUtil.isNotEmpty(userEncyRes) && StringUtils.isNotEmpty(newSimPasswd) && StringUtils.isNotEmpty(newSimKey);
        if (changPwdTag)
        {
        	UserTradeData userTrade = null;
        	List<UserTradeData>  userList = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
        	if(userList==null||userList.size()==0){
        		UcaData uca = btd.getRD().getUca();
                UserTradeData ucaData = uca.getUser();
                userTrade = ucaData.clone();
            }
        	else{
        		userTrade = userList.get(0);
        	}
            userTrade.setUserPasswd(newSimPasswd);
            userTrade.setRsrvStr3(newSimKey);
            userTrade.setRsrvStr4(userEncyRes.getData(0).getString("ENCRYPT_GENE", ""));
            userTrade.setRsrvStr5(btd.getRD().getUca().getUser().getUserPasswd());
            userTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            userTrade.setRsrvTag1("1");// 使用密码卡
            
            if(userList==null||userList.size()==0){
            	btd.add(btd.getRD().getUca().getSerialNumber(), userTrade);
            }

            // 如果是新的初始化服务密码，置台帐PROCESS_TAG_SET第一位为N，完工时根据此标识发提醒短信
            MainTradeData mainData = btd.getMainTradeData();
            String processTagSet = mainData.getProcessTagSet();
            if (StringUtils.isEmpty(processTagSet))
            {
                processTagSet = "0";
            }
            processTagSet = "N" + processTagSet.substring(1);
            btd.getMainTradeData().setProcessTagSet(processTagSet);
        }

    }

}
