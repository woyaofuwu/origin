
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class MiddleNumberInfoSynSVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(MiddleNumberInfoSynSVC.class);
	
    private static final long serialVersionUID = 1L;
    public static final String GROUP_NUMBER_PRODUCT="9101002";//企业和多号
    public static final String GOUP_PRIV="898";//集团客户归属省
    public static final String RELATION_TYPE_CODE="97";
    /**
     * 封装给客管调集团副号码信息同步接口（补录操作只有企业和多号有）
     * @param data
     * @throws Exception
     */
    public static void groupCustInfoForCustMgr(IData data) throws Exception
    { 
    	IData info=new DataMap();
    	String custId=data.getString("CUST_ID");
    	String proNumber=GROUP_NUMBER_PRODUCT;
    	String groupProductId = GrpCommonBean.merchToProduct(proNumber, 2 ,null);// 产品编号转化为本地产品编号   
    	IDataset productInfos=UserProductInfoQry.getUserProductInfoByCstId(custId,groupProductId,null);
    	if(IDataUtil.isNotEmpty(productInfos)){//订购了企业和多号产品
    		for(int i=0;i<productInfos.size();i++){
    			IData productdata=productInfos.getData(i);
    			String userId=productdata.getString("USER_ID");  
    			String eparchyCode=productdata.getString("EPARCHY_CODE",""); 
    			String merchRelationTypeCode =UProductCompInfoQry.getRelationTypeCodeByProductId(groupProductId);
//    			IData param=new DataMap();
//    		    param.put("RELATION_TYPE_CODE", merchRelationTypeCode);
//    		    param.put("USER_ID_A", userId);
//    		    param.put("ROLE_CODE_B", "1");// 子产品跟商品关系，成员用户与集团用户为1
//    		    IDataset relaUUInfo = RelaUUInfoQry.getUUInfoByUserEparchy(userId,"1",merchRelationTypeCode,eparchyCode);
    			IDataset relaUUInfo = RelaBBInfoQry.getExistsByUserIdA(userId,merchRelationTypeCode);
    		    if(IDataUtil.isNotEmpty(relaUUInfo)){
    		    	IDataset userDatas=new DatasetList();
    		    	for(int j=0;j<relaUUInfo.size();j++){
    		    		IData userInfo=new DataMap();
    		    		//查副号码归属省
    		    		String serialNumber=relaUUInfo.getData(j).getString("SERIAL_NUMBER_B");
    		    		IDataset areaInfos= AcctCall.queryMpAreaCode(serialNumber);
    		    		userInfo.put("FOLLOW_PROV",areaInfos.getData(0).getString("PROV_CODE"));
    		    		userInfo.put("SERIAL_NUMBER",serialNumber );
    		    		userInfo.put("OPR_CODE", "08");
    		    		userDatas.add(userInfo);
    		    	}
    		    	info.put("GROUP_CUST_ID", custId);
    		    	info.put("NUMBER_LIST", userDatas);   		    	
    		    	groupFollowMsisdnSyn(info);
    		    }   		    
    		}
    	}
    }
  
    /**
     * 
     * 集团副号码信息同步（中间号，和多号发起方）
     * @param data
     * @throws Exception
     */
    public static void groupFollowMsisdnSyn(IData data) throws Exception
    {   
    	IData param=new DataMap();
    	String groupCustId=data.getString("GROUP_CUST_ID");
    	IData groupRealInfo=getGroupRealInfo(data);//掉客官接口或查表（客户名称，责任人经办人以及图片信息）
    	String followPriv="";
    	if(IDataUtil.isNotEmpty(groupRealInfo)){
    		String groupCustName=groupRealInfo.getString("ENTERPRISE_NAME");//客户名称
    		String Mprovince=groupRealInfo.getString("ENTER_PROVINCE");//集团归属省编码
    		String pkgSeq=GOUP_PRIV+SysDateMgr.getSysDateYYYYMMDD()+SeqMgr.getLogId().substring(10);
    		IData zrrInfo=new DataMap();
    		zrrInfo.put("MSISDN_NAME", groupRealInfo.getString("ZRR_NAME"));//责任人姓名
    		zrrInfo.put("IDCARD_TYPE", groupRealInfo.getString("ZRR_IDCARD_TYPE"));//责任人证件类型
    		zrrInfo.put("IDCARD_NUM", groupRealInfo.getString("ZRR_IDCARD_NUM"));//责任人号码
    		zrrInfo.put("ADDRESS", groupRealInfo.getString("ZRR_IDCARD_ADDR"));//责任人证件地址
	     	IData jbrInfo=new DataMap();
    		jbrInfo.put("MSISDN_NAME", groupRealInfo.getString("JBR_NAME"));//经办人姓名
    		jbrInfo.put("IDCARD_TYPE", groupRealInfo.getString("JBR_IDCARD_TYPE"));//经办人证件类型
    		jbrInfo.put("IDCARD_NUM", groupRealInfo.getString("JBR_IDCARD_NUM"));//经办人号码
    		jbrInfo.put("ADDRESS", groupRealInfo.getString("JBR_IDCARD_ADDR"));//经办人证件地址
   		    //成员列表
        	IDataset numberLists = data.getDataset("NUMBER_LIST");
        	if(IDataUtil.isNotEmpty(numberLists)){      	
        	int numberSize=numberLists.size();
        	IDataset userDatas=new DatasetList();
        	for(int i=0;i<numberSize;i++){//用户数据
        	IData part=numberLists.getData(i);
            followPriv=part.getString("FOLLOW_PROV");
           	String serialNumber=part.getString("SERIAL_NUMBER");
            String oprCode=numberLists.getData(i).getString("OPR_CODE");
            String seq=GOUP_PRIV+"BIP5A014"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+SeqMgr.getLogId().substring(10);
        	String zrrPicZName="BOSS"+GOUP_PRIV+"_PICTURE_"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"_BOSS"+followPriv+".C"+SeqMgr.getLogId().substring(13)+"_Z.jpg";
         	String zrrPicFName="BOSS"+GOUP_PRIV+"_PICTURE_"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"_BOSS"+followPriv+".C"+SeqMgr.getLogId().substring(13)+"_F.jpg";
         	zrrInfo.put("PIC_NAMEZ",zrrPicZName);//责任人身份证正面图
         	zrrInfo.put("PIC_NAMEF", zrrPicFName);//责任人身份证反面图
         	String jbrPicTName="BOSS"+GOUP_PRIV+"_PICTURE_"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"_BOSS"+followPriv+".C"+SeqMgr.getLogId().substring(13)+"_Z.jpg";
         	String jbrPicZName="BOSS"+GOUP_PRIV+"_PICTURE_"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"_BOSS"+followPriv+".C"+SeqMgr.getLogId().substring(13)+"_F.jpg";
         	String jbrPicFName="BOSS"+GOUP_PRIV+"_PICTURE_"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"_BOSS"+followPriv+".C"+SeqMgr.getLogId().substring(13)+"_T.jpg";
         	jbrInfo.put("PIC_NAMET", jbrPicTName);//经办人身份证反面图
         	jbrInfo.put("PIC_NAMEZ", jbrPicZName);//经办人身份证正面图
         	jbrInfo.put("PIC_NAMEF", jbrPicFName);//经办人身份证反面图
        	 groupRealInfo.put("OPR_CODE", oprCode);
        	 groupRealInfo.put("FOLLOW_ACC_NUM", serialNumber);
        	 groupRealInfo.put("OPR_SEQ", seq);
        	 groupRealInfo.put("PKG_SEQ",pkgSeq);
        	 groupRealInfo.put("FOLLOW_USER_ID","-1");
        	 groupRealInfo.put("FOLLOW_CUST_ID","-1");
        	 groupRealInfo.put("RSRV_STR1",followPriv);//副号码归属省
        	 groupRealInfo.put("GROUP_CUST_ID",groupCustId);
        	 groupRealInfo.put("ENTERPRISE_NAME",groupCustName);
        	 groupRealInfo.put("ENTER_PROVINCE",Mprovince); 
        	 groupRealInfo.put("SYNC_ID",SysDateMgr.getSysDateYYYYMMDDHHMMSS()+SeqMgr.getLogId().substring(8));
             IData userInfo=new DataMap();
        	 userInfo.put("SEQ", seq);//3位省代码+8位业务编码（BIPCode）+14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始，增量步长为1
        	 userInfo.put("FOLLOW_MSISDN",numberLists.getData(i).getString("SERIAL_NUMBER"));//副号码标识        	
        	 userInfo.put("OPR_CODE",oprCode);//操作代码01订购，02退订,08补录
        	 if("01".equals(oprCode)||"08".equals(oprCode)){
        		 userInfo.put("ENTERPRISE_NAME",groupCustName);//集团客户名称
        		 userInfo.put("ENTERPRISE_ID",groupCustId);//集团客户ID
            	 userInfo.put("MPROVINCE",Mprovince);//集团客户归属省代码
            	 IData groupInfo=UcaInfoQry.qryGrpInfoByCustId(groupCustId);//qryGrpInfoByCustIdFromDB(groupCustId);
            	 if(IDataUtil.isNotEmpty(groupInfo)&&StringUtils.isNotBlank(groupInfo.getString("MP_GROUP_CUST_CODE"))){
            		 userInfo.put("ENTERPRISE_ID",groupInfo.getString("MP_GROUP_CUST_CODE"));//集团客户ID 
            	 }           	
            	 IDataset zrrInfoList=new DatasetList();
            	 zrrInfoList.add(zrrInfo);
            	 IDataset jbrInfoList=new DatasetList();
            	 jbrInfoList.add(jbrInfo);
            	 userInfo.put("ZRR_INFO",zrrInfoList);
            	 userInfo.put("JBR_INFO",jbrInfoList); 
            	//调客管接口保存图片file_id和文件名称的对应关系
            	 transPicInfoParam(groupRealInfo,zrrInfo,jbrInfo);
        	 } 
        	 //保存副号码同步信息
         if(!GOUP_PRIV.equals(followPriv)){ 
        	userDatas.add(userInfo);        	
        	param.put("PKG_SEQ",pkgSeq);//发起省代码＋YYYYMMDD＋6位定长流水，序号从000001开始，增量步长为1
        	param.put("REC_NUM", "1");
        	param.put("UD1", userDatas);
        	param.put("KIND_ID", "BIP5A014_T5000003_0_0");//掉IBOSS接口，接口未定
         	param.put("ROUTEVALUE",followPriv);//副号码归属省
         	param.put("ROUTETYPE","00");
			IBossCall.dealInvokeUrl("BIP5A014_T5000003_0_0", "IBOSS2", param);   
         }
            insertEcFollwSynInfo(groupRealInfo,zrrInfo,jbrInfo);
            }
        	}
    	}
    }
    /**
     * 从客官获取集团客户实名制信息
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getGroupRealInfo(IData data) throws Exception
    { 
    	IData param=new DataMap();
    	param.put("GROUP_CUST_ID", data.getString("GROUP_CUST_ID"));
    	IDataset infos=Dao.qryByCode("TF_F_GROUP_REALNAME_OTHER", "SEL_BY_CUSTID", param,Route.CONN_CRM_CG);
    	if(IDataUtil.isNotEmpty(infos)){
    		return infos.getData(0);
    	}else{
    		return null;
    	}
    }
    public static void insertEcFollwSynInfo(IData groupRealInfo,IData zrrInfo,IData jbrInfo) throws Exception {
    	IData request=new DataMap();
    	request.put("SYNC_ID",groupRealInfo.getString("SYNC_ID"));//同步标识ID
	    request.put("SYNC_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    request.put("PKG_SEQ",groupRealInfo.getString("PKG_SEQ"));	 
        request.put("OPR_SEQ",groupRealInfo.getString("OPR_SEQ") );
		request.put("OPR_CODE",groupRealInfo.getString("OPR_CODE"));			
	    request.put("FOLLOW_ACC_NUM",groupRealInfo.getString("FOLLOW_ACC_NUM"));
	    request.put("FOLLOW_USER_ID", groupRealInfo.getString("FOLLOW_USER_ID"));
	    request.put("FOLLOW_CUST_ID", groupRealInfo.getString("FOLLOW_CUST_ID"));
	    request.put("RSRV_STR1",groupRealInfo.getString("RSRV_STR1"));//副号码归属省
	    request.put("GROUP_CUST_ID",groupRealInfo.getString("GROUP_CUST_ID"));
	    request.put("ENTERPRISE_NAME",groupRealInfo.getString("ENTERPRISE_NAME"));
	    request.put("ENTER_PROVINCE",groupRealInfo.getString("ENTER_PROVINCE"));	
	    request.put("ZRR_IDCARD_TYPE",zrrInfo.getString("IDCARD_TYPE",""));//责任人证件类型
	    request.put("ZRR_IDCARD_NUM",zrrInfo.getString("IDCARD_NUM",""));//责任人证件号码
	    request.put("ZRR_NAME",zrrInfo.getString("MSISDN_NAME",""));//责任人名称
	    request.put("ZRR_IDCARD_ADDR",zrrInfo.getString("ADDRESS",""));//责任人证件地址	
	    request.put("ZRR_PIC_NAME_Z",zrrInfo.getString("PIC_NAMEZ",""));//责任人身份证正面
	    request.put("ZRR_PIC_NAME_F",zrrInfo.getString("PIC_NAMEF",""));//责任人身份证反面	
	    request.put("JBR_IDCARD_TYPE",jbrInfo.getString("IDCARD_TYPE",""));//经办人证件类型
	    request.put("JBR_IDCARD_NUM", jbrInfo.getString("IDCARD_NUM",""));//经办人证件号码
	    request.put("JBR_NAME",jbrInfo.getString("MSISDN_NAME",""));//经办人名称
	    request.put("JBR_IDCARD_ADDR",jbrInfo.getString("ADDRESS",""));//经办人证件地址
	    request.put("JBR_PIC_NAME_T",jbrInfo.getString("PIC_NAMET",""));//经办人头像
	    request.put("JBR_PIC_NAME_Z",jbrInfo.getString("PIC_NAMEZ",""));//经办人身份证正面
	    request.put("JBR_PIC_NAME_F",jbrInfo.getString("PIC_NAMEF",""));//经办人身份证反面
    	Dao.insert("TF_F_GROUP_FOLLOW_NUM",request,Route.CONN_CRM_CG);
	}

    public static void transPicInfoParam(IData groupRealInfo,IData zrrInfo,IData jbrInfo) throws Exception {
		String synId = groupRealInfo.getString("SYNC_ID");
		IDataset picDatas=new DatasetList();
		IData zrrPicInfoZ=new DataMap();
		IData zrrPicInfoF=new DataMap();
		IData jbrPicInfoZ=new DataMap();
		IData jbrPicInfoF=new DataMap();
		IData jbrPicInfoT=new DataMap();
		
		zrrPicInfoZ.put("PIC_FILE_REL_ID", SeqMgr.getLogId());
		zrrPicInfoZ.put("SYNC_ID", synId);
		zrrPicInfoZ.put("PIC_TYPE", "ZRR_PIC_Z");
		zrrPicInfoZ.put("PIC_FILE_ID", groupRealInfo.getString("ZRR_PIC_FILEID_Z",""));
		zrrPicInfoZ.put("PIC_NAME", zrrInfo.getString("PIC_NAMEZ"));
		picDatas.add(zrrPicInfoZ);
		zrrPicInfoF.put("PIC_FILE_REL_ID", SeqMgr.getLogId());
		zrrPicInfoF.put("SYNC_ID", synId);
		zrrPicInfoF.put("PIC_TYPE", "ZRR_PIC_F");
		zrrPicInfoF.put("PIC_FILE_ID", groupRealInfo.getString("ZRR_PIC_FILEID_F",""));
		zrrPicInfoF.put("PIC_NAME", zrrInfo.getString("PIC_NAMEF"));
		picDatas.add(zrrPicInfoF);
		jbrPicInfoZ.put("PIC_FILE_REL_ID", SeqMgr.getLogId());
		jbrPicInfoZ.put("SYNC_ID", synId);
		jbrPicInfoZ.put("PIC_TYPE", "JBR_PIC_Z");
		jbrPicInfoZ.put("PIC_FILE_ID", groupRealInfo.getString("JBR_PIC_FILEID_Z",""));
		jbrPicInfoZ.put("PIC_NAME", jbrInfo.getString("PIC_NAMEZ"));
		picDatas.add(jbrPicInfoZ);
		jbrPicInfoF.put("PIC_FILE_REL_ID", SeqMgr.getLogId());
		jbrPicInfoF.put("SYNC_ID", synId);
		jbrPicInfoF.put("PIC_TYPE", "JBR_PIC_F");
		jbrPicInfoF.put("PIC_FILE_ID", groupRealInfo.getString("JBR_PIC_FILEID_F",""));
		jbrPicInfoF.put("PIC_NAME", jbrInfo.getString("PIC_NAMEF"));
		picDatas.add(jbrPicInfoF);
		jbrPicInfoT.put("PIC_FILE_REL_ID", SeqMgr.getLogId());
		jbrPicInfoT.put("SYNC_ID", synId);
		jbrPicInfoT.put("PIC_TYPE", "JBR_PIC_T");
		jbrPicInfoT.put("PIC_FILE_ID", groupRealInfo.getString("JBR_PIC_FILEID_T",""));
		jbrPicInfoT.put("PIC_NAME", jbrInfo.getString("PIC_NAMET"));
		picDatas.add(jbrPicInfoT);
		insertPicInfo(picDatas);
		
	}


    public static void insertPicInfo(IDataset picDatas) throws Exception {
		if(null!=picDatas){
	    	for(int i=0;i<picDatas.size();i++){
	    		IData request=new DataMap();
	    		IData picInfo=picDatas.getData(i);
	    		request.put("PIC_FILE_REL_ID",picInfo.getString("PIC_FILE_REL_ID"));
	    		request.put("SYNC_ID",picInfo.getString("SYNC_ID"));
	    		request.put("PIC_TYPE",picInfo.getString("PIC_TYPE"));
	    		request.put("PIC_FILE_ID",picInfo.getString("PIC_FILE_ID"));
	    		request.put("PIC_NAME",picInfo.getString("PIC_NAME"));
	    		request.put("DEAL_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    		request.put("CREATE_DATE",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    		request.put("DEAL_STATUS","0");	    
	    		Dao.insert("TF_F_GROUP_RNAME_PIC_FILE_REL", request,Route.CONN_CRM_CG);
	    	}
	    	}
		
	}
}


