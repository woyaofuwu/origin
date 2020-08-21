
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.biz.service.BizRoute;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.leaderinfo.LeaderInfoBean;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 统一付费管理
 * 
 * @author zhouwu
 * @date 2014-07-03 16:23:40
 */
public class FamilyUnionPayBean extends CSBizBean
{

	Logger logger = Logger.getLogger(FamilyUnionPayBean.class);
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
     * 分散账期新增; 检测目标号码
     * 
     * @param userInfo
     * @param acctDay
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public IData checkAfterNewSnInfo(String mainSn, IData userInfo) throws Exception
    {

        // 1.根据USERID获取用户账期相关信息
        UcaData mainUcaData = UcaDataFactory.getNormalUca(mainSn);

        IData acctDataNew = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userInfo.getString("USER_ID"));

        if (acctDataNew == null)
        {
            // common.error("532009: 获取号码" + userInfo.getString("SERIAL_NUMBER") + "账期无数据！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_817, userInfo.getString("SERIAL_NUMBER"));
        }
        acctDataNew.put("TARGET_USER_ID", userInfo.getString("USER_ID"));// 将USER_ID保存到IData中

        if (!"".equals(acctDataNew.getString("NEXT_ACCT_DAY", "")))
        {
            // String erroInfo = "532006: 号码" + userInfo.getString("SERIAL_NUMBER") + "存在预约的帐期，" +
            // "账期生效时间为" + acctDataNew.getString("NEXT_FIRST_DATE") + "，账期生效后才能办理统一付费业务！";
            // common.error(erroInfo);
            CSAppException.apperr(FamilyException.CRM_FAMILY_830, userInfo.getString("SERIAL_NUMBER"), acctDataNew.getString("NEXT_FIRST_DATE"));
        }
        // 副卡结账日
        String targetAcctDay = acctDataNew.getString("ACCT_DAY");

        // 主卡计算开始账期
        String mainStartDate = this.getDiversityStartDate(mainUcaData.getAcctDay(), mainUcaData.getUserId());

        // 如果副卡结账日为非自然月，那么起始时间为分散号码的下账期初
        String startDate = this.getDiversityStartDate(targetAcctDay, userInfo.getString("USER_ID"));

        // 如果2个号码均是分散号码，开始时间需要重新处理

        if (!"1".equals(mainUcaData.getAcctDay()) && !"1".equals(targetAcctDay))
        {
            if (Integer.parseInt(startDate) < Integer.parseInt(mainStartDate))
            {
                startDate = mainStartDate;
            }
            startDate = bothDiversityStartDate(startDate);
        }

        acctDataNew.put("SELF_START_DATE", startDate);

        return acctDataNew;
    }

    /**
     * 副号码校验
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-18
     */
    public IData checkBySerialNumber(IData input) throws Exception
    {

        String checkSn = input.getString("CHECK_SERIAL_NUMBER");
        String mainSn = input.getString("MAIN_SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(checkSn);

        IData ajaxData = new DataMap();
        if (IDataUtil.isNotEmpty(userInfo))
        {

            UcaData ucaData = UcaDataFactory.getNormalUca(checkSn);
    		//REQ201811260018关于优化个人代付关系规则的需求 wuhao5
    		//取消该限制
/*            if("PWLW".equals(ucaData.getBrandCode()))
            {
            	SvcStateTradeData tdSvcState = ucaData.getUserSvcsStateByServiceId("99010000");
            	if(!"0".equals(tdSvcState.getStateCode()))
            	{
            		CSAppException.apperr(FamilyException.CRM_FAMILY_808, checkSn);
            	}
            }*/
            

            if (StringUtils.isNotBlank(ucaData.getNextAcctDay()))
            {
                CSAppException.apperr(AcctDayException.CRM_ACCTDAY_23);
            }
            //REQ201810180017关于放开二级副以上领导铁通固定电话统付权限的需求  wuhao5
            //校验副号是否为铁通固话号码
			/*
		 	----铁通固话，是通过以下品牌判断------------=
			brand_code  
			TT01  TT03  TT05 是铁通有线固话 
			TT02  TT04  是TD1代固话
			*/
            userInfo.put("FIXPHONE_FLAG", "0");
            if (StaticUtil.getStaticValue("TD_SN_CODE", ucaData.getBrandCode()) != null){
            	String staffId = getVisit().getStaffId();
            	boolean isDataPriv = StaffPrivUtil.isFuncDataPriv(staffId, "PRIV_TF_LEADER");
            	if(isDataPriv){
            		IData param = new DataMap();
            		param.put("SERIAL_NUMBER", mainSn);
            		LeaderInfoBean bean = BeanManager.createBean(LeaderInfoBean.class);
            		IDataset snLimits = bean.checkBySerialNumberAndPermission(param);
            		if(IDataUtil.isNotEmpty(snLimits)){
            			 userInfo.put("FIXPHONE_FLAG", "1");
            		}else{
            			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "主卡号码非二级副以上领导号码,无法添加铁通固话号码作为副号！");
    	            }
            	}else{
            		 CSAppException.apperr(CrmCommException.CRM_COMM_103, "该副号为铁通固话号码,需有权限的工号才能办理！");
            	}
            }
            
            this.checkOtherSerialBusiLimits(userInfo, mainSn , "0");

            try
            {
                // ---------集团统付号码作为统一付费业务副卡时，要进行界面提示-----------
                IDataset ds = PayRelaInfoQry.getPayRelatInfoByUserIdNow(userInfo.getString("USER_ID"));
                if (IDataUtil.isNotEmpty(ds))
                {
                    ajaxData.put("MSG", "集团统付用户");
                    ajaxData.put("CODE", "3");
                }
            }
            catch (Exception e)
            {
            	if(logger.isInfoEnabled()) logger.info(e);
                ajaxData.put("MSG", e.getMessage());
                ajaxData.put("CODE", "2");
            }
            finally
            {
                if (IDataUtil.isEmpty(ajaxData))
                {
                    ajaxData.put("MSG", "OK");
                    ajaxData.put("CODE", "0");
                }
            }

            IData userAcctDay = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userInfo.getString("USER_ID"));

            if (IDataUtil.isEmpty(userAcctDay))
            {
                // common.error("532009：获取用户帐期信息无数据!");
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_137);
            }

            if (!"".equals(userAcctDay.getString("NEXT_ACCT_DAY", "")))
            {
                String erroInfo = "532006: 号码" + userInfo.getString("SERIAL_NUMBER") + "存在预约的帐期，" + "账期生效时间为" + userAcctDay.getString("NEXT_FIRST_DATE") + "，账期生效后才能办理统一付费业务！";
                // common.error(erroInfo);
                CSAppException.apperr(CrmCommException.CRM_COMM_103, erroInfo);
            }
            
            
            IDataset kdAct1 = BreQryForCommparaOrTag.getCommpara("CSM", 212, "WIDE_YEAR_ACTIVE", "0898");
            String productId = "";
            if (IDataUtil.isNotEmpty(kdAct1)){
            	for(int i=0;i<kdAct1.size();i++){
            		productId = kdAct1.getData(i).getString("PARA_CODE1", "");
            		
            		IDataset userBookActive = UserSaleActiveInfoQry.queryUserSaleActiveBookProdId(userInfo.getString("USER_ID"), productId);
            		if (IDataUtil.isNotEmpty(userBookActive)){
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经预受理了宽带包年营销活动，不能作为统一付费副卡！");
            		}
            		
            		IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userInfo.getString("USER_ID"));
            		for(int j=0;j<saleActives.size();j++){
            			if(productId.equals(saleActives.getData(j).getString("PRODUCT_ID", ""))){
            				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经办理宽带包年营销活动，不能作为统一付费副卡！");
            			}
            		}
            		
            	}
            }
            
            //wangsc10-20190219-start
            IDataset kdAct2 = BreQryForCommparaOrTag.getCommpara("CSM", 215, "WIDE1_ADD_YEAR_ACTIVE", "0898");
            String productId1 = "";
            if (IDataUtil.isNotEmpty(kdAct2)){
            	for(int i=0;i<kdAct2.size();i++){
            		productId1 = kdAct2.getData(i).getString("PARA_CODE1", "");
            		
            		IDataset userBookActive = UserSaleActiveInfoQry.queryUserSaleActiveBookProdId(userInfo.getString("USER_ID"), productId1);
            		if (IDataUtil.isNotEmpty(userBookActive)){
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经预受理了宽带1+营销活动，不能作为统一付费副卡！");
            		}
            		
            		IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userInfo.getString("USER_ID"));
            		for(int j=0;j<saleActives.size();j++){
            			if(productId1.equals(saleActives.getData(j).getString("PRODUCT_ID", ""))){
            				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经办理宽带1+营销活动，不能作为统一付费副卡！");
            			}
            		}
            		
            	}
            }
            //end
            
            //办理和路通活动的不能作为统付业务副卡begin
            
            IDataset HLTAct = BreQryForCommparaOrTag.getCommpara("CSM", 214, "FUKA_ACTIVE_LIMIE", "0898");
            if (IDataUtil.isNotEmpty(HLTAct)){
            	for(int i=0;i<HLTAct.size();i++){
            		productId = HLTAct.getData(i).getString("PARA_CODE1", "");
            		
            		IDataset userBookActive = UserSaleActiveInfoQry.queryUserSaleActiveBookProdId(userInfo.getString("USER_ID"), productId);
            		if (IDataUtil.isNotEmpty(userBookActive)){
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经预受理了和路通营销活动，不能作为统一付费副卡！");
            		}
            		
            		IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userInfo.getString("USER_ID"));
            		for(int j=0;j<saleActives.size();j++){
            			if(productId.equals(saleActives.getData(j).getString("PRODUCT_ID", ""))){
            				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经办理和路通营销活动，不能作为统一付费副卡！");
            			}
            		}
            		
            	}
            }
            //办理和路通活动的不能作为统付业务副卡end
            
            
            
            
            
          //20190219-wangsc10 对于纯宽带包月的宽带客户，允许成为统付的副卡REQ201901070006
//        	IDataset kdUserInfos = UserInfoQry.getUserInfoBySn("KD_"+checkSn, "0");//获取KD_宽带号码的user_id
//        	
//        	//如果存在宽带用户
//        	if (IDataUtil.isNotEmpty(kdUserInfos))
//        	{ 
//        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经办理宽带业务，不能作为统一付费副卡！");
//        	}
        	
        	
        	//是否是宽带未完工用户
        	IDataset kdUserUnFinishInfos = BreQry.noFinishTradeInfos(checkSn);//宽带开户未完工工单信息
        	if (IDataUtil.isNotEmpty(kdUserUnFinishInfos))
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经办理宽带开户未完工，不能作为统一付费副卡！");
        	}

            IData checkSnInfo = this.checkAfterNewSnInfo(mainSn, userInfo);

            ajaxData.putAll(checkSnInfo);
        }
        else
        {
            ajaxData.put("MSG", "该号码无效");
            ajaxData.put("CODE", "1");
        }

        return ajaxData;

    }

    /**
     * 校验主卡的业务办理限制
     * 
     * @param input
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public void checkMainSerialBusiLimits(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        String serialNumber = input.getString("SERIAL_NUMBER", "");

        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);

        if (dataset.size() < 1)
        {
            // common.error("991010", "获取TF_F_USER_INFOCHANGE数据异常["+userId+"]！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        // -----6.非正常状态用户不能办理业务----------------
        if (!"0".equals(input.getString("USER_STATE_CODESET", "")))
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
        ds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "698", "1", productId);
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
        //-----移动员工不能办理----------
        boolean isCmccStaffUser = RelaUUInfoQry.isCMCCstaffUserNew(userId);
        
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
        	isCmccStaffUser = false;
        }

        
        if(isCmccStaffUser){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_16316);
        }
        
        // -----7.限制某些优惠不能作为主卡-------------------------------

        ds = DiscntInfoQry.queryLimitDiscnts(userId, "1");
        if (ds.size() > 0)
        {
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
            }
        	else{
	            // common.error("880016", "该号码["+serialNumber+"]存在["+ds.getData(0).getString("PARAM_NAME",
	            // "")+"]优惠，不能作为主卡，请确认！");
	            CSAppException.apperr(FamilyException.CRM_FAMILY_807, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
            }
        }
    }

    /**
     * 校验副卡的业务办理限制
     * 
     * @param checkSnUserInfo
     * @param mainSn
     * @param modifytag
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public void checkOtherSerialBusiLimits(IData checkSnUserInfo, String mainSn, String modifyTag) throws Exception
    {
    	UcaData mainSnUcaData = UcaDataFactory.getNormalUca(mainSn);
        String userId = checkSnUserInfo.getString("USER_ID", "");
        String checkSnCityCode = checkSnUserInfo.getString("CITY_CODE");
        String serialNumber = checkSnUserInfo.getString("SERIAL_NUMBER", "");
        String mainSnAcctCityCode = mainSnUcaData.getAccount().getCityCode();
        IDataset ds = null;

        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);

        if (dataset.size() < 1)
        {
            // common.error("991010", "获取TF_F_USER_INFOCHANGE数据异常["+userId+"]！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        // -----8.非正常状态用户不能作为副卡-------------------
		//REQ201811260018关于优化个人代付关系规则的需求 wuhao5
		//取消该限制
        //if (!"0".equals(checkSnUserInfo.getString("USER_STATE_CODESET", "")))
        //{
            // common.error("889917", "该号码["+serialNumber+"]是非正常状态用户，不能作为副卡，请确认！");
        //    CSAppException.apperr(FamilyException.CRM_FAMILY_808, serialNumber);
        //}
        // -----7.成员号码不能主号码一致-----------------------
        if (serialNumber.equals(mainSn))
        {
            // common.error("889916", "该号码["+serialNumber+"]与主号码["+mainSn+"]一致，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_809, serialNumber, mainSn);
        }
        if ("0".equals(modifyTag))
        {
        	if("HNSJ".equals(mainSnAcctCityCode)||"HNHN".equals(mainSnAcctCityCode)){
        		if(!mainSnAcctCityCode.equals(checkSnCityCode))
        			CSAppException.apperr(FamilyException.CRM_FAMILY_835);
        	}else{
        		if("HNSJ".equals(checkSnCityCode)||"HNHN".equals(checkSnCityCode))
        			CSAppException.apperr(FamilyException.CRM_FAMILY_836);
        	}
            // -----1.成员号码不能是其他家庭统付关系的副卡-----------
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "2");
            if (ds.size() > 0)
            {
                // common.error("889910", "该号码["+serialNumber+"]是其他统一付费关系的副卡，请先退出！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_811, serialNumber);
            }
            // -----2.成员号码不能是其他家庭家庭统付关系的主卡---------
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");
            if (ds.size() > 0)
            {
                // common.error("889911", "该号码["+serialNumber+"]是其他统一付费关系的主卡，请先退出！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_812, serialNumber);
            }
        }
        // -----3.移动公话不可作为副卡-----------------
        ds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "698", "2", productId);
        if (IDataUtil.isNotEmpty(ds))
        {
            // common.error("889912", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_813, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }

        // -----4.随E行绑定、IP后付费捆绑、一卡双号、一卡付多号业务的副卡不能作为家庭统一付费业务的副卡----
        ds = RelaUUInfoQry.queryLimitUUInfos(userId, "2", "2");
        if (ds.size() > 0)
        {
            // common.error("889913", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_813, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        // -----5.宽带捆绑的副卡不能作为家庭统一付费业务的副卡------------
        /*
         * String serialNumberTmp = "KD_"+serialNumber; ds = FamilyUnionPayUtilBean.queryUserInfoBySn2(pd,
         * serialNumberTmp); if(ds!=null && ds.size()>0){ common.error("889914",
         * "该号码["+serialNumber+"]是[宽带捆绑]用户，不能作为副卡，请确认！"); }
         */
        // -----6.用户存在往月欠费不能作为副卡--------------------------
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        String fee1 = oweFee.size() > 0 ? oweFee.getString("LAST_OWE_FEE") : "0";
        if (Integer.parseInt(fee1) > 0)
        {
            // common.error("889915","该号码["+serialNumber+"]存在往月欠费，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_815, serialNumber);
        }

        //-----移动员工不能办理----------
        boolean isCmccStaffUser = RelaUUInfoQry.isCMCCstaffUserNew(userId);
        
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
        	isCmccStaffUser = false;
        }
        
        if(isCmccStaffUser){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_16316);
        }
        
        // -----7.限制某些优惠不能作为副卡-------------------------------
        ds = DiscntInfoQry.queryLimitDiscnts(userId, "2");
        if (ds.size() > 0)
        {
            // common.error("889918", "该号码["+serialNumber+"]存在["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]优惠，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_816, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        
        //begin QR-20150310-07 898号码是否允许办理统一付费问题 @yanwu add
        String strNetTypeCode = checkSnUserInfo.getString("NET_TYPE_CODE");
        String fixPhoneFlag = checkSnUserInfo.getString("FIXPHONE_FLAG", "");
        //REQ201810180017关于放开二级副以上领导铁通固定电话统付权限的需求  wuhao5
        //fixPhoneFlag为0时,不满足固话可作为副号条件
        if("0".equals(fixPhoneFlag)){
            if( "11".equals(strNetTypeCode) || "12".equals(strNetTypeCode) 
                    || "13".equals(strNetTypeCode) || "14".equals(strNetTypeCode) || "15".equals(strNetTypeCode)  ){
                   	CSAppException.apperr(FamilyException.CRM_FAMILY_834, serialNumber);
        	}
        }
        //end
    }

    /**
     * 统一付费查询接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-03 16:21:12
     */
    public IDataset familyUnionPayQuery(IData data) throws Exception
    {
    	IDataset uuMembers = new DatasetList();
        IDataUtil.chkParam(data, "SERIAL_NUMBER");

        String serialNumber = data.getString("SERIAL_NUMBER");

        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
        	//String s = StringUtils.replaceOnce("该服务号码[%s]用户信息不存在", "%s", serialNumber);
        	IData noData = new DataMap();
        	/*noData.put("X_RECORDNUM", "0");
        	noData.put("X_RESULTSIZE", "0");
        	noData.put("X_RESULTINFO", s);*/
            noData.put("X_TAG", "9");
            uuMembers.add(noData);
            return uuMembers;
            // 用户信息不存在
            //CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }

        // 查询成员的统一付费关系
        String userIdB = user.getString("USER_ID");
        IDataset uuPayRelas = RelaUUInfoQry.getRelaUUInfoByRol(userIdB, "56");// 此方法的sql语句中有like
        // '%'||:RELATION_TYPE_CODE||'%'?
        if (IDataUtil.isEmpty(uuPayRelas))
        {
        	//String s = "查询统一付费关系无数据";
        	IData noData = new DataMap();
            noData.put("X_TAG", "9");
            
            uuMembers.add(noData);
            return uuMembers;
            // 查询统一付费关系无数据
            //CSAppException.apperr(FamilyException.CRM_FAMILY_730);
        }

        IData uuPayRela = uuPayRelas.getData(0);
        String userIdA = uuPayRela.getString("USER_ID_A", "");
        String roleCodeB = uuPayRela.getString("ROLE_CODE_B", "");

        //IDataset uuMembers = new DatasetList();

        // 查询号码是主号则列出所有成员信息
        if (StringUtils.equals(roleCodeB, "1"))
        {
            uuMembers = RelaUUInfoQry.queryAllUnionPayMembers(userIdA, "56", "2");
        }
        else
        {
            uuMembers = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA, "1", "56");
            if (IDataUtil.isNotEmpty(uuMembers) && uuMembers.size() == 1)
            {
                IData uuMember = uuMembers.getData(0);
                uuMember.put("RSRV_DATE1", uuPayRela.getString("START_DATE", ""));
                uuMember.put("RSRV_DATE2", uuPayRela.getString("END_DATE", ""));
            }
            else
            {
            	//String s = "副号发起查询获取主号码信息无数据";
            	IData noData = new DataMap();
                noData.put("X_TAG", "9");
                uuMembers.add(noData);
                return uuMembers;
                // 副号发起查询获取主号码信息无数据
                //CSAppException.apperr(FamilyException.CRM_FAMILY_731);
            }
        }

        for (int i = 0, size = uuMembers.size(); i < size; i++)
        {
            // 返回字段标识发起查询的号码是：1-主号、2-副号 、9-查询无数据为空
            uuMembers.getData(i).put("X_TAG", roleCodeB);
        }

        if (IDataUtil.isEmpty(uuMembers))
        {
            IData noData = new DataMap();
            noData.put("X_TAG", "9");
            if (StringUtils.equals(roleCodeB, "1"))
            {
            	noData.put("X_TAG", "1");
            }
            uuMembers.add(noData);
        }

        return uuMembers;
    }

    /**
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
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

    /**
     * 根据结账日判断业务生效时间；自然月用户为月初，分散用户则需要进行变更 变更规则如下：本账期不变，从下账期开始进行变更[变更到自然月]，遵循账期在15和45之间
     * 
     * @param acctDay
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
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
     * 统一付费主卡号码校验与查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public IDataset loadChildTradeInfo(IData input) throws Exception
    {

        IDataset uuMembers = new DatasetList();

        String userId = input.getString("USER_ID");

        UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);

        if (StringUtils.isNotBlank(ucaData.getNextAcctDay()))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_23);
        }

        // 校验主卡的业务办理限制
        this.checkMainSerialBusiLimits(input);

        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");

        if (uuDs.size() > 0)
        {
            // ---------获取所有家庭统付成员信息，不包括主号---------
            String userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            IData userSet = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (userSet.size() < 1)
            {
                // common.error("76603","未找到虚拟用户资料");
                CSAppException.apperr(FamilyException.CRM_FAMILY_831);
            }
            uuMembers = RelaUUInfoQry.queryAllUnionPayMembers(userIdA, "56", "2");
        }
        
        //begin QR-20150310-07 898号码是否允许办理统一付费问题 @yanwu add
        UserTradeData users = ucaData.getUser();
        String strNetTypeCode = users.getNetTypeCode();
        if( "11".equals(strNetTypeCode) || "12".equals(strNetTypeCode) 
         || "13".equals(strNetTypeCode) || "14".equals(strNetTypeCode) || "15".equals(strNetTypeCode)  ){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_834, users.getSerialNumber());
        }
        //end
        
        IData acctData = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);

        String startDate = this.getDiversityStartDate(acctData.getString("ACCT_DAY"), userId);
        String endDate = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.getSysDate(), Integer.parseInt(acctData.getString("ACCT_DAY")));
        String lastMonthEndDate = DiversifyAcctUtil.getLastDayThisAcctday(SysDateMgr.addMonths(SysDateMgr.getSysDate(), -1), Integer.parseInt(acctData.getString("ACCT_DAY")));//k3

        IData data = new DataMap();
        IData returnData = new DataMap();

        data.put("START_CYCLE_ID", startDate);
        data.put("MAIN_ACCT_DAY", acctData.getString("ACCT_DAY"));
        data.put("END_CYCLE_ID", endDate.replaceAll("-", ""));// 需要单独保存本账期末，付费关系删除操作需要
        data.put("END_DATE_THIS_ACCT", endDate + SysDateMgr.END_DATE);// 此项用于UU关系，不用去掉'-';上面2项用户付费关系

        data.put("START_DATE", SysDateMgr.getSysTime());
        data.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        data.put("LAST_TIME_THIS_MONTH", endDate + SysDateMgr.END_DATE);
        data.put("MEB_LIM", this.getCommParaByFamilyUPay());
        data.put("FMY_CUR_COUNT", uuMembers.size());
        
        data.put("END_CYCLE_ID_LAST_MONTH", lastMonthEndDate.replaceAll("-", ""));// 需要单独保存本账期末，付费关系删除操作需要
        data.put("END_DATE_THIS_ACCT_LAST_MONTH",lastMonthEndDate + SysDateMgr.END_DATE);
        data.put("LAST_TIME_THIS_MONTH_LAST_MONTH", lastMonthEndDate + SysDateMgr.END_DATE);

        //REQ202001300001  关于个人统付功能新增指定业务统付的需求
        if(IDataUtil.isNotEmpty(uuMembers)) {
            String acctId = "";
            String userBId = "";
            String payItemCode = "";
            String payItemCodeName = "";
            logger.info("个人统付功能-uuDs---------" + uuDs);
            IDataset acctADatas = PayRelaInfoQry.getPayrelaByUserId(uuDs.getData(0).getString("USER_ID_B", "-1"));
            logger.info("个人统付功能-acctADatas---------" + acctADatas);
            if (IDataUtil.isNotEmpty(acctADatas)) {
                acctId = acctADatas.getData(0).getString("ACCT_ID");
            }
            logger.info("个人统付功能-uuMembers---------" + uuMembers);
            userBId = uuMembers.getData(0).getString("USER_ID_B", "-1");
            logger.info("个人统付功能-userBId---------" + userBId);
            logger.info("个人统付功能-acctId---------" + acctId);
            IDataset userbPayRelations = PayRelaInfoQry.getPayrelaByUserbIdAndUseraAcctid(userBId, acctId);
            logger.info("个人统付功能-userbPayRelations---------" + userbPayRelations);
            if(IDataUtil.isNotEmpty(userbPayRelations)) {
                payItemCode = userbPayRelations.getData(0).getString("PAYITEM_CODE");
                IDataset payItemCodeList = StaticUtil.getStaticList("PAYITEM_CODE_TYPE", payItemCode);
                if(IDataUtil.isNotEmpty(payItemCodeList)) {
                    payItemCodeName = payItemCodeList.getData(0).getString("DATA_NAME", "");
                }
            }
            logger.info("个人统付功能-payItemCode---------" + payItemCode);
            logger.info("个人统付功能-payItemCodeName---------" + payItemCodeName);
            data.put("PAYITEM_CODE", payItemCode);
            data.put("PAYITEM_CODE_NAME", payItemCodeName);
        }else {
            /*IDataset payItemCodeSet = CommparaInfoQry.getCommPkInfo("CSM", "1010", "PAYITEM_CODE_TYPE", "0898");
            returnData.put("PAYITEM_CODE_LIST", payItemCodeSet);*/
            logger.info("个人统付功能-payItemCode---------" + "未指定业务");
            data.put("PAYITEM_CODE", "");
        }

        if(uuMembers.size() > 0){
        	returnData.put("QRY_MEMBER_LIST", uuMembers);
        }else{
        	returnData.put("QRY_MEMBER_LIST", "");
        }


        logger.info("个人统付功能-payItemCode---------" + data);
        returnData.put("FAM_PARA", data);

        IDataset returnDatas = new DatasetList();

        returnDatas.add(returnData);

        return returnDatas;

    }

}
