# GNU Java Utils Library

一个轻量级 Java 工具库，提供压缩/解压、终端彩色输出、文件创建与管理、文件加密/解密、随机数生成、硬件标识等功能。

> 注：此项目/仓库已同步到 Github：[https://github.com/databasemysql233-debug/gnu_java_utils_library.git](https://github.com/databasemysql233-debug/gnu_java_utils_library.git)  
> 原项目/仓库链接：[https://git.opensource-studio.info/database-mysql/gnu_java_utils_library.git](https://git.opensource-studio.info/database-mysql/gnu_java_utils_library.git)

## 技术栈

- **Java 25**
- **Maven** 构建
- **Apache Commons Compress 1.28.0**（压缩功能依赖）
- **JUnit Jupiter 5.13.0**（测试框架）

## 项目结构

```
src/main/java/top/gtb520/java/gnu/java/utils/library/
├── main.java                                    # 统一入口接口
├── version.java                                 # 项目版本信息
└── utils/
    ├── compress/
    │   └── compress.java                        # 压缩/解压工具类
    ├── file/
    │   ├── CreateFile.java                      # 文件创建/保存工具类
    │   └── EncryptionAndDecryption.java         # 文件加密/解密工具类
    ├── print/
    │   ├── echo.java                            # 终端彩色输出工具类
    │   └── ColorNameEnum.java                   # 颜色名称枚举
    └── processing/
        ├── random/
        │   └── defaults.java                    # 随机数/字符串生成器
        ├── hwid/
        │   └── getHWID.java                     # 硬件标识生成器
        └── runtime/
            ├── RunEnv.java                      # 运行环境信息
            └── JavaRunTimeEnv.java              # Java 运行时环境信息
```

## 功能模块

### 1. 压缩/解压 (`compress`)

支持 **Tar.Gz** 和 **Zip** 两种格式的压缩与解压，支持单文件和目录递归处理。

| 方法                                 | 说明                       | 参数                                  |
|------------------------------------|--------------------------|-------------------------------------|
| `compress.MakeTarGz(source, dest)` | 将文件或目录压缩为 `.tar.gz`    | `source` 源路径，`dest` 压缩包输出路径        |
| `compress.UnTarGz(source, dest)`   | 解压 `.tar.gz` 到指定目录     | `source` 压缩包路径，`dest` 解压目标目录        |
| `compress.MakeZip(source, dest)`   | 将文件或目录压缩为 `.zip`       | `source` 源路径，`dest` 压缩包输出路径        |
| `compress.UnZip(source, dest)`     | 解压 `.zip` 到指定目录         | `source` 压缩包路径，`dest` 解压目标目录        |

**使用示例：**

```java
import top.gtb520.java.gnu.java.utils.library.utils.compress.compress;

// 压缩目录为 Tar.Gz
compress.MakeTarGz("/path/to/source", "/path/to/output.tar.gz");

// 解压 Tar.Gz
compress.UnTarGz("/path/to/output.tar.gz", "/path/to/dest");

// 压缩文件为 Zip
compress.MakeZip("/path/to/file.txt", "/path/to/output.zip");

// 解压 Zip
compress.UnZip("/path/to/output.zip", "/path/to/dest");
```

也可以通过统一入口 `main` 接口调用：

```java
import top.gtb520.java.gnu.java.utils.library.main;

main.compress.UnTarGz(source, dest);
main.compress.TarGz(source, dest);
```

### 2. 终端彩色输出 (`echo`)

基于 ANSI 转义序列，在终端中输出彩色文本。提供三种打印方式：

#### 2.1 预定义颜色打印 — `ColorPrintln`

使用颜色枚举快速输出。

| 方法                                  | 说明          | 参数                             |
|-------------------------------------|-------------|--------------------------------|
| `echo.ColorPrintln(message, color)` | 使用预定义颜色打印   | `message` 文本，`color` 颜色枚举       |

**支持的颜色枚举 (`ColorNameEnum`)：**

| 枚举值      | 颜色 |
|----------|----|
| `RED`    | 红色 |
| `GREEN`  | 绿色 |
| `YELLOW` | 黄色 |
| `BLUE`   | 蓝色 |
| `PURPLE` | 紫色 |
| `CYAN`   | 青色 |

```java
import top.gtb520.java.gnu.java.utils.library.utils.print.echo;
import top.gtb520.java.gnu.java.utils.library.utils.print.ColorNameEnum;

echo.ColorPrintln("错误信息", ColorNameEnum.RED);
echo.ColorPrintln("成功信息", ColorNameEnum.GREEN);
```

#### 2.2 自定义颜色打印 — `ColorHexPrintln`

支持 `#RRGGBB`、`#RGB` 十六进制颜色及 ANSI 颜色代码。

| 方法                                        | 说明               | 参数                                  |
|-------------------------------------------|------------------|-------------------------------------|
| `echo.ColorHexPrintln(message, hexColor)` | 使用自定义颜色码打印      | `message` 文本，`hexColor` 颜色代码       |

**支持的颜色格式：**

| 格式          | 示例          | 说明                     |
|-------------|-------------|------------------------|
| `#RRGGBB`   | `#FFC0CB`   | 十六进制真彩色，自动转 ANSI 序列   |
| `#RGB`      | `#F0F`      | 十六进制简写，自动扩展后转 ANSI 序列 |
| ANSI 颜色码   | `31`          | 标准 ANSI 颜色（如 31=红色）   |
| ANSI 扩展色   | `38;5;208`  | 256 色扩展（如 208=橙色）     |

```java
// 十六进制颜色
echo.ColorHexPrintln("粉色文本", "#FFC0CB");
echo.ColorHexPrintln("简写青色", "#0FF");

// ANSI 颜色码
echo.ColorHexPrintln("红色文本", "31");
echo.ColorHexPrintln("扩展橙色", "38;5;208");
```

#### 2.3 Minecraft 风格多色打印 — `ColorPrintlnPlus`

在单行内使用 `&X` 格式码打印多种颜色和文本效果，仿照 Minecraft 格式化语法。

| 方法                                 | 说明                    | 参数                          |
|------------------------------------|-----------------------|-----------------------------|
| `echo.ColorPrintlnPlus(message)` | 解析 `&X` 格式码后打印多色文本  | `message` 含格式码的文本           |

**颜色代码：**

| 代码   | 颜色     | 代码   | 颜色     |
|------|--------|------|--------|
| `&0` | 黑色     | `&8` | 深灰色    |
| `&1` | 深蓝色    | `&9` | 浅蓝色    |
| `&2` | 深绿色    | `&a` | 浅绿色    |
| `&3` | 深青色    | `&b` | 浅青色    |
| `&4` | 深红色    | `&c` | 浅红色    |
| `&5` | 深紫色    | `&d` | 粉色     |
| `&6` | 橙色     | `&e` | 黄色     |
| `&7` | 灰色     | `&f` | 白色     |

**格式代码：**

| 代码   | 效果   | 代码   | 效果   |
|------|------|------|------|
| `&l` | 加粗   | `&k` | 乱码（随机字符替换） |
| `&m` | 删除线  | `&r` | 重置   |
| `&n` | 下划线  |      |      |
| `&o` | 斜体   |      |      |

> 格式码大小写不敏感，`&A` 与 `&a` 等效。非合法格式码（如 `&z`）将原样保留。

```java
import top.gtb520.java.gnu.java.utils.library.utils.print.echo;

// 多色文本
echo.ColorPrintlnPlus("&6Welcome &2to &4Project");

// 混合格式效果
echo.ColorPrintlnPlus("&4&l粗体红色 &n&9下划线蓝色");

// 乱码效果（每次调用生成不同随机字符）
echo.ColorPrintlnPlus("&k这是一段乱码文字");

// 重置后恢复普通文本
echo.ColorPrintlnPlus("&4红色文字 &r普通文字");
```

也可以通过统一入口 `main` 接口调用：

```java
import top.gtb520.java.gnu.java.utils.library.main;

main.echo.Println("警告信息", ColorNameEnum.YELLOW);
main.echo.ColorHexPrintln("自定义颜色", "#FF0000");
main.echo.ColorPrintlnPlus("&a多色 &c文本");
```

### 3. 文件创建与保存 (`file/CreateFile`)

支持创建普通/二进制文件，以及异步字节流文件保存。

| 方法                                                    | 说明                                    | 参数                                                          |
|-------------------------------------------------------|---------------------------------------|-------------------------------------------------------------|
| `CreateFile.NewFile(FileName, isBin, binFileType)`    | 创建空白文件，二进制文件自动写入文件头                   | `FileName` 文件名，`isBin` 是否二进制，`binFileType` 二进制类型（普通文件传 `null`） |
| `CreateFile.StreamSaveFile(file, TargePath, FileName)` | 以字节流方式异步保存文件到目标路径                    | `file` 源文件，`TargePath` 目标目录，`FileName` 保存的文件名             |

**二进制文件类型 (`BinFileType`)：**

| 枚举值   | 文件头标识           |
|-------|----------------|
| `ELF` | `7F 45 4C 46`  |
| `PE`  | `4D 5A`        |

```java
import top.gtb520.java.gnu.java.utils.library.utils.file.CreateFile;
import top.gtb520.java.gnu.java.utils.library.utils.file.CreateFile.BinFileType;
import java.io.File;
import java.nio.file.Path;

// 创建普通空白文件
File plainFile = CreateFile.NewFile("/path/to/file.txt", false, null);

// 创建 ELF 二进制文件（自动写入 ELF 文件头）
File elfFile = CreateFile.NewFile("/path/to/program", true, BinFileType.ELF);

// 创建 PE 二进制文件（自动写入 MZ 文件头）
File peFile = CreateFile.NewFile("/path/to/app.exe", true, BinFileType.PE);

// 异步字节流保存文件
boolean success = CreateFile.StreamSaveFile(sourceFile, Path.of("/path/to/dest"), "output.bin");
```

### 4. 文件加密/解密 (`file/EncryptionAndDecryption`)

支持 **AES**、**DES** 对称加密，加密后覆盖原文件，密钥单独保存到指定目录。解密时从密钥文件读取密钥，解密后覆盖还原原文件。

| 方法                                                             | 说明                       | 参数                                            |
|----------------------------------------------------------------|--------------------------|-----------------------------------------------|
| `EncryptionAndDecryption.EncryptionFile(file, type, keyPath)`  | 异步加密文件，覆盖原文件，密钥保存到 keyPath | `file` 文件，`type` 加密类型，`keyPath` 密钥目录路径     |
| `EncryptionAndDecryption.DecryptionFile(file, type, keyfile)`  | 异步解密文件，覆盖还原原文件            | `file` 加密文件，`type` 加密类型，`keyfile` 密钥文件     |

**加密类型 (`EncryptionType`)：**

| 枚举值   | 算法                  | 密钥长度    |
|-------|---------------------|---------|
| `AES` | AES/ECB/PKCS5Padding | 128 bit |
| `DES` | DES/ECB/PKCS5Padding | 56 bit  |
| `RSA` | AES（对称替代）           | 128 bit |

> 加密后密钥文件命名为 `原文件名.key`，保存在 `keyPath` 目录下。  
> 加密/解密均覆盖写回原文件，不添加额外后缀。

```java
import top.gtb520.java.gnu.java.utils.library.utils.file.EncryptionAndDecryption;
import top.gtb520.java.gnu.java.utils.library.utils.file.EncryptionAndDecryption.EncryptionType;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;

File file = new File("/path/to/data.txt");
Path keyDir = Path.of("/path/to/keys");

// 加密文件（密钥保存到 keyDir/data.txt.key）
File encrypted = EncryptionAndDecryption.EncryptionFile(file, EncryptionType.AES, keyDir);

// 解密文件（从密钥文件读取密钥）
File keyFile = keyDir.resolve("data.txt.key").toFile();
File decrypted = EncryptionAndDecryption.DecryptionFile(file, EncryptionType.AES, keyFile);
```

### 5. 随机生成器 (`random`)

| 方法                                          | 说明           | 参数                           |
|---------------------------------------------|--------------|------------------------------|
| `defaults.random(start_scope, end_scope)`   | 生成指定范围内的随机数  | `start_scope` 起始，`end_scope` 结束 |
| `defaults.randomString(length)`             | 生成指定长度的随机字符串 | `length` 字符串长度               |

```java
import top.gtb520.java.gnu.java.utils.library.utils.processing.random.defaults;

int num = defaults.random(1, 100);          // 生成 1~100 的随机整数
String str = defaults.randomString(16);     // 生成 16 位随机字符串（大小写字母+数字）
```

### 6. 硬件标识生成器 (`hwid`)

组合 CPU、主板、MAC 地址、硬盘序列号等信息，通过 SHA-256 生成唯一的硬件标识符（HWID）。支持 Windows、macOS、Linux 及 Android 平台。

| 方法                     | 说明       | 参数 |
|------------------------|----------|----|
| `getHWID.generateHWID()` | 生成硬件标识符 | 无  |

```java
import top.gtb520.java.gnu.java.utils.library.utils.processing.hwid.getHWID;

String hwid = getHWID.generateHWID();
System.out.println("HWID: " + hwid);
```

### 7. 版本信息 (`version`)

提供项目版本信息及编译时 Java/OpenJDK 版本常量。

| 方法                         | 说明             |
|----------------------------|----------------|
| `version.GetVersion()`     | 获取项目版本号        |
| `version.GetJavaVersion()` | 获取编译时 Java 版本  |

```java
import top.gtb520.java.gnu.java.utils.library.version;

version v = new version();
System.out.println("项目版本: " + v.GetVersion());
System.out.println("Java版本: " + v.GetJavaVersion());
```

也可以通过统一入口调用：

```java
import top.gtb520.java.gnu.java.utils.library.main;

main.version.getVersion();                  // 项目版本
main.version.getJavaVersion();              // Java 版本
main.version.getOpenJDKVersion();           // OpenJDK 版本
main.version.getOpenJDKRuntimeEnvironment();    // OpenJDK 运行时环境
main.version.getOpenJDK64BitServerVM();         // OpenJDK 64-Bit Server VM
```

### 8. 运行时信息 (`runtime`)

获取当前 Java 运行时环境信息，包括 JAR 路径、JVM 版本、位数等。

**运行环境 (`RunEnv`)：**

| 方法                         | 说明                                       |
|-------------------------------|------------------------------------------|
| `RunEnv.getJarPath()`         | 获取当前运行的 JAR 路径                       |
| `RunEnv.getTempDir()`         | 获取系统临时目录路径                             |
| `RunEnv.newTempDir()`         | 在系统临时目录下新建时间戳子目录，返回路径；已存在则返回 `null` |
| `RunEnv.newJarThisTempDir()`  | 在 JAR 所在目录下新建时间戳子目录，返回路径；已存在则返回 `null` |

**Java 运行时环境 (`JavaRunTimeEnv`)：**

| 方法                                     | 说明            |
|----------------------------------------|---------------|
| `JavaRunTimeEnv.JavaVersion()`         | Java 版本号      |
| `JavaRunTimeEnv.JavaRuntimeEnvironmentVersion()` | 运行时环境版本号     |
| `JavaRunTimeEnv.JavaVMVersion()`       | 虚拟机版本号        |
| `JavaRunTimeEnv.JavaBits()`            | JDK 位数（如 64）  |
| `JavaRunTimeEnv.JavaVMName()`          | 虚拟机名称         |

```java
import top.gtb520.java.gnu.java.utils.library.utils.runtime.RunEnv;
import top.gtb520.java.gnu.java.utils.library.utils.runtime.JavaRunTimeEnv;

System.out.println("JAR路径: " + RunEnv.getJarPath());
System.out.println("临时目录: " + RunEnv.getTempDir());
System.out.println("新建临时目录: " + RunEnv.newTempDir());
System.out.println("JAR目录临时子目录: " + RunEnv.newJarThisTempDir());
System.out.println("Java版本: " + JavaRunTimeEnv.JavaVersion());
System.out.println("JVM位数: " + JavaRunTimeEnv.JavaBits());
```

也可以通过统一入口调用：

```java
import top.gtb520.java.gnu.java.utils.library.main;

main.runtime.getJarPath();                          // JAR 路径
main.runtime.getJavaVersion();                      // Java 版本
main.runtime.getJavaRuntimeEnvironmentVersion();    // 运行时环境版本
main.runtime.getJavaVMVersion();                    // 虚拟机版本
main.runtime.getJavaBits();                         // JDK 位数
main.runtime.getJavaVMName();                       // 虚拟机名称

// runtime.runenv 子接口
main.runtime.runenv.getTempDir();                   // 系统临时目录
main.runtime.runenv.newTempDir();                   // 新建临时目录
main.runtime.runenv.newJarThisTempDir();            // JAR 目录下新建临时目录
main.runtime.runenv.getJarThisTempDir();            // 同 newJarThisTempDir
```

## 构建

```bash
mvn compile
```

或打包为 JAR：

```bash
mvn package
```

## 使用

编译成功后，将 `target/gnu-java-utils-library-1.6-SNAPSHOT.jar` 放入项目依赖中，或在 Releases 中下载最新版 JAR 包。

**pom.xml 引入本地依赖：**

```xml
<dependencies>
    <dependency>
        <groupId>top.gtb520.java.gnu</groupId>
        <artifactId>gnu_java_utils_library</artifactId>
        <version>1.6-SNAPSHOT</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/gnu_java_utils_library-1.6-SNAPSHOT.jar</systemPath>
    </dependency>
</dependencies>
```

## 异常处理

压缩/解压方法在以下情况会抛出 `IllegalArgumentException`：

- **压缩时**：源路径不存在
- **解压时**：目标路径不是有效的目录

随机数方法在以下情况会抛出 `IllegalArgumentException`：

- **起始值大于结束值时**

## 许可证

Apache License 2.0
