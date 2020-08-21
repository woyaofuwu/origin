
package com.asiainfo.veris.crm.order.web.group.bat.batbindbroadbandmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;

public abstract class BatBindBroadBandForDesk extends CSBasePage
{
	public abstract void setCondition(IData condition);
	  
	public void init(IRequestCycle cycle) throws Exception
    {
		setCondition(getData());
    }
   
}
