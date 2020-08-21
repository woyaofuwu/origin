package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.DestroyGroupReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupMemberData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class DestroyGroupTrade extends BaseTrade implements ITrade{
	
	/*
	 * 解散组网
	 * */
	public static void delVirtualUser(BusiTradeData bd, IData pData) throws Exception
    {
        String enddate = "";
        if (StringUtils.isNotBlank(pData.getString("END_DATE"))) 
        {
        	 enddate = pData.getString("END_DATE");
		}else 
		{
			enddate = bd.getRD().getAcceptTime();
		}
        UcaData uca = bd.getRD().getUca();

        String virtualSn = pData.getString("SERIAL_NUMBER");
        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);

        // 终止用户资料
        UserTradeData delUser = virtualUca.getUser().clone();
        delUser.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delUser.setDestroyTime(enddate);
        delUser.setRemoveTag("1");
        bd.add(virtualUca.getSerialNumber(), delUser);

        // 终止客户资料
        CustomerTradeData delCustomer = virtualUca.getCustomer().clone();
        delCustomer.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delCustomer.setRemoveTag("1");
        delCustomer.setRemoveDate(enddate);
        bd.add(virtualUca.getSerialNumber(), delCustomer);
        
        // 终止帐户信息
        AccountTradeData delAcct = virtualUca.getAccount().clone();
        delAcct.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delAcct.setRemoveTag("1");
        delAcct.setRemoveDate(enddate);
        bd.add(virtualUca.getSerialNumber(), delAcct);
        
        // 终止用户主产品信息
        ProductTradeData delPrdTD = virtualUca.getUserMainProduct();
        delPrdTD.setEndDate(enddate);
        delPrdTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        bd.add(virtualUca.getSerialNumber(), delPrdTD);
        
        // 终止用户优惠
        List<DiscntTradeData> userDiscntList = virtualUca.getUserDiscnts();
        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            DiscntTradeData userDiscnt = userDiscntList.get(i);
            DiscntTradeData delDiscntTD = userDiscnt.clone();
            delDiscntTD.setEndDate(enddate);
            delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(virtualUca.getSerialNumber(), delDiscntTD);
        }
        
    }
	
	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception 
	{
		DestroyGroupReqData reqData = (DestroyGroupReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String ucaSn = uca.getSerialNumber();
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        String strRemark = "多人约消组网一键注销";
        
        String sysdate ="";
        String endDate = bd.getRD().getPageRequestData().getString("END_DATE");
		if (StringUtils.isNotBlank(endDate)) 
		{
			sysdate = endDate;
		}else 
		{
			sysdate = bd.getRD().getAcceptTime();
		}
        IDataset relaList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", uca.getUserId(), "1");
        if (IDataUtil.isEmpty(relaList))
        {
            // 查询不到主卡的组网关系
            CSAppException.apperr(FamilyException.CRM_GROUP_3636);
        }

        String virtualSn = relaList.getData(0).getString("SERIAL_NUMBER_A");
        String userIdA = relaList.getData(0).getString("USER_ID_A");
        IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);

        if (IDataUtil.isEmpty(virtualUser))
        {
            // 没有找到虚拟用户
            CSAppException.apperr(FamilyException.CRM_GROUP_3535);
        }

        // 如果不是正常用户则报错
        String removeTag = virtualUser.getString("REMOVE_TAG");
        if (!StringUtils.equals(removeTag, "0"))
        {
            // 组网关系已被注销，请确认再办理
            CSAppException.apperr(FamilyException.CRM_GROUP_3434);
        }
        
        IData pData = new DataMap();
        pData.put("SERIAL_NUMBER", virtualSn);
        pData.put("EDN_DATE", sysdate);
        // 终止虚拟用户资料
        delVirtualUser(bd, pData);
        
        // 处理UU关系
        IDataset mebList = RelaUUInfoQry.qryAllRelation(userIdA);
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData mebData = mebList.getData(i);
            String sn = mebData.getString("SERIAL_NUMBER_B");
            
            //如果成员不存在则继续
            IData user = UcaInfoQry.qryUserInfoBySn(sn);
            if (IDataUtil.isEmpty(user))
				continue;
            
            UcaData mebUca = UcaDataFactory.getNormalUca(sn);
            //mebUca.setAcctTimeEnv();// 分散账期

            // 校验成员未完工工单限制 ----start----
            IData data = new DataMap();
            data.put("TRADE_TYPE_CODE", tradeTypeCode);
            data.put("USER_ID", mebUca.getUserId());
            data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
            data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
            data.put("BRAND_CODE", "");
            FamilyTradeHelper.checkMemberUnfinishTrade(data);
            // 校验成员未完工工单限制 ----end----

            RelationTradeData delMebRelTradeData = new RelationTradeData(mebData);
            delMebRelTradeData.setRsrvStr2("");
            delMebRelTradeData.setShortCode("");
            delMebRelTradeData.setEndDate(sysdate);// 改为立即失效
            delMebRelTradeData.setRsrvTag1("1");
            delMebRelTradeData.setRsrvStr1(mebUca.getCustomer().getCustName());
            delMebRelTradeData.setRemark(strRemark);
            delMebRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(ucaSn, delMebRelTradeData);

            // 处理优惠
            List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
            for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
            {
                DiscntTradeData userDiscnt = userDiscntList.get(j);
                if (userIdA.equals(userDiscnt.getUserIdA()))
                {
                    DiscntTradeData delDiscntTD = userDiscnt.clone();
                    delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delDiscntTD.setEndDate(sysdate);// 改为立即失效 
                    delDiscntTD.setRemark(strRemark);
                    bd.add(mebUca.getSerialNumber(), delDiscntTD);
                }
            }
            
        }
        
        //解散组网发送短信
        destroySMS(bd);
	}
	
	/**
     * 发送解散组网成功后短信通知
     * @param bd
     * @throws Exception
     */
    private void destroySMS(BusiTradeData bd) throws Exception
    {
    	  DestroyGroupReqData reqData = (DestroyGroupReqData) bd.getRD(); 
		  UcaData uca = reqData.getUca();
		  String mainSN = uca.getSerialNumber();
			  
		  //给主号通知
		  IData smsData = new DataMap(); // 短信数据
		  String smsContent="尊敬的客户，您已成功解散多人约消畅享宽带，详询10086。中国移动海南公司";
	      smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
	      smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空 
		  smsData.put("FORCE_OBJECT", "10086");// 发送对象
	      smsData.put("RECV_OBJECT", mainSN);// 接收对象
	      smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
	      PerSmsAction.insTradeSMS(bd, smsData);
    }

}
