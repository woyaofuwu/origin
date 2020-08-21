
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatGrpMemVpmnCreateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        if("".equals(batData.getString("DATA1","")))
        {
            String sc = batData.getString("SHORT_CODE","");
            if(!"".equals(sc))
            {
                batData.put("DATA1", sc);
            }
        }
        
        String shortCode = IDataUtil.getMandaData(batData, "DATA1");

        // 构建产品参数信息
        IData productParam = new DataMap();
        productParam.put("JOIN_IN", condData.getString("JOIN_IN",""));
        productParam.put("SHORT_CODE", shortCode);
        
        String joinIn = condData.getString("JOIN_IN","");
        if(joinIn != null && "1".equals(joinIn)){
            String serialNumber = batData.getString("SERIAL_NUMBER");
            if(StringUtils.isNotBlank(serialNumber)){
                IData param = new DataMap();
                param.put("SERIAL_NUMBER", serialNumber);
                IDataset resultSet = GrpMebInfoQry.getGrpMenberInfos(param);
                if(IDataUtil.isNotEmpty(resultSet)){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户"+ serialNumber +"已经是898集团成员!");
                }
            }
        }
        
        //start add by wangyf6 at 20150306 
        String grpClipType = condData.getString("GRP_CLIP_TYPE","");//GRP_CLIP_TYPE 呼叫来显方式 
        String grpUserClipType = condData.getString("GRP_USER_CLIP_TYPE","");//GRP_USER_CLIP_TYPE 选择号显方式 
        String grpUserMod = condData.getString("GRP_USER_MOD","");//GRP_USER_MOD 成员修改号显方式 
        String clipType = condData.getString("CLIP_TYPE",""); 
        if("1".equals(grpClipType) && "1".equals(grpUserMod)){ 
            if(StringUtils.isNotBlank(clipType)){ 
                productParam.put("NOTIN_GRP_CLIP_TYPE", grpClipType); 
                productParam.put("NOTIN_GRP_USER_CLIP_TYPE", grpUserClipType); 
                productParam.put("NOTIN_GRP_USER_MOD", grpUserMod); 
                productParam.put("NOTIN_CLIP_TYPE", clipType); 
            } 
        } 
        //end add by wangyf6 at 20150306 

        
        IDataset productParamDataset = new DatasetList();
        GroupBatTransUtil.buildProductParam(batData.getString("PRODUCT_ID"), productParam, productParamDataset);

        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
        svcData.put("SHORT_CODE", shortCode);

        // 构建RES资源信息
        IDataset resinfos = new DatasetList();
        IData res = new DataMap();
        res.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        res.put("RES_TYPE_CODE", "S");
        res.put("RES_CODE", shortCode);
        resinfos.add(res);

        svcData.put("RES_INFO", resinfos);
    }

}
