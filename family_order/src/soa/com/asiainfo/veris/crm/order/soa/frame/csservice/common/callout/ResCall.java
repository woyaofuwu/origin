
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.BizVisit;
import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;

public class ResCall
{
    protected static Logger log = Logger.getLogger(ResCall.class);

    /**
     * 写卡信息回写
     *
     * @param write_tag
     *            不用填写
     * @param FEE_TAG
     *            费用表示 0-未回缴；1-买断未回缴；2-已返还；3-已回缴。不填默认为0
     * @param REMOTE_MODE
     *            1-异地；不填默认为本地远程写卡
     * @param X_CHOICE_TAG
     *            写卡标识 0-成功；1-失败；不填默认为0
     */
    public static IDataset backWriteOnLineSaleCard(IData input) throws Exception
    {
        setPublicParam(input);
        return callRes("RCF.resource.ISimcardIntfOperateSV.backWriteOnLineSaleCard",input);
    }

    /**
     * 选号接口(selectSvcNum)
     */
    public static IDataset queryVAProductBrand(IData reqInfo) throws Exception {
        setPublicParam(reqInfo);
        return callRes("RCF.resource.ITermIntfQuerySV.getTermBrandByType", reqInfo);
    }

    /**
     * 选号接口(selectSvcNum)
     */
    public static IDataset selectSvcNum(IData reqInfo) throws Exception {
        setPublicParam(reqInfo);
        return callRes("RCF.resource.INumberIntfQuerySV.selectSvcNum", reqInfo);
    }
/**
   	 * @Function: getMphoneCodeInfoByResNo()
   	 * @Description:查询号码信息
   	 * @param:QRY_TAG(0) 1:表示查询use表，0：表示查询idle表，如果传入值为空或其他值先查use表再查idle表
   	 * @return：
   	 * @throws：异常描述
   	 * @version: v1.0.0
   	 * @author: yxd
   	 * @date: 2015-1-15 上午9:51:33 Modification History: Date Author Version
   	 *        Description
   	 *        ---------------------------------------------------------
   	 *        2015-1-15 yxd v1.0.0 修改原因
   	 */
   	public static IDataset getMphoneCodeInfoByResNo(String serialNumber, String qryTag) throws Exception
   	{
   		IData inData = new DataMap();
   		inData.put("SERIAL_NUMBER", serialNumber);
   		inData.put("QRY_TAG", qryTag);
   		setPublicParam(inData);
   		return callRes("RCF.resource.INumberIntfOperateSV.getPhoneInfoByNum", inData);
   	}    /*
     * 一号多終端登记完后回退释放预占资源
     */
    public static IDataset releaseMdrpRes(String resNo,String resTypeCode) throws Exception{
        IData inData = new DataMap();
        inData.put("RES_NO", resNo);
        inData.put("RES_TYPE_CODE", resTypeCode);
        
        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
        inData.put("USER_EPARCHY_CODE", CSBizBean.getUserEparchyCode());// 用户交易地州
        
        return callRes("RCF.resource.IResPublicIntfOperateSV.releaseAllResByNo", inData);
    }

    /**
     * 3.3.1.26.预占号码列表查询 (queryPhoneList)
     * 实名认证平台在APP+NFC模式做开户全流程中，根据客户身份证信息查询预占号码列表。
     */
    public static IDataset onlineQueryPhoneList(IData input) throws Exception {
        IData inData = new DataMap();
        IData params = new DataMap();
        setPublicParam(params);
        params.put("busiCode", input.getString("busiCode"));
        params.put("sourceCode", input.getString("sourceCode"));
        params.put("targetCode", input.getString("targetCode"));
        params.put("version", input.getString("version"));
        IData reqInfo = new DataMap();
        params.put("reqInfo", reqInfo);
        reqInfo.put("transactionID", input.getData("reqInfo").getString("transactionID"));
        reqInfo.put("operCode", input.getData("reqInfo").getString("operCode"));
        reqInfo.put("channelId", input.getData("reqInfo").getString("channelId"));
        reqInfo.put("custCertNo", input.getData("reqInfo").getString("custCertNo"));
        return callRes("RCF.resource.INumberIntfQuerySV.onlineQueryPhoneList", params);
    }
    /**
     * 3.3.1.32.SIM卡校验和预占接口(checkSim)
     * SIM卡校验和预占接口
     * 实名认证系统在线上选号业务流程中省公司配送订单信息环节，新用户实名认证后，将用户激活信息传递给接入方做激活操作。
     */
    public static IDataset onlineCheckSim(IData input) throws Exception {
        IData inData = new DataMap();
        IData params = new DataMap();
        setPublicParam(params);
        params.put("sourceCode", input.getString("sourceCode"));
        params.put("targetCode", input.getString("targetCode"));
        params.put("busiCode", input.getString("busiCode"));
        params.put("version", input.getString("version"));
        params.put("transactionID", input.getData("reqInfo").getString("transactionID"));
        params.put("IN_MODE_CODE", "0");
        IData reqInfo = new DataMap();
        params.put("reqInfo", reqInfo);
        reqInfo.put("simType", input.getData("reqInfo").getString("simType", ""));
        reqInfo.put("channelId", input.getData("reqInfo").getString("channelId", ""));
        reqInfo.put("sim", input.getData("reqInfo").getString("sim", ""));
        reqInfo.put("svcNum", input.getData("reqInfo").getString("svcNum", ""));
        reqInfo.put("operCode", input.getData("reqInfo").getString("operCode", ""));
        reqInfo.put("transactionID", input.getData("reqInfo").getString("transactionID", ""));
        reqInfo.put("xChoiceTag", input.getData("reqInfo").getString("xChoiceTag", ""));
        return callRes("RCF.resource.ISimcardIntfOperateSV.onlineCheckSim", params);
    }

    /**
     * 获取写卡基础数据(queryWriteCardBasicData)
     * @param svcNum 手机号码
     * @param EMPTY_CARD_ID 空白卡序列号,远程写卡时必传
     * @param OCCUPY_TIME_CODE 0:24小时；1:30分钟；2:当天 ，默认为2，可以不传
     * @param IS_TURNNET 1:携转，0:非携转,默认为0，可以不传
     * @return
     * @throws Exception
     */
    public static IDataset queryWriteCardBasicData(String svcNum, String EMPTY_CARD_ID, String OCCUPY_TIME_CODE, String IS_TURNNET) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("svcNum", svcNum); //手机号码
        if(StringUtils.isBlank(OCCUPY_TIME_CODE)){
            OCCUPY_TIME_CODE = "2";
        }
        inData.put("OCCUPY_TIME_CODE", OCCUPY_TIME_CODE); //0:24小时；1:30分钟；2:当天 ，默认为2，可以不传
        if(StringUtils.isNotBlank(EMPTY_CARD_ID)){
            inData.put("EMPTY_CARD_ID", EMPTY_CARD_ID); //空白卡序列号,远程写卡时必传
        }
        if(StringUtils.isBlank(IS_TURNNET)){
            IS_TURNNET = "0";
        }
        inData.put("IS_TURNNET", IS_TURNNET); //1:携转，0:非携转,默认为0，可以不传
        return callRes("RCF.resource.ISimcardIntfOperateSV.queryWriteCardBasicData", inData);
        //RCF.resource.ISimcardIntfOperateSV.queryWriteCardInfo
    }

    /**
     * 写卡信息获取接口(queryWriteCardInfo)
     * @param svcNum 手机号码
     * @param sn 空白卡序列号,远程写卡时必传
     * @param REMOTE_MODE 0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡 ，默认为2
     * @param OCCUPY_TIME_CODE 0:24小时；1:30分钟；2:当天 ，默认为2，可以不传
     * @param IS_TURNNET 1:携转，0:非携转,默认为0，可以不传
     * @return
     * @throws Exception
     */
    public static IDataset queryWriteCardInfo(String svcNum, String sn, String REMOTE_MODE, String OCCUPY_TIME_CODE, String IS_TURNNET) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("svcNum", svcNum); //手机号码
        inData.put("sn", sn); //空白卡序列号
        inData.put("EMPTY_CARD_ID", sn); //空白卡序列号

        if(StringUtils.isBlank(REMOTE_MODE)){
            REMOTE_MODE = "2";
        }
        inData.put("REMOTE_MODE", REMOTE_MODE); //0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡 默认为2

        if(StringUtils.isBlank(OCCUPY_TIME_CODE)){
            OCCUPY_TIME_CODE = "2";
        }
        inData.put("OCCUPY_TIME_CODE", OCCUPY_TIME_CODE); //0:24小时；1:30分钟；2:当天 ，默认为2，可以不传

        if(StringUtils.isBlank(IS_TURNNET)){
            IS_TURNNET = "0";
        }
        inData.put("IS_TURNNET", IS_TURNNET); //1:携转，0:非携转,默认为0，可以不传
        return callRes("RCF.resource.ISimcardIntfOperateSV.queryWriteCardInfo", inData);
        //RCF.resource.ISimcardIntfOperateSV.queryWriteCardInfo
    }
    /**
     * 集团远程写卡信息获取接口(queryWriteCardInfoByCreditPay)
     * @param svcNum 手机号码
     * @param sn 空白卡序列号,远程写卡时必传
     * @param REMOTE_MODE 0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡 ，默认为2
     * @param OCCUPY_TIME_CODE 0:24小时；1:30分钟；2:当天 ，默认为2，可以不传
     * @param IS_TURNNET 1:携转，0:非携转,默认为0，可以不传
     * @param CREDIT_PAY_TAG 通过标识CREDIT_PAY_TAG默认设定卡商编码为E，以便网络开通找K4秘钥
     * @return
     * @throws Exception
     */
	public static IDataset queryWriteCardInfoByCreditPay(String svcNum, String sn, String REMOTE_MODE, String OCCUPY_TIME_CODE, String IS_TURNNET) throws Exception
	{
		IData inData = new DataMap();
		setPublicParam(inData);
		inData.put("svcNum", svcNum); //手机号码
		inData.put("sn", sn); //空白卡序列号
		inData.put("EMPTY_CARD_ID", sn); //空白卡序列号

        if(StringUtils.isBlank(REMOTE_MODE)){
            REMOTE_MODE = "2";
        }
		inData.put("REMOTE_MODE", REMOTE_MODE); //0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡 默认为2

        if(StringUtils.isBlank(OCCUPY_TIME_CODE)){
            OCCUPY_TIME_CODE = "2";
        }
		inData.put("OCCUPY_TIME_CODE", OCCUPY_TIME_CODE); //0:24小时；1:30分钟；2:当天 ，默认为2，可以不传

        if(StringUtils.isBlank(IS_TURNNET)){
            IS_TURNNET = "0";
        }
		inData.put("IS_TURNNET", IS_TURNNET); //1:携转，0:非携转,默认为0，可以不传
		inData.put("CREDIT_PAY_TAG", "1");//信用购机标识 
		return callRes("RCF.resource.ISimcardIntfOperateSV.queryWriteCardInfo", inData);
		//RCF.resource.ISimcardIntfOperateSV.queryWriteCardInfo
	}
    /**
     * @param oldSimCardNo
     *            老SIM卡号
     * @param newSimCardNo
     *            新SIM卡号
     * @return X_RESULTCODE 0成功,1失败 X_RESULTINFO 返回信息 X_RESULTNUM 返回信息数量
     * @throws Exception
     */
    public static IDataset backupCardActivate(String oldSimCardNo, String newSimCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OLD_SIM_CARD_NO", oldSimCardNo);
        inData.put("NEW_SIM_CARD_NO", newSimCardNo);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.backupCardActivate",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.backupCardActivate", inData);
    }

    /**
     * @param SIM_CARD_NO
     *            新SIM卡号
     * @return X_RESULTCODE 0成功,1失败 X_RESULTINFO 返回信息 X_RESULTNUM 返回信息数量
     * @throws Exception
     */
    public static IDataset backupCardCancel(String newSimCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", newSimCardNo);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.specCompCardDeal",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.backupCardCancel", inData);
    }

    public static IDataset specCompCardDeal(IData param) throws Exception
    {
        setPublicParam(param);
        return callRes("RCF.resource.ISimcardIntfOperateSV.specCompCardDeal",param);//CSAppCall.call("RM.ResSimCardIntfSvc.backupCardCancel", inData);
    }

    /**
     * @param oldSimCardNo
     *            老SIM卡号
     * @param newSimCardNo
     *            新SIM卡号
     * @return X_RESULTCODE 0成功,1失败 X_RESULTINFO 返回信息 X_RESULTNUM 返回信息数量
     * @throws Exception
     */
    public static IDataset backupCardReturnSale(String oldSimCardNo, String newSimCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OLD_SIM_CARD_NO", oldSimCardNo);
        inData.put("NEW_SIM_CARD_NO", newSimCardNo);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.backupCardReturnSale",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.backupCardReturnSale", inData);
    }

    /**
     * 写卡信息回写
     *
     * @param write_tag
     *            不用填写
     * @param FEE_TAG
     *            费用表示 0-未回缴；1-买断未回缴；2-已返还；3-已回缴。不填默认为0
     * @param REMOTE_MODE
     *            1-异地；不填默认为本地远程写卡
     * @param X_CHOICE_TAG
     *            写卡标识 0-成功；1-失败；不填默认为0
     */
    public static IDataset backWriteSimCard(String imsiA, String emptyCardId, String imsiB, String eparchyCode, String writeTag, String simCardNoA, String simCardNoB, String feeTag, String remoteMode, String xChoiceTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("IMSI_A", imsiA);
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("IMSI_B", imsiB);
        inData.put("EPARCHY_CODE", eparchyCode);
        inData.put("WRITE_TAG", writeTag);
        inData.put("SIM_CARD_NO_A", simCardNoA);
        inData.put("SIM_CARD_NO_B", simCardNoB);
        inData.put("FEE_TAG", feeTag);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.remoteWriteCardRet",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteCardRet", inData);
    }

    /**
     * 写卡信息回写
     *
     * @param write_tag
     *            不用填写
     * @param FEE_TAG
     *            费用表示 0-未回缴；1-买断未回缴；2-已返还；3-已回缴。不填默认为0
     *      * @param REMOTE_MODE
     *            1-异地；不填默认为本地远程写卡
     * @param X_CHOICE_TAG
     *            写卡标识 0-成功；1-失败；不填默认为0
     */
    public static IDataset backWriteSimCard(String imsiA, String emptyCardId, String imsiB, String eparchyCode, String writeTag, String simCardNoA, String simCardNoB, String feeTag, String remoteMode, String xChoiceTag,String ki,String opc) throws Exception
    {
        IData inData = new DataMap();
        inData.put("IMSI_A", imsiA);
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("IMSI_B", imsiB);
        inData.put("EPARCHY_CODE", eparchyCode);
        inData.put("WRITE_TAG", writeTag);
        inData.put("SIM_CARD_NO_A", simCardNoA);
        inData.put("SIM_CARD_NO_B", simCardNoB);
        inData.put("FEE_TAG", feeTag);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        inData.put("KI", ki);
        inData.put("OPC", opc);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.remoteWriteCardRet",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteCardRet", inData);
    }


    /**
     * 跨区回写接口
     *
     *@param imsi:IMSI
     *            0-未写卡；2-正在写卡；3-写卡成功；4-写卡失败；
     * @param write_tag
     *            0-未写卡；2-正在写卡；3-写卡成功；4-写卡失败；
     * @param SIM_CARD_NO_A:卡号
     * @param REMOTE_MODE
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡    跨区写卡传1
     * @param X_CHOICE_TAG
     *            写卡标识 0-成功；1-失败；
     * @param KI:KI  请调用加密机先解密再加密
     * @param OPC:OPC 请调用加密机先解密再加密
     */
    public static IDataset backWriteSimCardByIntf(String imsi,String writeTag,String simCardNo,String remoteMode, String xChoiceTag,String ki,String opc,String serialNumber, String emptycardno) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", emptycardno);// 空白卡序列号
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getUserEparchyCode());// 用户归属地州
        inData.put("IMSI_A", imsi);
        inData.put("WRITE_TAG", writeTag);
        inData.put("SIM_CARD_NO_A", simCardNo);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        inData.put("KI", ki);
        inData.put("OPC", opc);
        inData.put("SERIAL_NUMBER", serialNumber);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.remoteWriteCardRet",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteCardRet", inData);
    }

    /**
     * 跨区回写接口
     *
     *@param imsi:IMSI
     *            0-未写卡；2-正在写卡；3-写卡成功；4-写卡失败；
     * @param write_tag
     *            0-未写卡；2-正在写卡；3-写卡成功；4-写卡失败；
     * @param SIM_CARD_NO_A:卡号
     * @param REMOTE_MODE
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡    跨区写卡传1
     * @param X_CHOICE_TAG
     *            写卡标识 0-成功；1-失败；
     * @param KI:KI  请调用加密机先解密再加密
     * @param OPC:OPC 请调用加密机先解密再加密
     */
    public static IDataset backWriteSimCardByIntfAndCardSN(String imsi,String writeTag,String simCardNo,String remoteMode, String xChoiceTag,String ki,String opc,String serialNumber,String emptyCardId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("CARD_SN_ID", emptyCardId);
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getUserEparchyCode());// 用户归属地州
        inData.put("IMSI_A", imsi);
        inData.put("WRITE_TAG", writeTag);
        inData.put("SIM_CARD_NO_A", simCardNo);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        inData.put("KI", ki);
        inData.put("OPC", opc);
        inData.put("SERIAL_NUMBER", serialNumber);
        //setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.remoteWriteCardRet",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteCardRet", inData);
    }
    /**
     * 跨区回写接口
     *
     *@param imsi:IMSI
     *            0-未写卡；2-正在写卡；3-写卡成功；4-写卡失败；
     * @param write_tag
     *            0-未写卡；2-正在写卡；3-写卡成功；4-写卡失败；
     * @param SIM_CARD_NO_A:卡号
     * @param REMOTE_MODE
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡    跨区写卡传1
     * @param X_CHOICE_TAG
     *            写卡标识 0-成功；1-失败；
     * @param KI:KI  请调用加密机先解密再加密
     * @param OPC:OPC 请调用加密机先解密再加密
     */
    public static IDataset backWriteSimCardByIntf(String imsi,String writeTag,String simCardNo,String remoteMode, String xChoiceTag,String ki,String opc,String serialNumber) throws Exception
    {
        IData inData = new DataMap();
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getUserEparchyCode());// 用户归属地州
        inData.put("IMSI_A", imsi);
        inData.put("WRITE_TAG", writeTag);
        inData.put("SIM_CARD_NO_A", simCardNo);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        inData.put("KI", ki);
        inData.put("OPC", opc);
        inData.put("SERIAL_NUMBER", serialNumber);
        //setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.remoteWriteCardRet",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteCardRet", inData);
    }
    /**
     * 票据取消作废
     *
     * @param ticketId
     *            票据编号
     * @param taxNo
     *            税务登记号
     * @param RES_KIND_CODE
     *            记录是发票还是收据
     * @return
     * @throws Exception
     */
    public static IDataset cancelStaffTicket(String resKindCode, String ticketId, String taxNo) throws Exception
    {
        IData callParam = new DataMap();

        IDataset ds = new DatasetList();
        IData param = new DataMap();
        param.put("TICKET_ID", ticketId);
        param.put("TAX_NO", taxNo);
        param.put("RES_KIND_CODE", resKindCode);

        ds.add(param);
        callParam.put("INVALID_TICKET_INFOS", ds);
        setPublicParam(callParam);
        return callRes("RCF.resource.IInvoiceIntfOperateSV.ticketInvalid",param);//CSAppCall.call("RM.ResTicketIntfSvc.ticketInvalid", callParam);
    }

    /**
     * 买断卡激活
     *
     * @author sunxin
     * @param visit
     * @param IMSI
     * @return
     * @throws Exception
     */
    public static void cardSaleActive(String imsi) throws Exception
    {
        IData inData = new DataMap();
        inData.put("IMSI", imsi);// IMSI
        setPublicParam(inData);
        callRes("RCF.resource.ISimcardIntfOperateSV.simCardSaleActive",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.cardSaleActive", inData);
    }

    /**
     * 补换卡SIM卡返销
     */
    public static IDataset changeSimCardCancel(String serialNumber, String oldSimCardNo, String newSimCardNo,String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber); // 库标识
        inData.put("OLD_SIM_CARD_NO", oldSimCardNo); // SIM卡号
        inData.put("NEW_SIM_CARD_NO", newSimCardNo); // 员工号
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.changeSimCardCancel",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.changeSimCardCancel", inData);
    }

    /**
     * sim卡占用,老SIM信息释放，对应sim信息更改
     *
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param res_type_code
     *            //资源类型 【无用参数】
     * @return
     * @throws Exception
     */
    public static IDataset changeSimCardFinish(String occupyTypeCode, String serialNumber, String simCardNo, String checkTag, String tradeId, String tradeTypeCode, String resTypeCode, String userId, String agentFee, String advanceFee,
            String productId, String netTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simCardNo);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("X_CHECK_TAG", checkTag);
        inData.put("TRADE_ID", tradeId);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("USER_ID", userId);
        inData.put("AGENT_FEE", agentFee);
        inData.put("ADVANCE_FEE", advanceFee);
        inData.put("PRODUCT_ID", productId);
        setPublicParam(inData);
      /*  if ("07".equals(netTypeCode))
        {
            inData.put("X_GET_MODE", "0");
            return CSAppCall.call("TM.ResSimCardIntfSvc.useOccupySimCard", inData);
        }*/
        return callRes("RCF.resource.ISimcardIntfOperateSV.changeSimCardFinish",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.changeSimCardFinish", inData);
    }

    /**
     * @Function:
     * @Description: 无线固话换卡返销
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-29 上午11:03:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-29 chengxf2 v1.0.0 修改原因
     */
    /*public static IDataset changeTSimCardCancel(String serialNumber, String oldImsi, String oldSimCardNo, String newSimCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber); // 库标识
        inData.put("IMSI", oldImsi); // 老IMSI
        inData.put("OLD_SIM_CARD_NO", oldSimCardNo); // 老SIM卡号
        inData.put("NEW_SIM_CARD_NO", newSimCardNo); // 新SIM卡号
        setPublicParam(inData);
        return CSAppCall.call("TM.ResSimCardIntfSvc.changeSimCardCancel", inData);//del
    }*/

    /**
     * @param 有价卡换卡去激活
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @return
     * @throws Exception
     */
    public static IDataset changeValuecardReturnInfo(String res_no_s, String res_no_e, String res_type_code, String stock_id, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("STOCK_ID", stock_id);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.changeValuecardReturnInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.changeValuecardReturnInfo", inData);
    }

    /**
     * 铁通固话号码校验
     *
     * @author liuzz
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            //手机号码
     * @param res_type_code
     *            //资源类型
     * @return
     * @throws Exception
     */
    /*public static IDataset checkResourceForFixLine(String resTypeCode, String x_get_mode, String serial_number, String xTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TRADE_CODE", "ICheckMphoneCodeInfo");
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("X_GETMODE", x_get_mode);
        inData.put("RES_CODE", serial_number);
        inData.put("RES_NO", serial_number);
        inData.put("X_TAG", xTag);

        inData.put("TRADE_TYPE_CODE", "10");
        inData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        inData.put("X_CHOICE_TAG", "0");// 非二次开户
        inData.put("X_CHECK_TAG", "1");// 查空闲表
        inData.put("OCCUPY_TYPE_CODE", "0");

        inData.put("PARA_VALUE1", "2");// 资源状态
        inData.put("PARA_VALUE7", "1");// 检查是否密码卡
        IDataset dataOutput = CSAppCall.call("TT.CheckResInfo", inData);//del
        return dataOutput;
    }*/

    /**
     * 物联网号码选占（包括释放选占资源,检查资源,选占资源）
     *
     * @author sunxin
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            // 检测号码
     * @param res_type_code
     *            //资源类型 0=号码 【无用参数】
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForIOTMphone(String occupy_type_code, String x_get_mode, String serial_number, String res_type_code, String depart_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
        inData.put("X_GET_MODE", x_get_mode); // 获取方式--
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("DEPART_ID", depart_id);// 选占部门 AGENT_DEPART_ID
        inData.put("OPEN_TYPE", com.asiainfo.veris.crm.order.pub.consts.PersonConst.IOT_OPEN);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.selOccupyPhoneNum", inData);//return CSAppCall.callTerminal("TM.ResPhoneIntfSvc.selOccupyPhoneNum", inData, false).getData();
    }




    /**
     * 物联网sim卡资源可用校验 sunxin
     *
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param res_type_code
     *            //资源类型 【无用参数】
     * @param depart_id
     *            //部门编码 为空，根据trade_depart_id选占，否则根据所传depart_id选占
     * @param is_release
     *            //为1，不释放同工号所选占sim卡,否则释放sim卡
     * @param x_choice_tag
     *            //查号码是否在资源测空闲【0：空闲（idle）,1：占用(USE)】资源默认查1
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForIOTSim(String occupy_type_code, String x_get_mode, String serial_number, String sim_card_no, String res_type_code, String depart_id, String is_release, String x_choice_tag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("X_GET_MODE", x_get_mode);
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("SIM_CARD_NO", sim_card_no); // 检测sim卡
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("DEPART_ID", depart_id);
        inData.put("IS_NOTRELEASE", is_release);
        inData.put("X_CHOICE_TAG", x_choice_tag);
        inData.put("OPEN_TYPE", com.asiainfo.veris.crm.order.pub.consts.PersonConst.IOT_OPEN);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);//CSAppCall.callTerminal("TM.ResSimCardIntfSvc.selOccupySimCard", inData, false).getData();
    }

    // 根据最新接口文档提供下列方法 ，sunxin 海南j2ee
 /**
     * 号码选占（包括释放选占资源,检查资源,选占资源）
     *
     * @author （开户已不用此接口）
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            // 检测号码
     * @param res_type_code
     *            //资源类型 0=号码 【无用参数】
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForMphone(String occupy_type_code, String serial_number, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 0-普通开户；1-网上开户；2-代理商开户；默认为0
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        return callRes("RCF.resource.INumberIntfOperateSV.selOccupyPhoneNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.selOccupyPhoneNum", inData);
    }
    //start--wangsc10--20181031
    public static IDataset checkResourceForTTphone(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        return callRes("RCF.resource.INumberIntfQuerySV.qryTTNumInfos",inData);
    }
    //end
    public static IDataset getTenImsiPhoneByCityCode(String occupy_type_code, String res_kind_code, String res_type_code, String city_code) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 0-普通开户；1-网上开户；2-代理商开户；默认为0
        inData.put("RES_KIND_CODE", res_kind_code); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("CITY_CODE", city_code);// 资源类型
        return callRes("RCF.resource.INumberIntfQuerySV.getTenImsiPhoneByCityCode", inData);
    }
    /**
     * 号码选占（包括释放选占资源,检查资源,选占资源）(批开用)
     *
     * @author sunxin
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            // 检测号码
     * @param res_type_code
     *            //资源类型 0=号码 【无用参数】
     * @param IS_NOTRELEASE
     *            //为1时表示只释放当前员工当前操作的号码，其他表示释放当前员工下所有选占的号码
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForMphone(String occupy_type_code, String serial_number, String res_type_code,String release) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 0-普通开户；1-网上开户；2-代理商开户；默认为0
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("IS_NOTRELEASE", release);// 不释放标记
        return callRes("RCF.resource.INumberIntfOperateSV.selOccupyPhoneNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.selOccupyPhoneNum", inData);
    }

    /**
     * 号码选占（包括释放选占资源,检查资源,选占资源）传递前台选择代理商
     *
     * @author sunxin
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            // 检测号码
     * @param res_type_code
     *            //资源类型 0=号码 【无用参数】
     * @param depart_id
     *            //部门 代理商需要使用
     * @param info_tag
     *            //处理网上预接口特殊标记
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForMphone(String occupy_type_code, String serial_number, String res_type_code, String depart_id, String info_tag) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 0-普通开户；1-网上开户；2-代理商开户；默认为0
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("INFO_TAG", info_tag);// 资源类型
        if (StringUtils.isNotEmpty(depart_id))
            inData.put("AGENT_DEPART_ID", depart_id);// 部门
        return callRes("RCF.resource.INumberIntfOperateSV.selOccupyPhoneNum", inData);//return CSAppCall.call("RM.ResPhoneIntfSvc.selOccupyPhoneNum", inData);
    }

    /**
     * SIM卡选占(批开用)
     *
     * @author sunxin
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            //0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param IS_NOTRELEASE
     *            //为1时表示只释放当前员工当前操作的sim卡，其他表示释放当前员工下所有sim选占的sim卡
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForSim(String occupy_type_code, String serial_number, String sim_card_no, String release) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("SIM_CARD_NO", sim_card_no); // 检测sim卡
        if (StringUtils.isNotEmpty(release))
        {
            inData.put("IS_NOTRELEASE", release);// 不释放标记
        }
        setPublicParam(inData);
        IDataset dataOutput = callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.selOccupySimCard", inData);
        return dataOutput;
    }

    /**
     * SIM卡选占 传递前台选择代理商
     *
     * @author sunxin
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            //0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param depart_id
     *            //部门 代理商需要使用
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForSimPreSale(String occupy_type_code, String serial_number, String sim_card_no, String psptId, String depart_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("SIM_CARD_NO", sim_card_no); // 检测sim卡
        setPublicParam(inData);
        if (StringUtils.isNotEmpty(psptId))
            inData.put("PSPT_ID", psptId);
        if (StringUtils.isNotEmpty(depart_id))
            inData.put("AGENT_DEPART_ID", depart_id);// 部门
        inData.put("PRE_SALE", "Y"); // 检测sim卡
        IDataset dataOutput = callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);
        return dataOutput;
    }

    /**
     * SIM卡选占 传递前台选择代理商
     *
     * @author sunxin
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            //0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param depart_id
     *            //部门 代理商需要使用
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForSim(String occupy_type_code, String serial_number, String sim_card_no, String psptId, String depart_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("SIM_CARD_NO", sim_card_no); // 检测sim卡
        setPublicParam(inData);
        if (StringUtils.isNotEmpty(psptId))
            inData.put("PSPT_ID", psptId);
        if (StringUtils.isNotEmpty(depart_id))
            inData.put("AGENT_DEPART_ID", depart_id);// 部门
        IDataset dataOutput = callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);//IDataset dataOutput = CSAppCall.call("RM.ResSimCardIntfSvc.selOccupySimCard", inData);
        return dataOutput;
    }

    /**
     * SIM卡选占
     *
     * @param simCardNo
     * @param serialNumber
     * @param occupyTypeCode
     * @param isNotrelease
     * @param psptId
     * @param xChoiceTag
     *            0-查询空闲表；1-查询使用表；默认不填为0。开户业务为0，复机、补换卡业务填1
     * @param remoteMode
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @param isTrunnet
     *            0-非携转用户；1-携转用户
     * @return
     * @throws Exception
     */

    public static IDataset checkResourceForSim(String simCardNo, String serialNumber, String occupyTypeCode, String isNotrelease, String psptId, String remoteMode, String xChoiceTag, String isTrunnet, String remark) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simCardNo);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("IS_NOTRELEASE", isNotrelease);
        inData.put("PSPT_ID", psptId);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        inData.put("IS_TURNNET", isTrunnet);
        inData.put("REMARK", remark);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.selOccupySimCard", inData);
    }

    /**
     * SIM卡选占
     *
     * @param simCardNo
     * @param serialNumber
     * @param occupyTypeCode
     * @param isNotrelease
     * @param psptId
     * @param xChoiceTag
     *            0-查询空闲表；1-查询使用表；默认不填为0。开户业务为0，复机、补换卡业务填1
     * @param remoteMode
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @param isTrunnet
     *            0-非携转用户；1-携转用户
     * @return
     * @throws Exception
     */

    public static IDataset checkResourceForSim(String simCardNo, String serialNumber, String occupyTypeCode, String isNotrelease, String psptId, String remoteMode, String xChoiceTag, String isTrunnet, String remark, String netTypeCode)
            throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simCardNo);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("IS_NOTRELEASE", isNotrelease);
        inData.put("PSPT_ID", psptId);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        inData.put("IS_TURNNET", isTrunnet);
        inData.put("REMARK", remark);
        setPublicParam(inData);
        if ("07".equals(netTypeCode)){
            //OPEN_TYPE为IOT_OPEN时只能用物联网卡,否则只能用普通卡
            inData.put("OPEN_TYPE", com.asiainfo.veris.crm.order.pub.consts.PersonConst.IOT_OPEN);
//            String departId = CSBizBean.getVisit().getDepartId();
//            return ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", departId, "", xChoiceTag);
        }

        return callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);
        //CSAppCall.call("RM.ResSimCardIntfSvc.selOccupySimCard", inData);
    }

    /**
     * SIM卡选占
     *
     * @param simCardNo
     * @param serialNumber
     * @param occupyTypeCode
     * @param isNotrelease
     * @param psptId
     * @param xChoiceTag
     *            0-查询空闲表；1-查询使用表；默认不填为0。开户业务为0，复机、补换卡业务填1
     * @param remoteMode
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @param isTrunnet
     *            0-非携转用户；1-携转用户
     * @param occupyTimeCode    // 0-24小时；1-30分钟；2或不传-当天
     * @return
     * @throws Exception
     */

    public static IDataset checkResourceForSim(String simCardNo, String serialNumber, String occupyTypeCode, String isNotrelease, String psptId, String remoteMode, String xChoiceTag, String isTrunnet, String remark, String netTypeCode, String occupyTimeCode)
            throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simCardNo);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("IS_NOTRELEASE", isNotrelease);
        inData.put("OCCUPY_TIME_CODE", occupyTimeCode);
        inData.put("PSPT_ID", psptId);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("X_CHOICE_TAG", xChoiceTag);
        inData.put("IS_TURNNET", isTrunnet);
        inData.put("REMARK", remark);
        setPublicParam(inData);
        if ("07".equals(netTypeCode))
        {
            String departId = CSBizBean.getVisit().getDepartId();
            return ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", departId, "", xChoiceTag);
        }

        return callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.selOccupySimCard", inData);
    }

    /**
     * 有价卡选占
     *
     * @author chenzm
     * @param visit
     * @param res_no_s
     *            开始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForValueCard(String res_no_s, String res_no_e, String stock_id, String res_type_code, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.selOccupyValueCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.getResInfo", inData);
    }


    /**
     * 流量卡选占
     *
     * @author chenzm
     * @param visit
     * @param res_no_s
     *            开始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForFlowValueCard(String res_no_s, String res_no_e, String stock_id, String res_type_code, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.selOccupyFlowCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.getFlowInfo", inData);
    }

    /**
     * &#x6539;&#x53f7;&#xff0c;&#x53f7;&#x7801;&#x9009;&#x5360;
     *
     * @param serialNumber
     * @param occupyTypeCode
     * @param occupyTimeCode
     * @param xChoiceTag
     * @param changeSn
     * @param oldSerialNumber
     * @param cityCode
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset checkResoureForPhone(String serialNumber, String occupyTypeCode, String occupyTimeCode, String xChoiceTag, String changeSn, String oldSerialNumber, String cityCode, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("RES_TYPE_CODE", "0");
        param.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        param.put("OCCUPY_TIME_CODE", occupyTimeCode);
        param.put("X_CHOICE_TAG", xChoiceTag);
        param.put("CHANGE_SN", changeSn);
        param.put("OLD_SERIAL_NUMBER", oldSerialNumber);
        param.put("CITY_CODE", cityCode);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(param);
        return callRes("RCF.resource.INumberIntfOperateSV.selOccupyPhoneNum",param);//CSAppCall.call("RM.ResPhoneIntfSvc.selOccupyPhoneNum", param);
    }

    /**
     * 票据资源校验
     *
     * @param visit
     * @param resKindCode
     * @param ticketId
     * @return
     * @throws Exception
     */
    /*public static IDataset checkTicket(String resKindCode, String ticketId, String taxNo) throws Exception
    {
        IData callParam = new DataMap();

        callParam.put("RES_KIND_CODE", resKindCode);
        callParam.put("TICKET_ID", ticketId);
        callParam.put("TAX_NO", taxNo);
        setPublicParam(callParam);
        return CSAppCall.call("RM.ResTicketIntfSvc.checkTicket", callParam);//del

    }*/

    /**
     * 虚拟卡校验
     *
     * @author chenzm
     * @param visit
     * @param res_no_s
     *            开始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset checkVirtualCard(String res_no_s, String res_no_e, String stock_id, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.selOccupyVitualCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.getVirtualInfo", inData);
    }

    /**
     * 虚拟卡校验
     *
     * @author chenzm
     * @param visit
     * @param res_no_s
     *            开始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     * @param x_check_tag
     * @param passwd
     *            验证码
     * @return
     * @throws Exception
     */
    /*public static IDataset checkVirtualCard(String res_no_s, String res_no_e, String stock_id, String res_type_code, String x_check_tag, String passwd) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("X_CHECK_TAG", x_check_tag);
        inData.put("PASSWD", passwd);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.getVirtualInfo", inData);//del

    }*/

    /**
     * 虚拟卡校验
     *
     * @author chenzm
     * @param visit
     * @param res_no_s
     *            开始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset checkVirtualGGCard(String res_no_s, String res_no_e, String stock_id, String gg_tag, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("GG_TAG", gg_tag);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.selOccupyVitualCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.getVirtualInfo", inData);
    }

    /**
     * @Function: chkNpMphoneInfo
     * @Description: 携转开户号码校验
     * @param sn
     * @param xGetMode
     *            2-携入中校验；3-携入后校验
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月5日 下午10:00:10
     */
    public static IDataset chkNpMphoneInfo(String sn, String xGetMode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", sn);
        inData.put("X_GETMODE", xGetMode);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.chkNpMphoneInfo",inData);//CSAppCall.call("RM.ResNpMphoneIntfSvc.chkNpMphoneInfo", inData);
    }

    /**
     * @Function: chkNpSimcardInfo
     * @Description: 该函数的功能描述
     * @param sim
     * @param xGetMode
     *            0-用SIM_CARD_NO查询；1-用IMSI查询
     * @param serialNumber
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月5日 下午10:04:06
     */
    public static IDataset chkNpSimcardInfo(String sim, String xGetMode, String serialNumber) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", sim);
        inData.put("X_GETMODE", xGetMode);
        inData.put("SERIAL_NUMBER", serialNumber);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfQuerySV.chkNpSimcardInfo",inData);//CSAppCall.call("RM.ResNpCardIntfSvc.chkNpSimcardInfo", inData);
    }

    /**
     * 隔月发票重打，调资源发票冲红接口
     *
     * @param ticketId
     *            票据编号
     * @param taxNo
     *            税务登记号
     * @param RES_KIND_CODE
     *            记录是发票还是收据
     * @return
     * @throws Exception
     */
    /*public static IDataset chongHongStaffTicket(String resKindCode, String ticketId, String taxNo) throws Exception
    {
        IData callParam = new DataMap();

        IDataset ds = new DatasetList();
        IData param = new DataMap();
        param.put("TICKET_ID", ticketId);
        param.put("TAX_NO", taxNo);
        param.put("RES_KIND_CODE", resKindCode);

        ds.add(param);
        callParam.put("RED_TICKET_INFOS", ds);
        setPublicParam(callParam);
        return CSAppCall.call("RM.ResTicketIntfSvc.ticketRed", callParam);//del
    }*/

    /**
     * 资源清KI
     *
     * @param visit
     * @param simCardNo
     *            ,staffId,dataCode
     * @return
     * @throws Exception
     */
    public static IDataset clearKI(String simCardNo, String staffId, String dataCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simCardNo); // SIM卡号
        inData.put("STAFF_ID", staffId); // 员工号
        inData.put("DATA_CODE", dataCode); // 库标识
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.updSimCardForClearKi",inData);//CSAppCall.call("RM.SimCardClearKiSvc.updSimCardForClearKi", inData);
    }

    /**
     * 销户释放号码资源 --最新
     *
     * @SERIAL_NUMBER 手机号码
     * @removeTag 销户类型 不传默认为2
     * @return
     * @throws Exception
     */
    public static IDataset destroyRealeaseMphone(String serialNumber, String removeTag) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("REMOVE_TAG", removeTag);// 销户状态
        return callRes("RCF.resource.INumberIntfOperateSV.destroyPhoneRelease",inData);
        //CSAppCall.call("RM.ResPhoneIntfSvc.destroyPhoneRelease", inData);
    }

    /**
     * 销户释放SIM卡资源
     *
     * @simCardNo sim卡号
     * @return
     * @throws Exception
     *             ;
     */
    public static IDataset destroyRealeaseSimCard(String simCardNo,String activeTag) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("SIM_CARD_NO", simCardNo);
        inData.put("ACTIVE_TAG", activeTag);
        return callRes("RCF.resource.ISimcardIntfOperateSV.releaseSim",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.realeaseSim", inData);
    }

    /**
     * 物联网号码销户
     *
     * @param visit
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset destroyRealeaseTMphone(String serial_number, String removeTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number); // 号码
        inData.put("REMOVE_TAG", removeTag); // 销户类型
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.destroyPhoneRelease",inData);//CSAppCall.callTerminal("TM.ResPhoneIntfSvc.updateMphonecodeStateDestory", inData, false).getData();
    }

    /**
     * 物联网SimCard销户
     *
     * @param visit
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset destroyRealeaseTSimCard(String simCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simCardNo); // simCard
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.releaseSim",inData);//CSAppCall.callTerminal("TM.ResSimCardIntfSvc.realeaseSim", inData, false).getData();
    }

    /**
     * 实体卡激活同步
     */
    public static IDataset entityCardActiveSync(String startCardNo, String endCardNo, String stock_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_E", endCardNo);
        inData.put("RES_NO_S", startCardNo);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", "3");
        setPublicParam(inData);

        return callRes("RCF.resource.IPayCardIntfOperateSV.changeEntityActiveState",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.changeEntityActiveState", inData);
    }

    /**
     * 查询白卡卡信息
     *
     * @param visit
     * @param res_no
     *            //资源号码
     * @param sn
     *            //　　手机号码
     * @param qry_tag
     *            //IDLE:查空闲 USE:查在用 不传则先查空闲，如没查到再查在用
     * @return
     * @throws Exception
     */
    public static IDataset getEmptycardInfo(String res_no, String sn, String qry_tag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", res_no);
        inData.put("QRY_TAG", qry_tag);
        setPublicParam(inData);
        return callRes("RCF.resource.ISncardIntfQuerySV.getEmptycardInfo",inData);
        //CSAppCall.call("RM.ResSimCardIntfSvc.getEmptycardInfo", inData);
    }

    /**
     * 查询号码信息
     *
     * @author chengxf2
     * @param visit
     * @param SERIAL_NUMBER
     *            手机号码
     * @return
     * @throws Exception
     */
    /*public static IDataset getIOTMphonecodeInfo(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number);
        setPublicParam(inData);
        return CSAppCall.call("TM.ResPhoneIntfSvc.getMphonecodeInfo", inData);//del
    }*/

    /**
     * 根据铁通号码获取移动号码资源
     *
     * @author chenzm
     * @param visit
     * @param serial_number
     * @throws Exception
     */
    public static IDataset getMobilePhoneByTieTongNumber(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPhoneIntfSvc.getTietMphoneCodeInfo", inData);//del该方法有被调用，但上级就没被调用了
    }

    /**
     * 根据铁通号码和移动号码获取移动号码资源 X_TAG =0 根据铁通号码查询 X_TAG =1 根据移动号码查询 SERIAL_NUMBER 必输
     *
     * @author chenzm
     * @param visit
     * @param serial_number
     * @throws Exception
     */
    /*public static IDataset getMobilePhoneByTieTongNumber(String serialNumber, String XTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("X_TAG", XTag);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPhoneIntfSvc.getTietMphoneCodeInfo", inData);//del
    }*///未被使用

    /**
     * @Function: getMphonecodeInfoByNumber()
     * @Description: 无线固话批量预开,根据手机号码查询SIM卡信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-13 下午4:29:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-13 yxd v1.0.0 修改原因
     */
    /*public static IDataset getMphonecodeIMSIInfoByNumber(String serialNumber) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber);
        setPublicParam(inData);
        return CSAppCall.callTerminal("TM.ResPhoneIntfSvc.getMphonecodeInfoByNumber", inData, false).getData();//del
    }*/

    /**
     * 查询号码信息
     *
     * @author sunxin
     * @param visit
     * @param SERIAL_NUMBER
     *            手机号码
     * @return
     * @throws Exception
     */
    public static IDataset getMphonecodeInfo(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.getPhoneInfoByNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.getMphonecodeInfo", inData);
    }
    public static IDataset getMphonecodeInfo(String serial_number,String qryTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("QRY_TAG", qryTag);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.getPhoneInfoByNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.getMphonecodeInfo", inData);
    }

    /**
     * 根据serialNumber获取sim卡信息
     *
     * @return
     * @throws Exceptionthrows
     */
    /*public static IDataset getMphoneSimInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_NO", serialNumber);
        setPublicParam(param);
        return CSAppCall.call("RM.ResPhoneIntfSvc.getMphoneSimInfo", param);//del
    }*/

    /**
     * 根据白卡卡号或临时手机号码获取资源信息
     *
     * @param sn
     * @param emptyCardId
     * @param xTag
     *            根据号码查询 X_TAG 0 SERIAL_NUMBER 根据白卡查询 X_TAG 1 EMPTY_CARD_ID
     * @return
     * @throws Exception
     */
    /*public static IDataset getPreSimcardBySnOrCardNo(String sn, String emptyCardId, String xTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", sn);
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("X_TAG", xTag);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResSimCardIntfSvc.getPreSNByResNo", inData);//del
    }*/

    /**
     * 获取资源的税率
     *
     * @return
     * @throws Exceptionthrows
     */
    /*public static IDataset getResTax(String resKindCode) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);
        callParam.put("RES_KIND_CODE", resKindCode);
        return CSAppCall.call("RM.ResTicketIntfSvc.getTax", callParam);

    }*/

    /**
     * 临时选占查询
     *
     * @author sunxin
     * @param visit
     * @param RES_TYPE_CODE
     *            // 传0
     * @param PARA_VALUE2
     *            // 占用类型 网上选号：1 ，自助选号：3
     * @param PARA_VALUE1
     *            //资源状态类型 2-选占;3-占用
     * @param PARA_VALUE3
     *            //身份证号码
     * @param RES_NO
     *            //资源编码，可为空
     * @return
     * @throws Exception
     */
    public static IDataset getSelTempOccupyNum(String res_type_code, String netchooseType, String psptId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TYPE_CODE", res_type_code); // 0
        inData.put("PARA_VALUE2", netchooseType); // 占用类型 网上选号：1 ，自助选号：3
        inData.put("PARA_VALUE1", "2"); // 2-选占;3-占用
        inData.put("PARA_VALUE3", psptId); // 身份证号码
        inData.put("RES_NO", ""); // 检测号码

        setPublicParam(inData);
        return callRes("RCF.resource.IResPublicIntfQuerySV.getSelTempOccupyNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.getSelTempOccupyNum", inData);
    }

    /**
     * @Function: getSerialNumberByCardNo()
     * @Description: 无线固话批量预开,根据sim_card_no查询卡号信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-13 下午4:35:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-13 yxd v1.0.0 修改原因
     */
    /*public static IDataset getSerialNumberByCardNo(String simcardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simcardNo);
        setPublicParam(inData);
        return CSAppCall.callTerminal("TM.ResSimCardIntfSvc.getSimcardInfoByCardNo", inData, false).getData();//del
    }*/

    /**
     * 查询sim卡信息【X_GET_MODE来判断查询的条件，X_GET_MODE为0用SIM_CARD_NO查询sim卡重要信息，1用IMSI查询sim卡重要信息】
     *
     * @author sunxin
     * @param x_get_mode
     * @param sim_card_no
     *            【X_GET_MODE为0时 必须】
     * @param imsi
     *            【X_GET_MODE为1时 必须】
     * @param qry_tag
     *            【0:查空闲;1:查在用;不传则先查空闲，如没查到再查在用】
     * @param visit
     * @return dataset.getData(0).get("X_RESULTCODE")该值为0时为查询有数据，否则无数据，X_RESULTCODE一定会有该key
     * @throws Exception
     */

    public static IDataset getSimCardInfo(String x_get_mode, String sim_card_no, String imsi, String qry_tag) throws Exception
    {
        IData callParam = new DataMap();
        callParam.put("X_GET_MODE", x_get_mode);

        if ("0".equals(x_get_mode))
        {
            callParam.put("SIM_CARD_NO", sim_card_no);
        }
        if ("1".equals(x_get_mode))
        {
            callParam.put("IMSI", imsi);
        }
        if (qry_tag != null && !"".equals(qry_tag))
        {
            callParam.put("QRY_TAG", qry_tag);
        }
        setPublicParam(callParam);
        IDataset dataset = callRes("RCF.resource.ISimcardIntfQuerySV.getSimcardInfo",callParam);//CSAppCall.call("RM.ResSimCardIntfSvc.getSimcardInfo", callParam);
        return dataset;
    }

    public static IDataset getSimCardInfo(String x_get_mode, String sim_card_no, String imsi, String qry_tag, String netTypeCode) throws Exception
    {
        IData callParam = new DataMap();
        callParam.put("X_GET_MODE", x_get_mode);

        if ("0".equals(x_get_mode))
        {
            callParam.put("SIM_CARD_NO", sim_card_no);
        }
        if ("1".equals(x_get_mode))
        {
            callParam.put("IMSI", imsi);
        }
        if (qry_tag != null && !"".equals(qry_tag))
        {
            callParam.put("QRY_TAG", qry_tag);
        }
        setPublicParam(callParam);
        /*if ("07".equals(netTypeCode))// 全走RCF
        {
            callParam.put("QRY_TAG", "");
            IDataset simcardInfos = CSAppCall.call("TM.ResSimCardIntfSvc.getSimcardInfo", callParam);
            for (int i = 0; i < simcardInfos.size(); i++)
            {
                IData simcard = simcardInfos.getData(i);
                String simTypeCode = simcard.getString("SIM_TYPE_CODE");
                String cardKindCode = simcard.getString("CARD_KIND_CODE");
                String capTypeCOde = simcard.getString("CAPACITY_TYPE_CODE");
                simcard.put("RES_TYPE_CODE", "1" + simTypeCode);
                simcard.put("RES_KIND_CODE", "1" + simTypeCode + cardKindCode + capTypeCOde);
            }
            return simcardInfos;
        }
*/
        IDataset dataset = callRes("RCF.resource.ISimcardIntfQuerySV.getSimcardInfo",callParam);//CSAppCall.call("RM.ResSimCardIntfSvc.getSimcardInfo", callParam);
        return dataset;
    }

    /**
     * @Function: getSimcardInfoByImsi()
     * @Description: 物联网获取Sim卡信息
     * @param:X_GET_MODE为0时,传SIM_CARD_NO | X_GET_MODE为1时 ，传IMSI
     * @param:qryTag:IDLE:查空闲 , USE:查在用 ,不传则先查空闲，如没查到再查在用
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-13 下午4:46:12 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-13 yxd v1.0.0 修改原因
     */
    /*public static IDataset getSimcardInfoByImsiOrSimcardNo(String simcard, String imsi, String xGetMode, String qryTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("X_GET_MODE", xGetMode);
        inData.put("SIM_CARD_NO", simcard);
        inData.put("IMSI", imsi);
        inData.put("QRY_TAG", qryTag);
        setPublicParam(inData);
        return CSAppCall.callTerminal("TM.ResSimCardIntfSvc.getSimcardInfo", inData, false).getData();//del
    }*/

    /**
     * 特殊补偿卡查询
     */
    public static IDataset getSpecCompCardInfo(String simCardNo, String userEparchyCode) throws Exception
    {

        IData inData = new DataMap();
        inData.put("RES_NO", simCardNo);
        inData.put("USER_EPARCHY_CODE", userEparchyCode);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfQuerySV.qrySpecCompCardInfo",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.qrySpecCompCardInfo", inData);
    }

    /**
     * 获取员工的票据
     *
     * @param staffId
     * @param ticketTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getStaffPrintTicket(String staffId, String ticketTypeCode) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);
        callParam.put("STAFF_ID", staffId);
        callParam.put("TICKET_TYPE_CODE", ticketTypeCode);
        return callRes("RCF.resource.IInvoiceIntfQuerySV.getInvoiceInfoByStaff",callParam);//CSAppCall.call("RM.ResTicketIntfSvc.getTicketInfoByStaff", callParam);
    }

    /**
     * 释放临时号码
     *
     * @param Imsi
     * @param userEparchyCode
     * @return
     * @throws Exception
     *             public static IDataset releaseTempImsi(String iccid, String userEparchyCode) throws Exception { IData
     *             inData = new DataMap(); inData.put("SIM_CARD_NO", iccid); inData.put(Route.USER_EPARCHY_CODE,
     *             userEparchyCode);// 用户归属地州 inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());//
     *             用户交易地州 return CSAppCall.call("RM.ResSimCardIntfSvc.recycleTempSimCardNo", inData); } 释放临时号码
     * @param tempSn
     * @return
     * @throws Exception
     *             public static IDataset releaseTempSn(String tempSn, String userEparchyCode) throws Exception { IData
     *             inData = new DataMap(); inData.put("SERIAL_NUMBER", tempSn); inData.put(Route.USER_EPARCHY_CODE,
     *             userEparchyCode);// 用户归属地州 inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());//
     *             用户交易地州 return CSAppCall.call("RM.ResPhoneIntfSvc.recycleTempSN", inData); }
     */

    /**
     * 获取票据号
     *
     * @param ticketTypeCode
     *            票据类型:A 表示预存\有价卡发票 B 押金\预存收据 C 营业发票 D 铁通预存发票 E 铁通收据 F 铁通营业发票
     * @param
     * @return RETURNCODE 0成功,1失败 X_RESULTINFO 返回信息 X_RESULTNUM 返回信息数量
     * @throws Exception
     */
    /*public static IDataset getTicketInfo(String staffId, String ticketTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("STAFF_ID", staffId);
        inData.put("TICKET_TYPE_CODE", ticketTypeCode);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResTicketIntfSvc.getTicketInfoByStaff", inData);//del
    }*/

    /**
     * 查询已经占用的实体卡信息
     *
     * @param res_no_s
     * @param res_no_e
     * @param stock_id
     * @param res_type_code
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-8-13
     */
    public static IDataset getUsedEntityCardInfo(String res_no_s, String res_no_e, String stock_id, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("QRY_TAG", "1");
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfQuerySV.queryPayCardInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.queryValuecardInfo", inData);
    }

    /**
     * 充值卡充值状态修改（外围接口调用）
     *
     * @param res_no_s
     * @param res_no_e
     * @param stock_id
     * @param res_type_code
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-8-13
     */
    public static IDataset modifyUsedEntityCardInfo(String res_no_s, String res_no_e, String stock_id, String res_type_code, String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("SERIAL_NUMBER", serial_number);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.changeChargeState",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.changeChargeState", inData);
    }

    /**
     * 获取远程写卡信息
     *
     * @param serialNumber
     * @param emptyCardId
     * @param occupyTimeCode
     *            0-选占当天；1-选占30分钟；2-选占24小时。不传默认为0
     * @param remoteMode
     *            1-异地USIM写卡 0-异地SIM写卡 2 本地写卡
     * @param isTurnnet
     *            1-携号用户。不填默认为否
     * @return
     * @throws Exception
     */
    public static IDataset getWriteSimCardInfo(String serialNumber, String emptyCardId, String occupyTimeCode, String remoteMode, String isTurnnet) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("OCCUPY_TIME_CODE", occupyTimeCode); // 0-只获取；1-获取+校验
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("IS_TURNNET", isTurnnet);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.selOccupyWriteSimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.selOccupyWriteSimCard", inData);
    }

    public static IDataset getSimDataInfo(String imsi) throws Exception
    {
        IData inData = new DataMap();
        inData.put("IMSI", imsi);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfQuerySV.getSimDataInfo",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.getSimDataInfo", inData);
    }
    public static IDataset ggCardReward(String resNoS, String resNoE, String stockId, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", resNoS);
        inData.put("RES_NO_E", resNoE);
        inData.put("STOCK_ID", stockId);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);

        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.ggCardReward",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.ggCardReward", inData);
    }

    /**
     * 实体卡占用
     *
     * @author chenzm
     * @param visit
     * @param device_no_s
     *            开始卡号
     * @param device_no_e
     *            结束卡号
     * @param stock_id
     * @param res_type_code
     * @param sale_fee
     * @param discount
     * @param agent_fee
     * @throws Exception
     */
    public static IDataset iEntityCardModifyState(String res_no_s, String res_no_e, String stock_id, String res_type_code, String sale_fee, String discount, String agent_fee, String fee_tag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("SALE_FEE", sale_fee);
        inData.put("DISCOUNT", discount);
        inData.put("AGENT_FEE", agent_fee);
        inData.put("FEE_TAG", fee_tag);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.occupyEntityCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.modifyEntityCardSaleInfo", inData);

    }

    /**
     * 查询有价卡信息 在查询的时候就已经选占
     *
     * @param route_eparchy_code
     *            号码归属地
     * @param device_no_s
     *            开始卡号
     * @param device_no_e
     *            结束卡号
     * @return
     * @throws Exception
     */
    public static IDataset iGetValueCardInfo(String device_no_s, String device_no_e, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();

        inData.put("RES_TYPE_CODE", "3"); // 3-有价卡

        inData.put("RES_NO_S", device_no_s);
        inData.put("RES_NO_E", device_no_e);
        inData.put("STOCK_ID", CSBizBean.getVisit().getDepartId());
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);

        setPublicParam(inData);
        IDataset dataset = callRes("RCF.resource.IPayCardIntfOperateSV.selOccupyValueCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.getResInfo", inData);

        return dataset;
    }

    /**
     * 有价卡占用
     *
     * @author chenzm
     * @param visit
     * @param device_no_s
     *            开始卡号
     * @param device_no_e
     *            结束卡号
     * @param stock_id
     * @param res_type_code
     * @param sale_fee
     * @param discount
     * @param agent_fee
     * @throws Exception
     */
    /*public static IDataset iValueCardModifyState(String res_no_s, String res_no_e, String stock_id, String res_type_code, String sale_fee, String discount, String agent_fee) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("SALE_FEE", sale_fee);
        inData.put("DISCOUNT", discount);
        inData.put("AGENT_FEE", agent_fee);
        inData.put("SALE_MONEY", sale_fee);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.modifyValueCardSaleInfo", inData);//del

    }*/

    /**
     * 号卡匹配
     *
     * @return
     * @throws Exceptionthrows
     */
    public static IDataset matchSimNumMgr(String serialNumber, String simCardNo, String imsi) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);
        callParam.put("SERIAL_NUMBER", serialNumber);
        callParam.put("SIM_CARD_NO", simCardNo);
        callParam.put("IMSI", imsi);
        return callRes("RCF.resource.INumberIntfOperateSV.matchMphoneSim",callParam);//CSAppCall.call("RM.ResPhoneIntfSvc.matchMphoneSim", callParam);
    }

    /**
     * 物联网号卡匹配
     *
     * @return
     * @throws Exceptionthrows
     */
    public static IDataset matchTSimNumMgr(String serialNumber, String simCardNo, String imsi) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);
        callParam.put("SERIAL_NUMBER", serialNumber);
        callParam.put("SIM_CARD_NO", simCardNo);
        callParam.put("IMSI", imsi);
        return callRes("RCF.resource.INumberIntfOperateSV.matchMphoneSim",callParam);//CSAppCall.callTerminal("TM.ResPhoneIntfSvc.matchSimNumMgr", callParam, false).getData();
    }

    /**
     * @Function: modifyNpMphoneInfo
     * @Description: 该函数的功能描述
     * @param sim
     * @param xGetMode携转状态
     * @param serialNumbe号码
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月7日 下午5:16:47
     */
    public static IDataset modifyNpMphoneInfo(String sim, String xGetMode, String serialNumbe) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", serialNumbe);
        inData.put("X_GETMODE", xGetMode);
        inData.put("SIM_CARD_NO", sim);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.modifyNpMphoneInfo",inData);//CSAppCall.call("RM.ResNpMphoneIntfSvc.modifyNpMphoneInfo", inData);
    }


    /**
     *
     * @Function: modifyNpMphoneInfo
     * @Description: 携转开户完工号码调用资源接口
     *
     * @param sim
     * @param xGetMode
     * @param serialNumbe
     * @param tradeId
     * @param tradeTypeCode
     * @param productId
     * @param brandCode
     * @return
     * @throws Exception
     *
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年9月17日 下午9:07:33
     *
     */
    public static IDataset modifyNpMphoneInfo(String sim, String xGetMode, String serialNumbe,String tradeId,String tradeTypeCode,String productId,String brandCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", serialNumbe);
        inData.put("X_GETMODE", xGetMode);
        inData.put("SIM_CARD_NO", sim);
        inData.put("TRADE_ID", tradeId);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("PRODUCT_ID", productId);
        inData.put("BRAND_CODE", brandCode);


        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.modifyNpMphoneInfo",inData);//CSAppCall.call("RM.ResNpMphoneIntfSvc.modifyNpMphoneInfo", inData);
    }

    /**
     * @Function: modifyNpSimInfo
     * @Description: 该函数的功能描述
     * @param simSIM卡号
     * @param xGetMode携转状态
     * @param serialNumber
     *            号码
     * @param tradeId营业侧TRADE_ID
     * @param userId营业侧用户ID
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月7日 上午11:46:46
     */
    public static IDataset modifyNpSimInfo(String sim, String xGetMode, String serialNumber, String tradeId, String userId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", sim);
        inData.put("X_GETMODE", xGetMode);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("TRADE_ID", tradeId);
        inData.put("USER_ID", userId);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.modifyNpSimInfo",inData);//CSAppCall.call("RM.ResNpCardIntfSvc.modifyNpSimInfo", inData);
    }

    /**
     *
     * @Function: modifyNpSimInfo
     * @Description: 携转开户登记调sim卡调用接口
     *
     * @param sim
     * @param xGetMode
     * @param serialNumber
     * @param tradeId
     * @param userId
     * @param tradeTypeCode
     * @return
     * @throws Exception
     *
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年9月17日 下午9:05:00
     *
     */
    public static IDataset modifyNpSimInfo(String sim, String xGetMode, String serialNumber, String tradeId, String userId,String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", sim);
        inData.put("X_GETMODE", xGetMode);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("TRADE_ID", tradeId);
        inData.put("USER_ID", userId);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.modifyNpSimInfo",inData);//CSAppCall.call("RM.ResNpCardIntfSvc.modifyNpSimInfo", inData);
    }

    /**
     * 改号，手机号码改号
     *
     * @param sim_card_no
     * @param imsi
     * @param old_serial_number
     * @param new_serial_number
     * @param product_id
     * @param trade_id
     * @param openTime
     * @param occupyTypeCode
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset modifyPhone(String sim_card_no, String imsi, String old_serial_number, String new_serial_number, String product_id, String trade_id, String openTime, String occupyTypeCode, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OLD_SERIAL_NUMBER", old_serial_number);
        inData.put("NEW_SERIAL_NUMBER", new_serial_number);
        inData.put("IMSI", imsi);
        inData.put("SIM_CARD_NO", sim_card_no);
        inData.put("PRODUCT_ID", product_id);
        inData.put("TRADE_ID", trade_id);
        inData.put("OPEN_TIME", openTime);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.modifyPhone",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.modifyPhone", inData);
    }

    /**
     * 改号，SIM卡改号
     *
     * @param occupyTypeCode
     * @param serialNumber
     * @param oldSimCardNo
     * @param newSimCardNo
     * @param checkTag
     * @param tradeId
     * @param tradeTypeCode
     * @param resTypeCode
     * @param userId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset modifySimCard(String occupyTypeCode, String serialNumber, String oldSimCardNo, String newSimCardNo, String checkTag, String tradeId, String tradeTypeCode, String resTypeCode, String userId, String productId)
            throws Exception
    {
        IData inData = new DataMap();
        inData.put("OLD_SIM_CARD_NO", oldSimCardNo);
        inData.put("NEW_SIM_CARD_NO", newSimCardNo);
        inData.put("SERIAL_NUMBER", serialNumber);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("X_CHECK_TAG", checkTag);
        inData.put("TRADE_ID", tradeId);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("USER_ID", userId);
        inData.put("PRODUCT_ID", productId);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.modifySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.modifySimCard", inData);
    }

    /**
     * 刮刮卡资源占用
     */
    public static IDataset modifyVitualCardSaleInfo(String res_no_s, String res_no_e, String stockId, String userEparchyCode, String tradeEparchyCode, String feeTag, String tradeTypeCode) throws Exception
    {

        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stockId);
        inData.put("USER_EPARCHY_CODE", userEparchyCode);
        inData.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        inData.put("FEE_TAG", feeTag);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);

        return callRes("RCF.resource.IPayCardIntfOperateSV.occupyVitualCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.modifyVitualCardSaleInfo", inData);
    }

    /**
     * 营销活动礼品占用
     *
     * @param resId
     * @return
     * @throws Exception
     */
    public static IData occupyGiftGoods4Sale(String resId, String saleTradeId, int number) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_KIND_CODE", resId);
        param.put("SALE_TRADE_ID", saleTradeId);
        param.put("X_GETMODE", "0");
        param.put("COUNT", number);
        setPublicParam(param);

        IDataset dataset = callRes("RCF.resource.IGoodsIntfOperateSV.occupyByCountForSale",param);//CSAppCall.call("RM.ResGoodsIntfSvc.occupyGoodsForSale", param);

        if (IDataUtil.isNotEmpty(dataset))
            return dataset.getData(0);

        return null;
    }

    /**
     * 实物状态更新
     */
    public static void occupyGoods(String trade_eparchy_code, String res_id, String oper_num, String tradeId) throws Exception
    {

        occupyGoods(trade_eparchy_code, res_id, oper_num, null, tradeId);

    }

    public static void occupyGoods(String trade_eparchy_code, String res_id, String oper_num, String stockId, String tradeId) throws Exception
    {

        IData inData = new DataMap();
        inData.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
        inData.put("X_GETMODE", "0");// 0 根据资源数量进行预占;1 根据资源编码进行预占
        inData.put("RES_NO", res_id);// 资源类型编码
        inData.put("COUNT", oper_num);// 数量
        inData.put("SALE_TRADE_ID", tradeId);
        if (StringUtils.isNotBlank(stockId))
        {
            inData.put("STOCK_ID", stockId);
        }
        setPublicParam(inData);
        callRes("RCF.resource.IGoodsIntfOperateSV.occupyGoodsForPerson",inData);//CSAppCall.call("RM.ResGoodsIntfSvc.occupyGoodsForPerson", inData);

    }

    /**
     * sim卡返销
     *
     * @author chenzm
     * @param visit
     * @param serial_number
     * @return
     * @throws Exception
     *             public static IDataset undoResPossessForSim(String sim_card_no, String net) throws Exception { IData
     *             inData = new DataMap(); inData.put("SIM_CARD_NO", sim_card_no);// 返销sim卡 setPublicParam(inData); if
     *             ("07".equals(net)) return CSAppCall.call("TM.ResSimCardIntfSvc.openUndoSim", inData); return
     *             CSAppCall.call("RM.ResSimCardIntfSvc.openUndoSim", inData); }
     */

    /**
     * 发票占用确认接口（营改增）
     *
     * @param taxNo
     * @param ticketId
     * @param ticketTypeCode
     * @param tradeId
     * @param serialNumber
     * @param fee
     * @param sfkj
     * @return
     * @throws Exception
     */
    public static IDataset occupyPrintTicket(String taxNo, String ticketId, String tradeTypeCode, String ticketTypeCode, String tradeId, String serialNumber, String fee,
            String sfkj,String updTaxNo,String updTicketId) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);

        /**
         * REQ201501270018关于票据作废界面走域权控制的优化
         * 2015-03-27 CHENXY3
         * */
        String tradeTypeName="";
        String checkRightClass="";
        if(tradeTypeCode!=null&&tradeTypeCode.indexOf("|")>-1){
            tradeTypeName=tradeTypeCode.substring(tradeTypeCode.indexOf("|")+1);
            tradeTypeCode=tradeTypeCode.substring(0,tradeTypeCode.indexOf("|"));
        }
        callParam.put("TAX_NO", taxNo);
        callParam.put("TICKET_ID", ticketId);
        callParam.put("TICKET_TYPE_CODE", ticketTypeCode);
        callParam.put("TRADE_TYPE_CODE", tradeTypeCode);
        callParam.put("TRADE_TYPE_NAME", tradeTypeName);
        callParam.put("TRADE_ID", tradeId);
        callParam.put("SERIAL_NUMBER", serialNumber);
        callParam.put("FEE", fee);
        callParam.put("SFKJ", sfkj);
        callParam.put("UPD_TAX_NO", updTaxNo);
        callParam.put("UPD_TICKET_ID", updTicketId);
        return callRes("RCF.resource.IInvoiceIntfOperateSV.invoiceConfirm",callParam);//CSAppCall.call("RM.ResTicketIntfSvc.receiptConfirm", callParam);
    }

    public static void occupyReleaseGoods(String resId, String count, String tradeId) throws Exception
    {

        IData inData = new DataMap();
        inData.put("RES_TYPE_CODE", "D");
        inData.put("X_GETMODE", "0");// 0 根据资源数量进行预占;1 根据资源编码进行预占
        inData.put("RES_NO", resId);// 资源类型编码
        inData.put("COUNT", count);// 数量
        inData.put("SALE_TRADE_ID", tradeId);
        setPublicParam(inData);
        callRes("RCF.resource.IGoodsIntfOperateSV.occupyReleaseGoods",inData);//CSAppCall.call("RM.ResGoodsIntfSvc.occupyReleaseGoods", inData);
    }

    /**
     * 一卡双号SIM卡预占
     *
     * @param simcardnoA
     * @param simcardnoB
     * @param serialNum
     * @return
     * @throws Exception
     */
    public static IDataset ocncPreOccupy(String simcardnoA, String simcardnoB, String serialNum) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO_A", simcardnoA);
        inData.put("SIM_CARD_NO_B", simcardnoB);
        inData.put("SERIAL_NUMBER", serialNum);
        inData.put("OCCUPY_TYPE_CODE", "0");
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.oCardTPhonePreOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.oCardTPhonePreOccupySimCard", inData);
    }

    /**
     * 一卡双号SIM卡选占
     *
     * @param simcardnoA
     * @param simcardnoB
     * @param serialNumA
     * @param serialNumB
     * @return
     * @throws Exception
     */
    public static IDataset ocncSelOccupy(String simcardnoA, String simcardnoB, String serialNumA, String serialNumB) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO_A", simcardnoA);
        inData.put("SIM_CARD_NO_B", simcardnoB);
        inData.put("SERIAL_NUMBER_A", serialNumA);
        inData.put("SERIAL_NUMBER_B", serialNumB);
        inData.put("OCCUPY_TYPE_CODE", "0");
        inData.put("IS_NOTRELEASE", "1");
        inData.put("X_CHOICE_TAG", "1");
        inData.put("IS_TURNNET", "0");
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.oCardTPhoneSelOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.oCardTPhoneSelOccupySimCard", inData);
    }

    /**
     * 一卡双号SIM卡占用
     *
     * @param simcardnoA
     * @param simcardnoB
     * @param serialNum
     * @return
     * @throws Exception
     */
    public static IDataset ocncUseOccupy(String simcardnoA, String simcardnoB, String serialNumA,String serialNumB,String tradeId,String userId) throws Exception
    {
        IData inData = new DataMap();
        if("".equals(tradeId) || tradeId == null){
            tradeId =serialNumA;
        }
        inData.put("SIM_CARD_NO_A", simcardnoA);
        inData.put("SIM_CARD_NO_B", simcardnoB);
        inData.put("SERIAL_NUMBER_A", serialNumA);
        inData.put("SERIAL_NUMBER_B", serialNumB);
        inData.put("OCCUPY_TYPE_CODE", "0");
        inData.put("X_CHECK_TAG", "0");
        inData.put("TRADE_ID", tradeId);
        inData.put("USER_ID", userId);
        inData.put("PRODUCT_ID", "5500");

        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.oCardTPhoneUseOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.oCardTPhoneUseOccupySimCard", inData);
    }

    /**
     * 白卡预占
     *
     * @param emptyCardId
     *            白卡序列号
     * @param occupyTypeCode
     *            0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param xGetMode
     *            填0
     * @return
     * @throws Exception
     */
    /*public static IDataset preOccupyEmptyCard(String emptyCardId, String occupyTypeCode, String xGetMode, String netTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("X_GET_MODE", xGetMode);
        setPublicParam(inData);
        if ("07".equals(netTypeCode))
        {
            return CSAppCall.call("TM.ResSimCardIntfSvc.preOccupyEmptyCard", inData);
        }
        return CSAppCall.call("RM.ResSimCardIntfSvc.preOccupyEmptyCard", inData);//del
    }*/

    /**
     * 白卡占用
     *
     * @param emptyCardId
     *            白卡序列号
     * @param simCardNo
     *      写卡个性化信息iccid
     * @param sn
     *            号码
     * @return
     * @throws Exception
     */
    public static IDataset occupyEmptyCard(String emptyCardId, String simCardNo, String sn,String routeTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", emptyCardId);
        inData.put("PARA_VALUE2", "0");
        inData.put("PARA_VALUE3", simCardNo);
        inData.put("PARA_VALUE4", sn);

        if ("1".equals(routeTag)) {
            inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());// 用户归属地州
            inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
            inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
            inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
            inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
            inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        } else {
            setPublicParam(inData);
        }
        return callRes("RCF.resource.ISncardIntfOperateSV.updEmptyCardState",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.updEmptyCardState", inData);
    }
    /**
     * 实物预占
     */
    /*public static void preOccupyGoods(String res_id, String oper_num, String tradeId) throws Exception
    {
        preOccupyGoods(res_id, oper_num, null, tradeId);

    }*/

    /**
     * 实物预占
     */
    /*public static void preOccupyGoods(String res_id, String oper_num, String stockId, String tradeId) throws Exception
    {
        // 实物预占准备
        IData inData = new DataMap();

        inData.put("RES_TYPE_CODE", "C");
        inData.put("X_GETMODE", "0");// 0 根据资源数量进行预占;1 根据资源编码进行预占
        inData.put("RES_KIND_CODE", res_id);// 资源类型编码
        inData.put("OPER_NUM", oper_num);// 数量
        inData.put("SALE_TRADE_ID", tradeId);
        if (StringUtils.isNotBlank(stockId))
        {
            inData.put("STOCK_ID", stockId);
        }
        setPublicParam(inData);
        CSAppCall.call("RM.ResGoodsIntfSvc.preOccupyGoods", inData);//del
    }*/

    /**
     * @param startValue
     * @param endValue
     * @return
     * @throws Exception
     */
    public static IDataset qryAllSimInfoByRangeNo(String startValue, String endValue) throws Exception
    {
        IData inData = new DataMap();
        inData.put("START_VALUE", startValue);
        inData.put("END_VALUE", endValue);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfQuerySV.qryAllSimInfoByRangeNo",inData);//CSAppCall.call("RM.ResQueryIntfSvc.qryAllSimInfoByRangeNo", inData);
    }

    /**
     * 空闲号码查询
     *
     * @author xiaobin
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryIdlePhone(String serialNumber) throws Exception
    {

        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.qryIdleByMatchSn",inData);//CSAppCall.call("RM.ResQueryIntfSvc.qryIdleByMatchSn", inData);
    }

    /**
     * 新匹配号码查询
     *
     * @author xiaobin
     * @param SERIAL_NUMBER_S
     *            ,SERIAL_NUMBER_E
     * @return
     * @throws Exception
     */
    public static IDataset qryMatchingPhone(String serialNumberS, String serialNumberE) throws Exception
    {

        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER_S", serialNumberS);
        inData.put("SERIAL_NUMBER_E", serialNumberE);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.getIdleByMatch",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.getIdleByMatch", inData);
    }

    /**
     * 两不一快,获取临时号码数据
     */
    public static IData qryPhoneEmptyBySn(String serialNumber, String resState) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        if (StringUtils.isNotEmpty(resState))
        {
            param.put("RES_STATE", resState);
        }
        setPublicParam(param);

        IDataset dataset = callRes("RCF.resource.INumberIntfQuerySV.qryPhoneEmptyBySn",param);//CSAppCall.call("RM.ResQueryIntfSvc.qryPhoneEmptyBySn", param);
        if (IDataUtil.isNotEmpty(dataset))
            return dataset.getData(0);

        return null;

    }

    /**
     * 查询实体卡信息是否存在 平台业务使用
     *
     * @param resNoStart
     * @param resNoEnd
     * @return
     * @throws Exception
     */
    public static IDataset queryEntityCardInfo(String resNoStart, String resNoEnd) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", resNoStart);
        inData.put("RES_NO_E", resNoEnd);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfQuerySV.queryPayCardInfo",inData);//CSAppCall.call("RM.ValueCardAssignSvc.queryValueCardInfo", inData);
    }

    /**
     * 营销活动礼品数量查询
     *
     * @param resId
     * @return
     * @throws Exception
     */
    public static IData queryGiftGoods4Sale(String resId) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_KIND_CODE", resId);
        setPublicParam(param);

        IDataset dataset = callRes("RCF.resource.IGoodsIntfQuerySV.queryGoodsInfos",param);//CSAppCall.call("RM.ResGoodsIntfSvc.queryGoods", param);

        if (IDataUtil.isNotEmpty(dataset))
            return dataset.getData(0);

        return null;
    }

    /**
     * 根据资源种类编码查询物品库存信息
     *
     * @param resId
     * @return
     * @throws Exception
     */
    public static IData queryGoods(String resId) throws Exception
    {
        return queryGoods(resId, null);
    }

    /**
     * 根据资源种类编码查询物品库存信息
     *
     * @param resId
     * @return
     * @throws Exception
     */
    public static IData queryGoods(String resId, String stockId) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TYPE_CODE", "D");
        param.put("RES_KIND_CODE", resId);
        if (StringUtils.isNotBlank(stockId))
        {
            param.put("STOCK_ID", stockId);
        }
        setPublicParam(param);
        IDataset dataset = callRes("RCF.resource.IGoodsIntfQuerySV.queryGoodsInfos",param);//CSAppCall.call("RM.ResGoodsIntfSvc.queryGoods", param);
        IData outIData = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            outIData = dataset.getData(0);
        }
        return outIData;
    }

    /**
     * 省内异地开户选号
     *
     * @author chenzm
     * @param visit
     * @param serail_number
     * @param x_tag
     * @param para_value7
     * @throws Exception
     */
    public static IDataset queryIDlePhone(String serail_number, String x_tag, String areaCode, String para_value7) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serail_number);
        inData.put("X_TAG", x_tag);
        inData.put("PARA_VALUE7", para_value7);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPhoneIntfSvc.getMphoneDepRoutePoolNumInfo", inData);//del被调用的方法没被调用过
    }

    /**
     * @param 查询补录卡信息
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @return
     * @throws Exception
     */
    /*public static IDataset queryReValueCardReturnInfoIntf(String res_no_s, String res_no_e, String stock_id, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("BOX_CODE", "1");
        inData.put("RES_TYPE_CODE", res_type_code);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.getReturnResInfo", inData);//del
    }*/

    /**
     * @param 获取有价卡信息
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @return
     * @throws Exception
     */
    public static IDataset queryValueCardInfoIntf(String res_no_s, String res_no_e, String stock_id, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfQuerySV.queryPayCardInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.queryValuecardInfo", inData);
    }

    /**
     * @param 获取已经销售掉的有价卡信息
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @return
     * @throws Exception
     */
    public static IDataset queryValueCardReturnInfoIntf(String res_no_s, String res_no_e, String stock_id, String res_type_code, String isFile, IDataset cardList, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("IS_FILE", isFile);
        inData.put("CARD_LIST", cardList);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfQuerySV.getReturnPayCardInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.getReturnResInfo", inData);
    }

    /**
     * @param 获取已经销售掉的有价卡vc平台信息
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @return
     * @throws Exception
     */
    public static IDataset queryValueCardReturnVcInfo(String res_no_s, String res_no_e, String stock_id, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfQuerySV.queryVCInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.queryVcInfo", inData);
    }

    /**
     * 营销活动礼品释放
     *
     * @param resId
     * @return
     * @throws Exception
     */
    public static IData releaseGiftGoods4Sale(String resId, String saleTradeId, int number) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_KIND_CODE", resId);
        param.put("X_GETMODE", "0");
        param.put("SALE_TRADE_ID", saleTradeId);
        param.put("COUNT", number);
        setPublicParam(param);

        IDataset dataset = callRes("RCF.resource.IGoodsIntfOperateSV.occupyReleaseGoods",param);//CSAppCall.call("RM.ResGoodsIntfSvc.occupyReleaseGoods", param);

        if (IDataUtil.isNotEmpty(dataset))
            return dataset.getData(0);

        return null;
    }

    /**
     * 资源释放接口
     *
     * @author sunxin
     * @param visit
     * @param X_GET_MODE
     *            //0-根据指定的RES_NO释放(号、卡)；1-根据指定的员工号释放；2-根据指定的RES_NO+STAFF_ID释放。
     * @param RES_NO
     *            //资源编码 X_GET_MODE=0、2时必传
     * @param STAFF_ID
     *            //员工工号 X_GET_MODE=1、2时必传
     * @param RES_TYPE_CODE
     *            //资源类型 0-号码；1-SIM卡
     * @return
     * @throws Exception
     */
    public static IDataset releaseRes(String x_get_mode, String res_no, String res_type_code, String staff_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("X_GET_MODE", x_get_mode);
        inData.put("RES_NO", res_no);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("STAFF_ID", staff_id);
        setPublicParam(inData);
        return callRes("RCF.resource.IResPublicIntfOperateSV.releaseRes",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.releaseRes", inData);
    }

    /**
     * 资源释放接口
     *
     * @author sunxin
     * @param visit
     * @param X_GET_MODE
     *            //0-根据指定的RES_NO释放(号、卡)；1-根据指定的员工号释放；2-根据指定的RES_NO+STAFF_ID释放。
     * @param RES_NO
     *            //资源编码 X_GET_MODE=0、2时必传
     * @param STAFF_ID
     *            //员工工号 X_GET_MODE=1、2时必传
     * @param RES_TYPE_CODE
     *            //资源类型 0-号码；1-SIM卡
     * @param OCCUPY_TYPE_CODE
     *            //资源类型 0-普通开户；2-代理商开户
     * @return
     * @throws Exception
     */
    public static IDataset releaseRes(String x_get_mode, String res_no, String res_type_code, String staff_id, String occupyTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("X_GET_MODE", x_get_mode);
        inData.put("RES_NO", res_no);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("STAFF_ID", staff_id);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IResPublicIntfOperateSV.releaseRes",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.releaseRes", inData);
    }

    /**
     * 资源释放接口
     *
     * @author wukw3
     * @param visit
     * @param X_GET_MODE
     *            //0-根据指定的RES_NO释放(号、卡)；1-根据指定的员工号释放；2-根据指定的RES_NO+STAFF_ID释放。
     * @param RES_NO
     *            //资源编码 X_GET_MODE=0、2时必传
     * @param STAFF_ID
     *            //员工工号 X_GET_MODE=1、2时必传
     * @param RES_TYPE_CODE
     *            //资源类型 0-号码；1-SIM卡
     * @param OCCUPY_TYPE_CODE
     *            //资源类型 0-普通开户；2-代理商开户
     * @return
     * @throws Exception
     */
    public static IDataset releaseResWL(String x_get_mode, String res_no, String res_type_code, String staff_id, String occupyTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("X_GET_MODE", x_get_mode);
        inData.put("RES_NO", res_no);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("STAFF_ID", staff_id);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IResPublicIntfOperateSV.releaseResNo",inData);//CSAppCall.call("TM.ResOtherIntfSvc.releaseResNo", inData);
    }

    /**
     * 原补换卡类业务，老卡号未做释放动作，会导致无法回收使用，j2ee中修复
     *
     * @param simCardInfos
     * @return
     * @throws Exception
     */
    /*public static IDataset releaseSimBatch(IDataset simCardInfos) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("SIM_INFOS", simCardInfos);

        return CSAppCall.call("RM.ResSimCardIntfSvc.realeaseSimBatch", inData);//del
    }*/

    /**
     * 异地写卡白卡占用 湖南用户到海南补换卡写卡后处理
     *
     * @param params
     * @throws Exception
     */
    public static IDataset remoteWriteEmptyCard(String result, String emptyCardId) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("X_CHOICE_TAG", result);
        inData.put("EMPTY_CARD_ID", emptyCardId);
        IDataset dataOutput = callRes("RCF.resource.ISncardIntfOperateSV.remoteWriteEmptyCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteEmptyCard", inData);
        return dataOutput;
    }

    public static IDataset remoteWriteEmptyCard(String result, String emptyCardId,String tradeId,String getMode) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("X_CHOICE_TAG", result);
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("TRADE_ID", tradeId);
        inData.put("X_GET_MODE",getMode);
        IDataset dataOutput = callRes("RCF.resource.ISncardIntfOperateSV.remoteWriteEmptyCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteEmptyCard", inData);
        return dataOutput;
    }

    /**
     * cmnet预占
     *
     * @param res_type_code
     * @param res_no
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForCmnet(String res_type_code, String res_no) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("RES_NO", res_no);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResCheckVarNumIntSvc.updateCmnetModifyState", inData);//无代码
    }

    /**
     * 物联网号码预占
     *
     * @author sunxin
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            // 检测号码
     * @param res_type_code
     *            //资源类型 0=号码 【无用参数】
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForIOTMphone(String occupy_type_code, String x_get_mode, String serial_number, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
        inData.put("X_GET_MODE", x_get_mode); // 获取方式
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.preOccupyPhoneNum",inData);//CSAppCall.callTerminal("TM.ResPhoneIntfSvc.preOccupyPhoneNum", inData, false).getData();
    }

    /**
     * 物联网sim卡预占 sunxin
     *
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param res_type_code
     *            //资源类型 【无用参数】
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForIOTSim(String occupy_type_code, String x_get_mode, String sim_card_no, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("X_GET_MODE", x_get_mode);
        inData.put("SIM_CARD_NO", sim_card_no);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.preOccupySimCard",inData);//CSAppCall.callTerminal("TM.ResSimCardIntfSvc.preOccupySimCard", inData, false).getData();
    }

    /**
     * 号码预占
     *
     * @author sunxin
     * @param visit
     * @param serial_number
     *            // 检测号码
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForMphone(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.preOccupyPhoneNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.preOccupyPhoneNum", inData);
    }

    /**
     * sim卡预占 最新
     *
     * @author sunxin
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForSim(String occupy_type_code, String sim_card_no, String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("SIM_CARD_NO", sim_card_no);
        inData.put("SERIAL_NUMBER", serial_number);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.preOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.preOccupySimCard", inData);
    }

    /**
     * 融合物联网及TD无线座机
     *
     * @param occupy_type_code
     * @param sim_card_no
     * @param serial_number
     * @param netTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForSim(String occupy_type_code, String sim_card_no, String serial_number, String netTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("SIM_CARD_NO", sim_card_no);
        inData.put("SERIAL_NUMBER", serial_number);
        setPublicParam(inData);
        /*if ("07".equals(netTypeCode))
        {
            inData.put("X_GET_MODE", "0");
            return CSAppCall.call("TM.ResSimCardIntfSvc.preOccupySimCard", inData);
        }*/
        return callRes("RCF.resource.ISimcardIntfOperateSV.preOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.preOccupySimCard", inData);
    }

    /**
     * sim白卡重置
     *
     * @param visit
     * @param emptyCardId
     *            //白卡ID
     * @param resTypeCode
     *            //资源类型
     * @return
     * @throws Exception
     */
    /*public static IDataset resetEmptyCard(String emptyCardId, String tradeEparyCode, String tradeCityCode, String tradeDepartId, String tradeStaffId, String routeEparchyCode, String inModeCode, String resTypeCode) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("EMPTY_CARD_ID", emptyCardId);
        inParam.put("TRADE_EPARCHY_CODE", tradeEparyCode);
        inParam.put("TRADE_CITY_CODE", tradeCityCode);
        inParam.put("TRADE_DEPART_ID", tradeDepartId);
        inParam.put("TRADE_STAFF_ID", tradeStaffId);
        inParam.put("IN_MODE_CODE", inModeCode);
        inParam.put("RES_TYPE_CODE", resTypeCode);
        setPublicParam(inParam);
        IDataset ret = CSAppCall.call("RM.ResSimCardIntfSvc.resetEmptyCard", inParam);//del
        return ret;
    }*/

    /**
     * cmnet占用
     *
     * @param res_type_code
     * @param res_no
     * @return
     * @throws Exception
     */
    public static IDataset resPossessForCmnet(String res_type_code, String res_no) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("RES_NO", res_no);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResCheckVarNumIntSvc.cmnetModifyStateInfo", inData);//无代码
    }

    /**
     * 物联网号码占用
     *
     * @author sunxin
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            // 检测号码
     * @param res_type_code
     *            //资源类型 0=号码 【无用参数】
     * @return
     * @throws Exception
     */
    public static IDataset resPossessForIOTMphone(String occupy_type_code, String x_get_mode, String sim_card_no, String imsi, String serial_number, String res_type_code, String product_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
        inData.put("X_GET_MODE", x_get_mode); // 获取方式
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("SIM_CARD_NO", sim_card_no);
        inData.put("IMSI", imsi);
        inData.put("PRODUCT_ID", product_id);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.useOccupyPhoneNum",inData);//CSAppCall.callTerminal("TM.ResPhoneIntfSvc.useOccupyPhoneNum", inData, false).getData();
    }

    /**
     * 物联网sim卡占用 sunxin
     *
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param res_type_code
     *            //资源类型 【无用参数】
     * @return
     * @throws Exception
     */
    public static IDataset resPossessForIOTSim(String occupy_type_code, String x_get_mode, String serial_number, String sim_card_no, String res_type_code,String check_tag, String trade_id, String user_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("X_GET_MODE", x_get_mode);
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("SIM_CARD_NO", sim_card_no);
        //零售库存中心新增参数
        inData.put("X_CHECK_TAG", check_tag);
        inData.put("TRADE_ID", trade_id);
        inData.put("USER_ID", user_id);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.useOccupySimCard",inData);//CSAppCall.callTerminal("TM.ResSimCardIntfSvc.useOccupySimCard", inData, false).getData();
    }

    /**
     * 号码占用
     *
     * @author sunxin
     * @param visit
     * @param sim_card_no
     *            //sim卡号
     * @param imsi
     *            //imsi
     * @param serial_number
     *            // 检测号码
     * @param product_id
     *            //产品id
     * @param trade_id
     *            //流水号
     * @param brand_Code
     *            //品牌
     * @return
     * @throws Exception
     */
    public static IDataset resPossessForMphone(String sim_card_no, String imsi, String serial_number, String product_id, String trade_id, String brand_Code, String check_tag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("SIM_CARD_NO", sim_card_no);
        inData.put("IMSI", imsi);
        inData.put("PRODUCT_ID", product_id);
        inData.put("TRADE_ID", trade_id);
        inData.put("BRAND_CODE", brand_Code);
        inData.put("X_CHECK_TAG", check_tag);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.useOccupyPhoneNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.useOccupyPhoneNum", inData);
    }

    /**
     * 号码占用
     *
     * @author sunxin
     * @param visit
     * @param sim_card_no
     *            //sim卡号
     * @param imsi
     *            //imsi
     * @param serial_number
     *            // 检测号码
     * @param product_id
     *            //产品id
     * @param trade_id
     *            //流水号
     * @param brand_Code
     *            //品牌
     *  @param msisdn_type
     *            //副号码类型
     * @return
     * @throws Exception
     */
    public static IDataset resPossessForMphone(String sim_card_no, String imsi, String serial_number, String product_id, String trade_id, String brand_Code, String check_tag,String msisdn_type) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("SIM_CARD_NO", sim_card_no);
        inData.put("IMSI", imsi);
        inData.put("PRODUCT_ID", product_id);
        inData.put("TRADE_ID", trade_id);
        inData.put("BRAND_CODE", brand_Code);
        inData.put("X_CHECK_TAG", check_tag);
        inData.put("X_PHONE_TYPE", msisdn_type);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.useOccupyPhoneNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.useOccupyPhoneNum", inData);
    }


    /**
     * sim卡占用（用于兼容买断卡处理）
     *
     * @author sunxin
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param x_check_tag
     *            //0-普通开户/补换卡, 2-批量预开户
     * @param trade_id
     *            //流水号
     *@param user_id
     *            //userId
     *@param advanceFee
     *            //预存款 非必填
     *@param agentFee
     *            //代办费 计算酬金 非必填
     * @param product_id
     *            //产品id
     *@param trade_type_code
     *            //业务类型
     *@param agentId
     *            //代理商部门id
     * @return
     * @throws Exception
     */
    public static IDataset resPossessForSimAgent(String occupy_type_code, String serial_number, String sim_card_no, String check_tag, String trade_id, String user_id, String advanceFee, String agentFee, String product_id, String tradeTypeCode,
            String agentId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("SIM_CARD_NO", sim_card_no);
        inData.put("X_CHECK_TAG", check_tag);
        inData.put("TRADE_ID", trade_id);
        inData.put("USER_ID", user_id);
        inData.put("AGENT_FEE", agentFee);
        inData.put("ADVANCE_FEE", advanceFee);
        inData.put("PRODUCT_ID", product_id);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("AGENT_DEPART_ID", agentId);// 部门
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.useOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.useOccupySimCard", inData);
    }

    /**
     * sim资源释放
     *
     * @param visit
     * @param x_get_mode
     *            //获取方式 3，根据资源类型与操作员释放
     * @param res_no
     *            //资源号码
     * @param x_get_mode
     *            //　　X_GET_MODE：0，根据资源编码释放；1，根据证件号释放；2，根据部门释放；3，根据资源类型与操作员释放；
     * @param pspt_id
     *            //
     * @return
     * @throws Exception
     */
    /*public static IDataset resRelease(String res_no, String x_get_mode, String pspt_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", res_no);
        inData.put("X_GET_MODE", x_get_mode);
        inData.put("PSPT_ID", pspt_id);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResOtherIntfSvc.releaseResNo", inData);//del
    }*/

    /**
     * 铁通固话号码释放
     *
     * @author liuzz
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            //手机号码
     * @param res_type_code
     *            //资源类型
     * @return
     * @throws Exception
     */
    /*public static IDataset ResTempOccupyReleaseForFixLine(String resTypeCode, String x_get_mode, String serial_number, String xTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TRADE_CODE", "ReleaseResTempoccupySingle");
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("X_GETMODE", x_get_mode);
        inData.put("RES_CODE", serial_number);
        inData.put("RES_NO", serial_number);
        inData.put("X_TAG", xTag);
        inData.put("X_CHECK_TAG", "0");// 查空闲表
        inData.put("PARA_VALUE1", "3");
        inData.put("PARA_VALUE2", "0");
        inData.put("PARA_VALUE3", SysDateMgr.getSysDate());
        inData.put("PARA_VALUE6", "0");// 检查是否密码卡

        IDataset dataOutput = CSAppCall.call("TT.ResTempOccupyRelease", inData);//del
        return dataOutput;
    }*/

    /**
     * 复机时校验手机号码是否可用
     *
     * @SERIAL_NUMBER 手机号码
     * @return
     * @throws Exception
     */
    public static IDataset restoreCheckMPhone(String serialNumber) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("SERIAL_NUMBER", serialNumber);
        return callRes("RCF.resource.INumberIntfQuerySV.checkMphoneRestore",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.checkMphoneRestore", inData);
    }

    /**
     * 复机时校验SIM是否可用
     *
     * @simCardNo sim卡卡号
     * @return
     * @throws Exception
     */
    /*public static IDataset restoreCheckSimCard(String simCardNo) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("SIM_CARD_NO", simCardNo);
        return CSAppCall.call("RM.ResSimCardIntfSvc.checkReuseResSimUse", inData);//del
    }*/

    /**
     * 物联网复机时校验手机号码是否可用
     *
     * @SERIAL_NUMBER 手机号码
     * @return
     * @throws Exception
     */
    /*public static IDataset restoreCheckTMPhone(String serialNumber) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber);
        setPublicParam(inData);
        return CSAppCall.callTerminal("TM.ResPhoneIntfSvc.checkMphoneRestore", inData, false).getData();//del
    }*/

    /**
     * 物联网复机时校验SIM是否可用
     *
     * @simCardNo sim卡卡号
     * @return
     * @throws Exception
     */
    /*public static IDataset restoreCheckTSimCard(String simCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", simCardNo);
        setPublicParam(inData);
        return CSAppCall.callTerminal("TM.ResSimCardIntfSvc.checkReuseResSimUse", inData, false).getData();//del
    }*/

    /**
     * 复机号码恢复
     *
     * @return
     * @throws Exception
     */
    public static IDataset restoreMobile(String serialNumber, String productId) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);
        callParam.put("SERIAL_NUMBER", serialNumber);
        callParam.put("PRODUCT_ID", productId);
        return callRes("RCF.resource.INumberIntfOperateSV.mphoneRestore",callParam);//CSAppCall.call("RM.ResPhoneIntfSvc.mphoneRestore", callParam);
    }

    /**
     * 复机sim卡恢复
     *
     * @return
     * @throws Exception
     */
    public static IDataset restoreSimcard(String simcardNo) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);
        callParam.put("SIM_CARD_NO", simcardNo);
        return callRes("RCF.resource.ISimcardIntfOperateSV.undoSim",callParam);//CSAppCall.call("RM.ResSimCardIntfSvc.undoSim", callParam);
    }

    /**
     * 物联网复机号码恢复
     *
     * @return
     * @throws Exception
     */
    public static IDataset restoreTMobile(String serialNumber, String productId) throws Exception
    {
        IData callParam = new DataMap();
        setPublicParam(callParam);
        callParam.put("SERIAL_NUMBER", serialNumber);
        callParam.put("PRODUCT_ID", productId);
        return callRes("RCF.resource.INumberIntfOperateSV.mphoneRestore",callParam);//CSAppCall.callTerminal("TM.ResPhoneIntfSvc.updateMphonecodeStateRestore", callParam, false).getData();
    }

    /**
     * 物联网复机sim卡恢复
     *
     * @return
     * @throws Exception
     */
    public static IDataset restoreTSimcard(String simcardNo) throws Exception
    {
        IData callParam = new DataMap();
        callParam.put("SIM_CARD_NO", simcardNo);
        setPublicParam(callParam);
        return callRes("RCF.resource.ISimcardIntfOperateSV.undoSim",callParam);//CSAppCall.callTerminal("TM.ResSimCardIntfSvc.undoSim", callParam, false).getData();
    }

    /**
     * 铁通固话号码实占
     *
     * @author liuzz
     * @param x_get_mode
     *            //获取方式
     * @param serial_number
     *            //手机号码
     * @param res_type_code
     *            //资源类型
     * @return
     * @throws Exception
     */
    /*public static IDataset ResUseManagerForFixLine(String busiType, String productId, String resTypeCode, String x_get_mode, String serial_number, String xTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TRADE_CODE", "IMphoneCodeUse");
        inData.put("BUSI_TYPE", busiType);// pboss要求传一个字段区分是什么业务
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("X_GETMODE", x_get_mode);
        inData.put("RES_CODE", serial_number);
        inData.put("RES_NO", serial_number);
        inData.put("X_TAG", xTag);
        inData.put("X_CHECK_TAG", "0");// 查空闲表

        inData.put("PARA_VALUE1", "0");
        inData.put("PARA_VALUE2", "0");
        inData.put("PARA_VALUE3", SysDateMgr.getSysDate());
        inData.put("PARA_VALUE4", productId);// 检查是否密码卡
        inData.put("PARA_VALUE6", "0");// 检查是否密码卡

        IDataset dataOutput = CSAppCall.call("TT.ResUseManager", inData);//del
        return dataOutput;
    }*/

    /**
     * 积分兑换有价卡SRV_STR9字段设置
     *
     * @author huangsl
     * @param valueCardNo
     * @return
     * @throws Exception
     */
    public static IDataset resValueCardCheck(String valueCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("VALUE_CARD_NO", valueCardNo);
        IDataset dataOutput = callRes("RCF.resource.IPayCardIntfQuerySV.queryVCCardInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.queryVCCardInfo", inData);
        return dataOutput;
    }

    /**
     * @param 终端库存查询接口
     * @param resTradeCode
     * @param resTypeCode
     * @param terminalModelCode
     * @param cityCode
     * @return
     * @throws Exception
     */
    /*public static IDataset saleActiveTerminalQry(String resTradeCode, String resTypeCode, String terminalModelCode, String cityCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TRADE_CODE", resTradeCode);
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("TERMINAL_MODEL_CODE", terminalModelCode);
        inData.put("CITY_CODE", cityCode);
        setPublicParam(inData);
        return CSAppCall.call("ITF_MONNI", inData);//del
    }*/

    /**
     * 白卡选占
     *
     * @param emptyCardId
     *            白卡卡号
     * @param occupyTypeCode
     *            0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param isNotRelease
     *            为1时表示只释放当前员工当前操作的白卡，其他表示释放当前员工下所有白选占的白卡
     * @return
     * @throws Exeption
     */
    public static IDataset selOccupyEmptyCard(String emptyCardId, String occupyTypeCode, String isNotRelease) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("OCCUPY_TYPE_CODE", occupyTypeCode);
        inData.put("IS_NOTRELEASE", isNotRelease);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.selOccupyEmptyCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.selOccupyEmptyCard", inData);
    }

    // 公共信息参数设置
    private static void setPublicParam(IData inData) throws Exception
    {
        inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 用户归属地州
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
        inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
    }

    /**
     * 国税已用发票作废
     *
     * @param param
     *            业务参数
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset stateTaxInvoiceCancel(String taxNo, String ticketId, String fwm, String kplx, String staffId,
            String nsrlx, String fpzlDm, String pyCode,String  ticketTypeCode) throws Exception
    {
        IData inData = new DataMap();

        /**
         * REQ201503040003关于资源侧发票使用界面走权限控制的优化
         * 2015-03-27 CHENXY3
         * 新增调用接口传入 TRADE_TYPE_CODE   TRADE_TYPE_NAME
         * tradeStaffId=tradeStaffId+"|"+tradeTypeCode+"-"+tradeTypeName;
         * */
        String tradeTypeCode="";
        String tradeTypeName="";
        if(staffId!=null&&staffId.indexOf("|")>-1){
            if(staffId!=null&&staffId.indexOf("-")>-1){
                tradeTypeName=staffId.substring(staffId.indexOf("-")+1);
                tradeTypeCode=staffId.substring(staffId.indexOf("|")+1,staffId.indexOf("-"));
            }else{
                tradeTypeCode=staffId.substring(staffId.indexOf("|")+1);
            }

            staffId=staffId.substring(0,staffId.indexOf("|"));
        }
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("TRADE_TYPE_NAME", tradeTypeName);

        inData.put("TAX_NO", taxNo);
        inData.put("TICKET_ID", ticketId);
        inData.put("FWM", fwm);
        inData.put("KPLX", kplx);// 开票类型
        inData.put("STAFF_ID", staffId);
        inData.put("NSRLX", nsrlx);
        inData.put("FPZL_DM", fpzlDm);// 发票种类代码
        inData.put("PYCODE", pyCode);// 票样代码
        inData.put("TICKET_TYPE_CODE", ticketTypeCode);//票样类型
        setPublicParam(inData);
        return callRes("RCF.resource.IInvoiceIntfOperateSV.receiptCancel",inData);//CSAppCall.call("RM.ResTicketIntfSvc.receiptCancel", inData);
    }

    /**
     * 国税已用发票作废
     * REQ201501270018关于票据作废界面走域权控制的优化
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset stateTaxInvoiceCancel(IData inparam) throws Exception
    {
        IData inData = new DataMap();
        inData.put("TAX_NO", inparam.getString("TAX_NO"));
        inData.put("TICKET_ID", inparam.getString("TICKET_ID"));
        inData.put("FWM", inparam.getString("FWM"));
        inData.put("KPLX", inparam.getString("KPLX"));// 开票类型
        inData.put("STAFF_ID", inparam.getString("STAFF_ID"));
        inData.put("NSRLX", inparam.getString("NSRLX"));
        inData.put("FPZL_DM", inparam.getString("FPZL_DM"));// 发票种类代码
        inData.put("PYCODE", inparam.getString("PYCODE"));// 票样代码
        inData.put("TICKET_TYPE_CODE", inparam.getString("TICKET_TYPE_CODE"));//票样类型

        /**
         * REQ201503040003关于资源侧发票使用界面走权限控制的优化
         * 2015-03-27 CHENXY3
         * 新增调用接口传入 TRADE_TYPE_CODE   TRADE_TYPE_NAME
         * */
        inData.put("TRADE_TYPE_NAME", inparam.getString("TRADE_TYPE_NAME")); // 业务名称
        inData.put("TRADE_TYPE_CODE", inparam.getString("TRADE_TYPE_CODE"));// 业务类别
        inData.put("TRADE_ID", inparam.getString("TRADE_ID"));//业务ID


        setPublicParam(inData);
        return callRes("RCF.resource.IInvoiceIntfOperateSV.receiptCancel",inData);//CSAppCall.call("RM.ResTicketIntfSvc.receiptCancel", inData);
    }

    /**
     * 国税发票校验
     *
     * @param param
     *            业务参数,涉及到参数比较多,此处传入IData,字段说明详见资源接口文档
     * @param
     * @return FWM 防伪码 EWM 二维码 KPRQ 开票日期 YYYYMMDDhh24MiSS
     * @throws Exception
     */
    public static IDataset stateTaxInvoiceCheck(IData param) throws Exception
    {
        setPublicParam(param);
        return callRes("RCF.resource.IInvoiceIntfOperateSV.invoiceCheck",param);//CSAppCall.call("RM.ResTicketIntfSvc.receiptCheck", param);
    }

    /**
     * 临时号码销户
     *
     * @param visit
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset tempPhoneOpenDestory(String serialNumber, String xChoiceTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumber); // 号码
        inData.put("X_CHOICE_TAG", xChoiceTag); // 0：开户 1：销户
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.tempPhoneOpenDestory",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.tempPhoneOpenDestory", inData);
    }

    /**
     * 票据返销
     *
     * @recoverTicketInfos 需要返销的票据信息 IDataset.toString()
     * @return
     * @throws Exception
     */
    /*public static IDataset ticketRecover(String recoverTicketInfos) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RECOVER_TICKET_INFOS", recoverTicketInfos);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResTicketIntfSvc.ticketRecover", inData);//del
    }*/

    /**
     * 有价卡返销接口
     *
     * @param visit
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            终止卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     *            资源类型
     * @throws Exception
     */
    public static IDataset undoEntityCardInfo(String res_no_s, String res_no_e, String stock_id, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.changeEntitycardReturnInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.changeEntitycardReturnInfo", inData);
    }

    /**
     * 物联网号码返销
     *
     * @author sunxin
     * @param visit
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset undoResPossessForIOTMphone(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number); // 返销号码
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.cancelMphonecode",inData);//CSAppCall.callTerminal("TM.ResPhoneIntfSvc.cancelMphonecode", inData, false).getData();
    }

    /**
     * 物联网sim卡返销
     *
     * @author sunxin
     * @param visit
     * @param sim_card_no
     * @return
     * @throws Exception
     */
    public static IDataset undoResPossessForIOTSim(String sim_card_no) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", sim_card_no);// 返销sim卡
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.openUndoSim",inData);//CSAppCall.callTerminal("TM.ResSimCardIntfSvc.openUndoSim", inData, false).getData();
    }

    /**
     * 号码返销
     *
     * @author sunxin
     * @param visit
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset undoResPossessForMphone(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number); // 返销号码
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.cancelMphonecode",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.cancelMphonecode", inData);
    }

    /**
     * sim卡返销
     *
     * @author sunxin
     * @param visit
     * @param sim_card_no
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset undoResPossessForSim(String sim_card_no,String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SIM_CARD_NO", sim_card_no);// 返销sim卡
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);// 返销sim卡
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.openUndoSim",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.openUndoSim", inData);
    }

    /**
     * 实物返销
     *
     * @throws Exception
     */
    /*public static void undoSaleGoods(String res_id, String oper_num, String tradeId, String stockId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("X_GETMODE", "0");// 0 根据资源数量进行预占;1 根据资源编码进行预占
        inData.put("RES_KIND_CODE", res_id);// 资源类型编码
        inData.put("OPER_NUM", oper_num);// 数量
        if (StringUtils.isNotBlank(stockId))
        {
            inData.put("STOCK_ID", stockId);
        }
        inData.put("SALE_TRADE_ID", tradeId);
        setPublicParam(inData);
        CSAppCall.call("RM.ResGoodsIntfSvc.returnSaleGoods", inData);//del
    }*/

    /**
     * 有价卡返销接口
     *
     * @param visit
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            终止卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     *            资源类型
     * @throws Exception
     */
    /*public static void undoValueCardInfo(String res_no_s, String res_no_e, String stock_id, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", res_type_code);
        setPublicParam(inData);
        CSAppCall.call("RM.ResPayCardIntfSvc.modifyValuecardReturnInfo", inData);//del
    }*/

    /**
     * @param 修改有价卡状态
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param sale_money
     *            销售价格
     * @return
     * @throws Exception
     */
    /*public static IDataset updateEntityCardInfoIntf(String res_no_s, String res_no_e, String sale_money, String res_type_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("SALE_MONEY", sale_money);
        inData.put("RES_TYPE_CODE", res_type_code);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.modifyEntityCardSaleInfo", inData);//del
    }*/

    /**
     * @param 修改有价卡状态
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param sale_money
     *            销售价格
     * @return
     * @throws Exception
     */
    public static IDataset updateValueCardInfoIntf(String res_no_s, String res_no_e, String sale_money, String fee_tag, String res_type_code, String stock_id, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("SALE_MONEY", sale_money);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("FEE_TAG", fee_tag);
        inData.put("STOCK_ID", stock_id);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.occupyValueCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.modifyValueCardSaleInfo", inData);
    }

    /**
     * @param 修改流量卡状态
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param sale_money
     *            销售价格
     * @return
     * @throws Exception
     */
    public static IDataset updateValueFlowCardInfoIntf(String res_no_s, String res_no_e, String sale_money, String fee_tag, String res_type_code, String stock_id, String tradeTypeCode, String group_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("SALE_MONEY", sale_money);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("FEE_TAG", fee_tag);
        inData.put("STOCK_ID", stock_id);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        inData.put("GROUP_ID", group_id);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.occupyFlowCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.modifyFlowCardSaleInfo", inData);
    }

    /**
     * @param 有价卡退卡接口
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号 @ isRile 有价卡赠送返销，文件时使用
     * @return
     * @throws Exception
     */
    public static IDataset updateValueCardReturnInfoIntf(String res_no_s, String res_no_e, String res_type_code, String stock_id, String isFile, IDataset cardList, String upStaffId, String upCityCode, String upDepartId, String tradeTypeCode)
            throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("STOCK_ID", stock_id);
        inData.put("IS_FILE", isFile);
        inData.put("CARD_LIST", cardList);
        inData.put("UP_STAFF_ID", upStaffId);
        inData.put("UP_CITY_CODE", upCityCode);
        inData.put("UP_DEPART_ID", upDepartId);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.modifyValuecardReturnInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.modifyValuecardReturnInfo", inData);
    }

    /**
     * @param 修改有价卡状态为补录
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param sale_money
     *            销售价格
     * @return
     * @throws Exception
     */
    /*public static IDataset updReviewResInfo(String res_no_s, String res_no_e, String res_type_code, String stock_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("BOX_CODE", "1");
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("STOCK_ID", stock_id);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.updReviewResInfo", inData);//del
    }*/

    /**
     * 更新员工当前票据号
     *
     * @param visit
     * @param staffId
     * @param resKindCode
     * @param ticketId
     * @param taxNo
     * @return
     * @throws Exception
     */
    /*public static IDataset updStaffTicket(String tradeId, String resKindCode, String ticketId, String taxNo) throws Exception
    {
        IData callParam = new DataMap();

        IDataset ds = new DatasetList();
        IData param = new DataMap();
        param.put("SALE_TRADE_ID", tradeId);
        param.put("TICKET_ID", ticketId);
        param.put("TAX_NO", taxNo);
        param.put("RES_KIND_CODE", resKindCode);

        ds.add(param);
        callParam.put("USE_TICKET_INFOS", ds);
        setPublicParam(callParam);
        return CSAppCall.call("RM.ResTicketIntfSvc.useTicket", callParam);//del

    }*/

    /**
     * 实体卡换卡校验
     *
     * @author chenzm
     * @param visit
     * @param res_no_s
     *            开始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     * @return
     * @throws Exception
     */
    /*public static IDataset validateChangeCardInfo(String old_no, String new_no, String stock_id, String in_mode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("NEW_CARD_NO", new_no);
        inData.put("OLD_CARD_NO", old_no);
        inData.put("STOCK_ID", stock_id);
        inData.put("IN_MODE_CODE", in_mode);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.getResInfo", inData);//del
    }*/

    /**
     * 实体卡换卡校验
     *
     * @author chenzm
     * @param visit
     * @param res_no_s
     *            开始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public static IDataset validateEntityCardInfo(String old_no, String new_no, String stock_id, String in_mode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_E", new_no);
        inData.put("RES_NO_S", old_no);
        inData.put("STOCK_ID", stock_id);
        inData.put("RES_TYPE_CODE", "3");
        setPublicParam(inData);

        return callRes("RCF.resource.IPayCardIntfOperateSV.selOccupyEntityCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.getEntityInfo", inData);
    }

    /**
     * 有价卡延期
     *
     * @param visit
     * @param res_no_s
     * @param res_no_e
     * @param endDate
     *            延期的时间
     * @return
     * @throws Exception
     */
    /*public static IDataset valuecardDelay(String res_no_s, String res_no_e, String endDate, String stock_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO_S", res_no_s);
        inData.put("RES_NO_E", res_no_e);
        inData.put("START_DATE_U", endDate);
        inData.put("STOCK_ID", stock_id);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.valuecardDelay", inData);//del
    }*/

    /**
     * WLAN电子占用
     *
     * @param resNo
     *            资源卡号
     * @param resTypeCode
     *            资源类型 32I305
     * @return
     * @throws Exception
     */
    public static IDataset wlanCardOccupy(String resNo, String resTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_KIND_CODE", resTypeCode);
        inData.put("RES_NO", resNo);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.occupyWlanCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.wlanCardOccupy", inData);
    }

    /**
     * @param 电子卡占用接口
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @return
     * @throws Exception
     */
    /*public static IDataset wlanCardOccupy(String res_no, String resKindCode, String saleMoney) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", res_no);
        inData.put("RES_TYPE_CODE", "321");
        inData.put("RES_KIND_CODE", resKindCode);
        inData.put("SALE_MONEY", saleMoney);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.wlanCardOccupy", inData);//del
    }*/

    /**
     * WLAN电子卡分配
     *
     * @param resTypeCode
     *            资源类型 32I305
     * @return [{"VALUE_PRICE":"250","RES_NO":"10135111120100024005318796"}]
     * @throws Exception
     */
    public static IDataset wlanCardOccupySel(String resTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_KIND_CODE", resTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfOperateSV.selOccupyWlanCard",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.wlanCardOccupySel", inData);
    }

    /**
     * @param 电子卡选占接口
     * @param res_no_s
     *            起始卡号
     * @param res_no_e
     *            结束卡号
     * @param stock_id
     *            部门编码
     * @return
     * @throws Exception
     */
    /*public static IDataset wlanCardPreOccupy(String resTypeCode, String resKindCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("RES_KIND_CODE", resKindCode);
        setPublicParam(inData);
        return CSAppCall.call("RM.ResPayCardIntfSvc.wlanCardOccupySel", inData);//del
    }*/
    /**
     * 根据资源类型，返回详细信息，判断4G等
     * @param resTypeCode 资源类型编码
     * @return NET_TYPE_CODE: 00-23G;01-4G;07-物联网;18-TD无线座机;19-固话;    RES_TYPE_CODE: 0-号码；1-SIM卡；3-有价卡；5-票据；6-白卡
     * @throws Exception
     */
    public static IDataset qrySimCardTypeByTypeCode(String resTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TYPE_CODE", resTypeCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IResPublicIntfQuerySV.qryTypeByCode",inData);//CSAppCall.call("RM.ResQueryIntfSvc.qryTypeByCode", inData);
    }

    public static IDataset qryResKindByCode(String resKindCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_KIND_CODE", resKindCode);
        setPublicParam(inData);
        return callRes("RCF.resource.IResPublicIntfQuerySV.qryKindByCode", inData);//暂未开发
    }

    /**
     * 号码选占（包括释放选占资源,检查资源,选占资源）(批开用,国内一卡多号改造)
     *
     * @author liuling
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            // 检测号码
     * @param res_type_code
     *            //资源类型 0=号码 【无用参数】
     * @param IS_NOTRELEASE
     *            //为1时表示只释放当前员工当前操作的号码，其他表示释放当前员工下所有选占的号码
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForMphoneBatch(String occupy_type_code, String serial_number, String res_type_code,String release,String brandCode) throws Exception
    {
        IData inData = new DataMap();
        setPublicParam(inData);
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);// 0-普通开户；1-网上开户；2-代理商开户；默认为0
        inData.put("SERIAL_NUMBER", serial_number); // 检测号码
        inData.put("RES_TYPE_CODE", res_type_code);// 资源类型
        inData.put("IS_NOTRELEASE", release);// 不释放标记
        inData.put("PARA_VALUE8", brandCode);  //国内一卡多号MOSP
        return callRes("RCF.resource.INumberIntfOperateSV.selOccupyPhoneNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.selOccupyPhoneNum", inData);
    }

    /**
     * SIM卡选占(批开用 ,国内一卡多号改造)
     *
     * @author liuling
     * @param visit
     * @param OCCUPY_TYPE_CODE
     *            //0-普通开户；1-网上开户；2-代理商开户；默认为0
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @param IS_NOTRELEASE
     *            //为1时表示只释放当前员工当前操作的sim卡，其他表示释放当前员工下所有sim选占的sim卡
     * @return
     * @throws Exception
     */
    public static IDataset checkResourceForSimBatch(String occupy_type_code, String serial_number, String sim_card_no, String release,String brandCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("SERIAL_NUMBER", serial_number);
        inData.put("SIM_CARD_NO", sim_card_no); // 检测sim卡
        if (StringUtils.isNotEmpty(release))
        {
            inData.put("IS_NOTRELEASE", release);// 不释放标记
        }
        setPublicParam(inData);
        inData.put("PARA_VALUE8", brandCode);  //国内一卡多号MOSP
        IDataset dataOutput = callRes("RCF.resource.ISimcardIntfOperateSV.selOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.selOccupySimCard", inData);
        return dataOutput;
    }

    /**
     * REQ201503230020两不一快临时卡自助换卡前系统拦截限制
     * CHENXY3 2015-03-31
     * RETURN RES_STATE
     * */
    public static IDataset getTemPhoneResState(String tempNumber) throws Exception
    {
        IData inData = new DataMap();
        inData.put("X_RESNO_S", tempNumber);
        inData.put("X_RESNO_E", tempNumber);
        inData.put("RES_STATE", "5");
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.qryTempPhone",inData);//CSAppCall.call("RM.TempPhoneMgrSvc.qryTempPhone", inData);
    }

    /**
     * 异地补换卡SIM卡返销
     */
    public static IDataset changeSimCardCancelYD(String empty_card_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", empty_card_id);
        setPublicParam(inData);
        return callRes("RCF.resource.ISncardIntfOperateSV.changeEmptyCardCancel",inData);//CSAppCall.call("RM.EmptyCardIntfSvc.changeEmptyCardCancel", inData);
    }

    /**
     * 异地补换卡SIM卡返销(类似开户失败返销)
     */
    public static IDataset transFormEmptyCardCancel(String empty_card_id) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", empty_card_id);
        setPublicParam(inData);
        return callRes("RCF.resource.ISncardIntfOperateSV.transFormEmptyCardCancel",inData);//类似开户失败返销白卡接口
    }

    public static IDataset checkEmptycardInfo(String res_no, String qry_tag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", res_no);
        inData.put("QRY_TAG", qry_tag);
        setPublicParam(inData);
        return callRes("RCF.resource.ISncardIntfQuerySV.getCheckEmptycardInfo",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.getCheckEmptycardInfo", inData);
    }
    public static IDataset getNetWorkPhone(IData inData) throws Exception
    {
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.getResNo",inData);//CSAppCall.call("RM.ResQueryIntfSvc.getResNo", inData);
    }
    public static IDataset resTempOccupyByNetSel(IData inData) throws Exception
    {
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.selOccupyPhoneByNet",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.selOccupyPhoneByNet", inData);
    }

    /**
     * 流量卡查询资源信息
     * <p>Title: queryValuecardInfo</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param inData
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2017-1-13 上午11:18:25
     */
    public static IDataset queryValuecardInfo(IData inData) throws Exception
    {
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfQuerySV.queryPayCardInfo",inData);//CSAppCall.call("RM.ResPayCardIntfSvc.queryValuecardInfo", inData);
    }
    /**
     * REQ201702170009关于过期有价卡不能做换卡的优化需求
     * <br/>
     * 通过卡号查询   有价卡VC信息
     * @author zhuoyingzhi
     * @date 20170314
     * @param valueCardNo
     * @return
     * @throws Exception
     */
    public static IDataset queryMPCardStatus(String valueCardNo) throws Exception
    {
        IData inData = new DataMap();
        inData.put("VALUE_CARD_NO", valueCardNo);
        inData.put("RES_NO_S", valueCardNo);
        setPublicParam(inData);
        return callRes("RCF.resource.IPayCardIntfQuerySV.queryVCInfo",inData);//CSAppCall.call("RM.ValueCardLogQuerySvc.queryMPCardStatus", inData);
    }

    /**
     * 开户撤单释放资源预占
     * @author huanghua
     * @date 20170602
     * @param resNo,resTypeCode,remark
     * @return
     * @throws Exception
     */
    public static IDataset releaseAllResByNo(String resNo, String resTypeCode, String remark, String mode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_NO", resNo);
        inData.put("OCCUPY_TYPE_CODE", "0");
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("REMARK", remark);
        inData.put("MODE", mode);
        setPublicParam(inData);
        return callRes("RCF.resource.IResPublicIntfOperateSV.releaseAllResByNo",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.releaseAllResByNo", inData);
    }

    /**
    public static IDataset remoteWriteCardRet(String imsi, String emptyCardId, String simCardNo, String writeTag, String remoteMode, String feeTag, String smsKindCode, String ki, String opc, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", emptyCardId);
        inData.put("IMSI", imsi);
        inData.put("FEE_TAG", feeTag);
        inData.put("WRITE_TAG", writeTag);
        inData.put("SIM_CARD_NO", simCardNo);
        inData.put("SMS_KIND_CODE", smsKindCode);
        inData.put("REMOTE_MODE", remoteMode);
        inData.put("KI", ki);
        inData.put("OPC", opc);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);// 业务类型
        setPublicParam(inData);

        return CSAppCall.call("RM.ResSimCardIntfSvc.remoteWriteCardRet", inData);//del
    }
     **/
    /*public static IDataset releaseResOccupy(String x_get_mode, String res_no, String res_type_code, String staff_id, String tradeTypeCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("X_GETMODE", x_get_mode);
        inData.put("RES_NO", res_no);
        inData.put("RES_TYPE_CODE", res_type_code);
        inData.put("STAFF_ID", staff_id);
        inData.put("TRADE_TYPE_CODE", tradeTypeCode);// 业务类型
        setPublicParam(inData);
        return CSAppCall.call("RM.ReleaseResOccupySvc.releaseResOccupy", inData);//del
    }*/
    /*public static IDataset getEmptyCardKi(String emptyCardId,String routeTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EMPTY_CARD_ID", emptyCardId);
        if ("1".equals(routeTag)) {
            inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());// 用户归属地州
            inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
            inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
            inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
            inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
            inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        } else {
            setPublicParam(inData);
        }
        return CSAppCall.call("RM.EmptyInfoIntfSvc.queryEmptyCardKi", inData);//del
    }*/


    /**
     * 随机获取sim卡与号码并选占(一号多终端接口开户用)
     * @return
     * @throws Exception
     */
    public static IDataset getONMTInfo() throws Exception
    {
        IData inData = new DataMap();

        inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());// 用户归属地州
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
        inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        return callRes("RCF.resource.INumberIntfOperateSV.getONMTInfo",inData);//CSAppCall.call("RM.PhoneInfoIntfSvc.getONMTInfo", inData);
    }

    public static IDataset queryResellInfo(IData inData) throws Exception
    {
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.queryResellInfo",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.queryResellInfo", inData);
    }



     /**
      * 和多号
     * 随机获取一个副号码(营业厅用)
     * @return
     * @throws Exception
     */
    public static IDataset getSubPhone(String eparchy_code) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EPARCHY_CODE", eparchy_code);
        return callRes("RCF.resource.INumberIntfOperateSV.getVirtualUseNum", inData);//new
    }
    /**
     * 和多号
     * 随机获取一个副号码,提供搜索功能(营业厅用)
     */
    public static IDataset getSubPhone(String eparchy_code,String start_num) throws Exception
    {
        IData inData = new DataMap();
        inData.put("EPARCHY_CODE", eparchy_code);
        inData.put("START_NUM", start_num);
        return callRes("RCF.resource.INumberIntfOperateSV.getVirtualUseNum", inData);//new
    }
    /**
     * [releaseVirtualUseNum 和多号 释放虚拟副号码]
     * @param  input     [description]
     * @return           [description]
     * @throws Exception [description]
     */
    public static IDataset releaseVirtualUseNum(IData input) throws Exception{
        setPublicParam(input);
        return callRes("RCF.resource.INumberIntfOperateSV.releaseVirtualUseNum",input);
    }

  	/**
     * 检查副号码的合法性
     * @return
     * @throws Exception
     */
    public static IDataset checkIsSubNumber(String eparchy_code, String serialNumberB) throws Exception {
        IData inData = new DataMap();
        inData.put("EPARCHY_CODE", eparchy_code);
        inData.put("SERIAL_NUMBER", serialNumberB);

        return callRes("RCF.resource.INumberIntfOperateSV.getVirtualUseNum", inData);//new
    }
      /**
        * 副号码占用
        * @return
        * @throws Exception
        */
    public static IDataset occupyVirtualSerialNumber(String serialNumberB) throws Exception {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumberB);
        return callRes("RCF.resource.INumberIntfOperateSV.useOccupyVirtualUseNum", inData);//new
    }
    /**
        * 副号码解除绑定
        * @return
        * @throws Exception
        */
    public static IDataset unbindVirtualSerialNumber(String serialNumberB) throws Exception {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serialNumberB);
        return callRes("RCF.resource.INumberIntfOperateSV.cancelOccupyPhoneNum", inData);//new
    }

    public static IDataset desResaleSimByImsi(IData param) throws Exception {
        setPublicParam(param);
        return callRes("RCF.resource.ISimcardIntfOperateSV.desResaleSimByImsi", param);//new   RM.SimCardInResaleSvc.qryResaleSimByImsi
    }
    public static IDataset qryResaleSimByImsi(IData param) throws Exception {
        setPublicParam(param);
        return callRes("RCF.resource.ISimcardIntfQuerySV.qryResaleSimByImsi", param);//new RM.SimCardInResaleSvc.qryResaleSimByImsi
    }

    public static IDataset qryValidPhoneNumByPsptId(IData param) throws Exception {
        setPublicParam(param);
        return callRes("RCF.resource.INumberIntfQuerySV.qryValidResNumByPsptId", param);//new RM.ResPhoneIntfSvc.qryValidPhoneNumByPsptId
    }

    public static IDataset querySupplierTypeRel(String resTypeId,String corpNo) throws Exception {
        IData param = new DataMap();
        param.put("RES_TYPE_ID", resTypeId);//资源类型
        param.put("SUPPLIER_NO", corpNo);//厂商编码
        setPublicParam(param);
        return callRes("RCF.resource.IResPublicIntfQuerySV.querySupplierTypeRel", param);//new RM.ResPhoneIntfSvc.qryValidPhoneNumByPsptId
    }





    public static IDataset queryValuecardInfoPage(IData param,Pagination page) throws Exception {
        setPublicParam(param);
        return null;//CSAppCall.callPage("RM.xx.desResaleSimByImsi", param,page);//new
    }



    /**
     * 零售库存中心项目改造
     * @param svcCode
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset callRes(String svcCode,IData param)throws Exception{
        ServiceRequest request = new ServiceRequest();
        request.setData(param);
        if(log.isDebugEnabled()){
            log.debug(svcCode + " send res params: " + param);
        }
     
        ServiceResponse response = (ServiceResponse) BizServiceFactory.call(svcCode, request, null);
        IData head = response.getHead();
        String xResultcode = head.getString("X_RESULTCODE");
        if(!"0".equals(xResultcode)){
            String xResultinfo = head.getString("X_RESULTINFO");
//          CSAppException.apperr(BizException.CRM_BIZ_171, svcCode, xResultinfo);
        }
        IDataset dataset = response.getData();
//        IDataset dataset = response.getDataset(OUTDATA);
        if(log.isDebugEnabled()){
            log.debug(svcCode + " receive res result: " + dataset);
        }
        return dataset;
    }
    /**
     * 分页查询 需要取 data datacount
     * @param svcCode
     * @param param
     * @param pagi
     * @return
     * @throws Exception
     */
    public static ServiceResponse callResPage(String svcCode,IData param,Pagination pagi)throws Exception{
//      String OUTDATA = "OUTDATA";//业务数据
        ServiceRequest request = new ServiceRequest();
        BizVisit visit = CSBizBean.getVisit();
        String inCodeCode = visit.getInModeCode();
        if(StringUtils.isBlank(inCodeCode)){
            inCodeCode = "0";
        }
        param.put("IN_MODE_CODE", inCodeCode);
        request.setData(param);
        if(log.isDebugEnabled()){
            log.debug(svcCode + " send res params: " + param);
        }
        ServiceResponse response = (ServiceResponse) BizServiceFactory.call(svcCode, request, pagi);

/*        IData head = response.getHead();
        String xResultcode = head.getString("X_RESULTCODE");
        if(!"0".equals(xResultcode)){
            String xResultinfo = head.getString("X_RESULTINFO");
//          CSAppException.apperr(BizException.CRM_BIZ_171, svcCode, xResultinfo);
        }
        IDataset dataset = response.getData();
//        IDataset dataset = response.getDataset(OUTDATA);
        if(log.isDebugEnabled()){
            log.debug(svcCode + " receive res result: " + dataset);
        }*/
        return response;
    }
    private static final String OUTDATA = "OUTDATA";//业务数据
    /**
     * 查询可选占号码
     */
    public static IDataset queryNumInfo4AreaSel(IData param) throws Exception {
        setPublicParam(param);
        return callRes("RCF.resource.INumberIntfQuerySV.queryNumInfo4AreaSel", param);
    }
    
    /**
     * 最易用手机号码预占
     * 
     * @author hujj5
     * @param visit
     * @param serial_number
     *            // 检测号码
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForMphoneEasy(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("ACCESS_NUMBER", serial_number); // 检测号码
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.preEasyOccupyPhoneNum",inData);//CSAppCall.call("RM.ResPhoneIntfSvc.preOccupyPhoneNum", inData);
    }
    
    /**
     *  最易用手机sim卡预占
     * 
     * @author hujj5
     * @param visit
     * @param occupy_type_code
     *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
     * @param serial_number
     *            //手机号码
     * @param sim_card_no
     *            //sim卡
     * @return
     * @throws Exception
     */
    public static IDataset resEngrossForSimEasy(String occupy_type_code, String sim_card_no, String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("ICC_ID", sim_card_no);
        inData.put("ACCESS_NUMBER", serial_number);
        setPublicParam(inData);
        return callRes("RCF.resource.ISimcardIntfOperateSV.preEasyOccupySimCard",inData);//CSAppCall.call("RM.ResSimCardIntfSvc.preOccupySimCard", inData);
    }

    /**
     * 白卡换号
     *      校验ICCID是否合法,并返回 临时号码信息
     * @return
     * @throws Exception
     */
    public static IDataset checkICCId(String iccid) throws Exception {
        IData inData = new DataMap();
        inData.put("ICC_ID", iccid);
        return callRes("RCF.resource.INumberIntfQuerySV.qryPhoneEmptyByIccId", inData);
    }
    /**
	 * 号码预占 一号一终端
	 * 
	 * @author
	 * @param visit
	 * @param serial_number
	 *            // 检测号码
	 * @param resTypeCode
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset resEngrossOneTerminalForMphone(String serial_number) throws Exception {
		IData inData = new DataMap();
		inData.put("ACCESS_NUMBER", serial_number); // 检测号码
		setPublicParam(inData);
		return callRes("RCF.resource.INumberIntfOperateSV.preOneOccupyPhoneNum",
				inData);
	}

	/**
	 * sim卡预占  一号一终端
	 * 
	 * @author
	 * @param visit
	 * @param occupy_type_code
	 *            //选占方式 0:开户选占 , 1:用户随机选占, 2－大屏幕选号, 3－预定
	 * @param serial_number
	 *            //手机号码
	 * @param sim_card_no
	 *            //sim卡
	 * @return
	 * @throws Exception
	 */
	public static IDataset resEngrossOneTerminalForSim(String occupy_type_code, String sim_card_no, String serial_number)
			throws Exception {
		IData inData = new DataMap();
		inData.put("OCCUPY_TYPE_CODE", occupy_type_code);
        inData.put("ICC_ID", sim_card_no);
        inData.put("ACCESS_NUMBER", serial_number);
		setPublicParam(inData);
		return callRes("RCF.resource.ISimcardIntfOperateSV.preOccupySimCard",
				inData);
		// return new DatasetList(inData);//
	}
	/**
     * 随机获取esim卡并选占(一号一终端补卡用)
     * @return
     * @throws Exception
     */
	   public static IDataset selOneOccupyESim(String serial_number,BizVisit visit,String tradeTypeCode) throws Exception
	    {
	        IData inData = new DataMap();
	        
	        inData.put("ACCESS_NUMBER", serial_number);
	        inData.put(Route.USER_EPARCHY_CODE, RouteInfoQry.getEparchyCodeBySn(serial_number));// 用户归属地州
	        inData.put("TRADE_EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(serial_number));// 用户交易地州
	        inData.put("TRADE_CITY_CODE", visit.getCityCode());// 受理业务区
	        inData.put("TRADE_DEPART_ID", visit.getDepartId());// 受理部门
	        inData.put("TRADE_STAFF_ID", visit.getStaffId());// 受理员工
            inData.put("TRADE_TYPE_CODE", tradeTypeCode);//
	        inData.put(Route.ROUTE_EPARCHY_CODE, RouteInfoQry.getEparchyCodeBySn(serial_number));
	        return callRes("RCF.resource.ISimcardIntfOperateSV.selOneOccupyESim", inData);
	    }

    public static IDataset selOneOccupyESim(String serial_number,BizVisit visit) throws Exception
    {
        IData inData = new DataMap();

        inData.put("ACCESS_NUMBER", serial_number);
        inData.put(Route.USER_EPARCHY_CODE, RouteInfoQry.getEparchyCodeBySn(serial_number));// 用户归属地州
        inData.put("TRADE_EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(serial_number));// 用户交易地州
        inData.put("TRADE_CITY_CODE", visit.getCityCode());// 受理业务区
        inData.put("TRADE_DEPART_ID", visit.getDepartId());// 受理部门
        inData.put("TRADE_STAFF_ID", visit.getStaffId());// 受理员工
        inData.put(Route.ROUTE_EPARCHY_CODE, RouteInfoQry.getEparchyCodeBySn(serial_number));
        return callRes("RCF.resource.ISimcardIntfOperateSV.selOneOccupyESim", inData);
    }
	   public static IDataset selOneOccupyESim(String serial_number) throws Exception
	    {
	        return selOneOccupyESim(serial_number,null);
	    }
    /**
     * 获取号码资源信息
     * @return
     * @throws Exception
     */
    public static IDataset qryUsedNumInfosByNum(IData param) throws Exception {
		setPublicParam(param);
		return callRes("RCF.resource.INumberIntfQuerySV.qryUsedNumInfosByNum", param);//new RM.ResPhoneIntfSvc.qryValidPhoneNumByPsptId
	}
    
    /**
     * 查询手机号码是否吉祥号码
     * @param resNo
     * @param resTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset querySerialNumberIsJXH(String simCardNo) throws Exception{
        IData inData = new DataMap();
        inData.put("ACCESS_NUMBER_S", simCardNo);
        inData.put("ACCESS_NUMBER_E", simCardNo);
        inData.put("TABLE_INFO", "3");
        inData.put("QRY_TAG", "1");

        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
        inData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 渠道

        return callRes("RC.resource.INumberIntfQuerySV.queryNumInfos", inData);
    }

    public static IDataset selOnePhoneNum(String isOneNum) throws Exception{
        IData inData = new DataMap();
        inData.put("IS_ONE_NUM", isOneNum);
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfQuerySV.selOnePhoneNum", inData);
    }

    public static IDataset selOnePhoneNum(String isOneNum,String resNoS,String resNoE) throws Exception{
        IData inData = new DataMap();
        inData.put("IS_ONE_NUM", isOneNum);
        inData.put("RES_NO_S", resNoS);
        inData.put("RES_NO_E", resNoE);
        setPublicParam(inData);
        inData.put("TRADE_DEPART_ID",null);//AEE的TRADE_DEPART_ID和一号一终端号码入库的ORG_ID不同，传空不拼接这个参数去查询
        log.error("BestUseMobileBean--selOnePhoneNum--params:" + inData);
        return callRes("RCF.resource.INumberIntfQuerySV.selOnePhoneNum", inData);
    }
    
    /**
     * 无手机号码返销
     *
     * @author duhj_kd
     * @param visit
     * @param serial_number
     * @return
     * @throws Exception
     */
    public static IDataset undoResPossessForNoPhone(String serial_number) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", serial_number); // 返销号码
        setPublicParam(inData);
        return callRes("RCF.resource.INumberIntfOperateSV.cancelMphonecodeForNoPhoneWidenet",inData);
    }
}
