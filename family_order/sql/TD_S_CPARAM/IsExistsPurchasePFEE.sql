select count(1) recordcount from tf_b_trade_other a where a.trade_id = to_number(:TRADE_ID) and
a.rsrv_str2 in(
select /*+ index(tf_f_user_other IDX_TF_F_USER_OTHER_VALUECODE) */ rsrv_str2 from tf_f_user_other where rsrv_value_code = 'PFEE')