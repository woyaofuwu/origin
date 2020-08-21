package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.ChangeSvcStateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.EmergencyOpenReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeSvcStateTrade.java
 * @Description:
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-7 下午2:01:45
 */

public class ChangeSvcStateTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangeSvcStateComm changeSvcStateComm = new ChangeSvcStateComm();
        changeSvcStateComm.getSvcStateChangeTrade(btd); // 获取用户服务状态变更订单
        changeSvcStateComm.modifyMainSvcStateByUserId(btd);// 修改用户主体服务状态和最后停机时间
        String tradeTypeCode = btd.getTradeTypeCode();
        // 报开业务，清理扩展指段的标记，放到最后
        if ("133".equals(tradeTypeCode))
        {
            changeSvcStateComm.clearRsrvSpecTagInfo(btd);
        }

        if ("492".equals(tradeTypeCode))
        {
            MainTradeData mainTradeData = btd.getMainTradeData();
            EmergencyOpenReqData reqData = (EmergencyOpenReqData) btd.getRD();
            IData rdData = reqData.getPageRequestData();
            String strVip = rdData.getString("VIP_CLASS_ID", "0");
            mainTradeData.setRsrvStr4(strVip);
            mainTradeData.setRsrvStr3(reqData.getOpenHours());
        }
        
        this.modifyMainTrade(btd);
        this.genPayRelationTrade(btd);
    }

    private void modifyMainTrade(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTradeData = btd.getMainTradeData();
        
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.equals("7301", tradeTypeCode) || StringUtils.equals("7303", tradeTypeCode)// 开机类
                || StringUtils.equals("7304", tradeTypeCode) || StringUtils.equals("7313", tradeTypeCode) || StringUtils.equals("7317", tradeTypeCode) || StringUtils.equals("7801", tradeTypeCode)
                || StringUtils.equals("7904", tradeTypeCode))
        {// 信控开机
            mainTradeData.setSubscribeType("201");
        }
        else if (StringUtils.equals("7314", tradeTypeCode) || StringUtils.equals("7311", tradeTypeCode) || StringUtils.equals("7316", tradeTypeCode) || StringUtils.equals("7210", tradeTypeCode) || StringUtils.equals("7101", tradeTypeCode)
                || StringUtils.equals("7110", tradeTypeCode) || StringUtils.equals("7220", tradeTypeCode) || StringUtils.equals("7122", tradeTypeCode) || StringUtils.equals("7312", tradeTypeCode) || StringUtils.equals("7305", tradeTypeCode)
                || StringUtils.equals("7315", tradeTypeCode) || StringUtils.equals("7121", tradeTypeCode) || StringUtils.equals("7802", tradeTypeCode) || StringUtils.equals("7803", tradeTypeCode) || StringUtils.equals("45", tradeTypeCode)
                || StringUtils.equals("7901", tradeTypeCode)|| StringUtils.equals("7902", tradeTypeCode)
                || StringUtils.equals("7903", tradeTypeCode))
        {
            mainTradeData.setSubscribeType("200");
            //add by zhangxing3 for REQ201906040011关于非实名关停号码自动删除一证五号平台数据的需求:用户非实名停机
            if(StringUtils.equals("7220", tradeTypeCode) && !"".equals(btd.getRD().getPageRequestData().getString("TRADE_ID",""))){
                String tradeId = btd.getRD().getPageRequestData().getString("TRADE_ID","");
                if(tradeId.startsWith("72"))
                {
                    mainTradeData.setRsrvStr9("StopNotRealName");
                    mainTradeData.setRsrvStr10("用户非实名停机");

                }
            }
            //add by zhangxing3 for REQ201906040011关于非实名关停号码自动删除一证五号平台数据的需求:用户非实名停机
        }

        if (StringUtils.equals("44", tradeTypeCode) || StringUtils.equals("46", tradeTypeCode))
        {
            mainTradeData.setExecTime(SysDateMgr.END_DATE_FOREVER);
            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
            mainTradeData.setRemark(tradeTypeName);
            mainTradeData.setSubscribeType("200");
        }
        else if (StringUtils.equals("43", tradeTypeCode))
        {
            IDataset commparaDataset = CommparaInfoQry.getCommpara("CSM", "2013", "HAIN", mainTradeData.getEparchyCode());
            if (IDataUtil.isEmpty(commparaDataset))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "查询COMMPARA参数表PARAM_ATTR=2013异常！");
            }
            String paraCode1 = commparaDataset.getData(0).getString("PARA_CODE1");
            if (StringUtils.isEmpty(paraCode1))
            {
                paraCode1 = "18000";// 默认为5小时后执行
            }
            int delayHours = Integer.parseInt(paraCode1) / 3600;
            String dealayTime = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), delayHours);
            mainTradeData.setExecTime(dealayTime);
            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
            mainTradeData.setRemark(tradeTypeName);
            mainTradeData.setSubscribeType("200");
        }
        
        //防止信控同一秒内发起两笔信控工单造成完工错乱，如果存在执行时间一样的信控工单存在，则后来的信控工单执行时间加一秒
        if("200".equals(mainTradeData.getSubscribeType())||"201".equals(mainTradeData.getSubscribeType())){
        	String nowExecTime = btd.getMainTradeData().getExecTime();
        	//nowExecTime = "2016-01-07 10:59:13";
        	String userId = btd.getMainTradeData().getUserId();
        	IDataset results = TradeInfoQry.qryCreditTradeInfo(userId, nowExecTime);
        	if(IDataUtil.isNotEmpty(results)&&results.size()>0){
        		String execTime =SysDateMgr.getNextSecond(nowExecTime);
        		mainTradeData.setExecTime(execTime);
        		mainTradeData.setRemark(mainTradeData.getRemark() + "【执行时间加1秒】");
        	}
        }
        if (StringUtils.equals("131", tradeTypeCode))
        {
            ChangeSvcStateReqData changeSvcStateBrd = (ChangeSvcStateReqData) btd.getRD();
            
            //是否关联停宽带
            mainTradeData.setRsrvStr1(changeSvcStateBrd.getIsStopWide());
            
            //是否渠道为一键停机
            if("M".equals(btd.getRD().getPageRequestData().getString("OPR_SOURCE"))){
                mainTradeData.setRemark("一键停机");
                btd.getRD().setCheckMode("M");
            }
        }

        if(StringUtils.equals("131", tradeTypeCode) || StringUtils.equals("133", tradeTypeCode)) {
            IData pageParam = btd.getRD().getPageRequestData();
            if("1".equals(pageParam.getString("KQ_FLAG"))) {
                mainTradeData.setRsrvStr10("KQFW");//跨区停复机标记
            }
        }
    }
    /**
     * 付费关系台帐处理
     * @param btd
     * @throws Exception
     * @author chenzg
     * @date 2018-4-12
     */
    private void genPayRelationTrade(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        if("7220".equals(tradeTypeCode) || "7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7317".equals(tradeTypeCode)){
        	//取当前用户的默认付费关系
        	IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(btd.getRD().getUca().getUserId());
        	if(IDataUtil.isNotEmpty(mainPayRelations)){
        		String mainAcctId = mainPayRelations.getString("ACCT_ID", "-1");
        		//欠费停机暂停统付关系
        		if("7220".equals(tradeTypeCode)){
        			//取账户对应的统付关系(和商务融合产品包)
        			IDataset payRelas = PayRelaInfoQry.queryUserPgPayreByAcctId(mainAcctId);
        			if(IDataUtil.isNotEmpty(payRelas)){
        				for(int i=0;i<payRelas.size();i++){
        					IData each = payRelas.getData(i);
        				    PayRelationTradeData payTd = new PayRelationTradeData(each);
        				    payTd.setEndCycleId(DiversifyAcctUtil.getLastDayThisAcct(btd.getRD().getUca().getUserId()).substring(0, 10).replace("-", ""));
        				    payTd.setRsrvStr8(each.getString("END_CYCLE_ID"));//记录原来的结束账期
        				    payTd.setRsrvStr10("PGCREDIT_STOP");			  //标识为欠费停机暂停
        				    payTd.setRemark("欠费停机暂停和商务融合产品统付关系");
        				    payTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        				    btd.add(btd.getRD().getUca().getSerialNumber(), payTd);
        				}
        			}
        		}
        		//缴费开机7301、7303和7317
        		else if("7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7317".equals(tradeTypeCode)){
        			//取可以恢复的统付关系
        			IDataset payRelas = PayRelaInfoQry.queryUserPgPayreForRestartByAcctId(mainAcctId);
        			if(IDataUtil.isNotEmpty(payRelas)){
        				for(int i=0;i<payRelas.size();i++){
        					IData each = payRelas.getData(i);
        				    PayRelationTradeData payTd = new PayRelationTradeData(each);
        				    payTd.setEndCycleId(payTd.getRsrvStr8());
        				    payTd.setRsrvStr10("");	//清空暂停标识
        				    payTd.setRemark("缴费开机恢复和商务融合产品统付关系");
        				    payTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
        				    btd.add(btd.getRD().getUca().getSerialNumber(), payTd);
        				}
        			}
        		}
        	}
        }
    }
}
