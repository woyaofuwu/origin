package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.INodeData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util.SubmitDataParseUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class WidePreRegService extends CSBizService{
	private static final long serialVersionUID = 1L;
	protected static final Logger log = Logger.getLogger(WidePreRegService.class);
    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-4 下午04:44:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-4 chengxf2 v1.0.0 修改原因
     */
    private IData buildWideNetBookValue(ISubmitData submitData)
    {
        List<INodeData> nodeDataList = submitData.getNodeDataList();
        if (!nodeDataList.isEmpty())
        {
            INodeData nodeData = nodeDataList.get(0);
            if (nodeData.containsJsonObject("WIDENET_BOOK_DATA"))
            {
                return nodeData.getJsonObject("WIDENET_BOOK_DATA").getValue();
            }
        }
        return null;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-4 下午04:49:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-4 chengxf2 v1.0.0 修改原因
     */
    public IData dealService(IData input) throws Exception
    {
        ISubmitData submitData = SubmitDataParseUtil.parseSubmitData(input);
        String eparchyCode = this.getTradeEparchyCode();
        IData wideNetBookValue = buildWideNetBookValue(submitData);
        if (IDataUtil.isNotEmpty(wideNetBookValue))
        {

            //宽带产品类型
        	wideNetBookValue.put("RSRV_TAG1",wideNetBookValue.getString("WIDE_TYPE_CODE",""));
            //预约安装时间
            wideNetBookValue.put("RSRV_DATE1",wideNetBookValue.getString("PRE_DATE",""));
            //登记状态
            wideNetBookValue.put("RSRV_STR1","1");
            //回访结果
            wideNetBookValue.put("RSRV_STR2","1");
            //地区
            wideNetBookValue.put("RSRV_STR3",wideNetBookValue.getString("AREA_CODE",""));
            //宽带带宽
            wideNetBookValue.put("RSRV_TAG2",wideNetBookValue.getString("WBBW",""));
            //预装原因
            wideNetBookValue.put("RSRV_STR4",wideNetBookValue.getString("PRE_CAUSE",""));
            //用户手机号码
            wideNetBookValue.put("RSRV_NUM4",wideNetBookValue.getString("SERIAL_NUMBER",""));
            wideNetBookValue.put("AUDIT_STATUS", "1");
            wideNetBookValue.put("INST_ID", SeqMgr.getInstId(eparchyCode));
            wideNetBookValue.put("REG_DATE", SysDateMgr.getSysTime());
            wideNetBookValue.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            wideNetBookValue.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            Dao.insert("TF_F_WIDENET_BOOK", wideNetBookValue, eparchyCode);
        }
        return wideNetBookValue;
    }

}
