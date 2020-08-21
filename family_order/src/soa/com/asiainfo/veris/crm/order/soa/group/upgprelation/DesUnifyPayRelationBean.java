
package com.asiainfo.veris.crm.order.soa.group.upgprelation;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MFileInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class DesUnifyPayRelationBean extends MemberBean
{
    private String actionFlag = null;

    private String acctId = null;

    private String payItemCode = null;

    private String mebFileShow = null;
    
    private String mebFileList = null;
    
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        actTradePayRela();
        
        insertMebUploadFiles();
    }
    
    /**
     * 处理付费关系
     * 
     * @throws Exception
     */
    public void actTradePayRela() throws Exception
    {
        // 查询成员非默认付费信息
        IDataset userPayList = PayRelaInfoQry.qryPayRelaByUserAcctIdDefTag(reqData.getUca().getUserId(),
                acctId, "0");
        
        // 查询成员特殊付费信息
        IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(reqData.getUca().getUserId(), 
                reqData.getGrpUca().getUserId(), 
                acctId, payItemCode);
        
        if (IDataUtil.isEmpty(userPayList))
        {
            return;
        }

        IDataset payRelaSet = new DatasetList();
        IDataset specPayRelaSet = new DatasetList();
        for (int i = 0, iRow = userPayList.size(); i < iRow; i++)
        {
            IData userPayData = userPayList.getData(i);
            String actTag = userPayData.getString("ACT_TAG");
            
            if (!payItemCode.equals(userPayData.getString("PAYITEM_CODE", "")) 
                    || !"1".equals(actTag))
            {
                continue;
            }
            
            for (int j = 0, jRow = userSpecialPayList.size(); j < jRow; j++)
            {
                IData userSpecialPyaData = userSpecialPayList.getData(j);

                if (!userPayData.getString("LIMIT", "").equals(userSpecialPyaData.getString("LIMIT")) || 
                        !userPayData.getString("LIMIT_TYPE", "").equals(userSpecialPyaData.getString("LIMIT_TYPE")))
                {
                    continue;
                }

                // 状态属性：0-增加，1-删除，2-变更
                userPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                userSpecialPyaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                if (reqData.isNeedSms())
                {
                    userPayData.put("RSRV_STR4", "2");
                    userPayData.put("RSRV_STR5", reqData.getUca().getSerialNumber());
                }
                // 注销(上月底)
                if ("1".equals(actionFlag))
                {

                    String lastCycleLastAcct = SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getAddMonthsLastDay(-1));
                    userPayData.put("END_CYCLE_ID", lastCycleLastAcct);
                    userPayData.put("ACT_TAG", "0");// 设为无效(0)
                    userPayData.put("REMARK", "注销(上月底),集团统一付费产品"); // 注销(上月底)

                    userSpecialPyaData.put("END_CYCLE_ID", lastCycleLastAcct);
                    userSpecialPyaData.put("REMARK", "注销(上月底),集团统一付费产品"); // 注销(上月底)
                }
                else if ("2".equals(actionFlag))
                {// 注销(本月底)
                    String lastCycleThisAcct = SysDateMgr.getLastCycleThisMonth();
                    userPayData.put("END_CYCLE_ID", lastCycleThisAcct);
                    // 状态属性：0-增加，1-删除，2-变更
                    userPayData.put("REMARK", "注销(本月底),集团统一付费产品"); // 注销(上月底)

                    userSpecialPyaData.put("END_CYCLE_ID", lastCycleThisAcct);
                    userSpecialPyaData.put("REMARK", "注销(本月底),集团统一付费产品"); // 注销(上月底)

                }
                payRelaSet.add(userPayData);
                specPayRelaSet.add(userSpecialPyaData);
            }

        }
        super.addTradePayrelation(payRelaSet);
        super.addTradeUserSpecialepay(specPayRelaSet);

    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        actionFlag = map.getString("ACTION_FLAG", "");
        acctId = map.getString("ACCT_ID", "");
        payItemCode = map.getString("PAY_ITEM_CODE", "");
        reqData.setNeedSms(false);
        
        mebFileShow = map.getString("MEB_FILE_SHOW");
        mebFileList = map.getString("MEB_FILE_LIST");
        
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
        
        super.makUcaForMebNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3639";
    }
    
    /**
     * 
     * @throws Exception
     */
    private void insertMebUploadFiles() throws Exception
    {
        
        if(StringUtils.isNotBlank(mebFileShow) 
                && StringUtils.equals("true", mebFileShow))
        {
            if(StringUtils.isNotEmpty(mebFileList))
            {
                
                String userIdb = reqData.getUca().getUserId();//成员user_id
                String serialNumberB = reqData.getUca().getSerialNumber();//成员号码
                String partitionId = StrUtil.getPartition4ById(userIdb);
                
                String serialNumberA = reqData.getGrpUca().getSerialNumber();//成员号码
                String groupId = reqData.getGrpUca().getCustGroup().getGroupId();
                String custName = reqData.getGrpUca().getCustGroup().getCustName();
                String staffId =  CSBizBean.getVisit().getStaffId();
                String createTime = SysDateMgr.getSysTime();
                String productId = reqData.getGrpProductId();
                String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
                String tradeTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), 
                        "TD_S_TRADETYPE", "TRADE_TYPE_CODE", 
                        "TRADE_TYPE", tradeTypeCode);
                
                String[] fileArray = mebFileList.split(",");
                for (int i = 0; i < fileArray.length; i++)
                {
                    IData fileData = new DataMap();
                    String fileId = fileArray[i];
                    
                    String fileName = "";
                    IDataset fileDatas = MFileInfoQry.qryFileInfoListByFileID(fileId);
                    if(IDataUtil.isNotEmpty(fileDatas))
                    {
                         fileName = fileDatas.getData(0).getString("FILE_NAME","");
                    }
                 
                    fileData.put("PARTITION_ID",  partitionId);
                    fileData.put("USER_ID",  userIdb);
                    fileData.put("FILE_ID",  fileId);
                    fileData.put("GROUP_ID",  groupId);
                    fileData.put("SERIAL_NUMBER_A",  serialNumberA);
                    fileData.put("GROUP_ID",  groupId);
                    fileData.put("CUST_NAME",  custName);
                    fileData.put("PRODUCT_ID",  productId);
                    fileData.put("CREATE_STAFF", staffId);
                    fileData.put("CREATE_TIME",  createTime);
                    fileData.put("TRADE_TYPE_CODE",  tradeTypeCode);
                    fileData.put("TRADE_TYPE",  tradeTypeName);
                    fileData.put("TRADE_TAG",  "3");
                    fileData.put("TRADE_ID",  getTradeId());
                    fileData.put("FILE_NAME", fileName);
                    fileData.put("SERIAL_NUMBER_B",  serialNumberB);
                    
                    boolean resultFlag = Dao.insert("TF_F_GROUP_FTPFILE", fileData, Route.getCrmDefaultDb());
                    
                }
            }
        }
    }
}
