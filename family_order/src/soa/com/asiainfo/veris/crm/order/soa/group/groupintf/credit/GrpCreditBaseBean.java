
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BlackWhiteTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GrpMemPlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GrpMerchMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ImpuTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationBBTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationxxtTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserPayPlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserSpecialePayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VpnMemTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.BlackWhiteOutInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class GrpCreditBaseBean
{
 
    public IDataset destroybyperson(IData inParam) throws Exception
    {
        GetOperationCode(inParam);
        List<BaseTradeData> retList = new ArrayList<BaseTradeData>();
        String dealFlag = inParam.getString("DEAL_FLAG");
        if (StringUtils.isBlank(dealFlag))
        {

        }
        else
        {
            this.CreateBlackWhiteTrade(inParam, retList);
            this.CreateBlackWhiteOut(inParam, retList);
            this.CreateVpnMebTrade(inParam, retList);
            this.CreateGrpMebPlatSvcTrade(inParam, retList);
            this.CreateGrpMerchMebTrade(inParam, retList);
            this.CreateEndPayPlanReg(inParam, retList);
            this.CreateEndSpecialePayInfoReg(inParam, retList);
            this.CreateGrpTradeImpu(inParam, retList);
            this.CreateRelationBbTrade(inParam, retList);
        }

        IDataset resulDataset = new DatasetList();
        IData data = new DataMap();
        data.put("GRP_TRADE_DATA", retList.toString());

        resulDataset.add(data);
        return resulDataset;

    }

    public void CreateBlackWhiteTrade(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID"); 
        String serialNumber_A =""; 
        String dealFlag = inParam.getString("DEAL_FLAG", "");
        String systiem = inParam.getString("ACCEPT_TIME");
        

        IDataset qrySerialNumber_A = RelaXxtInfoQry.getSerialNumberIData(userId); 
        if (IDataUtil.isNotEmpty(qrySerialNumber_A))
        {
              serialNumber_A = qrySerialNumber_A.getData(0).getString("SERIAL_NUMBER");
        } 
        if ("pause".equals(dealFlag) || "destroy".equals(dealFlag) || "ChgUs".equals(dealFlag))
        {
            IDataset blackWhiteInfos = UserBlackWhiteInfoQry.getBlackWhiteInfoByUserIData(userId);
            if (IDataUtil.isNotEmpty(blackWhiteInfos))
            {
                for (int i = 0, size = blackWhiteInfos.size(); i < size; i++)
                {
                    IData blackWhiteData = new DataMap();
                    blackWhiteData.putAll(blackWhiteInfos.getData(i));
                    blackWhiteData.put("END_DATE", systiem);
                    blackWhiteData.put("EXPECT_TIME", systiem);
                    blackWhiteData.put("UPDATE_TIME", systiem);
                    blackWhiteData.put("RSRV_NUM1", "0");
                    blackWhiteData.put("RSRV_TAG3", "0");
                    blackWhiteData.put("MODIFY_TAG", "2");
                    
                    if ("pause".equals(dealFlag)){
                    	 blackWhiteData.put("OPER_STATE", "04"); 	
                    	 blackWhiteData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    }
                       
                    if ("destroy".equals(dealFlag) || "ChgUs".equals(dealFlag))
                        blackWhiteData.put("OPER_STATE", "02");
                    
                    //增加对付费号码及家长号码是否一致判断  对校讯通用户做特殊处理 
                    if( "915001".endsWith(blackWhiteInfos.getData(i).getString("SERVICE_ID")) &&   blackWhiteInfos.getData(i).getString("RSRV_STR2") == null ) 
                        blackWhiteData.put("IS_NEED_PF", "0"); // 1或者是空 走服务开通发指令,0：不走服务开通不发指令
                    else 
                        blackWhiteData.put("IS_NEED_PF", "1");

                    BlackWhiteTradeData blackWhiteTradeData = new BlackWhiteTradeData(blackWhiteData);
                    resultList.add(blackWhiteTradeData);
                }
            }
            //add by dujt 增加对代付号码截止
            IDataset qryRelationXxt = RelaXxtInfoQry.getRelationXxtIData(serialNumber_A);  
            if(IDataUtil.isNotEmpty(qryRelationXxt)){ 
               for (int i = 0, size = qryRelationXxt.size(); i < size; i++){ 
                  IData RelationXxtInfo = qryRelationXxt.getData(i);
                  String SerialNumBerStr = RelationXxtInfo.getString("SERIAL_NUMBER_B");   
                  IDataset blackWhiteInfosXxt = UserBlackWhiteInfoQry.getBlackWhiteInfoBySerialNumBerIData(SerialNumBerStr);   
                  if(IDataUtil.isNotEmpty(blackWhiteInfosXxt)){ 
                   for (int j = 0, sizexxt = blackWhiteInfosXxt.size(); j < sizexxt; j++)
                    {
                      IData blackWhiteDataXxt = new DataMap();
                      blackWhiteDataXxt.putAll(blackWhiteInfosXxt.getData(j));
                      blackWhiteDataXxt.put("END_DATE", systiem);
                      blackWhiteDataXxt.put("EXPECT_TIME", systiem);
                      blackWhiteDataXxt.put("UPDATE_TIME", systiem);
                      blackWhiteDataXxt.put("RSRV_NUM1", "0");
                      blackWhiteDataXxt.put("RSRV_TAG3", "0");
                      blackWhiteDataXxt.put("MODIFY_TAG", "2");
                      if ("pause".equals(dealFlag)){
                    	  blackWhiteDataXxt.put("OPER_STATE", "04"); 
                    	  blackWhiteDataXxt.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                      }
                         
                      if ("destroy".equals(dealFlag) || "ChgUs".equals(dealFlag))
                          blackWhiteDataXxt.put("OPER_STATE", "02");  
                      BlackWhiteTradeData blackWhiteTradeData = new BlackWhiteTradeData(blackWhiteDataXxt);
                      resultList.add(blackWhiteTradeData);
                  }
                  } 
             } 
           }
            
        }
        else if ("back".equals(dealFlag))
        {
            IDataset blackWhiteInfos1 = UserBlackWhiteInfoQry.getBlackWhiteInfoByUserID(userId);
            if (IDataUtil.isNotEmpty(blackWhiteInfos1))
            {
                for (int i = 0, size = blackWhiteInfos1.size(); i < size; i++)
                {
                    IData blackWhiteData = new DataMap();
                    blackWhiteData.putAll(blackWhiteInfos1.getData(i));
                    blackWhiteData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    blackWhiteData.put("EXPECT_TIME", systiem);
                    blackWhiteData.put("UPDATE_TIME", systiem);
                    blackWhiteData.put("OPER_STATE", "05");
                    blackWhiteData.put("RSRV_NUM1", "0");
                    blackWhiteData.put("MODIFY_TAG", "2");
                    
                    //增加对付费号码及家长号码是否一致判断  对校讯通用户做特殊处理 
                    if( "915001".endsWith(blackWhiteInfos1.getData(i).getString("SERVICE_ID")) &&   blackWhiteInfos1.getData(i).getString("RSRV_STR2") == null ) 
                        blackWhiteData.put("IS_NEED_PF", "0"); // 1或者是空 走服务开通发指令,0：不走服务开通不发指令
                    else 
                        blackWhiteData.put("IS_NEED_PF", "1");

                    
                    BlackWhiteTradeData blackWhiteTradeData = new BlackWhiteTradeData(blackWhiteData);
                    resultList.add(blackWhiteTradeData);
                }
            }
           //add by zhengdx 增加对代付号码处理
            IDataset qryRelationXxt = RelaXxtInfoQry.getRelationXxtIData(serialNumber_A);  
            if(IDataUtil.isNotEmpty(qryRelationXxt)){ 
               for (int i = 0, size = qryRelationXxt.size(); i < size; i++){ 
                  IData RelationXxtInfo = qryRelationXxt.getData(i);
                  String SerialNumBerStr = RelationXxtInfo.getString("SERIAL_NUMBER_B");   
                  IDataset blackWhiteInfosXxt = UserBlackWhiteInfoQry.getBlackWhiteInfoBySerialNumBerIData(SerialNumBerStr);   
                  if(IDataUtil.isNotEmpty(blackWhiteInfosXxt)){ 
                    for (int j = 0, sizexxt = blackWhiteInfosXxt.size(); j < sizexxt; j++)
                     {
                        IData blackWhiteDataXxt = new DataMap();
                        blackWhiteDataXxt.putAll(blackWhiteInfosXxt.getData(j));
                        blackWhiteDataXxt.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                        blackWhiteDataXxt.put("EXPECT_TIME", systiem);
                        blackWhiteDataXxt.put("UPDATE_TIME", systiem);
                        blackWhiteDataXxt.put("RSRV_NUM1", "0");
                        blackWhiteDataXxt.put("RSRV_TAG3", "0");
                        blackWhiteDataXxt.put("MODIFY_TAG", "2");
                        blackWhiteDataXxt.put("OPER_STATE", "05"); 

                        BlackWhiteTradeData blackWhiteTradeData = new BlackWhiteTradeData(blackWhiteDataXxt);
                        resultList.add(blackWhiteTradeData);
                     }
                   } 
               }
            }
         }  
    }
    
    /**
     * 黑白名单及销户信息跨省同步
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public void CreateBlackWhiteOut(IData data, List<BaseTradeData> resultList) throws Exception
    {
        String userId = data.getString("USER_ID");
        String dealFlag = data.getString("DEAL_FLAG", "");
        String systime = data.getString("ACCEPT_TIME"); 
        String serial_number = data.getString("SERIAL_NUMBER");
        
        if ("destroy".equals(dealFlag))
        {
            IDataset blackWhiteOut = BlackWhiteOutInfoQry.getBlackWhiteOutBySnSycType(serial_number);
            if (IDataUtil.isNotEmpty(blackWhiteOut))
            {
                for (int i = 0, size = blackWhiteOut.size(); i < size; i++)
                {   
                    IData tmpData = new DataMap();
                    tmpData.putAll(blackWhiteOut.getData(i));
                    
                    tmpData.put("OPER_STATE", "02");    //02－退出名单
                    tmpData.put("SYNC_TYPE",  "03");
                    tmpData.put("STATUS",  "04");
                    tmpData.put("BK_RESULT",  "");
                    tmpData.put("STATUS_CODE",  "01");
                    
                    tmpData.put("START_DATE",systime);
                    tmpData.put("END_DATE",  SysDateMgr.getTheLastTime());
                    
                    tmpData.put("UPDATE_TIME", systime);
                    tmpData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    tmpData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    
                    //TF_F_BLACKWHITE_OUT在cen1库，暂时先直接insert，不用resultList
                    BlackWhiteOutInfoQry.insertBlackWhiteOut(tmpData); 
                    //BlackWhiteOutData blackWhiteOutData = new BlackWhiteOutData(tmpData);
                    //resultList.add(blackWhiteOut);
                }    
            }
        }
    }

    public void CreateVpnMebTrade(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        String dealFlag = inParam.getString("DEAL_FLAG", "");
        String system = inParam.getString("ACCEPT_TIME");

        if ("destroy".equals(dealFlag))
        {
            IDataset vpnList = UserVpnInfoQry.getMemberVpnByUserId(userId);
            if (IDataUtil.isNotEmpty(vpnList))
            {
                for (int i = 0, size = vpnList.size(); i < size; i++)
                {
                    IData vpnMebData = new DataMap();
                    vpnMebData.putAll(vpnList.getData(i));
                    vpnMebData.put("REMOVE_TAG", "1");
                    vpnMebData.put("MODIFY_TAG", "2");
                    vpnMebData.put("RSRV_NUM1", "0");
                    vpnMebData.put("RSRV_TAG3", "0");
                    vpnMebData.put("REMOVE_DATE", system);
                    vpnMebData.put("UPDATE_TIME", system);
                    VpnMemTradeData vpnMemTradeData = new VpnMemTradeData(vpnMebData);
                    resultList.add(vpnMemTradeData);
                }
            }
        }
    }

    public void CreateGrpMebPlatSvcTrade(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        String dealFlag = inParam.getString("DEAL_FLAG", "");
        String sysTiem = inParam.getString("ACCEPT_TIME");
        if ("destroy".equals(dealFlag) || "ChgUs".equals(dealFlag))
        {
            IDataset mebPlatSvcInfos = UserGrpMebPlatSvcInfoQry.getMemPlatSvcInfoByUserIDataset(userId);
            if (IDataUtil.isNotEmpty(mebPlatSvcInfos))
            {
                IData mebPlatSvcData = new DataMap();
                for (int i = 0, size = mebPlatSvcInfos.size(); i < size; i++)
                {
                    mebPlatSvcData.putAll(mebPlatSvcInfos.getData(i));
                    mebPlatSvcData.put("UPDATE_TIME", sysTiem);
                    mebPlatSvcData.put("RSRV_NUM1", "0");
                    mebPlatSvcData.put("MODIFY_TAG", "2");
                    mebPlatSvcData.put("END_DATE", sysTiem);
                    GrpMemPlatSvcTradeData grpMemPlatSvcTradeData = new GrpMemPlatSvcTradeData(mebPlatSvcData);
                    resultList.add(grpMemPlatSvcTradeData);
                }
            }
        }
    }

    public void CreateGrpMerchMebTrade(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        String sysTiem = inParam.getString("ACCEPT_TIME");

        // 状态变换
        CreateStateToNext(inParam);

        boolean hasMsg = false;
        ;

        String state = inParam.getString("OPER_STATE");

        // 查询用户主体服务
        IDataset mainUserSvcDate = UserSvcStateInfoQry.getUserMainState(userId);

        if (IDataUtil.isEmpty(mainUserSvcDate))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1, "没有获取到原用户为:" + userId + "主体服务状态资料");
        }

        String userstatecode = mainUserSvcDate.getData(0).getString("STATE_CODE");

        // 恢复, 不允许复机,原来是正常状态的,返回,不发报文;
        if (state == "A" && (userstatecode == "0" || userstatecode == "N" || userstatecode == "9" || userstatecode == "6"))
        {
            inParam.put("DEAL_FLAG1", null);
            return;
        }
        if ("B".equals(state)) // 半停机
        {
            hasMsg = true; // 发报文;
        }
        if (state == "D" && (userstatecode == "9" || userstatecode == "6")) // 注销, 原状态是注销的,返回,不发报文;
        {
            inParam.put("DEAL_FLAG1", null);
            return;
        }
        if (state == "ChgSN") // 改号
        {
            inParam.put("DEAL_FLAG1", state);
            hasMsg = true;
            return;
        }

        if (state == "ChgUs") // 过户
        {
            inParam.put("DEAL_FLAG1", state);
            hasMsg = true;
            state = "D";// 海南这边过户要求注销集团所有资料
        }
        if (state == "D") // 销号
        {
            hasMsg = true;
        }

        IDataset userGrpMerchMebInfos = UserGrpMerchMebInfoQry.qryMerchMebInfoByUserid(userId);
        if (IDataUtil.isNotEmpty(userGrpMerchMebInfos))
        {
            for (int i = 0; i < userGrpMerchMebInfos.size(); i++)
            {
                // 比较当前状态;
                // 如果原状态已经是停机或半停机,那么都不要再登记台帐;
                String status = userGrpMerchMebInfos.getData(i).getString("STATUS");

                if ((state == "B" || state == "N") && status.equals("N"))
                {
                    continue;
                }

                // 如果原状态已经是开机,那么不要再登记台帐;
                if (state == "A" && (status.equals("A") || StringUtils.isBlank(status)))
                {
                    continue;
                }

                IData merchMebData = new DataMap();
                merchMebData.putAll(userGrpMerchMebInfos.getData(i));
                merchMebData.put("UPDATE_TIME", sysTiem);
                merchMebData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                merchMebData.put("START_DATE", sysTiem);

                if (state == "N") // 暂停
                {
                    merchMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    merchMebData.put("STATUS", "N");
                }
                else if (state == "A")// 恢复;
                {
                    merchMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    merchMebData.put("STATUS", "A");
                }
                else if (state == "D")// 销号
                {
                    merchMebData.put("STATUS", "Z");
                    merchMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    merchMebData.put("END_DATE", SysDateMgr.getNextSecond(sysTiem));// 要加1秒;
                }
                else if (state == "B")// 半停机
                {
                    merchMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    merchMebData.put("STATUS", "N");
                }
                else
                {
                    merchMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    merchMebData.put("STATUS", "N");
                }

                GrpMerchMebTradeData grpMerchMebTradeData = new GrpMerchMebTradeData(merchMebData);
                resultList.add(grpMerchMebTradeData);
            }

            if ((state == "D") && (hasMsg))// 销号发报文
            {
                IData otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "BBSS");
                otherData.put("RSRV_VALUE", "集团BBOSS标志");
                otherData.put("RSRV_STR9", "78101");// 服务开通侧用成员的service_id对应为78101
                otherData.put("OPER_CODE", "07");
                otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                otherData.put("START_DATE", sysTiem);
                otherData.put("END_DATE", sysTiem);
                otherData.put("INST_ID", SeqMgr.getInstId());
                otherData.put("IS_NEFD_PF", "0");
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }
            else if ((state == "ChgSN") && (hasMsg))// 改号发报文
            {
                IData otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "BBSS");
                otherData.put("RSRV_VALUE", "集团BBOSS标志");
                otherData.put("RSRV_STR9", "78101");// 服务开通侧用成员的service_id对应为78101
                otherData.put("OPER_CODE", "06");
                otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                otherData.put("START_DATE", sysTiem);
                otherData.put("END_DATE", sysTiem);
                otherData.put("INST_ID", SeqMgr.getInstId());
                otherData.put("IS_NEFD_PF", "0");
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }
            else if ((state == "B") && (hasMsg))// 半停机发报文
            {
                IData otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "BBSS");
                otherData.put("RSRV_VALUE", "集团BBOSS标志");
                otherData.put("RSRV_STR9", "78101");// 服务开通侧用成员的service_id对应为78101
                otherData.put("OPER_CODE", "01");
                otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                otherData.put("START_DATE", sysTiem);
                otherData.put("END_DATE", sysTiem);
                otherData.put("INST_ID", SeqMgr.getInstId());
                otherData.put("IS_NEFD_PF", "0");
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }
        }
        else
        {
            inParam.put("DEAL_FLAG1", null);
        }

        String dealFlag1 = inParam.getString("DEAL_FLAG1");

        if (StringUtils.isBlank(dealFlag1))
        {
            return;
        }
    }

    public void CreateEndPayPlanReg(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        String dealFlag = inParam.getString("DEAL_FLAG", "");
        String sysTiem = inParam.getString("ACCEPT_TIME");
        if ("destroy".equals(dealFlag))
        {
            IDataset userPayPlanInfos = UserPayPlanInfoQry.getUserPayPlanInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(userPayPlanInfos))
            {
                IData userPayPlanData = new DataMap();
                for (int i = 0, size = userPayPlanInfos.size(); i < size; i++)
                {
                    userPayPlanData.putAll(userPayPlanInfos.getData(i));
                    userPayPlanData.put("UPDATE_TIME", sysTiem);
                    userPayPlanData.put("RSRV_NUM1", "0");
                    userPayPlanData.put("MODIFY_TAG", "1");
                    userPayPlanData.put("END_DATE", sysTiem);
                    UserPayPlanTradeData userPayPlanTradeData = new UserPayPlanTradeData(userPayPlanData);
                    resultList.add(userPayPlanTradeData);
                }
            }
        }
    }

    public void CreateEndSpecialePayInfoReg(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        String dealFlag = inParam.getString("DEAL_FLAG", "");
        String sysTiem = inParam.getString("ACCEPT_TIME");
        if ("destroy".equals(dealFlag))
        {
            IDataset userSpecialePayInfos = UserSpecialPayInfoQry.getUserSpecPayByUserId(userId);
            if (IDataUtil.isNotEmpty(userSpecialePayInfos))
            {
                IData userSpecialePayData = new DataMap();
                for (int i = 0, size = userSpecialePayInfos.size(); i < size; i++)
                {
                    userSpecialePayData.putAll(userSpecialePayInfos.getData(i));
                    userSpecialePayData.put("UPDATE_TIME", sysTiem);
                    userSpecialePayData.put("RSRV_NUM1", "0");
                    userSpecialePayData.put("MODIFY_TAG", "1");
                    userSpecialePayData.put("END_CYCLE_ID", SysDateMgr.getSysDateYYYYMMDD());
                    UserSpecialePayTradeData serSpecialePayTradeData = new UserSpecialePayTradeData(userSpecialePayData);
                    resultList.add(serSpecialePayTradeData);
                }
            }
        }
    }

    public void CreateGrpTradeImpu(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID", "");
        String dealFlag = inParam.getString("DEAL_FLAG", "");
        String sysTiem = inParam.getString("ACCEPT_TIME");
        if (StringUtils.equals(dealFlag, "destroy"))
        {
            // 销户时退出成员,impu截止
            IDataset impuList = UserImpuInfoQry.queryUserImpuInfo(userId);
            if (IDataUtil.isNotEmpty(impuList))
            {
                IData impuData = new DataMap();
                impuData.putAll(impuList.getData(0));
                impuData.put("END_DATE", sysTiem);
                impuData.put("MODIFY_TAG", "2");
                ImpuTradeData impuTradeData = new ImpuTradeData(impuData);
                resultList.add(impuTradeData);
            }
            // 判断用户是否是IMPU用户 销户时根据impu表进行注销CNTRX成员
            boolean impuInfo = false;
            IDataset impuInfos = UserImpuInfoQry.getInfoByUserIdRsrvStr1(userId);
            if (IDataUtil.isNotEmpty(impuInfos))
            {
                impuInfo = true;
            }
            if (impuInfo)
            {
                IData otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "CNTRX");
                otherData.put("RSRV_VALUE", "CNTRX成员");
                otherData.put("RSRV_NUM1", "0");
                otherData.put("RSRV_NUM2", "0");
                otherData.put("RSRV_NUM3", "0");
                otherData.put("RSRV_STR9", "8171");
                otherData.put("RSRV_STR11", "1");
                otherData.put("RSRV_STR12", "2");
                otherData.put("OPER_CODE", "02");
                otherData.put("START_DATE", SysDateMgr.getYesterdayTime());
                otherData.put("END_DATE", sysTiem);
                otherData.put("MODIFY_TAG", "0");
                otherData.put("INST_ID", SeqMgr.getInstId());
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }

            // 销户时 如果是融合总机成员 发送注销注销融合总机(成员)指令
            IDataset relationDataset = RelaUUInfoQry.qryUU("", userId, "S3", null, null);
            if (IDataUtil.isNotEmpty(relationDataset))
            {
                IData otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "CNTRX");
                otherData.put("RSRV_VALUE", "CNTRX成员");
                otherData.put("RSRV_NUM1", "0");
                otherData.put("RSRV_NUM2", "0");
                otherData.put("RSRV_NUM3", "0");
                otherData.put("RSRV_STR9", "6301");
                otherData.put("RSRV_STR11", "D");
                otherData.put("OPER_CODE", "02");
                otherData.put("START_DATE", SysDateMgr.getYesterdayTime());
                otherData.put("END_DATE", sysTiem);
                otherData.put("MODIFY_TAG", "0");
                otherData.put("INST_ID", SeqMgr.getInstId());
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }

            // 判断用户是否是IMPU用户 销户时 如果发了HSS、ENUM， 注销HSS、ENUM
            boolean impuInfo1 = false;
            IDataset impuhssDataset = UserImpuInfoQry.getImpuInfoByUserId(userId);

            if (IDataUtil.isNotEmpty(impuhssDataset))
            {
                impuInfo1 = true;
            }
            if (impuInfo1)
            {
                IData otherData = new DataMap();
                // 如果发了HSS 注销HSS
                otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "HSS");
                otherData.put("RSRV_VALUE", "HSS成员");
                otherData.put("RSRV_NUM1", "0");
                otherData.put("RSRV_NUM2", "0");
                otherData.put("RSRV_NUM3", "0");
                otherData.put("RSRV_STR9", "8172");
                otherData.put("RSRV_STR11", "1");
                otherData.put("OPER_CODE", "02");
                otherData.put("START_DATE", SysDateMgr.getYesterdayTime());
                otherData.put("END_DATE", sysTiem);
                otherData.put("MODIFY_TAG", "0");
                otherData.put("INST_ID", SeqMgr.getInstId());
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);

                // 如果发了ENUM 注销ENUM
                otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "ENUM");
                otherData.put("RSRV_VALUE", "ENUM成员");
                otherData.put("RSRV_NUM1", "0");
                otherData.put("RSRV_NUM2", "0");
                otherData.put("RSRV_NUM3", "0");
                otherData.put("RSRV_STR9", "8173");
                otherData.put("RSRV_STR11", "1");
                otherData.put("OPER_CODE", "02");
                otherData.put("START_DATE", SysDateMgr.getYesterdayTime());
                otherData.put("END_DATE", sysTiem);
                otherData.put("MODIFY_TAG", "0");
                otherData.put("INST_ID", SeqMgr.getInstId());
                otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }
        }
        else if (StringUtils.equals(dealFlag, "pause"))
        {// 暂停时 往other插一条04数据
            boolean impuInfo = false;
            IDataset impuInfos = UserImpuInfoQry.getInfoByUserIdRsrvStr1(userId);
            if (IDataUtil.isNotEmpty(impuInfos))
            {
                impuInfo = true;
            }
            if (impuInfo)
            {
                IData otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "CNTRX");
                otherData.put("RSRV_VALUE", "CNTRX成员");
                otherData.put("RSRV_NUM1", "0");
                otherData.put("RSRV_NUM2", "0");
                otherData.put("RSRV_NUM3", "0");
                otherData.put("RSRV_STR9", "8171");
                otherData.put("RSRV_STR11", "04");
                otherData.put("RSRV_STR12", "03");
                otherData.put("OPER_CODE", "08");
                otherData.put("START_DATE", SysDateMgr.getYesterdayTime());
                otherData.put("END_DATE", sysTiem);
                otherData.put("MODIFY_TAG", "0");
                otherData.put("INST_ID", SeqMgr.getInstId());
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }

        }
        else if (StringUtils.equals(dealFlag, "back"))
        {// 恢复时 往other插一条05数据
            boolean impuInfo = false;
            IDataset impuInfos = UserImpuInfoQry.getInfoByUserIdRsrvStr1(userId);
            if (IDataUtil.isNotEmpty(impuInfos))
            {
                impuInfo = true;
            }
            if (impuInfo)
            {
                IData otherData = new DataMap();
                otherData.put("USER_ID", userId);
                otherData.put("RSRV_VALUE_CODE", "CNTRX");
                otherData.put("RSRV_VALUE", "CNTRX成员");
                otherData.put("RSRV_NUM1", "0");
                otherData.put("RSRV_NUM2", "0");
                otherData.put("RSRV_NUM3", "0");
                otherData.put("RSRV_STR9", "8171");
                otherData.put("RSRV_STR11", "05");
                otherData.put("RSRV_STR12", "0");
                otherData.put("OPER_CODE", "08");
                otherData.put("START_DATE", SysDateMgr.getYesterdayTime());
                otherData.put("END_DATE", sysTiem);
                otherData.put("MODIFY_TAG", "0");
                otherData.put("INST_ID", SeqMgr.getInstId());
                OtherTradeData otherTradeData = new OtherTradeData(otherData);
                resultList.add(otherTradeData);
            }
        }
    }

    public void CreateStateToNext(IData inParam) throws Exception
    {
        String tradeTypeCode = inParam.getString("TRADE_TYPE_CODE");
        String userId = inParam.getString("USER_ID");
        String dealFlag = inParam.getString("DEAL_FLAG", "");
        String strstate;

        IDataset userGrpMerchMebInfos = UserGrpMerchMebInfoQry.qryMerchMebInfoByUserid(userId);

        // 是否订购了BBOSS业务;
        if (IDataUtil.isEmpty(userGrpMerchMebInfos))
        {
            inParam.put("DEAL_FLAG1", null);
            return;
        }

        if ("ChgUs".equals(dealFlag))// 过户;
        {
            strstate = "ChgUs";
        }
        else if ("ChgSN".equals(dealFlag))
        {
            strstate = "ChgSN"; // 改号;
        }
        else if ("destroy".equals(dealFlag))// 销号;
        {
            strstate = "D";
        }
        else
        {
            strstate = "ChgStatus"; // 状态变更: 半停机,停机和开机
        }

        if ("ChgStatus".equals(strstate))
        {
            IData userinfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
            // 取下BRAND_CODE，每次都按照ZZZZ查不合理，有些操作类型没有配置ZZZZ
            if (IDataUtil.isEmpty(userinfo))
            {
                return;
            }
            IDataset svcStateInfos = TradeSvcStateInfoQry.querySvcStateParamByKey(tradeTypeCode, userinfo.getString("BRAND_CODE"), "-1", "ZZZZ");

            if (IDataUtil.isEmpty(svcStateInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1, "办理[" + tradeTypeCode + "]业务，找不到用户变更的状态!", "");
            }

            String strNewstate;
            strNewstate = svcStateInfos.getData(0).getString("NEW_STATE_CODE");// 新状态;

            if (strNewstate == "0" || strNewstate == "N")
            {
                strstate = "A"; // 开机状态;

            }
            else if (strNewstate == "A" || strNewstate == "R" || strNewstate == "B")
            {
                strstate = "B"; // 半停机状态

            }
            else if (strNewstate == "5" || strNewstate == "7" || strNewstate == "R")
            {
                strstate = "N"; // 停机状态;

            }
            else if (strNewstate == "9" || strNewstate == "6")
            {
                strstate = "D"; // 销号
            }
            else
            {
                strstate = "N"; // 停机状态;
            }
        }

        inParam.put("OPER_STATE", strstate);
        inParam.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
    }

    /**
     * 处理relationBB表信息
     * 
     * @param data
     * @param resultLis
     * @throws Exception
     */
    public void CreateRelationBbTrade(IData data, List<BaseTradeData> resultLis) throws Exception
    {

        String userId = data.getString("USER_ID");
        String dealFlag = data.getString("DEAL_FLAG", "");
        String sysTiem = data.getString("ACCEPT_TIME");
        if ("destroy".equals(dealFlag))
        {
            IDataset relatinonBbInfos = RelaBBInfoQry.getBBInfo(userId);
            if (IDataUtil.isNotEmpty(relatinonBbInfos))
            {
                IData relaBBinfoData = new DataMap();
                for (int i = 0, size = relatinonBbInfos.size(); i < size; i++)
                {
                    relaBBinfoData.putAll(relatinonBbInfos.getData(i));
                    relaBBinfoData.put("UPDATE_TIME", sysTiem);
                    relaBBinfoData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    relaBBinfoData.put("END_DATE", sysTiem);
                    RelationBBTradeData relationTradeData = new RelationBBTradeData(relaBBinfoData);
                    resultLis.add(relationTradeData);
                }
            }
            IData userinfo = UcaInfoQry.qryUserInfoByUserId(userId);
            if (IDataUtil.isEmpty(userinfo))
            {
                CSAppException.apperr(GrpException.CRM_GRP_137);
            }
            
            IDataset relatinonxxtInfos = RelaXxtInfoQry.qryMemInfoBySNandUserIdA(userinfo.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(relatinonxxtInfos))
            {
                IData relaxxtinfoData = new DataMap();
                for (int i = 0, size = relatinonxxtInfos.size(); i < size; i++)
                {
                    relaxxtinfoData.putAll(relatinonxxtInfos.getData(i));
                    relaxxtinfoData.put("UPDATE_TIME", sysTiem);
                    relaxxtinfoData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    relaxxtinfoData.put("END_DATE", sysTiem);
                    RelationxxtTradeData relationxxtTradeData = new RelationxxtTradeData(relaxxtinfoData);
                    resultLis.add(relationxxtTradeData);
                }
            }
        }
    }

    public void GetOperationCode(IData data) throws Exception
    {
        if (StringUtils.equals("", data.getString("ACCEPT_TIME", "")))
        {
            data.put("ACCEPT_TIME", SysDateMgr.getSysTime());
        }
        String strTradeTypeCode = data.getString("TRADE_TYPE_CODE", "");
        if (StringUtils.equals("7110", strTradeTypeCode) || StringUtils.equals("7220", strTradeTypeCode) || StringUtils.equals("7305", strTradeTypeCode) || StringUtils.equals("131", strTradeTypeCode) || StringUtils.equals("136", strTradeTypeCode))
        {
            data.put("DEAL_FLAG", "pause"); // 停机 －对应成员的暂停
        }
        else if (StringUtils.equals("7303", strTradeTypeCode) || StringUtils.equals("7304", strTradeTypeCode) || StringUtils.equals("7301", strTradeTypeCode) || StringUtils.equals("133", strTradeTypeCode)
                || StringUtils.equals("126", strTradeTypeCode))
        {
            data.put("DEAL_FLAG", "back"); // 开机 - 对应成员的恢复
        }
        else if (StringUtils.equals("7302", strTradeTypeCode) || StringUtils.equals("310", strTradeTypeCode))// 缴费复机, 复机
        {
            data.put("DEAL_FLAG", "regain");// 对应成员的加入
        }
        else if (StringUtils.equals("192", strTradeTypeCode) || StringUtils.equals("7230", strTradeTypeCode) || StringUtils.equals("7240", strTradeTypeCode) || StringUtils.equals("42", strTradeTypeCode)) // 主动销户，欠费预销号，欠费销号,携号转网
        {
            data.put("DEAL_FLAG", "destroy");// 对应成员的退出
        }
        else if (StringUtils.equals("100", strTradeTypeCode))
        {
            data.put("DEAL_FLAG", "ChgUs");// 过户
        }
        else if (StringUtils.equals("9143", strTradeTypeCode) || StringUtils.equals("143", strTradeTypeCode))// 改号
        {
            data.put("DEAL_FLAG", "ChgSN");
        }
        else
        {
            data.put("DEAL_FLAG", "null"); // 无数据处理
        }
    }

    public IDataset bbossMemberStopOrBack(IData inParam) throws Exception
    {
        GetOperationCode(inParam);
        List<BaseTradeData> retList = new ArrayList<BaseTradeData>();

        this.CreateMebSvcStateTradeReg(inParam, retList);// 登记集团彩铃成员暂停恢复服务状态信息
        CreateStateToNext(inParam);// BBOSS暂停恢复状态转换
        String strOperCode=inParam.getString("OPER_STATE","");
        if("N".equals(strOperCode)||"B".equals(strOperCode))
        {
        	IData otherData = new DataMap();
            otherData.put("USER_ID", inParam.getString("USER_ID"));
            otherData.put("RSRV_VALUE_CODE", "BBSS");
            otherData.put("RSRV_VALUE", "集团BBOSS标志");
            otherData.put("RSRV_STR9", "78101");// 服务开通侧用成员的service_id对应为78101
            otherData.put("OPER_CODE", "01");
            otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            otherData.put("START_DATE", SysDateMgr.getSysDate());
            otherData.put("END_DATE", SysDateMgr.getTheLastTime());
            otherData.put("INST_ID", SeqMgr.getInstId());
            otherData.put("IS_NEFD_PF", "1");
            OtherTradeData otherTradeData = new OtherTradeData(otherData);
            retList.add(otherTradeData);
        	
        }
        else if ("A".equals(strOperCode))
        {
        	IData otherData = new DataMap();
            otherData.put("USER_ID", inParam.getString("USER_ID"));
            otherData.put("RSRV_VALUE_CODE", "BBSS");
            otherData.put("RSRV_VALUE", "集团BBOSS标志");
            otherData.put("RSRV_STR9", "78101");// 服务开通侧用成员的service_id对应为78101
            otherData.put("OPER_CODE", "01");
            otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            otherData.put("START_DATE", SysDateMgr.getSysDate());
            otherData.put("END_DATE", SysDateMgr.getTheLastTime());
            otherData.put("INST_ID", SeqMgr.getInstId());
            otherData.put("IS_NEFD_PF", "1");
            OtherTradeData otherTradeData = new OtherTradeData(otherData);
            retList.add(otherTradeData);
        	
        }
        
        IDataset resulDataset = new DatasetList();
        IData data = new DataMap();
        data.put("GRP_TRADE_DATA", retList);
        resulDataset.add(data);
        return resulDataset;

    }
    

    public IDataset destroyGrpMemInfo(IData inParam) throws Exception
    {
        GetOperationCode(inParam);
        List<BaseTradeData> retList = new ArrayList<BaseTradeData>();
        String dealFlag = inParam.getString("DEAL_FLAG");
        if (StringUtils.isNotBlank(dealFlag))
        {
            this.DestroyGrpMebAllInfo(inParam, retList);// 注销集团成员信息
            this.CreateBlackWhiteTrade(inParam, retList);// 注销黑白名单
            this.CreateGrpMebPlatSvcTrade(inParam, retList); // MebPlatSvc注销
            this.CreateMebSvcStateTradeReg(inParam, retList);// 发送BBOSS成员注销(流程实际操作的彩铃，此处应该有问题，暂时按照老系统处理)
            this.CreateStateToNext(inParam);// BBOSS暂停恢复状态转换
        }

        IDataset resulDataset = new DatasetList();
        IData data = new DataMap();
        data.put("GRP_TRADE_DATA", retList);
        resulDataset.add(data);
        return resulDataset;
    }

    public void DestroyGrpMebAllInfo(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        IDataset relations = RelaUUInfoQry.qryUU("", userId, "", null, null);
        if (IDataUtil.isNotEmpty(relations))
        {
            for (int i = 0, size = relations.size(); i < size; i++)
            {
                IData relation = relations.getData(i);
                String userIda = relation.getString("USER_ID_A");
                String relationTypeCode = relation.getString("RELATION_TYPE_CODE");
                if("XT".equals(relationTypeCode))
                {
                    EndRelationInfo(relation, resultList, false);//如果是校讯通业务，则需要注销uu表                    
                    continue;
                }

                // 查询集团用户信息
                IData userinfo = UcaInfoQry.qryUserMainProdInfoByUserId(userIda);
                if (IDataUtil.isEmpty(userinfo))
                {
                    continue;
                }
                // 不是集团产品或者是vpmn或者是bboss产品不处理
                if (!"10".equals(userinfo.getString("PRODUCT_MODE")) || "8000".equals(userinfo.getString("PRODUCT_ID")) || "BOSG".equals(userinfo.getString("BRAND_CODE")))
                {
                    continue;
                }
                EndRelationInfo(relation, resultList, false);
                EndDiscntInfo(userId, userIda, relationTypeCode, resultList);
                EndPayInfoReg(userId, userIda, userinfo.getString("PRODUCT_ID"), resultList);
                EndSvcInfo(userId, userIda, resultList);
                EndUserProductInfo(userId, userIda, relationTypeCode, resultList);
                EndUserOtherInfo(userId, userIda, relationTypeCode, resultList);
            }
        }

        IDataset relationbbs = RelaBBInfoQry.qryBB("", userId, "", null, null);
        if (IDataUtil.isNotEmpty(relationbbs))
        {
            for (int j = 0, size = relationbbs.size(); j < size; j++)
            {
                IData relation = relationbbs.getData(j);
                String userIda = relation.getString("USER_ID_A");

                // 查询集团用户信息
                IData userinfo = UcaInfoQry.qryUserMainProdInfoByUserId(userIda);
                if (IDataUtil.isEmpty(userinfo))
                {
                    continue;
                }
                if (!"10".equals(userinfo.getString("PRODUCT_MODE")) || "8000".equals(userinfo.getString("PRODUCT_ID")) || "BOSG".equals(userinfo.getString("BRAND_CODE")))
                {
                    continue;
                }

                String relationTypeCode = relation.getString("RELATION_TYPE_CODE");
                EndRelationInfo(relation, resultList, true);
                EndDiscntInfo(userId, userIda, relationTypeCode, resultList);
                EndPayInfoReg(userId, userIda, userinfo.getString("PRODUCT_ID"), resultList);
                EndSvcInfo(userId, userIda, resultList);
                EndUserProductInfo(userId, userIda, relationTypeCode, resultList);
                EndUserOtherInfo(userId, userIda, relationTypeCode, resultList);
            }
        }
        IDataset relatinonxxtInfos = RelaXxtInfoQry.qryMemInfoBySNandUserIdA(inParam.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(relatinonxxtInfos))
        {
            IData relaxxtinfoData = new DataMap();
            for (int i = 0, size = relatinonxxtInfos.size(); i < size; i++)
            {
                relaxxtinfoData.putAll(relatinonxxtInfos.getData(i));
                relaxxtinfoData.put("UPDATE_TIME", inParam.getString("ACCEPT_TIME"));
                relaxxtinfoData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                relaxxtinfoData.put("END_DATE", inParam.getString("ACCEPT_TIME"));
                RelationxxtTradeData relationxxtTradeData = new RelationxxtTradeData(relaxxtinfoData);
                resultList.add(relationxxtTradeData);
            }
        }


    }

    public void EndRelationInfo(IData relation, List<BaseTradeData> resultList, boolean isBB) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        IData tradeRelation = new DataMap();
        tradeRelation.putAll(relation);
        tradeRelation.put("UPDATE_TIME", sysTime);
        tradeRelation.put("MODIFY_TAG", "1");
        tradeRelation.put("END_DATE", sysTime);
        if (isBB)
        {
            RelationBBTradeData tradeRelationData = new RelationBBTradeData(tradeRelation);
            resultList.add(tradeRelationData);
        }
        else
        {
            RelationTradeData tradeRelationData = new RelationTradeData(tradeRelation);
            resultList.add(tradeRelationData);
        }
    }

    public void EndDiscntInfo(String userId, String userIda, String relationTypeCode, List<BaseTradeData> resultList) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        IDataset userDisInfos = UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, userIda);
        if (IDataUtil.isEmpty(userDisInfos))
        {
            return;
        }
        for (int i = 0, size = userDisInfos.size(); i < size; i++)
        {
            IData userDisData = new DataMap();
            userDisData.putAll(userDisInfos.getData(i));
            userDisData.put("UPDATE_TIME", sysTime);
            userDisData.put("MODIFY_TAG", "1");
            userDisData.put("END_DATE", sysTime);
            DiscntTradeData userDisTradeData = new DiscntTradeData(userDisData);
            resultList.add(userDisTradeData);

            EndAttrInfo(userId, userDisData.getString("INST_ID"), resultList);
        }
    }

    public void EndPayInfoReg(String userId, String userIda, String productId, List<BaseTradeData> resultList) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        IDataset userPayPlanInfos = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(userId, userIda);
        if (IDataUtil.isEmpty(userPayPlanInfos))
        {
            return;
        }
        IData userPayPlanData = new DataMap();
        for (int i = 0, size = userPayPlanInfos.size(); i < size; i++)
        {
            userPayPlanData.putAll(userPayPlanInfos.getData(i));
            userPayPlanData.put("UPDATE_TIME", sysTime);
            userPayPlanData.put("MODIFY_TAG", "1");
            userPayPlanData.put("END_DATE", sysTime);
            UserPayPlanTradeData userPayPlanTradeData = new UserPayPlanTradeData(userPayPlanData);
            resultList.add(userPayPlanTradeData);
            if ("G".equals(userPayPlanData.getString("PLAN_TYPE_CODE")))
            {
                // 注销特殊付费关系
                IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, userIda);

                if (IDataUtil.isEmpty(userSpecialPayList))
                {
                    continue;
                }

                IData userSpecialPayData = userSpecialPayList.getData(0);
                String endcycleId = DiversifyAcctUtil.getLastDayThisAcct(userId).replaceAll("-", "").substring(0, 8);

                userSpecialPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userSpecialPayData.put("END_CYCLE_ID", endcycleId);
                UserSpecialePayTradeData userSpecialPayTradeData = new UserSpecialePayTradeData(userSpecialPayData);
                resultList.add(userSpecialPayTradeData);

                String payItemCode = userSpecialPayData.getString("PAYITEM_CODE", "");
                String acctId = userSpecialPayData.getString("ACCT_ID", "");
                // 注销付费关系
                IDataset userPayRelaList = PayRelaInfoQry.getPyrlByPk(userId, acctId, payItemCode, userSpecialPayData.getString("START_CYCLE_ID"), null);

                if (IDataUtil.isNotEmpty(userPayRelaList))
                {
                    IData userPayRelaData = userPayRelaList.getData(0);

                    userPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userPayRelaData.put("END_CYCLE_ID", endcycleId);

                    PayRelationTradeData ayRelationTradeData = new PayRelationTradeData(userPayRelaData);
                    resultList.add(ayRelationTradeData);

                }
            }
        }
    }

    public void EndSvcInfo(String userId, String userIda, List<BaseTradeData> resultList) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        IDataset userSvcInfos = UserSvcInfoQry.getUserSvcByUserIdAB(userId, userIda);
        if (IDataUtil.isEmpty(userSvcInfos))
        {
            return;
        }
        for (int i = 0, size = userSvcInfos.size(); i < size; i++)
        {
            IData userSvcData = new DataMap();
            userSvcData.putAll(userSvcInfos.getData(i));
            userSvcData.put("UPDATE_TIME", sysTime);
            userSvcData.put("MODIFY_TAG", "1");
            userSvcData.put("END_DATE", sysTime);
            SvcTradeData userSvcTradeData = new SvcTradeData(userSvcData);
            resultList.add(userSvcTradeData);

            EndAttrInfo(userId, userSvcData.getString("INST_ID"), resultList);
        }
    }

    public void EndUserProductInfo(String userId, String userIda, String relationTypeCode, List<BaseTradeData> resultList) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        IDataset userProductInfos = UserProductInfoQry.getUserProductByValue(userId, userIda, "12", null);
        if (IDataUtil.isEmpty(userProductInfos))
        {
            return;
        }
        for (int i = 0, size = userProductInfos.size(); i < size; i++)
        {
            IData userProductInfoData = new DataMap();
            userProductInfoData.putAll(userProductInfos.getData(i));
            userProductInfoData.put("UPDATE_TIME", sysTime);
            userProductInfoData.put("MODIFY_TAG", "1");
            userProductInfoData.put("END_DATE", sysTime);
            ProductTradeData userProductTradeData = new ProductTradeData(userProductInfoData);
            resultList.add(userProductTradeData);

            EndAttrInfo(userId, userProductInfoData.getString("INST_ID"), resultList);
        }
    }

    public void EndUserOtherInfo(String userId, String userIda, String relationTypeCode, List<BaseTradeData> resultList) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();

        IDataset userOhterInfos = UserOtherInfoQry.queryUserAllValidInfos(userId);

        if (IDataUtil.isEmpty(userOhterInfos))
        {
            return;
        }
        for (int i = 0, size = userOhterInfos.size(); i < size; i++)
        {
            String rervValueCode = userOhterInfos.getData(i).getString("RSRV_VALUE_CODE");

            if (!"DLMR".equals(rervValueCode) && !"GRAD".equals(rervValueCode) && !"VGPR".equals(rervValueCode) && !"MEMP".equals(rervValueCode) && !"N001".equals(rervValueCode) && !"NPRI".equals(rervValueCode) && !"IDC".equals(rervValueCode)
                    && !"VOIP".equals(rervValueCode))
            {
                continue;
            }
            IData userOhterData = new DataMap();
            userOhterData.putAll(userOhterInfos.getData(i));
            userOhterData.put("UPDATE_TIME", sysTime);
            userOhterData.put("MODIFY_TAG", "1");
            userOhterData.put("END_DATE", sysTime);
            OtherTradeData userOtherTradeData = new OtherTradeData(userOhterData);
            resultList.add(userOtherTradeData);
        }
    }

    public void EndAttrInfo(String userId, String instId, List<BaseTradeData> resultList) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByInstID(userId, instId);
        if (IDataUtil.isEmpty(userAttrInfos))
        {
            return;
        }
        for (int i = 0, size = userAttrInfos.size(); i < size; i++)
        {
            IData userAttrData = new DataMap();
            userAttrData.putAll(userAttrInfos.getData(i));
            userAttrData.put("UPDATE_TIME", sysTime);
            userAttrData.put("MODIFY_TAG", "1");
            userAttrData.put("END_DATE", sysTime);
            AttrTradeData userAttrTradeData = new AttrTradeData(userAttrData);
            resultList.add(userAttrTradeData);
        }
    }

    public void CreateMebSvcStateTradeReg(IData inParam, List<BaseTradeData> resultList) throws Exception
    {
        String sysTime = SysDateMgr.getSysTime();
        String userId = inParam.getString("USER_ID");
        String tradeTypeCode = inParam.getString("TRADE_TYPE_CODE");
        String planTypeCode = "";
        IDataset relations = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userId, "26");
        if (IDataUtil.isEmpty(relations))
        {
            return;
        }
        String userIda = relations.getData(0).getString("USER_ID_A");

        IDataset userPayPlanInfos = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(userId, userIda);
        if (IDataUtil.isEmpty(userPayPlanInfos))
        {
            // 获取成员付费计划失败
            CSAppException.apperr(GrpException.CRM_GRP_825);
        }
        planTypeCode = userPayPlanInfos.getData(0).getString("PLAN_TYPE_CODE");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIda);
        if (IDataUtil.isEmpty(userInfo))
        {
            // 获取成员归属集团的用户信息失败失败!

            CSAppException.apperr(GrpException.CRM_GRP_469);
        }
        String strMainTag = userInfo.getString("USER_STATE_CODESET");
        if ("0".equals(strMainTag) && "7130".equals(tradeTypeCode))
        {
            // CRM台帐表中已存在该业务工单
            CSAppException.apperr(GrpException.CRM_GRP_826);
        }

        IDataset commPara = CommparaInfoQry.getCommpara("CSM", "6200", "910", inParam.getString("TRADE_EPARCHY_CODE"));
        String creadtCode = "";
        if (IDataUtil.isNotEmpty(commPara))
        {
            creadtCode = commPara.getData(0).getString("PARA_CODE1");
        }

        if (("P".equals(planTypeCode) && "6200".equals(creadtCode)) || "7130".equals(tradeTypeCode))
        {

            IDataset userSvcInfos = UserSvcInfoQry.getUserSvcByUserIdAB(userId, userIda);
            if (IDataUtil.isEmpty(userSvcInfos))
            {
                return;
            }
            for (int i = 0, size = userSvcInfos.size(); i < size; i++)
            {
                String serviceId = userSvcInfos.getData(i).getString("SERVICE_ID");
                IDataset userSvc = UserSvcInfoQry.getSvcUserId(userId, serviceId);
                if ("910".equals(serviceId) || ("20".equals(serviceId) && userSvc.size() == 1))
                {
                    IDataset userSvcStates = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);
                    IData userSvcState = new DataMap();
                    if (IDataUtil.isNotEmpty(userSvcStates))
                    {
                        userSvcState.putAll(userSvcStates.getData(0));
                        userSvcState.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userSvcState.put("END_DATE", sysTime);
                        userSvcState.put("UPDATE_TIME", sysTime);
                        SvcStateTradeData userSvcStateTradeData = new SvcStateTradeData(userSvcState);
                        resultList.add(userSvcStateTradeData);
                    }
                    else
                    {
                        userSvcState.put("USER_ID", userId);
                        userSvcState.put("SERVICE_ID", serviceId);
                        userSvcState.put("MAIN_TAG", "0");
                        userSvcState.put("UPDATE_TIME", sysTime);
                    }
                    if ("7110".equals(tradeTypeCode) || "7220".equals(tradeTypeCode) || "7305".equals(tradeTypeCode) || "7130".equals(tradeTypeCode))
                    {
                        userSvcState.put("STATE_CODE", "5");
                        userSvcState.put("RSRV_STR5", "7");
                    }

                    if ("7303".equals(tradeTypeCode) || "7301".equals(tradeTypeCode) || "7304".equals(tradeTypeCode))
                    {
                        userSvcState.put("STATE_CODE", "0");
                        userSvcState.put("RSRV_STR5", "");
                    }
                    userSvcState.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    userSvcState.put("START_DATE", sysTime);
                    userSvcState.put("END_DATE", SysDateMgr.getTheLastTime());
                    SvcStateTradeData userSvcStateTradeData = new SvcStateTradeData(userSvcState);
                    resultList.add(userSvcStateTradeData);

                }
                IData userSvcData = new DataMap();
                userSvcData.putAll(userSvcInfos.getData(i));
                userSvcData.put("UPDATE_TIME", sysTime);
                userSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                SvcTradeData userSvcTradeData = new SvcTradeData(userSvcData);
                resultList.add(userSvcTradeData);

            }
        }
    }

    /**
     * 对TradeImpu成员资料的处理
     * 
     * @param data
     * @return
     * @author liuzz 2014-07-26 10:45
     * @throws Exception
     */
    public IDataset CreateGrpTradeImpu(IData inParam) throws Exception
    {
        GetOperationCode(inParam);
        List<BaseTradeData> retList = new ArrayList<BaseTradeData>();
        String dealFlag = inParam.getString("DEAL_FLAG");
        String lockFlag = "2"; // 1:呼入闭锁 2：呼出闭锁 3：呼入呼出闭锁
        String userId = inParam.getString("USER_ID");
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(userId);
        if (IDataUtil.isEmpty(impuInfo))
        {
            return null;
        }
        IData impuData = impuInfo.getData(0);
        String userType = impuData.getString("RSRV_STR1");
        String rsrv5 = impuData.getString("RSRV_STR5");
        if ("destroy".equals(dealFlag))
        {
            // 1、结束IMPU
            impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            impuData.put("END_DATE", SysDateMgr.getSysTime());

            ImpuTradeData impu = new ImpuTradeData(impuData);
            retList.add(impu);
            // 2、如果是融合总机成员 发送注销注销融合总机(成员)指令
            IDataset relations = RelaUUInfoQry.qryUU("", userId, "S3", null, null);
            if (IDataUtil.isNotEmpty(relations))
            {
                setRegTradeOther(userId, retList, "6301", "24", TRADE_MODIFY_TAG.DEL.getValue(), "", "CNTRX", "CNTRX成员");
            }

            if ("0".equals(userType))
            {
                // 3、other表发centrx 8171
                setRegTradeOther(userId, retList, "8171", "02", TRADE_MODIFY_TAG.DEL.getValue(), lockFlag, "CNTRX", "CNTRX成员");
            }
            if ("0".equals(userType) || ("1".equals(userType) && "1".equals(rsrv5)))
            {
                // 4、other表发ENUM 8173
                setRegTradeOther(userId, retList, "8173", "02", TRADE_MODIFY_TAG.DEL.getValue(), "", "ENUM", "ENUM成员");
                // 5、other表发HSS 8172
                setRegTradeOther(userId, retList, "8172", "02", TRADE_MODIFY_TAG.DEL.getValue(), "", "HSS", "HSS成员成员");
            }
        }
        else if ("pause".equals(dealFlag) && "0".equals(userType))
        {
            lockFlag = "3";
            setRegTradeOther(userId, retList, "8171", "04", TRADE_MODIFY_TAG.Add.getValue(), lockFlag, "CNTRX", "CNTRX成员");

        }
        else if ("back".equals(dealFlag) && "0".equals(userType))
        {
            lockFlag = "0";
            setRegTradeOther(userId, retList, "8171", "05", TRADE_MODIFY_TAG.Add.getValue(), lockFlag, "CNTRX", "CNTRX成员");
        }

        IDataset resulDataset = new DatasetList();
        IData data = new DataMap();
        data.put("GRP_TRADE_DATA", retList);
        resulDataset.add(data);
        return resulDataset;
    }

    /**
     * 作用：写other表，用来发报文用
     * 
     * @author liuzz 2014-07-26 10:45
     * @throws Exception
     */
    public void setRegTradeOther(String userId, List<BaseTradeData> resultList, String serviceId, String operCode, String modifyTag, String lockflag, String valueCode, String rsrvValue) throws Exception
    {

        IData centreData = new DataMap();
        centreData.put("USER_ID", userId);
        centreData.put("RSRV_VALUE_CODE", valueCode); // domain域
        centreData.put("RSRV_VALUE", rsrvValue);
        centreData.put("RSRV_STR9", serviceId); // 服务id
        centreData.put("RSRV_STR12", lockflag);
        centreData.put("RSRV_NUM1", "0");
        centreData.put("RSRV_NUM2", "0");
        centreData.put("RSRV_NUM3", "0");

        centreData.put("RSRV_STR11", operCode);
        centreData.put("OPER_CODE", operCode); // 操作类型
        centreData.put("MODIFY_TAG", modifyTag);
        centreData.put("START_DATE", SysDateMgr.getTomorrowTime());
        centreData.put("END_DATE", SysDateMgr.getSysTime());
        centreData.put("INST_ID", SeqMgr.getInstId());

        OtherTradeData otherData = new OtherTradeData(centreData);
        resultList.add(otherData);
    }
}
