
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.switches;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

/**
 * 总开关开逻辑； 总开关关时，所有的分开关关闭； 总开关开时，只打开总开关
 * 
 * @author xiekl
 */
public class MainSwitchFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {

        IDataset platSwitchList = StaticUtil.getStaticList("PLAT_SWITCH");
        IDataset serviceIdList = new DatasetList();
        IDataset operCodeList = new DatasetList();
        IDataset startDateList = new DatasetList();
        String serialNumber = input.getString("SERIAL_NUMBER");

        int size = platSwitchList.size();
        for (int i = 0; i < size; i++)
        {
            IData platSwitch = platSwitchList.getData(i);

            String serviceId = platSwitch.getString("DATA_ID");
            UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);

            if (ucaData.getUserPlatSvcByServiceId(serviceId) != null && ucaData.getUserPlatSvcByServiceId(serviceId).size() > 0)
            {
                serviceIdList.add(serviceId);
                operCodeList.add(PlatConstants.OPER_SERVICE_OPEN); // 服务开关开
                startDateList.add(SysDateMgr.getSysTime());
            }
        }

        if (IDataUtil.isEmpty(serviceIdList))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "用户DSMP开关都已经打开，不需要要再打开！");
        }

        input.put("SERVICE_ID", serviceIdList);
        input.put("OPER_CODE", operCodeList);
        input.put("START_DATE", startDateList);
    }

}
