# Jot API

# JOT Api Endpoints

- Create JOT | https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots/JOT_ID
```
{
  "jot":{
     "title": "your title here"
      "note": "Your Note here",
    }
}
```
- Single GET JOT| https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots/JOT_ID
```
{
  "data": {
    "jot": {
      "note": "Cre2",
      "created_time": "2024-09-28 22:22:41:982",
      "modified_time": "2024-09-28 22:22:41:982",
      "id": "11585000000125180",
      "title": "addded title via PUT"
    }
  },
  "status": "success"
}
```

- Update JOT | https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots/JOT_ID
```
{
  "jot":{
     "title": "updated title here"
      "note": "updated Note here",
    }
}
```

- Delete JOT | https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots/JOT_ID
```
{
  "data": {
    "message": "deleted successfully"
  },
  "status": "success"
}
```

- Get BUlk JOT | https://jot-778776887.development.catalystserverless.com/server/JotServerApp/jots
```
{
  "data": {
    "paging_info": {
      "paging_info": {
        "per_page": 100,
        "page": 1,
        "has_more": false
      }
    },
    "jots": [
      {
        "created_time": "2024-09-28 22:22:41:982",
        "note": "Cre2",
        "modified_time": "2024-09-28 22:22:41:982",
        "id": "11585000000125180",
        "title": "addded title via PUT"
      }
    ]
  },
  "status": "success"
}
```