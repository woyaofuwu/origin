UPDATE TF_F_USER_DISCNT D
   SET D.START_DATE = to_date(:START_DATE, 'yyyy-MM-dd HH24:mi:ss'),
       D.END_DATE   = ADD_MONTHS(D.END_DATE, :BET_MONTH),
       D.REMARK     = SUBSTRB(D.REMARK, 1, 80) || '更新用户购机资料' ||
                      TO_CHAR(D.START_DATE, 'yyyymmdd')
 WHERE D.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND D.USER_ID = :USER_ID
   AND EXISTS (SELECT 1
          FROM TF_F_USER_SALE_ACTIVE S, TF_F_USER_OFFER_REL R
         WHERE S.USER_ID = R.USER_ID
           AND D.INST_ID = R.REL_OFFER_INS_ID
           AND D.DISCNT_CODE = R.REL_OFFER_CODE
           AND S.PACKAGE_ID = R.OFFER_CODE
           AND S.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
           and s.product_id = :PRODUCT_ID
           and s.package_id = :PACKAGE_ID
           AND S.USER_ID = :USER_ID
           AND S.PROCESS_TAG = '0')

