DELETE FROM  TF_F_PO_MBMP_SUB a
 WHERE exists (select 1 from TF_B_POPRODUCT_MBMP b 
                where b.trade_id = :TRADE_ID
                   and b.PRODUCTSPECNUMBER=a.PRODUCTSPECNUMBER
                   and b.user_id = a.user_id)
   and a.start_date >= a.end_date