# summer

http://localhost:8080/Login.html
admin 123456


## 项目后端API接口文档

### 1. 用户与认证相关

#### 登录
- **POST** `/api/login`
- 参数：`username`，`password`
- 返回：`token`

#### 注册人员
- **POST** `/api/person`
- 参数：`name`, `gender`, `id_card`, `phone`, `position`, `status`, `image`（base64）
- 返回：`personId`, `name`

#### 查询人员列表（分页）
- **GET** `/api/list`
- 参数：`page`（可选），`size`（可选）
- 返回：人员详细信息列表

#### 查询人员详细信息
- **GET** `/api/person/{id}`
- 参数：`id`（路径参数）
- 返回：单个人员详细信息

#### 检查姓名是否存在
- **GET** `/api/person/checkName?name=xxx`
- 返回：`exists`（布尔）

#### 检查身份证号是否存在
- **GET** `/api/person/checkIdCard?id_card=xxx`
- 返回：`exists`（布尔）

---

### 2. 人脸识别与数据

#### 实时识别（前端传base64图片）
- **POST** `/api/realtimeRecognizeByImage`
- 参数：`image`（base64）
- 返回：识别结果

#### 查询所有人脸数据（分页）
- **GET** `/api/face_data/list`
- 参数：`page`（可选），`size`（可选）
- 返回：人脸数据列表

#### 删除某个人的人脸数据
- **DELETE** `/api/face_data/{id}`
- 参数：`id`（路径参数，person_id）
- 返回：`code`, `msg`, `rows`

---

### 3. 设备与通行记录（如有）

> 设备和通行记录相关接口如有需要可补充。

---

### 4. 返回通用格式

```json
{
  "code": 0,
  "msg": "查询成功",
  "data": [ ... ]
}
```

---

如需详细参数、返回示例或补充其他接口，请补充说明。

---

## 如何在 APIfox 里测试各接口

### 1. 登录接口
- 方法：POST
- URL：`http://localhost:8080/api/login`
- Body（JSON）：
```json
{
  "username": "admin",
  "password": "123456"
}
```
- Headers：`Content-Type: application/json`
- 返回：
```json
{
  "code": 0,
  "token": "..."
}
```

### 2. 注册人员接口
- 方法：POST
- URL：`http://localhost:8080/api/person`
- Body（JSON）：
```json
{
  "name": "张三",
  "gender": 1,
  "id_card": "123456789012345678",
  "phone": "13800000000",
  "position": "员工",
  "status": 1,
  "image": "<图片base64字符串>"
}
```
- Headers：`Content-Type: application/json`，如需鉴权加 `Authorization: Bearer <token>`
- 返回：
```json
{
  "code": 0,
  "msg": "注册成功",
  "data": { "personId": 1234567890123456789, "name": "张三" }
}
```

### 3. 查询人员列表（分页）
- 方法：GET
- URL：`http://localhost:8080/api/list`
- Headers：如需鉴权加 `Authorization: Bearer <token>`
- 返回：
```json
{
  "code": 0,
  "msg": "查询成功",
  "data": [
    {
      "personId": 1234567890123456789,
      "name": "张三",
      "gender": 1,
      "idCard": "123456789012345678",
      "phone": "13800000000",
      "position": "员工",
      "registerTime": "2025-07-14T09:00:00",
      "updateTime": "2025-07-14T09:00:00",
      "status": 1
    }
  ]
}
```

### 4. 查询人员详细信息
- 方法：GET
- URL：`http://localhost:8080/api/person/1234567890123456789`
- Headers：如需鉴权加 `Authorization: Bearer <token>`
- 返回：
```json
{
  "code": 0,
  "msg": "查询成功",
  "data": {
    "personId": 1234567890123456789,
    "name": "张三",
    ...
  }
}
```

### 5. 检查姓名/身份证号是否存在
- 方法：GET
- URL：
  - `/api/person/checkName?name=张三`
  - `/api/person/checkIdCard?id_card=123456789012345678`
- 返回：
```json
{ "exists": true }
```

### 6. 实时识别接口
- 方法：POST
- URL：`http://localhost:8080/api/realtimeRecognizeByImage`
- Body（JSON）：
```json
{
  "image": "<图片base64字符串>"
}
```
- Headers：`Content-Type: application/json`，如需鉴权加 `Authorization: Bearer <token>`

### 7. 查询所有人脸数据（分页）
- 方法：GET
- URL：`http://localhost:8080/api/face_data/list?page=1&size=10`
- Headers：如需鉴权加 `Authorization: Bearer <token>`

### 8. 删除某个人的人脸数据
- 方法：DELETE
- URL：`http://localhost:8080/api/face_data/1234567890123456789`
- Headers：如需鉴权加 `Authorization: Bearer <token>`

http://localhost:8080/Login.html
admin 123456