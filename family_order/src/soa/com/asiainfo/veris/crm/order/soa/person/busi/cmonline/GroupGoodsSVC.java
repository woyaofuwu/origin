package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

/**   
* @Title: CMOnlineSVC.java 
* @Package com.asiainfo.veris.crm.order.soa.person.busi.cmonline 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2018年4月12日 上午11:02:46 
* @version V1.0   
*/


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/** 
 * @ClassName: CMOnlineSVC 
 * @Description: TODO(中移在线接口) 
 * @author A18ccms a18ccms_gmail_com 
 * @date 2018年4月12日 上午11:02:46 
 *  
 */
public class GroupGoodsSVC extends CSBizService{
	/**
	 * 集团已订购产品列表
	 * 8981656035
	* @Title: groupGoodsQry 
	* @Description: TODO 
	* @param @param data
	* @param @return
	* @param @throws Throwable   
	* @return IData    
	* @throws
	 */

	public IData groupGoodsQry(IData datas) throws Throwable
	{
		IData data = new DataMap(datas.toString()).getData("params");
		IDataUtil.chkParam(data, "groupId");
        
        String groupId = data.getString("groupId");
        IData rtn = new DataMap();
        
        IData result=new DataMap();
        IDataset results = new DatasetList();
        //1.集团客户信息
        IDataset userProductInfolist=UserProductInfoQry.getProductId(groupId);
        if(IDataUtil.isEmpty(userProductInfolist)){
        	rtn = prepareOutResultList(1, "用户资料为空", results,0);
        	return rtn;
        }
        for(int i=0,sizeI=userProductInfolist.size();i<sizeI;i++){
        	IData grpinfo=new DataMap();
        	IData userProductInfo=userProductInfolist.getData(i);
        	String useridA=userProductInfo.getString("USER_ID");
        	UcaData ucaData=UcaDataFactory.getUcaByUserId(useridA);
        	grpinfo.put("grpsubsid", useridA);
        	grpinfo.put("account", ucaData.getAcctId());
        	grpinfo.put("prodName",UpcCall.qryOfferNameByOfferTypeOfferCode(userProductInfo.getString("PRODUCT_ID"), BofConst.ELEMENT_TYPE_CODE_PRODUCT) );
        	grpinfo.put("prodId", userProductInfo.getString("PRODUCT_ID"));
        	//查询成员数
        	
        	IDataset uudDataset=RelaUUInfoQry.getAllValidRelaByUserIDA(useridA);
        	IDataset bbdDataset=RelaBBInfoQry.getBBInfo_A(useridA);
        	int purCount=0;
        	if(IDataUtil.isNotEmpty(uudDataset)){
        		purCount=uudDataset.size();
        	}
        	if(IDataUtil.isNotEmpty(bbdDataset)){
        		purCount=+bbdDataset.size();
        	}
        	grpinfo.put("purCount", purCount);
        	
        	grpinfo.put("status","1");
        	grpinfo.put("purTime", ucaData.getUser().getOpenDate());
        	grpinfo.put("beginTime", ucaData.getUser().getOpenDate());
        	grpinfo.put("endTime",ucaData.getUser().getDestroyTime());
        	grpinfo.put("price","");//查数据库
        	results.add(grpinfo);
        }
        result.put("grpinfolist", results);
        rtn = prepareOutResultMap(0,"成功", result,results.size());
        
    	return rtn;
	}
	/**
	 * 集团产品明细信息查询
	 * 1109092932071307
	* @Title: groupGoodsInfoQry 
	* @Description: TODO 
	* @param @param data
	* @param @return
	* @param @throws Throwable   
	* @return IDataset    
	* @throws
	 */
	public IData groupGoodsInfoQry(IData datas) throws Throwable
	{
		IData data = new DataMap(datas.toString()).getData("params");
		String userId = data.getString("grpSubsId");//集团用户id
		IDataset results = new DatasetList();
		IData rtn = new DataMap();
		IDataset userproduct =UserProductInfoQry.getUserAllProducts(userId);
		if(IDataUtil.isEmpty(userproduct)){
			rtn = prepareOutResultList(1, "用户资料为空", new DatasetList(),0);
        	return rtn;
		}
		IDataset outDataset= new DatasetList();
		IData outMapData=new DataMap();
		IData result=new DataMap();
		results.add(result);
		result.put("grpSubsId", userId);
		result.put("city", UcaDataFactory.getUcaByUserId(userId).getUser().getCityCode());
		result.put("prodId", userproduct.getData(0).getString("PRODUCT_ID"));
		result.put("prodType", userId);
		String productName=UpcCall.qryOfferNameByOfferTypeOfferCode(userproduct.getData(0).getString("PRODUCT_ID"), BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		result.put("prodDescrpt", productName);
		String isntId=userproduct.getData(0).getString("INST_ID");
		
		IDataset userAttrDataset= UserAttrInfoQry.getUserAttrByPK(userId, isntId,null);
		if(IDataUtil.isEmpty(userAttrDataset)){
			outMapData.put("productInfoList", results);
			outDataset.add(outMapData);
			rtn = prepareOutResultList(0, "成功", outDataset,1);
        	return rtn;
		}
		
		IDataset propList=new DatasetList();
		for(int i=0,sizeI=userAttrDataset.size();i<sizeI;i++){
			IData prop= new DataMap();
			prop.put("propertyCode", userAttrDataset.getData(i).getString("ATTR_CODE"));
			
			prop.put("propertyValue", userAttrDataset.getData(i).getString("ATTR_VALUE"));
			IDataset chaListDataset=UpcCall.queryBrandList(userAttrDataset.getData(i).getString("ATTR_CODE"));
			if(IDataUtil.isNotEmpty(chaListDataset)){
				prop.put("propertyName", chaListDataset.getData(0).getString("CHA_SPEC_NAME"));
			}
			propList.add(prop);
		}
		result.put("propList", propList);
		outMapData.put("productInfoList", results);
		outDataset.add(outMapData);
		rtn = prepareOutResultList(0,"成功", outDataset,results.size());
        
    	return rtn;
	}
	/**
	 * 集团用户成员列表查询
	 * 1109092932071307
	* @Title: groupMemQryList 
	* @Description: TODO 
	* @param @param data
	* @param @return
	* @param @throws Throwable   
	* @return IDataset    
	* @throws
	 */
	public IData groupMemQryList(IData datas) throws Throwable
	{
		IData data = new DataMap(datas.toString()).getData("params");
		IDataset results = new DatasetList();
		IData rtn = new DataMap();
		String userIdA = data.getString("grpSubId");//集团用户id
		IDataset outDataset= new DatasetList();
		IData outData= new DataMap();
		String serial_number = data.getString("memNumber");
		IData  puninfoData=data.getData("crmpfPubInfo");
		long  count=0;
		if(StringUtils.isNotBlank(serial_number)){
			
			IData result=queryMebInfo(userIdA,serial_number,null,null);
			if(!result.isEmpty()){
				results.add(result);
			}
			
			
		}else{
			 Pagination pagination = new Pagination();
			 if ("1".equals(puninfoData.getString("paging"))){//需要分页
		            pagination.setCount(puninfoData.getLong("rowsPerPage",20));
		            pagination.setCurrent(puninfoData.getInt("pageNum",1));
		            pagination.setPageSize(puninfoData.getInt("rowsPerPage",20));
		            pagination.setNeedCount(true);
		          }

			IDataset uudDataset=RelaUUInfoQry.getExistGrpOutinfo(userIdA,null,null,pagination);
		  count=	pagination.getCount();
			if(DataUtils.isNotEmpty(uudDataset)){
				 for(int i=0,sizeI=uudDataset.size();i<sizeI;i++){
					 IData relaInfo=uudDataset.getData(i);
					 serial_number=relaInfo.getString("SERIAL_NUMBER_B");
					 String startDate=relaInfo.getString("START_DATE");
					 String endDate=relaInfo.getString("END_DATE");
					 IData result=queryMebInfo(userIdA,serial_number,startDate,endDate);
					 if(!result.isEmpty()){
							results.add(result);
						}
				 }
			}
        	IDataset bbdDataset=RelaBBInfoQry.getExistGrpOutinfo(userIdA,null,null,pagination);
        	  count= count+pagination.getCount();
        	if(DataUtils.isNotEmpty(bbdDataset)){
				 for(int i=0,sizeI=bbdDataset.size();i<sizeI;i++){
					 IData relaInfo=bbdDataset.getData(i);
					 serial_number=relaInfo.getString("SERIAL_NUMBER_B");
					 String startDate=relaInfo.getString("START_DATE");
					 String endDate=relaInfo.getString("END_DATE");
					 IData result=queryMebInfo(userIdA,serial_number,startDate,endDate);
					 if(!result.isEmpty()){
							results.add(result);
						}
				 }
			}
		}
		outData.put("memList", results);
		outDataset.add(outData);
        rtn = prepareOutResultList(0,"成功", outDataset,(int)count);
      
    	return rtn;
	}
	/**
	 * 查询单个成员信息
	* @Title: queryMebInfo 
	* @Description: TODO 
	* @param @param userIdA
	* @param @param serial_number
	* @param @return
	* @param @throws Throwable   
	* @return IData    
	* @throws
	 */
	public IData queryMebInfo(String userIdA,String serial_number,String startDate,String endDate) throws Throwable
	{
		IData result = new DataMap();
		UcaData ucaData=UcaDataFactory.getNormalUca(serial_number);
		UcaData grpucaData=UcaDataFactory.getUcaByUserId(userIdA);
		result.put("memNumber", serial_number);
		result.put("memName", ucaData.getCustomer().getCustName());
		result.put("memBrand", ucaData.getBrandCode());
		result.put("custStarLevel", ucaData.getUserCreditClass());
		result.put("premiumOrNot", ucaData.getCustomer().getCustType());//
		result.put("region", ucaData.getUser().getEparchyCode());
		result.put("memType", ucaData.getUser().getUserTypeCode());
		result.put("memDep", grpucaData.getUser().getInDepartId());
		result.put("memStatus",ucaData.getUser().getState());
		
		IDataset payplanDataset=UserPayPlanInfoQry.getGrpMemPayPlanByUserId(ucaData.getUserId(),userIdA); 
		if(IDataUtil.isNotEmpty(payplanDataset)){
			String plan_type_code= payplanDataset.getData(0).getString("PLAN_TYPE_CODE");//P-个人付费；G-集团付费；C-定制；T-统付
			if("P".equals(plan_type_code)){
				result.put("payType","个人付费");//11
			}else if("G".equals(plan_type_code)){
				result.put("payType","集团付费");//11
			}
		}
		
		//产品套餐
		IDataset discntDataset=DiscntInfoQry.getUserProductDis(ucaData.getUserId(),userIdA,null);
		if(IDataUtil.isNotEmpty(discntDataset)){
			String discntName=discntDataset.getData(0).getString("DISCNT_NAME");
			result.put("memProd", discntName);
			
		}
		
		//订购时间
		if(StringUtils.isNotEmpty(startDate)){
			result.put("beginTime", startDate);
			result.put("endTime", endDate);
		}else{
			
			IDataset uudata=RelaUUInfoQry.checkMemRelaByUserIdb(userIdA, ucaData.getUserId(),null,null);
			if(IDataUtil.isEmpty(uudata)){
				uudata=RelaBBInfoQry.getBBByUserIdAB(userIdA, ucaData.getUserId(), null, null);
			}
			
			if(IDataUtil.isNotEmpty(uudata)){
				result.put("beginTime", uudata.getData(0).getString("START_DATE"));
				result.put("endTime", uudata.getData(0).getString("END_DATE"));
			}else{
				result.clear();
			}
			
		}
		
		return result;
	}
	
	public IData prepareOutResultMap(int i,String rtnMsg,IData outData, int rows)
    {
    	IData object = new DataMap();
    	IData result = new DataMap();

    	if (i==0)//成功
    	{
        	object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            object.put("resultRows", String.valueOf(rows));
            result.put("object", object);
    		result.put("rtnCode", "0");	
    		result.put("rtnMsg", "成功!");	
            return result;
    	}
    	else if(i==1)//失败
    	{
        	object.put("result", outData);
        	object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
    	}
    	return null;
    }
	
	
	public IData prepareOutResultList(int i,String rtnMsg,IDataset outData,int rows)
    {
    	IData object = new DataMap();
    	IData result = new DataMap();

    	if (i==0)//成功
    	{
        	object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            object.put("resultRows", String.valueOf(rows));
            
            result.put("object", object);
    		result.put("rtnCode", "0");	
    		result.put("rtnMsg", "成功!");
    		 object.put("resultRows", String.valueOf(rows));
            return result;
    	}
    	else if(i==1)//失败
    	{
        	object.put("result", outData);
        	object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
    	}
    	return null;
    }
	
}
