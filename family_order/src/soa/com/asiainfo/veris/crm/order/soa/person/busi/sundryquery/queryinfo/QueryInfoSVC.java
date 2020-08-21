
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.userident.UserIdentBean;
import org.apache.log4j.Logger;

public class QueryInfoSVC extends CSBizService
{
    static final Logger logger = Logger.getLogger(QueryInfoSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * @Description:弱密码校验
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData checkUserPoorPWD(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.checkUserPoorPWD(input);
    }

    /**
     * @Description:省内机场VIP鉴权
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData CheckVipAirPortRight(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.CheckVipAirPortRight(input);
    }

    /**
     * @Description:火车站VIP服务鉴权
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData CheckVipRailWayRight(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.CheckVipRailWayRight(input);
    }

    /**
     * @Description: 通用查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:08:11 PM
     */
    public IDataset commQuery(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);

        logger.error("SS.QueryInfoSVC.commQuery被调用,入参为: " + input.toString());

        return queryInfoBean.commQuery(input);
    }

    /**
     * @Description: 交易超时查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 19, 2014 14:24:34 AM
     */
    public IData createTradeTimeOut(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.createTradeTimeOut(input);
    }

    /**
     * @Description: 跨区入网服务确认
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData dealTradeConfirmation(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.dealTradeConfirmation(input);
    }

    /**
     * @Description: 跨区入网业务CRM 处理方法
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData dealTradeRep(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.dealTradeRep(input);
    }

    /**
     * @Description: 身份凭证有效期延长
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:14:45 PM
     */
    public IDataset extendIdent(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.extendIdent(input);
    }

    /**
     * @Description: 账户资料查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset getAcctInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getAcctInfo(input);
    }

    /**
     * @Description:查询自动缴费
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData getAutoContractInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getAutoContractInfo(input);
    }

    /**
     * @Description: 用户品牌信息查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData getBrandInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getBrandInfo(input);
    }

    /**
     * @Description:12580综合信息服务查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData getCustInfo12580(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getCustInfo12580(input);
    }

    /**
     * @Description: 查询呼入限制号码
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset getFilteIncomePhoneDs(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.getFilteIncomePhoneDs(input);
    }

    /**
     * @Description: 一级BOSS落地方12580资料查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData getIBoss12580Info(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.get12580Info(input);
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
    	String serialNumber = input.getString("IDVALUE");
    	IDataset res = CommparaInfoQry.queryCommInfos("7777", "IDENT_AUTH_CONFG", input.getString("KIND_ID", ""), input.getString("BIZ_TYPE_CODE", ""));
    	 if (IDataUtil.isNotEmpty(res)){
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        IData result = queryInfoBean.getMobileUserInfo(input);
        
        //移动商城1.5添加
        //1.添加用户实名制信息
        IData queryData = UcaInfoQry.qryCustomerInfoByCustId(result.getString("CUST_ID"));
        if(queryData == null)
        	CSAppException.apperr(CrmUserException.CRM_USER_397);
        result.put("REAL_NAME_INFO", "1".equals(queryData.getString("IS_REAL_NAME")) ? "2" : "1");//2:已登记，1:未登记
        //2.添加用户信誉度信息
        queryData = AcctCall.getUserCreditInfos("0", result.getString("USER_UNIQUE")).getData(0);
        if("-1".equals(queryData.getString("CREDIT_CLASS", "0"))){
        	result.put("STAR_LEVEL", "0");
        }else{
        	result.put("STAR_LEVEL", queryData.getString("CREDIT_CLASS", "0"));
        }
        String starScore = queryData.getString("STAR_SCORE", "0");
        result.put("STAR_SCORE", starScore);
        if(! "0".equals(result.getString("STAR_LEVEL"))){
        	String starTime = queryData.getString("STAR_TIME");
        	result.put("STAR_TIME", starTime);
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.getMsisdnCityInfo(param);
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
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getPlatsvcCountByCond(input);
    }

    /**
     * @Description: SP业务信息查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset GetSPServiceInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.GetSPServiceInfo(input);
    }

    /**
     * @Description: 查询SP服务信息（多个企业代码）
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset getSPServiceInfoMore(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getSPServiceInfoMore(input);
    }

    /**
     * @Description:神鹿交易明细查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset getTrainTradeInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getTrainTradeInfo(input);
    }

    /**
     * @Description: 查询用户归属地州
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 11:24:34 AM
     */
    public IDataset getUserCityInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.getUserCityInfo(input);
    }

    /**
     * 三户接口 给外围
     * 
     * @author huangsl
     */
    public IDataset getUserCustAcct(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.getUserCustAcct(input);
    }

    /**
     * @Description:获取预约未生效的GPRS套餐接口(客服)
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData getUserGprsDiscntIVR(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getUserGprsDiscntIVR(input);
    }

    /**
     * @Description: 查询用户基本信息
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset getUserInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getUserInfo(input);
    }

    /**
     * @Description:查询是否开通支付功能
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData getUserMpay(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getUserMpay(input);
    }

    /**
     * @Description: 查询用户资源信息
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset getUserResource(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.getUserResource(input);
    }

    /**
     * @Description: 查询最受欢迎业务
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset getWelcomeBiz(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getWelcomeBiz(input);
    }

    public IDataset getWidenetDiscntByProduct(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.getWidenetDiscntByProduct(input);
    }

    /**
     * @Description: 鉴权登出
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 20, 2014 9:13:57 AM
     */
    public IDataset logout(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.logout(input);
    }

    /**
     * @Description:梦网查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset oderInfoQry(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.oderInfoQry(input);
    }

    public IDataset operSMSTimeOut(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.operSMSTimeOut(input);
    }

    /**
     * add by ouyk
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAgentUserBackListByCond(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        IDataset rtnDataset = bean.qryAgentUserBackListByCond(input, getPagination());
        return rtnDataset;
    }
    /**
     * add by liquan
     * REQ201608250014新增代理商买断套卡库存清单查询界面和优化相关界面
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAgentUserListByCond(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        IDataset rtnDataset = bean.qryAgentUserListByCond(input, getPagination());
        return rtnDataset;
    }

    /**
     * 查询号码是否能销户，资源号码回收时使用
     * 
     * @param inParam
     *            key = SERIAL_NUMBER
     * @return
     * @throws Exception
     * @CREATE BY LIUKE
     */
    public IData qryDestroyUserInfo(IData inParam) throws Exception
    {

        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        return bean.qryDestroyUserInfo(serialNumber);
    }
    /**
     * 查询号码是否能销户，资源号码回收时使用
     * 
     * @param inParam
     *            key = SERIAL_NUMBER
     * @return
     * @throws Exception
     * @CREATE BY LIUKE
     */
    public IData qryDestroyUserInfo_1(IData inParam) throws Exception
    {

        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        return bean.qryDestroyUserInfo_1(serialNumber);
    }

    /**
     * @Description:梦网和自有业务查询（统一查询退订）
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset QryDreamNetQry(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.QryDreamNetQry(input);
    }

    /**
     * @Description:客服业务信息查询接口
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData qryPlatSvcByAll(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.qryPlatSvcByAll(input);
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.qryVideoMeetingBooking(param);
    }

    /**
     * @Description: 获取产品信息
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 11:25:10 AM
     */
    public IDataset queryAllElementIntf(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryAllElementIntf(input);
    }

    /**
     * @Description: 举报信息查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:10:01 PM
     */

    public IDataset queryBadnessReprotyInfos(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryBadnessReprotyInfos(input);
    }

    /**
     * @Description: 主套餐变更内容查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset queryChangeProductInfoIBoss(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryChangeProductInfoIBoss(input);
    }

    /**
     * @Description: 获取可变更产品列表
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:09:02 PM
     */
    public IDataset queryChangeProductIntf(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryChangeProductIntf(input);
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryChekInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryChekInfo(input);
    }

    /**
     * @Description: 产品资费详细信息介绍
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 11:28:22 AM
     */
    public IDataset queryCrmArgument(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);

        /*
         * IDataset setDataset1=new DatasetList(); setDataset1.add("product"); setDataset1.add("discnt");
         * setDataset1.add("service"); input.put("TYPE_CODE",setDataset1); IDataset setDataset2=new DatasetList();
         * setDataset2.add("10002116"); setDataset2.add("3300"); setDataset2.add("650");
         * input.put("TYPE_ID",setDataset2);
         */

        return queryInfoBean.queryCrmArgument(input);
    }

    /**
     * 数据业务店员积分统一查询功能 xuwb5
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCumuScore(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryCumuScore(input);
    }

    /**
     * @Description: 客户资料查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:08:33 PM
     */
    public IDataset queryCustInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryCustInfo(input);
    }

    /**
     * add by ouyk
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDepartKinds(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.qryDepartKinds(input);
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryDepartMoney(input);
    }

    /**
     * @Description: 根据用户查询平台优惠，供帐管用
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:13:27 PM
     */
    public IDataset queryDiscnt4ActByUserId1(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryDiscnt4ActByUserId1(input);
    }

    /**
     * @Description: 根据用户查询平台优惠，供帐管用
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:13:54 PM
     */
    public IDataset queryDiscnt4ActByUserId2(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryDiscnt4ActByUserId2(input);
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryDiscntByProduct(input);
    }

    /**
     * @Description: 积分兑换规则查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 19, 2014 14:24:34 AM
     */
    public IDataset queryExchangeList(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryExchangeList(input);
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryForcePackageDiscnt(input);
    }

    /**
     * @Description: WAP二期全球通套餐查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:11:58 PM
     */
    public IDataset queryGoToneDiscntInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryGoToneDiscntInfo(input);
    }

    /**
     * @Description: 积分兑换历史查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 14:24:34 AM
     */
    public IDataset queryHistoryList(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryHistoryList(input);
    }

    /**
     * @Description: 积分流量提醒GPRS服务查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:10:07 PM
     */
    public IDataset queryIBossBusiness(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryIBossBusiness(input);
    }

    /**
     * @Description: 电子渠道中奖情况查询接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 11:28:04 AM
     */
    public IDataset queryLotteryWinners(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryLotteryWinners(input);
    }

    /**
     * @Description: 主套餐查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 20, 2014 5:23:09 PM
     */
    public IDataset queryMainProductIBoss(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryMainProductIBoss(input);
    }

    public IDataset queryOperCode(IData data) throws Exception
    {
        String bizTypeCode = data.getString("BIZ_TYPE_CODE");
        IDataset result = StaticUtil.getStaticList("PLATQRY_OPER_CODE_" + bizTypeCode);
        return result;
    }

    /**
     * @Description:查询产品(主套餐)资费介绍信息
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData queryPersonProductInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryPersonProductInfo(input);
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

        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);

        return bean.queryPhoneRegCity(inParam);
    }

    public IDataset queryPlatInfo(IData data) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryPlatInfo(data, getPagination());
    }

    /**
     * @Description: 查询平台服务属性，供帐管用
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:14:00 PM
     */
    public IDataset queryPlatSvcAttrByUserIdSId(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryPlatSvcAttrByUserIdSId(input);
    }

    /**
     * @Description: WAP二期增值业务查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:15:02 PM
     */
    public IDataset queryPlatSvcInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryPlatSvcInfo(input);
    }

    /**
     * @Description: 获取可选产品列表
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 20, 2014 3:14:06 PM
     */
    public IDataset queryProductByBrand(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryProductByBrand(input);
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryProductElement(input);
    }

    /**
     * @Description:获取指定产品下可选包
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset queryProductPackageIntf(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryProductPackageIntf(input);
    }

    /**
     * @Description: 查询产品变更时能办理的VPMN优惠列表
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:09:17 PM
     */
    public IDataset queryProductVPMNDiscntIntf(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryProductVPMNDiscntIntf(input);
    }

    /**
     * @Description: 查询实名制开户数目接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 11:27:14 AM
     */
    public IDataset queryRealNameUserCount(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.getRealNameUserCount(input);
    }

    /**
     * @Description: 已办理营销活动查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset querySaleActiveDiscnt(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.querySaleActiveDiscnt(input);
    }
    
    public IDataset querySaleActiveByPID(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.querySaleActiveByPID(input);
    }

    /**
     * @Description: 用户活动查询/客户购机信息查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:07:58 PM
     */
    public IDataset querySaleActiveInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.querySaleActiveInfo(input);
    }

    /**
     * @Description: 跨区入网服务确认
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset querySaleActiveInfos(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.querySaleActiveInfos(input);
    }
		//新增网厅“查询已办理营销活动”的功能接口
			public IDataset querySaleActiveInfosByNet(IData input) throws Exception
	    {
	        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
	        return bean.querySaleActiveInfosByNet(input);
	    }

    /**
     * 获取该活动包的优惠元素 xuwb5
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySalePackageDiscntByPkg(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.querySalePackageDiscntByPkg(input);
    }

    /**
     * @Description: 积分兑奖话费规则查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 19, 2014 14:24:34 AM
     */
    public IData queryScoreExchagneRule(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        IData scoreInfo = new DataMap();
        IDataset scoreInfos = queryInfoBean.queryScoreExchagneRule(input);
        if (IDataUtil.isNotEmpty(scoreInfos))
        {
            scoreInfo = scoreInfos.getData(0);
        }
        return scoreInfo;
    }

    /**
     * @Description: 2011动感地带秒杀活动查询清单接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:05:31 PM
     */
    public IDataset querySecKillDetails(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.querySecKillDetails(input);
    }

    /**
     * @Description: 手机号码信息查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData querySigninfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.querySigninfo(input);
    }

    /**
     * 查询客户资料记录轨迹
     * 
     * @return
     */
    public IDataset queryStaffOperLogIntf(IData input) throws Exception
    {

        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);

        return queryInfoBean.queryStaffOperLogIntf(input);
    }

    /**
     * @Description: 移动联通转接号码互查
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 11:27:42 AM
     */
    public IDataset queryUnionTransPhone(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryUnionTransPhone(input);
    }

    /**
     * @Description: 用户黑白名单状态查询
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:09:30 PM
     */
    public IDataset queryUserAttr(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryUserAttr(input);
    }

    /**
     * xuwb5 外围接口查询返销接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserCancelTradeIntf(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryUserCancelTradeIntf(input);
    }

    public IDataset queryUserDiscntByType(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryUserDiscntByType(input);
    }

    /**
     * @Description: 统一门户WWW网站用户套餐查询接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 21, 2014 2:52:45 PM
     */
    public IDataset queryUserDiscntInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryUserDiscntInfo(input);
    }

    /**
     * @Description: IVR用户优惠查询（查询用户的约定消费套餐）
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:05:53 PM
     */
    public IDataset queryUserDiscntIVR(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryUserDiscntIVR(input);
    }

    /**
     * @Description: 押金查询，供帐管用
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:14:26 PM
     */
    public IDataset queryUserForegift(IData input) throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        //return queryInfoBean.queryUserForegift(input);
        return queryInfoBean.queryAllUserForegift(input);
    }

    /**
     * @Description: 校园计划用户信息查询
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData QueryUserInfo(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.QueryUserInfo(input);
    }

    /**
     * @Description:查询用户主体优惠的限制天数
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData queryUserMainDiscnt(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryUserMainDiscnt(input);
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
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryUserMainDiscntScope(input);
    }

    /**
     * @Description: 在用业务查询接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:06:48 PM
     */
    public IDataset QueryUserUsingBizInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.QueryUserUsingBizInfo(input);
    }

    /**
     * @Description: 历史业务查询接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:06:21 PM
     */
    public IDataset QueryUserUsingHBizInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.QueryUserUsingHBizInfo(input);
    }

    /**
     * @Description: 设置呼叫转移接口接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jul 7, 2014 4:49:28 PM
     */
    public IDataset queryVEMLAttr(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryVEMLAttr(input);
    }

    /**
     * @Description: 查询大客户可兑换礼品(自动匹配出礼品)
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset queryVipExchangeGifts(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryVipExchangeGifts(input);
    }

    /**
     * @Function: queryVoIp
     * @Description: ITF_CRM_VoIPQry VoIP订购关系查询
     * @param: @param input
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 上午11:23:10 2013-8-6 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-8-6 longtian3 v1.0.0 TODO:
     */
    public IDataset queryVoIp(IData input) throws Exception
    {
        QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
        return bean.queryVoIp(input);
    }

    /**
     * @Description: 宽带产品套餐查询接口
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:06:59 PM
     */
    public IDataset queryWidNetProductInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.queryWidNetProductInfo(input);
    }

    /**
     * 短信白名单 ITF_CRM_RedMember
     * 
     * @param IData
     * @return IDataset
     * @throws Exception
     * @author huanghui@asiainfo-linkage.com
     */
    public IDataset redMemberDeal(IData param) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.redMemberDeal(param);
    }

    /**
     * @Description: 终端库存查询接口
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IDataset saleActiveTerminalQry(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.saleActiveTerminalQry(input);
    }

    /**
     * @Description: 满意度调查
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData saveUserAnswer(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.saveUserAnswer(input);
    }

    /**
     * 客户资料查询及鉴权接口 xuwb5
     */
    public IDataset searchCustInfo(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.searchCustInfo(input);
    }

    /**
     * @Description:小额授信资料查询确认
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jun 23, 2014 14:24:34 AM
     */
    public IData SecSMSAffirm(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.SecSMSAffirm(input);
    }

    @Override
    public final void setTrans(IData input)
    {
        // IBOSS接口入参转换,避免找不到路由  xiekl修改bug 从手机终端过来的也需要转换
        if ("6".equals(getVisit().getInModeCode())  || "IL".equals(getVisit().getInModeCode()))
        {
            if (StringUtils.isNotBlank(input.getString("SERIAL_NUMBER")))
            {
                return;
            }
            else if ("01".equals(input.getString("ROUTETYPE")) && StringUtils.isNotBlank(input.getString("ROUTEVALUE")))
            {
                input.put("SERIAL_NUMBER", input.getString("ROUTEVALUE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("IDVALUE")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("MSISDN")))
            {
                input.put("SERIAL_NUMBER", input.getString("MSISDN"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("ID_VALUE")))
            {
                input.put("SERIAL_NUMBER", input.getString("ID_VALUE"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("IDITEMRANGE")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDITEMRANGE", ""));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("SUB_NUMBER")))
            {
                input.put("SERIAL_NUMBER", input.getString("SUB_NUMBER"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("REC_NUMB")))
            {
                input.put("SERIAL_NUMBER", input.getString("REC_NUMB"));
                return;
            }
        }
        else if ("1".equals(getVisit().getInModeCode()))
        {
            if (StringUtils.isNotBlank(input.getString("MSISDN")))
            {
                input.put("SERIAL_NUMBER", input.getString("MSISDN"));
                return;
            }
            else if (StringUtils.isNotBlank(input.getString("PHONE_CODE")))
            {
                input.put("SERIAL_NUMBER", input.getString("PHONE_CODE"));
                return;
            }
            else
            {
                input.put(Route.ROUTE_EPARCHY_CODE, "0898");
                return;
            }
        }
    }

    /**
     * 小额资金授信用户确认 xuwb5
     */

    public IDataset smallAwardUserInfoAffirm(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.smallAwardUserInfoAffirm(input);
    }

    /**
     * @Function: unifiedAuthentication
     * @Description: QCS_UnifiedAuthentication 统一用户鉴权
     * @param: @param input
     * @param: @return
     * @param: @throws Exception
     * @return：IData
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     */
    public IDataset unifiedAuthentication(IData input) throws Exception
    {
        QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
        IData data = bean.unifiedAuthentication(input);
        return new DatasetList(data);
    }

    /**
     * @Description: 客服签入
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:12:13 PM
     */
    public IDataset userVoucherEnter(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.userVoucherEnter(input);
    }

    /**
     * @Description: 客服签出
     * @param input
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 19, 2014 2:12:29 PM
     */
    public IDataset userVoucherOut(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return queryInfoBean.userVoucherOut(input);
    }
    
    /*******************************************************************
     * 用户在用业务信息查询：一级BOSS移动商城接口
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserBiz4ScoreMall(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
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
        QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
        return bean.qryUserBizInfo4ScoreMall(input);
    }
    
    /**
     * 查询产品元素接口服务
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryProductElementsInft(IData input) throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
    	return queryInfoBean.qryWidenetProductElements(input);
    }
    
    /**
     * 查询用户宽带信息
     * @param input
     * @return
     * @throws Exception
     */
    public IData qryWideUserInfo(IData input) throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
    	
    	return queryInfoBean.qryWideUserInfo(input);
    }
    
	/**
     * 新大陆-用户原卡信息查询接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-06-24 15:59:15
     */
    public IData getSimcardInfoBySn(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        IData result = queryInfoBean.getSimcardInfoBySn(input);
        return result;
    }
    /**
     * 新大陆-补换卡费用查询接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-06-24 15:59:15
     */
    public IData querySimCardPrice(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        IData result = queryInfoBean.querySimCardPrice(input);
        return result;
    }
    
    /**
   	 * 积分标价类交易超时查询
   	 * @author zhouyl
   	 * @param pd
   	 * @param inparam
   	 * @return result
   	 * @throws Exception
   	 */
       public IData createTradeTimeOutN(IData input) throws Exception
       {
           QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
           return bean.createTradeTimeOutN(input);
       }
    
       /**
        * PUK码查询
        * @param input
        * @return
        * @throws Exception
        */
       public IData queryUserPUKCode(IData input) throws Exception{
       	
       	QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
       	return bean.queryUserPUKCode(input);
       }
       
       /**
        * 个人信息查询
        * @param input
        * @return
        * @throws Exception
        */
       public IDataset queryPersonInfo(IData input) throws Exception{
       	
       	QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
       	return bean.queryPersonInfo(input);
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
        * @date: 上午11:37:47 2013-9-13 Modification History: Date Author Version Description
        *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
        */
       public IDataset queryUserGPRS(IData input) throws Exception
       {
           QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
           return bean.queryUserGPRS(input);
       }
       
       /**
        * @Function: queryUserSimBak
        * @Description: ITF_CRM_QueryUserSimBak 备卡信息查询
        * @param: @param data
        * @param: @return
        * @param: @throws Exception
        * @return：IDataset
        * @throws：
        * @version: v1.0.0
        * @author: Administrator
        * @date: 上午11:35:03 2013-9-13 Modification History: Date Author Version Description
        *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
        */
       public IDataset queryUserSimBak(IData input) throws Exception
       {
           QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
           return bean.queryUserSimBak(input);
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
        * @date: 下午02:14:21 2013-9-13 Modification History: Date Author Version Description
        *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
        */
       public IDataset queryUserInterRoamDay(IData input) throws Exception
       {
           QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
           return bean.queryUserInterRoamDay(input);
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
        * @date: 下午02:30:16 2013-9-13 Modification History: Date Author Version Description
        *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
        */
       public IDataset queryUserSaleActive(IData input) throws Exception
       {
           QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
           return bean.queryUserSaleActive(input);
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
        * @date: 下午03:43:09 2013-9-13 Modification History: Date Author Version Description
        *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
        */
       public IDataset queryUserProductInfo(IData input) throws Exception
       {
           QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
           return bean.queryUserProductInfo(input);
       }
	/**
        * 充值卡记录查询
        * @param input
        * @return
        * @throws Exception
        */
       public IDataset valueCardUseQuery(IData input)throws Exception{
    	   	 QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
    	   	 IDataset dataset = bean.valueCardUseQuery(input);
    	   	 
    	   	 return dataset;
       }
       
       /**
        * @Function: starScoreInfoQurey
        * @Description: ITF_CRM_StarScoreInfoQurey一级BOSS移动商城接口1.6-客户星级评定详情查询接口
        * @param: @param data(ROUTE_EPARCHY_CODE、IDTYPE、IDVALUE)
        * @param: @return
        * @return：IData
        * @throws：
        * @version: v1.0.0
        * @author: Administrator
        * @throws Exception
        * @Date:
        * 
        */
       public IDataset starScoreInfoQurey(IData input) throws Exception
       {
           QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
           return bean.starScoreInfoQurey(input);
       }
       
       /*********************************************************************
  	  * 统一查询：移动商城（只查询增值类业务）
  	  * @param pd
  	  * @param data
  	  * @return
  	  * @throws Exception
  	  */
       public IData queryOrderSvc4MM(IData data) throws Exception{
    	   String routeEparchyCode = data.getString(Route.ROUTE_EPARCHY_CODE);
           // 设置路由信息
           String serialNumber = data.getString("IDVALUE");
           if (StringUtils.isNotBlank(serialNumber))
           {
               String route = getMofficeBySN(serialNumber);
               if (StringUtils.isNotBlank(route))
                   routeEparchyCode = route;
           }
           if (StringUtils.isBlank(routeEparchyCode))
           {
               // 711002:无法获取正确的路由信息!"
               CSAppException.apperr(CrmUserException.CRM_USER_476);
           }
           this.setRouteId(routeEparchyCode);

           String bizCodeType = data.getString("BIZ_TYPE_CODE");//渠道编码
           // 身份鉴权
           if ("".equals(bizCodeType) || bizCodeType == null){
   			if ("62".equals(bizCodeType) || "76".equals(bizCodeType)
   					||"63".equals(bizCodeType) || "77".equals(bizCodeType)){
   				IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
   		    	
   		        if (IDataUtil.isEmpty(userInfo))
   		        {
   		        	CSAppException.apperr(CrmUserException.CRM_USER_112);
   		        }
   				
   		        String userid = userInfo.getString("USER_ID");
   		        String identCode = data.getString("IDENT_CODE");
   				//校验客户凭证
   				IDataset dataset = UserIdentInfoQry.searchIdentCode(userid, identCode);
   				if(IDataUtil.isEmpty(dataset)){
   					CSAppException.apperr(CrmUserException.CRM_USER_938);
   				}
   			}
   		}else{
   			IDataset res = CommparaInfoQry.queryCommInfos("7777", "IDENT_AUTH_CONFG", data.getString("KIND_ID", ""), data.getString("BIZ_TYPE_CODE", ""));
   			if (IDataUtil.isNotEmpty(res))
   		    {
   		        data.put("SERIAL_NUMBER", data.getString("IDVALUE", ""));
   		        UserIdentBean identBean = new UserIdentBean();
   		        identBean.identAuth(data);
   		    }
   		}
    	QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);   		
   		IData result =  bean.queryOrderSvc4MM(data);
   		return result;
   	 }
       
       public String getMofficeBySN(String serialNumber) throws Exception
       {
           IData data = new DataMap();
           data.put("SERIAL_NUMBER", serialNumber);

           IDataset tmp = UserInfoQry.getMofficeBySN(data);
           if (IDataUtil.isNotEmpty(tmp))
           {
               IData data2 = tmp.getData(0);
               return data2.getString("EPARCHY_CODE");
           }
           else
           {// 携转号码无moffice信息
               IDataset out = TradeNpQry.getValidTradeNpBySn(serialNumber);
               if (IDataUtil.isNotEmpty(out))
               {
                   return out.getData(0).getString("AREA_CODE");
               }
               else
               {
                   return null;
               }
           }
       }
       
       /**
        * 查询用户宽带信息
        * @param input
        * @return
        * @throws Exception
        */
       public IData qryWideUserIVRInfo(IData input) throws Exception
       {
       	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
       	IData data = queryInfoBean.qryWideUserIVRInfo(input);
       	
       	return data;
       }
       
       /**
        * @Description:1085 用户信息鉴权接口  user_info_auth
        * 为实名认证平台提供
        * @param input
        * @return
        * @throws Exception
        */
       public IData userInfoAuth(IData input) throws Exception
       {
           if (logger.isDebugEnabled())
           {
        	   logger.debug("用户信息鉴权接口>>>" + input.toString());
           }
           QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
           IData result  = new DataMap();
           try {
           		result = bean.userInfoAuth(input);
           } catch (BaseException e) {
	           	String error =  Utility.parseExceptionMessage(e); 
	           	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
	       		result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID","0"));
	       		result.put("RETURN_CODE", "2999");
	    		if (null != errorArray && errorArray.length>1) {
	    			result.put("RETURN_MESSAGE", errorArray[1]);
	    		} else {
	    			result.put("RETURN_MESSAGE", error);
	    		}  
           } 
           catch (Exception ex2) {
	           	ex2.printStackTrace();
	       		result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID","0"));
	       		result.put("RETURN_CODE", "1001");
	       		result.put("RETURN_MESSAGE","系统异常");
           }       	
           return result;
       }
	   
	 /**
	 * @Description: 接续左侧用户基本资料
	 * @param input
	 * @return
	 * @throws Exception
	 * @author: hx
	 * @date: 
	 */
	public IData getUserInfoForContinue(IData input) throws Exception
	{
	    QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
	    return bean.getUserInfoForContinue(input);
	}
	
	/**
     * @Description: 一级BOSS移动商城接口1.8-充值号码状态查询接口
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160523
     */
   	public IData queryTelStatus(IData input) throws Exception {
   		QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
   		return bean.queryTelStatus(input);
   	}
   	
   	/**
     * @Description: 一级BOSS移动商城接口1.8-流量购买资格校验接口
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160523
     */
   	public IData queryFlowPaymentQualifi(IData input) throws Exception {
   		QueryInfoBean bean = BeanManager.createBean(QueryInfoBean.class);
   		return bean.queryFlowPaymentQualifi(input);
   	}
   	
    public IData userInfoAuthForL2F(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("用户信息鉴权接口>>>" + input.toString());
        }
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        IData result  = new DataMap();
        try {
             result = bean.userInfoAuthForL2F(input);
        } catch (BaseException e) {
             e.printStackTrace();
             String error =  Utility.parseExceptionMessage(e); 
             String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
             result.put("BIZ_ORDER_RESULT", "3030");
             if (null != errorArray && errorArray.length>1) {
                 result.put("RESULT_DESC", errorArray[1]);
             } else {
                 result.put("RESULT_DESC", error);
             }
             result.put("X_RSPTYPE", "2");
             result.put("X_RSPCODE", "2998");             
             result.put("X_RSPDESC", error);
        } 
        catch (Exception ex2) {
             ex2.printStackTrace();
             result.put("BIZ_ORDER_RESULT", "3030");
             result.put("RESULT_DESC","系统异常");
             result.put("X_RSPTYPE", "2");
             result.put("X_RSPCODE", "2998");
             result.put("X_RSPDESC", "系统异常");
        }        
        return result;
    }
    
    /**
     * 关于本省10086IVR5号键宽带业务节点优化需求（REQ201612130011）
     * <p>Title: qryWideUserInspection</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param input
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2016-12-29 下午07:11:10
     */
    public IData qryWideUserInspection(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.qryWideUserInspection(input);
    }
    
    
    public IData queryOfferByOfferId(IData input)throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.queryOfferByOfferId(input);
    }
    
    public IDataset qryOfferCatalogByOfferId(IData input)throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.qryOfferCatalogByOfferId(input);
    }
    
    public IDataset qryOfferByOfferIdNameMode(IData input)throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.qryOfferByOfferIdNameMode(input);
    }

    public String queryServiceNameByServiceId(IData input)throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.queryServiceNameByServiceId(input);
    } 
    
    public String queryPlatsvcServiceNameByServiceId(IData input)throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.queryPlatsvcServiceNameByServiceId(input);
    } 
    
    public String querySpInfoNameByServiceId(IData input)throws Exception
    {
    	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.querySpInfoNameByServiceId(input);
    }
    
    /**
     * 关于下发基础业务纳入0000统一查询和退订的支撑系统改造要求的通知
     * @Description: 用户基础业务查询,用于0000展示
     * @author: lihb3
     * @date: 2019-5-10
     */
    public IDataset queryUserBaseBusi(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return bean.queryUserBaseBusi(input);
    }
    
    /**
     * REQ201907050031 关于为外部电商开通线上售卡业务的开发需求
     * 号码激活状态查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData QueryUserInfoAcctTag(IData input) throws Exception{
    	QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.QueryUserInfoAcctTag(input);
    }
	
    /**
     * 
     * @description 用户资料查询接口
     * @param @param 手机号码SERIAL_NUMBER、商品类型（S、D、Z）TYPE、商品编码 CODE
     * @param @return X_RESULT_CODE 0 存在订购关系 2998 不存在订购关系。
     * @param @throws Exception
     * @return IData
     * @author tanzheng
     * @date 2019年6月11日
     * @throws Exception
     */
    public IData qryOrderRelationship(IData input) throws Exception
    {
        QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return queryInfoBean.qryOrderRelationship(input);
    }
    /**
     * 主套餐明细查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryMainProductDetailInfo(IData input) throws Exception{
    	QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryMainProductDetailInfo(input);
    }
    /**
     * 移动商城2.8 用户关键信息查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserKeyInfo(IData input) throws Exception{
    	QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return bean.queryUserKeyInfo(input);
    }
    
    /**
     * 黑名单用户查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public static IData queryBlack(IData input) throws Exception{
    	QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.getBlackSerial(input);
    }
	
	/**
     * 移动商城2.8 速率查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryUserSpeed(IData input) throws Exception{
    	QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        
        return bean.queryUserSpeed(input);
    }
    
    
    //手机号码信息查询接口
    public IData QueryUserInfoBySn(IData input)  throws Exception {
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IData result = new DataMap();
		IDataset numberInfo = ResCall.getMphonecodeInfo(input.getString("SERIAL_NUMBER"));// 查询号码信息
		if (IDataUtil.isNotEmpty(numberInfo)) {
			IDataset commparaSet = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "3297", numberInfo.getData(0).getString("BIND_PRODUCT_ID"), numberInfo.getData(0).getString("BIND_PACKAGE_ID"));
			if (IDataUtil.isNotEmpty(commparaSet))
	        {
				if ("1".equals(numberInfo.getData(0).getString("BEAUTIFUAL_TAG"))) {
					result.put("CONSUME_FEE", commparaSet.getData(0).getString("PARA_CODE2"));
		        	result.put("BEAUTIFUAL_TAG", numberInfo.getData(0).getString("BEAUTIFUAL_TAG"));
		        	result.put("RESERVE_FEE", numberInfo.getData(0).getString("RESERVE_FEE"));
					result.put("X_RESULTCODE", "0000");
					result.put("X_RESULTINFO", "操作成功！");
				}else {
					result.put("BEAUTIFUAL_TAG", numberInfo.getData(0).getString("BEAUTIFUAL_TAG"));
		        	result.put("RESERVE_FEE", numberInfo.getData(0).getString("RESERVE_FEE"));
					result.put("X_RESULTCODE", "0000");
					result.put("X_RESULTINFO", "操作成功！");
				}

	        }
		}

		return result;
	}

    /**
     * “漫游地查询”业务--客户身份校验接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkCustBySnPsptId(IData input) throws Exception{
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.checkCustBySnPsptId(input);
    }

    /**
     * “漫游地查询”业务--漫游数据查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset QueryRoamAreaBySn(IData input) throws Exception{
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.QueryRoamAreaBySn(input);
    }
       
	 /**
     * 统一“行程码”查询
     * @param input
     * @return
     * @throws Exception
     */
    public IData tyxcQuery(IData input) throws Exception{
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.tyxcQuery(input);
    }

    /**
     * [SPAM]宽带提速活动_短厅办理前的校验接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData SPAMCheckLimit(IData input) throws Exception{
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.SPAMCheckLimit(input);
    }

    /**
     * 批量号码状态查询接口
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset QuerySnStatusBySn(IData input) throws Exception{
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.QuerySnStatusBySn(input);
    }

    
    
    /**
     * 根据集团订购实例id 查询三户资料
     * @param input
     * @return
     * @throws Exception
     * 2020-5-11 16:49:32
     * xuzh5
     */
    public IData queryCustInfoByid(IData input) throws Exception
    {
        QueryInfoBean bean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
        return bean.queryCustInfoByid(input);
    }
}
