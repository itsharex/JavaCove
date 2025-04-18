package com.ican.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户浏览
 *
 * @author gj
 **/
@Data
@ApiModel(description = "用户浏览")
public class UserViewVO {

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private String date;

    /**
     * pv
     */
    @ApiModelProperty(value = "pv")
    private Integer pv;

    /**
     * uv
     */
    @ApiModelProperty(value = "uv")
    private Integer uv;
}
