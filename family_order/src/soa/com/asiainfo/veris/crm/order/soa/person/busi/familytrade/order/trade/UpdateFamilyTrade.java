
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.UpdateFamilyReqData;

public class UpdateFamilyTrade extends BaseTrade implements ITrade
{

    public static void delVirtualUser(BusiTradeData bd, String virtualSn) throws Exception
    {
        // 这里用UcaDataFactory会存在潜在问题，因为这些虚拟用户的号码都是一样的，如果有多个USER
        // 则只会构造一个UCA
        // UcaData virtualUca = UcaDataFactory.getUcaByUserId(userIdA);
        String sysdate = bd.getRD().getAcceptTime();
        UcaData uca = bd.getRD().getUca();

        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);

        // 终止用户资料
        UserTradeData delUser = virtualUca.getUser().clone();
        delUser.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delUser.setDestroyTime(sysdate);
        delUser.setRemoveTag("1");
        bd.add(virtualUca.getSerialNumber(), delUser);

        // 终止客户资料
        CustomerTradeData delCustomer = virtualUca.getCustomer().clone();
        delCustomer.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delCustomer.setRemoveTag("1");
        delCustomer.setRemoveDate(sysdate);
        bd.add(virtualUca.getSerialNumber(), delCustomer);

        if (null != virtualUca.getCustFamily())
        {
            CustFamilyTradeData delCustFamily = virtualUca.getCustFamily().clone();
            delCustFamily.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delCustFamily.setRemoveTag("1");
            delCustFamily.setRemoveDate(sysdate);
            bd.add(virtualUca.getSerialNumber(), delCustFamily);
        }

        // 终止帐户信息
        AccountTradeData delAcct = virtualUca.getAccount().clone();
        delAcct.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delAcct.setRemoveTag("1");
        delAcct.setRemoveDate(sysdate);
        bd.add(virtualUca.getSerialNumber(), delAcct);

        // 终止付费关系
        IData payRela = UcaInfoQry.qryDefaultPayRelaByUserId(virtualUca.getUserId());
        if (IDataUtil.isNotEmpty(payRela))
        {
            PayRelationTradeData delPayRela = new PayRelationTradeData(payRela);
            // UserFamilyMgr.fjgetFirstDayThisAcct
            // 这里获取账期还要看下原CPP方法的逻辑
            String acctDay = uca.getAcctDay();
            delPayRela.setEndCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
            delPayRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(virtualUca.getSerialNumber(), delPayRela);
        }

        // 终止用户主产品信息
        ProductTradeData delPrdTD = virtualUca.getUserMainProduct();
        delPrdTD.setEndDate(sysdate);
        delPrdTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        bd.add(virtualUca.getSerialNumber(), delPrdTD);

        // 终止用户服务
        List<SvcTradeData> userSvcList = virtualUca.getUserSvcs();
        for (int i = 0, size = userSvcList.size(); i < size; i++)
        {
            SvcTradeData userSvc = userSvcList.get(i);
            SvcTradeData delSvcTD = userSvc.clone();
            delSvcTD.setEndDate(sysdate);
            delSvcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(virtualUca.getSerialNumber(), delSvcTD);
        }

        // 终止用户优惠
        List<DiscntTradeData> userDiscntList = virtualUca.getUserDiscnts();
        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            DiscntTradeData userDiscnt = userDiscntList.get(i);
            DiscntTradeData delDiscntTD = userDiscnt.clone();
            delDiscntTD.setEndDate(sysdate);
            delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(virtualUca.getSerialNumber(), delDiscntTD);
        }
    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
    	UpdateFamilyReqData reqData = (UpdateFamilyReqData) bd.getRD();
    	String strupdateNowCheckBox = reqData.getPageRequestData().getString("updateNowCheckBox", "");
        UcaData uca = reqData.getUca();
        //String ucaSn = uca.getSerialNumber();
        String ucaUserid = uca.getUserId();
        String sysdate = bd.getRD().getAcceptTime();
        //String sysLdtm = SysDateMgr.getLastDateThisMonth();	// 改为下账期失效 by yanwu
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();

        //获取主号码
        IDataset relaList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("45", ucaUserid, "1");
        if (IDataUtil.isEmpty(relaList))
        {
            // 查询不到主卡的亲亲关系
            CSAppException.apperr(FamilyException.CRM_FAMILY_84);
        }

        //String virtualSn = relaList.getData(0).getString("SERIAL_NUMBER_A");
        String userIdA = relaList.getData(0).getString("USER_ID_A");
        IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);

        if (IDataUtil.isEmpty(virtualUser))
        {
            // 没有找到虚拟用户
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }

        // 如果不是正常用户则报错
        String removeTag = virtualUser.getString("REMOVE_TAG");
        if (!StringUtils.equals(removeTag, "0"))
        {
            // 亲亲网已被注销，请确认再办理
            CSAppException.apperr(FamilyException.CRM_FAMILY_98);
        }
        IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);

		List<DiscntTradeData> userDiscnts = uca.getUserDiscntsByDiscntCodeArray(discntArrays); 
        if( "".equals(strupdateNowCheckBox) || !"true".equals(strupdateNowCheckBox) ){
        	//您的亲亲网已经升级过了,不需要重复办理该业务！
        	//IDataset userDiscnts = UserDiscntInfoQry.getDiscntsByPModeUpdate(ucaUserid, "05");
    		if (ArrayUtil.isNotEmpty(userDiscnts)) {
    			for (int i = 0, size = userDiscnts.size(); i < size; i++) {
    				DiscntTradeData data = userDiscnts.get(i);
    				String mebdc = data.getDiscntCode(); 	// 获取优惠
    				if ("3410".equals(mebdc)) { 					// 如果等于升级优惠，则报错
    					CSAppException.apperr(FamilyException.CRM_FAMILY_111);
    				}
    			}
    		}
        }else if( "true".equals(strupdateNowCheckBox) ){
        	//REQ201505120004 亲亲网升级界面优化 Modify @yanwu begin
            //您的亲亲网存在升级下月1日生效，如需立即生效，请办理立即生效！
            /*IDataset userDiscnts = UserDiscntInfoQry.getDiscntsByPModeUpdate(ucaUserid, "05");
            if (IDataUtil.isNotEmpty(userDiscnts)) {
            	for (int i = 0, size = userDiscnts.size(); i < size; i++) {
    				IData data = userDiscnts.getData(i);
    				String mebdc = data.getString("DISCNT_CODE"); 	// 获取优惠
    				String syssd = data.getString("START_DATE");
    				if ( "3410".equals(mebdc) && !"true".equals(strupdateNowCheckBox) ) {
    					if( sysdate.compareTo(syssd) < 0 ){
    						CSAppException.apperr(FamilyException.CRM_FAMILY_111);
    					}
    				}
    			}
            }*/
        	
            //您的亲亲网已经升级过了,不需要重复办理该业务！
        	userDiscnts = this.getEffectedDiscnts(userDiscnts );
        	//IDataset userDiscnts = UserDiscntInfoQry.getDiscntsByPModeUpdate01(ucaUserid, "05");
    		if (ArrayUtil.isNotEmpty(userDiscnts)) {
    			for (int i = 0, size = userDiscnts.size(); i < size; i++) {
    				DiscntTradeData data = userDiscnts.get(i);
    				String mebdc = data.getDiscntCode(); 	// 获取优惠
    				if ("3410".equals(mebdc)) { 					// 如果等于升级优惠，则报错
    					CSAppException.apperr(FamilyException.CRM_FAMILY_106);
    				}
    			}
    		}
    		//REQ201505120004 亲亲网升级界面优化 Modify @yanwu end
        }
		
        //您的亲亲网成员超出10个，不能升级亲亲网，请删除成员！
        IDataset Goods = RelaUUInfoQry.getRelaByGood(userIdA, "", "45");
		if( Goods.size() > 10 && IDataUtil.isNotEmpty(Goods) )
		{
			CSAppException.apperr(FamilyException.CRM_FAMILY_108);
		}

        // 终止虚拟用户资料
        //delVirtualUser(bd, virtualSn);// 这个方法后续看能否公用
        
        // 处理UU关系
        IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "45");
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData mebData = mebList.getData(i);
            String sn = mebData.getString("SERIAL_NUMBER_B");
            String rcb = mebData.getString("ROLE_CODE_B");
            //String sc = mebData.getString("SHORT_CODE", "");
            
            //如果成员不存在则继续
            IData user = UcaInfoQry.qryUserInfoBySn(sn);
            if (IDataUtil.isEmpty(user))
				continue;
            
            UcaData mebUca = UcaDataFactory.getNormalUca(sn);
            mebUca.setAcctTimeEnv();// 分散账期

            // 校验成员未完工工单限制 ----start----
            IData data = new DataMap();
            data.put("TRADE_TYPE_CODE", tradeTypeCode);
            data.put("USER_ID", mebUca.getUserId());
            data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
            data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
            data.put("BRAND_CODE", "");
            FamilyTradeHelper.checkMemberUnfinishTrade(data);
            // 校验成员未完工工单限制 ----end----

            /*RelationTradeData delMebRelTradeData = new RelationTradeData(mebData);
            delMebRelTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());// 改为下账期失效 by zhouwu
            delMebRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(ucaSn, delMebRelTradeData);*/


            // 处理优惠
            boolean adduserDiscnt = false;
            List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
            for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
            {
                DiscntTradeData userDiscnt = userDiscntList.get(j);
                /*String end_date=userDiscnt.getEndDate();
                if(end_date!=null&&end_date.length()>=10){
                	end_date=end_date.substring(0,10);
                }
                String tmp_end_date=SysDateMgr.getLastDateThisMonth().substring(0,10);*/
                if ( userIdA.equals(userDiscnt.getUserIdA()) )//&& !tmp_end_date.equals(end_date)
                {
                	if( "".equals(strupdateNowCheckBox) || !"true".equals(strupdateNowCheckBox) ){
                		//下账期生效  by yanwu
                		DiscntTradeData delDiscntTD = userDiscnt.clone();
                        delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());// 改为下账期失效 by yanwu
                        bd.add(mebUca.getSerialNumber(), delDiscntTD);
                        
                        DiscntTradeData addDiscntTD = userDiscnt.clone();
                        addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        //主号优惠3410，副号：3411
                        if( "1".equals(rcb) ){
                        	addDiscntTD.setElementId("3410");
                        }else{
                        	addDiscntTD.setElementId("3411");
                        }
                        addDiscntTD.setInstId(SeqMgr.getInstId());
                        addDiscntTD.setStartDate(SysDateMgr.getFirstDayOfNextMonth());// 改为下账期生效 by yanwu
                        addDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
                        bd.add(mebUca.getSerialNumber(), addDiscntTD);
                        
                	}else if( "true".equals(strupdateNowCheckBox) ){
                		//改为立即生效  by yanwu
                		DiscntTradeData delDiscntTD = userDiscnt.clone();
                        delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        delDiscntTD.setEndDate(sysdate);					// 改为立即失效 by yanwu
                        bd.add(mebUca.getSerialNumber(), delDiscntTD);
                        
                        if( !adduserDiscnt ){
                        	adduserDiscnt = true;
                        	DiscntTradeData addDiscntTD = userDiscnt.clone();
                            addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            //主号优惠3410，副号：3411
                            if( "1".equals(rcb) ){
                            	addDiscntTD.setElementId("3410");
                            }else{
                            	addDiscntTD.setElementId("3411");
                            }
                            addDiscntTD.setInstId(SeqMgr.getInstId());
                            addDiscntTD.setStartDate(sysdate);			// 改为立即生效 by yanwu
                            addDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
                            addDiscntTD.setRemark("亲亲网升级立即生效");
                            bd.add(mebUca.getSerialNumber(), addDiscntTD);
                            
                        }
                	}
                    
                }
            }

            // 处理可选优惠
            IDataset commparaList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", mebUca.getUserEparchyCode());
            for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
            {
                DiscntTradeData userDiscnt = userDiscntList.get(j);
                for (int k = 0, commparaSize = commparaList.size(); k < commparaSize; k++)
                {
                    IData commpara = commparaList.getData(k);
                    if (commpara.getString("PARAM_CODE", "").equals(userDiscnt.getElementId()))
                    {
                        DiscntTradeData delDiscntTD = userDiscnt.clone();
                        delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());// 改为下账期失效 by zhouwu
                        bd.add(mebUca.getSerialNumber(), delDiscntTD);
                        break;
                    }
                }

            }
        }
        
    }
    
    public String getDiscntArray(IDataset datas) throws Exception
    {
    	String discnts ="";
    	if(IDataUtil.isNotEmpty(datas))
    	{
    		for(int i=0;i<datas.size();i++)
    		{
    			IData data = datas.getData(i);
    			discnts += data.getString("DISCNT_CODE") +",";
    		}
    		if(StringUtils.isNotBlank(discnts))
    		{
    			discnts = discnts.substring(0, discnts.length()-1);
    		}
    	}
    	return discnts;
    }
    
    public List<DiscntTradeData> getEffectedDiscnts(List<DiscntTradeData> discnts)throws Exception
    {
    	List<DiscntTradeData> effectedDiscnts = new ArrayList<DiscntTradeData>();
    	if(ArrayUtil.isNotEmpty(discnts))
    	{
    		for(int i=0;i<discnts.size();i++)
    		{
    			DiscntTradeData userDiscnt = discnts.get(i);
    			String sysdate =SysDateMgr.getSysDate();
    			if(sysdate.compareTo(userDiscnt.getStartDate())>=0 && sysdate.compareTo(userDiscnt.getEndDate())<0)
    			{
    				effectedDiscnts.add(userDiscnt);
    			}
    		}
    	}
    	return effectedDiscnts;
    }

}
