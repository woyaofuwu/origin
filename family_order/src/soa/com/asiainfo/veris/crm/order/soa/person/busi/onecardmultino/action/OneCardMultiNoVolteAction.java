package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;

public class OneCardMultiNoVolteAction implements ITradeAction
{
    public static final String OPER_CODE_APPLY = "06";
    public static final String OPER_CODE_CANCEL = "07";

    public static final String IN_MODE_CODE_CRM = "0";
    public static final String IN_MODE_CODE_SMS = "5";
    public static final String IN_MODE_CODE_IBOSS = "6";
    protected static Logger log = Logger.getLogger(OneCardMultiNoVolteAction.class);
    
    public void executeAction(BusiTradeData btd) throws Exception
    {
        log.debug("*********input*********************");
        OneCardMultiNoReqData rd = (OneCardMultiNoReqData) btd.getRD();
        String oprCode = "06";
        MainTradeData mtd = btd.getMainTradeData();
        String category = "";
        String useridB = "";
        List<RelationTradeData> tradeRelations = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
        if (CollectionUtils.isNotEmpty(tradeRelations)) {
            RelationTradeData relaTrade = tradeRelations.get(0);
            useridB = relaTrade.getUserIdB();

            category = useridB.substring(useridB.length() - 1);
            String modtag = relaTrade.getModifyTag();
            log.debug("*********modtag*********************" + modtag);
            if ("1".equals(modtag)) {
                oprCode = "07";
            }
        }
        log.debug("*********useridB*********************" + useridB);
        log.debug("*********category*********************" + category);
        String rsrvStr5 = "";
        String rsrvStr6 = "";
        String rsrvStr7 = "";
        String rsrvStr8 = "";
        String rsrvStr10 = "";
        // String category = rd.getCategory();
        String sna = rd.getSerialNumber();
        log.debug("*********sna*********************" + sna);
        String snb = rd.getSerial_number_b();
        log.debug("*********snb*********************" + snb);
        IData snaInfo = UcaInfoQry.qryUserInfoBySn(sna);
        log.debug("*********snaInfo*********************" + snaInfo);
        
        String useridA = snaInfo.getString("USER_ID");
        log.debug("*********useridA*********************" + useridA);
        
        IDataset userVolteA = UserSvcInfoQry.getSvcUserId(useridA, "190");
        log.debug("*********userVolteA*********************" + userVolteA);
        IDataset asynInfo = RelaUUInfoQry.qryRelaUUByUserIdA(useridA, "M2");
        log.debug("*********asynInfo*********************" + asynInfo);

        String imsia = "";
        IDataset userAResInfos = UserResInfoQry.getUserResBySelbySerialnremove(sna, "1");// 查sim卡
        if (IDataUtil.isNotEmpty(userAResInfos)) {// 虚拟用户是没有该资料的
            imsia = userAResInfos.getData(0).getString("IMSI", "0");
        }
       
        String foluserid = "";
        String imsib = "";
       
        IData snbInfo =new DataMap();
		snbInfo = UcaInfoQry.qryUserInfoBySn(snb);
        log.debug("*********snbInfo*********************" + snbInfo);

		if (IDataUtil.isNotEmpty(snbInfo)) {
			foluserid = snbInfo.getString("USER_ID");
		}
        IDataset userBResInfos = UserResInfoQry.getUserResBySelbySerialnremove(snb, "1");// 查sim卡
        if (IDataUtil.isNotEmpty(userBResInfos)) {// 虚拟用户是没有该资料的
            imsib = userBResInfos.getData(0).getString("IMSI", "0");
        }
	

        IData snbInput = new DataMap();
        snbInput.put("CATEGORY", category);
        snbInput.put("FOLLOW_MSISDN", snb);
        snbInput.put("SERIAL_NUMBER", snb);
        snbInput.put("SERIAL_NUMBER_B", sna);
        snbInput.put("NEW_SERIAL_NUMBER", snb);
        snbInput.put("ORDERNO", "1");
        snbInput.put("FORCE_OBJECT", "1");
        snbInput.put("FLAG", "1");
        snbInput.put("INHERIT", "1");
        snbInput.put("ORP_CODE", oprCode);
        snbInput.put("MSISDN", sna);
        boolean isSnbVolte = false;

        if (OPER_CODE_APPLY.equals(oprCode)) {
            if (IDataUtil.isNotEmpty(snbInfo)) {

                if ("1".equals(category)) {//本省实体副号码获取副号码volte信息
                    IDataset userVolteB = UserSvcInfoQry.getSvcUserId(foluserid, "190");
                    log.debug("*********OPER_CODE_APPLY*********************" + userVolteB);
                    if (IDataUtil.isNotEmpty(userVolteA)) {//主号是VOLTE用户
                        if (IDataUtil.isEmpty(asynInfo)) {//主号非和多号用户
                            if (IDataUtil.isNotEmpty(userVolteB)) {//实体副号码是VOLTE用户
                                rsrvStr5 = "MAIN_VOLTE";
                                rsrvStr7 = imsib;
                                rsrvStr10 = imsia;

                                //重新生成3797的副号码工单信息  add by 20180409
                                isSnbVolte = true;
                            } else {
                                rsrvStr5 = "MAIN_VOLTE";
                                rsrvStr10 = imsia;
                            }
//                            rsrvStr6 = "SAME_PROV_FOLLOW";//本省副号码
                        } else {
                            //主号是和多号用户
                            if (IDataUtil.isNotEmpty(userVolteA)) {//实体副号码是VOLTE用户
                                //重新生成3797的副号码工单信息  add by 20180409
                                isSnbVolte = true;
                            }
                        }
                    } else {//主号不是VOLTE用户
                        if (IDataUtil.isNotEmpty(userVolteB)) {//实体副号码是VOLTE用户
                            //重新生成3797的副号码工单信息  add by 20180409
                            isSnbVolte = true;
                        }
                    }
                } else {//本省虚拟副号码
                    if (IDataUtil.isNotEmpty(userVolteA)) {//主号是VOLTE用户
                        if (IDataUtil.isEmpty(asynInfo)) {//主号非和多号用户
                            rsrvStr5 = "MAIN_VOLTE";
                            rsrvStr6 = "SAME_PROV_FOLLOW";//本省副号码
                            rsrvStr10 = imsia;
                        }
                    }
                }
            } else {//跨省副号码
                if (IDataUtil.isNotEmpty(userVolteA)) {//主号是VOLTE用户
                    if (IDataUtil.isEmpty(asynInfo)) {//主号非和多号用户
                        rsrvStr5 = "MAIN_VOLTE";
                        rsrvStr6 = "TRANS_PROV_FOLLOW";//跨省副号码
                        rsrvStr10 = imsia;
                    }
                }
            }
        } else if (OPER_CODE_CANCEL.equals(oprCode)) {
            if (IDataUtil.isNotEmpty(snbInfo)) {

                if ("1".equals(category)) {//本省实体副号码获取副号码volte信息
                    IDataset userVolteB = UserSvcInfoQry.getSvcUserId(foluserid, "190");
                    log.debug("*********OPER_CODE_CANCEL*********************" + userVolteB);
                    if (IDataUtil.isNotEmpty(userVolteA)) {//主号是VOLTE用户
                        if (IDataUtil.isNotEmpty(asynInfo) && asynInfo.size() == 1) {//最后一个副号码取消
                            if (IDataUtil.isNotEmpty(userVolteB)) {//副号码是VOLTE用户
//                                rsrvStr5 = "ALL_VOLTE";
                                rsrvStr5 = "MAIN_VOLTE";
                                rsrvStr7 = imsib;
                                rsrvStr10 = imsia;

                                //重新生成3797的副号码工单信息  add by 20180409
                                isSnbVolte = true;
                            } else {
                                rsrvStr5 = "MAIN_VOLTE";
                                rsrvStr10 = imsia;
                            }
                            rsrvStr6 = "SAME_PROV_FOLLOW";//本省副号码
                            rsrvStr8 = "MAIN_VOLTE_CC";
                        } else {//不是最后一个副号码取消
                            if (IDataUtil.isNotEmpty(userVolteB)) {//副号码是VOLTE用户
                                //重新生成3797的副号码工单信息  add by 20180409
                                isSnbVolte = true;
                            }
                        }
                    } else {//主号码不是VOLTE用户
                        if (IDataUtil.isNotEmpty(userVolteB)) {//副号码是VOLTE用户
                            //重新生成3797的副号码工单信息  add by 20180409
                            isSnbVolte = true;

                        }
                    }
                } else {//虚拟副号码
                    if (IDataUtil.isNotEmpty(userVolteA)) {//主号是VOLTE用户
                        if (IDataUtil.isNotEmpty(asynInfo) && asynInfo.size() == 1) {//最后一个副号码取消
                            rsrvStr5 = "MAIN_VOLTE";
                            rsrvStr6 = "SAME_PROV_FOLLOW";//本省副号码
                            rsrvStr8 = "MAIN_VOLTE_CC";
                            rsrvStr10 = imsia;
                        }
                    }
                }
            } else {//跨省副号码
                if (IDataUtil.isNotEmpty(userVolteA)) {//主号是VOLTE用户
                    if (IDataUtil.isNotEmpty(asynInfo) && asynInfo.size() == 1) {//最后一个副号码取消
                        rsrvStr5 = "MAIN_VOLTE";
                        rsrvStr6 = "TRANS_PROV_FOLLOW";//跨省副号码
                        rsrvStr8 = "MAIN_VOLTE_CC";
                        rsrvStr10 = imsia;
                    }
                }
            }
        }
        mtd.setRsrvStr5(rsrvStr5);
        mtd.setRsrvStr6(rsrvStr6);
        mtd.setRsrvStr7(rsrvStr7);
        mtd.setRsrvStr8(rsrvStr8);
        mtd.setRsrvStr10(rsrvStr10);

        //生成副号码的3797工单给服开发指令
        if(isSnbVolte){
            RegVolteTrade(snbInput);
        }
    }
    
    /**
     * 实体副号码生成对应的3797工单提供给服开处理
     *
     * @param input
     * @throws Exception
     */
    private void RegVolteTrade(IData input) throws Exception {
        String snb = input.getString("FOLLOW_MSISDN");
        String category = input.getString("CATEGORY");
        if ("1".equals(category)) {
        	if(("6".equals(CSBizBean.getVisit().getInModeCode()) && "06".equals(input.getString("ORP_CODE"))) || "07".equals(input.getString("ORP_CODE"))){
                CSAppCall.call("SS.OneCardMospFollowRegSVC.tradeReg", input);
            }
        }
    }
}
