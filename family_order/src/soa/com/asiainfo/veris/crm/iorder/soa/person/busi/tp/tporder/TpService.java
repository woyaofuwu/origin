package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporder;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.privm.CheckPriv;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase.TpOrderDetailQuery;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase.TpOrderRuleRouteQry;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase.TpOrderTemplCfgQuery;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderoper.TpOrderOperBean;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts.DealType;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts.TradeMode;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import org.apache.log4j.Logger;

public class TpService extends TpOrderCreate{
	private static Logger logger = Logger.getLogger(TpService.class);
	/**
	 * 甩单流程主方法
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset doThrowOrder(IData input)throws Exception{
		//1、获取作用对象列表
		input.put("SERIAL_NUMBER",input.getString("ACCESS_NUMBER"));
		IDataset datasetObjs = input.getDataset("OBJ_LIST");
		IDataset orderIdList = new DatasetList();//待记录工单关系表的单号  此处保留待用
		if(DataUtils.isNotEmpty(datasetObjs)){
			for(int i = 0; i < datasetObjs.size();i++){
				IData obj = datasetObjs.getData(i);
				//1、获取作用对象或者模板编码
				String withObj = obj.getString("WITH_OBJ");
				String templId = obj.getString("TEMPL_ID");
				
				if(StringUtils.isEmpty(withObj) && StringUtils.isEmpty(templId)){
					//抛出作用对象和模板编码为空异常
					CSAppException.apperr(TpOrderException.TP_ORDER_40002);
				}
				
				//2、获取工单模板
				IData param = new DataMap();
				param.put("TEMPL_ID", templId);
				param.put("WITH_OBJ", withObj);
				IData orderTemplCfg = TpOrderTemplCfgQuery.getOrderTempl(param);
				if(DataUtils.isEmpty(orderTemplCfg)){
					CSAppException.apperr(TpOrderException.TP_ORDER_400020,templId,withObj);
				}

				//3、判断操作员是否存在甩单对应功能页面操作权限
				String menuId = orderTemplCfg.getString("MENU_ID");
				if(StringUtils.isNotEmpty(menuId) && !CheckPriv.checkMenuPermission(getVisit().getStaffId(),menuId)){
					CSAppException.apperr(TpOrderException.TP_ORDER_400026,getVisit().getStaffId(),menuId);
				}

				//4、模板流程处理
				String dealType = orderTemplCfg.getString("DEAL_TYPE");
				if(DealType.auto.equals(dealType)){//走接口自动处理
					String orderId = dealInterface(orderTemplCfg,obj);
					orderIdList.add(orderId);
				}else if(DealType.page.equals(dealType)){//走界面处理
					
				}else if(DealType.manualOper.equals(dealType)){//走人为处理
					
				}else if(DealType.specialType.equals(dealType)){//特殊场景优先走规则路由配置的服务
					IData data =  new DataMap();
					data.put("TEMPL_ID",orderTemplCfg.getString("TEMPL_ID"));
					IData specalProcessCfg = TpOrderRuleRouteQry.getOrderRoute(data);
					String routeAction = specalProcessCfg.getString("ROUTE_ACTION");
					String routeStr = specalProcessCfg.getString("ROUTE_STR");

					param.clear();
					param.put(TpConsts.comKey.serialNumber,input.getString(TpConsts.comKey.serialNumber));
					IData actionAfter = call(routeAction,param);
					boolean flag = actionAfter.getBoolean(routeStr);
					if(!flag){
						String dealAction = actionAfter.getString("DEAL_ACTION");
						if(StringUtils.isNotEmpty(dealAction)){
							orderTemplCfg.put("DEAL_ACTION",dealAction);
						}
						String orderId = dealInterface(orderTemplCfg,obj);
						orderIdList.add(orderId);
					}
				}else {
					CSAppException.apperr(TpOrderException.TP_ORDER_400021,dealType);
				}
			}
		}else {
			CSAppException.apperr(TpOrderException.TP_ORDER_400014);
		}
		return orderIdList;
	}

	/**
	 * 自动化接口处理
	 * @param orderTemplCfg
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private String dealInterface(IData orderTemplCfg,IData input)throws Exception {
		String tradeMode = orderTemplCfg.getString("TRADE_MODE");
		String dealAction = orderTemplCfg.getString("DEAL_ACTION");
		String dealSvc = orderTemplCfg.getString("REQ_BUILDER_SVC");
		String orderId = "";
		if(TradeMode.tradeMode0.equals(tradeMode)){//生成正式业务订单order、trade

		}else if(TradeMode.tradeMode1.equals(tradeMode)){//生成甩单工单
			input.putAll(orderTemplCfg);
			//生成甩单工单前处理甩单工单数据
			IData messageData = call(dealSvc,input);
			if(DataUtils.isNotEmpty(messageData)){
				input.put("MESSAGE",messageData.toString());
				input.put("EVENT_TYPE",orderTemplCfg.getString("WITH_TYPE"));
				input.put("EVENT_OBJ",orderTemplCfg.getString("WITH_OBJ"));
				input.put("CHANNEL_ID",getVisit().getInModeCode());
				input.put("ROUTE_EPARCHY_CODE",getUserEparchyCode());
				orderId = this.orderCreate(input);//甩单工单号

				String svcKey = messageData.getString("SVC_KEY");
				if(StringUtils.isNotEmpty(svcKey)){
					String[] svcs = dealAction.split(",");
					for (int i = 0 ;i < svcs.length;i++){
						String svcInfo = svcs[i];
						String[] svc = svcInfo.split(":");
						String svck = svc[0];
						if(svcKey.equals(svck)){
							dealAction = svc[1];
							break;
						}
					}
				}

				IData dataset = callTradeReg(orderId,dealAction);//直接调度
				if(DataUtils.isNotEmpty(dataset)){
					TpOrderOperBean tpOrderOperBean = BeanManager.createBean(TpOrderOperBean.class);
					String orderIdA = dataset.getString("ORDER_ID");
					insertOrderRel(orderIdA,TpConsts.OrderABType.orderId,orderId,TpConsts.OrderABType.tpOrder,TpConsts.relType.TPORDER_WITH_TRADE);

					//修改甩单主表状态
					this.updateTpOrder(orderId,TpConsts.TpOrderState.state1);
					//记录操作日志
					IData operParam = creatOrderOper(orderId,TpConsts.OperType.creatTpOrder);
					tpOrderOperBean.insertOrderOper(operParam);//插入甩单操作表
				}else {
					CSAppException.apperr(TpOrderException.TP_ORDER_400023);
				}

			}else {
				CSAppException.apperr(TpOrderException.TP_ORDER_400024);
			}
		}else {
			CSAppException.apperr(TpOrderException.TP_ORDER_400022,tradeMode);
		}
		return orderId;
	}

	/**
	 * 调用接口生成正式业务订单工单
	 * @param dealAction
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	private IData callTradeReg(String orderId,String dealAction) throws Exception{
		//1、根据orderId获取甩单工单的接口报文
		IData input = new DataMap();
		input.put("ORDER_ID", orderId);
		IData tpOrderDetail = TpOrderDetailQuery.getOrderDetail(input);
		if(DataUtils.isEmpty(tpOrderDetail)){
			CSAppException.apperr(TpOrderException.TP_ORDER_40003,orderId);
		}
		//2、根据接口报文调用处理接口
		StringBuilder jsonData = new StringBuilder();
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_1"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_1"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_2"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_2"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_3"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_3"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_4"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_4"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_5"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_5"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_6"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_6"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_7"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_7"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_8"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_8"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_9"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_9"));
		}
		if(StringUtils.isNotEmpty(tpOrderDetail.getString("BUSI_CONTENT_10"))){
			jsonData.append(tpOrderDetail.getString("BUSI_CONTENT_10"));
		}
		
		IData result = call(dealAction,new DataMap(jsonData.toString()));
        return result;
	}

	public IData call(String svcName,IData data)throws Exception{
		ServiceResponse response = BizServiceFactory.call(svcName, data);
		return response.getBody();
	}
}
