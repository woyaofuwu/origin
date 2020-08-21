/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.PersonGrpUserUnionPayBean;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.PersonGrpUserUnionPayMemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.PersonGrpUserUnionPayReqData;

/**
 * 和商务融合产品包订购业务登记台帐处理类
 * @author chenzg
 *
 */
public class PersonGrpUserUnionPayTrade extends BaseTrade implements ITrade
{
	/**
	 * 分散账期处理
	 * @param btd
	 * @param memberList
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-4-11
	 */
    public void acctDayDeal(BusiTradeData btd, List<PersonGrpUserUnionPayMemberData> memberList) throws Exception
    {
        PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
        String startCycId = reqData.getStartCycId();
        String targetSeriNumber = null;
        String targetAcctDay = null; // 副卡号码结账日
        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            PersonGrpUserUnionPayMemberData member = memberList.get(i);
            targetSeriNumber = member.getMemberSn();
            if (!StringUtils.isBlank(member.getUca().getNextAcctDay()))
            {
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
     * @param firstDayNextAcct
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
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
     * @param reqData
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private void checkMainSerialBusiLimits(PersonGrpUserUnionPayReqData reqData) throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String serialNumber = reqData.getUca().getSerialNumber();
        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);
        if (dataset.size() < 1)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");
        //非正常状态用户不能办理业务
        if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_802, serialNumber);
        }
        //主卡不能是其他家庭统付关系的副卡
        IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");
        if (ds.size() > 0)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_803, serialNumber);
        }
        //如果该号码存在多条家庭统付关系的主号信息，则提示资料不正常
        ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "1");
        if (ds.size() > 1)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_804, serialNumber);
        }
        //随E行、移动公话、8位或11位TD无线固话不可作为主卡
        ds = CommparaInfoQry.getCommparaByCode1("CSM", "698", "1", productId);
        if (ds.size() > 0)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_805, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
    }

    /**
     * 副号码校验
     * @param btd
     * @param memberList
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private void checkSeSerialNumber(BusiTradeData btd, List<PersonGrpUserUnionPayMemberData> memberList) throws Exception
    {

    	PersonGrpUserUnionPayBean bean = BeanManager.createBean(PersonGrpUserUnionPayBean.class);
        for (int i = 0; i < memberList.size(); i++)
        {
            PersonGrpUserUnionPayMemberData member = memberList.get(i);
            IData input = new DataMap();
            input.put("CHECK_SERIAL_NUMBER", member.getMemberSn());
            input.put("MAIN_SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
            bean.checkBySerialNumber(input);
        }
    }
    
    /**
     * 主号码为办理了“2018年和商务融合产品包”营销活动的用户不允许取消统付关系
     * @param userIdB
     * @param brandCode
     * @param sn
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private void delCheckTradeActives(String userId,String brandCode,String sn) throws Exception
    {
    	//2018年和商务融合产品包
        IDataset dataset = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, "69800801","69008001");
        if (IDataUtil.isNotEmpty(dataset))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_7002, sn);
        }
    }
    
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
        List<PersonGrpUserUnionPayMemberData> memberList = reqData.getMemberList();
        String endDate = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.getSysDate(), Integer.parseInt(reqData.getUca().getAcctDay())) + SysDateMgr.END_DATE;
        String startCycId = this.getDiversityStartDate(reqData.getUca().getAcctDay(), reqData.getUca().getUserId());
        reqData.setEndDate(endDate);
        reqData.setStartCycId(startCycId);
        //this.intfDeal(btd, memberList);
        this.acctDayDeal(btd, memberList);
        this.genTradeRela(btd, memberList);
        this.genTradePayrelation(btd, memberList);
        this.genTradeOther(btd, memberList);
        this.checkUuMemGroupIdIsOne(btd, memberList);
        MainTradeData mainTD = btd.getMainTradeData();
        mainTD.setRemark(reqData.getRemark());
        mainTD.setRsrvStr1(reqData.getxTag());
    }
    /**
     * 生成虚拟用户的主产品台帐
     * @param btd
     * @param serialNumber
     * @param userId
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void createProductTradeData(BusiTradeData<BaseTradeData> btd, String serialNumber, String userId) throws Exception
    {
        PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
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
    /**
     * 生成虚拟用户台帐
     * @param btd
     * @param userIdA
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private String geneTradeUser(BusiTradeData<BaseTradeData> btd, String userIdA) throws Exception
    {
        PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
        UcaData ucaDta = reqData.getUca();
        CustomerTradeData custData = ucaDta.getCustomer();
        UserTradeData userData = new UserTradeData();// ucaDta.getUser().clone();
        //防止虚拟用户手机号码重复，改成57+主号手机号码作为虚拟用户的号码。
        String serialNumber = PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE + ucaDta.getSerialNumber(); // User表的SerialNumber
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
     * 付费关系台帐处理
     * 简单算法描述： 用主号的默认付费acct_id给副号加一条非默认的付费关系
     * @param btd
     * @param memberList
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void genTradePayrelation(BusiTradeData<BaseTradeData> btd, List<PersonGrpUserUnionPayMemberData> memberList) throws Exception
    {
        PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
        IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getUca().getUserId());
        
        String groupPackStr = reqData.getGroupPack();
        IDataset checkGroupPack = CommparaInfoQry.getCommparaInfoByCode("CSM", "1698",groupPackStr,groupPackStr,"0898");
        
        String remark = "";
        if(IDataUtil.isNotEmpty(checkGroupPack)){
        	remark = checkGroupPack.getData(0).getString("PARAM_NAME");	
        }
        
                
        if (IDataUtil.isEmpty(mainPayRelations))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "主卡号码无默认付费帐户！");
        }

        // ----------组织成员号码付费关系台账----------------
        String mainAcctId = mainPayRelations.getString("ACCT_ID", "-1");
        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            PersonGrpUserUnionPayMemberData member = memberList.get(i);
            String modifyTag = member.getModifyTag();
            String subRemark =  remark+"统付关系";

            if ("0".equals(modifyTag))
            {
                PayRelationTradeData payTd = new PayRelationTradeData();
                payTd.setAcctId(mainAcctId);
                payTd.setUserId(member.getUca().getUserId());
                payTd.setPayitemCode("41000");
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
                payTd.setRsrvStr9("PGPAY");
                payTd.setRemark(subRemark);
                payTd.setStartCycleId(member.getStartCycleId());// reqData.getStartCycId()
                payTd.setEndCycleId(mainPayRelations.getString("END_CYCLE_ID"));
                btd.add(reqData.getUca().getSerialNumber(), payTd);
            }
            else if ("1".equals(modifyTag))
            {
                IDataset payRelas = this.getMemberPayRela(member.getUca().getUserId(), mainAcctId);
                PayRelationTradeData payTd = new PayRelationTradeData(payRelas.getData(0));
                payTd.setEndCycleId(DiversifyAcctUtil.getLastDayThisAcct(reqData.getUca().getUserId()).substring(0, 10).replace("-", ""));
                payTd.setRemark(subRemark);
                payTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(reqData.getUca().getSerialNumber(), payTd);
            }
            else if ("2".equals(modifyTag))
            {
                IDataset payRelas = this.getMemberPayRela(member.getUca().getUserId(), mainAcctId);
                PayRelationTradeData payTd = new PayRelationTradeData(payRelas.getData(0));
                payTd.setEndCycleId(mainPayRelations.getString("END_CYCLE_ID"));
                payTd.setRemark(subRemark);
                payTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
                btd.add(reqData.getUca().getSerialNumber(), payTd);
            }
        }

    }
    /**
     * 处理UU关系台帐
     * @param btd
     * @param memberList
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void genTradeRela(BusiTradeData<BaseTradeData> btd, List<PersonGrpUserUnionPayMemberData> memberList) throws Exception
    {
        PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
        String groupPackStr = reqData.getGroupPack();
        IDataset checkGroupPack = CommparaInfoQry.getCommparaInfoByCode("CSM", "1698",groupPackStr,groupPackStr,"0898");
        
        String remark = "";
        if(IDataUtil.isNotEmpty(checkGroupPack)){
        	remark = checkGroupPack.getData(0).getString("PARAM_NAME");
        	
        }
        
        int addCnt = 0;
        int delCnt = 0;
        int modCnt = 0;
        // --------------判断主号是否已办理家庭统付关系---------------
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getUca().getUserId(), PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "1");
        // ---第一次办理，则要新加主号的UU关系，否则不需要加主号的uu关系
        String userIdA = "-1";
        String serialNumberA = "-1";

        if (IDataUtil.isEmpty(uuDs))
        {
        	String mainRemark = remark+"订购统付主号";
            userIdA = SeqMgr.getUserId();
            serialNumberA = geneTradeUser(btd, userIdA);
            RelationTradeData mainRTd = new RelationTradeData();// 主号码
            mainRTd.setUserIdA(userIdA);
            mainRTd.setSerialNumberA(serialNumberA);
            mainRTd.setUserIdB(reqData.getUca().getUserId());
            mainRTd.setSerialNumberB(reqData.getUca().getSerialNumber());
            mainRTd.setRelationTypeCode(PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE);
            mainRTd.setRoleCodeA("0");
            mainRTd.setRoleCodeB("1");
            mainRTd.setOrderno("");
            mainRTd.setInstId(SeqMgr.getInstId());
            mainRTd.setStartDate(reqData.getAcceptTime());
            mainRTd.setEndDate(SysDateMgr.END_DATE_FOREVER);
            mainRTd.setRemark(mainRemark);
            mainRTd.setModifyTag(BofConst.MODIFY_TAG_ADD);
            mainRTd.setRsrvStr1(reqData.getGroupPack());
            btd.add(reqData.getUca().getSerialNumber(), mainRTd);
        }
        else
        {
            userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            serialNumberA = uuDs.getData(0).getString("SERIAL_NUMBER_A", "-1");
        }

        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            PersonGrpUserUnionPayMemberData member = memberList.get(i);
            String modifyTag = member.getModifyTag();
            if ("0".equals(modifyTag))
            {   
            	String memberRemark = remark+"订购统付副号";
            	UcaData ucaData = member.getUca();
                UserTradeData users = ucaData.getUser();
                String strNetTypeCode = users.getNetTypeCode();
                if( "11".equals(strNetTypeCode) || "12".equals(strNetTypeCode) 
                 || "13".equals(strNetTypeCode) || "14".equals(strNetTypeCode) || "15".equals(strNetTypeCode)  ){
                	CSAppException.apperr(FamilyException.CRM_FAMILY_834, member.getMemberSn());
                }
                RelationTradeData td1 = new RelationTradeData();//
                td1.setUserIdA(userIdA);
                td1.setSerialNumberA(serialNumberA);
                td1.setUserIdB(member.getUca().getUserId());
                td1.setSerialNumberB(member.getMemberSn());
                td1.setRelationTypeCode(PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE);
                td1.setRoleCodeA("0");
                td1.setRoleCodeB("2");
                td1.setOrderno("");
                td1.setInstId(SeqMgr.getInstId());
                td1.setStartDate(reqData.getAcceptTime());
                td1.setEndDate(SysDateMgr.END_DATE_FOREVER);
                td1.setModifyTag(BofConst.MODIFY_TAG_ADD);
                td1.setRemark(memberRemark);
                td1.setRsrvStr1(reqData.getGroupPack());
                btd.add(reqData.getUca().getSerialNumber(), td1);
                addCnt++;
            }
            else if ("1".equals(modifyTag))
            {
                IDataset userRelationInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");
                if (IDataUtil.isNotEmpty(userRelationInfos))
                {
                	//当主号码为办理了“2018年和商务融合产品包”营销活动的用户不允许取消统付关系
                	this.delCheckTradeActives(reqData.getUca().getUserId(),reqData.getUca().getBrandCode(),reqData.getUca().getSerialNumber());
                    RelationTradeData td2 = new RelationTradeData(userRelationInfos.getData(0));
                    td2.setUserIdB(member.getUca().getUserId());
                    td2.setEndDate(reqData.getEndDate());
                    td2.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(reqData.getUca().getSerialNumber(), td2);
                    delCnt++;
                }
                else
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_818);
                }
            }
            else if ("2".equals(modifyTag))
            {
                IDataset userRelationInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");
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
                    CSAppException.apperr(FamilyException.CRM_FAMILY_818);
                }
            }
        }
        // -------------判断家庭统付成员数量是否超过限制---------------------------------------
        if (addCnt > 0)
        {
            int mebLim = this.getCommParaByFamilyUPay();
            IDataset uuMembers = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdA, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");
            int haveMeb = IDataUtil.isEmpty(uuMembers) ? 0 : uuMembers.size();
            if (addCnt + haveMeb > mebLim)
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_819, mebLim);
            }
        }
        // -------------判断是否已删除所有家庭统付成员，是的话则要取消主号的UU关系-----------------
        if (addCnt == 0 && modCnt == 0 && delCnt > 0)
        {
            IDataset uuMembers = RelaUUInfoQry.queryForeverValidUnionPayMembers(userIdA, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");
            if (IDataUtil.isNotEmpty(uuMembers) && delCnt == uuMembers.size())
            {
                RelationTradeData mainRTd = new RelationTradeData(uuDs.getData(0));
                mainRTd.setEndDate(reqData.getEndDate());
                mainRTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                mainRTd.setRemark("删除所有成员时取消主号UU关系");
                btd.add(reqData.getUca().getSerialNumber(), mainRTd);
                this.checkMainSerialBusiLimits(reqData);
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
        //主卡业务限制校验
        this.checkMainSerialBusiLimits(reqData);
    }
    /**
     * 生成other表台帐处理
     * @param btd
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private void genTradeOther(BusiTradeData<BaseTradeData> btd, List<PersonGrpUserUnionPayMemberData> memberList) throws Exception
    {
    	PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
    	/*如果该手机号码统付的用户已经包含  商务宽带、多媒体桌面电话  两种用户，那么给手机号码打上“和商务”标识。
    	 * 取消统付关系时，同时取消“和商务”标识；
    	 * --记录到tf_f_user_other表*/
    	//主号用户
    	String mainUserId = reqData.getUca().getUserId();
    	//取主号目前存在的统付成员,找出当前成员中商务宽带、多媒体桌面电话两种用户
    	IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(mainUserId, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "1");
    	IDataset uuMembers = new DatasetList();
    	IData memMap = new DataMap();
        if (uuDs.size() > 0)
        {
            String userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            uuMembers = RelaUUInfoQry.queryAllValidPgUnionPayMembers(userIdA, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");
            if(IDataUtil.isNotEmpty(uuMembers)){
            	for(int i=0;i<uuMembers.size();i++){
            		IData each = uuMembers.getData(i);
            		String memSn = each.getString("SERIAL_NUMBER_B", "");
            		IData memUserInfo = UcaInfoQry.qryUserMainProdInfoBySn(memSn, RouteInfoQry.getEparchyCodeBySn(memSn));
            		if(IDataUtil.isNotEmpty(memUserInfo)){
            			String memProdId = memUserInfo.getString("PRODUCT_ID", "");
            			if("7341".equals(memProdId) || "2222".equals(memProdId)){
            				memMap.put(memSn, memProdId);
            			}
            		}
            	}
            }
        }
    	
    	//判断当前提交操作的成员数据中是否有取消的商务宽带、多媒体桌面电话两种用户
    	for (int i = 0, size = memberList.size(); i < size; i++)
        {
            PersonGrpUserUnionPayMemberData member = memberList.get(i);
            String memProductId = member.getUca().getProductId();
            String memSn = member.getUca().getSerialNumber();
            //是否取消商务宽带、多媒体桌面电话两种用户的统付关系
            if("7341".equals(memProductId) || "2222".equals(memProductId)){
            	if("1".equals(member.getModifyTag())){
            		memMap.remove(memSn);
            	}else{
            		memMap.put(memSn, memProductId);
            	}
            }
        }
    	
    	
    	//先捞是否已是“和商务”标识用户
    	IDataset otherDs = UserOtherInfoQry.queryUserOtherInfoForPg(mainUserId, "PG_UNIONPAY");
    	//已存在标识,则判断是否要取消
    	if(IDataUtil.isNotEmpty(otherDs)){
    		//当前提交和已经存在的统付成员是否不包含商务宽带、多媒体桌面电话两种用户
    		if(!memMap.values().contains("7341") || !memMap.values().contains("2222")){
    			for(int i=0;i<otherDs.size();i++){
    				//取消商务标识
    				OtherTradeData otherTD = new OtherTradeData(otherDs.getData(i));
    				otherTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        	        otherTD.setEndDate(SysDateMgr.getSysTime());
        	        otherTD.setRemark("");
        	        btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
    			}
    		}
    	}
    	//未存在标识则判断是否要新增
    	else{
    		//判断当前提交和已经存在统付成员是否含有商务宽带、多媒体桌面电话两种用户
    		if(memMap.values().contains("7341") && memMap.values().contains("2222")){
    			OtherTradeData otherTD = new OtherTradeData();
    	        otherTD.setUserId(mainUserId);
    	        otherTD.setRsrvValueCode("PG_UNIONPAY");
    	        otherTD.setRsrvValue("和商务");
    	        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
    	        otherTD.setStartDate(reqData.getAcceptTime());
    	        otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
    	        otherTD.setInstId(SeqMgr.getInstId());
    	        btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
    		}
    	}
    }
    /**
     * 校验同一个主号的所有统付集团产品用户是否属于同一个集团(cust_id相同)
     * @param btd
     * @param memberList
     * @throws Exception
     * @author chenzg
     * @date 2018-4-12
     */
    private void checkUuMemGroupIdIsOne(BusiTradeData<BaseTradeData> btd, List<PersonGrpUserUnionPayMemberData> memberList) throws Exception{
    	PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();
    	//主号用户
    	String mainUserId = reqData.getUca().getUserId();
    	//取主号目前存在的统付成员
    	IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(mainUserId, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "1");
    	IDataset uuMembers = new DatasetList();
    	IData memMap = new DataMap();	//利用Map的key排重
        if (uuDs.size() > 0)
        {
            String userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            uuMembers = RelaUUInfoQry.queryAllUnionPayMembers(userIdA, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");
            if(IDataUtil.isNotEmpty(uuMembers)){
            	for(int i=0;i<uuMembers.size();i++){
            		IData each = uuMembers.getData(i);
            		String memSn = each.getString("SERIAL_NUMBER_B", "");
            		IData memUserInfo = UcaInfoQry.qryUserMainProdInfoBySn(memSn, RouteInfoQry.getEparchyCodeBySn(memSn));
            		if(IDataUtil.isNotEmpty(memUserInfo)){
            			String memCustId = memUserInfo.getString("CUST_ID", "");
            			memMap.put(memCustId, memCustId);
            		}
            	}
            }
        }
    	
    	//取当前提交的统付成员数据
    	for (int i = 0, size = memberList.size(); i < size; i++)
        {
            PersonGrpUserUnionPayMemberData member = memberList.get(i);
            String memCustId = member.getUca().getCustId();
            if("1".equals(member.getModifyTag())){
        		memMap.remove(memCustId);
        	}else{
        		memMap.put(memCustId, memCustId);
        	}
        }
    	//大于1说明有多个cust_id，即有多个集团
    	if(memMap.size()>1){
    		CSAppException.apperr(FamilyException.CRM_FAMILY_7003);
    	}
    }
    /**
     * 取统付成员的最大数量限制
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private int getCommParaByFamilyUPay() throws Exception
    {
        int mebLim = 99;
        IDataset commSet = CommparaInfoQry.getCommPkInfo("CSM", "1010", "FAMILY_UPAY_LIMIT", "0898");
        if (commSet.size() > 0)
        {
            IData temp = commSet.getData(0);
            mebLim = temp != null ? temp.getInt("PARA_CODE1", 99) : 99;
        }
        return mebLim;
    }
    
    /**
     * 取开始账期
     * @param acctDay
     * @param userId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
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
    /**
     * 取统付成员的付费关系数据
     * @param userId
     * @param acctId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private IDataset getMemberPayRela(String userId, String acctId) throws Exception
    {
        IDataset dataset = PayRelaInfoQry.queryNormalPayre(userId, acctId, "41000", "0");
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_821, userId);
        }

        if (dataset.size() > 1)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_822, userId);
        }
        return dataset;
    }

    /**
     * 
     * @param btd
     * @param memberList
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    private void intfDeal(BusiTradeData btd, List<PersonGrpUserUnionPayMemberData> memberList) throws Exception
    {

        PersonGrpUserUnionPayReqData reqData = (PersonGrpUserUnionPayReqData) btd.getRD();

        String modifyTag = reqData.getModifyTag();
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getUca().getUserId(), PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "1");
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

                    IDataset memberDs = RelaUUInfoQry.getUserRelationAll(userIdA, PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE);

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

                                PersonGrpUserUnionPayMemberData member = new PersonGrpUserUnionPayMemberData();

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

                PersonGrpUserUnionPayMemberData member = memberList.get(i);
                // 查询该成员号码是否是副卡
                IDataset memberRelas = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, member.getUca().getUserId(), PersonGrpUserUnionPayBean.PG_RELA_TYPE_CODE, "2");

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
