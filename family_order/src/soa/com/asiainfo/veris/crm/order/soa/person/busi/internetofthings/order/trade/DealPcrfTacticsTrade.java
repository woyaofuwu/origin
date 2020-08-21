
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.trade;

import java.util.List;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PcrfTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.DealPcrfTacticsReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.PcrfTacticData;


public class DealPcrfTacticsTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	DealPcrfTacticsReqData prd = (DealPcrfTacticsReqData) btd.getRD();
    	UcaData uca = btd.getRD().getUca();
    	List<PcrfTacticData> pcrfList = prd.getPcrfList();
    	
    	if (pcrfList != null && !pcrfList.isEmpty())
        {
    		for(PcrfTacticData pcrfData: pcrfList){
    			PcrfTradeData pcrfTradeData = new PcrfTradeData();
    			if(pcrfData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
    				pcrfTradeData.setInstId(SeqMgr.getInstId());
    			}else{
    				pcrfTradeData.setInstId(pcrfData.getInstId());
    			}
    			pcrfTradeData.setUserId(uca.getUserId());
    			pcrfTradeData.setRelaInstId(pcrfData.getRelaInstId());
    			pcrfTradeData.setAcceptMonth(SysDateMgr.getCurMonth());
    			pcrfTradeData.setServiceCode(pcrfData.getServiceCode());
    			pcrfTradeData.setUsageState(pcrfData.getUsageState());
    			pcrfTradeData.setBillingType(pcrfData.getBillingType());
    			pcrfTradeData.setModifyTag(pcrfData.getModifyTag());
    			pcrfTradeData.setStartDate(pcrfData.getStartDate());
    			if(pcrfData.getModifyTag().equals(BofConst.MODIFY_TAG_DEL)){
        			pcrfTradeData.setEndDate(SysDateMgr.getSysTime());
    			}else{
        			pcrfTradeData.setEndDate(pcrfData.getEndDate());
    			}
//    			IDataset infos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(uca.getUserId(), "9A",null);
//				
//    			IDataset service11 = UserSvcInfoQry.getUserProductSvcByUserIdAndInstId(uca.getUserId(), pcrfData.getRelaInstId());
//    			 
//				boolean isMem = false;
//		        if(IDataUtil.isNotEmpty(infos)){
//		            IDataset service = UserSvcInfoQry.getUserProductSvcByUserIdAndInstId(uca.getUserId(), pcrfData.getRelaInstId());
//		            if(IDataUtil.isNotEmpty(service)){
//		                String userida = service.getData(0).getString("USER_ID_A");
//		                if(!"-1".equals(userida)){
//		                	isMem = true;
//		                }
//		            }
//		        }
//		        //标记是否集团成员
//				if(isMem)
//				{
//					pcrfTradeData.setRsrvStr1("WLW_MEM");
//				}
    			btd.add(uca.getSerialNumber(), pcrfTradeData);
    		}
    		
    		if("280".equals(btd.getTradeTypeCode())){
    			dealMainTrade(btd);
    		}
        }
    }
    //集团成员需填写主台账中集团信息
    private void dealMainTrade(BusiTradeData btd) throws Exception {
    	UcaData uca = btd.getRD().getUca();
    	IDataset infos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(uca.getUserId(), "9A",null);
        if (IDataUtil.isNotEmpty(infos))
        {
            String userIdA = infos.getData(0).getString("USER_ID_A");
            String serialNumberA = infos.getData(0).getString("SERIAL_NUMBER_A");
            MainTradeData mainTrade = btd.getMainTradeData(); 
        	mainTrade.setUserIdB(userIdA);
        	mainTrade.setSerialNumberB(serialNumberA);
            IData userData = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (IDataUtil.isNotEmpty(userData))
			{
				String strCustIdB = userData.getString("CUST_ID", "");
				mainTrade.setCustIdB(strCustIdB);
			}    
            
            IData acctData = UcaInfoQry.qryAcctInfoByUserIdForGrp(userIdA);
            if (IDataUtil.isNotEmpty(acctData))
			{
				String accIdB = acctData.getString("ACCT_ID", "");
				mainTrade.setAcctIdB(accIdB);
			} 
        }
    }
}
