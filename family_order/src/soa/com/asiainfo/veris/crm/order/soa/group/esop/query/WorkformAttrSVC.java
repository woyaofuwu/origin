package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import org.apache.commons.lang.StringUtils;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.eopdatatrans.EopDataTransBean;

public class WorkformAttrSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    public static IDataset qryAttrBySubIbsysidAndGroupseq(IData param) throws Exception {
        String subIbsysid = param.getString("SUB_IBSYSID");
        String groupSeq = param.getString("GROUP_SEQ");
        return WorkformAttrBean.qryAttrBySubIbsysidAndGroupseq(subIbsysid, groupSeq);
    }

    public IDataset qryAttrByIbsysid(IData input) throws Exception {
        String ibsysId = input.getString("IBSYSID");
        return WorkformAttrBean.qryAttrByIbsysid(ibsysId);
    }

    public static IDataset qryrejectedlineAttrByIbsysId(IData input) throws Exception {
        IDataset retDataList = new DatasetList();
        IDataset paramDataList = new DatasetList();
        IDataset detailDataList = new DatasetList();
        String busiOperType = input.getString("BUSIFORM_OPER_TYPE");
        String operType = busiOperType.equalsIgnoreCase("20") ? "0" : busiOperType.equalsIgnoreCase("25") ? "1" : "2";
        String ibSysId = input.getString("IBSYSID", "");
        if(StringUtils.isEmpty(ibSysId)) {
            return paramDataList;
        }
        IDataset eopNodeInfos = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibSysId, "eOpenNewWorkSheet");
        if(IDataUtil.isEmpty(eopNodeInfos)) {
            return paramDataList;
        }

        IDataset eopProductInfos = WorkformProductBean.qryProductByIbsysid(ibSysId);
        if(IDataUtil.isEmpty(eopProductInfos)) {
            return paramDataList;
        }
        IDataset mebParams = new DatasetList();
        IDataset mebOthers = new DatasetList();
        for (int i = 1; i < eopProductInfos.size(); i++) {
            IData mebParam = new DataMap();
            IData mebOther = new DataMap();
            IDataset offerChaList = new DatasetList();
            mebOther.put("MEB_SERIAL_NUMBER", eopProductInfos.getData(i).getString("SERIAL_NUMBER"));
            mebOther.put("RECODR_NUM", i);
            mebParam.put("OFFER_CHA", offerChaList);
            IDataset eopAttrInfos = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(eopNodeInfos.first().getString("SUB_IBSYSID"), String.valueOf(i));
            mebParam.put("OFFER_CODE", eopProductInfos.getData(i).getString("PRODUCT_ID"));
            mebParam.put("OFFER_NAME", eopProductInfos.getData(i).getString("PRODUCT_NAME"));
            mebParam.put("OFFER_TYPE", input.getString("BUSI_TYPE"));
            mebParam.put("OPER_CODE", operType);
            mebParam.put("USER_ID", input.getString("USER_ID", ""));
            mebParam.put("OFFER_ID", eopProductInfos.getData(i).getString("RSRV_STR1"));
            IData attrInfo = new DataMap();
            attrInfo.put("IBSYSID", ibSysId);
            attrInfo.put("RECORD_NUM", String.valueOf(i));
            for (int j = 0; j < eopAttrInfos.size(); j++) {
                IData paramInfo = new DataMap();
                paramInfo.put("ATTR_NAME", eopAttrInfos.getData(j).getString("ATTR_NAME"));
                paramInfo.put("ATTR_VALUE", eopAttrInfos.getData(j).getString("ATTR_VALUE"));
                paramInfo.put("ATTR_CODE", eopAttrInfos.getData(j).getString("ATTR_CODE"));
                attrInfo.put(eopAttrInfos.getData(j).getString("ATTR_CODE"), eopAttrInfos.getData(j).getString("ATTR_VALUE"));
                offerChaList.add(paramInfo);
            }

            mebParams.add(mebParam);
            mebOthers.add(mebOther);
            paramDataList.add(attrInfo);
        }

        //拼装SELECT_ELEMENT_LIST
        IDataset selectElementLists = new DatasetList();
        for (int i = 1; i < eopProductInfos.size(); i++) {
            IDataset selectElementList = new DatasetList();
            // SVC表的数据
            IDataset svcInfos = EopDataTransBean.buildWorkformSvc(eopNodeInfos.first().getString("SUB_IBSYSID"), String.valueOf(i));
            // DIS表的数据
            IDataset disInfos = EopDataTransBean.buildWorkformDis(eopNodeInfos.first().getString("SUB_IBSYSID"), String.valueOf(i));
            selectElementList.addAll(svcInfos);
            selectElementList.addAll(disInfos);
            selectElementLists.add(selectElementList);
        }

        // 分离数据，组合成需要得结构
        for (int i = 0; i < paramDataList.size(); i++) {
            IData detailData = new DataMap();
            detailData.put("SELECT_ELEMENT_LIST", selectElementLists.getDataset(i));
            IDataset params = new DatasetList();
            params.add(mebParams.getData(i));
            detailData.put("MEB_PARAMS", params);
            detailData.put("MEB_OTHER", mebOthers.getData(i));
            paramDataList.getData(i).put("DETAIL_DATA", detailData);
        }

        retDataList.add(paramDataList);
        retDataList.add(detailDataList);

        return retDataList;
    }

    public static IDataset querylineAttrByIbsysid(IData input) throws Exception {

        IData lineParam = new DataMap();
        IDataset returnEomsSubInfos = new DatasetList();
        String ibSysId = input.getString("IBSYSID", "");
        if(StringUtils.isEmpty(ibSysId)) {
            return returnEomsSubInfos;
        }
        lineParam.put("IBSYSID", ibSysId);
        IDataset eomsSubInfos = WorkformAttrBean.qryAttrByIbsysid(ibSysId);

        if(IDataUtil.isNotEmpty(eomsSubInfos)) {
            for (int i = 0; i < eomsSubInfos.size(); i++) {
                String recordNum = eomsSubInfos.getData(i).getString("RECORD_NUM");
                int num = Integer.valueOf(recordNum);
                if(num > returnEomsSubInfos.size()) {
                    IData eomsSubInfo = new DataMap();
                    eomsSubInfo.put("IBSYSID", ibSysId);
                    eomsSubInfo.put("RECORD_NUM", eomsSubInfos.getData(i).getString("RECORD_NUM"));
                    returnEomsSubInfos.add(eomsSubInfo);
                }

                if(num > 0) {
                    returnEomsSubInfos.getData(num - 1).put(eomsSubInfos.getData(i).getString("ATTR_CODE"), eomsSubInfos.getData(i).getString("ATTR_VALUE"));
                }
            }
        }
        return returnEomsSubInfos;
    }

    public IData qryAttrByIbsysidRecordCode(IData input) throws Exception {
        String ibsysId = input.getString("IBSYSID");
        String attrCode = input.getString("ATTR_CODE");
        String recordNum = input.getString("RECORD_NUM");
        return WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId, attrCode, recordNum);
    }

    public static IDataset qryAttrBySubIbsysidAndRecordNum(IData param) throws Exception {
        String subIbsysid = param.getString("SUB_IBSYSID");
        String recordNum = param.getString("RECORD_NUM");
        return WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysid, recordNum);
    }

    public static IDataset qryEopAttrBySubIbsysid(IData param) throws Exception {
        String subIbsysid = param.getString("SUB_IBSYSID");
        return WorkformAttrBean.qryEopAttrBySubIbsysid(subIbsysid);
    }

    public static IDataset qryEopAttrFromMaxSubIbsysidByIbsysid(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID");
        return WorkformAttrBean.qryEopAttrFromMaxSubIbsysidByIbsysid(ibsysid);
    }

    public static IData qryBySubIbsysidRecordCode(IData param) throws Exception {
        String subIbsysid = param.getString("SUB_IBSYSID", "");
        String attrCode = param.getString("ATTR_CODE", "");
        String recordNum = param.getString("RECORD_NUM", "");

        return WorkformAttrBean.qryAttrBySubIbsysidRecordCode(subIbsysid, attrCode, recordNum);
    }

    public static IData qryByIbsysidProductNo(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID", "");
        String productNo = param.getString("PRODUCTNO", "");
        return WorkformAttrBean.qryByIbsysidProductNo(ibsysid, productNo);
    }

    public static IDataset qryInfoBySubIbsysid(IData param) throws Exception {
        return WorkformAttrBean.qryInfoBySubIbsysid(param);
    }

    public IDataset qryHisInfoByProductNo(IData param) throws Exception {
        return WorkformAttrBean.qryHisInfoByProductNo(param);
    }

    public IDataset qryHisInfoByIbsysidAndRecordnum(IData param) throws Exception {
        return WorkformAttrBean.qryHisInfoByIbsysidAndRecordnum(param);
    }

    public static IData qryByIbsysidProductNoNodeId(IData param) throws Exception {
        return WorkformAttrBean.qryByIbsysidProductNoNodeId(param);
    }
    
    public static IDataset getInfoByIbsysidRecordNum(IData param) throws Exception{
		return WorkformAttrBean.getInfoByIbsysidRecordNum(param);
	}
    
    public static IDataset qryAttrByIbsysidRecordNumNodeId(IData param) throws Exception{
		return WorkformAttrBean.qryAttrByIbsysidRecordNumNodeId(param);
	}
    
    public static IDataset getInfoByIbsysidAttrtype(IData param) throws Exception{
		return WorkformAttrBean.getInfoByIbsysidAttrtype(param);
	}
    public static IDataset getEopAttrToList(IData param) throws Exception{
		return WorkformAttrBean.getEopAttrToList(param);
	}
    public static IDataset getEopAttrToListForHis(IData param) throws Exception{
		return WorkformAttrBean.getEopAttrToListForHis(param);
	}
	public static IDataset getIbsysidByAttrcodeAndAttrvalue(IData param) throws Exception {
		return WorkformAttrBean.getIbsysidByAttrcodeAndAttrvalue(param);
	}

    public static IDataset getNewInfoByIbsysidAndNodeId(IData param) throws Exception {
        return WorkformAttrBean.getNewInfoByIbsysidAndNodeId(param);
    }

    public static IDataset getNewLineInfoList(IData param) throws Exception {
        return WorkformAttrBean.getNewLineInfoList(param);
    }

    public static IDataset getRecordNumByIbsysid(IData param) throws Exception {
        return WorkformAttrBean.getRecordNumByIbsysid(param);
    }
    
    public static IDataset qryRecordNumBySubIbsysid(IData param) throws Exception {
    	String subIbsysid = param.getString("SUB_IBSYSID","");
        return WorkformAttrBean.qryRecordNumBySubIbsysid(subIbsysid);
    }
    
	public static IDataset qryMaxSubibsysyid(IData param) throws Exception{
        return WorkformAttrHBean.qryMaxSubibsysyid(param);
    }
	public static IDataset qryEopAttrBySubIbsysidRecordNum(IData param) throws Exception{
		String subIbsysid = param.getString("SUB_IBSYSID");
		String recordNum = param.getString("RECORD_NUM");
        return WorkformAttrHBean.qryEopAttrBySubIbsysidRecordNum(subIbsysid,recordNum);
    }
	public static IDataset getMaxsubEopAttrToListForHis(IData param) throws Exception{
        return WorkformAttrHBean.getMaxsubEopAttrToListForHis(param);
    }
	public static IDataset qryLineNoByIbsysid(IData param) throws Exception{
        return WorkformAttrHBean.qryLineNoByIbsysid(param);
    }
	
	public static IDataset qryAttrByOperTypeEoms(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID", "");
		String subIbsysid = param.getString("SUB_IBSYSID", "");
		String recordNum = param.getString("RECORD_NUM", "");
		String serialNo = param.getString("SERIALNO", "");
		String groupSeq = param.getString("GROUP_SEQ", "");

		return WorkformAttrBean.qryAttrByOperTypeEoms(subIbsysid, ibsysid,groupSeq , recordNum, serialNo);
	}
	
	public static IData qryMaxSubIbsysId(IData param) throws Exception
	{
		IDataset attrHisInfo =  WorkformAttrHBean.qryMaxSubIbsysId(param);
		if(IDataUtil.isNotEmpty(attrHisInfo)) {
			return attrHisInfo.getData(0);
		}else {
			return null;
		}
	}
	
	public static IData qryMaxRecordNumByIbsysid(IData param) throws Exception{
		IDataset attrHisInfo = WorkformAttrHBean.qryMaxRecordNumByIbsysid(param);
		if(IDataUtil.isNotEmpty(attrHisInfo)) {
			return attrHisInfo.getData(0);
		}else {
			return null;
		}
    }
	
	public static IDataset qryEopAttrByIbsysidNodeid(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID", "");
		String nodeId = param.getString("NODE_ID", "");
		String recordNum = param.getString("RECORD_NUM", "");
		
		return WorkformAttrBean.qryEopAttrByIbsysidNodeid(ibsysid,nodeId,recordNum);
	}
	public static IDataset qryMaxEopAttrByIbsysidNodeid(IData param) throws Exception
	{
		String ibsysid = param.getString("IBSYSID", "");
		String nodeId = param.getString("NODE_ID", "");
		
		return WorkformAttrBean.qryMaxEopAttrByIbsysidNodeid(ibsysid,nodeId);
	}
	
	public static IDataset qryAttrBySubIbsysidAndRecordNumGroupSeq(IData param) throws Exception
	{
		String subIbsysid = param.getString("SUB_IBSYSID");
		String groupSeq = param.getString("GROUP_SEQ");
		String recordNum = param.getString("RECORD_NUM");
		
		return WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysid,groupSeq,recordNum);
	}
	
	
	public static IDataset qryreAuditAttrByIbsysId(IData param) throws Exception
	{
        IDataset paramDataList = new DatasetList();
        String ibSysId = param.getString("IBSYSID","");
        String nodeId = param.getString("NODE_ID","");
       
        IDataset eopNodeInfos = WorkformNodeBean.qryNodeByIbsysidNodeDesc(ibSysId, nodeId);
        if(IDataUtil.isEmpty(eopNodeInfos)) {
            return paramDataList;
        }
        
        IDataset eopAttrInfos = WorkformAttrBean.qryAttrRecodeNumBySubIbsysid(eopNodeInfos.first().getString("SUB_IBSYSID"));
        
        if(IDataUtil.isEmpty(eopAttrInfos)) {
            return paramDataList;
        }
        
        return eopAttrInfos;
	}

    public static IDataset qryHisEopAttrByAttrCodeAttrValue(IData param) throws Exception {
        String attrCode = param.getString("ATTR_CODE");
        String attrValue = param.getString("ATTR_VALUE");
        return WorkformAttrHBean.qryHisEopAttrByAttrCodeAttrValue(attrCode, attrValue);
    }

    public static IDataset qryNewHisEopAttrByIbsysidAndNodeId(IData param) throws Exception {
        return WorkformAttrHBean.qryNewHisEopAttrByIbsysidAndNodeId(param);
    }
    
    public IDataset getInfoBySubIbsysidRecordNum(IData param) throws Exception {
    	return WorkformAttrHBean.getInfoBySubIbsysidRecordNum(param);
    }
    
    public IDataset qryMaxAttrAndHisAttrByAttrCode(IData param) throws Exception {
        String isFinish = param.getString("IS_FINISH");
        if("true".equals(isFinish)){
            return WorkformAttrHBean.qryMaxHisAttrByAttrCode(param);
        }else{
            return WorkformAttrBean.qryMaxNewAttrByAttrCode(param);
        }
    }
    
    public IDataset qryMaxAttrByAttrCode(IData param) throws Exception {
    	String ibsysid =  param.getString("IBSYSID","");
    	String nodeId =  param.getString("NODE_ID","");
    	String recordNum =  param.getString("RECORD_NUM","");
    	String attrCode =  param.getString("ATTR_CODE","");
    	return WorkformAttrBean.qryMaxAttrByAttrCode(ibsysid,nodeId,recordNum,attrCode);
    }
	
	public static IData qryAuditInfoByIbsysid(IData param) throws Exception
    {
        param.put("IBSYSID", param.getString("IBSYSID",""));
        param.put("NODE_ID", param.getString("NODE_ID",""));
        IData result = new DataMap();
        result = WorkformAttrBean.qryAuditInfoByIbsysid(param);
        if(IDataUtil.isNotEmpty(result)) {
            return result;
        }
        return null;
    }
	
	public IDataset insertAttrAudtiInfo(IData param) throws Exception
    {
        String subIbsysId = SeqMgr.getSubIbsysId();
        param.put("SUB_IBSYSID", subIbsysId);
        param.put("SEQ", 0);
        param.put("GROUP_SEQ", 0);
        param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        param.put("UPDATE_TIME", SysDateMgr.getSysTime());
        param.put("RECORD_NUM", param.getString("RECORD_NUM","0"));
        IDataset params = new DatasetList(param);
        WorkformAttrBean.insertWorkformAttr(params);
        IDataset paramBusis =  new DatasetList();
        return paramBusis;
    }
    public static IData insertAttrToList(IData param) throws Exception
    {
    	IDataset attrList=param.getDataset("attrList");
		for (int i = 0; i < attrList.size(); i++) {
            IData workformAttr = attrList.getData(i);
            workformAttr.put("SEQ", SeqMgr.getAttrSeq());
		}
        WorkformAttrBean.insertWorkformAttr(attrList);
        return null;

    }
}
