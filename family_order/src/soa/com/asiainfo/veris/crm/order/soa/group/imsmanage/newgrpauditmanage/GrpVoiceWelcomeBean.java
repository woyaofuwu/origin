
package com.asiainfo.veris.crm.order.soa.group.imsmanage.newgrpauditmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MFileInfoQry;

public class GrpVoiceWelcomeBean extends CSBizBean
{

    /**
     * 保存附件信息
     * 
     * @param map
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData map) throws Exception
    {
        
        String userId = map.getString("USER_ID","");
        String fileList = map.getString("GRP_FILE_LIST");
        String vpnNo = map.getString("VPN_NO","");
        String wordsDes = map.getString("WORDS_DES","");
        
        IData grpInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if (IDataUtil.isEmpty(grpInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, userId);
        }
        
        String custId = grpInfo.getString("CUST_ID");
        // 查询集团客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isEmpty(grpCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_996, custId);
        }
        
        String flowId = SeqMgr.getFeeFlowId();
        
        if(StringUtils.isNotEmpty(fileList)){
            String serialNumberA = grpInfo.getString("SERIAL_NUMBER");
            String groupId = grpCustData.getString("GROUP_ID");
            String custName = grpCustData.getString("CUST_NAME");
            String staffId =  CSBizBean.getVisit().getStaffId();
            String createTime = SysDateMgr.getSysTime();
            String partitionId = StrUtil.getPartition4ById(userId);
            
            String[] fileArray = fileList.split(",");
            
            for (int i = 0, row = fileArray.length; i < row; i++)
            {
                IData fileData = new DataMap();
                
                String fileId = fileArray[i];               
                String fileName = "";
                IDataset fileDatas = MFileInfoQry.qryFileInfoListByFileID(fileId);
                if(IDataUtil.isNotEmpty(fileDatas)){
                     fileName = fileDatas.getData(0).getString("FILE_NAME","");
                }
                
                fileData.put("PARTITION_ID",  partitionId);
                fileData.put("USER_ID",  userId);
                fileData.put("FILE_ID",  fileId);
                fileData.put("GROUP_ID",  groupId);
                fileData.put("SERIAL_NUMBER_A",  serialNumberA);
                fileData.put("GROUP_ID",  groupId);
                fileData.put("CUST_NAME",  custName);
                fileData.put("PRODUCT_ID",  "6130");
                fileData.put("CREATE_STAFF", staffId);
                fileData.put("CREATE_TIME",  createTime);
                fileData.put("TRADE_TAG",  "4");
                fileData.put("FILE_NAME", fileName);
                fileData.put("TRADE_ID", flowId);
                fileData.put("TRADE_DESC", "融合总机铃音上传");
                fileData.put("RSRV_STR1", vpnNo);
                fileData.put("RSRV_STR2", wordsDes);
                
                Dao.insert("TF_F_GROUP_FTPFILE", fileData, Route.getCrmDefaultDb());
            }
        }
        
        IData retData = new DataMap();
        // 设置返回数据
        retData.put("ORDER_ID", flowId);

        return IDataUtil.idToIds(retData);
    }
    
    
    /**
     * 查询文件
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryGrpVoiceWelcomeFiles(IData inparams, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_GROUP_FTPFILE", "SEL_GRPMEMBER_FILE_ALL", inparams, pagination);
    }
    
    /**
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryImsGrpFileByFileId(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_GROUP_FTPFILE", "SEL_IMSGRP_FILE_BYFILEID", inparams);
    }
    
    /**
     * 审核欢迎词成功，更新标识RSRV_TAG1为9
     * @param params
     * @return
     * @throws Exception
     */
    public boolean  saveCheckWordWelcome(IData inData) throws Exception
    {
        boolean resultFlag = false;
        String userId = inData.getString("USER_ID","");
        IData data = new DataMap();
        data.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
        data.put("USER_ID", userId);
        data.put("TRADE_TAG", "4");
        data.put("FILE_ID", inData.getString("FILE_ID",""));
        data.put("RSRV_TAG1","9");
        resultFlag = Dao.save("TF_F_GROUP_FTPFILE", data,new String[]{"PARTITION_ID","USER_ID","FILE_ID","TRADE_TAG"});
        return resultFlag;
    }
    
}
