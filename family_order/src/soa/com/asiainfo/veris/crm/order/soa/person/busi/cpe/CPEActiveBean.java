package com.asiainfo.veris.crm.order.soa.person.busi.cpe;
 
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 统一付费管理
 * 
 * @author zhouwu
 * @date 2014-07-03 16:23:40
 */
public class CPEActiveBean extends CSBizBean
{ 
     
	private static final Logger log = Logger.getLogger(CPEActiveBean.class);
    /**
     * 查询已绑定的CPE号码
     * */
    public IDataset loadChildInfo2(IData input) throws Exception
    {
    	IData param=new DataMap();
    	param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));   
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_UU_CPE_BY_SN", param);
    }
    
    /**
     * BUG20160401095924 “CPE绑定付费关系”取消bug
     * 根据主号，获取判断是否主号已经绑定了CPE号码。
     * CHENXY3 20160506
     */
    public static IDataset getRelationUUCPEInfo(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.*   ");
        parser.addSQL(" from TF_F_RELATION_UU t  ");
        parser.addSQL(" where T.USER_ID_A = (select t.USER_ID_A  ");
        parser.addSQL(" from TF_F_RELATION_UU t  ");
        parser.addSQL(" where T.SERIAL_NUMBER_B = :SERIAL_NUMBER_B  ");
        parser.addSQL(" and t.relation_type_code = 'CP'  ");
        parser.addSQL(" AND SYSDATE < T.END_DATE  ");
        parser.addSQL(" AND T.ROLE_CODE_B = '1')  ");
    	return Dao.qryByParse(parser);
    }
    
    /**
     * 统一付费主卡号码校验与查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public IDataset loadChildInfo(IData input) throws Exception
    {
    	 
        IDataset uuMembers = new DatasetList();
        IDataset uuMembers2= new DatasetList();

        String userId = input.getString("USER_ID");

        UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);

        if (StringUtils.isNotBlank(ucaData.getNextAcctDay()))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_23);
        }

        // 校验主卡的业务办理限制
        this.checkMainSerialBusiLimits(input);
        //uuMembers = this.loadChildInfo2(input);
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "CP", "1");
        String equNum="";
        if (uuDs.size() > 0)
        {
            // ---------获取所有CPE成员信息，不包括主号---------
            String userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            IData userSet = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (userSet.size() < 1)
            {
                // common.error("76603","未找到虚拟用户资料");
                CSAppException.apperr(FamilyException.CRM_FAMILY_831);
            }
            uuMembers = RelaUUInfoQry.queryAllUnionPayMembers(userIdA, "CP", "2");
            
            //增加显示设备号（未完成）
            
            if(uuMembers.size()>0){
            	for(int k=0;k<uuMembers.size();k++){
            		IData uuMemData=uuMembers.getData(k);
	            	String memUserId=uuMemData.getString("USER_ID_B");
	            	IData memInput=new DataMap();
	            	memInput.put("USER_ID", memUserId);
	            	IDataset mems=this.qryUserOtherInfo(memInput);
	            	if(mems!=null && mems.size()>0){
	            		equNum=mems.getData(0).getString("RSRV_STR4"); 
	            		uuMemData.put("EQU_NUM", equNum);
	            		uuMembers2.add(uuMemData);
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
        data.put("FMY_CUR_COUNT", uuMembers2.size());
        if(uuMembers2.size() > 0){
        	returnData.put("QRY_MEMBER_LIST", uuMembers2);
        }else{
        	returnData.put("QRY_MEMBER_LIST", "");
        }
        
        returnData.put("FAM_PARA", data);

        IDataset returnDatas = new DatasetList();

        returnDatas.add(returnData);

        return returnDatas;

    }
    
    /**
     * 查询已绑定的CPE号码
     * */
    public IData checkCPENumber(IData input) throws Exception
    { 
    	IData param=new DataMap();  
    	param.put("SERIAL_NUMBER", input.getString("CHECK_SERIAL_NUMBER"));   
        IDataset results = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_CPE_BY_SN", param);
         IData rtn = new DataMap();
         if(results!=null && results.size()>0){  
        	IData checkData=checkBySerialNumber(input);
        	String code=checkData.getString("CODE","");
        	if("".equals(code)){
        		checkData.put("CODE", "0");
        	}
        	rtn.putAll(checkData);
         }else{
         	rtn.put("CODE", "2");
         	rtn.put("MSG", "该号码不是CPE开户号码。"); 
         }
        return  rtn;
    }
    
    /**
     * CPE用户信息查询
     * */
    public IDataset cpeInfoQry(IData input) throws Exception
    {
    	IDataset qryDataset=new DatasetList();  
    	
        IDataset results = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_CPE_BY_SN", input);
        IData rtn = new DataMap();
        if(results!=null && results.size()>0){  
        	qryDataset=ifCheckSnHasBand(input); 
        	if(qryDataset!=null && qryDataset.size()>0){
        		IData qryData=qryDataset.getData(0);
        		String ifLock=qryData.getString("RSRV_VALUE","");
        		if("0".equals(ifLock)){
        			qryData.put("RTN_CODE", "0");
        			/**
        			 * //REQ201601120007 CPE无线宽带查询用户使用业务所在小区界面
        			 * chenxy3 20160223
        			 * */
                	String areaCode=qryData.getString("RSRV_STR21","");
                	String cells="";
                	if(!"".equals(areaCode)){
                		IData areaInput=new DataMap();
                		if(areaCode.indexOf(",")>-1){
                			StringTokenizer st=new StringTokenizer(areaCode,",");
                			while(st.hasMoreElements()){
                				String cellName="";
                				String cellId=st.nextToken();
                				String cell_id=cellId.substring(9); 
                				areaInput.put("CELL_ID", cell_id);
                    			IDataset areaInfos=this.qryCPEAreaList2(areaInput);
                    			if(areaInfos!=null && areaInfos.size()>0){
                    				cellName=areaInfos.getData(0).getString("AREA_NAME");
                    			}else{
                    				cellName="新建小区-"+cell_id;
                    			}
                    			if("".equals(cells)){
                    				cells=cell_id+"("+cellName+")";
                    			}else{
                    				cells=cells+","+cell_id+"("+cellName+")";
                    			}
                			}
//                			String cell1=areaCode.substring(0,areaCode.indexOf(","));
//                			String cella1=cell1.substring(9);
//                			String cell2=areaCode.substring(areaCode.indexOf(",")+1);
//                			String cella2=cell2.substring(9);
//                			areaInput.put("CELL_ID", cella1);
//                			IDataset areaInfos=this.qryCPEAreaList(areaInput);
//                			String cellName1="";
//                			if(areaInfos!=null && areaInfos.size()>0){
//                				cellName1=areaInfos.getData(0).getString("AREA_NAME");
//                			}
//                			areaInput.put("CELL_ID", cella2);
//                			areaInfos=this.qryCPEAreaList(areaInput);
//                			String cellName2="";
//                			if(areaInfos!=null && areaInfos.size()>0){
//                				cellName2=areaInfos.getData(0).getString("AREA_NAME");
//                			}
//                			rsrv1=cell1+"("+cellName1+"),"+cell2+"("+cellName2+")";
                		}else{
                			String areaCode_old=areaCode.substring(9);
                			areaInput.put("CELL_ID", areaCode_old);
                			IDataset areaInfos=this.qryCPEAreaList(areaInput);
                			String cellName1=areaInfos.getData(0).getString("AREA_NAME");
                			cells=areaCode+"("+cellName1+")";
                		}
                		qryData.put("RSRV_STR1", cells);
                	}
                	qryDataset.add(qryData);
        		}else{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码未进行小区锁定！");
        		}        		
        	}else{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码还未进行绑定！");
        	}
         }else{
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码不是CPE开户号码！");
         }
         return  qryDataset;
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
        //sonar修改。
        if(userInfo.getString("USER_ID")!=null && !"".equals(userInfo.getString("USER_ID"))){
        	acctDataNew.put("TARGET_USER_ID", userInfo.getString("USER_ID"));// 将USER_ID保存到IData中
        }else{
       	 	CSAppException.apperr(CrmCommException.CRM_COMM_103,"取不到有效的用户信息[userInfo.getString(USER_ID)]="+userInfo.getString("USER_ID")+"！");
        }
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
        acctDataNew.put("NOW_ACCT_DAY", targetAcctDay);

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
        
        String equipNum=input.getString("EQUIPMENT_NUM");
        String ifFirstTimeTag="0";

        IData userInfo = UcaInfoQry.qryUserInfoBySn(checkSn);

        IData ajaxData = new DataMap();
        if (IDataUtil.isNotEmpty(userInfo))
        {
        	
            UcaData ucaData = UcaDataFactory.getNormalUca(checkSn); 
            if (StringUtils.isNotBlank(ucaData.getNextAcctDay()))
            {
                CSAppException.apperr(AcctDayException.CRM_ACCTDAY_23);
            }
            //判断是否副号是第一次绑定，如果不是，则要求设备号码必须有值
            IData inData=new DataMap();
            inData.put("USER_ID", userInfo.getString("USER_ID"));
            IDataset checkSnHis=this.ifCheckSnHasBand(inData);
            if( checkSnHis.size()>0 ){
            	//不是第一次
            	ifFirstTimeTag=""+checkSnHis.size();
            }else{
            	if(equipNum==null || "".equals(equipNum)){
            		CSAppException.apperr(FamilyException.CRM_FAMILY_6983, checkSn);
            	}else{
            		//这里调用接口,确认输入设备号码是否正确
            		IData callInput=new DataMap();
            		callInput.put("SERIAL_NUMBER", checkSn);
            		callInput.put("RES_ID", equipNum);
            		this.checkModem(callInput); //调用接口，判断设备是否合法
            	}
            }
            ajaxData.put("FIRSTTIME_TAG", ifFirstTimeTag);
            
            this.checkOtherSerialBusiLimits(userInfo, mainSn , "0");

            try
            {
                // ---------已存在统一付费业务副卡时，要进行界面提示-----------
                IDataset ds = PayRelaInfoQry.getPayRelatInfoByUserIdNow(userInfo.getString("USER_ID"));
                if (IDataUtil.isNotEmpty(ds))
                {
                    ajaxData.put("MSG", "已绑定付费用户");
                    ajaxData.put("CODE", "3");
                }
            }
            catch (Exception e)
            { 
            	//log.info("(e);
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
                String erroInfo = "532006: 号码" + userInfo.getString("SERIAL_NUMBER") + "存在预约的帐期，" + "账期生效时间为" + userAcctDay.getString("NEXT_FIRST_DATE") + "，账期生效后才能办理CPE绑定付费业务！";
                // common.error(erroInfo);
                CSAppException.apperr(CrmCommException.CRM_COMM_103, erroInfo);
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
        
        String checkFeeFlag="1";

        String serialNumber = input.getString("SERIAL_NUMBER", "");

        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);

        if (dataset.size() < 1)
        {
            // common.error("991010", "获取TF_F_USER_INFOCHANGE数据异常["+userId+"]！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");
        String brandCode=dataset.getData(0).getString("BRAND_CODE");
        
        IDataset userLists=this.qryUserInfo(input);
        if(userLists!=null && userLists.size()>0){
	        String userAcctTag= userLists.getData(0).getString("ACCT_TAG");
	        if(!"0".equals(userAcctTag)){
	        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "主号必须是激活后的正常用户！");
	        }
	        String isRealName= userLists.getData(0).getString("IS_REAL_NAME");
	        if(!"1".equals(isRealName) && !"2".equals(isRealName)){
	        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "主号必须是实名制用户！");
	        }
        }else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "主号无用户信息或者客户信息！");
        }
        
        
        if("CPE1".equals(brandCode)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "CPE号码无法作为付费主号！");
        }

        // -----6.非正常状态用户不能办理业务----------------13647571068
        if (!"0".equals(input.getString("USER_STATE_CODESET", "")))
        {
            // common.error("880015", "该号码["+serialNumber+"]是非正常状态用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_802, serialNumber);
        }
        // -----1.主卡不能是CPE的副卡-----------
        IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "CP", "2");
        if (ds.size() > 0)
        {
            // common.error("880010", "该号码["+serialNumber+"]是其他统一付费关系的副卡，不能作为主卡，请先退出！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_903, serialNumber);
        }
        // -----2.如果该号码存在多条CPE的主号信息，则提示资料不正常---------
        ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "CP", "1");
        if (ds.size() > 1)
        {
            // common.error("880011", "该号码["+serialNumber+"]存在多条统一付费的主号UU数据，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_904, serialNumber);

        }else if(ds.size()==1){
        	checkFeeFlag="0";
        }
        // -----3.随E行、移动公话、8位或11位TD无线固话不可作为主卡-----------------
        ds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "698", "1", productId);
        if (ds.size() > 0)
        {
            // common.error("880012", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_805, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        // -----4.随E行绑定、IP后付费捆绑、一卡双号、一卡付多号业务的副卡不能作主卡----
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
        /**
         * BUG20160401095924 “CPE绑定付费关系”取消bug
         * 根据主号，获取判断是否主号已经绑定了CPE号码。
         * CHENXY3 20160506
         */
        IData checkNum=new DataMap();
        checkNum.put("SERIAL_NUMBER_B", serialNumber);;
        IDataset checkSet=getRelationUUCPEInfo(checkNum);
        if(checkSet!=null && checkSet.size()>0){
        	
        }else{
	        IData saleactiveData = new DataMap();
	        saleactiveData.put("SERIAL_NUMBER", serialNumber);
	        saleactiveData.put("PRODUCT_ID", "99992825");
	        saleactiveData.put("PACKAGE_ID", "99992825");
	        saleactiveData.put("PRE_TYPE", "1");
//	        try{
	        	CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData); 
//	        }catch(BaseException e){
//	        	String showErrInfo="校验主号是否允许办理CPE营销活动报错："; 
//	        	CSAppException.apperr(CrmCommException.CRM_COMM_103,showErrInfo+e.getMessage()+";具体信息："+e.getInfo());
//	        }
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

        //
        
        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);
        if (dataset.size() < 1)
        {
            // common.error("991010", "获取TF_F_USER_INFOCHANGE数据异常["+userId+"]！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        // -----8.非正常状态用户不能作为副卡-------------------
        if (!"0".equals(checkSnUserInfo.getString("USER_STATE_CODESET", "")))
        {
            // common.error("889917", "该号码["+serialNumber+"]是非正常状态用户，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_808, serialNumber);
        }
        // -----7.成员号码不能主号码一致-----------------------
        if (serialNumber.equals(mainSn))
        {
            // common.error("889916", "该号码["+serialNumber+"]与主号码["+mainSn+"]一致，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_809, serialNumber, mainSn);
        }
        if ("0".equals(modifyTag))
        {
        	//暂时屏蔽
//        	if("HNSJ".equals(mainSnAcctCityCode)||"HNHN".equals(mainSnAcctCityCode)){
//        		if(!mainSnAcctCityCode.equals(checkSnCityCode))
//        			CSAppException.apperr(FamilyException.CRM_FAMILY_835);
//        	}else{
//        		if("HNSJ".equals(checkSnCityCode)||"HNHN".equals(checkSnCityCode))
//        			CSAppException.apperr(FamilyException.CRM_FAMILY_836);
//        	}
            // -----1.成员号码不能是其他家庭统付关系的副卡-----------
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "CP", "2");
            if (ds.size() > 0)
            {
                // common.error("889910", "该号码["+serialNumber+"]是其他统一付费关系的副卡，请先退出！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_911, serialNumber);
            }
            // -----2.成员号码不能是其他家庭家庭统付关系的主卡---------
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "CP", "1");
            if (ds.size() > 0)
            {
                // common.error("889911", "该号码["+serialNumber+"]是其他统一付费关系的主卡，请先退出！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_912, serialNumber);
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
      //暂时屏蔽
        // -----6.用户存在往月欠费不能作为副卡--------------------------
//        IData oweFee = AcctCall.getOweFeeByUserId(userId);
//        String fee1 = oweFee.size() > 0 ? oweFee.getString("LAST_OWE_FEE") : "0";
//        if (Integer.parseInt(fee1) > 0)
//        {
//            // common.error("889915","该号码["+serialNumber+"]存在往月欠费，不能作为副卡，请确认！");
//            CSAppException.apperr(FamilyException.CRM_FAMILY_815, serialNumber);
//        }

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
        if( "11".equals(strNetTypeCode) || "12".equals(strNetTypeCode) 
         || "13".equals(strNetTypeCode) || "14".equals(strNetTypeCode) || "15".equals(strNetTypeCode)  ){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_834, serialNumber);
        }
        
        IDataset otherSNs=RelaUUInfoQry.getCheckSnNum(mainSn);
        int numLimit=this.getCommParaByFamilyUPay();
        if(otherSNs.size()>=numLimit){
        	CSAppException.apperr(FamilyException.CRM_FAMILY_819, numLimit);
        }
        
        //end
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
     * 绑定限制数
     * */
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
    
    /**
     * CPE小区锁定
     * RSRV_VALUE=0（表示锁定）
     * RSRV_VALUE=1（表示解锁）
     * */
    public void lockArea(IData input) throws Exception
    {
    	String userId="";
    	String SerialNum="";
//    	IData param=new DataMap();   
    	String cellName="";
    	String tempData=input.getString("TEMP_DATA","");
//    	//取3天前开户的数据
//    	param.put("DAYNUM", 3);
//    	param.put("RSRV_VALUE", "1");//传解锁的
//        IDataset userOpen=Dao.qryByCode("TF_F_USER_OTHER", "SEL_CPE_OPEN_FEWDAYAGO", param); 
//        
//        //备用，取临时表的数据处理        
//        IDataset tempData=Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_CPE_TEMPLATE_DATA", param); 
//        
//        userOpen.addAll(tempData);
//        if(userOpen.size()>0){
//        	for(int i=0;i<userOpen.size();i++){
        		try{
	        		IData callParam=new DataMap();
	        		//userId=userOpen.getData(i).getString("USER_ID");
	        		userId=input.getString("USER_ID");
	        		String checkDay=SysDateMgr.getSysDateYYYYMMDD();
	        		callParam.put("USER_ID", userId);
	        		callParam.put("QUERY_DATE", checkDay);//取他当前日期
	        		 
	        		//调接口
	        		/**
	        		 * REQ201605300003 CPE使用区域由原86个乡镇修改为全省的4G网络均可使用 
	        		 * 20160725新改动返回结果：
	        		 * [{"IMSI_NUMBER_1":"460007591272469","IMSI_NUMBER_2":"460007591272469","CELL_ID_1":"0121683201","CELL_ID_2":"0240574593"}]
	        		 * 其中网维的要求就是：129-46000121683201 
									其中：IMSI_NUMBER_1截取前5位就是： 46000；
									        CLEE_ID_1截取后9位是：121683201
									拼接的数据是：129-46000121683201。
	        		 * */
	        		IData cells = AcctCall.queryCellidForCPE(callParam); 
	        		//cells.put("CELL_ID_1", "1229268491");//测试用
	        		//cells.put("CELL_ID_2", "122455555");//测试用
	        		String cellId="";
	        		boolean ifRtn=false;//接口是否返回数据，用于判定是否都不在范围内
	        		for(int k=1;k<=6;k++){
	        			String cell_ID=cells.getString("CELL_ID_"+k,"");
	        			String imsi_number=cells.getString("IMSI_NUMBER_"+k,"");
	        			if(!"".equals(cell_ID)){
	        				ifRtn=true;
	        				//cell_id有可能是返回10位，也有可能小于9位
	        				if(cell_ID.length()<9){
	        					cell_ID=String.format("%09d", Integer.parseInt(cell_ID));
	        				}else if(cell_ID.length()==10){
	        					cell_ID=cell_ID.substring(1);
	        				} 
		        			if(!"".equals(cellId)){
		        				cellId=cellId+","+"129-"+imsi_number.substring(0,5)+cell_ID;
		        			}else{
		        				//cellId="129-46000"+cell_ID;
		        				cellId="129-"+imsi_number.substring(0,5)+cell_ID;
		        			}
		        			//log.info("("***<cxy>**userId="+userId+"拼接的锁定小区的cellId为："+cellId);
		        			//取中文名
		        			IData cellinput=new DataMap();  
		        			cellinput.put("CELL_ID", cell_ID);
		        			IDataset cellInfos=new DatasetList();
		        			cellInfos=qryCPEAreaList2(cellinput);//Dao.qryByCode("TD_B_CPE_LIST_ALL", "SEL_CPE_LIST_BY_CELLID", cellinput);
		        			if(cellInfos!=null && cellInfos.size()>0){	
		        				if(!"".equals(cellName)){
		        					cellName=cellName+","+cellInfos.getData(0).getString("AREA_NAME");
		        				}else{
		        					cellName=cellInfos.getData(0).getString("AREA_NAME");
		        				}
		        			}else{
		        				if(!"".equals(cellName)){
		        					cellName=cellName+","+"新建小区-"+cell_ID;
		        				}else{
		        					cellName="新建小区-"+cell_ID;
		        				}
		        			}
	        			}
	        		}
	        		/**
	        		 * REQ201602160009 CPE无线宽带锁定小区的数量改成6个
	        		 * end
	        		 * */

	        		//取用户信息
	    			IData users=UserInfoQry.getGrpUserInfoByUserId(userId, "0", "0898");  
	    			SerialNum=users.getString("SERIAL_NUMBER");
	    			users.put("STAFF_ID", "SUPERUSR");
	    			users.put("LOGIN_EPARCHY_CODE", "0898");
	    			users.put("STAFF_EPARCHY_CODE", "0898");
	    			users.put("IN_MODE_CODE", "0");
	    			users.put("CITY_CODE", "HNSJ");
	    			users.put("DEPART_ID", "36601");
	    			users.put("DEPART_CODE", "HNSJ0000"); 
	    			//users.put("REMARK", cellName);
	        		//if(cellInfos!=null && cellInfos.size()>0){  REQ201602160009 CPE无线宽带锁定小区的数量改成6个
	        		if(!"".equals(cellId)){ 
	        			users.put("CELL_ID", cellId); 
	        			users.put("CELL_NAME", cellName); 
	        			users.put("REMARK", "CPE小区锁定！");
	        			//1、小区信息，在网元提供的局数据表里，生成小区锁定业务工单
	        			IDataset result = CSAppCall.call("SS.CPEAreaLockRegSVC.tradeReg", users);
	        			
	        		}else{
	        			if(ifRtn){  //REQ201602160009 CPE无线宽带锁定小区的数量改成6个 改动
	        				//2、存在接口返回的小区信息，但是不在表数据范围里，报停！
	        				//20160318 by songlm 改为调用服务状态变更接口，变更220服务，变更操作码09，业务类型130
	        				users.put("SERVICE_ID", "220");
	        				users.put("OPER_CODE", "09");
	        				users.put("REMARK", "非指定小区，暂停上网功能！");
		        			IDataset result = CSAppCall.call("SS.ServiceOperRegSVC.tradeReg", users); 
	        			}else{
	        				//3、接口调用返回的信息都没有，说明没有使用。则要求暂时不锁定。3天后再次确认
	        				IData inparams=new DataMap();
	        				//取3天后日期
	        				String dealDate=SysDateMgr.getDateForYYYYMMDD(SysDateMgr.addDays(SysDateMgr.getSysDate(), 3));
	        				inparams.put("TRADE_TYPE_CODE","697");
	        				inparams.put("USER_ID",userId);
	        				inparams.put("DEAL_TAG","0");
	        				inparams.put("RSRV_STR2",dealDate);
	        				this.insTemplate(inparams);
	        			}
	        		}
//	        		if(tempData!=null && tempData.size()>0){
	        		if(tempData!=null && "TRUE".equals(tempData)){
		        		IData updData=new DataMap();
		        		updData.put("TRADE_TYPE_CODE", "697");
		        		updData.put("USER_ID", userId);
		        		updData.put("DEAL_DAY", SysDateMgr.getSysDateYYYYMMDD());
		        		updData.put("DEAL_TAG", "1");
		        		updData.put("ERRMSG", "成功。");
		        		this.updateLockTempTag(updData);
	        		}
	        	}catch(Exception e){
	        		//log.info("(e);
//	        		if(tempData!=null && tempData.size()>0){
	        		if(tempData!=null && "TRUE".equals(tempData)){
		        		IData updData=new DataMap();
		        		updData.put("TRADE_TYPE_CODE", "697");
		        		updData.put("USER_ID", userId);
		        		updData.put("DEAL_DAY", SysDateMgr.getSysDateYYYYMMDD());
		        		updData.put("DEAL_TAG", "9");
		        		updData.put("ERRMSG", "锁定小区报错。");
		        		this.updateLockTempTag(updData);
	        		}
	        		IData errParam=new DataMap();
	        		errParam.put("SERIAL_NUMBER",SerialNum); 
	        		errParam.put("USER_ID",userId);                        
	        		errParam.put("TRADE_TYPE_CODE","697");                     
	        		errParam.put("DEAL_RESULT_CODE","9");                     
	        		errParam.put("DEAL_RESULT_INFO",e.getMessage());                             
	        		errParam.put("RSRV_STR1","");                     
	        		errParam.put("RSRV_STR2","");                     
	        		errParam.put("RSRV_STR3","");                     
	        		errParam.put("RSRV_STR4","");                     
	        		errParam.put("RSRV_STR5","");
	        		this.insTradeErrLog(errParam);
        	}
    }
    
    
    
    
    /**
     * CPE小区解锁
     * 判断：1、一个月只允许解锁2次
     * */
    public IDataset unlockAreaQryInfo(IData input) throws Exception
    {
    	IDataset rtns=new DatasetList();
    	IData rtnInfo=new DataMap();
    	IData param=new DataMap();  
    	param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));   
        IDataset results = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_CPE_BY_SN", param);
         IData rtn = new DataMap();
         if(results!=null && results.size()>0){ 
         }else{
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码不是CPE开户号码！");
         }
    	//1、获取解锁次数，当月不允许超2次
    	String limtTimes="";
    	IDataset ds = CommparaInfoQry.getCommNetInfo("CSM", "6980", "1");
    	if(ds!=null && ds.size()>0){
    		limtTimes=ds.getData(0).getString("PARA_CODE1");
    	}
    	IDataset times=this.unlockAreaTimes(input);
    	if(times!=null && times.size()>=Integer.parseInt(limtTimes) && Integer.parseInt(limtTimes)!=0){
    		CSAppException.apperr(FamilyException.CRM_FAMILY_6982, limtTimes); 
    	}else{
    		IDataset otherInfos=this.unlockAreaQryOther(input);
    		if(otherInfos!=null && otherInfos.size()>0){
    			rtnInfo.putAll(otherInfos.getData(0));
    			rtnInfo.put("RTN_CODE", "0");
    			rtnInfo.put("RTN_MSG", "");
    		}else{
    			CSAppException.apperr(FamilyException.CRM_FAMILY_6981, input.getString("USER_ID")); 
    		}
    	}
    	rtns.add(rtnInfo);
    	return rtns;
    }
    
    /**
     * 查询OTHER表已经锁定的信息
     * */
    public IDataset unlockAreaQryOther(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select T.* "); 
        parser.addSQL(" from TF_F_USER_OTHER t ");
        parser.addSQL(" where t.user_id = :USER_ID ");
        parser.addSQL(" and T.RSRV_VALUE_CODE='CPE_LOCATION' "); 
        parser.addSQL(" and T.RSRV_VALUE = '0' ");
        parser.addSQL(" and sysdate between t.start_date and nvl(t.end_date,to_date('2050-12-30','yyyy-mm-dd')) "); 

        return Dao.qryByParse(parser); 
    	
    }
    
    /**
     * 查询本月解锁次数。
     * */
    public IDataset unlockAreaTimes(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select  T.* "); 
        parser.addSQL(" from TF_BH_TRADE t ");
        parser.addSQL(" where T.USER_ID=:USER_ID ");
        parser.addSQL(" AND T.TRADE_TYPE_CODE='698' ");  
        parser.addSQL(" AND T.subscribe_state = '9'");
        parser.addSQL(" AND TRUNC(T.FINISH_DATE,'MM')=TRUNC(SYSDATE,'MM') "); 

        return Dao.qryByParse(parser,Route.getJourDb());
    }
    
    /**
     * 查询CPE全量小区清单
     * */
    public IDataset qryCPEAreaList(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select  T.* "); 
        parser.addSQL(" from TD_B_CPE_LIST_ALL t ");
        parser.addSQL(" where CELL_ID=TO_CHAR(TO_NUMBER(:CELL_ID)) "); 

        return Dao.qryByParse(parser);
    }
    
    /**
     * 查询CPE全量小区清单
     * */
    public IDataset qryCPEAreaList2(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select  T.* "); 
        parser.addSQL(" from TD_B_CPE_LIST_ALL2 t ");
        parser.addSQL(" where CELL_ID=TO_CHAR(TO_NUMBER(:CELL_ID)) "); 

        return Dao.qryByParse(parser);
    }
    
    /**
     * 查询用户信息。
     * */
    public IDataset qryUserInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select  a.*,b.*"); 
        parser.addSQL(" from TF_F_USER a,tf_f_customer b ");
        parser.addSQL(" where a.cust_id=b.cust_id and a.USER_ID=:USER_ID  and a.REMOVE_TAG='0'"); 

        return Dao.qryByParse(parser);
    }
    
    /**
     * 查询用户信息。
     * */
    public IDataset qryUserOtherInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select  T.* "); 
        parser.addSQL(" from tf_f_user_other t ");
        parser.addSQL(" where t.user_id = :USER_ID ");
        parser.addSQL(" and t.rsrv_value_code = 'CPE_LOCATION' "); 
        parser.addSQL(" and sysdate between t.start_date and t.end_date "); 
        return Dao.qryByParse(parser);
    }
     
    
    /**
     * 修改用户的开始时间为当前时间     *  
     * */
    public static void updateLockTempTag(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_TYPE_CODE", params.getString("TRADE_TYPE_CODE")); 
    	param.put("ERRMSG", params.getString("ERRMSG")); 
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("DEAL_TAG", params.getString("DEAL_TAG")); 
    	param.put("DEAL_DAY", params.getString("DEAL_DAY")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update tf_f_active_template t ");
    	sql.append(" set t.deal_tag=:DEAL_TAG,T.DEAL_TIME=SYSDATE,T.RSRV_STR5=:ERRMSG ");
    	sql.append(" WHERE T.TRADE_TYPE_CODE=:TRADE_TYPE_CODE AND T.USER_ID=to_number(:USER_ID) and t.RSRV_STR2=:DEAL_DAY");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 错误记录日志     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-8-11
     */
    public void insTradeErrLog(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_CRM_LOG", "INS_TF_B_TRADE_CRM_LOG", inparams, Route.getJourDb());
    }
    
    /**
     * 记录3天后再次查是否锁定     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-8-11
     */
    public void insTemplate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_ACTIVE_TEMPLATE", "INS_F_ACTIVE_TEMPLATE", inparams);
    }
    
    /**
     * 查询副号是否已经曾经绑定过
     * */
    public static IDataset ifCheckSnHasBand(IData inData)throws Exception
    {
    	IData param=new DataMap();
    	param.put("USER_ID", inData.getString("USER_ID"));   
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_CPE_OTHER_FIRSTTIME", param);
    }
    
    /**
     * 查询副号是否已经曾经绑定过
     * */
    public static IDataset checkSnSaleActiveIfEnd(IData inData)throws Exception
    {
    	IData param=new DataMap();
    	param.put("USER_ID", inData.getString("USER_ID"));   
    	param.put("PRODUCT_ID", inData.getString("PRODUCT_ID"));  
    	param.put("PROCESS_TAG", inData.getString("PROCESS_TAG"));  
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "QRY_SALE_ACTIVE_PRODUCT_ID", param);
    }
    
    /**
     * @Function: checkModem()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public IData checkModem(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset retDataset = new DatasetList();
        String onlyCheckFlag=input.getString("ONLY_CHECK","");
        if("1".equals(onlyCheckFlag)){
        	//提交时候只需要查询，不要预占了。
        	//retDataset = HwTerminalCall.getTerminalInfoByTerminalId(resNo,CSBizBean.getVisit().getStaffId(),serialNumber ); 
        	retDataset = HwTerminalCall.getTerminalByImei(resNo,"");
        }else{
        	//录入查询时候先预占（解除预占去【终端预占释放】）
        	retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
        }
        
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
        {
            IData res = retDataset.first();
            String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
            String supplyId = res.getString("SUPPLY_COOP_ID", "");
            retData.put("X_RESULTCODE", "0");
            retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
            retData.put("RES_ID", resNo); // 终端串号
            retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
            retData.put("RES_TYPE_CODE", "4"); // 终端类型编码：4
            retData.put("RES_BRAND_CODE", res.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
            retData.put("RES_BRAND_NAME", res.getString("DEVICE_BRAND")); // 终端品牌描述
            retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
            retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
            String resStateCode = res.getString("TERMINAL_STATE", ""); // 资源状态编码1 空闲 4 已销售
            retData.put("RES_STATE_CODE", resStateCode);
            retData.put("RES_STATE_NAME", "1".equals(resStateCode) ? "空闲" : "4".equals(resStateCode) ? "已销售" : "其他");
            retData.put("RES_FEE", Double.parseDouble(res.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
            retData.put("RES_SUPPLY_COOPID", supplyId); // 终端供货商编码
            retData.put("DEVICE_COST", res.getString("DEVICE_COST", "")); //终端成本价
        }
        else
        {
            String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
        }
        return retData;
    }
    
    /**
     * @Function: updateModem()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void updateModem(IData input) throws Exception
    {
    	IData param = new DataMap();
    	String billId = input.getString("BILL_ID");
    	param.put("RES_NO", input.getString("RES_NO"));//串号
    	param.put("SALE_FEE", "0");//销售费用:不是销售传0
    	param.put("PARA_VALUE1", billId);//购机用户的手机号码
    	param.put("PARA_VALUE7", "0");//代办费
    	param.put("DEVICE_COST", input.getString("DEVICE_COST"));//进货价格--校验接口取
    	param.put("TRADE_ID ",  input.getString("TRADE_ID"));//台账流水 
    	param.put("X_CHOICE_TAG", "0");//0-终端销售,1—终端销售退货
    	param.put("RES_TYPE_CODE", "4");//资源类型,终端的传入4
    	param.put("CONTRACT_ID",  input.getString("TRADE_ID"));//销售订单号
    	param.put("PRODUCT_MODE", "0");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	param.put("PARA_VALUE11", sdf.format(new Timestamp(System.currentTimeMillis())));//销售时间
    	param.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
    	param.put("PARA_VALUE14",  input.getString("DEVICE_COST"));//裸机价格  从检验接口取裸机价格
    	param.put("PARA_VALUE15", "0");//客户购机折让价格
    	param.put("PARA_VALUE16", "0");
    	param.put("PARA_VALUE17", "0");
    	param.put("PARA_VALUE18", "0");//客户实缴费用总额  //如果没有合约，就和实际付款相等就可以。 
    	param.put("PARA_VALUE9", "03");//客户捆绑合约类型 //合约类型：01—全网统一预存购机 02—全网统一购机赠费 03—预存购机 
    	param.put("PARA_VALUE1", billId);//客户号码
    	param.put("USER_NAME", input.getString("CUST_NAME"));//客户姓名
    	param.put("STAFF_ID", getVisit().getStaffId());//销售员工
    	param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

    	IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(param);
    	if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
    		String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
    		if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
    		}
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"华为接口调用异常！");
    	}
    } 
    
    /**
     * CPE 开户满3年返还押金
     * 主号的押金返还
     * 定时器处理
     * */
    public void check3yearCPEUser(IData inParam)throws Exception {
    	IDataset userset=this.qryCPEopen3years(new DataMap());//获取CPE开户已满3年的用户
    	String mainSn="";
		String mainUserId="";
    	if(userset!=null && userset.size()>0){
    		for(int i=0;i<userset.size();i++){
    			try{
	    			IData users=userset.getData(i);
	    			String userId_cpe=users.getString("USER_ID");
	    			String serialNumber_cpe=users.getString("SERIAL_NUMBER");
	    			
	    			IData otherData=new DataMap();
	    			otherData.put("USER_ID_CPE", userId_cpe);
	    			//1、要有UU关系表信息才处理
	    			IDataset uuInfos=this.qryUUMainSnByCPEUserId(otherData);
	    			if(uuInfos!=null && uuInfos.size()>0){
	    				mainSn=uuInfos.getData(0).getString("SERIAL_NUMBER_B");
	    				mainUserId=uuInfos.getData(0).getString("USER_ID_B");
	    				IData onlineUserData = UcaInfoQry.qryUserInfoBySn(mainSn);
	    				String mainCustId=onlineUserData.getString("CUST_ID","");
	    				String mainEparchycode=onlineUserData.getString("EPARCHY_CODE","");
	    				String mainCityCode=onlineUserData.getString("CITY_CODE","");
	    				//3、获取默认账户  （acct_id)
	    		    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(mainSn);
	    		    	if(accts!=null && accts.size()>0){
	    		    		String acctId=accts.getData(0).getString("ACCT_ID");
	    		    		//取押金金额commpara表param_attr=701 取0账户
	    		    		IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "701", "0", "0898");
	    		    		String deposit=paras.getData(0).getString("PARA_CODE1");
	    		    		/**
	    	            	* CPE 解冻押金
	    	            	* */
	    	            	IData transParam=new DataMap();
	    	            	transParam.put("DEPOSIT_CODE_OUT", "9003");
	    	            	transParam.put("TRANS_BUSINESS_TYPE","2");
	    	            	transParam.put("USER_ID", mainUserId);
	    	            	transParam.put("ACCT_ID", acctId);
	    	            	transParam.put("CUST_ID", mainCustId);
	    	            	transParam.put("TRADE_FEE", deposit);
	    	            	transParam.put("EPARCHY_CODE", mainEparchycode);
	    	            	transParam.put("CITY_CODE", mainCityCode);
	    	            	transParam.put("SERIAL_NUMBER", mainSn); 
	    		    		IData inAcct=AcctCall.adjustFee(transParam);//调用接口
	    		    		String result=inAcct.getString("RESULT_CODE","");
	    		    		if(!"".equals(result) && !"0".equals(result)){  
	    		    			IData errParam=new DataMap();
				        		errParam.put("SERIAL_NUMBER",mainSn); 
				        		errParam.put("USER_ID",mainUserId);                        
				        		errParam.put("TRADE_TYPE_CODE","6991");                     
				        		errParam.put("DEAL_RESULT_CODE","9");                     
				        		errParam.put("DEAL_RESULT_INFO",inAcct.getString("RESULT_INFO"));                             
				        		errParam.put("RSRV_STR1","CPE满3年退押金");                     
				        		errParam.put("RSRV_STR2","");                     
				        		errParam.put("RSRV_STR3","");                     
				        		errParam.put("RSRV_STR4","");                     
				        		errParam.put("RSRV_STR5","");
				        		this.insTradeErrLog(errParam);
	    		    		}else{
	    		    			this.updOtherBackDepositTag(otherData);
	    		    			IData errParam=new DataMap();
				        		errParam.put("SERIAL_NUMBER",mainSn); 
				        		errParam.put("USER_ID",mainUserId);                        
				        		errParam.put("TRADE_TYPE_CODE","6991");                     
				        		errParam.put("DEAL_RESULT_CODE","1");                     
				        		errParam.put("DEAL_RESULT_INFO","成功");                             
				        		errParam.put("RSRV_STR1","CPE满3年退押金");                     
				        		errParam.put("RSRV_STR2","");                     
				        		errParam.put("RSRV_STR3","");                     
				        		errParam.put("RSRV_STR4","");                     
				        		errParam.put("RSRV_STR5","");
				        		this.insTradeErrLog(errParam);
	    		    		}
	    		    	}else{
		    				IData errParam=new DataMap();
			        		errParam.put("SERIAL_NUMBER",mainSn); 
			        		errParam.put("USER_ID",mainUserId);                        
			        		errParam.put("TRADE_TYPE_CODE","6991");                     
			        		errParam.put("DEAL_RESULT_CODE","9");                     
			        		errParam.put("DEAL_RESULT_INFO","不存在payrelation数据，可能数据存在问题。");                             
			        		errParam.put("RSRV_STR1","CPE满3年退押金");                     
			        		errParam.put("RSRV_STR2","");                     
			        		errParam.put("RSRV_STR3","");                     
			        		errParam.put("RSRV_STR4","");                     
			        		errParam.put("RSRV_STR5","");
			        		this.insTradeErrLog(errParam);
		    			}    		    	
	    			}else{
	    				IData errParam=new DataMap();
		        		errParam.put("SERIAL_NUMBER",serialNumber_cpe); 
		        		errParam.put("USER_ID",userId_cpe);                        
		        		errParam.put("TRADE_TYPE_CODE","6991");                     
		        		errParam.put("DEAL_RESULT_CODE","9");                     
		        		errParam.put("DEAL_RESULT_INFO","不存在UU关系数据，可能未绑定。");                             
		        		errParam.put("RSRV_STR1","CPE满3年退押金");                     
		        		errParam.put("RSRV_STR2","");                     
		        		errParam.put("RSRV_STR3","");                     
		        		errParam.put("RSRV_STR4","");                     
		        		errParam.put("RSRV_STR5","");
		        		this.insTradeErrLog(errParam);
	    			}
    			}catch(Exception e){
    				//log.info("(e);
    				IData errParam=new DataMap();
	        		errParam.put("SERIAL_NUMBER",mainSn); 
	        		errParam.put("USER_ID",mainUserId);                        
	        		errParam.put("TRADE_TYPE_CODE","6991");                     
	        		errParam.put("DEAL_RESULT_CODE","9");                     
	        		errParam.put("DEAL_RESULT_INFO",e.getMessage().substring(0,e.getMessage().toString().length()>90?90:e.getMessage().toString().length()-1));                             
	        		errParam.put("RSRV_STR1","CPE满3年退押金");                     
	        		errParam.put("RSRV_STR2","程序报错");                     
	        		errParam.put("RSRV_STR3","");                     
	        		errParam.put("RSRV_STR4","");                     
	        		errParam.put("RSRV_STR5","");
	        		this.insTradeErrLog(errParam);
    			}    			
    		}
    	}
    }
    /**
     * 查询CPE开户满3年的用户
     * */
    public static IDataset qryCPEopen3years(IData inData)throws Exception
    {  
        return Dao.qryByCode("TF_F_USER", "SEL_CPE_OPEN_3YEAR", inData);
    } 
    
    /**
     * 根据CPE用户user_id查询TF_F_RELATION_UU表主号信息
     * */
    public static IDataset qryUUMainSnByCPEUserId(IData inData)throws Exception
    {  
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_UU_MAINSN_BY_CPE_USERID", inData);
    } 
    
    /**
     * 利用tf_f_user_other的RSRV_STR5记录已还押金
     * */
    public void updOtherBackDepositTag(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_BACK_DEPOSIT_TAG", inparams);
    }
    
    /**
     * REQ201601120007 CPE无线宽带查询用户使用业务所在小区界面
     * chenxy3 2016-01-15
     * */
    public static IDataset dataAmountQry(IData inData)throws Exception
    {  
    	IDataset infoList=new DatasetList();
    	IDataset results = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PRODUCT_CPE_BY_SN2", inData);
        if(results!=null && results.size()>0){
        	String openDate=results.getData(0).getString("OPEN_DATE","");
        	IData callParam=new DataMap();
        	callParam.put("SERIAL_NUMBER", inData.getString("SERIAL_NUMBER",""));
        	callParam.put("USER_ID", inData.getString("USER_ID",""));
        	callParam.put("QUERY_DATE", openDate);
        	try{
	        	//调接口，查询CPE流量使用量查询（预留，未提供）
	        	IDataset infoListCall = AcctCall.queryCellidDataAmountForCPE(callParam); 
	        	DataHelper.sort(infoListCall, "ACCUMULATED_TIME", IDataset.TYPE_INTEGER, IDataset.ORDER_DESCEND);
	        	if(infoListCall!=null && infoListCall.size()>0){
		    		for(int k=0;k<infoListCall.size();k++){
		    			IData cellData=infoListCall.getData(k); 
		    			String accTime=cellData.getString("ACCUMULATED_TIME");
		    			int second=Integer.parseInt(accTime);
		    			accTime=formatTime(second);//格式化成  XX时XX分XX秒
		    			String cellId=cellData.getString("CELL_ID");
		    			String areaName="非范围小区";
		    			String telArea="非范围乡镇";
		    			CPEActiveBean bean=new CPEActiveBean();
		    			IDataset areaList=bean.qryCPEAreaList2(cellData);
		    			if(areaList!=null && areaList.size()>0){
		    				areaName=areaList.getData(0).getString("AREA_NAME");
		    				telArea=areaList.getData(0).getString("TELEPHONE_AREA");
		    			} 
		    			IData rtnData=new DataMap();
		    			rtnData.put("CELL_ID", cellId);
		    			rtnData.put("ACCUMULATED_TIME", accTime);
		    			rtnData.put("AREA_NAME", areaName);
		    			rtnData.put("TELEPHONE_AREA", telArea);
		    			
		    			infoList.add(rtnData);
		    		} 
	        	}else{
	        		IData rtnInfo=new DataMap();
	            	rtnInfo.put("CODE", "2");
	            	rtnInfo.put("MSG", "未找到相应时间的上网时长信息。");
	            	infoList.add(rtnInfo);
	        	}
        	}catch(Exception e){
        		//log.info("(e);
        		IData rtnInfo=new DataMap();
            	rtnInfo.put("CODE", "2");
            	rtnInfo.put("MSG", "接口未找到相应时间的上网时长信息。");
            	infoList.add(rtnInfo);
        	}
        	
        	//*************测试用***********
//        	for(int i=0;i<5;i++){
//	        	IData dataInfo=new DataMap();
//	        	dataInfo.put("CELL_ID", "10000"+i);
//	        	dataInfo.put("ACCUMULATED_TIME", ""+1000*i);
//	        	dataInfo.put("AREA_NAME", "海口红明橡胶公司办公楼-HLH-2");
//	        	dataInfo.put("TELEPHONE_AREA", "海口三门坡镇");
//	        	infoList.add(dataInfo);
//        	}
        	//*******************************
        }else{
        	IData rtnInfo=new DataMap();
        	rtnInfo.put("CODE", "2");
        	rtnInfo.put("MSG", "无法查询到相应的信息，该号码不是CPE号码，请确认。");
        	infoList.add(rtnInfo);
        }
        
        return infoList;
    } 
    
    /**
     * 格式化日期，将XXXXX秒的数据格式化成“XX时XX分XX秒”。
     * */
    public static String formatTime(int second){
    	String time="";
    	int h=0;
    	int m=0;
    	int s=0;
    	int temp=second%3600;
    	
    	if(second>3600){
    		h=second/3600;
    		if(temp!=0){
    			if(temp>60){
    				m=temp/60;
    				if(temp%60!=0){
    					s=temp%60;
    				}
    			}else{
    				s=temp;
    			}
    		}
    	}else{
    		m=second/60;
    		if(second%60!=0){
    			s=second%60;
    		}
    	}
    	
    	if(h>0){
    		time=h+" 时 ";
    	}
    	if(m>0){
    		time=time+m+" 分 ";
    	}
    	time=time+s+" 秒";
    	
    	return time;
    }
    
    /**
     * REQ201612260011_新增CPE终端退回和销户界面
     * <br/>
     * 先判断是否是cpe开户用户,获取cpe other表信息(设备信息)
     * @param userId 副号id
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170214
     */
    public IDataset  qryCPEOtherInfo(String userId,String rsrvValueCode) throws Exception{
 	   try {
 	       IData param = new DataMap();
 		  	  param.put("USER_ID", userId);
// 		  	  param.put("RSRV_VALUE_CODE", "CPE_LOCATION");CPE_DEVICE
 		  	  param.put("RSRV_VALUE_CODE", rsrvValueCode);
 		  	  /**
 		  	   * code_code 已经存在
 		  	   */
 		   	return  Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USRID_RSRV_VALUE", param);
 	   } catch (Exception e) {
 		   		throw e;
 	   }
    }
}