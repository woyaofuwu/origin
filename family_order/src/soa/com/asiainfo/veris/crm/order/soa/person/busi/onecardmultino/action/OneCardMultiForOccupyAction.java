package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;

/**
 * 一卡多号 配置143改号业务的参数
 */
public class OneCardMultiForOccupyAction implements ITradeFinishAction {

    public void executeAction(IData mainTrade) throws Exception {
        //	String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        IDataset trade_relation_info = TradeRelaInfoQry.getTradeRelaByTradeIdRelaType(tradeId, "M2");

        if (!DataUtils.isEmpty(trade_relation_info)) {
            StringBuilder sbSerialNumberB = new StringBuilder();
            for (int i = 0, j = trade_relation_info.size(); i < j; i++) {
                IData relationTradeData = trade_relation_info.getData(i);
                if (!DataUtils.isEmpty(relationTradeData)) {
                    String user_id_b = relationTradeData.getString("USER_ID_B");
                    String modify = relationTradeData.getString("MODIFY_TAG");
                    // 当为虚拟副号码 ===》 user_id_b最后一位为0
                    if ("0".equals(user_id_b.substring(user_id_b.length() - 1))) {
                        // 获得关系台账的虚拟副号
                        String serialNumberB = relationTradeData.getString("SERIAL_NUMBER_B");
                        String orderno = relationTradeData.getString("ORDERNO");
//                        sbSerialNumberB.append(serialNumberB);
                        //判断对应的字符串中是否存在对应的副号码的信息，如果已经存在不做处理。
                        if (!sbSerialNumberB.toString().contains(serialNumberB)) {
                            //将副号码信息添加到副号码字符串信息中
                            sbSerialNumberB.append(serialNumberB).append(",");
                            IDataset uuinfos = OneCardMultiNoQry.qryRelationList(userId, "M2", serialNumberB, StringUtils.isBlank(orderno) ? null : orderno);
                            String msisdnType = getMsisdnTypeForUserRes(serialNumberB);

                            if (uuinfos != null && !uuinfos.isEmpty()) {
                                if (BofConst.MODIFY_TAG_ADD.equals(modify)) {//添加
                                    if (uuinfos.size() > 1) {
                                        continue;
                                    } else {
                                        //订购的时候不存在用户资料记录，调用资源接口
                                        //1、根据SERIALNUMBER获取TF_F_USER_RES表中RES_TYPE_CODE=0的记录；2、获取RSRV_STR2字段的值。如果值为0调用资源接口。
                                        if (StringUtils.isNotEmpty(msisdnType) && "0".equals(msisdnType)) {
                                            //调用资源接口
                                            ResCall.occupyVirtualSerialNumber(serialNumberB);
                                        }
                                    }
                                } 
                            }else if (BofConst.MODIFY_TAG_DEL.equals(modify)) { //删除
                            	uuinfos = OneCardMultiNoQry.qryRelationListAll(userId, "M2", serialNumberB, StringUtils.isBlank(orderno) ? null : orderno);
                            	if (uuinfos != null && !uuinfos.isEmpty()) {
                            	
	                                System.out.println("--OneCardMultiForOccupyAction--msisdnType="+msisdnType);
	
	                                if (StringUtils.isNotEmpty(msisdnType) && "0".equals(msisdnType)) {
	                                    ResCall.unbindVirtualSerialNumber(serialNumberB);
	                                }
                            	}
                            }
                        }
                    }
                }
            }
        }
    }

    private String getMsisdnTypeForUserRes(String serialNumberB) throws Exception {
        IDataset user_res = UserResInfoQry.getUserResBySelbySerialnremove(serialNumberB, "0");
        String msisdnType = "";
        if (user_res != null && !user_res.isEmpty()) {
            msisdnType = user_res.getData(0).getString("RSRV_STR2", "");
        }
        return msisdnType;
    }


}