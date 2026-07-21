# Java 基础速通

> 面向初学者的 Java 核心语法与常用概念整理，可直接当笔记或博客草稿用。

---

## 1. 环境与第一个程序

- **JDK**：开发工具包（编译器 `javac` + 运行时等）
- **JRE**：运行环境（现在一般直接装 JDK 即可）
- **JVM**：Java 虚拟机，负责跨平台执行字节码

```bash
javac Hello.java   # 编译 -> Hello.class
java Hello         # 运行主类（不要加 .class）
```

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}
```

约定：

- 源文件名必须与 `public class` 类名一致
- 入口方法签名固定：`public static void main(String[] args)`

---

## 2. 基本数据类型

| 类型 | 大小 | 说明 | 默认值（成员变量） |
|------|------|------|-------------------|
| `byte` | 1 字节 | -128 ~ 127 | 0 |
| `short` | 2 字节 | 短整型 | 0 |
| `int` | 4 字节 | 常用整型 | 0 |
| `long` | 8 字节 | 长整型，字面量加 `L` | 0L |
| `float` | 4 字节 | 单精度，字面量加 `f` | 0.0f |
| `double` | 8 字节 | 双精度（默认小数） | 0.0d |
| `char` | 2 字节 | Unicode 字符 | `'\u0000'` |
| `boolean` | — | `true` / `false` | `false` |

```java
int age = 18;
long count = 1000L;
double price = 19.9;
char grade = 'A';
boolean ok = true;
```

### 类型转换

- **自动（拓宽）**：`byte -> short -> int -> long -> float -> double`
- **强制（窄化）**：可能丢精度

```java
int a = 10;
double b = a;        // 自动
int c = (int) 3.9;   // 强制 -> 3
```

---

## 3. 引用类型与 String

除 8 种基本类型外，其余都是**引用类型**（类、接口、数组、枚举等）。

```java
String name = "Java";
String s2 = new String("Java");

// 常用方法
name.length();
name.equals("Java");      // 比较内容（不要用 == 比内容）
name.equalsIgnoreCase("java");
name.contains("va");
name.substring(0, 2);     // "Ja"
name.split(",");
name.trim();
name.toUpperCase();
```

字符串不可变（immutable）。频繁拼接用 `StringBuilder`：

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello").append(" ").append("Java");
String result = sb.toString();
```

---

## 4. 运算符

```java
// 算术
+ - * / % ++ --

// 关系
== != > < >= <=

// 逻辑
&& || !          // 短路
& | ^            // 按位/非短路逻辑

// 赋值
= += -= *= /= %=

// 三元
int max = a > b ? a : b;
```

注意：

- 整数除法：`5 / 2 == 2`
- `&&` / `||` 会短路；`&` / `|` 不会

---

## 5. 流程控制

### if / switch

```java
if (score >= 90) {
    System.out.println("优秀");
} else if (score >= 60) {
    System.out.println("及格");
} else {
    System.out.println("不及格");
}

// Java 14+ switch 表达式
String level = switch (score / 10) {
    case 10, 9 -> "A";
    case 8 -> "B";
    case 7, 6 -> "C";
    default -> "D";
};
```

### 循环

```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}

int i = 0;
while (i < 5) {
    i++;
}

do {
    // 至少执行一次
} while (false);

// 增强 for
int[] nums = {1, 2, 3};
for (int n : nums) {
    System.out.println(n);
}
```

- `break`：跳出循环
- `continue`：跳过本轮
- `return`：结束方法

---

## 6. 数组

```java
int[] a = new int[5];          // 默认全 0
int[] b = {1, 2, 3};
int[] c = new int[]{4, 5, 6};

System.out.println(b.length);  // 3
System.out.println(b[0]);      // 1

// 二维
int[][] matrix = {
    {1, 2},
    {3, 4}
};
```

常用工具：

```java
import java.util.Arrays;

Arrays.sort(b);
Arrays.toString(b);
Arrays.copyOf(b, 10);
Arrays.equals(b, c);
```

---

## 7. 方法（Method）

```java
public class MathUtil {
    // 修饰符 返回类型 方法名(参数列表)
    public static int add(int x, int y) {
        return x + y;
    }

    // 可变参数
    public static int sum(int... nums) {
        int total = 0;
        for (int n : nums) total += n;
        return total;
    }

    // 重载：同名不同参数列表
    public static double add(double x, double y) {
        return x + y;
    }
}
```

- **重载（Overload）**：同一类中方法名相同、参数不同
- **重写（Override）**：子类重新实现父类/接口方法
- 基本类型传值；引用类型传的是引用副本（可改对象内容，不能改调用方指向）

---

## 8. 面向对象三要素

### 封装

```java
public class Person {
    private String name;   // 私有字段
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        if (age > 0) this.age = age;
    }
}
```

### 继承

```java
public class Animal {
    public void eat() {
        System.out.println("eating...");
    }
}

public class Dog extends Animal {
    @Override
    public void eat() {
        System.out.println("dog eating...");
    }

    public void bark() {
        System.out.println("woof");
    }
}
```

- 单继承：一个类只能 `extends` 一个父类
- 可实现多个接口：`implements A, B`

### 多态

```java
Animal a = new Dog();  // 父类引用指向子类对象
a.eat();               // 调用 Dog 的 eat（动态绑定）
// a.bark();           // 编译报错，需强转
if (a instanceof Dog d) {  // Java 16+ pattern matching
    d.bark();
}
```

---

## 9. 访问修饰符

| 修饰符 | 同类 | 同包 | 子类 | 全部 |
|--------|------|------|------|------|
| `private` | ✅ | ❌ | ❌ | ❌ |
| 默认（包私有） | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

---

## 10. static / final / this / super

```java
public class Demo {
    public static int count = 0;   // 类变量，所有实例共享
    public final int id;          // 只能赋值一次
    public static final String APP = "Hblog"; // 常量

    public Demo(int id) {
        this.id = id;             // this：当前对象
        count++;
    }

    public static void show() {
        // 静态方法不能直接访问实例成员
        System.out.println(APP);
    }
}
```

- `super`：调用父类构造 / 方法 / 字段
- 子类构造第一行默认 `super()`，或显式调用父类构造

---

## 11. 抽象类与接口

### 抽象类

```java
public abstract class Shape {
    public abstract double area();  // 抽象方法无方法体

    public void print() {
        System.out.println(area());
    }
}
```

### 接口

```java
public interface Flyable {
    void fly();                     // 默认 public abstract

    default void info() {           // 默认方法
        System.out.println("can fly");
    }

    static void tip() {             // 静态方法
        System.out.println("Flyable tip");
    }
}

public class Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("bird flying");
    }
}
```

| | 抽象类 | 接口 |
|--|--------|------|
| 字段 | 可有普通字段 | 默认 `public static final` |
| 方法 | 可有普通方法 | 抽象 / default / static / private |
| 继承 | 单继承 | 多实现 |
| 构造 | 有 | 无 |

---

## 12. 常用关键字补充

```java
// 包
package com.example.demo;
import java.util.List;

// 枚举
public enum Status {
    NEW, RUNNING, DONE
}

// record（Java 16+）：不可变数据载体
public record User(String name, int age) {}
```

---

## 13. 异常处理

异常体系：

```
Throwable
├── Error          // 严重错误，一般不捕获
└── Exception
    ├── RuntimeException   // 非受检（空指针、越界等）
    └── 其他 Exception     // 受检，必须处理或声明
```

```java
try {
    int x = Integer.parseInt("abc");
} catch (NumberFormatException e) {
    System.out.println("格式错误: " + e.getMessage());
} finally {
    // 总会执行（常用于关资源）
}

// try-with-resources（自动关闭）
try (var in = new FileInputStream("a.txt")) {
    // ...
}

// 抛出
public void check(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("age < 0");
    }
}

// 声明受检异常
public void read() throws IOException {
    // ...
}
```

自定义异常：

```java
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
```

---

## 14. 集合框架（高频）

```
Collection
├── List   有序可重复
│   ├── ArrayList
│   └── LinkedList
├── Set    不可重复
│   ├── HashSet
│   └── TreeSet
└── Queue
    └── PriorityQueue

Map（另立体系）
├── HashMap
├── LinkedHashMap
└── TreeMap
```

```java
import java.util.*;

List<String> list = new ArrayList<>();
list.add("a");
list.add("b");
list.get(0);
list.size();

Set<Integer> set = new HashSet<>();
set.add(1);
set.contains(1);

Map<String, Integer> map = new HashMap<>();
map.put("age", 18);
map.get("age");
map.getOrDefault("name", 0);

// 遍历
for (String s : list) { }

map.forEach((k, v) -> System.out.println(k + "=" + v));

// 现代写法
List.of(1, 2, 3);                 // 不可变
list.stream()
    .filter(s -> s.length() > 0)
    .map(String::toUpperCase)
    .toList();
```

选型速记：

- 随机访问多 → `ArrayList`
- 头尾插删多 → `LinkedList`
- 键值对 → `HashMap`
- 要排序 → `TreeMap` / `TreeSet`
- 要插入顺序 → `LinkedHashMap`

---

## 15. 泛型

```java
public class Box<T> {
    private T value;

    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

Box<String> box = new Box<>();
box.set("hi");

// 通配符
List<? extends Number> nums;  // 上界：只读为主
List<? super Integer> sinks;  // 下界：只写为主
```

作用：编译期类型检查，减少强转与 `ClassCastException`。

---

## 16. Lambda 与 Stream（Java 8+）

函数式接口：只有一个抽象方法（如 `Runnable`、`Comparator`、`Function`）。

```java
List<String> names = Arrays.asList("Tom", "Jerry", "Alice");

names.stream()
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase)
    .sorted()
    .forEach(System.out::println);

int sum = Arrays.stream(new int[]{1, 2, 3, 4})
    .filter(n -> n % 2 == 0)
    .sum();
```

---

## 17. IO 基础

```java
// 字符文件读写（推荐 NIO 简便 API）
import java.nio.file.*;

String text = Files.readString(Path.of("a.txt"));
Files.writeString(Path.of("b.txt"), "hello");

List<String> lines = Files.readAllLines(Path.of("a.txt"));
```

传统流：

- 字节流：`InputStream` / `OutputStream`
- 字符流：`Reader` / `Writer`
- 缓冲：`BufferedReader` / `BufferedWriter`

---

## 18. 多线程入门

```java
// 1) 继承 Thread
new Thread(() -> System.out.println("run")).start();

// 2) 实现 Runnable
Runnable task = () -> System.out.println("task");
new Thread(task).start();

// 3) 线程池（推荐）
import java.util.concurrent.*;

ExecutorService pool = Executors.newFixedThreadPool(4);
pool.submit(() -> System.out.println("in pool"));
pool.shutdown();
```

同步：

```java
public synchronized void add() { /* 同步方法 */ }

synchronized (lock) {
    // 同步代码块
}
```

常用并发工具：`synchronized`、`ReentrantLock`、`volatile`、`AtomicInteger`、`ConcurrentHashMap`。

---

## 19. 常用 API 速查

```java
// 数学
Math.max(1, 2);
Math.min(1, 2);
Math.abs(-3);
Math.random();              // [0.0, 1.0)

// 包装类
Integer.parseInt("12");
Integer.valueOf("12");
String.valueOf(12);

// 时间（推荐 java.time）
import java.time.*;

LocalDate.now();
LocalDateTime.now();
LocalDate.parse("2026-07-21");
DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
```

---

## 20. 面向对象小练习

### 题目

设计一个 `BankAccount`：

1. 私有字段：账号、余额  
2. 存款、取款（余额不足抛异常）  
3. 查询余额  

### 参考实现

```java
public class BankAccount {
    private final String accountNo;
    private double balance;

    public BankAccount(String accountNo, double balance) {
        this.accountNo = accountNo;
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("存款金额必须 > 0");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("取款金额必须 > 0");
        }
        if (amount > balance) {
            throw new IllegalStateException("余额不足");
        }
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNo() {
        return accountNo;
    }
}
```

---

## 21. 学习路径建议

1. **语法**：类型、流程、数组、方法  
2. **OOP**：封装 / 继承 / 多态 / 接口  
3. **常用库**：集合、字符串、时间、异常  
4. **进阶**：泛型、Lambda、Stream、IO  
5. **并发与 JVM**：线程池、内存模型、GC 概念  
6. **生态**：Maven/Gradle、JUnit、Spring Boot  

---

## 22. 速记清单

- 比较字符串内容用 `equals`，不用 `==`
- 浮点比较慎用 `==`，金额用 `BigDecimal`
- 集合遍历删除用迭代器或 `removeIf`
- 资源关闭优先 try-with-resources
- 能用线程池就不要手搓无限 `new Thread`
- 对外 API 做参数校验，异常信息写清楚
- 先让代码正确、清晰，再考虑极致性能

---

## 参考

- [Oracle Java 教程](https://docs.oracle.com/javase/tutorial/)
- [Java Language Specification](https://docs.oracle.com/javase/specs/)

---

*文档版本：2026-07-21 · 适用于 Java 8 ~ 21 主流语法*
