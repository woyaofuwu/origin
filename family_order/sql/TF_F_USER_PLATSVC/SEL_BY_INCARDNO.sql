select /*+index (s, IDX_TF_F_USER_PLATSVC_STR4)*/s.serial_number, s.rsrv_str4
  from tf_f_user_platsvc s
 where s.rsrv_str4 = :RSRV_STR4
   and s.biz_state_code <> 'E'
   and sysdate between s.start_date and s.end_date