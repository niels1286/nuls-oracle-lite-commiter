# NULS轻量版预言机功能说明
## 合约说明
* 测试网合约地址： tNULSeBaN48anzp2dE6H546bEu8BFwPKQAU4dj
### 合约功能
#### 功能说明 
管理员可以存储字符串key-value对倒合约中，并可以随时更新value值。
其他合约可以通过查询接口查询单个或多个key对应的value值，同时得到value更新时间
#### 主要方法
* submit 
提交单个key-value值，key支持
* batchSubmit 
批量提交多个key-value值
* getValue
查询单个key对应的value值和提交时间
* getValues
批量查询多个key对应的value值和提交时间

## 提交程序
### 简介
程序启动后，根据配置，自动提交配置资产的价格到合约中。其中key支持资产id，如（1-1），也支持合约地址，如（tNULSeBaN48anzp2dE6H546bEu8BFwPKQAU4dj）
### 配置说明
```aidl
    //NULS链id
  "chainId": 2,
  //提交间隔时间，单位分钟
  "intervalMinite": 10,
  //oracle-lite合约地址，提交是数据存储在该合约中
  "oracleContract": "tNULSeBaN48anzp2dE6H546bEu8BFwPKQAU4dj",
  //NULS api服务的url
  "nulsApiUrl": "http://beta.api.nuls.io",
  //合约管理员权限地址对应的私钥，只有管理员权限才可以向合约提交数据
  "commiterAccoutPrikey": "055f2b3d21eea4b8902c9fd6a1885df6c4b61dadc6dba7ba3aff5f3ca2acbd8e",
  //资产管理系统api的URL，目前价格都是从资产管理系统中获取
  "assetSystemUrl": "https://beta.assets.nabox.io/api/",
  //支持提交价格的资产列表，支持资产id，如（1-1），也支持合约地址，如（tNULSeBaN48anzp2dE6H546bEu8BFwPKQAU4dj）
  "assets": [
    "2-1",
    "5-1"
  ]
```
### 扩展其他价格来源
修改以下代码
```aidl
io.nuls.oracle.commiter.task.PriceCollectTask
```
