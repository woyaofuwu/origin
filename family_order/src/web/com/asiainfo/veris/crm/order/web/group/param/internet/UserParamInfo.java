
package com.asiainfo.veris.crm.order.web.group.param.internet;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupEsopUtilView;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserParamInfo extends IProductParamDynamic
{

    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        String productNo = parainfo.getString("PRODUCT_ID");

        // 调用后台服务查专线名称
        IDataset dataLineInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "SP_LINE", "ZZZZ");

        parainfo.put("DATALINE_INFO", dataLineInfo);
        parainfo.put("NOTIN_METHOD_NAME", "ChgUs");

        String userId = data.getString("USER_ID");
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        userInParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSViewCall.call(bp, "CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = (IData) userInfo.get(0);
            parainfo.put("NOTIN_DETMANAGER_INFO", userData.get("RSRV_STR7"));
            parainfo.put("NOTIN_DETMANAGER_PHONE", userData.get("RSRV_STR8"));
            parainfo.put("NOTIN_DETADDRESS", userData.get("RSRV_STR9"));
            parainfo.put("NOTIN_PROJECT_NAME", userData.get("RSRV_STR10"));

        }

        // 查询专线信息
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "N001");
        IDataset userAttrInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

        // 从EOSP获取专线数据
        String eosStr = data.getString("EOS");
        IData eosList = new DataMap();
        String bossModify = ""; //产品新增的时候没选择新增产品。
        IData resultDataset = new DataMap();
        if (StringUtils.isNotEmpty(eosStr) && !"{}".equals(eosStr))
        {
            IDataset eos = new DatasetList(eosStr);
            eosList = eos.getData(0);
            bossModify =eosList.getString("NODE_ID");
            IDataset dataset = new DatasetList();
            IData inputParam = new DataMap();
            inputParam.put("NODE_ID", eosList.getString("NODE_ID", ""));
            inputParam.put("IBSYSID", eosList.getString("IBSYSID", ""));
            inputParam.put("SUB_IBSYSID", eosList.getString("SUB_IBSYSID", ""));
            inputParam.put("PRODUCT_ID", eosList.getString("PRODUCT_ID"));
            dataset.add(inputParam);
            logger.error("调用ESOP接口：----------------" + inputParam.toString());

            resultDataset = GroupEsopUtilView.getEsopData(bp, dataset);
        } else {
            parainfo.put("NOTIN_CHANGE_DISABLED", "true");//从boss界面进入,界面上的专线价格、安装调试费、一次性通信服务费编辑设置不可编辑
        }

        if (null != resultDataset && resultDataset.size() > 0)
        {
            IDataset pageDataline = new DatasetList();

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
                            IDataset userDataline = CSViewCall.call(bp, "CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
                            if(IDataUtil.isNotEmpty(userDataline)){
                                String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
                                userData.put("pam_LINE_TRADE_NAME", tradeName);
                            } else {
                                userData.put("pam_LINE_TRADE_NAME", "");
                            }
                            
                            userData.put("pam_NOTIN_IP_PRICE", userAttrData.get("RSRV_STR6"));
                            userData.put("pam_NOTIN_PRODUCT_NUMBER", userAttrData.get("RSRV_STR7"));
                            userData.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));
                            //add by chenzg@20180620
                            userData.put("pam_NOTIN_SOFTWARE_PRICE", userAttrData.get("RSRV_STR8"));
                            userData.put("pam_NOTIN_NET_PRICE", userAttrData.get("RSRV_STR10"));


                            if("bossModify".equals(bossModify)){
                                //通过合同编码，查询合同的专线信息。
                                IData contractParam = new DataMap();
                                contractParam.put("CONTRACT_ID", data.getString("CONTRACT_ID"));
                                contractParam.put("PRODUCT_ID", productNo);
                                contractParam.put("CUST_ID", data.getString("CUST_ID"));
                                contractParam.put("LINE_NO", userAttrData.get("RSRV_STR7"));
                                IDataset contratSet = CSViewCall.call(bp, "CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                                if (IDataUtil.isNotEmpty(contratSet)){
                                    IData tempLine = contratSet.getData(0);
//                                    userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                                    userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                                    userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                                    userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                                    userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                                    userData.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                                    
                                    //add by chenzg@20180620
                                    userData.put("pam_NOTIN_SOFTWARE_PRICE", tempLine.getString("RSRV_STR11","0"));	//软件应用服务费（元）
                                    userData.put("pam_NOTIN_NET_PRICE", tempLine.getString("RSRV_STR12","0"));	//网络技术支持服务费（元）
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
                    IDataset maxLineNumer = CSViewCall.call(bp, "SS.BookTradeSVC.getMaxLineNumberByUserId",lineNum);
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
                        
                        //add by chenzg@20180620
                        user.put("pam_NOTIN_SOFTWARE_PRICE", "0");
                        user.put("pam_NOTIN_NET_PRICE", "0");
                        
                        if("bossModify".equals(bossModify)){
                            //通过合同编码，查询合同的专线信息。
                            IData contractParam = new DataMap();
                            contractParam.put("CONTRACT_ID", data.getString("CONTRACT_ID"));
                            contractParam.put("PRODUCT_ID", productNo);
                            contractParam.put("CUST_ID", data.getString("CUST_ID"));
                            contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
                            IDataset contratSet = CSViewCall.call(bp, "CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                            if (IDataUtil.isNotEmpty(contratSet)){
                                IData tempLine = contratSet.getData(0);
//                                user.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                                user.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                                user.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                                user.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                                user.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                                user.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                                //add by chenzg@20180620
                                user.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//IP地址使用费
                                user.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//IP地址使用费
                            }
                        }

                        pageDataline.add(user);
                    }
                }
            }

            parainfo.put("VISP_INFO", pageDataline);
            parainfo.put("NOTIN_AttrInternet", pageDataline);
            parainfo.put("NOTIN_OLD_AttrInternet", pageDataline);
            parainfo.put("NOTIN_DATALINE_DATA", datalineData);
            parainfo.put("NOTIN_COMMON_DATA", commonData);

        }
        else
        {
            // ESOP无数据从CRM取
            if (null != userAttrInfo && userAttrInfo.size() > 0)
            {
                IDataset dataset = new DatasetList();
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
                    IDataset userDataline = CSViewCall.call(bp, "CS.TradeDataLineAttrInfoQrySVC.qryUserDatalineByProductNO", paramIn);
                    if(IDataUtil.isNotEmpty(userDataline)){
                        String tradeName = userDataline.getData(0).getString("RSRV_STR5","");
                        userData.put("pam_LINE_TRADE_NAME", tradeName);
                    } else {
                        userData.put("pam_LINE_TRADE_NAME", "");
                    }
                    
                    userData.put("pam_NOTIN_IP_PRICE", userAttrData.get("RSRV_STR6"));
                    userData.put("pam_NOTIN_PRODUCT_NUMBER", userAttrData.get("RSRV_STR7"));
                    userData.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));
                    
                    //add by chenzg@20180620
                    userData.put("pam_NOTIN_SOFTWARE_PRICE", userAttrData.get("RSRV_STR8"));
                    userData.put("pam_NOTIN_NET_PRICE", userAttrData.get("RSRV_STR10"));

                    if("bossModify".equals(bossModify)){
                        //通过合同编码，查询合同的专线信息。
                        IData contractParam = new DataMap();
                        contractParam.put("CONTRACT_ID", data.getString("CONTRACT_ID"));
                        contractParam.put("PRODUCT_ID", productNo);
                        contractParam.put("CUST_ID", data.getString("CUST_ID"));
                        contractParam.put("LINE_NO", userAttrData.get("RSRV_STR7"));
                        IDataset contratSet = CSViewCall.call(bp, "CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                        if (IDataUtil.isNotEmpty(contratSet)){
                            IData tempLine = contratSet.getData(0);
//                            userData.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                            userData.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                            userData.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                            userData.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                            userData.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                            userData.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                          //add by chenzg@20180620
                            userData.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//软件应用服务费（元）
                            userData.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//网络技术支持服务费（元）
                        }
                    }


                    dataset.add(userData);
                }
                parainfo.put("VISP_INFO", dataset);
                parainfo.put("NOTIN_AttrInternet", dataset);
            }
        }
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {

        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        String productNo = parainfo.getString("PRODUCT_ID");

        // 调用后台服务查专线名称
        IDataset dataLineInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "SP_LINE", "ZZZZ");
        parainfo.put("DATALINE_INFO", dataLineInfo);


        // 从EOSP获取专线数据，如果没有数据则认为不是从ESOP发起
        IDataset eos = data.getDataset("EOS");
        IData eosList = new DataMap();
        IData resultDataset = new DataMap();
        IDataset dataset = new DatasetList();
        if (null != eos && eos.size() > 0)
        {
            eosList = eos.getData(0);
            IData inputParam = new DataMap();
            inputParam.put("NODE_ID", eosList.getString("NODE_ID", ""));
            inputParam.put("IBSYSID", eosList.getString("IBSYSID", ""));
            inputParam.put("SUB_IBSYSID", eosList.getString("SUB_IBSYSID", ""));
            inputParam.put("PRODUCT_ID", eosList.getString("PRODUCT_ID"));
            dataset.add(inputParam);
            logger.error("调用ESOP接口：----------------" + inputParam.toString());

            resultDataset = GroupEsopUtilView.getEsopData(bp, dataset);
        }
        else
        {
            CSViewException.apperr(GrpException.CRM_GRP_838);
        }

        if (null != resultDataset && resultDataset.size() > 0)
        {
            IDataset commonData = resultDataset.getDataset("COMMON_DATA");
            IDataset datalineData = resultDataset.getDataset("DLINE_DATA");

            if (null != datalineData && datalineData.size() > 0)
            {
                for (int i = 0; i < datalineData.size(); i++)
                {
                    IData dataline = datalineData.getData(i);
                    int number = i + 1;
                    dataline.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i));
                    dataline.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number));
                    dataline.put("pam_NOTIN_LINE_BROADBAND", dataline.get("BANDWIDTH"));
                    dataline.put("pam_NOTIN_PRODUCT_NUMBER", dataline.get("PRODUCTNO"));
                    dataline.put("pam_NOTIN_LINE_INSTANCENUMBER", dataline.get("PRODUCTNO"));

                    //专线价格
                    dataline.put("pam_NOTIN_LINE_PRICE", "0");
                    //安装调试费
                    dataline.put("pam_NOTIN_INSTALLATION_COST", "0");
                    //一次性通信服务费
                    dataline.put("pam_NOTIN_ONE_COST", "0");
                    //IP地址使用费
                    dataline.put("pam_NOTIN_IP_PRICE", "0");
                    //软件应用服务费（元）
                    dataline.put("pam_NOTIN_SOFTWARE_PRICE", "0");
                    //网络技术支持服务费（元）
                    dataline.put("pam_NOTIN_NET_PRICE", "0");

                    dataline.put("pam_LINE_TRADE_NAME", dataline.getString("TRADENAME","")); 
                    
                    //通过合同编码，查询合同的专线信息。
                    IData contractParam = new DataMap();
                    contractParam.put("CONTRACT_ID", data.getString("CONTRACT_ID"));
                    contractParam.put("PRODUCT_ID", productNo);
                    contractParam.put("CUST_ID", data.getString("CUST_ID"));
                    contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
                    IDataset contratSet = CSViewCall.call(bp, "CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                    if (IDataUtil.isNotEmpty(contratSet)){
                        IData tempLine = contratSet.getData(0);
//                        dataline.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                        dataline.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                        dataline.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                        dataline.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                        dataline.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                        dataline.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                        
                        //add by chenzg@20180620 REQ201805140023关于新增集团信息化项目收入界面的申请
                        dataline.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//软件应用服务费（元）
                        dataline.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//网络技术支持服务费（元）
                    }
                }
            }

            parainfo.put("VISP_INFO", datalineData);
            parainfo.put("NOTIN_DATALINE_DATA", datalineData);
            parainfo.put("NOTIN_COMMON_DATA", commonData);
        }

        result.put("PARAM_INFO", parainfo);

        return result;
    }
}
