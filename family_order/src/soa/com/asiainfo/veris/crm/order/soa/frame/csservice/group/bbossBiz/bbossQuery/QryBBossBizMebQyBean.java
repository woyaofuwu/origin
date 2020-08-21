
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMebInfoQry;

/**
 * 一级BBOSS业务成员签约订购查询--用于查询页面衔接中间复杂查询逻辑
 * 
 * @author liuxx3
 * @date 2014 -07-10
 */
public class QryBBossBizMebQyBean
{

    /**
     * 一级BBOSS业务成员签约订购查询
     * 
     * @author liuxx3
     * @date 2014 -07-10
     */
    public static IDataset qryBBossBizMebQy(IData inParam, Pagination pagination) throws Exception
    {
        // 1.取出参数
        String serial_number = inParam.getString("SERIAL_NUMBER");
        String product_offer_id = inParam.getString("PRODUCT_OFFER_ID");
        String state = inParam.getString("STATE");
        String ec_serial_number = inParam.getString("EC_SERIAL_NUMBER");
        String groupId = inParam.getString("GROUP_ID");

//        String cust_id = "";
//        if(groupId!=null&&!groupId.equals("")){
//        	IDataset groupInfos = GrpInfoQry.queryGroupCustInfoByGroupId(groupId);
//        	if(!groupInfos.isEmpty()){
//        		cust_id = groupInfos.getData(0).getString("CUST_ID","");
//        	}
//        }
        
        IDataset result = new DatasetList();

        // 关联查询 tf_b_trade_grp_merch_meb tf_bh_trade tf_f_cust_group 3表
        IDataset MebQyInfos = TradeGrpMerchMebInfoQry.qryMerchMebInfoBySnPoofferIdStateEcSnCustId(groupId, serial_number, product_offer_id, state, ec_serial_number, pagination);

        // 判断查询结果为空 退出处理
        if (IDataUtil.isEmpty(MebQyInfos))
        {
            return result;
        }

        for (int i = 0; i < MebQyInfos.size(); i++)
        {
            IData MebQyInfo = MebQyInfos.getData(i);
            
//            String custId = MebQyInfo.getString("CUST_ID_B","");
//            IData groupInfo = UcaInfoQry.qryCustInfoByCustId(custId);
//            MebQyInfo.put("JT_CUST_NAME", groupInfo.getString("CUST_NAME", ""));
//            MebQyInfo.put("GROUP_ID", groupInfo.getString("GROUP_ID", ""));
//            MebQyInfo.put("CITY_CODE", groupInfo.getString("CITY_CODE", "")); 
            
            String staffId = MebQyInfo.getString("TRADE_STAFF_ID");

            String staffName = UStaffInfoQry.getStaffNameByStaffId(staffId);// 员工名称

            // 拼装页面信息
            if (StringUtils.isEmpty(staffName))
            {
                MebQyInfo.put("STAFF_NAME", "无效员工");
            }
            else
            {
                MebQyInfo.put("STAFF_NAME", staffName);
            }

            result.add(MebQyInfo);
        }

        return result;
    }

}
