package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;


import com.ailk.biz.service.BizRoute;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;


/**
 * @program: zd_hunan_order
 * @description: 集客大厅下产品变更需要发短信的都发一下短信
 * @author: zhangchengzhi
 * @create: 2018-12-25 15:38
 **/

public class JkdtFinishSms implements ITradeFinishAction {
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String trade_type_code = mainTrade.getString("TRADE_TYPE_CODE");
        String tradeId = mainTrade.getString("TRADE_ID");

//        集客大厅：1、套餐变更 2、业务暂停 3、业务恢复
//        产品暂停：2331
//        产品恢复：2332
//        修改产品资费：2333 
//        产品属性变更2337 
        if (StringUtils.equals(trade_type_code, "2331")) {
            changePast(mainTrade);
        } else if (StringUtils.equals(trade_type_code, "2332")) {
            changeContinue(mainTrade);
        } else if (StringUtils.equals(trade_type_code, "2337")||StringUtils.equals(trade_type_code, "2333")) {  
            changeDiscnt(mainTrade);                 
        }
    }

    /**
     * 修改产品资费调用此公共方法
     * @param mainTrade
     * @throws Exception
     */
    private void changeDiscnt(IData mainTrade) throws Exception {
        //集团V网走如下方法
        String productId = mainTrade.getString("PRODUCT_ID");
        String productSpecId = GrpCommonBean.productJKDTToMerch(productId, 0);
        if (StringUtils.equals("5001301", productSpecId)) {
            productSmsForVW(mainTrade, "CRM_SMS_GRP_JK_VW_001");
        }
    }

    /**
     * 产品暂停调用此公共方法
     * @param mainTrade
     * @throws Exception
     */
    private void changePast(IData mainTrade) throws Exception {
        //集团V网走如下方法
        String productId = mainTrade.getString("PRODUCT_ID");
        String productSpecId = GrpCommonBean.productJKDTToMerch(productId, 0);
        if (StringUtils.equals("5001301", productSpecId)) {
            productSmsForVW(mainTrade, "CRM_SMS_GRP_JK_VW_002");
        }
    }

    /**
     * 产品恢复调用此公共方法
     * @param mainTrade
     * @throws Exception
     */
    private void changeContinue(IData mainTrade) throws Exception {
        //集团V网走如下方法
        String productId = mainTrade.getString("PRODUCT_ID");
        String productSpecId = GrpCommonBean.productJKDTToMerch(productId, 0);
        if (StringUtils.equals("5001301", productSpecId)) {
            productSmsForVW(mainTrade, "CRM_SMS_GRP_JK_VW_003");
        }
    }


    /**
     * 集团V网产品发短信调用公共模版
     * @param mainTrade
     * @param tempId
     * @throws Exception
     */
    private void productSmsForVW(IData mainTrade, String tempId) throws Exception {
    	String tradeId = mainTrade.getString("TRADE_ID");//daidl
        String userId = mainTrade.getString("USER_ID");
        String productId = mainTrade.getString("PRODUCT_ID");
        String productSpecCode = GrpCommonBean.productJKDTToMerch(productId, 0);
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");

        //1 获取模版
        String templateContent = TemplateBean.getTemplate(tempId);
        if (StringUtils.isBlank(templateContent)) {
            return;
        }
        //1-1 资费变更模版
        if (StringUtils.equals(tempId, "CRM_SMS_GRP_JK_VW_001")) {
            //获取集团的资费
            String grpDiscntCode = "";
            IDataset grpDiscntInfos =getTradeDiscntByTradeId(tradeId,"0");//daidl
            for (int i = 0; i < grpDiscntInfos.size(); i++) {
                IData grpDiscntInfo = grpDiscntInfos.getData(i);
                String end_date = grpDiscntInfo.getString("END_DATE", "");
                String nextMonthFirstDay = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());
                if (IDataUtil.isNotEmpty(grpDiscntInfos) &&
                        SysDateMgr.compareTo(end_date, nextMonthFirstDay) > 0) {
                    grpDiscntCode = grpDiscntInfo.getString("DISCNT_CODE", "");
                }
            }
            String merchDiscnt = GrpCommonBean.productJKDTToMerch(grpDiscntCode, 1);
            if (StringUtils.isBlank(merchDiscnt)) {
                return;
            }
            IDataset prodCommparaList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9090",
                    productSpecCode, merchDiscnt);
            if (IDataUtil.isNotEmpty(prodCommparaList)) {
                String commParaMin = prodCommparaList.getData(0).getString("PARA_CODE2", "");// 属性值
                // 获取短信模板，拼短信内容
                templateContent = TemplateBean.getTemplate(tempId);
                templateContent = templateContent.replaceAll("@\\{MIN\\}", commParaMin);
            }
        }
        //1-2 产品暂停模版
        if (StringUtils.equals(tempId, "CRM_SMS_GRP_JK_VW_002")) {
            String month = SysDateMgr.getCurMonth();
            if("12".equals(month)){
            	month = "1";
            }else{
            	month = String.valueOf(Integer.parseInt(month) +1);
            }
            String day = "1";//SysDateMgr.getLastDateThisMonth();
            templateContent = templateContent.replaceAll("@\\{MONTH\\}", month);
            templateContent = templateContent.replaceAll("@\\{DAY\\}", day);
        }


        //2- 用集团User_Id，获取集团下所有的成员号码, 集团V网的号码发短信
        IDataset userRelationByUserIdA = RelaBBInfoQry.getUserExistsByUserIdA(userId, "V3");
        for (int i = 0, j = userRelationByUserIdA.size(); i < j; i++) {
            IData relation = userRelationByUserIdA.getData(i);
            //2-1如果不是V网则直接retrun
            if (IDataUtil.isEmpty(relation) ||!StringUtils.equals("V3", relation.getString("RELATION_TYPE_CODE"))) {
                continue;
            }

            String serialNumber = relation.getString("SERIAL_NUMBER_B");
            //插入短信表
            sendSMS(mainTrade, eparchyCode, serialNumber, userId, templateContent);
        }

    }

    /**
     * 发送短信
     * @param mainTrade
     * @param eparchyCode
     * @param serialNumber
     * @param userID
     * @param temp
     * @throws Exception
     */
    private static void sendSMS(IData mainTrade, String eparchyCode, String serialNumber, String userID, String temp) throws Exception {
        IData sendData = new DataMap();
        String sysdate = SysDateMgr.getSysTime();
        String smsNoticeId = SeqMgr.getSmsSendId();
        sendData.put("SMS_NOTICE_ID", smsNoticeId);
        sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
        sendData.put("EPARCHY_CODE", eparchyCode);
        sendData.put("RECV_OBJECT", serialNumber);// 需发短信的手机号
        sendData.put("RECV_ID", userID);
        sendData.put("NOTICE_CONTENT", temp);

        /*------------------------以下是原来写死的值，改用默认值--------------------------*/
        sendData.put("SEND_COUNT_CODE", "1");// 发送次数编码?
        sendData.put("REFERED_COUNT", "0");// 发送次数？
        sendData.put("CHAN_ID", "11");
        sendData.put("RECV_OBJECT_TYPE", "00");// 00手机号
        sendData.put("SMS_TYPE_CODE", "20");//
        sendData.put("SMS_KIND_CODE", "02");// 02与SMS_TYPE_CODE配套
        sendData.put("NOTICE_CONTENT_TYPE", "0");// 0指定内容发送
        sendData.put("FORCE_REFER_COUNT", "1");// 指定发送次数
        sendData.put("SMS_PRIORITY", "50");// 短信优先级
        sendData.put("REFER_TIME", sysdate);// 提交时间
        sendData.put("REFER_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID", ""));// 员工ID
        sendData.put("REFER_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID", ""));// 部门ID
        sendData.put("DEAL_TIME", sysdate);// 完成时间
        sendData.put("DEAL_STATE", "0");// 处理状态，0：已处理，15未处理
        sendData.put("SEND_OBJECT_CODE", "6");// 通知短信,见TD_B_SENDOBJECT
        sendData.put("SEND_TIME_CODE", "1");// 营销时间限制,见TD_B_SENDTIME
        sendData.put("REMARK", mainTrade.getString("REMARK", ""));// 备注

        /*------------------------以下是原来没有写入的值--------------------------*/
        sendData.put("BRAND_CODE", mainTrade.getString("BRAND_CODE", ""));
        sendData.put("IN_MODE_CODE", mainTrade.getString("IN_MODE_CODE", ""));// 接入方式编码
        sendData.put("SMS_NET_TAG", mainTrade.getString("SMS_NET_TAG", "0"));
        sendData.put("FORCE_OBJECT", mainTrade.getString("FORCE_OBJECT", ""));// 发送方号码
        sendData.put("FORCE_START_TIME", mainTrade.getString("FORCE_START_TIME", ""));// 指定起始时间
        sendData.put("FORCE_END_TIME", mainTrade.getString("FORCE_END_TIME", ""));// 指定终止时间
        sendData.put("DEAL_STAFFID", mainTrade.getString("TRADE_STAFF_ID", ""));// 完成员工
        sendData.put("DEAL_DEPARTID", mainTrade.getString("TRADE_DEPART_ID", ""));// 完成部门
        sendData.put("REVC1", mainTrade.getString("REVC1", ""));
        sendData.put("REVC2", mainTrade.getString("REVC2", ""));
        sendData.put("REVC3", mainTrade.getString("REVC3", ""));
        sendData.put("REVC4", mainTrade.getString("REVC4", ""));
        sendData.put("MONTH", sysdate.substring(5, 7));// 月份
        sendData.put("DAY", sysdate.substring(8, 10)); // 日期

        Dao.insert("TI_O_SMS", sendData);
    }

    
    /**通过trade_id 和modify_tag为0（新增）查tf_b_trade_discnt
     * daidl
     * 2019-3-5
     * **/
    public static IDataset getTradeDiscntByTradeId(String tradeId,String modifyTag) throws Exception
    {
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("MODIFY_TAG", modifyTag);
		
		StringBuilder sql=new StringBuilder();
		sql.append("select * from tf_b_trade_discnt t ");
		sql.append("WHERE 1 = 1 ");
	    sql.append("AND t.TRADE_ID = :TRADE_ID ");
	    sql.append("AND t.MODIFY_TAG = :MODIFY_TAG ");


	    return Dao.qryBySql(sql,param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
}
