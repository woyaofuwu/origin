
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 和商务融合产品包订购
 * REQ201804080012和商务融合产品包系统开发需求
 * @author chenzg
 *
 */
public class PersonGrpUserUnionPayBean extends CSBizBean
{
	public static final String PG_RELA_TYPE_CODE = "57"; 
	//用于获取前台数据进行校验
	private String checkTypeCode;
	Logger logger = Logger.getLogger(PersonGrpUserUnionPayBean.class);
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
     * 集团产品用户校验
     * @param input
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public IData checkBySerialNumber(IData input) throws Exception
    {
        String checkSn = input.getString("CHECK_SERIAL_NUMBER");
        String mainSn = input.getString("MAIN_SERIAL_NUMBER");
        String groupPack = input.getString("GROUP_PACK");
        this.putinLocalTradeCode(groupPack);
        IData userInfo = UcaInfoQry.qryUserInfoBySn(checkSn);
        IData ajaxData = new DataMap();
        if (IDataUtil.isNotEmpty(userInfo))
        {
            UcaData ucaData = UcaDataFactory.getNormalUca(checkSn);
            if (StringUtils.isNotBlank(ucaData.getNextAcctDay()))
            {
                CSAppException.apperr(AcctDayException.CRM_ACCTDAY_23);
            }

            this.checkOtherSerialBusiLimits(userInfo, mainSn , "0");
            IData checkSnInfo = this.checkAfterNewSnInfo(mainSn, userInfo);
            if(IDataUtil.isNotEmpty(checkSnInfo)){
            	checkSnInfo.put("GROUP_ID", ucaData.getCustGroup().getGroupId());
            }
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
     * @param input
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void checkMainSerialBusiLimits(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String serialNumber = input.getString("SERIAL_NUMBER", "");
        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);
        if (dataset.size() < 1)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        //非正常状态用户不能办理业务
        if (!"0".equals(input.getString("USER_STATE_CODESET", "")))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_802, serialNumber);
        }
        //主卡不能是其他家庭统付关系的副卡
        IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, PG_RELA_TYPE_CODE, "2");
        if (ds.size() > 0)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_803, serialNumber);
        }
        // -----2.如果该号码存在多条家庭统付关系的主号信息，则提示资料不正常---------
        ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, PG_RELA_TYPE_CODE, "1");
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
        if(isCmccStaffUser){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_16316);
        }
        
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
     * 避免修改原先代码结构，新增此方法获取前台参数
     * @param tradeTypeCode
     * @throws Exception
     * @author chenjg
     * @date 2020-2-22
     */
    private void putinLocalTradeCode(String tradeTypeCode) throws Exception
    {
    	this.checkTypeCode = tradeTypeCode;
    } 
    
    /**
     * 校验集团产品用户的业务办理限制
     * @param checkSnUserInfo
     * @param mainSn
     * @param modifyTag
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void checkOtherSerialBusiLimits(IData checkSnUserInfo, String mainSn, String modifyTag) throws Exception
    {
    	UcaData mainSnUcaData = UcaDataFactory.getNormalUca(mainSn);
        String userId = checkSnUserInfo.getString("USER_ID", "");
        String serialNumber = checkSnUserInfo.getString("SERIAL_NUMBER", "");
        IDataset ds = null;
        IDataset checkds = null;
        IDataset childPacks = null;
        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);
        if (dataset.size() < 1)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        //非正常状态用户不能作为副卡
        if (!"0".equals(checkSnUserInfo.getString("USER_STATE_CODESET", "")))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_808, serialNumber);
        }
        //成员号码不能与主号码一致
        if (serialNumber.equals(mainSn))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_809, serialNumber, mainSn);
        }
        if ("0".equals(modifyTag))
        {
            //成员号码不能是其他家庭统付关系的副卡
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, PG_RELA_TYPE_CODE, "2");
            if (ds.size() > 0)
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_811, serialNumber);
            }
            //成员号码不能是其他家庭家庭统付关系的主卡
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, PG_RELA_TYPE_CODE, "1");
            if (ds.size() > 0)
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_812, serialNumber);
            }
        }
        String mainTradeTypeCode = this.checkTypeCode;
        //校验某些产品才能作为统付成员(商务宽带、多媒体桌面电话、企业魔百和)
        ds = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "698", "PG2", productId,mainTradeTypeCode,"0898");
        checkds = CommparaInfoQry.getCommparaInfoByCode("CSM", "1698",mainTradeTypeCode,mainTradeTypeCode,"0898");
        //得到产品包所有子类信息
        if(StringUtils.isNotBlank(mainTradeTypeCode) ){
            childPacks = CommparaInfoQry.getCommparaInfoByCode2("CSM", "698","PG2",mainTradeTypeCode,"0898");
        }
        String childPacksNames = "";
	        //将子类拼接起来
	        if (IDataUtil.isNotEmpty(childPacks))
	        {
	        	for (int i = 0, iSize = childPacks.size(); i < iSize; i++)
	            {
	        		IData attrParam = childPacks.getData(i);
	    			String paraName = attrParam.getString("PARAM_NAME","");
	    			
	    			if(null != paraName&&"" != paraName){
	    				childPacksNames += paraName +"、";
	    			}
	            }
	        
	        }
	        //用于去掉最后一个顿号
	        if(null != childPacksNames && "" != childPacksNames){
	        	childPacksNames = childPacksNames.substring(0,childPacksNames.length()-1);
	        }
	        if(IDataUtil.isNotEmpty(checkds)){
        	   if (IDataUtil.isEmpty(ds))
               {
                   CSAppException.apperr(FamilyException.CRM_FAMILY_7001, serialNumber,childPacksNames);
               } 
	        }
           
        	
        
        //用户存在往月欠费不能作为统付成员
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        String fee1 = oweFee.size() > 0 ? oweFee.getString("LAST_OWE_FEE") : "0";
        if (Integer.parseInt(fee1) > 0)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_815, serialNumber);
        }

    }

    /**
     * 统一付费查询接口
     * @param data
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
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
     * 取可增加的统付成员数量
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
     * 根据结账日判断业务生效时间；自然月用户为月初，分散用户则需要进行变更 变更规则如下：本账期不变，从下账期开始进行变更[变更到自然月]，遵循账期在15和45之间
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
     * 统一付费主卡号码校验与查询
     * @param input
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public IDataset loadChildTradeInfo(IData input) throws Exception
    {
        IDataset uuMembers = new DatasetList();
        IDataset realUUMembers = new DatasetList();
        IDataset checkGroupPack = new DatasetList();
        String userId = input.getString("USER_ID");
        String groupPack = input.getString("GROUP_PACK");
        UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);
        
        if(StringUtils.isNotBlank(groupPack)){
            checkGroupPack = CommparaInfoQry.getCommparaInfoByCode("CSM", "1698",groupPack,groupPack,"0898");
        }
        String paramName = "";
        
        if(IDataUtil.isNotEmpty(checkGroupPack)){
        	paramName = checkGroupPack.getData(0).getString("PARAM_NAME");
        }
        if (StringUtils.isNotBlank(ucaData.getNextAcctDay()))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_23);
        }

        //校验主卡的业务办理限制
        this.checkMainSerialBusiLimits(input);
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, PG_RELA_TYPE_CODE, "1");
        if (uuDs.size() > 0)
        {
            //获取所有家庭统付成员信息，不包括主号
            String userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            IData userSet = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (userSet.size() < 1)
            {
                // common.error("76603","未找到虚拟用户资料");
                CSAppException.apperr(FamilyException.CRM_FAMILY_831);
            }
            uuMembers = RelaUUInfoQry.getRelationsByUserIdA(PG_RELA_TYPE_CODE,userIdA, "2");
            if(IDataUtil.isNotEmpty(uuMembers)){
            	for(int i=0;i<uuMembers.size();i++){
            		IData each = uuMembers.getData(i);
            		String remark = each.getString("REMARK");
            		if(remark.indexOf(paramName) >= 0){
            			UcaData memUcaData = UcaDataFactory.getUcaByUserId(each.getString("USER_ID_B"));
                		each.put("GROUP_ID", memUcaData.getCustGroup().getGroupId());
                		realUUMembers.add(each);
            		}
            	}
            }
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
        data.put("FMY_CUR_COUNT", realUUMembers.size());
        if(realUUMembers.size() > 0){
        	returnData.put("QRY_MEMBER_LIST", realUUMembers);
        }else{
        	returnData.put("QRY_MEMBER_LIST", "");
        }
        returnData.put("FAM_PARA", data);
        IDataset returnDatas = new DatasetList();
        returnDatas.add(returnData);
        return returnDatas;

    }

}
