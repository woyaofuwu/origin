update TF_F_SELFHELPCARD_FLOW
       set state = :STATE,
       remark = :REMARK,
       update_time = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'),
       RSRV_STR1= :RSRV_STR1
  WHERE TRANS_ID=:TRANS_ID