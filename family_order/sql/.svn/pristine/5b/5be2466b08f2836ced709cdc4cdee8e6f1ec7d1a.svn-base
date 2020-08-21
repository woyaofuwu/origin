delete FROM TF_F_PO_MBMP_SUB a
 where exists (select 1 from TF_B_POPRODUCT_MBMP b
                where b.trade_id = :TRADE_ID
                  and b.user_id = a.user_id
                  and b.PRODUCTSPECNUMBER=a.PRODUCTSPECNUMBER
                  and b.start_date = a.start_date)