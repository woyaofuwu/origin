
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ShareMealException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareInfoTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareRelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow.ShareClusterFlowQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata.MemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.requestdata.ShareMealReqData;

public class ShareMealTrade extends BaseTrade implements ITrade
{

    // @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ShareMealReqData reqData = (ShareMealReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String userId = uca.getUserId();
        String memberCancel = reqData.getMemberCancel();
        boolean firstTag = false;
        String shareId = "";
        String endDate = "";
        String disendDate = "";
        boolean syncFlag = false;
        boolean dealMain = false;
        IDataset sync = TagInfoQry.getTagInfo("ZZZZ", "SYNC_SHARE_TOACCT", "0");
        if (IDataUtil.isNotEmpty(sync))
        {
        	syncFlag = true;
        }
        
        int nMemberAddCount = 0;
        IData idPageRQ = bd.getRD().getPageRequestData();
        if(IDataUtil.isNotEmpty(idPageRQ))
        {
        	String strMemberAddCount = idPageRQ.getString("MemberAddCount", "0");
        	nMemberAddCount = Integer.parseInt(strMemberAddCount);
        }
            
        if ("0".equals(memberCancel))
        { // 判断主卡是否已经存在
            IDataset mainList = ShareInfoQry.queryMemberRela(userId, "01");
            if (IDataUtil.isEmpty(mainList))
            {
                firstTag = true;
                shareId = SeqMgr.getUserId();// 如果是新增关系，则shareid为新增序列
            }
            else
            {
                shareId = mainList.getData(0).getString("SHARE_ID");
                disendDate = mainList.getData(0).getString("END_DATE");
            }
            // 结束时间获取。取资费的最大结束时间。或者主卡的结束时间。
            IDataset discnt = ShareInfoQry.queryDiscnts(userId);
            if (IDataUtil.isNotEmpty(discnt))
            {
                endDate = discnt.getData(0).getString("END_DATE", "");
                if( "".equals(disendDate) )
                {
                	disendDate = endDate;
                }
            }

            if (endDate.trim().equals(""))
            {
                endDate = SysDateMgr.END_DATE_FOREVER;
                if( "".equals(disendDate) )
                {
                	disendDate = endDate;
                }
            }
            
            if(nMemberAddCount > 0 && !firstTag) 
            {
            	if("".equals(disendDate)){ //相关关系截止到月底时，当月又增加成员时，还是以截止到月底的时间做为新增的成员的结束时间
            		disendDate = SysDateMgr.getTheLastTime();
            	}
            }
            //BUG20190610103137多终端共享副号新增问题,去掉这段，因为相关关系截止到月底时，当月又增加成员时主卡的截止时间变了2050，而副卡如果是月底的话，等于下个月只剩主号自己了，不符合业务逻辑。
            //dealMain = dealMemBerRela(bd, shareId, disendDate, userId);
            dealMain = dealMemBerRela(bd, shareId, endDate, userId);
            // 首次新增or 关系全部解除 处理主卡与同步
            if (firstTag)
            {
            	dealMainrRela(bd, shareId, endDate, syncFlag, "add", userId);
            }
                
            if (dealMain)
            {
            	dealMainrRela(bd, shareId, endDate, syncFlag, "del", userId);
            }
            else if(!dealMain && nMemberAddCount > 0 && !firstTag)
            {
            	dealMainrRela(bd, shareId, endDate, syncFlag, "mod", userId);
            }

        }
        else
        {
            shareId = ShareInfoQry.queryMemberRela(userId, "02").getData(0).getString("SHARE_ID");

            String user_id = "";
            IDataset mainDat = ShareInfoQry.queryRelaByShareIdAndRoleCode(shareId, "01");

            if (IDataUtil.isEmpty(mainDat))
            {
            	CSAppException.apperr(ShareMealException.CRM_SHARE_13);
            }
                
            else
            {
            	user_id = mainDat.getData(0).getString("USER_ID_B");// 传递主卡的userId
            }  
            dealMain = dealMemBerRela(bd, shareId, endDate, user_id);
            if (dealMain)
            {
            	dealMainrRela(bd, shareId, endDate, syncFlag, "del", user_id);
            }
               
        }

    }

    // 首次新增 处理主卡与同步
    private void dealMainrRela(BusiTradeData bd, String shareId, String endDate, boolean syncFlag, String tag, String userId) throws Exception
    {
        UcaData uca = bd.getRD().getUca();
        String sysdate = bd.getRD().getAcceptTime();

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
            //addRela.setEndDate(SysDateMgr.END_DATE_FOREVER);
            addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
            
            bd.add(uca.getSerialNumber(), addRela);
        }
        else if ("del".equals(tag))
        {
            // 处理主卡关系删除
        	IDataset idsMain = ShareInfoQry.queryMemberRela(userId, "01");
        	if(IDataUtil.isNotEmpty(idsMain))
        	{
        		IData returnDataMain = idsMain.getData(0);
                ShareRelaTradeData addRela = new ShareRelaTradeData(returnDataMain);
                addRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
                addRela.setEndDate(SysDateMgr.getLastDateThisMonth());
                addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
                bd.add(uca.getSerialNumber(), addRela);
        	}
        }
        else if("mod".equals(tag))
        {
        	// 处理主卡关系
        	IDataset idsMain = ShareInfoQry.queryMemberRela(userId, "01");
        	if(IDataUtil.isNotEmpty(idsMain))
        	{
        		IData returnDataMain = idsMain.getData(0);
                ShareRelaTradeData addRela = new ShareRelaTradeData(returnDataMain);
                addRela.setModifyTag(BofConst.MODIFY_TAG_UPD);
                addRela.setEndDate(endDate);
                addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
                addRela.setRemark("主卡二次新增副卡，主卡关系延长与优惠一致");
                
                bd.add(uca.getSerialNumber(), addRela);
        	}
            
        }
        // 处理同步

        // 查询用户下所有可以共享4g资费
        IDataset discntInfo = ShareInfoQry.queryDiscnts(userId);
        if (syncFlag)
        {
            if ("add".equals(tag))
            {
                for (int i = 0; i < discntInfo.size(); i++)
                {
                    IData discnts = discntInfo.getData(i);
                    IData param = new DataMap();
                    param.put("SHARE_ID", shareId);
                    param.put("SHARE_INST_ID", SeqMgr.getUserId());
                    param.put("DISCNT_CODE", discnts.getString("DISCNT_CODE"));
                    param.put("PRIMARY_INST_ID", SeqMgr.getUserId());// info
                    param.put("INST_ID", discnts.getString("INST_ID"));// share
//                    param.put("DISCNT_CODE_A", discnts.getString("DISCNT_CODE_A"));// share
//                    param.put("SHARE_TYPE_CODE", discnts.getString("ITEM_TYPE"));// 取资费参数表里的item_type
                    param.put("DISCNT_CODE_A", "1450");//newbilling改造
                    param.put("START_DATE", discnts.getString("DISCNT_START_DATE"));
                    param.put("END_DATE", discnts.getString("DISCNT_END_DATE")); //共享的套餐信息以优惠的结束时间为准，不然套餐截止了，共享信息还存在。
                    param.put("MODIFY_TAG", "0");
                    strUserDiscntShare(bd, param);
                    //循环处理SHARE_TYPE_CODE --new billing
                    String strDiscntCode = discnts.getString("DISCNT_CODE");
                    IDataset shareTypecodes = ShareClusterFlowQry.queryResId(strDiscntCode);
                    if(IDataUtil.isNotEmpty(shareTypecodes))
                    {
                    	boolean bShare = true;
                    	for(int j=0, s=shareTypecodes.size(); j<s; j++){
                    		String strResType = shareTypecodes.getData(j).getString("RES_TYPE");
                    		IDataset compare3999 = CommparaInfoQry.getCommparaInfoByCode("CSM", "3999", strDiscntCode, strResType, "0898");
                    		if(IDataUtil.isNotEmpty(compare3999))
                            {
                    			bShare = false;
                    			param.put("SHARE_TYPE_CODE", shareTypecodes.getData(j).getString("RES_ID"));
                                strUserShareStable(bd, param);
                            }
                        }
                    	
                    	if(bShare)
                    	{
                    		CSAppException.apperr(CrmCommException.CRM_COMM_889, strDiscntCode);
                    	}
                    	
                    }
                    else
                    {
                    	CSAppException.apperr(CrmCommException.CRM_COMM_889, strDiscntCode);
                    }
//                    strUserShareStable(bd, param);
                }
            }
            else if ("del".equals(tag))
            {
                IDataset shareds = ShareInfoQry.queryAllShare(shareId, userId);
                if (IDataUtil.isNotEmpty(shareds))
                {
                    for (int i = 0; i < shareds.size(); i++)
                    {
                        IData share = shareds.getData(i);
                        share.put("MODIFY_TAG", "1");
                        strUserDiscntShare(bd, share);
                    }
                }
                IDataset infos = ShareInfoQry.queryAllShareInfo(userId);
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
            else if ("mod".equals(tag))
            {
            	IDataset shareds = ShareInfoQry.queryAllShareA(shareId, userId);
                if (IDataUtil.isNotEmpty(shareds))
                {           	
                	for(int k = 0; k < discntInfo.size(); k++)
                	{
                		IData allshare = discntInfo.getData(k);
                		String discode = allshare.getString("DISCNT_CODE");
                		boolean dflag = true;
                		for (int i = 0; i < shareds.size(); i++)
                        {
                            IData share = shareds.getData(i);
                            String dcode = share.getString("DISCNT_CODE");
                            if(!discode.equals(dcode))
                            {
                            	continue;
                            }else{
                            	dflag = false;
                            }
                            String strShareInstIdA = share.getString("SHARE_INST_ID");
                            share.put("END_DATE", allshare.getString("DISCNT_END_DATE"));
                            share.put("MODIFY_TAG", "2");
                            strUserDiscntShare(bd, share);
                           
                            IDataset infos = ShareInfoQry.queryAllShareInfoA(userId);
                            if (IDataUtil.isNotEmpty(infos))
                            {
                                for (int j = 0; j < infos.size(); j++)
                                {
                                	IData shareInfo = infos.getData(j);
                                	String strShareInstIdB = shareInfo.getString("SHARE_INST_ID", "");
                                	if(strShareInstIdA.equals(strShareInstIdB))
                                	{
                                        shareInfo.put("END_DATE", allshare.getString("DISCNT_END_DATE"));
                                        shareInfo.put("MODIFY_TAG", "2");
                                        strUserShareStable(bd, shareInfo);
                                	}
                                    
                                }
                            } 
                        }
                		if(dflag)
                		{
                			IData discnts = discntInfo.getData(k);
                            IData param = new DataMap();
                            param.put("SHARE_ID", shareId);
                            param.put("SHARE_INST_ID", SeqMgr.getUserId());
                            param.put("DISCNT_CODE", discnts.getString("DISCNT_CODE"));
                            param.put("PRIMARY_INST_ID", SeqMgr.getUserId());// info
                            param.put("INST_ID", discnts.getString("INST_ID"));// share
//                            param.put("DISCNT_CODE_A", discnts.getString("DISCNT_CODE_A"));// share
//                            param.put("SHARE_TYPE_CODE", discnts.getString("ITEM_TYPE"));// 取资费参数表里的item_type
                            param.put("DISCNT_CODE_A", "1450");//newbilling改造
                            param.put("START_DATE", discnts.getString("DISCNT_START_DATE"));
                            param.put("END_DATE", discnts.getString("DISCNT_END_DATE")); //共享的套餐信息以优惠的结束时间为准，不然套餐截止了，共享信息还存在。
                            param.put("MODIFY_TAG", "0");
                            strUserDiscntShare(bd, param);
                            //循环处理SHARE_TYPE_CODE --new billing
                            String strDiscntCode = discnts.getString("DISCNT_CODE");
                            IDataset shareTypecodes = ShareClusterFlowQry.queryResId(strDiscntCode);
                            if(IDataUtil.isNotEmpty(shareTypecodes))
                            {
                            	boolean bShare = true;
                            	for(int j=0, s=shareTypecodes.size(); j<s; j++){
                            		String strResType = shareTypecodes.getData(j).getString("RES_TYPE");
                            		IDataset compare3999 = CommparaInfoQry.getCommparaInfoByCode("CSM", "3999", strDiscntCode, strResType, "0898");
                            		if(IDataUtil.isNotEmpty(compare3999))
                                    {
                            			bShare = false;
                            			param.put("SHARE_TYPE_CODE", shareTypecodes.getData(j).getString("RES_ID"));
                                        strUserShareStable(bd, param);
                                    }
                                }
                            	
                            	if(bShare)
                            	{
                            		CSAppException.apperr(CrmCommException.CRM_COMM_889, strDiscntCode);
                            	}
                            	
                            }
                            else
                            {
                            	CSAppException.apperr(CrmCommException.CRM_COMM_889, strDiscntCode);
                            }
                		}
                	}  
                }
            }
            
        }

    }

    /**
     * 处理副卡
     * 
     * @param bd
     * @throws Exception
     */
    private boolean dealMemBerRela(BusiTradeData bd, String shareId, String endDate, String userId) throws Exception
    {
        ShareMealReqData reqData = (ShareMealReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();
        List<MemberData> mebDataList = reqData.getMemberDataList();
        boolean delFlag = false;// 删除整个关系标记

        IDataset mebList = ShareInfoQry.queryMember(userId);
        int currMebCount = 0;
        int deled = 0;
        if (mebList.size() > 0)
        {
            String date = SysDateMgr.getLastDateThisMonth();
            for (int i = 0; i < mebList.size(); i++)
            {
                IData map = mebList.getData(i);
                String enddate = map.getString("END_DATE");
                if (date.equals(enddate))
                    deled++;
            }
            currMebCount = mebList.size() - deled;// 这个主要用于判断是否副卡全部删除了
        }
        for (int i = 0, size = mebDataList.size(); i < size; i++)
        {
            MemberData mebData = mebDataList.get(i);
            String modifyTag = mebData.getModifyTag();
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                UcaData deckUca = mebData.getUca();
                ShareRelaTradeData addRela = new ShareRelaTradeData();
                addRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addRela.setShareId(shareId);
                addRela.setInstId(SeqMgr.getInstId());
                addRela.setSerialNumber(deckUca.getSerialNumber());
                addRela.setUserIdB(deckUca.getUserId());
                addRela.setRoleCode("02");// 副卡
                addRela.setStartDate(sysdate);
                addRela.setEndDate(endDate);
                //addRela.setEndDate(SysDateMgr.END_DATE_FOREVER);
                addRela.setEparchyCode(CSBizBean.getUserEparchyCode());
                if(StringUtils.isNotBlank(bd.getMainTradeData().getRemark()) && bd.getMainTradeData().getRemark().contains("158不限量套餐连带办理"))
                {
                	addRela.setRsrvTag1("1");
                }
                bd.add(uca.getSerialNumber(), addRela);

                currMebCount++;
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {

                String inst_id = mebData.getInstId();
                IDataset relaData = ShareInfoQry.queryRelaForInst(inst_id);
                if (IDataUtil.isEmpty(relaData))
                {
                    CSAppException.apperr(ShareMealException.CRM_SHARE_11, inst_id);
                }
                
              //当副号码的主号码是不限量158套餐有特殊标示RSRV_TAG1为1，则不允许解除共享关系
                
                this.delCheckTradeUserProductId(userId,relaData.getData(0).getString("RSRV_TAG1",""),bd);
                
                ShareRelaTradeData delRela = new ShareRelaTradeData(relaData.getData(0));
                delRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
                delRela.setEndDate(SysDateMgr.getLastDateThisMonth());
                                
                bd.add(uca.getSerialNumber(), delRela);

                currMebCount--;
            }

        }

        // 如果是全部删除
        if (currMebCount == 0)
        {
            delFlag = true;
        }

        return delFlag;
    }
    
    
    //当副号码的主号码是不限量158套餐有特殊标示RSRV_TAG1为1，则不允许解除共享关系 add by xuyt
    private void delCheckTradeUserProductId(String userId,String rsrvTag1, BusiTradeData bd) throws Exception
    {
    	//流量不限量158 元套餐 80003014
    	IDataset userP = UserProductInfoQry.getUserProductByUserIdProductId(userId, "80003014");
    	if(IDataUtil.isNotEmpty(userP) && "1".equals(rsrvTag1))
    	{
    		if(StringUtils.isNotBlank(bd.getMainTradeData().getRemark()) && !bd.getMainTradeData().getRemark().contains("158不限量套餐连带删除"))
            {
    			CSAppException.appError("68800936", "副号码的主号码是不限量158套餐，不允许解除共享关系！");
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
    	String strModify_tag = data.getString("MODIFY_TAG");
        ShareTradeData share = new ShareTradeData();
        UcaData uca = bd.getRD().getUca();
        share.setShareId(data.getString("SHARE_ID"));
        share.setShareInstId(data.getString("SHARE_INST_ID"));
        share.setUserId(uca.getUserId());
        share.setDiscntCode(data.getString("DISCNT_CODE"));
        share.setDiscntCodeA(data.getString("DISCNT_CODE_A"));
        if ("0".equals(strModify_tag))
        {
            share.setStartDate(data.getString("START_DATE"));//SysDateMgr.getSysTime()
            share.setRelaInstId(data.getString("INST_ID"));
            share.setModifyTag(BofConst.MODIFY_TAG_ADD);
            share.setEndDate(data.getString("END_DATE"));
        }
        else if("1".equals(strModify_tag))
        {
            share.setStartDate(data.getString("START_DATE"));
            share.setRelaInstId(data.getString("RELA_INST_ID"));
            share.setModifyTag(BofConst.MODIFY_TAG_DEL);
            share.setEndDate(SysDateMgr.getLastDateThisMonth());
        }
        else if("2".equals(strModify_tag))
        {
        	share.setStartDate(data.getString("START_DATE"));
            share.setRelaInstId(data.getString("RELA_INST_ID"));
            share.setModifyTag(BofConst.MODIFY_TAG_UPD);
            share.setEndDate(data.getString("END_DATE"));
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
    	String strModify_tag = data.getString("MODIFY_TAG");
        ShareInfoTradeData shareInfo = new ShareInfoTradeData();
        //String sysdate = bd.getRD().getAcceptTime();
        UcaData uca = bd.getRD().getUca();
        long partition = Long.parseLong(uca.getUserId());
        shareInfo.setUserId(uca.getUserId());
        shareInfo.setPartitionId("" + (partition % 10000));
        shareInfo.setShareWay("2");// 以账务为准 1.赠送 2.共享
        shareInfo.setShareInstId(data.getString("SHARE_INST_ID", ""));
        shareInfo.setShareLimit("999999999999");// 现在不存在分流量共享，即全部参与共享
        shareInfo.setShareTypeCode(data.getString("SHARE_TYPE_CODE", ""));
        if ("0".equals(strModify_tag))
        {
            shareInfo.setStartDate(data.getString("START_DATE"));//SysDateMgr.getSysTime()
            shareInfo.setModifyTag(BofConst.MODIFY_TAG_ADD);
            shareInfo.setInstId(SeqMgr.getInstId());//data.getString("PRIMARY_INST_ID")
            shareInfo.setEndDate(data.getString("END_DATE"));

        }
        else if("1".equals(strModify_tag))
        {
            shareInfo.setStartDate(data.getString("START_DATE"));
            shareInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
            shareInfo.setInstId(data.getString("INST_ID"));
            shareInfo.setEndDate(SysDateMgr.getLastDateThisMonth());

        }
        else if("2".equals(strModify_tag))
        {
        	shareInfo.setStartDate(data.getString("START_DATE"));
            shareInfo.setModifyTag(BofConst.MODIFY_TAG_UPD);
            shareInfo.setInstId(data.getString("INST_ID"));
            shareInfo.setEndDate(data.getString("END_DATE"));
        }

        bd.add(uca.getSerialNumber(), shareInfo);

    }

}
