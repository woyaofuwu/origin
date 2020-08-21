SELECT to_char(id) id,to_char(definition) definition,to_char(root) root,to_char(superprocesstoken) superprocesstoken,to_char(start_,'yyyy-mm-dd hh24:mi:ss') start_,to_char(end_,'yyyy-mm-dd hh24:mi:ss') end_,state 
  FROM tf_bpm_process_instance
 WHERE id=TO_NUMBER(:ID)