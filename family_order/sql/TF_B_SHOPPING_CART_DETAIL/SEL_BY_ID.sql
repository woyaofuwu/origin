select A.SHOPPING_CART_ID,
       A.DETAIL_ORDER_ID,
       A.TRADE_STAFF_ID,
       TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,
       A.REQUEST_DATA,
       A.DETAIL_STATE_CODE,
	   A.DETAIL_TYPE_CODE
  from TF_B_SHOPPING_CART_DETAIL A
 where A.SHOPPING_CART_ID = :SHOPPING_CART_ID
   and A.TRADE_STAFF_ID = :TRADE_STAFF_ID
   AND A.DETAIL_STATE_CODE<>'C'
 order by A.JOIN_SEQ