UPDATE TF_F_USER_PLATSVC A SET A.OPER_CODE='15',A.BIZ_STATE_CODE = 'A',A.REMARK='订购叠加包自动恢复'
                            WHERE A.USER_ID = TO_NUMBER(:USER_ID)
                            AND A.PARTITION_ID = MOD(:USER_ID,10000)
                            AND A.SERVICE_ID=:SERVICE_ID
                            AND A.OPER_CODE = '14'
                            AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE