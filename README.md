# ufrog-easy

基于 Spring Boot 的企业级 Java 开发框架，简化 Web 应用开发流程。

## 版本要求

- **Java**: 17+
- **Spring Boot**: 3.4.11

## 模块结构

```
ufrog-easy/
├── ufrog-easy-starter-parent/     # 父模块，统一依赖版本管理
└── ufrog-easy-starter-core/       # 核心模块，包含全部功能
```

## 核心特性

### 1. JPA 增强

提供完整的 Model-Repository-Service-Controller 分层架构：

| 组件 | 说明 |
|------|------|
| `EasyModel` | 基础实体模型，包含 id、创建/更新/删除审计字段 |
| `EasyRepository` | 基础仓库接口 |
| `EasyService` / `EasyServiceImpl` | 基础服务接口及实现 |
| `EasyController` | 基础控制器，提供 CRUD 接口 |

### 2. 认证授权

- `Authorize` - 认证抽象基类
- `JWTAuthorize` - JWT Token 认证实现
- `@AuthorizeIgnore` - 忽略认证注解
- `AuthorizeFilter` - 认证过滤器链

### 3. 缓存支持

| 实现 | 说明 |
|------|------|
| `EhCacheImpl` | EhCache 缓存实现 |
| `RedisImpl` | Redis 缓存实现 |
| `CacheUtil` | 缓存工具类 |

### 4. 国际化

- `I18N` - 国际化消息获取
- 支持 Header、Session 多种语言来源
- `SpringMessageSource` 集成

### 5. 请求日志

- `RequestLogAspect` - AOP 切面记录请求日志
- 支持自定义处理器 `RequestLogProcessor`

### 6. 工具类

| 工具类 | 功能 |
|--------|------|
| `StringUtil` | 字符串处理 |
| `DateTimeUtil` | 日期时间操作 |
| `CryptoUtil` | 加密解密（MD5、AES、DES、RSA） |
| `CollectionUtil` | 集合操作 |
| `ArrayUtil` | 数组操作 |
| `ObjectUtil` | 对象转换 |
| `DictUtil` | 数据字典 |
| `FileUtil` | 文件操作 |
| `LinkUtil` | 链接解析 |
| `MapUtil` | Map 构建 |
| `NumericUtil` | 数字/货币处理 |

### 7. Office 文档

- PDF 生成（基于 Flying Saucer + OpenPDF）
- Word 文档处理（基于 POI）
- FreeMarker 模板引擎

### 8. 其他功能

- **CORS 过滤器** - 跨域请求支持
- **Swagger/OpenAPI** - API 文档自动生成
- **Jasypt** - 配置加密
- **统一异常处理** - `CommonException`、`DataNotFoundException` 等

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ufrog</groupId>
    <artifactId>ufrog-easy-starter-core</artifactId>
    <version>3.4.11</version>
</dependency>
```

### 创建实体

```java
@Entity
@Table(name = "dd_user")
@SQLRestriction("dc_is_deleted = 'N'")
public class User extends EasyModel {
    
    private static final long serialVersionUID = 123456789L;
    
    @Column(name = "vc_name")
    private String name;
    
    // getter/setter
}
```

### 创建 Controller

```java
@RestController
@RequestMapping("/users")
public class UserController extends EasyController<User, UserResponse, UserRequest> {
    
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return findOne(id);
    }
    
    @PostMapping
    public UserResponse create(@RequestBody UserRequest request) {
        return create(request);
    }
    
    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        return delete(id);
    }
}
```

框架自动提供以下 API：

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /{id} | 根据 ID 查询 |
| GET | /list | 列表查询 |
| GET | /page | 分页查询 |
| POST | / | 新增 |
| PUT | /{id} | 更新 |
| DELETE | /{id} | 删除（逻辑删除） |

## 配置说明

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: password

easy:
  authorize:
    enabled: true
    ignore-uris: /api/public/**
  cache:
    type: redis
  i18n:
    default-locale: zh_CN
```

## License

Apache License 2.0
