# User模块踩坑记录

---

## 一、MyBatis 多参数传递规则

### 问题
`updateUser(Long id, User user)` 两个参数，XML 里 `WHERE id = #{id}` 永远取不到值。

### 原因
MyBatis 多参数时，不加 `@Param` 只能用位置参数 `#{arg0}`、`#{param1}`。写 `#{id}` 会去所有参数对象上找 `getId()` 方法：
- `Long id` 不是对象，没有 `getId()`
- `User user` 有 `getId()`，返回的是 `user.id`（为 null）

### 解决
只给需要区分的那一个参数加 `@Param`：

```java
// Mapper
int updateUser(@Param("userId") Long id, User user);
```

```xml
<!-- XML -->
WHERE id = #{userId}   <!-- 取的是 Long id 的值 -->
<if test="phone != null">phone = #{phone}</if>   <!-- User没加@Param，属性直接暴露 -->
```

### 规则总结
| Mapper 写法 | XML 取值 |
|---|---|
| `method(Long id, User user)` | `#{arg0}` / `#{arg1}` 或 `#{param1}` / `#{param2}` |
| `method(@Param("a") Long id, @Param("b") User user)` | `#{a}` / `#{b.phone}` |
| `method(@Param("userId") Long id, User user)` | `#{userId}` / `#{phone}` |

> **不加 `@Param` 的对象属性可以直接用 `#{属性名}`；加了 `@Param` 的要用 `#{Param名}`。**

---

## 二、密码安全

### 问题1：日志打印密码
```java
log.info("添加用户: {}", user);  // ❌ 密码明文出现在日志里
```

### 解决
只打印不敏感字段：
```java
log.info("添加用户: phone={}", user.getPhone());  // ✅
```

### 问题2：返回数据包含密码
```java
return Result.success(user);  // ❌ 前端拿到密码了
```

### 解决
返回前清空密码：
```java
user.setPassword(null);
return Result.success(user);  // ✅
```

---

## 三、代码执行顺序

### 问题
```java
// ❌ 错误顺序
user.setPassword(null);        // 先把密码清空了
int rows = userMapper.addUser(user);  // 数据库存的密码是 null
```

### 正确顺序
```
1. 设置默认值（role/status/memberLevel）  → 在插入前
2. userMapper.addUser(user)               → 插入数据库
3. user.setPassword(null)                 → 插入后清空，防泄露
4. return
```

---

## 四、默认值设置位置

### 问题
Controller 层设置默认值，违反了分层原则。

### 解决
默认值属于**业务逻辑**，放在 Service 层：
```java
// ✅ 在 UserServiceImpl.addUser() 中
if (user.getRole() == null) user.setRole("MEMBER");
if (user.getStatus() == null) user.setStatus("ENABLED");
if (user.getMemberLevel() == null) user.setMemberLevel("NORMAL");
```

### 分层职责
| 层 | 职责 |
|---|---|
| Controller | 接收请求、参数校验、返回响应 |
| Service | 业务逻辑（默认值、校验、组装） |
| Mapper | 数据库操作 |

---

## 五、MyBatis 动态SQL - `<set>` 标签

### 问题
手写 `SET` 时，每个 `<if>` 以逗号结尾，最后一个匹配的 `<if>` 会导致尾随逗号：
```sql
UPDATE user SET phone = ?, update_time = ?, WHERE id = ?  -- ❌ WHERE前多了逗号
```

### 解决
用 `<set>` 标签替换手写 `SET`，它会自动处理多余逗号：
```xml
UPDATE user
<set>
    <if test="phone != null">phone = #{phone},</if>
    <if test="updateTime != null">update_time = #{updateTime},</if>
</set>
WHERE id = #{userId}
```

---

## 六、批量删除 - `<foreach>` 的 `collection` 规则

### 问题
```java
// Mapper
int batchDeleteUsers(List<Long> ids);  // 没加 @Param
```
```xml
<foreach collection="ids" ...>  <!-- ❌ 找不到，报错 -->
```

### 原因
MyBatis 单参数 `List` 时，默认名是 `list`，不是参数名 `ids`。

### 解决
两种方式二选一：

```java
// 方式1：Mapper 加 @Param
int batchDeleteUsers(@Param("ids") List<Long> ids);
```

```xml
<!-- 方式2：XML 用默认名 list -->
<foreach collection="list" ...>
```

### 规则总结
| Mapper 参数类型 | XML `collection` 默认值 |
|---|---|
| `List` | `list` |
| `Set` | `collection` |
| `数组` | `array` |
| `@Param("xxx")` | `xxx` |

---

## 七、修改XML时注意标签完整性

### 问题
在 XML 中新增 `batchDeleteUsers` 的 `<update>` 标签时，不小心把 `findUserById` 的 `<select>` 开头标签删掉了，只剩：
```xml
    </update>
        SELECT id, phone...  <!-- ❌ 没有 <select> 开头 -->
    </select>                <!-- 这个 </select> 没有对应的开头 -->
```

### 解决
每次修改 XML 后，检查每个 `</update>`、`</select>` 等闭合标签是否都有对应的开头标签。改完一个 SQL 节点后，确认前后节点的标签都完整。

---

## 八、杂项

### 未使用的导入
IDE 会标灰，删掉即可，保持代码整洁。

### 防御性编程
即使 XML 没查 `password` 字段，Controller 也建议 `user.setPassword(null)`，防止将来有人加了字段导致泄露。

---

## 最终正确代码结构

```
addUser 流程：
  ① setCreateTime / setUpdateTime / setDeleted(0)
  ② 设置默认值（role/status/memberLevel）
  ③ userMapper.addUser(user)  ← 插入
  ④ user.setPassword(null)    ← 清空密码
  ⑤ return

updateUser 流程：
  ① setUpdateTime
  ② userMapper.updateUser(id, user)  ← WHERE id = #{userId}
  ③ return
```