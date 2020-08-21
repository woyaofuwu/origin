select to_char(a.operate_id) operate_id,
       a.operate_type,
       to_char(a.user_id) user_id,
       to_char(a.acct_id) acct_id,
       a.acyc_id,
       to_char(a.all_return_fee) rsrv_str5,
       to_char(b.return_fee) all_return_fee,
       to_char(a.bill_id) bill_id,
       to_char(charge_id) charge_id,
       a.status_code,
       enrol_staff_id,
       enrol_depart_id,
       to_char(enrol_time, 'yyyy-mm-dd hh24:mi:ss') enrol_time,
       auditing_staff_id,
       auditing_depart_id,
       return_staff_id,
       return_depart_id,
       to_char(return_time, 'yyyy-mm-dd hh24:mi:ss') return_time,
       bank_code,
       bank_acct_no,
       value_card_no,
       value_card_type_code,
       value_code,
       to_char(value_card) value_card,
       a.rsrv_str1,
       a.rsrv_str2,
       a.rsrv_str3,
       a.rsrv_str4,
       a.eparchy_code,
       to_char(auditing_time, 'yyyy-mm-dd hh24:mi:ss') auditing_time,
       b.integrate_item_code
  from tf_a_returnfee a, tf_a_subreturnfee b
 where a.operate_id = b.operate_id
   and a.user_id = to_number(:user_id)
   and a.acyc_id = :acyc_id
   and a.status_code = :status_code
   and a.operate_type <> '2'