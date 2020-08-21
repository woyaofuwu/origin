package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;


import java.util.Date;
import java.util.List;

import com.ailk.bizservice.callpf.auto.expression.function.SystemFunctions;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

/**
 * //开户绑定优惠，根据COMMPARA表param_attr=9226 的配置进行绑定 * 
 * @author chenxy3
 */
public class BindDiscntFromDiscntAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        // 关于新增3档随心选会员产品的需求,避免重复绑定优惠 add by wuhao5
        IData pageRequestData = btd.getRD().getPageRequestData();
        String dontBind = pageRequestData.getString("DONT_BIND","");
        if ("1".equals(dontBind)){
            return;
        }

       /* String prodId = "";//用户开户主产品。
        List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        ProductTradeData prodTradeData = new ProductTradeData();

        if (productTrade != null && productTrade.size() > 0) {
            for (ProductTradeData product : productTrade) {
                if (BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()) && "1".equals(product.getMainTag())) {
                    prodId = product.getProductId();
                    prodTradeData = product;
                }
            }
        }*/

        //CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        String UserId = uca.getUserId();//createPersonUserRD.getUca().getUser().getUserId();
        String orderdiscntCode = "";//用户新订购的优惠
        String orderdiscntCodeStarTime = "";//用户新订购的优惠开始时间
        
//        System.out.println("BindDiscntFromDiscntActionxxxxxxxxxxxxx33 " + btd);
        List<DiscntTradeData> orderDiscntTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
//        System.out.println("BindDiscntFromDiscntActionxxxxxxxxxxxxx35 " + orderDiscntTradeList);

        if (orderDiscntTradeList != null && orderDiscntTradeList.size() > 0) {
            for (int ij = 0; ij < orderDiscntTradeList.size(); ij++) {
                DiscntTradeData orderDiscntTradeData = orderDiscntTradeList.get(ij);           
                if (BofConst.MODIFY_TAG_ADD.equals(orderDiscntTradeData.getModifyTag())) {
                    orderdiscntCode = orderDiscntTradeData.getDiscntCode();
                    orderdiscntCodeStarTime = orderDiscntTradeData.getStartDate();
                    
                    IDataset commparaInfos9226 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9226", orderdiscntCode, btd.getRD().getUca().getUserEparchyCode());
//                    System.out.println("BindDiscntFromDiscntActionxxxxxxxxxxxxx42 "+commparaInfos9226);

                    if (IDataUtil.isNotEmpty(commparaInfos9226)) {
                        for (int i = 0; i < commparaInfos9226.size(); i++) {
                            String discntCode = commparaInfos9226.getData(i).getString("PARA_CODE1");//para_code1=后台绑定优惠
                            String continuous = commparaInfos9226.getData(i).getString("PARA_CODE2", "");//para_code2=绑定期限(数字代表几个月，null则到2050）
                            String effTime = commparaInfos9226.getData(i).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效 2-绝对时间
                            String giftTime = commparaInfos9226.getData(i).getString("PARA_CODE4", "");//para_code4=1 该条配置失效
                            String endTime = commparaInfos9226.getData(i).getString("PARA_CODE5", "");//para_code5=绝对时间
                            String isaccumulation = commparaInfos9226.getData(i).getString("PARA_CODE6", "");//p6=0,多次变更时要累加;
                            String discntCode7 = commparaInfos9226.getData(i).getString("PARA_CODE7", "");//para_code7=后台绑定多个优惠校验
                            String discntCode8 = commparaInfos9226.getData(i).getString("PARA_CODE8", "");//REQ201810100028 关于《校园套餐月租费打折活动》判断
                            String smsContent = commparaInfos9226.getData(i).getString("PARA_CODE20", "");// PARA_CODE20=开通短信内容

                            boolean flag = true;//允许办理条件
                            
                            if ("1".equals(giftTime)) {
                                flag = false;//1该条配置失效
                                continue;
                            }
                            
                            //REQ201810100028 关于《校园套餐月租费打折活动》校验：用户开户30天以上（含30天）
                            if ("Y".equals(discntCode8)) {
                            	IData param = new DataMap();
                            	param.put("USER_ID", UserId);
                            	IDataset checkUserInfos = checkIfUserOpen30Day(param);
                            	if (checkUserInfos == null || checkUserInfos.size() == 0) {
                            		flag = false;
                    			} 
                            }
                            
                            //2、本次办理的优惠如果存在该优惠，则不再绑定。
                            List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
                            for (DiscntTradeData discntTradeData : discntTradeDatas) {
                                if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag())) {
                                    String discntNew = discntTradeData.getElementId();//本次新办的该种优惠                                    
                                    if (discntNew.equals(discntCode)) {
                                        flag = false;
                                        break;
                                    }
                                    
                                    
                                 
                                }
                            }
                            
                            //用户办理业务前已经订购了该优惠，则不赠送
                            if (flag) {
                                IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUser(UserId, discntCode);
                                if (userDiscs != null && userDiscs.size() > 0) {
                                    flag = false;
                                    break;
                                }
                                //用户办理过para_code7将不再赠送优惠
                                SystemFunctions str =new SystemFunctions();
                                if(!str.isEmpty(discntCode7)){
                                	String[] s= discntCode7.split(";");
                                	for(String discnts:s){
                                		IDataset userDisc = UserDiscntInfoQry.getAllDiscntByUser_2(UserId, discnts);
                                        if (userDisc != null && userDisc.size() > 0) {
                                            flag = false;
                                            break;
                                        }
                                	}
                                	IDataset userDisc2 = UserDiscntInfoQry.getAllDiscntByUser_2(UserId, orderdiscntCode);
                                	 if (userDisc2 != null && userDisc2.size() > 0) {
                                         flag = false;
                                         break;
                                     }
                                }
                                //para_code8以时间配置控制是否绑定PARA_CODE1优惠
//                                if(!str.isEmpty(discntCode8)){
//                            		SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
//                            		Date date =format.parse(discntCode8);
//                            		Date now =new Date();
//                            	    if(date.before(now)){
//                            	    	 flag = false;
//                            	    }
//                                }
                            }
                            
                            //3、用户原来如存在该优惠，如p6=0，则要累计赠送，p6未其他值，则不赠送
                            if (flag) {
                                IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUser_2(UserId, discntCode);
                                if (userDiscs != null && userDiscs.size() > 0) {
                                    if(!isaccumulation.trim().equals("0")){
                                        flag = false;
                                        break;
                                    }else{
                                        int accumulationNum = 0;
                                        for (int j = 0; j < userDiscs.size(); j++) {
                                            IData data = userDiscs.getData(j);
                                            String startdate = data.getString("START_DATE");
                                            String enddate = data.getString("END_DATE");
                                            String discntInst = data.getString("INST_ID");
                                            
                                            IDataset userofferrelDs = UserOfferRelInfoQry.qryUserAllOfferRelByUserIdDiscntCode_1(UserId, discntCode, discntInst);
                                            if (userofferrelDs == null || userofferrelDs.size() == 0) {//赠送的优惠不会往 tf_f_user_offer_rel表里写记录。主动订购的则会写入该表
                                                int monthsbetween = SysDateMgr.monthsBetween(startdate, enddate)+1;
                                                accumulationNum += monthsbetween;
                                            }
                                        }
                                        if(accumulationNum>=Integer.parseInt(continuous)){
                                            flag = false;
                                            break;
                                        }else{
                                            continuous = String.valueOf(Integer.parseInt(continuous)-accumulationNum);
                                        }
                                    }                                   
                                }
                            }

                            if (flag) {
                                String startDate = "";
                                String endData = "";
                                if ("0".equals(effTime)) {//0-立即生效
                                    startDate = SysDateMgr.getSysTime();  
                                    if (!"".equals(continuous) && !"null".equals(continuous)) {
                                        endData = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous));//绑定期限(数字代表几个月，null则到2050）
                                    } else {
                                        endData = SysDateMgr.END_DATE_FOREVER;
                                    }
                                } else if ("1".equals(effTime)) {
                                    startDate = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
                                    if (!"".equals(continuous) && !"null".equals(continuous)) {
                                        endData = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous) + 1);//绑定期限(数字代表几个月，null则到2050）
                                    } else {
                                        endData = SysDateMgr.END_DATE_FOREVER;
                                    }
                                } else if ("2".equals(effTime)) {
                                    startDate = SysDateMgr.getSysTime(); 
                                    if (StringUtils.isNotBlank(endTime)) {
                                        endData = endTime;
                                    } else {
                                        endData = SysDateMgr.END_DATE_FOREVER;
                                    }
                                } else if ("4".equals(effTime)) {
                                	startDate = orderdiscntCodeStarTime;
                                	endData = SysDateMgr.endDateOffset(orderdiscntCodeStarTime, continuous, "3");;
                                } else {
                                    startDate = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
                                    if (StringUtils.isNotBlank(endTime)) {
                                        endData = endTime;
                                    } else {
                                        endData = SysDateMgr.END_DATE_FOREVER;
                                    }
                                }                                                              

                                DiscntTradeData newDiscnt = new DiscntTradeData();
                                newDiscnt.setUserId(UserId);
                                newDiscnt.setProductId("-1");
                                newDiscnt.setPackageId("-1");
                                newDiscnt.setElementId(discntCode);
                                newDiscnt.setInstId(SeqMgr.getInstId());
                                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                newDiscnt.setStartDate(startDate);
                                newDiscnt.setEndDate(endData);
                                newDiscnt.setRemark("根据业务类型及主产品后台绑定优惠");
//                                System.out.println("BindDiscntFromDiscntActionxxxxxxxxxxxxx123 "+newDiscnt);

                                btd.add(uca.getSerialNumber(), newDiscnt);

                                /*
                                //此处prodId与packId为-1，需要手动新增OfferRel
                                OfferRelTradeData offerRel = new OfferRelTradeData();
                                offerRel.setInstId(SeqMgr.getInstId());
                                offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                offerRel.setOfferInsId(prodTradeData.getInstId());
                                offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                                offerRel.setOfferCode(prodTradeData.getProductId());
                                offerRel.setUserId(prodTradeData.getUserId());
                                offerRel.setRelOfferInsId(newDiscnt.getInstId());
                                offerRel.setRelOfferCode(newDiscnt.getElementId());
                                offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
                                offerRel.setRelType(BofConst.OFFER_REL_TYPE_LINK);//连带关系
                                offerRel.setStartDate(newDiscnt.getStartDate());
                                offerRel.setEndDate(newDiscnt.getEndDate());
                                offerRel.setRelUserId(newDiscnt.getUserId());
                                offerRel.setGroupId("-1");
                                System.out.println("BindDiscntFromDiscntActionxxxxxxxxxxxxx162 "+offerRel);
                                btd.add(uca.getSerialNumber(), offerRel);*/
                                String serialNumber = uca.getSerialNumber();
                                if(StringUtils.isNotBlank(smsContent)){
                                    String date = startDate;
                                    Date tempDate = SysDateMgr.string2Date(date, SysDateMgr.PATTERN_STAND_YYYYMMDD);
                                    String chinaDate = SysDateMgr.date2String(tempDate, SysDateMgr.PATTERN_CHINA_DATE);
                                    smsContent = smsContent.replace("DATE_TIME", chinaDate);

                                    IData ObjectsmsData = new DataMap(); // 短信数据
                                    ObjectsmsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
                                    ObjectsmsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
                                    ObjectsmsData.put("FORCE_OBJECT", "10086");// 发送对象
                                    ObjectsmsData.put("RECV_OBJECT", serialNumber);// 接收对象
                                    ObjectsmsData.put("NOTICE_CONTENT", smsContent);// 短信内容
                                    PerSmsAction.insTradeSMS(btd, ObjectsmsData);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 号码必须是激活超过30天的  OPEN_30_DAY Y:30天外  N:30天内
     * */
    public IDataset checkIfUserOpen30Day(IData params) throws Exception{
    	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.*,t.rowid from tf_F_user t where t.USER_ID= :USER_ID and t.open_date <= sysdate -30 and t.remove_tag='0' "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
}
