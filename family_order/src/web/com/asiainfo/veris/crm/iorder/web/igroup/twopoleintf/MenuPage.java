package com.asiainfo.veris.crm.iorder.web.igroup.twopoleintf;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class MenuPage extends CSBasePage
{
    private static final Logger logger = Logger.getLogger(MenuPage.class);

    public void getZBEsopAddress(IRequestCycle cycle) throws Exception
    {
        //1、获取登录员工中对应的总部ESOP工号
        String staffId = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        IDataset result = CSViewCall.call(this, "CS.StaffInfoQrySVC.queryStaffInfoForBBoss", param);
        if(IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "工号【"+staffId+"】在系统中找不到对应的总部ESOP系统工号，请确认！");
        }
        String bbossStaffId = result.first().getString("STAFF_NUMBER");
        
        //2、获取加密文件，加密员工工号和当前时间
        String path = MenuPage.class.getClassLoader().getResource("").getPath();
        String secrityKey = path + "security/secretekey898.dat";
//        String secrityKey = GlobalCfg.getProperty("interproviwork.secrityKey");
//        if(StringUtils.isBlank(secrityKey))
//        {
//            CSViewException.apperr(GrpException.CRM_GRP_713, "系统参数配置中未配置加密文件路径，请确认！");
//        }

        if(logger.isDebugEnabled())
            logger.debug("========获取到加密文件路径========"+secrityKey);

        if(!new File(secrityKey).exists())
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "系统参数配置中配置加密文件不存在，请联系管理员！");
        }
        SecrityDigest2 des = new SecrityDigest2();
        String zbUser = des.encrypt(bbossStaffId, true, secrityKey); 
        
        Date cDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeSta = sdf.format(cDate);
        String time = des.encrypt(timeSta, true, secrityKey);
        
        //3、获取两级界面请求地址，并拼接对应参数
        String pageAddr = getData().getString("PAGE_ADDR");
        String urlBegin = GlobalCfg.getProperty("interproviwork.url", "ip/ESOPBIWeb/begin.do");
        String url = urlBegin + pageAddr + "&companyNumber=898&UserName=" + zbUser + "&TimeStamp=" + time;
        if(logger.isDebugEnabled())
            logger.debug("===========转换后两级界面请求地址==========="+url);

        setTargetUrl(url);
    }
    
    public abstract void setTargetUrl(String targetUrl) throws Exception;
}
