
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.recepHallOrderOpen;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.PoTradePlusInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/*
 * 工单开通，如果成员业务，本省不保存报文中的信息，直接返回开通成功，否则会将报文信息解析入表，入表后返回开通成功，此时返回的是瞬时响应报文， 用户需要在工单查询页面处理后才会将工单开通的结果归档给集团公司，此时返回的是归档报文
 */
public class ReceptionHallOrderOpen
{
	private static final Logger log = Logger.getLogger(ReceptionHallOrderOpen.class);
    /*
     * @description 处理BBOSS向省BOSS发的工单开通业务
     * @author xunyl
     * @date 2013-07-25
     */
    public static IDataset dealOrderOpen(IData map) throws Exception
    {
        // 1- 定义返回对象
        IDataset result = new DatasetList();

        // 2- 获取业务操作类型(业务操作类型为变更成员时，不入工单表，直接返回成功)
        String operaType = map.getString("OPERA_TYPE");
        if ("6".equals(operaType))
        {
            result = orderOpenForChgMeb(map);
            return result;
        }
        //云视讯新增加业务逻辑 start
        //登记台账之前，是否需要新增逻辑的标识，默认不需要，如果需要自行添加
        boolean beforeSaveOrderInfoFlag = true;
        beforeSaveOrderInfoFlag = beforeSaveOrderInfo(map);
        if(beforeSaveOrderInfoFlag){
        // 3- 订单信息入表
        result = saveOrderInfo(map);
        }
        
        // 4- 业务开通和业务取消需要反馈EC竣工通知
        rspEcFinishMess(map,beforeSaveOrderInfoFlag);

        // 5- 返回工单开通结果
        return result;
    }

    /*
     * @description 产品参数信息的特殊情况处理
     * @author xunyl
     * @date 2013-07-25
     * @remark 典型场景：企业飞信下发开通工单时会将所有的配合省信息一起发送下来，落地方只要接收本省的信息即可，因此它省信息需要过滤
     */
    protected static void dealSpecialCharacter(IDataset ProductSpecCharacterNumber, IDataset CharacterValue, IDataset CGroup) throws Exception
    {
        for (int i = 0; i < ProductSpecCharacterNumber.size(); i++)
        {
            if (null == ProductSpecCharacterNumber.get(i))
            {
                continue;
            }
            String curNumber = ProductSpecCharacterNumber.get(i).toString();
            String curValue = CharacterValue.get(i).toString();
            String curCGroup = CGroup.get(i).toString();
            if ("910602001".equals(curNumber) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(curValue))
            {
                if (!"".equals(curCGroup))
                {
                    for (int j = i + 1; j < ProductSpecCharacterNumber.size(); j++)
                    {
                        if (curCGroup.equals(CGroup.get(j)))
                        {
                            ProductSpecCharacterNumber.set(j, null);
                        }
                    }
                }
                ProductSpecCharacterNumber.set(i, null);
            }
        }
    }

    /*
     * @descripiton 处理操作类型为变更成员的工单开通业务
     * @author xunyl
     * @date 2013-07-25
     */
    protected static IDataset orderOpenForChgMeb(IData map) throws Exception
    {
        // 1- 拼装开通成功报文
        IData message = new DataMap();
        String provinceCode = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_INFO", "HNAN", CSBizBean.getTradeEparchyCode());
        message.put("PROVINCE_CODE", provinceCode);
        message.put("IN_MODE_CODE", "0");
        message.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        message.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        message.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        message.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        message.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码此密码由BOSS制定，接入渠道必填
        message.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        message.put("ROUTETYPE", "00");
        message.put("ROUTEVALUE", "000");
        message.put("KIND_ID", "BIP4B256_T4101033_0_0");
        message.put("ORDER_NO", map.getString("ORDER_NO"));
        message.put("RSPCODE", "00");
        message.put("RSPDESC", "00");
        message.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

        // 2- 往一级BOSS接口发送报文
        IDataset callResult = IBossCall.dealInvokeUrl("BIP4B256_T4101033_0_0", "IBOSS", message);
        if (IDataUtil.isEmpty(callResult))
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        }
        if (!"0000".equals(callResult.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_349, callResult.getData(0).getString("X_RSPCODE", ""), callResult.getData(0).getString("X_RSPDESC", ""));
        }

        // 3- 处理返回结果
        IData result = new DataMap();
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result.put("RSP_CODE", "00");
        result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        return IDataUtil.idToIds(result);
    }

    protected static void rspEcFinishMess(IData map,boolean beforeSaveOrderInfoFlag) throws Exception
    {
    	String productSpecNumber = map.getString("RSRV_STR10", "");//产品规格编号
        String operationSubTypeID = map.getString("OPERA_TYPE", "");//操作类型
        //5001702 固话云视讯 1 新增产品订购
        if (!beforeSaveOrderInfoFlag) {
            if ("5001702".equals(productSpecNumber)) {
                if("1".equals(operationSubTypeID)){
                    //未通过资源校验，直接返回告知结果
                    commonRspEc(map, "12", "业务互斥");
                }else if("2".equals(operationSubTypeID)){
                    commonRspEc(map, "99", "固化云视讯销户资源校验失败");
                }
            }
        } else {
            commonRspEc(map, "00", "00");
        }
    }
    private static void commonRspEc(IData map,String rspCode,String rspDesc) throws Exception {
        // 1- 拼装开通成功报文
        IData message = new DataMap();
        String provinceCode = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_INFO", "HNAN", CSBizBean.getTradeEparchyCode());
        message.put("PROVINCE_CODE", provinceCode);
        message.put("IN_MODE_CODE", "0");
        message.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        message.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        message.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        message.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        message.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码此密码由BOSS制定，接入渠道必填
        message.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        message.put("ROUTETYPE", "00");
        message.put("ROUTEVALUE", "000");
        message.put("KIND_ID", "POOpenRspService_BBOSS_0_0");
        message.put("PRODUCT_ORDER_NUMBER", map.getString("ORDER_NO"));
        message.put("RSP_CODE", "00");
        message.put("RSP_DESC", "00");
        message.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

        // 开通工单中拼写产品参数
        setProductParam(map, message);

        // 2- 往一级BOSS接口发送报文
        IDataset callResult = IBossCall.dealInvokeUrl("POOpenRspService_BBOSS_0_0", "IBOSS", message);
        if (callResult == null)
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        }
        if (!"0000".equals(callResult.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_349, callResult.getData(0).getString("X_RSPCODE", ""), callResult.getData(0).getString("X_RSPDESC", ""));
        }

    }

    /*
     * @description 订单信息入表
     * @author xunyl
     * @date 2013-07-25
     */
    protected static IDataset saveOrderInfo(IData map) throws Exception
    {
        // 1- 基本订购信息入表
        IData baseInfo = new DataMap();
        baseInfo.put("TRADE_ID", SeqMgr.getTradeId()); // 业务编码
        baseInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); // 受理月份
        baseInfo.put("ACCEPT_DATE", SysDateMgr.getSysDate()); // 受理时间
        baseInfo.put("PRODUCTORDERNUMBER", map.getString("ORDER_NO")); // 产品订单号
        baseInfo.put("PRODUCTSPECNUMBER", map.getString("RSRV_STR10")); // 产品规格编号
        baseInfo.put("ACCESSNUMBER", map.getString("RSRV_STR5")); // 产品关键号码
        baseInfo.put("PRIACCESSNUMBER", map.getString("RSRV_STR6")); // 产品附件号码
        baseInfo.put("LINKMAN", map.getString("CONTACTNAME")); // 联系人
        baseInfo.put("CONTACTPHONE", map.getString("CONTACT_PHONE")); // 联系电话
        baseInfo.put("DESCRIPTION", map.getString("DESCRIPTION")); // 产品描述
        baseInfo.put("SERVICELEVELID", map.getString("RSRV_STR7")); // 服务开通等级ID
        baseInfo.put("TERMINALCONFIRM", map.getString("RSRV_STR8")); // 是否需要二次确认
        baseInfo.put("OPERATIONSUBTYPEID", map.getString("OPERA_TYPE")); // 产品级业务操作
        baseInfo.put("TRADE_STATE", "0"); // 操作类型
        baseInfo.put("RSRV_STR5", map.getString("CUSTOMER_NUMBER")); // 集团客户编码 v1.1.6添加
        Dao.insert("TF_B_POTRADE", baseInfo,Route.getJourDb(BizRoute.getRouteId()));

        // 2- 产品参数信息入表
        IDataset ProductSpecCharacterNumber = IDataUtil.getDataset("RSRV_STR1", map);// 属性编码
        IDataset CharacterValue = IDataUtil.getDataset("RSRV_STR2", map);// 属性值
        IDataset Name = IDataUtil.getDataset("PROPERTY_NAME", map);// 属性名称
        IDataset Action = IDataUtil.getDataset("ACTION", map);// 操作类型
        IDataset CGroup = IDataUtil.getDataset("CHARACTER_GROUP", map);// 属性组
        // 2-1 特殊情况处理
//        dealSpecialCharacter(ProductSpecCharacterNumber, CharacterValue, CGroup);
        // 2-2 参数信息入表
        for (int i = 0; i < ProductSpecCharacterNumber.size(); i++)
        {
            if (null == ProductSpecCharacterNumber.get(i))
            {
                continue;
            }
            String curNumber = ProductSpecCharacterNumber.get(i).toString();
            String curName = Name.get(i).toString();
            String curValue = CharacterValue.get(i).toString();
            String curAction = Action.get(i).toString();
            String curCGroup = "";
            if(CGroup.size()>i){
                curCGroup = CGroup.get(i).toString();
            }

            IData productPlusInfo = new DataMap();
            productPlusInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); // 受理月
            productPlusInfo.put("PRODUCTORDERNUMBER", map.getString("ORDER_NO")); // 定单号
            productPlusInfo.put("PRODUCTSPECCHARACTERNUMBER", curNumber); // 产品属性代码
            productPlusInfo.put("CHARACTERVALUE", curValue); // 属性值
            productPlusInfo.put("NAME", curName); // 属性名
            productPlusInfo.put("ACTION", curAction);// 属性操作
            productPlusInfo.put("CHARACTERGROUP", curCGroup);// 属性组
            Dao.insert("TF_B_POTRADEPLUS", productPlusInfo,Route.getJourDb(BizRoute.getRouteId()));
        }
        //3-资费信息入表
        //3-1产品一次性资费入表  TF_B_POTRADEDISCNT
        IDataset productOrderChargeCode = IDataUtil.getDataset("PRODUCT_ORDER_CHARGE_CODE", map);// 产品一次性资费项
        IDataset productOrderChargeValue = IDataUtil.getDataset("PRODUCT_ORDER_CHARGE_VALUE", map);// 产品一次性费用
        
        // 参数信息入表
        for (int i = 0; i < productOrderChargeCode.size(); i++)
        {
            if (null == productOrderChargeCode.get(i))
            {
                continue;
            }
            String curChargeCode = productOrderChargeCode.get(i).toString();
            String curChargeValue = productOrderChargeValue.get(i).toString();
            
            IData discntInfo = new DataMap();
            discntInfo.put("PRODUCTORDERNUMBER", map.getString("ORDER_NO"));//产品订单号
        	discntInfo.put("DISCNT_ID", curChargeCode);//产品一次性资费项
        	discntInfo.put("PRODUCT_ORDER_CHARGE_VALUE",curChargeValue);//产品一次性费用
        	discntInfo.put("IS_ONCE_CHARGE", "1"); //标识
        	discntInfo.put("UPDATE_TIME", SysDateMgr.getSysDate());//更新时间
        	discntInfo.put("ACCEPT_MONTH",  SysDateMgr.getCurMonth());//受理月
            Dao.insert("TF_B_POTRADEDISCNT", discntInfo,Route.getJourDb(BizRoute.getRouteId()));
        }
        
        //资费信息入表TF_B_POTRADEDISCNT
        IDataset ratePlanId = IDataUtil.getDataset("RATE_PLAN_ID", map);//资费标识ID
        IDataset desc = IDataUtil.getDataset("DESCRIPTION", map);// 资费描述
        IDataset action = IDataUtil.getDataset("DISCNT_ACTION", map);// 操作
        for (int i = 0; i < ratePlanId.size(); i++)
        {
            if (null == ratePlanId.get(i))
            {
                continue;
            }
            String curPlanId = ratePlanId.get(i).toString();

            String curDesc = desc.size() > i ? desc.get(i).toString() : "";//daidl
            String curAction = action.get(i).toString();
            
            IData rateInfo = new DataMap();
            rateInfo.put("PRODUCTORDERNUMBER", map.getString("ORDER_NO"));//产品订单号
            rateInfo.put("DISCNT_ID", curPlanId);//资费标识ID
            rateInfo.put("ACTION", curAction);//操作
        	rateInfo.put("DESCRIPTION",curDesc);// 资费描述
        	rateInfo.put("IS_ONCE_CHARGE", "0"); //标识
        	rateInfo.put("UPDATE_TIME", SysDateMgr.getSysDate());//更新时间
        	rateInfo.put("ACCEPT_MONTH",  SysDateMgr.getCurMonth());//受理月
            Dao.insert("TF_B_POTRADEDISCNT", rateInfo,Route.getJourDb(BizRoute.getRouteId()));
        }
                              
       //3-2 属性表 TF_B_POTRADEPLUS             
        IDataset parameterNumber = IDataUtil.getDataset("PARAMETER_NUMBER", map);// 属性编码
        IDataset parameterValue = IDataUtil.getDataset("PARAMETER_VALUE", map);// 属性值
        IDataset parameterName = IDataUtil.getDataset("PARAMETER_NAME", map);// 属性名         
        //  参数信息入表
        for (int i = 0; i < parameterNumber.size(); i++)
        {
            if (null == parameterNumber.get(i))
            {
                continue;
            }
            String curNumber = parameterNumber.get(i).toString();
            String curName = parameterName.get(i).toString();
            String curValue = parameterValue.get(i).toString();
            String curPlanId = ratePlanId.get(i).toString();
            String curAction = action.get(i).toString();

            IData parameterInfo = new DataMap();
            parameterInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); // 受理月
            parameterInfo.put("PRODUCTORDERNUMBER", map.getString("ORDER_NO")); // 定单号
            parameterInfo.put("PRODUCTSPECCHARACTERNUMBER", curNumber); // 资费属性编码       
            parameterInfo.put("CHARACTERVALUE", curValue); // 属性值  
            parameterInfo.put("NAME", curName); // 属性名
            parameterInfo.put("ACTION", curAction);// 属性操作
            parameterInfo.put("RSRV_STR1", curPlanId);
            Dao.insert("TF_B_POTRADEPLUS", parameterInfo,Route.getJourDb(BizRoute.getRouteId()));
        }
              

        // 4- 处理返回结果
        IData result = new DataMap();
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result.put("RSP_CODE", "00");
        result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        return IDataUtil.idToIds(result);
    }

    /*
     * @description 帅选工单阶段反馈的属性
     * @author xunyl
     * @date 2014-04-14
     */
    protected static IDataset selOrderOpenRspParam(IDataset bbossAttrInfoList) throws Exception
    {
        // 1- 定义返回变量
        IDataset rspParamInfoList = new DatasetList();

        // 2- 如果产品对应的参数为空则直接退出
        if (IDataUtil.isEmpty(bbossAttrInfoList))
        {
            return rspParamInfoList;
        }

        // 3- 帅选VISIBLE为工单开通阶段的产品参数,自定义工单开通阶段的操作类型为98
        for (int i = 0; i < bbossAttrInfoList.size(); i++)
        {
            IData bbossAttrInfo = bbossAttrInfoList.getData(i);
            if (GrpCommonBean.nullToString(bbossAttrInfo.getString("VISIBLE")).contains("98"))
            {
                rspParamInfoList.add(bbossAttrInfo);
            }

        }

        // 4- 返回工单阶段反馈属性
        return rspParamInfoList;
    }

    /*
     * @description 开通工单中拼写产品参数
     * @author xunyl
     * @date 2014-04-14
     */
    protected static void setProductParam(IData map, IData message) throws Exception
    {
        // 1- 获取产品规格编号
        String productSpecNumber = map.getString("RSRV_STR10");

        // 2- 根据产品规格编号和操作编号查询是否有对应的产品参数需要在开通工单环节反馈
        IDataset bbossAttrInfoList = BBossAttrQry.qryBBossAttrByPospecBiztype(productSpecNumber, "1");
        IDataset rspParamInfoList = selOrderOpenRspParam(bbossAttrInfoList);

        // 3- 没有对应的产品参数需要反馈则直接退出
        if (IDataUtil.isEmpty(rspParamInfoList))
        {
            return;
        }

        // 4- 遍历需要反馈的产品参数，如果为属性组则意味着需要根据工单中下发的属性组来决定反馈多少组属性
        IDataset paramCodeList = new DatasetList();
        IDataset paramNameList = new DatasetList();
        IDataset paramValueList = new DatasetList();
        IDataset cgroupList = new DatasetList();
        for (int i = 0; i < rspParamInfoList.size(); i++)
        {
            IData rspParamInfo = rspParamInfoList.getData(i);
            if (StringUtils.isEmpty(rspParamInfo.getString("GROUPATTR")))
            {
                paramCodeList.add(rspParamInfo.getString("ATTR_CODE"));
                paramNameList.add(rspParamInfo.getString("ATTR_NAME"));
                paramValueList.add(rspParamInfo.getString("DEFAULT_VALUE"));
                cgroupList.add("");
            }
            else
            {
                String productSpecCharacterNum = rspParamInfo.getString("GROUPATTR").split("_")[0];
                String productOrderNum = map.getString("ORDER_NO");
                IDataset poTradePlusInfoList = PoTradePlusInfoQry.qryPoTradePlus(productOrderNum, productSpecCharacterNum);
                if (IDataUtil.isEmpty(poTradePlusInfoList))
                {
                    return;
                }
                for (int j = 0; j < poTradePlusInfoList.size(); j++)
                {
                    paramCodeList.add(rspParamInfo.getString("ATTR_CODE"));
                    paramNameList.add(rspParamInfo.getString("ATTR_NAME"));
                    paramValueList.add(rspParamInfo.getString("DEFAULT_VALUE"));
                    cgroupList.add(poTradePlusInfoList.getData(j).getString("CHARACTERGROUP"));
                }
            }
        }

        // 5- 拼写返回的参数
        IData extend = new DataMap();
        extend.put("PRODUCT_SPEC_CHARACTER_NUMBER", paramCodeList.toString());
        extend.put("CHARACTER_VALUE", paramValueList.toString());
        extend.put("NAME", paramNameList.toString());
        extend.put("CHARACTER_GROUP", cgroupList.toString());
        message.put("EXTENDS", IDataUtil.idToIds(extend));

    }
    /**
	 * 在登记台账前，对一些参数进行进行例如资源有效性校验等，此处逻辑可自行添加
	 * 
	 * @author
	 * @date 2019/8/12
	 * @version 1.0
	 *
	 */
	protected static boolean beforeSaveOrderInfo(IData map) throws Exception {
		boolean checkFlag = true;// 默认返回true
		IData snData = new DataMap();
		String operationSubTypeID = map.getString("OPERA_TYPE", "");// 操作类型
		String productSpecNumber = map.getString("RSRV_STR10", "");// 产品规格编号
		// 先判断是否为云视讯
		if ("5001702".equals(productSpecNumber)) {
			// 从map中获取需要的信息
			snData = getSerialNumber(map);
			// 固话云视讯开户校验
			if ("1".equals(operationSubTypeID)) {
				checkFlag = checkCloudVedioMessageCreate(snData);
			}
			// 固化云视讯销户校验
			else if ("2".equals(operationSubTypeID)) {
				checkFlag = checkCloudVedioMessageCancle(snData);
			}
			if (!checkFlag) { 
				return checkFlag;
			} else {
				dealCloudVedioMessage(snData,operationSubTypeID);
			}
			return checkFlag;
		} else {
			return checkFlag;// 不是云视讯直接返回true
		}
	}
	/**
	 * 获取固化云视讯号码的公共方法 operType 1 新增 2 销户
	 */
	public static IData getSerialNumber(IData map) throws Exception {
		IData snData = new DataMap();
		String serialNumber = "";
		String epachyCode = "";
		IDataset productSpecCharacterNumber = IDataUtil.getDataset("RSRV_STR1", map);// 属性编码
		IDataset characterValue = IDataUtil.getDataset("RSRV_STR2", map);// 属性值
		// 资源校验结果
		if (productSpecCharacterNumber != null && productSpecCharacterNumber.size() > 0) {
			for (int i = 0; i < productSpecCharacterNumber.size(); i++) {
				String pscNumber = productSpecCharacterNumber.get(i).toString();
				// 对拿到的云视讯固话号码(50017020006)进行资源校验
				if ("50017020006".equals(pscNumber)) {
					serialNumber = characterValue.get(i).toString();// 固化云视讯号码
				}
				if ("50017020005".equals(pscNumber)) {
					epachyCode = characterValue.get(i).toString();// 号码归属地市
				}
			}
		}
		snData.put("serialNumber", serialNumber);
		snData.put("epachyCode", epachyCode);
		snData.put("OPERA_TYPE", map.getString("OPERA_TYPE", ""));
		snData.put("RSRV_STR10", map.getString("RSRV_STR10", ""));
		snData.put("RSRV_STR1", map.getString("RSRV_STR1", ""));
		snData.put("RSRV_STR2", map.getString("RSRV_STR2", ""));
		snData.put("CUSTOMER_NUMBER", map.getString("CUSTOMER_NUMBER", ""));
		snData.put("CONTACT_PHONE", map.getString("CONTACT_PHONE", ""));
		snData.put("CONTACTNAME", map.getString("CONTACTNAME", ""));
		return snData;
	}
	/**
	 * 云视讯业务支撑实施方案V1.1 参考改造方案文档《云视讯业务支撑实施方案V1.1》， 参考规范《中国移动集客大厅与省公司的接口规范v1.1.7》，
	 * 参考产品规格文档《JTYW058+云视讯产品规格属性说明v1.0》
	 *
	 * 设计:开通过程如果是固化云视讯进行资源校验，在开户时需要传ENUM/DNS平台。
	 *
	 * 1、先由IBOSS传入的数据，拿到产品编码，
	 * 当产品编码是5001702时，拿到产品属性编码是云视讯固话号码（50017020006）进行资源校验。
	 *
	 * @author ReceptionHall Developer
	 * @date 2019/8/7
	 * @version 1.0
	 */
	protected static boolean checkCloudVedioMessageCreate(IData serialNumberData)
			throws Exception {
		boolean checkFlag = true;
		String serialNumber = serialNumberData.getString("serialNumber", "");
		String epachyCode = serialNumberData.getString("epachyCode", "");
		// 对资源进行校验
		IData resCheckData = checkResource(serialNumber, epachyCode);
		log.debug("调用完资源校验后的封装结果resCheckData===>" + resCheckData);
		if (!"0".equals(resCheckData.getString("X_RESULTCODE", ""))) {
			checkFlag = false;
		}
		return checkFlag;
	}
	public static boolean checkCloudVedioMessageCancle(IData serialNumberData) throws Exception {
		boolean checkFlag = true;
		String serialNummber = serialNumberData.getString("serialNumber", "");
		// String epachyCode = serialNumberData.getString("epachyCode","");
		String removeTag = "0";
		IData params = new DataMap();
		params.put("SERIAL_NUMBER", serialNummber);
		params.put("REMOVE_TAG", removeTag);
		IDataset result = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoBySnNoProduct", params);
		log.debug("注销前号码校验结果result====>" + result);
		if (IDataUtil.isEmpty(result)) {
			checkFlag = false;
		}
		return checkFlag;
	}
	/**
	 * 处理云视讯逻辑
	 * 
	 * @version 1.0
	 * @date 2019/8/12
	 */
	private static void dealCloudVedioMessage(IData snData,String operationSubTypeID) throws Exception {
			// 开通操作
			if ("1".equals(operationSubTypeID)) {
				// 开通固话云视讯
				openFixedPhone(snData);
			} else if ("2".equals(operationSubTypeID)) {
				// 注销操作
				cancleFixedPhone(snData);
			}
	}
	/**
	 * 调用CS.OpenGroupMemberSVC.checkResourceSn和TF_F_USER表有没有数据
	 * 由于CS.OpenGroupMemberSVC.checkResourceSn接口里已经会校验是否已经被占用，故没有必要要校验TF_F_USER表数据了
	 *
	 * @author ReceptionHall Developer
	 * @Date 2019/8/8
	 */
	private static IData checkResource(String pscValue, String pscEpachyVal) throws Exception {
		// 资源校验结果
		IData resCheckData = new DataMap();
		IData params = new DataMap();
		params.put("RES_VALUE", pscValue);
		// RES_TYPE_CODE不知道，看别人调这个接口都传的0
		params.put("RES_TYPE_CODE", "0");
		params.put("PRODUCT_ID", "");
		// 不清楚目前这一块这个业务类型对不对，看其他地方调这个接口传的都是3600
		params.put("TRADE_TYPE_CODE", "3610");
		params.put(Route.ROUTE_EPARCHY_CODE, pscEpachyVal);
		params.put(Route.USER_EPARCHY_CODE, pscEpachyVal);
		IDataset userInfoResult = UserInfoQry.getUserInfoBySerailNumber( "0",pscValue);
		String resResultCode = "";
		String resResultInfo = "";
		if (userInfoResult != null && userInfoResult.size() > 0) {
			resCheckData.put("X_RESULTCODE", "-1");
			resCheckData.put("X_RESULTINFO", "该号码已正常在网使用，无法受理该业务！");
		} else {											
			IDataset resourceCheckResult = CSAppCall.call("CS.OpenGroupMemberSVC.checkResourceSn", params);
			log.debug("调用完资源校验后的结果resourceCheckResult====>" + resourceCheckResult);
			if (resourceCheckResult != null && resourceCheckResult.size() > 0) {
				resResultCode = resourceCheckResult.first().getString("X_RESULTCODE", "");
				resResultInfo = resourceCheckResult.first().getString("X_RESULTINFO", "");
			}
		}
		resCheckData.put("X_RESULTCODE", resResultCode);
		resCheckData.put("X_RESULTINFO", resResultInfo);
		return resCheckData;
	}
	/**
	 * 固话云视讯开户发ENUM/DNS平台
	 * 
	 * @date 2019/8/9
	 * @version 1.0
	 */
	private static IDataset openFixedPhone(IData map) throws Exception {
		IDataset result = CSAppCall.call("SS.FixedPhoneCloudVedioMessageSVC.fixedPhoneCloudVedioCreateUser", map);

		log.debug("调完固化云视讯开户后的result===" + result);
		return result;
	}

	/**
	 * 固话云视讯注销发ENUM/DNS平台
	 * 
	 * @date 2019/8/14
	 * @version 1.0
	 */
	private static IDataset cancleFixedPhone(IData map) throws Exception {
		IDataset result = CSAppCall.call("SS.FixedPhoneCloudVedioMessageSVC.fixedPhoneCloudVedioCancleUser", map);
		log.debug("调完固化云视讯注销后的result===" + result);
		return result;
	}
}
