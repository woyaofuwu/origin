select count(*) CNT
  from TF_F_CUST_GROUPMEMBER A, TF_F_CUST_GROUP B
 where A.REMOVE_TAG = '0'
   and A.USER_ID = :USER_ID
   and A.CUST_ID = B.CUST_ID
   and B.REMOVE_TAG = '0'
   and B.CLASS_ID in (select PARA_CODE1
                        from TD_S_COMMPARA
                       where PARAM_ATTR = '142'
                         and SUBSYS_CODE = 'CSM'
                         and PARAM_CODE = :PRODUCT_ID
                         and END_DATE > START_DATE
                         and END_DATE > sysdate)