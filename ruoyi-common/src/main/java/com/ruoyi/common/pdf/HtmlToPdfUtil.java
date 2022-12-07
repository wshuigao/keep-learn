package com.ruoyi.common.pdf;

import com.alibaba.fastjson2.JSON;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * wkhtmltopdf 转换工具类
 */
public class HtmlToPdfUtil {

  private static final Logger log = LoggerFactory.getLogger(HtmlToPdfUtil.class);

  /**
   * wkhtmltopdf在系统中的路径
   */
  private static String toPdfTool = WkhtmltopdfConsts.CONVERSION_PLUGSTOOL_PATH_WINDOW;

  private static final int BUFFER_SIZE = 2 * 1024;


  /**
   * todo 填充 html 模板
   *  @param  data 要填充的元数控
   *  @param  htmlTemplate 要使用的模板名称
   *  @param  templatePath 要使用的模板所在位置
   *  @param  htmFolderPath 临时html存放的位置
   *  @param  htmName 临时html名称
   *
   * @return 填充好的本地html地址
   */
  public static String saveHtml(Map<String, Object> data, String htmlTemplate, String templatePath,
      String htmFolderPath, String htmName)
      throws IOException, TemplateException {
    // 获取模板,并设置编码方式
    Configuration freemarkerCfg = new Configuration(
        Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    //文件夹目录位置
    freemarkerCfg.setDirectoryForTemplateLoading(new File(templatePath));
    Template template = freemarkerCfg.getTemplate(htmlTemplate, "UTF-8");
    StringWriter out = new StringWriter();
    // 合并模板跟数据
    template.process(data, out);
    // htmlData 模板字符流
    String htmlData = out.toString();

    return writeToFile(htmFolderPath, htmName, htmlData);
  }


  /**
   * 创建文件
   *
   * @param folderPath 要存放的文件夹路径
   * @param fileName   要创建的文件名称
   * @param content    要填充的内容
   * @return 创建成功的文件地址
   */
  public static String writeToFile(String folderPath, String fileName, String content) {

    File dirFile = null;
    try {
      dirFile = new File(folderPath);
      if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
        if (dirFile.mkdirs()) {
          log.info("========创建文件夹成功========");
        } else {
          log.info("========创建文件夹失败========");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    String fullPath = dirFile + "/" + fileName + ".html";

    createTemporaryHtml(fullPath, content);
    return fullPath;
  }

  /**
   * 创建 临时 Html文件
   *
   * @param filePath    文件路径
   * @param fileContent 文件内容
   */
  public static void createTemporaryHtml(String filePath, String fileContent) {
    String s;
    StringBuilder s1 = new StringBuilder();
    BufferedWriter output = null;
    try {
      File f = new File(filePath);
      if (f.exists()) {
      } else {
        if (f.createNewFile()) {
          log.info("========Temporary Html Create Success========");
        } else {
          log.info("========Temporary Html Create Fail========");
        }
      }
      BufferedReader input = new BufferedReader(new FileReader(f));
      while ((s = input.readLine()) != null) {
        s1.append(s).append("\n");
      }
      input.close();
      s1.append(fileContent);
      output = new BufferedWriter(new FileWriter(f));
      output.write(s1.toString());
      output.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


  /**
   * todo html转pdf
   *
   * @param srcPath  html路径，可以是硬盘上的路径，也可以是网络路径
   * @param destPath pdf保存路径
   * @return 转换成功返回true
   */
  public static boolean convert(String srcPath, String destPath) {

    log.info("will convert htmlPath={} ------> pdfPath={}", srcPath, destPath);
    File file = new File(destPath);
    File parent = file.getParentFile();
    // 如果pdf保存路径不存在，则创建路径
    if (!parent.exists()) {
      parent.mkdirs();
    }
    StringBuilder cmd = new StringBuilder();
    String property = System.getProperty("os.name");
    log.info("当前运行系统：{}", property);
    if (!property.contains("Windows")) {
      // 非windows 系统
      toPdfTool = WkhtmltopdfConsts.CONVERSION_PLUGSTOOL_PATH_LINUX;
    }
    cmd.append(toPdfTool);
    cmd.append(" ");
    cmd.append(" \"");
    cmd.append(srcPath);
    cmd.append("\" ");
    cmd.append(" ");
    cmd.append(destPath);

    log.info("wkhtmltopdf final cmd：[{}]", cmd);

    boolean result = true;
    try {
      //            打开系统命令
      Process proc = Runtime.getRuntime().exec(cmd.toString());
      HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
      HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(proc.getInputStream());
      error.start();
      output.start();
      proc.waitFor();
    } catch (Exception e) {
      result = false;
      e.printStackTrace();
    }

    return result;
  }


  /**
   * todo 多个文件打包成zip
   *
   * @param srcDir           要压缩的文件夹路径,如:D:\pdf\2022\11\29
   * @param out              要压缩到哪里,叫什么名字,如:D:/pdf/test.zip
   * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
   * @throws RuntimeException 压缩失败会抛出运行时异常
   */
  public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
      throws RuntimeException {
    log.info("开始压缩------------------>");
    long start = System.currentTimeMillis();
    ZipOutputStream zos = null;
    try {
      zos = new ZipOutputStream(out);
      File sourceFile = new File(srcDir);
      compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
      long end = System.currentTimeMillis();
      log.info("压缩完成，耗时：{} ms", end - start);
    } catch (Exception e) {
      throw new RuntimeException(" zip error ", e);
    } finally {
      if (zos != null) {
        try {
          zos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * 递归压缩方法
   *
   * @param sourceFile       源文件
   * @param zos              zip输出流
   * @param name             压缩后的名称
   * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
   *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
   * @throws Exception
   */
  private static void compress(File sourceFile, ZipOutputStream zos, String name,
      boolean KeepDirStructure) throws Exception {
    byte[] buf = new byte[BUFFER_SIZE];
    if (sourceFile.isFile()) {
      // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
      zos.putNextEntry(new ZipEntry(name));
      // copy文件到zip输出流中
      int len;
      FileInputStream in = new FileInputStream(sourceFile);
      while ((len = in.read(buf)) != -1) {
        zos.write(buf, 0, len);
      }
      // Complete the entry
      zos.closeEntry();
      in.close();
    } else {
      File[] listFiles = sourceFile.listFiles();
      if (listFiles == null || listFiles.length == 0) {
        // 需要保留原来的文件结构时,需要对空文件夹进行处理
        if (KeepDirStructure) {
          // 空文件夹的处理
          zos.putNextEntry(new ZipEntry(name + "/"));
          // 没有文件，不需要文件的copy
          zos.closeEntry();
        }

      } else {
        for (File file : listFiles) {
          // 判断是否需要保留原来的文件结构
          if (KeepDirStructure) {
            // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
            // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
            compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
          } else {
            compress(file, zos, file.getName(), KeepDirStructure);
          }

        }
      }
    }
  }


  public static void main(String[] args) throws TemplateException, IOException {
    long startTime = System.currentTimeMillis();
    log.info("generate pdf start ... ");

    // 初始化一个文件夹路径
    Calendar calendar = Calendar.getInstance();//得到日历
    calendar.setTime(new Date());   //设置当前日期
    String yearStr = calendar.get(Calendar.YEAR) + "";//获取当前年
    int month = calendar.get(Calendar.MONTH) + 1;//获取月份
    int day = calendar.get(Calendar.DATE);//获取天

    // 文件最终存放的文件夹路径
    String folderPath = "D:\\pdf\\" + yearStr + "\\" + month + "\\" + day;

    //////////////////////////////////以下模拟数据取请求\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //1.拿取要加载的数据
    Map<String, Object> echartData = new HashMap<>();
    echartData.put("flagVal", 1);// 这个是标识,用于传入html中转换数据格式
    echartData.put("name", "wh");
    echartData.put("text", "Echart 折线图");
    List<String> xAxisList = new ArrayList<>();
    xAxisList.add("衬衫");
    xAxisList.add("羊毛衫");
    xAxisList.add("雪纺衫");
    xAxisList.add("裤子");
    xAxisList.add("高跟鞋");
    xAxisList.add("袜子");
    echartData.put("xAxisData", JSON.toJSONString(xAxisList));

    List<Object> seriesList = new ArrayList<>();
    Map<String, Object> seriesMap = new HashMap<>();
    seriesMap.put("name", "销量");
    seriesMap.put("type", "bar");
    int[] arr = {5, 20, 36, 10, 10, 20};
    seriesMap.put("data", arr);
    seriesList.add(seriesMap);
    echartData.put("series", JSON.toJSONString(seriesList));
    //////////////////////////////////以上模拟数据取请求\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //2.填充html
    // 要使用的html模板
    String htmlTemplate = "test_template.html";
    // 要使用的html模板存放位置
    String templatePath = "D:\\Code\\owntest\\keep-learn\\ruoyi-admin\\src\\main\\resources\\templates";

    //临时文件名称
    String htmName = "a";
    // 临时 html 文件路径
    String srcPath = saveHtml(echartData, htmlTemplate, templatePath, folderPath, htmName);

    //3.执行html到pdf的转换
    // pdf文档存储路径,以及名称
    String destPath = folderPath + "\\1.pdf";
    boolean convert = HtmlToPdfUtil.convert(srcPath, destPath);

    // html转pdf成功,删除临时html
    if (convert) {
      Files.delete(new File(srcPath).toPath());
      log.info("临时 {} DELETE SUCCESS", srcPath);
    }

    log.info("total time(ms)={}", System.currentTimeMillis() - startTime);
  }
}
