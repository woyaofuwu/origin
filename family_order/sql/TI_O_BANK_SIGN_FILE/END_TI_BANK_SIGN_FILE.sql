update ti_o_bank_sign_file t
   set t.end_date = sysdate
 where t.sign_id = :RSRV_STR6
   and t.user_type = :RSRV_STR12
   and t.user_value = :RSRV_STR13
   and t.mobile_type = :RSRV_STR16