
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.trade.WidenetMoveTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class WidenetMoveBean extends CSBizBean
{
    public IData checkSerialNumber(IData input) throws Exception
    {
        IData data = new DataMap();
        String mainSerialNumber = input.getString("SERIAL_NUMBER_A");
        String type = input.getString("PREWIDE_TYPE");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSerialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // 没有获取到有效的主号码信息！
            CSAppException.apperr(CrmUserException.CRM_USER_615);
        }
        // 查询主号码是否为宽带用户
        IDataset mainSet = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + mainSerialNumber);
        if (IDataUtil.isEmpty(mainSet))
        {
            // 该主账号还没有办理过宽带业务！
            CSAppException.apperr(CrmUserException.CRM_USER_1067);
        }
        else
        {
            if (!mainSet.getData(0).getString("RSRV_STR2").equals("1"))
            {
                // 非gpon宽带不能办理
                CSAppException.apperr(WidenetException.CRM_WIDENET_11);
            }
        }

        IDataset dataUU = null;// 查询主账号下的子账号
        String roleCodeB = null;
        String userIdB = mainSet.getData(0).getString("USER_ID");
        // 判断主账号是否有家庭,平行的子账号
        if ("2".equals(type))// 平行
        {
            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "77");
            if (IDataUtil.isNotEmpty(dataUU))
            {
                roleCodeB = dataUU.getData(0).getString("ROLE_CODE_B");
                if ("2".equals(roleCodeB))
                {
                    String number = "";// 主账号号码
                    // 查询主账号下所有子账号
                    IDataset allAcct = RelaUUInfoQry.getAllSubAcct(userIdB, "77");
                    if (IDataUtil.isNotEmpty(allAcct))
                    {
                        number = allAcct.getData(0).getString("SERIAL_NUMBER_B").substring(3);

                    }

                    // 该账号为子账号,不能继续开通平行子账号.其主账号号码为:["+number+"]
                    CSAppException.apperr(CrmUserException.CRM_USER_1068, number);
                }
            }

            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "78");// 选择平行账号主账号下不能有家庭子账号
            if (IDataUtil.isNotEmpty(dataUU))
            {
                // 此主账号存在家庭子账号，不能继续开通平行子账号!
                CSAppException.apperr(CrmUserException.CRM_USER_1069);
            }
        }
        else if ("1".equals(type))// 家庭
        {
            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "78");
            if (IDataUtil.isNotEmpty(dataUU))
            {
                // 该账号不是普通账号,不能继续开通家庭子账号!
                CSAppException.apperr(CrmUserException.CRM_USER_1070);

            }
            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "77");
            if (IDataUtil.isNotEmpty(dataUU))
            {
                // 该账号不是普通账号,不能继续开通家庭子账号!
                CSAppException.apperr(CrmUserException.CRM_USER_1070);

            }

        }
        // 判断主号码下面未完工单
        judgeMasterAccount(userIdB);

        IDataset resultList = CommparaInfoQry.getCommpara("CSM", "1900", null, CSBizBean.getTradeEparchyCode());// 配置参数表取限定个数
        if (IDataUtil.isEmpty(resultList))
        {
            // 没有配置宽带子账号个数，请配置！
            CSAppException.apperr(CrmUserException.CRM_USER_1075);
        }
        int num = resultList.getData(0).getInt("PARA_CODE1");
        int numfamily = resultList.getData(0).getInt("PARA_CODE2");

        if ("2".equals(type))// 判断平行关系个数
        {
            IDataset ExituserB = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("77", userIdB, "1");
            if (ExituserB.size() > 0 && ExituserB != null)
            {// 根据虚拟的id捞取下列所有的副卡用户
                IDataset ExituserAll = RelaUUInfoQry.getSEL_USER_ROLEA("77", ExituserB.getData(0).getString("USER_ID_A"), "2");
                if (ExituserAll.size() > 0 && ExituserAll != null)
                {
                    // 平行账号只能有6个

                    if (num <= ExituserAll.size())
                    {
                        // 主账号已存在"+num+"个平行关系子账号，请换一个主账号
                        CSAppException.apperr(CrmUserException.CRM_USER_1076, num);
                    }

                }

            }
        }
        if ("1".equals(type))
        {// 判断家庭关系个数
            IDataset ExitFauser = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("78", userIdB, "1");
            if (ExitFauser.size() > 0 && ExitFauser != null)
            {
                IDataset ExituserFaAll = RelaUUInfoQry.getSEL_USER_ROLEA("78", ExitFauser.getData(0).getString("USER_ID_A"), "2");
                if (ExituserFaAll.size() > 0 && ExituserFaAll != null)
                {
                    // 家庭只能有一个

                    if (numfamily <= ExituserFaAll.size())
                    {
                        // 主账号已存在"+numfamily+"个家庭关系子账号，请换一个主账号
                        CSAppException.apperr(CrmUserException.CRM_USER_1077, numfamily);
                    }

                }
            }
        }
        data.put("GPON_USER_ID", mainSet.getData(0).getString("USER_ID"));
        data.put("GPON_SERIAL_NUMBER", "KD_" + mainSerialNumber);
        data.put("STAND_ADDRESS", mainSet.getData(0).getString("STAND_ADDRESS"));
        data.put("STAND_ADDRESS_CODE", mainSet.getData(0).getString("STAND_ADDRESS_CODE"));
        data.put("DETAIL_ADDRESS", mainSet.getData(0).getString("DETAIL_ADDRESS"));
        data.put("PHONE", mainSet.getData(0).getString("PHONE"));
        data.put("CONTACT", mainSet.getData(0).getString("CONTACT"));
        data.put("CONTACT_PHONE", mainSet.getData(0).getString("CONTACT_PHONE"));

        IDataset allAcct = new DatasetList();
        IDataset relationInfos = RelaUUInfoQry.getAllSubAcct(userIdB, "77");

        if (relationInfos.size() > 0)
        {
            for (int i = 0, size = relationInfos.size(); i < size; i++)
            {
                IData relationInfo = relationInfos.getData(i);
                String userId = relationInfo.getString("USER_ID_B");
                String serialNumberB = relationInfo.getString("SERIAL_NUMBER_B");
                roleCodeB = relationInfo.getString("ROLE_CODE_B");
                IDataset svcStateInfos = UserSvcStateInfoQry.getUserValidSvcStateByUserId(userId);
                String stateNameStr = USvcStateInfoQry.qryStateNameBySvcIdStateCode("2010", svcStateInfos.getData(0).getString("STATE_CODE")).getData(0).getString("STATE_NAME");
                IData info = new DataMap();
                info.put("SERIAL_NUMBER", serialNumberB.substring(3));// 号码
                info.put("TYPE", roleCodeB.equals("1") ? "主账号" : "子账号");// 类型
                info.put("USER_STATE_CODESET", stateNameStr);// 主体服务状态
                allAcct.add(info);
            }

        }
        else
        {
            IDataset svcStateInfos = UserSvcStateInfoQry.getUserValidSvcStateByUserId(userIdB);
            String stateNameStr = USvcStateInfoQry.qryStateNameBySvcIdStateCode("2010", svcStateInfos.getData(0).getString("STATE_CODE")).getData(0).getString("STATE_NAME");
            IData info = new DataMap();
            info.put("SERIAL_NUMBER", mainSerialNumber);// 号码
            info.put("TYPE", "普通账号");// 类型
            info.put("USER_STATE_CODESET", stateNameStr);// 主体服务状态
            allAcct.add(info);
        }
        data.put("ALL_ACCT", allAcct);

        return data;
    }

    /**
     * 判断主账号下面是否有未完工单
     * 
     * @param userId
     * @throws Exception
     */
    private void judgeMasterAccount(String userId) throws Exception
    {

        // 需要判断有没有未完工的子账号开户
        IDataset ExituserFamib = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("77", userId, "1");
        if (IDataUtil.isNotEmpty(ExituserFamib))
        {
            IDataset ExituserFamiA = TradeInfoQry.getExistTrade("77", ExituserFamib.getData(0).getString("USER_ID_A"), "2");
            if (IDataUtil.isNotEmpty(ExituserFamiA))
            {
                // 此主账号存在未完工的平行子账号开户，不能继续开通子账号！
                CSAppException.apperr(CrmUserException.CRM_USER_1072);
            }
        }

        // 没有子账号
        IDataset Exituser = TradeInfoQry.getExistUser("77", userId, "1");

        if (IDataUtil.isNotEmpty(Exituser))
        {
            // 此主账号存在未完工的平行子账号开户，不能继续开通子账号！
            CSAppException.apperr(CrmUserException.CRM_USER_1072);
        }
        IDataset ExituserU = TradeInfoQry.getExistUser("78", userId, "1");
        if (IDataUtil.isNotEmpty(ExituserU))
        {
            // 此主账号存在未完工的家庭子账号开户，不能继续开通子账号！
            CSAppException.apperr(CrmUserException.CRM_USER_1073);
        }
    }

    //获取配置的宽带类型
    public IDataset showProdMode(IData input) throws Exception
    {
    	IDataset results = CommparaInfoQry.getCommpara("CSM", "210", "WIDE_TYPE_PROD_MODE", "0898");
    	return results;
    }


    //当月是否已经做过移机并且产品变更
    public IDataset chkWideMoveAndChgProdNum(IData input) throws Exception
    {
    	IDataset results = new DatasetList();
    	//判断本月是否做过移机，根据widenet表的start_date判断
    	//这个字段，在移机回单的时候，会进行更新
    	String userId = input.getString("USER_ID");
    	
        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", input.getString("SERIAL_NUMBER"));
        String inDate = userInfo.getData(0).getString("OPEN_DATE");
		String inMonth = SysDateMgr.getDateForYYYYMMDD(inDate).substring(0, 6);
		String currMonth = SysDateMgr.getNowCycle().substring(0, 6);
		boolean bExtend = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "WIDENETMOVE_FIRST");
		IData idFirst = new DataMap();
		idFirst.put("WIDENETMOVE_FIRST", "0");
		boolean bFirst = false;
		if (inMonth.equals(currMonth))//判断是不是宽带首月免费期内不能移机
		{
			if(!bExtend)
			{
				CSAppException.apperr(CrmUserException.CRM_USER_783,"宽带免费期内不能移机");
			}
			else
			{
				bFirst = true;
				idFirst.put("WIDENETMOVE_FIRST", "1");
			}
		}

        // 查询生效的优惠
        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
        if (discntInfo == null || discntInfo.size() <= 0)
        {
        	if(!bExtend)
			{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带免费期内不能移机");
			}
        	else
			{
        		bFirst = true;
				idFirst.put("WIDENETMOVE_FIRST", "1");
			}
        }
        // add by zhangxing3 for REQ201804280023优化“先装后付，免费体验”
        //查询是否存在生效的宽带免费体验套餐
        IDataset discntInfo1 = UserDiscntInfoQry.getUserIMSDiscnt(userId,"8523","0898");
        if (IDataUtil.isNotEmpty(discntInfo1))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带免费试用期间，不允许进行移机");
        }
        
    	IDataset wideNetInfo = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isNotEmpty(wideNetInfo))
        {
        	String startDate = wideNetInfo.getData(0).getString("START_DATE");
    		String smonth = SysDateMgr.getDateForYYYYMMDD(startDate).substring(0, 6);
    		String tmonth = SysDateMgr.getNowCycle().substring(0, 6);
    		if (smonth.equals(tmonth))//判断是不是最后一个月
    		{
    			if(!bFirst)
    			{
    				CSAppException.apperr(CrmUserException.CRM_USER_783,"请勿在本月重复办理移机业务！");
    			}
    		}
        }
        results.add(idFirst);
    	return results;
    }
    
    //是否可以进行产品变更
    public IDataset chkProductChgRule(IData input) throws Exception
    {
    	IData idata = new DataMap();
    	idata.put("IS_CHG_OTHER", "0");
    	idata.put("IS_HAS_YEAR_DISCNT", "0");
    	String wideUserId = input.getString("USER_ID");
    	
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(wideUserId);
        String sysDate = SysDateMgr.getSysTime();
        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (SysDateMgr.compareTo(userProduct.getString("START_DATE"),sysDate) >= 0)
                {
                	idata.put("IS_CHG_OTHER", "1");
                }
            }
        }
        
        //当前限制，有包年套餐的话，不能做包年套餐的升档降档或者变营销活动
		IDataset discntYears = BreQryForCommparaOrTag.getCommpara("CSM", 213, input.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());
		IDataset userDiscnts = UserDiscntInfoQry.getSpecDiscnt(wideUserId);
		if ((userDiscnts != null && userDiscnts.size() > 0)&&(discntYears != null && discntYears.size() > 0))
        {
			for (int i = 0; i < userDiscnts.size(); i++)
            {
                IData element = userDiscnts.getData(i);
                for(int j=0;j<discntYears.size();j++){
                	if(element.getString("DISCNT_CODE").equals(discntYears.getData(j).getString("PARA_CODE1"))){
                		String endDate = element.getString("END_DATE");
                		if(!(0==SysDateMgr.monthIntervalYYYYMM(chgFormat(SysDateMgr.getSysDate(),"yyyy-MM-dd","yyyyMM"),chgFormat(endDate,"yyyy-MM-dd HH:mm:ss","yyyyMM")))){
                        	idata.put("IS_HAS_YEAR_DISCNT", "1");
                		}
                	}
                }
            }
        }
        
    	IDataset results = new DatasetList();
    	results.add(idata);
    	return results;
    }
    
    public IDataset getWidenetProductInfo(IData input) throws Exception
    {
        String productMode = input.getString("NEW_PRODUCT_MODE");
        String userId = input.getString("USER_ID");
        
        //增加产品规则校验，产品变更时的规则不能直接拿过来用，此处增加处理
        String isChgOther = "",isHasYearDiscnt="";
        IDataset chkInfos = chkProductChgRule(input);
        if(chkInfos!=null&&chkInfos.size()>0){
        	if("1".equals(chkInfos.getData(0).getString("IS_CHG_OTHER"))){
        		isChgOther = chkInfos.getData(0).getString("IS_CHG_OTHER");
        		return chkInfos;
        	}
        	isChgOther = chkInfos.getData(0).getString("IS_CHG_OTHER");
        	isHasYearDiscnt = chkInfos.getData(0).getString("IS_HAS_YEAR_DISCNT");
        }

    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	if(serialNumber.startsWith("KD_1")){
    		serialNumber = serialNumber.substring(3);
    		//input.put("SERIAL_NUMBER", serialNumber);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	String seriUserId = userInfo.getData(0).getString("USER_ID");
        
        //修改原有方法，改为调用新增的查询产品列表的方法，新增的方法有速率信息
        IDataset widenetProductInfos = WidenetInfoQry.getWidenetProduct_RATE(productMode, CSBizBean.getTradeEparchyCode());// ProductInfoQry.getWidenetProductInfo(prod_mode, CSBizBean.getTradeEparchyCode());
        
        //add by zhangxing3 for REQ201704110012开发1000M宽带产品资费
        //isGigabit 表示端口是否支持1000M宽带，否-不支持；是-支持
        
        /*String isGigabit = input.getString("IS_GIGABIT","");
        System.out.println("============WidenetMoveBean============isGigabit:"+isGigabit);
        if("否".equals(isGigabit) || "".equals(isGigabit))//如果isGigabit = 否 或 空，表示端口不支持1000M宽带，需要从宽带产品中过滤掉
        {
        	widenetProductInfos = WideNetUtil.filterGigabitProduct(widenetProductInfos);
        }*/
        //add by zhangxing3 for REQ201704110012开发1000M宽带产品资费 end
        
        if("1".equals(input.getString("IS_BUSINESS_WIDE","")))
        {
        	//商务宽带不判断营销活动和包年套餐
        	return widenetProductInfos;
        }
        else
        {
            //不是商务宽带还需要过滤掉  小微商务商品
            widenetProductInfos = WideNetUtil.filterBusinessProduct("", widenetProductInfos);
        }
        
    	String hasEffActive = "0",hasYearActive="0",hasEndYearActive="0";
    	input.put("MOBILE_USER_ID", seriUserId);
    	if(isUserSaleActive(input)) hasEffActive = "1";
    	IData yearActive = getUserYearActive(input);
    	if (IDataUtil.isNotEmpty(yearActive)){
    		hasYearActive = "1";
    		hasEndYearActive = yearActive.getString("END_MONTH");
    	}
    	String hasEffYear = "0";
		IDataset discntYears = BreQryForCommparaOrTag.getCommpara("CSM", 213, input.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());
		IDataset userDiscnts = UserDiscntInfoQry.getSpecDiscnt(userId);
		if ((userDiscnts != null && userDiscnts.size() > 0)&&(discntYears != null && discntYears.size() > 0))
        {
			for (int i = 0; i < userDiscnts.size(); i++)
            {
                IData element = userDiscnts.getData(i);
                for(int j=0;j<discntYears.size();j++){
                	if(element.getString("DISCNT_CODE").equals(discntYears.getData(j).getString("PARA_CODE1"))){
                		String endDate = element.getString("END_DATE");
                		if(!(0==SysDateMgr.monthIntervalYYYYMM(chgFormat(SysDateMgr.getSysDate(),"yyyy-MM-dd","yyyyMM"),chgFormat(endDate,"yyyy-MM-dd HH:mm:ss","yyyyMM")))){
                			hasEffYear = "1";
                		}
                	}
                }
            }
        }
		
		//是否有包月优惠
		String hasMonthDiscnt = "0";
		
    	IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);
    	
    	// 查询改优惠配置
		IDataset discntCommparas = CommparaInfoQry.getCommparaAllColByParser("CSM", "532", "MONTH_DISCNT","0898");

		if (IDataUtil.isNotEmpty(userDiscntElements) && IDataUtil.isNotEmpty(discntCommparas))
		{
			IData userDiscntElement = null;
			IData discntCommpara = null;
			
			for (int i = 0; i < userDiscntElements.size(); i++)
			{
				userDiscntElement = userDiscntElements.getData(i);
				
				for (int j = 0; j < discntCommparas.size(); j++)
				{
					discntCommpara = discntCommparas.getData(j);
					
					if (userDiscntElement.getString("ELEMENT_ID","").equals(discntCommpara.getString("PARA_CODE1")))
					{
						hasMonthDiscnt = "1";
					}
				}
			}
		}
		
		for(int i=0;i<widenetProductInfos.size();i++){
			widenetProductInfos.getData(i).put("IS_CHG_OTHER", isChgOther);
			widenetProductInfos.getData(i).put("IS_HAS_YEAR_DISCNT", isHasYearDiscnt);
			widenetProductInfos.getData(i).put("HAS_EFF_ACTIVE", hasEffActive);
			widenetProductInfos.getData(i).put("HAS_EFF_YEAR", hasEffYear);
			widenetProductInfos.getData(i).put("HAS_YEAR_ACTIVE", hasYearActive);
			widenetProductInfos.getData(i).put("HAS_End_YEAR_ACTIVE", hasEndYearActive);
			widenetProductInfos.getData(i).put("HAS_MONTH_DISCNT", hasMonthDiscnt);
		}
        
        return widenetProductInfos;
    }

    public IDataset getModelInfo(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	if(serialNumber.startsWith("KD_1")){
    		serialNumber = serialNumber.substring(3);
    		input.put("SERIAL_NUMBER", serialNumber);
    	}
    	IDataset results = WidenetInfoQry.getCommQryInfo(input);
        if(results==null||results.size()==0){
        	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
        	String userId = userInfo.getData(0).getString("USER_ID");
        	results = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "FTTH_GROUP");
        }
    	return results;
    }
    

    public int getUserCaseBalance(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	StringBuffer depositeNotes = WidenetMoveTrade.getCashItemType();
		String serialNumberWide = serialNumber;
    	if(serialNumber.startsWith("KD_1")){
    		serialNumberWide = serialNumber.substring(3);
    	}
		IDataset allAccountDeposit = AcctCall.queryAccountDepositBySn(serialNumberWide);
        String [] balanceDepositCodesArray = depositeNotes.toString().split("\\|");
        int totalDepositBalance = 0;
        if (IDataUtil.isNotEmpty(allAccountDeposit))
        {
            for (int i = 0, size = allAccountDeposit.size(); i < size; i++)
            {
                IData depositData = allAccountDeposit.getData(i);
                String tempDepositCode = depositData.getString("DEPOSIT_CODE", "");
                String depositBalance = depositData.getString("DEPOSIT_BALANCE", "0");
                
                //判断当前用户存折是否属于可转出的现金类存折
                for (int j = 0,length = balanceDepositCodesArray.length; j < length; j++ )
                {
                    if (balanceDepositCodesArray[j].contains(tempDepositCode))
                    {
                        totalDepositBalance += Integer.parseInt(depositBalance);
                        break;
                    }
                }
            }
        }

    	return totalDepositBalance;
    }

    public IDataset getUserBalance(IData input) throws Exception
    {
        int totalDepositBalance = getUserCaseBalance(input);
    	IDataset results = WidenetInfoQry.getCommQryInfo(input);
    	results.getData(0).put("CASH_BALANCE", totalDepositBalance);
    	return results;
    }
    
    public IDataset getUserProductInfo(IData data) throws Exception
    {
    	String userId = data.getString("USER_ID");
        String productMode = "";
        String routeEparchyCode = data.getString("EPARCHY_CODE");
        String userProductName = "";
        String userBrandName = "";
        String userProductId = "";
        String userBrandCode = "";
        String nextProductId = "";
        String nextBrandCode = "";
        String nextBrandName = "";
        String nextProductName = "";
        String nextProductStartDate = "";
        //add by danglt
        String userSaleActiveCycle = "";
        String userSaleActiveFee = "";
        String userWideRate = WideNetUtil.getWidenetUserRate(data.getString("SERIAL_NUMBER"));
        //end
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        String sysDate = SysDateMgr.getSysTime();
        
        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                    userProductId = userProduct.getString("PRODUCT_ID");
                    userBrandCode = userProduct.getString("BRAND_CODE");
                    productMode = userProduct.getString("PRODUCT_MODE");
                }
                else
                {
                    nextProductId = userProduct.getString("PRODUCT_ID");
                    nextBrandCode = userProduct.getString("BRAND_CODE");
                    nextProductStartDate = userProduct.getString("START_DATE");
                }
            }
        }
        
        IData result = new DataMap();
        // 查询用户当前品牌名称，当前产品名称
        userBrandName = UBrandInfoQry.getBrandNameByBrandCode(userBrandCode);
        userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
        result.put("USER_PRODUCT_NAME", userProductName);
        result.put("USER_PRODUCT_ID", userProductId);
        result.put("USER_BRAND_NAME", userBrandName);
        if (!StringUtils.isBlank(nextProductId))
        {
            nextProductName = UProductInfoQry.getProductNameByProductId(nextProductId);
            nextBrandName = UBrandInfoQry.getBrandNameByBrandCode(nextBrandCode);
            result.put("NEXT_PRODUCT_NAME", nextProductName);
            result.put("NEXT_BRAND_NAME", nextBrandName);
            result.put("NEXT_PRODUCT_ID", nextProductId);
            result.put("NEXT_PRODUCT_START_DATE", nextProductStartDate);
        }
        result.put("EPARCHY_CODE", routeEparchyCode);

        IDataset widenetProductInfos = ProductInfoQry.getWidenetProductInfo(productMode, CSBizBean.getTradeEparchyCode());
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), widenetProductInfos);
        
        IDataset saleActives = getUserSaleActive(data);
        //add by zhangxing for 候鸟月、季、半年套餐（海南）
        String saleProductName = "",salePackageName = "",saleProductId = "",salePackageId="";
        if(saleActives!=null&&saleActives.size()>0){
        	IData sale = saleActives.getData(0);
        	saleProductId = sale.getString("PRODUCT_ID");
        	salePackageId = sale.getString("PACKAGE_ID");
        	
        	IDataset prodInfo = UpcCall.qryCatalogByCatalogId(saleProductId);
        	
        	if(prodInfo!=null&&prodInfo.size()>0)
        	{
        	    saleProductName = prodInfo.getData(0).getString("CATALOG_NAME");
        	} 
        	
        	salePackageName = UpcCall.qryOfferNameByOfferTypeOfferCode(salePackageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
            //danglt
            IDataset userSaleActiveCycleList = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", salePackageId, null);
            if (IDataUtil.isNotEmpty(userSaleActiveCycleList)) {
                userSaleActiveCycle = userSaleActiveCycleList.first().getString("PARA_CODE4");
            }

            IDataset userSaleActiveFeeList = UpcCall.qryPricePlanInfoByOfferId(salePackageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
            if (IDataUtil.isNotEmpty(userSaleActiveFeeList)) {
                userSaleActiveFee = userSaleActiveFeeList.first().getString("FEE");
            }
            //end
        }
        
        result.put("SALE_PRODUCT_NAME", saleProductName);
        result.put("SALE_PRODUCT_ID", saleProductId);
        result.put("SALE_PACKAGE_NAME", salePackageName);
        result.put("SALE_PACKAGE_ID", salePackageId);//danglt
        result.put("USER_SALE_CYCLE", userSaleActiveCycle);//danglt
        result.put("USER_SALE_FEE", userSaleActiveFee);//danglt
        result.put("USER_WIDE_RATE", userWideRate);//danglt
        result.put("PRODUCT_LIST", widenetProductInfos);
        result.put("PRODUCT_MODE", productMode);
        result.put("PRODUCT_ID", userProductId);
        IDataset resu = new DatasetList();
        resu.add(result);
        
        // 查询用户
        return resu;
    }
    
    
    
    public IDataset getUserProductInfoNew(IData data) throws Exception
    {
    	IData result = new DataMap();
    	 IDataset saleActivenew = getUserSaleActiveNew(data);
         String saleProductName = "",salePackageName = "",saleProductId = "";
 		if(saleActivenew!=null&&saleActivenew.size()>0){
 			IData sale = saleActivenew.getData(0);
 			saleProductId = sale.getString("PRODUCT_ID");
         	String salePackageIds = sale.getString("PACKAGE_ID");
         	
         	IDataset prodInfo = UpcCall.qryCatalogByCatalogId(saleProductId);
        	
        	if(prodInfo!=null&&prodInfo.size()>0)
        	{
        	    saleProductName = prodInfo.getData(0).getString("CATALOG_NAME");
        	} 
         	
         	salePackageName = UpcCall.qryOfferNameByOfferTypeOfferCode(salePackageIds, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
 		}
         
        result.put("SALE_PRODUCT_NAME", saleProductName);
        result.put("SALE_PACKAGE_NAME", salePackageName);
        IDataset resu = new DatasetList();
        resu.add(result);
        
        // 查询用户
        return resu;
    }
    
    
    
    public IDataset getSaleActiveComm(IData data) throws Exception
    {
    	String eparchyCode = data.getString("EPARCHY_CODE");
    	String paramAttr = data.getString("PARAM_ATTR");
    	String paramCode = data.getString("PARAM_CODE");
    	IDataset results = CommparaInfoQry.getCommpara("CSM", paramAttr, paramCode, eparchyCode);
    	return results;
    }
    
    public boolean isUserSaleActive(IData input) throws Exception{
    	//查看用户是否已经办理过营销活动
    	String seriUserId = input.getString("MOBILE_USER_ID");
    	
    	//根据tf_f_user_discnt的product_id获取营销活动的优惠对应的start_date，再取营销活动周期之后，判断是否已经满足一个周期
    	IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM", 212, input.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());
    	if (IDataUtil.isNotEmpty(commparaDs))
        {
    		if(seriUserId==null||"".equals(seriUserId)){
    			String serialNumber = input.getString("SERIAL_NUMBER");
    	    	if(serialNumber.startsWith("KD_1")){
    	    		serialNumber = serialNumber.substring(3);
    	    		//input.put("SERIAL_NUMBER", serialNumber);
    	    	}
    	    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	    	seriUserId = userInfo.getData(0).getString("USER_ID");	
    		}
    		
    		IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(seriUserId);
    		if (IDataUtil.isNotEmpty(useDiscnts)){
    			for(int i=0;i<commparaDs.size();i++){
    				for(int j=0;j<useDiscnts.size();j++){
    					IData commDs = commparaDs.getData(i);
    					IData userDs = useDiscnts.getData(j);
    					
    					if(StringUtils.isNotEmpty(userDs.getString("PRODUCT_ID"))&&userDs.getString("PRODUCT_ID").equals(commDs.getString("PARA_CODE1"))){
    						//新规则，活动的结束日期都是2050年，所以真正的结束日期要自己根据有效期计算
    	                	String sdate = userDs.getString("START_DATE");
    	                	// add by zhangxing3 for REQ201907040036关于宽带1+活动中途终止后可立即申请宽带产品变更
    	                	String edate = userDs.getString("END_DATE");
    	                	String emonth = SysDateMgr.getDateForYYYYMMDD(edate).substring(0, 6);
    	                	// add by zhangxing3 for REQ201907040036关于宽带1+活动中途终止后可立即申请宽带产品变更
    	                	String s_pakid = userDs.getString("PACKAGE_ID");
    	                	//获取营销活动的周期
    	                	IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", s_pakid, null);
    	                	int months=12;//默认12个月
    	                	if (IDataUtil.isNotEmpty(com181))
    	                	{
    	                		months=com181.getData(0).getInt("PARA_CODE4",12);
    	                	}
    	                	sdate = SysDateMgr.getAddMonthsLastDay(months, sdate);
    	                	int iret = SysDateMgr.compareTo(sdate, SysDateMgr.getSysDate());

    	                	if (iret >=0)
    	                	{
    	                		//有效
    	                		String smonth = SysDateMgr.getDateForYYYYMMDD(sdate).substring(0, 6);
    	                		String tmonth = SysDateMgr.getNowCycle().substring(0, 6);
    	                		//add by zhangxing3 for 候鸟月、季、半年套餐（海南）:针对候鸟特殊处理
        	                	// add by zhangxing3 for REQ201907040036关于宽带1+活动中途终止后可立即申请宽带产品变更
    	                		if (!smonth.equals(tmonth) && !emonth.equals(tmonth) && !"66002202".equals(userDs.getString("PRODUCT_ID",""))
    	                				&& !"66004809".equals(userDs.getString("PRODUCT_ID","")))//判断是不是最后一个月
    	                		{
    	                			return true;
    	                		}
    	                	}
    					}
    				}
    			}
    		}
        }
    	return false;
    }
    
    public IDataset getUserSaleActive(IData input) throws Exception{
    	//查看用户是否已经办理过营销活动
    	String seriUserId = input.getString("MOBILE_USER_ID", "");
    	IDataset resu = new DatasetList();
    	
    	//根据tf_f_user_discnt的product_id获取营销活动的优惠对应的start_date，再取营销活动周期之后，判断是否已经满足一个周期
    	IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM", 212, "606", CSBizBean.getUserEparchyCode());
    	IDataset commparaDsYear = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", CSBizBean.getUserEparchyCode());
    	if (IDataUtil.isNotEmpty(commparaDs)){
    		commparaDs.addAll(commparaDsYear);
    	}
    	
    	if (IDataUtil.isNotEmpty(commparaDs))
        {
    		if(seriUserId==null||"".equals(seriUserId)){
    			String serialNumber = input.getString("SERIAL_NUMBER");
    	    	if(serialNumber.startsWith("KD_1")){
    	    		serialNumber = serialNumber.substring(3);
    	    		//input.put("SERIAL_NUMBER", serialNumber);
    	    	}
    	    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	    	
    	    	if (IDataUtil.isEmpty(userInfo))
    	    	{
    	    	    CSAppException.appError("-1", "宽带手机用户信息不存在！");
    	    	}
    	    	
    	    	seriUserId = userInfo.getData(0).getString("USER_ID");	
    		}
    		
    		IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(seriUserId);
    		if (IDataUtil.isNotEmpty(useDiscnts)){
    			for(int i=0;i<commparaDs.size();i++){
    				for(int j=0;j<useDiscnts.size();j++){
    					IData commDs = commparaDs.getData(i);
    					IData userDs = useDiscnts.getData(j);
    					if(StringUtils.isNotEmpty(userDs.getString("PRODUCT_ID")) 
    					        && userDs.getString("PRODUCT_ID").equals(commDs.getString("PARA_CODE1"))){
    						//新规则，活动的结束日期都是2050年，所以真正的结束日期要自己根据有效期计算
    	                	//String sdate = userDs.getString("START_DATE");
    	                	//String s_pakid = userDs.getString("PACKAGE_ID");
    	                	//获取营销活动的周期
    	                	/*IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", s_pakid, null);
    	                	int months=12;//默认12个月
    	                	if (IDataUtil.isNotEmpty(com181))
    	                	{
    	                		months=com181.getData(0).getInt("PARA_CODE4",12);
    	                	}
    	                	sdate = SysDateMgr.getLastDayOfMonth(months);
    	                	int iret = SysDateMgr.compareTo(sdate, SysDateMgr.getSysDate());
    	                	if (iret >=0)
    	                	{
    	                		
    	                	}*/
    	                	resu.add(userDs);
	                		return resu;
    					}
    				}
    			}
    		}
        }
    	return null;
    }
    
    public IDataset getUserSaleActiveNew(IData input) throws Exception{
    	//查看用户是否已经办理过营销活动
    	String seriUserId = input.getString("MOBILE_USER_ID", "");
    	IDataset resu = new DatasetList();
    	
    	//根据tf_f_user_discnt的product_id获取营销活动的优惠对应的start_date，再取营销活动周期之后，判断是否已经满足一个周期
    	IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM", 1355, "606", CSBizBean.getUserEparchyCode());
    	
    	if (IDataUtil.isNotEmpty(commparaDs))
        {
    		if(seriUserId==null||"".equals(seriUserId)){
    			String serialNumber = input.getString("SERIAL_NUMBER");
    	    	if(serialNumber.startsWith("KD_1")){
    	    		serialNumber = serialNumber.substring(3);
    	    		//input.put("SERIAL_NUMBER", serialNumber);
    	    	}
    	    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	    	
    	    	if (IDataUtil.isEmpty(userInfo))
    	    	{
    	    	    CSAppException.appError("-1", "宽带手机用户信息不存在！");
    	    	}
    	    	
    	    	seriUserId = userInfo.getData(0).getString("USER_ID");	
    		}
    		
    		IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(seriUserId);
    		if (IDataUtil.isNotEmpty(useDiscnts)){
    			for(int i=0;i<commparaDs.size();i++){
    				for(int j=0;j<useDiscnts.size();j++){
    					IData commDs = commparaDs.getData(i);
    					IData userDs = useDiscnts.getData(j);
    					if(StringUtils.isNotEmpty(userDs.getString("PRODUCT_ID")) 
    					        && userDs.getString("PRODUCT_ID").equals(commDs.getString("PARA_CODE1"))){
    						//新规则，活动的结束日期都是2050年，所以真正的结束日期要自己根据有效期计算
    	                	//String sdate = userDs.getString("START_DATE");
    	                	//String s_pakid = userDs.getString("PACKAGE_ID");
    	                	//获取营销活动的周期
    	                	/*IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", s_pakid, null);
    	                	int months=12;//默认12个月
    	                	if (IDataUtil.isNotEmpty(com181))
    	                	{
    	                		months=com181.getData(0).getInt("PARA_CODE4",12);
    	                	}
    	                	sdate = SysDateMgr.getLastDayOfMonth(months);
    	                	int iret = SysDateMgr.compareTo(sdate, SysDateMgr.getSysDate());
    	                	if (iret >=0)
    	                	{
    	                		
    	                	}*/
    	                	resu.add(userDs);
	                		return resu;
    					}
    				}
    			}
    		}
        }
    	return null;
    }

    public IData getUserEffActive(IData input) throws Exception{
    	//查看用户是否已经办理过营销活动
    	String seriUserId = input.getString("MOBILE_USER_ID");
    	IData effActive = new DataMap();
    	
		IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(seriUserId);
		IDataset kdActives = BreQryForCommparaOrTag.getCommpara("CSM", 212, input.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());
		if ((saleActives != null && saleActives.size() > 0)&&(kdActives != null && kdActives.size() > 0))
        {
			for (int i = 0; i < saleActives.size(); i++)
            {
                IData element = saleActives.getData(i);
                for(int j=0;j<kdActives.size();j++){
                	if(element.getString("PRODUCT_ID").equals(kdActives.getData(j).getString("PARA_CODE1"))){
                		effActive = element;
                	}
                }
            }
        }
    	return effActive;
    }

    public IData getUserYearActive(IData input) throws Exception{
    	//查看用户是否已经办理过营销活动
    	String seriUserId = input.getString("MOBILE_USER_ID");
    	IData effActive = new DataMap();
    	
		IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(seriUserId);
		IDataset kdActives = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", CSBizBean.getUserEparchyCode());
		if ((saleActives != null && saleActives.size() > 0)&&(kdActives != null && kdActives.size() > 0))
        {
			for (int i = 0; i < saleActives.size(); i++)
            {
                IData element = saleActives.getData(i);
                for(int j=0;j<kdActives.size();j++){
                	if(element.getString("PRODUCT_ID").equals(kdActives.getData(j).getString("PARA_CODE1"))){
                		effActive = element;
                		String endDate = saleActives.getData(i).getString("END_DATE");
                		effActive.put("END_MONTH", "0");
                		if(!(0==SysDateMgr.monthIntervalYYYYMM(chgFormat(SysDateMgr.getSysDate(),"yyyy-MM-dd","yyyyMM"),chgFormat(endDate,"yyyy-MM-dd HH:mm:ss","yyyyMM")))){
                			effActive.put("END_MONTH", "1");
                		}
                	}
                }
            }
        }
    	return effActive;
    }
    
    public IData checkSaleActiveDiscnt(IData input) throws Exception
    {
    	String userId = input.getString("USER_ID");
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    		//input.put("SERIAL_NUMBER", serialNumber);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	String seriUserId = userInfo.getData(0).getString("USER_ID");
    	input.put("MOBILE_USER_ID", seriUserId);
    	
    	//查看用户是否同时办理包年优惠和营销活动
    	boolean hasSelYear = false, hasEffYear = false, hasEffActive = false, hasSelActive = false;
    	boolean isChgProd = false;
		String selElms = input.getString("SELECTED_ELEMENTS","");
    	if ((StringUtils.isNotBlank(input.getString("IS_CHG_PROD")))&&(input.getString("IS_CHG_PROD").equals("TRUE"))){
    		isChgProd = true;
    		IDataset discntYears = BreQryForCommparaOrTag.getCommpara("CSM", 213, input.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());
    		
    		IDataset selectedElements = new DatasetList(input.getString("SELECTED_ELEMENTS"));
    		if(!"".equals(selElms))
	    		if ((selectedElements != null && selectedElements.size() > 0)&&(discntYears != null && discntYears.size() > 0))
		        {
	    			for (int i = 0; i < selectedElements.size(); i++)
		            {
		                IData element = selectedElements.getData(i);
		                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE"))&&"0".equals(element.getString("MODIFY_TAG")))
		                {
		                    for(int j=0;j<discntYears.size();j++){
		                    	if(element.getString("ELEMENT_ID").equals(discntYears.getData(j).getString("PARA_CODE1"))){
		                    		hasSelYear = true;
		                    	}
		                    }
		                }
		            }
		        }
    	}
    	if("".equals(selElms)) input.put("IS_CHG_PROD", "FALSE");
    	
    	if ((StringUtils.isNotBlank(input.getString("IS_CHG_SALE")))&&(input.getString("IS_CHG_SALE").equals("TRUE"))&&
    			(StringUtils.isNotBlank(input.getString("SALEACTIVE_PACKAGE_ID")))&&(!"".equals(input.getString("SALEACTIVE_PACKAGE_ID")))){
    		hasSelActive = true;
    	}
    	
    	IData effActive = new DataMap();
    	IData effYearDiscnt = new DataMap();
		IDataset discntYears = BreQryForCommparaOrTag.getCommpara("CSM", 213, input.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());
		IDataset userDiscnts = UserDiscntInfoQry.getSpecDiscnt(userId);
		if ((userDiscnts != null && userDiscnts.size() > 0)&&(discntYears != null && discntYears.size() > 0))
        {
			for (int i = 0; i < userDiscnts.size(); i++)
            {
                IData element = userDiscnts.getData(i);
                for(int j=0;j<discntYears.size();j++){
                	if(element.getString("DISCNT_CODE").equals(discntYears.getData(j).getString("PARA_CODE1"))){
                		String endDate = element.getString("END_DATE");
                		if(!(0==SysDateMgr.monthIntervalYYYYMM(chgFormat(SysDateMgr.getSysDate(),"yyyy-MM-dd","yyyyMM"),chgFormat(endDate,"yyyy-MM-dd HH:mm:ss","yyyyMM")))){
                			hasEffYear = true;
                			effYearDiscnt = element;
                		}
                	}
                }
            }
        }
    	
    	hasEffActive = isUserSaleActive(input);
		effActive = getUserEffActive(input);
    	input.put("HAS_SEL_YEAR", hasSelYear);
    	input.put("HAS_EFF_YEAR", hasEffYear);
    	input.put("HAS_EFF_ACTIVE", hasEffActive);
    	input.put("EFF_ACTIVE", effActive);
    	input.put("EFF_YEAR_DISCNT", effYearDiscnt);
    	IData yearData = getUserYearActive(input);
    	boolean hasYearActive = false;
    	boolean isYearActiveEndMonth = false;
    	if (IDataUtil.isNotEmpty(yearData)){
    		hasYearActive = true;
    		if("1".equals(yearData.getString("END_MONTH","")))
    			isYearActiveEndMonth = true;
    	}
    	
    	if(hasSelYear&&hasSelActive){
    		CSAppException.apperr(CrmUserException.CRM_USER_783,"不能同时选择包年套餐和营销活动！");
    	}
    	/*if((hasEffYear||hasEffActive)&&hasSelYear&&hasEffActive&&isChgProd){
    		CSAppException.apperr(CrmUserException.CRM_USER_783,"用户有生效的营销活动或者包年套餐，请在新产品中选择移机后的包年套餐和营销活动！");
    	}*/
        String isSameSaleActive = input.getString("SAME_YEAR_SALE_ACTIVE_FLAG", "0");//add by danglt
        if((hasEffYear||hasEffActive||(hasYearActive&&isYearActiveEndMonth))&&!hasSelYear&&!hasSelActive&&isChgProd && "0".equals(isSameSaleActive)){
            CSAppException.apperr(CrmUserException.CRM_USER_783,"用户有生效的营销活动或者包年套餐，请在新产品中选择移机后的包年套餐或者营销活动！");
        }
    	
    	String modelMode = input.getString("MODEL_MODE");
    	int modelDeposit = 0;
    	//光猫押金
    	if(modelMode!=null&&"0".equals(modelMode)){
        	modelDeposit = Integer.parseInt(input.getString("MODEM_DEPOSIT"))*100;
        	//modelDeposit = "" + Integer.parseInt(modelDeposit)*100;
    		/*if("1".equals(isExchangeModel)||"0".equals(isExchangeModel)){//1 表示“新旧地区使用同厂家光猫”  0表示“新旧地区使用不同厂家光猫”
    			depositMoney = moveFtthMoney;
            }else{
        			if(hasSelActive||(!hasSelYear&&effActive!=null&&effActive.size()>0)){
                		depositMoney = secondRent;
                	}else{
                		depositMoney = firstRent;
                	}
                }else if("1".equals(isDMN)&&Integer.parseInt(ndmb)<2){//当前正在享受光猫优惠
                	depositMoney = dMNM;
                }else{//以前享受过光猫优惠
                	depositMoney = firstRent;
                }
            }*/
    	}
    	//包年活动预存
    	int saleActiveFee = Integer.parseInt(("".equals(input.getString("SALE_ACTIVE_FEE", ""))?"0":input.getString("SALE_ACTIVE_FEE", "0")));
		
    	if(saleActiveFee>0||modelDeposit>0){
	    	int useBalance = getUserCaseBalance(input);
	    	if(saleActiveFee>0&&modelDeposit>0){
	    		int totalFee = modelDeposit+saleActiveFee;
		    	if(useBalance<totalFee){
		    		CSAppException.apperr(CrmUserException.CRM_USER_783,"办理包年营销活动和租借光猫总共需要冻结预存款"+totalFee/100+"元，用户当前预存不足，请交纳预存");
		    	}
	    	}else if(saleActiveFee>0){
		    	if(useBalance<saleActiveFee){
		    		CSAppException.apperr(CrmUserException.CRM_USER_783,"办理包年营销活动需要预存款"+saleActiveFee/100+"元，用户当前预存不足，请交纳预存");
		    	}
	    	}else if(modelDeposit>0){
		    	if(useBalance<modelDeposit){
		    		CSAppException.apperr(CrmUserException.CRM_USER_783,"租借光猫需要冻结预存款"+modelDeposit/100+"元，用户当前预存不足，请交纳预存");
		    	}
	    	}
    	}
    	
    	input.put("DEPOSIT_MONEY", modelDeposit);
    	return null;
    }
    
    public IDataset getStaticInfoOnly(IData data) throws Exception
    {
    	String dataId = data.getString("DATA_ID");
    	String typeId = data.getString("TYPE_ID");
    	String dataValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", 
        		new java.lang.String[]{ "TYPE_ID", "DATA_ID" }, "PDATA_ID", new java.lang.String[]{ typeId, dataId });
    	
    	IDataset results = new DatasetList();
    	IData idata = new DataMap();
    	idata.put("DATA_VALUE", dataValue);
    	results.add(idata);
    	return results;
    }
    
    public IDataset getUserInfoBySerial(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	return userInfo;
    }
    
    public IDataset judgeIsCanMove(IData data) throws Exception
    {
        //当月是否已经做过移机并且产品变更
    	IDataset dataset = chkWideMoveAndChgProdNum(data);
    	return dataset;
    }

    //是否享受过光猫优惠
    public IDataset getDiscntModelBef(IData data) throws Exception
    {
    	String serialNumberBef = data.getString("SERIAL_NUMBER");
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    		//input.put("SERIAL_NUMBER", serialNumber);
    	}
    	data.put("SERIAL_NUMBER", serialNumber);
        IDataset modelBefInfo = FTTHModemManageBean.getUserOtherInfoByRsrvTag3(data);
    	data.put("SERIAL_NUMBER", serialNumberBef);
    	return modelBefInfo;
    }
    
    /**
     * 营销活动费用校验
     * 根据product_id和package_id查询营销活动费用，暂时只支持转账类的预存营销活动，如果配置的不是转账类的预存营销，则报错
     * 营销活动转入转出存折走费用配置表TD_B_PRODUCT_TRADEFEE IN_DEPOSIT_CODE,OUT_DEPOSIT_CODE
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryCheckSaleActiveFee(IData param) throws Exception{
    	IData result = new DataMap();
    	int totalFee = 0 ;
    	
    	//String campnType = param.getString("CAMPN_TYPE", "");
    	String activeFlag = param.getString("ACTIVE_FLAG","1") ; //活动标记，1：宽带营销活动，2：魔百和营销活动
    	String productId = param.getString("PRODUCT_ID","");
    	String packageId = param.getString("PACKAGE_ID","");
    	
    	//查询营销活动费用配置
    	//查询营销包下面所有默认必选元素费用
        IDataset businessFee = WideNetUtil.getWideNetSaleAtiveTradeFee(productId, packageId);
        
    	if (IDataUtil.isEmpty(businessFee))
        {
        	//如果该营销活动未配置费用，则直接返回成功
        	result.put("SALE_ACTIVE_FEE", "0");
        	result.put("X_RESULTCODE", "0");
            return result;
            
        }
        
    		
		for(int j = 0 ; j < businessFee.size() ; j++)
		{
			IData feeData = businessFee.getData(j);
            String fee = feeData.getString("FEE");
            
            /*if(fee != null && !"".equals(fee) && Integer.parseInt(fee) >0)
            {
            	if(!"1".equals(payMode))
                {
                	//付费模式非转账报错
            		String errorMsg = "";                				
            		if("1".equals(activeFlag))
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，融合宽带营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode)+ "]营销活动";
            		}
            		else
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户魔百和营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode)+ "]营销活动";
            		}
            		CSAppException.appError("61312", errorMsg);
                }
                
                if(!"2".equals(feeMode))
                {
                	//费用类型非预存费报错
                	String errorMsg = "";
            		if("1".equals(activeFlag))
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，宽带营销活动费用类型暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" :"营业费") + "]类型";
            		}
            		else
            		{
            			errorMsg = "营销包配置[" + packageId + "]错误，宽带营销活动暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" :"营业费") + "]类型";
            		}
            		CSAppException.appError("61313", errorMsg);
                }
            }*/
            
            totalFee += Integer.parseInt(fee);
		}
    	
    	result.put("SALE_ACTIVE_FEE", totalFee);
    	result.put("X_RESULTCODE", "0");
    	return result;
    }
    
    
    public IDataset filterElement(IDataset elements) throws Exception
    {
        IDataset matchElements = new DatasetList();

        for (int index = 0, size = elements.size(); index < size; index++)
        {
            IData element = elements.getData(index);
            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");

            if ("A".equals(elementTypeCode))
            {
            	matchElements.add(element);
            }

            if ("C".equals(elementTypeCode))
            {
            	matchElements.add(element);
            }
        }

        return matchElements;
    }

    public String getPayModeName(String payMode) throws Exception
    {
    	String payModeName = "" ;
    	if(payMode == null || "".equals(payMode))
    	{
    		payModeName = "未知类型";
    		return payModeName;
    	}
    	if("0".equals(payMode))
    	{
    		payModeName = "现金";
    	}
    	else if("1".equals(payMode))
    	{
    		payModeName = "转账";
    	}
    	else if("2".equals(payMode))
    	{
    		payModeName = "可选现金、转账";
    	}
    	else if("3".equals(payMode))
    	{
    		payModeName = "清退";
    	}
    	else if("4".equals(payMode))
    	{
    		payModeName = "分期付款";
    	}
    	return payModeName;
    }
    
	public static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
		if (null == strDate)
        {
            throw new NullPointerException();
        }

        DateFormat oldDf = new SimpleDateFormat(oldForm);
        Date date = oldDf.parse(strDate);

		String newStr = "";
        DateFormat newDf = new SimpleDateFormat(newForm);
        newStr = newDf.format(date);        
		return newStr;
	}
	

    //移机公共方法新增
    public IDataset getWideNetMoveInfo(IData data) throws Exception
    {
    	IDataset dataset = new DatasetList();
    	String dealType = data.getString("DEAL_TYPE");
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	
    	if(serialNumber.startsWith("KD_")){
    		serialNumber = serialNumber.substring(3);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	String seriUserId = userInfo.getData(0).getString("USER_ID");
    	
    	if("JUDGE_ACTIVE_INFO".equals(dealType)){
    		IData subData = new DataMap();
    		IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(seriUserId);
    		IDataset commDs = BreQryForCommparaOrTag.getCommpara("CSM", 212, "606", CSBizBean.getUserEparchyCode());
    		IDataset commYear = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", CSBizBean.getUserEparchyCode());
    		if(IDataUtil.isNotEmpty(commYear)) commDs.addAll(commYear);
    		if(IDataUtil.isNotEmpty(useDiscnts)){
    			for(int i=0;i<useDiscnts.size();i++){
    				for(int j=0;j<commDs.size();j++){
    					String dsProductId = useDiscnts.getData(i).getString("PRODUCT_ID");
    					String commPara1 = commDs.getData(j).getString("PARA_CODE1");
    					
    					if (StringUtils.isEmpty(dsProductId))
    					{
    					    continue;
    					}
    					
    					if(dsProductId.equals(commPara1)){
    			    		subData.put("PRODUCT_ID", dsProductId);
    			    		subData.put("IS_END_MONTH", "0");
    						String sdate = useDiscnts.getData(i).getString("START_DATE");
    	                	String s_pakid = useDiscnts.getData(i).getString("PACKAGE_ID");
    	                	//获取营销活动的周期
    	                	IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", s_pakid, null);
    	                	int months=12;//默认12个月
    	                	if (IDataUtil.isNotEmpty(com181))	months=com181.getData(0).getInt("PARA_CODE4",12);
    	                	sdate = SysDateMgr.getAddMonthsLastDay(months, sdate);
    	                	int iret = SysDateMgr.compareTo(sdate, SysDateMgr.getSysDate());

    	                	if (iret >=0)
    	                	{
    	                		//有效
    	                		String smonth = SysDateMgr.getDateForYYYYMMDD(sdate).substring(0, 6);
    	                		String tmonth = SysDateMgr.getNowCycle().substring(0, 6);
    	                		if (!smonth.equals(tmonth))//判断是不是最后一个月
    	                		{
    	    			    		subData.put("IS_END_MONTH", "1");
    	                		}
    	                	}
    					}
    				}
    			}
    			dataset.add(subData);
    		}
    	}else if("JUDGE_ACTIVE_IS_CAN_CHG".equals(dealType)){
    		IDataset subDataset = ParamInfoQry.getProcNameByHandGatherFee("CSM", "178", "600", CSBizBean.getUserEparchyCode());
    		return subDataset;
    	}
    	
    	return dataset;
    }
   //add by danglt
    public IDataset getSaleActiveCycle(IData input) throws Exception {
        String packageId = input.getString("PACKAGE_ID");
        return CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", packageId, null);
    }
    public IDataset checkSPAMDiscntRequirement(IData input) throws Exception {
        IData result = new DataMap();
        result.put("RESULT_CODE","0");
        IDataset dataset = new DatasetList();
        dataset.add(result);

        String serialNumber = input.getString("SERIAL_NUMBER");
        if (serialNumber.startsWith("KD_")) {
            serialNumber = serialNumber.substring(3);
        }

        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);

        if (IDataUtil.isEmpty(userInfo)) {
            return dataset;
        }

        String userId = userInfo.getString("USER_ID");
        IDataset userSPAMDiscnts = UserDiscntInfoQry.getAllDiscntByUserId(userId, "80176874");

        if (IDataUtil.isEmpty(userSPAMDiscnts)) {
            return dataset;//没有300M免费提速包优惠
        }

        String newProductStartDate = input.getString("NEW_PRODUCT_START_DATE","");
        if(StringUtils.isNotBlank(newProductStartDate)){
            String discntEndDate = userSPAMDiscnts.getData(0).getString("END_DATE");//优惠截止时间

            if(SysDateMgr.compareTo(newProductStartDate,discntEndDate) > 0){//开始时间大于优惠截止时间，则不做处理
                return dataset;
            }
        }

        String widenetType = input.getString("NEW_WIDE_TYPE","");//宽带制式
        String deviceId = input.getString("DEVICE_ID","");//宽带设备ID，用于判断城区

        //变更非FTTH制式
        if (!BofConst.WIDENET_TYPE_FTTH.equals(widenetType)
                && !BofConst.WIDENET_TYPE_TTFTTH.equals(widenetType)) {
            String errorInfo = "用户有300M提速优惠，宽带变更非FTTH制式，如果用户要求继续办理会截止该300M优惠。";
            result.put("RESULT_CODE","1");
            return dataset;
        }

        //移机到非城区
        if(StringUtils.isNotBlank(deviceId)){
            IData param = new DataMap();
            param.put("DEVICE_ID",deviceId);
            IDataset rs = CSAppCall.call("PB.AddressManageSvc.queryCityInfo", param);
            if(IDataUtil.isNotEmpty(rs)) {
                IData data = rs.first();
                if ("0".equals(data.getString("status", ""))) {
                    String errorInfo = "用户有300M提速优惠，宽带移机到非城区，如果用户要求继续办理会截止该300M优惠。";
                    result.put("RESULT_CODE","1");
                    return dataset;
                }
            }
        }

        return dataset;
    }
}
