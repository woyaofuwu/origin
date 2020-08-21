package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.DatalineOrderSVC;

public class CreateInternetGroupUserBatBean extends CSBizService
{
    
    public IDataset dealDatalineOrder(IData paramData) throws Exception {
        IDataset result = new DatasetList();
        //查询在途工单数据
        IData commonData = queryDataline(paramData);
        //获取工单类型
        IData sheetType = commonData.getData("SHEETTYPE");
        //拼装数据
        IData dataline = regDataline(commonData);
        //拼装订单数据
        IData regData  = regData(dataline,sheetType);
        result.add(regData);
        
        IDataset resultData = null;
        
        if("6".equals(sheetType.getString("SERVICETYPE")) && "32".equals(sheetType.getString("SHEETTYPE"))){
            resultData = CSAppCall.call("SS.CreateInternetGroupUserSVC.crtOrder", regData);
        }else  if("4".equals(sheetType.getString("SERVICETYPE")) && "32".equals(sheetType.getString("SHEETTYPE"))){
            resultData = CSAppCall.call("SS.CreateDatalineGroupUserSVC.crtOrder", regData);
        }else  if("7".equals(sheetType.getString("SERVICETYPE")) && "32".equals(sheetType.getString("SHEETTYPE"))){
            resultData = CSAppCall.call("SS.CreateVoipGroupUserSVC.crtOrder", regData);
        }else if("6".equals(sheetType.getString("SERVICETYPE")) && "33".equals(sheetType.getString("SHEETTYPE"))){
            resultData = CSAppCall.call("SS.ChangeNetinUserElementSVC.crtOrder", regData);
        }else if("4".equals(sheetType.getString("SERVICETYPE")) && "33".equals(sheetType.getString("SHEETTYPE"))){
            resultData = CSAppCall.call("SS.ChangeDatalineUserElementSVC.crtOrder", regData);
        }else if("7".equals(sheetType.getString("SERVICETYPE")) && "33".equals(sheetType.getString("SHEETTYPE"))){
            resultData = CSAppCall.call("SS.ChangeVoipUserElementSVC.crtOrder", regData);
        }
        
        DatalineOrderSVC svc = new DatalineOrderSVC();
        svc.updateDalineOrderState(paramData);
        
        return resultData;
    }
    
    public IData queryDataline(IData param) throws Exception{
        IData dataline = new DataMap();
        IData sheetType = new DataMap();
        
        DatalineOrderSVC svc = new DatalineOrderSVC();
        IData comnon = new DataMap();
        
        IDataset commonData = svc.queryCommonDataInfo(param);
        for (int i = 0; i < commonData.size(); i++)
        {
            IData data = commonData.getData(i);
            comnon.put(data.getString("FIELDEN_NAME"), data.getString("FIELD_CONTENT"));
            if("SHEETTYPE".equals(data.getString("FIELDEN_NAME"))){
                sheetType.put("SHEETTYPE", data.getString("FIELD_CONTENT"));
            }else if("SERVICETYPE".equals(data.getString("FIELDEN_NAME"))){
                sheetType.put("SERVICETYPE", data.getString("FIELD_CONTENT"));
            }else if("USER_ID".equals(data.getString("FIELDEN_NAME"))){
                sheetType.put("USER_ID", data.getString("FIELD_CONTENT"));
            }
        }
        
        IDataset datalineData = svc.queryDatalineInfo(param);
        IDataset datalineList = new DatasetList();
        IData lineCount = new DataMap();
        for (int i = 0; i < datalineData.size(); i++)
        {
            IData data = datalineData.getData(i);
            IData line = new DataMap();
            line.put(data.getString("FIELDEN_NAME"), data.getString("FIELD_CONTENT"));
            IData numberCode = new DataMap();
            numberCode.put("pam_NOTIN_LINE_NUMBER_CODE", data.getString("LINE_COUNT"));
            lineCount.put("LINE_COUNT", data.getString("LINE_COUNT"));
            
            datalineList.add(line);
            datalineList.add(numberCode);
        }
        
        dataline.put("NOTIN_COMMON_DATA", comnon);
        dataline.put("NOTIN_DATALINE_DATA", datalineList);
        dataline.put("LINE_COUNT", lineCount);
        dataline.put("SHEETTYPE", sheetType);
        
        return dataline;
    }
    
    private IData regDataline(IData queryDataline) throws Exception{
        IData notinCommonData = queryDataline.getData("NOTIN_COMMON_DATA");
        IDataset lineDataList = queryDataline.getDataset("NOTIN_DATALINE_DATA");
        IData lineCount = queryDataline.getData("LINE_COUNT");
        IData sheetType = queryDataline.getData("SHEETTYPE");

        String eparchyCode = "0898";
        String productId = "";
        if(null != notinCommonData && notinCommonData.size() >0){
            productId = notinCommonData.getString("PRODUCT_ID");
        }
        
        // 避免服务号码的重复 add begin
        IData param = new DataMap();
        param.put("PRODUCT_ID",productId);
        param.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        IDataset grpSnData = CSAppCall.call("CS.GrpGenSnSVC.genGrpSn", param);
        String serialNum = grpSnData.getData(0).getString("SERIAL_NUMBER","");
        
        UcaInfoQrySVC svc = new UcaInfoQrySVC();
        IData groupParam = new DataMap();
        groupParam.put("GROUP_ID", notinCommonData.getString("CUSTOMNO"));
        IDataset custInfo = svc.qryGrpInfoByGrpId(groupParam);
        String custId = custInfo.getData(0).getString("CUST_ID");
        
        IDataset acct = AcctInfoQry.getAcctInfoByCustIdForGrp(custId);
        String acctId = acct.getData(0).getString("ACCT_ID");
        String contractId = "";
        
        String detAddress = "";
        String detManger = "";
        String detMangerPhone = "";
        String projectName = "";
        
        String productNumber = "";
        for (int i = 0; i < lineDataList.size(); i++)
        {
            IData data = lineDataList.getData(i);
            String attr[] = data.getNames();
            if("PRODUCTNO".equals(attr[0])){
                productNumber = data.getString("PRODUCTNO");
            }
        }
        
        //服务包、元素ID
        String packageId = "";
        String elementId = "";
        
        if("6".equals(sheetType.getString("SERVICETYPE"))){
            packageId = "70110001";
            elementId = "951";
        }else  if("4".equals(sheetType.getString("SERVICETYPE"))){
            packageId = "70120001";
            elementId = "952";
        }else  if("7".equals(sheetType.getString("SERVICETYPE"))){
            packageId = "70100001";
            elementId = "950";
        } 
        
        IDataset productInfo = new DatasetList();
        IDataset productParam = new DatasetList();
        IData product = new DataMap();
        IData attrInternet = new DataMap();
        IData commonData = new DataMap();
        IData datalineData = new DataMap();
        
        //业务标 识
        IData notinProductNumber = new DataMap();
        notinProductNumber.put("ATTR_CODE", "NOTIN_PRODUCT_NUMBER");
        notinProductNumber.put("ATTR_VALUE", productNumber);
        
        productParam.add(notinProductNumber);
        
        //专线实例号
        IData notinLine = new DataMap();
        notinLine.put("ATTR_CODE", "NOTIN_LINE_INSTANCENUMBER");
        notinLine.put("ATTR_VALUE", productNumber);
        
        productParam.add(notinLine);

        
        //专线名称
        IData lineName = new DataMap();
        lineName.put("ATTR_CODE", "NOTIN_LINE_NUMBER");
        lineName.put("ATTR_VALUE", "");
        
        productParam.add(lineName);

        //联系地址
        IData notinDetAddress = new DataMap();
        notinDetAddress.put("ATTR_CODE", "NOTIN_DETADDRESS");
        notinDetAddress.put("ATTR_VALUE", detAddress);
        
        productParam.add(notinDetAddress);

        //管理员信息
        IData notinDetManger = new DataMap();
        notinDetManger.put("ATTR_CODE", "NOTIN_DETMANAGER_INFO");
        notinDetManger.put("ATTR_VALUE",detManger);
        
        productParam.add(notinDetManger);

        //管理员电话
        IData notinDetMangerPhone = new DataMap();
        notinDetMangerPhone.put("ATTR_CODE", "NOTIN_DETMANAGER_PHONE");
        notinDetMangerPhone.put("ATTR_VALUE",detMangerPhone);
        
        productParam.add(notinDetMangerPhone);

        //项目名称
        IData notinProject = new DataMap();
        notinProject.put("ATTR_CODE", "NOTIN_PROJECT_NAME");
        notinProject.put("ATTR_VALUE",projectName);
        
        productParam.add(notinProject);


        String lineNumberCode = lineCount.getString("LINE_COUNT");
        String lineNumber = "专线"+(Integer.parseInt(lineNumberCode)+1);
        String bandWidth = "";
        String price = "0";
        String installCost = "0";
        String oneCost = "0";
        String ipPrice = "0";
        
        for (int i = 0; i < lineDataList.size(); i++)
        {
            IData data = lineDataList.getData(i);
            String attr[] = data.getNames();
            if("BANDWIDTH".equals(attr[0])){
                bandWidth = data.getString("BANDWIDTH");
            }
        }
        
        IDataset internetList = new DatasetList();
        IData internet = new DataMap();
        internet.put("pam_NOTIN_LINE_NUMBER",lineNumber);
        internet.put("pam_NOTIN_LINE_NUMBER_CODE",lineNumberCode);
        internet.put("pam_NOTIN_LINE_BROADBAND",bandWidth);
        internet.put("pam_NOTIN_LINE_PRICE",price);
        internet.put("pam_NOTIN_INSTALLATION_COST",installCost);
        internet.put("pam_NOTIN_ONE_COST",oneCost);
        internet.put("pam_NOTIN_IP_PRICE",ipPrice);
        internet.put("pam_NOTIN_LINE_INSTANCENUMBER",productNumber);
        internet.put("pam_NOTIN_PRODUCT_NUMBER",productNumber);
        internet.put("tag","0");
        
        internetList.add(internet);
        attrInternet.put("ATTR_CODE", "NOTIN_AttrInternet");
        attrInternet.put("ATTR_VALUE",internetList);
        productParam.add(attrInternet);
        
        //VOIP语音专线处理
        if("7".equals(sheetType.getString("SERVICETYPE"))){
            IData zjAttrData = new DataMap();
            IDataset zjAttr = new  DatasetList();
            IData zjdata = new DataMap();
            
            zjdata.put("pam_NOTIN_ZJ_NUMBER","0");
            // 类型
            zjdata.put("pam_NOTIN_ZJ_TYPE", "0");
            // 总机号
            zjdata.put("pam_NOTIN_SUPER_NUMBER","0");
            // 名称
            zjdata.put("pam_NOTIN_TYPE_NAME","0");
            
            zjAttr.add(zjdata);
            
            zjAttrData.put("ATTR_CODE", "NOTIN_ZJ_ATTR");
            zjAttrData.put("ATTR_VALUE",zjAttr);
            
            productParam.add(zjAttrData);
        } 
        
        
        IDataset commonDataList =  new DatasetList();
        // 公共数据
        for (int i = 0; i < notinCommonData.size(); i++)
        {
            IData attrValue = new DataMap();
            String attr[] = notinCommonData.getNames();
            attrValue.put("ATTR_CODE", attr[i]);
            attrValue.put("ATTR_VALUE", notinCommonData.getString(attr[i]));
            commonDataList.add(attrValue);
        }
        
        commonData.put("ATTR_CODE", "NOTIN_COMMON_DATA");
        commonData.put("ATTR_VALUE",commonDataList);
        
        productParam.add(commonData);

        
        
        
        IDataset datalineList = new DatasetList();
        IData attrValue = new DataMap();
        // 专线数据
        for (int j = 0; j < lineDataList.size(); j++)
        {
            IData data = lineDataList.getData(j);
            for (int k = 0; k < data.size(); k++)
            {
                String attr[] = data.getNames();
                attrValue.put(attr[k], data.getString(attr[k]));
            }
        }
        datalineList.add(attrValue);
        
        
        datalineData.put("ATTR_CODE", "NOTIN_DATALINE_DATA");
        datalineData.put("ATTR_VALUE",datalineList);

        productParam.add(datalineData);

        product.put("PRODUCT_ID",productId);
        product.put("PRODUCT_PARAM",productParam);
        productInfo.add(product);
        
        IDataset elementInfo = new DatasetList();
        IData element = new DataMap();
        element.put("INST_ID",SeqMgr.getInstId());
        element.put("START_DATE",SysDateMgr.getSysTime());
        element.put("ELEMENT_TYPE_CODE", "S");
        element.put("MODIFY_TAG", "0");
        element.put("PRODUCT_ID",productId);
        element.put("END_DATE", SysDateMgr.getTheLastTime());
        element.put("PACKAGE_ID", packageId);
        element.put("ELEMENT_ID", elementId);
        
        if("4".equals(sheetType.getString("SERVICETYPE"))){
            IDataset attr = new DatasetList();
            IData attrParam = new DataMap();
            attrParam.put("ATTR_VALUE", "1");
            attrParam.put("ATTR_CODE", "V952V4");
            attr.add(attrParam);
            element.put("ATTR_PARAM", attr);
        }

        
        if("7".equals(sheetType.getString("SERVICETYPE"))){
            IData elementPack = new DataMap();
            elementPack.put("INST_ID",SeqMgr.getInstId());
            elementPack.put("START_DATE",SysDateMgr.getSysTime());
            elementPack.put("ELEMENT_TYPE_CODE", "D");
            elementPack.put("MODIFY_TAG", "0");
            elementPack.put("PRODUCT_ID",productId);
            elementPack.put("END_DATE", SysDateMgr.getTheLastTime());
            elementPack.put("PACKAGE_ID", "70100002");
            elementPack.put("ELEMENT_ID", "3063");
            
            elementInfo.add(elementPack);
        }
        elementInfo.add(element);
        
        IDataset planInfo = new DatasetList();
        IData plan = new DataMap();
        plan.put("MODIFY_TAG", "0");
        plan.put("PLAN_TYPE_CODE", "P");
        planInfo.add(plan);
        
        IDataset resInfo = new DatasetList();
        IData res = new DataMap();
        res.put("MODIFY_TAG", "0");
        res.put("RES_TYPE_CODE", "L");
        res.put("RES_CODE",serialNum);
        res.put("CHECKED", "true");
        res.put("DISABLED", "true");
        resInfo.add(res);

        
        IData userInfo = new DataMap();
        userInfo.put("CONTRACT_ID", contractId);
        
        
        IDataset grpPackInfo = new DatasetList();
        if("4".equals(sheetType.getString("SERVICETYPE"))){
            IData packInfo = new DataMap();
            packInfo.put("ELEMENT_TYPE_CODE", "S");        
            packInfo.put("MODIFY_TAG", "0");        
            packInfo.put("PRODUCT_ID", "701201");        
            packInfo.put("PACKAGE_ID", "70120101");        
            packInfo.put("ELEMENT_ID", "701201");        
            grpPackInfo.add(packInfo);
        }

        
       
        
        IData map = new DataMap();
        map.put("USER_EPARCHY_CODE", eparchyCode);
        map.put("PRODUCT_ID", productId);
        map.put("SERIAL_NUMBER", serialNum);
        map.put("PRODUCT_PARAM_INFO", productInfo);
        if("32".equals(sheetType.getString("SHEETTYPE"))){
            map.put("ELEMENT_INFO", elementInfo);
        }
        map.put("USER_INFO", userInfo);
        map.put("PLAN_INFO", planInfo);
        map.put("CUST_ID", custId);
        map.put("ACCT_ID", acctId);
        map.put("RES_INFO", resInfo);
        map.put("GRP_PACKAGE_INFO", grpPackInfo);
        
        return map;
    }
    
    private IData regData(IData dataline,IData sheetType){
        IData map = new DataMap();
        
        map.put("RULE_BIZ_KIND_CODE", "GrpUserOpen");
        map.put("USER_EPARCHY_CODE", dataline.getString("USER_EPARCHY_CODE"));
        map.put("PRODUCT_ID", dataline.getString("PRODUCT_ID"));
        map.put("X_TRADE_FEESUB", null);
        if("32".equals(sheetType.getString("SHEETTYPE"))){
            map.put("SERIAL_NUMBER", dataline.getString("SERIAL_NUMBER"));
        }else  if("33".equals(sheetType.getString("SHEETTYPE"))){
            map.put("BUSI_CTRL_TYPE","ChgUs");
            map.put("USER_ID",sheetType.getString("USER_ID"));
        }
        map.put("X_SUBTRANS_CODE","");
        map.put("REMARK", "J2EE在途工单割接数据");
        map.put("PRODUCT_PARAM_INFO", dataline.getDataset("PRODUCT_PARAM_INFO"));
        map.put("ELEMENT_INFO", dataline.getDataset("ELEMENT_INFO"));
        map.put("USER_INFO", dataline.getData("USER_INFO"));
        map.put("EFFECT_NOW", "false");
        map.put("PLAN_INFO", dataline.getDataset("PLAN_INFO"));
        map.put("CUST_ID", dataline.getString("CUST_ID"));
        map.put("ACCT_ID", dataline.getString("ACCT_ID"));
        map.put("RES_INFO", dataline.getDataset("RES_INFO"));
        map.put("GRP_PACKAGE_INFO",dataline.getDataset("GRP_PACKAGE_INFO"));
        map.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        map.put("X_TRADE_PAYMONEY", null);
        
        map.put("STAFF_ID", "SUPERUSR");
        map.put("STAFF_NAME", "测试姓名");
        
        map.put("LOGIN_EPARCHY_CODE", "0898");
        map.put("STAFF_EPARCHY_CODE", "0898"); 
        map.put("IN_MODE_CODE", "0");
        map.put("CITY_CODE", "HNSJ");
        map.put("DEPART_ID", "36601");
        map.put("DEPART_CODE", "HNSJ0000");
                
        return map;
    }

}
