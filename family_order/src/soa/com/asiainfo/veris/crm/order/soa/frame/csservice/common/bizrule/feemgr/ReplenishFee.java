package com.asiainfo.veris.crm.order.soa.frame.csservice.common.bizrule.feemgr;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee.FeeInfoQry;

public final class ReplenishFee
{
    private final static Logger logger = Logger.getLogger(ReplenishFee.class);

    public static IData getReplenishFeeSn(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("==============用户充值信息实时查询接口==================");
        }
        
        String serialNumber = input.getString("IDValue");
        String startActionTime = input.getString("StartActionTime");
        String endActionTime = input.getString("EndActionTime");
        
        boolean status = true;
        String desc = "";
        IData output = new DataMap();
        IDataset payLogsSet = new DatasetList();
        
        if (StringUtils.isEmpty(serialNumber)) {
        	status = false;
        	desc = "手机号码不可为空！";
		}else if (StringUtils.isEmpty(startActionTime)) {
			status = false;
        	desc = "开始时间不可为空！";
		}else if (StringUtils.isEmpty(endActionTime)) {
			status = false;
        	desc = "结束时间不可为空！";
		}
        if (status) {
        	IData data = new DataMap();
        	data.put("SERIAL_NUMBER", serialNumber);
        	data.put("START_ACTION_TIME", startActionTime);
        	data.put("END_ACTION_TIME", endActionTime);
        	IDataset infos = FeeInfoQry.getReplenishBySn(data);
        	logger.debug("chenhh==infos:"+infos);
        	if (IDataUtil.isNotEmpty(infos)) {
				for (int i = 0; i < infos.size(); i++) {
					IData map = new DataMap();
					map.put("IDValue", infos.getData(i).getString("SERIAL_NUMBER"));
					map.put("ActionTime", infos.getData(i).getString("ACTION_TIME"));
					map.put("ChargeMoney", infos.getData(i).getString("CHARGEMONEY"));
					map.put("OrganID", infos.getData(i).getString("ORGAN_ID"));
					payLogsSet.add(map);
				}
			}
        	
        	output.put("BizCode", "0000");
        	output.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	output.put("Paylogs", payLogsSet);
		}else{
			output.put("BizCode", "2998");
			output.put("BizDesc", desc);
		}
        logger.debug("chenhh==output:"+output);
        return output;
    }
}
