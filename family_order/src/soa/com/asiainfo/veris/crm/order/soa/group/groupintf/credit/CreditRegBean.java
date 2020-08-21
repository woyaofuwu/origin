
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.group.esp.DatalineEspUtil;

public class CreditRegBean extends GroupBean
{
    private String OperCode;

    private String TradeTypeCode;

    private String oldTradeTypeCode;

    private String userId;

    private String brandCode;

    private String productId;

    /**
     * 立即计费/下账期计费
     */
    private String acctpValue;

    private IData eosInfo;

    private IData dataline;

    private String operType;

    private String sheetType;

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 其它台帐处理-重点
     */
    @Override
    public void actTradeSub() throws Exception
    {
        infoRegDataUser();
        infoRegDataSvc();
        infoRegDataSvcState();
        if("ESPG".equals(brandCode)){//政企esp增加other表数据发服务开通中心拆单
        IDataset list=AttrBizInfoQry.getPfByAttrCode(productId);//判断政企业务产品是否需要发信控给平台
        if(list.size()>0){
        	actTradeOther();
        }	
        }
        
        if("7010".equals(productId) || "7011".equals(productId) || "7012".equals(productId)|| "7016".equals(productId)
        		|| "70111".equals(productId)|| "70112".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)) {

            infoRegDataDiscnt();
            //保存流程数据
            infoRegDataTradeExt();
            
            if(!"7010".equals(productId)) {
                actTradeDataline();
                actTradeDatalineAttr();
            }

        }

        if (!"VPMR".equals(brandCode) && !"9311".equals(productId) && !"N001".equals(brandCode) && !"WLAN".equals(brandCode) && !"VAOP".equals(brandCode))
        {
            infoRegDataGrpPlatsvc();
        }
    }
    public void actTradeOther() throws Exception{
		//String groupId = reqData.getGroupID();
     	IDataset managerDataset = new DatasetList();
     	IData data = new DataMap();
     	IData Otherdata = new DataMap();
     	IData Groupdata = new DataMap();
        IDataset userOtherList = UserOtherInfoQry.queryUserAllOther(userId);
        IDataset userGroupList = UserInfoQry.queryUserGroupId(userId);
        if(!userOtherList.isEmpty()){
        	 Otherdata=userOtherList.getData(0);
        }
        if(!userGroupList.isEmpty()){
        	Groupdata=userGroupList.getData(0);
        }
        
     	if("stop".equals(OperCode)){//暂停
     		data.put("RSRV_VALUE_CODE", "PESB");
	        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
	        data.put("START_DATE", getAcceptTime());
	        data.put("END_DATE", SysDateMgr.getTheLastTime());
	        data.put("INST_ID", SeqMgr.getInstId());
	       // data.put("GROUP_ID", groupId);
	        data.put("USER_ID", userId);
	        data.put("RSRV_STR1","898");//--集团客户归属省代码:898
	        data.put("OPER_CODE","04");//04-暂停、05-恢复、07-退订
	        data.put("RSRV_STR4","1");//订购关系状态变更请求类型:1-暂停、2-恢复、3-退订  
	        data.put("RSRV_STR2",Groupdata.getString("GROUP_ID",""));//--省集团客户编码
	        data.put("RSRV_STR3",Otherdata.getString("RSRV_STR4",""));//-产品订购实例ID
	        data.put("RSRV_STR9","1124");
     	}else{
     		//恢复
     		data.put("RSRV_VALUE_CODE", "PESB");
	        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
	        data.put("START_DATE", getAcceptTime());
	        data.put("END_DATE", SysDateMgr.getTheLastTime());
	        data.put("INST_ID", SeqMgr.getInstId());
	       // data.put("GROUP_ID", groupId);
	        data.put("USER_ID", userId);
	        data.put("RSRV_STR1","898");//--集团客户归属省代码:898
	        data.put("OPER_CODE","05");//04-暂停、05-恢复、07-退订
	        data.put("RSRV_STR4","2");//订购关系状态变更请求类型:1-暂停、2-恢复、3-退订  
	        data.put("RSRV_STR2",Groupdata.getString("GROUP_ID",""));//--省集团客户编码
	        data.put("RSRV_STR3",Otherdata.getString("RSRV_STR4",""));//-产品订购实例ID
	        data.put("RSRV_STR9","1124");
     		
     	}
	        
	        managerDataset.add(data);
	        addTradeOther(managerDataset);
	 }
    /**
     * 处理集团平台信息
     * 
     * @param data
     * @throws Exception
     */
    public void infoRegDataGrpPlatsvc() throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        IDataset userattrs = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserId(userId);
        if (userattrs.isEmpty())
            return;

        for (int i = 0; i < userattrs.size(); i++)
        {
            String bizStateCode = userattrs.getData(i).getString("BIZ_STATE_CODE", "");

            String rsrvTag1 = userattrs.getData(i).getString("RSRV_TAG1", "");
            userattrs.getData(i).put("MODIFY_TAG", "2");
            userattrs.getData(i).put("UPDATE_TIME", getAcceptTime());
            userattrs.getData(i).put("RSRV_TAG3", "0");
            if (OperCode.equals("stop") && bizStateCode.equals("A"))// 暂停
            {
                userattrs.getData(i).put("PLAT_SYNC_STATE", "P");
                userattrs.getData(i).put("OPER_STATE", "04");
                userattrs.getData(i).put("RSRV_TAG1", "7"); // 打上标记，说明是信控停的
                userattrs.getData(i).put("BIZ_STATE_CODE", "N");
                userattrs.getData(i).put("RSRV_TAG3", "0");

            }
            else if (OperCode.equals("back") && bizStateCode.equals("N") && rsrvTag1.equals("7"))// 恢复
            {
                userattrs.getData(i).put("PLAT_SYNC_STATE", "1");
                userattrs.getData(i).put("OPER_STATE", "05");
                userattrs.getData(i).put("RSRV_TAG1", ""); // 打上标记，说明是信控停的
                userattrs.getData(i).put("BIZ_STATE_CODE", "A");
                userattrs.getData(i).put("RSRV_TAG3", "0");
            }
        }

        if (userattrs.size() > 0)
            addTradeGrpPlatsvc(userattrs); // 组织添加台帐表TF_B_TRADE_GRP_PLATSVC

    }

    public void infoRegDataSvc() throws Exception {
        IDataset userSvcListNew = new DatasetList();
        if(OperCode.equals("stop")) {
            IDataset userSvcList = UserSvcInfoQry.queryUserAllSvc(userId);
            if(IDataUtil.isEmpty(userSvcList)) {
                return;
            }
            for (int i = 0, size = userSvcList.size(); i < size; i++) {
                IData userSvc = userSvcList.getData(i);
                if(!userSvc.getString("SERVICE_ID","").equals("1101")&&!userSvc.getString("SERVICE_ID","").equals("9000")&&!userSvc.getString("SERVICE_ID","").equals("7704")
                		&&!userSvc.getString("SERVICE_ID","").equals("9104")
                		&&!userSvc.getString("SERVICE_ID","").equals("10005821")
                		&&!userSvc.getString("SERVICE_ID","").equals("9304")
                		&&!userSvc.getString("SERVICE_ID","").equals("7701")
                		&&!userSvc.getString("SERVICE_ID","").equals("9101")
                	){
	                if(StringUtils.isBlank(operType)) {
	                    userSvc.put("RSRV_STR5", "7");//标记信控停的
	                } else {
	                    userSvc.put("RSRV_STR5", "9");//标记人工停的
	                }
	                userSvc.put("RSRV_DATE1", userSvc.getString("END_DATE"));
	                userSvc.put("END_DATE", getAcceptTime());
	                userSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
	                userSvcListNew.add(userSvc);
                }
            }

        } else if(OperCode.equals("back")) {
            IDataset userSvcList = UserSvcInfoQry.queryUserSvcByUserId(userId, "2");
            if(IDataUtil.isEmpty(userSvcList)) {
                return;
            }
            for (int i = 0, size = userSvcList.size(); i < size; i++) {
                IData userSvc = userSvcList.getData(i);
                if(StringUtils.isBlank(operType) && "7".equals(userSvc.getString("RSRV_STR5"))) {
                    userSvc.put("RSRV_STR5", "");//标记信控停的
                    userSvc.put("START_DATE", getAcceptTime());
                    userSvc.put("END_DATE", userSvc.getString("RSRV_DATE1"));
                    userSvc.put("RSRV_DATE1", "");
                    userSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    userSvcListNew.add(userSvc);
                } else if(StringUtils.isNotBlank(operType) && "9".equals(userSvc.getString("RSRV_STR5"))) {
                    userSvc.put("RSRV_STR5", "");//标记人工停的
                    userSvc.put("START_DATE", getAcceptTime());
                    userSvc.put("END_DATE", userSvc.getString("RSRV_DATE1"));
                    userSvc.put("RSRV_DATE1", "");
                    userSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    userSvcListNew.add(userSvc);
                }
            }
        } else {
            return;
        }
        if(IDataUtil.isNotEmpty(userSvcListNew)) {
            addTradeSvc(userSvcListNew);
        }
    }

    /**
     * 处理用户的服务状态
     * 
     * @param data
     * @throws Exception
     */

    public void infoRegDataSvcState() throws Exception
    {

        IDataset svcDatas = new DatasetList();

        IDataset svcState = new DatasetList();
        svcState = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, Route.CONN_CRM_CG);

        if (svcState.isEmpty())
        {
            return;
        }
        for (int i = 0, size = svcState.size(); i < size; i++)
        {
            if(StringUtils.isBlank(operType)) {
                if (OperCode.equals("back") && !svcState.getData(i).getString("RSRV_STR5", "").equals("7"))
                {
                    continue;
                }
            } else {
                if(OperCode.equals("back") && !svcState.getData(i).getString("RSRV_STR5", "").equals("9")) {
                    continue;
                }
            }
            IData temp1 = new DataMap();
            temp1.putAll(svcState.getData(i));
            IData temp = new DataMap();
            temp.putAll(svcState.getData(i));
            temp1.put("END_DATE", getAcceptTime());
            temp1.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            temp.put("START_DATE", getAcceptTime());
            temp.put("END_DATE", SysDateMgr.getTheLastTime());
            temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            temp.put("INST_ID", SeqMgr.getInstId());// 新增一条记录时要新增instid

            if (OperCode.equals("stop"))
            {
                if(StringUtils.isBlank(operType)) {
                    temp.put("STATE_CODE", "5");
                    temp.put("RSRV_STR5", "7"); // 打上标记，说明是信控停的
                } else {
                    temp.put("STATE_CODE", "n");
                    temp.put("RSRV_STR5", "9");// 打上标记，说明是人工停的
                }
            }
            else if (OperCode.equals("back"))
            {
                temp.put("RSRV_STR5", "");// 打上标记 说明是信控恢复的
                temp1.put("RSRV_STR5", "");
                /*if ("7304".equals(oldTradeTypeCode))
                {
                    temp.put("STATE_CODE", "N"); // 信控开机
                }
                else
                {*/
                    temp.put("STATE_CODE", "0"); // 信控开机
                //}
            }
            else
            {
                return;
            }

            svcDatas.add(temp);
            svcDatas.add(temp1);
        }
        if (svcDatas.size() > 0)
        {
            addTradeSvcstate(svcDatas);
        }

    }

    /**
     * 处理专线资费
     * 
     * @throws Exception
     */
    public void infoRegDataDiscnt() throws Exception {
        String mebProductId = "";
        if("7011".equals(productId)) {
            mebProductId = "97011";
        } else if("7012".equals(productId)) {
            mebProductId = "97012";
        } else if("7016".equals(productId)) {
            mebProductId = "97016";
        }else if("70111".equals(productId)) {
            mebProductId = "970111";
        }else if("70112".equals(productId)) {
            mebProductId = "970112";
        } else if("70121".equals(productId)) {
            mebProductId = "970121";
        } else if("70122".equals(productId)) {
            mebProductId = "970122";
        }

        IDataset discntInfos = null;
        IDataset changeDiscntInfos = new DatasetList();
        IDataset modifyUserAttrs = new DatasetList();
        if("stop".equals(OperCode)) {
            discntInfos = UserDiscntInfoQry.getAllDiscntInfo(userId);
            if(discntInfos != null && discntInfos.size() > 0) {
                for (int i = 0; i < discntInfos.size(); i++) {
                    IData discntInfo = discntInfos.getData(i);
                    String endData = "";
                    if(StringUtils.isNotBlank(operType)) {
                        discntInfo.put("RSRV_STR5", "9");//打标记说明是人工停的
                        if("0".equals(acctpValue)) {
                            endData = SysDateMgr.getLastMonthLastDate();
                        } else if("1".equals(acctpValue)) {
                            endData = SysDateMgr.getLastDateThisMonth();
                        } else {
                            CSAppException.apperr(GrpException.CRM_GRP_713, "人工停机未取到计费方式！");
                        }
                    } else {
                        discntInfo.put("RSRV_STR5", "7");//打标记说明是信控停的
                        endData = SysDateMgr.getLastMonthLastDate();
                    }
                    discntInfo.put("RSRV_DATE1", discntInfo.getString("END_DATE"));//保留之前的结束日期
                    discntInfo.put("END_DATE", endData);
                    discntInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    IDataset userAttrs = UserAttrInfoQry.getALLVaildUserDiscntInfoByUserId(userId, discntInfo.getString("INST_ID"));
                    if(IDataUtil.isNotEmpty(userAttrs)) {
                        for (int j = 0; j < userAttrs.size(); j++) {
                            IData userAttr = userAttrs.getData(j);
                            if(StringUtils.isNotBlank(operType)) {
                                userAttr.put("RSRV_STR5", "9");//打标记说明是人工停的
                            } else {
                                userAttr.put("RSRV_STR5", "7");//打标记说明是信控停的
                            }

                            if("59701004".equals(userAttr.getString("ATTR_CODE"))) {
                            	IData result = DatalineEspUtil.getOnceDiscntAttr(endData, userId, mebProductId);
                            	Boolean onceDiscnt  = result.getBoolean("FLAG");
                            	if(onceDiscnt) {
                                    userAttr.put("RSRV_STR4", "0");//已收取一次性资费
                                } else {
                                    userAttr.put("RSRV_STR4", "1");//未收取一次性资费
                                }
                            }

                            //先结束以前的资费属性
                            userAttr.put("RSRV_DATE1", userAttr.getString("END_DATE"));
                            userAttr.put("END_DATE", endData);
                            userAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            modifyUserAttrs.add(userAttr);
                        }
                    }
                    changeDiscntInfos.add(discntInfo);
                }
            }
        } else if("back".equals(OperCode)) {
            discntInfos = UserDiscntInfoQry.queryUserAllDiscntByUserIdForGrp(userId);
            if(discntInfos != null && discntInfos.size() > 0) {
                String sysdate = SysDateMgr.getCurDay();
                //复机计费方式：25号之前的当月计费，25号及25号之后的次月1日计费。
                String startDate = "";
                if(Integer.valueOf(sysdate) < 25) {
                    startDate = SysDateMgr.getFirstDayOfThisMonth();
                } else {
                    startDate = SysDateMgr.getFirstDayOfNextMonth();
                }
                for (int i = 0; i < discntInfos.size(); i++) {
                    IData discntInfo = discntInfos.getData(i);
                    IData newdiscntInfo = (IData) Clone.deepClone(discntInfo);
                    String newInstId = SeqMgr.getInstId();
                    //只恢复信控暂停的资费
                    if(StringUtils.isBlank(operType) && "7".equals(discntInfo.getString("RSRV_STR5"))) {
                        newdiscntInfo.put("END_DATE", discntInfo.getString("RSRV_DATE1"));
                        newdiscntInfo.put("RSRV_STR5", "");//打标记说明是信控停的
                        newdiscntInfo.put("START_DATE", startDate);
                        newdiscntInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        newdiscntInfo.put("INST_ID", newInstId);

                        discntInfo.put("RSRV_STR5", "");//去除停机标志
                        discntInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        changeDiscntInfos.add(discntInfo);

                        changeDiscntInfos.add(newdiscntInfo);

                        IDataset userAttrs = UserAttrInfoQry.getALLUserDiscntInfoByUserId(userId, discntInfo.getString("INST_ID"));
                        if(IDataUtil.isNotEmpty(userAttrs)) {
                            for (int j = 0; j < userAttrs.size(); j++) {
                                IData userAttr = userAttrs.getData(j);
                                if(StringUtils.isBlank(operType) && "7".equals(userAttr.getString("RSRV_STR5"))) {
                                    IData newUserAttr = (IData) Clone.deepClone(userAttr);
                                    newUserAttr.put("RSRV_STR5", "");//打标记说明是信控停的
                                    //先结束以前的资费属性
                                    newUserAttr.put("END_DATE", userAttr.getString("RSRV_DATE1"));
                                    newUserAttr.put("START_DATE", startDate);
                                    newUserAttr.put("RSRV_DATE1", "");
                                    newUserAttr.put("INST_ID", SeqMgr.getInstId());
                                    newUserAttr.put("RELA_INST_ID", newInstId);
                                    newUserAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                    //复机一次性费用变为0
                                    if("59701004".equals(newUserAttr.getString("ATTR_CODE"))) {
                                        if("0".equals(newUserAttr.getString("RSRV_STR4"))) {
                                            newUserAttr.put("ATTR_VALUE", "0");
                                        }
                                        newUserAttr.put("RSRV_STR4", "");
                                    }
                                    
                                    userAttr.put("RSRV_STR5", "");//去除停机标志
                                    userAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    modifyUserAttrs.add(userAttr);
                                    
                                    modifyUserAttrs.add(newUserAttr);
                                }
                            }
                        }

                    } else if(StringUtils.isNotBlank(operType) && "9".equals(discntInfo.getString("RSRV_STR5"))) {
                        newdiscntInfo.put("END_DATE", discntInfo.getString("RSRV_DATE1"));
                        newdiscntInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        newdiscntInfo.put("RSRV_STR5", "");//打标记说明是人工停的
                        newdiscntInfo.put("START_DATE", startDate);
                        newdiscntInfo.put("INST_ID", newInstId);

                        discntInfo.put("RSRV_STR5", "");//去除停机标志
                        discntInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        changeDiscntInfos.add(discntInfo);

                        changeDiscntInfos.add(newdiscntInfo);

                        IDataset userAttrs = UserAttrInfoQry.getALLUserDiscntInfoByUserId(userId, discntInfo.getString("INST_ID"));
                        if(IDataUtil.isNotEmpty(userAttrs)) {
                            for (int j = 0; j < userAttrs.size(); j++) {
                                IData userAttr = userAttrs.getData(j);
                                if(StringUtils.isNotBlank(operType) && "9".equals(userAttr.getString("RSRV_STR5"))) {
                                    IData newUserAttr = (IData) Clone.deepClone(userAttr);
                                    newUserAttr.put("RSRV_STR5", "");//打标记说明是人工停的
                                    //先结束以前的资费属性
                                    newUserAttr.put("END_DATE", userAttr.getString("RSRV_DATE1"));
                                    newUserAttr.put("START_DATE", startDate);
                                    newUserAttr.put("RSRV_DATE1", "");
                                    newUserAttr.put("INST_ID", SeqMgr.getInstId());
                                    newUserAttr.put("RELA_INST_ID", newInstId);
                                    newUserAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                    //复机一次性费用变为0
                                    if("59701004".equals(newUserAttr.getString("ATTR_CODE"))) {
                                        if("0".equals(newUserAttr.getString("RSRV_STR4"))) {
                                            newUserAttr.put("ATTR_VALUE", "0");
                                        }
                                        newUserAttr.put("RSRV_STR4", "");
                                    }

                                    userAttr.put("RSRV_STR5", "");//去除停机标志
                                    userAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    modifyUserAttrs.add(userAttr);

                                    modifyUserAttrs.add(newUserAttr);
                                }
                            }
                        }
                    }
                }
            }
        }
        if(DataUtils.isNotEmpty(changeDiscntInfos)) {
            addTradeDiscnt(changeDiscntInfos);
        }
        if(DataUtils.isNotEmpty(modifyUserAttrs)) {
            addTradeAttr(modifyUserAttrs);
        }

    }

    @Override
    public IData getProductInfoByElement(IDataset productInfoCaches, IData elementData) throws Exception {
        IDataset userProductInfo = UserProductInfoQry.queryMainProduct(userId);
        IData userProduct = new DataMap();
        if(IDataUtil.isNotEmpty(userProductInfo)) {
            userProduct = userProductInfo.first();
        }
        return userProduct;
    }

    private void actTradeDataline() throws Exception {
        String productNo = dataline.getString("PRODUCT_NO", "");
        String changeMode = dataline.getString("CHANGE_MODE", "");

        // 查询专线信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        //            inparam.put("SHEET_TYPE", "7");
        inparam.put("PRODUCT_NO", productNo);

        IDataset datalineList = new DatasetList();

        if("信控停机".equals(changeMode) || "人工停机".equals(changeMode)) {
            datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);
        } else if("信控开机".equals(changeMode) || "人工复机".equals(changeMode)) {
            datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNoForDatalineKJ(inparam);
        }

        if(null != datalineList && datalineList.size() > 0) {
            datalineList.getData(0).putAll(dataline);
            datalineList.getData(0).put("SHEETTYPE", "33");//专线变更
            if("信控停机".equals(changeMode) || "人工停机".equals(changeMode)) {
                //                  datalineList.getData(0).put("END_DATE", getAcceptTime());//停机截止dataline会引起esop开机无法导入数据
                datalineList.getData(0).put("RSRV_STR3", "1");
            } else if("信控开机".equals(changeMode) || "人工复机".equals(changeMode)) {
                //                  datalineList.getData(0).put("END_DATE", SysDateMgr.getTheLastTime());
                datalineList.getData(0).put("RSRV_STR3", "0");
            }
            datalineList.getData(0).put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }

        super.addTradeDataLine(datalineList);

    }

    private void actTradeDatalineAttr() throws Exception {
        IDataset dataset = new DatasetList();
        String productNo = dataline.getString("PRODUCT_NO", "");
        String changeMode = dataline.getString("CHANGE_MODE", "");
        String serialNo = dataline.getString("SERIALNO", "");
        String subscribeId = dataline.getString("SUBSCRIBE_ID", "");
        String crmNo = dataline.getString("CRMNO", "");

        IData userData = new DataMap();
        userData.put("USER_ID", userId);
        userData.put("START_DATE", getAcceptTime());
        //        userData.put("SHEET_TYPE", "7");
        String changeModeSend = changeMode;


        IDataset datalineTrades = TradeOtherInfoQry.queryDatalineAttrTrade(productNo, "PRODUCTNO");
        int j = 0;
        int k = 0;
        if(datalineTrades != null && datalineTrades.size() > 0) {
            // 查询专线信息
            IDataset datalineAttrs = TradeOtherInfoQry.queryDatalineAttr(datalineTrades.getData(0).getString("TRADE_ID", ""), null);
            for (int i = 0; i < datalineAttrs.size(); i++) {
                IData attr = datalineAttrs.getData(i);
                attr.put("UPDATE_TIME", getAcceptTime());
                if(attr.getString("ATTR_CODE", "").equals("SHEETTYPE")) {
                    datalineAttrs.getData(i).put("ATTR_VALUE", "33");//专线变更
                }
                if(attr.getString("ATTR_CODE", "").equals("CHANGEMODE")) {
                    datalineAttrs.getData(i).put("ATTR_VALUE", changeModeSend);//专线变更
                    j = 1;
                }
                if(attr.getString("ATTR_CODE", "").equals("ISCONTROL")) {
                    if(StringUtils.isBlank(operType)) {
                        datalineAttrs.getData(i).put("ATTR_VALUE", "是");//专线变更
                    } else {
                        datalineAttrs.getData(i).put("ATTR_VALUE", "否");
                    }
                    k = 1;
                }
                if(attr.getString("ATTR_CODE", "").equals("SUBSCRIBE_ID")) {
                    if(!subscribeId.equals("")) {
                        datalineAttrs.getData(i).put("ATTR_VALUE", subscribeId);
                    } else {
                        datalineAttrs.getData(i).put("ATTR_VALUE", getTradeId());
                    }

                }
                if(attr.getString("ATTR_CODE", "").equals("SERIALNO")) {
                    if(!serialNo.equals("")) {
                        datalineAttrs.getData(i).put("ATTR_VALUE", serialNo);
                    } else {
                        datalineAttrs.getData(i).put("ATTR_VALUE", "ESOP" + getTradeId() + "1");
                    }

                }
                if(attr.getString("ATTR_CODE", "").equals("CRMNO")) {
                    if(!crmNo.equals("")) {
                        datalineAttrs.getData(i).put("ATTR_VALUE", crmNo);
                    } else {
                        datalineAttrs.getData(i).put("ATTR_VALUE", getTradeId());
                    }
                }
                if(attr.getString("ATTR_CODE", "").equals("TITLE")) {
                    datalineAttrs.getData(i).put("ATTR_VALUE", changeModeSend);
                }

            }
            if(j == 0) {
                IData attrData = new DataMap(datalineAttrs.getData(0));
                attrData.put("ATTR_CODE", "CHANGEMODE");
                attrData.put("ATTR_VALUE", changeModeSend);
                datalineAttrs.add(attrData);
                /*datalineAttrs.getData(0).put("ATTR_CODE", "CHANGEMODE");
                datalineAttrs.getData(0).put("ATTR_VALUE", changeMode);*/
            }
            if(k == 0) {
                IData attrData = new DataMap(datalineAttrs.getData(0));
                attrData.put("ATTR_CODE", "ISCONTROL");
                if(StringUtils.isBlank(operType)) {
                    attrData.put("ATTR_VALUE", "是");
                } else {
                    attrData.put("ATTR_VALUE", "否");
                }
                datalineAttrs.add(attrData);
                /*datalineAttrs.getData(0).put("ATTR_CODE", "CHANGEMODE");
                datalineAttrs.getData(0).put("ATTR_VALUE", changeMode);*/
            }
            super.addTradeDataLineAttr(datalineAttrs);
        }
    }

    public void infoRegDataTradeExt() throws Exception {
        if(DataUtils.isEmpty(eosInfo)) {
            return;
        }
        IDataset extDatas = new DatasetList();
        IData data = new DataMap();
        data.put("ATTR_CODE", "ESOP");
        data.put("ATTR_VALUE", eosInfo.getString("IBSYSID"));
        data.put("RSRV_STR1", eosInfo.getString("NODE_ID"));
        data.put("RSRV_STR10", "EOS");
        data.put("RSRV_STR5", "NEWFLAG");
        data.put("RSRV_STR6", eosInfo.getString("RECORD_NUM"));
        extDatas.add(data);
        addTradeExt(extDatas);

    }
    /**
     * 信控处理用户信息
     * 
     * @param data
     * @throws Exception
     */

    public void infoRegDataUser() throws Exception
    {
        IDataset userDatas = new DatasetList();
        IData userInfos = UcaInfoQry.qryUserInfoByUserId(userId, Route.CONN_CRM_CG);
        if (userInfos.isEmpty())
        {
            return;
        }

        IData userInfo = userInfos;
        if (OperCode.equals("stop")) // stop 暂停; back 恢复
        {
            if(StringUtils.isNotBlank(operType)) {
                userInfo.put("USER_STATE_CODESET", "1"); // 人工停机
            } else {
                userInfo.put("USER_STATE_CODESET", "5"); // 信控停机
            }
            userInfo.put("LAST_STOP_TIME", getAcceptTime());
        }
        else if (OperCode.equals("back"))
        {
            if ("7304".equals(oldTradeTypeCode))
            {
                userInfo.put("USER_STATE_CODESET", "N"); // 信控开机
            }
            else
            {
                userInfo.put("USER_STATE_CODESET", "0"); // 信控开机
            }

        }
        else
        {
            return;
        }
        userInfo.put("MODIFY_TAG", "2");

        userDatas.add(userInfo);

        addTradeUser(userDatas);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
    }

    @Override
    protected void makInit(IData data) throws Exception
    {
        super.makInit(data);

        TradeTypeCode = data.getString("TRADE_TYPE_CODE");
        userId = data.getString("USER_ID");
        OperCode = data.getString("OperCode");
        brandCode = data.getString("BRAND_CODE");
        productId = data.getString("PRODUCT_ID");
        eosInfo = data.getData("ESOP");
        dataline = data.getData("DATALINE");
        operType = data.getString("OPER_TYPE");
        sheetType = data.getString("SHEETTYPE");
        acctpValue = data.getString("ACCEPTTANCE_PERIOD");
        if(!data.getString("OLD_TRADE_TYPE_CODE","").equals("")){
        	oldTradeTypeCode = data.getString("OLD_TRADE_TYPE_CODE");
        }else{
        	oldTradeTypeCode = data.getString("TRADE_TYPE_CODE");
        }
        
        if ("7110".equals(TradeTypeCode) || "7220".equals(TradeTypeCode) || "7305".equals(TradeTypeCode)
                || "7110".equals(oldTradeTypeCode) || "7220".equals(oldTradeTypeCode) || "7305".equals(oldTradeTypeCode) || "stop".equals(operType))
        {
            // 7110 高额停机 7220 欠费停机 7305 信用特殊停机
            TradeTypeCode = "4200"; // 集团暂停
            OperCode = "stop";
        }
        else if ("7303".equals(TradeTypeCode) || "7304".equals(TradeTypeCode) || "7301".equals(TradeTypeCode)|| "7317".equals(TradeTypeCode)
                || "7303".equals(oldTradeTypeCode) || "7304".equals(oldTradeTypeCode) || "7301".equals(oldTradeTypeCode) || "7317".equals(oldTradeTypeCode) || "back".equals(operType))
        {
            // 7301 交费开机 7303 高额开机 7304 信用特殊开机
            TradeTypeCode = "4201";// 集团恢复
            OperCode = "back";
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
    }

    @Override
    protected void makUca(IData data) throws Exception
    {
        makUcaForGrpNormal(data);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return TradeTypeCode;

    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        if(StringUtils.isBlank(operType)) {
            tradeData.put("SUBSCRIBE_TYPE", "200");
        }
    }

    @Override
    protected void regOrder() throws Exception
    {
        super.regOrder();
        IData tradeData = bizData.getTrade();

        if(StringUtils.isBlank(operType)) {
            tradeData.put("SUBSCRIBE_TYPE", "200");
        }
    }
    
    @Override
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();
        IData tradeData = bizData.getTrade();
        
        //System.out.println("20180503 setTradeBase:"+productId);
        if("7010".equals(productId) || "7011".equals(productId) || "7012".equals(productId)|| "7016".equals(productId)
        		|| "70111".equals(productId)|| "70112".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)){//专线产品
        	tradeData.put("OLCOM_TAG", "0");
            tradeData.put("PF_WAIT", "0");
        } 
        
        //全网短彩信
        if ("9230".equals(productId)) {
        	//直接存台账 
        	tradeData.put("RSRV_STR1", reqData.getUca().getCustGroup().getProvinceCode());//EC归属省编码
        	IData qryParam = new DataMap();
			qryParam.put("CUST_MANAGER_ID", reqData.getUca().getCustGroup().getCustManagerId());
			IDataset maninfos = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", qryParam, Route.CONN_CRM_CEN);
			if (maninfos.size()>0) {
				tradeData.put("RSRV_STR2", maninfos.getData(0).getString("SERIAL_NUMBER",""));//EC客户经理电话
			}
		}
    }
}
