package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark.maker.IWaterMarkMaker;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 水印处理分发器
 *
 * @author ckh
 * @date 2018/12/14.
 */
class WaterMarkDispatcher
{
    private static final transient Logger log = Logger.getLogger(WaterMarkDispatcher.class);
    ConcurrentHashMap<String, IWaterMarkMaker> dispatcherMap = new ConcurrentHashMap<String, IWaterMarkMaker>();

    /**
     * 构造方法，初始化{@linkplain WaterMarkDispatcher#dispatcherMap}
     *
     * @param watermarkConfig {@link IDataset 水印配置}
     */
    WaterMarkDispatcher(IDataset watermarkConfig)
    {
        for (int i = 0; i < watermarkConfig.size(); i++)
        {
            IData watermark = watermarkConfig.getData(i);
            String dataType = watermark.getString("DATA_ID", "");
            String dataClass = watermark.getString("DATA_NAME", "");
            try
            {
                dispatcherMap.put(dataType, (IWaterMarkMaker) Class.forName(dataClass).newInstance());
            }
            catch (Exception e)
            {
                log.error("水印处理类初始化失败！是否未实现IWaterMarkMaker接口？DATA_ID:" + dataType + "DATA_NAME:" + dataClass, e);
            }
        }
    }

    /**
     * 分发处理请求
     *
     * @param dataType 水印处理类型
     * @param paramMap 请求参数集合
     * @param resp     {@linkplain HttpServletResponse}
     * @throws Exception 处理异常
     */
    public void dispatch(String dataType, IData paramMap, HttpServletResponse resp) throws Exception
    {
        IWaterMarkMaker maker = dispatcherMap.get(dataType);
        if (maker == null)
        {
            IDataset watermarkConfig = StaticUtil.getStaticList("FILE_WATERMARK", dataType);
            if (DataUtils.isEmpty(watermarkConfig))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, dataType + "类型水印处理在数据库中未配置！");
            }
            maker = (IWaterMarkMaker) Class.forName(watermarkConfig.first().getString("DATA_NAME")).newInstance();
            dispatcherMap.put(dataType, maker);
        }
        maker.dealWaterMarkMaker(paramMap, resp);
    }
}
