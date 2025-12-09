## proplem

####  如何修改环境变量

**临时生效:** export  名称= 值

**永久生效：**

1. **修改用户环境变量**：编辑用户目录下的`.bashrc`文件（`vi ~/.bashrc`），在文件末尾添加`export 变量名=值`（如`export PATH=$PATH:/new/path`），保存后执行`source ~/.bashrc`使修改立即生效。
2. **修改系统环境变量**：编辑`/etc/profile`文件（`sudo vi /etc/profile`），添加变量配置，保存后执行`source /etc/profile`使修改生效。

####  windows怎么传文件到linux虚拟机  

##### 一、VMware（Workstation / Player）

**方法 1：开启“共享文件夹”**

1. 虚拟机关机 → 选中虚拟机 → **Settings**
2. 选择 **Options → Shared Folders**
3. 勾选 **Always enabled**
4. 添加 Windows 文件夹
5. 启动虚拟机，Linux 里路径通常在：

```
/mnt/hgfs/你的共享文件夹名
```

**方法 2：拖拽 / 复制粘贴**

需要安装 VMware Tools
在 Linux 里执行：

```
sudo apt install open-vm-tools open-vm-tools-desktop
```

然后重启即可支持拖拽文件。

##### 二、VirtualBox

**方法 1：共享文件夹**

1. 虚拟机关机 → 设置 → **共享文件夹**
2. 添加一个 Windows 文件夹，勾选自动挂载
3. Linux 启动后访问：

```
/media/sf_共享文件夹名
```

**方法 2：拖拽 / 拖放**

需要 Guest Additions：

```
sudo apt install virtualbox-guest-utils
```

重启后可拖拽文件。

##### 三、Hyper-V

**最简单方式：增强会话模式 (Enhanced Session)**

可以直接在窗口中复制粘贴文件或设置共享驱动器。

如果没有增强会话模式，可以启用：

```
Enable-VMEnhancedSessionMode -Enable
```

##### 四、WSL（Windows Subsystem for Linux）

**直接复制即可，WSL 自动挂载 Windows 磁盘**

在 Linux 里：

```
cd /mnt/c/Users/你的用户名/Desktop
```

或者 Windows 向 WSL：
在 Windows 的文件资源管理器中输入：

```
\\wsl$\
```

##### 五、通用方法（任何 Linux 虚拟机都能用）

**方法 1：SSH + SCP**

Linux 里安装 ssh：

```
sudo apt install openssh-server
```

然后在 Windows PowerShell 传文件到虚拟机：

```
scp 文件名 用户名@虚拟机IP:/home/用户名/
```

**方法 2：共享网络 + SFTP**

使用 WinSCP / FileZilla

- 主机：虚拟机 IP
- 协议：SFTP
- 用户名：Linux 用户名

图形界面直接拖拽即可。

**方法 3：通过 U 盘 / ISO**

1. 将 U盘插上电脑
2. 在虚拟机设置 → 设置 USB 直通
3. 在 Linux 挂载 U盘：

```
sudo mount /dev/sdb1 /mnt
```

## Note

- CentOS用yum管理器，Ubuntu用apt管理器
- 软链接类似于Windows的快捷方式
- 每一个操作系统都有IP地址，还有主机名
- 系统监控top，磁盘监控iostat，网络监控sar -n DEV