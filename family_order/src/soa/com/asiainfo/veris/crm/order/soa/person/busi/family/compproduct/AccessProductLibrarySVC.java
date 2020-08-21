
package com.asiainfo.veris.crm.order.soa.person.busi.family.compproduct;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;

public class AccessProductLibrarySVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(AccessProductLibrarySVC.class);

    private static final long serialVersionUID = 1L;

    /**
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData getStandardProductUrl(IData input) throws Exception
    {
    	IData data = new DataMap();
    	String result = "/dh-admin/prodRepoPortal/#/212092/";
    	
    	IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",9973,"STANDARD_PRODUCT_URL","ZZZZ");
    	if(IDataUtil.isNotEmpty(comparas) && comparas.size()>0){
			result = ((IData)comparas.get(0)).getString("PARA_CODE1");
		}
    	data.put("resultUrl",result);

        return data;
    }
}
