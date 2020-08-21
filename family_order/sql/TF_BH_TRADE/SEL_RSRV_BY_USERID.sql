SELECT /*+index(a,IDX_TF_BH_TRADE_USERID)*/ a.rsrv_str1,
       a.rsrv_str2,
       a.rsrv_str3,
       a.rsrv_str4,
       a.rsrv_str5,
       a.rsrv_str6,
       a.rsrv_str7,
       a.rsrv_str8,
       a.rsrv_str9,
       a.rsrv_str10
  FROM tf_bh_trade a
 WHERE a.trade_type_code = TO_NUMBER(:TRADE_TYPE_CODE)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.accept_date > TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd HH24:MI:SS')
   AND a.cancel_tag = :CANCEL_TAG