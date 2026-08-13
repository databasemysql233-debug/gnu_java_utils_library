1.5-SNAPSHOT

此版本包含：
1. 压缩/解压 (`compress`)
2. 终端彩色输出 (`echo`)
   2.1 预定义颜色打印 — `ColorPrintln`
   2.2 自定义颜色打印 — `ColorHexPrintln`
   2.3 Minecraft 风格多色打印 — `ColorPrintlnPlus`
3. 文件创建与保存 (`file/CreateFile`)
4. 文件加密/解密 (`file/EncryptionAndDecryption`)
5. 随机生成器 (`random`)
6. 硬件标识生成器 (`hwid`)
7. 版本信息 (`version`)
8. 运行时信息 (`runtime`)
    6.1 获取运行时临时目录
    6.2 新建临时目录
    6.3 获取临时目录
    6.4 获取当前运行的JAR文件路径

在 1.4-SNAPSHOT 的基础上调整了API接口，修复了一些BUG
