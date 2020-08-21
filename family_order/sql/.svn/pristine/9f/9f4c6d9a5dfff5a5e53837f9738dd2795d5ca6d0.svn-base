SELECT to_char(print_id) print_id,partition_id
  FROM tf_a_noteprintlog
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   and partition_id>=:START_PARTITION_ID
   AND partition_id<=:END_PARTITION_ID
   and cancel_tag='0'
union 
SELECT to_char(print_id) print_id,partition_id
  FROM tf_a_noteprintlog
 WHERE link_charge_id=TO_NUMBER(:CHARGE_ID)
   and partition_id>=:START_PARTITION_ID
   AND partition_id<=:END_PARTITION_ID
   and cancel_tag='0'