UPDATE tf_b_res_produce_sub
   SET receive_tag=:RECEIVE_TAG,
receive_date=decode(:RECEIVE_TAG,'1',sysdate,receive_date),finish_tag=:FINISH_TAG,
finish_produce_date=decode(:FINISH_TAG,'0',finish_produce_date,sysdate)  
 WHERE produce_sub_id=:PRODUCE_SUB_ID