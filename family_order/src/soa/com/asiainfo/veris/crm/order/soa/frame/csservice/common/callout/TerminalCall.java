
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.bcc.ConcurrentKeeper;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.data.TerminalUpData;

public class TerminalCall
{
	private static final Logger logger = Logger.getLogger(TerminalCall.class);
    

    public static IDataset callHwTerminal(String svcName, IData inparams) throws Exception
    {
        boolean dev = BizEnv.getEnvBoolean("hw.terminal.mgr.dev", false);

        IDataset result = new DatasetList();

        if (dev == true)
        {
            IData map = new DataMap();

            map.put("X_RESULTCODE", "0");

            map.put("DEVICE_MODEL_CODE", "2009103114401066"); // 终端类型编码
            map.put("DEVICE_MODEL", "手机"); // 终端型号名称
            map.put("DEVICE_COST", "510000"); // 终端成本价(结算价(进货价))
            map.put("DEVICE_BRAND", "三星"); // 终端品牌名称
            map.put("DEVICE_BRAND_CODE", "SANSUNG"); // 终端品牌编码
            map.put("SUPPLY_COOP_ID", "00401"); // 提供商编码
            map.put("TERMINAL_TYPE_CODE", "04"); // 某一终端类型编码
            map.put("TERMINAL_STATE", "0"); // 在途状态
            map.put("RSRV_STR6", "5000"); // 终端销售价--rsrv_str6
            map.put("RSRV_STR7", "1000"); // 代办费 rsrv_str7
            map.put("RSRV_STR1", "1"); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
            map.put("RSRV_STR3", "红色"); // 终端颜色描述
            map.put("RSRV_STR4", "理电池"); // 终端电池配置
            map.put("SERIAL_NUMBER", inparams.getString("RES_NO")); // 终端电池配置
            result.add(map);
        }
        else
        { 
            // 业务并发控制
            ConcurrentKeeper.protect("SVC_hw.terminal.mgr.url");

            String url = BizEnv.getEnvString("hw.terminal.mgr.url");
            
            /**
             * 退光猫操作
             * chenxy3
             * */
            String tradeStaffId="";
            String tradeDepatId="";
            String ftthTag=inparams.getString("FTTH_RTN_MODEM","");
            if(!"".equals(ftthTag)){
            	tradeStaffId=inparams.getString("TRADE_STAFF_ID",CSBizBean.getVisit().getStaffId());
            	tradeDepatId=inparams.getString("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
            }else{
            	tradeStaffId=CSBizBean.getVisit().getStaffId();
            	tradeDepatId=CSBizBean.getVisit().getDepartId();
            }

            inparams.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
            inparams.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            inparams.put("TRADE_STAFF_ID", tradeStaffId);
            inparams.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            inparams.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            inparams.put("TRADE_DEPART_ID", tradeDepatId);
            inparams.put(Route.ROUTE_EPARCHY_CODE, inparams.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));
            
            //如果是魔百和开户，则需要特殊处理，因为魔百和开户的登录信息是营业员信息，而华为预占的是施工人员的信息
            String internetTvOpenTag = inparams.getString("INTERNETTV_OPEN_TAG","");//是否是魔百和开户
            if("1".equals(internetTvOpenTag))
            {
            	inparams.put("STAFF_ID", inparams.getString("WORK_STAFF_ID", CSBizBean.getVisit().getStaffId()));
            	inparams.put("TRADE_STAFF_ID", inparams.getString("WORK_STAFF_ID", CSBizBean.getVisit().getStaffId()));
            	inparams.put("TRADE_DEPART_ID", inparams.getString("WORK_DEPART_ID", CSBizBean.getVisit().getDepartId()));
            	inparams.put("TRADE_CITY_CODE", inparams.getString("WORK_CITY_CODE", CSBizBean.getVisit().getCityCode()));
            }

            String inparams2String = Wade3DataTran.toWadeString(inparams);
            String terminalResult = Wade3ClientRequest.request(url, svcName, inparams2String, "GBK");


            result = Wade3DataTran.wade3To4Dataset(Wade3DataTran.strToList(terminalResult));
        }

        return result;
    }
    
    /**
     * 供PBOSS调用
     * @param svcName
     * @param inparams
     * @return
      * @author: lijun17
     * @date: 2016-5-30 下午7:41:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 7:41:42 lijun17 v1.0.0 修改原因
     */
    public static IDataset callHwTerminalForApp(String svcName, IData inparams) throws Exception
    {
        boolean dev = BizEnv.getEnvBoolean("hw.terminal.mgr.dev", false);

        IDataset result = new DatasetList();

        if (dev == true)
        {
            IData map = new DataMap();

            map.put("X_RESULTCODE", "0");

            map.put("DEVICE_MODEL_CODE", "2009103114401066"); // 终端类型编码
            map.put("DEVICE_MODEL", "手机"); // 终端型号名称
            map.put("DEVICE_COST", "510000"); // 终端成本价(结算价(进货价))
            map.put("DEVICE_BRAND", "三星"); // 终端品牌名称
            map.put("DEVICE_BRAND_CODE", "SANSUNG"); // 终端品牌编码
            map.put("SUPPLY_COOP_ID", "00401"); // 提供商编码
            map.put("TERMINAL_TYPE_CODE", "04"); // 某一终端类型编码
            map.put("TERMINAL_STATE", "0"); // 在途状态
            map.put("RSRV_STR6", "5000"); // 终端销售价--rsrv_str6
            map.put("RSRV_STR7", "1000"); // 代办费 rsrv_str7
            map.put("RSRV_STR1", "1"); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
            map.put("RSRV_STR3", "红色"); // 终端颜色描述
            map.put("RSRV_STR4", "理电池"); // 终端电池配置

            result.add(map);
        }
        else
        {
            // 业务并发控制
            ConcurrentKeeper.protect("SVC_hw.terminal.mgr.url");

            String url = BizEnv.getEnvString("hw.terminal.mgr.url");
            
            String tradeStaffId="";
            String tradeDepatId="";
            String ftthTag=inparams.getString("FTTH_RTN_MODEM","");
            tradeStaffId=inparams.getString("TRADE_STAFF_ID","SUPERUSR");
            tradeDepatId=inparams.getString("TRADE_DEPART_ID","36601");
            
            inparams.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
            inparams.put("IN_MODE_CODE", inparams.getString("IN_MODE_CODE","0"));
            inparams.put("TRADE_STAFF_ID", tradeStaffId);
            inparams.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            inparams.put("TRADE_CITY_CODE", inparams.getString("TRADE_CITY_CODE","HNSJ"));
            inparams.put("TRADE_DEPART_ID", tradeDepatId);
            inparams.put(Route.ROUTE_EPARCHY_CODE, inparams.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

            String inparams2String = Wade3DataTran.toWadeString(inparams);
            String terminalResult = Wade3ClientRequest.request(url, svcName, inparams2String, "GBK");

            if (logger.isDebugEnabled())
            {
                logger.debug("callHwTerminal terminalResult:>>>>>>>>>>> " + terminalResult);
            }

            result = Wade3DataTran.wade3To4Dataset(Wade3DataTran.strToList(terminalResult));
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("callHwTerminal result:>>>>>>>>> " + result);
        }

        return result;
    }

    public static IDataset cancelTerminalState() throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put(",X_CHOICE_TAG", "1");// 返销
        inData.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
        // IDataset result = CSAppCall.call("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateMgr", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateMgr", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    public static IDataset checkFourCodeInfo(String resNo, String routeEparchyCode) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        if (StringUtils.isNotBlank(routeEparchyCode))
        {
            inData.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        }
        inData.put("RES_NO", resNo);
        inData.put("RES_TRADE_CODE", "ICheckFourCodesInfo");
        inData.put("X_TAG", "1");

        // IDataset result = CSAppCall.call("TM.FourCodesToOneSvc.checkFourCodesToOne", inData);
        IDataset result = CSAppCall.callTerminal("TM.FourCodesToOneSvc.checkFourCodesToOne", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    public static IDataset checkTerminalOccupyInfo(String serialNumber, String rsrvStr2, String rsrvStr1) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("RES_TYPE_CODE", "4");
        inData.put("RES_STATE_CODE", "");
        inData.put("OCCUPY_TYPE_CODE", "");
        inData.put("RSRV_STR2", rsrvStr2);
        inData.put("RSRV_STR1", rsrvStr1);
        IDataset result = CSAppCall.callTerminal("TM.TerminalProcessInNetSvc.checkPreSelectInNet", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    /**
     * 终端上传校验
     * 
     * @param resNo
     * @return
     * @throws Exception
     */
    public static IDataset checkTerminalUpload(String resNo) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_NO", resNo);
        inData.put("RES_TRADE_CODE", "CheckTerminalId");
        // IDataset result = CSAppCall.call("TM.TerminalUpCheckSvc.checkTerminalUpload", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalUpCheckSvc.checkTerminalUpload", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    /**
     * 修改状态并上发集团
     * 
     * @param resNo
     * @param tradeId
     * @param paraCode1
     * @param saleFee
     * @param sn
     * @param campnId
     * @param userId
     * @param deviceCost
     * @return
     * @throws Exception
     */
    public static IDataset exchangeTerminalAndUpData(String resNo, String tradeId, String paraCode1, String saleFee, String sn, String campnId, String userId, String deviceCost, TerminalUpData upData) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_NO", resNo);
        inData.put("SALE_FEE", saleFee);
        inData.put("X_CHOICE_TAG", "2");// 受理
        inData.put("PARA_CODE1", paraCode1);
        inData.put("TRADE_ID", tradeId);
        if (StringUtils.isNotBlank(sn))
        {
            inData.put("SERIAL_NUMBER", sn);
        }
        if (StringUtils.isNotBlank(campnId))
        {
            inData.put("PARA_VALUE3", campnId);
        }
        if (StringUtils.isNotBlank(userId))
        {
            inData.put("PARA_VALUE4", userId);
        }
        if (StringUtils.isNotBlank(deviceCost))
        {
            inData.put("DEVICE_COST", deviceCost);
        }
        inData.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
        inData.putAll(upData.toData());

        // IDataset result = CSAppCall.call("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateAndUp", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateAndUp", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    /**
     * 用户换机
     * 
     * @param sn
     * @param userId
     * @param oldTerminalId
     * @param newTerminalId
     * @param oldtradeId
     * @param tradeId
     * @return true 更新成功 false 更新失败
     * @throws Exception
     */
    public static boolean exchangeTerminalBusiness(String sn, String userId, String oldTerminalId, String newTerminalId, String oldtradeId, String tradeId) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("SERIAL_NUMBER", sn);
        inData.put("USER_ID", userId);
        inData.put("RSRV_STR1", oldTerminalId);// 旧的终端编码
        inData.put("RSRV_STR2", newTerminalId);// 新的终端编码
        inData.put("RSRV_STR3", oldtradeId);// 旧的销售记录ID
        inData.put("TRADE_ID", tradeId);// 新的销售记录ID
        inData.put("RES_TRADE_CODE", "ITerminalExchange");

        IData result = CSAppCall.callTerminal("TM.TerminalExchangeBusinessSvc.exchangeTerminalBusiness", inData, false).getHead();

        if ("0".equals(result.getString("X_RESULTCODE")))
        {
            return true;

        }
        else
        {
            return false;
        }
    }

    /**
     * 4码合一销售及上传接口
     * 
     * @param serialNumber
     * @param resNo
     * @param routeEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset fourCodesToOneStateChangeAndUp(String serialNumber, String resNo, String choiceTag, String tradeId, String saleFee, String routeEparchyCode, TerminalUpData terminalUpData) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("RES_NO", resNo);
        inData.put("RES_TRADE_CODE", "IFourCodesToOne");
        inData.put("X_TAG", "1");
        inData.put("TRADE_ID", tradeId);
        inData.put("SALE_FEE", saleFee);
        inData.put("X_CHOICE_TAG", choiceTag);
        inData.put("X_GETMODE", "1");
        if (StringUtils.isNotBlank(routeEparchyCode))
        {
            inData.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        }
        inData.putAll(terminalUpData.toData());

        IDataset result = CSAppCall.callTerminal("TM.FourCodesToOneSvc.fourCodesToOneStateMgrAndUp", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    public static IData getTerminalByDeviceMode(String deviceModelCode, String resTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "IGetTerminalAllInfo");
        param.put("RES_TYPE_CODE", "4"); // 资源类型
        param.put("X_GETMODE", "0");
        param.put("TERMINAL_MODEL_CODE", deviceModelCode); // 终端类型
        param.put("RES_TYPE_ID", resTypeCode); // 子机型，网厅过来给新终端

        IData map = new DataMap();
        map.put("DEVICE_MODEL_CODE", "2009103114401066");// 终端类型编码
        map.put("DEVICE_MODEL", "手机"); // 终端型号名称
        map.put("DEVICE_COST", "510000"); // 终端成本价(结算价(进货价))
        map.put("DEVICE_BRAND", "三星"); // 终端品牌名称
        map.put("DEVICE_BRAND_CODE", "SANSUNG");// 终端品牌编码
        map.put("SUPPLY_COOP_ID", "00401"); // 提供商编码
        map.put("TERMINAL_TYPE_CODE", "04"); // 某一终端类型编码
        map.put("TERMINAL_STATE", "0"); // 在途状态
        map.put("SALE_PRICE", "5000"); // 终端销售价--rsrv_str6
        map.put("DEPUTY_FEE", "1000"); // 代办费 rsrv_str7
        map.put("IS_INTELL_TERMINAL", "1"); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
        map.put("RSRV_STR3", "红色"); // 终端颜色描述
        map.put("RSRV_STR4", "理电池"); // 终端电池配置
        return map;
    }

    /**
     * 根据终端串号，获取终端信息；不预占
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IData getTerminalInfoByTerminalId(String resNo) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo");
        param.put("RES_TYPE_CODE", "4"); // 资源类型
        param.put("RES_ID", ""); // 资源实物编码
        param.put("RES_NO", resNo); // 资源号
        param.put("OCCUPY_FLAG", "OCCUPY"); // 只做查询，不预占

        // TODO call huaW interface

        IData map = new DataMap();
        map.put("RES_CODE", resNo); // 实物编码
        map.put("DEVICE_MODEL_CODE", "2009103114401066");// 终端类型编码
        map.put("DEVICE_MODEL", "手机"); // 终端型号名称
        map.put("DEVICE_COST", "510000"); // 终端成本价(结算价(进货价))
        map.put("DEVICE_BRAND", "三星"); // 终端品牌名称
        map.put("DEVICE_BRAND_CODE", "SANSUNG");// 终端品牌编码
        map.put("SUPPLY_COOP_ID", "00401"); // 提供商编码
        map.put("TERMINAL_TYPE_CODE", "04"); // 某一终端类型编码
        map.put("TERMINAL_STATE", "0"); // 在途状态
        map.put("SALE_PRICE", "5000"); // 终端销售价--rsrv_str6
        map.put("DEPUTY_FEE", "1000"); // 代办费 rsrv_str7
        map.put("IS_INTELL_TERMINAL", "1"); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
        map.put("RSRV_STR3", "红色"); // 终端颜色描述
        map.put("RSRV_STR4", "理电池"); // 终端电池配置
        return map;
    }

    /**
     * 写这个方法是为了防止终端的接口里不满足业务规则了，但没有直接抛错，而是返回的X_RESULTCODE不为0的情况出现 如果X_RESULTCODE不为0，则在TerminalCall里抛出异常
     * 
     * @param result
     * @throws Exception
     */
    private static void judgeXResultCode(IDataset result) throws Exception
    {
        if (IDataUtil.isNotEmpty(result))
        {
            IData tmpData = result.getData(0);
            String xResultCode = tmpData.getString("X_RESULTCODE", "0");
            String xResultInfo = tmpData.getString("X_RESULTINFO", "调用终端接口失败");
            if (!"0".equals(xResultCode))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, xResultInfo);
            }
        }
    }

    /**
     * @Function: modifyStateInfoNoCen
     * @Description: 对应老流程TRM_IModifyStateInfoNoCen
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-9-10 上午9:34:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-10 lijm3 v1.0.0 修改原因
     */
    public static IDataset modifyStateInfoNoCen(String resTradeCode, String routeEparchyCode, String serialNumber, String resNo, String activeId, String rsrvStr1, String xtag, String xchoicetag, String tradeId, String saleFee, String para_value4,
            String x_getmode) throws Exception
    {

        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_TRADE_CODE", resTradeCode);
        inData.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("RES_NO", resNo);
        inData.put("ACTIVE_ID", activeId);
        inData.put("RSRV_STR1", rsrvStr1);
        inData.put("X_TAG", xtag);
        inData.put("X_CHOICE_TAG", xchoicetag);
        inData.put("TRADE_ID", tradeId);
        inData.put("SALE_FEE", saleFee);
        inData.put("PARA_VALUE4", para_value4);
        inData.put("X_GETMODE", x_getmode);
        IDataset result = CSAppCall.callTerminal("TM.FourCodesToOneSvc.fourCodesToOneStateMgr", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    public static IDataset modifyTerminalState(String resNo, String tradeId, String paraCode1, String saleFee, String sn, String campnId, String userId, String deviceCost) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_NO", resNo);
        inData.put("SALE_FEE", saleFee);
        inData.put("X_CHOICE_TAG", "0");// 受理
        inData.put("PARA_CODE1", paraCode1);
        inData.put("TRADE_ID", tradeId);
        if (StringUtils.isNotBlank(sn))
        {
            inData.put("SERIAL_NUMBER", sn);
        }
        if (StringUtils.isNotBlank(campnId))
        {
            inData.put("PARA_VALUE3", campnId);
        }
        if (StringUtils.isNotBlank(userId))
        {
            inData.put("PARA_VALUE4", userId);
        }
        if (StringUtils.isNotBlank(deviceCost))
        {
            inData.put("DEVICE_COST", deviceCost);
        }
        inData.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

        // IDataset result = CSAppCall.call("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateMgr", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateMgr", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    /**
     * 修改状态并上发集团
     * 
     * @param resNo
     * @param tradeId
     * @param paraCode1
     * @param saleFee
     * @param sn
     * @param campnId
     * @param userId
     * @param deviceCost
     * @return
     * @throws Exception
     */
    public static IDataset modifyTerminalStateAndUpData(String resNo, String tradeId, String paraCode1, String saleFee, String sn, String campnId, String userId, String deviceCost, TerminalUpData upData) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_NO", resNo);
        inData.put("SALE_FEE", saleFee);
        inData.put("X_CHOICE_TAG", "0");// 受理
        inData.put("PARA_CODE1", paraCode1);
        inData.put("TRADE_ID", tradeId);
        if (StringUtils.isNotBlank(sn))
        {
            inData.put("SERIAL_NUMBER", sn);
        }
        if (StringUtils.isNotBlank(campnId))
        {
            inData.put("PARA_VALUE3", campnId);
        }
        if (StringUtils.isNotBlank(userId))
        {
            inData.put("PARA_VALUE4", userId);
        }
        if (StringUtils.isNotBlank(deviceCost))
        {
            inData.put("DEVICE_COST", deviceCost);
        }
        inData.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
        inData.putAll(upData.toData());

        // IDataset result = CSAppCall.call("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateAndUp", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateAndUp", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    /**
     * 光猫校验、预占、查询OLT厂商是否与光猫厂商一致
     * 
     * @param resNo
     * @param houseId
     * @param coverType
     * @param servcieType
     * @param vendorName
     * @param custName
     * @param custSn
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-12-19
     */
    public static IDataset occupyEmodel(String resNo, String houseId, String coverType, String servcieType, String vendorName, String custName, String custSn) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_NO", resNo);// 光猫SN
        param.put("HOUSE_ID", houseId);// 房号
        param.put("SERVICE_TYPE", servcieType);// 产品类型
        param.put("COVER_TYPE", coverType);// 覆盖类型
        param.put("VENDOR_NAME", vendorName);// 覆盖类型
        param.put("USR_NAME", custName);
        param.put("USR_PHONE_NUMBER", custSn);
        setPubParam(param);

        IDataset result = CSAppCall.callTerminal("EM.EmodelIntfSvc.occupyEmodel", param, true).getData();

        return result;
    }

    public static IData occupyTerminalByTerminalId(String resNo, String saleStaffId, String serialNumber, String saleTag, String netOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo");
        param.put("Reserve_id", netOrderId);
        param.put("RES_TYPE_CODE", "4"); // 资源类型
        param.put("RES_ID", ""); // 资源实物编码
        param.put("RES_NO", resNo); // 资源号
        param.put("STAFF_ID", saleStaffId); // 促销员工
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("INFO_TAG", "1");
        param.put("SALE_TAG", saleTag);

        // TODO call huaW terminal intf

        IData map = new DataMap();
        map.put("RES_CODE", resNo); // 实物编码
        map.put("DEVICE_MODEL_CODE", "2009103114401066");// 终端类型编码
        map.put("DEVICE_MODEL", "手机"); // 终端型号名称
        map.put("DEVICE_COST", "510000"); // 终端成本价(结算价(进货价))
        map.put("DEVICE_BRAND", "三星"); // 终端品牌名称
        map.put("DEVICE_BRAND_CODE", "SANSUNG");// 终端品牌编码
        map.put("SUPPLY_COOP_ID", "00401"); // 提供商编码
        map.put("TERMINAL_TYPE_CODE", "04"); // 某一终端类型编码
        map.put("TERMINAL_STATE", "0"); // 在途状态
        map.put("SALE_PRICE", "5000"); // 终端销售价--rsrv_str6
        map.put("DEPUTY_FEE", "1000"); // 代办费 rsrv_str7
        map.put("IS_INTELL_TERMINAL", "1"); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
        map.put("RSRV_STR3", "红色"); // 终端颜色描述
        map.put("RSRV_STR4", "理电池"); // 终端电池配置
        return map;
    }

    /**
     * 移动宽带调用终端服务，查询覆盖地址的OLT厂商资料
     * 
     * @param houseId
     * @param coverType
     * @param servcieType
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-12-19
     */
    public static IDataset qryBDCoverInfo(String houseId, String coverType, String servcieType) throws Exception
    {
        IData param = new DataMap();
        param.put("HOUSE_ID", houseId);
        param.put("COVER_TYPE", coverType);
        param.put("SERVICE_TYPE", servcieType);
        setPubParam(param);

        IDataset result = CSAppCall.callTerminal("PB.PbossResIntfSvc.coverInfo", param, true).getData();

        return result;
    }

    public static int queryOccupyTerminalCount(String stockId, String terminalModelCode) throws Exception
    {
        int count = 0;
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RANDOM_NO", stockId);
        inData.put("RSRV_STR1", terminalModelCode);

        IDataset result = CSAppCall.callTerminal("TM.TerminalProcessInNetSvc.queryOccupyCount", inData, false).getData();
        judgeXResultCode(result);
        if (IDataUtil.isNotEmpty(result))
        {
            count = result.getData(0).getInt("RECORDCOUNT");
        }

        return count;
    }

    public static IDataset queryResParaInfo(String eparchyCode, String paraAttr, String paraCode1, String paraCode2) throws Exception
    {
        IData data = new DataMap();
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PARA_ATTR", paraAttr);
        data.put("PARA_CODE1", paraCode1);
        data.put("PARA_CODE2", paraCode2);
        IDataset result = CSAppCall.callTerminal("TM.ResParaMessQuerySvc.queryResParaInfo", data, false).getData();
        judgeXResultCode(result);
        return result;
    }

    public static IDataset queryTerminalBasicInfo(String terminalId, String eparchyCode) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_NO", terminalId);
        inData.put("EPARCHY_CODE", eparchyCode);
        inData.put("RES_TRADE_CODE", "IGetTerminalInfo");

        IDataset result = CSAppCall.callTerminal("TM.TerminalMessQuerySvc.queryTerminalBasicInfo", inData, false).getData();
        judgeXResultCode(result);
        return result;
    }

    /**
     * * 按部门查询终端数量
     * 
     * @param stockId
     * @param terminalModelCode
     * @return
     * @throws Exception
     */
    public static int queryTerminalCount(String stockId, String terminalModelCode) throws Exception
    {
        int count = 0;
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("STOCK_ID", stockId);
        inData.put("TERMINAL_MODEL_CODE", terminalModelCode);

        IDataset result = CSAppCall.callTerminal("TM.TerminalMessQuerySvc.countIdleTerminalNum", inData, false).getData();
        judgeXResultCode(result);
        if (IDataUtil.isNotEmpty(result))
        {
            count = result.getData(0).getInt("RECORDCOUNT");
        }

        return count;
    }

    public static IDataset queryTerminalForSale(String terminalId, String brandCode, String terminalModelCode) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("TERMINAL_ID", terminalId);
        inData.put("BRAND_CODE", brandCode);
        inData.put("TERMINAL_MODEL_CODE", terminalModelCode);

        IDataset result = CSAppCall.callTerminal("TM.TerminalMessQuerySvc.queryTerminalForSale", inData, false).getData();
        judgeXResultCode(result);
        return result;
    }

    public static IDataset queryTerminalInfoInCen(String terminalId, String stockId, String terminalState) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("TERMINAL_ID", terminalId);
        if (StringUtils.isNotBlank(stockId))
        {
            inData.put("STOCK_ID", stockId);
        }
        if (StringUtils.isNotBlank(terminalState))
        {
            inData.put("TERMINAL_STATE", terminalState);
        }

        // IDataset result = CSAppCall.call("TM.TerminalMessQuerySvc.queryTerminalInfoInCen", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalMessQuerySvc.queryTerminalInfoInCen", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    /**
     * 光猫释放
     * 
     * @param tradeId
     * @param usrNumber
     * @param resNoRelease
     * @return
     * @throws Exception
     *             wangjx 2013-12-19
     */
    public static IDataset releaseEmodel(String tradeId, String usrNumber, String resNoRelease) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USR_NUMBER", usrNumber);
        param.put("RES_NO_RELEASE", resNoRelease);
        setPubParam(param);

        IDataset result = CSAppCall.callTerminal("EM.EmodelIntfSvc.releaseEmodel", param, true).getData();

        return result;
    }

    private static void setPubParam(IData param) throws Exception
    {
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    }

    public static IDataset terminalTempOccupy(String resNo, String resId, String resTypeCode, String productId, String routeEparchyCode) throws Exception
    {
        // 通用入参：TRADE_EPARCHY_CODE=0731,ROUTE_EPARCHY_CODE=0731,TRADE_CITY_CODE=A31G,
        // TRADE_DEPART_ID=59804,TRADE_STAFF_ID=SUPERUSR,IN_MODE_CODE=0
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_NO", resNo);
        if (StringUtils.isNotBlank(resId))
        {
            inData.put("RES_ID", resId);
        }
        if (StringUtils.isNotBlank(resTypeCode))
        {
            inData.put("RES_TYPE_CODE", resTypeCode);
        }
        if (StringUtils.isNotBlank(productId))
        {
            inData.put("PRODUCT_ID", productId);
        }
        if (StringUtils.isNotBlank(routeEparchyCode))
        {
            inData.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        }
        inData.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo");

        // IDataset result = CSAppCall.call("TM.TerminalSaleCheckSvc.checkTerminalSale", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalSaleCheckSvc.checkTerminalSale", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    public static IDataset undoFourCodesToOneStateChangeAndUp(String serialNumber, String resNo, String choiceTag, String tradeId, String saleFee, String routeEparchyCode, TerminalUpData terminalUpData) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("RES_NO", resNo);
        inData.put("RES_TRADE_CODE", "IFourCodesToOne");
        inData.put("X_TAG", "2");
        inData.put("TRADE_ID", tradeId);
        inData.put("SALE_FEE", saleFee);
        inData.put("X_CHOICE_TAG", choiceTag);
        inData.put("X_GETMODE", "1");
        if (StringUtils.isNotBlank(routeEparchyCode))
        {
            inData.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);
        }
        inData.putAll(terminalUpData.toData());

        IDataset result = CSAppCall.callTerminal("TM.FourCodesToOneSvc.fourCodesToOneStateMgrAndUp", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    public static IDataset undoModifyTerminalStateAndUpData(String resNo, String tradeId, String paraCode1, String saleFee, String sn, String campnId, String userId, String deviceCost, TerminalUpData upData) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.put("RES_NO", resNo);
        inData.put("SALE_FEE", saleFee);
        inData.put("X_CHOICE_TAG", "1");// 退机
        inData.put("PARA_CODE1", paraCode1);
        inData.put("TRADE_ID", tradeId);
        if (StringUtils.isNotBlank(sn))
        {
            inData.put("SERIAL_NUMBER", sn);
        }
        if (StringUtils.isNotBlank(campnId))
        {
            inData.put("PARA_VALUE3", campnId);
        }
        if (StringUtils.isNotBlank(userId))
        {
            inData.put("PARA_VALUE4", userId);
        }
        if (StringUtils.isNotBlank(deviceCost))
        {
            inData.put("DEVICE_COST", deviceCost);
        }
        inData.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
        inData.putAll(upData.toData());

        // IDataset result = CSAppCall.call("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateAndUp", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalSaleModifyStateSvc.terminalSaleModifyStateAndUp", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    public static IDataset upTerminalInfoToGrp(TerminalUpData terminalUpData) throws Exception
    {
        IData inData = new DataMap();
        setPubParam(inData);
        inData.putAll(terminalUpData.toData());
        inData.put("RES_TRADE_CODE", "IMobileTerminalUse");
        // IDataset result = CSAppCall.call("TM.TerminalUpCheckSvc.checkTerminalUpload", inData);
        IDataset result = CSAppCall.callTerminal("TM.TerminalSaleDataUpSvc.commitTerminalSaleData", inData, false).getData();

        judgeXResultCode(result);

        return result;
    }

    /**
     * 光猫占用
     * 
     * @param resNoSale
     * @param usrNumber
     * @param tradeId
     * @param saleFee
     * @param custName
     * @param custSn
     * @return IDataset
     * @throws Exception
     *             wangjx 2013-12-19
     */
    public static IDataset useEmodel(String resNoSale, String usrNumber, String tradeId, String saleFee, String custName, String custSn) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_NO_SALE", resNoSale);
        param.put("USR_NUMBER", usrNumber);
        param.put("TRADE_ID", tradeId);
        param.put("SALE_FEE", saleFee);
        param.put("USR_NAME", custName);
        param.put("USR_PHONE_NUMBER", custSn);
        setPubParam(param);

        IDataset result = CSAppCall.callTerminal("EM.EmodelIntfSvc.useEmodel", param, true).getData();

        return result;
    }
}
