package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.DelGroupNetMemberReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupMemberData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class DelGroupNetMemberTrade extends BaseTrade implements ITrade{

	/*
	 * 删除成员
	 * */
	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		DelGroupNetMemberReqData reqData = (DelGroupNetMemberReqData) bd.getRD();
        List<GroupMemberData> mebUcaList = reqData.getMebUcaList();
        
        String snmember = "";
        String ralaCodeB = "";
        String userId = reqData.getUca().getUserId();
        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "61", null);
        IData rela = result.getData(0);
        String roleCodeB = rela.getString("ROLE_CODE_B");
        String userIDA = rela.getString("USER_ID_A");
        if (IDataUtil.isEmpty(result))
        {
            // 查询不到组网关系
            CSAppException.apperr(FamilyException.CRM_GROUP_3636);
        }else 
        {
        	if ("2".equals(roleCodeB)) 
        	{ 
        		snmember = bd.getRD().getPageRequestData().getString("SERIAL_NUMBER");
        		ralaCodeB = "2";//新加一个副号标识，后面用来判断
			}
		}
        
        if (StringUtils.isNotBlank(ralaCodeB)) {
        	reqData.getPageRequestData().put("KEY_RALA_CODE_B", ralaCodeB);
        	reqData.getPageRequestData().put("KEY_SN_B", snmember);
		}
        reqData.getPageRequestData().put("KEY_ROLE_CODE_B", roleCodeB);  
        reqData.getPageRequestData().put("KEY_USER_ID_A", userIDA);
        for (int i = 0; i < mebUcaList.size(); i++) {
			delMeb(bd, mebUcaList.get(i));
		}
        
        //删除后发送短信
        delSMS(bd);
        
	}
	
	private void delMeb(BusiTradeData bd, GroupMemberData groupMeb) throws Exception
    {
		DelGroupNetMemberReqData reqData = (DelGroupNetMemberReqData) bd.getRD();
		
		String sysdate = reqData.getAcceptTime();
		String strRemark ="删除组网成员";
		
		UcaData mebUca = groupMeb.getUca();
	    //mebUca.setAcctTimeEnv();// 分散账期
	
		String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        // 校验成员未完工工单限制 ----start----
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("USER_ID", mebUca.getUserId());
        data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
        data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
        data.put("BRAND_CODE", "");
        FamilyTradeHelper.checkMemberUnfinishTrade(data);
        // 校验成员未完工工单限制 ----end----

        String ralaCodeB = bd.getRD().getPageRequestData().getString("KEY_RALA_CODE_B");
        if (StringUtils.isNotBlank(ralaCodeB) && "2".equals(ralaCodeB)) //若满足条件，表示成员退出组网关系
        {
        	String snB = bd.getRD().getPageRequestData().getString("KEY_SN_B");
        	IDataset memberUserid = RelaUUInfoQry.queryRelaUUBySnb(snB,"61"); 
        	if (IDataUtil.isNotEmpty(memberUserid) && "2".equals(memberUserid.getData(0).getString("ROLE_CODE_B"))) 
        	{
        		IData rela = memberUserid.getData(0);
                String userIdA = rela.getString("USER_ID_A");
                IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
                if (IDataUtil.isEmpty(virtualUser))
                {
                    // 没有找到虚拟用户
                    CSAppException.apperr(FamilyException.CRM_GROUP_3535);
                }
                String roleCodeB = bd.getRD().getPageRequestData().getString("KEY_ROLE_CODE_B");
                if( "1".equals(roleCodeB) ){
                	strRemark = "主号码删除成员";
                }else if( "2".equals(roleCodeB) ){
                	strRemark = "成员号码退出组网";
                }
        		
                //终止UU关系
                RelationTradeData delMebRelTradeData = new RelationTradeData(rela);
                delMebRelTradeData.setRsrvStr2("");
                delMebRelTradeData.setShortCode("");
                delMebRelTradeData.setEndDate(sysdate);
                delMebRelTradeData.setRsrvTag1("");
                delMebRelTradeData.setRsrvStr1(mebUca.getCustomer().getCustName());
                delMebRelTradeData.setRemark(strRemark);
                delMebRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                bd.add(mebUca.getSerialNumber(), delMebRelTradeData);
                
                
                List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
                for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
                {
                    DiscntTradeData userDiscnt = userDiscntList.get(j);
                    if (userIdA.equals(userDiscnt.getUserIdA()))
                    {
                        DiscntTradeData delDiscntTD = userDiscnt.clone();
                        delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        delDiscntTD.setEndDate(sysdate);
                        delDiscntTD.setRemark(strRemark);
                        bd.add(mebUca.getSerialNumber(), delDiscntTD);
                    }
                }
                
			}
		}else 
		{
			IDataset result = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", mebUca.getUserId(), "2");
			if (IDataUtil.isEmpty(result)) 
        	{
        		// 查询不到成员的UU关系
                CSAppException.apperr(FamilyException.CRM_GROUP_3636);
			}
			
    		IData rela = result.getData(0);
            String userIdA = rela.getString("USER_ID_A");
            IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (IDataUtil.isEmpty(virtualUser))
            {
                // 没有找到虚拟用户
                CSAppException.apperr(FamilyException.CRM_GROUP_3535);
            }
            String roleCodeB = bd.getRD().getPageRequestData().getString("KEY_ROLE_CODE_B");
            if( "1".equals(roleCodeB) ){
            	strRemark = "主号码删除成员";
            }else if( "2".equals(roleCodeB) ){
            	strRemark = "成员号码退出组网";
            }
    		
            //终止UU关系
            RelationTradeData delMebRelTradeData = new RelationTradeData(rela);
            delMebRelTradeData.setRsrvStr2("");
            delMebRelTradeData.setShortCode("");
            delMebRelTradeData.setEndDate(sysdate);// 改为立即失效
            delMebRelTradeData.setRsrvTag1("1");
            delMebRelTradeData.setRsrvStr1(mebUca.getCustomer().getCustName());
            delMebRelTradeData.setRemark(strRemark);
            delMebRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(mebUca.getSerialNumber(), delMebRelTradeData);
            
            
            List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
            for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
            {
                DiscntTradeData userDiscnt = userDiscntList.get(j);
                if (userIdA.equals(userDiscnt.getUserIdA()))
                {
                    DiscntTradeData delDiscntTD = userDiscnt.clone();
                    delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delDiscntTD.setEndDate(sysdate);
                    delDiscntTD.setRemark(strRemark);
                    bd.add(mebUca.getSerialNumber(), delDiscntTD);
                }
            }
            
		}
        
    }
	
	/**
     * 发送删除成员成功后短信通知
     * @param bd
     * @throws Exception
     */
    private void delSMS(BusiTradeData bd) throws Exception
    {
    	DelGroupNetMemberReqData reqData = (DelGroupNetMemberReqData) bd.getRD(); 
		  UcaData uca = reqData.getUca();
		  String mainSN = uca.getSerialNumber();
		  List<GroupMemberData> mebs = reqData.getMebUcaList();
		  for(int i=0;i<mebs.size();i++)
		  {
			  GroupMemberData memberData=mebs.get(i);
			  
			  String memberSN = memberData.getUca().getSerialNumber();
			  
			  
			  
			  String userIDA = bd.getRD().getPageRequestData().getString("KEY_USER_ID_A");
			  String roleCodeB = bd.getRD().getPageRequestData().getString("KEY_ROLE_CODE_B");
			  
			  if ("C1".equals(reqData.getCancellMeb())) 
			  {
				IDataset result = RelaUUInfoQry.qryMainSN(userIDA);
		        if (IDataUtil.isNotEmpty(result)) {
		        	mainSN = result.getData(0).getString("SERIAL_NUMBER_B");
				}
		          //给主号通知
				  IData smsData = new DataMap(); 
				  String smsContent="尊敬的客户，"+memberSN+"手机号码由于状态异常，" +
				  					"已不再参加您邀请的多人约消畅享宽带，建议您再邀请其他成员参加，" +
				  					"确保消费金额达到月约消要求，避免产生额外的宽带费用。"+"中国移动海南公司";
			      smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
			      smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空 
				  smsData.put("FORCE_OBJECT", "10086");// 发送对象
			      smsData.put("RECV_OBJECT", mainSN);// 接收对象
			      smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
			      PerSmsAction.insTradeSMS(bd, smsData);
			  }else 
			  {
				  if( "1".equals(roleCodeB) )
				  {
		        	  //给主号通知
					  IData smsData = new DataMap(); // 短信数据
					  String smsContent="尊敬的客户，您已取消"+memberSN+"与您一起参与多人约消畅享宽带，" +
					  					"建议您再邀请其他成员参加，确保消费金额达到月约消要求，避免产生额外的宽带费用。中国移动海南公司";
				      smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
				      smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空 
					  smsData.put("FORCE_OBJECT", "10086");// 发送对象
				      smsData.put("RECV_OBJECT", mainSN);// 接收对象
				      smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
				      PerSmsAction.insTradeSMS(bd, smsData);
		          }else if( "2".equals(roleCodeB) )
		          {
		        	IDataset result = RelaUUInfoQry.qryMainSN(userIDA);
		        	if (IDataUtil.isNotEmpty(result)) {
		        		mainSN = result.getData(0).getString("SERIAL_NUMBER_B");
					}
		        	  //给主号通知
					  IData smsData = new DataMap(); // 短信数据
					  String smsContent="尊敬的客户，"+memberSN+"已取消不再参加您邀请的多人约消畅享宽带，" +
					  					"建议您再邀请其他成员参加，确保消费金额达到月约消要求，避免产生额外的宽带费用。中国移动海南公司";
				      smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
				      smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空 
					  smsData.put("FORCE_OBJECT", "10086");// 发送对象
				      smsData.put("RECV_OBJECT", mainSN);// 接收对象
				      smsData.put("NOTICE_CONTENT", smsContent);// 短信内容
				      PerSmsAction.insTradeSMS(bd, smsData);
				      
				      //给副号发通知
				      IData smsData1 = new DataMap(); // 短信数据
					  String smsContent1="尊敬的客户，您已成功取消。中国移动海南公司";
				      smsData1.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
				      smsData1.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空 
					  smsData1.put("FORCE_OBJECT", "10086");// 发送对象
				      smsData1.put("RECV_OBJECT", memberSN);// 接收对象
				      smsData1.put("NOTICE_CONTENT", smsContent1);// 短信内容
				      PerSmsAction.insTradeSMS(bd, smsData1);
		          }
			  }
			  
		  }
    }
    

}
