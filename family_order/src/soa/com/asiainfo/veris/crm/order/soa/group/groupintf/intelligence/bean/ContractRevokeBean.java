package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement.AgreementInfoBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContractInfoQry;

public class ContractRevokeBean extends CSBizBean {
    protected static final Logger logger = Logger.getLogger(ContractRevokeBean.class);

    public static final String RES_SUCCESS = "0000";//返回成功
    public static final String RES_FAILED = "0001";//返回失败
	public IData contractRevoke( IData input ) throws Exception{
		String contractNumber = IDataUtil.chkParam(input, "contractNumber");
		IData data = new DataMap();
		IData returnDatas = new DataMap();
        data.put("AGREEMENT_ID", contractNumber );
        data.put("ARCHIVES_STATE","4"); //直接 设置失效状态
        IDataset eleSet = AgreementInfoBean.queryElectronicAgreement( data ); //新增电子协议之类的

        if( CollectionUtils.isNotEmpty( eleSet ) ) {
            CSAppCall.call( "SS.AgreementInfoSVC.updateElectronicInfo", data);
        }else {
            IDataset conSet = CustContractInfoQry.qryContractInfoByContractId(contractNumber);//登记在客管那边的信息
        	//TODO 需要在查询 将 TF_F_CUST_CONTRACT_PRODUCT 0 --未开通
        	//                 TF_F_CUST_CONTRACT 4 注销 0 -无效
            if(CollectionUtils.isEmpty(conSet)){
                returnDatas.put("bizCode", RES_FAILED);
                returnDatas.put("bizDesc", "根据合同编码"+contractNumber+"未找到合同信息！");
                return returnDatas;
            }else{
                SQLParser parsers = new SQLParser( data );
                parsers.addSQL(" UPDATE TF_F_CUST_CONTRACT t set t.CONTRACT_STATE_CODE = :ARCHIVES_STATE, ");
                parsers.addSQL(" t.UPDATE_TIME = sysdate , t.CONTRACT_END_DATE = sysdate , t.CONTRACT_FLAG = '0'");
                parsers.addSQL(" WHERE t.CONTRACT_ID = :AGREEMENT_ID ");
                Dao.executeUpdate( parsers , Route.CONN_CRM_CG);
            }
        }
        returnDatas.put("bizCode", RES_SUCCESS );
		return returnDatas;
	}
}
