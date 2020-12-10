#### Jmtrace编译说明

1、编译环境：ubuntu18.04、jdk-11.0.9、asm-9.0

2、project目录下包含文件：Premain.java、APPtrans.java、MyVisitor.java、MyMethodVisitor.java、mtrace.java、MANIFEST.MF、Makefile、jm.sh

3、项目执行依赖asm-9.0.jar，需要将该依赖放到project目录下，asm-9.0.jar可在https://repository.ow2.org/nexus/content/repositories/releases/org/ow2/asm/asm/下载

4、在project目录下执行make run可以生成代理jar包work.jar

5、执行bash jm.sh target.jar即可运行项目。这个target.jar即为需要进行内存访问监控的jar包的路径。