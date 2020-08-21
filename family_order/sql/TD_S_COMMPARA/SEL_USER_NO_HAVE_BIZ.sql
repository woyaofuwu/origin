SELECT a.param_code,a.param_name,a.para_code1,a.para_code2,a.para_code3 FROM td_s_commpara a
WHERE a.subsys_code='CSM'
    AND a.param_attr=966
    AND a.param_code='1'      
    AND a.end_date>SYSDATE
    AND NOT Exists(
        SELECT /*+ index(b PK_TF_F_USER_DISCNT) */ 1 FROM tf_f_user_discnt b
        WHERE b.discnt_code=a.para_code1
           AND b.partition_id=MOD(to_number(:USER_ID),10000)
           AND b.user_id=to_number(:USER_ID)
           AND b.end_date>SYSDATE
    )
UNION
SELECT a.param_code,a.param_name,a.para_code1,a.para_code2,a.para_code3 FROM td_s_commpara a
WHERE a.subsys_code='CSM'
    AND a.param_attr=966
    AND a.param_code='2'      
    AND a.end_date>SYSDATE
    AND NOT Exists(
        SELECT 1 FROM tf_f_user_svc b
        WHERE b.service_id=a.para_code1
           AND b.partition_id=MOD(to_number(:USER_ID),10000)
           AND b.user_id=to_number(:USER_ID)
           AND b.end_date>SYSDATE
    )
UNION
SELECT a.param_code,a.param_name,a.para_code1,a.para_code2,a.para_code3 FROM td_s_commpara a
WHERE a.subsys_code='CSM'
    AND a.param_attr=966
    AND a.param_code='3'      
    AND a.end_date>SYSDATE
    AND NOT Exists(
        SELECT 1 FROM tf_f_user_platsvc b
        WHERE b.service_id=a.para_code1
           AND b.partition_id=MOD(to_number(:USER_ID),10000)
           AND b.user_id=to_number(:USER_ID)
           AND (b.biz_state_code='A' OR b.biz_state_code='N' OR b.biz_state_code='L')
           AND b.end_date>SYSDATE
    )