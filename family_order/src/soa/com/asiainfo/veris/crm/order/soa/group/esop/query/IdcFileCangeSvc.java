package com.asiainfo.veris.crm.order.soa.group.esop.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.file.FileUtil;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.FileManHelper;
import com.ailk.common.util.FtpUtil;
import com.ailk.org.apache.commons.io.FilenameUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class IdcFileCangeSvc extends CSBizService {
    private static final long serialVersionUID = 1L;
    private static final Logger logger=Logger.getLogger(IdcFileCangeSvc.class);
    public static void dealIdcFileCange(IData param) throws Exception {
      	logger.info("IdcFileCangeSvc-dealIdcFileCange param:"+param);
    	String ibSysid=param.getString("IBSYSID");
    	String filePathURL=null;
    	String charset=null;
    	IDataset configInfoList = EweConfigQry.qryByConfigName("FILEPATH","0");
        if (DataUtils.isNotEmpty(configInfoList))
        {
        	for(int i=0,size=configInfoList.size();i<size;i++){
        		IData configInfo=configInfoList.getData(i);
        		if("IDC".equals(configInfo.getString("PARAMNAME"))){
        			filePathURL=configInfo.getString("PARAMVALUE");
        			charset=configInfo.getString("RSRV_STR1");
        			break;
        		}
        	}
        }
        if(filePathURL==null){
        	filePathURL="/home/upload/groupserv/EOP/IDC";
        	charset="UTF-8";
        }
    	IData qrySubscribePoolParam1=new DataMap();
        qrySubscribePoolParam1.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam1.put("STATE", "F");
        qrySubscribePoolParam1.put("POOL_VALUE",ibSysid );
        IDataset qrySubscribePoolParamList1=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam1);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList1)||qrySubscribePoolParamList1.size()==0||
        		IDataUtil.isEmpty(qrySubscribePoolParamList1.getData(0))){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+ibSysid+"查询tf_b_eop_subscribe_pool表数据不存在!");
        }
        IData qrySubscribePoolParam=new DataMap();
        qrySubscribePoolParam.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam.put("STATE", "F");
        qrySubscribePoolParam.put("REL_IBSYSID", qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID"));
        IDataset qrySubscribePoolParamList=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList)||qrySubscribePoolParamList.size()==0){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID")+"查询tf_b_eop_subscribe_pool 表数据REL_IBSYSID不存在!");
        }
    	for (int s = 0; s < qrySubscribePoolParamList.size(); s++) {
        	IData qrySubscribePoolParamData=qrySubscribePoolParamList.getData(s);
	    	if (DataUtils.isNotEmpty(qrySubscribePoolParamData)) {
	        	String poolIbsysid=qrySubscribePoolParamData.getString("POOL_VALUE");

	        	//查询TF_B_EWE
	        	IData eweparam = new DataMap();
	            eweparam.put("BI_SN", poolIbsysid);
	            IDataset eweInfos = Dao.qryByCodeParser("TF_B_EWE", "SEL_BY_BISN", eweparam, Route.getJourDb(BizRoute.getRouteId()));
	            if(IDataUtil.isEmpty(eweInfos)||IDataUtil.isEmpty(eweInfos.getData(0))
	            		||"".equals(eweInfos.getData(0).getString("BI_SN",""))){
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE表流程信息为空!");
	            }
	            String poolBusiformId=eweInfos.getData(0).getString("BUSIFORM_ID");
	            //查询TF_B_EWE_NODE
	            IData eweNodeparam = new DataMap();
	            eweNodeparam.put("BUSIFORM_ID", poolBusiformId);
	            IDataset eweNodeInfos = Dao.qryByCodeParser("TF_B_EWE_NODE", "SEL_BY_BUSIFORM_ID", eweNodeparam, Route.getJourDb(BizRoute.getRouteId()));
	            if(poolIbsysid.equals(ibSysid)&&
	            		(IDataUtil.isEmpty(eweNodeInfos)||eweNodeInfos.size()!=2
		            	)
	              )
	            {
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表数据不正常,强行停止!");
	            }else if(!poolIbsysid.equals(ibSysid)&&
		            		(IDataUtil.isEmpty(eweNodeInfos)
		            		||eweNodeInfos.size()!=1
		            		||IDataUtil.isEmpty(eweNodeInfos.getData(0))
		            		||!"eomsWait".equals(eweNodeInfos.getData(0).getString("NODE_ID",""))
	        				||"M".equals(eweNodeInfos.getData(0).getString("STATE",""))
	        				)
        				)
	            {
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表数据不正常,强行停止!");
	            }
	            String poolSubIbsysId=null;
	            for (int r = 0; r < eweNodeInfos.size();r++) {
	            	if(IDataUtil.isNotEmpty(eweNodeInfos.getData(r))&&
	            			("eomsProess".equals(eweNodeInfos.getData(r).getString("NODE_ID",""))||"eomsWait".equals(eweNodeInfos.getData(r).getString("NODE_ID","")))){
	            		poolSubIbsysId=eweNodeInfos.getData(r).getString("SUB_BI_SN","");break;
	            	}
	            }
	            if(poolSubIbsysId==null){
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表eomsProess数据不正常,强行停止!");
	            }
    	
				IDataset attachInfos = WorkformAttachBean.qryEopAttrBySubIbsysid(poolSubIbsysId);
				
				if(DataUtils.isNotEmpty(attachInfos))
				{
					for(int i = 0 ; i < attachInfos.size() ; i ++)
			    	{
			    		IData attachInfo = attachInfos.getData(i);
			    		String fileId = attachInfo.getString("FILE_ID","");
			    		
			    		IData inputfile = new DataMap();
			       	 	((DataMap) inputfile).put("FILE_ID", fileId);  //变量，自己写语句循环
			       	 	((DataMap) inputfile).put("ACTION", "0");
			       	 	IDataset infosfiles = CSAppCall.call("SYS_FtpFileMgr", inputfile);
			       	 	logger.info("IdcFileCangeSvc-dealIdcFileCange infosfiles:"+infosfiles);
			       	 	if (IDataUtil.isNotEmpty(infosfiles)) {
			       	     IData infofile = infosfiles.first();
			       	     String fileName = infofile.getString("FILE_NAME");
			       	     String filePath = infofile.getString("FILE_PATH");
			       	     String ftpSite = infofile.getString("FTP_SITE");
			       	     
			       	     

			       	  

			       	     String localFilePath = FilenameUtils.concat(System.getProperty("java.io.tmpdir", ""), fileId);
			           	 logger.info("IdcFileCangeSvc-dealIdcFileCange localFilePath:"+localFilePath);
			       	     File file = new File(localFilePath);

			       	     IData input = new DataMap();
			       	     ((DataMap) input).put("ftpSite",ftpSite);
			       	     ((DataMap) input).put("ACTION","4");

			       	     IDataset outputftp = CSAppCall.call("SYS_FtpFileMgr", input);
//			       	     IData rs = outputftp.getData().getData(0);
			           	 logger.info("IdcFileCangeSvc-dealIdcFileCange outputftp:"+outputftp);

			       	     IData rs = outputftp.getData(0);
			       	     String acctUsr = rs.getString("ACCT_USR");
			       	     String acctPwd = rs.getString("ACCT_PWD");
			       	     
			       	     String url = rs.getString("FTP_URL");
			       	     String server = null;
			       	     int index = url.indexOf(":");
			       	     int port;
			       	     if (index == -1) {
			       	         server = url;
			       	         port = 21;
			       	     } else {
			       	         server = url.substring(0, index);
			       	         port = Integer.parseInt(url.substring(index + 1));
			       	     }

			       	     rs.put("FTP_SERVER", server);
			       	     rs.put("FTP_PORT", port);

			       	     FileUtil fileUtil = new FileUtil(rs);

			       	     fileUtil.setAutoRelease(false);
			       	     boolean changed = false;
			       	     changed = fileUtil.changeDirectory(filePath);
			       	     if(!changed){
				            CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"生成附件数据异常,已强行终止，fileId："+fileId+"!");
			       	     }
			       	     OutputStream out = FileManHelper.getOutputStream(file);
			       	     fileUtil.retrieveFile(out, fileId);//变量，自己写语句循环
			       	     fileUtil.releaseResourse();

			       	     String real = URLEncoder.encode(fileName, charset);//编码格式
//			 			 real =URLDecoder.decode(fileName, charset);//解码
			           	 logger.info("IdcFileCangeSvc-dealIdcFileCange filePathURL:"+filePathURL);
			       	     String newFilePath = FilenameUtils.concat(System.getProperty("java.io.tmpdir", ""), real);//不涉及主机传输，自己定义一个目录存放
			       	     File fileto = new File(newFilePath);
			           	 logger.info("IdcFileCangeSvc-dealIdcFileCange newFilePath:"+newFilePath);
			       	     OutputStream outto = FileManHelper.getOutputStream(fileto);
			       	     FileManHelper.writeInputToOutput(new FileInputStream(file), outto);
			       	     
			             FtpUtil destftp = new FtpUtil(server, acctUsr, acctPwd, filePathURL);
			             destftp.setFileType(2);
	                     destftp.uploadFile(newFilePath, filePathURL + "/" + real);
	                     //删除本地文件
	                     file.delete();
	                     fileto.delete();
			       	 }
			    		
			    		
			    		
			    		
			    	}
				}
	    	}
    	}
    	
    	 
    }
}
