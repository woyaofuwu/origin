INSERT INTO TI_B_REMINDTYPE(SYNC_SEQUENCE, MODIFY_TAG, TRADE_ID, PARTITION_ID, USER_ID, INST_ID, REMIND_TYPE_CODE, START_DATE, END_DATE, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK)
Select d.SYNC_SEQUENCE, d.MODIFY_TAG, d.TRADE_ID, d.PARTITION_ID, d.USER_ID, d.INST_ID, d.SERVICE_ID, d.START_DATE, d.END_DATE, d.UPDATE_TIME, d.UPDATE_STAFF_ID, d.UPDATE_DEPART_ID, d.REMARK
From TI_B_USER_SVC d
 Where SYNC_SEQUENCE = TO_NUMBER(:SYNC_SEQUENCE)
And D.SERVICE_ID In (SELECT b.PARA_CODE1 FROM TD_S_COMMPARA b WHERE b.SUBSYS_CODE='CSM' AND b.PARAM_ATTR ='555' AND b.PARAM_CODE ='REMIND')
 AND NOT EXISTS (SELECT 1 FROM TI_B_REMINDTYPE a
                         WHERE a.SYNC_SEQUENCE = TO_NUMBER(:SYNC_SEQUENCE)
                           AND d.PARTITION_ID = a.PARTITION_ID
                           AND d.SERVICE_ID =a.REMIND_TYPE_CODE
                           AND d.USER_ID = a.USER_ID
                           AND d.INST_ID = a.INST_ID
                           AND d.START_DATE = a.START_DATE)