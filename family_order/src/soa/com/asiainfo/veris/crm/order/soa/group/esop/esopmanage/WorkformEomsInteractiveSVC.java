package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.Clone;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttachBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttachHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateHBean;

// emos阶段性回复处理
public class WorkformEomsInteractiveSVC extends GroupOrderService {
    private static final long serialVersionUID = -3923226370322319051L;
    private static Logger logger = Logger.getLogger(WorkformEomsInteractiveSVC.class);

    private IData eosCom = new DataMap();

    public static int RESOURCES = 31;

    public static int CHANGERESOURCES = 35;

    public static int OPEN = 32;

    public static int CHANGE = 33;

    public static int CANCEL = 34;
    
//    public static int seq = 0;
    
    // 发送EOMS 或 接收EOMS
    public void record(IData inparam) throws Exception {
        eosCom = inparam;
        // esop数据处理
        String CANCLE_TAG = inparam.getString("CANCLE_TAG","");
        if ("true".equals(CANCLE_TAG)) {
        	saveHEosData();
		}else {
			saveEosData();
		}
    }

    public void updState(IData inparam) throws Exception {
        String serialNo = inparam.getString("SERIALNO", "");
        String productNo = inparam.getString("PRODUCT_NO", "");
        String busistste = inparam.getString("BUSISTSTE", "");

        IDataset eomsInfos = WorkformEomsBean.qryworkformEOMSBySerialNo(serialNo);
        if (DataUtils.isNotEmpty(eomsInfos)) {
            IData detailInfo = eomsInfos.first();
            WorkformEomsStateBean.updEomsStateByProductNo(detailInfo.getString("IBSYSID", ""), detailInfo.getString("RECORD_NUM", ""), productNo, busistste);
        }
    }

    public IData getWorkformEmos(IData inparam) throws Exception {
        String ibsysid = IDataUtil.chkParam(inparam, "IBSYSID");
        String recordNum = IDataUtil.chkParam(inparam, "RECORD_NUM");
        String operType = IDataUtil.chkParam(inparam, "OPER_TYPE");
        String serialNo = IDataUtil.chkParam(inparam,"SERIALNO");
        
        eosCom.put("OPER_TYPE", operType);
        eosCom.put("SERIALNO", serialNo);
        eosCom.put("TRADE_DRIECT", inparam.getString("TRADE_DRIECT",""));
        eosCom.put("opertype", operType);
        eosCom.put("OPPERSON", inparam.getString("OPPERSON",""));
        eosCom.put("OPCORP", inparam.getString("OPCORP",""));
        eosCom.put("OPDEPART", inparam.getString("OPDEPART",""));
        eosCom.put("OPCONTACT", inparam.getString("OPCONTACT",""));
        eosCom.put("OPTIME", inparam.getString("OPTIME",""));
        eosCom.put("ATTACHREF", inparam.getString("ATTACHREF",""));
        eosCom.put("SUB_IBSYSID", inparam.getString("SUB_IBSYSID",""));
        
        IDataset workformEmos = WorkformEomsBean.getEomsDatasetByIbsysidRecordNum(ibsysid, recordNum);
        
        if (DataUtils.isEmpty(workformEmos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:" + ibsysid + ",RECORD_NUM:"+recordNum+",未获取到Eoms数据！");
        }
        
        IData workformEmosInfo = workformEmos.first();
        
        if (Integer.parseInt(recordNum) > 0) {
            IDataset workformEmosStateS = WorkformEomsStateBean.qryEomsStateByIbsysidAndRecordNum(ibsysid, recordNum);
            if (DataUtils.isEmpty(workformEmosStateS)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:" + ibsysid + "RECORD_NUM:"+recordNum+",未获取到EomsState数据！");
            }
            
            IData workformEmosState = workformEmosStateS.first();
            workformEmosInfo.put("PRODUCTNO", workformEmosState.getString("PRODUCT_NO"));
            workformEmosInfo.put("TRADEID", workformEmosState.getString("TRADE_ID"));

		}
        
        workformEmosInfo.put("OPER_TYPE", operType);
        workformEmosInfo.put("SERIALNO", serialNo);
        
        return workformEmosInfo;
    }

    private void saveEosData() throws Exception {
    	IDataset eomsList = new DatasetList();
        IDataset attrList = new DatasetList();
        IDataset productAttrInfos = new DatasetList();
        IData eomsData = (IData) Clone.deepClone(eosCom);
        
        IDataset eomsInfos = new DatasetList(eomsData.getString("EOMS_INFOS",""));
        
        if (DataUtils.isEmpty(eomsInfos)) {
			return;
		}
        
        String ibsysid = IDataUtil.chkParam(eomsInfos.first(), "IBSYSID");
        String subIbsysid = "";
        String groupSeq = "";
        String nodeId = "";
        for (int i = 0; i < eomsInfos.size(); i++) {
        	IData eosCominfo = eomsInfos.getData(i);
        	IData workformEmosInfo = getWorkformEmos(eosCominfo);
            if (DataUtils.isEmpty(workformEmosInfo)) {
                return;
            }
            String productNo = workformEmosInfo.getString("PRODUCTNO","");
            String tradeId = workformEmosInfo.getString("TRADEID","");
            String operType = workformEmosInfo.getString("OPER_TYPE");

            // 存储EOMS SI工单基本信息表
            saveWorkformEoms(workformEmosInfo, eomsList);
            //
//            String ibsysid = workformEmosInfo.getString("IBSYSID");
            subIbsysid = workformEmosInfo.getString("SUB_IBSYSID");
            groupSeq = workformEmosInfo.getString("GROUP_SEQ", "0");
            nodeId = workformEmosInfo.getString("NODE_ID");
            String recordNum = workformEmosInfo.getString("RECORD_NUM", "-1");
            
            String stateTag = eosCominfo.getString("STATE_TAG","true");
            if ("true".equals(stateTag)) {
            	updateWorkformEomsState(ibsysid, productNo, tradeId, recordNum, operType);
    		}
            
            IDataset attrInfos = new DatasetList(eosCominfo.getString("ATTR_INFOS"));

            // 存储流程业务信息表
            saveWorkformAttr(ibsysid, subIbsysid, groupSeq, nodeId, recordNum, attrInfos, attrList);
            
		}
        logger.debug("========attrList============"+attrList);
        WorkformEomsBean.insertWorkformEoms(eomsList);
        WorkformAttrBean.insertWorkformAttr(attrList);
        
        // 存储TF_B_EOP_ATTACH表
        IDataset attachInfos = new DatasetList();
        String attachInfo = eomsData.getString("ATTACH_INFOS","");
        if (StringUtils.isNotEmpty(attachInfo)) {
        	attachInfos = new DatasetList(attachInfo);
		}
        
        if (IDataUtil.isNotEmpty(attachInfos)) {
        	IDataset attachList = new DatasetList();
            saveWorkformAttach(ibsysid, subIbsysid, groupSeq, nodeId, "0", attachInfos, attachList);
            WorkformAttachBean.insertWorkformAttach(attachList);
		}
        
        // 存储TF_B_EOP_ATTR表公共信息
        IDataset commonInfos = new DatasetList();
        String comminfo = eomsData.getString("COMMON_INFOS","");
        if (StringUtils.isNotEmpty(comminfo)) {
        	commonInfos = new DatasetList(comminfo);
		}
        if (IDataUtil.isNotEmpty(commonInfos)) {
        	IDataset commonList = new DatasetList();
            saveWorkformAttr(ibsysid, subIbsysid, groupSeq, nodeId, "0", commonInfos, commonList);
            WorkformAttrBean.insertWorkformAttr(commonList);
		}
        
        
        // 重派单，需要调用重派接口
        String operTye = eosCom.getString("opertype", "");
        if ("renewWorkSheet".equals(operTye)) {
            IData inData = new DataMap();
            inData.put("EMOS_INFO", eomsInfos);
            inData.put("IBSYSID", ibsysid);
            inData.put("SUB_IBSYSID", eosCom.getString("SUB_IBSYSID", ""));
            inData.put("WORKSHEET_EDITE", eomsData.getString("WORKSHEET_EDITE",""));//MORE-批量重派,ONE-单个重派
            logger.debug("======inData====inData========="+inData);
            CSAppCall.call("SS.WorkformReNewWorkSVC.execute", inData);
        }
    }
    
    private void saveWorkformEoms(IData workformEoms, IDataset eomsList) throws Exception {
    	
        workformEoms.put("MONTH", SysDateMgr.getCurMonth());
        workformEoms.put("GROUP_SEQ", workformEoms.getInt("GROUP_SEQ", 0) + 1);// +1
        workformEoms.put("TRADE_DRIECT",  StringUtils.isNotEmpty(eosCom.getString("TRADE_DRIECT"))? eosCom.getString("TRADE_DRIECT") : "1");
        workformEoms.put("OPER_TYPE", eosCom.getString("OPER_TYPE"));
        workformEoms.put("DEAL_STATE", "2");
        workformEoms.put("INSERT_TIME", SysDateMgr.getSysTime());
        workformEoms.put("UPDATE_TIME", SysDateMgr.getSysTime());
        workformEoms.put("SERIALNO", StringUtils.isNotEmpty(eosCom.getString("SERIALNO"))? eosCom.getString("SERIALNO") : workformEoms.getString("SERIALNO"));
        workformEoms.put("OPPERSON", StringUtils.isNotEmpty(eosCom.getString("OPPERSON"))? eosCom.getString("OPPERSON") : workformEoms.getString("OPPERSON"));
        workformEoms.put("OPCORP", StringUtils.isNotEmpty(eosCom.getString("OPCORP"))? eosCom.getString("OPCORP") : workformEoms.getString("OPCORP"));
        workformEoms.put("OPDEPART", StringUtils.isNotEmpty(eosCom.getString("OPDEPART"))? eosCom.getString("OPDEPART") : workformEoms.getString("OPDEPART"));
        workformEoms.put("OPCONTACT", StringUtils.isNotEmpty(eosCom.getString("OPCONTACT"))? eosCom.getString("OPCONTACT") : workformEoms.getString("OPCONTACT"));
        workformEoms.put("OPTIME", StringUtils.isNotEmpty(eosCom.getString("OPTIME"))? eosCom.getString("OPTIME") : workformEoms.getString("OPTIME"));
        workformEoms.put("OPDETAIL", StringUtils.isNotEmpty(eosCom.getString("OPDETAIL"))? eosCom.getString("OPDETAIL") : workformEoms.getString("OPDETAIL"));
        workformEoms.put("ATTACHREF", "");//综资附件太大，不能完全展示，先不存；如果要存，应该存在tf_b_eop_attach表
        		
        eomsList.add(workformEoms);
    }

    private void updateWorkformEomsState(String ibsysid, String productNo, String tradeId, String recordNum, String operType) throws Exception {
    	
        String newState = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", operType});
        
        
        if (Integer.parseInt(recordNum) == 0) {
        	IDataset stateDataset = WorkformEomsStateBean.queryEomsStateByIbsysid(ibsysid);
        	if (DataUtils.isEmpty(stateDataset)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:" + ibsysid + ",未获取到EomsState数据！");
            }
        	
        	WorkformEomsStateBean.updEomsStateByIbsysId(ibsysid, newState);
        	
		}else if((Integer.parseInt(recordNum) > 0)) {
			
	        IDataset details = WorkformEomsStateBean.qryEomsStateByIbsysidTradeId(ibsysid, recordNum);
	        if (DataUtils.isEmpty(details)) {
	            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据RECORD_NUM:" + recordNum + ",IBSYSID:" + ibsysid + ",未获取到EomsState数据！");
	        }
	        IData detail = details.first();
	        detail.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
	        detail.put("BUSI_STATE", newState);
	
	        if (StringUtils.isBlank(tradeId)) {
	            tradeId = detail.getString("TRADE_ID", "");
	        }
	        if (StringUtils.isBlank(productNo)) {
	            productNo = detail.getString("PRODUCT_NO", "");
	        }
	        WorkformEomsStateBean.updEomsStateByPk(ibsysid, recordNum, tradeId, productNo, newState);
		}
    }

    private void saveWorkformAttr(String ibsysid, String subIbsysid, String groupSeq, String nodeId, String recordNum, IDataset workformAttrs, IDataset attrList) throws Exception {

        for (int i = 0; i < workformAttrs.size(); i++) {
            IData workformAttr = workformAttrs.getData(i);
            IData attr = new DataMap();
            attr.put("RECORD_NUM", recordNum);
            attr.put("ATTR_CODE", workformAttr.getString("ATTR_CODE"));
            attr.put("ATTR_NAME", workformAttr.getString("ATTR_NAME"));
            attr.put("ATTR_VALUE", workformAttr.getString("ATTR_VALUE"));
            attr.put("SUB_IBSYSID", subIbsysid);
            attr.put("IBSYSID", ibsysid);
            attr.put("SEQ", SeqMgr.getAttrSeq());
            attr.put("GROUP_SEQ", groupSeq);
            attr.put("NODE_ID", nodeId);
            attr.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            attr.put("UPDATE_TIME", SysDateMgr.getSysTime());
            attrList.add(attr);
        }
    }
    
    private void saveWorkformAttach(String ibsysid, String subIbsysid, String groupSeq, String nodeId, String recordNum, IDataset workformAttrachs, IDataset attachList) throws Exception {
        for (int i = 0; i < workformAttrachs.size(); i++) {
            IData workformAttach = workformAttrachs.getData(i);
            IData attach = new DataMap();
            attach.put("RECORD_NUM", recordNum);
            attach.put("DISPLAY_NAME", workformAttach.getString("ATTACH_NAME",""));
            attach.put("ATTACH_NAME", workformAttach.getString("ATTACH_NAME",""));
            attach.put("ATTACH_URL", workformAttach.getString("ATTACH_URL",""));
            attach.put("ATTACH_LENGTH", workformAttach.getString("ATTACH_LENGTH",""));
            attach.put("SUB_IBSYSID", subIbsysid);
            attach.put("IBSYSID", ibsysid);
            attach.put("SEQ", SeqMgr.getAttrSeq());
            attach.put("GROUP_SEQ", groupSeq);
            attach.put("NODE_ID", nodeId);
            attach.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            attach.put("UPDATE_TIME", SysDateMgr.getSysTime());
            attach.put("INSERT_TIME", SysDateMgr.getSysTime());
            attach.put("ATTACH_LOCAL_PATH", workformAttach.getString("ATTACH_LOCAL_PATH",""));
            attach.put("ATTACH_CITY_CODE", workformAttach.getString("ATTACH_CITY_CODE",""));
            attach.put("ATTACH_EPARCHY_CODE", workformAttach.getString("ATTACH_EPARCHY_CODE",""));
            attach.put("ATTACH_DEPART_ID", workformAttach.getString("ATTACH_DEPART_ID",""));
            attach.put("ATTACH_DEPART_NAME", workformAttach.getString("ATTACH_DEPART_NAME",""));
            attach.put("ATTACH_STAFF_ID", workformAttach.getString("ATTACH_STAFF_ID",""));
            attach.put("ATTACH_STAFF_NAME", workformAttach.getString("ATTACH_STAFF_NAME",""));
            attach.put("ATTACH_STAFF_PHONE", workformAttach.getString("ATTACH_STAFF_PHONE",""));
            attach.put("FILE_ID", workformAttach.getString("FILE_ID",""));
            attach.put("REMARK", "");
            attach.put("VALID_TAG", "0");
            attach.put("ATTACH_TYPE", workformAttach.getString("ATTACH_TYPE",""));
            attachList.add(attach);
        }
    }
    
    private void saveHEosData() throws Exception {
    	IDataset eomsList = new DatasetList();
        IDataset attrList = new DatasetList();
        IData eomsData = (IData) Clone.deepClone(eosCom);
        
        IDataset eomsInfos = new DatasetList(eomsData.getString("EOMS_INFOS",""));
        
        if (DataUtils.isEmpty(eomsInfos)) {
			return;
		}
        
        String ibsysid = IDataUtil.chkParam(eomsInfos.first(), "IBSYSID");
        String subIbsysid = "";
        String groupSeq = "";
        String nodeId = "";
        for (int i = 0; i < eomsInfos.size(); i++) {
        	IData eosCominfo = eomsInfos.getData(i);
        	IData workformEmosInfo = getWorkformHEmos(eosCominfo);
            if (DataUtils.isEmpty(workformEmosInfo)) {
                return;
            }
            String productNo = workformEmosInfo.getString("PRODUCTNO","");
            String tradeId = workformEmosInfo.getString("TRADEID","");
            String operType = workformEmosInfo.getString("OPER_TYPE");

            // 存储EOMS SI工单基本信息表
            saveWorkformEoms(workformEmosInfo, eomsList);
            
            subIbsysid = workformEmosInfo.getString("SUB_IBSYSID");
            groupSeq = workformEmosInfo.getString("GROUP_SEQ", "0");
            nodeId = workformEmosInfo.getString("NODE_ID");
            String recordNum = workformEmosInfo.getString("RECORD_NUM", "-1");
            
            String stateTag = eosCominfo.getString("STATE_TAG","true");
            if ("true".equals(stateTag)) {
            	updateWorkformHEomsState(ibsysid, productNo, tradeId, recordNum, operType);
    		}
            
            IDataset attrInfos = new DatasetList(eosCominfo.getString("ATTR_INFOS"));

            // 存储流程业务信息表
            saveWorkformAttr(ibsysid, subIbsysid, groupSeq, nodeId, recordNum, attrInfos, attrList);
            
		}
        logger.debug("========attrList============"+attrList);
        WorkformEomsHBean.insertWorkformEoms(eomsList);
        WorkformAttrHBean.insertWorkformAttrH(attrList);
        
        // 存储TF_B_EOP_ATTACH表
        IDataset attachInfos = new DatasetList();
        String attachInfo = eomsData.getString("ATTACH_INFOS","");
        if (StringUtils.isNotEmpty(attachInfo)) {
        	attachInfos = new DatasetList(attachInfo);
		}
        
        if (IDataUtil.isNotEmpty(attachInfos)) {
        	IDataset attachList = new DatasetList();
            saveWorkformAttach(ibsysid, subIbsysid, groupSeq, nodeId, "0", attachInfos, attachList);
            WorkformAttachHBean.insertWorkformAttachH(attachList);
		}
        
        // 存储TF_B_EOP_ATTR表公共信息
        IDataset commonInfos = new DatasetList();
        String comminfo = eomsData.getString("COMMON_INFOS","");
        if (StringUtils.isNotEmpty(comminfo)) {
        	commonInfos = new DatasetList(comminfo);
		}
        if (IDataUtil.isNotEmpty(commonInfos)) {
        	IDataset commonList = new DatasetList();
            saveWorkformAttr(ibsysid, subIbsysid, groupSeq, nodeId, "0", commonInfos, commonList);
            WorkformAttrHBean.insertWorkformAttrH(commonList);
		}
    }
    
    
    public IData getWorkformHEmos(IData inparam) throws Exception {
        String ibsysid = IDataUtil.chkParam(inparam, "IBSYSID");
        String recordNum = IDataUtil.chkParam(inparam, "RECORD_NUM");
        String operType = IDataUtil.chkParam(inparam, "OPER_TYPE");
        String serialNo = IDataUtil.chkParam(inparam,"SERIALNO");
        
        eosCom.put("OPER_TYPE", operType);
        eosCom.put("SERIALNO", serialNo);
        eosCom.put("TRADE_DRIECT", inparam.getString("TRADE_DRIECT",""));
        eosCom.put("opertype", operType);
        eosCom.put("OPPERSON", inparam.getString("OPPERSON",""));
        eosCom.put("OPCORP", inparam.getString("OPCORP",""));
        eosCom.put("OPDEPART", inparam.getString("OPDEPART",""));
        eosCom.put("OPCONTACT", inparam.getString("OPCONTACT",""));
        eosCom.put("OPTIME", inparam.getString("OPTIME",""));
        eosCom.put("ATTACHREF", inparam.getString("ATTACHREF",""));
        eosCom.put("SUB_IBSYSID", inparam.getString("SUB_IBSYSID",""));
        
        IDataset workformEmos = WorkformEomsHBean.getHEomsDatasetByIbsysidRecordNum(ibsysid, recordNum);
        
        if (DataUtils.isEmpty(workformEmos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:" + ibsysid + ",RECORD_NUM:"+recordNum+",未获取到Eoms数据！");
        }
        
        IData workformEmosInfo = workformEmos.first();
        
        if (Integer.parseInt(recordNum) > 0) {
            IDataset workformEmosStateS = WorkformEomsStateHBean.qryHEomsStateByIbsysidAndRecordNum(ibsysid, recordNum);
            if (DataUtils.isEmpty(workformEmosStateS)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:" + ibsysid + "RECORD_NUM:"+recordNum+",未获取到EomsState数据！");
            }
            
            IData workformEmosState = workformEmosStateS.first();
            workformEmosInfo.put("PRODUCTNO", workformEmosState.getString("PRODUCT_NO"));
            workformEmosInfo.put("TRADEID", workformEmosState.getString("TRADE_ID"));

		}
        
        workformEmosInfo.put("OPER_TYPE", operType);
        workformEmosInfo.put("SERIALNO", serialNo);
        
        return workformEmosInfo;
    }
    
    
    private void updateWorkformHEomsState(String ibsysid, String productNo, String tradeId, String recordNum, String operType) throws Exception {
    	
        String newState = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", operType});
        
        
        if (Integer.parseInt(recordNum) == 0) {
        	IDataset stateDataset = WorkformEomsStateHBean.queryEomsStateByIbsysid(ibsysid);
        	if (DataUtils.isEmpty(stateDataset)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:" + ibsysid + ",未获取到EomsState数据！");
            }
        	
        	WorkformEomsStateHBean.updHEomsStateByIbsysId(ibsysid, newState);
        	
		}else if((Integer.parseInt(recordNum) > 0)) {
			
	        IDataset details = WorkformEomsStateHBean.qryHEomsStateByIbsysidTradeId(ibsysid, recordNum);
	        if (DataUtils.isEmpty(details)) {
	            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据RECORD_NUM:" + recordNum + ",IBSYSID:" + ibsysid + ",未获取到EomsState数据！");
	        }
	        IData detail = details.first();
	        detail.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
	        detail.put("BUSI_STATE", newState);
	
	        if (StringUtils.isBlank(tradeId)) {
	            tradeId = detail.getString("TRADE_ID", "");
	        }
	        if (StringUtils.isBlank(productNo)) {
	            productNo = detail.getString("PRODUCT_NO", "");
	        }
	        WorkformEomsStateHBean.updHEomsStateByPk(ibsysid, recordNum, tradeId, productNo, newState);
		}
    }
    
    public void updateEomsState(IData inparam) throws Exception
    {
    	String ibsysid = inparam.getString("IBSYSID","");
    	String productNo = inparam.getString("PRODUCT_NO","");
    	String tradeId = inparam.getString("TRADE_ID","");
    	String recordNum = inparam.getString("RECORD_NUM","");
    	String operType = inparam.getString("OPER_TYPE","");
    	updateWorkformEomsState(ibsysid, productNo, tradeId, recordNum, operType);
    }
}
