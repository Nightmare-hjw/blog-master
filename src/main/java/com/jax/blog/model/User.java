package com.jax.blog.model;

/**
 * @ClassName User
 * @Description 用户
 * @Author huangjw
 * @Date 2018/9/5 16:57
 * @Version 1.0
 **/

public class User {
    /** 主键编号 */
    private Integer uid;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 简介 */
    private String profile;

    /** email */
    private String email;

    /** 主页地址 */
    private String homeUrl;

    /**  用户显示的名称 */
    private String nickName;

    /** 签名 */
    private String sign;

    /** 头像路径 */
    private String imageName;

    /** 用户注册时的GMT unix时间戳 */
    private Integer created;

    /** 最后活跃时间 */
    private Integer activated;

    /** 用户组 */
    private String groupName;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Integer getActivated() {
        return activated;
    }

    public void setActivated(Integer activated) {
        this.activated = activated;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", profile='" + profile + '\'' +
                ", email='" + email + '\'' +
                ", homeUrl='" + homeUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sign='" + sign + '\'' +
                ", imageName='" + imageName + '\'' +
                ", created=" + created +
                ", activated=" + activated +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
