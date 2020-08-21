UPDATE TF_F_PO_MBMP_SUB a
   SET end_date=(SELECT start_date-1/24/3600 FROM TF_B_POPRODUCT_MBMP b
                  WHERE trade_id = :TRADE_ID
                    and sysdate between a.start_date and a.end_date)
 WHERE exists (select 1 from TF_B_POPRODUCT_MBMP b where
   trade_id = :TRADE_ID
   and a.user_id=b.user_id
   and a.POSPECNUMBER=b.POSPECNUMBER
   and a.PRODUCTSPECNUMBER=b.PRODUCTSPECNUMBER
   AND sysdate between a.start_date and a.end_date
   and oper_code <> '1')