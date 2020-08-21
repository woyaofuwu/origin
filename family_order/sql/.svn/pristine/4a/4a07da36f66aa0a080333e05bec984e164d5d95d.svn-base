insert into td_o_whiteuser_sms

  (white_type,

   white_serinum,

   eparchy_code,

   join_reason,

   start_time,

   end_time,

   remark,

   update_time,

   update_staff,

   update_depart,

   rsrv_str1,

   rsrv_str2,

   rsrv_str3)

values

  ('0',

   :WHITE_SERINUM,

   :EPARCHY_CODE, 

   :JOIN_REASON,

   to_date(:START_TIME, 'yyyy-mm-dd hh24:mi:ss'),

   to_date(:END_TIME, 'yyyy-mm-dd hh24:mi:ss'),  

   :REMARK,

    sysdate,

   :UPDATE_STAFF,

   :UPDATE_DEPART,

   :RSRV_STR1,

   :RSRV_STR2,

   :RSRV_STR3)