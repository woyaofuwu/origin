SELECT staff_id,evaluate_code,evaluate_class,evaluate_notion,to_char(evaluate_date,'yyyy-mm-dd hh24:mi:ss') evaluate_date,remark 
  FROM tf_f_user_evaluate
 WHERE staff_id=:STAFF_ID   AND evaluate_date=TO_DATE(:EVALUATE_DATE, 'YYYY-MM-DD')