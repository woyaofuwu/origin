
package com.asiainfo.veris.crm.order.soa.group.param.adc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.AdcCamponSvrcodeInfoQry;

public class AdcCamponSvrCodeSvc extends CSBizService
{
    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    public IDataset getAdcCamponSvrcodeInfo(IData input) throws Exception
    {
        IDataset output = AdcCamponSvrcodeInfoQry.qryAdcCamponSvrcodeInfo(input);
        return output;
    }

    public IDataset getAdcCamponSvrcodeInfoForSearch(IData input) throws Exception
    {
        IDataset output = AdcCamponSvrcodeInfoQry.qryAdcCamponSvrcodeInfoByKey(input);
        return output;
    }

    public void insertCamponServCode(IData input) throws Exception{

        input.put("OPER_CODE", "0");
        input.put("START_DATE", SysDateMgr.getSysTime());
        input.put("END_DATE", SysDateMgr.addDays(15));
        input.put("CALLING_TYPE_CODE", "");
        input.put("DEPART_ID", this.getVisit().getDepartId());
        input.put("STAFF_ID", this.getVisit().getStaffId());
        input.put("UPDATE_TIME", SysDateMgr.getSysTime());

        AdcCamponSvrcodeInfoQry.insertCamponServCode(input);
    }

    public IDataset cancelCamponServcode(IData input) throws Exception {
        IDataset result = new DatasetList();
        IData updrst= new DataMap();
        int updcnt = AdcCamponSvrcodeInfoQry.cancelCamponServcode(input);
        updrst.put("UPDCNT", updcnt);
        result.add(updrst);

        return result;
    }
}
