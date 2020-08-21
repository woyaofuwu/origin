Select A.PACKAGE_ID,
       A.PACKAGE_NAME,
       B.GOODS_NAME,
       B.GOODS_NUM,
       B.GOODS_STATE,
       B.ACCEPT_DATE,
       B.REMARK
  FROM TF_F_USER_SALE_ACTIVE A, TF_F_USER_SALE_GOODS B
 WHERE A.USER_ID = B.USER_ID
   AND A.PACKAGE_ID = B.PACKAGE_ID
   and a.relation_trade_id=b.relation_trade_id
   AND B.USER_ID = TO_NUMBER(:USER_ID)
   AND B.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND (:BEGIN_DATE is null or
       B.ACCEPT_DATE >= to_date(:BEGIN_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (:END_DATE is null or
       B.ACCEPT_DATE <= to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (:PACKAGE_ID is null or A.PACKAGE_ID LIKE '%' || :PACKAGE_ID || '%')
   AND (:PACKAGE_NAME is null or
       A.PACKAGE_NAME LIKE '%' || :PACKAGE_NAME || '%')