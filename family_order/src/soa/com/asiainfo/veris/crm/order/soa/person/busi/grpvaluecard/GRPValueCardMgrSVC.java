/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.FlowCardCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;

/**
 * @CREATED by gongp@2014-5-13 修改历史 Revision 2014-5-13 上午11:26:18
 */
public class GRPValueCardMgrSVC extends CSBizService {

	private static final long serialVersionUID = -5864192502093904255L;

	/**
	 * 获取流量卡信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-5-13
	 */
	public IDataset getValueCardInfo(IData param) throws Exception {
		GRPValueCardMgrBean bean = (GRPValueCardMgrBean) BeanManager.createBean(GRPValueCardMgrBean.class);

		IData data = bean.getValueCardInfo(param);

		IDataset resultSet = new DatasetList();
		resultSet.add(data);
		return resultSet;
	}

	// 调用流量平台接口(流量卡信息变更)
	public IDataset trafficcardSales(IData input) throws Exception {
		String OPERATE = input.getString("OPERATE", "");
		String CARDNUM = input.getString("CARDNUM", "");
		String EXPIREDATE = input.getString("EXPIREDATE", "");
		String EXCHANGECARDS = input.getString("EXCHANGECARDS", "");
		String GroupId = input.getString("GROUPID", "");

		IData svcData = new DataMap();
		IData contData = new DataMap();
		contData.put("Operate", OPERATE);
		contData.put("CardNum", CARDNUM);
		contData.put("ExpireDate", EXPIREDATE);
		contData.put("ExchangeCards", EXCHANGECARDS);
		contData.put("GroupId", GroupId);
		// contData.put("ProductCode", "HNa37b6bcbcade462783b6309f9aa4a");
		svcData.put("Card", contData);

		IDataset ds = new DatasetList();
		ds.add(FlowCardCall.callHttpFlowPlat(svcData));
		return ds;
	}

	// 调用流量平台接口(充值接口)
	public IDataset rechargecardSales(IData input) throws Exception {
		String PASSWORD = input.getString("PASSWORD", "");
		String CARDNUM = input.getString("CARDNUM", "");
		String MOBILE = input.getString("MOBILE", "");
		String SERIALNUM = input.getString("SERIALNUM", "");

		IData svcData = new DataMap();
		IData contData = new DataMap();
		contData.put("Password", PASSWORD);
		contData.put("CardNum", CARDNUM);
		contData.put("Mobile", MOBILE);
		contData.put("SerialNum", SERIALNUM);
		svcData.put("Card", contData);

		IDataset ds = new DatasetList();
		ds.add(FlowCardCall.callHttpFlowPlat(svcData));
		return ds;
	}

	/**
	 * 校验流量卡集团是否订购集团产品
	 * <p>
	 * Title: qryGrpByGIdAndPId
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-1-5 下午03:43:42
	 */
	public IDataset qryGrpByGIdAndPId(IData param) throws Exception {
		IDataset resultSet = UserGrpInfoQry.qryGrpByGIdAndPId(param);
		return resultSet;
	}

	/**
	 * 根据卡号查询修改优惠台账USER_ID_A
	 * <p>
	 * Title: qryGrpUserIDAByCard
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-1-10 下午04:22:10
	 */
	public static IDataset qryGrpUserIDAByCard(IData param) throws Exception {
		IDataset resultSet = UserGrpInfoQry.qryGrpUserIDAByCard(param);
		return resultSet;
	}

	/**
	 * 流量卡使用查询
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @Author:chenzg
	 * @Date:2017-1-9
	 */
	public IDataset queryGrpValueCardUseInfo(IData param) throws Exception {
		GRPValueCardMgrBean bean = (GRPValueCardMgrBean) BeanManager.createBean(GRPValueCardMgrBean.class);
		IDataset resultSet = bean.queryGrpValueCardUseInfo(param, this.getPagination());
		return resultSet;
	}

	/**
	 * 流量卡查询资源信息
	 * <p>
	 * Title: queryValuecardInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-1-13 上午11:17:50
	 */
	public static IDataset queryValuecardInfo(IData param) throws Exception {
		GRPValueCardMgrBean bean = (GRPValueCardMgrBean) BeanManager.createBean(GRPValueCardMgrBean.class);
		IDataset resultSet = bean.queryValuecardInfo(param);
		return resultSet;
	}
}
