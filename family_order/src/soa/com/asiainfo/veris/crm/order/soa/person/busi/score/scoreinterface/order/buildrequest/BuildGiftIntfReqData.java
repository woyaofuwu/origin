
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreGiftReqData;

public class BuildGiftIntfReqData extends BaseBuilder implements IBuilder
{
    @Override
    // 积分赠送
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        // 入参校验
        IDataUtil.chkParam(param, "TRADE_SEQ");
        IDataUtil.chkParam(param, "MOBILE");
        IDataUtil.chkParam(param, "ADD_POINT");
        if("02".equals(param.getString("POINT_TYPE",""))&&"".equals(param.getString("VALIDATE_TIME",""))){
        	param.put("VALIDATE_TIME", getThreeYearLater());
		}
        ScoreGiftReqData reqData = (ScoreGiftReqData) brd;
        reqData.setTRADE_SEQ(param.getString("TRADE_SEQ",""));
        reqData.setORGID(param.getString("ORGID",""));
        reqData.setTRADE_ID(param.getString("TRADE_ID",""));
        reqData.setF_ORDER_ID(param.getString("F_ORDER_ID",""));
        reqData.setMOBILE(param.getString("MOBILE",""));
        reqData.setADD_POINT(param.getString("ADD_POINT",""));
        reqData.setACTION_TYPE(param.getString("ACTION_TYPE",""));
        reqData.setCOMMENTS(param.getString("COMMENTS",""));
        reqData.setPOINT_TYPE(param.getString("POINT_TYPE",""));
        reqData.setVALIDATE_TIME(param.getString("VALIDATE_TIME",""));
        checkBefore(brd);// 业务受理时校验
    }

    private void checkBefore(BaseReqData brd) throws Exception
    {
        ScoreGiftReqData reqData = (ScoreGiftReqData) brd;

        // 判断用户状态
        if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_411);
        }
    }
    /**
	 * 获取三年之后年份
	 * @param pd
	 * @param yearId
	 * @return
	 * @throws Exception
	 */
	public String  getThreeYearLater() throws Exception{
		SQLParser parser = new SQLParser(new DataMap());

        parser.addSQL(" select to_char(trunc( to_date (to_char(to_char(sysdate, 'yyyy') + 3),'yyyy'),'yy')-1/86400,'YYYYMMDDHH24MiSS') OUTSTR from dual ");

        IDataset out = Dao.qryByParse(parser);

        return ((IData) out.get(0)).getString("OUTSTR", "");
	}
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ScoreGiftReqData();
    }

}
