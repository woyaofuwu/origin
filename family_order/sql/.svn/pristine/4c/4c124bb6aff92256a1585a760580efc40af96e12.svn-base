insert into tf_f_user_info_break
(uu_id,cust_name,pspt_type_code,pstp_id,start_date,add_time,op_id)
values
    (SEQ_USER_INFO_BREAK.Nextval,
     :CUST_NAME,
     :PSPT_TYPE_CODE,
     :PSTP_ID,
     to_date(:START_DATE,'yyyy-mm-dd'),
     sysdate,
     :OP_ID)
     