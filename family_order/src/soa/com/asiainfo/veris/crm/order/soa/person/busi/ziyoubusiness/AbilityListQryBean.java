
package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AbilityListQryBean extends ZiYouBusinessQryBean {

    private static final Logger logger = LoggerFactory.getLogger(AbilityListQryBean.class);

    //能力清单查询
    public IDataset qryAbilityList(IData input) throws Exception {

        String provinceID = input.getString("PROVINCEID");
        String indictSeq = this.getIndictSequence(provinceID);// 服务请求标识 数据构造：YYYYMMDD＋CSVC＋3位省代码＋7位流水号
        String originTime = SysDateMgr.getSysTime();// 用户查询时间
        String serviceTypeId = input.getString("SERVICETYPEID");
        String kindId = "BIP2C137_T2002137_0_0";
        input.put("INDICTSEQ", indictSeq);
        input.put("SERVICETYPEID", serviceTypeId);
        input.put("KIND_ID", kindId);
        //调用iboss的接口
        IData interfaceData = IBossCall.queryBussQureySeriveIBOSS(kindId,
                indictSeq, "", provinceID, "", originTime,
                "", "", "",
                serviceTypeId, "", "", "");
        logger.debug("---------------IBOSS查询结果：" + interfaceData);

        this.insertPlatLog(indictSeq, originTime, "12312341234", serviceTypeId,"10031");// 非空字段,记录接口日志

        // 处理分割iboss返回值
        IDataset results = new DatasetList();
        IData result = new DataMap();
        String returnData = interfaceData.getString("RETURNELEMENTS");
//        String returnData = "15218738787|201904250015|咪咕高级会员|咪咕高级会员||单品点播|111|20190703111122|中音平台|3355|成功,\" +\n" +
//                "                \"15218738787|201904250014|咪咕普通会员|咪咕普通会员||单品点播|111|20190703111008|中音平台|3355|成功,\" +\n" +
//                "                \"15218738787|201904250014|12|12||单品点播|111|20190703110926|中音平台|3355|成功";
        IDataset returnElements = this.resolveReturnStringForAbility(returnData);
        result.put("ReturnElements",returnElements);
        results.add(result);
        return results;
    }


}
