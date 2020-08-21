
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatGrpMemCentrexVpnCreateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String userId = IDataUtil.getMandaData(condData, "USER_ID"); // 集团用户ID

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验短号
        String shortCode = batData.getString("DATA1", ""); // 短号
        if (StringUtils.isBlank(shortCode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_603, serialNumber);
        }
        
        //查询成员用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serialNumber);
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        String netTypeCode = userinfo.getString("NET_TYPE_CODE");
        
        //重新过滤一下重复的号码
        String batchId = IDataUtil.chkParam(batData, "BATCH_ID");   
        if(StringUtils.isNotBlank(batchId))
        {
        	IData paramMap = new DataMap();
        	paramMap.put("SERIAL_NUMBER", serialNumber);
        	paramMap.put("BATCH_ID", batchId);
        	IDataset resultset = BatDealInfoQry.queryBatDealInfoCntForImsGrp(paramMap);
        	if(IDataUtil.isNotEmpty(resultset))
        	{
        		int cnt = resultset.getData(0).getInt("CNT");
        		if(cnt>1)
        		{
        			CSAppException.apperr(GrpException.CRM_GRP_713, "此次批量业务中存在重复服务号码!!");
        		}
        	}
        	paramMap.clear();
        	paramMap.put("SHORT_CODE", shortCode);
        	paramMap.put("BATCH_ID", batchId);
        	IDataset shortResult = BatDealInfoQry.queryShortCodeCntForImsGrp(paramMap);
        	if(IDataUtil.isNotEmpty(shortResult))
        	{
        		int cnt = shortResult.getData(0).getInt("CNT");
        		if(cnt>1)
        		{
        			CSAppException.apperr(GrpException.CRM_GRP_713, "此次批量业务中存在重复短号码!!");
        		}
        	}
        }
        
        
        IDataset defaultDiscntset = CommparaInfoQry.getOnlyByAttr("CGM","8001","0898");
        if (IDataUtil.isEmpty(defaultDiscntset))
        {
        	CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_3);
        }
        String IMS_VPN_DISCNT_00 = "";
        String IMS_VPN_DISCNT_05 = "";
        for (int i = 0, size = defaultDiscntset.size(); i < size; i++)
        {
            IData defaultDiscnt = defaultDiscntset.getData(i);
            String discntinfo = defaultDiscnt.getString("PARA_CODE1", "");
            String nettypecode = defaultDiscnt.getString("PARAM_CODE", "");
            if ("00".equals(nettypecode))
            {
                if (StringUtils.isBlank(IMS_VPN_DISCNT_00))
                {
                	IMS_VPN_DISCNT_00 = discntinfo;
                }
                else
                {
                	IMS_VPN_DISCNT_00 = IMS_VPN_DISCNT_00 + "," + discntinfo;
                }
            }
            else if ("05".equals(nettypecode))
            {
                if (StringUtils.isBlank(IMS_VPN_DISCNT_05))
                {
                	IMS_VPN_DISCNT_05 = discntinfo;
                }
                else
                {
                	IMS_VPN_DISCNT_05 = IMS_VPN_DISCNT_05 + "," + discntinfo;
                }
            }
        }
        
        //融合V网成员新增时,成员优惠的选择,是分手机优惠和固话优惠
        boolean exist00Discnt = false;//手机优惠
        boolean exist05Discnt = false;//固话优惠
        boolean exist862Svc = false;//862服务
        String discntCode = "";
        IDataset elementInfos = condData.getDataset("ELEMENT_INFO");
        if(IDataUtil.isNotEmpty(elementInfos))
        {
        	for (int i = 0, iSize = elementInfos.size(); i < iSize; i++)
            {
                IData element = elementInfos.getData(i);
                String elementId = element.getString("ELEMENT_ID", "");
                String elementType = element.getString("ELEMENT_TYPE_CODE", "");
                if("D".equals(elementType) && StringUtils.isNotBlank(elementId))
                {
                	discntCode = elementId;
                	if(IMS_VPN_DISCNT_00.contains(elementId))//手机优惠
                	{
                		exist00Discnt = true;
                	}
                	else if(IMS_VPN_DISCNT_05.contains(elementId))//固话优惠
                	{
                		exist05Discnt = true;
                	}
                }
                else if("S".equals(elementType) && "862".equals(elementId))
                {
                	exist862Svc = true;
                }
            }
        }
        
        if("05".equals(netTypeCode))//固话
        {
        	if(exist00Discnt)
        	{
        		String errMessage = "IMS固话号，不能对手机资费做操作，资费编码:"+discntCode;
        		CSAppException.apperr(GrpException.CRM_GRP_713, errMessage);
        	}
        	if(exist862Svc)
        	{
        		String errMessage = "固话号码不能选择862服务!";
        		CSAppException.apperr(GrpException.CRM_GRP_713, errMessage);
        	}
        }
        else if("00".equals(netTypeCode))//手机
        {
        	if(exist05Discnt)
        	{
        		String errMessage = "手机号，不能对IMS固话资费做操作，资费编码:"+discntCode;
        		CSAppException.apperr(GrpException.CRM_GRP_713, errMessage);
        	}
        }
        
        batData.put("SHORT_CODE", shortCode);
        batData.put("USER_ID", userId);
        if (!GroupImsUtil.checkImsVpnShortCode(batData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_502, batData.getString("ERROR_MESSAGE"));
        }

        // 资源信息
        IData resData = new DataMap();
        resData.put("RES_TYPE_CODE", "S");
        resData.put("CHECKED", "true");
        resData.put("DISABLED", "true");
        resData.put("RES_CODE", shortCode);
        resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 产品参数信息
        IData productParam = new DataMap();
        productParam.put("SHORT_CODE", shortCode);
        
        String callDispMode = condData.getString("CALL_DISP_MODE", "");
        if(StringUtils.isNotEmpty(callDispMode))
        {
            productParam.put("CALL_DISP_MODE", callDispMode);
        }
        
        if("05".equals(netTypeCode))
        {
        	String outerCall = condData.getString("OuterCall", "");
            if(StringUtils.isNotEmpty(outerCall))
            {
                productParam.put("OuterCall", outerCall);
            }
        }

        IDataset productParamDataset = new DatasetList();
        GroupBatTransUtil.buildProductParam(batData.getString("PRODUCT_ID"), productParam, productParamDataset);

        svcData.put("RES_INFO", IDataUtil.idToIds(resData));
        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

}
