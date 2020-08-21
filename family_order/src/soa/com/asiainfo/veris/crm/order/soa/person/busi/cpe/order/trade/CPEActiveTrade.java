
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.CPEActiveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEActiveMemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEActiveReqData;

/**
 * CPE套餐开户
 * 
 * @author chenxy3 2015-8-12
 */
public class CPEActiveTrade extends BaseTrade implements ITrade
{
	public void acctDayDeal(BusiTradeData btd, List<CPEActiveMemberData> memberList) throws Exception
    {

        CPEActiveReqData reqData = (CPEActiveReqData) btd.getRD();

        String startCycId = reqData.getStartCycId();
        String targetSeriNumber = null;
        String targetAcctDay = null; // 副卡号码结账日

        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            CPEActiveMemberData member = memberList.get(i);

            targetSeriNumber = member.getMemberSn();

            if (!StringUtils.isBlank(member.getUca().getNextAcctDay()))
            {
                // common.error("532008: 该用户号码"+ targetSeriNumber + "存在预约的帐期，" +
                // "账期生效时间为" + acctDataNew.getString("NEXT_FIRST_DATE") + "，账期生效后才能办理统一付费业务！ ");
                CSAppException.apperr(FamilyException.CRM_FAMILY_930, targetSeriNumber, member.getUca().getNextAcctDay());
            }
            targetAcctDay = member.getUca().getAcctDay();

            // 如果需要被动账期变更[分散用户]
            if (!"1".equals(targetAcctDay))
            {
                String memberStartDate = this.getDiversityStartDate(targetAcctDay, member.getUca().getUserId());

                if (!"1".equals(reqData.getUca().getAcctDay()))
                {// 主号为非自然账期
                    if (Integer.parseInt(memberStartDate) < Integer.parseInt(startCycId))
                    {
                        memberStartDate = startCycId;
                    }
                    memberStartDate = bothDiversityStartDate(memberStartDate);
                }
                member.setStartCycleId(memberStartDate);

                btd.addChangeAcctDayData(member.getUca().getUserId(), "1");

            }
            else
            {
                member.setStartCycleId(startCycId);
            }
        }

        if (!"1".equals(reqData.getUca().getAcctDay()))
        {
            btd.addChangeAcctDayData(reqData.getUca().getUserId(), "1");
        }

    }

    /**
     * 如果业务中2个号码均是分散用户，起始时间继续向后偏移一个账期 注意：将2个号码中账期日较大的下账期传入
     * 
     * @param firstDayNextAcct
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    private String bothDiversityStartDate(String firstDayNextAcct) throws Exception
    {
        // 将YYYMMDD转换为YYYY-MM-DD格式
        if (firstDayNextAcct.length() == 8)
            firstDayNextAcct = firstDayNextAcct.substring(0, 4) + "-" + firstDayNextAcct.substring(4, 6) + "-" + firstDayNextAcct.substring(6, 8);
        String startDate = null;
        // 如果不是1号 则需要进行变更，然后获取下账期初
        String chgAcctDay = "1";
        String sysDate = SysDateMgr.getSysDate();

        // 如果当前时间小于下账期初，即还在本账期内
        if (firstDayNextAcct.compareTo(sysDate) > 0)
            startDate = DiversifyAcctUtil.getLastTimeThisAcctday(firstDayNextAcct, Integer.parseInt(chgAcctDay));
        else
            startDate = DiversifyAcctUtil.getLastTimeThisAcctday(sysDate, Integer.parseInt(chgAcctDay));

        // 获取首次结账日
        startDate = SysDateMgr.getNextSecond(startDate);
        return startDate.substring(0, 10).replaceAll("-", "");
    }

    /**
     * 校验主卡的业务办理限制
     * 
     * @param input
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    private void checkMainSerialBusiLimits(CPEActiveReqData reqData) throws Exception
    {
        String userId = reqData.getUca().getUserId();

        String serialNumber = reqData.getUca().getSerialNumber();
        
        String modifyTag=reqData.getModifyTag();

        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);

        if (dataset.size() < 1)
        {
            // common.error("991010", "获取TF_F_USER_INFOCHANGE数据异常["+userId+"]！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        // -----6.非正常状态用户不能办理业务----------------
        if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
        {
            // common.error("880015", "该号码["+serialNumber+"]是非正常状态用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_802, serialNumber);
        }
        // -----1.主卡不能是其他家庭统付关系的副卡-----------
        IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "CP", "2");
        if (ds.size() > 0)
        {
            // common.error("880010", "该号码["+serialNumber+"]是其他统一付费关系的副卡，不能作为主卡，请先退出！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_903, serialNumber);
        }
        // -----2.如果该号码存在多条家庭统付关系的主号信息，则提示资料不正常---------
        ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "CP", "1");
        if (ds.size() > 1)
        {
            // common.error("880011", "该号码["+serialNumber+"]存在多条统一付费的主号UU数据，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_904, serialNumber);

        }
        // -----3.随E行、移动公话、8位或11位TD无线固话不可作为主卡-----------------
        ds = CommparaInfoQry.getCommparaByCode1("CSM", "698", "1", productId);
        if (ds.size() > 0)
        {
            // common.error("880012", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_805, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        // -----4.随E行绑定、IP后付费捆绑、一卡双号、一卡付多号业务的副卡不能作为家庭统一付费业务的主卡----
        ds = RelaUUInfoQry.queryLimitUUInfos(userId, "2", "1");
        if (ds.size() > 0)
        {
            // common.error("880013", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_805, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        // -----5.宽带捆绑的副卡不能作为家庭统一付费业务的主卡------------
        /*
         * String serialNumberTmp = "KD_"+serialNumber; ds = FamilyUnionPayUtilBean.queryUserInfoBySn2(pd,
         * serialNumberTmp); if(ds!=null && ds.size()>0){ common.error("880014",
         * "该号码["+serialNumber+"]是[宽带捆绑]用户，不能作为主卡，请确认！"); }
         */
        // -----7.限制某些优惠不能作为主卡-------------------------------

        ds = DiscntInfoQry.queryLimitDiscnts(userId, "1");
        if (ds.size() > 0)
        {
            // common.error("880016", "该号码["+serialNumber+"]存在["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]优惠，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_807, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        } 
    }

    /**
     * 副号码校验
     * 
     * @param btd
     * @param memberList
     * @throws Exception
     * @CREATE BY GONGP@2014-8-26
     */
    private void checkSeSerialNumber(BusiTradeData btd, List<CPEActiveMemberData> memberList) throws Exception
    {

        CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class);
        for (int i = 0; i < memberList.size(); i++)
        {

            CPEActiveMemberData member = memberList.get(i);
            IData input = new DataMap();
            String checkSn=member.getMemberSn();
            String mainSn=btd.getRD().getUca().getSerialNumber();
//            String mainUserid=btd.getRD().getUca().getUserId();
//            String firstTimeTag=member.getFirstTimeTag();
//            String modifyTag=member.getModifyTag();
//            if("1".equals(modifyTag)){
//            	IData inData=new DataMap();
//            	inData.put("USER_ID", mainUserid);   
//            	inData.put("PRODUCT_ID", "99992825");  
//            	inData.put("PROCESS_TAG", "0");  
//            	IDataset result=bean.checkSnSaleActiveIfEnd(inData);
//            	if(result!=null && result.size()>0){
//            		String x_resultinfo="主卡【"+mainSn+"】的CPE合约营销活动未终止，不允许解除绑定关系。";
//        	    	CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
//            	}
//            }
            String equipNum=member.getEquipNum();
            input.put("CHECK_SERIAL_NUMBER", checkSn);
            input.put("MAIN_SERIAL_NUMBER", mainSn);
            input.put("EQUIPMENT_NUM", equipNum);

            bean.checkBySerialNumber(input);
        }
    }

    private void checkTradeLimitBookActives(String userId) throws Exception
    {

//      该判断不需要  
//   	IDataset dataset = SaleActiveInfoQry.queryTradeLimitBookActives(userId, "699");
//
//        if (IDataUtil.isNotEmpty(dataset))
//        {
//            // common.error("770013", "主号存在预约办理的活动["+ds.getData(0).getString("PRODUCT_NAME",
//            // "")+"]不能解散统一付费，请先返销该预约活动后，再来办理！");
//            CSAppException.apperr(FamilyException.CRM_FAMILY_820, dataset.getData(0).getString("PRODUCT_NAME", ""));
//        }
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        CPEActiveReqData reqData = (CPEActiveReqData) btd.getRD();

        List<CPEActiveMemberData> memberList = reqData.getMemberList();

        String endDate = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.getSysDate(), Integer.parseInt(reqData.getUca().getAcctDay())) + SysDateMgr.END_DATE;

        String startCycId = this.getDiversityStartDate(reqData.getUca().getAcctDay(), reqData.getUca().getUserId());

        reqData.setEndDate(endDate);

        reqData.setStartCycId(startCycId);

        this.intfDeal(btd, memberList);

        this.acctDayDeal(btd, memberList);

        this.genTradeRela(btd, memberList);//判断是否够钱押金
        
        this.genTradePayrelation(btd, memberList);//20151202 先扣钱，如果错误可以回滚。
        
        this.createOtherTradeInfo(btd, memberList);//20151202 最后调用华为设备接口
        
        MainTradeData mainTD = btd.getMainTradeData();

        mainTD.setRemark(reqData.getRemark());

        mainTD.setRsrvStr1(reqData.getxTag());  
        
    	
    }

    public void createProductTradeData(BusiTradeData btd, String serialNumber, String userId) throws Exception
    {
        CPEActiveReqData reqData = (CPEActiveReqData) btd.getRD();

        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(userId);
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getUca().getProductId());
        productTD.setProductMode("00");
        productTD.setBrandCode("CPE1");
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getAcceptTime());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");
        btd.add(serialNumber, productTD);
    }

    private String geneTradeUser(BusiTradeData btd, String userIdA) throws Exception
    {

        CPEActiveReqData reqData = (CPEActiveReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();

        CustomerTradeData custData = ucaDta.getCustomer();
        UserTradeData userData = new UserTradeData();// ucaDta.getUser().clone();

        String serialNumber = "CPE" + userIdA.substring(userIdA.length() - 8); // User表的SerialNumber
        String custId = custData.getCustId();

        userData.setUserId(userIdA);
        userData.setSerialNumber(serialNumber);
        userData.setUserTypeCode("0");
        userData.setUserStateCodeset("0");
        userData.setAcctTag("0");
        userData.setOpenMode("0");
        userData.setCustId(custId);
        userData.setUsecustId(custId);
        userData.setMputeMonthFee("0");
        userData.setNetTypeCode(ucaDta.getUser().getNetTypeCode());
        userData.setEparchyCode(ucaDta.getUser().getEparchyCode());
        userData.setCityCode(ucaDta.getUser().getCityCode());
        userData.setInDate(reqData.getAcceptTime());
        userData.setInStaffId(CSBizBean.getVisit().getStaffId());// 建档员工
        userData.setInDepartId(CSBizBean.getVisit().getDepartId());// 建档渠道
        userData.setOpenDate(reqData.getAcceptTime());// 开户时间
        userData.setOpenStaffId(CSBizBean.getVisit().getStaffId());// 开户员工
        userData.setOpenDepartId(CSBizBean.getVisit().getDepartId());// 开户渠道
        userData.setPrepayTag("1");// 预付费标记：0：后付费，1：预付费
        userData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        userData.setRemoveTag("0");

        btd.add(ucaDta.getSerialNumber(), userData);

        this.createProductTradeData(btd, serialNumber, userIdA);

        btd.addOpenUserAcctDayData(userIdA, "1");

        return serialNumber;
    }

    /**
     * 简单算法描述： 用主号的默认付费acct_id给副号加一条非默认的付费关系
     * 
     * @param btd
     * @param memberList
     * @throws Exception
     * @CREATE BY GONGP@2014-6-18
     */
    public void genTradePayrelation(BusiTradeData btd, List<CPEActiveMemberData> memberList) throws Exception
    {

        CPEActiveReqData reqData = (CPEActiveReqData) btd.getRD();
        String mainSn=btd.getRD().getUca().getUser().getSerialNumber();
        String mailUserId=btd.getRD().getUca().getUser().getUserId();
        String mailCustId=btd.getRD().getUca().getUser().getCustId();
    	String mailEparchycode=btd.getRD().getUca().getUser().getEparchyCode();
    	String mailCitycode=btd.getRD().getUca().getUser().getCityCode();
        IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getUca().getUserId());
        
        //先取押金金额commpara表param_attr=701 取0账户
		IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "701", "0", "0898");
		String deposit=paras.getData(0).getString("PARA_CODE1");
        
        if (IDataUtil.isEmpty(mainPayRelations))
        {
            // common.error("主卡号码无默认付费帐户！");t
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "主卡号码无默认付费帐户！");
        }

        // ----------组织成员号码付费关系台账----------------
        String mainAcctId = mainPayRelations.getString("ACCT_ID", "-1");
        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            CPEActiveMemberData member = memberList.get(i);
            String modifyTag = member.getModifyTag();
            String memberSn = member.getMemberSn();
          //3、获取默认账户  （acct_id)
	    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(mainSn);
	    	String acctId=accts.getData(0).getString("ACCT_ID");
            if ("0".equals(modifyTag))
            {
            	 
                /**
            	* CPE 扣押金冻结
            	* */
            	
            	IData transParam=new DataMap();
            	transParam.put("DEPOSIT_CODE_IN", "9003");
            	transParam.put("TRANS_BUSINESS_TYPE","2");
            	transParam.put("USER_ID", mailUserId);
            	transParam.put("ACCT_ID", acctId);
            	transParam.put("CUST_ID", mailCustId);
            	transParam.put("TRADE_FEE", deposit);
            	transParam.put("EPARCHY_CODE", mailEparchycode);
            	transParam.put("CITY_CODE", mailCitycode);
            	transParam.put("SERIAL_NUMBER", mainSn); 
	    		IData inAcct=AcctCall.transFEEInFtth(transParam);//调用接口
	    		String result=inAcct.getString("RESULT_CODE","");
	    		if(!"".equals(result) && !"0".equals(result)){ 
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_CRM_TransFeeInFTTH冻结CPE业务押金错误:"+inAcct.getString("RESULT_INFO"));
	    		}
            	
                PayRelationTradeData payTd = new PayRelationTradeData();

                payTd.setAcctId(mainAcctId);
                payTd.setUserId(member.getUca().getUserId());
                payTd.setPayitemCode("-1");//需要改动
                payTd.setAcctPriority("0");
                payTd.setUserPriority("0");
                payTd.setBindType("0");
                payTd.setActTag("1");
                payTd.setDefaultTag("0");
                payTd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                payTd.setLimit("0");
                payTd.setLimitType("0");
                payTd.setComplementTag("0");
                payTd.setAddupMethod("0");
                payTd.setAddupMonths("0");
                payTd.setInstId(SeqMgr.getInstId());
                payTd.setRemark("CPE绑定付费关系");

                payTd.setStartCycleId(member.getStartCycleId());// reqData.getStartCycId()
                payTd.setEndCycleId(mainPayRelations.getString("END_CYCLE_ID"));

                btd.add(reqData.getUca().getSerialNumber(), payTd);

            }
            else if ("1".equals(modifyTag))
            {
            	/**
            	* CPE 解冻押金
            	* */
            	IData transParam=new DataMap();
            	transParam.put("DEPOSIT_CODE_OUT", "9003");
            	transParam.put("TRANS_BUSINESS_TYPE","2");
            	transParam.put("USER_ID", mailUserId);
            	transParam.put("ACCT_ID", acctId);
            	transParam.put("CUST_ID", mailCustId);
            	transParam.put("TRADE_FEE", deposit);
            	transParam.put("EPARCHY_CODE", mailEparchycode);
            	transParam.put("CITY_CODE", mailCitycode);
            	transParam.put("SERIAL_NUMBER", mainSn); 
	    		IData inAcct=AcctCall.adjustFee(transParam);//调用接口
	    		String result=inAcct.getString("RESULT_CODE","");
	    		if(!"".equals(result) && !"0".equals(result)){ 
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_CRM_TransFeeInFTTH解冻CPE业务押金错误:"+inAcct.getString("RESULT_INFO"));
	    		}
                IDataset payRelas = this.getMemberPayRela(member.getUca().getUserId(), mainAcctId);

                PayRelationTradeData payTd = new PayRelationTradeData(payRelas.getData(0));

                payTd.setEndCycleId(DiversifyAcctUtil.getLastDayThisAcct(reqData.getUca().getUserId()).substring(0, 10).replace("-", ""));
                payTd.setRemark("CPE绑定付费关系-解绑");
                payTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                btd.add(reqData.getUca().getSerialNumber(), payTd);

            }
            else if ("2".equals(modifyTag))
            {
            	/**
            	* CPE 扣押金冻结
            	* */
            	
            	IData transParam=new DataMap();
            	transParam.put("DEPOSIT_CODE_IN", "9003");
            	transParam.put("TRANS_BUSINESS_TYPE","2");
            	transParam.put("USER_ID", mailUserId);
            	transParam.put("ACCT_ID", acctId);
            	transParam.put("CUST_ID", mailCustId);
            	transParam.put("TRADE_FEE", deposit);
            	transParam.put("EPARCHY_CODE", mailEparchycode);
            	transParam.put("CITY_CODE", mailCitycode);
            	transParam.put("SERIAL_NUMBER", mainSn); 
	    		IData inAcct=AcctCall.transFEEInFtth(transParam);//调用接口
	    		String result=inAcct.getString("RESULT_CODE","");
	    		if(!"".equals(result) && !"0".equals(result)){ 
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_CRM_TransFeeInFTTH冻结CPE业务押金错误:"+inAcct.getString("RESULT_INFO"));
	    		}
                IDataset payRelas = this.getMemberPayRela(member.getUca().getUserId(), mainAcctId);

                PayRelationTradeData payTd = new PayRelationTradeData(payRelas.getData(0));

                payTd.setEndCycleId(mainPayRelations.getString("END_CYCLE_ID"));
                payTd.setRemark("CPE绑定付费关系");
                payTd.setModifyTag(BofConst.MODIFY_TAG_UPD);

                btd.add(reqData.getUca().getSerialNumber(), payTd);

            }

        }

    }

    public void genTradeRela(BusiTradeData btd, List<CPEActiveMemberData> memberList) throws Exception
    {
        CPEActiveReqData reqData = (CPEActiveReqData) btd.getRD();

        int addCnt = 0;
        int delCnt = 0;
        int modCnt = 0;
        // --------------判断主号是否已办理家庭统付关系---------------
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getUca().getUserId(), "CP", "1");
        // ---第一次办理，则要新加主号的UU关系，否则不需要加主号的uu关系
        String userIdA = "-1";
        String serialNumberA = "-1";
        
        String mainSn=btd.getRD().getUca().getSerialNumber();
        String mainUserid=btd.getRD().getUca().getUserId();

        if (IDataUtil.isEmpty(uuDs))
        {

            userIdA = SeqMgr.getUserId();
            serialNumberA = geneTradeUser(btd, userIdA);

            RelationTradeData mainRTd = new RelationTradeData();// 主号码

            mainRTd.setUserIdA(userIdA);
            mainRTd.setSerialNumberA(serialNumberA);
            mainRTd.setUserIdB(reqData.getUca().getUserId());
            mainRTd.setSerialNumberB(reqData.getUca().getSerialNumber());
            mainRTd.setRelationTypeCode("CP");
            mainRTd.setRoleCodeA("0");
            mainRTd.setRoleCodeB("1");
            mainRTd.setOrderno("");
            mainRTd.setInstId(SeqMgr.getInstId());
            mainRTd.setStartDate(reqData.getAcceptTime());
            mainRTd.setEndDate(SysDateMgr.END_DATE_FOREVER);
            mainRTd.setRemark("CPE绑定付费关系-主号");
            mainRTd.setModifyTag(BofConst.MODIFY_TAG_ADD);

            btd.add(reqData.getUca().getSerialNumber(), mainRTd);

        }
        else
        {
            userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            serialNumberA = uuDs.getData(0).getString("SERIAL_NUMBER_A", "-1");
        }

        for (int i = 0, size = memberList.size(); i < size; i++)
        {

            CPEActiveMemberData member = memberList.get(i);

            String modifyTag = member.getModifyTag();

            if ("0".equals(modifyTag))
            {
            	/**
                 * CHENXY3 CPE活动
                 * 校验主卡余额是否够付押金？
                 * 只有绑定账户才进行判断。
                 * */  
            	//先取押金金额commpara表param_attr=701  para_code1=只有现金账户有金额限制，para_code2=全部账户的总额下限
    			IDataset paras=CommparaInfoQry.getCommByParaAttr("CSM", "701", "0898");
    			String all_deposit=paras.getData(0).getString("PARA_CODE2");//取押金总额下限
    			//3、获取默认账户  （acct_id)		
    	    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(mainSn);
    	    	String acctId=accts.getData(0).getString("ACCT_ID");
    	    	IData param = new DataMap();
    	    	param.put("ACCT_ID", acctId); 
    	    	/**调用账务查询接口*/
    	    	IDataset checkCash= AcctCall.queryAcctDeposit(param); 
    	    	
    	    	String allCash="0";
    	    	if(checkCash!=null && checkCash.size()>0){
    	    		for(int j=0;j<checkCash.size();j++){
    	    			IData acctInfo=checkCash.getData(j);
    		    		String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
    		    		String DEPOSIT_BALANCE=acctInfo.getString("DEPOSIT_BALANCE");//存折余额
    		    		IDataset cashPara=CommparaInfoQry.getCommparaAllCol("CSM", "701", DEPOSIT_CODE, "0898");
    	    			if("0".equals(DEPOSIT_CODE)){
//    	    				String deposit=cashPara.getData(0).getString("PARA_CODE1");
//    	    				if(Integer.parseInt(DEPOSIT_BALANCE)<Integer.parseInt(deposit)){
//    	    					String x_resultinfo="主号现金账户余额不足【"+Integer.parseInt(DEPOSIT_BALANCE)/100+"】元，无法进行绑定操作";
//    	    					CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
//    	    				}else{
//    	    					allCash=""+Integer.parseInt(allCash)+Integer.parseInt(DEPOSIT_BALANCE);
//    	    				}
    	    				allCash=""+(Integer.parseInt(allCash)+Integer.parseInt(DEPOSIT_BALANCE));
    	    			}else{
    	    				if(cashPara!=null && cashPara.size()>0){ //要求用户的账户在COMMPARA表中存在存折账号才允许计算
    	    					allCash=""+(Integer.parseInt(allCash)+Integer.parseInt(DEPOSIT_BALANCE));
    	    				}
    	    			}
    	    		}
    	    		if(Integer.parseInt(allCash)<Integer.parseInt(all_deposit)){
    	    			String x_resultinfo="主号所有账户当前余额【"+Integer.parseInt(allCash)/100+"】，不足【"+Integer.parseInt(all_deposit)/100+"】元，无法进行绑定操作";
    					CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
    	    		}
    	    	}else{
    	    		String x_resultinfo="主号所有账户当前余额【0】，不足【"+Integer.parseInt(all_deposit)/100+"】元，无法进行绑定操作";
					CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
    	    	}
            	//begin QR-20150310-07 898号码是否允许办理统一付费问题 @yanwu add
            	UcaData ucaData = member.getUca();
                UserTradeData users = ucaData.getUser();
                String strNetTypeCode = users.getNetTypeCode();
                if( "11".equals(strNetTypeCode) || "12".equals(strNetTypeCode) 
                 || "13".equals(strNetTypeCode) || "14".equals(strNetTypeCode) || "15".equals(strNetTypeCode)  ){
                	CSAppException.apperr(FamilyException.CRM_FAMILY_834, member.getMemberSn());
                }
                //end

                RelationTradeData td1 = new RelationTradeData();//

                td1.setUserIdA(userIdA);
                td1.setSerialNumberA(serialNumberA);
                td1.setUserIdB(member.getUca().getUserId());
                td1.setSerialNumberB(member.getMemberSn());
                td1.setRelationTypeCode("CP");
                td1.setRoleCodeA("0");
                td1.setRoleCodeB("2");
                td1.setOrderno("");
                td1.setInstId(SeqMgr.getInstId());
                td1.setStartDate(reqData.getAcceptTime());
                td1.setEndDate(SysDateMgr.END_DATE_FOREVER);
                td1.setModifyTag(BofConst.MODIFY_TAG_ADD);
                td1.setRemark("CPE绑定付费关系-副号");

                btd.add(reqData.getUca().getSerialNumber(), td1);
                addCnt++;
            }
            else if ("1".equals(modifyTag))
            {	
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前系统暂时不允许解除绑定CPE。");
            	CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class);
            	IData inData=new DataMap();
            	inData.put("USER_ID", mainUserid);   
            	inData.put("PRODUCT_ID", "99992825");  
            	inData.put("PROCESS_TAG", "0");  
            	IDataset result=bean.checkSnSaleActiveIfEnd(inData);
            	if(result!=null && result.size()>0){
            		String x_resultinfo="主卡【"+mainSn+"】的CPE合约营销活动未终止，不允许解除绑定关系。";
        	    	CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
            	}
                IDataset userRelationInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), "CP", "2");

                if (IDataUtil.isNotEmpty(userRelationInfos))
                {

                    RelationTradeData td2 = new RelationTradeData(userRelationInfos.getData(0));

                    td2.setUserIdB(member.getUca().getUserId());
                    td2.setEndDate(reqData.getEndDate());
                    td2.setModifyTag(BofConst.MODIFY_TAG_DEL);

                    btd.add(reqData.getUca().getSerialNumber(), td2);
                    delCnt++;

                }
                else
                {
                    // common.error("770011", "修改或删除时，获取统一付费成员UU关系出错！");
                    CSAppException.apperr(FamilyException.CRM_FAMILY_918);
                }
            }
            else if ("2".equals(modifyTag))
            {

                IDataset userRelationInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), "CP", "2");

                if (IDataUtil.isNotEmpty(userRelationInfos))
                {

                    RelationTradeData td2 = new RelationTradeData(userRelationInfos.getData(0));

                    td2.setUserIdB(member.getUca().getUserId());
                    td2.setEndDate(SysDateMgr.END_DATE_FOREVER);
                    td2.setModifyTag(BofConst.MODIFY_TAG_UPD);

                    btd.add(reqData.getUca().getSerialNumber(), td2);
                    modCnt++;

                }
                else
                {
                    // common.error("770011", "修改或删除时，获取统一付费成员UU关系出错！");
                    CSAppException.apperr(FamilyException.CRM_FAMILY_918);
                }
            }
        }
        // -------------判断家庭统付成员数量是否超过限制---------------------------------------
        if (addCnt > 0)
        {

            int mebLim = this.getCommParaByFamilyUPay();
            IDataset uuMembers = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdA, "CP", "2");
            int haveMeb = IDataUtil.isEmpty(uuMembers) ? 0 : uuMembers.size();

            if (addCnt + haveMeb > mebLim)
            {
                // common.error("770012", "本次操作导致副卡数量超过["+mebLim+"]个，请确认！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_819, mebLim);
            }
        }
        // -------------判断是否一删除所有家庭统付成员，是的话则要取消主号的UU关系-----------------

        if (addCnt == 0 && modCnt == 0 && delCnt > 0)
        {

            IDataset uuMembers = RelaUUInfoQry.queryForeverValidUnionPayMembers(userIdA, "CP", "2");

            if (IDataUtil.isNotEmpty(uuMembers) && delCnt == uuMembers.size())
            {

                RelationTradeData mainRTd = new RelationTradeData(uuDs.getData(0));

                mainRTd.setEndDate(reqData.getEndDate());
                mainRTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                mainRTd.setRemark("删除所有成员时取消主号UU关系");

                btd.add(reqData.getUca().getSerialNumber(), mainRTd);

                this.checkMainSerialBusiLimits(reqData);

                this.checkTradeLimitBookActives(reqData.getUca().getUserId());
            }
        }
        /**
         * 先删除所有的副号码，tf_f_relation_uu表中主号码的失效时间变成与副号码一起失效， 这个时候新增一个副号码，失效时间为2050年，UU表中主号码的失效时间没有延长与副号码一致，
         */
        if ((addCnt > 0 || modCnt > 0) && IDataUtil.isNotEmpty(uuDs))
        {

            RelationTradeData mainRTd = new RelationTradeData(uuDs.getData(0));

            if (SysDateMgr.decodeTimestamp(mainRTd.getEndDate(), "yyyy-MM-dd").equals(SysDateMgr.decodeTimestamp(reqData.getEndDate(), "yyyy-MM-dd")))
            {

                mainRTd.setEndDate(SysDateMgr.END_DATE_FOREVER);
                mainRTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
                mainRTd.setRemark("新增或修改成员时延长主号UU关系");
                btd.add(reqData.getUca().getSerialNumber(), mainRTd);
            }
        }
        // ---------------------接口进来处理---begin---------
        // 通过接口关闭统一付费关系时，若不存在任何有效的统付成员，则只终止主号的统一付费UU关系
        if ("1".equals(reqData.getIntfFlag()) && "1".equals(reqData.getModifyTag()))
        {

            RelationTradeData mainRTd = new RelationTradeData(uuDs.getData(0));

            mainRTd.setEndDate(reqData.getEndDate());
            mainRTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            mainRTd.setRemark("主号通过接口关闭CPE付费时没有有效的统付成员则只终止自己的UU关系");

            btd.add(reqData.getUca().getSerialNumber(), mainRTd);

            this.checkTradeLimitBookActives(reqData.getUca().getUserId());

        }

        this.checkMainSerialBusiLimits(reqData);
    }

    private int getCommParaByFamilyUPay() throws Exception
    {

        int mebLim = 1;

        IDataset commSet = CommparaInfoQry.getCommPkInfo("CSM", "1010", "CPELIMIT", "0898");

        if (commSet.size() > 0)
        {
            IData temp = commSet.getData(0);
            mebLim = temp != null ? temp.getInt("PARA_CODE1", 1) : 1;
        }
        return mebLim;
    }

    private String getDiversityStartDate(String acctDay, String userId) throws Exception
    {
        String startDate = null;
        // 如果是1号，立即生效[本账期初]
        if ("1".equals(acctDay))
        {
            startDate = DiversifyAcctUtil.getFirstDayThisAcct(userId);
            // 分散用户需要进行账期变更，默认为下账期生效
        }
        else
        {
            startDate = DiversifyAcctUtil.getFirstDayNextAcct(userId);
        }
        return startDate.replaceAll("-", "");
    }

    private IDataset getMemberPayRela(String userId, String acctId) throws Exception
    {
    	//需要改动
        IDataset dataset = PayRelaInfoQry.queryNormalPayre(userId, acctId, "-1", "0");

        if (IDataUtil.isEmpty(dataset))
        {
            // common.error("770070", "获取统一付费成员["+userId+"]付费关系无数据！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_921, userId);
        }

        if (dataset.size() > 1)
        {
            // common.error("770071", "获取统一付费成员["+userId+"]付费关系存在多条数据！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_922, userId);
        }
        return dataset;
    }
    
    
    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd, List<CPEActiveMemberData> memberList) throws Exception
    {   
    	for (int i = 0, size = memberList.size(); i < size; i++)
        {
            CPEActiveMemberData member = memberList.get(i);
            String modifyTag = member.getModifyTag();
            String memberSn = member.getMemberSn();
            String memberUserid=member.getUca().getUserId();
            /**
             *第一次绑定调用接口
             * */
            String firstTimeTag=member.getFirstTimeTag();
            String equipNum=member.getEquipNum();
            CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class);
            
            if ("0".equals(modifyTag))
            {
            	/**
		         * REQ201612260011_新增CPE终端退回和销户界面
		         * @author zhuoyingzhi
		         * @date 20170310
		         * CPE_LOCATION 修改为    CPE_DEVICE
            	 */
		    	IDataset dataset = UserOtherInfoQry.getUserOtherUserId(memberUserid, "CPE_DEVICE", null);// 字段不全
			    if (dataset != null && dataset.size() > 0)
			    { }else{
			    	//这里调用接口,确认输入设备号码是否正确
			    	String custName=btd.getRD().getUca().getCustomer().getCustName();
		    		IData callInput=new DataMap();
		    		callInput.put("SERIAL_NUMBER", memberSn);
		    		callInput.put("BILL_ID", memberSn);
		    		callInput.put("RES_ID", equipNum);
		    		callInput.put("ONLY_CHECK", "1");
		    		IData callBack=bean.checkModem(callInput); //调用接口，判断是否设备合法，不合法抛错了。
		    		callBack.put("RES_ID", equipNum);
		    		callBack.put("DEVICE_COST", "11");
		    		String tradeId=btd.getTradeId();
		    		String resId=callBack.getString("RES_ID");
		    		callInput.put("TRADE_ID", tradeId);
		    		callInput.put("RES_NO", resId);
		    		callInput.put("CUST_NAME", custName);
		    		callInput.put("DEVICE_COST", callBack.getString("DEVICE_COST"));
		    		bean.updateModem(callInput);//调用接口，申领设备。
		    		
		    		CPEActiveReqData rd = (CPEActiveReqData) btd.getRD();
			        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber(); 
			        OtherTradeData otherTradeData = new OtherTradeData();
			        /**
			         * REQ201612260011_新增CPE终端退回和销户界面
			         * @author zhuoyingzhi
			         * @date 20170310
			         * CPE_LOCATION 修改为  CPE_DEVICE
			         */
//			        otherTradeData.setRsrvValueCode("CPE_LOCATION");
			        otherTradeData.setRsrvValueCode("CPE_DEVICE");
			        otherTradeData.setRsrvValue("1");//锁定
			        otherTradeData.setUserId(memberUserid);
			        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
			        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
			        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
			        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
			        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
			        otherTradeData.setRemark(rd.getRemark());          
			        otherTradeData.setRsrvStr3(memberSn);
			        otherTradeData.setRsrvStr4(resId);//终端串号 暂时屏蔽 
			        btd.add(memberSn, otherTradeData); 
			    }
            }
        }
    }
    
    /**
     * @param btd
     * @param memberList
     * @throws Exception
     * @CREATE BY GONGP@2014-8-16
     */
    private void intfDeal(BusiTradeData btd, List<CPEActiveMemberData> memberList) throws Exception
    {

        CPEActiveReqData reqData = (CPEActiveReqData) btd.getRD();

        String modifyTag = reqData.getModifyTag();
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getUca().getUserId(), "CP", "1");
        String lastDateThisMonth = DiversifyAcctUtil.getLastDayThisAcct(reqData.getUca().getUserId());

        if ("1".equals(reqData.getIntfFlag()))
        {// 主号发起

            String userIdA = "-1";

            if ("0".equals(modifyTag))
            {
                if (IDataUtil.isNotEmpty(uuDs))
                {

                    String endDateStr = uuDs.getData(0).getString("END_DATE", "");

                    if (SysDateMgr.string2Date(endDateStr, "yyyy-mm-dd").compareTo(SysDateMgr.string2Date(lastDateThisMonth, "yyyy-mm-dd")) <= 0)
                    {
                        // common.error("660011", "号码["+serialNumber+"]已开通统一付费[已终止但本月内仍有效]！");
                        CSAppException.apperr(FamilyException.CRM_FAMILY_925, reqData.getUca().getSerialNumber());
                    }
                    else
                    {
                        // common.error("660012", "号码["+serialNumber+"]已开通统一付费！");
                        CSAppException.apperr(FamilyException.CRM_FAMILY_926, reqData.getUca().getSerialNumber());
                    }
                }
            }
            else if ("1".equals(modifyTag))
            { 
                if (IDataUtil.isNotEmpty(uuDs))
                {
                    String endDateStr = uuDs.getData(0).getString("END_DATE", "");

                    if (SysDateMgr.string2Date(endDateStr, "yyyy-mm-dd").compareTo(SysDateMgr.string2Date(lastDateThisMonth, "yyyy-mm-dd")) <= 0)
                    {
                        // common.error("660014", "号码["+serialNumber+"]已关闭统一付费,本月内仍有效！");
                        CSAppException.apperr(FamilyException.CRM_FAMILY_932, reqData.getUca().getSerialNumber());
                    }

                    userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");

                    IDataset memberDs = RelaUUInfoQry.getUserRelationAll(userIdA, "CP");

                    if (IDataUtil.isNotEmpty(memberDs))
                    {

                        memberList.clear();

                        for (int i = 0, size = memberDs.size(); i < size; i++)
                        {

                            IData memberRela = memberDs.getData(i);

                            if (memberRela.getString("USER_ID_B").equals(reqData.getUca().getUserId()))// 过滤掉主号
                            {
                                continue;
                            }

                            if (SysDateMgr.string2Date(memberRela.getString("END_DATE"), "yyyy-mm-dd").compareTo(SysDateMgr.string2Date(lastDateThisMonth, "yyyy-mm-dd")) > 0)
                            {

                                CPEActiveMemberData member = new CPEActiveMemberData();

                                member.setMemberSn(memberRela.getString("SERIAL_NUMBER_B"));
                                member.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                UcaData memberUca = UcaDataFactory.getNormalUca(memberRela.getString("SERIAL_NUMBER_B", ""));
                                member.setUca(memberUca);

                                memberList.add(member);
                            }
                        }
                    }

                }
                else
                {
                    // common.error("660013", "号码["+serialNumber+"]未开通统一付费，不能关闭！");
                    CSAppException.apperr(FamilyException.CRM_FAMILY_927, reqData.getUca().getSerialNumber());
                }
            }
            else
            {
                // common.error("660015", "mofiy_tag为非允许的操作类型！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_828);
            }
        }
        else if ("2".equals(reqData.getIntfFlag()))// 副号发起
        {
            if (IDataUtil.isEmpty(uuDs))// 主号未开通报错
            {
                // common.error("660016", "主号["+serialNumber+"]尚未开通统一账户付费关系，请确认！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_929, reqData.getUca().getSerialNumber());
            }

            String userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");

            for (int i = 0; i < memberList.size(); i++)
            {

                CPEActiveMemberData member = memberList.get(i);
                // 查询该成员号码是否是副卡
                IDataset memberRelas = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), "CP", "2");

                if ("0".equals(modifyTag))
                {
                    if (IDataUtil.isNotEmpty(memberRelas))
                    {
                        IData temp = memberRelas.getData(i);

                        String endDateStr = temp.getString("END_DATE", "");
                        // 如果结束时间为月底
                        if (SysDateMgr.string2Date(endDateStr, "yyyy-mm-dd").compareTo(SysDateMgr.string2Date(lastDateThisMonth, "yyyy-mm-dd")) <= 0)
                        {
                            // common.error("660011", "号码["+serialNumber+"]已开通统一付费[已终止但本月内仍有效]！");
                            // CSAppException.apperr(FamilyException.CRM_FAMILY_825, member.getMemberSn());
                            // 延续结束时间为2050
                            member.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        }
                        else
                        {// 已经开通的报错
                            // common.error("660012", "号码["+serialNumber+"]已开通统一付费！");
                            CSAppException.apperr(FamilyException.CRM_FAMILY_926, member.getMemberSn());
                        }
                    }
                    else
                    {// 没有开通过的 执行校验
                        this.checkSeSerialNumber(btd, memberList);
                    }
                }
                else if ("1".equals(modifyTag))
                {// 删除成员
                    if (IDataUtil.isNotEmpty(memberRelas))
                    {
                        IData temp = memberRelas.getData(i);

                        String endDateStr = temp.getString("END_DATE", "");
                        // 结束时间为月底的已经删除过，无需继续执行
                        if (SysDateMgr.string2Date(endDateStr, "yyyy-mm-dd").compareTo(SysDateMgr.string2Date(lastDateThisMonth, "yyyy-mm-dd")) <= 0)
                        {
                            // common.error("660011", "号码["+serialNumber+"]已关闭统一付费[已终止但本月内仍有效]！");
                            CSAppException.apperr(FamilyException.CRM_FAMILY_932, member.getMemberSn());
                        }

                    }
                    else
                    {// 未开通的不能执行删除操作
                        // common.error("660013", "号码["+serialNumber+"]未开通统一付费，不能关闭！");
                        CSAppException.apperr(FamilyException.CRM_FAMILY_927, member.getMemberSn());
                    }
                }
                else
                {
                    // common.error("660015", "mofiy_tag为非允许的操作类型！");
                    CSAppException.apperr(FamilyException.CRM_FAMILY_828);
                }

            }

        }

    }
}
