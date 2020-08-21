package com.asiainfo.veris.crm.order.soa.person.busi.addgroupmemberbyorder;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class AddGroupmemberByOrderBean extends CSBizBean
{
	private static final transient Logger logger = LoggerFactory.getLogger(AddGroupmemberByOrderBean.class);
  	public IDataset querySubscriber(IData param) throws Exception{
  		return Dao.qryByCodeParser("TF_F_USER", "SEL_GROUPMEMBER_BY_SERIAL", param);
  	}
  	//查询异网号码
  	public IDataset queryMsisdn(IData param) throws Exception{
  		return Dao.qryByCode("TD_MSISDN", "SEL_BY_MSISDN_GROUP", param, Route.CONN_CRM_CEN);
  	}
  	//携转判断
  	public IDataset queryNp(IData param) throws Exception{
  		return Dao.qryByCode("TF_F_USER", "SEL_BY_SCHOOL", param);
  	}
  	
  	//898集团成员
  	public IDataset queryGroup(IData param) throws Exception{
  		return Dao.qryByCodeParser("TF_F_CUST_GROUPMEMBER", "SEL_GROUP_BY_USERID", param);
  	}
  	//判断号码是否重复加集团通讯录成员
  	public IDataset queryMember(IData param) throws Exception{
  		return Dao.qryByCodeParser("TF_F_ADDRESSBOOK_MEMBER", "SEL_MEMBER_BY_USERID", param);
  	}
  	//判断号码是否属于集团V网成员
  	public IDataset queryGroupVpmn(IData param) throws Exception{
  		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_VPMN_BY_USERIDB", param);
  	}
  	
  	public IData submitCheck(IData data) throws Exception{
        IData dataTag = new DataMap();
        Dao.insert("TF_F_ADDRESSBOOK_MEMBER", data);
        dataTag.put("SUCCESS", "SUCCESS");
        return dataTag;
    }
    // 批量新增导入数据处理
  	public IDataset gtmBookMemberImport(IData importData) throws Exception {
  		logger.debug(">>>>>>>>>>>>>>集团导入数据入参>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+importData);
  		IDataset succds = new DatasetList();
        IDataset faileds = new DatasetList(); 
		String existGroupId = importData.getString("GROUP_ID").trim();
		String existSerial = importData.getString("SERIAL_NUMBER").trim();
		String existMemberKind = importData.getString("MEMBER_KIND");
		String isContact = importData.getString("IS_CONTACT");
		String existCustName = importData.getString("CUST_NAME");
		String existUserCustName = importData.getString("USECUST_NAME");
		String existEparchyCode = importData.getString("EPARCHY_CODE");
		String existCityCode = importData.getString("CITY_CODE");
		if(StringUtils.isEmpty(existGroupId)){
			importData.put("REMARK","错误描述：集团编码898为必填！");
        	faileds.add(importData);
        	return faileds;
		}
		if(StringUtils.isEmpty(existSerial)){
			importData.put("REMARK","错误描述：成员手机号码为必填！");
        	faileds.add(importData);
        	return faileds;
		}else if(existSerial.length()>11){
			importData.put("REMARK","错误描述：手机号码有特殊字符！");
        	faileds.add(importData);
        	return faileds;
		}
		if(StringUtils.isEmpty(existMemberKind)){
			importData.put("REMARK","错误描述：成员类型为必填！");
        	faileds.add(importData);
        	return faileds;
		}else{
			//数据转换(name转换成code)
            String[] key1=new String[3];
    	         key1[0]="TYPE_ID";
    	         key1[1]="SUBSYS_CODE";
    	         key1[2]="DATA_NAME";
    	    String[] value1=new String[3];
 	    		 value1[0]="GRP_MEMKIND";
 	    		 value1[1]="CSM";
 	    		 value1[2]=existMemberKind;
            String memberKingNum = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key1, "DATA_ID", value1);
			if("2".equals(memberKingNum)){
				if(StringUtils.isBlank(isContact)){
					importData.put("REMARK","错误描述：集团领导人的是否可接触服务客户必填！");
                	faileds.add(importData);
                	return faileds;
				}
			}
		}
		//判断号码是否异网用户 mobileFlag="NO"异网号码
		String mobileFlag = mobileFlag(existSerial);
		if("NO".equals(mobileFlag)){//异网号码
			if(StringUtils.isEmpty(existCustName)){
				importData.put("REMARK","错误描述：开户客户为必填！");
            	faileds.add(importData);
            	return faileds;
			}
			if(StringUtils.isEmpty(existUserCustName)){
				importData.put("REMARK","错误描述：使用客户为必填！");
            	faileds.add(importData);
            	return faileds;
			}
			if(StringUtils.isEmpty(existEparchyCode)){
				importData.put("REMARK","错误描述：归属地州为必填！");
            	faileds.add(importData);
            	return faileds;
			}
			if(StringUtils.isEmpty(existCityCode)){
				importData.put("REMARK","错误描述：归属业务区为必填！");
            	faileds.add(importData);
            	return faileds;
			}
			IData iparam = new DataMap();
			IData paramInsert = new DataMap();
        	iparam.put("SERIAL_NUMBER1", existSerial);
        	//查询集团信息
        	IData groupData = UcaInfoQry.qryGrpInfoByGrpId(existGroupId);
        	if(IDataUtil.isNotEmpty(groupData)){
        		paramInsert.put("CUST_ID",groupData.getString("CUST_ID"));
        		paramInsert.put("GROUP_CUST_NAME", groupData.getString("CUST_NAME"));
        		paramInsert.put("GROUP_ID", groupData.getString("GROUP_ID"));
        		//paramInsert.put("GROUP_TYPE", groupData.getString("GROUP_TYPE"));
        		paramInsert.put("CUST_MANAGER_ID", groupData.getString("CUST_MANAGER_ID"));
        	}else{
        		importData.put("REMARK","错误描述：该集团编码查询不到对应的集团信息！");
            	faileds.add(importData);
            	return faileds;
        	}
        	//查询用户是否重复导入同一集团
    		IData param = new DataMap();
    		param.put("USER_ID", existSerial);
    		param.put("GROUP_ID", existGroupId);
    		IDataset txlData = Dao.qryByCodeParser("TF_F_ADDRESSBOOK_MEMBER", "SEL_MEMBER_BY_USERIDANDGROUPID", param);
    		if(IDataUtil.isNotEmpty(txlData)){
    			importData.put("REMARK","错误描述：该用户在通讯录中已是该集团成员！");
            	faileds.add(importData);
            	return faileds;
    		}
            		
    		//查询是否已经属于集团通讯录成员,是的话加迁移字段,加老的集团编码和名称
    		IData paramMem = new DataMap();
    		paramMem.put("USER_ID", existSerial);
    		//是否通讯录成员
    		IDataset dataMem = queryMember(paramMem);
    		if(IDataUtil.isNotEmpty(dataMem)){//不为空说明是迁移用户，上面已经判断用户重复加入同一集团，迁移的话要把上个集团记录失效
    			IData inparams = new DataMap();
        		inparams.put("USER_ID", existSerial);
        		//失效上条数据
        		Dao.executeUpdateByCodeCode("TF_F_ADDRESSBOOK_MEMBER", "UPDATE_GROUPBOOKS_BY_USERID", inparams);
        		
    			paramInsert.put("RSRV_TAG2", "1");//是否迁移
    			paramInsert.put("RSRV_TAG5", "1");//是否已经在集团通讯录
    			paramInsert.put("OLD_GROUP_CUST_NAME", dataMem.getData(0).getString("GROUP_CUST_NAME"));
    			paramInsert.put("OLD_GROUP_ID", dataMem.getData(0).getString("GROUP_ID"));
    			paramInsert.put("RSRV_STR4", dataMem.getData(0).getString("RSRV_STR4"));
    		}else{
    			paramInsert.put("RSRV_TAG5", "1");
    		}
    		paramInsert.put("PARTITION_ID", Long.parseLong(existSerial)%10000);
            paramInsert.put("USER_ID", existSerial);
            paramInsert.put("MEMBER_CUST_ID",existSerial);
            paramInsert.put("USECUST_ID", existSerial);
            paramInsert.put("CUST_NAME", importData.getString("CUST_NAME"));
            paramInsert.put("USECUST_NAME", importData.getString("USECUST_NAME"));
            //数据转换(name转换成code)
            if(!"".equals(importData.getString("IS_CONTACT"))){
            	String[] key3=new String[3];
    	         key3[0]="TYPE_ID";
    	         key3[1]="SUBSYS_CODE";
    	         key3[2]="DATA_NAME";
        	    String[] value3=new String[3];
     	    		 value3[0]="YESNO";
     	    		 value3[1]="CSM";
     	    		 value3[2]=importData.getString("IS_CONTACT");
               paramInsert.put("IS_CONTACT", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key3, "DATA_ID", value3));
            }
            
            //数据转换(name转换成code)
            String[] key=new String[3];
    	         key[0]="TYPE_ID";
    	         key[1]="SUBSYS_CODE";
    	         key[2]="DATA_NAME";
    	    String[] value=new String[3];
 	    		 value[0]="AREA_CODE";
 	    		 value[1]="CUSTMGR";
 	    		 value[2]=importData.getString("EPARCHY_CODE").trim();
            paramInsert.put("EPARCHY_CODE", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
            
            //数据转换(name转换成code)
            String[] key2=new String[3];
    	         key2[0]="TYPE_ID";
    	         key2[1]="SUBSYS_CODE";
    	         key2[2]="DATA_NAME";
    	    String[] value2=new String[3];
 	    		 value2[0]="CITY_CODE_GOODS";
 	    		 value2[1]="CSM";
 	    		 value2[2]=importData.getString("CITY_CODE").trim();
            paramInsert.put("CITY_CODE", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key2, "DATA_ID", value2));
          
            paramInsert.put("REMOVE_TAG","0");
            paramInsert.put("SERIAL_NUMBER", existSerial);
            paramInsert.put("USEPSPT_TYPE_CODE", "Z");
            paramInsert.put("USEPSPT_ID", "111111111111111111");
            paramInsert.put("USEPSPT_ADDR", "-1");
            paramInsert.put("USEPHONE", existSerial);
            //数据转换(name转换成code)
            String[] key1=new String[3];
    	         key1[0]="TYPE_ID";
    	         key1[1]="SUBSYS_CODE";
    	         key1[2]="DATA_NAME";
    	    String[] value1=new String[3];
 	    		 value1[0]="GRP_MEMKIND";
 	    		 value1[1]="CSM";
 	    		 value1[2]=importData.getString("MEMBER_KIND").trim();
            paramInsert.put("MEMBER_KIND", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key1, "DATA_ID", value1));
            paramInsert.put("IS_MOBILE", "2");
            paramInsert.put("JOIN_DATE", SysDateMgr.getSysTime());
            succds.add(importData);
            IDataset addParams = new DatasetList();
            addParams.add(paramInsert);
            if (IDataUtil.isNotEmpty(addParams)) {
                Dao.insert("TF_F_ADDRESSBOOK_MEMBER", addParams, Route.getCrmDefaultDb());
            }
		}else{//移动号码
			IData iparam = new DataMap();
			IData paramInsert = new DataMap();
        	iparam.put("SERIAL_NUMBER1", existSerial);
        	//查询集团信息
        	IData groupData = UcaInfoQry.qryGrpInfoByGrpId(existGroupId);
        	if(IDataUtil.isNotEmpty(groupData)){
        		paramInsert.put("CUST_ID",groupData.getString("CUST_ID"));
        		paramInsert.put("GROUP_CUST_NAME", groupData.getString("CUST_NAME"));
        		paramInsert.put("GROUP_ID", groupData.getString("GROUP_ID"));
        		//paramInsert.put("GROUP_TYPE", groupData.getString("GROUP_TYPE"));
        		paramInsert.put("CUST_MANAGER_ID", groupData.getString("CUST_MANAGER_ID"));
        	}else{
        		importData.put("REMARK","错误描述：该集团编码查询不到对应的集团信息！");
            	faileds.add(importData);
            	return faileds;
        	}
        	//查询客户信息
        	IDataset datas = Dao.qryByCodeParser("TF_F_USER", "SEL_GROUPMEMBER_BY_SERIAL", iparam);
        	//查询用户是否重复导入同一集团
        	if(IDataUtil.isNotEmpty(datas)){
        		String userId =datas.getData(0).getString("USER_ID");
            	if(StringUtils.isNotEmpty(userId)){
            		IData param = new DataMap();
            		param.put("USER_ID", userId);
            		param.put("GROUP_ID", existGroupId);
            		IDataset txlData = Dao.qryByCodeParser("TF_F_ADDRESSBOOK_MEMBER", "SEL_MEMBER_BY_USERIDANDGROUPID", param);
            		if(IDataUtil.isNotEmpty(txlData)){
            			importData.put("REMARK","错误描述：该用户在通讯录中已是该集团成员！");
                    	faileds.add(importData);
                    	return faileds;
            		}
            	}
        	}else{
        		importData.put("REMARK","错误描述：该用户号码查询不到对应的客户信息！");
            	faileds.add(importData);
            	return faileds;
        	}
        	if(IDataUtil.isNotEmpty(datas)){
        		IData userData = datas.getData(0);
        		String dataUserId = userData.getString("USER_ID");
        		if(StringUtils.isNotBlank(dataUserId)){
        			//查询是否898集团成员
        			IData inParam = new DataMap();
            		inParam.put("USER_ID", dataUserId);
            		IDataset datagroup = queryGroup(inParam);
            		if(IDataUtil.isNotEmpty(datagroup)){
            			paramInsert.put("RSRV_TAG1", "1");
            			paramInsert.put("RSRV_STR1", datagroup.getData(0).getString("GROUP_CUST_NAME"));
            		}else{
            			paramInsert.put("RSRV_TAG1", "2");
            		}
            		
            		//查询是否为V网成员
            		IData paramV = new DataMap();
            		paramV.put("USER_ID", dataUserId);
            		IDataset dataV = queryGroupVpmn(paramV);
            		if(IDataUtil.isNotEmpty(dataV)){
            			paramInsert.put("RSRV_TAG4", "1");
            			paramInsert.put("RSRV_STR4", dataV.getData(0).getString("CUST_NAME"));
            		}else{
            			paramInsert.put("RSRV_TAG4", "2");
            		}
            		
            		//查询是否已经属于集团通讯录成员,是的话加迁移字段,加老的集团编码和名称
            		IData paramMem = new DataMap();
            		paramMem.put("USER_ID", dataUserId);
            		//是否通讯录成员
            		IDataset dataMem = queryMember(paramMem);
            		if(IDataUtil.isNotEmpty(dataMem)){//不为空说明是迁移用户，上面已经判断用户重复加入同一集团，迁移的话要把上个集团记录失效
            			IData inparams = new DataMap();
                		inparams.put("USER_ID", dataUserId);
                		//失效上条数据
                		Dao.executeUpdateByCodeCode("TF_F_ADDRESSBOOK_MEMBER", "UPDATE_GROUPBOOKS_BY_USERID", inparams);
                		
            			paramInsert.put("RSRV_TAG2", "1");//是否迁移
            			paramInsert.put("RSRV_TAG5", "1");//是否已经在集团通讯录
            			paramInsert.put("OLD_GROUP_CUST_NAME", dataMem.getData(0).getString("GROUP_CUST_NAME"));
            			paramInsert.put("OLD_GROUP_ID", dataMem.getData(0).getString("GROUP_ID"));
            			paramInsert.put("RSRV_STR4", dataMem.getData(0).getString("VPN_NAME"));
            		}else{
            			paramInsert.put("RSRV_TAG5", "1");
            		}
        		}
        		
        		
        		paramInsert.put("PARTITION_ID", userData.getString("PARTITION_ID"));
                paramInsert.put("USER_ID", userData.getString("USER_ID"));
                paramInsert.put("MEMBER_CUST_ID",userData.getString("CUST_ID"));
                paramInsert.put("USECUST_ID", userData.getString("CUST_ID"));
                paramInsert.put("CUST_NAME", userData.getString("CUST_NAME"));
                paramInsert.put("USECUST_NAME", userData.getString("CUST_NAME"));
                paramInsert.put("EPARCHY_CODE",userData.getString("EPARCHY_CODE"));
                paramInsert.put("CITY_CODE", userData.getString("CITY_CODE"));
                paramInsert.put("REMOVE_TAG",userData.getString("REMOVE_TAG"));
                paramInsert.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
                paramInsert.put("USEPSPT_TYPE_CODE", userData.getString("PSPT_TYPE_CODE"));
                paramInsert.put("USEPSPT_ID", userData.getString("PSPT_ID"));
                paramInsert.put("USEPSPT_ADDR", userData.getString("PSPT_ADDR"));
                paramInsert.put("USEPOST_ADDR", userData.getString("POST_ADDRESS"));
                paramInsert.put("USEPSPT_END_DATE", userData.getString("PSPT_END_DATE"));
                paramInsert.put("USEPHONE", userData.getString("PHONE"));
                paramInsert.put("IS_MOBILE", "1");
                //数据转换(name转换成code)
                if(!"".equals(importData.getString("IS_CONTACT"))){
                	String[] key4=new String[3];
        	         key4[0]="TYPE_ID";
        	         key4[1]="SUBSYS_CODE";
        	         key4[2]="DATA_NAME";
	        	    String[] value4=new String[3];
	     	    		 value4[0]="YESNO";
	     	    		 value4[1]="CSM";
	     	    		 value4[2]=importData.getString("IS_CONTACT");
                   paramInsert.put("IS_CONTACT", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key4, "DATA_ID", value4));
                }
                //数据转换
                String[] key=new String[3];
        	         key[0]="TYPE_ID";
        	         key[1]="SUBSYS_CODE";
        	         key[2]="DATA_NAME";
        	    String[] value=new String[3];
     	    		 value[0]="GRP_MEMKIND";
     	    		 value[1]="CSM";
     	    		 value[2]=importData.getString("MEMBER_KIND").trim();
                paramInsert.put("MEMBER_KIND", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
                
        	}
        	
            paramInsert.put("JOIN_DATE", SysDateMgr.getSysTime());
            succds.add(importData);
            IDataset addParams = new DatasetList();
            addParams.add(paramInsert);
            if (IDataUtil.isNotEmpty(addParams)) {
                Dao.insert("TF_F_ADDRESSBOOK_MEMBER", addParams, Route.getCrmDefaultDb());
            }
			
		}
        return succds;
    }
 // 批量新增导入数据处理
   	public IData bookMemberImport(IData importParam) throws Exception {
     	IData fileData = importParam.getData("FILEDATA");
   		IDataset[] datasets = (IDataset[]) fileData.get("right");
   		for (int i = 0; i < datasets.length; i++) {
   			IDataset importDataset = datasets[i];
   			if (IDataUtil.isEmpty(importDataset)) {
                 CSAppException.apperr(BatException.CRM_BAT_20);
             }
   			for (int j = 0; j < importDataset.size(); j++) {
   				IData importData = importDataset.getData(j);
   				String existGroupId = importData.getString("GROUP_ID");
   				String existSerial = importData.getString("SERIAL_NUMBER");
   				if(StringUtils.isNotBlank(existGroupId)){
   					existGroupId = existGroupId.trim();
   					//去掉特殊空格
   					existGroupId = existGroupId.replaceAll("[\\s\\u00A0]", "");
   					//判断全角空格，有全角空格的话,去掉
   					if(existGroupId.contains("　")){
   						existGroupId = existGroupId.replace("　", "");
   					}
   				}
   				if(StringUtils.isNotBlank(existSerial)){
   					existSerial = existSerial.trim();
   					//去掉特殊空格
   					existSerial = existSerial.replaceAll("[\\s\\u00A0]", "");
   					//判断全角空格，有全角空格的话,去掉
   					if(existSerial.contains("　")){
   						existSerial = existSerial.replace("　", "");
   					}
   				}
   				String existMemberKind = importData.getString("MEMBER_KIND");
   				String isContact = importData.getString("IS_CONTACT");
   				String existCustName = importData.getString("CUST_NAME");
   				String existUserCustName = importData.getString("USECUST_NAME");
   				String existEparchyCode = importData.getString("EPARCHY_CODE");
   				String existCityCode = importData.getString("CITY_CODE");
   				//构建入表数据
   				IData data = new DataMap();
   				Date date = new Date();
        		SimpleDateFormat sdf = new SimpleDateFormat("MM");
        		String tradeId = SeqMgr.getTradeId();
        		data.put("MEMBER_ID", tradeId);
   				data.put("ACCEPT_MONTH", sdf.format(date));
   				data.put("GROUP_ID", existGroupId);
   				data.put("SERIAL_NUMBER", existSerial);
   				data.put("MEMBER_KIND", existMemberKind);
   				data.put("IS_CONTACT", isContact);
   				data.put("CUST_NAME", existCustName);
   				data.put("USECUST_NAME", existUserCustName);
   				data.put("EPARCHY_CODE", existEparchyCode);
   				data.put("CITY_CODE", existCityCode);
   				
   				Dao.insert("TF_F_ADDRESSBOOK_BAK", data);
   			}	
   		}
   		IData dataTag = new DataMap();
        dataTag.put("SUCCESS", "SUCCESS");
        return dataTag;
     }

  	/**
	 * 判断异网号码
	 */
	public String mobileFlag(String serialnumber) throws Exception {
		//异网标志,默认YES移动号码
		String mobileFlag = "YES";
		//先判断是否异网号码，异网号码不做信息查询
		IData paramHd = new DataMap();
		paramHd.put("PROV_CODE", "898");
		paramHd.put("SERIAL_NUMBER", serialnumber);
		AddGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		//判断号段，不为空是(海南移动)
		IDataset snInfos = groupmemberInfoQryBean.queryMsisdn(paramHd);
		if(IDataUtil.isNotEmpty(snInfos)){
			//移动号段
			//移动号段再判断是否已携出，判断携转状态，4已携出，1已携入
			IData paramNp = new DataMap();
			paramNp.put("SERIAL_NUMBER", serialnumber);
	        IDataset infos = Dao.qryByCode("TF_F_USER", "SEL_BY_SCHOOL", paramNp);
	        if(IDataUtil.isNotEmpty(infos)){
	        	IData catalog = infos.getData(0);
				String userTagSet = catalog.getString("USER_TAG_SET");//userTagSet=4是已携出号码
				if(StringUtils.isNotEmpty(userTagSet) && userTagSet.equals("4")){//等于4说明是携出号码
					mobileFlag = "NO";//异网号码
				}
	        }
		}else{
	      //异网号段
			//异网号段再判断是否已携入，判断携转状态，4已携出，1已携入
			IData paramNp = new DataMap();
			paramNp.put("SERIAL_NUMBER", serialnumber);
			IDataset infos = groupmemberInfoQryBean.queryNp(paramNp);
	        if(IDataUtil.isNotEmpty(infos)){
	        	IData catalog = infos.getData(0);
				String userTagSet = catalog.getString("USER_TAG_SET");//userTagSet=1是已携入号码
				if(StringUtils.isNotEmpty(userTagSet) && userTagSet.equals("1")){//等于1说明是移动号码
					
				}else{
					mobileFlag = "NO";//异网号码
				}
	        }else{
	        	mobileFlag = "NO";//异网号码
	        }
	        	
		}
		return mobileFlag;
	}
	
}

