package com.asiainfo.veris.crm.order.soa.group.esp;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.SubscribeViewInfoBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class CreateDatalineEspSVC extends GroupOrderService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String dataLineSvc = "952";

    private static transient Logger logger = Logger.getLogger(CreateDatalineEspSVC.class);
    
    public IDataset createDatalineOrder(IData map) throws Exception
    {
        // 从EOSP获取专线数据
        IData resultDataset = new DataMap();
       
        IDataset dataset = new DatasetList();
        IData inputParam = new DataMap();
        
        //esop入参
        String ibsysId = map.getString("IBSYSID");
        if(StringUtils.isBlank(ibsysId)){
            ibsysId = map.getString("BI_SN");
            map.put("IBSYSID", ibsysId);
        }
        String recordNum = map.getString("RECORD_NUM");
        String nodeId = map.getString("NODE_ID");
        //公共参数
        String serial_number = "";
        String custId = "";
        String groupId = "";
        String userId = "";
        String mebProductId = "";
        Boolean chageFlag = false;
        IData user = new DataMap();
        
        IData param = new DataMap();
        param.put("IBSYSID", ibsysId);
        IDataset groupInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(groupInfos))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
        }
        String productId = groupInfos.first().getString("PRODUCT_ID");
        if("7010".equals(productId)){
            IDataset esopData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
            if(DataUtils.isEmpty(esopData))
            {
                 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询流程信息错误!");
            }
            IData groupInfo = esopData.first();
            groupId = groupInfo.getString("GROUP_ID");
            if(StringUtils.isNotBlank(groupInfos.first().getString("SERIAL_NUMBER"))){
                chageFlag = true;
                serial_number =  groupInfos.first().getString("SERIAL_NUMBER");
                user = UcaInfoQry.qryUserInfoBySn(serial_number);
                userId = user.getString("USER_ID");
            }else{
                user = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                serial_number = createSerialNumber(groupId,productId,user.getString("EPARCHY_CODE"));
            }
            custId =  user.getString("CUST_ID");
        }else{
             //通过ibSysid查询集团userId
            IData groupInfo = new DataMap();
            
            groupInfo = groupInfos.first();
            userId = groupInfo.getString("USER_ID");//集团用户user_id
            serial_number = groupInfo.getString("SERIAL_NUMBER"); //集团服务号码
            
            if(StringUtils.isEmpty(userId)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号["+ibsysId+"]查询TF_B_EOP_PRODUCT表USER_ID无数据！");
            }
            user = UcaInfoQry.qryGrpInfoByUserId(userId);
            custId = user.getString("CUST_ID"); //集团cust_id
            groupId = user.getString("GROUP_ID");  //集团编码

            param.put("RECORD_NUM", recordNum);
            IDataset mebInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT_SUB", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
            if(DataUtils.isEmpty(mebInfos))
            {
                 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询产品信息无数据！");
            }
            mebProductId = mebInfos.first().getString("PRODUCT_ID");
        }
       
        
        
        inputParam.put("IBSYSID", ibsysId);
        inputParam.put("PRODUCT_ID", productId);
        inputParam.put("RECORD_NUM", recordNum);//测试用1，lineNumber
        inputParam.put("NODE_ID", nodeId);
        IDataset httResultSetDataset =DatalineEspUtil.getDataLineInfo(inputParam);//查询esop数据
     
         //获取esop信息
        // logger.error("调用ESOP接口：----------------" + inputParam.toString());
        // IDataset httResultSetDataset  = CSAppCall.call("SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inputParam);
         logger.error("RESULT_FRO_ESOP:" + httResultSetDataset.toString());

         if (null != httResultSetDataset && httResultSetDataset.size() > 0)
         {
             IData dataLine = httResultSetDataset.getData(0);
             IData data = dataLine.getData("DLINE_DATA");
             if (null != data && data.size() > 0)
             {
                 resultDataset = mergeData(dataset, httResultSetDataset);
             }
         }
        
      //  String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(productId);//根据productId查询集团成员产品
        
        //查询已有合同
        //String contractId = "2018112000119345";//测试用 map.getString("CONTRACT_ID")
        String contractId = "";//根据客管接口查询
        IDataset contracts = getcontractsInfo(resultDataset,custId,productId);
        if(IDataUtil.isNotEmpty(contracts)){
            contractId = contracts.getData(0).getString("CONTRACT_ID");
        }
 
        //获取合同信息
        IDataset datalineData = new DatasetList();
        IDataset AttrInternets = new DatasetList();
        if (null != httResultSetDataset && httResultSetDataset.size() > 0)
        {
            datalineData = resultDataset.getDataset("DLINE_DATA");
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

                    dataline.put("pam_LINE_TRADE_NAME", dataline.getString("TRADENAME",""));
                    if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
                    	 //IP地址使用费
                        dataline.put("pam_NOTIN_IP_PRICE", "0");
                        //软件应用服务费（元）
                        dataline.put("pam_NOTIN_SOFTWARE_PRICE", "0");
                        //网络技术支持服务费（元）
                        dataline.put("pam_NOTIN_NET_PRICE", "0");
                    }else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
                    	 //软件应用服务费（元）
                        dataline.put("pam_NOTIN_SOFTWARE_PRICE", "0");
                        //网络技术支持服务费（元）
                        dataline.put("pam_NOTIN_NET_PRICE", "0");
                        String cityA = StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", dataline.getString("CITYA"));
                        String cityZ = StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", dataline.getString("CITYZ"));

                        dataline.put("pam_NOTIN_A_CITY", cityA);
                        dataline.put("pam_NOTIN_Z_CITY", cityZ);
                       /* dataline.put("pam_NOTIN_GROUP_PERCENT","20%");//集团所在市县分成比例
                        dataline.put("pam_NOTIN_A_PERCENT","40%");//A端所在市县分成比例
                        dataline.put("pam_NOTIN_Z_PERCENT","40%");//Z端所在市县分成比例
                        dataline.put("pam_NOTIN_SLA", "0");//SLA服务费（元/月）
*/                    }else if("7016".equals(productId)){
                   	 //IP地址使用费
                       dataline.put("pam_NOTIN_IP_PRICE", "0");
                       //软件应用服务费（元）
                       dataline.put("pam_NOTIN_SOFTWARE_PRICE", "0");
                       //网络技术支持服务费（元）
                       dataline.put("pam_NOTIN_NET_PRICE", "0");

                   }
                  
                    
                    //通过合同编码，查询合同的专线信息。
                    IData contractParam = new DataMap();
                    contractParam.put("CONTRACT_ID", contractId);
                    contractParam.put("PRODUCT_ID", productId);
                    contractParam.put("CUST_ID", custId);
                    contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
                   
                    IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);
                    if (IDataUtil.isNotEmpty(contratSet)){
                        IData tempLine = contratSet.getData(0);
//                        dataline.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                        dataline.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                        dataline.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                        dataline.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                        dataline.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                        if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
                        	 dataline.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                             dataline.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//软件应用服务费（元）
                             dataline.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//网络技术支持服务费（元）
                        }else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
                        	 dataline.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//软件应用服务费（元）
                             dataline.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//网络技术支持服务费（元）
                             dataline.put("pam_NOTIN_GROUP_PERCENT",tempLine.getString("RSRV_STR6","20%"));//集团所在市县分成比例
                             dataline.put("pam_NOTIN_A_PERCENT",tempLine.getString("RSRV_STR7","40%"));//A端所在市县分成比例
                             dataline.put("pam_NOTIN_Z_PERCENT",tempLine.getString("RSRV_STR8","40%"));//Z端所在市县分成比例
                             dataline.put("pam_NOTIN_SLA", tempLine.getString("RSRV_STR16","0"));//SLA服务费（元/月）
                        }else if("7010".equals(productId)){
                        	dataline.put("pam_NOTIN_VOICE", tempLine.getString("RSRV_STR15","0"));//语音通信费（元/分钟）
                        }else if("7016".equals(productId)){
                       	 dataline.put("pam_NOTIN_IP_PRICE",tempLine.getString("RSRV_STR10","0"));//IP地址使用费
                         dataline.put("pam_NOTIN_SOFTWARE_PRICE",tempLine.getString("RSRV_STR11","0"));//软件应用服务费（元）
                         dataline.put("pam_NOTIN_NET_PRICE",tempLine.getString("RSRV_STR12","0"));//网络技术支持服务费（元）
                    }
                    }       

                    int linenumber = 0;
                    //拼装VIOP专线属性资料
                    IData AttrInternet = new DataMap();
                    if("7010".equals(productId)){
                        if(chageFlag){
                           IData lineNum = new DataMap();
                           lineNum.put("USER_ID", userId);
                           IDataset maxLineNumer = CSAppCall.call("SS.BookTradeSVC.getMaxLineNumberByUserId",lineNum);
                           if(null != maxLineNumer && maxLineNumer.size() >0 ){
                               IData line = maxLineNumer.getData(0);
                               linenumber = Integer.valueOf(line.getString("LINE_NUMBER_CODE"));
                           }
                        }
                        AttrInternet.put("pam_NOTIN_X_TAG", "0");//TAG
                        AttrInternet.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number+linenumber));//专线
                        AttrInternet.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i+linenumber));//专线CODE
                        AttrInternet.put("pam_NOTIN_LINE_BROADBAND",dataline.getString("pam_NOTIN_LINE_BROADBAND"));//专线带宽（兆）
                        AttrInternet.put("pam_NOTIN_LINE_PRICE", dataline.getString("pam_NOTIN_LINE_PRICE"));//专线价格（元）
                        AttrInternet.put("pam_LINE_TRADE_NAME", dataline.getString("pam_LINE_TRADE_NAME"));//专线名称
                        AttrInternet.put("pam_NOTIN_LINE_INSTANCENUMBER",dataline.getString("pam_NOTIN_LINE_INSTANCENUMBER"));//专线实例号
                        AttrInternet.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("pam_NOTIN_PRODUCT_NUMBER"));//业务标识
                        AttrInternet.put("pam_NOTIN_INSTALLATION_COST", dataline.getString("pam_NOTIN_INSTALLATION_COST"));//安装调试费
                        AttrInternet.put("pam_NOTIN_ONE_COST", dataline.getString("pam_NOTIN_ONE_COST"));//一次性通信服务费
                        AttrInternet.put("pam_NOTIN_VOICE", dataline.getString("pam_NOTIN_VOICE"));//语音通信费（元/分钟）
                        AttrInternet.put("pam_NOTIN_ZJ_NUMBER", dataline.getString("ZJ"));//中继号
                        AttrInternets.add(AttrInternet);
                    }else if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
                        AttrInternet.put("pam_NOTIN_TAG", String.valueOf(i));//TAG
                        AttrInternet.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number));//专线
                        AttrInternet.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i));//专线CODE
                        AttrInternet.put("pam_NOTIN_LINE_BROADBAND",dataline.getString("pam_NOTIN_LINE_BROADBAND"));//专线带宽（兆）
                        AttrInternet.put("pam_NOTIN_LINE_PRICE", dataline.getString("pam_NOTIN_LINE_PRICE"));//专线价格（元）
                        AttrInternet.put("pam_LINE_TRADE_NAME", dataline.getString("pam_LINE_TRADE_NAME"));//专线名称
                        AttrInternet.put("pam_NOTIN_LINE_INSTANCENUMBER",dataline.getString("pam_NOTIN_LINE_INSTANCENUMBER"));//专线实例号
                        AttrInternet.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("pam_NOTIN_PRODUCT_NUMBER"));//业务标识
                        AttrInternet.put("pam_NOTIN_INSTALLATION_COST", dataline.getString("pam_NOTIN_INSTALLATION_COST"));//安装调试费
                        AttrInternet.put("pam_NOTIN_ONE_COST", dataline.getString("pam_NOTIN_ONE_COST"));//一次性通信服务费
                        AttrInternet.put("pam_NOTIN_IP_PRICE", dataline.getString("pam_NOTIN_IP_PRICE"));//IP地址使用费
                        AttrInternet.put("pam_NOTIN_SOFTWARE_PRICE", dataline.getString("pam_NOTIN_SOFTWARE_PRICE"));//
                        AttrInternet.put("pam_NOTIN_NET_PRICE", dataline.getString("pam_NOTIN_NET_PRICE"));//
                        
                        AttrInternets.add(AttrInternet);
                    }else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
                        AttrInternet.put("pam_NOTIN_TAG", String.valueOf(i));//TAG
                        AttrInternet.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number));//专线
                        AttrInternet.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i));//专线CODE
                        AttrInternet.put("pam_NOTIN_LINE_BROADBAND",dataline.getString("pam_NOTIN_LINE_BROADBAND"));//专线带宽（兆）
                        AttrInternet.put("pam_NOTIN_LINE_PRICE", dataline.getString("pam_NOTIN_LINE_PRICE"));//专线价格（元）
                        AttrInternet.put("pam_LINE_TRADE_NAME", dataline.getString("pam_LINE_TRADE_NAME"));//专线名称
                        AttrInternet.put("pam_NOTIN_LINE_INSTANCENUMBER",dataline.getString("pam_NOTIN_LINE_INSTANCENUMBER"));//专线实例号
                        AttrInternet.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("pam_NOTIN_PRODUCT_NUMBER"));//业务标识
                        AttrInternet.put("pam_NOTIN_INSTALLATION_COST", dataline.getString("pam_NOTIN_INSTALLATION_COST"));//安装调试费
                        AttrInternet.put("pam_NOTIN_ONE_COST", dataline.getString("pam_NOTIN_ONE_COST"));//一次性通信服务费
                        AttrInternet.put("pam_NOTIN_SOFTWARE_PRICE", dataline.getString("pam_NOTIN_SOFTWARE_PRICE"));//
                        AttrInternet.put("pam_NOTIN_NET_PRICE", dataline.getString("pam_NOTIN_NET_PRICE"));//
                        AttrInternet.put("pam_NOTIN_GROUP_PERCENT", dataline.getString("pam_NOTIN_GROUP_PERCENT"));///集团所在市县分成比例
                        AttrInternet.put("pam_NOTIN_A_PERCENT", dataline.getString("pam_NOTIN_A_PERCENT"));//A端所在市县分成比例
                        AttrInternet.put("pam_NOTIN_Z_PERCENT", dataline.getString("pam_NOTIN_Z_PERCENT"));//Z端所在市县分成比例
                        AttrInternet.put("pam_NOTIN_SLA", dataline.getString("pam_NOTIN_SLA"));//SLA服务费（元/月）

                    	AttrInternets.add(AttrInternet);
                    }else if("7016".equals(productId)){
                    	AttrInternet.put("pam_NOTIN_TAG", String.valueOf(i));//TAG
                    	AttrInternet.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number));//专线
                    	AttrInternet.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i));//专线CODE
                    	AttrInternet.put("pam_NOTIN_LINE_BROADBAND",dataline.getString("pam_NOTIN_LINE_BROADBAND"));//专线带宽（兆）
                    	AttrInternet.put("pam_NOTIN_LINE_PRICE", dataline.getString("pam_NOTIN_LINE_PRICE"));//专线价格（元）
                    	AttrInternet.put("pam_LINE_TRADE_NAME", dataline.getString("pam_LINE_TRADE_NAME"));//专线名称
                    	AttrInternet.put("pam_NOTIN_LINE_INSTANCENUMBER",dataline.getString("pam_NOTIN_LINE_INSTANCENUMBER"));//专线实例号
                    	AttrInternet.put("pam_NOTIN_PRODUCT_NUMBER", dataline.getString("pam_NOTIN_PRODUCT_NUMBER"));//业务标识
                    	AttrInternet.put("pam_NOTIN_INSTALLATION_COST", dataline.getString("pam_NOTIN_INSTALLATION_COST"));//安装调试费
                    	AttrInternet.put("pam_NOTIN_ONE_COST", dataline.getString("pam_NOTIN_ONE_COST"));//一次性通信服务费
                    	AttrInternet.put("pam_NOTIN_IP_PRICE", dataline.getString("pam_NOTIN_IP_PRICE"));//IP地址使用费
                    	AttrInternet.put("pam_NOTIN_SOFTWARE_PRICE", dataline.getString("pam_NOTIN_SOFTWARE_PRICE"));//
                    	AttrInternet.put("pam_NOTIN_NET_PRICE", dataline.getString("pam_NOTIN_NET_PRICE"));//
                    	
                    	AttrInternets.add(AttrInternet);
                    }
                }
            }
            
        }
        
        //查询必选服务和优惠
        IData ElementsParam = new DataMap();
        if("7010".equals(productId)){
            ElementsParam.put("PRODUCT_ID",productId);
        }else{
            ElementsParam.put("PRODUCT_ID",mebProductId);
        }
        ElementsParam.put("EFFECT_NOW", false);
        ElementsParam.put("GROUP_ID", groupId);
        ElementsParam.put("CUST_ID", custId);
        ElementsParam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        IDataset ElementsInfoList = DatalineEspUtil.getElementsInfo(ElementsParam);
       
        IDataset selectElements = ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS");
        if("7010".equals(productId)){//这个voip的也是尴尬。这个70100002包底下还必须要有一个，结束时间变成系统时间前一秒吧
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", "8819");
            discnt.put("MODIFY_TAG", "0");
            discnt.put("ELEMENT_TYPE_CODE", "D");
            discnt.put("END_DATE", SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));
            discnt.put("START_DATE",SysDateMgr.getSysTime());
            discnt.put("PRODUCT_ID",productId);
            discnt.put("PACKAGE_ID","70100002");
            discnt.put("INST_ID","");
            selectElements.add(discnt);
            
        }
        //查询已有付费计划信息
        IDataset infosDataset = getPayPlanInfo(productId);
        
        //根据合同号查询账户信息
       // IDataset contractList = getAcctInfo(contractId);
        
        resultDataset.put("PRODUCT_ID", productId);
        resultDataset.put("USER_ID", userId);
       
        
        IData productParamInfo = new DataMap();
        productParamInfo.put("PRODUCT_ID", productId);
        IDataset productParam = new DatasetList();
        productParam = resultDataset.getDataset("COMMON_DATA");//resultDataset.getDataset("DLINE_DATA");//resultDataset.getDataset("COMMON_DATA");
        
//        IDataset LineTypeList = StaticUtil.getStaticList("LINETYPE_"+productId);
        IDataset LineTypeList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID",new String[] {
                "TYPE_ID"
            }, new String[] {
        		"LINETYPE_"+productId
            });

        boolean lineTypeFlag=false;
        for(int i=0,sizei=productParam.size();i<sizei;i++){//attr表数据循环
            IData productParamData = productParam.getData(i);
            if(IDataUtil.isNotEmpty(productParamData)
            		&&"LINE_TYPE".equals(productParamData.getString("ATTR_CODE",""))){
            	String lineType=productParamData.getString("ATTR_VALUE","");
                for(int j=0,sizej=LineTypeList.size();j<sizej;j++){//配置数据循环
                    IData LineTypeData = LineTypeList.getData(j);
                    if(IDataUtil.isNotEmpty(LineTypeData)&&lineType.equals(LineTypeData.getString("DATA_ID",""))){
                        	String lineTypePID=LineTypeData.getString("PDATA_ID","");
                            for(int k=0,sizek=selectElements.size();k<sizek;k++){//必选优惠循环
                                IData selectElementData = selectElements.getData(k);
                                if(IDataUtil.isNotEmpty(LineTypeData)&&lineTypePID.equals(selectElementData.getString("ELEMENT_ID")))
                                {
                                	selectElementData.put("ELEMENT_ID", LineTypeData.getString("DATA_ID",""));
                                }
                            	lineTypeFlag=true;
                            }
                            if(lineTypeFlag) break;
                    	
            		}
                	
                }
                if(lineTypeFlag) break;
            }
        }
        
        IData dataLineInfo = new DataMap();
        dataLineInfo.put("ATTR_CODE", "NOTIN_AttrInternet");//专线参数信息
        dataLineInfo.put("ATTR_VALUE", AttrInternets.toString());
        productParam.add(dataLineInfo);
        
        IData commondataLineInfo = new DataMap();
        commondataLineInfo.put("ATTR_CODE", "NOTIN_COMMON_DATA");//专线COMMON_DATA
        commondataLineInfo.put("ATTR_VALUE", resultDataset.getDataset("COMMON_DATA").toString());
        productParam.add(commondataLineInfo);
        
        IData dataLineDataInfo = new DataMap();
        dataLineDataInfo.put("ATTR_CODE", "NOTIN_DATALINE_DATA");//专线DATALINE_DATA
        dataLineDataInfo.put("ATTR_VALUE", resultDataset.getDataset("DLINE_DATA").toString());
        productParam.add(dataLineDataInfo);
        
        if(productId.equals("7010")) {
            IData zjAttrInfo = new DataMap();
            zjAttrInfo.put("ATTR_CODE", "NOTIN_ZJ_ATTR");//中继信息暂时塞值后面再改
            
            //String notinZj =map.getString("NOTIN_ZJ_ATTR","");
            /*if(StringUtils.isBlank(notinZj)){
            */  zjAttrInfo.put("ATTR_VALUE", new DatasetList().toString());//中继信息暂时从map里面直接取
            /*}else{
                zjAttrInfo.put("ATTR_VALUE", map.getDataset("NOTIN_ZJ_ATTR").toString());//中继信息暂时从map里面直接取
            }*/
            productParam.add(zjAttrInfo);
            IData productParamVoip  =  new DataMap();
            productParamVoip.putAll(DatalineEspUtil.getCommInfo(ibsysId));
           // IDataset productParamSet = DatalineEspUtil.saveProductParamInfoFrontData(productParamVoip);
           // inparam.put("PRODUCT_PARAM", productParamSet);
            //productParam.add(DatalineEspUtil.saveProductParamInfoFrontData(productParamVoip));
            
            IData projectNames =  WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId,"PROJECTNAME","0");
            if(IDataUtil.isEmpty(projectNames)){
                 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无项目名称信息!");
            }
            String projectName = projectNames.getString("ATTR_VALUE");
           // IData grpInfoParam  = new DataMap();
            productParamVoip.put("NOTIN_DETMANAGER_INFO", "");
            productParamVoip.put("NOTIN_DETMANAGER_PHONE", "");
            productParamVoip.put("NOTIN_DETADDRESS", "");
            productParamVoip.put("NOTIN_PROJECT_NAME", projectName);
            productParamVoip.put("PROJECT_NAME", projectName);
            productParam = DatalineEspUtil.saveProductParamInfo(productParam,productParamVoip);
            //IDataset list = DatalineEspUtil.saveProductParamInfoFrontData(grpInfoParam);
           // productParam.add(list);
        }
        
        productParamInfo.put("PRODUCT_PARAM", productParam); // 产品参数信息
        //拼装入参信息
        IData inparam = new DataMap();
        inparam.put("RES_INFO", new DatasetList());

        inparam.put("X_TRADE_FEESUB", null);
        inparam.put("ELEMENT_INFO",selectElements);//已有必选信息
        inparam.put("PLAN_INFO",infosDataset);//付费计划信息
        inparam.put("GRP_PACKAGE_INFO",new DatasetList());//产品参数信息因为是成员不要

        inparam.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParamInfo));
        
        map = DatalineEspUtil.getEosInfo(map, mebProductId, user.getString("EPARCHY_CODE"));
        
        IData contract = new DataMap();
        contract.put("CONTRACT_ID", contractId);
        inparam.put("USER_INFO", contract);//合同信息
        
        inparam.put("CUST_ID", custId);
       // inparam.put("ACCT_ID", contractList.getData(0).getString("ACCT_ID",""));
        if("7010".equals(productId)){
            if(!chageFlag){
                 //4、获取账户信息
                IData acctInfo= DatalineEspUtil.getAcctInfo(ibsysId);
                acctInfo = transformAcctInfo(acctInfo,user,contractId);
                if(StringUtils.isBlank(acctInfo.getString("ACCT_ID",""))){
                    inparam.put("ACCT_IS_ADD", true);
                }
                if(StringUtils.isNotBlank(acctInfo.getString("ACCT_ID","")))
                {
                    inparam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
                }
                inparam.put("ACCT_INFO", acctInfo);
            }
            inparam.put("EOS", IDataUtil.idToIds(map));//产品参数信息
            
        }else{
            inparam.put("ACCT_IS_ADD", false); //专线成员默认使用集团账户
            inparam.put("EOSS", IDataUtil.idToIds(map));//产品参数信息
        }
            
        inparam.put("PRODUCT_ID", productId);
        inparam.put("MEB_PRODUCT_ID", mebProductId);
        inparam.put("EFFECT_NOW", false);
        inparam.put("SERIAL_NUMBER", serial_number);
        inparam.put("AUDIT_STAFF_ID", "");
        inparam.put("X_TRADE_PAYMONEY",null);
        inparam.put("REMARK","");

        inparam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        inparam.put("USER_ID", userId);
        IDataset contratSet = new DatasetList();
        if("7010".equals(productId)){
            if(chageFlag){
                contratSet = CSAppCall.call("SS.ChangeVoipUserElementSVC.crtOrder",inparam);
            }else{
                contratSet = CSAppCall.call("SS.CreateVoipGroupUserNewSVC.crtOrder",inparam);
            }
        }else if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
            contratSet = CSAppCall.call("SS.CreateInternetGroupUserNewSVC.crtOrder",inparam);
        }else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
        	contratSet = CSAppCall.call("SS.CreateDataLineGroupUserNewSVC.crtOrder",inparam);
        }else if("7016".equals(productId)){
        	contratSet = CSAppCall.call("SS.CreateIMSGroupUserNewSVC.crtOrder",inparam);
        }
        return contratSet;
        
        
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
    
    
    /**
     * 根据custId,productId查询合同信息
     * @throws Exception 
     */
    private IDataset getcontractsInfo(IData resultDataset,String custId,String productId) throws Exception{
         //加载合同信息
        StringBuilder lines = new StringBuilder();
        if(IDataUtil.isNotEmpty(resultDataset)){
            IDataset datalineData = resultDataset.getDataset("DLINE_DATA",new DatasetList());
            if (null != datalineData && datalineData.size() > 0)
            {
                for (int i = 0; i < datalineData.size(); i++)
                {
                    String line = datalineData.getData(i).getString("PRODUCTNO");
                    lines.append("'").append(line).append("'");
                    if (i != datalineData.size()-1){
                        lines.append(",");
                    }
                }
            }
        }
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("LINE_NOS", lines.toString());
        IDataset contracts = CSAppCall.call("CM.ConstractGroupSVC.qryContractByCustIdProductId", inparam);
        if(IDataUtil.isEmpty(contracts)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过专线实例号："+lines.toString()+"和CUST_ID："+custId+"查询客管接口无合同数据！");
        }
        return contracts;
    }
    
    /**
     * 获取用户付费计划
     * @throws Exception 
     * 
     */
    private IDataset getPayPlanInfo(String productId) throws Exception{
        // 查产品是否配置了付费参数
        IDataset payPans = new DatasetList();
        
        IData map = new DataMap();
        map.put("ID", productId);
        map.put("ID_TYPE", "P");
        map.put("ATTR_OBJ", "0");
        map.put("ATTR_CODE", "PAYMODECODE");
        IDataset dsPayConfigs = CSAppCall.call("CS.AttrBizInfoQrySVC.getBizAttr", map);
        IData dsPayConfig = new DataMap();
        if (IDataUtil.isNotEmpty(dsPayConfigs))
        {
            dsPayConfig = dsPayConfigs.getData(0);
        }
        if (IDataUtil.isNotEmpty(dsPayConfig))
        {
            // 如果配置了付费参数，就按付费参数中的显示
            String[] configs = dsPayConfig.getString("ATTR_VALUE", "").split(",");
            for (int i = 0; i < configs.length; i++)
            {
                IData payPaln = new DataMap();
                payPaln.put("PLAN_TYPE", configs[i]);
                payPans.add(payPaln);
            }
        }
        // 集团付费计划中的付费账目
        String itemDesc = "";
        IData itemsParam = new DataMap();
        itemsParam.put("SUBSYS_CODE", "CGM");
        itemsParam.put("PARAM_ATTR", "1");
        itemsParam.put("PARAM_CODE", productId);
        IDataset payItems = CSAppCall.call("CS.CommparaInfoQrySVC.getPayItemsParam", itemsParam);//CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(bc, productId);
        if (IDataUtil.isNotEmpty(payItems))
        {
            // 付费账目描述
            for (int i = 0; i < payItems.size(); i++)
            {
                IData item = payItems.getData(i);

                itemDesc = itemDesc + item.getString("NOTE_ITEM", "") + "(" + item.getString("PARA_CODE1", "") + ")";
                if (i < payItems.size() - 1)
                    itemDesc += ",";
            }
        }

        if (IDataUtil.isEmpty(payPans))
        {
            // 如果没有取到配置的付费类型，则按默认为个人付费，如果配置了集团付费账目则将集团付费也作为默认付费账目
            IData payPaln = new DataMap();
            payPaln.put("MODIFY_TAG", "0");
            payPaln.put("PLAN_TYPE_CODE", "P");
            payPans.add(payPaln);
            // 如果配置了集团付费账目则将集团付费也作为默认付费账目
            if (StringUtils.isNotBlank(itemDesc))
            {
                IData payPalnG = new DataMap();
                payPaln.put("MODIFY_TAG", "0");
                payPalnG.put("PLAN_TYPE_CODE", "G");
                payPans.add(payPalnG);
            }
        }

       return new DatasetList(payPans);
         //查询已有付费计划信息
       // IData inparamPlan = new DataMap();
      //  inparamPlan.put("USER_ID", userId);
       // inparamPlan.put("USER_ID_A", "-1");
       // IDataset infosDataset = CSAppCall.call("CS.UserPayPlanInfoQrySVC.getPayPlanInfosByUserIdForGrp", inparamPlan);
      //  return infosDataset;
    }
    
    /**
     * 根据合同号查询账户信息
     * @throws Exception 
     */
    private IDataset getAcctInfo(String contractId) throws Exception{
        //根据合同号查询账户信息
        IData contractParam = new DataMap();
        contractParam.put("CONTRACT_ID", contractId);
        IDataset contractList = CSAppCall.call("CS.AcctInfoQrySVC.getAcctInfoByContractNoForGrp", contractParam);
        if(contractList.isEmpty()){
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据合同号查询账户信息不存在!");
        }
        return contractList;
    }
 
    /**
     * Create SerialNumber
     * 
     * @param pageData
     * @throws Exception
     */
    public String createSerialNumber(String groupId,String productId,String grpUserEparchyCode) throws Exception
    {
        if (StringUtils.isEmpty(productId))
        {
            return null;
        }

        // 避免服务号码的重复 add begin
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);

        IData grpSnData = new DataMap();
        for (int i = 0; i < 10; i++)
        {
            grpSnData = CSAppCall.call("CS.GrpGenSnSVC.genGrpSn", param).getData(0);

            String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

            if (StringUtils.isEmpty(serialNumber))
            {
                break;
            }
            
            IData params = new DataMap();
            params.put("SERIAL_NUMBER", serialNumber);
            IDataset userList = CSAppCall.call("CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", params);
            if (IDataUtil.isEmpty(userList))
            {
                break;
            }
        }
        // 避免服务号码的重复 add end

        String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

        return serialNumber;
    }
   /**
    * 
    * @param grpProductId
    * @param eparchyCode
    * @param grpUserId
    * @throws Exception
    */
    
    public IDataset dealGrpPakcageInfo(String grpProductId, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", grpProductId);
        inparam.put("EPARCHY_CODE", eparchyCode);
        //IDataset mebPkgList =  CSAppCall.call("CS.ProductPkgInfoQrySVC.getMebForcePackageByGrpProId", inparam);
  

        IDataset elementList = new DatasetList();
        
        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(grpProductId);
        if (StringUtils.isBlank(mebProductId))
        {
            return new DatasetList();
        }
        elementList = UProductElementInfoQry.queryForceElementsByProductId(mebProductId);
        IDataset userGrpPackageList = new DatasetList();
        for (int i = 0; i < elementList.size(); i++)
        {
            IData temp = elementList.getData(i);
            IData userGrpPackage = new DataMap();
            userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
            userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
            userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
            userGrpPackage.put("ELEMENT_FORCE_TAG", temp.getString("ELEMENT_FORCE_TAG"));
            userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
            userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
            userGrpPackage.put("MODIFY_TAG", "0");
            userGrpPackageList.add(userGrpPackage);
        }
        return userGrpPackageList;
    }
        
    
    public IDataset createUserDatalineOrder(IData map) throws Exception
    {
        String ibsysId = map.getString("IBSYSID");
        IDataset esopData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
        if(DataUtils.isEmpty(esopData))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询流程信息错误!");
        }
        IData groupInfo = esopData.first();
        String groupId = groupInfo.getString("GROUP_ID");
        String productId = groupInfo.getString("BUSI_CODE");
        IData user = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        String custId= user.getString("CUST_ID");
         //1、查询必选服务和优惠
        IData ElementsParam = new DataMap();
        ElementsParam.put("PRODUCT_ID",productId);
        ElementsParam.put("EFFECT_NOW", false);
        ElementsParam.put("GROUP_ID", groupId);
        ElementsParam.put("CUST_ID", user.getString("CUST_ID"));
        ElementsParam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        IDataset ElementsInfoList = DatalineEspUtil.getElementsInfo(ElementsParam);
        
        IDataset subScribes =  SubscribeViewInfoBean.qryWorkformNodeByIbsysid(ibsysId, "apply");//第一个节点名
        if(DataUtils.isEmpty(subScribes))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询该节点无参数信息!");
        }
        String subIbsyId  = subScribes.first().getString("SUB_IBSYSID");
        IDataset contractInfo = WorkformAttrBean.qryAttrBySubIbsysidAndAttrCode(subIbsyId, "CONTRACT_ID");//获取合同号
        if(DataUtils.isEmpty(contractInfo))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无合同信息!");
        }
        String contractId = contractInfo.first().getString("ATTR_VALUE");
        //7012服务参数设置
        if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
        	//获取服务参数
        	IDataset workformAttr = WorkformAttrBean.qryAttrBySubIbsysidAndAttrCode(subIbsyId, "RENTOUTTYPE");//服务属性
        	if(DataUtils.isEmpty(workformAttr))
            {
                 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无服务参数信息!");
            }
            String attrValue = workformAttr.first().getString("ATTR_VALUE");
            IDataset elements = ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS");
            for(int i =0;i<elements.size();i++){
                IData element = elements.getData(i);
                if(dataLineSvc.equals(element.getString("ELEMENT_ID"))){
                    IData attrParam = element.getDataset("ATTR_PARAM").getData(0);
                    attrParam.put("ATTR_VALUE", attrValue);
                }
            }
        }
        
        //2、查询已有付费计划信息
        IDataset infosDataset = getPayPlanInfo(productId);
        
        //3、获取服务号码
        String serialNumber = createSerialNumber(groupId,productId,user.getString("EPARCHY_CODE"));
         
        //4、获取账户信息
        IData acctInfo= DatalineEspUtil.getAcctInfo(ibsysId);
        acctInfo = transformAcctInfo(acctInfo,user,contractId);
        //拼装入参信息
        IData inparam = new DataMap();
        
        IData resInfo = new DataMap();
        resInfo.put("DISABLED", "true");
        resInfo.put("MODIFY_TAG", "0");
        resInfo.put("RES_CODE", serialNumber);
        resInfo.put("RES_TYPE_CODE", "L");
        resInfo.put("CHECKED", "true");
        inparam.put("RES_INFO", new DatasetList(resInfo));
        
        inparam.put("X_TRADE_FEESUB", null);
        inparam.put("X_TRADE_PAYMONEY", null);
        inparam.put("CUST_ID", custId);
        inparam.put("ELEMENT_INFO",ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS"));//已有必选信息
        if(StringUtils.isEmpty(acctInfo.getString("ACCT_ID",""))){
            inparam.put("ACCT_IS_ADD", true);
        }
        if(StringUtils.isNotEmpty(acctInfo.getString("ACCT_ID","")))
        {
            inparam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        }
        inparam.put("ACCT_INFO", acctInfo);
        //inparam.put("ACCT_ID", contractList.getData(0).getString("ACCT_ID",""));
        //inparam.put("ACCT_ID", "1114090205653902");//接口中获取，map.getString("ACCT_ID");
        inparam.put("GRP_PACKAGE_INFO",dealGrpPakcageInfo(productId,user.getString("EPARCHY_CODE")));//产品参数信息
        
        IDataset eweInfo = EweNodeQry.qryEweByIbsysid(ibsysId,"0");
        if(DataUtils.isEmpty(eweInfo))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无流程信息！");
        }
        map.put("BUSIFORM_ID", eweInfo.first().getString("BUSIFORM_ID"));
        map = DatalineEspUtil.getEosInfo(map, productId, user.getString("EPARCHY_CODE"));
       
        inparam.put("EOS", IDataUtil.idToIds(map));
        inparam.put("EFFECT_NOW", false);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("PLAN_INFO",infosDataset);//付费计划信息
        inparam.put("SERIAL_NUMBER", serialNumber);
      //  inparam.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParamInfo));
        
        IData contract = new DataMap();
        //String contractId = "2018112000119345";//接口中获取，map.getString("CONTRACT_ID");
        contract.put("CONTRACT_ID", contractId);
        inparam.put("USER_INFO", contract);//合同信息
        inparam.put("AUDIT_STAFF_ID", "");//
        inparam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        inparam.put("REMARK", "");
        
        IDataset paramInfo = new DatasetList();
        IData paramp = new DataMap();
        paramp.put("PRODUCT_ID", productId);
        IDataset productParam = new DatasetList();
        IData param = new DataMap();
        //inputParams.getDataset("DLINE_DATA").getData(arg0)
        IData projectNames =  WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId,"PROJECTNAME","0");
        if(IDataUtil.isEmpty(projectNames)){
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无项目名称信息!");
        }
        String projectName = projectNames.getString("ATTR_VALUE");
        param.put("NOTIN_DETMANAGER_INFO", "");
        param.put("NOTIN_DETMANAGER_PHONE", "");
        param.put("NOTIN_DETADDRESS", "");
        param.put("NOTIN_PROJECT_NAME", projectName);
        param.put("PROJECT_NAME", projectName);
       // param.putAll(DatalineEspUtil.getCommInfo(ibsysId));
        productParam = DatalineEspUtil.saveProductParamInfoFrontData(param);
        paramp.put("PRODUCT_PARAM", productParam);
        paramInfo.add(paramp);
        inparam.put("PRODUCT_PARAM_INFO",paramInfo);

        IDataset result = new DatasetList();
        inparam.put("PF_WAIT", "1");
        
        result = CSAppCall.call("SS.CreateGroupUserEspSVC.crtOrder", inparam);
        
        return result;
        
    }
    
    public IDataset createUserAttrDatalineOrder(IData map) throws Exception
    {
        logger.debug(">>>>>>>>>>>>>>>>>zoulu集团订单>>>>>>>>>>>>>>>>>>"+map);
        String ibsysId = map.getString("IBSYSID");
        IDataset esopData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
        if(DataUtils.isEmpty(esopData))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询流程信息错误!");
        }
         //根据订单号查询变更集团的SERIAL_NUMBER
        IData newParam  = new DataMap();
        newParam.put("IBSYSID", ibsysId);
        newParam.put("RECORD_NUM", "0");
        newParam.put("ATTR_CODE", "GROUP_ID");
        IDataset newGrpInfo = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_ATTRCODE",newParam , Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(newGrpInfo))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询归属集团无数据！");
        }
        IData groupInfo = esopData.first();
        String groupId = newGrpInfo.first().getString("ATTR_VALUE");
        String productId = groupInfo.getString("BUSI_CODE");
    	IData newParam1  = new DataMap();
        newParam1.put("IBSYSID", ibsysId);
        newParam1.put("RECORD_NUM", "0");
        newParam1.put("ATTR_CODE", "PRODUCT_ID_B");
        IDataset newproductIdInfo = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_ATTRCODE",newParam1 , Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isNotEmpty(newproductIdInfo))
        {
        	productId=newproductIdInfo.first().getString("ATTR_VALUE");
        }
        IData user = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        String custId= user.getString("CUST_ID");
         //1、查询必选服务和优惠
        IData ElementsParam = new DataMap();
        ElementsParam.put("PRODUCT_ID",productId);
        ElementsParam.put("EFFECT_NOW", false);
        ElementsParam.put("GROUP_ID", groupId);
        ElementsParam.put("CUST_ID", user.getString("CUST_ID"));
        ElementsParam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        IDataset ElementsInfoList = DatalineEspUtil.getElementsInfo(ElementsParam);
        
        IDataset subScribes =  SubscribeViewInfoBean.qryWorkformNodeByIbsysid(ibsysId, "apply");//第一个节点名
        if(DataUtils.isEmpty(subScribes))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询该节点无参数信息!");
        }
        String subIbsyId  = subScribes.first().getString("SUB_IBSYSID");
        IDataset contractInfo = WorkformAttrBean.qryAttrBySubIbsysidAndAttrCode(subIbsyId, "CONTRACT_ID");//获取合同号
        if(DataUtils.isEmpty(contractInfo))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无合同信息!");
        }
        String contractId = contractInfo.first().getString("ATTR_VALUE");
        //7012服务参数设置
        if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
        	String attrValue = DataLineDiscntConst.SVCELEMENTID;
        	IDataset elements = ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS");
        	for(int i =0;i<elements.size();i++){
        		IData element = elements.getData(i);
        		if(dataLineSvc.equals(element.getString("ELEMENT_ID"))){
        			IData attrParam = element.getDataset("ATTR_PARAM").getData(0);
        			attrParam.put("ATTR_VALUE", attrValue);
        		}
        	}
        }
        
        //2、查询已有付费计划信息
        IDataset infosDataset = getPayPlanInfo(productId);
        
        //3、获取服务号码
        String serialNumber = createSerialNumber(groupId,productId,user.getString("EPARCHY_CODE"));
         
        //4、获取账户信息
        IData acctInfo= DatalineEspUtil.getAcctInfo(ibsysId);
        acctInfo = transformAcctInfo(acctInfo,user,contractId);
        //拼装入参信息
        IData inparam = new DataMap();
        
        IData resInfo = new DataMap();
        resInfo.put("DISABLED", "true");
        resInfo.put("MODIFY_TAG", "0");
        resInfo.put("RES_CODE", serialNumber);
        resInfo.put("RES_TYPE_CODE", "L");
        resInfo.put("CHECKED", "true");
        inparam.put("RES_INFO", new DatasetList(resInfo));
        
        inparam.put("X_TRADE_FEESUB", null);
        inparam.put("X_TRADE_PAYMONEY", null);
        inparam.put("CUST_ID", custId);
        inparam.put("ELEMENT_INFO",ElementsInfoList.getData(0).getDataset("SELECTED_ELEMENTS"));//已有必选信息
        if(StringUtils.isBlank(acctInfo.getString("ACCT_ID",""))){
            inparam.put("ACCT_IS_ADD", true);
        }
        if(StringUtils.isNotBlank(acctInfo.getString("ACCT_ID","")))
        {
            inparam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        }
        inparam.put("ACCT_INFO", acctInfo);
        //inparam.put("ACCT_ID", contractList.getData(0).getString("ACCT_ID",""));
        //inparam.put("ACCT_ID", "1114090205653902");//接口中获取，map.getString("ACCT_ID");
        inparam.put("GRP_PACKAGE_INFO",dealGrpPakcageInfo(productId,user.getString("EPARCHY_CODE")));//产品参数信息
        
        IDataset eweInfo = EweNodeQry.qryEweByIbsysid(ibsysId,"0");
        if(DataUtils.isEmpty(eweInfo))
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无流程信息！");
        }
        map.put("BUSIFORM_ID", eweInfo.first().getString("BUSIFORM_ID"));
        map = DatalineEspUtil.getEosInfo(map, productId, user.getString("EPARCHY_CODE"));
        map.put("RSRV_STR7", "GROUPATTRCHANGE");
        inparam.put("EOS", IDataUtil.idToIds(map));
        inparam.put("EFFECT_NOW", false);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("PLAN_INFO",infosDataset);//付费计划信息
        inparam.put("SERIAL_NUMBER", serialNumber);
      //  inparam.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParamInfo));
        
        IData contract = new DataMap();
        //String contractId = "2018112000119345";//接口中获取，map.getString("CONTRACT_ID");
        contract.put("CONTRACT_ID", contractId);
        inparam.put("USER_INFO", contract);//合同信息
        inparam.put("AUDIT_STAFF_ID", "");//
        inparam.put("USER_EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        inparam.put("REMARK", "");
        
        IDataset paramInfo = new DatasetList();
        IData paramp = new DataMap();
        paramp.put("PRODUCT_ID", productId);
        IDataset productParam = new DatasetList();
        IData param = new DataMap();
        //inputParams.getDataset("DLINE_DATA").getData(arg0)
        IData projectNames =  WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId,"PROJECTNAME","0");
       /* if(IDataUtil.isEmpty(projectNames)){
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号查询无项目名称信息!");
        }
        String projectName = projectNames.getString("ATTR_VALUE");*/
        param.put("NOTIN_DETMANAGER_INFO", "");
        param.put("NOTIN_DETMANAGER_PHONE", "");
        param.put("NOTIN_DETADDRESS", "");
        param.put("NOTIN_PROJECT_NAME", "");
        param.put("PROJECT_NAME", "");
        productParam = DatalineEspUtil.saveProductParamInfoFrontData(param);
        paramp.put("PRODUCT_PARAM", productParam);
        paramInfo.add(paramp);
        inparam.put("PRODUCT_PARAM_INFO",paramInfo);

        IDataset result = new DatasetList();
        inparam.put("PF_WAIT", "1");
        
        result = CSAppCall.call("SS.CreateGroupUserEspSVC.crtOrder", inparam);
        
        return result;
        
    }
    
    private IData transformAcctInfo(IData accountInfo,IData user,String contractId) throws Exception
    {
        IData acctInfo = new DataMap();
        if(DataUtils.isEmpty(accountInfo))
        {
            return acctInfo;
        }
        String acctId = accountInfo.getString("ACCT_ID");
        if(StringUtils.isNotBlank(acctId))
        {
            acctInfo.put("ACCT_ID", acctId);
        }
        else
        {
            acctInfo.putAll(accountInfo);
        }
        
        acctInfo.put("PAY_NAME", accountInfo.getString("ACCT_NAME","0"));
        acctInfo.put("PAY_MODE_CODE", accountInfo.getString("ACCT_TYPE","0"));
        
        //现金直接返回
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("0")){
            return acctInfo;
        }
        
        //托收
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("1"))
            acctInfo.put("PAYMENT_ID", "4");
        
        acctInfo.put("START_CYCLE_ID", SysDateMgr.getSysDate().replace("-", "").substring(0, 6));
        acctInfo.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012());
        acctInfo.put("BANK_ACCT_NAME", acctInfo.getString("BANK_NAME"));
        acctInfo.put("CONSIGN_MODE", "1");
        acctInfo.put("ACT_TAG", "1");
        acctInfo.put("RSRV_STR1", "1");
        acctInfo.put("MODIFY_TAG", "0");
        acctInfo.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
        acctInfo.put("USER_EPARCHY_CODE",user.getString("EPARCHY_CODE"));
       
        //合同号
        acctInfo.put("CONTRACT_NO", contractId);
            
        return acctInfo;
    }
   
}
