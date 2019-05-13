package com.mmall.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter

@Component
public class Permission {

    private int id;

    private String token;
    /**资源url**/
    private String url;
    /**权限说明**/
    private String description;
    /**所属角色编号**/
    private int roleId;

    @Override
    public String toString() {
        return "PermissionPojo{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", roleId=" + roleId +
                '}';
    }

}
