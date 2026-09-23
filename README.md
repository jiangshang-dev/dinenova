# DineNova

> 多租户 SaaS 餐饮点餐系统 · Spring Boot 3 后端

<p align="center">
  <a href="https://github.com/jiangshang-dev/dinenova/stargazers"><img src="https://img.shields.io/github/stars/jiangshang-dev/dinenova?style=for-the-badge&logo=github" alt="Stars"/></a>
  <a href="https://github.com/jiangshang-dev/dinenova/network/members"><img src="https://img.shields.io/github/forks/jiangshang-dev/dinenova?style=for-the-badge" alt="Forks"/></a>
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/SaaS-Multi--Tenant-purple?style=for-the-badge" alt="SaaS"/>
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License"/>
</p>

<p align="center">
  <b>后端 API（本仓库）</b> ·
  <a href="https://github.com/jiangshang-dev/dinenova-views">PC 管理后台</a> ·
  <a href="https://github.com/jiangshang-dev/dinenova-uniapp">顾客端 UniApp</a> ·
  <a href="https://github.com/jiangshang-dev/dinenova-admin-uniapp">商家端 UniApp</a>
</p>

---

## ⭐ 开源约定（请先读）

本仓库遵循开源精神，代码可自由学习与二次开发。

同时请遵守一条**君子协议**：

> **请先给本仓库点一个 Star，再执行 Clone。**  
> 未 Star 禁止 Clone（开源江湖规矩，不做技术校验，全靠人品）。

点 Star 是对作者最大的鼓励，也方便你之后找回项目。  
四个仓库都 Star 一下更佳：

| 仓库 | 说明 | Star |
|------|------|------|
| [dinenova](https://github.com/jiangshang-dev/dinenova) | 后端 API（本仓库） | [![Star](https://img.shields.io/github/stars/jiangshang-dev/dinenova?style=social)](https://github.com/jiangshang-dev/dinenova) |
| [dinenova-views](https://github.com/jiangshang-dev/dinenova-views) | PC 管理后台 | [![Star](https://img.shields.io/github/stars/jiangshang-dev/dinenova-views?style=social)](https://github.com/jiangshang-dev/dinenova-views) |
| [dinenova-uniapp](https://github.com/jiangshang-dev/dinenova-uniapp) | 顾客点餐端（UniApp） | [![Star](https://img.shields.io/github/stars/jiangshang-dev/dinenova-uniapp?style=social)](https://github.com/jiangshang-dev/dinenova-uniapp) |
| [dinenova-admin-uniapp](https://github.com/jiangshang-dev/dinenova-admin-uniapp) | 商家管理端（UniApp） | [![Star](https://img.shields.io/github/stars/jiangshang-dev/dinenova-admin-uniapp?style=social)](https://github.com/jiangshang-dev/dinenova-admin-uniapp) |

```bash
# 正确姿势：浏览器点亮 Star → 再拉代码
git clone https://github.com/jiangshang-dev/dinenova.git
git clone https://github.com/jiangshang-dev/dinenova-views.git
git clone https://github.com/jiangshang-dev/dinenova-uniapp.git
git clone https://github.com/jiangshang-dev/dinenova-admin-uniapp.git
```

---

## 项目简介

**DineNova** 是一套面向餐饮行业的 **多租户 SaaS 点餐系统**：平台可接入多家商户，商户再管理门店、菜品、会员与订单。覆盖「扫码 / 小程序点餐 → 支付 → 出餐履约 → 会员营销」完整链路，适合学习 SaaS 多商户架构、二次开发与毕业设计参考。

### 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 21 · Spring Boot 3.5 · MyBatis-Plus · Redis · JWT · Hutool |
| PC 后台 | Vue 3 · Vite · Element Plus |
| 顾客端 | UniApp（微信小程序 / H5 / App） |
| 商家端 | UniApp 管理端（看板、接单、会员） |
| 存储 | 本地 / 阿里云 OSS / MinIO / RustFS（可配置） |
| 数据 | MySQL 8 |

### SaaS 能力

- **多商户隔离**：请求头 `merchantNo` + `merchantId` 维度隔离菜品、订单、会员、优惠券等数据  
- **商户 / 门店**：平台开户 → 商户配置门店、员工、桌台  
- **统一后端**：PC 后台、顾客小程序、商家小程序共用同一套 API（默认 `8082`）

### 功能概览

- **点餐交易**：菜品分类 / SKU、购物车结算、堂食 / 外卖类订单、支付与退款  
- **会员运营**：会员卡、积分、余额 / 储值、优惠券、会员分组与等级  
- **门店运营**：员工、库存、收银、桌台、数据看板与统计  
- **内容与触达**：文章、帮助、短信模板、订阅消息等  
- **权限**：后台账号与菜单权限、操作日志  

---

## 仓库结构

```
dinenova/                      # 后端（本仓库）
├── dinenova-common/           # 公共模块（core / security / redis / oss / datasource …）
├── dinenova-modules/          # 业务服务 / 仓储 / 框架
├── dinenova-web/              # 启动模块（8082）
├── sql/                       # 建库脚本（如 fuint-food.sql）
└── docs/                      # 说明文档

dinenova-views/                # PC 管理后台（Vite 端口 81）
dinenova-uniapp/               # 顾客点餐 UniApp
dinenova-admin-uniapp/         # 商家管理 UniApp
```

---

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- Node.js 18+（前端仓库）
- MySQL 8.0+
- Redis
- （可选）Elasticsearch，按 `application-dev.yml` 配置

### 1. 数据库

```bash
# 导入主库（以 sql 目录实际文件名为准）
mysql -uroot -p < sql/fuint-food.sql
# 若有「最新」脚本，按团队约定选用其一或按顺序增量执行
```

修改 `dinenova-web/src/main/resources/application-dev.yml`：

- MySQL / Redis 连接  
- OSS（`dinenova.oss`，**勿提交真实密钥到公开仓库**）  
- 微信 / 支付等相关配置（按业务需要）

### 2. 启动后端

```bash
cd dinenova
mvn -pl dinenova-web -am clean install -DskipTests
cd dinenova-web && mvn spring-boot:run
```

默认端口：`http://localhost:8082`

### 3. 启动各端前端

```bash
# PC 管理后台
cd dinenova-views && npm i && npm run dev
# → http://localhost:81

# 顾客端 / 商家端：用 HBuilderX 或 CLI 打开对应 UniApp 工程
# 修改 config.js 中的 apiUrl / baseUrl 指向 http://127.0.0.1:8082/
# 顾客端还需配置默认 merchantNo（后台商户列表获取）
```

---

## 多租户说明（简要）

1. 平台在后台创建商户，得到 `merchantNo`  
2. 顾客端 `config.js` 写入该 `merchantNo`，请求头带上商户号  
3. 服务端解析为 `merchantId`，业务查询均带商户维度，实现 SaaS 数据隔离  

---

## 文档与贡献

- Issue / PR 欢迎，请尽量附复现步骤与环境信息  
- 提交前请勿携带 `.env`、私钥、真实 OSS / 支付密钥  

---

## License

[MIT](./LICENSE) — 可商用、可修改，请保留版权声明。  
若本项目对你有帮助，请别忘了 **Star** 四连，谢谢！

<p align="center">
  <sub>Made with ☕ by <a href="https://github.com/jiangshang-dev">jiangshang-dev</a></sub>
</p>
