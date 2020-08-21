SELECT a.apply_no,to_char(a.apply_batch_id) apply_batch_id 
  from tf_b_resapply_main a,tf_b_testcard_use b
 WHERE a.apply_no = b.apply_no
  AND a.apply_batch_id = b.apply_batch_id
   AND a.apply_no=:APPLY_NO
  and b.card_state_code =:CARD_STATE_CODE