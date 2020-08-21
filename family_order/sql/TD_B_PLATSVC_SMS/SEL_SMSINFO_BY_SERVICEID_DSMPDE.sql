--IS_CACHE=Y
SELECT SMS_CONTENT, ORG_DOMAIN, SMS_PROCESS_TAG
  FROM TD_B_PLATSVC_SMS
 WHERE SERVICE_ID = '90000000'
   AND ORG_DOMAIN = 'DSMP'
   AND BIZ_PRI <> '-1'
   AND OPER_CODE = :OPER_CODE
   AND SUBSTR(SMS_PROCESS_TAG, 2, 1) = '1'
   AND BIZ_PRI = (SELECT MAX(BIZ_PRI)
                    FROM TD_B_PLATSVC_SMS
                   WHERE SERVICE_ID = '90000000'
                     AND ORG_DOMAIN = 'DSMP'
                     AND BIZ_PRI <> '-1'
                     AND OPER_CODE = :OPER_CODE
                     AND SUBSTR(SMS_PROCESS_TAG, 2, 1) = '1');