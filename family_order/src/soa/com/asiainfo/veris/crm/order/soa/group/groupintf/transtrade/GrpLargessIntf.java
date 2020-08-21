
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class GrpLargessIntf
{

    private static final long serialVersionUID = 1L;

    protected static final Logger logger = Logger.getLogger(GrpLargessIntf.class);
    
    /**
     * 畅享流量批量新增接口
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset BatAddLargessFluxDiscnt(IData data) throws Exception
    {
        String grp_serial_number = IDataUtil.getMandaData(data, "GRP_SERIAL_NUMBER");//集团产品编码
        String enet_info_query = IDataUtil.getMandaData(data, "ENET_INFO_QUERY");//绑定方式
        String limit_fee = IDataUtil.getMandaData(data, "LIMIT_FEE");//分配的流量(单位M)
        String serial_number = IDataUtil.getMandaData(data, "SERIAL_NUMBER");//成员号码
        String bat_task_name = data.getString("BAT_TASK_NAME");//批量任务名
        
        IDataset resultInfos = new DatasetList();
        IData batData = new DataMap();
        
        checkGrpLargessByGrpserialnumber(grp_serial_number,data);
        data.put(Route.USER_EPARCHY_CODE, "0898");
        IDataset batResultSet = CSAppCall.call("SS.LargessGrpMemBatMgrSVC.createBatLargessLimitationMember", data);
        
        
        if(IDataUtil.isNotEmpty(batResultSet)){
            batData = batResultSet.getData(0);
        }
        batData.put("X_RESULTINFO", "成功!");
        batData.put("X_RESULTCODE", "0");
        resultInfos.add(batData);
                

  
        return resultInfos;
        
    }
    
    
    /**
     * 校验传入的集团产品编码是否是畅享流量产品
     *
     * @param data
     * @return
     * @throws Exception
     */
    private static  void checkGrpLargessByGrpserialnumber(String serialNumber,IData info) throws Exception
    {
        if(StringUtils.isNotBlank(serialNumber)){
            IData grpuserInfo = UcaInfoQry.qryUserInfoBySnForGrp(serialNumber);
            
            if (IDataUtil.isEmpty(grpuserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
            }
            String userId = grpuserInfo.getString("USER_ID");
            String custId = grpuserInfo.getString("CUST_ID");
                        
            // 查询集团客户资料
            IData custinfos = UcaInfoQry.qryGrpInfoByCustId(custId);
            if (IDataUtil.isEmpty(custinfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_190);
            }
            String groupId = custinfos.getString("GROUP_ID");
            
            
            IData inParam = new DataMap();
            inParam.put("USER_ID", userId);
            IDataset gffInfoSets = CSAppCall.call("SS.LargessFluxGrpMainSVC.queryUserGrpGfffInfo", inParam);
            if (IDataUtil.isEmpty(gffInfoSets)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团产品未分配畅享流量!");
            }
            

        }
    }
    
    
}


