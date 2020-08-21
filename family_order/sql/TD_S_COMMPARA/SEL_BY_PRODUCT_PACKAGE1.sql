--IS_CACHE=Y
select SUBSYS_CODE,
       PARAM_ATTR,
       PARAM_CODE,
       PARAM_NAME,
       PARA_CODE1,
       PARA_CODE2,
       PARA_CODE3,
       PARA_CODE4,
       PARA_CODE5,
       PARA_CODE6,
       PARA_CODE7,
       PARA_CODE8,
       PARA_CODE9,
       PARA_CODE10,
       PARA_CODE11,
       PARA_CODE12,
       PARA_CODE13,
       PARA_CODE14,
       PARA_CODE15,
       PARA_CODE16,
       PARA_CODE17,
       PARA_CODE18,
       PARA_CODE19,
       PARA_CODE20,
       PARA_CODE21,
       PARA_CODE22,
       PARA_CODE23,
       PARA_CODE24,
       PARA_CODE25,
       PARA_CODE26,
       PARA_CODE27,
       PARA_CODE28,
       PARA_CODE29,
       PARA_CODE30,
       START_DATE,
       END_DATE,
       EPARCHY_CODE,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       UPDATE_TIME,
       REMARK
  from td_s_commpara A
 where A.para_code1 = :PACKAGE_ID
   and A.param_attr = :PARAM_ATTR
   and A.param_code = :PRODUCT_ID
   and A.subsys_code = 'CSM'
   and (A.eparchy_code = :EPARCHY_CODE or A.eparchy_code = 'ZZZZ')
   and A.end_date > sysdate
   AND A.END_DATE > A.START_DATE