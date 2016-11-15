package com.helloweather.app.model;

/**
 * @author HuaZhu
 *         created at 2016-11-10 17:44
 * @brief Province类
 */
public class Province {

    private int id;

    private String provinceName;

    private String provinceCode;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }
}
