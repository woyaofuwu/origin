select log_id,reg_date,reg_staff_id,reg_depart_id,state,
 sum(decode(t.fee_type_code, '101', t.fee_money, 0)) "FEETYPE_101",
 sum(decode(t.fee_type_code, '102', t.fee_money, 0)) "FEETYPE_102",
 sum(decode(t.fee_type_code, '103', t.fee_money, 0)) "FEETYPE_103",
 sum(decode(t.fee_type_code, '104', t.fee_money, 0)) "FEETYPE_104",
 sum(decode(t.fee_type_code, '105', t.fee_money, 0)) "FEETYPE_105",
 sum(decode(t.fee_type_code, '106', t.fee_money, 0)) "FEETYPE_106",
 sum(decode(t.fee_type_code, '107', t.fee_money, 0)) "FEETYPE_107",
 sum(decode(t.fee_type_code, '108', t.fee_money, 0)) "FEETYPE_108",
 sum(decode(t.fee_type_code, '109', t.fee_money, 0)) "FEETYPE_109",
 sum(decode(t.fee_type_code, '110', t.fee_money, 0)) "FEETYPE_110",
 sum(decode(t.fee_type_code, '111', t.fee_money, 0)) "FEETYPE_111",
 sum(decode(t.fee_type_code, '112', t.fee_money, 0)) "FEETYPE_112",
 sum(decode(t.fee_type_code, 'T1', t.fee_money, 0)) "FEETYPE_T1"
 from tf_F_feereg t
 where 1=1
 and t.fee_type_code not in('T2','T3','T4','T5')
 and t.state=:STATE
 and t.reg_staff_Id=:REG_STAFF_ID
 and t.reg_depart_Id=:REG_DEPART_ID
 and t.reg_date between to_date(:START_REG_DATE,'yyyy-mm-dd hh24:mi:ss') and to_date(:END_REG_DATE,'yyyy-mm-dd hh24:mi:ss')
 group by t.log_id,t.reg_date,t.reg_staff_id,reg_depart_id,state
 order by log_id