## proplem

####  如何修改环境变量

**临时生效:** export  名称= 值

**永久生效：**

1. **修改用户环境变量**：编辑用户目录下的`.bashrc`文件（`vi ~/.bashrc`），在文件末尾添加`export 变量名=值`（如`export PATH=$PATH:/new/path`），保存后执行`source ~/.bashrc`使修改立即生效。
2. **修改系统环境变量**：编辑`/etc/profile`文件（`sudo vi /etc/profile`），添加变量配置，保存后执行`source /etc/profile`使修改生效。

## Note

- CentOS用yum管理器，Ubuntu用apt管理器
- 软链接类似于Windows的快捷方式
- 每一个操作系统都有IP地址，还有主机名
- 系统监控top，磁盘监控iostat，网络监控sar -n DEV