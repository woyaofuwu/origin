SELECT to_char(bill_id) bill_id,to_char(id) id,id_type,partition_id,effect_item_code,act_mode,effect_mode,addup_mode,effect_value_type,to_char(effect_value) effect_value,to_char(refer_fee) refer_fee,start_acyc_id,end_acyc_id,to_char(remainfee) remainfee,to_char(use_fee1) use_fee1,to_char(use_fee2) use_fee2,to_char(limit_fee) limit_fee,recv_tag,adjust_reason_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,adjust_depart_id,adjust_staff_id,adjust_eparchy_code 
  FROM tf_a_adjustbbill
 WHERE id=TO_NUMBER(:ID)
   AND id_type =:ID_TYPE
   AND partition_id=:PARTITION_ID
   AND (start_acyc_id >= :START_ACYC_ID) 
   AND (end_acyc_id <= :END_ACYC_ID)