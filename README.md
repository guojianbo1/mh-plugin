# mh-plugin
简单的代码生成工具，可指定Controller、Model、Service、Mapper生成代码放置的路径<br>
使用方法如下：<br>
<em>0. 引入插件后，重启idea，在idea下方工具栏中找到CodeUtil的tag</em><br>
<em>1. 连接Mysql数据库</em><br>
<em>2. 输入需要生成代码的表名(多个表用,隔开)</em><br>
<em>3. 输入各层代码所放置的项目路径和包路径</em><br>
<em>4. 点击生成代码，等待代码生成(注意生成后，idea加载文件会有点延时)</em><br>
注意事项：<br>
<em>1. 暂时只支持Mysql数据库</em><br>
<em>2. 项目路径为全路径后除包名的路径，如：E:\mystudy\mh-demo-01\src\main\java</em><br>
<em>3. 插件支持的idea版本为：19.3版本至20.3版本</em><br>
<em>4. 如果生成的文件已经存在，则不会重新生成</em><br>
