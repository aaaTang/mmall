package com.mmall.pojo;

public class Area {
    private Integer areaid;

    private String areacode;

    private String areaname;

    private Byte level;

    private String citycode;

    private String center;

    private Integer parentid;

    public Area(Integer areaid, String areacode, String areaname, Byte level, String citycode, String center, Integer parentid) {
        this.areaid = areaid;
        this.areacode = areacode;
        this.areaname = areaname;
        this.level = level;
        this.citycode = citycode;
        this.center = center;
        this.parentid = parentid;
    }

    public Area() {
        super();
    }

    public Integer getAreaid() {
        return areaid;
    }

    public void setAreaid(Integer areaid) {
        this.areaid = areaid;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode == null ? null : areacode.trim();
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname == null ? null : areaname.trim();
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode == null ? null : citycode.trim();
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center == null ? null : center.trim();
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }
}