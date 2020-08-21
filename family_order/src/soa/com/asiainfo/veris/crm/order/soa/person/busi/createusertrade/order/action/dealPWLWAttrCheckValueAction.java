package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class dealPWLWAttrCheckValueAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

        System.out.println("dealPWLWAttrCheckValueAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx34 " + btd);

        List<MainTradeData> mainTradeInfos = btd.get("TF_B_TRADE");

        System.out.println("dealPWLWAttrCheckValueAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx38 " + mainTradeInfos);
        if (mainTradeInfos != null && mainTradeInfos.size() > 0) {
            MainTradeData maintradedata = mainTradeInfos.get(0);
            
            String brandCode = maintradedata.getBrandCode();
            System.out.println("dealPWLWAttrAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx43 " + brandCode);
            
            if (brandCode != null && "PWLW".equals(brandCode)) {//如果是物联网的才进行操作

                //"TF_B_TRADE_OFFER_REL":{"REL_TYPE":"C","MODIFY_TAG":"0","REL_OFFER_TYPE":"D","USER_ID":"1117081034057672","REL_OFFER_CODE":"20122160","END_DATE":"2050-12-31 23:59:59.0","START_DATE":"2017-08-10 10:13:24","OFFER_TYPE":"P","INST_ID":"1117081087399412","GROUP_ID":"70000005","OFFER_CODE":"20120706","OFFER_INS_ID":"1117081087399389","REL_USER_ID":"1117081034057672","REMARK":null,"REL_OFFER_INS_ID":"1117081087399404"}
                List<OfferRelTradeData> tf_b_trade_offer_rel_infos = btd.get("TF_B_TRADE_OFFER_REL");
                System.out.println("dealPWLWAttrCheckValueAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx48 " + tf_b_trade_offer_rel_infos);
                if (tf_b_trade_offer_rel_infos != null && tf_b_trade_offer_rel_infos.size() > 0) {
                    for (OfferRelTradeData offerRelTradeData : tf_b_trade_offer_rel_infos) {
                        System.out.println("dealPWLWAttrCheckValueAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx51 " + offerRelTradeData);
                        
                        String groupId = offerRelTradeData.getGroupId();
                        String modifytag_offer = offerRelTradeData.getModifyTag();
                        String discntCode = offerRelTradeData.getRelOfferCode();
                        if (groupId != null && (BofConst.MODIFY_TAG_ADD.equals(modifytag_offer) || BofConst.MODIFY_TAG_UPD.equals(modifytag_offer))) {//判断是否是 全国通用或定向包，本地通用或定向包， 如果是该包下的套餐，则需校验套餐的7个属性

                            //70000005 全国通用流量产品包(可选) ;70000008  全国定向流量产品包(可选) ; 70000009  本地通用流量产品包(可选) ; 70000011  本地定向流量产品包(可选)
                            if (groupId.equals("70000005") || groupId.equals("70000008") || groupId.equals("70000009") || groupId.equals("70000011")) {

                                //"TF_B_TRADE_ATTR":{"MODIFY_TAG":"0","RSRV_NUM4":null,"RSRV_NUM5":null,"END_DATE":"2050-12-31 23:59:59","RSRV_NUM1":"20122160","USER_ID":"1117081034057672","RSRV_NUM2":null,"RSRV_NUM3":null,"INST_ID":"1117081087399405","REMARK":null,"RELA_INST_ID":"1117081087399404","ELEMENT_ID":"20122160","START_DATE":"2017-08-10 10:13:24","RSRV_DATE3":null,"ATTR_CODE":"ApprovalNum","RSRV_DATE2":null,"INST_TYPE":"D","RSRV_DATE1":null,"RSRV_STR5":null,"ATTR_VALUE":"556757","RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,"RSRV_STR2":null,"RSRV_TAG2":null,"RSRV_TAG3":null,"RSRV_TAG1":null}                                
                                List<AttrTradeData> attrTradeDatas = btd.get("TF_B_TRADE_ATTR");
                                System.out.println("dealPWLWAttrCheckValueAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx64 " + attrTradeDatas);
                                if (attrTradeDatas != null && attrTradeDatas.size() > 0) {

                                    AttrTradeData serv99011021 = null;//[99011021]物联网专用数据通信服务(可选)
                                    AttrTradeData serv99011022 = null;//[99011022]通用流量4G_GPRS服务
                                    IData attrMap = new DataMap();
                                    for (AttrTradeData attrTradeData : attrTradeDatas) {
                                        if (attrTradeData.getRsrvNum1().equals("99011021")) {
                                            serv99011021 = attrTradeData;
                                        }
                                        if (attrTradeData.getRsrvNum1().equals("99011022")) {
                                            serv99011022 = attrTradeData;
                                        }

                                        if (attrTradeData.getRsrvNum1().equals(discntCode) && (BofConst.MODIFY_TAG_ADD.equals(attrTradeData.getModifyTag()) || BofConst.MODIFY_TAG_UPD.equals(attrTradeData.getModifyTag()))) {// RsrvNum1 该套餐编码的属性                                            

                                            if (attrTradeData.getAttrCode().equals("APNNAME")) {
                                                attrMap.put("APNNAME", attrTradeData.getAttrValue());
                                            }
                                            if (attrTradeData.getAttrCode().equals("Discount")) {
                                                attrMap.put("Discount", attrTradeData.getAttrValue());
                                            }
                                            if (attrTradeData.getAttrCode().equals("ApprovalNum")) {
                                                attrMap.put("ApprovalNum", attrTradeData.getAttrValue());
                                            }
                                            if (attrTradeData.getAttrCode().equals("PromiseUseMonths")) {
                                                attrMap.put("PromiseUseMonths", attrTradeData.getAttrValue());
                                            }
                                            if (attrTradeData.getAttrCode().equals("MinimumOfYear")) {
                                                attrMap.put("MinimumOfYear", attrTradeData.getAttrValue());
                                            }
                                            if (attrTradeData.getAttrCode().equals("BatchAccounts")) {
                                                attrMap.put("BatchAccounts", attrTradeData.getAttrValue());
                                            }
                                            if (attrTradeData.getAttrCode().equals("CanShare")) {
                                                attrMap.put("CanShare", attrTradeData.getAttrValue());
                                            }
                                        }
                                    }
                                    
                                    System.out.println("dealPWLWAttrCheckValueAction.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx103 " + attrMap);
                                    
                                    if (attrMap != null && attrMap.size() > 0) {
                                        
                                                                                
                                      
                                        IDataset discntDs = UpcCall.queryOfferNameByOfferCodeAndType(BofConst.ELEMENT_TYPE_CODE_DISCNT,discntCode);
                                        
                                        String discntName = discntDs.getData(0).getString("OFFER_NAME"); 
                                        System.out.println("dealPWLWAttrCheckValueAction.javaxxxxxxxxxxxxxxx115 "+ discntDs);
  
                                        //APNNAME
                                        if (attrMap.get("APNNAME") != null && ((String) attrMap.get("APNNAME")).trim().length() > 0) {
                                            //全国或本地定向流量优惠APNNAME和服务 99011021 的APNNAME保持一致
                                            System.out.println("xxxxxxxxxxxxxxxxxxxxyyyyyyyyyyyyyyyy "+serv99011021.getAttrValue()+"  "+serv99011022.getAttrValue()+"   "+attrMap.get("APNNAME"));
                                            if (groupId.equals("70000005") || groupId.equals("70000009")) {
                                                if (!serv99011021.getAttrValue().equals(attrMap.get("APNNAME"))) {//APNNAME值不相同                                                   
                                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的APNNAME和其依赖的【物联网专用数据通信服务(可选)】的APNNAME需保持一致，请核对。 ");
                                                }
                                            }
                                            //全国或本地通用流量优惠APNNAME和服务 99011022 的APNNAME保持一致         
                                            if (groupId.equals("70000008") || groupId.equals("70000011")) {
                                                if (!serv99011022.getAttrValue().equals(attrMap.get("APNNAME"))) {//APNNAME值不相同                                                                                                        
                                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" + discntName + "】的APNNAME和其依赖的【通用流量4G_GPRS服务】的APNNAME需保持一致，请核对。 ");
                                                }
                                            }
                                        } else {
                                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的APNNAME是必填项，请先填写。 ");
                                        }
                                        
                                        //固费折扣率 (必选) 1-100的整数
                                        if (attrMap.get("Discount") != null && ((String) attrMap.get("Discount")).trim().length() > 0) {

                                            boolean integerNumber = isInteger((String) attrMap.get("Discount"));
                                            if (integerNumber) {
                                                if (Integer.parseInt((String) attrMap.get("Discount")) < 1 && Integer.parseInt((String) attrMap.get("Discount")) > 100) {
                                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的固费折扣率必须是 1-100之间的整数。");
                                                }
                                            } else {
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的固费折扣率必须是 1-100之间的整数。");
                                            }

                                        } else {
                                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的固费折扣率是必填项，且必须是 1-100之间的整数");
                                        }

                                        //审批文号 (可选) 当折扣低于底线6折时，必须填写
                                        if (attrMap.get("ApprovalNum") != null && ((String) attrMap.get("ApprovalNum")).trim().length() > 0) {

                                        } else {
                                            if (Integer.parseInt((String) attrMap.get("Discount")) < 60) {//当折扣低于底线6折时，必须填写
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的折扣低于底线6折时，审批文号必须填写。");
                                            }
                                        }

                                        //承诺在网时间（月）(可选) 24及以上整数
                                        if (attrMap.get("PromiseUseMonths") != null && ((String) attrMap.get("PromiseUseMonths")).trim().length() > 0) {
                                            boolean integerNumber = isInteger((String) attrMap.get("PromiseUseMonths"));
                                            if (integerNumber) {
                                                if (Integer.parseInt((String) attrMap.get("PromiseUseMonths")) < 24) {
                                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的承诺在网时间（月）,须是24及以上整数。");
                                                }
                                            } else {
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的承诺在网时间（月）,须是24及以上整数。");
                                            }
                                        } else {
                                            if (Integer.parseInt((String) attrMap.get("Discount")) < 60) {//当折扣低于底线6折时，承诺在网时间必须填写
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的折扣低于底线6折时，承诺在网时间必须填写。");
                                            }
                                        }

                                        //年承诺收入（元）(可选)    50000及以上整数
                                        if (attrMap.get("MinimumOfYear") != null && ((String) attrMap.get("MinimumOfYear")).trim().length() > 0) {
                                            boolean integerNumber = isInteger((String) attrMap.get("MinimumOfYear"));
                                            if (integerNumber) {
                                                if (Integer.parseInt((String) attrMap.get("MinimumOfYear")) < 50000) {
                                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的年承诺收入（元）,须是50000及以上整数。");
                                                }
                                            } else {
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的年承诺收入（元）,须是50000及以上整数。");
                                            }
                                        } else {
                                            if (Integer.parseInt((String) attrMap.get("Discount")) < 60) {//当折扣低于底线6折时，年承诺收入必须填写
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的折扣低于底线6折时，年承诺收入必须填写。");
                                            }
                                        }

                                        //一次性批量入网用户数(张) (可选)  5000及以上整数
                                        if (attrMap.get("BatchAccounts") != null && ((String) attrMap.get("BatchAccounts")).trim().length() > 0) {
                                            boolean integerNumber = isInteger((String) attrMap.get("BatchAccounts"));
                                            if (integerNumber) {
                                                if (Integer.parseInt((String) attrMap.get("BatchAccounts")) < 50000) {
                                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的一次性批量入网用户数(张),须是5000及以上整数。");
                                                }
                                            } else {
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的一次性批量入网用户数(张),须是5000及以上整数。");
                                            }
                                        } else {
                                            if (Integer.parseInt((String) attrMap.get("Discount")) < 60) {//当折扣低于底线6折时，一次性批量入网用户数必须填写
                                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的折扣低于底线6折时，一次性批量入网用户数必须填写。");
                                            }
                                        }

                                        //是否可共享 (必选) 1是；0否
                                        if (attrMap.get("CanShare") != null && ((String) attrMap.get("CanShare")).trim().length() > 0) {

                                        } else {
                                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "优惠【" +  discntName + "】的是否可共享属性是必填项，请先填写。");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isInteger(String str)
    {
        Pattern pattern = Pattern.compile("^[\\+]?[\\d]+$");
        return pattern.matcher(str).matches();
    }

}
