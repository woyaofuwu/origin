UPDATE tf_a_consigninfolog
   SET commit_tag='0'
 WHERE commit_tag='1'
   AND charge_id=TO_NUMBER(:CHARGE_ID)