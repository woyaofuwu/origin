/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyUnionPayBean;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyUnionPayReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.UnionPayMemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.order.trade.ShareMealTrade;

/**
 * @CREATED by gongp@2014-5-22 修改历史 Revision 2014-5-22 上午09:25:09
 */
public class FamilyUnionPayTrade extends BaseTrade implements ITrade
{

    public void acctDayDeal(BusiTradeData btd, List<UnionPayMemberData> memberList) throws Exception
    {

        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();

        String startCycId = reqData.getStartCycId();
        String targetSeriNumber = null;
        String targetAcctDay = null; // 副卡号码结账日

        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            UnionPayMemberData member = memberList.get(i);

            targetSeriNumber = member.getMemberSn();

            if (!StringUtils.isBlank(member.getUca().getNextAcctDay()))
            {
                // common.error("532008: 该用户号码"+ targetSeriNumber + "存在预约的帐期，" +
                // "账期生效时间为" + acctDataNew.getString("NEXT_FIRST_DATE") + "，账期生效后才能办理统一付费业务！ ");
                CSAppException.apperr(FamilyException.CRM_FAMILY_830, targetSeriNumber, member.getUca().getNextAcctDay());
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
    private void checkMainSerialBusiLimits(FamilyUnionPayReqData reqData) throws Exception
    {
        String userId = reqData.getUca().getUserId();

        String serialNumber = reqData.getUca().getSerialNumber();

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
        IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "2");
        if (ds.size() > 0)
        {
            // common.error("880010", "该号码["+serialNumber+"]是其他统一付费关系的副卡，不能作为主卡，请先退出！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_803, serialNumber);
        }
        // -----2.如果该号码存在多条家庭统付关系的主号信息，则提示资料不正常---------
        ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");
        if (ds.size() > 1)
        {
            // common.error("880011", "该号码["+serialNumber+"]存在多条统一付费的主号UU数据，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_804, serialNumber);

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
            /**
             * REQ201803260019++关于申请下放二级副以上领导手机代付铁通固定电话费用的权限的需求
             * 有权限的工号，对领导号码，免密添加
             */
            //先判断是否有权限，号码是否在领导信息表中，都满足，把标识isCmccStaffUser置为false
            String staffId = getVisit().getStaffId();
            boolean hasLimit = StaffPrivUtil.isFuncDataPriv(staffId, "PRIV_TF_LEADER");
            
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumber);
            IDataset results = CSAppCall.call("SS.LeaderInfoSVC.queryLeaderInfo", param);
            if (!IDataUtil.isEmpty(results)&&hasLimit)
            {
        		//判断是否有权限，号码是否在领导信息表中，都满足
        		for (int i = 0; i < ds.size(); i++)
                {
        			IData dsData = ds.getData(i);
        			//270公司员工套餐(VPMN JTZ),领导号码去掉该条限制
        			if(!"270".equals(dsData.getString("DISCNT_CODE"))){
        	            // common.error("880016", "该号码["+serialNumber+"]存在["+ds.getData(0).getString("PARAM_NAME",
        	            // "")+"]优惠，不能作为主卡，请确认！");
        	            CSAppException.apperr(FamilyException.CRM_FAMILY_807, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        			}
                }
            }else {
            	// common.error("880016", "该号码["+serialNumber+"]存在["+ds.getData(0).getString("PARAM_NAME",
            	// "")+"]优惠，不能作为主卡，请确认！");
            	CSAppException.apperr(FamilyException.CRM_FAMILY_807, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
            }
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
    private void checkSeSerialNumber(BusiTradeData btd, List<UnionPayMemberData> memberList) throws Exception
    {

        FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);
        for (int i = 0; i < memberList.size(); i++)
        {

            UnionPayMemberData member = memberList.get(i);
            IData input = new DataMap();
            input.put("CHECK_SERIAL_NUMBER", member.getMemberSn());
            input.put("MAIN_SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());

            bean.checkBySerialNumber(input);

        }
    }

    private void checkTradeLimitBookActives(String userId) throws Exception
    {

        IDataset dataset = SaleActiveInfoQry.queryTradeLimitBookActives(userId, "325");

        if (IDataUtil.isNotEmpty(dataset))
        {
            // common.error("770013", "主号存在预约办理的活动["+ds.getData(0).getString("PRODUCT_NAME",
            // "")+"]不能解散统一付费，请先返销该预约活动后，再来办理！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_820, dataset.getData(0).getString("PRODUCT_NAME", ""));
        }
    }
    //当副号码为物联网号码 并且办理了和路通活动,则不允许主号码解除统一付费关系 add by fangwz
    private void delCheckTradeActives(String userIdB,String brandCode,String sn) throws Exception
    {
    	//和路通集团成员约定消费送终端活动
        IDataset dataset = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userIdB, "69170527","60170527");
        //和路通个人客户约定消费送终端活动
        IDataset datasetgroup = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userIdB, "69170528","60170528");
        if (IDataUtil.isNotEmpty(dataset)||IDataUtil.isNotEmpty(datasetgroup)&&"PWLW".equals(brandCode))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_68800935, sn);
        }
    }
    
    
    //当副号码的主号码是不限量158套餐有特殊标示RSRV_TAG1为1，则不允许解除统一付费关系 add by xuyt
    private void delCheckTradeUserProductId(String userId,String rsrvTag1, BusiTradeData btd) throws Exception
    {
    	//流量不限量158 元套餐 80003014
    	IDataset userP = UserProductInfoQry.getUserProductByUserIdProductId(userId, "80003014");

    	if(IDataUtil.isNotEmpty(userP) && "1".equals(rsrvTag1))
    	{
    		if(StringUtils.isNotBlank(btd.getMainTradeData().getRemark()) && !btd.getMainTradeData().getRemark().contains("158不限量套餐连带删除"))
            {
    			CSAppException.appError("68800936", "副号码的主号码是不限量158套餐，不允许解除统一付费关系！");
            }
    	}
    }
    

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();

        List<UnionPayMemberData> memberList = reqData.getMemberList();

        String endDate = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.getSysDate(), Integer.parseInt(reqData.getUca().getAcctDay())) + SysDateMgr.END_DATE;

        String startCycId = this.getDiversityStartDate(reqData.getUca().getAcctDay(), reqData.getUca().getUserId());
       
        String endDateLastMonth = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.addMonths(SysDateMgr.getSysDate(), -1), Integer.parseInt(reqData.getUca().getAcctDay()))+ SysDateMgr.END_DATE;

        String endDate_flag = reqData.getEndDate_flag();
        MainTradeData mainTD = btd.getMainTradeData();
        if("1".equals(endDate_flag)){
        	reqData.setEndDate(endDateLastMonth);
        	
            mainTD.setRemark("统一付费特殊终止");
            mainTD.setRsrvStr10(endDate_flag);

        }else{

        	reqData.setEndDate(endDate);

            mainTD.setRemark(reqData.getRemark());

        }

        reqData.setStartCycId(startCycId);

        this.intfDeal(btd, memberList);

        this.acctDayDeal(btd, memberList);

        this.genTradeRela(btd, memberList);

        this.genTradePayrelation(btd, memberList);

        mainTD.setRsrvStr1(reqData.getxTag());

    }

    public void createProductTradeData(BusiTradeData btd, String serialNumber, String userId) throws Exception
    {
        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();

        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(userId);
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getUca().getProductId());
        productTD.setProductMode("00");
        productTD.setBrandCode("JTTF");
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getAcceptTime());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");
        btd.add(serialNumber, productTD);
    }

    private String geneTradeUser(BusiTradeData btd, String userIdA) throws Exception
    {

        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();

        CustomerTradeData custData = ucaDta.getCustomer();
        UserTradeData userData = new UserTradeData();// ucaDta.getUser().clone();
        //防止虚拟用户手机号码重复，改成56+主号手机号码作为虚拟用户的号码。
        //String serialNumber = "56" + userIdA.substring(userIdA.length() - 8); // User表的SerialNumber
        String serialNumber = "56" + ucaDta.getSerialNumber(); // User表的SerialNumber
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
    public void genTradePayrelation(BusiTradeData btd, List<UnionPayMemberData> memberList) throws Exception
    {

        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();

        IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getUca().getUserId());

        if (IDataUtil.isEmpty(mainPayRelations))
        {
            // common.error("主卡号码无默认付费帐户！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "主卡号码无默认付费帐户！");
        }

        // ----------组织成员号码付费关系台账----------------
        String mainAcctId = mainPayRelations.getString("ACCT_ID", "-1");
        IData lastPayRelations = UcaInfoQry.qryLastPayRelaByUserId(reqData.getUca().getUserId());
        String lastEndCycleId = lastPayRelations.getString("END_CYCLE_ID");
        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            UnionPayMemberData member = memberList.get(i);
            String modifyTag = member.getModifyTag();

            if ("0".equals(modifyTag))
            {

                PayRelationTradeData payTd = new PayRelationTradeData();

                payTd.setAcctId(mainAcctId);
                payTd.setUserId(member.getUca().getUserId());
                //REQ201810180017关于放开二级副以上领导铁通固定电话统付权限的需求  wuhao5
                //如果副号是铁通固话号码,则写值为-1
                if (StaticUtil.getStaticValue("TD_SN_CODE", member.getUca().getBrandCode()) != null){ 
                	payTd.setPayitemCode("-1");
                }else{
                    //REQ202001300001  关于个人统付功能新增指定业务统付的需求
                    if(StringUtils.isNotEmpty(reqData.getPayitemCode())) {
                        payTd.setPayitemCode(reqData.getPayitemCode());
                    }else {
                        payTd.setPayitemCode("41000");
                    }
                }
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
                payTd.setRemark("统付成员付费关系");

                payTd.setStartCycleId(member.getStartCycleId());// reqData.getStartCycId()
                payTd.setEndCycleId(lastEndCycleId);

                btd.add(reqData.getUca().getSerialNumber(), payTd);

            }
            else if ("1".equals(modifyTag))
            {

                IDataset payRelas = this.getMemberPayRela(member.getUca().getUserId(), mainAcctId);

                PayRelationTradeData payTd = new PayRelationTradeData(payRelas.getData(0));
                
                if("1".equals(reqData.getEndDate_flag())){
                    String endDateLastMonth = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.addMonths(SysDateMgr.getSysDate(), -1), Integer.parseInt(reqData.getUca().getAcctDay()));
                    String endCycleId = SysDateMgr.getChinaDate(endDateLastMonth, SysDateMgr.PATTERN_TIME_YYYYMMDD);
                    payTd.setEndCycleId(endCycleId);

                }else{
                	payTd.setEndCycleId(DiversifyAcctUtil.getLastDayThisAcct(reqData.getUca().getUserId()).substring(0, 10).replace("-", ""));
                }
                payTd.setRemark("统付成员付费关系");
                payTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                btd.add(reqData.getUca().getSerialNumber(), payTd);

            }
            else if ("2".equals(modifyTag))
            {

                IDataset payRelas = this.getMemberPayRela(member.getUca().getUserId(), mainAcctId);

                PayRelationTradeData payTd = new PayRelationTradeData(payRelas.getData(0));
                if("1".equals(reqData.getEndDate_flag())){
                    String endDateLastMonth = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.addMonths(SysDateMgr.getSysDate(), -1), Integer.parseInt(reqData.getUca().getAcctDay()));
                    String endCycleId = SysDateMgr.getChinaDate(endDateLastMonth, SysDateMgr.PATTERN_TIME_YYYYMMDD);
                    payTd.setEndCycleId(endCycleId);
                }else{
                	payTd.setEndCycleId(lastEndCycleId);
                }
                payTd.setRemark("统付成员付费关系");
                payTd.setModifyTag(BofConst.MODIFY_TAG_UPD);

                btd.add(reqData.getUca().getSerialNumber(), payTd);

            }

        }

    }

    public void genTradeRela(BusiTradeData btd, List<UnionPayMemberData> memberList) throws Exception
    {
        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();

        int addCnt = 0;
        int delCnt = 0;
        int modCnt = 0;
        // --------------判断主号是否已办理家庭统付关系---------------
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getUca().getUserId(), "56", "1");
        // ---第一次办理，则要新加主号的UU关系，否则不需要加主号的uu关系
        String userIdA = "-1";
        String serialNumberA = "-1";

        if (IDataUtil.isEmpty(uuDs))
        {

            userIdA = SeqMgr.getUserId();
            serialNumberA = geneTradeUser(btd, userIdA);

            RelationTradeData mainRTd = new RelationTradeData();// 主号码

            mainRTd.setUserIdA(userIdA);
            mainRTd.setSerialNumberA(serialNumberA);
            mainRTd.setUserIdB(reqData.getUca().getUserId());
            mainRTd.setSerialNumberB(reqData.getUca().getSerialNumber());
            mainRTd.setRelationTypeCode("56");
            mainRTd.setRoleCodeA("0");
            mainRTd.setRoleCodeB("1");
            mainRTd.setOrderno("");
            mainRTd.setInstId(SeqMgr.getInstId());
            mainRTd.setStartDate(reqData.getAcceptTime());
            mainRTd.setEndDate(SysDateMgr.END_DATE_FOREVER);
            mainRTd.setRemark("统一付费关系主号");
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

            UnionPayMemberData member = memberList.get(i);

            String modifyTag = member.getModifyTag();

            if ("0".equals(modifyTag))
            {
            	//begin QR-20150310-07 898号码是否允许办理统一付费问题 @yanwu add
            	UcaData ucaData = member.getUca();
                UserTradeData users = ucaData.getUser();
                String strNetTypeCode = users.getNetTypeCode();
                //REQ201810180017关于放开二级副以上领导铁通固定电话统付权限的需求  wuhao5
                //副号为铁通固话号码时,不用拦截校验
    			/*----铁通固话，是通过以下品牌判断------------=
    			brand_code  
    			TT01  TT03  TT05 是铁通有线固话 
    			TT02  TT04  TD1代*/
                if (StaticUtil.getStaticValue("TD_SN_CODE", ucaData.getBrandCode()) == null){
	                if( "11".equals(strNetTypeCode) || "12".equals(strNetTypeCode) 
	                 || "13".equals(strNetTypeCode) || "14".equals(strNetTypeCode) || "15".equals(strNetTypeCode)  ){
	                	CSAppException.apperr(FamilyException.CRM_FAMILY_834, member.getMemberSn());
	                }
                }
                //end

                RelationTradeData td1 = new RelationTradeData();//

                td1.setUserIdA(userIdA);
                td1.setSerialNumberA(serialNumberA);
                td1.setUserIdB(member.getUca().getUserId());
                td1.setSerialNumberB(member.getMemberSn());
                td1.setRelationTypeCode("56");
                td1.setRoleCodeA("0");
                td1.setRoleCodeB("2");
                td1.setOrderno("");
                td1.setInstId(SeqMgr.getInstId());
                td1.setStartDate(reqData.getAcceptTime());
                td1.setEndDate(SysDateMgr.END_DATE_FOREVER);
                td1.setModifyTag(BofConst.MODIFY_TAG_ADD);
                td1.setRemark("统一付费关系副号");
                if(StringUtils.isNotBlank(btd.getMainTradeData().getRemark()) && btd.getMainTradeData().getRemark().contains("158不限量套餐连带办理"))
                {
                	td1.setRsrvTag1("1");
                }

                btd.add(reqData.getUca().getSerialNumber(), td1);
                addCnt++;
            }
            else if ("1".equals(modifyTag))
            { 
            	
            	 IDataset discntConfig = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9957",null,null);
                 if(IDataUtil.isNotEmpty(discntConfig)){
                 	for(int j=0;j<discntConfig.size();j++){
                 		if("Y".equals(discntConfig.getData(j).getString("PARA_CODE6"))){
                 			String paraCode4 = discntConfig.getData(j).getString("PARA_CODE4");//新卡办理的pakcage_id
                 			String paraCode10 = discntConfig.getData(j).getString("PARA_CODE10");//REQ201905130012 关于办理2018抢4G手机红包活动的用户取消统付优化--开关
                 		    IData params =new DataMap();
                           	params.put("PACKAGE_ID", discntConfig.getData(j).getString("PARA_CODE1"));
                           	params.put("USER_ID", reqData.getUca().getUserId());
                           	IDataset iDataset=Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params); 
                           	if (iDataset.size() > 0){
                           		if("Y".equals(paraCode10)){
                           			params.clear();
                               	 	params.put("PACKAGE_ID", paraCode4);
                                   	params.put("USER_ID", member.getUca().getUserId());
                                	IDataset saleActives=Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params); 
                                	if (saleActives.size() > 0){
                                		 CSAppException.apperr(FamilyException.CRM_FAMILY_66000238, member.getMemberSn(),saleActives.getData(0).getString("PACKAGE_NAME"));
                                	}
                           		}else{
                                    String paraCode20 = discntConfig.getData(j).getString("PARA_CODE20");
                           		    if(!"Y".equals(paraCode20)){
                                        CSAppException.apperr(FamilyException.CRM_FAMILY_66000238, reqData.getUca().getSerialNumber(),iDataset.getData(0).getString("PACKAGE_NAME"));
                                    }
                           		}
                           		
                           		
 	                        }
                 		}
                     }
                 }

                IDataset userRelationInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), "56", "2");

                if (IDataUtil.isNotEmpty(userRelationInfos))
                {
                	//当副号码为物联网号码 并且办理了和路通活动 则不允许主号码解除统一付费关系
                	this.delCheckTradeActives(member.getUca().getUserId(),member.getUca().getBrandCode(),member.getUca().getSerialNumber());
                	//当副号码的主号码是不限量158套餐有特殊标示RSRV_TAG1为1，则不允许解除统一付费关系
                	
                	this.delCheckTradeUserProductId(reqData.getUca().getUserId(),userRelationInfos.getData(0).getString("RSRV_TAG1",""),btd);
                    
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
                    CSAppException.apperr(FamilyException.CRM_FAMILY_818);
                }
            }
            else if ("2".equals(modifyTag))
            {

                IDataset userRelationInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), "56", "2");

                if (IDataUtil.isNotEmpty(userRelationInfos))
                {

                    RelationTradeData td2 = new RelationTradeData(userRelationInfos.getData(0));

                    td2.setUserIdB(member.getUca().getUserId());
                    td2.setEndDate(SysDateMgr.END_DATE_FOREVER);
                    td2.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    if(StringUtils.isNotBlank(btd.getMainTradeData().getRemark()) && btd.getMainTradeData().getRemark().contains("158不限量套餐连带办理"))
                    {
                    	td2.setRsrvTag1("1");
                    }

                    btd.add(reqData.getUca().getSerialNumber(), td2);
                    modCnt++;

                }
                else
                {
                    // common.error("770011", "修改或删除时，获取统一付费成员UU关系出错！");
                    CSAppException.apperr(FamilyException.CRM_FAMILY_818);
                }
            }
        }
        // -------------判断家庭统付成员数量是否超过限制---------------------------------------
        if (addCnt > 0)
        {

            int mebLim = this.getCommParaByFamilyUPay();
            IDataset uuMembers = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdA, "56", "2");
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
        	IDataset uuMembers = null;
        	String endDate_flag = reqData.getEndDate_flag();
        	if("1".equals(endDate_flag))
        	{
        		uuMembers = RelaUUInfoQry.queryAllMembersAndEndMonth(userIdA, "56", "2");
            }
        	else
        	{
        		uuMembers = RelaUUInfoQry.queryForeverValidUnionPayMembers(userIdA, "56", "2");
			}
        	
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
            mainRTd.setRemark("主号通过接口关闭统一付费时没有有效的统付成员则只终止自己的UU关系");

            btd.add(reqData.getUca().getSerialNumber(), mainRTd);

            this.checkTradeLimitBookActives(reqData.getUca().getUserId());

        }

        this.checkMainSerialBusiLimits(reqData);
    }

    private int getCommParaByFamilyUPay() throws Exception
    {

        int mebLim = 9;

        IDataset commSet = CommparaInfoQry.getCommPkInfo("CSM", "1010", "FAMILY_UPAY_LIMIT", "0898");

        if (commSet.size() > 0)
        {
            IData temp = commSet.getData(0);
            mebLim = temp != null ? temp.getInt("PARA_CODE1", 9) : 9;
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

        //REQ202001300001  关于个人统付功能新增指定业务统付的需求
        IDataset dataset = new DatasetList();

        IDataset payItemCodeList = StaticUtil.getStaticList("PAYITEM_CODE_TYPE");
        if(IDataUtil.isNotEmpty(payItemCodeList)) {
            for (int i = 0; i < payItemCodeList.size(); i++) {
                String payItemCodeName = payItemCodeList.getData(i).getString("DATA_ID", "");
                dataset = PayRelaInfoQry.queryNormalPayre(userId, acctId, payItemCodeName, "0");

                if(IDataUtil.isNotEmpty(dataset)) {
                    break;
                }

            }
        }

        if(IDataUtil.isEmpty(dataset)) {
            dataset = PayRelaInfoQry.queryNormalPayre(userId, acctId, "41000", "0");
        }

        if (IDataUtil.isEmpty(dataset))
        {
            // common.error("770070", "获取统一付费成员["+userId+"]付费关系无数据！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_821, userId);
        }

        if (dataset.size() > 1)
        {
            // common.error("770071", "获取统一付费成员["+userId+"]付费关系存在多条数据！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_822, userId);
        }
        return dataset;
    }

    /**
     * @param btd
     * @param memberList
     * @throws Exception
     * @CREATE BY GONGP@2014-8-16
     */
    private void intfDeal(BusiTradeData btd, List<UnionPayMemberData> memberList) throws Exception
    {

        FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();

        String modifyTag = reqData.getModifyTag();
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getUca().getUserId(), "56", "1");
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
                        CSAppException.apperr(FamilyException.CRM_FAMILY_825, reqData.getUca().getSerialNumber());
                    }
                    else
                    {
                        // common.error("660012", "号码["+serialNumber+"]已开通统一付费！");
                        CSAppException.apperr(FamilyException.CRM_FAMILY_826, reqData.getUca().getSerialNumber());
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
                        CSAppException.apperr(FamilyException.CRM_FAMILY_832, reqData.getUca().getSerialNumber());
                    }

                    userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");

                    IDataset memberDs = RelaUUInfoQry.getUserRelationAll(userIdA, "56");

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

                                UnionPayMemberData member = new UnionPayMemberData();

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
                    CSAppException.apperr(FamilyException.CRM_FAMILY_827, reqData.getUca().getSerialNumber());
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
                CSAppException.apperr(FamilyException.CRM_FAMILY_829, reqData.getUca().getSerialNumber());
            }

            String userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");

            for (int i = 0; i < memberList.size(); i++)
            {

                UnionPayMemberData member = memberList.get(i);
                // 查询该成员号码是否是副卡
                IDataset memberRelas = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), "56", "2");

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
                            CSAppException.apperr(FamilyException.CRM_FAMILY_826, member.getMemberSn());
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
                            CSAppException.apperr(FamilyException.CRM_FAMILY_832, member.getMemberSn());
                        }

                    }
                    else
                    {// 未开通的不能执行删除操作
                        // common.error("660013", "号码["+serialNumber+"]未开通统一付费，不能关闭！");
                        CSAppException.apperr(FamilyException.CRM_FAMILY_827, member.getMemberSn());
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
