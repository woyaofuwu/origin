update TF_B_TRADE_SALE_DEPOSIT D
   set D.RSRV_TAG3 = 'D'
 where D.TRADE_ID = :TRADE_ID
   and D.MODIFY_TAG in('0','1','2')
   AND (D.RSRV_TAG3<>'D' or D.RSRV_TAG3 IS NULL)