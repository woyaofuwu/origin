SELECT /*+ index(TL_B_IBTRADE IDX_TL_B_IBTRADE_4) */ ibsysid, bipcode, activitycode, to_char(cutoffday,'yyyy-mm-dd hh24:mi:ss') cutoffday, transido, transidh, transidc, procid, origdomain, homedomain, osnduns, hsnduns, routetype, routevalue, idtype, iditemrange, to_char(reqtime,'yyyy-mm-dd hh24:mi:ss') reqtime, to_char(rsptime,'yyyy-mm-dd hh24:mi:ss') rsptime, to_char(remoteproctime,'yyyy-mm-dd hh24:mi:ss') remoteproctime, to_char(accepttime,'yyyy-mm-dd hh24:mi:ss') accepttime, to_char(finishtime,'yyyy-mm-dd hh24:mi:ss') finishtime, rsptype, rspcode, rspdesc, resultcode, resultinfo, bipstatus, recontag, ibdirtag, reversedtag, to_char(ibbfee) ibbfee, ibbfeedir, to_char(ibafee) ibafee, ibafeedir, to_char(ibsfee) ibsfee, ibsfeedir, testflag, bipver, convid, svccontver, usereparchycode, tradecitycode, tradeeparchycode, tradestaffid, tradedepartid, to_char(updatetime,'yyyy-mm-dd hh24:mi:ss') updatetime, month, remark, rsrv_str2, to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2, rsrv_str3, to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1, rsrv_str1
  FROM TL_B_IBTRADE
 WHERE 
   (:PROCID is null or PROCID=:PROCID) AND (:CUTOFFDAY is null or CUTOFFDAY=to_date(:CUTOFFDAY,'yyyymmdd'))
   AND (:ROUTEVALUE is null or ROUTEVALUE=:ROUTEVALUE) AND (:ACTIVITYCODE is null or ACTIVITYCODE=:ACTIVITYCODE) 
   AND (:BIPCODE is null or BIPCODE=:BIPCODE) 
   AND ((:IDTYPE is null or IDTYPE=:IDTYPE) AND (:IDITEMRANGE is null or IDITEMRANGE=:IDITEMRANGE))
   AND (:TRADESTAFFID is null or TRADESTAFFID=:TRADESTAFFID)
   AND REQTIME between to_date(:STARTDATE,'yyyy-mm-dd hh24:mi:ss') AND to_date(:ENDDATE,'yyyy-mm-dd hh24:mi:ss')
   AND month BETWEEN to_char(to_date(:STARTDATE,'yyyy-mm-dd hh24:mi:ss'),'mm') AND to_char(to_date(:ENDDATE,'yyyy-mm-dd hh24:mi:ss'),'mm')