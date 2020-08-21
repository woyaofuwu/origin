package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

import java.net.URL;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;
import org.apache.axis.client.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.org.apache.commons.lang3.time.FastDateFormat;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.exception.TradeNpException;
import com.asiainfo.veris.crm.order.pub.exception.WapException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPayModeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UUserTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContactInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.dreamnet.WithinSetDreamNetQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.mphoneaddress.GetMphoneAddressQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BlackUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CreateRedMemberQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MofficeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SmsQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.StaffOperTypeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.RuleCfgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.WapSessionInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.SpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.QryScoreInfo;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.twocheck.TwoCheckInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.RedMemberDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCheck4BuyIstead;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserForegiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherservQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.auth.TradeInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.UserPasswordInfoComm;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo.CreateRedMemberBean;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardCheckBean;
import com.asiainfo.veris.crm.order.soa.person.busi.userident.UserIdentBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryInfoUtil;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSInputData;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSServNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSVarNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.NeaSoapBindingStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.SoapInputXml;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOPStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOP_ServiceLocator;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.holders.CWSVarNodeHolder;

public class QueryInfoBean extends CSBizBean
{
	private static transient final Logger logger = Logger.getLogger(QueryInfoBean.class);

	/* 号码归属运营商编码 1-移动 2-联通 3-电信 */
	private static final String ASP_YD = "1";

	private static final String ASP_LT = "2";

	private static final String ASP_DX = "3";

	/* 0查询用户产品下的元素, 1查询用户订购的用户产品下的元素, 2查询用户尚未订购的用户产品下的元素 */
	private static final String QUERY_TAG_ALL_ELEM_IN_PROD = "0";

	private static final String QUERY_TAG_ALL_ELEM_IN_UPROD = "1";

	private static final String QUERY_TAG_ALL_ELEM_NOTIN_UPROD = "2";

	/* 产品包元素类型 D优惠 , S服务 */
	private static final String ELEM_TYPE_D = "D";

	private static final String ELEM_TYPE_S = "S";

	/* 客户实名制标志 1 是, 0 否 */
	private static final String CUST_REALNAME_YES = "1";

	private static final String CUST_REALNAME_NO = "0";

	/*
	 * 0 根据证件号码获取个人客户资料 1 根据客户标识获取个人客户资料 2 根据客户姓名获取个人客户资料 3 根据集团编码获取集团客户资料 4
	 * 根据证件号码获取集团客户资料 5 根据集团客户名称获取集团客户资料 6 输入服务号码取所有用户 7 输入服务号码取所有非正常用户 8
	 * 输入服务号码取正常用户 9 根据用户标识获取个人客户资料
	 */
	private static final String X_GETMODE_GETPERINFO_BY_PSPT = "0";

	private static final String X_GETMODE_GETPERINFO_BY_CUSTID = "1";

	private static final String X_GETMODE_GETPERINFO_BY_CUSTNAME = "2";

	private static final String X_GETMODE_GETGRPINFO_BY_GROUPID = "3";

	private static final String X_GETMODE_GETGRPINFO_BY_PSPT = "4";

	private static final String X_GETMODE_GETGRPINFO_BY_CUSTNAME = "5";

	private static final String X_GETMODE_CUSTINFO_BY_SN_ALL = "6";

	private static final String X_GETMODE_CUSTINFO_BY_SN_ABNORMAL = "7";

	private static final String X_GETMODE_CUSTINFO_BY_SN_NORMAL = "8";

	private static final String X_GETMODE_CUSTINFO_BY_USERID = "9";

	/* 客户类型： 0 个人客户 1 集团客户 */
	private static final String CUST_TYPE_PER = "0";

	private static final String CUST_TYPE_GRP = "1";

	/* 字符常量 */
	private static final String S_E_DYH = "'";

	private static final String S_E_DH = ",";

	/* 验证码 有效性 01 有效 02 临时 */
	private static final String IDENT_CODE_TYPE_EFF = "01";

	private static final String IDENT_CODE_TYPE_TMP = "02";

	/**
	 * 解密算法
	 * 
	 * @param password
	 * @param key
	 * @return
	 */
	public static String decrypt(String password)
	{
		String key = "ailk";
		String tmp = "";
		for (int i = 0; i < key.length(); i++)
		{
			tmp += (int) key.charAt(i);
		}
		int ifloor = (int) Math.floor(tmp.length() / 5);
		String mult = "" + tmp.charAt(ifloor) + tmp.charAt(ifloor * 2) + tmp.charAt(ifloor * 3) + tmp.charAt(ifloor * 4) + tmp.charAt(ifloor * 5);

		int iceil = (int) Math.ceil(key.length() / 2);

		int ipow = (int) (Math.pow(2, 31) - 1);
		long irandom = Long.parseLong(password.substring(password.length() - 8, password.length()), 16);
		password = password.substring(0, password.length() - 8);
		tmp += irandom;
		while (tmp.length() > 10)
		{
			tmp = (Long.parseLong(tmp.substring(0, 10)) + Long.parseLong(tmp.substring(10, tmp.length()))) + "";
		}
		tmp = (Long.parseLong(tmp) * Long.parseLong(mult) + iceil) % ipow + "";
		int chr = 0;
		String out = "";
		for (int i = 0; i < password.length(); i += 2)
		{
			chr = Integer.parseInt(password.substring(i, i + 2), 16) ^ (int) Math.floor((Double.parseDouble(tmp) / ipow) * 255);
			out += (char) chr;
			tmp = (Long.parseLong(mult) * Long.parseLong(tmp) + iceil) % ipow + "";
		}
		return out;
	}

	private final String X_GETMODE_BY_SN_NORMAL = "0"; // 输入服务号码取正常用户

	private final String X_GETMODE_BY_SN_ALL = "1"; // 输入服务号码取所有用户

	private final String X_GETMODE_BY_SN_ABNORMAL = "2"; // 输入服务号码取所有非正常用户

	private final String X_GETMODE_BY_SN_LAST_ABNORMAL = "3"; // 输入服务号码取最后销户用户

	private String IBOSS_PREX = "IBOSS_";

	public static String CHECK_OK = "0";// OK

	public static String VIP_CARD_ERROR = "0976";// vip卡号错误

	public static String ID_CARD_ERROR = "0976";// 客户身份证号码

	public static String PASSWORD_ERROR = "0901";// 客户密码错误

	public static String GRADE_NOT_ON_LEVEL = "0975";// 客户级别不够

	public static String SCORE_NOT_ENOUGH = "0974";// 客户积分不够

	public static String UNKNOWN_ERROR = "0999";// 其它错误

	public static String GROUP_CUST_USE_VIP = "0978";// 集团客户，无个人身份信息，请用VIP卡号确认

	public static String CUST_STATE_UNNOMARL = "0977";// 客户状态不正常

	public boolean checkConfigRule(String flag, String eparchyCode, String serviceId, String productId, String brandCode, String userId) throws Exception
	{
		boolean openFlag = false;
		boolean productFlag = false;
		boolean brandFlag = false;
		boolean discntFlag = false;
		boolean packageFlag = true;

		productFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, productId, "P", eparchyCode);
		brandFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, brandCode, "B", eparchyCode);
		IDataset discntInfos = UserDiscntInfoQry.qryUserDiscntByUserId(userId, null);
		if (IDataUtil.isNotEmpty(discntInfos))
		{
			for (int i = 0, size = discntInfos.size(); i < size; i++)
			{
				IData discntInfo = discntInfos.getData(i);
				discntFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, discntInfo.getString("DISCNT_CODE"), "D", eparchyCode);
			}
		} else
		{
			discntFlag = true;
		}

		// 生效的订购关系
		if ("00".equals(flag))
		{
			IDataset saleActiveInfos = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
			if (IDataUtil.isNotEmpty(saleActiveInfos))
			{
				for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
				{
					IData saleActiveInfo = saleActiveInfos.getData(i);
					packageFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, saleActiveInfo.getString("PACKAGE_ID"), "K", eparchyCode);
				}
			} else
			{
				packageFlag = true;
			}

		} else
		{
			packageFlag = true;
		}

		if (productFlag && brandFlag && discntFlag && packageFlag)
		{
			openFlag = true;
		}

		return openFlag;
	}

	public boolean checkServiceConfigRule(String flag, String eparchyCode, String serviceId, String productId, String brandCode, String userId) throws Exception
	{
		boolean openFlag = false;
		boolean productFlag = false;
		boolean brandFlag = false;
		boolean discntFlag = false;
		boolean packageFlag = true;
		boolean svcFlag = true;

		productFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, productId, "P", eparchyCode);
		brandFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, brandCode, "B", eparchyCode);
		IDataset discntInfos = UserDiscntInfoQry.qryUserDiscntByUserId(userId, null);
		if (IDataUtil.isNotEmpty(discntInfos))
		{
			for (int i = 0, size = discntInfos.size(); i < size; i++)
			{
				IData discntInfo = discntInfos.getData(i);
				discntFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, discntInfo.getString("DISCNT_CODE"), "D", eparchyCode);
			}
		} else
		{
			discntFlag = true;
		}

		// 生效的订购关系
		if ("00".equals(flag))
		{
			IDataset saleActiveInfos = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
			if (IDataUtil.isNotEmpty(saleActiveInfos))
			{
				for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
				{
					IData saleActiveInfo = saleActiveInfos.getData(i);
					packageFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, saleActiveInfo.getString("PACKAGE_ID"), "K", eparchyCode);
				}
			} else
			{
				packageFlag = true;
			}

			svcFlag = RuleCfgInfoQry.getRuleConfigInfo(serviceId, serviceId, "S", eparchyCode);

		} else
		{
			packageFlag = true;
			svcFlag = true;
		}

		if (productFlag && brandFlag && discntFlag && packageFlag && svcFlag)
		{
			openFlag = true;
		}

		return openFlag;
	}

	/**
	 * 检查白名单用户存在
	 * 
	 * @param IData
	 * @return false为不存在，true为存在
	 * @throws Exception
	 * @author huanghui
	 */
	private boolean checkRedMemberIsExists(IData input) throws Exception
	{
		boolean result = false;
		IData inParam = new DataMap();
		inParam.clear();
		inParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		IDataset isHave = BadnessInfoQry.checkRedMemberIsExists(inParam);
		if (isHave.size() == 0 || IDataUtil.isEmpty(isHave))
		{
			return result;
		} else
		{
			result = true;
			return result;
		}

	}

	public IData checkUserPoorPWD(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0");
		result.put("X_CHECK_INFO", "0");
		result.put("X_RESULTINFO", "Trade Ok!");

		String psw = input.getString("USER_PASSWD", "");

		if (StringUtils.isBlank(psw))
		{
			result.put("X_RESULTCODE", "450000");
			result.put("X_CHECK_INFO", "1");
			result.put("X_RESULTINFO", "用户服务密码不存在");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");

			return result;
		}

		if ("1".equals(input.getString("X_CNVTAG", "")))
		{
			if (null != psw)
			{
				psw = decrypt(psw);
				input.put("USER_PASSWD", psw);
			}
		}

		if (psw.trim().length() != 6)// 密码长度不正确
		{
			result.put("X_RESULTCODE", "450006");
			result.put("X_CHECK_INFO", "3");
			result.put("X_RESULTINFO", "密码长度不正确");
			return result;
		}

		String serialNumber = input.getString("SERIAL_NUMBER");
		// 获取三户资料
		IDataset datasetUca = this.getUCAInfobySn(serialNumber);
		IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
		String psptId = custInfo.getString("PSPT_ID", "");

		// --------------------弱密码校验---BEGIN------------
		if (InterfaceUtil.checkSerieisNumber(psw))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "4");
			result.put("X_RESULTINFO", "6个连续数字不能作为服务密码，请重新输入!");
			return result;
		}
		if (InterfaceUtil.checkRepeatNumber(psw))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "5");
			result.put("X_RESULTINFO", "6个重复数字不能作为服务密码，请重新输入!");
			return result;
		}
		if (InterfaceUtil.checkContainInPspt(psw, psptId))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "7");
			result.put("X_RESULTINFO", "证件号码的连续6位数字不能作为服务密码，请重新输入!");
			return result;
		}
		if (InterfaceUtil.checkContainInSn(psw, serialNumber))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "8");
			result.put("X_RESULTINFO", "手机号码中的连续6位数字不能作为服务密码，请重新输入!");
			return result;
		}
		if (InterfaceUtil.checkContainInSnQh3Bit(psw, serialNumber))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "9");
			result.put("X_RESULTINFO", "手机号码中前三位+后三位或后三位+前三位的组合不能作为服务密码!");
			return result;

		}
		if (InterfaceUtil.checkPasswdRepeatBitNum(psw))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "10");
			result.put("X_RESULTINFO", "密码的不同号码数小于等于3 不能作为服务密码，请重新输入!");
			return result;

		}
		if (InterfaceUtil.checkPasswsQh3Bit(psw))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "11");
			result.put("X_RESULTINFO", "密码的前后三位一致,不能作为服务密码，请重新输入!");
			return result;

		}
		if (InterfaceUtil.checkPasswsAllJO(psw))
		{
			result.put("X_RESULTCODE", "0");
			result.put("X_CHECK_INFO", "12");
			result.put("X_RESULTINFO", "密码全为偶数或奇数,不能作为服务密码，请重新输入!");
			return result;

		}
		return result;
	}

	public IData CheckVipAirPortRight(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData data = convetData(input, true);
		IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(userInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = userInfos.getString("USER_ID");
		String productId = userInfos.getString("PRODUCT_ID");
		String userScore = queryUserScore(userId);
		boolean checkOk = false;

		// 获取已用服务总数
		IDataset templist = UserOtherInfoQry.getUserOther(userId, "AREM");
		int servdNum = 0;
		if (IDataUtil.isNotEmpty(templist))
		{
			servdNum = templist.getData(0).getInt("RSRV_STR1", 0);
		}

		IData checkdata = new DataMap();
		String custId = userInfos.getString("CUST_ID");
		IData custInfo = UcaInfoQry.qryCustInfoByCustId(custId);

		if (IDataUtil.isEmpty(custInfo))
		{
			CSAppException.apperr(CustException.CRM_CUST_105, serialNumber);
		}

		// 获取客户名称
		String custName = custInfo.getString("CUST_NAME");

		IDataset vipInfos = CustVipInfoQry.qryVipInfoByUserId(userId);

		String vipTypeCode = "";
		String vipClassId = "";
		String vipno = "IN_ERROR_VIPNO";
		if (IDataUtil.isNotEmpty(vipInfos))
		{
			IData vipInfo = vipInfos.getData(0);
			vipTypeCode = vipInfo.getString("VIP_TYPE_CODE", "");
			vipClassId = vipInfo.getString("VIP_CLASS_ID", "");
			vipno = vipInfo.getString("VIP_CARD_NO");
		}

		// 校验证件，不返回客户资料
		String inPsptTypeCode = data.getString("IDCARDTYPE", "IN_ERROR_IDTYPE");// 证件类型
		String inpsptId = data.getString("IDCARDNUM", "IN_ERROR_IDNUM");// 证件号码
		String psptId = custInfo.getString("PSPT_ID", "DEFAULT_ERROR_IDTYPE");
		String psptTypeCode = custInfo.getString("PSPT_TYPE_CODE", "DEFAULT_ERROR_IDNUM");

		// 公共的返回内容
		String idCardType = data.getString("IDCARDTYPE");
		String idCardNum = data.getString("IDCARDNUM");

		if (StringUtils.isNotBlank(idCardType))
		{
			checkdata.put(getIBossString("IDCARDTYPE"), idCardType);
		}

		if (StringUtils.isNotBlank(idCardNum))
		{
			checkdata.put(getIBossString("IDCARDNUM"), idCardNum);
		}

		// 用户状态判断
		String userStateCodeset = userInfos.getString("USER_STATE_CODESET", "");
		if (!InterfaceUtil.VALIDSTATE_MAP.containsKey(userStateCodeset))
		{

			checkdata.put("X_RESULTCODE", CUST_STATE_UNNOMARL);
			checkdata.put("NAME", custInfo.getString("CUST_NAME"));
			checkdata.put("USERSCORE", 0);
			checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
			checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
			checkdata.put("RSRV_STR19", 0);
			checkdata.put("X_RSPTYPE", "2");
			checkdata.put("X_RSPCODE", "2998");
			return checkdata;
		}

		String checkRCode = UNKNOWN_ERROR;
		String checkRInfo = "";

		// 先校验客服密码，正确的话则不需要校验证件号
		if (!checkOk)
		{
			String inpass = data.getString("USER_PASSWD", "");// 密码校验，当输入了密码则校验，不返回客户资料
			if (inpass.length() > 0)
			{
				boolean checkpwdok = false;
				boolean flag = UserInfoQry.checkUserPassword(userId, inpass);

				if (flag)
					checkpwdok = true;

				if (!checkpwdok)
				{
					checkRCode = PASSWORD_ERROR;
					checkRInfo = "密码错误";
				} else
				{
					checkOk = true;
				}

			}
		}

		// 如果客服密码不正确则校验证件号
		if (!checkOk && inPsptTypeCode.trim().length() >= 0 && inpsptId.trim().length() >= 0)
		{
			// 此处添加集团客户判断
			IData groupInfo = UcaInfoQry.qryGrpInfoByCustId(custId);

			if (IDataUtil.isNotEmpty(groupInfo) && !inPsptTypeCode.equalsIgnoreCase("1"))
			{
				checkRCode = GROUP_CUST_USE_VIP;
				checkRInfo = "集团客户用vip卡校验";
			} else if (inPsptTypeCode.equalsIgnoreCase("1"))// vip卡号校验
			{
				if (!vipno.equalsIgnoreCase(inpsptId))
				{
					checkRCode = VIP_CARD_ERROR;
					checkRInfo += " vip卡号码错误";
				} else
				{
					checkOk = true;
				}
			} else if (inPsptTypeCode.equalsIgnoreCase("0") || inPsptTypeCode.equalsIgnoreCase("Z"))// 证件校验
			{
				// 如果是身份证，则要特殊处理15位身份证和18位身份证转换对比的问题
				if (inPsptTypeCode.equalsIgnoreCase("0") && !InterfaceUtil.comparePsptId(psptId, inpsptId))
				{
					checkRCode = ID_CARD_ERROR;
					checkRInfo = "客户身份证号码错误";
				} else if (!inPsptTypeCode.equalsIgnoreCase("0") && !psptId.equalsIgnoreCase(inpsptId))// 证件校验
				{
					checkRCode = ID_CARD_ERROR;
					checkRInfo = "客户证件号码错误";
				} else
				{
					checkOk = true;
				}
			}

		}

		if (!checkOk)
		{
			checkdata.put("X_RESULTCODE", checkRCode);
			checkdata.put("X_RESULTINFO", checkRInfo);
			checkdata.put("X_RSPTYPE", "2");
			checkdata.put("X_RSPCODE", "2998");
			return checkdata;
		}

		// 通过VIP信息、服务级别、随从人数计算代扣积分和免费次数
		String svcLevel = data.getString("SVC_LEVEL", "");
		int inFreeNumToPay = data.getInt("ATTENDANTS", 0) + 1;
		int servScoreUnit = 1000;
		int servValueUnit = 5000;

		// 积分和费用调整
		if (svcLevel.equalsIgnoreCase("1"))
		{
			servScoreUnit = 4000;
			servValueUnit = 8000; // 单位（分）
		} else if (svcLevel.equalsIgnoreCase("2"))
		{
			servScoreUnit = 8000;
			servValueUnit = 15000;
		} else if (svcLevel.equalsIgnoreCase("3"))
		{
			servScoreUnit = 8000;
			servValueUnit = 15000;
		} else if (svcLevel.equalsIgnoreCase("4"))
		{
			servScoreUnit = 8000;
			servValueUnit = 15000;
		}

		int maxFreeSerNo = getTotalFreeCount(vipTypeCode, vipClassId, productId);

		/*-------------------------------------  重要客户剩余次数处理  -----------------------------------*/
		// 剩余免费次数
		int leftServNum = maxFreeSerNo - servdNum;
		leftServNum = leftServNum > 0 ? leftServNum : 0;

		// 校验客户级别,查验客户全球通VIP身份、VIP级别，如客户不是VIP客户或客户级别低于金卡级别，则拒绝为该客户提供服务。
		if ((!"0".equalsIgnoreCase(vipTypeCode) && !"2".equalsIgnoreCase(vipTypeCode) && !"5".equalsIgnoreCase(vipTypeCode)) || (!"2".equalsIgnoreCase(vipClassId) && !"3".equalsIgnoreCase(vipClassId) && !"4".equalsIgnoreCase(vipClassId)))
		{
			checkdata.put("X_RESULTCODE", GRADE_NOT_ON_LEVEL);
			checkdata.put("X_RESULTINFO", "客户级别不够");
			checkdata.put("NAME", custName);
			checkdata.put("USERSCORE", userScore);
			checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
			checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
			checkdata.put("RSRV_STR19", leftServNum);
			checkdata.put("X_RSPTYPE", "2");
			checkdata.put("X_RSPCODE", "2998");
			return checkdata;
		}

		/*-------------------------------------- 积分和剩余免费次数处理  ---------------------------------------*/
		// 如果剩余次数足够则扣次数，否则扣积分
		if (leftServNum > inFreeNumToPay)// 如果剩余次数还够
		{
			checkdata.put("X_RESULTCODE", CHECK_OK);
			checkdata.put("NAME", custName);
			checkdata.put("USERSCORE", userScore);
			checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
			checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
			checkdata.put("RSRV_STR19", leftServNum);
			return checkdata;
		} else
		// 否则剩余次数不够或者正好扣完
		{
			int scoreNum = inFreeNumToPay - leftServNum;// 待转为扣分的人数
			int scoreToPay = servScoreUnit * scoreNum;// 待扣积分数
			int valueToPay = servValueUnit * scoreNum;// 待扣金额数
			if (Integer.valueOf(userScore) < scoreToPay)
			{
				checkdata.put("X_RESULTCODE", SCORE_NOT_ENOUGH);
				checkdata.put("X_RESULTINFO", "客户积分不够");
				checkdata.put("NAME", custName);
				checkdata.put("USERSCORE", userScore);
				checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
				checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
				checkdata.put("RSRV_STR19", leftServNum);
				checkdata.put("X_RSPTYPE", "2");
				checkdata.put("X_RSPCODE", "2998");
				return checkdata;
			} else
			{
				checkdata.put("X_RESULTCODE", CHECK_OK);
				checkdata.put("NAME", custName);
				checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
				checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
				checkdata.put("USERSCORE", userScore);
				checkdata.put("RSRV_STR19", leftServNum);
				checkdata.put("X_RSPTYPE", "2");
				checkdata.put("X_RSPCODE", "2998");
				return checkdata;
			}
		}

	}

	public IData CheckVipRailWayRight(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "SVC_LEVEL");
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);

		IData checkdata = new DataMap();
		if (IDataUtil.isEmpty(userInfos))
		{
			checkdata.put("DESC", "05");// 密码错误
			checkdata.put("X_RESULTCODE", "0904");
			checkdata.put("X_RESULTINFO", "无此用户");
			checkdata.put("SERIAL_NUMBER", serialNumber);
			checkdata.put("X_RSPTYPE", "2");
			checkdata.put("X_RSPCODE", "2998");
			checkdata.put("X_RSPDESC", "无此用户");
			return checkdata;
		}

		String custId = userInfos.getString("CUST_ID");
		IData custInfos = UcaInfoQry.qryCustInfoByCustId(custId);
		if (IDataUtil.isEmpty(custInfos))
		{
			checkdata.put("DESC", "05");// 密码错误
			checkdata.put("X_RESULTCODE", "0904");
			checkdata.put("X_RESULTINFO", "无此用户");
			checkdata.put("SERIAL_NUMBER", serialNumber);
			checkdata.put("X_RSPTYPE", "2");
			checkdata.put("X_RSPCODE", "2998");
			checkdata.put("X_RSPDESC", "无此用户");
			return checkdata;
		}

		String userId = userInfos.getString("USER_ID");
		IDataset vipInfos = CustVipInfoQry.qryVipInfoByUserId(userId);
		IData vipInfo = new DataMap();
		if (IDataUtil.isNotEmpty(vipInfos))
		{
			vipInfo = vipInfos.getData(0);
		}
		// 校验证件，不返回客户资料
		String inPsptTypeCode = input.getString("IDCARDTYPE", "");// 证件类型
		String inpsptId = input.getString("IDCARDNUM", "");// 证件号码
		String psptId = custInfos.getString("PSPT_ID", "");
		String inpass = input.getString("USER_PASSWD", "");// 密码校验

		// 公共的返回内容
		String idCardType = input.getString("IDCARDTYPE");
		String idCardNum = input.getString("IDCARDNUM");
		if (StringUtils.isNotBlank(idCardType))
		{
			checkdata.put("IDCARDTYPE", idCardType);
		}

		if (StringUtils.isNotBlank(idCardNum))
		{
			checkdata.put("IDCARDNUM", idCardNum);
		}
		// 获取积分信息
		int totalScore = Integer.parseInt(queryUserScore(userId)); // 原有积分

		// 用户状态判断
		String userStateCodeset = userInfos.getString("USER_STATE_CODESET", "");
		String vipTypeCode = vipInfo.getString("VIP_TYPE_CODE", "");
		String vipClassId = vipInfo.getString("VIP_CLASS_ID", "");

		if (!"0".equalsIgnoreCase(userStateCodeset))
		{
			checkdata.put("DESC", "06");// 客户状态不正常
			checkdata.put("X_RESULTCODE", "0977");
			checkdata.put("X_RESULTINFO", "客户状态不正常，无法提供服务");
			checkdata.put("SERIAL_NUMBER", serialNumber);
			checkdata.put("NAME", custInfos.getString("CUST_NAME"));
			checkdata.put("USERSCORE", totalScore);
			checkdata.put("IDCARDTYPE", idCardType);
			checkdata.put("IDCARDNUM", idCardNum);
			checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
			checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
			checkdata.put("X_RSPTYPE", "2");
			checkdata.put("X_RSPCODE", "2998");
			checkdata.put("X_RSPDESC", "客户状态不正常，无法提供服务");
			return checkdata;
		}

		// 先判断密码是否有值，有则校验密码是否正确，没有则校验证件信息是否正确
		if (StringUtils.isNotBlank(inpass))
		{
			// boolean flag = UserInfoQry.checkUserPassword(userId, inpass);
			boolean flag = PasswdMgr.checkUserPassword(inpass, userId, userInfos.getString("USER_PASSWD"));

			if (!flag)
			{
				checkdata.put("DESC", "03");// 密码错误
				checkdata.put("X_RESULTCODE", "0901");
				checkdata.put("X_RESULTINFO", "客户密码错误");
				checkdata.put("SERIAL_NUMBER", serialNumber);
				checkdata.put("X_RSPTYPE", "2");
				checkdata.put("X_RSPCODE", "2998");
				checkdata.put("X_RSPDESC", "客户密码错误");
				return checkdata;
			}
		}
		// 校验证件号
		else if (StringUtils.isNotBlank(inPsptTypeCode) && StringUtils.isNotBlank(inpsptId))
		{
			// 此处添加集团客户判断
			inPsptTypeCode = InterfaceUtil.encodeIdType(inPsptTypeCode);
			IData groupInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
			if (IDataUtil.isNotEmpty(groupInfo) && !"1".equalsIgnoreCase(inPsptTypeCode))
			{
				checkdata.put("DESC", "07");// vip卡号码错误
				checkdata.put("X_RESULTINFO", "集团客户，无个人身份信息，请用VIP卡号确认");
				checkdata.put("X_RESULTCODE", "0978");
				checkdata.put("SERIAL_NUMBER", serialNumber);
				checkdata.put("X_RSPTYPE", "2");
				checkdata.put("X_RSPCODE", "2998");
				checkdata.put("X_RSPDESC", "集团客户，无个人身份信息，请用VIP卡号确认");
				return checkdata;
			} else if ("1".equalsIgnoreCase(inPsptTypeCode))
			{// vip卡号校验
				String vipno = vipInfo.getString("VIP_CARD_NO", "IN_ERROR_VIPNO");
				if (!vipno.equalsIgnoreCase(inpsptId))
				{
					checkdata.put("DESC", "04");// vip卡号码错误
					checkdata.put("X_RESULTINFO", "客户身份证号码或vip卡号错误");
					checkdata.put("X_RESULTCODE", "0976");
					checkdata.put("SERIAL_NUMBER", serialNumber);
					checkdata.put("X_RSPTYPE", "2");
					checkdata.put("X_RSPCODE", "2998");
					checkdata.put("X_RSPDESC", "vip卡号码错误");
					return checkdata;
				}
			} else if ("0".equalsIgnoreCase(inPsptTypeCode) || "Z".equalsIgnoreCase(inPsptTypeCode))
			{// 证件校验
				if (!psptId.equalsIgnoreCase(inpsptId))
				{// 证件校验
					checkdata.put("DESC", "04");// 客户身份证号码错误
					checkdata.put("X_RESULTCODE", "0976");
					checkdata.put("X_RESULTINFO", "客户身份证号码或vip卡号错误");
					checkdata.put("SERIAL_NUMBER", serialNumber);
					checkdata.put("X_RSPTYPE", "2");
					checkdata.put("X_RSPCODE", "2998");
					checkdata.put("X_RSPDESC", "客户身份证号码或vip卡号错误");
					return checkdata;
				}
			}
		}

		/*-------------------------------------- 客户级别  ---------------------------------------*/
		// 校验客户级别,查验客户全球通VIP身份、VIP级别，如客户不是VIP客户或客户级别低于银卡级别，则拒绝为该客户提供服务。
		if (!"4".equals(vipClassId) && !"2".equals(vipClassId) && !"3".equals(vipClassId))
		{
			checkdata.put("DESC", "02");// 客户级别不够
			checkdata.put("X_RESULTCODE", "0975");
			checkdata.put("X_RESULTINFO", "客户级别不够");
			checkdata.put("SERIAL_NUMBER", serialNumber);
			checkdata.put("NAME", custInfos.getString("CUST_NAME"));
			checkdata.put("USERSCORE", totalScore);
			checkdata.put("IDCARDTYPE", idCardType);
			checkdata.put("IDCARDNUM", idCardNum);
			checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
			checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
			checkdata.put("X_RSPTYPE", "2");
			checkdata.put("X_RSPCODE", "2998");
			checkdata.put("X_RSPDESC", "客户级别不够");
			return checkdata;
		}

		// 通过VIP信息、服务级别、随从人数计算代扣积分
		String svcLevel = input.getString("SVC_LEVEL", "");
		// 查询用户的默认消费积分和随从人员
		IData paramInfo = this.getVipParamMsg(svcLevel);
		// 获取服务积分
		IDataset templist = UserOtherInfoQry.getUserOther(userId, "FWJF");
		int svcScore = 0;
		if (IDataUtil.isNotEmpty(templist))
		{
			svcScore = templist.getData(0).getInt("RSRV_STR1", 0);
		}
		int reduceScoreUnit = paramInfo.getInt("REDUCE_SCORE");// 需要扣减的积分数
		int follow_num = input.getInt("ATTENDANTS"); // 随行人数
		int thisSvcCount = (follow_num + 1); // 本次服务总人数

		/*-------------------------------------- 积分  ---------------------------------------*/
		// 先判断是否存在服务积分，如果不存在则扣减实际积分
		int serviceAllScore = reduceScoreUnit * thisSvcCount; // 本次服务需要扣的总积分
		int restSvcScore = svcScore - serviceAllScore;
		if (restSvcScore > 0)
		{
			// 代表有足够的服务积分
		} else
		{
			if (totalScore - (serviceAllScore - svcScore) > 0)
			{
				// 代表有足够的实际积分
			} else
			{
				checkdata.put("DESC", "01");// 积分不够
				checkdata.put("X_RESULTCODE", "0974");
				checkdata.put("X_RESULTINFO", "客户积分不够");
				checkdata.put("SERIAL_NUMBER", serialNumber);
				checkdata.put("NAME", custInfos.getString("CUST_NAME"));
				checkdata.put("USERSCORE", totalScore);
				checkdata.put("IDCARDTYPE", idCardType);
				checkdata.put("IDCARDNUM", idCardNum);
				checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
				checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
				checkdata.put("X_RSPTYPE", "2");
				checkdata.put("X_RSPCODE", "2998");
				checkdata.put("X_RSPDESC", "客户积分不够");
				return checkdata;
			}
		}

		// 成功返回信息
		checkdata.put("DESC", "00");// 正常
		checkdata.put("SERIAL_NUMBER", serialNumber);
		checkdata.put("NAME", custInfos.getString("CUST_NAME"));
		checkdata.put("USERSCORE", totalScore);
		checkdata.put("IDCARDTYPE", idCardType);
		checkdata.put("IDCARDNUM", idCardNum);
		checkdata.put("USERSTATUS", InterfaceUtil.getIBossUserState(userStateCodeset));
		checkdata.put("USERRANK", InterfaceUtil.getIBossUserRank(vipTypeCode, vipClassId));
		checkdata.put("X_RSPTYPE", "0");
		checkdata.put("X_RSPCODE", "0000");
		checkdata.put("X_RSPDESC", "成功");

		return checkdata;
	}

	/**
	 * update yangsh6
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset commQuery(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		rets = CommparaInfoQry.queryCommparaByParaCode(input);
		return rets;
	}

	/**
	 * 将YYYY-MM-DD HH:24MM:SS转换成YYYYMMDDHH24MMSS
	 * 
	 * @return
	 */
	private String convertDateFormat(String inDate)
	{
		if (StringUtils.isBlank(inDate))
			return "";

		String outDate = inDate.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		// 日期字符串有可能不带时分秒，需要补齐14位
		if (outDate.length() == 8)
		{
			outDate += "000000";
		}

		return outDate;
	}

	private String convertElementType2Mall(String eleType) throws Exception
	{
		String eleTypeMall = "";
		if (eleType.equals("D"))
			eleTypeMall = "01";// 套餐类
		else if (eleType.equals("Z"))
			eleTypeMall = "02";// 增值业务类
		else if (eleType.equals("S"))
			eleTypeMall = "03";// 服务功能类

		return eleTypeMall;
	}

	/**
	 * 转化IBOSS和CRM之间数据差异
	 * 
	 * @author zhuyu
	 * @param data
	 * @param flag
	 * @return data
	 */
	public IData convetData(IData data, boolean flag)
	{
		IData returndata = new DataMap();
		if (data != null)
		{
			Iterator it = data.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				if (((String) key).equalsIgnoreCase("IDCARDTYPE"))
				{
					if (flag)
						returndata.put("IDCARDTYPE", InterfaceUtil.getCrmPsptType((String) entry.getValue()));
					if (!flag)
						returndata.put("IDCARDTYPE", InterfaceUtil.getIBossPsptType((String) entry.getValue()));

				} else
					returndata.put((String) key, entry.getValue());
			}
		}
		return returndata;
	}

	/**
	 * 新增短信白名单检查
	 * 
	 * @param Idata
	 * @throws Exception
	 * @author huanghui
	 */
	private void createRedMemberCheck(IData param) throws Exception
	{
		String endTime = param.getString("END_TIME", "");
		if (StringUtils.isBlank(endTime))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1170);
		}
		boolean flag = checkRedMemberIsExists(param);
		if (flag)
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1152);
		}

	}

	private IData createRuleData(IData userInfo) throws Exception
	{
		String userId = userInfo.getString("USER_ID");
		String brandCode = userInfo.getString("BRAND_CODE");
		String productId = userInfo.getString("PRODUCT_ID");

		return null;
	}

	/**
	 * @Function: createTradeTimeOut
	 * @Description: ITF_CRM_TradeTimeOutQry 交易超时查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IData
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 下午08:34:17 2014-6-20 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-20 zhuyu v1.0.0
	 */
	public IData createTradeTimeOut(IData input) throws Exception
	{
		// 获取子定单编号并设置resultInfo值
		String rsrv_str3 = input.getString("RSRV_STR3");
		String subscribe_id = input.getString("SUBSCRIBE_ID", "");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String orderId = input.getString("ORDER_ID");
		input.put("TRADE_TYPE_CODE", "329");
		String resultInfo = "";
		IDataset tradeInfos = TradeScoreInfoQry.queryTradeScoreByRsrvstr("329", serialNumber, orderId, subscribe_id);
		if (IDataUtil.isNotEmpty(tradeInfos))
		{
			String subscribe_state = tradeInfos.getData(0).getString("SUBSCRIBE_STATE");
			if ("6".equals(subscribe_state) || "M".equals(subscribe_state))
			{
				resultInfo = "02";
			} else
			{
				resultInfo = "01";
			}
		} else
		{
			resultInfo = "99";
		}

		// 设置返回数据
		IData result = new DataMap();
		result.put("RSRV_STR1", input.getString("RSRV_STR1"));
		result.put("SERIAL_NUMBER", serialNumber);
		result.put("ORDER_ID", orderId);
		result.put("SUBSCRIBE_ID", subscribe_id);
		result.put("RSRV_STR3", rsrv_str3);
		result.put("RESULT", resultInfo);
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "OK");

		return result;
	}

	private void custgroupTransCodeToName(IData data) throws Exception
	{
		data.put("CALLING_TYPE", StaticUtil.getStaticValue("TD_S_CALLINGTYPE", data.getString("CALLING_TYPE_CODE")));
		data.put("CALLING_AREA_NAME", StaticUtil.getStaticValue("TD_S_CALLING_AREA", data.getString("CALLING_AREA_CODE")));
		data.put("ENTERPRISE_TYPE", StaticUtil.getStaticValue("TD_S_ENTERPRISETYPE", data.getString("ENTERPRISE_TYPE_CODE")));
		data.put("ENTERPRISE_SIZE_NAME", StaticUtil.getStaticValue("TD_S_ENTERPRISE_SIZE", data.getString("ENTERPRISE_SIZE_CODE")));
		data.put("PAYFOR_WAY_NAME", StaticUtil.getStaticValue("TD_S_PAYFOR_WAY", data.getString("PAYFOR_WAY_CODE")));
		data.put("CALLING_SUB_TYPE_NAME", StaticUtil.getStaticValue("TD_S_CALLINGSUBTYPE", data.getString("CALLING_SUB_TYPE_CODE")));
		data.put("JURISTIC_TYPE", StaticUtil.getStaticValue("TD_S_JURISTIC_TYPE", data.getString("JURISTIC_TYPE_CODE")));
	}

	private void custgroupTransCodeToName(IDataset result) throws Exception
	{
		for (int i = 0; i < result.size(); i++)
		{
			IData data = result.getData(i);
			custgroupTransCodeToName(data);
		}
	}

	private void customerTransCodeToName(IData data) throws Exception
	{
		data.put("X_CUST_TYPE", StaticUtil.getStaticValue("CUSTOMER_CUSTTYPE", data.getString("CUST_TYPE")));
		data.put("X_CUST_STATE", StaticUtil.getStaticValue("CUSTOMER_CUSTSTATE", data.getString("CUST_STATE")));
		data.put("PSPT_TYPE", StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", data.getString("PSPT_TYPE_CODE")));
		data.put("X_REMOVE_TAG_NAME", StaticUtil.getStaticValue("CUSTGROUP_REMOVETAG", data.getString("REMOVE_TAG")));
		data.put("X_EPARCHY_NAME", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", data.getString("CITY_CODE")));
		data.put("X_DEVELOP_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(data.getString("DEVELOP_DEPART_ID")));
		data.put("X_DEVELOP_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(data.getString("DEVELOP_STAFF_ID")));
		data.put("X_IN_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(data.getString("IN_DEPART_ID")));
		data.put("X_IN_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(data.getString("IN_STAFF_ID")));
		data.put("CITY_NAME", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", data.getString("CUST_STATE")));
		if (false == data.getString("REMOVE_TAG").equals("0"))
		{
			// 接口规范，只有0正常、1销户2种情况，不需要知道具体是什么类型的销户
			data.put("REMOVE_TAG", "1");
		}
	}

	private void customerTransCodeToName(IDataset result) throws Exception
	{
		for (int i = 0; i < result.size(); i++)
		{
			IData data = result.getData(i);
			customerTransCodeToName(data);
		}
	}

	private void custpersonTransCodeToName(IData data) throws Exception
	{
		data.put("X_SEX", StaticUtil.getStaticValue("SEX", data.getString("SEX")));
		data.put("NATIONALITY_NAME", StaticUtil.getStaticValue("TD_S_NATIONALITY", data.getString("NATIONALITY_CODE")));
		data.put("LOCAL_NATIVE_NAME", StaticUtil.getStaticValue("TD_S_LOCAL_NATIVE", data.getString("LOCAL_NATIVE_CODE")));
		data.put("LANGUAGE_NAME", StaticUtil.getStaticValue("TD_S_LANGUAGE", data.getString("LANGUAGE_CODE")));
		data.put("FOLK", StaticUtil.getStaticValue("TD_S_FOLK", data.getString("FOLK_CODE")));
		data.put("JOB_TYPE", StaticUtil.getStaticValue("TD_S_JOBTYPE", data.getString("JOB_TYPE_CODE")));
		data.put("EDUCATE_DEGREE", StaticUtil.getStaticValue("CUSTPERSON_EDUCATEDEGREECODE", data.getString("EDUCATE_DEGREE_CODE")));
		data.put("RELIGION_NAME", StaticUtil.getStaticValue("TD_S_RELIGION", data.getString("RELIGION_CODE")));
		data.put("REVENUE_LEVEL", StaticUtil.getStaticValue("TD_S_REVENUE_LEVEL", data.getString("REVENUE_LEVEL_CODE")));
		data.put("X_MARRIAGE", StaticUtil.getStaticValue("CUSTPERSON_MARRIAGESTATE", data.getString("MARRIAGE")));
		data.put("CHARACTER_TYPE", StaticUtil.getStaticValue("TD_S_CHARACTERTYPE", data.getString("CHARACTER_TYPE_CODE")));
		data.put("CONTACT_TYPE", StaticUtil.getStaticValue("TD_S_CONTACTTYPE", data.getString("CONTACT_TYPE_CODE")));
	}

	private void custpersonTransCodeToName(IDataset result) throws Exception
	{
		for (int i = 0; i < result.size(); i++)
		{
			IData data = result.getData(i);
			custpersonTransCodeToName(data);
		}
	}

	public IData dealTradeConfirmation(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");

		// 获取三户资料
		IDataset datasetUca = this.getUCAInfobySn(serialNumber);
		IData userInfo = datasetUca.getData(0).getData("USER_INFO");
		IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
		IData vipInfo = datasetUca.getData(0).getData("VIP_INFO");
		String tradeId = SeqMgr.getTradeId();
		String orderId = SeqMgr.getOrderId();

		String userId = userInfo.getString("USER_ID");
		IDataset scoreInfos = AcctCall.queryUserScore(userId);

		IData scoreInfo = new DataMap();
		if (IDataUtil.isNotEmpty(scoreInfos))
		{
			scoreInfo = scoreInfos.getData(0);
		}

		IData inParams = new DataMap();
		inParams.put("TRADE_ID", tradeId);
		inParams.put("ORDER_ID", orderId);
		inParams.put("TRADE_TYPE_CODE", "415");
		inParams.put("SERIAL_NUMBER", serialNumber);
		inParams.put("CUST_ID", custInfo.getString("CUST_ID"));
		inParams.put("CUST_NAME", custInfo.getString("CUST_NAME"));
		inParams.put("USER_ID", userId);
		inParams.put("CITY_CODE", userInfo.getString("CITY_CODE"));
		inParams.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
		inParams.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
		inParams.put("RSRV_STR1", scoreInfo.getString("SUM_SCORE", "0")); // 清零的积分额
		inParams.put("RSRV_STR2", scoreInfo.getString("INTEGRAL_TYPE_CODE", "0")); // 积分类型
																					// 0－全球通积分；1－动感地带
		String classId = vipInfo.getString("CLASS_ID", "");
		inParams.put("RSRV_STR3", InterfaceUtil.getCustLevel(classId)); // 客户级别
																		// 0－普通用户（动感地带用户为普通用户）1－银卡2－金卡3－钻石卡
		inParams.put("RSRV_STR4", "1"); // 0-发起方;1-落地方
		inParams.put("REMARK", "跨区入网确认落地方执行成功!");

		// 积分清零
		AcctCall.cancelScoreValue("415", tradeId, userId);

		// VIP级别清零
		SccCall.delCustVipBySN(serialNumber);

		// 插入订单
		LanuchUtil lUtil = new LanuchUtil();
		String ret = lUtil.dealHBTrade(inParams);

		return null;
	}

	public IData dealTradeRep(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "NAME");
		String serialNumber = input.getString("SERIAL_NUMBER");

		// 获取三户资料
		IDataset datasetUca = this.getUCAInfobySn(serialNumber);
		IData userInfo = datasetUca.getData(0).getData("USER_INFO");
		IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
		IData vipInfo = datasetUca.getData(0).getData("VIP_INFO");

		IData ret = new DataMap();
		ret.put("IDTYPE", "01");
		ret.put("IDVALUE", serialNumber);
		ret.put("TYPEIDSET", "5");

		String classId = vipInfo.getString("CLASS_ID");
		ret.put("CLASS_LEVEL", InterfaceUtil.getCustLevel(classId));
		ret.put("JOIN_DATE", userInfo.getString("OPEN_DATE").substring(0, 8));

		if (!input.getString("NAME").equals(custInfo.getString("CUST_NAME")))
		{
			CSAppException.apperr(CustException.CRM_CUST_891);
		}

		ret.putAll(custInfo);
		ret.putAll(userInfo);
		ret.putAll(vipInfo);

		String userId = userInfo.getString("USER_ID");
		IDataset scoreInfos = AcctCall.qryCrossUserScore(userId);
		if (IDataUtil.isNotEmpty(scoreInfos))
		{
			IData userScore = scoreInfos.getData(0);

			ret.put("BRAND_AWARD_SCORE", userScore.getInt("BRAND_SCORE", 0));
			ret.put("YEAR_AWARD_SCORE", userScore.getInt("NETYEAR_SCORE", 0));
			ret.put("OTHER_SCORE", userScore.getInt("OTHER_SCORE", 0));
			ret.put("USE_SCORE", userScore.getInt("EXCHANGE_SCORE", 0));
			ret.put("ABLE_SCORE", userScore.getInt("USER_SCORE", 0));
			ret.put("ALL_CON_SCORE", userScore.getInt("CONSUM_SCORE", 0));
			ret.put("X_RESULTCODE", userScore.getString("X_RESULTCODE", ""));
			ret.put("X_RSPTYPE", "0");
			ret.put("RESULT", "00");
		} else
		{
			ret.put("X_RSPTYPE", "2");
			ret.put("X_RSPCODE", "2998");
			ret.put("RESULT", "99");
		}

		return ret;
	}

	/* CRM侧二次确认表修改状态 */
	public void dealTWOCheck(String forceObject, String oprFlag, String noticeContent) throws Exception
	{
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", forceObject);
		inParam.put("EXEC_FLAG", oprFlag);
		inParam.put("RSRV_STR2", CSBizBean.getVisit().getStaffId());
		inParam.put("RSRV_STR3", CSBizBean.getVisit().getInModeCode());
		inParam.put("RSRV_STR4", SysDateMgr.getSysTime());
		inParam.put("NOTICE_CONTENT", noticeContent);
		Dao.update("TF_B_TWO_CHECK", inParam);
	}

	public IDataset extendIdent(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String inModeCode = IDataUtil.chkParam(input, "IN_MODE_CODE");
		String identCode = IDataUtil.chkParam(input, "IDENT_CODE");
		String effectiveTime = IDataUtil.chkParam(input, "EFFECTIVE_TIME");

		String userType = input.getString("USER_TYPE", "");
		String identCodeType = input.getString("IDENT_CODE_TYPE", "");
		String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");

		// 当前仅支持统一认证中心进行调用
		if (false == StringUtils.equals(inModeCode, "11"))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_914);
		}

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		IDataset idents = UserIdentInfoQry.queryIdentInfoByCode(identCode, serialNumber);
		if (IDataUtil.isEmpty(idents))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1054);
		}

		// 凭证失效提示
		if (StringUtils.equals(idents.getData(0).getString("TAG", ""), "EXPIRE"))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1055);
		}
		// 临时身份凭证不能延长失效时间
		if (StringUtils.equals(idents.getData(0).getString("IDENT_CODE_TYPE", ""), IDENT_CODE_TYPE_TMP))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_917);
		}

		int icnt = UserIdentInfoQry.updIdentByCode(identCode, effectiveTime);
		if (icnt <= 0)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_916);
		}

		SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identextend");

		return rets;
	}

	public IData genIBossData(String serialNumber) throws Exception
	{
		IData outParam = new DataMap();
		IDataset paydata = AcctCall.qryAverFeeBill(serialNumber);

		if (IDataUtil.isNotEmpty(paydata))
		{
			outParam.put("AVG_PAYED", paydata.getData(0).getString("AVERAGE_FEE", ""));
		} else
		{
			outParam.put("AVG_PAYED", "0");
		}

		outParam.put("PAY_TYPE", "01");

		IData areaInfo = MsisdnInfoQry.getRouteInfoBySn(serialNumber);

		if (IDataUtil.isNotEmpty(areaInfo))
		{
			outParam.put("AREA_INFO", areaInfo.getString("PROV_CODE", ""));
		} else
		{
			outParam.put("AREA_INFO", "");
		}

		// 获取三户资料
		IDataset datasetUca = this.getUCAInfobySn(serialNumber);
		IData userInfo = datasetUca.getData(0).getData("USER_INFO");
		IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
		IData vipInfo = datasetUca.getData(0).getData("VIP_INFO");
		String userId = userInfo.getString("USER_ID");

		outParam.put("OPERATE_DATE", userInfo.getString("OPEN_DATE"));

		IDataset svcInfo = UserSvcInfoQry.getSvcUserId(userId, "19");
		if (IDataUtil.isNotEmpty(svcInfo))
		{
			outParam.put("FOREIGN_FLAG", "1");// 国际漫游
		} else
		{
			outParam.put("FOREIGN_FLAG", "0");// 国际漫游
		}

		IDataset uioInfo = PlatSvcInfoQry.queryUIOInfo(serialNumber);

		if (IDataUtil.isNotEmpty(uioInfo))
		{
			outParam.put("BRAND_CODE", uioInfo.getData(0).getString("BRAND_CODE", ""));
		}

		outParam.put("GROUP_LEVEL", "");// 集团客户级别

		String vipClassId = vipInfo.getString("VIP_CLASS_ID");

		if ("4".equals(vipClassId))
		{
			outParam.put("LEVEL", "3");// 客户级别
		} else if ("3".equals(vipClassId))
		{
			outParam.put("LEVEL", "2");// 客户级别
		} else if ("2".equals(vipClassId))
		{
			outParam.put("LEVEL", "1");// 客户级别
		} else
		{
			outParam.put("LEVEL", "0");// 客户级别
		}

		IDataset paramInfo = CommparaInfoQry.getInfoParaCode3("CSM", "5555", userInfo.getString("USER_STATE_CODESET", ""));
		if (IDataUtil.isNotEmpty(paramInfo))
		{
			outParam.put("STATUS", paramInfo.getData(0).getString("PARAM_CODE", ""));// 客户状态
		}

		outParam.put("USER_NAME", custInfo.getString("CUST_NAME", ""));

		outParam.put("REAL_FLAG", "0");// 0-非实名制 1-实名制

		if (custInfo.getString("PSPT_ID", "").length() == 18)
		{
			outParam.put("ID_CARD_TYPE", "01");
			outParam.put("ID_CARD_NUM", custInfo.getString("PSPT_ID", ""));
			outParam.put("REAL_FLAG", "1");
		} else if (custInfo.getString("PSPT_ID", "").length() == 15)
		{
			outParam.put("ID_CARD_TYPE", "02");
			outParam.put("ID_CARD_NUM", custInfo.getString("PSPT_ID", ""));
			outParam.put("REAL_FLAG", "1");
		} else
		{
			outParam.put("ID_CARD_TYPE", "");
			outParam.put("ID_CARD_NUM", "");
		}

		return outParam;
	}

	public IData get12580Info(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		// 获取三户资料
		IDataset datasetUca = getUCAInfobySn(serialNumber);
		IData userInfo = datasetUca.getData(0).getData("USER_INFO");
		IData custInfo = datasetUca.getData(0).getData("CUST_INFO");

		IData result = new DataMap();
		result.put("BRAND_CODE", "0" + InterfaceUtil.getBrandParam(userInfo.getString("BRAND_CODE")));
		// 当前省代码
		String province = UAreaInfoQry.getParentAreaCodeByAreaCode(CSBizBean.getVisit().getStaffEparchyCode());
		IDataset paraInfo = CommparaInfoQry.getCommparaByAttrCode2("CSM", "3032", province, CSBizBean.getVisit().getStaffEparchyCode(), null);

		result.put("HOME_PROV_CODE", "");
		if (IDataUtil.isNotEmpty(paraInfo))
		{
			result.put("HOME_PROV_CODE", paraInfo.getData(0).getString("PARA_CODE1"));
		}

		result.put("CUST_NAME", custInfo.getString("CUST_NAME"));
		result.put("CLASS_LEVEL", InterfaceUtil.getCustLevelParam(custInfo.getString("CLASS_ID")));

		return result;
	}

	public IData userInfoAuth(IData input) throws Exception
	{
		IData result = new DataMap();

		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "TRANSACTION_ID");
		String serialNumber = input.getString("SERIAL_NUMBER");
		// 获取三户资料
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String custCertType = uca.getCustomer().getPsptTypeCode();
		String custCertNo = uca.getCustomer().getPsptId();
		String custName = uca.getCustomer().getCustName();

		// 用户星级
		int creditClass = Integer.parseInt(uca.getUserCreditClass());
		String creditLevel = transUserStar(creditClass);
		String hlr = this.getSwitchId(serialNumber);
		String productName = UProductInfoQry.getProductNameByProductId(uca.getProductId());
		String brandName = UBrandInfoQry.getBrandNameByBrandCode(uca.getBrandCode());
		String acctName = uca.getAccount().getPayName();
		// 获取用户状态并转换用户状态
		String stateCode = uca.getUser().getUserStateCodeset();
		String userState = transUserState(stateCode);
		// String stateName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_S_SERVICESTATE", "STATE_CODE", "STATE_NAME", stateCode);
		String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode("0", stateCode);
		String ownerAddress = StaticUtil.getStaticValue("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()) + StaticUtil.getStaticValue("AREA_CODE", uca.getUser().getEparchyCode());// 归属省
		// 欠费查询
		int acctBalance = Integer.parseInt(uca.getAcctBlance());
		int lastOweFee = Integer.parseInt(uca.getLastOweFee());
		int oweFee = lastOweFee / 100;
		int balance = acctBalance / 100;
		// 入网时间
		String inData = uca.getUser().getInDate();
		Date formateDate = SysDateMgr.string2Date(inData, SysDateMgr.PATTERN_STAND);
		String netTime = SysDateMgr.date2String(formateDate, SysDateMgr.PATTERN_STAND);
		// 实名认证分级 ,过渡期方案：“用户信息鉴权接口”接口里面的字段“客户实名等级”取值
		// customer表中is_real_name：1=E，非1=A
		String custLevel = "A";
		String isRealName = uca.getCustomer().getIsRealName();
		if ("1".equals(isRealName))
		{
			custLevel = "E";
		}

		result.put("RETURN_CODE", "0000");
		result.put("RETURN_MESSAGE", "success");
		result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID"));
		result.put("PRODUCT_NAME", productName);
		result.put("PRODUCT_CODE", uca.getProductId());
		result.put("CUST_CERT_NO", custCertNo);
		result.put("USER_STATE_CODE", userState);
		result.put("USER_STATE_NAME", stateCode + ":" + stateName);
		result.put("CUST_NAME", custName);
		result.put("CUST_LEVEL", custLevel);
		result.put("CREDIT_LEVEL", creditLevel);
		// 最近入网时间不知道如何取，暂时这样。
		result.put("LAST_OPEN_TIME", netTime);
		result.put("CUST_ID", uca.getCustId());
		result.put("CUST_CERT_TYPE", custCertType);
		result.put("HLR", hlr);
		// resInfo.put("BRAND_NAME", brandName);
		// resInfo.put("ACCT_NAME", acctName);
		result.put("OWNER_ADDRESS", ownerAddress);
		result.put("OWE_FEE", oweFee);
		result.put("BALANCE", balance);

		return result;
	}

	public IData userInfoAuthForL2F(IData input) throws Exception
	{
		IData result = new DataMap();
		IDataUtil.chkParam(input, "ID_TYPE");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "ID_TYPE");
		IDataUtil.chkParam(input, "TYPE_ID");
		String idType = input.getString("ID_TYPE");
		String channelId=input.getString("CHANNEL_ID");
		result.put("ID_TYPE", "01");
		result.put("SERIAL_NUMBER", IDataUtil.chkParam(input, "SERIAL_NUMBER"));
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "OK");
		
		UcaData uca = null;
		if (StringUtils.equals(idType, "01"))
		{
			String serialNumber = input.getString("SERIAL_NUMBER");
			uca = UcaDataFactory.getNormalUca(serialNumber);
		} else if (StringUtils.equals(idType, "04"))
		{
			String vipCardNo = input.getString("SERIAL_NUMBER");
			IDataset vipInfo = CustVipInfoQry.queryVipInfoByVipNo(vipCardNo, "0");
			if (IDataUtil.isNotEmpty(vipInfo))
			{
				String serialNumber = vipInfo.getData(0).getString("SERIAL_NUMBER", "");
				uca = UcaDataFactory.getNormalUca(serialNumber);
			} else
			{
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RESULTCODE", "3030");
				result.put("X_RESULTINFO", "用户资料不存在");
				return result;
			}
		} else
		{
			String strError = "参数ID_TYPE取值不正确";
			CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
		}
		if (null == uca)
		{
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			result.put("X_RESULTCODE", "3030");
			result.put("X_RESULTINFO", "用户资料不存在");
			return result;
		}
		else
		{
			IData ucaInfo = UcaInfoQry.qryUserInfoBySn(input
					.getString("SERIAL_NUMBER"));
			String userStatus = getIBossUserStateParam(ucaInfo
					.getString("USER_STATE_CODESET"));
			boolean isCorrectStatus = false;
	        String errorMsg = "";
	        if("5".equals(ucaInfo.getString("USER_STATE_CODESET"))
	        		||"7".equals(ucaInfo.getString("USER_STATE_CODESET"))||"B".equals(ucaInfo.getString("USER_STATE_CODESET"))){
				isCorrectStatus = false;
				result.put("X_RESULTINFO", "手机号码欠费停机");
				result.put("X_RESULTCODE", "2005");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户状态异常");
				return result;
			}
	        else if ("00".equals(userStatus)) {
				isCorrectStatus = true;
				errorMsg = "ok";
				result.put("X_RESULTCODE", "0");
				result.put("X_RESULTINFO", "OK");
			} else if ("01".equals(userStatus)) {
				result.put("X_RESULTINFO", "此号码已单向停机");
				result.put("X_RESULTCODE", "2005");
				isCorrectStatus = false;
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户状态异常");
				return result;
			} else if ("02".equals(userStatus)) {
				isCorrectStatus = false;
				result.put("X_RESULTINFO", "用户主动停机号码");
				result.put("X_RESULTCODE", "2053");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户状态异常");
				return result;
			} else if ("03".equals(userStatus)) {
				isCorrectStatus = false;
				result.put("X_RESULTINFO", "此号码已预销户");
				result.put("X_RESULTCODE", "3030");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户状态异常");
				return result;
			}else {
				result.put("X_RESULTINFO", "此号码已销户");
				result.put("X_RESULTCODE", "3030");
				isCorrectStatus = false;
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户状态异常");
				return result;
			}
			if (!isCorrectStatus) {
	        	if (StringUtils.equals("5", uca.getUser().getUserStateCodeset()) // 欠费停机
	        			|| StringUtils.equals("A", uca.getUser().getUserStateCodeset())) {// 欠费半停机
	        		result.put("X_RESULTCODE", "2005");//2005:鉴权失败（欠费停机）
	        		errorMsg = "鉴权失败（欠费停机或欠费半停机）";
	        	} else {//二级返回码对于状态只有欠费停机与主动停机两种归类，其他的状态错误，只能归类于主动停机了。
	        		result.put("X_RESULTCODE", "2053");//2053:鉴权失败（用户主动停机）
	        		errorMsg = "鉴权失败（非正常在网用户）";
	        	}
	    		result.put("X_RESULTINFO", errorMsg); 
				return result;
	        }
			String isRealName = uca.getCustomer().getIsRealName();
			if(!"1".equals(isRealName)){
				result.put("X_RESULTINFO", "用户非实名状态");
				result.put("X_RESULTCODE", "3040");
				isCorrectStatus = false;
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户非实名状态");
				return result;
			}
			IDataset trade = TradeInfoQry.CheckIsExistNotBBSSFinishedTrade(input.getString("SERIAL_NUMBER"));
			if(DataUtils.isNotEmpty(trade)&&!"0".equals(trade.getData(0).getString("ROW_COUNT"))){
				result.put("X_RESULTINFO", "用户有未完工工单");
				result.put("X_RESULTCODE", "3012");
				isCorrectStatus = false;
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("X_RSPDESC", "用户有未完工工单");
				return result;
			}
		}
		
		
		
		String passwd = input.getString("CCPASSWD");
//		IDataUtil.chkParam(input, "IDCARD_TYPE");
//      IDataUtil.chkParam(input, "IDCARD_NUM");
		String pstpTypeCode =  getCrmPsptTypeParam(input.getString("IDCARD_TYPE","").trim());            
        String pstpId = input.getString("IDCARD_NUM");
        //if (StringUtils.isEmpty(passwd) && StringUtils.isEmpty(pstpId)) {
        if (StringUtils.isEmpty(pstpId)) {
         	result.put("X_RESULTCODE", "3030");
         	result.put("X_RESULTINFO","证件号码不能为空"); 
         	return result;                 
         } else {
    		 if (!StringUtils.isEmpty(pstpId)) {
	            boolean checkError = false;
	            if (StringUtils.equals(pstpId, uca.getCustomer().getPsptId())) {
	                if(input.getString("IDCARD_TYPE","").trim().equals("00")){
	                    if( StringUtils.equals("0", uca.getCustomer().getPsptTypeCode())|| StringUtils.equals("1", uca.getCustomer().getPsptTypeCode())){ 
	                    	
	                    }else{
	                        checkError = true;
	                    }
	                }else{
	                    if( StringUtils.equals(pstpTypeCode, uca.getCustomer().getPsptTypeCode())){                        
	                    }else{
	                        checkError = true;
	                    }                    
	                }
	            } else {
	            	if(input.getString("IDCARD_TYPE","").trim().equals("00")){
	            		if( pstpId.length() == 15)
	            		{
	            			pstpId = IdcardUtils.conver15CardTo18(pstpId);
	            			if (StringUtils.equals(pstpId.toUpperCase(), uca.getCustomer().getPsptId().toUpperCase())) {
	            				
	            			}
	            			else{
	                            checkError = true;
	                        } 
			
	            		}
	            		if( uca.getCustomer().getPsptId().length() == 15)
	            		{
	            			String strPstpId = IdcardUtils.conver15CardTo18(uca.getCustomer().getPsptId());
	            			if (StringUtils.equals(pstpId.toUpperCase(), strPstpId.toUpperCase())) {
	            				
	            			}
	            			else{
	                            checkError = true;
	                        } 
	
	            		}
	            		else
	            			checkError = true;
	            	}
	            	else 
	            		checkError = true;
	            }
	            
	            if(checkError){
	                result.put("X_RESULTCODE", "3030");
	                result.put("X_RESULTINFO","证件校验失败"); 
	                result.put("X_RSPTYPE", "2");
	                result.put("X_RSPCODE", "2998");
	                result.put("X_RSPDESC", "证件校验失败");                
	                return result;
	            }
	        } 
    		
    		
    		 
		     if(!"100000000200001".equals(input.getString("CHANNEL_ID",""))){
		    	 //判断用户是否是黑名单
	    		 /*IDataset custs = CustomerInfoQry.queryCustInfoBySN(input.getString("SERIAL_NUMBER"));
	    		 String psptId = custs.getData(0).getString("PSPT_ID", "0");
	    		 logger.debug(psptId+"用户拿去校验黑名单的身份证号");
	    		 input.put("PSPT_ID", pstpId);//入参
	    		 if(chkBlackUser(input)){
	 	        	result.put("X_RESULTINFO","黑名单用户"); 
	 	        	result.put("X_RSPDESC", "黑名单用户"); 
	 	        	result.put("X_RSPTYPE", "2");
	 	        	result.put("X_RSPCODE", "2998");
	 	            result.put("X_RESULTCODE", "3000");
	 	            return result;
	 	        }*/
		    	 //不是能开调用才进行服务密码鉴权 一级能开调用不需要客户服务密码
		    	 if(!StringUtils.isEmpty(passwd)) {
						IData data = new DataMap();
						data.put("SERIAL_NUMBER", uca.getSerialNumber());
						data.put("USER_PASSWD", passwd);
						IData checkResut = UserPasswordInfoComm.checkUserPWDForL2F(data);
						if (!StringUtils.equals(checkResut.getString("X_CHECK_INFO"), "0"))
						{
							result.put("X_RESULTCODE", "3030");
							result.put("X_RESULTINFO", checkResut.getString("X_RESULTINFO"));
							result.put("X_RSPTYPE", "2");
							result.put("X_RSPCODE", "2998");
							result.put("X_RSPDESC", checkResut.getString("X_RESULTINFO"));
							return result;
						}
					}else{
						 	result.put("X_RESULTCODE", "3030");
			                result.put("X_RESULTINFO","服务密码不能为空！"); 
			                result.put("X_RSPTYPE", "2");
			                result.put("X_RSPCODE", "2998");
			                result.put("X_RSPDESC", "服务密码不能为空！"); 
			                return result;
						}
		     	}
         }
        
        
		IData returnInfoType = new DataMap();
		IData returnInfoType1 = new DataMap();
		IDataset typeIdList = input.getDataset("TYPE_ID");
		if (IDataUtil.isNotEmpty(typeIdList))
		{
			for (int i = 0; i < typeIdList.size(); i++)
			{
				String typeId = (String) typeIdList.get(i);
				if (StringUtils.equals(typeId, "0"))
				{// 客户基本资料项目编码
					returnInfoType.put("100", uca.getCustId()); // 客户标识
					returnInfoType.put("101", uca.getCustomer().getCustName());
					returnInfoType.put("103", this.getIBossPsptTypeParam(uca.getCustomer().getPsptTypeCode()));
					returnInfoType.put("104", uca.getCustomer().getPsptId());
					String score = "0";
					String totalPoint = "";// 用户总积分
					IDataset scoreInfo = AcctCall.queryUserScore(uca.getUserId());
					if (IDataUtil.isNotEmpty(scoreInfo))
					{
						score = scoreInfo.getData(0).getString("SCORE");
						totalPoint = scoreInfo.getData(0).getString("SUM_SCORE");
					}
					IDataset scoreLimitDs = UserInfoQry.qryUserScoreLimit(uca.getSerialNumber());
					if (IDataUtil.isNotEmpty(scoreLimitDs))
					{
						returnInfoType.put("114", "0");
					} else
					{
						returnInfoType.put("114", score);
					}
					String inData = uca.getUser().getInDate();
					Date formateDate = SysDateMgr.string2Date(inData, SysDateMgr.PATTERN_STAND);
					String netTime = SysDateMgr.date2String(formateDate, SysDateMgr.PATTERN_STAND_SHORT);
					returnInfoType.put("118", netTime);// 注册日期/入网时间
					String userStatus = getIBossUserStateParam(uca.getUser().getUserStateCodeset());
					returnInfoType.put("119", userStatus);// 用户状态
					IDataset userRes = UserResInfoQry.getUserResInfoByUserId(uca.getUserId());
					if (IDataUtil.isEmpty(userRes))
					{
						CSAppException.apperr(CrmCardException.CRM_CARD_71); // 查询用户原SIM卡资源信息无记录！
					}
					for (int j = 0; j < userRes.size(); j++)
					{
						IData data = new DataMap();
						data = (IData) userRes.get(j);
						String resTypeCode = data.getString("RES_TYPE_CODE");
						if (resTypeCode.equals("1"))
						{
							returnInfoType.put("122", data.getString("PUK"));// TODO
																				// 不加密的PUK码
																				// PUK没获取到
						}
					}
					returnInfoType.put("123", uca.getCustomer().getCreditValue());// 填级别或信用额度
					
					IDataset platsvcids = UserPlatSvcInfoQry.queryPlatSvcByUserId(uca.getUserId());
			        String expIncrementBusi = "";
			        for (int k = 0; k < platsvcids.size(); k++)
			        {
			            if (null == platsvcids.getData(k).getString("SERVICE_NAME") || "".equals(platsvcids.getData(k).getString("SERVICE_NAME")))
			            {
			                break;
			            }
			            expIncrementBusi += platsvcids.getData(k).getString("SERVICE_NAME");
			            if (k != (platsvcids.size() - 1))
			            {
			            	expIncrementBusi += "|";
			            }
			        }
			        
			        if (null == expIncrementBusi || "".equals(expIncrementBusi))
			        {
			            expIncrementBusi = "无";
			        }
					returnInfoType.put("124", expIncrementBusi);// TODO 描述已开通的增值业务
													
					String ownerAddress = StaticUtil.getStaticValue("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()) + StaticUtil.getStaticValue("AREA_CODE", uca.getUser().getEparchyCode());
					returnInfoType.put("126", ownerAddress);// 归属地名称 归属省+地市名称
					int creditClass = Integer.parseInt(uca.getUserCreditClass());
					String creditLevel = transUserStar(creditClass);
					returnInfoType.put("127", creditLevel);// 用户星级
					returnInfoType.put("128", "");// TODO 还没与客户确认填什么，暂时为空
					returnInfoType.put("129", uca.getBrandCode()); // 客户品牌
				}
				if (StringUtils.equals(typeId, "1"))
				{// 个性化资料数据编码（基本资料）
					returnInfoType1.put("601", uca.getCustPerson().getCustName());// 姓名
					returnInfoType1.put("602", this.getIBossSexParam(uca.getCustPerson().getSex()));// 性别
																									// 0:男,1:女,2:未知
					returnInfoType1.put("604", uca.getCustPerson().getPsptTypeCode());// 身份证件类型
					returnInfoType1.put("605", uca.getCustPerson().getPsptId());// 身份证件号码
					returnInfoType1.put("606", this.getIBossMarriageParam(uca.getCustPerson().getMarriage()));// 婚姻状况
																												// 0:未婚,1:已婚,2:其它
					returnInfoType1.put("607", this.getIBossEducateDegreeParam(uca.getCustPerson().getEducateDegreeCode()));// 教育程度
					returnInfoType1.put("608", uca.getUser().getSerialNumber());// 手机号码
					returnInfoType1.put("609", uca.getCustPerson().getContactPhone());// 联系电话
					returnInfoType1.put("610", uca.getCustPerson().getPostAddress());// 联系地址
					String custManagerId = "";
					IData param = new DataMap();
					param.put("SERIAL_NUMBER", uca.getSerialNumber());
					IDataset vipInfo = Qry360InfoDAO.qryTelManager(param);
					if (IDataUtil.isNotEmpty(vipInfo))
					{
						custManagerId = vipInfo.getData(0).getString("CUST_MANAGER_ID", "");
					}
					returnInfoType1.put("611", custManagerId);// 客户经理工号 姓名
					String contactPhone = "";
					if (StringUtils.isNotEmpty(custManagerId))
					{
						IData staffInfo = StaffInfoQry.qryStaffInfoByPK(custManagerId);
						if (IDataUtil.isNotEmpty(staffInfo))
						{
							contactPhone = staffInfo.getString("SERIAL_NUMBER");
						}
					}
					returnInfoType1.put("627", contactPhone);// 客户经理联系电话
					returnInfoType1.put("628", uca.getCustPerson().getWorkName());// 工作单位
					IData cond = new DataMap();
					cond.put("SERIAL_NUMBER", uca.getUser().getSerialNumber());
					IDataset ds = CSAppCall.call("AM_QueryVoiceVariFee", cond);
					if (DataSetUtils.isNotBlank(ds))
					{
						IData data = ds.getData(0);
						returnInfoType1.put("621", data.getString("LOCALVOICE_FEE", "0"));// 本地市话月消费额(当月)
						returnInfoType1.put("622", data.getString("LOCALLONG_FEE", "0"));// 国内长途月消费额(当月)
						returnInfoType1.put("623", data.getString("INTERLONG_FEE", "0"));// 国际长途月消费额(当月)
						returnInfoType1.put("624", data.getString("LOCALROAM_FEE", "0"));// 国内漫游月消费额(当月)
						returnInfoType1.put("625", data.getString("INTERROAM_FEE", "0"));// 国际漫游月消费额(当月)
						returnInfoType1.put("629", data.getString("MAINSP_FEE", "0"));// 主要增值业务收入(当月)
					}
					
					IData idata = new DataMap();
             		String userId = uca.getUserId();
                    idata.put("USER_ID", userId);
                    idata.put("EPARCHY_CODE", BizRoute.getRouteId());
             		IDataset svcids = UserSvcInfoQry.queryUserSvcByUserIdAll(userId);
             		String usageDesc = "";
            		for(int k=0;k<svcids.size();k++)
            		{
            			if(null == svcids.getData(k).getString("SERVICE_NAME") || 
            					"".equals(svcids.getData(k).getString("SERVICE_NAME")))
            			{
            				break;
            			}
            			usageDesc += svcids.getData(k).getString("SERVICE_NAME");
            			if(k != (svcids.size()-1))
            			{
            				usageDesc += "|";
            			}
            		}
            		if(null == usageDesc || "".equals(usageDesc))
            			usageDesc = "无";
            		
            		returnInfoType1.put("630", usageDesc); //基本业务开通情况
					
					
					
					IDataset platsvcids = UserPlatSvcInfoQry.queryPlatSvcByUserId(uca.getUserId());
			        String expIncrementBusi = "";
			        for (int k = 0; k < platsvcids.size(); k++)
			        {
			            if (null == platsvcids.getData(k).getString("SERVICE_NAME") || "".equals(platsvcids.getData(k).getString("SERVICE_NAME")))
			            {
			                break;
			            }
			            expIncrementBusi += platsvcids.getData(k).getString("SERVICE_NAME");
			            if (k != (platsvcids.size() - 1))
			            {
			            	expIncrementBusi += "|";
			            }
			        }
			        
			        if (null == expIncrementBusi || "".equals(expIncrementBusi))
			        {
			            expIncrementBusi = "无";
			        }
			        result.put("631", expIncrementBusi);
				}
			}
		}
		IData infoCont = new DataMap();
		IDataset infoContList = new DatasetList();
		StringBuffer strItemId = new StringBuffer();
		StringBuffer strItemCont = new StringBuffer();
		StringBuffer strInfoTypeId = new StringBuffer();
		if (returnInfoType != null && !returnInfoType.isEmpty())
		{
			IDataset infoItems = new DatasetList();
			Iterator lter = returnInfoType.keySet().iterator();
			while (lter.hasNext())
			{
				String itemId = (String) lter.next();
				String ItemCont = returnInfoType.getString(itemId);
				IData infoItem = new DataMap();
				infoItem.put("ITEM_ID", itemId);
				infoItem.put("ITEM_CONT", ItemCont);
				infoItems.add(infoItem);
			}
			IData infoItem = new DataMap();
			infoItem.put("INFO_TYPEID", "0");// 客户基本资料
			infoItems.add(infoItem);
			infoContList.add(infoItems.toData());
		}
		if (returnInfoType1 != null && !returnInfoType1.isEmpty())
		{
			IDataset infoItems = new DatasetList();
			Iterator lter = returnInfoType1.keySet().iterator();
			while (lter.hasNext())
			{
				String itemId = (String) lter.next();
				String ItemCont = returnInfoType1.getString(itemId);
				IData infoItem = new DataMap();
				infoItem.put("ITEM_ID", itemId);
				infoItem.put("ITEM_CONT", ItemCont);
				infoItems.add(infoItem);
			}
			IData infoItem = new DataMap();
			infoItem.put("INFO_TYPEID", "1");// 客户个性化资料
			infoItems.add(infoItem);
			infoContList.add(infoItems.toData());
		}
		result.put("INFO_CONT", infoContList);
		int acctBalance = Integer.parseInt(uca.getAcctBlance());
		int lastOweFee = Integer.parseInt(uca.getLastOweFee());
		int oweFee = lastOweFee * 10;// 帐户应缴金额(厘)
		int balance = acctBalance * 10;// 帐户预存余额(厘)
		result.put("ACNT_PAYAMOUNT", oweFee);
		result.put("ACNT_BALANCE", balance);
		return result;
	}


	public IDataset getAcctInfo(IData input) throws Exception
	{

		IDataset result = new DatasetList();
		// 取用户USER_ID
		String x_getMode = input.getString("X_GETMODE", "");
		String serNum = input.getString("SERIAL_NUMBER", "");

		if (StringUtils.isBlank(x_getMode))
		{
			CSAppException.apperr(ParamException.CRM_PARAM_285);
		}
		if (StringUtils.isBlank(serNum))
		{
			CSAppException.apperr(ParamException.CRM_PARAM_371);
		}

		if (x_getMode.equals(X_GETMODE_BY_SN_NORMAL))
		{
			// 输入服务号码取正常用
			result = AcctInfoQry.qryAcctInfoBySn(serNum);
		} else if (x_getMode.equals(X_GETMODE_BY_SN_ALL))
		{
			// 输入服务号码取所有用户
			result = AcctInfoQry.qryAcctInfoBySnDef(serNum);
		} else if (x_getMode.equals(X_GETMODE_BY_SN_ABNORMAL))
		{
			// 输入服务号码取所有非正常用户
			result = AcctInfoQry.qryAcctInfoBySnDefAbn(serNum);
		} else if (x_getMode.equals(X_GETMODE_BY_SN_LAST_ABNORMAL))
		{
			// 输入服务号码取最后销户用户
			result = AcctInfoQry.qryAcctInfoBySnDefLAbn(serNum);
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_513);
		}

		for (int i = 0; i < result.size(); i++)
		{
			IData data = result.getData(i);
			data.put("PAY_MODE", UPayModeInfoQry.getPayModeNameByPayModeCode(data.getString("PAY_MODE_CODE")));
			if (!data.getString("REMOVE_TAG").equals("0"))
			{
				// 接口规范，只有0正常、1销户2种情况，不需要知道具体是什么类型的销户
				data.put("REMOVE_TAG", "1");
			}
			data.put("X_REMOVE_TAG_NAME", StaticUtil.getStaticValue("CUSTGROUP_REMOVETAG", data.getString("REMOVE_TAG")));
		}

		if (IDataUtil.isEmpty(result))
		{
			CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_85);
		}

		return result;
	}

	public IData getAutoContractInfo(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		if ("6".equals(CSBizBean.getVisit().getInModeCode()))
			input.put("KIND_ID", "BIP2B083_T2040029_1_0");

		String serialNumber = input.getString("SERIAL_NUMBER");
		IData userInfo = getUserInfo4SingleAll(serialNumber);

		IData ret = new DataMap();
		if (IDataUtil.isEmpty(userInfo))
		{
			ret.put("SIGNSTATUS", "9");

			return ret;
		} else
		{
			String userId = userInfo.getString("USER_ID");
			if (UserSvcInfoQry.getAutoPayContractState(userId, "171717"))
			{
				IDataset userAttr = UserAttrInfoQry.getAutoPayContractInfo(userId, "171717");
				if (IDataUtil.isNotEmpty(userAttr))
				{
					for (Object ret1 : userAttr)
					{
						IData userAttrInfo = (IData) ret1;
						ret.putAll(InterfaceUtil.detachStr(userAttrInfo.getString("ATTR_VALUE")));
					}
				}

				ret.put("SIGNSTATUS", "0");

			} else
			{
				ret.put("SIGNSTATUS", "1");
			}

		}

		return ret;
	}

	public IData getBrandInfo(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");

		// 获取正常用户信息
		IData userInfo = getUserInfo4All(serialNumber);

		String userId = userInfo.getString("USER_ID"); // 用户标识
		String eparchyCode = userInfo.getString("EPARCHY_CODE"); // 用户归属地州
		String brandCode = userInfo.getString("BRAND_CODE"); // 品牌
		String productId = userInfo.getString("PRODUCT_ID"); // 产品
		String openDate = userInfo.getString("OPEN_DATE").substring(0, 19); // 开户时间
																			// yyyy-MM-dd
																			// HH:mm:ss

		// 返回信息
		IData returnData = new DataMap();
		returnData.put("USER_ID", userId);
		returnData.put("USER_STATE_CODESET", userInfo.getString("USER_STATE_CODESET"));
		returnData.put("EPARCHY_CODE", eparchyCode);
		returnData.put("BRAND_CODE", brandCode);
		returnData.put("PRODUCT_ID", productId);
		returnData.put("OPEN_DATE", openDate);

		returnData.put("EPARCHY", UAreaInfoQry.getAreaNameByAreaCode(eparchyCode));
		returnData.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(brandCode));
		returnData.put("GROUP_BRAND", UBrandInfoQry.getBrandNameByBrandCode(brandCode));

		// 获取在网月数
		returnData.put("DYNAMIC_INTERVAL", SysDateMgr.monthInterval(openDate, SysDateMgr.getSysDate()));

		return returnData;
	}

	public String getCreditValue(String userId, String typeId) throws Exception
	{
		IData creaditInfo = queryCreditInfos(userId, typeId);
		return creaditInfo.getString("CREDIT_VALUE", "0");
	}

	public IData getCustInfo12580(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData ret = new DataMap();
		String serialNumber = input.getString("SERIAL_NUMBER");
		IDataset mofficeInfo = MofficeInfoQry.getMsisdnCityInfo(serialNumber);

		if (IDataUtil.isNotEmpty(mofficeInfo))
		{
			// 获取三户资料
			IDataset datasetUca = getUCAInfobySn(serialNumber);
			IData userInfo = datasetUca.getData(0).getData("USER_INFO");
			IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
			IData vipInfo = datasetUca.getData(0).getData("VIP_INFO");

			ret.put("SERIAL_NUMBER", serialNumber);
			ret.put("USER_ID", userInfo.getString("USER_ID"));
			ret.put("CUST_ID", custInfo.getString("CUST_ID"));
			ret.put("USECUST_ID", userInfo.getString("USECUST_ID"));// 使用客户标识
			ret.put("USER_NAME", custInfo.getString("CUST_NAME"));
			ret.put("IN_PROVINCE", StaticUtil.getStaticValue("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode()) + StaticUtil.getStaticValue("AREA_CODE", userInfo.getString("EPARCHY_CODE")));// 归属省
			ret.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));// 省内查询返回boss内部用户品牌编码
			ret.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(userInfo.getString("BRAND_CODE")));

			String userbrank = "";
			if (IDataUtil.isNotEmpty(vipInfo))
			{
				userbrank = "普通客户";
			} else
			{
				userbrank = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_VIPCLASS", "CLASS_ID", "CLASS_NAME", vipInfo.getString("VIP_CLASS_ID"));
			}

			ret.put("USERRANK", userbrank);
			ret.put("X_RSPTYPE", "0");
			ret.put("X_RSPCODE", "0000");
			ret.put("X_RSPDESC", "success");
		} else
		{
			IData bossInfo = IBossCall.callCustomerInfo4HaiNan(serialNumber);

			String brand = UBrandInfoQry.getBrandNameByBrandCode(bossInfo.getString("BRAND_CODE"));
			if (StringUtils.isBlank(brand))
			{
				brand = bossInfo.getString("BRAND_CODE");
			}

			/*
			 * 接口规范上是一位，但实际返回的是两位，接口和规范有差别 0 全球通 1 神州行 2 动感地带 3 其他品牌
			 */
			if ("00".equals(brand))
				brand = "全球通";
			if ("01".equals(brand))
				brand = "神州行";
			if ("02".equals(brand))
				brand = "动感地带";
			if ("03".equals(brand))
				brand = "其他品牌";

			ret.put("RSRV_STR1", brand);
			ret.put("RSRV_STR2", StaticUtil.getStaticValue("COP_SI_PROV_CODE", bossInfo.getString("HOME_PROV_CODE")));
			ret.put("RSRV_STR3", bossInfo.getString("CUST_NAME"));
			String class_id = bossInfo.getString("CLASS_LEVEL");
			if (StringUtils.isBlank(bossInfo.getString("CLASS_LEVEL")))
			{
				if ("100".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "普通客户";
				if ("200".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "重要客户";
				if ("201".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "党政机关客户";
				if ("202".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "军、警、安全机关客户";
				if ("203".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "联通合作伙伴客户";
				if ("204".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "英雄、模范、名星类客户";
				if ("300".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "普通大客户";
				if ("301".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "钻石卡大客户";
				if ("302".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "金卡大客户";
				if ("303".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "银卡大客户";
				if ("304".equals(bossInfo.getString("CLASS_LEVEL")))
					class_id = "贵宾卡大客户";
			}
			ret.put("RSRV_STR4", class_id);
			ret.put("RSRV_STR5", bossInfo.getString("INFO_CODE"));
			ret.put("RSRV_STR6", bossInfo.getString("INFO_VALUE"));
			ret.put("X_RSPTYPE", bossInfo.getString("X_RSPTYPE"));
			ret.put("X_RSPCODE", bossInfo.getString("X_RSPCODE"));
			ret.put("X_RSPDESC", bossInfo.getString("X_RSPDESC"));
		}

		return ret;
	}

	public IDataset getDreamNetServiceOrderInfo(String userId, String flag, String rsrvStr9, String productId, String brandCode, String eparchyCode) throws Exception
	{
		IDataset platInfos = new DatasetList();

		// 生效的订购关系
		platInfos = PlatSvcInfoQry.queryPlatOrderInfobyUserId01(userId, rsrvStr9, productId, brandCode, eparchyCode);

		// IDataset ret = new DatasetList();
		//
		// if (IDataUtil.isNotEmpty(platInfos))
		// {
		// for (int i = 0, size = platInfos.size(); i < size; i++)
		// {
		// IData platInfo = platInfos.getData(i);
		// boolean doKey = false;
		//                 
		// String serviceId = platInfo.getString("SERVICE_ID");
		//                 
		// boolean configKey = RuleCfgInfoQry.checkConfigInfo(serviceId);
		//
		// boolean configRuleKey = this.checkServiceConfigRule(flag,
		// eparchyCode, serviceId, productId, brandCode, userId);
		//
		// if (configKey && configRuleKey)
		// {
		// doKey = true;
		// }
		//                  
		// /******************** TD_B_SERVICE *************************/
		// String rsrvTag3 = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_SERVICE", "SERVICE_ID", "RSRV_TAG3", serviceId);
		//  
		// boolean str9Flag = false;
		// if (StringUtils.isNotBlank(rsrvTag3))
		// {
		// if (rsrvTag3.equals(rsrvStr9))
		// {
		// str9Flag = true;
		// }
		// }
		//                
		// 
		// if (doKey && str9Flag )
		// {
		// /******************** TD_B_SERVICE *************************/
		// String serviceName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_SERVICE", "SERVICE_ID", "SERVICE_NAME", serviceId);
		// platInfo.put("BIZ_NAME", serviceName);
		//                    
		// String rsrvStr4 = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_SERVICE", "SERVICE_ID", "RSRV_STR4", serviceId);
		// platInfo.put("BILLFLG", rsrvStr4);
		//                    
		// String rsrvStr3 = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_SERVICE", "SERVICE_ID", "RSRV_STR3", serviceId);
		// if(StringUtils.isNotBlank(rsrvStr3))
		// {
		// platInfo.put("PRICE", Integer.valueOf(rsrvStr3) / 1000);
		// }
		// else
		// {
		// platInfo.put("PRICE", "0");
		// }
		//                   
		//                    
		// platInfo.put("TAG_CHAR", rsrvTag3);
		//                    
		// String rsrvStr2 = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_SERVICE", "SERVICE_ID", "RSRV_STR2", serviceId);
		// platInfo.put("RSRV_STR10", rsrvStr2);
		//                    
		// String rsrvStr5 = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_SERVICE", "SERVICE_ID", "RSRV_STR5", serviceId);
		// platInfo.put("BIZ_DESC", rsrvStr5);
		// 
		// platInfo.put("SP_ID", "1001");
		// platInfo.put("BIZ_CODE", "");
		// platInfo.put("SP_NAME", "中国移动");
		// platInfo.put("GIFT_SERIAL_NUMBER", "");
		// platInfo.put("SP_SHORT_NAME", "");
		// platInfo.put("BIZ_TYPE", "");
		//                    
		// platInfo.put("BIZ_TYPE_CODE", "");
		// platInfo.put("BIZ_TYPE_CODE1", "");
		// platInfo.put("ORG_DOMAIN", "");
		// platInfo.put("OPR_SOURCE", "");
		// platInfo.put("BIZ_STATE_CODE", "");
		//                    
		// platInfo.put("SERV_ATTR", "");
		// platInfo.put("SERV_CODE", "");
		// platInfo.put("CS_TEL", "");
		// platInfo.put("SERIAL_NUMBER", "");
		// platInfo.put("PRODUCT_NO", "");
		//
		// platInfo.put("OPER_CODE", "");
		// platInfo.put("GIFT_USER_ID", "");
		// platInfo.put("SERV_TYPE", "0");
		// platInfo.put("SERVICE_TYPE", "S");
		//                     
		// ret.add(platInfo);
		// }
		// }
		// }

		return platInfos;
	}

	public IDataset getDreamNetPlatOrderInfo(String userId, String flag, String rsrvStr9, String productId, String brandCode, String eparchyCode) throws Exception
	{
		IDataset platInfos = new DatasetList();

		// 生效的订购关系
		if ("00".equals(flag))
		{
			platInfos = PlatSvcInfoQry.queryPlatOrderInfobyUserId04(userId, rsrvStr9, productId, brandCode, eparchyCode);
		}
		// 历史定购信息
		else if ("01".equals(flag))
		{
			platInfos = PlatSvcInfoQry.queryPlatOrderInfobyUserId05(userId, rsrvStr9, productId, brandCode, eparchyCode);
		}
		// 全部订购关系
		else if ("02".equals(flag))
		{
			platInfos = PlatSvcInfoQry.queryPlatOrderInfobyUserId06(userId, rsrvStr9, productId, brandCode, eparchyCode);
		}
		// else if ("04".equals(flag))
		// {
		// platInfos =
		// PlatSvcInfoQry.queryPlatOrderInfobyUserId03(userId,productId,eparchyCode);
		// }
		//
		// IDataset ret = new DatasetList();
		//
		// if (IDataUtil.isNotEmpty(platInfos))
		// {
		// for (int i = 0, size = platInfos.size(); i < size; i++)
		// {
		// IData platInfo = platInfos.getData(i);
		// boolean doKey = false;
		//
		// String serviceId = platInfo.getString("SERVICE_ID");
		//
		// if (!"04".equals(flag))
		// {
		// boolean configKey = RuleCfgInfoQry.checkConfigInfo(serviceId);
		//
		// boolean configRuleKey = this.checkConfigRule(flag, eparchyCode,
		// serviceId, productId, brandCode, userId);
		//
		// if (configKey && configRuleKey)
		// {
		// doKey = true;
		// }
		// }
		// else
		// {
		// doKey = true;
		// }
		//
		// /******************** TD_B_PLATSVC *************************/
		// String servType = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "SERV_TYPE", serviceId);
		//
		// String spCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "SP_CODE", serviceId);
		//                
		// String bizCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "BIZ_CODE", serviceId);
		//                
		// String bizTypeCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "BIZ_TYPE_CODE", serviceId);
		//                
		// String orgDomain = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "ORG_DOMAIN", serviceId);
		//                
		// String orgDomainParam =
		// StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PLATSVC_PARAM",
		// new String [] {"BIZ_TYPE_CODE","SERV_TYPE"}
		// , "ORG_DOMAIN",new String [] {bizTypeCode,servType});
		//                  
		// String TDRsrvStr9 = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ",new String [] {"SP_CODE","BIZ_CODE"}
		// , "RSRV_STR9", new String [] {spCode,bizCode});
		//
		// String bizStateCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "BIZ_STATE_CODE", new String [] {spCode,bizCode});
		// String bizStatus = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "BIZ_STATUS", new String [] {spCode,bizCode});
		// 
		//               
		// boolean statusFlag = true;
		// // 生效的订购关系
		// if ("00".equals(flag) || "04".equals(flag))
		// {
		// if ("4".equals(bizStateCode) || "4".equals(bizStatus))
		// {
		// statusFlag = false;
		// }
		// }
		//
		// boolean str9Flag = false;
		// if (!"04".equals(flag))
		// {
		// if (StringUtils.isBlank(rsrvStr9) || "0".equals(rsrvStr9))
		// {
		// str9Flag = true;
		// }
		//
		// if (StringUtils.isNotBlank(TDRsrvStr9))
		// {
		// if (TDRsrvStr9.equals(rsrvStr9))
		// {
		// str9Flag = true;
		// }
		// }
		// else
		// {
		// if (StringUtils.isBlank(rsrvStr9) || "0".equals(rsrvStr9))
		// {
		// str9Flag = true;
		// }
		// }
		// }
		// else
		// {
		// str9Flag = true;
		// }
		//
		// boolean sTypeFlag = false;
		// if ("0".equals(servType) || "1".equals(servType))
		// {
		// sTypeFlag = true;
		// }
		//
		// if (doKey && sTypeFlag && str9Flag && statusFlag)
		// {
		// /******************** TD_B_PLATSVC *************************/
		// platInfo.put("SERV_TYPE", servType);
		//
		// platInfo.put("BIZ_TYPE_CODE", bizTypeCode);
		//
		// platInfo.put("BIZ_CODE", bizCode);
		// String serviceName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME", serviceId);
		// platInfo.put("BIZ_NAME", serviceName);
		//
		// /******************** TD_M_SP_INFO *************************/
		// String spShortName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "SP_SHORT_NAME", spCode);
		// platInfo.put("SP_SHORT_NAME", spShortName);
		// String spName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "SP_NAME", spCode);
		// platInfo.put("SP_NAME", spName);
		// String servCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "SERV_CODE", spCode);
		// platInfo.put("SERV_CODE", servCode);
		// String csTel = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "CS_TEL", spCode);
		// platInfo.put("CS_TEL", csTel);
		//
		// /******************** TD_M_SP_BIZ *************************/
		// String price = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "PRICE", new String [] {spCode,bizCode});
		// if(StringUtils.isNotBlank(price))
		// {
		// platInfo.put("PRICE", Integer.valueOf(price) / 1000);
		// }
		// else
		// {
		// platInfo.put("PRICE", "0");
		// }
		//                    
		// String bizAttr = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "BIZ_ATTR", new String [] {spCode,bizCode});
		// platInfo.put("SERV_ATTR", bizAttr);
		//
		// String bizDesc = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "BIZ_DESC", new String [] {spCode,bizCode});
		// platInfo.put("BIZ_DESC", bizDesc);
		//
		// platInfo.put("TAG_CHAR", TDRsrvStr9);
		//
		// String rsrvStr10 = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "RSRV_STR10", new String [] {spCode,bizCode});
		// platInfo.put("RSRV_STR10", rsrvStr10);
		//
		// /******************** TD_B_PLATSVC_PARAM *************************/
		// String bizType = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC_PARAM", "BIZ_TYPE_CODE", "BIZ_TYPE", bizTypeCode);
		// platInfo.put("BIZ_TYPE", bizType);
		//
		// platInfo.put("SP_ID", spCode);
		//
		// if ("注册类业务".equals(platInfo.getString("SP_NAME")))
		// {
		// platInfo.put("SP_NAME", "中国移动");
		// }
		//
		// platInfo.put("BILLFLG", platInfo.getString("BILL_TYPE"));
		//
		// if ("00".equals(flag))
		// {
		// if ("-SJQB".equals(bizCode))
		// {
		// platInfo.put("TAG", "M");
		// }
		// else
		// {
		// platInfo.put("TAG", bizCode);
		// }
		// }
		//
		// if (!"04".equals(flag))
		// {
		// if ("04".equals(bizTypeCode))
		// {
		// platInfo.put("BIZ_TYPE_CODE1", "01");
		// }
		// else if ("05".equals(bizTypeCode))
		// {
		// platInfo.put("BIZ_TYPE_CODE1", "02");
		// }
		// else if ("03".equals(bizTypeCode))
		// {
		// platInfo.put("BIZ_TYPE_CODE1", "03");
		// }
		// else
		// {
		// }
		//
		// platInfo.put("SERVICE_TYPE", "Z");
		// }
		//
		// ret.add(platInfo);
		// }
		// }
		// }

		return platInfos;
	}

	private IDataset getElemsInProdModel(String productId, String tradeStaffId, String asp, String elementType) throws Exception
	{
		IDataset elems = new DatasetList();
		IDataset productElementInfos = ProductUtils.offerToElement(UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, null, null), productId);
		if (IDataUtil.isEmpty(productElementInfos))
		{
			return elems;
		}
		int productElementInfoSize = productElementInfos.size();
		OfferCfg offerCfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		String productName = "";
		String productMode = "";
		if (offerCfg != null)
		{
			productName = offerCfg.getOfferName();
			productMode = offerCfg.getProductMode();
		}
		if (ELEM_TYPE_S.equals(elementType))
		{
			if (ASP_YD.equals(asp))// 如果是移动用户
			{
				// elems = PkgElemInfoQry.getAllSvcsInProduct(productId);
				for (int i = 0; i < productElementInfoSize; i++)
				{
					IData productElementInfo = productElementInfos.getData(i);
					if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(productElementInfo.getString("ELEMENT_TYPE_CODE", "")))
					{
						productElementInfo.put("PRODUCT_NAME", productName);
						productElementInfo.put("PRODUCT_MODE", productMode);
						elems.add(productElementInfo);
					}
				}
			} else
			{
				// elems =
				// PkgElemInfoQry.getAllSvcsWithoutNpInProduct(productId);
				for (int i = 0; i < productElementInfoSize; i++)
				{
					IData productElementInfo = productElementInfos.getData(i);
					if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(productElementInfo.getString("ELEMENT_TYPE_CODE", "")))
					{
						productElementInfo.put("PRODUCT_NAME", productName);
						productElementInfo.put("PRODUCT_MODE", productMode);
						// lijun17 需过滤 如果该元素在NP表 则过滤
						IDataset npOfferInfos = UpcCall.qryOfferLimitNpByOfferId(BofConst.ELEMENT_TYPE_CODE_SVC, productElementInfo.getString("ELEMENT_ID", ""), "0");
						if (IDataUtil.isNotEmpty(npOfferInfos))
						{
							continue;
						}
						elems.add(productElementInfo);
					}
				}
			}
		} else if (ELEM_TYPE_D.equals(elementType))
		{
			if (ASP_YD.equals(asp))// 如果是移动用户
			{
				// elems = PkgElemInfoQry.getAllDicsInProduct(productId);
				for (int i = 0; i < productElementInfoSize; i++)
				{
					IData productElementInfo = productElementInfos.getData(i);
					if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(productElementInfo.getString("ELEMENT_TYPE_CODE", "")))
					{
						productElementInfo.put("PRODUCT_NAME", productName);
						productElementInfo.put("DISCNT_EXPLAIN", productElementInfo.getString("ELEMENT_EXPLAIN", ""));
						productElementInfo.put("PRODUCT_MODE", productMode);
						elems.add(productElementInfo);
					}
				}
			} else
			{
				// elems =
				// PkgElemInfoQry.getAllDicsWithoutNpInProduct(productId);
				for (int i = 0; i < productElementInfoSize; i++)
				{
					IData productElementInfo = productElementInfos.getData(i);
					if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(productElementInfo.getString("ELEMENT_TYPE_CODE", "")))
					{
						productElementInfo.put("PRODUCT_NAME", productName);
						productElementInfo.put("DISCNT_EXPLAIN", productElementInfo.getString("ELEMENT_EXPLAIN", ""));
						productElementInfo.put("PRODUCT_MODE", productMode);
						// 如果该元素在NP表 则过滤
						IDataset npOfferInfos = UpcCall.qryOfferLimitNpByOfferId(BofConst.ELEMENT_TYPE_CODE_DISCNT, productElementInfo.getString("ELEMENT_ID", ""), "0");
						if (IDataUtil.isNotEmpty(npOfferInfos))
						{
							continue;
						}
						elems.add(productElementInfo);
					}
				}
			}
		} else
		{
			// IDataset svcs = null;
			// IDataset dics = null;
			if (ASP_YD.equals(asp))// 如果是移动用户
			{
				// svcs = PkgElemInfoQry.getAllSvcsInProduct(productId);
				// dics = PkgElemInfoQry.getAllDicsInProduct(productId);
				for (int i = 0; i < productElementInfoSize; i++)
				{
					IData productElementInfo = productElementInfos.getData(i);
					productElementInfo.put("PRODUCT_NAME", productName);
					if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(productElementInfo.getString("ELEMENT_TYPE_CODE", "")))
					{
						productElementInfo.put("DISCNT_EXPLAIN", productElementInfo.getString("ELEMENT_EXPLAIN", ""));
					}
					productElementInfo.put("PRODUCT_MODE", productMode);
					elems.add(productElementInfo);
				}
			} else
			{
				// svcs =
				// PkgElemInfoQry.getAllSvcsWithoutNpInProduct(productId);
				// dics =
				// PkgElemInfoQry.getAllDicsWithoutNpInProduct(productId);
				for (int i = 0; i < productElementInfoSize; i++)
				{
					IData productElementInfo = productElementInfos.getData(i);
					productElementInfo.put("PRODUCT_NAME", productName);
					productElementInfo.put("PRODUCT_MODE", productMode);
					// lijun17 需过滤 如果该元素在NP表 则过滤
					IDataset npOfferInfos = UpcCall.qryOfferLimitNpByOfferId(productElementInfo.getString("ELEMENT_TYPE_CODE", ""), productElementInfo.getString("ELEMENT_ID", ""), "0");
					if (IDataUtil.isNotEmpty(npOfferInfos))
					{
						continue;
					}
					elems.add(productElementInfo);
				}
			}
			// if (IDataUtil.isNotEmpty(svcs))
			// {
			// elems.addAll(svcs);
			// }
			// if (IDataUtil.isNotEmpty(dics))
			// {
			// elems.addAll(dics);
			// }
		}
		ElementPrivUtil.filterElementListByPriv(tradeStaffId, elems);
		return elems;
	}

	private IDataset getElemsInUProdModel(String productId, String userId, String elementType, String offerInstId) throws Exception
	{
		IDataset elems = new DatasetList();
		if (ELEM_TYPE_S.equals(elementType))
		{
			// 海南第三代订单中心改造 IDataset userSvcs =
			// UserSvcInfoQry.queryUserNormalSvc(userId, productId);
			IDataset userSvcs = UserSvcInfoQry.queryUserNormalSvcNow(userId, productId, offerInstId);
			// 海南第三代订单中心改造 IDataset userSpecSvcs =
			// UserSvcInfoQry.queryUserSpecSvc(userId, productId);
			elems.addAll(userSvcs);
			// elems.addAll(userSpecSvcs);
		} else if (ELEM_TYPE_D.equals(elementType))
		{
			// 海南第三代订单中心改造 IDataset userNormalDics =
			// UserDiscntInfoQry.queryUserNormalDic(userId, productId);
			IDataset userNormalDics = UserDiscntInfoQry.queryUserNormalDicNow(userId, productId, offerInstId);
			// 海南第三代订单中心改造 IDataset userSpecDics =
			// UserDiscntInfoQry.queryUserSpecDic(userId, productId);
			elems.addAll(userNormalDics);
			// elems.addAll(userSpecDics);
		} else
		{
			IDataset userNormalSvcs = UserSvcInfoQry.queryUserNormalSvcNow(userId, productId, offerInstId);
			// IDataset userSpecSvcs = UserSvcInfoQry.queryUserSpecSvc(userId,
			// productId, offerInstId);
			IDataset userNormalDics = UserDiscntInfoQry.queryUserNormalDicNow(userId, productId, offerInstId);
			// IDataset userSpecDics =
			// UserDiscntInfoQry.queryUserSpecDic(userId, productId,
			// offerInstId);
			elems.addAll(userNormalSvcs);
			// elems.addAll(userSpecSvcs);
			elems.addAll(userNormalDics);
			// elems.addAll(userSpecDics);
		}

		// TODO: 老系统有这么一段， IDataset
		// userElement=bean.combineElementAttr(result,data);
		/**
		 * REQ201601020002 集团业务相关优惠在网厅界面只展示不可操作取消的优化 chenxy3 2016-03-01
		 * PRODUCT_MODE=12 或者 13的，则要求FORCE_TAG=1 根据坤威要求增加：
		 * 优化已订购套餐查询接口，该接口返回的FORCE_TAG字段信息
		 * ，要把4G自选套餐中的语音和流量套餐设置为1必选优惠，网厅就能自动屏蔽调取消套餐按钮
		 * */
		for (int i = 0; i < elems.size(); i++)
		{
			IData elm = elems.getData(i);
			String prodId = elm.getString("PRODUCT_ID", "");
			String prodMode = "";// 上面语句里返回的PRODUCT_MODE是错的。要重新取
			if ("-1".equals(prodId))
			{
				prodMode = elm.getString("PRODUCT_MODE", "");
			} else
			{
				String offerType = elm.getString("OFFER_TYPE", "");
				IDataset prodsets = new DatasetList();
				if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerType))
				{
					prodsets = ProductInfoQry.getProductInfoByid(prodId);
					if (prodsets != null && prodsets.size() > 0)
					{
						prodMode = prodsets.getData(0).getString("PRODUCT_MODE");
					} else
					{
						prodMode = elm.getString("PRODUCT_MODE", "");
					}
				} else if (BofConst.ELEMENT_TYPE_CODE_PACKAGE.equals(offerType))
				{
					String packageId = elm.getString("PACKAGE_ID", "");
					if (StringUtils.isNotBlank(packageId))
					{
						try
						{
							OfferCfg offercfg = OfferCfg.getInstance(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
							if (offercfg != null)
							{
								prodMode = offercfg.getProductMode();
							} else
							{
								prodMode = elm.getString("PRODUCT_MODE", "");
							}
						} catch (Exception e)
						{
							// TODO: handle exception
							prodMode = elm.getString("PRODUCT_MODE", "");
						}

					} else
					{
						prodMode = elm.getString("PRODUCT_MODE", "");
					}

				}

			}
			IDataset limitModes = CommparaInfoQry.getCommPkInfo("CSM", "1561", prodMode, "0898");// 配置，目前是12、13类型的
			if (limitModes != null && limitModes.size() > 0)
			{
				elems.getData(i).put("FORCE_TAG", "1");
			}
			if ("D".equals(elems.getData(i).getString("ELEMENT_TYPE_CODE", "-1")))
			{
				IDataset userDiscnt = CommparaInfoQry.getCommPkInfo("CSM", "156", elems.getData(i).getString("ELEMENT_ID", "-1"), "0898");
				if (userDiscnt != null && userDiscnt.size() > 0)
				{
					elems.getData(i).put("FORCE_TAG", "1");
				}
			}
		}

		return elems;
	}

	public IDataset getFilteIncomePhoneDs(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = input.getString("SERIAL_NUMBER", "");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isNotEmpty(userInfo))
		{
			return UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userInfo.getString("USER_ID", ""), "1301");
		}
		return rets;
	}

	/**
	 * 获取拼串
	 * 
	 * @author zhuyu
	 * @param s
	 * @return
	 */
	public String getIBossString(String s)
	{
		return IBOSS_PREX + s;
	}

	/**
	 * 手机营业厅-个人信息查询
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 * @date 2014-06-24 15:59:15
	 */
	public IData getMobileUserInfo(IData input) throws Exception
	{
		String idType = input.getString("IDTYPE");
		String idValue = input.getString("IDVALUE");
		String oprNumb = input.getString("OPR_NUMB");
		String bizTypeCode = input.getString("BIZ_TYPE_CODE");

		if (StringUtils.isEmpty(idType))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDTYPE");
		}
		if (StringUtils.isEmpty(idValue))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDVALUE");
		}
		if (StringUtils.isEmpty(oprNumb))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "OPR_NUMB");
		}
		if (StringUtils.isEmpty(bizTypeCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "BIZ_TYPE_CODE");
		}

		IData result = new DataMap();

		UcaData ucaData = UcaDataFactory.getNormalUca(idValue);
		UserTradeData userInfo = ucaData.getUser();
		String custType = ucaData.getCustomer().getCustType();

		// 用户姓名
		String userName = "";
		// Email地址
		String email = "";
		// 邮寄地址
		String userAdd = "";
		// 邮政编码
		String zipCode = "";
		// 联系电话
		String userNum = "";

		if (StringUtils.equals("0", custType))
		{// 个人客户
			CustPersonTradeData custPersonInfo = ucaData.getCustPerson();
			userName = custPersonInfo.getCustName();
			email = custPersonInfo.getEmail();
			userAdd = custPersonInfo.getPostAddress();
			zipCode = custPersonInfo.getPostCode();
			userNum = custPersonInfo.getPhone();
		}

		if (StringUtils.equals("1", custType))
		{// 集团客户
			CustGroupTradeData custGroupInfo = ucaData.getCustGroup();
			userName = custGroupInfo.getCustName();
			email = custGroupInfo.getEmail();
			zipCode = custGroupInfo.getPostCode();
		}

		// 用户品牌
		String userBrand = ucaData.getBrandCode();
		if (StringUtils.isEmpty(userBrand))
		{
			userBrand = "09";
		}
		if ("G001".equals(userBrand))
		{
			userBrand = "01";
		} else if ("G002".equals(userBrand))
		{
			userBrand = "02";
		} else if ("G010".equals(userBrand))
		{
			userBrand = "03";
		} else
		{
			userBrand = "09";
		}

		// 用户状态
		String userStatus = InterfaceUtil.getUserState4Mobile(userInfo.getUserStateCodeset());
		if (StringUtils.isBlank(userStatus))
		{
			userStatus = "00";
		}

		String userLevel = "";// 客户等级
		VipTradeData vipInfo = ucaData.getVip();
		if (null == vipInfo)
		{
			result.put("USER_ID", "00");// 用户标识 00-普通用户 01-VIP用户
		} else
		{
			String vipTypeCode = vipInfo.getVipTypeCode();// VIP类型编码
			String vipClassId = vipInfo.getVipClassId();// VIP级别标识
			userLevel = vipClassId;
			// 为什么这么判断？
			if ("5".equals(vipTypeCode) || ("0".equals(vipTypeCode) && !"1".equals(vipClassId)))
			{
				result.put("USER_ID", "01");

				String vipNo = vipInfo.getVipCardNo();// VIP卡号

				String vipLevel = vipClassId;// VIP级别
				if ("1".equals(vipLevel))
				{
					vipLevel = "";// 贵宾卡
				} else if ("2".equals(vipLevel))
				{
					vipLevel = "02";// 银卡
				} else if ("3".equals(vipLevel))
				{
					vipLevel = "01";// 金卡
				} else if ("4".equals(vipLevel))
				{
					vipLevel = "00";// 钻石卡
				}

				String vipEndDate = vipInfo.getIdentityExpDate();
				if (StringUtils.isBlank(vipEndDate))
				{
					vipEndDate = SysDateMgr.getTheLastTime();
				}
				vipEndDate = SysDateMgr.getDateForYYYYMMDD(vipEndDate);// VIP结束时间

				String vipManagerSN = "";// 客户经理联系方式
				String custManagerId = vipInfo.getCustManagerId();// 客户经理ID
				IData custManagerInfo = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custManagerId);
				if (IDataUtil.isNotEmpty(custManagerInfo))
				{
					vipManagerSN = custManagerInfo.getString("SERIAL_NUMBER", "");
				}

				result.put("VIP_LEVEL", vipLevel);// VIP等级
				result.put("VIP_NUMBER", vipNo);// VIP卡号
				result.put("VIP_DATE", vipEndDate);// VIP有效期
				result.put("CUSTOMER_INFO", vipManagerSN);// 客户经理联系方式
				result.put("PLAN_NUMBER", "0");// 机场VIP免费次数
			} else
			{
				result.put("USER_ID", "00");
			}
		}

		// 客户等级转换 老系统存在ABCDE的等级 需确认？
		if ("0".equals(userLevel))
			userLevel = "300";
		else if ("1".equals(userLevel))
			userLevel = "304";
		else if ("2".equals(userLevel))
			userLevel = "303";
		else if ("3".equals(userLevel))
			userLevel = "302";
		else if ("4".equals(userLevel))
			userLevel = "301";
		else if ("A".equals(userLevel))
			userLevel = "302";
		else if ("B".equals(userLevel))
			userLevel = "301";
		else if ("C".equals(userLevel))
			userLevel = "303";
		else if ("D".equals(userLevel))
			userLevel = "302";
		else if ("E".equals(userLevel))
			userLevel = "301";
		else
			userLevel = "100";

		// 入网时间，时间工具类没有获取yyyyMMddHHmmss这种格式的方法
		String userBegin = userInfo.getOpenDate().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		;// 老系统是取的inDate

		 //客户类型--移动商城2.5
        custType=getCustType(custType);
        result.put("USER_TYPE", custType);
        
       
        String newUserAdd = "";
        
        // 移动商城2.8 用户邮寄地址取值说明，进行敏感字段的模糊化处理 add by huangyq
		//change by liangdg3 at20190920 for BUG20190916170200 BIP3A206个人资料查询接口异常问题 邮寄地址长度未做判断导致字符串分割异常
		//增加条件邮寄地址长度小于4个字符不做模糊化
        if(StringUtils.isNotBlank(userAdd)){
			if(userAdd.length()>4){
				newUserAdd = userAdd.substring(0,2)+"***"+userAdd.substring(userAdd.length()-2, userAdd.length());
			}else{
				newUserAdd = userAdd;
			}
        }
		

		result.put("USER_NAME", userName);// 客户姓名需要模糊化：StringUtilForIntf.blurCustomerNameNewRule
		result.put("USER_BRAND", userBrand);
		result.put("USER_LEVEL", userLevel);
		result.put("USER_STATUS", userStatus);
		result.put("USER_BEGIN", userBegin);
		result.put("EMAIL", email);
		result.put("USER_ADD", newUserAdd);
		result.put("USER_ADD1", newUserAdd);
		result.put("ZIP_CODE", zipCode);
		result.put("USER_NUM", userNum);
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		// 移动商城1.5添加，将客户ID保存到结果中
		result.put("CUST_ID", userInfo.getCustId());
		result.put("USER_UNIQUE", userInfo.getUserId());// 因上面代码以用USER_ID字段，此处另起名称

		// 移动商城1.6添加
		// 1、4G用户标识字段。0：是4G 1：非4G，
		String userId = userInfo.getUserId();
		List<ResTradeData> resInfos = ucaData.getUserAllRes();
		String simCardNo = "";
		for (ResTradeData resInfo : resInfos)
		{
			if ("1".equals(resInfo.getResTypeCode()))
				simCardNo = resInfo.getResCode();
		}
		IDataset simCardInfos = ResCall.getSimCardInfo("0", simCardNo, "", "1");
		IDataset reSet =null;
		if(IDataUtil.isNotEmpty(simCardInfos))
		reSet=ResCall.qrySimCardTypeByTypeCode(simCardInfos.getData(0).getString("RES_TYPE_CODE"));
		
		if (IDataUtil.isNotEmpty(reSet))
		{
			String netTypeCode = reSet.getData(0).getString("NET_TYPE_CODE", "");
			String parentTypeCode = reSet.getData(0).getString("PARENT_TYPE_CODE", "");
			if ("01".equals(netTypeCode) && "1".equals(parentTypeCode))
			{
				result.put("4G_FLAG", "0");// 4G卡
			} else
			{
				result.put("4G_FLAG", "1");// 非4G
			}
		} else
		{
			result.put("4G_FLAG", "1");// 非4G
		}
		
		//判断是否5G  目前只能根据是否有5G服务判断  REQ202002040006 移动商城统一接口平台V3.0版规范（新版账单查询）省份改造通知
		IData servicedate=new DataMap();
		servicedate.put("USER_ID", userId);
		String services_5G="0";
		IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_NOW", servicedate, BizRoute.getRouteId());
		IDataset CurConfig = CommparaInfoQry.getCommparaCode1("CSM", "420", "YDSC","0898");
		
		if (IDataUtil.isNotEmpty(userSvcs)){
			for(int i=0;i<userSvcs.size();i++){
				String serviceid=userSvcs.getData(i).getString("SERVICE_ID","1");
				
				if (IDataUtil.isNotEmpty(CurConfig)) {
		        	for(int a=0;a<CurConfig.size();a++){
		        		services_5G=CurConfig.getData(a).getString("PARA_CODE1","0");
		        			if(services_5G.equals(serviceid)){
							 result.put("4G_FLAG", "2");// 5G标识
							 break;
			                 }
		        	}
		        }
				
			}
		}
        
        
		
		
		// 2、VOLTE标识字段
		IDataset userSet = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "190");
		if (IDataUtil.isNotEmpty(userSet))
		{
			result.put("VOLTE_FLAG", "0");// 是4G
		} else
		{
			result.put("VOLTE_FLAG", "1");// 非4G
		}
		// 3、 AccoutDay 出账日标识字段
		IDataset userAcctDayInfo = UserAcctDayInfoQry.getUserAcctDays(userId, "");
		if (IDataUtil.isNotEmpty(userAcctDayInfo))
		{
			result.put("ACCOUT_DAY", userAcctDayInfo.getData(0).getString("ACCT_DAY"));
		} else
		{
			result.put("ACCOUT_DAY", "1");
		}
		return result;
	}
	private String getCustType(String param)
    {
    	String result = "";
    	if("0".equals(param)){
    		result = "00";//个人客户
    	}else if("1".equals(param)){
    		result = "01";//集团客户
    	}
    	 return result;
    }

	public IData getMpayInfo(IData input) throws Exception
	{
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData userInfo = getUserInfo4SingleAll(serialNumber);

		String userId = userInfo.getString("USER_ID");

		IData result = new DataMap();
		result.put("USER_ID", userId);

		IDataset svc = PlatSvcInfoQry.qryPlatSvc4All("698000", "00000001", "54");
		if (IDataUtil.isEmpty(svc))
		{
			result.put("IDVALUE", serialNumber);
			result.put("BIZ_TYPE_CODE", "54");
			result.put("BIZ_STATE_CODE", "2");
		} else
		{
			for (int i = 0, size = svc.size(); i < size; i++)
			{
				IData svcInfo = svc.getData(i);

				IDataset svcTemp = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, svcInfo.getString("SERVICE_ID"));

				if (IDataUtil.isEmpty(svcTemp))
				{
					result.put("IDVALUE", serialNumber);
					result.put("BIZ_TYPE_CODE", "54");
					result.put("BIZ_STATE_CODE", "2");
				} else
				{
					String sTmp = svcTemp.getData(0).getString("BIZ_STATE_CODE");

					result.put("IDVALUE", svcTemp.getData(0).getString("SERIAL_NUMBER"));
					result.put("BIZ_TYPE_CODE", svcTemp.getData(0).getString("BIZ_TYPE_CODE"));

					if ("A".equals(sTmp))
						result.put("BIZ_STATE_CODE", "0");
					else if ("N".equals(sTmp))
						result.put("BIZ_STATE_CODE", "1");
					else if ("E".equals(sTmp))
						result.put("BIZ_STATE_CODE", "2");
					else
						result.put("BIZ_STATE_CODE", "2");
				}
			}
		}

		return result;
	}

	/**
	 * 获取移动号码归属地州 ITF_CRM_MphoneAddress
	 * 
	 * @param SERIAL_NUMBER
	 * @return EPARCHY_CODE
	 * @throws Exception
	 * @author huanghui@asiainfo-linkage.com
	 */
	public IDataset getMsisdnCityInfo(IData param) throws Exception
	{
		IDataset resultList = new DatasetList();
		IData params = new DataMap();
		String msisdn = param.getString("SERIAL_NUMBER", "");
		if (StringUtils.isBlank(msisdn))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
		}
		params.put("SERIAL_NUMBER", msisdn);
		resultList = MofficeInfoQry.getMsisdnCityInfo(msisdn);
		if (IDataUtil.isEmpty(resultList))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1043);
		}
		return resultList;
	}

	public IDataset getPlatOrderInfo(String userId, String flag) throws Exception
	{
		IDataset platInfos = new DatasetList();
		// 生效的订购关系
		if ("00".equals(flag))
		{
			platInfos = PlatSvcInfoQry.queryPlatOrderInfobyUserId00(userId);
			if (IDataUtil.isNotEmpty(platInfos))
			{
				for (int i = 0; i < platInfos.size(); i++)
				{
					IData platInfo = platInfos.getData(i);
					String serviceId = platInfo.getString("SERVICE_ID", "");
					IDataset spInfos = new DatasetList();
					try
					{
						spInfos = UpcCall.qrySpServiceSpInfo(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
					} catch (Exception e)
					{

					}
					if (IDataUtil.isNotEmpty(spInfos))
					{
						platInfo.putAll(spInfos.getData(0));
						String spName = "注册类业务".equals(platInfo.getString("SP_NAME", "")) ? "中国移动" : platInfo.getString("SP_NAME", "");
						platInfo.put("SP_NAME", spName);
						platInfo.put("BILLFLG", platInfo.getString("BILL_TYPE", ""));
						platInfo.put("SERV_ATTR", platInfo.getString("BIZ_ATTR", ""));
					}
				}
			}
		}
		// 历史定购信息
		else if ("01".equals(flag))
		{
			platInfos = PlatSvcInfoQry.queryPlatOrderInfobyUserId01(userId);
			if (IDataUtil.isNotEmpty(platInfos))
			{
				for (int i = 0; i < platInfos.size(); i++)
				{
					IData platInfo = platInfos.getData(i);
					String serviceId = platInfo.getString("SERVICE_ID", "");
					IDataset spInfos = new DatasetList();
					try
					{
						spInfos = UpcCall.qrySpServiceSpInfo(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
					} catch (Exception e)
					{

					}
					if (IDataUtil.isNotEmpty(spInfos))
					{
						platInfo.putAll(spInfos.getData(0));
						String spName = "注册类业务".equals(platInfo.getString("SP_NAME", "")) ? "中国移动" : platInfo.getString("SP_NAME", "");
						platInfo.put("SP_NAME", spName);
						platInfo.put("BILLFLG", platInfo.getString("BILL_TYPE", ""));
						platInfo.put("SERV_ATTR", platInfo.getString("BIZ_ATTR", ""));
					}
				}
			}
		}
		// 全部订购关系
		else if ("02".equals(flag))
		{
			platInfos = PlatSvcInfoQry.queryPlatOrderInfobyUserId02(userId);
			if (IDataUtil.isNotEmpty(platInfos))
			{
				for (int i = 0; i < platInfos.size(); i++)
				{
					IData platInfo = platInfos.getData(i);
					String serviceId = platInfo.getString("SERVICE_ID", "");
					IDataset spInfos = new DatasetList();
					try
					{
						spInfos = UpcCall.qrySpServiceSpInfo(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
					} catch (Exception e)
					{

					}
					if (IDataUtil.isNotEmpty(spInfos))
					{
						platInfo.putAll(spInfos.getData(0));
						String spName = "注册类业务".equals(platInfo.getString("SP_NAME", "")) ? "中国移动" : platInfo.getString("SP_NAME", "");
						platInfo.put("SP_NAME", spName);
						platInfo.put("BILLFLG", platInfo.getString("BILL_TYPE", ""));
						platInfo.put("SERV_ATTR", platInfo.getString("BIZ_ATTR", ""));
					}
				}
			}
		}

		// IDataset ret = new DatasetList();
		// if (IDataUtil.isNotEmpty(platInfos))
		// {
		// for (int i = 0, size = platInfos.size(); i < size; i++)
		// {
		// IData platInfo = platInfos.getData(i);
		// /******************** TD_B_PLATSVC *************************/
		// String servType = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "SERV_TYPE",
		// platInfo.getString("SERVICE_ID"));
		//
		// if ("0".equals(servType) || "1".equals(servType))
		// {
		// platInfo.put("SERV_TYPE", servType);
		//
		// String spCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "SP_CODE",
		// platInfo.getString("SERVICE_ID"));
		// String bizTypeCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "BIZ_TYPE_CODE",
		// platInfo.getString("SERVICE_ID"));
		// platInfo.put("BIZ_TYPE_CODE", bizTypeCode);
		// String bizCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "BIZ_CODE",
		// platInfo.getString("SERVICE_ID"));
		// platInfo.put("BIZ_CODE", bizCode);
		//
		// String serviceName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME",
		// platInfo.getString("SERVICE_ID"));
		// platInfo.put("BIZ_NAME", serviceName);
		//
		// /******************** TD_M_SP_INFO *************************/
		// String spShortName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "SP_SHORT_NAME", spCode);
		// platInfo.put("SP_SHORT_NAME", spShortName);
		// String spName = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "SP_NAME", spCode);
		// platInfo.put("SP_NAME", spName);
		// String servCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "SERV_CODE", spCode);
		// platInfo.put("SERV_CODE", servCode);
		// String csTel = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_INFO", "SP_CODE", "CS_TEL", spCode);
		// platInfo.put("CS_TEL", csTel);
		//
		// /******************** TD_M_SP_BIZ *************************/
		// String price = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "PRICE", new String [] {spCode,bizCode});
		// platInfo.put("PRICE", price);
		// String bizAttr = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_M_SP_BIZ", new String [] {"SP_CODE","BIZ_CODE"}
		// , "BIZ_ATTR", new String [] {spCode,bizCode});
		// platInfo.put("SERV_ATTR", bizAttr);
		//
		// /******************** TD_B_PLATSVC_PARAM *************************/
		// String bizType = StaticUtil.getStaticValue(CSBizBean.getVisit(),
		// "TD_B_PLATSVC_PARAM", "BIZ_TYPE_CODE", "BIZ_TYPE", bizTypeCode);
		// platInfo.put("BIZ_TYPE", bizType);
		//
		// platInfo.put("SP_ID", spCode);
		//
		// if ("注册类业务".equals(platInfo.getString("SP_NAME")))
		// {
		// platInfo.put("SP_NAME", "中国移动");
		// }
		//
		// platInfo.put("BILLFLG", platInfo.getString("BILL_TYPE"));
		//
		// ret.add(platInfo);
		// }
		// }
		// }

		return platInfos;
	}

	/**
	 * add by ouyk
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset getPlatsvcCountByCond(IData input) throws Exception
	{
		String userId = input.getString("USER_ID");
		String spCode = input.getString("SP_CODE");
		String bizCode = input.getString("BIZ_CODE");
		return UserPlatSvcInfoQry.queryPlatsvcCountByUserIdSpCodeBizCode(userId, spCode, bizCode);// 不需要返回值，返回一个空的Idataset
	}

	public IDataset getRealNameUserCount(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		IData ret0 = new DataMap();
		String serialNumber = input.getString("SERIAL_NUMBER", "");
		if (StringUtils.isBlank(serialNumber))
		{
			ret0.put("REALNAME_USER_COUNT", "0");
			ret0.put("X_RESULTCODE", "1");
			ret0.put("X_RESULTINFO", "用户号码不能为空！");
			ret0.put("X_RSPTYPE", "2");
			ret0.put("X_RSPCODE", "2998");
			rets.add(ret0);
			return rets;
		}

		IDataset custs = CustomerInfoQry.queryCustInfoBySN(serialNumber);
		if (IDataUtil.isNotEmpty(custs))
		{
			String psptId = custs.getData(0).getString("PSPT_ID", "0");
			String custName = custs.getData(0).getString("CUST_NAME", "0");
			String isRealName = custs.getData(0).getString("IS_REAL_NAME", CUST_REALNAME_NO);
			if (CUST_REALNAME_YES.equals(isRealName))
			{
				int realCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);
				ret0.put("REALNAME_USER_COUNT", realCount);
				ret0.put("X_RESULTCODE", "0");
				ret0.put("X_RESULTINFO", "OK!");
				ret0.put("REALNAME_USER_TAG", CUST_REALNAME_YES);
				ret0.put("CUST_NAME", custName);
				ret0.put("PSPT_ID", psptId);
			} else
			{
				ret0.put("CUST_NAME", custName);
				ret0.put("PSPT_ID", psptId);
				ret0.put("REALNAME_USER_TAG", "0");
				ret0.put("REALNAME_USER_COUNT", CUST_REALNAME_NO);
				ret0.put("X_RESULTCODE", "0");
				ret0.put("X_RESULTINFO", "该用户不是实名制用户！");
			}
		} else
		{
			ret0.put("CUST_NAME", "未知用户");
			ret0.put("PSPT_ID", "0");
			ret0.put("REALNAME_USER_TAG", "9");
			ret0.put("REALNAME_USER_COUNT", "0");
			ret0.put("X_RESULTCODE", "0");
			ret0.put("X_RESULTINFO", "查询该用户号码无数据！");
		}

		rets.add(ret0);
		return rets;
	}

	public IDataset GetSPServiceInfo(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SP_ID");
		IDataUtil.chkParam(input, "SP_SVC_ID");
		if (input.getString("SP_ID", "").indexOf(",") > 0)
		{
			return getSPServiceInfoMore(input);
		} else
		{
			IDataset result = new DatasetList();

			result = PlatInfoQry.queryPlatSVCInfoBySPCode(input.getString("SP_ID"), input.getString("SP_SVC_ID"));

			result = DataHelper.distinct(result, "SERVICE_ID", ""); // 去重

			return result;
		}

	}

	/**
	 * 提供给电子渠道的接口,电子渠道传多个sp_id,以逗号分隔,查出这些sp_id的具体信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset getSPServiceInfoMore(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SP_ID");
		String spId = input.getString("SP_ID", "");

		// 多个sp_id之间以逗号分隔
		String[] spIds = spId.split(",");
		IDataset results = new DatasetList();
		for (int i = 0; i < spIds.length; i++)
		{
			IData inParam = new DataMap();
			IDataset qryResults = new DatasetList();
			if (!"SUPERUSR".equals(input.getString("TRADE_STAFF_ID")))
			{
				qryResults = PlatInfoQry.queryPlatSVCInfoByStaffSPServiceInfo(spIds[i], input.getString("TRADE_STAFF_ID"));
			} else
			{
				qryResults = PlatInfoQry.queryPlatSVCInfoBySPCode(spIds[i], null);
			}
			results.addAll(qryResults);
		}

		return results;
	}

	/**
	 * 获取各等级大客户相应的可使用的免费次数
	 * 
	 * @param pd
	 * @param td
	 * @return totalFreeCount
	 * @throws Exception
	 */
	public int getTotalFreeCount(String vip_type_code, String vip_class_id, String product_id) throws Exception
	{
		int totalFreeCount = 0;
		if ("0".equals(vip_type_code) || "2".equals(vip_type_code) || "5".equals(vip_type_code))
		{
			if ("4".equals(vip_class_id))
			{ // 钻卡，最多免费次数默认12
				totalFreeCount = 12;
			} else if ("3".equals(vip_class_id))
			{// 金卡，最多免费次数默认6
				totalFreeCount = 6;
			} else if ("2".equals(vip_class_id))
			{// 银卡，最多免费次数默认0
				totalFreeCount = 0;
			} else
			{
				totalFreeCount = 0;
			}
		}
		if (!"".equals(product_id))
		{// 产品判断，最终次数默认可以提供的最大值
			if ("10007609".equals(product_id) || "10007633".equals(product_id))
			{ // 全球通商旅、上网288，次数3
				totalFreeCount = 3 > totalFreeCount ? 3 : totalFreeCount;
			} else if ("10007610".equals(product_id) || "10007634".equals(product_id))
			{// 全球通商旅、上网388，次数6
				totalFreeCount = 6 > totalFreeCount ? 6 : totalFreeCount;
			} else if ("10007611".equals(product_id) || "10007635".equals(product_id))
			{// 全球通商旅、上网588，次数6
				totalFreeCount = 6 > totalFreeCount ? 6 : totalFreeCount;
			} else if ("10007612".equals(product_id) || "10007636".equals(product_id))
			{// 全球通商旅、上网888，次数12
				totalFreeCount = 12 > totalFreeCount ? 12 : totalFreeCount;
			} else
			{
			}
		}

		return totalFreeCount;
	}

	public IDataset getTrainTradeInfo(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "CHANNEL_TRADE_ID");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String channelTradeId = input.getString("CHANNEL_TRADE_ID");

		IDataset trainInfos = TradeInfoQry.getTrainTradeInfo(serialNumber, channelTradeId);

		IDataset result = new DatasetList();
		if (IDataUtil.isNotEmpty(trainInfos))
		{
			IData trainInfo = trainInfos.getData(0);
			if (trainInfos.size() == 1 && "0".equals(trainInfo.get("CANCEL_TAG")))
			{
				IData tempInfo = new DataMap();
				tempInfo.put("SERIAL_NUMBER", serialNumber);
				tempInfo.put("TRADE_ID", trainInfo.get("TRADE_ID"));
				tempInfo.put("CHANNEL_TRADE_ID", channelTradeId);
				tempInfo.put("ACCEPT_TIME", trainInfo.getString("RSRV_STR5"));
				tempInfo.put("X_RESULTCODE", "0");
				tempInfo.put("X_RESULTINFO", "兑换成功");
				result.add(tempInfo);
			} else
			{
				for (int j = 0; j < trainInfos.size(); j++)
				{
					IData tempInfo = new DataMap();
					IData trainInfo2 = trainInfos.getData(j);
					if (!"1".equals(trainInfo2.get("CANCEL_TAG")))
					{
						continue;
					} else
					{
						tempInfo.put("SERIAL_NUMBER", serialNumber);
						tempInfo.put("TRADE_ID", trainInfo2.get("TRADE_ID"));
						tempInfo.put("CHANNEL_TRADE_ID", channelTradeId);
						tempInfo.put("ACCEPT_TIME", trainInfo2.getString("RSRV_STR5"));
						tempInfo.put("X_RESULTCODE", "2");
						tempInfo.put("X_RESULTINFO", "订单已经返销");
						tempInfo.put("X_RSPTYPE", "2");
						tempInfo.put("X_RSPCODE", "2998");
					}
					result.add(tempInfo);
				}
			}
		} else
		{
			IData tempInfo = new DataMap();
			tempInfo.put("SERIAL_NUMBER", serialNumber);
			tempInfo.put("TRADE_ID", "");
			tempInfo.put("CHANNEL_TRADE_ID", channelTradeId);
			tempInfo.put("DEDUCT_TIME", "");
			tempInfo.put("X_RESULTCODE", "1");
			tempInfo.put("X_RESULTINFO", "没有数据");
			tempInfo.put("X_RSPTYPE", "2");
			tempInfo.put("X_RSPCODE", "2998");

			result.add(tempInfo);
		}

		return result;
	}

	// 获取三户资料
	public IDataset getUCAInfobySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put(StrUtil.getNotFuzzyKey(), true);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("TRADE_TYPE_CODE", PersonConst.TRADE_TYPE_CODE_CREATE_PERSON_USER);
		IDataset datasetUca = CSAppCall.call("CS.GetInfosSVC.getUCAInfos", param);
		return datasetUca;
	}

	public IDataset getUserCityInfo(IData input) throws Exception
	{

		IDataset rets = new DatasetList();
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = userInfo.getString("USER_ID");
		String cityCode = "";
		IDataset userCitys = UserInfoQry.qryCityInfoByUserId(userId);
		if (IDataUtil.isNotEmpty(userCitys))
		{
			cityCode = userCitys.getData(0).getString("CITY_CODE", "");
		}

		if (StringUtils.isBlank(cityCode))
		{
			cityCode = userInfo.getString("CITY_CODE", "");
		}

		DataMap ret0 = new DataMap();
		ret0.put("USER_ID", userId);
		ret0.put("SERIAL_NUMBER", serialNumber);
		ret0.put("CITY_CODE", cityCode);
		rets.add(ret0);
		return rets;
	}

	/**
	 * @des 三户资料查询
	 * @author huangsl
	 * @param input
	 *            SERIAL_NUMBER USER_ID CUST_ID ACCT_ID VIP_NO
	 *            X_GETMODE(0-输入服务号码取正常用户 1-输入用户标识 2-输入客户标识取正常用户 3-输入服务号码取所有用户
	 *            4-输入服务号码取所有非正常用户 5-输入帐户标识取正常用户[不支持这种方式] 6-输入VIP卡号取正常用户
	 *            7-输入服务号码取最后销户用户[该号码必须无正常用户]
	 * @return
	 * @throws Exception
	 */
	public IDataset getUserCustAcct(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		int getMode = input.getInt("X_GETMODE");
		String userId = input.getString("USER_ID", "");
		String custId = input.getString("CUST_ID", "");
		String serialNumber = input.getString("SERIAL_NUMBER", "");
		IDataset users = null;
		if (getMode == 0)
		{// 0-输入服务号码取正常用户
			if (StringUtils.isBlank(serialNumber))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
			}
			users = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(serialNumber));
		} else if (getMode == 1)
		{// 1-输入用户标识
			if (StringUtils.isBlank(userId))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "USER_ID");
			}
			users = IDataUtil.idToIds(UcaInfoQry.qryUserInfoByUserId(userId));
		} else if (getMode == 2)
		{// 2-输入客户标识取正常用户
			if (StringUtils.isBlank(custId))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "CUST_ID");
			}
			users = UserInfoQry.getUserInfoByCstId(custId, null);
		} else if (getMode == 3)
		{// 3-输入服务号码取所有用户
			if (StringUtils.isBlank(serialNumber))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
			}
			users = UserInfoQry.queryAllUserInfoBySn(serialNumber);
		} else if (getMode == 4)
		{// 4-输入服务号码取所有非正常用户
			if (StringUtils.isBlank(serialNumber))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
			}
			users = UserInfoQry.getUserInfoBySnDestroyAll(serialNumber, null);
		} else if (getMode == 5)
		{// 5-输入帐户标识取正常用户[不支持这种方式]
			CSAppException.apperr(CrmCommException.CRM_COMM_1113);
			return null;
		} else if (getMode == 6)
		{// 6-输入VIP卡号取正常用户
			if (StringUtils.isBlank(input.getString("VIP_NO")))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "VIP_NO");
			}
			users = CustVipInfoQry.queryVipInfoByVipNo("0", input.getString("VIP_NO"));
		} else if (getMode == 7)
		{// 7-输入服务号码取最后销户用户[该号码必须无正常用户]
			if (StringUtils.isBlank(serialNumber))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
			}
			users = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1113);
			return null;
		}

		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		int usersSize = users.size();
		for (int i = 0; i < usersSize; i++)
		{
			if (users.getData(i) == null || StringUtils.isBlank(users.getData(i).getString("USER_ID")))
			{
				continue;
			}
			IData result = getUserCustAcctInfo(users.getData(i).getString("USER_ID"));
			results.add(result);
		}
		int resultsSize = results.size();
		for (int i = 0; i < resultsSize; i++)
		{
			IData tmp = results.getData(i);
			if (IDataUtil.isEmpty(tmp))
			{
				continue;
			}
			tmp.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(tmp.getString("BRAND_CODE")));
			tmp.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(tmp.getString("PRODUCT_ID")));
			tmp.put("X_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("EPARCHY_CODE")));
			tmp.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("CITY_CODE")));
			tmp.put("USER_TYPE", UUserTypeInfoQry.getUserTypeByUserTypeCode(tmp.getString("USER_TYPE_CODE")));

			String acctTag = tmp.getString("ACCT_TAG");
			if ("0".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "正常处理");
			else if ("1".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "定时激活");
			else if ("2".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "待激活用户");
			else if ("Z".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "不出帐");
			else
				tmp.put("X_ACCT_TAG_NAME", "未知出账标志");

			String prepayTag = tmp.getString("PREPAY_TAG");
			if ("0".equals(prepayTag))
				tmp.put("X_PREPAY_TAG_NAME", "后付费");
			else if ("1".equals(prepayTag))
				tmp.put("X_PREPAY_TAG_NAME", "预付费");
			else
				tmp.put("X_PREPAY_TAG_NAME", "未知预付费标志");

			String removeTag = tmp.getString("REMOVE_TAG");
			if ("0".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "正常");
			else if ("1".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "主动预销号");
			else if ("2".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "主动销号");
			else if ("3".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "欠费预销号");
			else if ("4".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "欠费销号");
			else if ("5".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "开户返销");
			else if ("6".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "过户注销");
			else
				tmp.put("X_REMOVE_TAG_NAME", "未知注销标志");

			String openMode = tmp.getString("OPEN_MODE");
			if ("0".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "正常");
			else if ("1".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "预开未返单");
			else if ("2".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "预开已返单");
			else if ("3".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "过户新增");
			else if ("4".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "当日返单并过户");
			else
				tmp.put("X_OPEN_MODE_NAME", "未知开户方式标志");

			// 获取担保类型
			tmp.put("ASSURE_TYPE", StaticUtil.getStaticValue("TD_S_ASSURETYPE", tmp.getString("ASSURE_TYPE_CODE")));

			tmp.put("X_DEVELOP_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("DEVELOP_EPARCHY_CODE")));
			tmp.put("X_DEVELOP_CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("DEVELOP_CITY_CODE")));
			tmp.put("X_DEVELOP_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("DEVELOP_DEPART_ID")));
			tmp.put("X_DEVELOP_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tmp.getString("DEVELOP_STAFF_ID")));
			tmp.put("X_IN_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("IN_DEPART_ID")));
			tmp.put("X_IN_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tmp.getString("IN_STAFF_ID")));
			tmp.put("X_REMOVE_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("REMOVE_EPARCHY_CODE")));
			tmp.put("X_REMOVE_CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("REMOVE_CITY_CODE")));
			tmp.put("X_REMOVE_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("REMOVE_DEPART_ID")));

			tmp.put("REMOVE_REASON", StaticUtil.getStaticValue("DESTROY_REASON", tmp.getString("REMOVE_REASON_CODE")));
			// 获取客户类型
			tmp.put("X_CUST_TYPE", StaticUtil.getStaticValue("CUST_TYPE", tmp.getString("CUST_TYPE")));
			// 获取客户状态
			tmp.put("X_CUST_STATE", StaticUtil.getStaticValue("CUST_STATE", tmp.getString("CUST_STATE")));

			tmp.put("PSPT_TYPE", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_PASSPORTTYPE", new String[]
			{ "EPARCHY_CODE", "PSPT_TYPE_CODE" }, "PSPT_TYPE", new String[]
			{ tmp.getString("EPARCHY_CODE"), tmp.getString("PSPT_TYPE_CODE") }));

			// 获取客户性别
			tmp.put("X_SEX", StaticUtil.getStaticValue("SEX", tmp.getString("SEX")));
			// 获取客户国家名称
			tmp.put("NATIONALITY_NAME", StaticUtil.getStaticValue("TD_S_NATIONALITY", tmp.getString("NATIONALITY_CODE")));
			// 获取客户籍贯
			tmp.put("LOCAL_NATIVE_NAME", StaticUtil.getStaticValue("TD_S_LOCAL_NATIVE", tmp.getString("LOCAL_NATIVE_CODE")));
			// 获取客户语言
			tmp.put("LANGUAGE_NAME", StaticUtil.getStaticValue("TD_S_LANGUAGE", tmp.getString("LANGUAGE_CODE")));
			// 获取客户民族
			tmp.put("FOLK", StaticUtil.getStaticValue("TD_S_FOLK", tmp.getString("FOLK_CODE")));
			// 获取客户工作类型
			tmp.put("JOB_TYPE", StaticUtil.getStaticValue("TD_S_JOBTYPE", tmp.getString("JOB_TYPE_CODE")));
			// 获取客户教育程度
			tmp.put("EDUCATE_DEGREE", StaticUtil.getStaticValue("CUSTPERSON_EDUCATEDEGREECODE", tmp.getString("EDUCATE_DEGREE_CODE")));
			// 获取客户信仰
			tmp.put("RELIGION_NAME", StaticUtil.getStaticValue("TD_S_RELIGION", tmp.getString("RELIGION_CODE")));
			// 获取客户收入等级
			tmp.put("REVENUE_LEVEL", StaticUtil.getStaticValue("TD_S_REVENUE_LEVEL", tmp.getString("REVENUE_LEVEL_CODE")));
			// 获取客户婚姻状况
			tmp.put("X_MARRIAGE", StaticUtil.getStaticValue("CUSTPERSON_MARRIAGESTATE", tmp.getString("MARRIAGE")));
			// 获取客户性格类型
			tmp.put("CHARACTER_TYPE", StaticUtil.getStaticValue("TD_S_CHARACTERTYPE", tmp.getString("CHARACTER_TYPE_CODE")));
			// 获取客户优先联系方式
			tmp.put("CONTACT_TYPE", StaticUtil.getStaticValue("TD_S_CONTACTTYPE", tmp.getString("CONTACT_TYPE_CODE")));
			// 获取账户类型
			tmp.put("PAY_MODE", StaticUtil.getStaticValue("TD_S_PAYMODE", tmp.getString("PAY_MODE_CODE")));
			// 获取银行名称
			tmp.put("BANK", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", "BANK_CODE", "BANK", tmp.getString("BANK_CODE")));
			// 获取大客户经理相关信息
			IData custMgrdata = UStaffInfoQry.qryCustManagerInfoByCustManagerId(tmp.getString("CUST_MANAGER_ID"));
			if (IDataUtil.isEmpty(custMgrdata))
			{
				tmp.put("VIP_MANAGER_NAME", "");
				tmp.put("LINK_PHONE", "");
			} else
			{
				tmp.put("VIP_MANAGER_NAME", custMgrdata.getString("CUST_MANAGER_NAME"));
				tmp.put("LINK_PHONE", custMgrdata.getString("LINK_PHONE"));
			}

			// 获取大客户归属集团名称
			if (StringUtils.isBlank(tmp.getString("VPMN_GROUP_ID")))
			{
				tmp.put("VPMN_GROUP_NAME", "");
			} else
			{
				tmp.put("VPMN_GROUP_NAME", tmp.getString("CUST_NAME"));
			}

			// 获取担保客户名称
			if (StringUtils.isBlank(tmp.getString("ASSURE_CUST_ID")))
			{
				tmp.put("ASSURE_NAME", "");
			} else
			{
				tmp.put("ASSURE_NAME", tmp.getString("CUST_NAME"));
			}

			// 大客户信息
			tmp.put("CLASS_ID2", tmp.getString("CLASS_ID"));
			tmp.put("CLIENT_INFO5", tmp.getString("CLASS_NAME"));
			tmp.put("CLIENT_INFO1", tmp.getString("VIP_MANAGER_NAME"));
			tmp.put("CLIENT_INFO2", tmp.getString("LINK_PHONE"));

			// 去客户昵称信息
			IDataset custTitleData = UserOtherInfoQry.getUserOther(userId, "CTHN");
			String custTitle = "";
			if (IDataUtil.isNotEmpty(custTitleData))
			{
				custTitle = custTitleData.getData(0).getString("RSRV_VALUE", "");
			}
			// 客户昵称
			tmp.put("RSRV_VALUE", custTitle);

		}
		return results;
	}

	/**
	 * 根据USER_ID获取3户资料
	 * 
	 * @param USER_ID
	 * @return IData
	 * @author huangsl
	 */
	private IData getUserCustAcctInfo(String userId) throws Exception
	{
		// 根据UserId查UCA
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		IData result = new DataMap();

		// 转换VIP信息

		if (uca == null || uca.getVip() == null)
		{
			// 没有大客户信息
			result.put("VIP_TAG", "N");
			result.put("CLASS_NAME", "");
		} else
		{
			result.put("VIP_TAG", "Y");
			String className = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(uca.getVip().getVipTypeCode(), uca.getVip().getVipClassId());
			result.put("CLASS_NAME", className);
		}

		// 转换SIM卡信息
		List<ResTradeData> resList = uca.getUserAllRes();
		Iterator it = resList.iterator();
		while (it.hasNext())
		{
			ResTradeData resData = (ResTradeData) it.next();
			if ("1".equals(resData.getResTypeCode()))
			{
				IDataset simDatas = ResCall.getSimCardInfo("1", null, resData.getImsi(), null);
				if (IDataUtil.isNotEmpty(simDatas))
				{
					result.put("SIM_CARD_NO", simDatas.getData(0).getString("SIM_CARD_NO"));
					result.put("PUK", simDatas.getData(0).getString("PUK"));
				}
			}
		}
		// 取需要的信息
		// 用户
		result.putAll(uca.getUser().toData());
		result.put("BRAND_CODE", uca.getBrandCode());
		result.put("PRODUCT_ID", uca.getProductId());
		result.put("EPARCHY_CODE", uca.getUserEparchyCode());
		// result.put("SCORE_VALUE", uca.getUserScore());
		// result.put("CREDIT_CLASS", uca.getUserCreditClass());
		// result.put("CREDIT_VALUE", uca.getUserCreditValue());

		// 客户
		CustomerTradeData custData = uca.getCustomer();
		result.put("CUST_NAME", custData.getCustName());
		result.put("CUST_TYPE", custData.getCustType());
		result.put("CUST_STATE", custData.getCustState());
		result.put("OPEN_LIMIT", custData.getOpenLimit());
		result.put("CUST_PASSWD", custData.getCustPasswd());
		result.put("PSPT_TYPE_CODE", custData.getPsptTypeCode());
		result.put("PSPT_ID", custData.getPsptId());
		
		/**
		 * 关于开发个人非身份证证件类型APP开户的需求
		 * <br/>
		 * 添加返回实名制字段  IS_REAL_NAME 1：实名制     0或空则为非实名制
		 * @author zhuoyingzhi
		 * @date 20180627
		 */
		result.put("IS_REAL_NAME", custData.getIsRealName());
		// 个人客户、集团客户
		// 取客户资料
		if (uca.getCustPerson() == null && uca.getCustGroup() == null)
		{
			// 客户资料不存在;客户详细信息不存在
			return null;
		}

		if ("0".equals(custData.getCustType()))// 个人客户
		{
			if (uca.getCustPerson() != null)
			{
				result.putAll(uca.getCustPerson().toData());
			}
		} else
		// 集团客户
		{
			if (uca.getCustGroup() != null)
			{
				result.putAll(uca.getCustGroup().toData());
			}
		}

		// 账户
		if (uca.getAccount() != null)
		{
			result.putAll(uca.getAccount().toData());
		}

		// VIP信息
		VipTradeData vips = uca.getVip();
		if (vips != null)
		{
			result.put("VIP_NO", vips.getVipCardNo());
			result.put("CLASS_ID", vips.getVipClassId());
			result.put("VIP_TYPE_CODE", vips.getVipTypeCode());
			result.put("CUST_MANAGER_ID", vips.getCustManagerId());// 客户经理编码
			result.put("VPMN_GROUP_ID", vips.getGroupId());// 集团标志
		}

		// 用户主体服务
		List<SvcStateTradeData> listSvc = uca.getUserSvcsState();
		if (listSvc == null || listSvc.size() < 1)
		{
			// 获取主体服务失败
			result.put("X_SVCSTATE_EXPLAIN", "获取用户主体服务失败");
		} else
		{
			int listSvcSize = listSvc.size();
			for (int i = 0; i < listSvcSize; i++)
			{
				if ("1".equals(listSvc.get(i).getMainTag()))
				{
					// 获取服务状态
					IDataset svc = USvcStateInfoQry.qryStateNameBySvcIdStateCode(listSvc.get(i).getServiceId(), listSvc.get(i).getStateCode());
					String svcStateExplan = "";
					for (int j = 0; j < svc.size(); j++)
					{
						svcStateExplan += ((IData) (svc.get(j))).getString("STATE_NAME");
						if (j != (svc.size() - 1))
							svcStateExplan += "、";
					}
					result.put("X_SVCSTATE_EXPLAIN", svcStateExplan);
				}
			}
		}
		return result;
	}

	public IData getUserGprsDiscntIVR(IData input) throws Exception
	{

		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData userInfo = getUserInfo4SingleAll(serialNumber);
		String userId = userInfo.getString("USER_ID");
		IDataset GPRSDiscnt = UserDiscntInfoQry.queryUserGPRSDiscnt(userId, CSBizBean.getVisit().getStaffEparchyCode());
		IData ret = new DataMap();
		if (IDataUtil.isEmpty(GPRSDiscnt))
		{
			ret.put("QUERY_TAG", "1");
		} else
		{
			ret = GPRSDiscnt.getData(0);
			ret.put("QUERY_TAG", "0");
		}

		return ret;
	}

	/**
	 * 手机支付系统通过手机号码，查询用户基本信息
	 * 
	 * @param input
	 * @return IDataset
	 * @throws Exception
	 * @param inparams
	 *            SERIAL_NUMBER USER_ID CUST_ID X_GETMODE(0-输入服务号码取正常用户 1-输入用户标识
	 *            2-输入客户标识取正常用户 3-输入服务号码取所有用户 4-输入服务号码取所有非正常用户 5-输入服务号码取最后销户用户 )
	 */
	public IDataset getUserInfo(IData input) throws Exception
	{
		IDataset result = new DatasetList();

		int getMode = input.getInt("X_GETMODE");
		String userId = input.getString("USER_ID", "");
		String custId = input.getString("CUST_ID", "");
		String serialNumber = input.getString("SERIAL_NUMBER", "");
		IData param = new DataMap();
		if (getMode == 0)// 0-输入服务号码取正常用户
		{
			IDataUtil.chkParam(input, "SERIAL_NUMBER");
			result = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(serialNumber));
			if (IDataUtil.isNotEmpty(result))
			{
				IDataset result4Person = new DatasetList();
				// 提供给客服的用户资料接口新增联系地址、Email、联系电话、优先联系方式字段
				String custId4Query = result.getData(0).getString("CUST_ID", "");
				result4Person = IDataUtil.idToIds(UcaInfoQry.qryPerInfoByCustId(custId4Query));
				if (IDataUtil.isNotEmpty(result4Person))
				{
					String homeAddress = result4Person.getData(0).getString("HOME_ADDRESS", "");
					String contactPhone = result4Person.getData(0).getString("CONTACT_PHONE", "");
					String contactTypeCode = result4Person.getData(0).getString("CONTACT_TYPE_CODE", "");
					String email = result4Person.getData(0).getString("EMAIL", "");
					result.getData(0).put("HOME_ADDRESS", homeAddress);
					result.getData(0).put("CONTACT_PHONE", contactPhone);
					result.getData(0).put("CONTACT_TYPE_CODE", contactTypeCode);
					result.getData(0).put("EMAIL", email);
				}
			}
		} else if (getMode == 1)// 1-输入用户标识
		{
			IDataUtil.chkParam(input, "USER_ID");
			result = IDataUtil.idToIds(UcaInfoQry.qryUserMainProdInfoByUserId(userId));
		} else if (getMode == 2)// 2-输入客户标识取正常用户
		{
			IDataUtil.chkParam(input, "CUST_ID");
			result = UserInfoQry.getAllNormalUserInfoByCustId(custId);
		} else if (getMode == 3)// 3-输入服务号码取所有用户
		{
			IDataUtil.chkParam(input, "SERIAL_NUMBER");
			result = UserInfoQry.getUserInfoBySnAll(serialNumber);
		} else if (getMode == 4)// 4-输入服务号码取所有非正常用户
		{
			IDataUtil.chkParam(input, "SERIAL_NUMBER");
			result = UserInfoQry.getUserInfoBySnDestroyAll(serialNumber, null);
		} else if (getMode == 5)// 5-输入服务号码取最后销户用户
		{
			IDataUtil.chkParam(input, "SERIAL_NUMBER");
			result = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_99);
			return null;
		}

		if (IDataUtil.isEmpty(result))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1);
			return null;
		}

		for (int i = 0; i < result.size(); i++)
		{
			IData tmp = result.getData(i);
			if (!tmp.containsKey("PRODUCT_ID") || !tmp.containsKey("BRAND_CODE"))
			{
				IData productInfo = TradeInfoBean.getUserProductInfo(tmp.getString("USER_ID"), tmp.getString("REMOVE_TAG", "0"));
				tmp.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID", ""));
				tmp.put("BRAND_CODE", productInfo.getString("BRAND_CODE", ""));
			}

			tmp.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(tmp.getString("BRAND_CODE")));
			tmp.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(tmp.getString("PRODUCT_ID")));
			tmp.put("X_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("EPARCHY_CODE")));
			tmp.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("CITY_CODE")));
			tmp.put("USER_TYPE", StaticUtil.getStaticValue(getVisit(), "TD_B_USERTYPE", new String[]
			{ "EPARCHY_CODE", "USER_TYPE_CODE" }, "USER_TYPE", new String[]
			{ CSBizBean.getTradeEparchyCode(), tmp.getString("USER_TYPE_CODE") }));

			String acctTag = tmp.getString("ACCT_TAG");
			if ("0".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "正常处理");
			else if ("1".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "定时激活");
			else if ("2".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "待激活用户");
			else if ("Z".equals(acctTag))
				tmp.put("X_ACCT_TAG_NAME", "不出帐");
			else
				tmp.put("X_ACCT_TAG_NAME", "未知出账标志");

			String prepayTag = tmp.getString("PREPAY_TAG");
			if ("0".equals(prepayTag))
				tmp.put("X_PREPAY_TAG_NAME", "后付费");
			else if ("1".equals(prepayTag))
				tmp.put("X_PREPAY_TAG_NAME", "预付费");
			else
				tmp.put("X_PREPAY_TAG_NAME", "未知预付费标志");

			String removeTag = tmp.getString("REMOVE_TAG");
			if ("0".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "正常");
			else if ("1".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "主动预销号");
			else if ("2".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "主动销号");
			else if ("3".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "欠费预销号");
			else if ("4".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "欠费销号");
			else if ("5".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "开户返销");
			else if ("6".equals(removeTag))
				tmp.put("X_REMOVE_TAG_NAME", "过户注销");
			else
				tmp.put("X_REMOVE_TAG_NAME", "未知注销标志");

			String openMode = tmp.getString("OPEN_MODE");
			if ("0".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "正常");
			else if ("1".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "预开未返单");
			else if ("2".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "预开已返单");
			else if ("3".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "过户新增");
			else if ("4".equals(openMode))
				tmp.put("X_OPEN_MODE_NAME", "当日返单并过户");
			else
				tmp.put("X_OPEN_MODE_NAME", "未知开户方式标志");

			// 用户主体服务
			IDataset svc = UserSvcInfoQry.getMainSvcUserId(tmp.getString("USER_ID"));
			if (IDataUtil.isEmpty(svc))
			{
				tmp.put("X_SVCSTATE_EXPLAIN", "获取用户主体服务失败");
			} else
			{
				String sMSvcID = svc.get(0, "SERVICE_ID").toString();
				String sUSCset = tmp.getString("USER_STATE_CODESET");
				IDataset sstates = USvcStateInfoQry.qryStateNameBySvcIdStateCode(sMSvcID, sUSCset);
				String svcStateExplan = "";
				for (int j = 0; j < sstates.size(); j++)
				{
					svcStateExplan += sstates.get(j, "STATE_NAME").toString();
					if (j < (sstates.size() - 1))
					{
						svcStateExplan += "、";
					}
				}
				tmp.put("X_SVCSTATE_EXPLAIN", svcStateExplan);
			}

			// 获取担保类型
			String sAsures = StaticUtil.getStaticValue("TD_S_ASSURETYPE", tmp.getString("ASSURE_TYPE_CODE"));
			if (StringUtils.isBlank(sAsures))
			{
				tmp.put("ASSURE_TYPE", "");
			} else
			{
				tmp.put("ASSURE_TYPE", sAsures);
			}

			tmp.put("X_DEVELOP_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("DEVELOP_EPARCHY_CODE")));
			tmp.put("X_DEVELOP_CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("DEVELOP_CITY_CODE")));
			tmp.put("X_DEVELOP_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("DEVELOP_DEPART_ID")));
			tmp.put("X_DEVELOP_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tmp.getString("DEVELOP_STAFF_ID")));
			tmp.put("X_IN_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("IN_DEPART_ID")));
			tmp.put("X_IN_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tmp.getString("IN_STAFF_ID")));
			tmp.put("X_REMOVE_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("REMOVE_EPARCHY_CODE")));
			tmp.put("X_REMOVE_CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("REMOVE_CITY_CODE")));
			tmp.put("X_REMOVE_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("REMOVE_DEPART_ID")));

			// 获取离网原因
			String sRemoveReason = StaticUtil.getStaticValue("DESTROY_REASON", tmp.getString("REMOVE_REASON_CODE"));
			if (StringUtils.isBlank(sRemoveReason))
			{
				tmp.put("REMOVE_REASON", "");
			} else
			{
				tmp.put("REMOVE_REASON", sRemoveReason);
			}

			// 获取其他信息
			// 取客户资料
			param.clear();
			custId = tmp.getString("CUST_ID", "");
			IData custids = UcaInfoQry.qryCustomerInfoByCustId(custId);
			if (IDataUtil.isEmpty(custids))
			{
				CSAppException.apperr(CustException.CRM_CUST_35);
			}
			tmp.put("CUST_NAME", custids.getString("CUST_NAME"));

			// 取大客户信息
			IDataset vipids = CustVipInfoQry.qryVipInfoByUserId(tmp.getString("USER_ID"));

			if (IDataUtil.isEmpty(vipids))
			{
				// 没有大客户信息
				tmp.put("VIP_NO", "");
				tmp.put("CLASS_ID", "");
				tmp.put("CLASS_ID2", "");
				tmp.put("IDENTITY_EXP_DATE", "");
			} else
			{
				tmp.put("VIP_NO", vipids.getData(0).getString("VIP_CARD_NO"));
				tmp.put("CLASS_ID", vipids.getData(0).getString("VIP_CLASS_ID"));
				tmp.put("CLASS_ID2", vipids.getData(0).getString("VIP_CLASS_ID_B"));
				tmp.put("IDENTITY_EXP_DATE", vipids.getData(0).getString("IDENTITY_EXP_DATE"));
			}

			// 去客户昵称信息
			IDataset custTitleData = UserOtherInfoQry.getUserOther(userId, "CTHN");

			String custTitle = "";
			if (IDataUtil.isNotEmpty(custTitleData))
			{
				custTitle = custTitleData.getData(0).getString("RSRV_VALUE", "");
			}

			// 客户昵称
			tmp.put("RSRV_VALUE", custTitle);
		}

		return result;
	}

	public IData getUserInfo4All(String serialNumber) throws Exception
	{
		IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(userInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		IData proInfo = UcaInfoQry.qryMainProdInfoByUserId(userInfos.getString("USER_ID"));

		if (IDataUtil.isEmpty(proInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}

		userInfos.putAll(proInfo);

		return userInfos;
	}

	public IData getUserInfo4SingleAll(String serialNumber) throws Exception
	{
		IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(userInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		return userInfos;
	}

	public IData getUserMpay(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		if ("6".equals(CSBizBean.getVisit().getInModeCode()))
			input.put("KIND_ID", "BIP2B083_T2040027_1_0");

		IData result = getMpayInfo(input);

		if ("0".equals(result.getString("BIZ_STATE_CODE")))
		{
			result.put("MPAY", "0");
		} else if ("1".equals(result.getString("BIZ_STATE_CODE")) || "2".equals(result.getString("BIZ_STATE_CODE")))
		{
			result.put("MPAY", "1");
		}

		result.put("ATTR_STR4", result.getString("MPAY"));// 海南特殊要求

		return result;
	}

	public IDataset getUserResource(IData input) throws Exception
	{
		IDataset res = new DatasetList();
		int getMode = input.getInt("X_GETMODE");
		String serNum = input.getString("SERIAL_NUMBER");

		if (0 == getMode)// 正常用户
		{
			res = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(serNum));
		} else if (1 == getMode)// 最后销号用户
		{
			res = UserInfoQry.getDestroyUserInfoBySn(serNum);
		}

		if (IDataUtil.isEmpty(res))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
			return null;
		}
		String userid = ((IData) res.get(0)).getString("USER_ID");

		// 获取资源
		res = UserResInfoQry.getUserResInfoByUserId(userid);

		for (int i = 0; i < res.size(); i++)
		{
			IData tmp = (IData) res.get(i);
			tmp.put("RES_TYPE", StaticUtil.getStaticValue(getVisit(),Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE", tmp.getString("RES_TYPE_CODE")));
			if (i > 0)
			{
				tmp.remove("USER_ID");
			}
		}

		return res;
	}

	/**
	 * 根据客户级别和服务类型 查询允许随从数,应扣积分
	 * 
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public IData getVipParamMsg(String serviceType) throws Exception
	{
		IData result = new DataMap();
		IData data = new DataMap();
		IDataset paramset = CommparaInfoQry.getCommpara("CSM", "1989", serviceType, "0898");
		if (IDataUtil.isNotEmpty(paramset))
		{
			data.putAll(paramset.getData(0));
			result.put("REDUCE_SCORE", data.getInt("PARA_CODE1", 0));
		} else
		{
			result.put("REDUCE_SCORE", "0");
		}

		// 由客户级别计算最多随从人数，默认为2
		result.put("MAX_FOLLOW_NUM", 2);
		return result;
	}

	public IDataset getWelcomeBiz(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "TRADE_STAFF_ID");
		IDataUtil.chkParam(input, "TRADE_DEPART_ID");
		IDataUtil.chkParam(input, "TRADE_CITY_CODE");
		IDataUtil.chkParam(input, "IN_MODE_CODE");
		IDataUtil.chkParam(input, "TRADE_EPARCHY_CODE");
		IDataUtil.chkParam(input, "USER_ID");
		String userId = input.getString("USER_ID");

		IDataset ret = new DatasetList();
		IDataset tmp = new DatasetList();

		tmp = SmsQry.queryPlatSVCInfoByUseridNow(userId);
		if (IDataUtil.isNotEmpty(tmp))
		{
			ret.addAll(tmp);
		}

		tmp.clear();
		tmp = SmsQry.querySVCInfoByUseridNow(userId);
		if (IDataUtil.isNotEmpty(tmp))
		{
			ret.addAll(tmp);
		}

		tmp.clear();
		tmp = SmsQry.queryDISCNTInfoByUseridNow(userId);
		if (IDataUtil.isNotEmpty(tmp))
		{
			ret.addAll(tmp);
		}

		DataHelper.sort(ret, "SEQ_NO", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);

		return ret;
	}

	public IDataset getWidenetDiscntByProduct(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String eparchyCode = IDataUtil.chkParam(input, "EPARCHY_CODE");
		String productId = IDataUtil.chkParam(input, "PRODUCT_ID");
		String staffId = IDataUtil.chkParam(input, "TRADE_STAFF_ID");

		IDataset widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
		if (IDataUtil.isEmpty(widenetInfo))
		{
			CSAppException.apperr(WidenetException.CRM_WIDENET_22);
		}

		IData info = widenetInfo.getData(0);
		String userId = info.getString("USER_ID");
		// boolean privForEle = input.getString("PRIV_FOR_ELE",
		// "").toLowerCase().equals("true");
		// boolean privForPack = input.getString("PRIV_FOR_PACK",
		// "").toLowerCase().equals("true");

		IDataset discntTempElements = new DatasetList();

		String discntCodes = "";

		IDataset elementLists = UpcCall.queryAllOfferEnablesByOfferIdAndRelType(productId, "2");

		if (IDataUtil.isNotEmpty(elementLists))
		{
			for (int i = 0; i < elementLists.size(); i++)
			{

				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementLists.getData(i).getString("ELEMENT_TYPE_CODE")))
				{
					discntTempElements.add(elementLists.getData(i));

					discntCodes += elementLists.getData(i).getString("ELEMENT_ID") + ",";
				}

			}
		}

		IDataset userValidDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);

		// 查询查询产品下所有的元素，包括已经失效的
		IDataset eglecElementLists = UpcCall.queryNeglectDateAllOfferEnablesByOfferId(productId, "2");

		if (IDataUtil.isNotEmpty(userValidDiscnts) && IDataUtil.isNotEmpty(eglecElementLists))
		{
			for (int j = 0; j < userValidDiscnts.size(); j++)
			{
				// 如果用户当前有效的优惠，但优惠配置已经下架，则到所有优惠配置中去关联，包括已经失效下架的
				if (!discntCodes.contains(userValidDiscnts.getData(j).getString("DISCNT_CODE")))
				{
					for (int k = 0; k < eglecElementLists.size(); k++)
					{
						if (userValidDiscnts.getData(j).getString("DISCNT_CODE").equals(eglecElementLists.getData(k).getString("ELEMENT_ID")) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eglecElementLists.getData(k).getString("ELEMENT_TYPE_CODE")))
						{
							discntTempElements.add(eglecElementLists.getData(k));
						}
					}
				}
			}
		}

		ElementPrivUtil.filterElementListByPriv(staffId, discntTempElements);

		return discntTempElements;
	}

	private void groupTransCodeToName(IDataset result) throws Exception
	{
		customerTransCodeToName(result);
		custgroupTransCodeToName(result);
	}

	public IDataset logout(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String inModeCode = IDataUtil.chkParam(input, "IN_MODE_CODE");
		String identCode = IDataUtil.chkParam(input, "IDENT_CODE");
		// update by xuwb5 IBOSS渠道编码是6
		if (!StringUtils.equals(inModeCode, "11"))// 当前仅支持统一认证中心进行调用
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_914);
		}

		IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		IDataset idents = UserIdentInfoQry.queryIdentInfoByCode(identCode, serialNumber);
		if (IDataUtil.isEmpty(idents))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1054);
		}
		String userType = idents.getData(0).getString("USER_TYPE");
		String identCodeType = idents.getData(0).getString("IDENT_CODE_TYPE");
		String identCodeLevel = idents.getData(0).getString("IDENT_CODE_LEVEL");

		String tag = idents.getData(0).getString("TAG");
		if (StringUtils.equals(tag, "VALID"))
		{
			UserIdentInfoQry.updIdent2DisableByCode(identCode);
		}

		SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "logout");

		return new DatasetList();
	}

	public IDataset oderInfoQry(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String removeTag = input.getString("REMOVE_TAG", "0");
		String dealTag = input.getString("DEAL_TAG", "00");

		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber(removeTag, serialNumber);

		if (IDataUtil.isEmpty(userInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		IData userInfo = userInfos.getData(0);

		String userId = userInfo.getString("USER_ID");

		IDataset result = getPlatOrderInfo(userId, dealTag);

		if (IDataUtil.isNotEmpty(result))
		{
			for (int i = 0; i < result.size(); i++)
			{
				IData svcInfo = result.getData(i);
				String giftSerialNmuber = svcInfo.getString("GIFT_SERIAL_NUMBER");
				if (StringUtils.isNotBlank(giftSerialNmuber))
				{
					svcInfo.put("GIFT_TYPE", "1");
				} else
				{
					svcInfo.put("GIFT_TYPE", "0");
				}

				String biztypecode = svcInfo.getString("BIZ_TYPE_CODE");
				String serviceID = svcInfo.getString("SERVICE_ID");
				String serv_type = svcInfo.getString("SERV_TYPE");
				String instId = svcInfo.getString("INST_ID");
				svcInfo.remove("INST_ID");
				if (StringUtils.isNotBlank(biztypecode) && "27".equals(biztypecode) && "0".equals(serv_type))
				{
					svcInfo.put("SERV_TYPE", "1");
				}

				if (StringUtils.isNotBlank(biztypecode) && biztypecode.equals("19") && serviceID.equals("98001901"))
				{
					IData param2 = new DataMap();
					IDataset musicattrs = new DatasetList();
					param2.put("USER_ID", userInfo.getString("USER_ID"));
					param2.put("RELA_INST_ID", instId);
					musicattrs = WithinSetDreamNetQry.selPlatAttrInfo(param2);

					if (IDataUtil.isNotEmpty(musicattrs))
					{
						for (int j = 0; j < musicattrs.size(); j++)
						{
							String attr_code = musicattrs.getData(j).getString("ATTR_CODE", "");
							String attr_value = musicattrs.getData(j).getString("ATTR_VALUE", "");

							if ("302".equals(attr_code))
							{
								if ("2".equals(attr_value))
								{
									svcInfo.put("PRICE", "5000");// 平台业务返回价格以厘为单位
									svcInfo.put("BIZ_NAME", "无线音乐平台高级会员");
									svcInfo.put("BIZ_TYPE", "无线音乐平台高级会员");
								} else if ("1".equals(attr_value))
								{
									svcInfo.put("PRICE", "0");
									svcInfo.put("BIZ_NAME", "无线音乐平台会员");
									svcInfo.put("BIZ_TYPE", "无线音乐平台会员");
								}
								break;
							}
						}
					}

				}
			}

		}

		return result;
	}

	public void operDataToIboss(IDataset dataset) throws Exception
	{
		for (int i = 0; i < dataset.size(); ++i)
		{
			IData info = dataset.getData(i);
			info.put("EXEC_FLAG", "3");
			Dao.save("TF_B_TWO_CHECK", info);
			// IData param = new DataMap();
			// param.put("KIND_ID", "BIP2B082_T2040025_0_0");
			// param.put("ID_TYPE", "01");
			// param.put("SERIAL_NUMBER", info.getString("SERIAL_NUMBER"));
			// param.put("TRANS_ID", info.getString("RSRV_STR1"));
			// param.put("RSP_CODE", "02");
			// IBossCall.dealInvokeUrl("BIP2B082_T2040025_0_0", "IBOSS", param);
			IBossCall.operSMSTimeOut("01", info.getString("SERIAL_NUMBER"), info.getString("RSRV_STR1"), "02", BizRoute.getRouteId(), getTradeEparchyCode());
		}
	}

	public IDataset operSMSTimeOut(IData input) throws Exception
	{
		IData param = new DataMap();
		param.put("OUTER_TRADE_ID", "999999999999");
		param.put("EXEC_FLAG", "0");
		param.put("ROUNUM", "3");
		IDataset dataset = new DatasetList();
		dataset = QueryInfoUtil.queryTfBTwoCheckByTimeout(param);
		if (dataset.size() > 0)
		{
			operDataToIboss(dataset);
		}
		IDataset returnDataList = new DatasetList();
		return returnDataList;// 不需要返回值，返回一个空的Idataset
	}

	private void personTransCodeToName(IDataset result) throws Exception
	{
		customerTransCodeToName(result);
		custpersonTransCodeToName(result);
	}

	/**
	 * add by ouyk
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryAgentUserBackListByCond(IData input, Pagination pagination) throws Exception
	{
		// String[] beginTime = input.getString("BEGIN_TIME").split("-");
		// String[] endTime = input.getString("END_TIME").split("-");
		input.put("START_DATE", input.getString("BEGIN_TIME"));
		input.put("END_DATE", input.getString("END_TIME"));
		String isExtendTime = input.getString("IS_EXTEND_TIME", "");
		IDataset rtnDataset = new DatasetList();
		if (isExtendTime.length() == 0)
		{// 查询全部
			rtnDataset = QueryInfoUtil.qryAgentUserBackListByCond_1(input, pagination);
		} else if (isExtendTime.equals("0"))
		{// 查询不延期
			rtnDataset = QueryInfoUtil.qryAgentUserBackListByCond_2(input, pagination);
		} else if (isExtendTime.equals("1"))
		{// 查询延期
			rtnDataset = QueryInfoUtil.qryAgentUserBackListByCond_3(input, pagination);
		}

		for (int i = 0; i < rtnDataset.size(); i++)
		{
			IData info = rtnDataset.getData(i);
			String resTypeCode = "1" + info.getString("SIM_TYPE_CODE");
			IData param = new DataMap();
			param.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
			param.put("RES_TYPE_CODE", resTypeCode);
			Map tagMap = new HashMap();
			tagMap.put("0", "即将回收");
			tagMap.put("1", "已销号未回收返还");
			tagMap.put("2", "已回收返还");
			tagMap.put("4", "延时回收");
			tagMap.put("9", "已激活未销号");
			String tag = info.getString("TAG");
			if (tagMap.containsKey(tag))
			{
				info.put("TAG", tagMap.get(tag).toString());
			}
			//IDataset rmDataset = CSAppCall.call("RCF.resource.IResPublicIntfQuerySV.qryTypeByCode", param);
		    /**
		     * 代理商即将逾期回收号码清单查询不能查询数据
		     * @author zhuoyingzhi
		     * date 20170112		     
		     */
			IDataset rmDataset = ResCall.qrySimCardTypeByTypeCode(resTypeCode);
			/*************************************************/
			if (null != rmDataset && rmDataset.size() > 0)
			{
				info.put("KIND_NAME", rmDataset.getData(0).getString("RES_TYPE_NAME"));
			} else
			{
				info.put("KIND_NAME", resTypeCode);
			}
		}
		return rtnDataset;
	}

	/**
	 * add by liquan REQ201608250014新增代理商买断套卡库存清单查询界面和优化相关界面
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryAgentUserListByCond(IData input, Pagination pagination) throws Exception
	{
		// String[] beginTime = input.getString("BEGIN_TIME").split("-");
		// String[] endTime = input.getString("END_TIME").split("-");
		input.put("START_DATE", input.getString("BEGIN_TIME"));
		input.put("END_DATE", input.getString("END_TIME"));
		// String isExtendTime = input.getString("IS_EXTEND_TIME","");
		IDataset rtnDataset = new DatasetList();
		rtnDataset = QueryInfoUtil.qryAgentUserListByCond_1(input, pagination);

		/*
		 * if(isExtendTime.length()==0){//查询全部 rtnDataset =
		 * QueryInfoUtil.qryAgentUserListByCond_1(input, pagination); }else
		 * if(isExtendTime.equals("0")){//查询不延期 rtnDataset =
		 * QueryInfoUtil.qryAgentUserListByCond_2(input, pagination); }else
		 * if(isExtendTime.equals("1")){//查询延期 rtnDataset =
		 * QueryInfoUtil.qryAgentUserListByCond_3(input, pagination); }
		 */

		for (int i = 0; i < rtnDataset.size(); i++)
		{
			IData info = rtnDataset.getData(i);
			info.put("QRYTIME", SysDateMgr.getSysDate("yyyy-MM-dd"));

			String resTypeCode = "1" + info.getString("SIM_TYPE_CODE");
			IData param = new DataMap();
			param.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
			param.put("RES_TYPE_CODE", resTypeCode);
			Map tagMap = new HashMap();
			tagMap.put("0", "即将回收");
			tagMap.put("1", "已销号未回收返还");
			tagMap.put("2", "已回收返还");
			tagMap.put("4", "延时回收");
			tagMap.put("9", "已激活未销号");
			String tag = info.getString("TAG");
			if (tagMap.containsKey(tag))
			{
				info.put("TAG", tagMap.get(tag).toString());
			}
			IDataset rmDataset = CSAppCall.call("RM.ResQueryIntfSvc.qryTypeByCode", param);
			if (null != rmDataset && rmDataset.size() > 0)
			{
				info.put("KIND_NAME", rmDataset.getData(0).getString("RES_TYPE_NAME"));
			} else
			{
				info.put("KIND_NAME", resTypeCode);
			}

			info.put("AREA_NAME", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", info.getString("CITY_CODE", "")));
		}
		return rtnDataset;
	}

	/**
	 * add by ouyk
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryDepartKinds(IData input) throws Exception
	{
		String eparchyCode = input.getString("EPARCHY_CODE");
		return QueryInfoUtil.queryDepartKinds(eparchyCode);
	}

	/**
	 * 校验号码是否能销户
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public IData qryDestroyUserInfo(String serialNumber) throws Exception
	{
		IData retData = new DataMap();
		// 取最近的一条用户信息
		IDataset userList = UserInfoQry.getLatestUserInfosBySerialNumber(serialNumber);
		if (IDataUtil.isNotEmpty(userList))
		{
			retData.put("USER_ID", userList.getData(0).getString("USER_ID"));
			retData.put("REMOVE_TAG", userList.getData(0).getString("REMOVE_TAG"));
			retData.put("DESTROY_TIME", userList.getData(0).getString("DESTROY_TIME", ""));
			// retData.put("IS_RED", false);
		} else
		{
			// 查询离网用户资料表
			userList = UserInfoQry.qryAllUserInfoBySnFromHis(serialNumber);
			if (IDataUtil.isNotEmpty(userList))
			{
				retData.put("USER_ID", userList.getData(0).getString("USER_ID"));
				retData.put("REMOVE_TAG", userList.getData(0).getString("REMOVE_TAG"));
				retData.put("DESTROY_TIME", userList.getData(0).getString("DESTROY_TIME", ""));
				// retData.put("IS_RED", false);
			}
		}
		return retData;
	}
	/**
     * 校验号码是否能销户
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IData qryDestroyUserInfo_1(String serialNumber) throws Exception
    {
        IData retData = new DataMap();
        // 取最近的一条用户信息
        IDataset userList = UserInfoQry.getLatestUserInfosBySerialNumber_1(serialNumber);
        if (IDataUtil.isNotEmpty(userList))
        {
            retData.put("USER_ID", userList.getData(0).getString("USER_ID"));
            retData.put("REMOVE_TAG", userList.getData(0).getString("REMOVE_TAG"));
            retData.put("DESTROY_TIME", userList.getData(0).getString("DESTROY_TIME", ""));
            // retData.put("IS_RED", false);
        } else
        {
            // 查询离网用户资料表
            userList = UserInfoQry.qryAllUserInfoBySnFromHis(serialNumber);
            if (IDataUtil.isNotEmpty(userList))
            {
                retData.put("USER_ID", userList.getData(0).getString("USER_ID"));
                retData.put("REMOVE_TAG", userList.getData(0).getString("REMOVE_TAG"));
                retData.put("DESTROY_TIME", userList.getData(0).getString("DESTROY_TIME", ""));
                // retData.put("IS_RED", false);
            }
        }
        return retData;
    }

	public IDataset QryDreamNetQry(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String removeTag = input.getString("REMOVE_TAG", "0");
		String dealTag = input.getString("DEAL_TAG", "00");// 默认单条数据发送
		String rsrvStr9 = input.getString("TAG_CHAR", "0");// 0,全部；1,自有业务，2,梦网业务
		String tagChar = input.getString("TAG_CHAR", "0");

		IData userInfo = getUserInfo4All(serialNumber);
		String userId = userInfo.getString("USER_ID");
		String productId = userInfo.getString("PRODUCT_ID");
		String brandCode = userInfo.getString("BRAND_CODE");
		String eparchyCode = userInfo.getString("EPARCHY_CODE");

		IDataset result = new DatasetList();

		result = getDreamNetPlatOrderInfo(userId, dealTag, rsrvStr9, productId, brandCode, eparchyCode);
		
		/*
		 * REQ201811290007  关于和多号0000查询及退订优化的需求
		 * 查询增加和多号副号显示
		 */
		if (IDataUtil.isNotEmpty(result))
		{
			for (int i = 0; i < result.size(); i++)
			{
				IData dataInfo = result.getData(i);
				String serviceId = dataInfo.getString("SERVICE_ID");
				String bizTypeCode = dataInfo.getString("BIZ_TYPE_CODE");
				String relateInstId = dataInfo.getString("RSRV_STR4");
				if("99941710".equals(serviceId)&&"74".equals(bizTypeCode)){
					IDataset relationList=OneCardMultiNoQry.qryRelationListByRelateInstId( dataInfo.getString("USER_ID"),OneCardMultiNoBean.RELATION_TYPE_CODE,relateInstId);
					if(null==relationList||relationList.isEmpty()){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"主号【" + dataInfo.getString("SERIAL_NUMBER") + "】不存在和多号副号订购关系！");
					}else if (null!=relationList && 1 == relationList.size()) {
						dataInfo.put("SERIAL_NUMBER_B", relationList.getData(0).getString("SERIAL_NUMBER_B"));
						dataInfo.put("ORDERNO", relationList.getData(0).getString("ORDERNO"));

						/*
						 * BUG20191119153838 和多号实体副号退订资料修改异常问题 by mengqx 20191121
						 * 查询接口提供副号类型字段
						 */
						dataInfo.put("CATEGORY", relationList.getData(0).getString("USER_ID_B").substring(relationList.getData(0).getString("USER_ID_B").length()-1));

					}
				}
			}
		}
		
		// 生效的订购关系
		if ("00".equals(dealTag))
		{
			if (!"2".equals(tagChar))
			{
				IDataset orderInfo = getDreamNetServiceOrderInfo(userId, dealTag, rsrvStr9, productId, brandCode, eparchyCode);
				result.addAll(orderInfo);
			}

			result = DataHelper.distinct(result, "SERVICE_ID,SERIAL_NUMBER_B", "");
			DataHelper.sort(result, "RSRV_STR10", 0, "BIZ_TYPE_CODE1", 0);
		}
		IDataset result2 = new DatasetList();
		if (IDataUtil.isNotEmpty(result))
		{
			for (int i = 0; i < result.size(); i++)
			{
				IData tempInfo = result.getData(i);

				String giftSerialNmuber = tempInfo.getString("GIFT_SERIAL_NUMBER");

				if (StringUtils.isNotBlank(giftSerialNmuber))
				{
					tempInfo.put("GIFT_TYPE", "1");
				} else
				{
					tempInfo.put("GIFT_TYPE", "0");
				}

				String price = tempInfo.getString("PRICE", "");
				if (price.length() > 0)
				{
					if (price.startsWith("."))
					{
						price = "0" + price;
					}
				}

				tempInfo.put("PRICE", price);

				String biztypecode = tempInfo.getString("BIZ_TYPE_CODE");
				String serviceID = tempInfo.getString("SERVICE_ID");
				String serv_type = tempInfo.getString("SERV_TYPE");
				String instId = tempInfo.getString("INST_ID");
				tempInfo.remove("INST_ID");
				if (StringUtils.isNotBlank(biztypecode) && biztypecode.equals("27") && StringUtils.isNotBlank(serv_type) && serv_type.equals("0"))
				{
					tempInfo.put("SERV_TYPE", "1");
				}

//				if (StringUtils.isNotBlank(biztypecode) && biztypecode.equals("74") && StringUtils.isNotBlank(serviceID))
//				{
//					IDataset gfspRela = RelaUUInfoQry.getRelaCoutByPK(userInfo.getString("USER_ID"), "M2");// T5:流量自由充(全量统付)
//					int gfspAllNum = gfspRela.getData(0).getInt("RECORDCOUNT");
//					IDataset discntIdAInfos = UserDiscntInfoQry.getAllDiscntByUser(userInfo.getString("USER_ID"), "99999062");
//					int iDiscnt = discntIdAInfos.size();
//					tempInfo.put("PRICE", (gfspAllNum - iDiscnt) * 5);
//
//				}

				if (StringUtils.isNotBlank(biztypecode) && biztypecode.equals("19") && StringUtils.isNotBlank(serviceID) && serviceID.equals("98001901"))
				{
					IData param2 = new DataMap();
					IDataset musicattrs = new DatasetList();
					param2.put("USER_ID", userInfo.getString("USER_ID"));
					param2.put("RELA_INST_ID", instId);
					musicattrs = WithinSetDreamNetQry.selPlatAttrInfo(param2);

					for (int j = 0; j < musicattrs.size(); j++)
					{
						String attr_code = musicattrs.getData(j).getString("ATTR_CODE");
						String attr_value = musicattrs.getData(j).getString("ATTR_VALUE");

						if ("302".equals(attr_code))
						{
							if ("2".equals(attr_value))
							{
								tempInfo.put("PRICE", "5");// 平台业务返回价格以元为单位
								tempInfo.put("BIZ_NAME", "咪咕音乐高级会员");
								tempInfo.put("BIZ_TYPE", "咪咕音乐高级会员");
								tempInfo.put("BILL_TYPE", "2");
								tempInfo.put("BILLFLG", "2");
								result2.add(tempInfo);
							} else if ("1".equals(attr_value))
							{
								tempInfo.put("PRICE", "0");
								tempInfo.put("BIZ_NAME", "咪咕音乐普通会员");
								tempInfo.put("BIZ_TYPE", "咪咕音乐普通会员");
								tempInfo.put("BILL_TYPE", "0");
								tempInfo.put("BILLFLG", "0");
							} else if ("3".equals(attr_value))
							{
								tempInfo.put("PRICE", "6");
								tempInfo.put("BIZ_NAME", "咪咕音乐特级会员");
								tempInfo.put("BIZ_TYPE", "咪咕音乐特级会员");
								tempInfo.put("BILL_TYPE", "2");
								tempInfo.put("BILLFLG", "2");
								result2.add(tempInfo);
							}

							break;
						}
					}
					continue;
				}

				if (StringUtils.isNotBlank(biztypecode) && biztypecode.equals("DX"))
				{
					IData param2 = new DataMap();
					IDataset musicattrs = new DatasetList();
					param2.put("USER_ID", userInfo.getString("USER_ID"));
					param2.put("RELA_INST_ID", instId);
					musicattrs = WithinSetDreamNetQry.selPlatAttrInfo(param2);

					for (int k = 0; k < musicattrs.size(); k++)
					{
						String attr_code = musicattrs.getData(k).getString("ATTR_CODE");
						String attr_value = musicattrs.getData(k).getString("ATTR_VALUE");

						if ("8899".equals(attr_code))
						{
							if ("XSMT".equals(attr_value))
							{
								tempInfo.put("PRICE", "0");// 平台业务返回价格以元为单位
								tempInfo.put("BIZ_NAME", "动感短信免费鉴赏会员业务");
								tempInfo.put("BIZ_TYPE", "动感短信免费鉴赏会员业务");
								tempInfo.put("BILLFLG", "0");
								tempInfo.put("BILL_TYPE", "0");
							} else if ("XSMTC3".equals(attr_value))
							{
								tempInfo.put("PRICE", "3");
								tempInfo.put("BIZ_NAME", "动感短信普通会员业务");
								tempInfo.put("BIZ_TYPE", "动感短信普通会员业务");
								tempInfo.put("BILLFLG", "2");
								tempInfo.put("BILL_TYPE", "2");
							} else if ("XSMTC5".equals(attr_value))
							{
								tempInfo.put("PRICE", "5");
								tempInfo.put("BIZ_NAME", "动感短信高级会员业务");
								tempInfo.put("BIZ_TYPE", "动感短信高级会员业务");
								tempInfo.put("BILLFLG", "2");
								tempInfo.put("BILL_TYPE", "2");
							}

							break;
						}
					}
				}
				result2.add(tempInfo);
			}
		}
		if (IDataUtil.isNotEmpty(result2)) {
			
			//查询用户的主产品对应的优惠
			IDataset newProductElements = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType("P",productId,"D","0898");
			StringBuilder stringBuilder = new StringBuilder();
			//如果产品的构成不为空，如果构成为空就查必选组优惠
			if(IDataUtil.isNotEmpty(newProductElements)){
				for(Object temp :newProductElements){
					IData data2 = (IData)temp;
					stringBuilder.append(data2.getString("OFFER_CODE"));
					stringBuilder.append(",");
				}
			}else{
				//如果产品的构成不为空，如果构成为空就查必选组优惠
				IDataset groupList = UpcCall.queryOfferGroups(productId);
				for(Object temp :groupList){
					IData data2 = (IData)temp;
					String selectFlag = data2.getString("SELECT_FLAG");
					//如果是必选组
					if("0".equals(selectFlag)){
						IDataset offerList = UpcCall.queryGroupComRelOfferByGroupId(data2.getString("GROUP_ID"), "");
						for(Object temp2 :offerList){
							IData data3 = (IData)temp2;
							String discntCode = data3.getString("OFFER_CODE");
							IDataset userDiscntList = UserDiscntInfoQry.getAllDiscntByUser(userInfo.getString("USER_ID"), discntCode);
							if(IDataUtil.isNotEmpty(userDiscntList)){
								stringBuilder.append(discntCode);
								stringBuilder.append(",");
							}
						}
					}
				}
				
				
			}
			
			String strTemp = stringBuilder.toString();
			String feepolicyId = strTemp.substring(0, strTemp.length()-1);
			
			
			for (int i = 0; i < result2.size(); i++) {
				IData svcInfo = result2.getData(i);
				String spCode = svcInfo.getString("SP_ID", "");
				String bizCode = svcInfo.getString("BIZ_CODE", "");
				String serviceId = svcInfo.getString("SERVICE_ID");

				IData acctResult = new DataMap();
				if ((StringUtils.isNotBlank(spCode) && StringUtils.isNotBlank(bizCode)) || StringUtils.isNotBlank(serviceId)) {
					acctResult = AcctCall.qrySpOrServiceRealFee(userId, spCode, bizCode, serviceId,feepolicyId);
				}
				if (IDataUtil.isNotEmpty(acctResult) && "0000".equals(acctResult.getString("X_RESULTCODE"))) {
					String realFee = acctResult.getString("REAL_FEE");
					svcInfo.put("PRICE", realFee);
				}
			}
		}		return result2;
	}

	public IData qryPlatSvcByAll(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SP_CODE");
		IDataUtil.chkParam(input, "BIZ_CODE");
		IDataUtil.chkParam(input, "BIZ_TYPE_CODE");
		String spCode = input.getString("SP_CODE");
		String bizCode = input.getString("BIZ_CODE");
		String bizTypeCode = input.getString("BIZ_TYPE_CODE");
		IDataset svcInfos = PlatSvcInfoQry.qryPlatSvcByAll(spCode, bizCode, bizTypeCode);

		if (IDataUtil.isEmpty(svcInfos))
		{
			CSAppException.apperr(ElementException.CRM_ELEMENT_152);
		}

		IData ret = svcInfos.getData(0);
		return ret;
	}

	/**
	 * 视频会议预约查询，接口名 ITF_CRM_VideoMeetingBookQry
	 * 
	 * @param IData
	 * @return IDataset
	 * @throws Exception
	 * @author huanghui@asiainfo-linkage.com
	 */
	public IDataset qryVideoMeetingBooking(IData param) throws Exception
	{
		IDataset resultList = new DatasetList();
		IData result = new DataMap();
		String sqlRef = "";
		String msisdn = param.getString("SERIAL_NUMBER", "");
		if (StringUtils.isBlank(msisdn))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
		}
		IData userInfo = UcaInfoQry.qryUserInfoBySn(msisdn);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String userId = userInfo.getString("USER_ID");
		IData param2 = new DataMap();
		param2.clear();
		param2.put("SERIAL_NUMBER", msisdn);
		param2.put("USER_ID", userId);
		sqlRef = "SEL_BY_USER_BIZCODE";
		param2.put("BIZ_TYPE_CODE", "32");
		// param2.put(Route.ROUTE_EPARCHY_CODE,
		// getVisit().getStaffEparchyCode());
		IDataset platSvcSet = PlatInfoQry.getUserPlatSvc(param2, sqlRef);
		if (IDataUtil.isEmpty(platSvcSet))
		{
			result.put("OPENED_CONF", "1"); // 约定的输出参数,标志是否已开通视频会议
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", "用户未开通基本业务");
			resultList.add(result);
			return resultList;
		}
		sqlRef = "SEL_BY_RESERVEORDER_E";
		IDataset bookingSet = PlatInfoQry.getUserPlatSvc(param2, sqlRef);
		if (IDataUtil.isEmpty(bookingSet))
		{
			result.put("OPENED_CONF", "0"); // 约定的输出参数,标志是否已开通视频会议
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", "没有用户预约信息");
			resultList.add(result);
			return resultList;
		}
		result.put("OPENED_CONF", "0"); // 约定的输出参数,标志是否已开通视频会议
		resultList.add(result);
		return resultList;
	}

	public IDataset queryAllElementIntf(IData input) throws Exception
	{

		String queryTag = IDataUtil.chkParam(input, "QUERY_TAG");
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String tradeStaffId = IDataUtil.chkParam(input, "TRADE_STAFF_ID");

		String elementType = input.getString("ELEMENT_TYPE_CODE", "");

		IDataset asps = TradeNpQry.getValidTradeNpBySn(serialNumber);
		if (IDataUtil.isEmpty(asps))
		{
			CSAppException.apperr(TradeNpException.CRM_TRADENP_1, serialNumber);
		}
		String asp = asps.getData(0).getString("ASP");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = userInfo.getString("USER_ID");
		IDataset userProducts = UserProductInfoQry.queryMainProductNow(userId);
		if (IDataUtil.isEmpty(userProducts))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}
		String productId = userProducts.getData(0).getString("PRODUCT_ID");
		String productInstId = userProducts.getData(0).getString("INST_ID");

		if (QUERY_TAG_ALL_ELEM_IN_PROD.equals(queryTag))
		{
			return getElemsInProdModel(productId, tradeStaffId, asp, elementType);
		} else if (QUERY_TAG_ALL_ELEM_IN_UPROD.equals(queryTag))
		{
			return getElemsInUProdModel(productId, userId, elementType, productInstId);
		} else if (QUERY_TAG_ALL_ELEM_NOTIN_UPROD.equals(queryTag))
		{
			IDataset plusElement = new DatasetList();
			IDataset allelems = getElemsInProdModel(productId, tradeStaffId, asp, elementType);
			IDataset uelems = getElemsInUProdModel(productId, userId, elementType, productInstId);

			boolean bExist = false;
			for (int i = 0; i < allelems.size(); i++)
			{
				bExist = false;
				IData element = allelems.getData(i);
				String elementid = element.getString("ELEMENT_ID");
				for (int j = 0; j < uelems.size(); j++)
				{
					String elementidTag = uelems.get(j, "ELEMENT_ID").toString();
					if (elementid.equals(elementidTag))
					{
						bExist = true;
						break;
					}
				}
				if (false == bExist)
				{
					plusElement.add(element);
				}
			}
			return plusElement;
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_99);
		}

		return null;
	}

	public IDataset queryBadnessReprotyInfos(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String indicSEQ = input.getString("INDICT_SEQ");
		IDataset badnessInfos = BadnessInfoQry.queryBadnessInfoByRecvId(indicSEQ);
		if (IDataUtil.isEmpty(badnessInfos))
		{

			CSAppException.apperr(CrmCommException.CRM_COMM_1043);
		}

		IData ret0 = badnessInfos.getData(0);
		ret0.put("INDICT_SEQ", ret0.getString("INFO_RECV_ID", ""));
		ret0.put("CURRENT_NODE", ret0.getString("STATE", ""));
		ret0.put("HANDING_DEPART", ret0.getString("DEAL_DEPART_ID", ""));
		ret0.put("HANDING_STAFF", ret0.getString("DEAL_STAFF_ID", ""));
		ret0.put("STAFF_CONTACT_PHONE", ret0.getString("CONTACT_SERIAL_NUMBER", ""));
		ret0.put("IS_ITERANCE", ret0.getString("REPEAT_REPORT", ""));
		ret0.put("IS_VALID", ret0.getString("IS_VALID", ""));
		rets.add(ret0);
		return rets;
	}

	public IDataset queryChangeProductInfoIBoss(IData input) throws Exception
	{
		this.validParams(input);
		// 本接口特定参数
		IDataUtil.chkParam(input, "NEW_PRODUCT_ID");
		String newProductId = input.getString("NEW_PRODUCT_ID");
		String serialNumber = input.getString("IDVALUE");
		IData normalUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(normalUserInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1);
		}

		// 验证用户的星级是否有权限办理产品
		IDataset checkProducts = CommparaInfoQry.getCommparaInfoByCode2("CSM", "1414", newProductId, "P", "0898");

		if (IDataUtil.isNotEmpty(checkProducts))
		{
			// 获取用户星级信息
			String userId = normalUserInfo.getString("USER_ID");
			UcaData uca = UcaDataFactory.getUcaByUserId(userId);
			String userCredit = uca.getUserCreditClass();

			int checkProductsI = Integer.parseInt(userCredit);

			boolean isValid = false;

			for (int i = 0, size = checkProducts.size(); i < size; i++)
			{
				String minClass = checkProducts.getData(i).getString("PARA_CODE3", "");
				String maxClass = checkProducts.getData(i).getString("PARA_CODE4", "");
				String realProductId = checkProducts.getData(i).getString("PARA_CODE1", "");

				if (!minClass.equals("") && !maxClass.equals(""))
				{
					int minClassI = Integer.parseInt(minClass);
					int maxClassI = Integer.parseInt(maxClass);

					if (checkProductsI >= minClassI && checkProductsI <= maxClassI)
					{
						isValid = true;
					}
				} else if (!minClass.equals(""))
				{
					int minClassI = Integer.parseInt(minClass);

					if (checkProductsI >= minClassI)
					{
						isValid = true;
					}
				} else if (!maxClass.equals(""))
				{
					int maxClassI = Integer.parseInt(maxClass);

					if (checkProductsI <= maxClassI)
					{
						isValid = true;
					}
				}

				// 如果匹配到星级的产品
				if (isValid)
				{
					newProductId = realProductId;
					input.put("NEW_PRODUCT_ID", realProductId);
					break;
				}

			}

			if (!isValid)
			{
				String strError = "用户星级不具备办理此产品！";
				Utility.error("2001", null, strError);
			}
		}

		// 先对身份凭证进行鉴权
		String identCode = input.getString("IDENT_CODE", "");
		String businessCode = input.getString("BUSINESS_CODE", "");
		String identCodeType = input.getString("IDENT_CODE_TYPE", "");
		String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");
		String userType = input.getString("USER_TYPE", "");

		String bizCodeType = input.getString("BIZ_TYPE_CODE", "");// 渠道编码
		String channelId = input.getString("CHANNEL_ID", "");// 微信微博渠道编码
		if(!("1".equals(input.getString("NO_CHECK_ABILITYPLAT")))){
			// 身份鉴权
			if ("62".equals(channelId) || "76".equals(channelId))
			{
				String userid = normalUserInfo.getString("USER_ID");
				// 校验客户凭证
				IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, serialNumber);
				if (IDataUtil.isEmpty(dataset))
				{
					CSAppException.apperr(CrmUserException.CRM_USER_938);
				}
			} else
			{
				IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
				if (IDataUtil.isEmpty(idents))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_915);
				}
			}
	
			if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_1103);
			}
	
			SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
		}
		input.put("ELEMENT_ID", newProductId);
		input.put("ELEMENT_TYPE_CODE", "P");
		input.put("MODIFY_TAG", "2");
		input.put("BOOKING_TAG", "0");

		IDataset dataset = CSAppCall.call("SS.ChangeProductIBossSVC.queryChangeProductInfoIBoss", input);

		return dataset;
	}

	public IDataset queryChangeProductIntf(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String productMode = IDataUtil.chkParam(input, "PRODUCT_MODE");
		String brandCode = IDataUtil.chkParam(input, "BRAND_CODE");

		String startDate = input.getString("START_DATE");
		String endDate = input.getString("END_DATE");
		String tradeStaffId = IDataUtil.chkParam(input, "TRADE_STAFF_ID");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		IDataset asps = TradeNpQry.getValidTradeNpBySn(serialNumber);
		if (IDataUtil.isEmpty(asps))
		{
			CSAppException.apperr(TradeNpException.CRM_TRADENP_1, serialNumber);
		}
		String asp = asps.getData(0).getString("ASP");

		IData user0 = user;
		String userId = user0.getString("USER_ID");
		IDataset userProducts = UserProductInfoQry.queryMainProductNow(userId);
		if (IDataUtil.isEmpty(userProducts))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}
		String productId = userProducts.getData(0).getString("PRODUCT_ID");

		if (productMode.equals("00"))
		{
			// if (ASP_YD.equals(asp))
			// {
			// rets = ProductInfoQry.getProductTrans(productId, brandCode,
			// startDate, endDate);
			// }
			// else
			// {
			// rets = ProductInfoQry.getNpProductTrans(productId, brandCode,
			// startDate, endDate);
			// }
			IDataset productInfos = UpcCall.qryJoinRelOfferInfoByOfferIdCatalogId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, null);
			if (IDataUtil.isNotEmpty(productInfos))
			{
				int productInfoSize = productInfos.size();
				for (int i = 0; i < productInfoSize; i++)
				{
					IData productInfo = productInfos.getData(i);
					String relBrandCode = "";
					if (StringUtils.isNotBlank(brandCode))
					{
						IDataset relOfferInfos = UpcCall.queryOfferComChaByCond(productInfo.getString("REL_OFFER_TYPE", ""), productInfo.getString("REL_OFFER_CODE", ""), "BRAND_CODE");
						if (IDataUtil.isEmpty(relOfferInfos))
						{
							continue;
						}
						relBrandCode = relOfferInfos.getData(0).getString("FIELD_VALUE", "");
						if (!brandCode.equals(relBrandCode) || StringUtils.isBlank(relBrandCode) || "".equals(relBrandCode))
						{
							continue;
						}
					}
					if (StringUtils.isNotBlank(startDate))
					{
						if (StringUtils.isEmpty(productInfo.getString("REL_VALID_DATE", "")))
						{
							continue;
						}
						if (SysDateMgr.compareTo(startDate, productInfo.getString("REL_VALID_DATE", "")) > 0)
						{
							continue;
						}
					}
					if (StringUtils.isNotBlank(endDate))
					{
						if (StringUtils.isEmpty(productInfo.getString("REL_EXPIRE_DATE", "")))
						{
							continue;
						}
						if (SysDateMgr.compareTo(productInfo.getString("REL_EXPIRE_DATE", ""), endDate) > 0)
						{
							continue;
						}
					}
					if (!ASP_YD.equals(asp))
					{// 如果号码归属运营商编码不是 1-移动 则过滤携转用户不能办理的必选业务的产品
						String productIdB = productInfo.getString("REL_OFFER_CODE", "");
						// 调产商品接口查询该产品是否有携转用户不能办理的必选业务，有则不显示该产品
						// TODO lijun17
						IDataset npOfferInfos = UpcCall.qryOfferLimitNpOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productIdB);
						if (IDataUtil.isNotEmpty(npOfferInfos))
						{
							continue;
						}
					}
					IData ret = new DataMap();
					ret.put("PRODUCT_ID", productInfo.getString("REL_OFFER_CODE", ""));
					ret.put("BRAND_CODE", relBrandCode);
					ret.put("PRODUCT_NAME", productInfo.getString("REL_OFFER_NAME", ""));
					ret.put("START_DATE", productInfo.getString("VALID_DATE", ""));
					ret.put("END_DATE", productInfo.getString("EXPIRE_DATE", ""));
					rets.add(ret);
				}
			}
		} else
		{
			// if ("1".equals(asp))
			// {
			// rets = ProductdLimitInfoQry.getLimitProduct(productId, brandCode,
			// startDate, endDate);
			// }
			// else
			// {
			// rets = ProductdLimitInfoQry.getLimitNPProduct(productId,
			// brandCode, startDate, endDate);
			// }
			IDataset offerInfos = UpcCall.queryOfferRelByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "4");
			if (IDataUtil.isNotEmpty(offerInfos))
			{
				int offerInfoSize = offerInfos.size();
				for (int i = 0; i < offerInfoSize; i++)
				{
					String relBrandCode = "";
					IData offerInfo = offerInfos.getData(i);
					if (StringUtils.isNotBlank(brandCode))
					{
						IDataset relOfferInfos = UpcCall.queryOfferComChaByCond(offerInfo.getString("REL_OFFER_TYPE", ""), offerInfo.getString("REL_OFFER_CODE", ""), "BRAND_CODE");
						if (IDataUtil.isEmpty(relOfferInfos))
						{
							continue;
						}
						relBrandCode = relOfferInfos.getData(0).getString("FIELD_VALUE", "");
						if (!brandCode.equals(relBrandCode) || StringUtils.isBlank(relBrandCode) || "".equals(relBrandCode))
						{
							continue;
						}
					}
					if (StringUtils.isNotBlank(startDate))
					{
						if (StringUtils.isEmpty(offerInfo.getString("REL_VALID_DATE", "")))
						{
							continue;
						}
						if (SysDateMgr.compareTo(startDate, offerInfo.getString("REL_VALID_DATE", "")) > 0)
						{
							continue;
						}
					}
					if (StringUtils.isNotBlank(endDate))
					{
						if (StringUtils.isEmpty(offerInfo.getString("REL_EXPIRE_DATE", "")))
						{
							continue;
						}
						if (SysDateMgr.compareTo(offerInfo.getString("REL_EXPIRE_DATE", ""), endDate) > 0)
						{
							continue;
						}
					}
					if (!ASP_YD.equals(asp))
					{// 如果号码归属运营商编码不是 1-移动 则过滤携转用户不能办理的必选业务的产品
						String productIdB = offerInfo.getString("REL_OFFER_CODE", "");
						// 调产商品接口查询该产品是否有携转用户不能办理的必选业务，有则不显示该产品
						IDataset npOfferInfos = UpcCall.qryOfferLimitNpOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productIdB);
						if (IDataUtil.isNotEmpty(npOfferInfos))
						{
							continue;
						}
					}
					IData ret = new DataMap();
					ret.put("PRODUCT_ID", offerInfo.getString("REL_OFFER_CODE", ""));
					ret.put("BRAND_CODE", relBrandCode);
					ret.put("PRODUCT_NAME", offerInfo.getString("REL_OFFER_NAME", ""));
					ret.put("START_DATE", offerInfo.getString("VALID_DATE", ""));
					ret.put("END_DATE", offerInfo.getString("EXPIRE_DATE", ""));
					rets.add(ret);
				}
			}
		}
		ProductPrivUtil.filterProductListByPriv(tradeStaffId, rets);
		return rets;
	}

	public IDataset queryChekInfo(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 手机号码
		IDataset tmp = UserCheck4BuyIstead.queryInfoInHalf(serialNumber);
		IDataset result = new DatasetList();// 插日志返回结果
		if (tmp.size() > 0)
		{
			IData tmp_Data = tmp.getData(0);
			if (tmp_Data.getString("CHECK_STATE").equals("1"))
			{
				IData reData = new DataMap();
				reData.put("CHECK_ID", tmp_Data.getString("CHECK_ID"));
				reData.put("SERIAL_NUMBER", tmp_Data.getString("SERIAL_NUMBER"));
				reData.put("STAFF_ID", tmp_Data.getString("STAFF_ID"));
				reData.put("IN_DATE", tmp_Data.getString("IN_DATE"));
				reData.put("X_RESULTCODE", "0");
				result.add(reData);
				return result;
			} else
			{
				IData reData = new DataMap();
				reData.put("X_RESULTCODE", "1");
				reData.put("X_RESULTINFO", "该手机号码号码最近半小时内无密码校验成功的记录");
				reData.put("X_RSPTYPE", "2");
				reData.put("X_RSPCODE", "2998");
				// common.error("该手机号码号码最近半小时内无密码校验成功的记录");
				result.add(reData);
				return result;
			}
		}

		IData reData = new DataMap();
		reData.put("X_RESULTCODE", "1");
		reData.put("X_RESULTINFO", "该手机号码号码最近半小时内无密码校验成功的记录");
		reData.put("X_RSPTYPE", "2");
		reData.put("X_RSPCODE", "2998");
		// common.error("该手机号码号码最近半小时内无密码校验成功的记录");
		result.add(reData);
		return result;

	}

	// 在结果集中取CREDIT_CLASS CREDIT_VALUE
	public IData queryCreditInfos(String userId, String typeId) throws Exception
	{
		IDataset result = AcctCall.getUserCreditInfos(typeId, userId);
		if (IDataUtil.isEmpty(result))
		{
			// 报错
			CSAppException.apperr(BofException.CRM_BOF_018);
		}

		IData creditInfo = result.getData(0);
		return creditInfo;
	}

	// {"TYPE_CODE":["product","discnt","service"],"TYPE_ID":["111","111","111"]}
	public IDataset queryCrmArgument(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		// IDataset types = new DatasetList(input.getString("TYPE_CODE","[]"));
		IDataset types = input.getDataset("TYPE_CODE");
		// IDataset ids = new DatasetList(input.getString("TYPE_ID","[]"));
		IDataset ids = input.getDataset("TYPE_ID");
		if (types.size() != ids.size())
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1036);
		}

		for (int i = 0; i < types.size(); i++)
		{
			String type = types.get(i).toString();
			String id = ids.get(i).toString();
			if (!"product".equals(type) && !"discnt".equals(type) && !"service".equals(type))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_1037, type);
			}
			if (StringUtils.isBlank(id))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_1038);
			}
			if (!id.matches("[0-9]*"))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_1039, id);
			}
		}

		// 查询数据
		for (int i = 0; i < types.size(); i++)
		{
			String type = types.get(i).toString();
			String id = ids.get(i).toString();

			IData info = new DataMap();
			if ("product".equals(type))
			{
				IData product = UProductInfoQry.qryProductByPK(id);
				if (IDataUtil.isNotEmpty(product))
				{
					info.put("TYPE_NAME", product.getString("PRODUCT_NAME", ""));
					info.put("TYPE_EXPLAIN", product.getString("PRODUCT_EXPLAIN", ""));
				} else
				{
					info.put("TYPE_NAME", "");
					info.put("TYPE_EXPLAIN", "");
				}
			} else if ("discnt".equals(type))
			{
				IData discnt = UDiscntInfoQry.getDiscntInfoByPk(id);
				if (IDataUtil.isNotEmpty(discnt))
				{
					info.put("TYPE_NAME", discnt.getString("DISCNT_NAME", ""));
					info.put("TYPE_EXPLAIN", discnt.getString("DISCNT_EXPLAIN", ""));
				} else
				{
					info.put("TYPE_NAME", "");
					info.put("TYPE_EXPLAIN", "");
				}
			} else if ("service".equals(type))
			{
				IData svcs = USvcInfoQry.qryServInfoBySvcId(id);
				if (IDataUtil.isNotEmpty(svcs))
				{
					info.put("TYPE_NAME", svcs.getString("SERVICE_NAME", ""));
					info.put("TYPE_EXPLAIN", "");
				} else
				{
					info.put("TYPE_NAME", "");
					info.put("TYPE_EXPLAIN", "");
				}
			}

			if (IDataUtil.isNotEmpty(info))
			{
				rets.add(info);
			}
		}

		return rets;
	}

	public IDataset queryCumuScore(IData input) throws Exception
	{
		String cumu_id = IDataUtil.chkParam(input, "CUMU_ID");
		String cu_pass = IDataUtil.chkParam(input, "CU_PASS");

		IDataset dataset = new DatasetList();
		IDataset datasetcompara = new DatasetList();
		IDataset datasetregi = new DatasetList();
		IDataset datasetregitwo = new DatasetList();
		IDataset datasetbook = new DatasetList();
		IDataset datasetsmsrec = new DatasetList();
		IData param = new DataMap();
		param.put("CUMU_ID", cumu_id);
		param.put("CU_PASS", cu_pass);

		IDataset retDataList = new DatasetList();

		dataset = ChnlInfoQry.selByCumuinfo(cumu_id, cu_pass);
		datasetcompara = ChnlInfoQry.selByCommpara(cumu_id, cu_pass);

		IData rData = new DataMap();
		if (dataset != null && dataset.size() > 0)
		{
			rData = dataset.getData(0);
			IData da = new DataMap();
			IData dt = new DataMap();
			if (datasetcompara != null && datasetcompara.size() > 0)
			{
				int checksum = 0;
				int dealsum = 0;
				for (int i = 0; i < datasetcompara.size(); i++)
				{

					dt = datasetcompara.getData(i);
					dt.put("CUMU_ID", cumu_id);
					datasetregi = ChnlInfoQry.selByRegi(cumu_id, dt.getString("PARA_CODE2"));// 三个月内成功登记XX分
					datasetregitwo = ChnlInfoQry.selByRegiTwo(cumu_id, dt.getString("PARA_CODE2"));// 三个月内成功积分XX分

					String check = dt.getString("PARA_CODE4") + "_" + "CHECK";// 三个月内成功登记XX分
					String deal = dt.getString("PARA_CODE4") + "_" + "DEAL";// 三个月内成功积分XX分

					if (datasetregi != null && datasetregi.size() > 0)
					{
						da.put(check, datasetregi.getData(0).getString("SCORE"));// 三个月内成功登记XX分
						checksum = Integer.parseInt(datasetregi.getData(0).getString("SCORE", "0")) + checksum;// 三个月内成功登记XX分总和

					} else
					{
						da.put(check, "0");// 三个月内成功登记XX分
						checksum = 0 + checksum;// 三个月内成功登记XX分总和
					}

					if (datasetregitwo != null && datasetregitwo.size() > 0)
					{
						da.put(deal, datasetregitwo.getData(0).getString("SCORE"));// 三个月内成功积分XX分
						dealsum = Integer.parseInt(datasetregitwo.getData(0).getString("SCORE", "0")) + dealsum;// 三个月内成功积分XX分总和
					} else
					{
						da.put(deal, "0");// 三个月内成功积分XX分
						dealsum = 0 + dealsum;// 三个月内成功积分XX分总和
					}
				}
				datasetbook = ChnlInfoQry.selByBook(cumu_id);// 三个月内未兑换XX分
				if (datasetbook != null && datasetbook.size() > 0)
				{
					da.put("EXCHANGE_N", datasetbook.getData(0).getString("LAST_SCORE"));
				} else
				{
					da.put("EXCHANGE_N", "0");
				}
				datasetsmsrec = ChnlInfoQry.selBySmsRec(cumu_id);// 三个月内兑换XX分
				if (datasetsmsrec != null && datasetsmsrec.size() > 0)
				{
					da.put("EXCHANGE_Y", datasetsmsrec.getData(0).getString("CHANGE_SCORE"));
				} else
				{
					da.put("EXCHANGE_Y", "0");
				}
				da.put("CHECK_SUM", checksum);
				da.put("DEAL_SUM", dealsum);
				retDataList.add(da);
				return retDataList;
			}
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1141);
		}
		retDataList.add(rData);
		return retDataList;
	}

	public IDataset queryCustInfo(IData input) throws Exception
	{
		IDataset rets = new DatasetList();

		String xGetMode = IDataUtil.chkParam(input, "X_GETMODE");
		if (xGetMode.equals(X_GETMODE_GETPERINFO_BY_PSPT))
		{
			// 0-根据证件号码获取个人客户资料
			String psptId = IDataUtil.chkParam(input, "PSPT_ID");
			String ptptTypeCode = IDataUtil.chkParam(input, "PSPT_TYPE_CODE");
			rets = CustomerInfoQry.getPersonCustInfoByPsptCustType(psptId, ptptTypeCode, CUST_TYPE_PER, null);
			personTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_GETPERINFO_BY_CUSTID))
		{
			// 1-根据客户标识获取个人客户资料
			String custId = IDataUtil.chkParam(input, "CUST_ID");
			rets = CustPersonInfoQry.qryPerCustInfoByCustIDCustType(custId, CUST_TYPE_PER);
			personTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_GETPERINFO_BY_CUSTNAME))
		{
			// 2-根据客户姓名获取个人客户资料
			String custName = IDataUtil.chkParam(input, "CUST_NAME");
			rets = CustPersonInfoQry.qryPerCustInfoByCustNameCustType(custName, CUST_TYPE_PER);
			personTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_GETGRPINFO_BY_GROUPID))
		{
			// 3-根据集团编码获取集团客户资料
			String groupId = IDataUtil.chkParam(input, "GROUP_ID");
			rets = GrpInfoQry.getGroupCustInfoByGrpIdCustType(groupId, CUST_TYPE_GRP);
			groupTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_GETGRPINFO_BY_PSPT))
		{
			// 4-根据证件号码获取集团客户资料
			String psptId = IDataUtil.chkParam(input, "PSPT_ID");
			String ptptTypeCode = IDataUtil.chkParam(input, "PSPT_TYPE_CODE");
			rets = GrpInfoQry.getGroupCustInfoByPsptIdPsptTypeCustType(psptId, ptptTypeCode, CUST_TYPE_GRP);
			groupTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_GETGRPINFO_BY_CUSTNAME))
		{
			// 5-根据集团客户名称获取集团客户资料
			String custName = IDataUtil.chkParam(input, "CUST_NAME");
			rets = CustomerInfoQry.queryCustInfoByCustTypeCustName(custName, CUST_TYPE_GRP);
			groupTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_CUSTINFO_BY_SN_ALL))
		{
			// 6-输入服务号码取所有用户
			String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			rets = CustomerInfoQry.queryCustUserInfoBySN(serialNumber);
			personTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_CUSTINFO_BY_SN_ABNORMAL))
		{
			// 7-输入服务号码取所有非正常用户
			String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			rets = CustomerInfoQry.getABNormalCustInfoBySn(serialNumber);
			personTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_CUSTINFO_BY_SN_NORMAL))
		{
			// 8-输入服务号码取正常用户
			String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			rets = CustomerInfoQry.getNormalCustInfoBySN(serialNumber);
			personTransCodeToName(rets);
		} else if (xGetMode.equals(X_GETMODE_CUSTINFO_BY_USERID))
		{
			// 9-根据用户标识获取个人客户资料
			String userId = IDataUtil.chkParam(input, "USER_ID");
			rets = CustomerInfoQry.getNormalCustInfoByUserId(userId);
			if (IDataUtil.isNotEmpty(rets))
			{
				IData user0 = UcaInfoQry.qryUserInfoByUserId(userId);
				if (IDataUtil.isNotEmpty(user0))
				{
					rets.getData(0).put("SERIAL_NUMBER", user0.get("SERIAL_NUMBER"));
				}
			}
			personTransCodeToName(rets);
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1042, xGetMode);
		}

		if (IDataUtil.isEmpty(rets))
		{
			CSAppException.apperr(CustException.CRM_CUST_160);
		}

		return rets;
	}

	/**
	 * 代理商终端押金查询接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 * @date 2014-07-08 21:40:16
	 */
	public IData queryDepartMoney(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "DEPART_ID");// 参数校验

		String chnlId = input.getString("DEPART_ID");

		IData rData = new DataMap();

		IDataset departMoneys = ChnlInfoQry.queryDepartMoney(chnlId);
		if (IDataUtil.isNotEmpty(departMoneys))
		{
			rData = departMoneys.getData(0);
			rData.put("X_RESULTCODE", "0");
			rData.put("X_RESULTINFO", "成功!");
			return rData;
		}

		rData.put("X_RESULTCODE", "0");
		rData.put("X_RESULTINFO", "该部门ID无对应数据");
		rData.put("MONEY", "0");
		return rData;
	}

	public IDataset queryDiscnt4ActByUserId1(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = user.getString("USER_ID");
		return UserDiscntInfoQry.getDiscnt4ActByUserId1(userId);
	}

	public IDataset queryDiscnt4ActByUserId2(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = user.getString("USER_ID");
		return UserDiscntInfoQry.getDiscnt4ActByUserId2(userId);
	}

	/**
	 * 产品优惠查询
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 * @date 2014-07-03 15:24:38
	 */
	public IDataset queryDiscntByProduct(IData input) throws Exception
	{
		String productId = input.getString("PRODUCT_ID");// 产品ID
		String staffId = input.getString("TRADE_STAFF_ID");// 工号

		if (StringUtils.isBlank(productId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "PRODUCT_ID");
		}

		if (StringUtils.isBlank(staffId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "TRADE_STAFF_ID");
		}

		// IDataset discntInfos = DiscntInfoQry.getDiscntByProduct(productId);
		IDataset productInfos = ProductUtils.offerToElement(UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, null, null), productId);
		IDataset discntInfos = new DatasetList();
		if (IDataUtil.isNotEmpty(productInfos))
		{
			int productInfosSize = productInfos.size();
			for (int i = 0; i < productInfosSize; i++)
			{
				IData productInfo = productInfos.getData(i);
				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(productInfo.getString("ELEMENT_TYPE_CODE", "")))
				{
					productInfo.put("DISCNT_CODE", productInfo.getString("ELEMENT_ID", ""));
					productInfo.put("DISCNT_NAME", productInfo.getString("ELEMENT_NAME", ""));
					productInfo.put("DISCNT_EXPLAIN", productInfo.getString("ELEMENT_EXPLAIN", ""));
					discntInfos.add(productInfo);
				}
			}
		}
		if (IDataUtil.isEmpty(discntInfos))
		{
			// 根据PRODUCT_ID没有找到对应的优惠信息
			CSAppException.apperr(ElementException.CRM_ELEMENT_39);
		}

		// 根据员工权限过滤产品
		DiscntPrivUtil.filterDiscntListByPriv(staffId, discntInfos);

		return discntInfos;
	}

	public IDataset queryExchangeList(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String eparachyCode = input.getString(Route.ROUTE_EPARCHY_CODE);
		rets = QryScoreInfo.queryExchangeList(eparachyCode);
		return rets;
	}

	/**
	 * 查询产品的必选包优惠
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 * @date 2014-07-09 15:58:19
	 */
	public IDataset queryForcePackageDiscnt(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");
		IDataUtil.chkParam(input, "TRADE_STAFF_ID");

		String productId = input.getString("PRODUCT_ID");
		String staffId = input.getString("TRADE_STAFF_ID");

		IDataset discntList = PkgElemInfoQry.queryDiscntOfForcePackage(productId);

		DiscntPrivUtil.filterDiscntListByPriv(staffId, discntList);

		return discntList;
	}

	public IDataset queryGoToneDiscntInfo(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		IData ret0 = new DataMap();

		// 1、身份校验
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String sessionId = IDataUtil.chkParam(input, "SESSIONID");
		// String inCredenceNo = input.getString("CREDENCE_NO","");//IdentCode
		// IDENTCODE
		String inCredenceNo = input.getString("IDENTCODE", "");
		String inModeCode = getVisit().getInModeCode();
		if (StringUtils.isBlank(inModeCode))
		{
			inModeCode = "L";
		}
		String inMediaCode = input.getString("IN_MEDIA_CODE", "L");
		String busiTypeRemark = input.getString("BUSINESS_TYPE_REMARK", "");
		String tradeDepartId = getVisit().getDepartId();
		if (StringUtils.isBlank(tradeDepartId))
		{
			tradeDepartId = "1";
		}

		IDataset users = UserInfoQry.getUserInfoBySn(serialNumber, "0", "00", null);
		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		IData user0 = users.getData(0);
		String userId = user0.getString("USER_ID");
		String custId = user0.getString("CUST_ID");
		IDataset userProducts = UserProductInfoQry.queryMainProductNow(userId);
		if (IDataUtil.isEmpty(userProducts))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}
		String productId = userProducts.getData(0).getString("PRODUCT_ID");
		String brandCode = userProducts.getData(0).getString("BRAND_CODE");
		String startDate = userProducts.getData(0).getString("START_DATE");
		String endDate = userProducts.getData(0).getString("END_DATE");

		// IDataset percusts = UcaInfoQry.qryPerInfoByCustId(custId);
		IData percusts = UcaInfoQry.qryCustomerInfoByCustId(custId);
		if (IDataUtil.isEmpty(percusts))
		{
			CSAppException.apperr(CustException.CRM_CUST_111);
		}
		String custName = percusts.getString("CUST_NAME");

		// 根据传入SESSION_ID和SERIAL_NUMBER查询该用户wap凭证信息，查询日期在当前日期有效期内的
		IDataset wapsessions = WapSessionInfoQry.queryWapSession(sessionId, serialNumber);
		if (IDataUtil.isEmpty(wapsessions))
		{
			CSAppException.apperr(WapException.CRM_WAP_700002);
		}
		String credenceNo = wapsessions.getData(0).getString("CREDENCE_NO", "");// 用户身份凭证号
		if (StringUtils.isBlank(credenceNo))
		{
			CSAppException.apperr(WapException.CRM_WAP_700003);
		}
		if (false == inCredenceNo.equals(credenceNo))
		{
			CSAppException.apperr(WapException.CRM_WAP_700004);
		}

		// 2、记录操作日志
		IData contact = new DataMap();
		String seqCustContactId = SeqMgr.getCustContact();
		String seqCustContactTraceId = SeqMgr.getCustContactTrace();
		String curdate = SysDateMgr.date2String(java.util.Calendar.getInstance().getTime(), SysDateMgr.PATTERN_STAND);

		contact.put("CUST_CONTACT_TRACE_ID", seqCustContactTraceId);
		contact.put("CUST_CONTACT_ID", seqCustContactId);
		contact.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		contact.put("CONTACT_MODE", "P"); // CONTACT_MODE 业务类型写死为P
		contact.put("CONTACT_SUB_MODE", "P");
		contact.put("CUST_ID", user0.getString("CUST_ID"));
		contact.put("USER_ID", userId);
		contact.put("SERIAL_NUMBER", serialNumber);
		contact.put("EPARCHY_CODE", user0.getString("EPARCHY_CODE"));
		contact.put("PRODUCT_ID", productId);
		contact.put("CITY_CODE", user0.getString("CITY_CODE"));
		contact.put("IN_MODE_CODE", inModeCode);
		contact.put("IN_MEDIA_CODE", inMediaCode);
		contact.put("START_TIME", curdate);
		contact.put("REMARK", busiTypeRemark);
		contact.put("CUST_NAME", custName);
		contact.put("CHANNEL_ID", tradeDepartId); // 取不到则取默认值
		contact.put("SUB_CHANNEL_ID", tradeDepartId);
		boolean contactOk = CustContactInfoQry.insertInfo(contact);
		if (contactOk == false)
		{
			CSAppException.apperr(WapException.CRM_WAP_700005);
		}

		// 3、业务基本规则检查
		// 3.1 未激活用户、申请停机、欠费停机、销户用户均不可以办理
		IDataset mainSvcStates = UserSvcStateInfoQry.queryUserValidMainSVCStateForWap(userId);
		if (IDataUtil.isEmpty(mainSvcStates))
		{
			CSAppException.apperr(SvcException.CRM_SVC_5);
		}
		IData mainSvcState0 = mainSvcStates.getData(0);
		String stateCode = mainSvcState0.getString("STATE_CODE", "");
		if (StringUtils.isBlank(stateCode))
		{
			CSAppException.apperr(SvcException.CRM_SVC_5);
		}
		if (false == StringUtils.equals(stateCode, "0"))
		{
			CSAppException.apperr(SvcException.CRM_SVC_6);
		}

		// 3.2 黑名单用户不能办理
		IDataset userBlackWhites = UserBlackWhiteInfoQry.getBlackUserInfo(userId, "-1", serialNumber, "B");
		if (IDataUtil.isNotEmpty(userBlackWhites))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1112);
		}
		// 3.3 未进行实名登记用户不可以办理
		if (false == StringUtils.equals(percusts.getString("IS_REAL_NAME", ""), CUST_REALNAME_YES))
		{
			CSAppException.apperr(CustException.CRM_CUST_2009);
		}

		// 4、查询用户已办理套餐列表
		IDataset usedOffers = new DatasetList();
		IDataset commparaInfos = CommparaInfoQry.getCommparaByAttrCode1("CSM", "2688", productId, "ZZZZ", null);
		if (IDataUtil.isNotEmpty(commparaInfos))
		{
			IData commparaInfo0 = commparaInfos.getData(0);
			String packId = commparaInfo0.getString("PARAM_CODE", "");
			String packName = commparaInfo0.getString("PARAM_NAME", "");
			startDate = startDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
			endDate = endDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

			IData usedOffer0 = new DataMap();

			usedOffer0.put("BRAND", InterfaceUtil.convertBrandCode(brandCode));
			usedOffer0.put("PACKID", packId);
			usedOffer0.put("PACKNAME", packName);
			usedOffer0.put("OPRBEGTIME", startDate);
			usedOffer0.put("OPRENDTIME", endDate);
			usedOffers.add(usedOffer0);
		}
		ret0.put("ENJOYINFO", usedOffers);

		// 4、查询用户可办理标准套餐列表
		IDataset mayOffers = new DatasetList();

		IDataset paraDatas = CommparaInfoQry.getCommByParaAttr("CSM", "2688", CSBizBean.getTradeEparchyCode());

		if (IDataUtil.isNotEmpty(paraDatas))
		{
			for (int i = 0; i < paraDatas.size(); i++)
			{
				IData para = paraDatas.getData(i);
				String paraCode1 = para.getString("PARA_CODE1");
				if (StringUtils.equals(productId, paraCode1))
				{
					paraDatas.remove(i);
					i--;
					continue;
				}
				IData offer = UpcCall.queryOfferByOfferId("P", paraCode1);
				if (IDataUtil.isNotEmpty(offer))
				{
					para.put("ENABLE_TAG", offer.getString("ENABLE_MODE"));
					para.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
					para.put("ELEMENT_DESC", offer.getString("DESCRIPTION"));
				}
			}
		}

		// IDataset mayGoneOffers =
		// ProductInfoQry.getProductForGotone(productId, "2688");
		for (int i = 0; i < paraDatas.size(); i++)
		{
			IData mayGoneOffer = paraDatas.getData(i);
			String paramCode = mayGoneOffer.getString("PARAM_CODE");// 套餐编码（一级BOSS侧）
			String productName = mayGoneOffer.getString("PRODUCT_NAME");// 套餐名称
			String enableTag = mayGoneOffer.getString("ENABLE_TAG");// 生效方式类型
			String enableTagDesc = InterfaceUtil.convertEnabletag(enableTag);// 生效方式描述
			String elementDesc = mayGoneOffer.getString("ELEMENT_DESC");// 套餐描述

			IData dataTmp = new DataMap();
			dataTmp.put("PACKID", paramCode);
			dataTmp.put("PACKNAME", productName);
			dataTmp.put("EFFECTTYPE", enableTag);
			dataTmp.put("EFFECTDESC", enableTagDesc);
			dataTmp.put("PACKDESC", elementDesc);
			mayOffers.add(dataTmp);
		}
		ret0.put("UNENJOYINFO", mayOffers);

		rets.add(ret0);
		return rets;
	}

	public IDataset queryHistoryList(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = input.getString("SERIAL_NUMBER");
		String removeTag = input.getString("REMOVE_TAG");
		String startDate = input.getString("START_DATE");
		String endDate = input.getString("END_DATE");
		String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
		String eparachyCode = input.getString(Route.ROUTE_EPARCHY_CODE);
		rets = QryScoreInfo.queryHistoryList(serialNumber, removeTag, startDate, endDate, tradeTypeCode, eparachyCode);
		return rets;
	}

	public IDataset queryIBossBusiness(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		IData ret0 = new DataMap();

		String serialNumber = input.getString("SERIAL_NUMBER", "");
		if (StringUtils.isBlank(serialNumber))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1046);
		}

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = userInfo.getString("USER_ID");

		int codeOper = Integer.valueOf(input.getString("QRYTYPE"));
		String noticeContent = "";

		IData sms = new DataMap();
		sms.put("RECV_OBJECT", serialNumber);
		sms.put("RECV_ID", userId);
		sms.put("BRAND_CODE", "");
		sms.put("DEAL_STAFFID", getVisit().getStaffId());
		sms.put("DEAL_DEPARTID", getVisit().getDepartId());
		sms.put("REVC1", "");
		sms.put("REVC2", "");
		sms.put("REVC3", "");
		sms.put("REVC4", "");
		switch (codeOper)
		{
		case 1:// 查询积分余额
			noticeContent = "您好！您可以直接回复序号进行业务查询与办理：\n\r101.查询余额\n\r" + "102.查询实时话费\n\r103.查询历史话费\n\r104.查询充值缴费记录\n\r105." + "查询账单\n\r106.定制话费账单\n\r107.积分/M值服务\n\r108.账户余额提醒\n\r109." + "查询套餐\n\r110.短信充值\n\r中国移动";
			break;
		case 2:// 查询GPRS服务
			IDataset usersvcs = UserSvcInfoQry.queryUserSvcByUserId(userId, "22", null);
			String state = "YESORNO";
			String smstr = "您好！您" + state + "开通移动数据流量功能，目前可使用。如需办理其他业务请直接发送业务名称到10086。中国移动";
			noticeContent = IDataUtil.isNotEmpty(usersvcs) ? smstr.replaceAll(state, "已") : smstr.replaceAll(state, "未");
			break;
		case 3:// 查询流量提醒服务
			noticeContent = "您好！【流量提醒介绍】。您可以直接回复序号进行业务查询与办理：\n\r30200501.开通流量提醒\n\r30200502.取消流量提醒\n\r中国移动";
			break;
		default:
			noticeContent = "您好!执行此次操作失败,请您稍后再试。如需办理其他业务请直接发送业务名称到10086。中国移动";
			ret0.put("X_RESULTCODE", "2998");
			ret0.put("X_RESULTINFO", "指令操作失败!");
			ret0.put("X_RSPTYPE", "2");
			ret0.put("X_RSPCODE", "2998");
		}
		sms.put("NOTICE_CONTENT", noticeContent);
		SmsSend.insSms(sms);
		rets.add(ret0);
		return rets;
	}

	public IDataset queryLotteryWinners(IData input) throws Exception
	{

		String activityNumber = IDataUtil.chkParam(input, "ACTIVITY_NUMBER");
		String beginDate = IDataUtil.chkParam(input, "BEGIN_DATE");
		String endDate = IDataUtil.chkParam(input, "END_DATE");

		String userId = input.getString("USER_ID", "");
		String execFlag = input.getString("EXEC_FLAG", "");
		String prizeTypeCode = input.getString("PRIZE_TYPE_CODE", "");
		String serialNumber = input.getString("SERIAL_NUMBER", "");

		int rowNumber = 10000;
		if (input.containsKey("ROW_NUMBER"))
		{
			rowNumber = input.getInt("ROW_NUMBER", 0);
			if (rowNumber <= 0)
			{
				rowNumber = 10000;
			}
		}

		IDataset lotteryWinners = SmsQry.queryLotteryWinners("1", activityNumber, beginDate, endDate, String.valueOf(rowNumber), userId, execFlag, prizeTypeCode, serialNumber);

		for (int i = 0; i < lotteryWinners.size(); i++)
		{
			IData lotteryWinner = lotteryWinners.getData(i);
			String prizeTypeName = StaticUtil.getStaticValue("UECLOTTERY_PRIZE_TYPE_CODE" + lotteryWinner.getString("ACTIVITY_NUMBER"), lotteryWinner.getString("PRIZE_TYPE_CODE"));
			lotteryWinner.put("PRIZE_NAME", prizeTypeName);
			lotteryWinner.remove("PRIZE_ODDS_1");
			lotteryWinner.remove("PRIZE_ODDS_2");
			lotteryWinner.remove("PRIZE_ODDS_3");
			lotteryWinner.remove("PRIZE_ODDS_4");
			lotteryWinner.remove("PRIZE_ODDS_5");
			lotteryWinner.remove("PRIZE_ODDS_6");
			lotteryWinner.remove("REVC1");
			lotteryWinner.remove("REVC2");
			lotteryWinner.remove("REVC3");
			lotteryWinner.remove("REVC4");
			lotteryWinner.remove("REVC5");
		}
		return lotteryWinners;
	}

	public IDataset queryMainProductIBoss(IData input) throws Exception
	{
		IDataset rets = new DatasetList();

		String inModeCode = input.getString("IN_MODE_CODE", "");
		if (StringUtils.isBlank(inModeCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IN_MODE_CODE");
		}

		String kindId = input.getString("KIND_ID", "");
		if (StringUtils.isBlank(kindId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "KIND_ID");
		}

		String idType = input.getString("IDTYPE", "");
		if (StringUtils.isBlank(idType))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDTYPE");
		}

		String idValue = input.getString("IDVALUE", "");
		if (StringUtils.isBlank(idValue))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDVALUE");
		}

		String identCode = input.getString("IDENT_CODE", "");
		if (StringUtils.isBlank(identCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDENT_CODE");
		}

		String bizTypeCode = input.getString("BIZ_TYPE_CODE", "");
		if (StringUtils.isBlank(bizTypeCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "BIZ_TYPE_CODE");
		}

		String tradeStaffId = input.getString("TRADE_STAFF_ID", "");
		if (StringUtils.isBlank(tradeStaffId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "TRADE_STAFF_ID");
		}

		String tradeDepartId = input.getString("TRADE_DEPART_ID", "");
		if (StringUtils.isBlank(tradeDepartId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "TRADE_DEPART_ID");
		}

		String tradeCityCode = input.getString("TRADE_CITY_CODE", "");
		if (StringUtils.isBlank(tradeCityCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "TRADE_CITY_CODE");
		}

		String serialNumber = input.getString("SERIAL_NUMBER");
		String businessCode = input.getString("BUSINESS_CODE", "");
		String identCodeType = input.getString("IDENT_CODE_TYPE", "");
		String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");
		String userType = input.getString("USER_TYPE", "");

		String bizCodeType = input.getString("BIZ_TYPE_CODE", "");// 渠道编码
		String channelId = input.getString("CHANNEL_ID", "");// 微信微博渠道编码

		if(!("1".equals(input.getString("NO_CHECK_ABILITYPLAT")))){
			/***************************** 合版本 duhj 2017/5/3 start ***********************************/
			// 身份鉴权
			if ("62".equals(channelId) || "76".equals(channelId) || ("62".equals(bizCodeType)&&!"UMMP".equals(input.getString("ORIGDOMAIN"))) || "76".equals(bizCodeType))
			{// 微信微博身份鉴权
				// 校验客户凭证
				IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, serialNumber);
				if (IDataUtil.isEmpty(dataset))
				{
					CSAppException.apperr(CrmUserException.CRM_USER_938);
				}
			} else if (CustServiceHelper.isCustomerServiceChannel(bizCodeType))
			{// 一级客服升级业务能力开放平台身份鉴权
				IData identPara = new DataMap();
				identPara.put("SERIAL_NUMBER", serialNumber);
				identPara.put("IDENT_CODE", identCode);
				CustServiceHelper.checkCertificate(identPara);
			} else
			{ // if("07".equals(bizCodeType)){//移动商城身份鉴权
				IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
				if (IDataUtil.isEmpty(idents))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_915);
				}
			}
			/***************************** 合版本 duhj 2017/5/3 end ***********************************/
	
			if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_1103);
			}
	
			SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
		}
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String userId = userInfo.getString("USER_ID");

		IDataset userProducts = UserProductInfoQry.queryMainProductNow(userId);
		if (IDataUtil.isEmpty(userProducts))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}
		String productId = userProducts.getData(0).getString("PRODUCT_ID");
		String brandCode = userProducts.getData(0).getString("BRAND_CODE");

		String nowPackDesc = "";
		IData product = UProductInfoQry.qryProductByPK(productId);
		if (IDataUtil.isNotEmpty(product))
		{
			nowPackDesc = product.getString("PRODUCT_NAME");
		}

		String nextPackCode = "";// 下帐期主套餐标识
		String nextPackDesc = "";// 下账期套餐描述

		IDataset userAcctdays = UserAcctDayInfoQry.getUserAcctDay(userId);
		if (IDataUtil.isEmpty(userAcctdays))
		{
			CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_132);
		}
		String firstDayNextAcct = userAcctdays.getData(userAcctdays.size() - 1).getString("FIRST_DATE");

		IDataset userProducts2 = UserProductInfoQry.queryProductByUserIdAndStartDate(firstDayNextAcct, userId);
		if (IDataUtil.isNotEmpty(userProducts2))
		{
			nextPackCode = userProducts2.getData(0).getString("PRODUCT_ID");
			IData product2 = UProductInfoQry.qryProductByPK(productId);
			nextPackDesc = product2.getString("PRODUCT_NAME");
		} else
		{
			nextPackCode = productId;
			nextPackDesc = nowPackDesc;
		}
		// 移动商城1.5添加，新增用户主产品开始、截止日期
		IDataset userProduct = UserProductInfoQry.queryUserMainProduct(userInfo.getString("USER_ID"));
		if (userProduct == null || userProduct.isEmpty())
			CSAppException.apperr(CrmUserException.CRM_USER_631);

		IData ret0 = new DataMap();
		ret0.put("BRAND_CODE", InterfaceUtil.transBrand(brandCode));
		//ret0.put("BRAND_NAME", InterfaceUtil.getBrandName(brandCode));// 品牌名称
		ret0.put("BRAND_NAME", InterfaceUtil.getBrandName(InterfaceUtil.transBrand(brandCode)));// 品牌名称
		ret0.put("CUR_PRODUCT_ID", productId);// 本帐期主套餐编码
		ret0.put("CUR_PRODUCT_NAME", nowPackDesc);// 本账期主套餐名称
		ret0.put("NEXT_PRODUCT_ID", nextPackCode);// 下帐期主套餐编码
		ret0.put("NEXT_PRODUCT_NAME", nextPackDesc);// 下账期主套餐名称
		ret0.put("OPR_TIME", SysDateMgr.date2String(java.util.Calendar.getInstance().getTime(), SysDateMgr.PATTERN_STAND_SHORT));


		//REQ202002040006 移动商城统一接口平台V3.0版规范（新版账单查询）省份改造通知
		String CurNetExpensesCode="0";	// 本账期主套餐 全网资费编码
		String NextNetExpensesCode="0"; // 下账期主套餐  全网资费编码
		IDataset CurConfig = CommparaInfoQry.getCommparaByCodeCode1("CSM", "410", "YDSC",productId);
        if (IDataUtil.isNotEmpty(CurConfig)) {
        	CurNetExpensesCode=CurConfig.getData(0).getString("PARA_CODE2","0");
        }
        
		IDataset NextConfig = CommparaInfoQry.getCommparaByCodeCode1("CSM", "410", "YDSC",nextPackCode);
        if (IDataUtil.isNotEmpty(NextConfig)) {
        	NextNetExpensesCode=NextConfig.getData(0).getString("PARA_CODE2","0");
        }
		ret0.put("CURNET_EXPENSES_CODE", CurNetExpensesCode);
		ret0.put("NEXT_NETEXPENSES_CODE", NextNetExpensesCode);
		
		
		// 移动商城添加
		IData userProductInfo = userProduct.getData(0);
		ret0.put("START_TIME", transDate(userProductInfo.getString("START_DATE"), "yyyy-MM-dd HH:mm:SS", "yyyyMMddHHmmSS"));// 用户当前主产品的开始时间
		ret0.put("END_TIME", transDate(userProductInfo.getString("END_DATE"), "yyyy-MM-dd HH:mm:SS", "yyyyMMddHHmmSS"));// 用户当前主产品的结束时间
		
		/*
		 * REQ201904160026线上渠道已订业务失效时间屏蔽需求
		 * 对于线上渠道，当失效时间比当前时间大于12个月时，屏蔽失效时间（或显示失效时间为当年12月31日，与报纸公告一致）。
		 */
		String SYS_TIME = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);//当前系统时间
		String END_TIME = userProductInfo.getString("END_DATE");//主套餐失效时间
		int month = SysDateMgr.monthInterval(SYS_TIME,END_TIME);//获取两个时间的月差值
		if(month > 12){
			END_TIME = SYS_TIME.substring(0, 4) + "-12-31 23:59:59";
			END_TIME = transDate(END_TIME, "yyyy-MM-dd HH:mm:SS", "yyyyMMddHHmmSS");
			ret0.put("END_TIME",END_TIME);
		}
		rets.add(ret0);
		return rets;
	}

	// 查询个人产品套餐介绍信息
	public IData queryPersonProductInfo(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "QUERY_TAG");
		String queryTag = input.getString("QUERY_TAG");
		IDataset ret = new DatasetList();
		IDataset userret = new DatasetList();

		if ("0".equals(queryTag))
		{
			IDataUtil.chkParam(input, "SERIAL_NUMBER");
			String serialNumber = input.getString("SERIAL_NUMBER");
			IData userInfo = getUserInfo4All(serialNumber);
			String userId = userInfo.getString("USER_ID");
			String productId = userInfo.getString("PRODUCT_ID");
			userret = UserDiscntInfoQry.qryUserDiscntByUserId(userId, null);
			// ret =
			// UProductElementInfoQry.queryForceDiscntsByProductId(productId);
			IDataset productElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, null, null);
			Boolean isExistDiscnt = false;
			if (IDataUtil.isNotEmpty(productElements))
			{
				for (Object obj : productElements)
				{
					IData productElement = (IData) obj;
					if ("1".equals(productElement.getString("GROUP_FORCE_TAG", "")) && "0".equals(productElement.getString("FORCE_TAG", "")) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(productElement.getString("OFFER_TYPE", "")))
					{
						// 必选包下的可选优惠
						if (IDataUtil.isNotEmpty(userret))
						{
							for (int i = 0; i < userret.size(); i++)
							{
								IData userDiscnt = userret.getData(i);
								if (StringUtils.equals(userDiscnt.getString("DISCNT_CODE", ""), productElement.getString("OFFER_CODE", "")))
								{
									IData result = new DataMap();
									result.put("DISCNT_EXPLAIN", productElement.getString("DESCRIPTION", ""));
									ret.add(result);
									isExistDiscnt = true;
								}
							}
						}
					} else
					{
						//isExistDiscnt = true;
					}
				}
			}
			if (!isExistDiscnt)
			{
				IData reData = UProductInfoQry.getProductInfo(productId);
				if (StringUtils.isNotEmpty(productId))
				{
					IData result = new DataMap();
					result.put("DISCNT_EXPLAIN", reData.getString("PRODUCT_EXPLAIN", ""));
					ret.add(result);
				}
			}
			if (IDataUtil.isEmpty(ret) && IDataUtil.isEmpty(userret))
			{
				// 空时取产品的说明
				IData reData = UProductInfoQry.getProductInfo(productId);
				if (StringUtils.isNotEmpty(productId))
				{
					String strDiscntExplain = reData.getString("PRODUCT_EXPLAIN", "");
					reData.put("DISCNT_EXPLAIN", strDiscntExplain);
					return reData;
				}
				return null;
			}
			if (IDataUtil.isEmpty(ret))
			{
				return null;
			} else
			{
				return ret.getData(0);
			}

		} else if ("1".equals(queryTag))
		{
			IDataUtil.chkParam(input, "DISCNT_CODE");
			String discntCode = input.getString("DISCNT_CODE");
			String discntExplain = DiscntInfoQry.getDiscntExplanByDiscntCode(discntCode);
			IData dis = new DataMap();
			dis.put("DISCNT_EXPLAIN", discntExplain);
			return dis;
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1113);
		}

		return null;
	}

	/**
	 * 查询手机开卡归属地
	 * 
	 * @param inParam
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-7-28
	 */
	public IData queryPhoneRegCity(IData inParam) throws Exception
	{

		IDataset dataSet = UserInfoQry.queryPhoneCity(inParam.getString("SERIAL_NUMBER"));

		IData data = new DataMap();

		if (IDataUtil.isEmpty(dataSet))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1138);
		} else
		{
			IData tmp = dataSet.getData(0);
			data.put("X_RESULTCODE", "0");
			data.put("X_RESULTINFO", "查询手机开卡归属地成功！");
			data.put("RegProvince", tmp.getString("EPARCHY_NAME"));
			data.put("RegCity", tmp.getString("CITY_NAME1"));

			return data;
		}
		return new DataMap();
	}

	public IDataset queryPlatInfo(IData data, Pagination page) throws Exception
	{
		IDataset result = new DatasetList();
		for (int i = 0; i < 5; i++)
		{
			IData tmp = new DataMap();
			for (int j = 1; j <= 13; j++)
			{
				tmp.put("DATA" + j, j + "_" + data.getString("OPER_CODE"));
			}
			result.add(tmp);
		}
		// String serialNumber = data.getString("SERIAL_NUMBER");
		// // 第一步，查询用户信息
		// IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		// if (IDataUtil.isEmpty(userInfo))
		// {
		// CSAppException.apperr(CrmCommException.CRM_COMM_906);
		// }
		//
		// // 第二步，查询客户信息
		// String custId = userInfo.getString("CUST_ID", "");
		//
		// IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
		// if (IDataUtil.isEmpty(custInfo))
		// {
		// CSAppException.apperr(CustException.CRM_CUST_201);
		// }
		//        
		// String tradeId = SeqMgr.getTradeId();
		// String proCode = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC",
		// new String[]{"TYPE_ID","DATA_ID"},
		// "PDATA_ID", new String[]{"PROVINCE_CODE",
		// getVisit().getProvinceCode()});
		// if(StringUtils.isBlank(proCode)) {
		// CSAppException.apperr(CrmCommException.CRM_COMM_310);
		// }
		//    	
		// String opernumb = new
		// StringBuilder().append(SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD)).append("CSVC")
		// .append(StringUtils.substring(proCode, proCode.length() -
		// 3)).append(StringUtils.substring(tradeId,
		// tradeId.length() - 7)).toString();
		// String svcTypeId = "0303";
		// String cond = "";
		// String bizTypeCode = data.getString("BIZ_TYPE_CODE");
		// String operCode = data.getString("OPER_CODE");
		// String bizType = bizTypeCode + operCode;
		// if("4001001".equals(bizType) || "4001011".equals(bizType)) {
		// cond = serialNumber;
		// }else if("4001002".equals(bizType)) {
		// cond = serialNumber + "|" + data.getString("START_DATE") + "|" +
		// data.getString("END_DATE");
		// }else {
		// cond = serialNumber + "|" + data.getString("START_DATE") + "|" +
		// data.getString("END_DATE") + "|40";
		// }
		//    	
		// IDataset result = IBossCall.queryFromIBOSS(opernumb, svcTypeId, cond,
		// serialNumber,
		// custInfo.getString("CUST_NAME"), "08", bizTypeCode, operCode);
		//    	
		// if(page.getCurrent() != 1) {
		// result = IBossCall.queryFromIBOSSLeft(opernumb, page.getCurrent());
		// }
		// resultConvert(bizType, result);
		//    	
		// if (IDataUtil.isEmpty(result) ||
		// (result.getData(0).getInt("RSLTTOTALCNT") <= page.getCurrent() *
		// result.getData(0).getInt("RSLTPAGEMAXCNT")))
		// {
		// String remark = "查询数据获取完毕归档";
		// IBossCall.BussToHisIBOSS(opernumb, remark);
		// }
		return result;
	}

	public IDataset queryPlatSvcAttrByUserIdSId(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serviceId = IDataUtil.chkParam(input, "SERVICE_ID");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = user.getString("USER_ID");

		return UserPlatSvcInfoQry.getPlatSvcAttrByUserIdSId(userId, serviceId);
	}

	public IDataset queryPlatSvcInfo(IData input) throws Exception
	{

		IDataset rets = new DatasetList();
		IData ret0 = new DataMap();

		// 通用参数
		String serialNumber = "";
		String inCredenceNo = "";

		boolean bRealNameCheck = false;
		boolean hasWlan = false;
		String inModeCode = input.getString("IN_MODE_CODE", "");
		if (StringUtils.isBlank(inModeCode))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "IN_MODE_CODE");
		}

		String kindId = input.getString("KIND_ID", "");
		if (StringUtils.isBlank(kindId))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "KIND_ID");
		}

		String idType = input.getString("IDTYPE", "");
		if (StringUtils.isBlank(idType))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "IDTYPE");
		}

		String idItemRange = input.getString("IDITEMRANGE", "");
		if (StringUtils.isBlank(idItemRange))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "IDITEMRANGE");
		}
		serialNumber = idItemRange;

		String oprnumb = input.getString("OPRNUMB", "");
		if (StringUtils.isBlank(oprnumb))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "OPRNUMB");
		}

		String bizType = input.getString("BIZTYPE", "");
		if (StringUtils.isBlank(bizType))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "BIZTYPE");
		}
		if (StringUtils.equals(bizType, "WLAN"))
		{
			bRealNameCheck = true;
		}

		String channelId = input.getString("CHANNELID", "");
		if (StringUtils.isBlank(channelId))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "CHANNELID");
		}

		String sessionId = input.getString("SESSIONID", "");
		if (StringUtils.isBlank(sessionId))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "SESSIONID");
		}

		String identCode = input.getString("IDENTCODE", "");
		if (StringUtils.isBlank(identCode))
		{
			CSAppException.apperr(WapException.CRM_WAP_100001, "IDENTCODE");
		}
		inCredenceNo = identCode;

		String inMediaCode = input.getString("IN_MEDIA_CODE", "L");
		String busiTypeRemark = input.getString("BUSINESS_TYPE_REMARK", "");
		String tradeDepartId = getVisit().getDepartId();
		if (StringUtils.isBlank(tradeDepartId))
		{
			tradeDepartId = "1";
		}

		// 1、身份认证
		IDataset users = UserInfoQry.getUserInfoBySn(serialNumber, "0", "00", null);
		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		IData user0 = users.getData(0);
		String userId = user0.getString("USER_ID");
		String custId = user0.getString("CUST_ID");
		IDataset userProducts = UserProductInfoQry.queryMainProductNow(userId);
		if (IDataUtil.isEmpty(userProducts))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}
		String productId = userProducts.getData(0).getString("PRODUCT_ID");
		String brandCode = userProducts.getData(0).getString("BRAND_CODE");
		String startDate = userProducts.getData(0).getString("START_DATE");
		String endDate = userProducts.getData(0).getString("END_DATE");
		// 查询个人客户资料 无CUST_NAME 和IS_REAL_NAME 改查 客户资料
		// IDataset percusts = UcaInfoQry.qryPerInfoByCustId(custId);
		IData percusts = UcaInfoQry.qryCustomerInfoByCustId(custId);
		if (IDataUtil.isEmpty(percusts))
		{
			CSAppException.apperr(CustException.CRM_CUST_111);
		}
		String custName = percusts.getString("CUST_NAME");

		// 根据传入SESSION_ID和SERIAL_NUMBER查询该用户wap凭证信息，查询日期在当前日期有效期内的
		IDataset wapsessions = WapSessionInfoQry.queryWapSession(sessionId, serialNumber);
		if (IDataUtil.isEmpty(wapsessions))
		{
			CSAppException.apperr(WapException.CRM_WAP_700002);
		}
		String credenceNo = wapsessions.getData(0).getString("CREDENCE_NO", "");// 用户身份凭证号
		if (StringUtils.isBlank(credenceNo))
		{
			CSAppException.apperr(WapException.CRM_WAP_700003);
		}
		if (false == inCredenceNo.equals(credenceNo))
		{
			CSAppException.apperr(WapException.CRM_WAP_700004);
		}

		// 2、记录操作日志
		IData contact = new DataMap();
		String seqCustContactId = SeqMgr.getCustContact();
		String seqCustContactTraceId = SeqMgr.getCustContactTrace();
		String curdate = SysDateMgr.date2String(java.util.Calendar.getInstance().getTime(), SysDateMgr.PATTERN_STAND);

		contact.put("CUST_CONTACT_TRACE_ID", seqCustContactTraceId);
		contact.put("CUST_CONTACT_ID", seqCustContactId);
		contact.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		contact.put("CONTACT_MODE", "P"); // CONTACT_MODE 业务类型写死为P
		contact.put("CONTACT_SUB_MODE", "P");
		contact.put("CUST_ID", user0.getString("CUST_ID"));
		contact.put("USER_ID", userId);
		contact.put("SERIAL_NUMBER", serialNumber);
		contact.put("EPARCHY_CODE", user0.getString("EPARCHY_CODE"));
		contact.put("PRODUCT_ID", productId);
		contact.put("CITY_CODE", user0.getString("CITY_CODE"));
		contact.put("IN_MODE_CODE", inModeCode);
		contact.put("IN_MEDIA_CODE", inMediaCode);
		contact.put("START_TIME", curdate);
		contact.put("REMARK", busiTypeRemark);
		contact.put("CUST_NAME", custName);
		contact.put("CHANNEL_ID", tradeDepartId); // 取不到则取默认值
		contact.put("SUB_CHANNEL_ID", tradeDepartId);
		boolean contactOk = CustContactInfoQry.insertInfo(contact);
		if (contactOk == false)
		{
			CSAppException.apperr(WapException.CRM_WAP_700005);
		}

		// 3、业务基本规则检查
		// 3.1 未激活用户、申请停机、欠费停机、销户用户均不可以办理
		IDataset mainSvcStates = UserSvcStateInfoQry.queryUserValidMainSVCStateForWap(userId);
		if (IDataUtil.isEmpty(mainSvcStates))
		{
			CSAppException.apperr(SvcException.CRM_SVC_5);
		}
		IData mainSvcState0 = mainSvcStates.getData(0);
		String stateCode = mainSvcState0.getString("STATE_CODE", "");
		if (StringUtils.isBlank(stateCode))
		{
			CSAppException.apperr(SvcException.CRM_SVC_5);
		}
		if (false == StringUtils.equals(stateCode, "0"))
		{
			CSAppException.apperr(SvcException.CRM_SVC_6);
		}

		// 3.2 黑名单用户不能办理
		IDataset userBlackWhites = UserBlackWhiteInfoQry.getBlackUserInfo(userId, "-1", serialNumber, "B");
		if (IDataUtil.isNotEmpty(userBlackWhites))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1112);
		}
		// 3.3 未进行实名登记用户不可以办理
		if (bRealNameCheck && !StringUtils.equals(percusts.getString("IS_REAL_NAME", ""), CUST_REALNAME_YES))
		{
			CSAppException.apperr(CustException.CRM_CUST_2009);
		}

		// 4、查询用户XXX业务的订购信息
		IDataset userPlatsvcs1 = new DatasetList();
		IDataset userPlatsvcs2 = UserPlatSvcInfoQry.getPlatSvcByUserId(userId, bizType);
		if (IDataUtil.isNotEmpty(userPlatsvcs2))
		{
			for (int i = 0; i < userPlatsvcs2.size(); i++)
			{
				IData userPlatsvc = userPlatsvcs2.getData(i);
				String spCode = userPlatsvc.getString("SP_CODE");
				String bizCode = userPlatsvc.getString("BIZ_CODE");
				String bizTypeCode = userPlatsvc.getString("BIZ_TYPE_CODE");
				String sd = userPlatsvc.getString("START_DATE");
				String ed = userPlatsvc.getString("END_DATE");
				String productName = userPlatsvc.getString("PARA_CODE2");
				String serviceId = userPlatsvc.getString("PARA_CODE1");

				IData userPlatsvc1 = new DataMap();
				// 可办理产品的编码类型 01:产品编码；02:企业代码+业务代码
				userPlatsvc1.put("ENCODTYPE", "02");
				userPlatsvc1.put("PRODUNCTID", "");
				userPlatsvc1.put("PRODUNCTNAME", productName);
				// 企业代码
				userPlatsvc1.put("SPID", spCode);

				// 业务代码
				// 注册类平台服务的sp_code和biz_code均为REG_SP，不能唯一区分，统一通过biz_type_code区分，
				// 并且接口中BIZCODE字段设置为biz_type_code的值，其它非注册类仍然使用biz_code的值。
				if ("WLAN".equals(bizType))
				{
					userPlatsvc1.put("BIZCODE", bizTypeCode);

					// 高校WLAN有套餐选择，特殊处理
					if ("92".equals(bizTypeCode))
					{
						IDataset uds2 = UserPlatSvcInfoQry.getUserDiscntInfo(userId, serviceId);
						if (IDataUtil.isNotEmpty(uds2))
						{
							userPlatsvc1.put("PRODUNCTID", uds2.getData(0).getString("DISCNT_CODE", ""));// 这里放置优惠ID
							userPlatsvc1.put("PRODUNCTNAME", uds2.getData(0).getString("PARA_CODE2", ""));
						}
					}
				} else
				{
					// 可能存在浏览器中的特殊字符，预先转义。
					userPlatsvc1.put("BIZCODE", InterfaceUtil.convertStr(bizCode, 1));
				}
				// 开始时间 格式为：YYYYMMDDHH24MMSS
				userPlatsvc1.put("OPRBEGTIME", sd);
				// 失效时间 格式为：YYYYMMDDHH24MMSS
				userPlatsvc1.put("OPRENDTIME", ed);
				userPlatsvcs1.add(userPlatsvc1);
			}
		}
		ret0.put("ENJOYINFO", userPlatsvcs1);

		// 5、查询用户可办理XXX业务列表
		IDataset userOtherPlatsvcs1 = new DatasetList();
		IDataset userOtherPlatsvcs2 = PlatSvcInfoQry.queryPlatSvcByUserIdForWap(userId, "CSM", "2689", bizType);
		if (IDataUtil.isNotEmpty(userOtherPlatsvcs2))
		{
			for (int j = 0; j < userOtherPlatsvcs2.size(); j++)
			{
				IData userOtherPlatsvc2 = userOtherPlatsvcs2.getData(j);
				String spCode = userOtherPlatsvc2.getString("SP_CODE");
				String bizCode = userOtherPlatsvc2.getString("BIZ_CODE");
				String bizTypeCode = userOtherPlatsvc2.getString("BIZ_TYPE_CODE");
				String platSvcDesc = userOtherPlatsvc2.getString("ELEMENT_DESC");
				String productName = userOtherPlatsvc2.getString("PARA_CODE2");
				String serviceId = userOtherPlatsvc2.getString("PARA_CODE1");

				IData userOtherPlatsvc1 = new DataMap();
				// 可办理产品的编码类型 01:产品编码；02:企业代码+业务代码
				userOtherPlatsvc1.put("ENCODTYPE", "02");
				userOtherPlatsvc1.put("PRODUNCTID", "");
				userOtherPlatsvc1.put("PRODUNCTNAME", productName);
				// 企业代码
				userOtherPlatsvc1.put("SPID", spCode);
				// 业务代码
				// 注册类平台服务的sp_code和biz_code均为REG_SP，不能唯一区分，统一通过biz_type_code区分，
				// 并且接口中BIZCODE字段设置为biz_type_code的值，其它非注册类仍然使用biz_code的值。
				if ("WLAN".equals(bizType))
				{
					userOtherPlatsvc1.put("BIZCODE", bizTypeCode);
				} else
				{
					// 可能存在浏览器中的特殊字符，预先转义。
					userOtherPlatsvc1.put("BIZCODE", InterfaceUtil.convertStr(bizCode, 1));
				}

				// 产品描述
				userOtherPlatsvc1.put("PRODUNCTDESC", platSvcDesc);
				if ("WLAN".equals(bizType))// 是否支持叠加 0:可叠加；1:不支持叠加。Wlan套餐专有。
				{
					hasWlan = true;
					if ("92".equals(bizTypeCode))// 高校WLAN有套餐选择，特殊处理
					{
						IDataset dataset2 = CommparaInfoQry.getOtherDiscntInfo(serviceId);
						if (IDataUtil.isNotEmpty(dataset2))
						{
							for (int k = 0; k < dataset2.size(); k++)
							{
								IData dataTmp2 = new DataMap();
								// 可办理产品的编码类型 01:产品编码；02:企业代码+业务代码
								userOtherPlatsvc1.put("ENCODTYPE", "02");
								userOtherPlatsvc1.put("PRODUNCTID", dataset2.getData(k).getString("PARA_CODE1", ""));// 这里放置优惠ID
								userOtherPlatsvc1.put("PRODUNCTNAME", dataset2.getData(k).getString("PARA_CODE2", ""));
								// 企业代码
								userOtherPlatsvc1.put("SPID", spCode);
								userOtherPlatsvc1.put("BIZCODE", bizTypeCode);
								userOtherPlatsvc1.put("PRODUNCTDESC", dataset2.getData(k).getString("ELEMENT_DESC", ""));
								userOtherPlatsvcs1.add(dataTmp2);
							}
						}
						continue;
					}
				}
				userOtherPlatsvcs1.add(userOtherPlatsvc1);
			}
		}
		ret0.put("UNENJOYINFO", userOtherPlatsvcs1);

		if (hasWlan)
		{
			ret0.put("SUPERIMPOSE", "1");
		}

		rets.add(ret0);
		return rets;
	}

	public IDataset queryProductByBrand(IData input) throws Exception
	{
		String brandCode = IDataUtil.chkParam(input, "BRAND_CODE");
		String productMode = IDataUtil.chkParam(input, "PRODUCT_MODE");

		String getMode = input.getString("X_GETMODE", "");
		String tradeStaffId = input.getString("TRADE_STAFF_ID", "");

		IDataset products = ProductInfoQry.getProductsByBrand(brandCode, productMode);
		if (StringUtils.equals(getMode, "1") && StringUtils.isNotBlank(tradeStaffId))
		{
			ProductPrivUtil.filterProductListByPriv(tradeStaffId, products);
		}

		return products;
	}

	/**
	 * 查询产品构成信息接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 * @date 2014-07-09 11:08:59
	 */
	public IDataset queryProductElement(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");
		IDataUtil.chkParam(input, "TRADE_STAFF_ID");

		String productId = input.getString("PRODUCT_ID");
		String staffId = input.getString("TRADE_STAFF_ID");
		String asp = input.getString("ASP");
		String elementType = input.getString("ELEMENT_TYPE_CODE");

		return getElemsInProdModel(productId, staffId, asp, elementType);
	}

	// 获取指定产品下的可选包,包含对地州的过滤
	public IDataset queryProductPackageIntf(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");
		IDataUtil.chkParam(input, "EPARCHY_CODE");

		return ProductInfoQry.getGrpPackagesByProductId(input.getString("PRODUCT_ID"), input.getString("EPARCHY_CODE"));
	}

	public IDataset queryProductVPMNDiscntIntf(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String productId = IDataUtil.chkParam(input, "PRODUCT_ID");

		// 检查新产品是否允许开通VPMN优惠
		IDataset vpmnProducts = CommparaInfoQry.getCommpara("CSM", "380", productId, "ZZZZ");
		if (IDataUtil.isEmpty(vpmnProducts))
		{
			return rets;
		}

		IData users = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = users.getString("USER_ID");
		IDataset userRelas = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userId, "20");
		if (IDataUtil.isEmpty(userRelas))
		{
			return rets;
		}
		// 此sql已经废弃，改为调用产商品接口 duhj 2017/5/22
		// IDataset userVpmnDiscnts =
		// UserDiscntInfoQry.queryUserDisByUidTypePkgid(userId, "20",
		// "80000102");
		IDataset userVpmnDiscnts = new DatasetList();
		IDataset userVpmnDiscnts2 = new DatasetList();

		// 查询用户所有优惠
		IDataset userdiscntAll = UserDiscntInfoQry.getUserVPMNDiscntUpc(userId, "20", "80000102");// 获取用户的优惠

		// 查询产品包下所有元素
		IDataset elemtntAll = UPackageElementInfoQry.getPackageElementInfoByPackageId("80000102");
		// 过滤是否有Vpmn数据

		for (int j = 0; j < userdiscntAll.size(); j++)
		{
			IData temp = userdiscntAll.getData(j);
			for (int k = 0; k < elemtntAll.size(); k++)
			{
				String prarmdiscode = elemtntAll.getData(k).getString("ELEMENT_ID");
				if (temp.getString("DISCNT_CODE").equals(prarmdiscode))
				{
					userVpmnDiscnts.add(temp);
				}
			}
		}

		if (IDataUtil.isEmpty(userVpmnDiscnts))
		{
			return rets;
		} else
		{
			// 加此段逻辑是为了回填PRODUCT_ID，因为现在表tf_f_user_discnt表 product_id字段已经删掉
			// modify by duhj 2017/5/22 修改code_code
			IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, BizRoute.getRouteId());
			userVpmnDiscnts2 = OfferUtil.fillStructAndFilter(userVpmnDiscnts, userOfferRels);
		}

		IData userVpmnDiscnt0 = userVpmnDiscnts2.getData(0);
		String vUserIDA = userVpmnDiscnt0.getString("USER_ID_A");
		String vProudctId = userVpmnDiscnt0.getString("PRODUCT_ID");

		IDataset prdForDis = ProductPkgInfoQry.getPackageByProductIdForOpen(productId);
		StringBuilder discntAllId = new StringBuilder("");
		if (IDataUtil.isNotEmpty(prdForDis))
		{
			for (int i = 0; i < prdForDis.size(); i++)
			{
				String discntId = prdForDis.get(i, "ELEMENT_ID").toString();
				discntAllId.append(S_E_DYH + discntId + S_E_DYH);
				if (i < prdForDis.size() - 1)
				{
					discntAllId.append(S_E_DH);
				}
			}
		}
		IDataset vpmnDiscntLimits = UserGrpPkgInfoQry.qryDiscntLimitByUserId(vUserIDA, vProudctId, discntAllId.toString());
		if (IDataUtil.isEmpty(vpmnDiscntLimits))
		{
			return rets;
		}
		DiscntPrivUtil.filterDiscntListByPriv(getVisit().getStaffId(), vpmnDiscntLimits);
		rets.addAll(vpmnDiscntLimits);
		return rets;
	}

	public IDataset querySaleActiveDiscnt(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");// 产品ID
		IDataUtil.chkParam(input, "PACKAGE_ID");// 活动包ID
		IDataUtil.chkParam(input, "IN_MODE_CODE");
		String productId = input.getString("PRODUCT_ID", "");
		String packageId = input.getString("PACKAGE_ID", "");

		// return PkgElemInfoQry.getSalePackageDiscntByPkg(productId,
		// packageId);
		return UPackageElementInfoQry.qrySalePackagesByProdIdPkgIdAndElementTypeCode(productId, packageId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
	}

	public IDataset querySaleActiveByPID(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");// 产品ID
		// IDataUtil.chkParam(input, "PACKAGE_ID");// 活动包ID
		// IDataUtil.chkParam(input, "IN_MODE_CODE");
		String productId = input.getString("PRODUCT_ID", "");
		// String packageId = input.getString("PACKAGE_ID", "");
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = userInfo.getString("USER_ID");
		IData param1 = new DataMap();
		param1.put("USER_ID", userId);
		param1.put("PRODUCT_ID", productId);
		// param1.put("PACKAGE_ID", packageId);
		param1.put("PROCESS_TAG", "0");
		IDataset dataset1 = new DatasetList();
		dataset1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PPID", param1);
		IDataset dataset2 = new DatasetList();
		IData data = new DataMap();
		int rowCounts = 0;
		if (IDataUtil.isNotEmpty(dataset1))
		{
			rowCounts = dataset1.size();
		}
		data.put("ROW_COUNTS", Integer.toString(rowCounts));
		dataset2.add(data);
		return dataset2;
	}

	public IDataset querySaleActiveInfo(IData input) throws Exception
	{
		IDataset rets = new DatasetList();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String removeTag = input.getString("REMOVE_TAG", "0");
		String processTag = input.getString("PROCESS_TAG", "0");// 不传默认查正常的

		IDataset users = UserInfoQry.getUserInfoBySerailNumber(removeTag, serialNumber);

		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		IData user0 = users.getData(0);
		String userId = user0.getString("USER_ID");
		String custId = user0.getString("CUST_ID");
		IData cust = UcaInfoQry.qryCustomerInfoByCustId(custId);
		if (IDataUtil.isEmpty(cust))
		{
			CSAppException.apperr(CustException.CRM_CUST_165);
		}

		String custManagerId = "";
		IData custvip = CustVipInfoQry.qryVipByUserId(userId);
		if (IDataUtil.isNotEmpty(custvip))
		{
			custManagerId = custvip.getString("CUST_MANAGER_ID", "");
		}

		String campnType = "YX03"; // YX03代表用户购机信息
		IDataset usaleactives = UserSaleActiveInfoQry.queryInfosByUserIdProcessCampn(userId, processTag, campnType);
		if (IDataUtil.isNotEmpty(usaleactives))
		{
			for (int i = 0; i < usaleactives.size(); i++)
			{
				IData ret = new DataMap();
				ret.put("SERIAL_NUMBER", serialNumber);
				ret.put("CUST_ID", custId);
				ret.put("USER_ID", userId);
				ret.put("CUST_NAME", cust.getString("CUST_NAME", ""));
				ret.put("CUST_TYPE", cust.getString("CUST_TYPE", ""));
				ret.put("MANAGER_STAFF_ID", custManagerId);
				ret.put("OPEN_DATE", user0.getString("OPEN_DATE"));
				ret.put("PURCHASE_INFO", usaleactives.getData(i).getString("PRODUCT_NAME", ""));// 购机类型
				ret.put("PURCHASE_DESC", usaleactives.getData(i).getString("PACKAGE_NAME", ""));// 手机型号
				ret.put("TRADE_ID", usaleactives.getData(i).getString("RELATION_TRADE_ID", ""));// 关联流水
				ret.put("ACCEPT_DATE", usaleactives.getData(i).getString("ACCEPT_DATE", ""));
				ret.put("START_DATE", usaleactives.getData(i).getString("START_DATE", ""));
				ret.put("END_DATE", usaleactives.getData(i).getString("END_DATE", ""));
				ret.put("PROCESS_TAG", usaleactives.getData(i).getString("PROCESS_TAG", ""));
				rets.add(ret);
			}
		}

		return rets;
	}

	public IDataset querySaleActiveInfos(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData userInfo = getUserInfo4All(serialNumber);

		String userId = "";
		String brandCode = "";
		if (IDataUtil.isNotEmpty(userInfo))
		{
			userId = userInfo.getString("USER_ID");
			brandCode = userInfo.getString("BRAND_CODE");
		}

		String startDate = SysDateMgr.decodeTimestamp(input.getString("START_DATE", ""), SysDateMgr.PATTERN_STAND);
		String endDate = SysDateMgr.decodeTimestamp(input.getString("END_DATE", ""), SysDateMgr.PATTERN_STAND);
		IDataset saleActiveInfos = UserSaleActiveInfoQry.getSaleActiveInfo(userId, "9", startDate, endDate);

		IDataset retDs = new DatasetList();
		IData retD = new DataMap();

		if (IDataUtil.isNotEmpty(saleActiveInfos))
		{
			for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
			{
				IData saleActiveInfo = saleActiveInfos.getData(i);

				// 用户号码、用户品牌、已办理营销活动名称、营销活动用户办理生效时间、营销活动用户办理失效时间。
				IData retD1 = new DataMap();
				retD1.put("SERIAL_NUMBER", serialNumber);
				retD1.put("BRAND_CODE", brandCode);
				retD1.put("PRODUCT_NAME", saleActiveInfo.getString("PRODUCT_NAME", ""));
				retD1.put("START_DATE", saleActiveInfo.getString("START_DATE", ""));
				retD1.put("END_DATE", saleActiveInfo.getString("END_DATE", ""));
				retDs.add(retD1);
			}
		} else
		{
			retD.put("X_RESULTCODE", "-1");
			retD.put("X_RESULTINFO", "该用户无生效营销活动");
			retDs.add(retD);
		}

		return retDs;
	}

	// 新增网厅“查询已办理营销活动”的功能接口
	public IDataset querySaleActiveInfosByNet(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData userInfo = getUserInfo4All(serialNumber);

		String userId = "";
		String brandCode = "";
		if (IDataUtil.isNotEmpty(userInfo))
		{
			userId = userInfo.getString("USER_ID");
			brandCode = userInfo.getString("BRAND_CODE");
		}

		String startDate = SysDateMgr.decodeTimestamp(input.getString("START_DATE", ""), SysDateMgr.PATTERN_STAND);
		String endDate = SysDateMgr.decodeTimestamp(input.getString("END_DATE", ""), SysDateMgr.PATTERN_STAND);
		IDataset saleActiveInfos = UserSaleActiveInfoQry.getSaleActiveInfo2(userId, "0", startDate, endDate);

		IDataset retDs = new DatasetList();
		IData retD = new DataMap();

		if (IDataUtil.isNotEmpty(saleActiveInfos))
		{
			for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
			{
				IData saleActiveInfo = saleActiveInfos.getData(i);

				// 用户号码、用户品牌、已办理营销活动名称、营销活动用户办理生效时间、营销活动用户办理失效时间。
				IData retD1 = new DataMap();
				retD1.put("SERIAL_NUMBER", serialNumber);
				retD1.put("BRAND_CODE", brandCode);
				retD1.put("PRODUCT_NAME", saleActiveInfo.getString("PRODUCT_NAME", ""));
				retD1.put("START_DATE", saleActiveInfo.getString("START_DATE", ""));
				retD1.put("END_DATE", saleActiveInfo.getString("END_DATE", ""));
				retD1.put("RSRV_DATE1", saleActiveInfo.getString("RSRV_DATE1", ""));
				retD1.put("RSRV_DATE2", saleActiveInfo.getString("RSRV_DATE2", ""));
				retD1.put("ACCEPT_DATE", saleActiveInfo.getString("ACCEPT_DATE", ""));

				IData pkgInfo = UPackageInfoQry.getPackageByPK(saleActiveInfo.getString("PACKAGE_ID"));
				retD1.put("PACKAGE_DESC", pkgInfo.getString("DESCRIPTION", ""));
				retDs.add(retD1);
			}
		} else
		{
			retD.put("X_RESULTCODE", "-1");
			retD.put("X_RESULTINFO", "该用户无生效营销活动");
			retDs.add(retD);
		}

		return retDs;
	}

	/**
	 * 根据产品ID和包ID获取该活动包的优惠元素 xuwb5
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset querySalePackageDiscntByPkg(IData input) throws Exception
	{
		String productId = input.getString("PRODUCT_ID", "");
		String packageId = input.getString("PACKAGE_ID", "");
		return UPackageElementInfoQry.qrySalePackagesByProdIdPkgIdAndElementTypeCode(productId, packageId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
		// return PkgElemInfoQry.getSalePackageDiscntByPkg(productId,
		// packageId);
	}

	public IDataset queryScoreExchagneRule(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData userInfos = getUserInfo4All(serialNumber);

		String userId = userInfos.getString("USER_ID");

		String brandCode = "";

		if (IDataUtil.isNotEmpty(userInfos))
		{
			brandCode = userInfos.getString("BRAND_CODE");
		}

		rets = QryScoreInfo.queryScoreExchagneRule(userInfos.getString("EPARCHY_CODE"), brandCode, queryUserScore(userId));

		return rets;
	}

	public IDataset querySecKillDetails(IData input) throws Exception
	{
		IDataset rets = new DatasetList();

		String activityNumber = IDataUtil.chkParam(input, "ACTIVITY_NUMBER");
		String queryType = IDataUtil.chkParam(input, "QUERY_TYPE");

		String startTime = IDataUtil.chkParam(input, "START_TIME");
		String endTime = IDataUtil.chkParam(input, "END_TIME");

		String serialNumber = null;
		String secKillType = null;

		if ("SerialNumber".equals(queryType))
		{
			serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		} else if ("SecKillType".equals(queryType))
		{
			secKillType = IDataUtil.chkParam(input, "SECKILL_TYPE");
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1040);
		}
		rets = SundryQry.querySecKillDetails(activityNumber, serialNumber, secKillType, startTime, endTime);
		return rets;
	}

	public IData querySigninfo(IData input) throws Exception
	{
		String userType = IDataUtil.chkParam(input, "USER_TYPE");
		if (userType.length() != 2)
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1120);
		}

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		if ("04".equals(userType)) {     //宽带用户进行充值的校验    add by tanjuliang   2017-11-06
			String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
			IDataset tagInfos = TagInfoQry.queryNormalTagInfoByTagCode(eparchyCode, "CS_CHR_BANKSIGN_PROV_NO", "CSM", "0");
			int homeProv = 0;
			if (IDataUtil.isNotEmpty(tagInfos)) {
				homeProv = tagInfos.getData(0).getInt("TAG_NUMBER", 0);
			}

			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if (IDataUtil.isEmpty(userInfo)) {
				input.put("HOME_PROV", homeProv);
				input.put("USER_STATUS", "99");
				input.put("SIGN_STATUS", "1");
				input.put("USER_CAT", "0");
				return input;
			}
			//1、无手机宽带号码（6开头）全部都是包年的，因此无手机宽带号码也不允许充值；2、为了防止用户把钱冲到宽带账户中，不允许给“KD_+手机号”这种格式的宽带号码充值；
			IDataset mainSet = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
			if (IDataUtil.isEmpty(mainSet)) {     //查询不为宽带号码
				input.put("HOME_PROV", homeProv);
				input.put("USER_STATUS", "08");
				input.put("SIGN_STATUS", "1");
				input.put("USER_CAT", "0");
				return input;
			}

			//"67220428" 宽带包年活动,包年用户不允许进行充值
			IDataset userSaleActiveProdId = UserSaleActiveInfoQry.queryUserSaleActiveProdId(userInfo.getString("USER_ID"), "67220428", "0");
			if (IDataUtil.isNotEmpty(userSaleActiveProdId)) {
				input.put("HOME_PROV", homeProv);
				input.put("USER_STATUS", "08");
				input.put("SIGN_STATUS", "1");
				input.put("USER_CAT", "0");
				return input;
			}
		}else if("03".equals(userType)){ //固话用户进行充值的校验
			String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
			IDataset tagInfos = TagInfoQry.queryNormalTagInfoByTagCode(eparchyCode, "CS_CHR_BANKSIGN_PROV_NO", "CSM", "0");
			int homeProv = 0;
			if (IDataUtil.isNotEmpty(tagInfos)) {
				homeProv = tagInfos.getData(0).getInt("TAG_NUMBER", 0);
			}
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if (IDataUtil.isEmpty(userInfo)) {
				input.put("HOME_PROV", homeProv);
				input.put("ACCOUNT_TYPE", "00");
				input.put("USER_STATUS", "08");
				input.put("SIGN_STATUS", "1");
				input.put("USER_CAT", "0");
				return input;
			}
			IDataset userProducts =UserProductInfoQry.queryMainProduct(userInfo.getString("USER_ID"));
			if(IDataUtil.isEmpty(userProducts)){
				input.put("HOME_PROV", homeProv);
				input.put("ACCOUNT_TYPE", "00");
				input.put("USER_STATUS", "08");
				input.put("SIGN_STATUS", "1");
				input.put("USER_CAT", "0");
				return input;
			}
			//非固话
			if(!"IMSG,IMSP,TT01,TT02,TT03,TT04,TT05,TDTT".contains(userProducts.getData(0).getString("BRAND_CODE"))){
				input.put("HOME_PROV", homeProv);
				input.put("ACCOUNT_TYPE", "00");
				input.put("USER_STATUS", "08");
				input.put("SIGN_STATUS", "1");
				input.put("USER_CAT", "0");
				return input;
			}
			
			IDataset payRelaInfos = PayRelaInfoQry.getPayRelaByUserId(userInfo.getString("USER_ID"));
			boolean flag = false ; 
			IDataset payRelaInfoset = new DatasetList();
			if(IDataUtil.isNotEmpty(payRelaInfos)){
				payRelaInfoset = PayRelaInfoQry.getPayRelaBySelUserValidDefault(payRelaInfos.first().getString("ACCT_ID"));
				if(IDataUtil.isNotEmpty(payRelaInfoset)){
					flag = true;
				}
			}
			if(flag && payRelaInfoset.size() > 1){
				//主用户标识：在多个用户合并付费则有多条对应一个账户的付费关系中取默认账户的acctid
				//用	ACCT_ID 去查 custid  用custid 去查  userid 
				IData accountInfo = UcaInfoQry.qryAcctInfoByAcctId(payRelaInfos.first().getString("ACCT_ID"));
				IDataset userInfos = UserInfoQry.getUserInfoByCustId(accountInfo.getString("CUST_ID"),null);
				if(IDataUtil.isNotEmpty(userInfos)){
					input.put("MAIN_ID_VALUE", userInfos.first().getString("USER_ID"));
					input.put("ACCOUNT_TYPE", "01");
				}
			}else if(flag && payRelaInfoset.size() == 1){
				input.put("ACCOUNT_TYPE", "00");
			}
			
		}

		// 获取三户资料
		IDataset datasetUca = this.getUCAInfobySn(serialNumber);
		IData userInfo = datasetUca.getData(0).getData("USER_INFO");
		IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
		IData acctInfo = datasetUca.getData(0).getData("ACCT_INFO");

		String eparchyCode = "";
		if (IDataUtil.isEmpty(userInfo))
		{
			eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
			IDataset tagInfos = TagInfoQry.queryNormalTagInfoByTagCode(eparchyCode, "CS_CHR_BANKSIGN_PROV_NO", "CSM", "0");
			int homeProv = 0;
			if (IDataUtil.isNotEmpty(tagInfos))
			{
				homeProv = tagInfos.getData(0).getInt("TAG_NUMBER", 0);
			}

			input.put("HOME_PROV", homeProv);
			input.put("USER_STATUS", "99");
			input.put("SIGN_STATUS", "1");
			input.put("USER_CAT", "0");
			return input;

		} else
		{
			eparchyCode = userInfo.getString("EPARCHY_CODE");
		}

		input.put("USER_NAME", custInfo.getString("CUST_NAME"));
		IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "3129", userInfo.getString("USER_STATE_CODESET"), eparchyCode);
		if (IDataUtil.isEmpty(commparaInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1121);
		}

		input.put("USER_STATUS", commparaInfos.getData(0).getString("PARA_CODE1"));
		if("03".equals(userType)){
			input.put("USER_STATUS", "00");
		}
		IDataset tagInfos = TagInfoQry.queryNormalTagInfoByTagCode(eparchyCode, "CS_CHR_BANKSIGN_PREPAY", "CSM", "0");

		String prepayString = userInfo.getString("PREPAY_TAG");

		String prepayTag = "1";
		if (IDataUtil.isNotEmpty(tagInfos))
		{
			prepayTag = tagInfos.getData(0).getString("TAG_INFO", "1");
		}

		if (StringUtils.isNotBlank(prepayTag))
		{
			/*
			 * if (prepayTag.indexOf(prepayString) > -1) prepayTag = "1"; else
			 * prepayTag = "0";
			 */
			prepayTag = prepayString;
		} else
		{
			prepayTag = prepayString;
		}

		input.put("USER_CAT", prepayTag);

		tagInfos.clear();
		tagInfos = TagInfoQry.queryNormalTagInfoByTagCode(eparchyCode, "CS_CHR_BANKSIGN_PROV_NO", "CSM", "0");
		int homeProv = 0;
		if (IDataUtil.isNotEmpty(tagInfos))
		{
			homeProv = tagInfos.getData(0).getInt("TAG_NUMBER", 0);
		}
		input.put("HOME_PROV", homeProv);

		if ("0".equals(prepayTag))
		{
			/*
			 * PayAmount 后付费用户返回并且只返回此字段。 当后付费用户有预存时，此字段返回0；
			 * 当后付费用户有应缴时，正整数返回实际应缴金额。 如：预存10元，此字段返回“0”。应缴5元，此字段返回“500”。
			 */
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
			String strAcct = uca.getAcctBlance();
			if (uca == null)
			{
				CSAppException.apperr(CrmUserException.CRM_USER_1151);
			} else
			{
				Integer nAcct = 0;
				try
				{
					nAcct = Integer.parseInt(strAcct);
					if (nAcct >= 0)
					{
						strAcct = "0";
					} else
					{
						nAcct = Math.abs(nAcct);
						strAcct = nAcct.toString();
					}
				} catch (Exception e)
				{
					strAcct = "0";
				}
				// 实时结余赋值
				input.remove("BALANCE");
				// input.put("BALANCE", "0: "
				// +acctInfo.getString("ACCT_BALANCE"));// 客户余额
				input.put("PAY_AMOUNT", strAcct);// 应缴总金额
			}
		} else
		{
			/*
			 * Balance 预付费用户返回并且只返回此字段。 当预付费用户有预存时，正整数返回实际余额；
			 * 当预付费用户有欠费时，负整数返回实际欠费金额。 如：余额10元，此字段返回“1000”。欠费5元，此字段返回“-500”。
			 */
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
			String strAcct = uca.getAcctBlance();
			if (uca == null)
			{
				CSAppException.apperr(CrmUserException.CRM_USER_1151);// getUCAInfobySn
			} else
			{
				try
				{
					Integer nAcct = Integer.parseInt(strAcct);
				} catch (Exception e)
				{
					strAcct = "0";
				}
				// 实时结余赋值
				input.remove("PAY_AMOUNT");
				input.put("BALANCE", strAcct);// 客户余额
				// input.put("PAY_AMOUNT", "1: "
				// +acctInfo.getString("ACCT_BALANCE"));// 应缴总金额
			}
		}

		// 实时结余赋值
		// input.put("BALANCE", acctInfo.getString("ALL_NEW_BALANCE"));// 客户余额
		// input.put("PAY_AMOUNT", acctInfo.getString("SPAY_FEE"));// 应缴总金额

		// 判断当前是否有有效的签约
		IDataset mainSignInfo = UserBankMainSignInfoQry.getInfoByUser(input.getString("USER_TYPE", "01"), serialNumber);
		if (IDataUtil.isNotEmpty(mainSignInfo))
		{
			input.put("SIGN_STATUS", "0");
			input.put("BANK_ID", mainSignInfo.getData(0).getString("BANK_ID"));
		} else
		{
			mainSignInfo.clear();
			mainSignInfo = UserBankMainSignInfoQry.queryUserBankSubSignByUID(input.getString("USER_TYPE", "01"), serialNumber);
			if (IDataUtil.isNotEmpty(mainSignInfo))
			{
				input.put("SIGN_STATUS", "2");
			} else
			{
				input.put("SIGN_STATUS", "1");
			}
		}

		input.put("X_RESULTCODE", "0000");
		input.put("X_RESULTINFO", "OK");
		input.put("X_RSPCODE", "0000");
		input.put("X_RSPTYPE", "0");
		input.put("X_RSPDESC", "受理成功");

		return input;
	}

	/**
	 * 查询客户资料记录轨迹 入 SERIAL_NUMBER OBJECT_ID
	 * =person.changepostinfo.QueryUserPostRepair DEAL_TYPE = Q
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("finally")
	public IDataset queryStaffOperLogIntf(IData data)
	{
		IDataset result = new DatasetList();// 插日志返回结果
		IData resultMap = new DataMap();

		try
		{
			boolean isLog = true; // 是否记录日志，默认为true
			// 校验必填参数
			String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
			String tradeStaffId = IDataUtil.chkParam(data, "TRADE_STAFF_ID");
			// td_s_tag 开关控制

			IDataset paramDataset = TagInfoQry.getTagInfoBySubSys("ZZZZ", "CS_DEFAULT_CUST_QRY_LOG", "0", "CSM", null);
			if (IDataUtil.isEmpty(paramDataset))
			{
				// common.error("TD_S_TAG没有配置日志记录开关");
				CSAppException.apperr(CrmCommException.CRM_COMM_1139);
			}

			String tagChar = paramDataset.getData(0).getString("TAG_CHAR");
			if (!StringUtils.equals("Y", tagChar))
			{// 如果没有配置成Y,则不记录日志
				isLog = false; // 不记录日志
			}

			String objectId = data.getString("OBJECT_ID");

			String dealType = data.getString("DEAL_TYPE", "Q");// 默认为查询

			IData param = new DataMap();

			String accpet_date = SysDateMgr.getSysTime();
			String acceptMonth = "";
			if (StringUtils.isNotBlank(accpet_date) && accpet_date.length() >= 9)
			{
				acceptMonth = accpet_date.substring(5, 7);
			}

			IDataset operDataset = StaffOperTypeQry.queryByObjectOper(objectId, dealType);

			if (IDataUtil.isNotEmpty(operDataset) && isLog)
			{

				String id = data.getString("ID", "0");
				param.put("ID_TYPE", data.getString("ID_TYPE", "9"));
				param.put("ID", id); // 一般情况为手机号码，GROUP_ID
				param.put("ID_A", data.getString("ID_A", "")); // 预留，用于组合条件查询传入查询条件
				param.put("ID_B", data.getString("ID_B", "")); // 预留，用于组合条件查询传入查询条件
				param.put("ID_C", data.getString("ID_C", "")); // 预留，用于组合条件查询传入查询条件
				param.put("OPER_TYPE_CODE", operDataset.getData(0).getString("OPER_TYPE_CODE"));
				param.put("ACCEPT_MONTH", acceptMonth);

				param.put("USER_ID", data.getString("USER_ID", ""));
				param.put("CLIENT_IP", data.getString("CLIENT_IP"));
				param.put("CLIENT_MAC", data.getString("CLIENT_MAC", ""));
				param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "0"));
				param.put("ACCEPT_DATE", accpet_date);
				param.put("OPER_TIME", accpet_date);
				param.put("TRADE_STAFF_ID", tradeStaffId);
				param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", ""));
				param.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE", ""));
				param.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", ""));
				param.put("REMARK", data.getString("REMARK", ""));
				param.put("RSRV_STR1", data.getString("RSRV_STR1", ""));
				param.put("RSRV_STR2", data.getString("RSRV_STR2", ""));
				param.put("RSRV_STR3", data.getString("RSRV_STR3", ""));
				param.put("RSRV_STR4", data.getString("RSRV_STR4", ""));
				param.put("RSRV_STR5", data.getString("RSRV_STR5", ""));

				// AppEntity dao = new AppEntity(pd);
				// 根据ID字段值最后一位来插表，该字段默认为0，且判断最后一位是否是数字，如果不是数字默认为0
				String snLength = id.length() > 1 ? id.substring(id.length() - 1, id.length()) : id;
				Pattern pattern = Pattern.compile("[0-9]*");
				if (!pattern.matcher(snLength).matches())
				{
					snLength = "0";
				}
				Dao.insert("TF_B_STAFFOPERLOG_" + snLength, param);

				resultMap.put("X_RESULTCODE", "0");
				resultMap.put("X_RESULTINFO", "插入TF_B_STAFFOPERLOG成功");
			}
		} catch (Exception e)
		{
			resultMap.put("X_RESULTCODE", "-1");
			resultMap.put("X_RESULTINFO", "插入TF_B_STAFFOPERLOG失败");
			resultMap.put("X_RSPTYPE", "2");
			resultMap.put("X_RSPCODE", "2998");
			e.printStackTrace();

		} finally
		{
			result.add(resultMap);
			return result;
		}
	}

	public IDataset queryUnionTransPhone(IData input) throws Exception
	{
		String xGetMode = IDataUtil.chkParam(input, "X_GETMODE");
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		// 1－根据移动号码查询联通号码 0－根据联通号码查询移动号码
		String phoneCodeA = null;
		String phoneCodeB = null;
		IDataset phoneTrans = null;
		if ("0".equals(xGetMode))
		{
			phoneTrans = SundryQry.queryUnionTransIntf(phoneCodeA, serialNumber);
		} else if ("1".equals(xGetMode))
		{
			phoneTrans = SundryQry.queryUnionTransIntf(serialNumber, phoneCodeB);
		}

		if (IDataUtil.isNotEmpty(phoneTrans))
		{
			for (int i = 0; i < phoneTrans.size(); i++)
			{
				phoneTrans.getData(i).put("SERIAL_NUMBER", serialNumber);
			}
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_244);
		}
		return phoneTrans;
	}

	public IDataset queryUserAttr(IData input) throws Exception
	{
		IDataset rets = new DatasetList();

		IData ret0 = new DataMap();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String provCode = input.getString("PROV_CODE");
		String recNumb = input.getString("REC_NUMB");

		IDataset blackUsers = BlackUserInfoQry.queryBlackUserInfo(serialNumber, provCode);

		ret0.put("USER_ID", serialNumber);
		ret0.put("REC_NUMB", recNumb);
		ret0.put("PROV_CODE", "");
		ret0.put("IS_WHITE", "4");

		if (IDataUtil.isEmpty(blackUsers))
		{
			ret0.put("IS_WHITE", "2");
		} else
		{
			ret0.put("IS_WHITE", "1");
			ret0.put("PROV_CODE", blackUsers.getData(0).getString("PROVINCE_CODE", ""));
		}

		rets.add(ret0);
		return rets;
	}

	/**
	 * 外围接口查询返销接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryUserCancelTradeIntf(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String beginDate = IDataUtil.chkParam(input, "BEGIN_DATE");
		String endDate = IDataUtil.chkParam(input, "END_DATE");

		IDataset datas = TradeHistoryInfoQry.queryUserCancelTradeIntf(serialNumber, CSBizBean.getVisit().getStaffEparchyCode(), beginDate, endDate);

		if (IDataUtil.isEmpty(datas))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1140);
		}
		return datas;
	}

	/**
	 * 查询用户优惠表和某个服务相关联的优惠，当前支持GPRS服务
	 * 
	 * @Function: queryUserDiscntByType
	 * @Description: ITF_CRM_QueryUserDiscntRelySvc
	 * @date Jul 11, 2014 9:47:40 PM
	 * @param input
	 * @return
	 * @throws Exception
	 * @author longtian3
	 */
	public IDataset queryUserDiscntByType(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serviceid = IDataUtil.chkParam(input, "ELEMENT_ID");

		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_126);
		}

		// 获得服务绑定的优惠类型
		IDataset discntInfo = CommparaInfoQry.getCommparaAllCol("CSM", "1113", serviceid, CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(discntInfo))
		{
			CSAppException.apperr(SvcException.CRM_SVC_7, serviceid);
		}

		String userId = userInfo.getString("USER_ID");
		String discntTypeCode = discntInfo.getData(0).getString("PARA_CODE1", "");
		IDataset result = UserDiscntInfoQry.qryUserOwn(userId, discntTypeCode, CSBizBean.getUserEparchyCode());

		return result;
	}

	public IDataset getProductForGotone(String productId, String paraAttr) throws Exception
	{
		IDataset paraDatas = CommparaInfoQry.getCommByParaAttr("CSM", paraAttr, CSBizBean.getTradeEparchyCode());

		if (IDataUtil.isNotEmpty(paraDatas))
		{
			for (int i = 0; i < paraDatas.size(); i++)
			{
				IData para = paraDatas.getData(i);
				String paraCode1 = para.getString("PARA_CODE1");
				if (StringUtils.equals(productId, paraCode1))
				{
					paraDatas.remove(i);
					i--;
					continue;
				}
				IData offer = UpcCall.queryOfferByOfferId("P", paraCode1);
				if (IDataUtil.isNotEmpty(offer))
				{
					para.put("ENABLE_TAG", offer.getString("ENABLE_MODE"));
					para.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
					para.put("ELEMENT_DESC", offer.getString("DESCRIPTION"));
				}
			}
		}

		return paraDatas;
	}

	public IDataset queryUserDiscntInfo(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String inModeCode = input.getString("IN_MODE_CODE", "");
		if (StringUtils.isBlank(inModeCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IN_MODE_CODE");
		}

		String kindId = input.getString("KIND_ID", "");
		if (StringUtils.isBlank(kindId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "KIND_ID");
		}

		String idType = input.getString("IDTYPE", "");
		if (StringUtils.isBlank(idType))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDTYPE");
		}

		String idItemRange = input.getString("IDITEMRANGE", "");
		if (StringUtils.isBlank(idItemRange))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDITEMRANGE");
		}

		String serialNumber = input.getString("SERIAL_NUMBER");

		// 判断是否为多账期
		boolean ifuseracct = false; // 是否为多账期

		String firstDayThisAcct = SysDateMgr.firstDayOfMonth(0);// 本账期第一天
		String lastDayThisAcct = SysDateMgr.getLastDayOfMonth(0);// 本账期最后一天
		String firstDayNextAcct = SysDateMgr.firstDayOfMonth(1);// 下账期第一天
		String lastDayNextAcct = SysDateMgr.getLastDayOfMonth(1);// 下账期最后一天

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String userId = userInfo.getString("USER_ID");

		IDataset userProducts = UserProductInfoQry.queryMainProductNow(userId);
		if (IDataUtil.isEmpty(userProducts))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}
		String productId = userProducts.getData(0).getString("PRODUCT_ID");

		String nowPackCode = "";// 本帐期主套餐标识
		String nowLocalCode = "";// 本账期省内套餐编码
		String nowPackDesc = "";// 本账期套餐描述

		IDataset paraDatas = this.getProductForGotone(productId, "2691");
		if (IDataUtil.isEmpty(paraDatas))
		{
			nowPackCode = "999999999999999999";
			IData productInfo = UpcCall.queryOfferByOfferId("P", productId);
			// IData productInfo = UProductInfoQry.qryProductByPK(productId);
			if (IDataUtil.isNotEmpty(productInfo))
			{
				nowPackDesc = productInfo.getString("OFFER_NAME", "") + "；" + productInfo.getString("DESCRIPTION", "");
			}
		} else
		{
			nowPackCode = paraDatas.getData(0).getString("PARAM_CODE", "");
			String paraCode1 = paraDatas.getData(0).getString("PARA_CODE1", "");
			nowLocalCode = "";
			nowPackDesc = paraDatas.getData(0).getString("ELEMENT_DESC", "");
			/*
			 * IDataset elemDescs = ElemInfoQry.getDescInfo(paraCode1, "P",
			 * "09"); if (IDataUtil.isNotEmpty(elemDescs)) { nowPackDesc =
			 * elemDescs.getData(0).getString("ELEMENT_DESC", ""); }
			 */
		}

		String nextPackCode = "";// 下帐期主套餐标识
		String nextLocalCode = "";// 下账期省内套餐编码
		String nextPackDesc = "";// 下账期套餐描述

		IDataset userNextProducts = UserProductInfoQry.queryProductByUserIdAndStartDate(firstDayNextAcct, userId);
		if (IDataUtil.isEmpty(userNextProducts))
		{
			nextPackCode = nowPackCode;
			nextLocalCode = nowLocalCode;
			nextPackDesc = nowPackDesc;
		} else
		{
			String nextProductId = userNextProducts.getData(0).getString("PRODUCT_ID");
			IDataset nextcommparas = this.getProductForGotone(nextProductId, "2691");
			if (IDataUtil.isEmpty(nextcommparas))
			{
				nextPackCode = "999999999999999999";
				nextLocalCode = productId;
				IData productInfo = UpcCall.queryOfferByOfferId("P", productId);
				if (IDataUtil.isNotEmpty(productInfo))
				{
					nextPackDesc = productInfo.getString("OFFER_NAME", "") + "；" + productInfo.getString("DESCRIPTION", "");
				}
			} else
			{
				nextPackCode = nextcommparas.getData(0).getString("PARAM_CODE", "");
				String paraCode1 = nextcommparas.getData(0).getString("PARA_CODE1", "");
				nextLocalCode = "";
				nowPackDesc = nextcommparas.getData(0).getString("ELEMENT_DESC", "");
				/*
				 * IDataset elemDescs = ElemInfoQry.getDescInfo(paraCode1, "P",
				 * "09"); if (IDataUtil.isNotEmpty(elemDescs)) { nowPackDesc =
				 * elemDescs.getData(0).getString("ELEMENT_DESC", ""); }
				 */
			}
		}

		IData ret0 = new DataMap();
		ret0.put("IDTYPE", idType);// 标识类型
		ret0.put("IDITEMRANGE", idItemRange);// 标识号码
		ret0.put("ACCOUNT_TYPE", ifuseracct ? "2" : "1");// 帐期类型 1:自然月账期，2:分散帐期
		ret0.put("CURR_BILL_CYCLE_START_DATE", firstDayThisAcct.replaceAll("-", ""));// 当前帐期的起始日
		ret0.put("CURR_BILL_CYCLE_END_DATE", lastDayThisAcct.replaceAll("-", ""));// 当前帐期结束日
		ret0.put("NEXT_BILL_CYCLE_END_DATE", lastDayNextAcct.replaceAll("-", ""));// 下一帐期结束日
		ret0.put("NOW_PACK_CODE", nowPackCode);// 本帐期主套餐标识
		ret0.put("NOW_PACK_DESC", nowPackDesc);// 本账期套餐描述
		ret0.put("NOW_LOCAL_CODE", nowLocalCode);// 本账期省内套餐编码
		ret0.put("NEXT_PACK_CODE", nextPackCode);// 下帐期主套餐标识
		ret0.put("NEXT_PACK_DESC", nextPackDesc);// 下账期主套餐描述
		ret0.put("NEXT_LOCAL_CODE", nextLocalCode);// 下账期省内套餐编码

		rets.add(ret0);
		return rets;
	}

	public IDataset queryUserDiscntIVR(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		IData ret0 = new DataMap();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = user.getString("USER_ID");
		String eparchyCode = user.getString("EPARCHY_CODE");

		IDataset userDiscnts = UserDiscntInfoQry.getUserDiscnt(userId, eparchyCode);

		if (IDataUtil.isEmpty(userDiscnts))
		{
			ret0.put("RSRV_STR4", "0");
		} else
		{
			ret0.put("RSRV_STR1", userDiscnts.getData(0).getString("PARAM_CODE"));
			ret0.put("RSRV_STR2", userDiscnts.getData(0).getString("PARAM_NAME"));
			ret0.put("RSRV_STR3", userDiscnts.getData(0).getString("PARA_CODE2"));
			ret0.put("RSRV_STR4", "1");
		}
		rets.add(ret0);
		return rets;
	}

	public IDataset queryUserForegift(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = user.getString("USER_ID");
		return UserForegiftInfoQry.getUserForegift(userId);
	}

	public IData QueryUserInfo(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String operNumb = input.getString("OPER_NUMB");
		String passwd = input.getString("USER_PWD");

		// 获取三户资料
		IDataset datasetUca = getUCAInfobySn(serialNumber);
		IData userInfo = datasetUca.getData(0).getData("USER_INFO");
		IData custInfo = datasetUca.getData(0).getData("CUST_INFO");
		IData vipInfo = datasetUca.getData(0).getData("VIP_INFO");

		IData retList = new DataMap();
		retList.put("OPER_NUMB", operNumb);
		retList.put("RECEIVE_OPER_NUMB", operNumb);
		retList.put("SERIAL_NUMBER", serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			retList.put("X_RESULTCODE", "2998");
			retList.put("X_RESULTINFO", "未找到此用户信息");
			retList.put("X_RECORDNUM", "0");
			retList.put("X_RSPTYPE", "2");
			retList.put("X_RSPCODE", "2998");
			return retList;
		} else
		{
			String userId = userInfo.getString("USER_ID");
			// 判断用户客服密码是否正确
			if (StringUtils.isNotBlank(passwd))
			{
				// boolean flag = UserInfoQry.checkUserPassword(userId, passwd);
				boolean flag = PasswdMgr.checkUserPassword(passwd, userId, userInfo.getString("USER_PASSWD"));
				if (!flag)
				{
					retList.put("X_RESULTCODE", "2996");
					retList.put("X_RESULTINFO", "用户密码错误");
					retList.put("X_RECORDNUM", "0");
					retList.put("X_RSPTYPE", "2");
					retList.put("X_RSPCODE", "2998");
					return retList;
				}
			}

			LanuchUtil ltool = new LanuchUtil();
			retList.put("CUST_NAME", custInfo.getString("CUST_NAME"));
			retList.put("PSPT_TYPE_CODE", ltool.decodeIdType(custInfo.getString("PSPT_TYPE_CODE")));
			retList.put("PSPT_ID", custInfo.getString("PSPT_ID"));
			retList.put("POST_ADDRESS", custInfo.getString("POST_ADDRESS"));
			retList.put("EMAIL", custInfo.getString("EMAIL"));
			retList.put("BRAND_CODE", InterfaceUtil.convertBrandCode(userInfo.getString("BRAND_CODE")));
			retList.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(userInfo.getString("BRAND_CODE")));

			if (IDataUtil.isEmpty(vipInfo))
			{
				// 没有大客户信息
				vipInfo.put("VIP_TAG", "N");
				vipInfo.put("CLASS_NAME", "");
			}

			if ("N".equals(vipInfo.getString("VIP_TAG")))
			{
				retList.put("CUST_LEVEL", "100");
			} else
			{
				retList.put("CUST_LEVEL", InterfaceUtil.getIBossUserRank(vipInfo.getString("VIP_TYPE_CODE"), vipInfo.getString("VIP_CLASS_ID")));
			}
			retList.put("IS_VIP", retList.getString("CUST_LEVEL").equals("100") ? "0" : "1");
			if ("1".equals(retList.getString("IS_VIP")))
			{
				retList.put("VIP_NO", custInfo.getString("VIP_NO", "0000"));
			}

			retList.put("SCORE_VALUE", queryUserScore(userId));
			// retList.put("CREDIT_VALUE", getCreditValue(userId, "1"));
			retList.put("CREDIT_VALUE", getCreditValue(userId, "0"));// 账管那边
																		// 0是按USER_ID查询
																		// 1按CUST_ID查询
																		// 原逻辑有问题,修改
																		// duhj
																		// 2017/5/15
			retList.put("USER_STATE_CODESET", InterfaceUtil.getIBossUserState(userInfo.getString("USER_STATE_CODESET")));

			retList.put("IS_CONTRACT_BIND", "1");
			retList.put("CONTRACT_COMMENT", "");
			boolean productFalg = ProductInfoQry.queryProductType(userInfo.getString("PRODUCT_ID"));
			retList.put("PRODUCT_TYPE", productFalg ? "1" : "0");
			retList.put("RESOURCE_CODE", "");
			retList.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(userInfo.getString("PRODUCT_ID")));
			retList.put("X_RESULTCODE", "0");
			retList.put("X_RESULTINFO", "ok");
			retList.put("X_RECORDNUM", "1");

		}

		return retList;
	}

	// 查询用户主体优惠是否受包月限制，如果限制查询出限制天数
	public IData queryUserMainDiscnt(IData input) throws Exception
	{

		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData userInfo = getUserInfo4SingleAll(serialNumber);
		String userId = userInfo.getString("USER_ID");
		IDataset mainDiscnt = UserDiscntInfoQry.queryUserMainDiscnt(userId);
		if (IDataUtil.isEmpty(mainDiscnt))
		{
			return null;
		}

		return mainDiscnt.getData(0);
	}

	/**
	 * 查询动感地带校园音乐套餐用户主体优惠变更范围
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 * @date 2014-07-09 20:49:38
	 */
	public IDataset queryUserMainDiscntScope(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");

		String serialNumber = input.getString("SERIAL_NUMBER");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);

		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
		}

		String userId = user.getString("USER_ID", "");

		IDataset result = UserDiscntInfoQry.queryUserMainDiscntScope(userId);

		if (IDataUtil.isEmpty(result))
		{
			result = new DatasetList();
		}

		return result;
	}

	public String queryUserScore(String userId) throws Exception
	{
		String userScore = "0";

		IDataset userScoreData = AcctCall.queryUserScore(userId);

		if (IDataUtil.isNotEmpty(userScoreData))
		{
			userScore = userScoreData.getData(0).getString("SUM_SCORE", "0");
		}

		return userScore;
	}

	public IDataset QueryUserUsingBizInfo(IData input) throws Exception
	{

		IDataset rets = new DatasetList();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IDataset users = UserInfoQry.getUserInfoBySn(serialNumber, "0", "00", null);
		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = users.getData(0).getString("USER_ID");
		String userIdA = "-1";

		String curdate = SysDateMgr.date2String(java.util.Calendar.getInstance().getTime(), SysDateMgr.PATTERN_STAND);

		IDataset userProducts = UserProductInfoQry.getProductInfo(userId, userIdA, null);
		IDataset userProducts2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userProducts))
		{
			IData userProduct2 = null;
			for (int i = 0; i < userProducts.size(); i++)
			{
				userProduct2 = new DataMap();
				IData userProduct = userProducts.getData(i);
				userProduct2.put("BIZ_ID", userProduct.getString("PRODUCT_ID"));
				userProduct2.put("BIZ_NAME", UProductInfoQry.getProductNameByProductId(userProduct.getString("PRODUCT_ID")));
				userProduct2.put("BIZ_START_DATE", userProduct.getString("START_DATE"));
				userProduct2.put("BIZ_END_DATE", userProduct.getString("END_DATE"));
				userProduct2.put("BIZ_TIME", curdate);
				userProduct2.put("BIZ_TYPE", "套餐");
				userProducts2.add(userProduct2);
			}
		}

		IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
		IDataset userDiscnts2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			IData userDiscnt2 = null;
			for (int i = 0; i < userDiscnts.size(); i++)
			{
				userDiscnt2 = new DataMap();
				IData userDiscnt = userDiscnts.getData(i);
				userDiscnt2.put("BIZ_ID", userDiscnt.getString("DISCNT_CODE"));
				userDiscnt2.put("BIZ_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscnt.getString("DISCNT_CODE")));
				userDiscnt2.put("BIZ_START_DATE", userDiscnt.getString("START_DATE"));
				userDiscnt2.put("BIZ_END_DATE", userDiscnt.getString("END_DATE"));
				userDiscnt2.put("BIZ_TIME", curdate);
				userDiscnt2.put("BIZ_TYPE", "套餐");
				userDiscnts2.add(userDiscnt2);
			}
		}

		IDataset userSvcs = UserSvcInfoQry.queryUserSvcByUserIdAll(userId);
		IDataset userSvcs2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userSvcs))
		{
			IData userSvc2 = null;
			for (int i = 0; i < userSvcs.size(); i++)
			{
				userSvc2 = new DataMap();
				IData userSvc = userSvcs.getData(i);
				userSvc2.put("BIZ_ID", userSvc.getString("SERVICE_ID"));
				userSvc2.put("BIZ_NAME", userSvc.getString("SERVICE_NAME"));
				userSvc2.put("BIZ_START_DATE", userSvc.getString("START_DATE"));
				userSvc2.put("BIZ_END_DATE", userSvc.getString("END_DATE"));
				userSvc2.put("BIZ_TIME", curdate);
				userSvc2.put("BIZ_TYPE", "服务");
				userSvcs2.add(userSvc2);
			}
		}

		IDataset userPlatsvcs = UserPlatInfoQry.getUserPlatInfoByUserId(userId);
		IDataset userPlatsvcs2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userPlatsvcs))
		{
			IData userPlatsvc2 = null;
			for (int i = 0; i < userPlatsvcs.size(); i++)
			{
				userPlatsvc2 = new DataMap();
				IData userPlatsvc = userPlatsvcs.getData(i);
				userPlatsvc2.put("BIZ_ID", userPlatsvc.getString("SERVICE_ID"));
				// userPlatsvc2.put("BIZ_NAME",
				// StaticUtil.getStaticValue(CSBizBean.getVisit(),
				// "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME",
				// userPlatsvc.getString("SERVICE_ID")));
				userPlatsvc2.put("BIZ_NAME", UPlatSvcInfoQry.getSvcNameBySvcId(userPlatsvc.getString("SERVICE_ID"))); // modify
																														// by
																														// duhj
				userPlatsvc2.put("BIZ_START_DATE", userPlatsvc.getString("START_DATE"));
				userPlatsvc2.put("BIZ_END_DATE", userPlatsvc.getString("END_DATE"));
				userPlatsvc2.put("BIZ_TIME", curdate);
				userPlatsvc2.put("BIZ_TYPE", "平台服务");
				userPlatsvcs2.add(userPlatsvc2);
			}
		}

		IDataset userSaleactives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
		IDataset userSaleactives2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userSaleactives))
		{
			IData userSaleactive2 = null;
			for (int i = 0; i < userSaleactives.size(); i++)
			{
				userSaleactive2 = new DataMap();
				IData userSaleactive = userSaleactives.getData(i);
				userSaleactive2.put("BIZ_ID", userSaleactive.getString("PRODUCT_ID"));
				userSaleactive2.put("BIZ_NAME", userSaleactive.getString("PACKAGE_NAME"));
				userSaleactive2.put("BIZ_START_DATE", userSaleactive.getString("START_DATE"));
				userSaleactive2.put("BIZ_END_DATE", userSaleactive.getString("END_DATE"));
				userSaleactive2.put("BIZ_TIME", curdate);
				userSaleactive2.put("BIZ_TYPE", "营销活动");
				userSaleactives2.add(userSaleactive2);
			}
		}
		rets.addAll(userProducts2);
		rets.addAll(userDiscnts2);
		rets.addAll(userSvcs2);
		rets.addAll(userPlatsvcs2);
		rets.addAll(userSaleactives2);
		return rets;
	}

	public IDataset QueryUserUsingHBizInfo(IData input) throws Exception
	{

		IDataset rets = new DatasetList();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		int beforeCurDays = -10000;// 当前时间往前推10000天，亚信crm当时在海南吗？
		IDataset users = UserInfoQry.getUserInfoBySn(serialNumber, "0", "00", null);
		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = users.getData(0).getString("USER_ID");
		String startDate = SysDateMgr.suffixDate(SysDateMgr.addDays(beforeCurDays), 0);
		String endDate = SysDateMgr.getEndCycle20501231() + SysDateMgr.getEndTime235959();
		String userIdA = "-1";

		String curdate = SysDateMgr.date2String(java.util.Calendar.getInstance().getTime(), SysDateMgr.PATTERN_STAND);

		IDataset userProducts = UserProductInfoQry.getUserProductByStartEndDate(userId, userIdA, startDate, endDate);
		IDataset userProducts2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userProducts))
		{
			IData userProduct2 = null;
			for (int i = 0; i < userProducts.size(); i++)
			{
				userProduct2 = new DataMap();
				IData userProduct = userProducts.getData(i);
				userProduct2.put("BIZ_ID", userProduct.getString("PRODUCT_ID"));
				userProduct2.put("BIZ_NAME", UProductInfoQry.getProductNameByProductId(userProduct.getString("PRODUCT_ID")));
				userProduct2.put("BIZ_START_DATE", userProduct.getString("START_DATE"));
				userProduct2.put("BIZ_END_DATE", userProduct.getString("END_DATE"));
				userProduct2.put("BIZ_TIME", curdate);
				userProduct2.put("BIZ_TYPE", "套餐");
				userProducts2.add(userProduct2);
			}
		}

		IDataset userDiscnts = UserDiscntInfoQry.getUserDiscntByStartEndDate(userId, startDate, endDate);
		IDataset userDiscnts2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			IData userDiscnt2 = null;
			for (int i = 0; i < userDiscnts.size(); i++)
			{
				userDiscnt2 = new DataMap();
				IData userDiscnt = userDiscnts.getData(i);
				userDiscnt2.put("BIZ_ID", userDiscnt.getString("DISCNT_CODE"));
				userDiscnt2.put("BIZ_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscnt.getString("DISCNT_CODE")));
				userDiscnt2.put("BIZ_START_DATE", userDiscnt.getString("START_DATE"));
				userDiscnt2.put("BIZ_END_DATE", userDiscnt.getString("END_DATE"));
				userDiscnt2.put("BIZ_TIME", curdate);
				userDiscnt2.put("BIZ_TYPE", "套餐");
				userDiscnts2.add(userDiscnt2);
			}
		}

		IDataset userSvcs = UserSvcInfoQry.getUserSvcByStartEndDate(userId, startDate, endDate);
		IDataset userSvcs2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userSvcs))
		{
			IData userSvc2 = null;
			for (int i = 0; i < userSvcs.size(); i++)
			{
				userSvc2 = new DataMap();
				IData userSvc = userSvcs.getData(i);
				userSvc2.put("BIZ_ID", userSvc.getString("SERVICE_ID"));
				userSvc2.put("BIZ_NAME", USvcInfoQry.getSvcNameBySvcId(userSvc.getString("SERVICE_ID")));
				userSvc2.put("BIZ_START_DATE", userSvc.getString("START_DATE"));
				userSvc2.put("BIZ_END_DATE", userSvc.getString("END_DATE"));
				userSvc2.put("BIZ_TIME", curdate);
				userSvc2.put("BIZ_TYPE", "服务");
				userSvcs2.add(userSvc2);
			}
		}

		IDataset userPlatsvcs = UserPlatInfoQry.getUserPlatByStartEndDate(userId, startDate, endDate);
		IDataset userPlatsvcs2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userPlatsvcs))
		{
			IData userPlatsvc2 = null;
			for (int i = 0; i < userPlatsvcs.size(); i++)
			{
				userPlatsvc2 = new DataMap();
				IData userPlatsvc = userPlatsvcs.getData(i);
				userPlatsvc2.put("BIZ_ID", userPlatsvc.getString("SERVICE_ID"));
				userPlatsvc2.put("BIZ_NAME", UPlatSvcInfoQry.getSvcNameBySvcId(userPlatsvc.getString("SERVICE_ID")));
				userPlatsvc2.put("BIZ_START_DATE", userPlatsvc.getString("START_DATE"));
				userPlatsvc2.put("BIZ_END_DATE", userPlatsvc.getString("END_DATE"));
				userPlatsvc2.put("BIZ_TIME", curdate);
				userPlatsvc2.put("BIZ_TYPE", "平台服务");
				userPlatsvcs2.add(userPlatsvc2);
			}
		}

		IDataset userSaleactives = UserSaleActiveInfoQry.getUserSaleActiveByStartEndDate(userId, startDate, endDate);
		IDataset userSaleactives2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userSaleactives))
		{
			IData userSaleactive2 = null;
			for (int i = 0; i < userSaleactives.size(); i++)
			{
				userSaleactive2 = new DataMap();
				IData userSaleactive = userSaleactives.getData(i);
				userSaleactive2.put("BIZ_ID", userSaleactive.getString("PRODUCT_ID"));
				userSaleactive2.put("BIZ_NAME", userSaleactive.getString("PACKAGE_NAME"));
				userSaleactive2.put("BIZ_START_DATE", userSaleactive.getString("START_DATE"));
				userSaleactive2.put("BIZ_END_DATE", userSaleactive.getString("END_DATE"));
				userSaleactive2.put("BIZ_TIME", curdate);
				userSaleactive2.put("BIZ_TYPE", "营销活动");
				userSaleactives2.add(userSaleactive2);
			}
		}
		rets.addAll(userProducts2);
		rets.addAll(userDiscnts2);
		rets.addAll(userSvcs2);
		rets.addAll(userPlatsvcs2);
		rets.addAll(userSaleactives2);
		return rets;
	}

	public IDataset queryVEMLAttr(IData input) throws Exception
	{

		IDataset rets = new DatasetList();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String oprNumb = IDataUtil.chkParam(input, "OPR_NUMB");

		IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = user.getString("USER_ID");

		IData ret0 = new DataMap();
		ret0.put("ID_TYPE", "01");
		ret0.put("ID_VALUE", serialNumber);
		ret0.put("OPR_NUMB", oprNumb);

		String cfunum = "";// 无条件呼转号码
		String cfbnum = "";// 遇忙呼转号码
		String cfnrynum = "";// 无应答转号码
		String cfnrcnum = "";// 不可及转号

		IDataset staticInfos = StaticInfoQry.getStaticValueByTypeId("VEML_SVC_CODE");
		if (IDataUtil.isNotEmpty(staticInfos))
		{
			for (int i = 0; i < staticInfos.size(); i++)
			{
				IData staticInfo = staticInfos.getData(i);
				String dataId = staticInfo.getString("DATA_ID");
				String dataName = staticInfo.getString("DATA_NAME");
				IDataset userAttrs = UserAttrInfoQry.getAutoPayContractInfo(userId, dataName);
				if (IDataUtil.isNotEmpty(userAttrs))
				{
					IData userAttr0 = userAttrs.getData(0);
					String attrValue = userAttr0.getString("ATTR_VALUE");
					if (StringUtils.equals("1", dataId))
					{
						cfunum = attrValue;
					} else if (StringUtils.equals("2", dataId))
					{
						cfbnum = attrValue;
					} else if (StringUtils.equals("3", dataId))
					{
						cfnrynum = attrValue;
					} else if (StringUtils.equals("4", dataId))
					{
						cfnrcnum = attrValue;
					}
				}
			}
		}

		ret0.put("CFU_NUM", cfunum);// 无条件呼转号码
		ret0.put("CFB_NUM", cfbnum);// 遇忙呼转号码
		ret0.put("CFNRY_NUM", cfnrynum);// 无应答转号码
		ret0.put("CFNRC_NUM", cfnrcnum);// 不可及转号
		rets.add(ret0);
		return rets;
	}

	public IDataset queryVipExchangeGifts(IData input) throws Exception
	{
		IDataset result = new DatasetList();
		// 服务号码
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		// 兑换礼品类别：1-大客户生日礼品券兑换、2-大客户消费情况兑换、3-大客户生日话费直充、4-网上兑换电影票
		IDataUtil.chkParam(input, "GIFT_TYPE_CODE");

		String serialNumber = input.getString("SERIAL_NUMBER");
		IDataset vipInfo = CustVipInfoQry.qryVipInfoBySn(serialNumber);

		if (IDataUtil.isNotEmpty(vipInfo))
		{
			String userVipClassId = vipInfo.getData(0).getString("VIP_CLASS_ID");
			String inGiftTypeCode = input.getString("GIFT_TYPE_CODE");

			if (StringUtils.isNotBlank(inGiftTypeCode))
			{
				IDataset paraInfo = CommparaInfoQry.getCommpara("CSM", "362", inGiftTypeCode, "0898");
				if (IDataUtil.isNotEmpty(paraInfo))
				{
					// 需要查询用户上月消费情况
					int intfFee = 0; // 单位分
					if ("2".equals(inGiftTypeCode))
					{
						IData userInfo = getUserInfo4SingleAll(serialNumber);
						if (IDataUtil.isNotEmpty(userInfo))
						{
							// 调用账户接口获取上月消费情况
							IDataset acctInfo = AcctCall.queryLastMonthFee(serialNumber, userInfo.getString("USER_ID"));

							if (IDataUtil.isNotEmpty(acctInfo))
							{
								// 调用成功，记录兑换日志
								intfFee = acctInfo.getData(0).getInt("ALL_RETURN_FEE", 0); // 单位：分

							}

						}
					}

					for (int i = 0; i < paraInfo.size(); i++)
					{
						IData each = paraInfo.getData(i);
						// 1-大客户生日礼品券兑换、3-大客户生日话费直充,这两类兑换需要校验大客户级别
						if ("1".equals(inGiftTypeCode) || "3".equals(inGiftTypeCode))
						{
							String paraVipClassId = each.getString("PARA_CODE3", "");
							if (paraVipClassId.equals(userVipClassId))
							{
								each.put("GIFT_TYPE_CODE", each.getString("PARAM_CODE", ""));
								each.put("GIFT_ID", each.getString("PARA_CODE1", ""));
								each.put("GIFT_NAME", each.getString("PARAM_NAME", ""));
								result.add(each);
								continue;
							}
						}
						// 2-大客户消费情况兑换，这类兑换需要判断消费情况
						else if ("2".equals(inGiftTypeCode))
						{
							int paraFee1 = each.getInt("PARA_CODE4", 0);
							int paraFee2 = each.getInt("PARA_CODE5", 0);
							// 判断用户上月消费情况是否在区间：paraFee1≤intfFee＜paraFee2
							if (intfFee >= paraFee1 && intfFee < paraFee2)
							{
								each.put("GIFT_TYPE_CODE", each.getString("PARAM_CODE", ""));
								each.put("GIFT_ID", each.getString("PARA_CODE1", ""));
								each.put("GIFT_NAME", each.getString("PARAM_NAME", ""));
								result.add(each);
								continue;
							}
						}
						// 4-网上兑换电影票
						else if ("4".equals(inGiftTypeCode))
						{
							each.put("GIFT_TYPE_CODE", each.getString("PARAM_CODE", ""));
							each.put("GIFT_ID", each.getString("PARA_CODE1", ""));
							each.put("GIFT_NAME", each.getString("PARAM_NAME", ""));
							result.add(each);
							continue;
						} else
						{
							each.put("GIFT_TYPE_CODE", each.getString("PARAM_CODE", ""));
							each.put("GIFT_ID", each.getString("PARA_CODE1", ""));
							each.put("GIFT_NAME", each.getString("PARAM_NAME", ""));
							result.add(each);
							continue;
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * @Function: queryVoIp
	 * @Description: ITF_CRM_VoIPQry VoIP订购关系查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 */
	public IDataset queryVoIp(IData data) throws Exception
	{
		IData result = new DataMap();
		String serialNumber = IDataUtil.chkParam(data, "ID_VALUE");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1);
		}
		String userId = userInfo.getString("USER_ID");

		IDataset platsvc = UserPlatSvcInfoQry.queryUserPlatByUserIdAndServiceId(userId, "71");
		if (platsvc != null && platsvc.size() > 0)
		{
			result.put("X_RESULTCODE", "0");
		} else
		{
			result.put("X_RESULTCODE", "2034");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
		}
		result.put("ID_TYPE", "01");
		result.put("ID_VALUE", serialNumber);
		result.put("OPR_NUMB", data.getString("OPR_NUMB"));

		return new DatasetList(result);
	}

	public IDataset queryWidNetProductInfo(IData input) throws Exception
	{
		IDataset rets = new DatasetList();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IData widnetUser0 = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
		if (IDataUtil.isEmpty(widnetUser0))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1109);
		}

		String userId = widnetUser0.getString("USER_ID");

		String wideType = input.getString("WIDE_TYPE");
		String productMode = "07";// 针对gpon

		if ("1".equals(wideType))
		{
			productMode = "09"; // 针对adsl
		} else if ("2".equals(wideType))
		{
			productMode = "11";// 针对光纤
		} else if ("3".equals(wideType))
		{
			productMode = "13";// 针对校园宽带
		}

		IDataset userWidnetProdcutInfos = UserProductInfoQry.getUserWidenetProductInfo(userId, productMode);
		if (IDataUtil.isEmpty(userWidnetProdcutInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1110);
		}

		String proudctId = userWidnetProdcutInfos.getData(0).getString("PRODUCT_ID");
		IData widnetProduct = UProductInfoQry.qryProductByPK(proudctId);

		if (IDataUtil.isEmpty(userWidnetProdcutInfos))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1033, proudctId);

		}

		String proudctName = widnetProduct.getString("PRODUCT_NAME");
		String pMode = widnetProduct.getString("PRODUCT_MODE");
		String pMode2 = "";
		if ("07".equals(pMode))
		{
			pMode2 = "0";
		} else if ("09".equals(pMode))
		{
			pMode2 = "1";
		} else if ("11".equals(pMode))
		{
			pMode2 = "2";
		}

		IData ret0 = new DataMap();
		ret0.put("INFO_CODE", proudctId);
		ret0.put("INFO_NAME", proudctName);
		ret0.put("STARTDATE", userWidnetProdcutInfos.getData(0).getString("START_DATE"));
		ret0.put("ENDDATE", userWidnetProdcutInfos.getData(0).getString("END_DATE"));
		ret0.put("ELEMENT_ID", "1");
		ret0.put("PRODUCT_MODE", pMode2);
		rets.add(ret0);
		return rets;
	}

	/**
	 * 短信白名单 ITF_CRM_RedMember
	 * 
	 * @param IData
	 * @return IDataset
	 * @throws Exception
	 * @author huanghui@asiainfo.com
	 */
	public IDataset redMemberDeal(IData param) throws Exception
	{
		IDataUtil.chkParam(param, "SERIAL_NUMBER");
		IDataUtil.chkParam(param, Route.ROUTE_EPARCHY_CODE);
		IDataUtil.chkParam(param, "OPER_TYPE");
		String serialNumber = param.getString("SERIAL_NUMBER");
		String routeEparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);
		String operType = param.getString("OPER_TYPE", "");
		CreateRedMemberBean bean = (CreateRedMemberBean) BeanManager.createBean(CreateRedMemberBean.class);
		IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);
		IData inParamb = new DataMap();
		IData inParamc = new DataMap();
		if (operType.equals("0"))
		{
			if (IDataUtil.isEmpty(userInfos))
			{
				CSAppException.apperr(CrmUserException.CRM_USER_1151);
			}
			createRedMemberCheck(param);

			IData inParam = new DataMap();
			inParam.put("SERIAL_NUMBER", serialNumber);
			inParam.put("USER_ID", userInfos.getString("USER_ID"));
			if (StringUtils.isBlank(param.getString("START_TIME", "")))
			{
				inParam.put("START_TIME", SysDateMgr.getSysDate());
			} else
			{
				inParam.put("START_TIME", param.getString("START_TIME", ""));
			}

			if (StringUtils.isBlank(param.getString("END_TIME", "")))
			{
				inParam.put("END_TIME", SysDateMgr.getTheLastTime());
			} else
			{
				inParam.put("END_TIME", param.getString("END_TIME") + SysDateMgr.getEndTime235959());
			}
			inParam.put("REMARK", param.getString("REMARK", ""));
			IDataset params = IDataUtil.idToIds(inParam);
			bean.createRedMember(params);
			// 在黑名单表中存在的有效的用户
			IDataset dataset = RedMemberDealInfoQry.getBlackUserdata(userInfos.getString("USER_ID"));
			IData inParama = new DataMap();

			if (IDataUtil.isNotEmpty(dataset))
			{
				inParama.clear();
				inParama.put("USER_ID", userInfos.getString("USER_ID"));
				inParama.put("PROCESS_TAG", "1");
				RedMemberDealInfoQry.updateExitBlackUser(inParama);

				inParama.put("PROCESS_TAG", "2");
				inParama.put("SERIAL_NUMBER", "86" + serialNumber);
				RedMemberDealInfoQry.InsertBlackUser(inParama);
			}
		} else
		{
			inParamb.clear();
			inParamb.put("SERIAL_NUMBER", serialNumber);
			IDataset result = CreateRedMemberQry.checkRedMemberIsExists(inParamb);
			if (IDataUtil.isEmpty(result))
			{
				CSAppException.apperr(CrmUserException.CRM_USER_1187);
			}
			inParamc.clear();
			inParamc.put("USER_ID", userInfos.getString("USER_ID"));
			inParamc.put("SERIAL_NUMBER", serialNumber);
			inParamc.put("END_TIME", SysDateMgr.getSysTime());
			CreateRedMemberQry.save(inParamc);
		}
		IDataset returnDataList = new DatasetList();
		IData returnData = new DataMap();
		returnData.put("ORDER_ID", SeqMgr.getOrderId());
		returnDataList.add(returnData);
		return returnDataList;
	}

	private IDataset resultConvert(String bizType, IDataset result) throws Exception
	{
		IData data = new DataMap();
		for (int i = 0; i < result.size(); i++)
		{
			data = result.getData(i);
			String str = data.getString("QRYRSLTLIST", "");
			if (str.indexOf("|") > 0)
			{
				String[] strs = str.split("\\|");
				for (int j = 0; j < strs.length; j++)
				{
					data.put("DATA" + (j + 1), strs[j]);
				}
			} else
			{
				data.put("DATA1", str);
			}
		}
		return result;
	}

	public IDataset saleActiveTerminalQry(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "DEVICE_MODEL_CODE");
		IDataUtil.chkParam(input, "CITY_CODE");
		String terminalModelCode = input.getString("DEVICE_MODEL_CODE");
		String cityCode = input.getString("CITY_CODE");

		return HwTerminalCall.saleActiveTerminalQry("IGetTerminalOccupyNum", "4", terminalModelCode, cityCode);
	}

	public IData saveUserAnswer(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "COUNT_FLG", "1"); // 回复次数
		IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 手机号码
		IDataUtil.chkParam(input, "REVERT_SMS_CONTENT"); // 回复内容
		IDataUtil.chkParam(input, "REPLY_NUMBER", "10086700"); // 回复号码

		getUserInfo4SingleAll(input.getString("SERIAL_NUMBER"));

		// 第一次回复 DAYS限制当天
		if ("1".equals(input.getString("COUNT_FLG", "1")))
		{
			ParamInfoQry.saveUserAnswer3(input.getString("REVERT_SMS_CONTENT"), input.getString("SERIAL_NUMBER"), input.getString("REPLY_NUMBER"), "0");
		}
		// 第二次回复
		else if ("2".equals(input.getString("COUNT_FLG", "")))
		{
			ParamInfoQry.saveUserAnswer4(input.getString("REVERT_SMS_CONTENT"), input.getString("SERIAL_NUMBER"), input.getString("REPLY_NUMBER"), "0");
		}

		return new DataMap();
	}

	/**
	 * 必备参数SERIAL_NUMBER,REMOVE_TAG,USER_PASSWD,NET_TYPE_CODE 返回CUST_NAME客户名称
	 * td_m_vipclass表中 CLASS_NAMEZW VIP等级 CLASS_NAME CLASS_NAME VIP CLASS_ID
	 * USER_SCORE 积分
	 */
	public IDataset searchCustInfo(IData data) throws Exception
	{

		IDataset resultSet = new DatasetList();
		IData result = new DataMap();
		String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
		String removeTag = IDataUtil.chkParam(data, "REMOVE_TAG", "0");
		String inputPasswd = IDataUtil.chkParam(data, "USER_PASSWD");
		String netTypeCode = IDataUtil.chkParam(data, "NET_TYPE_CODE", "00");

		// 取用户资料
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1131);
		}

		// 校验密码
		if (!PasswdMgr.checkUserPassword(inputPasswd, userInfo.getString("USER_ID"), userInfo.getString("USER_PASSWD")))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_401);
		}

		// 取客户资料
		IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
		if (IDataUtil.isEmpty(custInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_1134);
		}
		result.put("CUST_NAME", custInfo.getString("CUST_NAME"));

		String vipClassId = "";
		String vipTypeCode = "";
		String className = "";
		// 用户大客户资格处理 获取VIP等级
		IDataset custVipDataset = CustVipInfoQry.qryVipInfoByCustId(custInfo.getString("CUST_ID"));
		if (IDataUtil.isNotEmpty(custVipDataset))
		{
			vipClassId = custVipDataset.getData(0).getString("VIP_CLASS_ID");// VIP等级
			vipTypeCode = custVipDataset.getData(0).getString("VIP_TYPE_CODE");
			className = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipTypeCode, vipClassId);
		} else
		{
			className = "非大客户";
		}

		result.put("CLASS_NAME", vipClassId);
		result.put("CLASS_NAMEZW", className);

		String score = "";
		// 查用户积分
		IDataset scoreInfo = AcctCall.queryUserScore(userInfo.getString("USER_ID"));
		if (IDataUtil.isNotEmpty(scoreInfo))
		{
			score = scoreInfo.getData(0).getString("SUM_SCORE", "");
		}
		result.put("USER_SCORE", score);
		resultSet.add(result);

		return resultSet;
	}

	public IData SecSMSAffirm(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "FORCE_OBJECT");
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String forceObject = input.getString("FORCE_OBJECT", "");// 10086+3位端口号+tradeId后8位
		forceObject = forceObject.substring(8, forceObject.length());
		String serialNumber = input.getString("SERIAL_NUMBER", "");
		String noticeContent = input.getString("NOTICE_CONTENT", "");// 用户回复短信内容
		String rspCode = "01";// 给IBOSS的返回标识 00-用户回复授权 01- 用户返回不同意 02-超时
		String oprFlag = "0";// 操作标识，同EXEC_FLAG 0-未处理 1-用户回复授权 2-用户回复不同意 3-超时

		IDataset checkInfo = TwoCheckInfoQry.querySecCheckByPK(forceObject, serialNumber);

		if (IDataUtil.isNotEmpty(checkInfo))
		{
			IData checkData = checkInfo.getData(0);
			int execFlag = checkData.getInt("EXEC_FLAG");// 0-未处理 1-客户回复授权
															// 2-客户回复不同意 3=超时
			String revertDate = checkData.getString("REVERT_DATE");// 短信发给用户的时间
			if (StringUtils.isBlank(revertDate))
			{
				CSAppException.apperr(TradeException.CRM_TRADE_328);
			}

			revertDate = SysDateMgr.decodeTimestamp(revertDate, SysDateMgr.PATTERN_STAND);
			Date addTime = DateUtils.addMinutes(SysDateMgr.string2Date(revertDate, SysDateMgr.PATTERN_STAND), 30);
			String outDate = SysDateMgr.date2String(addTime, SysDateMgr.PATTERN_STAND);// 超过30分钟则超时
			String sysDate = SysDateMgr.getSysTime();

			switch (execFlag)
			{
			case 0:// 未处理
				if (sysDate.compareTo(outDate) > 0)// 已超时
				{
					rspCode = "02";
					oprFlag = "3";
				} else
				// 进行正常处理，调用IBOSS反馈接口
				{
					oprFlag = "2";
					if ("是".equals(noticeContent.trim()))
					{
						oprFlag = "1";
						rspCode = "00";
					}
				}
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			}

			if (0 == execFlag && !("3".equals(oprFlag)))
			{
				/* CRM侧二次确认表修改状态 */
				dealTWOCheck(forceObject, oprFlag, noticeContent);

				IData ibossParam = genIBossData(serialNumber);

				/* 将此前未处理的记录结果反馈给IBOSS */
				IDataset ret = IBossCall.sendSecCheckInfo(serialNumber, "01", checkData.getString("RSRV_STR1"), rspCode, ibossParam.getString("AREA_INFO"), ibossParam.getString("OPERATE_DATE"), ibossParam.getString("BRAND_CODE"), ibossParam.getString("PAY_TYPE"), ibossParam.getString("AVG_PAYED"), ibossParam.getString("FOREIGN_FLAG"), ibossParam.getString("GROUP_LEVEL"), ibossParam.getString("LEVEL"), ibossParam.getString("STATUS"), ibossParam.getString("USER_NAME"), ibossParam.getString("REAL_FLAG"), ibossParam.getString("ID_CARD_TYPE"), ibossParam.getString("ID_CARD_NUM"));

				if (IDataUtil.isEmpty(ret))
				{
					CSAppException.apperr(CrmUserException.CRM_USER_1141);
				} else
				{
					if (!"0000".equals(ret.getData(0).getString("X_RSPCODE")))
					{
						CSAppException.apperr(CrmUserException.CRM_USER_1141);
					}
				}
			}

		} else
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1140);
		}

		IData ret = new DataMap();

		return ret;
	}

	private void setOptionalElement(IData item, String oldProductId, String newProductId) throws Exception
	{
		String productChangeDate = null;
		IDataset productTrans = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);

		if (IDataUtil.isNotEmpty(productTrans))
		{
			IData productTran = productTrans.getData(0);
			String enableTag = productTran.getString("ENABLE_TAG");

			if (enableTag.equals("0"))
			{// 立即生效
				productChangeDate = SysDateMgr.getSysTime();
			} else if ((enableTag.equals("1")) || (enableTag.equals("2")))
			{// 下帐期生效

				productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
			} else if (enableTag.equals("3"))
			{
				// 按原产品的生效方效

				IData productInfo = UProductInfoQry.qryProductByPK(oldProductId);
				String enableTagOld = productInfo.getString("ENABLE_TAG");

				if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
				{// 立即生效
					productChangeDate = SysDateMgr.getSysTime();
				} else if (enableTagOld.equals("1"))
				{// 下帐期生效

					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			} else if (enableTag.equals("4"))
			{
				// 按新产品的生效方式

				IData productInfo = UProductInfoQry.qryProductByPK(newProductId);
				String enableTagNew = productInfo.getString("NEW_PRODUCT_ID");

				if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
				{// 立即生效
					productChangeDate = SysDateMgr.getSysTime();
				} else if (enableTagNew.equals("1"))
				{// 下帐期生效

					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
		}

		ProductTimeEnv env = new ProductTimeEnv();
		env.setBasicAbsoluteStartDate(productChangeDate);
		ProductModuleData forceElement = null;
		if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(item.getString("ELEMENT_TYPE_CODE")))
			forceElement = new SvcData(item);
		else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(item.getString("ELEMENT_TYPE_CODE")))
			forceElement = new DiscntData(item);

		String startDate = ProductModuleCalDate.calStartDate(forceElement, env);
		item.put("START_DATE", startDate);
		String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);
		item.put("END_DATE", endDate);
	}

	public IDataset smallAwardUserInfoAffirm(IData data) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = IDataUtil.chkParam(data, "ID_VALUE");
		/*
		 * QueryTradeBean bean = new QueryTradeBean(); setBaseBean(bean);
		 */
		IDataset result = new DatasetList();
		// CSAppEntity dao = new CSAppEntity(pd);
		IData resultData = new DataMap();
		IData param = new DataMap();

		// String kindId = data.getString("KIND_ID"); BIP2B082_T2040023_0_0
		// if(!"BIP2B082_T2040024_1_0".equals(data.getString("KIND_ID"))){
		if (!StringUtils.equals(data.getString("KIND_ID"), "BIP2B082_T2040024_1_0"))
		{
			resultData.put("X_RESULTCODE", "2106");
			resultData.put("X_RESULTINFO", "KIND_ID不正确！");
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			rets.add(resultData);
			return rets;
		}
		String SEQ = IDataUtil.chkParam(data, "SEQ");
		if (StringUtils.isBlank(SEQ))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "SEQ");
		}

		if (StringUtils.isNotBlank("ID_TYPE") || StringUtils.equals(data.getString("ID_TYPE"), "01"))
		{
			param.put("SERIAL_NUMBER", serialNumber);
		} else
		{
			resultData.put("X_RESULTCODE", "2107");
			resultData.put("X_RESULTINFO", "用户不存在，传值非法！");
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			rets.add(resultData);
			return rets;
		}
		result = UserPlatSvcInfoQry.queryUserPlatUiaInfo(serialNumber);
		if (result.isEmpty())
		{
			resultData.put("USER_STATE_CODESET", "99");
			resultData.put("X_RESULTCODE", "0925");
			resultData.put("X_RESULTINFO", "其他资源表错误");
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			rets.add(resultData);
			return rets;
		}
		IData userInfo = result.getData(0);

		// String tradeIdTmp = DualMgr.getSeqId(pd, "seq_trade_id");

		String tradeIdTmp = SeqMgr.getTradeId();
		String tradeId = tradeIdTmp.substring(tradeIdTmp.length() - 8, tradeIdTmp.length());
		// 端口号
		String port = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", "TYPE_ID", "DATA_ID", "PERSON_TWO_CHECK_FOR_BANK");

		try
		{
			IData params = new DataMap();
			params.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
			params.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
			params.put("SERIAL_NUMBER", data.getString("ID_VALUE"));
			params.put("USER_ID", userInfo.getString("USER_ID"));
			params.put("PRIORITY", "1000");
			params.put("STAFF_ID", data.getString("TRADE_STAFF_ID"));
			params.put("DEPART_ID", data.getString("TRADE_DEPART_ID"));
			params.put("REMARK", "小额授信资料查询确认短信");
			params.put("NOTICE_CONTENT", "您好，浦发银行联名卡业务需查询您在中国移动的相关信息。半小时内回复“是”同意，回复其他内容或不回复不同意，可能影响信用额度。中国移动");
			params.put("FORCE_OBJECT", "10086" + port + tradeId);

			Dao.executeUpdateByCodeCode("TI_O_SMS", "INS_SMSCO_CS", params);

			IData para = new DataMap();
			para.put("TRADE_ID", tradeId);
			para.put("SERIAL_NUMBER", data.getString("ID_VALUE"));
			para.put("REMARK", "浦发银行联名卡小额授信查询二次确认短信");
			para.put("RERV_STR1", SEQ);
			para.put("EXEC_FLAG", "0");

			Dao.executeUpdateByCodeCode("TF_B_TWO_CHECK", "INS_SMALL_AWARD_TWO_CHECK", para);

		} catch (Exception e)
		{
			String eMessage = e.getMessage();
			resultData.put("X_RESULTCODE", "2600");
			resultData.put("X_RESULTINFO", eMessage);
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			rets.add(resultData);
			return rets;
		}
		resultData.put("X_RESULTCODE", "0");
		resultData.put("X_RESULTINFO", "成功");
		rets.add(resultData);
		return rets;
	}

	// SEL_PLATSVC_ATTR_BY_USERID
	// UserDiscntInfoQry.getDiscnt4ActByUserId2
	// UserDiscntInfoQry.getDiscnt4ActByUserId1
	public IData unifiedAuthentication(IData data) throws Exception
	{
		IData result = new DataMap();

		String kindId = data.getString("KIND_ID");
		String idType = data.getString("IDTYPE", "01");
		String serialNumber = "";
		if ("01".equals(idType))
		{
			serialNumber = data.getString("IDVALUE");
		} else
		{
			result.put("X_RESULTCODE", "0925");
			result.put("X_RESULTINFO", "用户不存在，传值非法！");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			return result;
		}

		IDataset dataset = UserPlatSvcInfoQry.queryUiaBySerialNumber(serialNumber);
		if (IDataUtil.isEmpty(dataset))
		{
			if (StringUtils.equals("BIP3A105_T3000003_1_0", kindId))
			{
				result.put("USER_STATE_CODESET", "99");
				result.put("X_RESULTCODE", "0925");
				result.put("X_RESULTINFO", "手机号对应的用户不存在或该号码是副号码");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				return result;
			}
			if (StringUtils.equals("BIP3A100_T3000001_1_0", kindId))
			{// 无线音乐销户鉴权特殊处理
				result.put("USER_STATE_CODESET", "99");
				result.put("BRAND_CODE", "03");
				result.put("STATUS_CHG_TIME", "20080517163419");
				result.put("X_RESULTCODE", "0925");
				result.put("X_RESULTINFO", "用户不存在");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				return result;
			}
			result.put("USER_STATE_CODESET", "99");
			result.put("X_RESULTCODE", "0925");
			result.put("X_RESULTINFO", "用户不存在");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			return result;
		}

		IData userInfo = dataset.getData(0);
		// String cityName = StaticUtil.getStaticValue(getVisit(),
		// "TD_M_CUSTCITY", "CITY_CODE", "CITY_NAME",
		// userInfo.getString("CITY_CODE"));
		// userInfo.put("CITY_NAME", cityName);

		String userId = userInfo.getString("USER_ID");
		IDataset vipInfo = UserPlatSvcInfoQry.queryUiaVipInfoByUserId(userId);
		if (IDataUtil.isNotEmpty(vipInfo))
		{
			userInfo.putAll(vipInfo.getData(0));
		}

		// 密码校验 优先于 其他 业务查询判断
		String passwd = data.getString("PASSWD");
		if (StringUtils.isNotBlank(passwd))
		{
			String password = PasswdMgr.encryptPassWD(passwd, userId);
			String inpass = userInfo.getString("USER_PASSWD", "");
			if (!password.equals(inpass))
			{
				result.put("X_RESULTCODE", "0901");
				result.put("X_RESULTINFO", "密码不正确");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				return result;
			}
		}

		// 用户销户或停机 状态校验
		String removeTag = userInfo.getString("REMOVE_TAG");
		String stateCodeSet = userInfo.getString("USER_STATE_CODESET");
		result.put("STATUS_CHG_TIME", userInfo.getString("STATUS_CHG_TIME"));
		if ("1".equals(removeTag) || "3".equals(removeTag))
		{
			result.put("USER_STATE_CODESET", stateCodeSet);
			result.put("X_RESULTCODE", "0923");
			result.put("X_RESULTINFO", "用户预销户");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			return result;
		} else if (!"0".equals(removeTag))
		{
			result.put("USER_STATE_CODESET", stateCodeSet);
			result.put("X_RESULTCODE", "0924");
			result.put("X_RESULTINFO", "用户销户");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			return result;
		}

		String acct_tag = userInfo.getString("ACCT_TAG");
		// 手机支付平台
		if (kindId.equals("BIP3A105_T3000003_1_0"))
		{
			if (!StringUtils.equals("0", acct_tag))
			{
				result.put("USER_STATE_CODESET", "99");
				result.put("X_RESULTCODE", "2998");
				result.put("X_RESULTINFO", "用户状态未激活");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				return result;
			}
			String brand = userInfo.getString("BRAND_CODE");
			if (StringUtils.equals(brand, "00"))
			{
				userInfo.put("BRAND_CODE", "01");
			}
			if (StringUtils.equals(brand, "01"))
			{
				userInfo.put("BRAND_CODE", "02");
			}
			if (StringUtils.equals(brand, "02"))
			{
				userInfo.put("BRAND_CODE", "03");
			}
			if (StringUtils.equals(brand, "03"))
			{
				userInfo.put("BRAND_CODE", "09");
			}

			IDataset attrResult = new DatasetList();
			IData data101 = new DataMap();
			data101.put("INFO_CODE", "101");
			data101.put("INFO_VALUE", encryptMode(userInfo.getString("CUST_NAME", "")));
			attrResult.add(data101);

			IData data102 = new DataMap();
			data102.put("INFO_CODE", "102");
			data102.put("INFO_VALUE", userInfo.getString("SEX", ""));
			attrResult.add(data102);

			IData data103 = new DataMap();
			data103.put("INFO_CODE", "103");
			data103.put("INFO_VALUE", userInfo.getString("PSPT_TYPE_CODE", ""));
			attrResult.add(data103);

			IData data104 = new DataMap();
			data104.put("INFO_CODE", "104");
			data104.put("INFO_VALUE", encryptMode(userInfo.getString("PSPT_ID", "")));
			attrResult.add(data104);

			IData data126 = new DataMap();
			data126.put("INFO_CODE", "126");
			data126.put("INFO_VALUE", userInfo.getString("CITY_NAME", ""));// 获取归属地名称
			attrResult.add(data126);

			IData data170 = new DataMap();
			data170.put("INFO_CODE", "170");
			data170.put("INFO_VALUE", userInfo.getString("IMSI", ""));
			attrResult.add(data170);
			userInfo.putAll(attrResult.toData());
		}

		// DSMP 业务 开关状态 的 判断
		if (kindId.equals("BIP6B641_T6000603_1_0"))
		{
			String service_id = "";
			String re_biz_type_code = "";
			if (data.getString("BIZ_TYPE_CODE").equals("03"))
			{
				service_id = "98008003";
				re_biz_type_code = "103";
			}
			if (data.getString("BIZ_TYPE_CODE").equals("04"))
			{
				service_id = "98008004";
				re_biz_type_code = "104";
			}
			if (data.getString("BIZ_TYPE_CODE").equals("05"))
			{
				service_id = "98008005";
				re_biz_type_code = "105";
			}
			if (data.getString("BIZ_TYPE_CODE").equals("13"))
			{
				service_id = "98008013";
				re_biz_type_code = "113";
			}
			if (data.getString("BIZ_TYPE_CODE").equals("99"))
			{
				service_id = "98009044";
				re_biz_type_code = "199";
			}
			IDataset switchInfos = UserPlatSvcInfoQry.querySvcInfoByUserIdAndSvcId(userId, service_id);
			IData switchInfo = new DataMap();
			if (switchInfos.isEmpty())
			{
				switchInfo.put("INFO_CODE", re_biz_type_code);
				switchInfo.put("INFO_VALUE", "0000");
			} else
			{
				switchInfo.put("INFO_CODE", re_biz_type_code);
				switchInfo.put("INFO_VALUE", "1111");
			}
			userInfo.putAll(switchInfo);
		}

		result = userInfo;
		if (kindId.equals("BIP3A105_T3000003_1_0"))
		{// 手机支付鉴权停机状态可查询

		} else if (kindId.equals("BIP3A102_T3000001_1_0"))
		{// 通信账户支付及手机银行卡业务注册鉴权
			// 查询出来的结果：00－全球通 01－神州行 02－动感地带 03－其它省内品牌
			// 00－全球通 01－神州行 02－动感地带 03－其它省内品牌 04-BOSS侧神州行用户
		} else
		{
			if (stateCodeSet.equals("01"))
			{
				result.put("X_RESULTCODE", "0921");
				result.put("X_RESULTINFO", "用户已单向停机");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				return result;
			}
			if (stateCodeSet.equals("02"))
			{
				result.put("X_RESULTCODE", "0922");
				result.put("X_RESULTINFO", "用户已停机");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				return result;
			}
			if (stateCodeSet.equals("03"))
			{
				result.put("X_RESULTCODE", "0923");
				result.put("X_RESULTINFO", "用户预销户");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				return result;
			}
		}
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "成功");
		return result;
	}

	public IDataset userVoucherEnter(IData input) throws Exception
	{
		IDataset rets = new DatasetList();
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		String idType = IDataUtil.chkParam(input, "IDTYPE");
		if (StringUtils.isBlank(idType))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDTYPE");
		}

		String idItemRange = IDataUtil.chkParam(input, "IDITEMRANGE");
		if (StringUtils.isBlank(idItemRange))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDITEMRANGE");
		}

		String authType = IDataUtil.chkParam(input, "AUTHTYPE");
		if (StringUtils.isBlank(authType))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "AUTHTYPE");
		}

		String sessionId = IDataUtil.chkParam(input, "SESSIONID");
		if (StringUtils.isBlank(sessionId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "SESSIONID");
		}

		String oprnumb = IDataUtil.chkParam(input, "OPRNUMB");
		if (StringUtils.isBlank(oprnumb))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "OPRNUMB");
		}

		String ccpasswd = input.getString("CCPASSWD", "");
		if (false == StringUtils.equals(authType, "02") && StringUtils.isBlank(ccpasswd))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "CCPASSWD");
		}

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		String userId = userInfo.getString("USER_ID");
		String custId = userInfo.getString("CUST_ID");

		// if(false == UserInfoQry.checkUserPassword(userId, ccpasswd))
		if (false == PasswdMgr.checkUserPassword(ccpasswd, userId, userInfo.getString("USER_PASSWD")) && !"".equals(ccpasswd))
		// if (false == PasswdMgr.checkUserPassword(ccpasswd, userId,
		// userInfo.getString("USER_PASSWD")))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_401);
		}

		IDataset userProducts = UserProductInfoQry.queryMainProductNow(userId);
		if (IDataUtil.isEmpty(userProducts))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_224);
		}
		String brandCode = userProducts.getData(0).getString("BRAND_CODE");
		// TODO: QAM_USERSCORE，获取返回值SCORE
		String scoreValue = "0";
		String allNewBalance = AcctCall.getOweFeeByUserId(userId).getString("ACCT_BALANCE", "0");
		String custContactId = null;
		String custContactTraceId = SeqMgr.getCustContactTrace();
		String custContactTraceIdNew = custContactTraceId.substring(custContactTraceId.length() - 8);
		IDataset custContactInfos = CustContactInfoQry.queryCustInfoByCustIdAndSessionId(custId, sessionId);
		if (IDataUtil.isEmpty(custContactInfos))
		{
			custContactId = SeqMgr.getCustContact();
		} else
		{
			custContactId = custContactInfos.getData(0).getString("CUST_CONTACT_ID");
		}

		String curday = SysDateMgr.date2String(java.util.Calendar.getInstance().getTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD);

		String startDate = SysDateMgr.getSysTime();
		String endDate = SysDateMgr.getOtherSecondsOfSysDate(30 * 60);
		String credenceNo = "aaaaabbbbbcccccd" + curday + custContactTraceIdNew;

		WapSessionInfoQry.insertData(sessionId, custContactId, idType, serialNumber, "6", credenceNo, startDate, endDate, authType);

		// 重新格式化时间输出
		String startDateFormat = startDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String endDateFormat = endDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

		IData ret0 = new DataMap();
		ret0.put("IDENTCODE", credenceNo);
		ret0.put("REGISTTIME", startDateFormat);
		ret0.put("IDENTUNEFFT", endDateFormat);
		ret0.put("AUTHGRADE", authType);
		ret0.put("BRAND", InterfaceUtil.convertBrandCode(brandCode));
		ret0.put("POINTBALANCE", scoreValue);
		ret0.put("BALANCE", allNewBalance);
		rets.add(ret0);
		return rets;
	}

	public IDataset userVoucherOut(IData input) throws Exception
	{
		IDataset rets = new DatasetList();

		String idType = IDataUtil.chkParam(input, "IDTYPE");
		if (StringUtils.isBlank(idType))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDTYPE");
		}

		String idItemRang = IDataUtil.chkParam(input, "IDITEMRANGE");
		if (StringUtils.isBlank(idItemRang))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDITEMRANGE");
		}

		String idEntCode = IDataUtil.chkParam(input, "IDENTCODE");
		if (StringUtils.isBlank(idItemRang))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "IDENTCODE");
		}

		String sessionId = IDataUtil.chkParam(input, "SESSIONID");
		if (StringUtils.isBlank(idItemRang))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "SESSIONID");
		}

		String oprNumb = IDataUtil.chkParam(input, "OPRNUMB");
		if (StringUtils.isBlank(idItemRang))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "OPRNUMB");
		}

		IData ret0 = new DataMap();
		ret0.put("ID_TYPE", idType);
		ret0.put("SERIAL_NUMBER", idItemRang);
		ret0.put("CREDENCE_NO", idEntCode);
		ret0.put("SESSION_ID", sessionId);

		int i = WapSessionInfoQry.updateDataByOut(sessionId, idType, idItemRang, idEntCode);
		/*
		 * if (i <= 0) { CSAppException.apperr(WapException.CRM_WAP_700006); }
		 */
		rets.add(ret0);
		return rets;
	}

	private void validParams(IData data) throws Exception
	{
		// 通用参数
		IDataUtil.chkParam(data, "IN_MODE_CODE");
		IDataUtil.chkParam(data, "KIND_ID");
		IDataUtil.chkParam(data, "IDTYPE");
		IDataUtil.chkParam(data, "IDVALUE");
		IDataUtil.chkParam(data, "IDENT_CODE");
		IDataUtil.chkParam(data, "BIZ_TYPE_CODE");
		IDataUtil.chkParam(data, "TRADE_STAFF_ID");
		IDataUtil.chkParam(data, "TRADE_DEPART_ID");
		IDataUtil.chkParam(data, "TRADE_CITY_CODE");
		data.put("SERIAL_NUMBER", data.getString("IDVALUE", ""));
	}

	/*******************************************************************
	 * 移动商城：查询用户服务、增值服务信息
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset qryUserBizInfo4ScoreMall(IData data) throws Exception
	{
		// 1.参数校验
		checkParameters(data);
		// 2.查询用户信息
		String serialNumber = data.getString("SERIAL_NUMBER");
		IData uca = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(uca))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}

		String userID = uca.getString("USER_ID");
		String productType = data.getString("PRODUCT_TYPE");
		// 3.查询用户服务、增值服务信息
		IDataset results = new DatasetList();
		IData result=new DataMap();
		if ("01".equals(productType)){
			result = queryUserSvcInfo(userID, data.getString("SERVICE_ID"));
			if(!"0".equals(result.getString("STATE"))){
	             result = queryUserProdInfo(userID, data.getString("SERVICE_ID"));
	         }
	         if(!"0".equals(result.getString("STATE"))){
	             result = queryUserDiscntInfo(userID, data.getString("SERVICE_ID"));
	         }
	         if(!"0".equals(result.getString("STATE"))){
	        	 result =querySaleActiveByUserIdPrdId(userID, data.getString("SERVICE_ID")) ;
	         }
	         results.add(result);	
		}else if ("02".equals(productType))
			results.add(queryUserPlatSvcInfo(userID, data.getString("SP_CODE"), data.getString("BIZ_CODE")));

		return results;
	}

	/**************************************************************************************************
	 * 参数校验<BR/>
	 * 参数校验如下：
	 * 用户手机号码[SERIAL_NUMBER]、操作员工[TRADE_STAFF_ID]、操作员工部门[TRADE_DEPART_ID]、
	 * 交易地州[TRADE_CITY_CODE]、接入方式[IN_MODE_CODE] 其中：产品类型[PRODUCT_TYPE：01-产品校验
	 * 02-增值服务校验] 1.PRODUCT_TYPE为产品校验[01]时，需要校验：服务编码[SERVICE_ID]
	 * 2.PRODUCT_TYPE为增值服务校验[02]时，需要校验：企业编码[SP_CODE]、业务编码[BIZ_CODE]、
	 * 
	 * @param data
	 * @throws Exception
	 */
	private void checkParameters(IData data) throws Exception
	{
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "PRODUCT_TYPE");
		IDataUtil.chkParam(data, "TRADE_STAFF_ID");
		IDataUtil.chkParam(data, "TRADE_DEPART_ID");
		IDataUtil.chkParam(data, "TRADE_CITY_CODE");
		IDataUtil.chkParam(data, "IN_MODE_CODE");

		String productType = data.getString("PRODUCT_TYPE");

		if ("01".equals(productType))
		{
			IDataUtil.chkParam(data, "SERVICE_ID");

		} else if ("02".equals(productType))
		{
			IDataUtil.chkParam(data, "SP_CODE");
			IDataUtil.chkParam(data, "BIZ_CODE");

		} else
		{
			CSAppException.apperr(CrmUserException.CRM_USER_863);
		}
	}

	/*******************************************************************************************
	 * 将日期date从格式srcPattern转换为格式desPattern <BR/>
	 * 
	 * @param dateStr
	 *            源日期
	 * @param srcPattern
	 *            源日期格式
	 * @param desPattern
	 *            转换后的日期格式
	 * @return
	 * @throws Exception
	 */
	private static String transDate(String dateStr, String srcPattern, String desPattern) throws Exception
	{
		// 日期格式化模板
		Date date = DateUtils.parseDate(dateStr, new String[]
		{ srcPattern });

		FastDateFormat format = FastDateFormat.getInstance(desPattern);
		return format.format(date);
	}

	/**********************************************************************************************
	 * 根据用户标志[USER_ID]、服务编码[SERVICE_ID]查询用户产品信息<BR/>
	 * 返回结果：结构[MAP]
	 * 	SERVICE_ID：产品编码
	 *  STATE：服务状态[0：开通 1：未开通]
	 * @param userID
	 * @param serviceID
	 * @return
	 * @throws Exception
	 */
	private IData queryUserProdInfo(String userID, String serviceID)throws Exception{
		//此处需要处理：SERVICE_ID以P+SERVICE_ID的方式传入
		String qrySvcID =serviceID;
		if("D".equals(serviceID.substring(0, 1))||"S".equals(serviceID.substring(0, 1))||
		  "P".equals(serviceID.substring(0, 1))|| "A".equals(serviceID.substring(0, 1))){
			qrySvcID=serviceID.substring(1);	
		}	
		
		//查一把家庭网
		IData familyInfo=queryFamilyProductInfo(userID,qrySvcID);
		if(IDataUtil.isNotEmpty(familyInfo)){
			return familyInfo;
		}
		//1.查询用户是否订购指定服务信息
		IDataset proInfos = UserProductInfoQry.getUserProductByUserIdProductId(userID, qrySvcID);
		IData proInfo = proInfos.isEmpty() ? null : proInfos.getData(0);//以后需求可能会用到其中字段，所以取出信息
		
		boolean isEmpty = proInfo == null || proInfo.isEmpty();
		String resultInfo = "查询成功！", resultCode = "0";
		//没有订购指定服务
		if(isEmpty){
			IData res=new DataMap();
			try{
              res=UProductInfoQry.qryProductByPK(qrySvcID);
			}catch(Exception e){
			  res=null;
			}
				if(IDataUtil.isEmpty(res)){//用户未订购该服务
				resultCode = "4010";
				resultInfo = "查询失败：该产品编码错误！";
				String rspCode = "4010";
				String rspType = "2";
				IData result = new DataMap();
				result.put("SERVICE_ID", serviceID);
				if(isEmpty)
					result.put("STATE", "1");
				else
					result.put("STATE", "0");
				result.put("X_RESULTCODE", resultCode);
				result.put("X_RESULTINFO", resultInfo);
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				result.put("X_RSPCODE", rspCode);
				result.put("X_RSPTYPE", rspType);
				return result;
				
			}else{//服务编码错误
				resultInfo = "用户未订购该产品！";
			}
		}
		
		IData result = new DataMap();
		result.put("SERVICE_ID", serviceID);
		if(isEmpty)
			result.put("STATE", "1");
		else
			result.put("STATE", "0");
		result.put("X_RESULTCODE", resultCode);
		result.put("X_RESULTINFO", resultInfo);
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		
		return result;
	}
	private IData queryFamilyProductInfo(String userID, String serviceID)throws Exception{
		IData result=new DataMap();
		String resultInfo = "查询成功！", resultCode = "0";
		String merchRelationTypeCode ="45";//UProductCompInfoQry.getRelationTypeCodeByProductId(serviceID);
		IDataset infos = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(userID,merchRelationTypeCode);			
		if(IDataUtil.isNotEmpty(infos)){
			IData info_new = infos.getData(0);
			String user_id_a = info_new.getString("USER_ID_A");
			IDataset userSvcInfos = UserProductInfoQry.getUserProductByUserIdProductId(user_id_a, serviceID);
	        if (IDataUtil.isNotEmpty(userSvcInfos))
	        {
	            result.put("SERVICE_ID", serviceID);
	            result.put("STATE", "0");
	            result.put("X_RESULTCODE", resultCode);
	            result.put("X_RESULTINFO", resultInfo);
	            result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	           
	        }
		}
		 return result;

	}
	 /**********************************************************************************************
	 * 根据用户标志[USER_ID]、服务编码[SERVICE_ID]查询用户优惠信息<BR/>
	 * 返回结果：结构[MAP]
	 * 	SERVICE_ID：服务编码
	 *  STATE：服务状态[0：开通 1：未开通]
	 * @param userID
	 * @param serviceID
	 * @return
	 * @throws Exception
	 */
	private IData queryUserDiscntInfo(String userID, String serviceID)throws Exception{
		//此处需要处理：SERVICE_ID以D+SERVICE_ID的方式传入
		String qrySvcID =serviceID;
		if("D".equals(serviceID.substring(0, 1))||"S".equals(serviceID.substring(0, 1))||
		  "P".equals(serviceID.substring(0, 1))|| "A".equals(serviceID.substring(0, 1))){
			qrySvcID=serviceID.substring(1);	
		}	
		//1.查询用户是否订购指定服务信息
		IDataset discntInfoList =  UserDiscntInfoQry.getAllDiscntByUserId(userID, qrySvcID);
		IData discntInfo = discntInfoList.isEmpty() ? null : discntInfoList.getData(0);//以后需求可能会用到其中字段，所以取出信息
		
		boolean isEmpty = discntInfo == null || discntInfo.isEmpty();
		String resultInfo = "查询成功！", resultCode = "0";
		//没有订购指定服务
		if(isEmpty){
			IData res=new DataMap();
			try{
			res=UDiscntInfoQry.getDiscntInfoByPk(qrySvcID);
			}catch(Exception e){
				res=null;	
			}
				if(IDataUtil.isEmpty(res)){//用户未订购该优惠
				resultCode = "4010";
				resultInfo = "查询失败：该产品编码错误！";
				String rspCode = "4010";
				String rspType = "2";
				IData result = new DataMap();
				result.put("SERVICE_ID", serviceID);
				if(isEmpty)
					result.put("STATE", "1");
				else
					result.put("STATE", "0");
				result.put("X_RESULTCODE", resultCode);
				result.put("X_RESULTINFO", resultInfo);
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				result.put("X_RSPCODE", rspCode);
				result.put("X_RSPTYPE", rspType);
				return result;
				
			}else{//服务编码错误
				resultInfo = "用户未订购该产品！";
			}
		}
		
		IData result = new DataMap();
		result.put("SERVICE_ID", serviceID);
		if(isEmpty)
			result.put("STATE", "1");
		else
			result.put("STATE", "0");
		result.put("X_RESULTCODE", resultCode);
		result.put("X_RESULTINFO", resultInfo);
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		
		return result;
	}
	 /**********************************************************************************************
	 * 根据用户标志[USER_ID]、服务编码[SERVICE_ID]查询用户服务信息<BR/>
	 * 返回结果：结构[MAP] SERVICE_ID：服务编码 STATE：服务状态[0：开通 1：未开通]
	 * 
	 * @param userID
	 * @param serviceID
	 * @return
	 * @throws Exception
	 */
	private IData queryUserSvcInfo(String userID, String serviceID) throws Exception
	{
		// 此处需要处理：SERVICE_ID以S+SERVICE_ID的方式传入
		String qrySvcID =serviceID;
		if("D".equals(serviceID.substring(0, 1))||"S".equals(serviceID.substring(0, 1))||
		  "P".equals(serviceID.substring(0, 1))|| "A".equals(serviceID.substring(0, 1))){
			qrySvcID=serviceID.substring(1);	
		}		
		// 1.查询用户是否订购指定服务信息
		IDataset svcInfoList = UserSvcInfoQry.queryUserSvcByUseridSvcid(userID, qrySvcID);
		IData svcInfo = svcInfoList.isEmpty() ? null : svcInfoList.getData(0);// 以后需求可能会用到其中字段，所以取出信息

		boolean isEmpty = svcInfo == null || svcInfo.isEmpty();
		String resultInfo = "查询成功！", resultCode = "0";
		// 没有订购指定服务
		if (isEmpty)
		{
			IData res =new DataMap();
			try{
			res= USvcInfoQry.qryServInfoBySvcId(qrySvcID);
			}catch(Exception e){
			res=null;
			}
			if (IDataUtil.isEmpty(res))
			{// 用户未订购该服务
				resultCode = "4010";
				resultInfo = "查询失败：该服务编码错误！";
				String rspCode = "4010";
				String rspType = "2";
				IData result = new DataMap();
				result.put("SERVICE_ID", serviceID);
				if (isEmpty)
					result.put("STATE", "1");
				else
					result.put("STATE", "0");
				result.put("X_RESULTCODE", resultCode);
				result.put("X_RESULTINFO", resultInfo);
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				result.put("X_RSPCODE", rspCode);
				result.put("X_RSPTYPE", rspType);
				return result;

			} else
			{// 服务编码错误
				resultInfo = "用户未订购该服务！";
			}
		}

		IData result = new DataMap();
		result.put("SERVICE_ID", serviceID);
		if (isEmpty)
			result.put("STATE", "1");
		else
			result.put("STATE", "0");
		result.put("X_RESULTCODE", resultCode);
		result.put("X_RESULTINFO", resultInfo);
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

		return result;
	}
	 /**********************************************************************************************
	 * 根据用户标志[USER_ID]、服务编码[SERVICE_ID]查询用户优惠信息<BR/>
	 * 返回结果：结构[MAP]
	 * 	SERVICE_ID：服务编码
	 *  STATE：服务状态[0：开通 1：未开通]
	 * @param userID
	 * @param serviceID
	 * @return
	 * @throws Exception
	 */
	private IData querySaleActiveByUserIdPrdId(String userID, String serviceID)throws Exception{
		//此处需要处理：SERVICE_ID以D+SERVICE_ID的方式传入
		String qrySvcID =serviceID;
		if("D".equals(serviceID.substring(0, 1))||"S".equals(serviceID.substring(0, 1))||
		  "P".equals(serviceID.substring(0, 1))|| "A".equals(serviceID.substring(0, 1))){
			qrySvcID=serviceID.substring(1);	
		}	
		//1.查询用户是否订购指定服务信息
		IDataset activeInfoList =  UserSaleActiveInfoQry.querySaleActiveByUserIdPrdId(userID, qrySvcID);
		IData activeInfo = activeInfoList.isEmpty() ? null : activeInfoList.getData(0);//以后需求可能会用到其中字段，所以取出信息
		
		boolean isEmpty = activeInfo == null || activeInfo.isEmpty();
		String resultInfo = "查询成功！", resultCode = "0";
		//没有订购指定服务		
		IData result = new DataMap();
		result.put("SERVICE_ID", serviceID);
		if(isEmpty){
			result.put("X_RSPCODE", "4010");
			result.put("X_RSPTYPE", "2");
			result.put("STATE", "1");
			resultCode = "4010";
			resultInfo = "用户未订购该产品！";
		}else{
			result.put("STATE", "0");
		}
		result.put("X_RESULTCODE", resultCode);
		result.put("X_RESULTINFO", resultInfo);
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		
		return result;
	}
	

	/************************************************************************************************
	 * 根据用户标识[USER_ID]、企业编码[SP_CODE]、企业业务代码[BIZ_CODE]查询平台业务信息<BR/>
	 * 返回结果：结构[MAP] SP_CODE：企业编码 BIZ_CODE：企业业务代码 STATE：服务状态[0：开通 1：未开通]
	 * 
	 * @param userID
	 * @param spCode
	 * @param bizCode
	 * @return
	 * @throws Exception
	 */
	private IData queryUserPlatSvcInfo(String userID, String spCode, String bizCode) throws Exception
	{
		// 此处需要处理BIZ_CODE以BIZ_CODE|BIZ_TYPE_CODE的传入方式
		int index = bizCode.indexOf("|");
		String qryBizCode = index == -1 ? bizCode : bizCode.substring(0, index);
		// 1.查询用户是否订购指定SP业务
		IDataset platsvcInfos = UserPlatSvcInfoQry.queryUserPlatSvcInfo(userID, spCode, qryBizCode);// modify
																									// by
																									// duhj

		String state = "1";
		if (platsvcInfos != null && !platsvcInfos.isEmpty())
			if (!"E".equals(platsvcInfos.getData(0).getString("BIZ_STATE_CODE")))// 除了“退订”外，其他都为已订购
				state = "0";
		/*
		 * 当ProductType为02时,输入用户未订购的SPID和BizCode,确认ProStatus字段返回1(未开通) 不限
		 * X_RESULTCODE ： 0
		 * 当ProductType为02时,输入用户订购的SPID和BizCode,确认ProStatus字段返回0(开通) 不限
		 * X_RESULTCODE ： 0 当ProductType为02时,输入不存在的SPID 不限 X_RESULTCODE ： 4006
		 * 当ProductType为02时,输入存在的SPID,但是BizCode不存在 不限 X_RESULTCODE ： 4007
		 */
		String rspCode = "0", resInfo = "查询成功！";
		//
		boolean isEmpty = platsvcInfos == null || platsvcInfos.isEmpty();
		if (isEmpty)
		{
			// if(SpInfoQry.querySpInfosBySpcodeSpstatus(spCode).isEmpty()){//查询企业信息
			if (UpcCall.querySpInfoNameByCond(spCode).isEmpty())
			{// 查询企业信息 modify by duhj
				rspCode = "4006";
				resInfo = "查询失败：企业代码错误！";
				String rspType = "2";
				IData result = new DataMap();
				result.put("SP_CODE", spCode);
				result.put("BIZ_CODE", bizCode);
				if (isEmpty)
					result.put("STATE", "1");
				else
					result.put("STATE", "0");
				result.put("X_RESULTCODE", rspCode);
				result.put("X_RESULTINFO", resInfo);
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				result.put("X_RSPCODE", rspCode);
				result.put("X_RSPTYPE", rspType);
				return result;
			} else if (SpInfoQry.querySpBizInfo(spCode, qryBizCode).isEmpty())
			{// 查询企业业务信息
				rspCode = "4007";
				String rspType = "2";
				resInfo = "查询失败：企业代码正常，但企业业务编码错误！";
				IData result = new DataMap();
				result.put("SP_CODE", spCode);
				result.put("BIZ_CODE", bizCode);
				if (isEmpty)
					result.put("STATE", "1");
				else
					result.put("STATE", "0");
				result.put("X_RESULTCODE", rspCode);
				result.put("X_RESULTINFO", resInfo);
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				result.put("X_RSPCODE", rspCode);
				result.put("X_RSPTYPE", rspType);
				return result;
			}
		}
		IData result = new DataMap();
		result.put("SP_CODE", spCode);
		result.put("BIZ_CODE", bizCode);
		if (isEmpty)
			result.put("STATE", "1");
		else
			result.put("STATE", "0");
		result.put("X_RESULTCODE", rspCode);
		result.put("X_RESULTINFO", resInfo);
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

		return result;
	}

	/**
	 * 获取产品元素信息
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset qryWidenetProductElements(IData data) throws Exception
	{
		IDataUtil.chkParam(data, "PRODUCT_ID");
		String productId = data.getString("PRODUCT_ID");

		return UpcCall.queryAllOfferEnablesByOfferIdAndRelType(productId, "2");
	}

	public IData qryWideUserInfo(IData data) throws Exception
	{
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		data.put("SERIAL_NUMBER", "KD_" + data.getString("SERIAL_NUMBER"));
		IDataset userinfo = new DatasetList();
		userinfo = UserInfoQry.getWideUserInfoBySN(data);
		if (userinfo == null || userinfo.size() == 0)
			CSAppException.apperr(CrmCommException.CRM_COMM_159);
		return userinfo.getData(0);
		// CSAppCall.call("CS.GetInfosSVC.getUCAInfos", param);
	}

	/**
	 * 新大陆-用户原卡信息查询接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wujy3
	 */
	public IData getSimcardInfoBySn(IData input) throws Exception
	{
		String sn = input.getString("SERIAL_NUMBER");

		if (StringUtils.isEmpty(sn))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "SERIAL_NUMBER");
		}

		IData result = new DataMap();

		UcaData ucaData = UcaDataFactory.getNormalUca(sn);
		UserTradeData userInfo = ucaData.getUser();
		CustPersonTradeData custPersonInfo = ucaData.getCustPerson();
		List<ResTradeData> resInfos = ucaData.getUserAllRes();

		String simCardNo = "";
		for (ResTradeData resInfo : resInfos)
		{
			if ("1".equals(resInfo.getResTypeCode()))
				simCardNo = resInfo.getResCode();
		}

		IDataset simCardInfos = ResCall.getSimCardInfo("0", simCardNo, "", "1");

		String PsptTypeCode = StaticUtil.getStaticValueDataSource(getVisit(), "cen1", "TD_S_PASSPORTTYPE", "PSPT_TYPE_CODE", "PSPT_TYPE", custPersonInfo.getPsptTypeCode());
		result.put("PSPT_TYPE_CODE", PsptTypeCode);
		result.put("PSPT_ID", custPersonInfo.getPsptId());
		result.put("PSPT_ADDR", custPersonInfo.getPsptAddr());
		result.put("OPEN_MODE", userInfo.getOpenMode());
		result.put("OPEN_DATE", userInfo.getOpenDate());
		String ResTypeCode = StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", simCardInfos.getData(0).getString("RES_TYPE_CODE"));
		result.put("RES_TYPE_CODE", ResTypeCode);
		return result;
	}

	/**
	 * 新大陆-查询卡费信息接口
	 * 
	 * @params PageData,TradeData
	 * @return IData
	 * @exception
	 */
	public IData querySimCardPrice(IData input) throws Exception
	{
		String sn = input.getString("SERIAL_NUMBER");
		String sim_card_no = input.getString("SIM_CARD_NO");
		if (StringUtils.isEmpty(sn))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "SERIAL_NUMBER");
		}

		if (StringUtils.isEmpty(sim_card_no))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_913, "SIM_CARD_NO");
		}

		UcaData ucaData = UcaDataFactory.getNormalUca(sn);
		UserTradeData userInfo = ucaData.getUser();
		List<ResTradeData> resInfos = ucaData.getUserAllRes();

		String simCardNo = "";
		for (ResTradeData resInfo : resInfos)
		{
			if ("1".equals(resInfo.getResTypeCode()))
				simCardNo = resInfo.getResCode();
		}

		String oldSimCardNo = simCardNo;
		String newSimCardNo = input.getString("SIM_CARD_NO");
		IDataset simCardInfos = ResCall.getSimCardInfo("0", newSimCardNo, "", "0");
		String newImsi = simCardInfos.getData(0).getString("IMSI");
		String newResTypeCode = simCardInfos.getData(0).getString("RES_TYPE_CODE");
		String ResTypeCode = StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "TD_S_RESTYPE", "RES_TYPE_CODE", "RES_TYPE_NAME", simCardInfos.getData(0).getString("RES_TYPE_CODE"));
		String newSimOPC = simCardInfos.getData(0).getString("OPC");
		IDataset newUsim4G = ResCall.qrySimCardTypeByTypeCode(newResTypeCode);
		if (StringUtils.isNotEmpty(newSimOPC) && "01".equals(newUsim4G.getData(0).getString("NET_TYPE_CODE")))
		{
			IDataset userSvc = UserSvcInfoQry.getSvcUserId(userInfo.getUserId(), "22");
			if (IDataUtil.isEmpty(userSvc))
			{
				CSAppException.apperr(CrmCardException.CRM_CARD_240);
			}

			boolean lowDiscnt = false;
			String lowDiscntCode = "";
			// 与与4GUSIM卡互斥的卡互斥的优惠
			IDataset userDiscntInfo = new DatasetList();
			IDataset userDiscntInfoTmp = UserDiscntInfoQry.getAllValidDiscntByUserId(userInfo.getUserId());
			for (int i = 0; i < userDiscntInfoTmp.size(); i++)
			{
				String discntCode = userDiscntInfoTmp.getData(i).getString("DISCNT_CODE");
				IDataset discntSet = CommparaInfoQry.getCommparaInfoBy5("CSM", "8550", "4G", discntCode, "ZZZZ", null);
				if (IDataUtil.isNotEmpty(discntSet))
				{
					userDiscntInfo.add(userDiscntInfoTmp.getData(i));
				}
			}

			if (IDataUtil.isNotEmpty(userDiscntInfo))
			{
				lowDiscnt = true;
				for (int i = 0; i < userDiscntInfo.size(); i++)
				{
					IData userDiscnt = userDiscntInfo.getData(i);
					lowDiscntCode += "".equals(lowDiscntCode) ? userDiscnt.getString("DISCNT_CODE", "") : ',' + userDiscnt.getString("DISCNT_CODE", "");
				}

			}

			if (lowDiscnt)
			{
				CSAppException.apperr(CrmCardException.CRM_CARD_241, lowDiscntCode);
			}
		}

		// 查询是否要卡费
		SimCardBean cBean = BeanManager.createBean(SimCardBean.class);
		String netTypeCode = userInfo.getNetTypeCode();
		String tradeTypeCode = "142";
		if ("18".equals(netTypeCode))
		{
			tradeTypeCode = "3821";
		}
		IData pData = cBean.getSimCardPrice(oldSimCardNo, newSimCardNo, sn, tradeTypeCode);
		pData.put("NEW_RES_TYPE_CODE", ResTypeCode);
		pData.put("NEW_IMSI", newImsi);
		// 进行选占
		String writeTag = "";
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		param.put("SIM_CARD_NO", newSimCardNo);
		if (StringUtils.isNotEmpty(simCardInfos.getData(0).getString("EMPTY_CARD_ID")))
			writeTag = "0";// 新卡为白卡
		param.put("WRITE_TAG", writeTag);
		SimCardCheckBean cardBean = (SimCardCheckBean) BeanManager.createBean(SimCardCheckBean.class);
		cardBean.preOccupySimCard(param);
		return pData;
	}

	/**
	 * 积分标价类交易超时查询
	 * 
	 * @author zhouyl
	 * @param pd
	 * @param inparam
	 * @return result
	 * @throws Exception
	 */
	public IData createTradeTimeOutN(IData inparam) throws Exception
	{
		inparam.put("SERIAL_NUMBER", inparam.getString("MOBILE", ""));
		inparam.put("ORDER_ID", inparam.getString("TRADE_SEQ", ""));
		String oprType = inparam.getString("ORD_OPR_TYPE", "");
		String opr_type = transData(oprType);
		inparam.put("TRADE_TYPE_CODE", "329");
		String subscribe_id = inparam.getString("SUBSCRIBE_ID", "");
		// 获取子定单编号并设置resultInfo值 01：积分支付；02：积分支付回退；03：积分充值/赠送
		String resultInfo = "";
		IDataset tradeInfos = TradeScoreInfoQry.queryTradeScoreByRsrvstrN("329", inparam.getString("SERIAL_NUMBER"), inparam.getString("ORDER_ID"), subscribe_id, opr_type);
		if (tradeInfos != null && tradeInfos.size() > 0)
		{
			String subscribe_state = tradeInfos.getData(0).getString("SUBSCRIBE_STATE");
			if ("6".equals(subscribe_state) || "M".equals(subscribe_state))
			{
				if ("01".equals(oprType))
				{
					resultInfo = "11";
				} else if ("02".equals(oprType))
				{
					resultInfo = "12";
				} else if ("03".equals(oprType))
				{
					resultInfo = "13";
				}

			} else
			{
				if ("01".equals(oprType))
				{
					resultInfo = "01";
				} else if ("02".equals(oprType))
				{
					resultInfo = "02";
				} else if ("03".equals(oprType))
				{
					resultInfo = "03";
				}
			}
		} else
		{
			resultInfo = "99";
		}

		// 设置返回数据
		IData result = new DataMap();
		result.put("TRADE_SEQ", inparam.getString("TRADE_SEQ", ""));
		result.put("ORGID", inparam.getString("ORGID", ""));
		result.put("MOBLIE", inparam.getString("MOBLIE", ""));
		result.put("STATUS", resultInfo);
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "OK");

		return result;
	}

	public String transData(String oprType) throws Exception
	{
		String opr_type = "030";
		if ("01".endsWith(oprType))
		{
			opr_type = "031";
		} else if ("02".endsWith(oprType))
		{
			opr_type = "032";
		} else if ("03".endsWith(oprType))
		{
			opr_type = "030";
		} else
		{
			opr_type = "030";
		}
		return opr_type;
	}

	/**
	 * 查询用户信息
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryAllUserForegift(IData input) throws Exception
	{
		String serialNumber = input.getString("SERIAL_NUMBER", "");
		String userId = input.getString("USER_ID", "");

		// 如果传入的手机号和用户ID为空，直接报错
		if (serialNumber.trim().equals("") && userId.trim().equals(""))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_3002);
		}

		IDataset allUserInfo = UserInfoQry.queryAllUserBySerialNumberOrUserId(serialNumber, userId);

		if (IDataUtil.isEmpty(allUserInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		
		//strat-REQ201809170020押金业务优化-wangsc10-20180920
		//账务侧界面押金转预存在判断用户押金时，仅判断表tf_f_user_foregift数据，而营业侧需通过此表和另一个表tf_f_user_otherserv表的押金数据一致才可以正常操作押金。 
		//请新增押金判断接口，提供于界面押金转预存时，账务侧调该接口判断押金表tf_f_user_otherserv的数据，与tf_f_user_foregift表数据一致时，才可办理该业务。
		IDataset userForegift = UserForegiftInfoQry.getUserForegift(userId);
		if (IDataUtil.isNotEmpty(userForegift))
		{
			boolean bz = true;
			for (int i = 0, size = userForegift.size(); i < size; i++)
			{	//发票资料表 中的押金金额
				String userForeMoneyStr = userForegift.getData(i).getString("MONEY");
				double userForeMoney = 0;
				if(userForeMoneyStr !=null&&!"".equals(userForeMoneyStr)){
					userForeMoney=Double.valueOf(userForeMoneyStr);
				}
				//发票记录表 中的发票金额,根据发票资料表传来的数据得到RSRV_NUM2 字段的和
				double userOtherservMoney = 0;
				String FOREGIFT_CODE = userForegift.getData(i).getString("FOREGIFT_CODE");
				IDataset userOtherserv = UserOtherservQry.qryServInfoByuserIdrsrvStr1(userId, FOREGIFT_CODE);
				if(IDataUtil.isNotEmpty(userOtherserv)){
					String userOtherservMoneyStr = userOtherserv.getData(0).getString("RSRV_NUM2");
					if(userOtherservMoneyStr !=null&&!"".equals(userOtherservMoneyStr)){
						userOtherservMoney=Double.valueOf(userOtherservMoneyStr);
					}
				}
				if(userForeMoney != userOtherservMoney){
					bz = false;
				}
			}
			
			if(!bz){
				CSAppException.apperr(CrmUserException.CRM_USER_3010);
			}
		}else{
			CSAppException.apperr(CrmUserException.CRM_USER_3011);
		}
		//end
		IDataset result = new DatasetList();
		for (int i = 0, size = allUserInfo.size(); i < size; i++)
		{
			String userIdTemp = allUserInfo.getData(i).getString("USER_ID");

			IDataset userGiftInfo = UserForegiftInfoQry.getUserForegift(userIdTemp);
			if (IDataUtil.isNotEmpty(userGiftInfo))
			{
				result.addAll(userGiftInfo);
			}

		}

		return result;
	}

	/**
	 * PUK码查询
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryUserPUKCode(IData input) throws Exception
	{

		IData param = new DataMap();

		this.checkState(input);

		String serial_number = input.getString("SERIAL_NUMBER");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_126, serial_number);// 该号码【%s】没有有效的用户信息
		}
		String userId = userInfo.getString("USER_ID");
		String custId = userInfo.getString("CUST_ID");

		// 获取三户资料
		IData idata = new DataMap();
		idata.clear();
		idata.put("SERIAL_NUMBER", serial_number);
		idata.put("KIND_ID", input.getString("KIND_ID"));
		idata.put("USER_ID", userId);
		idata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		idata.put("IDTYPE", input.getString("IDTYPE"));
		idata.put("X_GETMODE", "1");// 根据USER_ID获取
		IDataset res = getUserCustAcct(idata);

		String pukcode = res.getData(0).getString("PUK");

		if (pukcode == null || "".equals(pukcode))
		{

			CSAppException.apperr(CrmUserException.CRM_USER_3000);

		}

		param.put("PUK_CODE", pukcode);
		return param;
	}

	/**
	 * 客户有效性校验
	 * 
	 * @Title : checkState
	 * @Description:TODO
	 * @Param : @param input
	 * @return: void
	 * @throws Exception
	 */
	private void checkState(IData data) throws Exception
	{

		String sn = data.getString("SERIAL_NUMBER");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);

		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userid = userInfo.getString("USER_ID");
		String identCode = data.getString("IDENT_CODE");
		String contactId = data.getString("CONTACT_ID");

		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);

		if (IDataUtil.isEmpty(dataset))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}

	}

	/**
	 * @Function: queryUserGPRS
	 * @Description: ITF_CRM_QueryUserGPRS GPRS状态查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 上午11:37:47 2013-9-13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-13 longtian3 v1.0.0 TODO:
	 */
	public IDataset queryUserGPRS(IData data) throws Exception
	{
		// 入参校验
		IDataUtil.chkParam(data, "OPR_NUMB");
		String contactId = IDataUtil.chkParam(data, "CONTACT_ID");
		String serialNumber = IDataUtil.chkParam(data, "MSISDN");
		String identCode = data.getString("IDENT_CODE", "");

		// 用户校验
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}

		// 用户凭证校验
		String userId = userInfo.getString("USER_ID");
		if (StringUtils.isBlank(identCode))
		{
			IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
			if (IDataUtil.isEmpty(dataset))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有登录或者登录已经失效!");
			}
		}

		IDataset usersvc = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, "22");
		String startDate = "";
		String endDate = "";
		String state = "";
		if (IDataUtil.isNotEmpty(usersvc))
		{
			startDate = usersvc.getData(0).getString("START_DATE");
			endDate = usersvc.getData(0).getString("END_DATE");
			state = "0";
		} else
		{
			startDate = SysDateMgr.getSysTime();
			endDate = startDate;
			state = "1";
		}

		startDate = startDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		endDate = endDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

		IData result = new DataMap();
		result.put("USER_ID", serialNumber);
		result.put("EFFECT_TIME", startDate);
		result.put("VALID_TIME", endDate);
		result.put("STATE", state);

		return new DatasetList(result);
	}

	/**
	 * @Function: queryUserSimBak
	 * @Description: ITF_CRM_QueryUserSimBak 备卡信息查询
	 * @param:
	 * @param data
	 * @param:
	 * @return
	 * @param:
	 * @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 上午11:35:03 2013-9-13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-13 longtian3 v1.0.0 TODO:
	 */
	public IDataset queryUserSimBak(IData data) throws Exception
	{
		IDataUtil.chkParam(data, "OPR_NUMB");
		String contactId = IDataUtil.chkParam(data, "CONTACT_ID");
		String serialNumber = IDataUtil.chkParam(data, "MSISDN");
		String identCode = data.getString("IDENT_CODE", "");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}

		// 用户凭证校验
		String userId = userInfo.getString("USER_ID");
		if (StringUtils.isBlank(identCode))
		{
			IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
			if (IDataUtil.isEmpty(dataset))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有登录或者登录已经失效!");
			}
		}

		// // 获取vip信息
		// IDataset vipInfo = CustVipInfoQry.querySimBakInfo(serialNumber,
		// userInfo.getString("REMOVE_TAG"));
		// if (IDataUtil.isEmpty(vipInfo))
		// {
		// CSAppException.apperr(CustException.CRM_CUST_178);// 获取大客户信息无数据
		// }

		// 获取vip备卡信息
		IDataset simbak = CustVipInfoQry.querySimBakByVipId(userId, "0");
		if (IDataUtil.isEmpty(simbak))
		{
			CSAppException.apperr(CrmCardException.CRM_CARD_8);// 获取用户无备卡申请信息
		}

		// 如果存在备卡信息，获取备卡类型编码和名称信息
		String resNo = simbak.getData(0).getString("SIM_CARD_NO");

		IData result = new DataMap();
		result.put("SIM_CARDNO2", resNo);

		return new DatasetList(result);
	}

	/**
	 * @Function: queryUserInterRoamDay
	 * @Description: ITF_CRM_QueryUserInterRoamDay 国际漫游业务日套餐状态查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 下午02:14:21 2013-9-13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-13 longtian3 v1.0.0 TODO:
	 */
	public IDataset queryUserInterRoamDay(IData data) throws Exception
	{
		// 入参校验
		IDataUtil.chkParam(data, "OPR_NUMB");
		String contactId = IDataUtil.chkParam(data, "CONTACT_ID");
		String serialNumber = IDataUtil.chkParam(data, "MSISDN");
		String identCode = data.getString("IDENT_CODE", "");

		// 用户校验
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}

		// 用户凭证校验
		String userId = userInfo.getString("USER_ID");
		if (StringUtils.isBlank(identCode))
		{
			IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
			if (IDataUtil.isEmpty(dataset))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有登录或者登录已经失效!");
			}
		}

		String eparchyCode = userInfo.getString("EPARCHY_CODE");
		IDataset roamDay = UserDiscntInfoQry.queryInterRoamDayInfo(userId, eparchyCode);

		String packName = "";
		String packCode = "";
		String packExplain = "";
		String packType = "";
		String effectTime = "";
		String validTime = "";
		String state = "";
		if (IDataUtil.isNotEmpty(roamDay))
		{
			IData tempDate = roamDay.getData(0);
			packName = tempDate.getString("PARAM_NAME");
			packCode = tempDate.getString("PARA_CODE2");
			packExplain = tempDate.getString("PACKAGE_DESC");
			packType = tempDate.getString("PARA_CODE3");
			effectTime = tempDate.getString("EFFECT_TIME");
			validTime = tempDate.getString("VALID_TIME");
			state = tempDate.getString("STATE");
		} else
		{
			effectTime = SysDateMgr.getSysTime();
			validTime = effectTime;
			state = "0";
		}

		effectTime = effectTime.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		validTime = validTime.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

		IData result = new DataMap();
		result.put("USER_ID", serialNumber);
		result.put("PACK_NAME", packName);
		result.put("PACK_CODE", packCode);
		result.put("PACK_EXPLAIN", packExplain);
		result.put("PACK_TYPE", packType);
		result.put("EFFECT_TIME", effectTime);
		result.put("VALID_TIME", validTime);
		result.put("PACK_STATE", state);

		return new DatasetList(result);
	}

	/**
	 * @Function: queryUserSaleActive
	 * @Description: ITF_CRM_QueryUserSaleActive 本地营销案查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 下午02:30:16 2013-9-13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-13 longtian3 v1.0.0 TODO:
	 */
	public IDataset queryUserSaleActive(IData data) throws Exception
	{
		IDataset results = new DatasetList();
		// 入参校验
		IDataUtil.chkParam(data, "OPR_NUMB");
		String contactId = IDataUtil.chkParam(data, "CONTACT_ID");
		String serialNumber = IDataUtil.chkParam(data, "MSISDN");
		String identCode = data.getString("IDENT_CODE", "");

		// 用户校验
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}

		// 用户凭证校验
		String userId = userInfo.getString("USER_ID");
		if (StringUtils.isBlank(identCode))
		{
			IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
			if (IDataUtil.isEmpty(dataset))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有登录或者登录已经失效!");
			}
		}

		String eparchyCode = userInfo.getString("EPARCHY_CODE");
		String provName = UAreaInfoQry.getAreaNameByAreaCode(getVisit().getProvinceCode());
		String cityName = UAreaInfoQry.getAreaNameByAreaCode(eparchyCode);

		String userCity = provName + cityName;
		IDataset activeInfo = UserSaleActiveInfoQry.queryUserSaleActiveByTag(userId);
		String startDate = "";
		String endDate = "";
		String productId = "";
		String productName = "";
		if (IDataUtil.isNotEmpty(activeInfo))
		{
			startDate = activeInfo.getData(0).getString("START_DATE");
			endDate = activeInfo.getData(0).getString("END_DATE");
			productId = activeInfo.getData(0).getString("PRODUCT_ID");
			productName = activeInfo.getData(0).getString("PRODUCT_NAME");

			for (int i = 0; i < activeInfo.size(); i++)
			{
				IData userSaleActive = new DataMap();
				userSaleActive.put("USER_ID", data.getString("SERIAL_NUMBER"));

				startDate = ((IData) activeInfo.get(i)).getString("START_DATE");
				endDate = ((IData) activeInfo.get(i)).getString("END_DATE");
				productId = ((IData) activeInfo.get(i)).getString("PRODUCT_ID");
				productName = ((IData) activeInfo.get(i)).getString("PRODUCT_NAME");

				IData acctInfo = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);
				if (IDataUtil.isEmpty(acctInfo))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "缺少用户账期数据!");
				}

				String accountDay = acctInfo.getString("FIRST_DAY_NEXTACCT");
				startDate = startDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
				endDate = endDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
				accountDay = accountDay.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

				IData result = new DataMap();
				result.put("USER_ID", serialNumber);
				result.put("NO_PROV", userCity);
				result.put("CASE_NAME", productName);
				result.put("CASE_CODE", productId);
				result.put("EFFECT_TIME", startDate);
				result.put("VALID_TIME", endDate);
				result.put("ACCOUNT_DAY", accountDay);
				// rsltId
				result.put("RSLT_ID", i + 1);
				results.add(result);
			}
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有营销方案记录信息!");
		}

		return results;
	}

	/**
	 * @Function: queryUserProductInfo
	 * @Description: ITF_CRM_QueryUserProductInfo 已订购业务查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 下午03:43:09 2013-9-13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-13 longtian3 v1.0.0 TODO:
	 */
	public IDataset queryUserProductInfo(IData data) throws Exception
	{
		IDataUtil.chkParam(data, "OPR_NUMB");
		String contactId = IDataUtil.chkParam(data, "CONTACT_ID");
		String serialNumber = IDataUtil.chkParam(data, "MSISDN");
		String identCode = data.getString("IDENT_CODE", "");

		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}

		// 用户凭证校验
		String userId = userInfo.getString("USER_ID");
		if (StringUtils.isBlank(identCode))
		{
			IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
			if (IDataUtil.isEmpty(dataset))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有登录或者登录已经失效!");
			}
		}

		String productId = userInfo.getString("PRODUCT_ID");
		String eparchyCode = userInfo.getString("EPARCHY_CODE");

		// IDataset dataset1 =
		// UserPlatSvcInfoQry.queryNormalPlatSvcInfoByUserId(productId, userId,
		// "1", eparchyCode);//拆sql，等待产商品接口
		// IDataset dataset2 =
		// PlatSvcInfoQry.queryPlatOrderInfobyUserId01(userId);//拆sql，等待产商品接口
		// IDataset dataset3 =
		// UserPlatSvcInfoQry.queryNormalPlatSvcInfoByUserId(productId, userId,
		// "2", eparchyCode);

		IDataset dataset1 = UserPlatSvcInfoQry.queryNormalPlatSvcInfoByUserId(userId);

		// dataset1.addAll(dataset2);
		// dataset1.addAll(dataset3);

		IDataset productDataset = new DatasetList();
		if (IDataUtil.isNotEmpty(dataset1))
		{
			for (int i = 0; i < dataset1.size(); i++)
			{
				IData d = new DataMap();
				d.put("BUNESS_TYPE", "02");
				d.put("BUNESS_CODE", "");

				d.put("SP_ID", dataset1.getData(i).getString("SP_ID"));
				d.put("BIZ_CODE", dataset1.getData(i).getString("BIZ_CODE") + "|" + dataset1.getData(i).getString("BIZ_TYPE_CODE"));
				d.put("BUNESS_NAME", dataset1.getData(i).getString("SERVICE_NAME"));

				String price = dataset1.getData(i).getString("PRICE", "0");
				if (price.length() > 0)
				{
					if (price.startsWith("."))
					{
						price = "0" + price;
					}
				}
				d.put("BUNESS_FREE", price + "元/月");

				d.put("START_TIME", dataset1.getData(i).getString("START_DATE").substring(0, 10).replace("-", ""));
				d.put("DEAD_TIME", dataset1.getData(i).getString("END_DATE").substring(0, 10).replace("-", ""));
				productDataset.add(d);
			}
		}

		// 套餐
		IDataset queryInfos = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
		if (IDataUtil.isNotEmpty(queryInfos))
		{
			for (int i = 0; i < queryInfos.size(); i++)
			{
				String explain = queryInfos.getData(i).getString("DISCNT_EXPLAIN");
				if (explain != null && explain.length() > 128)
				{
					explain = explain.substring(0, 128);// 接口范围长度128，实际可能更长些
				}
				String discnt = queryInfos.getData(i).getString("DISCNT_CODE", "");
				IDataset paraInfo = CommparaInfoQry.queryCommparaInfoByParaCode2("CSM", "3700", discnt);

				int tag = 0;
				String paraCode1 = "";
				String spCode = "";
				if (IDataUtil.isNotEmpty(dataset1) && IDataUtil.isNotEmpty(paraInfo))
				{
					for (int j = 0; j < dataset1.size(); j++)
					{
						IData plat = dataset1.getData(j);
						for (int k = 0; k < paraInfo.size(); k++)
						{
							IData para = paraInfo.getData(k);
							if (plat.getString("SERVICE_ID", "").equals(para.getString("PARAM_CODE", "")))
							{
								tag++;
								paraCode1 = para.getString("PARA_CODE1", "");
								spCode = plat.getString("SP_CODE");
								String price = plat.getString("PRICE", "0");
								break;
							}
						}
					}
				}

				IData temp = new DataMap();
				if (tag > 0)
				{
					temp.put("BUNESS_TYPE", "02");
					temp.put("BIZ_CODE", paraCode1 + "|" + "02");
				} else
				{
					temp.put("BUNESS_TYPE", "01");
					temp.put("BIZ_CODE", "");
				}
				temp.put("BUNESS_CODE", "D" + queryInfos.getData(i).getString("DISCNT_CODE"));
				temp.put("SP_ID", spCode);
				temp.put("BUNESS_NAME", queryInfos.getData(i).getString("DISCNT_NAME"));
				temp.put("BUNESS_FREE", "");
				// temp.put("ORDERING_TIME",
				// queryInfos.getData(i).getString("START_DATE").substring(0,
				// 10).replaceAll("-", ""));
				temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
				temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
				// temp.put("FEE_TYPE", "");

				productDataset.add(temp);
			}
		}

		// 产品
		queryInfos = UserProductInfoQry.queryUserProductByUserId(userId);// 解藕
																			// duhj
		if (IDataUtil.isNotEmpty(queryInfos))
		{
			for (int i = 0; i < queryInfos.size(); i++)
			{
				String explain = queryInfos.getData(i).getString("PRODUCT_EXPLAIN");
				if (explain != null && explain.length() > 128)
				{
					explain = explain.substring(0, 128);// 接口范围长度128，实际可能更长些
				}
				IData temp = new DataMap();
				temp.put("BUNESS_TYPE", "01");
				temp.put("BUNESS_CODE", "P" + queryInfos.getData(i).getString("PRODUCT_ID"));
				temp.put("SP_ID", "");
				temp.put("BIZ_CODE", "");
				temp.put("BUNESS_NAME", queryInfos.getData(i).getString("PRODUCT_NAME"));
				temp.put("BUNESS_FREE", "");
				// temp.put("ORDERING_TIME",
				// queryInfos.getData(i).getString("START_DATE").substring(0,
				// 10).replaceAll("-", ""));
				temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
				temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
				// temp.put("FEE_TYPE", "");
				productDataset.add(temp);
			}
		}

		// 服务功能
		queryInfos = UserSvcInfoQry.queryGPRSSvcByUserId(userId);// 解藕 duhj
		if (IDataUtil.isNotEmpty(queryInfos))
		{
			for (int i = 0; i < queryInfos.size(); i++)
			{
				IData temp = new DataMap();
				temp.put("BUNESS_TYPE", "01");
				temp.put("BUNESS_CODE", "S" + queryInfos.getData(i).getString("SERVICE_ID"));
				temp.put("SP_ID", "");
				temp.put("BIZ_CODE", "");
				temp.put("BUNESS_NAME", queryInfos.getData(i).getString("SERVICE_NAME"));
				temp.put("BUNESS_FREE", "");

				// temp.put("ORDERING_TIME", "");
				temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
				temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
				// temp.put("FEE_TYPE", "");
				productDataset.add(temp);
			}
		}

		// 营销活动
		queryInfos = UserSaleActiveInfoQry.queryUserSaleActiveByUserId(userId);
		if (IDataUtil.isNotEmpty(queryInfos))
		{
			for (int i = 0; i < queryInfos.size(); i++)
			{
				IData temp = new DataMap();
				temp.put("BUNESS_TYPE", "01");
				temp.put("BUNESS_CODE", queryInfos.getData(i).getString("CAMPN_ID"));
				temp.put("SP_ID", "");
				temp.put("BIZ_CODE", "");
				temp.put("BUNESS_NAME", queryInfos.getData(i).getString("PRODUCT_NAME") + "-" + queryInfos.getData(i).getString("PACKAGE_NAME"));
				temp.put("BUNESS_FREE", "");

				// temp.put("ORDERING_TIME",
				// queryInfos.getData(i).getString("ACCEPT_DATE").substring(0,
				// 10).replaceAll("-", ""));
				temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
				temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
				// temp.put("FEE_TYPE", "");
				productDataset.add(temp);
			}
		}

		IData result = new DataMap();
		result.putAll(productDataset.toData());
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

		return new DatasetList(result);
	}

	/**
	 * 客户级别
	 */
	private String getCustLevelParam(String param)
	{
		String result = "";

		if ("0".equals(param))
			result = "300";
		else if ("1".equals(param) || "B".equals(param) || "E".equals(param))
			result = "301";
		else if ("2".equals(param) || "A".equals(param) || "D".equals(param))
			result = "302";
		else if ("3".equals(param) || "C".equals(param))
			result = "303";
		else if ("4".equals(param))
			result = "304";
		else
			result = "100";

		return result;
	}

	/**
	 * 获取用户状态编码
	 */
	private String getUserStateParam(String param)
	{
		String result = "";

		if ("0".equals(param))
			result = "00";
		else if ("1".equals(param) || "2".equals(param) || "3".equals(param) || "4".equals(param) || "5".equals(param) || "7".equals(param))
			result = "02";
		else if ("6".equals(param))
			result = "04";
		else if ("8".equals(param) || "9".equals(param))
			result = "03";
		else if ("A".equals(param) || "B".equals(param) || "G".equals(param))
			result = "01";
		else if ("C".equals(param) || "D".equals(param) || "I".equals(param) || "J".equals(param) || "K".equals(param) || "L".equals(param) || "M".equals(param) || "O".equals(param) || "Q".equals(param))
			result = "02";
		else if ("E".equals(param))
			result = "04";
		else if ("F".equals(param) || "H".equals(param))
			result = "03";
		else if ("N".equals(param))
			result = "00";
		else
			result = "00";

		return result;
	}

	/**
	 * @Function: queryPersonInfo
	 * @Description: ITF_CRM_QueryUserInfo 客户个人信息查询
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 下午03:42:53 2013-9-13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-13 longtian3 v1.0.0 TODO:
	 */
	public IDataset queryPersonInfo(IData data) throws Exception
	{
		// 入参校验
		IDataUtil.chkParam(data, "OPR_NUMB");
		String contactId = data.getString("CONTACT_ID");// IDataUtil.chkParam(data,
														// "CONTACT_ID");
		String serialNumber = IDataUtil.chkParam(data, "MSISDN");
		IDataUtil.chkParam(data, "IDENT_CODE");
		String identCode = data.getString("IDENT_CODE", "");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_13, "4005", "用户资料不存在!");
		}

		// 用户凭证校验
		String userId = userInfo.getString("USER_ID");
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
		if (IDataUtil.isEmpty(dataset))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有登录或者登录已经失效!");
		}

		IData param = new DataMap();
		param.put("X_GETMODE", "1");
		param.put("USER_ID", userId);
		IDataset ucaset = this.getUserCustAcct(param);
		IData uca = ucaset.getData(0);

		// 用户姓名
		String userName = uca.getString("CUST_NAME", "");
		// 手机号码
		String msisdn = serialNumber;
		// 归属地
		String userCity = UAreaInfoQry.getAreaNameByAreaCode(getVisit().getProvinceCode() + uca.getString("X_EPARCHY_NAME"));
		// 入网时间
		String userBegin = uca.getString("IN_DATE", "");
		// 用户品牌
		String userBrand = uca.getString("BRAND_CODE", "");
		// 客户等级
		String userLevel = uca.getString("CLASS_ID", "");
		// SIM卡号
		String simCardNo = uca.getString("SIM_CARD_NO", "");
		// 用户状态
		String userStatus = uca.getString("USER_STATE_CODESET", "");

		IDataset userbrand = UserBrandInfoQry.getUserBrandChangeBySn(userId, null);
		if (IDataUtil.isNotEmpty(userbrand))
		{
			userBrand = userbrand.getData(0).getString("BRAND_CODE");
		}

		// 中高端标识
		IDataset highCust = CustVipInfoQry.queryHighCustByUserId(userId, "*");
		String highLevelId = "";
		if (IDataUtil.isNotEmpty(highCust))
		{
			highLevelId = "1";
		} else
		{
			highLevelId = "0";
		}

		String para1 = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
		{ "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", new String[]
		{ "998", userBrand });
		if (StringUtils.isBlank(para1))
		{
			userBrand = "";
		} else
		{
			if ("0".equals(para1))
			{
				userBrand = "01";// 全球通
			} else if ("1".equals(para1))
			{
				userBrand = "02";// 神州行
			} else if ("2".equals(para1))
			{
				userBrand = "03";// 动感地带
			} else if ("3".equals(para1))
			{
				userBrand = "02";// 神州行
			} else
			{
				userBrand = "09";// 其它品牌
			}
		}

		userLevel = this.getCustLevelParam(userLevel);
		userStatus = this.getUserStateParam(userStatus);
		userBegin = userBegin.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

		// 客户名称限制32位
		char ch[] = userName.toCharArray();
		int j = 0;
		for (int i = 0; i < ch.length; i++)
		{
			if (ch[i] > 255)
			{
				j = j + 2;
			} else
			{
				j = j + 1;
			}
			if (j > 32)
			{
				j = i;
				break;
			}
		}
		if (j > ch.length)
			j = ch.length;
		userName = userName.substring(0, j);

		IData result = new DataMap();
		result.put("USER_NAME", userName);
		result.put("MSISDN", msisdn);
		result.put("USER_CITY", userCity);
		result.put("USER_BEGIN", userBegin);
		result.put("USER_BRAND", userBrand);
		result.put("USER_LEVEL", userLevel);
		result.put("HIGH_LEVELID", highLevelId);
		result.put("SIM_CARDNO", simCardNo);
		result.put("USER_STATUS", userStatus);

		return new DatasetList(result);
	}

	/**
	 * 上网日志查询接口
	 */
	public IDataset queryOnlineDetails(IData data) throws Exception
	{
		// 入参校验
		IDataUtil.chkParam(data, "START_TIME");// 上网开始时间
		IDataUtil.chkParam(data, "END_TIME");// 上网结束时间
		IDataUtil.chkParam(data, "MS_ID");// 上网账号
		IDataUtil.chkParam(data, "STAFF_ID");// 查询员工
		IDataUtil.chkParam(data, "KF_TRADE_ID");// 投诉工单号

		IDataset details = new DatasetList();
		IData returndata = new DataMap();
		IDataset returnset = new DatasetList();
		// 1.首先判断用户是否存在
		String serialNumber = data.getString("MS_ID");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_13, "4005", "用户资料不存在!");
		}
		String userId = userInfo.getString("USER_ID");
		// 2.给客户发通知短信(内容包含查询员工，查询时间，查询方式)
		IDataset inMode = Dao.qryByCode("TD_S_CPARAM", "SEL_INMODE_BY_PK", data);
		String content = "您好，" + data.getString("STAFF_ID") + "于" + SysDateMgr.getSysDate() + "通过" + inMode.getData(0).getString("IN_MODE") + "方式查询您上网日志,详情请垂询10086。中国移动";
		sms(userId, serialNumber, content, "上网日志查询提醒", data);
		// 3.入请求表,用户查询开始时间,结束时间,当前时间,当前时间+配置时间,初始状态0

		// 首先查询tf_b_gonet_reg_log表是否有数据
		IData param = new DataMap();
		param.put("KF_TRADE_ID", data.getString("KF_TRADE_ID"));

		IDataset datasetList = new DatasetList();
		// 获取省编码
		IDataset proList = new DatasetList();
		String pdata_id = "";
		data.put("TYPE_ID", "PROVINCE_CODE");
		proList = Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID", data);
		for (int j = 0; j < proList.size(); j++)
		{
			if (getVisit().getProvinceCode().equals(proList.getData(j).getString("DATA_ID")))
			{
				pdata_id = proList.getData(j).getString("PDATA_ID");
			}
		}
		param.put("PROV_ID", pdata_id);

		// 获取配置时间

		IData inparam = new DataMap();
		inparam.put("SUBSYS_CODE", "CSM");
		inparam.put("PARAM_ATTR", "1119");
		inparam.put("PARAM_CODE", "1119");
		inparam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

		IDataset timeInfo = Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", inparam);
		String timeCom = timeInfo.getData(0).getString("PARA_CODE1");

		Date d = new Date();
		Calendar c = Calendar.getInstance();
		/**
		 * SELECT * FROM tf_b_gonet_reg_log a WHERE a.kf_trade_id =:VKF_TRADE_ID
		 * ORDER BY QUERY_END_TIME (按最后时间升序排序)
		 */
		/*
		 * 测试用 SQLParser parser = new SQLParser(param);
		 * parser.addSQL(" SELECT * FROM TF_B_GONET_REG_LOG A ");
		 * parser.addSQL(" WHERE A.KF_TRADE_ID = :KF_TRADE_ID ");
		 * parser.addSQL(" ORDER BY QUERY_END_TIME"); IDataset sets =
		 * Dao.qryByParse(parser);
		 */

		IDataset sets = Dao.qryByCode("TF_B_GONET_REG_LOG", "SEL_BY_TRADEID", param);
		if (sets.size() <= 0)
		{
			// 没有数据代表第一次查询,入表

			param.put("ACCEPT_MONTH", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(5, 7));
			param.put("EVENT_ID", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").replaceAll(" ", "-").replaceAll(":", "-") + "-" + pdata_id + "00");// 这里序列写死,反正只有一个
			param.put("START_TIME", data.getString("START_TIME"));
			param.put("END_TIME", data.getString("END_TIME"));
			param.put("MS_ID", data.getString("MS_ID"));
			param.put("CHNL_ID", data.getString("IN_MODE_CODE"));
			param.put("QUERY_STATUS", "0");
			param.put("QUERY_START_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
			param.put("STAFF_ID", data.getString("STAFF_ID"));

			c.setTime(d);
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + Integer.parseInt(timeCom));

			// param.put("QUERY_END_TIME", format.format(c.getTime()));
			param.put("QUERY_END_TIME", DateFormatUtils.format(c.getTime(), "yyyy-MM-dd HH:mm:ss"));

			/**
			 * INSERT INTO TF_B_GONET_REG_LOG
			 * (KF_TRADE_ID,ACCEPT_MONTH,EVENT_ID,
			 * START_TIME,END_TIME,MS_ID,CHNL_ID,
			 * QUERY_STATUS,QUERY_START_TIME,QUERY_END_TIME) values
			 * (:VKF_TRADE_ID
			 * ,:VACCEPT_MONTH,:VEVENT_ID,:VSTART_TIME,:VEND_TIME,:
			 * VMS_ID,:VCHNL_ID,
			 * :VQUERY_STATUS,:VQUERY_START_TIME,:VQUERY_END_TIME);
			 */
			boolean a = Dao.insert("TF_B_GONET_REG_LOG", param);
			// 调用网状网发起查询请求

			queryOnlineDetailsByBOSS(param);

		} else
		{
			// 有数据,就找有效的那条数据
			for (int i = 0; i < sets.size(); i++)
			{
				if ("1".equals(sets.getData(i).getString("QUERY_STATUS")))
				{

					// 如果为1则代表日志表已经有数据,直接返回
					/**
					 * SELECT
					 * ACCEPT_MONTH,START_TIME,END_TIME,MS_ID,NAT_CLIENT_IP
					 * ,NAT_CLIENT_PORT,DESTINATION_URL
					 * DESTINATION_IP,DESTINATION_PORT,CLIENT_IP,CLIENT_PORT,APN
					 * FROM TF_B_GONET_LOG WHERE MS_ID = :VMS_ID AND
					 * TO_DATE(:VSTATE_TIME,'yyyy-MM-dd HH:mm:ss')<=STATE_TIME
					 * AND TO_DATE(:VEND_TIME,'yyyy-MM-dd HH:mm:ss')>=END_TIME
					 */
					/*
					 * 测试用 SQLParser parser1 = new SQLParser(data);
					 * parser1.addSQL(
					 * " SELECT ACCEPT_MONTH,START_TIME,END_TIME,MS_ID,NAT_CLIENT_IP,NAT_CLIENT_PORT,DESTINATION_URL "
					 * );parser1.addSQL(
					 * " DESTINATION_IP,DESTINATION_PORT,CLIENT_IP,CLIENT_PORT,APN FROM TF_B_GONET_LOG "
					 * ); parser1.addSQL(" WHERE MS_ID = :MS_ID ");
					 * parser1.addSQL(
					 * " AND TO_DATE(:START_TIME,'yyyy-MM-dd HH24:mi:ss')<=START_TIME "
					 * );parser1.addSQL(
					 * " AND TO_DATE(:END_TIME,'yyyy-MM-dd HH24:mi:ss')>=END_TIME "
					 * ); datasetList =
					 * Dao.qryByParse(parser1,Route.CONN_CRM_CEN);
					 */

					datasetList = Dao.qryByCode("TF_B_GONET_LOG", "SEL_ONLINEDETAIL_BY_TIME", data, Route.CONN_CRM_CEN);
					if (datasetList != null && datasetList.size() > 0)
					{
						datasetList.getData(0).put("X_PAGE_COUNT", datasetList.size());
						return datasetList;
					}

				} else
				{
					if (new Integer(SysDateMgr.getSysTime()) <= new Integer(sets.getData(i).getString("QUERY_END_TIME")))
					{
						returndata.put("X_RESULTINFO", "还没有获取到数据,请不要频繁发起请求,稍后再试!");
						returndata.put("X_RESULTCODE", "-1");
						returnset.add(returndata);
						return returnset;
					}

				}
				// 代表没有有效的数据
				if (i == sets.size() - 1)
				{
					// 重新插入一条数据,从上条数据复制过来
					String queryStatus = sets.getData(i).getString("QUERY_STATUS");
					param.put("ACCEPT_MONTH", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(5, 7));
					param.put("KF_TRADE_ID", sets.getData(i).getString("KF_TRADE_ID"));
					param.put("EVENT_ID", sets.getData(i).getString("EVENT_ID"));
					param.put("START_TIME", sets.getData(i).getString("START_TIME"));
					param.put("END_TIME", sets.getData(i).getString("END_TIME"));
					param.put("MS_ID", sets.getData(i).getString("MS_ID"));
					param.put("CHNL_ID", sets.getData(i).getString("CHNL_ID"));
					param.put("QUERY_STATUS", queryStatus);
					param.put("QUERY_START_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));

					c.setTime(d);
					c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + Integer.parseInt(timeCom));

					param.put("QUERY_END_TIME", DateFormatUtils.format(c.getTime(), "yyyy-MM-dd HH:mm:ss"));
					/**
					 * INSERT INTO TF_B_GONET_REG_LOG
					 * (KF_TRADE_ID,ACCEPT_MONTH,EVENT_ID
					 * ,START_TIME,END_TIME,MS_ID,CHNL_ID,
					 * QUERY_STATUS,QUERY_START_TIME,QUERY_END_TIME) values
					 * (:VKF_TRADE_ID
					 * ,:ACCEPT_MONTH,:VEVENT_ID,:VSTART_TIME,:VEND_TIME
					 * ,:VMS_ID,:VCHNL_ID,
					 * :VQUERY_STATUS,:VQUERY_START_TIME,:VQUERY_END_TIME);
					 */
					boolean a = Dao.insert("TF_B_GONET_REG_LOG", param);
					// pd.commitDBConn();
					//				
					// 告知客服再等待查询
					returndata.put("X_RESULTINFO", "还没有获取到数据,请稍后再试!");
					returndata.put("X_RESULTCODE", "-1");
					returnset.add(returndata);
					return returnset;

				}
			}
		}
		return datasetList;
	}

	/**
	 * 发短信通知客户
	 * 
	 * @param pd
	 * @param td
	 * @param sn
	 * @param content
	 * @param remark
	 * @throws Exception
	 */
	public void sms(String userId, String sn, String content, String remark, IData inparams) throws Exception
	{
		IData newParam = new DataMap();
		String seqId = SeqMgr.getSmsSendId();
		newParam.put("SMS_NOTICE_ID", seqId);
		newParam.put("PARTITION_ID", seqId.substring(seqId.length() - 4));
		newParam.put("SEND_COUNT_CODE", "1");
		newParam.put("REFERED_COUNT", "0");
		newParam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
		newParam.put("IN_MODE_CODE", inparams.getString("IN_MODE_CODE"));
		newParam.put("CHAN_ID", "11");// 短信渠道编码:客户服务
		newParam.put("RECV_OBJECT_TYPE", "00");// 被叫对象类型:00－手机号码
		newParam.put("FORCE_OBJECT", "10086");
		newParam.put("RECV_OBJECT", sn); // 被叫对象:传手机号码
		newParam.put("RECV_ID", userId); // 被叫对象标识:传用户标识
		newParam.put("SMS_TYPE_CODE", "20"); // 短信类型:20-业务通知
		newParam.put("SMS_KIND_CODE", "02"); // 短信种类:02－短信通知
		newParam.put("NOTICE_CONTENT_TYPE", "0");// 短信内容类型:0－指定内容发送
		if (content.length() > 500)
			content = content.substring(0, 500);
		newParam.put("NOTICE_CONTENT", content);// 短信内容类型:0－指定内容发送
		newParam.put("FORCE_REFER_COUNT", "1");// 指定发送次数
		newParam.put("SMS_PRIORITY", 1000);// 短信优先级
		newParam.put("REFER_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")); // 提交时间
		newParam.put("REFER_STAFF_ID", getVisit().getStaffId());// 提交员工
		newParam.put("REFER_DEPART_ID", getVisit().getDepartId());// 提交部门
		newParam.put("DEAL_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")); // 处理时间
		newParam.put("DEAL_STATE", "15");// 处理状态:0－未处理
		newParam.put("REMARK", remark);
		newParam.put("SEND_TIME_CODE", "1");
		newParam.put("SEND_OBJECT_CODE", "6");

		newParam.put("SMS_NET_TAG", "0");
		newParam.put("MONTH", Integer.parseInt(SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(5, 7)));
		newParam.put("DAY", Integer.parseInt(SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(8, 10)));

		Dao.insert("TI_O_SMS", newParam);
	}

	/**
	 * 调用网状网查询接口
	 */
	public void queryOnlineDetailsByBOSS(IData inparams) throws Exception
	{

		IData rData = new DataMap();
		IData inparam = new DataMap();
		// 调用网状网的上网时间查询请求接口进行信息的查询

		try
		{
			inparam.put("KIND_ID", "BIP2C100_T2002100_0_0"); // 交易唯一标识
			inparam.put("X_TRANS_CODE", "");
			inparam.put("EVENTID ", inparams.getString("EVENT_ID"));
			inparam.put("PROVID", inparams.getString("PROV_ID"));
			inparam.put("STARTTIME", inparams.getString("START_TIME"));
			inparam.put("ENDTIME", inparams.getString("END_TIME"));
			inparam.put("MSISDN", inparams.getString("MS_ID"));
			inparam.put("QUERYSTAFF", inparams.getString("STAFF_ID"));
			rData = (IData) IBossCall.callHttpIBOSS("IBOSS", inparam);

		} catch (Exception e)
		{
			String errStr = e.toString();
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用一级客服接口 进行用户上网日志查询出错" + errStr);

		}
	}

	/**
	 * 结果响应告知接口
	 */
	public IDataset resultResponseByBOSS(IData inparams) throws Exception
	{

		IData returndata = new DataMap();
		IDataset returnset = new DatasetList();
		IDataUtil.chkParam(inparams, "EVENT_ID");// 请求流水号
		// 更新TF_B_GONET_REG_LOG表中状态

		/**
		 * UPDATE TF_B_GONET_REG_LOG SET QUERY_STATUS = '1' WHERE EVENT_ID =
		 * :VEVENT_ID
		 */
		/*
		 * 测试用 SQLParser parser = new SQLParser(inparams);parser.addSQL(
		 * " UPDATE TF_B_GONET_REG_LOG SET QUERY_STATUS = '1' WHERE EVENT_ID = :EVENT_ID  "
		 * ); Dao.executeUpdates(parser);
		 */
		Dao.executeUpdateByCodeCode("TF_B_GONET_REG_LOG", "UPD_QUERY_STATUS", inparams);

		// 告知客服
		returndata.put("X_RESULTINFO", "已经获取到上网日志数据,请再试!");
		returndata.put("X_RESULTCODE", "0");
		returnset.add(returndata);
		return returnset;
	}

	/**
	 * 充值卡记录查询
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset valueCardUseQuery(IData input) throws Exception
	{
		IDataset dataset = new DatasetList();
		this.checkState(input);
		IData param = new DataMap();

		String serial_number = input.getString("SERIAL_NUMBER");
		param.put("PHONE_NUMBER", serial_number);

		String start_time = input.getString("QRY_STR_TIME");
		String end_time = input.getString("QRY_END_TIME");

		String qry_time = SysDateMgr.date2String(SysDateMgr.string2Date(start_time, "yyyyMMddHHmmss"), "yyyy-MM-dd");
		String qry_end = SysDateMgr.date2String(SysDateMgr.string2Date(end_time, "yyyyMMddHHmmss"), "yyyy-MM-dd");

		param.put("START_DATE", qry_time);

		param.put("END_DATE", qry_end);

		CWSVarNode mVVarList[] = new CWSVarNode[4];

		int timeOut = 60000;// 调用SMPVC超时时间

		String paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TIME_OUT");

		timeOut = Integer.parseInt(paramValue);

		CWSServNode servNode = new CWSServNode();
		servNode.setMStrServName("G2914");

		CWSServNode mVServList[] = new CWSServNode[1];
		mVServList[0] = servNode;

		CWSVarNode varNode = new CWSVarNode();

		varNode.setMStrName("G004");
		varNode.setMStrValue(input.getString("SERIAL_NUMBER"));
		mVVarList[0] = varNode;

		CWSVarNode varNode1 = new CWSVarNode();
		varNode1.setMStrName("V934");
		varNode1.setMStrValue(qry_time);
		mVVarList[1] = varNode1;

		CWSVarNode varNode2 = new CWSVarNode();
		varNode2.setMStrName("V935");
		varNode2.setMStrValue(qry_end);
		mVVarList[2] = varNode2;

		CWSInputData inputData = new CWSInputData();
		inputData.setMStrOrderID(SysDateMgr.getSysDate("yyyyMMddHHmmssSSS"));
		inputData.setMStrSerialNumber(input.getString("SERIAL_NUMBER"));
		inputData.setMStrSwitchid("SMP10");
		inputData.setNPriority(50);
		inputData.setMVServList(mVServList);
		inputData.setMVVarList(mVVarList);

		StringHolder m_strOrderID = new StringHolder();
		IntHolder m_nOperationResult = new IntHolder();
		StringHolder m_strFinishTime = new StringHolder();
		StringHolder m_strErrorDescription = new StringHolder();
		IntHolder m_nCMDCount = new IntHolder();
		StringHolder m_strAutoCMDList = new StringHolder();
		CWSVarNodeHolder m_vQueryResult = new CWSVarNodeHolder();
		
		   //新联指
        String operationResult = null;
        String errorDescription = null;
        String cmdList = null;
        java.util.List<Element> queryResultList = null;


		   String checktype="0";	//默认不使用新调用方式
	        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CHECK_TYPE");
	        if (StringUtils.isNotBlank(paramValue))
	            {checktype = paramValue;}
	        String url = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "QRYURL"); 
	        if(checktype.equals("1")){
				SoapInputXml soapinput = new SoapInputXml();
				URL u=new URL(url);
				NeaSoapBindingStub neasop=new NeaSoapBindingStub(u,new Service());
				String xmlParamStr = null;
				
				String serlial_number = input.getString("SERIAL_NUMBER");
				String priority = "50";
				xmlParamStr = soapinput.receiveMobileRecharge_second(SysDateMgr.getSysDate("yyyyMMddHHmmssSSS"),serlial_number,priority,"SMP10",mVServList,mVVarList);
				String res =neasop.callWSSOP(xmlParamStr); //新联指调用
				 Document dom=DocumentHelper.parseText(res);
				  Element root=dom.getRootElement();
				  operationResult=root.element("operationResult").getText();
				  errorDescription=root.element("description").getText();
				  cmdList=root.element("cmdList").getText();
				  queryResultList = root.elements("queryResultList");
			}else{
		//WSSOP_ServiceLocator wssopLocator = new WSSOP_ServiceLocator("http://10.200.141.51:22000/");
		WSSOP_ServiceLocator wssopLocator = new WSSOP_ServiceLocator(url);
		WSSOPStub binding = (WSSOPStub) wssopLocator.getWSSOP();
		binding.setTimeout(timeOut);
		binding.callWSSOP(inputData, m_strOrderID, m_nOperationResult, m_strFinishTime, m_strErrorDescription, m_nCMDCount, m_strAutoCMDList, m_vQueryResult);
			}
		IData queryResult = new DataMap();
		if (logger.isDebugEnabled())
		{
			logger.debug("m_vQueryResult.value------------------------------->" + m_vQueryResult.value);
		}
		
		 if(queryResultList != null){
	    		String strName= null;
	    		String strValue= null;
	    		for(int i =0; i<queryResultList.size();i++){
	    		   strName = ((Element) queryResultList.get(i)).element("strName").getText();
	    		   strValue = ((Element) queryResultList.get(i)).element("strValue").getText();
	    		   queryResult.put(strName, strValue);

	    		}
	    	}
		
		if (m_vQueryResult.value != null && !"".equals(m_vQueryResult.value))
		{
			Iterator iterator = (Iterator) m_vQueryResult.value.iterator();
			CWSVarNode node = new CWSVarNode();
			while (iterator.hasNext())
			{
				node = (CWSVarNode) iterator.next();
				queryResult.put(node.getMStrName(), node.getMStrValue());
			}
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("queryResult------------------------------->" + queryResult);
		}
		dataset.add(queryResult);
		IDataset result = new DatasetList();

		for (int i = 0; i < dataset.size(); i++)
		{

			IData resultdata = new DataMap();
			String inchargenum = dataset.getData(i).getString("ACCOUNTPIN");
			resultdata.put("USER_ID", inchargenum);
			IData userInfo = UcaInfoQry.qryUserInfoBySn(inchargenum);

			String eparchycode = userInfo.getString("EPARCHY_CODE");
			String cityName = UAreaInfoQry.getAreaNameByAreaCode(eparchycode);

			String userCity = cityName;

			resultdata.put("NO_PROV", userCity);

			resultdata.put("PAYMENT_CHANNEL", dataset.getData(i).getString("PAYMENT_CHANNEL"));
			resultdata.put("CARD_NUMBER", dataset.getData(i).getString("SEQUENCE"));
			resultdata.put("MSISDN", serial_number);
			resultdata.put("CARD_NO", dataset.getData(i).getString("COUNTTOTAL/100"));
			resultdata.put("VALID_TIME", dataset.getData(i).getString("CRDDAY"));
			resultdata.put("TRANS_ACTIONTIME", dataset.getData(i).getString("TRADETIME"));

			String state = dataset.getData(i).getString("CRDFLG_T");
			String card_state = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), "res", "RES_STATE_DEF", new java.lang.String[]
			{ "RES_TYPE_ID", "TABLE_COL", "STATE_CODE" }, "STATE_NAME", new java.lang.String[]
			{ "3", "RES_STATE", state });

			resultdata.put("CARD_STATE", card_state);
			resultdata.put("SUM_TYPE", dataset.getData(i).getString("TradeTypeDesc"));
			resultdata.put("REMARK", dataset.getData(i).getString("ACTDAY"));

			result.add(resultdata);
		}

		return result;
	}

	/**
	 * @Function: starScoreInfoQurey
	 * @Description: ITF_CRM_StarScoreInfoQurey 一级BOSS移动商城接口1.6新增-客户星级评定详情查询接口
	 * @param: @param data
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date:
	 */
	public IDataset starScoreInfoQurey(IData input) throws Exception
	{
		String serialNumber = input.getString("IDVALUE");
		IDataset res = CommparaInfoQry.queryCommInfos("7777", "IDENT_AUTH_CONFG", input.getString("KIND_ID", ""), input.getString("BIZ_TYPE_CODE", ""));
		if (IDataUtil.isNotEmpty(res))
		{
			// 先对身份凭证进行鉴权
			String identCode = input.getString("IDENT_CODE", "");
			String businessCode = input.getString("BUSINESS_CODE", "");
			String identCodeType = input.getString("IDENT_CODE_TYPE", "");
			String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");
			String userType = input.getString("USER_TYPE", "");

			IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
			if (IDataUtil.isEmpty(idents))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_915);
			}

			if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_1103);
			}

			SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
		}
		IDataset result = AcctCall.queryCreditClassDetail(input);
		return result;

	}

	/********************************************************************************
	 * 统一查询：移动商城<BR/>
	 * 只查询用户所订购的增值类业务
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData queryOrderSvc4MM(IData data) throws Exception
	{
		String idType = data.getString("IDTYPE");
		String idValue = data.getString("IDVALUE");
		if (StringUtils.isBlank(idType) || !"01".equals(idType))
		{
			// 标识类型错误
			CSAppException.apperr(CrmUserException.CRM_USER_1190);
		}
		if (StringUtils.isBlank(idValue))
		{
			// 标识号码错误
			CSAppException.apperr(CrmUserException.CRM_USER_1191);
		}
		IData userInfo = UcaInfoQry.qryUserInfoBySn(idValue);
		if (IDataUtil.isEmpty(userInfo))
		{
			// 用户资料不存在
			CSAppException.apperr(CrmUserException.CRM_USER_1);
		}
		String userId = userInfo.getString("USER_ID");
		// 查询可退订的增值业务
		IData inParams = new DataMap();
		inParams.put("USER_ID", userInfo.getString("USER_ID"));
		inParams.put("REMOVE_TAG", "0");// 0:正常用户
		inParams.put("RSRV_STR9", "0");// 0:全部；1:自有业务，2:梦网业务
		inParams.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
		inParams.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));

		IDataset qryList = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATORDERINFO_BY_SN04", inParams);
		IDataset userSvcInfo = Dao.qryByCode("TF_F_USER_SVC", "SEL_PLATORDERINFO_BY_SN01", inParams);
		qryList.addAll(userSvcInfo);

		// 特殊处理
		IDataset myDataset = new DatasetList();
		if (qryList != null && !qryList.isEmpty())
		{
			IData tempData = new DataMap();
			IDataset tmpDataset = new DatasetList();

			for (int i = 0; i < qryList.size(); i++)
			{
				// 1.如果平台服务已经暂停，不能返回
				if (!"A".equals(qryList.getData(i).getString("BIZ_STATE_CODE", "")))
				{
					continue;
				}
				IData item = new DataMap();
				item.put("BUNESS_TYPE", "02");
				item.put("BUNESS_CODE", "");
				if ("02".equals(qryList.getData(i).getString("BIZ_TYPE_CODE", "")))
				{
					item.put("SP_ID", "02");
				} else if ("19".equals(qryList.getData(i).getString("BIZ_TYPE_CODE", "")))
				{
					item.put("SP_ID", "19");
				} else if ("92".equals(qryList.getData(i).getString("BIZ_TYPE_CODE", "")))
				{
					item.put("SP_ID", "92");
				} else
				{
					item.put("SP_ID", qryList.getData(i).getString("SP_ID"));
				}

				item.put("BIZ_CODE", qryList.getData(i).getString("BIZ_CODE") + "|" + qryList.getData(i).getString("BIZ_TYPE_CODE"));
				item.put("BUNESS_NAME", qryList.getData(i).getString("BIZ_NAME"));
				String priceStr = qryList.getData(i).getString("PRICE");
				int priceInt = Integer.parseInt(priceStr);
				item.put("BUNESS_FREE", priceInt / 1000 + "元/月");
				item.put("ORDERING_TIME", "");
				// 获取开始、截止时间，由于SQL中没有使用TO_CHAR，所以此处格式为YYYY-MM-DD HH:MI24:SS.S
				String startDate = qryList.getData(i).getString("START_DATE");
				String endDate = qryList.getData(i).getString("END_DATE");
				startDate = startDate.indexOf('.') == -1 ? startDate : startDate.substring(0, startDate.indexOf('.'));
				endDate = endDate.indexOf('.') == -1 ? endDate : endDate.substring(0, endDate.indexOf('.'));
				item.put("START_TIME", getTimeString(startDate).substring(0, 8));
				item.put("DEAD_TIME", getTimeString(endDate).substring(0, 8));
				// 在STATIC表查询 FEE_TYPE字段
				String billType = qryList.getData(i).getString("BILL_TYPE");
				String dataName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
				{ "DATA_ID", "TYPE_ID" }, "DATA_NAME", new String[]
				{ billType, "SPBIZ_BILL_TYPE" });
				item.put("FEE_TYPE", dataName);
				
				// 移动商城2.8 出参信息，新增2个字段，省份短信0000模板分类和是否可退订功能--通过该接口 查询的 都为可退订产品 add by huangyq
				item.put("CANCEL_FLAG", "0");// 0：可退订    1：不可退
				String servMode = qryList.getData(i).getString("SERV_MODE");
        		if("0".equals(servMode)){	//0,梦网业务，1,自有业务
        			item.put("BUSI_TYPE", "1");	// 0：中国移动业务	1：中国移动代收费业务
        			item.put("REGION_BUSI_TYPE", "中国移动代收费业务");
        		}else if("1".equals(servMode)){
        			item.put("BUSI_TYPE", "0");	
        			item.put("REGION_BUSI_TYPE", "中国移动业务");
        		}
//				String busiType = item.getString("BUSI_TYPE","0");// 0：中国移动业务【包含、自有业务和集团业务】,1：中国移动代收费业务【合作业务】,2：基础业务
//                if("0".equals(busiType)){
//                	item.put("REGION_BUSI_TYPE", "中国移动业务");        	
//                }else if("1".equals(busiType)){
//                	item.put("REGION_BUSI_TYPE", "中国移动代收费业务");
//                }else if("2".equals(busiType)){
//                	item.put("REGION_BUSI_TYPE", "基础业务");
//                }
                
				tmpDataset.add(item);
			}
			tempData.put("PRODUCT_TYPE", "02");// 增值类
			// 移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
			if (!tmpDataset.isEmpty())
				DataHelper.sort(tmpDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
			tempData.put("SUB", tmpDataset.toData());
			myDataset.add(tempData);
		}

		IData resultData = new DataMap();
		resultData.putAll(myDataset.toData());
		resultData.put("OPR_TIME", getSysDatePrue());

		return resultData;
	}

	public static String getSysDatePrue() throws Exception
	{
		SQLParser parser = new SQLParser(new DataMap());
		parser.addSQL(" SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') OUTSTR FROM DUAL ");
		IDataset out = Dao.qryByParse(parser);
		return ((IData) out.get(0)).getString("OUTSTR", "");
	}

	public static String getTimeString(String sysdate) throws Exception
	{
		String sql_ref = "select to_char(to_date('" + sysdate + "', 'yyyy-mm-dd hh24:mi:ss'), 'yyyymmddhh24miss') OUTSTR from dual";
		SQLParser parser = new SQLParser(new DataMap());
		parser.addSQL(sql_ref);
		IDataset dataset = Dao.qryByParse(parser);
		return dataset.get(0, "OUTSTR").toString();
	}

	public IData qryWideUserIVRInfo(IData data) throws Exception
	{
		IData returnData = new DataMap();
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		String serial_number = data.getString("SERIAL_NUMBER");
		data.put("SERIAL_NUMBER", "KD_" + data.getString("SERIAL_NUMBER"));
		IDataset kduserinfo = new DatasetList();
		IData userinfo = new DataMap();
		kduserinfo = UserInfoQry.getWideUserIVRInfoBySN(data);
		userinfo = UserInfoQry.getUserInfoBySN(serial_number);

		if (IDataUtil.isEmpty(userinfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		Qry360InfoDAO dao = new Qry360InfoDAO();
		if (kduserinfo == null || kduserinfo.size() == 0)
		{
			returnData.put("TAG", "1");// 是否开通宽带：0开通1未开通
		} else
		{
			returnData.put("TAG", "0");// 是否开通宽带：0开通1未开通
			IData param = kduserinfo.getData(0);
			IDataset productInfo = dao.queryWideNetUserProductInfoIVR(param);
			if (IDataUtil.isNotEmpty(productInfo))
			{
				// 宽带产品名称，取客户资料综合查询->宽带业务->宽带信息->产品名称，(开通宽带时必须返回)
				returnData.put("PRODUCT_NAME", productInfo.getData(0).getString("PRODUCT_NAME", ""));

				// //是否有光猫押金：0无 其余数字为押金金额，单位元(开通宽带时必须返回)DEPOSIT
				if ("11".equals(productInfo.getData(0).getString("PRODUCT_MODE", "")))
				{
					IDataset deposit = dao.queryFTTHDepositInfoIVR(userinfo);
					if (IDataUtil.isNotEmpty(deposit))
					{
						returnData.put("DEPOSIT", deposit.getData(0).getString("RSRV_STR2", "0"));
					} else
					{
						returnData.put("DEPOSIT", "0");
					}
				} else
				{
					returnData.put("DEPOSIT", "0");
				}

				// 是否办理宽带包年：0办理1未办理，(开通宽带时必须返回)
				IDataset discntDataset = dao.queryWideNetUserDiscntIVR(param);
				if (IDataUtil.isNotEmpty(discntDataset))
				{
					// 是否办理宽带包年：0办理1未办理，(开通宽带时必须返回)PRODUCT _TAG
					returnData.put("PRODUCT_TAG", "0");
					// 宽带1+活动或包年套餐的开始时间，年月日，(办理宽带1+活动或包年套餐时必须返回)START_DATE
					returnData.put("START_DATE", getTimeStringChange(discntDataset.getData(0).getString("START_DATE", "")));
					// //宽带1+活动或包年套餐的结束时间，年月日，(办理宽带1+活动或包年套餐时必须返回)END_DATE
					returnData.put("END_DATE", getTimeStringChange(discntDataset.getData(0).getString("END_DATE", "")));
					String fee = discntDataset.getData(0).getString("PARA_CODE1", "");
					String package_desc = "包年费用" + fee + "元一次性扣费";
					// 宽带1+活动或包年套餐的资费信息PACKAGE_DESC
					returnData.put("PACKAGE_DESC", package_desc);
				} else
				{
					returnData.put("PRODUCT_TAG", "1");
				}
			}
		}
		// 是否办理宽带1+营销活动：0办理1未办理，(开通宽带时必须返回)SALEACTIVE_TAG
		IDataset active = dao.qryUserSaleActiveInfoIVR(userinfo);
		if (IDataUtil.isNotEmpty(active))
		{
			returnData.put("SALEACTIVE_TAG", "0");
			// 宽带1+活动或包年套餐的开始时间，年月日，(办理宽带1+活动或包年套餐时必须返回)START_DATE
			returnData.put("START_DATE", getTimeStringChange(active.getData(0).getString("RSRV_DATE1", "")));
			// //宽带1+活动或包年套餐的结束时间，年月日，(办理宽带1+活动或包年套餐时必须返回)END_DATE
			returnData.put("END_DATE", getTimeStringChange(active.getData(0).getString("RSRV_DATE2", "")));
			//屏蔽 TD_B_PACKAGE表
			//String msg = active.getData(0).getString("PACKAGE_DESC", "");
			// 约定最低消费**元/月含**M优惠，即每月约定最低消费**元，若消费不到则按**元补收满**元，每月免收宽带功能费用
			// String package_desc = "包年费用" + fee + "元一次性扣费";
			// 宽带1+活动或包年套餐的资费信息PACKAGE_DESC
			String packageId = active.getData(0).getString("PACKAGE_ID");
			OfferCfg offerInfo = OfferCfg.getInstance(packageId, "K");
			String description = offerInfo.getDescription();
			returnData.put("PACKAGE_DESC", description);
		} else
		{
			returnData.put("SALEACTIVE_TAG", "1");
		}

		returnData.put("X_RESULTCODE", "0");
		returnData.put("X_RECORDNUM", "1");
		returnData.put("X_RESULTINFO", "IVR宽带信息查询接口成功");

		return returnData;
	}

	public static String getTimeStringChange(String sysdate) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(sysdate);
		String str = null;
		str = DateFormatUtils.format(date, "yyyy年MM月dd日");
		return str;
	}

	/**
	 * 根据服务号码调用资源接口获取局向Id信息
	 * 
	 * @throws Exception
	 */
	public String getSwitchId(String serialNumber) throws Exception
	{
		String switchId = null;
		IDataset resInfos = ResCall.getMphonecodeInfo(serialNumber);
		if (IDataUtil.isNotEmpty(resInfos))
		{
			IData resInfo = resInfos.getData(0);
			switchId = resInfo.getString("SWITCH_ID");
		}

		return switchId;
	}

	// 转换用户状态
	public String transUserState(String stateCode) throws Exception
	{
		String retUserState = "";
		String userStatus = "";
		if ("0".equals(stateCode) || "N".equals(stateCode))
		{
			userStatus = "00"; // 1 正常 5:qian欠费 8.9
		} else if ("8".equals(stateCode))
		{
			userStatus = "03"; // 预销户
		} else if ("6".equals(stateCode) || "9".equals(stateCode))
		{
			userStatus = "04"; // 4 销户
		} else if ("R".equals(stateCode) || "A".equals(stateCode) || "B".equals(stateCode) || "G".equals(stateCode))
		{
			userStatus = "01"; // 单向停机
		} else if ("1".equals(stateCode) || "2".equals(stateCode) || "3".equals(stateCode) || "4".equals(stateCode) || "5".equals(stateCode) || "7".equals(stateCode) || "E".equals(stateCode) || "F".equals(stateCode) || "I".equals(stateCode) || "S".equals(stateCode))
		{
			userStatus = "02"; // 3 停机
		}

		// 以上是转换用户状态的老逻辑。由于需要将其转换为实名平台要求的状态，对此再转换一次
		if ("00".equals(userStatus))
		{
			retUserState = "1";
		} else if ("03".equals(userStatus))
		{// td_s_servicestate显示statecode=8是欠费预销户 . 这里作为欠费处理
			retUserState = "2";
		} else if ("01".equals(userStatus) || "02".equals(userStatus))
		{
			retUserState = "3";
		} else if ("04".equals(userStatus))
		{
			retUserState = "4";
		} else
		{
			retUserState = "9";
		}
		return retUserState;
	}

	/**
	 * 按实名认主平台要求转换客户星级
	 */
	public String transUserStar(int creditClass)
	{
		switch (creditClass)
		{
		case 0:
			return "00"; // 准星
		case 1:
			return "01"; // 一星
		case 2:
			return "02"; // 二星
		case 3:
			return "03"; // 三星
		case 4:
			return "04"; // 四星
		case 5:
			return "05"; // 普通五星
		case 6:
			return "06"; // 五星金卡
		case 7:
			return "07"; // 五星钻卡
		default:
			return "09";
		}
	}

	/**
	 * 提供给接续左侧展示的接口 查询用户基本信息
	 * 
	 * @param input
	 * @return userInfo
	 * @throws Exception
	 * @author hx
	 */
	public IData getUserInfoForContinue(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String remove_tag = "0";
		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber(remove_tag, serialNumber);// 根据号码取正常用户资料
		IDataset delUser = UserInfoQry.getLatestUserInfosBySerialNumber(serialNumber);// 根据号码取最后销户用户资料
		IDataset acctInfos = AcctInfoQry.qryAcctInfoBySn(serialNumber); // 根据号码取所有正常用户帐户资料

		IData userInfo = new DataMap();
		String typeName = "";
		String userType = "";
		String isGrp = "";
		String isSale = "";

		if (IDataUtil.isEmpty(userInfos) && IDataUtil.isNotEmpty(delUser))
		{
			userInfo.put("STATE", "该用户已销户");

			return userInfo;
		}

		if (IDataUtil.isEmpty(userInfos))
		{
			IData ifLocal = MsisdnInfoQry.getMsisdnBySnNumForCrm(serialNumber);
			if (IDataUtil.isEmpty(ifLocal))
			{
				IData put = new DataMap();
				put.put("SERIAL_NUMBER", serialNumber);
				IDataset localInfo = GetMphoneAddressQry.querySnCity(put);
				userInfo.put("SERIAL_NUMBER", serialNumber);
				userInfo.put("AREA_CODE", "");
				userInfo.put("CITY_NAME", "");
				userInfo.put("PROV_NAME", "");
				if (IDataUtil.isNotEmpty(localInfo))
				{
					userInfo.put("AREA_CODE", localInfo.getData(0).getString("AREA_CODE"));
					userInfo.put("CITY_NAME", localInfo.getData(0).getString("CITY_NAME"));
					userInfo.put("PROV_NAME", localInfo.getData(0).getString("PROV_NAME"));
				}
			}

			return userInfo;
		}

		String userId = userInfos.getData(0).getString("USER_ID");
		String eparchyCode = userInfos.getData(0).getString("EPARCHY_CODE");
		String custId = userInfos.getData(0).getString("CUST_ID");
		IData customerinfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
		String custName = customerinfo.getString("CUST_NAME");

		// 是否是集团用户
		IData group = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
		if (IDataUtil.isNotEmpty(group))
		{
			isGrp = "0";
			if (IDataUtil.isNotEmpty(UserInfoQry.getGrpRole(serialNumber)))
			{
				isGrp = "1";
			}
		} else
		{
			isGrp = "2";
		}

		// 服务状态
		String stateName = "";
		IDataset userSvcInfo = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
		if (IDataUtil.isNotEmpty(userSvcInfo))
		{
			stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode(userSvcInfo.getData(0).getString("SERVICE_ID"), userSvcInfo.getData(0).getString("STATE_CODE"));
		}

		// 实时话费
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IData feeInfo = AcctCall.getOweFeeByUserId(userId);
		String acctBalance = feeInfo.getString("ACCT_BALANCE");
		acctBalance = Double.parseDouble(acctBalance) * 0.01 + "";

		// 查询基本信息当前业务区
		IDataset userCityInfo = Qry360InfoDAO.qryUserCityInfo(param);
		if (IDataUtil.isNotEmpty(userInfos) && IDataUtil.isNotEmpty(userCityInfo))
		{
			userInfo.put("USER_CITY_CODE", userCityInfo.getData(0).getString("CITY_CODE", ""));
		}
		if (IDataUtil.isNotEmpty(userInfos) && IDataUtil.isEmpty(userCityInfo))
		{
			userInfo.put("USER_CITY_CODE", userInfos.getData(0).getString("CITY_CODE", ""));
		}
		// 客户星级
		// IData inparam = new DataMap();
		// IDataset creInfo = CreditCall.getCreditInfo(userId, "0");
		// if (IDataUtil.isNotEmpty(creInfo))
		// {
		// userInfo.put("USER_CLASS",
		// creInfo.getData(0).getString("CREDIT_CLASS"));
		// }
		// else
		// {
		// userInfo.put("USER_CLASS", "00");
		// }

		// 是否参加营销活动 0是 1否
		IDataset saleInfo = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
		if (IDataUtil.isNotEmpty(saleInfo))
		{
			isSale = "0";
		} else
		{
			isSale = "1";
		}

		// 付费类型
		String payModeCode = acctInfos.getData(0).getString("PAY_MODE_CODE", "");

		// 获取用户品牌
		IData brandInfo = TradeInfoBean.getUserProductInfo(userId, "0");
		String bandCode = null;
		if (IDataUtil.isNotEmpty(brandInfo))
		{
			bandCode = brandInfo.getString("BRAND_CODE", "");
		}

		userInfo.put("CUST_NAME", custName);
		userInfo.put("STATE", stateName);
		userInfo.put("EPARCHY_CODE", eparchyCode);
		userInfo.put("ISACTIVE", isSale);
		userInfo.put("ACCT_TYPE", UPayModeInfoQry.getPayModeNameByPayModeCode(payModeCode));
		userInfo.put("ISGRP", isGrp);
		userInfo.put("ACCT_BALANCE", acctBalance);
		userInfo.put("BRAND_CODE", bandCode);
		userInfo.put("CUST_ID", custId);
		userInfo.put("BIZ_AREA", userInfos.getData(0).getString("CITY_CODE", ""));

		String city_code = userInfos.getData(0).getString("CITY_CODE", "");
		userInfo.put("AREA_NAME", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", city_code));
		// userInfo.put("BRAND", StaticUtil.getStaticValue(getVisit(),
		// "TD_S_BRAND", "BRAND_CODE", "BRAND",
		// brandInfo.getString("BRAND_CODE","")));
		userInfo.put("BRAND", UpcCall.queryBrandNameByChaVal(brandInfo.getString("BRAND_CODE", "")));

		return userInfo;
	}

	// 已开业务查询，服务+优惠+平台信息，多了INST_ID
	public IDataset QueryUserUsingBizOpenInfo(IData input) throws Exception
	{

		IDataset rets = new DatasetList();

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

		IDataset users = UserInfoQry.getUserInfoBySn(serialNumber, "0", "00", null);
		if (IDataUtil.isEmpty(users))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}

		String userId = users.getData(0).getString("USER_ID");
		String userIdA = "-1";

		String curdate = SysDateMgr.date2String(java.util.Calendar.getInstance().getTime(), SysDateMgr.PATTERN_STAND);

		IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserIdNew(userId);
		IDataset userDiscnts2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			IData userDiscnt2 = null;
			for (int i = 0; i < userDiscnts.size(); i++)
			{
				userDiscnt2 = new DataMap();
				IData userDiscnt = userDiscnts.getData(i);
				userDiscnt2.put("BUSINESS_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscnt.getString("DISCNT_CODE")));
				userDiscnt2.put("START_DATE", userDiscnt.getString("START_DATE"));
				userDiscnt2.put("END_DATE", userDiscnt.getString("END_DATE"));
				userDiscnt2.put("INST_ID", userDiscnt.getString("INST_ID"));
				userDiscnt2.put("BUSINESS_TYPE", "1");
				userDiscnts2.add(userDiscnt2);
			}
		}

		IDataset userSvcs = UserSvcInfoQry.queryUserSvcByUserIdAllNew(userId);
		IDataset userSvcs2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userSvcs))
		{
			IData userSvc2 = null;
			for (int i = 0; i < userSvcs.size(); i++)
			{
				userSvc2 = new DataMap();
				IData userSvc = userSvcs.getData(i);
				userSvc2.put("BUSINESS_NAME", userSvc.getString("SERVICE_NAME"));
				userSvc2.put("START_DATE", userSvc.getString("START_DATE"));
				userSvc2.put("END_DATE", userSvc.getString("END_DATE"));
				userSvc2.put("INST_ID", userSvc.getString("INST_ID"));
				userSvc2.put("BUSINESS_TYPE", "2");
				userSvcs2.add(userSvc2);
			}
		}

		IDataset userPlatsvcs = UserPlatInfoQry.getUserPlatInfoByUserIdNew(userId);
		IDataset userPlatsvcs2 = new DatasetList();
		if (IDataUtil.isNotEmpty(userPlatsvcs))
		{
			IData userPlatsvc2 = null;
			for (int i = 0; i < userPlatsvcs.size(); i++)
			{
				userPlatsvc2 = new DataMap();
				IData userPlatsvc = userPlatsvcs.getData(i);
				// userPlatsvc2.put("BUSINESS_NAME",
				// StaticUtil.getStaticValue(CSBizBean.getVisit(),
				// "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME",
				// userPlatsvc.getString("SERVICE_ID")));
				userPlatsvc2.put("BUSINESS_NAME", UPlatSvcInfoQry.getSvcNameBySvcId(userPlatsvc.getString("SERVICE_ID")));
				userPlatsvc2.put("START_DATE", userPlatsvc.getString("START_DATE"));
				userPlatsvc2.put("END_DATE", userPlatsvc.getString("END_DATE"));
				userPlatsvc2.put("INST_ID", userPlatsvc.getString("INST_ID"));
				userPlatsvc2.put("BUSINESS_TYPE", "3");
				userPlatsvcs2.add(userPlatsvc2);
			}
		}

		rets.addAll(userDiscnts2);
		rets.addAll(userSvcs2);
		rets.addAll(userPlatsvcs2);
		return rets;
	}

	/**
	 * @Description: 一级BOSS移动商城接口1.8-充值号码状态查询接口
	 * @version: v1.0.0
	 * @throws Exception
	 * @author lihb3 20160523
	 */
	public IData queryTelStatus(IData input) throws Exception
	{

		// 查询用户信息
		String serialNumber = IDataUtil.chkParam(input, "IDVALUE");
		IData ucaInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(ucaInfo))
		{
			IDataset userInfo = UserInfoQry.queryUserBySnDestroyLatest(serialNumber);
			if (IDataUtil.isEmpty(userInfo))
			{
				IData result = new DataMap();
				result.put("USER_NAME", "");
				result.put("USER_STATUS", "99"); // 此号码不存在
				result.put("X_RESULTCODE", "0");
				result.put("X_RESULTINFO", "OK");
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				return result;
			}
			ucaInfo = userInfo.getData(0);
		}

		// 转换用户状态
		String userStatus = getIBossUserStateParam(ucaInfo.getString("USER_STATE_CODESET"));

		// 设置返回数据
		IData result = new DataMap();
		result.put("USER_NAME", "");
		result.put("USER_STATUS", userStatus);
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULTINFO", "OK");
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		return result;
	}

	/**
	 * @Description: 一级BOSS移动商城接口1.8-流量购买资格校验接口
	 * @version: v1.0.0
	 * @throws Exception
	 * @author lihb3 20160523
	 */
	public IData queryFlowPaymentQualifi(IData input) throws Exception
	{
		// 查询用户信息
		String serialNumber = IDataUtil.chkParam(input, "IDVALUE");

		// 校验未完工单
		IDataset tradeInfo = TradeInfoQry.CheckIsExistNotBBSSFinishedTrade(serialNumber);
		if (IDataUtil.isNotEmpty(tradeInfo))
		{
			int count = tradeInfo.getData(0).getInt("ROW_COUNT");
			if (count > 0)
			{
				IData idResult = new DataMap();
				idResult.put("VALID_FLAG", "1"); // 0：可以进行下一步流量直充操作； 1：不能进行流量直充
				idResult.put("REASON", "用户有未完工单");
				idResult.put("X_RESULTCODE", "3012");
				idResult.put("X_RESULTINFO", "用户有未完工单");
				idResult.put("X_RSPTYPE", "2");
				idResult.put("X_RSPCODE", "2998");
				idResult.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				return idResult;
			}
		}

		IData ucaInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		IData result = new DataMap();

		if (IDataUtil.isEmpty(ucaInfo))
		{
			IDataset userInfo = UserInfoQry.queryUserBySnDestroyLatest(serialNumber);
			if (IDataUtil.isEmpty(userInfo))
			{
				result.put("VALID_FLAG", "1"); // 0：可以进行下一步流量直充操作； 1：不能进行流量直充
				result.put("REASON", "此号码不存在");
				result.put("X_RESULTCODE", "2A11");
				result.put("X_RESULTINFO", "OK");
				result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				return result;
			}
			ucaInfo = userInfo.getData(0);
		}
		// 现网产品变更需要做实名制较验，帮流量直充较验也加上实名制较验保持一致
		IDataset custInfoIds = UserInfoQry.getUserinfo(serialNumber);
		if (!IDataUtil.isEmpty(custInfoIds) && !custInfoIds.getData(0).get("IS_REAL_NAME").equals("1"))
		{
			result.put("VALID_FLAG", "1"); // 0：可以进行下一步流量直充操作； 1：不能进行流量直充
			result.put("REASON", "此号码为非实名用户，请补录实名信息后方可继续办理");
			result.put("X_RESULTCODE", "2A19");
			result.put("X_RESULTINFO", "OK");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
			return result;
		}

		// 转换用户状态
		String userStatus = getIBossUserStateParam(ucaInfo.getString("USER_STATE_CODESET"));
		if ("00".equals(userStatus))
		{
			result.put("VALID_FLAG", "0");
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", "OK");
			result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		} else
		{
			String reason = "";
			if ("01".equals(userStatus))
			{
				reason = "此号码已单向停机";
			} else if ("02".equals(userStatus))
			{
				reason = "此号码已停机";
			} else if ("03".equals(userStatus))
			{
				reason = "此号码已预销户";
			} else
			{
				reason = "此号码已销户";
			}
			result.put("VALID_FLAG", "1");
			result.put("REASON", reason);
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", "OK");
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
			return result;
		}
		result = flowPaymentCheck(input);
		
		return result;

	}

	 /**
     * @Description: 一级BOSS移动商城接口1.8-流量直充接口
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160523
     */
	public IData flowPaymentCheck(IData input) throws Exception{
		IDataUtil.chkParam(input, "IDVALUE");         // 手机号
		//IDataUtil.chkParam(input, "PAYED");           // 充值流量，流量单位：M
		IDataUtil.chkParam(input, "PRODUCT_NO");      // 流量直充产品编码
		//IDataUtil.chkParam(input, "ORDER_CNT");       // 用户购买的产品数量
		//IDataUtil.chkParam(input, "TRANS_ID");        // 操作流水号
		//IDataUtil.chkParam(input, "UIPBUSIID");       // 业务流水号
		//IDataUtil.chkParam(input, "PAYMENT");         // 订单总金额
		
		IData result = new DataMap();  
		//int counts = Integer.parseInt(input.getString("ORDER_CNT"));
		String discntId = input.getString("PRODUCT_NO");
		String elementTypeCode = "D";
		String modifyTag = "0";
		String serialNumber = input.getString("IDVALUE");
		
		//订购数量限制,各档位流量包每月累计限购/叠加/兑换次数为10次
		IDataset dataset = CommparaInfoQry.getCommparaInfos("CSM","2788",discntId);
		String flowType =null;
		if(IDataUtil.isNotEmpty(dataset)){
			flowType = dataset.getData(0).getString("PARA_CODE4"); 
		}
		if(StringUtils.isNotEmpty(flowType) && "PAY_GIFT_SCORE".contains(flowType)){
			int discntCounts = 0;
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
			List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
			IDataset commparaResults = CommparaInfoQry.getCommparaInfoByParacode4AndAttr("CSM","2788", flowType, null);
			for (DiscntTradeData userDiscnt : userDiscnts ){
	            String discntCode  = userDiscnt.getElementId();
	            for(int i = 0,j = commparaResults.size(); i < j; i++){
	            	if(discntCode.equals(discntId)&&discntCode.equals(commparaResults.getData(i).getString("PARA_CODE1"))){
	            		discntCounts++;
	            		break;
	            	}
	            }	            
			}
			if(discntCounts>=10){
				result.put("VALID_FLAG", "1");
				result.put("REASON", "用户本月拥有的相同档次流量包产品已达到上限10次");
				result.put("X_RESULTCODE", "3022");
		      	result.put("X_RESULTINFO",  "用户本月拥有的相同档次流量包产品已达到上限10次");
		      	result.put("X_RSPTYPE", "2");
				result.put("X_RSPCODE", "2998");
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
				return result;
			}
		}
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("ELEMENT_ID", discntId);
		param.put("ELEMENT_TYPE_CODE", elementTypeCode);
		param.put("MODIFY_TAG", modifyTag);
		param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
		
		try{                          
			IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
			result = results.getData(0);
		} catch (Exception e){
	      	if(null != e.getMessage()){
					result.put("VALID_FLAG", "1");
					result.put("REASON", "用户存在订购某种套餐的限制（业务受限），不允许订购");
					result.put("X_RESULTCODE", "3006");
			      	result.put("X_RESULTINFO",  "用户存在订购某种套餐的限制（业务受限），不允许订购");
			      	result.put("X_RSPTYPE", "2");
					result.put("X_RSPCODE", "2998");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
	      	}
		}
		//将CRM处理结果回写给一级BOSS对账用UOP_CEN1.TO_O_FLOWDIRECT_MOBILEMALL
		result.put("VALID_FLAG", "0");
		result.put("X_RESULTCODE", "0");
      	result.put("X_RESULTINFO", "OK");
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
		return result;
	}
	
	/**
	 * 获取用户状态编码
	 */
	private String getIBossUserStateParam(String param)
	{
		IData userStateData = new DataMap();
		userStateData.put("N", "00");// 信用有效时长开通
		userStateData.put("T", "01");// 骚扰电话半停机
		userStateData.put("0", "00");// 开通
		userStateData.put("1", "02");// 申请停机
		userStateData.put("2", "02");// 挂失停机
		userStateData.put("3", "02");// 并机停机
		userStateData.put("4", "02");// 局方停机
		userStateData.put("5", "02");// 欠费停机
		userStateData.put("6", "04");// 申请销号
		userStateData.put("7", "02");// 高额停机
		userStateData.put("8", "03");// 欠费预销号
		userStateData.put("9", "04");// 欠费销号
		userStateData.put("A", "01");// 欠费半停机
		userStateData.put("B", "01");// 高额半停机
		userStateData.put("E", "02");// 转网销号停机
		userStateData.put("F", "02");// 申请预销停机
		userStateData.put("G", "01");// 申请半停机
		userStateData.put("I", "02");// 申请停机（收月租）

		return userStateData.getString(param);
	}
    /**
     * 获取全网证件类型编码     
     * 跨区补卡 zhongsr
     *
     * 
     */
    private String getIBossPsptTypeParam(String param)
    {
        //将海南个性化定义字典转为平台要求的全网编码
        IData psptTypeData = new DataMap();
        //海南个性化定义字典 来源于td_s_static type_id=TD_S_PASSPORTTYPE2
        psptTypeData.put("0", "00");// 0:本地身份证 -->00:身份证件
        psptTypeData.put("1", "00");// 1:外地身份证-->00:身份证件
        psptTypeData.put("2", "11");// 2:户口本-->11:户口簿
        psptTypeData.put("A", "02");// A:护照-->02:护照
        psptTypeData.put("C", "04");// C:军官证-->04:军官证
        psptTypeData.put("D", "99");// D:单位证明-->99:其他证件
        psptTypeData.put("E", "99");// E:营业执照-->99:其他证件
        psptTypeData.put("G", "99");// G:事业单位法人证书-->99:其他证件
        psptTypeData.put("H", "99");// H:港澳居民回乡证-->99:其他证件
        psptTypeData.put("I", "99");// I:台湾居民回乡证-->99:其他证件
        psptTypeData.put("J", "99");// J:港澳通行证-->99:其他证件
        psptTypeData.put("L", "99");// L:社会团体法人登记证书-->99:其他证件
        psptTypeData.put("M", "99");// M:组织机构代码证-->99:其他证件
        psptTypeData.put("N", "13");// N:台湾居民来往大陆通行证-->13:台湾居民来往大陆通行证
        psptTypeData.put("O", "12");// O:港澳居民来往内地通行证-->12:港澳居民往来内地通行证
        psptTypeData.put("R", "14");// R:外国人永久居留证-->14:外国人永久居留证

        return psptTypeData.getString(param,"99");//其他的转换为99:其他证件
        //全网证件编码中01:VIP卡，05:武装警察身份证，10:临时居民身份证 在海南本地字典没有。若后面添加，不修改代码的话，会转换为99:其他证件
    }
    
    private String getCrmPsptTypeParam(String param)
    {   
        //将海南个性化定义字典转为平台要求的全网编码
        IData psptTypeData = new DataMap();
        //海南个性化定义字典 来源于td_s_static type_id=TD_S_PASSPORTTYPE2
        psptTypeData.put("00","0" );// 0:本地身份证 <-->00:身份证件
        psptTypeData.put("00","1" );// 1:外地身份证<-->00:身份证件
        psptTypeData.put("11","2" );// 2:户口本<-->11:户口簿
        psptTypeData.put("02","A" );// A:护照<-->02:护照
        psptTypeData.put("04","C" );// C:军官证<-->04:军官证
        psptTypeData.put("99","D" );// D:单位证明<-->99:其他证件
        psptTypeData.put("99","E" );// E:营业执照<-->99:其他证件
        psptTypeData.put("99","G" );// G:事业单位法人证书<-->99:其他证件
        psptTypeData.put("99","H" );// H:港澳居民回乡证<-->99:其他证件
        psptTypeData.put("99","I" );// I:台湾居民回乡证<-->99:其他证件
        psptTypeData.put("99","J" );// J:港澳通行证<-->99:其他证件
        psptTypeData.put("99","L" );// L:社会团体法人登记证书<-->99:其他证件
        psptTypeData.put("99","M" );// M:组织机构代码证<-->99:其他证件
        psptTypeData.put("13","N" );// N:台湾居民来往大陆通行证<-->13:台湾居民来往大陆通行证
        psptTypeData.put("12","O" );// O:港澳居民来往内地通行证<-->12:港澳居民往来内地通行证
        psptTypeData.put("14","R");// R:外国人永久居住身份证-->14:外国人永久居住身份证
        

        return psptTypeData.getString(param,"");//其他的转换为99:其他证件
        //全网证件编码中01:VIP卡，05:武装警察身份证，10:临时居民身份证 在海南本地字典没有。若后面添加，不修改代码的话，会转换为99:其他证件
    }    
    
    /**
     * 获取全网性别编码     
     * 跨区补卡 zhongsr
     *
     */
    private String getIBossSexParam(String param)
    {
        //将海南个性化定义字典转为平台要求的全网编码
        IData sexData = new DataMap();
        //海南个性化定义字典 来源于td_s_static type_id=SEX
        sexData.put("M", "0");// M:男-->0:男
        sexData.put("F", "1");// F:女 -->1:女
        return sexData.getString(param,"2");//其他的转换为2:未知
    }

	/**
	 * 获取婚姻状况编码 跨区补卡 zhongsr
	 */
	private String getIBossMarriageParam(String param)
	{
		// 将海南个性化定义字典转为平台要求的全网编码
		IData marriageData = new DataMap();
		// 海南个性化定义字典 来源于td_s_static type_id=SALECHANCECONTACT_MARRIAGE
		marriageData.put("0", "0");// 0:未婚-->0:未婚
		marriageData.put("1", "1");// 1:已婚 -->1:已婚
		return marriageData.getString(param, "2");// 其他的转换为2:其它
	}

	/**
	 * 获取教育程度编码 跨区补卡 zhongsr
	 */
	private String getIBossEducateDegreeParam(String param)
	{
		// 将海南个性化定义字典转为平台要求的全网编码
		IData educateDegreeData = new DataMap();
		// 海南个性化定义字典 来源于td_s_static type_id=CUSTPERSON_EDUCATEDEGREECODE
		educateDegreeData.put("0", "00");// 0:无-->00:无
		educateDegreeData.put("1", "01");// 1:小学 -->01:小学
		educateDegreeData.put("2", "02");// 2:初中-->02:初中
		educateDegreeData.put("3", "03");// 3:高中 -->03:高中
		educateDegreeData.put("4", "04");// 4:中专 -->04:中专
		educateDegreeData.put("5", "05");// 5:大专 -->05:大专
		educateDegreeData.put("6", "06");// 6:学士-->06:本科
		educateDegreeData.put("7", "07");// 7:硕士-->07:研究生以上
		educateDegreeData.put("8", "07");// 8:博士-->07:研究生以上

		return educateDegreeData.getString(param);
	}

	public IData qryWideUserInspection(IData data) throws Exception
	{
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "INSPECTION_TAG");
		IData returnData = new DataMap();
		String xMsg = "密码校验成功！";
		String xCode = "0";
		if (!"KD_".equals(data.getString("SERIAL_NUMBER").substring(0, 3)))
		{
			data.put("SERIAL_NUMBER", "KD_" + data.getString("SERIAL_NUMBER"));
		}

		IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(data.getString("SERIAL_NUMBER"));
		if (IDataUtil.isNotEmpty(widenetInfos))
		{
			data.put("USER_ID", widenetInfos.getData(0).getString("USER_ID"));
			String mima = widenetInfos.getData(0).getString("RSRV_STR2");
			if (mima.equals("4"))
			{
				xMsg = "校园宽带密码变更无法办理！";
				xCode = "17011001";
				// CSAppException.appError("17011001", "校园宽带密码变更无法办理！");
			} else
			{
				if (data.getString("INSPECTION_TAG", "0").equals("0")) // 校验标识0：宽带密码校验；1宽带身份证校验
				{
					IDataUtil.chkParam(data, "OLDPASSWD");

					IData param = new DataMap();
					param.put("PASSWORD", data.getString("OLDPASSWD"));
					param.put("USER_ID", data.getString("USER_ID"));
					param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER").indexOf("KD_") != -1 ? data.getString("SERIAL_NUMBER").substring(3) : data.getString("SERIAL_NUMBER"));
					IDataset infos = CSAppCall.call("CS.AuthCheckSVC.checkPasswd", param);

					Boolean flag = infos.getData(0).getString("RESULT_CODE") == "0" ? true : false;
					if (!flag)
					{
						xMsg = "用户原密码校验不正确！";
						xCode = "17011011";
						// CSAppException.appError("17011011", "用户原密码校验不正确！");
					}
				} else
				{
					IDataUtil.chkParam(data, "PSPTID");
					IData param = new DataMap();
					String userId = data.getString("USER_ID");

					IDataset infos = CustomerInfoQry.getNormalCustInfoByUserIdPT(userId);
					if (IDataUtil.isNotEmpty(infos))
					{
						if (!infos.getData(0).getString("PSPT_ID", "").equals(data.getString("PSPTID")))
						{
							xMsg = "用户身份证校验不正确！";
							xCode = "17011011";
							// CSAppException.appError("17011011",
							// "用户身份证校验不正确！");
						}
					} else
					{
						xMsg = "获取不到用户身份信息！";
						xCode = "17011002";
						// CSAppException.appError("17011002", "获取不到用户身份信息！");
					}

				}
			}
		} else
		{
			xMsg = "获取不到用户宽带信息！";
			xCode = "17011003";
			// CSAppException.appError("17011003", "获取不到用户宽带信息！");
		}

		returnData.put("X_RESULTCODE", xCode);
		returnData.put("X_RECORDNUM", "1");
		returnData.put("X_RESULTINFO", xMsg);
		return returnData;
	}

	public IData queryOfferByOfferId(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");
		// IDataUtil.chkParam(input, "OFFER_TYPE");
		String offerType = BofConst.ELEMENT_TYPE_CODE_PRODUCT;
		String offerCode = input.getString("PRODUCT_ID", "");
		IData resutls = UpcCall.queryOfferByOfferId(offerType, offerCode, "Y");
		if (IDataUtil.isNotEmpty(resutls))
		{
			resutls.put("PRODUCT_ID", resutls.getString("OFFER_CODE"));
			resutls.put("PRODUCT_NAME", resutls.getString("OFFER_NAME"));

		}
		return resutls;
	}

	public IDataset qryOfferCatalogByOfferId(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");
		// IDataUtil.chkParam(input, "OFFER_TYPE");

		String offerType = BofConst.ELEMENT_TYPE_CODE_PRODUCT;
		String offerCode = input.getString("PRODUCT_ID", "");
		IDataset offerInfos = UpcCall.qryOfferCatalogByOfferId(offerType, offerCode);

		return offerInfos;
	}

	public IDataset qryOfferByOfferIdNameMode(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "PRODUCT_ID");
		// IDataUtil.chkParam(input, "OFFER_TYPE");
		IDataUtil.chkParam(input, "PRODUCT_NAME");
		IDataUtil.chkParam(input, "PRODUCT_MODE");

		String offerType = BofConst.ELEMENT_TYPE_CODE_PRODUCT;
		String offerCode = input.getString("OFFER_CODE", "");
		String name = input.getString("PRODUCT_NAME", "");
		String mode = input.getString("PRODUCT_MODE", "");
		IDataset results = new DatasetList();
		IDataset offerInfos = UpcCall.qryOfferByOfferIdNameMode(offerType, offerCode, name, mode);
		if (IDataUtil.isNotEmpty(offerInfos))
		{
			for (Object obj : offerInfos)
			{
				IData result = new DataMap();
				IData offerInfo = (IData) obj;
				result.put("PRODUCT_ID", offerInfo.getString("OFFER_CODE", ""));
				result.put("PRODUCT_NAME", offerInfo.getString("OFFER_NAME", ""));
				results.add(result);
			}
		}
		return results;
	}

	public String queryServiceNameByServiceId(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERVICE_ID");
		String offerType = BofConst.ELEMENT_TYPE_CODE_SVC;
		String offerCode = input.getString("SERVICE_ID", "");
		IData resutls = UpcCall.queryOfferByOfferId(offerType, offerCode, "Y");
		String serviceName = "";
		if (IDataUtil.isNotEmpty(resutls))
		{
			serviceName = resutls.getString("OFFER_NAME");

		}
		return serviceName;
	}

	public String queryPlatsvcServiceNameByServiceId(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERVICE_ID");
		String offerType = BofConst.ELEMENT_TYPE_CODE_PLATSVC;
		String offerCode = input.getString("SERVICE_ID", "");
		IData resutls = UpcCall.queryOfferByOfferId(offerType, offerCode, "Y");
		String serviceName = "";
		if (IDataUtil.isNotEmpty(resutls))
		{
			serviceName = resutls.getString("OFFER_NAME");

		}
		return serviceName;
	}

	public String querySpInfoNameByServiceId(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SP_CODE");
		String spCode = input.getString("SP_CODE", "");
		IDataset resutls = UpcCall.querySpInfoNameByCond(spCode);
		String spName = "";
		if (IDataUtil.isNotEmpty(resutls))
		{
			spName = resutls.getData(0).getString("SP_NAME");
		}
		return spName;
	}
    public static String encryptMode(String sdata) throws Exception {
    	IDataset keys = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "ENCRYPT_MODE_KEY", null);
    	if(IDataUtil.isEmpty(keys)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"TD_S_COMMPARA中未配置ENCRYPT_MODE_KEY");    		
    	}
    	String skey = keys.getData(0).getString("PARA_CODE1","");
	    byte[] key = new BASE64Decoder().decodeBuffer(skey);
	    byte[] data = sdata.getBytes("UTF-8");
	    Key deskey = null;
	    DESedeKeySpec spec = new DESedeKeySpec(key);
	    SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
	    deskey = keyfactory.generateSecret(spec);
	    Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
	    cipher.init(1, deskey);
	    byte[] bOut = cipher.doFinal(data);
	    return new BASE64Encoder().encode(bOut);
	  }	
    
    public static boolean chkBlackUser(IData data) throws Exception {
    	 IDataset callSets = null;
    	    try {
    	        callSets = AcctCall.qryBlackListByPsptId(data);//调账务接口查黑名单
    	        for (int i = 0; i < callSets.size(); i++) {
    	            IData blackInfo = callSets.getData(i);
    	            String owe_fee = blackInfo.getString("OWE_FEE", "0");
    	            if (owe_fee != null && !"0".equals(owe_fee)) {
    	                return true;
    	            }
    	        }
    	    } catch (Exception e) {
    	    	String strError = "调用黑名单接口报错！";
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
    	    }
    	    return false;
    }

	public IDataset queryUserBaseBusi(IData input) throws Exception{

		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");	
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);       
        if (IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmUserException.CRM_USER_126, serialNumber);
        }

        String userId = userInfo.getString("USER_ID");
        String productId = userInfo.getString("PRODUCT_ID");
        
        IDataset results = new DatasetList();
        IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
        if (IDataUtil.isEmpty(userDiscnts)){
        	return results;
        }
        
        IDataset forceDiscntInfos = PkgElemInfoQry.queryDiscntOfForcePackage(productId);
        IData forceDiscnts = new DataMap();
        if(IDataUtil.isNotEmpty(forceDiscntInfos)){
        	for(int j=0; j<forceDiscntInfos.size();j++){
        		forceDiscnts.put(forceDiscntInfos.getData(j).getString("ELEMENT_ID"), "1");
        	}
        }        
        
        for(int i = 0;i < userDiscnts.size(); i++){
        	IData userDiscnt = userDiscnts.getData(i);
        	String endDate  = userDiscnt.getString("END_DATE");
        	String discntCode = userDiscnt.getString("DISCNT_CODE");
        	if(!StringUtils.equals("-1", userDiscnt.getString("USER_ID_A"))){	//集团成员业务，不展示
        		continue;
        	}
        	
        	if(SysDateMgr.compareTo(endDate, SysDateMgr.END_DATE_FOREVER)<0){ //截止时间小于2050代表非连续包月,不展示
        		continue;
        	}
        	
        	if(StringUtils.isNotEmpty(forceDiscnts.getString(discntCode))){ //主产品下必选元素，不展示
        		continue;
        	}
        	
        	//获取0000是否可查询及基础价格信息
        	IDataset discntInfo = UpcCall.queryOfferChaByOfferCode(discntCode, "D");
        	if(IDataUtil.isEmpty(discntInfo)){	//查不到产商品0000配置信息，不展示
        		continue;
        	}       	
        	String offerName = discntInfo.first().getString("OFFER_NAME");
        	String fee = discntInfo.first().getString("FEE");
        	String cancelTag = discntInfo.first().getString("IS_CANCEL_TAG");
        	if(StringUtils.isEmpty(offerName) || StringUtils.isEmpty(fee) || StringUtils.isEmpty(cancelTag)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "产商品配置错误，产品编码："+ discntCode);
        	}
        	
        	IData result = new DataMap();
        	result.put("ELEMENT_ID", discntCode);
        	result.put("ELEMENT_TYPE_CODE", "D");
        	result.put("ELEMENT_NAME", offerName);
        	result.put("PRICE", fee);
        	result.put("CANCEL_TAG", cancelTag);       	
        	results.add(result);

        }
		return results;
	}
	
	/**移动商城2.8
     * 用户主套餐明细查询
     * @param input
     * @return 当前主套餐明细信息
     * @throws Exception
     */
    public IData queryMainProductDetailInfo(IData input) throws Exception{
    	IDataUtil.chkParam(input,"ID_TYPE");
    	IDataUtil.chkParam(input,"SERIAL_NUMBER");
    	IDataUtil.chkParam(input,"OPR_NUMB");
    	IDataUtil.chkParam(input,"BIZ_TYPE_CODE");
    	IDataUtil.chkParam(input,"IDENT_CODE");
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	IDataset planQryInfo = new DatasetList();
    	IData planQryInfoMap = new DataMap();
    	IData result = new DataMap();
    	result.put("RSP_CODE", "0000");
    	result.put("RSP_DESC", "查询成功！");
    	result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	//用户身份凭证验证
	     try{
			  IData param1 = new DataMap();
	          param1.put("IDENT_CODE", input.getString("IDENT_CODE", ""));
	          param1.put("SERIAL_NUMBER", serialNumber);
			  UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
			  userIdentBean.identAuth(param1);
		 }catch(Exception e){
			 result.put("RSP_CODE", "3018");
			 result.put("RSP_DESC","凭证校验失败："+e.getMessage());
			 result.put("X_RSPTYPE", "2");
			 result.put("X_RSPCODE", "3018");
			 result.put("VALID_FLAG", "1");//鉴权不通过
			 result.put("REASON", "凭证校验失败："+e.getMessage());
			 //planQryInfo.add(result);
	         //return planQryInfo;
			 return result;
		 }
        
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        String userId = uca.getUserId();
        IData mainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
        String productId = mainProductInfo.getString("PRODUCT_ID","");
        String productName = mainProductInfo.getString("PRODUCT_NAME","");
        String brandCode= mainProductInfo.getString("BRAND_CODE","");
        String brandName= mainProductInfo.getString("BRAND_NAME","");
       
        //IDataset offerData = UpcCall.queryOfferPriceRelPriceByOfferId(productId, "P");
        IDataset offerData = UpcCall.queryOfferPriceRelPriceByOfferId("P", productId);
        // product_price处理 单位元 保留2小数
        String prodPrice = "0";
        int upcPrice = Integer.parseInt(offerData.getData(0).getString("FEE","0"));// upc查出的fee
    	if(upcPrice % 100 == 0){// 为整数
    		prodPrice = String.valueOf(upcPrice/100);
    	}else{
        	double doublePrice = Double.parseDouble(offerData.getData(0).getString("FEE","0"))/100;
    		prodPrice = String.format("%.2f", doublePrice);
    	}
    	// 时间处理 返回  14位
    	String startTime = mainProductInfo.getString("START_DATE");
    	String endTime = mainProductInfo.getString("END_DATE");
    	if(startTime.indexOf("-")>-1){// yyyy-MM-dd HH:mm:ss  --> yyyyMMddHHmmss
    		Date formateDate = SysDateMgr.string2Date(mainProductInfo.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
            startTime = SysDateMgr.date2String(formateDate, SysDateMgr.PATTERN_STAND_SHORT);//yyyyMMddHHmmss
    	}
    	if(endTime.indexOf("-")>-1){
    		Date formateDate1 = SysDateMgr.string2Date(mainProductInfo.getString("END_DATE"), SysDateMgr.PATTERN_STAND);
    		endTime = SysDateMgr.date2String(formateDate1, SysDateMgr.PATTERN_STAND_SHORT);//yyyyMMddHHmmss
    	}
 		
        /*IData planinfo = new DataMap();
        planinfo.put("USER_BRAND", brandCode);
        planinfo.put("BRAND_NAME", brandName);
        planinfo.put("PRODUCT_ID", productId);
        planinfo.put("PRODUCT_NAME", productName);
        planinfo.put("PRODUCT_PRICE", prodPrice);
        planinfo.put("START_TIME", startTime);
        planinfo.put("END_TIME", endTime);*/
    	if("G001".equals(brandCode)){
    		brandCode = "01";
    		brandName = "全球通";
    	}else if("G002".equals(brandCode)){
    		brandCode = "02";
    		brandName = "神州行";
    	}else if("G010".equals(brandCode)){
    		brandCode = "03";
    		brandName = "动感地带";
    	}else{
    		brandCode = "09";
    		brandName = "其他品牌";
    	}
    	planQryInfoMap.put("USER_BRAND", brandCode);
    	planQryInfoMap.put("BRAND_NAME", brandName);
    	planQryInfoMap.put("PRODUCT_ID", productId);
    	planQryInfoMap.put("PRODUCT_NAME", productName);
    	planQryInfoMap.put("PRODUCT_PRICE", prodPrice);
    	planQryInfoMap.put("START_TIME", startTime);
    	planQryInfoMap.put("END_TIME", endTime);
        
        StringBuilder sBuilder = new StringBuilder(1000);
        IDataset productElements = ProductElementsCache.getProductElements(productId);
        if(IDataUtil.isNotEmpty(productElements))
    	{
        	for (int i = 0; i < productElements.size(); i++)
    		{
        		IData ProductElement = productElements.getData(i);
        		String strElementID = ProductElement.getString("ELEMENT_ID", "");
    			String strElementTypeCode = ProductElement.getString("ELEMENT_TYPE_CODE", "");
    			String strElementForceTag = ProductElement.getString("ELEMENT_FORCE_TAG", "");
    			String strGroupForceTag = ProductElement.getString("PACKAGE_FORCE_TAG", "");
    			//取主产品下的构成必选优惠，或者主产品下必选组的优惠
		        if("D".equals(strElementTypeCode) && ("1".equals(strElementForceTag) || "1".equals(strGroupForceTag)))
				{
		        	IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
		        	if(IDataUtil.isNotEmpty(userDiscnts))
		        	{
			        	for (int j = 0; j < userDiscnts.size(); j++) {
			        		IData userDiscnt = userDiscnts.getData(j);
			        		String discntCode = userDiscnt.getString("DISCNT_CODE", "");
			        		if(strElementID.equals(discntCode)){
			        			sBuilder.append(discntCode).append(",");
	    						break;
			        		}
						}
		        	}
				}
    		}
    	}
        /*IDataset forceDiscntInfos = UpcCall.qryAtomOffersFromGroupByOfferId("P", productId,"D");
        IDataset userAllDis = UserDiscntInfoQry.getAllValidDiscntByUserId(userId,uca.getUserEparchyCode());
        if(IDataUtil.isNotEmpty(userAllDis)){
        	for(int i=0;i<userAllDis.size();i++){
        		String disCode = userAllDis.getData(i).getString("DISCNT_CODE");
    			if(IDataUtil.isNotEmpty(forceDiscntInfos)){
    				for(int j=0;j<forceDiscntInfos.size();j++){
    					String mainDiscntCode = forceDiscntInfos.getData(j).getString("ELEMENT_ID");
    					if(disCode.equals(mainDiscntCode)){
    						sBuilder.append(disCode).append(",");
    						break;
    					}
    				}
    			}
        	}
        }*/
        

        IData map = new DataMap();
        String PRODUCT_OFFERING_ID = "";
        if(StringUtils.isNotBlank(sBuilder)){
        	PRODUCT_OFFERING_ID = sBuilder.substring(0, sBuilder.length()-1).toString();
        }
        logger.debug("========sBuilder===:"+sBuilder);
        map.put("PRODUCT_OFFERING_ID", PRODUCT_OFFERING_ID);        
        IDataset returnList = null;
        try{
        	returnList = AcctCall.productOfferingConfig(map);
        	if(IDataUtil.isEmpty(returnList)){
        		result.put("RSP_CODE", "2998");
            	result.put("X_RSPTYPE", "2");
    			result.put("X_RSPCODE", "2998");
    			result.put("RSP_DESC", "主套餐明细查询AcctCall接口返回集为空！");
    			//planQryInfo.add(result);
    			//return planQryInfo;
    			return result;
        	}
        }catch(Exception e){
        	result.put("RSP_CODE", "2998");
        	result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			result.put("RSP_DESC", e.getMessage());
			//planQryInfo.add(result);
			//return planQryInfo;
			return result;
        }
        
        /*if(IDataUtil.isNotEmpty(returnList)){
        	IDataset planQryInfoList = new DatasetList();
        	for(int i=0;i<returnList.size();i++){
        		IData busiMap = returnList.getData(i);
    	        //busiInfolist.put("TYPE", busiMap.getString("type"));
        		IData busiInfo = new DataMap();
        		IData busiInfoMap = new DataMap();
				IDataset busiInfoList = new DatasetList();
        		
        		busiInfo.put("BUSI_ID", busiMap.getString("busiid",""));
        		busiInfo.put("BUSI_NAME", busiMap.getString("businame",""));
        		busiInfo.put("BUSI_NUM", busiMap.getString("busiNum",""));
        		busiInfo.put("UNIT", busiMap.getString("Unit",""));
        		busiInfo.put("BUSI_PRICE", busiMap.getString("busiprice",""));
        		busiInfoList.add(busiInfo);
        		busiInfoMap.put("TYPE",busiMap.getString("type",""));
        		busiInfoMap.put("BUSI_INFO",busiInfoList);
        		planQryInfoList.add(busiInfoMap);
        	}
        	planQryInfoMap.put("BUSI_INFO_LIST",planQryInfoList);
        	
        }*/
        
        
        if(IDataUtil.isNotEmpty(returnList)){
        	IDataset planQryInfoList = new DatasetList();
        	IDataset busiInfoList_0 = new DatasetList();
        	IDataset busiInfoList_1 = new DatasetList();
        	IDataset busiInfoList_2 = new DatasetList();
        	IDataset busiInfoList_3 = new DatasetList();
        	IDataset busiInfoList_4 = new DatasetList();
        	String type_0 = "";
        	String type_1 = "";
        	String type_2 = "";
        	String type_3 = "";
        	String type_4 = "";
        	
        	for(int i=0;i<returnList.size();i++){
        		IData busiMap = returnList.getData(i);
    	        //busiInfolist.put("TYPE", busiMap.getString("type"));
        		IData busiInfo = new DataMap();
        		//IData busiInfoMap = new DataMap();
				//IDataset busiInfoList = new DatasetList();
        		
        		busiInfo.put("BUSI_ID", busiMap.getString("busiid",""));
        		busiInfo.put("BUSI_NAME", busiMap.getString("businame",""));
        		busiInfo.put("BUSI_NUM", busiMap.getString("busiNum",""));
        		busiInfo.put("UNIT", busiMap.getString("Unit",""));
        		//busiInfo.put("BUSI_PRICE", busiMap.getString("busiprice",""));
        		if("0".equals(busiMap.getString("type",""))){
        			busiInfoList_0.add(busiInfo);
        			type_0 = "0";
        		}
        		if("1".equals(busiMap.getString("type",""))){
        			busiInfoList_1.add(busiInfo);
        			type_1 = "1";
        		}
        		if("2".equals(busiMap.getString("type",""))){
        			busiInfoList_2.add(busiInfo);
        			type_2 = "2";
        		}
        		if("3".equals(busiMap.getString("type",""))){
        			busiInfoList_3.add(busiInfo);
        			type_3 = "3";
        		}
        		if("4".equals(busiMap.getString("type",""))){
        			busiInfoList_4.add(busiInfo);
        			type_4 = "4";
        		}
        		if("Z".equals(busiMap.getString("type",""))){
        			planQryInfoMap.put("PRODUCT_PRICE", busiMap.getString("busiprice",""));
        		}
        		
        	}
        	
        	IData busiInfoMap_0 = new DataMap();
        	IData busiInfoMap_1 = new DataMap();
        	IData busiInfoMap_2 = new DataMap();
        	IData busiInfoMap_3 = new DataMap();
        	IData busiInfoMap_4 = new DataMap();
        	if("0".equals(type_0) && IDataUtil.isNotEmpty(busiInfoList_0)){
        		busiInfoMap_0.put("TYPE",type_0);
        		busiInfoMap_0.put("BUSI_INFO",busiInfoList_0);
        		planQryInfoList.add(busiInfoMap_0);
        	}
        	if("1".equals(type_1) && IDataUtil.isNotEmpty(busiInfoList_1)){
        		busiInfoMap_1.put("TYPE",type_1);
        		busiInfoMap_1.put("BUSI_INFO",busiInfoList_1);
        		planQryInfoList.add(busiInfoMap_1);
        	}
        	if("2".equals(type_2) && IDataUtil.isNotEmpty(busiInfoList_2)){
        		busiInfoMap_2.put("TYPE",type_2);
        		busiInfoMap_2.put("BUSI_INFO",busiInfoList_2);
        		planQryInfoList.add(busiInfoMap_2);
        	}
        	if("3".equals(type_3) && IDataUtil.isNotEmpty(busiInfoList_3)){
        		busiInfoMap_3.put("TYPE",type_3);
        		busiInfoMap_3.put("BUSI_INFO",busiInfoList_3);
        		planQryInfoList.add(busiInfoMap_3);
        	}
        	if("4".equals(type_4) && IDataUtil.isNotEmpty(busiInfoList_4)){
        		busiInfoMap_4.put("TYPE",type_4);
        		busiInfoMap_4.put("BUSI_INFO",busiInfoList_4);
        		planQryInfoList.add(busiInfoMap_4);
        	}
        	planQryInfoMap.put("BUSI_INFO_LIST",planQryInfoList);
        	
        }
        planQryInfo.add(planQryInfoMap);
        
        result.put("PLAN_QRY_INFO", planQryInfo);
        
    	return result;
    }
	
	/**
     * 移动商城2.8 用户关键信息查询接口 add by huangyq
     * @param input
     * @return 入网时间，网龄，全球通标识
     * @throws Exception
     */
    public IDataset queryUserKeyInfo(IData input) throws Exception{
    	IDataUtil.chkParam(input,"ID_TYPE");
    	IDataUtil.chkParam(input,"SERIAL_NUMBER");
    	IDataUtil.chkParam(input,"OPR_NUMB");
    	IDataUtil.chkParam(input,"BIZ_TYPE_CODE");
    	IDataUtil.chkParam(input,"IDENT_CODE");
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	IDataset userInfoRsp = new DatasetList();
    	IData res = new DataMap();
    	//用户身份凭证验证
	     try{
			  IData param1 = new DataMap();
	          param1.put("IDENT_CODE", input.getString("IDENT_CODE", ""));
	          param1.put("SERIAL_NUMBER", serialNumber);
			  UserIdentBean userIdentBean = BeanManager.createBean(UserIdentBean.class);
			  userIdentBean.identAuth(param1);
		 }catch(Exception e){
			 res.put("RSP_CODE", "3018");
			 res.put("RSP_DESC","凭证校验失败："+e.getMessage());
			 res.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			 res.put("X_RSPTYPE", "2");
			 res.put("X_RSPCODE", "3018");
			 res.put("VALID_FLAG", "1");//鉴权不通过
			 res.put("REASON", "凭证校验失败："+e.getMessage());
		     userInfoRsp.add(res);
	         return userInfoRsp;
		 }
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	// 入网时间
    	String inDate = uca.getUser().getInDate();
    	// yyyyMMddHHmmss --》 yyyy-MM-dd HH:mm:ss
    	StringBuilder tmp = new StringBuilder();
    	if(inDate.indexOf("-") < 0){ // 非 yyyy-MM-dd HH:mm:ss 格式
    		tmp.append(inDate.substring(0,4)).append("-").append(inDate.substring(4, 6)).append("-").append(inDate.substring(6, 8));
    		tmp.append(" ").append(inDate.substring(8,10)).append(":").append(inDate.substring(10,12)).append(":").append(inDate.substring(12,14));
    		inDate = tmp.toString();
    	}
        Date formateDate = SysDateMgr.string2Date(inDate, SysDateMgr.PATTERN_STAND);
        String netTime = SysDateMgr.date2String(formateDate, SysDateMgr.PATTERN_STAND_SHORT);//yyyyMMddHHmmss
        
        //网龄
        String netAge = new String();
        if(StringUtils.isNotBlank(inDate)){
        	netAge = SysDateMgr.daysBetween4netAge(inDate, SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        }
        // 集团要求 只返回XX年xx月 
        netAge = netAge.substring(0, netAge.indexOf("月")+1);
        
        //全球通标识  1、全球通银卡 2、全球通金卡  3、全球通白金卡 4、全球通钻石卡（非终身）5、全球通终身钻石卡 6、全球通客户（体验）7、其他非全球通用户
        IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	IDataset roamTag = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GSM_BY_SN", param);
    	String UserGSMState = "7";
    	if(DataUtils.isNotEmpty(roamTag))
    	{
    		UserGSMState = roamTag.getData(0).getString("USER_CLASS","7");
    	}
    	
    	IData result = new DataMap();
    	result.put("RSP_CODE", "0000");
    	result.put("RSP_DESC", "成功！");
    	result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	result.put("USER_BEGIN", netTime);
    	result.put("USER_CHINA_MOBILEAGE", netAge);
    	result.put("USER_GSM_STATE", UserGSMState);
    	userInfoRsp.add(result);
    	
    	return userInfoRsp;
    	
    }
	/**
     *  移动商城 2.8 新增速率查询接口给账管调用
     *    根据号码查询 TI_O_PCC_SUBSCRIBER 表中的限速标记，在compara表中配置限速标记和对应的速率，根据用户当前的限速情况匹配速率并返回。
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryUserSpeed(IData input) throws Exception{
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	IData result = new DataMap();
    	result.put("RSP_CODE", "0000");
    	result.put("RSP_DESC", "成功！");
    	result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	String LimitRate = "";
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        //1根据号码查询  TI_O_PCC_SUBSCRIBER 表中的限速标记
        IData inParam = new DataMap();
		inParam.put("USER_ID", uca.getUserId());
		inParam.put("USRIDENTIFIER", "86"+uca.getSerialNumber());
		inParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		IDataset pccResult= PCCBusinessQry.qryPccOperationTypeForSubscriber(inParam);
		if (IDataUtil.isNotEmpty(pccResult)) {
			//usrStatus 1为解速标识；2、3、4、6、8为限速标识
			String usrStatus = pccResult.getData(0).getString("USR_STATUS", "");
			//execState 处理状态 0-入库、1-处理中、2-处理完成、9-处理失败
			String execState = pccResult.getData(0).getString("EXEC_STATE", "");
			if ("2".equals(execState)){
				// 2查询commpara表中配置的标记对应的速率 匹配后 返回速率
	    		IDataset paramList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "2001", "USER_LIMIT_SPEED", usrStatus);
	    		if(IDataUtil.isNotEmpty(paramList)){
	    			LimitRate = paramList.getData(0).getString("PARA_CODE2"); //2-384k/512k/1Mbps  3-128Kbps  4-2Mbps  6-1Mbps   8-4Mbps
	    		}else{
	    			result.put("RSP_CODE", "2998");
	    	    	result.put("RSP_DESC", "用户限速标记对应速率查询失败,对应速率未配置！");
	    		}
			}
		}        
    	/*IDataset otherInfolist = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "LIMIT_SPEED_STATUS");
    	if(IDataUtil.isNotEmpty(otherInfolist)){
    		String speedTag= otherInfolist.getData(0).getString("RSRV_STR1"); //1：解除限速 ，2：一次限速，3：二次限速
    		
    		// 2查询commpara表中配置的标记对应的速率 匹配后 返回速率
    		IDataset paramList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "2001", "USER_LIMIT_SPEED", speedTag);
    		if(IDataUtil.isNotEmpty(otherInfolist)){
    			LimitRate = paramList.getData(0).getString("PARA_CODE2"); // 1-4Mbps   2-1Mbps  3-128Kbps
    		}else{
    			result.put("RSP_CODE", "2998");
    	    	result.put("RSP_DESC", "用户限速标记对应速率查询失败！");
    		}
    	}else{
    		result.put("RSP_CODE", "2998");
        	result.put("RSP_DESC", "用户限速标记查询失败！");
    	}*/
    	// 用户限速速率
    	result.put("USER_SPEED", LimitRate);
    	
    	return result;
    }
	
    /**
	 * @description
	 * @param @param input
	 * @param @return
	 * @return IData
	 * @author tanzheng
	 * @date 2019年6月11日
	 * @param input
	 * @return
	 */
	public IData qryOrderRelationship(IData input) {
		IData result = new DataMap();
		try {
			String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			IDataUtil.chkParam(input, "QUERY_LIST");
			IDataset queryList = input.getDataset("QUERY_LIST");
			if(queryList==null||queryList.size()<0){
				result.put("X_RESULT_CODE", "2999");
				result.put("X_RESULT_MSG","查询列表QUERY_LIST不可为空");
				return result;
			}
			result.put("X_RESULT_CODE", "0000");
			result.put("X_RESULT_MSG","查询成功");
			IDataset resultList = new DatasetList();
			String userId = UcaDataFactory.getNormalUca(serialNumber).getUserId();
			for(int i=0;i<queryList.size();i++){
				IData data = (IData)queryList.get(i);
				String type = IDataUtil.chkParam(data, "TYPE");
				String code = IDataUtil.chkParam(data, "CODE");
				IDataset userOrder = null ;
				switch (type.charAt(0)) {
				case 'D':
					userOrder = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId, code,null);
					break;
				case 'S':
					userOrder = UserSvcInfoQry.getSvcUserId(userId, code);
					break;
				case 'Z':
					userOrder = UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(userId, code);
					break;
				default:
					break;
				}
				
				if(IDataUtil.isNotEmpty(userOrder)){
					data.put("IS_ORDER", "1");
					
				}else{
					data.put("IS_ORDER", "0");
				}
				resultList.add(data);
			}
			
			result.put("RESULT_LIST", resultList);
			
		} catch (Exception e) {
			result.put("X_RESULT_CODE", "2999");
			result.put("X_RESULT_MSG", e.getMessage());
		}
		return result;
	}
	
	/**
     * REQ201907050031 关于为外部电商开通线上售卡业务的开发需求
     * 号码激活状态查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData QueryUserInfoAcctTag(IData input) throws Exception{
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "查询成功！");
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
    	param.put("REMOVE_TAG", "0");
    	try {
			IDataset userInfos = Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_SN", param);
			if(IDataUtil.isEmpty(userInfos)){
				result.put("X_RESULTCODE", "2005");
				result.put("X_RESULTINFO", "用户不存在！");
				result.put("ACCT_TAG", "-1");
			}else{
				result.put("ACCT_TAG", userInfos.getData(0).getString("ACCT_TAG",""));
			}
		} catch (Exception e) {
			result.put("X_RESULTCODE", "2999");
			result.put("X_RESULTINFO", e.getMessage());
		}
    	return result;
    }
    
    public IData getBlackSerial(IData serialNumber) throws Exception
	{
		
		return Dao.qryByCodeOnlyOne("TF_F_VOLTE_BRANK_USER", "QRE_USER_SERIAL", serialNumber);
	}

	/**
	 * “漫游地查询”业务--客户身份校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkCustBySnPsptId(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "校验成功！");

		try {
			String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
			String psptIdCheck = IDataUtil.chkParam(input, "PSPT_ID");
			//查询调用次数，做成配置模式
//			IDataset roamAreaLogs = Dao.qryByCode("TF_F_ROAM_AREA_LOG", "SEL_BY_SN", input);
//			IDataset LimitCountDataset = CommparaInfoQry.getCommparaAllCol("CSM", "1951", "ROAM_AREA_LIMIT", BizRoute.getRouteId());
//			if (IDataUtil.isNotEmpty(LimitCountDataset))
//			{
//				IData LimitCountData = LimitCountDataset.getData(0);
//				int LimitCount = LimitCountData.getInt("PARA_CODE1");
//				if(roamAreaLogs.size() >= LimitCount){
//					result.put("X_RESULTCODE", "2003");
//					result.put("X_RESULTINFO", "超过查询次数限制！");
//					return result;
//				}
//			}

			if (4 == psptIdCheck.trim().length()) {
				IDataset custs = CustomerInfoQry.queryCustInfoBySN(serialNumber);
				if (IDataUtil.isNotEmpty(custs)) {
					String psptId = custs.getData(0).getString("PSPT_ID", "0");
					String isRealName = custs.getData(0).getString("IS_REAL_NAME", CUST_REALNAME_NO);
					if (CUST_REALNAME_YES.equals(isRealName)) {
						if (psptId.toUpperCase().endsWith(psptIdCheck.toUpperCase())) {
							result.put("X_RESULTCODE", "0000");
							result.put("X_RESULTINFO", "校验成功！");
						} else {
							result.put("X_RESULTCODE", "2002");
							result.put("X_RESULTINFO", "证件号后四位与本号码实名认证信息不一致！");
						}
					} else {
						result.put("X_RESULTCODE", "2001");
						result.put("X_RESULTINFO", "非实名制客户！");
					}
				} else {
					result.put("X_RESULTCODE", "2998");
					result.put("X_RESULTINFO", "获取不到用户身份信息！");
				}
			} else {
				result.put("X_RESULTCODE", "2002");
				result.put("X_RESULTINFO", "证件号后四位与本号码实名认证信息不一致！");
			}

			//记录调用次数
//			IData param = new DataMap();
//			param.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));
//			param.put("IN_MODE_CODE",CSBizBean.getVisit().getInModeCode());
//			param.put("RSRV_STR1",CSBizBean.getVisit().getStaffId());
//			param.put("AUTH_CODE",psptIdCheck);
//			param.put("RESULT_CODE",result.getString("X_RESULTCODE"));
//			param.put("RESULT_INFO",result.getString("X_RESULTINFO"));
//			param.put("START_DATE",SysDateMgr.getSysTime());
//			param.put("END_DATE",SysDateMgr.getLastSecond(SysDateMgr.getTomorrowDate()));
//			Dao.insert("TF_F_ROAM_AREA_LOG", param);

		} catch (Exception e) {
			result.put("X_RESULTCODE", "2998");
			result.put("X_RESULTINFO", e.getMessage());
		}

		return result;
	}

	/**
	 * “漫游地查询”业务--漫游数据查询接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset QueryRoamAreaBySn(IData input) throws Exception{
		IDataset results = new DatasetList();
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "操作成功！");
		result.put("TEMPLATE", "");
		result.put("ROAM_AREA1", "");
		result.put("ROAM_AREA2", "");

		try {
			String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");

			//查询调用次数，做成配置模式
			IDataset roamAreaLogs = Dao.qryByCode("TF_F_ROAM_AREA_LOG", "SEL_BY_SN", input);
			IDataset LimitCountDataset = CommparaInfoQry.getCommparaAllCol("CSM", "1951", "ROAM_AREA_LIMIT", BizRoute.getRouteId());
			if (IDataUtil.isNotEmpty(LimitCountDataset))
			{
				IData LimitCountData = LimitCountDataset.getData(0);
				int LimitCount = LimitCountData.getInt("PARA_CODE1");
				if(roamAreaLogs.size() >= LimitCount){
					result.put("X_RESULTCODE", "2003");
					result.put("X_RESULTINFO", "超过查询次数限制！");
					results.add(result);
					return results;
				}
			}

			//对入网不到14天的用户不提供行程证明,增加对应的响应编码2004
			UcaData ucaDataTemp = UcaDataFactory.getNormalUca(serialNumber);
			int day = SysDateMgr.dayInterval(ucaDataTemp.getUser().getOpenDate(), SysDateMgr.getSysDate());
			if (15 > day) {
				result.put("X_RESULTCODE", "2004");
				result.put("X_RESULTINFO", "入网时间不足14日!");
				results.add(result);
				return results;
			}

			//拼接漫游信息
			//取最新的数据展示给客户。即2月16日查询，取20200215日期的数据展示，2月16日还没入库20200215日期的数据时取20200214日期的数据展示。
			IDataset hasData = Dao.qryByCode("TF_F_USER_ROAM_AREA", "SEL_BY_SN", input);
			IDataset roamAreaList = new DatasetList();
			if(IDataUtil.isNotEmpty(hasData) && 100 == hasData.getData(0).getInt("NUMB"))//为了防止我们手工插数据影响判断，改成大于100就是有入库的
			{
				//已入库，取昨天的数据
				roamAreaList = Dao.qryByCode("TF_F_USER_ROAM_AREA", "SEL_BY_SN_1", input);
			}else {
				//未入库，取前天的数据
				roamAreaList = Dao.qryByCode("TF_F_USER_ROAM_AREA", "SEL_BY_SN_2", input);
			}
			//ROAM_AREA1数据
			getRoamAreaData(roamAreaList,result,"1");

			//ROAM_AREA2数据
			if(31 <= day) {//入网时间超过30日的。ROAM_AREA2 才需要查。
				getRoamAreaData(roamAreaList,result,"2");
			}

			//没有数据返回的时候，接口默认返回海南
			String ROAM_AREA1 = result.getString("ROAM_AREA1");
			String ROAM_AREA2 = result.getString("ROAM_AREA2");
			if(StringUtils.isBlank(ROAM_AREA1)){
				result.put("ROAM_AREA1", "海南");
			}
			if(StringUtils.isBlank(ROAM_AREA2) && (30 <= day)){//入网时间超过14天，但不足30日的。ROAM_AREA2 返回空。
				result.put("ROAM_AREA2", "海南");
			}

			//模板TEMPLATE取值
			//带密接模板开关
			String templateId = "3";//3：返回不带密接模板
			IDataset closeContactSwitch = CommparaInfoQry.getCommparaAllCol("CSM", "1951", "CLOSE_CONTACT_SWITCH", BizRoute.getRouteId());
			if (IDataUtil.isNotEmpty(closeContactSwitch)) {
				if("1".equals(closeContactSwitch.getData(0).getString("PARA_CODE1"))){//1表示启用带密接模板
					//TODO 密接判断
					boolean closeContact = true;
					if(!closeContact){
						templateId = "1";//1：返回带密接—非密接人员模板
					} else {
						templateId = "2";//2：返回带密接—密接人员模板
					}
				}
			}

			IDataset templateData = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1951", "TEMPLATE", templateId);
			if (IDataUtil.isNotEmpty(templateData)) {
				result.put("TEMPLATE", templateData.getData(0).getString("PARA_CODE3")==null?"":templateData.getData(0).getString("PARA_CODE3"));
			}

			//记录调用日志
			IData param = new DataMap();
			param.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));
			param.put("IN_MODE_CODE",CSBizBean.getVisit().getInModeCode());
			param.put("RSRV_STR1",CSBizBean.getVisit().getStaffId());
//			param.put("AUTH_CODE",psptIdCheck);
			param.put("RESULT_CODE",result.getString("X_RESULTCODE"));
			param.put("RESULT_INFO",result.getString("X_RESULTINFO"));
			param.put("START_DATE",SysDateMgr.getSysTime());
			param.put("END_DATE",SysDateMgr.getLastSecond(SysDateMgr.getTomorrowDate()));
			Dao.insert("TF_F_ROAM_AREA_LOG", param);

		} catch (Exception e) {
			result.put("X_RESULTCODE", "2998");
			result.put("X_RESULTINFO", e.getMessage());
		}

		results.add(result);
		return results;
	}

	/**
	 * 获取ROAM_AREA数据
	 */
	private void getRoamAreaData(IDataset roamAreaList,IData result,String param) throws Exception{
		if (IDataUtil.isNotEmpty(roamAreaList))
		{
			roamAreaList = DataHelper.filter(roamAreaList, "RSRV_TAG1=" + param);
			roamAreaList = DataHelper.distinct(roamAreaList, "MPROVINCE,MCITY", "");//去重
			String roamArea = "";
			IData roamAreaData = new DataMap();

            /**
             * a、如果只存在一条到访省和到访地市相同记录，则展示该记录的到访地市（即省份名称）；
             * b、如果存在到访省和到访地市相同+到访省和到访地市不同的多条记录，则只展示到访省和到访地市不相同记录中的到访地市（即城市）；
             * c、如果只存在到访省和到访地市不同的一条或多条记录，则按现有逻辑展示到访地市（即城市）。
             */
            for(Iterator<Object> iterator = roamAreaList.iterator(); iterator.hasNext();){
                IData data = (IData) iterator.next();

                String iMprovince = data.getString("MPROVINCE", "").replace(" ", "");
                String iMcity = data.getString("MCITY", "").replace(" ", "");

                boolean flag = false;

                if(iMprovince.equals(iMcity)){
                    for(int i=0;i<roamAreaList.size();i++){
                        String mprovince = roamAreaList.getData(i).getString("MPROVINCE", "").replace(" ", "");
                        String mcity = roamAreaList.getData(i).getString("MCITY", "").replace(" ", "");

                        //如果存在到访省和到访地市相同+到访省和到访地市不同的多条记录，删除到访省和到访地市相同的记录
                        if(iMprovince.equals(mprovince) && !iMcity.equals(mcity)){
                            flag = true;
                            break;
                        }
                    }

                    if (flag){
                        iterator.remove();
                    }
                }
            }

			for(int i=0;i<roamAreaList.size();i++){
				String mprovince = roamAreaList.getData(i).getString("MPROVINCE", "").replace(" ", "");
				String mcity = roamAreaList.getData(i).getString("MCITY", "").replace(" ", "");
//				if(StringUtils.isBlank(mcity)){
//					continue;//过滤城市数据为空的数据
//				}

				if(roamAreaData.containsKey(mprovince)){
					roamAreaData.put(mprovince, roamAreaData.getString(mprovince)+"、"+mcity);
				} else {
					roamAreaData.put(mprovince, mcity);
				}
			}
			logger.debug("========QueryRoamAreaBySn===roamAreaData:"+roamAreaData);

			if (IDataUtil.isNotEmpty(roamAreaData)) {
				String provinceLevelMunicipality = "北京、天津、上海、重庆";
				for (String key : roamAreaData.keySet()) {
                    String cityName = roamAreaData.get(key).toString();

					if(provinceLevelMunicipality.contains(key) || StringUtils.isBlank(cityName) || key.equals(cityName)){//四个直辖市,只返回直辖市的名称就可以了；国际话单只有国家或地区，放在省份字段里面传给我们，没有城市信息。
						roamArea += key + "、";
					}else{
						roamArea += key + "（" + cityName + "）" + "、";
					}
				}
				logger.debug("========QueryRoamAreaBySn===ROAM_AREA"+param + ":"+result.getString("ROAM_AREA"+param));
				result.put("ROAM_AREA"+param, roamArea.substring(0, roamArea.length() - 1));
				logger.debug("========QueryRoamAreaBySn===ROAM_AREA"+param + ":"+result.getString("ROAM_AREA"+param));
			}
		}
	}


	/**
	 * 统一“行程码”查询
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData tyxcQuery(IData input) throws Exception {
		IData result = new DataMap();
		result.put("BIZ_ORDER_RESULT", "0000");
		result.put("RESULT_DESC", "查询成功");
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        IDataset userInfosList = new DatasetList();
        IData userInfos = new DataMap();
        userInfosList.add(userInfos);
		userInfos.put("SERV_NUM","");
        result.put("USER_INFOS", userInfosList);

		try {
			String serialNumber = IDataUtil.chkParam(input, "ID_VALUE");
			String cityName = "-99";//-99：表示无地市、无国家代码位置轨迹数据。
			userInfos.put("SERV_NUM",serialNumber);
			userInfos.put("CITY_NAME", cityName);

			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if(IDataUtil.isEmpty(userInfo))
			{
				userInfos.put("CITY_NAME", "-1");//-1：非移动用户
				logger.debug("========tyxcQuery===result"+result);
				return result;
			}

			//对入网不到14天的用户不提供行程证明，和省内逻辑保持一致;如果是不满15天，你就返回杠99
			int day = SysDateMgr.dayInterval(userInfo.getString("OPEN_DATE"), SysDateMgr.getSysDate());
			if (15 > day) {
                userInfos.put("CITY_NAME", "-3");//-3 :15天内新入网用户
				logger.debug("========tyxcQuery==未满14天=result"+result);
				return result;
			}

            //取最新的数据展示给客户。即2月16日查询，取20200215日期的数据展示，2月16日还没入库20200215日期的数据时取20200214日期的数据展示。
            input.put("SERIAL_NUMBER",serialNumber);
            IDataset hasData = Dao.qryByCode("TF_F_USER_ROAM_AREA", "SEL_BY_SN", input);
            IDataset roamAreaList = new DatasetList();
            if(IDataUtil.isNotEmpty(hasData) && 100 == hasData.getData(0).getInt("NUMB"))//为了防止我们手工插数据影响判断，改成大于100就是有入库的
            {
                //已入库，取昨天的数据
                roamAreaList = Dao.qryByCode("TF_F_USER_ROAM_AREA", "SEL_BY_SN_3", input);
            }else {
                //未入库，取前天的数据
                roamAreaList = Dao.qryByCode("TF_F_USER_ROAM_AREA", "SEL_BY_SN_4", input);
            }

			logger.debug("========tyxcQuery===roamAreaList"+roamAreaList);
            //CITY_NAME数据
			if (IDataUtil.isNotEmpty(roamAreaList))
			{
				roamAreaList = DataHelper.distinct(roamAreaList, "MPROVINCE,MCITY", "");//去重
				String roamArea = "";//总位置轨迹
				String domestic = "";//地市
				String outlands = "";//出入境
				logger.debug("========tyxcQuery===roamAreaList"+roamAreaList);

                /**
                 * a、如果只存在一条到访省和到访地市相同记录，则展示该记录的到访地市（即省份名称）；
                 * b、如果存在到访省和到访地市相同+到访省和到访地市不同的多条记录，则只展示到访省和到访地市不相同记录中的到访地市（即城市）；
                 * c、如果只存在到访省和到访地市不同的一条或多条记录，则按现有逻辑展示到访地市（即城市）。
                 */
                for(Iterator<Object> iterator = roamAreaList.iterator(); iterator.hasNext();){
                    IData data = (IData) iterator.next();

                    String iMprovince = data.getString("MPROVINCE", "").replace(" ", "");
                    String iMcity = data.getString("MCITY", "").replace(" ", "");

                    boolean flag = false;

                    if(iMprovince.equals(iMcity)){
                        for(int i=0;i<roamAreaList.size();i++){
                            String mprovince = roamAreaList.getData(i).getString("MPROVINCE", "").replace(" ", "");
                            String mcity = roamAreaList.getData(i).getString("MCITY", "").replace(" ", "");

                            //如果存在到访省和到访地市相同+到访省和到访地市不同的多条记录，删除到访省和到访地市相同的记录
                            if(iMprovince.equals(mprovince) && !iMcity.equals(mcity)){
                                flag = true;
                                break;
                            }
                        }

                        if (flag){
                            iterator.remove();
                        }
                    }
                }

				for(int i=0;i<roamAreaList.size();i++) {
					String mprovince = roamAreaList.getData(i).getString("MPROVINCE", "").replace(" ", "");
					String mcity = roamAreaList.getData(i).getString("MCITY", "").replace(" ", "");
					String area = "";

					if (StringUtils.isNotBlank(mcity)) {
						//城市字段不为空
						area = mcity;
					} else {
						//城市字段为空，取省份字段判断
						area = mprovince;
					}
					IData areaData = new DataMap();
					areaData.put("ACCT_NAME", area);

					//查询转换表
					IDataset areaDataList = new DatasetList();
					areaDataList = Dao.qryByCode("TF_F_GOV_AREA_COMPARE", "SEL_BY_ACCT_NAME", areaData);
					logger.debug("========tyxcQuery===areaDataList"+areaDataList);
					if (IDataUtil.isNotEmpty(areaDataList)) {
						//类型：地市或出入境
						String type = areaDataList.getData(0).getString("TYPE_CODE", "");
						//编码
						String code = areaDataList.getData(0).getString("GOV_CODE", "");

						if (StringUtils.equals(type, "1") && StringUtils.isNotBlank(code)) {//地市
							domestic += code + ",";
						} else if (StringUtils.equals(type, "2") && StringUtils.isNotBlank(code)) {//出入境
							outlands += code + ",";
						}
					}
				}

				/*
				必须有分隔符
				只有国内的：地市编码1,地市编码2 |
				只有国际的：| 国家代码1,国家代码2
				国内、国际均有：地市编码1,地市编码2 | 国家代码1,国家代码2
				只能判断用户省份：省份编码1 |
				只有国际：| 国家代码1
				*/
				//地市不为空则拼接
				logger.debug("========tyxcQuery===domestic"+domestic);
				logger.debug("========tyxcQuery===outlands"+outlands);

				if (StringUtils.isNotBlank(domestic)) {
                    domestic = distinctString(domestic.substring(0, domestic.length() - 1));//去重
					roamArea += domestic.substring(0, domestic.length() - 1);
				}
				roamArea += "|";//必须有分隔符
				//出入境不为空则拼接
				if (StringUtils.isNotBlank(outlands)) {
                    outlands = distinctString(outlands.substring(0, outlands.length() - 1));//去重
					roamArea += outlands.substring(0, outlands.length() - 1);
				}

				//CITY_NAME数据放入总位置轨迹
				if(!StringUtils.equals(roamArea, "|")){
					userInfos.put("CITY_NAME", roamArea);
				}
				logger.debug("========tyxcQuery===userInfos"+userInfos);
			}

			//没有数据返回的时候，接口默认返回海南
			if(StringUtils.equals(userInfos.getString("CITY_NAME"),"-99")){
				IData areaData = new DataMap();
				areaData.put("ACCT_NAME", "海南");

				//查询转换表
				IDataset areaDataList = new DatasetList();
				areaDataList = Dao.qryByCode("TF_F_GOV_AREA_COMPARE", "SEL_BY_ACCT_NAME", areaData);
				if (IDataUtil.isNotEmpty(areaDataList)) {
					userInfos.put("CITY_NAME", areaDataList.getData(0).getString("GOV_CODE", "") + "|");
				}
			}

        } catch (Exception e) {
            logger.error("========tyxcQuery===ErrorMessage:"+ e.getMessage());
            userInfos.put("CITY_NAME", "-2");//-2：数据异常
        }

		logger.debug("========tyxcQuery===result"+result);
		return result;
	}


    /**
     * 逗号分隔字符串排重后返回
     *
     * @return
     */
    private String distinctString(String str)
    {
        String newStr = "";

        String[] arr = str.split(",");
        IDataset list = new DatasetList();
        for(int i = 0; i < arr.length; i++){
            String s = arr[i].trim();
            if(!list.contains(s)){
                list.add(s);
            }
        }
        for(Object s : list){
            newStr += (String)s + ",";
        }

        return newStr;
    }

	/**
	 * [SPAM]宽带提速活动_短厅办理前的校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData SPAMCheckLimit(IData input) throws Exception {
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "校验通过！");

        try {
            String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
            result.put("SERIAL_NUMBER", serialNumber);
            logger.debug("========SPAMCheckLimit===serialNumber:"+serialNumber);

            //和校园（校讯通）在网
            IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
            logger.debug("========SPAMCheckLimit===userInfo:" + userInfo);

            if (IDataUtil.isNotEmpty(userInfo)) {
                String userId = userInfo.getString("USER_ID");
                IDataset qryXxtValidProduct =UserProductInfoQry.getXxtValidProduct(userId);
                if (IDataUtil.isEmpty(qryXxtValidProduct)) {
                    logger.debug("========SPAMCheckLimit===和校园（校讯通）在网:"+qryXxtValidProduct);
                    result.put("X_RESULTCODE", "3002");
                    result.put("X_RESULTINFO", "校验不通过！");
                    return result;
                }
            }

            //宽带产品为FTTH 300M以下
            IDataset widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
            if (IDataUtil.isEmpty(widenetInfo))
            {
                logger.debug("========SPAMCheckLimit===非宽带:"+widenetInfo);
                result.put("X_RESULTCODE", "3003");
                result.put("X_RESULTINFO", "校验不通过！");
                return result;
            }
            //非FTTH
            if (!BofConst.WIDENET_TYPE_FTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2"))
                    && !BofConst.WIDENET_TYPE_TTFTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2"))) {
                logger.debug("========SPAMCheckLimit===非FTTH:"+widenetInfo.getData(0).getString("RSRV_STR2"));
                result.put("X_RESULTCODE", "3005");
                result.put("X_RESULTINFO", "校验不通过！");
                return result;
            }
            //非300M以下
            String widenetUserRate = WideNetUtil.getWidenetUserRate("KD_" + serialNumber);//宽带用户速率
            if (Integer.valueOf(widenetUserRate) >= (300 * 1024)){
                logger.debug("========SPAMCheckLimit===非300M以下:"+widenetUserRate);
                result.put("X_RESULTCODE", "3004");
                result.put("X_RESULTINFO", "校验不通过！");
                return result;
            }

            //城区
            String deviceId = widenetInfo.getData(0).getString("RSRV_NUM1");
            IData param = new DataMap();
            param.put("DEVICE_ID",deviceId);
            IDataset rs = CSAppCall.call("PB.AddressManageSvc.queryCityInfo", param);
            if(IDataUtil.isNotEmpty(rs))
            {
                IData data = rs.first();
                if("0".equals(data.getString("status",""))){
                    logger.debug("========SPAMCheckLimit===城区:"+rs);
                    result.put("X_RESULTCODE", "3001");
                    result.put("X_RESULTINFO", "校验不通过！");
                    return result;
                }
            }else {
                logger.debug("========SPAMCheckLimit===城区:"+rs);
                result.put("X_RESULTCODE", "3001");
                result.put("X_RESULTINFO", "校验不通过！");
                return result;
            }


        } catch (Exception e) {
            logger.debug("========SPAMCheckLimit===ErrorMessage:"+ e.getMessage());
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "校验失败！");
        }

		return result;
	}

	/**
	 * 批量号码状态查询接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset QuerySnStatusBySn(IData input)throws Exception
	{
        String snArray = input.getString("SERIAL_NUMBER");
        IDataset returnDatas = new DatasetList();
        if (StringUtils.isNotBlank(snArray)) {
            String[] sn = snArray.split("\\|");
            for (int i = 0;i < sn.length;i ++) {
                IData tempRtn = new DataMap();
                String tempSn = sn[i];
                try {
                    IData tempData = UserInfoQry.getActiveUserBySN(tempSn);
                    if (IDataUtil.isNotEmpty(tempData)) {
                        tempRtn.put("SERIAL_NUMBER",tempSn);
                        // 0--正常 1--已销号
                        tempRtn.put("USER_STATUS","0");
                        tempRtn.put("X_RESULTCODE","0000");
                        tempRtn.put("X_RESULTINFO","成功");
                    }else {
                        tempRtn.put("SERIAL_NUMBER",tempSn);
                        // 0--正常 1--已销号
                        tempRtn.put("USER_STATUS","1");
                        tempRtn.put("X_RESULTCODE","0000");
                        tempRtn.put("X_RESULTINFO","成功");
                    }
                }
                catch (Exception e) {
                    // 控制报错长度
                    String errorInfo = e.getMessage().length() > 200 ? e.getMessage().substring(0,200) : e.getMessage();
                    tempRtn.put("X_RESULTCODE","2998");
                    tempRtn.put("X_RESULTINFO","号码" + tempSn + "查询异常,报错信息:" + errorInfo);
                }
                returnDatas.add(tempRtn);
            }
            return returnDatas;
        }else {
            IData tempRtn = new DataMap();
            tempRtn.put("X_RESULTCODE","2998");
            tempRtn.put("X_RESULTINFO","未传入手机号码，请检查");
            returnDatas.add(tempRtn);
            return returnDatas;
        }
	}
	
	
	
	/**
	 * 根据集团订购实例id 查询三户资料
	 * @param input
	 * @return
	 * @throws Exception
	 */
	
	public IData queryCustInfoByid(IData input) throws Exception
	{
		IData user =new DataMap();
		IData result =new DataMap();
		String Status="0";//成功
		String Msg="SUCCESS";
		try{
			
			IDataUtil.chkParam(input, "EC_CODE");
			IDataUtil.chkParam(input, "PRODUCT_ID");
	        IDataset acctinfoset = AcctInfoQry.qryAcctInfoByProductOrderID(input.getString("EC_CODE"),input.getString("PRODUCT_ID"));
	        
	        if(IDataUtil.isNotEmpty(acctinfoset)){
	        	IData userinfo=acctinfoset.getData(0);
	        	user.put("USER_ID", userinfo.getString("USER_ID"));
	        	user.put("ACCT_ID", userinfo.getString("ACCT_ID"));
	        	user.put("CUST_ID", userinfo.getString("CUST_ID"));
	        	user.put("SERIAL_NUMBER", userinfo.getString("SERIAL_NUMBER"));
	        }else {
	        	Status="2";//无数据
				Msg="无相关三户资料!";
	        }
	        
		}catch(Exception e){
			logger.error("getCustInfoByid==ErrorMessage:"+ e.getMessage());
			Status="1";//失败
			Msg=e.getMessage();
		}
		
		result.put("STATUS", Status);
		result.put("MSG", Msg);
		result.put("USERINFO", user);
		return result;
	}

	
}
