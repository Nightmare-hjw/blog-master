package com.jax.blog.utils;

import com.jax.blog.constant.URLMapper;
import com.jax.blog.constant.WebConst;
import com.jax.blog.controller.admin.AttachController;
import com.jax.blog.exception.BusinessException;
import com.jax.blog.model.User;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.awt.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName TaleUtils
 * @Description TODO
 * @Author huangjw
 * @Date 2018/9/7 11:54
 * @Version 1.0
 **/

public class TaleUtils {
    /** 一个月 */
    private static final int one_month = 30 * 24 * 60 * 60;

    /** 匹配邮箱正则 */
    private static final Pattern VAILD_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /** 路径正则 */
    private static final Pattern SLUG_REGEX = Pattern.compile("^[A-Za-z0-9_-]{5,100}$", Pattern.CASE_INSENSITIVE);

    /** 使用双重检查锁的单例方式需要添加 volatile 关键字 */
    private static volatile DataSource newDataSource;

    /** markdown 解析器 */
    private static Parser parser = Parser.builder().build();

    /** 获取文件所在目录 */
    private static String location = TaleUtils.class.getClassLoader().getResource("").getPath();

    /**
     * 判断邮箱格式是否正确
     * */
    public static boolean isEmail(String emailStr) {
        Matcher matcher = VAILD_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    /**
     * @param filename 获取jar外部文件
     * @return
     */
    public static Properties getPropFromFile(String filename) {
        Properties properties = new Properties();
        try {
            // 默认是classpath路径
            InputStream resourceAsStream = new FileInputStream(filename);
            properties.load(resourceAsStream);
        } catch (BusinessException | IOException e) {
            //LOGGER.error("get properties file fail={}", e.getMessage());
        }
        return properties;
    }

    /**
     * md5 加密
     * @param source 数据源
     * @return 加密字符串
     */
    public static String MD5encode(String source) {
        if(StringUtils.isBlank(source)) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }
        byte[] encode = messageDigest.digest(source.getBytes());
        StringBuilder hexString = new StringBuilder();
        for(byte anEncode : encode) {
            String hex = Integer.toHexString(0xff & anEncode);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * PBKDF2(Password-Based Key Derivation Function) 加密算法
     * @param source 数据源
     * @param salt 盐值
     * @return
     */
    public static String PBKDF2encode(String source, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(StringUtils.isBlank(source) || StringUtils.isBlank(salt)) {
            return null;
        }

        return PasswordEncryption.getEncryptedPassword(source, salt);
    }

    /**
     * 获取新的数据源
     * @return
     */
    public static DataSource getNewDataSource() {
        if(newDataSource == null) synchronized (TaleUtils.class){
            if(newDataSource == null) {
                Properties properties = TaleUtils.getPropFromFile("application-default.properties");
                if(properties.size() == 0) {
                    return newDataSource;
                }
                DriverManagerDataSource managerDataSource = new DriverManagerDataSource();
                managerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
                managerDataSource.setPassword(properties.getProperty("spring.datasource.password"));
                String url = "jdbc:mysql://" + properties.getProperty("spring.datasource.url") + "/" + properties.getProperty("spring.datasource.dbname") + "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
                managerDataSource.setUrl(url);
                managerDataSource.setUsername(properties.getProperty("spring.datasource.username"));
                newDataSource = managerDataSource;
            }
        }
        return newDataSource;
    }

    /**
     * 返回当前登录用户
     * @param request
     * @return
     */
    public static User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(null == session) {
            return null;
        }
        return (User) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
    }

    /**
     * 获取cookie中的用户id
     * @param request
     * @return
     */
    public static Integer getCookieUid(HttpServletRequest request) {
        if(null != request) {
            Cookie cookie = cookieRaw(WebConst.USER_IN_COOKIE, request);
            if(cookie != null && cookie.getValue() != null) {
                try {
                    String uid = Tools.deAes(cookie.getValue(), WebConst.AES_SALT);
                    return StringUtils.isNotBlank(uid) && Tools.isNumber(uid) ? Integer.valueOf(uid) : null;
                } catch (Exception e)  {
                }
            }
        }
        return null;
    }

    /**
     * 从cookies中获取指定的cookie
     * @param name 名称
     * @param request 请求
     * @return cookie
     */
    private static Cookie cookieRaw(String name, HttpServletRequest request) {
        Cookie[] servletCookies = request.getCookies();
        if(servletCookies == null) {
            return null;
        }
        for(Cookie c : servletCookies) {
            if(c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * 设置记住密码cookie
     * @param response
     * @param uid
     */
    public static void setCookie(HttpServletResponse response, Integer uid) {
        try {
            String val = Tools.enAes(uid.toString(), WebConst.AES_SALT);
            boolean isSSL = false;
            Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, val);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            cookie.setSecure(isSSL);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取html中的文字
     * @param html
     * @return
     */
    public static String htmlToText(String html) {
        if(StringUtils.isNotBlank(html)) {
            return html.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        }
        return "";
    }

    /**
     * markdown转换为html
     * @param markdown
     * @return
     */
    public static String mdToHtml(String markdown) {
        if(StringUtils.isBlank(markdown)) {
            return null;
        }
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        String content = renderer.render(document);
        content = Commons.emoji(content);
        return content;
    }

    /**
     * 退出登录状态
     * @param session
     * @param response
     */
    public static void logout(HttpSession session, HttpServletResponse response) {
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try {
            response.sendRedirect(URLMapper.ADMIN_LOGIN);
        } catch (IOException e) {
            //LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 替换HTML脚本
     * @param value
     * @return
     */
    public static String cleanXSS(String value) {
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }

    /**
     * 过滤XSS注入
     * @param value
     * @return
     */
    public static String filterXSS(String value) {
        String cleanValue = null;
        if (value != null) {
            cleanValue = Normalizer.normalize(value, Normalizer.Form.NFD);
            // Avoid null characters
            cleanValue = cleanValue.replaceAll("\0", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            cleanValue = scriptPattern.matcher(cleanValue).replaceAll("");
        }
        return cleanValue;
    }

    /**
     * 判断是否是合法路径
     * @param slug
     * @return
     */
    public static boolean isPath(String slug) {
        if(StringUtils.isNotBlank(slug)) {
            if(slug.contains("/") || slug.contains(" ") || slug.contains(".")) {
                return false;
            }
            Matcher matcher = SLUG_REGEX.matcher(slug);
            return matcher.find();
        }
        return false;
    }

    /**
     * 判断文件是否是图片类型
     * @param imageFile
     * @return
     */
    public static boolean isImage(InputStream imageFile) {
        try {
            Image image = ImageIO.read(imageFile);
            if(image == null || image.getWidth(null) <= 0 || image.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 随机数
     * @param size
     * @return
     */
    public static String getRandomNumber(int size) {
        String num = "";

        for(int i = 0; i < size; ++i) {
            double a = Math.random() * 9.0D;
            a = Math.ceil(a);
            int randomNum = (new Double(a)).intValue();
            num = num + randomNum;
        }
        return num;
    }

    /**
     * 获取文件保存的位置，jar所在目录路径
     * @return
     */
    public static String getUploadFilePath() {
        String path = TaleUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(1, path.length());
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int lastIndex = path.lastIndexOf("/") + 1;
        path = path.substring(0, lastIndex);
        File file = new File("");
        return file.getAbsolutePath() + "/";
    }

    public static String getFileKey(String name) {
        String prefix = "/upload/" + DateKit.dateFormat(new Date(), "yyyy/MM");
        if (!new File(AttachController.CLASSPATH + prefix).exists()) {
            new File(AttachController.CLASSPATH + prefix).mkdirs();
        }

        name = StringUtils.trimToNull(name);
        if (name == null) {
            return prefix + "/" + UUID.UU32() + "." + null;
        } else {
            name = name.replace('\\', '/');
            name = name.substring(name.lastIndexOf("/") + 1);
            int index = name.lastIndexOf(".");
            String ext = null;
            if (index >= 0) {
                ext = StringUtils.trimToNull(name.substring(index + 1));
            }
            return prefix + "/" + UUID.UU32() + "." + (ext == null ? null : (ext));
        }
    }
}
