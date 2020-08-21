package com.asiainfo.veris.crm.order.soa.person.busi.phone;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BackUserExtendSVC extends CSBizService{

	
	private static final long serialVersionUID = 1L;
	
	public IDataset importDataByFile(IData data) throws Exception
    {
		IDataset dataset=new DatasetList();
		
		String fileId = data.getString("cond_STICK_LIST"); // 上传OCS监控excelL文件的编号
		String extendTime=data.getString("EXTEND_TIME","0");
		String tradeStaffId=data.getString("STAFF_ID","");
		String tradeDepartId=data.getString("STAFF_DEPART_ID","");
		String[] fileIds = fileId.split(",");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        IDataset fails = new DatasetList();
        int allCount = 0;
        for (String strfileId : fileIds)
        {
            IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/ImportExtendUserBack.xml"));
            IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
            IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
            dataset.addAll(suc[0]);
            fails.addAll(err[0]);
            if(IDataUtil.isNotEmpty(dataset)){
            	allCount = dataset.size()+fails.size();
            }else{
            	allCount = fails.size();
            }
        }
        
        //不允许超过600条数据
        if(allCount>600){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "导入的数据量不能超过600条！");
        }
        
        
        IDataset dealResult=new DatasetList();
    	int sucSize=0;
        
        if(IDataUtil.isNotEmpty(dataset)){
        	List<String> phones=new ArrayList<String>();
        	StringBuilder phonesSql=new StringBuilder();
        	
        	for(int i=0,size=dataset.size();i<size;i++){
        		if(i==size-1){
        			phonesSql.append("'");
        			phonesSql.append(dataset.getData(i).getString("SERIAL_NUMBER"));
        			phonesSql.append("'");
        		}else{
        			phonesSql.append("'");
        			phonesSql.append(dataset.getData(i).getString("SERIAL_NUMBER"));
        			phonesSql.append("',");
        		}
        		
        		phones.add(dataset.getData(i).getString("SERIAL_NUMBER"));
        		
        	}
        	
        	//查询表当中的验证信息
        	IDataset backUserInfos=UserInfoQry.queryBackUserBySerialNumber(phonesSql.toString());
        	
        	//最终可以进行延期的号码
        	IDataset extendRightPhones=new DatasetList();
        	
        	if(IDataUtil.isNotEmpty(backUserInfos)){
        		//获取员工的地区编码
        		boolean isNeedVerifyRight=true;
            	String staffCityCode=data.getString("STAFF_CITY_CODE","0");
            	
            	if(staffCityCode.equals("HNSJ")||staffCityCode.equals("HNHN")||
        				staffCityCode.equals("HNYD")){		//如果为省级工号
            		isNeedVerifyRight=false;
        		}
            	
            	for(int j=0,sizej=backUserInfos.size();j<sizej;j++){
            		IData backUserinfo=backUserInfos.getData(j);
            		String serialNumber=backUserinfo.getString("SERIAL_NUMBER","");
            		String valuen2=backUserinfo.getString("VALUEN2","");
            		String areacode=backUserinfo.getString("AREA_CODE","");
            		
            		/*
                	 * 验证号码是否存TS_S_USER_BACK表当中
                	 * 如果包含就去掉这个号码，剩余就是 已经被回收或者不在延期表当中
                	 */
            		if(phones.contains(serialNumber)){
            			phones.remove(serialNumber);
            		}
            		
            		/*
                	 * 验证号码是否已经延期 
                	 */
            		if(valuen2!=null&&valuen2.equals("1")){
            			IData errorData=new DataMap();
            			errorData.put("SERIAL_NUMBER", serialNumber);
            			errorData.put("IMPORT_ERROR", "号码已经被延期");
            			
            			fails.add(errorData);
            			continue;
            		}
            		
            		
            		/*
                	 * 验证当前操作元是否有权限操作这些号码
                	 */
            		if(isNeedVerifyRight){	//如果需要验证权限
            			if(areacode.equals(staffCityCode)){		//有权限处理
            				backUserinfo.put("EXTEND_MONTH", extendTime);
            				backUserinfo.put("TRADE_STAFF_ID", tradeStaffId);
            				backUserinfo.put("TRADE_DEPART_ID", tradeDepartId);
            				extendRightPhones.add(backUserinfo);
            			}else{
            				IData errorData=new DataMap();
                			errorData.put("SERIAL_NUMBER", serialNumber);
                			errorData.put("IMPORT_ERROR", "号码不属于操作员的业务区，无权进行延期");
                			
                			fails.add(errorData);
                			continue;
            			}
            		}else{
            			backUserinfo.put("EXTEND_MONTH", extendTime);
        				backUserinfo.put("TRADE_STAFF_ID", tradeStaffId);
        				backUserinfo.put("TRADE_DEPART_ID", tradeDepartId);
            			extendRightPhones.add(backUserinfo);
            		}
            	}
            	
            	
            	//如果存在号码可以进行延期，进行号码延期
            	if(IDataUtil.isNotEmpty(extendRightPhones)){
            		/*
            		 * 验证号码数量
            		 */
            		UserInfoQry.extendBackUserByDepartId(extendRightPhones);
            		
            		/*
            		 * 记录操作日志
            		 */
            		UserInfoQry.saveBackUserExtendLog(extendRightPhones);
            		
            	}
            	
        	}
        	
        	
        	//如果存在没有在ts_s_back_user表的数据
        	if(phones!=null&&phones.size()>0){
        		for(int z=0,sizez=phones.size();z<sizez;z++){
        			IData errorData=new DataMap();
        			errorData.put("SERIAL_NUMBER", phones.get(z));
        			errorData.put("IMPORT_ERROR", "号码在要延期的数据当中查询不到，原因可能为：号码已经被延期，或者不属于延期号码，或者号码不属于操作员的业务区无权进行延期！");
        			
        			fails.add(errorData);
        			
        		}
        	}
        	
        	
        	if(IDataUtil.isNotEmpty(fails)){
        		dealResult.addAll(fails);
        	}
        	
        	
        	//封装处理成功的号码信息
        	if(IDataUtil.isNotEmpty(extendRightPhones)){
        		
        		sucSize=extendRightPhones.size();
        		
        		for(int l=0,sizel=extendRightPhones.size();l<sizel;l++){
        			IData sucData=new DataMap();
        			sucData.put("SERIAL_NUMBER", extendRightPhones.getData(l).getString("SERIAL_NUMBER",""));
        			sucData.put("IMPORT_ERROR", "延期成功！");
        			
        			dealResult.add(sucData);
        		}
        	}
        	
        	      	
        	
        }
        
        
        /*
    	 * 封装最终的处理结果
    	 */
    	String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
        String fileName = "DealResult.xls";
        File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
        { dealResult }, "personserv", fileIdE, null, "import/ImportExtendUserBack.xml");
        String resultFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/import", fileName);
        String resultFileUrl = ImpExpUtil.getDownloadPath(resultFileId, fileName);
        
    	IData returnInfo=new DataMap();
        returnInfo.put("DATASET_SIZE", allCount);
        returnInfo.put("SUCC_SIZE", sucSize);
        returnInfo.put("FAILD_SIZE", allCount-sucSize);
        returnInfo.put("FILE_ID", resultFileId);
        returnInfo.put("FILE_URL", resultFileUrl);
        
        IDataset returnInfos=new DatasetList();
        returnInfos.add(returnInfo);
        
		return returnInfos;
		
    }
	

	public IDataset exportDataByFile(IData param) throws Exception
    {
		IDataset result=UserInfoQry.queryBackUser(param, getPagination());
		
		if(IDataUtil.isNotEmpty(result)){
			BizVisit visit=CSBizBean.getVisit();
			
			for(int i=0,size=result.size();i<size;i++){
				IData data=result.getData(i);
				
				if(!data.getString("PRODUCT_ID","").equals("")){
					data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID","")));
				}
				if(!data.getString("BRAND_CODE","").equals("")){
					data.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(data.getString("BRAND_CODE","")));
				}
				if(!data.getString("CITY_CODE","").equals("")){
					data.put("AREA_NAME", StaticUtil.getStaticValue(visit,
							"TD_M_AREA", "AREA_CODE", "AREA_NAME", data.getString("CITY_CODE","")));
				}
				if(!data.getString("DEVELOP_DEPART_ID","").equals("")){
					data.put("DEPART_NAME", StaticUtil.getStaticValue(visit,
							"TD_M_DEPART", "DEPART_ID", "DEPART_NAME", data.getString("DEVELOP_DEPART_ID","")));
                    data.put("DEPART_CODE", StaticUtil.getStaticValue(visit,
                            "TD_M_DEPART", "DEPART_ID", "DEPART_CODE", data.getString("DEVELOP_DEPART_ID","")));
				}
			}
		}
		
		
		return result;
    }
	
}
