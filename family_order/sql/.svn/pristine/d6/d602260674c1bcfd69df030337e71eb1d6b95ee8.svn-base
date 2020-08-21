UPDATE tf_b_lteb_order_pf 
   SET TRADE_ID         = :TRADE_ID,
       ACCEPT_DATE      = SYSDATE,
       STATUS           = :STATUS,
       UPDATE_TIME      = SYSDATE,
       UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID = :UPDATE_DEPART_ID,
       REMARK           = :REMARK
 WHERE USER_ID = :USER_ID
   AND OPR_NUMB = :OPR_NUMB
   AND OPR_CODE = :OPR_CODE

