/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import com.ailk.biz.util.StaticUtil;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ForeGiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserForegiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * @CREATED by gongp@2014-4-9 修改历史 Revision 2014-4-9 下午04:52:13
 */
public class ForeGiftBean extends CSBizBean
{
    private static final transient Logger log = Logger.getLogger(ForeGiftBean.class);

    public IData getForegiftlimitDs() throws Exception
    {

        IDataset params = ParamInfoQry.getCommparaByAttrCode("CSM", "188", "30", null, "0898");
        IData commInfo = new DataMap();

        if (params.size() > 0)
        {
            String limitDate = params.getData(0).getString("PARA_CODE2");
            String sysDate = SysDateMgr.getSysDate();

            if (SysDateMgr.encodeTimestamp("yyyy-MM-DD", limitDate).getTime() >= SysDateMgr.encodeTimestamp("yyyy-MM-DD", sysDate).getTime())
            {
                // 在limitDate之前不允许清退188保证金
                commInfo.put("FOREGIFT_LIMIT", "YES");
            }
            else
            {
                commInfo.put("FOREGIFT_LIMIT", "NO");
            }
        }
        else
        {
            // common.error("获取188靓号竞拍保证金清退限制失败！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取188靓号竞拍保证金清退限制失败！");
        }
        return commInfo;

    }

    /**
     * 根据发票号码获取发票信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-17
     */
    public IDataset getInvoiceInfo(IData param) throws Exception
    {

        IDataset returnDatas = new DatasetList();
        IData tempdata = new DataMap();

        // 查询该发票是否被使用
        IDataset dataset = UserOtherInfoQry.getInvoiceInfo(param.getString("INVOICE_NO"), "FG");
        String operType = param.getString("OPER_TYPE");
        int feeSum = 0;
        if ("0".equals(operType))
        {// 收取押金

            if (dataset.size() > 0)
            {
                tempdata.put("IS_USED", "YES");// 发票号码已被使用

                IData otherServ = dataset.getData(0);

                tempdata.put("PROCESS_TAG", otherServ.getString("PROCESS_TAG"));// 发票是否被清退
            }
            else
            {
                tempdata.put("IS_USED", "NO");// 发票号码没被使用
                tempdata.put("PROCESS_TAG", "0");// 发票是否被清退
            }
            tempdata.put("INVOICE_NO", param.getString("INVOICE_NO"));
        }
        else if ("1".equals(operType))
        {// 清退押金

            dataset = UserOtherInfoQry.queryUserInvoiceInfos(param.getString("USER_ID"), param.getString("INVOICE_NO"));

            if (dataset.size() > 0)
            {
                tempdata.put("IS_USED", "YES");// 发票号码已被使用

                IData otherServ = dataset.getData(0);

                String processTag = otherServ.getString("PROCESS_TAG");
                tempdata.put("PROCESS_TAG", processTag);// 发票是否被清退

                if ("1".equals(processTag))
                {
                    // common.error("此发票已经清退押金！");
                    CSAppException.apperr(FeeException.CRM_FEE_124);
                }
				/**
				 * REQ201610110009_押金业务界面增加判断拦截
				 * @author zhuoyingzhi
				 * 20161108
				 */
				String  endDate=otherServ.getString("END_DATE");
				
				if(sysDateCompareToEndDate(endDate)){
					 //结束时间小于等于系统当前时间
					 //由于押金发票表没有未清退的记录，请和业务支撑部联系修改数据后再给用户清退
					CSAppException.apperr(FeeException.CRM_FEE_170, "该用户押金数据异常(押金发票表没有未清退的记录),请发OA单到业务支撑部协助核查，谢谢!");
				}
				
				//根据tf_f_user_otherserv中id查询    发票资料表
				String  userOtherservUserId=otherServ.getString("USER_ID");
				
				//userid为0  拦截
				if("0".equals(userOtherservUserId)){
					  //出现数据异常
					//user_id=0, 请和业务支撑部联系修改数据后再给用户清退
					CSAppException.apperr(FeeException.CRM_FEE_170, "该用户押金数据异常(user_id=0),请发OA单到业务支撑部协助核查，谢谢!");
				}
				
				//发票记录表  中的押金金额
				double  money = otherServ.getDouble("RSRV_NUM2");
				String  foregiftCode = otherServ.getString("RSRV_NUM1");
				if(isForeGiftMoney(userOtherservUserId, money,foregiftCode)){
					  //出现数据异常
					 //清退金额大于押金表总金额，请联系业务支撑部修改数据，谢谢！
					 CSAppException.apperr(FeeException.CRM_FEE_170, "该用户押金数据异常(清退金额大于押金表金额),请发OA单到业务支撑部协助核查，谢谢!");
				}
				/*************************end*************************************/
				
                for (int i = 0, size = dataset.size(); i < size; i++)
                {
                    IData servInfo = dataset.getData(i);

                    feeSum += Integer.parseInt(servInfo.getString("RSRV_NUM2", "0"));

                }

                tempdata.put("INVOICE_FEE_SUM", feeSum);// 发票总金额
                tempdata.put("INVOICE_NO", param.getString("INVOICE_NO"));

            }
            else
            {
                tempdata.put("IS_USED", "NO");// 发票号码没被使用
                tempdata.put("PROCESS_TAG", "0");// 发票是否被清退
            }
            tempdata.put("INVOICE_NO", param.getString("INVOICE_NO"));
        }
        else if ("2".equals(operType))
        {// 清退无主押金

            IDataset invoiceDataset = new DatasetList();

            // dataset = UserOtherInfoQry.queryWZUserInvoiceInfos(param.getString("USER_ID"),
            // param.getString("INVOICE_NO"));
            dataset = UserOtherInfoQry.getInvoiceInfo(param.getString("INVOICE_NO"), "FG");
            
        	/**
        	 * REQ201610110009_押金业务界面增加判断拦截
        	 * @author zhuoyingzhi
        	 * 当是无主押金清退的时候,一个发票号存在两个以上用户使用或者同一个用户使用两次，就拦截
        	 * 提示 
        	 * 数据错误,存在重复发票号，请和业务支撑部联系修改数据后再给用户清退
        	 * 
        	 */
            if(dataset.size() > 1 ){
            	//
            	//该发票号存在多条记录，请和业务支撑部联系修改数据后再给用户清退
            	CSAppException.apperr(FeeException.CRM_FEE_170,"该用户押金数据异常(发票号存在多条记录),请发OA单到业务支撑部协助核查，谢谢!");
            } 
            
            
            String nonCustomerUserId = "";
            if (dataset.size() > 0)
            {

                tempdata.put("IS_USED", "YES");// 发票号码已被使用

                IData otherServ = dataset.getData(0);

                String processTag = otherServ.getString("PROCESS_TAG");
                tempdata.put("PROCESS_TAG", processTag);// 发票是否被清退
                /**
                 * REQ201610110009_押金业务界面增加判断拦截
                 * @author zhuoyingzhi
                 * 20161117
                 * 此发票已经清退押金   这个效验  移到  点击"清退"按钮   那里
                 */
/*                if ("1".equals(processTag))
                {
                    // common.error("此发票已经清退押金！");
                    CSAppException.apperr(FeeException.CRM_FEE_124);
                }*/
				
                String userId = otherServ.getString("USER_ID", "-1");

                IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
                if (IDataUtil.isNotEmpty(userInfo))
                {
                    // common.error("非无主用户，不可以按发票号码清退押金！");
                    CSAppException.apperr(CrmUserException.CRM_USER_464);
                }

                for (int i = 0, size = dataset.size(); i < size; i++)
                {
                    IData servInfo = dataset.getData(i);

                    feeSum += Integer.parseInt(servInfo.getString("RSRV_NUM2", "0"));

                    IData tempdata1 = new DataMap();
                    tempdata1.put("FOREGIFT_CODE", servInfo.getString("RSRV_NUM1"));
                    tempdata1.put("MONEY", servInfo.getString("RSRV_NUM2"));
                    tempdata1.put("CUST_NAME", servInfo.getString("RSRV_STR2"));
                    tempdata1.put("PSPT_ID", "");
                    tempdata1.put("UPDATE_TIME", servInfo.getString("START_DATE"));
                    tempdata1.put("USER_ID", servInfo.getString("USER_ID"));
    				/**
    				 * REQ201610110009_押金业务界面增加判断拦截
    				 * @author zhuoyingzhi
    				 * 20161117
    				 */
                    //清退标识
                    tempdata1.put("PROCESS_TAG", servInfo.getString("PROCESS_TAG"));
                    //结束时间
                    tempdata1.put("END_DATE", servInfo.getString("END_DATE"));
                    /************************end***********************************/

                    nonCustomerUserId = servInfo.getString("USER_ID");
                    invoiceDataset.add(tempdata1);
                }
            }
            else
            {
                tempdata.put("IS_USED", "NO");// 发票号码没被使用
                tempdata.put("PROCESS_TAG", "0");// 发票是否被清退
            }

            tempdata.put("INVOICE_FEE_SUM", feeSum);// 发票总金额
            //获取手机号码在前台显示
            if(IDataUtil.isNotEmpty(invoiceDataset)&&invoiceDataset.size()>0)
            {
            	for(int i=0; i<invoiceDataset.size(); i++)
            	{
            		IData userinfo = new DataMap();
            		String user_id = invoiceDataset.getData(i).getString("USER_ID","");
            		userinfo = UcaInfoQry.qryUserInfoByUserId(user_id);
            		if(IDataUtil.isEmpty(userinfo)){
            			userinfo = UserInfoQry.qryUserInfoByUserIdFromHis(user_id);
            		}
            		if(IDataUtil.isNotEmpty(userinfo)){
            			invoiceDataset.getData(i).put("SERIAL_NUMBER", userinfo.getString("SERIAL_NUMBER",""));
            		}else{
            			invoiceDataset.getData(i).put("SERIAL_NUMBER", "无主押金");
            		}
            	}
            }
            tempdata.put("INVOICE_DATAS", invoiceDataset.toString());
            tempdata.put("INVOICE_NO", param.getString("INVOICE_NO"));
            tempdata.put("NON_CUSTOMER_USER_ID", nonCustomerUserId);

            qryForgiftConfig(tempdata);
        }
        returnDatas.add(tempdata);

        return returnDatas;
    }

    /**
     * 获取是否要输入发票的标志和长途漫游级别
     */
    public IData getInvoiceTag(String userId) throws Exception
    {

        IData commInfo = new DataMap();

        IDataset templist = TagInfoQry.getTagInfosByTagCode("0898", "CS_CHR_FOREGIFTINVOICE", "CSM", "0");

        if (templist.size() > 0)
        {

            IData data = templist.getData(0);
            commInfo.put("INVOICE_TAG", data.getString("TAG_CHAR", "0"));// 押金是否必须输入发票号码(1-必输 其它-不必输)
        }

        IDataset userSvcs = UserSvcInfoQry.queryUserSvcByUserIdAll(userId);

        String longId;
        // SERVICE_ID值‘13~19’分别代表 ‘本地通话、国内长途、国际长途、本地漫游、省内漫游、国内漫游、国际漫游’

        int size = userSvcs.size();

        for (int j = 0; j < size; j++)
        {
            IData svcDatas = userSvcs.getData(j);
            longId = svcDatas.getString("SERVICE_ID").toString();
            if ("13".equals(longId) || "14".equals(longId) || "15".equals(longId))
            {

                commInfo.put("LONG_SERVICE_ID", longId);
                commInfo.put("LONG_SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(longId));
                commInfo.put("PRODUCT_ID", "-1");
                commInfo.put("PACKAGE_ID", "-1");
                String startDate = svcDatas.getString("START_DATE").toString();
                String initDate = "1998-09-18" + SysDateMgr.START_DATE_FOREVER;
                if (SysDateMgr.encodeTimestamp("yyyy-MM-DD", startDate).getTime() > SysDateMgr.encodeTimestamp("yyyy-MM-DD", initDate).getTime())
                {
                    commInfo.put("ABOVE_START_DATE", "YES");
                }
                else
                {
                    commInfo.put("ABOVE_START_DATE", "NO");
                }
            }
            else if ("16".equals(longId) || "17".equals(longId) || "18".equals(longId) || "19".equals(longId))
            {
                commInfo.put("ROAM_SERVICE_ID", longId);
                commInfo.put("ROAM_SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(longId));
                commInfo.put("PRODUCT_ID", "-1");
                commInfo.put("PACKAGE_ID", "-1");
            }
        }
        // 目前的押金清退流程，新增国际及港澳台业务退订后的15天才 允许押金清退。

        IDataset dataset = UserSvcInfoQry.queryCancelLongRoamService(userId);

        if (dataset != null && dataset.size() > 0)
        {
        	//commInfo.put("CANCEL_LONGROAM_TIME", "NO");
        	
        	//如果是98年9-18 之前的用户，不判断国际长途
        	if(StringUtils.equals(commInfo.getString("ABOVE_START_DATE"), "NO")){
        		if(1==dataset.size() && StringUtils.equals("15",dataset.getData(0).getString("SERVICE_ID"))){
        			commInfo.put("CANCEL_LONGROAM_TIME", "YES");
        		}
        	}
        }
        else
        {
            commInfo.put("CANCEL_LONGROAM_TIME", "YES");
        }

        return commInfo;
    }

    /**
     * 重载父类函数 获得除三户资料以外的其它资料，
     */
    public IDataset getLoadInfo(IData param) throws Exception
    {
        IDataset results = new DatasetList();

        String sn = param.getString("SERIAL_NUMBER");
        String userId = param.getString("USER_ID");

        IData params = new DataMap();
        IDataset UserForegiftDs = UserForegiftInfoQry.getUserForegift(param.getString("USER_ID")); // 获取用户押金列表
        //获取手机号码在前台显示
        if(IDataUtil.isNotEmpty(UserForegiftDs)&&UserForegiftDs.size()>0)
        {
        	for(int i=0; i<UserForegiftDs.size(); i++)
        	{
        		IData userinfo = new DataMap();
        		String user_id = UserForegiftDs.getData(i).getString("USER_ID","");
        		userinfo = UcaInfoQry.qryUserInfoByUserId(user_id);
        		if(IDataUtil.isEmpty(userinfo)){
        			userinfo = UserInfoQry.qryUserInfoByUserIdFromHis(user_id);
        		}
        		if(IDataUtil.isNotEmpty(userinfo)){
        			UserForegiftDs.getData(i).put("SERIAL_NUMBER", userinfo.getString("SERIAL_NUMBER",""));
        		}else{
        			UserForegiftDs.getData(i).put("SERIAL_NUMBER", "无主押金");
        		}
        	}
        }
        params.put("USERFOREGIFT", UserForegiftDs);

        IData commonInfo = getInvoiceTag(userId);// 获取是否要输入发票的标志和长途漫游级别

        String BALANCE_rs = "0";// 用户不能清退

        boolean hfqtTag = true;//StaffPrivUtil.getFieldPrivClass(this.getVisit().getStaffId(), "HFQT_PRV") != null;

        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);// UserInfoQry.getUserInfosByUserId(userId);

        if (IDataUtil.isNotEmpty(userInfo) && "0".equals(userInfo.getString("REMOVE_TAG")))
        {
            if (queryRedCollectionInfo(sn)// 红名单或托收用户
                    || (queryBalanceCredit(sn, userId) > 0) // 信用度+实时结余>0
                    || queryVip(userId)// 是否为金、钻用户
                    || hfqtTag)
            {
                BALANCE_rs = "1";
            }
        }
        else
        {// 非正常用户，判断实时结余，大于等于零给退

            if (Integer.parseInt(getUserBalance(userId)) >= 0)
            {
                BALANCE_rs = "1";
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户有欠款没有缴清，请缴清后再办理！");
            }
        }
        params.put("BALANCE_RS", BALANCE_rs);

        commonInfo.putAll(getForegiftlimitDs());

        if (hfqtTag)
        {
            commonInfo.put("HFQT_PRV", "1");
        }
        else
        {
            commonInfo.put("HFQT_PRV", "0"); 
        }

        qryForgiftConfig(commonInfo);

        params.put("COMMON_INFO", commonInfo);

        results.add(params);
        return results;
    }

    public String getUserBalance(String userId) throws Exception
    {

        String userBalance = "0"; // 用户余额（实时结余）

        IData ownFeeDat = AcctCall.getOweFeeByUserId(userId);

        if (IDataUtil.isNotEmpty(ownFeeDat))
        {

            userBalance = ownFeeDat.getString("ACCT_BALANCE");
        }
        else
        {
            // String reinfo = "701014" + ownFeeDat.getString("X_RESULTINFO");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "701014:未查询到用户结余!");
        }

        return userBalance;
    }

    /**
     * 查询用户余额+信用度值
     * 
     * @param serial_number
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public int queryBalanceCredit(String serial_number, String userId) throws Exception
    {

        String userBalance = "0"; // 用户余额（实时结余）
        String userCredit = "0"; // 信用度值

        IDataset dataset = AcctCall.getUserCreditInfos("0", userId);

        if (dataset.size() > 0)
        {
            userCredit = dataset.getData(0).getString("CREDIT_VALUE", "0");
        }

        userBalance = getUserBalance(userId);
        return Integer.parseInt(userBalance) + Integer.parseInt(userCredit);

    }

    /**
     * 查询是否为红名单、托收用户
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public boolean queryRedCollectionInfo(String serialNumber) throws Exception
    {

        IDataset dataset = AcctInfoQry.qryAcctInfoBySn(serialNumber);

        String payModeCode = "";

        if (dataset.size() > 0)
        {
            payModeCode = dataset.getData(0).getString("PAY_MODE_CODE", "");
        }

        if (null != payModeCode && payModeCode.equals("1"))
        {// '1'代表托收
            return true;
        }

        return queryRedUser(serialNumber);

    }

    public boolean queryRedUser(String serialNumber) throws Exception
    {

        IDataset dataset = ForeGiftInfoQry.queryRedUser(serialNumber);

        if (dataset.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 查询是否为金、钻用户
     * 
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public boolean queryVip(String userId) throws Exception
    {

        IDataset dataset = CustVipInfoQry.getCustVipByUserId(userId, "0");

        if (null == dataset || dataset.size() == 0) // 没查到
        {
            return false;
        }

        for (int i = 0; i < dataset.size(); i++)
        {
            if (dataset.getData(i).getString("VIP_TYPE_CODE").equals("0"))
            {
                if (dataset.getData(i).getString("VIP_CLASS_ID").equals("3") || dataset.getData(i).getString("VIP_CLASS_ID").equals("4"))
                {
                    return true;
                }
            }
            if (dataset.getData(i).getString("VIP_TYPE_CODE").equals("2"))
            {
                if (dataset.getData(i).getString("VIP_CLASS_ID").equals("3") || dataset.getData(i).getString("VIP_CLASS_ID").equals("4"))
                {
                    return true;
                }
            }
            if (dataset.getData(i).getString("VIP_TYPE_CODE").equals("8"))
            {
                if (dataset.getData(i).getString("VIP_CLASS_ID").equals("2") || dataset.getData(i).getString("VIP_CLASS_ID").equals("1"))
                {
                    return true;
                }
            }
        }
        return false;

	}
	
	/**
	 * REQ201610110009_押金业务界面增加判断拦截
	 * @author zhuoyingzhi
	 * 20161108
	 * <br/>
	 * 判断结束时间与系统当前时间的大小
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public boolean  sysDateCompareToEndDate(String endDate) throws Exception{
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			String sys=SysDateMgr.getSysTime();
	       
			Calendar sysTime = Calendar.getInstance();  
		    Calendar endDateTime = Calendar.getInstance(); 
		    
		    sysTime.setTime(df.parse(sys));  
		    endDateTime.setTime(df.parse(endDate.trim()));  
		    //利用Calendar的方法比较大小  
	        if (endDateTime.compareTo(sysTime) <= 0) {
	        	//结束时间小于等于系统当前时间,拦截
	            return true;
	        }
			return false;
		} catch (Exception e) {
		     //log.info("(e.getMessage());
		     throw e;
		}
	}
	/**
	 * REQ201610110009_押金业务界面增加判断拦截
	 * @author zhuoyingzhi
	 * 20161116
	 * <br/>
	 * 比较tf_f_user_foregift表（发票资料表）和tf_f_user_otherserv （发票记录表）的发票金额，
	 * 如果tf_f_user_otherserv表的发票金额大于tf_f_user_foregift表的发票金额，
	 * 应弹出拦截，拦截信息为：数据异常，请和业务支撑部联系修改数据后再给用户清退
	 * @param userOtherservUserId
	 * @param money
	 * @return
	 * @throws Exception
	 */
	public  boolean isForeGiftMoney(String userOtherservUserId,double money,String foregiftCode)throws Exception{
		try {
			 IDataset  userForegift=UserForegiftInfoQry.getUserForegift(userOtherservUserId,foregiftCode);
			 if(IDataUtil.isNotEmpty(userForegift)){
				//发票资料表 中的押金金额
				String  userForeMoney=userForegift.getData(0).getString("MONEY");
				if(userForeMoney !=null&&!"".equals(userForeMoney)){
					  double  userForeMoney1=Double.valueOf(userForeMoney);
					  if(money > userForeMoney1){
						  return true;
					  }
				}else{
					//资料表中  无费用  拦截
					return true;
				}
			 }else{
				 //TF_F_USER_FOREGIFT 不存在  拦截
				 return true;
			 }
			return false;
		} catch (Exception e) {
		     //log.info("(e.getMessage());
		     throw e;
		}
		
	}

    /**
     * 供生产环境配置修改
     * @param params
     * @throws Exception
     */
	private void qryForgiftConfig(IData params) throws Exception{
        //4A金库认证开关
        String foreGift4ATag = StaticUtil.getStaticValue("FOREGIFT_STATIC" , "FOREGIFT_4A_TAG");
        params.put("FOREGIFT_4A_TAG", StringUtils.isBlank(foreGift4ATag)?"0":foreGift4ATag);
        //4A金库认证押金类型
        String foreGiftType = StaticUtil.getStaticValue("FOREGIFT_STATIC" , "FOREGIFT_4A_TYPE");
        params.put("FOREGIFT_4A_TYPE", StringUtils.isBlank(foreGiftType)?"1|4|9|10|13|14":foreGiftType);
        //押金收据备注
        String foreGiftRemark = StaticUtil.getStaticValue("FOREGIFT_STATIC" , "FOREGIFT_REMARK");
        params.put("FOREGIFT_REMARK", StringUtils.isBlank(foreGiftRemark)?"押金收据仅作为收取凭证，不作为退款主要依据。":foreGiftRemark);

    }

}
