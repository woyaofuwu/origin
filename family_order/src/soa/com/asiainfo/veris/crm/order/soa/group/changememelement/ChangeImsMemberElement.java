package com.asiainfo.veris.crm.order.soa.group.changememelement;

import java.util.Calendar;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.changeuserelement.ChangeInternetUserElementReqData;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;
import com.asiainfo.veris.crm.order.soa.group.esp.DataLineDiscntConst;

public class ChangeImsMemberElement extends ChangeUserElement {

    /**
     * 构造函数
     * 
     * @return
     */
    public ChangeImsMemberElement() {

    }

    protected ChangeInternetUserElementReqData reqData = null;

    protected String userIdB = "";

    protected BaseReqData getReqData() throws Exception {
        return new ChangeInternetUserElementReqData();
    }

    protected void actTradeBefore() throws Exception {
        super.actTradeBefore();

    }

    protected void initReqData() throws Exception {
        super.initReqData();

        reqData = (ChangeInternetUserElementReqData) getBaseReqData();

    }

    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);

        reqData.setInterData(map.getData("ATTRINTERNET"));

        reqData.setDataline(map.getData("DATALINE"));

        reqData.setCommonData(map.getDataset("COMMON_DATA"));

        reqData.setElementInfo(map.getDataset("ELEMENT_LIST"));

        reqData.setCancelTag(map.getString("CANCEL_TAG"));

        reqData.setChangegrpTag(map.getString("CHANGEGRP_TAG"));
        
        reqData.setInsertTime(map.getString("INSERT_TIME",""));
    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception {
        super.actTradeSub();
        String flag = "2";// 2修改专线
        userIdB = reqData.getUca().getUser().getUserId();
        String cancelTag = reqData.getCancelTag();// 拆机标记，先停资费和服务
        if ("true".equals(cancelTag)) {
            actTradeUserAttrDel();
            actTradeSvcDel();
        } else {
            actTradeDataline(flag);
            actTradeDatalineAttr();
            infoRegVispDataOther(flag);
            actTradeUserAttr();
        }

    }

    private void actTradeDatalineAttr() throws Exception {
        IData dataline = reqData.getDataline();
        IDataset attrDataList = reqData.getCommonData();
        IData internet = reqData.getInterData();
        IDataset dataset = new DatasetList();

        IData userData = new DataMap();
        userData.put("USER_ID", userIdB);
        userData.put("START_DATE", getAcceptTime());
        userData.put("SHEET_TYPE", "6");
        userData.put("BANDWIDTH", internet.getString("pam_NOTIN_LINE_BROADBAND"));
        if (DataUtils.isNotEmpty(attrDataList)) { // 没有getCommonData判空
            for (Object attrInf : attrDataList) {
                IData attrInfo = (IData) attrInf;
                String attrCode = attrInfo.getString("ATTR_CODE");
                String attrValue = attrInfo.getString("ATTR_VALUE");
                if ("PRODUCT_ID".equals(attrCode) && "7016".equals(attrValue)) {
                    attrInfo.put("ATTR_VALUE", "97016");
                }
            }
        }

        dataset = DatalineUtil.addTradeUserDataLineAttr(attrDataList, dataline, userData);
        super.addTradeDataLineAttr(dataset);
    }

    private void actTradeDataline(String flag) throws Exception {
        IData dataline = reqData.getDataline();
        IData internet = reqData.getInterData();
        IData lineInfo = new DataMap();
        IDataset dataset = new DatasetList();
        // add by REQ201802260030 关于集客业务支撑流程式快速开通功能需求
        if (dataline != null && dataline.getString("LINEOPENTAG", "").equals("1")) {
            dataline.put("RSRV_NUM1", dataline.getString("LINEOPENTAG", ""));
        }
        if (dataline != null && !dataline.getString("IPTYPE", "").equals("")) {
            dataline.put("RSRV_STR1", dataline.getString("IPTYPE", ""));
        }
        if (dataline != null && !dataline.getString("CUSAPPSERVIPV4ADDNUM", "").equals("")) {
            dataline.put("RSRV_NUM1", dataline.getString("CUSAPPSERVIPV4ADDNUM", ""));
        }
        if (dataline != null && !dataline.getString("CUSAPPSERVIPV6ADDNUM", "").equals("")) {
            dataline.put("RSRV_NUM2", dataline.getString("CUSAPPSERVIPV6ADDNUM", ""));
        }
        if (null != dataline && dataline.size() > 0 && flag.equals("2")) {
            String productNo = dataline.getString("PRODUCTNO");

            // 查询专线信息
            IData inparam = new DataMap();
            inparam.put("USER_ID", userIdB);
            inparam.put("SHEET_TYPE", "8");
            inparam.put("PRODUCT_NO", productNo);

            IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);
            if (null != datalineList && datalineList.size() > 0) {
                lineInfo = datalineList.getData(0);
            }

            IData userData = new DataMap();
            userData.put("USER_ID", userIdB);
            userData.put("UPDATE_TIME", getAcceptTime());
            userData.put("SHEET_TYPE", "8");

            dataset = DatalineUtil.updateTradeUserDataline(dataline, lineInfo, internet, userData);

            super.addTradeDataLine(dataset);

        } else if (null != dataline && dataline.size() > 0 && flag.equals("1"))// modify by lim 拆机逻辑
        {
            String productNo = dataline.getString("PRODUCTNO");

            // 查询专线信息
            IData inparam = new DataMap();
            inparam.put("USER_ID", userIdB);
            inparam.put("SHEET_TYPE", "8");
            inparam.put("PRODUCT_NO", productNo);

            IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);
            if (null != datalineList && datalineList.size() > 0) {
                lineInfo = datalineList.getData(0);
                lineInfo.put("UPDATE_TIME", getAcceptTime());
                lineInfo.put("END_DATE", getAcceptTime());
                lineInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }

            super.addTradeDataLine(datalineList);

        } else if (flag.equals("0")) {
            IData userData = new DataMap();
            userData.put("USER_ID", userIdB);
            userData.put("START_DATE", getAcceptTime());
            userData.put("SHEET_TYPE", "8");

            dataset = DatalineUtil.addTradeUserDataLine(dataline, internet, userData);

            super.addTradeDataLine(dataset);
        }
    }

    public void infoRegVispDataOther(String flag) throws Exception {
        IDataset dataset = new DatasetList();
        IData internet = reqData.getInterData();
        IData dataline = reqData.getDataline();

        // 查询专线信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userIdB);
        inparam.put("RSRV_VALUE_CODE", "N001");
        IDataset userOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);

        if (internet != null && internet.size() > 0 && null != userOther && userOther.size() > 0 && flag.equals("2")) {
            String numberCode = internet.getString("pam_NOTIN_PRODUCT_NUMBER");

            for (int i = 0; i < userOther.size(); i++) {
                IData vispUser = userOther.getData(i);
                IData newVispUser = (IData) Clone.deepClone(vispUser);

                String lineNumberCode = vispUser.getString("RSRV_STR7");
                // 变更时先将原数据END_DATE修改为当前时间
                if (numberCode.equals(lineNumberCode)) {
                    vispUser.put("UPDATE_TIME", getAcceptTime());
                    vispUser.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                    vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataset.add(vispUser);
                }

                // 新增一条END_DATE为2050-12-31的记录
                if (numberCode.equals(lineNumberCode)) {
                    newVispUser.put("USER_ID", userIdB);
                    newVispUser.put("START_DATE", getAcceptTime());
                    newVispUser.put("UPDATE_TIME", getAcceptTime());
                    newVispUser.put("END_DATE", SysDateMgr.getTheLastTime());
                    newVispUser.put("RSRV_VALUE_CODE", "N001");
                    newVispUser.put("INST_ID", SeqMgr.getInstId());
                    newVispUser.put("IS_NEED_PF", "1");

                    if (null != dataline && dataline.size() > 0) {
                        String changeMode = dataline.getString("CHANGEMODE");
                        if (StringUtils.isNotBlank(changeMode)) {
                            if ("停机".equals(changeMode)) {
                                newVispUser.put("END_DATE", getAcceptTime());
                            } else if ("复机".equals(changeMode)) {
                                newVispUser.put("END_DATE", SysDateMgr.getTheLastTime());
                            }
                        }
                    }

                    newVispUser.put("RSRV_VALUE", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")) + 1);
                    // 专线
                    newVispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_LINE_NUMBER"));
                    // 专线带宽
                    // modify by fufn BUG20180116174029 esop扩容开通单台账
                    // if(dataline!=null&&dataline.getString("BANDWIDTH")!=null){
                    // newVispUser.put("RSRV_STR2", dataline.getString("BANDWIDTH"));
                    // }else{
                    // newVispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND", ""));
                    // }
                    // // newVispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND"));
                    // // 专线价格
                    // newVispUser.put("RSRV_STR3", internet.getString("pam_NOTIN_LINE_PRICE"));
                    // // 安装调试费
                    // newVispUser.put("RSRV_STR4", internet.getString("pam_NOTIN_INSTALLATION_COST"));
                    // // 专线一次性通信服务费
                    // newVispUser.put("RSRV_STR5", internet.getString("pam_NOTIN_ONE_COST"));
                    // // IP地址使用费
                    // newVispUser.put("RSRV_STR6", internet.getString("pam_NOTIN_IP_PRICE"));
                    // // 业务标识
                    newVispUser.put("RSRV_STR7", internet.getString("pam_NOTIN_PRODUCT_NUMBER"));
                    // //软件应用服务费（元）add by chenzg@20180620
                    // newVispUser.put("RSRV_STR8", internet.getString("pam_NOTIN_SOFTWARE_PRICE"));
                    // // 专线实例号
                    // newVispUser.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));
                    // //网络技术支持服务费（元） add by chenzg@20180620
                    // newVispUser.put("RSRV_STR10", internet.getString("pam_NOTIN_NET_PRICE"));

                    newVispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                    newVispUser.put("IS_NEED_PF", "1");

                    dataset.add(newVispUser);
                }
            }
            addTradeOther(dataset);
        } else if (internet != null && internet.size() > 0 && null != userOther && userOther.size() > 0 && flag.equals("1")) {// modify by lim 移机处理

            String numberCode = internet.getString("pam_NOTIN_PRODUCT_NUMBER");

            for (int i = 0; i < userOther.size(); i++) {
                IData vispUser = userOther.getData(i);
                String lineNumberCode = vispUser.getString("RSRV_STR7");

                if (numberCode.equals(lineNumberCode)) {
                    vispUser.put("UPDATE_TIME", getAcceptTime());
                    vispUser.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                    vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                    dataset.add(vispUser);
                }

            }

            addTradeOther(dataset);

        }

    }

    protected void setTradeBase() throws Exception {
        super.setTradeBase();

        IData map = bizData.getTrade();

        // CRM发起资料变更走开环
        if (null == reqData.getDataline() || null == reqData.getCommonData() || reqData.getDataline().size() < 1) {
            map.put("OLCOM_TAG", "0");
        } else {
            // ESOP发起变更根据PFWAIT处理，真正的变更闭环处理，开通转变更的根据ESOP传的PFWAIT处理
            int pfWait = 0;
            IDataset commonData = reqData.getCommonData();
            if (null != commonData && commonData.size() > 0) {
                for (int i = 0; i < commonData.size(); i++) {
                    IData data = commonData.getData(i);

                    if ("PF_WAIT".equals(data.getString("ATTR_CODE")) && StringUtils.isNotBlank(data.getString("ATTR_CODE"))) {

                        pfWait = data.getInt("ATTR_VALUE");
                    }
                }
            }

            map.put("OLCOM_TAG", "1");
            map.put("PF_WAIT", pfWait);

        }
    }

    @Override
    protected void regTrade() throws Exception {
        super.regTrade();
    }

    private void actTradeUserAttr() throws Exception {
        IDataset elementInfos = reqData.getElementInfo();
        String productId = reqData.getUca().getProductId();
        String discntCode = "";
        if ("97012".equals(productId)) {
            productId = "7012";
            discntCode = DataLineDiscntConst.datalineElementId;
        } else if ("97011".equals(productId)) {
            productId = "7011";
            discntCode = DataLineDiscntConst.internetElementId;
        } else if ("97016".equals(productId)) {
            productId = "7016";
            discntCode = DataLineDiscntConst.imsElementId;
        }
        if (IDataUtil.isNotEmpty(elementInfos)) {
            IDataset dataSet = new DatasetList();
            IDataset dataDiscnt = new DatasetList();
            IDataset dataofferRel = new DatasetList();
            for (Object object : elementInfos) {
                IData elementInfo = (IData) object;
                IDataset elementParams = elementInfo.getDataset("ATTR_PARAM");
                if (IDataUtil.isNotEmpty(elementParams)) {
                    String instId = SeqMgr.getInstId();
                    for (Object object2 : elementParams) {
                        IData userAttr = new DataMap();
                        IData elementParam = (IData) object2;
                        String attrCode = elementParam.getString("ATTR_CODE");
                        String attrValue = elementParam.getString("ATTR_VALUE");
                        IData discntParam = new DataMap();
                        discntParam.put("USER_ID", userIdB);
                        discntParam.put("ATTR_CODE", attrCode);
                        discntParam.put("INST_TYPE", "D");
                        IDataset userAttrs = UserAttrInfoQry.getUserLineInfoByUserIdAttrCode(discntParam);
                        
                        if (IDataUtil.isNotEmpty(userAttrs)) {
                        	for(int i =0 ;i<userAttrs.size();i++){
                        		 IData data = new DataMap();
                                 data = userAttrs.getData(i);
                                 data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                 if(reqData.getInsertTime().isEmpty()){
                                 	data.put("END_DATE", SysDateMgr.getSysDate());
                                 }else{
                                 	data.put("END_DATE", SysDateMgr.getLastSecond(reqData.getInsertTime()));
                                 }
                                 dataSet.add(data);
                        	}
                           
                        }
                        userAttr.put("USER_ID", reqData.getUca().getUser().getUserId());
                        userAttr.put("USER_ID_A", "-1");
                        userAttr.put("INST_TYPE", "D");
                        userAttr.put("INST_ID", SeqMgr.getInstId());
                        userAttr.put("RELA_INST_ID", instId);
                        userAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        userAttr.put("ATTR_CODE", attrCode);
                        userAttr.put("ATTR_VALUE", attrValue);
                        userAttr.put("ELEMENT_ID", discntCode);
                        if(reqData.getInsertTime().isEmpty()){
                        	userAttr.put("START_DATE", SysDateMgr.getSysDate());
                        }else{
                        	userAttr.put("START_DATE", reqData.getInsertTime());
                        }
                        userAttr.put("END_DATE", SysDateMgr.getTheLastTime());
                        dataSet.add(userAttr);
                    }
                    if (IDataUtil.isNotEmpty(dataSet)) {
                    	 IDataset discntInfos = UserDiscntInfoQry.getAllDiscntByUD(userIdB, discntCode);
                         if (IDataUtil.isNotEmpty(discntInfos)) {
                         	IData data = new DataMap();
 	                        data = discntInfos.getData(0);
                         	for(int i = 0;i<discntInfos.size();i++){
 	                		  IData dataTemp = new DataMap();
 	                		  dataTemp = discntInfos.getData(i);
 	                		  dataTemp.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
 	                		  dataTemp.put("UPDATE_TIME", SysDateMgr.getSysDate());
 	                          if(reqData.getInsertTime().isEmpty()){
 	                        	  dataTemp.put("END_DATE", SysDateMgr.getSysDate());
 	                          }else{
 	                        	  dataTemp.put("END_DATE", SysDateMgr.getLastSecond(reqData.getInsertTime()));
 	                          }
 	                          dataDiscnt.add(dataTemp);
                         	}

                            IData tempData = new DataMap();

                            tempData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(getTradeId()));
                            tempData.put("USER_ID", data.getString("USER_ID"));
                            tempData.put("USER_ID_A", data.getString("USER_ID_A"));
                            tempData.put("RSRV_NUM1", data.getString("RSRV_NUM1"));
                            tempData.put("RSRV_NUM2", data.getString("RSRV_NUM2"));
                            tempData.put("RSRV_NUM3", data.getString("RSRV_NUM3"));
                            tempData.put("INST_ID", instId);
                            tempData.put("DISCNT_CODE", discntCode);
                            tempData.put("SPEC_TAG", "0");// 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
                            if(reqData.getInsertTime().isEmpty()){
                            	tempData.put("START_DATE", SysDateMgr.getSysDate());
                            }else{
                            	tempData.put("START_DATE", reqData.getInsertTime());
                            }
                            tempData.put("END_DATE", SysDateMgr.getTheLastTime());
                            tempData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            tempData.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            tempData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                            tempData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                            dataDiscnt.add(tempData);
                            

                            IData productInfo =  new DataMap();
                            IDataset oldOfferRel = UserOfferRelInfoQry.qryUserAllOfferRelByUserIdDiscntCode(reqData.getUca().getUserId(),discntCode,data.getString("INST_ID",""));
                            if(IDataUtil.isNotEmpty(oldOfferRel)){
                            	 productInfo  = oldOfferRel.first();
                            	 productInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            	 productInfo.put("UPDATE_TIME", SysDateMgr.getSysDate());
                                 if(reqData.getInsertTime().isEmpty()){
                                	 productInfo.put("END_DATE", SysDateMgr.getSysDate());
                                 }else{
                                	 productInfo.put("END_DATE", SysDateMgr.getLastSecond(reqData.getInsertTime()));
                                 }
                                 dataofferRel.add(productInfo);
	                        	 IData omOfferRel = new DataMap();
	                             omOfferRel.put("INST_ID", SeqMgr.getInstId());
	                             omOfferRel.put("OFFER_INS_ID", productInfo.getString("INST_ID"));
	                             omOfferRel.put("USER_ID", productInfo.getString("USER_ID"));
	                             omOfferRel.put("OFFER_CODE", reqData.getUca().getProductId());
	                             omOfferRel.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
	                             omOfferRel.put("REL_OFFER_INS_ID", instId);
	                             omOfferRel.put("REL_USER_ID", productInfo.getString("USER_ID"));
	                             omOfferRel.put("REL_OFFER_CODE", discntCode);
	                             omOfferRel.put("REL_OFFER_TYPE", "D");
	                             omOfferRel.put("REL_TYPE", BofConst.OFFER_REL_TYPE_COM);// C-构成关系,组关系;L-连带关系
	                             omOfferRel.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	                             if(reqData.getInsertTime().isEmpty()){
	                             	omOfferRel.put("START_DATE", SysDateMgr.getSysDate());
	                             }else{
	                             	omOfferRel.put("START_DATE", reqData.getInsertTime());
	                             }
	                             omOfferRel.put("END_DATE", SysDateMgr.getTheLastTime());
	                             omOfferRel.put("GROUP_ID", "0");
	                             omOfferRel.put("REMARK", "");
	                             dataofferRel.add(omOfferRel);
                            }
                           
                        }
                    }
                }
            }
            
            addTradeAttr(dataSet);
            addTradeDiscnt(dataDiscnt);
            if(IDataUtil.isNotEmpty(dataofferRel)){
            	addTradeOfferRel(dataofferRel);
            }

        }
    }

    private void actTradeUserAttrDel() throws Exception {
        IDataset dataSet = new DatasetList();
        IDataset userAttrs = UserAttrInfoQry.getUserAttrByUserIda(userIdB);
        if (IDataUtil.isNotEmpty(userAttrs)) {
            for (Object object : userAttrs) {
                IData data = (IData) object;
                String endData = data.getString("END_DATE").toString();
                Date date1 = SysDateMgr.string2Date(endData, SysDateMgr.PATTERN_STAND_YYYYMMDD);
                Date date2 = SysDateMgr.string2Date(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD);

                Calendar c1 = Calendar.getInstance();
                c1.setTime(date1);

                Calendar c2 = Calendar.getInstance();
                c2.setTime(date2);

                if (c1.getTimeInMillis() > c2.getTimeInMillis()) {
                    data.put("RSRV_DATE1", data.getString("START_DATE"));
                    data.put("RSRV_DATE2", data.getString("END_DATE"));
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    data.put("END_DATE", SysDateMgr.getSysDate());
                    dataSet.add(data);
                }
            }
            addTradeAttr(dataSet);
        }
    }

    private void actTradeSvcDel() throws Exception {
        IDataset dataSvcs = new DatasetList();
        IDataset dataSvcStates = new DatasetList();
        IDataset userSvcs = UserSvcInfoQry.queryUserAllSvc(userIdB);
        IDataset userSvcStates = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userIdB, "0898");
        if (IDataUtil.isNotEmpty(userSvcs)) {
            for (Object object : userSvcs) {
                IData data = (IData) object;
                data.put("RSRV_DATE1", data.getString("START_DATE"));
                data.put("RSRV_DATE2", data.getString("END_DATE"));
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                data.put("END_DATE", SysDateMgr.getSysDate());
            }
            addTradeSvc(dataSvcs);
        }
        if (IDataUtil.isNotEmpty(userSvcStates)) {
            for (Object object : userSvcStates) {
                IData data = (IData) object;
                data.put("RSRV_DATE1", data.getString("START_DATE"));
                data.put("RSRV_DATE2", data.getString("END_DATE"));
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                data.put("END_DATE", SysDateMgr.getSysDate());
                dataSvcStates.add(data);
            }
            addTradeSvcstate(dataSvcStates);
        }
    }
}
