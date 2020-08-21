package com.asiainfo.veris.crm.order.soa.group.minorec.quickorder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class QuickOrderDataSVC extends GroupOrderService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static IDataset getQuickorderData(IData param) throws Exception
	{
		return QuickOrderDataListBean.getQuickorderData(param);
	}
	
    /**
     ** 获取最新SUB_IBSYSID的数据。 
     * @param param 根据IBSYSID查询 PRODUCT_ID
     * @return IDataset
     * @throws Exception
     * @Date 2019年10月24日
     * @author xieqj 
     */
	public static IDataset getNewQuickorderData(IData param) throws Exception
	{
		return QuickOrderDataListBean.getNewQuickorderData(param);
	}
	
	/**
	 * 
	* @Title: qryCustIdByIbsysid 
	* @Description: 根据ibsysid查询custid 
	* @param param
	* @return
	* @throws Exception IDataset
	* @author zhangzg
	* @date 2019年10月19日上午10:36:51
	 */
	public static IDataset qryCustIdByIbsysid(IData param) throws Exception
	{
	    return QuickOrderDataListBean.qryCustIdByIbsysid(param);
	}
	
	/**
	 * 
	* @Title: updateEopProductInfo 
	* @Description: 客户经理确认节点提交更新信息 TF_B_EOP_PRODUCT 
	* @param USER_ID
	* @param TRADE_ID
	* @param SERIAL_NUMBER
	* @return
	* @throws Exception IDataset
	* @author zhangzg
	* @date 2019年10月31日下午5:56:06
	 */
	public static int updateEopProductInfo(IData param) throws Exception
	{
	    int reesult = QuickOrderDataListBean.updateEopProductInfo(param);
	    reesult += QuickOrderDataListBean.updateUserInfo(param);
        return reesult;
	}
	
	/**
	 * 
	* @Title: updateUserInfo 
	* @Description: 客户经理确认节点提交更新信息 TF_F_USER 
	* @param CONTRACT_ID
	* @return
	* @throws Exception IDataset
	* @author zhangzg
	* @date 2019年10月31日下午5:56:40
	 */
	public static int updateUserInfo(IData param) throws Exception
	{
	    return QuickOrderDataListBean.updateUserInfo(param);
	}
	
	/**
	 * 
	* @Title: qryUserInfoUserIdBySerialNumber 
	* @Description: 查询TF_F_USER表 USER_ID 
	* @param SERIAL_NUMBER
	* @return
	* @throws Exception IDataset
	* @author zhangzg
	* @date 2019年11月7日下午5:17:56
	 */
	public static IDataset qryUserInfoUserIdBySerialNumber(IData param) throws Exception {
	    return QuickOrderDataListBean.qryUserInfoUserIdBySerialNumber(param);
	}

	/**
	 * 
	* @Title: SS.QuickOrderDataSVC.qryAllQuickOrderInfoByIbsysidAndProductId 
	* @Description: 查询现表和历史表
	* @param IBSYSID PRODUCT_ID
	* @return
	* @throws Exception IDataset
	* @author zhangzg
	* @date 2019年11月18日下午7:34:27
	 */
	public static IDataset qryAllQuickOrderInfoByIbsysidAndProductId(IData param) throws Exception {
	    return QuickOrderDataListBean.qryAllQuickOrderInfoByIbsysidAndProductId(param);
	}
}
