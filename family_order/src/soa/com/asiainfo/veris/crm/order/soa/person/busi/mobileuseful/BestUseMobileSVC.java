package com.asiainfo.veris.crm.order.soa.person.busi.mobileuseful;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.mobileuseful.BestUseMobileBean;
public class BestUseMobileSVC extends CSBizService{

	private static final long serialVersionUID = 3518074013533042087L;

	/**
     * 请求响应
     * 
     */
    public IData responseFeedBack(IData input) throws Exception
    {
        
        BestUseMobileBean bean = BeanManager.createBean(BestUseMobileBean.class);
        return bean.responseFeedBack(input);
    }
    
	
	/**
     * 业务提交
     * 
     */
    public IData createPersonSubscriberMobile(IData input) throws Exception
    {
    	
    	BestUseMobileBean bean = BeanManager.createBean(BestUseMobileBean.class);
    	return bean.createPersonSubscriberMobile(input);
    }
    
    /**一号一终端
 	 * 查询是否申请补换eSIM设备
 	 * @param input
 	 * @return
 	 * @throws Exception
 	 */
     public IData qryApplyReplaceESIM(IData input) throws Exception
     {
     	BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
     	return bestusemobile.qryApplyReplaceESIM(input);
     }
     /**
 	 * 接收能开传入的Profile准备结果通知，提醒营业员提示用户可以
 	 * 登录SM-DP+平台下载profile
 	 * @param input
 	 * @return
 	 * @throws Exception
 	 */
      public IData profilePrepareResult(IData input) throws Exception
      {
        	BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
        	return bestusemobile.profilePrepareResult(input);
       }

	/**
	 * 需求名: 关于下发eSIM独立号码服务（一号一终端）支撑方案的通知
	 * 接口描述：前台检索eSIM设备合法性校验结果
	 * @author by tancs 2019-05-22
	 * @throws Exception
	 */
	public IData queryCheckResult(IData input) throws Exception{
		BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
		return bestusemobile.queryCheckResult(input);
	}

	/**
	 * v1.0
	 * 一号一终端业务
	 * 校验新IMEI是否在一级能开登记了补换eSIM设备申请,
	 * 校验通过后返回新卡数据
	 *-----------------------------update
	 * v2.0
	 * 需求名: 关于下发eSIM独立号码服务（一号一终端）支撑方案的通知
	 * 接口名：校验eSIM设备合法性请求（CIP00125）
	 * 接口描述：用于省公司向一级能力开放平台发起eSIM设备的合法性校验,在V1.0版本接口上修改
	 * @author tancs 2019-05-22
	 */
	public IData verifyIMEI(IData input) throws Exception{
		BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
		return bestusemobile.verifyESIM(input);
	}

	/**TF_F_USER_RES  预留字段2 保存了OLD_EID
       	* @param input
   	 	* @return
   	 	* @throws Exception
   	 	*/
        public IDataset getUserResInfoByUserIdRestype(IData in) throws Exception
        {
          	BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
          	return bestusemobile.getUserResInfoByUserIdRestype(in);
         }
        /**一号一终端
     	 * 向一级能力开放平台发起补换eSIM成功通知或者向能力开放平台同步销号通知
     	 * @param input
     	 * @return
     	 * @throws Exception
     	 */
         public IData noticeChangeCardOrDestroy(IData input) throws Exception
         {
         	BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
         	return bestusemobile.noticeChangeCardOrDestroy(input);
         }

	/**一号一终端
	 * 本接口用于一级能力开放平台向省公司反馈eSIM设备校验结果。
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData eSIMDeviceCheckResult(IData input) throws Exception
	{
		BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
		return bestusemobile.eSIMDeviceCheckResult(input);
	}
	/**一号一终端
	 * 本接口用于一级能开向省公司反馈profile准备请求。
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData prepareProfileRequest(IData input) throws Exception
	{
		BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
		return bestusemobile.prepareProfileRequest(input);
	}

	/**一号一终端
	 * 本接口用于一级能开向省公司反馈profile准备结果及SM-DP+下载地址。
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData prepareProfileResult(IData input) throws Exception
	{
		BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
		return bestusemobile.prepareProfileResult(input);
	}

	/**一号一终端
	 * 本接口用于一级能力开放平台向省公司发起用户登记信息一致性校验请求。
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData userInfoCheckRequest(IData input) throws Exception
	{
		BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
		return bestusemobile.userInfoCheckRequest(input);
	}

	/**
	 * 需求名: 关于下发eSIM独立号码服务（一号一终端）支撑方案的通知
	 * 接口名：用户登记信息一致性校验结果通知（CIP00130）
	 * 接口描述：用于省公司向一级能力开放平台反馈用户登记信息一致性校验结果
	 * @author by tancs 2019-05-22
	 * @throws Exception
	 */
	public IData userInfoCheckResult(IData input) throws Exception{
		BestUseMobileBean bean = BeanManager.createBean(BestUseMobileBean.class);
		return bean.userInfoCheckResult(input);
	}

	/**一号一终端
	 * 用户更换eSIM设备请求（CIP00131）本接口用于一级能力开放平台向省公司发起用户更换eSIM设备请求。
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData changeESIMDeviceRequest(IData input) throws Exception
	{
		BestUseMobileBean bestusemobile = BeanManager.createBean(BestUseMobileBean.class);
		return bestusemobile.changeESIMDeviceRequest(input);
	}

	/**
	 * 需求名: 关于下发eSIM独立号码服务（一号一终端）支撑方案的通知
	 * 接口名：用户更换eSIM设备结果通知（CIP00132）
	 * 接口描述：用于省公司向一级能力开放平台反馈更换eSIM设备处理结果。
	 * @author  by tancs 2019-05-22
	 * @throws Exception
	 */
	public IData changeESIMDeviceResult(IData input) throws Exception{
		BestUseMobileBean bean = BeanManager.createBean(BestUseMobileBean.class);
		return bean.changeESIMDeviceResult(input);
	}
}
