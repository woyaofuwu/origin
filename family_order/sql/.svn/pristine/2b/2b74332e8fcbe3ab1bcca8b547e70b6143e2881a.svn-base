SELECT *
  FROM (SELECT A.USER_ID PARA_CODE1,
               A.SERIAL_NUMBER PARA_CODE2,
               B.USER_ID PARA_CODE3,
               DECODE(C.IS_REAL_NAME, '1', '是') PARA_CODE4,
               B.RSRV_STR1 PARA_CODE5,
               B.RSRV_STR2 PARA_CODE6,
               DECODE(B.RSRV_STR4,
                      '10',
                      '用户开户',
                      '60',
                      '客户资料变更',
                      '100',
                      '过户',
                      '62',
                      '实名制预登记') PARA_CODE7,
               A.CUST_ID PARA_CODE8,
               C.CUST_ID PARA_CODE9,
               C.CUST_NAME PARA_CODE10,
               DECODE(A.REMOVE_TAG,
                      '0',
                      '正常',
                      '1',
                      '主动预销号',
                      '2',
                      '主动销号',
                      '3',
                      '欠费预销号',
                      '4',
                      '欠费销号',
                      '5',
                      '开户返销',
                      '6',
                      '过户注销') PARA_CODE11,
	       B.RSRV_STR9 PARA_CODE12,
	       B.RSRV_STR10 PARA_CODE13,
               B.REMARK
          FROM TF_F_USER A, TF_F_USER_OTHER B, TF_F_CUSTOMER C
         WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER
           AND A.USER_ID = B.USER_ID
           AND A.CUST_ID = C.CUST_ID
           AND A.REMOVE_TAG = 0
           AND C.REMOVE_TAG = 0
           AND B.RSRV_VALUE_CODE = 'CHRN')
 ORDER BY PARA_CODE6 DESC