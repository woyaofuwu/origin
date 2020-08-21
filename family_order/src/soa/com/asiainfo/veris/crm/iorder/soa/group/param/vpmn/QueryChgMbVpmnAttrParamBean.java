package com.asiainfo.veris.crm.iorder.soa.group.param.vpmn;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryChgMbVpmnAttrParamBean  extends QueryAttrParamBean
{
   
    public static IData queryVpnParamAttrForChgInit(IData param) throws Exception
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
        
        IDataset pzAttrItems = UItemAInfoQry.queryOfferChaAndValByCond("8000", "P", "1", null);

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
        boolean shortCodeRight = false; // false:没有短号权限
        
        IDataset shortCodeRight1 = StaffInfoQry.queryGrpRightByIdCode(staffId, rightCode, userProductCode);
        if (IDataUtil.isNotEmpty(shortCodeRight1))
        {
            IDataset shortCodeRight2 = DataHelper.filter(shortCodeRight1, "STAFF_ID=" + staffId);
            if (IDataUtil.isNotEmpty(shortCodeRight2))
            {
                shortCodeRight = true;
            }
        }
        else
        {// 没有短号权限限制就视为有短号权限
            shortCodeRight = true;
        }
        if ("SUPERUSR".equals(staffId) || shortCodeRight)
        {
            parainfo.put("RIGHT_CODE_chg", "yes");
        }
     
     // 查询用户VPN信息
        IDataset userVpnData = UserVpnInfoQry.qryUserVpnByUserId(ecUserId);
        if(IDataUtil.isEmpty(userVpnData)){
            
            userVpnData = new DatasetList();
        }
        
        IData userVpnDatas = userVpnData.getData(0);
        
        String vpn_scare_code = userVpnDatas.getString("VPN_SCARE_CODE", "");
    
        IDataset defualtDiscntset = ParamInfoQry.getCommparaByCode("CGM", "80", null, eparchyCode);
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
     
        IData userattrinfo = UserVpnInfoQry.getMemberVpnByUserId(userId, ecUserId, eparchyCode);

        // 防止资源表与VPN_MEB中短号不一致，导致变更不删除原有资源，短号从资源表获取。
  
        IDataset userresinfo = UserResInfoQry.getUserResByUserIdA(userId, ecUserId);
        if (IDataUtil.isNotEmpty(userresinfo))
        {
            IData userres = userresinfo.getData(0);
            userattrinfo.put("SHORT_CODE", userres.getString("RES_CODE", ""));

        }
        
        IDataset attrInfos = UserOtherInfoQry.getUserOtherInfoByAll(ecUserId, "VPMN_GRPCLIP");
        if (IDataUtil.isNotEmpty(attrInfos))
        {
        	IData attrInfo = attrInfos.getData(0);
        	if(IDataUtil.isNotEmpty(attrInfos))
     	   	{
        		String grpClipType = attrInfo.getString("RSRV_STR1","");//GRP_CLIP_TYPE 呼叫来显方式
        		String grpUserClipType = attrInfo.getString("RSRV_STR2","");//GRP_USER_CLIP_TYPE 选择号显方式
        		String grpUserMod = attrInfo.getString("RSRV_STR3","");//GRP_USER_MOD 成员修改号显方式
        		userattrinfo.put("GRP_CLIP_TYPE", grpClipType);
        		userattrinfo.put("GRP_USER_CLIP_TYPE", grpUserClipType);
        		userattrinfo.put("GRP_USER_MOD", grpUserMod);
     	   	}
        }
        
        IDataset mebOtherInfos = UserOtherInfoQry.getUserOtherInfoByAll(userId, "VPMN_MEBCLIP");
        if(IDataUtil.isNotEmpty(mebOtherInfos))
        {
        	IData mebOtherInfo = mebOtherInfos.getData(0);
        	String rsrvValue = mebOtherInfo.getString("RSRV_VALUE","");
        	userattrinfo.put("CLIP_TYPE", rsrvValue);
        	userattrinfo.put("OLD_CLIP_TYPE", rsrvValue);
        }
        
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
