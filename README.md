# GNU Java Utils Library

一个轻量级 Java 工具库，提供常用的压缩/解压与终端彩色输出功能等。

## 技术栈

- **Java 25**
- **Maven** 构建
- **Apache Commons Compress 1.28.0**（压缩功能依赖）

## 项目结构

```
src/main/java/top/gtb520/java/gnu/java/utils/library/
├── main.java                          # 统一入口接口
├── utils/
│   ├── compress/
│   │   └── compress.java              # 压缩/解压工具类
│   └── print/
│       ├── echo.java                  # 终端彩色输出工具类
│       └── ColorNameEnum.java         # 颜色名称枚举
```

## 功能模块

### 1. 压缩/解压 (`compress`)

支持 **Tar.Gz** 和 **Zip** 两种压缩格式的压缩与解压操作，支持单文件和整个目录的递归处理。

| 方法                                 | 说明                  | 参数                           |
|------------------------------------|---------------------|------------------------------|
| `compress.MakeTarGz(source, dest)` | 将文件或目录压缩为 `.tar.gz` | `source` 源路径，`dest` 压缩包输出路径  |
| `compress.UnTarGz(source, dest)`   | 解压 `.tar.gz` 到指定目录  | `source` 压缩包路径，`dest` 解压目标目录 |
| `compress.MakeZip(source, dest)`   | 将文件或目录压缩为 `.zip`    | `source` 源路径，`dest` 压缩包输出路径  |
| `compress.UnZip(source, dest)`     | 解压 `.zip` 到指定目录     | `source` 压缩包路径，`dest` 解压目标目录 |

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

基于 ANSI 转义序列，在终端中输出彩色文本。

| 方法                                        | 说明               | 参数                                  |
|-------------------------------------------|------------------|-------------------------------------|
| `echo.ColorPrintln(message, color)`       | 使用预定义颜色打印        | `message` 文本内容，`color` 颜色枚举         |
| `echo.ColorHexPrintln(message, hexColor)` | 使用自定义 ANSI 颜色码打印 | `message` 文本内容，`hexColor` ANSI 颜色代码 |

**支持的颜色枚举 (`ColorNameEnum`)：**

| 枚举值      | 颜色 |
|----------|----|
| `RED`    | 红色 |
| `GREEN`  | 绿色 |
| `YELLOW` | 黄色 |
| `BLUE`   | 蓝色 |
| `PURPLE` | 紫色 |
| `CYAN`   | 青色 |

**使用示例：**

```java
import top.gtb520.java.gnu.java.utils.library.utils.print.echo;
import top.gtb520.java.gnu.java.utils.library.utils.print.ColorNameEnum;

// 使用预定义颜色
echo.ColorPrintln("错误信息", ColorNameEnum.RED);
echo.ColorPrintln("成功信息", ColorNameEnum.GREEN);

// 使用自定义 ANSI 颜色码
echo.ColorHexPrintln("自定义橙色", "38;5;208");
```

也可以通过统一入口 `main` 接口调用：

```java
import top.gtb520.java.gnu.java.utils.library.main;
import top.gtb520.java.gnu.java.utils.library.utils.print.ColorNameEnum;

main.echo.Println("警告信息", ColorNameEnum.YELLOW);
main.echo.ColorHexPrintln("自定义颜色", "31");
```

### 3. 随机生成器 (`random`)

随机生成器，提供生成随机字符串、数字、UUID 等功能。

| 方法                                                          | 说明           | 参数                                |
|-------------------------------------------------------------|--------------|-----------------------------------|
| `processing.random.defaults.random(start_scope, end_scope)` | 根据输入的范围生成随机数 | `start_scope` 起始数，`end_scope` 结束数 |
| `processing.random.defaults.randomString(int length)`       | 生成指定长度的随机字符串 | `length` 字符串长度                    |

**使用示例：**

```java
package top.gtb520.java.gnu.java.utils.library.utils.processing.random;

defaults.random(1, 100);
defaults.randomString(10);
 ```

### 4. HWID生成

HWID生成器，提供生成硬件ID的功能。

| 方法                                       | 说明     | 参数 |
|------------------------------------------|--------|----|
| `processing.hwid.getHWID.generateHWID()` | 生成HWID | 无  |

**使用示例：**

```java
import top.gtb520.java.gnu.java.utils.library.utils.processing.hwid.getHWID;

String Hwid_Str;
Hwid_Str = getHWID.generateHWID();
System.out.println("HWID: " + Hwid_Str);
System.out.println("HWID length: " + Hwid_Str.length());
 ```

### 5. 终端颜色打印Plus（ColorPrintlnPlus）

在单行内打印多种颜色的文字，包含Minecraft格式代码的文本

| 方法                                 | 说明                   | 参数                          |
|------------------------------------|----------------------|-----------------------------|
| `ColorPrintlnPlus(String Message)` | 仿照Minecraft类型的文本终端打印 | `message` 待打印的文本，包含颜色与功能控制符 |

**使用示例：**

```java
import top.gtb520.java.gnu.java.utils.library.utils.print.echo;

public class Main {
    public static void main() {
        echo.ColorPrintlnPlus("&dHello &2World&6!");
    }
}
```

## 构建

```bash
mvn compile
```
或
```bash
mvn package
```

## 使用
编译成功后，将 `target/gnu-java-utils-library-1.0.0.jar` 放入项目依赖中。</br>
或者在releases中下载最新版jar包，放入项目依赖中。

#### 使用示例：

**pom.xml**
```xml
<dependencies>
	<!-- 引入本地依赖，lib/gnu_java_utils_library-1.0-SNAPSHOT.jar -->
    <dependency>
        <groupId>top.gtb520.java.gnu</groupId>
        <artifactId>gnu_java_utils_library</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>system</scope>
		<!-- ${project.basedir} 为项目目录， /lib/ 为lib目录，xxxx.jar 依赖的Jar包-->
        <systemPath>${project.basedir}/lib/gnu_java_utils_library-1.0-SNAPSHOT.jar</systemPath>
    </dependency>
</dependencies>
```




## 异常处理

压缩/解压方法在以下情况会抛出 `IllegalArgumentException`：

- **压缩时**：源路径不存在
- **解压时**：目标路径不是有效的目录

## 许可证

Apache License 2.0

禁止商业用途
