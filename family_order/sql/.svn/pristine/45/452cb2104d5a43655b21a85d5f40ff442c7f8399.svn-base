select /*+index (s, IDX_PLATSVC_INCARD_NO)*/ s.in_card_no,s.service_id
  from tf_f_user_platsvc s
 where s.in_card_no = :IN_CARD_NO   
   and s.biz_state_code <> 'E'
   and sysdate between s.start_date and s.end_date