SELECT to_char(a.USER_ID) USER_ID,
       a.RES_TYPE_CODE,
       a.RES_CODE,
       a.INST_ID,
       a.UPDATE_STAFF_ID,
       a.UPDATE_DEPART_ID,
       to_char(a.ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,
       a.RSRV_STR1,
       a.RSRV_STR2,
       a.RSRV_STR3,
       a.RSRV_STR4,
       a.RSRV_STR5,
       b.ATTR_VALUE LEVEL_CODE,
       b.RSRV_STR1 PRIZE_CODE,
       b.REMARK REMARK
  FROM tf_f_user_sale_goods a, tf_f_user_attr b
 WHERE a.INST_ID = b.INST_ID(+)
   AND a.user_id = b.user_id(+)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.res_type_code = :RES_TYPE_CODE
   and a.campn_id =:CAMPN_ID
   and a.GOODS_STATE ='0'