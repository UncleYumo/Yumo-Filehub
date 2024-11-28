为了帮助您将个人项目 Yumo-Filehub 部署到云服务器，并通过 Nginx 作为反向代理来提供服务，同时使用 Certbot 来获取 HTTPS 证书，我会提供一份详细的部署文档。这份文档将涵盖从准备环境、配置 Nginx、启动应用、获取并配置 SSL 证书到安全加固的全过程。

### 1. 准备工作

#### 1.1 确认 Java 环境
确保您的云服务器上已经安装了 JDK。可以通过以下命令检查：
```bash
java -version
```
如果未安装，请根据您的操作系统选择合适的包管理器进行安装。

#### 1.2 安装 Nginx
在 Ubuntu/Debian 系统上，可以使用如下命令安装 Nginx：
```bash
sudo apt update
sudo apt install nginx
```

#### 1.3 启动 Nginx 并设置开机自启
```bash
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### 1.4 检查 Nginx 是否正常运行
访问 `http://<your_server_ip>` 应该能看到 Nginx 的默认欢迎页面。

### 2. 配置 Nginx

#### 2.1 创建 Nginx 配置文件
创建一个新的 Nginx 配置文件 `/etc/nginx/sites-available/yumo-filehub`，内容如下：
```nginx
server {
    listen 80;
    server_name filehub.uncleyumo.cn;

    location / {
        proxy_pass http://localhost:49153;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### 2.2 启用配置
创建一个符号链接以启用新配置：
```bash
sudo ln -s /etc/nginx/sites-available/yumo-filehub /etc/nginx/sites-enabled/
```

#### 2.3 测试 Nginx 配置
```bash
sudo nginx -t
```

#### 2.4 重新加载 Nginx
```bash
sudo systemctl reload nginx
```

### 3. 部署应用程序

#### 3.1 将 JAR 包复制到服务器
使用 SCP 或其他方法将您的 JAR 文件上传至服务器上的指定目录 `/opt/yumo-filehub`。

#### 3.2 运行应用程序
您可以直接运行 JAR 文件，或者使用 nohup 和 & 来后台运行：
```bash
nohup java -jar /opt/yumo-filehub/your-app.jar &
```

### 4. 获取并配置 SSL 证书

#### 4.1 安装 Certbot
```bash
sudo apt install certbot python3-certbot-nginx
```

#### 4.2 获取证书
```bash
sudo certbot --nginx -d filehub.uncleyumo.cn
```
按照提示完成证书申请过程。

#### 4.3 自动更新证书
Certbot 会自动配置定时任务来更新证书。您也可以手动测试更新：
```bash
sudo certbot renew --dry-run
```

### 5. 安全加固与维护

- **防火墙配置**：确保只有必要的端口（如 80, 443）对外开放。
- **日志监控**：定期检查 Nginx 和应用程序的日志文件。
- **备份策略**：定期备份重要数据和配置文件。

以上步骤应该能够帮您成功部署您的 Web 应用程序。如果有任何问题或需要进一步的帮助，请随时联系我！