1.7-SNAPSHOT

此版本包含：
1. 压缩/解压 (`compress`)
   1.1 Tar.Gz / Zip 压缩与解压
   1.2 PNG 图片压缩
2. 终端彩色输出 (`echo`)
   2.1 预定义颜色打印 — `ColorPrintln`
   2.2 自定义颜色打印 — `ColorHexPrintln`
   2.3 Minecraft 风格多色打印 — `ColorPrintlnPlus`（格式码支持叠加）
3. 文件创建与保存 (`file/CreateFile`)
4. 文件加密/解密 (`file/EncryptionAndDecryption`)
   4.1 AES-256/GCM 加密
   4.2 RSA-2048/OAEP 混合加密
   4.3 DES 加密（已废弃，仅兼容旧版本）
5. 随机生成器 (`random`)
6. 硬件标识生成器 (`hwid`)
7. 版本信息 (`version`)
8. 运行时信息 (`runtime`)
   8.1 获取运行时临时目录
   8.2 新建临时目录
   8.3 获取当前运行的 JAR 文件路径

1.7 更新内容：
- 安全：修复 Zip-Slip 路径穿越漏洞，解压时校验条目路径并拒绝符号/硬链接条目
- 安全：AES 升级为 AES-256/GCM（随机 IV + 认证标签），RSA 实现真实混合加密
  （RSA-2048/OAEP 包裹随机 AES-256 会话密钥），密钥文件限制为仅属主可读写
- 健壮性：加解密改为流式处理（支持大文件），临时文件 + 原子替换，中途失败不损坏原数据
- 修复：随机数上界 Integer.MAX_VALUE 溢出；ColorPrintlnPlus 格式码叠加（颜色+加粗等）
- 修复：getHWID 命令执行增加超时并并发排空输出流，Windows 改用 PowerShell CIM（wmic 回退），
  修正 Linux 磁盘序列号命令，MAC 优先物理网卡
- 修复：getJarPath 经 URI 解析（正确处理空格与 URL 编码），临时目录改用唯一命名
- 工程：测试全部迁移至 JUnit 5（53 个用例），pom 补充元数据与插件版本，
  版本号统一由 pom / MANIFEST Implementation-Version 提供

在 1.6-SNAPSHOT 的基础上进行安全加固与缺陷修复。
