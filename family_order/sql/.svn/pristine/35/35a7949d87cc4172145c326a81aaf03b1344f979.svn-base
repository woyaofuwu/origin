SELECT A.USER_ID, A.CUST_ID, C.PAY_NAME, C.BANK_ACCT_NO, C.BANK_CODE
  FROM TF_F_USER A, TF_F_ACCOUNT C
 WHERE A.CUST_ID = C.CUST_ID
   AND A.PARTITION_ID = MOD(A.USER_ID, 10000)
   AND C.PARTITION_ID = MOD(C.ACCT_ID, 10000)
   AND C.PAY_MODE_CODE IN ('1', '3', '4', '6', '8')
   AND C.EPARCHY_CODE = :EPARCHY_CODE
   AND C.BANK_ACCT_NO = :BANK_ACCT_NO
   AND A.NET_TYPE_CODE NOT IN ('11', '12', '13', '14', '15')
   AND A.REMOVE_TAG = '0'
   and not exists (SELECT *
          FROM tf_f_user_product p
         where p.partition_id = a.partition_id
           and p.user_id = a.user_id
           and p.brand_code IN ('IP01', 'IP00')
           and p.main_tag = 1
           and sysdate between p.start_date and p.end_date)
   and exists
  (select *
          from tf_a_payrelation b
         where b.acct_id = c.acct_id
           and b.default_tag = '1'
           and b.payitem_code = '-1'
           and b.end_cycle_id >= to_char(sysdate, 'yyyymmdd'))