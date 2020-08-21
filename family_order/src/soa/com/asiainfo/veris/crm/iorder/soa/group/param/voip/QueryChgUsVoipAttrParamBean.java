package com.asiainfo.veris.crm.iorder.soa.group.param.voip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

import net.sf.json.JSONArray;

public class QueryChgUsVoipAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryVoipParamAttrForChgInit(IData param) throws Exception
    {
    	IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String custId = param.getString("CUST_ID");
        String userId = param.getString("USER_ID");
        
        IDataset dataset = UserAttrInfoQry.getUserProductAttrByUTForGrp(userId, "P", null);
        IData attrItemA = new DataMap();
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData user_attrida = (IData) dataset.get(i);
                String byFeeParam = user_attrida.getString("ATTR_CODE");
                if (!"".equals(byFeeParam) && byFeeParam.length() > 3 && byFeeParam.substring(0, 3).equals("FEE"))
                {
                    String serParamStr = user_attrida.getString("ATTR_VALUE", "0");
                    user_attrida.put("ATTR_VALUE", Integer.parseInt(serParamStr) / 100);
                    dataset.set(i, user_attrida);
                }

            }
        }
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItemA, pzAttrItem);
        }
       
        //IDataset userInfo = UserVpnInfoQry.qryUserVpnByUserId(userId);
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        userInParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSAppCall.call("CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
        IData userVoipData =new DataMap();
        
        if(IDataUtil.isNotEmpty(userInfo)){
        	userVoipData = userInfo.getData(0);
            
        	userVoipData.put("NOTIN_DETMANAGER_INFO", userVoipData.get("RSRV_STR7"));
        	userVoipData.put("NOTIN_DETMANAGER_PHONE", userVoipData.get("RSRV_STR8"));
        	userVoipData.put("NOTIN_DETADDRESS", userVoipData.get("RSRV_STR9"));
            userVoipData.put("NOTIN_PROJECT_NAME", userVoipData.get("RSRV_STR10"));
        }
        if (IDataUtil.isEmpty(userVoipData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_130, param.getString("USER_ID"));
        }
        
        
        userVoipData.put("NOTIN_METHOD_NAME", "ChgUs");
        
        
     	// 查询专线信息
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "N001");
        IDataset userAttrInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

        // 中继信息查询
        IData inparmeZj = new DataMap();
        inparmeZj.put("USER_ID", userId);
        inparmeZj.put("RSRV_VALUE_CODE", "VOIP");
        IDataset userAttrInfoZj = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparmeZj);

        // 从EOSP获取专线数据
        String eosStr = param.getString("EOS");
        IData eosList = new DataMap();
        String bossModify = ""; //产品新增的时候没选择新增产品。
        IData resultDataset = new DataMap();
        if (StringUtils.isNotEmpty(eosStr) && !"{}".equals(eosStr))
        {
            IDataset eos = new DatasetList(eosStr);
            eosList = eos.getData(0);
            bossModify =eosList.getString("NODE_ID");
            IDataset eosDataset = new DatasetList();
            IData inputParam = new DataMap();
            inputParam.put("NODE_ID", eosList.getString("NODE_ID", ""));
            inputParam.put("IBSYSID", eosList.getString("IBSYSID", ""));
            inputParam.put("SUB_IBSYSID", eosList.getString("SUB_IBSYSID", ""));
            inputParam.put("PRODUCT_ID", eosList.getString("PRODUCT_ID"));
            eosDataset.add(inputParam);

            resultDataset = getEsopData(eosDataset);

        } else {
        	userVoipData.put("NOTIN_CHANGE_DISABLED", "true");//从boss界面进入,界面上的专线价格、安装调试费、一次性通信服务费编辑设置不可编辑
        }

        if (null != resultDataset && resultDataset.size() > 0)
        {
            IDataset pageDataline = new DatasetList();
            IDataset pageDataZJ = new DatasetList();

            IDataset commonData = resultDataset.getDataset("COMMON_DATA");
            IDataset datalineData = resultDataset.getDataset("DLINE_DATA");

            boolean flag = true;
            for (int i = 0; i < commonData.size(); i++)
            {
                IData sheetType = commonData.getData(i);
                if("SHEETTYPE".equals(sheetType.getString("ATTR_CODE")) && "32".equals(sheetType.getString("ATTR_VALUE"))){
                    flag = false;
                }
            }
            // 专线信息
            if (null != datalineData && datalineData.size() > 0 && null != userAttrInfo && userAttrInfo.size() > 0 && flag)
            {

                for (int i = 0; i < datalineData.size(); i++)
                {
                    IData esopdataline = datalineData.getData(i);

                    for (int j = 0; j < userAttrInfo.size(); j++)
                    {
                        IData userAttrData = userAttrInfo.getData(j);

                        if (esopdataline.getString("PRODUCTNO").equals(userAttrData.getString("RSRV_STR7")))
                        {
                            IData userData = new DataMap();

                            String numberCode = (String) userAttrData.get("RSRV_VALUE");
                            userData.put("pam_NOTIN_LINE_NUMBER_CODE", Integer.valueOf(numberCode) - 1);
                            userData.put("pam_NOTIN_LINE_NUMBER", userAttrData.get("RSRV_STR1"));
                            userData.put("pam_NOTIN_LINE_BROADBAND", esopdataline.get("BANDWIDTH"));
                            userData.put("pam_NOTIN_LINE_PRICE", userAttrData.get("RSRV_STR3"));

                            // 安装调试费、一次性费用START_DATE不在当月显示为0
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date nowDate = new Date();
                            String startDate4 = userAttrData.getString("START_DATE");
                            int nowYear = nowDate.getYear();
                            int nowMonth = nowDate.getMonth();
                            int startYear = sdf.parse(startDate4).getYear();
                            int startMonth = sdf.parse(startDate4).getMonth();

                            if (nowMonth == startMonth && nowYear == startYear){
                                userData.put("pam_NOTIN_INSTALLATION_COST", userAttrData.get("RSRV_STR4"));
                                userData.put("pam_NOTIN_ONE_COST", userAttrData.get("RSRV_STR5"));

                            }else{
                                userData.put("pam_NOTIN_INSTALLATION_COST", "0");
                                userData.put("pam_NOTIN_ONE_COST", "0");
                            }

                            String productNo2 = userAttrData.getString("RSRV_STR7","");
                            IData paramIn = new DataMap();
                            paramIn.put("USER_ID", userId);
                            paramIn.put("PRODUCT_NO", productNo2);
                            IDataset userDataline = CSAppCall.call("CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
                            if(IDataUtil.isNotEmpty(userDataline)){
                                String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
                                userData.put("pam_LINE_TRADE_NAME", tradeName);
                            } else {
                                userData.put("pam_LINE_TRADE_NAME", "");
                            }
                            
                            userData.put("pam_NOTIN_PRODUCT_NUMBER", userAttrData.get("RSRV_STR7"));
                            userData.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));

                            if ("bossModify".equals(bossModify)){
                                //通过合同编码，查询合同的专线信息。
                                IData contractParam = new DataMap();
                                contractParam.put("CONTRACT_ID", param.getString("CONTRACT_ID"));
                                contractParam.put("PRODUCT_ID", offerCode);
                                contractParam.put("CUST_ID", param.getString("CUST_ID"));
                                contractParam.put("LINE_NO", userAttrData.get("RSRV_STR7"));
                                IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                                if (IDataUtil.isNotEmpty(contratSet)){
                                    IData tempLine = contratSet.getData(0);
//                                    userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                                    userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                                    userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                                    userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                                    userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                                }
                            }


                            pageDataline.add(userData);
                        }
                    }
                }
            }else{
                //如果SHEETTYPE=32新增专线
                if(null != datalineData && datalineData.size() > 0){
                    IData lineNum = new DataMap();
                    int linenumber = 0;
                    lineNum.put("USER_ID", userId);
                    IDataset maxLineNumer = CSAppCall.call("SS.BookTradeSVC.getMaxLineNumberByUserId",lineNum);
                    if(null != maxLineNumer && maxLineNumer.size() >0 ){
                        IData line = maxLineNumer.getData(0);
                        linenumber = Integer.valueOf(line.getString("LINE_NUMBER_CODE"));
                    }

                    for (int i = 0; i < datalineData.size(); i++)
                    {
                        IData dataline = datalineData.getData(i);
                        IData user = new DataMap();
                        linenumber = linenumber + 1;
                        user.put("pam_NOTIN_LINE_NUMBER_CODE", linenumber-1);
                        user.put("pam_NOTIN_LINE_NUMBER", "专线" + linenumber);
                        user.put("pam_NOTIN_LINE_BROADBAND", dataline.getString("BANDWIDTH"));
                        user.put("pam_NOTIN_LINE_PRICE", "0");
                        user.put("pam_NOTIN_INSTALLATION_COST", "0");
                        user.put("pam_NOTIN_ONE_COST", "0");
                        user.put("pam_NOTIN_IP_PRICE", "0");
                        user.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("PRODUCTNO"));
                        user.put("pam_NOTIN_LINE_INSTANCENUMBER", dataline.getString("PRODUCTNO"));

                        user.put("pam_LINE_TRADE_NAME", dataline.getString("TRADENAME",""));
                        
                        if ("bossModify".equals(bossModify)){
                            //通过合同编码，查询合同的专线信息。
                            IData contractParam = new DataMap();
                            contractParam.put("CONTRACT_ID", param.getString("CONTRACT_ID"));
                            contractParam.put("PRODUCT_ID", offerCode);
                            contractParam.put("CUST_ID", param.getString("CUST_ID"));
                            contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
                            IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                            if (IDataUtil.isNotEmpty(contratSet)){
                                IData tempLine = contratSet.getData(0);
//                                user.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                                user.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                                user.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                                user.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                                user.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                            }
                        }

                        pageDataline.add(user);
                    }
                }
            }

            // 中继信息
            if (null != userAttrInfoZj && userAttrInfoZj.size() > 0)
            {

                for (int i = 0; i < userAttrInfoZj.size(); i++)
                {
                    IData dataInfo = userAttrInfoZj.getData(i);
                    dataInfo.put("pam_NOTIN_ZJ_NUMBER", dataInfo.getString("RSRV_VALUE"));
                    dataInfo.put("pam_NOTIN_ZJ_TYPE", dataInfo.getString("RSRV_STR1"));
                    dataInfo.put("pam_NOTIN_SUPER_NUMBER", dataInfo.getString("RSRV_STR2"));
                    dataInfo.put("pam_NOTIN_TYPE_NAME", dataInfo.getString("RSRV_STR3"));

                    pageDataZJ.add(dataInfo);
                }
            }

            userVoipData.put("VISP_INFO", JSONArray.fromObject(pageDataline).toString().replaceAll("\"", "\'"));
            userVoipData.put("VISP_INFO_ZJ", JSONArray.fromObject(pageDataZJ).toString().replaceAll("\"", "\'"));
            userVoipData.put("NOTIN_AttrInternet", JSONArray.fromObject(pageDataline).toString().replaceAll("\"", "\'"));
            userVoipData.put("NOTIN_DATALINE_DATA", JSONArray.fromObject(datalineData).toString().replaceAll("\"", "\'"));
            userVoipData.put("NOTIN_COMMON_DATA", JSONArray.fromObject(commonData).toString().replaceAll("\"", "\'"));

        }
        else
        {
            // ESOP无数据从CRM取
            IDataset datasetTemp = new DatasetList();
            IDataset datasetzj = new DatasetList();

            // 专线信息
            if (null != userAttrInfo && userAttrInfo.size() > 0)
            {
                for (int i = 0; i < userAttrInfo.size(); i++)
                {
                    IData userAttrData = userAttrInfo.getData(i);
                    IData userData = new DataMap();
                    String numberCode = (String) userAttrData.get("RSRV_VALUE");
                    userData.put("pam_NOTIN_LINE_NUMBER_CODE", Integer.valueOf(numberCode) - 1);
                    userData.put("pam_NOTIN_LINE_NUMBER", userAttrData.get("RSRV_STR1"));
                    userData.put("pam_NOTIN_LINE_BROADBAND", userAttrData.get("RSRV_STR2"));
                    userData.put("pam_NOTIN_LINE_PRICE", userAttrData.get("RSRV_STR3"));

                    // 安装调试费、一次性费用START_DATE不在当月显示为0
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date nowDate = new Date();
                    String startDate4 = userAttrData.getString("START_DATE");
                    int nowYear = nowDate.getYear();
                    int nowMonth = nowDate.getMonth();
                    int startYear = sdf.parse(startDate4).getYear();
                    int startMonth = sdf.parse(startDate4).getMonth();

                    if (nowMonth == startMonth && nowYear == startYear){
                        userData.put("pam_NOTIN_INSTALLATION_COST", userAttrData.get("RSRV_STR4"));
                        userData.put("pam_NOTIN_ONE_COST", userAttrData.get("RSRV_STR5"));

                    }else{
                        userData.put("pam_NOTIN_INSTALLATION_COST", "0");
                        userData.put("pam_NOTIN_ONE_COST", "0");
                    }

                    String productNo2 = userAttrData.getString("RSRV_STR7","");
                    IData paramIn = new DataMap();
                    paramIn.put("USER_ID", userId);
                    paramIn.put("PRODUCT_NO", productNo2);
                    IDataset userDataline = CSAppCall.call("CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
                    if(IDataUtil.isNotEmpty(userDataline)){
                        String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
                        userData.put("pam_LINE_TRADE_NAME", tradeName);
                    } else {
                        userData.put("pam_LINE_TRADE_NAME", "");
                    }
                    
                    userData.put("pam_NOTIN_PRODUCT_NUMBER", userAttrData.get("RSRV_STR7"));
                    userData.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));

                    if ("bossModify".equals(bossModify)){
                        //通过合同编码，查询合同的专线信息。
                        IData contractParam = new DataMap();
                        contractParam.put("CONTRACT_ID", param.getString("CONTRACT_ID"));
                        contractParam.put("PRODUCT_ID", offerCode);
                        contractParam.put("CUST_ID", param.getString("CUST_ID"));
                        contractParam.put("LINE_NO", userAttrData.get("RSRV_STR7"));
                        IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                        if (IDataUtil.isNotEmpty(contratSet)){
                            IData tempLine = contratSet.getData(0);
//                            userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                            userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                            userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                            userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                            userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                        }
                    }



                    datasetTemp.add(userData);
                }
            }

            // 中继信息
            if (null != userAttrInfoZj && userAttrInfoZj.size() > 0)
            {

                for (int i = 0; i < userAttrInfoZj.size(); i++)
                {
                    IData dataInfo = userAttrInfoZj.getData(i);
                    dataInfo.put("pam_NOTIN_ZJ_NUMBER", dataInfo.getString("RSRV_VALUE"));
                    dataInfo.put("pam_NOTIN_ZJ_TYPE", dataInfo.getString("RSRV_STR1"));
                    dataInfo.put("pam_NOTIN_SUPER_NUMBER", dataInfo.getString("RSRV_STR2"));
                    dataInfo.put("pam_NOTIN_TYPE_NAME", dataInfo.getString("RSRV_STR3"));

                    datasetzj.add(dataInfo);
                }
            }

            userVoipData.put("VISP_INFO", JSONArray.fromObject(datasetTemp).toString().replaceAll("\"", "\'"));
            userVoipData.put("VISP_INFO_ZJ", JSONArray.fromObject(datasetzj).toString().replaceAll("\"", "\'"));
            userVoipData.put("NOTIN_AttrInternet", JSONArray.fromObject(datasetTemp).toString().replaceAll("\"", "\'"));
        }
        
        
        
        IData userAttrItem = IDataUtil.iDataA2iDataB(userVoipData, "ATTR_VALUE");
        transComboBoxValue(userAttrItem,attrItemA);
        
        if (IDataUtil.isNotEmpty(userAttrItem))
        {
            Set<String> propNames = userAttrItem.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = userAttrItem.getData(key);
                IData attrItem = new DataMap();
                if(IDataUtil.isEmpty(attrCodeInfo))
                  continue;
                if ("VISION_NUMBER".equals(key))
                {
                    IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    attrItem.put("ATTR_VALUE", attrItemValue);
                    result.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    if(StringUtils.isBlank(attrItemValue))
                       continue;
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem);
                    
                }
                
               
            }
        }
        
        
        return result;
    }
    
    public static void transComboBoxValue(IData userAttrItem, IData pzAttrItem) throws Exception
    {
        if (IDataUtil.isEmpty(pzAttrItem))
        {
            return;
        }
        else if (IDataUtil.isEmpty(userAttrItem)) 
        {
            userAttrItem.putAll(pzAttrItem);
        }
        else 
        {
            for (Iterator iterator = pzAttrItem.keySet().iterator(); iterator.hasNext();)
            {
                String datakey = (String) iterator.next();
                IData tempData = userAttrItem.getData(datakey);
                IData tempData2 = pzAttrItem.getData(datakey);
                if (IDataUtil.isEmpty(tempData))
                {
                    tempData = tempData2;
                    userAttrItem.put(datakey, tempData);
                }
                else if (IDataUtil.isNotEmpty(tempData2))
                {
                    tempData.put("DATA_VAL", tempData2.getDataset("DATA_VAL"));
                }
            }
        }
    }
    
    /**
     * 从ESOP获取专线信息
     * 
     * @author liujy
     * @param bc
     * @param param
     * @return
     * @throws Exception
     */
    public static IData getEsopData(IDataset eosDataset) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData eosData = eosDataset.getData(0);
        IData resultDataset = new DataMap();

        IData inputParam = new DataMap();
        inputParam.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
        inputParam.put("X_SUBTRANS_CODE", "GetEosInfo");
        inputParam.put("NODE_ID", eosData.getString("NODE_ID", ""));
        inputParam.put("IBSYSID", eosData.getString("IBSYSID", ""));
        inputParam.put("SUB_IBSYSID", eosData.getString("SUB_IBSYSID", ""));
        inputParam.put("PRODUCT_ID", eosData.getString("PRODUCT_ID"));
        inputParam.put("OPER_CODE", "14");

        IDataset httResultSetDataset = CSAppCall.call("SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inputParam);

        if (null != httResultSetDataset && httResultSetDataset.size() > 0)
        {
            IData dataLine = httResultSetDataset.getData(0);
            IData data = dataLine.getData("DLINE_DATA");
            if (null != data && data.size() > 0)
            {
                resultDataset = mergeData(dataset, httResultSetDataset);
            }
        }

        return resultDataset;
    }
    
    /**
     * 解析专线数据
     * 
     * @param dataset
     * @param httpResult
     * @return
     * @throws Exception
     */
    protected static IData mergeData(IDataset dataset, IDataset httpResult) throws Exception
    {
        IData resultData = new DataMap();
        IDataset comData = new DatasetList();
        IDataset dataLineAttr = new DatasetList();

        if (null != httpResult && httpResult.size() > 0)
        {
            IData dataLine = httpResult.getData(0);
            if (null != dataLine && dataLine.size() > 0)
            {
                IData totalData = dataLine.getData("DLINE_DATA");
                IData commonData = totalData.getData("COMM_DATA_MAP");
                IDataset lineDataList = totalData.getDataset("LINE_DATA_LIST");

                // 公共数据
                for (int i = 0; i < commonData.size(); i++)
                {
                    IData attrValue = new DataMap();
                    String attr[] = commonData.getNames();
                    attrValue.put("ATTR_CODE", attr[i]);
                    attrValue.put("ATTR_VALUE", commonData.getString(attr[i]));
                    comData.add(attrValue);
                }

                // 专线数据
                for (int j = 0; j < lineDataList.size(); j++)
                {
                    IData data = lineDataList.getData(j);
                    IData attrValue = new DataMap();
                    for (int k = 0; k < data.size(); k++)
                    {
                        String attr[] = data.getNames();
                        attrValue.put(attr[k], data.getString(attr[k]));
                    }
                    dataLineAttr.add(attrValue);
                }
            }
        }

        resultData.put("COMMON_DATA", comData);
        resultData.put("DLINE_DATA", dataLineAttr);

        return resultData;
    }
    
}
