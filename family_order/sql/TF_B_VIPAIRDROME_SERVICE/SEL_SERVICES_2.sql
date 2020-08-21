select a.STATE CANCEL_TAG,decode(a.STATE,'0','已受理','1','已返销') STATE,a.SERIAL_NUMBER,a.CUST_NAME,a.VIP_NO,a.AIRDROME_NAME,a.PLANE_LINE,a.FOLLOW_NUM,a.RSRV_STR2,
       decode(a.SERVICE_TYPE,'1','第一类服务(国内)','2','第二类服务(国内)','3','第一类服务(国际)','4','第二类服务(国际)') SERVICE_TYPE,a.SERVICE_CONTENT,a.OLD_SCORE_VALUE,a.CONSUME_SCORE,a.HANDING_CHARGE,a.SERVICE_CHARGE,
       a.SERVICE_STAFF,TO_CHAR(a.SERVICE_DATE,'yyyy-mm-dd hh24:mi:ss') SERVICE_DATE,a.SERVICE_ID as trade_id, c.user_id from tf_b_vipairdrome_service a, tf_f_cust_vip b, tf_f_user c
where 1=1
and a.vip_id = b.vip_id(+)
and c.serial_number = a.serial_number 
and c.remove_tag = '0' 
and a.SERVICE_DATE > c.open_date 
and ((a.serial_number= :SERIAL_NUMBER and :QUERY_TYPE = 1) OR (a.vip_no= :SERIAL_NUMBER and :QUERY_TYPE = 2))
and ((:IS_FREE = 1 and a.rsrv_str2>0) or (:IS_FREE = 0 and a.rsrv_str2=0) OR :IS_FREE IS NULL)
and ((:IS_RETURN = 1 and a.state = 1) or (:IS_RETURN = 0 and a.state=0) OR :IS_RETURN IS NULL)
and (a.service_date>=to_date(:START_DATE,'YYYY-MM-DD') OR :START_DATE IS NULL)
and (a.service_date <= to_date(:END_DATE,'YYYY-MM-DD')+ 1 OR :END_DATE IS NULL)
order by a.SERVICE_DATE desc