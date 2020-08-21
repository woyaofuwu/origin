package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckVideoColorRingServAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        IData commData = getCommParam();
        UcaData uca = btd.getRD().getUca();
        
        //检查是否有针对视频彩铃的操作
        SvcTradeData videoSvc = checkSvcList(btd, commData,"VIDEO");
        if (videoSvc == null)
        {
            //校验是否有VOLTE或彩铃业务退订
            videoSvc = checkSvcList(btd, commData,"CANCEL_SVC");
            
            if (videoSvc != null)
            {
                crtSvcTrade(btd, videoSvc, commData);
            }
        }
        else
        {
            //校验操作是否合理
            checkOper(videoSvc, commData, uca);
        }
        
    }
    /***
     * 检查操作是否合理
     * @param videoSvc
     * @param commData
     * @param uca
     * @throws Exception
     */
    private void checkOper(SvcTradeData videoSvc,IData commData,UcaData uca) throws Exception
    {
        //订购
        if("0".equals(videoSvc.getModifyTag()))
        {
            //1151    用户未开通VoLTE功能
            if (!checkSvc(uca, commData,"PARA_CODE2"))
            {
                CSAppException.appError("1151","用户未开通VoLTE功能,无法订购视频彩铃服务!");
            }
            //1152    用户未开通音频彩铃功能
            if (!checkSvc(uca, commData,"PARA_CODE3"))
            {
                CSAppException.appError("1152","用户未开通音频彩铃功能,无法订购视频彩铃服务!");
            }
            
            //是否开通视频彩铃服务
            IDataset volteSvc = UserSvcInfoQry.getSvcUserId(uca.getUserId(),commData.getString("PARA_CODE1"));
    		
            //1153    用户已开通视频彩铃功能
            if (IDataUtil.isNotEmpty(volteSvc))
            {
                CSAppException.appError("1153","用户已开通视频彩铃功能,无法再次订购视频彩铃服务!");
            }
            
        }
        else if ("1".equals(videoSvc.getModifyTag()))
        {
        	//取消
        	//是否开通彩铃服务
            IDataset volteSvc = UserSvcInfoQry.getSvcUserId(uca.getUserId(),commData.getString("PARA_CODE1"));
        	
            //1154    用户已注销视频彩铃功能
            if (IDataUtil.isEmpty(volteSvc))
            {
                CSAppException.appError("1154","用户已注销视频彩铃功能,无法再次注销视频彩铃服务!");
            }
        }
    }
    
    private IData getCommParam() throws Exception
    {
      //查询视频彩铃服务配置,PARA_CODE1 配置为视频彩铃服务、PARA_CODE2 配置为VOLTE服务、PARA_CODE3 配置为彩铃服务
        IDataset commList=CommparaInfoQry.getCommpara("CSM","2017","VIDEO_COLORRING_SERV",CSBizBean.getUserEparchyCode());
        if(IDataUtil.isEmpty(commList))
        {
            String errors = "视频彩铃服务静态参数【VIDEO_COLORRING_SERV】未配置，请联系管理员！";
            CSAppException.apperr(CrmCommException.CRM_COMM_103, errors);
        }
        return commList.getData(0);
    }
    
    /***
     * 检查用户是否有针对视频彩铃做操作 或者是退订VOLTE\彩铃
     * @param tradeData
     * @param commData
     * @return
     * @throws Exception
     */
    public SvcTradeData checkSvcList(BusiTradeData btd,IData commData,String serv_type) throws Exception
    {
        List<SvcTradeData> tradeSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        //检查是否有视频彩铃服务操作
        SvcTradeData svc_Trd = null;
        for(int i = 0 ; i < tradeSvcs.size() ; i++)
        {
            if ("VIDEO".equals(serv_type))
            {
                if (commData.getString("PARA_CODE1").equals(tradeSvcs.get(i).getElementId()) &&
                        "0_1".indexOf(tradeSvcs.get(i).getModifyTag()) > -1)
                {
                    svc_Trd = tradeSvcs.get(i);
                    
                    //如果是平台过来订购或者取消视频彩铃服务则不需要再次发送服开
                    if ("6".equals(CSBizBean.getVisit().getInModeCode()))
                    {
                    	svc_Trd.setIsNeedPf("0");
                    }
                    
                    break;
                }
            }
            else if("CANCEL_SVC".equals(serv_type))
            {//校验是否有VOLTE或彩铃业务退订
                if ((commData.getString("PARA_CODE2").equals(tradeSvcs.get(i).getElementId()) ||
                        commData.getString("PARA_CODE3").equals(tradeSvcs.get(i).getElementId()))&&
                        "1".equals(tradeSvcs.get(i).getModifyTag()))
                {
                    svc_Trd = tradeSvcs.get(i);
                    break;
                }
            }
        }
        return svc_Trd;
    }
    
    /***
     * 创建视频彩铃退订的服务台账
     * @param btd
     * @param svc
     * @param commData
     * @throws Exception
     */
    public void crtSvcTrade(BusiTradeData btd,SvcTradeData svc,IData commData) throws Exception
    {
    	//视频彩铃
    	List<SvcTradeData> crtSvcs =  btd.getRD().getUca().getUserSvcBySvcId(commData.getString("PARA_CODE1"));
    	
        //如果用户存在视频彩铃业务,并且当前操作为VOLTE或彩铃业务退订时，视频彩铃业务一并退订
        if("1".equals(svc.getModifyTag()) && (null != crtSvcs && crtSvcs.size() > 0))
        {
            SvcTradeData std = crtSvcs.get(0);
            
            std.setEndDate(svc.getEndDate());
            std.setModifyTag(svc.getModifyTag());
            
            btd.add(btd.getRD().getUca().getSerialNumber(), std);
        }
    }
    
    /***
     * 检查用户是否开通了相关服务
     * @param btd
     * @param commData
     * @throws Exception
     */
    public boolean checkSvc(UcaData uca,IData commData,String para_code) throws Exception
    {
        List<SvcTradeData> svc = uca.getUserSvcBySvcId(commData.getString(para_code));
        
        return (svc != null && svc.size() > 0) ? true : false;
    }
}
