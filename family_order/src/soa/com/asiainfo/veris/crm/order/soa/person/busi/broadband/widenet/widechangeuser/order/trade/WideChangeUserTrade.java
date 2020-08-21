
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.WideChangeUserCheckBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.order.requestdata.WideChangeUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.FTTHModermApplyBean;

public class WideChangeUserTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        WideChangeUserRequestData reqData = (WideChangeUserRequestData) btd.getRD();
        IData changeInfo = reqData.getWideChangeInfo();
        // 主台账数据修改
        MainTradeData mainData = btd.getMainTradeData();
        mainData.setSubscribeType("300");
        mainData.setPfType("300");
        mainData.setOlcomTag("1");
        mainData.setSerialNumber("KD_" + changeInfo.getString("SERIAL_NUMBER_PRE")); // 变换后的号码
        mainData.setRsrvStr1(changeInfo.getString("SERIAL_NUMBER")); // 变换前号码
        String ACTIVE_EVERY_MON_FEE=changeInfo.getString("ACTIVE_EVERY_MON_FEE");
        mainData.setRsrvStr2(changeInfo.getString("ACTIVE_EVERY_MON_FEE"));//营销活动每月金额，用于完工后转回A用户
        IData tempparam=new DataMap();
        tempparam.put("SERIAL_NUMBER_PRE", changeInfo.getString("SERIAL_NUMBER_PRE"));
        tempparam.put("SERIAL_NUMBER", changeInfo.getString("SERIAL_NUMBER"));
        WideChangeUserCheckBean.insertWideChangeUserTempInfo(tempparam);
        // 用户资料修改
        this.createTradeUser(btd, changeInfo);
        createTradeRelation(btd, changeInfo);
        geneTradePayrelation(btd, changeInfo);
        
        String oldSn =changeInfo.getString("AUTH_SERIAL_NUMBER","");
        String oldUserId="";
        IDataset userSet = UserInfoQry.getUserInfoBySn(oldSn,"0"); //取旧号码信息
    	if(userSet!=null && userSet.size()>0){
    		String oldEparchyCode = userSet.getData(0).getString("EPARCHY_CODE");
    		String oldCityCode=userSet.getData(0).getString("CITY_CODE");
    		changeInfo.put("OLD_EPARCHY_CODE", oldEparchyCode);
    		changeInfo.put("OLD_CITY_CODE", oldCityCode);
    		oldUserId = userSet.getData(0).getString("USER_ID");
    	}
    	IDataset newUserSet = UserInfoQry.getUserInfoBySn(changeInfo.getString("SERIAL_NUMBER_PRE"),"0"); //取新号码信息
    	String userId_new="";
    	if(newUserSet!=null && newUserSet.size()>0){
    		String newEparchyCode = userSet.getData(0).getString("EPARCHY_CODE");
    		String newCityCode=userSet.getData(0).getString("CITY_CODE");
    		userId_new=userSet.getData(0).getString("USER_ID");
    		changeInfo.put("NEW_EPARCHY_CODE", newEparchyCode);
    		changeInfo.put("NEW_CITY_CODE", newCityCode);
    	}
        if(changeInfo.getString("OTHER_DATA","")!=null && "1".equals(changeInfo.getString("OTHER_DATA",""))){
         /**
        	 * 1、退老用户光猫押金
        	 * 2、扣新用户光猫押金
        	 * 3、TF_F_USER_OTHER改USER_ID
        	 * */
        	modifyOtherTradeInfo(btd, changeInfo,oldSn,oldUserId);//如果存在光猫串号，other表数据修改。
        	
        }
        
        IData inparam=new DataMap();
        //String everyMonFee=changeInfo.getString("ACTIVE_EVERY_MON_FEE");//每月费用
    	inparam.put("SERIAL_NUMBER_1",oldSn);
    	inparam.put("SERIAL_NUMBER_2",changeInfo.getString("SERIAL_NUMBER_PRE"));
    	inparam.put("DEPOSIT_CODE_1","9021");
    	inparam.put("DEPOSIT_CODE_2","9021"); 
    	inparam.put("REMARK","宽带过户-A活动余额转入B");
    	inparam.put("USER_ID_IN",userId_new);
    	inparam.put("USER_ID_OUT",oldUserId); 
    	inparam.put("DEDUCT_TYPE_CODE","1"); //不抵扣欠费
    	//inparam.put("ACTIVE_EVERY_MON_FEE",everyMonFee); 
    	transRemainFeeAtoB(inparam);
    }

    // uu关系
    private void createTradeRelation(BusiTradeData<BaseTradeData> btd, IData changeInfo) throws Exception
    {
        // 准备
        RelationTradeData data1 = new RelationTradeData();
        RelationTradeData data2 = new RelationTradeData();
        RelationTradeData data3 = new RelationTradeData();
        RelationTradeData data4 = new RelationTradeData();
        // 找数据
        UcaData ucaData = btd.getRD().getUca();
        String wideUserId = ucaData.getUserId();// 宽带id
        String userIdNew = changeInfo.getString("USER_ID_NEW");// 新用户id
        String serilaNumberNew = changeInfo.getString("SERIAL_NUMBER_PRE"); // SERIAL_NUMBER_A
        // 先处理副卡两条记录，再处理主卡两条记录，都为一条新增，一条删除
        IDataset uuSetB = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("47", wideUserId, "2");
        // 拼台账

        if (IDataUtil.isNotEmpty(uuSetB))
        {
            IData map1 = uuSetB.getData(0);
            // 处理副卡记录
            data3.setUserIdA(map1.getString("USER_ID_A"));// 关联标识的用户标识
            data3.setSerialNumberA(map1.getString("SERIAL_NUMBER_A")); // 关联标识的服务号码
            data3.setUserIdB(map1.getString("USER_ID_B")); // 副卡用户用户标识
            data3.setSerialNumberB(map1.getString("SERIAL_NUMBER_B"));// 副卡用户服务号码
            data3.setRelationTypeCode("47"); // 关系类型
            data3.setRoleTypeCode("0");
            data3.setRoleCodeA("0"); // A角色编码
            data3.setRoleCodeB(map1.getString("ROLE_CODE_B"));// B角色编码,2表示副卡
            data3.setOrderno(map1.getString("ORDERNO"));
            data3.setStartDate(map1.getString("START_DATE"));
            data3.setEndDate(btd.getRD().getAcceptTime());
            data3.setInstId(map1.getString("INST_ID")); // 账务唯一标识
            data3.setModifyTag("1"); // 删除复烤老记
            btd.add(btd.getRD().getUca().getSerialNumber(), data3);

            data4.setUserIdA(map1.getString("USER_ID_A"));
            data4.setSerialNumberA(map1.getString("SERIAL_NUMBER_A"));
            data4.setUserIdB(wideUserId); // 副卡用户用户标识
            data4.setSerialNumberB("KD_" + serilaNumberNew);
            data4.setRelationTypeCode("47"); // 关系类型
            data4.setRoleTypeCode("0");
            data4.setRoleCodeA("0"); // A角色编码
            data4.setRoleCodeB(map1.getString("ROLE_CODE_B"));// B角色编码,2表示副卡
            data4.setOrderno(map1.getString("ORDERNO"));
            data4.setStartDate(btd.getRD().getAcceptTime());
            data4.setEndDate(SysDateMgr.END_DATE_FOREVER);
            data4.setInstId(SeqMgr.getInstId());// 账务唯一标识
            data4.setModifyTag("0");
            btd.add(serilaNumberNew, data4);
            // 处理主卡记录
            IDataset uuSetA = RelaUUInfoQry.getRelationsByUserIdA("47", map1.getString("USER_ID_A"), "1");
            if (IDataUtil.isNotEmpty(uuSetA))
            {
                IData map = uuSetA.getData(0);
                data1.setUserIdA(map.getString("USER_ID_A"));// 关联标识的用户标识
                data1.setSerialNumberA(map.getString("SERIAL_NUMBER_A")); // 关联标识的服务号码
                data1.setUserIdB(map.getString("USER_ID_B")); // 副卡用户用户标识
                data1.setSerialNumberB(map.getString("SERIAL_NUMBER_B"));// 副卡用户服务号码
                data1.setRelationTypeCode("47"); // 关系类型
                data1.setRoleTypeCode("0");
                data1.setRoleCodeA("0"); // A角色编码
                data1.setRoleCodeB(map.getString("ROLE_CODE_B"));// B角色编码,2表示副卡
                data1.setOrderno(map.getString("ORDERNO"));
                data1.setStartDate(map.getString("START_DATE"));
                data1.setEndDate(btd.getRD().getAcceptTime());
                data1.setInstId(map.getString("INST_ID")); // 账务唯一标识
                data1.setModifyTag("1"); // 删除复烤老记录
                btd.add(btd.getRD().getUca().getSerialNumber(), data1);

                data2.setUserIdA(map.getString("USER_ID_A"));
                data2.setSerialNumberA(map.getString("SERIAL_NUMBER_A"));
                data2.setUserIdB(userIdNew); // 副卡用户用户标识
                data2.setSerialNumberB(serilaNumberNew);
                data2.setRelationTypeCode("47"); // 关系类型
                data2.setRoleTypeCode("0");
                data2.setRoleCodeA("0"); // A角色编码
                data2.setRoleCodeB(map.getString("ROLE_CODE_B"));// B角色编码,2表示副卡
                data2.setOrderno(map.getString("ORDERNO"));
                data2.setStartDate(btd.getRD().getAcceptTime());
                data2.setEndDate(SysDateMgr.END_DATE_FOREVER);
                data2.setInstId(SeqMgr.getInstId());// 账务唯一标识
                data2.setModifyTag("0");
                btd.add(serilaNumberNew, data2);
            }
        }
    }

    // 用户资料
    private void createTradeUser(BusiTradeData<BaseTradeData> btd, IData changeInfo) throws Exception
    {
        String oldSn = btd.getRD().getUca().getSerialNumber();
        UserTradeData userData = btd.getRD().getUca().getUser().clone();
        userData.setCustId(changeInfo.getString("CUST_ID_NEW")); // 新客户id
        userData.setUsecustId(changeInfo.getString("CUST_ID_NEW"));
        userData.setRemark("宽带改号修改user表");// 取值写死
        userData.setSerialNumber("KD_" + changeInfo.getString("SERIAL_NUMBER_PRE")); // 以前是在完工的时候修改的
        userData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        userData.setCityCode(CSBizBean.getVisit().getCityCode());
        userData.setModifyTag("2");
        userData.setRemoveTag("0");
        userData.setRsrvStr1(changeInfo.getString("SERIAL_NUMBER_PRE")); // 新用户sn
        userData.setRsrvStr2(changeInfo.getString("USER_ID_NEW")); // 新用户号码
        userData.setRsrvStr3("KD_"+oldSn); // 旧号码
        btd.add(oldSn, userData);// 加入busiTradeData中
        
        //修改用户的KV用户信息
        IDataset kvUserInfos=WidenetInfoQry.getKVUserInfo(oldSn.replaceAll("KD_", ""));
        if(IDataUtil.isNotEmpty(kvUserInfos)){
        	IData kvUser=kvUserInfos.getData(0);
        	
        	UserTradeData kvUserData=new UserTradeData(kvUser);
        	kvUserData.setCustId(changeInfo.getString("CUST_ID_NEW"));
        	kvUserData.setUsecustId(changeInfo.getString("CUST_ID_NEW"));
        	kvUserData.setRemark("宽带改号修改user表");// 取值写死
        	kvUserData.setSerialNumber("KV_" + changeInfo.getString("SERIAL_NUMBER_PRE")); // 以前是在完工的时候修改的
        	kvUserData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        	kvUserData.setCityCode(CSBizBean.getVisit().getCityCode());
        	kvUserData.setModifyTag("2");
        	kvUserData.setRemoveTag("0");
        	kvUserData.setRsrvStr1(changeInfo.getString("SERIAL_NUMBER_PRE")); // 新用户sn
        	kvUserData.setRsrvStr2(changeInfo.getString("USER_ID_NEW")); // 新用户号码
        	kvUserData.setRsrvStr3("KV_" +oldSn); // 旧号码
            btd.add(oldSn, kvUserData);// 加入busiTradeData中
        	
        }
    }

    /**
     * 付费关系子台帐拼串
     * 
     * @param pd
     * @param td
     * @throws Exception
     * @author chenzm
     */
    private void geneTradePayrelation(BusiTradeData<BaseTradeData> btd, IData changeInfo) throws Exception
    {
        // 删掉老的付费关系
        UcaData ucaData = btd.getRD().getUca();
        String userId = ucaData.getUserId();
        IDataset oldPayRelas = PayRelaInfoQry.getPayRelatInfoAllColsByUserId(userId);
        if (IDataUtil.isEmpty(oldPayRelas))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "原用户付费关系查找失败!");
        }
        PayRelationTradeData oldPayRelation = new PayRelationTradeData();
        IData oldPay = oldPayRelas.getData(0);
        oldPayRelation.setUserId(oldPay.getString("USER_ID"));
        oldPayRelation.setAcctId(oldPay.getString("ACCT_ID"));
        oldPayRelation.setPayitemCode(oldPay.getString("PAYITEM_CODE"));
        oldPayRelation.setAcctPriority(oldPay.getString("ACCT_PRIORITY"));
        oldPayRelation.setUserPriority(oldPay.getString("USER_PRIORITY"));
        oldPayRelation.setBindType(oldPay.getString("BIND_TYPE"));
        oldPayRelation.setStartCycleId(oldPay.getString("START_CYCLE_ID"));
        oldPayRelation.setEndCycleId(SysDateMgr.getSysDateYYYYMMDD());
        oldPayRelation.setActTag(oldPay.getString("ACT_TAG"));
        oldPayRelation.setDefaultTag(oldPay.getString("DEFAULT_TAG"));
        oldPayRelation.setModifyTag(BofConst.MODIFY_TAG_DEL);
        oldPayRelation.setLimit(oldPay.getString("LIMIT"));
        oldPayRelation.setLimitType(oldPay.getString("LIMIT_TYPE"));
        oldPayRelation.setComplementTag(oldPay.getString("COMPLEMENT_TAG"));
        oldPayRelation.setRemark("宽带过户付费关系变更-删除关系");
        oldPayRelation.setAddupMethod(oldPay.getString("ADDUP_METHOD"));
        oldPayRelation.setAddupMonths(oldPay.getString("ADDUP_MONTHS"));
        //oldPayRelation.setInstId(SeqMgr.getInstId());
        oldPayRelation.setInstId(oldPay.getString("INST_ID"));
        btd.add(ucaData.getSerialNumber(), oldPayRelation);
        // 添加新 的付费关系
        PayRelationTradeData payRelation = new PayRelationTradeData();
        payRelation.setUserId(userId);
        payRelation.setAcctId(changeInfo.getString("ACCT_ID_CHANGE"));
        payRelation.setPayitemCode("-1");
        payRelation.setAcctPriority("0");
        payRelation.setUserPriority("0");
        payRelation.setBindType("1");
        payRelation.setStartCycleId(SysDateMgr.getSysDateYYYYMMDD());
        payRelation.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payRelation.setActTag("1");
        payRelation.setDefaultTag("1");
        payRelation.setModifyTag(BofConst.MODIFY_TAG_ADD);
        payRelation.setLimit("0");
        payRelation.setLimitType("0");
        payRelation.setComplementTag("0");
        payRelation.setRemark("");
        payRelation.setAddupMethod("0");
        payRelation.setAddupMonths("0");
        payRelation.setInstId(SeqMgr.getInstId());
        btd.add(changeInfo.getString("SERIAL_NUMBER_PRE"), payRelation);
    }
    
    /**
     * 处理other台账表
     * rsrv_str1--光猫串号
		rsrv_str2--押金金额
		rsrv_str6--光猫型号
		rsrv_str7--押金状态  0,押金、1,已转移、2已退还、3,已沉淀
		rsrv_str8--BOSS押金转移流水
		rsrv_str9--移机、拆机未退光猫标志：1.移机未退光猫  2.拆机未退光猫
		rsrv_str11==原来的手机号码
		rsrv_str12==原来的TRADE_ID
		rsrv_tag1--申领模式  0租赁，1购买，2赠送，3自备
		rsrv_tag2--光猫状态  1:申领，2:更改，3:退还，4:丢失
		rsrv_tag3--业务类型  1:开户，2:移机
		rsrv_date1--拆机时间，移机时间，，当拆机不退光猫的时候记录拆机时间，end_date就不修改了
     */
    private void modifyOtherTradeInfo(BusiTradeData btd,IData changeInfo,String serialNumber, String snUserId) throws Exception
    { 
        String deposit=changeInfo.getString("MODEM_FEE","0");
        // 如果本次修改 时存在实名制预受理的信息，则需要终止本条实名制预受理的信息
        IDataset dataset = UserOtherInfoQry.getUserOtherUserId(snUserId, "FTTH", null);
        if (dataset != null && dataset.size() > 0)
        {
        	//只有押金大于0才进行退费扣费。
        	if(Integer.parseInt(deposit)>0){
	        	IData user=dataset.getData(0);   
				String eparchycode=changeInfo.getString("OLD_EPARCHY_CODE"); 
			    String citycode=changeInfo.getString("OLD_CITY_CODE");  
			    String applyStaffId=user.getString("STAFF_ID");//必传--退押金时候传申领操作员工号
	   			String applyDepartId=user.getString("DEPART_ID");//必传 --退押金时候传申领操作员工号部门
	   			String applyCityCode=citycode;
	   			IData tradeData=new DataMap();
	   			tradeData.put("TRADE_ID", user.getString("TRADE_ID",""));
	   			IDataset tradeset=FTTHModermApplyBean.getFtthApplyTradeInfo(tradeData);
	   			if(tradeset!=null && tradeset.size()>0){
	   				applyStaffId=tradeset.getData(0).getString("TRADE_STAFF_ID");
	   				applyDepartId=tradeset.getData(0).getString("TRADE_DEPART_ID");
	   				applyCityCode=tradeset.getData(0).getString("TRADE_CITY_CODE");
	   			}
	   			IData inparam=new DataMap();
	   			inparam.put("SERIAL_NUM", serialNumber);
	   			inparam.put("DEPOSIT", deposit);
	   			inparam.put("SN_USER_ID", snUserId); 
	   			inparam.put("EPARCHY_CODE", eparchycode);
	   			inparam.put("CITY_CODE", applyCityCode);
	   			inparam.put("APPLY_STAFF_ID", applyStaffId);
	   			inparam.put("APPLY_DEPART_ID", applyDepartId);
	   			inparam.put("APPLY_CITY_CODE", applyCityCode);
				IData callRtn=backOldUserDeposit(inparam);//退光猫押金 
				String callRtnType=callRtn.getString("RESULT_CODE","");//0-成功 1-失败
				String rtnInfo=callRtn.getString("RESULT_INFO",""); 
				if(callRtnType==null || !"0".equals(callRtnType)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "原用户【"+serialNumber+"】退光猫押金失败!原因："+rtnInfo);
				}
				changeInfo.put("DEPOSIT", deposit);
				IData callNewFee=tranNewUserDeposit(changeInfo);
				String callNewType=callNewFee.getString("RESULT_CODE","");//0-成功 1-失败
				String rtnNewInfo=callNewFee.getString("RESULT_INFO",""); 
				if(callNewType==null || !"0".equals(callNewType)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "新用户【"+inparam.getString("SERIAL_NUMBER_PRE")+"】转存押金错误!原因："+rtnNewInfo);
				}
        	}
        	// 构建TF_F_USER_OTHER表修改台账
            
            OtherTradeData otherTradeDataNew = new OtherTradeData(dataset.getData(0));
            otherTradeDataNew.setUserId(changeInfo.getString("USER_ID_NEW")); 
            otherTradeDataNew.setModifyTag(BofConst.MODIFY_TAG_ADD);//新增
            otherTradeDataNew.setRsrvStr13(serialNumber);//老号码
            otherTradeDataNew.setRsrvStr14(dataset.getData(0).getString("TRADE_ID",""));//老的TRADE_ID
            otherTradeDataNew.setInstId(SeqMgr.getInstId());
            btd.add(changeInfo.getString("SERIAL_NUMBER_PRE"), otherTradeDataNew);// 加入busiTradeData中
            
            // 老用户终止。用查询出来的tf_f_user_other对象来构建otherTradeData
            // 注意： 一定要保证查询出来数据包含了tf_f_user_other表的所有列，否则在完工时会将没有的列值update为空了
            OtherTradeData otherTradeDataDel = new OtherTradeData(dataset.getData(0));
            otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_DEL);// 终止
            otherTradeDataDel.setRsrvStr2("0");// 钱设置为0
            otherTradeDataDel.setRsrvStr13(changeInfo.getString("SERIAL_NUMBER_PRE"));//过户的新号码
            otherTradeDataDel.setEndDate(SysDateMgr.getSysTime());// 取当前时间，取requestData中的
            // acceptTime，不要用sysDateMgr.getSysTime()
            btd.add(serialNumber, otherTradeDataDel);
        } 
    }

    
    /**
     * 退还老用户光猫押金
     * */
    private IData backOldUserDeposit(IData inparam) throws Exception{
    	IData callRtn=new DataMap();
    	IData inparms=new DataMap(); 
		String serialNumber    = inparam.getString("SERIAL_NUM",     "");
		String deposit         = inparam.getString("DEPOSIT",        "");
		String snUserId        = inparam.getString("SN_USER_ID",     ""); 
		String eparchycode     = inparam.getString("EPARCHY_CODE",   "");
		String citycode        = inparam.getString("CITY_CODE",      "");
		String applyStaffId    = inparam.getString("APPLY_STAFF_ID", "");
		String applyDepartId   = inparam.getString("APPLY_DEPART_ID","");
		String applyCityCode   = inparam.getString("APPLY_CITY_CODE","");
    	//3、获取默认账户  （acct_id)
	   	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
	   	String acctId=accts.getData(0).getString("ACCT_ID"); 
	   	String custid= accts.getData(0).getString("CUST_ID");
			//if(modermNum!=null && !"".equals(modermNum)){
	   	/**
	   	 * 押金可能有0的情况，改成只有>0才退押金及发短信,因为这里即便退了也不终止记录，只是
	   	 * 将押金置为0，因此如果已经是0的什么都不做。
	   	 * */ 
	   	if(!"".equals(deposit) && Integer.parseInt(deposit)>0){
			IData params=new DataMap(); 
			params.put("USER_ID", snUserId);
			params.put("ACCT_ID", acctId);
			params.put("CUST_ID", custid);
			params.put("TRADE_FEE", deposit);
			params.put("EPARCHY_CODE", eparchycode);
			params.put("CITY_CODE", citycode);
	   		params.put("SERIAL_NUMBER", serialNumber); 
	   		params.put("TRADE_STAFF_ID", applyStaffId);//退押金时候传申领光猫时候的操作员工号部门TF_F_USER_OTHER
			params.put("TRADE_DEPART_ID", applyDepartId);//退押金时候传申领光猫时候的操作员工号部门TF_F_USER_OTHER
			params.put("TRADE_CITY_CODE",applyCityCode);
			params.put("SUB_SYS","RESSERV_TF_RH_SALE_DEAL");
			//调用接口，将【押金】——>【现金】
			callRtn=AcctCall.adjustFee(params);//将宽带光猫押金存折里的钱转到现金存折
			 
	   	}
	   	return callRtn;
   	}
    
    
    /**
     * 扣转新用户的光猫押金。现金-存折
     * */
    private IData tranNewUserDeposit(IData inparam) throws Exception{
    	IData rtnData=new DataMap();
    	String serialNumber = inparam.getString("SERIAL_NUMBER_PRE");//新手机号码
    	String userid=inparam.getString("USER_ID_NEW");
    	String deposit= inparam.getString("DEPOSIT","");
    	String eparchycode=inparam.getString("NEW_EPARCHY_CODE");
    	String citycode=inparam.getString("NEW_CITY_CODE"); 
     
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber); 
		//已存在宽带产品，需要判断用户的现金是否足够
		//3、获取默认账户  （acct_id)
    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
    	String acctId=accts.getData(0).getString("ACCT_ID");
    	String custid=accts.getData(0).getString("CUST_ID");
    	param.put("X_PAY_ACCT_ID", acctId); 
    	//4、调接口判断用户的现金是否足够，不够则提示缴费，不登记台账；调用接口
    	IData checkCash= AcctCall.getZDepositBalance(param);
    	String cash=checkCash.getString("CASH_BALANCE");
    	if(Integer.parseInt(cash)<Integer.parseInt(deposit)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "新号码【"+serialNumber+"】账户存折可用余额不足，请先办理缴费。账户余额："+Double.parseDouble(cash)/100+"元，押金金额："+Integer.parseInt(deposit)/100+"元");
    	}else{
    		//5、调账务提供的接口将现金存折的钱转到宽带光猫押金存折； 
    		IData inparams=new DataMap();
    		inparams.put("USER_ID", userid);
    		inparams.put("ACCT_ID", acctId);
    		inparams.put("CUST_ID", custid);
    		inparams.put("TRADE_FEE", deposit);
    		inparams.put("EPARCHY_CODE", eparchycode);
    		inparams.put("CITY_CODE", citycode);
    		inparams.put("SERIAL_NUMBER", serialNumber); 
    		rtnData=AcctCall.transFEEInFtth(inparams); 
		} 
    	return rtnData;
   	}
    
    
    /**
     * 扣转新用户的光猫押金。现金-存折
     * SERIAL_NUMBER_1	              转出号码
		SERIAL_NUMBER_2	              转入号码
		DEPOSIT_CODE_1	              转出帐本编码
		DEPOSIT_CODE_2	              转入帐本编码
		FEE	 	                转账金额
		REMARK	 	              备注
		USER_ID_IN	               转入用户ID
		USER_ID_OUT	            转出用户ID
     * */
    private void transRemainFeeAtoB(IData inparam) throws Exception{
    	IData rtnData=new DataMap();
    	String DEPOSIT_BALANCE9021="0";//存折余额9021存折
    	String DEPOSIT_BALANCE9023="0";//存折余额9023存折
    	String DEPOSIT_BALANCE9015="0";//候鸟套餐的存折
		//3、获取默认账户  （acct_id)		
    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(inparam.getString("SERIAL_NUMBER_1"));
    	String acctId=accts.getData(0).getString("ACCT_ID");
    	IData param = new DataMap();
    	param.put("ACCT_ID", acctId); 
    	/**调用账务查询接口*/
    	IDataset checkCash= AcctCall.queryAcctDeposit(param); 
    	 
    	if(checkCash!=null && checkCash.size()>0){
    		for(int j=0;j<checkCash.size();j++){
    			IData acctInfo=checkCash.getData(j);
	    		String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
	    		
	    		if("9021".equals(DEPOSIT_CODE)){
	    			DEPOSIT_BALANCE9021=""+(Integer.parseInt(DEPOSIT_BALANCE9021)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
	    		}
	    		
	    		if("9023".equals(DEPOSIT_CODE)){ 
	    	    	DEPOSIT_BALANCE9023=""+(Integer.parseInt(DEPOSIT_BALANCE9023)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
	    		}
	    		if("9015".equals(DEPOSIT_CODE)){ 
	    			DEPOSIT_BALANCE9015=""+(Integer.parseInt(DEPOSIT_BALANCE9015)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
	    		}
	    		
    		}
    	}
    	//转9021存折
    	if(Integer.parseInt(DEPOSIT_BALANCE9021)>0){
    		inparam.put("DEPOSIT_CODE_1","9021");
	    	inparam.put("DEPOSIT_CODE_2","9021"); 
	    	inparam.put("FEE",DEPOSIT_BALANCE9021);//A用户活动余额
	    	AcctCall.transRemainFeeAtoB(inparam);
    	}
    	
    	//转9023存折
    	if(Integer.parseInt(DEPOSIT_BALANCE9023)>0){
    		inparam.put("DEPOSIT_CODE_1","9023");
	    	inparam.put("DEPOSIT_CODE_2","9023"); 
	    	inparam.put("FEE",DEPOSIT_BALANCE9023);//A用户活动余额
	    	AcctCall.transRemainFeeAtoB(inparam);
    	}
    	
    	//转9015存折
    	if(Integer.parseInt(DEPOSIT_BALANCE9015)>0){
    		inparam.put("DEPOSIT_CODE_1","9015");
	    	inparam.put("DEPOSIT_CODE_2","9015"); 
	    	inparam.put("FEE",DEPOSIT_BALANCE9015);//A用户活动余额
	    	AcctCall.transRemainFeeAtoB(inparam);
    	}
    }
}
