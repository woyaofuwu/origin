
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean.LargessFluxQry;

public class BatAddLargessFluxDiscntTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        String grpSerialNumber = IDataUtil.chkParam(condData, "GRP_SERIAL_NUMBER");
        IDataUtil.chkParam(condData, "DISCNT_CODE");
        String limitFee = IDataUtil.chkParam(condData, "LimitFee");
        String batchId = IDataUtil.chkParam(batData, "BATCH_ID");
                
        batData.put("GRP_SERIAL_NUMBER",grpSerialNumber);
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", grpSerialNumber);
        UcaData grpUcaData = UcaDataFactory.getNormalUcaBySnForGrp(inparam);
        
        String grpUserId = grpUcaData.getUserId();
        String grpProductId = grpUcaData.getProductId();
        batData.put("GRP_USER_ID", grpUserId);
        batData.put("GRP_PRODUCT_ID", grpProductId);
        
        String bindTeam = IDataUtil.chkParam(condData, "BindTeam");
        if(StringUtils.isNotBlank(bindTeam) && "0".equals(bindTeam)){//个人用户
            String serialNumber = batData.getString("SERIAL_NUMBER");            
            // 查询号码用户信息
            IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(GrpException.CRM_GRP_715);
            }
            
            String custId = userInfo.getString("CUST_ID");
            IData custInfo = UcaInfoQry.qryPerInfoByCustId(custId);
            if (IDataUtil.isEmpty(custInfo))
            {
                CSAppException.apperr(GrpException.CRM_GRP_716);
            }
            
        } else if(StringUtils.isNotBlank(bindTeam) && "1".equals(bindTeam)){//集团用户
            String serialNumber = batData.getString("SERIAL_NUMBER");
            // 查询集团用户信息
            IData grpuserInfo = UcaInfoQry.qryUserInfoBySnForGrp(serialNumber);
            
            
            if (IDataUtil.isEmpty(grpuserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
            }
            
            String custId = grpuserInfo.getString("CUST_ID");
            // 查询集团客户资料
            IData custinfos = UcaInfoQry.qryGrpInfoByCustId(custId);
            if (IDataUtil.isEmpty(custinfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_190);
            }
            
            IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BofException.CRM_BOF_017, serialNumber);
            }
            
            String productId = userInfo.getString("PRODUCT_ID","");
            
            if(!"7051".equals(productId) && !"7342".equals(productId) && !"7344".equals(productId)
                    && !"9945".equals(productId) && !"9935".equals(productId) && !"7340".equals(productId)){
                CSAppException.apperr(GrpException.CRM_GRP_869);
            }

        }
        
        inparam.clear();
        inparam.put("USER_ID", grpUserId);
        IDataset feeSets = LargessFluxQry.qryUserGrpGfffAllFeeBySn(inparam);//查询需要分配的流量
        if(IDataUtil.isEmpty(feeSets)){
            CSAppException.apperr(GrpException.CRM_GRP_863,grpSerialNumber);
        }
        String allFeeStr = feeSets.getData(0).getString("ALLFEE");
        double intAllFee = Double.parseDouble(allFeeStr);//集团产品分配到总流量
        
        double intLimitFee = Double.parseDouble(limitFee);//每个用户分配的流量数
        IData data = new DataMap();
        data.put("BATCH_ID", batchId);
        data.put("DEAL_STATE", "9");
        IDataset batFeeSet = BatDealInfoQry.queryBatchDealSumByBatchId(data);//该批量里导入数量
        
        int allNum = 0;
        if(IDataUtil.isNotEmpty(batFeeSet)){
            String upSum = batFeeSet.getData(0).getString("UDSUM");
            allNum = Integer.parseInt(upSum);
        }
        
        //集团产品的总流量不足以分配时，拦截提示
        if((allNum * intLimitFee) > intAllFee){
            CSAppException.apperr(GrpException.CRM_GRP_867,grpSerialNumber);
        }
        
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("GRP_SERIAL_NUMBER", batData.getString("GRP_SERIAL_NUMBER"));
        svcData.put("GRP_USER_ID", batData.getString("GRP_USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("PRODUCT_ID", batData.getString("GRP_PRODUCT_ID"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "false"));
        svcData.put("DISCNT_CODE", condData.getString("DISCNT_CODE"));
        svcData.put("BindTeam", condData.getString("BindTeam"));
        svcData.put("LimitFee", condData.getString("LimitFee"));
    }

}
