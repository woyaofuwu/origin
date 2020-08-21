
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareInfoTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareRelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow.ShareClusterFlowQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: GprsDiscntShareAction.java
 * @Description: 有关流量共享处理
 * @version: v1.0.0
 * @author: sunxin
 */
public class GprsDiscntShareAction implements ITradeAction
{

    /**
     * 用户资费共享台账子表
     * 
     * @throws Exception
     */
    private void dealShareRela(BusiTradeData bd, IData data) throws Exception
    {
        ShareRelaTradeData delRela = new ShareRelaTradeData(data);
        delRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
        //delRela.setEndDate(SysDateMgr.getLastDateThisMonth());
        bd.add(bd.getRD().getUca().getSerialNumber(), delRela);
    }

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	
    	List<ProductTradeData> lsProductTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
		if(CollectionUtils.isNotEmpty(lsProductTrades))
		{
			if (lsProductTrades != null && lsProductTrades.size() > 0)
            {
                for (ProductTradeData lsProductTrade : lsProductTrades)
                {
                	if("80003014".equals(lsProductTrade.getProductId()) && "1".equals(lsProductTrade.getMainTag()) && "1".equals(lsProductTrade.getModifyTag()))
                	{
                		return;
                	}
                }
            }
		}
		
		
        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        String userId = btd.getRD().getUca().getUserId();
        IDataset returnData = ShareInfoQry.queryMemberRela(userId, "01");// 查询主卡是否存在共享关系
        //IDataset Share = ShareInfoQry.getAllShareDiscnt();// 查询所有可用共享优惠
        IDataset infos = new DatasetList();
        IDataset shareds = new DatasetList();
        //String discntCode = "";
        int count = 0;
        int num = 0;
        boolean compareBool = true;  //开关
        String strEtime = "";
        // 有共享关系才进行下列操作
        if (IDataUtil.isNotEmpty(returnData))
        {
            IData mainShare = returnData.getData(0);
            String shareId = mainShare.getString("SHARE_ID");
            
            //判断共享类型 ---newbilling add by xiaobin
            boolean is4GShare = false;
            String discnt_a = "1450";
            IDataset userinfo = UserInfoQry.getUserInfoByUserCodeSet(shareId, "0");
            if(IDataUtil.isNotEmpty(userinfo)){
            	is4GShare = true;	//有虚拟用户为4G共享
            	discnt_a = "1451";
            }
            
            if (discntTrades != null && discntTrades.size() > 0)
            {
                for (DiscntTradeData discntTrade : discntTrades)
                {
                    String elementId = discntTrade.getElementId();
                    // 如果新增共享流量优惠，侧需要同步给帐务
                    if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag()))
                    {
                    	if( compareBool ){
                    		IDataset compare4455 = CommparaInfoQry.getCommparaInfoBy5("CSM", "4455", "SHARE", elementId, "0898", null);
                    		if( compare4455.size() > 0 ){
                    			compareBool = false;	//只要有一个符合，不在做判断！
                    		}
                    	}
                    	if( !compareBool ){
                    		
                    		IDataset Share4455 = ShareInfoQry.getShareByDiscnt(elementId);
                    		if( Share4455.size() > 0 ){
                    			
                    			String strED = discntTrade.getEndDate();
                    			String mainShareED = mainShare.getString("END_DATE");
                    			String strParamED = strED;
                        		if( strED.compareTo(mainShareED) > 0 ){
                        			strParamED = mainShareED;
                        		}
                    			
                    			IData SD = Share4455.getData(0);
                    			String strDca = SD.getString("DISCNT_CODE_A");
                    			String strIt = SD.getString("ITEM_TYPE");
                    			IData param = new DataMap();
                                param.put("SHARE_ID", shareId);
                                param.put("SHARE_INST_ID", SeqMgr.getUserId());
                                param.put("DISCNT_CODE", elementId);
                                param.put("PRIMARY_INST_ID", SeqMgr.getUserId());
                                param.put("INST_ID", discntTrade.getInstId());
                                param.put("DISCNT_CODE_A", discnt_a);
                                param.put("SHARE_TYPE_CODE", strIt);// 取资费参数表里的item_type
                                param.put("START_DATE", discntTrade.getStartDate());
                                param.put("END_DATE", strParamED);//mainShare.getString("END_DATE"),discntTrade.getEndDate()
                                param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                                this.strUserDiscntShare(btd, param);
                                
                                //循环处理SHARE_TYPE_CODE --new billing
                                IDataset shareTypecodes = ShareClusterFlowQry.queryResId(elementId);
                                if(IDataUtil.isNotEmpty(shareTypecodes))
                                {
                                	boolean bShare = true;
                                	for(int j=0, s=shareTypecodes.size(); j<s; j++){
                                		String strResType = shareTypecodes.getData(j).getString("RES_TYPE");
                                		IDataset compare3999 = CommparaInfoQry.getCommparaInfoByCode("CSM", "3999", elementId, strResType, "0898");
                                		if(IDataUtil.isNotEmpty(compare3999))
                                        {
                                			bShare = false;
                                			param.put("SHARE_TYPE_CODE", shareTypecodes.getData(j).getString("RES_ID"));
                                        	this.strUserShareStable(btd, param);
                                        }
                                    }
                                	
                                	if(bShare)
                                	{
                                		CSAppException.apperr(CrmCommException.CRM_COMM_889, elementId);
                                	}
                                }
                                else
                                {
                                	CSAppException.apperr(CrmCommException.CRM_COMM_889, elementId);
                                }
                                count--;
                    		}
                    	}
                    }
                    // 如果删除共享流量优惠，侧需要同步给帐务，如果将所有流量共享优惠删除，侧需要同时处理关系
                    if (BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag()))
                    {
                        shareds = ShareInfoQry.queryAllShare(shareId, userId);// 查询已存在所有share信息
                        infos = ShareInfoQry.queryAllShareInfo(userId);// 查询已存在所有shareinfo信息
                        if (IDataUtil.isNotEmpty(shareds))
                        {
                            num = shareds.size();
                            for (int j = 0; j < num; j++)
                            {
                                IData share = shareds.getData(j);
                                String shareInstId = "";
                                if (share.getString("DISCNT_CODE").equals(elementId))
                                {
                                	if( !"".equals(strEtime) ){
                                		String strED = discntTrade.getEndDate();
                                		if( strED.compareTo(strEtime) > 0 ){
                                			strEtime = strED;
                                		}
                                	}else{
                                		strEtime = discntTrade.getEndDate();
                                	}
                                    shareInstId = share.getString("SHARE_INST_ID");
                                    share.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                                    share.put("END_DATE", discntTrade.getEndDate());
                                    strUserDiscntShare(btd, share);
                                    count++;
                                }
                                // 根据SHARE_INST_ID删除对应的shareinfo记录
                                if (IDataUtil.isNotEmpty(infos))
                                {
                                    for (int n = 0; n < infos.size(); n++)
                                    {
                                        IData shareInfo = infos.getData(n);
                                        if (shareInstId.equals(shareInfo.getString("SHARE_INST_ID")))
                                        {
                                            shareInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                                            shareInfo.put("END_DATE", discntTrade.getEndDate());
                                            strUserShareStable(btd, shareInfo);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }
            // 处理所有资费删除后的关系解除
            if (num == count && num != 0)
            {
            	String mainSn = mainShare.getString("SERIAL_NUMBER");
            	IData smsData = new DataMap(); // 短信数据
                smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
                smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
                IDataset returnDataMember = ShareInfoQry.queryMember(userId);
                // 处理副卡
                for (int i = 0; i < returnDataMember.size(); i++)
                {
                    IData relaInfo = returnDataMember.getData(i);
                    relaInfo.put("END_DATE", strEtime);
                    String sn = relaInfo.getString("SERIAL_NUMBER");
                    dealShareRela(btd, relaInfo);
                    //流量提醒：您好！ **客户取消共享流量，XXXX年XX月XX日生效。生效后，您将不能使用该客户共享的免费流量。中国移动！
                    //String strSms = "流量提醒：您好！ " + mainSn + "客户取消共享流量，" + strEtime + "生效。生效后，您将不能使用该客户共享的免费流量。中国移动！";
                    String strSms = "共享提醒：您好！ "+ mainSn +"客户取消共享功能，下月1日生效。生效后，您将不能使用该客户共享套餐内的指定业务资源。中国移动";
                    
                    smsData.put("FORCE_OBJECT", "10086");// 发送对象
                    smsData.put("RECV_OBJECT", sn);// 接收对象
                    smsData.put("NOTICE_CONTENT", strSms);// 短信内容
                    PerSmsAction.insTradeSMS(btd, smsData);
                }
                // 处理主卡
                mainShare.put("END_DATE", strEtime);
                dealShareRela(btd, mainShare);
                
                if(is4GShare){//4G共享需要取消赠送的1452优惠
                	dealGiftDiscnt(btd);
                }
                
                //流量提醒：您好！ 您已成功取消共享流量，XXXX年XX月XX日生效。生效后，副号码将不能使用您共享的免费流量。中国移动！
                //String strSms = "流量提醒：您好！ 您已成功取消共享流量，" + strEtime + "生效。生效后，副号码将不能使用您共享的免费流量。中国移动！";
                String strSms = "共享提醒：您好！ 您已成功取消共享功能，下月1日生效。生效后，副卡客户将不能使用您共享套餐内的指定业务资源。中国移动";
                smsData.put("FORCE_OBJECT", "10086");// 发送对象
                smsData.put("RECV_OBJECT", mainSn);// 接收对象
                smsData.put("NOTICE_CONTENT", strSms);// 短信内容
                PerSmsAction.insTradeSMS(btd, smsData);
                
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
            share.setStartDate(data.getString("START_DATE"));//SysDateMgr.getSysTime()
            share.setRelaInstId(data.getString("INST_ID"));
            share.setModifyTag(BofConst.MODIFY_TAG_ADD);
            share.setEndDate(data.getString("END_DATE"));
        }
        else
        {
            share.setStartDate(data.getString("START_DATE"));
            share.setRelaInstId(data.getString("RELA_INST_ID"));
            share.setModifyTag(BofConst.MODIFY_TAG_DEL);
            share.setEndDate(data.getString("END_DATE"));//SysDateMgr.getLastDateThisMonth()
        }
        share.setShareType("1");

        bd.add(uca.getSerialNumber(), share);

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
            shareInfo.setStartDate(data.getString("START_DATE"));//SysDateMgr.getSysTime()
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
            shareInfo.setEndDate(data.getString("END_DATE"));//SysDateMgr.getLastDateThisMonth()

        }

        bd.add(uca.getSerialNumber(), shareInfo);

    }
    
    private void dealGiftDiscnt(BusiTradeData bd) throws Exception
    {
        IDataset mainDiscntInfos = DiscntInfoQry.getDiscntNowValid(bd.getRD().getUca().getUserId(), "1452", null);
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
