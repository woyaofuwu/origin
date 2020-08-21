package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import org.apache.log4j.Logger;

public class AbilityPlatSVC extends CSBizService {
	static Logger logger = Logger.getLogger(AbilityPlatSVC.class);
	/**
	 * 能力开放平台总接口
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*** 随机短信验证码下发（在一级能力平台做）*/
//	public IData issuedSmsCode(IData Idata) throws Exception
//	{
//		AbilityPlatBean bean=new AbilityPlatBean();
//		return bean.issuedSmsCode(Idata);			
//	}
	/** 
	* 随机短信验证码校验（在一级能力平台做）
    */
//	public IData checkSmsCode(IData Idata) throws Exception
//	{
//		AbilityPlatBean bean=new AbilityPlatBean();
//		return bean.checkSmsCode(Idata);
//	}

	/**
	 * 入网资格校验 
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData checkComeInNet(IData Idata) throws Exception {		 
		AbilityPlatBean bean=new AbilityPlatBean();
		return bean.checkComeInNet(Idata);
	}
	
	/**
	 * 业务办理资格校验
     @throws Exception
	 */
	public IData checkCustomerDoService(IData input) throws Exception {
		AbilityPlatBean bean=new AbilityPlatBean();
		return bean.checkCustomerDoService(input);
	}
		 
	/**
	 * 手机宽带业务办理资格校验*/
	public IData checkSnBroadband(IData Idata) throws Exception
	{
		AbilityPlatBean bean=new AbilityPlatBean();
		return bean.checkSnBroadband(Idata);
	}
	/**
	 * 用户状态查询
	 */
	public IData getUserState(IData Idata) throws Exception
	{
		AbilityPlatBean bean=new AbilityPlatBean();
		return bean.getUserState(Idata);
		
	}
	/**
	 * 用户归属地市查询 */
	public IData getUserEparchyCode(IData Idata) throws Exception
	{
		AbilityPlatBean bean=new AbilityPlatBean();
		return bean.getUserEparchyCode(Idata);
		
	}
	/**
	 * 销售订单信息同步接口*/
    public IData sellOrderSynchro(IData input) throws Exception
    {
    	AbilityPlatBean bean=new AbilityPlatBean();
		return bean.sellOrderSynchro(input);
    }
	
	/**
	 * 退订订单信息同步*/
	public IData refundOrderSynchro(IData data) throws Exception{
		AbilityPlatBean bean=new AbilityPlatBean();
		return bean.refundOrderSynchro(data);		  
	}
	/**
	 * 商品信息同步*/
	public IData goodsInfoSyn(IData Idata) throws Exception
	{
		AbilityPlatBean bean=new AbilityPlatBean();
		return bean.goodsInfoSyn(Idata);		  
	}
	/*** 8.6.9.	已订购业务查询*/
	public IData getOrderedSvc(IData input) throws Exception
	{
	    AbilityPlatBean bean=new AbilityPlatBean();
		return bean.getOrderedSvc(input);	    	
	}
	/*** 8.6.20  查询订单状态*/
	public IData queryOrderStatus(IData input) throws Exception
	{
	    AbilityPlatBean bean=new AbilityPlatBean();
		return bean.queryOrderStatus(input);	    	
	}
	/*** 8.6.17  用户实名制查询*/
    public IData checkUserRealName(IData input) throws Exception
    {
        AbilityPlatBean bean=new AbilityPlatBean();
        return bean.checkUserRealName(input);            
    }
    /**
     * 6.40 属性信息同步
     */
    public IData attrInfoSynchro(IData input) throws Exception {
        AbilityPlatBean  bean = new AbilityPlatBean();
        return bean.attrInfoSynchro(input);
    }
    
    /**
     * 6.41 产品信息同步
     */
    public IData commonProductInfoSynchro(IData input) throws Exception {
        AbilityPlatBean  bean = new AbilityPlatBean();
        return bean.commonProductInfoSynchro(input);
    }
    /**
     * 8.6.27 终端出库/换货
     */
    public IData orderStatusSynchro(IData input) throws Exception {
        AbilityPlatBean  bean = new AbilityPlatBean();
        return bean.orderStatusSynchro(input);
    }
    /**
     *实名制校验
     */
    public IData checkIsRealName(IData input) throws Exception {
		AbilityPlatBean  bean = new AbilityPlatBean();
		return bean.checkIsRealName(input);
	}  
    /**
	 * 	渠道信息同步
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData channelSynchro(IData input) throws Exception {
		AbilityPlatBean  bean = new AbilityPlatBean();
		return bean.channelSynchro(input);
	}

	 /**特定产品订购关系查询
  	 * 查询用户是否存在指定产品的订购关系及生效时间，失效时间。如同一商品存在多条订购关系，
  	 * 按订购时间顺序返回多条。如不存在订购关系，也需返回一条记录。
  	 * 如果查询时入参为一级能开的商品编码时，不限订购渠道，或是省侧与此商品映射的本省商品，
  	 * 如存在订购关系，均认为是存在订购关系，需返回订购记录。
  	 * @param Idata
  	 * @return
  	 * @throws Exception
  	 */
	public IData querySpecGoodsOrder(IData input) throws Exception {
		AbilityPlatBean  bean = new AbilityPlatBean();
		return bean.querySpecGoodsOrder(input);
	}
	   /**
    * @Function: getMobileUserInfo
    * @Description: 用户信息查询
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IDataset 
    */
	public IData getMobileUserInfo(IData input) throws Exception {
		AbilityPlatBean  bean = new AbilityPlatBean();
		IData result = bean.getMobileUserInfo(input);
		return result;
	}
	/**
	 * 和家庭成员查询
	 * 查询用户所在的和家庭业务中的家庭成员信息，主号和副号均可发起查询，返回信息中包含查询用户的记录
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryFamilyMembinfo(IData input) throws Exception {
		AbilityPlatBean  bean = new AbilityPlatBean();
		return bean.queryFamilyMembinfo(input);
	}
    

    public void setTrans(IData input) throws Exception
    {
        if (StringUtils.isNotEmpty(input.getString("SERVICE_NO"))
            && StringUtils.isEmpty(input.getString("SERIAL_NUMBER")))
        {
            input.put("SERIAL_NUMBER", input.getString("SERVICE_NO"));
        }

		if (StringUtils.isNotEmpty(input.getString("serviceNumber"))
				&& StringUtils.isEmpty(input.getString("SERIAL_NUMBER"))) {
			input.put("SERIAL_NUMBER", input.getString("serviceNumber"));
		}
    }

	public IDataset queryDireFlowProdOrdRela(IData input) throws Exception {
		AbilityPlatBean bean = BeanManager.createBean(AbilityPlatBean.class);
		return bean.queryDireFlowProdOrdRela(input);
	}

	/**
	 * 和家固话入网资格校验接口(CIP00115)
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkComeInNetForFixPhone(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.checkComeInNetForFixPhone(input);
	}

	/**
	 * 查询已办理和家固话信息（CIP00116）
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryHFixPhoneInfo(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.queryHFixPhoneInfo(input);
	}

	public IData querySpecMainProductOrder(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.querySpecMainProductOrder(input);
	}
	/**
	 * 一、二级能力平台接口 - 主套餐查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryMainProduct(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.queryMainProduct(input);
	}
	/**
	 * 一、二级能力平台接口 - 统一查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getOrderSvc(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.getOrderSvc(input);
	}
	/**
	 * 一、二级能力平台接口 - 统一退订
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData closeAndOrderTrade(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.closeAndOrderTrade(input);
	}
	/**
	 * 一、二级能力平台接口 - 用户综合信息查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryAllInfo(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.queryAllInfo(input);
	}
	/**
	 * 一、二级能力平台接口 - 客户资料查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryCustomerInfo(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.queryCustomerInfo(input);
	}
	/**
	 * 一、二级能力平台接口 - 用户等级信息查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryUserLevel(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.queryUserLevel(input);
	}
	/**
	 * 一、二级能力平台接口 - 客户星级评分详情查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getStarScoreInfo(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.getStarScoreInfo(input);
	}
	/**
	 * 一、二级能力平台接口 - 主套餐变更内容查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryChangeProductInfo(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.queryChangeProductInfo(input);
	}
	/**
	 * 一、二级能力平台接口 - 4G套餐多终端共享成员查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData qryShareMealMember(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.qryShareMealMember(input);
	}
	/**
	 * 一、二级能力平台接口 - 4G套餐多终端共享成员管理
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData chgShareMealMemberShip(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.chgShareMealMemberShip(input);
	}
	/**
	 * 一、二级能力平台接口 - 服务密码凭证申请
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData certificateRequest(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.certificateRequest(input);
	}
	/**
	 * 一、二级能力平台接口 - 信用分查询
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getUserCreditScore(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.getUserCreditScore(input);
	}
	/**
	 * 一、二级能力平台接口 - 家庭网业务办理
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData familyNetOperate(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.familyNetOperate(input);
	}
	/** CIP00139 全球通标签查询
     * @param input
     * @return
     * @throws Exception
     */
    public IData qryGsmTag(IData input) throws Exception {
    	AbilityPlatBean bean = new AbilityPlatBean();
    	return bean.qryGsmTag(input);
    }
    
    /** CIP00135 IMSI一致性校验
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkImsiIsSame(IData input) throws Exception {
    	AbilityPlatBean bean = new AbilityPlatBean();
    	return bean.checkImsiIsSame(input);
    }
    
	/**
	 * 号卡状态同步接口
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset insertSycnNumberStatus(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.insertSycnNumberStatus(input);
	}
	
	/**
	 * 号卡状态同步接口--OAO订单状态为FA数据
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	
	public IDataset sycnNumberStatusByFA(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.sycnNumberStatusByFA(input);
	}
	
	/**
	 * CIP00140 携号转出资格校验接口
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkNpOutMessage(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.checkNpOutMessage(input);
	}

	/**
	 * CIP00142 号码局向查询接口
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData selSNInfo(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.selSNInfo(input);
	}

	/**
	 * CIP00143 ICCID局向查询接口
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData selICCIDInfo(IData input) throws Exception {
		AbilityPlatBean bean = new AbilityPlatBean();
		return bean.selICCIDInfo(input);
	}
	
	/**
	 *权益状态通知接口
	 */
	public IData NotificationOfInterestStatus(IData input) throws Exception {
		AbilityPlatBean  bean = new AbilityPlatBean();
		return bean.NotificationOfInterestStatus(input);
	}
	
	/**
	 * 
	 *订单状态反馈（CIP00081 ）
	 *REQ202004290001关于与全网权益平台对接的需求
	 *by chenyw7
	 *
	 *
	 */
	public IData feedbackOrderStatus(IData input) throws Exception {
		logger.debug("======================AbilityPlatSVC.feedbackOrderStatus  input:"+input);
		AbilityPlatBean  bean = new AbilityPlatBean();
		return bean.feedbackOrderStatus(input);
	}
}
