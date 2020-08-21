
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckWidenetProductTypeNew extends BreBase implements IBREScript
{
	private static final long serialVersionUID = 1L;
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String userId = databus.getString("USER_ID");
        //String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String productMode = "";
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// 查询时校验
        {
        	IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
            if (IDataUtil.isEmpty(dataset))
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_3);
            }
            String  widetype = dataset.getData(0).getString("RSRV_STR2");
            //--07：移动GPON宽带，09：ADSL宽带产品，11：移动FTTH宽带，16：海南铁通FTTH，17：海南铁通FTTB，
        	if ("1".equals(widetype))
            {
        		productMode="07";
            }else if ("2".equals(widetype))
            {
            	productMode="09";
            }else if ("3".equals(widetype))
            {
            	productMode="11";
            }else if ("5".equals(widetype))
            {
            	//productMode="16";
            	productMode="11";  //移动FTTH与铁通FTTH合并，使用同一套产品
            }else if ("6".equals(widetype))
            {
            	//productMode="17";
            	productMode="07"; //移动FTTB与铁通FTTB合并，使用同一套产品
            }
            
            IDataset userProductInfos = UserProductInfoQry.getUserWidenetProductInfo(userId, productMode);
            if (IDataUtil.isEmpty(userProductInfos))
            {
            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604020", "未查询到用户订购的有效宽带产品！");
            }

        }

        return false;
    }

}
