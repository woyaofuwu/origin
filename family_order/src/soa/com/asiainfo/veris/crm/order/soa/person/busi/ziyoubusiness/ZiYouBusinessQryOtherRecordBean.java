
package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

import java.util.Date;

public class ZiYouBusinessQryOtherRecordBean extends ZiYouBusinessQryBean
{

    public IDataset queryOtherRecord(IData input) throws Exception {

        //判断是否已经归档,如果已经归档，报错提醒，不进行后面的动作
        checkIsPigeOnHold(input);

        //判断时间是否已经超过了10分钟,如果查过10分钟就报错,不进行后面的动作
        checkQueryTime(input);

        //未到达业务查询服务输入参数构造,调用IBOSS查询下一页数据
        IDataset ibossResponse = qryNextInfo(input);

        //新接收的数据点击记录，记入NEXT_RECORD_PARAM的REPORTPAGE中
        IData nextRecordParam = input.getData("NEXT_RECORD_PARAM");
        String currentPage = input.getString("CURRENTPAGE");//当前页数
        String oldEnterTime = nextRecordParam.getData("REPORTPAGE").getString("ENTERTIME");
        nextRecordParam.getData("REPORTPAGE").put("ENTERPAGE"+oldEnterTime, currentPage);
        int newEnterTime  = Integer.parseInt(oldEnterTime)+1;
        nextRecordParam.getData("REPORTPAGE").put("ENTERTIME", String.valueOf(newEnterTime));

        // 检查是否数据全部接收完毕,如果未接收完，则继续点击下一页查询
        boolean continueOk = checkIsQueryContinue(input);

        //准备接收新的页面记录
        if(!continueOk){
            String indictSeq = input.getData("NEXT_RECORD_PARAM").getData("TEMPPARAM1").getString("INDICTSEQ");
            this.updatePlatLog(indictSeq, "1");
            nextRecordParam.getData("PIGEONHOLE").put("PIGEONHOLE", "1");
        }

        IData result = new DataMap();
        IDataset results = new DatasetList();
        result.put("OUTDATA", ibossResponse);
        result.put("NEXT_RECORD_PARAM", nextRecordParam);
        results.add(result);
        return results;
    }
    //手机查询类功能点击下一页调用的iBoss查询方法
    public IDataset qryNextInfo(IData input) throws Exception {

        String indictSeq = input.getData("NEXT_RECORD_PARAM").getData("TEMPPARAM1").getString("INDICTSEQ");
        String currentPage = input.getString("CURRENTPAGE");//当前页数
        IData nuQueryData = new DataMap();
        nuQueryData.put("KIND_ID", "BIP2C093_T2002093_0_0");
        nuQueryData.put("INDICTSEQ", indictSeq);
        nuQueryData.put("QUERYPAGENUM", currentPage);
        return this.UnfinishBussQureySerive(nuQueryData);
    }

    /**
     * 判断点击下一页的时间是否超出查询时间10分钟
     * @throws Exception
     */
    public void checkQueryTime(IData input) throws Exception {

        String orginTime = input.getData("NEXT_RECORD_PARAM").getData("TEMPPARAM1").getString("ORIGINTIME");
        Date d1 = SysDateMgr.string2Date(orginTime, SysDateMgr.PATTERN_STAND);
        String sysTime = SysDateMgr.getSysTime();
        Date d2 = SysDateMgr.string2Date(sysTime, SysDateMgr.PATTERN_STAND);

        long diff = (d2.getTime() - d1.getTime())/60000;
        if(diff>10){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "距第一次查询服务请求超过10分钟，为获取到最新数据，请重新点击查询按钮！");
        }

    }

    /**
     *判断是否已经归档,如果已经归档，报错提醒，不进行后面的动作
     *@throws Exception
     */
    public void checkIsPigeOnHold(IData input) throws Exception {
        if(null!=input&&!input.isEmpty()&&
                null!=input.getData("NEXT_RECORD_PARAM")&&!input.getData("NEXT_RECORD_PARAM").isEmpty()&&
                null!=input.getData("NEXT_RECORD_PARAM").getData("PIGEONHOLE")&&!input.getData("NEXT_RECORD_PARAM").getData("PIGEONHOLE").isEmpty()){
            String pigeOnHole = input.getData("NEXT_RECORD_PARAM").getData("PIGEONHOLE").getString("PIGEONHOLE","");//归档标识
            if("1".equals(pigeOnHole)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您已经获取了全部查询记录，如果您需要再次浏览记录，请重新点击查询按钮！");
            }
        }
    }

    /**
     * 检查是否数据已经接收完
     */
    public boolean checkIsQueryContinue(IData input){

        IData nextRecordParam = input.getData("NEXT_RECORD_PARAM");
        int MaxRecordNum = Integer.parseInt(nextRecordParam.getData("TEMPPARAM1").getString("MAXRECORDNUM"));
        int RsltPageCurrCnt = Integer.parseInt(nextRecordParam.getData("TEMPPARAM1").getString("RSLTPAGECURRCNT"));

        boolean continueOk = true;// true表示页面没有完全点完，还有页面记录没有接收到，false表示全部记录都接收完毕
        int PageNumber;
        if (MaxRecordNum % RsltPageCurrCnt == 0) {
            PageNumber = (int) Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 1;
        } else {
            PageNumber = (int) Math.ceil(MaxRecordNum / RsltPageCurrCnt) + 2;
        }
        for (int c = 1; c < PageNumber; c++) {
            for (int j = 0; j < nextRecordParam.getData("REPORTPAGE").size() - 1; j++) {
                if (c == Integer.parseInt(nextRecordParam.getData("REPORTPAGE").getString("ENTERPAGE" + j))) {
                    continueOk = false;
                }
            }
            if (continueOk == true) {
                break;
            }
            if (c == PageNumber - 1) {
                continueOk = false;
            } else {
                continueOk = true;
            }
        }

        return continueOk;

    }

}
