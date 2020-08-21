package com.asiainfo.veris.crm.order.soa.group.minorec.queryContract;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ContractOfferChaListBean {

    public static IDataset queryContractOfferChaList(IData param) throws Exception {

        IData params = new DataMap();

        params.put("AGREEMENT_ID", param.getString("CONTRACT_ID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT E.ATTR_CODE,  ");
        parser.addSQL(" E.ATTR_VALUE, ");
        parser.addSQL(" T.AGREEMENT_ID ");
        parser.addSQL(" FROM TF_F_ELECTRONIC_AGREEMENT T, TF_F_ELECTRONIC_ARCHIVES_ATTR E ");
        parser.addSQL(" WHERE T.ARCHIVES_ID = E.ARCHIVES_ID ");
        parser.addSQL(" AND T.AGREEMENT_ID = :AGREEMENT_ID ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);

    }

    public static IDataset queryContractOfferChaListVw(IData param) throws Exception {

        IDataset attrList = new DatasetList();
        IData params = new DataMap();

        params.put("AGREEMENT_ID", param.getString("CONTRACT_ID"));

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT E.ARCHIVES_ID,  ");
        parser.addSQL(" T.AGREEMENT_ID ");
        parser.addSQL(" FROM TF_F_ELECTRONIC_AGRE_ATTACH T,TF_F_ELECTRONIC_ARCHIVES E ");
        parser.addSQL(" WHERE T.AGREEMENT_ID = :AGREEMENT_ID ");
        parser.addSQL(" AND E.ARCHIVES_ID = T.ARCHIVES_ID ");
        parser.addSQL(" ORDER BY E.UPDATE_TIME DESC ");

        IDataset archivesList = Dao.qryByParse(parser, Route.CONN_CRM_CG);

        if (IDataUtil.isNotEmpty(archivesList)) {
            String archivesId = archivesList.first().getString("ARCHIVES_ID");

            IData params1 = new DataMap();
            params1.put("ARCHIVES_ID", archivesId);

            SQLParser parser1 = new SQLParser(params1);
            parser1.addSQL(" SELECT E.ATTR_CODE,  ");
            parser1.addSQL(" E.ATTR_VALUE ");
            parser1.addSQL(" FROM TF_F_ELECTRONIC_ARCHIVES_ATTR E ");
            parser1.addSQL(" WHERE E.ARCHIVES_ID = :ARCHIVES_ID ");
            attrList = Dao.qryByParse(parser1, Route.CONN_CRM_CG);

        }

        return attrList;

    }

}
