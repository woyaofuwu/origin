
package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ShareClusterFlowException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow.ShareClusterFlowQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNetNpQry;
import com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.requestdata.MemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow.order.requestdata.ShareClusterFlowReqData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class ShareSmsAction implements ITradeAction
{

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	ShareClusterFlowReqData reqData = (ShareClusterFlowReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();
        String memberCancel = reqData.getMemberCancel();
        String cancelTag=reqData.getCancelTag();
        String isExist=reqData.getIsExist();
        boolean addFlag = false;
        boolean delFlag = false;
        String mainserialNumber = "";// 主号

        IData smsData = new DataMap(); // 短信数据
        smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
        smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
        List<MemberData> mebDataList = reqData.getMemberDataList();
        String addSerialNumberBs = "";// 新增成员号码
        String delSerialNumberBs = "";// 删除成员号码
        int iCount = 0;   	//当前存在副号码的个数
    	int memberNum = 0;  //还能添加的副号码的个数
    	
        // 处理副卡发送短信
        
        if ("1".equals(memberCancel))// 副卡操作
        {
//            if (mebDataList.size()>0)
//            {
                MemberData mebData = mebDataList.get(0);
                UcaData deckUca = mebData.getUca();
                mainserialNumber=deckUca.getSerialNumber();
//            }
            IDataset qryShareRelaAll =ShareClusterFlowQry.queryMember(deckUca.getUserId());
    		//查询副号码申请加入家庭群组的预约工单信息
//    		IDataset result = TradeNetNpQry.getInNpTradeInfo(mainserialNumber,"276",deckUca.getUserEparchyCode());
    		
//            for (int i = 0, size = mebDataList.size(); i < size; i++)
//            {
//                MemberData mebData = mebDataList.get(i);
                String modifyTag = mebData.getModifyTag();
//                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
//                {
//                    StringBuilder smsContentAdd = new StringBuilder();
//                    smsContentAdd.append("您申请加入的流量共享号码"+mainserialNumber+"请求已发出，正在等待主卡确认!");
//                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
//                    smsData.put("RECV_OBJECT", serialNumber);// 接收对象
//                    smsData.put("NOTICE_CONTENT", smsContentAdd.toString());// 短信内容
//                    PerSmsAction.insTradeSMS(btd, smsData);
//                }

                if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {
                    StringBuilder smsContentDel = new StringBuilder();
                    smsContentDel.append("您好，您已退出"+mainserialNumber+"的流量共享群组，下月生效，您可发送KTQZLLGX到10086开通群组流量共享功能，您可发送SQJRQZ+群主账户手机号码到10086申请加入流量共享群组。您可以关注微信公众号“和4G-惠分享”了解流量共享套餐相关信息。中国移动");
                    delSerialNumberBs += serialNumber + ",";
                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("RECV_OBJECT", serialNumber);// 接收对象
                    smsData.put("NOTICE_CONTENT", smsContentDel.toString());// 短信内容
                    PerSmsAction.insTradeSMS(btd, smsData);
                    delFlag = true;
                }
//            }
            iCount = qryShareRelaAll.size()-1 ;//+ result.size();
        	memberNum=4-iCount;
            //主卡发送消息
            StringBuilder smsContent2 = new StringBuilder();
            if (delFlag)
            {
                smsContent2.append("您好，"+delSerialNumberBs.substring(0, delSerialNumberBs.length() - 1)+"退出了流量共享群组，下月生效，目前群组中有"+iCount+"个副账户，您还可以添加"+memberNum+"个副账户，您可发送TJFZH+手机号码到10086添加群组副账户，您可发送SCFZH+手机号码到10086删除群组副账户，添加群组副账户当月生效，删除群组副账户下月生效。您还可以关注微信公众号“和4G-惠分享”，了解相关信息和执行相关操作。中国移动");
                smsData.put("FORCE_OBJECT", "10086");// 发送对象
                smsData.put("RECV_OBJECT", mainserialNumber);// 接收对象
                smsData.put("NOTICE_CONTENT", smsContent2.toString());// 短信内容
                PerSmsAction.insTradeSMS(btd, smsData);
            }
        }else{// 主卡操作
        	IDataset qryShareRelaAll =ShareClusterFlowQry.queryMember( reqData.getUca().getUserId());
    		//查询副号码申请加入家庭群组的预约工单信息
//    		IDataset result = TradeNetNpQry.getInNpTradeInfo(mainserialNumber,"276", reqData.getUca().getUserEparchyCode());
        	iCount = qryShareRelaAll.size();// + result.size();
     		
        	 for (int i = 0, size = mebDataList.size(); i < size; i++)
             {
        		 
                 MemberData mebData = mebDataList.get(i);
                 String modifyTag = mebData.getModifyTag();
                 UcaData deckUca = mebData.getUca();
                 String serialNumberB = deckUca.getSerialNumber();
                 if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                 {
                     StringBuilder smsContentAdd = new StringBuilder();
                     smsContentAdd.append("您好，您已被"+serialNumber+"加入流量共享群组，您可发送TCQZ+主账户手机号到10086退出流量共享群组。您可发送CXLL到10086查询本账户流量消费情况，或发送CXZZHGXLL到10086查询主账户共享流量的剩余情况，主账户可对各副账户共享流量的使用明细情况进行查询您还可以关注微信公众号“和4G-惠分享”，了解群组相关信息。中国移动");
                     addSerialNumberBs += serialNumberB + ",";
                     smsData.put("FORCE_OBJECT", "10086");// 发送对象
                     smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                     smsData.put("NOTICE_CONTENT", smsContentAdd.toString());// 短信内容
                     PerSmsAction.insTradeSMS(btd, smsData);
                     addFlag = true;
                     iCount++;
                 }
                
                 if (BofConst.MODIFY_TAG_DEL.equals(modifyTag)&&cancelTag.equals("0"))
                 {
                     StringBuilder smsContentDel = new StringBuilder();
//                     String inst_id = mebData.getInstId();
//                     IDataset relaData = ShareClusterFlowQry.queryRelaForInst(inst_id);
//                     String serialNumberDel = relaData.getData(0).getString("SERIAL_NUMBER");
                     smsContentDel.append("您好，您已被");
                     smsContentDel.append(serialNumber);
                     smsContentDel.append("删除出流量共享群组，下月开始生效，您可发送SQJRQZ+群组主账户手机号码到10086申请加入群组。您可以关注微信公众号“和4G-惠分享”了解流量共享套餐相关信息。中国移动");
                     delSerialNumberBs += serialNumberB + ",";
                     smsData.put("FORCE_OBJECT", "10086");// 发送对象
                     smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                     smsData.put("NOTICE_CONTENT", smsContentDel.toString());// 短信内容
                     PerSmsAction.insTradeSMS(btd, smsData);
                     delFlag = true;
                     iCount--;
                 }else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag)&&cancelTag.equals("1"))
                 {
                     StringBuilder smsContentDel = new StringBuilder();
                     smsContentDel.append("您好，");
                     smsContentDel.append(serialNumber);
                     smsContentDel.append("取消了群组流量共享，下月生效，您可发送KTQZLLGX到10086开通群组流量共享功能，您可发送SQJRQZ +群组主账户手机号码到10086申请加入群组。您可以关注微信公众号“和4G-惠分享”，了解群组流量共享相关信息。中国移动");
                     delSerialNumberBs += serialNumberB + ",";
                     smsData.put("FORCE_OBJECT", "10086");// 发送对象
                     smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                     smsData.put("NOTICE_CONTENT", smsContentDel.toString());// 短信内容
                     PerSmsAction.insTradeSMS(btd, smsData);
//                     delFlag = true;
                 }
             }
        	 memberNum=4-iCount;
        	 //主卡发送消息
             StringBuilder smsContent1 = new StringBuilder();
             // 新加入成员短信
             if (addFlag)
             {
                 smsContent1.append("您好，您已将");
                 smsContent1.append(addSerialNumberBs.substring(0, addSerialNumberBs.length() - 1));
                 smsContent1.append("加入流量共享群组，当月生效，该群组目前群组中有"+iCount+"个副账户，您还可以添加"+memberNum+"个副账户，发送短信代码TJFZH+手机号到10086可添加群组副账户，发送SCFZH+手机号码到10086可删除群组副账户，添加群组副账户当月生效，删除群组副账户下月生效。或发送CCFZHLL到10086查询各副账户共享流量的使用明细，副账户可对主账户共享流量的剩余情况进行查询。您还可以关注微信公众号“和4G-惠分享”，了解相关信息和执行相关操作。中国移动");
                 smsData.put("FORCE_OBJECT", "10086");// 发送对象
                 smsData.put("RECV_OBJECT", serialNumber);// 接收对象
                 smsData.put("NOTICE_CONTENT", smsContent1.toString());// 短信内容
                 PerSmsAction.insTradeSMS(btd, smsData);
             }
             StringBuilder smsContent2 = new StringBuilder();
             if (delFlag)
             {
                 smsContent2.append("您好，您已将");
                 smsContent2.append(delSerialNumberBs.substring(0, delSerialNumberBs.length() - 1));
                 smsContent2.append("删除出流量共享群组，下月开始生效，目前群组中有"+iCount+"个副账户，您还可以添加"+memberNum+"个副账户，您可以发送TJFZH+手机号码到10086添加群组副账户，您可以发送SCFZH+手机号码到10086删除群组副账户，添加群组副账户当月生效，删除群组副账户下月生效。您还可以关注微信公众号“和4G-惠分享”，了解相关信息和执行相关操作。中国移动");
                 smsData.put("FORCE_OBJECT", "10086");// 发送对象
                 smsData.put("RECV_OBJECT", serialNumber);// 接收对象
                 smsData.put("NOTICE_CONTENT", smsContent2.toString());// 短信内容
                 PerSmsAction.insTradeSMS(btd, smsData);
             }
        }
       
        
        // 处理主卡发送短信
        StringBuilder smsContent3 = new StringBuilder();
        if(cancelTag.equals("1")){
            smsContent3.append("您好，您已取消群组流量共享功能，下月生效，您可发送SQJRQZ+群组主账户手机号码到10086申请加入群组。您可以关注微信公众号“和4G-惠分享”，了解群组流量共享相关信息。中国移动");
            smsData.put("FORCE_OBJECT", "10086");// 发送对象
            smsData.put("RECV_OBJECT", serialNumber);// 接收对象
            smsData.put("NOTICE_CONTENT", smsContent3.toString());// 短信内容
            PerSmsAction.insTradeSMS(btd, smsData);
        }
        StringBuilder smsContent4 = new StringBuilder();
        if(isExist.equals("0")){
            smsContent4.append("您好！您已开通群组流量共享功能，当月生效。您可发送TJFZH+手机号码到10086添加群组副账户，当月生效，您最多可添加4个副账户，您可发送QXQZLLGX到10086取消群组流量共享功能。您还可以关注微信公众号“和4G-惠分享”，了解相关信息和执行相关操作。中国移动");
            smsData.put("FORCE_OBJECT", "10086");// 发送对象
            smsData.put("RECV_OBJECT", serialNumber);// 接收对象
            smsData.put("NOTICE_CONTENT", smsContent4.toString());// 短信内容
            PerSmsAction.insTradeSMS(btd, smsData);
        }

    }
}
