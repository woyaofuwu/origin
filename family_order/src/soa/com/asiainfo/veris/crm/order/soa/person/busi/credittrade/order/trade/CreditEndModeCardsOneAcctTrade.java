
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CreditEndModeCardsOneAcctTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset relaUU = RelaUUInfoQry.getRelaUUInfoByRol(userId, "56");
        if (IDataUtil.isNotEmpty(relaUU))
        {
            IData relaData = relaUU.getData(0);
            String userIdA = relaData.getString("USER_ID_A");
            String roleCodeB = relaData.getString("ROLE_CODE_B");
            boolean flag=false;
            // 主号码发起才取消所有的家庭统付成员资料
            if ("1".equals(roleCodeB))
            {
                // -----------终止UU关系---------
                IDataset dataset = RelaUUInfoQry.qryRelaUUByUserIdA(userIdA, "56");
                for (int i = 0, size = dataset.size(); i < size; i++)
                {    IData d=dataset.getData(i);
                    RelationTradeData relaTD = new RelationTradeData(d);
                    relaTD.setUserIdA(userIdA);
                    relaTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    String end_date = SysDateMgr.getAddMonthsLastDay(-1, SysDateMgr.getSysDate()).substring(0, 10) + SysDateMgr.END_DATE;
                    relaTD.setEndDate(end_date);
                   
                    String rcb =d.getString("ROLE_CODE_B");
                    String usn=d.getString("SERIAL_NUMBER_B");
                    UcaData ucaData =UcaDataFactory.getNormalUca(usn);
                    if(null!=ucaData){
                    	String brand_code= ucaData.getBrandCode();
                    	if("2".equals(rcb)&&"PWLW".equals(brand_code)){
                    		relaTD.setRemark("欠费停机后信控发起的7325解除统付关系-物联网副号");
                    		flag=true;
                    	}
                    }
                    btd.add(serialNumber, relaTD);
                }

                // ----------终止付费关系----------
                // 1.根据user_id查询默认付费账户的付费关系
                IData defaultPayRela = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
                if (IDataUtil.isNotEmpty(defaultPayRela))
                {
                    String acctId = defaultPayRela.getString("ACCT_ID");
                    // 2.根据acct_id查询科目为41000且不是默认付费账户的所有付费关系
                    IDataset payRelaInfo = PayRelaInfoQry.qryPayRealInfoByItemAndAcctId("41000", acctId);
                    if (IDataUtil.isNotEmpty(payRelaInfo))
                    {
                        for (int i = 0, size = payRelaInfo.size(); i < size; i++)
                        {
                            IData parRelaData = payRelaInfo.getData(i);
                            PayRelationTradeData payTradeData = new PayRelationTradeData(parRelaData);
                            payTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            String lastCycle = SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getAddMonthsLastDay(-1));
                            payTradeData.setEndCycleId(lastCycle);
                            String uid=parRelaData.getString("USER_ID");
                            UcaData ucaData =UcaDataFactory.getUcaByUserId(uid);
                            if(null!=ucaData){
                            	String brand_code= ucaData.getBrandCode();
                            	if(flag&&"PWLW".equals(brand_code)){
                            		payTradeData.setRemark("欠费停机后信控发起的7325解除统付关系-物联网副号");
                            	}
                            }
                            btd.add(serialNumber, payTradeData);
                        }
                    }
                }

            }
            if(flag){
            	  OtherTradeData otherTradeData = new OtherTradeData();
                  otherTradeData.setRsrvValueCode("END_PAYREL");
                  otherTradeData.setRsrvValue("7325解除统付关系");
                  otherTradeData.setUserId(userId);
                  otherTradeData.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                  otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
                  otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
                  otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
                  otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
                  otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
                  btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);          
            }
            // TODO Auto-generated method stub
            String tradeTypeCode = btd.getTradeTypeCode();
            if (StringUtils.equals("7325", tradeTypeCode))
            {
                btd.getMainTradeData().setSubscribeType("200");
            }
        }
    }
    
    

}
