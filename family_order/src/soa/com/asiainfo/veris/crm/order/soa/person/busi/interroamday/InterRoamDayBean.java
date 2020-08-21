
package com.asiainfo.veris.crm.order.soa.person.busi.interroamday;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.InterRoamDayException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AirlinesInterRoamUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class InterRoamDayBean extends CSBizBean
{
	private static Logger logger = Logger.getLogger(InterRoamDayBean.class);
    /**
     * 根据user_id获取用户国漫服务信息
     */
    public void checkUserInterRoamSvcInfo(IData userInfo) throws Exception
    {
        String userId = userInfo.getString("USER_ID");
        IDataset svcInfos = UserSvcInfoQry.getSvcUserId(userId, PersonConst.INTER_ROAM_SVC);
        if (svcInfos == null || svcInfos.size() <= 0)
        {
            CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_1);
        }

        IDataset elems = UserDiscntInfoQry.getUserDiscntByUserAndElementId(userId, PersonConst.INTER_ROAM_DAY_PACKAGE_ID, "", "D");
        if (DataSetUtils.isNotBlank(elems))
        {
            for (int i = 0, s = elems.size(); i < s; i++)
            {
                IData elem = elems.getData(i);

                if (elem.getString("END_DATE").substring(0, 10).equals(SysDateMgr.getSysDate()))
                {
                    CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_2);
                }
            }
        }

    }
    
    
    /**
     * 根据user_id获取用户国漫服务信息
     */
    public void checkUserInterRoamSvcInfoIsOrder(IData userInfo) throws Exception
    {
        String userId = userInfo.getString("USER_ID");
//        IDataset elems = UserDiscntInfoQry.queryInvalidUserDiscntInPackage(userId, PersonConst.INTER_ROAM_DAY_PACKAGE_ID, "D");
        IDataset elems =new DatasetList();
       //查询包下所有优惠
		IDataset  upackageEleInfos=UPackageElementInfoQry.getElementInfoByGroupId(PersonConst.INTER_ROAM_DAY_PACKAGE_ID);
        
      //获取用户的所有的优惠
		IDataset  userDiscntsInfos= UserDiscntInfoQry.getAllValidDiscntByUserId(userId);//获取用户的优惠
		
		//循环获取用户已订购的国漫优惠信息
        for(int j=0; j<userDiscntsInfos.size();j++){
 	       IData temp = userDiscntsInfos.getData(j);
 	      for(int k=0; k<upackageEleInfos.size();k++){
 	    	     String  prarmdiscode=upackageEleInfos.getData(k).getString("ELEMENT_ID");
 	    	     if(temp.getString("DISCNT_CODE").equals(prarmdiscode)){
// 	    	    	 temp.putAll(upackageEleInfos.getData(k));	    	 
 	    	    	 elems.add(temp);
 	    	     }
 	      }
       }
        if (DataSetUtils.isNotBlank(elems))
        {
            for (int i = 0, s = elems.size(); i < s; i++)
            {
                IData elem = elems.getData(i);

                if (elem.getString("END_DATE").substring(0, 10).equals(SysDateMgr.getSysDate()))
                {
                    CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_2);
                }
            }
        }

    }
    

    /**
     * 计算取消优惠的失效时间（页面后台共用）
     */
    public IData geneDelDiscntDate(IData params) throws Exception
    {
        String serial_number = params.getString("SERIAL_NUMBER");
        UcaDataFactory.getNormalUca(serial_number);
         
        
        IData returnInfo = new DataMap();
        params.put("PRODUCT_ID", "-1");
        params.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
        DiscntData discntData = new DiscntData(params);
        String endDate = ProductModuleCalDate.calCancelDate(discntData, null);
        returnInfo.put("END_DATE", endDate);

        return returnInfo;
    }

    /**
     * 计算新增优惠的生失效时间（页面后台共用）
     */
    public IData geneNewDiscntDate(IData params) throws Exception
    {
        IData returnInfo = new DataMap();

        String serial_number = params.getString("SERIAL_NUMBER");
        UcaDataFactory.getNormalUca(serial_number);

        /*
         * if(DataSetUtils.isNotBlank(userinfo)){ UcaData uca =
         * UcaDataFactory.getUcaByUserId(userinfo.getData(0).getString("USER_ID")); AcctTimeEnv env = new
         * AcctTimeEnv(uca.getAcctDay(), "", "", ""); AcctTimeEnvManager.setAcctTimeEnv(env); }
         */
//        params.put("PRODUCT_ID", "-1");
        params.put("PRODUCT_ID", UcaDataFactory.getNormalUca(serial_number).getProductId());
        params.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        
        params.put("PACKAGE_ID", PersonConst.INTER_ROAM_DAY_PACKAGE_ID);
        DiscntData discntData = new DiscntData(params);
        if ("1".equals(params.getString("CHOOSE_FLAG", "2")))// 可选账期，前台传了立即
            discntData.setEnableTag("0");

        String startDate = ProductModuleCalDate.calStartDate(discntData, null);
        String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);

        returnInfo.put("START_DATE", startDate);
        returnInfo.put("END_DATE", endDate);
        return returnInfo;
    }

    /**
     * 根据user_id获取国漫优惠信息
     */
    public IDataset getInterRoamDayDiscntInfo(IData userInfo) throws Exception
    {
    	
        String userId = userInfo.getString("USER_ID");
        UcaDataFactory.getUcaByUserId(userId);
//        IDataset infos = UserDiscntInfoQry.getRoamDiscntByPackageId(userId, PersonConst.INTER_ROAM_DAY_PACKAGE_ID, eparchyCode);
        //add by hefeng
      IDataset infos =new DatasetList();
     //查询包下所有优惠    
		IDataset  upackageEleInfos=UPackageElementInfoQry.getElementInfoByGroupId(PersonConst.INTER_ROAM_DAY_PACKAGE_ID);
    //获取用户的所有的优惠
		IDataset  userDiscntsInfos= UserDiscntInfoQry.getAllValidDiscntByUserId(userId);//获取用户的优惠
		//循环获取用户已订购的国漫优惠信息
      for(int j=0; j<userDiscntsInfos.size();j++){ 	  
	       IData temp = userDiscntsInfos.getData(j);
	      for(int k=0; k<upackageEleInfos.size();k++){
	    	  IData tempdis=new DataMap();
	    	     String  prarmdiscode=upackageEleInfos.getData(k).getString("DISCNT_CODE");
	    	     
	    	     if(!"2".equals(upackageEleInfos.getData(k).getString("RSRV_TAG1","0")) && !"3".equals(upackageEleInfos.getData(k).getString("RSRV_TAG1","0")) ){
	    	    	 
			    	     if(temp.getString("DISCNT_CODE").equals(prarmdiscode)){
			    	    	 upackageEleInfos.getData(k).put("CHECKED", "true");
			    	    	 upackageEleInfos.getData(k).put("OLD_CHECKED", "true");
			    	    	 tempdis.putAll(upackageEleInfos.getData(k));
			    	    	 tempdis.put("INST_ID", temp.getString("INST_ID"));
			    	    	 tempdis.put("OLD_START_DATE", temp.getString("START_DATE"));
			    	    	 tempdis.put("OLD_END_DATE", temp.getString("END_DATE"));
			    	    	 tempdis.put("START_DATE", temp.getString("START_DATE"));
			    	    	 tempdis.put("END_DATE", temp.getString("END_DATE"));
			    	    	 infos.add(tempdis);
			    	    	 upackageEleInfos.remove(k);
			    	    	    	 
			    	     }else{
			    	         
			    	    	 upackageEleInfos.getData(k).put("CHECKED", "false");
			    	    	 upackageEleInfos.getData(k).put("OLD_CHECKED", "false");
			    	    	 tempdis.putAll(upackageEleInfos.getData(k));
			    	    	 tempdis.put("INST_ID", "0");
			    	    	 tempdis.put("OLD_START_DATE", tempdis.getString("START_DATE"));
			    	    	 tempdis.put("OLD_END_DATE", tempdis.getString("END_DATE"));
			    	         infos.add(tempdis);
			    	         upackageEleInfos.remove(k);
			    	     }
	    	     } 
	      } 
     }
        for (int i = 0; i < infos.size(); i++)
        {
            // 如果优惠结束日期等于取消该优惠的结束日期 不能被再次取消
            IData data = infos.getData(i);
            data.put("MODIFY_TAG", "1");

            if (data.getString("CHECKED").equals("true"))
            { // 处理用户已订购业务
                DiscntData discntData = new DiscntData(data);
                String cancelTag = discntData.getCancelTag();
                if ("4".equals(cancelTag))
                {
                    data.put("DISABLED_CANCEL", "1");
                }
                else
                {
                    String cancelDate = ProductModuleCalDate.calCancelDate(discntData, null).substring(0, 10);

                    if (data.getString("END_DATE", "").equals(cancelDate))
                    {
                        data.put("DISABLED_CANCEL", "1");
                    }
                }
            }
        }
        return infos;
    
    	}

    /**
     * 根据user_id获取用户资源信息
     */
    public IData getUserRes(IData inparam) throws Exception
    {
        String userId = inparam.getString("USER_ID");
        String resTypeCode = "1";
        IDataset datas = UserResInfoQry.queryUserSimInfo(userId, resTypeCode);
        return datas != null && datas.size() > 0 ? datas.getData(0) : new DataMap();
    }
    
    
    
    /**
	 * 开通/变更国漫日套餐
	 * @Title : openSer
	 * @Description:TODO
	 * @Param : @param input
	 * @Param : @return
	 * @Param : @throws Exception
	 * @return: IDataset
	 */
	public IDataset openSer(IData input) throws Exception{
		
		this.checkState(input);
		
		IData data = new DataMap();
		
		IDataset dataset = new DatasetList();
		
		IDataset datas1 = new DatasetList();
		
		data = changeData(input);
		
		dataset = CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", data);
		
		return dataset;
	}
	
	
	/**
	 * 客户有效性校验
	 * @Title : checkState
	 * @Description:TODO
	 * @Param : @param input
	 * @return: void
	 * @throws Exception 
	 */
	private void checkState(IData data) throws Exception{
		
		String sn = data.getString("SERIAL_NUMBER");
		
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String userid = userInfo.getString("USER_ID");
        String identCode = data.getString("IDENT_CODE");
		String contactId = data.getString("CONTACT_ID");
		
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
		
	}
	
	
	/**
	 * @Title : changeData
	 * @Description:TODO
	 * @Param : @param input
	 * @Param : @return
	 * @return: IData
	 * @throws Exception 
	 */
	public IData changeData(IData input) throws Exception{
		
		IData result = new DataMap();
		
		IDataset dataset = new DatasetList();
		
		String packageName = input.getString("PACKAGE_NAME");
		
		String packageCode = input.getString("PACKAGE_CODE");
		
		String identCode = input.getString("IDENT_CODE");
		
		String cmdCode = input.getString("CMD_CODE");   //1：开通；2：修改
		
		String packOldname = input.getString("PACK_OLDNAME");
		
		String packOldcode = input.getString("PACK_OLDCODE");
		
		String relogtag = input.getString("relogtag","");
		
		String effectType = input.getString("EFFECT_MODE"); //1：立即；2：次日；3：次月
		
		String serial_number = input.getString("SERIAL_NUMBER");
		
		result.put("MODIFY_TAG", "0");
		
		if("2".equals(cmdCode)){  //修改时，第一次走的节点
			
			packageCode = packOldcode;
		
			result.put("MODIFY_TAG", "1");
		}
		
		dataset = CommparaInfoQry.getCommparaByCode2("CSM", "2789", "99990000",packageCode,"ZZZZ");  //根据套餐代码查询本省套餐
		
		if(IDataUtil.isEmpty(dataset)){
			
			CSAppException.apperr(ParamException.CRM_PARAM_292);
			
		}
		
		String discnt_code = dataset.getData(0).getString("PARA_CODE1");   //获取到优惠编码
		 String packageId = dataset.getData(0).getString("PARAM_CODE");
		result.put("ELEMENT_ID", discnt_code);
		result.put("SERIAL_NUMBER", serial_number);
		result.put("ELEMENT_TYPE_CODE", "D");
		
		IDataset elem = BofQuery.getServElementByPk(packageId, discnt_code, "D");
		
		if("1".equals(effectType)){//立即
			result.put("RSRV_STR1", effectType);
		}else if("2".equals(effectType)){//次日
			result.put("RSRV_STR1", "2");
			result.put("ABSOLUTE_START_DATE", SysDateMgr.getTomorrowDate());
		}else if("3".equals(effectType)){//次月
			result.put("RSRV_STR1", "0");
		}
		IData dataInfo = new DataMap();
        dataInfo.put("SERIAL_NUMBER", serial_number);
        dataInfo.put("ELEMENT_ID", discnt_code);
        dataInfo.put("ELEMENT_TYPE_CODE", "D");
        dataInfo.put("MODIFY_TAG", "1".equals(cmdCode)?"0":"1");
        
        dataInfo.put("SELECTED_ELEMENTS", elem);
       
		return dataInfo ;
	}
	
	 //根据package_id和element_id从配置表中获取国漫配置的优惠信息
    public IData getPackageDiscnt(String packageid, String elementId) throws Exception
    {

        String elementTypeCode = "D";
        IDataset tmp = PkgElemInfoQry.getServElementByPk(packageid, elementTypeCode, elementId);
        return tmp != null && tmp.size() > 0 ? tmp.getData(0) : new DataMap();
    }
    
    // 根据user_id和discnt_code从用户优惠表中获取优惠信息  
    public int existRoamEighteenDiscnt(String userId, String discntCode) throws Exception
    {
    	IData disParam = new DataMap();
		disParam.put("USER_ID", userId);
		disParam.put("DISCNT_CODE", discntCode);
		IDataset disInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_BY_PK", disParam);
        return disInfos == null ? 0 : disInfos.size();
    }
    
    public IDataset getUserDiscnts(IData inParam) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        String packageId = inParam.getString("PACKAGE_ID");
        String elementId = inParam.getString("ELEMENT_ID");
        String elementTypeCode = inParam.getString("ELEMENT_TYPE_CODE");
        return UserDiscntInfoQry.getUserDiscntByUserAndElementId(userId, packageId, elementId, elementTypeCode);// 查询国际漫游数据流量日套餐优惠
    }
    
    public IDataset getUserDiscntByTheMonth(IData inParam) throws Exception
    {
        String userId = inParam.getString("USER_ID");
        String packageId = inParam.getString("PACKAGE_ID");
        return UserDiscntInfoQry.getUserDiscntByTheMonth(userId, packageId);
    }
    
	public IDataset queryDiscntAreas(IData input) throws Exception {
		String subsysCode = input.getString("SUBSYS_CODE");
		String paramAttr = input.getString("PARAM_ATTR");
		String eparchyCode = input.getString("EPARCHY_CODE");
		IDataset commparaSet = CommparaInfoQry.getCommByParaAttr(subsysCode, paramAttr, eparchyCode);

		/*** add by xieyf5 ***/
		// 查询用户是否为白名单号码
		String serial_number = input.getString("SERIAL_NUMBER");
		IDataset whiteUserSet = AirlinesInterRoamUtil.qryInterRoamAirWhite(serial_number);
		if (IDataUtil.isEmpty(whiteUserSet)) {
			// 假如用户不是白名单号码则将航空公司专属开通方向去掉
			String airlinesOpenDirction = AirlinesInterRoamUtil.getInterRoamDiscnts();
			for (int i = (commparaSet.size() - 1); i >= 0; i--) {
				String openDirection = commparaSet.getData(i).getString("PARA_CODE1");
				if (airlinesOpenDirction.equals(openDirection)) {
					commparaSet.remove(i);
				}
			}
		}
		/*** add by xieyf5 ***/

		return commparaSet;
	}

	public IDataset queryDiscntInfos(IData input) throws Exception {
		 String subsysCode = input.getString("SUBSYS_CODE");
	        String paramAttr = input.getString("PARAM_ATTR");
	        String eparchyCode = input.getString("EPARCHY_CODE");
	        return CommparaInfoQry.getCommByParaAttr(subsysCode, paramAttr, eparchyCode);
	}
	
	public IDataset queryDiscntDetails(IData input) throws Exception {
		
		IDataset set =new DatasetList();
//	        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_ELEMENT_BY_PACKAGE", input);
		set = UPackageElementInfoQry.getElementInfoByGroupId(input.getString("PACKAGE_ID"));
		return set;
		
	}
	
	/**
	 * 
	 */
	public IDataset interRoamQuery(IData input) throws Exception {
		    String serialNumber = input.getString("SERIAL_NUMBER");

	        IDataset  result =  IBossCall.interRoamQuery(serialNumber);
	        
	        IData ibossdata =  result.getData(0);
            
            IDataset serial_number = ibossdata.getDataset("SERIAL_NUMBER");
            
            
            if(IDataUtil.isEmpty(serial_number)){
            	
            	return  result;
            }
            
            if( 1 <= serial_number.size()){
            	
            	IDataset infos = new DatasetList();
            	
            	IDataset userTypes = ibossdata.getDataset("USER_TYPE");
            	IDataset groupNames = ibossdata.getDataset("GROUP_NAME");
            	IDataset provCodes = ibossdata.getDataset("PROV_CODE");
            	String  oprTimes = ibossdata.getString("OPR_TIME");
            	IDataset prodInstIds = ibossdata.getDataset("PROD_INST_ID");
            	IDataset prodIds = ibossdata.getDataset("PROD_ID");
            	IDataset prodTypes = ibossdata.getDataset("PROD_TYPE");
            	IDataset prodStats = ibossdata.getDataset("PROD_STAT");
            	IDataset validDates = ibossdata.getDataset("VALID_DATE");
            	IDataset expiretimes = ibossdata.getDataset("EXPIRE_TIME");
            	IDataset firstTimes = ibossdata.getDataset("FIRST_TIME");
            	IDataset endTimes = ibossdata.getDataset("END_TIME");
            	IDataset channels = ibossdata.getDataset("CHANNEL");
            	
            	for(int i=0;i<serial_number.size();i++){
            		
            		IData info = new DataMap(); 
            		info.put("SERIAL_NUMBER", serial_number.get(i));
            		info.put("USER_TYPE", userTypes.get(i));
            		info.put("GROUP_NAME", groupNames.get(i));
            		info.put("PROV_CODE", provCodes.get(i));
            		info.put("OPR_TIME", oprTimes);
            		info.put("PROD_INST_ID", prodInstIds.get(i));
            		info.put("PROD_ID", prodIds.get(i));
            		info.put("PROD_TYPE", prodTypes.get(i));
            		info.put("PROD_STAT", prodStats.get(i));
            		info.put("VALID_DATE", validDates.get(i));
            		info.put("EXPIRE_TIME", expiretimes.get(i));
            		info.put("FIRST_TIME", firstTimes.get(i));
            		info.put("END_TIME", endTimes.get(i));
            		info.put("CHANNEL", channels.get(i));

            		infos.add(info);
            	}
            	
            	return  infos;
            }
            return  result;
	}

	 public IDataset queryCancelRoamDict(IData input) throws Exception {
	    	IDataset infos = new DatasetList();
	    	String userId = input.getString("USER_ID");
	    	IDataset userDisinfos = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
	    	IDataset commparaSet = CommparaInfoQry.getCommByParaAttr("CSM", "2742", "ZZZZ");
	    	if(DataSetUtils.isNotBlank(commparaSet))
	    	{    		
	    		for(int i=0 ;i< commparaSet.size() ;i++)
	    		{
	    			IData data = commparaSet.getData(i);
	    			String paraCode5 = data.getString("PARA_CODE5");
	    			if(StringUtils.isBlank(paraCode5))
	    			{
	    				continue;
	    			}else if("01".equals(paraCode5))
	    			{
	    				String discntId = data.getString("PARAM_CODE");
	    				for(int j=0 ; j < userDisinfos.size() ;j++)
	    				{
	    					IData discntInfo = userDisinfos.getData(j);
	    					// add by  huangyq
	    					// 根据 user_id  real_inst_id attr_code 查询 对应的 attr_value (订购流水号)
	    					IData attrInfo = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, discntInfo.get("INST_ID").toString(), "PROD_INST_ID", getTradeEparchyCode());
	    					
	    					if(IDataUtil.isNotEmpty(attrInfo)){
	    						if(StringUtils.isNotBlank(attrInfo.getString("ATTR_VALUE"))){
			    					if(discntId.equals(discntInfo.getString("DISCNT_CODE", "")))
			    					{
			    						IData reslutData = new DataMap();
			    						reslutData.putAll(discntInfo);
			    						reslutData.put("DISCNT_NAME", data.getString("PARAM_NAME"));
			    						// 新增 返回 订购流水号  add by  huangyq
			    						reslutData.put("PROD_INST_ID",attrInfo.getString("ATTR_VALUE"));
			    						
			    						infos.add(reslutData);
			    					}
	    						}
	    					}
	    				}
	    			}
	    			
	    		}
	    	}
	    	return infos;
	    }

	 /**
     * 查询 TF_B_TRADEFEE_OTHERFEE 表中 状态异常数据
     *  重新调用账管接口 AM_CRM_DoRomanAccep
     *   并 更新 调用状态
     * @return
     * @throws Exception
     */
    public IData reCalldoRomanAccep4TradeOtherFee(IData input) throws Exception
    {
    	IData result = new DataMap();
    	String month = "0";
    	String startTime="";//跑特定时间之后的
    	//  查询配置获取 时间段   有配置 则查询时间段内的数据，无配置则查询全部
    	IDataset commparaSet = CommparaInfoQry.getCommByParaAttr("CSM", "2748", "ZZZZ");
    	if(DataSetUtils.isNotBlank(commparaSet))
    	{
    		month = commparaSet.getData(0).getString("PARA_CODE1","");
    		startTime=commparaSet.getData(0).getString("PARA_CODE2","");
    	}
    	String UPDATE_TIME = "";
    	Calendar  calendar = Calendar.getInstance();
    	if(!"0".equals(month)){
    		calendar.add(Calendar.MONTH, -Integer.valueOf(month));
    		UPDATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    	}	
    	
    	//有配置特定时间，跑特定时间之后的
	    if(StringUtils.isNotEmpty(startTime)) {
	    	UPDATE_TIME=startTime;
	    }
	    
    	IData param = new DataMap();
    	param.put("UPDATE_TIME", UPDATE_TIME);
    	// 获取表中 状态异常的数据（即除去 0000-成功   9999-待完工 状态） 重新调用账管接口
    	IDataset otherFeeList = Dao.qryByCodeParser("TF_B_TRADEFEE_OTHERFEE", "SEL_ALL_BY_RSRV4", param);
    	logger.debug("---reCalldoRomanAccep4TradeOtherFee--otherFeeList--"+otherFeeList);
    	if(DataUtils.isEmpty(otherFeeList))
        {
        	return null; 
        }else{
        	for(int i=0;i<otherFeeList.size();++i){
        		IData tradeOtherFee = otherFeeList.getData(i);
        		String userId = tradeOtherFee.getString("USER_ID","");
        		String tradeId = tradeOtherFee.getString("TRADE_ID","");
        		String operType = tradeOtherFee.getString("RSRV_STR1");//1:订购；2:激活；3:退订；4: 未激活，已过期
        		IDataset tradeAttr = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(tradeId, "PROD_INST_ID", null);
        		String businessIdA = "";
                String businessIdB = "";
                if("1".equals(operType))
                {//订购时使用订购流水号
                	businessIdA = tradeAttr.getData(0).getString("ATTR_VALUE");
                }
                else
                {//其他操作使用随机序列用于区分
                	businessIdA = tradeId;
                	businessIdB = tradeAttr.getData(0).getString("ATTR_VALUE");
                }
                
                IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId, BizRoute.getRouteId());
        		String ACCESS_NO = IDataUtil.isNotEmpty(userInfo) ? userInfo.getString("SERIAL_NUMBER","") : "";
                 		
				param.clear();
				param.put("ACCT_ID", tradeOtherFee.getString("ACCT_ID"));
			    param.put("ACCESS_NO", ACCESS_NO);
			    param.put("OPER_TYPE", operType);
			    param.put("FEE",  tradeOtherFee.getString("OPER_FEE"));
			    param.put("TRADE_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			    param.put("PEER_BUSINESS_ID_A",  businessIdA);
			    param.put("PEER_BUSINESS_ID_B",  businessIdB);
			    param.put("BILL_ITEM",tradeOtherFee.getString("RSRV_STR2",""));//账目编码
			    String flag = "0000"; // 调账管接口 0000-成功，2998-异常，9999-待完工，其他-失败
		        tradeOtherFee.put("RSRV_STR6", flag);
		        tradeOtherFee.put("RSRV_STR7", "调账管 AM_CRM_DoRomanAccep 接口返回成功！");
				try{
			    	// 调账管 AM_CRM_DoRomanAccep 接口
					result = AcctCall.doRomanAccep(param);
					if(!"0000".equals(result.getString("RESULT_CODE"))){
						flag = result.getString("RESULT_CODE");
						tradeOtherFee.put("RSRV_STR6", flag);
				        tradeOtherFee.put("RSRV_STR7", result.getString("RESULT_MSG"));
					}
			    }catch(Exception e){
			    	logger.debug("InterRoamDayBean---AcctCall.doRomanAccep exception--:"+e.getMessage());
			    	// 执行异常 置为 2998
			    	flag = "2998";
			    	String message ="";
		        	if (e.getMessage().length()>=200) {
		        		message = e.getMessage().substring(0, 200);
					}else {
						message = e.getMessage();
					}
			    	tradeOtherFee.put("RSRV_STR6", flag);
			        tradeOtherFee.put("RSRV_STR7", message);
			    }
				tradeOtherFee.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				// 更新 调用成功与否状态
				Dao.update("TF_B_TRADEFEE_OTHERFEE", tradeOtherFee,new String[]{"TRADE_ID","OPER_TYPE"},Route.getJourDb(BizRoute.getRouteId()));
				
        	}
        }
    	
    	return result;
    }
    
    /**
     * 判断 当前是否处于 抵扣期间
     *  抵扣期间不允许订购操作
     * @throws Exception
     */
    public void checkIsWriteOffPeriod(IData input) throws Exception
    {
    	IData param = new DataMap();
    	String acctId = "";
    	String partitionId = "";
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER",""), BizRoute.getRouteId());
    	if(DataUtils.isNotEmpty(userInfo)){    		
    		partitionId = userInfo.getString("PARTITION_ID","");
    		IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID",""));
    		acctId = acctInfo.getString("ACCT_ID","");
    	}
    	param.put("ACCT_ID", acctId);
    	param.put("PARTITION_ID",partitionId);
    	logger.debug("---checkIsWriteOffPeriod---"+param);
    	IData result = AcctCall.isWriteOffPeriod(param);
    	if("0".equals(result.getString("RESULT_CODE",""))){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"抵扣期间不允许订购！");
    	}
    	
    }
}
