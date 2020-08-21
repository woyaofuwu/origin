
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.action;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata.MemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata.ShareMealReqData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class ShareSmsAction implements ITradeAction
{

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ShareMealReqData reqData = (ShareMealReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();// 主号
        String custName = reqData.getUca().getCustomer().getCustName();
        String memberCancel = reqData.getMemberCancel();
        String userId = reqData.getUca().getUserId();
        boolean addFlag = false;
        boolean delFlag = false;

        IData smsData = new DataMap(); // 短信数据
        smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
        smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
        List<MemberData> mebDataList = reqData.getMemberDataList();
        String addSerialNumberBs = "";// 新增成员号码
        String delSerialNumberBs = "";// 删除成员号码
        // 因为是副卡操作，所以需要将主卡处理
        if ("1".equals(memberCancel))
        {
            String shareId = ShareInfoQry.queryMemberRela(userId, "02").getData(0).getString("SHARE_ID");
            IDataset mainDat = ShareInfoQry.queryRelaByShareIdAndRoleCode(shareId, "01");
            serialNumber = mainDat.getData(0).getString("SERIAL_NUMBER");
            IDataset custInfo = CustomerInfoQry.queryCustInfoBySN(serialNumber);
            custName = custInfo.getData(0).getString("CUST_NAME");

        }
       //start-20190131-wangsc 查询主号userid
       String userIdA="";
 	   IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber); 
 	   if(IDataUtil.isNotEmpty(userInfoData)){
 		   userIdA =  userInfoData.getString("USER_ID","").trim();
 	   }
 	   // 查询用户下预约生效的可以共享的4g资费。20190131-wangsc10
       IDataset discntInfo = ShareInfoQry.queryDiscntsNEW(userIdA);
       String startdate = "24小时内";
       String deldate = "下月1日";
       if(IDataUtil.isNotEmpty(discntInfo) && null != discntInfo && discntInfo.size() > 0){
    	   startdate = SysDateMgr.getChinaDate(discntInfo.getData(0).getString("DISCNT_START_DATE"),SysDateMgr.PATTERN_CHINA_DATE);
    	   deldate = "立即";
       }
        // 处理副卡发送短信
        for (int i = 0, size = mebDataList.size(); i < size; i++)
        {
            MemberData mebData = mebDataList.get(i);
            String modifyTag = mebData.getModifyTag();
            UcaData deckUca = mebData.getUca();
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                StringBuilder smsContentAdd = new StringBuilder();
                String serialNumberB = deckUca.getSerialNumber();
                smsContentAdd.append("共享提醒：您好！");
                smsContentAdd.append(serialNumber);
                smsContentAdd.append("客户邀请您使用共享套餐内的流量等业务，"+startdate+"生效。生效后，您可发送CXGXYW至10086查询共享套餐的使用情况。中国移动");
                addSerialNumberBs += serialNumberB + ",";
                smsData.put("FORCE_OBJECT", "10086");// 发送对象
                smsData.put("RECV_OBJECT", serialNumberB);// 接收对象
                smsData.put("NOTICE_CONTENT", smsContentAdd.toString());// 短信内容
                PerSmsAction.insTradeSMS(btd, smsData);
                addFlag = true;
            }

            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                StringBuilder smsContentDel = new StringBuilder();
                String inst_id = mebData.getInstId();
                IDataset relaData = ShareInfoQry.queryRelaForInst(inst_id);
                String serialNumberDel = relaData.getData(0).getString("SERIAL_NUMBER");
                //smsContentDel.append("您已退出（");
                //smsContentDel.append(serialNumber);
                //smsContentDel.append("）客户号码的数据流量共享，从下一个账期起将不再享受该优惠。");
                
                smsContentDel.append("共享提醒：您好！ ");
                smsContentDel.append(serialNumber);
                smsContentDel.append("客户取消共享功能，"+deldate+"生效。生效后，您将不能使用该客户共享套餐内的流量等业务。中国移动");
                
                delSerialNumberBs += serialNumberDel + ",";
                smsData.put("FORCE_OBJECT", "10086");// 发送对象
                smsData.put("RECV_OBJECT", serialNumberDel);// 接收对象
                smsData.put("NOTICE_CONTENT", smsContentDel.toString());// 短信内容
                PerSmsAction.insTradeSMS(btd, smsData);
                delFlag = true;
            }
        }
        // 处理主卡发送短信

        StringBuilder smsContent1 = new StringBuilder();
        // 新加入成员短信
        if (addFlag)
        {
            smsContent1.append("共享提醒：您好！您已成功邀请");
            smsContent1.append(addSerialNumberBs.substring(0, addSerialNumberBs.length() - 1));
            smsContent1.append("客户使用共享套餐内的流量等业务，"+startdate+"生效。生效后，将向您一次性收取月功能费10元（指定套餐的部分副卡免功能费）。您可发送CXGXYW至10086查询共享套餐的使用情况。中国移动");
            smsData.put("FORCE_OBJECT", "10086");// 发送对象
            smsData.put("RECV_OBJECT", serialNumber);// 接收对象
            smsData.put("NOTICE_CONTENT", smsContent1.toString());// 短信内容
            PerSmsAction.insTradeSMS(btd, smsData);
        }
        StringBuilder smsContent2 = new StringBuilder();
        if (delFlag)
        {
        	smsContent2.append("共享提醒：您好！ 您已成功取消共享功能，"+deldate+"生效。生效后，");
            //smsContent2.append("尊敬的");
            //smsContent2.append(custName);
            //smsContent2.append("客户，成员");
            smsContent2.append(delSerialNumberBs.substring(0, delSerialNumberBs.length() - 1));
            //smsContent2.append("已经退出您的数据流量共享，流量共享功能有效期至本账期末。");
            smsContent2.append("客户将不能使用您共享套餐内的流量等业务。中国移动");
            smsData.put("FORCE_OBJECT", "10086");// 发送对象
            smsData.put("RECV_OBJECT", serialNumber);// 接收对象
            smsData.put("NOTICE_CONTENT", smsContent2.toString());// 短信内容
            PerSmsAction.insTradeSMS(btd, smsData);
        }

    }
}
