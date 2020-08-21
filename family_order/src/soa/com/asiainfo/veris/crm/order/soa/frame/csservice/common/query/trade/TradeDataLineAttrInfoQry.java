package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class TradeDataLineAttrInfoQry extends CSBizBean {

    /**
     * 
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeDataLineInfoByAttrValue(String attrValue) throws Exception {
        IData param = new DataMap();
        param.put("ATTR_VALUE", attrValue);
        IDataset infos = Dao.qryByCode("TF_B_TRADE_DATALINE_ATTR", "SEL_BY_ATTR_CODE", param, Route.getJourDb(Route.CONN_CRM_CG));
        IDataset results = new DatasetList();
        if (IDataUtil.isNotEmpty(infos)) {
            for (int i = 0; i < infos.size(); i++) {
                IData data = infos.getData(i);
                IDataset userPros = UserProductInfoQry.getUserAllProducts(data.getString("USER_ID"));
                if (IDataUtil.isEmpty(userPros)) {
                    continue;
                }
                for (int j = 0; j < userPros.size(); j++) {
                    data.put("PRODUCT_ID", userPros.getData(j).getString("PRODUCT_ID"));
                    results.add(data);
                }

            }
        }
        return results;
    }

    /**
     * 
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeAllDataLineInfoByAttrValue(String attrValue) throws Exception {
        IData param = new DataMap();
        param.put("ATTR_VALUE", attrValue);
        return Dao.qryByCode("TF_B_TRADE_DATALINE_ATTR", "SEL_ALL_BY_ATTR_CODE", param);
    }

    /**
     * 
     * @param userName
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpUserDatalineByName(String userName) throws Exception {
        IData param = new DataMap();
        param.put("RSRV_STR5", userName);
        IDataset dataNameInfos = new DatasetList();

        dataNameInfos = Dao.qryByCode("TF_F_USER_DATALINE", "SEL_GRP_DATALINE_BY_USERNAME", param);

        if (IDataUtil.isEmpty(dataNameInfos)) {
            IData params = new DataMap();
            params.put("ATTR_VALUE", userName);

            SQLParser parser = new SQLParser(params);
            parser.addSQL(" SELECT * FROM (SELECT T.IBSYSID, ");
            parser.addSQL(" T.ATTR_VALUE,T.RECORD_NUM, ");
            parser.addSQL(" T.SUB_IBSYSID,T.GROUP_SEQ,T.UPDATE_TIME, ");
            parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY T.RECORD_NUM ORDER BY T.UPDATE_TIME DESC) G  ");
            parser.addSQL(" FROM TF_B_EOP_ATTR T WHERE T.ATTR_VALUE=:ATTR_VALUE AND T.ATTR_CODE='TRADENAME') R ");
            parser.addSQL(" WHERE R.G<=1 ");

            IDataset dataAttrInfos = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

            if (IDataUtil.isNotEmpty(dataAttrInfos)) {
                for (Object object : dataAttrInfos) {
                    IData attrInfo = (IData) object;
                    String ibsysId = attrInfo.getString("IBSYSID");
                    String recordNum = attrInfo.getString("RECORD_NUM");
                    IData params1 = new DataMap();
                    params1.put("IBSYSID", ibsysId);

                    SQLParser parser1 = new SQLParser(params1);
                    parser1.addSQL(" SELECT T.BPM_TEMPLET_ID ");
                    parser1.addSQL(" FROM TF_B_EWE T ");
                    parser1.addSQL(" WHERE T.TEMPLET_TYPE ='0' ");
                    parser1.addSQL(" AND T.BI_SN =:IBSYSID ");

                    IDataset eweInfos = Dao.qryByParse(parser1, Route.getJourDb(Route.CONN_CRM_CG));

                    if (IDataUtil.isNotEmpty(eweInfos)) {
                        String bpmtempletId = eweInfos.first().getString("BPM_TEMPLET_ID");
                        if ("ERESOURCECONFIRMZHZG".equals(bpmtempletId) || "ECHANGERESOURCECONFIRM".equals(bpmtempletId)) {
                            continue;
                        } else {
                            IData params2 = new DataMap();
                            params2.put("RECORD_NUM", recordNum);
                            params2.put("IBSYSID", ibsysId);

                            SQLParser parser2 = new SQLParser(params2);
                            parser2.addSQL(" SELECT T.ATTR_VALUE ");
                            parser2.addSQL(" FROM TF_B_EOP_ATTR T ");
                            parser2.addSQL(" WHERE T.RECORD_NUM = :RECORD_NUM ");
                            parser2.addSQL(" AND T.ATTR_CODE ='PRODUCTNO' ");
                            parser2.addSQL(" AND T.IBSYSID = :IBSYSID ");

                            IDataset productNoInfos = Dao.qryByParse(parser2, Route.getJourDb(Route.CONN_CRM_CG));

                            if (IDataUtil.isNotEmpty(productNoInfos)) {
                                attrInfo.put("PRODUCT_NO", productNoInfos.first().getString("ATTR_VALUE"));
                            }
                            dataNameInfos.add(attrInfo);
                            break;
                        }
                    }
                }
            }
        }

        return dataNameInfos;
    }

    /**
     * 获取TF_F_USER_DATALINE数据
     * 
     * @param userId
     * @param productNo
     * @return
     * @throws Exception
     */
    public static IDataset qryUserDatalineByProductNO(String productNo) throws Exception {
        IData data = new DataMap();
        data.put("PRODUCT_NO", productNo);
        return Dao.qryByCode("TF_F_USER_DATALINE", "SEL_DATALINE_BY_PRODUCTNO", data);
    }

    /**
     * 获取TF_F_USER_DATALINE数据
     * 
     * @param userId
     * @param productNo
     * @return
     * @throws Exception
     */
    public static IDataset qryUserDatalineByProductNO(String userId, String productNo) throws Exception {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PRODUCT_NO", productNo);
        return Dao.qryByCode("TF_F_USER_DATALINE", "SEL_USER_DATALINE_BY_PRODUCTNO", data);
    }

    /**
     * 根据专线实例查询专线台账
     * 
     * @param productNo
     * @return
     * @throws Exception
     */
    public static IDataset qryDatalineInstanceByProductNo(String productNo) throws Exception {
        IData param = new DataMap();
        param.put("PRODUCT_NO", productNo);
        return Dao.qryByCode("TF_B_TRADE_DATALINE_ATTR", "SEL_DATALINE_INSTANCE_BY_PRONO", param, Route.getJourDb());
    }

    /**
     * ESOP查询稽核员工
     * 
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static IDataset qryStaffinfoForESOPNEW(IData data) throws Exception {
        String flag = data.getString("FLAG");
        IDataset dataset = new DatasetList();
        if ("1".equals(flag)) {
            dataset = Dao.qryByCodeParser("TD_M_STAFF", "SEL_STAFFINFO_FOR_ESOPFIRST", data, Route.CONN_SYS);
        } else if ("2".equals(flag)) {
            dataset = Dao.qryByCodeParser("TD_M_STAFF", "SEL_STAFFINFO_FOR_ESOPSECOND", data, Route.CONN_CRM_CG);
        }
        return dataset;
    }

    /**
     * ESOP查询稽核信息
     * 
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static IDataset qryAuditinfoForESOP(IData data) throws Exception {
        IDataset dataset = Dao.qryByCodeParser("TF_B_TRADE", "QRY_AUDITINFO_FOR_ESOP", data, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * 获取TF_F_USER_DATALINE数据
     * 
     * @param userId
     * @param productNo
     * @return
     * @throws Exception
     */
    public static IDataset qryAllUserDatalineByProductNO(String productNo) throws Exception {
        IData data = new DataMap();
        data.put("PRODUCT_NO", productNo);
        return Dao.qryByCode("TF_F_USER_DATALINE", "SEL_ALL_USER_DATALINE_BY_PRODUCTNO", data);
    }

    /**
     * 获取TF_F_USER_DATALINE数据
     * 
     * @param userId
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset qryUserDatalineByProductNO(IData data) throws Exception {

        IData params = new DataMap();
        params.put("PRODUCT_NO", data.getString("PRODUCTNO", ""));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT t.RSRV_STR5  ");
        parser.addSQL(" FROM TF_F_USER_DATALINE T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.PRODUCT_NO=:PRODUCT_NO ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        IDataset attrNameInfos = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return attrNameInfos;
    }
}
