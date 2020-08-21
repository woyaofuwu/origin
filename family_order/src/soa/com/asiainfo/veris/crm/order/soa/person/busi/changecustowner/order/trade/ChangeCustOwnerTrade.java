
package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.AcctDateUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctConsignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustFamilyMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEncryptGeneInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.requestdata.ChangeCustOwnerReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

public class ChangeCustOwnerTrade extends BaseTrade implements ITrade
{

    /**
     * 设置新增业务台帐帐户托收信息子表记录
     * 
     * @param btd
     * @throws Exception
     */
    private void createAccountConsignTradeInfo(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {

        AcctConsignTradeData acctConsignTradeData = new AcctConsignTradeData();
        String startCycleId = "";// 新的托收开始账期
        if (StringUtils.equals("1", reqData.getUca().getAcctDay()))// 用户账期日为自然月账期1号
        {
            startCycleId = SysDateMgr.getNowCyc();// 当前月份第一天
        }
        else
        {
            // 取用户下个账期日所在月的下个月
            String firstDayNextAcct = AcctDateUtils.getFirstDayNextAcct(reqData.getAcceptTime(), reqData.getUca().getAcctDay(), reqData.getUca().getFirstDate());
            startCycleId = StringUtils.replace(SysDateMgr.getDateNextMonthFirstDay(firstDayNextAcct).substring(0, 8), "-", "");
        }

        if (StringUtils.isNotBlank(reqData.getNewAcctId()))// 新建账户
        {
            acctConsignTradeData.setAcctId(reqData.getNewAcctId());
        }
        else
        {
            acctConsignTradeData.setAcctId(reqData.getUca().getUserOriginalData().getAccount().getAcctId());// 老acctid
            this.delAccountConsignTradeInfo(btd, startCycleId);// 删除该账户原有的托收信息
        }
        acctConsignTradeData.setPayModeCode(reqData.getNewPayModeCode());
        acctConsignTradeData.setEparchyCode(CSBizBean.getUserEparchyCode());
        acctConsignTradeData.setCityCode(reqData.getNewCityCode());
        if ("0".equals(reqData.getNewPayModeCode()))
        {
            acctConsignTradeData.setSuperBankCode("-1");
            acctConsignTradeData.setBankCode("-1");
            acctConsignTradeData.setBankAcctNo("-1");
            acctConsignTradeData.setBankAcctName("-1");
        }
        else
        {
            acctConsignTradeData.setSuperBankCode(reqData.getNewSuperBankCode());
            acctConsignTradeData.setBankCode(reqData.getNewBankCode());
            acctConsignTradeData.setBankAcctNo(reqData.getNewBankAcctNo());
            String bankName = UBankInfoQry.getBankNameByBankCode(reqData.getNewBankCode());
            acctConsignTradeData.setBankAcctName(bankName);
        }

        acctConsignTradeData.setContractId(reqData.getNewContractNo());
        acctConsignTradeData.setContractName(reqData.getNewContact());
        acctConsignTradeData.setContact(reqData.getNewContact());
        acctConsignTradeData.setContactPhone(reqData.getNewPhone());
        acctConsignTradeData.setPostAddress(reqData.getNewPostAddress());
        acctConsignTradeData.setPostCode(reqData.getNewPostCode());
        acctConsignTradeData.setConsignMode("1");
        acctConsignTradeData.setPriority("0");
        acctConsignTradeData.setActTag("1");
        acctConsignTradeData.setAssistantTag("0");
        acctConsignTradeData.setPaymentId("4");
        acctConsignTradeData.setPayFeeModeCode("4");
        acctConsignTradeData.setStartCycleId(startCycleId);
        acctConsignTradeData.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTradeData.setInstId(SeqMgr.getInstId());
        acctConsignTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        acctConsignTradeData.setRemark(reqData.getRemark());
        btd.add(btd.getRD().getUca().getSerialNumber(), acctConsignTradeData);
    }

    /**
     * 生成修改账户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createAccountTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData, String modifyTag) throws Exception
    {
        String payModeCode = reqData.getNewPayModeCode();
        AccountTradeData acctTradeData = null;
        if (StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag))// 新增账户
        {
            acctTradeData = new AccountTradeData();
            String newAcctId = SeqMgr.getAcctId();
            reqData.setNewAcctId(newAcctId);
            acctTradeData.setAcctId(newAcctId);// 生成新的账户ID
        }
        else if (StringUtils.equals(BofConst.MODIFY_TAG_UPD, modifyTag))// 修改账户
        {
            acctTradeData = reqData.getUca().getAccount();
        }
        acctTradeData.setCustId(reqData.getNewCustId());// 新客户ID
        acctTradeData.setPayModeCode(payModeCode);
        acctTradeData.setPayName(reqData.getNewPayName());
        acctTradeData.setBankCode(reqData.getNewBankCode());
        if (StringUtils.equals("0", payModeCode))// 现金账户
        {
            acctTradeData.setBankAcctNo("");
            acctTradeData.setContractNo("");
        }
        else
        {
            acctTradeData.setBankAcctNo(reqData.getNewBankAcctNo());
            acctTradeData.setContractNo(reqData.getNewContractNo());// 合同号

            // 选择托收，则自动生成代扣号
            if (StringUtils.isBlank(acctTradeData.getContractNo()) && StringUtils.equals("1", payModeCode) && acctTradeData.getBankCode().length() > 3)
            {
                acctTradeData.setContractNo(PersonUtil.createContractNo(acctTradeData.getAcctId(), acctTradeData.getBankCode()));
            }
        }
        acctTradeData.setRsrvStr6(reqData.getNewBankAgreementNo());// 银行协议号 界面没有
        acctTradeData.setAcctPasswd("");
        acctTradeData.setAcctTag("0");
        acctTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        acctTradeData.setCityCode(reqData.getNewCityCode());// 考虑和客户的一致
        acctTradeData.setScoreValue("0");
        acctTradeData.setCreditClassId("0");
        acctTradeData.setBasicCreditValue("0");
        acctTradeData.setCreditValue("0");
        acctTradeData.setDebutyUserId("");
        acctTradeData.setDebutyCode("");
        acctTradeData.setDepositPriorRuleId("0");
        acctTradeData.setOpenDate(reqData.getAcceptTime());
        acctTradeData.setRemoveTag(reqData.getUca().getUser().getRemoveTag());
        acctTradeData.setRemoveDate("");
        acctTradeData.setModifyTag(modifyTag);// 传入
        acctTradeData.setRemark(reqData.getRemark());
        acctTradeData.setRsrvStr1(reqData.getUca().getUserOriginalData().getCustomer().getCustId());// 原客户CUSTID ?
        acctTradeData.setRsrvStr2("");// 当前账期 ?
        acctTradeData.setRsrvStr3(reqData.getUca().getUserOriginalData().getAccount().getAcctId());// 原客户ACCTID ?

        // 新增账户 ,,用老账户的扩展字段值来填充，感觉意义不大。
        if (StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag))
        {
            acctTradeData.setRsrvStr4(reqData.getUca().getAccount().getRsrvStr4());
            acctTradeData.setRsrvStr5(reqData.getUca().getAccount().getRsrvStr5());
            acctTradeData.setRsrvStr7(reqData.getUca().getAccount().getRsrvStr7());
            acctTradeData.setRsrvStr8(reqData.getUca().getAccount().getRsrvStr8());
            acctTradeData.setRsrvStr9(reqData.getUca().getAccount().getRsrvStr9());
            acctTradeData.setRsrvStr10(reqData.getUca().getAccount().getRsrvStr10());
        }
        btd.add(reqData.getUca().getSerialNumber(), acctTradeData);
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ChangeCustOwnerReqData reqData = (ChangeCustOwnerReqData) btd.getRD();// 请求数据

        /*************** 客户资料处理 ***************/
        createCustomerTrade(btd, reqData);// 生成客户资料
        createCustPersonTrade(btd, reqData);
        createCustFamilyMemberTrade(btd, reqData);// 修改家庭客户成员资料

        /*************** 账户资料处理 ***************/
        String isAcctOnlyFlag = PayRelaInfoQry.isAcctOnly(reqData.getUca().getAcctId(), "1", "1");// 检查是否独立账户
        
        
        /*
         * 判断用户是否存在多个付费关系
         * 如果存在的数量为2个，并且其中一个为正常用户，另外一个为宽带用户，那么就走修改账户资料 
         */
        if(!StringUtils.equals("true", isAcctOnlyFlag)){
        	IDataset payRelations=PayRelaInfoQry.queryAcctValidPayRelation(reqData.getUca().getAcctId(), "1", "1");
            if(IDataUtil.isNotEmpty(payRelations)){
            	if(payRelations.size()>=2){
            		
            		boolean nomalUserCount=false;
        			boolean widenetUserCount=false;
        			String userId=reqData.getUca().getUserId();
            		
            		for(int i=0,size=payRelations.size();i<size;i++){
            			String payRelationUserId=payRelations.getData(i).getString("USER_ID");
            			
            			if(payRelationUserId.equals(userId)){
            				nomalUserCount=true;
            			}
            			
            			if(!widenetUserCount){
            				widenetUserCount=WidenetInfoQry.checkUserIsWideNetUser(payRelationUserId, 
            						reqData.getUca().getSerialNumber());
            			}
            			
            			if(nomalUserCount&&widenetUserCount){
            				break;
            			}
            			
            		}
            		
            		if(nomalUserCount&&widenetUserCount){
        				isAcctOnlyFlag="true";
        			}
            		
            	}
            }
        }
        
        
        if (StringUtils.equals("true", isAcctOnlyFlag))// 独立账户
        {
            // 修改账户资料
            createAccountTrade(btd, reqData, BofConst.MODIFY_TAG_UPD);
        }
        else
        // 非独立账户
        {
            // 新增账户
            createAccountTrade(btd, reqData, BofConst.MODIFY_TAG_ADD);// 里面会生成新acct_id，注意要放在前面
            // 修改付费关系
            createPayRelationTrade(btd, reqData);
            // 增加账户账期数据 --账期日为用户以前账户的账期日？待确定
            btd.addOpenAccountAcctDayData(reqData.getNewAcctId(), reqData.getUca().getAcctDay());
        }

        // 相关信息变化后，需要插台帐表
        AccountTradeData oldAcctData = reqData.getUca().getUserOriginalData().getAccount();// 取原始的账户信息资料
        String oldBankCode = oldAcctData.getBankCode() == null ? "" : oldAcctData.getBankCode();
        String oldBankAcctNo = oldAcctData.getBankAcctNo() == null ? "" : oldAcctData.getBankAcctNo();
        String oldContractNo = oldAcctData.getContractNo() == null ? "" : oldAcctData.getContractNo();
        if (!StringUtils.equals(oldAcctData.getPayModeCode(), reqData.getNewPayModeCode()) || !StringUtils.equals(oldBankCode, reqData.getNewBankCode()) || !StringUtils.equals(oldBankAcctNo, reqData.getNewBankAcctNo())
                || !StringUtils.equals(oldContractNo, reqData.getNewContractNo()))
        {
            // 分散账期新增，如果号码是非自然月，需要进行被动账期变更 --为什么是在这个if循环里面呢？需要和现场确认。
            if (!StringUtils.equals("1", reqData.getUca().getAcctDay()))
            {
                btd.addChangeAcctDayData(reqData.getUca().getUserId(), "1");// 变更账期日为自然月
            }
            createAccountConsignTradeInfo(btd, reqData);// 托收信息处理
        }

        /*************** 用户资料处理 ***************/
        createUserTrade(btd, reqData);
        createUserOtherTrade(btd, reqData);
        createUserSvcTradeData(btd, reqData);// 处理用户服务信息 终止国际漫游和国际长途
        createCreditTrade(btd, reqData);//取消用户星级，TI_O_YWTOCREDIT
        
        //如果用户是宽带用户，继续宽带过户
        geneWideNetTrade(btd);
        
        createMainTrade(btd, reqData);
        /************** action ***************/
        // TCS_DestroyGrpMebInfo 集团成员销户
        // 平台业务处理

        /************** finishaction ***************/
        // 密码因子删除
        // 更新过户业务的用户押金资料为无主押金
        // ITF_CRM_AutoDestroyBankSign 银行解约
  
        /***************使用人***************************/
        createCustPersonTradeInfo(btd);
        CustomerTradeData customerTD = reqData.getUca().getCustomer();
        String psptTypeCode = customerTD.getPsptTypeCode();
        if(psptTypeCode!=null&&psptTypeCode.trim().equals("E")){//营业执照
            createOtherTradeDataEnterprise(btd);
        }else if(psptTypeCode!=null&&psptTypeCode.trim().equals("M")){//组织机构代码证
            createOtherTradeDataOrg(btd);
        }

    }

    /**
     * 处理家庭成员
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createCustFamilyMemberTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        String oldCustId = reqData.getUca().getUserOriginalData().getCustomer().getCustId();
        IDataset set = CustFamilyMebInfoQry.getFamilyMem(oldCustId, "0");// 查询是否存在有效的记录
        if (IDataUtil.isNotEmpty(set))
        {
            for (int i = 0, size = set.size(); i < size; i++)
            {
                // 用新的custId新增
                CustFamilyMebTradeData addTrade = new CustFamilyMebTradeData(set.getData(i));
                addTrade.setMemberCustId(reqData.getNewCustId());
                addTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                btd.add(btd.getRD().getUca().getSerialNumber(), addTrade);

                // 将原custid对应的记录终止
                CustFamilyMebTradeData delTrade = new CustFamilyMebTradeData(set.getData(i));
                delTrade.setRemoveTag("1");
                delTrade.setRemoveDate(reqData.getAcceptTime());
                delTrade.setRemoveStaffId(CSBizBean.getVisit().getStaffId());
                delTrade.setRemoveDepartId(CSBizBean.getVisit().getDepartId());
                delTrade.setRemoveReason("过户终止原客户在家庭成员表中记录！");
                delTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(btd.getRD().getUca().getSerialNumber(), delTrade);
            }
        }
    }

    /**
     * 生成客户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createCustomerTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        String newCustId = SeqMgr.getCustId();
        reqData.setNewCustId(newCustId);// 记录新的cust_id

        CustomerTradeData custTradeData = new CustomerTradeData();
        custTradeData.setCustId(reqData.getNewCustId());
        custTradeData.setCustName(reqData.getNewCustName());
        custTradeData.setCustType("0");
        custTradeData.setCustState("0");
        custTradeData.setRemoveTag("0");
        custTradeData.setPsptTypeCode(reqData.getNewPsptTypeCode());
        custTradeData.setPsptId(reqData.getNewPsptId());
        custTradeData.setOpenLimit("0");// 需要确定取值？？？
        custTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        custTradeData.setCityCode(reqData.getNewCityCode());
        custTradeData.setIsRealName(reqData.getNewIsRealName());
        custTradeData.setInDate(btd.getRD().getAcceptTime());
        custTradeData.setInStaffId(CSBizBean.getVisit().getStaffId());
        custTradeData.setInDepartId(CSBizBean.getVisit().getDepartId());
        custTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        custTradeData.setRemark(btd.getRD().getRemark());
        custTradeData.setRsrvStr7(reqData.getAgentCustName());// 经办人名称
        custTradeData.setRsrvStr8(reqData.getAgentPsptTypeCode());// 经办人证件类型
        custTradeData.setRsrvStr9(reqData.getAgentPsptId());// 经办人证件号码
        custTradeData.setRsrvStr10(reqData.getAgentPsptAddr());// 经办人地址
        
        //REQ201911080010 关于实名入网办理日志留存的改造通知 
        custTradeData.setRsrvStr6(reqData.getDevRead()+","+reqData.getReadRuslt()+","+reqData.getComparisonIs()
        		+","+reqData.getComparisonRuslt()+","+reqData.getComparisonSeq()+","+reqData.getAuthenticityIs()
        		+","+reqData.getAuthenticityRuslt()+","+reqData.getAuthenticitySeq()+","+reqData.getProvenumIs()+","
        		+reqData.getProvenumRuslt()+","+reqData.getProvenumSeq());
        
      //获取页面参数
    	IData requestData=btd.getRD().getPageRequestData();
    	String specialChangeCust=requestData.getString("SPECIAL_CHANGE_CUST", "");
    	if(!specialChangeCust.isEmpty() && specialChangeCust.indexOf("on") != -1){	//如果勾选了 单方特殊过户
    		 //将特殊时间设置成当前时间
    		custTradeData.setRsrvDate1(SysDateMgr.getSysDate());
	   		 
	   		 String tradeId=btd.getTradeId();
	   		 String remark=custTradeData.getRemark();
	   		 if(remark!=null&&!remark.trim().equals("")){
	   			 //验证字段内容是否超过了100(字段长度)
	   			 String totalContent=remark+tradeId;
	   			 byte[] byteContent=totalContent.getBytes();
	   			 if(byteContent.length<=100){
	   				custTradeData.setRemark(totalContent);
	   			 }
	   		 }else{
	   			custTradeData.setRemark(tradeId);
	   		 }
    	}
        btd.add(btd.getRD().getUca().getSerialNumber(), custTradeData);

    }

    /**
     * 生成个人客户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createCustPersonTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        CustPersonTradeData custPersonTradeData = new CustPersonTradeData();
        custPersonTradeData.setCustId(reqData.getNewCustId());

        custPersonTradeData.setPsptTypeCode(reqData.getNewPsptTypeCode());
        custPersonTradeData.setPsptId(reqData.getNewPsptId());
        custPersonTradeData.setCustName(reqData.getNewCustName());
        custPersonTradeData.setPsptEndDate(reqData.getNewPsptEndDate());
        custPersonTradeData.setPsptAddr(reqData.getNewPsptAddr());
        custPersonTradeData.setSex(reqData.getNewSex());
        custPersonTradeData.setCityCode(reqData.getNewCityCode());
        custPersonTradeData.setBirthday(reqData.getNewBirthday());
        if(reqData.getNewBirthday()==null||reqData.getNewBirthday().trim().length()==0){
            custPersonTradeData.setBirthday("1900-01-01");
        }else{
            custPersonTradeData.setBirthday(reqData.getNewBirthday());
        }
        custPersonTradeData.setPostAddress(reqData.getNewPostAddress());
        custPersonTradeData.setPostCode(reqData.getNewPostCode());
        custPersonTradeData.setPostPerson(reqData.getNewCustName());

        custPersonTradeData.setPhone(reqData.getNewPhone());
        custPersonTradeData.setFaxNbr(reqData.getNewFaxNbr());
        custPersonTradeData.setEmail(reqData.getNewEmail());
        custPersonTradeData.setHomeAddress(reqData.getNewHomeAddr());
        custPersonTradeData.setWorkName(reqData.getNewWorkName());
        custPersonTradeData.setWorkKind(reqData.getNewWorkKind());// baseCommInfo.getString("JOB_TYPE"));
        custPersonTradeData.setWorkDepart(reqData.getNewWorkDepart());

        custPersonTradeData.setContact(reqData.getNewContact());
        custPersonTradeData.setContactPhone(reqData.getNewContactPhone());
        custPersonTradeData.setContactTypeCode(reqData.getNewContactTypeCode());// baseCommInfo.getString("PRIORITY")

        custPersonTradeData.setNationalityCode(reqData.getNewNationalityCode());
        custPersonTradeData.setLanguageCode(reqData.getNewLanguageCode());
        custPersonTradeData.setFolkCode(reqData.getNewFolkCode());
        custPersonTradeData.setReligionCode(reqData.getNewReligionCode());
        custPersonTradeData.setJobTypeCode(reqData.getNewJobTypeCode());
        custPersonTradeData.setEducateDegreeCode(reqData.getNewEducateDegreeCode());
        custPersonTradeData.setMarriage(reqData.getNewMarriage());
        custPersonTradeData.setCommunityId(reqData.getNewCommunityId());

        custPersonTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        custPersonTradeData.setRemoveTag("0");
        custPersonTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        custPersonTradeData.setRemark(btd.getRD().getRemark());
        
        custPersonTradeData.setRsrvStr5(reqData.getUseName());
        custPersonTradeData.setRsrvStr6(reqData.getUsePsptTypeCode());
        custPersonTradeData.setRsrvStr7(reqData.getUsePsptId());
        custPersonTradeData.setRsrvStr8(reqData.getUsePsptAddr());

        btd.add(btd.getRD().getUca().getSerialNumber(), custPersonTradeData);
    }

    /**
     * 主台账处理
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createMainTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        btd.getMainTradeData().setCustId(reqData.getNewCustId());
        btd.getMainTradeData().setCustName(reqData.getNewCustName());
        if (StringUtils.isNotBlank(reqData.getNewAcctId()))// 如果是新建账户
        {
            btd.getMainTradeData().setAcctId(reqData.getNewAcctId());
        }

        if (StringUtils.isNotBlank(reqData.getNewPassword()))
        {
            IDataset geneInfo = UserEncryptGeneInfoQry.getEncryptGeneBySn(reqData.getUca().getUserId());
            if (IDataUtil.isNotEmpty(geneInfo))
            {// 如果有密码因子且修改了密码
                btd.getMainTradeData().setRsrvStr9("1");
            }
        }
        // ** 把修改之前的一些信息放到主台账表中，在打印的时候需要把该三个属性修改之前的原信息打印出来**/
        // ** rsrv_str8（客户名称,证件类型,证件号码）**/
        CustPersonTradeData oldperson = btd.getRD().getUca().getUserOriginalData().getCustPerson();
        String custName = oldperson.getCustName();
        String psptTypeCode = oldperson.getPsptTypeCode();
        String psptId = oldperson.getPsptId();
        if (null == custName || "".equals(custName))
            custName = " ";
        if (null == psptTypeCode || "".equals(psptTypeCode))
            psptTypeCode = " ";
        if (null == psptId || "".equals(psptId))
            psptId = " ";
        btd.getMainTradeData().setRsrvStr8(custName + "," + psptTypeCode + "," + psptId);
        btd.getMainTradeData().setRsrvStr10(btd.getRD().getPageRequestData().getString("WIDENET_DEAL",""));
        //获取页面参数
    	IData requestData=btd.getRD().getPageRequestData();
    	String specialChangeCust=requestData.getString("SPECIAL_CHANGE_CUST", "");
    	if(!specialChangeCust.isEmpty() && specialChangeCust.indexOf("on") != -1){	//如果勾选了 单方特殊过户
    		 IData userInfo=UcaInfoQry.qryUserInfoBySn(btd.getRD().getUca().getSerialNumber());
    		 String oldCustId=userInfo.getString("CUST_ID");
    		 
    		 btd.getMainTradeData().setRsrvStr1("1");
    		 btd.getMainTradeData().setRsrvStr2(oldCustId);		//用于后面来发送短信
    	}
    	
    	//单方过户
    	String checkMode=btd.getRD().getCheckMode();
    	if(checkMode!=null&&(checkMode.equals("2")||checkMode.equals("6"))){
    		String addRemark="无原户主证件办理单方特殊过户"; 
    		
    		String remark=btd.getMainTradeData().getRemark();
    		if(remark!=null&&!remark.trim().equals("")){
    			String allRemark=remark+addRemark;
    			int allLength=allRemark.getBytes().length;
    			if(allLength>100){
    				int remarkLength=remark.length();
    				int restLength=50-remarkLength;
    				if(restLength>0){
    					String restAddRemark=addRemark.substring(0,restLength-1);
        				btd.getMainTradeData().setRemark(remark+restAddRemark);
    				}
    			}else{
    				btd.getMainTradeData().setRemark(allRemark);
    			}
    			
    		}else{
    			btd.getMainTradeData().setRemark(addRemark);
    		}
    	}

    	//  REQ201610270005关于红名单客户过户、改名后取消服务的需求 liquan
    	IData cond  = new DataMap();
    	cond.put("USER_ID", btd.getMainTradeData().getUserId());
    	//RESULT_CODE  0成功，1失败       	RESULT_INFO 返回错误信息       	RESULT_TAG  如果用户不是红名单RESULT_TAG返回0，是红名单RESULT_TAG返回1
    	IDataset ds = CSAppCall.call("QCC_CRM_ENDREDUSER", cond);    	
        if (DataSetUtils.isNotBlank(ds)) {
            IData re = ds.getData(0);
            String result_code = re.getString("RESULT_CODE", "").trim();
            String result_tag = re.getString("RESULT_TAG", "").trim();
            if (result_code.equals("0") && result_tag.equals("1")) {//是红名单，并且已被删除 
                String addRemark = "删除红名单成功;";
                String remark = btd.getMainTradeData().getRemark();               
                if (remark != null && !remark.trim().equals("")) {
                    String allRemark = addRemark + remark;
                    int allLength = allRemark.getBytes().length;
                    String insertRemark = "";
                    if (allLength > 148) {
                        int i = 1;
                        while (allLength > 148) {
                            allLength = allRemark.substring(0, allRemark.length() - i).getBytes().length;
                            insertRemark = allRemark.substring(0, allRemark.length() - i);
                            i++;
                        }
                        btd.getMainTradeData().setRemark(insertRemark);
                    } else {
                        btd.getMainTradeData().setRemark(allRemark);
                    }

                } else {
                    btd.getMainTradeData().setRemark(addRemark);
                }
            }
        }
      // REQ201610270005关于红名单客户过户、改名后取消服务的需求 liquan
    	
    }

    /**
     * 生成付费关系台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createPayRelationTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        String firstDayOfThisMonth = SysDateMgr.getFirstDayOfThisMonth().substring(0, 10);// 取当前账期的第一天
        String newStartCycleId = StringUtils.replace(firstDayOfThisMonth, "-", "");
        boolean flag = true;
        // 查询用户还有效的付费关系信息
        IDataset userValidPayRela = PayRelaInfoQry.qryValidPayRelationByUserId(reqData.getUca().getUserId(), "1", "1");
        if (IDataUtil.isNotEmpty(userValidPayRela))
        {
            for (int i = 0, size = userValidPayRela.size(); i < size; i++)
            {
                String tempStartCycleId = userValidPayRela.getData(i).getString("START_CYCLE_ID");
                // 新的开始时间大于原来的开始时间，则是正在生效的，需要终止
                if (SysDateMgr.getTimeDiff(tempStartCycleId, newStartCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD) >= 0)
                {
                    PayRelationTradeData delPayRelTrade = new PayRelationTradeData(userValidPayRela.getData(i));
                    delPayRelTrade.setEndCycleId(StringUtils.replace(SysDateMgr.addDays(firstDayOfThisMonth, -1), "-", ""));// 结束时间为新的开始时间减1天
                    delPayRelTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(reqData.getUca().getSerialNumber(), delPayRelTrade);
                }
                else
                // 当前用户还未生效的付费关系
                {
                    PayRelationTradeData modPayRelTrade = new PayRelationTradeData(userValidPayRela.getData(i));
                    modPayRelTrade.setAcctId(reqData.getNewAcctId());// 改成新的账户ID--很奇怪，需要询问一下现场
                    modPayRelTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    btd.add(reqData.getUca().getSerialNumber(), modPayRelTrade);
                    flag = false;
                }
            }
        }
        if (flag)
        {// 用户不存在还未生效的付费关系 则新增
            // 用新的acctId增加新的付费关系
            PayRelationTradeData newPayRelTrade = new PayRelationTradeData();
            newPayRelTrade.setAcctId(reqData.getNewAcctId());
            newPayRelTrade.setUserId(reqData.getUca().getUserId());
            newPayRelTrade.setPayitemCode("-1");
            newPayRelTrade.setAcctPriority("0");
            newPayRelTrade.setUserPriority("0");
            newPayRelTrade.setAddupMethod("0");
            newPayRelTrade.setAddupMonths("0");
            newPayRelTrade.setBindType("1");
            newPayRelTrade.setActTag("1");
            newPayRelTrade.setDefaultTag("1");
            newPayRelTrade.setLimit("0");
            newPayRelTrade.setLimitType("0");
            newPayRelTrade.setComplementTag("0");
            newPayRelTrade.setStartCycleId(newStartCycleId);
            newPayRelTrade.setEndCycleId(SysDateMgr.getEndCycle20501231());
            newPayRelTrade.setInstId(SeqMgr.getInstId());
            newPayRelTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(reqData.getUca().getSerialNumber(), newPayRelTrade);
        }

    }

    /**
     * 用户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createUserOtherTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        if (StringUtils.equals("1", reqData.getNewIsRealName()))
        {
            OtherTradeData otherTrade = new OtherTradeData();
            otherTrade.setUserId(reqData.getUca().getUserId());
            otherTrade.setRsrvValueCode("CHRN");
            otherTrade.setStartDate(reqData.getAcceptTime());
            otherTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTrade.setInstId(SeqMgr.getInstId());
            otherTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTrade.setRsrvStr3("1");
            otherTrade.setRsrvStr4("100");
            
            /*REQ201702040006优化保存过户前证件号
             * added by zhangxing3
             */
            otherTrade.setRsrvStr9(reqData.getUca().getUserOriginalData().getCustomer().getCustName());//过户之前的姓名
            otherTrade.setRsrvStr10(reqData.getUca().getUserOriginalData().getCustomer().getPsptId());//过户之前的姓名身份证号
            otherTrade.setRsrvStr1(CSBizBean.getVisit().getStaffId()); //受理员工
            otherTrade.setRsrvStr2(reqData.getAcceptTime()); //受理时间
            
            btd.add(reqData.getUca().getSerialNumber(), otherTrade);
        }
    }

    /**
     * 删除用户 service_id in ('15','19') 国际及港澳台长途 /国际及港澳台漫游
     * 
     * @param btd
     * @param startCycleId
     * @throws Exception
     */
    private void createUserSvcTradeData(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {

        List<SvcTradeData> list = reqData.getUca().getUserSvcs();
        if (list.size() > 0)
        {
            for (int i = 0, size = list.size(); i < size; i++)
            {
                SvcTradeData svcTradeData = list.get(i);
                if (StringUtils.equals("15", svcTradeData.getElementId()) || StringUtils.equals("19", svcTradeData.getElementId()))
                {
                    if(StringUtils.equals("15", svcTradeData.getElementId())){
                		SvcTradeData svcTradeData2 = svcTradeData.clone();
                		svcTradeData2.setModifyTag(BofConst.MODIFY_TAG_ADD);
                		svcTradeData2.setElementId("14");
                		svcTradeData2.setStartDate(SysDateMgr.getNextSecond(reqData.getAcceptTime()));
                		svcTradeData2.setInstId(SeqMgr.getInstId());
                		btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData2);
                	}
                	if(StringUtils.equals("19", svcTradeData.getElementId())){
                		SvcTradeData svcTradeData3 = svcTradeData.clone();
                		svcTradeData3.setModifyTag(BofConst.MODIFY_TAG_ADD);
                		svcTradeData3.setElementId("18");
                		svcTradeData3.setStartDate(SysDateMgr.getNextSecond(reqData.getAcceptTime()));
                		svcTradeData3.setInstId(SeqMgr.getInstId());
                		btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData3);
                	}
                    svcTradeData.setEndDate(reqData.getAcceptTime());
                    svcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData);
                }
            }
        }
    }

    /**
     * 用户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createUserTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        String brandCode = btd.getRD().getUca().getBrandCode();       
        UserTradeData userTradeData = btd.getRD().getUca().getUser();
        userTradeData.setCustId(reqData.getNewCustId());
        userTradeData.setUsecustId(reqData.getNewCustId());
        userTradeData.setAcctTag(brandCode.substring(0, 2).equals("GS") ? "Z" : userTradeData.getAcctTag());
        userTradeData.setChangeuserDate(btd.getRD().getAcceptTime());
        userTradeData.setCityCode(reqData.getNewCityCode());
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        userTradeData.setRemark(reqData.getRemark());
        
        String newPwd = reqData.getNewPassword();
        if (StringUtils.isNotBlank(newPwd))
        {
            String newpasswd = PasswdMgr.encryptPassWD(newPwd, PasswdMgr.genUserId(userTradeData.getUserId()));// 密文密码
            userTradeData.setUserPasswd(newpasswd);
        }

        btd.add(reqData.getUca().getSerialNumber(), userTradeData);
    }

    /**
     * 删除业务台账帐户托收表记录
     * 
     * @param btd
     * @param startCycleId
     * @throws Exception
     */
    private void delAccountConsignTradeInfo(BusiTradeData btd, String startCycleId) throws Exception
    {
        IDataset ids = AcctConsignInfoQry.getConsignInfoByAcctIdAndCycle(btd.getRD().getUca().getAcctId(), startCycleId);
        for (int i = 0, size = ids.size(); i < size; i++)
        {
            AcctConsignTradeData acctConsign = new AcctConsignTradeData(ids.getData(i));
            acctConsign.setModifyTag(BofConst.MODIFY_TAG_DEL);
            acctConsign.setEndCycleId(SysDateMgr.getAddMonthsNowday(-1, startCycleId));
            btd.add(btd.getRD().getUca().getSerialNumber(), acctConsign);
        }
    }

    /********************************** 工具类方法 *******************************/

    /**
     * 用户信用度台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createCreditTrade(BusiTradeData btd, ChangeCustOwnerReqData reqData) throws Exception
    {
        CreditTradeData creditTrade = new CreditTradeData();
        creditTrade.setUserId(reqData.getUca().getUserId());
        creditTrade.setCreditMode("callCredit");// 'addCredit-增加扣减信用度;callCredit-触发信控';
        creditTrade.setStartDate(reqData.getAcceptTime());
        creditTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
        creditTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(reqData.getUca().getSerialNumber(), creditTrade);
    }
    
    
    /**
     * 更新宽带的付费关系子台帐拼串
     * 
     * @param pd
     * @param td
     * @throws Exception
     * @author chenzm
     */
    private void geneWideNetTrade(BusiTradeData btd) throws Exception
    {	
    	ChangeCustOwnerReqData rq = (ChangeCustOwnerReqData) btd.getRD();
    	IData requestParam=btd.getRD().getPageRequestData();
    	String wideNetDeal=requestParam.getString("WIDENET_DEAL","");	//获取宽带的处理内容
    	
    	UcaData uca=btd.getRD().getUca();
    	String serialNumber=uca.getSerialNumber();
    	
    	//核对用户是否为宽带用户
        IDataset userInfo = WidenetInfoQry.getUserInfo(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
        	return ;
        }
        String userId = userInfo.getData(0).getString("USER_ID");
        // 用户宽带资料
        IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(dataset))
        {
        	return;
        }
        
        //
        String rsrvStr2Db=dataset.getData(0).getString("RSRV_STR2","");
		if(!(rsrvStr2Db.equals("1")||	
				rsrvStr2Db.equals("2")||rsrvStr2Db.equals("3")||rsrvStr2Db.equals("5"))){
			return ;
		}
    	
    	
    	if(wideNetDeal.equals("1")){	//宽带过户
    		 /*
             * 修改宽带用户的用户信息
             */
            CustomerTradeData custData=uca.getCustomer();
            String custId=custData.getCustId();
            
            //修改KD用户的cust_id信息
            UserTradeData userData = new UserTradeData(userInfo.getData(0));
            userData.setCustId(custId); // 新客户id
            userData.setUsecustId(custId);
            userData.setRemark("过户修改user表");// 取值写死
            userData.setEparchyCode(CSBizBean.getTradeEparchyCode());
            userData.setCityCode(CSBizBean.getVisit().getCityCode());
            userData.setModifyTag("2");
            userData.setRemoveTag("0");
            btd.add(serialNumber, userData);// 加入busiTradeData中
            
            //修改KV用户的cust_id信息(待确认)
          //修改用户的KV用户信息
            IDataset kvUserInfos=WidenetInfoQry.getKVUserInfo(serialNumber);
            if(IDataUtil.isNotEmpty(kvUserInfos)){
            	IData kvUser=kvUserInfos.getData(0);
            	
            	UserTradeData kvUserData=new UserTradeData(kvUser);
            	kvUserData.setCustId(custId);
            	kvUserData.setUsecustId(custId);
            	kvUserData.setRemark("过户修改user表");// 取值写死
            	kvUserData.setEparchyCode(CSBizBean.getTradeEparchyCode());
            	kvUserData.setCityCode(CSBizBean.getVisit().getCityCode());
            	kvUserData.setModifyTag("2");
            	kvUserData.setRemoveTag("0");
                btd.add(serialNumber, kvUserData);// 加入busiTradeData中
            }
            
        	/*
        	 * 修改宽带用户的付费关系
        	 */
            //获取account_id
//            AccountTradeData accountData=uca.getAccount();
//            String acctId=accountData.getAcctId();
//        	
//            // 删掉老的付费关系
//            IDataset oldPayRelas = PayRelaInfoQry.getPayRelatInfoAllColsByUserId(userId);        									  
//            if (IDataUtil.isEmpty(oldPayRelas))
//            {
//                CSAppException.apperr(CrmCommException.CRM_COMM_103, "原用户付费关系查找失败!");
//            }
//            PayRelationTradeData oldPayRelation = new PayRelationTradeData();
//            IData oldPay = oldPayRelas.getData(0);
//            oldPayRelation.setUserId(oldPay.getString("USER_ID"));
//            oldPayRelation.setAcctId(oldPay.getString("ACCT_ID"));
//            oldPayRelation.setPayitemCode(oldPay.getString("PAYITEM_CODE"));
//            oldPayRelation.setAcctPriority(oldPay.getString("ACCT_PRIORITY"));
//            oldPayRelation.setUserPriority(oldPay.getString("USER_PRIORITY"));
//            oldPayRelation.setBindType(oldPay.getString("BIND_TYPE"));
//            oldPayRelation.setStartCycleId(oldPay.getString("START_CYCLE_ID"));
//            oldPayRelation.setEndCycleId(SysDateMgr.getSysDateYYYYMMDD());
//            oldPayRelation.setActTag(oldPay.getString("ACT_TAG"));
//            oldPayRelation.setDefaultTag(oldPay.getString("DEFAULT_TAG"));
//            oldPayRelation.setModifyTag(BofConst.MODIFY_TAG_DEL);
//            oldPayRelation.setLimit(oldPay.getString("LIMIT"));
//            oldPayRelation.setLimitType(oldPay.getString("LIMIT_TYPE"));
//            oldPayRelation.setComplementTag(oldPay.getString("COMPLEMENT_TAG"));
//            oldPayRelation.setRemark("宽带过户付费关系变更-删除关系");
//            oldPayRelation.setAddupMethod(oldPay.getString("ADDUP_METHOD"));
//            oldPayRelation.setAddupMonths(oldPay.getString("ADDUP_MONTHS"));
//            //oldPayRelation.setInstId(SeqMgr.getInstId());
//            oldPayRelation.setInstId(oldPay.getString("INST_ID"));
//            btd.add(serialNumber, oldPayRelation);
//            
//            // 添加新 的付费关系
//            PayRelationTradeData payRelation = new PayRelationTradeData();
//            payRelation.setUserId(userId);
//            payRelation.setAcctId(acctId);
//            payRelation.setPayitemCode("-1");
//            payRelation.setAcctPriority("0");
//            payRelation.setUserPriority("0");
//            payRelation.setBindType("1");
//            payRelation.setStartCycleId(SysDateMgr.getSysDateYYYYMMDD());
//            payRelation.setEndCycleId(SysDateMgr.getEndCycle20501231());
//            payRelation.setActTag("1");
//            payRelation.setDefaultTag("1");
//            payRelation.setModifyTag(BofConst.MODIFY_TAG_ADD);
//            payRelation.setLimit("0");
//            payRelation.setLimitType("0");
//            payRelation.setComplementTag("0");
//            payRelation.setRemark("");
//            payRelation.setAddupMethod("0");
//            payRelation.setAddupMonths("0");
//            payRelation.setInstId(SeqMgr.getInstId());
//            btd.add(serialNumber, payRelation);
//            
            //IMS固话处理
            String IMSDeal = requestParam.getString("DESTORY_IMS_TAG");
            if("2".equals(IMSDeal))  // IMS过户
            {
            	//修改IMS固话cust_id
            	String IMSSerialNumber = rq.getIMSSerialNumber();
            	IData IMSUserInfo = UserInfoQry.getUserInfoBySN(IMSSerialNumber);
            	if (IDataUtil.isNotEmpty(IMSUserInfo)) {
            		UserTradeData IMSuserData = new UserTradeData(IMSUserInfo);
            		IMSuserData.setCustId(custId); // 新客户id
            		IMSuserData.setUsecustId(custId);
            		IMSuserData.setRemark("过户修改user表");// 取值写死
            		IMSuserData.setEparchyCode(CSBizBean.getTradeEparchyCode());
            		IMSuserData.setCityCode(CSBizBean.getVisit().getCityCode());
            		IMSuserData.setModifyTag("2");
            		IMSuserData.setRemoveTag("0");
                    btd.add(serialNumber, IMSuserData);// 加入busiTradeData中
				}
            	
            	//修改IMS虚拟号码
            	IData IMSVUserInfo = UserInfoQry.getUserInfoBySN("IMS_"+serialNumber);
            	if (IDataUtil.isNotEmpty(IMSVUserInfo)) {
            		UserTradeData IMSVuserData = new UserTradeData(IMSVUserInfo);
            		IMSVuserData.setCustId(custId); // 新客户id
            		IMSVuserData.setUsecustId(custId);
            		IMSVuserData.setRemark("过户修改user表");// 取值写死
            		IMSVuserData.setEparchyCode(CSBizBean.getTradeEparchyCode());
            		IMSVuserData.setCityCode(CSBizBean.getVisit().getCityCode());
            		IMSVuserData.setModifyTag("2");
            		IMSVuserData.setRemoveTag("0");
                    btd.add(serialNumber, IMSVuserData);// 加入busiTradeData中
				}
            	
            }else if ("1".equals(IMSDeal)) {  //IMS拆机
            	IData param = new DataMap();
				param.put(Route.ROUTE_EPARCHY_CODE, "0898");
				param.put("EPARCHY_CODE", "0898");
				String IMSSerialNumber = rq.getIMSSerialNumber();
				param.put("SERIAL_NUMBER", IMSSerialNumber);
				param.put("IMS_BRAND", rq.getIMSBrand());
				param.put("IMS_PRODUCT", rq.getIMSProduct());
				param.put("IMS_SERIAL_NUMBER", IMSSerialNumber);
				param.put("WIDE_SERIAL_NUMBER", "KD_"+serialNumber);
				IDataset result = CSAppCall.call("SS.IMSDestroyRegSVC.tradeReg", param);
			}
            
    	}else if(wideNetDeal.equals("0")){		//取消宽带
    		
//    		String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
//            if ("4".equals(wideType))
//            {
//                input.put("TRADE_TYPE_CODE", "635");
//            }
//            else if ("3".equals(wideType))
//            {
//                input.put("TRADE_TYPE_CODE", "625");
//            }
//            else if ("2".equals(wideType) || "5".equals(wideType))
//            {
//                input.put("TRADE_TYPE_CODE", "624");
//            }
//            else
//            {
//                input.put("TRADE_TYPE_CODE", "605");
//            }
    		
    		
    		String tradeTypeCode="605";
    		//String rsrvStr2=dataset.getData(0).getString("RSRV_STR2","");
    		/*if(rsrvStr2.equals("1")||"2".equals(rsrvStr2) || "5".equals(rsrvStr2)||rsrvStr2.equals("3")){	//GPON
    			tradeTypeCode="605";
    		}*/
    		/*else if("2".equals(rsrvStr2) || "5".equals(rsrvStr2)){	//TTADSL  ADSL
    			tradeTypeCode="624";
    		}else if(rsrvStr2.equals("3")){	//FTTH
    			tradeTypeCode="625";
    		}*/
    		if(tradeTypeCode!=null){
    			//办理取消宽带业务
        		IData param=new DataMap();
        		//param.put("TRADE_TYPE_CODE", tradeTypeCode);
        		param.put("SERIAL_NUMBER", serialNumber);
        		param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
        		
                IDataset widenetResult = CSAppCall.call("SS.WidenetDestroyNewRegSVC.tradeReg", param);
                
    		}
    	}
    }
    
    
    /**
     * 处理使用人信息
     * REQ201607110001关于优化过户界面限制的需求
     * @throws Exception
     * @author zhuoyingzhi
     */
    private void createCustPersonTradeInfo(BusiTradeData btd) throws Exception
    {

    	ChangeCustOwnerReqData rd = (ChangeCustOwnerReqData) btd.getRD();// 获取请求数据对象
        CustPersonTradeData custPersonTradeData = rd.getUca().getCustPerson();// 个人客户资料数据对象
        if (custPersonTradeData == null)
        {// 考虑资料未全异常资料的情况下
            CSAppException.apperr(CustException.CRM_CUST_69);// 获取个人客户资料无数据!
        }
       // custPersonTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);// 标记订单为修改
        
 
        //custPersonTradeData.setRsrvStr5(rd.getUseName());
        //custPersonTradeData.setRsrvStr6(rd.getUsePsptTypeCode());
        //custPersonTradeData.setRsrvStr7(rd.getUsePsptId());
        //custPersonTradeData.setRsrvStr8(rd.getUsePsptAddr());

        ///btd.add(rd.getUca().getUser().getSerialNumber(), custPersonTradeData);// 添加订单对象到btd
        
        //新增使用人证件类型录入
        String strUseName = custPersonTradeData.getRsrvStr5();
    	String strUsePsptTypeCode = custPersonTradeData.getRsrvStr6();
    	String strUsePsptId = custPersonTradeData.getRsrvStr7();
    	String strUsePsptAddr = custPersonTradeData.getRsrvStr8();
    	
    	String strCustId = custPersonTradeData.getCustId();
    	String strPartition_id = strCustId.substring(strCustId.length() - 4);	
    	
    	String strTradeTpyeCode = btd.getTradeTypeCode();
    	//获取责任人信息
    	IData idRequestData = btd.getRD().getPageRequestData();
    	String strRsrvstr2 = "";
    	String strRsrvstr3 = "";
    	String strRsrvstr4 = "";
    	String strRsrvstr5 = "";
    	if(IDataUtil.isNotEmpty(idRequestData)){
    		strRsrvstr2 = idRequestData.getString("RSRV_STR2", "");
        	strRsrvstr3 = idRequestData.getString("RSRV_STR3", "");
        	strRsrvstr4 = idRequestData.getString("RSRV_STR4", "");
        	strRsrvstr5 = idRequestData.getString("RSRV_STR5", "");
    	}
    	if( StringUtils.isNotBlank(strUseName) || StringUtils.isNotBlank(strUsePsptTypeCode)
          ||StringUtils.isNotBlank(strUsePsptId) || StringUtils.isNotBlank(strUsePsptAddr) ){
    		
    		IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);//Dao.qryByParse(parser, BizRoute.getRouteId());
    		if( IDataUtil.isNotEmpty(list) ){
    			IData custPersonOtherData = list.first();
    			custPersonOtherData.put("USE_NAME", custPersonTradeData.getRsrvStr5());
            	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custPersonTradeData.getRsrvStr6());
            	custPersonOtherData.put("USE_PSPT_ID", custPersonTradeData.getRsrvStr7());
            	custPersonOtherData.put("USE_PSPT_ADDR", custPersonTradeData.getRsrvStr8());
            	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
            	if(StringUtils.isNotBlank(strRsrvstr2))
    			{
            		custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr3))
    			{
            		custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr4))
    			{
            		custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr5))
    			{
            		custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
    			}
    			Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
    		}else{
    			IData custPersonOtherData = new DataMap();
            	custPersonOtherData.put("PARTITION_ID", strPartition_id);
            	custPersonOtherData.put("CUST_ID", custPersonTradeData.getCustId());
            	custPersonOtherData.put("USE_NAME", custPersonTradeData.getRsrvStr5());
            	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custPersonTradeData.getRsrvStr6());
            	custPersonOtherData.put("USE_PSPT_ID", custPersonTradeData.getRsrvStr7());
            	custPersonOtherData.put("USE_PSPT_ADDR", custPersonTradeData.getRsrvStr8());
            	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
            	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            	custPersonOtherData.put("REMARK", "过户-使用人录入");
            	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
            	if(StringUtils.isNotBlank(strRsrvstr2))
    			{
            		custPersonOtherData.put("RSRV_STR2", strRsrvstr2);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr3))
    			{
            		custPersonOtherData.put("RSRV_STR3", strRsrvstr3);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr4))
    			{
            		custPersonOtherData.put("RSRV_STR4", strRsrvstr4);
    			}
            	if(StringUtils.isNotBlank(strRsrvstr5))
    			{
            		custPersonOtherData.put("RSRV_STR5", strRsrvstr5);
    			}
    			Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
    		}
    		
    	}else{
    		IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);
    		if( IDataUtil.isNotEmpty(list) ){
    			IData custPersonOtherData = list.first();
    			custPersonOtherData.put("USE_NAME", custPersonTradeData.getRsrvStr5());
            	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custPersonTradeData.getRsrvStr6());
            	custPersonOtherData.put("USE_PSPT_ID", custPersonTradeData.getRsrvStr7());
            	custPersonOtherData.put("USE_PSPT_ADDR", custPersonTradeData.getRsrvStr8());
            	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
    			Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
    		}
    	}
        
    }
    public void createOtherTradeDataEnterprise(BusiTradeData btd) throws Exception
    {
        ChangeCustOwnerReqData reqData = (ChangeCustOwnerReqData) btd.getRD();// 请求数据
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        IData idRequestData = btd.getRD().getPageRequestData();
        String legalperson = idRequestData.getString("legalperson", "").trim();//法人
        String startdate = idRequestData.getString("startdate", "").trim();//成立日期
        String termstartdate = idRequestData.getString("termstartdate", "").trim();//营业开始时间
        String termenddate = idRequestData.getString("termenddate", "").trim();//营业结束时间
        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(reqData.getUca().getUserId());
        otherTD.setRsrvValueCode("ENTERPRISE");
        otherTD.setRsrvValue("营业执照");
        otherTD.setRsrvStr1(legalperson);
        otherTD.setRsrvStr2(startdate);
        otherTD.setRsrvStr3(termstartdate);
        otherTD.setRsrvStr4(termenddate);
        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setEndDate(SysDateMgr.getTheLastTime());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
    }
    public void createOtherTradeDataOrg(BusiTradeData btd) throws Exception
    {
        ChangeCustOwnerReqData reqData = (ChangeCustOwnerReqData) btd.getRD();// 请求数据
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        IData idRequestData = btd.getRD().getPageRequestData();
        String orgtype = idRequestData.getString("orgtype", "").trim();
        String effectiveDate = idRequestData.getString("effectiveDate", "").trim();//有效日期
        String expirationDate = idRequestData.getString("expirationDate", "").trim();//失效日期
        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(reqData.getUca().getUserId());
        otherTD.setRsrvValueCode("ORG");
        otherTD.setRsrvValue("组织机构代码证");
        otherTD.setRsrvStr1(orgtype);
        otherTD.setRsrvStr2(effectiveDate);
        otherTD.setRsrvStr3(expirationDate);
        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setEndDate(SysDateMgr.getTheLastTime());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
    }
}
