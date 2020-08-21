
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

/**
 * BBOSS成员变更接口(提供给湖南集团外围)
 * 
 * @author jiangmj
 */
public class BBossMemberChgIntf
{

    /**
     * 成员变更
     */
    public IDataset MemberChgSync(IData data) throws Throwable
    {

        String sGVALUE_CODE = IDataUtil.getMandaData(data, "GVALUE_CODE");
        data.put("PRODUCTID", sGVALUE_CODE);
        data.put("ORDER_NO", sGVALUE_CODE);
        String sITEM_FIELD_NAME = IDataUtil.getMandaData(data, "ITEM_FIELD_NAME");
        data.put("SRC", sITEM_FIELD_NAME);

        String sSERIAL_NUMBER = IDataUtil.getMandaData(data, "SERIAL_NUMBER");

        String sOPER_CODE = IDataUtil.getMandaData(data, "OPER_CODE");
        data.put("ACTION", sOPER_CODE);
        String sUSER_TYPE = IDataUtil.getMandaData(data, "USER_TYPE");

        String sCOMP_TAG = data.getString("COMP_TAG", "");
        data.put("RSRV_STR10", sCOMP_TAG);

        String sSTART_DATE = IDataUtil.getMandaData(data, "START_DATE");
        data.put("EFFDATE", sSTART_DATE);

        String sPARM_TYPE = data.getString("PARM_TYPE", "");
        data.put("RSRV_STR11", sPARM_TYPE);
        String sPARM_NAME = data.getString("PARM_NAME", "");
        data.put("RSRV_STR12", sPARM_NAME);
        String sPARM_VALUE = data.getString("PARM_VALUE", "");
        data.put("RSRV_STR13", sPARM_VALUE);
        // 报文类型
        data.put("BUSI_SIGN", "BIP4B257_T4101034_1_0");
        // 设置为发起方
        data.put("LOCATION", "OUTINTF");

        IDataset ids = null;// 返回数据定义
        // ids = GrpIntf.IntfDeal(data); // 集团反向接口处理
        return ids;
    }
}
