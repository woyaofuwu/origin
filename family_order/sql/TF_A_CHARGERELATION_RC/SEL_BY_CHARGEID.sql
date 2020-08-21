SELECT to_char(operate_id1) operate_id1,to_char(operate_id2) operate_id2 
  FROM tf_a_chargerelation_rc
 WHERE operate_id1=TO_NUMBER(:OPERATE_ID1)
   AND relation_type='2'
   AND partition_id=:PARTITION_ID