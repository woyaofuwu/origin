
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.ChangeSvcStateReqData;

public class ChangeWidenetSvcStateAction implements ITradeAction
{

    private static transient Logger logger = Logger.getLogger(ChangeWidenetSvcStateAction.class);

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("进入ChangeWidenetSvcStateAction函数，宽带报停报开Action...Start!");
        }

        UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        String KDSerialNumber = "KD_" + serialNumber;
        String tradeTypeCode = btd.getTradeTypeCode();

        IData wideInfo = UcaInfoQry.qryUserInfoBySn(KDSerialNumber);
        if (IDataUtil.isNotEmpty(wideInfo))
        {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", KDSerialNumber);
            param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            param.put("VALID_FLAG", "1");// 必传，宽带规则判断标示，传1宽带不判断手机状态

            // 宽带业务类型
            String operName = "";
            IDataset userWidenetInfo = WidenetInfoQry.getUserWidenetInfo(wideInfo.getString("USER_ID"));
            if (IDataUtil.isNotEmpty(userWidenetInfo))
            {
                IData userWidenet = userWidenetInfo.getData(0);
                String widenetType = userWidenet.getString("RSRV_STR2");
                if ("131".equals(tradeTypeCode))
                {// 手机报停
                    operName = "发起宽带报停";
                    if ("4".equals(widenetType))
                    { // 增加校园宽带，手机报停，宽带报停
                        param.put("TRADE_TYPE_CODE", "632");
                    }
                    else
                    {
                        param.put("TRADE_TYPE_CODE", "603");
                    }

                }
                else if ("133".equals(tradeTypeCode))
                {// 手机报开
                    operName = "发起宽带报开";

                    if ("4".equals(widenetType))
                    { // 增加校园宽带，手机报开，宽带报开
                        param.put("TRADE_TYPE_CODE", "633");
                    }
                    else
                    {
                        param.put("TRADE_TYPE_CODE", "604");
                    }
                }
                else if ("497".equals(tradeTypeCode) || "496".equals(tradeTypeCode) || "126".equals(tradeTypeCode))
                {
                    // 紧急开机、担保开机、局方开机
                    operName = "发起宽带紧急报开";
                    if ("1".equals(widenetType))
                    {
                        param.put("TRADE_TYPE_CODE", "639");
                    }
                    else if ("2".equals(widenetType) || "5".equals(widenetType)||"6".equals(widenetType))
                    {
                        param.put("TRADE_TYPE_CODE", "637");
                    }
                    else if ("3".equals(widenetType))
                    {
                        param.put("TRADE_TYPE_CODE", "638");
                    }
                    else if ("4".equals(widenetType))
                    {
                        param.put("TRADE_TYPE_CODE", "641");
                    }
                    else
                    {
                        param.put("TRADE_TYPE_CODE", "639");
                    }
                }
            }

            String remark = "";
            IData temp = new DataMap();
            temp.putAll(param);
            try
            {
            	if ("131".equals(tradeTypeCode))
            	{
            	    ChangeSvcStateReqData changeSvcStateBrd = (ChangeSvcStateReqData) btd.getRD();
            	    
            	    if ("Y".equals(changeSvcStateBrd.getIsStopWide()))
            	    {
            	    	temp.put("TV_FLAG", "1");// 魔百和规则判断标示，传1时：手机关联宽带停机时不判断魔百和是否停机？？

            	    	IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", temp);
                        IData idata = result.getData(0);
                        remark = "[" + idata.getString("TRADE_ID") + "]";
            	    }
            	    else
            	    {
            	        remark = new StringBuilder(operName).append("：").append("手机报停时选择不停宽带!").toString();
            	    }
            	}else if ("133".equals(tradeTypeCode))
            	{
            	    ChangeSvcStateReqData changeSvcStateBrd = (ChangeSvcStateReqData) btd.getRD();
            	    
            	    if ("Y".equals(changeSvcStateBrd.getIsOpenWide()))
            	    {
            	        IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", temp);
                        IData idata = result.getData(0);
                        remark = "[" + idata.getString("TRADE_ID") + "]";
            	    }
            	    else
            	    {
            	        remark = new StringBuilder(operName).append("：").append("手机报开时选择不开宽带!").toString();
            	    }
            	}else{
            		IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", temp);
                    IData idata = result.getData(0);
                    remark = "[" + idata.getString("TRADE_ID") + "]";
            	}
            }
            catch (Exception e)
            {
                remark = new StringBuilder(operName).append("：").append(Utility.getBottomException(e).getMessage()).toString();
                if (logger.isDebugEnabled())
                {
                    logger.debug(remark);
                }
            }

            // 标记手机报停报开连带宽带报停报开的成功与否，修改TF_F_USER_SVCSTATE表
            List<SvcStateTradeData> list = btd.getRD().getUca().getUserSvcsState();
            for (int i = 0, size = list.size(); i < size; i++)
            {
                SvcStateTradeData tradeData = list.get(i);
                String mainTag = tradeData.getMainTag();
                // String stateCode = tradeData.getStateCode();

                if (StringUtils.equals("1", mainTag))
                {// && StringUtils.equals("0", stateCode)
                    tradeData.setRsrvStr5(subRemark(remark));
                    param.put("ORDER_TYPE_CODE", temp.getString("ORDER_TYPE_CODE"));
                    tradeData.setRsrvStr4(param.toString());
                    break;
                }
            }

            if (logger.isDebugEnabled())
            {
                logger.debug("退出ChangeWidenetSvcStateAction，宽带报停报开Action...End!");
            }
        }
    }

    private String subRemark(String remark)
    {
        byte[] bytes = remark.getBytes();
        if (bytes.length > 200)
        {
            byte[] newbytes = new byte[200];
            for (int j = 200; j > 0; j--)
            {
                newbytes[200 - j] = bytes[bytes.length - j];
            }
            remark = new String(newbytes);
        }
        return remark;
    }

}
