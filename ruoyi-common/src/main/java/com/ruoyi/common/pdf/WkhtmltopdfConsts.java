package com.ruoyi.common.pdf;

/**
 * Wkhtmltopdf 常量配置类
 */
public class WkhtmltopdfConsts {

  /**
   * 0.12.6版本默认禁用本地文件访问（图片等）
   * cmd 命令 加上以下命令参数即可
   * 表示启动本地文件访问
   */
  private static final String PARAMETER = "--enable-local-file-access";

  /**
   * 允许本地文件访问
   */
  private static final String ALLOW = "--allow";

  /**
   * wkhtmltopdf在系统中的路径(全路径)
   */
  public static final String CONVERSION_PLUGSTOOL_PATH_WINDOW = "D:/wkhtmltopdf/bin/wkhtmltopdf.exe "+PARAMETER;


  /**
   * wkhtmltopdf在Linux系统中的路径
   */
  public static final String CONVERSION_PLUGSTOOL_PATH_LINUX = "/usr/local/bin/wkhtmltopdf"+PARAMETER;

}
