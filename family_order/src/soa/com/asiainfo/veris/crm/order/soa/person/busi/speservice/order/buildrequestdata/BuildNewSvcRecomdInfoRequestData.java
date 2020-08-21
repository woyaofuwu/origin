
package com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.buildrequestdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata.NewSvcRecomdInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata.NewSvcRecomdInfoRequestData;

public class BuildNewSvcRecomdInfoRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildNewSvcRecomdInfoRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        NewSvcRecomdInfoRequestData reqData = (NewSvcRecomdInfoRequestData) brd;
        IDataset recommInfos = new DatasetList(data.getString("cond_ADD_SMS_INFO"));
        if (IDataUtil.isNotEmpty(recommInfos))
        {
            List<NewSvcRecomdInfoData> RecomdInfoList = new ArrayList<NewSvcRecomdInfoData>();
            for (int i = 0; i < recommInfos.size(); i++)
            {
                IData recommInfo = recommInfos.getData(i);

                NewSvcRecomdInfoData newSvcRecomdInfo = new NewSvcRecomdInfoData();
                newSvcRecomdInfo.setRecomd_campn_id(recommInfo.getString("CAMPN_ID"));
                newSvcRecomdInfo.setRecomd_campn_name(recommInfo.getString("CAMPN_NAME"));
                newSvcRecomdInfo.setRecomd_reply(recommInfo.getString("REPLY"));
                newSvcRecomdInfo.setRecomd_object_type_desc(recommInfo.getString("OBJECT_TYPE_DESC"));
                newSvcRecomdInfo.setRecomd_object_id(recommInfo.getString("OBJECT_ID"));
                newSvcRecomdInfo.setRecomd_refuse_remark(recommInfo.getString("REFUSE_REMARK"));
                newSvcRecomdInfo.setRecomd_refuse_reason_code(recommInfo.getString("REFUSE_REASON_CODE"));
                newSvcRecomdInfo.setRecomd_object_type(recommInfo.getString("OBJECT_TYPE"));
                newSvcRecomdInfo.setRecomd_other_refuse_reason(recommInfo.getString("OTHER_REFUSE_REASON"));
                newSvcRecomdInfo.setRecomd_source_id(recommInfo.getString("SOURCE_ID"));
                RecomdInfoList.add(newSvcRecomdInfo);

            }
            reqData.setRecomdInfo(RecomdInfoList);
        }

        // reqData.setRecomd_product(data.getString("recomd_PRODUCT")); // 推荐产品
        // reqData.setRecomd_product_result(data.getString("recomd_PRODUCT_RESULT"));// 推荐产品结果
        // reqData.setRecomd_discnt(data.getString("recomd_DISCNT"));// 推荐优惠
        // reqData.setRecomd_discnt_result(data.getString("recomd_DISCNT_RESULT"));// 推荐优惠结果
        // reqData.setRecomd_service(data.getString("recomd_SERVICE"));// 推荐服务
        // reqData.setRecomd_service_result(data.getString("recomd_SERVICE_RESULT"));// 推荐服务结果
        // reqData.setRecomd_platsvc(data.getString("recomd_PLATSVC"));// 推荐平台业务
        // reqData.setRecomd_platsvc_result(data.getString("recomd_PLATSVC_RESULT"));// 推荐平台业务
        // reqData.setRecomd_action(data.getString("recomd_ACTION"));// 推荐活动
        // reqData.setRecomd_action_result(data.getString("recomd_ACTION_RESULT"));// 推荐活动结果
        reqData.setRemark(data.getString("recomd_REMARK"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new NewSvcRecomdInfoRequestData();
    }

}
