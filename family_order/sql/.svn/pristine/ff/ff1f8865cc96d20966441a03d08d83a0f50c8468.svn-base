INSERT INTO tf_f_user_mbmp_sub(partition_id,user_id,sp_id,biz_type_code,org_domain,opr_source,

            sp_svc_id,start_date,end_date,biz_state_code,billflg,rsrv_num1,rsrv_num2,rsrv_num3,

            rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,

            rsrv_date2,rsrv_date3,remark,update_staff_id,update_depart_id,update_time,serial_number)

SELECT MOD(:USER_ID,10000),TO_NUMBER(:USER_ID),:SP_ID,:BIZ_TYPE_CODE,:ORG_DOMAIN,:OPR_SOURCE,

       :SP_SVC_ID,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),:BIZ_STATE_CODE,:BILLFLG,TO_NUMBER(:RSRV_NUM1),TO_NUMBER(:RSRV_NUM2),TO_NUMBER(:RSRV_NUM3),

       (SELECT /*+index(a,pk_tf_f_user_mbmp_sub)*/ COUNT(1)

          FROM tf_f_user_mbmp_sub

         WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(:USER_ID,10000)

           AND sp_id = :SP_ID

           AND biz_type_code=:BIZ_TYPE_CODE

           AND sp_svc_id=:SP_SVC_ID

           AND end_date = (select max(end_date)

                             from tf_f_user_mbmp_sub

                            where user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(:USER_ID,10000)

                              AND sp_id = :SP_ID

                              AND biz_type_code=:BIZ_TYPE_CODE

                              AND sp_svc_id=:SP_SVC_ID

                          )

           AND biz_state_code = 'N'

       ),TO_NUMBER(:RSRV_NUM4)+TO_NUMBER(:RSRV_NUM5),:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_STR4,:RSRV_STR5,TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS'),

       TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS'),:REMARK,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,SYSDATE,serial_number 

  FROM tf_f_user 

 WHERE user_id = TO_NUMBER(:USER_ID) AND partition_id=MOD(:USER_ID,10000)