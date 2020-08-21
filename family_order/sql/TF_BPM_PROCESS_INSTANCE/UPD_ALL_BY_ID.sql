UPDATE tf_bpm_process_instance
   SET definition=TO_NUMBER(:DEFINITION),root=TO_NUMBER(:ROOT),superprocesstoken=TO_NUMBER(:SUPERPROCESSTOKEN),end_=SYSDATE,state=:STATE  
 WHERE id=TO_NUMBER(:ID)