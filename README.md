# ezypaytest
EzyPay Test

sample payload:

{
    "amount": "10.00",
    "type": 2,
    "dayOfWeek": 1,
    "dayOfMonth": 12, 
    "fromDate": "06-02-2018",
    "toDate": "27-02-2018"
}

type
1 = daily
2 = weekly
3 = monthly

dayOfWeek (if empty, default is day of the week of the fromDate, used only if type=2)
1 = monday
2 = tuesday
...
7 = sunday

dayOfMonth (if empty, default is end of month, used only if type=3)
