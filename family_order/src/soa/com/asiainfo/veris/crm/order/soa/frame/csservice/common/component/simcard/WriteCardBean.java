
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.AssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.IssueData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.WebServiceClient;

/**
 * @author Administrator
 */
public class WriteCardBean extends CSBizBean
{

    /**
     * sim卡单双两位互换公共方法
     * 
     * @param a
     * @return
     */
    public static String replace(String a) throws Exception
    {
        if (a.length() != 20)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "SIM_CARD_NO长度不为20位！");
        }
        char[] chars = a.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if (i % 2 == 0)
            {
                char a1 = chars[i];
                chars[i] = chars[i + 1];
                chars[i + 1] = a1;
            }
        }
        String a2 = String.valueOf(chars);
        return a2;

    }

    public IDataset afterWriteCard(IData input) throws Exception
    {
        // 物联网 net _type_code =07 调用老资源接口
        // String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        String imsi = input.getString("IMSI");
        String emptyCardId = input.getString("EMPTY_CARD_ID");
        String resultCode = input.getString("RESULT_CODE");// 0 成功 1 失败
        // 调用资源接口，写卡信息回传,写卡成功与失败，均需要进行回写
        remoteWriteUpdate(imsi, emptyCardId, simCardNo, "2", resultCode);
        IDataset set = new DatasetList();
        set.add(input);
        return set;
    }

    /**
     * 一卡多号回传
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset afterWriteOCNCard(IData input) throws Exception
    {
        String simCardNo = input.getString("SIM_CARD_NO");
        String simCardNo2 = input.getString("SIM_CARD_NO2");

        String imsi = input.getString("IMSI");
        String imsi2 = input.getString("IMSI2");

        String emptyCardId = input.getString("EMPTY_CARD_ID");
        String resultCode = input.getString("RESULT_CODE");// 0 成功 1 失败
        remoteWriteUpdate(imsi, emptyCardId, imsi2, "", simCardNo, simCardNo2, "2", resultCode);

        IDataset set = new DatasetList();
        set.add(input);
        return set;
    }

    public IDataset beforeWriteCard(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
        String emptyCardId = input.getString("EMPTY_CARD_ID");

        // 校验白卡信息并预占,资源侧在获取个性化资料里一并做了处理
        // 获取SIM卡个性资料
        ResCall.checkEmptycardInfo(emptyCardId, "IDLE");
        IDataset speSimList = getSpeSimInfo(serialNumber, emptyCardId, "2", "2");
        String imsi = speSimList.getData(0).getString("IMSI");
        String simCardNo = speSimList.getData(0).getString("SIM_CARD_NO");
        String encodeStr = "";
        String isNewCard = input.getString("IS_NEW");
        // 写卡数据拼串处理

        // 老卡
        if ("0".equals(isNewCard))
        {
            encodeStr = simCardEncode(speSimList, input);
        }

        // 新卡
        if ("1".equals(isNewCard))
        {
            String tradeId = SeqMgr.getTradeId().substring(6);
            // 调用webservice接口加密
            AssemDynData ass = new AssemDynData();
            EncAssemDynData enAss = new EncAssemDynData();
            List<EncAssemDynData> enAsses = new ArrayList<EncAssemDynData>();
            IssueData issue = new IssueData();
            issue.setIccId(speSimList.getData(0).getString("SIM_CARD_NO", ""));
            issue.setImsi(speSimList.getData(0).getString("IMSI", ""));
            issue.setPin1(speSimList.getData(0).getString("PIN", ""));
            issue.setPin2(speSimList.getData(0).getString("PIN2", "1425"));
            issue.setPuk1(speSimList.getData(0).getString("PUK", ""));
            issue.setPuk2(speSimList.getData(0).getString("PUK2", "56541837"));
            issue.setSmsp("+8613800898500");
            enAss.setMsisdn(serialNumber);
            enAss.setIssueData(issue);
            enAsses.add(enAss);

            ass.setChanelflg("1");
            String cardInfo = "080A" + this.replace(speSimList.getData(0).getString("SIM_CARD_NO", "")) + "0E0A" + emptyCardId;
            ass.setCardInfo(cardInfo);
            ass.setEnc(enAsses);
            ass.setSeqNo(tradeId);
            WebServiceClient client = new WebServiceClient();
            EncAssemDynDataRsp resAssemData = client.encAssemClient(ass);
            encodeStr = resAssemData.getIssueData();
            String result_code = resAssemData.getResultCode();
            if (!"0".equals(result_code))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用现场实时写卡系统失败");
            }
            returnInfo.put("TRADE_ID", tradeId);
        }

        returnInfo.put("ENCODE_STR", encodeStr);
        returnInfo.put("IMSI", imsi);
        returnInfo.put("SIM_CARD_NO", simCardNo);
        IDataset set = new DatasetList();
        set.add(returnInfo);
        return set;
    }

    /**
     * 一卡双号写卡前数据拼装
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset beforeWriteOCNCard(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
        String serialNumberB = input.getString("SERIAL_NUMBERB");

        String emptyCardId = input.getString("EMPTY_CARD_ID");

        // 校验白卡信息并预占,资源侧在获取个性化资料里一并做了处理
        // 获取SIM卡个性资料
        IData speSimListA = getSpeSimInfo(serialNumber, emptyCardId, "2", "2").getData(0);
        IData speSimListB = getSpeSimInfo(serialNumberB, emptyCardId, "2", "2").getData(0);
        IDataset speSimList = new DatasetList();
        speSimList.add(speSimListA);
        speSimList.add(speSimListB);

        String imsiA = speSimListA.getString("IMSI");
        String simCardNoA = speSimListA.getString("SIM_CARD_NO");

        String imsiB = speSimListB.getString("IMSI");
        String simCardNoB = speSimListB.getString("SIM_CARD_NO");

        String encodeStr = "";
        // 写卡数据拼串处理
        encodeStr = simCardEncode(speSimList, input);

        String tradeId = SeqMgr.getTradeId().substring(6);

        returnInfo.put("ENCODE_STR", encodeStr);
        returnInfo.put("IMSI", imsiA);
        returnInfo.put("SIM_CARD_NO", simCardNoA);
        returnInfo.put("IMSI_O", imsiB);
        returnInfo.put("SIM_CARD_NO_O", simCardNoB);

        IDataset set = new DatasetList();
        set.add(returnInfo);
        return set;
    }

    /**
     * 校验版本信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public boolean checkOcxVersion(IData input) throws Exception
    {
        String strOcxVersion = input.getString("OCX_VERSION");
        String strICCID = input.getString("ICCID1");
        IData outParam = getOcxVersion(input);
        String dbVersion = outParam.getString("OCX_VERSION");
        if (null != outParam && !outParam.isEmpty())
        {
            if ("" != dbVersion && !dbVersion.equals(strOcxVersion))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_191, strOcxVersion);
            }
            else if (strICCID.substring(1, 5) == "89860")
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_149);
            }
            else
            {
                input.putAll(outParam);
                return true;
            }
        }
        else
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_150);
        }
        return false;
    }

    public IData checkWriteCard(IData input) throws Exception
    {
        String cardInfo = input.getString("CARD_INFO", "");
        String resultInfo = input.getString("RESULT_INFO", "");
        String tradeId = input.getString("TRADE_ID", "");
        AssemDynData ass = new AssemDynData();
        ass.setSeqNo(tradeId);
        ass.setCardInfo(cardInfo);
        ass.setCardRsp(resultInfo);
        WebServiceClient client = new WebServiceClient();
        EncAssemDynDataRsp resAssemData = client.repCheckClient(ass);
        String resultCode = resAssemData.getResultCode();

        // if(!"0".equals(resultCode)){
        // CSAppException.apperr(CrmCommException.CRM_COMM_103,"写卡验证失败！【"+resAssemData.getResultMessage()+"】！");
        // }
        IData returnData = new DataMap();
        returnData.put("RESULT_CODE", resultCode);
        returnData.put("RESULT_INFO", resAssemData.getResultMessage());
        return returnData;
    }

    /**
     * 获取资源信息
     * 
     * @param strEmptyCardId
     * @param tag
     *            IDLE:查空闲 USE:查在用 不传则先查空闲，如没查到再查在用
     * @return
     * @throws Exception
     */
    /*
     * public IData getEmptyCardInfo(String strEmptyCardId, String tag) throws Exception { // 查询白卡信息 IDataset
     * emptyCardInfo = ResCall.getEmptycardInfo(strEmptyCardId, "", tag); // 获取白卡信息并预占 // IDataset emptyCardInfo =
     * ResCall.getWriteSimCardInfo(input.getString("EMPTY_CARD_ID"), // input.getString("SERIAL_NUMBER"), "1", "",
     * "",""); if (IDataUtil.isNotEmpty(emptyCardInfo)) { return emptyCardInfo.getData(0); } else {
     * CSAppException.apperr(CrmCardException.CRM_CARD_144); return new DataMap(); } }
     */

    /**
     * 释放临时选占资源
     * 
     * @param xGetMode
     *            0-根据指定的RES_NO释放(号、卡)；1-根据指定的员工号释放；2-根据指定的RES_NO+STAFF_ID释放
     * @param resTypeCode
     *            0-号码；1-SIM卡； 3-有价卡；5-票据；6-白卡；9-空卡
     * @param resNo
     *            X_GET_MODE=0、2时必传
     * @param STAFF_ID
     *            X_GET_MODE=1、2时必传
     */
    public void freeResource(String xGetMode, String resTypeCode, String resNo, String staffId) throws Exception
    {
        IDataset resInfos = ResCall.releaseRes(xGetMode, resNo, resTypeCode, staffId);
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "释放临时选占资源错误！");
        }
        if (resInfos.getData(0).getInt("X_RESULTCODE") != 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resInfos.getData(0).getString("X_RESULTINFO"));
        }
    }

    /**
     * 确实是否为新卡
     * 
     * @param input
     * @return
     * @throws Exception
     */
    /*
     * public IData getNewCardInfo(IData input) throws Exception { String isNew = "0"; String tradeTypeCode =
     * input.getString("TRADE_TYPE_CODE", ""); String emptyCardNo = input.getString("EMPTY_CARD_NO",""); String
     * eparchyCode = input.getString("EPARCHY_CODE",""); IDataset dataset = ResCall.getEmptycardInfo(emptyCardNo, "",
     * "0"); IData outParam = (IData) dataset.get(0); if (outParam==null||outParam.getInt("X_RESULTCODE")!= 0) {
     * CSAppException.apperr(CrmCardException.CRM_CARD_258); } String simTypeCode = outParam.getString("SIM_TYPE_CODE");
     * String cardType = outParam.getString("CARD_TYPE"); String cardTypeCode =
     * outParam.getString("SIM_CARD_TYPE_CODE"); IDataset Infos = ParamInfoQry.getSimCardType("CSM", "312", cardType,
     * cardTypeCode, eparchyCode); if(Infos.size()>0){ if("141".equals(tradeTypeCode)){
     * if("1".equals(Infos.getData(0).getString
     * ("PARA_CODE2",""))||"2".equals(Infos.getData(0).getString("PARA_CODE2",""))){
     * CSAppException.apperr(CrmCardException.CRM_CARD_259); } }
     * if("1".equals(Infos.getData(0).getString("PARA_CODE2",""))){ isNew = "1"; } }else{
     * CSAppException.apperr(CrmCardException.CRM_CARD_260, cardType, cardTypeCode); } IData data = new DataMap();
     * data.put("ISNEW", isNew); return data; }
     */

    /**
     * 获取远程写卡参数获取
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getOcxVersion(IData input) throws Exception
    {
        IData resultMap = new DataMap();
        IDataset paramSet = CommparaInfoQry.getCommparaAllCol("CSM", "189", "1", getUserEparchyCode());
        if (IDataUtil.isNotEmpty(paramSet))
        {
            IData tempInfo = (IData) paramSet.get(0);
            resultMap.put("SMSP", tempInfo.getString("PARA_CODE2"));// 短信中心号码
            resultMap.put("OCX_VERSION", tempInfo.getString("PARA_CODE3"));// OCX版本号
            resultMap.put("SEPARATOR", tempInfo.getString("PARA_CODE6"));
            resultMap.put("KI_FROM_PROV", tempInfo.getString("PARA_CODE8"));// 获取是否从省中心获取KI
            resultMap.put("PARA_CODE12", tempInfo.getString("PARA_CODE12"));// 控件更新地址
        }
        else
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_150);
        }
        String oldOcxVersion = input.getString("OCX_VERSION", "");
        String ocxVersion = resultMap.getString("OCX_VERSION", "");
        /*
         * if (!oldOcxVersion.toUpperCase().equals(ocxVersion.toUpperCase())){
         * CSAppException.apperr(CrmCardException.CRM_CARD_251, oldOcxVersion, ocxVersion); }
         */
        return resultMap;
    }

    /**
     * 获取
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRemoteWriteCardUrl(IData input) throws Exception
    {
        IDataset outputSet = new DatasetList();
        String strATR = input.getString("ATR", "");
        String strCardType = input.getString("CARD_TYPE", "");

        IData data = getOcxVersion(input);
        String strUrl = data.getString("PARA_CODE12", "");

        // --获取写卡组件参数信息
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(strUrl);
        method.addParameter("action", "query");
        method.addParameter("ATR", strATR);
        method.addParameter("cardType", strCardType);
        StringBuilder sb = new StringBuilder();
        int status = client.executeMethod(method);
        if (status == HttpStatus.SC_OK)
        {
            InputStreamReader reader = new InputStreamReader(method.getResponseBodyAsStream(), "gb2312"); // 此处编码相当重要，针对中文解析
            int chars = 0;
            while ((chars = reader.read()) != -1)
            {
                sb.append((char) chars);
            }
            reader.close();
        }
        else
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_252, status, strATR, strCardType);
        }
        IData dt = new DataMap(sb.toString());
        String xResultCode = dt.getString("result", "");
        if (!"0".equals(xResultCode))
        {
            String message = dt.getString("message", "");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, message + "result=[" + xResultCode + "]，ATR=[" + strATR + "]，cardType=[" + strCardType + "]！");
        }
        outputSet.add(dt);
        return outputSet;
    }

    /**
     * 获取白卡对应的SIM个性化资料并预占
     * 
     * @param serialNumber
     * @param emptyCardId
     * @param occupyTimeCode
     *            0-选占当天；1-选占30分钟；2-选占24小时。不传默认为0
     * @param remoteMode
     *            1-异地USIM写卡 0-异地SIM写卡 2 本地写卡
     * @return
     * @throws Exception
     */
    public IDataset getSpeSimInfo(String serialNumber, String emptyCardId, String occupyTimeCode, String remoteMode) throws Exception
    {
        String isNp = "0";
        IData uData = MsisdnInfoQry.getCrmMsisonBySerialnumber(serialNumber);
        if(IDataUtil.isEmpty(uData)){
        	uData = MsisdnInfoQry.getCrmMsisonBySerialnumberNew(serialNumber);
        }
        if (IDataUtil.isNotEmpty(uData))
        {
            String asp = uData.getString("ASP", "");
            if (!"1".equals(asp))
            {
            	isNp = "1";
            }
        }
        IDataset simInfos = ResCall.getWriteSimCardInfo(serialNumber, emptyCardId, occupyTimeCode, remoteMode, isNp);
        if (IDataUtil.isNotEmpty(simInfos))
        {
            return simInfos;
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "资源个性化资料选占失败！");
            return null;
        }

    }
    public IData getSimDataInfo(String imsi) throws Exception
    {
        IDataset simInfos = ResCall.getSimDataInfo(imsi);
        if (IDataUtil.isNotEmpty(simInfos))
        {
            return simInfos.getData(0);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询资源个性化资料失败！");
            return null;
        }
    }
    
    public IData getSimCardInfoByImsi(String imsi) throws Exception
    {
        String x_get_mode = "1";
        String sim_card_no = null;
        String qry_tag = "";

        IDataset simInfos = ResCall.getSimCardInfo(x_get_mode,sim_card_no,imsi,qry_tag);
        if (IDataUtil.isNotEmpty(simInfos))
        {
            return simInfos.getData(0);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询资源个性化资料失败！");
            return null;
        }
    }

    /**
     * 根据写卡返回值更新白卡及SIM个性化资料状态
     * 
     * @param remoteMode
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @param xChoiceTag
     *            写卡标识 0-成功；1-失败；不填默认为0
     */
    public IData remoteWriteUpdate(String imsi, String emptyCardId, String simCardNo, String remoteMode, String xChoiceTag) throws Exception
    {
        IDataset resInfos = ResCall.backWriteSimCard(imsi, emptyCardId, "", getTradeEparchyCode(), "", simCardNo, "", "", remoteMode, xChoiceTag);
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
        }
        return resInfos.getData(0);
    }
    
    public IData remoteWriteUpdate(String imsi, String emptyCardId, String simCardNo, String remoteMode, String xChoiceTag,String ki,String opc) throws Exception
    {
        IDataset resInfos = ResCall.backWriteSimCard(imsi, emptyCardId, "", getTradeEparchyCode(), "", simCardNo, "", "", remoteMode, xChoiceTag,ki,opc);
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
        }
        return resInfos.getData(0);
    }

    /**
     * @param imsiA
     *            主卡IMSI
     * @param emptyCardId
     *            白卡卡号
     * @param imsiB
     *            副卡IMSI
     * @param writeTag
     *            写卡标志
     * @param simCardNoA
     *            主卡卡号
     * @param simCardNoB
     *            副卡卡号
     * @param remoteMode
     *            写卡标识 0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @param xChoiceTag
     *            写卡标识 0-成功；1-失败；不填默认为0
     * @throws Exception
     */
    public void remoteWriteUpdate(String imsiA, String emptyCardId, String imsiB, String writeTag, String simCardNoA, String simCardNoB, String remoteMode, String xChoiceTag) throws Exception
    {
        IDataset resInfos = ResCall.backWriteSimCard(imsiA, emptyCardId, imsiB, getTradeEparchyCode(), "", simCardNoA, simCardNoB, "", remoteMode, xChoiceTag);
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
        }
    }

    /**
     * 对写卡信息进行拼串
     * 
     * @param simcards
     * @return
     * @throws Exception
     */
    public String simCardEncode(IDataset simcards, IData input) throws Exception
    {
        StringBuilder encodeBuilder = new StringBuilder();
        String decodeMode = "1";

        String SMSP = simcards.getData(0).getString("SMSP", "");
        String Separator = simcards.getData(0).getString("SEPARATOR", "&");
        String PhoneNum = input.getString("PHONE_NUM", "0");
        String OcxVersion = null;
        String isUsim = input.getString("USIM", "0");

        if (simcards.getData(0).containsKey("OcxVersion"))
            OcxVersion = simcards.getData(0).getString("OcxVersion");

        if (OcxVersion != null && "WatchData V1.0.0".equals(OcxVersion))
        {
            for (int i = 0; i <= simcards.size(); i++)
            {
                encodeBuilder.append(simcards.getData(i).getString("SIM_CARD_NO") + Separator).append(simcards.getData(i).getString("IMSI") + Separator).append(simcards.getData(i).getString("KI") + Separator).append(SMSP + Separator).append(
                        simcards.getData(i).getString("PIN") + Separator).append(simcards.getData(i).getString("PIN2") + Separator).append(simcards.getData(i).getString("PUK") + Separator).append(simcards.getData(i).getString("PUK2") + Separator);
            }
        }
        else
        {
            encodeBuilder.append("DATATYPE=" + Integer.toString(simcards.size()) + Separator);
            for (int i = 1; i <= simcards.size(); i++)
            {
                encodeBuilder.append("ICCID" + i + "=" + simcards.getData(i - 1).getString("SIM_CARD_NO") + Separator).append("IMSI" + i + "=" + simcards.getData(i - 1).getString("IMSI") + Separator).append(
                        "KI" + i + "=" + simcards.getData(i - 1).getString("KI") + Separator).append("SMSP" + i + "=" + SMSP + Separator).append("PIN1" + i + "=" + simcards.getData(i - 1).getString("PIN") + Separator).append(
                        "PUK1" + i + "=" + simcards.getData(i - 1).getString("PUK") + Separator).append("PIN2" + i + "=" + simcards.getData(i - 1).getString("PIN2") + Separator).append(
                        "PUK2" + i + "=" + simcards.getData(i - 1).getString("PUK2") + Separator);
                if ("2".equals(PhoneNum) || "20".equals(PhoneNum) || "1".equals(isUsim))
                {
                    encodeBuilder.append("OPC" + i + "=" + simcards.getData(i - 1).getString("OPC") + Separator);
                }
                if (simcards.size() == i - 1)
                {
                    encodeBuilder.append("DECODEMODE" + i + "=" + decodeMode);
                }
                else
                {
                    encodeBuilder.append("DECODEMODE" + i + "=" + decodeMode + Separator);
                }
            }
        }

        return encodeBuilder.toString();
    }

}
