select  /*+ use_nl(t t1)*/ t.rsrv_num2,t1.user_id,t1.serial_number,T.INST_ID
   from TF_F_USER_RES t,tf_f_user t1
  where t.user_id=t1.user_id
	  and t.partition_id=t1.partition_id 
    and t.rsrv_num2 is not null
    and sysdate between t.start_date and t.end_date
    and t.start_date<= add_months(sysdate, -36) 
    and to_number(t.rsrv_num2)>0
    and t.rsrv_tag1 = 'J'
    and t.res_type_code= '4'