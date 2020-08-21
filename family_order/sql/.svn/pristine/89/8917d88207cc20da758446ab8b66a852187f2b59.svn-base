SELECT distinct to_char(p.USER_ID) USER_ID,p.PARTITION_ID, to_char(p.USER_ID_A) USER_ID_A, p.PRODUCT_ID, p.PRODUCT_MODE, p.BRAND_CODE,
 to_char(p.INST_ID) INST_ID, to_char(p.CAMPN_ID) CAMPN_ID, to_char(p.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, 
 to_char(p.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(p.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, p.UPDATE_STAFF_ID, p.UPDATE_DEPART_ID, p.REMARK, 
 p.RSRV_NUM1, p.RSRV_NUM2, p.RSRV_NUM3, to_char(p.RSRV_NUM4) RSRV_NUM4, to_char(p.RSRV_NUM5) RSRV_NUM5, p.RSRV_STR1, p.RSRV_STR2, p.RSRV_STR3, p.RSRV_STR4, p.RSRV_STR5,
 to_char(p.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(p.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(p.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, p.RSRV_TAG1, p.RSRV_TAG2, p.RSRV_TAG3,p.MAIN_TAG 
  from UCR_CRM1.tf_b_trade_svc s,TF_F_USER_SVC u ,TF_F_USER_PRODUCT p 
where   
		 s.trade_id=:TRADE_ID  AND s.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
		 AND u.user_id=s.user_id   AND u.PARTITION_ID = MOD(s.USER_ID, 10000) and u.service_id='860'
         AND p.user_id=u.user_id_a AND p.PARTITION_ID = MOD(u.user_id_a, 10000) AND p.MAIN_TAG='1'
     AND sysdate BETWEEN p.start_date AND p.end_date
     AND sysdate BETWEEN u.start_date AND u.end_date 