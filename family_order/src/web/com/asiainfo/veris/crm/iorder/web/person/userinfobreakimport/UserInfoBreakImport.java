package com.asiainfo.veris.crm.iorder.web.person.userinfobreakimport;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserInfoBreakImport extends PersonBasePage {
	
	 /**
     * 批量查询黑名单信息
     * @param cycle
     * @throws Exception
     * @author weipeng.feng
     * @date 2018-1-12
     */
    public void  importQueryDataInfo(IRequestCycle cycle) throws Exception{
    	
    	    IData param = this.getData();
    	    param.put("PSTP_ID", "1");
            IDataset result = CSViewCall.call(this, "SS.UserInfoBreakImportQrySVC.batchQryUserServiceState", param);
            this.setInfos(result);
    }
	
    public abstract void setInfos(IDataset infos);
	public abstract void setCode(IDataset code);
    public abstract void setInfo(IData info);
	public abstract void setCount(long count);
	
}
