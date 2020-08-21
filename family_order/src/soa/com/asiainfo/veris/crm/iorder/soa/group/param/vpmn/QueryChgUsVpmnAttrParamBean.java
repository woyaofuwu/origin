package com.asiainfo.veris.crm.iorder.soa.group.param.vpmn;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryChgUsVpmnAttrParamBean extends QueryAttrParamBean
{
	private static final Logger logger = LoggerFactory.getLogger(QueryChgUsVpmnAttrParamBean.class);
   
    public static IData queryVpnParamAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        IData attrItemA = new DataMap();
        IData transData = new DataMap();
        IData parainfo = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");
        String userId = param.getString("USER_ID");
        String custId = param.getString("CUST_ID");
        String eparchyCode = param.getString("EPARCHY_CODE");
        //IData results = initChgUs(userId,offerCode);//取老代码父类
        
        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        IDataset userVpnData = UserVpnInfoQry.qryUserVpnByUserId(userId);
        if(IDataUtil.isEmpty(userVpnData)){
            
        //    CSViewException.apperr(VpmnUserException.VPMN_USER_6, userId);
        }
        
        IData userVpnDatas = userVpnData.getData(0);
       
      
        
        // 特殊字段数据处理 vpn.FUNC_TLAGS vpn.CALL_NET_TYPE vpn.RSRV_TAG1 vpn.RSRV_TAG5
        // 分解拼串数据 动态表格值 start
        String defaultFunctlags = "1100000000000000000000000000000000000000";
        String functlags = userVpnDatas.getString("FUNC_TLAGS", "1100000000000000000000000000000000000000");
        if (functlags.length() < 40)
        {
            functlags = IDataUtil.replacStrByint(defaultFunctlags, functlags, 1, functlags.length());
        }

        
        IDataset dataChaSpec = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "2", null);
        if (IDataUtil.isNotEmpty(dataChaSpec))
        {
            for (int i = 0; i < dataChaSpec.size(); i++)
            {
                IData data1 = dataChaSpec.getData(i);
                String attrCode = data1.getString("ATTR_CODE");
                try
                {
                    int codeNumber = Integer.valueOf(attrCode.substring(attrCode.length() - 2));
                    if (codeNumber <= 40)
                    {
                        data1.put("ATTR_INIT_VALUE", functlags.substring(codeNumber - 1, codeNumber));
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
                data1.put("DEFAULT_VALUE", data1.getString("ATTR_INIT_VALUE"));
                dataChaSpec.set(i, data1);
            }
            
            transData = IDataUtil.hTable2STable(dataChaSpec, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        
        if (IDataUtil.isNotEmpty(transData))
        {
            Set<String> propNames = transData.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = transData.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                attrItem.put("DATA_VAL", workTypeCodeInfo);
                attrItem.put("ATTR_VALUE", attrItemValue);
                result.put(key, attrItem);
            }
        }
        
        // 分解拼串数据 动态表格值 end
        // VPMN表格参数
        // 分解拼串数据 呼叫网络类型 start
        String callnettype = userVpnDatas.getString("CALL_NET_TYPE", "");// 呼叫网络类型

        if (callnettype.length() >= 1)
        { // 内网
            String CALL_NET_TYPE1 = callnettype.substring(0, 1);
            userVpnDatas.put("CALL_NET_TYPE1", CALL_NET_TYPE1);
        }
        if (callnettype.length() >= 2)
        { // 网间
            String CALL_NET_TYPE2 = callnettype.substring(1, 2);
            userVpnDatas.put("CALL_NET_TYPE2", CALL_NET_TYPE2);
        }
        if (callnettype.length() >= 3)
        { // 网外
            String CALL_NET_TYPE3 = callnettype.substring(2, 3);
            userVpnDatas.put("CALL_NET_TYPE3", CALL_NET_TYPE3);
        }
        if (callnettype.length() >= 4)
        { // 网外号码组
            String CALL_NET_TYPE4 = callnettype.substring(3, 4);
            userVpnDatas.put("CALL_NET_TYPE4", CALL_NET_TYPE4);
        }
        
        userVpnDatas.put("VPN_CLASS", userVpnDatas.getString("RSRV_STR5", "")); // VPMN类别

        String vap_scare = userVpnDatas.getString("VPN_SCARE_CODE", ""); // 集团范围属性 2:跨省
        // IDataset vpnInfos = new DatasetList();
        IData custInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
        IData vpnparam = new DataMap();
        vpnparam.put("GROUP_ID", custInfo.getString("GROUP_ID"));
        vpnparam.put("RSRV_TAG1", "0");
        vpnparam.put("RSRV_TAG2", "0");
        IDataset vpninfos = new DatasetList();
        if (vap_scare.equals("2")) // 全国集团
        {
            vpnparam.put("VPN_NO", userVpnDatas.getString("VPN_NO", ""));
            vpninfos =GrpExtInfoQry.getVPNNOByVPNNO(vpnparam);
        }
        else
        {
            userVpnData.remove("VPN_NO");
            vpninfos =GrpExtInfoQry.getVPNNOByVPNNO(vpnparam);
        }
        
        if (IDataUtil.isNotEmpty(vpninfos))
        {
            int len = vpninfos.size();
            for (int i = 0; i < len; i++)
            {
                IData vpnInfoData = vpninfos.getData(i);
                String rsrvStr1 = vpnInfoData.getString("RSRV_STR1", "");
                String rsrvStr2 = vpnInfoData.getString("RSRV_STR2", "");
                String rsrvStr3 = vpnInfoData.getString("RSRV_STR3", "");
                vpnInfoData.put("DISPLAY_NAME", rsrvStr1 + "|" + rsrvStr2 + "|" + rsrvStr3);

            }
        }
        
        // 成员升级资费 start
        IDataset defualtDiscntset = CommparaInfoQry.getOnlyByAttr("CGM", "80", eparchyCode);
        
        if (IDataUtil.isEmpty(defualtDiscntset))
        {
           CSAppException.apperr(VpmnUserException.VPMN_USER_177);

        }
        IDataset defDiscntset = new DatasetList();
        String defaultDiscnt = "";
        String PROVICE_VPN_DISCNT = "";
        for (int i = 0; i < defualtDiscntset.size(); i++)
        {

            String Discnttag = defualtDiscntset.getData(i).getString("PARAM_CODE", "");
            String discntinfo = defualtDiscntset.getData(i).getString("PARA_CODE1", "");
            if (PROVICE_VPN_DISCNT.equals(""))
                PROVICE_VPN_DISCNT = discntinfo;
            else
                PROVICE_VPN_DISCNT = PROVICE_VPN_DISCNT + "," + discntinfo;
            if (Discnttag.equals("0"))
            {
                defaultDiscnt = discntinfo;
            }
            IData defDisData = new DataMap();
            defDisData.put("VALUE", discntinfo);
            defDisData.put("TEXT", defualtDiscntset.getData(i).getString("PARAM_NAME", ""));
            defDiscntset.add(defDisData);
        }
        
        IData defaultDiscntcode = new DataMap();
        defaultDiscntcode.put("DATA_VAL", defDiscntset);
        defaultDiscntcode.put("ATTR_VALUE", defaultDiscnt);
        result.put("DEFAULT_DISCNTCODE", defaultDiscntcode);
        
        //userVpnDatas.put("DEFAULT_DISCNTCODE", defaultDiscnt); // 成员升级资费
        // j2ee: 界面隐藏区域，用于下一步判断“非跨省集团，不能对跨省集团资费做操作”“跨省集团需要跨省资费，请选择跨省资费!”
        userVpnDatas.put("PROVICE_VPN_DISCNT", PROVICE_VPN_DISCNT);
        // 成员升级资费 end
        // 获取统一付费优惠 j2ee:没找到写入的地方 start
     
        IData data = UserInfoQry.getGrpUserInfoByUserId(userId, "0", eparchyCode);
        
        if (IDataUtil.isNotEmpty(data))
        {
            data.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(data.getString("EPARCHY_CODE")));
            data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID")));
            data.put("BRAND_NAME", UBankInfoQry.getBankNameByBankCode(data.getString("BRAND_CODE")));
        }
        IDataset datasetss = IDataUtil.idToIds(data);
        IData userInfo = new DataMap();
        String isModify = "";
        if (IDataUtil.isNotEmpty(datasetss))
        {
            userInfo = datasetss.getData(0);
            String grpPayDiscnt = userInfo.getString("RSRV_STR5", "");
            if (StringUtils.isNotBlank(grpPayDiscnt))
            {
                isModify = "YES";// 统一付费优惠已有资料（YES），则前台不让操作（disabled=true）
            }
        }
        userVpnDatas.put("GRP_PAY_DISCNT", userInfo.getString("RSRV_STR5", ""));
        String str7 = userInfo.getString("RSRV_STR7", "");
        if (StringUtils.isBlank(userVpnDatas.getString("CUST_MANAGER", "")) && StringUtils.isNotBlank(str7))
        {
            userVpnDatas.put("CUST_MANAGER", str7); // 分管客户经理
        }
        userVpnDatas.put("VPN_GRP_ATTR", userInfo.getString("RSRV_STR10", "")); // 集团属性
    
        // 获取统一付费优惠 ,变更才能选择 end

        // modify by lixiuyu@20101009 用户要求升级为跨省VPMN时默认选上"长、短号拨打"属性值为2
        userVpnDatas.put("DIAL_TYPE_CODE", userVpnDatas.getString("RSRV_TAG1", "2")); // 拨打方式 ,跨省VPMN才有插值

        IData userattritem = IDataUtil.iDataA2iDataB(userVpnDatas, "ATTR_VALUE"); // 转格式为可ognl:getAttrItemValue('CALL_AREA_TYPE','ATTR_VALUE')
       transComboBoxValue(userattritem, attrItemA);
        // add by xuecd 20100317 判读本集团能否订购跨省V网优惠，如果配置为空，则成员定制不能订购99720501-99720505的5个跨省资费
       
       if (IDataUtil.isNotEmpty(userattritem))
       {
           Set<String> propNames = userattritem.keySet();
           for (String key : propNames)
           {
               IData attrCodeInfo = userattritem.getData(key);
               IData attrItem = new DataMap();
               IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
             
               if (IDataUtil.isNotEmpty(workTypeCodeInfo))
               {
                   String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }
               else
               {
                   String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                   attrItem.put("DATA_VAL", attrItemValue);
                   result.put(key, attrItem);
               }
               if(key.equals("WORK_TYPE_CODE")){
                   String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }
               if ("CUST_MANAGER".equals(key))
               {
                   IData custMan = new DataMap();
                   IDataset managerInfos = StaffInfoQry.qryManagerIdJobType(custMan);
                   if (IDataUtil.isNotEmpty(managerInfos))
                   {
                       int len = managerInfos.size();
                       for (int i = 0; i < len; i++)
                       {
                           IData infoData = managerInfos.getData(i);
                           String rsrvStr1 = infoData.getString("CUST_MANAGER_NAME");
                           String rsrvStr2 = infoData.getString("CUST_MANAGER_ID");
                           infoData.put("CUST_MANAGER_NAME", rsrvStr1 + "|" + rsrvStr2);
                       }
                   }
                   String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   attrItem.put("DATA_VAL", managerInfos);
                   result.put(key, attrItem);
               }
               if("VPN_SCARE_CODE".equals(key)){
                   String attrItemValue = userVpnDatas.getString("VPN_SCARE_CODE");
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }

               if("SINWORD_TYPE_CODE".equals(key)){
                   String attrItemValue = userVpnDatas.getString("SINWORD_TYPE_CODE");
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }

               if("OVER_FEE_TAG".equals(key)){
                   String attrItemValue = userVpnDatas.getString("OVER_FEE_TAG");
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }

               if("CALL_AREA_TYPE".equals(key)){
                   String attrItemValue = userVpnDatas.getString("CALL_AREA_TYPE");
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }

               if("GRP_CLIP_TYPE".equals(key)){
                   String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                   //IDataset attrInfos = UserAttrInfoQry.getUserProductAttrValue(userId, "P", "GRP_CLIP_TYPE");
                   IDataset attrInfos = UserOtherInfoQry.getUserOtherInfoByAll(userId, "VPMN_GRPCLIP");
                   if (IDataUtil.isNotEmpty(attrInfos))
                   {
                	   IData attrInfo = attrInfos.getData(0);
                	   String atrrValue = attrInfo.getString("RSRV_STR1","");//GRP_CLIP_TYPE 呼叫来显方式
                	   if(StringUtils.isNotBlank(atrrValue))
                	   {
                		   attrItemValue = atrrValue;
                	   }
                   }
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }
               
               if("GRP_USER_CLIP_TYPE".equals(key)){
                   String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                   //IDataset attrInfos = UserAttrInfoQry.getUserProductAttrValue(userId, "P", "GRP_USER_CLIP_TYPE");
                   IDataset attrInfos = UserOtherInfoQry.getUserOtherInfoByAll(userId, "VPMN_GRPCLIP");
                   if (IDataUtil.isNotEmpty(attrInfos))
                   {
                	   IData attrInfo = attrInfos.getData(0);
                	   String atrrValue = attrInfo.getString("RSRV_STR1","");//GRP_CLIP_TYPE 呼叫来显方式
                	   String grpUserClipType = attrInfo.getString("RSRV_STR2","");//GRP_USER_CLIP_TYPE 选择号显方式
                	   if(StringUtils.isNotBlank(atrrValue) && StringUtils.equals("1", atrrValue))
                	   {
                		   attrItemValue = grpUserClipType;
                	   }
                	   else if(StringUtils.isNotBlank(atrrValue) && StringUtils.equals("0", atrrValue))
                	   {
                		   attrItemValue = "";
                	   }
                   }
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }
               
               if("GRP_USER_MOD".equals(key)){
                   String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                   //IDataset attrInfos = UserAttrInfoQry.getUserProductAttrValue(userId, "P", "GRP_USER_MOD");
                   IDataset attrInfos = UserOtherInfoQry.getUserOtherInfoByAll(userId, "VPMN_GRPCLIP");
                   if (IDataUtil.isNotEmpty(attrInfos))
                   {
                	   IData attrInfo = attrInfos.getData(0);
                	   String atrrValue = attrInfo.getString("RSRV_STR1","");//GRP_CLIP_TYPE 呼叫来显方式
                	   String grpUserMod = attrInfo.getString("RSRV_STR3","");//GRP_USER_MOD 成员修改号显方式
                	   if(StringUtils.isNotBlank(atrrValue) && StringUtils.equals("1", atrrValue))
                	   {
                		   attrItemValue = grpUserMod;
                	   }
                	   else if(StringUtils.isNotBlank(atrrValue) && StringUtils.equals("0", atrrValue))
                	   {
                		   attrItemValue = "";
                	   }
                   }
                   attrItem.put("DATA_VAL", workTypeCodeInfo);
                   attrItem.put("ATTR_VALUE", attrItemValue);
                   result.put(key, attrItem);
               }
           }
       }
       
        IData userInfoss = UcaInfoQry.qryUserInfoByUserId(userId);
        
        
        String serialNumber = "";
        if(IDataUtil.isNotEmpty(userInfoss)){
            
            serialNumber = userInfoss.getString("SERIAL_NUMBER");
        }
        
        
        String subsysCode = "CSM";
        String paramAttr = "119";
        String paramCode = "0";
        String paraCode1 = serialNumber;
        IDataset commParams = CommparaInfoQry.getCommparaInfoBy5(subsysCode, paramAttr, paramCode, paraCode1, eparchyCode, null);
        boolean canOrderDis = true;
        if (IDataUtil.isEmpty(commParams))
        {
            canOrderDis = false;
        }
        
        IData map = new DataMap();
        IDataset managerInfos = StaffInfoQry.qryManagerIdJobType(map);
        if (IDataUtil.isNotEmpty(managerInfos))
        {
            int len = managerInfos.size();
            for (int i = 0; i < len; i++)
            {
                IData infoData = managerInfos.getData(i);
                String rsrvStr1 = infoData.getString("CUST_MANAGER_NAME");
                String rsrvStr2 = infoData.getString("CUST_MANAGER_ID");
                infoData.put("CUST_MANAGER_NAME", rsrvStr1 + "|" + rsrvStr2);

            }
        }
        // SCPCODE start
        String scpCode = userVpnDatas.getString("SCP_CODE", ""); // SCP_CODE
        IDataset idatas = StaticUtil.getStaticList("TD_B_SCP", scpCode);
        IData data1 = new DataMap();
        IData scpdata = new DataMap();
        IDataset SCPInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(idatas))
        {
            data1 = idatas.getData(0);
            scpdata.put("DATA_NAME", data1.getString("DATA_NAME", ""));
            scpdata.put("DATA_ID", data1.getString("DATA_ID", ""));
        }
        SCPInfos.add(scpdata);
        // SCPCODE end
        // String str = queryStaffPriv(bp, data);
        String staffId = getVisit().getStaffId();

        parainfo.put("RIGHT_CODE", StaffPrivUtil.isFuncDataPriv(staffId, "SYS126"));
        parainfo.put("managerInfos", managerInfos);
        parainfo.put("vpnInfos", vpninfos);
        parainfo.put("userParamInfos", dataChaSpec); // 动态表格数据
        parainfo.put("SCPInfos", SCPInfos); // SCP代码
        parainfo.put("GRP_SN", data.getString("GRP_SN"));
        // parainfo.put("GRP_USER_ID", grpUserId);
        parainfo.put("defualtDiscntset", defDiscntset); // 跨省升级时，成员初始资费列表
        parainfo.put("canOrderDis", canOrderDis); // 判断该集团能否定制跨省5个资费
        parainfo.put("IS_MODIFY", isModify); // 统一付费优惠已有资料（YES），则前台不让操作（disabled=true）
        
        logger.debug("==============================数据{}", result);
        
        return result;

    }
    
    
    public static IData initChgUs(String userId,String offerCode) throws Exception{
        
        IData result = new DataMap();
        IDataset attrItemSet = new DatasetList();
        IData attrItem = new DataMap();
        // String eparchyCode = param.getString("EPARCHY_CODE");
        IDataset dataset = UserAttrInfoQry.getUserProductAttrByUTForGrp(userId, "P", null);
        
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
        attrItemSet = dataset;
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItem = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItem = new DataMap();
        }
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItem, pzAttrItem);
        }
        result.put("ATTRITEM", attrItem);
        result.put("ATTRITEMSET", attrItemSet);
        
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
    
}
