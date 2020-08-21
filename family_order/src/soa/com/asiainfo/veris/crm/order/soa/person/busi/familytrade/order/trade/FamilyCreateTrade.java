
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;

public class FamilyCreateTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        FamilyCreateReqData reqData = (FamilyCreateReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String virtualSn = "JT" + uca.getSerialNumber();
        String sysdate = reqData.getAcceptTime();
        String verifyMode = reqData.getVerifyMode();// 副卡校验方式
        MainTradeData mainTD = bd.getMainTradeData();
        String processTagSet = mainTD.getProcessTagSet();
        processTagSet = StringUtils.substring(processTagSet, 2, 3) + verifyMode + StringUtils.substring(processTagSet, 4);

        String userId = uca.getUserId();	//UID
        
        IData user = UcaInfoQry.qryUserInfoBySn(virtualSn);
        if (IDataUtil.isEmpty(user))
        {
            // 虚拟用户不存在，需创建虚拟用户
            createFamily(bd);

            // 新增主卡优惠
            createMainDiscnt(bd);
        }
        
        IDataset userSvcList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("45", userId, "1");
        //IDataset userSvcList = UserSvcInfoQry.getByPMode(userId, "05");
        if (IDataUtil.isEmpty(userSvcList)) {
        	
        	if( IDataUtil.isNotEmpty(user)  ){
        		CSAppException.apperr(FamilyException.CRM_FAMILY_110);
        	}
        	
			List<FamilyMemberData> mebs = reqData.getMemberDataList();
			int Number = mebs.size() + 1;
			if( Number > 10 ){
				 //您的亲亲网成员超出10个，不能升级亲亲网，请删除成员！
	        	CSAppException.apperr(FamilyException.CRM_FAMILY_108);
	        }
			
	        IDataset ids = RelaUUInfoQry.getRelationsByCount("45", userId, "1");
	        if( IDataUtil.isNotEmpty(ids) ){
	        	IData id = ids.getData(0);
	        	String s = id.getString("RECORDCOUNT", "0");
	        	Integer n = Integer.parseInt(s);
	        	if( n >= 2 ){
	        		//尊敬的客户，亲亲网每月最多生效两批亲亲网成员。您组建亲亲网已超出批数限制，本次操作失败。 请您下月再操作！
	        		CSAppException.apperr(FamilyException.CRM_FAMILY_113);
	        	}
	        }
			
		}else{
			//if( virtualUca != null ){
			IData userSvc = userSvcList.getData(0);
			String uIdA = userSvc.getString("USER_ID_A");

			//放开限制
	        /*IDataset userMainDiscnt = UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, uIdA);
			if (IDataUtil.isNotEmpty(userMainDiscnt)) {
				String sysNow = SysDateMgr.getSysTime();
				for (int i = 0, size = userMainDiscnt.size(); i < size; i++) {
					IData data = userMainDiscnt.getData(i);
					String strDC = data.getString("DISCNT_CODE");
					String strSd = data.getString("START_DATE");
					if( "3410".equals(strDC) ){
						int nCount = sysNow.compareTo(strSd);
						if( nCount < 0 ){
							//您的亲亲网升级优惠未生效，还不能增加成员，升级优惠生效时间[%s]！
							CSAppException.apperr(FamilyException.CRM_FAMILY_109, strSd);
						}
					}
				}
			}*/

			List<FamilyMemberData> mebs = reqData.getMemberDataList();
	        IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(uIdA);
	        int Number = mebList.size() + mebs.size() + 1;
	        if( Number > 10 ){
	        	 //您的亲亲网成员超出10个，不能升级亲亲网，请删除成员！
	        	CSAppException.apperr(FamilyException.CRM_FAMILY_108);
	        }
			//}
		}

        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);
        List<SvcTradeData> userFamilyNetSvcs = virtualUca.getUserSvcBySvcId("830");
        if (userFamilyNetSvcs == null || userFamilyNetSvcs.size() == 0)
        {
            // 创建家庭网服务
            SvcTradeData addfamilyNetSvc = new SvcTradeData();
            addfamilyNetSvc.setUserId(virtualUca.getUserId());
            addfamilyNetSvc.setUserIdA(virtualUca.getUserId());
            addfamilyNetSvc.setProductId("99000001");// 先写死
            addfamilyNetSvc.setPackageId("99850001");// 先写死
            addfamilyNetSvc.setElementId("830");
            addfamilyNetSvc.setStartDate(sysdate);
            addfamilyNetSvc.setEndDate(SysDateMgr.getTheLastTime());
            addfamilyNetSvc.setMainTag("0");
            addfamilyNetSvc.setInstId(SeqMgr.getInstId());
            addfamilyNetSvc.setModifyTag(BofConst.MODIFY_TAG_ADD);
            addfamilyNetSvc.setRsrvStr1(virtualSn);

            bd.add(virtualUca.getSerialNumber(), addfamilyNetSvc);

            if (StringUtils.isNotBlank(reqData.getShortCode()))
            {
                List<SvcTradeData> userShortCodeSvcs = uca.getUserSvcBySvcId("831");
                if (userShortCodeSvcs != null && userShortCodeSvcs.size() > 0)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_54);
                }

                // 创建主号短号
                SvcTradeData addShortCodeSvc = new SvcTradeData();
                addShortCodeSvc.setUserId(uca.getUserId());
                addShortCodeSvc.setUserIdA(virtualUca.getUserId());
                addShortCodeSvc.setProductId("99000001");// 先写死
                addShortCodeSvc.setPackageId("99850001");// 先写死
                addShortCodeSvc.setElementId("831");
                addShortCodeSvc.setStartDate(sysdate);
                addShortCodeSvc.setEndDate(SysDateMgr.getTheLastTime());
                addShortCodeSvc.setMainTag("0");
                addShortCodeSvc.setInstId(SeqMgr.getInstId());
                addShortCodeSvc.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addShortCodeSvc.setRsrvStr1(uca.getSerialNumber());
                addShortCodeSvc.setRsrvStr2(reqData.getShortCode());
                addShortCodeSvc.setRsrvStr3(virtualSn);
                addShortCodeSvc.setRsrvStr5(uca.getUserId());

                bd.add(uca.getSerialNumber(), addShortCodeSvc);
            }

            // 第二位表示是否建家1是0否
            processTagSet = StringUtils.substring(processTagSet, 1, 2) + "1" + StringUtils.substring(processTagSet, 3);
        }

        if (("0").equals(reqData.getInTagNew())) { // 0-界面互联网前台受理
            mainTD.setRsrvStr2(reqData.getInTagNew()); // 0-界面互联网前台受理
            dealSmsMebList(bd); // 处理短信校验方式的副卡
            dealMebListNew(bd); // 处理密码校验和免密码校验方式的副卡
        } else { // 原逻辑
            if (StringUtils.equals(verifyMode, "1")) { // 短信
                StringBuilder serialNumberBSb = new StringBuilder();
                StringBuilder discntCodeSb = new StringBuilder();
                StringBuilder appDiscntCodeSb = new StringBuilder();
                StringBuilder shortCodeBSb = new StringBuilder();

                List<FamilyMemberData> mebDataList = reqData.getMemberDataList();
                for (int i = 0, size = mebDataList.size(); i < size; i++) {
                    FamilyMemberData memberData = mebDataList.get(i);
                    UcaData ucaData = memberData.getUca();
                    String serialNumberB = ucaData.getSerialNumber();// 成员号码

                    DiscntData memberDiscnt = memberData.getDiscntData();
                    String discntCode = memberDiscnt.getElementId();// 成员优惠编码

                    DiscntData appMemberDiscnt = memberData.getAppDiscntData();
                    String appDiscntCode = " ";// 空串，以免在短信处理的时候报错
                    if (null != appMemberDiscnt) {
                        appDiscntCode = appMemberDiscnt.getElementId();// 成员叠加优惠编码
                    }

                    String shortCodeB = memberData.getShortCode();// 成员短号
                    if (StringUtils.isBlank(shortCodeB)) {
                        shortCodeB = " ";
                    }

                    serialNumberBSb.append(serialNumberB);
                    discntCodeSb.append(discntCode);
                    appDiscntCodeSb.append(appDiscntCode);
                    shortCodeBSb.append(shortCodeB);

                    if (i + 1 < size) {
                        serialNumberBSb.append(",");
                        discntCodeSb.append(",");
                        appDiscntCodeSb.append(",");
                        shortCodeBSb.append(",");
                    }
                }

                processTagSet = "1" + StringUtils.substring(processTagSet, 1);

                mainTD.setRsrvStr4(discntCodeSb.toString());
                mainTD.setRsrvStr6(shortCodeBSb.toString());
                mainTD.setRsrvStr7(serialNumberBSb.toString());
                mainTD.setRsrvStr8(appDiscntCodeSb.toString());
                mainTD.setProcessTagSet(processTagSet);
            } else {
                dealMebList(bd);
            }
        }

        mainTD.setRsrvStr1(virtualUca.getUserId());// 虚拟用户ID
        mainTD.setRsrvStr3(reqData.getShortCode());// 主号短号
        mainTD.setRsrvStr5("45");// 关系类型
        
        String remark="";
        if (("0").equals(reqData.getInTagNew())) {
            remark = "界面互联网受理";
        } else { // 原逻辑
            if (StringUtils.equals(verifyMode, "0")) {
                remark = "密码";
            } else if (StringUtils.equals(verifyMode, "1")) {
                remark = "短信";
            } else if (StringUtils.equals(verifyMode, "2")) {
                remark = "免密码";
            }
            mainTD.setRsrvStr10(verifyMode);
        }
        mainTD.setRemark(remark);
    }

    /**
     * 新增家庭套餐
     * 
     * @param bd
     * @throws Exception
     */
    private String createFamily(BusiTradeData bd) throws Exception
    {
        FamilyCreateReqData reqData = (FamilyCreateReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();

        String homeAcctId = SeqMgr.getAcctId();
        String packageId = null;
        String userIdA = SeqMgr.getUserId();
        String homeCustId = SeqMgr.getCustId();
        // String homeId = SeqMgr.getHomeId();
        String virtualSn = "JT" + uca.getSerialNumber();
        String acctDay = uca.getAcctDay();

        // 新增用户
        UserTradeData user = new UserTradeData();
        UserTradeData tempUser = uca.getUser();
        user.setUserId(userIdA);
        user.setCustId(homeCustId);
        user.setUsecustId(homeCustId);
        user.setEparchyCode(tempUser.getEparchyCode());
        user.setCityCode(tempUser.getCityCode());
        user.setUserPasswd(tempUser.getUserPasswd());
        user.setAcctTag("0");
        user.setUserTypeCode("0");
        user.setContractId(tempUser.getContractId());
        user.setSerialNumber(virtualSn);
        user.setPrepayTag("0");
        user.setOpenDate(sysdate);
        user.setOpenMode("0");
        user.setUserStateCodeset("0");
        user.setNetTypeCode("00");
        user.setMputeMonthFee("0");
        user.setInDate(sysdate);
        user.setRemoveTag("0");
        user.setInStaffId(CSBizBean.getVisit().getStaffId());
        user.setInDepartId(CSBizBean.getVisit().getDepartId());
        user.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        user.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        user.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        user.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        user.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(virtualSn, user);

        // 新增客户
        CustomerTradeData customer = new CustomerTradeData();
        CustomerTradeData tempCustomer = uca.getCustomer().clone();
        customer.setCustId(homeCustId);
        customer.setCustName(tempCustomer.getCustName());
        if (StringUtils.isNotBlank(reqData.getHomeName()))
        {
            customer.setCustName(reqData.getHomeName());
        }
        customer.setCustType("2");
        customer.setCustState("0");
        customer.setPsptTypeCode(tempCustomer.getPsptTypeCode());
        customer.setPsptId(tempCustomer.getPsptId());
        customer.setOpenLimit(tempCustomer.getOpenLimit());
        customer.setEparchyCode(tempCustomer.getEparchyCode());
        customer.setCityCode(tempCustomer.getCityCode());
        customer.setCustPasswd(tempCustomer.getCustPasswd());
        customer.setDevelopDepartId(tempCustomer.getDevelopDepartId());
        customer.setDevelopStaffId(tempCustomer.getDevelopStaffId());
        customer.setInDepartId(tempCustomer.getInDepartId());
        customer.setInStaffId(tempCustomer.getInStaffId());
        customer.setInDate(tempCustomer.getInDate());
        customer.setRemark("办理亲亲网业务添加虚拟客户");
        customer.setModifyTag(BofConst.MODIFY_TAG_ADD);
        customer.setRemoveTag("0");
        bd.add(virtualSn, customer);

        // 新增家庭客户信息
        CustFamilyTradeData addCustFamilyTD = new CustFamilyTradeData();
        addCustFamilyTD.setHomeCustId(homeCustId);
        addCustFamilyTD.setHomeId(virtualSn); // 将取序列改为virtualSn
        if (StringUtils.isBlank(reqData.getHomeName()))
        {
            addCustFamilyTD.setHomeName(uca.getCustomer().getCustName());
        }
        else
        {
            addCustFamilyTD.setHomeName(reqData.getHomeName());
        }
        addCustFamilyTD.setHomeAddress(reqData.getHomeAddress());
        addCustFamilyTD.setHomePhone(reqData.getHomePhone());
        addCustFamilyTD.setSerialNumber(uca.getSerialNumber());
        addCustFamilyTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addCustFamilyTD.setMemberNum("0");
        addCustFamilyTD.setRemoveTag("0");
        addCustFamilyTD.setInDate(reqData.getAcceptTime());
        addCustFamilyTD.setInStaffId(CSBizBean.getVisit().getStaffId());
        addCustFamilyTD.setInDepartId(CSBizBean.getVisit().getDepartId());
        bd.add(virtualSn, addCustFamilyTD);

        // 新增帐户
        AccountTradeData acct = new AccountTradeData();
        acct.setEparchyCode(uca.getUserEparchyCode());
        acct.setCityCode(uca.getUser().getCityCode());
        acct.setAcctId(homeAcctId);
        acct.setCustId(homeCustId);
        if (StringUtils.isBlank(reqData.getHomeName()))
        {
            acct.setPayName(uca.getCustomer().getCustName());
        }
        else
        {
            acct.setPayName(reqData.getHomeName());
        }
        acct.setPayModeCode("0");
        acct.setScoreValue("0");
        acct.setOpenDate(sysdate);
        acct.setRemoveTag("0");
        acct.setBasicCreditValue("0");
        acct.setCreditValue("0");
        acct.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(virtualSn, acct);

        // 新增付费关系
        PayRelationTradeData payRela = new PayRelationTradeData();
        payRela.setModifyTag(BofConst.MODIFY_TAG_ADD);
        payRela.setUserId(userIdA);
        payRela.setAcctId(homeAcctId);
        payRela.setPayitemCode("0");
        payRela.setAcctPriority("0");
        payRela.setUserPriority("0");
        payRela.setBindType("0");
        payRela.setStartCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
        payRela.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payRela.setDefaultTag("1");
        payRela.setActTag("1");
        payRela.setLimitType("0");
        payRela.setLimit("0");
        payRela.setComplementTag("0");
        payRela.setInstId(SeqMgr.getInstId());
        bd.add(virtualSn, payRela);

        // 建立虚拟用户与主卡的UU关系
        RelationTradeData addRelaUUTD = new RelationTradeData();
        addRelaUUTD.setUserIdA(userIdA);
        addRelaUUTD.setSerialNumberA(virtualSn);
        addRelaUUTD.setUserIdB(uca.getUserId());
        addRelaUUTD.setSerialNumberB(uca.getSerialNumber());
        addRelaUUTD.setRelationTypeCode("45");
        addRelaUUTD.setRoleTypeCode("0");
        addRelaUUTD.setRoleCodeA("0");
        addRelaUUTD.setRoleCodeB("1");
        addRelaUUTD.setOrderno("0");
        addRelaUUTD.setShortCode(reqData.getShortCode());
        addRelaUUTD.setStartDate(reqData.getAcceptTime());
        addRelaUUTD.setEndDate(SysDateMgr.getTheLastTime());
        addRelaUUTD.setInstId(SeqMgr.getInstId());
        addRelaUUTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(uca.getSerialNumber(), addRelaUUTD);

        // 建立用户产品信息
        ProductTradeData addProductTD = new ProductTradeData();
        addProductTD.setUserId(userIdA);
        addProductTD.setProductId(reqData.getDiscntData().getProductId());
        addProductTD.setBrandCode("VPCN");
        addProductTD.setStartDate(sysdate);
        addProductTD.setEndDate(SysDateMgr.getTheLastTime());
        addProductTD.setMainTag("1");
        addProductTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addProductTD.setProductMode("05");
        addProductTD.setUserIdA("-1");
        addProductTD.setCampnId("0");
        addProductTD.setInstId(SeqMgr.getInstId());
        bd.add(virtualSn, addProductTD);

        // 用户账期
        bd.addOpenUserAcctDayData(userIdA, acctDay);
        // 帐户账期
        bd.addOpenAccountAcctDayData(homeAcctId, acctDay);

        // 临时办法
        UcaData virtualUca = DataBusManager.getDataBus().getUca(virtualSn);
        virtualUca.setAcctDay(acctDay);
        virtualUca.setFirstDate(reqData.getAcceptTime());

        return userIdA;
    }

    private void createMainDiscnt(BusiTradeData bd) throws Exception
    {
        FamilyCreateReqData reqData = (FamilyCreateReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();
        DiscntData discntData = reqData.getDiscntData();
        DiscntData appDiscntData = reqData.getAppDiscntData();
        String virtualSn = "JT" + uca.getSerialNumber();
        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);

        
        DiscntTradeData addDiscntTD = new DiscntTradeData();
        addDiscntTD.setUserId(uca.getUserId());
        addDiscntTD.setUserIdA(virtualUca.getUserId());
        addDiscntTD.setProductId(discntData.getProductId());
        addDiscntTD.setPackageId(discntData.getPackageId());
        addDiscntTD.setElementId(discntData.getElementId());
        addDiscntTD.setSpecTag("2");
        addDiscntTD.setRelationTypeCode("45");
        addDiscntTD.setCampnId("0");
        addDiscntTD.setStartDate(reqData.getAcceptTime());
        addDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
        addDiscntTD.setInstId(SeqMgr.getInstId());
        addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(uca.getSerialNumber(), addDiscntTD);

        if (appDiscntData != null)
        {
            DiscntTradeData addAppDiscntTD = new DiscntTradeData();
            addAppDiscntTD.setUserId(uca.getUserId());
            addAppDiscntTD.setUserIdA("-1");
            addAppDiscntTD.setProductId(appDiscntData.getProductId());
            addAppDiscntTD.setPackageId(appDiscntData.getPackageId());
            addAppDiscntTD.setElementId(appDiscntData.getElementId());
            addAppDiscntTD.setSpecTag("0");
            addAppDiscntTD.setCampnId("0");
            addAppDiscntTD.setStartDate(reqData.getAcceptTime());
            addAppDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
            addAppDiscntTD.setInstId(SeqMgr.getInstId());
            addAppDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            bd.add(uca.getSerialNumber(), addAppDiscntTD);
        }
        //		
        // //得到packageId
        // IDataset discntList = DiscntInfoQry.qryByPidPkgTypeCode(discntData.getProductId(), "5",
        // uca.getUserEparchyCode());
        // for(int i = 0, size = discntList.size(); i < size; i++)
        // {
        // IData tmp = discntList.getData(i);
        // if(discntData.getElementId().equals(tmp.getString("DISCNT_CODE")))
        // {
        // packageId = tmp.getString("PACKAGE_ID");
        // break;
        // }
        // }
        //		
        // if(StringUtils.isBlank(packageId))
        // {
        // CSAppException.apperr(ElementException.CRM_ELEMENT_144,discntCode);
        // }
    }

    /**
     * 海南目前的这个业务只支持建家和新增成员
     * 
     * @param bd
     * @param bd
     * @throws Exception
     */
    private void dealMebList(BusiTradeData bd) throws Exception
    {
        FamilyCreateReqData reqData = (FamilyCreateReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String virtualSn = "JT" + uca.getSerialNumber();
        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);
        String sysdate = reqData.getAcceptTime();
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        String userId = uca.getUserId();	//UID

        List<FamilyMemberData> mebDataList = reqData.getMemberDataList();
        /*IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(virtualUca.getUserId());
        int Number = mebList.size() + mebDataList.size() + 1;
        if( Number > 10 ){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_108);
        }*/
        for (int i = 0, size = mebDataList.size(); i < size; i++)
        {
            FamilyMemberData mebData = mebDataList.get(i);
            UcaData mebUca = mebData.getUca();
            DiscntData discntData = mebData.getDiscntData();

            // 校验成员未完工工单限制 ----start----
            IData data = new DataMap();
            data.put("TRADE_TYPE_CODE", tradeTypeCode);
            data.put("USER_ID", mebUca.getUserId());
            data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
            data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
            data.put("BRAND_CODE", "");
            FamilyTradeHelper.checkMemberUnfinishTrade(data);
            // 校验成员未完工工单限制 ----end----

            String uIdA = virtualUca.getUserId();
            boolean strSj = false; 
            IDataset userMainDiscnt = UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, uIdA);
			if (IDataUtil.isNotEmpty(userMainDiscnt)) {
				String sysNow = SysDateMgr.getSysTime();
				for (int ni = 0, nsize = userMainDiscnt.size(); ni < nsize; ni++) {
					IData data1 = userMainDiscnt.getData(ni);
					String strDC = data1.getString("DISCNT_CODE");
					String strSd = data1.getString("START_DATE");
					if( "3410".equals(strDC) ){
						int nCount = sysNow.compareTo(strSd);
						if( nCount < 0 ){
							strSj = true;
						}
					}
				}
			}
			if( strSj ){
				String sn = mebUca.getSerialNumber();
				UcaData mainUca = UcaDataFactory.getNormalUca(sn);
				mainUca.setAcctTimeEnv();// 分散账期
	            
				DiscntTradeData addDiscntTD3404 = new DiscntTradeData();
				addDiscntTD3404.setUserId(mebUca.getUserId());
				addDiscntTD3404.setUserIdA(virtualUca.getUserId());
				addDiscntTD3404.setProductId(discntData.getProductId());
				addDiscntTD3404.setPackageId(discntData.getPackageId());
				addDiscntTD3404.setElementId("3404");
				addDiscntTD3404.setSpecTag("2");
				addDiscntTD3404.setRelationTypeCode("45");
				addDiscntTD3404.setStartDate(sysdate);
				addDiscntTD3404.setEndDate(SysDateMgr.getLastDateThisMonth());
				addDiscntTD3404.setInstId(SeqMgr.getInstId());
				addDiscntTD3404.setModifyTag(BofConst.MODIFY_TAG_ADD);

	            bd.add(mebUca.getSerialNumber(), addDiscntTD3404);
	            
	            DiscntTradeData addDiscntTD3411 = new DiscntTradeData();
	            addDiscntTD3411.setUserId(mebUca.getUserId());
	            addDiscntTD3411.setUserIdA(virtualUca.getUserId());
	            addDiscntTD3411.setProductId(discntData.getProductId());
	            addDiscntTD3411.setPackageId(discntData.getPackageId());
	            addDiscntTD3411.setElementId("3411");
	            addDiscntTD3411.setSpecTag("2");
	            addDiscntTD3411.setRelationTypeCode("45");
	            addDiscntTD3411.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
	            addDiscntTD3411.setEndDate(SysDateMgr.getTheLastTime());
	            addDiscntTD3411.setInstId(SeqMgr.getInstId());
	            addDiscntTD3411.setModifyTag(BofConst.MODIFY_TAG_ADD);
	            
	            bd.add(mebUca.getSerialNumber(), addDiscntTD3411);
			}else{
				DiscntTradeData addDiscntTD = new DiscntTradeData();
	            addDiscntTD.setUserId(mebUca.getUserId());
	            addDiscntTD.setUserIdA(virtualUca.getUserId());
	            addDiscntTD.setProductId(discntData.getProductId());
	            addDiscntTD.setPackageId(discntData.getPackageId());
	            addDiscntTD.setElementId(discntData.getElementId());
	            addDiscntTD.setSpecTag("2");
	            addDiscntTD.setRelationTypeCode("45");
	            addDiscntTD.setStartDate(sysdate);
	            addDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
	            addDiscntTD.setInstId(SeqMgr.getInstId());
	            addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

	            bd.add(mebUca.getSerialNumber(), addDiscntTD);
			}
            

            DiscntData appDiscntData = mebData.getAppDiscntData();

            if (appDiscntData != null)
            {
                DiscntTradeData addAppDiscntTD = new DiscntTradeData();
                addAppDiscntTD.setUserId(mebUca.getUserId());
                addAppDiscntTD.setUserIdA("-1");
                addAppDiscntTD.setProductId(appDiscntData.getProductId());
                addAppDiscntTD.setPackageId(appDiscntData.getPackageId());
                addAppDiscntTD.setElementId(appDiscntData.getElementId());
                addAppDiscntTD.setSpecTag("0");
                addAppDiscntTD.setStartDate(sysdate);
                addAppDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
                addAppDiscntTD.setInstId(SeqMgr.getInstId());
                addAppDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

                bd.add(mebUca.getSerialNumber(), addAppDiscntTD);
            }

            // 建立UU关系
            RelationTradeData addRelaUUTD = new RelationTradeData();
            addRelaUUTD.setUserIdA(virtualUca.getUserId());
            addRelaUUTD.setSerialNumberA(virtualSn);
            addRelaUUTD.setUserIdB(mebUca.getUserId());
            addRelaUUTD.setSerialNumberB(mebUca.getSerialNumber());
            addRelaUUTD.setRelationTypeCode("45");
            addRelaUUTD.setRoleTypeCode("0");
            addRelaUUTD.setRoleCodeA("0");
            addRelaUUTD.setRoleCodeB("2");
            addRelaUUTD.setShortCode(mebData.getShortCode());
            addRelaUUTD.setOrderno("0");
            addRelaUUTD.setStartDate(sysdate);
            addRelaUUTD.setEndDate(SysDateMgr.getTheLastTime());
            addRelaUUTD.setInstId(SeqMgr.getInstId());
            addRelaUUTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            bd.add(uca.getSerialNumber(), addRelaUUTD);

            if (null != virtualUca.getCustFamily())
            {
                // 建立亲亲网客户关系
                CustFamilyMebTradeData addCustFamilyMebTD = new CustFamilyMebTradeData();
                addCustFamilyMebTD.setHomeCustId(virtualUca.getCustFamily().getHomeCustId());
                addCustFamilyMebTD.setHomeId(virtualUca.getCustFamily().getHomeId());
                addCustFamilyMebTD.setNickName(mebData.getNickName());
                addCustFamilyMebTD.setMemberCustId(mebUca.getCustId());
                addCustFamilyMebTD.setMemberRole(StringUtils.isBlank(mebData.getMemberRole()) == true ? "0" : mebData.getMemberRole());
                addCustFamilyMebTD.setMemberKind(mebData.getMemberKind());
                addCustFamilyMebTD.setSerialNumber(mebUca.getSerialNumber());
                addCustFamilyMebTD.setShortCode(mebData.getShortCode());
                addCustFamilyMebTD.setNetTypeCode("00");
                addCustFamilyMebTD.setMemberBelong("1");
                addCustFamilyMebTD.setRemoveTag("0");
                addCustFamilyMebTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addCustFamilyMebTD.setJoinDate(sysdate);
                addCustFamilyMebTD.setJoinStaffId(CSBizBean.getVisit().getStaffId());
                addCustFamilyMebTD.setJoinDepartId(CSBizBean.getVisit().getDepartId());
                addCustFamilyMebTD.setInstId(SeqMgr.getInstId()); // 新增inst_id字段 by zhouwu
                bd.add(uca.getSerialNumber(), addCustFamilyMebTD);
            }

            if (StringUtils.isNotBlank(mebData.getShortCode()))
            {
                SvcTradeData addShortCodeSvc = new SvcTradeData();
                addShortCodeSvc.setUserId(mebUca.getUserId());
                addShortCodeSvc.setUserIdA(virtualUca.getUserId());
                addShortCodeSvc.setProductId("99000001");// 先写死
                addShortCodeSvc.setPackageId("99850001");// 先写死
                addShortCodeSvc.setElementId("831");
                addShortCodeSvc.setStartDate(sysdate);
                addShortCodeSvc.setEndDate(SysDateMgr.getTheLastTime());
                addShortCodeSvc.setMainTag("0");
                addShortCodeSvc.setInstId(SeqMgr.getInstId());
                addShortCodeSvc.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addShortCodeSvc.setRsrvStr1(mebUca.getSerialNumber());
                addShortCodeSvc.setRsrvStr2(mebData.getShortCode());
                addShortCodeSvc.setRsrvStr3(virtualSn);
                addShortCodeSvc.setRsrvStr5(mebUca.getUserId());

                bd.add(mebUca.getSerialNumber(), addShortCodeSvc);
            }
        }
    }

    /**
     * @Description 副号台账登记处理（短信校验方式），界面互联网化-亲亲网组建/增加成员，副卡可以分别使用不同的校验方式改造。
     * @Author zhaohj3
     * @Date 2018/8/17 下午 2:55
     * @Param 
     * @return 
     */
    private void dealSmsMebList(BusiTradeData bd) throws Exception {
        MainTradeData mainTD = bd.getMainTradeData();
        FamilyCreateReqData reqData = (FamilyCreateReqData) bd.getRD();
        StringBuilder serialNumberBSb = new StringBuilder();
        StringBuilder discntCodeSb = new StringBuilder();
        StringBuilder appDiscntCodeSb = new StringBuilder();
        StringBuilder shortCodeBSb = new StringBuilder();

        List<FamilyMemberData> mebDataList = reqData.getMemberDataList();
        for (int i = 0, size = mebDataList.size(); i < size; i++) {
            FamilyMemberData memberData = mebDataList.get(i);

            if (StringUtils.equals(memberData.getMebVerifyMode(), "1")) { // 短信方式校验的副卡
                UcaData ucaData = memberData.getUca();
                String serialNumberB = ucaData.getSerialNumber();// 成员号码

                DiscntData memberDiscnt = memberData.getDiscntData();
                String discntCode = memberDiscnt.getElementId();// 成员优惠编码

                DiscntData appMemberDiscnt = memberData.getAppDiscntData();
                String appDiscntCode = " ";// 空串，以免在短信处理的时候报错
                if (null != appMemberDiscnt) {
                    appDiscntCode = appMemberDiscnt.getElementId();// 成员叠加优惠编码
                }

                String shortCodeB = memberData.getShortCode();// 成员短号
                if (StringUtils.isBlank(shortCodeB)) {
                    shortCodeB = " ";
                }

                serialNumberBSb.append(serialNumberB);
                discntCodeSb.append(discntCode);
                appDiscntCodeSb.append(appDiscntCode);
                shortCodeBSb.append(shortCodeB);

                serialNumberBSb.append(",");
                discntCodeSb.append(",");
                appDiscntCodeSb.append(",");
                shortCodeBSb.append(",");
            }
        }
        // 去掉最后的逗号
        if (StringUtils.isNotBlank(discntCodeSb)) {
            if (StringUtils.equals(",", discntCodeSb.substring(discntCodeSb.length() - 1))) {
                mainTD.setRsrvStr4(discntCodeSb.substring(0, discntCodeSb.length() - 1));
            } else {
                mainTD.setRsrvStr4(discntCodeSb.toString());
            }
        }
        if (StringUtils.isNotBlank(shortCodeBSb)) {
            if (StringUtils.equals(",", shortCodeBSb.substring(shortCodeBSb.length() - 1))) {
                mainTD.setRsrvStr6(shortCodeBSb.substring(0, shortCodeBSb.length() - 1));
            } else {
                mainTD.setRsrvStr6(shortCodeBSb.toString());
            }
        }
        if (StringUtils.isNotBlank(serialNumberBSb)) {
            if (StringUtils.equals(",", serialNumberBSb.substring(serialNumberBSb.length() - 1))) {
                mainTD.setRsrvStr7(serialNumberBSb.substring(0, serialNumberBSb.length() - 1));
            } else {
                mainTD.setRsrvStr7(serialNumberBSb.toString());
            }
        }
        if (StringUtils.isNotBlank(appDiscntCodeSb)) {
            if (StringUtils.equals(",", appDiscntCodeSb.substring(appDiscntCodeSb.length() - 1))) {
                mainTD.setRsrvStr8(appDiscntCodeSb.substring(0, appDiscntCodeSb.length() - 1));
            } else {
                mainTD.setRsrvStr8(appDiscntCodeSb.toString());
            }
        }
    }

    /**
     * @Description 副号台账登记处理（密码/免密码校验方式），界面互联网化-亲亲网组建/增加成员，副卡可以分别使用不同的校验方式改造。
     * @Author zhaohj3
     * @Date 2018/8/17 下午 2:51
     * @Param 
     * @return 
     */
    private void dealMebListNew(BusiTradeData bd) throws Exception {
        MainTradeData mainTD = bd.getMainTradeData();
        FamilyCreateReqData reqData = (FamilyCreateReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String virtualSn = "JT" + uca.getSerialNumber();
        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);
        String sysdate = reqData.getAcceptTime();
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        String userId = uca.getUserId();    //UID

        List<FamilyMemberData> mebDataList = reqData.getMemberDataList();
        /*IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(virtualUca.getUserId());
        int Number = mebList.size() + mebDataList.size() + 1;
        if( Number > 10 ){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_108);
        }*/
        String remarkTag  = "0";
        for (int i = 0, size = mebDataList.size(); i < size; i++) {
            FamilyMemberData mebData = mebDataList.get(i);
            if (!StringUtils.equals(mebData.getMebVerifyMode(), "1")) { // 密码或者免密码方式校验的副卡
                String remark = mainTD.getRemark();
                if (StringUtils.isBlank(remark)) {
                    mainTD.setRemark(remark);
                }
                UcaData mebUca = mebData.getUca();
                DiscntData discntData = mebData.getDiscntData();

                // 校验成员未完工工单限制 ----start----
                IData data = new DataMap();
                data.put("TRADE_TYPE_CODE", tradeTypeCode);
                data.put("USER_ID", mebUca.getUserId());
                data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
                data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
                data.put("BRAND_CODE", "");
                FamilyTradeHelper.checkMemberUnfinishTrade(data);
                // 校验成员未完工工单限制 ----end----

                String uIdA = virtualUca.getUserId();
                boolean strSj = false;
                IDataset userMainDiscnt = UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, uIdA);
                if (IDataUtil.isNotEmpty(userMainDiscnt)) {
                    String sysNow = SysDateMgr.getSysTime();
                    for (int ni = 0, nsize = userMainDiscnt.size(); ni < nsize; ni++) {
                        IData data1 = userMainDiscnt.getData(ni);
                        String strDC = data1.getString("DISCNT_CODE");
                        String strSd = data1.getString("START_DATE");
                        if ("3410".equals(strDC)) {
                            int nCount = sysNow.compareTo(strSd);
                            if (nCount < 0) {
                                strSj = true;
                            }
                        }
                    }
                }
                if (strSj) {
                    String sn = mebUca.getSerialNumber();
                    UcaData mainUca = UcaDataFactory.getNormalUca(sn);
                    mainUca.setAcctTimeEnv();// 分散账期

                    DiscntTradeData addDiscntTD3404 = new DiscntTradeData();
                    addDiscntTD3404.setUserId(mebUca.getUserId());
                    addDiscntTD3404.setUserIdA(virtualUca.getUserId());
                    addDiscntTD3404.setProductId(discntData.getProductId());
                    addDiscntTD3404.setPackageId(discntData.getPackageId());
                    addDiscntTD3404.setElementId("3404");
                    addDiscntTD3404.setSpecTag("2");
                    addDiscntTD3404.setRelationTypeCode("45");
                    addDiscntTD3404.setStartDate(sysdate);
                    addDiscntTD3404.setEndDate(SysDateMgr.getLastDateThisMonth());
                    addDiscntTD3404.setInstId(SeqMgr.getInstId());
                    addDiscntTD3404.setModifyTag(BofConst.MODIFY_TAG_ADD);

                    bd.add(mebUca.getSerialNumber(), addDiscntTD3404);

                    DiscntTradeData addDiscntTD3411 = new DiscntTradeData();
                    addDiscntTD3411.setUserId(mebUca.getUserId());
                    addDiscntTD3411.setUserIdA(virtualUca.getUserId());
                    addDiscntTD3411.setProductId(discntData.getProductId());
                    addDiscntTD3411.setPackageId(discntData.getPackageId());
                    addDiscntTD3411.setElementId("3411");
                    addDiscntTD3411.setSpecTag("2");
                    addDiscntTD3411.setRelationTypeCode("45");
                    addDiscntTD3411.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
                    addDiscntTD3411.setEndDate(SysDateMgr.getTheLastTime());
                    addDiscntTD3411.setInstId(SeqMgr.getInstId());
                    addDiscntTD3411.setModifyTag(BofConst.MODIFY_TAG_ADD);

                    bd.add(mebUca.getSerialNumber(), addDiscntTD3411);
                } else {
                    DiscntTradeData addDiscntTD = new DiscntTradeData();
                    addDiscntTD.setUserId(mebUca.getUserId());
                    addDiscntTD.setUserIdA(virtualUca.getUserId());
                    addDiscntTD.setProductId(discntData.getProductId());
                    addDiscntTD.setPackageId(discntData.getPackageId());
                    addDiscntTD.setElementId(discntData.getElementId());
                    addDiscntTD.setSpecTag("2");
                    addDiscntTD.setRelationTypeCode("45");
                    addDiscntTD.setStartDate(sysdate);
                    addDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
                    addDiscntTD.setInstId(SeqMgr.getInstId());
                    addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

                    bd.add(mebUca.getSerialNumber(), addDiscntTD);
                }

                DiscntData appDiscntData = mebData.getAppDiscntData();

                if (appDiscntData != null) {
                    DiscntTradeData addAppDiscntTD = new DiscntTradeData();
                    addAppDiscntTD.setUserId(mebUca.getUserId());
                    addAppDiscntTD.setUserIdA("-1");
                    addAppDiscntTD.setProductId(appDiscntData.getProductId());
                    addAppDiscntTD.setPackageId(appDiscntData.getPackageId());
                    addAppDiscntTD.setElementId(appDiscntData.getElementId());
                    addAppDiscntTD.setSpecTag("0");
                    addAppDiscntTD.setStartDate(sysdate);
                    addAppDiscntTD.setEndDate(SysDateMgr.getTheLastTime());
                    addAppDiscntTD.setInstId(SeqMgr.getInstId());
                    addAppDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

                    bd.add(mebUca.getSerialNumber(), addAppDiscntTD);
                }

                // 建立UU关系
                RelationTradeData addRelaUUTD = new RelationTradeData();
                addRelaUUTD.setUserIdA(virtualUca.getUserId());
                addRelaUUTD.setSerialNumberA(virtualSn);
                addRelaUUTD.setUserIdB(mebUca.getUserId());
                addRelaUUTD.setSerialNumberB(mebUca.getSerialNumber());
                addRelaUUTD.setRelationTypeCode("45");
                addRelaUUTD.setRoleTypeCode("0");
                addRelaUUTD.setRoleCodeA("0");
                addRelaUUTD.setRoleCodeB("2");
                addRelaUUTD.setShortCode(mebData.getShortCode());
                addRelaUUTD.setOrderno("0");
                addRelaUUTD.setStartDate(sysdate);
                addRelaUUTD.setEndDate(SysDateMgr.getTheLastTime());
                addRelaUUTD.setInstId(SeqMgr.getInstId());
                addRelaUUTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                bd.add(uca.getSerialNumber(), addRelaUUTD);

                if (null != virtualUca.getCustFamily()) {
                    // 建立亲亲网客户关系
                    CustFamilyMebTradeData addCustFamilyMebTD = new CustFamilyMebTradeData();
                    addCustFamilyMebTD.setHomeCustId(virtualUca.getCustFamily().getHomeCustId());
                    addCustFamilyMebTD.setHomeId(virtualUca.getCustFamily().getHomeId());
                    addCustFamilyMebTD.setNickName(mebData.getNickName());
                    addCustFamilyMebTD.setMemberCustId(mebUca.getCustId());
                    addCustFamilyMebTD.setMemberRole(StringUtils.isBlank(mebData.getMemberRole()) == true ? "0" : mebData.getMemberRole());
                    addCustFamilyMebTD.setMemberKind(mebData.getMemberKind());
                    addCustFamilyMebTD.setSerialNumber(mebUca.getSerialNumber());
                    addCustFamilyMebTD.setShortCode(mebData.getShortCode());
                    addCustFamilyMebTD.setNetTypeCode("00");
                    addCustFamilyMebTD.setMemberBelong("1");
                    addCustFamilyMebTD.setRemoveTag("0");
                    addCustFamilyMebTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    addCustFamilyMebTD.setJoinDate(sysdate);
                    addCustFamilyMebTD.setJoinStaffId(CSBizBean.getVisit().getStaffId());
                    addCustFamilyMebTD.setJoinDepartId(CSBizBean.getVisit().getDepartId());
                    addCustFamilyMebTD.setInstId(SeqMgr.getInstId()); // 新增inst_id字段 by zhouwu
                    bd.add(uca.getSerialNumber(), addCustFamilyMebTD);
                }

                if (StringUtils.isNotBlank(mebData.getShortCode())) {
                    SvcTradeData addShortCodeSvc = new SvcTradeData();
                    addShortCodeSvc.setUserId(mebUca.getUserId());
                    addShortCodeSvc.setUserIdA(virtualUca.getUserId());
                    addShortCodeSvc.setProductId("99000001");// 先写死
                    addShortCodeSvc.setPackageId("99850001");// 先写死
                    addShortCodeSvc.setElementId("831");
                    addShortCodeSvc.setStartDate(sysdate);
                    addShortCodeSvc.setEndDate(SysDateMgr.getTheLastTime());
                    addShortCodeSvc.setMainTag("0");
                    addShortCodeSvc.setInstId(SeqMgr.getInstId());
                    addShortCodeSvc.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    addShortCodeSvc.setRsrvStr1(mebUca.getSerialNumber());
                    addShortCodeSvc.setRsrvStr2(mebData.getShortCode());
                    addShortCodeSvc.setRsrvStr3(virtualSn);
                    addShortCodeSvc.setRsrvStr5(mebUca.getUserId());

                    bd.add(mebUca.getSerialNumber(), addShortCodeSvc);
                }
            }
        }
    }
}
