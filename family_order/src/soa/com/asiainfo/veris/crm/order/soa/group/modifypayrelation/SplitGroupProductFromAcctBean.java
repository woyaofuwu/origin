package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.PayRelationTradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class SplitGroupProductFromAcctBean extends MemberBean
{
    private final IData baseCommInfo = new DataMap();
    
    protected MemberReqData reqData = null;
    
    protected String setTradeTypeCode() throws Exception
    {
        return "3605";
    }
    
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (MemberReqData) getBaseReqData();
    }
    
    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map);
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        IData mebUserAcctDay = DiversifyAcctUtil.getUserAcctDay(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(mebUserAcctDay))
        {
            CSAppException.apperr(GrpException.CRM_GRP_657, reqData.getUca().getSerialNumber());
        }

        String userId = reqData.getUca().getUserId();

        String nextStartDate = DiversifyAcctUtil.getFirstDayNextAcct(userId);
        String endThisCycleId = DiversifyAcctUtil.getLastDayThisAcct(userId);

        if(!"".equals(endThisCycleId))
        {
            endThisCycleId = endThisCycleId.split(" ")[0];
            endThisCycleId = endThisCycleId.replaceAll("-", "");
        }
        
        if(!"".equals(nextStartDate))
        {   
            nextStartDate = nextStartDate.split(" ")[0];
            nextStartDate = nextStartDate.replaceAll("-", "");
        }
 
        baseCommInfo.put("NEW_ACCT_ID", map.getString("NEW_ACCT_ID"));
        baseCommInfo.put("NEXT_START_DAY", nextStartDate);
        baseCommInfo.put("END_CYCLE_ID", endThisCycleId);
        baseCommInfo.put("GROUP_ID", map.getString("GROUP_ID"));
    }
    
    /**
     * 生成付费关系子台帐
     * 删除原来的付费关系，生成新的付费关系
     */
    private void geneTradePayrelation() throws Exception
    {
        String acctId = reqData.getGrpUca().getAcctId();// 集团账户id
        String userId = reqData.getUca().getUserId();// 成员用户标识
        String custId = reqData.getUca().getCustId();
        String brandCode= reqData.getGrpUca().getBrandCode();
        
        //判断是否存在未生效的集团产品账户拆分
        IDataset defAccts = queryDefPayRealByUserId(userId);
        if(defAccts != null && defAccts.size() > 1)
        {
            CSAppException.apperr(PayRelationTradeException.CRM_PAYRELATION_24, userId);
        }
        
        //
        IDataset acctInfos = queryPayRelaByUserAndAcctId(userId,acctId);
        if (IDataUtil.isEmpty(acctInfos))
        {
            CSAppException.apperr(PayRelationTradeException.CRM_PAYRELATION_23, acctId);
        }
       
        //判断集团产品对应的账户是否只含一个集团产品
        
        IDataset acctsForCust =  queryDefPayRelationByCustIdAndAcctId(acctId,custId);
        if(acctsForCust != null && acctsForCust.size() == 1)
        {
            CSAppException.apperr(PayRelationTradeException.CRM_PAYRELATION_25, acctId);
        }
        
        IData acctInfo = acctInfos.getData(0);
        IDataset dataset = new DatasetList();
        
        //终止原付费关系
        IData inParams = new DataMap();
        inParams.put("USER_ID", userId);// 成员user_id
        inParams.put("ACCT_ID", acctId);// 集团acct_id
        inParams.put("PAYITEM_CODE",acctInfo.getString("PAYITEM_CODE"));
        inParams.put("ACCT_PRIORITY", acctInfo.getString("ACCT_PRIORITY"));
        inParams.put("USER_PRIORITY", acctInfo.getString("USER_PRIORITY"));
        inParams.put("BIND_TYPE", acctInfo.getString("BIND_TYPE"));
        inParams.put("START_CYCLE_ID", acctInfo.getString("START_CYCLE_ID"));
        inParams.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID"));
        inParams.put("ACT_TAG", acctInfo.getString("ACT_TAG"));
        inParams.put("DEFAULT_TAG", acctInfo.getString("DEFAULT_TAG"));

        inParams.put("STATE", "DEL");
        inParams.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        inParams.put("LIMIT_TYPE", acctInfo.getString("LIMIT_TYPE"));
        inParams.put("LIMIT", acctInfo.getString("LIMIT"));
        inParams.put("COMPLEMENT_TAG", acctInfo.getString("COMPLEMENT_TAG"));
        inParams.put("INST_ID", acctInfo.getString("INST_ID"));

        dataset.add(inParams);
        
        //生成新的付费关系
        IData inParamsNew = new DataMap();
        inParamsNew.put("USER_ID", userId);// 成员user_id
        inParamsNew.put("ACCT_ID", baseCommInfo.getString("NEW_ACCT_ID"));// 新账户ID
        inParamsNew.put("PAYITEM_CODE",acctInfo.getString("PAYITEM_CODE"));
        inParamsNew.put("ACCT_PRIORITY", acctInfo.getString("ACCT_PRIORITY"));
        inParamsNew.put("USER_PRIORITY", acctInfo.getString("USER_PRIORITY"));
        inParamsNew.put("BIND_TYPE", acctInfo.getString("BIND_TYPE"));
        inParamsNew.put("START_CYCLE_ID", baseCommInfo.getString("NEXT_START_DAY"));
        inParamsNew.put("END_CYCLE_ID", acctInfo.getString("END_CYCLE_ID"));
        inParamsNew.put("ACT_TAG", acctInfo.getString("ACT_TAG"));
        inParamsNew.put("DEFAULT_TAG", acctInfo.getString("DEFAULT_TAG"));

        inParamsNew.put("STATE", "ADD");
        inParamsNew.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        inParamsNew.put("LIMIT_TYPE", acctInfo.getString("LIMIT_TYPE"));
        inParamsNew.put("LIMIT", acctInfo.getString("LIMIT"));
        inParamsNew.put("COMPLEMENT_TAG", acctInfo.getString("COMPLEMENT_TAG"));
        inParamsNew.put("INST_ID", SeqMgr.getInstId());
        dataset.add(inParamsNew);
        
        if("BOSG".equals(brandCode))
        {
            IDataset relaBBInfo = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userId, "90", "0");
            
            for (int i = 0; i < relaBBInfo.size(); i++)
            {
                String userIdb =relaBBInfo.getData(i).getString("USER_ID_B");
                IDataset acctuseridaInfos = queryPayRelaByUserAndAcctId(userIdb,acctId);
                if (IDataUtil.isEmpty(acctInfos))
                {
                    CSAppException.apperr(PayRelationTradeException.CRM_PAYRELATION_23, acctId);
                }
                IData inParamsBbss = (IData) Clone.deepClone(inParams);
                inParamsBbss.put("USER_ID", userIdb);
                inParamsBbss.put("INST_ID",acctuseridaInfos.getData(0).getString("INST_ID"));
                dataset.add(inParamsBbss);
                
                IData inParamsNewBbss = (IData) Clone.deepClone(inParamsNew);
                inParamsNewBbss.put("USER_ID", userIdb);
                inParamsNewBbss.put("INST_ID",SeqMgr.getInstId());
                dataset.add(inParamsNewBbss);
                
            }
            
        }
        
        this.addTradePayrelation(dataset);
    }
    
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        geneTradePayrelation();
    }
    
    public IDataset queryPayRelaByUserAndAcctId(String userId ,String acctId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("ACCT_ID", acctId);

        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT * FROM TF_A_PAYRELATION WHERE USER_ID = TO_NUMBER(:USER_ID) "); 
        parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND DEFAULT_TAG = '1' " );
        parser.addSQL(" AND ACCT_ID = :ACCT_ID " );
        parser.addSQL(" AND ACT_TAG = '1' " );
        parser.addSQL(" AND END_CYCLE_ID > TO_CHAR(SYSDATE, 'YYYYMMDD') " );
        parser.addSQL(" AND END_CYCLE_ID > START_CYCLE_ID ") ;
       return Dao.qryByParse(parser);
    }
    
    /**
     * 查询默认付费关系
     * @param userId
     * @return
     * @throws Exception
     */
    public IDataset queryDefPayRealByUserId(String userId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);

        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT * FROM TF_A_PAYRELATION WHERE USER_ID = TO_NUMBER(:USER_ID) "); 
        parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND DEFAULT_TAG = '1' " );
        parser.addSQL(" AND ACT_TAG = '1' " );
        parser.addSQL(" AND END_CYCLE_ID >= TO_CHAR(SYSDATE, 'YYYYMMDD') " );
        parser.addSQL(" AND END_CYCLE_ID > START_CYCLE_ID ") ;
       return Dao.qryByParse(parser);
    }
    
    /**
     * 
     * @param acctId
     * @param custId
     * @return
     * @throws Exception
     */
    public IDataset queryDefPayRelationByCustIdAndAcctId(String acctId,String custId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);
        inparams.put("ACCT_ID", acctId);
        
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT * FROM TF_A_PAYRELATION A WHERE A.ACCT_ID = :ACCT_ID AND A.DEFAULT_TAG = '1' "); 
        parser.addSQL(" AND A.ACT_TAG = '1' AND A.END_CYCLE_ID >= TO_CHAR((LAST_DAY(SYSDATE) + 1),'YYYYMMDD') AND A.END_CYCLE_ID > A.START_CYCLE_ID ");
        parser.addSQL(" AND EXISTS (SELECT 1 FROM TF_F_USER B WHERE A.USER_ID = B.USER_ID AND B.REMOVE_TAG = '0' AND B.CUST_ID = :CUST_ID)" );
    
        return Dao.qryByParse(parser);
    }
}
