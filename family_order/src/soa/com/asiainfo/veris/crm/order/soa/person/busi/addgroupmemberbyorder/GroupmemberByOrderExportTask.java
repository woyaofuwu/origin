
package com.asiainfo.veris.crm.order.soa.person.busi.addgroupmemberbyorder;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GroupmemberByOrderExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData data, Pagination arg1) throws Exception
    {
    	IDataset detial = CSAppCall.call("SS.ReqGroupmemberByOrderSVC.queryGrpBooktInfos", data);
    	System.out.println("=====++++==="+detial);
    	
        if (IDataUtil.isNotEmpty(detial))
        {
            for (int i = 0; i < detial.size(); i++)
            {
                String cityCode = detial.getData(i).getString("CITY_CODE");
                String userpspt_type_code = detial.getData(i).getString("USEPSPT_TYPE_CODE");
                String rsrvTag2 = detial.getData(i).getString("RSRV_TAG2");
                String eparchyCode = detial.getData(i).getString("EPARCHY_CODE");
                String svcModeCode = detial.getData(i).getString("SVC_MODE_CODE");
                
                String memberKind = detial.getData(i).getString("MEMBER_KIND");
                String isContact = detial.getData(i).getString("IS_CONTACT");
                String isMobile = detial.getData(i).getString("IS_MOBILE");
                String rsrvTag1 = detial.getData(i).getString("RSRV_TAG1");
                String rsrvTag4 = detial.getData(i).getString("RSRV_TAG4");
                String rsrvTag5 = detial.getData(i).getString("RSRV_TAG5");
                
                if (!"".equals(memberKind))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "GRP_MEMKIND";
        			value[1] = "CSM";
        			value[2] = memberKind;
        			detial.getData(i).put("MEMBER_KIND", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                if (!"".equals(isContact))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "YESNO";
        			value[1] = "CSM";
        			value[2] = isContact;
        			detial.getData(i).put("IS_CONTACT", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                if (!"".equals(isMobile))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "YESNO";
        			value[1] = "CSM";
        			value[2] = isMobile;
        			detial.getData(i).put("IS_MOBILE", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                if (!"".equals(rsrvTag1))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "YESNO";
        			value[1] = "CSM";
        			value[2] = rsrvTag1;
        			detial.getData(i).put("RSRV_TAG1", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                if (!"".equals(rsrvTag4))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "YESNO";
        			value[1] = "CSM";
        			value[2] = rsrvTag4;
        			detial.getData(i).put("RSRV_TAG4", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                if (!"".equals(rsrvTag5))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "YESNO";
        			value[1] = "CSM";
        			value[2] = rsrvTag5;
        			detial.getData(i).put("RSRV_TAG5", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                /////////////////////////////////////////////////////////////////////
                
                if (!"".equals(cityCode))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "CITY_CODE_GOODS";
        			value[1] = "CSM";
        			value[2] = cityCode;
        			detial.getData(i).put("CITY_CODE", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                
                if (!"".equals(userpspt_type_code))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "TD_S_PASSPORTTYPE";
        			value[1] = "CUSTMGR";
        			value[2] = userpspt_type_code;
        			detial.getData(i).put("USEPSPT_TYPE_CODE", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                
                if (!"".equals(rsrvTag2))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "YESNO";
        			value[1] = "CSM";
        			value[2] = rsrvTag2;
        			detial.getData(i).put("RSRV_TAG2", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                
                if (!"".equals(rsrvTag2))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "AREA_CODE";
        			value[1] = "CUSTMGR";
        			value[2] = eparchyCode;
        			detial.getData(i).put("EPARCHY_CODE", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                
                if (!"".equals(rsrvTag2))
                {
                	// 类型转换
        			String[] keys = new String[3];
        			keys[0] = "TYPE_ID";
        			keys[1] = "SUBSYS_CODE";
        			keys[2] = "DATA_ID";
        			String[] value = new String[3];
        			value[0] = "TD_S_SERVICE_MODE";
        			value[1] = "CUSTMGR";
        			value[2] = svcModeCode;
        			detial.getData(i).put("SVC_MODE_CODE", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value));
                }
                
                
                
            }
        }
        return detial;
    }

}
