Select PARTITION_ID,
       to_char(USER_ID) USER_ID,
       SERIAL_NUMBER,
       SERIAL_NUMBER_B,
       PRODUCT_MODE,
       PRODUCT_ID,
       PRODUCT_NAME,
       PACKAGE_ID,
       PACKAGE_NAME,
       to_char(CAMPN_ID) CAMPN_ID,
       CAMPN_CODE,
       CAMPN_NAME,
       CAMPN_TYPE,
       to_char(OPER_FEE) OPER_FEE,
       to_char(FOREGIFT) FOREGIFT,
       to_char(ADVANCE_PAY) ADVANCE_PAY,
       to_char(SCORE_CHANGED) SCORE_CHANGED,
       to_char(CREDIT_VALUE) CREDIT_VALUE,
       MONTHS,
       ACTOR_PSPT_ID,
       ACTOR_PSPT_TYPE_CODE,
       ACTOR_PHONE,
       ACTOR_NAME,
       PROCESS_TAG,
       to_char(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,
       TRADE_STAFF_ID,
       to_char(RELATION_TRADE_ID) RELATION_TRADE_ID,
       to_char(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,
       CANCEL_STAFF_ID,
       CANCEL_CAUSE,
       to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       to_char(RSRV_NUM4) RSRV_NUM4,
       to_char(RSRV_NUM5) RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       to_char(nvl(rsrv_date1,start_date), 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(nvl(rsrv_date2,end_date), 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  From TF_F_USER_SALE_ACTIVE
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   And nvl(rsrv_date2,end_date) >= Sysdate
   and process_tag in( '0','4')
   AND end_date>START_DATE
   AND RELATION_TRADE_ID<>TO_NUMBER(:RELATION_TRADE_ID)
and product_id not in(SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='0'   and subsys_code = 'CSM'
      and end_date > sysdate)
    and package_id not in (SELECT para_code1 FROM  td_s_commpara WHERE (param_attr = '155' or param_attr = '158') and param_code='1'   and subsys_code = 'CSM'
      and end_date > sysdate)
order by nvl(rsrv_date2,end_date) desc