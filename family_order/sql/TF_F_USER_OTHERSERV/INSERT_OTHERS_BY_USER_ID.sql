INSERT INTO tf_f_user_otherserv(partition_id,user_id,
service_mode,serial_number,process_info,rsrv_num1,rsrv_num2,
rsrv_num3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,
rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_date1,
rsrv_date2,rsrv_date3,process_tag,staff_id,depart_id,start_date,end_date,remark,inst_id)
Select MOD(TO_NUMBER(:USER_ID_A),10000),TO_NUMBER(:USER_ID_A),service_mode,serial_number,process_info,rsrv_num1,rsrv_num2,
rsrv_num3,rsrv_str1,rsrv_str2,rsrv_str3,user_id,rsrv_str5,
rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_date1,
rsrv_date2,rsrv_date3,process_tag,staff_id,depart_id,start_date,end_date,to_char(:REMARK),TO_NUMBER(F_SYS_GETSEQID('0898', 'SEQ_INST_ID')) from tf_f_user_otherserv
where partition_id=mod(to_number(:USER_ID),10000)
  and user_id=to_number(:USER_ID)
  and service_mode='FG'
  and rsrv_num1<>20
  and process_tag='0'