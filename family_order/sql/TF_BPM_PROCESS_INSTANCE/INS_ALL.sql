INSERT INTO tf_bpm_process_instance(id,definition,root,superprocesstoken,start_,state)
 VALUES(TO_NUMBER(:ID),TO_NUMBER(:DEFINITION),TO_NUMBER(:ROOT),TO_NUMBER(:SUPERPROCESSTOKEN),SYSDATE,:STATE)