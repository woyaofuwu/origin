package com.asiainfo.veris.crm.iorder.soa.group.param.jwtvpn;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class QueryChgMbJwtVpnAttrParamBean  extends QueryAttrParamBean
{
   
    public static IData queryJwtVpnAttrForChgInit(IData param) throws Exception
    {
        IData result = new DataMap();
        IData attrItemA = new DataMap();
        //IData transData = new DataMap();
        IData parainfo = new DataMap();
        String ecUserId = param.getString("EC_USER_ID");
        String userId = param.getString("USER_ID");
       // String offerCode = param.getString("OFFER_CODE");
        String eparchyCode = param.getString("EPARCHY_CODE");
        //IData results = initChgUs(userId,offerCode);//取老代码父类
        IDataset dataset =  UserAttrInfoQry.getUserProductAttrByUserIdAndUserIdA(userId, ecUserId, "P");
        
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
        
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");

        }
        
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond("6200", "P", "1", null);

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItemA, pzAttrItem);
        }
        
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(ecUserId);
        String userProductCode = userInfo.getString("SERIAL_NUMBER");
        String staffId = getVisit().getStaffId();
        String rightCode = "VPN_SHORT_CODE";
        boolean shortCodeRight = false; // false:有短号权限限制
        // 成员短号操作权限：先判断该VPMN集团是否有短号权限限制
        IData inparam = new DataMap();
        inparam.put("RIGHT_CODE", rightCode);
        inparam.put("USER_PRODUCT_CODE", userProductCode);
        IDataset shortCodeRight1 = CSAppCall.call("CS.StaffInfoQrySVC.queryGrpRightByIdCode", inparam);
        // 有短号权限限制，再判断该员工是否有权限
        if (IDataUtil.isNotEmpty(shortCodeRight1))
        {
            IDataset shortCodeRight2 = DataHelper.filter(shortCodeRight1, "STAFF_ID=" + staffId);
            if (IDataUtil.isNotEmpty(shortCodeRight2))
            {
                shortCodeRight = true;
            }
        }
        else
        {// 没有短号权限限制就跳过
            shortCodeRight = true;
        }
        if ("SUPERUSR".equals(staffId) || shortCodeRight)
        {
            parainfo.put("RIGHT_CODE_chg", "yes");
        }
        
        //IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, grpUserId, false);
        IData svcData = new DataMap();
        svcData.put("USER_ID", ecUserId);
        IData userVpnData = CSAppCall.callOne("CS.UserVpnInfoQrySVC.qryUserVpnByUserId", svcData);
        IData vpninfo = new DataMap();
        if (IDataUtil.isNotEmpty(userVpnData))
        {
            vpninfo = userVpnData;
        }
        String vpn_scare_code = vpninfo.getString("VPN_SCARE_CODE", "");
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CGM");
        iparam.put("PARAM_ATTR", "80");
        iparam.put("PARAM_CODE", null);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset defualtDiscntset = CSAppCall.call("CS.ParamInfoQrySVC.getCommparaByCode", iparam);
        String PROVICE_VPN_DISCNT = "";
        if (IDataUtil.isNotEmpty(defualtDiscntset))
        {
            for (int i = 0; i < defualtDiscntset.size(); i++)
            {
                String discntinfo = defualtDiscntset.getData(i).getString("PARA_CODE1", "");
                if ("".equals(PROVICE_VPN_DISCNT))
                    PROVICE_VPN_DISCNT = discntinfo;
                else
                    PROVICE_VPN_DISCNT = PROVICE_VPN_DISCNT + "," + discntinfo;
            }
        }
        parainfo.put("VPN_SCARE_CODE", vpn_scare_code);
        parainfo.put("PROVICE_VPN_DISCNT", PROVICE_VPN_DISCNT);
        //String memEparchyCode = param.getString("MEB_EPARCHY_CODE", "");
        inparam.clear();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", ecUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IData userattrinfo = CSAppCall.callOne("CS.UserVpnInfoQrySVC.getMemberVpnByUserId", inparam);

        // 防止资源表与VPN_MEB中短号不一致，导致变更不删除原有资源，短号从资源表获取。	
        inparam.clear();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", ecUserId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
        IDataset userresinfo = CSAppCall.call("CS.UserResInfoQrySVC.getUserResByUserIdA", inparam);
        if (IDataUtil.isNotEmpty(userresinfo))
        {
            IData userres = userresinfo.getData(0);
            userattrinfo.put("SHORT_CODE", userres.getString("RES_CODE", ""));

        }
        userattrinfo.putAll(parainfo);
       
        IData userattritem = IDataUtil.iDataA2iDataB(userattrinfo, "ATTR_VALUE");

        transComboBoxValue(userattritem,attrItemA);
     
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
    
}
