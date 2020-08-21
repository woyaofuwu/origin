select log_id,reg_date,reg_staff_id,reg_depart_id,state,rsrv_str1,rsrv_str2,rsrv_tag1,
 sum(decode(t.fee_type_code, 'T2', t.fee_money, 0)) "FEETYPE_T2",
 sum(decode(t.fee_type_code, 'T3', t.fee_money, 0)) "FEETYPE_T3",
 sum(decode(t.fee_type_code, 'T4', t.fee_money, 0)) "FEETYPE_T4",
 sum(decode(t.fee_type_code, 'T5', t.fee_money, 0)) "FEETYPE_T5"
 from tf_F_feereg t
 where 1=1
 and t.fee_type_code in('T2','T3','T4','T5')
 and t.state=:STATE
 and t.reg_staff_Id=:REG_STAFF_ID
 and t.reg_depart_Id=:REG_DEPART_ID
 and t.reg_date between to_date(:START_REG_DATE,'yyyy-mm-dd hh24:mi:ss') and to_date(:END_REG_DATE,'yyyy-mm-dd hh24:mi:ss')
 group by t.log_id,t.reg_date,t.reg_staff_id,reg_depart_id,state,rsrv_str1,rsrv_str2,rsrv_tag1
 order by t.log_id