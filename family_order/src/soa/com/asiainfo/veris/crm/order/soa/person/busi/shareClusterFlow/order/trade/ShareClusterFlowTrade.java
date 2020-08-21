
package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ShareClusterFlowException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareInfoTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareRelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow.ShareClusterFlowQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;
import com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.requestdata.MemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.requestdata.ShareClusterFlowReqData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class ShareClusterFlowTrade extends BaseTrade implements ITrade
{

    // @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
    	ShareClusterFlowReqData reqData = (ShareClusterFlowReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String userId = uca.getUserId();
        String serialNumber= uca.getSerialNumber();
        String memberCancel = reqData.getMemberCancel();
        String cancelTag = reqData.getCancelTag();
        String isExist = reqData.getIsExist();
        String shareId = "";
        String endDate = "";
//        boolean syncFlag = false;
        IData inparams=new DataMap();
        inparams.put("EPARCHY_CODE", "ZZZZ");
        inparams.put("TAG_CODE", "SYNC_SHARE_TOACCT");
        inparams.put("USE_TAG", "0");
//        IDataset sync = TagInfoQry.getTagInfo(inparams);
//        if (IDataUtil.isNotEmpty(sync))
//            syncFlag = true;
        if ("0".equals(memberCancel))
        { // 判断主卡是否已经存在
            IDataset mainList = ShareClusterFlowQry.queryMemberRela(userId, "01");
            if (IDataUtil.isEmpty(mainList))
            {
                shareId = SeqMgr.getUserId();// 如果是新增关系，则shareid为新增序列
            }
            else
            {
                shareId = mainList.getData(0).getString("SHARE_ID");
            }
            // 结束时间获取。取资费的最大结束时间。或者主卡的结束时间。
            IDataset discnt = ShareClusterFlowQry.queryDiscnt(userId);
            if (IDataUtil.isNotEmpty(discnt))
                endDate = discnt.getData(0).getString("END_DATE", "");

            if (endDate.trim().equals(""))
            {
                endDate = SysDateMgr.END_DATE_FOREVER;
            }
            if(cancelTag.equals("1")){
            	IDataset qryShareRelaAlls =ShareClusterFlowQry.queryMember(userId);
                if(qryShareRelaAlls!=null && qryShareRelaAlls.size() >0){
    				for(int i=0;i<qryShareRelaAlls.size();i++){
    					IData qryShareRelaAll = qryShareRelaAlls.getData(i);
    					if(!qryShareRelaAll.getString("END_DATE").equals(SysDateMgr.getLastDateThisMonth())){
	    	                UcaData mebUca = null;
	                        String sn = qryShareRelaAll.getString("SERIAL_NUMBER");
	                        mebUca = UcaDataFactory.getNormalUca(sn);
	                        MemberData mebData = new MemberData();
	                        mebData.setModifyTag(BofConst.MODIFY_TAG_DEL);
	                        mebData.setUca(mebUca);
	                        reqData.addMemberData(mebData);
    					}
    				}
    			}
            }
            
            // 首次新增or 关系全部解除 处理主卡与同步
            if (isExist.equals("0"))
            	//创建主卡流量共享关系表（TF_B_TRADE_SHARE_RELA）、语言共享关系表（TF_B_TRADE_RELATION）、优惠共享（TF_B_TRADE_SHARE）、优惠共享属性（TF_B_TRADE_SHARE_INFO）
                dealMainrRela(bd, shareId, endDate, "add", userId);
            if (cancelTag.equals("1"))
            	//删除主卡流量共享关系表（TF_B_TRADE_SHARE_RELA）、语言共享关系表（TF_B_TRADE_RELATION）、优惠共享（TF_B_TRADE_SHARE）、优惠共享属性（TF_B_TRADE_SHARE_INFO）、
                dealMainrRela(bd, shareId, endDate, "del", userId);
            //创建副卡流量共享关系（TF_B_TRADE_SHARE_RELA）、语言共享关系（TF_B_TRADE_RELATION）
            dealMemBerRela(bd, shareId, endDate, userId,serialNumber,memberCancel,cancelTag,isExist);

        }

        else
        {
        	List<MemberData> mebDataList = reqData.getMemberDataList();
            MemberData mebData = mebDataList.get(0);
            UcaData deckUca = mebData.getUca();
            String mainUserId = deckUca.getUserId();
            IDataset mainList = ShareClusterFlowQry.queryMemberRela(mainUserId, "01");
            if (IDataUtil.isNotEmpty(mainList))
            {
                shareId = mainList.getData(0).getString("SHARE_ID");
            }else{
            	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_22);
            }
        	IDataset discnt = ShareClusterFlowQry.queryDiscnt(mainUserId);
            if (IDataUtil.isNotEmpty(discnt))
                endDate = discnt.getData(0).getString("END_DATE", "");

            if (endDate.trim().equals(""))
            {
                endDate = SysDateMgr.END_DATE_FOREVER;
            }
            String user_id = "";
            IDataset mainDat = ShareClusterFlowQry.queryRelaByShareIdAndRoleCode(shareId, "01");

            if (IDataUtil.isEmpty(mainDat))
                CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_13);
            else{
            	user_id = mainDat.getData(0).getString("USER_ID_B");// 传递主卡的userId
            	serialNumber=mainDat.getData(0).getString("SERIAL_NUMBER");
            }
            dealMemBerRela(bd, shareId, endDate, user_id,serialNumber,memberCancel,cancelTag,isExist);
        }

    }

    // 首次新增 处理主卡与同步
    private void dealMainrRela(BusiTradeData bd, String shareId, String endDate, String tag, String userId) throws Exception
    {
        UcaData uca = bd.getRD().getUca();
        String sysdate = bd.getRD().getAcceptTime();
        bd.addOpenUserAcctDayData(shareId, "1");
        genTradeUserInfo(bd, shareId, endDate, tag);
//        TradeRelationShare(bd,endDate,shareId,tag);
        if ("add".equals(tag))
        {
            // 处理主卡关系新增
            ShareRelaTradeData addRela = new ShareRelaTradeData();
            addRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
            addRela.setShareId(shareId);
            addRela.setInstId(SeqMgr.getInstId());
            addRela.setSerialNumber(uca.getSerialNumber());
            addRela.setUserIdB(userId);
            addRela.setRoleCode("01");// 主卡
            addRela.setStartDate(sysdate);
            addRela.setEndDate(endDate);
            addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
            bd.add(uca.getSerialNumber(), addRela);
            
            RelationTradeData addRelation=new RelationTradeData();
            String serialNumber = "";
    		if (shareId.length() < 8) {
    			serialNumber = "G4" + shareId;
    		} else {
    			serialNumber = "G4" + shareId.substring(shareId.length() - 8);
    		}
            addRelation.setEndDate(endDate);
            addRelation.setInstId(SeqMgr.getInstId());
            addRelation.setModifyTag(BofConst.MODIFY_TAG_ADD);
            addRelation.setRelationTypeCode("G4");
            addRelation.setUserIdB(uca.getUserId());
            addRelation.setUserIdA(shareId);
            addRelation.setSerialNumberA(serialNumber);
            addRelation.setSerialNumberB(uca.getSerialNumber());
            addRelation.setRoleCodeA("0");
            addRelation.setRoleCodeB("1");// 2表示副卡
            addRelation.setOrderno("0");
            addRelation.setStartDate(sysdate);
            bd.add(uca.getSerialNumber(), addRelation);
        }
        else if ("del".equals(tag))
        {
            // 处理主卡关系删除
            IData returnDataMain = ShareClusterFlowQry.queryMemberRela(userId, "01").getData(0);
            ShareRelaTradeData addRela = new ShareRelaTradeData(returnDataMain);
            addRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
            addRela.setEndDate(SysDateMgr.getLastDateThisMonth());
            addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
            bd.add(uca.getSerialNumber(), addRela);
            
            IData relaBData = RelaUUInfoQry.getRelationUUByPk(shareId,uca.getUserId(), "G4", null);
            if (IDataUtil.isEmpty(relaBData))
            {
            	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_23, uca.getUserId());
            }
            relaBData.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
            relaBData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            RelationTradeData delRelation=new RelationTradeData(relaBData);
            bd.add(uca.getSerialNumber(), delRelation);
        }
        // 处理同步

        // 查询用户下所有可以共享4g资费
        IDataset discntInfo = ShareClusterFlowQry.queryDiscnt(userId);
//        if (syncFlag)
//        {
            if ("add".equals(tag))
            {
                for (int i = 0; i < discntInfo.size(); i++)
                {
                    IData discnts = discntInfo.getData(i);
                    IData param = new DataMap();
                    param.put("SHARE_ID", shareId);
                    param.put("SHARE_INST_ID", SeqMgr.getUserId());
                    param.put("DISCNT_CODE", discnts.getString("DISCNT_CODE"));
//                    param.put("PRIMARY_INST_ID", SeqMgr.getUserId());// info
                    param.put("INST_ID", discnts.getString("INST_ID"));// share
//                    param.put("DISCNT_CODE_A", discnts.getString("DISCNT_CODE_A"));// share
//                    param.put("SHARE_TYPE_CODE", discnts.getString("ITEM_TYPE"));// 取资费参数表里的item_type
                    param.put("DISCNT_CODE_A", "1451");//newbilling改造
//                    param.put("SHARE_TYPE_CODE", );
                    param.put("END_DATE", endDate);
                    param.put("MODIFY_TAG", "0");
                    //TF_B_TRADE_SHARE
                    strUserDiscntShare(bd, param);
                    //TF_B_TRADE_SHARE_INFO
                    //循环处理SHARE_TYPE_CODE --new billing
                    IDataset shareTypecodes = ShareClusterFlowQry.queryResId(discnts.getString("DISCNT_CODE"));
                    for(int j=0,s=shareTypecodes.size();j<s;j++){
                    	param.put("SHARE_TYPE_CODE", shareTypecodes.getData(j).getString("RES_ID"));
                        strUserShareStable(bd, param);
                    }
                    
                }
            }
            else if ("del".equals(tag))
            {
                IDataset shareds = ShareClusterFlowQry.queryAllShare(shareId, userId);
                if (IDataUtil.isNotEmpty(shareds))
                {
                    for (int i = 0; i < shareds.size(); i++)
                    {
                        IData share = shareds.getData(i);
                        share.put("MODIFY_TAG", "1");
                        strUserDiscntShare(bd, share);
                    }
                }
                IDataset infos = ShareClusterFlowQry.queryAllShareInfo(userId);
                if (IDataUtil.isNotEmpty(infos))
                {
                    for (int i = 0; i < infos.size(); i++)
                    {
                        IData shareInfo = infos.getData(i);
                        shareInfo.put("MODIFY_TAG", "1");
                        strUserShareStable(bd, shareInfo);
                    }
                }
            }
//        }
            
        //newbilling  增加主卡绑1452资费 
        if ("add".equals(tag)){
        	addMainDiscnt(bd,sysdate,endDate,userId);
        }else if ("del".equals(tag)){
        	removeMainDiscnt(bd,sysdate,endDate,userId);
        }
        

    }

    /**
     * 处理副卡
     * 
     * @param bd
     * @throws Exception
     */
    private void dealMemBerRela(BusiTradeData bd, String shareId, String endDate, String userId,String serialNumber,String memberCancel,String cancelTag,String isExist) throws Exception
    {
    	ShareClusterFlowReqData reqData = (ShareClusterFlowReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();
        List<MemberData> mebDataList = reqData.getMemberDataList();
        String serialNumberA = "";
		 if (shareId.length() < 8) {
		 	serialNumberA = "G4" + shareId;
		 } else {
		 	serialNumberA = "G4" + shareId.substring(shareId.length() - 8);
		 }
        if(memberCancel.equals("0")){//主卡操作
        	 for (int i = 0, size = mebDataList.size(); i < size; i++)
             {
                 MemberData mebData = mebDataList.get(i);
                 String modifyTag = mebData.getModifyTag();
                 UcaData deckUca = mebData.getUca();
                 if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                 {
                     ShareRelaTradeData addRela = new ShareRelaTradeData();
                     addRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
                     addRela.setShareId(shareId);
                     addRela.setInstId(SeqMgr.getInstId());
                     addRela.setSerialNumber(deckUca.getSerialNumber());
                     addRela.setUserIdB(deckUca.getUserId());
                     addRela.setRoleCode("02");// 副卡
                     addRela.setStartDate(sysdate);
                     addRela.setEndDate(endDate);
                     addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
                     bd.add(uca.getSerialNumber(), addRela);

                     RelationTradeData addRelation=new RelationTradeData();
                     addRelation.setEndDate(endDate);
                     addRelation.setInstId(SeqMgr.getInstId());
                     addRelation.setModifyTag(BofConst.MODIFY_TAG_ADD);
                     addRelation.setRelationTypeCode("G4");
                     addRelation.setUserIdB(deckUca.getUserId());
                     addRelation.setUserIdA(shareId);
                     addRelation.setSerialNumberA(serialNumberA);
                     addRelation.setSerialNumberB(deckUca.getSerialNumber());
                     addRelation.setRoleCodeA("0");
                     addRelation.setRoleCodeB("2");// 2表示副卡
                     addRelation.setOrderno("0");
                     addRelation.setStartDate(sysdate);
                     bd.add(uca.getSerialNumber(), addRelation);
                 }
                 else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                 {

                	 IDataset relaData = ShareClusterFlowQry.queryMemberRela(deckUca.getUserId(), "02");
                     if (IDataUtil.isEmpty(relaData))
                     {
                     	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_23, deckUca.getUserId());
                     }
                 	
                     ShareRelaTradeData delRela = new ShareRelaTradeData(relaData.getData(0));
                     delRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
                     delRela.setEndDate(SysDateMgr.getLastDateThisMonth());
                     bd.add(uca.getSerialNumber(), delRela);
                     
                     IData relaBData = RelaUUInfoQry.getRelationUUByPk(shareId,deckUca.getUserId(), "G4", null);
                     if (IDataUtil.isEmpty(relaBData))
                     {
                     	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_23, deckUca.getUserId());
                     }
                     relaBData.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                     relaBData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                     RelationTradeData delRelation=new RelationTradeData(relaBData);
                     bd.add(uca.getSerialNumber(), delRelation);
                 }

             }
        }else{//副卡操作
                MemberData mebData = mebDataList.get(0);
                String modifyTag = mebData.getModifyTag();
//                UcaData deckUca = mebData.getUca();
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                	int iCount = 0;   	//当前存在副号码的个数
                	int memberNum = 0;  //还能添加的副号码的个数
                	IDataset qryShareRelaAll =ShareClusterFlowQry.queryMember(userId);
             		iCount = qryShareRelaAll.size() ;
             		memberNum=4-iCount;
                    ShareRelaTradeData addRela = new ShareRelaTradeData();
                    addRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    addRela.setShareId(shareId);
                    addRela.setInstId(SeqMgr.getInstId());
                    addRela.setSerialNumber(uca.getSerialNumber());
                    addRela.setUserIdB(uca.getUserId());
                    addRela.setRoleCode("02");// 副卡
                    addRela.setStartDate(sysdate);
                    addRela.setEndDate(endDate);
                    addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
                    bd.add(uca.getSerialNumber(), addRela);

                    RelationTradeData addRelation=new RelationTradeData();
                    addRelation.setEndDate(endDate);
                    addRelation.setInstId(SeqMgr.getInstId());
                    addRelation.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    addRelation.setRelationTypeCode("G4");
                    addRelation.setUserIdB(uca.getUserId());
                    addRelation.setUserIdA(shareId);
                    addRelation.setSerialNumberA(serialNumberA);
                    addRelation.setSerialNumberB(uca.getSerialNumber());
                    addRelation.setRoleCodeA("0");
                    addRelation.setRoleCodeB("2");// 2表示副卡
                    addRelation.setOrderno("0");
                    addRelation.setStartDate(sysdate);
                    bd.add(uca.getSerialNumber(), addRelation);
                    
                    IData smsData = new DataMap(); // 短信数据
                    smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
                    smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
                    StringBuilder smsContentmain = new StringBuilder();
                    smsContentmain.append("您好，您同意了");
                    smsContentmain.append(uca.getSerialNumber());
                    smsContentmain.append("加入流量共享群组，当月生效，目前群组共有"+iCount+"个副账户，您还可以添加"+memberNum+"个副账户，您可发送TJFZH+手机号码到10086添加群组副账户，您可以发送SCFZH+手机号码到10086删除群组副账户，添加群组副账户当月生效，删除群组副账户下月生效。您可发送CXLL到10086查询流量消费情况，或发送CCFZHLL到10086查询各副账户共享流量的使用明细，副账户可对主账户共享流量的剩余情况进行查询。您还可以关注微信公众号“和4G-惠分享”，了解相关信息和执行相关操作。中国移动");
                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("RECV_OBJECT", serialNumber);// 接收对象
                    smsData.put("NOTICE_CONTENT", smsContentmain.toString());// 短信内容
                    PerSmsAction.insTradeSMS(bd, smsData);
                    
                    StringBuilder smsContentmeb = new StringBuilder();
                    smsContentmeb.append("您好，您已加入");
                    smsContentmeb.append(serialNumber);
                    smsContentmeb.append("的流量共享群组，本月开始生效，您可以发送TCQZ+群组主账户手机号码到10086，退出群组，下月生效。您可发送CXLL到10086查询本账户流量消费情况，或发送CXZZHGXLL到10086查询主账户共享流量的剩余情况，主账户可对各副账户共享流量的使用明细情况进行查询。中国移动");
                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("RECV_OBJECT", uca.getSerialNumber());// 接收对象
                    smsData.put("NOTICE_CONTENT", smsContentmeb.toString());// 短信内容
                    PerSmsAction.insTradeSMS(bd, smsData);
                    
//                    IData smsData = new DataMap(); // 短信数据
//                    smsData.put("CANCEL_TAG", "0");
                    String smsContentAdd = "";
                    String serialNumberB = serialNumber;
                    smsContentAdd = "您好，"+uca.getSerialNumber()+"申请加入流量共享群组，同意请回复Y，拒绝请回复N。中国移动";
                    
                    smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                    smsData.put("TRADE_ID", bd.getRD().getTradeId());
                    smsData.put("SERIAL_NUMBER", serialNumber);
                    smsData.put("DEAL_STATE", "00");
                    smsData.put("EXTEND_TAG", "0");
                    smsData.put("SMS_CONTENT", smsContentAdd);
                    smsData.put("TIMEOUT", "");
                    smsData.put("OPR_SOURCE", "1");// 报文说明 告警短信 OPR_SOURCE = 2 一级boss点播OPR_SOURCE = 0 平台业务订购类：OPR_SOURCE = 1
                    smsData.put("RSRV_STR1", "");
                    smsData.put("RSRV_STR2", "");
                    smsData.put("RSRV_STR3", "3");
                    smsData.put("RSRV_STR4", "");
                    smsData.put("RSRV_STR5", "");
                    smsData.put("SMS_TYPE", BofConst.ENTITY_CARD);
                    
                    
                    IData preOderData = new DataMap();
                    
                    
                    preOderData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                    preOderData.put("ORDER_ID", bd.getRD().getOrderId());
                    preOderData.put("SVC_NAME", "SS.ShareClusterFlowRegSVC.tradeReg");
                    IDataset mebs = new DatasetList();
                    IData map = new DataMap();
                	map.put("SERIAL_NUMBER", serialNumber);
                	map.put("tag",0);
                    mebs.add(map);
                    preOderData.put("SERIAL_NUMBER",uca.getSerialNumber());
                    preOderData.put("MEB_LIST", mebs);
                    preOderData.put("MEMBER_CANCEL", memberCancel);
                    preOderData.put("CANCEL_CLUSTER", cancelTag);
                    preOderData.put("IS_EXIST", isExist);
                    
                    bd.getRD().setPreType(BofConst.PRE_TYPE_SMS_CONFIRM);
                    
                    TwoCheckSms.twoCheck(bd.getTradeTypeCode(), 0, preOderData, smsData);
                    
                    
                    
                }
                else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {

                	IDataset relaData = ShareClusterFlowQry.queryMemberRela(uca.getUserId(), "02");
                    if (IDataUtil.isEmpty(relaData))
                    {
                    	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_23, uca.getUserId());
                    }
                	
                    ShareRelaTradeData delRela = new ShareRelaTradeData(relaData.getData(0));
                    delRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delRela.setEndDate(SysDateMgr.getLastDateThisMonth());
                    bd.add(uca.getSerialNumber(), delRela);
                    
                    IData relaBData = RelaUUInfoQry.getRelationUUByPk(shareId,uca.getUserId(), "G4", null);
                    if (IDataUtil.isEmpty(relaBData))
                    {
                    	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_23, uca.getUserId());
                    }
                    relaBData.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                    relaBData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                    RelationTradeData delRelation=new RelationTradeData(relaBData);

                    bd.add(uca.getSerialNumber(), delRelation);
                }

        }
    }

    /**
     * 用户资费共享台账子表
     * 
     * @throws Exception
     */
    private void strUserDiscntShare(BusiTradeData bd, IData data) throws Exception
    {
        ShareTradeData share = new ShareTradeData();
        UcaData uca = bd.getRD().getUca();
        share.setShareId(data.getString("SHARE_ID"));
        share.setShareInstId(data.getString("SHARE_INST_ID"));
        share.setUserId(uca.getUserId());
        share.setDiscntCode(data.getString("DISCNT_CODE"));
        share.setDiscntCodeA(data.getString("DISCNT_CODE_A"));
        if (data.getString("MODIFY_TAG").equals("0"))
        {
            share.setStartDate(SysDateMgr.getSysTime());
            share.setRelaInstId(data.getString("INST_ID"));
            share.setModifyTag(BofConst.MODIFY_TAG_ADD);
            share.setEndDate(data.getString("END_DATE"));
        }
        else
        {
            share.setStartDate(data.getString("START_DATE"));
            share.setRelaInstId(data.getString("RELA_INST_ID"));
            share.setModifyTag(BofConst.MODIFY_TAG_DEL);
            share.setEndDate(SysDateMgr.getLastDateThisMonth());
        }
        share.setShareType("1");
        
        bd.add(uca.getSerialNumber(), share);

    }
    /**
     * 语音共享关系
     * 
     * @throws Exception
     */
    private void TradeRelationShare(BusiTradeData bd, String endDate,String shareId,String modifyFag) throws Exception
    {
    	RelationTradeData addRelation=new RelationTradeData();
    	String serialNumber = "";
		if (shareId.length() < 8) {
			serialNumber = "G4" + shareId;
		} else {
			serialNumber = "G4" + shareId.substring(shareId.length() - 8);
		}
    	UcaData uca = bd.getRD().getUca();
        addRelation.setEndDate(endDate);
        addRelation.setInstId(SeqMgr.getInstId());
        addRelation.setModifyTag(modifyFag.equals("add")?"0":"1");
        addRelation.setRelationTypeCode("G4");
        addRelation.setUserIdB(uca.getUserId());
        addRelation.setUserIdA(shareId);
        addRelation.setSerialNumberA(serialNumber);
        addRelation.setSerialNumberB(uca.getSerialNumber());
        addRelation.setRoleCodeA("0");
        addRelation.setRoleCodeB("1");// 2表示副卡
        addRelation.setOrderno("0");
        addRelation.setStartDate(bd.getRD().getAcceptTime());

        bd.add(uca.getSerialNumber(), addRelation);
    }
    
    /**
     * 用户资费共享信息台账子表
     * 
     * @throws Exception
     */
    private void strUserShareStable(BusiTradeData bd, IData data) throws Exception
    {
    	
        ShareInfoTradeData shareInfo = new ShareInfoTradeData();
        String sysdate = bd.getRD().getAcceptTime();
        UcaData uca = bd.getRD().getUca();
        long partition = Long.parseLong(uca.getUserId());
        shareInfo.setUserId(uca.getUserId());
        shareInfo.setPartitionId("" + (partition % 10000));
        shareInfo.setShareWay("2");// 以账务为准 1.赠送 2.共享
        shareInfo.setShareInstId(data.getString("SHARE_INST_ID", ""));
        shareInfo.setShareLimit("999999999999");// 现在不存在分流量共享，即全部参与共享
        shareInfo.setShareTypeCode(data.getString("SHARE_TYPE_CODE", ""));
        if (data.getString("MODIFY_TAG").equals("0"))
        {
            shareInfo.setStartDate(SysDateMgr.getSysTime());
            shareInfo.setModifyTag(BofConst.MODIFY_TAG_ADD);
//            shareInfo.setInstId(data.getString("PRIMARY_INST_ID"));
            shareInfo.setInstId(SeqMgr.getUserId());
            shareInfo.setEndDate(data.getString("END_DATE"));

        }
        else
        {
            shareInfo.setStartDate(data.getString("START_DATE"));
            shareInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
            shareInfo.setInstId(data.getString("INST_ID"));
            shareInfo.setEndDate(SysDateMgr.getLastDateThisMonth());

        }
        
        bd.add(uca.getSerialNumber(), shareInfo);

    }
    /**
	 * 插台帐用户子表拼串(TF_B_TRADE_USER)家庭群组虚拟用户资料
	 * 
	 * @author tanjl
	 * @param pd
	 * @param params
	 * @param td
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void genTradeUserInfo(BusiTradeData bd, String shareId, String endDate, String tag) throws Exception {
		String serialNumber = "";
		if (shareId.length() < 8) {
			serialNumber = "G4" + shareId;
		} else {
			serialNumber = "G4" + shareId.substring(shareId.length() - 8);
		}
		UcaData uca = bd.getRD().getUca();
		UserTradeData utd = new UserTradeData();
        utd.setCustId(shareId);
        utd.setUsecustId(shareId);
        utd.setUserTypeCode("0");
        utd.setUserStateCodeset("0");
        utd.setNetTypeCode("00");
        utd.setUserId(shareId);
        utd.setAcctTag("Z");// 不参与出账
        utd.setPrepayTag("0");// 预付费标志：0-后付费，1-预付费。（省内标准）
        utd.setMputeMonthFee("0");// 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算

        utd.setOpenDate(SysDateMgr.getSysTime());
        utd.setOpenMode("0");// // 正常开户\
        utd.setRemoveTag("0");// 正常用户
        utd.setInDate(SysDateMgr.getSysTime());
        utd.setInDepartId(CSBizBean.getVisit().getDepartId());
        utd.setInStaffId(CSBizBean.getVisit().getStaffId());
        utd.setEparchyCode(CSBizBean.getTradeEparchyCode());
        utd.setCityCode(CSBizBean.getVisit().getCityCode());
        utd.setSerialNumber(serialNumber);
        if (tag.equals("add"))
        {
        	utd.setRemoveTag("0");
        	utd.setModifyTag("0");

        }
        else
        {
        	utd.setModifyTag("1");
        	utd.setRemoveTag("2");
        	utd.setDestroyTime(SysDateMgr.getLastDateThisMonth());

        }
        bd.add(uca.getSerialNumber(), utd);
	}
	
	/**
     * 处理主卡赠送1452优惠
     * 
     * @param bd
     * @throws Exception
     */
    private void addMainDiscnt(BusiTradeData bd,String startDate, String endDate, String userId) throws Exception
    {
    	DiscntTradeData discntData = new DiscntTradeData();
    	discntData.setUserId(userId);
    	discntData.setInstId(SeqMgr.getInstId()); // 实例标识
        discntData.setModifyTag(BofConst.MODIFY_TAG_ADD); // 0-增加，1－删除，2－修改
        discntData.setStartDate(startDate);
        discntData.setEndDate(endDate);
        discntData.setUserIdA("-1");
        discntData.setPackageId("-1"); 
        discntData.setProductId("-1");
        discntData.setElementId("1452");
        discntData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
        discntData.setSpecTag("0"); // 正常产品优惠
        
        bd.add(bd.getRD().getUca().getSerialNumber(), discntData);
    }

    
    /**
     * 处理主卡终止1452优惠
     * 
     * @param bd
     * @throws Exception
     */
    private void removeMainDiscnt(BusiTradeData bd,String startDate, String endDate, String userId) throws Exception
    {
    	IDataset mainDiscntInfos = DiscntInfoQry.getDiscntNowValid(userId, "1452", null);
    	if(IDataUtil.isEmpty(mainDiscntInfos)){
    		return;
    	}else{
    		IData mainDiscntInfo = mainDiscntInfos.getData(0);
    	
	    	DiscntTradeData discntData = new DiscntTradeData(mainDiscntInfo);
	        discntData.setModifyTag(BofConst.MODIFY_TAG_DEL); // 0-增加，1－删除，2－修改
	        discntData.setEndDate(SysDateMgr.getLastDateThisMonth()); // 正常产品优惠
	        bd.add(bd.getRD().getUca().getSerialNumber(), discntData);
    	}
    }

}
