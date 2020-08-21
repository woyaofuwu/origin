update tf_f_user_purchase
set user_id = to_number( :USER_ID_B )
where user_id = to_number(:USER_ID_A)
      AND PURCHASE_MODE in ('Q1', 'Q2')
      AND process_tag='0'
      AND sysdate<end_date