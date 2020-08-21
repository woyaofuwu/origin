select count(*) recordcount
  from TF_B_TRADE a
 where a.trade_id = :TRADE_ID
   and accept_month=substr(:TRADE_ID,5,2)
   AND (
         (EXISTS (SELECT 1
                  FROM   TF_F_CUST_VIP B
                  WHERE A.USER_ID = B.USER_ID
                    AND B.VIP_CLASS_ID IN ('2', '3', '4')
                    AND B.VIP_TYPE_CODE IN ('0', '1', '2')
                    AND b.remove_tag='0'
                  )
              AND A.rsrv_str2 = '15'
         ) or A.rsrv_str2 != '15'
        )