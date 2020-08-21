UPDATE tf_a_paylog_emer
   SET cancel_tag='1',deal_tag='1',deal_time=sysdate  
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
 AND cancel_tag='0'