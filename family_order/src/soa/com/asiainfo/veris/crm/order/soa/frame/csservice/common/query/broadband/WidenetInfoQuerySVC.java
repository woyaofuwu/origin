
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoSVC;

public class WidenetInfoQuerySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询产品中的所有元素
     * 
     * @author chenzm
     * @param input
     * @return
     * @throws Exception
     */
    public IData getElementByProduct(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String tradeStaffId = input.getString("TRADE_STAFF_ID");
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
        if (IDataUtil.isEmpty(widenetInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_4);
        }
//        IDataset packageList = ProductInfoQry.getPackagesByProductId(productId, eparchyCode);
//
//        if (input.getString("PRIV_FOR_PACK", "").toLowerCase().equals("true"))
//        {
//            PackagePrivUtil.filterPackageListByPriv(tradeStaffId, packageList);
//        }
        IDataset servTempElements = new DatasetList();
        IDataset discntTempElements = new DatasetList();
      
//        PkgElemInfoQry.getPackageElementByPackageId(packageId);
        
        IDataset elemetTempElements =  new QueryInfoSVC().qryProductElementsInft(input);
        
        if (input.getString("PRIV_FOR_ELE", "").toLowerCase().equals("true"))
        {
            ElementPrivUtil.filterElementListByPriv(tradeStaffId, elemetTempElements);
        }

        for (int i = 0; i < elemetTempElements.size(); i++)
        {
            IData elementData = elemetTempElements.getData(i);
            String elementTypeCode = elementData.getString("ELEMENT_TYPE_CODE");
            if ("S".equals(elementTypeCode))
            {
                servTempElements.add(elementData);
                continue;
            }
            if ("D".equals(elementTypeCode))
            {
                discntTempElements.add(elementData);
            }

        }

        IData returnSet = new DataMap();

        returnSet.put("SERV_ELEMENTS", servTempElements);
        returnSet.put("DISCNT_ELEMENTS", discntTempElements);

        return returnSet;

    }

    /**
     * 获取宽带资料
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserWidenetInfo(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_3);
        }
        return dataset;
    }

    public IDataset getWidenetProductInfo(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String eparchyCode = input.getString("EPARCHY_CODE");
        String tradeStaffId = input.getString("TRADE_STAFF_ID");

        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
        if (IDataUtil.isEmpty(widenetInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_4);
        }
        String productMode = "";
        String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
        if ("2".equals(wideType))
        {
            productMode = "09";// 针对adsl
        }
        else if ("3".equals(wideType))
        {
            productMode = "11";// 针对光纤
        }
        else
        {
            productMode = "07";// 针对gpon
        }
        IDataset widenetProductInfos = ProductInfoQry.getWidenetProductInfo(productMode, eparchyCode);
        ProductPrivUtil.filterProductListByPriv(tradeStaffId, widenetProductInfos);
        return widenetProductInfos;
    }

    /**
     * 宽带业务开通状态查询
     * 
     * @author chenzm
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryWideNetStatus(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
        if (IDataUtil.isEmpty(widenetInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_4);
        }
        String userId = widenetInfos.getData(0).getString("USER_ID");
        IDataset datasetsvcstate = UserSvcStateInfoQry.getUserValidSvcStateByUserId(userId);
        if (IDataUtil.isEmpty(datasetsvcstate))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_23);
        }
        IData usersvcstatedata = new DataMap();
        // 判断宽带业务是否已经办理，如果已经办理，返回服务当前状态
        String service_id = "";
        String userStatCodeSet = "";
        String startDate = "";
        boolean flag_state = false;
        boolean bookIng_state = false;
        for (int i = 0; i < datasetsvcstate.size(); i++)
        {
            service_id = datasetsvcstate.get(i, "SERVICE_ID").toString();
            userStatCodeSet = datasetsvcstate.get(i, "STATE_CODE").toString();
            startDate = datasetsvcstate.get(i, "START_DATE").toString();
            if (("2010".equals(service_id) || "2019".equals(service_id) || "2020".equals(service_id) || "2025".equals(service_id)) && startDate.compareTo(SysDateMgr.getSysTime()) < 0)
            {
                flag_state = true;
                usersvcstatedata = datasetsvcstate.getData(i);
                String stateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode(service_id, userStatCodeSet);
                usersvcstatedata.put("STATE_NAME", stateName);
            }
            else if (("2010".equals(service_id) || "2019".equals(service_id) || "2020".equals(service_id) || "2025".equals(service_id)) && startDate.compareTo(SysDateMgr.getSysTime()) > 0)
            {
                bookIng_state = true;
            }
        }
        if (!flag_state)
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_24);
        }

        if (bookIng_state)
        {
            usersvcstatedata.put("STATE_CODE", "S");
            usersvcstatedata.put("STATE_NAME", "预约报停");
        }
        return usersvcstatedata;
    }
    
    /**
     * 查询号码的宽带信息
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset queryUserWideNetInfo(IData input)throws Exception{
    	IDataset userInfo = WidenetInfoQry.getUserInfo(input.getString("SERIAL_NUMBER"));
    	if(IDataUtil.isNotEmpty(userInfo)){
    		 String userId = userInfo.getData(0).getString("USER_ID");
    		 IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
    		 
    		 if(IDataUtil.isNotEmpty(dataset)){
    			 String rsrvStr2=dataset.getData(0).getString("RSRV_STR2","");
    			 if(rsrvStr2.equals("1")||
    					 rsrvStr2.equals("2")||rsrvStr2.equals("3")||rsrvStr2.equals("5")){
    				 return dataset;
    			 }else{
    				 return null;
    			 }
    		 }else{
    			 return null;
    		 }
    	}else{
    		return null;
    	}
    	
    }
    
    /**
     * 查询号码的宽带信息
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset queryUserWideNetBySnInfo(IData input)throws Exception{
    	IDataset userInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(input.getString("SERIAL_NUMBER"));
    	if(IDataUtil.isNotEmpty(userInfo)){
    		 String userId = userInfo.getData(0).getString("USER_ID");
    		 IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
    		 
    		 if(IDataUtil.isNotEmpty(dataset)){
    				 return dataset;
			 }else{
				 return null;
			 }
    	}else{
    		return null;
    	}
    	
    }
	/**
	 * REQ201708240014_家庭IMS固话开发需求
	 * <br/>
	 * 判断IMS用户是否是宽带报停状态,手机号码状态是否正常,进来这里一定是IMS家庭固话了
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData checkWidnetStopIMS(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        //0：校验通过，1：校验报错
        retrunData.put("RESULT_CODE", "0");
        retrunData.put("RESULT_INFO", "校验正常！");
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        //主号信息
        IData  mainInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(mainInfo)){
        	String mainState=mainInfo.getString("USER_STATE_CODESET","");
        	if(!"0".equals(mainState)){
				retrunData.put("RESULT_CODE", "-1");
				retrunData.put("RESULT_INFO", "手机号码状态不正常，不能关联开机！");
        	}
        }
        
        IData  param=new DataMap();
        	   param.put("SERIAL_NUMBER", serialNumber);
        //获取ims帐号信息	   
        IData  userBInfo=CSAppCall.callOne("SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", param);
        if(IDataUtil.isNotEmpty(userBInfo)){
        	//获取IMS手机号码
        	String serialNumberIMS = userBInfo.getString("SERIAL_NUMBER_B","");
        	IData imsInfo = UcaInfoQry.qryUserInfoBySn(serialNumberIMS);
			if (!"1".equals(imsInfo.getString("USER_STATE_CODESET"))) {
				retrunData.put("RESULT_CODE", "-1");
				retrunData.put("RESULT_INFO", "不是宽带停机状态，不能关联开机！");
			}
        }
        return retrunData;
    }    
}
