update td_o_whiteuser_sms

   set white_type       = :WHITE_TYPE,

       start_time       = to_date(:START_TIME, 'yyyy-mm-dd hh24:mi:ss'),

       END_TIME         = to_date(:END_TIME, 'yyyy-mm-dd hh24:mi:ss'),

       join_reason       = :JOIN_REASON,   

       EPARCHY_CODE     = :EPARCHY_CODE, 

       remark           = :REMARK,

       update_time      = SYSDATE,

       update_staff      = :UPDATE_STAFF,

       update_depart    = :UPDATE_DEPART,

       rsrv_str1        = :RSRV_STR1,

       rsrv_str2        = :RSRV_STR2,

       rsrv_str3        = :RSRV_STR3 

 WHERE white_serinum = :WHITE_SERINUM