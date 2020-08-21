/**
 * @Title: TestSVCProcess.java
 * @Package com.ailk.groupservice.demo.bof
 * @Description: TODO(用一句话描述该文件做什么)
 * @author A18ccms A18ccms_gmail_com
 * @date Feb 21, 2014 3:03:38 PM
 * @version V1.0
 */

package com.asiainfo.veris.crm.order.soa.group.demo.bof;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.proxy.BuilderProxy;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.TradeProcess;

/**
 * 项目名称：yn_groupserv 类名称：TestSVCProcess 类描述： 创建人：penghaibo 创建时间：Feb 21, 2014 3:03:38 PM 修改人：penghaibo 修改时间：Feb 21, 2014
 * 3:03:38 PM 修改备注：
 * 
 * @version
 */
public class TestSVCProcess
{
    protected static Logger log = Logger.getLogger(TradeProcess.class);

    public static BaseReqData acceptSvc(IData param) throws Exception
    {
        long beginTime = 0;

        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        String orderTypeCode = param.getString("ORDER_TYPE_CODE");

        // 构建rd
        IBuilder rdb = BuilderProxy.getInstance(tradeTypeCode, orderTypeCode, param);

        beginTime = System.currentTimeMillis();
        if (log.isDebugEnabled())
        {
            log.debug("开始构建请求对象");
        }

        BaseReqData rd = rdb.buildRequestData(param);
        if (log.isDebugEnabled())
        {
            log.debug("构建请求对象 cost time:" + (System.currentTimeMillis() - beginTime) / 1000.0D + "s");
        }

        return rd;
    }
}
