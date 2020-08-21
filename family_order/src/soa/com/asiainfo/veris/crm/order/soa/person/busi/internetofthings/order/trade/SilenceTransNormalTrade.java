package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SilenceTransNormalTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception 
	{
		UcaData uca = btd.getRD().getUca();
		
        UserTradeData userTradeData = uca.getUser();

        //将测试期优惠立即截止
        IData testDiscnt = getTestDiscntPara(btd, uca);
        
        if ("F".equals(userTradeData.getUserTypeCode()) || "2".equals(testDiscnt.getString("PARA_CODE7")))
        {
	        UserTradeData cloneUser = userTradeData.clone();
	        cloneUser.setAcctTag("0");// 正常期用户 出账标识为正常处理
	        cloneUser.setUserTypeCode("0");// 正常期用户标识
	        cloneUser.setFirstCallTime(SysDateMgr.getSysTime());// 首话时间为当前时间
	        cloneUser.setModifyTag(BofConst.MODIFY_TAG_UPD);
	        btd.add(uca.getSerialNumber(), cloneUser);
	        
	        SvcStateTradeData svcStateTrade = uca.getUserSvcsStateByServiceId("99010000");
	        svcStateTrade.setEndDate(SysDateMgr.getSysTime());
	        svcStateTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
	        btd.add(uca.getSerialNumber(), svcStateTrade);
	        
	        SvcStateTradeData newSvcStateTrade = new SvcStateTradeData();
	        newSvcStateTrade.setUserId(uca.getUserId());
	        newSvcStateTrade.setEndDate(SysDateMgr.getTheLastTime());
	        newSvcStateTrade.setInstId(SeqMgr.getInstId());
	        newSvcStateTrade.setMainTag("1");
	        newSvcStateTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        newSvcStateTrade.setRemark("物联网正常期");
	        newSvcStateTrade.setServiceId("99010000");
	        newSvcStateTrade.setStartDate(SysDateMgr.getSysTime());
	        newSvcStateTrade.setStateCode("0");
	        btd.add(uca.getSerialNumber(), newSvcStateTrade);
	        
	        //修改普通产品生效时间
	        //changeNormalProductEffectTime(btd,uca);
        }
        else
        {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "非测试期和沉默期用户不能转为正常期用户");
        }
       
	}
	public IData getTestDiscntPara(BusiTradeData btd, UcaData uca) throws Exception
	{
		IData idOutput = new DataMap();
		idOutput.put("PARA_CODE7", "1");
		IDataset idsCommparaInfo = CommparaInfoQry.getOnlyByAttr("CSM", "1551", "0898");
        if (IDataUtil.isNotEmpty(idsCommparaInfo))
        {
            for (int i = 0; i < idsCommparaInfo.size(); i++) 
            {
            	IData idConfig = idsCommparaInfo.getData(i);
            	String strDiscntId = idConfig.getString("PARAM_CODE");
            	List<DiscntTradeData> lsDiscntTest = uca.getUserDiscntByDiscntId(strDiscntId);
            	if(CollectionUtils.isNotEmpty(lsDiscntTest))
            	{
            		idOutput.put("PARA_CODE7", "2");
            		DiscntTradeData dtDiscntTest = lsDiscntTest.get(0).clone();
                	dtDiscntTest.setModifyTag(BofConst.MODIFY_TAG_UPD);
                	dtDiscntTest.setEndDate(SysDateMgr.getSysTime());
                    btd.add(uca.getSerialNumber(), dtDiscntTest);
            	}
            }
        }
        return idOutput;
	}
	
	/**
     * 进入正常期修改普通产品的生效时间。 
     * 
     * @param btd
     * @throws Exception
     */
    private void changeNormalProductEffectTime(BusiTradeData btd, UcaData uca) throws Exception
    {
    	 IDataset commparaInfos = CommparaInfoQry.getOnlyByAttr("CSM", "1551", "0898");
         if (IDataUtil.isEmpty(commparaInfos))
         {
             return;
         }
         IDataset userDiscnts = BofQuery.queryUserAllValidDiscnt(uca.getUserId(), "0898");
         //正式优惠改成立即生效
         if(IDataUtil.isNotEmpty(userDiscnts))
         {
        	 for (int i = 0; i < userDiscnts.size(); i++) 
        	 {
        		 DiscntTradeData discntTradeData = new DiscntTradeData(userDiscnts.getData(i));
        		 String strElementId = discntTradeData.getElementId();
        		 boolean isModfy = true;
                 for (int j = 0; j < commparaInfos.size(); j++)
                 {
                	 IData commparaInfo = commparaInfos.getData(j);
                     String paraCode = commparaInfo.getString("PARAM_CODE", "");
                     if (strElementId.equals(paraCode))
                     {
                     	isModfy = false;
                     	break;
                     }
                 }
                 if(isModfy)
                 {
                	 DiscntTradeData dtDiscntDel = discntTradeData.clone();
                	 dtDiscntDel.setModifyTag(BofConst.MODIFY_TAG_DEL);
                	 String strEndDate = dtDiscntDel.getEndDate();
                	 dtDiscntDel.setEndDate(SysDateMgr.getSysTime());
                     btd.add(uca.getSerialNumber(), dtDiscntDel);
                     
                     String strInstIdDel = dtDiscntDel.getInstId();
                     String strInstIdAdd = SeqMgr.getInstId();
                     String strSysTime = SysDateMgr.addSecond(SysDateMgr.getSysTime(), 1);
                     DiscntTradeData dtDiscntAdd = discntTradeData.clone();
                     dtDiscntAdd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                     dtDiscntAdd.setStartDate(strSysTime);
                     dtDiscntAdd.setInstId(strInstIdAdd);
                     
                     List<AttrTradeData> lsAttrDel = uca.getUserAttrsByRelaInstId(strInstIdDel);
                     if(CollectionUtils.isNotEmpty(lsAttrDel))
                     {
                    	 //List<AttrTradeData> lsAttrAdd = new ArrayList<AttrTradeData>();
                    	 for (int j = 0; j < lsAttrDel.size(); j++) 
                    	 {
                    		 AttrTradeData atAttrAdd = lsAttrDel.get(j).clone();
                    		 atAttrAdd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    		 atAttrAdd.setStartDate(strSysTime);
                    		 atAttrAdd.setEndDate(strEndDate);
                    		 atAttrAdd.setInstId(SeqMgr.getInstId());
                    		 atAttrAdd.setRelaInstId(strInstIdAdd);
                    		 uca.getUserAttrs().add(atAttrAdd);
                    		 //lsAttrAdd.add(atAttrAdd);
                    		 btd.add(uca.getSerialNumber(), atAttrAdd);
                    	 }
                    	 //dtDiscntAdd.getAttrTradeDatas().add(e).setAttrTradeDatas(lsAttrAdd);
                     }
                     btd.add(uca.getSerialNumber(), dtDiscntAdd);
                 } 
        	 }
         }
    }
}
