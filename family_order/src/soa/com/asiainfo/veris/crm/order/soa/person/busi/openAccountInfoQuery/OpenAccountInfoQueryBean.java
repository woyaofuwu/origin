package com.asiainfo.veris.crm.order.soa.person.busi.openAccountInfoQuery;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import org.apache.log4j.Logger;

public class OpenAccountInfoQueryBean extends CSBizBean {
    private static transient Logger logger = Logger.getLogger(OpenAccountInfoQueryBean.class);


    public static IDataset queryOpenAccountInfo(IData input ,Pagination pagination) throws Exception{

    	return Dao.qryByCodeParser("TF_F_USER_OTHER", "DIFF_AREA_TAG", input,pagination);
    	
    }
    public static IDataset qryChnlInfo(IData input) throws Exception{
        IDataset result = new DatasetList();
        String Abilityurl = "";
        IData param = new DataMap();
        param.put("PARAM_NAME", "crm.ABILITY.CHNL");
        StringBuilder getInterFaceSQL = new StringBuilder().append("SELECT T.* FROM TD_S_BIZENV T WHERE T.PARAM_NAME = :PARAM_NAME AND T.STATE= 'U' ");
        IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param, "cen");
        if (Abilityurls != null && Abilityurls.size() > 0)
        {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            logger.error("crm.ABILITY.CHNL接口地址未在TD_S_BIZENV表中配置");
            //CSAppException.appError("-1", "crm.ABILITY.CHNL接口地址未在TD_S_BIZENV表中配置");
            return result;
        }
        String apiAddress = Abilityurl;
        IData data =  new DataMap();
        data.put("chnlCode",input.getString("DEPART_CODE"));
        IData busiParams = new DataMap();
        busiParams.put("BUSIPARAMS",data);
        IData requst = new DataMap();
        requst.put("REQUEST",busiParams);

        logger.debug("调用能开参数：==46=="+requst.toString());
        IData abilityResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,requst);
        logger.info("调用结果返回==48==："+abilityResult.toString());
        String resCode=abilityResult.getString("resCode");
        IData out=abilityResult.getData("result");
        String X_RSPCODE="";
        String X_RSPDESC="";
        X_RSPCODE=out.getString("responsecode");
        X_RSPDESC=out.getString("responsemessage");
        if(!"00000".equals(resCode)){
            logger.error("调用能开参数：==56=="+requst.toString());
            logger.error("调用能开返回结果：==57=="+abilityResult.toString());
            //CSAppException.appError("-1", "调用能力开放平台出错" + abilityResult.getString("resMsg"));
            return result;
        }
        if(!"0".equals(X_RSPCODE))
        {
            logger.error("调用能开参数：=70=="+requst.toString());
            logger.error("调用能开返回结果：==71=="+abilityResult.toString());
            logger.error("调用渠道中心接口出错==72=="+X_RSPDESC);
            //CSAppException.appError("-1", "调用渠道中心接口出错==64==" + X_RSPDESC);
            return result;
        }

        logger.debug("调用能开返回结果：==77=="+abilityResult.toString());
        result = out.getData("response").getDataset("members");
        logger.debug("接口返回结果==79=="+result.toString());
        return result;
    }
}
