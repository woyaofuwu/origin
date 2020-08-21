package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class WorkformAttrBean {
    /**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformAttr(IDataset param) throws Exception {
        return Dao.insert("TF_B_EOP_ATTR", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryAttrByIbsysid(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_ATTR", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static void delAttrByIbsysid(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        Dao.executeUpdateByCodeCode("TF_B_EOP_ATTR", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryAttrBySubIbsysidAndGroupseq(String subIbsysid, String groupSeq) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("GROUP_SEQ", groupSeq);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_SUBIBSYSID_GROUPSEQ", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryAttrBySubIbsysidAndRecordNum(String subIbsysid, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_ATTR", "SEL_BY_SUBIBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryAttrBySubIbsysidAndRecordNumGroupSeq(String subIbsysid, String groupSeq, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("GROUP_SEQ", groupSeq);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_ATTR", "SEL_BY_SUBIBSYSID_RECORDNUM_GROUPSEQ", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryAttrBySubIbsysidAndAttrCode(String subIbsysid, String attrCode) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("ATTR_CODE", attrCode);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_SUBIBSYSID_ATTRCODE", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IData qryAttrBySubIbsysidRecordCode(String subIbsysid, String attrCode, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("ATTR_CODE", attrCode);
        param.put("RECORD_NUM", recordNum);
        IDataset attrInfos = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_SUBIBSYSID_RECORDNUM_ATTRCODE", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(attrInfos)) {
            return new DataMap();
        } else {
            return attrInfos.first();
        }
    }

    public static IDataset qryAttrRecodeNumBySubIbsysid(String subIbsysid) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 查询节点recordnum,不包括0的公共数据。
     * 
     * @param subIbsysid
     * @return
     * @throws Exception
     */
    public static IDataset qryRecordNumBySubIbsysid(String subIbsysid) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_RECORDNUM_BY_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IData qryAttrByIbsysidRecordCode(String ibsysid, String attrCode, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("ATTR_CODE", attrCode);
        param.put("RECORD_NUM", recordNum);
        IDataset attrInfos = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_ATTRCODE", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isEmpty(attrInfos)) {
            return new DataMap();
        } else {
            return attrInfos.first();
        }
    }

    //根据groupSeq排序
    public static IDataset qryEopAttrBySubIbsysid(String subIbsysid) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static void delEopAttrBySubibsysidGroupseq(String subIbsysid, String groupSeq, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("GROUP_SEQ", groupSeq);
        param.put("RECORD_NUM", recordNum);
        Dao.executeUpdateByCodeCode("TF_B_EOP_ATTR", "DEL_BY_SUBIBSYSID_GROUPSEQ", param, Route.getJourDb(BizRoute.getRouteId()));

    }

    public static IDataset qryEopAttrFromMaxSubIbsysidByIbsysid(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);

        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT * FROM TF_B_EOP_ATTR T ");
        strSql.append(" WHERE T.IBSYSID =:IBSYSID ");
        strSql.append(" and t.sub_ibsysid =( ");
        strSql.append(" SELECT max(a.sub_ibsysid) ");
        strSql.append(" FROM TF_B_EOP_ATTR a ");
        strSql.append(" WHERE a.IBSYSID =T.IBSYSID) ");

        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IData qryByIbsysidProductNo(String ibsysid, String productNo) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("PRODUCTNO", productNo);

        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT A.* FROM TF_B_EOP_ATTR A ");
        strSql.append(" WHERE A.IBSYSID=:IBSYSID AND  A.RECORD_NUM IN ( ");
        strSql.append(" SELECT T.RECORD_NUM FROM TF_B_EOP_ATTR T ");
        strSql.append(" WHERE T.IBSYSID=:IBSYSID AND T.ATTR_CODE='PRODUCTNO' ");
        strSql.append(" AND T.ATTR_VALUE=:PRODUCTNO) ");
        IDataset attrInfos = Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
        IData result = new DataMap();
        if(attrInfos != null && attrInfos.size() > 0) {
            for (int i = 0; i < attrInfos.size(); i++) {
                IData data = attrInfos.getData(i);
                result.put(data.getString("ATTR_CODE"), data.getString("ATTR_VALUE"));
            }
        }
        return result;
    }

    /**
     * 根据ibsysId,nodeId以及SUBIBSYSID查询attr
     * 
     * @param subIbsysid
     * @return
     * @throws Exception
     */
    public static IDataset qryInfoBySubIbsysid(IData param) throws Exception {
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_INFO_BY_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryHisInfoByProductNo(IData param) throws Exception {
        String productNo = param.getString("PRODUCT_NO");
        param.put("PRODUCT_NO", productNo);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT A.* FROM TF_BH_EOP_ATTR A ");
        strSql.append(" WHERE A.ATTR_CODE='PRODUCTNO'");
        strSql.append(" AND A.ATTR_VALUE=:PRODUCT_NO");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryHisInfoByIbsysidAndRecordnum(IData param) throws Exception {
        String ibsysId = param.getString("IBSYSID");
        String recordNum = param.getString("RECORD_NUM");
        param.put("IBSYSID", ibsysId);
        param.put("RECORD_NUM", recordNum);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT A.* FROM TF_BH_EOP_ATTR A ");
        strSql.append(" WHERE A.IBSYSID=:IBSYSID");
        strSql.append(" AND A.RECORD_NUM=:RECORD_NUM");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IData qryByIbsysidProductNoNodeId(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT A.* FROM TF_B_EOP_ATTR A ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND A.IBSYSID = :IBSYSID ");
        sql.addSQL(" AND A.NODE_ID = :NODE_ID ");
        sql.addSQL(" AND A.ATTR_CODE = 'PRODUCTNO' ");
        sql.addSQL(" AND A.ATTR_VALUE = :ATTR_VALUE ");
        sql.addSQL(" ORDER BY A.UPDATE_TIME DESC ");

        IDataset attrInfos = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        IData result = new DataMap();
        if(IDataUtil.isEmpty(attrInfos)) {
            return result;
        }
        String recordNum = attrInfos.first().getString("RECORD_NUM");
        param.put("RECORD_NUM", recordNum);
        IDataset attrRNInfos = getNewInfoByIbsysidAndNodeId(param);
        if(IDataUtil.isNotEmpty(attrRNInfos)) {
            for (int i = 0; i < attrRNInfos.size(); i++) {
                result.put(attrRNInfos.getData(i).getString("ATTR_CODE"), attrRNInfos.getData(i).getString("ATTR_VALUE"));
            }
        }
        return result;

    }
    
    public static IDataset getInfoByIbsysidRecordNum(IData param)throws Exception {
    	String ibsysId = param.getString("IBSYSID");
        String recordNum = param.getString("RECORD_NUM");
        param.put("IBSYSID", ibsysId);
        param.put("RECORD_NUM", recordNum);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT A.* FROM TF_B_EOP_ATTR A ");
        strSql.append(" WHERE A.IBSYSID=:IBSYSID");
        strSql.append(" AND A.RECORD_NUM=:RECORD_NUM");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
    
    public static IDataset qryAttrByIbsysidRecordNumNodeId(IData param) throws Exception {
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_NODEID_ATTRCODE", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset getInfoByIbsysidAttrtype(IData param)throws Exception {
    	String ibsysId = param.getString("IBSYSID");
        param.put("IBSYSID", ibsysId);
        param.put("ATTR_TYPE", "1");
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT A.* FROM TF_B_EOP_ATTR A ");
        strSql.append(" WHERE A.IBSYSID=:IBSYSID");
        strSql.append(" AND A.ATTR_TYPE=:ATTR_TYPE");
        strSql.append(" order by A.SUB_IBSYSID,A.SEQ");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
    
    public static IDataset getEopAttrToList(IData param)throws Exception {
    	String ibsysId = param.getString("IBSYSID");
        param.put("IBSYSID", ibsysId);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT A.* FROM TF_B_EOP_ATTR A ");
        strSql.append(" WHERE A.IBSYSID=:IBSYSID");
        strSql.append(" order by A.SUB_IBSYSID,A.SEQ");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
    
	public static IDataset getEopAttrToListForHis(IData param)throws Exception {
    	String ibsysId = param.getString("IBSYSID");
        param.put("IBSYSID", ibsysId);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT A.* FROM TF_BH_EOP_ATTR A ");
        strSql.append(" WHERE A.IBSYSID=:IBSYSID");
        strSql.append(" order by A.SUB_IBSYSID,A.SEQ");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
    public static int getSeq(String ibsysId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysId);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT MAX(SEQ) FROM TF_B_EOP_ATTR A ");
        strSql.append(" WHERE A.IBSYSID=:IBSYSID ");
        IDataset infos = Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
        if (DataUtils.isNotEmpty(infos)) {
			return infos.first().getInt("MAX(SEQ)");
		}else {
			return 0;
		}
    }
    
    public static IDataset getIbsysidByAttrcodeAndAttrvalue(IData param) throws Exception{
    	return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_IBSYSID_BY_ATTRCODE_ATTRVALUE", param, Route.getJourDb(BizRoute.getRouteId()));
	}

    public static IDataset getNewInfoByIbsysidAndNodeId(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT A.* FROM TF_B_EOP_ATTR A ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND A.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND A.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND A.RECORD_NUM=:RECORD_NUM ");
        sql.addSQL(" AND A.SUB_IBSYSID =  ( ");
        sql.addSQL(" SELECT MAX(B.SUB_IBSYSID) FROM TF_B_EOP_ATTR B ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND B.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND B.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND B.RECORD_NUM=:RECORD_NUM ");
        sql.addSQL(" ) ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset getInfoByIbsysidAndNodeId(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT A.ATTR_CODE,A.ATTR_VALUE FROM TF_B_EOP_ATTR A ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND A.GROUP_SEQ = (SELECT max(T.GROUP_SEQ) FROM TF_B_EOP_ATTR T where T.IBSYSID =:IBSYSID AND T.NODE_ID=:NODE_ID) ");
        sql.addSQL(" AND A.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND A.RECORD_NUM = :RECORD_NUM ");
        sql.addSQL(" AND A.IBSYSID = :IBSYSID ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updateByIbsysid(IData param) throws Exception {
    	
    	Dao.executeUpdateByCodeCode("TF_B_EOP_ATTR", "UPD_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset getNewLineInfoList(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT DISTINCT A.RECORD_NUM FROM TF_B_EOP_ATTR A ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND A.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND A.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND A.SUB_IBSYSID =  ( ");
        sql.addSQL(" SELECT MAX(B.SUB_IBSYSID) FROM TF_B_EOP_ATTR B ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND B.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND B.NODE_ID=:NODE_ID ");
        sql.addSQL(" ) ");
        sql.addSQL(" GROUP BY A.RECORD_NUM ");
        IDataset recodeNumList = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        IDataset lineInfoList = new DatasetList();
        if(IDataUtil.isNotEmpty(recodeNumList)) {
            for (int i = 0; i < recodeNumList.size(); i++) {
                IData recodeNumData = recodeNumList.getData(i);
                String recodeNum = recodeNumData.getString("RECORD_NUM");
                if(!"0".equals(recodeNum)) {
                    param.put("RECORD_NUM", recodeNum);
                    IDataset attrDatas = getNewInfoByIbsysidAndNodeId(param);
                    IData lineInfo = new DataMap();
                    if(IDataUtil.isNotEmpty(attrDatas)) {
                        for (int j = 0; j < attrDatas.size(); j++) {
                            IData attrData = attrDatas.getData(j);
                            String attrCode = attrData.getString("ATTR_CODE");
                            String attrValue = attrData.getString("ATTR_VALUE");
                            /*if(attrCode != null && !attrCode.startsWith("NOTIN_")) {
                                attrCode = "pattr_" + attrCode;
                            }*/
                            //加载页面数据时，去掉百分号
                            if("NOTIN_RSRV_STR6".equals(attrCode) || "NOTIN_RSRV_STR7".equals(attrCode) || "NOTIN_RSRV_STR8".equals(attrCode)) {
                                if(attrValue != null && attrValue.endsWith("%")) {
                                    attrValue = attrValue.substring(0, attrValue.length() - 1);
                                }
                            }
                            lineInfo.put(attrCode, attrValue);
                            lineInfo.put("IBSYSID", attrData.getString("IBSYSID"));
                        }
                    }
                    lineInfoList.add(lineInfo);
                }
            }
        }
        return lineInfoList;
    }
    
    public static IDataset getRecordNumByIbsysid(IData param) throws Exception {
		StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT distinct t.RECORD_NUM ");
        sql.append(" FROM TF_B_EOP_ATTR t ");
        sql.append(" WHERE t.IBSYSID=:IBSYSID ");
        sql.append(" AND t.RECORD_NUM > 0 ");
        IDataset dataset = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        return dataset;
	}
    
    public static IDataset selProductNoByIbsysid(IData param) throws Exception {
    	return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_PRODUCTNO_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
    
    public static IDataset qryEopAttrByIbsysidNodeid(String ibsysid, String nodeID, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", nodeID);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSIDNODEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryMaxEopAttrByIbsysidNodeid(String ibsysid, String nodeID) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", nodeID);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_MAXIBSYSIDNODEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryAttrByOperTypeEoms(String subIbsysid, String ibsysid, String groupSeq, String recordNum, String serialNo) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        param.put("IBSYSID", ibsysid);
        param.put("GROUP_SEQ", groupSeq);
        param.put("SERIALNO", serialNo);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_EOMS_OPERTYPE", param, Route.getJourDb(BizRoute.getRouteId()));
    }

	public static IDataset qryEopAttrBySubIbsysidProductNo(String subIbsysid,String productNO) throws Exception{
		IData param = new DataMap();
        param.put("PRODUCT_NO", productNO);
        param.put("SUB_IBSYSID", subIbsysid);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT T.SUB_IBSYSID,T.IBSYSID,T.ACCEPT_MONTH,T.SEQ,T.GROUP_SEQ,T.NODE_ID,T.ATTR_CODE,T.ATTR_NAME,T.ATTR_VALUE, ");
        strSql.append(" T.PARENT_ATTR_CODE,T.RECORD_NUM,TO_CHAR(T.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,T.RSRV_STR1,T.RSRV_STR2,T.ATTR_TYPE");
        strSql.append(" FROM TF_B_EOP_ATTR T");
        strSql.append(" WHERE T.ATTR_CODE='PRODUCTNO'");
        strSql.append(" AND T.ATTR_VALUE=:PRODUCT_NO");
        strSql.append(" AND T.SUB_IBSYSID=:SUB_IBSYSID");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
    public static IDataset qryMaxNewAttrByAttrCode(IData param) throws Exception {
          SQLParser sql = new SQLParser(param);
          sql.addSQL(" SELECT A.* FROM TF_B_EOP_ATTR A ");
          sql.addSQL(" WHERE 1=1 ");
          sql.addSQL(" AND A.IBSYSID=:IBSYSID ");
          sql.addSQL(" AND A.NODE_ID=:NODE_ID ");
          sql.addSQL(" AND A.ATTR_CODE=:ATTR_CODE ");
          sql.addSQL(" AND A.SUB_IBSYSID =  ( ");
          sql.addSQL(" SELECT MAX(B.SUB_IBSYSID) FROM TF_B_EOP_ATTR B ");
          sql.addSQL(" WHERE 1=1 ");
          sql.addSQL(" AND B.IBSYSID=:IBSYSID ");
          sql.addSQL(" AND B.NODE_ID=:NODE_ID ");
          sql.addSQL(" AND B.ATTR_CODE=:ATTR_CODE ");
          sql.addSQL(" ) ");
          return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryMaxAttrByAttrCode(String ibsysid, String nodeId, String recordNum, String attrCode) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", nodeId);
        param.put("RECORD_NUM", recordNum);
        param.put("ATTR_CODE", attrCode);
        return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_ATTRCODE_MAXSUB", param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
	public static IData qryAuditInfoByIbsysid(IData param) throws Exception
    {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT T.ATTR_CODE,T.ATTR_VALUE FROM TF_B_EOP_ATTR T ");
        sql.addSQL(" WHERE T.IBSYSID=:IBSYSID AND T.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND T.ATTR_CODE IN ('GROUPNME','LINE_TYPE','WIDTH','DISCNT') ");
        IDataset results = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(results))
        {
            for (Object obj : results)
            {
                IData data = (IData) obj;
                if ("1".equals(data.getString("ATTR_VALUE")))
                {
                    result.put(data.getString("ATTR_CODE"), "是");
                }
                else
                {
                    result.put(data.getString("ATTR_CODE"), "否");
                }
            }
        }
        return result;
    }
}
